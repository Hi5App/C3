package com.example.myapplication__volume;

import android.app.Application;
import android.content.Context;

import com.example.basic.CrashHandler;


//用于在app全局获取context

public class Myapplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
    public static Context getContext() {
        return context;
    }
}
