package com.penglab.hi5.core.ui.password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by Jackiexing on 12/7/21
 */
public class FindPasswordActivity extends AppCompatActivity {

    private FindPasswordViewModel findPasswordViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        findPasswordViewModel = new ViewModelProvider(this, new ViewModelFactory())
                .get(FindPasswordViewModel.class);

        EditText usernameEditText = findViewById(R.id.username);
        EditText emailEditText = findViewById(R.id.email);
        Button confirmButton = findViewById(R.id.confirm);

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
