package com.penglab.hi5.core.ui.home.screens;

import static com.penglab.hi5.basic.utils.FileHelper.recursionDeleteFile;
import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.getContext;
import static com.penglab.hi5.data.ResourceDataSource.ALREADY_LATEST_VERSION;
import static com.penglab.hi5.data.UserDataSource.LOGOUT_SUCCESS;
import static com.penglab.hi5.data.model.user.LogStatus.GUEST;
import static com.penglab.hi5.data.model.user.LogStatus.LOGIN;
import static com.penglab.hi5.data.model.user.LogStatus.LOGOUT;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.data.ResourceDataSource;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.user.LogStatus;
import com.penglab.hi5.data.model.user.LoggedInUser;

import java.io.File;

public class HomeViewModel extends ViewModel {

    private final String TAG = "HomeViewModel";

    public enum WorkStatus{
        START_TO_DOWNLOAD_APK, ALREADY_LATEST_VERSION, NONE
    }

    private final MutableLiveData<UserView> userView = new MutableLiveData<>();
    private final MutableLiveData<LogStatus> logStatus = new MutableLiveData<>();
    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private final MutableLiveData<FilePath> apkPath = new MutableLiveData<>();
    private final MutableLiveData<String> apkUrl = new MutableLiveData<>();
    private final MutableLiveData<WorkStatus> workStatus = new MutableLiveData<>();

    private final UserInfoRepository userInfoRepository;
    private final UserDataSource userDataSource;
    private final ResourceDataSource resourceDataSource;

    public HomeViewModel(UserInfoRepository userInfoRepository, UserDataSource userDataSource, ResourceDataSource resourceDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.userDataSource = userDataSource;
        this.resourceDataSource = resourceDataSource;
    }

    public LiveData<UserView> getUserView() {
        return userView;
    }

    public LiveData<LogStatus> getLogStatus() {
        return logStatus;
    }

    public LiveData<Integer> getScore() {
        return score;
    }

    public MutableLiveData<FilePath> getApkPath() {
        return apkPath;
    }

    public MutableLiveData<String> getApkUrl() {
        return apkUrl;
    }

    public MutableLiveData<WorkStatus> getWorkStatus() {
        return workStatus;
    }

    UserDataSource getUserDataSource() {
        return userDataSource;
    }

    ResourceDataSource getResourceDataSource() {
        return resourceDataSource;
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
    public void updateResourceResult(Result result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof String) {
                String dataContent = (String) data;
                if (dataContent.equals(ALREADY_LATEST_VERSION)) {
                    workStatus.setValue(WorkStatus.ALREADY_LATEST_VERSION);
                } else if (dataContent.endsWith(".apk")) {
                    apkUrl.setValue(dataContent);
                }
            } else if (data instanceof FilePath) {
                apkPath.setValue((FilePath) data);
            }
        } else if (result instanceof Result.Error){
            ToastEasy(result.toString());
        }
    }
    public void checkLatestVersion(String localVersionName) {
        resourceDataSource.checkLatestVersion(localVersionName);
    }
    public void downloadLatestVersion(String url, String filename){
        workStatus.setValue(WorkStatus.START_TO_DOWNLOAD_APK);
        resourceDataSource.downloadLatestVersion(url, filename);
    }
    public void cleanImgCache() {
        File file = new File(getContext().getExternalFilesDir(null).toString() + "/Image");
        recursionDeleteFile(file);
    }
}
