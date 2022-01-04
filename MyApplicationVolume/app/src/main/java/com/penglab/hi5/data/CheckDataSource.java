package com.penglab.hi5.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsCheck;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Yihang zhu 01/04/21
 */
public class CheckDataSource {
    private final MutableLiveData<Result> result = new MutableLiveData<>();

    public LiveData<Result> getResult() {
        return result;
    }

    public void uploadCheckResult(String arborName, int checkResult) {
        try {
            HttpUtilsCheck.checkWithOkHttp(arborName, InfoCache.getAccount(), InfoCache.getToken(), InfoCache.getAccount(), checkResult, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Fail to send check result")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    result.postValue(new Result.Success("Success to send check result"));
                }
            });
        } catch (Exception exception) {

        }
    }
}
