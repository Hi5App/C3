package com.penglab.hi5.core.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.R;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.user.LoggedInUser;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final UserInfoRepository userInfoRepository;
    private final UserDataSource userDataSource;

    public LoginViewModel(UserInfoRepository userInfoRepository, UserDataSource userDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.userDataSource = userDataSource;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    UserDataSource getUserDataSource(){
        return userDataSource;
    }

    public void login(String username, String password) {
        // launched in a separate asynchronous job
        userDataSource.login(username, password);
    }

    public void updateLoginResult(Result<LoggedInUser> result){
        // will be called in LoginActivity
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            userInfoRepository.setLoggedInUser(data);
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUserId(), data.getNickName())));
        } else {
            loginResult.setValue(new LoginResult(result.toString()));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}