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
    public BufferedReader mReader;//BufferedWriter 用于推送消息
    public PrintWriter mPWriter;  //PrinterWriter  用于接收消息
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

                ShowListDialog(context, file_list);
            }else if (information.contains(CurrentDirLoadExp)){
                String [] file_string = information.split(":");
                String [] file_list = file_string[0].split(";");

                for (int i = 0; i < file_list.length; i++)
                    Log.v("onReadyRead", file_list[i]);

                ShowListDialog(context, file_list);
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


    /**
     * show the choice of files
     * @param context
     * @param items
     */
    private void ShowListDialog(final Context context, final String[] items) {

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
                send1(items[which], context);
            }
        });
        listDialog.show();

    }

}
