package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.netease.nim.uikit.common.util.C;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsCollaborate;
import com.penglab.hi5.core.net.HttpUtilsQualityInspection;
import com.penglab.hi5.data.model.img.CollaborateNeuronInfo;
import com.penglab.hi5.data.model.img.PotentialArborMarkerInfo;
import com.penglab.hi5.data.model.img.PotentialSomaInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public void getImageList() {
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
                            String[] brainNumber = responseData.split(",");
                            Log.e("brainNumberSize",""+brainNumber.length);
                            brianListResult.postValue(new Result.Success<String[]>(brainNumber));
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

    public void getNeuron(String brainNum) {

        try {
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsCollaborate.getNeuronsWithOkHttp(userInfo, brainNum, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    neuronListResult.postValue(new Result.Error(new Exception("Connect failed when update arbor result !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "responseCode" + response.code());
                    int responseCode = response.code();
                    responseData = response.body().string();
                    Log.e(TAG, "response getNeuron: " + responseData);

                    if (responseCode == 200) {
                        try {
                            JSONArray neuronNameArray = new JSONArray(responseData);
                            List<CollaborateNeuronInfo> neuronList = new ArrayList<>();
                            for (int i=0; i<neuronNameArray.length(); i++){
                                JSONObject neuronInfo = neuronNameArray.getJSONObject(i);
                                if(neuronInfo.getString("name").equals("18454_00019")){
                                    neuronList.add(new CollaborateNeuronInfo(neuronInfo.getString("imageid"),
                                            neuronInfo.getString("name"),
                                            new XYZ ((float)neuronInfo.getDouble("x"),
                                                    (float)neuronInfo.getDouble("y"),
                                                    (float)neuronInfo.getDouble("z"))));
                                }

                            }
                            neuronListResult.postValue(new Result.Success<List<CollaborateNeuronInfo>>(neuronList));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            neuronListResult.postValue(new Result.Error(new Exception("Fail to parse query arbor result !")));
                        }
                        response.body().close();
                        response.close();
                    } else if (responseCode == 502) {
                        neuronListResult.postValue(new Result.Success<String>(NO_MORE_FILE));
//                        }
                    } else {
                        Log.e(TAG, "response update arbor result: " + response.body().string());
                        neuronListResult.postValue(new Result.Error(new Exception("Fail to get query arbor result !")));
                    }
                }
            });

        } catch (Exception e) {
            neuronListResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
            e.printStackTrace();
        }
    }

    public void getAno(String neuronNum) {
        try {
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsCollaborate.getAnoWithOkHttp(userInfo, neuronNum, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    anoListResult.postValue(new Result.Error(new Exception("Connect failed when update get ano result !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "responseCode" + response.code());
                    int responseCode = response.code();
                    responseData = response.body().string();
                    if (responseCode == 200) {
                        Log.e(TAG, "response getAno: " + responseData);
                        // process response
                        try {
                            JSONArray anoNameArray = new JSONArray(responseData);
                            List<String> anoNameList = new ArrayList<String>();
                            for(int i =0;i<anoNameArray.length();i++){
                                JSONObject anoInfo = anoNameArray.getJSONObject(i);
                                anoNameList.add(anoInfo.getString("name"));
                            }
                            anoListResult.postValue(new Result.Success<List<String>>(anoNameList));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            anoListResult.postValue(new Result.Error(new Exception("Fail to parse anolist result !")));
                        }
                        response.body().close();
                        response.close();
                    } else if (responseCode == 502) {
                        responseData = response.body().string();
                        if (responseData.trim().equals("Empty")) {
                            Log.e(TAG, "get Empty response");
                            anoListResult.postValue(new Result.Success<String>(NO_MORE_FILE));
                        }
                    } else {
                        Log.e(TAG, "response update arbor result: " + response.body().string());
                        anoListResult.postValue(new Result.Error(new Exception("Fail to get ano list !")));
                    }
                }
            });

        } catch (Exception e) {
            anoListResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
            e.printStackTrace();
        }
    }

    public void loadAno(String brainNumber,String neuronNumber,String ano) {

        try {
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsCollaborate.loadAnoWithOkHttp(userInfo,brainNumber,neuronNumber,ano, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadAnoResult.postValue(new Result.Error(new Exception("Connect failed when update arbor result !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "responseCode_loadano" + response.code());
                    int responseCode = response.code();
                    responseData = response.body().string();
                    if (responseCode == 200) {
//                        Log.e(TAG, "response loadano: " + responseData);
                        // process response
                        try {
                            JSONObject loadAnoArray = new JSONObject(responseData);
                            downloadAnoResult.postValue(new Result.Success<JSONObject>(loadAnoArray));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            downloadAnoResult.postValue(new Result.Error(new Exception("Fail to parse query arbor result !")));
                        }
                        response.body().close();
                        response.close();
                    } else if (responseCode == 502) {
                        responseData = response.body().string();
                        if (responseData.trim().equals("Empty")) {
                            Log.e(TAG, "get Empty response");
                            downloadAnoResult.postValue(new Result.Success<String>(NO_MORE_FILE));
                        }
                    } else {
//                        Log.e(TAG, "response update arbor result: " + response.body().string());
                        downloadAnoResult.postValue(new Result.Error(new Exception("Fail to get query arbor result !")));
                    }
                }
            });

        } catch (Exception e) {
            downloadAnoResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
            e.printStackTrace();
        }
    }



















}
