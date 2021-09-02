package com.penglab.hi5.core;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.penglab.hi5.R;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.chat.nim.mixpush.DemoMixPushMessageHandler;
import com.penglab.hi5.chat.nim.util.sys.SysInfoUtil;
import com.penglab.hi5.core.collaboration.basic.ReceiveMsgInterface;
import com.penglab.hi5.core.collaboration.connector.ServerConnector;
import com.penglab.hi5.core.collaboration.service.BasicService;
import com.penglab.hi5.core.collaboration.service.ManageService;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.dataStore.PreferenceLogin;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

public class SplashScreenActivity extends BaseActivity implements ReceiveMsgInterface {

    private static final String TAG = "SplashScreenActivity";

    private boolean customSplash = false;

    private static boolean firstEnter = true; // 是否首次进入

    private ManageService manageService;

    private boolean mBound;

    private static Context splashContext;

    private int musicTotalNum = 3;
    private int musicAlreadyNum = 0;

    private static Timer timerLogin;


    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BasicService.LocalBinder binder = (BasicService.LocalBinder) service;
            manageService = (ManageService) binder.getService();
            binder.addReceiveMsgInterface((SplashScreenActivity) getActivityFromContext(splashContext));
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    ToastEasy("Failed to download");
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (musicAlreadyNum < musicTotalNum) {
                                if (!NimUIKit.isInitComplete()) {
                                    Log.v(TAG, "wait for uikit cache!");
                                    new Handler().postDelayed(this, 100);
                                    return;
                                }

                                customSplash = false;
                                if (canAutoLogin()) {
                                    onIntent();
                                } else {
                                    LoginActivity.start(SplashScreenActivity.this);
                                    finish();
                                }
                            }
                        }
                    };
                    if (customSplash) {
                        new Handler().postDelayed(runnable, 1000);
                    } else {
                        runnable.run();
                    }
                    break;
                case 1:
                    showMainActivity(null);
                    break;
                case 2:
                    String message = msg.getData().getString("toastMsg");
                    ToastEasy(message, Toast.LENGTH_LONG);
                    LoginActivity.start(SplashScreenActivity.this);
                    finish();
                    break;
                case 3:
                    ToastEasy("Time out, check the network please !", Toast.LENGTH_LONG);
                    LoginActivity.start(SplashScreenActivity.this);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.e(TAG, " onCreate in SplashScreenActivity ");

        InfoCache.setMainTaskLaunching(true);
        splashContext = this;

        if (savedInstanceState != null) {
            setIntent(new Intent()); // 从堆栈恢复，不再重复解析之前的intent
        }

        Log.e(TAG, " Before if (!firstEnter) { ");

        if (!firstEnter) {
            Log.e(TAG, " !firstEnter ");
            onIntent(); // APP进程还在，Activity被重新调度起来
        } else {
            showSplashView(); // APP进程重新起来
        }

        initServerConnector();
        initService();

        Log.e(TAG, " After if (!firstEnter) } ");

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (firstEnter) {
            firstEnter = false;
            File musicDir = new File(getApplicationContext().getExternalFilesDir(null) + "/Resources/Music");
            if (!musicDir.exists() || musicDir.listFiles().length < musicTotalNum){
                ServerConnector serverConnector = ServerConnector.getInstance();
                Log.d(TAG, "onResume");
                ToastEasy("It may take a while to download", Toast.LENGTH_LONG);

                File music = new File(getApplicationContext().getExternalFilesDir(null) + "/Resources/Music/CoyKoi.mp3");
                if (!music.exists())
                    serverConnector.sendMsg("GETMUSIC:CoyKoi.mp3",false,false);
                else
                    musicAlreadyNum += 1;

                music = new File(getApplicationContext().getExternalFilesDir(null) + "/Resources/Music/DelRioBravo.mp3");
                if (!music.exists())
                    serverConnector.sendMsg("GETMUSIC:DelRioBravo.mp3",false,false);
                else
                    musicAlreadyNum += 1;

                music = new File(getApplicationContext().getExternalFilesDir(null) + "/Resources/Music/ForestFrolicLoop.mp3");
                if (!music.exists())
                    serverConnector.sendMsg("GETMUSIC:ForestFrolicLoop.mp3",false,false);
                else
                    musicAlreadyNum += 1;

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (musicAlreadyNum < musicTotalNum) {
                            ToastEasy("Failed to download");
                            if (!NimUIKit.isInitComplete()) {
                                Log.v(TAG, "wait for uikit cache!");
                                new Handler().postDelayed(this, 100);
                                return;
                            }

                            customSplash = false;
                            if (canAutoLogin()) {
                                onIntent();
                            } else {
                                LoginActivity.start(SplashScreenActivity.this);
                                finish();
                            }
                        }
                    }
                };
                if (customSplash) {
                    new Handler().postDelayed(runnable, 31000);
                } else {
                    new Handler().postDelayed(runnable, 30000);

                }
            } else {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!NimUIKit.isInitComplete()) {
                            Log.v(TAG, "wait for uikit cache!");
                            new Handler().postDelayed(this, 100);
                            return;
                        }

                        customSplash = false;
                        if (canAutoLogin()) {
                            onIntent();
                        } else {
                            LoginActivity.start(SplashScreenActivity.this);
                            finish();
                        }
                    }
                };
                if (customSplash) {
                    new Handler().postDelayed(runnable, 1000);
                } else {
                    runnable.run();
                }
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        InfoCache.setMainTaskLaunching(false);

        // avoid memory leak
        splashContext = null;
    }


    private void initService(){
        // Bind to LocalService
        Log.d(TAG, "initService");
        Intent intent = new Intent(this, ManageService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }


    private void showSplashView() {
        // 首次进入，打开欢迎界面
        getWindow().setBackgroundDrawableResource(R.drawable.splash_screen_background);
        customSplash = true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /*
         * 如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
         * 场景：点击通知栏跳转到此，会收到Intent
         */
        setIntent(intent);
        if (!customSplash) {
            onIntent();
        }
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    // 处理收到的Intent
    private void onIntent() {
        Log.v(TAG, "onIntent");
        if (TextUtils.isEmpty(InfoCache.getAccount())) {
            // 判断当前app是否正在运行
            if (!SysInfoUtil.stackResumed(this)) {
                LoginActivity.start(this);
            }
            finish();
        } else {
            // 已经登录过了，处理过来的请求
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    parseNotifyIntent(intent);
                    return;
//                } else if (NIMClient.getService(MixPushService.class).isFCMIntent(intent)) {
//                    parseFCMNotifyIntent(NIMClient.getService(MixPushService.class).parseFCMPayload(intent));
                }
            }

            if (!firstEnter && intent == null) {
                finish();
            } else {
                showMainActivity();
            }
        }
    }

    /**
     * 已经登陆过，自动登陆
     */
    private boolean canAutoLogin() {
        PreferenceLogin preferenceLogin = new PreferenceLogin(this);
        String account = preferenceLogin.getUsername();
        String token = preferenceLogin.getPassword();

        return !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
    }

    private void parseNotifyIntent(Intent intent) {
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() > 1) {
            showMainActivity(null);
        } else {
            showMainActivity(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages.get(0)));
        }
    }

    private void parseFCMNotifyIntent(String payloadString) {
        Map<String, String> payload = JSON.parseObject(payloadString, Map.class);
        String sessionId = payload.get(DemoMixPushMessageHandler.PAYLOAD_SESSION_ID);
        String type = payload.get(DemoMixPushMessageHandler.PAYLOAD_SESSION_TYPE);
        if (sessionId != null && type != null) {
            int typeValue = Integer.valueOf(type);
            IMMessage message = MessageBuilder.createEmptyMessage(sessionId, SessionTypeEnum.typeOfValue(typeValue), 0);
            showMainActivity(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, message));
        } else {
            showMainActivity(null);
        }
    }

    private void parseNormalIntent(Intent intent) {
        showMainActivity(intent);
    }

    private void showMainActivity() {
        Log.e(TAG,"showMainActivity null");
        autoLogin();
        timerLogin = new Timer();
        timerLogin.schedule(new TimerTask() {
            @Override
            public void run() {
                timeOutHandler();
            }
        },5 * 1000);
    }

    private void showMainActivity(Intent intent) {
        Log.e(TAG,"showMainActivity");
        PreferenceLogin preferenceLogin = new PreferenceLogin(SplashScreenActivity.this);
        String account = preferenceLogin.getUsername();

        InfoCache.setAccount(account);
        InfoCache.setToken(preferenceLogin.getPassword());

        MainActivity.actionStart(SplashScreenActivity.this, account);
        ToastEasy("Welcome, " + account +" !");
        finish();
    }

    private void timeOutHandler(){
        handler.sendEmptyMessage(3);
    }


    private void autoLogin(){
        PreferenceLogin preferenceLogin = new PreferenceLogin(this);
        ServerConnector serverConnector = ServerConnector.getInstance();

        if (serverConnector.checkConnection()){
            if ((preferenceLogin.getUsername() != null) && (preferenceLogin.getPassword() != null) &&
                    !preferenceLogin.getUsername().equals("") && !preferenceLogin.getPassword().equals("")){
                serverConnector.sendMsg(String.format("LOGIN:%s %s", preferenceLogin.getUsername(), preferenceLogin.getPassword()),
                        false,false);
            }else {
                Log.e(TAG,"user info is empty !");
                ToastEasy("user info is empty !");
            }
        }
    }


    private void initServerConnector(){

        Log.e(TAG, "initServerConnector");
        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.setIp(ip_TencentCloud);
        serverConnector.setPort(port_TencentCloud);
        Log.e(TAG, "serverConnector.initConnection()");
        serverConnector.initConnection();

    }

    @Override
    public void onRecMessage(String msg) {
        Log.d(TAG, "onRecMessage: " + msg);

        if (msg.startsWith("MUSICLIST:")){
            String [] list = msg.split(":")[1].split(";");
            if (list.length > 0){

                musicAlreadyNum = 0;
                ServerConnector serverConnector = ServerConnector.getInstance();
            }

        }

        if (msg.startsWith("File:")){
            musicAlreadyNum += 1;
//            if (musicAlreadyNum >= musicTotalNum){
                Log.e(TAG, "Download finished");


//                LoginActivity.start(SplashScreenActivity.this);

//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        LoginActivity.start(SplashScreenActivity.this);
//                    }
//                };
//                runnable.run();
                if (musicAlreadyNum >= musicTotalNum) {
                    ToastEasy("Download finished");
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
//                        ToastEasy("runnable.run()1");
                            if (!NimUIKit.isInitComplete()) {
//                            ToastEasy("runnable.run()2");
                                Log.v(TAG, "wait for uikit cache!");
                                new Handler().postDelayed(this, 100);
                                return;
                            }
//                        ToastEasy("runnable.run()3");

                            customSplash = false;
                            if (canAutoLogin()) {
//                            ToastEasy("runnable.run()4");

                                onIntent();
                            } else {
//                            ToastEasy("runnable.run()5");

                                LoginActivity.start(SplashScreenActivity.this);
                                finish();
                            }
                        }
                    };
                    if (customSplash) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    runnable.run();
                }
//                if (customSplash) {
//                    ToastEasy("runnable.run()6");
//
//                    new Handler().postDelayed(runnable, 1000);
//                } else {
//                    runnable.run();
//                }

//            }
        }


        if (msg.startsWith("LOGIN")){
            /* for login */
            timerLogin.cancel();

            if (msg.startsWith("LOGIN:0")){
                Log.e(TAG,"autoLogin Successfully !");
                handler.sendEmptyMessage(1);
            }else{
                Message message = new Message();
                message.what = 2;
                Bundle bundle = new Bundle();
                message.setData(bundle);

                if (msg.startsWith("LOGIN:-1")){
                    Log.e(TAG, "Result.Error");
                    bundle.putString("toastMsg","Something wrong with database !");
                }else if (msg.startsWith("LOGIN:-2")){
                    bundle.putString("toastMsg","Can not find user !");
                }else if (msg.startsWith("LOGIN:-3")){
                    bundle.putString("toastMsg","username or password is wrong !");
                }else if (msg.startsWith("LOGIN:-4")){
                    bundle.putString("toastMsg","account already login in other device !");
                }else {
                    bundle.putString("toastMsg","Something else Wrong !");
                }
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }

    }
}
