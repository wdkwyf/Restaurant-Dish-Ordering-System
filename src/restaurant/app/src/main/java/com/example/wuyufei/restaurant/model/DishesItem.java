package com.example.wuyufei.restaurant.model;

import android.content.ClipData;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by wuyufei on 16/1/13.
 */
@Table(name = "DishesItem")
public class DishesItem extends Model {
    @Column(name = "Remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String remoteId;
    // This is a regular field
    @Column(name = "Name")
    public String name;
    @Column(name = "Price")
    public double price;
    @Column(name = "Type")
    public int type;


    public DishesItem() {
        super();
    }

    public DishesItem(String remoteId, String name, double price) {
        super();
        this.remoteId = remoteId;
        this.name = name;
        this.price = price;

    }

    public static DishesItem getItem(String remoteId) {
        return new Select()
                .from(DishesItem.class)
                .where("Remote_id = ?", remoteId)
                .orderBy("Name ASC")
                .executeSingle();
    }

}
