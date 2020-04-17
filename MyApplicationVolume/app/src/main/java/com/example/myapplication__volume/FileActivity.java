package com.example.myapplication__volume;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import cn.carbs.android.library.MDDialog;


//打开文件管理器读取文件
public class FileActivity extends AppCompatActivity {

    //message 字符串用于传递文件的路径到Mainactivity中
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private static Context context;

    private InputStream is;
    private int length;
    private CompleteReceiver completeReceiver;
    private ManageSocket manageSocket;
    private BroadcastReceiver broadcastReceiver;


    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MyRenderer.OUTOFMEM_MESSAGE);
        if (message != null){
            Toast toast = Toast.makeText(FileActivity.this, message, Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        setContentView(R.layout.activity_file);
        tv = (TextView)findViewById(R.id.textView3);

        String http = gethttp();
        EditText editText = (EditText) findViewById(R.id.editText);
        if (!http.equals("")){
            editText.setText(http);
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }


//        sethttp();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }


    // Open the local file
    public void ReadFile(View view) {
        Log.v("MainActivity","Log.v输入日志信息");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }


    /**
     * set the http address to EditText
     */
    private void sethttp(){

        String http = gethttp();
        EditText editText = (EditText) findViewById(R.id.editText);
        if (!http.equals("")){
            editText.setText(http);
        }
    }


    //Download file from the server
    public void DownloadFile(View v){

        EditText editText = (EditText) findViewById(R.id.editText);
        String http = editText.getText().toString();

        String downloadpath = "";

        Log.v("DownloadFile", http+"   LLLLLLLLLLLLL");

//        //创建下载任务,downloadUrl就是下载链接
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
//        //指定下载路径和下载文件名
//        request.setDestinationInExternalPublicDir("/download/", fileName);

//        String downloadpath = "https://penglab.com/temp/test1.raw";
//        downloadpath = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";

        if (http.startsWith("https://")){
            downloadpath = http;
        }else {
            downloadpath = "https://" + http;
        }

        // Path where you want to download file.
        Uri uri = Uri.parse(downloadpath);

        try {
            DownloadManager.Request request = new DownloadManager.Request(uri);

            // Tell on which network you want to download file.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

            // This will show notification on top when downloading the file.
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // Title for notification.
            request.setTitle(uri.getLastPathSegment());
            request.setDescription("Download from C3");

            //设置文件类型
//            request.setMimeType("application/cn.trinea.download.file");

            //创建目录
//        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir() ;

            //设置文件存放路径
//        request.setDestinationInExternalPublicDir(  Environment.DIRECTORY_DOWNLOADS  , "weixin.apk" ) ;
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());

            request.setDestinationInExternalFilesDir( this , Environment.DIRECTORY_DOWNLOADS ,  uri.getLastPathSegment() );


            //获取下载管理器
            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            //将下载任务加入下载队列，否则不会进行下载
            long ID = downloadManager.enqueue(request);
            Toast.makeText(this, "Start to download the file", Toast.LENGTH_SHORT).show();

            listener(ID);

        }catch (Exception e){
            Toast.makeText(this, "make sure the address is legal", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * inform when the file is downloaded
     * @param Id the id of download request
     */
    private void listener(final long Id) {

        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    Toast.makeText(getApplicationContext(), "文件下载完成!", Toast.LENGTH_LONG).show();
                    Log.v("MainActivity", "DownloadFile  successfully");

                }
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);
    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
//    }




    public void Connect(View view) {
        Log.v("MainActivity","Log.v输入日志信息");

        Intent intent = new Intent(this, ConnectActivity.class);
        startActivity(intent);

    }


    // Open the server file
    public void ImageBlock(View v){
        context = v.getContext();
        manageSocket = new ManageSocket();

        new MDDialog.Builder(this)
//              .setContentView(customizedView)
                .setContentView(R.layout.image_select)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et0 = (EditText) contentView.findViewById(R.id.edit0);
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);
//                        et0.setText("192.168.2.108");
                        et0.setText(getip());
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
                        EditText et0 = (EditText) contentView.findViewById(R.id.edit0);
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);

                        String ip         = et0.getText().toString();
                        String offset_x   = et1.getText().toString();
                        String offset_y   = et2.getText().toString();
                        String offset_z   = et3.getText().toString();

                        if (ip != getip()){
                            setip(ip);
                        }


                        if(!ip.isEmpty() && !offset_x.isEmpty() && !offset_y.isEmpty() && !offset_z.isEmpty()){

                            //输入的信息全，就可以进行连接操作
                            Image(ip, offset_x, offset_y, offset_z, context);
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



    private void Image(final String ip, final String offset_x, final String offset_y, final String offset_z, final Context context){

        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    manageSocket.ip = ip;
                    manageSocket.ImgSocket = new Socket(ip, Integer.parseInt("9999"));
                    manageSocket.ImgReader = new BufferedReader(new InputStreamReader(manageSocket.ImgSocket.getInputStream(), "UTF-8"));
//                    mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "utf-8"));
                    manageSocket.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(manageSocket.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));

//                    Toast.makeText(getApplicationContext(), "InitSocket", Toast.LENGTH_SHORT).show();
                    Log.v("Image", "ImgSocket successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//
//                    Filesocket_receive filesocket_receive = new Filesocket_receive();
//                    filesocket_receive.filesocket = new Socket(ip, 9997);
//                    filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
//                    filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
//                    filesocket_receive.path = getExternalFilesDir(null).toString();


                    if(manageSocket.ImgSocket.isConnected()){

                        manageSocket.ImgPWriter.println( "1.v3draw" + "_" + offset_x + "_" + offset_y + "_" + offset_z + "_" + "64" + "_" + "imgblock.");
//                        manageSocket.ImgPWriter.println( "http://" + " " + "1pic1.v3draw" + " " + ":choose3.");
                        manageSocket.ImgPWriter.flush();

                        Log.v("Image", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }

//                    filesocket_receive.readImg("1pic1.v3draw", context);


                    Looper.prepare();

                    //接收来自服务器的消息
                    while(manageSocket.ImgSocket.isConnected()) {

                        Log.v("Image", "Connect successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

//                        if(manageSocket.mReader.ready()) {
                        if(!manageSocket.ImgSocket.isInputShutdown()) {
                        /*读取一行字符串，读取的内容来自于客户机
                        reader.readLine()方法是一个阻塞方法，
                        从调用这个方法开始，该线程会一直处于阻塞状态，
                        直到接收到新的消息，代码才会往下走*/
                            String content = "";

                            Log.v("Image", "Reply successfully~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                            while ((content = manageSocket.ImgReader.readLine()) != null) {

                                Log.v("Image", content);
                                if (!((Activity) context).isFinishing()){
                                    manageSocket.onReadyRead(content, context);
                                    Looper.loop();
                                }
                            }

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

    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        String message = "Hello World";

        Log.v("FileActivity", "dfdsfsdfsdfsdfvxcxv");

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String filePath = uri.toString();
            String filePath_getPath = uri.getPath();
//            String filePath = Uri2PathUtil.getRealPathFromUri(getApplicationContext(), uri);
//            String filePath = FilePath.substring(14);
//            String filePath = "/storage/emulated/0/Download/image.v3draw";


            Log.v("FileActivity", filePath);
            Log.v("filePath_getPath", filePath_getPath);
            Log.v("Uri_Scheme:", uri.getScheme());
            tv.setText(filePath);
            Toast.makeText(this, "Open" + filePath + "--successfully", Toast.LENGTH_SHORT).show();

            try {

                Log.v("MainActivity", "123456");
//                Log.v("MainActivity",String.valueOf(fileSize));

                Intent intent = new Intent(this, MainActivity.class);
                message = filePath;
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);

                File file = new File(filePath);

                Uri uri_1 = Uri.parse((String) filePath);

                Log.v("Uri_1: ", uri_1.toString());

                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(uri_1, "r");

                is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);

                length = (int) parcelFileDescriptor.getStatSize();

                Log.v("Legth: ", Integer.toString(length));


//                File f = new File(filePath);
//                FileInputStream fid = new FileInputStream(f);

//                fid.write(message.getBytes());
//                long fileSize = f.length();
            } catch (Exception e) {
                Toast.makeText(this, " dddddd  ", Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", "111222");
            }
        }
    }


    public InputStream getInputStream(){
        return is;
    }

    public long getlength(){
        return length;
    }


    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    private String getip(){
        String ip = null;

        String filepath = getExternalFilesDir(null).toString();
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
    private void setip(String ip){
        String filepath = getExternalFilesDir(null).toString();
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
     * get the http address from local file
     * @return http latest address you input
     */
    private String gethttp(){
        String http = null;

        String filepath = getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/http.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                if (!dir.exists()){
                    dir.mkdirs();
                }
                file.createNewFile();

                String str = "https address";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get http", "Fail to create file");
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
                http = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("get http", "Fail to read file");
        }
//        Log.v("get http", http);
        return http;
    }


    /**
     * put the http address you input to local file
     * @param http the http address currently input
     */
    private void sethttp(String http){
        String filepath = getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/http.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get http", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(http.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // get complete download id
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            // to do here
            Toast.makeText(getApplicationContext(),"Download successfully!!!", Toast.LENGTH_SHORT).show();
        }
    };
}
