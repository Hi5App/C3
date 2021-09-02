package com.penglab.hi5.core;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ui.imageview.CircleImageView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.LocationSimple;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.feature_calc_func.MorphologyCalculate;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.learning.pixelclassification.PixelClassification;
import com.penglab.hi5.basic.tracingfunc.app2.ParaAPP2;
import com.penglab.hi5.basic.tracingfunc.app2.V3dNeuronAPP2Tracing;
import com.penglab.hi5.basic.tracingfunc.gd.CurveTracePara;
import com.penglab.hi5.basic.tracingfunc.gd.V3dNeuronGDTracing;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.basic.utils.CrashHandler;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.chat.ChatActivity;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.chat.nim.main.helper.SystemMessageUnreadManager;
import com.penglab.hi5.chat.nim.reminder.ReminderManager;
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
import com.penglab.hi5.core.game.DailyQuestsContainer;
import com.penglab.hi5.core.game.LeaderBoardActivity;
import com.penglab.hi5.core.game.LeaderBoardContainer;
import com.penglab.hi5.core.game.LeaderBoardItem;
import com.penglab.hi5.core.game.QuestActivity;
import com.penglab.hi5.core.game.RewardActivity;
import com.penglab.hi5.core.game.RewardLitePalConnector;
import com.penglab.hi5.core.game.Score;
import com.penglab.hi5.core.game.ScoreLitePalConnector;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.dataStore.PreferenceLogin;
import com.penglab.hi5.dataStore.SettingFileManager;
import com.warkiz.widget.IndicatorSeekBar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.dataStore.SettingFileManager.getFilename_Remote;
import static com.penglab.hi5.dataStore.SettingFileManager.getoffset_Remote;
import static com.penglab.hi5.dataStore.SettingFileManager.setSelectSource;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

//import com.penglab.hi5.chat.agora.AgoraService;
//import com.penglab.hi5.chat.agora.message.AgoraMsgManager;


public class MainActivity extends BaseActivity implements ReceiveMsgInterface {
    private static final String TAG = "MainActivity";

    private Timer timer = null;
    private TimerTask timerTask;


    private static MyGLSurfaceView myGLSurfaceView;
    private static MyRenderer myrenderer;
    private static Context mainContext;

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
    private boolean ifDeletingLine = false;
    private boolean ifSpliting = false;
    private boolean ifChangeLineType = false;
    private boolean ifSwitch = false;
    private boolean ifLoadLocal = false;
    private boolean ifButtonShowed = true;
    private boolean ifAnimation = false;
    private boolean ifSettingROI =false;
<<<<<<< HEAD
    public static boolean ifGuestLogin = false;
=======
>>>>>>> e51b59b135d0080fe842e1b5d7db26a545ac2463

    private boolean[] temp_mode = new boolean[8];

    private static Button Zoom_in;
    private static Button Zoom_out;

    private static Button Zoom_in_Big;
    private static Button Zoom_out_Big;
    private ImageButton Rotation_i;
    private ImageButton Hide_i;

    private static ImageButton Undo_i;
    private static ImageButton Redo_i;
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


    private static ImageButton navigation_left;
    private static ImageButton navigation_right;
    private static ImageButton navigation_up;
    private static ImageButton navigation_down;
    private static ImageButton navigation_location;
    private static ImageButton manual_sync;
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


    private FrameLayout.LayoutParams lp_undo_i;
    private FrameLayout.LayoutParams lp_left_i;
    private FrameLayout.LayoutParams lp_right_i;
    private static FrameLayout.LayoutParams lp_up_i;
    private FrameLayout.LayoutParams lp_down_i;
    private FrameLayout.LayoutParams lp_front_i;
    private FrameLayout.LayoutParams lp_back_i;
    private static FrameLayout.LayoutParams lp_nacloc_i;
    private static FrameLayout.LayoutParams lp_sync_i;
//    private static FrameLayout.LayoutParams lp_sync_push;
//    private static FrameLayout.LayoutParams lp_sync_pull;
//    private static FrameLayout.LayoutParams lp_neuron_list;
//    private static FrameLayout.LayoutParams lp_blue_color;
//    private static FrameLayout.LayoutParams lp_red_color;
    private static FrameLayout.LayoutParams lp_res_list;
    private FrameLayout.LayoutParams lp_animation_i;
    private static FrameLayout.LayoutParams lp_undo;
    private static FrameLayout.LayoutParams lp_redo;
    private static FrameLayout.LayoutParams lp_score;

    private static FrameLayout.LayoutParams lp_room_id;
    private static FrameLayout.LayoutParams lp_user_list;

    private Button PixelClassification;
    private boolean[][]select= {{true,true,true,false,false,false,false},
            {true,true,true,false,false,false,false},
            {false,false,false,false,false,false,false},
            {false,false,false,false,false,false,false},
            {false,false,false,false,false,false,false},
            {true,true,true,false,false,false,false}};


    private BigImgReader bigImgReader;



    private LinearLayout ll_top;
    private LinearLayout ll_bottom;
    private static LinearLayout ll_file;

    private int measure_count = 0;
    private List<double[]> fl;

    private static boolean isBigData_Remote;
    private static boolean isBigData_Local;
    private static ProgressBar progressBar;

    private CircleImageView wave;

    private int eswc_length;
    // 读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int TOAST_INFO_STATIC = 5;

    //    private int Paintmode = 0;
    private ArrayList<Float> lineDrawed = new ArrayList<Float>();

    private BroadcastReceiver broadcastReceiver;

    private String currentPhotoPath; // 指定一个不会跟其他文件产生冲突的文件名，用于后面相机拍照的图片的保存

    private File showPic;
    private Uri picUri;

    private static BasePopupView popupView;
    private static BasePopupView popupViewSync;

    private static final int animation_id = 0;
    private int rotation_speed = 36;

    private long exitTime = 0;

    private static String filename = "";

    private enum PenColor {
        WHITE, BLACK, RED, BLUE, PURPLE, CYAN, YELLOW, GREEN
    }

    private enum VoicePattern {
        PEER_TO_PEER, CHAT_ROOM, UNCERTAIN
    }


    private BasePopupView drawPopupView;

    private static boolean ifGame = false;

    HashMap<Integer, String> User_Map = new HashMap<Integer, String>();

    public static String USERNAME = "username";

    public static String username;

    private static float [] gamePositionForIntent = {0.5f, 0.5f, 0.5f};
    private static float [] gameDirForIntent = {1, 1, 1};
    private static float [] gameHeadForIntent = {1, 0, -1};
    private static int gameLastIndexForIntent = -1;
    private static boolean gameIfNewForIntent = true;
    private static int gameScoreForIntent = 0;
    private SoundPool soundPool;
    private final int SOUNDNUM = 4;
    private int [] soundId;

//    private boolean mBoundAgora = false;
    private boolean mBoundManagement = false;
    private boolean mBoundCollaboration = false;

    private int count = 0;

    private static String conPath = "";

    private float bgmVolume = 0f;
    private float buttonVolume = 1.0f;
    private float actionVolume = 1.0f;
    public static boolean firstLoad = true;
    private boolean firstJoinRoom = true;
    private boolean copyFile = false;

    private int score = 0;
    private String scoreString = "00000";

    private static TextView scoreText;
    private int selectedBGM = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRecMessage(String msg) {


        if (msg.startsWith("TestSocketConnection")){
            ServerConnector.getInstance().sendMsg("HeartBeat");
        }else {
            Log.e(TAG,"onRecMessage()  " + msg);
//            Logcat.w("onRecMessage", msg);
        }


        /*
        select file
         */
        if (msg.startsWith("GETFILELIST:")){
            LoadFiles(msg.split(":")[1]);
        }



        /*
        After msg:  "LOADFILES:0 /17301/17301_00019/17301_00019_x20874.000_y23540.000_z7388.000.ano /17301/17301_00019/test_01_fx_lh_test.ano"

        when the file is selected, room will be created, and collaborationService will be init, port is room number
         */
        if (msg.startsWith("Port:")){

            if (msg.split(":")[1].equals("-1")){
                Toast_in_Thread("Something wrong with this img, choose other img please !");
                soundPool.play(soundId[3], actionVolume, actionVolume, 0, 0, 1.0f);
                return;
            }

            initMsgConnector(msg.split(":")[1]);
            if (firstJoinRoom){
                initMsgService();
                firstJoinRoom = false;
            }else {

                /*
                reset the msg connect in collaboration service
                 */
                CollaborationService.resetConnection();
            }

            /*
            when join the room, user should login first
             */
            MsgConnector.getInstance().sendMsg("/login:" + username);
        }




        /*
        After msg:  "/login:xf"

        server will send user list when the users in current room are changed
         */
        if (msg.startsWith("/users:")){

            if (firstLoad || copyFile){
                /*
                when first join the room, try to get the image
                 */
                MsgConnector.getInstance().sendMsg("/ImageRes:" + Communicator.BrainNum);
                firstLoad = false;
                copyFile   = false;
            }
            /*
            update the user list
             */
            String[] users = msg.split(":")[1].split(";");
            List<String> newUserList = Arrays.asList(users);
            updateUserList(newUserList);

        }



        /*
        After msg:  "/ImageRes:18454"

        process the img resolution info
         */
        if (msg.startsWith("ImgRes")){
            Log.e(TAG,"msg: " + msg);
            int resDefault = Math.min(2, Integer.parseInt(msg.split(";")[1]));
            Communicator.getInstance().initImgInfo(null, Integer.parseInt(msg.split(";")[1]), resDefault, msg.split(";"));

//            communicator.setResolution(msg.split(";"));
//            communicator.setImgRes(Integer.parseInt(msg.split(";")[1]));
//            communicator.setCurRes(Integer.parseInt(msg.split(";")[1]));

            MsgConnector.getInstance().sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + Communicator.getCurRes() + ";" + Communicator.getCurrentPos() + ";");

        }



        /*
        After msg:  "/Imgblock:"

        process the img block & swc apo file
         */
        if (msg.startsWith("Block:")){

            loadBigDataImg(msg.split(":")[1]);
            MsgConnector.getInstance().sendMsg("/GetBBSwc:" + Communicator.BrainNum + ";" + Communicator.getCurRes() + ";" + Communicator.getCurrentPos() + ";");

        }

        if (msg.startsWith("File:")){
            if(msg.endsWith(".apo")){

                Log.e(TAG, "File: .apo");
                loadBigDataApo(msg.split(":")[1]);

            }else if (msg.endsWith(".swc") || msg.endsWith(".eswc")){

                Log.e(TAG, "File: .eswc");
                loadBigDataSwc(msg.split(":")[1]);
                hideSyncBar();  // for sync button

            }
        }


        if (msg.startsWith("Score:")){
            Log.e(TAG,"get score: " + msg);
            int serverScore = Integer.parseInt(msg.split(":")[1].split(" ")[1]);
            Score score = Score.getInstance();
            if (score.serverUpdateScore(serverScore)){
                updateScoreText();
            }
//            initDataBase(Integer.parseInt(msg.split(":")[1].split(" ")[1]));
        }



        /*
        for collaboration -------------------------------------------------------------------
         */

        if (msg.startsWith("/drawline_norm:")){
            Log.e(TAG,"drawline_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String seg      = msg.split(":")[1];

            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                myrenderer.syncAddSegSWC(communicator.syncSWC(seg));
                myGLSurfaceView.requestRender();
            }

        }


        if (msg.startsWith("/delline_norm:")){
            Log.e(TAG,"delline_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String seg      = msg.split(":")[1];


            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                myrenderer.syncDelSegSWC(communicator.syncSWC(seg));
                myGLSurfaceView.requestRender();
            }

        }

        if (msg.startsWith("/addmarker_norm:")){
            Log.e(TAG,"addmarker_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String marker      = msg.split(":")[1].split(";")[1];

            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                myrenderer.syncAddMarker(communicator.syncMarker(marker));
                myGLSurfaceView.requestRender();
            }
        }



        if (msg.startsWith("/delmarker_norm:")){
            Log.e(TAG,"delmarker_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String marker      = msg.split(":")[1].split(";")[1];

            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                myrenderer.syncDelMarker(communicator.syncMarker(marker));
                myGLSurfaceView.requestRender();
            }
        }



        if (msg.startsWith("/retypeline_norm:")){
            Log.e(TAG,"retypeline_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String seg    = msg.split(":")[1];

            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                myrenderer.syncRetypeSegSWC(communicator.syncSWC(seg));
                myGLSurfaceView.requestRender();
            }

        }

        /*
        for collaboration -------------------------------------------------------------------
         */

