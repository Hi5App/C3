package com.penglab.hi5.dataStore;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceLogin {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Context mContext;

    public PreferenceLogin(Context context){
        mContext = context;
        pref = mContext.getSharedPreferences("Login",Context.MODE_PRIVATE);
    }

    public void setPref(String username, String password, boolean rem_or_not){
        editor = pref.edit();
        editor.putString("Username",username);
        editor.putString("Password",password);
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

}
