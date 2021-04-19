package com.example.myapplication__volume.collaboration.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.myapplication__volume.MainActivity;
import com.example.myapplication__volume.collaboration.MsgConnector;
import com.example.myapplication__volume.collaboration.basic.DataType;
import com.example.myapplication__volume.collaboration.basic.ReceiveMsgInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class CollaborationService extends Service {


    private static final String TAG = "CollaborationService";

    private ReceiveMsgInterface receiveMsgInterface;

    // Binder given to clients
    private final IBinder binder = new CollaborationService.LocalBinder();

    private  static CollaborationService.ReadThread mReadThread;

    boolean mAllowRebind; // indicates whether onRebind should be used

    private Socket msgSocket;

    private DataType dataType = new DataType();

    private static final long HEART_BEAT_RATE = 5 * 60 * 1000;

    private Timer timer;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {


        public CollaborationService getService() {
            // Return this instance of CollaborationService so clients can call public methods
            return CollaborationService.this;
        }

        public void addReceiveMsgInterface(ReceiveMsgInterface mreceiveMsgInterface){
            receiveMsgInterface = mreceiveMsgInterface;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgSocket = msgConnector.getMsgSocket();
        mReadThread = new CollaborationService.ReadThread(msgSocket);
        mReadThread.start();

        timer = new Timer();
        timer.schedule(task, 0, HEART_BEAT_RATE);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        receiveMsgInterface = null;
        if (mReadThread != null)
            mReadThread.flag = false;
        timer.cancel();
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d(TAG, "onDestroy");
        super.onDestroy();
//        if (mReadThread != null)
//            mReadThread.flag = false;
//        timer.cancel();
    }

    public static void setFlag(boolean flag){
        mReadThread.flag = flag;
    }



    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Log.e(TAG,"Send Heart Beat Msg");
            sendMsg("HeartBeat:");
        }
    };


    public void sendMsg(final String msg){
        MsgConnector msgConnector = MsgConnector.getInstance();
        Socket mSocket = msgConnector.getMsgSocket();
        if (mSocket != null && !mSocket.isClosed() && mSocket.isConnected()){

            try {
                if (!msgConnector.sendMsg(msg, true, false)){
                    reConnect();
                }
                Log.e(TAG,"Send Heart Beat Msg Successfully !");
            }catch (Exception e){
                Log.e(TAG,"Fail to send Heart Beat Msg !");
                reConnect();
            }

        }else {
            return;
        }
    }




    public static void resetConnection(){
        mReadThread.reSetConnect();
    }


    private void reConnect(){
        Log.e(TAG,"Start to reConnect");
        MsgConnector.getInstance().releaseConnection();
        mReadThread.reConnect();
    }


    /**
     * thread for read and process msg
     */
    class ReadThread extends Thread {
        private Socket mSocket;
        private InputStream is;
        private boolean isReconnect = false;
        private boolean flag = true;

        public ReadThread(Socket socket) {
            mSocket = socket;
        }


        private void reConnect(){

            Log.e(TAG,"Start to reConnect in mReadThread !");
            isReconnect = true;
            releaseSocket();

            try {

                MsgConnector msgConnector = MsgConnector.getInstance();
                msgConnector.initConnection();
                mSocket = msgConnector.getMsgSocket();
                is = msgConnector.getMsgSocket().getInputStream();

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"reConnect");
                isReconnect = false;
            }

            isReconnect = false;

        }


        private void releaseSocket(){
            if (mSocket != null){
                try {
                    if (!mSocket.isClosed()){
                        mSocket.close();
                    }
                    mSocket = null;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }


        public void reSetConnect(){

            try {

                mSocket = MsgConnector.getInstance().getMsgSocket();
                is = mSocket.getInputStream();

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"reConnect");
            }
        }


        //同步方法读取返回得数据
        @Override
        public void run() {
            super.run();
            if (null != mSocket) {
                try {
                    is = mSocket.getInputStream();
                    while(flag) {
                        try {
                            synchronized (this) {

                                if (!(mSocket==null) && !mSocket.isClosed() && !mSocket.isInputShutdown()) {
                                    if (!isReconnect){
                                        onRead("in the while loop");
                                    }
                                }else {
                                    if (!isReconnect){
                                        reConnect();
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            System.out.println("网络异常！");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }


        private void onRead(String tag){

            if(!dataType.isFile){
                if (dataType.dataSize == 0){
                    try {
                        String header = "";
                        if (is.available() > 0){
                            Log.e(TAG,"available size: " + is.available());

                            header = MyReadLine(is);

                            Log.e(TAG,"available size: " + is.available());
                            Log.e(TAG,"read header: " + header);

                            if (processHeader(header + "\n")){
                                onRead("after read header! ");
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d(TAG,"Fail to get Input Stream!");
                    }
                }else {
                    try {
                        if (is.available() >= dataType.dataSize){
                            // read msg
                            Log.e(TAG,"read msg !");
                            String msg = MyReadLine(is) + "\n";
                            if (processMsg(msg)){
                                onRead("after read msg !");
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d(TAG,"Fail to get Input Stream!");
                    }
                }
            }else {

                Log.e(TAG,"Else process file !");

                try {
                    // process file
                    if (is.available() > 0){

                        int ret = 0;

                        Log.e(TAG,"Start to process file !");

                        File dir = new File(dataType.filepath);
                        if (!dir.exists()){
                            if(dir.mkdirs()){
                                Log.e("Get_File", "Create dirs Successfully !");
                            }
                        }

                        //打开文件，如果没有，则新建文件
                        File file = new File(dataType.filepath + "/" + dataType.filename);
//                        File file = new File(dataType.filepath + "/" + "1.v3draw");
                        if(!file.exists()){
                            if (file.createNewFile()){
                                Log.e("Get_File", "Create file Successfully !");
                            }
                        }

                        FileOutputStream out = new FileOutputStream(file);

                        int File_Content_Int = (int) dataType.dataSize;
                        int Loop = File_Content_Int / 1024;
                        int End = File_Content_Int % 1024;

                        Log.e(TAG, "Loop: " + Loop + "; End: " + End);

                        byte [] File_Content = new byte[1024];
                        byte [] File_Content_End = new byte[End];

                        for(int i = 0; i< Loop; i++){

                            if (is.available() < 1024){
                                i--;
                                continue;
                            }
                            is.read(File_Content, 0, 1024);
                            out.write(File_Content);
                        }


                        Log.e(TAG, "Start to read end content !");

                        if (End > 0){

                            Log.e(TAG, "Wait for the data !");

                            for (int i = 0; i < 1; i++){
                                if (is.available() < End){
                                    i--;
                                    continue;
                                }
                                is.read(File_Content_End, 0, End);
                            }

                            Log.e(TAG, "Finish read the data !");

                            out.write(File_Content_End);
                        }
                        out.close();

                        Log.e(TAG, "Start to read end content !");
                        if (dataType.filename.endsWith(".v3draw") || dataType.filename.endsWith("v3dpbd")){
                            receiveMsgInterface.onRecMessage("Block:" + dataType.filepath + "/" + dataType.filename);
                            MainActivity.hideProgressBar();
                        }else{
                            receiveMsgInterface.onRecMessage("File:" + dataType.filepath + "/" + dataType.filename);
                        }

                        Log.e(TAG,"Finish process file !");
                        resetDataType();

                        if(ret!=0){
                            errorprocess(ret, dataType.filename);
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }



        private boolean processHeader(final String rmsg){

            int ret = 0;
            if (rmsg.endsWith("\n")){
                String msg = rmsg.trim();
                if (msg.startsWith("DataTypeWithSize:")){
                    Log.e(TAG,"msg: " + msg);
                    msg = msg.substring("DataTypeWithSize:".length());
                    Log.e(TAG,"msg: " + msg);

                    String[] paras_list = msg.split(" ");
                    ArrayList<String> paras = new ArrayList<>();

                    Log.e(TAG,"paras_list: " + Arrays.toString(paras_list));

                    if (paras_list.length==2 && paras_list[0].equals("0")){
                        dataType.dataSize = Long.parseLong(paras_list[1]);
                    }else if(paras_list.length==3 && paras_list[0].equals("1")){
                        dataType.isFile = true;
                        dataType.filename = paras_list[1];
                        dataType.dataSize = Long.parseLong(paras_list[2]);
                        dataType.filepath = getApplicationContext().getExternalFilesDir(null).toString() + "/Draw/Sync/BlockGet";

                        Log.e(TAG,"This is a file !");
                        /*
                        data file path
                         */

                    }else {
                        ret = 3;
                    }
                }else {
                    ret = 2;
                }
            }else {
                ret = 1;
            }

            Log.e(TAG, "ret: " + ret);
            if (ret==0) return true;
            errorprocess(ret, rmsg.trim());  return false;

        }



        private boolean processMsg(final String msg){
            if (msg.endsWith("\n")){
                receiveMsgInterface.onRecMessage(msg.trim());
                resetDataType();
                return true;
            }
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
                    Log.d(TAG, "ERROR: msg not end with '\\n', ");
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

            MainActivity.hideProgressBar();
            reConnect();

        }


        /*
        reset datatype after process the msg
        */
        private void resetDataType(){

            dataType.isFile=false;
            dataType.dataSize=0;
            dataType.filename = null;
            dataType.filepath = null;

        }
    }


    /**
     * my function for Readline from inputstream
     * @param is inputstream
     * @return
     */
    private String MyReadLine(InputStream is){

        String s = "";
        try {

            String c;
            int num;
            do {
                byte[] byte_c = new byte[1];
                num = is.read(byte_c);
                c = new String(byte_c, StandardCharsets.UTF_8);
                if (c.equals("\n"))
                    break;
                s += c + "";
            } while (num > 0);

        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }


}
