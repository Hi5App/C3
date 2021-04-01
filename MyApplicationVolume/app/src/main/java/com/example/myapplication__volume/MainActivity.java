package com.example.myapplication__volume;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
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

import com.example.ImageReader.BigImgReader;
import com.example.basic.CrashHandler;
import com.example.basic.DragFloatActionButton;
import com.example.basic.FileManager;
import com.example.basic.Image4DSimple;
import com.example.basic.ImageMarker;
import com.example.basic.LocationSimple;
import com.example.basic.MsgPopup;
import com.example.basic.NeuronSWC;
import com.example.basic.NeuronTree;
import com.example.chat.ChatActivity;
import com.example.chat.ChatManager;
import com.example.chat.MessageActivity;
import com.example.chat.MessageUtil;
import com.example.datastore.PreferenceLogin;
import com.example.datastore.SettingFileManager;
import com.example.datastore.database.Reward;
import com.example.myapplication__volume.FileReader.AnoReader;
import com.example.myapplication__volume.FileReader.ApoReader;
import com.example.myapplication__volume.Nim.main.helper.SystemMessageUnreadManager;
import com.example.myapplication__volume.Nim.reminder.ReminderManager;
import com.example.myapplication__volume.Nim.session.extension.InviteAttachment;
import com.example.myapplication__volume.agora.AgoraService;
import com.example.myapplication__volume.agora.message.AgoraMsgManager;
import com.example.myapplication__volume.collaboration.service.CollaborationService;
import com.example.myapplication__volume.collaboration.Communicator;
import com.example.myapplication__volume.collaboration.service.ManageService;
import com.example.myapplication__volume.collaboration.MsgConnector;
import com.example.myapplication__volume.collaboration.ServerConnector;
import com.example.myapplication__volume.collaboration.basic.ReceiveMsgInterface;
import com.example.myapplication__volume.game.AchievementPopup;
import com.example.myapplication__volume.game.DailyQuestLitePalConnector;
import com.example.myapplication__volume.game.DailyQuestsContainer;
import com.example.myapplication__volume.game.LeaderBoardActivity;
import com.example.myapplication__volume.game.QuestActivity;
import com.example.myapplication__volume.game.RewardActivity;
import com.example.myapplication__volume.game.RewardLitePalConnector;
import com.example.myapplication__volume.game.Score;
import com.example.myapplication__volume.game.ScoreLitePalConnector;
import com.example.myapplication__volume.ui.login.LoginActivity;
import com.example.server_communicator.Remote_Socket;
import com.feature_calc_func.MorphologyCalculate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.learning.pixelclassification.PixelClassification;
import com.learning.randomforest.RandomForest;
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
import com.tracingfunc.app2.ParaAPP2;
import com.tracingfunc.app2.V3dNeuronAPP2Tracing;
import com.tracingfunc.gd.CurveTracePara;
import com.tracingfunc.gd.V3dNeuronGDTracing;
import com.tracingfunc.gd.V_NeuronSWC;
import com.tracingfunc.gd.V_NeuronSWC_list;
import com.tracingfunc.gsdt.GSDT;
import com.tracingfunc.gsdt.ParaGSDT;
import com.warkiz.widget.IndicatorSeekBar;

import org.apache.commons.io.FileUtils;
import org.litepal.tablemanager.Connector;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import cn.carbs.android.library.MDDialog;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;

import static com.example.datastore.SettingFileManager.getArborNum;
import static com.example.datastore.SettingFileManager.getFilename_Remote;
import static com.example.datastore.SettingFileManager.getNeuronNumber_Remote;
import static com.example.datastore.SettingFileManager.getSelectSource;
import static com.example.datastore.SettingFileManager.getUserAccount;
import static com.example.datastore.SettingFileManager.getUserAccount_Check;
import static com.example.datastore.SettingFileManager.getoffset_Remote;
import static com.example.datastore.SettingFileManager.setFilename_Remote;
import static com.example.datastore.SettingFileManager.setSelectSource;
import static com.example.datastore.SettingFileManager.setUserAccount;
import static com.example.datastore.SettingFileManager.setUserAccount_Check;
import static com.example.datastore.SettingFileManager.setoffset_Remote;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

//import org.opencv.android.OpenCVLoader;


public class MainActivity extends BaseActivity implements ReceiveMsgInterface {
    //    private int UNDO_LIMIT = 5;
//    private enum Operate {DRAW, DELETE, SPLIT};
//    private Operate [] process = new Operate[UNDO_LIMIT];
    public static final String NAME = "com.example.myapplication__volume.MainActivity";

    public static final String File_path = "com.example.myfirstapp.MESSAGE";
    private static final String TAG = "MainActivity";

    private Timer timer=null;
    private TimerTask timerTask;


    private static MyGLSurfaceView myGLSurfaceView;
    private static MyRenderer myrenderer;
    private static final String DEBUG_TAG = "Gestures";
    //    private static Context context;
    private static Context mainContext;
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
    private static Button Check_Uncertain;
    private static Button Zoom_in_Big;
    private static Button Zoom_out_Big;
    private Button Rotation;
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
    private static Button res_list;
    private static ImageButton sync_push;
    private static ImageButton sync_pull;

    private static ImageButton user_list;
    private static ImageButton room_id;


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
    private static FrameLayout.LayoutParams lp_res_list;
    private static FrameLayout.LayoutParams lp_red_color;
    private FrameLayout.LayoutParams lp_animation_i;
    private static FrameLayout.LayoutParams lp_undo;
    private static FrameLayout.LayoutParams lp_redo;

    private static FrameLayout.LayoutParams lp_room_id;
    private static FrameLayout.LayoutParams lp_user_list;

    private Button PixelClassification;
    private boolean[][]select= {{true,true,true,false,false,false,false},
            {true,true,true,false,false,false,false},
            {false,false,false,false,false,false,false},
            {false,false,false,false,false,false,false},
            {false,false,false,false,false,false,false},
            {true,true,true,false,false,false,false}};

    private Button detectLineButton;
    private RandomForest rf = null;


    //    private static RemoteImg remoteImg;
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
    private static ProgressBar progressBar;

    private CircleImageView wave;

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
        WHITE, BLACK, RED, BLUE, PURPLE, CYAN, YELLOW, GREEN
    }

    private enum VoicePattern {
        PEER_TO_PEER, CHAT_ROOM, UNCERTAIN
    }

    private VoicePattern voicePattern = VoicePattern.UNCERTAIN;
    private int chat_room_num = 0;

    private static String[] push_info_swc = {"New", "New"};
    private static String[] push_info_apo = {"New", "New"};

    private BasePopupView drawPopupView;

    private static boolean ifGame = false;

    HashMap<Integer, String> User_Map = new HashMap<Integer, String>();

    public static String USERNAME = "username";

    public static String username;

    private RtmClientListener mClientListener;

    private final String callMsgPattern = "##CallFrom.*##In##.*##";

    private static float [] gamePositionForIntent = {0.5f, 0.5f, 0.5f};
    private static float [] gameDirForIntent = {1, 1, 1};
    private static float [] gameHeadForIntent = {1, 0, -1};
    private static int gameLastIndexForIntent = -1;
    private static boolean gameIfNewForIntent = true;
    private static int gameScoreForIntent = 0;
    private SoundPool soundPool;
    private final int SOUNDNUM = 4;
    private int [] soundId;

    private ManageService manageService;
    private CollaborationService collaborationService;
    private boolean mBound;

    private int count = 0;

    private String conPath = "";

    private float bgmVolume = 1.0f;
    private float buttonVolume = 1.0f;
    private float actionVolume = 1.0f;
    private boolean firstLogin = true;
    private boolean firstJoinRoom = true;
    private boolean copyFile = false;

    private int score = 0;
    private String scoreString = "00000";


    private static TextView scoreText;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRecMessage(String msg) {

        Log.e(TAG,"onRecMessage()  " + msg);

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
                Toast_in_Thread("Something Wrong with Server");
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

            if (firstLogin || copyFile){
                /*
                when first join the room, try to get the image
                 */
                MsgConnector.getInstance().sendMsg("/ImageRes:" + Communicator.BrainNum);
                firstLogin = false;
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
            Communicator communicator = Communicator.getInstance();
            communicator.initImgInfo(null, Integer.parseInt(msg.split(";")[1]), msg.split(";"));

//            communicator.setResolution(msg.split(";"));
//            communicator.setImgRes(Integer.parseInt(msg.split(";")[1]));
//            communicator.setCurRes(Integer.parseInt(msg.split(";")[1]));

            MsgConnector.getInstance().sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + communicator.getCurRes() + ";" + Communicator.getCurrentPos() + ";");

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

            }
        }


        if (msg.startsWith("Score:")){
            Log.e(TAG,"get score: " + msg);
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

    }


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


//                                Check_Yes.setVisibility(View.GONE);
//                                Check_No.setVisibility(View.GONE);
//                                Check_Uncertain.setVisibility(View.GONE);
//                                res_list.setVisibility(View.GONE);
//                                sync_pull.setVisibility(View.VISIBLE);
//                                sync_push.setVisibility(View.VISIBLE);
//                                neuron_list.setVisibility(View.VISIBLE);
                                user_list.setVisibility(View.VISIBLE);
                                room_id.setVisibility(View.VISIBLE);
                                blue_pen.setVisibility(View.VISIBLE);
                                red_pen.setVisibility(View.VISIBLE);
                            }
                            else {
                                Check_Yes.setVisibility(View.VISIBLE);
                                Check_No.setVisibility(View.VISIBLE);
                                Check_Uncertain.setVisibility(View.VISIBLE);


//                                res_list.setVisibility(View.VISIBLE);
//                                sync_pull.setVisibility(View.VISIBLE);
//                                sync_push.setVisibility(View.GONE);
                                neuron_list.setVisibility(View.VISIBLE);
                                blue_pen.setVisibility(View.GONE);
                                red_pen.setVisibility(View.GONE);
                            }
                        }

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
                            String filename = getFilename_Remote(context);
                            String brain_number = getNeuronNumber_Remote(context,filename);
                            result = name.split("RES")[0].split("_")[1] + "_" + brain_number.split("_")[1];
