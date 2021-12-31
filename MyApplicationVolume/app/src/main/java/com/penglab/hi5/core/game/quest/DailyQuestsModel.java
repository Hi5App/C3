package com.penglab.hi5.core.game.quest;

import java.util.ArrayList;

/**
 * Created by Yihang zhu 12/29/21
 */
public class DailyQuestsModel {
    private String userId;

    private ArrayList<Quest> dailyQuests = new ArrayList<>();

    public void setUserId(String userId){
        this.userId = userId;
    }

    public ArrayList<Quest> getDailyQuests() {
        return dailyQuests;
    }

    public void setDailyQuests(ArrayList<Quest> dailyQuests) {
        this.dailyQuests = dailyQuests;
    }

    public void initFromLitePal(){
        DailyQuestLitePalConnector dailyQuestLitePalConnector = new DailyQuestLitePalConnector();
        dailyQuests = (ArrayList<Quest>) ((ArrayList<Quest>) dailyQuestLitePalConnector.getDailyQuests(userId)).clone();
    }

    public void updateNDailyQuest(int n, int alreadyDone){
        if (dailyQuests.size() < n){
            return;
        }

        dailyQuests.get(n).updateAlreadyDone(alreadyDone);

        DailyQuestLitePalConnector dailyQuestLitePalConnector = new DailyQuestLitePalConnector();
        dailyQuestLitePalConnector.updateDailyQuests(userId, n, dailyQuests.get(n).getContent(), dailyQuests.get(n).getStatus().ordinal(), dailyQuests.get(n).getAlreadyDone());
    }

    public void updateNDailyQuest(int n, Quest.Status status){
        if (dailyQuests.size() < n){
            return;
        }

        dailyQuests.get(n).setStatus(status);

        DailyQuestLitePalConnector dailyQuestLitePalConnector = new DailyQuestLitePalConnector();
        dailyQuestLitePalConnector.updateDailyQuests(userId, n, dailyQuests.get(n).getContent(), dailyQuests.get(n).getStatus().ordinal(), dailyQuests.get(n).getAlreadyDone());
    }

    public void updateCurveNum(int n){
        // draw a curve
        dailyQuests.get(1).updateAlreadyDone(n);
        // draw 100 curves
        dailyQuests.get(2).updateAlreadyDone(n);

        DailyQuestLitePalConnector dailyQuestLitePalConnector = new DailyQuestLitePalConnector();
        dailyQuestLitePalConnector.updateDailyQuests(userId, 1, dailyQuests.get(1).getContent(), dailyQuests.get(1).getStatus().ordinal(), dailyQuests.get(1).getAlreadyDone());
        dailyQuestLitePalConnector.updateDailyQuests(userId, 2, dailyQuests.get(2).getContent(), dailyQuests.get(2).getStatus().ordinal(), dailyQuests.get(2).getAlreadyDone());
    }

    public void updateMarkerNum(int n){
        // draw a marker
        dailyQuests.get(3).updateAlreadyDone(n);
        // draw 100 markers
        dailyQuests.get(4).updateAlreadyDone(n);

        DailyQuestLitePalConnector dailyQuestLitePalConnector = new DailyQuestLitePalConnector();
        dailyQuestLitePalConnector.updateDailyQuests(userId, 3, dailyQuests.get(3).getContent(), dailyQuests.get(3).getStatus().ordinal(), dailyQuests.get(3).getAlreadyDone());
        dailyQuestLitePalConnector.updateDailyQuests(userId, 4, dailyQuests.get(4).getContent(), dailyQuests.get(4).getStatus().ordinal(), dailyQuests.get(4).getAlreadyDone());
    }

    public int questFinished(Quest quest) {
        for (int i = 0; i < dailyQuests.size(); i++) {
            Quest dailyQuest = dailyQuests.get(i);
            if (quest.getContent().equals(dailyQuest.getContent())) {
                updateNDailyQuest(i, Quest.Status.Finished);
                return dailyQuest.getReward();
            }
        }
        return 0;
    }
}
