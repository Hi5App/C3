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
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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
    private final MutableLiveData<Result> downloadButtonImageResult = new MutableLiveData<>();
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LiveData<Result> getBrainListResult() {
        return brainListResult;
    }

    public MutableLiveData<Result> getDownloadImageResult() {
        return downloadImageResult;
    }

    public MutableLiveData<Result> getDownloadButtonImageResult() {
        return downloadButtonImageResult;
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
                            Log.e(TAG,"getNeuronList"+jsonArray);
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
            JSONObject pa1 = new JSONObject().put("x", offsetX - size/2).put("y", offsetY - size/2).put("z", offsetZ - size/2);
            JSONObject pa2 = new JSONObject().put("x", offsetX + size/2).put("y", offsetY + size/2).put("z", offsetZ + size/2);
            JSONObject bBox = new JSONObject().put("pa1", pa1).put("pa2", pa2).put("res", res).put("obj", brainId);
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());

            HttpUtilsImage.downloadImageWithOkHttp(userInfo, bBox, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download Image")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        int responseCode = response.code();
                        Log.e(TAG,"downloadImage_responseCode"+responseCode);
                        if (responseCode == 200) {
                            if (response.body() != null) {
                                byte[] fileContent = response.body().bytes();
                                Log.e(TAG, "file size: " + fileContent.length);
                                String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
                                String filename = brainId + "_" + res + "_"  + offsetX + "_" + offsetY + "_" + offsetZ + ".v3dpbd";

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

    public void downloadImage(String brainId, String res, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax){
        try {
            JSONObject pa1 = new JSONObject().put("x", xMin).put("y", yMin).put("z", zMin);
            JSONObject pa2 = new JSONObject().put("x", xMax).put("y", yMax).put("z", zMax);
            JSONObject bBox = new JSONObject().put("pa1", pa1).put("pa2", pa2).put("res", res).put("obj", brainId);
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());

            HttpUtilsImage.downloadImageWithOkHttp(userInfo, bBox, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download Image")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        int responseCode = response.code();
                        Log.e(TAG,"downloadImage_responseCode"+responseCode);
                        if (responseCode == 200) {
                            if (response.body() != null) {
                                byte[] fileContent = response.body().bytes();
                                Log.e(TAG, "file size: " + fileContent.length);
                                String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
                                String filename = brainId + "_" + res + "_"  + xMin + "_" + xMax + "_" + yMin + "_" + yMax + "_" + zMin + "_" + zMax + ".v3dpbd";

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

    public void downloadButtonImage(String arborId){
        try{
            JSONObject user = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsImage.downloadButtonImageWithOkHttp(user,
                    arborId,
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            downloadButtonImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download bouton Image")));
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                int responseCode = response.code();
                                Log.e(TAG, "download_bouton_Image_responseCode" + responseCode);
                                if (responseCode == 200) {
                                    if (response.body() != null) {
                                        byte[] fileContent = response.body().bytes();
                                        Log.e(TAG, "file size: " + fileContent.length);
                                        String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
                                        String filename = arborId+".v3dpbd";
                                        //                            String filename = brainId + "_" + res + "_"  + offsetX + "_" + offsetY + "_" + offsetZ + ".v3dpbd";

                                        if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                                            downloadButtonImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
                                        }
                                        downloadButtonImageResult.postValue(new Result.Success(storePath + "/" + filename));
                                        response.body().close();
                                        response.close();
                                    } else {
                                        downloadButtonImageResult.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
                                    }
                                } else {
                                    downloadButtonImageResult.postValue(new Result.Error(new Exception("Response from server is error when download image !")));
                                }
                            } catch (Exception exception) {
                                downloadButtonImageResult.postValue(new Result.Error(new Exception("Fail to download image file !")));
                            }
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
