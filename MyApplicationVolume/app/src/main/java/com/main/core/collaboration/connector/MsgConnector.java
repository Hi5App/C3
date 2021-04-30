package com.main.core.collaboration.connector;

import android.content.Context;
import android.util.Log;

import com.main.chat.nim.InfoCache;
import com.main.core.collaboration.MsgSender;
import com.main.core.collaboration.basic.ReconnectionInterface;
import com.main.core.collaboration.service.CollaborationService;

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
        if (!checkConnection())
            initConnection();
        return msgSender.SendMsg(mSocket, msg, waited, resend, this);
    }

    @Override
    public void reLogin() {
        Log.e(TAG,"Start to reLogin !");
        MsgConnector.getInstance().sendMsg("/login:" + InfoCache.getAccount());
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
        releaseConnection();
        initConnection();
        CollaborationService.resetConnection();
        sendMsg(msg, false, false);
    }


}
