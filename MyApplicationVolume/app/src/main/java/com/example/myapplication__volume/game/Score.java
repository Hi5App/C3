package com.example.myapplication__volume.game;

import android.content.Context;
import android.widget.Toast;

import com.example.datastore.database.User;

import org.litepal.LitePal;

import java.util.List;

public class Score{

    private static Score INSTANCE;

    private static String userid;

    private static Context mContext;

    public static void init(Context mContext){
        Score.mContext = mContext;
    }

    public static void initUser(String userid){
        Score.userid = userid;
    }


    /**
     * 获取Score实例 ,单例模式
     */
    public static Score getInstance(){
        if (INSTANCE == null){
            synchronized (Score.class){
                if (INSTANCE == null){
                    INSTANCE = new Score();
                }
            }
        }
        return INSTANCE;
    }


    public int queryScore(){
        if (Score.userid != null)
            return queryScore(Score.userid);
        else
            return -1;
    }


    public int queryScore(String userid){
        List<User> users = LitePal.select("userid", userid).find(User.class);
        if (users.size() != 1 ) {
            Toast.makeText(mContext, "Something wrong with database !", Toast.LENGTH_SHORT).show();
            return -1;
        }else {
            return users.get(0).getScore();
        }
    }



    public boolean updateScore(int score){
        return updateScore(Score.userid, score);
    }


    public boolean updateScore(String userid, int score){
        List<User> users = LitePal.select("userid", userid).find(User.class);
        if (users.size() == 0){
            User user = new User();
            user.setUserid(userid);
            user.setScore(score);
            user.save();
        }else if(users.size() == 1){
            User user = users.get(0);
            user.setScore(score);
            user.save();
        }else {
            Toast.makeText(mContext, "Something wrong with database !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }



    public boolean addScore(String userid, int score){
        return addScore(score);
    }


    public boolean addScore(int score){
        List<User> users = LitePal.select("userid", userid).find(User.class);
        if (users.size() == 0){
            User user = new User();
            user.setUserid(userid);
            user.setScore(score);
            user.save();
        }else if(users.size() == 1){
            User user = users.get(0);
            user.setScore(user.getScore() + score);
            user.save();
        }else {
            Toast.makeText(mContext, "Something wrong with database !", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
