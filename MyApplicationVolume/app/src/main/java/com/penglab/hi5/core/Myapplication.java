package com.penglab.hi5.core;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hms.support.common.ActivityMgr;
import com.lazy.library.BuildConfig;
import com.lazy.library.logging.Builder;
import com.lazy.library.logging.Logcat;
import com.lazy.library.logging.extend.JLog;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.business.contact.core.query.PinYin;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.utils.CrashHandler;
import com.penglab.hi5.basic.utils.ToastUtil;
import com.penglab.hi5.chat.ChatActivity;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.chat.nim.NIMInitManager;
import com.penglab.hi5.chat.nim.NimSDKOptionConfig;
import com.penglab.hi5.chat.nim.contact.ContactHelper;
import com.penglab.hi5.chat.nim.event.DemoOnlineStateContentProvider;
import com.penglab.hi5.chat.nim.mixpush.DemoPushContentProvider;
import com.penglab.hi5.chat.nim.preference.UserPreferences;
import com.penglab.hi5.chat.nim.session.NimDemoLocationProvider;
import com.penglab.hi5.chat.nim.session.SessionHelper;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.basic.ImageInfo;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.core.collaboration.connector.ServerConnector;
import com.penglab.hi5.core.game.RewardLitePalConnector;
import com.penglab.hi5.core.game.Score;
import com.penglab.hi5.core.music.MusicHelper;
import com.penglab.hi5.data.dataStore.PreferenceLogin;
import com.penglab.hi5.data.dataStore.PreferenceMusic;
import com.penglab.hi5.data.dataStore.PreferenceSetting;
import com.penglab.hi5.data.dataStore.PreferenceSoma;

import org.litepal.LitePal;
import java.io.IOException;

import io.agora.rtc.internal.RtcEngineMessage;

public class Myapplication extends Application {

    /**
     *    1. init user database: score info & image info
     *    2. init IM module
     *    3. init collaboration module
     *    4. init AV module agora
     */
    private static Myapplication mInstance;

    private static Context context;

    private static InfoCache infoCache;

//    private static AgoraMsgManager agoraMsgManager;

    private static ServerConnector serverConnector;
    private static MsgConnector msgConnector;
    private static Communicator communicator;
    private static MusicHelper musicHelper;

    public static final Object lockForMsgSocket = new Object();
    public static final Object lockForManageSocket = new Object();

    private final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();

        // store crash info
        CrashHandler.getInstance().init(getApplicationContext(), this);

        // store user info
        PreferenceLogin.init(getApplicationContext());
        PreferenceSetting.init(getApplicationContext());
        PreferenceMusic.init(getApplicationContext());
        PreferenceSoma.init(getApplicationContext());

        LitePal.initialize(this);
        SQLiteDatabase db = LitePal.getDatabase();
        Score.init(this);
        RewardLitePalConnector.init(this);
        ImageInfo.init(this);

        Log.e(TAG, String.format("Database version: %d", db.getVersion()));

        // music module
        MusicHelper.init(getApplicationContext());
        musicHelper = MusicHelper.getInstance();

//        AgoraClient.init(this, getLoginInfo());
//        agoraMsgManager = AgoraMsgManager.getInstance();

        // for collaboration
        ServerConnector.init(this);
        MsgConnector.init(this);
        Communicator.init(this);
        serverConnector = ServerConnector.getInstance();
        msgConnector = MsgConnector.getInstance();
        communicator = Communicator.getInstance();

        // user cache info
        InfoCache.setContext(this);
        infoCache = InfoCache.getInstance();

        // init log module
        initLogcat();

        // IM 网易云信
        // 4.6.0 开始，第三方推送配置入口改为 SDKOption#mixPushConfig，旧版配置方式依旧支持。
        NIMClient.init(this, getLoginInfo(), NimSDKOptionConfig.getSDKOptions(this));

        // 使用 `NIMUtil` 类可以进行主进程判断。
        if (NIMUtil.isMainProcess(this)) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用
            Log.e(TAG,"NIMUtil.isMainProcess(this)");

            ActivityMgr.INST.init(this);

            // init pinyin
            PinYin.init(this);
            PinYin.validate();

            initUIKit();

            // 初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

