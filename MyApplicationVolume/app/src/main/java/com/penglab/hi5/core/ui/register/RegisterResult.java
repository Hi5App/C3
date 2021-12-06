package com.penglab.hi5.core.ui.register;

import androidx.annotation.Nullable;

public class RegisterResult {
   @Nullable
    private RegisterView success;
    @Nullable
    private Integer error;
    @Nullable
    private String errorString;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable String errorString){
        this.errorString = errorString;
    }

    RegisterResult(@Nullable RegisterView success) {
        this.success = success;
    }

    @Nullable
    RegisterView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }

    @Nullable
    String getErrorString() {
        return errorString;
    }
}
