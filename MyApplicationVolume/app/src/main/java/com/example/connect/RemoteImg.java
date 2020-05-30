package com.example.connect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication__volume.Filesocket_receive;
import com.example.myapplication__volume.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import cn.carbs.android.library.MDDialog;

public class RemoteImg extends Socket {
    public String ip;
    public String id;
    public Socket mSocket;
    public Socket ImgSocket;
    public PrintWriter mPWriter;  //PrinterWriter  用于接收消息
    public BufferedReader ImgReader;//BufferedWriter 用于推送消息
    public PrintWriter ImgPWriter;  //PrinterWriter  用于接收消息

    public boolean isSocketSet;

    public String path;

    public volatile boolean flag;


    public RemoteImg(){

        isSocketSet = false;

    }


    public void onReadyRead(String information, Context context){

        String LoginRex = ":log in success.";
        String LogoutRex = ":log out success.";
        String ImportRex = ":import port.";
        String CurrentDirDownExp = ":currentDir_down.";
        String CurrentDirLoadExp = ":currentDir_load.";
        String CurrentDirImgDownExp = ":currentDirImg.";
        String MessagePortExp = ":messageport.\n";
        String ImportportExp = ":importport.\n";


        Log.v("onReadyRead", information);

        if(information != null){

            if (information.contains(LoginRex)){

                Toast.makeText(context, "login successfully.", Toast.LENGTH_SHORT).show();
            }else if (information.contains(LogoutRex)){

                disconnectFromHost();
                Toast.makeText(context, "logout successfully.", Toast.LENGTH_SHORT).show();

            }else if (information.contains(ImportRex)){

                if (!ImgSocket.isConnected()){

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
            }else if (information.contains(ImportportExp)){

            }
        }
    }


//    public void SendSwc(){
//        try {
//            Log.v("SendSwc: ", "Start to connect filesend_server");
//
//            Filesocket_send filesocket_send = new Filesocket_send();
//            filesocket_send.filesocket = new Socket(ip, 9001);
//            filesocket_send.mReader = new BufferedReader(new InputStreamReader(filesocket_send.filesocket.getInputStream(), "UTF-8"));
//            filesocket_send.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_send.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
//
//
//            Log.v("before filesocket_send:", "Connect with Server successfully");
//
//            if (filesocket_send.filesocket.isConnected()) {
//
//                Context[] contexts = new Context[1];
//                contexts[0] = context;
//
//                Log.v("filesocket_send: ", "Connect with Server successfully");
//
//                filesocket_send.sendImg(filename, is, length, context);
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }


