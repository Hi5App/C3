package com.penglab.hi5.dataStore.database;

import com.netease.nim.uikit.common.util.collection.CollectionUtil;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class UserDailyQuest extends LitePalSupport {

    private long id;

    @Column(unique = true, defaultValue = "")
    private String userId;

    private List<DailyQuest> dailyQuests;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<DailyQuest> getDailyQuests(){
        String linkId = this.getClass().getSimpleName().toLowerCase();
        List<DailyQuest> list = LitePal.where(linkId + "_id=?", String.valueOf(id)).find(DailyQuest.class);
        if (CollectionUtil.isEmpty(list)){
            dailyQuests = null;
        } else {
            dailyQuests = list;
        }
        return dailyQuests;
    }

    public void setDailyQuests(List<DailyQuest> dailyQuests){
        for (int i = 0; i < dailyQuests.size(); i++){
            dailyQuests.get(i).save();
        }
        this.dailyQuests = dailyQuests;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
