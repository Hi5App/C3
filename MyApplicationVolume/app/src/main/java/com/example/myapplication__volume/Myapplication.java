package com.example.myapplication__volume;

import android.app.Application;
import android.content.Context;

import com.example.basic.CrashHandler;
import com.example.chat.ChatManager;


//用于在app全局获取context

public class Myapplication extends Application {
    private static Myapplication sInstance;
    private static Context context;
    private ChatManager mChatManager;

    public static Myapplication the() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        context = getApplicationContext();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        mChatManager = new ChatManager(this);
        mChatManager.init();
    }

    public static Context getContext() {
        return context;
    }

    public ChatManager getChatManager() {
        return mChatManager;
    }
}
