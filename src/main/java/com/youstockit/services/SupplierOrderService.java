package com.youstockit.services;

import com.youstockit.SupplierErrorCode;

public interface SupplierOrderService {

    public int getQuantitySupplied();

    public SupplierErrorCode getOrderCode();
}