//                            result = name.split("_")[1].substring(0,name.split("_")[1].length()-3);
                        }else if(source.equals("Remote Server SEU")){
                            String filename = getFilename_Remote(context);
                            String brain_number = getNeuronNumber_Remote(context,filename);
                            Log.d(TAG, "brain_number: " + brain_number);
                            Log.d(TAG, "brain_number.split(\"_\")[0]: " + brain_number.split("_")[0]);
                            if (brain_number.split("_")[0].equals("pre")){
                                Log.d(TAG, "brain_number.split(\"_\")[0]: " + brain_number.split("_")[0]);
                                result = name.split("RES")[0].split("_")[1] + "_" + brain_number.split("_")[2];
                            } else {
                                result = name.split("RES")[0].split("_")[1] + "_" + brain_number.split("_")[1];
                            }
                        }
                    }else {
                        String brain_num = getFilename_Remote(context);
                        String neuron_num = getNeuronNumber_Remote(context, brain_num);
                        result = brain_num.split("_")[0] + "_" + neuron_num.split("_")[1] + "_" + getArborNum(context,brain_num.split("/")[0] + "_" + neuron_num).split(":")[0];
                    }

                    setFilename(result);
                    break;

                case 5:
                    String Toast_msg = msg.getData().getString("Toast_msg");
                    Toast.makeText(getContext(),Toast_msg, Toast.LENGTH_SHORT).show();
                    break;

                case 6:
                    progressBar.setVisibility(View.GONE);
                    break;

                case 7:
                    updateScoreText();
                    break;

                default:
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
                    Log.e("onUserOffline","Here we are !");
                    String userAccount = User_Map.get(uid);
                    onRemoteUserLeft(userAccount, reason);
                    if (voicePattern == VoicePattern.PEER_TO_PEER){
                        fab.setVisibility(View.GONE);
                        leaveChannel();
                        RtcEngine.destroy();
                        mRtcEngine = null;
                        Log.e("onUserOffline","voicePattern == VoicePattern.PEER_TO_PEER");
                    }else if (voicePattern == VoicePattern.CHAT_ROOM){
                        chat_room_num--;
                        Log.e("onUserOffline","chat_room_num: " + chat_room_num);
                        if (chat_room_num == 0){
                            fab.setVisibility(View.GONE);
                            leaveChannel();
                            RtcEngine.destroy();
                            mRtcEngine = null;
                            Log.e("onUserOffline","voicePattern == VoicePattern.CHAT_ROOM");
                        }
                    }

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
                    if (voicePattern == VoicePattern.CHAT_ROOM){
                        chat_room_num++;
                        Log.e("onUserInfoUpdated","chat_room_num: " + chat_room_num);
                    }
                    User_Map.put(user.uid, user.userAccount);
                    onRemoteUserJoined(user.userAccount);
                }
            });
        }
    };

    private RtmClient mRtmClient;
    private ChatManager mChatManager;







    /**
     * The onCreate Function
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"------------------ onCreate ------------------");

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

        Intent bgmIntent = new Intent(MainActivity.this, MusicServer.class);
        startService(bgmIntent);

        isBigData_Remote = false;
        isBigData_Local  = false;

        popupView = new XPopup.Builder(this)
                .asLoading("Downloading......");

        myrenderer = new MyRenderer(this);

        soundPool = new SoundPool(SOUNDNUM, AudioManager.STREAM_MUSIC, 5);
        soundId = new int[SOUNDNUM];
        soundId[0] = soundPool.load(this, R.raw.piano2, 1);
        soundId[1] = soundPool.load(this, R.raw.piano1, 1);
        soundId[2] = soundPool.load(this, R.raw.button01, 1);
        soundId[3] = soundPool.load(this, R.raw.fail, 1);

        Intent intent = getIntent();
        String MSG = intent.getStringExtra(MyRenderer.OUT_OF_MEMORY);
        username = intent.getStringExtra(USERNAME);

//        mChatManager = Myapplication.the().getChatManager();
//        mRtmClient = mChatManager.getRtmClient();

        initDataBase();

        wave = new CircleImageView(getContext());
        wave.setScaleType(ImageView.ScaleType.CENTER_CROP);



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
//                Image4DSimple img = myrenderer.getImg();
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
//                Image4DSimple img = myrenderer.getImg();
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

//                if (isBigData_Remote && DrawMode){
                if (isBigData_Remote){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Communicator communicator = Communicator.getInstance();
                            communicator.zoomIn();

//                            remote_socket.Zoom_in();
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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

//                if (isBigData_Remote && DrawMode){
                if (isBigData_Remote){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Communicator communicator = Communicator.getInstance();
                            communicator.zoomOut();

//                            remote_socket.Zoom_out();
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

        Check_Uncertain = new Button(this);
        Check_Uncertain.setText("?");


        FrameLayout.LayoutParams lp_check_yes = new FrameLayout.LayoutParams(120, 120);
        lp_check_yes.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_check_yes.setMargins(0, 0, 20, 590);
        this.addContentView(Check_Yes, lp_check_yes);

        FrameLayout.LayoutParams lp_check_no = new FrameLayout.LayoutParams(120, 120);
        lp_check_no.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_check_no.setMargins(0, 0, 20, 500);
        this.addContentView(Check_No, lp_check_no);

        FrameLayout.LayoutParams lp_check_uncertain = new FrameLayout.LayoutParams(120, 120);
        lp_check_uncertain.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_check_uncertain.setMargins(0, 0, 20, 410);
        this.addContentView(Check_Uncertain, lp_check_uncertain);

        Check_Yes.setVisibility(View.GONE);
        Check_No.setVisibility(View.GONE);
        Check_Uncertain.setVisibility(View.GONE);


        Check_Yes.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (getUserAccount_Check(context).equals("--11--") || getUserAccount_Check(context).equals("")){
//                            PopUp_UserAccount(MainActivity.this);
                            Toast_in_Thread("Please Input your User name first in more functions !");
                        }else {
                            remote_socket.Check_Result("YES");
                            Toast_in_Thread("Check YES Successfully");
                        }

                    }
                }).start();
                return true;
            }
        });



        Check_No.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (getUserAccount_Check(context).equals("--11--") || getUserAccount_Check(context).equals("")){
//                            PopUp_UserAccount(MainActivity.this);
                            Toast_in_Thread("Please Input your User name first in more functions !");
                        }else {
                            remote_socket.Check_Result("NO");
                            Toast_in_Thread("Check NO Successfully");
                        }

                    }
                }).start();
                return true;
            }
        });

        Check_Uncertain.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (getUserAccount_Check(context).equals("--11--") || getUserAccount_Check(context).equals("")){
//                            PopUp_UserAccount(MainActivity.this);
                            Toast_in_Thread("Please Input your User name first in more functions !");
                        }else {
                            remote_socket.Check_Result("UNCERTAIN");
                            Toast_in_Thread("Check UNCERTAIN Successfully");
                        }

                    }
                }).start();
                return true;
            }
        });


//        Check_Uncertain.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (getUserAccount_Check(context).equals("--11--") || getUserAccount_Check(context).equals("")){
////                            PopUp_UserAccount(MainActivity.this);
//                            Toast_in_Thread("Please Input your User name first in more functions !");
//                        }else {
//                            remote_socket.Check_Result("UNCERTAIN");
//                            Toast_in_Thread("Check Uncertain Successfully");
//                        }
//                    }
//                }).start();
//            }
//        });



        FrameLayout.LayoutParams lp_draw_i = new FrameLayout.LayoutParams(200, 160);

        draw_i = new ImageButton(this);
        draw_i.setImageResource(R.drawable.ic_draw_main);
        ll_top.addView(draw_i,lp_draw_i);

        draw_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if (!myrenderer.getIfFileLoaded()){
                    Toast.makeText(context, "Please load a File First", Toast.LENGTH_SHORT).show();
                    return;
                }
                Draw_list(v);
            }
        });


        FrameLayout.LayoutParams lp_tracing_i = new FrameLayout.LayoutParams(200, 160);


        tracing_i=new ImageButton(this);
        tracing_i.setImageResource(R.drawable.ic_neuron);
        ll_top.addView(tracing_i,lp_tracing_i);

        tracing_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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

        FrameLayout.LayoutParams lp_classify_i = new FrameLayout.LayoutParams(200, 160);

        classify_i=new ImageButton(this);
        classify_i.setImageResource(R.drawable.ic_classify_mid);
        ll_top.addView(classify_i,lp_classify_i);

        classify_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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


//        Rotation = new Button(this);
//        Rotation.setText("Rotate");
//        Rotation.getSolidColor();

        FrameLayout.LayoutParams lp_rotation = new FrameLayout.LayoutParams(120, 120);
        lp_rotation.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_rotation.setMargins(0, 0, 160, 20);

        Rotation_i = new ImageButton(this);
        Rotation_i.setImageResource(R.drawable.ic_3d_rotation_red_24dp);
        Rotation_i.setBackgroundResource(R.drawable.circle_normal);

        this.addContentView(Rotation_i, lp_rotation);

        final boolean[] b_rotate = {true};

        Rotation_i.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if (isBigData_Remote && !DrawMode){
                    myrenderer.resetRotation();
                    myGLSurfaceView.requestRender();
                }else {
                    Rotation();
                }
            }
        });

        FrameLayout.LayoutParams lp_hide = new FrameLayout.LayoutParams(120, 120);
        lp_hide.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_hide.setMargins(0, 0, 20, 20);
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

        lp_redo = new FrameLayout.LayoutParams(120, 120);
        lp_redo.setMargins(0, 20, 20, 0);

        Redo_i = new ImageButton(this);
        Redo_i.setImageResource(R.drawable.ic_redo);
        Redo_i.setBackgroundResource(R.drawable.circle_normal);
        ll_hs_back.addView(Redo_i, lp_redo);

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

        scoreText = new TextView(this);
        scoreText.setTextColor(Color.YELLOW);
        scoreText.setText("00000");
        scoreText.setTypeface(Typeface.DEFAULT_BOLD);
        scoreText.setLetterSpacing(0.8f);
        scoreText.setTextSize(15);

        updateScoreText();

        FrameLayout.LayoutParams lp_score = new FrameLayout.LayoutParams(350, 300);
        lp_score.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_score.setMargins(0, 350, 20, 0);
        this.addContentView(scoreText, lp_score);

        FrameLayout.LayoutParams lp_downsample = new FrameLayout.LayoutParams(120, 120);


        Switch = new Button(this);
        Switch.setText("Pause");

        Switch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Switch();
            }
        });


        lp_animation_i = new FrameLayout.LayoutParams(200, 160);
        animation_i = new ImageButton(this);
        animation_i.setImageResource(R.drawable.ic_animation);
        animation_i.setId(animation_id);

        animation_i.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Left");
            }
        });



        lp_right_i = new FrameLayout.LayoutParams(100, 150);
        lp_right_i.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        navigation_right = new ImageButton(this);
        navigation_right.setImageResource(R.drawable.ic_chevron_right_black_24dp);

        navigation_right.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Set_Nav_Mode();
            }
        });


        lp_blue_color = new FrameLayout.LayoutParams(120, 120);
        lp_blue_color.gravity = Gravity.TOP | Gravity.LEFT;
        lp_blue_color.setMargins(20, 490, 0, 0);


        blue_pen = new Button(this);
        blue_pen.setText("B");

        blue_pen.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                myrenderer.pencolorchange(PenColor.valueOf("BLUE").ordinal());
                blue_pen.setTextColor(Color.BLUE);
                red_pen.setTextColor(Color.BLACK);
            }
        });


        lp_res_list = new FrameLayout.LayoutParams(120, 120);
        lp_res_list.gravity = Gravity.TOP | Gravity.LEFT;
        lp_res_list.setMargins(20, 490, 0, 0);


        res_list = new Button(this);
        res_list.setText("R");
        res_list.setTextColor(Color.BLUE);

        res_list.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                remote_socket.switchRES();
            }
        });



        lp_red_color = new FrameLayout.LayoutParams(120, 120);
        lp_red_color.gravity = Gravity.TOP | Gravity.LEFT;
        lp_red_color.setMargins(20, 580, 0, 0);

        red_pen = new Button(this);
        red_pen.setText("R");

        red_pen.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                myrenderer.pencolorchange(PenColor.valueOf("RED").ordinal());
                red_pen.setTextColor(Color.RED);
                blue_pen.setTextColor(Color.BLACK);
            }
        });



        lp_sync_push = new FrameLayout.LayoutParams(115, 115);
        lp_sync_push.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_sync_push.setMargins(0, 350, 20, 0);

        sync_push = new ImageButton(this);
        sync_push.setImageResource(R.drawable.ic_cloud_upload_black_24dp);
        sync_push.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if (DrawMode){
                    PullSwc_block_Manual(DrawMode);

                    //  for apo sync
//                    PullApo_block_Manual();
                }else {
                    remote_socket.pullCheckResult(false);
                }
            }
        });

        sync_pull.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (!DrawMode){
                    remote_socket.pullCheckResult(true);
                }
                return true;
            }
        });


        lp_neuron_list = new FrameLayout.LayoutParams(115, 115);
        lp_neuron_list.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_neuron_list.setMargins(0, 580, 20, 0);

        neuron_list = new ImageButton(this);
        neuron_list.setImageResource(R.drawable.ic_assignment_black_24dp);
        neuron_list.setOnLongClickListener(new Button.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (DrawMode){
                    push_info_swc = SaveSWC_Block_Auto();
                    remote_socket.Select_Neuron_Fast();
                }else {
                    remote_socket.Select_Neuron_Fast();
//                    remote_socket.Select_Arbor_Fast();
                }
                return true;
            }
        });


        neuron_list.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if (DrawMode){
                    push_info_swc = SaveSWC_Block_Auto();
                    remote_socket.Next_Neuron();
//                    remote_socket.Select_Neuron_Fast();
                }else {
//                    remote_socket.Select_Neuron_Fast();
                    remote_socket.Select_Arbor_Fast();
                }

            }
        });



        lp_room_id = new FrameLayout.LayoutParams(115, 115);
        lp_room_id.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_room_id.setMargins(0, 440, 20, 0);

        room_id = new ImageButton(this);
        room_id.setImageResource(R.drawable.ic_baseline_place_24);
        room_id.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoomID();
            }
        });




        lp_user_list = new FrameLayout.LayoutParams(115, 115);
        lp_user_list.gravity = Gravity.TOP | Gravity.RIGHT;
        lp_user_list.setMargins(0, 580, 20, 0);

        user_list = new ImageButton(this);
        user_list.setImageResource(R.drawable.ic_baseline_account_box_24);
        user_list.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserList();
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
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
        this.addContentView(res_list, lp_res_list);
        this.addContentView(red_pen, lp_red_color);
        this.addContentView(blue_pen, lp_blue_color);

        this.addContentView(room_id, lp_room_id);
        this.addContentView(user_list, lp_user_list);

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
        res_list.setVisibility(View.GONE);
        red_pen.setVisibility(View.GONE);
        blue_pen.setVisibility(View.GONE);

        room_id.setVisibility(View.GONE);
        user_list.setVisibility(View.GONE);


        // set Check Mode  & DownSample Mode
        myrenderer.setIfNeedDownSample(preferenceSetting.getDownSampleMode());
        myrenderer.resetContrast(preferenceSetting.getContrast());
        DrawMode = !preferenceSetting.getCheckMode();


        // Set the permission for user
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }


        myGLSurfaceView.requestRender();
        remote_socket = new Remote_Socket(this);
        bigImgReader = new BigImgReader();

//        context = getApplicationContext();
        mainContext = this;

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

        initNim();

        initServerConnector();
        initService();

        doLoginAgora();
        initAgora();

//        /*
//        sync the score
//         */
//        getScore();


    }


    @Override
    protected void onStart() {
        super.onStart();
    }



    /*
    get score
     */
    private void getScore(){
        ServerConnector.getInstance().sendMsg("GETSCORE");
    }

    private void setScore(int score){
        ServerConnector.getInstance().sendMsg("SETSOCRE:" + score);
    }



    /*
    for service ------------------------------------------------------------------------------------
     */


    private void doLoginAgora(){
        AgoraMsgManager.getInstance().getRtmClient().login(null, username, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "agora login success");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "agora login failed: " + errorInfo.getErrorCode());
                LoginActivity.actionStart(context);
            }
        });
    }

    private void initAgora(){
        Intent intent = new Intent(this, AgoraService.class);
        startService(intent);
    }

    private void initService(){
        // Bind to LocalService
        Intent intent = new Intent(this, ManageService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void initMsgService(){
        // Bind to LocalService
        Intent intent = new Intent(this, CollaborationService.class);
        bindService(intent, connection_msg, Context.BIND_AUTO_CREATE);
    }

    private void initMsgConnector(String port){
        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.setContext(this);

        msgConnector.setIp(ip_ALiYun);
        msgConnector.setPort(port);
        msgConnector.initConnection();
    }


    private void initServerConnector(){

        ServerConnector serverConnector = ServerConnector.getInstance();
        ServerConnector.setContext(this);

        serverConnector.setIp(ip_ALiYun);
        serverConnector.setPort("23763");
        serverConnector.initConnection();

    }


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ManageService.LocalBinder binder = (ManageService.LocalBinder) service;
            manageService = (ManageService) binder.getService();
            binder.addReceiveMsgInterface((MainActivity) getActivityFromContext(mainContext));
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };



    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection_msg = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CollaborationService.LocalBinder binder = (CollaborationService.LocalBinder) service;
            collaborationService = (CollaborationService) binder.getService();
            binder.addReceiveMsgInterface((MainActivity) getActivityFromContext(mainContext));
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };



    /*
    for service ------------------------------------------------------------------------------------
     */



    /**
     *
     * @param FileList
     */
    private void LoadFiles(String FileList){


        Map<String, String> fileType = new HashMap<>();
        String[] list = FileList.split(";;");
        List<String> list_array = new ArrayList<>();

        Log.e(TAG, "list.length: " + list.length);

        for (int i = 0; i < list.length; i++){
            if (list[i].split(" ")[0].endsWith(".apo") || list[i].split(" ")[0].endsWith(".eswc")
                    || list[i].split(" ")[0].endsWith(".swc") || list[i].split(" ")[0].endsWith("log") )
                continue;
            fileType.put(list[i].split(" ")[0], list[i].split(" ")[1]);
            list_array.add(list[i].split(" ")[0]);

            Communicator.getInstance().initSoma(list[i].split(" ")[0]);

        }

        String[] list_show = new String[list_array.size()];
        for (int i = 0; i < list_array.size(); i++){
            list_show[i] = list_array.get(i);
        }

        new XPopup.Builder(this)
                .maxHeight(1350)
                .maxWidth(800)
                .asCenterList("BigData File",list_show,
                        new OnSelectListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSelect(int position, String text) {
                                ServerConnector serverConnector = ServerConnector.getInstance();
                                Log.e(TAG, "test: " + text);
                                Log.e(TAG, "test type: " + fileType.get(text));

                                if (fileType.get(text).equals("0")){
                                    conPath = conPath + "/" + text;
                                    serverConnector.sendMsg("GETFILELIST:" + conPath);
                                }else {
                                    Log.e(TAG, "fileType.get(text).equals(\"1\")");
                                    selectMode(conPath + "/" + text, text);
                                    Communicator.BrainNum = conPath.split("/")[1];
                                    Communicator.Path = conPath + "/" + text;
//                                    serverConnector.sendMsg("LOADFILES:0 " + conPath + "/" + text + " " + conPath + "/test_01_fx_lh_test.ano");
                                }
                            }
                        })
                .show();
    }


    private void selectMode(String oldname, String text){

        Communicator communicator = Communicator.getInstance();
        boolean mode = communicator.initSoma(text);

        String[] modeList;
        if (mode){
            modeList = new String[]{"New File"};
        }else {
            modeList = new String[]{"Load File", "Copy File"};
        }

        new XPopup.Builder(this)
                .asCenterList("Select Mode",modeList,
                        new OnSelectListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text){
                                    case "New File":
                                        // 0
                                        CreateFile(oldname,"0");
                                        break;
                                    case "Copy File":
                                        // 1
                                        CreateFile(oldname,"1");
                                        break;
                                    case "Load File":
                                        // 2
                                        ServerConnector serverConnector = ServerConnector.getInstance();
                                        serverConnector.sendMsg("LOADFILES:2 " + oldname);
                                        Communicator.getInstance().setConPath(oldname);

                                        String[] list = oldname.split("/");
                                        serverConnector.setRoomName(list[list.length - 1]);
                                        break;
                                    default:
                                        Log.e(TAG,"Something Wrong with SelectMode");
                                }
                            }
                        })
                .show();

    }


    /**
     * create the new file & input the name of file
     * @param oldname oldname of file
     * @param mode work mode
     */
    private void CreateFile(String oldname, String mode){
        new XPopup.Builder(this)
                .asInputConfirm("CreateFile", "Input the name of new File",
                new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String text) {
                        ServerConnector serverConnector = ServerConnector.getInstance();
                        switch (mode){
                            case "0":
                                serverConnector.sendMsg("LOADFILES:0 " + oldname + " " + conPath + "/" + text);
                                Communicator.getInstance().setConPath(conPath + "/" + text);
                                serverConnector.setRoomName(text);
                                copyFile = true;
                                break;
                            case "1":
                                serverConnector.sendMsg("LOADFILES:1 " + oldname + " " + conPath + "/" + text);
                                Communicator.getInstance().setConPath(conPath + "/" + text);
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
        firstLogin = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerConnector.getInstance().sendMsg("GETFILELIST:" + "/", true);
            }
        }).start();

    }


    private void showRoomID(){

        MsgConnector msgConnector = MsgConnector.getInstance();
        ServerConnector serverConnector = ServerConnector.getInstance();
        new XPopup.Builder(this).asConfirm("Collaboration Room", "Room name: " + serverConnector.getRoomName() + "\n\n"
                        + "Room ID: " + msgConnector.getPort(),
                new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                    }
                })
                .show();
    }


    private void showUserList(){
        String [] userList = (String[]) MsgConnector.userList.toArray();
        String [] list = new String[userList.length + 1];
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



    private void updateUserList(List<String> newUserList){

        if (MsgConnector.userList.size() < newUserList.size()){
            for (int i = 0; i < newUserList.size(); i++){
                if (!MsgConnector.userList.contains(newUserList.get(i)) && newUserList.get(i) != username){
                    Toast_in_Thread("User " + newUserList.get(i) + " join !");
                }
            }
        }

        if (MsgConnector.userList.size() > newUserList.size()){
            for (int i = 0; i < MsgConnector.userList.size(); i++){
                if (!newUserList.contains(MsgConnector.userList.get(i))){
                    Toast_in_Thread("User " + MsgConnector.userList.get(i) + " left !");
                }
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
                                String nickname = NIMClient.getService(UserService.class).getUserInfo(username).getName();
                                InviteAttachment attachment = new InviteAttachment(nickname, communicator.Path,  communicator.getInitSomaMsg());
                                IMMessage message = MessageBuilder.createCustomMessage(text, SessionTypeEnum.P2P, attachment);
//                                message.setSessionUpdate(false);
                                NIMClient.getService(MsgService.class).sendMessage(message, true).setCallback(new RequestCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void param) {
                                        Toast_in_Thread_static("Sended to" + text);
                                    }

                                    @Override
                                    public void onFailed(int code) {
                                        Toast_in_Thread_static("Invite Send Failed");
                                    }

                                    @Override
                                    public void onException(Throwable exception) {

                                    }
                                });
                            }
                        })
                .show();
    }





    private void initNim(){
//        registerMsgUnreadInfoObserver(true);
        registerSystemMessageObservers(true);
    }


