package com.penglab.hi5.core.ui.userProfile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import static com.penglab.hi5.basic.utils.FileHelper.recursionDeleteFile;
import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.getContext;
import static com.penglab.hi5.data.UserDataSource.LOGOUT_SUCCESS;
import static com.penglab.hi5.data.model.user.LogStatus.GUEST;
import static com.penglab.hi5.data.model.user.LogStatus.LOGIN;
import static com.penglab.hi5.data.model.user.LogStatus.LOGOUT;

import android.util.Log;

import com.penglab.hi5.core.ui.home.screens.UserView;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.UserPerformanceDataSource;
import com.penglab.hi5.data.model.user.LogStatus;
import com.penglab.hi5.data.model.user.LoggedInUser;
import com.penglab.hi5.data.UserInfoRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class MyViewModel extends ViewModel {

    private MutableLiveData<UserView> userView = new MutableLiveData<>();
    private MutableLiveData<LogStatus> logStatus = new MutableLiveData<>();
    private MutableLiveData<Integer> score = new MutableLiveData<>();
    private MutableLiveData<Integer> somaCount = new MutableLiveData<>();
    private MutableLiveData<Integer> dailySomaCount = new MutableLiveData<>();

    private final UserInfoRepository userInfoRepository;
    private final UserDataSource userDataSource;
    private final UserPerformanceDataSource userPerformanceDataSource;

    public MyViewModel(UserInfoRepository userInfoRepository, UserDataSource userDataSource, UserPerformanceDataSource userPerformanceDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.userDataSource = userDataSource;
        this.userPerformanceDataSource = userPerformanceDataSource;
    }
    public void getUserPerformance(){
        userPerformanceDataSource.getUserPerformance();
    }

    public UserInfoRepository getUserInfoRepository() {
        return userInfoRepository;
    }

    public MutableLiveData<UserView> getUserView() {
        return userView;
    }

    public MutableLiveData<LogStatus> getLogStatus() {
        return logStatus;
    }

    public MutableLiveData<Integer> getScore() {
        return score;
    }

    UserDataSource getUserDataSource() {
        return userDataSource;
    }

    public UserPerformanceDataSource getUserPerformanceDataSource() {
        return userPerformanceDataSource;
    }

    public MutableLiveData<Integer> getSomaCount() {
        return somaCount;
    }

    public MutableLiveData<Integer> getDailySomaCount() {
        return dailySomaCount;
    }

    public boolean isLogged() {
        return logStatus.getValue() != null && logStatus.getValue() == LOGIN;
    }

    public void logout() {
        userDataSource.logout();
    }

    public void updateLogged() {
        if (userInfoRepository.isLoggedIn()) {
            logStatus.setValue(LOGIN);
        } else {
            logStatus.setValue(GUEST);
        }
    }

    public void updateUserView() {
        if (isLogged()) {
            LoggedInUser loggedInUser = userInfoRepository.getUser();
            userView.setValue(new UserView(
                    loggedInUser.getUserId(),
                    loggedInUser.getNickName(),
                    loggedInUser.getEmail()));
        } else {
            userView.setValue(new UserView());
        }
    }

    public void updateScore() {
        if (isLogged()) {
            LoggedInUser loggedInUser = userInfoRepository.getUser();
            score.setValue(loggedInUser.getScore());
        } else {
            score.setValue(0);
        }
    }

    public void updateLogStatus(Result result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof String) {
                if (((String) data).equals(LOGOUT_SUCCESS)) {
                    userInfoRepository.logout();
                    logStatus.setValue(LOGOUT);
                }
            }
        }
    }

    public void cleanImgCache() {
        File file = new File(getContext().getExternalFilesDir(null).toString() + "/Img");
        recursionDeleteFile(file);
    }

    public void updateSomaAndDailySoma(Result result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONArray) {
                JSONArray performanceResult = (JSONArray) data;
                try {
                    somaCount.postValue(performanceResult.getInt(0));
                    dailySomaCount.postValue(performanceResult.getInt(1));
                    Log.e("somaCount", String.valueOf(somaCount));
                    Log.e("dailySomaCount",String.valueOf(dailySomaCount));
                } catch (Exception e) {
                    ToastEasy("Fail to parse jsonArray when get user performance !");
                }
            }
        } else {
            ToastEasy(result.toString());
        }
    }
}


