package com.penglab.hi5.core.ui.login;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.login.data.RegisterRespository;
import com.penglab.hi5.core.ui.login.data.Result;
import com.penglab.hi5.core.ui.login.data.model.LoggedInUser;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private RegisterRespository registerRespository;

    RegisterViewModel(RegisterRespository registerRespository) {
        this.registerRespository = registerRespository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String email, String username, String nickname, String password, String inviterCode) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = registerRespository.register(email, username, nickname, password, inviterCode);

        Log.d("RegisterViewModel", "register");

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            registerResult.setValue(new RegisterResult(new LoggedInUserView(data.getDisplayName())));

        } else {
//            registerResult.setValue(new RegisterResult(R.string.register_failed));
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
        if (username == null) {
            return false;
        }

        return !username.trim().isEmpty();

    }

    // A placeholder username validation check
    private boolean isNickNameValid(String nickname) {
        if (nickname == null) {
            return false;
        }

        return !nickname.trim().isEmpty();

    }

    private boolean isEmailValid(String email){
        if (email == null){
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
