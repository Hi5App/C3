package com.penglab.hi5.core.collaboration.connector;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

public abstract class BasicConnector {

    public  String TAG;

    protected Socket mSocket = null;

    private String ip;

    private String port;

    protected abstract void initBasic();

    public abstract boolean sendMsg(String msg, boolean waited, boolean resend);

    public abstract void reLogin();

    public abstract void setRelease();


    /**
     * 初始化连接
     */
    public void initConnection(){

        Log.d(TAG,"Start to Connect Server !");

        /*
        如果已经和服务器建立连接了，就return
         */
        if (mSocket != null && !mSocket.isClosed() && mSocket.isConnected()){
            return;
        }

        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {

                try {

                    Log.d(TAG,String.format("ip: %s,  port: %s", ip, port));
                    mSocket = new Socket(ip, Integer.parseInt(port));                // 服务器的ip和端口号

                    /*
                    判断是否成功建立连接
                     */
                    if (mSocket.isConnected()) {
                        Log.d(TAG, "Connect Server Successfully !");
                    } else {
                        ToastEasy("Can't Connect Server, Try Again Please!");
                    }


                } catch (IOException e) {
                    ToastEasy("Something Wrong When Connect Server");
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
        Log.e(TAG,"release connect !");
        setRelease();
        if (mSocket != null){
            try {
                if (!mSocket.isClosed()) {
                    mSocket.close();
                }
                mSocket = null;
            }catch (Exception e){
                System.out.println("Something wrong with releaseConnection !");
            }
        }
    }


    public boolean checkConnection(){
        return mSocket != null && mSocket.isConnected() && !mSocket.isClosed();
    }




    /**
     * send msg without waiting
     */
    public boolean sendMsg(String msg){
        return sendMsg(msg, false, true);
    }




    /**
     * get the socket
     * @return socket connected with server
     */
    public Socket getSocket() {
        return mSocket;
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

}
