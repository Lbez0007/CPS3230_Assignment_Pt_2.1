package com.youstockit;

import com.youstockit.factories.CatalogueProvisioning;
import com.youstockit.factories.SupplierServerProvisioning;
import com.youstockit.services.EmailService;
import com.youstockit.services.OrderService;
import com.youstockit.stubs.EmailServiceStub;
import com.youstockit.stubs.OrderServiceStub;
import com.youstockit.stubs.SupplierOrderServiceStub;
import com.youstockit.users.Manager;
import com.youstockit.users.Supplier;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Main main = new Main();

        System.out.println("YouStockIt");
        System.out.println("----------");
        System.out.println("1. View Catalogue");
        System.out.println("2. View items in a category");
        System.out.println("3. Add item to Catalogue");
        System.out.println("4. Remove item from Catalogue");
        System.out.println("5. Place customer order");
        System.out.println("6. Calculate profit from sales");
        System.out.println("7. Exit");

        Scanner choice = new Scanner(System.in);
        System.out.println();
        System.out.print("Please select an option: ");
        int choiceEntry = choice.nextInt();

        main.menu(choiceEntry);
    }

    public void menu(int choice) {
        CatalogueProvisioning provisioning = new CatalogueProvisioning();
        SupplierServerProvisioning provisioningSupplier = new SupplierServerProvisioning();
        ProductCatalogue catalogue = provisioning.provideMultiStockedCatalogue();
        SupplierServer supplierServer = provisioningSupplier.provideSupplierServer();
        Manager manager= new Manager("Mgr Test", "manager@test.com");
        Supplier supplier = new Supplier("Supplier Test", "supplier@test.com", supplierServer);
        EmailServiceStub emailServiceStub = new EmailServiceStub();
        OrderServiceStub orderServiceStub = new OrderServiceStub();
        SupplierOrderServiceStub supplierOrderServiceStub = new SupplierOrderServiceStub();

        catalogue.setSupplier(supplier);
        catalogue.setManager(manager);
        catalogue.setEmailService(emailServiceStub);
        catalogue.setOrderService(orderServiceStub);
        supplierServer.setSupplierOrderService(supplierOrderServiceStub);

        switch (choice) {
            case 1:
                for (StockItem item : catalogue.items) {
                    System.out.print("Item ID: " + item.id);
                    System.out.print(" Name: " + item.name);
                    System.out.print(" Quantity: " + item.quantity);
                }
                break;

            case 2:
                Scanner choiceCase2 = new Scanner(System.in);
                System.out.println();
                System.out.print("Insert Category of items: ");
                String categoryCase2 = choiceCase2.nextLine();

                List<StockItem> categoryItems = catalogue.searchCatalogueCategory(categoryCase2);

                for (StockItem item : categoryItems) {
                    System.out.print("Item ID: " + item.id);
                    System.out.print(" Name: " + item.name);
                    System.out.print(" Quantity: " + item.quantity);
                }
                break;

            case 3:
                Scanner choiceCase3 = new Scanner(System.in);
                System.out.println();
                System.out.print("Insert Item ID: ");
                int id = choiceCase3.nextInt();

                System.out.print("Insert Item Name: ");
                String name = choiceCase3.nextLine();

                System.out.print("Insert Item Category: ");
                String category = choiceCase3.nextLine();

                System.out.print("Insert Item Description: ");
                String description = choiceCase3.nextLine();

                System.out.print("Insert Minimum Order Quantity for Item: ");
                int minQty = choiceCase3.nextInt();

                System.out.print("Insert Item Quantity: ");
                int qty = choiceCase3.nextInt();

                System.out.print("Insert Order Amount for Item: ");
                int orderAmt = choiceCase3.nextInt();

                System.out.print("Insert Cost Price for Item: ");
                double costPrice = choiceCase3.nextDouble();

                System.out.print("Insert Selling Price for Item: ");
                double sellingPrice = choiceCase3.nextDouble();

                StockItem item = new StockItem(id, name, category, description, minQty, qty, orderAmt, costPrice, sellingPrice);
                catalogue.addItem(item);

                System.out.println("Item added to catalogue!");
                break;

            case 4:
                Scanner choiceCase4 = new Scanner(System.in);
                System.out.println();
                System.out.print("Insert ID of item to remove: ");
                int idCase4 = choiceCase4.nextInt();

                boolean removed = catalogue.removeItem(idCase4);
                if(removed) {
                    System.out.println("Item Removed");
                }
                else
                    System.out.println("Item not found in catalogue!");
                break;

            case 5:
                Scanner choiceCase5= new Scanner(System.in);

                System.out.println();
                System.out.print("Insert ID of item to order: ");
                int idCase5 = choiceCase5.nextInt();
                System.out.println();
                System.out.print("Insert quantity of item to order: ");
                int qtyEntry = choiceCase5.nextInt();

                catalogue.sellItem(idCase5, qtyEntry);
                System.out.println("Customer Order Effected!");
                System.out.println("Triggering Automatic Stock Ordering...");

                ItemOrder itemOrder = new ItemOrder(idCase5,qtyEntry);
                ItemOrder [] items = {itemOrder};
                SupplierResponse[] supplierResponse = catalogue.automatedStockOrdering(items);

                System.out.println("Supplier Response: "+supplierResponse[0].supplierErrorCode);
                break;

            case 6:
                System.out.println("Total Profit generated: "+catalogue.totalProfit);
                break;

            case 7:
                System.exit(0);
        }
    }
}
