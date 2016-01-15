package com.example.wuyufei.restaurant.beans;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by wuyufei on 16/1/13.
 */
public  class TableInfo{
    @Expose
    public String tableId;
    @Expose
    public int customerCount;

    public TableInfo() {
    }

    public TableInfo(String tableId, int customerCount) {
        this.tableId = tableId;
        this.customerCount = customerCount;
    }

}
