package com.penglab.hi5.core.net;


import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtilsUser extends HttpUtils {

    private static final String URL_REGISTER = "http://192.168.3.158:8000/user/register";
    private static final String URL_LOGIN = "http://192.168.3.158:8000/user/login";
    private static final String URL_UPDATE_PASSWORD = "http://192.168.3.158:8000/user/updatepassword";
    private static final String URL_FIND_PASSWORD = "http://192.168.3.158:8000/user/forgetpassword";

    /* 登录 异步方法 */
    public static void loginWithOkHttp(String account, String password, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", account)
                    .put("password", password)
                    .put("limit", 1)));
            asyncRequest(URL_LOGIN, body, callback);
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
                    .put("password", password)
                    .put("nickname", nickname)));
            asyncRequest(URL_REGISTER, body, callback);
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
            asyncRequest(URL_FIND_PASSWORD, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 更新密码 异步方法 */
    public static void updatePasswordWithOkHttp(String email, String password, String n_password, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("email", email)
                    .put("password", password)
                    .put("n_password", n_password)));
            asyncRequest(URL_UPDATE_PASSWORD, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}