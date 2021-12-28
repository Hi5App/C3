package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.net.HttpUtilsImage;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Class that handles image information.
 *
 * Created by Jackiexing on 12/09/21
 */
public class ImageDataSource {

    private final MutableLiveData<Result> result = new MutableLiveData<>();

    public LiveData<Result> getResult() {
        return result;
    }

    public void getBrainList(){
        try {
            HttpUtilsImage.getBrainListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect failed when get Brain List")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            String str = response.body().string();
                            Log.e("GetBrainList", str);
                            JSONArray jsonArray = new JSONArray(str);
                            result.postValue(new Result.Success<JSONArray>(jsonArray));
                        } else {
                            result.postValue(new Result.Error(new Exception("Response from server is null !")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.postValue(new Result.Error(new Exception("Fail to get brain list !")));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getNeuronList(String brainId){
        try {
            HttpUtilsImage.getNeuronListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), brainId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect failed when get neuron list")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            result.postValue(new Result.Success<JSONArray>(jsonArray));
                        } else {
                            result.postValue(new Result.Error(new Exception("Response from server is null !")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.postValue(new Result.Error(new Exception("Fail to get neuron list !")));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getAnoList(String neuronId){
        try {
            HttpUtilsImage.getAnoListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), neuronId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect failed when get ano list")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            result.postValue(new Result.Success<JSONArray>(jsonArray));
                        } else {
                            result.postValue(new Result.Error(new Exception("Response from server is null !")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.postValue(new Result.Error(new Exception("Fail to get ano list !")));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void downloadImage(String brainId, String roi, int offsetX, int offsetY, int offsetZ, int size){
        try {
            HttpUtilsImage.downloadImageWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(),
                    brainId, roi, offsetX, offsetY, offsetZ, size, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    result.postValue(new Result.Error(new Exception("Connect Failed When Download Music")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            byte[] fileContent = response.body().bytes();
                            String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Resources/Image";
                            String filename = brainId + "_" + roi + "_" + offsetX + "_" + offsetY + "_" + offsetZ + ".v3dpbd";
                            if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                            }
                            result.postValue(new Result.Success(storePath + "/" + filename));
                        } else {
                            result.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.postValue(new Result.Error(new Exception("Fail to download image file !")));
                    }
                }
            });
        } catch (Exception exception) {
            result.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

}
