package com.example.wuyufei.restaurant.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wuyufei.restaurant.R;
import com.example.wuyufei.restaurant.beans.OrderDetail;
import com.example.wuyufei.restaurant.beans.OrderHead;
import com.example.wuyufei.restaurant.model.DishesItem;
import com.example.wuyufei.restaurant.model.OrderItem;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

/**
 * Created by wuyufei on 16/1/13.
 */
public class ExpandableViewAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<OrderHead> deptList;
    private static final String TAG = "ExpandableViewAdapter";
    private OnButtonClickListener callback;

    public ExpandableViewAdapter(ArrayList<OrderHead> deptList,OnButtonClickListener callback) {
        this.deptList = deptList;
        this.callback = callback;
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }


    @Override
    public int getGroupCount() {
        return deptList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return deptList.get(groupPosition).getProductList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return deptList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<OrderDetail> productList = deptList.get(groupPosition).getProductList();

        return productList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        OrderHead headerInfo = (OrderHead) getGroup(groupPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cart_row, null);
        }
        TextView heading = (TextView) convertView.findViewById(R.id.cart_head);

        heading.setText(headerInfo.getType());
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final OrderDetail detailInfo = (OrderDetail) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cart_group, null);
        }


        final TextView nameTextView = (TextView) convertView.findViewById(R.id.cart_textview_name);
        nameTextView.setText(detailInfo.getName());

        TextView priceTextView = (TextView) convertView.findViewById(R.id.cart_textview_price);
        priceTextView.setText("" + detailInfo.getPrice());

        final TextView countTextView = (TextView) convertView.findViewById(R.id.cart_textview_count);
        countTextView.setText("" + detailInfo.getCount());

        final CircleButton buttonAdd = (CircleButton) convertView.findViewById(R.id.circle_button_cart_right);
        final CircleButton buttonReduce = (CircleButton) convertView.findViewById(R.id.circle_button_cart_left);

//        Log.d(TAG, "getChildView: " + convertView.getParent().toString());

        DishesItem tmp = DishesItem.getItem(detailInfo.getRemoteId());
        final OrderItem orderItem = OrderItem.getOrderItem(tmp);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update
                Long tsLong = System.currentTimeMillis() / 1000;
                orderItem.quantity = orderItem.quantity + 1;
                orderItem.timestamp = tsLong;
                orderItem.save();
                detailInfo.setCount(orderItem.quantity);
                countTextView.setText("" + orderItem.quantity);
                callback.onClick();

            }
        });
        buttonReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long tsLong = System.currentTimeMillis() / 1000;
                //delete
                if (orderItem.quantity <= 1) {
                    orderItem.delete();
                    countTextView.setText("" + 0);
                    detailInfo.setCount(0);
                    deptList.remove(detailInfo);
                    buttonAdd.setClickable(false);
                    buttonReduce.setClickable(false);


                } else {
                    //update
                    orderItem.quantity = orderItem.quantity - 1;
                    orderItem.timestamp = tsLong;
                    orderItem.save();
                    countTextView.setText("" + orderItem.quantity);
                    detailInfo.setCount(orderItem.quantity);

                }
                callback.onClick();

            }

        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public interface OnButtonClickListener{
        public void onClick();
    }
    public void setBtnClickListener(OnButtonClickListener listener){
        this.callback = listener;
    }

}
