package com.penglab.hi5.core.ui.register;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.R;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.model.user.RegisterUser;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private final UserDataSource registerDataSource;

    public RegisterViewModel(UserDataSource registerDataSource) {
        this.registerDataSource = registerDataSource;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    UserDataSource getRegisterDataSource(){
        return registerDataSource;
    }

    public void register(String email, String username, String nickname, String password, String inviterCode) {
        // launched in a separate asynchronous job
        registerDataSource.register(email, username, nickname, password, inviterCode);
    }

    public void updateRegisterResult(Result<RegisterUser> result){
        // will be called in RegisterActivity
        if (result instanceof Result.Success) {
            RegisterUser data = ((Result.Success<RegisterUser>) result).getData();
            registerResult.setValue(new RegisterResult(new RegisterView(data.getUserId())));
        } else {
            registerResult.setValue(new RegisterResult(((Result.Error) result).getError().getMessage()));
        }
    }

    public void registerDataChanged(String email, String username, String nickname, String password) {
        if (!isEmailValid(email)){
            registerFormState.setValue(new RegisterFormState(R.string.invalid_email, null, null, null));
        } else if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_password, null));
        } else if (!isNickNameValid(nickname)) {
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.invalid_nickname));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return username != null && !username.trim().isEmpty();
    }

    // A placeholder username validation check
    private boolean isNickNameValid(String nickname) {
        return nickname != null && !nickname.trim().isEmpty();
    }

    // A placeholder email validation check
    private boolean isEmailValid(String email){
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
