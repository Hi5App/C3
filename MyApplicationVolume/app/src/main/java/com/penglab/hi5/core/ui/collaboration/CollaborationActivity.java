package com.penglab.hi5.core.ui.collaboration;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.michaldrabik.tapbarmenulib.TapBarMenu;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
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
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.QualityInspection.QualityInspectionViewModel;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.annotation.AnnotationViewModel;
import com.penglab.hi5.core.ui.annotation.EditMode;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.model.img.CollaborateNeuronInfo;
import com.penglab.hi5.data.model.img.PotentialArborMarkerInfo;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CollaborationActivity extends BaseActivity implements ReceiveMsgInterface,ColorPickerDialogListener {

    private static final String TAG = "CollaborationActivity";
    private AnnotationGLSurfaceView annotationGLSurfaceView;
    private CollaborationViewModel collaborationViewModel;
    private AnnotationRender annotationRender;
    private static String conPath = "";
    public static boolean firstLoad = true;
    private boolean firstJoinRoom = true;
    private boolean copyFile = false;
    private boolean mBoundManagement = false;
    private boolean mBoundCollaboration = false;
    private static Context mainContext;

    private static final int HANDLER_SHOW_DOWNLOADING_POPUPVIEW = 0;
    private static final int HANDLER_HIDE_DOWNLOADING_POPUPVIEW = 1;
    private static final int HANDLER_SET_BUTTONS_BIGDATA = 2;
    private static final int HANDLER_NETWORK_TIME_OUT = 3;
    private static final int HANDLER_SET_FILENAME_BIGDATA = 4;
    private static final int HANDLER_TOAST_INFO_STATIC = 5;
    private static final int HANDLER_SHOW_PROGRESSBAR = 6;
    private static final int HANDLER_SHOW_SYNCING_POPUPVIEW = 9;
    private static final int HANDLER_HIDE_SYNCING_POPUPVIEW = 10;

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
    private ImageButton editModeIndicator;
    private static BasePopupView downloadingPopupView;
    private static BasePopupView syncingPopupView;

    static Timer timerDownload;
    static Timer timerSync;
    private Timer timer = null;
    private TimerTask timerTask;
    private Toolbar toolbar;

    private List<String> neuronNumberList = new ArrayList<>();

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRecMessage(String msg) {
        if (msg.startsWith("TestSocketConnection")) {
            //ServerConnector.getInstance().sendMsg("HeartBeat");
        } else {
            Log.e(TAG,"onRecMessage()  " + msg);
        }
        /*
        After msg:  "/login:xf"

        server will send user list when the users in current room are changed
         */
        if (msg.startsWith("/activeusers:")){
            String[] users = msg.split(":")[1].split(",");
            List<String> newUserList = Arrays.asList(users);
            updateUserList(newUserList);
        }

        if(msg.startsWith("File:")){
            if(msg.endsWith(".apo")){
                loadBigDataApo(msg.split(":")[1]);
            }else if (msg.endsWith(".swc") || msg.endsWith(".eswc")){
                loadBigDataSwc(msg.split(":")[1]);
            }

        }
        /*
        for collaboration -------------------------------------------------------------------
         */

        if(msg.startsWith("/drawline_norm:") || msg.startsWith("/delline_norm:") || msg.startsWith("/retypeline_norm:") ||msg.startsWith("/addmarker_norm:") || msg.startsWith("/delmarker_norm:"))
        {
            String[] singleMsg = msg.split(";");
            for(int i =0;i<singleMsg.length;i++) {
                String singlemsg = singleMsg[i];

                if (singlemsg.startsWith("/drawline_norm:")) {
                    String userID = singlemsg.split(":")[1].split(",")[0].split(" ")[1];
                    String toolType = singlemsg.split(":")[1].split(",")[0].split(" ")[0];
                    int index = singlemsg.indexOf(",");
                    String seg = singlemsg.substring(index+1);

                    if (!userID.equals(String.valueOf(id))) {
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncAddSegSWC(communicator.syncSWC(seg,toolType));
                        annotationGLSurfaceView.requestRender();
                    }
                }

                if (singlemsg.startsWith("/delline_norm:")) {
//                    Log.e(TAG,"delline_norm");

                    String userID = singlemsg.split(":")[1].split(",")[0].split(" ")[1];
                    String toolType = singlemsg.split(":")[1].split(",")[0].split(" ")[0];
                    int index = singlemsg.indexOf(",");
                    String seg = singlemsg.substring(index+1);
                    if (!userID.equals(String.valueOf(id))) {
                        Log.e(TAG,"enter delete");
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncDelSegSWC(communicator.syncSWC(seg,toolType));
//                        annotationGLSurfaceView.getAnnotationRender().syncDelSegSWC(communicator.syncSWC(seg));
                        annotationGLSurfaceView.requestRender();
                    }
                }

                if (singlemsg.startsWith("/addmarker_norm:")) {

                    String userID = singlemsg.split(":")[1].split(",")[0].split(" ")[1];

                    int index = singlemsg.indexOf(",");
                    String marker = singlemsg.substring(index+1);
//                    String marker      = singlemsg.split(":")[1].split(",")[1];

                    if (!userID.equals(String.valueOf(id))) {
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncAddMarker(communicator.syncMarker(marker));
                        annotationGLSurfaceView.requestRender();
                    }
                }

                if (singlemsg.startsWith("/delmarker_norm:")) {
//                    Log.e(TAG,"delmarker_norm");

                    String userID = singlemsg.split(":")[1].split(",")[0].split(" ")[1];
                    int index = singlemsg.indexOf(",");
                    String marker = singlemsg.substring(index+1);

                    if (!userID.equals(String.valueOf(id))){
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncDelMarker(communicator.syncMarker(marker));
                        annotationGLSurfaceView.requestRender();
                    }
                }

                if (msg.startsWith("/retypeline_norm:")) {
//                    Log.e(TAG,"retypeline_norm");

                    String userID = msg.split(":")[1].split(",")[0].split(" ")[1];
                    String toolType = singlemsg.split(":")[1].split(",")[0].split(" ")[0];
                    int index = singlemsg.indexOf(",");
                    String seg = singlemsg.substring(index+1);

                    if (!userID.equals(String.valueOf(id))) {
                        Communicator communicator = Communicator.getInstance();
                        annotationGLSurfaceView.syncRetypeSegSWC(communicator.syncSWC(seg,toolType));
                        annotationGLSurfaceView.requestRender();
                    }
                }
            }
        }
    }

    public static void Toast_in_Thread_static(String message) {
        Message msg = new Message();
        msg.what = HANDLER_TOAST_INFO_STATIC;
        Bundle bundle = new Bundle();
        bundle.putString("Toast_msg",message);
        msg.setData(bundle);
        puiHandler.sendMessage(msg);
    }

    @Override
    public void onRecBinData(String msg, byte[] a) {

    }

    @SuppressLint("HandlerLeak")
    private static Handler puiHandler = new Handler() {
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_SHOW_DOWNLOADING_POPUPVIEW:
                    downloadingPopupView.show();
                    Activity activity = getActivityFromContext(mainContext);
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;

                case HANDLER_HIDE_DOWNLOADING_POPUPVIEW:
                    downloadingPopupView.dismiss();
                    Activity activity_2 = getActivityFromContext(mainContext);
                    activity_2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    break;

                case HANDLER_SET_BUTTONS_BIGDATA:
                    break;

                case HANDLER_NETWORK_TIME_OUT:
                    Toast.makeText(context,"Time out, please try again!",Toast.LENGTH_SHORT).show();
                    break;

                case HANDLER_SET_FILENAME_BIGDATA:
                    break;

                case HANDLER_TOAST_INFO_STATIC:
                    String Toast_msg = msg.getData().getString("Toast_msg");
                    Toast.makeText(getContext(),Toast_msg, Toast.LENGTH_SHORT).show();
                    break;

                case HANDLER_SHOW_PROGRESSBAR:
                    break;

                case HANDLER_SHOW_SYNCING_POPUPVIEW:
                    syncingPopupView.show();
                    break;

                case HANDLER_HIDE_SYNCING_POPUPVIEW:
                    syncingPopupView.dismiss();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collaboration);

        annotationGLSurfaceView = findViewById(R.id.gl_surface_view);
        annotationGLSurfaceView.setBigData(true);
        toolbar = findViewById(R.id.toolbar_collaboration);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        collaborationViewModel = new ViewModelProvider(this,new ViewModelFactory()).get(CollaborationViewModel.class);

        downloadingPopupView = new XPopup.Builder(this)
                .asLoading("Downloading......");

        syncingPopupView = new XPopup.Builder(this)
                .asLoading("Syncing......");

        username =  InfoCache.getAccount();

        id = InfoCache.getId();

        mainContext = this;

        collaborationViewModel.getAnnotationMode().observe(this, new androidx.lifecycle.Observer<CollaborationViewModel.AnnotationMode>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(CollaborationViewModel.AnnotationMode annotationMode) {
                if(annotationMode == null ) {
                    return;
                }
                updateUI(annotationMode);
                updateOptionsMenu(annotationMode);
            }
        });

        collaborationViewModel.getCollorationDataSource().getBrainListCollaborate().observe(this, new androidx.lifecycle.Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if(result instanceof Result.Success){
                    String[] data = (String[]) ((Result.Success<?>) result).getData();
                    Set<String> set=new HashSet<String>(Arrays.asList(data));
                    String[] listShow = set.toArray(new String[set.size()]);

                    new XPopup.Builder(CollaborationActivity.this).
                            maxHeight(1350).
                            maxWidth(800).
                            asCenterList("Brain Number",
                                    listShow, new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {
                                            ToastEasy("Click"+text);
                                            collaborationViewModel.handleBrainNumber(text.trim());
                                        }
                                    }).show();
                } else if(result instanceof Result.Error){
                    ToastEasy(result.toString());
                }
            }
        });

        collaborationViewModel.getCollorationDataSource().getNeuronListCollaborate().observe(this, new androidx.lifecycle.Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result instanceof Result.Success) {
                    List<CollaborateNeuronInfo> potentialDownloadNeuronInfoList = (List<CollaborateNeuronInfo>) ((Result.Success<?>) result).getData();
                    for(int i = 0; i<potentialDownloadNeuronInfoList.size();i++){
                        CollaborateNeuronInfo potentialDownloadNeuronInfo = potentialDownloadNeuronInfoList.get(i);
                        neuronNumberList.add(potentialDownloadNeuronInfo.getNeuronName());
                    }
                    String[] neuronNumberListShow = neuronNumberList.toArray(new String[neuronNumberList.size()]);
                    new XPopup.Builder(CollaborationActivity.this).
                            maxHeight(1350).
                            maxWidth(800).
                            asCenterList("Neuron Number",
                                    neuronNumberListShow, new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {
                                            ToastEasy("Click"+text);
                                            collaborationViewModel.handleNeuronNumber(text.trim());
                                            collaborationViewModel.handleLoadImage(potentialDownloadNeuronInfoList.get(position));
                                        }
                                    }).show();
                } else {
                    ToastEasy(result.toString());
                }


            }
        });

        collaborationViewModel.getCollorationDataSource().getAnoListCollaborate().observe(this, new androidx.lifecycle.Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result instanceof Result.Success){
                    List<String>data = (List<String>) ((Result.Success<?>) result).getData();
                    String[] str = new String[data.size()];
                    String[] anoListShow = data.toArray(str);
                    new XPopup.Builder(CollaborationActivity.this).
                            maxHeight(1350).
                            maxWidth(800).
                            asCenterList("Ano Number",
                                    anoListShow, new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {
                                            ToastEasy("Click"+text);
                                            collaborationViewModel.handleAnoResult(text.trim());
                                        }
                                    }).show();
                } else if(result instanceof Result.Error){
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
                initMsgConnector(s);
                if (firstJoinRoom){
                    initMsgService();
                    firstJoinRoom = false;
                }else {
                    CollaborationService.resetConnection();
                }
                MsgConnector.getInstance().sendMsg("/login:" + InfoCache.getId() + " " + 2 );
            }
        });

        collaborationViewModel.getImageDataSource().getBrainListResult().observe(this, new androidx.lifecycle.Observer<Result>() {
            @Override
            public void onChanged(Result result) {
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
            }
        });

        collaborationViewModel.getImageResult().observe(this, new androidx.lifecycle.Observer<ResourceResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult == null){
                    return;
                }
                if (resourceResult.isSuccess()){
                    annotationGLSurfaceView.openFile();
                } else {
                    ToastEasy(resourceResult.getError());
                }

            }
        });
    }
    @Override
    protected void onResume() {
        Log.e(TAG,"onResume");
        super.onResume();
        annotationGLSurfaceView.onResume();
    }
    @Override
    protected void onPause() {
        Log.e(TAG,"onPause");
        super.onPause();
        annotationGLSurfaceView.onPause();
    }
    @Override
    protected void onRestart() {
        Log.e(TAG,"onRestart");
        super.onRestart();
    }
    @Override
    protected void onStop() {
        Log.e(TAG,"onStop");
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

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

        if (timerDownload != null){
            timerDownload.cancel();
            timerDownload = null;
        }

        if (timerSync != null){
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

    /** Defines callbacks for service binding, passed to bindService() */
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



    /** Defines callbacks for service binding, passed to bindService() */
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
//    private void registerSystemMessageObservers(boolean register) {
//        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(
//                sysMsgUnreadCountChangedObserver, register);
//
//        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(inviteMessageObserver, true);
//    }

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


    private static void acceptInvitation(String path, String soma) {

        String[] list = path.split("/");
        String roomName = list[list.length - 1];

        Communicator communicator = Communicator.getInstance();
        communicator.initSoma(soma);
        communicator.setPath(path);
        Communicator.BrainNum = path.split("/")[1];
        conPath = path;
        firstLoad = true;

        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg("LOADFILES:2 " + path);
        serverConnector.setRoomName(roomName);
    }

    private void showUserList() {
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
//                                    showFriendsList(userList);
                                }
                            }
                        })
                .show();
    }

    private void updateUserList(List<String> newUserList) {

        for (int i = 0; i < newUserList.size(); i++){
            if (!MsgConnector.userList.contains(newUserList.get(i)) && newUserList.get(i) != String.valueOf(id)){
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

    public void loadBigDataApo(String filepath) {

        try {
            ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
            ApoReader apoReader = new ApoReader();
            apo = apoReader.read(filepath);

            if (apo == null){
                Toast_in_Thread("There is something wrong with apo file !");
            }

            annotationGLSurfaceView.importApo(Communicator.getInstance().convertApo(apo));
            annotationGLSurfaceView.requestRender();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadBigDataSwc(String filepath) {
        try {
            NeuronTree nt = NeuronTree.readSWC_file(filepath);

            annotationGLSurfaceView.importNeuronTree(Communicator.getInstance().convertNeuronTree(nt),false);
            annotationGLSurfaceView.requestRender();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isTopActivity() {
        ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;
        if(runningTaskInfos != null) {
            cmpNameTemp = runningTaskInfos.get(0).topActivity.toString();
        }
        if(cmpNameTemp == null) {
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
        if (bigDataModeView != null){
            bigDataModeView.setVisibility(View.GONE);
        }
    }
    private void hideCommonUI() {
        if (commonView != null){
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

        } else {
            bigDataModeView.setVisibility(View.VISIBLE);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showCommonUI() {
        if (commonView == null) {
            // load layout view
            LinearLayout.LayoutParams lpCommon = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            commonView = getLayoutInflater().inflate(R.layout.annotation_common_collaborate, null);
            this.addContentView(commonView, lpCommon);

            editModeIndicator = findViewById(R.id.edit_mode_indicator_collaborate);
            tapBarMenu = findViewById(R.id.tapBarMenu_collaborate);
            addCurve = tapBarMenu.findViewById(R.id.draw_i_collaborate);
            addMarker = tapBarMenu.findViewById(R.id.pinpoint_collaborate);
            deleteCurve = tapBarMenu.findViewById(R.id.delete_curve_collaborate);
            deleteMarker = tapBarMenu.findViewById(R.id.delete_marker_collaborate);
            splitCurve = tapBarMenu.findViewById(R.id.split_curve_collaborate);

            BoomMenuButton boomMenuButton = tapBarMenu.findViewById(R.id.expanded_menu_collaborate);

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

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder().listener(index -> {
                ToastEasy("App2 tracing algorithm start !");
                executorService.submit(() ->annotationGLSurfaceView.APP2());
            }).normalImageRes(R.drawable.ic_neuron));


            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .listener(index -> annotationGLSurfaceView.setEditMode(EditMode.DELETE_MULTI_MARKER))
                    .normalImageRes(R.drawable.ic_delete_multimarker).normalText("Delete Multi Markers"));

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder().listener(index -> {
                ToastEasy("GD tracing algorithm start !");
                executorService.submit(() -> annotationGLSurfaceView.GD());
            }).normalImageRes(R.drawable.ic_gd_tracing).normalText("GD-Tracing"));

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .listener(index -> annotationGLSurfaceView.clearAllTracing())
                    .normalImageRes(R.drawable.ic_clear).normalText("Clear Tracing"));

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
                if (annotationGLSurfaceView.setEditMode(EditMode.PAINT_CURVE)){
                    addCurve.setImageResource(R.drawable.ic_draw);
                }
                break;
            case R.id.pinpoint_collaborate:
                if (annotationGLSurfaceView.setEditMode(EditMode.PINPOINT)){
                    addMarker.setImageResource(R.drawable.ic_add_marker);
                }
                break;
            case R.id.split_curve_collaborate:
                if(annotationGLSurfaceView.setEditMode(EditMode.SPLIT)){
                    splitCurve.setImageResource(R.drawable.ic_split);
                }
                break;
            case R.id.delete_curve_collaborate:
                if (annotationGLSurfaceView.setEditMode(EditMode.DELETE_CURVE)){
                    deleteCurve.setImageResource(R.drawable.ic_delete_curve);
                }
                break;
            case R.id.delete_marker_collaborate:
                if (annotationGLSurfaceView.setEditMode(EditMode.DELETE_MARKER)){
                    deleteMarker.setImageResource(R.drawable.ic_marker_delete);
                }
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    private boolean onMenuItemLongClick(View view) {
        switch (view.getId()){
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
        switch (dialogId){
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
                Log.e(TAG,"Default UI");
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
                finish();
                return true;

            case R.id.undo:
                annotationGLSurfaceView.undo();
                return true;

            case R.id.redo:
                annotationGLSurfaceView.redo();
                return true;

            case R.id.file:
                openFile();
                return true;

            case R.id.share:
                annotationGLSurfaceView.screenCapture();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    private void openFile() {
        new XPopup.Builder(this)
                .asCenterList("File Open", new String[]{"Open BigData"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect (int position, String item) {
                                switch (item) {
                                    case "Open BigData":
                                        collaborationViewModel.getImageList();
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

}
