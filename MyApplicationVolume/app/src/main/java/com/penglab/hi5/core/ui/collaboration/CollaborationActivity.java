package com.penglab.hi5.core.ui.collaboration;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.michaldrabik.tapbarmenulib.TapBarMenu;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_unit;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.chat.nim.main.helper.SystemMessageUnreadManager;
import com.penglab.hi5.chat.nim.reminder.ReminderManager;
import com.penglab.hi5.chat.nim.session.extension.InviteAttachment;
import com.penglab.hi5.core.BaseActivity;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.basic.ReceiveMsgInterface;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.core.collaboration.connector.ServerConnector;
import com.penglab.hi5.core.collaboration.service.BasicService;
import com.penglab.hi5.core.collaboration.service.CollaborationService;
import com.penglab.hi5.core.collaboration.service.ManageService;
import com.penglab.hi5.core.fileReader.annotationReader.ApoReader;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.BoutonDetection.BoutonDetectionActivity;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.annotation.EditMode;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.core.ui.marker.MarkerFactoryActivity;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.model.img.CollaborateNeuronInfo;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollaborationActivity extends BaseActivity implements ReceiveMsgInterface, ColorPickerDialogListener {

    private static final String TAG = "CollaborationActivity";
    private AnnotationGLSurfaceView annotationGLSurfaceView;
    private CollaborationViewModel collaborationViewModel;

    private static String conPath = "";
    private static String port = "";
    public static boolean firstLoad = true;
    private boolean firstJoinRoom = true;
    private boolean copyFile = false;

    private boolean mBoundManagement = false;
    private boolean mBoundCollaboration = false;

    private static Context mainContext;
    private AlertDialog dialog;

    private static final int HANDLER_TOAST_INFO_STATIC = 5;

    public static String username;

    public static int id;

    private View bigDataModeView;
    private View commonView;

    private TapBarMenu tapBarMenu;
    private ImageView addCurve;
    private ImageView addMarker;
    private ImageView deleteCurve;
    private ImageView deleteMarker;
    private ImageView splitCurve;
    private Button collaborateResButton;

    private Button btnUserList;

    private ImageButton collaborateRightButton;

    private ImageButton collaborateLeftButton;

    private ImageButton collaborateUpWardButton;

    private ImageButton collaborateDownWardButton;

    private Button collaborateForWardButton;

    private Button collaborateBackWardButton;

    public enum ShiftDirection {
        RIGHT, LEFT, UP, DOWN, FRONT, BACK
    }

    private ImageButton ROI_i;
    private ImageButton editModeIndicator;
    private static BasePopupView downloadingPopupView;
    private static BasePopupView syncingPopupView;

    static Timer timerDownload;
    static Timer timerSync;
    private Timer timer = null;
    private TimerTask timerTask;
    private Toolbar toolbar;

    private List<String> neuronNumberList = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRecMessage(String msg) {
        if (msg.startsWith("TestSocketConnection")) {
            //ServerConnector.getInstance().sendMsg("HeartBeat");
        } else {
            Log.e(TAG, "onRecMessage()  " + msg);
        }
        /*
        After msg:  "/login:xf"

        server will send user list when the users in current room are changed
         */
        if (msg.startsWith("/onlineusers:")) {
            String[] users = msg.split(":")[1].split(",");
            List<String> newUserList = Arrays.asList(users);
            updateUserList(newUserList);
        }

        if (msg.startsWith("File:")) {
            if (msg.endsWith(".apo")) {
                loadBigDataApo(msg.split(":")[1]);
            } else if (msg.endsWith(".swc") || msg.endsWith(".eswc")) {
                loadBigDataSwc(msg.split(":")[1]);
            }

        }
        /*
        for collaboration -------------------------------------------------------------------
         */

        if (msg.startsWith("/drawline_norm:") || msg.startsWith("/delline_norm:") || msg.startsWith("/retypeline_norm:") || msg.startsWith("/splitline_norm:") || msg.startsWith("/addmarker_norm:") || msg.startsWith("/delmarker_norm:")) {
            String[] singleMsg = msg.split(";");
            for (int i = 0; i < singleMsg.length; i++) {
                String singlemsg = singleMsg[i];

                if (singlemsg.startsWith("/splitline_norm:")) {
                    String userID = singlemsg.split(":")[1].split(",")[0].split(" ")[1];
                    String toolType = singlemsg.split(":")[1].split(",")[0].split(" ")[0];
                    int index = singlemsg.indexOf(",");
                    String seg = singlemsg.substring(index + 1);

                    if (!userID.equals(String.valueOf(id))) {
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncSplitSegSWC(communicator.syncSWC(seg, toolType));
                        annotationGLSurfaceView.requestRender();
                    }
                }

                if (singlemsg.startsWith("/drawline_norm:")) {
                    String userID = singlemsg.split(":")[1].split(",")[0].split(" ")[1];
                    String toolType = singlemsg.split(":")[1].split(",")[0].split(" ")[0];
                    int index = singlemsg.indexOf(",");
                    String seg = singlemsg.substring(index + 1);

                    if (!userID.equals(String.valueOf(id))) {
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncAddSegSWC(communicator.syncSWC(seg, toolType));
                        annotationGLSurfaceView.requestRender();
                    }
                }

                if (singlemsg.startsWith("/delline_norm:")) {
//                    Log.e(TAG,"delline_norm");

                    String userID = singlemsg.split(":")[1].split(",")[0].split(" ")[1];
                    String toolType = singlemsg.split(":")[1].split(",")[0].split(" ")[0];
                    int index = singlemsg.indexOf(",");
                    String seg = singlemsg.substring(index + 1);
                    if (!userID.equals(String.valueOf(id))) {
                        Log.e(TAG, "enter delete");
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncDelSegSWC(communicator.syncSWC(seg, toolType));
//                        annotationGLSurfaceView.getAnnotationRender().syncDelSegSWC(communicator.syncSWC(seg));
                        annotationGLSurfaceView.requestRender();
                    }
                }

                if (singlemsg.startsWith("/addmarker_norm:")) {

                    String userID = singlemsg.split(":")[1].split(",")[0].split(" ")[1];

                    int index = singlemsg.indexOf(",");
                    String markers = singlemsg.substring(index + 1);
//                    String marker      = singlemsg.split(":")[1].split(",")[1];

                    if (!userID.equals(String.valueOf(id))) {
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncAddMarker(communicator.syncMarker(markers));
//                        annotationGLSurfaceView.syncAddMarkerGlobal(communicator.syncMarkerGlobal(marker));
                        annotationGLSurfaceView.requestRender();
                    }
                }

                if (singlemsg.startsWith("/delmarker_norm:")) {
//                    Log.e(TAG,"delmarker_norm");

                    String userID = singlemsg.split(":")[1].split(",")[0].split(" ")[1];
                    int index = singlemsg.indexOf(",");
                    String markers = singlemsg.substring(index + 1);

                    if (!userID.equals(String.valueOf(id))) {
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncDelMarker(communicator.syncMarker(markers));
//                        annotationGLSurfaceView.syncDelMarkerGlobal(communicator.syncMarkerGlobal(marker));
                        annotationGLSurfaceView.requestRender();
                    }
                }

                if (msg.startsWith("/retypeline_norm:")) {

                    String userID = msg.split(":")[1].split(",")[0].split(" ")[1];
                    String toolType = singlemsg.split(":")[1].split(",")[0].split(" ")[0];

                    int index = singlemsg.indexOf(",");
                    String seg = singlemsg.substring(index + 1);

                    if (!userID.equals(String.valueOf(id))) {
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncRetypeSegSWC(communicator.syncSWC(seg, toolType));
                        annotationGLSurfaceView.requestRender();
                    }
                }
            }
        }

        if (msg.startsWith("/WARN")) {
            String[] singleMsg = msg.split(";");
            for (String singlemsg : singleMsg) {
                String regex = "/WARN_(.*):(.*)";

                // 编译正则表达式
                Pattern pattern = Pattern.compile(regex);

                // 创建Matcher对象
                Matcher matcher = pattern.matcher(singlemsg);

                // 查找匹配
                if (matcher.find()) {
                    // 获取第一个分组（.*）
                    String reason = Objects.requireNonNull(matcher.group(1)).trim();
                    // 获取第二个分组（.*）
                    String msgInfos = Objects.requireNonNull(matcher.group(2)).trim();
                    List<String> listWithHeader = Arrays.asList(msgInfos.split(","));
                    List<String> listWithHeader2 = new ArrayList<>(listWithHeader);
                    System.out.println(listWithHeader);
                    String header = listWithHeader.get(0);
                    String sender = header.split(" ")[0].trim();
                    listWithHeader2.remove(0);
                    if(sender.equals("server")){
                        if(reason.equals("TipUndone") || reason.equals("CrossingError") || reason.equals("MulBifurcation") ||
                                reason.equals("BranchingError") || reason.equals("NearBifurcation")){
                            Communicator communicator = Communicator.getInstance();
                            annotationGLSurfaceView.syncAddMarker(communicator.syncMarker(String.join(",", listWithHeader2)));
//                        annotationGLSurfaceView.syncAddMarkerGlobal(communicator.syncMarkerGlobal(marker));
                            annotationGLSurfaceView.requestRender();
                        }
                        else if(reason.equals("Loop")){
                            int result = Integer.parseInt(header.split(" ")[1].trim());
                            if(result == 0){
                                Communicator communicator = Communicator.getInstance();
                                annotationGLSurfaceView.syncAddMarker(communicator.syncMarker(String.join(",", listWithHeader2)));
//                        annotationGLSurfaceView.syncAddMarkerGlobal(communicator.syncMarkerGlobal(marker));
                                annotationGLSurfaceView.requestRender();
                            }
                        }
                        else{
                            Toast_in_Thread_static("error from DBMS!");
                        }
                    }
                }
            }
        }

        if (msg.startsWith("/FEEDBACK_ANALYZE")) {
            String[] singleMsg = msg.split(";");
            for (String singlemsg : singleMsg) {
                String regex = "/FEEDBACK_ANALYZE_(.*):(.*)";

                // 编译正则表达式
                Pattern pattern = Pattern.compile(regex);

                // 创建Matcher对象
                Matcher matcher = pattern.matcher(singlemsg);

                // 查找匹配
                if (matcher.find()) {
                    // 获取第一个分组（.*）
                    String reason = Objects.requireNonNull(matcher.group(1)).trim();
                    // 获取第二个分组（.*）
                    String msgInfos = Objects.requireNonNull(matcher.group(2)).trim();
                    List<String> listWithHeader = Arrays.asList(msgInfos.split(","));
                    List<String> listWithHeader2 = new ArrayList<>(listWithHeader);
                    String header = listWithHeader.get(0);
                    String sender = header.split(" ")[0].trim();
                    String request_userid = header.split(" ")[1].trim();
                    int result = Integer.parseInt(header.split(" ")[2].trim());
                    listWithHeader2.remove(0);
                    if(sender.equals("server")){
                        if(reason.equals("ColorMutation") || reason.equals("Dissociative")|| reason.equals("Angle")){
                            if(result == 0){
                                Communicator communicator = Communicator.getInstance();
                                annotationGLSurfaceView.syncAddMarker(communicator.syncMarker(String.join(",", listWithHeader2)));
//                        annotationGLSurfaceView.syncAddMarkerGlobal(communicator.syncMarkerGlobal(marker));
                                annotationGLSurfaceView.requestRender();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void Toast_in_Thread_static(String message) {
        Message msg = new Message();
        msg.what = HANDLER_TOAST_INFO_STATIC;
        Bundle bundle = new Bundle();
        bundle.putString("Toast_msg", message);
        msg.setData(bundle);
    }

    @Override
    public void onRecBinData(String msg, byte[] a) {

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collaboration);

        annotationGLSurfaceView = findViewById(R.id.gl_surface_view);
        annotationGLSurfaceView.setBigData(true);

        toolbar = findViewById(R.id.toolbar_collaboration);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Collaboration");

//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        collaborationViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(CollaborationViewModel.class);

        downloadingPopupView = new XPopup.Builder(this)
                .asLoading("Call Hi5 Programming......");

        syncingPopupView = new XPopup.Builder(this)
                .asLoading("Syncing......");

        username = InfoCache.getAccount();

        collaborationViewModel.getUserIdForCollaborate(InfoCache.getAccount());

        mainContext = this;

        collaborationViewModel.getAnnotationMode().observe(this, new androidx.lifecycle.Observer<CollaborationViewModel.AnnotationMode>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(CollaborationViewModel.AnnotationMode annotationMode) {
                if (annotationMode == null) {
                    return;
                }
                updateUI(annotationMode);
                updateOptionsMenu(annotationMode);
            }
        });


        collaborationViewModel.getCollorationDataSource().getNeuronListCollaborate().observe(this, new androidx.lifecycle.Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result instanceof Result.Success) {
                    int index = 0;
                    List<CollaborateNeuronInfo> potentialDownloadNeuronInfoList = (List<CollaborateNeuronInfo>) ((Result.Success<?>) result).getData();
                    neuronNumberList.clear();
                    for (int i = 0; i < potentialDownloadNeuronInfoList.size(); i++) {
                        CollaborateNeuronInfo item = potentialDownloadNeuronInfoList.get(i);
                        neuronNumberList.add(item.getNeuronName());
                        if(item.getNeuronName().equals(collaborationViewModel.getPotentialDownloadNeuronInfo().getNeuronName())){
                            index = i;
                        }
                    }
                    collaborationViewModel.handleLoadImage(potentialDownloadNeuronInfoList.get(index));
                    downloadingPopupView.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            downloadingPopupView.dismiss();
                        }
                    }, 2000);

                }
            }
        });

        collaborationViewModel.getCollorationDataSource().getAnoListCollaborate().observe(this, new androidx.lifecycle.Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result instanceof Result.Success) {
                    List<String> data = (List<String>) ((Result.Success<?>) result).getData();
                    String[] str = new String[data.size()];
//                    String[] anoListShow = data.toArray(str);
                    String[] anoListShow = {"18454_00019","00029_P001_T01-S001_MFG_R0460_WY-20220415_GYC_03"};
                    new XPopup.Builder(CollaborationActivity.this).
                            maxHeight(1350).
                            maxWidth(800).
                            asCenterList("Ano Number",
                                    anoListShow, new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {
                                            ToastEasy("Click" + text);
                                            collaborationViewModel.handleAnoResult(text.trim());
                                        }
                                    }).show();
                } else if (result instanceof Result.Error) {
                    ToastEasy(result.toString());
                }
            }
        });
        collaborationViewModel.getCollorationDataSource().getDownloadAnoResult().observe(this, new androidx.lifecycle.Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                collaborationViewModel.handleLoadAnoResult(result);

            }
        });

        collaborationViewModel.getPortResult().observe(this, new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(String s) {
                port = s;
            }
        });

        collaborationViewModel.getImageDataSource().getBrainListResult().observe(this, new androidx.lifecycle.Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                Log.e(TAG, "enter getBrainListResult()");
                if (result == null) {
                    return;
                }
                collaborationViewModel.handleBrainListResult(result);
            }
        });

        collaborationViewModel.getImageDataSource().getDownloadImageResult().observe(this, new androidx.lifecycle.Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                collaborationViewModel.handleDownloadImageResult(result);

                initMsgConnector(port);
                if (firstJoinRoom) {
                    initMsgService();
                    firstJoinRoom = false;
                } else {
                    CollaborationService.resetConnection();
                }

                id = collaborationViewModel.getCollorationDataSource().getUserId();
                Log.e(TAG, collaborationViewModel.getResMap().toString());
                List<String> roiList = collaborationViewModel.getResMap().get(collaborationViewModel.getPotentialDownloadNeuronInfo().getBrainName());

                assert roiList != null;
                MsgConnector.getInstance().sendMsg("/login:" + id + " " + InfoCache.getAccount() + " " + InfoCache.getToken() + " " + roiList.get(0) + " " + 2);
            }
        });

        collaborationViewModel.getImageResult().observe(this, new androidx.lifecycle.Observer<ResourceResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult == null) {
                    return;
                }
                if (resourceResult.isSuccess()) {
                    annotationGLSurfaceView.openFile();
                } else {
                    ToastEasy(resourceResult.getError());
                }

            }
        });

        collaborationViewModel.getCollaborationArborInfoState().getCenterLocation().observe(this, new androidx.lifecycle.Observer<XYZ>() {
            @Override
            public void onChanged(XYZ centerLocation) {

                collaborationViewModel.navigateAndZoomInBlock((int) centerLocation.x, (int) centerLocation.y, (int) centerLocation.z);
                annotationGLSurfaceView.convertCoordsForMarker(collaborationViewModel.getCoordinateConvert());
                annotationGLSurfaceView.convertCoordsForSWC(collaborationViewModel.getCoordinateConvert());
            }
        });
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        annotationGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
        annotationGLSurfaceView.onPause();
    }

    @Override
    protected void onRestart() {
        Log.e(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

        mainContext = null;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (timerDownload != null) {
            timerDownload.cancel();
            timerDownload = null;
        }

        if (timerSync != null) {
            timerSync.cancel();
            timerSync = null;
        }
    }

    private void initService() {
        // Bind to ManageService
        Intent intent = new Intent(this, ManageService.class);
        bindService(intent, connection_management, Context.BIND_AUTO_CREATE);
    }

    private void initMsgService() {
//        Log.e(TAG,"initMsgService");
        // Bind to CollaborationService
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
        ServerConnector serverConnector = ServerConnector.getInstance();

        serverConnector.setIp(ip_TencentCloud);
        serverConnector.setPort("26000");
        serverConnector.initConnection();

    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection_management = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BasicService.LocalBinder binder = (BasicService.LocalBinder) service;
//            ManageService manageService = (ManageService) binder.getService();
            binder.addReceiveMsgInterface((CollaborationActivity) getActivityFromContext(mainContext));
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
//            CollaborationService collaborationService = (CollaborationService) binder.getService();
            binder.addReceiveMsgInterface((CollaborationActivity) getActivityFromContext(mainContext));
            mBoundCollaboration = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBoundCollaboration = false;
        }
    };

    public static Context getContext() {
        return context;
    }

    /**
     * 注册/注销系统消息未读数变化
     */

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

                            invitePopup(mainContext, invitor, path, soma);
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
                                communicator.setPath(path);
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

    public void showUserList(Activity activity) {
        Log.d("MyApp", "showUserList function called"); // Add a log statement to check if the function is called

        if (MsgConnector.userList.isEmpty()) {
            Toast.makeText(activity, "No online users found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the online user list to a string array
        String[] userList = MsgConnector.userList.toArray(new String[0]);
        Log.e(TAG,"Userlist"+userList);

        // Create the AlertDialog only if it's null
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Online Users")
                    .setItems(userList, null);
            dialog = builder.create();
        }

        // Show the dialog if the activity is running
        if (!activity.isFinishing() && !activity.isDestroyed()) {
            dialog.show();
        }
    }

    private void updateUserList(List<String> newUserList) {
        List<String> addedUsers = new ArrayList<>();
        for (String user : newUserList) {
            if (!MsgConnector.userList.contains(user) && !user.equals(username)) {
                addedUsers.add(user);
            }
        }
        for (String user : addedUsers) {
            Toast_in_Thread("User " + user + " joined!");
        }

        List<String> removedUsers = new ArrayList<>();
        for (String user : MsgConnector.userList) {
            if (!newUserList.contains(user)) {
                removedUsers.add(user);
            }
        }
        for (String user : removedUsers) {
            Toast_in_Thread("User " + user + " left!");
        }

        // Update MsgConnector.userList with the new user list
        MsgConnector.userList.clear();
        MsgConnector.userList.addAll(newUserList);
    }


    public void loadBigDataApo(String filepath) {

        try {
            ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
            ApoReader apoReader = new ApoReader();
            apo = apoReader.read(filepath);

            if (apo == null) {
                Toast_in_Thread("There is something wrong with apo file !");
            }

            annotationGLSurfaceView.importApo(apo);
            annotationGLSurfaceView.requestRender();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadBigDataSwc(String filepath) {
        try {
            NeuronTree nt = NeuronTree.readSWC_file(filepath);

            annotationGLSurfaceView.importNeuronTree(Communicator.getInstance().convertNeuronTree(nt), false);
            annotationGLSurfaceView.requestRender();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return cmpNameTemp.equals("ComponentInfo{com.penglab.hi5/com.penglab.hi5.core.CollaborationActivity}");
    }

    private void resetUI4AllMode() {
        hideUI4BigDataMode();
        hideCommonUI();
    }

    private void hideUI4BigDataMode() {
        if (bigDataModeView != null) {
            bigDataModeView.setVisibility(View.GONE);
        }
    }

    private void hideCommonUI() {
        if (commonView != null) {
            commonView.setVisibility(View.GONE);
        }
    }

    private void showUI4BigDataMode() {
        if (bigDataModeView == null) {
            // load layout view
            LinearLayout.LayoutParams lp4BigDataMode = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            bigDataModeView = getLayoutInflater().inflate(R.layout.annotation_collaborate_mode, null);


            this.addContentView(bigDataModeView, lp4BigDataMode);

            collaborateRightButton = findViewById(R.id.collaborate_right_file_button);
            collaborateLeftButton = findViewById(R.id.collaborate_left_file_button);
            collaborateUpWardButton = findViewById(R.id.collaborate_upward_file_button);
            collaborateDownWardButton = findViewById(R.id.collaborate_downward_file_button);
            collaborateForWardButton = findViewById(R.id.collaborate_forward_file_button);
            collaborateBackWardButton = findViewById(R.id.collaborate_backward_file_button);
            collaborateRightButton.setVisibility(View.VISIBLE);
            collaborateLeftButton.setVisibility(View.VISIBLE);
            collaborateUpWardButton.setVisibility(View.VISIBLE);
            collaborateDownWardButton.setVisibility(View.VISIBLE);
            collaborateForWardButton.setVisibility(View.VISIBLE);
            collaborateBackWardButton.setVisibility(View.VISIBLE);

            collaborateRightButton.setOnClickListener(new CollaborateButtonClickListener());
            collaborateLeftButton.setOnClickListener(new CollaborateButtonClickListener());
            collaborateUpWardButton.setOnClickListener(new CollaborateButtonClickListener());
            collaborateDownWardButton.setOnClickListener(new CollaborateButtonClickListener());
            collaborateForWardButton.setOnClickListener(new CollaborateButtonClickListener());
            collaborateBackWardButton.setOnClickListener(new CollaborateButtonClickListener());

            ImageButton collaborateCheckFileButton = findViewById(R.id.check_file_list_button);
            collaborateCheckFileButton.setVisibility(View.GONE);

        } else {
            bigDataModeView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showCommonUI() {
        if (commonView == null) {
            // load layout view
            LinearLayout.LayoutParams lpCommon = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            commonView = getLayoutInflater().inflate(R.layout.annotation_common_collaborate, null);
            this.addContentView(commonView, lpCommon);

            collaborateResButton = findViewById(R.id.collaborate_res_button);
            collaborateResButton.setVisibility(View.VISIBLE);

            btnUserList = findViewById(R.id.collaborate_user_list);
            btnUserList.setVisibility(View.VISIBLE);

            ROI_i = new ImageButton(this);
            ROI_i.setImageResource(R.drawable.ic_roi);
            ROI_i.setBackgroundResource(R.drawable.circle_normal);

            FrameLayout.LayoutParams lp_ROI_i = new FrameLayout.LayoutParams(120, 120);
            lp_ROI_i.gravity = Gravity.BOTTOM | Gravity.LEFT;
            lp_ROI_i.setMargins(20, 0, 300, 400);
            this.addContentView(ROI_i,lp_ROI_i);

            editModeIndicator = findViewById(R.id.edit_mode_indicator_collaborate);
            tapBarMenu = findViewById(R.id.tapBarMenu_collaborate);
            addCurve = tapBarMenu.findViewById(R.id.draw_i_collaborate);
            addMarker = tapBarMenu.findViewById(R.id.pinpoint_collaborate);
            deleteCurve = tapBarMenu.findViewById(R.id.delete_curve_collaborate);
            deleteMarker = tapBarMenu.findViewById(R.id.delete_marker_collaborate);
            splitCurve = tapBarMenu.findViewById(R.id.split_curve_collaborate);

            BoomMenuButton boomMenuButton = tapBarMenu.findViewById(R.id.expanded_menu_collaborate);

            collaborateResButton.setOnClickListener(new CollaborateButtonClickListener());
            btnUserList.setOnClickListener(new CollaborateButtonClickListener());

            ROI_i.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(annotationGLSurfaceView.getEditMode().getValue() == EditMode.ZOOM_IN_ROI){
                        annotationGLSurfaceView.setEditMode(EditMode.NONE);
                        ROI_i.setImageResource(R.drawable.ic_roi_stop);
                    }else{
                        annotationGLSurfaceView.setEditMode(EditMode.ZOOM_IN_ROI);
                        ROI_i.setImageResource(R.drawable.ic_roi);
                    }
                    }
            });

            tapBarMenu.setOnClickListener(v -> tapBarMenu.toggle());
            addCurve.setOnClickListener(this::onMenuItemClick);
            addMarker.setOnClickListener(this::onMenuItemClick);
            deleteCurve.setOnClickListener(this::onMenuItemClick);
            deleteMarker.setOnClickListener(this::onMenuItemClick);
            splitCurve.setOnClickListener(this::onMenuItemClick);

            addCurve.setOnLongClickListener(this::onMenuItemLongClick);
            addMarker.setOnLongClickListener(this::onMenuItemLongClick);

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .listener(index -> annotationGLSurfaceView.setEditMode(EditMode.CHANGE_CURVE_TYPE))
                    .normalImageRes(R.drawable.ic_change_curve_type).normalText("Change Curve Color"));

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .listener(index -> annotationGLSurfaceView.setEditMode(EditMode.CHANGE_MARKER_TYPE))
                    .normalImageRes(R.drawable.ic_change_marker_type).normalText("Change Marker Color"));

        } else {
            commonView.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    private void onMenuItemClick(View view) {
        // resetUI
        addCurve.setImageResource(R.drawable.ic_draw_main);
        addMarker.setImageResource(R.drawable.ic_marker_main);
        deleteCurve.setImageResource(R.drawable.ic_delete_curve_normal);
        deleteMarker.setImageResource(R.drawable.ic_marker_delete_normal);
        splitCurve.setImageResource(R.drawable.ic_split_undo);

        switch (view.getId()) {
            case R.id.draw_i_collaborate:
                if (annotationGLSurfaceView.setEditMode(EditMode.PAINT_CURVE)) {
                    addCurve.setImageResource(R.drawable.ic_draw);
                }
                break;
            case R.id.pinpoint_collaborate:
                if (annotationGLSurfaceView.setEditMode(EditMode.PINPOINT)) {
                    addMarker.setImageResource(R.drawable.ic_add_marker);
                }
                break;
            case R.id.split_curve_collaborate:
                if (annotationGLSurfaceView.setEditMode(EditMode.SPLIT)) {
                    splitCurve.setImageResource(R.drawable.ic_split);
                }
                break;
            case R.id.delete_curve_collaborate:
                if (annotationGLSurfaceView.setEditMode(EditMode.DELETE_CURVE)) {
                    deleteCurve.setImageResource(R.drawable.ic_delete_curve);
                }
                break;
            case R.id.delete_marker_collaborate:
                if (annotationGLSurfaceView.setEditMode(EditMode.DELETE_MARKER)) {
                    deleteMarker.setImageResource(R.drawable.ic_marker_delete);
                }
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    private boolean onMenuItemLongClick(View view) {
        switch (view.getId()) {
            case R.id.draw_i_collaborate:
                ColorPickerDialog.newBuilder()
                        .setShowColorShades(false)
                        .setAllowCustom(false)
                        .setDialogId(R.id.draw_i_collaborate)
                        .setDialogTitle(R.string.curve_map_title)
                        .setColor(ContextCompat.getColor(this,
                                V_NeuronSWC_unit.typeToColor(annotationGLSurfaceView.getLastCurveType())))
                        .setPresets(getResources().getIntArray(R.array.colorMap))
                        .setSelectedButtonText(R.string.color_selector_confirm)
                        .show(this);
                return true;
            case R.id.pinpoint_collaborate:
                ColorPickerDialog.newBuilder()
                        .setShowColorShades(false)
                        .setAllowCustom(false)
                        .setDialogId(R.id.pinpoint_collaborate)
                        .setDialogTitle(R.string.marker_map_title)
                        .setColor(ContextCompat.getColor(this,
                                ImageMarker.typeToColor(annotationGLSurfaceView.getLastMarkerType())))
                        .setPresets(getResources().getIntArray(R.array.colorMap))
                        .setSelectedButtonText(R.string.color_selector_confirm)
                        .show(this);
                return true;
            default:
                return false;
        }
    }


    @SuppressLint("NonConstantResourceId")
    public void onColorSelected(int dialogId, int color) {
        String colorRGB = Integer.toHexString(color).toUpperCase(Locale.ROOT);
        switch (dialogId) {
            case R.id.draw_i_collaborate:
                annotationGLSurfaceView.setLastCurveType(V_NeuronSWC_unit.colorToType(colorRGB));
                break;
            case R.id.pinpoint_collaborate:
                annotationGLSurfaceView.setLastMarkerType(ImageMarker.colorToType(colorRGB));
                break;
        }
    }

    public void onDialogDismissed(int dialogId) {
        // TODO: sth when dialog dismiss, cur is empty
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUI(CollaborationViewModel.AnnotationMode annotationMode) {
        resetUI4AllMode();
        switch (annotationMode) {
            case BIG_DATA:
                showCommonUI();
                showUI4BigDataMode();
                break;
            case NONE:
                Log.e(TAG, "Default UI");
                break;
            default:
                ToastEasy("Something wrong with annotation mode !");
        }
    }

    private void updateOptionsMenu(CollaborationViewModel.AnnotationMode annotationMode) {
        toolbar.getMenu().clear();
        switch (annotationMode) {
            case NONE:
                toolbar.inflateMenu(R.menu.annotation_menu_basic);
                break;
            case BIG_DATA:
                toolbar.inflateMenu(R.menu.annotation_menu_editable_file);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.annotation_menu_basic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                LoginActivity.start(CollaborationActivity.this);
                finish();
                return true;

//            case R.id.undo:
//                annotationGLSurfaceView.undo();
//                return true;
//
//            case R.id.redo:
//                annotationGLSurfaceView.redo();
//                return true;

            case R.id.file:
                openFile();
                return true;

            case R.id.more:
                moreFunctions();
                return true;

//            case R.id.share:
//                annotationGLSurfaceView.screenCapture();
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    class CollaborateButtonClickListener implements View.OnClickListener {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.collaborate_res_button:
                    showResListPopup();
                    break;
                case R.id.collaborate_user_list:
                    showUserList(CollaborationActivity.this);
                    break;
                case R.id.collaborate_right_file_button:
                    collaborationViewModel.shiftBlock(ShiftDirection.RIGHT);
                    annotationGLSurfaceView.convertCoordsForMarker(collaborationViewModel.getCoordinateConvert());
                    annotationGLSurfaceView.convertCoordsForSWC(collaborationViewModel.getCoordinateConvert());
                    break;
                case R.id.collaborate_left_file_button:
                    collaborationViewModel.shiftBlock(ShiftDirection.LEFT);
                    annotationGLSurfaceView.convertCoordsForMarker(collaborationViewModel.getCoordinateConvert());
                    annotationGLSurfaceView.convertCoordsForSWC(collaborationViewModel.getCoordinateConvert());
                    break;
                case R.id.collaborate_upward_file_button:
                    collaborationViewModel.shiftBlock(ShiftDirection.UP);
                    annotationGLSurfaceView.convertCoordsForMarker(collaborationViewModel.getCoordinateConvert());
                    annotationGLSurfaceView.convertCoordsForSWC(collaborationViewModel.getCoordinateConvert());
                    break;
                case R.id.collaborate_downward_file_button:
                    collaborationViewModel.shiftBlock(ShiftDirection.DOWN);
                    annotationGLSurfaceView.convertCoordsForMarker(collaborationViewModel.getCoordinateConvert());
                    annotationGLSurfaceView.convertCoordsForSWC(collaborationViewModel.getCoordinateConvert());
                    break;
                case R.id.collaborate_forward_file_button:
                    collaborationViewModel.shiftBlock(ShiftDirection.FRONT);
                    annotationGLSurfaceView.convertCoordsForMarker(collaborationViewModel.getCoordinateConvert());
                    annotationGLSurfaceView.convertCoordsForSWC(collaborationViewModel.getCoordinateConvert());
                    break;
                case R.id.collaborate_backward_file_button:
                    collaborationViewModel.shiftBlock(ShiftDirection.BACK);
                    annotationGLSurfaceView.convertCoordsForMarker(collaborationViewModel.getCoordinateConvert());
                    annotationGLSurfaceView.convertCoordsForSWC(collaborationViewModel.getCoordinateConvert());
                    break;
                default:
                    break;
            }
        }
    }

    private void openFile() {
        new XPopup.Builder(this)
                .asCenterList("File Open", new String[]{"Open BigData"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String item) {
                                switch (item) {
                                    case "Open BigData":
                                        collaborationViewModel.getAno();
//                                        collaborationViewModel.getImageList();
                                        break;
                                    default:
                                        ToastEasy("Something wrong in function openFile !");
                                }
                            }
                        })
                .show();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CollaborationActivity.class);
        context.startActivity(intent);
    }

    private void showResListPopup() {
        List<String> roiList = collaborationViewModel.getResMap().get(collaborationViewModel.getPotentialDownloadNeuronInfo().getBrainName());
        assert roiList != null;
        String[] rois = roiList.toArray(new String[]{});
        rois[collaborationViewModel.getCoordinateConvert().getResIndex()-1]=rois[collaborationViewModel.getCoordinateConvert().getResIndex()-1] + "  √";
        new XPopup.Builder(CollaborationActivity.this).
                maxHeight(1350).
                maxWidth(800).
                asCenterList("Res List", rois, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        ToastEasy("Click" + text);
                        collaborationViewModel.switchRes(position, text.trim());
                        annotationGLSurfaceView.convertCoordsForMarker(collaborationViewModel.getCoordinateConvert());
                        annotationGLSurfaceView.convertCoordsForSWC(collaborationViewModel.getCoordinateConvert());
//                        annotationGLSurfaceView.requestRender();
                    }
                }).show();

    }

    private void moreFunctions() {
        new XPopup.Builder(this)
                .maxHeight(1500)
                .asCenterList("More Collaborations...", new String[]{"Soma Pinpointing", "Synapse Validation"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Soma Pinpointing":
                                        somaPinpointing();
                                        break;
                                    case "Synapse Validation":
                                        synapseValidation();
                                        break;
                                    default:
                                        ToastEasy("Something wrong with more functions...");
                                }
                            }
                        })
                .show();
    }

    private void somaPinpointing(){
        MarkerFactoryActivity.start(CollaborationActivity.this);
    }

    private void synapseValidation() {
        BoutonDetectionActivity.start(CollaborationActivity.this);
    }

}
