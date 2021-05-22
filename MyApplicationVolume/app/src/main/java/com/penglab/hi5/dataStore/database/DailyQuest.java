package com.penglab.hi5.dataStore.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class DailyQuest extends LitePalSupport {

    @Column(defaultValue = "unknown")
    private String content;

    @Column(defaultValue = "0")
    private int status;

    @Column(defaultValue = "0")
    private int reward;

    @Column(defaultValue = "0")
    private int alreadyDone;

    @Column(defaultValue = "0")
    private int toBeDone;

    public int getAlreadyDone() {
        return alreadyDone;
    }

    public void setAlreadyDone(int alreadyDone) {
        this.alreadyDone = alreadyDone;
    }

    public int getToBeDone() {
        return toBeDone;
    }

    public void setToBeDone(int toBeDone) {
        this.toBeDone = toBeDone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
