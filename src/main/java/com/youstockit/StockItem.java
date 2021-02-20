package com.youstockit;

import java.util.UUID;

public class StockItem {

    protected int id;
    protected String name;
    protected String category;
    protected String description;
    protected int minimumOrderQty;
    protected int quantity;
    protected int orderAmount;
    protected double costPrice;
    protected double sellingPrice;
    protected int numTimesSold;

    public StockItem(int id, String name, String category, String description, int minimumOrderQty,
                     int quantity, int orderAmount, double costPrice, double sellingPrice) {
        this.id = id;
        // Random ID
        // id = UUID.randomUUID().toString();
        setName(name);
        this.category = category;
        setDescription(description);
        this.minimumOrderQty = minimumOrderQty;
        this.quantity = quantity;
        this.orderAmount = orderAmount;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        numTimesSold = 0;
    }

    public boolean setName(String name){
        if(name.length() >= 5 && name.length() <= 100){
            this.name = name;
            return true;
        }
        else if (name.length() < 5){
            this.name = name + "-----";
            return false;
        }
        else {
            this.name = name.substring(0, 100);
            return false;
        }
    }

    public boolean setDescription(String description){
        if(description.length() <= 500){
            this.description = description;
            return true;
        }
        else {
            this.description = description.substring(0, 500);
            return false;
        }
    }
}
