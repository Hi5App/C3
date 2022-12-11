package com.penglab.hi5.data.dataStore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceLogin {

    @SuppressLint("StaticFieldLeak")
    private static volatile PreferenceLogin INSTANCE;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private final SharedPreferences pref;

    public static void init(Context context){
        mContext = context;
    }

    public static PreferenceLogin getInstance(){
        if (INSTANCE == null){
            synchronized (PreferenceLogin.class){
                if (INSTANCE == null){
                    INSTANCE = new PreferenceLogin();
                }
            }
        }
        return INSTANCE;
    }

    private PreferenceLogin(){
        pref = mContext.getSharedPreferences("Login", Context.MODE_PRIVATE);
    }

    public void setPref(String username, String password, int id, boolean autoLogin, boolean rem_or_not){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Username",username);
        editor.putString("Password",password);
        editor.putInt("id",id);
        editor.putBoolean("AutoLogin",autoLogin);
        editor.putBoolean("Rem_or_not",rem_or_not);
        editor.apply();
    }

    public String getUsername(){
        return pref.getString("Username","");
    }

    public String getPassword(){
        return pref.getString("Password","");
    }

    public boolean getRem_or_not(){
        return pref.getBoolean("Rem_or_not",false);
    }

    public boolean getAutoLogin(){
        return pref.getBoolean("AutoLogin",false);
    }

    public int getId() { return pref.getInt("id",-1); }
}
