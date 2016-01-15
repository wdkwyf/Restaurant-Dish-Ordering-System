package com.example.wuyufei.restaurant.adapter;

public class DishItem {
    private String DishType;
    private String DishName;
    private double DishPrice;
    private String Dishid;

    public DishItem(String type, String name, double price, String id) {
        DishType = type;
        DishName = name;
        DishPrice = price;
        Dishid = id;

    }


    public String getDishid() {
        return Dishid;
    }

    public void setDishid(String dishid) {
        Dishid = dishid;
    }

    public String getDishType() {
        return DishType;
    }

    public void setDishType(String dishType) {
        DishType = dishType;
    }

    public String getDishName() {
        return DishName;
    }

    public void setDishName(String dishName) {
        DishName = dishName;
    }

    public double getDishPrice() {
        return DishPrice;
    }

    public void setDishPrice(float dishPrice) {
        DishPrice = dishPrice;
    }
}