package com.example.connect;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.basic.NeuronTree;
import com.example.myapplication__volume.MyRenderer;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
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
        Log.v("Init Filesocket_send:", "Init Filesocket_send successfully");

    }


    public void sendImg(final String filename, final InputStream is, final long length_content, final Context context) throws InterruptedException {
        final Boolean[] stop = {false};

//        BasePopupView popupView = new XPopup.Builder(context)
//                .asLoading("Uploading......");
//        popupView.show();

//        Toast.makeText(context, "Start ot upload file!!!", Toast.LENGTH_SHORT).show();


        Thread thread = new Thread()  {
            public void run(){
                try {

//                    if (Looper.myLooper() == null){
//                        Looper.prepare();
//                    }

                    Log.v("sendImg", "start to send file");
                    OutputStream out = filesocket.getOutputStream();
//                    DataOutputStream out = new DataOutputStream((FileOutputStream)(filesocket.getOutputStream()));

                    Log.v("readFile", "start to read Datainputstream");

                    Log.v("sendImg Filename:", filename);
                    long filename_size = filename.getBytes(StandardCharsets.UTF_8).length;
                    long file_size = 16 + filename_size + length_content;

                    byte[] filename_size_byte = longToBytes(filename_size);
                    byte[] file_size_byte = longToBytes(file_size);

                    out.write(file_size_byte,0,8);
                    out.write(filename_size_byte,0,8);
                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度

                    Log.v("readFile: file_size", Long.toString(bytesToLong(file_size_byte)));
                    Log.v("readFile: filename_size", Long.toString(bytesToLong(filename_size_byte)));

                    String str = new String(filename.getBytes(StandardCharsets.UTF_8), "UTF-8"); // for UTF-8 encoding
                    Log.v("readFile: filename", str);

                    out.write(filename.getBytes(StandardCharsets.UTF_8));

//                    //打开文件，如果没有，则新建文件
//                    File file = new File("/storage/emulated/0/C3" + "/" + filename);
//                    if(!file.exists()){
//                        file.createNewFile();
//                        Log.v("readFile", "Create file successfully");
//                    }
//                    FileOutputStream outputStream = new FileOutputStream(file);

//                    byte buffer[] = new byte[4 * 1024];
//                    int temp = 0;
//                    // 循环读取文件
//                    while ((temp = is.read(buffer)) != -1) {
//                        // 把数据写入到OuputStream对象中
//                        outputStream.write(buffer, 0, temp);
//                    }

                    Log.v("send2", Integer.toString(IOUtils.copy(is, out)));

                    out.flush();
                    is.close();

                    stop[0] = true;


                    Log.v("send2", "send file successfully!!!");

//                    popupView.dismiss();


                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Fail to Upload file", Toast.LENGTH_SHORT).show();
                }
            }
        };
        thread.start();

//        Log.v("send2", "finished");

        while (!stop[0]){
        }

        Log.v("send2", "finished");
        Toast.makeText(context, "Upload file successfully!!!", Toast.LENGTH_SHORT).show();



    }


    public boolean sendImg_test(final String filename, final InputStream is, final long length_content, final Context context) throws InterruptedException {
        final Boolean[] stop = {false};

//        BasePopupView popupView = new XPopup.Builder(context)
//                .asLoading("Uploading......");
//        popupView.show();

//        Toast.makeText(context, "Start ot upload file!!!", Toast.LENGTH_SHORT).show();


        Thread thread = new Thread()  {
            public void run(){
                try {

                    if (Looper.myLooper() == null){
                        Looper.prepare();
                    }

                    Log.v("sendImg", "start to send file");
                    OutputStream out = filesocket.getOutputStream();
//                    DataOutputStream out = new DataOutputStream((FileOutputStream)(filesocket.getOutputStream()));

                    Log.v("readFile", "start to read Datainputstream");

                    Log.v("sendImg Filename:", filename);
                    long filename_size = filename.getBytes(StandardCharsets.UTF_8).length;
                    long file_size = 16 + filename_size + length_content;

                    byte[] filename_size_byte = longToBytes(filename_size);
                    byte[] file_size_byte = longToBytes(file_size);

                    out.write(file_size_byte,0,8);
                    out.write(filename_size_byte,0,8);
                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度

                    Log.v("readFile: file_size", Long.toString(bytesToLong(file_size_byte)));
                    Log.v("readFile: filename_size", Long.toString(bytesToLong(filename_size_byte)));

                    String str = new String(filename.getBytes(StandardCharsets.UTF_8), "UTF-8"); // for UTF-8 encoding
                    Log.v("readFile: filename", str);

                    out.write(filename.getBytes(StandardCharsets.UTF_8));

//                    //打开文件，如果没有，则新建文件
//                    File file = new File("/storage/emulated/0/C3" + "/" + filename);
//                    if(!file.exists()){
//                        file.createNewFile();
//                        Log.v("readFile", "Create file successfully");
//                    }
//                    FileOutputStream outputStream = new FileOutputStream(file);

//                    byte buffer[] = new byte[4 * 1024];
//                    int temp = 0;
//                    // 循环读取文件
//                    while ((temp = is.read(buffer)) != -1) {
//                        // 把数据写入到OuputStream对象中
//                        outputStream.write(buffer, 0, temp);
//                    }

                    Log.v("send2", Integer.toString(IOUtils.copy(is, out)));

                    out.flush();
                    is.close();

                    stop[0] = true;


                    Log.v("send2", "send file successfully!!!");

//                    popupView.dismiss();


                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Fail to Upload file", Toast.LENGTH_SHORT).show();
                }

            }
        };
        thread.start();

//        Log.v("send2", "finished");

//        while (!stop[0]){
//        }

        Log.v("send2", "finished");
        Toast.makeText(context, "Upload file successfully!!!", Toast.LENGTH_SHORT).show();

        return true;

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
