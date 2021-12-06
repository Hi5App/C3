package com.penglab.hi5.core.ui.register;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable
    private final Integer emailError;
    @Nullable
    private final Integer usernameError;
    @Nullable
    private final Integer passwordError;
    @Nullable
    private final Integer nicknameError;

    private final boolean isDataValid;

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
