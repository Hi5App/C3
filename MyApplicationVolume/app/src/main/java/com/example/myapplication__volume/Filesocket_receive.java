package com.example.myapplication__volume;

import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


public class Filesocket_receive {
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

    Filesocket_receive(){
        totalsize = 0;
        filenamesize = 0;
        m_bytesreceived = 0;
    }

    public void readFile(final String filename){
        new Thread()  {
            public void run(){
                try {

                    Log.v("readFile", "start to read file");
                    DataInputStream in = new DataInputStream((FileInputStream)(filesocket.getInputStream()));

                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度
                    byte [] file_size = new byte[8];
                    byte [] filename_size = new byte[8];
                    in.read(file_size, 0, 8);
                    in.read(filename_size, 0, 8);


                    int filename__size = (int) bytesToLong(filename_size) + 4;
                    int filecontent__size = (int) bytesToLong(file_size) - filename__size;

                    //读取文件名和内容
                    byte [] filename_qstring = new byte[filename__size];
//                    byte [] file_content = new byte[filecontent__size];

                    in.read(filename_qstring, 0, filename__size);
//                    in.read(file_content, 0, filecontent__size);

                    String filename_string = new String(filename_qstring, StandardCharsets.UTF_8);
//                    String filecontent_string = new String(file_content, StandardCharsets.UTF_8);
                    Log.v("readFile: file_size", Long.toString(bytesToLong(file_size)));
                    Log.v("readFile: filename_size", Long.toString(bytesToLong(filename_size)));
                    Log.v("readFile", filename_string);
//                    Log.v("readFile", filecontent_string);


                    int loop = filecontent__size / 1024;
                    int end  = filecontent__size % 1024;


                    //打开文件，如果没有，则新建文件
                    File file = new File(path + "/" + filename);
                    if(!file.exists()){
                        file.createNewFile();
                        Log.v("readFile", "Create file successfully");
                    }

                    FileOutputStream outputStream = new FileOutputStream(file);


//                    IOUtils.copy(in, outputStream);

                    byte [] file_content = new byte[1024];
                    byte [] file_content_end = new byte[end];

                    for(int i = 0; i< loop; i++){
                        in.read(file_content, 0, 1024);
                        outputStream.write(file_content);
                    }


                    in.read(file_content_end, 0, end);
                    outputStream.write(file_content_end);

                    outputStream.close();








                    //对文件进行写入操作
//                    FileOutputStream outputStream = new FileOutputStream(file);
//                    outputStream.write(file_content);
//                    outputStream.close();





                    //发送消息，已获得文件
                    Log.v("readFile","filename:" + filename);
                    mPWriter.println("received " + filename + "\n");
                    mPWriter.flush();


                    Log.v("readFile", filename.substring(filename.lastIndexOf(".")));
                    String file_type = filename.substring(filename.lastIndexOf("."));


                    if ( file_type.equals(".ano")){
                        readFile(filename + ".eswc");
                    }


                    if (file_type.equals(".eswc")){
                        readFile(filename.substring(0, filename.length() - 5) + ".apo");
                    }

                    if (file_type.equals(".apo")){
                        disconnect();
                    }





//                    if (filesocket.isConnected()){
//                        DataInputStream in_2 = new DataInputStream((FileInputStream)(filesocket.getInputStream()));
//
////                        String content;
////                        if ((content = mReader.readLine()) != null)
////                                Log.v("send1", content);
//
//                        byte [] new_file_size = new byte[8];
//                        byte [] new_filename_size = new byte[8];
//                        byte [] new_file_name = new byte[140+4];
//                        byte [] new_file_content = new byte[104-4];
//                        byte [] new_file_content2 = new byte[1024];
//                        byte [] new_file_content3 = new byte[1024];
//
//                        in_2.read(new_file_size, 0, 8);
//                        in_2.read(new_filename_size, 0, 8);
//                        in_2.read(new_file_name, 0, 140+4);
//                        in_2.read(new_file_content,0,104-4);
//
//                        String new_file_string = new String(new_file_name, StandardCharsets.UTF_8);
//                        String new_file_content_string = new String(new_file_content, StandardCharsets.UTF_8);
//
//                        Log.v("readFile","Connect successfully");
//                        Log.v("readFile", Long.toString(bytesToLong(new_file_size)));
//                        Log.v("readFile", Long.toString(bytesToLong(new_filename_size)));
//                        Log.v("readFile", new_file_string);
//                        Log.v("readFile", new_file_content_string);
//                        File file1 = new File(path + "/" + filename + ".eswc");
//
//                        if(!file1.exists()){
//                            file1.createNewFile();
//                            Log.v("readFile", "Create file successfully");
//                        }
//
//
//                        FileOutputStream outputStream1 = new FileOutputStream(file1);
//                        outputStream1.write(new_file_content);
//
//                        outputStream1.close();
//
//                    }





                    /**
                     * read apo file
                     */

//                    Log.v("readFile","filename:" + filename);
//                    mPWriter.println("received " + filename + ".eswc" + "\n");
//                    mPWriter.flush();
//                    Log.v("readFile","here we are");
//
//                    if (filesocket.isConnected()){
////                        DataInputStream in_2 = new DataInputStream((FileInputStream)(filesocket.getInputStream()));
//
////                        String content;
////                        if ((content = mReader.readLine()) != null)
////                                Log.v("send1", content);
//
//                        byte [] new_file_size = new byte[8];
//                        byte [] new_filename_size = new byte[8];
//
//                        in.read(new_file_size, 0, 8);
//                        in.read(new_filename_size, 0, 8);
//
//                        int total_size = (int) bytesToLong(new_file_size);
//                        int filename_size_apo = (int) bytesToLong(new_filename_size);
//
//                        byte [] new_file_name = new byte[filename_size_apo + 4];
//                        byte [] new_file_content = new byte[total_size - filename_size_apo - 4];
//                        in.read(new_file_name, 0, filename_size_apo + 4);
//                        in.read(new_file_content,0,total_size - filename_size_apo - 4);
//
//                        String new_file_string = new String(new_file_name, StandardCharsets.UTF_8);
//                        String new_file_content_string = new String(new_file_content, StandardCharsets.UTF_8);
//
//                        Log.v("readFile","Connect successfully");
//                        Log.v("readFile", Long.toString(bytesToLong(new_file_size)));
//                        Log.v("readFile", Long.toString(bytesToLong(new_filename_size)));
//                        Log.v("readFile", new_file_string);
//                        Log.v("readFile", new_file_content_string);
//                        File file1 = new File(path + "/" + filename + ".apo");
//
//                        if(!file1.exists()){
//                            file1.createNewFile();
//                            Log.v("readFile", "Create file successfully");
//                        }
//
//
//                        FileOutputStream outputStream1 = new FileOutputStream(file1);
//                        outputStream1.write(new_file_content);
//
//                        outputStream1.close();
//
//                    }




//            if (m_bytesreceived == 0){
////                if (){
////
////                }
////                String path = getExternalFilesDir(null).toString()
//
//            }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }




    public void readImg(final String filename){
        new Thread()  {
            public void run(){
                try {

                    Log.v("readFile", "start to read file");
                    DataInputStream in = new DataInputStream((FileInputStream)(filesocket.getInputStream()));

                    //前两个 uint64 记录传输内容的总长度 和 文件名的长度
                    byte [] file_size = new byte[8];
                    byte [] filename_size = new byte[8];
                    in.read(file_size, 0, 8);
                    in.read(filename_size, 0, 8);


                    int filename__size = (int) bytesToLong(filename_size) + 4;
                    int filecontent__size = (int) bytesToLong(file_size) - filename__size;

                    //读取文件名和内容
                    byte [] filename_qstring = new byte[filename__size];
//                    byte [] file_content = new byte[filecontent__size];

                    in.read(filename_qstring, 0, filename__size);
//                    in.read(file_content, 0, filecontent__size);

                    String filename_string = new String(filename_qstring, StandardCharsets.UTF_8);
//                    String filecontent_string = new String(file_content, StandardCharsets.UTF_8);
                    Log.v("readFile: file_size", Long.toString(bytesToLong(file_size)));
                    Log.v("readFile: filename_size", Long.toString(bytesToLong(filename_size)));
                    Log.v("readFile", filename_string);
//                    Log.v("readFile", filecontent_string);


                    int loop = filecontent__size / 1024;
                    int end  = filecontent__size % 1024;


                    //打开文件，如果没有，则新建文件
                    File file = new File(path + "/" + filename);
                    if(!file.exists()){
                        file.createNewFile();
                        Log.v("readFile", "Create file successfully");
                    }

                    FileOutputStream outputStream = new FileOutputStream(file);


                    IOUtils.copy(in, outputStream);

//                    byte [] file_content = new byte[1024];
//                    byte [] file_content_end = new byte[end];
//
//                    for(int i = 0; i< loop; i++){
//                        in.read(file_content, 0, 1024);
//                        outputStream.write(file_content);
//                    }
//
//
//                    in.read(file_content_end, 0, end);
//                    outputStream.write(file_content_end);
//
//                    outputStream.close();



                    //对文件进行写入操作
//                    FileOutputStream outputStream = new FileOutputStream(file);
//                    outputStream.write(file_content);
//                    outputStream.close();






                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
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
