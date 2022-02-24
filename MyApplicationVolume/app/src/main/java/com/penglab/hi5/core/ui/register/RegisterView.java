package com.penglab.hi5.core.ui.register;

/**
 * Class exposing authenticated user details to the UI.
 */
public class RegisterView {
    private final String displayName;
    //... other data fields that may be accessible to the UI

    public RegisterView(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}


