package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsCheckArbor;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Yihang zhu 01/04/21
 */
public class CheckArborDataSource {
    private final String TAG = "CheckArborDataSource";
    private final MutableLiveData<Result> result = new MutableLiveData<>();

    public LiveData<Result> getResult() {
        return result;
    }

    public void getCheckArborList(Boolean withChecked, int off, int limit) {
        try {
            HttpUtilsCheckArbor.getCheckArborList(InfoCache.getAccount(), InfoCache.getToken(), withChecked, off, limit, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect failed when get arbor list")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            String str = response.body().string();
                            JSONArray jsonArray = new JSONArray(str);
                            result.postValue(new Result.Success<JSONArray>(jsonArray));
                        } else {
                            result.postValue(new Result.Error(new Exception("Response from server is null !")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.postValue(new Result.Error(new Exception("Fail to get arbor list !")));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }
}
