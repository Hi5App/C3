package com.penglab.hi5.core.ui;


import androidx.annotation.Nullable;

/**
 * Resources download Result: success or error message.
 *
 * Created by Jackiexing on 12/13/21
 */
public class ResourceResult {
    @Nullable
    private boolean isSuccess;

    private String error;

    public ResourceResult(boolean isSuccess, @Nullable String error) {
        this.isSuccess = isSuccess;
        this.error = error;
    }

    public ResourceResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Nullable
    public boolean isSuccess() {
        return isSuccess;
    }

    public String getError() {
        return error;
    }
}
