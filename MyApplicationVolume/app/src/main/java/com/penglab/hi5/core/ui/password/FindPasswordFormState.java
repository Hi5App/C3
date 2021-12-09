package com.penglab.hi5.core.ui.password;

import androidx.annotation.Nullable;

/**
 * Created by Jackiexing on 12/7/21
 */
public class FindPasswordFormState {
    @Nullable
    private final Integer emailError;
    @Nullable
    private final Integer usernameError;

    private final boolean isDataValid;

    FindPasswordFormState(@Nullable Integer emailError, @Nullable Integer usernameError) {
        this.emailError = emailError;
        this.usernameError = usernameError;
        this.isDataValid = false;
    }

    FindPasswordFormState(boolean isDataValid) {
        this.emailError = null;
        this.usernameError = null;
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

    boolean isDataValid() {
        return isDataValid;
    }
}
