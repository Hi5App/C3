package com.main.core.ui.login.data;

import android.util.Log;

import com.main.core.ui.login.data.model.LoggedInUser;

public class RegisterRespository {
    private static volatile RegisterRespository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private RegisterRespository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static RegisterRespository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new RegisterRespository(dataSource);
        }
        return instance;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<LoggedInUser> register(String email, String username, String nickname, String password, String inviterCode) {
        // handle login
        Result<LoggedInUser> result = dataSource.register(email, username, nickname, password, inviterCode);
        Log.d("RegisterRespository", "register");
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }
}
