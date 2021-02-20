package com.youstockit.users;

import java.util.UUID;

public class Manager extends User {

    protected String id;
    protected String name;
    protected String email;

    public Manager(String name, String email) {
        super(name, email);
        id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }
}
