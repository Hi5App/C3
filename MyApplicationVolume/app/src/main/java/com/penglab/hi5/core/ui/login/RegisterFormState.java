package com.penglab.hi5.core.ui.login;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer nicknameError;

    private boolean isDataValid;

    RegisterFormState(@Nullable Integer emailError, @Nullable Integer usernameError, @Nullable Integer passwordError, @Nullable Integer nicknameError) {
        this.emailError = emailError;
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.nicknameError = nicknameError;
        this.isDataValid = false;
    }

    RegisterFormState(boolean isDataValid) {
        this.emailError = null;
        this.usernameError = null;
        this.passwordError = null;
        this.nicknameError = null;
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

    @Nullable
    Integer getNicknameError() {
        return nicknameError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
