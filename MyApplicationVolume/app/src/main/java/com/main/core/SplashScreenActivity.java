package com.main.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;


public class SplashScreenActivity extends BaseActivity {

    private static final String TAG = "SplashScreenActivity";

    private boolean customSplash = false;

    private static boolean firstEnter = true; // 是否首次进入

    private static Context splashContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.e(TAG, " Before DemoCache.setMainTaskLaunching(true) ");

        splashContext = this;

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
                        customSplash = false;
                        onIntent();
                }
            };
            if (customSplash) {
                new Handler().postDelayed(runnable, 3 * 1000);
            } else {
                new Handler().postDelayed(runnable, 3 * 1000);

            }
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    customSplash = false;
                        onIntent();
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

        // avoid memory leak
        splashContext = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    // 处理收到的Intent
    private void onIntent() {
        // 已经登录过了，处理过来的请求
        Intent intent = getIntent();

        if (!firstEnter && intent == null) {
            finish();
        } else {
            showMainActivity();
        }

    }

    private void showMainActivity() {
        Log.e(TAG,"showMainActivity null");
        showMainActivity(null);
    }

    private void showMainActivity(Intent intent) {
        Log.e(TAG,"showMainActivity");
        MainActivity.actionStart(this);
        finish();
    }


}
