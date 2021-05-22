package com.penglab.hi5.core.game;

public class LeaderBoardItem {
    private String nickname;
    private int score;
    private String account;

    public LeaderBoardItem(String account, String nickname, int score){
        this.account = account;
        this.nickname = nickname;
        this.score = score;
    }

    public LeaderBoardItem(String account, int score){
        this.account = account;
        this.score = score;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
