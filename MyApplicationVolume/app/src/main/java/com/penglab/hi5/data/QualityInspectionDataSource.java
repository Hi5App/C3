package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.net.HttpUtilsQualityInspection;
import com.penglab.hi5.core.net.HttpUtilsSoma;
import com.penglab.hi5.data.model.img.PotentialArborMarkerInfo;
import com.penglab.hi5.data.model.img.PotentialArborMarkerInfo;

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
    private final MutableLiveData<Result> downloadSwcResult = new MutableLiveData<>();
    private String responseData;

    public LiveData<Result> getPotentialArborLocationResult() {
        return potentialArborLocationResult;
    }
    public MutableLiveData<Result> getDownloadSwcResult(){
        return downloadSwcResult;
    }

    public MutableLiveData<Result> getArborMarkerListResult() {
        return arborMarkerListResult;
    }

    public MutableLiveData<Result> getUpdateCheckResult() {
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
                            PotentialArborMarkerInfo potentialArborMarkerInfo = new PotentialArborMarkerInfo(
                                    potentialArborLocation.getInt("id"),
                                    potentialArborLocation.getString("name"),
                                    potentialArborLocation.getString("somaId"),
                                    potentialArborLocation.getString("image"),
                                    new XYZ((float) loc.getDouble("x"),
                                            (float) loc.getDouble("y"),
                                            (float) loc.getDouble("z")));
                            Log.e(TAG, "handle response successfully");
                            potentialArborLocationResult.postValue(new Result.Success<PotentialArborMarkerInfo>(potentialArborMarkerInfo));
                        } catch (Exception e) {
                            Log.e(TAG,"potentialArborLocationResultException");
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

    public void getSwc(String res,float offsetX,float offsetY,float offsetZ,int size,String arborName){
        try{
            Log.e("TAG","getSwc");
            JSONObject pa1 = new JSONObject().put("x", offsetX - size/2).put("y", offsetY - size/2).put("z", offsetZ - size/2);
            JSONObject pa2 = new JSONObject().put("x", offsetX + size/2).put("y", offsetY + size/2).put("z", offsetZ + size/2);
            JSONObject bBox = new JSONObject().put("pa1", pa1).put("pa2", pa2).put("res", res).put("obj", arborName);
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsQualityInspection.getSwcWithOkHttp(userInfo,bBox, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadSwcResult.postValue(new Result.Error(new Exception("Connect Failed When Download Music")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        int responseCode = response.code();
                        Log.e(TAG,"getSwcResponseCode" + responseCode);
                        if(responseCode == 200){
                            if(response.body()!= null){
                                byte[] fileContent = response.body().bytes();
                                Log.e(TAG, "swc file size: " + fileContent.length);
                                String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/swc";
                                String filename = arborName + "_"  + offsetX + "_" + offsetY + "_" + offsetZ + ".swc";
                                if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                                    downloadSwcResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
                                }
                                downloadSwcResult.postValue(new Result.Success(storePath + "/" + filename));
                                response.body().close();
                                response.close();
                            }else{
                                downloadSwcResult.postValue(new Result.Error(new Exception("Response from server is null when download swc !")));
                            }
                        }else{
                            downloadSwcResult.postValue(new Result.Error(new Exception("Response from server is error when download swc !")));
                        }
                    } catch (Exception e) {
                        Log.e(TAG,"download swc failed");
                        downloadSwcResult.postValue(new Result.Error(new Exception("Fail to download swc file !")));
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            downloadSwcResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
            e.printStackTrace();
        }
    }


    public void UpdateCheckResult(int arborId,String arborName,JSONArray insertList,JSONArray deleteList,String name){
        try{
            Log.e(TAG,"start updateCheckResult");
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsQualityInspection.UpdateCheckResultWithOkHttp(userInfo, arborId,arborName, insertList, deleteList, name, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    updateArborMarkerResult.postValue(new Result.Error(new Exception("Connect failed when upload marker list !")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG,"receive response when upload marker");
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        // process response
                        updateArborMarkerResult.postValue(new Result.Success<String>(UPLOAD_SUCCESSFULLY));
                    } else {
                        Log.e(TAG,"response_upload_marker_list: " + response.body().string());
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
                    arborMarkerListResult.postValue(new Result.Error(new Exception("Connect failed when get arbor marker list!")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    if(responseCode == 200){
                        responseData = response.body().string();
                        Log.e(TAG,"responseData_getArborMarker"+responseData);
                        try{
                            JSONArray arborMarkerList = new JSONArray(responseData);
                            MarkerList markerList = MarkerList.parseToJSONArray(arborMarkerList);
                            arborMarkerListResult.postValue(new Result.Success<MarkerList>(markerList));
                        } catch (Exception e) {
                            arborMarkerListResult.postValue(new Result.Error(new Exception("Fail to parse arbor marker list !")));
                            e.printStackTrace();
                        }
                        response.body().close();
                    }else{
                        arborMarkerListResult.postValue(new Result.Error(new Exception("Fail to get arbor marker list !")));
                    }
                }
            });

        } catch (Exception e) {
            arborMarkerListResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
            e.printStackTrace();
        }
    }

}
