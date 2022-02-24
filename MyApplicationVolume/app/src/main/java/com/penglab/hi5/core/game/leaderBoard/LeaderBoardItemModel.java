package com.penglab.hi5.core.game.leaderBoard;

import java.util.ArrayList;

/**
 * Created by Yihang zhu 01/11/21
 */
public class LeaderBoardItemModel {
    private static LeaderBoardItemModel INSTANCE;

    private ArrayList<LeaderBoardItem> leaderBoardItems = new ArrayList<>();

    public static LeaderBoardItemModel getInstance(){
        if (INSTANCE == null) {
            synchronized (LeaderBoardItemModel.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LeaderBoardItemModel();
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
