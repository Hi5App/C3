package com.example.myapplication__volume.game;

import android.content.Context;
import android.util.Log;

import com.example.datastore.database.DailyQuest;
import com.example.datastore.database.UserDailyQuest;

import java.util.ArrayList;
import java.util.List;

public class DailyQuestsContainer {

    private String TAG = "DailyQuestsContainer";

    private ArrayList<Quest> dailyQuests = new ArrayList<>();

    private static DailyQuestsContainer INSTANCE;
    private static Context mContext;

    private static String userId;

    public static DailyQuestsContainer getInstance(){
        if (INSTANCE == null){
            synchronized (DailyQuestsContainer.class){
                if (INSTANCE == null){
                    INSTANCE = new DailyQuestsContainer();
                }
            }
        }
        return INSTANCE;
    }

    public static void init(Context context){
        mContext = context;
    }

    public static void initId(String userId){
        DailyQuestsContainer.userId = userId;
    }

    public ArrayList<Quest> getDailyQuests() {
        return dailyQuests;
    }

    public void setDailyQuests(ArrayList<Quest> dailyQuests) {
        this.dailyQuests = dailyQuests;
    }

    public void initFromLitePal(){
        DailyQuestLitePalConnector dailyQuestLitePalConnector = DailyQuestLitePalConnector.getInstance();
        dailyQuests = (ArrayList<Quest>) ((ArrayList<Quest>) dailyQuestLitePalConnector.getDailyQuests(userId)).clone();
        Log.d(TAG, "dailyQuests.size(): " + dailyQuests.size());
    }

    public void updateNDailyQuest(int n, int alreadyDone){
        if (dailyQuests.size() < n){
            return;
        }

        dailyQuests.get(n).updateAlreadyDone(alreadyDone);
    }

    public void updateCurveNum(int n){
        // draw a curve
        dailyQuests.get(1).updateAlreadyDone(n);
        // draw 100 curves
        dailyQuests.get(2).updateAlreadyDone(n);
    }

    public void updateMarkerNum(int n){
        // draw a marker
        dailyQuests.get(3).updateAlreadyDone(n);
        // draw 100 markers
        dailyQuests.get(4).updateAlreadyDone(n);
    }
}
