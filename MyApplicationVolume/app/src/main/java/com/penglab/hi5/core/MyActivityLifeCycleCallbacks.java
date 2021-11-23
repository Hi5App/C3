package com.penglab.hi5.core;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.core.collaboration.connector.ServerConnector;
import com.penglab.hi5.core.game.Score;

import java.util.LinkedList;
import java.util.List;

public class MyActivityLifeCycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private final String TAG = "MyActivityLifeCycle";
    private final String MY_PKG_NAME = "com.penglab.hi5";
    private int activityCount = 0;

    private List<Activity> activities = new LinkedList<>();

    public static int sAnimationId = 0;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.d(TAG, "onActivityStarted");
        activityCount++;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d(TAG, "onActivityStopped");
        activityCount--;
        if (activityCount <= 0){
            Log.d(TAG, "Now On Background");
            if (isActivityAlive("ComponentInfo{com.penglab.hi5/com.penglab.hi5.core.MainActivity}")){
                // update score
                Score score = Score.getInstance();
                MainActivity.setScore(score.getScore());
            }
            ServerConnector.getInstance().closeSender();
            MsgConnector.getInstance().closeSender();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        removeActivity(activity);
    }

    private boolean isAppRun() {
        ActivityManager am = (ActivityManager)Myapplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list){
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) && info.baseActivity.getPackageName().equals(MY_PKG_NAME))
                return true;
        }
        return false;
    }

    private boolean isActivityAlive(String activityName) {
        ActivityManager am = (ActivityManager)Myapplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list){
            if (info.topActivity.toString().equals(activityName) || info.baseActivity.toString().equals(activityName))
                return true;
        }
        return false;
    }

    public void addActivity(Activity activity) {
        if (activities == null) {
            activities = new LinkedList<>();
        }

        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (activities.remove(activity)) {
            activities.remove(activity);
        }

        if (activities.size() == 0) {
            activities = null;
        }
    }

    public void removeAllActivities() {
        for (Activity activity : activities) {
            activity.finish();
            activity.overridePendingTransition(0, sAnimationId);
        }
    }
}
