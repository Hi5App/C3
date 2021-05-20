package com.main.core;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.main.basic.CrashHandler;
import com.main.basic.ToastUtil;



//用于在app全局获取context

public class MyApplication extends Application {

    /**
     *    1. init crash info handler
     */
    private static Context context;


    private final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //store crash info
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

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

    /**
     * toast info in the thread
     * @param message the message you wanna toast
     */
    public static void ToastEasy(String message, int Length){
        Toast.makeText(context, message, Length);
    }

}
