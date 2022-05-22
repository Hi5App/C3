package com.penglab.hi5.core;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.chaychan.viewlib.PowerfulEditText;
import com.kongqw.rockerlibrary.view.RockerView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ui.imageview.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.chat.ChatActivity;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.chat.nim.session.extension.InviteAttachment;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.basic.ReceiveMsgInterface;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.core.collaboration.connector.ServerConnector;
import com.penglab.hi5.core.collaboration.service.BasicService;
import com.penglab.hi5.core.collaboration.service.CollaborationService;
import com.penglab.hi5.core.collaboration.service.ManageService;
import com.penglab.hi5.core.fileReader.annotationReader.AnoReader;
import com.penglab.hi5.core.fileReader.annotationReader.ApoReader;
import com.penglab.hi5.core.fileReader.imageReader.BigImgReader;
import com.penglab.hi5.core.game.AchievementPopup;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.data.dataStore.PreferenceLogin;
import com.penglab.hi5.data.dataStore.SettingFileManager;
import com.warkiz.widget.IndicatorSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import cn.carbs.android.library.MDDialog;
import tv.danmaku.ijk.media.example.application.Settings;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.data.dataStore.SettingFileManager.getFilename_Remote;
import static com.penglab.hi5.data.dataStore.SettingFileManager.getoffset_Remote;
import static com.penglab.hi5.data.dataStore.SettingFileManager.setSelectSource;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

//import com.penglab.hi5.chat.agora.AgoraService;
//import com.penglab.hi5.chat.agora.message.AgoraMsgManager;


public class S2Activity extends BaseActivity implements ReceiveMsgInterface {
    private static final String TAG = "S2Activity";


    private Timer timer = null;
    private TimerTask timerTask;

    private static Bitmap bitmap2D = null;
    private static MyGLSurfaceView myS2GLSurfaceView;
    private static MyRenderer myS2renderer;
    private static Context S2Context;
    //ServerConnector ServerConnectorForScope;

    private String filepath = "";
    private boolean ifZooming = false;
    private boolean ifDeletingMultiMarker = false;
    private boolean ifChangeMarkerType = false;
    private boolean ifPainting = false;
    private boolean ifPoint = false;
    private boolean ifImport = false;
    private boolean ifAnalyze = false;
    private boolean ifUpload = false;
    private boolean ifDeletingMarker = false;
    private boolean ifGetRoiPoint = false;
    private boolean ifDeletingLine = false;
    private boolean ifSpliting = false;
    private boolean ifChangeLineType = false;
    private boolean ifSwitch = false;
    private boolean ifLoadLocal = false;
    private boolean ifButtonShowed = true;
    private boolean ifAnimation = false;
    private boolean ifSettingROI = false;
    private boolean isforceupdate = false;

    private static boolean isZscanSeries = false;

    private boolean[] temp_mode = new boolean[8];
    private float[] locationFor2dImg = new float[2];


    private View pvcamModeView;
    private View pvcamRtmpModeView;

    private static Button Zslice_up;
    private static Button Zslice_down;

    private static Button Zoom_in_Big;
    private static ImageButton si_logo;
    private static ImageButton Hide_i;


    private static Button Zoom_out_Big;
    private static ImageButton zseries_scan;
    private static ImageButton Camera_open;
    private static ImageButton S2start;
    private static ImageButton imgs2stack;


    private Button Switch;

    private ImageButton animation_i;
    private ImageButton draw_i;

    private static TextView filenametext;
    private RockerView s2rocekerview_xy;
    private RockerView s2rocekerview_z;
    private static ImageView PV_imageView;
    private IjkVideoView mVideoView;
    private Settings mSettings;
    private AndroidMediaController mMediaController;
    private TableLayout mHudView;
    private static ImageButton MoveXtop;
    private static ImageButton navigation_left;
    private static ImageButton navigation_right;
    private static ImageButton navigation_up;
    private static ImageButton navigation_down;
    private static ImageButton navigation_location;
    private static ImageButton eswc_sync;
    private static ImageButton ROI_i;
    private static Button navigation_front;
    private static Button navigation_back;
//    private static Button blue_pen;
//    private static Button red_pen;

    private static Button res_list;
    private static ImageButton user_list;
    private static ImageButton room_id;


//    private static ImageButton neuron_list;
//    private static ImageButton sync_push;
//    private static ImageButton sync_pull;

    private FrameLayout ll;

    private FrameLayout.LayoutParams lp_left_i;
    private FrameLayout.LayoutParams lp_right_i;
    private static FrameLayout.LayoutParams lp_up_i;
    private FrameLayout.LayoutParams lp_down_i;
    private FrameLayout.LayoutParams lp_front_i;
    private FrameLayout.LayoutParams lp_back_i;
    private static FrameLayout.LayoutParams lp_nacloc_i;
    private static FrameLayout.LayoutParams lp_sync_i;

    private static FrameLayout.LayoutParams lp_res_list;
    private FrameLayout.LayoutParams lp_animation_i;

    private static FrameLayout.LayoutParams lp_x_pos;
    private static FrameLayout.LayoutParams lp_y_pos;
    private static FrameLayout.LayoutParams lp_z_pos;

    private static FrameLayout.LayoutParams lp_room_id;
    private static FrameLayout.LayoutParams lp_user_list;


    private BigImgReader bigImgS2Reader;


    private LinearLayout ll_top;
    private LinearLayout ll_bottom;
    private static LinearLayout ll_file;


    private List<double[]> fl;

    private static boolean isBigData_Remote;
    private static boolean isVirtualScope;
    private static boolean isCheckmode;
    private static boolean isBigData_Local;
    private static boolean isS2Start = false;
    private static boolean isCamera;
    private static boolean ifTouchCamera = false;

    private static ProgressBar progressBar;
    private static ProgressDialog progressDialog_zscan;
    private static ProgressDialog progressDialog_loadimg;

    private CircleImageView wave;


    // 读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private static final int TOAST_INFO_STATIC = 5;

    //    private int Paintmode = 0;
    private ArrayList<Float> lineDrawed = new ArrayList<Float>();


    private static BasePopupView popupView;
    private static BasePopupView popupViewSync;

    private static final int animation_id = 0;
    private int rotation_speed = 36;


    private static String s2workstate = "";
    private static String filename = "";
    private static String s2filename = "";
    private static String s2EswcPath = "";
    private String S2CheckImgpath = "";

    private enum PenColor {
        WHITE, BLACK, RED, BLUE, PURPLE, CYAN, YELLOW, GREEN
    }


    private BasePopupView drawPopupView;

    private static boolean ifGame = false;

    HashMap<Integer, String> User_Map = new HashMap<Integer, String>();

    public static String USERNAME = "username";

    public static String username;


    //    private boolean mBoundAgora = false;
    private boolean mBoundManagement = false;
    private boolean mBoundCollaboration = false;
    private boolean mBounds2 = false;

    private int count = 0;

    private static String conPath = "";


    public static boolean firstLoad = true;

    private boolean ifgetTest = false;
    private boolean ifSmartControl = false;


    private TextView x_pos_Text;
    private TextView y_pos_Text;
    private TextView z_pos_Text;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRecMessage(String msg) {


        if (msg.startsWith("TestSocketConnection")) {
            //serverConnectorForScope.getInstance().sendMsg("HeartBeat");
        } else {
            Log.e(TAG, "onRecMessage()  " + msg);

        }


        /*
        select file
         */
        if (msg.startsWith("GETFILELIST:")) {
            LoadFiles(msg.split(":")[1]);
        }


        if (msg.startsWith("getimglist:")) {
            LoadFiles(msg.split(":")[1]);
        }

        if (msg.startsWith("img2stack:")) {
            LoadFiles(msg.split(":")[1]);
        }

        if (msg.startsWith("s2start:")) {

            if (msg.endsWith("scan on")) {
                Toast_in_Thread("scope scan on!");
                Log.e(TAG, "scope scan on!!");
            } else if (msg.endsWith("scan off")) {
                Toast_in_Thread("scope scan off!");
                Log.e(TAG, "scope scan off!!");
            }

            Log.e(TAG, "s2start:()  " + msg);


        }


        if (msg.startsWith("zcan:done")) {

            Toast_in_Thread("img stack is done!");
            Log.e(TAG, "img stack is done:()  " + msg);
            ServerConnector.getInstance().sendMsg("getzstack:");
        }

        if (msg.startsWith("test:")) {

            Toast_in_Thread("Connect to scope successfully!");
            Log.e(TAG, "s2start:()  " + msg);
            ifgetTest = true;
        }


        if (msg.startsWith("s2_move:")) {
            // loadBigDataImg(msg.split(":")[1]);


            Log.e(TAG, "s2_move:");
            if (msg.endsWith("X")) {
                Toast_in_Thread("Stage X is out of max range!");
                Log.e(TAG, "s2_move:Stage X is out of max range!");
            } else if (msg.endsWith("Y")) {
                Toast_in_Thread("Stage Y is out of max range!");
                Log.e(TAG, "s2_move:Stage Y is out of max range!");
            } else if (msg.endsWith("Z")) {
                Toast_in_Thread("Stage Z is out of max range!");
                Log.e(TAG, "s2_move:Stage Z is out of max range!");
            }
        }


        if (msg.startsWith("ZScan:")) {
            if (msg.endsWith("ZStart")) {
                Toast_in_Thread("ZScan first img has been set up!");
                Log.e(TAG, "ZScan first img has been set up!");
            } else if (msg.endsWith("ZStop")) {
                Toast_in_Thread("ZScan last img has been set up!");
                Log.e(TAG, "ZScan last img has been set up!");
            } else if (msg.endsWith("Zslicesize1")) {
                Toast_in_Thread("ZScan Zslicesize1 has been set up!");
                Log.e(TAG, "ZScan Zslicesize1 has been set up!");
            } else if (msg.endsWith("start")) {
                Toast_in_Thread("ZScan on!");
                Log.e(TAG, "ZScan on!");
            }
        }


//unused!
        if (msg.startsWith("stagePos:")) {
            // loadBigDataImg(msg.split(":")[1]);
            String msgs = msg.split(":")[1];
            String msgss = msg.split(":")[2];

            Log.e(TAG, "stagePos:  " + msgs);
            Log.e(TAG, "stagePos:  " + msgss);
            if (msgs.startsWith("x")) {
                x_pos_Text.setText(msgss);
            } else if (msgs.startsWith("y")) {
                y_pos_Text.setText(msgss);
            } else if (msgs.startsWith("z")) {
                z_pos_Text.setText(msgss);
            }


            // Log.e(TAG,"s2start:()  " + msg.split(":")[1]);

        }

        if (msg.startsWith("GetPos:")) {
            // loadBigDataImg(msg.split(":")[1]);
            String msgs = msg.split(":")[1];
            String msgx = msgs.split(" ")[0];
            String msgy = msgs.split(" ")[1];
            String msgz = msgs.split(" ")[2];

            Log.e(TAG, "XstagePos:" + msgx);
            Log.e(TAG, "YstagePos:" + msgy);
            Log.e(TAG, "ZstagePos:" + msgz);

            x_pos_Text.setText(msgx);
            y_pos_Text.setText(msgy);
            z_pos_Text.setText(msgz);


            // Log.e(TAG,"s2start:()  " + msg.split(":")[1]);

        }


        if (msg.startsWith("File:")) {
            if (msg.endsWith(".v3draw")) {

                Log.e(TAG, "File: .v3draw");
                loadBigDataImg(msg.split(":")[1]);

                //PV_imageView.setImageBitmap(bitmap); //设置Bitmap
            }
            if (msg.endsWith(".v3dpbd")) {

                Log.e(TAG, "File: .v3dpbd");
                loadBigDataImg(msg.split(":")[1]);

                //PV_imageView.setImageBitmap(bitmap); //设置Bitmap
            }
            if (msg.endsWith(".jpg")) {

                Log.e(TAG, "File: .jpg");
                loadBigDataImg(msg.split(":")[1]);

            }
            if (msg.endsWith(".tif")) {

                Log.e(TAG, "File: .tif");
                loadBigDataImg(msg.split(":")[1]);

            }
            if (msg.endsWith(".tiff")) {

                Log.e(TAG, "File: .tiff");
                loadBigDataImg(msg.split(":")[1]);

            }
            if (msg.endsWith(".jpeg")) {

                Log.e(TAG, "File: .jpeg");
                loadBigDataImg(msg.split(":")[1]);

            }
            if (msg.endsWith(".png")) {

                Log.e(TAG, "File: .png");
                loadBigDataImg(msg.split(":")[1]);

            }
            if (msg.endsWith(".swc")) {

                Log.e(TAG, "File: .swc");
                loadBigDataSwc(msg.split(":")[1]);

            }


        }


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRecBinData(String msg, byte[] a) {
        if (msg.startsWith("pvcam")) {

            Log.e(TAG, "onRecBinData" + msg);
            //loadPvcamData(a);
            bitmap2D = BitmapFactory.decodeByteArray(a, 0, a.length);
            puiHandler.sendEmptyMessage(11);

        }
    }

    private void postMessage(String message) {
        Message msg = Message.obtain();
        msg.what = 8;
        msg.obj = message;
        puiHandler.sendMessage(msg);
    }


    @SuppressLint("HandlerLeak")
    private static Handler puiHandler = new Handler() {
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    popupView.show();
                    Activity activity = getActivityFromContext(S2Context);
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;

                case 1:
                    popupView.dismiss();
                    Activity activity_2 = getActivityFromContext(S2Context);
                    activity_2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;

                case 2:
                    setButtonsAndState();
                    if (isBigData_Local) {
                        String filename = SettingFileManager.getFilename_Local(context);
                        String offset = SettingFileManager.getoffset_Local(context, filename);

                        String offset_x = offset.split("_")[0];
                        String offset_y = offset.split("_")[1];
                        String offset_z = offset.split("_")[2];
                        Toast.makeText(getContext(), "Current offset: " + "x: " + offset_x + " y: " + offset_y + " z: " + offset_z, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 3:
                    Toast.makeText(context, "Time out, please try again!", Toast.LENGTH_SHORT).show();
                    break;

                case 4:
                    setFileName(Communicator.BrainNum);
                    break;

                case 5:
                    String Toast_msg = msg.getData().getString("Toast_msg");
                    Toast.makeText(getContext(), Toast_msg, Toast.LENGTH_SHORT).show();
                    break;

                case 6:
                    progressBar.setVisibility(View.GONE);
                    break;

                case 7:
                    setFileName();
                    break;

                case 8:
                    Toast_in_Thread_static((String) msg.obj);
                    break;

                case 9:
                    popupViewSync.show();
                    break;

                case 10:
                    popupViewSync.dismiss();
                    break;
                case 11:
                    PV_imageView.setImageBitmap(bitmap2D); //设置Bitmap
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * The onCreate Function
     *
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "------------------ onCreate ------------------");

        // set layout
        setContentView(R.layout.activity_s2);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        S2Context = this;

        isVirtualScope = false;
        isBigData_Remote = false;
        isBigData_Local = false;

        popupView = new XPopup.Builder(this)
                .asLoading("Downloading......");

        popupViewSync = new XPopup.Builder(this)
                .asLoading("Syncing......");


        Intent intent = getIntent();
        String OOM = intent.getStringExtra(MyRenderer.OUT_OF_MEMORY);
        username = intent.getStringExtra(USERNAME);

        if (OOM != null)
            Toast.makeText(this, OOM, Toast.LENGTH_SHORT).show();

        wave = new CircleImageView(getContext());
        wave.setScaleType(ImageView.ScaleType.CENTER_CROP);


        myS2renderer = new MyRenderer(this);
        myS2GLSurfaceView = new MyGLSurfaceView(this);


        initButtons();


        // set contrast  & DownSample Mode
        myS2renderer.setIfNeedDownSample(preferenceSetting.getDownSampleMode());
        myS2renderer.resetContrast(preferenceSetting.getContrast());


        // Set the permission for user
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        PreferenceLogin.init(getApplicationContext());

        myS2GLSurfaceView.requestRender();
        bigImgS2Reader = new BigImgReader();


        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
        params.gravity = Gravity.CENTER;
        this.addContentView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        progressDialog_zscan = new ProgressDialog(S2Activity.this);
        progressDialog_zscan.setTitle("Loading Image Scan.....");
        progressDialog_zscan.setMessage("Waiting");
        progressDialog_zscan.setCancelable(true);


        progressDialog_loadimg = new ProgressDialog(S2Activity.this);
        progressDialog_loadimg.setTitle("Loading Image .....");
        progressDialog_loadimg.setMessage("Wait about 1 minute");
        progressDialog_loadimg.setCancelable(true);

        initDir();


        initServerConnector();
        initService();


        //s2initialization();
        s2Confirm_Password();

        /*
        init database for score module
         */
        //       initDataBase();


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // getScore();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // getLeaderBoard();
            }
        }, 1 * 1000);

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    //renderer 的生存周期和activity保持一致
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

//        Intent bgmIntent = new Intent(this, MusicServer.class);
//        stopService(bgmIntent);

