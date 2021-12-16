package com.penglab.hi5.core.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {

    private final String userId;
    private final String nickName;

    public LoggedInUserView(String userId, String nickName) {
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