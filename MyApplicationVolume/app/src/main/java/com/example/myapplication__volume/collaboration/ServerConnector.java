package com.example.myapplication__volume.collaboration;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication__volume.Nim.InfoCache;
import com.example.myapplication__volume.collaboration.basic.DataType;
import com.example.myapplication__volume.collaboration.basic.ReconnectionInterface;

import java.io.IOException;
import java.net.Socket;

public class ServerConnector implements ReconnectionInterface {

    private static final String TAG = "ServerConnector";

    private static final String EMPTY_MSG = "the msg is empty";


    private Socket manageSocket = null;

    /**
     * ServerConnector 实例
     */
    public static ServerConnector INSTANCE;

    private static Context mContext;

    private String ip;

    private String port;

    private DataType dataType;

    public static MsgSender msgSender;

    public static MsgReceiver msgReceiver;

    private String roomName = "";

    private int userScore = -1;


    private ServerConnector(){
        msgSender = new MsgSender(mContext);
        msgReceiver = new MsgReceiver(mContext);
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



    /**
     * 初始化连接
     */
    public void initConnection(){

        Log.d(TAG,"Start to Connect Server !");

        /*
        如果已经和服务器建立连接了，就return
         */
        if (manageSocket != null && !manageSocket.isClosed() && manageSocket.isConnected()){
            return;
        }

        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {

                try {

                    Log.d(TAG,String.format("ip: %s,  port: %s", ip, port));
                    manageSocket = new Socket(ip, Integer.parseInt(port));                // 服务器的ip和端口号

                    /*
                    判断是否成功建立连接
                     */
                    if (manageSocket.isConnected()) {
                        Log.d(TAG, "Connect Server Successfully !");
                    } else {
                        Toast_in_Thread("Can't Connect Server, Try Again Please!");
                    }


                } catch (IOException e) {
                    Toast_in_Thread("Something Wrong When Connect Server");
                    e.printStackTrace();
                }

            }
        };
        thread.start();


        /*
        用于暂停主线程，等待子线程执行完成
         */
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




    public void releaseConnection(){

        if (manageSocket != null){
            try {
                if (!manageSocket.isClosed()) {
                    manageSocket.close();
                }
                manageSocket = null;
            }catch (Exception e){
                System.out.println("NULL!!!");
            }
        }
    }



    private void makeConnect(){

        Log.e(TAG,"makeConnect()");

        if (manageSocket == null || !checkConnection()){
            Log.e(TAG,"Connect Again");
            initConnection();
            reLogin();
        }

    }

    public boolean checkConnection(){

        return manageSocket!= null && manageSocket.isConnected() && !manageSocket.isClosed();

    }



    public void reLogin(){
        Log.e(TAG,"Start to reLogin !");
        sendMsg(String.format("LOGIN:%s %s", InfoCache.getAccount(), InfoCache.getToken()), true);
    }



    /**
     * get the socket
     * @return socket connected with server
     */
    public Socket getManageSocket() {
        return manageSocket;
    }



    public boolean sendMsg(String msg){

        return sendMsg(msg, false);

    }


    public boolean sendMsg(String msg, boolean waited){
        makeConnect();
        if (checkConnection()){
            return msgSender.SendMsg(manageSocket, msg, waited, this);
        }
        return false;
    }


    public String ReceiveMsg(){

        makeConnect();

        String msg = msgReceiver.ReceiveMsg(manageSocket);

        if (msg != null)
            return msg;
        else
            return EMPTY_MSG;
    }



    public static void setContext(Context mContext) {
        ServerConnector.mContext = mContext;
        msgReceiver.setContext(mContext);
        msgSender.setContext(mContext);
    }


    /**
     * 获取ip地址
     * @return ip地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * 修改ip地址
     * @param ip ip地址
     */
    public void setIp(String ip){
        this.ip = ip;
    }


    /**
     * 获取port端口号
     * @return port端口号
     */
    public String getPort() {
        return port;
    }

    /**
     * 修改port端口号
     * @param port 端口号
     */
    public void setPort(String port) {
        this.port = port;
    }


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    /**
     * toast info in the thread
     * @param message the message you wanna toast
     */
    public void Toast_in_Thread(String message){

//        Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onReconnection(String msg) {

        /*
        reconnect
         */
        initConnection();
        ManageService.resetConnection();
        sendMsg(msg);
    }

}
