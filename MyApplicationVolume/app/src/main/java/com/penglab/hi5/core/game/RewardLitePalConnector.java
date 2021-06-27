package com.penglab.hi5.core.game;

import android.content.Context;
import android.util.Log;

import com.penglab.hi5.dataStore.database.Reward;
import com.penglab.hi5.dataStore.database.UserReward;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class RewardLitePalConnector {
    private String TAG = "RewardLitePalConnector";

    private static RewardLitePalConnector INSTANCE;

    private static Context mContext;

    private static String userId;

    public static RewardLitePalConnector getInstance(){
        if (INSTANCE == null){
            synchronized (RewardLitePalConnector.class) {
                if (INSTANCE == null)
                    INSTANCE = new RewardLitePalConnector();
            }
        }
        return INSTANCE;
    }

    public static void init(Context context){
        mContext = context;
    }

    public static void initUserId(String id){
        userId = id;
    }

    public List<Integer> getRewards(){
        List<UserReward> userRewards = LitePal.where("userid = ?", userId).find(UserReward.class);
        if (userRewards.size() > 1){
            Log.d(TAG, "Error in reward database");
            return null;
        } else if (userRewards.size() == 0){
            Log.d(TAG, "Not Founded");

            UserReward userReward = new UserReward();
            userReward.setUserId(userId);
            List<Reward> rewards = new ArrayList<>();
            for (int i = 0; i < 2; i++){
                Reward reward = new Reward();
                reward.setNum(i);
                reward.setReceived(0);
                rewards.add(reward);
            }
            userReward.setRewards(rewards);
            userReward.save();

            List<Integer> list = new ArrayList<>();

            for (int i = 0; i < rewards.size(); i++){
                list.add(rewards.get(i).getReceived());
            }
            return list;
        } else {
            Log.d(TAG, "Founded");
            List<Reward> rewards = userRewards.get(0).getRewards();
            Log.d(TAG, Integer.toString(rewards.size()));

            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < rewards.size(); i++){
                list.add(rewards.get(i).getReceived());
            }
            return list;
        }
    }

    public void updateRewards(int num, int received){
        List<UserReward> userRewards = LitePal.where("userid = ?", userId).find(UserReward.class);
        if (userRewards.size() != 1){
            Log.d(TAG, "Error in reward database");
            return;
        }

        List<Reward> rewards = userRewards.get(0).getRewards();
        rewards.get(num).setReceived(received);
        rewards.get(num).save();
        userRewards.get(0).save();
    }
}
