package com.penglab.hi5.core.ui.home.screens;

import androidx.annotation.Nullable;

import com.penglab.hi5.core.ui.login.LoggedInUserView;

/**
 * Class exposing authenticated user details to the UI.
 *
 * Created by Jackiexing on 12/14/21
 */
public class UserView {

    @Nullable
    private final String userId;
    @Nullable
    private final String nickName;
    @Nullable
    private final String email;

    public UserView(@Nullable String userId, @Nullable String nickName, @Nullable String email) {
        this.userId = userId;
        this.nickName = nickName;
        this.email = email;
    }

    public UserView() {
        this.userId = null;
        this.nickName = null;
        this.email = null;
    }

    @Nullable
    public String getUserId() {
        return userId;
    }

    @Nullable
    public String getNickName() {
        return nickName;
    }

    @Nullable
    public String getEmail() {
        return email;
    }
}
