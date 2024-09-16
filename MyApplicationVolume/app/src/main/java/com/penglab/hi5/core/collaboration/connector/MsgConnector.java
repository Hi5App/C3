package com.penglab.hi5.core.collaboration.connector;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.collaboration.MsgSender;
import com.penglab.hi5.core.collaboration.basic.ReconnectionInterface;
import com.penglab.hi5.core.collaboration.service.CollaborationService;
import com.penglab.hi5.core.ui.collaboration.CollaborationActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.lockForMsgSocket;

public class MsgConnector extends BasicConnector implements ReconnectionInterface {

    //    private static final String TAG = "MsgConnector";

    /**
     * MsgConnector 实例
     */
    public static volatile MsgConnector INSTANCE;

    private static Context mContext;

    private MsgSender msgSender;

    private Handler checkConnHandler;

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
        Log.e(TAG,"sendMsg"+msg);
        return msgSender.sendMsg(mSocket, msg, waited, resend, this);
    }

    @Override
    public void reLogin() {
        Log.e(TAG,"Start to reLogin !");
        if (InfoCache.getAccount() != null){
            CollaborationActivity.firstLoad = true;
            MsgConnector.getInstance().sendMsg("/login:" + InfoCache.getId() + " " + "2", true, false);
        }
        else
            ToastEasy("user account is null, fail to relogin !");
    }

    @Override
    public void setRelease() {
        CollaborationService.setRelease(true);
    }

    @Override
    public void onReconnection(String msg) {
        /*
        reconnect
         */
        synchronized (lockForMsgSocket){
            if (!msgSender.testConnection(mSocket)){
                releaseConnection();
                initConnection();
                CollaborationService.resetConnection();
                reLogin();
                sendMsg(msg, false, false);
            }
        }
    }

    public void closeSender(){
        msgSender.close();
    }

    public boolean testConnection(){
        return msgSender.testConnection(mSocket);
    }
}
