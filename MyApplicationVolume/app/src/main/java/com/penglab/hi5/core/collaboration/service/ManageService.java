package com.penglab.hi5.core.collaboration.service;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.penglab.hi5.core.collaboration.connector.ServerConnector;

import static com.penglab.hi5.core.Myapplication.lockForManageSocket;

public class ManageService extends BasicService {

    private static final String TAG = "ManageService";

    protected static ReadThread mReadThread;

    private static volatile boolean isReleased = false;

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
            mReadThread.needStop = true;
        return mAllowRebind;
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public static void setRelease(boolean flag){
        isReleased = flag;
    }

    @Override
    protected boolean getRelease(){
        return isReleased;
    }

    @Override
    protected void setReleaseInner(boolean flag) {
        isReleased = flag;
    }

    /* Stop the run() in mReadThread when unbind the service in destroy() in MainActivity */
    public static void setStop(boolean flag){
        mReadThread.needStop = flag;
    }

    public static void resetConnection(){
        mReadThread.resetConnection();
    }

    /* use synchronized to avoid the using conflict of socket*/
    public void reConnection(){
        Log.e(TAG,"Start to reConnect");
        synchronized (lockForManageSocket){
            if(!mBasicConnector.checkConnection()){
                mBasicConnector.releaseConnection();
                mReadThread.reConnect();
            }
        }
    }

}