        // Score score = Score.getInstance();
        //setScore(score.getScore());

//        if ((mBoundAgora)){
//            Log.e(TAG,"unbind agora service !");
//            unbindService(connection_agora);
//            Intent agoraServiceIntent = new Intent(this, AgoraService.class);
//            stopService(agoraServiceIntent);
//        }


        if (mBoundManagement) {
            Log.e(TAG, "unbind management service !");
            ManageService.setStop(true);
            unbindService(connection_management);
            Intent manageServiceIntent = new Intent(this, ManageService.class);
            stopService(manageServiceIntent);

            ServerConnector.getInstance().releaseConnection(false);
        }
        if (mBoundCollaboration) {
            Log.e(TAG, "unbind collaboration service !");
            CollaborationService.setStop(true);
            unbindService(connection_collaboration);
            Intent collaborationServiceIntent = new Intent(this, CollaborationService.class);
            stopService(collaborationServiceIntent);

            MsgConnector.getInstance().releaseConnection(false);
        }


        ll_top = null;
        ll_bottom = null;
        ll_file = null;
        lineDrawed = null;
        popupView = null;
        popupViewSync = null;
        drawPopupView = null;

        si_logo = null;
        Hide_i = null;

        bitmap2D = null;
        pvcamModeView = null;

        progressBar = null;
        progressDialog_zscan = null;
        progressDialog_loadimg = null;


        Zslice_up = null;
        Zslice_down = null;

        Zoom_in_Big = null;
        bigImgS2Reader = null;
        Zoom_out_Big = null;
        zseries_scan = null;
        Camera_open = null;
        S2start = null;
        imgs2stack = null;

        Switch = null;

        animation_i = null;
        draw_i = null;

        filenametext = null;
        s2rocekerview_xy = null;
        s2rocekerview_z = null;
        PV_imageView = null;
        mVideoView = null;
        mHudView = null;
        mSettings = null;
        mMediaController = null;
        MoveXtop = null;
        navigation_left = null;
        navigation_right = null;
        navigation_up = null;
        navigation_down = null;
        navigation_location = null;
        eswc_sync = null;
        ROI_i = null;
        navigation_front = null;
        navigation_back = null;
//    private static Button blue_pen;
//    private static Button red_pen;

        res_list = null;
        user_list = null;
        room_id = null;

        pvcamModeView = null;
        pvcamRtmpModeView = null;
        myS2GLSurfaceView = null;
        myS2renderer = null;
        S2Context = null;
        ifTouchCamera=false;
        //serverConnector.closeSender();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause start");
        myS2GLSurfaceView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume start");
        Log.v("Path", filepath);
        myS2GLSurfaceView.onResume();

    }

    @Override
    protected void onStop() {
//        Intent bgmIntent = new Intent(S2Activity.this, MusicServer.class);
//        stopService(bgmIntent);
        super.onStop();
        if (ifTouchCamera) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
            ifTouchCamera=false;
            IjkMediaPlayer.native_profileEnd();
        }

    }

    @Override
    protected void onRestart() {
//        initMusicService();
        super.onRestart();
    }

    /**
     * quick start for S2Activity
     *
     * @param context
     */

    public static void start(Context context) {
        start(context, null);
    }


    /**
     * quick start for S2Activity
     *
     * @param context
     * @param extras
     */
    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, S2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }


    public static void actionStart(Context context, String username) {
        Intent intent = new Intent(context, S2Activity.class);
        intent.putExtra(USERNAME, username);
        context.startActivity(intent);
    }

    private void initBasicLayout() {
        ll = (FrameLayout) findViewById(R.id.container1);
        ll.addView(myS2GLSurfaceView);

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
        this.addContentView(ll_bottom, lp);
        ll_bottom.setLayoutParams(lp);

        hs_top.addView(ll_top);
    }

    private void initPvcamRtmplayout() {
        if (isCamera) {


            hideButtons();
            ll.setVisibility(View.GONE);

            LinearLayout.LayoutParams lp4BigDataMode = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            // load layout view
            mSettings = new Settings(this);
            String mVideoPath = "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
            String mPvcamPath = "rtmp://139.155.28.154:8513/stream/123";

            mMediaController = new AndroidMediaController(this, false);


            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");

            pvcamRtmpModeView = getLayoutInflater().inflate(R.layout.activity_s2_rtmp, null);
            this.addContentView(pvcamRtmpModeView, lp4BigDataMode);

            // MoveXtop = findViewById(R.id.pv_top);
            //ImageButton MoveX = findViewById(R.id.zoomOut);
            mVideoView = (IjkVideoView) findViewById(R.id.si_videoView);
            mHudView = (TableLayout) findViewById(R.id.hud_s2_view);
            mVideoView.setMediaController(mMediaController);
            mVideoView.setHudView(mHudView);

            RockerView s2rocekerview_xy = (RockerView) findViewById(R.id.s2rockerView_xy);

            Toolbar toolbar = findViewById(R.id.toolbar2);
            TextView mLogLeft = findViewById(R.id.textViews_xy);
            TextView mLogright = findViewById(R.id.textViews_z);
            setSupportActionBar(toolbar);

            s2rocekerview_xy.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);


            if (TextUtils.isEmpty(mPvcamPath)) {
                Toast.makeText(this,
                        "No Video Found! Press Back Button To Exit",
                        Toast.LENGTH_LONG).show();
            } else {
                mVideoView.setVideoURI(Uri.parse(mPvcamPath));
                mVideoView.start();
            }

//            MoveXtop.setOnClickListener(new Button.OnClickListener() {
//                public void onClick(View v) {
//
//                    Log.e(TAG, "onClick: MoveXtop");
//                    //Switch();
//                }
//            });


//            if (s2rocekerview_xy != null) {
//                s2rocekerview_xy.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
//                    @Override
//                    public void onStart() {
//
//                        mLogLeft.setText("XY Stage");
//
//                    }
//
//                    @Override
//                    public void angle(double angle) {
//
//                        Block_navigate("Left");
//                        mLogLeft.setText("Angle : " + angle);
//
//                    }
//
//                    @Override
//                    public void onFinish() {
//
//                        mLogLeft.setText("XY Stage");
//                    }
//                });
//            }
//
            if (s2rocekerview_xy != null) {
                s2rocekerview_xy.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
                s2rocekerview_xy.setOnShakeListener(RockerView.DirectionMode.DIRECTION_4_ROTATE_45, new RockerView.OnShakeListener() {
                    @Override
                    public void onStart() {
                        mLogLeft.setText("XY Stage");
                    }

                    @Override
                    public void direction(RockerView.Direction direction) {
                        mLogLeft.setText("Direction : " + getDirection(direction));

                        pvRockerControl(false, direction);

                    }

                    @Override
                    public void onFinish() {
                        mLogLeft.setText("XY Stage");
                    }
                });
            }
            s2rocekerview_z = (RockerView) findViewById(R.id.s2rockerView_z);
            if (s2rocekerview_z != null) {
                s2rocekerview_z.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
                s2rocekerview_z.setOnShakeListener(RockerView.DirectionMode.DIRECTION_2_VERTICAL, new RockerView.OnShakeListener() {
                    @Override
                    public void onStart() {
                        mLogright.setText("Z Stage");
                    }

                    @Override
                    public void direction(RockerView.Direction direction) {

                        mLogright.setText("Direction : " + getDirection(direction));
                        pvRockerControl(true, direction);
                    }

                    @Override
                    public void onFinish() {
                        mLogright.setText("Z Stage");
                    }
                });
            }

            // MoveXtop.setOnClickListener(this::MoveXtop());
            //zoomOut.setOnClickListener(v -> annotationGLSurfaceView.zoomOut());

        } else {
            pvcamRtmpModeView.setVisibility(View.VISIBLE);
        }
    }

    private String getDirection(RockerView.Direction direction) {
        String message = null;
        switch (direction) {
            case DIRECTION_LEFT:
                message = "左";
                break;
            case DIRECTION_RIGHT:
                message = "右";
                break;
            case DIRECTION_UP:
                message = "上";
                break;
            case DIRECTION_DOWN:
                message = "下";
                break;
            case DIRECTION_UP_LEFT:
                message = "左上";
                break;
            case DIRECTION_UP_RIGHT:
                message = "右上";
                break;
            case DIRECTION_DOWN_LEFT:
                message = "左下";
                break;
            case DIRECTION_DOWN_RIGHT:
                message = "右下";
                break;
            default:
                break;
        }
        return message;
    }

    private void pvRockerControl(boolean ifz, RockerView.Direction direction) {
        String message = null;
        String[] Direction = {"Left", "Right", "Top", "Bottom", "Front", "Back", "Lefttop", "leftbottom", "Righttop", "Rightbottom"};
        switch (direction) {
            case DIRECTION_LEFT:
                message = Direction[0];
                break;
            case DIRECTION_RIGHT:
                message = Direction[1];
                break;
            case DIRECTION_UP:
                if (ifz) {
                    message = Direction[4];
                } else {
                    message = Direction[2];
                }
                break;
            case DIRECTION_DOWN:
                if (ifz) {
                    message = Direction[5];
                } else {
                    message = Direction[3];
                }
                break;
            case DIRECTION_UP_LEFT:
                message = Direction[6];
                break;
            case DIRECTION_UP_RIGHT:
                message = Direction[7];
                break;
            case DIRECTION_DOWN_LEFT:
                message = Direction[8];
                break;
            case DIRECTION_DOWN_RIGHT:
                message = Direction[9];
                break;
            default:
                break;
        }

        Block_navigate(message);
    }

    private void initPvcamLayout() {
        if (isCamera) {
            // load layout view
            hideButtons();
            ll.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp4BigDataMode = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            pvcamModeView = getLayoutInflater().inflate(R.layout.activity_s2_pvcam, null);
            this.addContentView(pvcamModeView, lp4BigDataMode);

            MoveXtop = findViewById(R.id.pv_top);
            //ImageButton MoveX = findViewById(R.id.zoomOut);
            PV_imageView = (ImageView) findViewById(R.id.imageView2);


            Toolbar toolbar = findViewById(R.id.toolbar2);

            setSupportActionBar(toolbar);

            MoveXtop.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {

                    Log.e(TAG, "onClick: MoveXtop");
                    //Switch();
                }
            });

            // MoveXtop.setOnClickListener(this::MoveXtop());
            //zoomOut.setOnClickListener(v -> annotationGLSurfaceView.zoomOut());

        } else {
            pvcamModeView.setVisibility(View.VISIBLE);
        }
    }

    /*
     * init buttons
     */
    private void initButtons() {
        /*
        basic Layout ------------------------------------------------------------------------------------------------------------------------
         */

        initBasicLayout();


        /*
        init buttons ------------------------------------------------------------------------------------------------------------------------
         */
//        si_logo = new ImageButton(this);
//        si_logo.setImageResource(R.drawable.si_logo);
//        si_logo.setBackgroundResource(R.drawable.circle_normal);

//        eswc_sync = new ImageButton(this);
//        eswc_sync.setImageResource(R.drawable.ic_baseline_autorenew_24);


        Hide_i = new ImageButton(this);
        Hide_i.setImageResource(R.drawable.ic_not_hide);
        Hide_i.setBackgroundResource(R.drawable.circle_normal);

        Zoom_in_Big = new Button(this);
        Zoom_in_Big.setText("+");
//        Zoom_in_Big = new Button(this);
//        Zoom_in_Big.setText("+");

        Zoom_out_Big = new Button(this);
        Zoom_out_Big.setText("-");
//        Zoom_out_Big = new Button(this);
//        Zoom_out_Big.setText("-");

        Zslice_up = new Button(this);
        Zslice_up.setText("front");

        Zslice_down = new Button(this);
        Zslice_down.setText("back");


        Camera_open = new ImageButton(this);
        Camera_open.setImageResource(R.drawable.ic_camera_foreground);
        //  Camera_open.setBackgroundResource(R.);

        zseries_scan = new ImageButton(this);
        zseries_scan.setImageResource(R.drawable.ic_zscan_foreground);
        //  zseries_scan.setBackgroundResource(R.drawable.);


        S2start = new ImageButton(this);
        S2start.setImageResource(R.drawable.ic_animation);
        S2start.setBackgroundResource(R.drawable.circle_normal);


        imgs2stack = new ImageButton(this);
        imgs2stack.setImageResource(R.drawable.ic_baseline_autorenew_24);
        //  imgs2stack.setBackgroundResource(R.drawable.circle_normal);


        animation_i = new ImageButton(this);
        animation_i.setImageResource(R.drawable.ic_animation);
        animation_i.setId(animation_id);

        navigation_left = new ImageButton(this);
        navigation_left.setImageResource(R.drawable.ic_chevron_left_black_24dp);

        navigation_right = new ImageButton(this);
        navigation_right.setImageResource(R.drawable.ic_chevron_right_black_24dp);

        navigation_up = new ImageButton(this);
        navigation_up.setImageResource(R.drawable.ic_expand_less_black_24dp);

        navigation_down = new ImageButton(this);
        navigation_down.setImageResource(R.drawable.ic_expand_more_black_24dp);

        navigation_front = new Button(this);
        navigation_front.setText("z+");

        navigation_back = new Button(this);
        navigation_back.setText("z-");

        navigation_location = new ImageButton(this);
        navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
        navigation_location.setBackgroundResource(R.drawable.circle_normal);

        res_list = new Button(this);
        res_list.setText("R");
        res_list.setTextColor(Color.BLUE);

        eswc_sync = new ImageButton(this);
        eswc_sync.setImageResource(R.drawable.ic_baseline_autorenew_24);


        room_id = new ImageButton(this);
        room_id.setImageResource(R.drawable.ic_baseline_place_24);

        user_list = new ImageButton(this);
        user_list.setImageResource(R.drawable.ic_baseline_account_box_24);

        x_pos_Text = new TextView(this);
        x_pos_Text.setTextColor(Color.YELLOW);
        x_pos_Text.setText("00000000");
        x_pos_Text.setTypeface(Typeface.DEFAULT_BOLD);
        x_pos_Text.setLetterSpacing(0.4f);
        x_pos_Text.setTextSize(15);

        y_pos_Text = new TextView(this);
        y_pos_Text.setTextColor(Color.YELLOW);
        y_pos_Text.setText("00000000");
        y_pos_Text.setTypeface(Typeface.DEFAULT_BOLD);
        y_pos_Text.setLetterSpacing(0.4f);
        y_pos_Text.setTextSize(15);

        z_pos_Text = new TextView(this);
        z_pos_Text.setTextColor(Color.YELLOW);
        z_pos_Text.setText("00000000");
        z_pos_Text.setTypeface(Typeface.DEFAULT_BOLD);
        z_pos_Text.setLetterSpacing(0.4f);
        z_pos_Text.setTextSize(15);

        /*
        set button layout ------------------------------------------------------------------------------------
         */

        FrameLayout.LayoutParams lp_zscan_up = new FrameLayout.LayoutParams(120, 120);
        lp_zscan_up.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_zscan_up.setMargins(0, 200, 20, 0);

        FrameLayout.LayoutParams lp_zscan_down = new FrameLayout.LayoutParams(120, 120);
        lp_zscan_down.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_zscan_down.setMargins(0, 290, 20, 0);

        FrameLayout.LayoutParams lp_zoom_in_no = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_in_no.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_zoom_in_no.setMargins(0, 0, 20, 290);


        FrameLayout.LayoutParams lp_zoom_out_no = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_out_no.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_zoom_out_no.setMargins(0, 0, 20, 200);


        FrameLayout.LayoutParams lp_rotation = new FrameLayout.LayoutParams(180, 120);
        lp_rotation.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_rotation.setMargins(0, 0, 180, 20);


//        FrameLayout.LayoutParams lp_si_logo = new FrameLayout.LayoutParams(150, 150);
//        lp_si_logo.gravity = Gravity.TOP | Gravity.LEFT;
//        lp_si_logo.setMargins(0, 0, 0, 0);

        FrameLayout.LayoutParams lp_hide = new FrameLayout.LayoutParams(120, 120);
        lp_hide.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_hide.setMargins(0, 0, 20, 20);


        FrameLayout.LayoutParams lp_camera = new FrameLayout.LayoutParams(120, 120);
        lp_camera.gravity = Gravity.BOTTOM | Gravity.LEFT;
        lp_camera.setMargins(60, 0, 0, 20);


        FrameLayout.LayoutParams lp_img2stack = new FrameLayout.LayoutParams(120, 120);
        lp_img2stack.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_img2stack.setMargins(0, 300, 0, 20);

        lp_x_pos = new FrameLayout.LayoutParams(450, 300);
        lp_x_pos.gravity = Gravity.TOP | Gravity.LEFT;
        lp_x_pos.setMargins(20, 200, 0, 0);


        lp_y_pos = new FrameLayout.LayoutParams(450, 300);
        lp_y_pos.gravity = Gravity.TOP | Gravity.LEFT;
        lp_y_pos.setMargins(20, 260, 0, 0);
        y_pos_Text.setLayoutParams(lp_y_pos);

        lp_z_pos = new FrameLayout.LayoutParams(450, 300);
        lp_z_pos.gravity = Gravity.TOP | Gravity.LEFT;
        lp_z_pos.setMargins(20, 320, 0, 0);
        z_pos_Text.setLayoutParams(lp_z_pos);

        lp_animation_i = new FrameLayout.LayoutParams(200, 160);

        lp_left_i = new FrameLayout.LayoutParams(100, 150);
        lp_left_i.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;

        lp_right_i = new FrameLayout.LayoutParams(100, 150);
        lp_right_i.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        lp_up_i = new FrameLayout.LayoutParams(150, 100);
        lp_up_i.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        lp_up_i.setMargins(0, 310, 0, 0);

        lp_down_i = new FrameLayout.LayoutParams(150, 100);
        lp_down_i.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp_down_i.setMargins(0, 0, 0, 0);

        lp_front_i = new FrameLayout.LayoutParams(120, 120);
        lp_front_i.gravity = Gravity.LEFT | Gravity.BOTTOM;
        lp_front_i.setMargins(20, 0, 0, 290);

        lp_back_i = new FrameLayout.LayoutParams(120, 120);
        lp_back_i.gravity = Gravity.LEFT | Gravity.BOTTOM;
        lp_back_i.setMargins(20, 0, 0, 200);

        lp_nacloc_i = new FrameLayout.LayoutParams(90, 90);
        lp_nacloc_i.gravity = Gravity.TOP | Gravity.LEFT;
        lp_nacloc_i.setMargins(20, 350, 0, 0);

        lp_res_list = new FrameLayout.LayoutParams(120, 120);
        lp_res_list.gravity = Gravity.TOP | Gravity.LEFT;
        lp_res_list.setMargins(20, 490, 0, 0);

        lp_sync_i = new FrameLayout.LayoutParams(120, 120);
        lp_sync_i.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_sync_i.setMargins(0, 0, 20, 140);

        lp_room_id = new FrameLayout.LayoutParams(115, 115);
        lp_room_id.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_room_id.setMargins(0, 440, 20, 0);

        lp_user_list = new FrameLayout.LayoutParams(115, 115);
        lp_user_list.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_user_list.setMargins(0, 540, 20, 0);


        Zslice_up.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "Zslice_up push ! ");
