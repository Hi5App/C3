package com.penglab.hi5.dataStore;

import android.content.Context;
import android.content.SharedPreferences;

public class DataManager {

    private Context mContext;
    public DataManager(Context context) {
        mContext = context;
    }

    public void preferenceSetting(){

        SharedPreferences.Editor editor = mContext.getSharedPreferences("settings",Context.MODE_PRIVATE).edit();
    }

}
