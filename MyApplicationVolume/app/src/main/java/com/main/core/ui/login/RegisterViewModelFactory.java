package com.main.core.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.main.core.ui.login.data.LoginDataSource;
import com.main.core.ui.login.data.RegisterRespository;

public class RegisterViewModelFactory implements ViewModelProvider.Factory  {
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(RegisterRespository.getInstance(new LoginDataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
