package com.penglab.hi5.core.ui.password;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.R;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.model.user.FindPasswordUser;

/**
 * Created by Jackiexing on 12/7/21
 */
public class FindPasswordViewModel extends ViewModel {

    private final MutableLiveData<FindPasswordFormState> findPasswordFormState = new MutableLiveData<>();
    private final MutableLiveData<FindPasswordResult> findPasswordResult = new MutableLiveData<>();
    private final UserDataSource userDataSource;

    public FindPasswordViewModel(UserDataSource userDataSource){
        this.userDataSource = userDataSource;
    }

    public LiveData<FindPasswordFormState> getFindPasswordFormState(){
        return findPasswordFormState;
    }

    public UserDataSource getUserDataSource(){
        return userDataSource;
    }

    public LiveData<FindPasswordResult> getFindPasswordResult(){
        return findPasswordResult;
    }

    public void findPassword(String username, String email){
        userDataSource.findPassword(username, email);
    }

    public void updateFindPasswordResult(Result<FindPasswordUser> result){
        // will be called in FindPasswordActivity
        if (result instanceof Result.Success) {
            FindPasswordUser data = ((Result.Success<FindPasswordUser>) result).getData();
            findPasswordResult.setValue(new FindPasswordResult(new FindPasswordView(data.getUserId())));
        } else {
            findPasswordResult.setValue(new FindPasswordResult(((Result.Error) result).getError().getMessage()));
        }
    }

    public void userDataChanged(String username, String email){
        if (!isUserNameValid(username)){
            findPasswordFormState.setValue(new FindPasswordFormState(null, R.string.invalid_username));
        } else if (!isEmailValid(email)){
            findPasswordFormState.setValue(new FindPasswordFormState(R.string.invalid_email, null));
        }else {
            findPasswordFormState.setValue(new FindPasswordFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        return username != null && !username.trim().isEmpty();
    }

    // A placeholder email validation check
    private boolean isEmailValid(String email){
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
