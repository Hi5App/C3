package com.example.datastore.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class User extends LitePalSupport {

    @Column(unique = true, defaultValue = "unknown")
    private String userid;

    @Column(defaultValue = "0")
    private int Score;





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
}
