package com.youstockit.stubs;

import com.youstockit.services.OrderService;

public class OrderServiceStub implements OrderService {
    public int numCallsReorder = 0;

    public void reOrder(){
        numCallsReorder++;
    };
}
