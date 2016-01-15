package com.example.wuyufei.restaurant.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyufei on 16/1/13.
 */
@Table(name = "OrderItem")
public class OrderItem extends Model {
    @Column(name = "TIMESTAMP_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long timestamp;

    @Column(name = "DishItem", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public DishesItem item;

    @Expose
    @Column(name = "Quantity")
    public int quantity;


    @Expose
    @Column(name = "DishId")
    public String dishId;

    public OrderItem() {
        super();
    }

    public OrderItem(DishesItem item, int quantity, long timestamp) {
        super();
        this.item = item;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.dishId = item.remoteId;
    }

    public static OrderItem getOrderItem(DishesItem item) {
        long remote_id = item.getId();
        return new Select().
                from(OrderItem.class)
                .where("DishItem = ?", remote_id)
                .executeSingle();
    }

    public static List<OrderItem> getAll() {
        // This is how you execute a query
        List<OrderItem> result = new Select().all()
                .from(OrderItem.class)
                .orderBy("TIMESTAMP_id ASC")
                .execute();

        return result;
    }

    public static void deleteAll() {
        new Delete().from(OrderItem.class)
                .execute();
    }

    public static List<String> getOrderResult() {
        double totPrice = 0;
        int totCount = 0;
        List<OrderItem> result = new Select().all()
                .from(OrderItem.class)
                .orderBy("TIMESTAMP_id ASC")
                .execute();
        for (int i = 0; i < result.size(); i++) {
            totPrice += result.get(i).quantity * result.get(i).item.price;
            totCount += result.get(i).quantity;

        }
        List<String> tot = new ArrayList<>();
        tot.add(totCount + "");
        tot.add(totPrice + "");
        return tot;

    }

}
