package com.example.myapplication__volume;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.datastore.PreferenceLogin;
import com.example.myapplication__volume.Nim.DemoCache;
import com.example.myapplication__volume.Nim.mixpush.DemoMixPushMessageHandler;
import com.example.myapplication__volume.Nim.util.sys.SysInfoUtil;
import com.example.myapplication__volume.collaboration.ServerConnector;
import com.example.myapplication__volume.ui.login.LoginActivity;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.Map;

import static com.example.myapplication__volume.BaseActivity.ip_ALiYun;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    private boolean customSplash = false;

    private static boolean firstEnter = true; // 是否首次进入


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.e(TAG, " Before DemoCache.setMainTaskLaunching(true) ");

        DemoCache.setMainTaskLaunching(true);

        Log.e(TAG, " After DemoCache.setMainTaskLaunching(true) ");

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

        Log.e(TAG, " After if (!firstEnter) { ");

    }




    private void showSplashView() {
        // 首次进入，打开欢迎界面
//        getWindow().setBackgroundDrawableResource(R.drawable.splash_bg);
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
    protected void onResume() {
        super.onResume();

        if (firstEnter) {
            firstEnter = false;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!NimUIKit.isInitComplete()) {
                        LogUtil.i(TAG, "wait for uikit cache!");
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DemoCache.setMainTaskLaunching(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    // 处理收到的Intent
    private void onIntent() {
        LogUtil.i(TAG, "onIntent...");

        if (TextUtils.isEmpty(DemoCache.getAccount())) {
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
//                autoLogin();
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

        Log.i(TAG, "get local sdk token =" + token);
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

        showMainActivity(null);
    }

    private void showMainActivity(Intent intent) {

        Log.e(TAG,"showMainActivity");

        PreferenceLogin preferenceLogin = new PreferenceLogin(this);
        String account = preferenceLogin.getUsername();
        Toast.makeText(getApplicationContext(),"Welcome, " + account +" !",Toast.LENGTH_SHORT).show();

        MainActivity.actionStart(this, account);
        finish();

    }


    private void autoLogin(){
        PreferenceLogin preferenceLogin = new PreferenceLogin(this);

        initServerConnector();
        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg(String.format("LOGIN:%s %s", preferenceLogin.getUsername(), preferenceLogin.getPassword()));
        String result = serverConnector.ReceiveMsg();
        Log.e(TAG,"msg: " + result);


    }


    private void initServerConnector(){

        ServerConnector serverConnector = ServerConnector.getInstance();
        ServerConnector.setContext(this);

        serverConnector.setIp(ip_ALiYun);
        serverConnector.setPort("23763");
        serverConnector.initConnection();

    }

}
