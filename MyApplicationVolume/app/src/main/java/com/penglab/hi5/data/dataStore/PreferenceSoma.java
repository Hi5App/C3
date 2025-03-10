package com.penglab.hi5.data.dataStore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jackiexing on 01/13/22
 */
public class PreferenceSoma {
    @SuppressLint("StaticFieldLeak")
    private static volatile PreferenceSoma INSTANCE;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private final SharedPreferences pref;

    public static void init(Context context){
        mContext = context;
    }

    public static PreferenceSoma getInstance(){
        if (INSTANCE == null){
            synchronized (PreferenceSoma.class){
                if (INSTANCE == null){
                    INSTANCE = new PreferenceSoma();
                }
            }
        }
        return INSTANCE;
    }

    private PreferenceSoma(){
        pref = mContext.getSharedPreferences("somaSettings", Context.MODE_PRIVATE);
    }

    public void setShowBoringFileWarning(boolean ShowBoringFileWarning){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("ShowBoringFileWarning", ShowBoringFileWarning);
        editor.putBoolean("AutoUploadMode", getAutoUploadMode());
        editor.putInt("ImageSize", getImageSize());
        editor.apply();
    }

    public void setAutoUploadMode(boolean autoUploadMode){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("AutoUploadMode", autoUploadMode);
        editor.putBoolean("ShowBoringFileWaring", getShowBoringFileWarning());
        editor.putInt("ImageSize", getImageSize());
        editor.apply();
    }

    public void setImageSize(int imageSize){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ImageSize", imageSize);
        editor.putBoolean("AutoUploadMode", getAutoUploadMode());
        editor.putBoolean("ShowBoringFileWarning", getShowBoringFileWarning());
        editor.apply();
    }

    public boolean getAutoUploadMode(){
        return pref.getBoolean("AutoUploadMode",false);
    }

    public boolean getShowBoringFileWarning(){
        return pref.getBoolean("ShowBoringFileWarning",true);
    }

    public int getImageSize(){
        return pref.getInt("ImageSize",128);
    }

}
