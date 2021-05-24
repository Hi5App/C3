package com.penglab.hi5.basic.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.penglab.hi5.R;
import com.penglab.hi5.core.MusicServer;
import com.penglab.hi5.core.MyActivityLifeCycleCallbacks;
import com.penglab.hi5.core.Myapplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.carbs.android.library.BuildConfig;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".txt";

    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;

    private Application mApplication;

    private MyActivityLifeCycleCallbacks mMyActivityLifeCycleCallbacks = new MyActivityLifeCycleCallbacks();

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (CrashHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CrashHandler();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象,
     * 获取系统默认的UncaughtException处理器,可以将部分操作交给默认处理器处理
     * 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx, Myapplication application) {
        mContext = ctx;
        mApplication = application;
        application.registerActivityLifecycleCallbacks(mMyActivityLifeCycleCallbacks);

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);


    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        boolean isHandel = handleException(ex);
        if (isHandel || mDefaultHandler != null) {
            Log.d(TAG, "try to uncaughtException");

            //收集完信息后，交给系统自己处理崩溃
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
//            try {
//                Thread.sleep(2800);
//            } catch (InterruptedException e) {
//                Log.e(TAG, "uncaughtException() InterruptedException:" + e);
//            }
            Log.d(TAG, "try to removeAllActivities");

            mMyActivityLifeCycleCallbacks.removeAllActivities();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            System.gc();
        }
    }

    /**
     * 自定义错误处理,收集错误信息
     * 发送错误报告等操作均在此完成.
     * 开发者可以根据自己的情况来自定义异常处理逻辑
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            Log.w(TAG, "handleException--- ex==null");
            return false;
        }
        String msg = ex.getLocalizedMessage();
        if (msg == null) {
            return false;
        }
        //收集设备信息
        //保存错误报告文件
        saveCrashInfoToFile(ex);
        return true;
    }


    /**
     * 获取错误报告文件路径
     *
     * @param ctx
     * @return
     */
    public static String[] getCrashReportFiles(Context ctx) {
        File filesDir = new File(getCrashFilePath(ctx));
        String[] fileNames = filesDir.list();
        for (int i = 0; i < fileNames.length; i++){
            fileNames[i] = fileNames[i].substring(0, fileNames[i].indexOf("."));
        }
//        int length = fileNames.length;
//        String[] filePaths = new String[length];
//        for (int i = 0; i < length; i++) {
//            filePaths[i] = getCrashFilePath(ctx) + fileNames[i];
//        }
        return fileNames;
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private void saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        printWriter.close();
        StringBuilder sb = new StringBuilder();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String now = sdf.format(new Date());
        sb.append("TIME:").append(now);//崩溃时间
        //程序信息
        sb.append("\nAPPLICATION_ID:").append(BuildConfig.APPLICATION_ID);//软件APPLICATION_ID
        sb.append("\nVERSION_CODE:").append(BuildConfig.VERSION_CODE);//软件版本号
        sb.append("\nVERSION_NAME:").append(BuildConfig.VERSION_NAME);//VERSION_NAME
        sb.append("\nBUILD_TYPE:").append(BuildConfig.BUILD_TYPE);//是否是DEBUG版本
        //设备信息
        sb.append("\nMODEL:").append(android.os.Build.MODEL);
        sb.append("\nRELEASE:").append(Build.VERSION.RELEASE);
        sb.append("\nSDK:").append(Build.VERSION.SDK_INT);
        sb.append("\nEXCEPTION:").append(ex.getLocalizedMessage());
        sb.append("\nSTACK_TRACE:").append(result);
        try {
            FileWriter writer = new FileWriter(getCrashFilePath(mContext) + now + CRASH_REPORTER_EXTENSION);
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件夹路径
     *
     * @param context
     * @return
     */
    public static String getCrashFilePath(Context context) {
        String path = null;
        try {
            path = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + context.getResources().getString(R.string.app_name) + "/Crash/";
            File file = new File(path);
            if (!file.exists()) {
                if (!file.mkdirs()){
                    Toast.makeText(context,"Fail to create the folder !",Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
