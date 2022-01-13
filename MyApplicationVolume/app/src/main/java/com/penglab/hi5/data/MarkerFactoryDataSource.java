package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsSoma;
import com.penglab.hi5.core.net.HttpUtilsUser;
import com.penglab.hi5.data.model.img.PotentialSomaInfo;
import com.penglab.hi5.data.model.user.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Jackiexing on 01/11/21
 */
public class MarkerFactoryDataSource {
    private final String TAG = "MarkerFactoryDataSource";
    private final MutableLiveData<Result> result = new MutableLiveData<>();
    private String responseData;

    public LiveData<Result> getResult() {
        return result;
    }

    public void getPotentialLocation(){
        try {
            HttpUtilsSoma.getPotentialLocationWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect Failed When Login")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    responseData = response.body().string();
                    Log.e(TAG, "responseData: " + responseData);

                    if (responseCode == 200) {
                        try {
                            JSONObject potentialLocation = new JSONObject(responseData);
                            JSONObject loc = potentialLocation.getJSONObject("loc");

                            PotentialSomaInfo potentialSomaInfo = new PotentialSomaInfo(
                                    potentialLocation.getInt("id"),
                                    potentialLocation.getString("image"),
                                    new XYZ(loc.getInt("x"),
                                            loc.getInt("y"),
                                            loc.getInt("z")));
                            result.postValue(new Result.Success<PotentialSomaInfo>(potentialSomaInfo));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            result.postValue(new Result.Error(new Exception("Fail to parse user info !")));
                        }
                    } else {
                        result.postValue(new Result.Error(new IOException(responseData)));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getSomaList(String image, int x, int y, int z){
        try {
            HttpUtilsSoma.getSomaListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), image, x, y, z, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect Failed When Login")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    responseData = response.body().string();
                    Log.e(TAG, "responseData: " + responseData);

                    if (responseCode == 200) {
                        try {
                            JSONArray somaList = new JSONArray(responseData);
                            MarkerList markerList = MarkerList.parseFromJSONArray(somaList);
                            result.postValue(new Result.Success<MarkerList>(markerList));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            result.postValue(new Result.Error(new Exception("Fail to parse user info !")));
                        }
                    } else {
                        result.postValue(new Result.Error(new IOException(responseData)));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void insertSomaList(String image, int locationId, JSONArray somaList){
        try {
            HttpUtilsSoma.insertSomaListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), locationId, somaList, "xingfei", image, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect Failed When Login")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    responseData = response.body().string();
                    Log.e(TAG, "responseData: " + responseData);

                    if (responseCode == 200) {
                        result.postValue(new Result.Success<String>("Insert soma successfully !"));
                    } else {
                        result.postValue(new Result.Error(new IOException(responseData)));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }
}
