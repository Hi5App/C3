package com.example.myapplication__volume.game;

import android.content.Context;

import com.example.datastore.database.User;

import org.litepal.LitePal;

import java.util.List;

import static com.example.myapplication__volume.MainActivity.Toast_in_Thread_static;

public class ScoreLitePalConnector {

    private static ScoreLitePalConnector INSTANCE;

    private static String userid;

    private static Context mContext;

    public static void init(Context mContext){
        ScoreLitePalConnector.mContext = mContext;
    }

    public static void initUser(String userid){
        ScoreLitePalConnector.userid = userid;
    }


    /**
     * 获取ScoreLitePalConnector实例 ,单例模式
     */
    public static ScoreLitePalConnector getInstance(){
        if (INSTANCE == null){
            synchronized (ScoreLitePalConnector.class){
                if (INSTANCE == null){
                    INSTANCE = new ScoreLitePalConnector();
                }
            }
        }
        return INSTANCE;
    }


    public int queryScore(){
        if (ScoreLitePalConnector.userid != null)
            return queryScore(ScoreLitePalConnector.userid);
        else
            return -1;
    }


    public int queryScore(String userid){
        List<User> users = LitePal.where("userid = ?", userid).find(User.class);
        if (users.size() != 1 ) {
            Toast_in_Thread_static("Something wrong with database !");
            return -1;
        }else {
            return users.get(0).getScore();
        }
    }

    public void initScoreFromLitePal(String userid){
        List<User> users = LitePal.where("userid = ?", userid).find(User.class);
        if (users.size() != 1 ) {
            Toast_in_Thread_static("Something wrong with database !");
        } else {
            User user = users.get(0);
            Score score = Score.getInstance();
            score.setId(userid);
            score.setCurveNum(user.getCurveNum());
            score.setMarkerNum(user.getMarkerNum());
            score.setLastLoginDay(user.getLastLoginDay());
            score.setLastLoginYear(user.getLastLoginYear());
            score.setCurveNumToday(user.getCurveNumToday());
            score.setMarkerNumToday(user.getMarkerNumToday());
            score.setEditImageNum(user.getEditImageNum());
            score.setEditImageNumToday(user.getEditImageNumToday());
        }
    }



    public boolean updateScore(int score){
        return updateScore(ScoreLitePalConnector.userid, score);
    }


    public boolean updateScore(String userid, int score){
        List<User> users = LitePal.where("userid = ?", userid).find(User.class);
        if (users.size() == 0){
            User user = new User();
            user.setUserid(userid);
            user.setScore(score);
            user.save();
        }else if(users.size() == 1){
            User user = new User();
            user.setScore(score);
            user.updateAll("userid = ?", userid);
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
        List<User> users = LitePal.where("userid = ?", userid).find(User.class);
        if (users.size() == 0){
            User user = new User();
            user.setUserid(userid);
            user.setScore(score);
            user.save();
        }else if(users.size() == 1){
            User user = new User();
            user.setScore(user.getScore() + score);
            user.updateAll("userid = ?", userid);
        }else {
            Toast_in_Thread_static("Something wrong with database !");
            return false;
        }
        return true;
    }


}
