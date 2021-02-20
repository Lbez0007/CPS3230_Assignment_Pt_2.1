package com.youstockit;

import com.youstockit.services.EmailService;
import com.youstockit.services.SupplierOrderService;
import com.youstockit.users.Supplier;

public class SupplierServer {

    protected SupplierOrderService supplierOrderService;
    protected EmailService emailService;


    public SupplierResponse[] orderItems(ItemOrder itemOrder[]) {

        SupplierResponse[] supplierResponses = new SupplierResponse[itemOrder.length];

        for (int i = 0; i < itemOrder.length; i++) {
            // Invoke object of type SupplierResponse for every item order of type ItemOrder
            SupplierResponse supplierResponse = new SupplierResponse
                    (itemOrder[i].qtyItemsOrdered, supplierOrderService.getQuantitySupplied(),
                            supplierOrderService.getOrderCode());

            // Assign element i of supplierResponses to object invoked
            supplierResponses[i] = supplierResponse;
        }
        return supplierResponses;
    }

    // Setters for dependency injection
    public void setSupplierOrderService(SupplierOrderService supplierOrderService) {
        this.supplierOrderService = supplierOrderService;
    }

    public void setEmailService(EmailService emailService){
        this.emailService = emailService;
    }
}

