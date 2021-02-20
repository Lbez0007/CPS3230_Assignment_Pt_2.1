package com.youstockit;

import com.youstockit.factories.CatalogueProvisioning;
import com.youstockit.factories.SupplierServerProvisioning;
import com.youstockit.services.SupplierOrderService;
import com.youstockit.spies.EmailServiceSpy;
import com.youstockit.spies.OrderServiceSpy;
import com.youstockit.users.Manager;
import com.youstockit.users.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Or;

public class SupplierResponseTests {

    SupplierServer supplierServer;
    Supplier supplier;
    Manager manager;
    ProductCatalogue productCatalogue;
    CatalogueProvisioning provisioning;
    SupplierServerProvisioning provisioningSupplier;
    SupplierOrderService supplierOrderService;
    EmailServiceSpy emailServiceSpy;
    OrderServiceSpy orderServiceSpy;
    StockItem stockItem;
    int stockItemQty;

    @BeforeEach
    public void setup(){
        supplierServer =  new SupplierServer();
        supplier = new Supplier("Mr Test", "test@test.com", supplierServer);
        manager= new Manager("Mgr Test", "manager@test.com");


        // Instantiating objects of Product Catalogue and Supplier Server Factories
        provisioning = new CatalogueProvisioning();
        provisioningSupplier = new SupplierServerProvisioning();

        supplierOrderService = Mockito.mock(SupplierOrderService.class);
        emailServiceSpy = new EmailServiceSpy();
        orderServiceSpy = new OrderServiceSpy();

        // Using Stocked Catalogue test double provided from factory
        productCatalogue = provisioning.provideStockedCatalogue();
        // Using Supplier Server test double provided from factory
        supplier.supplierServer = provisioningSupplier.provideSupplierServer();

        stockItem = productCatalogue.items.get(0);
        stockItemQty = stockItem.quantity;

        productCatalogue.setOrderService(orderServiceSpy);
        productCatalogue.setEmailService(emailServiceSpy);
        productCatalogue.setManager(manager);
    }

    @Test
    public void testOrderStockItemSuccessful(){
        // Setup
        int qtyItemsOrdered = 5;
        ItemOrder itemOrder = new ItemOrder(1, qtyItemsOrdered);
        ItemOrder[] items = {itemOrder};

        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(itemOrder.qtyItemsOrdered);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.SUCCESS);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        productCatalogue.setSupplier(supplier);

        // Exercise
        productCatalogue.automatedStockOrdering(items);

        // Verify
        Assertions.assertEquals(productCatalogue.searchCatalogue(stockItem.id).quantity, stockItemQty + qtyItemsOrdered);
    }

    @Test
    public void testOrderStockItemOutOfStock(){

        // Setup
        int qtyItemsOrdered = 1001;
        ItemOrder itemOrder = new ItemOrder(1, qtyItemsOrdered);
        ItemOrder[] items = {itemOrder};

        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(stockItem.quantity);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.OUT_OF_STOCK);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        productCatalogue.setSupplier(supplier);

        // Exercise
        productCatalogue.automatedStockOrdering(items);

        // Verify
        Assertions.assertNotEquals(productCatalogue.searchCatalogue(stockItem.id).quantity, stockItem.quantity + qtyItemsOrdered);
        Assertions.assertEquals(1, emailServiceSpy.numCallsSendEmail);
    }

    @Test
    public void testOrderStockItemCommunicationError(){

        // Setup
        int qtyItemsOrdered = 5;
        ItemOrder itemOrder = new ItemOrder(1, qtyItemsOrdered);
        ItemOrder[] items = {itemOrder};

        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(0);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.COMMUNICATION_ERROR);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        productCatalogue.setSupplier(supplier);

        // Exercise
        productCatalogue.automatedStockOrdering(items);

        // Verify
        Assertions.assertNotEquals(productCatalogue.searchCatalogue(stockItem.id).quantity, stockItem.quantity + qtyItemsOrdered);
        Assertions.assertEquals(1, emailServiceSpy.numCallsSendEmail);
        Assertions.assertEquals(3, orderServiceSpy.numCallsReorder);
    }

    @Test
    public void testOrderStockItemNotFound(){

        // Setup
        int qtyItemsOrdered = 1001;
        ItemOrder itemOrder = new ItemOrder(1, qtyItemsOrdered);
        ItemOrder[] items = {itemOrder};

        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(0);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.ITEM_NOT_FOUND);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        productCatalogue.setSupplier(supplier);

        // Exercise
        productCatalogue.automatedStockOrdering(items);

        // Verify
        Assertions.assertEquals(0, productCatalogue.searchCatalogue(items[0].itemId).minimumOrderQty);
    }

    @Test
    public void testOrderStockItemNotFoundSellAll(){

        // Setup
        int qtyItemsOrdered = 1001;
        ItemOrder itemOrder = new ItemOrder(1, qtyItemsOrdered);
        ItemOrder[] items = {itemOrder};

        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(0);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.ITEM_NOT_FOUND);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        productCatalogue.setSupplier(supplier);

        // Exercise
        productCatalogue.automatedStockOrdering(items);
        productCatalogue.sellItem(stockItem.id, 1000);

        // Verify
        Assertions.assertNull (productCatalogue.searchCatalogue(items[0].itemId));
    }

    @Test
    public void testOrderStockItemNotFoundSellAllBarOne(){

        // Setup
        int qtyItemsOrdered = 1001;
        ItemOrder itemOrder = new ItemOrder(1, qtyItemsOrdered);
        ItemOrder[] items = {itemOrder};

        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(0);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.ITEM_NOT_FOUND);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        productCatalogue.setSupplier(supplier);

        // Exercise
        productCatalogue.automatedStockOrdering(items);
        productCatalogue.sellItem(stockItem.id, 999);

        // Verify
        Assertions.assertNotNull (productCatalogue.searchCatalogue(items[0].itemId));
    }
}