//                if(!myS2renderer.getIfFileLoaded()){
//                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
//                    return;
//                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Zseries_navigate("ZStart");
                        Zseries_navigate("Zslicesize1");
//                        myS2renderer.zoom_in();
//                        myS2GLSurfaceView.requestRender();
                    }
                }).start();

            }
        });

        Zslice_down.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "Zslice_down push ! ");
//                if(!myS2renderer.getIfFileLoaded()){
//                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
//                    return;
//                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Zseries_navigate("ZStop");
                        Zseries_navigate("Zslicesize1");
//                        myS2renderer.zoom_in();
//                        myS2GLSurfaceView.requestRender();
                    }
                }).start();

            }
        });
//
        Zoom_in_Big.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!myS2renderer.getIfFileLoaded()) {
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isBigData_Remote) {
                    ifZooming = !ifZooming;
                    ifChangeLineType = false;
                    ifDeletingLine = false;
                    ifPainting = false;
                    ifSettingROI = false;
                    ifPoint = false;
                    ifDeletingMarker = false;
                    ifSpliting = false;
                    ifChangeMarkerType = false;
                    ifDeletingMultiMarker = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            myS2renderer.zoom_in();
                            myS2GLSurfaceView.requestRender();
                        }
                    }).start();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            Communicator communicator = Communicator.getInstance();
//                            communicator.zoomIn();
//
//                        }
//                    }).start();
                } else {
                    myS2renderer.zoom_in();
                    myS2GLSurfaceView.requestRender();
                }

            }
        });


        Zoom_out_Big.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!myS2renderer.getIfFileLoaded()) {
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isBigData_Remote) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            myS2renderer.zoom_out();
                            myS2GLSurfaceView.requestRender();
                        }
                    }).start();
                } else {
                    myS2renderer.zoom_out();
                    myS2GLSurfaceView.requestRender();
                }

            }
        });


//        draw_i.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                int a = 1 / 0;
//
//                if (!myS2renderer.getIfFileLoaded()){
//                    Toast.makeText(context, "Please load a File First", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Draw_list(v);
//            }
//        });


//        tracing_i.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                Tracing(v);
//            }
//        });
//
//
//
//        classify_i.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                PixelClassification(v);
//            }
//        });


        zseries_scan.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.e(TAG, "zseries_scan push ! ");
                //hideButtons();

                //myS2GLSurfaceView.requestRender();

                isZscanSeries = true;
                isS2Start = false;
                if (!isZscanSeries) {
//                    x_pos_Text.setLayoutParams(lp_x_pos);
//                    y_pos_Text.setLayoutParams(lp_y_pos);
//                    z_pos_Text.setLayoutParams(lp_z_pos);
                    progressDialog_zscan.show();


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Zseries_navigate("ZScan");
                        }
                    }).start();

                } else {


                    Log.e(TAG, "zseries_scan already push ! ");
                }
                setButtons();
//                if (isBigData_Remote){
//                    myS2renderer.resetRotation();
//                    myS2GLSurfaceView.requestRender();
//                }else {
//                    Rotation();
//                }
//                Rotation();
            }
        });

        Camera_open.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e(TAG, "s2send ");


                isCamera = true;


                //image1.setImageBitmap(bitmap); //设置Bitmap
                setButtons();
//
//                myS2renderer.clearView(isCamera);  //clean view before showing new image
//                myS2GLSurfaceView.requestRender();
//
//                shutFileName();
//                //ServerConnectorForScope.sendMsg("s2start:");
//                ServerConnector.getInstance().sendMsg("Pvcam:");
//                //ServerConnector.getInstance().sendMsg("s2start:");
//
                Toast_in_Thread("Pvcam!");
                initPvcamRtmplayout();
            }
        });

        S2start.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e(TAG, "s2send ");

                isS2Start = true;
                setButtons();

                myS2renderer.clearView(true);  //clean view before showing new image
                myS2GLSurfaceView.requestRender();

                shutFileName();
                //ServerConnectorForScope.sendMsg("s2start:");
                ServerConnector.getInstance().sendMsg("s2start:");
                //ServerConnector.getInstance().sendMsg("s2start:");

                //Toast_in_Thread("scope scan off!");
            }
        });

        imgs2stack.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e(TAG, "get imgs stack ");

                isVirtualScope = true;

                setButtons();

                myS2renderer.clearView(true);  //clean view before showing new image
                myS2GLSurfaceView.requestRender();

                shutFileName();
                //ServerConnectorForScope.sendMsg("s2start:");
                ServerConnector.getInstance().sendMsg("imgs2stack:");
                //ServerConnector.getInstance().sendMsg("s2start:");

                //Toast_in_Thread("scope scan off!");
            }
        });


//        Switch = new Button(this);
//        Switch.setText("Pause");
//
//        Switch.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//
//
//                Switch();
//            }
//        });


        Hide_i.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if (!myS2renderer.getIfFileLoaded()) {
                    Toast.makeText(context, "Please load a File First", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (myS2renderer.getIfShowSWC()) {
                    myS2renderer.setIfShowSWC(false);
                    myS2GLSurfaceView.requestRender();
                    Hide_i.setImageResource(R.drawable.ic_hide);

                } else {
                    myS2renderer.setIfShowSWC(true);
                    myS2GLSurfaceView.requestRender();
                    Hide_i.setImageResource(R.drawable.ic_not_hide);
                }
            }
        });
        animation_i.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                Animation(v);
            }
        });


        navigation_left.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                Block_navigate("Left");
            }
        });


        navigation_right.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                Block_navigate("Right");
            }
        });


        navigation_up.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                Block_navigate("Top");
            }
        });


        navigation_down.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                Block_navigate("Bottom");
            }
        });


        navigation_front.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                Block_navigate("Front");
            }
        });


        navigation_back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                Block_navigate("Back");
            }
        });


        navigation_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

//                Set_Nav_Mode();
            }
        });


        eswc_sync.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showSyncBar();
                String eswcPath = null;
                Log.e(TAG, "eswc_sync" + s2EswcPath + "  " + s2EswcPath.split("\\.")[1]);
                if (s2EswcPath.endsWith("tif")) {
                    eswcPath = s2EswcPath.replace(".tif", "_refined_pruned.swc");
                    Log.e(TAG, "eswc_sync" + eswcPath);
                } else if (s2EswcPath.endsWith("v3draw")) {
                    eswcPath = s2EswcPath.replace(".v3draw", "_refined_pruned.swc");
                    Log.e(TAG, "eswc_sync" + eswcPath);
                } else if (s2EswcPath.endsWith("v3dpbd")) {
                    eswcPath = s2EswcPath.replace(".v3dpbd", "_refined_pruned.swc");
                    Log.e(TAG, "eswc_sync" + eswcPath);
                }else {
                    //ServerConnector.getInstance().sendMsg("getimglist:/img_stack/" + s2EswcPath.split("/")[1]);
                    Log.e(TAG, "error file format!");
                    return;
                }
                File eswc_f = new File(eswcPath);
                if (!eswc_f.exists()) {
                    String[] str;
                    str = eswcPath.split("/");
                    String eswc = str[str.length - 1];
                    ServerConnector.getInstance().sendMsg("getimglist:/mouse_img_stack/" + eswc);
                    Log.e(TAG, "eswc is not existed!" + eswc);
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    loadBigDataSwc(eswcPath);
                }

            }
        });


        res_list.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Communicator.getInstance().switchRes(S2Activity.this);
            }
        });


        room_id.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoomID();
            }
        });


        user_list.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserList();
            }
        });


//        scoreText = new TextView(this);
//        scoreText.setTextColor(Color.YELLOW);
//        scoreText.setText("00000");
//        scoreText.setTypeface(Typeface.DEFAULT_BOLD);
//        scoreText.setLetterSpacing(0.8f);
//        scoreText.setTextSize(15);



        /*
        add button to the view  -------------------------------------------------------------------
         */

        this.addContentView(Zslice_up, lp_zscan_up);
        this.addContentView(Zslice_down, lp_zscan_down);
        this.addContentView(Zoom_in_Big, lp_zoom_in_no);
        this.addContentView(Zoom_out_Big, lp_zoom_out_no);


//        this.addContentView(si_logo, lp_si_logo);
        this.addContentView(zseries_scan, lp_rotation);
        this.addContentView(S2start, lp_hide);


        this.addContentView(Camera_open, lp_camera);

        this.addContentView(Hide_i, lp_hide);

        this.addContentView(imgs2stack, lp_img2stack);
        //this.addContentView(ROI_i,lp_ROI_i);
        //this.addContentView(scoreText, lp_score);

        this.addContentView(x_pos_Text, lp_x_pos);
        this.addContentView(y_pos_Text, lp_y_pos);
        this.addContentView(z_pos_Text, lp_z_pos);

        this.addContentView(navigation_left, lp_left_i);
        this.addContentView(navigation_right, lp_right_i);
        this.addContentView(navigation_up, lp_up_i);
        this.addContentView(navigation_down, lp_down_i);
        this.addContentView(navigation_front, lp_front_i);
        this.addContentView(navigation_back, lp_back_i);
        this.addContentView(navigation_location, lp_nacloc_i);


        this.addContentView(res_list, lp_res_list);
        this.addContentView(eswc_sync, lp_sync_i);
//        this.addContentView(red_pen, lp_red_color);
//        this.addContentView(blue_pen, lp_blue_color);

        this.addContentView(room_id, lp_room_id);
        this.addContentView(user_list, lp_user_list);

        x_pos_Text.setVisibility(View.GONE);
        y_pos_Text.setVisibility(View.GONE);
        z_pos_Text.setVisibility(View.GONE);

        Zslice_up.setVisibility(View.GONE);
        Zslice_down.setVisibility(View.GONE);

        zseries_scan.setVisibility(View.GONE);
        S2start.setVisibility(View.GONE);
        Camera_open.setVisibility(View.GONE);
        Hide_i.setVisibility(View.GONE);
        imgs2stack.setVisibility(View.GONE);
        Zoom_in_Big.setVisibility(View.GONE);
        Zoom_out_Big.setVisibility(View.GONE);

        navigation_left.setVisibility(View.GONE);
        navigation_right.setVisibility(View.GONE);
        navigation_up.setVisibility(View.GONE);
        navigation_down.setVisibility(View.GONE);
        navigation_front.setVisibility(View.GONE);
        navigation_back.setVisibility(View.GONE);
        navigation_location.setVisibility(View.GONE);


        res_list.setVisibility(View.GONE);
        eswc_sync.setVisibility(View.GONE);


        room_id.setVisibility(View.GONE);
        user_list.setVisibility(View.GONE);


    }


    /*
    init dir
     */
    private void initDir() {

        File dir_str_server = getExternalFilesDir(context.getResources().getString(R.string.app_name) + "/S2/Checkdata");
        //String dir_str_server="/storage/emulated/0/Hi 5/S2";
        Log.e(TAG, " dir_str_server.text;" + dir_str_server);
        S2CheckImgpath = dir_str_server.getAbsolutePath();
        File dir_server = dir_str_server;
        if (!dir_server.exists()) {
            dir_server.mkdirs();

        }
        //String dir_PVCAM_server="/storage/emulated/0/Hi 5/S2/Pvcam";
        File dir_PVCAM_server = getExternalFilesDir(context.getResources().getString(R.string.app_name) + "/S2/Pvcam");
        //String dir_PVCAM_server = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + context.getResources().getString(R.string.app_name) + "/S2/Pvcam";
        Log.e(TAG, " dir_PVCAM_server;" + dir_PVCAM_server);
        File PVCAM_server = dir_PVCAM_server;
        if (!PVCAM_server.exists()) {
            PVCAM_server.mkdirs();
        }

    }


//    private void doLoginAgora(){
//        AgoraMsgManager.getInstance().getRtmClient().login(null, username, new ResultCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.e(TAG, "agora login success");
//            }
//
//            @Override
//            public void onFailure(ErrorInfo errorInfo) {
//                Log.e(TAG, "agora login failed: " + errorInfo.getErrorCode());
//            }
//        });
//    }


//    private void initAgoraService(){
//        Intent intent = new Intent(this, AgoraService.class);
//        bindService(intent, connection_agora, Context.BIND_AUTO_CREATE);
////        startService(intent);
//    }

    private void initService() {
        // Bind to LocalService
        Intent intent = new Intent(this, ManageService.class);
        bindService(intent, connection_management, Context.BIND_AUTO_CREATE);
    }


    private void initServerConnector() {


        ServerConnector serverConnector = ServerConnector.getInstance();

        serverConnector.setIp(ip_TencentCloud);
        serverConnector.setPort("8511");
        serverConnector.initConnection();

    }


