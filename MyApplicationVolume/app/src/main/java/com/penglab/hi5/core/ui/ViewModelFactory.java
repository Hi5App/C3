package com.penglab.hi5.core.ui;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.penglab.hi5.core.ui.check.CheckViewModel;
import com.penglab.hi5.core.ui.home.screens.HomeViewModel;
import com.penglab.hi5.core.ui.login.LoginViewModel;
import com.penglab.hi5.core.ui.password.FindPasswordViewModel;
import com.penglab.hi5.core.ui.register.RegisterViewModel;
import com.penglab.hi5.core.ui.splash.SplashScreenViewModel;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ResourceDataSource;
import com.penglab.hi5.data.UserDataSource;
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
            // used in LoginActivity
            return (T) new LoginViewModel(UserInfoRepository.getInstance(), new UserDataSource());
        } else if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            // used in RegisterActivity
            return (T) new RegisterViewModel(new UserDataSource());
        } else if (modelClass.isAssignableFrom(FindPasswordViewModel.class)) {
            // used in FindPasswordActivity
            return (T) new FindPasswordViewModel(new UserDataSource());
        } else if (modelClass.isAssignableFrom(SplashScreenViewModel.class)) {
            // used in SplashScreenActivity
            return (T) new SplashScreenViewModel(UserInfoRepository.getInstance(), new ResourceDataSource(), new UserDataSource());
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            // used in HomeActivity
            return (T) new HomeViewModel(UserInfoRepository.getInstance(), new UserDataSource());
        } else if (modelClass.isAssignableFrom(CheckViewModel.class)) {
            // used in HomeActivity
            return (T) new CheckViewModel(new ImageDataSource());
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}