package com.penglab.hi5.core.ui.login.data;

import android.util.Log;

import com.penglab.hi5.core.collaboration.connector.ServerConnector;
import com.penglab.hi5.core.ui.login.data.model.LoggedInUser;

import java.io.IOException;

import static com.penglab.hi5.core.BaseActivity.ip_TencentCloud;
import static com.penglab.hi5.core.BaseActivity.port_TencentCloud;

/**s
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class DataSource {

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

                LoggedInUser curUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                username);
                return new Result.Success<>(curUser);

            }else if (responseData.startsWith("LOGIN:-1")){
                Log.e(TAG, "Result.Error");
                return new Result.Error(new IOException("Something wrong with database !"));
            }else if (responseData.startsWith("LOGIN:-2")){
                Log.e(TAG, "Result.Error");
                return new Result.Error(new IOException("Can not find user !"));
            }else if (responseData.startsWith("LOGIN:-3")){
                Log.e(TAG, "Result.Error");
                return new Result.Error(new IOException("username or password is wrong !"));
            }else if (responseData.startsWith("LOGIN:-4")){
                Log.e(TAG, "Result.Error");
                return new Result.Error(new IOException("account already login in other device !"));
            }else if(responseData.equals("NULL")){
                return new Result.Error(new IOException("Fail to Connect the server !"));
            }else if (responseData.equals("TimeOut")){
                return new Result.Error(new IOException("Time out, check the network connection please !"));
            }else {
                Log.e(TAG, "Result.Error");
                return new Result.Error(new IOException("Something else Wrong !"));
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Check the network please !", e));
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
                return new Result.Error(new IOException("User Already Exist !"));
            }else if (responseData.equals("REGISTER:-3")){
                Log.e(TAG, "fail to register !");
                return new Result.Error(new IOException("User Already Exist !"));
            }else if(responseData.equals("NULL")){
                return new Result.Error(new IOException("Fail to Connect the server !"));
            }else if (responseData.equals("TimeOut")){
                return new Result.Error(new IOException("Time out, check the network connection please !"));
            }else {
                Log.e(TAG, "fail to register !");
                return new Result.Error(new IOException("Something Wrong with Database !"));
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Check the network please !", e));
        }
    }


    private void LoginWithSocket(String username, String password){

        initServerConnector();
        ServerConnector serverConnector = ServerConnector.getInstance();
        if (serverConnector.checkConnection()){
            serverConnector.sendMsg(String.format("LOGIN:%s %s", username, password), true, false);
            String result = serverConnector.ReceiveMsg();
            Log.e(TAG,"msg: " + result);

            if (result == null){
                responseData = "TimeOut";
            }else if (result.equals("socket is closed or fail to connect")){
                responseData = "TimeOut";
            }else {
                responseData = result;
            }
        }
    }


    private void RegisterWithSocket(String username, String password, String email, String nickname, String inviterCode){

        if (inviterCode.equals(""))
            inviterCode = "0";

        initServerConnector();
        ServerConnector serverConnector = ServerConnector.getInstance();
        if(serverConnector.checkConnection()){
            serverConnector.sendMsg(String.format("REGISTER:%s %s %s %s %s", username, email, nickname, password, inviterCode), true, false);
            String result = serverConnector.ReceiveMsg();
            Log.e(TAG,"msg: " + result);

            if (result == null){
                responseData = "TimeOut";
            }else if (result.equals("socket is closed or fail to connect")){
                responseData = "TimeOut";
            }else {
                responseData = result;
            }
        }else {
            responseData = "NULL";
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }


    private void initServerConnector(){
        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.releaseConnection(false);
        serverConnector.setIp(ip_TencentCloud);
        serverConnector.setPort(port_TencentCloud);
        serverConnector.initConnection();
    }

}