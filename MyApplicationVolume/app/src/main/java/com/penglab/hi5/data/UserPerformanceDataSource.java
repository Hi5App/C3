package com.penglab.hi5.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsUserPerformance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Yihang zhu 01/20/21
 */
public class UserPerformanceDataSource {
    private final MutableLiveData<Result> personalResult = new MutableLiveData<>();
    private final MutableLiveData<Result> leaderboardResult = new MutableLiveData<>();

    public LiveData<Result> getPersonalResult() {
        return personalResult;
    }
    public LiveData<Result> getLeaderBoardResult() {
        return  leaderboardResult;
    }

    public void getUserPerformance() {
        try {
            HttpUtilsUserPerformance.getUserPerformance(InfoCache.getAccount(), InfoCache.getToken(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    personalResult.postValue(new Result.Error(new Exception("Connect failed when get user performance")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            personalResult.postValue(new Result.Success<JSONObject>(jsonObject));
                        } else {
                            personalResult.postValue(new Result.Error(new Exception("Response from server is null !")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        personalResult.postValue(new Result.Error(new Exception("Fail to get user performance !")));
                    }
                }
            });
        } catch (Exception exception) {
            personalResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getUserPerformanceTopK(int k) {
        try {
            HttpUtilsUserPerformance.getUserPerformanceTopK(InfoCache.getAccount(), InfoCache.getToken(), k, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    leaderboardResult.postValue(new Result.Error(new Exception("Connect failed when get leaderboard")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            leaderboardResult.postValue(new Result.Success<JSONArray>(jsonArray));
                        } else {
                            leaderboardResult.postValue(new Result.Error(new Exception("Response from server is null")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        leaderboardResult.postValue(new Result.Error(new Exception("Fail to get leaderboard !")));
                    }
                }
            });
        } catch (Exception exception) {
            personalResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }
}