//    /** Defines callbacks for service binding, passed to bindService() */
//    private ServiceConnection connection_agora = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mBoundAgora = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBoundAgora = false;
//        }
//    };


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection_management = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BasicService.LocalBinder binder = (BasicService.LocalBinder) service;
            ManageService manageService = (ManageService) binder.getService();
            binder.addReceiveMsgInterface((S2Activity) getActivityFromContext(S2Context));
            mBoundManagement = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundManagement = false;
        }
    };


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection_collaboration = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BasicService.LocalBinder binder = (BasicService.LocalBinder) service;
            CollaborationService collaborationService = (CollaborationService) binder.getService();
            binder.addReceiveMsgInterface((S2Activity) getActivityFromContext(S2Context));
            mBoundCollaboration = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundCollaboration = false;
        }
    };


    private ArrayList<String> getFiles(String FilePath) {
        ArrayList<String> arr = new ArrayList<>();
        File file = new File(FilePath);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File childFile = files[i];
            String childName = childFile.getName();

            //String fileSizeString = formetFileSize(childFile);
            //Log.e("iiii", "getFiles: "+childName);
            arr.add(childName);
            // Log.e("iiii", "fileLength="+fileSizeString);
        }
        return arr;
    }

    /*
    for service ------------------------------------------------------------------------------------
     */


    /**
     * @param FileList filelist from server
     */
    private void LoadFiles(String FileList) {
        List<String> list_array = new ArrayList<>();
        String[] list = FileList.split(";;");
        ArrayList<String> arr = new ArrayList<>();
        String dir_str_server = S2CheckImgpath;
//        try {
//            dir_str_server = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + "Hi 5" + "/S2";
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        boolean isFile = false;
        String[] fileName = new String[1];

//        Log.e(TAG, "list.length: " + list.length);
        for (int i = 0; i < list.length; i++) {
            if (list[i].split(" ")[0].endsWith(".apo") || list[i].split(" ")[0].endsWith(".eswc")
                    || list[i].split(" ")[0].endsWith(".swc") || list[i].split(" ")[0].endsWith("log"))
                continue;

            if (list[i].split(" ")[0].endsWith(".tif")) {

                isFile = true;

            }
//            if (Communicator.getInstance().initSoma(list[i].split(" ")[0])) {
//                fileName[0] = list[i].split(" ")[0];
//                isFile = true;
//                continue;
//            }

            list_array.add(list[i].split(" ")[0]);
        }
//        if (isFile) {
//            list_array.add("create a new Room");
//        }
        if (!isFile) {
            list_array.add("Update all data from microscope");
        }


        String[] list_show = new String[list_array.size()];
        for (int i = 0; i < list_array.size(); i++) {
            list_show[i] = list_array.get(i);
        }

        {
            String finalDir_str_server = dir_str_server;
            new XPopup.Builder(this)
                    .maxHeight(1350)
                    .maxWidth(1200)
                    .asCenterList("VirtualScope File", list_show,
                            new OnSelectListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onSelect(int position, String text) {


                                    Log.e(TAG, " ServerConnector.text;" + text);
                                    if (text.contains(".tif") || (text.contains(".v3draw")|| text.contains(".v3dpbd"))) {
                                        isVirtualScope = false;
                                        isCheckmode = true;

                                    } else if (text.contains("img2d_")) {
                                        isVirtualScope = true;
                                        isCheckmode = false;
                                    } else if (text.contains(".swc")) {
                                        isCheckmode = true;
                                    }
                                    if (text.contains("Update all data from microscope")) {
                                        ServerConnector.getInstance().sendMsg("Update_data:");
                                        Toast_in_Thread("Request for data! ");
                                        return;
                                    }
                                    File f = new File(finalDir_str_server);

                                    if (!f.exists()) {
                                        Toast_in_Thread("failed to create folder! ");
                                        return;
                                    }
                                    Log.e(TAG, "isforceupdate" + isforceupdate);
                                    if(!isforceupdate)
                                    {

                                    if (getFiles(finalDir_str_server).contains(text) && (text.contains(".v3draw") || text.contains(".v3dpbd")|| text.contains(".tif"))) {

                                        String filepath = finalDir_str_server + "/" + text;
                                        Log.e(TAG, "getFiles(finalDir_str_server).contains(text)" + filepath);
                                        loadBigDataImg(filepath);
                                        return;
                                    }
                                    if (getFiles(finalDir_str_server).contains(text) && text.contains(".swc")) {
                                        String filepathgg = finalDir_str_server + "/" + text;
                                        Log.e(TAG, "loadBigDataSwc" + filepathgg);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            loadBigDataSwc(filepathgg);
                                        }
                                        return;
                                    }
                                    }
                                    // ServerConnector serverConnector = ServerConnector.getInstance();
                                    conPath = conPath + "/" + text;
                                    ServerConnector.getInstance().sendMsg("getimglist:" + conPath);
                                    Log.e(TAG, " ServerConnector.getInstance().sendMsg;" + conPath);
                                    if (!text.contains("img_stack") && text.length() > 10) {
                                        progressDialog_loadimg.show();
                                        isforceupdate=false;
                                    }

                                    Toast_in_Thread("VirtualScope Mode!");

                                }
                            })
                    .show();
        }
//        if (isFile) {
//            /*
//            the last directory
//            */
//            new XPopup.Builder(this)
//                    .maxHeight(1350)
//                    .maxWidth(800)
//                    .asCenterList("BigData File", list_show,
//                            new OnSelectListener() {
//                                @RequiresApi(api = Build.VERSION_CODES.N)
//                                @Override
//                                public void onSelect(int position, String text) {
//                                    Communicator.BrainNum = conPath.split("/")[1];
//                                    switch (text) {
//                                        case "Update all data from microscope":
//
//                                            ServerConnector.getInstance().sendMsg("Update_data:");
//                                            Toast_in_Thread("Request for data! ");
//                                            //CreateFile(conPath + "/" + fileName[0], "0");
//                                            break;
//
//                                        default:
//                                            loadFileMode(conPath + "/" + text);
//                                            Communicator.Path = conPath + "/" + text;
//                                            break;
//                                    }
//                                }
//                            })
//                    .show();
//
//        } else {
//            new XPopup.Builder(this)
//                    .maxHeight(1350)
//                    .maxWidth(800)
//                    .asCenterList("VirtualScope File", list_show,
//                            new OnSelectListener() {
//                                @RequiresApi(api = Build.VERSION_CODES.N)
//                                @Override
//                                public void onSelect(int position, String text) {
//
//                                    if(text.contains("Update all data from microscope"))
//                                    {
//                                        ServerConnector.getInstance().sendMsg("Update_data:");
//                                        Toast_in_Thread("Request for data! ");
//                                        return;
//                                    }
//                                    ServerConnector serverConnector = ServerConnector.getInstance();
//                                    conPath = conPath + "/" + text;
//                                    serverConnector.sendMsg("getimglist:" + conPath);
//
//                                    isZscanSeries=false;
//                                    isS2Start = false;
//                                    if(text.contains(".tif"))
//                                    {
//                                        isVirtualScope=false;
//
//                                    }else {
//                                        isVirtualScope=true;
//                                    }
//                                    Toast_in_Thread("VirtualScope Mode!");
//
//                                }
//                            })
//                    .show();
//        }
    }


    private void loadFileMode(String filepath) {
        String[] list = filepath.split("/");
        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg("LOADFILES:2 " + filepath);
        serverConnector.setRoomName(list[list.length - 1]);

        Communicator.getInstance().setPath(filepath);
        firstLoad = true;
    }


    /**
     * load Big Data
     */
    private void loadBigData() {

        conPath = "";

        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerConnector.getInstance().sendMsg("GETFILELIST:" + "/", true, true);
            }
        }).start();

    }

    private void loadvirtualscope() {

        conPath = "";

        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerConnector.getInstance().sendMsg("getimglist:" + "/", true, true);
            }
        }).start();

    }

    private void showRoomID() {

        new XPopup.Builder(this).asConfirm("Collaboration Room", "Room name: " + ServerConnector.getInstance().getRoomName() + "\n\n"
                        + "Room ID: " + MsgConnector.getInstance().getPort(),
                new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                    }
                })
                .show();
    }


    private void showUserList() {
        String[] userList = (String[]) MsgConnector.userList.toArray();
        String[] list = new String[userList.length + 1];
        list[userList.length] = "invite friend to join...";
        System.arraycopy(userList, 0, list, 0, userList.length);
        new XPopup.Builder(this)
                //.maxWidth(600)
                .asCenterList("User List", list,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if (position < userList.length)
                                    Toast_in_Thread("User " + text + " in Room !");
                                else {
                                    showFriendsList(userList);
                                }
                            }
                        })
                .show();
    }


    private void updateUserList(List<String> newUserList) {

        for (int i = 0; i < newUserList.size(); i++) {
            if (!MsgConnector.userList.contains(newUserList.get(i)) && newUserList.get(i) != username) {
                Toast_in_Thread("User " + newUserList.get(i) + " join !");
            }
        }

        for (int i = 0; i < MsgConnector.userList.size(); i++) {
            if (!newUserList.contains(MsgConnector.userList.get(i))) {
                Toast_in_Thread("User " + MsgConnector.userList.get(i) + " left !");
            }
        }

        MsgConnector.userList = newUserList;
    }


    private void showFriendsList(String[] userList) {
        List<String> friends = NIMClient.getService(FriendService.class).getFriendAccounts();
        String[] friendList = new String[friends.size()];
        for (int i = 0; i < friends.size(); i++) {
            friendList[i] = friends.get(i);
        }
        new XPopup.Builder(this)
                .asCenterList("Friend List", friendList,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {

                                for (int i = 0; i < userList.length; i++) {
                                    if (userList[i].equals(text)) {
                                        Toast_in_Thread("Already in this room");
                                        return;
                                    }
                                }


                                Communicator communicator = Communicator.getInstance();
                                Log.e(TAG, "Send invite username: " + InfoCache.getAccount());
                                if (NIMClient.getService(UserService.class).getUserInfo(username.toLowerCase()) == null) {
                                    Toast_in_Thread_static("Invite Send Failed");
                                    return;
                                }

                                String nickname = NIMClient.getService(UserService.class).getUserInfo(username.toLowerCase()).getName();

                                InviteAttachment attachment = new InviteAttachment(nickname, communicator.Path, communicator.getInitSomaMsg());
                                IMMessage message = MessageBuilder.createCustomMessage(text, SessionTypeEnum.P2P, attachment);
                                NIMClient.getService(MsgService.class).sendMessage(message, true).setCallback(new RequestCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void param) {
                                        Toast_in_Thread_static("Sended to " + text);
                                    }

                                    @Override
                                    public void onFailed(int code) {
                                        Toast_in_Thread_static("Invite Send Failed " + code);
                                    }

                                    @Override
                                    public void onException(Throwable exception) {

                                    }
                                });
                            }
                        })
                .show();
    }






    /*
    for IM module ------------------------------------------------------------------------------------
     */


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(getContext(), "横屏", Toast.LENGTH_LONG).show();
        } else {
//            Toast.makeText(getContext(), "竖屏", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * on top bar menu created, link res/menu/main.xml
     *
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
     * open file
     */
    public void File_icon() {

        new XPopup.Builder(this)
                .asCenterList("File Open", new String[]{"Open virtualscope", "Open LocalFile"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {


                                switch (text) {

                                    case "Open LocalFile":
                                        loadLocalFile();
                                        Log.e("Open LocalFile", "Open LocalFile");
                                        break;
                                    case "Open virtualscope":
                                        loadvirtualscope();
                                        break;
//                                    case "Open BigData":
//                                        loadBigData();
//                                        break;


                                    default:
                                        ToastEasy("Default in file");
                                }
                            }
                        })
                .show();

    }

    private void LoadSwcFile() {

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


    private void loadLocalFile() {
        ifLoadLocal = true;
        ifImport = false;
        ifAnalyze = false;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
            ToastEasy("Error when open file!" + e.getMessage());
        }
    }


    /**
     * for draw button
     *
     * @param v
     */
    private void Draw_list(View v) {
        String[] drawList = isBigData_Remote ? new String[]{"For Marker", "For Curve", "Exit Drawing Mode"} : new String[]{"For Marker", "For Curve", "Clear Tracing", "Exit Drawing Mode"};
        drawPopupView = new XPopup.Builder(this)
                .atView(v)
                .autoDismiss(false)
                .asAttachList(drawList,
                        new int[]{}, new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {


                                switch (text) {
                                    case "For Marker":
                                        markerProcessList(v);
                                        break;
                                    case "For Curve":
                                        curveProcessList(v);
                                        break;
                                    case "Clear Tracing":
                                        myS2renderer.deleteAllTracing();
                                        myS2GLSurfaceView.requestRender();
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

    private void markerProcessList(View v) {
        String[] processList = isBigData_Remote ? new String[]{"PinPoint   ", "Delete Marker", "Delete MultiMarker", "Set MColor", "Change MColor"}
                : new String[]{"PinPoint   ", "Delete Marker", "Delete MultiMarker", "Set MColor", "Change MColor", "Change All MColor"};
        new XPopup.Builder(this)
                .atView(v)
                .offsetX(580)
                .isRequestFocus(false)
                .popupPosition(PopupPosition.Right)
                .asAttachList(processList, new int[]{}, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {


                        switch (text) {
                            case "PinPoint   ":
                                if (!myS2renderer.ifImageLoaded()) {
                                    ToastEasy("Please load a image first");
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
                                ifZooming = false;
                                if (ifPoint && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_add_marker);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                      ll_top.addView(buttonUndo_i, lp_undo_i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifPoint = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                  ll_top.removeView(buttonUndo_i);
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
                                ifZooming = false;
                                if (ifDeletingMarker && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_marker_delete);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                      ll_top.addView(buttonUndo_i, lp_undo_i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifDeletingMarker = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                  ll_top.removeView(buttonUndo_i);
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
                                ifZooming = false;
                                if (ifDeletingMultiMarker && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_draw_main);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                      ll_top.addView(buttonUndo_i, lp_undo_i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifDeletingMultiMarker = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                  ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Set MColor":
                                markerPenSet();
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
                                ifZooming = false;
                                if (ifChangeMarkerType && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_draw_main);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                      ll_top.addView(buttonUndo_i, lp_undo_i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifChangeMarkerType = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                  ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Change All MColor":
                                try {
                                    myS2renderer.changeAllMarkerType();
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                myS2GLSurfaceView.requestRender();
                                break;

                        }
                        drawPopupView.dismiss();
                    }
                }).show();
    }


    private void curveProcessList(View v) {
        String[] processList = isBigData_Remote ? new String[]{"Draw Curve", "Delete Curve", "Split       ", "Set PenColor", "Change PenColor"}
                : new String[]{"Draw Curve", "Delete Curve", "Split       ", "Set PenColor", "Change PenColor", "Change All PenColor"};
        new XPopup.Builder(this)
                .atView(v)
                .offsetX(580)
                .asAttachList(processList, new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {


                                switch (text) {
                                    case "Draw Curve":
                                        if (!myS2renderer.ifImageLoaded()) {
                                            ToastEasy("Please load a image first");
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
                                        ifZooming = false;
                                        if (ifPainting && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_draw);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            } catch (Exception e) {
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
                                        ifZooming = false;
                                        if (ifDeletingLine && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_delete_curve);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            } catch (Exception e) {
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
                                        ifZooming = false;
                                        if (ifSpliting && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_split);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            } catch (Exception e) {
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
                                        // 调用选择画笔窗口
                                        penSet();
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
                                        ifZooming = false;
                                        if (ifChangeLineType && !ifSwitch) {
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
                                            } catch (Exception e) {
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
                                            myS2renderer.changeAllType();
                                        } catch (CloneNotSupportedException e) {
                                            e.printStackTrace();
                                        }
                                        myS2GLSurfaceView.requestRender();
                                        break;

                                }
                                drawPopupView.dismiss();
                            }
                        }).show();
    }


    public void penSet() {
        String[] pcolor = new String[1];
        new MDDialog.Builder(this)
                .setContentView(R.layout.pen_choose)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view

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
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        //这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view，目的是方便在确定/取消按键中对contentView进行操作，如获取数据等。
//                        EditText et1 = (EditText) contentView.findViewById(R.id.pencolor);
                        String color = pcolor[0];

                        if (!color.isEmpty()) {

//                            myS2renderer.pencolorchange(Integer.parseInt(color));;
                            myS2renderer.pencolorchange(PenColor.valueOf(color).ordinal());
                            System.out.println("pen color is");
                            System.out.println(color);
                            ToastEasy("penColor set ~ ");
                        } else {
                            ToastEasy("Please make sure all the information is right !!!");
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

    public void markerPenSet() {
        String[] pcolor = new String[1];
        new MDDialog.Builder(this)
                .setContentView(R.layout.marker_pen_choose)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view

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
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
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

                        if (!color.isEmpty()) {

                            myS2renderer.markercolorchange(PenColor.valueOf(color).ordinal());
                            System.out.println("marker color is");
                            System.out.println(color);
                            ToastEasy("markerColor set ~");

                        } else {
                            ToastEasy("Please make sure all the information is right !");
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


    private void SaveSWC() {
        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.save_swc)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                    }
                })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                        EditText swcName = contentView.findViewById(R.id.swcname);
                        String swcFileName = swcName.getText().toString();
                        if (swcFileName == "") {
                            ToastEasy("The name should not be empty !");
                        }
                        myS2renderer.reNameCurrentSwc(swcFileName);

                        String dir_str = "/storage/emulated/0/Hi5/SwcSaved";
                        File dir = new File(dir_str);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        String error = null;
                        try {
                            error = myS2renderer.saveCurrentSwc(dir_str);
                        } catch (Exception e) {
                            ToastEasy(e.getMessage());
                        }
                        if (!error.equals("")) {
                            if (error.equals("This file already exits")) {
                                AlertDialog aDialog = new AlertDialog.Builder(S2Context)
                                        .setTitle("This file already exits")
                                        .setMessage("Are you sure to overwrite it?")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String errorMessage = "";
                                                try {
                                                    errorMessage = myS2renderer.oversaveCurrentSwc(dir_str);
                                                    if (errorMessage.equals(""))
                                                        ToastEasy("Overwrite successfully !");
                                                    if (errorMessage == "Overwrite failed!")
                                                        ToastEasy("Overwrite failed !");

                                                } catch (Exception e) {
                                                    System.out.println(errorMessage);
                                                    ToastEasy(e.getMessage());
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
                        } else {
                            ToastEasy("save SWC to " + dir + "/" + swcFileName + ".swc", Toast.LENGTH_LONG);
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
    }


    private void ShareScreenShot() {

        myS2renderer.setTakePic(true, this);
        myS2GLSurfaceView.requestRender();
    }


    /**
     * call the corresponding function when button in top bar clicked
     *
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

                ShareScreenShot();
                return true;

            case R.id.more:

                More_icon();
                return true;

//            case R.id.view:
//
//                if (ifButtonShowed){
//                    hideButtons();
//                    item.setIcon(R.drawable.ic_visibility_off_black_24dp);
//                } else {
//                    showButtons();
//                    item.setIcon(R.drawable.ic_visibility_black_24dp);
//                }
//                return true;
            default:
                return true;
        }
    }


//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                ToastEasy("Press again to exit the program");
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    /**
     * pop up a menu when button more is clicked, include analyze swc file, sensor information, downsample mode, animate and version
     */
    public void More_icon() {

        new XPopup.Builder(this)
                .asCenterList("More Functions...", new String[]{"liveScan", "Settings","Tag data"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {

                                switch (text) {

                                    case "liveScan":
                                        openlivescan();
                                        break;

                                    case "Settings":
                                        setSettings();
                                        break;
                                    case "Tag data":
                                        s2Tagimg();
                                        break;

//                                    case "Crash Info":
//                                        CrashInfoShare();
//                                        break;
//
//                                    case "Help":
//                                        try {
//                                            Intent helpIntent = new Intent(S2Activity.this, HelpActivity.class);
//                                            startActivity(helpIntent);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        break;


                                    default:
                                        ToastEasy("Default in More Functions...");

                                }
                            }
                        })
                .show();

    }


    private void openChatActivity() {
        Intent intent = new Intent(S2Activity.this, ChatActivity.class);
        startActivity(intent);
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
                                        myS2renderer.setIfDownSampling(true);
                                        myS2renderer.myAnimation.Start();
                                        break;

                                    case "Pause":
                                        myS2renderer.setIfDownSampling(false);
                                        myS2renderer.myAnimation.Pause();
                                        break;

                                    case "Resume":
                                        myS2renderer.setIfDownSampling(true);
                                        myS2renderer.myAnimation.Resume();
                                        break;

                                    case "Stop":
                                        myS2renderer.setIfDownSampling(false);
                                        myS2renderer.myAnimation.Stop();
                                        ifAnimation = false;
                                        ll_top.removeView(animation_i);
                                        zseries_scan.setVisibility(View.VISIBLE);
                                        break;

                                    default:
                                        ToastEasy("There is something wrong in animation");
                                }
                            }
                        })
                .show();

    }


    private void Rotation() {

        if (myS2renderer.myAnimation != null && myS2renderer.ifImageLoaded()) {
            ifAnimation = !ifAnimation;

            if (ifAnimation) {
                myS2renderer.setIfDownSampling(true);
                zseries_scan.setImageResource(R.drawable.ic_block_red_24dp);
                myS2renderer.myAnimation.quickStart();
                myS2GLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            } else {
                myS2renderer.setIfDownSampling(false);
                zseries_scan.setImageResource(R.drawable.ic_3d_rotation_red_24dp);
                myS2renderer.myAnimation.quickStop();
                myS2GLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                myS2GLSurfaceView.requestRender();
            }

        } else {
            ToastEasy("Pleas load a file first !");
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
            ifPainting = temp_mode[0];
            ifPoint = temp_mode[1];
            ifDeletingLine = temp_mode[2];
            ifDeletingMarker = temp_mode[3];
            ifSpliting = temp_mode[4];
            ifChangeLineType = temp_mode[5];
            ifChangeMarkerType = temp_mode[6];
            ifDeletingMultiMarker = temp_mode[7];
        }
    }


    private void About() {
        new XPopup.Builder(this)
                .asConfirm("Hi5: VizAnalyze Big 3D Images", "By Peng lab @ BrainTell. \n\n" +

                                "Version: 20210630a 21:45 UTC+8 build",

                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                            }
                        })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
    }


    public void popUpUserAccount(Context context) {
        new MDDialog.Builder(context)
                .setContentView(R.layout.user_account_check)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        // 这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et = (EditText) contentView.findViewById(R.id.userAccount_edit_check);
                        et.setText(InfoCache.getAccount());
                    }
                })
                .setTitle("Current UserName")
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
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


    MDDialog.Builder ar_mdDialog_bd = new MDDialog.Builder(this).setContentView(R.layout.analysis_result);
    MDDialog ar_mdDialog = null;


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


    private void s2initialization() {
        boolean[] isif_flag = new boolean[3];

        isif_flag[0] = false;
        isif_flag[1] = false;
        isif_flag[2] = false;

        MDDialog.Builder builder = new MDDialog.Builder(this);
        builder.setContentView(R.layout.s2initialization);
        builder.setContentViewOperator(new MDDialog.ContentViewOperator() {
            @Override
            public void operate(View contentView) {


                Switch Connect_Server = contentView.findViewById(R.id.Connect_Server_mode);
                Switch Connect_Scope = contentView.findViewById(R.id.Connect_Scope_mode);
                //TextView clean_S2_cache = contentView.findViewById(R.id.clean_S2_cache);
                IndicatorSeekBar indicator_XY = contentView.findViewById(R.id.indicator_XY_seekbar);
                IndicatorSeekBar indicator_Z = contentView.findViewById(R.id.indicator_Z_seekbar);
                Switch Start_smart_control = contentView.findViewById(R.id.Start_smart_control_mode);


                boolean ifConnect_Server = s2paraSetting.getConnect_ServerMode();
                boolean ifConnect_Scope = s2paraSetting.getConnect_ScopeMode();
                boolean ifSmart_Control = s2paraSetting.getSmart_ControlMode();

                isif_flag[2] = ifSmart_Control;
                int ParaXY = s2paraSetting.getParaXY();
                int ParaZ = s2paraSetting.getParaZ();

                Connect_Server.setChecked(ifConnect_Server);
                Connect_Scope.setChecked(ifConnect_Scope);
                Start_smart_control.setChecked(ifSmart_Control);


                indicator_XY.setProgress(ParaXY);
                indicator_Z.setProgress(ParaZ);


                Connect_Server.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //boolean ifconnect_Server=ServerConnector.getInstance().sendMsg("test!");
                        boolean ifconnect_Server = ServerConnector.getInstance().checkConnection();
                        Log.e(TAG, "ifconnect_Server: " + ifconnect_Server);
                        Connect_Server.setChecked(ifconnect_Server);
                        isif_flag[0] = ifconnect_Server;

                    }
                });

                Connect_Scope.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        boolean ifConnect_Scope = ServerConnector.getInstance().sendMsg("test:");
                        Log.e(TAG, "ifConnect_Scope: " + ifgetTest);

                        Connect_Scope.setChecked(ifgetTest);
                        isif_flag[1] = ifConnect_Scope;

                    }
                });

                Start_smart_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        boolean ifSmart_Control = isif_flag[2];

                        if (ifSmart_Control) {
                            ifSmart_Control = false;
                            ifSmartControl = true;
                        } else {
                            ifSmart_Control = true;
                            ifSmartControl = false;
                        }

                        isif_flag[2] = ifSmart_Control;


                        Log.e(TAG, "ifSmartControl: " + ifSmart_Control);
                    }
                });
