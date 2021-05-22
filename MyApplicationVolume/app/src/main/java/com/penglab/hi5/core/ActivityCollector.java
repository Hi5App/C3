package com.penglab.hi5.core;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
        Log.v("ActivityCollector","addActivity, size of activities: " + activities.size());
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
        Log.v("ActivityCollector","removeActivity, size of activities: " + activities.size());
    }

    public static boolean isLastActivity(){
        return activities.size() == 0;
    }

}
