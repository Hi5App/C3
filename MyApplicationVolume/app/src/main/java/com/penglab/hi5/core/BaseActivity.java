package com.penglab.hi5.core;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.penglab.hi5.basic.utils.AppStatus;
import com.penglab.hi5.basic.utils.AppStatusManager;
import com.penglab.hi5.core.ui.splash.SplashScreenActivity;
import com.penglab.hi5.data.dataStore.DataManager;
import com.penglab.hi5.data.dataStore.PreferenceSetting;
import com.penglab.hi5.data.dataStore.S2paraSetting;

import java.io.File;

public class BaseActivity extends AppCompatActivity {

    protected DataManager dataManager;
    protected PreferenceSetting preferenceSetting;
    protected S2paraSetting s2paraSetting;
    protected static Context context;

    public static final String ip_SEU = "223.3.33.234";
    public static final String ip_ALiYun = "39.100.35.131";
    public static final String ip_TencentCloud = "114.117.165.134";
//    public static final String ip_TencentCloud = "192.168.1.114";
    public static final String port_TencentCloud = "23763";
//    public static final String ip_TencentCloud = "139.155.28.154";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("BaseActivity","onCreate()");

        dataManager = new DataManager(this);
        preferenceSetting = PreferenceSetting.getInstance();
        s2paraSetting = new S2paraSetting(this);
        context = getApplicationContext();
        ActivityCollector.addActivity(this);

        if (AppStatusManager.getInstance().getAppStatus() == AppStatus.STATUS_RECYCLE){
            // app 在后台被杀掉之后重启会打开 SplashScreenActivity
            SplashScreenActivity.start(this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("BaseActivity","onDestroy()");
        ActivityCollector.removeActivity(this);
    }


    public void Toast_in_Thread(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static Activity getActivityFromView(View view) {
        if (null != view) {
            Context context = view.getContext();
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }


    public static Activity getActivityFromContext(Context context) {
        if (context != null) {
            while (context instanceof ContextWrapper) {
                if (context instanceof Activity) {
                    return (Activity) context;
                }
                context = ((ContextWrapper) context).getBaseContext();
            }
        }
        return null;
    }


    public static void recursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            Log.v("RecursionDeleteFile","file.isDirectory()");
            File[] childFile = file.listFiles();
            Log.v("RecursionDeleteFile","childFile.length: " + childFile.length);
            if (childFile.length == 0) {
                file.delete();
                return;
            }
            int count = 1;
            for (File f : childFile) {
                Log.v("RecursionDeleteFile","count: " + count++);
                recursionDeleteFile(f);
            }
            file.delete();
        }
    }

}
