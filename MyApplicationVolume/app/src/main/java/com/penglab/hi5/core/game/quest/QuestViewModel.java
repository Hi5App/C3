package com.penglab.hi5.core.game.quest;

import androidx.lifecycle.ViewModel;

import com.penglab.hi5.core.game.score.ScoreModel;
import com.penglab.hi5.data.UserInfoRepository;

/**
 * Created by Yihang zhu 12/30/21
 */
public class QuestViewModel extends ViewModel {
    private DailyQuestsModel dailyQuestsModel;
    private ScoreModel scoreModel;

    public QuestViewModel() {
        super();
        scoreModel = UserInfoRepository.getInstance().getScoreModel();
        dailyQuestsModel = UserInfoRepository.getInstance().getScoreModel().getDailyQuestsModel();
    }

    public void questFinished(Quest quest) {
        scoreModel.questFinished(quest);
    }

    public DailyQuestsModel getDailyQuestsModel() {
        return dailyQuestsModel;
    }

    public ScoreModel getScoreModel() {
        return scoreModel;
    }
}
