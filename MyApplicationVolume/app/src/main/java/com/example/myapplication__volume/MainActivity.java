package com.example.myapplication__volume;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.basic.FileManager;
import com.example.basic.Image4DSimple;
import com.example.basic.ImageMarker;
import com.example.basic.LocationSimple;
import com.example.basic.NeuronSWC;
import com.example.basic.NeuronTree;
import com.example.connect.Filesocket_send;
import com.example.connect.RemoteImg;
import com.feature_calc_func.MorphologyCalculate;
import com.learning.pixelclassification.PixelClassification;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.tracingfunc.app2.ParaAPP2;
import com.tracingfunc.app2.V3dNeuronAPP2Tracing;
import com.tracingfunc.gd.CurveTracePara;
import com.tracingfunc.gd.V3dNeuronGDTracing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import cn.carbs.android.library.MDDialog;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class MainActivity extends AppCompatActivity {
//    private int UNDO_LIMIT = 5;
//    private enum Operate {DRAW, DELETE, SPLIT};
//    private Operate [] process = new Operate[UNDO_LIMIT];


    private MyGLSurfaceView myGLSurfaceView;
    private MyRenderer myrenderer;
    private static final String DEBUG_TAG = "Gestures";
    private static Context context;
    private long length;
    private InputStream is;
    private String filepath = "";
    private boolean ifPainting = false;
    private boolean ifPoint = false;
    private boolean ifImport = false;
    private boolean ifAnalyze = false;
    private boolean ifUpload = false;
    //    private boolean ifSaveSwc = false;
    private boolean ifDeletingMarker = false;
    private boolean ifDeletingLine = false;
    private boolean ifSpliting = false;
    private boolean ifSwitch = false;
    private boolean select_img = false;
    private boolean ifLoadLocal = false;
    private boolean ifRemote = false;
    private boolean ifDownloadByHttp = false;

    private boolean[] temp_mode = new boolean[5];

    private boolean ifAnimation = false;
    private Button buttonAnimation;
    private Button buttonUndo;
    private Button Draw;
    private Button Tracing;
    private Button Others;
    private Button FileManager;
    private Button Zoom_in;
    private Button Zoom_out;
    private Button Rotation;
    private Button Sync;
    private Button Switch;
    private Button Share;

    private Button PixelClassification;
    private boolean[][]select= {{true,true,true,true,true,true,true},
    {true,true,true,true,true,true,true},
    {false,false,false,false,false,false,false},
    {false,false,false,false,false,false,false},
    {false,false,false,false,false,false,false},
    {true,true,true,true,true,true,true}};


    private RemoteImg remoteImg;


    private static final int PICKFILE_REQUEST_CODE = 100;

    private LinearLayout ll_top;
    private LinearLayout ll_bottom;

    private int measure_count = 0;
    private List<double[]> fl;



    private int eswc_length;
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static int REQUEST_PERMISSION_CODE = 1;

    //    private int Paintmode = 0;
    private ArrayList<Float> lineDrawed = new ArrayList<Float>();

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myrenderer = new MyRenderer();

        //接受从fileactivity传递过来的文件路径
        Intent intent1 = getIntent();
        String filepath = intent1.getStringExtra(MyRenderer.FILE_PATH);

        if (filepath != null)
            myrenderer.SetPath(filepath);

        Intent intent2 = getIntent();
        String MSG = intent2.getStringExtra(MyRenderer.OUTOFMEM_MESSAGE);

        if (MSG != null)
            Toast.makeText(this, MSG, Toast.LENGTH_SHORT).show();

//        try {
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }



//        Toast.makeText(this,"Filepath: " + filepath, Toast.LENGTH_SHORT).show();

//        Uri uri = Uri.parse((String) filepath);
//
//        try {
//            ParcelFileDescriptor parcelFileDescriptor =
//                    getContentResolver().openFileDescriptor(uri, "r");
//
//            is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//
//            length = (int)parcelFileDescriptor.getStatSize();
//            myrenderer.setInputStream(is);
//            myrenderer.setLength(length);
//
//        }catch (Exception e){
//            Log.v("MainActivity","Some problems in the MainActivity");
//        }


//        Log.v("filepath-mainactivity", filepath);

        myGLSurfaceView = new MyGLSurfaceView(this);
        setContentView(myGLSurfaceView);

        ll_top = new LinearLayout(this);
        ll_bottom = new LinearLayout(this);

        HorizontalScrollView hs_top = new HorizontalScrollView(this);
        ScrollView hs_bottom = new ScrollView(this);

        this.addContentView(hs_top, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        this.addContentView(hs_bottom, lp);

        hs_top.addView(ll_top);
        hs_bottom.addView(ll_bottom);


        Zoom_in = new Button(this);
        Zoom_in.setText("+");

        Zoom_out = new Button(this);
        Zoom_out.setText("-");

        FrameLayout.LayoutParams lp_zoom_in = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_in.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        this.addContentView(Zoom_in, lp_zoom_in);

        FrameLayout.LayoutParams lp_zoom_out = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_out.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        this.addContentView(Zoom_out, lp_zoom_out);


        Zoom_in.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                myrenderer.zoom_in();
                myGLSurfaceView.requestRender();
            }
        });


        Zoom_out.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                myrenderer.zoom_out();
                myGLSurfaceView.requestRender();
            }
        });


        FileManager = new Button(this);
        FileManager.setText("File");
        ll_top.addView(FileManager);

        FileManager.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileManager(v);
            }
        });


        Draw = new Button(this);
        Draw.setText("Draw");
        ll_top.addView(Draw);

        Draw.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Draw(v);
            }
        });


        Tracing = new Button(this);
        Tracing.setText("Trace");
        ll_top.addView(Tracing);

        Tracing.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Tracing(v);
            }
        });

        //像素分类总button
        PixelClassification = new Button(this);
        PixelClassification.setText("Classify");
        ll_top.addView(PixelClassification);

        PixelClassification.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                PixelClassification(v);
            }
        });


        Rotation = new Button(this);
        Rotation.setText("Rotate");

//        int image_res_r_id = getApplicationContext().getResources().getIdentifier("image_name", "drawable", getApplicationContext().getPackageName());
//        Rotation.setBackgroundResource(image_res_r_id);

        ll_top.addView(Rotation);
        final boolean[] b_rotate = {true};

        Rotation.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Rotation();
            }
        });





        Others = new Button(this);
        Others.setText("Config");
        ll_bottom.addView(Others);

        Others.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Other(v);
            }
        });

        Sync = new Button(this);
        Sync.setText("Share");
        ll_bottom.addView(Sync);

        Sync.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Sync(v);
            }
        });

        Switch = new Button(this);
        Switch.setText("Pause");

        Switch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Switch();
            }
        });

//        Share = new Button(this);
//        Share.setText("Share");
//        ll_bottom.addView(Share);
//
//        Share.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(final View v) {
//                myrenderer.setTakePic(true);
//                myGLSurfaceView.requestRender();
//                final String[] imgPath = new String[1];
//                final boolean[] isGet = {false};
//
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @RequiresApi(api = Build.VERSION_CODES.N)
//                    @Override
//                    public void run() {
//
//                        try {
//
//                            Looper.prepare();
//
//                            imgPath[0] = myrenderer.getmCapturePath();
//                            myrenderer.resetCapturePath();
//
//                            if (imgPath[0] !=  null)
//                                Toast.makeText(v.getContext(), "save img to "+ imgPath[0], Toast.LENGTH_SHORT).show();
//                            else
//                                Toast.makeText(v.getContext(), "Fail to Screenshot", Toast.LENGTH_SHORT).show();
//
//                            isGet[0] = true;
//                            Looper.loop();
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                },3000); // 延时3秒
//
//                while (imgPath[0] == null && !isGet[0])
//                    System.out.println("null");
//
//                if (imgPath[0] != null){
//                    Intent shareIntent = new Intent();
//                    shareIntent.setAction(Intent.ACTION_SEND);
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgPath[0]));
//                    shareIntent.setType("image/jpeg");
//                    startActivity(Intent.createChooser(shareIntent, "share"));
//                }
//            }
//        });


        buttonAnimation = new Button(this);
        buttonAnimation.setText("Animation");

        buttonAnimation.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Animation(v);
            }
        });

        buttonUndo = new Button(this);
        buttonUndo.setText("Undo");

        buttonUndo.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                boolean undoSuccess = myrenderer.undo();
                if (!undoSuccess){
                    Toast.makeText(context, "nothing to undo", Toast.LENGTH_SHORT).show();
                }
                myGLSurfaceView.requestRender();
            }
        });

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        remoteImg = new RemoteImg();
        context = getApplicationContext();


