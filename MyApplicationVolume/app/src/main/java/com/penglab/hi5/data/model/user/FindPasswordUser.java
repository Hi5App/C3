package com.penglab.hi5.data.model.user;

/**
 * Data class that captures user information when find back password
 * Created by Jackiexing on 12/7/21
 */
public class FindPasswordUser extends User {

    private final String userId;
    private final String email;

    public FindPasswordUser(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
