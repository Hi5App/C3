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
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.Server_Communication.Remote_Socket;
import com.example.basic.DragFloatActionButton;
import com.example.ImageReader.BigImgReader;
import com.example.basic.FileManager;
import com.example.basic.Image4DSimple;
import com.example.basic.ImageMarker;
import com.example.basic.LocationSimple;
import com.example.basic.NeuronSWC;
import com.example.basic.NeuronTree;
import com.example.basic.SettingFileManager;
import com.example.myapplication__volume.FileReader.AnoReader;
import com.example.myapplication__volume.FileReader.ApoReader;
import com.example.myapplication__volume.FileReader.SwcReader;
import com.example.server_connect.Filesocket_receive;
import com.example.server_connect.Filesocket_send;
import com.example.server_connect.RemoteImg;
import com.feature_calc_func.MorphologyCalculate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.learning.opimageline.Consensus;
import com.learning.opimageline.DetectLine;
import com.learning.pixelclassification.PixelClassification;
import com.learning.randomforest.RandomForest;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.lxj.xpopup.interfaces.SimpleCallback;
import com.tracingfunc.app2.ParaAPP2;
import com.tracingfunc.app2.V3dNeuronAPP2Tracing;
import com.tracingfunc.gd.CurveTracePara;
import com.tracingfunc.gd.V3dNeuronGDTracing;
import com.tracingfunc.gd.V_NeuronSWC;
import com.tracingfunc.gd.V_NeuronSWC_list;
import com.tracingfunc.gsdt.GSDT;
import com.tracingfunc.gsdt.ParaGSDT;

//import org.opencv.android.OpenCVLoader;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import Jama.Matrix;
import cn.carbs.android.library.MDDialog;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;

import static com.example.basic.BitmapRotation.getBitmapDegree;
import static com.example.basic.SettingFileManager.getFilename_Local;
import static com.example.basic.SettingFileManager.getFilename_Remote;
import static com.example.basic.SettingFileManager.getFilename_Remote_Check;
import static com.example.basic.SettingFileManager.getNeuronNumber_Remote;
import static com.example.basic.SettingFileManager.getSelectSource;
import static com.example.basic.SettingFileManager.getUserAccount;
import static com.example.basic.SettingFileManager.getoffset_Local;
import static com.example.basic.SettingFileManager.getoffset_Remote;
import static com.example.basic.SettingFileManager.getoffset_Remote_Check;
import static com.example.basic.SettingFileManager.setSelectSource;
import static com.example.basic.SettingFileManager.setUserAccount;
import static com.example.basic.SettingFileManager.setoffset_Local;
import static com.example.server_connect.RemoteImg.getFilename;
import static com.example.server_connect.RemoteImg.getoffset;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class MainActivity extends AppCompatActivity {
//    private int UNDO_LIMIT = 5;
//    private enum Operate {DRAW, DELETE, SPLIT};
//    private Operate [] process = new Operate[UNDO_LIMIT];

    public static final String File_path = "com.example.myfirstapp.MESSAGE";

    private SensorManager mSensorManager;
    private Timer timer=null;

    private TimerTask timerTask;
    TextView etGyro;
    TextView etMagnetic;
    TextView etLinearAcc;
    TextView etAcc;
    TextView etLight;
    TextView etPressure;
    TextView etProximity;
    TextView etGravity;
    TextView etRotation_vector;
    Button Sensor_Info;
    Button start;
    Button stop;
    float flag=0;
    ArrayList <Float> AccList=new ArrayList<Float>();
    ArrayList <Float> GyrList=new ArrayList<Float>();
    ArrayList <Float> MagList=new ArrayList<Float>();
    ArrayList <Float> AccList2=new ArrayList<Float>();
    ArrayList <Float> LightList=new ArrayList<Float>();
    ArrayList <Float> PreList=new ArrayList<Float>();
    ArrayList <Float> ProList=new ArrayList<Float>();
    ArrayList <Float> GraList=new ArrayList<Float>();
    ArrayList <Float> Rot_Vec_List=new ArrayList<Float>();

    private float AccData[]=new float[3];
    private float GyrData[]=new float[3];
    private float MagData[]=new float[3];
    private float AccData2[]=new float[3];
    private float LightData[]=new float[1];
    private float PreData[]=new float[1];
    private float ProData[]=new float[1];
    private float GraData[]=new float[3];
    private float Rot_Vec_Data[]=new float[3];


    private static MyGLSurfaceView myGLSurfaceView;
    private static MyRenderer myrenderer;
    private static final String DEBUG_TAG = "Gestures";
    private static Context context;
    private long length;
    private InputStream is;
    private String filepath = "";
    private boolean ifDeletingMultiMarker = false;
    private boolean ifChangeMarkerType = false;
    private boolean ifPainting = false;
    private boolean ifPoint = false;
    private boolean ifImport = false;
    private boolean ifAnalyze = false;
    private boolean ifUpload = false;
    //    private boolean ifSaveSwc = false;
    private boolean ifDeletingMarker = false;
    private boolean ifDeletingLine = false;
    private boolean ifSpliting = false;
    private boolean ifChangeLineType = false;
    private boolean ifSwitch = false;
    private boolean select_img = false;
    private boolean ifLoadLocal = false;
    private boolean ifRemote = false;
    private boolean ifDownloadByHttp = false;
    private boolean ifButtonShowed = true;
//    private boolean ifTakePhoto = false;

    private boolean[] temp_mode = new boolean[8];

    private boolean ifAnimation = false;
    private Button buttonUndo;
    private Button Draw;
    private Button Tracing;
    private Button Others;
    private static Button Zoom_in;
    private static Button Zoom_out;
    private static Button Check_Yes;
    private static Button Check_No;
    private static Button Zoom_in_Big;
    private static Button Zoom_out_Big;
    private Button Rotation;
    private ImageButton Rotation_i;
    private ImageButton Hide_i;
    private static ImageButton Undo_i;
    private ImageButton Sync_i;
    private Button Sync;
    private Button Switch;
    private Button Remoteleft;
    private Button Share;
    private ImageButton animation_i;
    private ImageButton draw_i;
    private ImageButton tracing_i;
    private ImageButton classify_i;
    private static TextView filenametext;

//    private ImageButton buttonUndo_i;
    private static ImageButton navigation_left;
    private static ImageButton navigation_right;
    private static ImageButton navigation_up;
    private static ImageButton navigation_down;
    private static ImageButton navigation_location;
    private static ImageButton neuron_list;
    private static Button navigation_front;
    private static Button navigation_back;
    private static Button blue_pen;
    private static Button red_pen;
    private static ImageButton sync_push;
    private static ImageButton sync_pull;

    private static FloatingActionButton Audio_call;
    private static DragFloatActionButton fab;
//    private static DragFloatActionButton fab;

    private FrameLayout.LayoutParams lp_undo_i;
    private FrameLayout.LayoutParams lp_left_i;
    private FrameLayout.LayoutParams lp_right_i;
    private static FrameLayout.LayoutParams lp_up_i;
    private FrameLayout.LayoutParams lp_down_i;
    private FrameLayout.LayoutParams lp_front_i;
    private FrameLayout.LayoutParams lp_back_i;
    private static FrameLayout.LayoutParams lp_nacloc_i;
    private static FrameLayout.LayoutParams lp_sync_push;
    private static FrameLayout.LayoutParams lp_sync_pull;
    private static FrameLayout.LayoutParams lp_neuron_list;
    private static FrameLayout.LayoutParams lp_blue_color;
    private static FrameLayout.LayoutParams lp_red_color;
    private FrameLayout.LayoutParams lp_animation_i;
    private static FrameLayout.LayoutParams lp_undo;

    private Button PixelClassification;
    private boolean[][]select= {{true,true,true,false,false,false,false},
    {true,true,true,false,false,false,false},
    {false,false,false,false,false,false,false},
    {false,false,false,false,false,false,false},
    {false,false,false,false,false,false,false},
    {true,true,true,false,false,false,false}};

    private Button detectLineButton;
    private RandomForest rf = null;


    private static RemoteImg remoteImg;
    @SuppressLint("StaticFieldLeak")
    private static Remote_Socket remote_socket;
    private BigImgReader bigImgReader;


    private static final int PICKFILE_REQUEST_CODE = 100;

    private LinearLayout ll_top;
    private LinearLayout ll_bottom;
    private static LinearLayout ll_file;

    private int measure_count = 0;
    private List<double[]> fl;

    private static boolean isBigData_Remote;
    private static boolean isBigData_Local;
    private ProgressBar progressBar;


    private int eswc_length;
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;

    private static final String LOG_TAG = VoiceChatViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int Toast_Info_static = 5;

    //    private int Paintmode = 0;
    private ArrayList<Float> lineDrawed = new ArrayList<Float>();

    private BroadcastReceiver broadcastReceiver;

    private String currentPhotoPath; //指定一个不会跟其他文件产生冲突的文件名，用于后面相机拍照的图片的保存

    private File showPic;
    private Uri picUri;

    private static BasePopupView popupView;

    private static final int animation_id = 0;
    private int rotation_speed = 36;

    private long exitTime = 0;

    private static String filename = "";

    private static String file_path_temp = "";

    private static boolean DrawMode = true;

    private enum PenColor {
        BLACK, WHITE, RED, BLUE, GREEN, PURPLE, YELLOW
    }


    private static String[] push_info_swc = {"New", "New"};
    private static String[] push_info_apo = {"New", "New"};

    private BasePopupView drawPopupView;


    HashMap<Integer, String> User_Map = new HashMap<Integer, String>();

    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler(){
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.v("filesocket_send: ", "Connect with Server successfully");
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    System.out.println("------ Upload file successfully!!! -------");
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private static Handler puiHandler = new Handler(){
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    popupView.dismiss();
                    break;
                case 2:
                    if (isBigData_Remote || isBigData_Local){
                        navigation_left.setVisibility(View.VISIBLE);
                        navigation_right.setVisibility(View.VISIBLE);
                        navigation_up.setVisibility(View.VISIBLE);
                        navigation_down.setVisibility(View.VISIBLE);
                        navigation_front.setVisibility(View.VISIBLE);
                        navigation_back.setVisibility(View.VISIBLE);
                        navigation_location.setVisibility(View.VISIBLE);

                        Zoom_in_Big.setVisibility(View.VISIBLE);
                        Zoom_out_Big.setVisibility(View.VISIBLE);
                        Zoom_in.setVisibility(View.GONE);
                        Zoom_out.setVisibility(View.GONE);

                        if (isBigData_Remote){
                            if (DrawMode){
                                Check_Yes.setVisibility(View.GONE);
                                Check_No.setVisibility(View.GONE);
                                sync_pull.setVisibility(View.VISIBLE);
                                sync_push.setVisibility(View.VISIBLE);
                                neuron_list.setVisibility(View.VISIBLE);
                                blue_pen.setVisibility(View.VISIBLE);
                                red_pen.setVisibility(View.VISIBLE);
                            }
                            else {
                                Check_Yes.setVisibility(View.VISIBLE);
                                Check_No.setVisibility(View.VISIBLE);
                                sync_pull.setVisibility(View.VISIBLE);
                                sync_push.setVisibility(View.GONE);
                                neuron_list.setVisibility(View.VISIBLE);
                                blue_pen.setVisibility(View.GONE);
                                red_pen.setVisibility(View.GONE);
                            }
                        }

                    }

                    if (isBigData_Remote){
//                        String filename = getFilename(context);
//                        String offset = getoffset(context, filename);
//
//                        String offset_x = offset.split("_")[0];
//                        String offset_y = offset.split("_")[1];
//                        String offset_z = offset.split("_")[2];
//                        Toast.makeText(getContext(),"Current offset: " + "x: " + offset_x + " y: " + offset_y + " z: " + offset_z, Toast.LENGTH_SHORT).show();
                    }

                    if (isBigData_Local){
                        String filename = SettingFileManager.getFilename_Local(context);
                        String offset = SettingFileManager.getoffset_Local(context, filename);

                        String offset_x = offset.split("_")[0];
                        String offset_y = offset.split("_")[1];
                        String offset_z = offset.split("_")[2];
                        Toast.makeText(getContext(),"Current offset: " + "x: " + offset_x + " y: " + offset_y + " z: " + offset_z, Toast.LENGTH_SHORT).show();
                    }

                    break;

                case 3:
                    Toast.makeText(context,"Time out, please try again!",Toast.LENGTH_SHORT).show();
                    break;

                case 4:
                    File file = new File(file_path_temp);
                    String name = file.getName();
                    Log.v("Handler",name);

                    String result = null;
                    if (DrawMode){
                        String source = getSelectSource(context);
                        if (source.equals("Remote Server Aliyun")){
                            result = name.split("_")[1].substring(0,name.split("_")[1].length()-3);
                        }else if(source.equals("Remote Server SEU")){
                            String filename = getFilename(context);
                            String brain_number = getNeuronNumber_Remote(context,filename);
                            result = name.split("RES")[0].split("_")[1] + "_" + brain_number.split("_")[1];
                        }
                    }else {
                        result = name.split("__")[0];
                    }

                    setFilename(result);
                    break;

                case 5:
                    String Toast_msg = msg.getData().getString("Toast_msg");
                    Toast.makeText(getContext(),Toast_msg, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };


    private RtcEngine mRtcEngine; // Tutorial Step 1
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
         *     Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who
         * leaves
         * the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 4
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String userAccount = User_Map.get(uid);
                    onRemoteUserLeft(userAccount, reason);
                }
            });
        }

        /**
         * Occurs when a remote user stops/resumes sending the audio stream.
         * The SDK triggers this callback when the remote user stops or resumes sending the audio stream by calling the muteLocalAudioStream method.
         *
         * @param uid ID of the remote user.
         * @param muted Whether the remote user's audio stream is muted/unmuted:
         *
         *     true: Muted.
         *     false: Unmuted.
         */
        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVoiceMuted(uid, muted);
                }
            });
        }

//        //用户加入了房间
//        @Override
//        public void onUserJoined(final int uid, final int elapsed) {
//            super.onUserJoined(uid, elapsed);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    onIntoRoom(uid, elapsed);
//                }
//            });
//        }

        @Override
        public void onUserInfoUpdated(int uid, UserInfo user) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    User_Map.put(user.uid, user.userAccount);
                    onRemoteUserJoined(user.userAccount);
                }
            });
        }
    };


    /**
     * The onCreate Function
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("------------------");
        System.out.println("onCreate");

        isBigData_Remote = false;
        isBigData_Local  = false;

        popupView = new XPopup.Builder(this)
                .asLoading("Downloading......");

        myrenderer = new MyRenderer();


        Intent intent2 = getIntent();
        String MSG = intent2.getStringExtra(MyRenderer.OUTOFMEM_MESSAGE);

        if (MSG != null)
            Toast.makeText(this, MSG, Toast.LENGTH_SHORT).show();


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myGLSurfaceView = new MyGLSurfaceView(this);
        FrameLayout ll = (FrameLayout) findViewById(R.id.container);
        ll.addView(myGLSurfaceView);

        LinearLayout ll_up = new LinearLayout(this);
        ll_up.setOrientation(LinearLayout.VERTICAL);

        LinearLayout ll_hs_back = new LinearLayout(this);
        ll_hs_back.setOrientation(LinearLayout.HORIZONTAL);
        ll_hs_back.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout ll_space = new LinearLayout(this);
        ll_space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));


        ll_file = new LinearLayout(this);
        FrameLayout.LayoutParams lp_filename = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        ll_file.setLayoutParams(lp_filename);
        ll_file.setBackgroundColor(Color.YELLOW);

        filenametext = new TextView(this);
        filenametext.setText("");
//        filenametext.setBackgroundColor(Color.BLACK);
        filenametext.setTextColor(Color.BLACK);
        ll_file.addView(filenametext);
        ll_file.setVisibility(View.GONE);

        ll_top = new LinearLayout(this);

        ll_bottom = new LinearLayout(this);

        HorizontalScrollView hs_top = new HorizontalScrollView(this);

        ll_up.addView(ll_file);

        ll_hs_back.addView(hs_top, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll_hs_back.addView(ll_space);

        ll_up.addView(ll_hs_back);

        ll.addView(ll_up);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(1080, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
//        this.addContentView(hs_bottom, lp);
        this.addContentView(ll_bottom, lp);
        ll_bottom.setLayoutParams(lp);

        System.out.println("width:" + ll_bottom.getWidth());

        hs_top.addView(ll_top);
//        hs_bottom.addView(ll_bottom);


//        hs_top.addView(filenametext, lp_filename);

        Zoom_in = new Button(this);
        Zoom_in.setText("+");
        Zoom_in_Big = new Button(this);
        Zoom_in_Big.setText("+");

        Zoom_out = new Button(this);
        Zoom_out.setText("-");
        Zoom_out_Big = new Button(this);
        Zoom_out_Big.setText("-");

        FrameLayout.LayoutParams lp_zoom_in = new FrameLayout.LayoutParams(120, 120);
        lp_zoom_in.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_zoom_in.setMargins(0, 0, 20, 290);
        this.addContentView(Zoom_in_Big, lp_zoom_in);

        FrameLayout.LayoutParams lp_zoom_out = new FrameLayout.LayoutParams(120, 120);
        lp_zoom_out.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_zoom_out.setMargins(0, 0, 20, 200);
        this.addContentView(Zoom_out_Big, lp_zoom_out);

        Zoom_in_Big.setVisibility(View.GONE);
        Zoom_out_Big.setVisibility(View.GONE);



        FrameLayout.LayoutParams lp_zoom_in_no = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_in_no.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        this.addContentView(Zoom_in, lp_zoom_in_no);

        FrameLayout.LayoutParams lp_zoom_out_no = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_out_no.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        this.addContentView(Zoom_out, lp_zoom_out_no);




        Zoom_in.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Image4DSimple img = myrenderer.getImg();
                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }
                myrenderer.zoom_in();
                myGLSurfaceView.requestRender();
            }
        });


        Zoom_out.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Image4DSimple img = myrenderer.getImg();
                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }
                myrenderer.zoom_out();
                myGLSurfaceView.requestRender();
            }
        });

        Zoom_in_Big.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Image4DSimple img = myrenderer.getImg();
                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isBigData_Remote && DrawMode){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            remote_socket.Zoom_in();
                        }
                    }).start();
                }else {
                    myrenderer.zoom_in();
                    myGLSurfaceView.requestRender();
                }

            }
        });


        Zoom_out_Big.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Image4DSimple img = myrenderer.getImg();
                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isBigData_Remote && DrawMode){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            remote_socket.Zoom_out();
                        }
                    }).start();
                }else {
                    myrenderer.zoom_out();
                    myGLSurfaceView.requestRender();
                }

            }
        });



        Check_Yes = new Button(this);
        Check_Yes.setText("Y");

        Check_No = new Button(this);
        Check_No.setText("N");

        FrameLayout.LayoutParams lp_check_yes = new FrameLayout.LayoutParams(120, 120);
        lp_check_yes.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_check_yes.setMargins(0, 0, 20, 500);
        this.addContentView(Check_Yes, lp_check_yes);

        FrameLayout.LayoutParams lp_check_no = new FrameLayout.LayoutParams(120, 120);
        lp_check_no.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_check_no.setMargins(0, 0, 20, 410);
        this.addContentView(Check_No, lp_check_no);

        Check_Yes.setVisibility(View.GONE);
        Check_No.setVisibility(View.GONE);


        Check_Yes.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        remote_socket.Check_Yes(false);
                        Toast_in_Thread("Check Yes Successfully");
                    }
                }).start();
            }
        });


        Check_No.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        remote_socket.Check_No(false);
                        Toast_in_Thread("Check No Successfully");
                    }
                }).start();
            }
        });


        FrameLayout.LayoutParams lp_draw_i = new FrameLayout.LayoutParams(230, 160);

        draw_i = new ImageButton(this);
        draw_i.setImageResource(R.drawable.ic_draw_main);
        ll_top.addView(draw_i,lp_draw_i);

        draw_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myrenderer.getIfFileLoaded()){
                    Toast.makeText(context, "Please load a File First", Toast.LENGTH_SHORT).show();
                    return;
                }
                Draw_list(v);
            }
        });


        FrameLayout.LayoutParams lp_tracing_i = new FrameLayout.LayoutParams(230, 160);


        tracing_i=new ImageButton(this);
        tracing_i.setImageResource(R.drawable.ic_neuron);
        ll_top.addView(tracing_i,lp_tracing_i);

        tracing_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tracing(v);
            }
        });

        /*Tracing = new Button(this);
        Tracing.setText("Trace");
        ll_top.addView(Tracing);

        Tracing.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Tracing(v);
            }
        });*/

        FrameLayout.LayoutParams lp_classify_i = new FrameLayout.LayoutParams(230, 160);

        classify_i=new ImageButton(this);
        classify_i.setImageResource(R.drawable.ic_classify_mid);
        ll_top.addView(classify_i,lp_classify_i);

        classify_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                PixelClassification(v);
            }
        });

//        detectLineButton = new Button(this);
//        detectLineButton.setText("DetectLine");
//        ll_top.addView(detectLineButton);
//        detectLineButton.setOnClickListener(new Button.OnClickListener(){
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View v) {
//                LineDetect(v);
//            }
//        });

        //像素分类总button
        /*PixelClassification = new Button(this);
        PixelClassification.setText("Classify");
        ll_top.addView(PixelClassification);

        PixelClassification.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                PixelClassification(v);
            }
        });*/


//        Rotation = new Button(this);
//        Rotation.setText("Rotate");
//        Rotation.getSolidColor();

        FrameLayout.LayoutParams lp_rotation = new FrameLayout.LayoutParams(120, 120);
        lp_rotation.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_rotation.setMargins(0, 0, 20, 20);

        Rotation_i = new ImageButton(this);
        Rotation_i.setImageResource(R.drawable.ic_3d_rotation_red_24dp);
        Rotation_i.setBackgroundResource(R.drawable.circle_normal);

        this.addContentView(Rotation_i, lp_rotation);

        final boolean[] b_rotate = {true};

        Rotation_i.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Rotation();
            }
        });

        FrameLayout.LayoutParams lp_hide = new FrameLayout.LayoutParams(120, 120);
        lp_hide.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_hide.setMargins(0, 0, 160, 20);
//        @SuppressLint("ResourceType") XmlPullParser parser = MainActivity.this.getResources().getXml(R.layout.activity_main);
//        AttributeSet attributes = Xml.asAttributeSet(parser);
//        MyRockerView rockerView = new MyRockerView(context, attributes);

        Hide_i = new ImageButton(this);
        Hide_i.setImageResource(R.drawable.ic_not_hide);
        Hide_i.setBackgroundResource(R.drawable.circle_normal);

        this.addContentView(Hide_i, lp_hide);

        Hide_i.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!myrenderer.getIfFileLoaded()){
                    Toast.makeText(context, "Please load a File First", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (myrenderer.getIfShowSWC()){
                    myrenderer.setIfShowSWC(false);
                    myGLSurfaceView.requestRender();
                    Hide_i.setImageResource(R.drawable.ic_hide);
                } else {
                    myrenderer.setIfShowSWC(true);
                    myGLSurfaceView.requestRender();
                    Hide_i.setImageResource(R.drawable.ic_not_hide);
                }
            }
        });

        lp_undo = new FrameLayout.LayoutParams(120, 120);

