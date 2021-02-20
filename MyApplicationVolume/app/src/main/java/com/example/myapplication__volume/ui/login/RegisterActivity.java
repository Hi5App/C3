package com.example.myapplication__volume.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication__volume.R;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private RegisterViewModel registerViewModel;

    final private int REGISTER_ON_CLICK = 2;

    EditText usernameEditText;
    EditText passwordEditText;
    EditText nicknameEditText;
    EditText emailEditText;
    Button registerButton;
    ProgressBar loadingProgressBar;

    private SoundPool soundPool;
    private int soundId;

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REGISTER_ON_CLICK:
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    registerViewModel.register(emailEditText.getText().toString(), usernameEditText.getText().toString(),
                            nicknameEditText.getText().toString(), passwordEditText.getText().toString());
                    Log.d("LoginButton:", "onClickkkkkkkk");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,"Start to register: onCreate !");

        setContentView(R.layout.activity_register);
        registerViewModel = ViewModelProviders.of(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        usernameEditText = findViewById(R.id.register_username);
        nicknameEditText = findViewById(R.id.register_nickname);
        passwordEditText = findViewById(R.id.register_password);
        emailEditText = findViewById(R.id.register_email);
        registerButton = findViewById(R.id.register);
        loadingProgressBar = findViewById(R.id.loading);

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        soundId = soundPool.load(this, R.raw.button01, 1);

        // for test
//        usernameEditText.setText("xf");
//        nicknameEditText.setText("xingfei");
//        passwordEditText.setText("123456");
//        emailEditText.setText("761496766@qq.com");

        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                registerButton.setEnabled(registerFormState.isDataValid());
                if (registerFormState.getEmailError() != null){
                    emailEditText.setError(getString(registerFormState.getEmailError()));
                }
                if (registerFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getNicknameError() != null) {
                    nicknameEditText.setError(getString(registerFormState.getNicknameError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registerFormState.getPasswordError()));
                }
            }
        });

        registerViewModel.getRegisterResult().observe(this, new Observer<RegisterResult>() {
            @Override
            public void onChanged(@Nullable RegisterResult registerResult) {
                Log.d("LoginResultOnChanged", "innnnnn");
                if (registerResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (registerResult.getError() != null) {
                    showRegisterFailed(registerResult.getError());

                } else if (registerResult.getErrorString() != null){
                    showRegisterFailed(registerResult.getErrorString());
                    Log.e("LoginResultOnChanged", registerResult.getErrorString());
                }
                if (registerResult.getSuccess() != null) {
                    Log.d("LoginResultOnChanged", "getSuccess");
                    LoginActivity.actionStart(RegisterActivity.this);

                    updateUiWithUser(registerResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
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
                registerViewModel.registerDataChanged(emailEditText.getText().toString(), usernameEditText.getText().toString(),
                        nicknameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        nicknameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerViewModel.register(emailEditText.getText().toString(), usernameEditText.getText().toString(),
                            nicknameEditText.getText().toString(), passwordEditText.getText().toString());
                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);

                handler.sendEmptyMessage(REGISTER_ON_CLICK);
//                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                startActivity(intent);
//                LoginActivity.actionStart(RegisterActivity.this);
            }
        });

    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showRegisterFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    private void showRegisterFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
}