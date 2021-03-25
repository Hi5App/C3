package com.example.myapplication__volume.game;

import android.content.Context;

import com.example.datastore.database.DailyQuest;
import com.example.datastore.database.UserDailyQuest;

import java.util.ArrayList;
import java.util.List;

public class DailyQuestsContainer {
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

    public void initFromInstance(){
        DailyQuestLitePalConnector dailyQuestLitePalConnector = DailyQuestLitePalConnector.getInstance();
        dailyQuests = (ArrayList<Quest>) dailyQuestLitePalConnector.getDailyQuests(userId);
    }

    public void updateNDailyQuest(int n, int alreadyDone){
        if (dailyQuests.size() < n){
            return;
        }

        dailyQuests.get(n).updateAlreadyDone(alreadyDone);
    }
}
