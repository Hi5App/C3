package com.penglab.hi5.core.ui.check;

import androidx.annotation.Nullable;

public class ImageResult {

    private boolean isSuccess;

    @Nullable
    private String error;

    public ImageResult(boolean isSuccess, @Nullable String error) {
        this.isSuccess = isSuccess;
        this.error = error;
    }

    public ImageResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    @Nullable
    public String getError() {
        return error;
    }
}