    public void disconnectFromHost(){

        try {

            ImgSocket.shutdownInput();
            ImgSocket.shutdownOutput();
            ImgSocket.close();
            ImgReader.close();
            ImgPWriter.close();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    private void send1(final String item, final Context context){
        new Thread() {

            public void run() {
                try {

                    if (Looper.myLooper() == null){
                        Looper.prepare();
                    }
                    Log.v("send1", "here we are");

                    Filesocket_receive filesocket_receive = new Filesocket_receive();
                    filesocket_receive.filesocket = new Socket(ip, 9002);
                    filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
                    filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
                    filesocket_receive.IsDown = true;
                    filesocket_receive.path = context.getExternalFilesDir(null).toString();

//                    Log.v("send1", context.getExternalFilesDir(null).toString());

                    if (ImgSocket.isConnected()) {
                        if (!ImgSocket.isOutputShutdown()) {
                            Log.v("send1", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                            ImgPWriter.println(item + ":choose1.");
                            ImgPWriter.flush();


                            String content;
//                            if ((content = filesocket_receive.mReader.readLine()) != null) {
//                                Log.v("send1", content);
//
////                                if (!((Activity) context).isFinishing()) {
////                                    onReadyRead(content, context);
////                                    Looper.loop();
////                                }
//                            }
                            filesocket_receive.readFile(item, context);

                            Looper.loop();
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
                    filesocket_receive.filesocket = new Socket(ip, 9002);
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
                        Context[] contexts = new Context[1];
                        contexts[0] = context;
                        if (filesocket_receive.filesocket.isConnected())
                            filesocket_receive.readImg(item, contexts);

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


    public void Selectblock_fast(Context context, boolean source, String direction){

        flag = false;
        Log.v("Selectblock_fast","Here we are");
        if (!source){
            ConnectServerImg(context);
        }

        while (flag == false);

        flag = true;

        if (getFilename(context) == "--11--"){
            Toast.makeText(context,"Select file first!", Toast.LENGTH_SHORT);
            return;
        }

        switch (direction){
            case "Left":
                PullImageBlock_fast(context, "Left");
                break;
            case "Right":
                PullImageBlock_fast(context, "Right");
                break;
            case "Top":
                PullImageBlock_fast(context, "Top");
                break;
            case "Bottom":
                PullImageBlock_fast(context, "Bottom");
                break;
            case "Front":
                PullImageBlock_fast(context, "Front");
                break;
            case "Back":
                PullImageBlock_fast(context, "Back");
                break;
            default:
                Toast.makeText(context,"Something wrong when pull img", Toast.LENGTH_SHORT).show();
        }


    }

    private void PullImageBlock_fast(Context context, String direction){
        String filename = getFilename(context);
        String offset = getoffset(context, filename);

        String offset_x = offset.split("_")[0];
        String offset_y = offset.split("_")[1];
        String offset_z = offset.split("_")[2];
        String size     = offset.split("_")[3];

        int size_i     = Integer.parseInt(size);
        int offset_x_i = Integer.parseInt(offset_x);
        int offset_y_i = Integer.parseInt(offset_y);
        int offset_z_i = Integer.parseInt(offset_z);

        switch (direction){
            case "Left":
                offset_x_i -= size_i/2;
                offset_x = Integer.toString(offset_x_i);
                break;
            case "Right":
                offset_x_i += size_i/2;
                offset_x = Integer.toString(offset_x_i);
                break;
            case "Top":
                offset_y_i -= size_i/2;
                offset_y = Integer.toString(offset_y_i);
                break;
            case "Bottom":
                offset_y_i += size_i/2;
                offset_y = Integer.toString(offset_y_i);
                break;
            case "Front":
                offset_z_i -= size_i/2;
                offset_z = Integer.toString(offset_z_i);
                break;
            case "Back":
                offset_z_i += size_i/2;
                offset_z = Integer.toString(offset_z_i);
                break;
        }


        offset = offset_x + "_" + offset_y + "_" + offset_z + "_" + size;
        setoffset(offset, filename, context);

        System.out.println("---------" + offset + "---------");
        String[] input = JudgeEven(offset_x, offset_y, offset_z, size);

        if (!JudgeBounding(input, context)){
            Toast.makeText(context, "Please make sure all the information is right!!!", Toast.LENGTH_SHORT).show();
        }else {
            PullImageBlcok(input[0], input[1], input[2], input[3], context);
        }

    }

    public void Selectblock(Context context, boolean source){

        if (!source){
//            if (!isSocketSet){
//                ConnectServerImg(context);
//            }
            ConnectServerImg(context);

        }

        if (getFilename(context).equals("--11--")){
            Toast.makeText(context,"Select a remote file first,  please!!!", Toast.LENGTH_SHORT).show();
            Log.v("Selectblock","the file is not selected");
            return;
        }

        PopUp(context);


    }

    private void PopUp(Context context){
        new MDDialog.Builder(context)
//              .setContentView(customizedView)
                .setContentView(R.layout.image_bais_select)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);
                        EditText et4 = (EditText) contentView.findViewById(R.id.edit4);

                        String filename = getFilename(context);
                        String offset = getoffset(context, filename);

                        String offset_x = offset.split("_")[0];
                        String offset_y = offset.split("_")[1];
                        String offset_z = offset.split("_")[2];
                        String size     = offset.split("_")[3];


                        et1.setText(offset_x);
                        et2.setText(offset_y);
                        et3.setText(offset_z);
                        et4.setText(size);

                    }
                })
                .setTitle("Download image")
                .setNegativeButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        //这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view，目的是方便在确定/取消按键中对contentView进行操作，如获取数据等。
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);
                        EditText et4 = (EditText) contentView.findViewById(R.id.edit4);

                        String offset_x   = et1.getText().toString();
                        String offset_y   = et2.getText().toString();
                        String offset_z   = et3.getText().toString();
                        String size       = et4.getText().toString();

                        if( !offset_x.isEmpty() && !offset_y.isEmpty() && !offset_z.isEmpty() && !size.isEmpty()){

                            String offset = offset_x + "_" + offset_y + "_" + offset_z + "_" + size;

                            String filename = getFilename(context);
                            setoffset(offset, filename, context);

                            String[] input = JudgeEven(offset_x, offset_y, offset_z, size);

                            if (!JudgeBounding(input, context)){
                                PopUp(context);
                                Toast.makeText(context, "Please make sure all the information is right!!!", Toast.LENGTH_SHORT).show();
                            }else {
                                PullImageBlcok(input[0], input[1], input[2], input[3], context);
                            }

                        }else{

                            Toast.makeText(context, "Please make sure all the information is right!!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
//                        EditText et = (EditText) contentView.findViewById(R.id.edit1);
//                        Toast.makeText(getApplicationContext(), "edittext 1 : " + et.getText(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setWidthMaxDp(600)
                .create()
                .show();
    }


    private String[] JudgeEven(String offset_x, String offset_y, String offset_z, String size){

        float x_offset_f = Float.parseFloat(offset_x);
        float y_offset_f = Float.parseFloat(offset_y);
        float z_offset_f = Float.parseFloat(offset_z);
        float size_f     = Float.parseFloat(size);

        int x_offset_i = (int) x_offset_f;
        int y_offset_i = (int) y_offset_f;
        int z_offset_i = (int) z_offset_f;
        int size_i = (int) size_f;

        if (x_offset_i % 2 == 1)
            x_offset_i += 1;

        if (y_offset_i % 2 == 1)
            y_offset_i += 1;

        if (z_offset_i % 2 == 1)
            z_offset_i += 1;

        if (size_i % 2 == 1)
            size_i += 1;

        String[] result = new String[4];
        result[0] = Integer.toString(x_offset_i);
        result[1] = Integer.toString(y_offset_i);
        result[2] = Integer.toString(z_offset_i);
        result[3] = Integer.toString(size_i);

        Log.v("JudgeEven", Arrays.toString(result));

        return result;
    }

    private boolean JudgeBounding(String[] input, Context context){

        int x_offset_i = Integer.parseInt(input[0]);
        int y_offset_i = Integer.parseInt(input[1]);
        int z_offset_i = Integer.parseInt(input[2]);
        int size_i = Integer.parseInt(input[3]);

        String filename = getFilename(context);
        String size = filename.split("RES")[1];

        System.out.println("hhh-------" + size + "--------hhh");

        int x_size = Integer.parseInt(size.split("x")[0]);
        int y_size = Integer.parseInt(size.split("x")[1]);
        int z_size = Integer.parseInt(size.split("x")[2]);



        if ( ( x_offset_i - size_i/2 < 0 ) || ( x_offset_i + size_i/2 > x_size -2) )
            return false;

        if ( ( y_offset_i - size_i/2 < 0 ) || ( y_offset_i + size_i/2 > y_size -2) )
            return false;

        if ( ( z_offset_i - size_i/2 < 0 ) || ( z_offset_i + size_i/2 > z_size -2) )
            return false;


        return true;
    }

    private void PullImageBlcok(final String offset_x, final String offset_y, final String offset_z, final String size, final Context context){

        new Thread() {

            public void run() {

                try {

                    if(Looper.myLooper() == null){
                        Looper.prepare();
                    }

//                    Filesocket_receive filesocket_receive = new Filesocket_receive();
//                    filesocket_receive.filesocket = new Socket(ip, 9002);
//                    filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
//                    filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
//                    filesocket_receive.path = context.getExternalFilesDir(null).toString();

                    String filename = getFilename(context);


                    if (ImgSocket.isConnected()) {

                        Filesocket_receive filesocket_receive = new Filesocket_receive();
                        filesocket_receive.filesocket = new Socket(ip, 9002);
                        filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
                        filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
                        filesocket_receive.path = context.getExternalFilesDir(null).toString();


//                        String storefilename = filename.split("RES")[0] +
//                                "_" + offset_x_i + "_" + offset_y_i + "_" + offset_z_i + "_" + size +"_" + size +"_" + size + ".v3draw";


                        Context[] contexts = new Context[1];
                        contexts[0] = context;

                        if (filesocket_receive.filesocket.isConnected()){



                            String content = ImgReader.readLine();

                            if ( content.contains(":file connected.") ){

                                Log.v("PullImageblock", content);

                                if (!ImgSocket.isOutputShutdown()) {
                                    System.out.println("-------------pull img block---------------");

                                    ImgPWriter.println(filename + "__" + offset_x + "__" + offset_y + "__" + offset_z + "__" + size + ":imgblock.");
//                                  ImgPWriter.println(filename + "__" + offset_x_i + "__" + offset_y_i + "__" + offset_z_i + "__" + size + ":imgblock.");
                                    ImgPWriter.flush();

                                    System.out.println("-------------" + filename + "---------------");

                                }

//                                String storefilename = filename.split("RES")[0] +
//                                        "_" + offset_x + "_" + offset_y + "_" + offset_z + "_" + size +"_" + size +"_" + size + ".v3draw";

                                String storefilename = filename.split("RES")[0] +
                                        "_" + offset_x + "_" + offset_y + "_" + offset_z + "_" + size +"_" + size +"_" + size + ".v3dpbd";


                                Log.v("PullImageBlcok", "x: " + offset_x + ", y:" + offset_y + ", z:" +offset_z + ", size: " + size);

                                filesocket_receive.readImg(storefilename, contexts);

                                Log.v("PullImageBlcok", "x: " + offset_x + ", y:" + offset_y + ", z:" +offset_z + ", size: " + size +  " successfully---------");

                            }

                        }

                    }

                    disconnectFromHost();

                    Log.v("PullImageBlcok", "x: " + offset_x + ", y:" + offset_y + ", z:" +offset_z + ", size: " + size +  " successfully---------");


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }.start();
    }



    private void PullImageBlcok_backup(final String offset_x, final String offset_y, final String offset_z, final String size, final Context context){
        new Thread() {

            public void run() {
                try {

                    if(Looper.myLooper() == null){
                        Looper.prepare();
                    }

//                    Filesocket_receive filesocket_receive = new Filesocket_receive();
//                    filesocket_receive.filesocket = new Socket(ip, 9002);
//                    filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
//                    filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
//                    filesocket_receive.path = context.getExternalFilesDir(null).toString();

                    String filename = getFilename(context);

                    if (ImgSocket.isConnected()) {

                        Filesocket_receive filesocket_receive = new Filesocket_receive();
                        filesocket_receive.filesocket = new Socket(ip, 9002);
                        filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
                        filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
                        filesocket_receive.path = context.getExternalFilesDir(null).toString();


//                        String storefilename = filename.split("RES")[0] +
//                                "_" + offset_x_i + "_" + offset_y_i + "_" + offset_z_i + "_" + size +"_" + size +"_" + size + ".v3draw";


                        Context[] contexts = new Context[1];
                        contexts[0] = context;

                        if (filesocket_receive.filesocket.isConnected()){



                            String content = ImgReader.readLine();

                            if ( content.contains(":file connected.") ){

                                Log.v("PullImageblock", content);

                                if (!ImgSocket.isOutputShutdown()) {
                                    System.out.println("-------------pull img block---------------");

                                    ImgPWriter.println(filename + "__" + offset_x + "__" + offset_y + "__" + offset_z + "__" + size + ":imgblock.");
//                                  ImgPWriter.println(filename + "__" + offset_x_i + "__" + offset_y_i + "__" + offset_z_i + "__" + size + ":imgblock.");
                                    ImgPWriter.flush();

                                    System.out.println("-------------" + filename + "---------------");

                                }

                                String storefilename = filename.split("RES")[0] +
                                        "_" + offset_x + "_" + offset_y + "_" + offset_z + "_" + size +"_" + size +"_" + size + ".v3draw";


                                Log.v("PullImageBlcok", "x: " + offset_x + ", y:" + offset_y + ", z:" +offset_z + ", size: " + size);

                                filesocket_receive.readImg(storefilename, contexts);

                                Log.v("PullImageBlcok", "x: " + offset_x + ", y:" + offset_y + ", z:" +offset_z + ", size: " + size +  " successfully---------");

                            }

                        }

                    }

                    disconnectFromHost();

                    Log.v("PullImageBlcok", "x: " + offset_x + ", y:" + offset_y + ", z:" +offset_z + ", size: " + size +  " successfully---------");


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }.start();
    }


    /**
     * choice the file you want down
     * @param context the activity context
     * @param items the list of filename
     * @param type the communication type
     */
    private void ShowListDialog(final Context context, final String[] items, final String type) {

//        final String[] items = { "我是1","我是2","我是3","我是4" };
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("选择要下载的文件");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(context,"你点击了" + items[which], Toast.LENGTH_SHORT).show();
                if (type.equals("CurrentDirDownExp"))
                    send1(items[which], context);
                if (type.equals("CurrentDirLoadExp"))
                    send1(items[which], context);
                if (type.equals("CurrentDirImgDownExp")){
                    setFilename(items[which], context);
                    Selectblock(context, true);
//                    send2(items[which], context);
                }
            }
        });
        listDialog.show();

    }


    public void ConnectServerImg(Context context){
        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        this.ip = getip(context);

        Log.v("ConnectServerImg","Here we are");

        Thread thread = new Thread() {
            @Override
            public void run() {
//                Looper.prepare();
                Log.v("ConnectServerImg", "successfully init");
                try {
                    ImgSocket = new Socket("39.100.35.131", Integer.parseInt("9000"));
                    ImgReader = new BufferedReader(new InputStreamReader(ImgSocket.getInputStream(), "UTF-8"));
                    ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));
                    Log.v("ConnectServerImg", "successfully init");
                    flag = true;

                } catch (Exception e) {
                    Log.v("ConnectServerImg", "connect failed");
                    Log.v("ConnectServerImg", e.getMessage());
                    e.printStackTrace();
                    Log.v("ConnectServerImg", e.getMessage());
//                    Toast.makeText(context, "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                }
                flag = true;
            }
        };
        thread.start();

    }

    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static String getFilename(Context context){
        String filename = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Remote_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get filename", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                filename = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get filename", filename);
        return filename;
    }

    /**
     * put the ip address you input to local file
     * @param filename the ip address currently input
     */
    public void setFilename(String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Remote_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set filename", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(filename.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    private String getip(Context context){
        String ip = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/ip.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "223.3.33.234";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get ip", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                ip = line;

//                //分行读取
//                while ((line = buffreader.readLine()) != null) {
//                    ip = line;
//                }
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get ip", ip);
        return ip;
    }

    /**
     * put the ip address you input to local file
     * @param ip the ip address currently input
     */
    public void setip(String ip, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/ip.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get ip", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(ip.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getoffset(Context context, String filename){
        String offset = null;

        String offset_x = filename.split("x")[3];
        String offset_y = filename.split("x")[4];
        String offset_z = filename.split("x")[5];

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = offset_x + "_" + offset_y + "_" + offset_z + "_128";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                offset = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get ip", offset);
        return offset;
    }

    /**
     * put the offset you input to local file
     * @param offset the offset currently input
     */
    private void setoffset(String offset, String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(offset.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
