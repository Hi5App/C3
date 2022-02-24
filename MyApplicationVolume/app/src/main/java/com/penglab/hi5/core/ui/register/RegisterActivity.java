package com.penglab.hi5.core.ui.register;

import static com.penglab.hi5.core.Myapplication.playButtonSound;

import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.data.Result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private RegisterViewModel registerViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"start to register: onCreate !");

        setContentView(R.layout.activity_register);
        registerViewModel = new ViewModelProvider(this, new ViewModelFactory())
                .get(RegisterViewModel.class);

        EditText usernameEditText = findViewById(R.id.register_username);
        EditText nicknameEditText = findViewById(R.id.register_nickname);
        EditText passwordEditText = findViewById(R.id.register_password);
        EditText passwordCheckEditText = findViewById(R.id.register_password_check);
        EditText emailEditText = findViewById(R.id.register_email);
        EditText inviterText = findViewById(R.id.register_inviter);
        Button   registerButton = findViewById(R.id.register);

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
                if (registerResult == null) {
                    return;
                }
                if (registerResult.getErrorString() != null){
                    showRegisterFailed(registerResult.getErrorString());
                }
                if (registerResult.getSuccess() != null) {
                    showRegisterSuccessfully(registerResult.getSuccess());
                }
            }
        });

        registerViewModel.getRegisterDataSource().getResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null){
                    return;
                }
                registerViewModel.updateRegisterResult(result);
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
                registerViewModel.registerDataChanged(emailEditText.getText().toString(), usernameEditText.getText().toString(),
                        nicknameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        };


        usernameEditText.addTextChangedListener(afterTextChangedListener);
        nicknameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordCheckEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerViewModel.register(emailEditText.getText().toString(), usernameEditText.getText().toString(),
                            nicknameEditText.getText().toString(), passwordEditText.getText().toString(), inviterText.toString());
                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButtonSound();

                if (passwordEditText.getText().toString().equals(passwordCheckEditText.getText().toString())){
                    registerViewModel.register(emailEditText.getText().toString(), usernameEditText.getText().toString(),
                            nicknameEditText.getText().toString(), passwordEditText.getText().toString(), inviterText.getText().toString());
                }else {
                    Toast.makeText(getApplication(),"Confirm the password is consistent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showRegisterSuccessfully(RegisterView model) {
        String welcome = "Register user:  " + model.getDisplayName() + "  successfully !";
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        LoginActivity.start(RegisterActivity.this);
        finish();
    }

    private void showRegisterFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }

    public static void start(Context context){
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

}