package com.youstockit;

import com.youstockit.factories.CatalogueProvisioning;
import com.youstockit.factories.SupplierServerProvisioning;
import com.youstockit.services.SupplierOrderService;
import com.youstockit.spies.EmailServiceSpy;
import com.youstockit.users.Manager;
import com.youstockit.users.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class ProductCatalogueTests {

    CatalogueProvisioning provisioning;
    SupplierServerProvisioning provisioningSupplier;
    ProductCatalogue productCatalogue;
    SupplierServer supplierServer;
    Supplier supplier;
    StockItem stockItem;
    Manager manager;
    EmailServiceSpy emailServiceSpy;
    SupplierOrderService supplierOrderService;


    @BeforeEach
    public void setup(){
        productCatalogue = new ProductCatalogue();
        // Instantiating object of Product Catalogue Factory
        provisioning = new CatalogueProvisioning();
        provisioningSupplier = new SupplierServerProvisioning();

        // Using Supplier Server test double provided from factory
        supplierServer = provisioningSupplier.provideSupplierServer();
        emailServiceSpy = new EmailServiceSpy();

        supplierOrderService = Mockito.mock(SupplierOrderService.class);

        supplier = new Supplier("Supplier Test", "supplier@test.com", supplierServer);
        manager= new Manager("Mgr Test", "manager@test.com");
        stockItem = new StockItem(2, "Test Product 2", "Validating", "Validation Item", 5,
                10, 20, 1.00,1.50);
    }

    @Test
    public void testEmptyProductCatalogue() {
        //Setup
        ProductCatalogue catalogue = provisioning.provideEmptyCatalogue();

        //Exercise
        List<StockItem> items = catalogue.items;

        //Verify
        Assertions.assertEquals(0, items.size());
    }

    @Test
    public void testProductCatalogueWithOneItem() {
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideStockedCatalogue();

        //Exercise
        List<StockItem> items = productCatalogue.items;

        //Verify
        Assertions.assertEquals(1, productCatalogue.items.size());
    }

    @Test
    public void testProductCatalogueWithMoreThanOneItem() {
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();

        //Exercise
        List<StockItem> items = productCatalogue.items;

        //Verify
        Assertions.assertNotEquals(1, productCatalogue.items.size());
        Assertions.assertNotEquals(0, productCatalogue.items.size());
    }

    @Test
    public void testAddItemToEmptyProductCatalogue() {
        //Setup
        ProductCatalogue catalogue = provisioning.provideEmptyCatalogue();

        //Exercise
        catalogue.addItem(stockItem);
        List<StockItem> items = catalogue.items;

        //Verify
        Assertions.assertEquals(1, items.size());
    }

    @Test
    public void testRemoveItemFromStockedProductCatalogue() {
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);
        productCatalogue.setManager(manager);
        productCatalogue.setEmailService(emailServiceSpy);

        //Exercise
        productCatalogue.removeItem(item.id);

        //Verify
        Assertions.assertEquals(0, productCatalogue.items.size());
    }

    @Test
    public void testRemoveItemFromMultiStockedProductCatalogue() {
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);
        productCatalogue.setManager(manager);
        productCatalogue.setEmailService(emailServiceSpy);
        int catSize = productCatalogue.items.size();

        //Exercise
        productCatalogue.removeItem(stockItem.id);

        //Verify
        Assertions.assertEquals(catSize - 1, productCatalogue.items.size());
    }

    @Test
    public void testRemoveItemFromEmptyProductCatalogue() {
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideEmptyCatalogue();
        int catSize = productCatalogue.items.size();

        //Exercise
        productCatalogue.removeItem(stockItem.id);

        //Verify
        Assertions.assertEquals(catSize, productCatalogue.items.size());
    }

    @Test
    public void testSearchProductCatalogue(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);

        //Exercise
        StockItem stockItem = productCatalogue.searchCatalogue(item.id);

        //Verify
        Assertions.assertNotNull(stockItem);
    }

    @Test
    public void testSearchProductCatalogueWrongId(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);

        //Exercise
        StockItem stockItem = productCatalogue.searchCatalogue(item.id + 1);

        //Verify
        Assertions.assertNull(stockItem);
    }

    @Test
    public void testSearchProductCatalogueWithNoItem(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideEmptyCatalogue();

        //Exercise
        StockItem item = productCatalogue.searchCatalogue(1);

        //Verify
        Assertions.assertNull(item);
    }

    @Test
    public void testSearchProductCatalogueCategory(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);

        //Exercise
        List<StockItem> stockItem = productCatalogue.searchCatalogueCategory(item.category);

        //Verify
        Assertions.assertNotEquals(0, stockItem.size());
    }

    @Test
    public void testSearchProductCatalogueWrongCategory(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);

        //Exercise
        List<StockItem> stockItem = productCatalogue.searchCatalogueCategory("Abc");

        //Verify
        Assertions.assertEquals(0, stockItem.size());
    }

    @Test
    public void testSellItem(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);
        int itemQuantity = item.quantity;
        int itemNumTimesSold = item.numTimesSold;

        //Exercise
        productCatalogue.sellItem(item.id, 5);

        //Verify
        Assertions.assertEquals(itemQuantity - 5, productCatalogue.searchCatalogue(item.id).quantity );
        Assertions.assertEquals(productCatalogue.searchCatalogue(item.id).numTimesSold, itemNumTimesSold + 1);
    }

    @Test
    public void testSellItemWrongId(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);
        int itemQuantity = item.quantity;

        //Exercise
        productCatalogue.sellItem(0, 5);

        //Verify
        Assertions.assertEquals(itemQuantity, productCatalogue.searchCatalogue(item.id).quantity);
    }

    @Test
    public void testSellItemIncorrectQuantity(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);

        //Setup - Automatic Stock Ordering
        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(item.orderAmount);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.SUCCESS);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        productCatalogue.setSupplier(supplier);

        //Exercise
        int sellSuccess = productCatalogue.sellItem(item.id, item.quantity + 1);

        //Verify
        Assertions.assertEquals(0, sellSuccess);
    }

    @Test
    public void testProfitFromSellingItem(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);
        int qtyToSell = 5;
        double itemSellingPrice = item.sellingPrice;
        double itemCostPrice = item.costPrice;
        double expectedProfit = itemSellingPrice - itemCostPrice;


        //Exercise
        productCatalogue.sellItem(item.id, qtyToSell);
        double profit = productCatalogue.totalProfit;

        //Verify
        Assertions.assertEquals(expectedProfit * qtyToSell, profit);
    }

    @Test
    public void testProfitFromSellingMultipleItems(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();
        StockItem item1 = productCatalogue.items.get(0);
        StockItem item2 = productCatalogue.items.get(1);
        int qtyToSell1 = 5;
        int qtyToSell2 = 2;
        double item1SellingPrice = item1.sellingPrice;
        double item2SellingPrice = item2.sellingPrice;
        double item1CostPrice = item1.costPrice;
        double item2CostPrice = item2.costPrice;
        double expectedProfit1 = item1SellingPrice - item1CostPrice;
        double expectedProfit2 = item2SellingPrice - item2CostPrice;


        //Exercise
        productCatalogue.sellItem(item1.id, qtyToSell1);
        productCatalogue.sellItem(item2.id, qtyToSell2);
        double profit = productCatalogue.totalProfit;

        //Verify
        Assertions.assertEquals((expectedProfit1 * qtyToSell1) + (expectedProfit2 * qtyToSell2) , profit);
    }

    @Test
    public void testReorderItemExceedingMinQtyLimit(){

        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);
        int quantityOrdered = item.quantity - item.minimumOrderQty;

        //Exercise
        productCatalogue.sellItem(item.id, quantityOrdered);
        ItemOrder itemOrder = productCatalogue.checkOrderQuantity(item);

        //Verify
        Assertions.assertNotNull(itemOrder);
    }

    @Test
    public void testReorderItemNotExceedingMinQtyLimit(){
        //Setup
        ProductCatalogue productCatalogue = provisioning.provideMultiStockedCatalogue();
        StockItem item = productCatalogue.items.get(0);
        int quantityOrdered = item.quantity - item.minimumOrderQty;

        //Exercise
        productCatalogue.sellItem(item.id, quantityOrdered - 1);
        ItemOrder itemOrder = productCatalogue.checkOrderQuantity(item);

        //Verify
        Assertions.assertNull(itemOrder);
    }
}
