package com.penglab.hi5.core.game.leaderBoard;

import androidx.lifecycle.ViewModel;

import java.util.List;

/**
 * Created by Yihang zhu 01/11/21
 */
public class LeaderBoardViewModel extends ViewModel {
    private LeaderBoardItemModel leaderBoardItemModel;

    public LeaderBoardViewModel(LeaderBoardItemModel leaderBoardItemModel) {
        this.leaderBoardItemModel = leaderBoardItemModel;
    }

    public List<LeaderBoardItem> getLeaderBoardItemList() {
        return leaderBoardItemModel.getLeaderBoardItems();
    }
}
