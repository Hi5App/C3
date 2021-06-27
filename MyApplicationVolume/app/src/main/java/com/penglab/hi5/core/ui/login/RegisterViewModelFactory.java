package com.penglab.hi5.core.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.penglab.hi5.core.ui.login.data.DataSource;
import com.penglab.hi5.core.ui.login.data.RegisterRespository;

public class RegisterViewModelFactory implements ViewModelProvider.Factory  {
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(RegisterRespository.getInstance(new DataSource()));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
