package com.penglab.hi5.core.collaboration.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.basic.DataType;
import com.penglab.hi5.core.collaboration.basic.ReceiveMsgInterface;
import com.penglab.hi5.core.collaboration.connector.BasicConnector;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BasicService extends Service {

//    private static String TAG = BasicService.class.getSimpleName();
    protected String TAG;

    private ReceiveMsgInterface receiveMsgInterface;

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    // indicates whether onRebind should be used
    boolean mAllowRebind;

    protected BasicConnector mBasicConnector;

    private DataType dataType = new DataType();

    protected long HEART_BEAT_RATE;

    protected Timer timer;

    /*
    init function
     */
    public abstract void init();

    protected abstract void reConnection();

    protected abstract boolean getRelease();

    protected abstract void setReleaseInner(boolean flag);

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {

        public BasicService getService() {
            // Return this instance of BasicService so clients can call public methods
            return BasicService.this;
        }

        public void addReceiveMsgInterface(ReceiveMsgInterface mreceiveMsgInterface){
            receiveMsgInterface = mreceiveMsgInterface;
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer();
        timer.schedule(task, 30 * 1000, HEART_BEAT_RATE);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        receiveMsgInterface = null;
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
        super.onDestroy();
    }





    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (!getRelease())
                sendMsg("HeartBeat");
        }
    };


    public void sendMsg(final String msg){
        Log.e(TAG,"Send Heart Beat Msg");

        Socket mSocket = mBasicConnector.getSocket();
        if (mSocket != null && !mSocket.isClosed() && mSocket.isConnected()){
            if (mBasicConnector.sendMsg(msg, true, true)){
                Log.e(TAG,"Send Heart Beat Msg Successfully !");
                if (TAG.startsWith("CollaborationService")){
                    MsgConnector.getInstance().sendMsg("/GetBBSwc:" + Communicator.BrainNum + ";" + Communicator.getCurRes() + ";" + Communicator.getCurrentPos() + ";");
                }
            }
        }else {
            reConnection();
        }
    }


    /**
     * thread for read and process msg
     */
    class ReadThread extends Thread {
        private Socket mSocket;
        private InputStream is;
        private boolean isReconnect = false;         /* when the something wrong with the connect, and need to reconnect in the service */
        private boolean isReset = false;             /* when the connector release the connect, and need to reset connect in this thread */
        protected boolean needStop = false;          /* depend on whether need to stop run() */

        public ReadThread(Socket socket) {
            mSocket = socket;
        }


        protected void reConnect(){
            Log.e(TAG,"Start to reConnect in mReadThread !");

            isReconnect = true;
            releaseSocket();
            resetDataType();

            try {

                mBasicConnector.initConnection();
                mSocket = mBasicConnector.getSocket();
                is = mBasicConnector.getSocket().getInputStream();
                mBasicConnector.reLogin();
                isReconnect = false;
                isReset = false;

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"reConnect");
            }

            setReleaseInner(false);
        }



        public void resetConnection(){
            isReset = true;

            try {

                mSocket = mBasicConnector.getSocket();
                is = mSocket.getInputStream();
                isReconnect = false;
                isReset = false;

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"Fail to reSetConnection");
            }

            setReleaseInner(false);
        }


        //同步方法读取返回得数据
        @Override
        public void run() {
            super.run();
            if (null != mSocket) {
                try {
                    is = mSocket.getInputStream();
                    int keepalive = 0;
                    while(!needStop) {
                        try {
                            synchronized (this) {
                                if (!getRelease()){
                                    if (!isReset && !isReconnect){
                                        if (!(mSocket==null) && !mSocket.isClosed() && !mSocket.isInputShutdown()) {
                                            onRead("in the while loop");
                                        }else {
                                            reConnection();
                                        }
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e(TAG,"Network Error ！");
                        }
                        keepalive++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        private void onRead(String tag){

//            try {
//                if ((is.available()>0 || dataType.dataSize >0) && !dataType.isFile){
//                    Log.e(TAG, "tag: " + tag + ";  available size : " + is.available());
//                    Log.e(TAG, "dataType.isFile: " + dataType.isFile + ";  dataType.dataSize : " + dataType.dataSize);
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }

            if(!dataType.isFile){
                if (dataType.dataSize == 0){
                    try {
                        String header = "";
                        if (is.available() > 0){
//                            Log.e(TAG,"available size: " + is.available());
                            header = MyReadLine(is);

//                            Log.e(TAG,"read header: " + header);
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

                try {
                    // process file
                    if (is.available() > 0){
                        int ret = 0;

                        File dir = new File(dataType.filepath);
                        if (!dir.exists()){
                            if(dir.mkdirs()){
                                Log.v(TAG, "Create dirs Successfully !");
                            }
                        }

                        //打开文件，如果没有，则新建文件
                        File file = new File(dataType.filepath + "/" + dataType.filename);
                        if(!file.exists()){
                            if (file.createNewFile()){
                                Log.v(TAG, "Create file Successfully !");
                            }
                        }

                        FileOutputStream out = new FileOutputStream(file);
                        int File_Content_Int = (int) dataType.dataSize;
                        int Loop = File_Content_Int / 1024;
                        int End = File_Content_Int % 1024;

//                        Log.e(TAG, "Loop: " + Loop + "; End: " + End);
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

//                        Log.e(TAG, "Start to read end content !");
                        if (End > 0){

//                            Log.e(TAG, "Wait for the data !");
                            for (int i = 0; i < 1; i++){
                                if (is.available() < End){
                                    i--;
                                    continue;
                                }
                                is.read(File_Content_End, 0, End);
                            }
//                            Log.e(TAG, "Finish read the data !");
                            out.write(File_Content_End);
                        }
                        out.close();

                        if (dataType.filename.endsWith(".v3draw") || dataType.filename.endsWith("v3dpbd")){
                            receiveMsgInterface.onRecMessage("Block:" + dataType.filepath + "/" + dataType.filename);
//                            MainActivity.hideProgressBar();
                        }else{
                            receiveMsgInterface.onRecMessage("File:" + dataType.filepath + "/" + dataType.filename);
                        }

//                        Log.e(TAG,"Finish process file !");
                        resetDataType();

                        if(ret!=0){
                            errorprocess(ret, dataType.filename);
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    resetDataType();
                }
            }

        }



        private boolean processHeader(final String rmsg){

            int ret = 0;
            if (rmsg.endsWith("\n")){
                String msg = rmsg.trim();
                if (msg.startsWith("DataTypeWithSize:")){
                    msg = msg.substring("DataTypeWithSize:".length());

                    String[] paras_list = msg.split(" ");
                    ArrayList<String> paras = new ArrayList<>();

                    if (paras_list.length==2 && paras_list[0].equals("0")){
                        dataType.dataSize = Long.parseLong(paras_list[1]);
                    }else if(paras_list.length==3 && paras_list[0].equals("1")){
                        dataType.isFile = true;
                        dataType.filename = paras_list[1];
                        dataType.dataSize = Long.parseLong(paras_list[2]);

                        if (dataType.filename.endsWith(".mp3") || dataType.filename.endsWith(".wmv"))
                            dataType.filepath = getApplicationContext().getExternalFilesDir(null).toString() + "/Resources/Music";
                        else
                            dataType.filepath = getApplicationContext().getExternalFilesDir(null).toString() + "/Img";

                        Log.e(TAG,"This is a file !");
                    }else {
                        ret = 3;
                    }
                }else {
                    ret = 2;
                }
            }else {
                ret = 1;
            }

            if (ret != 0)
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
            reConnection();
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
     * @return the data read from inputstream
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
