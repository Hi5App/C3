package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsSoma;
import com.penglab.hi5.data.model.img.PotentialSomaInfo;

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
    public static final String UPLOAD_SUCCESSFULLY = "Upload soma successfully !";
    public static final String NO_MORE_FILE = "No more file need to process !";
    private final String TAG = "MarkerFactoryDataSource";
    private final MutableLiveData<Result> result = new MutableLiveData<>();
    private String responseData;

    public LiveData<Result> getResult() {
        return result;
    }

    public void getPotentialLocation(){
        try {
            Log.e(TAG,"getPotentialLocation");
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsSoma.getPotentialLocationWithOkHttp(userInfo, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect failed when get potential location !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        // process response
                        responseData = response.body().string();
                        Log.e(TAG, "responseData: " + responseData);
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
                            result.postValue(new Result.Error(new Exception("Fail to parse potential location info !")));
                        }
                        response.body().close();
                        response.close();
                    } else if (responseCode == 502) {
                        responseData = response.body().string();
                        Log.e(TAG,"responseData: " + responseData);
                        if (responseData.trim().equals("Empty")) {
                            Log.e(TAG,"get Empty response");
                            result.postValue(new Result.Success<String>(NO_MORE_FILE));
                        }
                    } else {
                        result.postValue(new Result.Error(new Exception("Fail to get potential location info !")));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getSomaList(String image, int x, int y, int z, int size){
        try {
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsSoma.getSomaListWithOkHttp(userInfo, image, x, y, z, size, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect failed when get soma list !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        // process response
                        responseData = response.body().string();
                        Log.e(TAG, "responseData: " + responseData);
                        try {
                            JSONArray somaList = new JSONArray(responseData);
                            MarkerList markerList = MarkerList.parseFromJSONArray(somaList);
                            result.postValue(new Result.Success<MarkerList>(markerList));
                        } catch (JSONException e) {
                            result.postValue(new Result.Error(new Exception("Fail to parse soma list !")));
                            e.printStackTrace();
                        }
                        response.body().close();
                        response.close();
                    } else {
                        result.postValue(new Result.Error(new Exception("Fail to get soma list !")));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void updateSomaList(String image, int locationId, String username, JSONArray insertSomaList, JSONArray deleteSomaList){
        try {
            Log.e(TAG,"start updateSomaList");
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsSoma.updateSomaListWithOkHttp(userInfo, locationId, insertSomaList, deleteSomaList, username, image, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect failed when upload marker list !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG,"receive response");
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        // process response
                        result.postValue(new Result.Success<String>(UPLOAD_SUCCESSFULLY));
                    } else {
                        Log.e(TAG,"response: " + response.body().string());
                        result.postValue(new Result.Error(new Exception("Fail to upload marker list !")));
                    }
                    response.close();
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }
}
