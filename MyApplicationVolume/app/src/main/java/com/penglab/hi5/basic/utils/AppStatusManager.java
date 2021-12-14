package com.penglab.hi5.basic.utils;

/**
 * Created by Jackiexing on 12/13/21
 */
public class AppStatusManager {
    public int appStatus = AppStatus.STATUS_RECYCLE;

    private static volatile AppStatusManager instance;

    private AppStatusManager(){}

    public static AppStatusManager getInstance(){
        if (instance == null){
            synchronized (AppStatusManager.class){
                if (instance == null){
                    instance = new AppStatusManager();
                }
            }
        }
        return instance;
    }

    public int getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }
}
