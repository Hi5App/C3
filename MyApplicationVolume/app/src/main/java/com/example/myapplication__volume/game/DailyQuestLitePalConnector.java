package com.example.myapplication__volume.game;

import android.content.Context;
import android.widget.Toast;

import com.example.datastore.database.DailyQuest;
import com.example.datastore.database.UserDailyQuest;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DailyQuestLitePalConnector {
    private static DailyQuestLitePalConnector INSTANCE;
    private static Context mContext;

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
        if (userDailyQuests.size() != 1){
            Toast.makeText(mContext, "Something wrong in dailyquests database", Toast.LENGTH_SHORT);
            return null;
        }
        List<DailyQuest> dailyQuests = userDailyQuests.get(0).getDailyQuests();
        ArrayList<Quest> quests = new ArrayList<>();
        for (int i = 0; i < dailyQuests.size(); i++){
            quests.add(new Quest(dailyQuests.get(i).getContent(), dailyQuests.get(i).getStatus(), dailyQuests.get(i).getAlreadyDone(), dailyQuests.get(i).getToBeDone(), dailyQuests.get(i).getReward()));
        }
//        Quest [] quests = new Quest[dailyQuests.size()];
//        for (int i = 0; i < dailyQuests.size(); i++){
//            quests[i] = new Quest(dailyQuests.get(i).getContent(), dailyQuests.get(i).getStatus(), dailyQuests.get(i).getAlreadyDone(), dailyQuests.get(i).getToBeDone(), dailyQuests.get(i).getReward());
//        }
        return quests;
    }
}
