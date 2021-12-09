package com.penglab.hi5.data.model.user;

/**
 * Data class that captures user information when find back password
 * Created by Jackiexing on 12/7/21
 */
public class FindPasswordUser {

    private final String username;
    private final String email;

    public FindPasswordUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
