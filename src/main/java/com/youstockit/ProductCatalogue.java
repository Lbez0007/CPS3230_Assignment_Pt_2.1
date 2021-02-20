package com.youstockit;

import com.youstockit.services.EmailService;
import com.youstockit.services.OrderService;
import com.youstockit.users.Manager;
import com.youstockit.users.Supplier;

import java.util.LinkedList;
import java.util.List;

public class ProductCatalogue {

    protected List<StockItem> items;
    public EmailService emailService;
    protected OrderService orderService;
    //protected SupplierServer supplierServer;
    protected Supplier supplier;
    public double totalProfit;
    public Manager manager;

    public ProductCatalogue (){
        items = new LinkedList<StockItem>();
    }

    // Searching for items in catalogue using item ID
    public StockItem searchCatalogue(int itemId){
        for (int i = 0; i < items.size(); i++){
            if (items.get(i).id == itemId){
                return items.get(i);
            }
        }
        return null;
    }

    // Searching for a list of items in catalogue on the basis of their category
    public List<StockItem> searchCatalogueCategory(String category){
        List<StockItem> itemsRet = new LinkedList<StockItem>();

        for (int i = 0; i < items.size(); i++){
            if (items.get(i).category == category){
                itemsRet.add(items.get(i));
            }
        }
        return itemsRet;
    }

    // Adding an item to stock/catalogue
    public void addItem(StockItem item) {
        items.add(item);
    }

    // Removing item from stock/catalogue
    public boolean removeItem(int itemId) {
        StockItem item = searchCatalogue(itemId);
        if (item != null) {
            items.remove(item);
            emailService.sendEmail(manager);
            return true;
        }
        return false;
    }

    // Selling item to customer
    public int sellItem(int itemId, int qty) {
        StockItem item = searchCatalogue(itemId);

        // if item exists in catalogue
        if (item != null) {
            // if there is enough quantity of item to satisfy order
            if ((item.quantity >= qty)) {
                item.quantity = item.quantity - qty;
                // order created ONLY when due to sale, item falls under minimum order quantity
                ItemOrder order =  checkOrderQuantity(item);

                // trigger automatic stock ordering if order is created
                if (order != null && supplier != null){
                    ItemOrder[] itemOrder = {order};
                    automatedStockOrdering(itemOrder);
                }

                item.numTimesSold++;
                totalProfit += (item.sellingPrice * qty) - (item.costPrice * qty);
                removeNilQuantityItems();
                return 1; // successful sale
            }
            // if there is not enough quantity of item to satisfy order
            else{
                ItemOrder itemOrder = new ItemOrder(item.id, item.orderAmount);
                ItemOrder[] items = {itemOrder};
                automatedStockOrdering(items);
                return 0; // failed sale
            }
        }
        return 0;
    }

    // Check if item falls under (or equates) minimum order quantity
    public ItemOrder checkOrderQuantity(StockItem item){
        if (item.quantity <= item.minimumOrderQty){
            ItemOrder itemOrder = new ItemOrder(item.id, item.orderAmount);
            return itemOrder;
        } return null;
    }

    public void increaseItemQuantity(int itemId, int qty){
        StockItem item = searchCatalogue(itemId);
        if (item != null) {
            item.quantity = item.quantity + qty;
        }
    }

    // Setting minimum order quantity for item to zero (when out of stock from suppliers)
    public void setMinimumOrderQuantityZero(int itemId){
        StockItem item = searchCatalogue(itemId);
        if (item != null) {
            item.minimumOrderQty = 0;
        }
    }

    // Deleting from stock items with 0 quantity and 0 minimum order quantity
    public void removeNilQuantityItems(){
        for (int i=0; i < items.size(); i++){
            StockItem item = items.get(i);
            if (item.quantity == 0 && item.minimumOrderQty == 0){
                removeItem(item.id);
            }
        }
    }

    // Setter methods
    public void setEmailService(EmailService emailService){
        this.emailService = emailService;
    }

    public void setOrderService(OrderService orderService){
        this.orderService = orderService;
    }

    public void setSupplier(Supplier supplier){
        this.supplier = supplier;
    }

    public void setManager(Manager manager){
        this.manager = manager;
    }

    // Automatic Stock Ordering Process
    // triggered by other methods when item falls under (or equates) minimum order qty
    public SupplierResponse[] automatedStockOrdering(ItemOrder[] items){
        // Item Orders sent to supplier - response is returned
        SupplierResponse[] responses = supplier.supplierServer.orderItems(items);

        // Handling response for each item order
        for (int i = 0; i < items.length; i++) {
            ItemOrder item = items[i];
            SupplierErrorCode errorCode = responses[i].supplierErrorCode;
            int qtySupplied = responses[i].qtyItemsProvided;

            if (errorCode.equals(SupplierErrorCode.SUCCESS)) {
                increaseItemQuantity(item.itemId, qtySupplied);
            }
            if (errorCode.equals(SupplierErrorCode.OUT_OF_STOCK)) {
                emailService.sendEmail(manager);
                increaseItemQuantity(item.itemId, qtySupplied);
            }
            if (errorCode.equals(SupplierErrorCode.ITEM_NOT_FOUND)) {
                setMinimumOrderQuantityZero(item.itemId);
            }
            if (errorCode.equals(SupplierErrorCode.COMMUNICATION_ERROR)) {
                for (int j = 0; j < 3; j++) {
                    try {
                        Thread.sleep(5000); //awaiting 5s before reordering
                        orderService.reOrder();
                        if (!errorCode.equals(SupplierErrorCode.COMMUNICATION_ERROR)) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                emailService.sendEmail(manager);
            }
        }

        return responses;
    }
}
