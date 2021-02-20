package com.youstockit;

import com.youstockit.enums.YouStockItStates;
import com.youstockit.factories.CatalogueProvisioning;
import com.youstockit.factories.SupplierServerProvisioning;
import com.youstockit.services.OrderService;
import com.youstockit.services.SupplierOrderService;
import com.youstockit.spies.EmailServiceSpy;
import com.youstockit.spies.OrderServiceSpy;
import com.youstockit.stubs.OrderServiceStub;
import com.youstockit.users.Manager;
import com.youstockit.users.Supplier;
import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import nz.ac.waikato.modeljunit.GreedyTester;
import nz.ac.waikato.modeljunit.StopOnFailureListener;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionPairCoverage;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class YouStockItModelTest implements FsmModel {
    // Instantiating object of Product Catalogue Factory
    CatalogueProvisioning provisioning = new CatalogueProvisioning();
    SupplierServerProvisioning provisioningSupplier = new SupplierServerProvisioning();

    // Using Supplier Server test double provided from factory
    SupplierServer supplierServer = provisioningSupplier.provideSupplierServer();
    ProductCatalogue systemUnderTest = provisioning.provideMultiStockedCatalogue();

    StockItem stockItem = systemUnderTest.items.get(0);
    // Test Sale should ensure minimum order quantity is reached to trigger automatic stock ordering
    int sampleSaleAmount = stockItem.quantity - (stockItem.minimumOrderQty - 3);
    Supplier supplier = new Supplier("Test", "supplier@test.com", supplierServer);
    Manager manager = new Manager("Test", "manager@test.com");

    // Test Doubles
    OrderServiceSpy orderServiceSpy = new OrderServiceSpy();
    EmailServiceSpy emailServiceSpy = new EmailServiceSpy();
    SupplierOrderService supplierOrderService = Mockito.mock(SupplierOrderService.class);

    //State Variables
    private YouStockItStates catalogueState = YouStockItStates.Catalogue_PreOrder;
    private int itemQuantity = stockItem.quantity;
    private int minimumOrderQuantity = stockItem.minimumOrderQty;
    private int emailsSent = 0;
    private int numTimesSold = 0;

    //Method implementations
    public YouStockItStates getState() {
        return catalogueState;
    }

    public void reset(final boolean initialTest) {
        if (initialTest) {
            systemUnderTest = provisioning.provideMultiStockedCatalogue();
            orderServiceSpy = new OrderServiceSpy();
            emailServiceSpy = new EmailServiceSpy();
        }
        StockItem stockItem = systemUnderTest.items.get(0);
        sampleSaleAmount = stockItem.quantity - (stockItem.minimumOrderQty - 3);
        catalogueState = YouStockItStates.Catalogue_PreOrder;
        itemQuantity = stockItem.quantity;
        minimumOrderQuantity = stockItem.minimumOrderQty;
        numTimesSold = 0;
        emailsSent = 0;
    }

    //Transitions incl. guards
    public boolean orderSuccessfulGuard() {
        return getState().equals(YouStockItStates.Catalogue_PreOrder);
    }
    public @Action
    void orderSuccessful() {
        //Injection of test doubles and required instances
        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(stockItem.orderAmount);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.SUCCESS);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        systemUnderTest.setEmailService(emailServiceSpy);
        systemUnderTest.setOrderService(orderServiceSpy);
        systemUnderTest.setSupplier(supplier);
        systemUnderTest.setManager(manager);

        //Updating SUT
        systemUnderTest.sellItem(stockItem.id, sampleSaleAmount);

        //Updating model
        catalogueState = YouStockItStates.Item_Qty_Increase;
        itemQuantity = itemQuantity - sampleSaleAmount + stockItem.orderAmount;

        //Checking correspondence between model and SUT.
        assertEquals(itemQuantity, systemUnderTest.searchCatalogue(stockItem.id).quantity);
    }

    public boolean orderItemNotFoundGuard() {
        return getState().equals(YouStockItStates.Catalogue_PreOrder);
    }
    public @Action
    void orderItemNotFound() {
        //Injection of test doubles and required instances
        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(0);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.ITEM_NOT_FOUND);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        systemUnderTest.setEmailService(emailServiceSpy);
        systemUnderTest.setOrderService(orderServiceSpy);
        systemUnderTest.setSupplier(supplier);
        systemUnderTest.setManager(manager);

        //Updating SUT
        systemUnderTest.sellItem(stockItem.id, sampleSaleAmount);

        //Updating model
        catalogueState = YouStockItStates.Item_Min_Qty_0;
        minimumOrderQuantity = 0;

        //Checking correspondence between model and SUT.
        assertEquals(minimumOrderQuantity, systemUnderTest.searchCatalogue(stockItem.id).minimumOrderQty);
    }

    public boolean orderOutOfStockGuard() {
        return getState().equals(YouStockItStates.Catalogue_PreOrder);
    }
    public @Action
    void orderOutOfStock() {
        //Injection of test doubles and required instances
        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(stockItem.orderAmount - 1);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.OUT_OF_STOCK);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        systemUnderTest.setEmailService(emailServiceSpy);
        systemUnderTest.setOrderService(orderServiceSpy);
        systemUnderTest.setSupplier(supplier);
        systemUnderTest.setManager(manager);

        //Updating SUT
        systemUnderTest.sellItem(stockItem.id, sampleSaleAmount);

        //Updating model
        catalogueState = YouStockItStates.Item_Qty_Increase_Less_Than_Order;
        itemQuantity = itemQuantity - sampleSaleAmount + stockItem.orderAmount - 1;

        //Checking correspondence between model and SUT.
        assertEquals(itemQuantity, systemUnderTest.searchCatalogue(stockItem.id).quantity);
    }

    public boolean supplierCommunicationErrorGuard() {
        return getState().equals(YouStockItStates.Catalogue_PreOrder);
    }
    public @Action
    void supplierCommunicationError() {
        //Injection of test doubles and required instances
        Mockito.when(supplierOrderService.getQuantitySupplied()).thenReturn(0);
        Mockito.when(supplierOrderService.getOrderCode()).thenReturn(SupplierErrorCode.OUT_OF_STOCK);
        supplier.supplierServer.setSupplierOrderService(supplierOrderService);
        supplier.supplierServer.setEmailService(emailServiceSpy);
        systemUnderTest.setEmailService(emailServiceSpy);
        systemUnderTest.setOrderService(orderServiceSpy);
        systemUnderTest.setSupplier(supplier);
        systemUnderTest.setManager(manager);

        //Updating SUT
        systemUnderTest.sellItem(stockItem.id, sampleSaleAmount);

        //Updating model
        catalogueState = YouStockItStates.Communication_Error;
        itemQuantity = itemQuantity - sampleSaleAmount;

        //Checking correspondence between model and SUT.
        assertEquals(itemQuantity, systemUnderTest.searchCatalogue(stockItem.id).quantity);
    }

    public boolean sendingEmailGuard() {
        return getState().equals(YouStockItStates.Item_Qty_Increase_Less_Than_Order) || getState().equals(YouStockItStates.Communication_Error);
    }
    public @Action
    void sendingEmail() {
        //Updating model
        catalogueState = YouStockItStates.Email_Sent;
        emailsSent = 1;

        //Checking correspondence between model and SUT.
        assertEquals(emailsSent, systemUnderTest.emailService.returnNumCalls());
    }

    public boolean incrementNumberOfTimesSoldGuard() {
        return getState().equals(YouStockItStates.Email_Sent)
                || getState().equals(YouStockItStates.Item_Qty_Increase);
    }
    public @Action
    void incrementNumberOfTimesSold() {
        //Updating model
        numTimesSold ++;
        catalogueState = YouStockItStates.Item_No_Times_Sold_Incremented;

        //Checking correspondence between model and SUT.
        assertEquals(numTimesSold, systemUnderTest.searchCatalogue(stockItem.id).numTimesSold);
    }

    //Test runner
    @Test
    public void YouStockItModelRunner() {
        final GreedyTester tester = new GreedyTester(new YouStockItModelTest()); // test generator that can generate random walks, giving preference to transitions that have never been taken before
        tester.setRandom(new Random()); // random path each time the model is run.
        tester.buildGraph();
        tester.addListener(new StopOnFailureListener()); // forces the test class to stop running as soon as a failure is encountered.
        tester.addListener("verbose");
        tester.addCoverageMetric(new TransitionPairCoverage()); // number of paired transitions traversed during the execution of the test.
        tester.addCoverageMetric(new StateCoverage()); // number of states which have been visited during the execution of the test.
        tester.addCoverageMetric(new ActionCoverage()); // number of @Action methods which have ben executed during the execution of the test.

        tester.generate(500); // Generates 500 transitions
        tester.printCoverage(); // Prints the coverage metrics specified above.
    }

    //Test runner
    @Test
    public void YouStockItModelRunnerActionCoverage() {
        final GreedyTester tester = new GreedyTester(new YouStockItModelTest()); // test generator that can generate random walks, giving preference to transitions that have never been taken before
        tester.setRandom(new Random()); // random path each time the model is run.
        tester.buildGraph();
        tester.addListener(new StopOnFailureListener()); // forces the test class to stop running as soon as a failure is encountered.
        tester.addListener("verbose");
        tester.addCoverageMetric(new TransitionPairCoverage()); // number of paired transitions traversed during the execution of the test.
        tester.addCoverageMetric(new StateCoverage()); // number of states which have been visited during the execution of the test.
        tester.addCoverageMetric(new ActionCoverage()); // number of @Action methods which have ben executed during the execution of the test.

        final ActionCoverage actionCoverage = (ActionCoverage) tester.getModel().getListener("action coverage");

        do{
            tester.generate();
        }
        while (actionCoverage.getCoverage() != actionCoverage.getMaximum());
        System.out.println((actionCoverage.getCoverage()));
    }

    //Test runner
    @Test
    public void YouStockItModelRunnerStateCoverage() {
        final GreedyTester tester = new GreedyTester(new YouStockItModelTest()); // test generator that can generate random walks, giving preference to transitions that have never been taken before
        tester.setRandom(new Random()); // random path each time the model is run.
        tester.buildGraph();
        tester.addListener(new StopOnFailureListener()); // forces the test class to stop running as soon as a failure is encountered.
        tester.addListener("verbose");
        tester.addCoverageMetric(new TransitionPairCoverage()); // number of paired transitions traversed during the execution of the test.
        tester.addCoverageMetric(new StateCoverage()); // number of states which have been visited during the execution of the test.
        tester.addCoverageMetric(new ActionCoverage()); // number of @Action methods which have ben executed during the execution of the test.

        final StateCoverage stateCoverage = (StateCoverage) tester.getModel().getListener("state coverage");

        do{
            tester.generate();
        }
        while (stateCoverage.getCoverage() != stateCoverage.getMaximum());
        System.out.println((stateCoverage.getCoverage()));
    }
}
