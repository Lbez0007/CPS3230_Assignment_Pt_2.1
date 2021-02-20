package com.youstockit;

public class SupplierResponse {

    protected int qtyItemsRequested;
    protected int qtyItemsProvided;
    protected SupplierErrorCode supplierErrorCode;

    public SupplierResponse(int qtyItemsRequested, int qtyItemsProvided, SupplierErrorCode supplierErrorCode){
        this.qtyItemsRequested = qtyItemsRequested;
        this.qtyItemsProvided = qtyItemsProvided;
        this.supplierErrorCode = supplierErrorCode;
    }
}
