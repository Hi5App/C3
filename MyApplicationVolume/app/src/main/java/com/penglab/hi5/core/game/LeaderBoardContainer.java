package com.penglab.hi5.core.game;

import java.util.ArrayList;

public class LeaderBoardContainer {

    private static LeaderBoardContainer INSTANCE;

    private ArrayList<LeaderBoardItem> leaderBoardItems = new ArrayList<>();

    public static LeaderBoardContainer getInstance(){
        if (INSTANCE == null) {
            synchronized (LeaderBoardContainer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LeaderBoardContainer();
                }
            }
        }
        return INSTANCE;
    }

    public ArrayList<LeaderBoardItem> getLeaderBoardItems() {
        return leaderBoardItems;
    }

    public void setLeaderBoardItems(ArrayList<LeaderBoardItem> leaderBoardItems) {
        this.leaderBoardItems = leaderBoardItems;
    }
}
