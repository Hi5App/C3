package com.example.myapplication__volume.collaboration;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication__volume.MainActivity;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MsgSender {

    private final String TAG = "MsgSender";
    private Context mContext;

    public MsgSender(Context context){
        this.mContext = context;
    }

    public void SendMsg(Socket socket, String message){

        if (!socket.isConnected()){
            Toast_in_Thread("Fail to Send_Message, Try Again Please !");
            return;
        }

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();

                /**
                 * show the progressbar
                 */
                try {


                    Log.d(TAG, "Start to Send Message");
                    OutputStream out = socket.getOutputStream();

                    String data = message + "\n";
                    int datalength = data.getBytes(StandardCharsets.UTF_8).length;

                    /*
                    msg header = type + msglength
                     */
                    String header = String.format("DataTypeWithSize:%d;;%s\n",0, datalength);
                    int headerlength = header.getBytes().length;

                    Log.d(TAG,"header: " + header);
                    Log.d(TAG,"data: " + data);

                    out.write(header.getBytes(StandardCharsets.UTF_8));
                    out.write(data.getBytes(StandardCharsets.UTF_8));
                    out.flush();

                    if (message.startsWith("/Imgblock:")){
                        MainActivity.showProgressBar();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "Fail to get OutputStream");
                }

            }
        };

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




    public void SendFile(Socket socket, String filename, InputStream is, long filelength){

        if (!socket.isConnected()){
            Toast_in_Thread("Socket is not Connected, Try Again Please !");
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
                    Toast_in_Thread("Fail to get OutputStream");
                }
            }
        };
        thread.start();


        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Toast_in_Thread("Send File Successfully !");

    }


    public void setContext(Context mContext){
        this.mContext = mContext;
    }



    /**
     * toast info in the uithread
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
