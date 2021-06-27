package com.example.myapplication__volume.game;

public class RewardItem {
    private int num;
    private int received;

    public RewardItem(int num, int received){
        this.num = num;
        this.received = received;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }
}
