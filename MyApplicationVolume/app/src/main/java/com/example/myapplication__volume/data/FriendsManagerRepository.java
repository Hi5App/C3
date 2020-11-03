package com.example.myapplication__volume.data;

import android.util.Log;

import com.example.myapplication__volume.data.model.LoggedInUser;

public class FriendsManagerRepository {
    private static volatile FriendsManagerRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private FriendsManagerRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static FriendsManagerRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new FriendsManagerRepository(dataSource);
        }
        return instance;
    }

    public String addFriends(String username, String peer) {
        // handle addFriends
        Log.d("FriendsManagerRep", "addFriends");
        return dataSource.addFriends(username, peer);
//        if (result instanceof Result.Success) {
//        }
//        return result;
    }

    public String queryFriends(String username) {
        // handle addFriends
        Log.d("FriendsManagerRep", "queryFriends");
        return dataSource.queryFriends(username);
//        if (result instanceof Result.Success) {
//        }
//        return result;
    }
}
