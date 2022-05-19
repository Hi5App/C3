package com.penglab.hi5.data.dataStore.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {

    @Column(unique = true, defaultValue = "unknown")
    private String userid;

    @Column(defaultValue = "0")
    private int Score;

    @Column(defaultValue = "0")
    private int curveNum;

    @Column(defaultValue = "0")
    private int somaNum;

    @Column(defaultValue = "0")
    private int lastLoginYear;

    @Column(defaultValue = "0")
    private int lastLoginDay;

    @Column(defaultValue = "0")
    private int curveNumToday;

    @Column(defaultValue = "0")
    private int somaNumToday;

    @Column(defaultValue = "0")
    private int editImageNum;

    @Column(defaultValue = "0")
    private int editImageNumToday;



    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public void setCurveNum(int curveNum) {
        this.curveNum = curveNum;
    }

    public void setSomaNum(int somaNum) {
        this.somaNum = somaNum;
    }

    public void setLastLoginYear(int lastLoginYear) {
        this.lastLoginYear = lastLoginYear;
    }

    public void setLastLoginDay(int lastLoginDay) {
        this.lastLoginDay = lastLoginDay;
    }


    public void setCurveNumToday(int curveNumToday) {
        this.curveNumToday = curveNumToday;
    }

    public void setSomaNumToday(int somaNumToday) {this.somaNumToday = somaNumToday; }

    public void setEditImageNumToday(int editImageNumToday) { this.editImageNumToday = editImageNumToday; }

    public void setEditImageNum(int editImageNum) {
        this.editImageNum = editImageNum;
    }


    public int getCurveNum() {
        return curveNum;
    }

    public int getSomaNum() { return somaNum; }

    public int getLastLoginYear() {
        return lastLoginYear;
    }

    public int getLastLoginDay() {
        return lastLoginDay;
    }

    public int getCurveNumToday() {
        return curveNumToday;
    }

    public int getSomaNumToday() {
        return somaNumToday;
    }

    public int getEditImageNum() {
        return editImageNum;
    }

    public int getEditImageNumToday() {
        return editImageNumToday;
    }

}