//
//
//                clean_S2_cache.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Toast_in_Thread("clean_cache setting!");
//
//                    }
//                });

            }
        }).setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast_in_Thread("Cancel setting!");
            }
        });
        builder.setPositiveButton("Confirm", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast_in_Thread("Confirm setting!");

            }
        });
        builder.setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
            @Override
            public void onClick(View clickedView, View contentView) {


                IndicatorSeekBar indicator_XY = contentView.findViewById(R.id.indicator_XY_seekbar);
                int XY_Per_Step = indicator_XY.getProgress();

                IndicatorSeekBar indicator_Z = contentView.findViewById(R.id.indicator_Z_seekbar);
                int Z_Per_Step = indicator_Z.getProgress();

                s2paraSetting.setPara(isif_flag[0], isif_flag[1], isif_flag[2], XY_Per_Step, Z_Per_Step);


                Log.v(TAG, "indicator_XY: " + XY_Per_Step + ",indicator_Z: " + Z_Per_Step);


                ifSmartControl = s2paraSetting.getSmart_ControlMode();
                Log.v(TAG, "openSmartControl: " + ifSmartControl);
                if (ifSmartControl) {
                    startSmartControl();
                }

                S2start.setVisibility(View.VISIBLE);
                Camera_open.setVisibility(View.VISIBLE);
                myS2renderer.clearView(isS2Start);  //clean view before showing new image
                myS2GLSurfaceView.requestRender();
                Toast_in_Thread("Confirm down!");


            }
        });
        builder.setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
            @Override
            public void onClick(View clickedView, View contentView) {
                Toast_in_Thread("Cancel down!");
            }
        });
        builder.setTitle("S2 Initialization");
        MDDialog mdDialog = builder
                .create();
        mdDialog.show();
    }
    private void s2Tagimg(){
        boolean[] isif_flag = new boolean[3];

        PreferenceLogin preferenceLogin = PreferenceLogin.getInstance();
        String account = preferenceLogin.getUsername();



        MDDialog.Builder builder = new MDDialog.Builder(this);
        builder.setContentView(R.layout.s2_tag_imgcheckmode);
        builder.setContentViewOperator(new MDDialog.ContentViewOperator() {
            @Override
            public void operate(View contentView) {


                PowerfulEditText tag_userid = (PowerfulEditText)contentView.findViewById(R.id.s2_tag_userid);

                tag_userid.setText(account);
                PowerfulEditText tag_filename = (PowerfulEditText)contentView.findViewById(R.id.s2_tag_userid);

                tag_filename.setText(s2filename);

                IndicatorSeekBar image_quality = contentView.findViewById(R.id.s2_tag_image_quality);
                IndicatorSeekBar swc_quality = contentView.findViewById(R.id.s2_tag_swc_quality);

                PowerfulEditText tag_notes = (PowerfulEditText)contentView.findViewById(R.id.s2_tag_notes);

                image_quality.setProgress(0);
                swc_quality.setProgress(0);



            }
        }).setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast_in_Thread("Cancel setting!");
            }
        });
        builder.setPositiveButton("Confirm", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast_in_Thread("Confirm setting!");

            }
        });
        builder.setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
            @Override
            public void onClick(View clickedView, View contentView) {

                IndicatorSeekBar image_quality = contentView.findViewById(R.id.s2_tag_image_quality);
                IndicatorSeekBar swc_quality = contentView.findViewById(R.id.s2_tag_swc_quality);
                PowerfulEditText tag_userid = (PowerfulEditText)contentView.findViewById(R.id.s2_tag_userid);
                PowerfulEditText tag_notes = (PowerfulEditText)contentView.findViewById(R.id.s2_tag_notes);
                PowerfulEditText tag_filename = (PowerfulEditText)contentView.findViewById(R.id.s2_tag_filename);


                int image_quality_score = image_quality.getProgress();
                int swc_quality_score = swc_quality.getProgress();
                String user_id="";
                String notes="";
                String file_name="";
                user_id=tag_userid.getText().toString();
                notes=tag_notes.getText().toString();
                file_name=tag_filename.getText().toString();
                s2paraSetting.setTag(image_quality_score, swc_quality_score, user_id, file_name, notes);


                Log.v(TAG, "image_quality_score: " + image_quality_score + ",swc_quality_score: " + swc_quality_score);

                Log.v(TAG, "user_id: " + user_id + ",file_name: " + file_name+ ",notes: " + notes);







            }
        });
        builder.setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
            @Override
            public void onClick(View clickedView, View contentView) {
                Toast_in_Thread("Cancel down!");
            }
        });
        builder.setTitle("s2_tag_data");
        MDDialog mdDialog = builder
                .create();
        mdDialog.show();
    }


    private void s2Confirm_Password(){
        boolean[] isif_flag = new boolean[3];

        isif_flag[0] = false;
        isif_flag[1] = false;
        isif_flag[2] = false;

        MDDialog.Builder builder = new MDDialog.Builder(this);
        builder.setContentView(R.layout.s2_confirm_password);
        //builder.setCancelable(false);
        builder.setContentViewOperator(new MDDialog.ContentViewOperator()
        {
            @Override
            public void operate(View contentView) {

                builder.setCancelable(false);
                IndicatorSeekBar s2_password1 = contentView.findViewById(R.id.s2_password_1);
                IndicatorSeekBar s2_password2 = contentView.findViewById(R.id.s2_password_2);
                IndicatorSeekBar s2_password3 = contentView.findViewById(R.id.s2_password_3);
                IndicatorSeekBar s2_password4 = contentView.findViewById(R.id.s2_password_4);
                IndicatorSeekBar s2_password5 = contentView.findViewById(R.id.s2_password_5);








                s2_password1.setProgress(0);
                s2_password2.setProgress(0);
                s2_password3.setProgress(0);
                s2_password4.setProgress(0);
                s2_password5.setProgress(0);






            }
        }).setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast_in_Thread("Cancel setting!");
            }
        });
        builder.setPositiveButton("Confirm", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast_in_Thread("Confirm setting!");

            }
        });
        builder.setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
            @Override
            public void onClick(View clickedView, View contentView) {


                IndicatorSeekBar s2_password1 = contentView.findViewById(R.id.s2_password_1);
                int password1 = s2_password1.getProgress();

                IndicatorSeekBar s2_password2 = contentView.findViewById(R.id.s2_password_2);
                int password2 = s2_password2.getProgress();

                IndicatorSeekBar s2_password3 = contentView.findViewById(R.id.s2_password_3);
                int password3 = s2_password3.getProgress();

                IndicatorSeekBar s2_password4 = contentView.findViewById(R.id.s2_password_4);
                int password4 = s2_password4.getProgress();

                IndicatorSeekBar s2_password5 = contentView.findViewById(R.id.s2_password_5);
                int password5 = s2_password5.getProgress();

                //s2paraSetting.setPara(isif_flag[0], isif_flag[1], isif_flag[2], XY_Per_Step, Z_Per_Step);

                if(password1==5&&password2==2&&password3==0&&password4==1&&password5==3)
                {
                    myS2renderer.clearView(true);  //clean view before showing new image
                    myS2GLSurfaceView.requestRender();
                    Toast_in_Thread("Confirm down!");
                }else
                {
                    finish();
                   // System.exit(0);
                }
                //Log.v(TAG, "indicator_XY: " + XY_Per_Step + ",indicator_Z: " + Z_Per_Step);






            }
        });
        builder.setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
            @Override
            public void onClick(View clickedView, View contentView) {
                Toast_in_Thread("Cancel down!");
            }
        });
        builder.setTitle("s2_confirm_password");
        MDDialog mdDialog = builder
                .create();
        mdDialog.show();
    }



