package com.penglab.hi5.data.dataStore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jackiexing on 01/05/21
 */
public class PreferenceMusic {

    @SuppressLint("StaticFieldLeak")
    private static volatile PreferenceMusic INSTANCE;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private final SharedPreferences pref;

    public static void init(Context context){
        mContext = context;
    }

    public static PreferenceMusic getInstance(){
        if (INSTANCE == null){
            synchronized (PreferenceMusic.class){
                if (INSTANCE == null){
                    INSTANCE = new PreferenceMusic();
                }
            }
        }
        return INSTANCE;
    }
    private PreferenceMusic(){
        pref = mContext.getSharedPreferences("music", Context.MODE_PRIVATE);
    }

    public void setPref(int backgroundSound, int buttonSound, int actionSound){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("BackgroundSound", backgroundSound);
        editor.putInt("ButtonSound", buttonSound);
        editor.putInt("ActionSound", actionSound);
        editor.apply();
    }

    public void setBackgroundSound(int backgroundSound){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("BackgroundSound", backgroundSound);
        editor.putInt("ButtonSound", pref.getInt("ButtonSound", 0));
        editor.putInt("ActionSound", pref.getInt("ActionSound", 0));
        editor.apply();
    }

    public void setButtonSound(int backgroundSound){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ButtonSound", backgroundSound);
        editor.putInt("BackgroundSound", pref.getInt("BackgroundSound", 0));
        editor.putInt("ActionSound", pref.getInt("ActionSound", 0));
        editor.apply();
    }

    public void setActionSound(int actionSound){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ActionSound", actionSound);
        editor.putInt("BackgroundSound", pref.getInt("BackgroundSound", 0));
        editor.putInt("ButtonSound", pref.getInt("ButtonSound", 0));
        editor.apply();
    }

    public int getBackgroundSound(){
        return pref.getInt("BackgroundSound", 0);
    }

    public int getButtonSound(){
        return pref.getInt("ButtonSound", 0);
    }

    public int getActionSound(){
        return pref.getInt("ActionSound", 0);
    }

}
