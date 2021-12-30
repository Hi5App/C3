package com.penglab.hi5.data.dataStore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * Manager the pref in settings
 * three prefs: downsample mode;  check mode;  contrast
 */

public class PreferenceSetting {

    @SuppressLint("StaticFieldLeak")
    private static volatile PreferenceSetting INSTANCE;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private final SharedPreferences pref;

    public static void init(Context context){
        mContext = context;
    }

    public static PreferenceSetting getInstance(){
        if (INSTANCE == null){
            synchronized (PreferenceLogin.class){
                if (INSTANCE == null){
                    INSTANCE = new PreferenceSetting();
                }
            }
        }
        return INSTANCE;
    }
    private PreferenceSetting(){
        pref = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public void setPref(boolean DownSampleMode, int Contrast){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("DownSampleMode",DownSampleMode);
        editor.putInt("Contrast",Contrast);
        editor.apply();
    }

    public boolean getDownSampleMode(){
        return pref.getBoolean("DownSampleMode",true);
    }

    public int getContrast(){
        return pref.getInt("Contrast",0);
    }

}
