package com.penglab.hi5.core.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.common.ToastHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.penglab.hi5.R;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.dataStore.PreferenceLogin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.penglab.hi5.core.MainActivity.ifGuestLogin;

//import com.penglab.hi5.chat.agora.AVConfig;

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";
    private static final String KICK_OUT = "KICK_OUT";
    private static final String KICK_OUT_DESC = "KICK_OUT_DESC";

    private LoginViewModel loginViewModel;

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    Button VisitorLogin;
    ProgressBar loadingProgressBar;
    Button registerButton;
    CheckBox remember_pwd;
    PreferenceLogin preferenceLogin;

    LinearLayout mLlLoginPull;
    View mLlLoginLayer;
    LinearLayout mLlLoginOptions;
    LinearLayout mLlLoginUsername;
    ImageView mIvLoginUsernameDel;
    LinearLayout mLlLoginPwd;
    ImageView mIvLoginPwdDel;
    ImageView mIvLoginLogo;
    LinearLayout mLayBackBar;


    //全局Toast
    private Toast mToast;

    private int mLogoHeight;
    private int mLogoWidth;

    private SoundPool soundPool;
    private int soundId;

    private float bgmVolume = 1.0f;
    private float buttonVolume = 1.0f;
    private float actionVolume = 1.0f;

    final private int LOGIN_IN_ON_CLICK = 1;
    private long exitTime = 0;

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOGIN_IN_ON_CLICK:
                    Log.d(TAG, "LoginButton");
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);


        /*
        set context for toast in ServerConnector
         */
        preferenceLogin = new PreferenceLogin(this);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        VisitorLogin = findViewById(R.id.visitor_login);
        loadingProgressBar = findViewById(R.id.loading);
        registerButton = findViewById(R.id.goto_register);
        remember_pwd = findViewById(R.id.remember_pwd);
