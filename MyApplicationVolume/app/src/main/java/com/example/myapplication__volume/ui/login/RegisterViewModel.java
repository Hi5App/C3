package com.example.myapplication__volume.ui.login;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication__volume.R;
import com.example.myapplication__volume.data.LoginRepository;
import com.example.myapplication__volume.data.RegisterRespository;
import com.example.myapplication__volume.data.Result;
import com.example.myapplication__volume.data.model.LoggedInUser;

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

    public void register(String email, String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = registerRespository.register(email, username, password);

        Log.d("RegisterViewModel", "register");

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            registerResult.setValue(new RegisterResult(new LoggedInUserView(data.getDisplayName())));

        } else {
//            registerResult.setValue(new RegisterResult(R.string.register_failed));
            registerResult.setValue(new RegisterResult(((Result.Error) result).getError().getMessage()));
        }
    }

    public void registerDataChanged(String email, String username, String password) {
        if (!isEmailValid(email)){
            registerFormState.setValue(new RegisterFormState(R.string.invalid_email, null, null));
        } else if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_password));
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