//        lp_undo.gravity = Gravity.RIGHT | Gravity.TOP;
        lp_undo.setMargins(0, 20, 20, 0);

        Undo_i = new ImageButton(this);
        Undo_i.setImageResource(R.drawable.ic_undo);
        Undo_i.setBackgroundResource(R.drawable.circle_normal);
        ll_hs_back.addView(Undo_i, lp_undo);
//        this.addContentView(Undo_i, lp_undo);

        Undo_i.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                boolean undoSuccess = myrenderer.undo2();
                if (!undoSuccess) {
                    Toast.makeText(context, "nothing to undo", Toast.LENGTH_SHORT).show();
                }
                myGLSurfaceView.requestRender();
            }
        });


        FrameLayout.LayoutParams lp_downsample = new FrameLayout.LayoutParams(120, 120);


        Switch = new Button(this);
        Switch.setText("Pause");

        Switch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Switch();
            }
        });


        lp_animation_i = new FrameLayout.LayoutParams(230, 160);
        animation_i = new ImageButton(this);
        animation_i.setImageResource(R.drawable.ic_animation);
        animation_i.setId(animation_id);

        animation_i.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Animation(v);
            }
        });



        lp_undo_i = new FrameLayout.LayoutParams(230, 160);

//        buttonUndo_i=new ImageButton(this);
//        buttonUndo_i.setImageResource(R.drawable.ic_undo_black_24dp);
//        buttonUndo_i.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                boolean undoSuccess = myrenderer.undo2();
//                if (!undoSuccess) {
//                    Toast.makeText(context, "nothing to undo", Toast.LENGTH_SHORT).show();
//                }
//                myGLSurfaceView.requestRender();
//            }
//        });


        lp_left_i = new FrameLayout.LayoutParams(100, 150);
        lp_left_i.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;

        navigation_left = new ImageButton(this);
        navigation_left.setImageResource(R.drawable.ic_chevron_left_black_24dp);

        navigation_left.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Block_navigate("Left");
            }
        });



        lp_right_i = new FrameLayout.LayoutParams(100, 150);
        lp_right_i.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        navigation_right = new ImageButton(this);
        navigation_right.setImageResource(R.drawable.ic_chevron_right_black_24dp);

        navigation_right.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Block_navigate("Right");
            }
        });




        lp_up_i = new FrameLayout.LayoutParams(150, 100);
        lp_up_i.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        lp_up_i.setMargins(0, 310, 0, 0);


        navigation_up = new ImageButton(this);
        navigation_up.setImageResource(R.drawable.ic_expand_less_black_24dp);

        navigation_up.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Block_navigate("Top");
            }
        });




        lp_down_i = new FrameLayout.LayoutParams(150, 100);
        lp_down_i.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp_down_i.setMargins(0, 0, 0, 0);

        navigation_down = new ImageButton(this);
        navigation_down.setImageResource(R.drawable.ic_expand_more_black_24dp);

        navigation_down.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Block_navigate("Bottom");
            }
        });


        lp_front_i = new FrameLayout.LayoutParams(120, 120);
        lp_front_i.gravity = Gravity.LEFT | Gravity.BOTTOM;
        lp_front_i.setMargins(20, 0, 0, 290);

        navigation_front = new Button(this);
        navigation_front.setText("F");
        navigation_front.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Block_navigate("Front");
            }
        });


        lp_back_i = new FrameLayout.LayoutParams(120, 120);
        lp_back_i.gravity = Gravity.LEFT | Gravity.BOTTOM;
        lp_back_i.setMargins(20, 0, 0, 200);

        navigation_back = new Button(this);
        navigation_back.setText("B");
        navigation_back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Block_navigate("Back");
            }
        });


        lp_nacloc_i = new FrameLayout.LayoutParams(90, 90);
        lp_nacloc_i.gravity = Gravity.TOP | Gravity.LEFT;
        lp_nacloc_i.setMargins(20, 350, 0, 0);

        navigation_location = new ImageButton(this);
        navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
        navigation_location.setBackgroundResource(R.drawable.circle_normal);
        navigation_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Set_Nav_Mode();
            }
        });


        lp_blue_color = new FrameLayout.LayoutParams(120, 120);
        lp_blue_color.gravity = Gravity.TOP | Gravity.LEFT;
        lp_blue_color.setMargins(20, 490, 0, 0);


        blue_pen = new Button(this);
        blue_pen.setText("B");
        blue_pen.setTextColor(Color.BLUE);

        blue_pen.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                myrenderer.pencolorchange(PenColor.valueOf("BLUE").ordinal());
            }
        });



        lp_red_color = new FrameLayout.LayoutParams(120, 120);
        lp_red_color.gravity = Gravity.TOP | Gravity.LEFT;
        lp_red_color.setMargins(20, 580, 0, 0);

        red_pen = new Button(this);
        red_pen.setText("R");
        red_pen.setTextColor(Color.RED);

        red_pen.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                myrenderer.pencolorchange(PenColor.valueOf("RED").ordinal());
            }
        });



        lp_sync_push = new FrameLayout.LayoutParams(115, 115);
        lp_sync_push.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_sync_push.setMargins(0, 350, 20, 0);

        sync_push = new ImageButton(this);
        sync_push.setImageResource(R.drawable.ic_cloud_upload_black_24dp);
        sync_push.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                PushSWC_Block_Manual();

                //  for apo sync
//                PushAPO_Block_Manual();
            }
        });

        lp_sync_pull = new FrameLayout.LayoutParams(115, 115);
        lp_sync_pull.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_sync_pull.setMargins(0, 440, 20, 0);

        sync_pull = new ImageButton(this);
        sync_pull.setImageResource(R.drawable.ic_cloud_download_black_24dp);
        sync_pull.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (DrawMode){
                    PullSwc_block_Manual(DrawMode);

                    //  for apo sync
//                    PullApo_block_Manual();
                }else {
                    remote_socket.PullCheckResult();
                }
            }
        });


        lp_neuron_list = new FrameLayout.LayoutParams(115, 115);
        lp_neuron_list.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_neuron_list.setMargins(0, 580, 20, 0);

        neuron_list = new ImageButton(this);
        neuron_list.setImageResource(R.drawable.ic_assignment_black_24dp);
        neuron_list.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DrawMode){
                    push_info_swc = SaveSWC_Block_Auto();
                    remote_socket.Select_Neuron_Fast();
                }else {
                    remote_socket.Select_Arbor_Fast();
                }

            }
        });


