package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.netease.nim.uikit.common.util.C;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsCollaborate;
import com.penglab.hi5.core.net.HttpUtilsQualityInspection;
import com.penglab.hi5.core.net.HttpUtilsUser;
import com.penglab.hi5.data.model.img.CollaborateNeuronInfo;
import com.penglab.hi5.data.model.img.PotentialArborMarkerInfo;
import com.penglab.hi5.data.model.img.PotentialSomaInfo;

import org.json.JSONArray;
import org.json.JSONException;
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

    private int userId;
    public int getUserId(){
        return userId;
    }
    public void setUserId(int id){
        userId = id;
    }
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
                    String responseBody = response.body().string();
                    if (responseCode == 200) {
                        Log.e(TAG, "responseData: " + responseBody);
                        try {

                            String[] brainNumber = responseBody.split(",");
                            Log.e("brainNumberSize",""+brainNumber.length);
                            brianListResult.postValue(new Result.Success<String[]>(brainNumber));
                        } catch (Exception e) {
                            e.printStackTrace();
                            brianListResult.postValue(new Result.Error(new Exception("Fail to parse brain list info !")));
                        }
                        response.body().close();
                        response.close();
                    } else if (responseCode == 502) {
                        if (responseBody.trim().equals("Empty")) {
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
                    neuronListResult.postValue(new Result.Error(new Exception("Connect failed when update neuron result !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "responseCode" + response.code());
                    int responseCode = response.code();
                    String responseBody = response.body().string();
                    Log.e(TAG, "response getNeuron: " + responseBody);
                    if (responseCode == 200) {
                        try {
                            JSONArray neuronNameArray = new JSONArray(responseBody);
                            List<CollaborateNeuronInfo> neuronList = new ArrayList<>();
                            for (int i=0; i<neuronNameArray.length(); i++){
                                JSONObject neuronInfo = neuronNameArray.getJSONObject(i);
                                neuronList.add(new CollaborateNeuronInfo(neuronInfo.getString("imageid"),
                                        neuronInfo.getString("name"),
                                        new XYZ ((float)neuronInfo.getDouble("x"),
                                                (float)neuronInfo.getDouble("y"),
                                                (float)neuronInfo.getDouble("z"))));
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
                        Log.e(TAG, "response update arbor result: " + responseBody);
                        neuronListResult.postValue(new Result.Error(new Exception("Fail to get query arbor result !")));
                    }
                }
            });

        } catch (Exception e) {
            neuronListResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
            e.printStackTrace();
        }
    }

    public void getAno() {
        try {
            JSONObject userVerifyInfo = new JSONObject().put("UserName", InfoCache.getAccount()).put("UserToken", "");
            JSONObject metaInfo = new JSONObject().put("ApiVersion","2024.01.19");
            JSONObject param = new JSONObject();
            param.put("UserVerifyInfo",userVerifyInfo);
            param.put("metaInfo",metaInfo);
            HttpUtilsCollaborate.getAllSwcMetaInfoWithOkHttp(param, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    anoListResult.postValue(new Result.Error(new Exception("Connect failed when getAllSwcMetaInfo result !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "responseCode" + response.code());
                    int responseCode = response.code();
                    String responseBody = response.body().string();
                    if (responseCode == 200) {
                        Log.e(TAG, "response getAno: " + responseBody);
                        try {
                            JSONObject resultJson = new JSONObject(responseBody);
                            JSONObject metaInfo = resultJson.getJSONObject("metaInfo");
                            boolean status = metaInfo.getBoolean("Status");
                            String message = metaInfo.getString("Message");
                            if(!status){
                                anoListResult.postValue(new Result.Error(new Exception("Get SwcMetaInfo Failed" + message)));
                            }

                            JSONArray swcInfos = resultJson.getJSONArray("SwcInfo");
                            List<String> anoNameList = new ArrayList<String>();
                            for(int i =0;i<swcInfos.length();i++){
                                JSONObject swcInfo = swcInfos.getJSONObject(i);
                                String swcName = swcInfo.getString("Name");
                                int removedLen = ".ano.eswc".length();
                                int len = swcName.length();
                                String anoName = swcName.substring(0,len-removedLen);
                                anoNameList.add(anoName);
                            }
                            anoListResult.postValue(new Result.Success<List<String>>(anoNameList));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            anoListResult.postValue(new Result.Error(new Exception("Fail to parse GetSwcMetaInfo result !")));
                        }
                        response.body().close();
                        response.close();
                    }
                    else {
                        Log.e(TAG, "response update arbor result: " + responseBody);
                        anoListResult.postValue(new Result.Error(new Exception("Fail to get swcmetainfo !")));
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
            HttpUtilsCollaborate.loadAnoWithOkHttp(userInfo, brainNumber, neuronNumber, ano, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadAnoResult.postValue(new Result.Error(new Exception("Connect failed when update arbor result !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    String responseBody = response.body().string();
                    if (responseCode == 200) {
                        Log.e(TAG, "response loadano: " + responseBody);
                        try {
                            JSONObject loadAnoArray = new JSONObject(responseBody);
                            downloadAnoResult.postValue(new Result.Success<JSONObject>(loadAnoArray));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            downloadAnoResult.postValue(new Result.Error(new Exception("Fail to parse query arbor result !")));
                        }
                        response.body().close();
                        response.close();
                    } else if (responseCode == 502) {
                        if (responseBody.trim().equals("Empty")) {
                            Log.e(TAG, "get Empty response");
                            downloadAnoResult.postValue(new Result.Success<String>(NO_MORE_FILE));
                        }
                    } else {
                        downloadAnoResult.postValue(new Result.Error(new Exception("Fail to get query arbor result !")));
                    }
                }
            });

        } catch (Exception e) {
            downloadAnoResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
            e.printStackTrace();
        }
    }

    public void getUserId(String username){
        try {
            JSONObject userVerifyInfo = new JSONObject().put("UserName", username).put("UserToken", "");
            JSONObject metaInfo = new JSONObject().put("ApiVersion","2024.01.19");
            JSONObject param = new JSONObject();
            param.put("UserName",username);
            param.put("UserVerifyInfo",userVerifyInfo);
            param.put("metaInfo",metaInfo);
            HttpUtilsUser.getUserIdWithOKHttp(param, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Connect Failed When GetUserId");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    String responseBody = response.body().string();
                    Log.e(TAG,"responsecode of getuserid"+responseCode);
                    if (responseCode == 200) {
                        Log.e(TAG, "responseData_getuserid: " +  responseBody);
                        try {
                            JSONObject resultJson = new JSONObject(responseBody);
                            JSONObject metaInfo = resultJson.getJSONObject("metaInfo");
                            boolean status = metaInfo.getBoolean("Status");
                            String message = metaInfo.getString("Message");
                            if(!status){
                                Log.e(TAG, "GetUserId Failed" + message);
                            }
                            int id = resultJson.getJSONObject("UserInfo").getInt("UserId");
                            Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + id);
                            setUserId(id);
                            response.body().close();
                            response.close();
                        } catch (JSONException e) {
                            e.printStackTrace();}
                    }
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
