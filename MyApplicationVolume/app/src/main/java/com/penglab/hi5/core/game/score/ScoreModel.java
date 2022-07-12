package com.penglab.hi5.core.game.score;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.game.RewardLitePalConnector;
import com.penglab.hi5.core.game.quest.DailyQuestsModel;
import com.penglab.hi5.core.game.quest.Quest;
import com.penglab.hi5.core.ui.userProfile.PhotoUtils;
import com.penglab.hi5.data.dataStore.database.User;

import org.litepal.LitePal;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Yihang zhu 12/29/21
 */
public class ScoreModel {
    private String id;
    private MutableLiveData<Integer> score = new MutableLiveData<>();
    private int curveNum;
//    private int markerNum;
    private int lastLoginYear;
    private int lastLoginDay;
    private int curveNumToday;
//    private int markerNumToday;

    private MutableLiveData<Integer> somaNum = new MutableLiveData<>();
    private MutableLiveData<Integer> somaNumToday = new MutableLiveData<>();
    private MutableLiveData<Integer> editImageNum = new MutableLiveData<>();
    private MutableLiveData<Integer> editImageNumToday = new MutableLiveData<>();

    private DailyQuestsModel dailyQuestsModel;

    public ScoreModel() {
        dailyQuestsModel = new DailyQuestsModel();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        dailyQuestsModel.setUserId(id);
    }

    public MutableLiveData<Integer> getObservableScore() {
        return score;
    }

    public int getScore() {
        return score.getValue();
    }

    public void setScore(int score) {
        this.score.setValue(score);
    }

    public int getCurveNum() {
        return curveNum;
    }

    public void setCurveNum(int curveNum) {
        this.curveNum = curveNum;
    }


    public int getLastLoginYear() {
        return lastLoginYear;
    }

    public void setLastLoginYear(int lastLoginYear) {
        this.lastLoginYear = lastLoginYear;
    }

    public int getLastLoginDay() {
        return lastLoginDay;
    }

    public void setLastLoginDay(int lastLoginDay) {
        this.lastLoginDay = lastLoginDay;
    }

    public int getCurveNumToday() {
        return curveNumToday;
    }

    public void setCurveNumToday(int curveNumToday) {
        this.curveNumToday = curveNumToday;
    }


    public void setSomaNum (int somaNum){ this.somaNum.setValue(somaNum);}

    public void setSomaNumToday(int somaNumToday) { this.somaNumToday.setValue(somaNumToday); }

    public int getSomaNumToday() {return somaNumToday.getValue();}

    public MutableLiveData<Integer> getObserveSomaNumToday(){ return somaNumToday; }



    public void setEditImageNum(int editImageNum) {
        this.editImageNum.setValue(editImageNum);
    }

    public void setEditImageNumToday(int editImageNumToday) { this.editImageNumToday.setValue(editImageNumToday); }

    public int getEditImageNumToday() { return editImageNumToday.getValue(); }

    public MutableLiveData<Integer> getObserveEditImageToday(){ return editImageNumToday; }



    public DailyQuestsModel getDailyQuestsModel() {
        return dailyQuestsModel;
    }

    public void setDailyQuestsModel(DailyQuestsModel dailyQuestsModel) {
        this.dailyQuestsModel = dailyQuestsModel;
    }

    public boolean initFromLitePal(){
        boolean result = true;
        ScoreLitePalConnector scoreLitePalConnector = new ScoreLitePalConnector(id);
        ScoreModel scoreModel = scoreLitePalConnector.getScoreModelFromLitePal();

        dailyQuestsModel.initFromLitePal();

        RewardLitePalConnector.initUserId(id);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int date = calendar.get(Calendar.DAY_OF_YEAR);
        scoreLitePalConnector.updateTime(year, date);

//        Log.d("initfromlitePal", "" + scoreModel.editImageNumToday.getValue());
//        Log.d("lastloginday", ""+ scoreModel.lastLoginDay + " " + scoreModel.lastLoginYear);
//        Log.d("today", "" + date + " " + year);

        if (year > scoreModel.lastLoginYear || (year == scoreModel.lastLoginYear && date > scoreModel.lastLoginDay)){
//            Log.d("initfromlitepal", "newday");
            dailyQuestsModel.updateNDailyQuest(0, 1);
            scoreModel.editImageNumToday.setValue(0);
            scoreModel.somaNumToday.setValue(0);
            scoreLitePalConnector.updateEditImageNumToday(scoreModel.editImageNumToday);
            scoreLitePalConnector.updateSomaNumToday(scoreModel.somaNumToday);
        }
        if (scoreModel == null) {
            result = false;
        } else {
            updateWithNewScoreModel(scoreModel);
        }

        return result;
    }

