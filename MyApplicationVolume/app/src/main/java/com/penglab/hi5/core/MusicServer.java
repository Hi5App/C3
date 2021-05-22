package com.penglab.hi5.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.penglab.hi5.R;

import java.io.File;
import java.io.IOException;

public class MusicServer extends Service {
    private static String TAG = MusicServer.class.getSimpleName();

    private static MediaPlayer bgmPlayer;

    private static float volume = 0;

    public static final String CHANNEL_ID_STRING = "service_01";

    private Notification notification;

    public MusicServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //适配8.0 service
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID_STRING, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
            startForeground(1, notification);
        }
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForeground(1, notification);
        }

        if (bgmPlayer == null){
//            bgmPlayer = new MediaPlayer();
            bgmPlayer = MediaPlayer.create(this, R.raw.bgm03sponge);
            bgmPlayer.setVolume(volume, volume);
//            String externalFileDir = getBaseContext().getExternalFilesDir(null).toString();
//            try {
//                bgmPlayer.setDataSource(externalFileDir + "/Resources/Music/CoyKoi.mp3");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            bgmPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    bgmPlayer.start();
//                }
//            });
//            bgmPlayer.prepareAsync();
            bgmPlayer.setLooping(true);
            bgmPlayer.start();
        }
    }

    public static void setVolume(float f){
        volume = f;
    }

    public static void setBgmVolume(float f){
        bgmPlayer.setVolume(f, f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bgmPlayer.stop();
    }

    public static void setBgmSource(String path){
        File file = new File(path);
        if (!file.exists()){
            Log.d(TAG, "Music File Not Found");
            return;
        }
        bgmPlayer.stop();
        bgmPlayer = new MediaPlayer();
        try {
            bgmPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bgmPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    bgmPlayer.start();
                }
            });
        bgmPlayer.setVolume(volume, volume);
        bgmPlayer.setLooping(true);
        bgmPlayer.prepareAsync();
    }

    public static void defaultBgmSource(){
        bgmPlayer.stop();
        bgmPlayer = MediaPlayer.create(Myapplication.getContext(), R.raw.bgm03sponge);
        bgmPlayer.setVolume(volume, volume);
        bgmPlayer.setLooping(true);
        bgmPlayer.start();
    }
}