//    /**
//     * 注册未读消息数量观察者
//     */
//    private void registerMsgUnreadInfoObserver(boolean register) {
//        if (register) {
//            ReminderManager.getInstance().registerUnreadNumChangedCallback(this);
//        } else {
//            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(this);
//        }
//    }

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
            Toast_in_Thread_static("Receive Msg");
            for (int i = 0; i < imMessages.size(); i++) {
                if (imMessages.get(i).getMsgType() == MsgTypeEnum.custom){
                    MsgAttachment attachment = imMessages.get(i).getAttachment();
                    if (attachment instanceof InviteAttachment){
                        Toast_in_Thread_static("Receive Invite");
                        String data = attachment.toJson(false);
                        Toast_in_Thread_static(data);
                        Log.d(TAG, "Invite data: " + data);

                        data = data.replaceAll("\"", "");
                        data = data.replaceAll("\\u007B", "");
                        data = data.replaceAll("\\}", "");
                        Log.d(TAG, "Invite data: " + data);

                        String [] informs = data.split(",");
                        String invitor = informs[0].split(":")[2];
                        String path = informs[1].split(":")[1];
                        String soma = informs[2].split(":")[1];

                        invitePopup(mainContext, invitor, path, soma);
                    }
                }
            }
        }
    };

    private void invitePopup(Context context, String invitor, String path, String soma){
        String[] list = path.split("/");
        String roomName = list[list.length - 1];
        new XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asConfirm("INVITE", invitor + " is inviting you to join game in room " + roomName, "Reject", "Join",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                ServerConnector serverConnector = ServerConnector.getInstance();
                                serverConnector.sendMsg("LOADFILES:2 " + path);

//                                String[] list = path.split("/");
                                serverConnector.setRoomName(roomName);

                                Communicator communicator = Communicator.getInstance();
                                communicator.initSoma(soma);
                                communicator.setConPath(path);
                                Communicator.BrainNum = path.split("/")[1];
                                conPath = path;
                            }
                        }, new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        }, false)
        .show();
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
//                return super.onOptionsItemSelected(item);
        }
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
        String[] item_list = null;
        if (DrawMode){
            item_list = new String[]{"Analyze SWC", "Chat", "Animate", "Settings", "Logout", "Crash Info", "Game", "About", "Help", "Quests", "Reward"};
        }else{
            item_list = new String[]{"Analyze SWC", "Chat", "Animate", "Settings", "Logout", "Crash Info", "Account Name", "Game", "About", "Help"};
        }

        new XPopup.Builder(this)
