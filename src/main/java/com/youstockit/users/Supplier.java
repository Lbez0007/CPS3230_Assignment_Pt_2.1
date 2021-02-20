package com.youstockit.users;

import com.youstockit.SupplierServer;

import java.util.UUID;

public class Supplier extends User {

    protected String id;
    protected String name;
    protected String email;
    public SupplierServer supplierServer;

    public Supplier(String name, String email) {
        super(name, email);
        id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }

    public Supplier(String name, String email, SupplierServer supplierServer) {
        super(name, email);
        id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.supplierServer = supplierServer;
    }
}
