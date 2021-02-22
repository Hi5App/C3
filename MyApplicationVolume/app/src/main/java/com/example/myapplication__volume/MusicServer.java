package com.example.myapplication__volume;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

import static com.example.myapplication__volume.BaseActivity.context;

public class MusicServer extends Service {
    private static MediaPlayer bgmPlayer;

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
//            String externalFileDir = context.getExternalFilesDir(null).toString();
//            try {
//                bgmPlayer.setDataSource(externalFileDir + "/bgm01main.mp3");
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

    public static void setBgmVolume(float f){
        bgmPlayer.setVolume(f, f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bgmPlayer.stop();
    }
}