//        .maxWidth(400)
//        .maxHeight(1350)
                .asCenterList("More Functions...", item_list,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                                            setAnimation();
                                        }else {
                                            Toast.makeText(context,"Please Load a Img First !!!", Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                    case "VoiceChat":
//                                        PopUp_Chat(MainActivity.this);
                                        chooseVoiceChatMode();
                                        break;

                                    case "MessageChat":
                                        chooseChatMode();
                                        break;

                                    case "Chat":
                                        openChatActivity();
                                        break;

                                    case "Game":
                                        System.out.println("Game Start!!!!!!!");

                                        ifGame = true;
                                        Select_map();
                                        break;

                                    case "Settings":
                                        setSettings();
                                        break;

                                    case "Account Name":
                                        PopUp_UserAccount(MainActivity.this);
                                        break;

                                    case "Logout":
                                        logout();
                                        break;
                                    case "Crash Info":
                                        CrashInfoShare();
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

                                    default:
                                        Toast.makeText(getContext(), "Default in More Functions...", Toast.LENGTH_SHORT).show();

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
        }
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



    /**
     * add friends
     */
    public void addFriends(){
        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.peer_chat)
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton(R.string.btn_chat, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        Log.d("PeerToPeer", "Start To Chat");
                        EditText targetEdit = (EditText)contentView.findViewById(R.id.target_name_edit);
                        String mTargetName = targetEdit.getText().toString();
                        if (mTargetName.equals("")) {
                            Toast_in_Thread(getString(R.string.account_empty));
                        } else if (mTargetName.length() >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
                            Toast_in_Thread(getString(R.string.account_too_long));
                        } else if (mTargetName.startsWith(" ")) {
                            Toast_in_Thread(getString(R.string.account_starts_with_space));
                        } else if (mTargetName.equals("null")) {
                            Toast_in_Thread(getString(R.string.account_literal_null));
                        } else if (mTargetName.equals(username)) {
                            Toast_in_Thread(getString(R.string.account_cannot_be_yourself));
                        } else {
                            mChatManager.addFriends(mTargetName);
                        }
                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle(R.string.title_add_friends)
                .create();

        mdDialog.show();
    }



    public void chooseChatMode(){
        new XPopup.Builder(this)
                .asCenterList("Choose Chat Mode", new String[]{"Peer Chat", "Selection Tab Channel"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text){
                                    case "Peer Chat":
                                        peerToPeer();
                                        break;
                                    case "Selection Tab Channel":
                                        chooseChannel();
                                        break;
                                }
                            }
                        }).show();

    }

    private void peerToPeer(){
        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.peer_chat)
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
                .setPositiveButton(R.string.btn_chat, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        Log.d("PeerToPeer", "Start To Chat");
                        EditText targetEdit = (EditText)contentView.findViewById(R.id.target_name_edit);
                        String mTargetName = targetEdit.getText().toString();
                        if (mTargetName.equals("")) {
                            Toast_in_Thread(getString(R.string.account_empty));
                        } else if (mTargetName.length() >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
                            Toast_in_Thread(getString(R.string.account_too_long));
                        } else if (mTargetName.startsWith(" ")) {
                            Toast_in_Thread(getString(R.string.account_starts_with_space));
                        } else if (mTargetName.equals("null")) {
                            Toast_in_Thread(getString(R.string.account_literal_null));
                        } else if (mTargetName.equals(username)) {
                            Toast_in_Thread(getString(R.string.account_cannot_be_yourself));
                        } else {
                            openMessageActivity(true, mTargetName);
//                            mChatButton.setEn
//                            jumpToMessageActivity();
                        }
                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle(R.string.title_peer_msg)

                .create();

        mdDialog.show();
    }

    private void chooseChannel(){
        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.channel_chat)
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
                .setPositiveButton(R.string.btn_join, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        EditText targetEdit = (EditText)contentView.findViewById(R.id.channel_name_edit);
                        String mTargetName = targetEdit.getText().toString();
                        if (mTargetName.equals("")) {
                            Toast_in_Thread(getString(R.string.channel_name_empty));
                        } else if (mTargetName.length() >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
                            Toast_in_Thread(getString(R.string.channel_name_too_long));
                        } else if (mTargetName.startsWith(" ")) {
                            Toast_in_Thread(getString(R.string.channel_name_starts_with_space));
                        } else if (mTargetName.equals("null")) {
                            Toast_in_Thread(getString(R.string.channel_name_literal_null));
                        }  else {
                            openMessageActivity(false, mTargetName);
//                            mChatButton.setEn
//                            jumpToMessageActivity();
                        }
                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle(R.string.title_channel_message)

                .create();

        mdDialog.show();
    }

    private void openMessageActivity(boolean isPeerToPeerMode, String targetName){
        Intent intent = new Intent(MainActivity.this, MessageActivity.class);
        intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, isPeerToPeerMode);
        intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, targetName);
        intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, username);
