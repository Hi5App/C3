package com.example.myapplication__volume.game;

/*
基本：
Curve 2
Marker 1

成就：
1.	首次登陆       100
2.	首次打开图像   100
3.	首次划线       100
4.	首次点点       100
5.	划线100次      500
6.	点点100次      300
7.

每日任务：
1.	首次登陆       20
2.	首次打开图像   20
3.	首次划线       20
4.	首次点点       20
5.

*/

import android.content.Context;

import com.example.datastore.database.DailyQuest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Score {

    private static Score INSTANCE;
    private static Context mContext;

    private static String id;
    private int score;
    private int curveNum;
    private int markerNum;
    private int lastLoginYear;
    private int lastLoginDay;
    private int curveNumToday;
    private int markerNumToday;
    private int editImageNum;
    private int editImageNumToday;

    private int [] achievementsScore = { -1, 100, 100, 100, 100, 500, 300 };
    private int [] dailyQuestsScore = { -1, 20, 20, 20, 20 };

//    private Quest[] dailyQuests = {
//            new Quest("Login", 0, 1, 20),
//            new Quest("Open a image", 0, 1, 20),
//            new Quest("Draw a line", 0, 1, 20),
//            new Quest("Draw a marker", 0, 1, 20),
//    };

    ArrayList<Quest> dailyQuests;

    public Score(){

    }

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

    public static void init(Context context){
        Score.mContext = context;
    }

    public static void initId(String id){
        Score.id = id;
    }

    public void initFromLitePal(){
        ScoreLitePalConnector scoreLitePalConnector = ScoreLitePalConnector.getInstance();
        scoreLitePalConnector.initScoreFromLitePal(id);

        DailyQuestsContainer dailyQuestsContainer = DailyQuestsContainer.getInstance();
        setDailyQuests(dailyQuestsContainer.getDailyQuests());

//        DailyQuestLitePalConnector dailyQuestLitePalConnector = DailyQuestLitePalConnector.getInstance();
//        setDailyQuests(dailyQuestLitePalConnector.getDailyQuests(id));

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int date = calendar.get(Calendar.DAY_OF_YEAR);
        if (year > lastLoginYear || (year == lastLoginYear && date > lastLoginDay)){
            dailyQuestsContainer.updateNDailyQuest(0, 1);
//            dailyQuests[0].updateAlreadyDone(1);
        }
    }

    public void updateLitePalDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int date = calendar.get(Calendar.DAY_OF_YEAR);


    }

    public void drawACurve(){
        if (curveNum == 0) {
            achievementFinished(1);
        }

        if (curveNumToday == 0) {
            dailyQuestFinished(3);
        }

        if (curveNum == 99) {
            achievementFinished(5);
        }

        curveNum += 1;
        curveNumToday += 1;
        score += 2;
    }

    public void pinpoint(){
        if (markerNum == 0) {
            achievementFinished(2);
        }

        if (markerNumToday == 0) {
            dailyQuestFinished(4);
        }

        if (markerNum == 99) {
            achievementFinished(6);
        }

        markerNum += 1;
        markerNumToday += 1;
        score += 1;
    }

    public void openImage(){
        if (editImageNum == 0) {
            achievementFinished(1);
        }

        if (editImageNumToday == 0) {
            dailyQuestFinished(1);
        }

        editImageNum += 1;
        editImageNumToday += 1;
    }

    public void achievementFinished(int i){
        score += achievementsScore[i];
    }

    public void dailyQuestFinished(int i){
        score += dailyQuestsScore[i];
    }

    public static Context getmContext() {
        return mContext;
    }

    public static void setmContext(Context mContext) {
        Score.mContext = mContext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCurveNum() {
        return curveNum;
    }

    public void setCurveNum(int curveNum) {
        this.curveNum = curveNum;
    }

    public int getMarkerNum() {
        return markerNum;
    }

    public void setMarkerNum(int markerNum) {
        this.markerNum = markerNum;
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

    public int getMarkerNumToday() {
        return markerNumToday;
    }

    public void setMarkerNumToday(int markerNumToday) {
        this.markerNumToday = markerNumToday;
    }

    public int getEditImageNum() {
        return editImageNum;
    }

    public void setEditImageNum(int editImageNum) {
        this.editImageNum = editImageNum;
    }

    public int getEditImageNumToday() {
        return editImageNumToday;
    }

    public void setEditImageNumToday(int editImageNumToday) {
        this.editImageNumToday = editImageNumToday;
    }

    public int[] getAchievementsScore() {
        return achievementsScore;
    }

    public void setAchievementsScore(int[] achievementsScore) {
        this.achievementsScore = achievementsScore;
    }

    public int[] getDailyQuestsScore() {
        return dailyQuestsScore;
    }

    public void setDailyQuestsScore(int[] dailyQuestsScore) {
        this.dailyQuestsScore = dailyQuestsScore;
    }

    public ArrayList<Quest> getDailyQuests() {
        return dailyQuests;
    }

    public void setDailyQuests(ArrayList<Quest> dailyQuests) {
        this.dailyQuests = dailyQuests;
    }
}
