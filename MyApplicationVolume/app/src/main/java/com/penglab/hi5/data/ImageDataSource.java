package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.net.HttpUtilsImage;

import org.apache.lucene.util.packed.PackedInts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Class that handles image information.
 *
 * Created by Jackiexing on 12/09/21
 */
public class ImageDataSource {
    public static final String DOWNLOAD_IMAGE_FAILED = "Something wrong when download image !";

    private final String TAG = "ImageDataSource";
    private final MutableLiveData<Result> brainListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> downloadImageResult = new MutableLiveData<>();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LiveData<Result> getBrainListResult() {
        return brainListResult;
    }

    public MutableLiveData<Result> getDownloadImageResult() {
        return downloadImageResult;
    }

    public void getBrainList(){
        try {
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsImage.getBrainListWithOkHttp(userInfo, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    brainListResult.postValue(new Result.Error(new Exception("Connect failed when get Brain List")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            String str = response.body().string();
                            Log.e("GetBrainList", str);
                            JSONArray jsonArray = new JSONArray(str);
                            brainListResult.postValue(new Result.Success<JSONArray>(jsonArray));
                            response.body().close();
                            response.close();
                        } else {
                            brainListResult.postValue(new Result.Error(new Exception("Response from server is null !")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brainListResult.postValue(new Result.Error(new Exception("Fail to get brain list !")));
                    }
                }
            });
        } catch (Exception exception) {
            brainListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getNeuronList(String brainId){
        try {
            HttpUtilsImage.getNeuronListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), brainId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    brainListResult.postValue(new Result.Error(new Exception("Connect failed when get neuron list")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            brainListResult.postValue(new Result.Success<JSONArray>(jsonArray));
                        } else {
                            brainListResult.postValue(new Result.Error(new Exception("Response from server is null !")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brainListResult.postValue(new Result.Error(new Exception("Fail to get neuron list !")));
                    }
                }
            });
        } catch (Exception exception) {
            brainListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getAnoList(String neuronId){
        try {
            HttpUtilsImage.getAnoListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), neuronId, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    brainListResult.postValue(new Result.Error(new Exception("Connect failed when get ano list")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.body() != null) {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            brainListResult.postValue(new Result.Success<JSONArray>(jsonArray));
                        } else {
                            brainListResult.postValue(new Result.Error(new Exception("Response from server is null !")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brainListResult.postValue(new Result.Error(new Exception("Fail to get ano list !")));
                    }
                }
            });
        } catch (Exception exception) {
            brainListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void downloadImage(String brainId, String res, int offsetX, int offsetY, int offsetZ, int size){
        try {

            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            JSONObject loc = new JSONObject().put("x", offsetX).put("y", offsetY).put("z", offsetZ);
            Log.d(TAG, brainId + loc.toString());

            HttpUtilsImage.downloadImageWithOkHttp(userInfo, brainId, res, loc, size, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download Music")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            if (response.body() != null) {
                                byte[] fileContent = response.body().bytes();

                                Log.e(TAG, "file size: " + fileContent.length);
                                String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
                                String filename = brainId + "_" + res + "_" + offsetX + "_" + offsetY + "_" + offsetZ + ".v3dpbd";

                                if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                                    downloadImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
                                }
                                downloadImageResult.postValue(new Result.Success(storePath + "/" + filename));
                                response.body().close();
                                response.close();
                            } else {
                                downloadImageResult.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
                            }
                        } else {
                            downloadImageResult.postValue(new Result.Error(new Exception("Response from server is error when download image !")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        downloadImageResult.postValue(new Result.Error(new Exception("Fail to download image file !")));
                    }
                }
            });
        } catch (Exception exception) {
            downloadImageResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

}
