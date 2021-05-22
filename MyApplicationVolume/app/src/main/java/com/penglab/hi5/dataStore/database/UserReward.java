package com.penglab.hi5.dataStore.database;

import com.netease.nim.uikit.common.util.collection.CollectionUtil;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class UserReward extends LitePalSupport {

    private long id;

    @Column(unique = true, defaultValue = "unknown")
    private String userId;

    private List<Reward> rewards;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Reward> getRewards() {
        String linkId = this.getClass().getSimpleName().toLowerCase();
        List<Reward> list = LitePal.where(linkId + "_id=?", String.valueOf(id)).find(Reward.class);
        if (CollectionUtil.isEmpty(list)){
            rewards = null;
        } else {
            rewards = list;
        }
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        for (int i = 0; i < rewards.size(); i++){
            rewards.get(i).save();
        }
        this.rewards = rewards;
    }
}
