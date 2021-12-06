package com.penglab.hi5.core.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {

    private final String userId;
    private final String displayName;

    public LoggedInUserView(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}