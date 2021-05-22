package com.penglab.hi5.serverCommunicator;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Socket_Send {

    private Context mContext;

    public Socket_Send(Context context){
        mContext = context;
    }

    public void Send_Message(Socket socket, String message){

        if (!socket.isConnected()){
            Toast_in_Thread("Fail to Send_Message, Try Again Please !");
            return;
        }

        Thread thread = new Thread()  {
            public void run(){
                try {

                    Log.d("Send_Message", "Start to Send Message");
                    OutputStream out = socket.getOutputStream();

                    Log.i("Send_Message:", message);
                    long Message_size = message.getBytes(StandardCharsets.UTF_8).length;
                    long Data_size = 16 + Message_size;

                    byte[] Data_size_Byte = longToBytes(Data_size);
                    byte[] Message_size_Byte = longToBytes(Message_size);

                    String str = new String(message.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);  // for UTF-8 encoding
//                    Log.i("Send_Msg: Data_size", Long.toString(bytesToLong(Data_size_Byte)));
//                    Log.i("Send_Msg: Message_size", Long.toString(bytesToLong(Message_size_Byte)));
//                    Log.i("Send_Msg: message", str);

                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度
                    out.write(Data_size_Byte,0,8);
                    out.write(Message_size_Byte,0,8);
                    out.write(message.getBytes(StandardCharsets.UTF_8));

                    out.flush();

                    Log.d("Send_Msg", "Send Message Successfully !");

                }catch (Exception e){
                    e.printStackTrace();
                    Toast_in_Thread("Fail to Send_Message");
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

    public void Send_File(Socket socket, String filename, InputStream is, long length_content){

        if (!socket.isConnected()){
            Toast_in_Thread("Socket is not Connected, Try Again Please !");
            return;
        }

        Thread thread = new Thread()  {
            public void run(){
                try {

                    Log.i("Send_File", "Start to Upload");
                    OutputStream out = socket.getOutputStream();


                    Log.i("sendImg Filename:", filename);

                    long FileName_size = filename.getBytes(StandardCharsets.UTF_8).length;
                    long Data_size = 16 + FileName_size + length_content;

                    byte[] FileName_size_byte = longToBytes(FileName_size);
                    byte[] Data_size_byte = longToBytes(Data_size);

                    out.write(Data_size_byte,0,8);
                    out.write(FileName_size_byte,0,8);


                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度
//                    Log.i("Send_File:Data_size", Long.toString(bytesToLong(Data_size_byte)));
//                    Log.i("Send_File:FileName_size", Long.toString(bytesToLong(FileName_size_byte)));

                    String FileName_String = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8); // for UTF-8 encoding
//                    Log.i("Send_File: FileName", FileName_String);

                    out.write(filename.getBytes(StandardCharsets.UTF_8));
                    Log.i("Send_File", Integer.toString(IOUtils.copy(is, out)));

                    out.flush();
                    is.close();

                }catch (Exception e){
                    e.printStackTrace();
                    Toast_in_Thread("Fail to Upload file");
                }
            }
        };
        thread.start();


        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Toast_in_Thread("Upload Successfully !");

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