    public void drawACurve(){
        curveNum += 1;
        curveNumToday += 1;
        addScore(ScoreRule.getScorePerCurve());

        dailyQuestsModel.updateCurveNum(curveNumToday);

        User user = new User();
        user.setScore(score.getValue());
        user.setCurveNum(curveNum);
        user.setCurveNumToday(curveNumToday);
        user.updateAll("userid = ?", id);
    }

//    public void pinpoint(){
////        markerNum += 1;
////        markerNumToday += 1;
////        markerNum.setValue(markerNum.getValue()+1);
////        markerNumToday.setValue(markerNumToday.getValue()+1);
//        addScore(ScoreRule.getScorePerPinPoint());
//
////        dailyQuestsModel.updateMarkerNum(markerNumToday.getValue());
//
//        User user = new User();
//        user.setScore(score.getValue());
////        user.setMarkerNum(markerNum.getValue());
////        user.setMarkerNumToday(markerNumToday.getValue());
//        user.updateAll("userid = ?", id);
//    }

    public void pinpointSoma (int somaNumSize) {
        addScore(ScoreRule.getScorePerSoma(somaNumSize));
        somaNumToday.setValue(somaNumToday.getValue()+somaNumSize);
        Log.e("somaNumToday",""+somaNumToday.getValue());
        User user = new User();
        user.setScore(score.getValue());
        user.setSomaNumToday(somaNumToday.getValue());
        List<User> users = LitePal.where("userid = ?", id).find(User.class);

        user.updateAll("userid = ?", id);

    }

    public void getScorePerRewardLevel (int level){

        addScore(ScoreRule.getScorePerRewardLevel()*level);
        User user = new User();
        user.setScore(score.getValue());
        user.updateAll("userid = ?", id);

    }

    public void getScorePerGuessMusic (){
        addScore(ScoreRule.getScorePerGuessMusic());
        User user = new User();
        user.setScore(score.getValue());
        user.updateAll("userid = ?", id);
    }


    public void finishAnImage(){
        addScore(ScoreRule.getScorePerImage());
        editImageNum.setValue(editImageNum.getValue()+1);
        editImageNumToday.setValue(editImageNumToday.getValue()+1);
        Log.e("editImageNumToday",editImageNumToday.getValue().toString());

        User user = new User();
        user.setScore(score.getValue());
        user.setEditImageNum(editImageNum.getValue());

        user.setEditImageNumToday(editImageNumToday.getValue());
        user.updateAll("userid = ?", id);
    }

    public void addScore(int s){
        score.setValue(score.getValue() + s);

        User user = new User();
        user.setScore(score.getValue());
        user.updateAll("userid = ?", id);
    }

    private void updateWithNewScoreModel(ScoreModel scoreModel) {
        this.score = scoreModel.score;
        this.curveNum = scoreModel.curveNum;
        this.somaNumToday = scoreModel.somaNumToday;
        this.curveNumToday = scoreModel.curveNumToday;
        this.lastLoginDay = scoreModel.lastLoginDay;
        this.lastLoginYear = scoreModel.lastLoginYear;
        this.editImageNum = scoreModel.editImageNum;
        this.editImageNumToday = scoreModel.editImageNumToday;
    }

    public void questFinished(Quest quest) {
        int rewardScore = dailyQuestsModel.questFinished(quest);
        addScore(rewardScore);
    }
}
