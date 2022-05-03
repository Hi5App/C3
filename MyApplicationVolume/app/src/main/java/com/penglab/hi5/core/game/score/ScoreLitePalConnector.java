package com.penglab.hi5.core.game.score;

import static com.penglab.hi5.core.MainActivity.Toast_in_Thread_static;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.core.game.Score;
import com.penglab.hi5.data.dataStore.database.User;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by Yihang zhu 12/29/21
 */
public class ScoreLitePalConnector {

    private String TAG = "ScoreLitePalConnector";

    private String userId;

    public ScoreLitePalConnector(String userId) {
        this.userId = userId;
    }

    public int queryScore(){
        if (userId != null)
            return queryScore(userId);
        else
            return -1;
    }


    private int queryScore(String userid){
        List<User> users = LitePal.where("userid = ?", userid).find(User.class);
        if (users.size() != 1 ) {
            Toast_in_Thread_static("Something wrong with database !");
            return -1;
        }else {
            return users.get(0).getScore();
        }
    }

    public ScoreModel getScoreModelFromLitePal() {
        List<User> users = LitePal.where("userid = ?", userId).find(User.class);
        ScoreModel scoreModel = new ScoreModel();
        if (users.size() > 1 ) {
            return null;
        } else if (users.size() == 0){
            Log.d(TAG, "Not found int database. Create a new one");
            User user = new User();
            user.setUserid(userId);
            user.setCurveNum(0);
            user.setMarkerNum(0);
            user.setCurveNumToday(0);
            user.setMarkerNumToday(0);
            user.setEditImageNum(0);
            user.setEditImageNumToday(0);
            user.setLastLoginDay(0);
            user.setLastLoginYear(0);
            user.setScore(0);
            user.save();

            scoreModel.setId(userId);
            scoreModel.setScore(user.getScore());
            scoreModel.setCurveNum(user.getCurveNum());
            scoreModel.setMarkerNum(user.getMarkerNum());
            scoreModel.setLastLoginDay(user.getLastLoginDay());
            scoreModel.setLastLoginYear(user.getLastLoginYear());
            scoreModel.setCurveNumToday(user.getCurveNumToday());
            scoreModel.setMarkerNumToday(user.getMarkerNumToday());
            scoreModel.setEditImageNum(user.getEditImageNum());
            scoreModel.setEditImageNumToday(user.getEditImageNumToday());
        } else {
            User user = users.get(0);
            scoreModel.setId(userId);
            scoreModel.setScore(user.getScore());
            scoreModel.setCurveNum(user.getCurveNum());
            scoreModel.setMarkerNum(user.getMarkerNum());
            scoreModel.setLastLoginDay(user.getLastLoginDay());
            scoreModel.setLastLoginYear(user.getLastLoginYear());
            scoreModel.setCurveNumToday(user.getCurveNumToday());
            scoreModel.setMarkerNumToday(user.getMarkerNumToday());
            scoreModel.setEditImageNum(user.getEditImageNum());
            scoreModel.setEditImageNumToday(user.getEditImageNumToday());
        }
        return scoreModel;
    }



    public boolean updateScore(int score){
        return updateScore(userId, score);
    }


    private boolean updateScore(String userid, int score) {
        List<User> users = LitePal.where("userid = ?", userid).find(User.class);
        if (users.size() == 0){
            User user = new User();
            user.setUserid(userid);
            user.setScore(score);
            user.save();
        }else if(users.size() == 1) {
            User user = new User();
            user.setScore(score);
            user.updateAll("userid = ?", userid);
        }else {
            Toast_in_Thread_static("Something wrong with database !");
            return false;
        }
        return true;
    }

    public boolean updateEditImageNumToday(MutableLiveData<Integer> editImageNumToday) {
        Log.e(TAG,"updateEditImageNumToday"+editImageNumToday.getValue().toString());
        return updateEditImageNumToday(userId,editImageNumToday);

    }

    private boolean updateEditImageNumToday(String userid, MutableLiveData<Integer>  editImageNumToday) {
        List<User> users = LitePal.where("userid = ?", userid).find(User.class);
        if(users.size() == 0) {
            User user = new User();
            user.setUserid(userid);
            user.setEditImageNumToday(editImageNumToday.getValue());
            Log.e(TAG,"updateEditImageNumToday_user"+editImageNumToday.getValue().toString());
            user.save();
        } else if(users.size() ==1) {
            User user = new User();
            user.setEditImageNumToday(editImageNumToday.getValue());
            user.updateAll("userid=?",userid);
        }else {
            Toast_in_Thread_static("Something wrong with database !");
            return false;
        }
        return true;
    }


    public boolean addScore(String userid, int score){
        return addScore(score);
    }


    public boolean addScore(int score){
        List<User> users = LitePal.where("userid = ?", userId).find(User.class);
        if (users.size() == 0){
            User user = new User();
            user.setUserid(userId);
            user.setScore(score);
            user.save();
        }else if(users.size() == 1){
            User user = new User();
            user.setScore(user.getScore() + score);
            user.updateAll("userid = ?", userId);
        }else {
            Toast_in_Thread_static("Something wrong with database !");
            return false;
        }
        return true;
    }

}
