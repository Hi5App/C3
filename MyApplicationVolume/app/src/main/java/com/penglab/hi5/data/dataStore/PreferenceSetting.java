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

    private int contrastEnhanceRatio;

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

    public int getContrastEnhanceRatio(){
        return pref.getInt("ContrastEnhanceRatio",1);
    }

    public void setContrastEnhanceRatio(int ratio){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ContrastEnhanceRatio", ratio);
        editor.apply();
    }

    public void setPref(boolean DownSampleMode, int Contrast){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("DownSampleMode",DownSampleMode);
        editor.putInt("Contrast",Contrast);
        editor.apply();
    }

    public void setDownSampleMode(boolean downSampleMode){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("DownSampleMode", downSampleMode);
        editor.putInt("Contrast", pref.getInt("Contrast",0));
        editor.apply();
    }

    public void setContrast(int contrast){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("DownSampleMode", pref.getBoolean("DownSampleMode",true));
        editor.putInt("Contrast", contrast);
        editor.apply();
    }

    public void setPointStroke(boolean pointStroke){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("DownSampleMode",pref.getBoolean("DownSampleMode",true));
        editor.putInt("Contrast", pref.getInt("Contrast",0));
        editor.putBoolean("pointStroke",pointStroke);
        editor.apply();
    }

    public boolean getDownSampleMode(){
        return pref.getBoolean("DownSampleMode",true);
    }

    public int getContrast(){
        return pref.getInt("Contrast",0);
    }

    public boolean getPointStrokeMode() {return pref.getBoolean("pointStroke",true);}

}
