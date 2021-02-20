package com.youstockit;

public class ItemOrder {

    protected int itemId;
    protected int qtyItemsOrdered;

    public ItemOrder(int itemId, int qtyItemsOrdered){
        this.itemId = itemId;
        this.qtyItemsOrdered = qtyItemsOrdered;
    }
}
