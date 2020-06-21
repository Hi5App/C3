package com.example.myapplication__volume;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.server_connect.Filesocket_receive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import cn.carbs.android.library.MDDialog;

public class ConnectActivity extends AppCompatActivity {

    private ManageSocket manageSocket;
    private Boolean isStartRecieveMsg;
    protected BufferedWriter mWriter;//BufferedReader 用于接收消息
//    protected BufferedReader mReader;//BufferedWriter 用于推送消息
//    protected PrintWriter mPWriter;  //PrinterWriter  用于接收消息

    private String content = "";
    //    private Socket mSocket;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        CreateDir();

    }


//    public void Connect(View v){
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    acceptServer();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//    }
//
//    private void acceptServer() throws IOException {
//        //1.创建客户端Socket，指定服务器地址和端口
//        Socket socket = new Socket("223.3.33.234", 9999);
//        //2.获取输出流，向服务器端发送信息
//        OutputStream os = socket.getOutputStream();//字节输出流
//        PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
//        //获取客户端的IP地址
//        InetAddress address = InetAddress.getLocalHost();
//        String ip = address.getHostAddress();
//        pw.write("1");
//        pw.flush();
//        socket.shutdownOutput();//关闭输出流
//        socket.close();
//    }

    private void CreateDir(){
        File file = new File(getExternalFilesDir(null).toString());
        Log.v("CreateDir", getExternalFilesDir(null).toString());
        if (!file.exists()) {
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            if(file.mkdirs())
                Log.v("CreateDir", "Create Dir successfully");
            else
                Log.v("CreateDir", "Create Dir defeat");
        }
    }

    public void Connect(View v){
        context = v.getContext();

        new MDDialog.Builder(this)
//              .setContentView(customizedView)
                .setContentView(R.layout.content_dialog)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et0 = (EditText) contentView.findViewById(R.id.edit0);
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
//                        et0.setText("223.3.33.234");
                        et0.setText("192.168.2.108");
                        et1.setText("9999");
                        et2.setText("xf");

//                        et0.tex("input ip of server");
                    }
                })
                .setTitle("Login")
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
                        EditText et0 = (EditText) contentView.findViewById(R.id.edit0);
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);

                        String ip   = et0.getText().toString();
                        String port = et1.getText().toString();
                        String id   = et2.getText().toString();

