package com.youstockit.stubs;

import com.youstockit.SupplierErrorCode;
import com.youstockit.services.SupplierOrderService;

public class SupplierOrderServiceStub implements SupplierOrderService {
    public int getQuantitySupplied(){
        return 10;
    };

    public SupplierErrorCode getOrderCode(){
        return SupplierErrorCode.SUCCESS;
    };
}
