package com.example.myapplication__volume;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.basic.CrashHandler;
import com.example.chat.ChatActivity;
import com.example.datastore.PreferenceLogin;
import com.example.myapplication__volume.Nim.InfoCache;
import com.example.myapplication__volume.Nim.NIMInitManager;
import com.example.myapplication__volume.Nim.NimSDKOptionConfig;
import com.example.myapplication__volume.Nim.contact.ContactHelper;
import com.example.myapplication__volume.Nim.event.DemoOnlineStateContentProvider;
import com.example.myapplication__volume.Nim.mixpush.DemoPushContentProvider;
import com.example.myapplication__volume.Nim.preference.UserPreferences;
import com.example.myapplication__volume.Nim.session.NimDemoLocationProvider;
import com.example.myapplication__volume.Nim.session.SessionHelper;
import com.example.chat.agora.AgoraClient;
import com.example.myapplication__volume.collaboration.Communicator;
import com.example.myapplication__volume.collaboration.MsgConnector;
import com.example.myapplication__volume.collaboration.ServerConnector;
import com.example.myapplication__volume.collaboration.basic.ImageInfo;
import com.example.myapplication__volume.game.DailyQuestLitePalConnector;
import com.example.myapplication__volume.game.DailyQuestsContainer;
import com.example.myapplication__volume.game.RewardLitePalConnector;
import com.example.myapplication__volume.game.Score;
import com.example.myapplication__volume.game.ScoreLitePalConnector;
import com.huawei.hms.support.common.ActivityMgr;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.business.contact.core.query.PinYin;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

import org.litepal.LitePal;


//用于在app全局获取context

public class Myapplication extends Application {

    /**
     *    1. init user database: score info & image info
     *    2. init IM module
     *    3. init collaboration module
     *    4. init AV module agora
     */
    private static Myapplication sInstance;
    private static Context context;

    public static Myapplication the() {
        return sInstance;
    }

    private final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        context = getApplicationContext();

        //store crash infozyh
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        // store user info
        LitePal.initialize(this);
        SQLiteDatabase db = LitePal.getDatabase();
        DailyQuestsContainer.init(this);
        DailyQuestLitePalConnector.init(this);

        ScoreLitePalConnector.init(this);
        Score.init(this);
        RewardLitePalConnector.init(this);
        ImageInfo.init(this);


        Log.e(TAG, String.format("Database version: %d", db.getVersion()));
        AgoraClient.init(this, getLoginInfo());


        // for collaboration
        ServerConnector.init(this);
        MsgConnector.init(this);
        Communicator.init(this);

        // user cache info
        InfoCache.setContext(this);

        // IM 网易云信
        // 4.6.0 开始，第三方推送配置入口改为 SDKOption#mixPushConfig，旧版配置方式依旧支持。
        NIMClient.init(this, getLoginInfo(), NimSDKOptionConfig.getSDKOptions(this));

        // 使用 `NIMUtil` 类可以进行主进程判断。
        if (NIMUtil.isMainProcess(this)) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用

            ActivityMgr.INST.init(this);

            // init pinyin
            PinYin.init(this);
            PinYin.validate();

            Log.e(TAG,"NIMUtil.isMainProcess(this)");
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
        PreferenceLogin preferenceLogin = new PreferenceLogin(this);
        String account = preferenceLogin.getUsername();
        String token = preferenceLogin.getPassword();

        if (preferenceLogin.getRem_or_not() && !account.equals("") && !token.equals("")) {
            Log.e(TAG,"account: " + account);
            Log.e(TAG,"token: "   + token);

            InfoCache.setAccount(account.toLowerCase());
            InfoCache.setToken(token);
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }





    private SDKOptions options(){

//        SDKOptions options = new SDKOptions();
//        // 配置是否需要预下载附件缩略图，默认为 true
//        options.preloadAttach = true;
//        options.appKey = "0fda06baee636802cb441b62e6f65549";
//        return options;

        SDKOptions options = new SDKOptions();

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;
        options.appKey = "614728a863de5e1ae3ed42137d0911f6";

        // 配置由sdk托管来接收新的消息通知
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = ChatActivity.class;
        config.notificationSmallIconId = R.mipmap.ic_launcher;
        options.statusBarNotificationConfig = config;

        // 配置文件存储路径
        String sdkPath = getExternalFilesDir(null).toString() + "/nim";
        options.sdkStorageRootPath = sdkPath;
        options.thumbnailSize = 480 / 2;

        return options;

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

}
