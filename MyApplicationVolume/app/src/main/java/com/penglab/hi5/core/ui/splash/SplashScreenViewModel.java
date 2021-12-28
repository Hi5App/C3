package com.penglab.hi5.core.ui.splash;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.login.LoggedInUserView;
import com.penglab.hi5.data.ResourceDataSource;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.user.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class SplashScreenViewModel extends ViewModel {

    private final String TAG = "SplashScreenViewModel";
    public static final String MUSIC_EXIST = "All the music resources already exist.";
    public static final String MUSIC_DOWNLOAD_FINISHED = "All the music resources are downloaded.";

    private final MutableLiveData<ResourceResult> musicResult = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final UserInfoRepository userInfoRepository;
    private final ResourceDataSource resourceDataSource;
    private final UserDataSource userDataSource;

    private int musicNumAlreadyDownload = 0;
    private int musicSumToDownload = 0;

    public SplashScreenViewModel(UserInfoRepository userInfoRepository, ResourceDataSource resourceDataSource, UserDataSource userDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.resourceDataSource = resourceDataSource;
        this.userDataSource = userDataSource;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    LiveData<ResourceResult> getMusicResult() {
        return musicResult;
    }

    UserDataSource getUserDataSource() {
        return userDataSource;
    }

    ResourceDataSource getResourceDataSource() {
        return resourceDataSource;
    }

    public void login(String username, String password) {
        // launched in a separate asynchronous job
        userDataSource.login(username, password);
    }

    public void getMusicList() {
        // launched in a separate asynchronous job
        resourceDataSource.getMusicList();
    }

    public void downloadMusic(JSONArray musicArray) {
        // judge if music resources already exist
        File musicDir = FileHelper.getDir(Myapplication.getContext().getExternalFilesDir(null) + "/Resources/Music");

        if (!musicDir.exists() || musicDir.listFiles() == null || musicDir.listFiles().length < musicArray.length()) {
            ToastEasy("Need to download some resources file !");
            musicSumToDownload = musicArray.length();
            musicNumAlreadyDownload = 0;

            for (int i = 0; i < musicSumToDownload; i++) {
                try{
                    JSONObject musicInfo = musicArray.getJSONObject(i);
                    Log.e(TAG, "name: " + musicInfo.getString("name") + ", url: " + musicInfo.getString("url"));
                    resourceDataSource.downloadMusic(musicInfo.getString("name"), musicInfo.getString("url"));
                }catch (Exception e){
                    musicResult.setValue(new ResourceResult(false,"Fail to parse music list !"));
                }
            }
        } else {
            musicResult.setValue(new ResourceResult(true));
        }
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

    public void updateMusicResult(Result result){
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONArray) {
                // get music list successfully
                downloadMusic((JSONArray) data);
            }

            if (data instanceof String){
                // download music successfully
                if (((String) data).equals(MUSIC_DOWNLOAD_FINISHED)){
                    File musicDir = FileHelper.getDir(Myapplication.getContext().getExternalFilesDir(null) + "/Resources/Music");
                    if (musicDir.exists() && musicDir.listFiles() != null && musicDir.listFiles().length >= musicSumToDownload) {
                        musicResult.setValue(new ResourceResult(true));
                    }
                }
            }
        } else {
            musicResult.setValue(new ResourceResult(false, result.toString()));
        }
    }

}
