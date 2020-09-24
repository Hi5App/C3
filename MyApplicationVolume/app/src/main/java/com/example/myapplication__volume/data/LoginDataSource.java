package com.example.myapplication__volume.data;

import android.util.Log;

import com.example.myapplication__volume.data.model.LoggedInUser;
import com.example.server_connect.HttpUtil;
import com.tracingfunc.gd.V_NeuronSWC;
import com.tracingfunc.gd.V_NeuronSWC_list;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private String responseData;
    private boolean ifResponsed = false;
    private final String ip = "http:www.baidu.com";
    private final String ipLogin = "http:10.194.211.159:8080/Server_C3/LoginServlet";
    private final String ipRegister = "http:10.194.211.159:8080/Server_C3/RegisterServlet";
//    private final String ipLogin = "http:39.100.35.131:8080/Server_C3/LoginServlet";
//    private final String ipRegister = "http:39.100.35.131:8080/Server_C3/RegisterServlet";

    public Result<LoggedInUser> login(String username, String password) {

        try {
            loginWithOkHttp(ipLogin, username, password);
            while (!ifResponsed){
                System.out.println("aaa");
            }
            ifResponsed = false;
            if (responseData.equals("true")){
                Log.d("LoginDataSource", "login");
                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                username);
                return new Result.Success<>(fakeUser);
            } else {
                Log.d("LoginDataSource", "Result.Error");
                return new Result.Error(new IOException(responseData));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<LoggedInUser> register(String email, String username, String password){
        try {
            registerWithOkHttp(ipRegister, email, username, password);
            while (!ifResponsed){ }
            ifResponsed = false;
            if (responseData.equals("true")) {
                Log.d("LoginDataSource", "register");
                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                username);
                return new Result.Success<>(fakeUser);
            } else {
                return new Result.Error(new IOException(responseData));
            }


        } catch (Exception e) {
            return new Result.Error(new IOException("Error registering", e));
        }
    }

    private void loginWithOkHttp(String address, String account, String password){
        HttpUtil.loginWithOkHttp(address, account, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseData = "Connect Failed When Login";
                ifResponsed = true;
                Log.d("loginWithHttp", "onFailure: " + responseData);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                ifResponsed = true;
                Log.d("loginWithHttp", "responseData: " + responseData);
//                runOnUiThread
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    OkHttpClient client = new OkHttpClient();    //创建OkHClient实例
//                    Request request = new Request.Builder()     //发请求创建一个Request对象
//                            .url("http:www.baidu.com")
//                            .build();
//                    Response response = client.newCall(request).execute();  //发请求获取服务器返回的数据
//                    String responseData = response.body().string();
//
//                    Log.d("loginWithOkHttp", "responseData: " + responseData);
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();

    }

    private void registerWithOkHttp(String address, String email, String username, String password){
        HttpUtil.registerWithOkHttp(address, email, username, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseData = "Connect Failed When Register";
                ifResponsed = true;
                Log.d("RegisterWithHttp", "responseData: " + responseData);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                ifResponsed = true;
                Log.d("RegisterWithHttp", "responseData: " + responseData);
            }
        });
    }

    public void logout() {
        // TODO: revoke authentication
    }
}