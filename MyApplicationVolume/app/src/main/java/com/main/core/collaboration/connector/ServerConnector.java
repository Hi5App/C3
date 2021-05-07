package com.main.core.collaboration.connector;

import android.content.Context;
import android.util.Log;

import com.main.chat.nim.InfoCache;
import com.main.core.collaboration.MsgReceiver;
import com.main.core.collaboration.MsgSender;
import com.main.core.collaboration.basic.ReconnectionInterface;
import com.main.core.collaboration.service.ManageService;

public class ServerConnector extends BasicConnector implements ReconnectionInterface {

    private static final String EMPTY_MSG = "the msg is empty";

    /**
     * ServerConnector 实例
     */
    public static volatile ServerConnector INSTANCE;

    private static Context mContext;

    public static MsgSender msgSender;

    public static MsgReceiver msgReceiver;

    private String roomName = "";


    private ServerConnector(){
        msgSender = new MsgSender();
        msgReceiver = new MsgReceiver();
    }

    @Override
    protected void initBasic() {
        TAG = "ServerConnector";
    }

    public static void init(Context ctx){
        mContext = ctx;
    }

    public static ServerConnector getInstance(){
        if (INSTANCE == null){
            synchronized (ServerConnector.class){
                if (INSTANCE == null){
                    INSTANCE = new ServerConnector();
                }
            }
        }
        return INSTANCE;
    }


    private void makeConnect(){
        Log.e(TAG,"makeConnect()");

        try{
            if (mSocket == null || !checkConnection()){
                Log.e(TAG,"Connect Again");
                initConnection();
                reLogin();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public boolean sendMsg(String msg, boolean waited, boolean resend){
        if (!checkConnection())
            initConnection();
        return msgSender.SendMsg(mSocket, msg, waited, resend, this);
    }


    public String ReceiveMsg(){

        makeConnect();
        String msg = msgReceiver.ReceiveMsg(mSocket);

        if (msg != null)
            return msg;
        else
            return EMPTY_MSG;
    }


    @Override
    public void reLogin(){
        Log.e(TAG,"Start to reLogin !");
        sendMsg(String.format("LOGIN:%s %s", InfoCache.getAccount(), InfoCache.getToken()), true, false);
    }

    @Override
    public void setRelease() {
        ManageService.setRelease(true);
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


    @Override
    public void onReconnection(String msg) {

        /*
        reconnect
         */

        releaseConnection();
        initConnection();
        ManageService.resetConnection();
        reLogin();
        sendMsg(msg,false, false);
    }

}
