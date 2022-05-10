package com.penglab.hi5.core.ui.login;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.SoundPool;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.home.screens.HomeActivity;
import com.penglab.hi5.core.ui.password.FindPasswordActivity;
import com.penglab.hi5.core.ui.register.RegisterActivity;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.dataStore.PreferenceLogin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.penglab.hi5.core.MainActivity.ifGuestLogin;
import static com.penglab.hi5.core.Myapplication.playButtonSound;

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";
    private static final String KICK_OUT = "KICK_OUT";
    private static final String KICK_OUT_DESC = "KICK_OUT_DESC";

    private LoginViewModel loginViewModel;
    private long exitTime = 0;

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    Button VisitorLogin;
    Button registerButton;
    Button findPasswordButton;
    PreferenceLogin preferenceLogin;

    LinearLayout mLlLoginUsername;
    ImageView mIvLoginUsernameDel;
    LinearLayout mLlLoginPwd;
    ImageView mIvLoginPwdDel;
    ImageView mIvLoginLogo;
    LinearLayout mLayBackBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(LoginViewModel.class);
        preferenceLogin = PreferenceLogin.getInstance();

        /* set the view */
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        VisitorLogin = findViewById(R.id.visitor_login);
        registerButton = findViewById(R.id.goto_register);
        findPasswordButton = findViewById(R.id.forget_password);

        mLlLoginUsername = findViewById(R.id.ll_login_username);
        mIvLoginUsernameDel = findViewById(R.id.iv_login_username_del);
        mLlLoginPwd = findViewById(R.id.ll_login_pwd);
        mIvLoginPwdDel = findViewById(R.id.iv_login_pwd_del);

        mLayBackBar = findViewById(R.id.ly_retrieve_bar);
        mIvLoginLogo = findViewById(R.id.iv_login_logo);

        /* load local user info */
        if (preferenceLogin.getRem_or_not()){
            usernameEditText.setText(preferenceLogin.getUsername());
            passwordEditText.setText(preferenceLogin.getPassword());
            loginButton.setBackgroundResource(R.drawable.bg_login_submit);
            loginButton.setTextColor(getResources().getColor(R.color.white));
        }

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
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
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    preferenceLogin.setPref(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(),true,true);
                    updateUiWithUser(loginResult.getSuccess());
                    showHomeActivity();
                    setResult(Activity.RESULT_OK);
                }
            }
        });

        loginViewModel.getUserDataSource().getResult().observe(this, new Observer<Result>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onChanged(Result result) {
                if (result == null){
                    return;
                }
                loginViewModel.updateLoginResult(result);
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // 是否显示清除按钮
                if (username.length() > 0) {
                    mIvLoginUsernameDel.setVisibility(View.VISIBLE);
                } else {
                    mIvLoginUsernameDel.setVisibility(View.INVISIBLE);
                }
                if (password.length() > 0) {
                    mIvLoginPwdDel.setVisibility(View.VISIBLE);
                } else {
                    mIvLoginPwdDel.setVisibility(View.INVISIBLE);
                }

                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {
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
                loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(new MyClick());
        VisitorLogin.setOnClickListener(new MyClick());
        registerButton.setOnClickListener(new MyClick());
        findPasswordButton.setOnClickListener(new MyClick());

        mIvLoginUsernameDel.setOnClickListener(new MyClick());
        mIvLoginPwdDel.setOnClickListener(new MyClick());
        mLayBackBar.getViewTreeObserver().addOnGlobalLayoutListener(new MyViewTreeObserver());

    }

    public class MyClick implements View.OnClickListener{
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.login:
                    playButtonSound();
                    loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                    break;
                case R.id.visitor_login:
                    playButtonSound();
                    ifGuestLogin = true;
                    HomeActivity.start(LoginActivity.this);
                    Toast.makeText(getApplicationContext(),"you are now logged in as a visitor",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case R.id.goto_register:
                    RegisterActivity.start(LoginActivity.this);
                    break;
                case R.id.forget_password:
                    FindPasswordActivity.start(LoginActivity.this);
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
                    usernameEditText.setText(null);
                    break;
                case R.id.iv_login_pwd_del:
                    passwordEditText.setText(null);
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

    // 显示或隐藏logo
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

            // 隐藏logo
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
            // 显示logo
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
        String welcome = getString(R.string.welcome) + model.getNickName() + " !";
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@Nullable String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }

    private void showHomeActivity(){
        HomeActivity.start(LoginActivity.this);
        finish();
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