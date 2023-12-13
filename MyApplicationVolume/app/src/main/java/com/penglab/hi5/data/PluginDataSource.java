package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.net.HttpUtilsImage;
import com.penglab.hi5.core.net.HttpUtilsPlugin;
import com.penglab.hi5.core.ui.ResourceResult;

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
public class PluginDataSource {
    public static final String DOWNLOAD_IMAGE_FAILED = "Something wrong when download image !";

    private final String TAG = "PluginDataSource";
    private final MutableLiveData<Result> pluginListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> imageListResult = new MutableLiveData<>();

    private final MutableLiveData<Result> downloadPluginImageResult = new MutableLiveData<>();

    private final MutableLiveData<Result> originImageResult = new MutableLiveData<>();

    private final MutableLiveData<Result> modelImageResult = new MutableLiveData<>();

    public LiveData<Result> getPluginListResult() {
        return pluginListResult;
    }
    public LiveData<Result> getImageListResult() {
        return imageListResult;
    }

    public MutableLiveData<Result> getDownloadPluginImageResult() {
        return downloadPluginImageResult;
    }

    public MutableLiveData<Result> getOriginImageResult() {
        return originImageResult;
    }

    public MutableLiveData<Result> getModelImageResult() {
        return modelImageResult;
    }

    public void getPluginList() {
        try {
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsPlugin.getPluginListWithOkHttp(userInfo, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    pluginListResult.postValue(new Result.Error(new Exception("Connect failed when get Brain List")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.body() != null) {
                        String str = response.body().string();
                        Log.e("GetPluginList", str);
                        try{
                            str = str.replace("[", "").replace("]", "");
                            String[] pluginName = str.split(",");

                            String[] pNames = new String[pluginName.length];
                            for (int i = 0; i < pluginName.length; i++) {
                                String path = pluginName[i];
                                // 使用"/"分割路径，并取最后一个部分作为文件名
                                pNames[i] = path.replace("\"","");
                            }

                            pluginListResult.postValue(new Result.Success<String[]>(pNames));
                        } catch (Exception e){
                            e.printStackTrace();
                            pluginListResult.postValue(new Result.Error(new Exception("Fail to parse brain list info !")));
                        }
                        response.body().close();
                        response.close();
                    } else {
                        pluginListResult.postValue(new Result.Error(new Exception("Response from server is null !")));
                    }
                }
            });
        } catch (Exception exception) {
            pluginListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getImageList() {
        try {
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsPlugin.getImageListWithOkHttp(userInfo, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    imageListResult.postValue(new Result.Error(new Exception("Connect failed when get Brain List")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int responseCode = response.code();
                    Log.e("GetImageListResponseCode",""+responseCode);
                    try {
                        if (response.body() != null) {
                            String str = response.body().string();
                            Log.e("GetImageList", str);
                            str= str.replaceAll("\\\"","");
                            str = str.replace("[", "").replace("]", "");
                            String[] paths = str.split(",");
                            String[] fileNames = new String[paths.length];
                            for (int i = 0; i < paths.length; i++) {
                                String path = paths[i];
                                // 使用"/"分割路径，并取最后一个部分作为文件名
                                fileNames[i] = path.substring(path.lastIndexOf("/") + 1);

                            }
                            Log.e("filenames",""+fileNames[0]);
                            imageListResult.postValue(new Result.Success<String[]>(fileNames));
                            response.body().close();
                            response.close();
                        } else {
                            imageListResult.postValue(new Result.Error(new Exception("Response from server is null !")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        imageListResult.postValue(new Result.Error(new Exception("Fail to get image list !")));
                    }
                }
            });
        } catch (Exception exception) {
            imageListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void doPlugin(String imageName, String pluginName){
        try {
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsPlugin.doPluginListWithOkHttp(userInfo, imageName,pluginName, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadPluginImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download Image")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        int responseCode = response.code();
                        Log.e(TAG,"doPlugin_responseCode"+responseCode);
                        if (responseCode == 200) {
                            if (response.body() != null) {
                                byte[] fileContent = response.body().bytes();
                                Log.e(TAG, "file size: " + fileContent.length);
                                String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
                                String filename = imageName;
                                if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                                    downloadPluginImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
                                }
                                downloadPluginImageResult.postValue(new Result.Success(storePath + "/" + filename));
                                response.body().close();
                                response.close();
                            } else {
                                downloadPluginImageResult.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
                            }
                        } else {
                            downloadPluginImageResult.postValue(new Result.Error(new Exception("Response from server is error when download image !")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        downloadPluginImageResult.postValue(new Result.Error(new Exception("Fail to download image file !")));
                    }
                }
            });
        } catch (Exception exception) {
            downloadPluginImageResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getOriginImage(String imageName){
        try {
            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
            HttpUtilsPlugin.getOriginImageWithOkHttp(userInfo, imageName,new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    originImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download Image")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        int responseCode = response.code();
                        Log.e(TAG,"doPlugin_responseCode"+responseCode);
                        if (responseCode == 200) {
                            if (response.body() != null) {
                                byte[] fileContent = response.body().bytes();
                                Log.e(TAG, "file size: " + fileContent.length);
                                String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
                                String filename = imageName;
                                if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                                    originImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
                                }
                                originImageResult.postValue(new Result.Success(storePath + "/" + filename));
                                response.body().close();
                                response.close();
                            } else {
                                originImageResult.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
                            }
                        } else {
                            originImageResult.postValue(new Result.Error(new Exception("Response from server is error when download image !")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        originImageResult.postValue(new Result.Error(new Exception("Fail to download image file !")));
                    }
                }
            });
        } catch (Exception exception) {
            originImageResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getModelImage(String imageName){
         try {
        JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
        HttpUtilsPlugin.doModelWithOkHttp(userInfo, imageName,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                modelImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download Image")));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    int responseCode = response.code();
                    Log.e(TAG,"doModel_responseCode"+responseCode);
                    if (responseCode == 200) {
                        if (response.body() != null) {
                            byte[] fileContent = response.body().bytes();
                            Log.e(TAG, "file size: " + fileContent.length);
                            String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
                            String filename = imageName;
                            if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                                modelImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
                            }
                            modelImageResult.postValue(new Result.Success(storePath + "/" + filename));
                            response.body().close();
                            response.close();
                        } else {
                            modelImageResult.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
                        }
                    } else {
                        modelImageResult.postValue(new Result.Error(new Exception("Response from server is error when download image !")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    modelImageResult.postValue(new Result.Error(new Exception("Fail to download image file !")));
                }
            }
        }) ;
    } catch (Exception exception) {
             modelImageResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
    }
}





//    public void getNeuronList(String brainId){
//        try {
//            HttpUtilsImage.getNeuronListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), brainId, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    brainListResult.postValue(new Result.Error(new Exception("Connect failed when get neuron list")));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    try {
//                        if (response.body() != null) {
//                            JSONArray jsonArray = new JSONArray(response.body().string());
//                            Log.e(TAG,"getNeuronList"+jsonArray);
//                            brainListResult.postValue(new Result.Success<JSONArray>(jsonArray));
//                        } else {
//                            brainListResult.postValue(new Result.Error(new Exception("Response from server is null !")));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        brainListResult.postValue(new Result.Error(new Exception("Fail to get neuron list !")));
//                    }
//                }
//            });
//        } catch (Exception exception) {
//            brainListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
//        }
//    }
//
//    public void getAnoList(String neuronId){
//        try {
//            HttpUtilsImage.getAnoListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), neuronId, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    brainListResult.postValue(new Result.Error(new Exception("Connect failed when get ano list")));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    try {
//                        if (response.body() != null) {
//                            JSONArray jsonArray = new JSONArray(response.body().string());
//                            brainListResult.postValue(new Result.Success<JSONArray>(jsonArray));
//                        } else {
//                            brainListResult.postValue(new Result.Error(new Exception("Response from server is null !")));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        brainListResult.postValue(new Result.Error(new Exception("Fail to get ano list !")));
//                    }
//                }
//            });
//        } catch (Exception exception) {
//            brainListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
//        }
//    }
//
//    public void downloadImage(String brainId, String res, int offsetX, int offsetY, int offsetZ, int size){
//        try {
//            JSONObject pa1 = new JSONObject().put("x", offsetX - size/2).put("y", offsetY - size/2).put("z", offsetZ - size/2);
//            JSONObject pa2 = new JSONObject().put("x", offsetX + size/2).put("y", offsetY + size/2).put("z", offsetZ + size/2);
//            JSONObject bBox = new JSONObject().put("pa1", pa1).put("pa2", pa2).put("res", res).put("obj", brainId);
//            JSONObject userInfo = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
//
//            HttpUtilsImage.downloadImageWithOkHttp(userInfo, bBox, new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    downloadImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download Image")));
//                }
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    try {
//                        int responseCode = response.code();
//                        Log.e(TAG,"downloadImage_responseCode"+responseCode);
//                        if (responseCode == 200) {
//                            if (response.body() != null) {
//                                byte[] fileContent = response.body().bytes();
//                                Log.e(TAG, "file size: " + fileContent.length);
//                                String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
//                                String filename = brainId + "_" + res + "_"  + offsetX + "_" + offsetY + "_" + offsetZ + ".v3dpbd";
//
//                                if (!FileHelper.storeFile(storePath, filename, fileContent)) {
//                                    downloadImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
//                                }
//                                downloadImageResult.postValue(new Result.Success(storePath + "/" + filename));
//                                response.body().close();
//                                response.close();
//                            } else {
//                                downloadImageResult.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
//                            }
//                        } else {
//                            downloadImageResult.postValue(new Result.Error(new Exception("Response from server is error when download image !")));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        downloadImageResult.postValue(new Result.Error(new Exception("Fail to download image file !")));
//                    }
//                }
//            });
//        } catch (Exception exception) {
//            downloadImageResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
//        }
//    }
//
//    public void downloadButtonImage(String arborId){
//        try{
//            JSONObject user = new JSONObject().put("name", InfoCache.getAccount()).put("passwd", InfoCache.getToken());
//            HttpUtilsImage.downloadButtonImageWithOkHttp(user,
//                    arborId,
//                    new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            downloadButtonImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download bouton Image")));
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            try {
//                                int responseCode = response.code();
//                                Log.e(TAG, "download_bouton_Image_responseCode" + responseCode);
//                                if (responseCode == 200) {
//                                    if (response.body() != null) {
//                                        byte[] fileContent = response.body().bytes();
//                                        Log.e(TAG, "file size: " + fileContent.length);
//                                        String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
//                                        String filename = arborId+".v3dpbd";
//                                        //                            String filename = brainId + "_" + res + "_"  + offsetX + "_" + offsetY + "_" + offsetZ + ".v3dpbd";
//
//                                        if (!FileHelper.storeFile(storePath, filename, fileContent)) {
//                                            downloadButtonImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
//                                        }
//                                        downloadButtonImageResult.postValue(new Result.Success(storePath + "/" + filename));
//                                        response.body().close();
//                                        response.close();
//                                    } else {
//                                        downloadButtonImageResult.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
//                                    }
//                                } else {
//                                    downloadButtonImageResult.postValue(new Result.Error(new Exception("Response from server is error when download image !")));
//                                }
//                            } catch (Exception exception) {
//                                downloadButtonImageResult.postValue(new Result.Error(new Exception("Fail to download image file !")));
//                            }
//                        }
//                    });
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}
