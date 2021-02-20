package com.youstockit.services;

import com.youstockit.users.User;

public interface OrderService {
    public int numCallsReorder = 0;

    public void reOrder();
}