//        Audio_call = new FloatingActionButton(this);
//        Audio_call.setImageResource(R.drawable.btn_end_call);
//        Audio_call.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                Set_Nav_Mode();
//            }
//        });


        fab = findViewById(R.id.img_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fab.setVisibility(View.GONE);
                leaveChannel();
                RtcEngine.destroy();
                mRtcEngine = null;

                System.out.println("---- click the button ----");
            }
        });

        fab.setVisibility(View.GONE);


        this.addContentView(navigation_left, lp_left_i);
        this.addContentView(navigation_right, lp_right_i);
        this.addContentView(navigation_up, lp_up_i);
        this.addContentView(navigation_down, lp_down_i);
        this.addContentView(navigation_front, lp_front_i);
        this.addContentView(navigation_back, lp_back_i);
        this.addContentView(navigation_location, lp_nacloc_i);

        this.addContentView(sync_pull, lp_sync_pull);
        this.addContentView(sync_push, lp_sync_push);
        this.addContentView(neuron_list, lp_neuron_list);
        this.addContentView(red_pen, lp_red_color);
        this.addContentView(blue_pen, lp_blue_color);

        navigation_left.setVisibility(View.GONE);
        navigation_right.setVisibility(View.GONE);
        navigation_up.setVisibility(View.GONE);
        navigation_down.setVisibility(View.GONE);
        navigation_front.setVisibility(View.GONE);
        navigation_back.setVisibility(View.GONE);
        navigation_location.setVisibility(View.GONE);

        sync_pull.setVisibility(View.GONE);
        sync_push.setVisibility(View.GONE);
        neuron_list.setVisibility(View.GONE);
        red_pen.setVisibility(View.GONE);
        blue_pen.setVisibility(View.GONE);


        SettingFileManager settingFileManager = new SettingFileManager();
        String DownSampleMode = settingFileManager.getDownSampleMode(this);
        if (DownSampleMode.equals("DownSampleYes")){
            myrenderer.setIfNeedDownSample(true);
        }else if (DownSampleMode.equals("DownSampleNo")){
            myrenderer.setIfNeedDownSample(false);
        }

        // "CheckMode"  for check, "DrawMode" for draw
        String BigDataMode = settingFileManager.getBigDataMode(this);
        if (BigDataMode.equals("Draw Mode")){
            DrawMode = true;
        }else if (BigDataMode.equals("Check Mode")){
            DrawMode = false;
        }

        // Set the permission for user
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }


        myGLSurfaceView.requestRender();
        remoteImg = new RemoteImg();
        remote_socket = new Remote_Socket(this);
        bigImgReader = new BigImgReader();

        context = getApplicationContext();

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
        params.gravity = Gravity.CENTER;
        this.addContentView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        String dir_str = "/storage/emulated/0/C3";
        File dir = new File(dir_str);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String dir_str_server = "/storage/emulated/0/C3/Server";
        File dir_server = new File(dir_str_server);
        if (!dir_server.exists()) {
            dir_server.mkdirs();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            Toast.makeText(getContext(), "横屏", Toast.LENGTH_LONG).show();
        }else{
//            Toast.makeText(getContext(), "竖屏", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * on top bar menu created, link res/menu/main.xml
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * call the corresponding function when button in top bar clicked
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.file:
                File_icon();
                return true;
            case R.id.share:
                Share_icon();
                return true;
            case R.id.more:
                More_icon();
                return true;
//            case R.id.experiment:
//                Experiment_icon();
//                return true;
            case R.id.view:
                if (ifButtonShowed){
                    hideButtons();

                    item.setIcon(R.drawable.ic_visibility_off_black_24dp);

                } else {
                    showButtons();

                    item.setIcon(R.drawable.ic_visibility_black_24dp);
                }
                return true;
            default:
                return true;
//                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * popup a menu when share button is clicked, include screenshot share, upload swc and download swc
     */
    public void Share_icon(){

        // delete upload and download:    , "Upload SWC", "Download SWC"
        new XPopup.Builder(this)
                .asCenterList("Share & Cloud server", new String[]{"Screenshot share"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Screenshot share":
                                        ShareScreenShot();
                                        break;

                                    case "Upload SWC":
                                        UploadSWC();
                                        break;

                                    case "Download SWC":
                                        DownloadSWC();
                                        break;

                                    default:
                                        Toast.makeText(getContext(), "Default in share", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                .show();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * pop up a menu when button more is clicked, include analyze swc file, sensor information, downsample mode, animate and version
     */
    public void More_icon(){
        SettingFileManager settingFileManager = new SettingFileManager();
        String DownSample_mode;
        String BigData_mode = "";

        if (myrenderer.getIfNeedDownSample()){
            DownSample_mode = "Normal Rotate";
        } else {
            DownSample_mode = "Downsample When Rotate";
        }

        if (DrawMode){
            BigData_mode = "Switch to Check Mode";
        }else{
            BigData_mode = "Switch to Draw Mode";
        }


        new XPopup.Builder(this)
//        .maxWidth(400)
//        .maxHeight(1350)
                .asCenterList("More Functions...", new String[]{"Analyze SWC", "Sensor Information", "VoiceChat - 1 to 1", "Animate", "Settings","About", "Help"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Analyze SWC":
                                        Analyse();
                                        break;

                                    case "Animate":
                                        if (myrenderer.getImg() != null){
                                            ifPainting = false;
                                            ifPoint = false;
                                            ifDeletingMarker = false;
                                            ifDeletingLine = false;
                                            ifSpliting = false;
                                            ifChangeLineType = false;
                                            SetAnimation();
                                        }else {
                                            Toast.makeText(context,"Please Load a Img First !!!", Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                    case "VoiceChat - 1 to 1":
                                        PopUp_Chat(MainActivity.this);
                                        break;

                                    case "Sensor Information":
                                        SensorInfo();
                                        break;

//                                    case "Corner Detection":
//                                        myrenderer.corner_detection();
//                                        myGLSurfaceView.requestRender();
//                                        break;

                                    case "Downsample When Rotate":
                                        myrenderer.setIfNeedDownSample(true);
                                        settingFileManager.setDownSampleMode("DownSampleYes", getContext());
                                        break;

                                    case "Normal Rotate":
                                        myrenderer.setIfNeedDownSample(false);
                                        settingFileManager.setDownSampleMode("DownSampleNo", getContext());
                                        break;

                                    case "Switch to Check Mode":
                                        DrawMode = false;
                                        settingFileManager.setBigDataMode("Check Mode", getContext());
                                        break;

                                    case "Switch to Draw Mode":
                                        DrawMode = true;
                                        settingFileManager.setBigDataMode("Draw Mode", getContext());
                                        break;

                                    case "Game":
                                        System.out.println("Game Start!!!!!!!");
//                                        float [] vertexPoints = new float[]{
//                                                // Back face
//                                                0.0f,  0.0f,  0.0f,
//                                                0.0f,  1, 0.0f,
//                                                1, 1, 0.0f,
//                                                1, 0.0f,  0.0f,
//
//                                                // Top face
//                                                0.0f, 1,  0.0f,
//                                                0.0f, 1,  1,
//                                                1, 1, 1,
//                                                1, 1, 0.0f,
//
//                                                // Right face
//                                                1, 0.0f,  0.0f,
//                                                1, 1, 0.0f,
//                                                1, 1, 1,
//                                                1, 0.0f,  1,
//
//                                                //
//                                                0.0f, 0.0f, 0.0f,
//                                                0.0f, 1, 1,
//                                                1, 0, 1,
//                                        };

//                                        float [] startPoint = new float[]{
//                                                0.5f, 0.5f, 0.5f
//                                        };
//
//                                        float [] dir = new float[]{
//                                                1, 1, -1
//                                        };
//
//                                        ArrayList<Float> tangent = myrenderer.tangentPlane(startPoint[0], startPoint[1], startPoint[2], dir[0], dir[1], dir[2], 0, 1);
//
//                                        System.out.println("TangentPlane:::::");
//                                        System.out.println(tangent.size());
//
//                                        float [] vertexPoints = new float[tangent.size()];
//                                        for (int i = 0; i < tangent.size(); i++){
//
//                                            vertexPoints[i] = tangent.get(i);
//                                            System.out.print(vertexPoints[i]);
//                                            System.out.print(" ");
//                                            if (i % 3 == 2){
//                                                System.out.print("\n");
//                                            }
//                                        }
//
//                                        boolean gameSucceed = myrenderer.driveMode(vertexPoints, dir);
//                                        if (!gameSucceed){
//                                            Toast.makeText(context, "wrong vertex to draw", Toast.LENGTH_SHORT);
//                                        } else {
//                                            myGLSurfaceView.requestRender();
//                                        }
                                        gameStart();
                                        break;

                                    case "Settings":
                                        setSettings();
                                        break;

                                    case "About":
                                        About();;
                                        break;

                                    case "Help":
                                        try{
                                            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                                            startActivity(intent);
                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        break;

                                    default:
//                                        Toast.makeText(context, "Default in analysis", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getContext(), "Default in More", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                .show();

    }

    /**
     * called when request permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_PERMISSION_CODE: {
                for (int i = 0; i < permissions.length; i++) {
                    Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
                }
                break;
            }
//            case PERMISSION_REQ_ID_RECORD_AUDIO: {
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                    initAgoraEngineAndJoinChannel();
//                } else {
//                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
//                    finish();
//                }
//                break;
//            }


        }

//        if (requestCode == REQUEST_PERMISSION_CODE) {
//            for (int i = 0; i < permissions.length; i++) {
//                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
//            }
//        }
//        if (requestCode == REQUEST_TAKE_PHOTO) {
//            for (int i = 0; i < permissions.length; i++) {
//                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
//            }
//        }
    }


    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(LOG_TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }


    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Context context = this;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            System.out.println("BBBBB");
//            Bundle extras = data.getExtras();
//
//            Uri imageUri = (Uri) extras.get(MediaStore.EXTRA_OUTPUT);
//            Bitmap imageBitmap = (Bitmap) extras.get("data");  //如果直接保存imageBitmap，保存成txt? 还是直接保存图片？
//            savePhoto(imageBitmap);
//            ImageView imageView = new ImageView(this);
//            imageView.setImageBitmap(imageBitmap);
////            ifTakePhoto = false;
//            return;
//            Bitmap bitmap= BitmapFactory.decodeFile(String.valueOf(showPic));
//            Uri imageUri = data.getData();
//            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
//            Cursor cur = managedQuery(picUri, orientationColumn, null, null, null);
//            int orientation = -1;
//            if (cur != null && cur.moveToFirst()) {
//                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
//            }
//            CameraManager mCameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);;
//            try {
//                CameraCharacteristics mCameraCharacteristics = mCameraManager.getCameraCharacteristics(Integer.toString(CameraCharacteristics.LENS_FACING_FRONT));
//                float [] r = mCameraCharacteristics.get(CameraCharacteristics.LENS_POSE_ROTATION);
//                System.out.println(r);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
//            android.hardware.Camera.getCameraInfo(0, info);
//            System.out.println("OOOOOOOOOOO");
//            System.out.println(info.orientation);
//            int degree = getBitmapDegree(showPic.getAbsolutePath());
//            System.out.println(degree);
            myrenderer.SetPath(showPic.getAbsolutePath());
            //System.out.println(showPic.getAbsolutePath());
            myGLSurfaceView.requestRender();
            return;
        }

        if (resultCode == RESULT_OK) {
            String folderPath = data.getDataString();
            Uri uri = data.getData();

            String filePath = uri.toString();

            String filePath_getPath = uri.getPath();

            Log.v("MainActivity", filePath);
            Log.v("uri.getPath()", filePath_getPath);
            Log.v("Uri_Scheme:", uri.getScheme());

            try {
                Log.v("MainActivity", "onActivityResult");
                if (ifImport) {

                    FileManager fileManager = new FileManager();
                    String fileName = fileManager.getFileName(uri);
                    String filetype = fileName.substring(fileName.lastIndexOf(".")).toUpperCase();
                    System.out.println("FileType: " + filetype + ", FileName: " + fileName);

                    if (myrenderer.getIfFileLoaded()) {
                        System.out.println("------ load local file ------");
                        switch (filetype) {
                            case ".APO":
                                Log.v("MainActivity", uri.toString());
                                ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
                                ApoReader apoReader = new ApoReader();
                                apo = apoReader.read(uri);
                                if (apo == null){
                                    Toast.makeText(this,"Make sure the .apo file is right",Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                myrenderer.importApo(apo);
                                break;
                            case ".SWC":
                            case ".ESWC":
                                Log.v("onActivityResult", ".eswc");
                                NeuronTree nt = NeuronTree.readSWC_file(uri);
                                myrenderer.importNeuronTree(nt);
                                break;

                            case ".ANO":
                                ArrayList<ArrayList<Float>> ano_apo = new ArrayList<ArrayList<Float>>();
                                AnoReader anoReader = new AnoReader();
                                ApoReader apoReader_1 = new ApoReader();
                                anoReader.read(uri);

                                String swc_path = anoReader.getSwc_Path();
                                String apo_path = anoReader.getApo_Path();

                                NeuronTree nt2 = NeuronTree.readSWC_file(swc_path);
                                ano_apo = apoReader_1.read(apo_path);
                                myrenderer.importNeuronTree(nt2);
                                myrenderer.importApo(ano_apo);
                                break;

                            default:
                                Toast.makeText(this, "do not support this file", Toast.LENGTH_SHORT).show();

                        }
                    }

                    else {
                        System.out.println("-------- open --------");
                        myrenderer.SetSWCPath(filePath);
                        ifLoadLocal = false;
                        if (isBigData_Remote || isBigData_Local){
                            if (isBigData_Remote){
                                if (DrawMode){
                                    sync_push.setVisibility(View.GONE);
                                    sync_pull.setVisibility(View.GONE);
                                    neuron_list.setVisibility(View.GONE);
                                    blue_pen.setVisibility(View.GONE);
                                    red_pen.setVisibility(View.GONE);
                                }
                                else{
                                    Check_Yes.setVisibility(View.GONE);
                                    Check_No.setVisibility(View.GONE);
                                    sync_pull.setVisibility(View.GONE);
                                    neuron_list.setVisibility(View.GONE);
                                }
                            }
                            isBigData_Remote = false;
                            isBigData_Local  = false;
                            try {

                                Zoom_in_Big.setVisibility(View.GONE);
                                Zoom_out_Big.setVisibility(View.GONE);
                                navigation_left.setVisibility(View.GONE);
                                navigation_right.setVisibility(View.GONE);
                                navigation_up.setVisibility(View.GONE);
                                navigation_down.setVisibility(View.GONE);
                                navigation_front.setVisibility(View.GONE);
                                navigation_back.setVisibility(View.GONE);
                                navigation_location.setVisibility(View.GONE);

                                Zoom_in.setVisibility(View.VISIBLE);
                                Zoom_out.setVisibility(View.VISIBLE);

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                    ifImport = false;
                }


                if (ifAnalyze) {
                    MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
                    List features = morphologyCalculate.Calculate(uri, false);

//                    fl = new ArrayList<double[]>(features);
//                    if (features.size() != 0) displayResult(features);
//                    else Toast.makeText(getContext(), "the file is empty", Toast.LENGTH_SHORT).show();

                    if (features != null) {
                        fl = new ArrayList<double[]>(features);
                        displayResult(features);
                    }
                }


                if (ifUpload) {

                    ParcelFileDescriptor parcelFileDescriptor =
                            getContentResolver().openFileDescriptor(uri, "r");
                    InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
                    long length = (int) parcelFileDescriptor.getStatSize();

                    FileManager fileManager = new FileManager();
                    String filename = fileManager.getFileName(uri);
                    SendSwc("39.100.35.131", this, is, length, filename);
                }


                if (ifLoadLocal) {
                    System.out.println("load local");
                    myrenderer.SetPath(filePath);
                    System.out.println(filePath);

                    if (isBigData_Remote || isBigData_Local){
                        if (isBigData_Remote){
                            if (DrawMode){
                                sync_push.setVisibility(View.GONE);
                                sync_pull.setVisibility(View.GONE);
                                neuron_list.setVisibility(View.GONE);
                                blue_pen.setVisibility(View.GONE);
                                red_pen.setVisibility(View.GONE);
                            }
                            else{
                                Check_Yes.setVisibility(View.GONE);
                                Check_No.setVisibility(View.GONE);
                                sync_pull.setVisibility(View.GONE);
                                neuron_list.setVisibility(View.GONE);
                            }
                        }
                        isBigData_Remote = false;
                        isBigData_Local  = false;

                        try {

                            Zoom_in_Big.setVisibility(View.GONE);
                            Zoom_out_Big.setVisibility(View.GONE);
                            navigation_left.setVisibility(View.GONE);
                            navigation_right.setVisibility(View.GONE);
                            navigation_up.setVisibility(View.GONE);
                            navigation_down.setVisibility(View.GONE);
                            navigation_front.setVisibility(View.GONE);
                            navigation_back.setVisibility(View.GONE);
                            navigation_location.setVisibility(View.GONE);

                            Zoom_in.setVisibility(View.VISIBLE);
                            Zoom_out.setVisibility(View.VISIBLE);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    String [] temp = filePath.split("/");
                    String [] temp2 = temp[temp.length - 1].split("%2F");
                    String s = temp2[temp2.length - 1];
                    String filename = FileManager.getFileName(uri);

                    setFilename(filename);


                }

                if (ifDownloadByHttp) {
                    myrenderer.SetPath(filePath);
                    ifDownloadByHttp = false;
                }

//                if (ifTakePhoto){
//                    System.out.println("BBBBB");
//                    Bundle extras = data.getExtras();
//                    Bitmap imageBitmap = (Bitmap) extras.get("data");  //如果直接保存imageBitmap，保存成txt? 还是直接保存图片？
//                    ImageView imageView = new ImageView(this);
//                    imageView.setImageBitmap(imageBitmap);
//                    ifTakePhoto = false;
////                    return;
//                }


            } catch (OutOfMemoryError e) {
                Toast.makeText(this, " Fail to load file  ", Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", "111222");
                Log.v("Exception", e.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param ip
     * @param context
     * @param is
     * @param length
     * @param filename
     */
    private void SendSwc(String ip, Context context, InputStream is, long length, String filename) {
        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                Log.v("SendSwc", "here we are");

                try {
                    remoteImg.ip = ip;
                    Log.v("SendSwc", "connext socket");
                    remoteImg.ImgSocket = new Socket(ip, Integer.parseInt("9000"));
                    remoteImg.ImgReader = new BufferedReader(new InputStreamReader(remoteImg.ImgSocket.getInputStream(), "UTF-8"));
                    remoteImg.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(remoteImg.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));

                    if (remoteImg.ImgSocket.isConnected()) {

                        String content_id = remoteImg.ImgReader.readLine();
                        remoteImg.id = Integer.parseInt(content_id.split(":")[0]);
                        Log.v("ConnectServer", content_id);

                        remoteImg.isSocketSet = true;
                        Toast.makeText(getContext(), "Start to upload!!!", Toast.LENGTH_SHORT).show();
                        if (!remoteImg.isOutputShutdown()) {
                            Log.v("SendSwc: ", "Connect with Server successfully");
                            remoteImg.ImgPWriter.println("connect for android client" + ":import.");
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

                                        if (filesocket_send.filesocket.isConnected()) {

                                            filesocket_send.mPWriter.println(remoteImg.id + ":manage handle.");
                                            filesocket_send.mPWriter.flush();

                                            Context[] contexts = new Context[1];
                                            contexts[0] = context;

                                            Log.v("filesocket_send: ", "Connect with Server successfully");

                                            filesocket_send.sendImg(filename, is, length, context);
//                                            Looper.loop();

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, 1000);  //延迟10秒执行


                            Looper.loop();

                        }


                    } else {
                        Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        };
        thread.start();

    }


    /**
     *
     * @param ip ip of cloud server
     * @param context context of current activity
     * @param is inputstream of file
     * @param length length of file
     * @param filename name of swc file
     */
    private void PushSwc(String ip, Context context, InputStream is, long length, String filename) {
        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                Log.v("SendSwc", "here we are");

                remoteImg.disconnectFromHost();

                try {
                    remoteImg.ip = ip;
//                    if (!remoteImg.isSocketSet) {
                        Log.v("SendSwc", "connext socket");
                        remoteImg.ImgSocket = new Socket(ip, Integer.parseInt("9000"));
                        remoteImg.ImgReader = new BufferedReader(new InputStreamReader(remoteImg.ImgSocket.getInputStream(), "UTF-8"));
                        remoteImg.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(remoteImg.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));
//                    }


                    if (remoteImg.ImgSocket.isConnected()) {

                        remoteImg.isSocketSet = true;
                        Toast.makeText(getContext(), "Start to upload!!!", Toast.LENGTH_SHORT).show();
                        if (!remoteImg.isOutputShutdown()) {
                            Log.v("SendSwc: ", "Connect with Server successfully");
                            remoteImg.ImgPWriter.println("connect for android client" + ":import.");
                            remoteImg.ImgPWriter.flush();

//                            String content = remoteImg.ImgReader.readLine();

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

//                            System.out.println(content);
//                            if (content.contains(":import port.")){
//
//                                try {
//                                    Log.v("SendSwc: ", "Start to connect filesend_server");
//
//                                    Filesocket_send filesocket_send = new Filesocket_send();
//                                    filesocket_send.filesocket = new Socket(ip, 9001);
//                                    filesocket_send.mReader = new BufferedReader(new InputStreamReader(filesocket_send.filesocket.getInputStream(), "UTF-8"));
//                                    filesocket_send.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_send.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
//
//
//                                    Log.v("before filesocket_send:", "Connect with Server successfully");
//
//                                    if (filesocket_send.filesocket.isConnected()) {
//
//                                        Context[] contexts = new Context[1];
//                                        contexts[0] = context;
//
//                                        Log.v("filesocket_send: ", "Connect with Server successfully");
//
//                                        filesocket_send.sendImg(filename, is, length, context);
//
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
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

                                        if (filesocket_send.filesocket.isConnected()) {

                                            Context[] contexts = new Context[1];
                                            contexts[0] = context;

                                            Log.v("filesocket_send: ", "Connect with Server successfully");
                                            filesocket_send.sendImg_test(filename, is, length, context);

//                                            if (filesocket_send.sendImg_test(filename, is, length, context, myrenderer)){
//
//                                                Message msg = new Message();
//                                                msg.what = 1;
//                                                uiHandler.sendMessage(msg);
//
                                            Log.v("filesocket_send: ", "Connect with Server successfully");
                                            System.out.println("------ Upload file successfully!!! -------");
////                                                NeuronTree nt = NeuronTree.readSWC_file("/storage/emulated/0/Download/" + filename);
////                                                myrenderer.importNeuronTree(nt);
////                                                myGLSurfaceView.requestRender();
////                                                Toast.makeText(context, "Upload file successfully!!!", Toast.LENGTH_SHORT).show();
//                                            }



//                                            Looper.loop();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, 1000);  //延迟1秒执行



                        }


                    } else {
                        Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        };
        thread.start();

    }


    public void DownloadSwc(String ip, Context context) {
        Thread thread = new Thread() {
            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }

                try {
                    remoteImg.ip = ip;
//                    if (!remoteImg.isSocketSet) {

                        Log.v("DownloadSwc: ", "Connect server");

                        remoteImg.ImgSocket = new Socket(ip, Integer.parseInt("9000"));
                        remoteImg.ImgReader = new BufferedReader(new InputStreamReader(remoteImg.ImgSocket.getInputStream(), "UTF-8"));
                        remoteImg.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(remoteImg.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));
//                    }

                    Log.v("DownloadSwc: ", "here we are 2");

                    if (remoteImg.ImgSocket.isConnected()) {
                        remoteImg.isSocketSet = true;
                        Log.v("DownloadSwc: ", "Connect with Server successfully");
                        Toast.makeText(getContext(), "Connect with Server successfully", Toast.LENGTH_SHORT).show();
                        remoteImg.ImgPWriter.println("connect from android client" + ":down.");
                        remoteImg.ImgPWriter.flush();

                    } else {
                        Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    }

                    //接收来自服务器的消息
                    while (remoteImg.ImgSocket.isConnected()) {
                        if (!remoteImg.ImgSocket.isInputShutdown()) {
                        /*读取一行字符串，读取的内容来自于客户机
                        reader.readLine()方法是一个阻塞方法，
                        从调用这个方法开始，该线程会一直处于阻塞状态，
                        直到接收到新的消息，代码才会往下走*/
                            String content = "";
                            while ((content = remoteImg.ImgReader.readLine()) != null) {
                                Log.v("---------Image------:", content);
                                if (!((Activity) context).isFinishing()) {
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
                    Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        };
        thread.start();

    }



    private static void PullSwc_block(String ip, Context context){

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Thread thread = new Thread() {
            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }

                try {
                    remoteImg.ip = ip;

                    Log.v("DownloadSwc: ", "Connect server");

                    remoteImg.ImgSocket = new Socket(ip, Integer.parseInt("9000"));
                    remoteImg.ImgReader = new BufferedReader(new InputStreamReader(remoteImg.ImgSocket.getInputStream(), "UTF-8"));
                    remoteImg.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(remoteImg.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));

                    if (remoteImg.ImgSocket.isConnected()){
                        String content = remoteImg.ImgReader.readLine();
                        remoteImg.id = Integer.parseInt(content.split(":")[0]);
                        Log.v("ConnectServer", content);

                    }

                    Filesocket_receive filesocket_receive = new Filesocket_receive();
                    filesocket_receive.filesocket = new Socket(ip, 9002);
                    filesocket_receive.mReader = new BufferedReader(new InputStreamReader(filesocket_receive.filesocket.getInputStream(), "UTF-8"));
                    filesocket_receive.mPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(filesocket_receive.filesocket.getOutputStream(), StandardCharsets.UTF_8)));
                    filesocket_receive.IsDown = true;
                    filesocket_receive.path = context.getExternalFilesDir(null).toString() + "/Sync/BlockGet";

                    if (remoteImg.ImgSocket.isConnected() && filesocket_receive.filesocket.isConnected()){
                        filesocket_receive.mPWriter.println(remoteImg.id + ":manage handle.");
                        filesocket_receive.mPWriter.flush();
                    }

                    Log.v("PullSwc: ", "here we are 2");

                    if (remoteImg.ImgSocket.isConnected()) {
                        remoteImg.isSocketSet = true;
                        Log.v("PullSwc: ", "Connect with Server successfully");
                        Toast.makeText(getContext(), "Connect with Server successfully", Toast.LENGTH_SHORT).show();

                        String filename = getFilename(context);
                        String offset = getoffset(context, filename);
                        int[] index = BigImgReader.getIndex(offset);
                        System.out.println(filename);

                        String SwcFileName = filename.split("RES")[0] + "__" +
                                index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5];

                        remoteImg.ImgPWriter.println(SwcFileName + ":GetBBSwc.");
                        remoteImg.ImgPWriter.flush();

                        filesocket_receive.readFile("blockGet__" + SwcFileName + ".swc", context);

                        filesocket_receive = null;

                        NeuronTree nt = NeuronTree.readSWC_file(context.getExternalFilesDir(null).toString() + "/Sync/BlockGet/" +  "blockGet__" + SwcFileName + ".swc");
                        myrenderer.SetSwcLoaded();
                        myrenderer.importNeuronTree(nt);
                        myGLSurfaceView.requestRender();
//                        uiHandler.sendEmptyMessage(1);
                        remoteImg.disconnectFromHost();

                        Looper.loop();

                    } else {
                        Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                        remoteImg.disconnectFromHost();
                        Looper.loop();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    remoteImg.disconnectFromHost();
                    Looper.loop();
                }
            }
        };
        thread.start();
    }


    private void PullSwc_block_Manual(boolean isDrawMode){

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String SwcFilePath = remote_socket.PullSwc_block(isDrawMode);

                if (SwcFilePath.equals("Error")){
                    Toast_in_Thread("Something Wrong When Pull Swc File !");
                }

                try {
                    NeuronTree nt = NeuronTree.readSWC_file(SwcFilePath);
                    myrenderer.SetSwcLoaded();
                    myrenderer.importNeuronTree(nt);
                    myGLSurfaceView.requestRender();
                    uiHandler.sendEmptyMessage(1);
                }catch (Exception e){
                    Toast_in_Thread("Some Wrong when open the Swc File, Try Again Please !");
                }

            }
        });

        thread.start();
    }



    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private static void PullSwc_block_Auto(boolean isDrawMode){

        String SwcFilePath = remote_socket.PullSwc_block(isDrawMode);

        if (SwcFilePath.equals("Error")){
            Toast_in_Thread_static("Something Wrong When Pull Swc File !");
            return;
        }

        try {
            NeuronTree nt = NeuronTree.readSWC_file(SwcFilePath);
            myrenderer.SetSwcLoaded();
            myrenderer.importNeuronTree(nt);
            myGLSurfaceView.requestRender();
        }catch (Exception e){
            Toast_in_Thread_static("Something Wrong when open Swc File !");
        }


    }


    private void PullApo_block_Manual(){

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String ApoFilePath = remote_socket.PullApo_block();

                if (ApoFilePath.equals("Error")){
                    Toast_in_Thread("Something Wrong When Pull Swc File !");
                }

                try {
                    ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
                    ApoReader apoReader = new ApoReader();
                    apo = apoReader.read(ApoFilePath);
                    if (apo == null){
                        Toast.makeText(context,"Make sure the .apo file is right",Toast.LENGTH_SHORT).show();
                    }
                    myrenderer.importApo(apo);
                    myGLSurfaceView.requestRender();
                    uiHandler.sendEmptyMessage(1);
                }catch (Exception e){
                    Toast_in_Thread("Some Wrong when open the Swc File, Try Again Please !");
                }

            }
        });

        thread.start();
    }

    private static void PullApo_block_Auto(){

        String ApoFilePath = remote_socket.PullApo_block();

        if (ApoFilePath.equals("Error")){
            Toast_in_Thread_static("Something Wrong When Pull Swc File !");
            return;
        }

        try {
            ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
            ApoReader apoReader = new ApoReader();
            apo = apoReader.read(ApoFilePath);
            if (apo == null){
                Toast.makeText(context,"Make sure the .apo file is right",Toast.LENGTH_SHORT).show();
            }
            myrenderer.importApo(apo);
            myGLSurfaceView.requestRender();
        }catch (Exception e){
            Toast_in_Thread_static("Something Wrong when open Swc File !");
        }

    }



    public void disconnectFromHost(){

        System.out.println("---- disconnect from host ----");
        try {

            if (remoteImg.ImgSocket != null){
                remoteImg.ImgSocket.close();
            }

            if (remoteImg.ImgSocket != null){
                remoteImg.ImgReader.close();
            }

            if (remoteImg.ImgSocket != null){
                remoteImg.ImgPWriter.close();
            }


        } catch (IOException e) {

            e.printStackTrace();
        }

    }


// 不保存完整图片，仅拍照
//    private void Camera(){
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String getImageFilePath(){
        String mCaptureDir = "/storage/emulated/0/C3/cameraPhoto";
        File dir = new File(mCaptureDir);
        if (!dir.exists()){
            dir.mkdirs();
        }

        String mCapturePath = mCaptureDir + "/" + "Photo_" + System.currentTimeMillis() +".jpg";
//        System.out.println("before" + mCapturePath);
//        try {
//            FileOutputStream fos = new FileOutputStream(mCapturePath);
////            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
////                        fos.flush();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        System.out.println("after" + mCapturePath);
        return mCapturePath;
    }

    private void Camera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_IMAGE_CAPTURE);
            File photoFile = null;
//            String photoFilePath = null;
            try {
                photoFile = createImageFile();
                showPic = photoFile;
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
//            photoFilePath = getImageFilePath();

//            System.out.println(photoFilePath);
//            try {
//                File f = new File(photoFilePath);
//
//                if (!f.exists())
//                    f.createNewFile();
//            } catch (Exception e){
//                System.out.println("AAAAAAA");
//                e.printStackTrace();
//            }
//            String imageUri = photoFile.getAbsolutePath();
//            String imageUri = insertImageToSystem(context, photoFilePath);

//            Uri photoURI = Uri.parse(imageUri);
//            System.out.println(imageUri);
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                        "com.example.myapplication__volume.provider",
                        photoFile);
                picUri = photoURI;
//                takePictureIntent.putExtra("output", photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

//                ifTakePhoto = true;
//                startActivityForResult(takePictureIntent, 1);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.v("Camera", "Here we are");

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void savePhoto(Bitmap photoBitmap){
        String photoFilePath = getImageFilePath();

        System.out.println(photoFilePath);
        File f = new File(photoFilePath);
        FileOutputStream fileOutputStream = null;
        try {
//            File f = new File(photoFilePath);

            fileOutputStream = new FileOutputStream(f);
            if (photoBitmap != null){
                if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }

            else {
                System.out.println("bitmap is empty");
            }
            return;
        } catch (Exception e){
            System.out.println("AAAAAAA");
            e.printStackTrace();
        }

        if (!f.exists()) {
            Uri uri = Uri.parse(photoFilePath);

            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        getContext().getContentResolver().openFileDescriptor(uri, "w");

                fileOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptor);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } else {
                    System.out.println("bitmap is empty");
                }
            } catch (Exception e) {
                System.out.println("CCCCCCC");
                e.printStackTrace();
            }
        }

        myrenderer.SetPath(photoFilePath);

        myGLSurfaceView.requestRender();

//        String imageUri = photoFilePath;
////            String imageUri = insertImageToSystem(context, photoFilePath);
//
//        Uri photoURI = Uri.parse(imageUri);
    }



//    private void Camera() {
//        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // 判断是否有相机
//        if (captureIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            Uri photoUri = null;
//
//            startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
//    }


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

//        Image4DSimple img = myrenderer.getImg();
//        if(img == null || !img.valid()){
//            Toast.makeText(this, "Please load image first!", Toast.LENGTH_LONG).show();
//            return;
//        }

