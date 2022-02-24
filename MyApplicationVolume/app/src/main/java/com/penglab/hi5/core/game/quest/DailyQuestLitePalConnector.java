package com.penglab.hi5.core.game.quest;

import android.util.Log;

import com.penglab.hi5.data.dataStore.database.DailyQuest;
import com.penglab.hi5.data.dataStore.database.UserDailyQuest;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yihang zhu 12/29/21
 */
public class DailyQuestLitePalConnector {

    private Quest[] newQuests = {
            new Quest("Login",  0, 1, 20),
            new Quest("Draw a line", 0, 1, 20),
            new Quest("Draw 100 lines", 0, 100, 100),
            new Quest("Draw a marker", 0, 1, 20),
            new Quest("Draw 100 markers", 0, 100, 100),
    };

    public List<Quest> getDailyQuests(String userId){
        List<UserDailyQuest> userDailyQuests = LitePal.where("userid = ?", userId).find(UserDailyQuest.class);
        if (userDailyQuests.size() > 1){
            return null;
        } else if (userDailyQuests.size() == 0){
            UserDailyQuest userDailyQuest = new UserDailyQuest();
            userDailyQuest.setUserId(userId);
            List<DailyQuest> dailyQuests = initNewDailyQuests();
            userDailyQuest.setDailyQuests(dailyQuests);
            userDailyQuest.save();

            ArrayList<Quest> quests = new ArrayList<>();
            for (int i = 0; i < dailyQuests.size(); i++){
                quests.add(new Quest(dailyQuests.get(i).getContent(), dailyQuests.get(i).getStatus(), dailyQuests.get(i).getAlreadyDone(), dailyQuests.get(i).getToBeDone(), dailyQuests.get(i).getReward()));
            }

            return quests;
        } else {
            List<DailyQuest> dailyQuests = userDailyQuests.get(0).getDailyQuests();
            ArrayList<Quest> quests = new ArrayList<>();
            for (int i = 0; i < dailyQuests.size(); i++) {
                quests.add(new Quest(dailyQuests.get(i).getContent(), dailyQuests.get(i).getStatus(), dailyQuests.get(i).getAlreadyDone(), dailyQuests.get(i).getToBeDone(), dailyQuests.get(i).getReward()));
            }

            return quests;
        }
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
            return;
        }
        Long id = userDailyQuests.get(0).getId();
        List<DailyQuest> dailyQuests = userDailyQuests.get(0).getDailyQuests();
        dailyQuests.get(n).setStatus(status);
        dailyQuests.get(n).setAlreadyDone(alreadyDone);
        dailyQuests.get(n).save();
        userDailyQuests.get(0).save();
    }
}
