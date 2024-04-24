package com.penglab.hi5.core.ui.splash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.netease.nim.uikit.api.NimUIKit;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.utils.AppStatus;
import com.penglab.hi5.basic.utils.AppStatusManager;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.collaboration.CollaborationActivity;
import com.penglab.hi5.core.ui.home.screens.HomeActivity;
import com.penglab.hi5.core.ui.login.LoggedInUserView;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.dataStore.PreferenceLogin;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";

    private SplashScreenViewModel splashScreenViewModel;
    private PreferenceLogin preferenceLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        InfoCache.setMainTaskLaunching(true);
        preferenceLogin = PreferenceLogin.getInstance();
        splashScreenViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(SplashScreenViewModel.class);

        splashScreenViewModel.getResourceDataSource().getResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                splashScreenViewModel.updateMusicResult(result);
            }
        });

        splashScreenViewModel.getUserDataSource().getResult().observe(this, new Observer<Result>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                splashScreenViewModel.updateLoginResult(result);
            }
        });

        splashScreenViewModel.getMusicResult().observe(this, new Observer<ResourceResult>() {
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult == null) {
                    return;
                }

                if (!resourceResult.isSuccess()) {
                    showFailed(resourceResult.getError());
                }
                postLogin();
            }
        });

        splashScreenViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    showFailed(loginResult.getError());
                    LoginActivity.start(SplashScreenActivity.this);
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    showCollaborateActivity();
//                    showHomeActivity();
                }
                setResult(Activity.RESULT_OK);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        splashScreenViewModel.getMusicList();
        AppStatusManager.getInstance().setAppStatus(AppStatus.STATUS_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InfoCache.setMainTaskLaunching(false);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SplashScreenActivity.class);
        context.startActivity(intent);
    }

    private void postLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!NimUIKit.isInitComplete()) {
                    Log.e(TAG, "Waiting for uikit cache!");
                    new Handler().postDelayed(this, 100);
                    return;
                }

//                if (canAutoLogin()) {
////                    Log.e(TAG,"Temperaly");
////                    autoLogin();
//                } else {
                LoginActivity.start(SplashScreenActivity.this);
//                }
            }
        }, 1500);
    }

//    private boolean canAutoLogin() {
//        return PreferenceLogin.getInstance().getAutoLogin();
//    }

    private void autoLogin() {
        if (!TextUtils.isEmpty(preferenceLogin.getUsername()) && !TextUtils.isEmpty(preferenceLogin.getPassword())) {
            splashScreenViewModel.login(preferenceLogin.getUsername(), preferenceLogin.getPassword());
        } else {
            LoginActivity.start(SplashScreenActivity.this);
            finish();
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = "Welcome entering in CAR_Mobile !";
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showFailed(@Nullable String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }

    private void showHomeActivity(){
        HomeActivity.start(SplashScreenActivity.this);
        finish();
    }

    private void showCollaborateActivity() {
        CollaborationActivity.start(SplashScreenActivity.this);
        finish();
    }

}

