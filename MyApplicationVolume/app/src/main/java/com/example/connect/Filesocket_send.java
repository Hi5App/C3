package com.example.connect;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Filesocket_send {


    public long totalsize;
    public long filenamesize;
    public long m_bytesreceived;
    public String ip;
    public String port;
    private String ANOfilename;
    public Socket filesocket;

    public Boolean IsDown = false;
    public BufferedReader mReader;
    public PrintWriter mPWriter;
    public String path;
//    private static ByteBuffer buffer = ByteBuffer.allocate(8);

    public Filesocket_send(){
        totalsize = 0;
        filenamesize = 0;
        m_bytesreceived = 0;
    }


    public void sendImg(final String filename, final InputStream is, final long length_content, final Context[] context) throws InterruptedException {
        Boolean stop = false;

        BasePopupView popupView = new XPopup.Builder(context[0])
                .asLoading("Uploading......");
        popupView.show();


        Thread thread = new Thread()  {
            public void run(){
                try {

                    if (Looper.myLooper() == null){
                        Looper.prepare();
                    }

                    Log.v("sendImg", "start to send file");
                    DataOutputStream out = new DataOutputStream((FileOutputStream)(filesocket.getOutputStream()));

                    Log.v("readFile", "start to read Datainputstream");


                    long filename_size = filename.getBytes(StandardCharsets.UTF_8).length;
                    long file_size = 16 + filename_size + length_content;

                    byte[] filename_size_byte = longToBytes(filenamesize);
                    byte[] file_size_byte = longToBytes(file_size);

                    out.write(file_size_byte,0,8);
                    out.write(filename_size_byte,0,8);
                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度

                    Log.v("readFile: file_size", Long.toString(bytesToLong(filename_size_byte)));
                    Log.v("readFile: filename_size", Long.toString(bytesToLong(file_size_byte)));

                    out.write(filename.getBytes(StandardCharsets.UTF_8));

//                    //打开文件，如果没有，则新建文件
//                    File file = new File(path + "/" + filename);
//                    if(!file.exists()){
//                        file.createNewFile();
//                        Log.v("readFile", "Create file successfully");
//                    }

                    Log.v("send2", Integer.toString(IOUtils.copy(is, out)));

                    out.flush();
                    out.close();
                    is.close();

                    popupView.dismiss();

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context[0], "Fail to download img", Toast.LENGTH_SHORT).show();
                }
            }
        };
        thread.start();

    }


    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private void disconnect() {
        try {
            mPWriter.close();
            mReader.close();
            filesocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
