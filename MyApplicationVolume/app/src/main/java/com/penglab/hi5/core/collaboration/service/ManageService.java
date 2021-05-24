package com.penglab.hi5.core.collaboration.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.penglab.hi5.core.collaboration.connector.ServerConnector;

import java.util.Timer;

public class ManageService extends BasicService {

    private static final String TAG = "ManageService";

    private static ReadThread mReadThread;

    @Override
    public void init() {

        super.TAG = TAG;
        HEART_BEAT_RATE = 5 * 60 * 1000;
        mBasicConnector = ServerConnector.getInstance();
        mReadThread = new ReadThread(mBasicConnector.getSocket());
        mReadThread.start();

        timer = new Timer();
        timer.schedule(task, 5 * 1000, HEART_BEAT_RATE);
    }


    @Override
    public void onCreate() {
        init();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        super.onUnbind(intent);
        if (mReadThread != null)
            mReadThread.flag = false;
        timer.cancel();
        return mAllowRebind;
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public static void setRelease(boolean flag){
        mReadThread.setRelease(flag);
    }

    public static void setFlag(boolean flag){
        mReadThread.flag = flag;
    }

    public static void resetConnection(){
        mReadThread.setRelease(true);
        mReadThread.reSetConnect();
        mReadThread.setRelease(false);
    }

    @Override
    public void reConnect(){
        Log.e(TAG,"Start to reConnect");
        mBasicConnector.releaseConnection();
        mReadThread.reConnect();
    }

}
