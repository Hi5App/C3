package com.penglab.hi5.data.model.user;

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
