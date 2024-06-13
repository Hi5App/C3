package com.penglab.hi5.data;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsCollaborate;
import com.penglab.hi5.core.net.HttpUtilsUser;
import com.penglab.hi5.data.model.img.CollaborateNeuronInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CollorationDataSource {

    public static final String NO_MORE_FILE = "No more file need to process !";

    private final String TAG = "CollorationDataSource";

    private String responseData;

    private int userId;
    public int getUserId(){
        return userId;
    }
    public void setUserId(int id){
        userId = id;
    }

    public Pair<String, String> CurrentProjectInfo;
    public Pair<String, String> CurrentSwcInfo;

    private final MutableLiveData<Result> neuronListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> anoListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> projectListResult = new MutableLiveData<>(); // Uuid, Name pair
    private final MutableLiveData<Result> downloadAnoResult = new MutableLiveData<>();

    public LiveData<Result> getNeuronListCollaborate() {
        return neuronListResult;
    }
    public LiveData<Result> getAnoListCollaborate() {
        return anoListResult;
    }
    public LiveData<Result> getAllProjectListCollaborate() {
        return projectListResult;
    }
    public LiveData<Result> getDownloadAnoResult() {
        return downloadAnoResult;
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
                                neuronList.add(new CollaborateNeuronInfo(neuronInfo.getString("imageid"),
                                        neuronInfo.getString("name"),
                                        new XYZ ((float)neuronInfo.getDouble("x"),
                                                (float)neuronInfo.getDouble("y"),
                                                (float)neuronInfo.getDouble("z"))));
                            }
                            neuronListResult.postValue(new Result.Success<>(neuronList));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            neuronListResult.postValue(new Result.Error(new Exception("Fail to parse query arbor result !")));
                        }
                        response.body().close();
                        response.close();
                    } else if (responseCode == 502) {
                        neuronListResult.postValue(new Result.Success<>(NO_MORE_FILE));
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

    public void getAllProject() {
        try {
            JSONObject userVerifyInfo = new JSONObject().put("UserName", InfoCache.getAccount()).put("UserPassword", InfoCache.getToken());
            JSONObject metaInfo = new JSONObject().put("ApiVersion","2024.05.06");
            JSONObject param = new JSONObject();
            param.put("UserVerifyInfo",userVerifyInfo);
            param.put("metaInfo",metaInfo);
            HttpUtilsCollaborate.getAllProject(param, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    projectListResult.postValue(new Result.Error(new Exception("Connect failed when getAllProject result !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "responseCode" + response.code());
                    int responseCode = response.code();
                    responseData = response.body().string();
                    if (responseCode == 200) {
                        Log.e(TAG, "response getAllProject: " + responseData);
                        // process response
                        try {
                            JSONObject resultJson = new JSONObject(responseData);
                            JSONObject metaInfo = resultJson.getJSONObject("metaInfo");
                            boolean status = metaInfo.getBoolean("Status");
                            String message = metaInfo.getString("Message");
                            if(!status){
                                projectListResult.postValue(new Result.Error(new Exception("Get getAllProject Failed with " + message)));
                            }

                            JSONArray projectInfos = resultJson.getJSONArray("ProjectInfo");
                            List<android.util.Pair<String,String>> projectList = new ArrayList<>();
                            for(int i =0;i<projectInfos.length();i++){
                                JSONObject projectInfo = projectInfos.getJSONObject(i);
                                String projectUuid = projectInfo.getJSONObject("Base").getString("Uuid");
                                String projectName = projectInfo.getString("Name");
                                projectList.add(new  android.util.Pair<>(projectUuid, projectName));
                            }
                            projectListResult.postValue(new Result.Success<>(projectList));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            projectListResult.postValue(new Result.Error(new Exception("Fail to parse getAllProject result !")));
                        }
                        response.body().close();
                        response.close();
                    }
                    else {
                        Log.e(TAG, "Fail to getAllProject with: " + response.body().string());
                        projectListResult.postValue(new Result.Error(new Exception("Fail to getAllProject !")));
                    }
                }
            });

        } catch (Exception e) {
            anoListResult.postValue(new Result.Error(new IOException("Check the network please !", e)));
            e.printStackTrace();
        }
    }

    public void getSwcNameAndUuidByProject(String projectUuid) {
        try {
            JSONObject userVerifyInfo = new JSONObject().put("UserName", InfoCache.getAccount()).put("UserPassword", InfoCache.getToken());
            JSONObject metaInfo = new JSONObject().put("ApiVersion","2024.05.06");
            JSONObject param = new JSONObject();
            param.put("UserVerifyInfo",userVerifyInfo);
            param.put("metaInfo",metaInfo);
            param.put("ProjectUuid", projectUuid);
            HttpUtilsCollaborate.getProjectSwcNamesByProjectUuid(param, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    anoListResult.postValue(new Result.Error(new Exception("Connect failed when getSwcNameAndUuidByProject result !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "responseCode" + response.code());
                    int responseCode = response.code();
                    responseData = response.body().string();
                    if (responseCode == 200) {
                        Log.e(TAG, "response getSwcNameAndUuidByProject: " + responseData);
                        // process response
                        try {
                            JSONObject resultJson = new JSONObject(responseData);
                            JSONObject metaInfo = resultJson.getJSONObject("metaInfo");
                            boolean status = metaInfo.getBoolean("Status");
                            String message = metaInfo.getString("Message");
                            if(!status){
                                anoListResult.postValue(new Result.Error(new Exception("Get getSwcNameAndUuidByProject Failed with " + message)));
                            }

                            JSONArray projectInfos = resultJson.getJSONArray("swcUuidName");
                            List<android.util.Pair<String,String>> projectList = new ArrayList<>();
                            for(int i =0;i<projectInfos.length();i++){
                                JSONObject projectInfo = projectInfos.getJSONObject(i);
                                String swcUuid = projectInfo.getString("SwcUuid");
                                String swcName = projectInfo.getString("SwcName");
                                projectList.add(new android.util.Pair<>(swcUuid, swcName));
                            }
                            anoListResult.postValue(new Result.Success<>(projectList));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            anoListResult.postValue(new Result.Error(new Exception("Fail to parse getSwcNameAndUuidByProject result !")));
                        }
                        response.body().close();
                        response.close();
                    }
                    else {
                        Log.e(TAG, "Fail to getSwcNameAndUuidByProject with: " + response.body().string());
                        anoListResult.postValue(new Result.Error(new Exception("Fail to getSwcNameAndUuidByProject !")));
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
            HttpUtilsCollaborate.loadAnoWithOkHttp(userInfo, brainNumber, neuronNumber, ano, CurrentProjectInfo.second,new Callback() {
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
                            downloadAnoResult.postValue(new Result.Success<>(loadAnoArray));
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
                            downloadAnoResult.postValue(new Result.Success<>(NO_MORE_FILE));
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

    public void getUserId(String username){
        try {
            JSONObject userVerifyInfo = new JSONObject().put("UserName", username).put("UserToken", "");
            JSONObject metaInfo = new JSONObject().put("ApiVersion","2024.05.06");
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
                    Log.e(TAG,"responsecode of getuserid"+responseCode);
                    if (responseCode == 200) {
                        responseData = response.body().string();
                        Log.e(TAG, "responseData_getuserid: " + responseData);
                        try {
                            JSONObject resultJson = new JSONObject(responseData);
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
