package com.penglab.hi5.data.model.user;

/**
 * Data class that captures user information when register
 * Created by Jackiexing on 12/7/21
 */
public class RegisterUser {

    private final String userId;
    private final String nickName;

    public RegisterUser(String userId, String nickName) {
        this.userId = userId;
        this.nickName = nickName;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }
}
