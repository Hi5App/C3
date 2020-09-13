package com.example.myapplication__volume;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datastore.DataManager;
import com.example.datastore.PreferenceSetting;

import java.io.File;

public class BaseActivity extends AppCompatActivity {

    protected DataManager dataManager;
    protected PreferenceSetting preferenceSetting;
    protected static Context context;

    public static final String ip_SEU = "223.3.33.234";
    public static final String ip_ALiYun = "39.100.35.131";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager = new DataManager(this);
        preferenceSetting = new PreferenceSetting(this);
        context = this;
        Log.v("BaseActivity","onCreate()");
        ActivityCollector.addActivity(this);
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


    public static void deleteImg(){
        Log.v("BaseActivity","deleteImg()");
        String img_path = context.getExternalFilesDir(null).toString() + "/Img";
        Log.v("BaseActivity","img_path" + img_path);

        File file = new File(img_path);
        RecursionDeleteFile(file);
    }

    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            Log.v("RecursionDeleteFile","file.isDirectory()");
            File[] childFile = file.listFiles();
            Log.v("RecursionDeleteFile","childFile.length: " + childFile.length);
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            int count = 1;
            for (File f : childFile) {
                Log.v("RecursionDeleteFile","count: " + count++);
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

}