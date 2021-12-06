package com.penglab.hi5.data;

import static com.penglab.hi5.core.BaseActivity.ip_TencentCloud;
import static com.penglab.hi5.core.BaseActivity.port_TencentCloud;

import android.util.Log;

import com.penglab.hi5.core.collaboration.connector.ServerConnector;
import com.penglab.hi5.data.model.user.RegisterUser;

import java.io.IOException;

public class RegisterDataSource {
    private static final String TAG = "LoginDataSource";
    private String responseData;

    public Result<RegisterUser> register(String email, String username, String nickname, String password, String inviterCode){
        try {
            RegisterWithSocket(username, password, email, nickname, inviterCode);

            if (responseData.equals("REGISTER:0")){
                Log.e(TAG, "register successfully !");
                RegisterUser fakeUser =
                        new RegisterUser(
                                username,
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

    private void initServerConnector(){
        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.releaseConnection(false);
        serverConnector.setIp(ip_TencentCloud);
        serverConnector.setPort(port_TencentCloud);
        serverConnector.initConnection();
    }
}
