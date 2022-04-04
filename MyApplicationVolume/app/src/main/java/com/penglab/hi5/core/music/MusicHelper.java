package com.penglab.hi5.core.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.penglab.hi5.R;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.data.dataStore.PreferenceMusic;

import java.lang.ref.PhantomReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to manage backgroundSound, buttonSound, actionSound
 *
 * Created by Jackiexing on 01/05/21
 */
public class MusicHelper {

    private final String TAG = "MusicHelper";

    private static MediaPlayer mPlayer;

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
            R.raw.fail,
            R.raw.nice,
            R.raw.wonderful,
            R.raw.unbelievable,

    };

    private final int[] musicId = new int[]{
            R.raw.kldykuangxiangqu,
            R.raw.tiankongzhicheng,
            R.raw.tougong,
            R.raw.wanisha,
            R.raw.yiqiangeshangxindeliyou,
            R.raw.yujian,
            R.raw.zhongsendedashu
    };

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private final PreferenceMusic preferenceMusic;
    private final SoundPool soundPool;
    private final int SOUND_NUM = 7;
    private final int[] soundId = new int[SOUND_NUM];
    private final int MUSIC_NUM = 7;
    private final int[] music_id = new int [MUSIC_NUM];

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
        executorService.submit(() -> soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f));
    }

    public void playActionSound(ActionType actionType){
        switch (actionType){
            case CURVE:
                executorService.submit(() -> soundPool.play(soundId[1], actionVolume, actionVolume, 0, 0, 1.0f));
                break;
            case MARKER:
                executorService.submit(() -> soundPool.play(soundId[0], actionVolume, actionVolume, 0, 0, 1.0f));
                break;
        }
    }

    public void playFailSound(){
        executorService.submit(() -> soundPool.play(soundId[3], buttonVolume, buttonVolume, 0, 0, 1.0f));
    }

    public void playRewardSound(int level) {
        executorService.submit(() -> soundPool.play(soundId[3+level], 0.3f, 0.3f, 0, 0, 1.0f));
    }

    public void playMusicReward(int level) {
        mPlayer = MediaPlayer.create(Myapplication.getContext(),musicId[level]);
        mPlayer.setVolume(0.5f,0.5f);
        mPlayer.start();

    }

    public void stopMusicRewardPlay() {
        mPlayer.stop();
    }

    public void updateVolume(){
        bgmVolume = preferenceMusic.getBackgroundSound() / 100.0f;
        buttonVolume = preferenceMusic.getButtonSound() / 100.0f;
        actionVolume = preferenceMusic.getActionSound() / 100.0f;
        MusicService.setVolume(bgmVolume);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        executorService.shutdown();
    }
}
