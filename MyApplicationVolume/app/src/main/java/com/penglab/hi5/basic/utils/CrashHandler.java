package com.penglab.hi5.basic.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.text.PrecomputedText;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import com.huawei.hms.push.utils.DateUtil;

import com.netease.nim.uikit.common.util.file.FileUtil;
import com.penglab.hi5.R;
import com.penglab.hi5.core.MyActivityLifeCycleCallbacks;
import com.penglab.hi5.core.Myapplication;

import org.apache.commons.io.IOExceptionWithCause;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import java.io.FilenameFilter;
import cn.carbs.android.library.BuildConfig;
import io.agora.rtm.jni.LOGIN_ERR_CODE;

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

    private final MyActivityLifeCycleCallbacks mMyActivityLifeCycleCallbacks = new MyActivityLifeCycleCallbacks();

    private final Map<String, String> infos = new HashMap();

    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;

    private Application mApplication;

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

    public class ThreadCollector {
        @NonNull
        public String collect(@Nullable Thread thread) {
            StringBuilder result = new StringBuilder();
            if(thread != null){
                result.append("id=").append(thread.getId()).append("\n");
                result.append("name=").append(thread.getName()).append("\n");
                result.append("priority=").append(thread.getPriority()).append("\n");
                if(thread.getThreadGroup() !=null){
                    result.append("groupName=").append(thread.getThreadGroup().getName()).append("\n");
                }
            }
            return result.toString();
        }

    }

    final class DumpSysCollector {
        private static final String LOG_TAG = "DumpSysCollector";
        private static final int DEFAULT_BUFFER_SIZE_IN_BYTES = 8192;
        @NonNull
        public String collectMemInfo() {
            final StringBuilder meminfo = new StringBuilder();
            BufferedReader bufferedReader = null;
            try{
                final List<String> commandLine = new ArrayList<String>();
                commandLine.add("dumpsys");
                commandLine.add("meminfo");
                commandLine.add(Integer.toString(android.os.Process.myPid()));
                final Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()),DEFAULT_BUFFER_SIZE_IN_BYTES);
                while (true)
                {
                    final String line = bufferedReader.readLine();
                    if(line == null){
                        break;
                    }
                    meminfo.append(line);
                    meminfo.append("\n");
                }
            }catch (IOException e){
                Log.e(LOG_TAG,"DumpSysCollector.meminfo could not retrieve data", e);
            }
            try{
                if(null != bufferedReader){
                    bufferedReader.close();
                }
            }catch (IOException e){
            }
            return meminfo.toString();
        }
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

        collectDeviceInfo();
        saveCrashInfoToFile(ex);
        return true;
    }


    public void collectDeviceInfo() {
        try {
            PackageManager pm = mApplication.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mApplication.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "collectDeviceInfo() an error occured when collect package info NameNotFoundException:");
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.i(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "collectDeviceInfo() an error occured when collect crash info Exception:");
            }
        }
    }


    /**
     * Get the crash report to show
     */
    public static CrashReports getCrashReportFiles(Context ctx) {
        CrashReports crashReportList = new CrashReports();
        List<File> fileList = getFileSort(getCrashFilePath(ctx));
        String[] fileNames = new String[fileList.size()];
        if (fileList.size() != 0) {
            for (int i = 0; i < fileList.size(); i++){
                String fileName = fileList.get(i).getName();
                fileNames[i] = fileName.substring(0, fileName.indexOf("."));
            }
            crashReportList.isEmpty = false;
            crashReportList.reportNames = fileNames;
        } else {
            crashReportList.isEmpty = true;
            crashReportList.reportNames = new String[] {"There is no crash report!"};
        }
        return crashReportList;
    }

    /**
     * Save crash info into local file
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
     * Get the path of Crash Report
     */
    public static String getCrashFilePath(Context context) {
        String path = context.getExternalFilesDir(null) + "/Crash/";
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Toast.makeText(context,"Fail to create the folder !",Toast.LENGTH_SHORT).show();
            }
        }
        return path;
    }

    /**
     * Sort the file list by last modified time
     */
    public static List<File> getFileSort(String path) {
        List<File> list = getFiles(path, new ArrayList<File>());
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        return list;
    }

    /**
     *  Get the original file list in rootPath recursively
     */
    public static List<File> getFiles(String rootPath, List<File> files) {
        File realFile = new File(rootPath);
        if (realFile.isDirectory()) {
            File[] fileList = realFile.listFiles();
            for (File file : fileList) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

}
