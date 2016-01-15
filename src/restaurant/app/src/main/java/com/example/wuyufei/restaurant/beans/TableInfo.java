package com.example.wuyufei.restaurant.beans;

/**
 * Created by wuyufei on 16/1/13.
 */
public  class TableInfo {
    public String tableId;
    public int customerCount;

    public TableInfo(String tableId, int customerCount) {
        this.tableId = tableId;
        this.customerCount = customerCount;
    }
}