        if (msg.startsWith("GETFIRSTK:")){
            Log.d(TAG, msg);
            if (msg.split(":").length > 1) {
                String body = msg.split(":")[1];
                String [] accountsWithScore = body.split(";");
                Log.d(TAG, "accountsWithScore: " + accountsWithScore.length);
                Log.d(TAG, "accountsWithScore: " + accountsWithScore);

                if (accountsWithScore.length % 2 == 0) {
                    ArrayList<String> accounts = new ArrayList<>();
                    for (int i = 0; i < accountsWithScore.length / 2; i++) {
                        accounts.add(accountsWithScore[i * 2]);
                    }
                    ArrayList<LeaderBoardItem> leaderBoardItems = new ArrayList<>();

                    NIMClient.getService(UserService.class).fetchUserInfo(accounts).setCallback(new RequestCallback<List<NimUserInfo>>() {
                        @Override
                        public void onSuccess(List<NimUserInfo> param) {
                            if (param.size() == accounts.size()) {
                                for (int i = 0; i < param.size(); i++) {
                                    leaderBoardItems.add(new LeaderBoardItem(accountsWithScore[i * 2], param.get(i).getName(), Integer.parseInt(accountsWithScore[i * 2 + 1])));
                                }
                            } else {
                                postMessage("LeaderBoard Account Error");
                            }
                        }

                        @Override
                        public void onFailed(int code) {
                            postMessage("LeaderBoard Account Error");

                        }

                        @Override
                        public void onException(Throwable exception) {
                            postMessage("LeaderBoard Account Error");

                        }
                    });
                    LeaderBoardContainer leaderBoardContainer = LeaderBoardContainer.getInstance();
                    leaderBoardContainer.setLeaderBoardItems(leaderBoardItems);
                } else {
                    Toast_in_Thread_static("LeaderBoard Message Error");
                }
            } else {
                Toast_in_Thread_static("LeaderBoard Message Error");
            }
        }
    }

    private void postMessage(String message) {
        Message msg = Message.obtain();
        msg.what = 8;
        msg.obj = message;
        puiHandler.sendMessage(msg);
    }


    @SuppressLint("HandlerLeak")
    private static Handler puiHandler = new Handler(){
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    popupView.show();
                    Activity activity = getActivityFromContext(mainContext);
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;

                case 1:
                    popupView.dismiss();
                    Activity activity_2 = getActivityFromContext(mainContext);
                    activity_2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;

                case 2:
                    setButtonsBigData();
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
                    setFileName(Communicator.BrainNum);
                    break;

                case 5:
                    String Toast_msg = msg.getData().getString("Toast_msg");
                    Toast.makeText(getContext(),Toast_msg, Toast.LENGTH_SHORT).show();
                    break;

                case 6:
                    progressBar.setVisibility(View.GONE);
                    break;

                case 7:
                    updateScoreTextHandler();
                    break;

                case 8:
                    Toast_in_Thread_static((String)msg.obj);
                    break;

                case 9:
                    popupViewSync.show();
                    break;

                case 10:
                    popupViewSync.dismiss();
                    break;

                default:
                    break;
            }
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
        Log.e(TAG,"------------------ onCreate ------------------");

        // set layout
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainContext = this;

        /*
        music module  -------------------------------------------------------------------------------------------------
         */
        File volumeFile = new File(context.getExternalFilesDir(null).toString() + "/Settings/volume.txt");
        if (volumeFile.exists()){
            try {
                BufferedReader volumeReader = new BufferedReader(new InputStreamReader(new FileInputStream(volumeFile)));
                String volumeStr = volumeReader.readLine();
                if (volumeStr != null) {
                    String[] volumes = volumeStr.split(" ");
                    Log.d(TAG, "VolumeStr: " + volumeStr);
                    if (volumes.length == 3){
                        bgmVolume = Float.parseFloat(volumes[0]);
                        buttonVolume = Float.parseFloat(volumes[1]);
                        actionVolume = Float.parseFloat(volumes[2]);

                        Log.d(TAG, "Volumes: " + Arrays.toString(volumes));
                        Log.d(TAG, "Volumes: " + bgmVolume + " " + buttonVolume + " " + actionVolume);

                        MusicServer.setVolume(bgmVolume);
                    }
                }
                volumeReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        initMusicService();

        soundPool = new SoundPool(SOUNDNUM, AudioManager.STREAM_MUSIC, 5);
        soundId = new int[SOUNDNUM];
        soundId[0] = soundPool.load(this, R.raw.piano2, 1);
        soundId[1] = soundPool.load(this, R.raw.piano1, 1);
        soundId[2] = soundPool.load(this, R.raw.button01, 1);
        soundId[3] = soundPool.load(this, R.raw.fail, 1);




        isBigData_Remote = false;
        isBigData_Local  = false;

        popupView = new XPopup.Builder(this)
                .asLoading("Downloading......");

        popupViewSync = new XPopup.Builder(this)
                .asLoading("Syncing......");


        Intent intent = getIntent();
        String OOM = intent.getStringExtra(MyRenderer.OUT_OF_MEMORY);
        intent.getBooleanExtra("isVisit",false);
        username   = intent.getStringExtra(USERNAME);

        if (OOM != null)
            Toast.makeText(this, OOM, Toast.LENGTH_SHORT).show();

        wave = new CircleImageView(getContext());
        wave.setScaleType(ImageView.ScaleType.CENTER_CROP);


        myrenderer = new MyRenderer(this);
        myGLSurfaceView = new MyGLSurfaceView(this);


        initButtons();


        // set contrast  & DownSample Mode
        myrenderer.setIfNeedDownSample(preferenceSetting.getDownSampleMode());
        myrenderer.resetContrast(preferenceSetting.getContrast());


        // Set the permission for user
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }


        myGLSurfaceView.requestRender();
        bigImgReader = new BigImgReader();



        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
        params.gravity = Gravity.CENTER;
        this.addContentView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        initDir();

        if(ifGuestLogin == false) {
            initNim();
            initServerConnector();
            initService();
            initDataBase();
        }else{
            Toast.makeText(context,
                    "You are logged in as a visitor",Toast.LENGTH_LONG).show();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getScore();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getLeaderBoard();
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

        Intent bgmIntent = new Intent(this, MusicServer.class);
        stopService(bgmIntent);

        Score score = Score.getInstance();
        setScore(score.getScore());

//        if ((mBoundAgora)){
//            Log.e(TAG,"unbind agora service !");
//            unbindService(connection_agora);
//            Intent agoraServiceIntent = new Intent(this, AgoraService.class);
//            stopService(agoraServiceIntent);
//        }


        if (mBoundManagement){
            Log.e(TAG,"unbind management service !");
            ManageService.setStop(true);
            unbindService(connection_management);
            Intent manageServiceIntent = new Intent(this, ManageService.class);
            stopService(manageServiceIntent);

            ServerConnector.getInstance().releaseConnection(false);
        }
        if (mBoundCollaboration){
            Log.e(TAG,"unbind collaboration service !");
            CollaborationService.setStop(true);
            unbindService(connection_collaboration);
            Intent collaborationServiceIntent = new Intent(this, CollaborationService.class);
            stopService(collaborationServiceIntent);

            MsgConnector.getInstance().releaseConnection(false);
        }


        mainContext = null;

        if (timer != null){
            timer.cancel();
            timer = null;
        }

        if (timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause start");
        myGLSurfaceView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume start");
        Log.v("Path", filepath);
        myGLSurfaceView.onResume();

    }

    @Override
    protected void onStop() {
        Intent bgmIntent = new Intent(MainActivity.this, MusicServer.class);
        stopService(bgmIntent);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        initMusicService();
        super.onRestart();
    }

    /**
     * quick start for MainActivity
     * @param context
     */

    public static void start(Context context) {
        start(context, null);
    }


    /**
     * quick start for MainActivity
     * @param context
     * @param extras
     */
    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }




    public static void actionStart(Context context, String username){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USERNAME, username);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String invitor, String path, String soma){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        username = InfoCache.getAccount();
        acceptInvitation(path, soma);
    }




    /*
     * init buttons
     */
    private void initButtons(){
        /*
        basic Layout ------------------------------------------------------------------------------------------------------------------------
         */

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


        /*
        init buttons ------------------------------------------------------------------------------------------------------------------------
         */

        Zoom_in = new Button(this);
        Zoom_in.setText("+");
        Zoom_in_Big = new Button(this);
        Zoom_in_Big.setText("+");

        Zoom_out = new Button(this);
        Zoom_out.setText("-");
        Zoom_out_Big = new Button(this);
        Zoom_out_Big.setText("-");

        draw_i = new ImageButton(this);
        draw_i.setImageResource(R.drawable.ic_draw_main);

        tracing_i=new ImageButton(this);
        tracing_i.setImageResource(R.drawable.ic_neuron);

        classify_i=new ImageButton(this);
        classify_i.setImageResource(R.drawable.ic_classify_mid);

        Rotation_i = new ImageButton(this);
        Rotation_i.setImageResource(R.drawable.ic_3d_rotation_red_24dp);
        Rotation_i.setBackgroundResource(R.drawable.circle_normal);

        Hide_i = new ImageButton(this);
        Hide_i.setImageResource(R.drawable.ic_not_hide);
        Hide_i.setBackgroundResource(R.drawable.circle_normal);

        ROI_i = new ImageButton(this);
        ROI_i.setImageResource(R.drawable.ic_roi);
        ROI_i.setBackgroundResource(R.drawable.circle_normal);


        Undo_i = new ImageButton(this);
        Undo_i.setImageResource(R.drawable.ic_undo);
        Undo_i.setBackgroundResource(R.drawable.circle_normal);

        Redo_i = new ImageButton(this);
        Redo_i.setImageResource(R.drawable.ic_redo);
        Redo_i.setBackgroundResource(R.drawable.circle_normal);

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
        navigation_front.setText("F");

        navigation_back = new Button(this);
        navigation_back.setText("B");

        navigation_location = new ImageButton(this);
        navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
        navigation_location.setBackgroundResource(R.drawable.circle_normal);

        res_list = new Button(this);
        res_list.setText("R");
        res_list.setTextColor(Color.BLUE);

        manual_sync = new ImageButton(this);
        manual_sync.setImageResource(R.drawable.ic_baseline_autorenew_24);


        room_id = new ImageButton(this);
        room_id.setImageResource(R.drawable.ic_baseline_place_24);

        user_list = new ImageButton(this);
        user_list.setImageResource(R.drawable.ic_baseline_account_box_24);



        /*
        set button layout ------------------------------------------------------------------------------------
         */

        FrameLayout.LayoutParams lp_zoom_in = new FrameLayout.LayoutParams(120, 120);
        lp_zoom_in.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_zoom_in.setMargins(0, 0, 20, 290);

        FrameLayout.LayoutParams lp_zoom_out = new FrameLayout.LayoutParams(120, 120);
        lp_zoom_out.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_zoom_out.setMargins(0, 0, 20, 200);

        FrameLayout.LayoutParams lp_zoom_in_no = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_in_no.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;


        FrameLayout.LayoutParams lp_zoom_out_no = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_out_no.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        FrameLayout.LayoutParams lp_draw_i = new FrameLayout.LayoutParams(200, 160);
        ll_top.addView(draw_i,lp_draw_i);

        FrameLayout.LayoutParams lp_tracing_i = new FrameLayout.LayoutParams(200, 160);
        ll_top.addView(tracing_i,lp_tracing_i);

        FrameLayout.LayoutParams lp_classify_i = new FrameLayout.LayoutParams(200, 160);
        ll_top.addView(classify_i,lp_classify_i);

        FrameLayout.LayoutParams lp_rotation = new FrameLayout.LayoutParams(120, 120);
        lp_rotation.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_rotation.setMargins(0, 0, 160, 20);

        FrameLayout.LayoutParams lp_hide = new FrameLayout.LayoutParams(120, 120);
        lp_hide.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_hide.setMargins(0, 0, 20, 20);

        FrameLayout.LayoutParams lp_ROI_i = new FrameLayout.LayoutParams(120, 120);
        lp_ROI_i.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_ROI_i.setMargins(0, 0, 300, 20);

        lp_undo = new FrameLayout.LayoutParams(120, 120);
        lp_undo.setMargins(0, 20, 20, 0);
        ll_hs_back.addView(Undo_i, lp_undo);

        lp_redo = new FrameLayout.LayoutParams(120, 120);
        lp_redo.setMargins(0, 20, 20, 0);
        ll_hs_back.addView(Redo_i, lp_redo);

        lp_score = new FrameLayout.LayoutParams(350, 300);
        lp_score.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_score.setMargins(0, 350, 20, 0);

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

        lp_sync_i = new FrameLayout.LayoutParams(120,120);
        lp_sync_i.gravity = Gravity.TOP | Gravity.LEFT;
        lp_sync_i.setMargins(0, 440, 0, 0);

        lp_room_id = new FrameLayout.LayoutParams(115, 115);
        lp_room_id.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_room_id.setMargins(0, 440, 20, 0);

        lp_user_list = new FrameLayout.LayoutParams(115, 115);
        lp_user_list.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_user_list.setMargins(0, 540, 20, 0);




        /*
        button onclick event  -----------------------------------------------------------------------------
         */

        Zoom_in.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myrenderer.zoom_in();
                        myGLSurfaceView.requestRender();
                    }
                }).start();

            }
        });


        Zoom_out.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myrenderer.zoom_out();
                        myGLSurfaceView.requestRender();
                    }
                }).start();

            }
        });

        Zoom_in_Big.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isBigData_Remote){
                    ifZooming = !ifZooming;
                    ifChangeLineType = false;
                    ifDeletingLine = false;
                    ifPainting = false;
                    ifSettingROI=false;
                    ifPoint = false;
                    ifDeletingMarker = false;
                    ifSpliting = false;
                    ifChangeMarkerType = false;
                    ifDeletingMultiMarker = false;
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            Communicator communicator = Communicator.getInstance();
//                            communicator.zoomIn();
//
//                        }
//                    }).start();
                }else {
                    myrenderer.zoom_in();
                    myGLSurfaceView.requestRender();
                }

            }
        });


        Zoom_out_Big.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isBigData_Remote){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Communicator communicator = Communicator.getInstance();
                            communicator.zoomOut();

                        }
                    }).start();
                }else {
                    myrenderer.zoom_out();
                    myGLSurfaceView.requestRender();
                }

            }
        });



        draw_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int a = 1 / 0;
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if (!myrenderer.getIfFileLoaded()){
                    Toast.makeText(context, "Please load a File First", Toast.LENGTH_SHORT).show();
                    return;
                }
                Draw_list(v);
            }
        });



        tracing_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Tracing(v);
            }
        });



        classify_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                PixelClassification(v);
            }
        });



        Rotation_i.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

//                if (isBigData_Remote){
//                    myrenderer.resetRotation();
//                    myGLSurfaceView.requestRender();
//                }else {
//                    Rotation();
//                }
                Rotation();
            }
        });


        Hide_i.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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


        ROI_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isBigData_Remote){
<<<<<<< HEAD
                    ifSettingROI = !ifSettingROI;
                    if(ifSettingROI == true){
                        ROI_i.setImageResource(R.drawable.ic_roi_stop);
                    }else{
                        ROI_i.setImageResource(R.drawable.ic_roi);
                    }
=======
                    ifSettingROI=!ifSettingROI;
>>>>>>> e51b59b135d0080fe842e1b5d7db26a545ac2463
                    ifZooming = false;
                    ifChangeLineType = false;
                    ifDeletingLine = false;
                    ifPainting = false;
                    ifPoint = false;
                    ifDeletingMarker = false;
                    ifSpliting = false;
                    ifChangeMarkerType = false;
                    ifDeletingMultiMarker = false;
//                    new Thread(new Runnable() {
//                        @Override
<<<<<<< HEAD


=======
>>>>>>> e51b59b135d0080fe842e1b5d7db26a545ac2463
//                        public void run() {
//
//                            Communicator communicator = Communicator.getInstance();
//                            communicator.zoomIn();
//
//                        }
//                    }).start();
                }

            }
        });




        Undo_i.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                boolean undoSuccess = false;
                try {
                    undoSuccess = myrenderer.undo2();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                if (!undoSuccess) {
                    Toast.makeText(context, "nothing to undo", Toast.LENGTH_SHORT).show();
                }
                myGLSurfaceView.requestRender();
            }
        });



        Redo_i.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                boolean redoSuccess = myrenderer.redo();
                if (!redoSuccess){
                    Toast_in_Thread("nothing to redo");
                }
                myGLSurfaceView.requestRender();
            }
        });



        Switch = new Button(this);
        Switch.setText("Pause");

        Switch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Switch();
            }
        });


        animation_i.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Animation(v);
            }
        });


        navigation_left.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Left");
            }
        });


        navigation_right.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                Block_navigate("Right");
            }
        });


        navigation_up.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Top");
            }
        });


        navigation_down.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Bottom");
            }
        });


        navigation_front.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Front");
            }
        });


        navigation_back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Back");
            }
        });


        navigation_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
//                Set_Nav_Mode();
            }
        });


        manual_sync.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSyncBar();
                myrenderer.deleteAllTracing();
                MsgConnector.getInstance().sendMsg("/GetBBSwc:" + Communicator.BrainNum + ";" + Communicator.getCurRes() + ";" + Communicator.getCurrentPos() + ";");
            }
        });


        res_list.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                Communicator.getInstance().switchRes(MainActivity.this);
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


        scoreText = new TextView(this);
        scoreText.setTextColor(Color.YELLOW);
        scoreText.setText("00000");
        scoreText.setTypeface(Typeface.DEFAULT_BOLD);
        scoreText.setLetterSpacing(0.8f);
        scoreText.setTextSize(15);
        if(ifGuestLogin){
            scoreText.setVisibility(View.GONE);
        }else
            scoreText.setVisibility(View.VISIBLE);



        /*
        add button to the view  -------------------------------------------------------------------
         */

        this.addContentView(Zoom_in_Big, lp_zoom_in);
        this.addContentView(Zoom_out_Big, lp_zoom_out);
        this.addContentView(Zoom_in, lp_zoom_in_no);
        this.addContentView(Zoom_out, lp_zoom_out_no);


        this.addContentView(Rotation_i, lp_rotation);
        this.addContentView(Hide_i, lp_hide);
        this.addContentView(ROI_i,lp_ROI_i);
        this.addContentView(scoreText, lp_score);

        this.addContentView(navigation_left, lp_left_i);
        this.addContentView(navigation_right, lp_right_i);
        this.addContentView(navigation_up, lp_up_i);
        this.addContentView(navigation_down, lp_down_i);
        this.addContentView(navigation_front, lp_front_i);
        this.addContentView(navigation_back, lp_back_i);
        this.addContentView(navigation_location, lp_nacloc_i);


        this.addContentView(res_list, lp_res_list);
        this.addContentView(manual_sync, lp_sync_i);
