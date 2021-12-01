package com.penglab.hi5.core;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.app.ProgressDialog;
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

import com.lazy.library.logging.Logcat;
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


public class S2Activity extends BaseActivity implements ReceiveMsgInterface {
    private static final String TAG = "S2Activity";

    private Timer timer = null;
    private TimerTask timerTask;


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
    private boolean ifZscanSeries = false;

    private boolean[] temp_mode = new boolean[8];
    private float[] locationFor2dImg = new float[2];

    private static Button Zoom_in;
    private static Button Zoom_out;

    private static Button Zslice_up;
    private static Button Zslice_down;

    private static Button Zoom_in_Big;
    private static ImageButton si_logo;

    private static Button Zoom_out_Big;
    private ImageButton zseries_scan;
    private ImageButton S2start;

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
    private static FrameLayout.LayoutParams lp_x_pos;
    private static FrameLayout.LayoutParams lp_y_pos;
    private static FrameLayout.LayoutParams lp_z_pos;

    private static FrameLayout.LayoutParams lp_room_id;
    private static FrameLayout.LayoutParams lp_user_list;

    private Button PixelClassification;
    private boolean[][] select = {{true, true, true, false, false, false, false},
            {true, true, true, false, false, false, false},
            {false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false},
            {true, true, true, false, false, false, false}};


    private BigImgReader bigImgS2Reader;


    private LinearLayout ll_top;
    private LinearLayout ll_bottom;
    private static LinearLayout ll_file;

    private int measure_count = 0;
    private List<double[]> fl;

    private static boolean isBigData_Remote;
    private static boolean isBigData_Local;
    private static boolean isS2Start = false;
    private static ProgressBar progressBar;
    private static ProgressDialog progressDialog_zscan;

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

    private ArrayList<Float> loactionOf2dImg = new ArrayList<Float>();

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

    private static float[] gamePositionForIntent = {0.5f, 0.5f, 0.5f};
    private static float[] gameDirForIntent = {1, 1, 1};
    private static float[] gameHeadForIntent = {1, 0, -1};
    private static int gameLastIndexForIntent = -1;
    private static boolean gameIfNewForIntent = true;
    private static int gameScoreForIntent = 0;
    private SoundPool soundPool;
    private final int SOUNDNUM = 4;


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
    private boolean  ifgetTest = false;
    private boolean ifSmartControl = false;

    private int score = 0;
    private String scoreString = "00000";

    private static TextView scoreText;
    private int selectedBGM = 0;

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

        if (msg.startsWith("s2start:")) {

            if(msg.endsWith("scan on"))
            {
                Toast_in_Thread("scope scan on!");
                Log.e(TAG, "scope scan on!!" );
            }else if (msg.endsWith("scan off"))
            {
                Toast_in_Thread("scope scan off!");
                Log.e(TAG, "scope scan off!!" );
            }

            Log.e(TAG, "s2start:()  " + msg);


        }

        if (msg.startsWith("test:")) {

            Toast_in_Thread("Connect to scope successfully!");
            Log.e(TAG, "s2start:()  " + msg);
            ifgetTest=true;
        }





        if (msg.startsWith("s2_move:")) {
            // loadBigDataImg(msg.split(":")[1]);


            Log.e(TAG, "s2_move:" );
            if (msg.endsWith("X")) {
                Toast_in_Thread("Stage X is out of max range!");
                Log.e(TAG, "s2_move:Stage X is out of max range!" );
            } else if (msg.endsWith("Y")) {
                Toast_in_Thread("Stage Y is out of max range!");
                Log.e(TAG, "s2_move:Stage Y is out of max range!" );
            } else if (msg.endsWith("Z")) {
                Toast_in_Thread("Stage Z is out of max range!");
                Log.e(TAG, "s2_move:Stage Z is out of max range!" );
            }
        }


