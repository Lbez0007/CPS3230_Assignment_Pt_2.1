package com.youstockit.spies;

import com.youstockit.services.OrderService;

public class OrderServiceSpy implements OrderService {
    public int numCallsReorder = 0;

    public void reOrder(){
        numCallsReorder++;
    };
}
