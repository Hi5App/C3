package com.example.myapplication__volume.collaboration;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication__volume.collaboration.basic.ReconnectionInterface;
import com.example.myapplication__volume.collaboration.service.CollaborationService;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MsgConnector implements ReconnectionInterface {


    private static final String TAG = "MsgConnector";

    private Socket msgSocket = null;

    /**
     * ServerConnector 实例
     */
    public static MsgConnector INSTANCE;

    private static Context mContext;

    private String ip;

    private String port;

    private MsgSender msgSender;

    public static List<String> userList = new ArrayList<String>();


    private MsgConnector(){
        msgSender = new MsgSender(mContext);
    }

    public static void init(Context ctx){
        mContext = ctx;
    }

    public static MsgConnector getInstance(){
        if (INSTANCE == null){
            synchronized (ServerConnector.class){
                if (INSTANCE == null){
                    INSTANCE = new MsgConnector();
                }
            }
        }
        return INSTANCE;
    }



    /**
     * 初始化连接
     */
    public void initConnection(){

        Log.i(TAG,"Start to Connect Server !");

        /*
        如果已经和服务器建立连接了，就return
         */
        if (msgSocket != null && !msgSocket.isClosed() && msgSocket.isConnected()){
            return;
        }

        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {

                try {

                    Log.d(TAG,String.format("ip: %s,  port: %s", ip, port));
                    msgSocket = new Socket(ip, Integer.parseInt(port));                // 服务器的ip和端口号

                    /*
                    判断是否成功建立连接
                     */
                    if (msgSocket.isConnected()) {
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

        if (msgSocket != null){
            try {
                if (!msgSocket.isClosed()) {
                    msgSocket.close();
                }
                msgSocket = null;
            }catch (Exception e){
                System.out.println("Something wrong with releaseConnection !");
            }
        }
    }





    private void makeConnect(){

        Log.e(TAG,"makeConnect()");

        if (msgSocket == null || !checkConnection()){
            Log.e(TAG,"Connect Again");
            initConnection();
        }

    }

    private boolean checkConnection(){
        return msgSocket != null && msgSocket.isConnected() && !msgSocket.isClosed();
    }


    /**
     * get the socket
     * @return socket connected with server
     */
    public Socket getMsgSocket() {
        return msgSocket;
    }


    /**
     * send msg without waiting
     */
    public boolean sendMsg(String msg){
        return sendMsg(msg, false, true);
    }


    public boolean sendMsg(String msg, boolean waited, boolean resend){
        if (checkConnection()){
            return msgSender.SendMsg(msgSocket, msg, waited, resend, this);
        }
        return false;
    }


    public static void setContext(Context mContext) {
        MsgConnector.mContext = mContext;
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
        releaseConnection();
        initConnection();
        CollaborationService.resetConnection();
        sendMsg(msg, false, true);
    }


}
