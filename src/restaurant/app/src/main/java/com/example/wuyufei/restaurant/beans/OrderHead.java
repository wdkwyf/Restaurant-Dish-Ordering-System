package com.example.wuyufei.restaurant.beans;

import java.util.ArrayList;

/**
 * Created by wuyufei on 16/1/14.
 */
public class OrderHead {
    private String type;
    private ArrayList<OrderDetail> productList = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<OrderDetail> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<OrderDetail> productList) {
        this.productList = productList;
    }
}
