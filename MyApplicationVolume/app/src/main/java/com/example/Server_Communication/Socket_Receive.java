package com.example.Server_Communication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication__volume.MainActivity;
import com.example.myapplication__volume.MainActivity_Jump;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Socket_Receive {

    private Context mContext;

    private static final int LIMIT_SIZE = 30000000;

    public Socket_Receive(Context context){
        mContext = context;
    }

    public String Get_Message(Socket socket) {

        if (!socket.isConnected()){
            Toast_in_Thread("Fail to Get_Message, Try Again Please !");
            return null;
        }

        final String[] Msg = {""};
        Thread thread = new Thread() {
            public void run() {
                try {

                    Log.v("Get_Message", "start to read file");
                    DataInputStream in = new DataInputStream((FileInputStream) (socket.getInputStream()));

                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度
                    byte[] Data_size = new byte[8];
                    byte[] Message_size = new byte[8];
                    in.read(Data_size, 0, 8);
                    in.read(Message_size, 0, 8);

                    int Data_size_int = (int) bytesToLong(Data_size);
                    int Message_size_int = (int) bytesToLong(Message_size);


                    if (Data_size_int != Message_size_int + 4){
//                        Log.v("readfile IoUtils", Integer.toString(IOUtils.copy(in, outputStream)));
                    }

                    //读取 Msg 内容
                    byte[] Msg_String_byte = new byte[Message_size_int];
                    in.read(Msg_String_byte, 0, Message_size_int);

                    String Msg_String = new String(Msg_String_byte, StandardCharsets.UTF_8);
                    String Msg_SubString = Msg_String.substring(4);


                    Log.v("Get_Msg: Data_size", Long.toString(bytesToLong(Data_size)));
                    Log.v("Get_Msg: Message_size", Long.toString(bytesToLong(Message_size)));
                    Log.v("Get_Msg", Msg_SubString);


                    Msg[0] = Msg_SubString;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.v("Get_Message: ", "Receive Msg Successfully !");
        return Msg[0];

    }


    public String Get_File(Socket socket, String file_path, boolean Need_Waited){

        if (!socket.isConnected()){
            Toast_in_Thread("Fail to Get_File, Try Again Please !");
            return "Error";
        }

        String[] File_Name_Complete = {"Error"};

        Thread thread = new Thread() {
            public void run() {
                try {

                    Log.v("Get_Message", "start to read file");
                    DataInputStream in = new DataInputStream((FileInputStream) (socket.getInputStream()));

                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度
                    byte[] Data_size = new byte[8];
                    byte[] FileName_size = new byte[8];
                    in.read(Data_size, 0, 8);
                    in.read(FileName_size, 0, 8);

                    int Data_size_int = (int) bytesToLong(Data_size);
                    int FileName_size_int = (int) bytesToLong(FileName_size);


                    if (Data_size_int <= FileName_size_int + 16){

                        Toast_in_Thread("Can't get the SWC in BB ,please try again");
                        return;

                    }

                    //读取 Msg 内容
                    byte[] FileName_String_byte = new byte[FileName_size_int];
                    in.read(FileName_String_byte, 0, FileName_size_int);

                    String FileName_String = new String(FileName_String_byte, StandardCharsets.UTF_8);
                    String FileName_SubString = "";
                    if (FileName_String.contains(".swc")){
                        FileName_SubString = FileName_String.substring(4, FileName_String.indexOf(".") + 4);
                    }else {
                        FileName_SubString = FileName_String.substring(4, FileName_String.length() - 4);
                    }


                    Log.v("Get_Msg: Data_size", Long.toString(bytesToLong(Data_size)));
                    Log.v("Get_Msg: FileName_size", Long.toString(bytesToLong(FileName_size)));
                    Log.v("Get_Msg", FileName_String);
                    Log.v("Get_Msg", FileName_SubString);


//                    for (byte a : FileName_String_byte){
//                        int hi = ((a>>4)&0x0F);
//                        int lo = (a&0x0F);
//                        char[] hex = new char[2];
//                        hex[0] = hi>9?(char)(hi - 10 + 'a'):(char)(hi + '0');
//                        hex[1] = lo>9?(char)(lo - 10 + 'a'):(char)(lo + '0');
//                        System.out.print(new String(hex));
//                    }

                    File dir = new File(file_path);
                    if (!dir.exists()){
                        if(dir.mkdirs()){
                            Log.v("Get_File", "Create dirs Successfully !");
                        }
                    }

                    //打开文件，如果没有，则新建文件
                    File file = new File(file_path + "/" + FileName_SubString);
                    if(!file.exists()){
                        if (file.createNewFile()){
                            Log.v("Get_File", "Create file Successfully !");
                        }
                    }

                    FileOutputStream out = new FileOutputStream(file);

                    int File_Content_Int = Data_size_int - 16 - FileName_size_int;
                    int Loop = File_Content_Int / 1024;
                    int End = File_Content_Int % 1024;

                    byte [] File_Content = new byte[1024];
                    byte [] File_Content_End = new byte[End];

                    for(int i = 0; i< Loop; i++){

                        if (in.available() < 1024){
                            i--;
                            continue;
                        }
                        in.read(File_Content, 0, 1024);
                        out.write(File_Content);
                    }

                    if (End > 0){
                        for (int i = 0; i < 1; i++){
                            if (in.available() < End){
                                i--;
                                continue;
                            }
                            in.read(File_Content_End, 0, End);
                        }
                        out.write(File_Content_End);
                    }

                    out.close();

                    File_Name_Complete[0] =  file_path + "/" + FileName_SubString;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        if (Need_Waited){

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        Log.v("Get_File: ", "Receive File Successfully !");

        return File_Name_Complete[0];

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Get_Block(Socket socket, String file_path, boolean Need_Waited){

        if (!socket.isConnected() || socket.isClosed()){
            Toast_in_Thread("Fail to Get_Block, Try Again Please !");
            return;
        }

        MainActivity.showProgressBar();
        boolean[] ifDownloaded = { false };
        String[] FileName = { "" };


        Thread thread = new Thread() {
            public void run() {
                try{

                    Log.v("Get_Block", "Start to Read Block");
                    DataInputStream in = new DataInputStream((FileInputStream)(socket.getInputStream()));

                    boolean[] isFinished = { false };


                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {

                            Log.v("--- Get_Block ---", "Start TimerTask");

                            long startTime=System.currentTimeMillis();

                            long i = 0;
                            while (i < 20000000000L)
                                i++;

                            long stopTime=System.currentTimeMillis();

                            // current time cost 45s
                            Log.v("Get_Block: ", "Time: " + Long.toString(stopTime - startTime) + "ms" ) ;

                            if (!isFinished[0]){
                                try {

                                    MainActivity.Time_Out();
                                    in.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.v("--- Get_Block ---", "Fail to Close DataInputStream!");
                                }
                            }

                        }
                    }, 5 * 1000); // 延时5秒


                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度
                    byte [] Data_size = new byte[8];
                    byte [] FileName_size = new byte[8];

                    in.read(Data_size, 0, 8);
                    in.read(FileName_size, 0, 8);


                    int FileName_size_int = (int) bytesToLong(FileName_size);
                    int Data_size_int = (int) bytesToLong(Data_size);

                    Log.v("Get_Block: Data", Long.toString(bytesToLong(Data_size)));
                    Log.v("Get_Block: FileName", Long.toString(bytesToLong(FileName_size)));

                    if (Data_size_int <= 16 + FileName_size_int){
                        Toast_in_Thread("Fail to Download File, Try Again Later !");
                        return;
                    }

                    if (Data_size_int >= LIMIT_SIZE || FileName_size_int >= LIMIT_SIZE){
                        Toast_in_Thread("Something Wrong when Download File, Try again please !");
                        return;
                    }

                    //读取文件名和内容
                    byte [] FileName_String_byte = new byte[FileName_size_int - 4];
                    in.read(FileName_String_byte, 0, FileName_size_int - 4);
                    String FileName_String = new String(FileName_String_byte, StandardCharsets.UTF_8);
                    String FileName_SubString = FileName_String.substring(4, FileName_String.length());

//                    Log.v("Get_Block: Data", Long.toString(bytesToLong(Data_size)));
//                    Log.v("Get_Block: FileName", Long.toString(bytesToLong(FileName_size)));
//                    Log.v("Get_Block", FileName_String);
//                    Log.v("Get_Block", FileName_SubString);

                    byte[] FileContent_byte = new byte[4];
                    in.read(FileContent_byte, 0, 4);

                    Log.v("Get: FileContent_size", Long.toString(bytesToInt(FileContent_byte)));

                    File dir = new File(file_path);
                    if (!dir.exists()){
                        if(dir.mkdirs()){
                            Log.v("Get_File", "Create dirs Successfully !");
                        }
                    }

                    //打开文件，如果没有，则新建文件
                    File file = new File(file_path + "/" + FileName_SubString);
                    if(!file.exists()){
                        if (file.createNewFile()){
                            Log.v("Get_File", "Create file Successfully !");
                        }
                    }

                    FileOutputStream out = new FileOutputStream(file);

                    int File_Content_Int = Data_size_int - 16 - FileName_size_int;
                    int Loop = File_Content_Int / 1024;
                    int End = File_Content_Int % 1024;

                    byte [] File_Content = new byte[1024];
                    byte [] File_Content_End = new byte[End];

                    for(int i = 0; i< Loop; i++){

                        if (in.available() < 1024){
                            i--;
                            continue;
                        }
                        in.read(File_Content, 0, 1024);
                        out.write(File_Content);
                    }

                    if (End > 0){
                        for (int i = 0; i < 1; i++){
                            if (in.available() < End){
                                i--;
                                continue;
                            }
                            in.read(File_Content_End, 0, End);
                        }
                        out.write(File_Content_End);
                    }

                    out.close();

                    Log.v("File Size", "Size :" + file.length());
                    isFinished[0] = true;
                    ifDownloaded[0] = true;

                    MainActivity.hideProgressBar();

                    FileName[0] = FileName_SubString;

                }catch (Exception e){
                    e.printStackTrace();
                    Toast_in_Thread("Fail to Download Block");
                }

            }
        };
        thread.start();

        if (Need_Waited){

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        if (ifDownloaded[0]){

            MainActivity.LoadBigFile_Remote(file_path + "/" + FileName[0]);

        }

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

    public static long bytesToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getInt();
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
