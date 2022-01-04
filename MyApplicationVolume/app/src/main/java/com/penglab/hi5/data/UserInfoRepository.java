package com.penglab.hi5.data;

import android.util.Log;

import com.penglab.hi5.core.game.Score;
import com.penglab.hi5.core.game.score.ScoreModel;
import com.penglab.hi5.data.model.user.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class UserInfoRepository {

    private static volatile UserInfoRepository instance;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    private ScoreModel scoreModel = new ScoreModel();

    // private constructor : singleton access
    private UserInfoRepository() {
    }

    public static UserInfoRepository getInstance() {
        if (instance == null) {
            synchronized (UserInfoRepository.class){
                if (instance == null){
                    instance = new UserInfoRepository();
                }
            }
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
    }

    public LoggedInUser getUser() {
        return user;
    }

    public void setLoggedInUser(LoggedInUser user) {
        Log.e("UserInfoRepository", "userId: " + user.getUserId());
        Log.e("UserInfoRepository", "nickName: " + user.getNickName());
        Log.e("UserInfoRepository", "email: " + user.getEmail());
        this.user = user;
        scoreModel.setId(user.getUserId());
        scoreModel.initFromLitePal();
    }

    public ScoreModel getScoreModel() {
        return scoreModel;
    }
}
