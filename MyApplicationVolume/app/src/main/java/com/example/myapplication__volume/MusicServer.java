package com.example.myapplication__volume;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static com.example.myapplication__volume.BaseActivity.context;

public class MusicServer extends Service {
    private static String TAG = "MusicServer";

    private static MediaPlayer bgmPlayer;

    private static float volume = 1;

    public MusicServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

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