        if(msg.startsWith("ZScan:"))
        {
            if (msg.endsWith("ZStart")) {
                Toast_in_Thread("ZScan first img has been set up!");
                Log.e(TAG, "ZScan first img has been set up!" );
            } else if (msg.endsWith("ZStop")) {
                Toast_in_Thread("ZScan last img has been set up!");
                Log.e(TAG, "ZScan last img has been set up!" );
            } else if (msg.endsWith("Zslicesize1")) {
                Toast_in_Thread("ZScan Zslicesize1 has been set up!");
                Log.e(TAG, "ZScan Zslicesize1 has been set up!" );
            }else if (msg.endsWith("start")) {
                Toast_in_Thread("ZScan on!");
                Log.e(TAG, "ZScan on!" );
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
        }

        /*
        After msg:  "LOADFILES:0 /17301/17301_00019/17301_00019_x20874.000_y23540.000_z7388.000.ano /17301/17301_00019/test_01_fx_lh_test.ano"

        when the file is selected, room will be created, and collaborationService will be init, port is room number
         */
        if (msg.startsWith("Port:")) {

            if (msg.split(":")[1].equals("-1")) {
                Toast_in_Thread("Something wrong with this img, choose other img please !");

                return;
            }

            initMsgConnector(msg.split(":")[1]);
            if (firstJoinRoom) {
                initMsgService();
                firstJoinRoom = false;
            } else {

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
        if (msg.startsWith("/users:")) {

            if (firstLoad || copyFile) {
                /*
                when first join the room, try to get the image
                 */
                MsgConnector.getInstance().sendMsg("/ImageRes:" + Communicator.BrainNum);
                firstLoad = false;
                copyFile = false;
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
        if (msg.startsWith("ImgRes")) {
            Log.e(TAG, "msg: " + msg);
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
        if (msg.startsWith("Block:")) {

            loadBigDataImg(msg.split(":")[1]);
            MsgConnector.getInstance().sendMsg("/GetBBSwc:" + Communicator.BrainNum + ";" + Communicator.getCurRes() + ";" + Communicator.getCurrentPos() + ";");

        }


//        if (msg.startsWith("Score:")) {
//            Log.e(TAG, "get score: " + msg);
//            int serverScore = Integer.parseInt(msg.split(":")[1].split(" ")[1]);
//            Score score = Score.getInstance();
//            if (score.serverUpdateScore(serverScore)) {
//                updateScoreText();
//            }
////            initDataBase(Integer.parseInt(msg.split(":")[1].split(" ")[1]));
//        }



        /*
        for collaboration -------------------------------------------------------------------
         */

        if (msg.startsWith("/drawline_norm:")) {
            Log.e(TAG, "drawline_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String seg = msg.split(":")[1];

            if (!userID.equals(username)) {
                Communicator communicator = Communicator.getInstance();
                myS2renderer.syncAddSegSWC(communicator.syncSWC(seg));
                myS2GLSurfaceView.requestRender();
            }

        }


        if (msg.startsWith("/delline_norm:")) {
            Log.e(TAG, "delline_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String seg = msg.split(":")[1];

            if (!userID.equals(username)) {
                Communicator communicator = Communicator.getInstance();
                myS2renderer.syncDelSegSWC(communicator.syncSWC(seg));
                myS2GLSurfaceView.requestRender();
            }

        }

        if (msg.startsWith("/addmarker_norm:")) {
            Log.e(TAG, "addmarker_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String marker = msg.split(":")[1].split(";")[1];

            if (!userID.equals(username)) {
                Communicator communicator = Communicator.getInstance();
                myS2renderer.syncAddMarker(communicator.syncMarker(marker));
                myS2GLSurfaceView.requestRender();
            }
        }


        if (msg.startsWith("/delmarker_norm:")) {
            Log.e(TAG, "delmarker_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String marker = msg.split(":")[1].split(";")[1];

            if (!userID.equals(username)) {
                Communicator communicator = Communicator.getInstance();
                myS2renderer.syncDelMarker(communicator.syncMarker(marker));
                myS2GLSurfaceView.requestRender();
            }
        }


        if (msg.startsWith("/retypeline_norm:")) {
            Log.e(TAG, "retypeline_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String seg = msg.split(":")[1];

            if (!userID.equals(username)) {
                Communicator communicator = Communicator.getInstance();
                myS2renderer.syncRetypeSegSWC(communicator.syncSWC(seg));
                myS2GLSurfaceView.requestRender();
            }

        }

        /*
        for collaboration -------------------------------------------------------------------
         */

        if (msg.startsWith("GETFIRSTK:")) {
            Log.d(TAG, msg);
            if (msg.split(":").length > 1) {
                String body = msg.split(":")[1];
                String[] accountsWithScore = body.split(";");
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
                    setButtonsBigData();
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
                    //updateScoreTextHandler();
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
        progressDialog_zscan.setCancelable(false);


        initDir();
        initNim();

        initServerConnector();
        initService();



        s2initialization();


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


        S2Context = null;

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

    public static void actionStart(Context context, String invitor, String path, String soma) {
        Intent intent = new Intent(context, S2Activity.class);
        context.startActivity(intent);
        username = InfoCache.getAccount();
        acceptInvitation(path, soma);
    }


    /*
     * init buttons
     */
    private void initButtons() {
        /*
        basic Layout ------------------------------------------------------------------------------------------------------------------------
         */

        FrameLayout ll = (FrameLayout) findViewById(R.id.container1);
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


        /*
        init buttons ------------------------------------------------------------------------------------------------------------------------
         */
        si_logo = new ImageButton(this);
        si_logo.setImageResource(R.drawable.si_logo);
        si_logo.setBackgroundResource(R.drawable.circle_normal);


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

//        draw_i = new ImageButton(this);
//        draw_i.setImageResource(R.drawable.ic_draw_main);
//
//        tracing_i=new ImageButton(this);
//        tracing_i.setImageResource(R.drawable.ic_neuron);
//
//        classify_i=new ImageButton(this);
//        classify_i.setImageResource(R.drawable.ic_classify_mid);
//
//        zseries_scan = new ImageButton(this);
//        zseries_scan.setImageResource(R.drawable.ic_visibility_black_24dp);
//        zseries_scan.setBackgroundResource(R.drawable.circle_normal);
        zseries_scan = new ImageButton(this);
        zseries_scan.setImageResource(R.drawable.ic_zscan_foreground);
        //  zseries_scan.setBackgroundResource(R.drawable.);


        S2start = new ImageButton(this);
        S2start.setImageResource(R.drawable.ic_animation);
        S2start.setBackgroundResource(R.drawable.circle_normal);

//        ROI_i = new ImageButton(this);
//        ROI_i.setImageResource(R.drawable.ic_roi);
//        ROI_i.setBackgroundResource(R.drawable.circle_normal);


//        Undo_i = new ImageButton(this);
//        Undo_i.setImageResource(R.drawable.ic_undo);
//        Undo_i.setBackgroundResource(R.drawable.circle_normal);
//
//        Redo_i = new ImageButton(this);
//        Redo_i.setImageResource(R.drawable.ic_redo);
//        Redo_i.setBackgroundResource(R.drawable.circle_normal);

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

        manual_sync = new ImageButton(this);
        manual_sync.setImageResource(R.drawable.ic_baseline_autorenew_24);


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
//
//        FrameLayout.LayoutParams lp_draw_i = new FrameLayout.LayoutParams(200, 160);
//        ll_top.addView(draw_i,lp_draw_i);
//
//        FrameLayout.LayoutParams lp_tracing_i = new FrameLayout.LayoutParams(200, 160);
//        ll_top.addView(tracing_i,lp_tracing_i);

//        FrameLayout.LayoutParams lp_classify_i = new FrameLayout.LayoutParams(200, 160);
//        ll_top.addView(classify_i,lp_classify_i);

        FrameLayout.LayoutParams lp_rotation = new FrameLayout.LayoutParams(180, 120);
        lp_rotation.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_rotation.setMargins(0, 0, 180, 20);


        FrameLayout.LayoutParams lp_si_logo = new FrameLayout.LayoutParams(170, 170);
        lp_si_logo.gravity = Gravity.TOP | Gravity.LEFT;
        lp_si_logo.setMargins(0, 0, 0, 0);

        FrameLayout.LayoutParams lp_hide = new FrameLayout.LayoutParams(120, 120);
        lp_hide.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_hide.setMargins(0, 0, 20, 20);

//        FrameLayout.LayoutParams lp_ROI_i = new FrameLayout.LayoutParams(120, 120);
//        lp_ROI_i.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//        lp_ROI_i.setMargins(0, 0, 300, 20);

//        lp_undo = new FrameLayout.LayoutParams(120, 120);
//        lp_undo.setMargins(0, 20, 20, 0);
//        ll_hs_back.addView(Undo_i, lp_undo);
//
//        lp_redo = new FrameLayout.LayoutParams(120, 120);
//        lp_redo.setMargins(0, 20, 20, 0);
//        ll_hs_back.addView(Redo_i, lp_redo);

//        lp_score = new FrameLayout.LayoutParams(350, 300);
//        lp_score.gravity = Gravity.TOP | Gravity.RIGHT;
//        lp_score.setMargins(0, 350, 20, 0);

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

//        Zoom_in.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if(!myS2renderer.getIfFileLoaded()){
//                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        myS2renderer.zoom_in();
//                        myS2GLSurfaceView.requestRender();
//                    }
//                }).start();
//
//            }
//        });


//        Zoom_out.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if(!myS2renderer.getIfFileLoaded()){
//                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        myS2renderer.zoom_out();
//                        myS2GLSurfaceView.requestRender();
//                    }
//                }).start();
//
//            }
//        });

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
                myS2GLSurfaceView.requestRender();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Zseries_navigate("ZScan");


                    }
                }).start();


                if (!ifZscanSeries) {
//                    x_pos_Text.setLayoutParams(lp_x_pos);
//                    y_pos_Text.setLayoutParams(lp_y_pos);
//                    z_pos_Text.setLayoutParams(lp_z_pos);
                    progressDialog_zscan.show();
                    ifZscanSeries = true;
                } else {
                    Log.e(TAG, "zseries_scan already push ! ");
                }

//                if (isBigData_Remote){
//                    myS2renderer.resetRotation();
//                    myS2GLSurfaceView.requestRender();
//                }else {
//                    Rotation();
//                }
//                Rotation();
            }
        });


        S2start.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e(TAG, "s2send ");
                isBigData_Remote = true;
                isBigData_Local = false;
                ifGetRoiPoint = true;
                isS2Start = true;
                setButtons();

                myS2renderer.clearView(isS2Start);  //clean view before showing new image
                myS2GLSurfaceView.requestRender();

                shutFileName();
                //ServerConnectorForScope.sendMsg("s2start:");
                ServerConnector.getInstance().sendMsg("s2start:");
                //ServerConnector.getInstance().sendMsg("s2start:");

                //Toast_in_Thread("scope scan off!");
            }
        });


//        ROI_i.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if(!myS2renderer.getIfFileLoaded()){
//                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if (isBigData_Remote){
//                    ifSettingROI=!ifSettingROI;
//                    ifZooming = false;
//                    ifChangeLineType = false;
//                    ifDeletingLine = false;
//                    ifPainting = false;
//                    ifPoint = false;
//                    ifDeletingMarker = false;
//                    ifSpliting = false;
//                    ifChangeMarkerType = false;
//                    ifDeletingMultiMarker = false;
////                    new Thread(new Runnable() {
////                        @Override
////                        public void run() {
////
////                            Communicator communicator = Communicator.getInstance();
////                            communicator.zoomIn();
////
////                        }
////                    }).start();
//                }
//
//            }
//        });


//        Undo_i.setOnClickListener(new Button.OnClickListener(){
//            public void onClick(View v){
//
//                boolean undoSuccess = false;
//                try {
//                    undoSuccess = myS2renderer.undo2();
//                } catch (CloneNotSupportedException e) {
//                    e.printStackTrace();
//                }
//                if (!undoSuccess) {
//                    Toast.makeText(context, "nothing to undo", Toast.LENGTH_SHORT).show();
//                }
//                myS2GLSurfaceView.requestRender();
//            }
//        });
//
//
//
//        Redo_i.setOnClickListener(new Button.OnClickListener(){
//            public void onClick(View v){
//
//
//                boolean redoSuccess = myS2renderer.redo();
//                if (!redoSuccess){
//                    Toast_in_Thread("nothing to redo");
//                }
//                myS2GLSurfaceView.requestRender();
//            }
//        });


        Switch = new Button(this);
        Switch.setText("Pause");

        Switch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                Switch();
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


        manual_sync.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSyncBar();
                myS2renderer.deleteAllTracing();
                MsgConnector.getInstance().sendMsg("/GetBBSwc:" + Communicator.BrainNum + ";" + Communicator.getCurRes() + ";" + Communicator.getCurrentPos() + ";");
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


        this.addContentView(si_logo, lp_si_logo);
        this.addContentView(zseries_scan, lp_rotation);
        this.addContentView(S2start, lp_hide);
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
        this.addContentView(manual_sync, lp_sync_i);
//        this.addContentView(red_pen, lp_red_color);
//        this.addContentView(blue_pen, lp_blue_color);

        this.addContentView(room_id, lp_room_id);
        this.addContentView(user_list, lp_user_list);

        x_pos_Text.setVisibility(View.GONE);
        y_pos_Text.setVisibility(View.GONE);
        z_pos_Text.setVisibility(View.GONE);

        Zslice_up.setVisibility(View.GONE);
        Zslice_down.setVisibility(View.GONE);

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
        // ROI_i.setVisibility(View.GONE);
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
    private void initDir() {

        try {

            String dir_str_server = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + context.getResources().getString(R.string.app_name) + "/S2";
            File dir_server = new File(dir_str_server);
            if (!dir_server.exists()) {
                dir_server.mkdirs();
            }

        } catch (IOException e) {
            e.printStackTrace();
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

    private void initMsgService() {
        // Bind to LocalService
        Intent intent = new Intent(this, CollaborationService.class);
        bindService(intent, connection_collaboration, Context.BIND_AUTO_CREATE);
    }

    private void initMsgConnector(String port) {
        MsgConnector msgConnector = MsgConnector.getInstance();

        if (!firstJoinRoom)
            msgConnector.releaseConnection();
        msgConnector.setIp(ip_TencentCloud);
        msgConnector.setPort(port);
        msgConnector.initConnection();
    }


    private void initServerConnector() {
//        ServerConnector serverConnectorForScope = ServerConnector.getInstance();
//
//        serverConnectorForScope.setIp(ip_TencentCloud);
//        serverConnectorForScope.setPort("8511");
//        serverConnectorForScope.initConnection();


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

    /*
    for service ------------------------------------------------------------------------------------
     */


    /**
     * @param FileList filelist from server
     */
    private void LoadFiles(String FileList) {
        List<String> list_array = new ArrayList<>();
        String[] list = FileList.split(";;");

        boolean isFile = false;
        String[] fileName = new String[1];

//        Log.e(TAG, "list.length: " + list.length);
        for (int i = 0; i < list.length; i++) {
            if (list[i].split(" ")[0].endsWith(".apo") || list[i].split(" ")[0].endsWith(".eswc")
                    || list[i].split(" ")[0].endsWith(".swc") || list[i].split(" ")[0].endsWith("log"))
                continue;

            if (Communicator.getInstance().initSoma(list[i].split(" ")[0])) {
                fileName[0] = list[i].split(" ")[0];
                isFile = true;
                continue;
            }

            list_array.add(list[i].split(" ")[0]);
        }
        if (isFile) {
            list_array.add("create a new Room");
        }

        String[] list_show = new String[list_array.size()];
        for (int i = 0; i < list_array.size(); i++) {
            list_show[i] = list_array.get(i);
        }


        if (isFile) {
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
                                    switch (text) {
                                        case "create a new Room":
                                            CreateFile(conPath + "/" + fileName[0], "0");
                                            break;

                                        default:
                                            loadFileMode(conPath + "/" + text);
                                            Communicator.Path = conPath + "/" + text;
                                            break;
                                    }
                                }
                            })
                    .show();

        } else {
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


    private void loadFileMode(String filepath) {
        String[] list = filepath.split("/");
        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg("LOADFILES:2 " + filepath);
        serverConnector.setRoomName(list[list.length - 1]);

        Communicator.getInstance().setPath(filepath);
        firstLoad = true;
    }


    /**
     * create the new file & input the name of file
     *
     * @param oldname oldname of file
     * @param mode    work mode
     */
    private void CreateFile(String oldname, String mode) {
        new XPopup.Builder(this)
                .asInputConfirm("CreateRoom", "Input the name of the new Room",
                        new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String text) {
                                ServerConnector serverConnector = ServerConnector.getInstance();
                                switch (mode) {
                                    case "0":
                                        Communicator.getInstance().setPath(conPath + "/" + text);
                                        serverConnector.sendMsg("LOADFILES:0 " + oldname + " " + conPath + "/" + text);
                                        serverConnector.setRoomName(text);
                                        copyFile = true;
                                        break;
                                    case "1":
                                        Communicator.getInstance().setPath(conPath + "/" + text);
                                        serverConnector.sendMsg("LOADFILES:1 " + oldname + " " + conPath + "/" + text);
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
    private void loadBigData() {

        conPath = "";

        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerConnector.getInstance().sendMsg("GETFILELIST:" + "/", true, true);
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

    private void initNim() {
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
        Log.e("Observer<Integer>", "Observer unreadCount");
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


    private static void invitePopup(Context context, String invitor, String path, String soma) {
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
                                //    communicator.setConPath(path);
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


    private static void acceptInvitation(String path, String soma) {

        String[] list = path.split("/");
        String roomName = list[list.length - 1];

        Communicator communicator = Communicator.getInstance();
        communicator.initSoma(soma);
//        communicator.setConPath(path);
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
                .asCenterList("File Open", new String[]{"Open BigData", "Open LocalFile", "Load SwcFile"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {


                                switch (text) {

                                    case "Open LocalFile":
                                        loadLocalFile();
                                        Log.e("Open LocalFile", "Open LocalFile");
                                        break;
                                    case "Open BigData":
                                        loadBigData();
                                        break;
                                    case "Load SwcFile":
                                        LoadSwcFile();
                                        break;

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


    /**
     * function for the Tracing button
     *
     * @param v the button: tracing
     */
    private void Tracing(final View v) {

        Image4DSimple img = myS2renderer.getImg();
        if (img == null || !img.valid()) {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void APP2() throws Exception {
        Image4DSimple img = myS2renderer.getImg();
        if (img == null || !img.valid()) {
            ToastEasy("Please load image first !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        float imgZ = 0;
        boolean is2D = false;
        if (myS2renderer.getFileType() == MyRenderer.FileType.JPG || myS2renderer.getFileType() == MyRenderer.FileType.PNG) {
            imgZ = Math.max((int) img.getSz0(), (int) img.getSz1()) / 2;
            is2D = true;
        }

        ArrayList<ImageMarker> markers = myS2renderer.getMarkerList().getMarkers();
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
                p.landmarks[i] = is2D ? new LocationSimple(markers.get(i).x, markers.get(i).y, 0) :
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
//                    myS2renderer.getMarkerList().add(m);
//                }
            }
            System.out.println("size: " + nt.listNeuron.size());

            ToastEasy("APP2-Tracing finish, size of result swc: " + Integer.toString(nt.listNeuron.size()));
            myS2renderer.importNeuronTree(nt, isBigData_Remote);
            myS2renderer.saveUndo();
            myS2GLSurfaceView.requestRender();
            progressBar.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            ToastEasy(e.getMessage());
            progressBar.setVisibility(View.INVISIBLE);
        }


    }

    private void GDTracing() throws Exception {
        Image4DSimple img = myS2renderer.getImg();
        if (img == null || !img.valid()) {
            ToastEasy("Please load image first !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        ArrayList<ImageMarker> markers = myS2renderer.getMarkerList().getMarkers();
        if (markers.size() <= 1) {
            Log.v("GDTracing", "Please generate at least two markers!");
            ToastEasy("Please produce at least two markers !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        boolean is2D = false;
        float imgZ = 0;
        if (myS2renderer.getFileType() == MyRenderer.FileType.JPG || myS2renderer.getFileType() == MyRenderer.FileType.PNG) {
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
        myS2renderer.importNeuronTree(outswc, isBigData_Remote);
        myS2renderer.saveUndo();
        myS2GLSurfaceView.requestRender();
        progressBar.setVisibility(View.INVISIBLE);

    }


    private void PixelClassification(final View v) {

        Image4DSimple img = myS2renderer.getImg();
        if (img == null || !img.valid()) {
            ToastEasy("Please load a 3d image first !");
            return;
        }

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Filter by example"}, new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {


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

        Image4DSimple img = myS2renderer.getImg();
        if (img == null) {

            ToastEasy("Please load image first !");
            return;
        }
        Image4DSimple outImg = new Image4DSimple();

        NeuronTree nt = myS2renderer.getNeuronTree();
        PixelClassification p = new PixelClassification();

        boolean[][] selections = select;
        System.out.println("select is");
        System.out.println(select);
        p.setSelections(selections);

        ToastEasy("pixel  classification start !");

        try {
            outImg = p.getPixelClassificationResult(img, nt);
            System.out.println("outImg: " + outImg.getSz0() + " " + outImg.getSz1() + " " + outImg.getSz2() + " " + outImg.getSz3());
            System.out.println(outImg.getData().length);

            myS2renderer.resetImg(outImg);
            myS2GLSurfaceView.requestRender();
        } catch (Exception e) {
            ToastEasy(e.getMessage());
        }
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
    public void More_icon() {

        new XPopup.Builder(this)
                .asCenterList("More Functions...", new String[]{"Analyze Swc", "Chat", "Animate", "Settings", "Crash Info", "Quests", "Reward", "LeaderBoard", "Logout", "Help", "About"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {

                                switch (text) {
                                    case "Analyze Swc":
                                        AnalyzeSwc();
                                        break;

                                    case "Animate":
                                        if (myS2renderer.ifImageLoaded()) {
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

                                    case "Chat":
                                        openChatActivity();
                                        break;

                                    case "Game":
//                                        System.out.println("Game Start!!!!!!!");
//
//                                        ifGame = true;
//                                        Select_map();
                                        break;

                                    case "Settings":
                                        setSettings();
                                        break;

                                    case "Account Name":
                                        popUpUserAccount(S2Activity.this);
                                        break;

                                    case "Logout":
                                        logout();
                                        break;
                                    case "Crash Info":
                                        CrashInfoShare();
                                        break;

                                    case "About":
                                        About();
                                        ;
                                        break;

                                    case "Help":
                                        try {
                                            Intent helpIntent = new Intent(S2Activity.this, HelpActivity.class);
                                            startActivity(helpIntent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "Quests":
                                        startActivity(new Intent(S2Activity.this, QuestActivity.class));
                                        break;

                                    case "Achievements":
                                        showAchievementFinished();
                                        break;

                                    case "LeaderBoard":
                                        startActivity(new Intent(S2Activity.this, LeaderBoardActivity.class));
                                        break;

                                    case "Reward":
                                        startActivity(new Intent(S2Activity.this, RewardActivity.class));
                                        break;

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
                                        NeuronTree nt = myS2renderer.getNeuronTree();
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
     *
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

        isif_flag[0]=false;
        isif_flag[1]=false;
        isif_flag[2]=false;

        MDDialog.Builder builder = new MDDialog.Builder(this);
        builder.setContentView(R.layout.s2initialization);
        builder.setContentViewOperator(new MDDialog.ContentViewOperator() {
            @Override
            public void operate(View contentView) {


                Switch Connect_Server = contentView.findViewById(R.id.Connect_Server_mode);
                Switch Connect_Scope = contentView.findViewById(R.id.Connect_Scope_mode);
                TextView clean_S2_cache = contentView.findViewById(R.id.clean_S2_cache);
                IndicatorSeekBar indicator_XY = contentView.findViewById(R.id.indicator_XY_seekbar);
                IndicatorSeekBar indicator_Z = contentView.findViewById(R.id.indicator_Z_seekbar);
                Switch Start_smart_control = contentView.findViewById(R.id.Start_smart_control_mode);


                boolean ifConnect_Server = s2paraSetting.getConnect_ServerMode();
                boolean ifConnect_Scope = s2paraSetting.getConnect_ScopeMode();
                boolean ifSmart_Control = s2paraSetting.getSmart_ControlMode();

                isif_flag[2]=ifSmart_Control;
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
                        Log.e(TAG, "ifConnect_Scope: " + ifConnect_Scope);

                        Connect_Scope.setChecked(ifgetTest);
                        isif_flag[1] = ifConnect_Scope;

                    }
                });

                Start_smart_control.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        boolean ifSmart_Control= isif_flag[2];

                        if (ifSmart_Control) {
                            ifSmart_Control = false;
                            ifSmartControl = true;
                        } else {
                            ifSmart_Control=true;
                            ifSmartControl = false;
                        }

                        isif_flag[2] = ifSmart_Control;


                        Log.e(TAG, "ifSmartControl: " + ifSmart_Control);
                    }
                });
//
//
                clean_S2_cache.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast_in_Thread("clean_cache setting!");
                    }
                });

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

                s2paraSetting.setPara(isif_flag[0],isif_flag[1],isif_flag[2],XY_Per_Step,Z_Per_Step);


                Log.v(TAG, "indicator_XY: " + XY_Per_Step + ",indicator_Z: " + Z_Per_Step);



                ifSmartControl=s2paraSetting.getSmart_ControlMode();
                Log.v(TAG, "openSmartControl: " + ifSmartControl );
                if (ifSmartControl) {
                    startSmartControl();
                }


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

    private void startSmartControl() {



        ifGetRoiPoint = true;
        Log.v(TAG, "ifSmartControl: " + ifSmartControl );
    }

    private void SmartControl() {
        String scLocation=null;

        int x=myS2renderer.getImgWidth();
        int y=myS2renderer.getImgHeight();
        Log.v(TAG, "myS2renderer:scLocation " + x+y );

        Log.v(TAG, "scLocation: " + locationFor2dImg[0]+locationFor2dImg[1] );

        int xx = (int) (locationFor2dImg[0]-x/2.0);
        int yy = (int) (locationFor2dImg[1]-y/2.0);

        scLocation="sclocation:"+String.valueOf(xx)+":"+String.valueOf(yy);
        Log.v(TAG, "scLocation: " + scLocation );

        ServerConnector.getInstance().sendMsg(scLocation);

    }

    private void setSettings() {
        //  boolean[] downsample = new boolean[1];

        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.s2settings)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
//                        //Switch downsample_on_off = contentView.findViewById(R.id.switch_rotation_mode);
//                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.indicator_seekbar);
                        TextView clean_S2_cache = contentView.findViewById(R.id.clean_S2_1cache);
//                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
//                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
//                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);
//                        Spinner bgmSpinner = contentView.findViewById(R.id.bgm_spinner);
//
//                        boolean ifDownSample = preferenceSetting.getDownSampleMode();
//                        int contrast = preferenceSetting.getContrast();
//
//                        //downsample_on_off.setChecked(ifDownSample);
//                        seekbar.setProgress(contrast);
//                        bgmVolumeBar.setProgress((int) (bgmVolume * 100));
//                        buttonVolumeBar.setProgress((int) (buttonVolume * 100));
//                        actionVolumeBar.setProgress((int) (actionVolume * 100));
//
//                        RewardLitePalConnector rewardLitePalConnector = RewardLitePalConnector.getInstance();
//                        List<Integer> rewards = rewardLitePalConnector.getRewards();
//                        List<String> list = new ArrayList<>();
//                        list.add("BGM0");
//                        for (int i = 0; i < rewards.size(); i++) {
//                            if (rewards.get(i) == 1)
//                                list.add("BGM" + Integer.toString(i + 1));
//                        }
//                        String[] spinnerItems = new String[list.size()];
//                        for (int i = 0; i < list.size(); i++) {
//                            spinnerItems[i] = list.get(i);
//                        }
//
//
//                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(S2Context, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
//                        bgmSpinner.setAdapter(spinnerAdapter);
//                        bgmSpinner.setSelection(selectedBGM);
//
//                       // downsample[0] = downsample_on_off.isChecked();
//
////                        downsample_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////                            @Override
////                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                                downsample[0] = isChecked;
////                            }
////                        });
//
//
                        clean_S2_cache.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast_in_Thread("clean_cache setting!");
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
//                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.indicator_seekbar);
//                        int contrast = seekbar.getProgress();
//
//                        myS2renderer.setIfNeedDownSample(downsample[0]);
//                        myS2renderer.resetContrast(contrast);
//
//                        Log.v(TAG, "downsample: " + downsample[0] + ",contrast: " + contrast);
//                        preferenceSetting.setPref(downsample[0], contrast);
//                        myS2GLSurfaceView.requestRender();

//                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
//                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
//                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);
//
//                        bgmVolume = (float) (bgmVolumeBar.getProgress()) / 100.0f;
//                        buttonVolume = (float) (buttonVolumeBar.getProgress()) / 100.0f;
//                        actionVolume = (float) (actionVolumeBar.getProgress()) / 100.0f;

//                        Spinner bgmSpinner = contentView.findViewById(R.id.bgm_spinner);
//                        String selected = bgmSpinner.getSelectedItem().toString();
//                        if (selectedBGM != bgmSpinner.getSelectedItemPosition()) {
////                            if (selected.equals("BGM1"))
////                              //  MusicServer.setBgmSource(getApplicationContext().getExternalFilesDir(null) + "/Resources/Music/CoyKoi.mp3");
////
////                            else if (selected.equals("BGM2"))
////                              //  MusicServer.setBgmSource(getApplicationContext().getExternalFilesDir(null) + "/Resources/Music/DelRioBravo.mp3");
////
////                            else
////                              //  MusicServer.defaultBgmSource();
//
//                            selectedBGM = bgmSpinner.getSelectedItemPosition();
//
//                        }

//                        MusicServer.setBgmVolume(bgmVolume);
//                        MusicServer.setVolume(bgmVolume);

//                        String settingsPath = context.getExternalFilesDir(null).toString() + "/Settings";
//                        File settingsFile = new File(settingsPath);
//                        if (!settingsFile.exists()) {
//                            settingsFile.mkdir();
//                        }
//
//                        String volumePath = settingsPath + "/volume.txt";
//                        File volumeFile = new File(volumePath);
//                        if (!volumeFile.exists()) {
//                            try {
//                                volumeFile.createNewFile();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        try {
//                            BufferedWriter volumeWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(volumeFile)));
//                            volumeWriter.write(Float.toString(bgmVolume) + " " + Float.toString(buttonVolume) + " " + Float.toString(actionVolume));
//                            volumeWriter.flush();
//                            volumeWriter.close();
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                        Toast_in_Thread("Confirm down!");
                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        Toast_in_Thread("Cancel down!");
                    }
                })
                .setTitle("s2settings")
                .create();
        mdDialog.show();
    }



    public void cleanCache() {
        AlertDialog aDialog = new AlertDialog.Builder(S2Context)
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


    private void deleteImg() {
        Log.v("BaseActivity", "deleteImg()");
        String img_path = context.getExternalFilesDir(null).toString() + "/Img";
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

                        PreferenceLogin preferenceLogin = new PreferenceLogin(S2Activity.this);
                        //         preferenceLogin.setPref("","",false);
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            myS2renderer.setPath(showPic.getAbsolutePath());
            myS2GLSurfaceView.requestRender();
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {

                if (isBigData_Remote) {
                    String[] Direction = {"Left", "Right", "Top", "Bottom", "Front", "Back"};
                    if (Arrays.asList(Direction).contains(text)) {
                        Log.e("S2_Block_navigate", text);
                        ServerConnector.getInstance().sendMsg("s2_move:" + text);

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

                if (isBigData_Remote) {
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
                            Log.v("ifGetRoiPoint", "ifGetRoiPoint"+ifGetRoiPoint);
                            if (ifGetRoiPoint) {
                                if (myS2renderer.getFileType() == MyRenderer.FileType.JPG || myS2renderer.getFileType() == MyRenderer.FileType.PNG  || myS2renderer.getFileType() == MyRenderer.FileType.TIF) {

                                    locationFor2dImg = myS2renderer.get2dLocation(normalizedX, normalizedY);
                                    Log.v("ifGetRoiPoint", "locationFor2dImg"+locationFor2dImg[0]+locationFor2dImg[1]);
                                    SmartControl();

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
                                        if (myS2renderer.getFileType() == MyRenderer.FileType.JPG || myS2renderer.getFileType() == MyRenderer.FileType.PNG)
                                            myS2renderer.add2DMarker(normalizedX, normalizedY);
                                        else {
                                            myS2renderer.setMarkerDrawed(normalizedX, normalizedY, isBigData_Remote);
                                        }
                                        Log.v("actionPointerDown", "(" + X + "," + Y + ")");
                                        requestRender();

                                    }
                                    if (ifGetRoiPoint) {
                                        Log.v("ifGetRoiPoint", "DeletingRoiPoint");
                                        Log.v("ifGetRoiPoint", "locationFor2dImg"+locationFor2dImg[0]+locationFor2dImg[1]);
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
        isBigData_Local = false;
        ifZooming = false;

        myS2renderer.setPath(filepath);
        myS2renderer.zoom(2f);
        myS2GLSurfaceView.requestRender();
        if (ifZscanSeries) {
            progressDialog_zscan.dismiss();
        }

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
            NeuronTree nt = NeuronTree.readSWC_file(filepath);

            myS2renderer.importNeuronTree(Communicator.getInstance().convertNeuronTree(nt), false);
            myS2renderer.saveUndo();
            myS2GLSurfaceView.requestRender();
            setBigDataName();

        } catch (Exception e) {
            e.printStackTrace();
        }
        hideProgressBar();
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

    public static void setButtonsBigData() {
        if (isBigData_Remote || isBigData_Local) {
            navigation_left.setVisibility(View.VISIBLE);
            navigation_right.setVisibility(View.VISIBLE);
            navigation_up.setVisibility(View.VISIBLE);
            navigation_down.setVisibility(View.VISIBLE);
            navigation_front.setVisibility(View.VISIBLE);
            navigation_back.setVisibility(View.VISIBLE);

            Zslice_up.setVisibility(View.VISIBLE);
            Zslice_down.setVisibility(View.VISIBLE);
            Zoom_in_Big.setVisibility(View.VISIBLE);
            Zoom_out_Big.setVisibility(View.VISIBLE);

            if (isBigData_Remote) {
                //  res_list.setVisibility(View.VISIBLE);
                //  user_list.setVisibility(View.VISIBLE);
                // room_id.setVisibility(View.VISIBLE);
                // manual_sync.setVisibility(View.VISIBLE);
                //   ROI_i.setVisibility(View.VISIBLE);

            }
        }
    }

    public void setButtonsLocal() {
        if (isBigData_Remote || isBigData_Local) {
            if (isBigData_Remote) {
                res_list.setVisibility(View.GONE);
                user_list.setVisibility(View.GONE);
                room_id.setVisibility(View.GONE);
                manual_sync.setVisibility(View.GONE);
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
                manual_sync.setVisibility(View.GONE);
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


    private void hideButtons() {
        if (!ifButtonShowed)
            return;

//       ll_top.setVisibility(View.GONE);
//        ll_bottom.setVisibility(View.GONE);
//
//        animation_i.setVisibility(View.GONE);
//      //  zseries_scan.setVisibility(View.GONE);
//      //  S2start.setVisibility(View.GONE);
//        Undo_i.setVisibility(View.GONE);
//        Redo_i.setVisibility(View.GONE);


        if (isBigData_Remote || isBigData_Local) {
            navigation_back.setVisibility(View.GONE);
            navigation_down.setVisibility(View.GONE);
            navigation_front.setVisibility(View.GONE);
            navigation_left.setVisibility(View.GONE);
            navigation_right.setVisibility(View.GONE);
            navigation_up.setVisibility(View.GONE);

            Zslice_up.setVisibility(View.GONE);
            Zslice_down.setVisibility(View.GONE);

            Zoom_in_Big.setVisibility(View.GONE);
            Zoom_out_Big.setVisibility(View.GONE);

            if (isBigData_Remote) {
                res_list.setVisibility(View.GONE);
                user_list.setVisibility(View.GONE);
                room_id.setVisibility(View.GONE);
                manual_sync.setVisibility(View.GONE);
                ROI_i.setVisibility(View.GONE);
            }
        } else {

        }


        ifButtonShowed = false;

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
                manual_sync.setVisibility(View.VISIBLE);
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
        isBigData_Remote = false;
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











