package com.youstockit.factories;

import com.youstockit.ProductCatalogue;
import com.youstockit.StockItem;

// Factory providing DOC - in this case, the product catalogue, to the SUT
// Refer to XUnit Pattern Dependency Lookup
// http://xunitpatterns.com/Dependency%20Lookup.html

public class CatalogueProvisioning {

    StockItem stockItem;
    StockItem stockItem2;
    StockItem stockItem3;
    StockItem stockItem4;
    ProductCatalogue productCatalogue;

    public ProductCatalogue provideEmptyCatalogue(){
        productCatalogue = new ProductCatalogue();
        return productCatalogue;
    }

    public ProductCatalogue provideStockedCatalogue(){

        stockItem = new StockItem(1, "Test Product", "Testing", "Test Item", 10,
                1000, 250, 1.00, 1.50);
        productCatalogue = new ProductCatalogue();
        productCatalogue.addItem(stockItem);

        return productCatalogue;
    }

    public ProductCatalogue provideMultiStockedCatalogue(){

        stockItem = new StockItem(1, "Test Product", "Testing", "Test Item", 10,
                1000, 250, 1.00, 1.50);
        stockItem2 = new StockItem(2, "Test Product 2", "Groceries", "Test Item 2", 10,
                150, 50, 4.00,5.50);
        stockItem3 = new StockItem(3, "Test Product 3", "Groceries", "Test Item 3 ", 10,
                70, 10,  6.00, 7.99);
        stockItem4 = new StockItem(4, "Test Product 4", "Utilities", "Test Item 4", 10,
                800, 50,  6.75, 10.00);

        productCatalogue = new ProductCatalogue();
        productCatalogue.addItem(stockItem);
        productCatalogue.addItem(stockItem2);
        productCatalogue.addItem(stockItem3);
        productCatalogue.addItem(stockItem4);

        return productCatalogue;
    }
}