//                        String ip   = "192.168.2.108";
//                        String port = "9999";
//                        String id   = "xf";

                        if(!ip.isEmpty() && !port.isEmpty() && !id.isEmpty()){

                            //输入的信息全，就可以进行连接操作
                            Login(ip, port, id);
                        }else{

                            Toast.makeText(getApplicationContext(), "Please make sure all the information is right!!!", Toast.LENGTH_SHORT).show();
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
//              .setShowTitle(false)//default is true
//              .setShowButtons(true)//default is true
                .create()
                .show();
    }

    private void Login(String ip, String port, final String id){

        manageSocket = new ManageSocket();
        manageSocket.ip = ip;
        manageSocket.port = port;
        manageSocket.id = id;

        InitSocket(ip, port, id);


//        new Thread() {
//
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//
//                    Log.v("Login", "mSocket == null");
//
//                    if (mSocket.isConnected()) {
//                        if (!mSocket.isOutputShutdown()) {
//                            Log.v("Login", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//                            mPWriter.println(id +":login."+"\n");
//                        }
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();

    }



    public void Logout(View v){
        context = v.getContext();

        new Thread() {

            public void run() {
                try {

                    Log.v("Logout", "mSocket == null");

                    if (manageSocket!=null && manageSocket.mSocket.isConnected()) {
                        if (!manageSocket.mSocket.isOutputShutdown()) {
                            Log.v("Logout", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            manageSocket.mPWriter.println( manageSocket.id+":logout." );
                            manageSocket.mPWriter.flush();

                            Looper.prepare();

                            if ((content = manageSocket.mReader.readLine()) != null) {

                                Log.v("Download", content);

                                if (!((Activity) context).isFinishing()){
                                    manageSocket.onReadyRead(content, context);
                                    Looper.loop();
                                }

                            }

                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "You have been logout", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }




    public void Import(View v){
        context = v.getContext();

        new Thread() {

            public void run() {
                try {

                    Log.v("Import", "mSocket == null");

                    if (manageSocket!=null && manageSocket.mSocket.isConnected()) {
                        if (!manageSocket.mSocket.isOutputShutdown()) {

                            Log.v("Import", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            manageSocket.mPWriter.println( manageSocket.id+":import." );
                            manageSocket.mPWriter.flush();

                            Looper.prepare();

                            if ((content = manageSocket.mReader.readLine()) != null) {

                                Log.v("Download", content);

                                if (!((Activity) context).isFinishing()){
                                    manageSocket.onReadyRead(content, context);
                                    Looper.loop();
                                }

                            }
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "You have been logout", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }




    public void Download(View v){
        context = v.getContext();

        new Thread() {

            public void run() {
                try {

                    if (manageSocket.mSocket == null) {

                        Log.v("Download", "mSocket == null");
//                        return;
                    }


                    if (manageSocket!=null && manageSocket.mSocket.isConnected()) {
                        if (!manageSocket.mSocket.isOutputShutdown()) {

                            Log.v("Download", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//                            manageSocket.mPWriter.println( manageSocket.id+":down."+"\n" );
                            manageSocket.mPWriter.println( manageSocket.id+":load.");
                            manageSocket.mPWriter.flush();

                            Looper.prepare();

                            if ((content = manageSocket.mReader.readLine()) != null) {

                                Log.v("Download", content);

                                if (!((Activity) context).isFinishing()){
                                    manageSocket.onReadyRead(content, context);
                                    Looper.loop();
                                }

                            }

                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "You have been logout", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    public void ImageBlock(View v){
        context = v.getContext();

        new MDDialog.Builder(this)
//              .setContentView(customizedView)
                .setContentView(R.layout.image_information)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et = (EditText) contentView.findViewById(R.id.edit);
                        EditText et0 = (EditText) contentView.findViewById(R.id.edit0);
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);
                        et.setText("192.168.2.108");
                        et0.setText("1pic1");
                        et1.setText("0");
                        et2.setText("0");
                        et3.setText("0");

//                        et0.tex("input ip of server");
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
                        EditText et = (EditText) contentView.findViewById(R.id.edit);
                        EditText et0 = (EditText) contentView.findViewById(R.id.edit0);
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);

                        String ip         = et.getText().toString();
                        String filename   = et0.getText().toString();
                        String offset_x   = et1.getText().toString();
                        String offset_y   = et2.getText().toString();
                        String offset_z   = et3.getText().toString();

//                        String ip   = "192.168.2.108";
//                        String port = "9999";
//                        String id   = "xf";

                        if(!ip.isEmpty() && !filename.isEmpty() && !offset_x.isEmpty() && !offset_y.isEmpty() && !offset_z.isEmpty()){

                            //输入的信息全，就可以进行连接操作
                            Image(ip, filename, offset_x, offset_y, offset_z, context);
                        }else{

                            Toast.makeText(getApplicationContext(), "Please make sure all the information is right!!!", Toast.LENGTH_SHORT).show();
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

    private void Image(final String ip, final String filename, final String offset_x, final String offset_y, final String offset_z, final Context context){

        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {


                try {
                    manageSocket.ImgSocket = new Socket(ip, Integer.parseInt("9999"));
                    manageSocket.ImgReader = new BufferedReader(new InputStreamReader(manageSocket.ImgSocket.getInputStream(), "UTF-8"));
//                    mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "utf-8"));
                    manageSocket.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(manageSocket.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));

//                    Toast.makeText(getApplicationContext(), "InitSocket", Toast.LENGTH_SHORT).show();
                    Log.v("Image", "ImgSocket successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                    Filesocket_receive filesocket_receive = new Filesocket_receive();
                    filesocket_receive.filesocket = new Socket(ip, 9997);
                    filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
                    filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
                    filesocket_receive.path = getExternalFilesDir(null).toString();


                    if(manageSocket.ImgSocket.isConnected()){

                        manageSocket.ImgPWriter.println( "http:// " + filename + " " + offset_x + " " + offset_y + " " +offset_z + ":imgblock.");
                        manageSocket.ImgPWriter.flush();

                        Log.v("Image", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }

                    Context[] contexts = new Context[1];
                    contexts[0] = context;
                    filesocket_receive.readImg(filename + ".v3draw", contexts);


                    //接收来自服务器的消息
                    while(manageSocket.ImgSocket.isConnected()) {

                        Log.v("Image", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

//                        if(manageSocket.mReader.ready()) {
                        if(!manageSocket.ImgSocket.isInputShutdown()) {
                        /*读取一行字符串，读取的内容来自于客户机
                        reader.readLine()方法是一个阻塞方法，
                        从调用这个方法开始，该线程会一直处于阻塞状态，
                        直到接收到新的消息，代码才会往下走*/
                            //String data = mReader.readLine();
                            String txt = "";

                            Log.v("InitSocket", "Reply successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            while ((content = manageSocket.ImgReader.readLine()) != null) {

                                Log.v("InitSocket", content);
                            }

                            Log.v("InitSocket", "Start to receive file ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//                            filesocket_receive.readFile(filename + ".v3draw");

                        }
                        Thread.sleep(200);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(context, "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        };
        thread.start();


//        new Thread() {
//
//            public void run() {
//                try {
//
//                    if (manageSocket.mSocket == null) {
//
//                        Log.v("Download", "mSocket == null");
////                        return;
//                    }
//
//
//                    if (manageSocket!=null && manageSocket.mSocket.isConnected()) {
//                        if (!manageSocket.mSocket.isOutputShutdown()) {
//
////                            Log.v("Image", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
////                            manageSocket.mPWriter.println( manageSocket.id+":down."+"\n" );
//                            manageSocket.mPWriter.println( filename + ";" + offset_x + ";" + offset_y + ";" +offset_z);
//                            manageSocket.mPWriter.flush();
//
//                            Looper.prepare();
//
//                            while ((content = manageSocket.mReader.readLine()) != null) {
//
//                                Log.v("Image", content);
//
//                                if (!((Activity) context).isFinishing()){
//                                    manageSocket.onReadyRead(content, context);
//                                    Looper.loop();
//                                }
//
//                            }
//
//                        }
//                    }else {
//                        Toast.makeText(getApplicationContext(), "You have been logout", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();


    }



//    private void InitSocket(String ip, String port) {
//
//        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    isStartRecieveMsg = true;
//                    manageSocket = new Socket(ip, port);
//                    mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
//                    mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "utf-8"));
//
//                    //接收来自服务器的消息
//                    while(isStartRecieveMsg) {
//
//                        if(mReader.ready()) {
//                        /*读取一行字符串，读取的内容来自于客户机
//                        reader.readLine()方法是一个阻塞方法，
//                        从调用这个方法开始，该线程会一直处于阻塞状态，
//                        直到接收到新的消息，代码才会往下走*/
//                            //String data = mReader.readLine();
//                            String txt = "";
//                            while ((content = mReader.readLine()) != null) {
//                                //txt = content ;
//                                mHandler.sendMessage(mHandler.obtainMessage(0, content));
//                            }
//                            mReader.close();
//                            //handler发送消息，在handleMessage()方法中接收
//                            //mHandler.obtainMessage(0, data).sendToTarget();
//                        }
//                        Thread.sleep(200);
//                    }
//                    mWriter.close();
//
//                    mSocket.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//    }




    /**
     * 初始化socket
     */
    private void InitSocket(final String ip, final String port, final String id) {


        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    isStartRecieveMsg = true;
                    manageSocket.mSocket = new Socket(ip, Integer.parseInt(port));
                    manageSocket.mReader = new BufferedReader(new InputStreamReader(manageSocket.mSocket.getInputStream(), "UTF-8"));
//                    mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "utf-8"));
                    manageSocket.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(manageSocket.mSocket.getOutputStream(), StandardCharsets.UTF_8)));

//                    Toast.makeText(getApplicationContext(), "InitSocket", Toast.LENGTH_SHORT).show();
                    Log.v("InitSocket", "InitSocket successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                    if(manageSocket.mSocket.isConnected()){

                        manageSocket.mPWriter.println(id +":login.");

//                        manageSocket.mPWriter.println( manageSocket.id+":down."+"\n" );
                        manageSocket.mPWriter.flush();
                        Log.v("InitSocket", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }


                    //接收来自服务器的消息
                    while(isStartRecieveMsg && manageSocket.mSocket.isConnected()) {

                        Log.v("InitSocket", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

//                        if(manageSocket.mReader.ready()) {
                        if(!manageSocket.mSocket.isInputShutdown()) {
                        /*读取一行字符串，读取的内容来自于客户机
                        reader.readLine()方法是一个阻塞方法，
                        从调用这个方法开始，该线程会一直处于阻塞状态，
                        直到接收到新的消息，代码才会往下走*/
                            //String data = mReader.readLine();
                            String txt = "";

                            Log.v("InitSocket", "Reply successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            while ((content = manageSocket.mReader.readLine()) != null) {

                                Log.v("InitSocket", content);

                                Looper.prepare();
                                manageSocket.onReadyRead(content, context);
                                Looper.loop();
                            }
//                            mReader.close();
                            //handler发送消息，在handleMessage()方法中接收
                            //mHandler.obtainMessage(0, data).sendToTarget();
                        }
                        Thread.sleep(200);
                    }
//                    mWriter.close();
//
//                    manageSocket.mSocket.close();

                    Log.v("InitSocket", "The end");

                    if(manageSocket.mSocket == null)
                        Log.v("InitSocket", "mSocket == null");

                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(context, "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        };
        thread.start();
    }


}
