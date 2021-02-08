package com.example.myapplication__volume.collaboration;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ServerConnector {

    private static final String TAG = "ServerConnector";

    private Socket manageSocket = null;

    /**
     * ServerConnector 实例
     */
    public static ServerConnector INSTANCE;

    private static Context mContext;

    private String ip;

    private String port;

    private DataType dataType;

    private MsgSender msgSender;


    private ServerConnector(){
        msgSender = new MsgSender(mContext);
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

        Log.i(TAG,"Start to Connect Server !");

        /*
        如果已经和服务器建立连接了，就return
         */
//        if (manageSocket != null && !manageSocket.isClosed() && manageSocket.isConnected()){
//            return;
//        }

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


    private void onRead(){
        if(!dataType.isFile){
            if (dataType.dataSize == 0){
                try {
                    if (manageSocket.getInputStream().available() >= 1024){
                        // read header
                        BufferedReader ImgReader = new BufferedReader(new InputStreamReader(manageSocket.getInputStream(), "UTF-8"));
                        String header = ImgReader.readLine();
                        if (processHeader(header)){
                            onRead();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG,"Fail to get Input Stream!");
                }
            }else {
                try {
                    if (manageSocket.getInputStream().available() >= dataType.dataSize){
                        // read msg
                        BufferedReader ImgReader = new BufferedReader(new InputStreamReader(manageSocket.getInputStream(), "UTF-8"));
                        String msg = ImgReader.readLine();
                        if (processMsg(msg)){
                            onRead();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG,"Fail to get Input Stream!");
                }
            }
        }else {
            // process file

        }
    }


    private boolean processHeader(final String rmsg){

        int ret = 0;
        if (rmsg.endsWith("\n")){
            String msg = rmsg.trim();
            if (msg.startsWith("DataTypeWithSize:")){
                msg = msg.substring(msg.length() - "DataTypeWithSize:".length());

                String[] paras_list = msg.split(";;");
                ArrayList<String> paras = new ArrayList<>();
                for (int i = 0; i < paras_list.length; i++){
                    if (!paras_list[i].equals(""))
                        paras.add(paras_list[i]);
                }

                if (paras.size()==2 && paras.get(0)=="0"){
                    dataType.dataSize = Long.parseLong(paras.get(1));
                }else if(paras.size()==3 && paras.get(0)=="1"){
                    dataType.isFile = true;
                    dataType.filename = paras.get(1);
                    dataType.dataSize = Long.parseLong(paras.get(2));


                }else {
                    ret = 3;
                }
            }else {
                ret = 2;
            }
        }else {
            ret = 1;
        }

        if (ret==0) return true;
        errorprocess(ret,rmsg.trim());  return false;

    }



    private boolean processMsg(final String msg){
        if (msg.endsWith("\n"))
            return true;
        else{
            errorprocess(1, msg);
            return false;
        }
    }


    private void errorprocess(int errcode, String msg){

        //error code
        //1:not end with '\n';
        //2:not start wth "DataTypeWithSize"
        //3:msg not 2/3 paras
        //4:cannot open file
        //5:read socket != write file
        //6:next read size < 0

        switch (errcode){
            case 1:
                Log.d(TAG, "ERROR: msg not end with '\n', ");
                break;
            case 2:
                Log.d(TAG, String.format("ERROR:%s not start wth \"DataTypeWithSize\"", msg));
                break;
            case 3:
                Log.d(TAG, String.format("ERROR:%s not 2/3 paras", msg));
                break;
            case 4:
                Log.d(TAG, String.format("ERROR:%s cannot open file", msg));
                break;
            case 5:
                Log.d(TAG, String.format("ERROR:%s read socket != write file", msg));
                break;
            case 6:
                Log.d(TAG, String.format("ERROR:%s next read size < 0", msg));
                break;
        }

        Toast_in_Thread("Something Error, the socket will be disconnected !");

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
        }

    }

    private boolean checkConnection(){

        return manageSocket!= null && manageSocket.isConnected() && !manageSocket.isClosed();

//        try{
//            manageSocket.sendUrgentData(0xFF);
//        }catch(Exception e){
//            return false;
//        }

    }


    /**
     * get the socket
     * @return socket connected with server
     */
    public Socket getManageSocket() {
        return manageSocket;
    }



    public void sendMsg(String msg){

        makeConnect();

        if (checkConnection()){
            msgSender.SendMsg(manageSocket, msg);
        }
    }


    public static void setContext(Context mContext) {
        ServerConnector.mContext = mContext;
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




    /*
    class for data type
     */

    class DataType{
        public boolean isFile = false;   //  false for msg;  true for file
        public long    dataSize = 0;
        public String  filename;
//        public Qfile point;
    }




}
