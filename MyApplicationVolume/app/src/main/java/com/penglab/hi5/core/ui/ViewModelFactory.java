package com.penglab.hi5.core.ui;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.penglab.hi5.core.ui.login.LoginViewModel;
import com.penglab.hi5.core.ui.password.FindPasswordViewModel;
import com.penglab.hi5.core.ui.register.RegisterViewModel;
import com.penglab.hi5.data.FindPasswordDataSource;
import com.penglab.hi5.data.LoginDataSource;
import com.penglab.hi5.data.RegisterDataSource;
import com.penglab.hi5.data.UserInfoRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(UserInfoRepository.getInstance(new LoginDataSource()));
        } else if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(new RegisterDataSource());
        } else if (modelClass.isAssignableFrom(FindPasswordViewModel.class)) {
            return (T) new FindPasswordViewModel(new FindPasswordDataSource());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}