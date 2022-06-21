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
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.penglab.hi5.R;
import com.penglab.hi5.chat.nim.main.helper.SystemMessageUnreadManager;
import com.penglab.hi5.chat.nim.reminder.ReminderManager;
import com.penglab.hi5.chat.nim.session.extension.InviteAttachment;
import com.penglab.hi5.core.BaseActivity;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.basic.ReceiveMsgInterface;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.core.collaboration.connector.ServerConnector;
import com.penglab.hi5.core.collaboration.service.BasicService;
import com.penglab.hi5.core.collaboration.service.CollaborationService;
import com.penglab.hi5.core.collaboration.service.ManageService;
import com.penglab.hi5.core.game.LeaderBoardContainer;
import com.penglab.hi5.core.game.LeaderBoardItem;
import com.penglab.hi5.core.game.Score;
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.annotation.AnnotationViewModel;
import com.penglab.hi5.data.dataStore.SettingFileManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jackiexing on 05/17/21
 */
public class CollaborationActivity extends BaseActivity implements ReceiveMsgInterface {

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
    private static final int HANDLER_UPDATE_SCORE_TEXT = 7;
    private static final int HANDLER_SHOW_SYNCING_POPUPVIEW = 9;
    private static final int HANDLER_HIDE_SYNCING_POPUPVIEW = 10;
    private static final int HANDLER_DISPLAY_ANALYZE_RESULT = 11;

    public static String username;




    @Override
    public void onRecMessage(String msg) {
        if (msg.startsWith("TestSocketConnection")){
            //ServerConnector.getInstance().sendMsg("HeartBeat");
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
                /*a
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
        for collaboration -------------------------------------------------------------------
         */

        if (msg.startsWith("/drawline_norm:")){
            Log.e(TAG,"drawline_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String seg      = msg.split(":")[1];

            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                annotationRender.syncAddSegSWC(communicator.syncSWC(seg));
                annotationGLSurfaceView.requestRender();
            }

        }


        if (msg.startsWith("/delline_norm:")){
            Log.e(TAG,"delline_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String seg      = msg.split(":")[1];

            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                annotationRender.syncDelSegSWC(communicator.syncSWC(seg));
                annotationGLSurfaceView.requestRender();
            }

        }

        if (msg.startsWith("/addmarker_norm:")){
            Log.e(TAG,"addmarker_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String marker      = msg.split(":")[1].split(";")[1];

            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                annotationRender.syncAddMarker(communicator.syncMarker(marker));
                annotationGLSurfaceView.requestRender();
            }
        }



        if (msg.startsWith("/delmarker_norm:")){
            Log.e(TAG,"delmarker_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String marker      = msg.split(":")[1].split(";")[1];

            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                annotationRender.syncDelMarker(communicator.syncMarker(marker));
                annotationGLSurfaceView.requestRender();
            }
        }



        if (msg.startsWith("/retypeline_norm:")){
            Log.e(TAG,"retypeline_norm");

            String userID = msg.split(":")[1].split(";")[0].split(" ")[0];
            String seg    = msg.split(":")[1];

            if (!userID.equals(username)){
                Communicator communicator = Communicator.getInstance();
                annotationRender.syncRetypeSegSWC(communicator.syncSWC(seg));
                annotationGLSurfaceView.requestRender();
            }

        }
    }

    public static void Toast_in_Thread_static(String message){
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
    private static Handler puiHandler = new Handler(){
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
                    setButtonsBigData();
                    showButtonsOnFile();
                    if (isBigData_Local){
                        String filename = SettingFileManager.getFilename_Local(context);
                        String offset = SettingFileManager.getoffset_Local(context, filename);

                        String offset_x = offset.split("_")[0];
                        String offset_y = offset.split("_")[1];
                        String offset_z = offset.split("_")[2];
                        Toast.makeText(getContext(),"Current offset: " + "x: " + offset_x + " y: " + offset_y + " z: " + offset_z, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case HANDLER_NETWORK_TIME_OUT:
                    Toast.makeText(context,"Time out, please try again!",Toast.LENGTH_SHORT).show();
                    break;

                case HANDLER_SET_FILENAME_BIGDATA:
                    setFileName(Communicator.BrainNum);
                    break;

                case HANDLER_TOAST_INFO_STATIC:
                    String Toast_msg = msg.getData().getString("Toast_msg");
                    Toast.makeText(getContext(),Toast_msg, Toast.LENGTH_SHORT).show();
                    break;

                case HANDLER_SHOW_PROGRESSBAR:
                    progressBar.setVisibility(View.GONE);
                    break;

                case HANDLER_UPDATE_SCORE_TEXT:
                    updateScoreTextHandler();
                    break;

                case HANDLER_SHOW_SYNCING_POPUPVIEW:
                    syncingPopupView.show();
                    break;

                case HANDLER_HIDE_SYNCING_POPUPVIEW:
                    syncingPopupView.dismiss();
                    break;

                case HANDLER_DISPLAY_ANALYZE_RESULT:
                    List<double[]> features = (List<double[]>) msg.obj;
                    if (features.size() != 0) displayResult(features);
                    else ToastEasy("The file is empty");
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

        Toolbar toolbar = findViewById(R.id.toolbar_collaboration);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        collaborationViewModel = new ViewModelProvider(this,new ViewModelFactory()).get(CollaborationViewModel.class);





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
    }


    private void LoadFiles(String FileList){
        List<String> list_array = new ArrayList<>();
        String[] list = FileList.split(";;");

        boolean isFile = false;
        String[] fileName = new String[1];

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
            list_array.add(0, "Create A New Room");
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
                                    conPath = conPath + "/" + text;
                                    ServerConnector.getInstance().sendMsg("GETFILELIST:" + conPath);
                                }
                            })
                    .show();
        }
    }
    private void loadFileMode(String filepath){
        String[] list = filepath.split("/");
        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg("LOADFILES:2 " + filepath);
        serverConnector.setRoomName(list[list.length - 1]);

        Communicator.getInstance().setPath(filepath);
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

    private void initService(){
        // Bind to ManageService
        Intent intent = new Intent(this, ManageService.class);
        bindService(intent, connection_management, Context.BIND_AUTO_CREATE);
    }

    private void initMsgService(){
        // Bind to CollaborationService
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

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection_management = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BasicService.LocalBinder binder = (BasicService.LocalBinder) service;
//            ManageService manageService = (ManageService) binder.getService();
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
//            CollaborationService collaborationService = (CollaborationService) binder.getService();
            binder.addReceiveMsgInterface((MainActivity) getActivityFromContext(mainContext));
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

                            invitePopup(mainContext, invitor, path, soma);
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


    private static void acceptInvitation(String path, String soma){

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
//                                    showFriendsList(userList);
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










}
