package com.penglab.hi5.core.ui.annotation;

/**
 * Class exposing authenticated user details to the UI.
 *
 * Created by Jackiexing on 12/09/21
 */
public class UserInfoView {

    private final boolean login;
    private final String userId;
    private final String displayName;

    public UserInfoView(boolean login, String userId, String displayName) {
        this.login = login;
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isLogin() {
        return login;
    }

    public String getDisplayName() {
        return displayName;
    }
}