//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(myToolbar);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String fodlerPath = data.getDataString();
            Uri uri = data.getData();
//            Uri uri_old = data.getData();

            String filePath = uri.toString();
//            String filePath= getpath(this, uri);

            String filePath_getPath = uri.getPath();

//            String filePath = uri.getPath().substring(14);
//            String filePath = Uri2PathUtil.getRealPathFromUri(getApplicationContext(), uri);
//            String filePath = FilePath.substring(14);
//            String filePath = "/storage/emulated/0/Download/image.v3draw";


            Log.v("MainActivity", filePath);
            Log.v("uri.getPath()", filePath_getPath);
            Log.v("Uri_Scheme:", uri.getScheme());

//            Log.v("Uri_Scheme:", DocumentsContract.getDocumentId(uri));

//            Uri uri_old = data.getData();

//            Toast.makeText(this, "Open" + filePath + "--successfully", Toast.LENGTH_SHORT).show();

            try {

                Log.v("MainActivity", "123456");
//                Log.v("MainActivity",String.valueOf(fileSize));

//                Uri uri_1 = Uri.parse((String) filePath);
//                Log.v("Uri_1: ", uri_1.toString());


                //用于测试能不能修改uri
//                String uri_string = uri_old.toString();
//                int index = uri_string.lastIndexOf("/");
//
//                String filePath = uri_string.substring(0, index+1) + "1pic1.v3draw.apo";
//
//                Uri uri = Uri.parse((String) filePath);


                //-----------------------------------------------
//                ParcelFileDescriptor parcelFileDescriptor =
//                        getContentResolver().openFileDescriptor(uri, "r");
//                is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//                int length = (int) parcelFileDescriptor.getStatSize();
//
//                Log.v("Legth: ", Integer.toString(length));

                //-----------------------------------------------


//                is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//
//                eswc_length = (int)parcelFileDescriptor.getStatSize();
//
//                Log.v("Legth: ", Integer.toString(eswc_length));
//
//                ArrayList<ArrayList<Float>> eswc = new ArrayList<ArrayList<Float>>();
//                EswcReader eswcReader = new EswcReader();
//
//                eswc = eswcReader.read(eswc_length, is);
//
//                myrenderer.importEswc(eswc);

                if (ifImport) {

//                    Log.v("load file", filetype);

                    FileManager fileManager = new FileManager();
                    String fileName = fileManager.getFileName(uri);
                    String filetype = fileName.substring(fileName.lastIndexOf(".")).toUpperCase();

                    System.out.println("filetype: " + filetype + " filename: " + fileName);


                    switch (filetype) {
                        case ".APO":
                            Log.v("Mainctivity", uri.toString());
                            ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
                            ApoReader apoReader = new ApoReader();
                            apo = apoReader.read(uri);
                            myrenderer.importApo(apo);
                            break;
                        case ".SWC":
//                            ArrayList<ArrayList<Float>> swc = new ArrayList<ArrayList<Float>>();
//                            SwcReader swcReader = new SwcReader();
//                            swc = swcReader.read(uri);
                            NeuronTree nt = NeuronTree.readSWC_file(uri);
                            myrenderer.importNeuronTree(nt);
                            break;
                        case ".ANO":
                            ArrayList<ArrayList<Float>> ano_swc = new ArrayList<ArrayList<Float>>();
                            ArrayList<ArrayList<Float>> ano_apo = new ArrayList<ArrayList<Float>>();
                            AnoReader anoReader = new AnoReader();
                            SwcReader swcReader_1 = new SwcReader();
                            ApoReader apoReader_1 = new ApoReader();
                            anoReader.read(uri);

//                        Uri swc_uri = anoReader.getSwc_result();
//                        Uri apo_uri = anoReader.getApo_result();

                            String swc_path = anoReader.getSwc_Path();
                            String apo_path = anoReader.getApo_Path();

//                        String swc_path = getPath(this, swc_uri);
//                        String apo_path = getPath(this, apo_uri);


////                        requestPermissions(PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//                        grantUriPermission(getPackageName(), swc_uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        ParcelFileDescriptor parcelFileDescriptor_swc =
//                                getContentResolver().openFileDescriptor(swc_uri, "r");
//                        InputStream is_swc = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor_swc);
//                        int length_swc = (int)parcelFileDescriptor_swc.getStatSize();
//
//                        Log.v("Length of swc: ", Integer.toString(length_swc));
//
//
////                        requestPermissions(PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//                        grantUriPermission(getPackageName(), apo_uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        ParcelFileDescriptor parcelFileDescriptor_apo =
//                                getContentResolver().openFileDescriptor(apo_uri, "r");
//                        InputStream is_apo = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor_apo);
//                        int length_apo = (int)parcelFileDescriptor_apo.getStatSize();
//
//                        Log.v("Length of apo: ", Integer.toString(length_apo));


//                        ano_swc = swcReader_1.read(length_swc, is_swc);
//                        ano_apo = apoReader_1.read(length_apo, is_apo);

//                        ano_swc = swcReader_1.read(swc_uri);
//                        ano_apo = apoReader_1.read(apo_uri);

//                            ano_swc = swcReader_1.read(swc_path);
                            NeuronTree nt2 = NeuronTree.readSWC_file(swc_path);
                            ano_apo = apoReader_1.read(apo_path);
//                            myrenderer.importSwc(ano_swc);
                            myrenderer.importNeuronTree(nt2);
                            myrenderer.importApo(ano_apo);
                            break;
                        case ".ESWC":
//                            ArrayList<ArrayList<Float>> eswc = new ArrayList<ArrayList<Float>>();
//                            EswcReader eswcReader = new EswcReader();
//
//                            eswc = eswcReader.read(length, is);
//                            myrenderer.importEswc(eswc);
                            NeuronTree nt1 = NeuronTree.readSWC_file(uri);
                            myrenderer.importNeuronTree(nt1);
                            break;

                        default:
                            Toast.makeText(this, "do not support this file", Toast.LENGTH_SHORT).show();

                    }

                }


                if (ifAnalyze) {
                    MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
                    List features = morphologyCalculate.Calculate(uri, false);

//                    fl = new ArrayList<double[]>(features);
//                    if (features.size() != 0) displayResult(features);
//                    else Toast.makeText(getContext(), "the file is empty", Toast.LENGTH_SHORT).show();

                    if (features != null){
                        fl = new ArrayList<double[]>(features);
                        displayResult(features);
                    }
                }



                if (ifUpload){

                    ParcelFileDescriptor parcelFileDescriptor =
                            getContentResolver().openFileDescriptor(uri, "r");
                    InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
                    long length = (int) parcelFileDescriptor.getStatSize();

                    FileManager fileManager = new FileManager();
                    String filename = fileManager.getFileName(uri);

//                    SendSwc("223.3.33.234", this, is, length, filename);
//                    SendSwc("192.168.2.109", this, is, length, filename);
                    SendSwc("39.100.35.131", this, is, length, filename);

                }

                if (ifLoadLocal){
                    myrenderer.SetPath(filePath);
                    ifLoadLocal = false;
                }

//                if (ifRemote){
//                    myrenderer.SetPath(filePath);
//                    ifRemote = false;
//                }

                if (ifDownloadByHttp){
                    myrenderer.SetPath(filePath);
                    ifDownloadByHttp = false;
                }
//
//                ArrayList<ArrayList<Float>> swc = new ArrayList<ArrayList<Float>>();
//                SwcReader swcReader = new SwcReader();
//
//                swc = swcReader.read(uri);
////                apo = apoReader.read(filePath);
//
//                myrenderer.importSwc(swc);

//                ArrayList<ArrayList<Float>> swc = new ArrayList<ArrayList<Float>>();
//                ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
//                AnoReader anoReader = new AnoReader();
//
//                anoReader.read(uri);
//
//                swc = anoReader.getSwc_result();
//                apo = anoReader.getApo_result();
////                apo = apoReader.read(filePath);
//
//                myrenderer.importSwc(swc);
//                myrenderer.importApo(apo);


//                File f = new File(filePath);
//                FileInputStream fid = new FileInputStream(f);

//                fid.write(message.getBytes());
//                long fileSize = f.length();
//            } catch (Exception e) {
//                Toast.makeText(this, " Fail to load file  ", Toast.LENGTH_SHORT).show();
//                Log.v("MainActivity", "111222");
//                Log.v("Exception", e.toString());
//            }

            } catch (OutOfMemoryError e) {
                Toast.makeText(this, " Fail to load file  ", Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", "111222");
                Log.v("Exception", e.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }




    private void SendSwc(String ip, Context context, InputStream is, long length, String filename){
        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {

                if(Looper.myLooper() == null){
                    Looper.prepare();
                }
                Log.v("SendSwc", "here we are");

                try {
                    remoteImg.ip = ip;
                    if (!remoteImg.isSocketSet){
                        Log.v("SendSwc","connext socket");
                        remoteImg.ImgSocket = new Socket(ip, Integer.parseInt("9000"));
                        remoteImg.ImgReader = new BufferedReader(new InputStreamReader(remoteImg.ImgSocket.getInputStream(), "UTF-8"));
                        remoteImg.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(remoteImg.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));
                    }


                    if(remoteImg.ImgSocket.isConnected()){

                        remoteImg.isSocketSet = true;
                        Toast.makeText(context, "Start to upload!!!", Toast.LENGTH_SHORT).show();
                        if (!remoteImg.isOutputShutdown()) {
                            Log.v("SendSwc: ", "Connect with Server successfully");
                            remoteImg.ImgPWriter.println( "connect for android client" + ":import.");
                            remoteImg.ImgPWriter.flush();


                            String content = remoteImg.ImgReader.readLine();
//                            //接收来自服务器的消息
//                            if(remoteImg.ImgSocket.isConnected()) {
//                                if(!remoteImg.ImgSocket.isInputShutdown()) {
//                                    /*读取一行字符串，读取的内容来自于客户机
//                                    reader.readLine()方法是一个阻塞方法，
//                                    从调用这个方法开始，该线程会一直处于阻塞状态，
//                                    直到接收到新的消息，代码才会往下走*/
//                                    String content = "";
//                                    while ((content = remoteImg.ImgReader.readLine()) != null) {
//                                        Log.v("---------Image------:", content);
//                                        if (!((Activity) context).isFinishing()){
//                                            Log.v("Download SWC file: ", content);
////                                            remoteImg.onReadyRead(content, context);
//                                            Looper.loop();
//                                        }
//                                    }
//
//                                }
//                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //TODO  todo somthing here

                                    try {
                                        Log.v("SendSwc: ", "Start to connect filesend_server");

                                        Filesocket_send filesocket_send = new Filesocket_send();
                                        filesocket_send.filesocket = new Socket(ip, 9001);
                                        filesocket_send.mReader = new BufferedReader(new InputStreamReader(filesocket_send.filesocket.getInputStream(), "UTF-8"));
                                        filesocket_send.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_send.filesocket.getOutputStream(), StandardCharsets.UTF_8)));


                                        Log.v("before filesocket_send:", "Connect with Server successfully");

                                        if (filesocket_send.filesocket.isConnected()){

                                            Context[] contexts = new Context[1];
                                            contexts[0] = context;

                                            Log.v("filesocket_send: ", "Connect with Server successfully");

                                            filesocket_send.sendImg(filename, is, length, context);
//                                            Looper.loop();

                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            },1000);  //延迟10秒执行



                            Looper.loop();

                        }



                    }else {
                        Toast.makeText(context, "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        };
        thread.start();

    }


    public void DownloadSwc(String ip, Context context){
        Thread thread = new Thread() {
            @Override
            public void run() {

                if(Looper.myLooper() == null){
                    Looper.prepare();
                }

                try {
                    remoteImg.ip = ip;
                    if (!remoteImg.isSocketSet){

                        Log.v("DownloadSwc: ", "Connect server");

                        remoteImg.ImgSocket = new Socket(ip, Integer.parseInt("9000"));
                        remoteImg.ImgReader = new BufferedReader(new InputStreamReader(remoteImg.ImgSocket.getInputStream(), "UTF-8"));
                        remoteImg.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(remoteImg.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));
                    }

                    Log.v("DownloadSwc: ", "here we are 2");

                    if(remoteImg.ImgSocket.isConnected()){
                        remoteImg.isSocketSet = true;
                        Log.v("DownloadSwc: ", "Connect with Server successfully");
                        Toast.makeText(getContext(), "Connect with Server successfully", Toast.LENGTH_SHORT).show();
                        remoteImg.ImgPWriter.println( "connect from android client" + ":down.");
                        remoteImg.ImgPWriter.flush();

                    }else {
                        Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    }

                    //接收来自服务器的消息
                    while(remoteImg.ImgSocket.isConnected()) {
                        if(!remoteImg.ImgSocket.isInputShutdown()) {
                        /*读取一行字符串，读取的内容来自于客户机
                        reader.readLine()方法是一个阻塞方法，
                        从调用这个方法开始，该线程会一直处于阻塞状态，
                        直到接收到新的消息，代码才会往下走*/
                            String content = "";
                            while ((content = remoteImg.ImgReader.readLine()) != null) {
                                Log.v("---------Image------:", content);
                                if (!((Activity) context).isFinishing()){
                                    Log.v("Download SWC file: ", content);
                                    remoteImg.onReadyRead(content, context);
                                    Looper.loop();
                                }
                            }

                        }
                        Thread.sleep(200);
                    }

                    Looper.loop();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        };
        thread.start();

    }


     private void ConnectServer(){

     }

   /**
     * function for the FileManager button
     *
     * @param v the button: FileManager
     */
    private void FileManager(View v) {
        new XPopup.Builder(this)
                .atView(v)
                .asAttachList(new String[]{ "Open Local file", "Open Remote file", "Load SWC file", "Save SWC file" },
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Load SWC file":
                                        LoadSWC();
                                        break;
                                    case "Save SWC file":
                                        SaveSWC();
                                        break;
                                    case "Open Local file":
                                        loadLocalFile();
                                        break;
                                    case "Open Remote file":
                                        remote(v);
                                        break;
                                }

                            }
                        })
                .show();
    }

    private void downloadFile(){
        ifDownloadByHttp = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.downloaddialog_layout, null);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();

        final EditText editTextHttp = (EditText) view.findViewById(R.id.http);
        Button buttonConfirm = (Button) view.findViewById(R.id.confirm);
        Button buttonCancel = (Button) view.findViewById(R.id.canel);

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
                                             String http = "";

                                             @Override
                                             public void onClick(View v) {
                                                 http = editTextHttp.getText().toString();

                                                 String downloadpath = "";

                                                 Log.v("DownloadFile", http+"   LLLLLLLLLLLLL");

                                                 if (http.startsWith("https://")){
                                                     downloadpath = http;
                                                 }else {
                                                     downloadpath = "https://" + http;
                                                 }

//                downloadpath = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";

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

                                                     request.setDestinationInExternalFilesDir( context , Environment.DIRECTORY_DOWNLOADS ,  uri.getLastPathSegment() );


                                                     //获取下载管理器
                                                     DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                                     //将下载任务加入下载队列，否则不会进行下载
                                                     long ID = downloadManager.enqueue(request);
                                                     Toast.makeText(context, "Start to download the file", Toast.LENGTH_SHORT).show();

                                                     listener(ID);

                                                 }catch (Exception e){
                                                     Toast.makeText(context, "make sure the address is legal", Toast.LENGTH_SHORT).show();
                                                 }

                                             }
                                         }
        );

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

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

    /**
     * function for the draw button
     *
     * @param v the button: draw
     */
    private void Draw(View v) {

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"PinPoint", "Draw Curve", "Delete marker", "Delete curve", "Split", "Set PenColor", "Exit Drawing mode"},
//                        new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
//                                if (!(ifPoint || ifPainting || ifDeletingMarker || ifDeletingLine || ifSpliting))
//                                    ll_top.addView(buttonUndo);
                                switch (text) {
                                    case "PinPoint":
                                        ifPoint = !ifPoint;
                                        ifPainting = false;
                                        ifDeletingMarker = false;
                                        ifDeletingLine = false;
                                        ifSpliting = false;
                                        if (ifPoint) {
                                            Draw.setText("PinPoint");
                                            Draw.setTextColor(Color.RED);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
                                                ll_top.addView(buttonUndo);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            Draw.setText("Define Objects");
                                            Draw.setTextColor(Color.BLACK);
                                            ll_bottom.removeView(Switch);
                                            ll_top.removeView(buttonUndo);
                                        }
                                        break;

                                    case "Draw Curve":
                                        ifPainting = !ifPainting;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifDeletingLine = false;
                                        ifSpliting = false;
                                        if (ifPainting) {
                                            Draw.setText("Draw Curve");
                                            Draw.setTextColor(Color.RED);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
                                                ll_top.addView(buttonUndo);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            Draw.setText("Define Objects");
                                            Draw.setTextColor(Color.BLACK);
                                            ll_bottom.removeView(Switch);
                                            ll_top.removeView(buttonUndo);
                                        }
                                        break;

                                    case "Delete marker":
                                        ifDeletingMarker = !ifDeletingMarker;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingLine = false;
                                        ifSpliting = false;
                                        if (ifDeletingMarker) {
                                            Draw.setText("Delete marker");
                                            Draw.setTextColor(Color.RED);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
                                                ll_top.addView(buttonUndo);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            Draw.setText("Define Objects");
                                            Draw.setTextColor(Color.BLACK);
                                            ll_bottom.removeView(Switch);
                                            ll_top.removeView(buttonUndo);
                                        }
                                        break;

                                    case "Delete curve":
                                        ifDeletingLine = !ifDeletingLine;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        if (ifDeletingLine) {
                                            Draw.setText("Delete curve");
                                            Draw.setTextColor(Color.RED);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
                                                ll_top.addView(buttonUndo);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            Draw.setText("Define Objects");
                                            Draw.setTextColor(Color.BLACK);
                                            ll_bottom.removeView(Switch);
                                            ll_top.removeView(buttonUndo);
                                        }
                                        break;

                                    case "Split":
                                        ifSpliting = !ifSpliting;
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        if (ifSpliting) {
                                            Draw.setText("Split");
                                            Draw.setTextColor(Color.RED);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
                                                ll_top.addView(buttonUndo);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            Draw.setText("Define Objects");
                                            Draw.setTextColor(Color.BLACK);
                                            ll_bottom.removeView(Switch);
                                            ll_top.removeView(buttonUndo);
                                        }
                                        break;

                                    case "Set PenColor":
                                        //调用选择画笔窗口
                                        PenSet();

                                        break;

                                    case "Exit Drawing mode":
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        Draw.setText("Define Objects");
                                        Draw.setTextColor(Color.BLACK);
                                        ll_bottom.removeView(Switch);
                                        ll_top.removeView(buttonUndo);
                                        break;

                                }
                            }
                        })
                .show();
    }


    /**
     * function for the Tracing button
     *
     * @param v the button: tracing
     */
    private void Tracing(final View v) {

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"APP2", "GD", "Clear tracing"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "GD":
                                        try {
                                            Log.v("Mainactivity", "GD-Tracing start~");
                                            Toast.makeText(v.getContext(), "GD-Tracing start~", Toast.LENGTH_SHORT).show();
                                            Timer timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void run() {

                                                    try {
                                                        GDTracing();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, 1000); // 延时1秒
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "APP2":
                                        try {
                                            Log.v("Mainactivity", "APP2-Tracing start~");
                                            Toast.makeText(v.getContext(), "APP2-Tracing start~", Toast.LENGTH_SHORT).show();
                                            Timer timer = new Timer();
                                            timer.schedule(new TimerTask() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void run() {

                                                    try {
                                                        APP2();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, 1000); // 延时1秒
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "Clear tracing":
                                        myrenderer.deleteAllTracing();
                                        myGLSurfaceView.requestRender();
                                        break;
                                }
                            }
                        })
                .show();
    }


    /**
     * function for the other button
     *
     * @param v the button: other
     */
    private void Other(final View v) {
        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Analyze", "Animate", "Share", "Version"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Analyze":
                                        Analyse();
                                        break;

                                    case "Animate":
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifDeletingLine = false;
                                        SetAnimation();
                                        break;

                                    case "Share":
                                        Share(v);
                                        break;

                                    case "Version":
                                        Version();
                                        break;

                                    case "Learning":
                                        Learning();
                                        break;

                                }
                            }
                        })
                .show();
    }

    //像素分类界面
    private void PixelClassification(final View v) {
        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Run", "For Developer..."},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "For Developer...":
                                        //调用特征选择窗口
                                        FeatureSet();
                                        break;

                                    case "Run":
                                        //调用像素分类接口，显示分类结果
                                        Learning();
                                        break;

                                }
                            }
                        })
                .show();
    }







    /**
     * function for the Sync button
     *
     * @param v the button: Sync
     */
    private void Sync(final View v) {
        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Upload SWC", "Download SWC"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Upload SWC":
                                        UploadSWC();
                                        break;

                                    case "Download SWC":
                                        DownloadSWC();
                                        break;
                                }
                            }
                        })
                .show();
    }



    private void UploadSWC() {

        ifUpload = true;
        ifImport = false;
        ifAnalyze = false;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }


    private void DownloadSWC() {

//        DownloadSwc("223.3.33.234", this);
        Log.v("DownloadSWC: ", "here we are");
//        DownloadSwc("192.168.2.109", this);
        DownloadSwc("39.100.35.131", this);
    }


    private void LoadSWC() {

        if (!ifImport) {
            ifImport = true;
            ifAnalyze = false;
            ifUpload = false;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }


    private void Share(View v) {
        myrenderer.setTakePic(true);
        myGLSurfaceView.requestRender();
        final String[] imgPath = new String[1];
        final boolean[] isGet = {false};

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                try {

                    if (Looper.myLooper() == null)
                        Looper.prepare();

                    imgPath[0] = myrenderer.getmCapturePath();
                    myrenderer.resetCapturePath();

                    if (imgPath[0] != null)
//                        Toast.makeText(v.getContext(), "save screenshot to " + imgPath[0], Toast.LENGTH_SHORT).show();
                        Log.v("Share","save screenshot to " + imgPath[0]);
                    else
                        Toast.makeText(v.getContext(), "Fail to screenshot", Toast.LENGTH_SHORT).show();

                    isGet[0] = true;
                    Looper.loop();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 2000); // 延时2秒

        while (imgPath[0] == null && !isGet[0]);
//            System.out.println("null");

        if (imgPath[0] != null) {
            Intent shareIntent = new Intent();
            String imageUri = insertImageToSystem(context, imgPath[0]);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUri));
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, "Share from C3"));
        }

    }


    private static String insertImageToSystem(Context context, String imagePath) {
        String url = "";
        String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1 );
        try {
            url = MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, filename, "ScreenShot from C3");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Filename: " + filename);
        System.out.println("Url: " + url);
        return url;
    }



    private void Analyse() {


        new XPopup.Builder(this)
//        .maxWidth(400)
//        .maxHeight(1350)
                .asCenterList("morphology calculate", new String[]{"Analyze a SWC file", "Analyze current tracing"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Analyze a SWC file":
                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        startActivityForResult(intent, 1);
                                        ifAnalyze = true;
                                        ifImport = false;
                                        ifUpload = false;
                                        break;

                                    case "Analyze current tracing":
                                        NeuronTree nt = myrenderer.getNeuronTree();
                                        if (nt.listNeuron.isEmpty()) {
//                                            Toast.makeText(context, "Empty tracing, do nothing", Toast.LENGTH_LONG).show();
                                            Toast.makeText(getContext(), "Empty tracing, do nothing", Toast.LENGTH_LONG).show();
                                            break;
                                        }
                                        MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
                                        List<double[]> features = morphologyCalculate.CalculatefromNT(nt, false);
                                        fl = new ArrayList<double[]>(features);
                                        if (features.size() != 0) displayResult(features);
                                        else Toast.makeText(getContext(), "the file is empty", Toast.LENGTH_SHORT).show();
                                        break;

                                    default:
//                                        Toast.makeText(context, "Default in analysis", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getContext(), "Default in analysis", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                .show();


    }


    private void Animation(final View v) {

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Start", "Pause", "Resume", "Stop"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Start":
                                        myrenderer.myAnimation.Start();
                                        break;

                                    case "Pause":
                                        myrenderer.myAnimation.Pause();
                                        break;

                                    case "Resume":
                                        myrenderer.myAnimation.Resume();
                                        break;

                                    case "Stop":
                                        myrenderer.myAnimation.Stop();
                                        ifAnimation = false;
                                        ll_top.removeView(buttonAnimation);
                                        break;

                                    default:
                                        Toast.makeText(getContext(), "there is something wrong in animation", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .show();

    }


    private void Rotation() {

        if (myrenderer.myAnimation != null){
            ifAnimation = !ifAnimation;

            if (ifAnimation) {
                Rotation.setText("Pause");
                myrenderer.myAnimation.quickStart();
                myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            } else {
                Rotation.setText("Rotate");
                myrenderer.myAnimation.quickStop();
                myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            }

        }else {
            Toast.makeText(this,"Pleas load a file first!",Toast.LENGTH_SHORT).show();
        }
    }


    private void Switch() {
        ifSwitch = !ifSwitch;
        if (ifSwitch) {
            Switch.setText("Restart");
            Switch.setTextColor(Color.RED);
            temp_mode[0] = ifPainting;
            temp_mode[1] = ifPoint;
            temp_mode[2] = ifDeletingLine;
            temp_mode[3] = ifDeletingMarker;
            temp_mode[4] = ifSpliting;

            ifPainting = false;
            ifPoint = false;
            ifDeletingLine = false;
            ifDeletingMarker = false;
            ifSpliting = false;

        } else {
            Switch.setText("Pause");
            Switch.setTextColor(Color.BLACK);
            ifPainting       = temp_mode[0];
            ifPoint          = temp_mode[1];
            ifDeletingLine   = temp_mode[2];
            ifDeletingMarker = temp_mode[3];
            ifSpliting       = temp_mode[4];
        }
    }

    private void Version() {
        new XPopup.Builder(this)
                .asConfirm("Version", "version: 202005014b XF",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                            }
                        })
                .show();
    }


    private void loadLocalFile(){
        ifLoadLocal = true;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent,1);
//            startActivityForResult(Intent.createChooser(intent, "Open folder"), 1);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Error when open file!" + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void remote(View v){
        context = v.getContext();
        ifRemote = true;

        if ( select_img ){

            Select_img();
//            select_img = false;
        }else {

            new XPopup.Builder(this)
                    .atView(v)
                    .asAttachList(new String[]{"Select file", "Select block", "Download by http"},
                            new int[]{},
                            new OnSelectListener() {
                                @Override
                                public void onSelect(int position, String text) {
                                    switch (text) {
                                        case "Select block":
                                            remoteImg.Selectblock(context, false);
                                            break;

                                        case "Select file":
                                            Select_img();
                                            break;

                                        case "Download by http":
                                            downloadFile();
                                            break;
                                    }
                                }
                            })
                    .show();
        }
    }

    public void Select_img(){
        new MDDialog.Builder(this)
                .setContentView(R.layout.image_select)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view

                        EditText et0 = (EditText) contentView.findViewById(R.id.edit0);
                        et0.setText(getip());
                    }
                })
                .setTitle("Connect with Server")
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

                        String ip = et0.getText().toString();

                        Log.v("Select_img", ip);

                        if (ip != getip()){
                            setip(ip);
                        }

                        if(!ip.isEmpty()){
                            //输入的信息全，就可以进行连接操作
                            ConnectServer(ip, context);
                        }else{
                            Toast.makeText(getApplicationContext(), "Please make sure the information is right!!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setWidthMaxDp(600)
                .create()
                .show();
    }



    private void ConnectServer(String ip, Context context){
        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {
//                Looper.prepare();

                try {

                    if (!remoteImg.isSocketSet){
                        Log.v("ConnectServer","start to connect server");
                        remoteImg.ip = ip;
                        remoteImg.ImgSocket = new Socket(ip, Integer.parseInt("9000"));
                        remoteImg.ImgReader = new BufferedReader(new InputStreamReader(remoteImg.ImgSocket.getInputStream(), "UTF-8"));
                        remoteImg.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(remoteImg.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));
                    }


                    if(remoteImg.ImgSocket.isConnected()){

                        Log.v("ConnectServer","send some message to server");
                        remoteImg.isSocketSet = true;
                        ShowToast(context, "Connect with Server successfully");
//                        Toast.makeText(getContext(), "Connect with Server successfully", Toast.LENGTH_SHORT).show();
                        remoteImg.ImgPWriter.println( "connect for android client" + ":choose3.");
                        remoteImg.ImgPWriter.flush();
//                        Looper.loop();

                    }else {
                        Log.v("ConnectServer","fail to connect server");
                        ShowToast(context, "Can't connect, try again please!");
//                        Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
//                        Looper.loop();
                    }

                    //接收来自服务器的消息
                    while(remoteImg.ImgSocket.isConnected()) {
                        if(!remoteImg.ImgSocket.isInputShutdown()) {
                        /*读取一行字符串，读取的内容来自于客户机
                        reader.readLine()方法是一个阻塞方法，
                        从调用这个方法开始，该线程会一直处于阻塞状态，
                        直到接收到新的消息，代码才会往下走*/
                            String content = "";
                            while ((content = remoteImg.ImgReader.readLine()) != null) {
                                Log.v("---------Image------:", content);
                                if (!((Activity) context).isFinishing()){
                                    remoteImg.onReadyRead(content, context);
                                    Looper.loop();
                                }
                            }

                        }
                        Thread.sleep(200);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    ShowToast(context, "Can't connect, try again please!");
//                    Toast.makeText(context, "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                }
            }
        };
        thread.start();

    }




    private String getip(){
        String ip = null;

        String filepath = getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/ip.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "39.100.35.131";
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

    private void SaveSWC() {
        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.save_swc)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view

                    }
                })
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

                        System.out.println("start to save-------");

                        EditText swcName = contentView.findViewById(R.id.swcname);
                        String swcFileName = swcName.getText().toString();
                        if (swcFileName == ""){
                            Toast.makeText(context, "The name should not be empty.", Toast.LENGTH_SHORT).show();
                        }
                        myrenderer.reNameCurrentSwc(swcFileName);