//
        //登录层、下拉层、其它登录方式层
        mLlLoginLayer = findViewById(R.id.ll_login_layer);
        mLlLoginPull = findViewById(R.id.ll_login_pull);
        mLlLoginOptions = findViewById(R.id.ll_login_options);

        mLlLoginUsername = findViewById(R.id.ll_login_username);
        mIvLoginUsernameDel = findViewById(R.id.iv_login_username_del);

        mLlLoginPwd = findViewById(R.id.ll_login_pwd);
        mIvLoginPwdDel = findViewById(R.id.iv_login_pwd_del);


        //导航栏+返回按钮
        mLayBackBar = findViewById(R.id.ly_retrieve_bar);

       //logo
       mIvLoginLogo = findViewById(R.id.iv_login_logo);


        /*
        music volume
         */
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        soundId = soundPool.load(this, R.raw.button01, 1);

        File volumeFile = new File(getBaseContext().getExternalFilesDir(null).toString() + "/Settings/volume.txt");
        if (volumeFile.exists()){
            try {
                BufferedReader volumeReader = new BufferedReader(new InputStreamReader(new FileInputStream(volumeFile)));
                String volumeStr = volumeReader.readLine();
                if (volumeStr != null) {
                    String[] volumes = volumeStr.split(" ");
                    Log.d(TAG, "VolumeStr: " + volumeStr);
                    if (volumes.length == 3){
                        bgmVolume = Float.parseFloat(volumes[0]);
                        buttonVolume = Float.parseFloat(volumes[1]);
                        actionVolume = Float.parseFloat(volumes[2]);
                    }
                }
                volumeReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (preferenceLogin.getRem_or_not()){
            usernameEditText.setText(preferenceLogin.getUsername());
            passwordEditText.setText(preferenceLogin.getPassword());
            remember_pwd.setChecked(true);
//            loginButton.setEnabled(true);
        }

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
//                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });


        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                Log.d(TAG, "LoginResultOnChanged");

                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    if (remember_pwd.isChecked()){
                        preferenceLogin.setPref(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString(),true);
                    }else {
                        preferenceLogin.setPref("","",false);
                    }

                    /*
                    login IM module
                     */
                    loginNim(loginResult.getSuccess().getDisplayName(), passwordEditText.getText().toString(), loginResult);
                }
                setResult(Activity.RESULT_OK);

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                String username = usernameEditText.getText().toString().trim();
                String pwd = passwordEditText.getText(

                ).toString().trim();

                //是否显示清除按钮
                if (username.length() > 0) {
                    mIvLoginUsernameDel.setVisibility(View.VISIBLE);
                } else {
                    mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
                }
                if (pwd.length() > 0) {
                    mIvLoginPwdDel.setVisibility(View.VISIBLE);
                } else {
                    mIvLoginPwdDel.setVisibility(View.INVISIBLE);
                }

                if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
                    loginButton.setBackgroundResource(R.drawable.bg_login_submit);
                    loginButton.setTextColor(getResources().getColor(R.color.white));
                } else {
                    loginButton.setBackgroundResource(R.drawable.bg_login_submit_lock);
                    loginButton.setTextColor(getResources().getColor(R.color.character_bg));
                }


            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        usernameEditText.setOnFocusChangeListener(this::onFocusChange);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnFocusChangeListener(this::onFocusChange);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.sendEmptyMessage(LOGIN_IN_ON_CLICK);
            }
            return false;
        });

        loginButton.setOnClickListener(new MyClick());
        VisitorLogin.setOnClickListener(new MyClick());
        registerButton.setOnClickListener(new MyClick());

        mLlLoginPull.setOnClickListener(new MyClick());
        mIvLoginUsernameDel.setOnClickListener(new MyClick());
        mIvLoginPwdDel.setOnClickListener(new MyClick());
        mLayBackBar.getViewTreeObserver().addOnGlobalLayoutListener(new MyViewTreeObserver());

    }

    public class MyClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.login:
                    soundPool.play(soundId, buttonVolume, buttonVolume, 0, 0, 1.0f);
                    ifGuestLogin = false;
                    handler.sendEmptyMessage(LOGIN_IN_ON_CLICK);
                    break;
                case R.id.visitor_login:
                    soundPool.play(soundId, buttonVolume, buttonVolume, 0, 0, 1.0f);
                    ifGuestLogin = true;
                    Intent intent = new Intent (LoginActivity.this,MainActivity.class);
                    intent.putExtra("isVisit",true);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this,"you are now logged in as a visitor",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case R.id.goto_register:
                    Log.e(TAG,"start to register !");
                    RegisterActivity.actionStart(LoginActivity.this);
                    break;
                case R.id.username:
                    usernameEditText.clearFocus();
                    usernameEditText.setFocusableInTouchMode(true);
                    usernameEditText.requestFocus();
                    break;
                case R.id.password:
                    passwordEditText.clearFocus();
                    passwordEditText.setFocusableInTouchMode(true);
                    passwordEditText.requestFocus();
                    break;
                case R.id.iv_login_username_del:
                    //清空用户名
                    usernameEditText.setText(null);
                    break;
                case R.id.iv_login_pwd_del:
                    passwordEditText.setText(null);
                case R.id.ll_login_layer:
                case R.id.ll_login_pull:
                    mLlLoginPull.animate().cancel();
                    mLlLoginLayer.animate().cancel();

                    int height = mLlLoginOptions.getHeight();
                    float progress = (mLlLoginLayer.getTag() != null && mLlLoginLayer.getTag() instanceof Float) ? (float) mLlLoginLayer.getTag() : 1;
                    int time = (int) (360 * progress);

                    if (mLlLoginPull.getTag() != null) {
                        mLlLoginPull.setTag(null);
                        glide(height, progress, time);
                    } else {
                        mLlLoginPull.setTag(true);
                        upGlide(height, progress, time);
                    }
                    break;
                default:
                    break;


            }
        }
    }


    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.username) {
            if (hasFocus) {
                mLlLoginUsername.setActivated(true);
                mLlLoginPwd.setActivated(false);
            }
        } else {
            if (hasFocus) {
                mLlLoginPwd.setActivated(true);
                mLlLoginUsername.setActivated(false);
            }
        }
    }

    /**
     * menu glide
     *
     * @param height   height
     * @param progress progress
     * @param time     time
     */
    private void glide(int height, float progress, int time) {
        mLlLoginPull.animate()
                .translationYBy(height - height * progress)
                .translationY(height)
                .setDuration(time)
                .start();

        mLlLoginLayer.animate()
                .alphaBy(1 * progress)
                .alpha(0)
                .setDuration(time)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                        mLlLoginLayer.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /**
     * menu up glide
     *
     * @param height   height
     * @param progress progress
     * @param time     time
     */
    private void upGlide(int height, float progress, int time) {
        mLlLoginPull.animate()
                .translationYBy(height * progress)
                .translationY(0)
                .setDuration(time)
                .start();
        mLlLoginLayer.animate()
                .alphaBy(1 - progress)
                .alpha(1)
                .setDuration(time)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mLlLoginLayer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation instanceof ValueAnimator) {
                            mLlLoginLayer.setTag(((ValueAnimator) animation).getAnimatedValue());
                        }
                    }
                })
                .start();
    }

    //显示或隐藏logo
    public class MyViewTreeObserver implements ViewTreeObserver.OnGlobalLayoutListener {

        private int mLogoHeight;
        private int mLogoWidth;

        @Override
        public void onGlobalLayout() {
            final ImageView ivLogo =mIvLoginLogo;
            Rect KeypadRect = new Rect();

            mLayBackBar.getWindowVisibleDisplayFrame(KeypadRect);

            int screenHeight = mLayBackBar.getRootView().getHeight();
            int keypadHeight = screenHeight - KeypadRect.bottom;

            //隐藏logo
            if (keypadHeight > 300 && ivLogo.getTag() == null) {
                final int height = ivLogo.getHeight();
                final int width = ivLogo.getWidth();
                this.mLogoHeight = height;
                this.mLogoWidth = width;

                ivLogo.setTag(true);

                ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
                valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
                valueAnimator.addUpdateListener(animation -> {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                    layoutParams.height = (int) (height * animatedValue);
                    layoutParams.width = (int) (width * animatedValue);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(animatedValue);
                });

                if (valueAnimator.isRunning()) {
                    valueAnimator.cancel();
                }
                valueAnimator.start();
            }
            //显示logo
            else if (keypadHeight < 300 && ivLogo.getTag() != null) {
                final int height = mLogoHeight;
                final int width = mLogoWidth;

                ivLogo.setTag(null);

                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(400).setInterpolator(new DecelerateInterpolator());
                valueAnimator.addUpdateListener(animation -> {
                    float animatedValue = (float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = ivLogo.getLayoutParams();
                    layoutParams.height = (int) (height * animatedValue);
                    layoutParams.width = (int) (width * animatedValue);
                    ivLogo.requestLayout();
                    ivLogo.setAlpha(animatedValue);
                });

                if (valueAnimator.isRunning()) {
                    valueAnimator.cancel();
                }
                valueAnimator.start();
            }
            
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName() + " !";
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

//    private void showLoginFailed(@StringRes Integer errorString) {
//        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
//    }

    private void showLoginFailed(@Nullable String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }



    private void loginNim(String account, String password, LoginResult loginResult){
        Log.e(TAG, "account: " + account + ", password: " + password);

        /* for test */
//        InfoCache.setAccount(account);
//        InfoCache.setToken(password);

//        // 进入主界面
//        MainActivity.actionStart(LoginActivity.this, loginResult.getSuccess().getDisplayName());
//        updateUiWithUser(loginResult.getSuccess());
//        finish();

        NimUIKit.login(new LoginInfo(account, password),
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        LogUtil.i(TAG, "login success");
                        InfoCache.setAccount(account);
                        InfoCache.setToken(password);
                        NimUIKit.loginSuccess(account);

                        // 进入主界面
                        if(ifGuestLogin){
                            MainActivity.start(LoginActivity.this);
                        }
                        else{
                            MainActivity.actionStart(LoginActivity.this, loginResult.getSuccess().getDisplayName());
                            updateUiWithUser(loginResult.getSuccess());finish();
                        }

                    }


                    @Override
                    public void onFailed(int code) {
                        if (code == 302 || code == 404) {
                            ToastHelper.showToast(LoginActivity.this,
                                    R.string.login_failed);
                        } else if(code == 408) {
                            ToastHelper.showToast(LoginActivity.this,
                                    "连接超时： " + account);
                        }else {
                            ToastHelper.showToast(LoginActivity.this,
                                    "登录失败: " + code);
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        ToastHelper.showToast(LoginActivity.this,
                                "Exception When Login !");
                    }
                });
    }




    public static void start(Context context) {
        start(context, false, "");
    }

    public static void start(Context context, boolean kickOut, String kickOutDesc) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(KICK_OUT, kickOut);
        intent.putExtra(KICK_OUT_DESC, kickOutDesc);
        context.startActivity(intent);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "Press again to exit the program",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}