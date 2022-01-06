package com.penglab.hi5.core.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.SoundPool;

import com.penglab.hi5.R;
import com.penglab.hi5.data.dataStore.PreferenceMusic;

/**
 * Class to manage backgroundSound, buttonSound, actionSound
 *
 * Created by Jackiexing on 01/05/21
 */
public class MusicHelper {

    private final String TAG = "MusicHelper";

    public enum ActionType{
        CURVE, MARKER
    }

    @SuppressLint("StaticFieldLeak")
    private static volatile MusicHelper INSTANCE;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    private final int[] resId = new int[]{
            R.raw.marker,
            R.raw.curve,
            R.raw.button,
            R.raw.fail };

    private final PreferenceMusic preferenceMusic;
    private final SoundPool soundPool;
    private final int SOUND_NUM = 4;
    private final int[] soundId = new int[SOUND_NUM];

    private float bgmVolume;
    private float buttonVolume;
    private float actionVolume;

    public static MusicHelper getInstance(){
        if (INSTANCE == null){
            synchronized (MusicHelper.class){
                if (INSTANCE == null){
                    INSTANCE = new MusicHelper();
                }
            }
        }
        return INSTANCE;
    }

    public static void init(Context context){
        mContext = context;
    }

    private MusicHelper() {
        preferenceMusic = PreferenceMusic.getInstance();
        updateVolume();

        soundPool = new SoundPool.Builder().setMaxStreams(SOUND_NUM).build();
        for (int i = 0; i < SOUND_NUM; i++){
            soundId[i] = soundPool.load(mContext, resId[i], 1);
        }
    }

    public void playButtonSound(){
        soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
    }

    public void playActionSound(ActionType actionType){
        switch (actionType){
            case CURVE:
                soundPool.play(soundId[1], actionVolume, actionVolume, 0, 0, 1.0f);
                break;
            case MARKER:
                soundPool.play(soundId[0], actionVolume, actionVolume, 0, 0, 1.0f);
                break;
        }
    }

    public void playFailSound(){
        soundPool.play(soundId[3], buttonVolume, buttonVolume, 0, 0, 1.0f);
    }

    public void updateVolume(){
        bgmVolume = preferenceMusic.getBackgroundSound() / 100.0f;
        buttonVolume = preferenceMusic.getButtonSound() / 100.0f;
        actionVolume = preferenceMusic.getActionSound() / 100.0f;
        MusicService.setVolume(bgmVolume);
    }
}
