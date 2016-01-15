package com.example.wuyufei.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.activeandroid.ActiveAndroid;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wuyufei.restaurant.adapter.DishItem;
import com.example.wuyufei.restaurant.adapter.DishAdapter;
import com.example.wuyufei.restaurant.listener.EndlessScrollListener;
import com.example.wuyufei.restaurant.model.DishesItem;
import com.melnykov.fab.FloatingActionButton;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TabFragment extends Fragment {
    private final String TAG = "TabFragment";
//    private final String[] DISH_TYPE = {"meat", "vegetable", "staple", "drink"};
    private final String baseUrl = "http://10.60.44.189:3000/menulist/0000/?dishtype=";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final int position = FragmentPagerItem.getPosition(getArguments());

        RecyclerView rvItems = (RecyclerView) view.findViewById(R.id.rvContacts);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<OrderItem> orderList = OrderItem.getAll();
//                List<MealSet> mealSetList = new ArrayList<MealSet>();
//                Gson gson = new GsonBuilder().
//                        excludeFieldsWithoutExposeAnnotation().
//                        setPrettyPrinting().
//                        serializeNulls().
//                        create();
//                JsonElement element = gson.toJsonTree(orderList, new TypeToken<List<OrderItem>>() {
//                }.getType());
//                JsonElement elementOrderSumSet = gson.toJsonTree(mealSetList, new TypeToken<List<MealSet>>() {
//                }.getType());
//                List<String> orderResult = OrderItem.getOrderResult();
//                TableInfo tableInfo = new TableInfo("001", 3);
//                OrderSum orderSum = new OrderSum(orderResult.get(0), orderResult.get(1));
//
//                JsonElement elementTable = gson.toJsonTree(tableInfo);
//                JsonElement elementOrderSum = gson.toJsonTree(orderSum);
//                JsonObject jsonObject = new JsonObject();
//                if (element.isJsonArray()) {
//                    jsonObject.add("dish", element);
//                    jsonObject.add("table", elementTable);
//                    jsonObject.add("orderSum", elementOrderSum);
//                    jsonObject.add("setmeal", elementOrderSumSet);
//                    Log.d(TAG, "onClick: floating button" + jsonObject.toString());
//                }
                Intent intent = new Intent(getActivity(),CartActivity.class);
                startActivity(intent);
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(jsonObject.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                String url = "http://10.60.44.189:3000/orderlist/";
//                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url,
//                        obj, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) throws JSONException {
//                        Log.d(TAG, "onResponse: success");
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        VolleyLog.e("Error: ", error.getMessage());
//                    }
//                });
//                mRequestQueue.add(req);

            }
        });

        fab.attachToRecyclerView(rvItems);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        final DishAdapter adapter;
        final List<DishItem> dishItemArrayList = new ArrayList<DishItem>();


        adapter = new DishAdapter(dishItemArrayList);
        rvItems.setAdapter(adapter);

        rvItems.setLayoutManager(linearLayoutManager);
        String url = baseUrl + entity.DISH_TYPE[position];
        JsonObjectRequest req = new JsonObjectRequest(url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result = response.getJSONArray("menu");
                            dishItemArrayList.clear();
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject jsonObject = result.getJSONObject(i);
                                String name = (String) jsonObject.get("name");
//                                double dishPrice = (double) jsonObject.get("spend");
                                double dishPrice =Double.parseDouble(jsonObject.getString("spend"));
                                String dishId = (String) jsonObject.get("id");
                                //test for active android
                                ActiveAndroid.beginTransaction();
                                try {
                                    DishesItem item = new DishesItem();
                                    item.remoteId = dishId;
                                    item.name = name;
                                    item.price = dishPrice;
                                    item.type = position;
                                    item.save();
                                    ActiveAndroid.setTransactionSuccessful();
                                } finally {
                                    ActiveAndroid.endTransaction();
                                }

//                                Log.d(TAG, "onResponse: "+item.name);

                                dishItemArrayList.add(new DishItem(entity.DISH_TYPE[position], name, dishPrice, dishId));
                            }

                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        mRequestQueue.add(req);
        rvItems.addOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

//                List<DishItem> moreDishItems = ;  ;
                int curSize = adapter.getItemCount();
//                dishItemArrayList.addAll(moreDishItems);
                adapter.notifyItemRangeInserted(curSize, dishItemArrayList.size() - 1);
            }
        });


    }



}
