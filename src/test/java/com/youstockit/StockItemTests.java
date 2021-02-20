package com.youstockit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StockItemTests {

    StockItem stockItem;


    @Test
    public void testCreateStockItemLongName() {
        //Setup
        String name = "";
        for(int i=0; i<= 100; i++){
            name = name + "a";
        }

        //Exercise
        stockItem = new StockItem(1, name, "Testing", "Test Item", 10, 1000, 250, 1.00, 1.50);

        //Verify
        Assertions.assertNotEquals(name, stockItem.name);
    }

    @Test
    public void testCreateStockItemShortName() {
        //Setup
        String name = "abcd";

        //Exercise
        stockItem = new StockItem(1, name, "Testing", "Test Item", 10, 1000, 250, 1.00,1.50);

        //Verify
        Assertions.assertNotEquals(name, stockItem.name);
    }

    @Test
    public void testCreateStockItemCorrectName() {
        //Setup
        String name = "";
        for(int i=0; i <= 99; i++){
            name = name + "a";
        }

        //Exercise
        stockItem = new StockItem(1, name, "Testing", "Test Item", 10, 1000, 250,1.00,  1.50);

        //Verify
        Assertions.assertEquals(name, stockItem.name);
    }

    @Test
    public void testCreateStockItemLongDescription() {
        //Setup
        String description = "";
        for(int i=0; i<= 500; i++){
            description = description + "a";
        }

        //Exercise
        stockItem = new StockItem(1, "Test Product", "Testing", description, 10, 1000, 250, 1.00, 1.50);

        //Verify
        Assertions.assertNotEquals(description, stockItem.description);
    }

    @Test
    public void testCreateStockItemCorrectDescription() {
        //Setup
        String description = "";
        for(int i=0; i< 500; i++){
            description = description + "a";
        }

        //Exercise
        stockItem = new StockItem(1, "Test Product", "Testing", description, 10, 1000, 250,  1.00, 1.50);

        //Verify
        Assertions.assertEquals(description, stockItem.description);
    }
}
