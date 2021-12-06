package com.penglab.hi5.core.ui.register;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.R;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.RegisterDataSource;
import com.penglab.hi5.data.model.user.RegisterUser;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private final RegisterDataSource registerDataSource;

    public RegisterViewModel(RegisterDataSource registerDataSource) {
        this.registerDataSource = registerDataSource;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String email, String username, String nickname, String password, String inviterCode) {
        Result<RegisterUser> result = registerDataSource.register(email, username, nickname, password, inviterCode);

        if (result instanceof Result.Success) {
            RegisterUser data = ((Result.Success<RegisterUser>) result).getData();
            registerResult.setValue(new RegisterResult(new RegisterView(data.getNickName())));
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
