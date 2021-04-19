package com.example.chat.agora;

import android.content.Context;

import com.example.chat.agora.message.AgoraMsgManager;
import com.netease.nimlib.sdk.auth.LoginInfo;

public class AgoraClient {

    private static Context mContext;


    /*
    store user info;
    init AgoraMsgManager;
     */
    public static void init(Context mContext, LoginInfo loginInfo){

        AgoraClient.mContext = mContext;
        AgoraMsgManager.getInstance().init(mContext);

        if (loginInfo == null){
            AVConfig.init();
        }else {
            AVConfig.init(loginInfo);
        }

    }

}
