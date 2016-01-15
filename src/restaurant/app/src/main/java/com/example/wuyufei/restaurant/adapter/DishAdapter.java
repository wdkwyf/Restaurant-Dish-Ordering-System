package com.example.wuyufei.restaurant.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.example.wuyufei.restaurant.R;
import com.example.wuyufei.restaurant.model.DishesItem;
import com.example.wuyufei.restaurant.model.OrderItem;

import java.util.List;

import at.markushi.ui.CircleButton;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class DishAdapter extends
        RecyclerView.Adapter<DishAdapter.ViewHolder> {
    private int totCount = 0;
    private double totPrice = 0;

    private static final String TAG = "DishAdapter";
    // Store a member variable for the contacts
    private List<DishItem> mDishItems;


    // Pass in the contact array into the constructor
    public DishAdapter(List<DishItem> dishItems) {
        mDishItems = dishItems;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public TextView priceTextView;
        public CircleButton selectCircleButtion;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.dishes_name);
            priceTextView = (TextView) itemView.findViewById(R.id.dishes_price);
            selectCircleButtion = (CircleButton) itemView.findViewById(R.id.select_circle_button);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_dish, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final DishItem dishItem = mDishItems.get(position);
//        Log.d(TAG, "onBindViewHolder: "+dishItem.getDishName());

        final TextView nameTextView = viewHolder.nameTextView;
        TextView priceTextView = viewHolder.priceTextView;
        CircleButton selectCircleButton = viewHolder.selectCircleButtion;

        nameTextView.setText(dishItem.getDishName());
        priceTextView.setText(dishItem.getDishPrice() + "");
        selectCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                RelativeLayout mRoot = (RelativeLayout) v.findViewById(R.id.layout_fragment_relative);
                Long tsLong = System.currentTimeMillis() / 1000;
                ActiveAndroid.beginTransaction();
                try {
                    DishesItem tmp = DishesItem.getItem(dishItem.getDishid());
                    OrderItem orderItem = OrderItem.getOrderItem(tmp);
                    if (orderItem == null) {
                        //insert
                        OrderItem item = new OrderItem();
                        item.dishId = tmp.remoteId;
                        item.item = tmp;
                        item.quantity = 1;
                        item.timestamp = tsLong;
                        item.save();

                    } else {
                        //update
                        orderItem.quantity = orderItem.quantity + 1;
                        orderItem.timestamp = tsLong;

                        orderItem.save();
                    }

                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
                List<String> orderResult = OrderItem.getOrderResult();
                final Snackbar snackbar = Snackbar.make(v,"您已购买："+orderResult.get(0)+"件 "+"总计："
                        +orderResult.get(1)+"元",
                        Snackbar.LENGTH_LONG);
                snackbar.setAction("继续看看", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mDishItems.size();
    }
}