package com.example.wuyufei.restaurant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wuyufei.restaurant.R;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class DishAdapter extends
        RecyclerView.Adapter<DishAdapter.ViewHolder> {

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

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.dishes_name);
            priceTextView = (TextView) itemView.findViewById(R.id.dishes_price);
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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        DishItem dishItem = mDishItems.get(position);

        TextView nameTextView = viewHolder.nameTextView;
        nameTextView.setText(dishItem.getDishName());

//        Button button = viewHolder.messageButton;

//        if (dishItem.isOnline()) {
//            button.setText("Message");
//            button.setEnabled(true);
//        }
//        else {
//            button.setText("Offline");
//            button.setEnabled(false);
//        }
    }

    @Override
    public int getItemCount() {
        return mDishItems.size();
    }
}