//        this.addContentView(red_pen, lp_red_color);
//        this.addContentView(blue_pen, lp_blue_color);

        this.addContentView(room_id, lp_room_id);
        this.addContentView(user_list, lp_user_list);


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
        manual_sync.setVisibility(View.GONE);
        ROI_i.setVisibility(View.GONE);
//        red_pen.setVisibility(View.GONE);
//        blue_pen.setVisibility(View.GONE);

        room_id.setVisibility(View.GONE);
        user_list.setVisibility(View.GONE);


//        this.addContentView(neuron_list, lp_neuron_list);
//        neuron_list.setVisibility(View.GONE);
//        this.addContentView(sync_pull, lp_sync_pull);
//        this.addContentView(sync_push, lp_sync_push);
//        sync_pull.setVisibility(View.GONE);
//        sync_push.setVisibility(View.GONE);

    }


    /*
    init dir
     */
    private void initDir(){

        try{
            String dir_str = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + context.getResources().getString(R.string.app_name);
            File dir = new File(dir_str);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String dir_str_server = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + context.getResources().getString(R.string.app_name) + "/Server";
            File dir_server = new File(dir_str_server);
            if (!dir_server.exists()) {
                dir_server.mkdirs();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }



    /*
    get score
     */
    public static void getScore(){
        ServerConnector.getInstance().sendMsg("GETSCORE");
    }

    public static void setScore(int score){
        if (score == 0)
            return;
        ServerConnector.getInstance().sendMsg("SETSOCRE:" + score);
    }

    public static void getLeaderBoard(){
        ServerConnector.getInstance().sendMsg("GETFIRSTK:3");
    }



    /*
    for service ------------------------------------------------------------------------------------
     */


    private void initMusicService(){
        Log.e(TAG,"init MusicService");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MusicServer.class));
        } else {
            startService(new Intent(this, MusicServer.class));
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

    private void initService(){
        // Bind to LocalService
        Intent intent = new Intent(this, ManageService.class);
        bindService(intent, connection_management, Context.BIND_AUTO_CREATE);
    }

    private void initMsgService(){
        // Bind to LocalService
        Intent intent = new Intent(this, CollaborationService.class);
        bindService(intent, connection_collaboration, Context.BIND_AUTO_CREATE);
    }

    private void initMsgConnector(String port){
        MsgConnector msgConnector = MsgConnector.getInstance();

        if (!firstJoinRoom)
            msgConnector.releaseConnection();
        msgConnector.setIp(ip_TencentCloud);
        msgConnector.setPort(port);
        msgConnector.initConnection();
    }


    private void initServerConnector(){
        ServerConnector serverConnector = ServerConnector.getInstance();

        serverConnector.setIp(ip_TencentCloud);
        serverConnector.setPort("23763");
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


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection_management = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BasicService.LocalBinder binder = (BasicService.LocalBinder) service;
            ManageService manageService = (ManageService) binder.getService();
            binder.addReceiveMsgInterface((MainActivity) getActivityFromContext(mainContext));
            mBoundManagement = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundManagement = false;
        }
    };



    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection_collaboration = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BasicService.LocalBinder binder = (BasicService.LocalBinder) service;
            CollaborationService collaborationService = (CollaborationService) binder.getService();
            binder.addReceiveMsgInterface((MainActivity) getActivityFromContext(mainContext));
            mBoundCollaboration = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundCollaboration = false;
        }
    };

    /*
    for service ------------------------------------------------------------------------------------
     */






    /**
     * @param FileList filelist from server
     */
    private void LoadFiles(String FileList){
        List<String> list_array = new ArrayList<>();
        String[] list = FileList.split(";;");

        boolean isFile = false;
        String[] fileName = new String[1];

//        Log.e(TAG, "list.length: " + list.length);
        for (int i = 0; i < list.length; i++){
            if (list[i].split(" ")[0].endsWith(".apo") || list[i].split(" ")[0].endsWith(".eswc")
                    || list[i].split(" ")[0].endsWith(".swc") || list[i].split(" ")[0].endsWith("log") )
                continue;

            if(Communicator.getInstance().initSoma(list[i].split(" ")[0])){
                fileName[0] = list[i].split(" ")[0];
                isFile = true;
                continue;
            }

            list_array.add(list[i].split(" ")[0]);
        }
        if (isFile){
            list_array.add("create a new Room");
        }

        String[] list_show = new String[list_array.size()];
        for (int i = 0; i < list_array.size(); i++){
            list_show[i] = list_array.get(i);
        }


        if (isFile){
            /*
            the last directory
            */
            new XPopup.Builder(this)
                    .maxHeight(1350)
                    .maxWidth(800)
                    .asCenterList("BigData File", list_show,
                            new OnSelectListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onSelect(int position, String text) {
                                    Communicator.BrainNum = conPath.split("/")[1];
                                    switch (text){
                                        case "create a new Room":
                                            CreateFile(conPath + "/" + fileName[0],"0");
                                            break;

                                        default:
                                            loadFileMode(conPath + "/" + text);
                                            Communicator.Path = conPath + "/" + text;
                                            break;
                                    }
                                }
                            })
                    .show();

        }else {
            new XPopup.Builder(this)
                    .maxHeight(1350)
                    .maxWidth(800)
                    .asCenterList("BigData File", list_show,
                            new OnSelectListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onSelect(int position, String text) {
                                    ServerConnector serverConnector = ServerConnector.getInstance();
                                    conPath = conPath + "/" + text;
                                    serverConnector.sendMsg("GETFILELIST:" + conPath);
                                }
                            })
                    .show();
        }
    }



    private void loadFileMode(String filepath){
        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg("LOADFILES:2 " + filepath);
        Communicator.getInstance().setConPath(filepath);

        String[] list = filepath.split("/");
        serverConnector.setRoomName(list[list.length - 1]);
        firstLoad = true;
    }


    /**
     * create the new file & input the name of file
     * @param oldname oldname of file
     * @param mode work mode
     */
    private void CreateFile(String oldname, String mode){
        new XPopup.Builder(this)
                .asInputConfirm("CreateRoom", "Input the name of the new Room",
                new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String text) {
                        ServerConnector serverConnector = ServerConnector.getInstance();
                        switch (mode){
                            case "0":
                                serverConnector.sendMsg("LOADFILES:0 " + oldname + " " + conPath + "/" + text);
                                Communicator.getInstance().setConPath(conPath + "/" + text);
                                Communicator.Path = conPath + "/" + text;
                                serverConnector.setRoomName(text);
                                copyFile = true;
                                break;
                            case "1":
                                serverConnector.sendMsg("LOADFILES:1 " + oldname + " " + conPath + "/" + text);
                                Communicator.getInstance().setConPath(conPath + "/" + text);
                                Communicator.Path = conPath + "/" + text;
                                serverConnector.setRoomName(text);
                                copyFile = true;
                                break;
                        }
                    }
                })
                .show();
    }




    /**
     * load Big Data
     */
    private void loadBigData(){

        conPath = "";

        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerConnector.getInstance().sendMsg("GETFILELIST:" + "/", true, true);
            }
        }).start();

    }


    private void showRoomID(){

        new XPopup.Builder(this).asConfirm("Collaboration Room", "Room name: " + ServerConnector.getInstance().getRoomName() + "\n\n"
                        + "Room ID: " + MsgConnector.getInstance().getPort(),
                new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                    }
                })
                .show();
    }


    private void showUserList(){
        String [] userList = (String[]) MsgConnector.userList.toArray();
        String [] list = new String[ userList.length + 1 ];
        list[ userList.length ] = "invite friend to join...";
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



    private void updateUserList(List<String> newUserList){

        for (int i = 0; i < newUserList.size(); i++){
            if (!MsgConnector.userList.contains(newUserList.get(i)) && newUserList.get(i) != username){
                Toast_in_Thread("User " + newUserList.get(i) + " join !");
            }
        }

        for (int i = 0; i < MsgConnector.userList.size(); i++){
            if (!newUserList.contains(MsgConnector.userList.get(i))){
                Toast_in_Thread("User " + MsgConnector.userList.get(i) + " left !");
            }
        }

        MsgConnector.userList = newUserList;
    }



    private void showFriendsList(String [] userList){
        List<String> friends = NIMClient.getService(FriendService.class).getFriendAccounts();
        String [] friendList = new String[friends.size()];
        for (int i = 0; i < friends.size(); i++){
            friendList[i] = friends.get(i);
        }
        new XPopup.Builder(this)
                .asCenterList("Friend List", friendList,
                        new OnSelectListener(){
                            @Override
                            public void onSelect(int position, String text) {

                                for (int i = 0; i < userList.length; i++) {
                                    if (userList[i].equals(text)){
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

                                InviteAttachment attachment = new InviteAttachment(nickname, communicator.Path,  communicator.getInitSomaMsg());
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

    private void initNim(){
        registerSystemMessageObservers(true);
    }


    /**
     * 注册/注销系统消息未读数变化
     */
    private void registerSystemMessageObservers(boolean register) {
        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(
                sysMsgUnreadCountChangedObserver, register);

        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(inviteMessageObserver, true);
    }

    private Observer<Integer> sysMsgUnreadCountChangedObserver = (Observer<Integer>) unreadCount -> {
        Log.e("Observer<Integer>","Observer unreadCount");
        SystemMessageUnreadManager.getInstance().setSysMsgUnreadCount(unreadCount);
        ReminderManager.getInstance().updateContactUnreadNum(unreadCount);
    };



    private Observer<List<IMMessage>> inviteMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> imMessages) {
//            Toast_in_Thread_static("Receive Msg");
            if (isTopActivity()) {
                for (int i = 0; i < imMessages.size(); i++) {
                    if (imMessages.get(i).getMsgType() == MsgTypeEnum.custom) {
                        MsgAttachment attachment = imMessages.get(i).getAttachment();
                        if (attachment instanceof InviteAttachment) {
//                        Toast_in_Thread_static("Receive Invite");
                            String data = attachment.toJson(false);
//                        Toast_in_Thread_static(data);
                            Log.d(TAG, "Invite data: " + data);

                            data = data.replaceAll("\"", "");
                            data = data.replaceAll("\\u007B", "");
                            data = data.replaceAll("\\}", "");
                            Log.d(TAG, "Invite data: " + data);

                            String[] informs = data.split(",");
                            String invitor = informs[0].split(":")[2];
                            String path = informs[1].split(":")[1];
                            String soma = informs[2].split(":")[1];

                            invitePopup(getContext(), invitor, path, soma);
                        }
                    }
                }
            }
        }
    };



    private static void invitePopup(Context context, String invitor, String path, String soma){
        Log.d(TAG, "invitePopup: " + invitor + " " + path + " " + soma);
        String[] list = path.split("/");
        String roomName = list[list.length - 1];
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asConfirm("INVITE", invitor + " is inviting you to join game in room " + roomName, "Reject", "Join",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {

                                Communicator communicator = Communicator.getInstance();
                                communicator.initSoma(soma);
                                communicator.setConPath(path);
                                Communicator.Path = path;
                                Communicator.BrainNum = path.split("/")[1];
                                conPath = path;
                                firstLoad = true;

                                ServerConnector serverConnector = ServerConnector.getInstance();
                                serverConnector.sendMsg("LOADFILES:2 " + path);

//                                String[] list = path.split("/");
                                serverConnector.setRoomName(roomName);
                            }
                        }, new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        }, false)
        .show();
    }


    private static void acceptInvitation(String path, String soma){

        String[] list = path.split("/");
        String roomName = list[list.length - 1];

        Communicator communicator = Communicator.getInstance();
        communicator.initSoma(soma);
        communicator.setConPath(path);
        Communicator.Path = path;
        Communicator.BrainNum = path.split("/")[1];
        conPath = path;
        firstLoad = true;

        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg("LOADFILES:2 " + path);
        serverConnector.setRoomName(roomName);
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
     * open file
     */
    public void File_icon(){

        new XPopup.Builder(this)
                .asCenterList("File Open", new String[]{"Open BigData", "Open LocalFile", "Load SwcFile"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text) {

                                    case "Open LocalFile":
                                        loadLocalFile();
                                        break;
                                    case "Open BigData":
                                        if(ifGuestLogin){
                                            Toast.makeText(MainActivity.this,"you are now logged in as a visitor and need to log in to use this function",Toast.LENGTH_SHORT).show();
                                            break;
                                        }else{
                                            loadBigData();
                                        }
                                        break;

                                    case "Load SwcFile":
                                        LoadSwcFile();
                                        break;

//                                    case "Open DemoFile":
//                                        openDemoFile();
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


    private void loadLocalFile(){
        ifLoadLocal = true;
        ifImport = false;
        ifAnalyze = false;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent,1);
        }catch (Exception e){
            e.printStackTrace();
            ToastEasy("Error when open file!" + e.getMessage());
        }
    }

//    private void openDemoFile(){
//        new XPopup.Builder(this)
//                .asCenterList("More Functions...", new String[]{"DemoData1", "DemoData2", "DemoData3", "DemoData4", "DemoData5"},
//                        new OnSelectListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.N)
//                            @Override
//                            public void onSelect(int position, String text) {
//                                Log.e(TAG,"File name: " + text);
//                                myrenderer.loaDemoFile("Demo:" + text);
//                                myGLSurfaceView.requestRender();
//                                setFileName(text);
//                            }
//                        })
//                .show();
//    }



    /**
     * for draw button
     * @param v
     */
    private void Draw_list(View v){
        String[] drawList = isBigData_Remote ? new String[]{"For Marker", "For Curve", "Exit Drawing Mode"} : new String[]{"For Marker", "For Curve", "Clear Tracing", "Exit Drawing Mode"};
        drawPopupView = new XPopup.Builder(this)
                .atView(v)
                .autoDismiss(false)
                .asAttachList(drawList,
                        new int[]{}, new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                        soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                        switch (text){
                            case "PinPoint   ":
                                if (!myrenderer.ifImageLoaded()){
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
//                                  ll_top.removeView(buttonUndo_i);
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
        String[] processList = isBigData_Remote ? new String[]{"Draw Curve", "Delete Curve", "Split       ", "Set PenColor", "Change PenColor"}
                                                : new String[]{"Draw Curve", "Delete Curve", "Split       ", "Set PenColor", "Change PenColor", "Change All PenColor"};
        new XPopup.Builder(this)
                .atView(v)
                .offsetX(580)
                .asAttachList(processList, new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text){
                                    case "Draw Curve":
                                        if (!myrenderer.ifImageLoaded()){
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
                                        ifZooming = false;
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
                                        ifZooming = false;
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



    public void penSet(){
        String [] pcolor = new String[1];
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
                        String color  = pcolor[0];

                        if( !color.isEmpty()){

//                            myrenderer.pencolorchange(Integer.parseInt(color));;
                            myrenderer.pencolorchange(PenColor.valueOf(color).ordinal());
                            System.out.println("pen color is");
                            System.out.println(color);
                            ToastEasy("penColor set ~ ");
                        }else{
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

    public void markerPenSet(){
        String [] pcolor = new String[1];
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

                        if( !color.isEmpty()){

                            myrenderer.markercolorchange(PenColor.valueOf(color).ordinal());
                            System.out.println("marker color is");
                            System.out.println(color);
                            ToastEasy("markerColor set ~");

                        }else{
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




    /**
     * function for the Tracing button
     * @param v the button: tracing
     */
    private void Tracing(final View v) {

        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            ToastEasy("Please load a 3d image first !");
            return;
        }

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"APP2", "GD", "Save SwcFile"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text) {
                                    case "GD":
                                        try {
                                            Log.v(TAG, "GD-Tracing start ~");
                                            ToastEasy("GD-Tracing start !");
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
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        break;

                                    case "APP2":
                                        try {
                                            Log.v(TAG, "APP2-Tracing start~");
                                            ToastEasy("APP2-Tracing start !");
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
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "Save SwcFile":
                                        SaveSWC();
                                        break;

                                    default:
                                        break;
                                }
                            }
                        })
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
                        if (swcFileName == ""){
                            ToastEasy("The name should not be empty !");
                        }
                        myrenderer.reNameCurrentSwc(swcFileName);

                        String dir_str = "/storage/emulated/0/Hi5/SwcSaved";
                        File dir = new File(dir_str);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        String error = null;
                        try {
                            error = myrenderer.saveCurrentSwc(dir_str);
                        } catch (Exception e) {
                            ToastEasy(e.getMessage());
                        }
                        if (!error.equals("")) {
                            if (error.equals("This file already exits")){
                                AlertDialog aDialog = new AlertDialog.Builder(mainContext)
                                        .setTitle("This file already exits")
                                        .setMessage("Are you sure to overwrite it?")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String errorMessage = "";
                                                try{
                                                    errorMessage = myrenderer.oversaveCurrentSwc(dir_str);
                                                    if (errorMessage.equals(""))
                                                        ToastEasy("Overwrite successfully !");
                                                    if (errorMessage == "Overwrite failed!")
                                                        ToastEasy("Overwrite failed !");

                                                }catch (Exception e){
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
                        } else{
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void APP2() throws Exception {
        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            ToastEasy("Please load image first !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        float imgZ=0;
        boolean is2D = false;
        if(myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG){
            imgZ = Math.max((int)img.getSz0(), (int)img.getSz1()) / 2;
            is2D = true;
        }

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
                p.landmarks[i] = is2D ? new LocationSimple(markers.get(i).x, markers.get(i).y, 0):
                                    new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
            }
            System.out.println("---------------start---------------------");
            V3dNeuronAPP2Tracing.proc_app2(p);
            NeuronTree nt = p.resultNt;
            for (int i = 0; i < nt.listNeuron.size(); i++) {
                nt.listNeuron.get(i).type = 4;
                if (is2D) nt.listNeuron.get(i).z = imgZ;
//                if (nt.listNeuron.get(i).parent == -1) {s
//                    NeuronSWC s = nt.listNeuron.get(i);
//                    ImageMarker m = new ImageMarker(s.x, s.y, s.z);
//                    m.type = 2;
//                    myrenderer.getMarkerList().add(m);
//                }
            }
            System.out.println("size: " + nt.listNeuron.size());

            ToastEasy("APP2-Tracing finish, size of result swc: " + Integer.toString(nt.listNeuron.size()));
            myrenderer.importNeuronTree(nt, isBigData_Remote);
            myrenderer.saveUndo();
            myGLSurfaceView.requestRender();
            progressBar.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            ToastEasy(e.getMessage());
            progressBar.setVisibility(View.INVISIBLE);
        }


    }

    private void GDTracing() throws Exception {
        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            ToastEasy("Please load image first !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        ArrayList<ImageMarker> markers = myrenderer.getMarkerList().getMarkers();
        if (markers.size() <= 1) {
            Log.v("GDTracing", "Please generate at least two markers!");
            ToastEasy("Please produce at least two markers !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        boolean is2D = false;
        float imgZ = 0;
        if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG){
            is2D = true;
            imgZ = markers.get(0).z;
        }


        LocationSimple p0;
        p0 = is2D ? new LocationSimple(markers.get(0).x, markers.get(0).y, 0) :
                new LocationSimple(markers.get(0).x, markers.get(0).y, markers.get(0).z);
        Vector<LocationSimple> pp = new Vector<LocationSimple>();
        for (int i = 1; i < markers.size(); i++) {
            LocationSimple p = is2D ? new LocationSimple(markers.get(i).x, markers.get(i).y, 0) :
                    new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
            pp.add(p);
        }

        NeuronTree outswc = new NeuronTree();
        CurveTracePara curveTracePara = new CurveTracePara();

        try {
            outswc = V3dNeuronGDTracing.v3dneuron_GD_tracing(img, p0, pp, curveTracePara, 1.0);
        } catch (Exception e) {
            ToastEasy(e.getMessage());
            progressBar.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < outswc.listNeuron.size(); i++) {
            outswc.listNeuron.get(i).type = 5;
            if (is2D) outswc.listNeuron.get(i).z = imgZ;
        }

        ToastEasy("GD-Tracing finished, size of result swc: " + Integer.toString(outswc.listNeuron.size()));
        myrenderer.importNeuronTree(outswc,isBigData_Remote);
        myrenderer.saveUndo();
        myGLSurfaceView.requestRender();
        progressBar.setVisibility(View.INVISIBLE);

    }



    private void PixelClassification(final View v) {

        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            ToastEasy("Please load a 3d image first !");
            return;
        }

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Filter by example"}, new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text) {
                                    case "Filter by example":
                                        //调用像素分类接口，显示分类结果
                                        progressBar.setVisibility(View.VISIBLE);

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pixelClassification();
                                                puiHandler.sendEmptyMessage(6);
                                            }
                                        }).start();
                                        break;
                                }
                            }
                        })
                .show();
    }

    private void pixelClassification() {

        Image4DSimple img = myrenderer.getImg();
        if(img == null){

            ToastEasy("Please load image first !");
            return;
        }
        Image4DSimple outImg = new Image4DSimple();

        NeuronTree nt = myrenderer.getNeuronTree();
        PixelClassification p = new PixelClassification();

        boolean[][] selections = select;
        System.out.println("select is");
        System.out.println(select);
        p.setSelections(selections);

        ToastEasy("pixel  classification start !");

        try{
            outImg = p.getPixelClassificationResult(img,nt);
            System.out.println("outImg: "+outImg.getSz0()+" "+outImg.getSz1()+" "+outImg.getSz2()+" "+outImg.getSz3());
            System.out.println(outImg.getData().length);

            myrenderer.resetImg(outImg);
            myGLSurfaceView.requestRender();
        }catch (Exception e){
            ToastEasy(e.getMessage());
        }
    }


    private void ShareScreenShot() {

        myrenderer.setTakePic(true, this);
        myGLSurfaceView.requestRender();
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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                File_icon();
                return true;

            case R.id.share:
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                ShareScreenShot();
                return true;

            case R.id.more:
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                More_icon();
                return true;

            case R.id.view:
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
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
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastEasy("Press again to exit the program");
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
        if(ifGuestLogin)
        {
            new XPopup.Builder(this)
                    .asCenterList("More Functions...", new String[]{"Login", "Analyze Swc", "Animate", "Crash Info", "Settings", "Help", "About"},
                            new OnSelectListener() {
                                @Override

                                public void onSelect(int position, String text) {
                                    soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                    switch (text) {
                                        case "Login":
                                            login();
                                            break;

                                        case "Analyze Swc":
                                            AnalyzeSwc();
                                            break;

                                        case "Animate":
                                            if (myrenderer.ifImageLoaded()) {
                                                ifPainting = false;
                                                ifPoint = false;
                                                ifDeletingMarker = false;
                                                ifDeletingLine = false;
                                                ifSpliting = false;
                                                ifChangeLineType = false;
                                                ifZooming = false;
                                                setAnimation();
                                            } else {
                                                ToastEasy("Please Load a Img First !");
                                            }
                                            break;

                                        case "Account Name":
                                            popUpUserAccount(MainActivity.this);
                                            break;

                                        case "Crash Info":
                                            CrashInfoShare();
                                            break;

                                        case "Settings":
                                            setSettings();
                                            break;

                                        case "About":
                                            About();
                                            break;

                                        case "Help":
                                            try {
                                                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                                                startActivity(helpIntent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        default:
                                            ToastEasy("Default in More Functions...");
                                    }
                                }
                            })
                    .show();
        }else
            new XPopup.Builder(this)
                    .asCenterList("More Functions...", new String[] {"Analyze Swc", "Chat", "Animate", "Crash Info", "Quests", "Reward", "LeaderBoard", "Logout", "Settings", "Help", "About"},
                            new OnSelectListener() {
                                @Override

                                public void onSelect(int position, String text) {
                                    soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                    switch (text) {
                                        case "Analyze Swc":
                                            AnalyzeSwc();
                                            break;

                                        case "Animate":
                                            if (myrenderer.ifImageLoaded()){
                                                ifPainting = false;
                                                ifPoint = false;
                                                ifDeletingMarker = false;
                                                ifDeletingLine = false;
                                                ifSpliting = false;
                                                ifChangeLineType = false;
                                                ifZooming = false;
                                                setAnimation();
                                            }else {
                                                ToastEasy("Please Load a Img First !");
                                            }
                                            break;

                                        case "Chat":
                                            openChatActivity();
                                            break;

                                        case "Game":
    //                                        System.out.println("Game Start!!!!!!!");
    //
    //                                        ifGame = true;
    //                                        Select_map();
                                            break;

                                        case "Account Name":
                                            popUpUserAccount(MainActivity.this);
                                            break;

                                        case "Logout":
                                            logout();
                                            break;

                                        case "Crash Info":
                                            CrashInfoShare();
                                            break;

                                        case "Quests":
                                            startActivity(new Intent(MainActivity.this, QuestActivity.class));
                                            break;

                                        case "Achievements":
                                            showAchievementFinished();
                                            break;

                                        case "LeaderBoard":
                                            startActivity(new Intent(MainActivity.this, LeaderBoardActivity.class));
                                            break;

                                        case "Reward":
                                            startActivity(new Intent(MainActivity.this, RewardActivity.class));
                                            break;

                                        case "Settings":
                                            setSettings();
                                            break;

                                        case "About":
                                            About();;
                                            break;

                                        case "Help":
                                            try{
                                                Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                                                startActivity(helpIntent);
                                            } catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            break;

                                        default:
                                            ToastEasy("Default in More Functions...");

                                    }
                                }
                            })
                    .show();

    }


    private void openChatActivity(){
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
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
                                        Rotation_i.setVisibility(View.VISIBLE);
                                        break;

                                    default:
                                        ToastEasy("There is something wrong in animation");
                                }
                            }
                        })
                .show();

    }


    private void Rotation() {

        if (myrenderer.myAnimation != null && myrenderer.ifImageLoaded()){
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
                .asConfirm("Hi5: VizAnalyze Big 3D Images", "By Peng lab @ BrainTell. \n\n" +

                                "Version: 20210803a 11:39 UTC+8 build",

                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                            }
                        })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
    }


    public void popUpUserAccount(Context context){
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


    private void AnalyzeSwc() {
        new XPopup.Builder(this)
                .asCenterList("morphology calculate", new String[]{"Analyze a Swc file", "Analyze current tracing"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Analyze a Swc file":
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
                                            ToastEasy("Empty tracing, do nothing");
                                            break;
                                        }
                                        MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
                                        List<double[]> features = morphologyCalculate.calculatefromNT(nt, false);
                                        fl = new ArrayList<double[]>(features);
                                        if (features.size() != 0) displayResult(features);
                                        else ToastEasy("The file is empty");
                                        break;

                                    default:
                                        ToastEasy("Default in analysis");

                                }
                            }
                        })
                .show();


    }

    MDDialog.Builder ar_mdDialog_bd = new MDDialog.Builder(this).setContentView(R.layout.analysis_result);
    MDDialog ar_mdDialog = null;

    /**
     * display the result of morphology calculate
     * @param featureList the features of result
     */
    @SuppressLint("DefaultLocale")
    private void displayResult(final List<double[]> featureList) {
        final String[] title;
        final int[] id_title;
        final int[] id_content;
        final int[] id_rl;
        if (measure_count > featureList.size() - 1) {
            measure_count = 0;
        } else if (measure_count < 0) {
            measure_count = featureList.size() - 1;
        }
        double[] result = featureList.get(measure_count);
        String[] subtitle = new String[featureList.size()];
        for (int i = 0; i < featureList.size(); i++) {
            if (featureList.size() > 1) {
                subtitle[i] = String.format("Tree %d/%d", i + 1, featureList.size());
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
        ar_mdDialog = ar_mdDialog_bd
                        .setContentView(R.layout.analysis_result)
                        .setContentViewOperator(new MDDialog.ContentViewOperator() {
                            @Override
                            public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                                //analysis_result next page
                                Button ar_right = (Button) contentView.findViewById(R.id.ar_right);
                                ar_right.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (featureList.size() > 1) {
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
                                        if (featureList.size() > 1) {
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
        ar_mdDialog.show();
    }


    /**
     * format output of morphology feature's value
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


    private void setSettings(){
        boolean [] downsample = new boolean[1];

        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.settings)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        Switch downsample_on_off = contentView.findViewById(R.id.switch_rotation_mode);
                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.indicator_seekbar);
                        TextView clean_cache = contentView.findViewById(R.id.clean_cache);
                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);
                        Spinner bgmSpinner = contentView.findViewById(R.id.bgm_spinner);

                        boolean ifDownSample = preferenceSetting.getDownSampleMode();
                        int contrast = preferenceSetting.getContrast();

                        downsample_on_off.setChecked(ifDownSample);
                        seekbar.setProgress(contrast);
                        bgmVolumeBar.setProgress((int)(bgmVolume * 100));
                        buttonVolumeBar.setProgress((int)(buttonVolume * 100));
                        actionVolumeBar.setProgress((int)(actionVolume * 100));

                        if(ifGuestLogin == false){
                            RewardLitePalConnector rewardLitePalConnector = RewardLitePalConnector.getInstance();
                            List<Integer> rewards = rewardLitePalConnector.getRewards();
                            List<String> list = new ArrayList<>();
                            list.add("BGM0");
                            for (int i = 0; i < rewards.size(); i++){
                                if (rewards.get(i) == 1)
                                    list.add("BGM" + Integer.toString(i+1));
                            }
                            String [] spinnerItems = new String[list.size()];
                            for (int i = 0; i < list.size(); i++){
                                spinnerItems[i] = list.get(i);
                            }

                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mainContext, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
                            bgmSpinner.setAdapter(spinnerAdapter);
                            bgmSpinner.setSelection(selectedBGM);

                        }


                        downsample[0] = downsample_on_off.isChecked();

                        downsample_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                downsample[0] = isChecked;
                            }
                        });


                        clean_cache.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cleanCache();
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
                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.indicator_seekbar);
                        int contrast = seekbar.getProgress();

                        myrenderer.setIfNeedDownSample(downsample[0]);
                        myrenderer.resetContrast(contrast);

                        Log.v(TAG,"downsample: " + downsample[0] + ",contrast: " + contrast);
                        preferenceSetting.setPref(downsample[0], contrast);
                        myGLSurfaceView.requestRender();

                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);

                        bgmVolume = (float)(bgmVolumeBar.getProgress()) / 100.0f;
                        buttonVolume = (float)(buttonVolumeBar.getProgress()) / 100.0f;
                        actionVolume = (float)(actionVolumeBar.getProgress()) / 100.0f;

                        if(!ifGuestLogin){
                            Spinner bgmSpinner = contentView.findViewById(R.id.bgm_spinner);
                            String selected = bgmSpinner.getSelectedItem().toString();
                            if (selectedBGM != bgmSpinner.getSelectedItemPosition()) {
                                if (selected.equals("BGM1"))
                                    MusicServer.setBgmSource(getApplicationContext().getExternalFilesDir(null) + "/Resources/Music/CoyKoi.mp3");

                                else if (selected.equals("BGM2"))
                                    MusicServer.setBgmSource(getApplicationContext().getExternalFilesDir(null) + "/Resources/Music/DelRioBravo.mp3");

                                else
                                    MusicServer.defaultBgmSource();

                                selectedBGM = bgmSpinner.getSelectedItemPosition();

                            }

                        }


                        MusicServer.setBgmVolume(bgmVolume);
                        MusicServer.setVolume(bgmVolume);

                        String settingsPath = context.getExternalFilesDir(null).toString() + "/Settings";
                        File settingsFile = new File(settingsPath);
                        if (!settingsFile.exists()){
                            settingsFile.mkdir();
                        }

                        String volumePath = settingsPath + "/volume.txt";
                        File volumeFile = new File(volumePath);
                        if (!volumeFile.exists()){
                            try {
                                volumeFile.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            BufferedWriter volumeWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(volumeFile)));
                            volumeWriter.write(Float.toString(bgmVolume) + " " + Float.toString(buttonVolume) + " " + Float.toString(actionVolume));
                            volumeWriter.flush();
                            volumeWriter.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
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
    }


    public void cleanCache(){
        AlertDialog aDialog = new AlertDialog.Builder(mainContext)
                .setTitle("Clean All The Img Cache")
                .setMessage("Are you sure to CLEAN ALL IMG CACHE?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImg();
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


    private void deleteImg(){
        Log.v("BaseActivity","deleteImg()");
        String img_path = context.getExternalFilesDir(null).toString() + "/Img";
        Log.v("BaseActivity","img_path" + img_path);

        File file = new File(img_path);
        recursionDeleteFile(file);
    }


    private void logout(){

        AlertDialog aDialog = new AlertDialog.Builder(mainContext)
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

                        PreferenceLogin preferenceLogin = new PreferenceLogin(MainActivity.this);
                        preferenceLogin.setPref("","",false);
                        // DemoCache.clear();

                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

    private void login(){

        AlertDialog aDialog = new AlertDialog.Builder(mainContext)
                .setTitle("Log in")
                .setMessage("Are you sure to Log in?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 清理缓存&注销监听&清除状态
                        NimUIKit.logout();
                        NIMClient.getService(AuthService.class).logout();

//                        AgoraMsgManager.getInstance().getRtmClient().logout(null);

                        PreferenceLogin preferenceLogin = new PreferenceLogin(MainActivity.this);
                        preferenceLogin.setPref("","",false);
                        // DemoCache.clear();

                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
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




    private void CrashInfoShare(){
        String[] info_path = CrashHandler.getCrashReportFiles(getApplicationContext());
        new XPopup.Builder(this)
                .maxHeight(1350)
                .asCenterList("Select a Crash Report", info_path,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                String file_path = CrashHandler.getCrashFilePath(getApplicationContext()) + "/" + text + ".txt";
                                File file = new File(file_path);
                                if (file.exists()){
                                    Intent intent = new Intent();

                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "com.penglab.hi5.provider", new File(file_path)));  //传输图片或者文件 采用流的方式
                                    intent.setType("*/*");   //分享文件
                                    startActivity(Intent.createChooser(intent, "Share From C3"));
                                }else {
                                    Toast_in_Thread("File does not exist");
                                }
                            }
                        })
                .show();
    }


    private void setAnimation() {

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

                        if (rotation_speed_string.isEmpty()){
                            ToastEasy("Make sure the input is right !");
                            setAnimation();
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
        }
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
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG,"onActivityResult start");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            myrenderer.setPath(showPic.getAbsolutePath());
            myGLSurfaceView.requestRender();
            return;
        }

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String filePath = uri.toString();
            Log.v(TAG, filePath);

            try {
                if (ifImport) {

                    FileManager fileManager = new FileManager();
                    String fileName = fileManager.getFileName(uri);
                    String filetype = fileName.substring(fileName.lastIndexOf(".")).toUpperCase();
                    Log.v(TAG,"FileType: " + filetype + ", FileName: " + fileName);

                    if (myrenderer.getIfFileLoaded()) {
                        switch (filetype) {
                            case ".APO":
                                ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
                                ApoReader apoReader = new ApoReader();
                                apo = apoReader.read(uri);
                                if (apo == null){
                                    ToastEasy("Make sure the .apo file is right");
                                    break;
                                }

                                myrenderer.importApo(apo);
                                myrenderer.saveUndo();
                                break;
                            case ".SWC":
                            case ".ESWC":
                                NeuronTree nt = NeuronTree.readSWC_file(uri);
                                myrenderer.importNeuronTree(nt,false);
                                myrenderer.saveUndo();
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

                                myrenderer.importNeuronTree(nt2,false);
                                myrenderer.importApo(ano_apo);
                                myrenderer.saveUndo();
                                break;

                            default:
                                ToastEasy("Unsupported file type");
                        }
                    }

                    else {
                        System.out.println("-------- open --------");
                        myrenderer.setSwcPath(filePath);
                        ifLoadLocal = false;
                        setButtonsImport();
                    }
                    ifImport = false;
                }


                if (ifAnalyze) {
                    MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
                    List features = morphologyCalculate.calculate(uri, false);

                    if (features != null) {
                        fl = new ArrayList<double[]>(features);
                        displayResult(features);
                    }
                }


                if (ifLoadLocal) {
                    Log.e(TAG,"Load Local File");
                    myrenderer.setPath(filePath);

                    setButtonsLocal();
                    String filename = FileManager.getFileName(uri);
                    setFileName(filename);
                }


            } catch (OutOfMemoryError e) {
                ToastEasy("Fail to load file");
                Log.v("Exception", e.toString());
            } catch (CloneNotSupportedException e){
                Log.v("Exception:", e.toString());
            }
        }
    }





    /**
     * for game ------------------------------------------------------------------------------------
     */
//    public void Select_map(){
//        new XPopup.Builder(this)
//                .asCenterList("Game Start", new String[]{"New Game", "Load Game"},
//                        new OnSelectListener() {
//                            @Override
//                            public void onSelect(int position, String text) {
//                                switch (text){
//                                    case "New Game":
////                                        setSelectSource("Remote Server SEU", context);
////                                        BigFileRead_Remote(ip_SEU);
//                                        setSelectSource("Remote Server Aliyun",context);
//                                        BigFileRead_Remote(ip_TencentCloud);
//
//                                        break;
//
//                                    case "Load Game":
//                                        loadGameList();
//                                        break;
//
//                                    default:
//                                        ToastEasy("Something Wrong Here");
//                                }
//                            }
//                        }).show();
//    }
//
//    private void loadGameList(){
//        String externalFileDir = context.getExternalFilesDir(null).toString();
//        String [] fileList = {"[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]"};
//        File file = new File(externalFileDir + "/Game/Archives");
//        if (file.exists()){
//            try {
//                for (int i = 0; i < 10; i++) {
//                    File tempFile = new File(externalFileDir + "/Game/Archives/Archive_" + i);
//                    if (!tempFile.exists()) {
//                        tempFile.mkdir();
//                    } else {
//                        File [] archiveFiles = tempFile.listFiles();
//                        if (archiveFiles.length > 0){
//                            fileList[i] = archiveFiles[0].getName().split(".txt")[0];
//                        }
//                    }
//                }
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        } else {
//            File parent = file.getParentFile();
//            if (!parent.exists()){
//                parent.mkdir();
//            }
//            file.mkdir();
//            for (int i = 0; i < 10; i++){
//                File tempFile = new File(externalFileDir + "/Game/Archives/Archive_" + i);
//                tempFile.mkdir();
//            }
//        }
//
//        new XPopup.Builder(this)
//                .autoDismiss(false)
//                .asCenterList("Archives", fileList,
//                        new OnSelectListener() {
//                            @Override
//                            public void onSelect(int position, String text) {
//                                if (text.equals("[Empty Archive]")){
//
//                                } else {
//                                    if (loadGame(position))
//                                        Toast_in_Thread("Loaded successfully");
//                                    else
//                                        Toast_in_Thread("Failed To Load!!!");
//                                }
//                            }
//                        }).show();
//    }
//
//    private boolean loadGame(int num){
//        String archiveImageName;
//        String archiveOffset;
//        float [] pos = new float[3];
//        float [] dir = new float[3];
//        float [] head = new float[3];
//        String externalFileDir = context.getExternalFilesDir(null).toString();
//        File file = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num);
//        if (!file.exists()){
//            file.mkdir();
//            return false;
//        }
//
//        File [] tempList = file.listFiles();
//        if(tempList.length == 0){
//            return false;
//        }
//
//        try{
//            FileInputStream inStream = new FileInputStream(tempList[0]);
//            if (inStream != null) {
//                InputStreamReader inputreader
//                        = new InputStreamReader(inStream, "UTF-8");
//                BufferedReader buffreader = new BufferedReader(inputreader);
//                String line = "";
//
//                line = buffreader.readLine();
//                archiveImageName = line;
//                String tempFilename = archiveImageName.split("/")[0];
//
//                File archiveSWCFile = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + tempFilename + ".swc");
//                if (archiveSWCFile.exists()){
//                    File newSWCFile = new File(externalFileDir + "/Game/SWCs/" + tempFilename + ".swc");
//                    if (newSWCFile.exists()){
//                        newSWCFile.delete();
//                    }
//                    newSWCFile.createNewFile();
//
//                    FileUtils.copyFile(archiveSWCFile, newSWCFile);
//                } else {
//                    return false;
//                }
//
//                File archiveFlagFile = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + tempFilename + ".txt");
//                if (archiveFlagFile.exists()){
//                    File newFlagFile = new File(externalFileDir + "/Game/Flags/" + tempFilename + ".txt");
//                    if (newFlagFile.exists()){
//                        newFlagFile.delete();
//                    }
//                    newFlagFile.createNewFile();
//
//                    FileUtils.copyFile(archiveFlagFile, newFlagFile);
//                }
//
//                line = buffreader.readLine();
//                archiveOffset = line;
//                Log.d(TAG, "LoadGame offset: " + archiveOffset);
//
//                line = buffreader.readLine();
//                pos[0] = Float.parseFloat(line.split(" ")[0]);
//                pos[1] = Float.parseFloat(line.split(" ")[1]);
//                pos[2] = Float.parseFloat(line.split(" ")[2]);
//
//                line = buffreader.readLine();
//                dir[0] = Float.parseFloat(line.split(" ")[0]);
//                dir[1] = Float.parseFloat(line.split(" ")[1]);
//                dir[2] = Float.parseFloat(line.split(" ")[2]);
//
//                line = buffreader.readLine();
//                head[0] = Float.parseFloat(line.split(" ")[0]);
//                head[1] = Float.parseFloat(line.split(" ")[1]);
//                head[2] = Float.parseFloat(line.split(" ")[2]);
//
//                line = buffreader.readLine();
//                gameLastIndexForIntent = Integer.parseInt(line);
//
//                line = buffreader.readLine();
//                gameScoreForIntent = Integer.parseInt(line);
//
//                inStream.close();//关闭输入流
//
//                gamePositionForIntent = pos;
//                gameDirForIntent = dir;
//                gameHeadForIntent = head;
//
//                gameIfNewForIntent = false;
//
//                if (archiveImageName != null && archiveOffset != null){
//                    remote_socket.disConnectFromHost();
////                    remote_socket.connectServer(ip_SEU);
//                    remote_socket.connectServer(ip_TencentCloud);
//                    remote_socket.pullImageBlockWhenLoadGame(archiveImageName, archiveOffset);
//
//                    setFilename_Remote(archiveImageName, context);
////                    setNeuronNumber_Remote(neuronNum_Backup,fileName_Backup,mContext);
//                    setoffset_Remote(archiveOffset, archiveImageName, context);
//                }
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    /**
     * for game ------------------------------------------------------------------------------------
     */

    private void setIfGuestLogin(){
        isBigData_Remote = true;
        isBigData_Local = false;
        scoreText.setVisibility(View.GONE);
        Toast.makeText(context, "you are now logged in as a visitor",Toast.LENGTH_SHORT).show();
        return;
    }



    private void BigFileRead_local(){
        String[] filename_list = bigImgReader.ChooseFile(this);
        if (filename_list != null){
            String [] str = new String[1];
            bigImgReader.ShowListDialog(this, filename_list);
        }
    }


    public void Block_navigate(String text){

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {

                if (isBigData_Remote){
                    String[] Direction = {"Left", "Right", "Top", "Bottom", "Front", "Back"};
                    if (Arrays.asList(Direction).contains(text)){
                        Log.e("Block_navigate", text);

                        Communicator communicator = Communicator.getInstance();
                        communicator.navigateBlock(text);
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
                    String filepath = "/storage/emulated/0/Hi5/Server/" + filename + ".v3draw";
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
            filename = getFilename_Remote(this);
            offset   = getoffset_Remote(this, filename);
        }
        if (isBigData_Local){
            filename = SettingFileManager.getFilename_Local(this);
            offset   = SettingFileManager.getoffset_Local(this, filename);
        }

        if (filename == null || offset == null)
            return;

        if (isBigData_Local || isBigData_Remote){

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

            String[] offset_arr = offset.split("_");
            int[] offset_arr_i = new int[4];
            for (int i =0; i<offset_arr_i.length; i++){
                offset_arr_i[i] = Integer.parseInt(offset_arr[i]);
            }

            block  = new float[]{offset_arr_i[0], offset_arr_i[1], offset_arr_i[2]};
            size   = new float[]{offset_arr_i[3], offset_arr_i[3], offset_arr_i[3]};
//            neuron = remote_socket.getImg_size_f(block);

            Log.i(TAG,Arrays.toString(block));
            Log.i(TAG,Arrays.toString(neuron));

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
                            X = normalizedX;
                            Y = normalizedY;
                            if (ifPainting || ifDeletingLine || ifSpliting || ifChangeLineType || ifDeletingMultiMarker || ifSettingROI) {
                                soundPool.play(soundId[0], actionVolume, actionVolume, 0, 0, 1.0f);
                                lineDrawed.add(X);
                                lineDrawed.add(Y);
                                lineDrawed.add(-1.0f);
                                myrenderer.setIfPainting(true);
                                requestRender();
                                Log.v("actionPointerDown", "Paintinggggggggggg");
                            }
                            if (ifPoint){
                                soundPool.play(soundId[1], actionVolume, actionVolume, 0, 0, 1.0f);
                            }
                            if (ifPainting){
                                soundPool.play(soundId[0], actionVolume, actionVolume, 0, 0, 1.0f);
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
                            } else if (!isZooming){
                                if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker && !ifChangeMarkerType && !ifDeletingMultiMarker && !ifSettingROI) {
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
//                            myrenderer.setIfPaiFting(false);
//                            requestRender();
//
//                        }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!isZooming) {
                                try {
                                    if (ifZooming) {
                                        ifZooming = false;
                                        float [] center = myrenderer.solveMarkerCenter(normalizedX, normalizedY);
                                        if (center != null) {
                                            Communicator communicator = Communicator.getInstance();
                                            communicator.navigateAndZoomInBlock((int) center[0] - 64, (int) center[1] - 64, (int) center[2] - 64);
                                            requestRender();
                                        }
                                    }
                                    if (ifPoint) {
                                        if(ifGuestLogin == false)
                                        {
                                            Score scoreInstance = Score.getInstance();
                                            scoreInstance.pinpoint();
                                        }
                                        Log.v("actionUp", "Pointinggggggggggg");
                                        if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)
                                            myrenderer.add2DMarker(normalizedX, normalizedY);
                                        else {
                                            myrenderer.setMarkerDrawed(normalizedX, normalizedY, isBigData_Remote);
                                        }
                                        Log.v("actionPointerDown", "(" + X + "," + Y + ")");
                                        requestRender();

                                    }
                                    if (ifDeletingMarker) {
                                        Log.v("actionUp", "DeletingMarker");
                                        myrenderer.deleteMarkerDrawed(normalizedX, normalizedY, isBigData_Remote);
                                        requestRender();
                                    }
                                    if (ifDeletingMultiMarker) {
                                        myrenderer.deleteMultiMarkerByStroke(lineDrawed, isBigData_Remote);
                                        requestRender();
                                    }
                                    if (ifChangeMarkerType) {
                                        myrenderer.changeMarkerType(normalizedX, normalizedY, isBigData_Remote);
                                        requestRender();
                                    }
                                    if (ifPainting ) {
                                        Vector<Integer> segids = new Vector<>();
                                        myrenderer.setIfPainting(false);
                                        if(ifGuestLogin == false)
                                        {
                                            Score scoreInstance = Score.getInstance();
                                            scoreInstance.drawACurve();

                                        }

                                        if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)
                                            myrenderer.add2DCurve(lineDrawed);
                                        else {

                                            Callable<String> task = new Callable<String>() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public String call() throws Exception {
                                                    int lineType = myrenderer.getLastLineType();
                                                        V_NeuronSWC_list [] v_neuronSWC_list = new V_NeuronSWC_list[1];
                                                        V_NeuronSWC seg = myrenderer.addBackgroundLineDrawed(lineDrawed, v_neuronSWC_list);
                                                        System.out.println("feature");
                                                        if (seg != null)
                                                        { myrenderer.addLineDrawed2(lineDrawed, seg, isBigData_Remote);
                                                        myrenderer.deleteFromCur(seg, v_neuronSWC_list[0]);
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
                                        myrenderer.setLineDrawed(lineDrawed);

                                        requestRender();
                                    }

                                    if(ifSettingROI){
<<<<<<< HEAD
=======
                                        ifSettingROI=false;
>>>>>>> e51b59b135d0080fe842e1b5d7db26a545ac2463
                                        float [] center = myrenderer.GetROICenter(lineDrawed,isBigData_Remote);
                                        if (center != null) {
                                            Communicator communicator = Communicator.getInstance();
                                            communicator.navigateAndZoomInBlock((int) center[0] - 64, (int) center[1] - 64, (int) center[2] - 64);
                                            requestRender();
                                        }
                                    }


                                    if (ifDeletingLine) {
                                        myrenderer.setIfPainting(false);
                                        myrenderer.deleteLine1(lineDrawed, isBigData_Remote);
                                        lineDrawed.clear();
                                        myrenderer.setLineDrawed(lineDrawed);
                                        requestRender();
                                    }
                                    if (ifSpliting) {
                                        myrenderer.setIfPainting(false);
                                        myrenderer.splitCurve(lineDrawed, isBigData_Remote);
                                        lineDrawed.clear();
                                        myrenderer.setLineDrawed(lineDrawed);
                                        requestRender();
                                    }
                                    if (ifChangeLineType) {
                                        myrenderer.setIfPainting(false);
                                        int type = myrenderer.getLastLineType();
                                        myrenderer.changeLineType(lineDrawed, type, isBigData_Remote);
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

            }catch (IllegalArgumentException e){
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
    public void loadBigDataImg(String filepath){
        isBigData_Remote = true;
        isBigData_Local = false;
        ifZooming = false;

        myrenderer.setPath(filepath);
        myrenderer.zoom(2.2f);
        myGLSurfaceView.requestRender();

        setButtons();
    }


    public void loadBigDataApo(String filepath){

        try {
            ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
            ApoReader apoReader = new ApoReader();
            apo = apoReader.read(filepath);
            if (apo == null){
                Toast_in_Thread("There is something wrong with apo file !");
            }

            myrenderer.importApo(Communicator.getInstance().convertApo(apo));
            myrenderer.saveUndo();
            myGLSurfaceView.requestRender();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadBigDataSwc(String filepath){
        try {
            NeuronTree nt = NeuronTree.readSWC_file(filepath);

            myrenderer.importNeuronTree(Communicator.getInstance().convertNeuronTree(nt),false);
            myrenderer.saveUndo();
            myGLSurfaceView.requestRender();
            setBigDataName();

        }catch (Exception e){
            e.printStackTrace();
        }
        hideProgressBar();
    }

    /*
    load Img Block after downloading file  ---------------------------------------------------------------
     */

    static Timer timerDownload;

    public static void showProgressBar(){
        puiHandler.sendEmptyMessage(0);
        timerDownload = new Timer();
        timerDownload.schedule(new TimerTask() {
            @Override
            public void run() {
                timeOutHandler();
            }
        },30 * 1000);
    }


    public static void hideProgressBar(){
        timerDownload.cancel();
        puiHandler.sendEmptyMessage(1);
    }


    public static void timeOutHandler(){
        hideProgressBar();
        puiHandler.sendEmptyMessage(3);
    }


    public static void showSyncBar(){
        puiHandler.sendEmptyMessage(9);
        timerDownload = new Timer();
        timerDownload.schedule(new TimerTask() {
            @Override
            public void run() {
                timeOutHandler();
            }
        },10 * 1000);
    }


    public static void hideSyncBar(){
        timerDownload.cancel();
        puiHandler.sendEmptyMessage(10);
    }


    public static void setBigDataName(){
        puiHandler.sendEmptyMessage(4);
    }


    public static void setFileName(String name){
        filename = name;

        filenametext.setText(filename);
        ll_file.setVisibility(View.VISIBLE);

//        lp_undo.setMargins(0, 240, 20, 0);
//        Undo_i.setLayoutParams(lp_undo);

        lp_up_i.setMargins(0, 360, 0, 0);
        navigation_up.setLayoutParams(lp_up_i);

        lp_nacloc_i.setMargins(20, 400, 0, 0);
        navigation_location.setLayoutParams(lp_nacloc_i);

        lp_score.setMargins(0, 380, 20, 0);
        scoreText.setLayoutParams(lp_score);

//        lp_sync_push.setMargins(0, 400, 20, 0);
//        sync_push.setLayoutParams(lp_sync_push);
//
//        lp_sync_pull.setMargins(0, 490, 20, 0);
//        sync_pull.setLayoutParams(lp_sync_pull);

//        lp_neuron_list.setMargins(0, 630, 20, 0);
//        neuron_list.setLayoutParams(lp_neuron_list);


//        lp_blue_color.setMargins(0, 540, 20, 0);
//        blue_pen.setLayoutParams(lp_blue_color);
//
//        lp_red_color.setMargins(0, 630, 20, 0);
//        red_pen.setLayoutParams(lp_red_color);

        lp_res_list.setMargins(0, 540, 20, 0);
        res_list.setLayoutParams(lp_res_list);
    }


    private static void setButtons(){
        puiHandler.sendEmptyMessage(2);
    }

    public static void setButtonsBigData(){
        if (isBigData_Remote || isBigData_Local){
            navigation_left.setVisibility(View.VISIBLE);
            navigation_right.setVisibility(View.VISIBLE);
            navigation_up.setVisibility(View.VISIBLE);
            navigation_down.setVisibility(View.VISIBLE);
            navigation_front.setVisibility(View.VISIBLE);
            navigation_back.setVisibility(View.VISIBLE);

            Zoom_in_Big.setVisibility(View.VISIBLE);
            Zoom_out_Big.setVisibility(View.VISIBLE);
            Zoom_in.setVisibility(View.GONE);
            Zoom_out.setVisibility(View.GONE);

            if (isBigData_Remote){
                    res_list.setVisibility(View.VISIBLE);
                    user_list.setVisibility(View.VISIBLE);
                    room_id.setVisibility(View.VISIBLE);
                    manual_sync.setVisibility(View.VISIBLE);
                    ROI_i.setVisibility(View.VISIBLE);

            }
        }
    }

    public void setButtonsLocal(){
        if (isBigData_Remote || isBigData_Local){
            if (isBigData_Remote){
                res_list.setVisibility(View.GONE);
                user_list.setVisibility(View.GONE);
                room_id.setVisibility(View.GONE);
                manual_sync.setVisibility(View.GONE);
                ROI_i.setVisibility(View.GONE);
            }
            isBigData_Remote = false;
            isBigData_Local  = false;

            try {

                Zoom_in.setVisibility(View.VISIBLE);
                Zoom_out.setVisibility(View.VISIBLE);

                Zoom_in_Big.setVisibility(View.GONE);
                Zoom_out_Big.setVisibility(View.GONE);
                navigation_left.setVisibility(View.GONE);
                navigation_right.setVisibility(View.GONE);
                navigation_up.setVisibility(View.GONE);
                navigation_down.setVisibility(View.GONE);
                navigation_front.setVisibility(View.GONE);
                navigation_back.setVisibility(View.GONE);
                navigation_location.setVisibility(View.GONE);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void setButtonsImport(){
        if (isBigData_Remote || isBigData_Local){
            if (isBigData_Remote){
                res_list.setVisibility(View.GONE);
                user_list.setVisibility(View.GONE);
                room_id.setVisibility(View.GONE);
                manual_sync.setVisibility(View.GONE);
                ROI_i.setVisibility(View.GONE);
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


    private void hideButtons(){
        if (!ifButtonShowed)
            return;

        ll_top.setVisibility(View.GONE);
        ll_bottom.setVisibility(View.GONE);

        animation_i.setVisibility(View.GONE);
        Rotation_i.setVisibility(View.GONE);
        Hide_i.setVisibility(View.GONE);
        Undo_i.setVisibility(View.GONE);
        Redo_i.setVisibility(View.GONE);


        if (isBigData_Remote || isBigData_Local){
            navigation_back.setVisibility(View.GONE);
            navigation_down.setVisibility(View.GONE);
            navigation_front.setVisibility(View.GONE);
            navigation_left.setVisibility(View.GONE);
            navigation_right.setVisibility(View.GONE);
            navigation_up.setVisibility(View.GONE);

            Zoom_in_Big.setVisibility(View.GONE);
            Zoom_out_Big.setVisibility(View.GONE);

            if (isBigData_Remote) {
                res_list.setVisibility(View.GONE);
                user_list.setVisibility(View.GONE);
                room_id.setVisibility(View.GONE);
                manual_sync.setVisibility(View.GONE);
                ROI_i.setVisibility(View.GONE);
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
        Redo_i.setVisibility(View.VISIBLE);


        if (isBigData_Remote || isBigData_Local){
            navigation_back.setVisibility(View.VISIBLE);
            navigation_down.setVisibility(View.VISIBLE);
            navigation_front.setVisibility(View.VISIBLE);
            navigation_left.setVisibility(View.VISIBLE);
            navigation_right.setVisibility(View.VISIBLE);
            navigation_up.setVisibility(View.VISIBLE);

            Zoom_in_Big.setVisibility(View.VISIBLE);
            Zoom_out_Big.setVisibility(View.VISIBLE);

            if (isBigData_Remote) {
                res_list.setVisibility(View.VISIBLE);
                user_list.setVisibility(View.VISIBLE);
                room_id.setVisibility(View.VISIBLE);
                manual_sync.setVisibility(View.VISIBLE);
                ROI_i.setVisibility(View.VISIBLE);
            }

        }else {
            Zoom_in.setVisibility(View.VISIBLE);
            Zoom_out.setVisibility(View.VISIBLE);
        }

        ifButtonShowed = true;
    }


    public static void updateScore(){
        puiHandler.sendEmptyMessage(7);
    }


    private void addScore(int s){
        score += s;
        updateScoreText();
    }

    private static void updateScoreText(){
        puiHandler.sendEmptyMessage(7);
    }

    private static void updateScoreTextHandler(){
        Score scoreInstance = Score.getInstance();
        int score = scoreInstance.getScore();
        String scoreString;
        if (score < 10){
            scoreString = "0000" + Integer.toString(score);
        } else if (score >= 10 && score < 100){
            scoreString = "000" + Integer.toString(score);
        } else if (score >= 100 && score < 1000){
            scoreString = "00" + Integer.toString(score);
        } else if (score >= 1000 && score < 10000){
            scoreString = "0" + Integer.toString(score);
        } else {
            scoreString = Integer.toString(score);
        }
        Log.d("UpdateScore", Integer.toString(score) + "   " + scoreString);
        scoreText.setText(scoreString);
    }

    public void showAchievementFinished(){
        new XPopup.Builder(mainContext)
                .offsetY(1000)
                .popupAnimation(PopupAnimation.TranslateAlphaFromBottom)
                .asCustom(new AchievementPopup(mainContext))
                .show();
    }

    public void initDataBase(int serverScore){
        DailyQuestsContainer.initId(username);
        Score.initId(username);
        ScoreLitePalConnector.initUser(username);
        RewardLitePalConnector.initUserId(username);

        Score score = Score.getInstance();
        if (!score.initFromLitePal()) {
            setScore(score.getScore());
        }

        updateScoreText();
    }

    public void initDataBase(){
        DailyQuestsContainer.initId(username);
        Score.initId(username);
        ScoreLitePalConnector.initUser(username);
        RewardLitePalConnector.initUserId(username);

        Score score = Score.getInstance();
        if (score.initFromLitePal()) {
            setScore(score.getScore());
        }

        updateScoreText();
    }





    public static void Toast_in_Thread_static(String message){
        Message msg = new Message();
        msg.what = TOAST_INFO_STATIC;
        Bundle bundle = new Bundle();
        bundle.putString("Toast_msg",message);
        msg.setData(bundle);
        puiHandler.sendMessage(msg);
    }

    public static Context getContext() {
        return context;
    }






    /*
    functions for old version bigdata  ---------------------------------------------------------------------------------
     */

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
        ToastEasy("Current offset: " + "x: " + offset_x + " y: " + offset_y + " z: " + offset_z);
        myGLSurfaceView.requestRender();

        setSelectSource("Local Server",context);
        setButtons();

    }


//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public static void LoadBigFile_Remote(String filepath){
//
//        Log.v("MainActivity","LoadBigFile_Remote()");
//
//        if (ifGame){
//
//            Log.v("MainActivity","LoadBigFile_Remote() ifGame");
//            try {
//                Log.v("GameIntent", "inNNNNNNNNNNNNNNNNNNNNN");
//                Intent gameIntent = new Intent(mainContext, GameActivity.class);
//                gameIntent.putExtra("FilePath", filepath);
//                gameIntent.putExtra("Position", gamePositionForIntent);
//                gameIntent.putExtra("Dir", gameDirForIntent);
//                gameIntent.putExtra("Head", gameHeadForIntent);
//                gameIntent.putExtra("LastIndex", gameLastIndexForIntent);
//                gameIntent.putExtra("IfNewGame", gameIfNewForIntent);
//                gameIntent.putExtra("Score", gameScoreForIntent);
//                mainContext.startActivity(gameIntent);
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
//            Log.v("MainActivity","LoadBigFile_Remote() ifGame");
//            Log.v("MainActivity",remote_socket.getIp());
//            if (remote_socket.getIp().equals(ip_TencentCloud)){
//                setSelectSource("Remote Server Aliyun",context);
//            } else if (remote_socket.getIp().equals(ip_SEU)){
//                setSelectSource("Remote Server SEU",context);
//            }
//
//            myrenderer.setPath(filepath);
//            myrenderer.zoom(2.2f);
//            setBigDataName();
//
//            System.out.println("------" + filepath + "------");
//            isBigData_Remote = true;
//            isBigData_Local = false;
//            myGLSurfaceView.requestRender();
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
//        myrenderer.importMarker(marker_list);
//        myGLSurfaceView.requestRender();
//
//    }



    /*
    functions for old version bigdata  ---------------------------------------------------------------------------------
     */





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


        ArrayList<Float> tangent = myrenderer.tangentPlane(startPoint[0], startPoint[1], startPoint[2], dir[0], dir[1], dir[2], 1);

        System.out.println("TangentPlane:::::");
        System.out.println(tangent.size());



        float [] vertexPoints = new float[sec_anti.size()];
        for (int i = 0; i < sec_anti.size(); i++){

            vertexPoints[i] = sec_anti.get(i);
            System.out.print(vertexPoints[i]);
            System.out.print(" ");
            if (i % 3 == 2){
                System.out.print("\n");
            }
        }

//        boolean gameSucceed = myrenderer.driveMode(vertexPoints, dir);
//        if (!gameSucceed){
//            Toast.makeText(context, "wrong vertex to draw", Toast.LENGTH_SHORT);
//        } else {
//            myGLSurfaceView.requestRender();
//        }
    }

    public static void setIfGame(boolean b){
        ifGame = b;
    }








    private boolean isTopActivity(){
        ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;
        if(runningTaskInfos != null){
            cmpNameTemp = runningTaskInfos.get(0).topActivity.toString();
        }
        if(cmpNameTemp == null){
            return false;
        }
        Log.d(TAG, "isTopActivity" + cmpNameTemp);
        return cmpNameTemp.equals("ComponentInfo{com.penglab.hi5/com.penglab.hi5.core.MainActivity}");
    }
//
//
//    private void PullSwc_block_Manual(boolean isDrawMode){
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String SwcFilePath = remote_socket.PullSwc_block(isDrawMode);
//
//                if (SwcFilePath.equals("Error")){
//                    Toast_in_Thread("Something Wrong When Pull Swc File !");
//                }
//
//                try {
//                    NeuronTree nt = NeuronTree.readSWC_file(SwcFilePath);
//                    myrenderer.setSwcLoaded();
//                    myrenderer.importNeuronTree(nt,false);
//                    myGLSurfaceView.requestRender();
////                    uiHandler.sendEmptyMessage(1);
//                }catch (Exception e){
//                    Toast_in_Thread("Some Wrong when open the Swc File, Try Again Please !");
//                }
//
//            }
//        });
//
//        thread.start();
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
//    private static void PullSwc_block_Auto(boolean isDrawMode){
//
//        String SwcFilePath = remote_socket.PullSwc_block(isDrawMode);
//
//        if (SwcFilePath.equals("Error")){
//            Toast_in_Thread_static("Something Wrong When Pull Swc File !");
//            return;
//        }
//
//        try {
//            NeuronTree nt = NeuronTree.readSWC_file(SwcFilePath);
//            myrenderer.setSwcLoaded();
//            myrenderer.importNeuronTree(nt,false);
//            myGLSurfaceView.requestRender();
//        }catch (Exception e){
//            Toast_in_Thread_static("Something Wrong when open Swc File !");
//        }
//
//
//    }
//
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "IMG" + timeStamp;
//        File storageDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
//        File image = File.createTempFile(
//                imageFileName,     /* prefix */
//                ".jpg",     /* suffix */
//                storageDir         /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
//        return image;
//    }
//
//
//    private String getImageFilePath(){
//        String mCaptureDir = "/storage/emulated/0/C3/cameraPhoto";
//        File dir = new File(mCaptureDir);
//        if (!dir.exists()){
//            dir.mkdirs();
//        }
//
//        String mCapturePath = mCaptureDir + "/" + "Photo_" + System.currentTimeMillis() +".jpg";
//        return mCapturePath;
//    }
//
//
//
//    //GSDT_function
//    public void GSDT_Fun(){
//        //building...
//        Image4DSimple img = myrenderer.getImg();
//        //img.getDataCZYX();
//        if(img == null || !img.valid()){
//            Log.v("GSDT", "Please load img first!");
//            ToastEasy("Please load image first !");
//            return;
//        }
//
//        Log.v("GSDT", "Have got the image successfully!!");
//        try {
//
//            System.out.println("Start here.....");
//            ParaGSDT p = new ParaGSDT();
//            p.p4DImage = img;
//            GSDT.GSDT_Fun(p);
//            Log.v("GSDT", "GSDT function finished");
//
//            //preparations for show
//            myrenderer.resetImg(p.outImage);
//            myrenderer.getMarkerList().getMarkers().addAll(p.markers);//blue marker
//            myrenderer.getMarkerList().add(p.MaxMarker);//red marker
//            myGLSurfaceView.requestRender();
//
//            ToastEasy("marker_loc:"+ p.max_loc[0] + "," + p.max_loc[1] + "," + p.max_loc[2]);
//            progressBar.setVisibility(View.INVISIBLE);
//
//
//            /*
//            ImageMarker m = p.GSDT_Fun(img, para);
//            System.out.println("marker:"+ m.getXYZ().x + "," + m.getXYZ().y+","+m.getXYZ().z);
//            m.type = 2;
//            m.radius = 5;
//            Log.v("GSDT", "got here2");
//            markers.add(m);
//             */
//            //myGLSurfaceView.requestRender();
//
//        }catch (Exception e) {
//            ToastEasy(e.getMessage());
//            progressBar.setVisibility(View.INVISIBLE);
//        }
//
//    }







//    /**
//     * load big data
//     */
//    public void loadBigData(){
//
//        new XPopup.Builder(this)
//                .asCenterList("BigData File",new String[]{"Select File", "Open RecentBlock"},
//                        new OnSelectListener() {
//                            @RequiresApi(api = Build.VERSION_CODES.N)
//                            @Override
//                            public void onSelect(int position, String text) {
//                                switch (text) {
//                                    case "Select File":
//                                        Select_img();
//                                        break;
//
//                                    case "Open RecentBlock":
//                                        Select_Block();
//                                        break;
////
////                                    case "Download by http":
////                                        downloadFile();
////                                        break;
//                                }
//                            }
//                        })
//                .show();
//
//
//    }



//
//
//    /**
//     * init function for VoiceCall
//     */
//    private void initAgoraEngineAndJoinChannel(String Channel, String userAccount) {
//        initializeAgoraEngine(userAccount);     // Tutorial Step 1
//        joinChannel(userAccount, Channel);               // Tutorial Step 2
//    }
//
//    public final void showLongToast(final String msg) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//
//    // Tutorial Step 7
//    public void onLocalAudioMuteClicked(View view) {
//        ImageView iv = (ImageView) view;
//        if (iv.isSelected()) {
//            iv.setSelected(false);
//            iv.clearColorFilter();
//        } else {
//            iv.setSelected(true);
//            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
//        }
//
//        // Stops/Resumes sending the local audio stream.
//        mRtcEngine.muteLocalAudioStream(iv.isSelected());
//    }
//
//    // Tutorial Step 5
//    public void onSwitchSpeakerphoneClicked(View view) {
//        ImageView iv = (ImageView) view;
//        if (iv.isSelected()) {
//            iv.setSelected(false);
//            iv.clearColorFilter();
//        } else {
//            iv.setSelected(true);
//            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
//        }
//
//        // Enables/Disables the audio playback route to the speakerphone.
//        //
//        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
//        mRtcEngine.setEnableSpeakerphone(view.isSelected());
//    }
//
//    // Tutorial Step 3
//    public void onEncCallClicked(View view) {
//        leaveChannel();
//        RtcEngine.destroy();
//        mRtcEngine = null;
//
//    }
//
//    // Tutorial Step 1
//    private void initializeAgoraEngine(String userAccount) {
//        try {
//            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
//            // Sets the channel profile of the Agora RtcEngine.
//            // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
//            // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
//            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
//
//            /**
//             * register a account
//             */
//            mRtcEngine.registerLocalUserAccount(getString(R.string.agora_app_id), userAccount);
//
//        } catch (Exception e) {
//            Log.e(LOG_TAG, Log.getStackTraceString(e));
//
//            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
//        }
//    }
//
//    // Tutorial Step 2
//    private void joinChannel(String userAccount, String Channel) {
//        String accessToken = getString(R.string.agora_access_token);
//        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
//            accessToken = null; // default, no token
//        }
//
//        // 使用注册的用户 ID 加入频道
//        mRtcEngine.joinChannelWithUserAccount(accessToken, Channel, userAccount);
//
//        showLongToast("You joined Successfully !!!");
//
//
////        // Allows a user to join a channel.
////        mRtcEngine.joinChannel(accessToken, "1", "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
//    }
//
//    // Tutorial Step 3
//    private void leaveChannel() {
//        mRtcEngine.leaveChannel();
//        voicePattern = VoicePattern.UNCERTAIN;
//        chat_room_num = 0;
//    }
//
//    // Tutorial Step 4
//    private void onRemoteUserLeft(String userAccount, int reason) {
//        if (voicePattern == VoicePattern.PEER_TO_PEER){
//            showLongToast("The CALL is End !");
//        }else {
//            if (chat_room_num > 1){
//                showLongToast("user " + userAccount + " left : " + reason);
//            }else {
//                showLongToast("The CALL is End !");
//            }
//        }
//    }
//
//    // Tutorial Step 4
//    private void onRemoteUserJoined(String userAccount) {
//        showLongToast("user " + userAccount + " joined !");
//    }
//
//    // Tutorial Step 6
//    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
////        mRtcEngine.getUserInfoByUid(uid);
//        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
//    }













//    /**
//     * add friends
//     */
//    public void addFriends(){
//        MDDialog mdDialog = new MDDialog.Builder(this)
//                .setContentView(R.layout.peer_chat)
//                .setNegativeButton("Cancel", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                })
//                .setPositiveButton(R.string.btn_chat, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                })
//                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
//                    @Override
//                    public void onClick(View clickedView, View contentView) {
//                        Log.d("PeerToPeer", "Start To Chat");
//                        EditText targetEdit = (EditText)contentView.findViewById(R.id.target_name_edit);
//                        String mTargetName = targetEdit.getText().toString();
//                        if (mTargetName.equals("")) {
//                            Toast_in_Thread(getString(R.string.account_empty));
//                        } else if (mTargetName.length() >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
//                            Toast_in_Thread(getString(R.string.account_too_long));
//                        } else if (mTargetName.startsWith(" ")) {
//                            Toast_in_Thread(getString(R.string.account_starts_with_space));
//                        } else if (mTargetName.equals("null")) {
//                            Toast_in_Thread(getString(R.string.account_literal_null));
//                        } else if (mTargetName.equals(username)) {
//                            Toast_in_Thread(getString(R.string.account_cannot_be_yourself));
//                        } else {
//                            mChatManager.addFriends(mTargetName);
//                        }
//                    }
//                })
//                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
//                    @Override
//                    public void onClick(View clickedView, View contentView) {
//
//                    }
//                })
//                .setTitle(R.string.title_add_friends)
//                .create();
//
//        mdDialog.show();
//    }



//    public void chooseChatMode(){
//        new XPopup.Builder(this)
//                .asCenterList("Choose Chat Mode", new String[]{"Peer Chat", "Selection Tab Channel"},
//                        new OnSelectListener() {
//                            @Override
//                            public void onSelect(int position, String text) {
//                                switch (text){
//                                    case "Peer Chat":
//                                        peerToPeer();
//                                        break;
//                                    case "Selection Tab Channel":
//                                        chooseChannel();
//                                        break;
//                                }
//                            }
//                        }).show();
//
//    }

//    private void peerToPeer(){
//        MDDialog mdDialog = new MDDialog.Builder(this)
//                .setContentView(R.layout.peer_chat)
//                .setContentViewOperator(new MDDialog.ContentViewOperator() {
//                    @Override
//                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
//
//                    }
//                })
//                .setNegativeButton("Cancel", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                })
//                .setPositiveButton(R.string.btn_chat, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                })
//                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
//                    @Override
//                    public void onClick(View clickedView, View contentView) {
//                        Log.d("PeerToPeer", "Start To Chat");
//                        EditText targetEdit = (EditText)contentView.findViewById(R.id.target_name_edit);
//                        String mTargetName = targetEdit.getText().toString();
//                        if (mTargetName.equals("")) {
//                            Toast_in_Thread(getString(R.string.account_empty));
//                        } else if (mTargetName.length() >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
//                            Toast_in_Thread(getString(R.string.account_too_long));
//                        } else if (mTargetName.startsWith(" ")) {
//                            Toast_in_Thread(getString(R.string.account_starts_with_space));
//                        } else if (mTargetName.equals("null")) {
//                            Toast_in_Thread(getString(R.string.account_literal_null));
//                        } else if (mTargetName.equals(username)) {
//                            Toast_in_Thread(getString(R.string.account_cannot_be_yourself));
//                        } else {
//                            openMessageActivity(true, mTargetName);
////                            mChatButton.setEn
////                            jumpToMessageActivity();
//                        }
//                    }
//                })
//                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
//                    @Override
//                    public void onClick(View clickedView, View contentView) {
//
//                    }
//                })
//                .setTitle(R.string.title_peer_msg)
//
//                .create();
//
//        mdDialog.show();
//    }
//
//    private void chooseChannel(){
//        MDDialog mdDialog = new MDDialog.Builder(this)
//                .setContentView(R.layout.channel_chat)
//                .setContentViewOperator(new MDDialog.ContentViewOperator() {
//                    @Override
//                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
//
//                    }
//                })
//                .setNegativeButton("Cancel", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                })
//                .setPositiveButton(R.string.btn_join, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                })
//                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
//                    @Override
//                    public void onClick(View clickedView, View contentView) {
//                        EditText targetEdit = (EditText)contentView.findViewById(R.id.channel_name_edit);
//                        String mTargetName = targetEdit.getText().toString();
//                        if (mTargetName.equals("")) {
//                            Toast_in_Thread(getString(R.string.channel_name_empty));
//                        } else if (mTargetName.length() >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
//                            Toast_in_Thread(getString(R.string.channel_name_too_long));
//                        } else if (mTargetName.startsWith(" ")) {
//                            Toast_in_Thread(getString(R.string.channel_name_starts_with_space));
//                        } else if (mTargetName.equals("null")) {
//                            Toast_in_Thread(getString(R.string.channel_name_literal_null));
//                        }  else {
//                            openMessageActivity(false, mTargetName);
////                            mChatButton.setEn
////                            jumpToMessageActivity();
//                        }
//                    }
//                })
//                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
//                    @Override
//                    public void onClick(View clickedView, View contentView) {
//
//                    }
//                })
//                .setTitle(R.string.title_channel_message)
//
//                .create();
//
//        mdDialog.show();
//    }

//    private void openMessageActivity(boolean isPeerToPeerMode, String targetName){
//        Intent intent = new Intent(MainActivity.this, MessageActivity.class);
//        intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, isPeerToPeerMode);
//        intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, targetName);
//        intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, username);
//        startActivity(intent);
//    }









//    class MyRtmClientListener implements RtmClientListener {
//
//        @Override
//        public void onConnectionStateChanged(final int state, int reason) {
//            runOnUiThread(() -> {
//                switch (state) {
//                    case RtmStatusCode.ConnectionState.CONNECTION_STATE_RECONNECTING:
//                        Toast_in_Thread(getString(R.string.reconnecting));
//                        break;
//                    case RtmStatusCode.ConnectionState.CONNECTION_STATE_ABORTED:
//                        Toast_in_Thread(getString(R.string.account_offline));
//                        setResult(MessageUtil.ACTIVITY_RESULT_CONN_ABORTED);
//                        finish();
//                        break;
//                }
//            });
//        }
//
//        @SuppressLint("LongLogTag")
//        @Override
//        public void onMessageReceived(final RtmMessage message, final String peerId) {
//            if (isTopActivity()) {
//                Log.d("onMessageRecievedFromPeer", message.getText() + " from " + peerId);
//                String msg = message.getText();
//                if (Pattern.matches(callMsgPattern, message.getText())) {
//
//                    String targetName = msg.substring(10, msg.indexOf("##In##"));
//                    String channelName = msg.substring(msg.indexOf("##In##") + 6, msg.lastIndexOf("##"));
//                    final boolean[] answered = {false};
//                    runOnUiThread(() -> {
//
//
//                        BasePopupView calledPopup = new XPopup.Builder(mainContext)
//                                .dismissOnTouchOutside(false)
//                                .dismissOnBackPressed(false)
//                                .asConfirm("Phone Call", "from " + targetName, "Reject", "Answer",
//                                        new OnConfirmListener() {
//                                            @Override
//                                            public void onConfirm() {
//                                                answered[0] = true;
//
//                                                VoiceChat(channelName, username);
//                                                String callMessage = "##SuccessToAnswer##";
//                                                RtmMessage answerMessage = mRtmClient.createMessage();
//                                                answerMessage.setText(callMessage);
//
//                                                mRtmClient.sendMessageToPeer(targetName, answerMessage, mChatManager.getSendMessageOptions(), new ResultCallback<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(ErrorInfo errorInfo) {
//
//                                                    }
//                                                });
//                                            }
//                                        }, new OnCancelListener() {
//                                            @Override
//                                            public void onCancel() {
//                                                answered[0] = true;
//
//                                                String callMessage = "##RefuseToAnswer##";
//                                                RtmMessage refuseMessage = mRtmClient.createMessage();
//                                                refuseMessage.setText(callMessage);
//
//                                                mRtmClient.sendMessageToPeer(targetName, refuseMessage, mChatManager.getSendMessageOptions(), new ResultCallback<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(ErrorInfo errorInfo) {
//
//                                                    }
//                                                });
//                                            }
//                                        }, false);
//                        calledPopup.show();
//                        calledPopup.delayDismiss(20000);
//                        calledPopup.dismissWith(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (answered[0] == false){
//                                    String callMessage = "##TimeOutToAnswer##";
//                                    RtmMessage timeOutMessage = mRtmClient.createMessage();
//                                    timeOutMessage.setText(callMessage);
//
//                                    mRtmClient.sendMessageToPeer(targetName, timeOutMessage, mChatManager.getSendMessageOptions(), new ResultCallback<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                        }
//
//                                        @Override
//                                        public void onFailure(ErrorInfo errorInfo) {
//
//                                        }
//                                    });
//                                }
//                            }
//                        });
//
//                    });
//                } else if (msg.equals("##RefuseToAnswer##")){
//                    runOnUiThread(() -> {
//                        Toast_in_Thread("Target Refused To Answer");
//                        fab.setVisibility(View.GONE);
//                        try {
//                            leaveChannel();
//                            RtcEngine.destroy();
//                        } catch (Exception e){
//                            Toast_in_Thread(e.getMessage());
//                        }
//                        mRtcEngine = null;
//                    });
//
//                } else if (msg.equals("##SuccessToAnswer##")){
//                    runOnUiThread(() -> {
//                        Toast_in_Thread("Connection Succeeded");
//                    });
//                } else if (msg.equals("##TimeOutToAnswer##")){
//                    runOnUiThread(() -> {
//                        Toast_in_Thread("Target Time Out To Answer");
//                        fab.setVisibility(View.GONE);
//                        try {
//                            leaveChannel();
//                            RtcEngine.destroy();
//                        } catch (Exception e){
//                            Toast_in_Thread(e.getMessage());
//                        }
//                        mRtcEngine = null;
//                    });
//                } else {
//                    runOnUiThread(() -> {
//
//                        MessageUtil.addMessageBean(peerId, message);
//
//                        MsgPopup msgPopup = new MsgPopup(mainContext, 3000);
//                        msgPopup.setText(peerId + ": " + message.getText());
//                        TextView msgText = msgPopup.findViewById(R.id.msg_text);
//                        msgText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Log.d("MsgText", "OnClick");
//                                openMessageActivity(true, peerId);
//                            }
//                        });
//
//                        BasePopupView xMsgPopup = new XPopup.Builder(mainContext)
//                                .hasShadowBg(false)
//                                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
//                                .isCenterHorizontal(true)
//                                .offsetY(200)
//                                .asCustom(msgPopup);
//
//                        xMsgPopup.show();
//
//                        Log.d("onMessageReceived", "runOnUiThread");
//
//
//                    });
//                }
//            }
//        }
//
//        @SuppressLint("LongLogTag")
//        @Override
//        public void onImageMessageReceivedFromPeer(final RtmImageMessage rtmImageMessage, final String peerId) {
//            if (isTopActivity()) {
//                Log.d("onMessageRecievedFromPeer", rtmImageMessage.getText() + " from " + peerId);
//                runOnUiThread(() -> {
//                    MessageUtil.addMessageBean(peerId, rtmImageMessage);
//                    MsgPopup msgPopup = new MsgPopup(mainContext, 3000);
//                    msgPopup.setText(peerId + ": [Image]");
//                    TextView msgText = msgPopup.findViewById(R.id.msg_text);
//                    msgText.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Log.d("MsgText", "OnClick");
//                            openMessageActivity(true, peerId);
//                        }
//                    });
//                    new XPopup.Builder(mainContext)
//                            .hasShadowBg(false)
//                            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
//                            .isCenterHorizontal(true)
//                            .offsetY(200)
//                            .asCustom(msgPopup)
//                            .show();
//
//                });
//            }
//
//
////            runOnUiThread(() -> {
////                if (peerId.equals(mPeerId)) {
////                    MessageBean messageBean = new MessageBean(peerId, rtmImageMessage, false);
////                    messageBean.setBackground(getMessageColor(peerId));
////                    mMessageBeanList.add(messageBean);
////                    mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
////                    mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
////                } else {
////                    MessageUtil.addMessageBean(peerId, rtmImageMessage);
////                }
////            });
//        }
//
//        @Override
//        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {
//
//        }
//
//        @Override
//        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
//
//        }
//
//        @Override
//        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
//
//        }
//
//        @Override
//        public void onTokenExpired() {
//
//        }
//
//        @Override
//        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {
//            String[] peerName = (String[]) map.keySet().toArray();
//            int status = map.get(peerName[0]);
//            switch (status){
////                case
//            }
//
//        }
//    }




//    private void PushSWC_Block_Manual(){
//
//        String filepath = this.getExternalFilesDir(null).toString();
//        String swc_file_path = filepath + "/Sync/BlockSet";
//        File dir = new File(swc_file_path);
//
//        if (!dir.exists()){
//            if (!dir.mkdirs())
//                Toast.makeText(this,"Fail to create file: PushSWC_Block", Toast.LENGTH_SHORT).show();
//        }
//
//        String filename = getFilename_Remote(this);
//        String neuron_number = getNeuronNumber_Remote(this, filename);
//        String offset = getoffset_Remote(this, filename);
//        System.out.println(offset);
//        int[] index = BigImgReader.getIndex(offset);
//        System.out.println(filename);
//
//        String ratio = Integer.toString(remote_socket.getRatio_SWC());
//        String SwcFileName = "blockSet__" + neuron_number + "__" +
//                index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5] + "__" + ratio;
//
//        System.out.println(SwcFileName);
//
//        if (Save_curSwc_fast(SwcFileName, swc_file_path)){
//            File SwcFile = new File(swc_file_path + "/" + SwcFileName + ".swc");
//            try {
//                System.out.println("Start to push swc file");
//                InputStream is = new FileInputStream(SwcFile);
//                long length = SwcFile.length();
//
//                if (length < 0 || length > Math.pow(2, 28)){
//                    Toast_in_Thread("Something Wrong When Upload SWC, Try Again Please !");
//                    return;
//                }
//
//                remote_socket.PushSwc_block(SwcFileName + ".swc", is, length);
//
//            } catch (Exception e){
//                System.out.println("----" + e.getMessage() + "----");
//            }
//        }
//    }
//
//
//    private String[] SaveSWC_Block_Auto(){
//
//        String filepath = this.getExternalFilesDir(null).toString();
//        String swc_file_path = filepath + "/Sync/BlockSet";
//        File dir = new File(swc_file_path);
//
//        if (!dir.exists()){
//            if (!dir.mkdirs())
//                Toast.makeText(this,"Fail to create file: PushSWC_Block", Toast.LENGTH_SHORT).show();
//        }
//
//        String filename = getFilename_Remote(this);
//        String neuron_number = getNeuronNumber_Remote(this, filename);
//        String offset = getoffset_Remote(this, filename);
//        System.out.println(offset);
//        int[] index = BigImgReader.getIndex(offset);
//        System.out.println(filename);
//
//        String ratio = Integer.toString(remote_socket.getRatio_SWC());
//        String SwcFileName = "blockSet__" + neuron_number + "__" +
//                index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5] + "__" + ratio;
//
//        System.out.println(SwcFileName);
//
//        if (Save_curSwc_fast(SwcFileName, swc_file_path)){
//            return new String[]{ swc_file_path, SwcFileName };
//        }
//
//        Log.v("SaveSWC_Block_Auto","Save Successfully !");
//        return new String[]{"Error", "Error"};
//    }
//
//
//
//    private static void PushSWC_Block_Auto(String swc_file_path, String SwcFileName){
//
//        if (swc_file_path.equals("Error"))
//            return;
//
//        File SwcFile = new File(swc_file_path + "/" + SwcFileName + ".swc");
//        if (!SwcFile.exists()){
//            Toast_in_Thread_static("Something Wrong When Upload SWC, Try Again Please !");
//            return;
//        }
//        try {
//            System.out.println("Start to push swc file");
//            InputStream is = new FileInputStream(SwcFile);
//            long length = SwcFile.length();
//
//            if (length <= 0 || length > Math.pow(2, 28)){
//                Toast_in_Thread_static("Something Wrong When Upload SWC, Try Again Please !");
//                return;
//            }
//            remote_socket.PushSwc_block(SwcFileName + ".swc", is, length);
//
//        } catch (Exception e){
//            System.out.println("----" + e.getMessage() + "----");
//        }
//    }
//
//    private boolean Save_curSwc_fast(String SwcFileName, String dir_str){
//
//        System.out.println("start to save-------");
//        myrenderer.reNameCurrentSwc(SwcFileName);
//
//        String error = "init";
//        try {
//            error = myrenderer.saveCurrentSwc(dir_str);
//            System.out.println("error:" + error);
//        } catch (Exception e) {
//            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!error.equals("")) {
//            if (error.equals("This file already exits")){
//                String errorMessage = "";
//                try{
//                    errorMessage = myrenderer.oversaveCurrentSwc(dir_str);
//                    if (errorMessage == "Overwrite failed!"){
//                        Toast_in_Thread("Fail to save swc file: Save_curSwc_fast");
//                        return false;
//                    }
//                }catch (Exception e){
//                    System.out.println(errorMessage);
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            }
////            if (error.equals("Current swc is empty!")){
////                Toast_in_Thread("Current swc file is empty!");
////                return false;
////            }
//        } else{
//            System.out.println("save SWC to " + dir_str + "/" + SwcFileName + ".swc");
//        }
//        return true;
//    }


//    backup setFileName
//                    File file = new File(file_path_temp);
//                    String name = file.getName();
//                    Log.v("Handler",name);
//
//                    String result = null;
//                    if (DrawMode){
//                        String source = getSelectSource(context);
//                        if (source.equals("Remote Server Aliyun")){
//                            String filename = getFilename_Remote(context);
//                            String brain_number = getNeuronNumber_Remote(context,filename);
//                            result = name.split("RES")[0].split("_")[1] + "_" + brain_number.split("_")[1];
////                            result = name.split("_")[1].substring(0,name.split("_")[1].length()-3);
//                        }else if(source.equals("Remote Server SEU")){
//                            String filename = getFilename_Remote(context);
//                            String brain_number = getNeuronNumber_Remote(context,filename);
//                            Log.d(TAG, "brain_number: " + brain_number);
//                            Log.d(TAG, "brain_number.split(\"_\")[0]: " + brain_number.split("_")[0]);
//                            if (brain_number.split("_")[0].equals("pre")){
//                                Log.d(TAG, "brain_number.split(\"_\")[0]: " + brain_number.split("_")[0]);
//                                result = name.split("RES")[0].split("_")[1] + "_" + brain_number.split("_")[2];
//                            } else {
//                                result = name.split("RES")[0].split("_")[1] + "_" + brain_number.split("_")[1];
//                            }
//                        }
//                    }else {
//                        String brain_num = getFilename_Remote(context);
//                        String neuron_num = getNeuronNumber_Remote(context, brain_num);
//                        result = brain_num.split("_")[0] + "_" + neuron_num.split("_")[1] + "_" + getArborNum(context,brain_num.split("/")[0] + "_" + neuron_num).split(":")[0];
//                    }





//                    if (isBigData_Remote || isBigData_Local){
//                        navigation_left.setVisibility(View.VISIBLE);
//                        navigation_right.setVisibility(View.VISIBLE);
//                        navigation_up.setVisibility(View.VISIBLE);
//                        navigation_down.setVisibility(View.VISIBLE);
//                        navigation_front.setVisibility(View.VISIBLE);
//                        navigation_back.setVisibility(View.VISIBLE);
////                        navigation_location.setVisibility(View.VISIBLE);
//
//                        Zoom_in_Big.setVisibility(View.VISIBLE);
//                        Zoom_out_Big.setVisibility(View.VISIBLE);
//                        Zoom_in.setVisibility(View.GONE);
//                        Zoom_out.setVisibility(View.GONE);
//
//                        if (isBigData_Remote){
//                            if (DrawMode){
//
//
////                                Check_Yes.setVisibility(View.GONE);
////                                Check_No.setVisibility(View.GONE);
////                                Check_Uncertain.setVisibility(View.GONE);
//                                res_list.setVisibility(View.GONE);
////                                sync_pull.setVisibility(View.VISIBLE);
////                                sync_push.setVisibility(View.VISIBLE);
////                                neuron_list.setVisibility(View.VISIBLE);
//                                user_list.setVisibility(View.VISIBLE);
//                                room_id.setVisibility(View.VISIBLE);
////                                blue_pen.setVisibility(View.VISIBLE);
////                                red_pen.setVisibility(View.VISIBLE);
//                            }
//                            else {
//                                Check_Yes.setVisibility(View.VISIBLE);
//                                Check_No.setVisibility(View.VISIBLE);
//                                Check_Uncertain.setVisibility(View.VISIBLE);
//
//
////                                res_list.setVisibility(View.VISIBLE);
////                                sync_pull.setVisibility(View.VISIBLE);
////                                sync_push.setVisibility(View.GONE);
////                                neuron_list.setVisibility(View.VISIBLE);
////                                blue_pen.setVisibility(View.GONE);
////                                red_pen.setVisibility(View.GONE);
//                            }
//                        }
//                    }


}