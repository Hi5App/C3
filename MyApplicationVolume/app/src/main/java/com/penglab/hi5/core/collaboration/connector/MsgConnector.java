package com.penglab.hi5.core.collaboration.connector;

import android.content.Context;
import android.util.Log;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.collaboration.MsgSender;
import com.penglab.hi5.core.collaboration.basic.ReconnectionInterface;
import com.penglab.hi5.core.collaboration.service.CollaborationService;

import java.util.ArrayList;
import java.util.List;

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
        return msgSender.sendMsg(mSocket, msg, waited, resend, this);
    }

    @Override
    public void reLogin() {
        Log.e(TAG,"Start to reLogin !");
        if (InfoCache.getAccount() != null){
            MainActivity.firstLoad = true;
            MsgConnector.getInstance().sendMsg("/login:" + InfoCache.getAccount(), true, false);
        }
//        else
//            ToastEasy("user account is null, fail to relogin !");
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


}
