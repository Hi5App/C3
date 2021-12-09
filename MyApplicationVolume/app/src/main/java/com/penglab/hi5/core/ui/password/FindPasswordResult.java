package com.penglab.hi5.core.ui.password;

import androidx.annotation.Nullable;

/**
 * Created by Jackiexing on 12/7/21
 */
public class FindPasswordResult {

    @Nullable
    private FindPasswordView success;
    @Nullable
    private String errorString;

    public FindPasswordResult(@Nullable FindPasswordView success) {
        this.success = success;
    }

    public FindPasswordResult(@Nullable String errorString) {
        this.errorString = errorString;
    }

    @Nullable
    public FindPasswordView getSuccess() {
        return success;
    }

    @Nullable
    public String getErrorString() {
        return errorString;
    }
}
