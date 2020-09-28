package com.example.myapplication__volume.ui.login;

import androidx.annotation.Nullable;

public class RegisterResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
