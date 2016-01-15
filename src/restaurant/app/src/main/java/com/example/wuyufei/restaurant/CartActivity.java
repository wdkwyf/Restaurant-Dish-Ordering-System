package com.example.wuyufei.restaurant;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wuyufei.restaurant.adapter.ExpandableViewAdapter;
import com.example.wuyufei.restaurant.beans.MealSet;
import com.example.wuyufei.restaurant.beans.OrderDetail;
import com.example.wuyufei.restaurant.beans.OrderHead;
import com.example.wuyufei.restaurant.beans.OrderSum;
import com.example.wuyufei.restaurant.beans.TableInfo;
import com.example.wuyufei.restaurant.model.OrderItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ExpandableViewAdapter.OnButtonClickListener {
    private static final String TAG = "CartActivity";
    private Context context;
    private LinkedHashMap<String, OrderHead> myDishTypes = new LinkedHashMap<String, OrderHead>();
    private ArrayList<OrderHead> deptList = new ArrayList<OrderHead>();
    private ExpandableViewAdapter adapter;
    private Button buttonConfirm, buttonCancel;
    private TextView tvTotPrice;
    private RequestQueue mRequestQueue;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        setSupportActionBar(toolbar);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        loadData();
        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandable_list);
        expandableListView.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = pref.edit();
        adapter = new ExpandableViewAdapter(deptList, this);
        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        expandableListView.setAdapter(adapter);

        for (int i = 0; i < myDishTypes.size(); i++) {
            if (adapter.getChildrenCount(i) != 0)
                expandableListView.expandGroup(i);
        }
        mRequestQueue = Volley.newRequestQueue(this);
        buttonCancel = (Button) findViewById(R.id.cart_button_cancel);
        buttonConfirm = (Button) findViewById(R.id.cart_button_confirm);
        tvTotPrice = (TextView) findViewById(R.id.cart_sum_price);
        tvTotPrice.setText(OrderItem.getOrderResult().get(1));
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context).title(R.string.cancel_question).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        //delete all order
                        OrderItem.deleteAll();
                        finish();
                    }
                }).positiveText(R.string.confirm)
                        .negativeText(R.string.doubt).negativeColor(Color.RED)
                        .show();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context).title(R.string.confirm_question).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        //confirm submit order to restaurant
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(jsonProcess().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, entity.URL_ORDER_COMMIT,
                                obj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) throws JSONException {
                                String result = response.getString("waiting_time");
                                editor.putString("waiting_time", result);
                                editor.commit();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Error: ", error.getMessage());
                            }
                        });
                        mRequestQueue.add(req);
                        finish();

                    }
                }).positiveText(R.string.confirm).positiveColor(Color.RED)
                        .negativeText(R.string.doubt)
                        .show();
            }
        });

    }


    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private int addProduct(String dishType, String product, double price, int count, String id) {
        int groupPosition = 0;
        //check the hash map if the group already exists
        OrderHead headerInfo = myDishTypes.get(dishType);
        //add the group if doesn't exists
        if (headerInfo == null) {
            headerInfo = new OrderHead();
            headerInfo.setType(dishType);
            myDishTypes.put(dishType, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<OrderDetail> productList = headerInfo.getProductList();
        //size of the children list
        int listSize = productList.size();
        //add to the counter
        listSize++;

        //create a new child and add that to the group
        OrderDetail detailInfo = new OrderDetail();
//        detailInfo.setName(String.valueOf(listSize));
        detailInfo.setName(product);
        detailInfo.setPrice(price);
        detailInfo.setCount(count);
        detailInfo.setRemoteId(id);
        productList.add(detailInfo);
        headerInfo.setProductList(productList);

        //find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
    }

    private void loadData() {
        List<OrderItem> data = OrderItem.getAll();
        for (OrderItem item : data) {
            addProduct(entity.DISH_TYPE_CHINA[item.item.type], item.item.name, item.item.price, item.quantity, item.dishId);
        }

    }

    @Override
    public void onClick() {
        String s = OrderItem.getOrderResult().get(1);
        tvTotPrice.setText(s);

    }

    private JsonObject jsonProcess() {
        List<OrderItem> orderList = OrderItem.getAll();
        List<MealSet> mealSetList = new ArrayList<MealSet>();
        Gson gson = new GsonBuilder().
                excludeFieldsWithoutExposeAnnotation().
                setPrettyPrinting().
                serializeNulls().
                create();
        JsonElement element = gson.toJsonTree(orderList, new TypeToken<List<OrderItem>>() {
        }.getType());
        JsonElement elementOrderSumSet = gson.toJsonTree(mealSetList, new TypeToken<List<MealSet>>() {
        }.getType());
        List<String> orderResult = OrderItem.getOrderResult();

        TableInfo tableInfo = new TableInfo(pref.getString("table_id","000"), pref.getInt("customer_nums",1));
        OrderSum orderSum = new OrderSum(orderResult.get(0), orderResult.get(1));

        JsonElement elementTable = gson.toJsonTree(tableInfo);
        JsonElement elementOrderSum = gson.toJsonTree(orderSum);
        JsonObject jsonObject = new JsonObject();
        if (element.isJsonArray()) {
            jsonObject.add("dish", element);
            jsonObject.add("table", elementTable);
            jsonObject.add("orderSum", elementOrderSum);
            jsonObject.add("setmeal", elementOrderSumSet);
            Log.d(TAG, "onClick: floating button" + jsonObject.toString());
        }
        return jsonObject;

    }
}
