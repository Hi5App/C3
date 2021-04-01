package com.example.myapplication__volume;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BaseService extends Service {

    public Context context;

    public BaseService(){
        context = getBaseContext();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
