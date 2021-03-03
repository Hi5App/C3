package com.example.myapplication__volume.data;

import android.util.Log;

import com.example.myapplication__volume.collaboration.ServerConnector;
import com.example.myapplication__volume.data.model.LoggedInUser;
import com.example.server_communicator.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**s
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private String responseData;
    private boolean ifResponsed = false;
//    private final String ipLogin = "http:192.168.1.108:8080/Server_C3/LoginServlet";
//    private final String ipRegister = "http:192.168.1.108:8080/Server_C3/RegisterServlet";
//    private final String ipAddFriends = "http:192.168.1.108:8080/Server_C3/AddFriendsServlet";
//    private final String ipQueryFriends = "http:192.168.1.108:8080/Server_C3/QueryFriendsServlet";

    private final String ipLogin = "http:39.100.35.131:8080/Server_C3/LoginServlet";
    private final String ipRegister = "http:39.100.35.131:8080/Server_C3/RegisterServlet";
    private final String ipAddFriends = "http:39.100.35.131:8080/Server_C3/AddFriendsServlet";
    private final String ipQueryFriends = "http:39.100.35.131:8080/Server_C3/QueryFriendsServlet";

    private final String ip = "39.35.100.131";
    private final String port = "39.35.100.131";

    private static final String EMPTY_MSG = "the msg is empty";
    private static final String TAG = "LoginDataSource";

    public Result<LoggedInUser> login(String username, String password) {

        try {

            Log.e(TAG,"login start !");

            LoginWithSocket(username, password);

            if (responseData.startsWith("LOGIN:0")){
                Log.e(TAG,"login Successfully !");

                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                username);
                return new Result.Success<>(fakeUser);

            }else if (responseData.startsWith("LOGIN:-1")){
                Log.e(TAG, "Result.Error");
                return new Result.Error(new IOException("Something wrong with database !"));
            }else if (responseData.startsWith("LOGIN:-2")){
                Log.e(TAG, "Result.Error");
                return new Result.Error(new IOException("Can not find user !"));
            }else if (responseData.startsWith("LOGIN:-3")){
                Log.e(TAG, "Result.Error");
                return new Result.Error(new IOException("username or password is wrong !"));
            }else {
                Log.e(TAG, "Result.Error");
                return new Result.Error(new IOException("Something else wrong !"));
            }

//            /*
//            just for test
//             */
//            LoggedInUser fakeUser =
//                    new LoggedInUser(
//                            java.util.UUID.randomUUID().toString(),
//                            username);
//            return new Result.Success<>(fakeUser);




//            loginWithOkHttp(ipLogin, username, password);
//            while (!ifResponsed){
//                Log.e("LoginLoop", "AAAAAAAAAAAAAAAAA");
//            }
//            ifResponsed = false;
//            if (responseData.equals("true")){
//                Log.e("LoginDataSource", "login");
//                LoggedInUser fakeUser =
//                        new LoggedInUser(
//                                java.util.UUID.randomUUID().toString(),
//                                username);
//                return new Result.Success<>(fakeUser);
//            } else {
//                Log.e("LoginDataSource", "Result.Error");
//                return new Result.Error(new IOException(responseData));
//            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<LoggedInUser> register(String email, String username, String nickname, String password, String inviterCode){
        try {

            RegisterWithSocket(username, password, email, nickname, inviterCode);

            if (responseData.equals("REGISTER:0")){
                Log.e(TAG, "register successfully !");
                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                username);
                return new Result.Success<>(fakeUser);
            }else if (responseData.equals("REGISTER:-2")){
                Log.e(TAG, "fail to register !");
                return new Result.Error(new IOException("User already exist !"));
            }else {
                Log.e(TAG, "fail to register !");
                return new Result.Error(new IOException("Something wrong with database !"));
            }



//            registerWithOkHttp(ipRegister, email, username, nickname, password);
//            while (!ifResponsed){
//                Log.e("RegisterLoop", "AAAAAAAAAAAAAAAAA");
//            }
//            ifResponsed = false;
//            if (responseData.equals("true")) {
//                Log.e("LoginDataSource", "register");
//                LoggedInUser fakeUser =
//                        new LoggedInUser(
//                                java.util.UUID.randomUUID().toString(),
//                                username);
//                return new Result.Success<>(fakeUser);
//            } else{
//                Log.e("RegisterDataSource", "register");
//                return new Result.Error(new IOException(responseData));
//            }




        } catch (Exception e) {
            return new Result.Error(new IOException("Error registering", e));
        }
    }


    private void LoginWithSocket(String username, String password){

        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg(String.format("LOGIN:%s %s", username, password));
        String result = serverConnector.ReceiveMsg();
        Log.e(TAG,"msg: " + result);

        if (result == null){
            responseData = "NULL";
        }else {
            responseData = result;
        }
    }


    private void RegisterWithSocket(String username, String password, String email, String nickname, String inviterCode){

        if (inviterCode.equals(""))
            inviterCode = "0";

        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg(String.format("REGISTER:%s %s %s %s %s", username, email, nickname, password, inviterCode));
        String result = serverConnector.ReceiveMsg();
        Log.e(TAG,"msg: " + result);

        if (result == null){
            responseData = "NULL";
        }else {
            responseData = result;
        }

    }



    public String addFriends(String username, String peer){
        try {
            addFriendsWithOkHttp(ipAddFriends, username, peer);

            while (!ifResponsed){
                Log.e("AddFriendsLoop", "AAAAAAAAAAAAAAAAA");
            }

            ifResponsed = false;
            if (responseData.equals("true")) {
                Log.e("LoginDataSource", "addFriends successfully !");
                Log.e("LoginDataSource","username: " + username + " & peer: " + peer);
            } else{
                Log.e("LoginDataSource","Something Wrong when add Friends: " + responseData);
                return responseData;
            }
        }catch (Exception e){
            Log.e("LoginDataSource","Something Wrong when add Friends!");
            return "Something Wrong when add Friends!";
        }
        return "true";
    }


    public String queryFriends(String username){
        try {
            queryFriendsWithOkHttp(ipQueryFriends, username);

            while (!ifResponsed){
                Log.e("QueryLoop", "AAAAAAAAAAAAAAAAA");
            }

            ifResponsed = false;
            if (responseData.equals("true")) {
                Log.e("LoginDataSource", "queryFriends successfully !");
                Log.e("LoginDataSource","username: " + username);
            } else{
                Log.e("LoginDataSource","Something Wrong when query Friends: " + responseData);
                return responseData;
            }
        }catch (Exception e){
            Log.e("LoginDataSource","Something Wrong when add Friends!");
            return "Something Wrong when add Friends!";
        }
        return "true";
    }



    private void loginWithOkHttp(String address, String account, String password){
        HttpUtil.loginWithOkHttp(address, account, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseData = "Connect Failed When Login";
                ifResponsed = true;
                Log.e("loginWithHttp", "onFailure: " + responseData);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                ifResponsed = true;
                Log.e("loginWithHttp", "responseData: " + responseData);
//                runOnUiThread
            }
        });
    }

    private void registerWithOkHttp(String address, String email, String username, String nickname, String password){
        HttpUtil.registerWithOkHttp(address, email, username, nickname, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseData = "Connect Failed When Register";
                ifResponsed = true;
                Log.i("RegisterWithHttp", "responseData: " + responseData);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                ifResponsed = true;
                Log.i("RegisterWithHttp", "responseData: " + responseData);
            }
        });
    }


    private void addFriendsWithOkHttp(String address, String username, String peer){
        HttpUtil.addFriendsWithOkHttp(address, username, peer, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseData = "Connect Failed When Register";
                ifResponsed = true;
                Log.i("addFriendsWithOkHttp", "responseData: " + responseData);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                ifResponsed = true;
                Log.i("addFriendsWithOkHttp", "responseData: " + responseData);
            }
        });
    }

    private void queryFriendsWithOkHttp(String address, String username){
        HttpUtil.queryFriendsWithOkHttp(address, username, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                responseData = "Connect Failed When queryFriendsWithOkHttp";
                ifResponsed = true;
                Log.i("queryFriendsWithOkHttp", "responseData: " + responseData);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                ifResponsed = true;
                Log.i("queryFriendsWithOkHttp", "responseData: " + responseData);
            }
        });
    }

    public void logout() {
        // TODO: revoke authentication
    }
}