            // 包含一些通知栏提醒的设置
            // 云信sdk相关业务初始化
            NIMInitManager.getInstance().init(true);
        }
    }


    // load user info
    private LoginInfo getLoginInfo() {
        PreferenceLogin preferenceLogin = PreferenceLogin.getInstance();
        String account = preferenceLogin.getUsername();
        String password = preferenceLogin.getPassword();

        if (preferenceLogin.getRem_or_not() && !account.equals("") && !password.equals("")) {
            Log.e(TAG,"account: " + account + ";  password: " + password);
            InfoCache.setAccount(account.toLowerCase());
            InfoCache.setToken(password);
            return new LoginInfo(account, password);
        } else {
            return null;
        }
    }


    // build log to local file
    private void initLogcat(){
        Builder builder = Logcat.newBuilder();
        //设置Log 保存的文件夹
        builder.logSavePath(getExternalFilesDir(null) + "/Logs/");
        //设置输出日志等级
        if (BuildConfig.DEBUG) {
            builder.logCatLogLevel(Logcat.SHOW_ALL_LOG);
            //设置输出文件日志等级
            builder.fileLogLevel(Logcat.SHOW_ALL_LOG);
        } else {
//            builder.logCatLogLevel(Logcat.SHOW_INFO_LOG | Logcat.SHOW_WARN_LOG | Logcat.SHOW_ERROR_LOG);
            builder.logCatLogLevel(Logcat.SHOW_ERROR_LOG);
            //设置输出文件日志等级
            builder.fileLogLevel(Logcat.SHOW_INFO_LOG | Logcat.SHOW_WARN_LOG | Logcat.SHOW_ERROR_LOG);
        }
        //不显示日志
        //builder.fileLogLevel(Logcat.NOT_SHOW_LOG);

//        builder.topLevelTag(TAG_TOP_1);
        //删除过了几天无用日志条目
        builder.deleteUnusedLogEntriesAfterDays(7);
        //输出到Java控制台服务端
        if (NIMUtil.isMainProcess(this)) {
            builder.dispatchLog(new JLog("192.168.3.11", 5036));
        }
        //是否自动保存日志到文件中
        builder.autoSaveLogToFile(true);
        //是否显示打印日志调用堆栈信息
        builder.showStackTraceInfo(true);
        //是否显示文件日志的时间
        builder.showFileTimeInfo(true);
        //是否显示文件日志的进程以及Linux线程
        builder.showFilePidInfo(true);
        //是否显示文件日志级别
        builder.showFileLogLevel(true);
        //是否显示文件日志标签
        builder.showFileLogTag(true);
        //是否显示文件日志调用堆栈信息
        builder.showFileStackTraceInfo(true);
        //添加该标签,日志将被写入文件
        builder.addTagToFile("onRecMessage");
//        builder.addTagToFile("sender");
//        builder.addTagToFile(TAG_APP_EVENT);
        Logcat.initialize(this, builder.build());

    }


    private void initUIKit() {
        // 初始化
        NimUIKit.init(this, buildUIKitOptions());

        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
        NimUIKit.setLocationProvider(new NimDemoLocationProvider());

        // IM 会话窗口的定制初始化。
        SessionHelper.init();
//
//        // 聊天室聊天窗口的定制初始化。
//        ChatRoomSessionHelper.init();
//
        // 通讯录列表定制初始化
        ContactHelper.init();

        // 添加自定义推送文案以及选项，请开发者在各端（Android、IOS、PC、Web）消息发送时保持一致，以免出现通知不一致的情况
        NimUIKit.setCustomPushContentProvider(new DemoPushContentProvider());

        NimUIKit.setOnlineStateContentProvider(new DemoOnlineStateContentProvider());
    }


    private UIKitOptions buildUIKitOptions() {
        UIKitOptions options = new UIKitOptions();
        // 设置app图片/音频/日志等缓存目录
        options.appCacheDir = NimSDKOptionConfig.getAppCacheDir(this) + "/nim";
        return options;
    }


    public static Context getContext() {
        return context;
    }


    /**
     * toast info in the thread
     * @param message the message you wanna toast
     */
    public static void ToastEasy(String message){

        ToastUtil.getInstance()
                .createBuilder(context)
                .setMessage(message)
                .show();
    }

    public static void ToastEasy(int stringId){

        ToastUtil.getInstance()
                .createBuilder(context)
                .setMessage(context.getString(stringId))
                .show();
    }

    public static void ToastEasy(String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    public static void playButtonSound() {
        musicHelper.playButtonSound();
    }

    public static void playRewardSound(int level) {
        musicHelper.playRewardSound(level);
    }

    public static void playMusicReward(int level) {
        musicHelper.playMusicReward(level);

    }
    public static void stopMusicRewardPlay() {
        musicHelper.stopMusicRewardPlay();
    }

    public static void playCurveActionSound() {
        musicHelper.playActionSound(MusicHelper.ActionType.CURVE);
    }

    public static void playMarkerActionSound() {
        musicHelper.playActionSound(MusicHelper.ActionType.MARKER);
    }

    public static void playRightAnswerSound() {
        musicHelper.playRightAnswerSound();
    }

    public static void playWrongAnswerSound() {
        musicHelper.playWrongAnswerSound();
    }

    public static void playFailSound(){
        musicHelper.playFailSound();
    }

    public static void updateMusicVolume(){
        musicHelper.updateVolume();
    }

}
