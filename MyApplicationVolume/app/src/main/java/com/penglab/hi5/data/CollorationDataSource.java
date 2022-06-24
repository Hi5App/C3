package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsCollaborate;
import com.penglab.hi5.core.net.HttpUtilsQualityInspection;
import com.penglab.hi5.data.model.img.PotentialArborMarkerInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CollorationDataSource {

    public static final String UPLOAD_SUCCESSFULLY = "Upload result successfully !";
    public static final String NO_MORE_FILE = "No more file need to process !";

    private final String TAG = "CollorationDataSource";

    private String responseData;

    private final MutableLiveData<Result> brianListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> neuronListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> anoListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> downloadAnoResult = new MutableLiveData<>();

    public LiveData<Result> getBrainListCollaborate() {
        return brianListResult;
    }

    public LiveData<Result> getNeuronListCollaborate() {
        return neuronListResult;
    }
    public LiveData<Result> getAnoListCollaborate() {
        return anoListResult;
    }
    public LiveData<Result> getDownloadAnoResult() {
        return downloadAnoResult;
    }

    public void getImageList(){
        try {
            Log.e(TAG,"getImageList");
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsCollaborate.getImageListWithOkHttp(userInfo, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    brianListResult.postValue(new Result.Error(new Exception("Connect failed when get potential location !")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        // process response
                        responseData = response.body().string();
                        Log.e(TAG, "responseData: " + responseData);
                        try {
                            JSONArray brainListArray = new JSONArray(responseData);
                            brianListResult.postValue(new Result.Success<JSONArray>(brainListArray));
                        } catch (Exception e) {
                            e.printStackTrace();
                            brianListResult.postValue(new Result.Error(new Exception("Fail to parse brain list info !")));
                        }
                        response.body().close();
                        response.close();
                    } else if (responseCode == 502) {
                        responseData = response.body().string();
                        if (responseData.trim().equals("Empty")) {
                            Log.e(TAG,"get Empty response");
                            brianListResult.postValue(new Result.Success<String>(NO_MORE_FILE));
                        }
                    } else {
                        brianListResult.postValue(new Result.Error(new Exception("Fail to get brain list info !")));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void getNeuron(int brain,String userName) {
//
//        try {
//            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
//            JSONObject brainName = new JSONObject().put("",brain);
//            HttpUtilsCollaborate.getNeuronsWithOkHttp(userInfo, brainName, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    neuronListResult.postValue(new Result.Error(new Exception("Connect failed when update arbor result !")));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    Log.e(TAG, "responseCode" + response.code());
//                    int responseCode = response.code();
//                    responseData = response.body().string();
//                    if (responseCode == 200) {
//                        Log.e(TAG, "response queryArborResult: " + responseData);
//                        // process response
//                        try {
//                            JSONArray neuronNameArray = new JSONArray(responseData);
//                            neuronListResult.postValue(new Result.Success<JSONArray>(neuronNameArray));
//                        } catch (Exception exception) {
//                            exception.printStackTrace();
//                            neuronListResult.postValue(new Result.Error(new Exception("Fail to parse query arbor result !")));
//                        }
//                        response.body().close();
//                        response.close();
//                    } else if (responseCode == 502) {
//                        responseData = response.body().string();
//                        if (responseData.trim().equals("Empty")) {
//                            Log.e(TAG, "get Empty response");
//                            neuronListResult.postValue(new Result.Success<String>(NO_MORE_FILE));
//                        }
//                    } else {
//                        Log.e(TAG, "response update arbor result: " + response.body().string());
//                        neuronListResult.postValue(new Result.Error(new Exception("Fail to get query arbor result !")));
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            neuronListResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
//            e.printStackTrace();
//        }
//    }

//    public void getAno(int brain,int neuron,String userName){
//        try {
//            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
//            HttpUtilsCollaborate.getAnoWithOkHttp(userInfo, arborId, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    anoListResult.postValue(new Result.Error(new Exception("Connect failed when update arbor result !")));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    Log.e(TAG, "responseCode" + response.code());
//                    int responseCode = response.code();
//                    responseData = response.body().string();
//                    if (responseCode == 200) {
//                        Log.e(TAG, "response queryArborResult: " + responseData);
//                        // process response
//                        try {
//                            JSONArray anoNameArray = new JSONArray(responseData);
//                            anoListResult.postValue(new Result.Success<JSONArray>(anoNameArray));
//                        } catch (Exception exception) {
//                            exception.printStackTrace();
//                            anoListResult.postValue(new Result.Error(new Exception("Fail to parse query arbor result !")));
//                        }
//                        response.body().close();
//                        response.close();
//                    } else if (responseCode == 502) {
//                        responseData = response.body().string();
//                        if (responseData.trim().equals("Empty")) {
//                            Log.e(TAG, "get Empty response");
//                            anoListResult.postValue(new Result.Success<String>(NO_MORE_FILE));
//                        }
//                    } else {
//                        Log.e(TAG, "response update arbor result: " + response.body().string());
//                        anoListResult.postValue(new Result.Error(new Exception("Fail to get query arbor result !")));
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            anoListResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
//            e.printStackTrace();
//        }
//    }

//    public void loadAno(int brain,int neuron,int ano,String userName){
//
//        try {
//            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
//            HttpUtilsCollaborate.loadAnoWithOkHttp(userInfo, arborId, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    downloadAnoResult.postValue(new Result.Error(new Exception("Connect failed when update arbor result !")));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    Log.e(TAG, "responseCode" + response.code());
//                    int responseCode = response.code();
//                    responseData = response.body().string();
//                    if (responseCode == 200) {
//                        Log.e(TAG, "response queryArborResult: " + responseData);
//                        // process response
//                        try {
//                            JSONObject downloadAnoObject = new JSONObject(responseData);
//                            downloadAnoResult.postValue(new Result.Success<JSONObject>(downloadAnoObject));
//                        } catch (Exception exception) {
//                            exception.printStackTrace();
//                            downloadAnoResult.postValue(new Result.Error(new Exception("Fail to parse query arbor result !")));
//                        }
//                        response.body().close();
//                        response.close();
//                    } else if (responseCode == 502) {
//                        responseData = response.body().string();
//                        if (responseData.trim().equals("Empty")) {
//                            Log.e(TAG, "get Empty response");
//                            downloadAnoResult.postValue(new Result.Success<String>(NO_MORE_FILE));
//                        }
//                    } else {
//                        Log.e(TAG, "response update arbor result: " + response.body().string());
//                        downloadAnoResult.postValue(new Result.Error(new Exception("Fail to get query arbor result !")));
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            downloadAnoResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
//            e.printStackTrace();
//        }
//    }
//


















}