//        intent.putExtra(MessageUtil.INTENT_EXTRA_MESSAGE_LIST, messageMap.get(targetName));
        startActivity(intent);
    }

    private void openChatActivity(){
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            myrenderer.setPath(showPic.getAbsolutePath());
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
                                myrenderer.saveUndo();
                                break;
                            case ".SWC":
                            case ".ESWC":
                                Log.v("onActivityResult", ".eswc");
                                NeuronTree nt = NeuronTree.readSWC_file(uri);

                                myrenderer.importNeuronTree(nt);
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

                                myrenderer.importNeuronTree(nt2);
                                myrenderer.importApo(ano_apo);
                                myrenderer.saveUndo();
                                break;

                            default:
                                Toast.makeText(this, "do not support this file", Toast.LENGTH_SHORT).show();

                        }
                    }

                    else {
                        System.out.println("-------- open --------");
                        myrenderer.setSWCPath(filePath);
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
                                    Check_Uncertain.setVisibility(View.GONE);
                                    sync_pull.setVisibility(View.GONE);
                                    neuron_list.setVisibility(View.GONE);
                                    res_list.setVisibility(View.GONE);
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
                    List features = morphologyCalculate.calculate(uri, false);

                    if (features != null) {
                        fl = new ArrayList<double[]>(features);
                        displayResult(features);
                    }
                }


                if (ifLoadLocal) {
                    System.out.println("load local");
                    myrenderer.setPath(filePath);
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
                                Check_Uncertain.setVisibility(View.GONE);
                                sync_pull.setVisibility(View.GONE);
                                neuron_list.setVisibility(View.GONE);
                                res_list.setVisibility(View.GONE);
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


            } catch (OutOfMemoryError e) {
                Toast.makeText(this, " Fail to load file  ", Toast.LENGTH_SHORT).show();
                Log.v("MainActivity", "111222");
                Log.v("Exception", e.toString());
            } catch (CloneNotSupportedException e){
                Log.v("Exception:", e.toString());
            }
        }
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
        return cmpNameTemp.equals("ComponentInfo{com.example.myapplication__volume/com.example.myapplication__volume.MainActivity}");
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
                    myrenderer.setSwcLoaded();
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
            myrenderer.setSwcLoaded();
            myrenderer.importNeuronTree(nt);
            myGLSurfaceView.requestRender();
        }catch (Exception e){
            Toast_in_Thread_static("Something Wrong when open Swc File !");
        }


    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
        File image = File.createTempFile(
                imageFileName,     /* prefix */
                ".jpg",     /* suffix */
                storageDir         /* directory */
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
        return mCapturePath;
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
     * for draw button
     * @param v
     */
    private void Draw_list(View v){
        drawPopupView = new XPopup.Builder(this)
                .atView(v)
                .autoDismiss(false)
                .asAttachList(new String[]{"For Marker", "For Curve", "Clear Tracing", "Exit Drawing Mode"},
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
        new XPopup.Builder(this)
                .atView(v)
                .offsetX(580)
                .isRequestFocus(false)
                .popupPosition(PopupPosition.Right)
                .asAttachList(new String[]{"PinPoint   ", "Delete Marker", "Delete MultiMarker", "Set MColor", "Change MColor",
                        "Change All MColor"}, new int[]{}, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
        new XPopup.Builder(this)
                .atView(v)
                .offsetX(580)
                .asAttachList(new String[]{"Draw Curve", "Delete Curve", "Split       ",
                                "Set PenColor", "Change PenColor", "Change All PenColor"}, new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                .asAttachList(new String[]{"APP2", "GD", "Save SWCFile"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

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
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "Save SWCFile":
                                        SaveSWC();
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

                .asAttachList(new String[]{"Filter by example"},

                        new int[]{},
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
                                                Learning();
                                                puiHandler.sendEmptyMessage(6);
                                            }
                                        }).start();
                                        break;
                                }
                            }
                        })
                .show();
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
//            if (error.equals("Current swc is empty!")){
//                Toast_in_Thread("Current swc file is empty!");
//                return false;
//            }
        } else{
            System.out.println("save SWC to " + dir_str + "/" + SwcFileName + ".swc");
        }
        return true;
    }



    private void ShareScreenShot() {

        myrenderer.setTakePic(true, this);
        myGLSurfaceView.requestRender();
        final String[] imgPath = new String[1];

        Log.v("Share","save screenshot to " + imgPath[0]);
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
                                "Version: 20210402a 10:00 UTC+8 build",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                            }
                        })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
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
            Toast.makeText(getContext(), "Error when open file!" + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * for game ------------------------------------------------------------------------------------
     */
    public void Select_map(){
        new XPopup.Builder(this)
                .asCenterList("Game Start", new String[]{"New Game", "Load Game"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text){

                                    case "New Game":
//                                        setSelectSource("Remote Server SEU", context);
//                                        BigFileRead_Remote(ip_SEU);
                                        setSelectSource("Remote Server Aliyun",context);
                                        BigFileRead_Remote(ip_ALiYun);

                                        break;

                                    case "Load Game":
                                        loadGameList();
                                        break;

                                    default:
                                        Toast.makeText(getContext(), "Something Wrong Here", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
    }

    private void loadGameList(){
        String externalFileDir = context.getExternalFilesDir(null).toString();
        String [] fileList = {"[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]"};
        File file = new File(externalFileDir + "/Game/Archives");
        if (file.exists()){
            try {
                for (int i = 0; i < 10; i++) {
                    File tempFile = new File(externalFileDir + "/Game/Archives/Archive_" + i);
                    if (!tempFile.exists()) {
                        tempFile.mkdir();
                    } else {
                        File [] archiveFiles = tempFile.listFiles();
                        if (archiveFiles.length > 0){
                            fileList[i] = archiveFiles[0].getName().split(".txt")[0];
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            File parent = file.getParentFile();
            if (!parent.exists()){
                parent.mkdir();
            }
            file.mkdir();
            for (int i = 0; i < 10; i++){
                File tempFile = new File(externalFileDir + "/Game/Archives/Archive_" + i);
                tempFile.mkdir();
            }
        }

        new XPopup.Builder(this)
                .autoDismiss(false)
                .asCenterList("Archives", fileList,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if (text.equals("[Empty Archive]")){

                                } else {
                                    if (loadGame(position))
                                        Toast_in_Thread("Loaded successfully");
                                    else
                                        Toast_in_Thread("Failed To Load!!!");
                                }
                            }
                        }).show();
    }

    private boolean loadGame(int num){
        String archiveImageName;
        String archiveOffset;
        float [] pos = new float[3];
        float [] dir = new float[3];
        float [] head = new float[3];
        String externalFileDir = context.getExternalFilesDir(null).toString();
        File file = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num);
        if (!file.exists()){
            file.mkdir();
            return false;
        }

        File [] tempList = file.listFiles();
        if(tempList.length == 0){
            return false;
        }

        try{
            FileInputStream inStream = new FileInputStream(tempList[0]);
            if (inStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                archiveImageName = line;
                String tempFilename = archiveImageName.split("/")[0];

                File archiveSWCFile = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + tempFilename + ".swc");
                if (archiveSWCFile.exists()){
                    File newSWCFile = new File(externalFileDir + "/Game/SWCs/" + tempFilename + ".swc");
                    if (newSWCFile.exists()){
                        newSWCFile.delete();
                    }
                    newSWCFile.createNewFile();

                    FileUtils.copyFile(archiveSWCFile, newSWCFile);
                } else {
                    return false;
                }

                File archiveFlagFile = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + tempFilename + ".txt");
                if (archiveFlagFile.exists()){
                    File newFlagFile = new File(externalFileDir + "/Game/Flags/" + tempFilename + ".txt");
                    if (newFlagFile.exists()){
                        newFlagFile.delete();
                    }
                    newFlagFile.createNewFile();

                    FileUtils.copyFile(archiveFlagFile, newFlagFile);
                }

                line = buffreader.readLine();
                archiveOffset = line;
                Log.d(TAG, "LoadGame offset: " + archiveOffset);

                line = buffreader.readLine();
                pos[0] = Float.parseFloat(line.split(" ")[0]);
                pos[1] = Float.parseFloat(line.split(" ")[1]);
                pos[2] = Float.parseFloat(line.split(" ")[2]);

                line = buffreader.readLine();
                dir[0] = Float.parseFloat(line.split(" ")[0]);
                dir[1] = Float.parseFloat(line.split(" ")[1]);
                dir[2] = Float.parseFloat(line.split(" ")[2]);

                line = buffreader.readLine();
                head[0] = Float.parseFloat(line.split(" ")[0]);
                head[1] = Float.parseFloat(line.split(" ")[1]);
                head[2] = Float.parseFloat(line.split(" ")[2]);

                line = buffreader.readLine();
                gameLastIndexForIntent = Integer.parseInt(line);

                line = buffreader.readLine();
                gameScoreForIntent = Integer.parseInt(line);

                inStream.close();//关闭输入流

                gamePositionForIntent = pos;
                gameDirForIntent = dir;
                gameHeadForIntent = head;

                gameIfNewForIntent = false;

                if (archiveImageName != null && archiveOffset != null){
                    remote_socket.disConnectFromHost();
//                    remote_socket.connectServer(ip_SEU);
                    remote_socket.connectServer(ip_ALiYun);
                    remote_socket.pullImageBlockWhenLoadGame(archiveImageName, archiveOffset);

                    setFilename_Remote(archiveImageName, context);
//                    setNeuronNumber_Remote(neuronNum_Backup,fileName_Backup,mContext);
                    setoffset_Remote(archiveOffset, archiveImageName, context);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * for game ------------------------------------------------------------------------------------
     */




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

    /**
     * Read Big File from Remote Server
     * @param ip server ip
     */
    private void BigFileRead_Remote(String ip){

        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                /*
                1.先断开一下连接，以防之前连接过，出现同一个ip连接两次服务器
                2.与服务器建立socket连接
                3.获取文件列表
                 */
                remote_socket.disConnectFromHost();
                remote_socket.connectServer(ip);
                remote_socket.Select_Brain(true);
            }
        });
        thread.start();

    }


    private void BigFileRead_Remote_Check(String ip){

        Log.v("Remote_Check","Here We are !");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                remote_socket.disConnectFromHost();
                remote_socket.connectServer(ip);
//                remote_socket.Select_Arbor();
                remote_socket.Select_Brain(false);
            }
        });
        thread.start();

    }


    public static void Toast_in_Thread_static(String message){
        Message msg = new Message();
        msg.what = Toast_Info_static;
        Bundle bundle = new Bundle();
        bundle.putString("Toast_msg",message);
        msg.setData(bundle);
        puiHandler.sendMessage(msg);
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
            filename = getFilename_Remote(this);
            offset   = getoffset_Remote(this, filename);
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

            String[] offset_arr = offset.split("_");
            int[] offset_arr_i = new int[4];
            for (int i =0; i<offset_arr_i.length; i++){
                offset_arr_i[i] = Integer.parseInt(offset_arr[i]);
            }

            block  = new float[]{offset_arr_i[0], offset_arr_i[1], offset_arr_i[2]};
            size   = new float[]{offset_arr_i[3], offset_arr_i[3], offset_arr_i[3]};
            neuron = remote_socket.getImg_size_f(block);

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

                        System.out.println("start to save-------");

                        EditText swcName = contentView.findViewById(R.id.swcname);
                        String swcFileName = swcName.getText().toString();
                        if (swcFileName == ""){
                            Toast.makeText(getContext(), "The name should not be empty.", Toast.LENGTH_SHORT).show();
                        }
                        myrenderer.reNameCurrentSwc(swcFileName);

                        String dir_str = "/storage/emulated/0/C3/SWCSaved";
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
                        if (!error.equals("")) {
                            if (error == "This file already exits"){
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
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void APP2() throws Exception {
        Image4DSimple img = myrenderer.getImg();
        if(img == null){
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();
            return;
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
                p.landmarks[i] = new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
            }
            System.out.println("---------------start---------------------");
            V3dNeuronAPP2Tracing.proc_app2(p);
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
            myrenderer.saveUndo();
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
        if(img == null){
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            Toast.makeText(this, "Please load image first!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            Looper.loop();
            return;
        }

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
//            outswc.listNeuron.get(i).type = 4;
            outswc.listNeuron.get(i).type = 5;
        }

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        Toast.makeText(getContext(), "GD-Tracing finished, size of result swc: " + Integer.toString(outswc.listNeuron.size()), Toast.LENGTH_SHORT).show();
        myrenderer.importNeuronTree(outswc);
        myrenderer.saveUndo();
        myGLSurfaceView.requestRender();
        progressBar.setVisibility(View.INVISIBLE);
        Looper.loop();

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
            myrenderer.resetImg(p.outImage);
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
                                            Toast.makeText(getContext(), "Empty tracing, do nothing", Toast.LENGTH_LONG).show();
                                            break;
                                        }
                                        MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
                                        List<double[]> features = morphologyCalculate.calculatefromNT(nt, false);
                                        fl = new ArrayList<double[]>(features);
                                        if (features.size() != 0) displayResult(features);
                                        else Toast.makeText(getContext(), "the file is empty", Toast.LENGTH_SHORT).show();
                                        break;

                                    default:
                                        Toast.makeText(getContext(), "Default in analysis", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                .show();


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
        ar_mdDialog.show();
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




    private void setSettings(){

        boolean [] downsample = new boolean[1];
        boolean [] check = new boolean[1];

        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.settings)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        Switch downsample_on_off = contentView.findViewById(R.id.switch_rotation_mode);
                        Switch check_on_off = contentView.findViewById(R.id.switch_check_mode);
                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.indicator_seekbar);
                        TextView clean_cache = contentView.findViewById(R.id.clean_cache);
                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);
                        Spinner bgmSpinner = contentView.findViewById(R.id.bgm_spinner);

                        boolean ifDownSample = preferenceSetting.getDownSampleMode();
                        int contrast = preferenceSetting.getContrast();

                        downsample_on_off.setChecked(ifDownSample);
                        check_on_off.setChecked(!DrawMode);
                        seekbar.setProgress(contrast);
                        bgmVolumeBar.setProgress((int)(bgmVolume * 100));
                        buttonVolumeBar.setProgress((int)(buttonVolume * 100));
                        actionVolumeBar.setProgress((int)(actionVolume * 100));

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

                        downsample[0] = downsample_on_off.isChecked();
                        check[0]      = check_on_off.isChecked();

                        downsample_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                downsample[0] = isChecked;
                            }
                        });

                        check_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                check[0] = isChecked;
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
                        DrawMode = !check[0];

                        Log.v(TAG,"downsample: " + downsample[0] + ", check: " + check[0] + ",contrast: " + contrast);
                        preferenceSetting.setPref(downsample[0], check[0], contrast);
                        myGLSurfaceView.requestRender();

                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);

                        bgmVolume = (float)(bgmVolumeBar.getProgress()) / 100.0f;
                        buttonVolume = (float)(buttonVolumeBar.getProgress()) / 100.0f;
                        actionVolume = (float)(actionVolumeBar.getProgress()) / 100.0f;

                        MusicServer.setBgmVolume(bgmVolume);

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

                        AgoraMsgManager.getInstance().getRtmClient().logout(null);

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
                                    intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "com.example.myapplication__volume.provider", new File(file_path)));  //传输图片或者文件 采用流的方式
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
                            Toast.makeText(context,"Make sure the input is right !!!",Toast.LENGTH_SHORT).show();
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

        Intent bgmIntent = new Intent(this, MusicServer.class);
        stopService(bgmIntent);

        Intent manageServiceIntent = new Intent(this, ManageService.class);
        stopService(manageServiceIntent);

        Intent collaborationServiceIntent = new Intent(this, CollaborationService.class);
        stopService(collaborationServiceIntent);

        Intent agoraServiceIntent = new Intent(this, AgoraService.class);
        stopService(agoraServiceIntent);

        /*
        release socket
         */
        MsgConnector.getInstance().releaseConnection();
        ServerConnector.getInstance().releaseConnection();

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

    //renderer 的生存周期和activity保持一致
    @Override
    protected void onPause() {
        super.onPause();
        myGLSurfaceView.onPause();
        Log.v("onPause", "start-----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        myGLSurfaceView.onResume();
        Log.v("Path", filepath);
        Log.v("onResume", "start-----");
    }

    @Override
    protected void onStop() {
        Intent bgmIntent = new Intent(MainActivity.this, MusicServer.class);
        stopService(bgmIntent);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Intent bgmIntent = new Intent(MainActivity.this, MusicServer.class);
        startService(bgmIntent);
        super.onRestart();
    }

    private void Learning() {

        Image4DSimple img = myrenderer.getImg();
        if(img == null){

            Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_SHORT).show();
            return;
        }
        Image4DSimple outImg=new Image4DSimple();

        NeuronTree nt = myrenderer.getNeuronTree();
        PixelClassification p = new PixelClassification();

        boolean[][] selections = select;
        System.out.println("select is");
        System.out.println(select);
        p.setSelections(selections);

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        Toast.makeText(getContext(), "pixel  classification start~", Toast.LENGTH_SHORT).show();

        try{
            outImg = p.getPixelClassificationResult(img,nt);
            System.out.println("outImg: "+outImg.getSz0()+" "+outImg.getSz1()+" "+outImg.getSz2()+" "+outImg.getSz3());
            System.out.println(outImg.getData().length);

            myrenderer.resetImg(outImg);
            myGLSurfaceView.requestRender();
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




    //画笔颜色设置
    public void penSet(){

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


    public void markerPenSet(){

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
                    }
                })
                .setWidthMaxDp(600)
                .create()
                .show();
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
        @RequiresApi(api = Build.VERSION_CODES.O)
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
                            if (ifPainting || ifDeletingLine || ifSpliting || ifChangeLineType || ifDeletingMultiMarker) {
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
//                            myrenderer.setIfPaiFting(false);
//                            requestRender();
//
//                        }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!isZooming) {
                                try {
                                    if (ifPoint) {
                                        Score scoreInstance = Score.getInstance();
                                        scoreInstance.pinpoint();

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
                                        myrenderer.deleteMultiMarkerByStroke(lineDrawed);
                                        requestRender();
                                    }
                                    if (ifChangeMarkerType) {
                                        myrenderer.changeMarkerType(normalizedX, normalizedY, isBigData_Remote);
                                        requestRender();
                                    }
                                    if (ifPainting) {
                                        Vector<Integer> segids = new Vector<>();
                                        myrenderer.setIfPainting(false);

                                        Score scoreInstance = Score.getInstance();
                                        scoreInstance.drawACurve();

                                        if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)
                                            myrenderer.add2DCurve(lineDrawed);
                                        else {

                                            Callable<String> task = new Callable<String>() {
                                                @Override
                                                public String call() throws Exception {
                                                    int lineType = myrenderer.getLastLineType();
                                                        V_NeuronSWC_list [] v_neuronSWC_list = new V_NeuronSWC_list[1];
                                                        V_NeuronSWC seg = myrenderer.addBackgroundLineDrawed(lineDrawed, v_neuronSWC_list);
                                                        System.out.println("feature");

                                                        if (seg != null) {
                                                            myrenderer.addLineDrawed2(lineDrawed, seg, isBigData_Remote);
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


    /**
     * open file
     */
    public void File_icon(){

        new XPopup.Builder(this)
//                .hasShadowBg(false)
//        .maxWidth(400)
//        .maxHeight(1350)
//                .asCenterList("File Open & Save", new String[]{"Open BigData", "Open LocalFile", "Load SWCFile","Camera"},
                .asCenterList("File Open", new String[]{"Open BigData", "Open LocalFile", "Load SWCFile"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text) {

                                    case "Open LocalFile":
                                        loadLocalFile();
                                        break;
                                    case "Open BigData":
                                        /**
                                         * xf szt
                                         */

                                        loadBigData();
                                        break;
                                    case "Load SWCFile":
                                        LoadSWC();
                                        break;

                                    default:
                                        Toast.makeText(getContext(), "Default in file", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                .show();

    }




    public void PopUp_UserAccount(Context context){

        new MDDialog.Builder(context)
//              .setContentView(customizedView)
                .setContentView(R.layout.user_account_check)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et = (EditText) contentView.findViewById(R.id.userAccount_edit_check);
                        String userAccount = getUserAccount_Check(context);

                        if (userAccount.equals("--11--")){
                            userAccount = "";
                        }

                        et.setText(userAccount);
                    }
                })
                .setTitle("UserName for Check")
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
                        EditText et = (EditText) contentView.findViewById(R.id.userAccount_edit_check);

                        String userAccount   = et.getText().toString();


                        if( !userAccount.isEmpty() ){
                            setUserAccount_Check(userAccount, context);

                        }else{
                            PopUp_UserAccount(context);
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

    public void chooseVoiceChatMode(){
        new XPopup.Builder(this)
                .asCenterList("Choose Voice Chat Mode", new String[]{"Peer Chat", "Selection Tab Channel"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text){
                                    case "Peer Chat":
                                        peerToPeerChat();
                                        break;
                                    case "Selection Tab Channel":
                                        PopUp_Chat(mainContext);
                                        break;
                                }
                            }
                        }).show();

    }

    private void peerToPeerChat(){
        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.peer_chat)
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
                .setPositiveButton(R.string.btn_chat, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        Log.d("PeerToPeer", "Start To Chat");
                        EditText targetEdit = (EditText)contentView.findViewById(R.id.target_name_edit);
                        String mTargetName = targetEdit.getText().toString();
                        if (mTargetName.equals("")) {
                            Toast_in_Thread(getString(R.string.account_empty));
                        } else if (mTargetName.length() >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
                            Toast_in_Thread(getString(R.string.account_too_long));
                        } else if (mTargetName.startsWith(" ")) {
                            Toast_in_Thread(getString(R.string.account_starts_with_space));
                        } else if (mTargetName.equals("null")) {
                            Toast_in_Thread(getString(R.string.account_literal_null));
                        } else if (mTargetName.equals(username)) {
                            Toast_in_Thread(getString(R.string.account_cannot_be_yourself));
                        } else {
                            callTarget(mTargetName);
                            voicePattern = VoicePattern.PEER_TO_PEER;
                            Log.e("peerToPeerChat","voicePattern = VoicePattern.PEER_TO_PEER");
                        }
                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle(R.string.title_peer_voice)

                .create();

        mdDialog.show();
    }

    private void callTarget(String target){
        String channelName = target + "And" + username;
        String callMessage = "##CallFrom" + username + "##In##" + channelName + "##";
        RtmMessage message = mRtmClient.createMessage();
        message.setText(callMessage);

        mRtmClient.sendMessageToPeer(target, message, mChatManager.getSendMessageOptions(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                runOnUiThread(() -> {
                    VoiceChat(channelName, username);
                });

            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                final int errorCode = errorInfo.getErrorCode();
                runOnUiThread(() -> {
                    switch (errorCode){
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_TIMEOUT:
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_FAILURE:
                            Toast_in_Thread(getString(R.string.call_failed));
                            break;
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_PEER_UNREACHABLE:
                            Toast_in_Thread(getString(R.string.peer_offline));
                            break;
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_CACHED_BY_SERVER:
                            Toast_in_Thread(getString(R.string.call_cached));
                            break;
                    }
                });
            }
        });
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
                        EditText et1 = (EditText) contentView.findViewById(R.id.channel_edit);
                        EditText et2 = (EditText) contentView.findViewById(R.id.userAccount_edit);

                        String Channel   = et1.getText().toString();
                        String userAccount   = et2.getText().toString();


                        if( !Channel.isEmpty() && !userAccount.isEmpty() ){
                            VoiceChat(Channel, userAccount);
                            setUserAccount(userAccount, context);
                            voicePattern = VoicePattern.CHAT_ROOM;
                            Log.e("PopUp_Chat","voicePattern = VoicePattern.CHAT_ROOM");

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Select_Block(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                String source = getSelectSource(context);
                String ip = "";
                switch (source){
                    case "Remote Server Aliyun":
                    case "Remote Server SEU":
                        if (source.equals("Remote Server Aliyun")){
                            ip = ip_ALiYun;
                        }else if(source.equals("Remote Server SEU")){
                            ip = ip_SEU;
                        }else {
                            Toast_in_Thread("Something Wrong when choose Remote Server !");
                            return;
                        }

                        remote_socket.disConnectFromHost();
                        remote_socket.connectServer(ip);
                        remote_socket.loadNeuronTxt(DrawMode);
                        remote_socket.selectBlock();
                        break;
                    case "Local Server":
                        bigImgReader.PopUp(context);
                        break;
                    default:
                        Toast_in_Thread("Load a File First !");
                        break;
                }
            }
        });
        thread.start();

    }


    public static void showProgressBar(){
        puiHandler.sendEmptyMessage(0);
    }

    public static void hideProgressBar(){
        puiHandler.sendEmptyMessage(1);
    }

    public static void setFileName(String filepath){
        file_path_temp = filepath;
        puiHandler.sendEmptyMessage(4);
    }



    /*
    load Img Block after downloading file
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void loadBigDataImg(String filepath){
        isBigData_Remote = true;
        isBigData_Local = false;

        myrenderer.setPath(filepath);
        myrenderer.zoom(2.2f);
        myGLSurfaceView.requestRender();

        SetButtons();
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


    public void loadBigDataSwc(String filepath){
        try {
            NeuronTree nt = NeuronTree.readSWC_file(filepath);

            myrenderer.importNeuronTree(Communicator.getInstance().convertNeuronTree(nt));
            myrenderer.saveUndo();
            myGLSurfaceView.requestRender();

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void LoadBigFile_Remote(String filepath){

        Log.v("MainActivity","LoadBigFile_Remote()");

        if (ifGame){

            Log.v("MainActivity","LoadBigFile_Remote() ifGame");
            try {
                Log.v("GameIntent", "inNNNNNNNNNNNNNNNNNNNNN");
                Intent gameIntent = new Intent(mainContext, GameActivity.class);
                gameIntent.putExtra("FilePath", filepath);
                gameIntent.putExtra("Position", gamePositionForIntent);
                gameIntent.putExtra("Dir", gameDirForIntent);
                gameIntent.putExtra("Head", gameHeadForIntent);
                gameIntent.putExtra("LastIndex", gameLastIndexForIntent);
                gameIntent.putExtra("IfNewGame", gameIfNewForIntent);
                gameIntent.putExtra("Score", gameScoreForIntent);
                mainContext.startActivity(gameIntent);
                gamePositionForIntent = new float[]{0.5f, 0.5f, 0.5f};
                gameDirForIntent = new float[]{1, 1, 1};
                gameHeadForIntent = new float[]{1, 0, -1};
                gameLastIndexForIntent = -1;
                gameIfNewForIntent = true;
                gameScoreForIntent = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {

            Log.v("MainActivity","LoadBigFile_Remote() ifGame");
            Log.v("MainActivity",remote_socket.getIp());
            if (remote_socket.getIp().equals(ip_ALiYun)){
                setSelectSource("Remote Server Aliyun",context);
            } else if (remote_socket.getIp().equals(ip_SEU)){
                setSelectSource("Remote Server SEU",context);
            }

            myrenderer.setPath(filepath);
            myrenderer.zoom(2.2f);
            setFileName(filepath);

            System.out.println("------" + filepath + "------");
            isBigData_Remote = true;
            isBigData_Local = false;
            myGLSurfaceView.requestRender();
            SetButtons();

            PullSwc_block_Auto(true);

            if (DrawMode){
                LoadMarker();
                if (!push_info_swc[0].equals("New")){
//                    String filepath = this.getExternalFilesDir(null).toString();
//                    String swc_file_path = filepath + "/Sync/BlockSet";
                    PushSWC_Block_Auto(push_info_swc[0], push_info_swc[1]);
                }
            }
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
    }


    private static void SetButtons(){
        puiHandler.sendEmptyMessage(2);
    }

    public static void updateScore(){
        puiHandler.sendEmptyMessage(7);
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
            navigation_location.setVisibility(View.GONE);

            Zoom_in_Big.setVisibility(View.GONE);
            Zoom_out_Big.setVisibility(View.GONE);

            if (isBigData_Remote) {
                if (!DrawMode){

                    Check_No.setVisibility(View.GONE);
                    Check_Yes.setVisibility(View.GONE);
                    Check_Uncertain.setVisibility(View.GONE);
                    neuron_list.setVisibility(View.GONE);
                    res_list.setVisibility(View.GONE);
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
        Redo_i.setVisibility(View.VISIBLE);

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
                    Check_Uncertain.setVisibility(View.VISIBLE);
                    neuron_list.setVisibility(View.VISIBLE);
                    res_list.setVisibility(View.VISIBLE);
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

        lp_res_list.setMargins(0, 540, 20, 0);
        res_list.setLayoutParams(lp_res_list);
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
        voicePattern = VoicePattern.UNCERTAIN;
        chat_room_num = 0;
    }

    // Tutorial Step 4
    private void onRemoteUserLeft(String userAccount, int reason) {
        if (voicePattern == VoicePattern.PEER_TO_PEER){
            showLongToast("The CALL is End !");
        }else {
            if (chat_room_num > 1){
                showLongToast("user " + userAccount + " left : " + reason);
            }else {
                showLongToast("The CALL is End !");
            }
        }
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

    public static void actionStart(Context context, String username){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USERNAME, username);
        context.startActivity(intent);
    }



    private void addScore(int s){
        score += s;

        updateScoreText();
    }

    private static void updateScoreText(){
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

    public void initDataBase(){
        DailyQuestsContainer.initId(username);
        Score.initId(username);
        ScoreLitePalConnector.initUser(username);
        RewardLitePalConnector.initUserId(username);

        Score score = Score.getInstance();
        score.initFromLitePal();
    }















    class MyRtmClientListener implements RtmClientListener {

        @Override
        public void onConnectionStateChanged(final int state, int reason) {
            runOnUiThread(() -> {
                switch (state) {
                    case RtmStatusCode.ConnectionState.CONNECTION_STATE_RECONNECTING:
                        Toast_in_Thread(getString(R.string.reconnecting));
                        break;
                    case RtmStatusCode.ConnectionState.CONNECTION_STATE_ABORTED:
                        Toast_in_Thread(getString(R.string.account_offline));
                        setResult(MessageUtil.ACTIVITY_RESULT_CONN_ABORTED);
                        finish();
                        break;
                }
            });
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onMessageReceived(final RtmMessage message, final String peerId) {
            if (isTopActivity()) {
                Log.d("onMessageRecievedFromPeer", message.getText() + " from " + peerId);
                String msg = message.getText();
                if (Pattern.matches(callMsgPattern, message.getText())) {

                    String targetName = msg.substring(10, msg.indexOf("##In##"));
                    String channelName = msg.substring(msg.indexOf("##In##") + 6, msg.lastIndexOf("##"));
                    final boolean[] answered = {false};
                    runOnUiThread(() -> {


                        BasePopupView calledPopup = new XPopup.Builder(mainContext)
                                .dismissOnTouchOutside(false)
                                .dismissOnBackPressed(false)
                                .asConfirm("Phone Call", "from " + targetName, "Reject", "Answer",
                                        new OnConfirmListener() {
                                            @Override
                                            public void onConfirm() {
                                                answered[0] = true;

                                                VoiceChat(channelName, username);
                                                String callMessage = "##SuccessToAnswer##";
                                                RtmMessage answerMessage = mRtmClient.createMessage();
                                                answerMessage.setText(callMessage);

                                                mRtmClient.sendMessageToPeer(targetName, answerMessage, mChatManager.getSendMessageOptions(), new ResultCallback<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }

                                                    @Override
                                                    public void onFailure(ErrorInfo errorInfo) {

                                                    }
                                                });
                                            }
                                        }, new OnCancelListener() {
                                            @Override
                                            public void onCancel() {
                                                answered[0] = true;

                                                String callMessage = "##RefuseToAnswer##";
                                                RtmMessage refuseMessage = mRtmClient.createMessage();
                                                refuseMessage.setText(callMessage);

                                                mRtmClient.sendMessageToPeer(targetName, refuseMessage, mChatManager.getSendMessageOptions(), new ResultCallback<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }

                                                    @Override
                                                    public void onFailure(ErrorInfo errorInfo) {

                                                    }
                                                });
                                            }
                                        }, false);
                        calledPopup.show();
                        calledPopup.delayDismiss(20000);
                        calledPopup.dismissWith(new Runnable() {
                            @Override
                            public void run() {
                                if (answered[0] == false){
                                    String callMessage = "##TimeOutToAnswer##";
                                    RtmMessage timeOutMessage = mRtmClient.createMessage();
                                    timeOutMessage.setText(callMessage);

                                    mRtmClient.sendMessageToPeer(targetName, timeOutMessage, mChatManager.getSendMessageOptions(), new ResultCallback<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }

                                        @Override
                                        public void onFailure(ErrorInfo errorInfo) {

                                        }
                                    });
                                }
                            }
                        });

                    });
                } else if (msg.equals("##RefuseToAnswer##")){
                    runOnUiThread(() -> {
                        Toast_in_Thread("Target Refused To Answer");
                        fab.setVisibility(View.GONE);
                        try {
                            leaveChannel();
                            RtcEngine.destroy();
                        } catch (Exception e){
                            Toast_in_Thread(e.getMessage());
                        }
                        mRtcEngine = null;
                    });

                } else if (msg.equals("##SuccessToAnswer##")){
                    runOnUiThread(() -> {
                        Toast_in_Thread("Connection Succeeded");
                    });
                } else if (msg.equals("##TimeOutToAnswer##")){
                    runOnUiThread(() -> {
                        Toast_in_Thread("Target Time Out To Answer");
                        fab.setVisibility(View.GONE);
                        try {
                            leaveChannel();
                            RtcEngine.destroy();
                        } catch (Exception e){
                            Toast_in_Thread(e.getMessage());
                        }
                        mRtcEngine = null;
                    });
                } else {
                    runOnUiThread(() -> {

                        MessageUtil.addMessageBean(peerId, message);

                        MsgPopup msgPopup = new MsgPopup(mainContext, 3000);
                        msgPopup.setText(peerId + ": " + message.getText());
                        TextView msgText = msgPopup.findViewById(R.id.msg_text);
                        msgText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("MsgText", "OnClick");
                                openMessageActivity(true, peerId);
                            }
                        });

                        BasePopupView xMsgPopup = new XPopup.Builder(mainContext)
                                .hasShadowBg(false)
                                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                                .isCenterHorizontal(true)
                                .offsetY(200)
                                .asCustom(msgPopup);

                        xMsgPopup.show();

                        Log.d("onMessageReceived", "runOnUiThread");


                    });
                }
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onImageMessageReceivedFromPeer(final RtmImageMessage rtmImageMessage, final String peerId) {
            if (isTopActivity()) {
                Log.d("onMessageRecievedFromPeer", rtmImageMessage.getText() + " from " + peerId);
                runOnUiThread(() -> {
                    MessageUtil.addMessageBean(peerId, rtmImageMessage);
                    MsgPopup msgPopup = new MsgPopup(mainContext, 3000);
                    msgPopup.setText(peerId + ": [Image]");
                    TextView msgText = msgPopup.findViewById(R.id.msg_text);
                    msgText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("MsgText", "OnClick");
                            openMessageActivity(true, peerId);
                        }
                    });
                    new XPopup.Builder(mainContext)
                            .hasShadowBg(false)
                            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                            .isCenterHorizontal(true)
                            .offsetY(200)
                            .asCustom(msgPopup)
                            .show();

                });
            }


//            runOnUiThread(() -> {
//                if (peerId.equals(mPeerId)) {
//                    MessageBean messageBean = new MessageBean(peerId, rtmImageMessage, false);
//                    messageBean.setBackground(getMessageColor(peerId));
//                    mMessageBeanList.add(messageBean);
//                    mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
//                    mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
//                } else {
//                    MessageUtil.addMessageBean(peerId, rtmImageMessage);
//                }
//            });
        }

        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {

        }

        @Override
        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onTokenExpired() {

        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {
            String[] peerName = (String[]) map.keySet().toArray();
            int status = map.get(peerName[0]);
            switch (status){
//                case
            }

        }
    }
}