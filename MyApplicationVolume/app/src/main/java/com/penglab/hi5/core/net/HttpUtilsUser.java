package com.penglab.hi5.core.net;


import android.util.Log;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class HttpUtilsUser extends HttpUtils {
    private static final String URL_REGISTER = SERVER_IP + "/dynamic/user/register";
    private static final String URL_LOGIN = SERVER_IP + "/dynamic/user/login";
    private static final String URL_UPDATE_PASSWORD = SERVER_IP + "/dynamic/user/updatepassword";
    private static final String URL_FIND_PASSWORD = SERVER_IP + "/dynamic/user/forgetpassword";

    /* 登录 异步方法 */
    public static void loginWithOkHttp(JSONObject userInfo, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("user", userInfo)));
            asyncPostRequest(URL_LOGIN, body, callback);
            Log.e("loginwithokhttp",String.valueOf(new JSONObject().put("user",userInfo)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 注册 异步方法 */
    public static void registerWithOkHttp(String email, String username, String nickname, String password, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", username)
                    .put("email", email)
                    .put("passwd", password)
                    .put("nickname", nickname)));
            asyncPostRequest(URL_REGISTER, body, callback);
            Log.e("register",String.valueOf(new JSONObject()
                    .put("name", username)
                    .put("email", email)
                    .put("passwd", password)
                    .put("nickname", nickname)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 找回密码 异步方法 */
    public static void findPasswordWithOkHttp(String username, String email, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("email", email)
                    .put("username", username)));
            asyncPostRequest(URL_FIND_PASSWORD, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 更新密码 异步方法 */
    public static void updatePasswordWithOkHttp(String email, String password, String n_password, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("email", email)
                    .put("passwd", password)
                    .put("n_password", n_password)));
            asyncPostRequest(URL_UPDATE_PASSWORD, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}