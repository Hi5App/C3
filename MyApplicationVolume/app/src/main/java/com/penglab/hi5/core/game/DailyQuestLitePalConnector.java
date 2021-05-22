package com.penglab.hi5.core.game;

import android.content.Context;
import android.util.Log;

import com.penglab.hi5.dataStore.database.DailyQuest;
import com.penglab.hi5.dataStore.database.UserDailyQuest;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DailyQuestLitePalConnector {
    private String TAG = "DailyQuestLitePalConnector";

    private static DailyQuestLitePalConnector INSTANCE;
    private static Context mContext;

    private Quest[] newQuests = {
            new Quest("Login",  0, 1, 20),
            new Quest("Draw a line", 0, 1, 20),
            new Quest("Draw 100 lines", 0, 100, 100),
            new Quest("Draw a marker", 0, 1, 20),
            new Quest("Draw 100 markers", 0, 100, 100),
    };

    public static DailyQuestLitePalConnector getInstance(){
        if (INSTANCE == null){
            synchronized (DailyQuestLitePalConnector.class) {
                if (INSTANCE == null)
                    INSTANCE = new DailyQuestLitePalConnector();
            }
        }
        return INSTANCE;
    }

    public static void init(Context context){
        mContext = context;
    }

    public List<Quest> getDailyQuests(String userId){
        List<UserDailyQuest> userDailyQuests = LitePal.where("userid = ?", userId).find(UserDailyQuest.class);
        if (userDailyQuests.size() > 1){

            Log.d(TAG, "Something wrong in dailyquests database");
            return null;
        } else if (userDailyQuests.size() == 0){

            Log.d(TAG, "Not found int database. Create a new one.");

            UserDailyQuest userDailyQuest = new UserDailyQuest();
            userDailyQuest.setUserId(userId);
            List<DailyQuest> dailyQuests = initNewDailyQuests();
            Log.d(TAG, "dailyQuests.size(): " + dailyQuests.size());
            userDailyQuest.setDailyQuests(dailyQuests);
            userDailyQuest.save();

            ArrayList<Quest> quests = new ArrayList<>();
            for (int i = 0; i < dailyQuests.size(); i++){
                quests.add(new Quest(dailyQuests.get(i).getContent(), dailyQuests.get(i).getStatus(), dailyQuests.get(i).getAlreadyDone(), dailyQuests.get(i).getToBeDone(), dailyQuests.get(i).getReward()));
            }
            Log.d(TAG, "quests.size(): " + quests.size());
            Log.d(TAG, quests.get(0).getContent());

            return quests;
        } else {

            Log.d(TAG, "Founded");

            List<DailyQuest> dailyQuests = userDailyQuests.get(0).getDailyQuests();
            ArrayList<Quest> quests = new ArrayList<>();
            for (int i = 0; i < dailyQuests.size(); i++) {
                quests.add(new Quest(dailyQuests.get(i).getContent(), dailyQuests.get(i).getStatus(), dailyQuests.get(i).getAlreadyDone(), dailyQuests.get(i).getToBeDone(), dailyQuests.get(i).getReward()));
            }

            return quests;
        }
//        Quest [] quests = new Quest[dailyQuests.size()];
//        for (int i = 0; i < dailyQuests.size(); i++){
//            quests[i] = new Quest(dailyQuests.get(i).getContent(), dailyQuests.get(i).getStatus(), dailyQuests.get(i).getAlreadyDone(), dailyQuests.get(i).getToBeDone(), dailyQuests.get(i).getReward());
//        }

    }

    private List<DailyQuest> initNewDailyQuests(){
        List<DailyQuest> newDailyQuestList = new ArrayList<>();

        for (int i = 0; i < newQuests.length; i++) {
            DailyQuest dailyQuest = new DailyQuest();
            dailyQuest.setContent(newQuests[i].getContent());
            dailyQuest.setAlreadyDone(newQuests[i].getAlreadyDone());
            dailyQuest.setToBeDone(newQuests[i].getToBeDone());
            dailyQuest.setReward(newQuests[i].getReward());
            dailyQuest.setStatus(newQuests[i].getStatus().ordinal());

            newDailyQuestList.add(dailyQuest);
        }

        return newDailyQuestList;
    }

    public void updateDailyQuests(String userId, int n, String content, int status, int alreadyDone){
        List<UserDailyQuest> userDailyQuests = LitePal.where("userid = ?", userId).find(UserDailyQuest.class);
        if (userDailyQuests.size() != 1){
            Log.d(TAG, "Error in database");
            return;
        }
        Long id = userDailyQuests.get(0).getId();
        List<DailyQuest> dailyQuests = userDailyQuests.get(0).getDailyQuests();
        dailyQuests.get(n).setStatus(status);
        dailyQuests.get(n).setAlreadyDone(alreadyDone);
        dailyQuests.get(n).save();
        userDailyQuests.get(0).save();
//        DailyQuest dailyQuest = new DailyQuest();
//        dailyQuest.setStatus(status);
//        dailyQuest.setAlreadyDone(alreadyDone);
//        dailyQuest.updateAll("id = ? and content = ?", String.valueOf(id), content);
    }
}
