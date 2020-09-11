package com.example.myapplication__volume;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datastore.DataManager;
import com.example.datastore.PreferenceSetting;

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
    }

    public void Toast_in_Thread(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
