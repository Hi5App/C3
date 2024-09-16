package com.penglab.hi5.core.collaboration;

import android.util.Log;

import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.collaboration.basic.ReconnectionInterface;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

public class MsgSender {

    private final String TAG = "MsgSender";

    private final ExecutorService executorService;

    public MsgSender(){
        executorService = Executors.newFixedThreadPool(4);
    }

    public boolean sendMsg(Socket socket, String message, boolean waited, boolean resend, ReconnectionInterface reconnectionInterface){

        if ((socket == null || !socket.isConnected()) && !resend){
            ToastEasy("Fail to Send Message, check the network please !");
            return false;
        }
        
        Future<Boolean> result = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    OutputStream out = socket.getOutputStream();

                    String data = message;
                    if(!message.startsWith("/login"))
                        data = message + "\n";

                    int dataLength = data.getBytes(StandardCharsets.UTF_8).length;

                    /*
                    msg header = type + dataLength
                     */
                    String header = String.format("DataTypeWithSize:%d %s\n",0, dataLength);

//                    if (!data.startsWith("HeartBeat")){
//                        Log.d(TAG,"header: " + header.trim() + ",  data: " + data);
////                        Logcat.w("sender", data);
//                    }

                    String finalMsg = header + data;
                    out.write(finalMsg.getBytes(StandardCharsets.UTF_8));
                    out.flush();

                    /*
                     * show the progressbar
                     */
//                    if (message.startsWith("/Imgblock:")){
//                        MainActivity.showDownloadingPopupView();
//                    }

                }catch (Exception e){
                    Log.d(TAG, "Something wrong when send message to server");
                    e.printStackTrace();
                    if (resend){
                        reconnectionInterface.onReconnection(message);
                    }
                    return false;
                }
                return true;
            }
        });

        try {
            if (waited){
                return result.get();
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }



    public boolean testConnection(Socket socket){

        Future<Boolean> result = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    if(socket == null || socket.isClosed()){
                        return false;
                    }
                    OutputStream out = socket.getOutputStream();
                    String data = "HeartBeat\n";
                    int dataLength = data.getBytes(StandardCharsets.UTF_8).length;

                    /* msg header = type + dataLength */
                    String header = String.format("DataTypeWithSize:%d %s\n",0, dataLength);
                    String finalMsg = header + data;
                    out.write(finalMsg.getBytes(StandardCharsets.UTF_8));
                    out.flush();

                }catch (Exception e){
                    Log.d(TAG, "Fail to send msg when test Connection");
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });

        try {
            return result.get();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
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

    public void close(){
        executorService.shutdown();
    }
}
