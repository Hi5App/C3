package com.example.myapplication__volume.collaboration;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication__volume.collaboration.basic.DataType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MsgReceiver {

    private final static String TAG = "MsgReceiver";

    private Context mContext;

    private DataType dataType = new DataType();

    private InputStream is;

    private String message = "";

    private boolean finished = false;

    private static final String SOCKET_CLOSED = "socket is closed or fail to connect";

    public MsgReceiver(Context context){
        this.mContext = context;
    }

    public String ReceiveMsg(Socket socket){

        if (!socket.isConnected()){
            Toast_in_Thread("Fail to Send_Message, Try Again Please !");
            return SOCKET_CLOSED;
        }

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    resetDataType();

                    Log.e(TAG, "Start to receive msg !");
                    is = socket.getInputStream();

                    finished = false;
                    while (!finished){
                        onRead("ReceiveMsg");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "Fail to get InputStream");
                }

            }
        };

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return message;

    }



    private void onRead(String tag){

        Log.e(TAG,"msg: " + tag);

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
//                            onRead("after read msg !");
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

                Log.e(TAG,"Else process file !");

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
            msg.trim();
            message = msg.replace("\n","");
            finished = true;
            return true;
        }
        else{
            errorprocess(1, msg);
            finished = true;
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
        finished = true;
    }


    private void resetDataType(){
        dataType.dataSize = 0;
        dataType.isFile = false;
    }

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


    public void setContext(Context mContext){
        this.mContext = mContext;
    }


    /**
     * toast info in the thread
     * @param message the message you wanna toast
     */
    public void Toast_in_Thread(String message){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
