package com.example.datastore.database;

import org.litepal.annotation.Column;

public class User {

    @Column(unique = true, defaultValue = "unknown")
    private String id;

    @Column(nullable = false, defaultValue = "unknown")
    private String name;



}
