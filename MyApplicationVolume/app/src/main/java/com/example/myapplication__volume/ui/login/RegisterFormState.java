package com.example.myapplication__volume.ui.login;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    private boolean isDataValid;

    RegisterFormState(@Nullable Integer emailError, @Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.emailError = emailError;
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.emailError = null;
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
