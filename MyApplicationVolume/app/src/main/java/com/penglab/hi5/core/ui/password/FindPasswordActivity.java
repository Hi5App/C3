package com.penglab.hi5.core.ui.password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.core.ui.register.RegisterActivity;
import com.penglab.hi5.core.ui.register.RegisterView;
import com.penglab.hi5.data.Result;

/**
 * Created by Jackiexing on 12/7/21
 */
public class FindPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "FindPasswordActivity";
    private FindPasswordViewModel findPasswordViewModel;
    private EditText usernameEditText;
    private EditText emailEditText;
    private Button confirmButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        findPasswordViewModel = new ViewModelProvider(this, new ViewModelFactory())
                .get(FindPasswordViewModel.class);

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        confirmButton = findViewById(R.id.confirm);

        findPasswordViewModel.getFindPasswordFormState().observe(this, new Observer<FindPasswordFormState>() {
            @Override
            public void onChanged(FindPasswordFormState findPasswordFormState) {
                if (findPasswordFormState == null) {
                    return;
                }
                confirmButton.setEnabled(findPasswordFormState.isDataValid());
                if (findPasswordFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(findPasswordFormState.getUsernameError()));
                }
                if (findPasswordFormState.getEmailError() != null) {
                    emailEditText.setError(getString(findPasswordFormState.getEmailError()));
                }
            }
        });

        findPasswordViewModel.getFindPasswordResult().observe(this, new Observer<FindPasswordResult>() {
            @Override
            public void onChanged(FindPasswordResult findPasswordResult) {
                if (findPasswordResult == null) {
                    return;
                }
                if (findPasswordResult.getSuccess() != null) {
                    showFindPasswordSuccessfully(findPasswordResult.getSuccess());
                }
                if (findPasswordResult.getErrorString() != null) {
                    showRegisterFailed(findPasswordResult.getErrorString());
                }
            }
        });

        findPasswordViewModel.getUserDataSource().getResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                findPasswordViewModel.updateFindPasswordResult(result);
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
                findPasswordViewModel.userDataChanged(usernameEditText.getText().toString(),
                        emailEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        confirmButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm){
            findPasswordViewModel.findPassword(usernameEditText.getText().toString(), emailEditText.getText().toString());
        }else {
            Log.e(TAG,"Can find this view");
        }
    }

    private void showFindPasswordSuccessfully(FindPasswordView model) {
        new XPopup.Builder(this).asConfirm("Find Password", "Dear " + model.getDisplayName() + ", your password is sent to you by email.", new OnConfirmListener() {
            @Override
            public void onConfirm() {
                finish();
            }
        }).show();
    }

    private void showRegisterFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, FindPasswordActivity.class);
        context.startActivity(intent);
    }
}
