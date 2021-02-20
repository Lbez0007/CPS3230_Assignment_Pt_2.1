package com.youstockit.users;

import com.youstockit.SupplierServer;

import java.util.UUID;

public abstract class User {
    protected String id;
    protected String name;
    protected String email;

    public User(String name, String email) {

        id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }
}
