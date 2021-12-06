package com.penglab.hi5.data.model.user;

/**
 * Data class that captures user information for logged in users retrieved from UserInfoRepository
 * Created by Jackiexing on 12/5/21
 */
public class LoggedInUser {

    private boolean logged;
    private String userId;
    private String nickName;
    private String email;
    private int Score;

    public LoggedInUser(boolean logged, String userId, String nickName, String email) {
        this.logged = logged;
        this.userId = userId;
        this.nickName = nickName;
        this.email = email;
    }

    public boolean isLogged() {
        return logged;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public int getScore() {
        return Score;
    }
}
