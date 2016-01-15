package com.example.wuyufei.restaurant.beans;

import com.example.wuyufei.restaurant.model.OrderItem;
import com.google.gson.annotations.Expose;

/**
 * Created by wuyufei on 16/1/13.
 */
public class OrderSum {
    @Expose
    public String count;

    @Expose
    public String totPrice;

    public OrderSum(String count, String totPrice) {
        this.count = count;
        this.totPrice = totPrice;
    }
}
