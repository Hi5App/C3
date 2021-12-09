package com.penglab.hi5.core.net;


import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtilsUser {

    private static final String URL_REGISTER = "http://192.168.3.158:8000/user/register";
    private static final String URL_LOGIN = "http://192.168.3.158:8000/user/login";
    private static final String URL_UPDATE_PASSWORD = "http://192.168.3.158:8000/user/updatepassword";
    private static final String URL_FIND_PASSWORD = "http://192.168.3.158:8000/user/forgetpassword";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /* 登录 同步方法 */
    public static Response loginWithOkHttp(String account, String password){
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", account)
                    .put("password", password)
                    .put("limit",1)));
            Request request = new Request.Builder()
                    .url(URL_LOGIN)
                    .post(body)
                    .build();
            response = client.newCall(request).execute();
            client.connectionPool().evictAll();
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    /* 注册 同步方法 */
    public static Response registerWithOkHttp(String email, String username, String nickname, String password){
        Response response;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", username)
                    .put("email", email)
                    .put("password", password)
                    .put("nickname", nickname)));
            Request request = new Request.Builder()
                    .url(URL_REGISTER)
                    .post(body)
                    .build();
            response = client.newCall(request).execute();
            client.connectionPool().evictAll();
        }catch (Exception e){
            response = null;
            e.printStackTrace();
        }
        return response;
    }

    /* 找回密码 同步方法 */
    public static Response findPasswordWithOkHttp(String username, String email){
        Response response;
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("email", email)
                    .put("username", username)));
            Request request = new Request.Builder()
                    .url(URL_FIND_PASSWORD)
                    .post(body)
                    .build();
            response = client.newCall(request).execute();
            client.connectionPool().evictAll();
        }catch (Exception e){
            response = null;
            e.printStackTrace();
        }
        return response;
    }

    // 更新密码
    public static void updatePasswordWithOkHttp(String email, String password, String n_password, okhttp3.Callback callback){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("email", email)
                    .put("password", password)
                    .put("n_password", n_password)));
            Request request = new Request.Builder()
                    .url(URL_UPDATE_PASSWORD)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);
            client.connectionPool().evictAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //添加好友
    public static void addFriendsWithOkHttp(String address, String username, String peer, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();;
        RequestBody body = new FormBody.Builder()
                .add("Username", username)
                .add("Peer", peer)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
        client.connectionPool().evictAll();
    }

    //查询好友
    public static void queryFriendsWithOkHttp(String address, String username, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();;
        RequestBody body = new FormBody.Builder()
                .add("Username", username)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
        client.connectionPool().evictAll();
    }
}