//        if (myrenderer.getIfFileSupport()){
//            Toast.makeText(context, "Please load a image first", Toast.LENGTH_SHORT).show();
//            return;
//        }


        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示

                .asAttachList(new String[]{"PinPoint   ", "Draw Curve", "Delete Marker", "Delete MultiMarker", "Delete Curve", "Split       ",
                                "Set PenColor", "Change PenColor", "Change All PenColor", "Set MColor", "Change MColor",
                                "Change All MColor", "Exit Drawing mode", "Clear Tracing"},
//                        new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
//                                if (!(ifPoint || ifPainting || ifDeletingMarker || ifDeletingLine || ifSpliting))
//                                    ll_top.addView(buttonUndo);
                                switch (text) {
                                    case "PinPoint   ":
                                        if (!myrenderer.ifImageLoaded()){
                                            Toast.makeText(context, "Please load a image first", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        ifPoint = !ifPoint;
                                        ifPainting = false;
                                        ifDeletingMarker = false;
                                        ifDeletingLine = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifPoint && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_add_marker);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifPoint = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Draw Curve":
                                        if (!myrenderer.ifImageLoaded()){
                                            Toast.makeText(context, "Please load a image first", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        ifPainting = !ifPainting;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifDeletingLine = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifPainting && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_draw);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifPainting = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Delete Marker":
                                        ifDeletingMarker = !ifDeletingMarker;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingLine = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifDeletingMarker && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_marker_delete);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifDeletingMarker = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Delete MultiMarker":
                                        ifDeletingMultiMarker = !ifDeletingMultiMarker;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingLine = false;
                                        if (ifDeletingMultiMarker && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_draw_main);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifDeletingMultiMarker = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Delete Curve":
                                        ifDeletingLine = !ifDeletingLine;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifDeletingLine && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_delete_curve);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifDeletingLine = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Split       ":
                                        ifSpliting = !ifSpliting;
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifSpliting && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_split);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifSpliting = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Set PenColor":
                                        //调用选择画笔窗口
                                        PenSet();

                                        break;

                                    case "Change PenColor":
                                        ifChangeLineType = !ifChangeLineType;
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if(ifChangeLineType && !ifSwitch){
//                                            Draw.setText("Change PenColor");
//                                            Draw.setTextColor(Color.RED);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);

                                            try {
                                                ifSwitch = false;
//                                                ifChangeLineType = false;
//                                                Switch.setText("Pause");
//                                                Switch.setTextColor(Color.BLACK);
//                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifChangeLineType = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
//                                            draw_i.setImageResource(R.drawable.ic_draw_main);

                                        }
                                        break;

                                    case "Change All PenColor":
                                        try {
                                            myrenderer.changeAllType();
                                        } catch (CloneNotSupportedException e) {
                                            e.printStackTrace();
                                        }
                                        myGLSurfaceView.requestRender();
                                        break;

                                    case "Set MColor":
                                        MarkerPenSet();
                                        break;

                                    case "Change MColor":
                                        ifChangeMarkerType = !ifChangeMarkerType;
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifChangeLineType = false;
                                        ifSpliting = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifChangeMarkerType && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_draw_main);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifChangeMarkerType = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Change All MColor":
                                        try {
                                            myrenderer.changeAllMarkerType();
                                        } catch (CloneNotSupportedException e) {
                                            e.printStackTrace();
                                        }
                                        myGLSurfaceView.requestRender();
                                        break;

                                    case "Exit Drawing mode":
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        draw_i.setImageResource(R.drawable.ic_draw_main);
                                        ll_bottom.removeView(Switch);
//                                        ll_top.removeView(buttonUndo_i);
                                        break;

                                    case "Clear Tracing":
                                        myrenderer.deleteAllTracing();
                                        myGLSurfaceView.requestRender();
                                        break;
                                }
                            }
                        }).show();

    }

    private void Draw_list(View v){
        drawPopupView = new XPopup.Builder(this)
                .atView(v)
                .autoDismiss(false)
                .asAttachList(new String[]{"For Marker", "For Curve", "Clear Tracing", "Exit Drawing Mode"},
                        new int[]{}, new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text){
                                    case "For Marker":
                                        markerProcessList(v);
                                        break;
                                    case "For Curve":
                                        curveProcessList(v);
                                        break;
                                    case "Clear Tracing":
                                        myrenderer.deleteAllTracing();
                                        myGLSurfaceView.requestRender();
                                        drawPopupView.dismiss();
                                        break;
                                    case "Exit Drawing Mode":
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        draw_i.setImageResource(R.drawable.ic_draw_main);
                                        ll_bottom.removeView(Switch);
                                        drawPopupView.dismiss();
//                                        ll_top.removeView(buttonUndo_i);
                                        break;
                                }
                            }
                        }).show();
    }

    private void markerProcessList(View v){
//        System.out.println(drawPopupView.getWidth());
//        drawPopupView.show();
        new XPopup.Builder(this)
                .atView(v)
                .offsetX(580)
                .isRequestFocus(false)
                .popupPosition(PopupPosition.Right)
                .asAttachList(new String[]{"PinPoint   ", "Delete Marker", "Delete MultiMarker", "Set MColor", "Change MColor",
                        "Change All MColor"}, new int[]{}, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        switch (text){
                            case "PinPoint   ":
                                if (!myrenderer.ifImageLoaded()){
                                    Toast.makeText(context, "Please load a image first", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ifPoint = !ifPoint;
                                ifPainting = false;
                                ifDeletingMarker = false;
                                ifDeletingLine = false;
                                ifSpliting = false;
                                ifChangeLineType = false;
                                ifChangeMarkerType = false;
                                ifDeletingMultiMarker = false;
                                if (ifPoint && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_add_marker);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifPoint = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Delete Marker":
                                ifDeletingMarker = !ifDeletingMarker;
                                ifPainting = false;
                                ifPoint = false;
                                ifDeletingLine = false;
                                ifSpliting = false;
                                ifChangeLineType = false;
                                ifChangeMarkerType = false;
                                ifDeletingMultiMarker = false;
                                if (ifDeletingMarker && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_marker_delete);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifDeletingMarker = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Delete MultiMarker":
                                ifDeletingMultiMarker = !ifDeletingMultiMarker;
                                ifPainting = false;
                                ifPoint = false;
                                ifDeletingMarker = false;
                                ifSpliting = false;
                                ifChangeLineType = false;
                                ifChangeMarkerType = false;
                                ifDeletingLine = false;
                                if (ifDeletingMultiMarker && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_draw_main);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifDeletingMultiMarker = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Set MColor":
                                MarkerPenSet();
                                break;

                            case "Change MColor":
                                ifChangeMarkerType = !ifChangeMarkerType;
                                ifDeletingLine = false;
                                ifPainting = false;
                                ifPoint = false;
                                ifDeletingMarker = false;
                                ifChangeLineType = false;
                                ifSpliting = false;
                                ifDeletingMultiMarker = false;
                                if (ifChangeMarkerType && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_draw_main);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifChangeMarkerType = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Change All MColor":
                                try {
                                    myrenderer.changeAllMarkerType();
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                myGLSurfaceView.requestRender();
                                break;

                        }
                        drawPopupView.dismiss();
                    }
                }).show();
    }

    private void curveProcessList(View v){
        new XPopup.Builder(this)
                .atView(v)
                .offsetX(580)
                .asAttachList(new String[]{"Draw Curve", "Delete Curve", "Split       ",
                                "Set PenColor", "Change PenColor", "Change All PenColor"}, new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text){
                                    case "Draw Curve":
                                        if (!myrenderer.ifImageLoaded()){
                                            Toast.makeText(context, "Please load a image first", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        ifPainting = !ifPainting;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifDeletingLine = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifPainting && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_draw);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifPainting = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Delete Curve":
                                        ifDeletingLine = !ifDeletingLine;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifDeletingLine && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_delete_curve);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifDeletingLine = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Split       ":
                                        ifSpliting = !ifSpliting;
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifSpliting && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_split);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifSpliting = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Set PenColor":
                                        //调用选择画笔窗口
                                        PenSet();

                                        break;

                                    case "Change PenColor":
                                        ifChangeLineType = !ifChangeLineType;
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if(ifChangeLineType && !ifSwitch){
//                                            Draw.setText("Change PenColor");
//                                            Draw.setTextColor(Color.RED);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);

                                            try {
                                                ifSwitch = false;
//                                                ifChangeLineType = false;
//                                                Switch.setText("Pause");
//                                                Switch.setTextColor(Color.BLACK);
//                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifChangeLineType = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
//                                            draw_i.setImageResource(R.drawable.ic_draw_main);

                                        }
                                        break;

                                    case "Change All PenColor":
                                        try {
                                            myrenderer.changeAllType();
                                        } catch (CloneNotSupportedException e) {
                                            e.printStackTrace();
                                        }
                                        myGLSurfaceView.requestRender();
                                        break;

                                }
                                drawPopupView.dismiss();
                            }
                        }).show();
    }



    /**
     * function for the Tracing button
     *
     * @param v the button: tracing
     */
    private void Tracing(final View v) {

        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            Toast.makeText(this, "Please load image first!", Toast.LENGTH_LONG).show();
            return;
        }

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"APP2", "GD", "Save SWC file"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "GD":
                                        try {
                                            Log.v("Mainactivity", "GD-Tracing start~");
                                            Toast.makeText(v.getContext(), "GD-Tracing start~", Toast.LENGTH_SHORT).show();
//                                            Timer timer = new Timer();
                                            progressBar.setVisibility(View.VISIBLE);
                                            timer = new Timer();
                                            timerTask = new TimerTask() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void run() {

                                                    try {
                                                        GDTracing();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            };
                                            timer.schedule(timerTask, 1000);
//                                            timer.schedule(new TimerTask() {
//                                                @RequiresApi(api = Build.VERSION_CODES.N)
//                                                @Override
//                                                public void run() {
//
//                                                    try {
//                                                        GDTracing();
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//
//                                                }
//
//                                            }, 1000); // 延时1秒
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        break;

                                    case "APP2":
                                        try {
                                            Log.v("Mainactivity", "APP2-Tracing start~");
                                            Toast.makeText(v.getContext(), "APP2-Tracing start~", Toast.LENGTH_SHORT).show();
//                                            Timer timer = new Timer();
                                            progressBar.setVisibility(View.VISIBLE);
                                            timer = new Timer();
                                            timerTask = new TimerTask() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void run() {

                                                    try {
                                                        APP2();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            };
                                            timer.schedule(timerTask, 1000);
//                                            timer.schedule(new TimerTask() {
//                                                @RequiresApi(api = Build.VERSION_CODES.N)
//                                                @Override
//                                                public void run() {
//
//                                                    try {
//                                                        APP2();
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//
//                                                }
//                                            }, 1000); // 延时1秒
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

//                                    case "DetectLine":
////                                        LineDetect(v);
//                                        break;

                                    case "Save SWC file":
                                        SaveSWC();
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
                .asAttachList(new String[]{"Analyze SWC", "Animate", "Screenshot", "----", "About"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Analyze SWC":
                                        Analyse();
                                        break;

                                    case "Animate":
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifDeletingLine = false;
                                        SetAnimation();
                                        break;

                                    case "Screenshot":
                                        ShareScreenShot();
                                        break;

                                    case "About":
                                        About();
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

        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
            return;
        }

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示

                .asAttachList(new String[]{"Filter by exemplars"},

                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
//                                    case "For Developer...":
//                                        //调用特征选择窗口
//                                        FeatureSet();
//                                        break;

                                    case "Filter by exemplars":
                                        //调用像素分类接口，显示分类结果
                                        Learning();
                                        break;


//                                    case "GSDT":
//                                        //gsdt检测斑点（eg.soma）
//                                        try {
//                                            Log.v("Mainactivity", "GSDT function.");
//                                            Toast.makeText(getContext(), "GSDT function start~", Toast.LENGTH_SHORT).show();
////                                            Timer timer = new Timer();
//                                            timer = new Timer();
//                                            timerTask = new TimerTask() {
//                                                @RequiresApi(api = Build.VERSION_CODES.N)
//                                                @Override
//                                                public void run() {
//                                                    try {
//                                                        Log.v("Mainactivity", "GSDT start.");
//                                                        GSDT_Fun();
//                                                        Log.v("Mainactivity", "GSDT end.");
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            };
//                                            timer.schedule(timerTask, 0);
////                                            timer.schedule(new TimerTask() {
////                                                @RequiresApi(api = Build.VERSION_CODES.N)
////                                                @Override
////                                                public void run() {
////
////                                                    try {
////                                                        Log.v("Mainactivity", "GSDT start.");
////                                                        GSDT_Fun();
////                                                        Log.v("Mainactivity", "GSDT end.");
////                                                    } catch (Exception e) {
////                                                        e.printStackTrace();
////                                                    }
////
////                                                }
////                                            }, 0); // 延时0秒
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        break;

//                                    case "Anisotropic":
//                                        //调用各向异性滤波，显示滤波结果
//                                        try {
//                                            Log.v("Mainactivity", "Anisotropic function.");
//                                            Toast.makeText(getContext(), "Anisotropic function start~", Toast.LENGTH_SHORT).show();
////                                            Timer timer = new Timer();
//                                            timer = new Timer();
//                                            timerTask = new TimerTask() {
//                                                @RequiresApi(api = Build.VERSION_CODES.N)
//                                                @Override
//                                                public void run() {
//                                                    try {
//                                                        Log.v("Mainactivity", "Anisotropic start.");
//                                                        Anisotropic();
//                                                        Log.v("Mainactivity", "Anisotropic end.");
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            };
//                                            timer.schedule(timerTask, 0);
////                                            timer.schedule(new TimerTask() {
////                                                @RequiresApi(api = Build.VERSION_CODES.N)
////                                                @Override
////                                                public void run() {
////
////                                                    try {
////                                                        Log.v("Mainactivity", "Anisotropic start.");
////                                                        Anisotropic();
////                                                        Log.v("Mainactivity", "Anisotropic end.");
////                                                    } catch (Exception e) {
////                                                        e.printStackTrace();
////                                                    }
////
////                                                }
////                                            }, 0); // 延时0秒
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        break;
                                }
                            }
                        })
                .show();
    }


    /**
     * used to detect the line
     */
    private void LineDetect(){
        new XPopup.Builder(this)
//                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示

                .asCenterList("Detect Line", new String[]{"Run", "DetectLineUseApp2", "Consensus", "ConsensusWithAllResult", "Check Current Lines", "Detect Tips", "Train", "SaveRandomForest", "ReadRandomForest"},


                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Run":
                                        Toast.makeText(getContext(), "start~", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        timer = new Timer();
                                        timerTask = new TimerTask() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void run() {
                                                try {
                                                    DetectLine d = new DetectLine();
                                                    Image4DSimple img = myrenderer.getImg();
                                                    if(img == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);

                                                        Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    NeuronTree nt = myrenderer.getNeuronTree();
                                                    if(nt.listNeuron.isEmpty() || nt == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Please add line", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }

                                                    try {
                                                        NeuronTree app2Result = d.detectLine(img,nt);
                                                        NeuronTree result = new NeuronTree();

                                                        if(rf != null){
                                                            result = d.lineClassification(img,app2Result,rf);
                                                        }else {
                                                            result = app2Result;
                                                        }
                                                        myrenderer.importNeuronTree(result);
                                                        myGLSurfaceView.requestRender();
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    } catch (Exception e) {
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                } catch (Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    e.printStackTrace();
                                                }

                                            }
                                        };
                                        timer.schedule(timerTask, 1000);
                                        break;

                                    case "DetectLineUseApp2":
                                        Toast.makeText(getContext(), "start~", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        timer = new Timer();
                                        timerTask = new TimerTask() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void run() {
                                                try {
                                                    Image4DSimple img = myrenderer.getImg();
                                                    if(img == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);

                                                        Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    NeuronTree nt = myrenderer.getNeuronTree();
                                                    myrenderer.deleteAllTracing();
                                                    if(nt.listNeuron.isEmpty() || nt == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Please add line", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    try {
                                                        ParaAPP2 p = new ParaAPP2();
                                                        p.p4dImage = img;
                                                        p.xc0 = p.yc0 = p.zc0 = 0;
                                                        p.xc1 = (int) p.p4dImage.getSz0() - 1;
                                                        p.yc1 = (int) p.p4dImage.getSz1() - 1;
                                                        p.zc1 = (int) p.p4dImage.getSz2() - 1;
                                                        ArrayList<ImageMarker> markers = myrenderer.getMarkerList().getMarkers();
                                                        p.landmarks = new LocationSimple[markers.size()];
                                                        p.bkg_thresh = -1;
                                                        for (int i = 0; i < markers.size(); i++) {
                                                            p.landmarks[i] = new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
                                                        }
                                                        System.out.println("---------------start---------------------");
                                                        V3dNeuronAPP2Tracing.app2DetectLine(p,nt);

                                                        NeuronTree result = p.resultNt;
                                                        for(int i=0; i<result.listNeuron.size(); i++){
                                                            result.listNeuron.get(i).type = 4;
                                                        }
                                                        myrenderer.importNeuronTree(result);
                                                        myGLSurfaceView.requestRender();
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }catch (Exception e){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }

                                                } catch (Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    e.printStackTrace();
                                                }

                                            }
                                        };
                                        timer.schedule(timerTask, 1000);
                                        break;

                                    case "Consensus":
                                        Toast.makeText(getContext(), "start~", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        timer = new Timer();
                                        timerTask = new TimerTask() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void run() {
                                                try {
                                                    Image4DSimple img = myrenderer.getImg();
                                                    if(img == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);

                                                        Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    NeuronTree nt = myrenderer.getNeuronTree();
                                                    myrenderer.deleteAllTracing();
                                                    if(nt.listNeuron.isEmpty() || nt == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Please add line", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    try {
                                                        NeuronTree result = Consensus.run(img,nt,2,false);
                                                        myrenderer.importNeuronTree(result);
                                                        myGLSurfaceView.requestRender();
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }catch (Exception e){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }

                                                } catch (Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    e.printStackTrace();
                                                }

                                            }
                                        };
                                        timer.schedule(timerTask, 1000);
                                        break;

                                    case "ConsensusWithAllResult":
                                        Toast.makeText(getContext(), "start~", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        timer = new Timer();
                                        timerTask = new TimerTask() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void run() {
                                                try {
                                                    Image4DSimple img = myrenderer.getImg();
                                                    if(img == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);

                                                        Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    NeuronTree nt = myrenderer.getNeuronTree();
                                                    myrenderer.deleteAllTracing();
                                                    if(nt.listNeuron.isEmpty() || nt == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Please add line", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    try {
                                                        NeuronTree result = Consensus.run(img,nt,2,true);
                                                        myrenderer.importNeuronTree(result);
                                                        myGLSurfaceView.requestRender();
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }catch (Exception e){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }

                                                } catch (Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    e.printStackTrace();
                                                }

                                            }
                                        };
                                        timer.schedule(timerTask, 1000);
                                        break;


                                    case "Check Current Lines":
                                        Toast.makeText(getContext(), "start~", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        timer = new Timer();
                                        timerTask = new TimerTask() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void run() {
                                                try {
                                                    DetectLine d = new DetectLine();
                                                    Image4DSimple img = myrenderer.getImg();
                                                    if(img == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);

                                                        Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    NeuronTree nt = myrenderer.getNeuronTree();
                                                    if(nt.listNeuron.isEmpty() || nt == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Please add line", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }

                                                    try {

                                                        NeuronTree result;

                                                        if(rf != null){
                                                            result = d.lineClassification(img,nt,rf);
                                                            myrenderer.deleteAllTracing();
                                                            myrenderer.importNeuronTree(result);
                                                            myGLSurfaceView.requestRender();
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }else {
                                                            if (Looper.myLooper() == null) {
                                                                Looper.prepare();
                                                            }

                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(getContext(), "random forest is none", Toast.LENGTH_LONG).show();
                                                            Looper.loop();
                                                        }

                                                    } catch (Exception e) {
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                } catch (Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    e.printStackTrace();
                                                }

                                            }
                                        };
                                        timer.schedule(timerTask, 1000);
                                        break;

                                    case "Detect Tips":
                                        Toast.makeText(getContext(), "start~", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
                                        timer = new Timer();
                                        timerTask = new TimerTask() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void run() {
                                                try {
                                                    DetectLine d = new DetectLine();

                                                    Image4DSimple img = myrenderer.getImg();
                                                    if(img == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    NeuronTree nt = myrenderer.getNeuronTree();
                                                    if(nt.listNeuron.isEmpty() || nt == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Please add line", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    ArrayList<ImageMarker> tips = d.detectTips(img,nt);
                                                    myrenderer.getMarkerList().getMarkers().addAll(tips);
                                                    myGLSurfaceView.requestRender();
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }

                                            }
                                        };
                                        timer.schedule(timerTask, 1000);
                                        break;


                                    case "Train":
//                                        if (Looper.myLooper() == null) {
//                                            Looper.prepare();
//                                        }

                                        Toast.makeText(getContext(), "train start~", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);
//                                        Looper.loop();
//                                        Timer timer1 = new Timer();
//                                        timer1.schedule(new TimerTask() {
                                        timer = new Timer();
                                        timerTask = new TimerTask() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void run() {
                                                try{
                                                    DetectLine d = new DetectLine();
                                                    Image4DSimple img = myrenderer.getImg();
                                                    if(img == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }
                                                    NeuronTree nt = myrenderer.getNeuronTree();
                                                    if(nt.listNeuron.isEmpty() || nt == null){
                                                        if (Looper.myLooper() == null) {
                                                            Looper.prepare();
                                                        }
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), "Please add train line", Toast.LENGTH_LONG).show();
                                                        Looper.loop();
                                                    }

                                                    rf = d.train(img,nt,rf);

                                                    if (Looper.myLooper() == null) {
                                                        Looper.prepare();
                                                    }

                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getContext(), "train is ended", Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }catch (Exception e){
                                                    if (Looper.myLooper() == null) {
                                                        Looper.prepare();
                                                    }
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    Looper.loop();
                                                }

                                            }
                                        };
                                        timer.schedule(timerTask, 1000);
                                        break;

                                    case "SaveRandomForest":
                                        if(rf == null){
//                                            if (Looper.myLooper() == null) {
//                                                Looper.prepare();
//                                            }

                                            Toast.makeText(getContext(), "randomForest is null", Toast.LENGTH_LONG).show();
//                                            Looper.loop();
                                        }else {
                                            String randomForestDir = "/storage/emulated/0/C3/randomForest";
                                            File dir = new File(randomForestDir);
                                            if (!dir.exists()){
                                                dir.mkdirs();
                                            }
                                            try {
                                                rf.saveRandomForest(dir);
                                                Toast.makeText(getContext(), "save successfully to "+randomForestDir, Toast.LENGTH_LONG).show();
                                            } catch (IOException e) {
//                                                if (Looper.myLooper() == null) {
//                                                    Looper.prepare();
//                                                }

                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                                                Looper.loop();
                                            }
                                        }
                                        break;

                                    case "ReadRandomForest":
                                        String randomForestDir = "/storage/emulated/0/C3/randomForest";
                                        try {
                                            System.out.println("-----------------start------------------");
                                            rf = new RandomForest();
                                            rf.readRandomForest(randomForestDir);
                                            Toast.makeText(getContext(), "load successfully", Toast.LENGTH_LONG).show();
                                        }catch (Exception e){
//                                            if (Looper.myLooper() == null) {
//                                                Looper.prepare();
//                                            }

                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                                            Looper.loop();
                                        }
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
//                                        UploadSWC();
//                                        PushSWC_Block();
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

        Log.v("DownloadSWC: ", "here we are");
        DownloadSwc("39.100.35.131", this);
    }


    private void LoadSWC() {

//        if (!myrenderer.ifImageLoaded()){
//            Toast.makeText(context, "Please open a image first", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (!ifImport) {
            ifImport = true;
            ifAnalyze = false;
            ifUpload = false;
            ifLoadLocal = false;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }


    private void PushSWC_Block_Manual(){

        String filepath = this.getExternalFilesDir(null).toString();
        String swc_file_path = filepath + "/Sync/BlockSet";
        File dir = new File(swc_file_path);

        if (!dir.exists()){
            if (!dir.mkdirs())
                Toast.makeText(this,"Fail to create file: PushSWC_Block", Toast.LENGTH_SHORT).show();
        }

        String filename = getFilename_Remote(this);
        String neuron_number = getNeuronNumber_Remote(this, filename);
        String offset = getoffset_Remote(this, filename);
        System.out.println(offset);
        int[] index = BigImgReader.getIndex(offset);
        System.out.println(filename);

        String ratio = Integer.toString(remote_socket.getRatio_SWC());
        String SwcFileName = "blockSet__" + neuron_number + "__" +
                index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5] + "__" + ratio;

        System.out.println(SwcFileName);

        if (Save_curSwc_fast(SwcFileName, swc_file_path)){
            File SwcFile = new File(swc_file_path + "/" + SwcFileName + ".swc");
            try {
                System.out.println("Start to push swc file");
                InputStream is = new FileInputStream(SwcFile);
                long length = SwcFile.length();

                if (length < 0 || length > Math.pow(2, 28)){
                    Toast_in_Thread("Something Wrong When Upload SWC, Try Again Please !");
                    return;
                }

                remote_socket.PushSwc_block(SwcFileName + ".swc", is, length);

            } catch (Exception e){
                System.out.println("----" + e.getMessage() + "----");
            }
        }
    }


    private void PushAPO_Block_Manual(){

        String filepath = this.getExternalFilesDir(null).toString();
        String apo_file_path = filepath + "/Sync/BlockSet/Apo";
        File dir = new File(apo_file_path);

        if (!dir.exists()){
            if (!dir.mkdirs())
                Toast.makeText(this,"Fail to create file: PushAPO_Block", Toast.LENGTH_SHORT).show();
        }

        String filename = getFilename_Remote(this);
        String neuron_number = getNeuronNumber_Remote(this, filename);
        String offset = getoffset_Remote(this, filename);
        System.out.println(offset);
        int[] index = BigImgReader.getIndex(offset);
        System.out.println(filename);

        String ratio = Integer.toString(remote_socket.getRatio_SWC());
        String ApoFileName = "blockSet__" + neuron_number + "__" +
                index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5] + "__" + ratio;

        System.out.println(ApoFileName);

        if (Save_curSwc_fast(ApoFileName, apo_file_path)){
            File ApoFile = new File(apo_file_path + "/" + ApoFileName + ".swc");
            try {
                System.out.println("Start to push swc file");
                InputStream is = new FileInputStream(ApoFile);
                long length = ApoFile.length();

                if (length < 0 || length > Math.pow(2, 28)){
                    Toast_in_Thread("Something Wrong When Upload SWC, Try Again Please !");
                    return;
                }

                remote_socket.PushApo_block(apo_file_path + ".swc", is, length);

            } catch (Exception e){
                System.out.println("----" + e.getMessage() + "----");
            }
        }
    }


    private String[] SaveSWC_Block_Auto(){

        String filepath = this.getExternalFilesDir(null).toString();
        String swc_file_path = filepath + "/Sync/BlockSet";
        File dir = new File(swc_file_path);

        if (!dir.exists()){
            if (!dir.mkdirs())
                Toast.makeText(this,"Fail to create file: PushSWC_Block", Toast.LENGTH_SHORT).show();
        }

        String filename = getFilename_Remote(this);
        String neuron_number = getNeuronNumber_Remote(this, filename);
        String offset = getoffset_Remote(this, filename);
        System.out.println(offset);
        int[] index = BigImgReader.getIndex(offset);
        System.out.println(filename);

        String ratio = Integer.toString(remote_socket.getRatio_SWC());
        String SwcFileName = "blockSet__" + neuron_number + "__" +
                index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5] + "__" + ratio;

        System.out.println(SwcFileName);

        if (Save_curSwc_fast(SwcFileName, swc_file_path)){
            return new String[]{ swc_file_path, SwcFileName };
        }

        Log.v("SaveSWC_Block_Auto","Save Successfully !");
        return new String[]{"Error", "Error"};
    }



    private static void PushSWC_Block_Auto(String swc_file_path, String SwcFileName){

        if (swc_file_path.equals("Error"))
            return;

        File SwcFile = new File(swc_file_path + "/" + SwcFileName + ".swc");
        if (!SwcFile.exists()){
            Toast_in_Thread_static("Something Wrong When Upload SWC, Try Again Please !");
            return;
        }
        try {
            System.out.println("Start to push swc file");
            InputStream is = new FileInputStream(SwcFile);
            long length = SwcFile.length();

            if (length <= 0 || length > Math.pow(2, 28)){
                Toast_in_Thread_static("Something Wrong When Upload SWC, Try Again Please !");
                return;
            }
            remote_socket.PushSwc_block(SwcFileName + ".swc", is, length);

        } catch (Exception e){
            System.out.println("----" + e.getMessage() + "----");
        }
    }


    private static void PushAPO_Block_Auto(String apo_file_path, String ApoFileName){

        if (apo_file_path.equals("Error"))
            return;

        File ApoFile = new File(apo_file_path + "/" + ApoFileName + ".apo");
        if (!ApoFile.exists()){
            Toast_in_Thread_static("Something Wrong When Upload APO, Try Again Please !");
            return;
        }
        try {
            System.out.println("Start to push apo file");
            InputStream is = new FileInputStream(ApoFile);
            long length = ApoFile.length();

            if (length <= 0 || length > Math.pow(2, 28)){
                Toast_in_Thread_static("Something Wrong When Upload APO, Try Again Please !");
                return;
            }
            remote_socket.PushApo_block(ApoFileName + ".apo", is, length);

        } catch (Exception e){
            System.out.println("----" + e.getMessage() + "----");
        }
    }


    private boolean Save_curSwc_fast(String SwcFileName, String dir_str){

        System.out.println("start to save-------");
        myrenderer.reNameCurrentSwc(SwcFileName);

        String error = "init";
        try {
            error = myrenderer.saveCurrentSwc(dir_str);
            System.out.println("error:" + error);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!error.equals("")) {
            if (error.equals("This file already exits")){
                String errorMessage = "";
                try{
                    errorMessage = myrenderer.oversaveCurrentSwc(dir_str);
                    if (errorMessage == "Overwrite failed!"){
                        Toast_in_Thread("Fail to save swc file: Save_curSwc_fast");
                        return false;
                    }
                }catch (Exception e){
                    System.out.println(errorMessage);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (error.equals("Current swc is empty!")){
                Toast_in_Thread("Current swc file is empty!");
                return false;
            }
        } else{
            System.out.println("save SWC to " + dir_str + "/" + SwcFileName + ".swc");
        }
        return true;
    }



    private String[] SaveAPO_Block_Auto(){

        String filepath = this.getExternalFilesDir(null).toString();
        String apo_file_path = filepath + "/Sync/BlockSet/Apo";
        File dir = new File(apo_file_path);

        if (!dir.exists()){
            if (!dir.mkdirs())
                Toast.makeText(this,"Fail to create file: PushAPO_Block", Toast.LENGTH_SHORT).show();
        }

        String filename = getFilename_Remote(this);
        String neuron_number = getNeuronNumber_Remote(this, filename);
        String offset = getoffset_Remote(this, filename);
        System.out.println(offset);
        int[] index = BigImgReader.getIndex(offset);
        System.out.println(filename);

        String ratio = Integer.toString(remote_socket.getRatio_SWC());
        String ApoFileName = "blockSet__" + neuron_number + "__" +
                index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5] + "__" + ratio;

        System.out.println(ApoFileName);

        if (Save_curApo_fast(ApoFileName, apo_file_path)){
            return new String[]{ apo_file_path, ApoFileName };
        }

        Log.v("SaveAPO_Block_Auto","Save Successfully !");
        return new String[]{"Error", "Error"};
    }


    private boolean Save_curApo_fast(String ApoFileName, String dir_str){

        System.out.println("start to save apo-------");

        String error = "init";
        try {
            error = myrenderer.saveCurrentApo(dir_str + ApoFileName + ".apo");
            System.out.println("error:" + error);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (error.equals("Current apo is empty!")){
            Toast_in_Thread("Current apo file is empty!");
            return false;
        } else{
            System.out.println("save SWC to " + dir_str + "/" + ApoFileName + ".apo");
        }
        return true;
    }



    private void ShareScreenShot() {

        myrenderer.setTakePic(true, this);
        myGLSurfaceView.requestRender();
        final String[] imgPath = new String[1];
        final boolean[] isGet = {false};

//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void run() {
//
//                try {
//
//                    if (Looper.myLooper() == null)
//                        Looper.prepare();
//
//                    imgPath[0] = myrenderer.getmCapturePath();
//                    myrenderer.resetCapturePath();
//
//                    if (imgPath[0] != null)
//                    {
//                        // Toast.makeText(v.getContext(), "save screenshot to " + imgPath[0], Toast.LENGTH_SHORT).show();
//                        Log.v("Share","save screenshot to " + imgPath[0]);
//
//                        Intent shareIntent = new Intent();
//                        String imageUri = insertImageToSystem(context, imgPath[0]);
//                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                        shareIntent.setAction(Intent.ACTION_SEND);
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUri));
//                        shareIntent.setType("image/jpeg");
//                        startActivity(Intent.createChooser(shareIntent, "Share from C3"));
//
//                    }
//                    else{
//                        Toast.makeText(getContext(), "Fail to screenshot", Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, 3 * 1000); // 延时0.1秒 //change from 2000 by HP

//        while (imgPath[0] == null && !isGet[0]); //why  this?? by HP

//        if (imgPath[0] != null) {
//            Intent shareIntent = new Intent();
//            String imageUri = insertImageToSystem(context, imgPath[0]);
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUri));
//            shareIntent.setType("image/jpeg");
//            startActivity(Intent.createChooser(shareIntent, "Share from C3"));
//        }

        Log.v("Share","save screenshot to " + imgPath[0]);


    }


    private static String insertImageToSystem(Context context, String imagePath) {
        String url = "";
        String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1 );
        try {
            url = MediaStore.Images.Media.insertImage(context.getContentResolver(), imagePath, filename, "ScreenShot from C3");
        } catch (FileNotFoundException e) {
            System.out.println("SSSSSSSSSSSS");
            e.printStackTrace();
        }
        System.out.println("Filename: " + filename);
        System.out.println("Url: " + url);
        return url;
    }



    MDDialog.Builder si_mdDialog_bd = new MDDialog.Builder(this).setContentView(R.layout.sensor_result);
    MDDialog si_mdDialog = null;

    private void SensorInfo(){

//        MDDialog.Builder si_mdDialog_bd = new MDDialog.Builder(this).setContentView(R.layout.sensor_result);
//        MDDialog si_mdDialog = null;

        String[] SensorList = {""};

        si_mdDialog = si_mdDialog_bd
                .setContentView(R.layout.sensor_result)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {

                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view

                        etGyro=(TextView)contentView.findViewById(R.id.etGyro);

                        etLinearAcc=(TextView)contentView.findViewById(R.id.etLinearAcc);

                        etMagnetic=(TextView)contentView.findViewById(R.id.etMagnetic);

                        etAcc=(TextView)contentView.findViewById(R.id.etAcc);

                        etLight=(TextView)contentView.findViewById(R.id.etLight);

                        // if (etLight == null)
                        // System.out.println("---- etLight is null ----");

                        etPressure=(TextView)contentView.findViewById(R.id.etPressure);

                        etProximity=(TextView)contentView.findViewById(R.id.etProximity);

                        etGravity=(TextView)contentView.findViewById(R.id.etGravity);

                        etRotation_vector=(TextView)contentView.findViewById(R.id.etRotation_vector);

                        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

                        StartSensorListening();//启动传感数据采集(注册三个传感器）

                    }

                })
                .setTitle("Sensor information")
                .create();
        si_mdDialog.show();
        si_mdDialog.getWindow().setLayout(1000, 1500);

        si_mdDialog.setOnDismissListener(si_mdDialog->StopSensorListening());

