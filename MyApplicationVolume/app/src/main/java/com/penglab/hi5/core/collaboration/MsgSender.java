package com.penglab.hi5.core.collaboration;

import android.util.Log;

import com.lazy.library.logging.Logcat;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.collaboration.basic.ReconnectionInterface;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

public class MsgSender {

    private final String TAG = "MsgSender";

    public MsgSender(){
    }

    public boolean sendMsg(Socket socket, String message, boolean waited, boolean resend, ReconnectionInterface reconnectionInterface){

        final boolean[] flag = {true};
        if ((socket == null || !socket.isConnected()) && !resend){
            ToastEasy("Fail to Send Message, check the network please !");
            return false;
        }

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();

                try {
//                    Log.d(TAG, "Start to Send Message");
                    OutputStream out = socket.getOutputStream();

                    String data = message + "\n";
                    int datalength = data.getBytes(StandardCharsets.UTF_8).length;

                    /*
                    msg header = type + msglength
                     */
                    String header = String.format("DataTypeWithSize:%d;;%s\n",0, datalength);
                    int headerlength = header.getBytes().length;

                    if (!data.startsWith("HeartBeat")){
                        Log.d(TAG,"header: " + header.trim() + ",  data: " + data);
//                        Logcat.w("sender", data);
                    }

                    String finalMsg = header + data;
                    out.write(finalMsg.getBytes(StandardCharsets.UTF_8));
                    out.flush();

                    /*
                     * show the progressbar
                     */
                    if (message.startsWith("/Imgblock:")){
                        MainActivity.showProgressBar();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "Fail to get OutputStream");
                    flag[0] = false;

                    if (resend){
                        reconnectionInterface.onReconnection(message);
                    }
                }

            }
        };

        thread.start();

        try {
            if (waited)
                thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return flag[0];
    }



    public boolean testConnection(Socket socket){
        final boolean[] flag = {true};

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();

                try {

                    OutputStream out = socket.getOutputStream();
                    String data = "HeartBeat\n";
                    int datalength = data.getBytes(StandardCharsets.UTF_8).length;

                    /* msg header = type + msglength */
                    String header = String.format("DataTypeWithSize:%d;;%s\n",0, datalength);
                    int headerlength = header.getBytes().length;

                    String finalMsg = header + data;
                    out.write(finalMsg.getBytes(StandardCharsets.UTF_8));
                    out.flush();

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "Fail to send msg when test Connection");
                    flag[0] = false;
                }

            }
        };

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return flag[0];
    }




    public void sendFile(Socket socket, String filename, InputStream is, long filelength){

        if (!socket.isConnected()){
            ToastEasy("Socket is not Connected, Try Again Please !");
            return;
        }

        Thread thread = new Thread()  {
            public void run(){
                try {

                    Log.d(TAG, "Start to Send File");
                    OutputStream out = socket.getOutputStream();

                    /*
                    file header = type + filename + filelength
                     */
                    String header = String.format("DataTypeWithSize:%d %s %d\n",1, filename, filelength);

                    out.write(header.getBytes());
                    Log.d(TAG, "File length: " + Integer.toString(IOUtils.copy(is, out)));

                    out.flush();
                    is.close();

                }catch (Exception e){
                    e.printStackTrace();
                    ToastEasy("Fail to get OutputStream");
                }
            }
        };
        thread.start();


        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ToastEasy("Send File Successfully !");

    }



    /**
     * Transform bytes[] to long
     * @param bytes the byte[]
     * @return the long of byte[]
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }


    /**
     * Transform long to byte[]
     * @param x the long
     * @return the byte[]
     */
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(x);
        return buffer.array();
    }
}
