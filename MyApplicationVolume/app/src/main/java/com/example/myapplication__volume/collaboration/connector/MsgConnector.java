package com.example.myapplication__volume.collaboration.connector;

import android.content.Context;
import android.util.Log;

import com.example.myapplication__volume.collaboration.MsgSender;
import com.example.myapplication__volume.collaboration.basic.ReconnectionInterface;
import com.example.myapplication__volume.collaboration.service.CollaborationService;

import java.util.ArrayList;
import java.util.List;

public class MsgConnector extends BasicConnector implements ReconnectionInterface {

    //    private static final String TAG = "MsgConnector";

    /**
     * MsgConnector 实例
     */
    public static volatile MsgConnector INSTANCE;

    private static Context mContext;

    private MsgSender msgSender;

    public static List<String> userList = new ArrayList<String>();

    private MsgConnector(){
        msgSender = new MsgSender();
        initBasic();
    }

    public static void init(Context ctx){
        mContext = ctx;
    }

    @Override
    protected void initBasic() {
        TAG = "MsgConnector";
    }


    public static MsgConnector getInstance(){
        if (INSTANCE == null){
            synchronized (MsgConnector.class){
                if (INSTANCE == null){
                    INSTANCE = new MsgConnector();
                }
            }
        }
        return INSTANCE;
    }



    @Override
    public boolean sendMsg(String msg, boolean waited, boolean resend){
        if (checkConnection()){
            return msgSender.SendMsg(mSocket, msg, waited, resend, this);
        }
        return false;
    }

    @Override
    public void reLogin() {
        Log.e(TAG,"empty login in msgConnector !");
    }


    @Override
    public void onReconnection(String msg) {

        /*
        reconnect
         */
        releaseConnection();
        initConnection();
        CollaborationService.resetConnection();
        sendMsg(msg, false, false);
    }


}