//        StopSensorListening();

    }


    private SensorEventListener listener = new SensorEventListener()
    {
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            //// TODO
        }

        public void onSensorChanged(SensorEvent e)
        {
            StringBuilder sb=null;
            switch (e.sensor.getType()) {
                case Sensor.TYPE_GYROSCOPE:     //陀螺传感器
                    sb = new StringBuilder();
                    sb.append("陀螺仪传感器:");
                    sb.append("\n绕X轴转过的角速度:");
                    sb.append(e.values[0]);
                    sb.append("\n绕Y轴转过的角速度:");
                    sb.append(e.values[1]);
                    sb.append("\n绕Z轴转过的角速度:");
                    sb.append(e.values[2]);
                    etGyro.setText(sb.toString());
                    GyrData[0] = e.values[0];
                    GyrData[1] = e.values[1];
                    GyrData[2] = e.values[2];
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:    //磁场传感器
                    sb = new StringBuilder();
                    sb.append("\n磁场传感器：");
                    sb.append("\n绕X轴-磁场:");
                    sb.append(e.values[0]);
                    sb.append("\n绕Y轴-磁场:");
                    sb.append(e.values[1]);
                    sb.append("\n绕Z轴-磁场:");
                    sb.append(e.values[2]);
                    etMagnetic.setText(sb.toString());
                    MagData[0] = e.values[0];
                    MagData[1] = e.values[1];
                    MagData[2] = e.values[2];
                    break;
                case Sensor.TYPE_ACCELEROMETER:   //加速度传感器
                    sb = new StringBuilder();
                    sb.append("\n加速度传感器:");
                    sb.append("\nX轴-加速度:");
                    sb.append(e.values[0]);
                    sb.append("\nY轴-加速度:");
                    sb.append(e.values[1]);
                    sb.append("\nZ轴-加速度:");
                    sb.append(e.values[2]);
                    etLinearAcc.setText(sb.toString());
                    AccData[0] = e.values[0];
                    AccData[1] = e.values[1];
                    AccData[2] = e.values[2];
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:   //线性加速度传感器
                    sb = new StringBuilder();
                    sb.append("\n线性加速度传感器:");
                    sb.append("\nX轴-线性加速度:");
                    sb.append(e.values[0]);
                    sb.append("\nY轴-线性加速度:");
                    sb.append(e.values[1]);
                    sb.append("\nZ轴-线性加速度:");
                    sb.append(e.values[2]);
                    etAcc.setText(sb.toString());
                    AccData2[0] = e.values[0];
                    AccData2[1] = e.values[1];
                    AccData2[2] = e.values[2];
                    break;
                case Sensor.TYPE_LIGHT:   //线性加速度传感器
                    sb = new StringBuilder();
                    sb.append("\n光线传感器:");
                    sb.append("\n光照强度:");
                    sb.append(e.values[0]);

                    if (sb == null)
                        System.out.println("----- sb is null----- ");
                    if (etLight == null)
                        System.out.println("----- etLight is null----- ");

                    etLight.setText(sb.toString());
                    LightData[0] = e.values[0];
                    break;
                case Sensor.TYPE_PRESSURE:   //线性加速度传感器
                    sb = new StringBuilder();
                    sb.append("\n压力传感器:");
                    sb.append("\n压力:");
                    sb.append(e.values[0]);
                    etPressure.setText(sb.toString());
                    PreData[0] = e.values[0];
                    break;
                case Sensor.TYPE_PROXIMITY:   //线性加速度传感器
                    sb = new StringBuilder();
                    sb.append("\n距离传感器:");
                    sb.append("\n距离:");
                    sb.append(e.values[0]);
                    etProximity.setText(sb.toString());
                    ProData[0] = e.values[0];
                    break;
                case Sensor.TYPE_GRAVITY:   //线性加速度传感器
                    sb = new StringBuilder();
                    sb.append("\n重力加速度传感器:");
                    sb.append("\nX轴-重力加速度:");
                    sb.append(e.values[0]);
                    sb.append("\nY轴-重力加速度:");
                    sb.append(e.values[1]);
                    sb.append("\nZ轴-重力加速度:");
                    sb.append(e.values[2]);
                    etGravity.setText(sb.toString());
                    GraData[0] = e.values[0];
                    GraData[1] = e.values[1];
                    GraData[2] = e.values[2];
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:   //线性加速度传感器
                    sb = new StringBuilder();
                    sb.append("\n旋转角度:");
                    sb.append("\nX轴-旋转角度:");
                    sb.append(e.values[0]);
                    sb.append("\nY轴-旋转角度:");
                    sb.append(e.values[1]);
                    sb.append("\nZ轴-旋转角度:");
                    sb.append(e.values[2]);
                    etRotation_vector.setText(sb.toString());
                    Rot_Vec_Data[0] = e.values[0];
                    Rot_Vec_Data[1] = e.values[1];
                    Rot_Vec_Data[2] = e.values[2];
                    break;

            }
        }
    };

    public void StartSensorListening()
    {
        //super.onResume();
        Log.v("StartSensorListening","Here we are!!!");

        //陀螺传感器注册监听器
        mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_NORMAL);
        //磁场传感器注册监听器
        mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(
                Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
        //加速度传感器注册监听器
        mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        //线性加速度传感器注册监听器
        mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(
                Sensor.TYPE_LINEAR_ACCELERATION),SensorManager.SENSOR_DELAY_NORMAL);
        //光线传感器注册监听器
        mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(
                Sensor.TYPE_LIGHT),SensorManager.SENSOR_DELAY_NORMAL);
        //压力传感器注册监听器
        mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(
                Sensor.TYPE_PRESSURE),SensorManager.SENSOR_DELAY_NORMAL);
        //距离传感器注册监听器
        mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(
                Sensor.TYPE_PROXIMITY),SensorManager.SENSOR_DELAY_NORMAL);
        //重力加速度传感器注册监听器
        mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(
                Sensor.TYPE_GRAVITY),SensorManager.SENSOR_DELAY_NORMAL);
        //ROTATION_VECTOR传感器注册监听器
        mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR),SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void StopSensorListening()
    {
        Toast.makeText(getContext(), "Sensor stopped", Toast.LENGTH_SHORT).show();
        mSensorManager.unregisterListener(listener);
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
                                        myrenderer.setIfDownSampling(true);
                                        myrenderer.myAnimation.Start();
                                        break;

                                    case "Pause":
                                        myrenderer.setIfDownSampling(false);
                                        myrenderer.myAnimation.Pause();
                                        break;

                                    case "Resume":
                                        myrenderer.setIfDownSampling(true);
                                        myrenderer.myAnimation.Resume();
                                        break;

                                    case "Stop":
                                        myrenderer.setIfDownSampling(false);
                                        myrenderer.myAnimation.Stop();
                                        ifAnimation = false;
                                        ll_top.removeView(animation_i);
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
                myrenderer.setIfDownSampling(true);
                Rotation_i.setImageResource(R.drawable.ic_block_red_24dp);
                myrenderer.myAnimation.quickStart();
                myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            } else {
                myrenderer.setIfDownSampling(false);
                Rotation_i.setImageResource(R.drawable.ic_3d_rotation_red_24dp);
                myrenderer.myAnimation.quickStop();
                myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                myGLSurfaceView.requestRender();
            }

        }else {
            Toast.makeText(getContext(),"Pleas load a file first!",Toast.LENGTH_SHORT).show();
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
            temp_mode[5] = ifChangeLineType;
            temp_mode[6] = ifChangeMarkerType;
            temp_mode[7] = ifDeletingMultiMarker;

            ifPainting = false;
            ifPoint = false;
            ifDeletingLine = false;
            ifDeletingMarker = false;
            ifSpliting = false;
            ifChangeLineType = false;
            ifChangeMarkerType = false;
            ifDeletingMultiMarker = false;

        } else {
            Switch.setText("Pause");
            Switch.setTextColor(Color.BLACK);
            ifPainting            = temp_mode[0];
            ifPoint               = temp_mode[1];
            ifDeletingLine        = temp_mode[2];
            ifDeletingMarker      = temp_mode[3];
            ifSpliting            = temp_mode[4];
            ifChangeLineType      = temp_mode[5];
            ifChangeMarkerType    = temp_mode[6];
            ifDeletingMultiMarker = temp_mode[7];
        }
    }

    private void About() {
        new XPopup.Builder(this)

                .asConfirm("C3: VizAnalyze Big 3D Images", "By Peng lab @ BrainTell. \n\n" +
                                "Version: 20200825b 22:24 UTC+8 build",
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
            Toast.makeText(getContext(), "Error when open file!" + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }




    public void Select_img(){
        Context context = this;
        new XPopup.Builder(this)
                .asCenterList("Select Remote Server", new String[]{"AliYun Server", "SEU Server", "Local Server"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "AliYun Server":
                                        String ip = "39.100.35.131";
//                                        String ip = "192.168.31.11";
                                        if (DrawMode){
                                            BigFileRead_Remote(ip);
                                        }else {
                                            BigFileRead_Remote_Check(ip);
                                        }
                                        break;

                                    case "SEU Server":
                                        if (DrawMode){
                                            BigFileRead_Remote("223.3.33.234");
                                        }else {
                                            BigFileRead_Remote_Check("223.3.33.234");
                                        }
//
//                                        BigFileRead_Remote("223.3.33.234");
//                                        Toast.makeText(getContext(), "The server is not available now", Toast.LENGTH_SHORT).show();
                                        break;

                                    case "Local Server":
                                        BigFileRead_local();
                                        break;

                                    default:
                                        Toast.makeText(getContext(), "Something Wrong Here", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                .show();

    }


    private void BigFileRead_local(){
        String[] filename_list = bigImgReader.ChooseFile(this);
        if (filename_list != null){
            String [] str = new String[1];
            bigImgReader.ShowListDialog(this, filename_list);

//            filenametext.setText(filename);
//            ll_file.setVisibility(View.VISIBLE);
//
//            lp_undo.setMargins(0, 240, 20, 0);
//            Undo_i.setLayoutParams(lp_undo);
//
//            lp_up_i.setMargins(0, 360, 0, 0);
//            navigation_up.setLayoutParams(lp_up_i);
//
//            lp_nacloc_i.setMargins(20, 400, 0, 0);
//            navigation_location.setLayoutParams(lp_nacloc_i);
//
//            lp_sync_push.setMargins(0, 400, 20, 0);
//            sync_push.setLayoutParams(lp_sync_push);
//
//            lp_sync_pull.setMargins(0, 490, 20, 0);
//            sync_pull.setLayoutParams(lp_sync_pull);
        }
    }

    private void BigFileRead_Remote(String ip){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                remote_socket.DisConnectFromHost();
                remote_socket.ConnectServer(ip);
                remote_socket.Select_Brain();
            }
        });
        thread.start();

    }


    private void BigFileRead_Remote_Check(String ip){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                remote_socket.DisConnectFromHost();
                remote_socket.ConnectServer(ip);
                remote_socket.Select_Arbor();
            }
        });
        thread.start();

    }

    private void ConnectServer(String ip, Context context){

        remoteImg.setip(ip, context);
        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                remoteImg.disconnectFromHost();

                try {

                        Log.v("ConnectServer","start to connect server");
                        remoteImg.ip = ip;
                        remoteImg.ImgSocket = new Socket(ip, Integer.parseInt("9000"));
                        remoteImg.ImgReader = new BufferedReader(new InputStreamReader(remoteImg.ImgSocket.getInputStream(), "UTF-8"));
                        remoteImg.ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(remoteImg.ImgSocket.getOutputStream(), StandardCharsets.UTF_8)));


                    if(remoteImg.ImgSocket.isConnected()){
                        String content = remoteImg.ImgReader.readLine();
                        remoteImg.id = Integer.parseInt(content.split(":")[0]);
                        Log.v("ConnectServer", content);

                        Log.v("ConnectServer","send some message to server");
                        remoteImg.isSocketSet = true;
//                        Toast.makeText(getContext(), "Connect with Server successfully", Toast.LENGTH_SHORT).show();
                        Toast_in_Thread("hello world");
                        remoteImg.ImgPWriter.println( "connect for android client" + ":choose3.");
                        remoteImg.ImgPWriter.flush();

                    }else {
                        Log.v("ConnectServer","fail to connect server");
                        Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    }

                    //接收来自服务器的消息
                    if(remoteImg.ImgSocket.isConnected()) {
                        if(!remoteImg.ImgSocket.isInputShutdown()) {
                        /*读取一行字符串，读取的内容来自于客户机
                        reader.readLine()方法是一个阻塞方法，
                        从调用这个方法开始，该线程会一直处于阻塞状态，
                        直到接收到新的消息，代码才会往下走*/
                            String content = "";
                            Log.v("---------Image------:", "start to readline");

                            boolean[] isFinished = {false};

                            if ((content = remoteImg.ImgReader.readLine()) != null) {

                                isFinished[0] = true;
                                Log.v("---------Image------:", content);
                                if (!((Activity) context).isFinishing()){
                                    if (Looper.myLooper() == null)
                                        Looper.prepare();
                                    remoteImg.onReadyRead(content, context);
                                    Looper.loop();
                                }

//                                long l2 = System.currentTimeMillis();
//                                if (l2 - l1 < maxtimeout){
//
//                                }else {
//                                    ShowToast(context, "---Timeout---");
//                                }
                            }



//                            TimerTask ft = new TimerTask(){
//                                public void run(){
//                                    if (!isFinished[0]){
//                                        Log.v("---------Image------:", "start to close bufferreader");
//
//                                        try {
//                                            remoteImg.ImgReader.close();
//                                            ShowToast(context, "---Timeout---");
//                                            Log.v("---------Image------:", "bufferreader closed!");
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                            Log.v("---------Image------:", "fail to close bufferreader!");
//                                        }
//                                    }
//                                }
//                            };
//
//                            (new Timer()).schedule(ft, 5 * 1000);

                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Can't connect, try again please!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        };
        thread.start();

    }


    public void Toast_in_Thread(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void Toast_in_Thread_static(String message){
        Message msg = new Message();
        msg.what = Toast_Info_static;
        Bundle bundle = new Bundle();
        bundle.putString("Toast_msg",message);
        msg.setData(bundle);
        puiHandler.sendMessage(msg);
    }

    public void Block_switch(View v){
        context = v.getContext();
        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示

                .asAttachList(new String[]{"Left", "Right", "Top", "Bottom", "Front", "Back"},

                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Left":
                                        remoteImg.Selectblock_fast(context, false, "Left");
                                        break;

                                    case "Right":
                                        remoteImg.Selectblock_fast(context, false, "Right");
                                        break;

                                    case "Top":
                                        remoteImg.Selectblock_fast(context, false, "Top");
                                        break;

                                    case "Bottom":
                                        remoteImg.Selectblock_fast(context, false, "Bottom");
                                        break;

                                    case "Front":
                                        remoteImg.Selectblock_fast(context, false, "Front");
                                        break;

                                    case "Back":
                                        remoteImg.Selectblock_fast(context, false, "Back");
                                        break;
                                }
                            }
                        })
                .show();
    }



    public void Block_navigate(String text){
        context = this;


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {

                if (isBigData_Remote){

                    String[] Direction = {"Left", "Right", "Top", "Bottom", "Front", "Back"};

                    if (Arrays.asList(Direction).contains(text)){

                        Log.v("Block_navigate", text);
                        if (DrawMode){
                            push_info_swc = SaveSWC_Block_Auto();

                            //  for apo sync
//                            push_info_apo = SaveAPO_Block_Auto();
                            remote_socket.Selectblock_fast(context, false, text);
//                            PushSWC_Block_Auto(push_info[0], push_info[1]);

                        }else {
                            remote_socket.Selectblock_fast_Check(context, false, text);
                        }
                    }

                }
                if (isBigData_Local){
                    boolean ifNavigationLocation = myrenderer.getNav_location_Mode();
                    if (ifNavigationLocation){
                        Quit_Nav_Mode();
                    }

                    String filename = SettingFileManager.getFilename_Local(context);
                    int[] index = bigImgReader.SelectBlock_fast(text, context);
                    if (index == null){
                        System.out.println("----- index is null -----");
                        return;
                    }
                    String filepath = "/storage/emulated/0/C3/Server/" + filename + ".v3draw";
                    myrenderer.SetPath_Bigdata(filepath, index);
                    myGLSurfaceView.requestRender();
                }


            }
        }, 0 * 1000); // 延时5秒


    }

    private void Quit_Nav_Mode(){
        System.out.println("---------QuitNavigationLocation---------");
        myrenderer.quitNav_location_Mode();
        navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
    }


    private void Update_Nav_Mode(){
        String filename = SettingFileManager.getFilename_Local(this);
        String offset   = SettingFileManager.getoffset_Local(this, filename);

        float size_x = Float.parseFloat(filename.split("RES")[1].split("x")[0]);
        float size_y = Float.parseFloat(filename.split("RES")[1].split("x")[1]);
        float size_z = Float.parseFloat(filename.split("RES")[1].split("x")[2]);

        float offset_x = Float.parseFloat(offset.split("_")[0]);
        float offset_y = Float.parseFloat(offset.split("_")[1]);
        float offset_z = Float.parseFloat(offset.split("_")[2]);
        float size_block = Float.parseFloat(offset.split("_")[3]);

        float[] neuron = {size_x, size_y, size_z};
        float[] block = {offset_x, offset_y, offset_z};
        float[] size = {size_block, size_block, size_block};

        myrenderer.setNav_location(neuron, block, size);
    }

    public void Set_Nav_Mode(){
        String filename = null;
        String offset   = null;
        float[] neuron = null; float[] block = null; float[] size = null;
        if (isBigData_Remote){
            if (DrawMode){
                filename = getFilename_Remote(this);
                offset   = getoffset_Remote(this, filename);
            }else {
                filename = getFilename_Remote_Check(this);
                offset   = getoffset_Remote_Check(this, filename);
            }
        }
        if (isBigData_Local){
            filename = SettingFileManager.getFilename_Local(this);
            offset   = SettingFileManager.getoffset_Local(this, filename);
        }

        if (filename == null || offset == null)
            return;

        if (isBigData_Local || (isBigData_Remote && DrawMode)){

            float size_x, size_y, size_z;

            if (isBigData_Local){
                size_x = Float.parseFloat(filename.split("RES")[1].split("x")[0]);
                size_y = Float.parseFloat(filename.split("RES")[1].split("x")[1]);
                size_z = Float.parseFloat(filename.split("RES")[1].split("x")[2]);

            }else {
                size_x = Float.parseFloat(filename.split("RES")[1].split("x")[1]);
                size_y = Float.parseFloat(filename.split("RES")[1].split("x")[0].replace("(",""));
                size_z = Float.parseFloat(filename.split("RES")[1].split("x")[2].replace(")",""));
            }

            float offset_x = Float.parseFloat(offset.split("_")[0]);
            float offset_y = Float.parseFloat(offset.split("_")[1]);
            float offset_z = Float.parseFloat(offset.split("_")[2]);
            float size_block = Float.parseFloat(offset.split("_")[3]);

            neuron = new float[]{size_x, size_y, size_z};
            block  = new float[]{offset_x, offset_y, offset_z};
            size   = new float[]{size_block, size_block, size_block};
        }else {

            int offset_x_i = ( Integer.parseInt(offset.split(";")[0]) + Integer.parseInt(offset.split(";")[1]) ) / 2 -1;
            int offset_y_i = ( Integer.parseInt(offset.split(";")[2]) + Integer.parseInt(offset.split(";")[3]) ) / 2 -1;
            int offset_z_i = ( Integer.parseInt(offset.split(";")[4]) + Integer.parseInt(offset.split(";")[5]) ) / 2 -1;
            int size_i     = ( Integer.parseInt(offset.split(";")[1]) - Integer.parseInt(offset.split(";")[0]) );

            neuron = remote_socket.getImg_size_f();
            block  = new float[]{offset_x_i, offset_y_i, offset_z_i};
            size   = new float[]{size_i, size_i, size_i};

        }

        boolean ifNavigationLocation = myrenderer.getNav_location_Mode();

        if (!ifNavigationLocation){
            System.out.println("--------!ifNavigationLocation---------");
            myrenderer.setNav_location_Mode();
            myrenderer.setNav_location(neuron, block, size);
            myGLSurfaceView.requestRender();
            navigation_location.setImageResource(R.drawable.ic_gps_off_black_24dp);
        }else {
            System.out.println("---------ifNavigationLocation---------");
            myrenderer.setNav_location_Mode();
            myGLSurfaceView.requestRender();
            navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
        }
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
        context = this;
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
                            Toast.makeText(getContext(), "The name should not be empty.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if (error != "") {
                            if (error == "This file already exits"){
//                                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
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
                                                    if (errorMessage == "")
                                                        Toast.makeText(getContext(),"Overwrite successfully!", Toast.LENGTH_SHORT).show();
                                                    if (errorMessage == "Overwrite failed!")
                                                        Toast.makeText(getContext(),"Overwrite failed!", Toast.LENGTH_SHORT).show();

                                                }catch (Exception e){
                                                    System.out.println(errorMessage);
                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })

                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .create();
                                aDialog.show();
                            }
