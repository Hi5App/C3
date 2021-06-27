package com.penglab.hi5.core.game;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

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

        DailyQuestLitePalConnector dailyQuestLitePalConnector = DailyQuestLitePalConnector.getInstance();
        dailyQuestLitePalConnector.updateDailyQuests(userId, n, dailyQuests.get(n).getContent(), dailyQuests.get(n).getStatus().ordinal(), dailyQuests.get(n).getAlreadyDone());
    }

    public void updateNDailyQuest(int n, Quest.Status status){
        if (dailyQuests.size() < n){
            return;
        }

        dailyQuests.get(n).setStatus(status);

        DailyQuestLitePalConnector dailyQuestLitePalConnector = DailyQuestLitePalConnector.getInstance();
        dailyQuestLitePalConnector.updateDailyQuests(userId, n, dailyQuests.get(n).getContent(), dailyQuests.get(n).getStatus().ordinal(), dailyQuests.get(n).getAlreadyDone());
    }

    public void updateCurveNum(int n){
        // draw a curve
        dailyQuests.get(1).updateAlreadyDone(n);
        // draw 100 curves
        dailyQuests.get(2).updateAlreadyDone(n);

        DailyQuestLitePalConnector dailyQuestLitePalConnector = DailyQuestLitePalConnector.getInstance();
        dailyQuestLitePalConnector.updateDailyQuests(userId, 1, dailyQuests.get(1).getContent(), dailyQuests.get(1).getStatus().ordinal(), dailyQuests.get(1).getAlreadyDone());
        dailyQuestLitePalConnector.updateDailyQuests(userId, 2, dailyQuests.get(2).getContent(), dailyQuests.get(2).getStatus().ordinal(), dailyQuests.get(2).getAlreadyDone());
    }

    public void updateMarkerNum(int n){
        // draw a marker
        dailyQuests.get(3).updateAlreadyDone(n);
        // draw 100 markers
        dailyQuests.get(4).updateAlreadyDone(n);

        DailyQuestLitePalConnector dailyQuestLitePalConnector = DailyQuestLitePalConnector.getInstance();
        dailyQuestLitePalConnector.updateDailyQuests(userId, 3, dailyQuests.get(3).getContent(), dailyQuests.get(3).getStatus().ordinal(), dailyQuests.get(3).getAlreadyDone());
        dailyQuestLitePalConnector.updateDailyQuests(userId, 4, dailyQuests.get(4).getContent(), dailyQuests.get(4).getStatus().ordinal(), dailyQuests.get(4).getAlreadyDone());
    }
}
