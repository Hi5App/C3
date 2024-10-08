package com.penglab.hi5.core.game.quest;

import androidx.databinding.BaseObservable;

import com.netease.nim.uikit.common.framework.infra.Observable;

public class Quest extends BaseObservable {
    public enum Status
    {
        UnFinished, Pending, Finished
    }

    public final String NOT_FINISHED_BUTTON_TEXT = "RECEIVE";
    public final String FINISHED_BUTTON_TEXT = "FINISHED";

    private String content = "";
    private Status status = Status.UnFinished;

    private int alreadyDone = 0;
    private int toBeDone = 0;

    private int reward = 0;

    Quest(){

    }

    Quest(String content, Status status, int alreadyDone, int toBeDone, int reward){
        this.content = content;
        this.status = status;
        this.alreadyDone = alreadyDone;
        this.toBeDone = toBeDone;
        this.reward = reward;
    }

    Quest(String content, int statusId, int alreadyDone, int toBeDone, int reward){
        this.content = content;
        this.status = Status.values()[statusId];
        this.alreadyDone = alreadyDone;
        this.toBeDone = toBeDone;
        this.reward = reward;
    }

    Quest(String content, int alreadyDone, int toBeDone, int reward){
        this.content = content;
        this.alreadyDone = alreadyDone;
        this.toBeDone = toBeDone;
        this.reward = reward;
    }

    public void updateAlreadyDone(int done){
        if (status == Status.UnFinished) {
            alreadyDone = done;
            if (alreadyDone >= toBeDone) {
                status = Status.Pending;
            }
        }
    }

    public void updateAlreadyDone(int done, Status status){
        alreadyDone = done;
        this.status = status;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setStatus(Status status){
        this.status = status;
        notifyChange();
    }

    public void setAlreadyDone(int alreadyDone) {
        this.alreadyDone = alreadyDone;
    }

    public void setToBeDone(int toBeDone) {
        this.toBeDone = toBeDone;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getContent() {
        return content;
    }

    public Status getStatus() {
        return status;
    }

    public int getAlreadyDone() {
        return alreadyDone;
    }

    public int getToBeDone() {
        return toBeDone;
    }

    public int getReward() {
        return reward;
    }

}