//                        String dir = getExternalFilesDir(null).toString();
                        String dir_str = "/storage/emulated/0/C3";

                        File dir = new File(dir_str);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        String error = null;
                        try {
                            error = myrenderer.saveCurrentSwc(dir_str);
                        } catch (Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (error != "") {
                            if (error == "This file already exits"){
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                                AlertDialog aDialog = new AlertDialog.Builder(context)
                                        .setTitle("This file already exits")
                                        .setMessage("Are you sure to overwrite it?")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String errorMessage = "";
                                                try{
                                                    errorMessage = myrenderer.oversaveCurrentSwc(dir_str);
                                                }catch (Exception e){
                                                    System.out.println(errorMessage);
                                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })

                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .create();
                            }
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                        } else{
                            Toast.makeText(context, "save SWC to " + dir + "/" + swcFileName + ".swc", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle("Save SWC File")
                .create();

        mdDialog.show();
        mdDialog.getWindow().setLayout(1000, 1500);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void APP2() throws Exception {
        Image4DSimple img = myrenderer.getImg();
        img.getDataCZYX();
        if (!img.valid()) {
            Log.v("APP2Tracing", "Please load img first!");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(this, "Please load img first!", Toast.LENGTH_LONG).show();
            Looper.loop();
            return;
        }
        ArrayList<ImageMarker> markers = myrenderer.getMarkerList();
        try {
            ParaAPP2 p = new ParaAPP2();
            p.p4dImage = img;
            p.xc0 = p.yc0 = p.zc0 = 0;
            p.xc1 = (int) p.p4dImage.getSz0() - 1;
            p.yc1 = (int) p.p4dImage.getSz1() - 1;
            p.zc1 = (int) p.p4dImage.getSz2() - 1;
            p.landmarks = new LocationSimple[markers.size()];
            p.bkg_thresh = -1;
            for (int i = 0; i < markers.size(); i++) {
                p.landmarks[i] = new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
            }
            System.out.println("---------------start---------------------");
            p.outswc_file = getExternalFilesDir(null).toString() + "/" + "app2.swc";//"/storage/emulated/0/Download/app2.swc";
            System.out.println(p.outswc_file);
            V3dNeuronAPP2Tracing.proc_app2(p);

            NeuronTree nt = NeuronTree.readSWC_file(p.outswc_file);
            for (int i = 0; i < nt.listNeuron.size(); i++) {
                nt.listNeuron.get(i).type = 4;
                if (nt.listNeuron.get(i).parent == -1) {
                    NeuronSWC s = nt.listNeuron.get(i);
                    ImageMarker m = new ImageMarker(s.x, s.y, s.z);
                    m.type = 2;
                    myrenderer.getMarkerList().add(m);
                }
            }
            System.out.println("size: " + nt.listNeuron.size());
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(this, "APP2-Tracing finish, size of result swc: " + Integer.toString(nt.listNeuron.size()), Toast.LENGTH_SHORT).show();
            myrenderer.importNeuronTree(nt);
            myGLSurfaceView.requestRender();
            Looper.loop();

        } catch (Exception e) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Looper.loop();
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void GDTracing() throws Exception {
        Image4DSimple img = myrenderer.getImg();
        if (!img.valid()) {
            Log.v("GDTracing", "Please load img first!");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(this, "Please load img first!", Toast.LENGTH_LONG).show();
            Looper.loop();
            return;
        }
        ArrayList<ImageMarker> markers = myrenderer.getMarkerList();
        if (markers.size() <= 1) {
            Log.v("GDTracing", "Please generate at least two markers!");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(this, "Please produce at least two markers!", Toast.LENGTH_LONG).show();
            Looper.loop();
            return;
        }
        LocationSimple p0 = new LocationSimple(markers.get(0).x, markers.get(0).y, markers.get(0).z);
        Vector<LocationSimple> pp = new Vector<LocationSimple>();
        for (int i = 1; i < markers.size(); i++) {
            LocationSimple p = new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
            pp.add(p);
        }

        NeuronTree outswc = new NeuronTree();
//        int[] sz = new int[]{(int) img.getSz0(), (int) img.getSz1(), (int) img.getSz2(), (int) img.getSz3()};
        CurveTracePara curveTracePara = new CurveTracePara();


        try {
            outswc = V3dNeuronGDTracing.v3dneuron_GD_tracing(img, p0, pp, curveTracePara, 1.0);
        } catch (Exception e) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            Looper.loop();
        }
        for (int i = 0; i < outswc.listNeuron.size(); i++) {
            outswc.listNeuron.get(i).type = 6;
        }

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        Toast.makeText(this, "GD-Tracing finished, size of result swc: " + Integer.toString(outswc.listNeuron.size()), Toast.LENGTH_SHORT).show();
        myrenderer.importNeuronTree(outswc);
        myGLSurfaceView.requestRender();
        Looper.loop();

    }


    MDDialog.Builder ar_mdDialog_bd = new MDDialog.Builder(this).setContentView(R.layout.analysis_result);
    MDDialog ar_mdDialog = null;

    /**
     * display the result of morphology calculate
     *
     * @param featurelist the features of result
     */

    @SuppressLint("DefaultLocale")
    private void displayResult(final List<double[]> featurelist) {
        final String[] title;
        final int[] id_title;
        final int[] id_content;
        final int[] id_rl;
        if (measure_count > featurelist.size() - 1) {
            measure_count = 0;
        } else if (measure_count < 0) {
            measure_count = featurelist.size() - 1;
        }
        double[] result = featurelist.get(measure_count);
        String[] subtitle = new String[featurelist.size()];
        for (int i = 0; i < featurelist.size(); i++) {
//            if(result[0] > 1 && i == 0){
//                subtitle[0] = "Global";
//            }
//            else if (result[0] > 1&& i>0){
            if (featurelist.size() > 1) {
                subtitle[i] = String.format("Tree %d/%d", i + 1, featurelist.size());
            } else {
                subtitle[i] = "";
            }
        }

        title = new String[]{
                "number of nodes",
                "soma surface",
                "number of stems",
                "number of bifurcations",
                "number of branches",
                "number of tips",
                "overall width",
                "overall height",
                "overall depth",
                "average diameter",
                "total length",
                "total surface",
                "total volume",
                "max euclidean distance",
                "max path distance",
                "max branch order",
                "average contraction",
                "average fragmentation",
                "average parent-daughter ratio",
                "average bifurcation angle local",
                "average bifurcation angle remote",
                "Hausdorff dimension"
        };

        id_title = new int[]{R.id.title0, R.id.title1, R.id.title2, R.id.title3, R.id.title4,
                R.id.title5, R.id.title6, R.id.title7, R.id.title8, R.id.title9,
                R.id.title10, R.id.title11, R.id.title12, R.id.title13, R.id.title14,
                R.id.title15, R.id.title16, R.id.title17, R.id.title18, R.id.title19,
                R.id.title20, R.id.title21};

        id_content = new int[]{R.id.content0, R.id.content1, R.id.content2, R.id.content3, R.id.content4,
                R.id.content5, R.id.content6, R.id.content7, R.id.content8, R.id.content9,
                R.id.content10, R.id.content11, R.id.content12, R.id.content13, R.id.content14,
                R.id.content15, R.id.content16, R.id.content17, R.id.content18, R.id.content19,
                R.id.content20, R.id.content21};

        id_rl = new int[]{R.id.RL0, R.id.RL1, R.id.RL2, R.id.RL3, R.id.RL4,
                R.id.RL5, R.id.RL6, R.id.RL7, R.id.RL8, R.id.RL9,
                R.id.RL10, R.id.RL11, R.id.RL12, R.id.RL13, R.id.RL14,
                R.id.RL15, R.id.RL16, R.id.RL17, R.id.RL18, R.id.RL19,
                R.id.RL20, R.id.RL21};
//        new XPopup.Builder(this)
////                .maxWidth(960)
//                .maxHeight(1350)
//                .asBottomList("Global features of the neuron", result_display,
//                        new OnSelectListener() {
//                            @Override
//                            public void onSelect(int position, String text) {
////                                toast("click " + text);
//                            }
//                        })
//                .show();


//        MDDialog mdDialog = new MDDialog.Builder(this)
        ar_mdDialog =
                ar_mdDialog_bd
                        .setContentView(R.layout.analysis_result)
                        .setContentViewOperator(new MDDialog.ContentViewOperator() {

                            @Override
                            public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                                //analysis_result next page
                                Button ar_right = (Button) contentView.findViewById(R.id.ar_right);
                                ar_right.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (featurelist.size() > 1) {
                                            measure_count++;
                                            ar_mdDialog.dismiss();
                                            displayResult(fl);
                                        }
                                    }
                                });
                                Button ar_left = (Button) contentView.findViewById(R.id.ar_left);
                                ar_left.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (featurelist.size() > 1) {
                                            measure_count--;
                                            ar_mdDialog.dismiss();
                                            displayResult(fl);
                                        }
                                    }
                                });
                                if (title.length == 8) {
                                    for (int i = 8; i < id_rl.length; i++) {
                                        contentView.findViewById(id_rl[i]).setVisibility(View.GONE);
                                    }
                                } else if (title.length == 22) {
                                    for (int i = 8; i < id_rl.length; i++) {
                                        contentView.findViewById(id_rl[i]).setVisibility(View.VISIBLE);
                                    }
                                }


                                String result_str;
                                int num;
                                for (int i = 0; i < title.length; i++) {
                                    TextView tx = contentView.findViewById(id_title[i]);
                                    tx.setText(title[i]);

                                    TextView ct = contentView.findViewById(id_content[i]);
                                    if (title[i].substring(0, 6).equals("number") || title[i].substring(0, 6).equals("max br")) {
                                        result_str = ": " + String.format("%d", (int) result[i + 1]);
                                    } else {
                                        num = Value_Display_Length(result[i + 1]);
                                        result_str = ": " + String.format("%." + String.format("%d", num) + "f", (float) result[i + 1]);
                                    }
                                    ct.setText(result_str);

                                }
                            }

                        })
                        .setTitle("Measured features " + subtitle[measure_count])
                        .create();

