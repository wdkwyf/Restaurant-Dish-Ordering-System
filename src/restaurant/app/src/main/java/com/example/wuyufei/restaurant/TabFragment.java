package com.example.wuyufei.restaurant;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wuyufei.restaurant.adapter.DishItem;
import com.example.wuyufei.restaurant.adapter.DishAdapter;
import com.example.wuyufei.restaurant.listener.EndlessScrollListener;
import com.melnykov.fab.FloatingActionButton;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TabFragment extends Fragment {
    private String[] dishType = {"meat", "vegetable", "staple", "drink"};

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
        fab.attachToRecyclerView(rvItems);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        final List<DishItem> allDishItems;
        final DishAdapter adapter;
        final RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity());
        final List<DishItem> dishItemArrayList = new ArrayList<DishItem>();

        adapter = new DishAdapter(dishItemArrayList);
        rvItems.setAdapter(adapter);

        rvItems.setLayoutManager(linearLayoutManager);
        String url = "http://10.60.44.189:3000/menulist/0000/?dishtype="+dishType[position];
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
                                double dishPrice = (double) jsonObject.get("spend");
                                String Dishid = (String) jsonObject.get("id");
                                DishItem tmp = new DishItem(dishType[position], name, dishPrice, Dishid);
                                dishItemArrayList.add(tmp);
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
