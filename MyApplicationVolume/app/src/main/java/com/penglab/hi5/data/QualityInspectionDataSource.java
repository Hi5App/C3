package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsQualityInspection;
import com.penglab.hi5.core.net.HttpUtilsSoma;
import com.penglab.hi5.data.model.img.PotentialArborInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class QualityInspectionDataSource {

    public static final String UPLOAD_SUCCESSFULLY = "Upload soma successfully !";
    public static final String NO_MORE_FILE = "No more file need to process !";
    private final String TAG = "QualityInspectionDataSource";

    private final MutableLiveData<Result> potentialArborLocationResult = new MutableLiveData<>();
    private final MutableLiveData<Result> arborMarkerListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> updateArborMarkerResult = new MutableLiveData<>();
    private String responseData;

    public LiveData<Result> getPotentialArborLocationResult() {
        return potentialArborLocationResult;
    }

    public MutableLiveData<Result> getSomaListResult() {
        return arborMarkerListResult;
    }

    public MutableLiveData<Result> getUpdateSomaResult() {
        return updateArborMarkerResult;
    }

    public void getArbor(){
        try {
            Log.e(TAG,"getArbor");
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsQualityInspection.getArborWithOkHttp(userInfo, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    potentialArborLocationResult.postValue(new Result.Error(new Exception("Connect failed when get potential location !")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        // process response
                        responseData = response.body().string();
                        Log.e(TAG, "responseData: " + responseData);
                        try {
                            JSONObject potentialArborLocation = new JSONObject(responseData);
                            JSONObject loc = potentialArborLocation.getJSONObject("loc");

                            PotentialArborInfo potentialArborInfo = new PotentialArborInfo(
                                    potentialArborLocation.getInt("id"),
                                    potentialArborLocation.getInt("somaId"),
                                    potentialArborLocation.getString("image"),
                                    new XYZ(loc.getInt("x"),
                                            loc.getInt("y"),
                                            loc.getInt("z")));
                            potentialArborLocationResult.postValue(new Result.Success<PotentialArborInfo>(potentialArborInfo));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            potentialArborLocationResult.postValue(new Result.Error(new Exception("Fail to parse potential location info !")));
                        }
                        response.body().close();
                        response.close();
                    } else if (responseCode == 502) {
                        responseData = response.body().string();
                        Log.e(TAG,"responseData: " + responseData);
                        if (responseData.trim().equals("Empty")) {
                            Log.e(TAG,"get Empty response");
                            potentialArborLocationResult.postValue(new Result.Success<String>(NO_MORE_FILE));
                        }
                    } else {
                        potentialArborLocationResult.postValue(new Result.Error(new Exception("Fail to get potential location info !")));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getSwc(int x,int y,int z,int size,String arborName){
        try{
            JSONObject pa1 = new JSONObject().put("x", x - size/2).put("y", y - size/2).put("z", z - size/2);
            JSONObject pa2 = new JSONObject().put("x", x + size/2).put("y", y + size/2).put("z", z + size/2);
            JSONObject bBox = new JSONObject().put("pa1", pa1).put("pa2", pa2).put("res", "").put("obj", arborName);
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsQualityInspection.getSwcWithOkHttp(userInfo,bBox, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    if(responseCode == 200){
                        responseData = response.body().string();
                        try{
                            JSONObject arbor_location = new JSONObject(responseData);
                            JSONObject loc = arbor_location.getJSONObject("loc");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        response.body().close();
                        response.close();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void UpdateCheckResult(int arborId,String arborName,int somaType,JSONArray insertList,JSONArray deleteList,String name,String image){
        try{
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsQualityInspection.UpdateCheckResultWithOkHttp(userInfo, arborId,arborName,somaType, insertList, deleteList, name, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    updateArborMarkerResult.postValue(new Result.Error(new Exception("Connect failed when upload marker list !")));

                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG,"receive response");
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        // process response
                        updateArborMarkerResult.postValue(new Result.Success<String>(UPLOAD_SUCCESSFULLY));
                    } else {
                        Log.e(TAG,"response: " + response.body().string());
                        updateArborMarkerResult.postValue(new Result.Error(new Exception("Fail to upload marker list !")));
                    }
                    response.close();
                }
            });

        } catch (Exception e) {
            updateArborMarkerResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
            e.printStackTrace();
        }
    }


    public void getArborMarkerList(String arborName){
        try{
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsQualityInspection.getArborMarkerListWithOkHttp(userInfo, arborName, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    arborMarkerListResult.postValue(new Result.Error(new Exception("Connect failed when get arbor list!")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





//    public void getSomaList(String image, int x, int y, int z, int size){
//        try {
//            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
//            HttpUtilsSoma.getSomaListWithOkHttp(userInfo, image, x, y, z, size, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    somaListResult.postValue(new Result.Error(new Exception("Connect failed when get soma list !")));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    int responseCode = response.code();
//                    if (responseCode == 200) {
//                        // process response
//                        responseData = response.body().string();
//                        Log.e(TAG, "responseData: " + responseData);
//                        try {
//                            JSONArray somaList = new JSONArray(responseData);
//                            MarkerList markerList = MarkerList.parseFromJSONArray(somaList);
//                            somaListResult.postValue(new Result.Success<MarkerList>(markerList));
//                        } catch (JSONException e) {
//                            somaListResult.postValue(new Result.Error(new Exception("Fail to parse soma list !")));
//                            e.printStackTrace();
//                        }
//                        response.body().close();
//                        response.close();
//                    } else {
//                        somaListResult.postValue(new Result.Error(new Exception("Fail to get soma list !")));
//                    }
//                }
//            });
//        } catch (Exception exception) {
//            somaListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
//        }
//    }
//
//    public void updateSomaList(String image, int locationId, String username, JSONArray insertSomaList, JSONArray deleteSomaList){
//        try {
//            Log.e(TAG,"start updateSomaList");
//            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
//            HttpUtilsSoma.updateSomaListWithOkHttp(userInfo, locationId, insertSomaList, deleteSomaList, username, image, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    updateSomaResult.postValue(new Result.Error(new Exception("Connect failed when upload marker list !")));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    Log.e(TAG,"receive response");
//                    int responseCode = response.code();
//                    if (responseCode == 200) {
//                        // process response
//                        updateSomaResult.postValue(new Result.Success<String>(UPLOAD_SUCCESSFULLY));
//                    } else {
//                        Log.e(TAG,"response: " + response.body().string());
//                        updateSomaResult.postValue(new Result.Error(new Exception("Fail to upload marker list !")));
//                    }
//                    response.close();
//                }
//            });
//        } catch (Exception exception) {
//            updateSomaResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
//        }
//    }
}