//                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                        } else{
                            Toast.makeText(getContext(), "save SWC to " + dir + "/" + swcFileName + ".swc", Toast.LENGTH_LONG).show();
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
//        img.getDataCZYX();
//        if (!img.valid()) {
//            Log.v("APP2Tracing", "Please load img first!");
//            if (Looper.myLooper() == null) {
//                Looper.prepare();
//            }
//            Toast.makeText(this, "Please load img first!", Toast.LENGTH_LONG).show();
//            Looper.loop();
//            return;
//        }
        if(img == null){
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();
            return;
        }
        myrenderer.saveUndo();
        ArrayList<ImageMarker> markers = myrenderer.getMarkerList().getMarkers();
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
//            p.outswc_file = getExternalFilesDir(null).toString() + "/" + "app2.swc";//"/storage/emulated/0/Download/app2.swc";
//            System.out.println(p.outswc_file);
            V3dNeuronAPP2Tracing.proc_app2(p);

//            NeuronTree nt = NeuronTree.readSWC_file(p.outswc_file);
            NeuronTree nt = p.resultNt;
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
            Toast.makeText(getContext(), "APP2-Tracing finish, size of result swc: " + Integer.toString(nt.listNeuron.size()), Toast.LENGTH_SHORT).show();
            myrenderer.importNeuronTree(nt);
            myGLSurfaceView.requestRender();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();

        } catch (Exception e) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void GDTracing() throws Exception {
        Image4DSimple img = myrenderer.getImg();
//        if (!img.valid()) {
//            Log.v("GDTracing", "Please load img first!");
//            if (Looper.myLooper() == null) {
//                Looper.prepare();
//            }
//            Toast.makeText(this, "Please load img first!", Toast.LENGTH_LONG).show();
//            Looper.loop();
//            return;
//        }
        if(img == null){
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            Toast.makeText(this, "Please load image first!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();
            return;
        }
        myrenderer.saveUndo();
        ArrayList<ImageMarker> markers = myrenderer.getMarkerList().getMarkers();
        if (markers.size() <= 1) {
            Log.v("GDTracing", "Please generate at least two markers!");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(getContext(), "Please produce at least two markers!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
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
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();
        }
        for (int i = 0; i < outswc.listNeuron.size(); i++) {
            outswc.listNeuron.get(i).type = 6;
        }

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        Toast.makeText(getContext(), "GD-Tracing finished, size of result swc: " + Integer.toString(outswc.listNeuron.size()), Toast.LENGTH_SHORT).show();
        myrenderer.importNeuronTree(outswc);
        myGLSurfaceView.requestRender();
        progressBar.setVisibility(View.INVISIBLE);
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

    private void setSettings(){

        SettingFileManager settingFileManager = new SettingFileManager();
        boolean [] ifChecked = new boolean[2];
        boolean [] ifChecked2 = new boolean[2];
        ifChecked[0] = false;
        ifChecked2[0] = false;

        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.settings)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        Switch downsample_on_off = contentView.findViewById(R.id.switch_rotation_mode);
                        boolean ifDownSample = myrenderer.getIfNeedDownSample();
                        downsample_on_off.setChecked(ifDownSample);

                        downsample_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                ifChecked[0] = true;
                                ifChecked[1] = isChecked;
                            }
                        });

                        Switch check_on_off = contentView.findViewById(R.id.switch_check_mode);
                        check_on_off.setChecked(!DrawMode);

                        check_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                ifChecked2[0] = true;
                                ifChecked2[1] = isChecked;
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
                        if (ifChecked[0]) {
                            myrenderer.setIfNeedDownSample(ifChecked[1]);
                            if (ifChecked[1]) {
                                settingFileManager.setDownSampleMode("DownSampleYes", getContext());
                            } else {
                                settingFileManager.setDownSampleMode("DownSampleNo", getContext());
                            }
                        }

                        if (ifChecked2[0]) {
                            DrawMode = !ifChecked2[1];
                            if (ifChecked2[1]) {
                                settingFileManager.setBigDataMode("Check Mode", getContext());
                            } else {
                                settingFileManager.setBigDataMode("Draw Mode", getContext());
                            }
                        }

                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle("Settings")
                .create();

        mdDialog.show();
        mdDialog.getWindow().setLayout(1000, 1500);
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
                        EditText speed = contentView.findViewById(R.id.edit_speed);
                        speed.setText(Integer.toString(rotation_speed));

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
                        if (ifChecked[1]) {
                            ifAnimation = ifChecked[0];
                        }

                        EditText speed = contentView.findViewById(R.id.edit_speed);
                        String rotation_speed_string = speed.getText().toString();

                        if (rotation_speed_string.isEmpty()){
                            Toast.makeText(context,"Make sure the input is right !!!",Toast.LENGTH_SHORT).show();
                            SetAnimation();
                            return;
                        }
                        rotation_speed = (int) Float.parseFloat(rotation_speed_string);

                        myrenderer.myAnimation.Stop();
                        myrenderer.setIfDownSampling(false);
                        myGLSurfaceView.requestRender();

                        if (ifAnimation) {
                            myrenderer.myAnimation.setAnimation(true, Float.parseFloat(rotation_speed_string), rotation_type[0]);
                            myrenderer.setIfDownSampling(true);

                            Rotation_i.setVisibility(View.GONE);

                            if (ll_top.findViewById(animation_id) == null){
                                ll_top.addView(animation_i,lp_animation_i);
                            }
                            myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

                        } else {

                            Rotation_i.setVisibility(View.VISIBLE);

                            if (ll_top.findViewById(animation_id) != null){
                                ll_top.removeView(animation_i);
                            }

                            myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

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
        remoteImg.disconnectFromHost();
        context = null;
        remoteImg = null;

        if (timer != null){
            timer.cancel();
            timer = null;
        }

        if (timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
    }

    //renderer 的生存周期和activity保持一致
    @Override
    protected void onPause() {
        super.onPause();
        myGLSurfaceView.onPause();
        Log.v("onPause", "start-----");
        remoteImg.disconnectFromHost();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myGLSurfaceView.onResume();
        Log.v("Path", filepath);
        Log.v("onResume", "start-----");
//        if (!OpenCVLoader.initDebug()) {
//
//            Log.i("cv", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
//
//        } else {
//
//            Log.i("cv", "OpenCV library found inside package. Using it!");
//
//        }
//        remoteImg = new RemoteImg();
    }




    private void Learning() {

        Image4DSimple img = myrenderer.getImg();
        if(img == null){

            Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_SHORT).show();
            return;
        }
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

        Toast.makeText(getContext(), "pixel  classification start~", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//        Image4DSimple out = new Image4DSimple();
//        out.setData(img);
//        myrenderer.ResetImg(out);




    }

    private void Anisotropic(){

        //获取当前显示图像
        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            Log.v("Anisotropic", "Please load img first!");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
            Looper.loop();
            return;
        }

        Log.v("Anisotropic", "Have got the image successfully!!");
        try {
            //Log.v("Anisotropic", "here");///problems
            System.out.println("Start here.....");
            //调用anisotropy功能函数处理图像
            img = anisotropy_demo(img);
            Log.v("Anisotropic", "Anisotropic function finished");

            //处理结果输出
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            if (img == null) {
                //显示滤波失败
                System.out.println("Fail to run anisotropic function.");
                Toast.makeText(getContext(), "Fail to run anisotropic function.", Toast.LENGTH_SHORT).show();
            }else {
                //输出滤波结果
                Toast.makeText(getContext(), "Run successfully.", Toast.LENGTH_SHORT).show();
                myrenderer.ResetImg(img);
                myGLSurfaceView.requestRender();
                Toast.makeText(getContext(), "Have been shown on the screen.", Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();

        }catch (Exception e) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();
        }
    }

    private Image4DSimple anisotropy_demo(Image4DSimple img) throws Exception {

        int width = (int) img.getSz0();
        int height = (int) img.getSz1();
        int depth = (int) img.getSz2();
        int channel = (int) img.getSz3();
        int[][][][] image = img.getDataCZYX();
        float[][][][] imageCopy = new float[channel][depth][height][width];
        double rate;
        int[][][][] S = new int[channel][depth][height][width];
        double[][][][] T = new double[channel][depth][height][width];

        double[] imax = new double[channel], new_imax = new double[channel];
        double[] imin = new double[channel], new_imin = new double[channel];

        //获取GSDT距离图像
        ParaGSDT p = new ParaGSDT();
        p.p4DImage = img;
        GSDT.GSDT_Fun(p);
        int[][][][] image2 = p.outImage.getDataCZYX();

        //参数初始化+计算原图灰度均值
        for (int chl = 0; chl < channel; chl++) {
            imax[chl] = 0;
            imin[chl] = Integer.MAX_VALUE;
            new_imax[chl] = 0;
            new_imin[chl] = Integer.MAX_VALUE;
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    for (int dep = 0; dep < depth; dep++) {
                        //初始化resultCopy
                        imageCopy[chl][dep][row][col] = image[chl][dep][row][col];
                        //初始化S
                        S[chl][dep][row][col] = 0;
                    }
                }
            }
        }

        int[] numOfNull = new int[channel];
        int[] numOfSmall = new int[channel];
        String score;

        //评估矩阵计算
        for (int chl = 0; chl < channel; chl++) {
            numOfNull[chl] = 0;
            numOfSmall[chl] = 0;
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    for (int dep = 0; dep < depth; dep++) {
                        //old version
                        score = myLocalEigenScore(imageCopy, 128, 128, 128, col, row, dep, chl, 9);
                        if (score.equals("null")) {
                            numOfNull[chl]++;
                            S[chl][dep][row][col] = -1;
                            T[chl][dep][row][col] = 0;
                            continue;
                        } else if (score.equals("0")) {
                            numOfSmall[chl]++;
                            S[chl][dep][row][col] = -2;
                            T[chl][dep][row][col] = 0;
                            continue;
                        }

                        T[chl][dep][row][col] = Double.parseDouble(score) * image2[chl][dep][row][col];

                    }

                }

            }
            System.out.println("numOfNull["+chl+"] = " + numOfNull[chl]);
            System.out.println("numOfSmall["+chl+"] = " + numOfSmall[chl]);
        }

        //获取原图和迭代后待变换区域灰度最值
        for (int chl = 0; chl < channel; chl++) {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    for (int dep = 0; dep < depth; dep++) {
                        if (S[chl][dep][row][col] == -1 || S[chl][dep][row][col] == -2) {
                            continue;
                        }
                        //原图
                        if (image[chl][dep][row][col] > imax[chl]) {
                            imax[chl] = image[chl][dep][row][col];
                        } else if (image[chl][dep][row][col] < imin[chl]) {
                            imin[chl] = image[chl][dep][row][col];
                        }
                        //处理后
                        if (T[chl][dep][row][col] > new_imax[chl]) {
                            new_imax[chl] = T[chl][dep][row][col];
                        } else if (T[chl][dep][row][col] < new_imin[chl]) {
                            new_imin[chl] = T[chl][dep][row][col];
                        }
                    }
                }
            }
            System.out.println("imax["+chl+"] = "+imax[chl]);
            System.out.println("imin["+chl+"] = "+imin[chl]);
            System.out.println("new_imax["+chl+"] = "+new_imax[chl]);
            System.out.println("new_imin["+chl+"] = "+new_imin[chl]);
        }

        //进行灰度区间线性变换

        for (int chl = 0; chl < channel; chl++) {
            rate = (new_imax[chl]==new_imin[chl]) ? 1 : (255)/(new_imax[chl]-new_imin[chl]);
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    for (int dep = 0; dep < depth; dep++) {
                        image[chl][dep][row][col] = (int)((T[chl][dep][row][col]-new_imin[chl]) * rate + 0);
                    }
                }
            }
        }


        boolean bool = img.setDataFormCZYX(image,img.getSz0(),img.getSz1(),img.getSz2(),img.getSz3(),img.getDatatype(),img.getIsBig());

        if (!bool){
            img = null;
        }
        return img;
    }

    public String myLocalEigenScore(float[][][][] imageCZYX, int sx, int sy, int sz, int x0, int y0, int z0, int chl, int r) {

        double score;

        //获取边界
        int xb, xe, yb, ye, zb, ze;
        xb = x0 - r;
        if (xb < 0) xb = 0;
        else if (xb >= sx) xb = sx - 1;
        xe = x0 + r;
        if (xe < 0) xe = 0;
        else if (xe >= sx) xe = sx - 1;
        yb = y0 - r;
        if (yb < 0) yb = 0;
        else if (yb >= sy) yb = sy - 1;
        ye = y0 + r;
        if (ye < 0) ye = 0;
        else if (ye >= sy) ye = sy - 1;
        zb = z0 - r;
        if (zb < 0) zb = 0;
        else if (zb >= sz) zb = sz - 1;
        ze = z0 + r;
        if (ze < 0) ze = 0;
        else if (ze >= sz) ze = sz - 1;

        //计算质心
        float xm = 0, ym = 0, zm = 0, s = 0, mv = 0;
        float w;

        for (int k = zb; k <= ze; k++) {
            for (int j = yb; j <= ye; j++) {
                for (int i = xb; i <= xe; i++) {
                    w = imageCZYX[chl][k][j][i];
                    xm += w * i;
                    ym += w * j;
                    zm += w * k;
                    s += w;
                }
            }
        }

        if (s > 0) {
            xm /= s;
            ym /= s;
            zm /= s;
            mv = s / (float)((ze - zb + 1) * (ye - yb + 1) * (xe - xb + 1));
        } else {
            System.out.println("Sum of window pixels equals or is smaller than 0. The window is not valid or some other problems in the data. Do nothing.");
            //score = 0;  //有问题
            return "null";
        }

        //计算协方差
        float cc11 = 0, cc12 = 0, cc13 = 0, cc22 = 0, cc23 = 0, cc33 = 0;
        float dfx, dfy, dfz;
        for (int k = zb; k <= ze; k++) {
            dfz = (float)k - zm;
            for (int j = yb; j <= ye; j++) {
                dfy = (float)j - ym;
                for (int i = xb; i <= xe; i++) {
                    dfx = (float)i - xm;

                    w = imageCZYX[chl][k][j][i] - mv;
                    if (w < 0) w = 0;

                    cc11 += w * dfx * dfx;
                    cc12 += w * dfx * dfy;
                    cc13 += w * dfx * dfz;
                    cc22 += w * dfy * dfy;
                    cc23 += w * dfy * dfz;
                    cc33 += w * dfz * dfz;
                }
            }
        }

        cc11 /= s;
        cc12 /= s;
        cc13 /= s;
        cc22 /= s;
        cc23 /= s;
        cc33 /= s;

        //获取矩阵特征值

        double[][] cov_matrix = new double[3][3];
        cov_matrix[0][0] = (double) cc11;
        cov_matrix[0][1] = cov_matrix[1][0] = (double) cc12;
        cov_matrix[1][1] = (double) cc22;
        cov_matrix[0][2] = cov_matrix[2][0] = (double) cc13;
        cov_matrix[1][2] = cov_matrix[2][1] = (double) cc23;
        cov_matrix[2][2] = (double) cc33;

        //定义一个矩阵
        Matrix A = new Matrix(cov_matrix);

        //计算特征值
        double[] eigenvalues = A.eig().getRealEigenvalues();

        //特征值排序
        for (int i = 0; i < 2; i++) {
            for (int j = i+1; j < 3; j++) {
                if (eigenvalues[i] < eigenvalues[j]) {
                    score = eigenvalues[i];
                    eigenvalues[i] = eigenvalues[j];
                    eigenvalues[j] = score;
                }
            }
        }

        //异常结果处理
        score = eigenvalues[0];
        if (score == 0d) {
            return "null";
        } else if (abs(eigenvalues[0]) < 0.001d) {
            return "0";
        }

        //计算返回结果
        double temp = 0;
        for (int i = 1; i < 3; i++) {
            if (eigenvalues[i] == 0d) {
                return "null";
            }
            if (abs(eigenvalues[i]) < 0.001d) {
                return "0";
            }
            temp += eigenvalues[i];
        }
        score /= (temp/2);
        return String.valueOf(score);

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


    //GSDT_function
    public void GSDT_Fun(){
        //building...
        Image4DSimple img = myrenderer.getImg();
        //img.getDataCZYX();
        if(img == null || !img.valid()){
            Log.v("GSDT", "Please load img first!");
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
            Looper.loop();
            return;
        }

        Log.v("GSDT", "Have got the image successfully!!");
        try {

            System.out.println("Start here.....");
            ParaGSDT p = new ParaGSDT();
            p.p4DImage = img;
            GSDT.GSDT_Fun(p);
            Log.v("GSDT", "GSDT function finished");

            //preparations for show
            myrenderer.ResetImg(p.outImage);
            myrenderer.getMarkerList().getMarkers().addAll(p.markers);//blue marker
            myrenderer.getMarkerList().add(p.MaxMarker);//red marker
            myGLSurfaceView.requestRender();
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            Toast.makeText(getContext(), "marker_loc:"+ p.max_loc[0] + "," + p.max_loc[1] + "," + p.max_loc[2], Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();
            /*
            ImageMarker m = p.GSDT_Fun(img, para);
            System.out.println("marker:"+ m.getXYZ().x + "," + m.getXYZ().y+","+m.getXYZ().z);
            m.type = 2;
            m.radius = 5;
            Log.v("GSDT", "got here2");
            markers.add(m);
             */
            //myGLSurfaceView.requestRender();

        }catch (Exception e) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();
        }

    }



    //画笔颜色设置
    public void PenSet(){

        String [] pcolor = new String[1];

        new MDDialog.Builder(this)
                .setContentView(R.layout.pen_choose)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
//                        EditText et1 = (EditText) contentView.findViewById(R.id.pencolor);
                        //pencolor= 2;
                        /*String color  = et1.getText().toString();
                        pencolor= Integer.parseInt(color);
                        System.out.println("pen color is");
                        System.out.println(pencolor);*/

                        final Spinner chooseColor = contentView.findViewById(R.id.pencolor);
                        chooseColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                pcolor[0] = chooseColor.getSelectedItem().toString();
                                Log.v("onItemSelected", chooseColor.getSelectedItem().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

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
//                        EditText et1 = (EditText) contentView.findViewById(R.id.pencolor);
                        String color  = pcolor[0];

                        if( !color.isEmpty()){

//                            myrenderer.pencolorchange(Integer.parseInt(color));;
                            myrenderer.pencolorchange(PenColor.valueOf(color).ordinal());
                            System.out.println("pen color is");
                            System.out.println(color);
                            //Log.v("Mainactivity", "GD-Tracing start~");
                            //Toast.makeText(v.getContext(), "GD-Tracing start~", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "pencolor set~", Toast.LENGTH_SHORT).show();


                        }else{

                            Toast.makeText(getContext(), "Please make sure all the information is right!!!", Toast.LENGTH_SHORT).show();
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

    public void MarkerPenSet(){

        String [] pcolor = new String[1];

        new MDDialog.Builder(this)
                .setContentView(R.layout.marker_pen_choose)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
//                        EditText et1 = (EditText) contentView.findViewById(R.id.markercolor);
                        //pencolor= 2;
                        /*String color  = et1.getText().toString();
                        pencolor= Integer.parseInt(color);
                        System.out.println("pen color is");
                        System.out.println(pencolor);*/

                        final Spinner chooseColor = contentView.findViewById(R.id.markercolor);
                        chooseColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                pcolor[0] = chooseColor.getSelectedItem().toString();
                                Log.v("onItemSelected", chooseColor.getSelectedItem().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                })
                .setTitle("Marker Color Set")
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
//                        EditText et1 = (EditText) contentView.findViewById(R.id.markercolor);
//                        String color  = et1.getText().toString();
                        String color = pcolor[0];

                        if( !color.isEmpty()){

                            myrenderer.markercolorchange(PenColor.valueOf(color).ordinal());
                            System.out.println("marker color is");
                            System.out.println(color);
                            //Log.v("Mainactivity", "GD-Tracing start~");
                            //Toast.makeText(v.getContext(), "GD-Tracing start~", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), "markercolor set~", Toast.LENGTH_SHORT).show();


                        }else{

                            Toast.makeText(getContext(), "Please make sure all the information is right!!!", Toast.LENGTH_SHORT).show();
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
        private float dis_x_start;
        private float dis_y_start;
        private boolean isZooming;
        private boolean isZoomingNotStop;
        private float x1_start;
        private float x0_start;
        private float y1_start;
        private float y0_start;


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
                        if (ifPainting || ifDeletingLine || ifSpliting || ifChangeLineType || ifDeletingMultiMarker) {
                            lineDrawed.add(X);
                            lineDrawed.add(Y);
                            lineDrawed.add(-1.0f);
                            myrenderer.setIfPainting(true);
                            requestRender();
                            Log.v("actionPointerDown", "Paintinggggggggggg");
                        }
//                        if (ifPoint) {
//                            Log.v("actionPointerDown", "Pointinggggggggggg");
//                            if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)
//                                myrenderer.add2DMarker(X, Y);
//                            else {
//                                myrenderer.setMarkerDrawed(X, Y);
//                            }
//                            Log.v("actionPointerDown", "(" + X + "," + Y + ")");
//                            requestRender();
//
//                        }
//                        if (ifDeletingMarker) {
//                            Log.v("actionPointerDown", "DeletingMarker");
//                            myrenderer.deleteMarkerDrawed(X, Y);
//                            requestRender();
//                        }
//                        if (ifDeletingLine){
//                            lineDrawed.add(X);
//                            lineDrawed.add(Y);
//                            lineDrawed.add(-1.0f);
//                            myrenderer.setIfPainting(true);
//                            requestRender();
//                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        lineDrawed.clear();
                        myrenderer.setIfPainting(false);
                        requestRender();
                        isZooming = true;
                        isZoomingNotStop = true;
                        float x1 = toOpenGLCoord(this, motionEvent.getX(1), true);
                        float y1 = toOpenGLCoord(this, motionEvent.getY(1), false);

//                        float x1=motionEvent.getX(1);
//                        float y1=motionEvent.getY(1);
                        dis_start = computeDis(normalizedX, x1, normalizedY, y1);
                        dis_x_start = x1 - normalizedX;
                        dis_y_start = y1 - normalizedY;

                        x0_start = normalizedX;
                        y0_start = normalizedY;
                        x1_start = x1;
                        y1_start = y1;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isZooming && isZoomingNotStop) {

                            float x2 = toOpenGLCoord(this, motionEvent.getX(1), true);
                            float y2 = toOpenGLCoord(this, motionEvent.getY(1), false);

//                            float x2=motionEvent.getX(1);
//                            float y2=motionEvent.getY(1);
                            double dis = computeDis(normalizedX, x2, normalizedY, y2);
                            double scale = dis / dis_start;
//                            if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker) {
                                myrenderer.zoom((float) scale);
//                            }
                            float dis_x = x2 - normalizedX;
                            float dis_y = y2 - normalizedY;
                            float ave_x = (x2 - x1_start + normalizedX - x0_start) / 2;
                            float ave_y = (y2 - y1_start + normalizedY - y0_start) / 2;
                            if (!(myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)) {
                                if (myrenderer.getIfDownSampling() == false)
                                    myrenderer.setIfDownSampling(true);
                            }
//                            if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker){
//                                myrenderer.rotate2f(dis_x_start, dis_x, dis_y_start, dis_y);
//                            }else {
//                                myrenderer.rotate(dis_x - dis_x_start, dis_y - dis_y_start, (float) (computeDis(dis_x, dis_x_start, dis_y, dis_y_start)));
                                myrenderer.rotate(ave_x, ave_y, (float)(computeDis((x2 + normalizedX) / 2, (x1_start + x0_start) / 2, (y2 + normalizedY) / 2, (y1_start + y0_start) / 2)));
//                            }
                            //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                            requestRender();
                            dis_start = dis;
                            dis_x_start = dis_x;
                            dis_y_start = dis_y;
                            x0_start = normalizedX;
                            y0_start = normalizedY;
                            x1_start = x2;
                            y1_start = y2;
                        } else {
                            if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker && !ifChangeMarkerType && !ifDeletingMultiMarker) {
                                if (!(myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)) {
                                    if (myrenderer.getIfDownSampling() == false)
                                        myrenderer.setIfDownSampling(true);
                                }
                                myrenderer.rotate(normalizedX - X, normalizedY - Y, (float) (computeDis(normalizedX, X, normalizedY, Y)));

                                //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                                requestRender();
                                X = normalizedX;
                                Y = normalizedY;
                            } else {
                                lineDrawed.add(normalizedX);
                                lineDrawed.add(normalizedY);
                                lineDrawed.add(-1.0f);

                                myrenderer.setLineDrawed(lineDrawed);
                                requestRender();

                                invalidate();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
//                        isZooming = false;
                        isZoomingNotStop = false;
                        myrenderer.setIfDownSampling(false);
                        X = normalizedX;
                        Y = normalizedY;
                        lineDrawed.clear();
                        myrenderer.setIfPainting(false);
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
                        if (!isZooming) {
                            try {
                                if (ifPoint) {
                                    Log.v("actionUp", "Pointinggggggggggg");
                                    if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)
                                        myrenderer.add2DMarker(normalizedX, normalizedY);
                                    else {
                                        myrenderer.setMarkerDrawed(normalizedX, normalizedY);
                                    }
                                    Log.v("actionPointerDown", "(" + X + "," + Y + ")");
                                    requestRender();

                                }
                                if (ifDeletingMarker) {
                                    Log.v("actionUp", "DeletingMarker");
                                    myrenderer.deleteMarkerDrawed(normalizedX, normalizedY);
                                    requestRender();
                                }
                                if (ifDeletingMultiMarker) {
                                    myrenderer.deleteMultiMarkerByStroke(lineDrawed);
                                    requestRender();
                                }
                                if (ifChangeMarkerType) {
                                    myrenderer.changeMarkerType(normalizedX, normalizedY);
                                    requestRender();
                                }
                                if (ifPainting) {
                                    Vector<Integer> segids = new Vector<>();
                                    myrenderer.setIfPainting(false);
//                            myrenderer.addLineDrawed(lineDrawed);

                                    if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)
                                        myrenderer.add2DCurve(lineDrawed);
                                    else {
//                                        int lineType = myrenderer.getLastLineType();
//                                        if (lineType != 3) {
////                                            int segid = myrenderer.addLineDrawed(lineDrawed);
////                                    segids.add(segid);
////                            requestRender();
////                                                    int [] curUndo = new int[1];
////                                                    curUndo[0] = -1;
//
//                                            V_NeuronSWC seg = myrenderer.addBackgroundLineDrawed(lineDrawed);
//                                            System.out.println("feature");
////                                                    System.out.println(v_neuronSWC_list.seg.size());
//                                            if (seg != null) {
//                                                V_NeuronSWC_list [] v_neuronSWC_list = new V_NeuronSWC_list[1];
//                                                myrenderer.addLineDrawed2(lineDrawed, v_neuronSWC_list);
//                                                myrenderer.deleteFromCur(seg, v_neuronSWC_list[0]);
//                                            }
////                                                    else {
////                                                        Toast.makeText(getContext(), "Please make sure the curve is inside the bounding box", Toast.LENGTH_LONG);
////                                                    }
////                                            myrenderer.deleteFromNew(segid);
//                                        } else {
//                                            myrenderer.addBackgroundLineDrawed(lineDrawed);
//                                        }
                                        Callable<String> task = new Callable<String>() {
                                            @Override
                                            public String call() throws Exception {
                                                int lineType = myrenderer.getLastLineType();
                                                if (lineType != 2) {
//                                            int segid = myrenderer.addLineDrawed(lineDrawed);
//                                    segids.add(segid);
//                            requestRender();
//                                                    int [] curUndo = new int[1];
//                                                    curUndo[0] = -1;

                                                    V_NeuronSWC seg = myrenderer.addBackgroundLineDrawed(lineDrawed);
                                                    System.out.println("feature");
//                                                    System.out.println(v_neuronSWC_list.seg.size());
                                                    if (seg != null) {
                                                        V_NeuronSWC_list [] v_neuronSWC_list = new V_NeuronSWC_list[1];
                                                        myrenderer.addLineDrawed2(lineDrawed, v_neuronSWC_list);
                                                        myrenderer.deleteFromCur(seg, v_neuronSWC_list[0]);
                                                    }
//                                                    else {
//                                                        Toast.makeText(getContext(), "Please make sure the curve is inside the bounding box", Toast.LENGTH_LONG);
//                                                    }
//                                            myrenderer.deleteFromNew(segid);
                                                } else {
                                                    myrenderer.addBackgroundLineDrawed(lineDrawed);
                                                }
                                                requestRender();
                                                return "succeed";
                                            }
                                        };
                                        ExecutorService exeService = Executors.newSingleThreadExecutor();
                                        Future<String> future = exeService.submit(task);
                                        try {
                                            String result = future.get(1500, TimeUnit.MILLISECONDS);
                                            System.err.println("Result:" + result);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println("unfinished in 1.5 seconds");
//                                            exeService.shutdown();
//                                            future.cancel(true);
                                        }
//                                int lineType = myrenderer.getLastLineType();
//                                if (lineType != 3) {
//                                    int segid = myrenderer.addLineDrawed(lineDrawed);
//                                    segids.add(segid);
//                            requestRender();

//                                    myrenderer.addLineDrawed2(lineDrawed);
//                                    myrenderer.deleteFromNew(segid);
//                                } else {
//                                    myrenderer.addBackgroundLineDrawed(lineDrawed);
//                                }
                                    }
//                            requestRender();
////
//                            if (myrenderer.deleteFromNew(segid)) {
//                                myrenderer.addLineDrawed2(lineDrawed);
//                                requestRender();
//                            }
//
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
////                            myrenderer.addLineDrawed2(lineDrawed);
                                    lineDrawed.clear();
                                    myrenderer.setLineDrawed(lineDrawed);
//
                                    requestRender();
                                }
//                            requestRender();

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
                                if (ifChangeLineType) {
                                    myrenderer.setIfPainting(false);
                                    int type = myrenderer.getLastLineType();
                                    myrenderer.changeLineType(lineDrawed, type);
                                    lineDrawed.clear();
                                    myrenderer.setLineDrawed(lineDrawed);
                                    requestRender();
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            lineDrawed.clear();
                            myrenderer.setIfPainting(false);
                        }
                        lineDrawed.clear();
                        myrenderer.setIfPainting(false);
                        requestRender();
                        isZooming = false;
                        myrenderer.setIfDownSampling(false);
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



    public void File_icon(){

        new XPopup.Builder(this)
//        .maxWidth(400)
//        .maxHeight(1350)
                .asCenterList("File Open & Save", new String[]{"Open LocalFile", "Open BigData", "Load SWCFile","Camera"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Open LocalFile":
                                        loadLocalFile();
                                        break;
                                    case "Open BigData":
                                        loadBigData();
                                        break;
                                    case "Load SWCFile":
                                        LoadSWC();
                                        break;
                                    case"Camera":
                                        Camera();
                                        break;
                                    default:
                                        Toast.makeText(getContext(), "Default in file", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                .show();

    }

    public void Experiment_icon(){
        Context context = this;
        new XPopup.Builder(this)
                .setPopupCallback(new SimpleCallback() { //设置显示和隐藏的回调
                    @Override
                    public void onDismiss() {
                        // 完全隐藏的时候执行
//                        Toast.makeText(getContext(), "GSDT function start~", Toast.LENGTH_SHORT).show();
//                        GD_Tracing();
                    }
                })
//        .maxWidth(400)
//        .maxHeight(1350)
                .asCenterList("Experimental Features", new String[]{"Detect Line", "Detect Corner", "GSDT","Anisotropic", "VoiceChat-1to1", "For Developer(Classify)"},
                        new OnSelectListener() {
//                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Detect Line":
                                        LineDetect();
                                        break;
                                    case "Detect Corner":
//                                        try {
////                                            progressBar.setVisibility(View.VISIBLE);
////                                            timer = new Timer();
////                                            timerTask = new TimerTask() {
////                                                @Override
////                                                public void run() {
////                                                    myrenderer.corner_detection();
////                                                    myGLSurfaceView.requestRender();
////                                                    progressBar.setVisibility(View.INVISIBLE);
////                                                }
////                                            };
//////                                            progressBar.setVisibility(View.INVISIBLE);
////                                        }catch (Exception e){
////                                            e.printStackTrace();
////                                        }
//                                        timer.schedule(timerTask, 0);
                                        if (myrenderer.if2dImageLoaded()){
                                            myrenderer.corner_detection();
                                            myGLSurfaceView.requestRender();
                                        } else {
                                            Toast.makeText(getContext(), "Please load a 2d image first", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case "GSDT":
                                        //gsdt检测斑点（eg.soma）
                                        try {
                                            Log.v("Mainactivity", "GSDT function.");
                                            Toast.makeText(getContext(), "GSDT function start~", Toast.LENGTH_SHORT).show();
//                                            Timer timer = new Timer();
                                            progressBar.setVisibility(View.VISIBLE);
                                            timer = new Timer();
                                            timerTask = new TimerTask() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void run() {
                                                    try {
                                                        Log.v("Mainactivity", "GSDT start.");
                                                        GSDT_Fun();
                                                        Log.v("Mainactivity", "GSDT end.");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            timer.schedule(timerTask, 0);

//                                            timer.schedule(new TimerTask() {
//                                                @RequiresApi(api = Build.VERSION_CODES.N)
//                                                @Override
//                                                public void run() {
//
//                                                    try {
//                                                        Log.v("Mainactivity", "GSDT start.");
//                                                        GSDT_Fun();
//                                                        Log.v("Mainactivity", "GSDT end.");
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//
//                                                }
//                                            }, 0); // 延时0秒
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case"Anisotropic":
                                        //调用各向异性滤波，显示滤波结果
                                        try {
                                            Log.v("Mainactivity", "Anisotropic function.");
                                            Toast.makeText(getContext(), "Anisotropic function start~, it will take about 6 mins", Toast.LENGTH_SHORT).show();
//                                            Timer timer = new Timer();
                                            progressBar.setVisibility(View.VISIBLE);
                                            timer = new Timer();
                                            timerTask = new TimerTask() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void run() {
                                                    try {
                                                        Log.v("Mainactivity", "Anisotropic start.");
                                                        Anisotropic();
                                                        Log.v("Mainactivity", "Anisotropic end.");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            timer.schedule(timerTask, 0);
//                                            timer.schedule(new TimerTask() {
//                                                @RequiresApi(api = Build.VERSION_CODES.N)
//                                                @Override
//                                                public void run() {
//
//                                                    try {
//                                                        Log.v("Mainactivity", "Anisotropic start.");
//                                                        Anisotropic();
//                                                        Log.v("Mainactivity", "Anisotropic end.");
//                                                    } catch (Exception e) {
//                                                        e.printStackTrace();
//                                                    }
//
//                                                }
//                                            }, 0); // 延时0秒
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "VoiceChat-1to1":
                                        PopUp_Chat(context);
                                        break;

                                    case "For Developer(Classify)":
                                        FeatureSet();
                                        break;

                                    default:
//                                        Toast.makeText(context, "Default in analysis", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getContext(), "Default in file", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                .show();
    }


    public void PopUp_Chat(Context context){

        new MDDialog.Builder(context)
//              .setContentView(customizedView)
                .setContentView(R.layout.chat_connect)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et1 = (EditText) contentView.findViewById(R.id.channel_edit);
                        EditText et2 = (EditText) contentView.findViewById(R.id.userAccount_edit);
                        String userAccount = getUserAccount(context);

                        if (userAccount.equals("--11--")){
                            userAccount = "";
                        }

                        et1.setText("channel_1");
                        et2.setText(userAccount);

                    }
                })
                .setTitle("Voice Chat")
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
                        EditText et1 = (EditText) contentView.findViewById(R.id.channel_edit);
                        EditText et2 = (EditText) contentView.findViewById(R.id.userAccount_edit);

                        String Channel   = et1.getText().toString();
                        String userAccount   = et2.getText().toString();


                        if( !Channel.isEmpty() && !userAccount.isEmpty() ){
                            VoiceChat(Channel, userAccount);
                            setUserAccount(userAccount, context);

                        }else{
                            PopUp_Chat(context);
                            Toast.makeText(context, "Please make sure all the information is right!!!", Toast.LENGTH_SHORT).show();
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


    private void VoiceChat(String Channel, String userAccount){
//        Intent intent = new Intent(this, VoiceChatViewActivity.class);
//        this.startActivity(intent);

        initAgoraEngineAndJoinChannel(Channel, userAccount);
        fab.setVisibility(View.VISIBLE);
        mRtcEngine.setEnableSpeakerphone(true);

    }

    private void GD_Tracing(){
        Context context = this;

//        Zoom_in.setVisibility(View.VISIBLE);
//        Zoom_out.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        boolean[] flag = {false};

        Log.v("GD_Tracing","popupView.showed");

        timer = new Timer();
        timerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                try {

                    Log.v("Mainactivity", "GSDT start.");
                    GSDT_Fun();
                    Log.v("Mainactivity", "GSDT end.");

//                    Zoom_in.setVisibility(View.INVISIBLE);
//                    Zoom_out.setVisibility(View.INVISIBLE);

//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        timer.schedule(timerTask, 0);

//        GSDT_Fun();
//        Log.v("GD_Tracing","GSDT_Fun");
//        while(!flag[0]) ;


//        Zoom_in.setVisibility(View.INVISIBLE);
//        Zoom_out.setVisibility(View.INVISIBLE);


//        popupView.dismiss();
        Log.v("GD_Tracing","popupView.dismissed");

    }

    public void loadBigData(){

        new XPopup.Builder(this)
                .asCenterList("BigData File",new String[]{"Select File", "Select Block", "Download by http"},
                        new OnSelectListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Select File":
                                        Select_img();
                                        break;

                                    case "Select Block":
                                        Select_Block();
                                        break;

                                    case "Download by http":
                                        downloadFile();
                                        break;
                                }
                            }
                        })
                .show();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Select_Block(){

        String source = getSelectSource(this);
        String ip = "";
        switch (source){
            case "Remote Server Aliyun":
            case "Remote Server SEU":
                if (source.equals("Remote Server Aliyun")){
                    ip = "39.100.35.131";
                }else {
                    ip = "223.3.33.234";
                }
                if (DrawMode){
                    remote_socket.DisConnectFromHost();
                    remote_socket.ConnectServer(ip);
                    remote_socket.LoadNeuronTxt();
                    remote_socket.SelectBlock();
                }else {
                    String arbor_num = getFilename_Remote_Check(context);
                    if (!arbor_num.equals("--11--")){
                        remote_socket.DisConnectFromHost();
                        remote_socket.ConnectServer(ip);
                        remote_socket.LoadNeuronTxt();
                        remote_socket.Send_Arbor_Number(arbor_num);
                    }else {
                        Toast_in_Thread("Select a Img First !");
                    }

                }
                break;
            case "Local Server":
                bigImgReader.PopUp(context);
                break;
            default:
                Toast_in_Thread("Load a File First !");
                break;
        }

//        Context context = this;
//        new XPopup.Builder(this)
////        .maxWidth(400)
////        .maxHeight(1350)
//                .asCenterList("Select block", new String[]{"Remote Server", "Local Server"},
//                        new OnSelectListener() {
//                            @Override
//                            public void onSelect(int position, String text) {
//                                switch (text) {
//                                    case "Remote Server":
////                                        remoteImg.Selectblock(context, false);
//                                        String ip = "39.100.35.131";
//                                        remote_socket.DisConnectFromHost();
//                                        remote_socket.ConnectServer(ip);
//                                        remote_socket.SelectBlock();
//                                        break;
//                                    case "Local Server":
//                                        bigImgReader.PopUp(context);
//                                        break;
//                                    default:
////                                        Toast.makeText(context, "Default in analysis", Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(getContext(), "Default in file", Toast.LENGTH_SHORT).show();
//
//                                }
//                            }
//                        })
//                .show();
    }


    public static void showProgressBar(){
        popupView.show();
    }

    public static void hideProgressBar(){
        puiHandler.sendEmptyMessage(1);
    }

    public static void setFileName(String filepath){
        file_path_temp = filepath;
        puiHandler.sendEmptyMessage(4);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void LoadBigFile_Remote(String filepath){
        myrenderer.SetPath(filepath);

        setFileName(filepath);

        System.out.println("------" + filepath + "------");
        isBigData_Remote = true;
        isBigData_Local = false;
        myGLSurfaceView.requestRender();

        Log.v("MainActivity",remote_socket.getIp());
        if (remote_socket.getIp().equals("39.100.35.131")){
            setSelectSource("Remote Server Aliyun",context);
        } else if(remote_socket.getIp().equals("223.3.33.234")){
            setSelectSource("Remote Server SEU",context);
        }

        SetButtons();

        PullSwc_block_Auto(DrawMode);
        LoadMarker();

        //  for apo sync
//        PullApo_block_Auto();

        if (DrawMode){
            if (!push_info_swc[0].equals("New")){
                PushSWC_Block_Auto(push_info_swc[0], push_info_swc[1]);
            }

            //  for apo sync
//            if (!push_info_apo[0].equals("New")){
//                PushAPO_Block_Auto(push_info_swc[0], push_info_swc[1]);
//            }
        }

    }

    public static void LoadBigFile_Local(String filepath_local){
        System.out.println("------" + filepath_local + "------");
        isBigData_Local = true;
        isBigData_Remote = false;
        String filename = SettingFileManager.getFilename_Local(context);
        String offset = SettingFileManager.getoffset_Local(context, filename);

        int[] index = BigImgReader.getIndex(offset);
        myrenderer.SetPath_Bigdata(filepath_local, index);

        String offset_x = offset.split("_")[0];
        String offset_y = offset.split("_")[1];
        String offset_z = offset.split("_")[2];
        Toast.makeText(getContext(),"Current offset: " + "x: " + offset_x + " y: " + offset_y + " z: " + offset_z, Toast.LENGTH_SHORT).show();
        myGLSurfaceView.requestRender();

        setSelectSource("Local Server",context);
        SetButtons();

    }


    private static void LoadMarker(){

        String filename = getFilename_Remote(context);
        String offset = getoffset_Remote(context, filename);
        int[] index = BigImgReader.getIndex(offset);
        Log.v("LoadMarker",Arrays.toString(index));

        ArrayList<ArrayList<Integer>> marker_list = new ArrayList<ArrayList<Integer>>();
        marker_list = remote_socket.getMarker(index);

        myrenderer.importMarker(marker_list);
        myGLSurfaceView.requestRender();

    }


    public static void Time_Out(){
        hideProgressBar();
        puiHandler.sendEmptyMessage(3);
//        Toast_in_Thread("Time out, please try again!")
//        Toast.makeText(context, "Time out, please try again!", Toast.LENGTH_SHORT).show();
    }


    private static void SetButtons(){
        puiHandler.sendEmptyMessage(2);
    }


    private void hideButtons(){
        if (!ifButtonShowed)
            return;

        ll_top.setVisibility(View.GONE);
        ll_bottom.setVisibility(View.GONE);

        animation_i.setVisibility(View.GONE);
        Rotation_i.setVisibility(View.GONE);
        Hide_i.setVisibility(View.GONE);
        Undo_i.setVisibility(View.GONE);

        if (isBigData_Remote || isBigData_Local){
            navigation_back.setVisibility(View.GONE);
            navigation_down.setVisibility(View.GONE);
            navigation_front.setVisibility(View.GONE);
            navigation_left.setVisibility(View.GONE);
            navigation_right.setVisibility(View.GONE);
            navigation_up.setVisibility(View.GONE);
            navigation_location.setVisibility(View.GONE);

            Zoom_in_Big.setVisibility(View.GONE);
            Zoom_out_Big.setVisibility(View.GONE);

            if (isBigData_Remote) {
                if (!DrawMode){
                    Check_No.setVisibility(View.GONE);
                    Check_Yes.setVisibility(View.GONE);
                    neuron_list.setVisibility(View.GONE);
                }else {
                    sync_push.setVisibility(View.GONE);
                    neuron_list.setVisibility(View.GONE);
                    blue_pen.setVisibility(View.GONE);
                    red_pen.setVisibility(View.GONE);
                }
                    sync_pull.setVisibility(View.GONE);
            }
        }else {
            Zoom_in.setVisibility(View.GONE);
            Zoom_out.setVisibility(View.GONE);
        }


        ifButtonShowed = false;

    }

    private void showButtons(){
        if (ifButtonShowed)
            return;

        ll_top.setVisibility(View.VISIBLE);
        ll_bottom.setVisibility(View.VISIBLE);

        animation_i.setVisibility(View.VISIBLE);
        Rotation_i.setVisibility(View.VISIBLE);
        Hide_i.setVisibility(View.VISIBLE);
        Undo_i.setVisibility(View.VISIBLE);

        if (isBigData_Remote || isBigData_Local){
            navigation_back.setVisibility(View.VISIBLE);
            navigation_down.setVisibility(View.VISIBLE);
            navigation_front.setVisibility(View.VISIBLE);
            navigation_left.setVisibility(View.VISIBLE);
            navigation_right.setVisibility(View.VISIBLE);
            navigation_up.setVisibility(View.VISIBLE);
            navigation_location.setVisibility(View.VISIBLE);

            Zoom_in_Big.setVisibility(View.VISIBLE);
            Zoom_out_Big.setVisibility(View.VISIBLE);

            if (isBigData_Remote) {
                if (!DrawMode){
                    Check_No.setVisibility(View.VISIBLE);
                    Check_Yes.setVisibility(View.VISIBLE);
                    neuron_list.setVisibility(View.GONE);
                }else {
                    sync_push.setVisibility(View.VISIBLE);
                    neuron_list.setVisibility(View.VISIBLE);
                    blue_pen.setVisibility(View.VISIBLE);
                    red_pen.setVisibility(View.VISIBLE);
                }
                sync_pull.setVisibility(View.VISIBLE);
            }

        }else {
            Zoom_in.setVisibility(View.VISIBLE);
            Zoom_out.setVisibility(View.VISIBLE);
        }

        ifButtonShowed = true;
    }


    /**
     * init function for VoiceCall
     */
    private void initAgoraEngineAndJoinChannel(String Channel, String userAccount) {
        initializeAgoraEngine(userAccount);     // Tutorial Step 1
        joinChannel(userAccount, Channel);               // Tutorial Step 2
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    // Tutorial Step 7
    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }

    // Tutorial Step 5
    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }

    // Tutorial Step 3
    public void onEncCallClicked(View view) {
        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;

    }

    // Tutorial Step 1
    private void initializeAgoraEngine(String userAccount) {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            // Sets the channel profile of the Agora RtcEngine.
            // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
            // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

            /**
             * register a account
             */
            mRtcEngine.registerLocalUserAccount(getString(R.string.agora_app_id), userAccount);

        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void joinChannel(String userAccount, String Channel) {
        String accessToken = getString(R.string.agora_access_token);
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
            accessToken = null; // default, no token
        }

        // 使用注册的用户 ID 加入频道
        mRtcEngine.joinChannelWithUserAccount(accessToken, Channel, userAccount);

        showLongToast("You joined Successfully !!!");


//        // Allows a user to join a channel.
//        mRtcEngine.joinChannel(accessToken, "1", "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    // Tutorial Step 3
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    // Tutorial Step 4
    private void onRemoteUserLeft(String userAccount, int reason) {
        showLongToast("user " + userAccount + " left : " + reason);
    }

    // Tutorial Step 4
    private void onRemoteUserJoined(String userAccount) {
        showLongToast("user " + userAccount + " joined !");
    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
//        mRtcEngine.getUserInfoByUid(uid);
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }

    public static void setFilename(String s){
        filename = s;

        filenametext.setText(filename);
        ll_file.setVisibility(View.VISIBLE);

//        lp_undo.setMargins(0, 240, 20, 0);
//        Undo_i.setLayoutParams(lp_undo);

        lp_up_i.setMargins(0, 360, 0, 0);
        navigation_up.setLayoutParams(lp_up_i);

        lp_nacloc_i.setMargins(20, 400, 0, 0);
        navigation_location.setLayoutParams(lp_nacloc_i);

        lp_sync_push.setMargins(0, 400, 20, 0);
        sync_push.setLayoutParams(lp_sync_push);

        lp_sync_pull.setMargins(0, 490, 20, 0);
        sync_pull.setLayoutParams(lp_sync_pull);

        lp_neuron_list.setMargins(0, 630, 20, 0);
        neuron_list.setLayoutParams(lp_neuron_list);


        lp_blue_color.setMargins(0, 540, 20, 0);
        blue_pen.setLayoutParams(lp_blue_color);

        lp_red_color.setMargins(0, 630, 20, 0);
        red_pen.setLayoutParams(lp_red_color);
    }

    private void gameStart(){
        float [] startPoint = new float[]{
                0.5f, 0.5f, 0.5f
        };

        float [] dir = new float[]{
                1, 1, 1
        };

        ArrayList<Integer> sec_proj1 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj2 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj3 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj4 = new ArrayList<Integer>();
        ArrayList<Float> sec_anti = new ArrayList<Float>();

        ArrayList<Float> tangent = myrenderer.tangentPlane(startPoint[0], startPoint[1], startPoint[2], dir[0], dir[1], dir[2], 0, 1);

        System.out.println("TangentPlane:::::");
        System.out.println(tangent.size());

        //然后对三维坐标进行映射
        if (dir[2]==0)
        //先判断切面是不是与XOY面垂直，如果垂直就映射到XOZ平面
        {
            for (int i=0;i<tangent.size();i+=3) {
                if(tangent.get(i)>=0 & tangent.get(i+2)>=0) {

                    sec_proj1.add(i);

                }// 第一象限
                else if(tangent.get(i)<=0 & tangent.get(i+2)>=0) {

                    sec_proj2.add(i);

                }// 第二象限
                else if(tangent.get(i)<=0 & tangent.get(i+2)<=0) {

                    sec_proj3.add(i);

                }// 第三象限
                else if(tangent.get(i)>=0 & tangent.get(i+2)<=0) {

                    sec_proj4.add(i);

                }// 第四象限

            }



            //只用判断大于1的情况，如果没有那就刚好不用管了，如果只有一个元素，那也不用排序了
            if (sec_proj1.size()>1) {
                for (int i=0;i<sec_proj1.size();i++) {
                    for (int j=0;j<sec_proj1.size()-i-1;j++) {
                        if(tangent.get(sec_proj1.get(j))!=0 & tangent.get(sec_proj1.get(j+1))!=0) {
                            if(tangent.get(sec_proj1.get(j)+2)/tangent.get(sec_proj1.get(j)) > tangent.get(sec_proj1.get(j+1)+2)/tangent.get(sec_proj1.get(j+1))) {
                                int temp = sec_proj1.get(j);
                                sec_proj1.set(j, sec_proj1.get(j+1));
                                sec_proj1.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj1.get(j))==0 & tangent.get(sec_proj1.get(j+1))==0) {
                                if(tangent.get(sec_proj1.get(j)+2)<tangent.get(sec_proj1.get(j+1)+2)) {
                                    int temp = sec_proj1.get(j);
                                    sec_proj1.set(j, sec_proj1.get(j+1));
                                    sec_proj1.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj1.get(j))==0) {
                                    int temp = sec_proj1.get(j);
                                    sec_proj1.set(j, sec_proj1.get(j+1));
                                    sec_proj1.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj2.size()>1) {
                for (int i=0;i<sec_proj2.size();i++) {
                    for (int j=0;j<sec_proj2.size()-i-1;j++) {
                        if(tangent.get(sec_proj2.get(j))!=0 & tangent.get(sec_proj2.get(j+1))!=0) {
                            if(tangent.get(sec_proj2.get(j)+2)/tangent.get(sec_proj2.get(j)) > tangent.get(sec_proj2.get(j+1)+2)/tangent.get(sec_proj2.get(j+1))) {
                                int temp = sec_proj2.get(j);
                                sec_proj2.set(j, sec_proj2.get(j+1));
                                sec_proj2.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj2.get(j))==0 & tangent.get(sec_proj2.get(j+1))==0) {
                                if(tangent.get(sec_proj2.get(j)+2)<tangent.get(sec_proj2.get(j+1)+2)) {
                                    int temp = sec_proj2.get(j);
                                    sec_proj2.set(j, sec_proj2.get(j+1));
                                    sec_proj2.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj2.get(j))==0) {
                                    int temp = sec_proj2.get(j);
                                    sec_proj2.set(j, sec_proj2.get(j+1));
                                    sec_proj2.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj3.size()>1) {
                for (int i=0;i<sec_proj3.size();i++) {
                    for (int j=0;j<sec_proj3.size()-i-1;j++) {
                        if(tangent.get(sec_proj3.get(j))!=0 & tangent.get(sec_proj3.get(j+1))!=0) {
                            if(tangent.get(sec_proj3.get(j)+2)/tangent.get(sec_proj3.get(j)) > tangent.get(sec_proj3.get(j+1)+2)/tangent.get(sec_proj3.get(j+1))) {
                                int temp = sec_proj3.get(j);
                                sec_proj3.set(j, sec_proj3.get(j+1));
                                sec_proj3.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj3.get(j))==0 & tangent.get(sec_proj3.get(j+1))==0) {
                                if(tangent.get(sec_proj3.get(j)+2)<tangent.get(sec_proj3.get(j+1)+2)) {
                                    int temp = sec_proj3.get(j);
                                    sec_proj3.set(j, sec_proj3.get(j+1));
                                    sec_proj3.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj3.get(j))==0) {
                                    int temp = sec_proj3.get(j);
                                    sec_proj3.set(j, sec_proj3.get(j+1));
                                    sec_proj3.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj4.size()>1) {
                for (int i=0;i<sec_proj4.size();i++) {
                    for (int j=0;j<sec_proj4.size()-i-1;j++) {
                        if(tangent.get(sec_proj4.get(j))!=0 & tangent.get(sec_proj4.get(j+1))!=0) {
                            if(tangent.get(sec_proj4.get(j)+2)/tangent.get(sec_proj4.get(j)) > tangent.get(sec_proj4.get(j+1)+2)/tangent.get(sec_proj4.get(j+1))) {
                                int temp = sec_proj4.get(j);
                                sec_proj4.set(j, sec_proj4.get(j+1));
                                sec_proj4.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj4.get(j))==0 & tangent.get(sec_proj4.get(j+1))==0) {
                                if(tangent.get(sec_proj4.get(j)+1)<tangent.get(sec_proj4.get(j+1)+1)) {
                                    int temp = sec_proj4.get(j);
                                    sec_proj4.set(j, sec_proj4.get(j+1));
                                    sec_proj4.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj4.get(j))==0) {
                                    int temp = sec_proj4.get(j);
                                    sec_proj4.set(j, sec_proj4.get(j+1));
                                    sec_proj4.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }


        }
        else {
            for (int i=0;i<tangent.size();i+=3) {
                if(tangent.get(i)>=0 & tangent.get(i+1)>=0) {

                    sec_proj1.add(i);

                }// 第一象限
                else if(tangent.get(i)<=0 & tangent.get(i+1)>=0) {

                    sec_proj2.add(i);

                }// 第二象限
                else if(tangent.get(i)<=0 & tangent.get(i+1)<=0) {

                    sec_proj3.add(i);

                }// 第三象限
                else if(tangent.get(i)>=0 & tangent.get(i+1)<=0) {

                    sec_proj4.add(i);

                }// 第四象限

            }
        }





        //只用判断大于1的情况，如果没有那就刚好不用管了，如果只有一个元素，那也不用排序了
        if (sec_proj1.size()>1) {
            for (int i=0;i<sec_proj1.size();i++) {
                for (int j=0;j<sec_proj1.size()-i-1;j++) {
                    if(tangent.get(sec_proj1.get(j))!=0 & tangent.get(sec_proj1.get(j+1))!=0) {
                        if(tangent.get(sec_proj1.get(j)+1)/tangent.get(sec_proj1.get(j)) > tangent.get(sec_proj1.get(j+1)+1)/tangent.get(sec_proj1.get(j+1))) {
                            int temp = sec_proj1.get(j);
                            sec_proj1.set(j, sec_proj1.get(j+1));
                            sec_proj1.set(j+1, temp); //冒泡排序
                        }
                    }
                    else {
                        if(tangent.get(sec_proj1.get(j))==0 & tangent.get(sec_proj1.get(j+1))==0) {
                            if(tangent.get(sec_proj1.get(j)+1)<tangent.get(sec_proj1.get(j+1)+1)) {
                                int temp = sec_proj1.get(j);
                                sec_proj1.set(j, sec_proj1.get(j+1));
                                sec_proj1.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj1.get(j))==0) {
                                int temp = sec_proj1.get(j);
                                sec_proj1.set(j, sec_proj1.get(j+1));
                                sec_proj1.set(j+1, temp); //冒泡排序
                            }
                        }
                    }
                }
            }
        }

        if (sec_proj2.size()>1) {
            for (int i=0;i<sec_proj2.size();i++) {
                for (int j=0;j<sec_proj2.size()-i-1;j++) {
                    if(tangent.get(sec_proj2.get(j))!=0 & tangent.get(sec_proj2.get(j+1))!=0) {
                        if(tangent.get(sec_proj2.get(j)+1)/tangent.get(sec_proj2.get(j)) > tangent.get(sec_proj2.get(j+1)+1)/tangent.get(sec_proj2.get(j+1))) {
                            int temp = sec_proj2.get(j);
                            sec_proj2.set(j, sec_proj2.get(j+1));
                            sec_proj2.set(j+1, temp); //冒泡排序
                        }
                    }
                    else {
                        if(tangent.get(sec_proj2.get(j))==0 & tangent.get(sec_proj2.get(j+1))==0) {
                            if(tangent.get(sec_proj2.get(j)+1)<tangent.get(sec_proj2.get(j+1)+1)) {
                                int temp = sec_proj2.get(j);
                                sec_proj2.set(j, sec_proj2.get(j+1));
                                sec_proj2.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj2.get(j))==0) {
                                int temp = sec_proj2.get(j);
                                sec_proj2.set(j, sec_proj2.get(j+1));
                                sec_proj2.set(j+1, temp); //冒泡排序
                            }
                        }
                    }
                }
            }
        }

        if (sec_proj3.size()>1) {
            for (int i=0;i<sec_proj3.size();i++) {
                for (int j=0;j<sec_proj3.size()-i-1;j++) {
                    if(tangent.get(sec_proj3.get(j))!=0 & tangent.get(sec_proj3.get(j+1))!=0) {
                        if(tangent.get(sec_proj3.get(j)+1)/tangent.get(sec_proj3.get(j)) > tangent.get(sec_proj3.get(j+1)+1)/tangent.get(sec_proj3.get(j+1))) {
                            int temp = sec_proj3.get(j);
                            sec_proj3.set(j, sec_proj3.get(j+1));
                            sec_proj3.set(j+1, temp); //冒泡排序
                        }
                    }
                    else {
                        if(tangent.get(sec_proj3.get(j))==0 & tangent.get(sec_proj3.get(j+1))==0) {
                            if(tangent.get(sec_proj3.get(j)+1)<tangent.get(sec_proj3.get(j+1)+1)) {
                                int temp = sec_proj3.get(j);
                                sec_proj3.set(j, sec_proj3.get(j+1));
                                sec_proj3.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj3.get(j))==0) {
                                int temp = sec_proj3.get(j);
                                sec_proj3.set(j, sec_proj3.get(j+1));
                                sec_proj3.set(j+1, temp); //冒泡排序
                            }
                        }
                    }
                }
            }
        }

        if (sec_proj4.size()>1) {
            for (int i=0;i<sec_proj4.size();i++) {
                for (int j=0;j<sec_proj4.size()-i-1;j++) {
                    if(tangent.get(sec_proj4.get(j))!=0 & tangent.get(sec_proj4.get(j+1))!=0) {
                        if(tangent.get(sec_proj4.get(j)+1)/tangent.get(sec_proj4.get(j)) > tangent.get(sec_proj4.get(j+1)+1)/tangent.get(sec_proj4.get(j+1))) {
                            int temp = sec_proj4.get(j);
                            sec_proj4.set(j, sec_proj4.get(j+1));
                            sec_proj4.set(j+1, temp); //冒泡排序
                        }
                    }
                    else {
                        if(tangent.get(sec_proj4.get(j))==0 & tangent.get(sec_proj4.get(j+1))==0) {
                            if(tangent.get(sec_proj4.get(j)+1)<tangent.get(sec_proj4.get(j+1)+1)) {
                                int temp = sec_proj4.get(j);
                                sec_proj4.set(j, sec_proj4.get(j+1));
                                sec_proj4.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj4.get(j))==0) {
                                int temp = sec_proj4.get(j);
                                sec_proj4.set(j, sec_proj4.get(j+1));
                                sec_proj4.set(j+1, temp); //冒泡排序
                            }
                        }
                    }
                }
            }
        }


        for(int i=0;i<sec_proj1.size();i++) {
            sec_anti.add(tangent.get(sec_proj1.get(i)));
            sec_anti.add(tangent.get(sec_proj1.get(i)+1));
            sec_anti.add(tangent.get(sec_proj1.get(i)+2));
        }
        for(int i=0;i<sec_proj2.size();i++) {
            sec_anti.add(tangent.get(sec_proj2.get(i)));
            sec_anti.add(tangent.get(sec_proj2.get(i)+1));
            sec_anti.add(tangent.get(sec_proj2.get(i)+2));
        }
        for(int i=0;i<sec_proj3.size();i++) {
            sec_anti.add(tangent.get(sec_proj3.get(i)));
            sec_anti.add(tangent.get(sec_proj3.get(i)+1));
            sec_anti.add(tangent.get(sec_proj3.get(i)+2));
        }
        for(int i=0;i<sec_proj4.size();i++) {
            sec_anti.add(tangent.get(sec_proj4.get(i)));
            sec_anti.add(tangent.get(sec_proj4.get(i)+1));
            sec_anti.add(tangent.get(sec_proj4.get(i)+2));
        }

        float [] vertexPoints = new float[sec_anti.size()];
        for (int i = 0; i < sec_anti.size(); i++){

            vertexPoints[i] = sec_anti.get(i);
            System.out.print(vertexPoints[i]);
            System.out.print(" ");
            if (i % 3 == 2){
                System.out.print("\n");
            }
        }

        boolean gameSucceed = myrenderer.driveMode(vertexPoints, dir);
        if (!gameSucceed){
            Toast.makeText(context, "wrong vertex to draw", Toast.LENGTH_SHORT);
        } else {
            myGLSurfaceView.requestRender();
        }
    }

}
