package com.penglab.hi5.data;

import static com.penglab.hi5.core.MainActivity.ifGuestLogin;
import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.getContext;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.penglab.hi5.R;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsResource;
import com.penglab.hi5.core.net.HttpUtilsUser;
import com.penglab.hi5.core.ui.home.screens.HomeActivity;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.data.dataStore.PreferenceLogin;
import com.penglab.hi5.data.model.user.FindPasswordUser;
import com.penglab.hi5.data.model.user.LoggedInUser;
import com.penglab.hi5.data.model.user.RegisterUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Class that handles user information w/ server.
 *
 * Created by Jackiexing on 12/7/21
 */
public class UserDataSource {

    public static final String LOGOUT_SUCCESS = "Logout Success";

    private final String TAG = "UserDataSource";
    private final String REGISTER_SUCCESS = "Register Success";
    private final String LOGIN_SUCCESS = "Login Success";
    private final MutableLiveData<Result> result = new MutableLiveData<>();
    private String responseData;

    public LiveData<Result> getResult() {
        return result;
    }

    /**
     * called in LoginViewModel & SplashScreenViewModel, change of result will be observe in LoginActivity & SplashScreenActivity
     */
    public void login(String username, String password) {
        try {
            JSONObject userInfo = new JSONObject().put("name", username).put("passwd", password);
            HttpUtilsUser.loginWithOkHttp(userInfo, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect Failed When Login")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        responseData = response.body().string();
                        Log.e(TAG, "responseData: " + responseData);
                        try {
                            JSONObject userInfo = new JSONObject(responseData);
                            LoggedInUser loggedInUser = new LoggedInUser(
                                    userInfo.getString("name"),
                                    userInfo.getString("nickname"),
                                    userInfo.getString("email"));
                            result.postValue(new Result.Success<LoggedInUser>(loggedInUser));
                            storeUserCache(username, password);

                            response.body().close();
                            response.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            result.postValue(new Result.Error(new Exception("Fail to parse user info !")));
                        }
                    } else {
                        result.postValue(new Result.Error(new IOException(responseData)));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    /**
     * called in RegisterViewModel, change of result will be observe in RegisterActivity
     */
    public void register(String email, String username, String nickname, String password, String inviterCode) {
        try {
            HttpUtilsUser.registerWithOkHttp(email, username, nickname, password, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect Failed When Register")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    Log.e("RegisterViewModel","ResponseCode: " + response.code());
                    if (responseCode == 200) {
                        RegisterUser registerUser = new RegisterUser(username, nickname);
                        result.postValue(new Result.Success<RegisterUser>(registerUser));

                        response.body().close();
                        response.close();
                    } else if (responseCode == 501) {
                        result.postValue(new Result.Error(new IOException("Fail to register !")));
                    } else {
                        result.postValue(new Result.Error(new IOException(responseData)));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    /**
     * called in FindPasswordViewModel, change of result will be observe in FindPasswordActivity
     */
    public void findPassword(String username, String email) {
        try {
            HttpUtilsUser.findPasswordWithOkHttp(username, email, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect Failed When Register")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseData = response.body().string();
                    Log.e(TAG, "responseData: " + responseData);

                    if (responseData.equals("ForgetPassword Success")) {
                        FindPasswordUser findPasswordUser = new FindPasswordUser(username, email);
                        result.postValue(new Result.Success<FindPasswordUser>(findPasswordUser));
                    } else {
                        result.postValue(new Result.Error(new IOException(responseData)));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    /**
     * Login Nim account
     */
    public void loginNim(String username, String password, LoggedInUser loggedInUser) {
        NimUIKit.login(new LoginInfo(loggedInUser.getUserId(), password),
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        Log.e(TAG, "login success");
                        NimUIKit.loginSuccess(loggedInUser.getUserId());
                        result.postValue(new Result.Success<>(loggedInUser));
                    }

                    @Override
                    public void onFailed(int code) {
                        Log.e(TAG, "login failed");
                        if (code == 302 || code == 404) {
                            result.postValue(new Result.Error(new Exception(getContext().getString(R.string.login_failed))));
                        } else if (code == 408) {
                            result.postValue(new Result.Error(new Exception("Nim: Time out " + username)));
                        } else {
                            result.postValue(new Result.Error(new Exception("Nim: Login Failed, Fail Code: " + code)));
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        result.postValue(new Result.Error(new Exception("Nim: Login Failed")));
                    }
                });
    }

    /**
     * Logout account, clear local info
     */
    public void logout() {
        // 清理缓存 & 注销监听 & 清除状态
        NimUIKit.logout();
        NIMClient.getService(AuthService.class).logout();

        PreferenceLogin preferenceLogin = PreferenceLogin.getInstance();
        preferenceLogin.setPref(
                preferenceLogin.getUsername(),
                preferenceLogin.getPassword(),
                false,
                true);

        result.setValue(new Result.Success<String>(LOGOUT_SUCCESS));
    }

    private void storeUserCache(String username, String password){
        InfoCache.setAccount(username);
        InfoCache.setToken(password);
    }
}
