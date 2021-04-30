package com.main.core.collaboration.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.main.core.collaboration.connector.ServerConnector;

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
        mReadThread.setRelease(false);
        mReadThread.reSetConnect();
    }

    @Override
    public void reConnect(){
        Log.e(TAG,"Start to reConnect");
        mBasicConnector.releaseConnection();
        mReadThread.reConnect();
    }

}
