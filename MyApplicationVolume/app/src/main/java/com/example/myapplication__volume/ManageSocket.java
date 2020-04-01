package com.example.myapplication__volume;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ManageSocket extends Socket {
    public String ip;
    public String port;
    public String id;
    public Socket mSocket;
    public Socket ImgSocket;
    public BufferedReader mReader;//BufferedWriter 用于推送消息
    public PrintWriter mPWriter;  //PrinterWriter  用于接收消息

    public BufferedReader ImgReader;//BufferedWriter 用于推送消息
    public PrintWriter ImgPWriter;  //PrinterWriter  用于接收消息

    public String path;
//    public PrintWriter mPWriter;



    ManageSocket(){

    }


    public void onReadyRead(String information, Context context){

        String LoginRex = ":log in success.";
        String LogoutRex = ":log out success.";
        String ImportRex = ":import port.";
        String CurrentDirDownExp = ":currentDir_down.";
        String CurrentDirLoadExp = ":currentDir_load.";
        String CurrentDirImgDownExp = ":currentDirImg_down.";
        String MessagePortExp = ":messageport.\n";


        Log.v("onReadyRead", information);

        if(information != null){

            if (information.contains(LoginRex)){

                Toast.makeText(context, "login successfully.", Toast.LENGTH_SHORT).show();
            }else if (information.contains(LogoutRex)){

                disconnectFromHost();
                Toast.makeText(context, "logout successfully.", Toast.LENGTH_SHORT).show();

            }else if (information.contains(ImportRex)){

                if (!mSocket.isConnected()){

                    Toast.makeText(context, "can not connect with Manageserver.", Toast.LENGTH_SHORT).show();
                    return;
                }
                /**
                 *  something
                 */
            }else if (information.contains(CurrentDirDownExp)){
                Log.v("onReadyRead", "CurrentDirDownExp  here we are");
                String [] file_string = information.split(":");
                String [] file_list = file_string[0].split(";");

                for (int i = 0; i < file_list.length; i++)
                    Log.v("onReadyRead", file_list[i]);

                ShowListDialog(context, file_list, "CurrentDirDownExp");

            }else if (information.contains(CurrentDirLoadExp)){
                String [] file_string = information.split(":");
                String [] file_list = file_string[0].split(";");

                for (int i = 0; i < file_list.length; i++)
                    Log.v("onReadyRead", file_list[i]);

                ShowListDialog(context, file_list, "CurrentDirLoadExp");
            }else if (information.contains(CurrentDirImgDownExp)){
                String [] file_string = information.split(":");
                String [] file_list = file_string[0].split(";");

                for (int i = 0; i < file_list.length; i++)
                    Log.v("onReadyRead", file_list[i]);

                ShowListDialog(context, file_list, "CurrentDirImgDownExp");
            }
        }
    }



    private void disconnectFromHost(){

        try {
            mSocket.shutdownInput();
            mSocket.shutdownOutput();
            mSocket.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    private void send1(final String item, final Context context){
        new Thread() {

            public void run() {
                try {

                    Log.v("send1", "here we are");

                    Filesocket_receive filesocket_receive = new Filesocket_receive();
                    filesocket_receive.filesocket = new Socket(ip, 9997);
                    filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
                    filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
                    filesocket_receive.IsDown = true;
                    filesocket_receive.path = context.getExternalFilesDir(null).toString();

//                    Log.v("send1", context.getExternalFilesDir(null).toString());

                    if (mSocket.isConnected()) {
                        if (!mSocket.isOutputShutdown()) {
                            Log.v("send1", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                            mPWriter.println(item + ":choose1." + "\n");
                            mPWriter.flush();

                            Looper.prepare();

                            String content;
//                            if ((content = filesocket_receive.mReader.readLine()) != null) {
//                                Log.v("send1", content);
//
////                                if (!((Activity) context).isFinishing()) {
////                                    onReadyRead(content, context);
////                                    Looper.loop();
////                                }
//                            }
                            filesocket_receive.readFile(item);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }




    private void send2(final String item, final Context context){
        new Thread() {

            public void run() {
                try {

                    Log.v("send1", "here we are");
                    Log.v("send2", ip);

                    Filesocket_receive filesocket_receive = new Filesocket_receive();
                    filesocket_receive.filesocket = new Socket(ip, 9997);
                    filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
                    filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
                    filesocket_receive.path = context.getExternalFilesDir(null).toString();

                    if (ImgSocket.isConnected()) {
                        if (!ImgSocket.isOutputShutdown()) {
                            Log.v("send2", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            Log.v("send2", item);

                            ImgPWriter.println("http" + " " + item + " " + ":choose3.");
                            ImgPWriter.flush();

                        }

//                        Looper.prepare();
                        if (filesocket_receive.filesocket.isConnected())
                            filesocket_receive.readImg(item, context);

//                        //接收来自服务器的消息
//                        while(ImgSocket.isConnected()) {
//
//                            Log.v("Image", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//
////                        if(manageSocket.mReader.ready()) {
//                            if(!ImgSocket.isInputShutdown()) {
//                        /*读取一行字符串，读取的内容来自于客户机
//                        reader.readLine()方法是一个阻塞方法，
//                        从调用这个方法开始，该线程会一直处于阻塞状态，
//                        直到接收到新的消息，代码才会往下走*/
//                                //String data = mReader.readLine();
//                                String content = "";
//
//                                Log.v("InitSocket", "Reply successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//                                while ((content = ImgReader.readLine()) != null) {
//
//                                    Log.v("InitSocket", content);
//                                }
//
//                                Log.v("InitSocket", "Start to receive file ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//
//                            }
//                            Thread.sleep(200);
//                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }



    /**
     * choice the file you want down
     * @param context
     * @param items
     * @param type
     */
    private void ShowListDialog(final Context context, final String[] items, final String type) {

//        final String[] items = { "我是1","我是2","我是3","我是4" };
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("选择要下载的文件");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                // ...To-do
                Toast.makeText(context,"你点击了" + items[which], Toast.LENGTH_SHORT).show();
                if (type.equals("CurrentDirDownExp"))
                    send1(items[which], context);
                if (type.equals("CurrentDirLoadExp"))
                    send1(items[which], context);
                if (type.equals("CurrentDirImgDownExp"))
                    send2(items[which], context);
            }
        });
        listDialog.show();

    }

}