//        ar_mdDialog.show();
        ar_mdDialog.show();
        ar_mdDialog.getWindow().setLayout(1000, 1500);

    }

    /**
     * format output of morphology feature's value
     *
     * @param value feature's value
     * @return Number of digits
     */
    private int Value_Display_Length(double value) {
        String s_value = (value + "").split("\\.")[0];
        int len = s_value.length();
        if (len >= 8) {
            return 0;
        } else if ((8 - len) > 4) {
            return 4;
        } else {
            return 8 - len;
        }
    }

    /**
     * add space at the end of string
     *
     * @param str   original string
     * @param count num of space
     * @return result string
     */
    private String AddSpace(String str, int count) {
        String result = str;
        for (int i = 0; i < count; i++) {
            result = result + " ";
        }
        return result;
    }


    private void SetAnimation() {

        final String[] rotation_type = new String[1];
        final boolean [] ifChecked = {false, false};

        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.animation)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view

                        Switch on_off = contentView.findViewById(R.id.switch_animation);
                        on_off.setChecked(ifAnimation);

                        on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                ifChecked[1] = true;
                                if (isChecked) {
                                    ifChecked[0] = true;
                                } else {
                                    ifChecked[0] = false;
                                }
                            }
                        });

                        final Spinner type = contentView.findViewById(R.id.spinner_type);
                        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                rotation_type[0] = type.getSelectedItem().toString();
                                Log.v("onItemSelected", type.getSelectedItem().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                    }
                })
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
                        if (ifChecked[1] == true) {
                            ifAnimation = ifChecked[0];
                        }

                        EditText speed = contentView.findViewById(R.id.edit_speed);
                        String rotation_speed = speed.getText().toString();

                        myrenderer.myAnimation.Stop();

                        if (ifAnimation) {
                            myrenderer.myAnimation.setAnimation(ifAnimation, Float.parseFloat(rotation_speed), rotation_type[0]);
                        }

                        if (ifAnimation) {
                            myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//                            Draw.setText("Draw");
//                            Draw.setTextColor(Color.BLACK);
//                            Others.setText("Animaition");
//                            Others.setTextColor(Color.RED);

                            ll_top.addView(buttonAnimation);

                        } else {

                            try {
                                ll_top.removeView(buttonAnimation);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//                            Others.setText("Others");
//                            Others.setTextColor(Color.BLACK);
                        }

                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle("Animation")
                .create();

        mdDialog.show();
        mdDialog.getWindow().setLayout(1000, 1500);
    }


    private String getPath(Context context, Uri uri) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return path;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getpath(Context context, Uri uri) {
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        String path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    String path = getDataColumn(context, contentUri, null, null);
                    return path;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    String path = getDataColumn(context, contentUri, selection, selectionArgs);
                    return path;
                }
            }
        }
        return null;
    }


    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static Context getContext() {
        return context;
    }


    //renderer 的生存周期和activity保持一致
    @Override
    public void onDestroy() {
        super.onDestroy();
        context = null;
        remoteImg = null;
    }

    //renderer 的生存周期和activity保持一致
    @Override
    protected void onPause() {
        super.onPause();
        myGLSurfaceView.onPause();
        Log.v("onPause", "start-----");
//        remoteImg = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        myGLSurfaceView.onResume();
        Log.v("Path", filepath);
        Log.v("onResume", "start-----");
//        remoteImg = new RemoteImg();
    }

    private void Learning() {

        Image4DSimple img = myrenderer.getImg();
//        Image4DSimple resampleimg=new Image4DSimple();
//        Image4DSimple upsampleimg=new Image4DSimple();
        Image4DSimple outImg=new Image4DSimple();
//        if(img.getSz0()>32&&img.getSz1()>32&&img.getSz2()>32)
//        {
//            Image4DSimple.resample3dimg_interp(resampleimg,img,img.getSz0()/32 , img.getSz1()/32, img.getSz2()/32, 1);
//        }
//        else
//        {
//            resampleimg=img;
//        }
//        //对图像进行降采样
//
//        System.out.println("downsample image");
        NeuronTree nt = myrenderer.getNeuronTree();
        PixelClassification p = new PixelClassification();

        /*boolean[][] selections = new boolean[][]{
                {true,true,true,true,true,true,true},
                {true,true,true,true,true,true,true},
                {false,false,false,false,false,false,false},
                {false,false,false,false,false,false,false},
                {false,false,false,false,false,false,false},
                {true,true,true,true,true,true,true}
        };*/
        boolean[][] selections = select;
        System.out.println("select is");
        System.out.println(select);
        p.setSelections(selections);

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        Toast.makeText(this, "pixel  classification start~", Toast.LENGTH_SHORT).show();
//        Looper.loop();
        try{
//            outImg = p.getPixelClassificationResult(resampleimg,nt);
            outImg = p.getPixelClassificationResult(img,nt);
            System.out.println("outImg: "+outImg.getSz0()+" "+outImg.getSz1()+" "+outImg.getSz2()+" "+outImg.getSz3());
            System.out.println(outImg.getData().length);

//            if(img.getSz0()>32&&img.getSz1()>32&&img.getSz2()>32)
//            {
//                Image4DSimple.upsample3dimg_interp(upsampleimg, outImg,img.getSz0()/32 , img.getSz1()/32, img.getSz2()/32, 1);
//            }
//            else
//            {
//                upsampleimg=outImg;
//            }
//            System.out.println("upsample image");
//            myrenderer.ResetImg(upsampleimg);
            myrenderer.ResetImg(outImg);
            myGLSurfaceView.requestRender();
        }catch (Exception e){
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

//        Image4DSimple out = new Image4DSimple();
//        out.setData(img);
//        myrenderer.ResetImg(out);




    }

    public void FeatureSet(){

        new MDDialog.Builder(this)
                .setContentView(R.layout.pixel_choose)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view


                    }
                })
                .setTitle("Feature Set")
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
                        CheckBox ch1 = (CheckBox) contentView.findViewById(R.id.checkBox1);
                        CheckBox ch2 = (CheckBox) contentView.findViewById(R.id.checkBox2);
                        CheckBox ch3 = (CheckBox) contentView.findViewById(R.id.checkBox3);
                        CheckBox ch4 = (CheckBox) contentView.findViewById(R.id.checkBox4);
                        CheckBox ch5 = (CheckBox) contentView.findViewById(R.id.checkBox5);
                        CheckBox ch6 = (CheckBox) contentView.findViewById(R.id.checkBox6);
                        CheckBox ch7 = (CheckBox) contentView.findViewById(R.id.checkBox7);
                        CheckBox ch8 = (CheckBox) contentView.findViewById(R.id.checkBox8);
                        CheckBox ch9 = (CheckBox) contentView.findViewById(R.id.checkBox9);
                        CheckBox ch10 = (CheckBox) contentView.findViewById(R.id.checkBox10);
                        CheckBox ch11 = (CheckBox) contentView.findViewById(R.id.checkBox11);
                        CheckBox ch12 = (CheckBox) contentView.findViewById(R.id.checkBox12);
                        CheckBox ch13 = (CheckBox) contentView.findViewById(R.id.checkBox13);
                        CheckBox ch14 = (CheckBox) contentView.findViewById(R.id.checkBox14);
                        CheckBox ch22 = (CheckBox) contentView.findViewById(R.id.checkBox22);
                        CheckBox ch23 = (CheckBox) contentView.findViewById(R.id.checkBox23);
                        CheckBox ch24 = (CheckBox) contentView.findViewById(R.id.checkBox24);
                        CheckBox ch25 = (CheckBox) contentView.findViewById(R.id.checkBox25);
                        CheckBox ch26 = (CheckBox) contentView.findViewById(R.id.checkBox26);
                        CheckBox ch27 = (CheckBox) contentView.findViewById(R.id.checkBox27);
                        CheckBox ch28 = (CheckBox) contentView.findViewById(R.id.checkBox28);
                        CheckBox ch29 = (CheckBox) contentView.findViewById(R.id.checkBox29);
                        CheckBox ch30 = (CheckBox) contentView.findViewById(R.id.checkBox30);
                        CheckBox ch31 = (CheckBox) contentView.findViewById(R.id.checkBox31);
                        CheckBox ch32 = (CheckBox) contentView.findViewById(R.id.checkBox32);
                        CheckBox ch33 = (CheckBox) contentView.findViewById(R.id.checkBox33);
                        CheckBox ch34 = (CheckBox) contentView.findViewById(R.id.checkBox34);
                        CheckBox ch35 = (CheckBox) contentView.findViewById(R.id.checkBox35);
                        CheckBox ch36 = (CheckBox) contentView.findViewById(R.id.checkBox36);
                        CheckBox ch37 = (CheckBox) contentView.findViewById(R.id.checkBox37);
                        CheckBox ch38 = (CheckBox) contentView.findViewById(R.id.checkBox38);
                        CheckBox ch39 = (CheckBox) contentView.findViewById(R.id.checkBox39);
                        CheckBox ch40 = (CheckBox) contentView.findViewById(R.id.checkBox40);
                        CheckBox ch41 = (CheckBox) contentView.findViewById(R.id.checkBox41);
                        CheckBox ch42 = (CheckBox) contentView.findViewById(R.id.checkBox42);
                        CheckBox ch43 = (CheckBox) contentView.findViewById(R.id.checkBox43);
                        CheckBox ch44 = (CheckBox) contentView.findViewById(R.id.checkBox44);
                        CheckBox ch45 = (CheckBox) contentView.findViewById(R.id.checkBox45);
                        CheckBox ch46 = (CheckBox) contentView.findViewById(R.id.checkBox46);
                        CheckBox ch47 = (CheckBox) contentView.findViewById(R.id.checkBox47);
                        CheckBox ch48 = (CheckBox) contentView.findViewById(R.id.checkBox48);
                        CheckBox ch49 = (CheckBox) contentView.findViewById(R.id.checkBox49);


                        select = new boolean[][]{
                                {ch1.isChecked(),ch2.isChecked(),ch3.isChecked(),ch4.isChecked(),ch5.isChecked(),ch6.isChecked(),ch7.isChecked()},
                                {ch8.isChecked(),ch9.isChecked(),ch10.isChecked(),ch11.isChecked(),ch12.isChecked(),ch13.isChecked(),ch14.isChecked()},
                                {ch22.isChecked(),ch23.isChecked(),ch24.isChecked(),ch25.isChecked(),ch26.isChecked(),ch27.isChecked(),ch28.isChecked()},
                                {ch29.isChecked(),ch30.isChecked(),ch31.isChecked(),ch32.isChecked(),ch33.isChecked(),ch34.isChecked(),ch35.isChecked()},
                                {ch36.isChecked(),ch37.isChecked(),ch38.isChecked(),ch39.isChecked(),ch40.isChecked(),ch41.isChecked(),ch42.isChecked()},
                                {ch43.isChecked(),ch44.isChecked(),ch45.isChecked(),ch46.isChecked(),ch47.isChecked(),ch48.isChecked(),ch49.isChecked()}
                        };
                        System.out.println("select is");
                        System.out.println(select);
                        //Log.v("Mainactivity", "GD-Tracing start~");
                        Toast.makeText(getContext(), "feature set~", Toast.LENGTH_SHORT).show();


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

    //画笔颜色设置
    public void PenSet(){

        new MDDialog.Builder(this)
                .setContentView(R.layout.pen_choose)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et1 = (EditText) contentView.findViewById(R.id.pencolor);
                        //pencolor= 2;
                        /*String color  = et1.getText().toString();
                        pencolor= Integer.parseInt(color);
                        System.out.println("pen color is");
                        System.out.println(pencolor);*/

                    }
                })
                .setTitle("Pen Set")
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
                        EditText et1 = (EditText) contentView.findViewById(R.id.pencolor);
                        String color  = et1.getText().toString();

                        if( !color.isEmpty()){

                            myrenderer.pencolorchange(Integer.parseInt(color));;
                            System.out.println("pen color is");
                            System.out.println(Integer.parseInt(color));
                            //Log.v("Mainactivity", "GD-Tracing start~");
                            //Toast.makeText(v.getContext(), "GD-Tracing start~", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "pencolor set~", Toast.LENGTH_SHORT).show();


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

    //opengl中的显示区域
    class MyGLSurfaceView extends GLSurfaceView {
        private float X, Y;
        private double dis_start;
        private boolean isZooming;


        public MyGLSurfaceView(Context context) {
            super(context);

            ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            String v = info.getGlEsVersion(); //判断是否为3.0 ，一般4.4就开始支持3.0版本了。

            Log.v("MainActivity", "GLES-version: " + v);

            //设置一下opengl版本；
            setEGLContextClientVersion(3);

//            myrenderer.setLineDrawed(lineDrawed);

            setRenderer(myrenderer);


            //调用 onPause 的时候保存EGLContext
            setPreserveEGLContextOnPause(true);

            //当发生交互时重新执行渲染， 需要配合requestRender();
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        }


        //触摸屏幕的事件
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouchEvent(MotionEvent motionEvent) {

            //ACTION_DOWN不return true，就无触发后面的各个事件
            if (motionEvent != null) {
                final float normalizedX = toOpenGLCoord(this, motionEvent.getX(), true);
                final float normalizedY = toOpenGLCoord(this, motionEvent.getY(), false);
//
//                final float normalizedX =motionEvent.getX();
//                final float normalizedY =motionEvent.getY();

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        X = normalizedX;
                        Y = normalizedY;
                        if (ifPainting || ifDeletingLine || ifSpliting) {
                            lineDrawed.add(X);
                            lineDrawed.add(Y);
                            lineDrawed.add(-1.0f);
                            myrenderer.setIfPainting(true);
                            requestRender();
                            Log.v("actionPointerDown", "Paintinggggggggggg");
                        }
                        if (ifPoint) {
                            Log.v("actionPointerDown", "Pointinggggggggggg");
                            myrenderer.setMarkerDrawed(X, Y);
                            Log.v("actionPointerDown", "(" + X + "," + Y + ")");
                            requestRender();

                        }
                        if (ifDeletingMarker) {
                            Log.v("actionPointerDown", "DeletingMarker");
                            myrenderer.deleteMarkerDrawed(X, Y);
                            requestRender();
                        }
//                        if (ifDeletingLine){
//                            lineDrawed.add(X);
//                            lineDrawed.add(Y);
//                            lineDrawed.add(-1.0f);
//                            myrenderer.setIfPainting(true);
//                            requestRender();
//                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        isZooming = true;
                        float x1 = toOpenGLCoord(this, motionEvent.getX(1), true);
                        float y1 = toOpenGLCoord(this, motionEvent.getY(1), false);

//                        float x1=motionEvent.getX(1);
//                        float y1=motionEvent.getY(1);
                        dis_start = computeDis(normalizedX, x1, normalizedY, y1);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!ifPainting && !ifDeletingLine && !ifSpliting) {
                            if (isZooming) {
                                float x2 = toOpenGLCoord(this, motionEvent.getX(1), true);
                                float y2 = toOpenGLCoord(this, motionEvent.getY(1), false);

//                            float x2=motionEvent.getX(1);
//                            float y2=motionEvent.getY(1);
                                double dis = computeDis(normalizedX, x2, normalizedY, y2);
                                double scale = dis / dis_start;
                                myrenderer.zoom((float) scale);

                                //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                                requestRender();
                                dis_start = dis;
                            } else {
                                myrenderer.rotate(normalizedX - X, normalizedY - Y, (float) (computeDis(normalizedX, X, normalizedY, Y)));

                                //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                                requestRender();
                                X = normalizedX;
                                Y = normalizedY;
                            }
                        } else {
                            lineDrawed.add(normalizedX);
                            lineDrawed.add(normalizedY);
                            lineDrawed.add(-1.0f);

                            myrenderer.setLineDrawed(lineDrawed);
                            requestRender();

                            invalidate();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        isZooming = false;
                        X = normalizedX;
                        Y = normalizedY;
//                        if (ifPainting){
//                            lineDrawed.clear();
//                            myrenderer.setLineDrawed(lineDrawed);
//                            requestRender();
//
//                            myrenderer.setIfPainting(false);
//                            requestRender();
//
//                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isZooming = false;
                        if (ifPainting) {
                            Vector<Integer> segids = new Vector<>();
                            myrenderer.setIfPainting(false);
//                            myrenderer.addLineDrawed(lineDrawed);

                            int lineType = myrenderer.getLastLineType();
                            if (lineType != 3) {
                                int segid = myrenderer.addLineDrawed(lineDrawed);
//                                    segids.add(segid);
//                            requestRender();

                                myrenderer.addLineDrawed2(lineDrawed);
                                myrenderer.deleteFromNew(segid);
                            }

                            else{
                                myrenderer.addBackgroundLineDrawed(lineDrawed);
                            }
//                            requestRender();

//                            if (myrenderer.deleteFromNew(segid)) {
//                                myrenderer.addLineDrawed2(lineDrawed);
//                                requestRender();
//                            }

//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    int segid = myrenderer.addLineDrawed(lineDrawed);
////                                    segids.add(segid);
//                                    requestRender();
//                                    if (myrenderer.deleteFromNew(segid)) {
//                                        myrenderer.addLineDrawed2(lineDrawed);
//                                        requestRender();
//                                    }
//                                }
//                            }).start();
//                            myrenderer.addLineDrawed2(lineDrawed);
                            lineDrawed.clear();
                            myrenderer.setLineDrawed(lineDrawed);

                            requestRender();
//                            requestRender();
                        }
                        if (ifDeletingLine) {
                            myrenderer.setIfPainting(false);
                            myrenderer.deleteLine1(lineDrawed);
                            lineDrawed.clear();
                            myrenderer.setLineDrawed(lineDrawed);
                            requestRender();
                        }
                        if (ifSpliting) {
                            myrenderer.setIfPainting(false);
                            myrenderer.splitCurve(lineDrawed);
                            lineDrawed.clear();
                            myrenderer.setLineDrawed(lineDrawed);
                            requestRender();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
            return false;
        }


        //坐标系变换
        private float toOpenGLCoord(View view, float value, boolean isWidth) {
            if (isWidth) {
                return (value / (float) view.getWidth()) * 2 - 1;
            } else {
                return -((value / (float) view.getHeight()) * 2 - 1);
            }
        }


        //距离计算
        private double computeDis(float x1, float x2, float y1, float y2) {
            return sqrt(pow((x2 - x1), 2) + pow((y2 - y1), 2));
        }
    }


    /**
     * Toast some information in child thread
     * @param context the activity context
     * @param text the information to show
     */
    public static void ShowToast(Context context, String text) {
        Toast toast = null;

        Looper myLooper = Looper.myLooper();
        if (myLooper == null) {
            Looper.prepare();
            myLooper = Looper.myLooper();
        }

        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.show();
        if ( myLooper != null) {
            Looper.loop();
            myLooper.quit();
        }
    }

}