//    private void setSettings(){
//        boolean [] downsample = new boolean[1];
//
//        MDDialog mdDialog = new MDDialog.Builder(this)
//                .setContentView(R.layout.settings)
//                .setContentViewOperator(new MDDialog.ContentViewOperator() {
//                    @Override
//                    public void operate(View contentView) {
//
//                        Switch downsample_on_off = contentView.findViewById(R.id.downSample_mode);
//                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.contrast_indicator_seekbar);
//                        TextView clean_cache = contentView.findViewById(R.id.clean_cache);
//                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
//                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
//                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);
//                        Spinner bgmSpinner = contentView.findViewById(R.id.bgm_spinner);
//
//                        boolean ifDownSample = preferenceSetting.getDownSampleMode();
//                        int contrast = preferenceSetting.getContrast();
//
//                        downsample_on_off.setChecked(ifDownSample);
//                        seekbar.setProgress(contrast);
//
//
//
//                        downsample[0] = downsample_on_off.isChecked();
//
//                        downsample_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                downsample[0] = isChecked;
//                            }
//                        });
//
//
//                        clean_cache.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                cleanCache();
//                            }
//                        });
//
//                    }
//                })
//                .setNegativeButton("Cancel", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                })
//                .setPositiveButton("Confirm", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                })
//                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
//                    @Override
//                    public void onClick(View clickedView, View contentView) {
//                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.contrast_indicator_seekbar);
//                        int contrast = seekbar.getProgress();
//
//                        myS2renderer.setIfNeedDownSample(downsample[0]);
//                        myS2renderer.resetContrast(contrast);
//
//                        Log.v(TAG,"downsample: " + downsample[0] + ",contrast: " + contrast);
//                        preferenceSetting.setPref(downsample[0], contrast);
//                        myS2GLSurfaceView.requestRender();
//
//                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
//                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
//                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);
//
//
//
//
//
//
//
//
//                        String settingsPath = context.getExternalFilesDir(null).toString() + "/Settings";
//                        File settingsFile = new File(settingsPath);
//                        if (!settingsFile.exists()){
//                            settingsFile.mkdir();
//                        }
//
//                        String volumePath = settingsPath + "/volume.txt";
//                        File volumeFile = new File(volumePath);
//                        if (!volumeFile.exists()){
//                            try {
//                                volumeFile.createNewFile();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//
//
//
//                    }
//                })
//                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
//                    @Override
//                    public void onClick(View clickedView, View contentView) {
//
//                    }
//                })
//                .setTitle("Settings")
//                .create();
//        mdDialog.show();
//    }
//

    private void startSmartControl() {


        ifGetRoiPoint = true;
        Log.v(TAG, "ifSmartControl: " + ifSmartControl);
    }

    private void SmartControl() {
        String scLocation = null;
        char img_type = 0;

        if (myS2renderer.getFileType() == MyRenderer.FileType.JPG) {
            img_type = 1;

        } else if (myS2renderer.getFileType() == MyRenderer.FileType.TIF) {
            img_type = 2;
        } else {
            Log.e(TAG, "SmartControl img type error");
            return;
        }


        int x = myS2renderer.getImgWidth(img_type);
        int y = myS2renderer.getImgHeight(img_type);
        Log.v(TAG, "myS2renderer:scLocation " + x + y + "img_type" + img_type);

        Log.v(TAG, "scLocation: " + locationFor2dImg[0] + locationFor2dImg[1]);

        int xx = (int) (locationFor2dImg[0] - x / 2.0);
        int yy = -(int) (locationFor2dImg[1] - y / 2.0);

        scLocation = "sclocation:" + String.valueOf(xx) + ":" + String.valueOf(yy);
        Log.v(TAG, "scLocation: " + scLocation);

        ServerConnector.getInstance().sendMsg(scLocation);

    }

    private void openlivescan() {


        hideButtons();
        s2initialization();
    }

    private void setSettings() {
        boolean[] downsample = new boolean[1];
        boolean[] forceupdate = new boolean[1];
        MDDialog.Builder builder = new MDDialog.Builder(this);
        builder.setContentView(R.layout.s2settings);
        builder.setContentViewOperator(new MDDialog.ContentViewOperator() {
            @Override
            public void operate(View contentView) {


                Switch downsample_on_off = contentView.findViewById(R.id.s2downSample_mode);
                Switch force_updateImg = contentView.findViewById(R.id.s2force_updateImg);
                IndicatorSeekBar seekbar = contentView.findViewById(R.id.contrast_s2indicator_seekbar);
                TextView clean_S2_cache = contentView.findViewById(R.id.clean_S2_1cache);

//
                boolean ifDownSample = preferenceSetting.getDownSampleMode();
                int contrast = preferenceSetting.getContrast();
//
                downsample_on_off.setChecked(ifDownSample);
                force_updateImg.setChecked(false);
                seekbar.setProgress(contrast);


                downsample[0] = downsample_on_off.isChecked();
                //forceupdate[0] = force_updateImg.isChecked();


                downsample_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        downsample[0] = isChecked;
                    }
                });

                force_updateImg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        forceupdate[0] = true;
                    }
                });


                clean_S2_cache.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast_in_Thread("clean_cache setting!");
                        cleanCache(S2CheckImgpath);
                    }
                });

            }
        })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast_in_Thread("Cancel setting!");
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast_in_Thread("Confirm setting!");
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.contrast_s2indicator_seekbar);
                        int contrast = seekbar.getProgress();

                        setifforceupdate(forceupdate[0]);
                        preferenceSetting.setPref(downsample[0], contrast);
                        myS2renderer.setIfNeedDownSample(downsample[0]);
                        myS2renderer.resetContrast(contrast);

                        Log.v(TAG, "downsample: " + downsample[0] +"forceupdate: " + forceupdate[0]+ ",contrast: " + contrast);
                        preferenceSetting.setPref(downsample[0], contrast);
                        myS2GLSurfaceView.requestRender();


                        String settingsPath = context.getExternalFilesDir(null).toString() + "/Settings";
                        File settingsFile = new File(settingsPath);
                        if (!settingsFile.exists()) {
                            settingsFile.mkdir();
                        }


                        Toast_in_Thread("Confirm down!");
                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        Toast_in_Thread("Cancel down!");
                    }
                });
        builder.setTitle("S2 setting");
        MDDialog mdDialog = builder
                .create();
        mdDialog.show();
    }
    public void setifforceupdate(boolean ifforceupdate)
    {
        isforceupdate=ifforceupdate;
        Toast_in_Thread("forceupdate");
    }

    public void cleanCache(String img_path) {
        AlertDialog aDialog = new AlertDialog.Builder(S2Context)
                .setTitle("Clean All The Img Cache")
                .setMessage("Are you sure to CLEAN ALL IMG CACHE?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                        deleteImg(img_path);
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


    
    private void deleteImg(String img_path) {
        Log.v("BaseActivity", "deleteImg()");
       
        Log.v("BaseActivity", "img_path" + img_path);

        File file = new File(img_path);
        recursionDeleteFile(file);
    }


    private void logout() {

        AlertDialog aDialog = new AlertDialog.Builder(S2Context)
                .setTitle("Log out")
                .setMessage("Are you sure to Log out?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 清理缓存&注销监听&清除状态
                        NimUIKit.logout();
                        NIMClient.getService(AuthService.class).logout();

//                        AgoraMsgManager.getInstance().getRtmClient().logout(null);

//                        PreferenceLogin preferenceLogin = PreferenceLogin.getInstance();
//                        preferenceLogin.setPref("","",false);
                        // DemoCache.clear();

                        startActivity(new Intent(S2Activity.this, LoginActivity.class));
                        finish();
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


    private void CrashInfoShare() {

    }


    private void setAnimation() {

        final String[] rotation_type = new String[1];
        final boolean[] ifChecked = {false, false};

        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.dialog_animation)
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
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
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

                        if (rotation_speed_string.isEmpty()) {
                            ToastEasy("Make sure the input is right !");
                            setAnimation();
                            return;
                        }
                        rotation_speed = (int) Float.parseFloat(rotation_speed_string);

                        myS2renderer.myAnimation.Stop();
                        myS2renderer.setIfDownSampling(false);
                        myS2GLSurfaceView.requestRender();

                        if (ifAnimation) {
                            myS2renderer.myAnimation.setAnimation(true, Float.parseFloat(rotation_speed_string), rotation_type[0]);
                            myS2renderer.setIfDownSampling(true);

                            zseries_scan.setVisibility(View.GONE);

                            if (ll_top.findViewById(animation_id) == null) {
                                ll_top.addView(animation_i, lp_animation_i);
                            }
                            myS2GLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

                        } else {

                            zseries_scan.setVisibility(View.VISIBLE);

                            if (ll_top.findViewById(animation_id) != null) {
                                ll_top.removeView(animation_i);
                            }

                            myS2GLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

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
    }


    /**
     * called when request permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                for (int i = 0; i < permissions.length; i++) {
                    Log.i("S2Activity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
                }
                break;
            }
        }
    }


    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult start");
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            myS2renderer.setPath(showPic.getAbsolutePath());
//            myS2GLSurfaceView.requestRender();
//            return;
//        }

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String filePath = uri.toString();
            Log.v(TAG, filePath);

            try {
                if (ifImport) {

                    FileManager fileManager = new FileManager();
                    String fileName = fileManager.getFileName(uri);
                    String filetype = fileName.substring(fileName.lastIndexOf(".")).toUpperCase();
                    Log.v(TAG, "FileType: " + filetype + ", FileName: " + fileName);

                    if (myS2renderer.getIfFileLoaded()) {
                        switch (filetype) {
                            case ".APO":
                                ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
                                ApoReader apoReader = new ApoReader();
                                apo = apoReader.read(uri);
                                if (apo == null) {
                                    ToastEasy("Make sure the .apo file is right");
                                    break;
                                }

                                myS2renderer.importApo(apo);
                                myS2renderer.saveUndo();
                                break;
                            case ".SWC":
                            case ".ESWC":
                                NeuronTree nt = NeuronTree.readSWC_file(uri);
                                myS2renderer.importNeuronTree(nt, false);
                                myS2renderer.saveUndo();
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

                                myS2renderer.importNeuronTree(nt2, false);
                                myS2renderer.importApo(ano_apo);
                                myS2renderer.saveUndo();
                                break;

                            default:
                                ToastEasy("Unsupported file type");
                        }
                    } else {
                        System.out.println("-------- open --------");
                        myS2renderer.setSwcPath(filePath);
                        ifLoadLocal = false;
                        setButtonsImport();
                    }
                    ifImport = false;
                }


                if (ifAnalyze) {
                    //                   MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
//                    List features = morphologyCalculate.calculate(uri, false);

//                    if (features != null) {
//                        fl = new ArrayList<double[]>(features);
//                        displayResult(features);
//                    }
                }


                if (ifLoadLocal) {
                    Log.e(TAG, "Load Local File");
                    myS2renderer.setPath(filePath);

                    setButtonsLocal();
                    String filename = FileManager.getFileName(uri);
                    setFileName(filename);
                }


            } catch (OutOfMemoryError e) {
                ToastEasy("Fail to load file");
                Log.v("Exception", e.toString());
            } catch (CloneNotSupportedException e) {
                Log.v("Exception:", e.toString());
            }
        }
    }


    private void BigFileRead_local() {
        String[] filename_list = bigImgS2Reader.ChooseFile(this);
        if (filename_list != null) {
            String[] str = new String[1];
            bigImgS2Reader.ShowListDialog(this, filename_list);
        }
    }


    public void Block_navigate(String text) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @SuppressLint("LongLogTag")
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {
                String[] Direction = {"Left", "Right", "Top", "Bottom", "Front", "Back", "Lefttop", "leftbottom", "Righttop", "Rightbottom"};
                if ((isBigData_Remote && !isVirtualScope && isCamera) || s2workstate == "Camera") {

                    if (Arrays.asList(Direction).contains(text)) {
                        Log.e("S2_Block_navigate", text);
                        ServerConnector.getInstance().sendMsg("s2_move:" + text);

                    } else {
                        return;
                    }


                } else if (isVirtualScope) {
                    //String[] Direction = {"Left", "Right", "Top", "Bottom", "Front", "Back"};
                    if (Arrays.asList(Direction).contains(text)) {
                        Log.e("VirtualScope_Block_navigate", text);
                        ServerConnector.getInstance().sendMsg("virtualscope:" + text);

                    }
                }


            }
        }, 0 * 1000); // 延时5秒


    }

    public void Zseries_navigate(String text) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {

                if (isZscanSeries) {
                    String[] Direction = {"ZStart", "ZStop", "Zslicesize1", "ZScan"};
                    if (Arrays.asList(Direction).contains(text)) {
                        Log.e("S2_Zscan", text);
                        ServerConnector.getInstance().sendMsg("Zscan:" + text);

                    }

                }


            }
        }, 0 * 1000); // 延时5秒


    }

    private void Quit_Nav_Mode() {
        System.out.println("---------QuitNavigationLocation---------");
        myS2renderer.quitNav_location_Mode();
        navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
    }


    private void Update_Nav_Mode() {
        String filename = SettingFileManager.getFilename_Local(this);
        String offset = SettingFileManager.getoffset_Local(this, filename);

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

        myS2renderer.setNav_location(neuron, block, size);
    }


    public void Set_Nav_Mode() {
        String filename = null;
        String offset = null;
        float[] neuron = null;
        float[] block = null;
        float[] size = null;
        if (isBigData_Remote) {
            filename = getFilename_Remote(this);
            offset = getoffset_Remote(this, filename);
        }
        if (isBigData_Local) {
            filename = SettingFileManager.getFilename_Local(this);
            offset = SettingFileManager.getoffset_Local(this, filename);
        }

        if (filename == null || offset == null)
            return;

        if (isBigData_Local || isBigData_Remote) {

            float size_x, size_y, size_z;

            if (isBigData_Local) {
                size_x = Float.parseFloat(filename.split("RES")[1].split("x")[0]);
                size_y = Float.parseFloat(filename.split("RES")[1].split("x")[1]);
                size_z = Float.parseFloat(filename.split("RES")[1].split("x")[2]);

            } else {
                size_x = Float.parseFloat(filename.split("RES")[1].split("x")[1]);
                size_y = Float.parseFloat(filename.split("RES")[1].split("x")[0].replace("(", ""));
                size_z = Float.parseFloat(filename.split("RES")[1].split("x")[2].replace(")", ""));
            }

            float offset_x = Float.parseFloat(offset.split("_")[0]);
            float offset_y = Float.parseFloat(offset.split("_")[1]);
            float offset_z = Float.parseFloat(offset.split("_")[2]);
            float size_block = Float.parseFloat(offset.split("_")[3]);

            neuron = new float[]{size_x, size_y, size_z};
            block = new float[]{offset_x, offset_y, offset_z};
            size = new float[]{size_block, size_block, size_block};
        } else {

            String[] offset_arr = offset.split("_");
            int[] offset_arr_i = new int[4];
            for (int i = 0; i < offset_arr_i.length; i++) {
                offset_arr_i[i] = Integer.parseInt(offset_arr[i]);
            }

            block = new float[]{offset_arr_i[0], offset_arr_i[1], offset_arr_i[2]};
            size = new float[]{offset_arr_i[3], offset_arr_i[3], offset_arr_i[3]};
//            neuron = remote_socket.getImg_size_f(block);

            Log.i(TAG, Arrays.toString(block));
            Log.i(TAG, Arrays.toString(neuron));

        }

        boolean ifNavigationLocation = myS2renderer.getNav_location_Mode();

        if (!ifNavigationLocation) {
            System.out.println("--------!ifNavigationLocation---------");
            myS2renderer.setNav_location_Mode();
            myS2renderer.setNav_location(neuron, block, size);
            myS2GLSurfaceView.requestRender();
            navigation_location.setImageResource(R.drawable.ic_gps_off_black_24dp);
        } else {
            System.out.println("---------ifNavigationLocation---------");
            myS2renderer.setNav_location_Mode();
            myS2GLSurfaceView.requestRender();
            navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
        }
    }


    // OpenGL 中的显示区域
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

            Log.e(TAG, "GLES-version: " + v);

            //设置一下opengl版本；
            setEGLContextClientVersion(3);

            setRenderer(myS2renderer);

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

            try {

                //ACTION_DOWN不return true，就无触发后面的各个事件
                if (motionEvent != null) {
                    final float normalizedX = toOpenGLCoord(this, motionEvent.getX(), true);
                    final float normalizedY = toOpenGLCoord(this, motionEvent.getY(), false);
//
//                final float normalizedX =motionEvent.getX();
//                final float normalizedY =motionEvent.getY();

                    switch (motionEvent.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.e("MotionEvent.ACTION_DOWN", "locationX" + normalizedX + "locationY" + normalizedY);
                            X = normalizedX;
                            Y = normalizedY;
                            if (ifPainting || ifDeletingLine || ifSpliting || ifChangeLineType || ifDeletingMultiMarker || ifSettingROI) {

                                lineDrawed.add(X);
                                lineDrawed.add(Y);
                                lineDrawed.add(-1.0f);
                                myS2renderer.setIfPainting(true);
                                requestRender();
                                Log.v("actionPointerDown", "Paintinggggggggggg");
                            }
                            if (ifPoint) {

                            }
                            if (ifPainting) {

                            }
                            Log.v("ifGetRoiPoint", "ifGetRoiPoint" + ifGetRoiPoint);
                            if (ifGetRoiPoint) {
                                if (myS2renderer.getFileType() == MyRenderer.FileType.JPG || myS2renderer.getFileType() == MyRenderer.FileType.PNG || myS2renderer.getFileType() == MyRenderer.FileType.TIF) {

                                    locationFor2dImg = myS2renderer.get2dLocation(normalizedX, normalizedY);
                                    if (locationFor2dImg != null) {
                                        Log.v("ifGetRoiPoint", "locationFor2dImg" + locationFor2dImg[0] + locationFor2dImg[1]);
                                        SmartControl();

                                    }


                                } else {
                                    myS2renderer.setMarkerDrawed(normalizedX, normalizedY, isBigData_Remote);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            lineDrawed.clear();
                            myS2renderer.setIfPainting(false);
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
                                myS2renderer.zoom((float) scale);
//                            }
                                float dis_x = x2 - normalizedX;
                                float dis_y = y2 - normalizedY;
                                float ave_x = (x2 - x1_start + normalizedX - x0_start) / 2;
                                float ave_y = (y2 - y1_start + normalizedY - y0_start) / 2;
                                if (!(myS2renderer.getFileType() == MyRenderer.FileType.JPG || myS2renderer.getFileType() == MyRenderer.FileType.PNG)) {
                                    if (myS2renderer.getIfDownSampling() == false)
                                        myS2renderer.setIfDownSampling(true);
                                }
//                            if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker){
//                                myS2renderer.rotate2f(dis_x_start, dis_x, dis_y_start, dis_y);
//                            }else {
//                                myS2renderer.rotate(dis_x - dis_x_start, dis_y - dis_y_start, (float) (computeDis(dis_x, dis_x_start, dis_y, dis_y_start)));
                                if (!isS2Start) {
                                    myS2renderer.rotate(ave_x, ave_y, (float) (computeDis((x2 + normalizedX) / 2, (x1_start + x0_start) / 2, (y2 + normalizedY) / 2, (y1_start + y0_start) / 2)));
                                }
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
                            } else if (!isZooming) {
                                if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker && !ifChangeMarkerType && !ifDeletingMultiMarker && !ifSettingROI) {
                                    if (!(myS2renderer.getFileType() == MyRenderer.FileType.JPG || myS2renderer.getFileType() == MyRenderer.FileType.PNG)) {
                                        if (myS2renderer.getIfDownSampling() == false)
                                            myS2renderer.setIfDownSampling(true);
                                    }
                                    if (!isS2Start) {
                                        myS2renderer.rotate(normalizedX - X, normalizedY - Y, (float) (computeDis(normalizedX, X, normalizedY, Y)));
                                    }
                                    //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                                    requestRender();
                                    X = normalizedX;
                                    Y = normalizedY;
                                } else {
                                    lineDrawed.add(normalizedX);
                                    lineDrawed.add(normalizedY);
                                    lineDrawed.add(-1.0f);

                                    myS2renderer.setLineDrawed(lineDrawed);
                                    requestRender();

                                    invalidate();
                                }
                            }
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
//                        isZooming = false;
                            isZoomingNotStop = false;
                            myS2renderer.setIfDownSampling(false);
                            X = normalizedX;
                            Y = normalizedY;
                            lineDrawed.clear();
                            myS2renderer.setIfPainting(false);
//                        if (ifPainting){
//                            lineDrawed.clear();
//                            myS2renderer.setLineDrawed(lineDrawed);
//                            requestRender();
//
//                            myS2renderer.setIfPaiFting(false);
//                            requestRender();
//
//                        }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!isZooming) {
                                try {
                                    if (ifZooming) {
                                        ifZooming = false;
                                        float[] center = myS2renderer.solveMarkerCenter(normalizedX, normalizedY);
                                        if (center != null) {
                                            Communicator communicator = Communicator.getInstance();
                                            communicator.navigateAndZoomInBlock((int) center[0] - 64, (int) center[1] - 64, (int) center[2] - 64);
                                            requestRender();
                                        }
                                    }
                                    if (ifPoint) {
//                                        Score scoreInstance = Score.getInstance();
//                                        scoreInstance.pinpoint();

                                        Log.v("actionUp", "Pointinggggggggggg");
                                        if (myS2renderer.getFileType() == MyRenderer.FileType.JPG || myS2renderer.getFileType() == MyRenderer.FileType.PNG || myS2renderer.getFileType() == MyRenderer.FileType.TIF)
                                            myS2renderer.add2DMarker(normalizedX, normalizedY);
                                        else {
                                            myS2renderer.setMarkerDrawed(normalizedX, normalizedY, isBigData_Remote);
                                        }
                                        Log.v("actionPointerDown", "(" + X + "," + Y + ")");
                                        requestRender();

                                    }
                                    if (ifGetRoiPoint) {
                                        Log.v("ifGetRoiPoint", "DeletingRoiPoint");
                                        Log.v("ifGetRoiPoint", "locationFor2dImg" + locationFor2dImg[0] + locationFor2dImg[1]);
                                        myS2renderer.deleteRoiLocation(locationFor2dImg[0], locationFor2dImg[1], ifGetRoiPoint);
                                        requestRender();
                                    }
                                    if (ifDeletingMarker) {
                                        Log.v("actionUp", "DeletingMarker");
                                        myS2renderer.deleteMarkerDrawed(normalizedX, normalizedY, isBigData_Remote);
                                        requestRender();
                                    }
                                    if (ifDeletingMultiMarker) {
                                        myS2renderer.deleteMultiMarkerByStroke(lineDrawed, isBigData_Remote);
                                        requestRender();
                                    }
                                    if (ifChangeMarkerType) {
                                        myS2renderer.changeMarkerType(normalizedX, normalizedY, isBigData_Remote);
                                        requestRender();
                                    }
                                    if (ifPainting) {
                                        Vector<Integer> segids = new Vector<>();
                                        myS2renderer.setIfPainting(false);

//                                        Score scoreInstance = Score.getInstance();
//                                        scoreInstance.drawACurve();

                                        if (myS2renderer.getFileType() == MyRenderer.FileType.JPG || myS2renderer.getFileType() == MyRenderer.FileType.PNG)
                                            myS2renderer.add2DCurve(lineDrawed);
                                        else {

                                            Callable<String> task = new Callable<String>() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public String call() throws Exception {
                                                    int lineType = myS2renderer.getLastLineType();
                                                    V_NeuronSWC_list[] v_neuronSWC_list = new V_NeuronSWC_list[1];
                                                    V_NeuronSWC seg = myS2renderer.addBackgroundLineDrawed(lineDrawed, v_neuronSWC_list);
                                                    System.out.println("feature");
                                                    if (seg != null) {
                                                        myS2renderer.addLineDrawed2(lineDrawed, seg, isBigData_Remote);
                                                        myS2renderer.deleteFromCur(seg, v_neuronSWC_list[0]);
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
                                        }

                                        lineDrawed.clear();
                                        myS2renderer.setLineDrawed(lineDrawed);

                                        requestRender();
                                    }

                                    if (ifSettingROI) {
                                        ifSettingROI = false;
                                        float[] center = myS2renderer.GetROICenter(lineDrawed, isBigData_Remote);
                                        if (center != null) {
                                            Communicator communicator = Communicator.getInstance();
                                            communicator.navigateAndZoomInBlock((int) center[0] - 64, (int) center[1] - 64, (int) center[2] - 64);
                                            requestRender();
                                        }
                                    }


                                    if (ifDeletingLine) {
                                        myS2renderer.setIfPainting(false);
                                        myS2renderer.deleteLine1(lineDrawed, isBigData_Remote);
                                        lineDrawed.clear();
                                        myS2renderer.setLineDrawed(lineDrawed);
                                        requestRender();
                                    }
                                    if (ifSpliting) {
                                        myS2renderer.setIfPainting(false);
                                        myS2renderer.splitCurve(lineDrawed, isBigData_Remote);
                                        lineDrawed.clear();
                                        myS2renderer.setLineDrawed(lineDrawed);
                                        requestRender();
                                    }
                                    if (ifChangeLineType) {
                                        myS2renderer.setIfPainting(false);
                                        int type = myS2renderer.getLastLineType();
                                        myS2renderer.changeLineType(lineDrawed, type, isBigData_Remote);
                                        lineDrawed.clear();
                                        myS2renderer.setLineDrawed(lineDrawed);
                                        requestRender();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                lineDrawed.clear();
                                myS2renderer.setIfPainting(false);
                            }
                            lineDrawed.clear();
                            myS2renderer.setIfPainting(false);
                            requestRender();
                            isZooming = false;
                            myS2renderer.setIfDownSampling(false);
                            break;
                        default:
                            break;
                    }
                    return true;
                }

            } catch (IllegalArgumentException | CloneNotSupportedException e) {
                e.printStackTrace();
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


    /*
    load Img Block after downloading file  ---------------------------------------------------------------
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadBigDataImg(String filepath) {
        isBigData_Remote = true;

        Log.e(TAG, "loadBigDataImg: " + filepath);
        if (isZscanSeries) {
            progressDialog_zscan.dismiss();
            isZscanSeries = false;
            Log.e(TAG, "loadBigDataImg:isZscanSeries " + isZscanSeries);
        }
        if (isCheckmode) {
            progressDialog_loadimg.dismiss();
            //isCheckmode = false;
            Log.e(TAG, "loadBigDataImg:isVirtualScope " + isVirtualScope);
        }

        String[] list = filepath.split("/");
        String file_Name = list[list.length - 1];
        Log.e(TAG, "loadBigDataImg file_Name: " + file_Name);
        s2filename = file_Name;
        s2EswcPath = filepath;
        puiHandler.sendEmptyMessage(7);
        //progressDialog_zscan.dismiss();
        myS2renderer.setPath(filepath);
        myS2renderer.zoom(2f);
        myS2GLSurfaceView.requestRender();
//        if(isCamera){
//            File file = new File(filepath);
//            InputStream is = null;
//            if (file.exists()) {
//                try {
//
//                    is = new FileInputStream(file);
//
//                    Log.v("getIntensity_3d", filepath);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//
//                bitmap2D = BitmapFactory.decodeStream(is);
//                PV_imageView.setImageBitmap(bitmap2D); //设置Bitmap
//
//            }
//        }else
//        {
//        myS2renderer.setPath(filepath);
//        myS2renderer.zoom(2f);
//        myS2GLSurfaceView.requestRender();
//        }
        setButtons();
    }


    /*
 load pvcam image after downloading data  ---------------------------------------------------------------
 added by ld for pvcam
 2022.4.13
  */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadPvcamData(byte[] a) {
        isBigData_Remote = true;

        Log.e(TAG, "loadPvcamData: " + a.length);


        myS2renderer.setPvData(a);
        myS2renderer.zoom(2f);
        myS2GLSurfaceView.requestRender();

        setButtons();
    }

    public void loadBigDataApo(String filepath) {

        try {
            ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
            ApoReader apoReader = new ApoReader();
            apo = apoReader.read(filepath);
            if (apo == null) {
                Toast_in_Thread("There is something wrong with apo file !");
            }

            myS2renderer.importApo(Communicator.getInstance().convertApo(apo));
            myS2renderer.saveUndo();
            myS2GLSurfaceView.requestRender();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadBigDataSwc(String filepath) {
        try {
            //NeuronTree nt = NeuronTree.readSWC_file(filepath);
            isCheckmode = true;
            Log.e(TAG, "load .swc file !");
            float downsample = (float) 0.5;
            NeuronTree neuronTree = NeuronTree.parse(filepath, downsample);
//            if (neuronTree == null){
//                ToastEasy("Something wrong with this .swc/.eswc file, can't load it");
//            } else {
//                myS2renderer.loadNeuronTree(neuronTree, false);
//            }

            myS2renderer.importNeuronTree(neuronTree, false);
            //  myS2renderer.saveUndo();
            myS2GLSurfaceView.requestRender();

            String[] list = filepath.split("/");
            String file_Name = list[list.length - 1];
            s2filename = file_Name;
            puiHandler.sendEmptyMessage(7);
            setButtons();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //hideProgressBar();
    }

    /*
    load Img Block after downloading file  ---------------------------------------------------------------
     */

    static Timer timerDownload;

    public static void showProgressBar() {
        puiHandler.sendEmptyMessage(0);
        timerDownload = new Timer();
        timerDownload.schedule(new TimerTask() {
            @Override
            public void run() {
                timeOutHandler();
            }
        }, 30 * 1000);
    }


    public static void hideProgressBar() {
        timerDownload.cancel();
        puiHandler.sendEmptyMessage(1);
    }


    public static void timeOutHandler() {
        hideProgressBar();
        puiHandler.sendEmptyMessage(3);
    }


    public static void showSyncBar() {
        puiHandler.sendEmptyMessage(9);
        timerDownload = new Timer();
        timerDownload.schedule(new TimerTask() {
            @Override
            public void run() {
                timeOutHandler();
            }
        }, 10 * 1000);
    }


    public static void hideSyncBar() {
        timerDownload.cancel();
        puiHandler.sendEmptyMessage(10);
    }


    public static void setBigDataName() {
        puiHandler.sendEmptyMessage(4);
    }


    public static void setFileName(String name) {


        filename = name;

        filenametext.setText(filename);
        ll_file.setVisibility(View.VISIBLE);


        lp_up_i.setMargins(0, 360, 0, 0);
        navigation_up.setLayoutParams(lp_up_i);

        lp_nacloc_i.setMargins(20, 400, 0, 0);
        navigation_location.setLayoutParams(lp_nacloc_i);


        lp_res_list.setMargins(0, 540, 20, 0);
        res_list.setLayoutParams(lp_res_list);

    }

    public static void setFileName() {


        String file_name = s2filename;

        filenametext.setText(file_name);
        ll_file.setVisibility(View.VISIBLE);


        lp_up_i.setMargins(0, 360, 0, 0);
        navigation_up.setLayoutParams(lp_up_i);

        lp_nacloc_i.setMargins(20, 400, 0, 0);
        navigation_location.setLayoutParams(lp_nacloc_i);


        lp_res_list.setMargins(0, 540, 20, 0);
        res_list.setLayoutParams(lp_res_list);

    }

    public static void shutFileName() {


        //filenametext.setText(filename);
        ll_file.setVisibility(View.GONE);


        lp_up_i.setMargins(0, 310, 0, 0);
        navigation_up.setLayoutParams(lp_up_i);

        lp_nacloc_i.setMargins(20, 350, 0, 0);
        navigation_location.setLayoutParams(lp_nacloc_i);


        lp_res_list.setMargins(20, 490, 0, 0);
        res_list.setLayoutParams(lp_res_list);
    }


    private static void setButtons() {
        puiHandler.sendEmptyMessage(2);
    }

    public static void cleans2workstate() {

        isBigData_Remote = false;
        isVirtualScope = false;
        isCheckmode = false;
        isBigData_Local = false;
        isS2Start = false;
        isCamera = false;
        isZscanSeries = false;
    }

    public static void updates2workstate() {


        if (isCheckmode) {
            s2workstate = "Checkmode";
            // cleans2workstate();
        } else if (isVirtualScope) {

            s2workstate = "VirtualScope";
            // cleans2workstate();


        } else if (isCamera) {
            s2workstate = "Camera";
            ifTouchCamera = true;
            //  cleans2workstate();


        } else if (isS2Start) {
            s2workstate = "S2Start";
            // cleans2workstate();


        } else if (isZscanSeries) {

            s2workstate = "ZscanSeries";
            // cleans2workstate();

        } else if (isBigData_Remote) {

            s2workstate = "BigData_Remote";
            //cleans2workstate();

        } else if (isBigData_Local) {

            s2workstate = "BigData_Local";
            //cleans2workstate();

        } else {

            s2workstate = "";
            cleans2workstate();
        }
        Log.v(TAG, "s2workstate: " + s2workstate);
    }

    public static void setButtonsAndState() {


        hideButtons();
        updates2workstate();
        switch (s2workstate) {
            case "Checkmode":
                eswc_sync.setVisibility(View.VISIBLE);
                Hide_i.setVisibility(View.VISIBLE);
                cleans2workstate();
                isCheckmode = true;

                break;
            case "VirtualScope":
                imgs2stack.setVisibility(View.VISIBLE);
                Zoom_in_Big.setVisibility(View.VISIBLE);
                Zoom_out_Big.setVisibility(View.VISIBLE);
                cleans2workstate();
                isVirtualScope = true;
                break;
            case "Camera":
                cleans2workstate();
                isCamera = true;
                break;
            case "S2Start":
                Zoom_in_Big.setVisibility(View.VISIBLE);
                Zoom_out_Big.setVisibility(View.VISIBLE);
                zseries_scan.setVisibility(View.VISIBLE);
                S2start.setVisibility(View.VISIBLE);
                Camera_open.setVisibility(View.GONE);
                navigation_left.setVisibility(View.VISIBLE);
                navigation_right.setVisibility(View.VISIBLE);
                navigation_up.setVisibility(View.VISIBLE);
                navigation_down.setVisibility(View.VISIBLE);
                navigation_front.setVisibility(View.VISIBLE);
                navigation_back.setVisibility(View.VISIBLE);
                cleans2workstate();
                isS2Start = true;
                break;
            case "ZscanSeries":
                zseries_scan.setVisibility(View.VISIBLE);
                S2start.setVisibility(View.GONE);
                Camera_open.setVisibility(View.VISIBLE);
                Zslice_up.setVisibility(View.VISIBLE);
                Zslice_down.setVisibility(View.VISIBLE);
                cleans2workstate();
                isZscanSeries = true;
                break;
            case "BigData_Remote":

                Zoom_in_Big.setVisibility(View.VISIBLE);
                Zoom_out_Big.setVisibility(View.VISIBLE);
                cleans2workstate();
                isBigData_Remote = true;
                break;
            case "BigData_Local":

                Zoom_in_Big.setVisibility(View.VISIBLE);
                Zoom_out_Big.setVisibility(View.VISIBLE);
                cleans2workstate();
                isBigData_Local = true;
                break;
            default:
                break;
        }


    }

    public void setButtonsLocal() {
        if (isBigData_Remote || isBigData_Local) {
            if (isBigData_Remote) {
                res_list.setVisibility(View.GONE);
                user_list.setVisibility(View.GONE);
                room_id.setVisibility(View.GONE);
                eswc_sync.setVisibility(View.GONE);
                ROI_i.setVisibility(View.GONE);
            }
            isBigData_Remote = false;
            isBigData_Local = false;

            try {

                Zoom_in_Big.setVisibility(View.GONE);
                Zoom_out_Big.setVisibility(View.GONE);

                Zslice_up.setVisibility(View.GONE);
                Zslice_down.setVisibility(View.GONE);
                navigation_left.setVisibility(View.GONE);
                navigation_right.setVisibility(View.GONE);
                navigation_up.setVisibility(View.GONE);
                navigation_down.setVisibility(View.GONE);
                navigation_front.setVisibility(View.GONE);
                navigation_back.setVisibility(View.GONE);
                navigation_location.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setButtonsImport() {
        if (isBigData_Remote || isBigData_Local) {
            if (isBigData_Remote) {
                res_list.setVisibility(View.GONE);
                user_list.setVisibility(View.GONE);
                room_id.setVisibility(View.GONE);
                eswc_sync.setVisibility(View.GONE);
                ROI_i.setVisibility(View.GONE);
            }
            isBigData_Remote = false;
            isBigData_Local = false;
            try {

                Zslice_up.setVisibility(View.GONE);
                Zslice_down.setVisibility(View.GONE);
                navigation_left.setVisibility(View.GONE);
                navigation_right.setVisibility(View.GONE);
                navigation_up.setVisibility(View.GONE);
                navigation_down.setVisibility(View.GONE);
                navigation_front.setVisibility(View.GONE);
                navigation_back.setVisibility(View.GONE);
                navigation_location.setVisibility(View.GONE);

                Zoom_in_Big.setVisibility(View.GONE);
                Zoom_out_Big.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static void hideButtons() {
        navigation_left.setVisibility(View.GONE);
        navigation_right.setVisibility(View.GONE);
        navigation_up.setVisibility(View.GONE);
        navigation_down.setVisibility(View.GONE);
        navigation_front.setVisibility(View.GONE);
        navigation_back.setVisibility(View.GONE);

        zseries_scan.setVisibility(View.GONE);
        S2start.setVisibility(View.GONE);
        Camera_open.setVisibility(View.GONE);
        Zslice_up.setVisibility(View.GONE);
        Zslice_down.setVisibility(View.GONE);
        imgs2stack.setVisibility(View.GONE);
        Hide_i.setVisibility(View.GONE);
        zseries_scan.setVisibility(View.GONE);
        S2start.setVisibility(View.GONE);
        eswc_sync.setVisibility(View.GONE);
        Zoom_in_Big.setVisibility(View.GONE);
        Zoom_out_Big.setVisibility(View.GONE);

    }


    private void showButtons() {
        if (ifButtonShowed)
            return;

        // ll_top.setVisibility(View.VISIBLE);
        // ll_bottom.setVisibility(View.VISIBLE);

        // animation_i.setVisibility(View.VISIBLE);
        zseries_scan.setVisibility(View.VISIBLE);
        S2start.setVisibility(View.VISIBLE);
        // Undo_i.setVisibility(View.VISIBLE);
        //Redo_i.setVisibility(View.VISIBLE);

        if (isBigData_Remote || isBigData_Local) {
            navigation_back.setVisibility(View.VISIBLE);
            navigation_down.setVisibility(View.VISIBLE);
            navigation_front.setVisibility(View.VISIBLE);
            navigation_left.setVisibility(View.VISIBLE);
            navigation_right.setVisibility(View.VISIBLE);
            navigation_up.setVisibility(View.VISIBLE);

            Zslice_up.setVisibility(View.VISIBLE);
            Zslice_down.setVisibility(View.VISIBLE);

            Zoom_in_Big.setVisibility(View.VISIBLE);
            Zoom_out_Big.setVisibility(View.VISIBLE);

            if (isBigData_Remote) {
                res_list.setVisibility(View.VISIBLE);
                user_list.setVisibility(View.VISIBLE);
                room_id.setVisibility(View.VISIBLE);
                eswc_sync.setVisibility(View.VISIBLE);
                ROI_i.setVisibility(View.VISIBLE);
            }

        } else {
//            Zoom_in_Big.setVisibility(View.VISIBLE);
//            Zoom_out_Big.setVisibility(View.VISIBLE);
        }

        ifButtonShowed = true;
    }


//    public static void updateScore() {
//        puiHandler.sendEmptyMessage(7);
//    }
//
//
//    private void addScore(int s) {
//        score += s;
//        updateScoreText();
//    }

//    private static void updateScoreText() {
//        puiHandler.sendEmptyMessage(7);
//    }
//
//    private static void updateScoreTextHandler() {
//        Score scoreInstance = Score.getInstance();
//        int score = scoreInstance.getScore();
//        String scoreString;
//        if (score < 10) {
//            scoreString = "0000" + Integer.toString(score);
//        } else if (score >= 10 && score < 100) {
//            scoreString = "000" + Integer.toString(score);
//        } else if (score >= 100 && score < 1000) {
//            scoreString = "00" + Integer.toString(score);
//        } else if (score >= 1000 && score < 10000) {
//            scoreString = "0" + Integer.toString(score);
//        } else {
//            scoreString = Integer.toString(score);
//        }
//        Log.d("UpdateScore", Integer.toString(score) + "   " + scoreString);
//        scoreText.setText(scoreString);
//    }

    public void showAchievementFinished() {
        new XPopup.Builder(S2Context)
                .offsetY(1000)
                .popupAnimation(PopupAnimation.TranslateAlphaFromBottom)
                .asCustom(new AchievementPopup(S2Context))
                .show();
    }


    public static void Toast_in_Thread_static(String message) {
        Message msg = new Message();
        msg.what = TOAST_INFO_STATIC;
        Bundle bundle = new Bundle();
        bundle.putString("Toast_msg", message);
        msg.setData(bundle);
        puiHandler.sendMessage(msg);
    }

    public static Context getContext() {
        return context;
    }






    /*
    functions for old version bigdata  ---------------------------------------------------------------------------------
     */

    public static void LoadBigFile_Local(String filepath_local) {
        System.out.println("------" + filepath_local + "------");
        isBigData_Local = true;

        String filename = SettingFileManager.getFilename_Local(context);
        String offset = SettingFileManager.getoffset_Local(context, filename);

        int[] index = BigImgReader.getIndex(offset);
        myS2renderer.SetPath_Bigdata(filepath_local, index);

        String offset_x = offset.split("_")[0];
        String offset_y = offset.split("_")[1];
        String offset_z = offset.split("_")[2];
        ToastEasy("Current offset: " + "x: " + offset_x + " y: " + offset_y + " z: " + offset_z);
        myS2GLSurfaceView.requestRender();

        setSelectSource("Local Server", context);
        setButtons();

    }


//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public static void LoadBigFile_Remote(String filepath){
//
//        Log.v("S2Activity","LoadBigFile_Remote()");
//
//        if (ifGame){
//
//            Log.v("S2Activity","LoadBigFile_Remote() ifGame");
//            try {
//                Log.v("GameIntent", "inNNNNNNNNNNNNNNNNNNNNN");
//                Intent gameIntent = new Intent(S2Context, GameActivity.class);
//                gameIntent.putExtra("FilePath", filepath);
//                gameIntent.putExtra("Position", gamePositionForIntent);
//                gameIntent.putExtra("Dir", gameDirForIntent);
//                gameIntent.putExtra("Head", gameHeadForIntent);
//                gameIntent.putExtra("LastIndex", gameLastIndexForIntent);
//                gameIntent.putExtra("IfNewGame", gameIfNewForIntent);
//                gameIntent.putExtra("Score", gameScoreForIntent);
//                S2Context.startActivity(gameIntent);
//                gamePositionForIntent = new float[]{0.5f, 0.5f, 0.5f};
//                gameDirForIntent = new float[]{1, 1, 1};
//                gameHeadForIntent = new float[]{1, 0, -1};
//                gameLastIndexForIntent = -1;
//                gameIfNewForIntent = true;
//                gameScoreForIntent = 0;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }else {
//
//            Log.v("S2Activity","LoadBigFile_Remote() ifGame");
//            Log.v("S2Activity",remote_socket.getIp());
//            if (remote_socket.getIp().equals(ip_TencentCloud)){
//                setSelectSource("Remote Server Aliyun",context);
//            } else if (remote_socket.getIp().equals(ip_SEU)){
//                setSelectSource("Remote Server SEU",context);
//            }
//
//            myS2renderer.setPath(filepath);
//            myS2renderer.zoom(2.2f);
//            setBigDataName();
//
//            System.out.println("------" + filepath + "------");
//            isBigData_Remote = true;
//            isBigData_Local = false;
//            myS2GLSurfaceView.requestRender();
//            setButtons();
//
//            PullSwc_block_Auto(true);
//
//            if (DrawMode){
//                LoadMarker();
//                if (!push_info_swc[0].equals("New")){
////                    String filepath = this.getExternalFilesDir(null).toString();
////                    String swc_file_path = filepath + "/Sync/BlockSet";
////                    PushSWC_Block_Auto(push_info_swc[0], push_info_swc[1]);
//                }
//            }
//        }
//
//    }
//
//
//
//    private static void LoadMarker(){
//
//        String filename = getFilename_Remote(context);
//        String offset = getoffset_Remote(context, filename);
//        int[] index = BigImgReader.getIndex(offset);
//        Log.v("LoadMarker",Arrays.toString(index));
//
//        ArrayList<ArrayList<Integer>> marker_list = new ArrayList<ArrayList<Integer>>();
//        marker_list = remote_socket.getMarker(index);
//
//        myS2renderer.importMarker(marker_list);
//        myS2GLSurfaceView.requestRender();
//
//    }



    /*
    functions for old version bigdata  ---------------------------------------------------------------------------------
     */


    private void gameStart() {
        float[] startPoint = new float[]{
                0.5f, 0.5f, 0.5f
        };

        float[] dir = new float[]{
                1, 1, 1
        };

        ArrayList<Integer> sec_proj1 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj2 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj3 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj4 = new ArrayList<Integer>();
        ArrayList<Float> sec_anti = new ArrayList<Float>();


        ArrayList<Float> tangent = myS2renderer.tangentPlane(startPoint[0], startPoint[1], startPoint[2], dir[0], dir[1], dir[2], 1);

        System.out.println("TangentPlane:::::");
        System.out.println(tangent.size());


        float[] vertexPoints = new float[sec_anti.size()];
        for (int i = 0; i < sec_anti.size(); i++) {

            vertexPoints[i] = sec_anti.get(i);
            System.out.print(vertexPoints[i]);
            System.out.print(" ");
            if (i % 3 == 2) {
                System.out.print("\n");
            }
        }

//        boolean gameSucceed = myS2renderer.driveMode(vertexPoints, dir);
//        if (!gameSucceed){
//            Toast.makeText(context, "wrong vertex to draw", Toast.LENGTH_SHORT);
//        } else {
//            myS2GLSurfaceView.requestRender();
//        }
    }

    public static void setIfGame(boolean b) {
        ifGame = b;
    }


    private boolean isTopActivity() {
        ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;
        if (runningTaskInfos != null) {
            cmpNameTemp = runningTaskInfos.get(0).topActivity.toString();
        }
        if (cmpNameTemp == null) {
            return false;
        }
        Log.d(TAG, "isTopActivity" + cmpNameTemp);
        return cmpNameTemp.equals("ComponentInfo{com.penglab.hi5/com.penglab.hi5.core.S2Activity}");
    }
}











