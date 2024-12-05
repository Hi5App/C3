package com.penglab.hi5.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.net.HttpUtilsPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class that handles image information.
 * <p>
 * Created by Jackiexing on 12/09/21
 */
public class PluginDataSource {
    public static final String DOWNLOAD_IMAGE_FAILED = "Something wrong when download image !";

    private final String TAG = "PluginDataSource";
    private final MutableLiveData<Result> pluginListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> imageListResult = new MutableLiveData<>();

    public final MutableLiveData<Result> downloadPluginImageResult = new MutableLiveData<>();

    private final MutableLiveData<Result> originImageResult = new MutableLiveData<>();

    public LiveData<Result> getPluginListResult() {
        return pluginListResult;
    }

    public LiveData<Result> getImageListResult() {
        return imageListResult;
    }

    public MutableLiveData<Result> getOriginImageResult() {
        return originImageResult;
    }

    public final MutableLiveData<Result> getDownloadPluginImageResult() {
        return downloadPluginImageResult;
    }

    public void getPluginList() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(HttpUtilsPlugin.UrlGetMethodList).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    pluginListResult.postValue(new Result.Error(new Exception("Connect failed when get method list")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        Log.e("response", String.valueOf(response.code()));
                        pluginListResult.postValue(new Result.Error(new Exception("Connect failed when get method list")));
                        response.close();
                        return;
                    }
                    String body = response.body().string();
                    String[] pNames = new String[0];
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(body);
                        if (jsonObject.has("response")) {
                            pNames = new String[jsonObject.getJSONArray("response").length()];
                            for (int i = 0; i < jsonObject.getJSONArray("response").length(); i++) {
                                Log.e("response", jsonObject.getJSONArray("response").get(i).toString());
                                pNames[i] = jsonObject.getJSONArray("response").get(i).toString();
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    pluginListResult.postValue(new Result.Success<>(pNames));
                    response.body().close();
                    response.close();
                }
            });
        } catch (Exception exception) {
            pluginListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getImageList() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(HttpUtilsPlugin.UrlGetImageList).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    imageListResult.postValue(new Result.Error(new Exception("Connect failed when get image list")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        Log.e("response", String.valueOf(response.code()));
                        imageListResult.postValue(new Result.Error(new Exception("Connect failed when get image list")));
                        return;
                    }
                    String body = response.body().string();
                    String[] fileNames = new String[0];
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(body);
                        if (jsonObject.has("response")) {
                            fileNames = new String[jsonObject.getJSONArray("response").length()];
                            for (int i = 0; i < jsonObject.getJSONArray("response").length(); i++) {
                                Log.e("response", jsonObject.getJSONArray("response").get(i).toString());
                                fileNames[i] = jsonObject.getJSONArray("response").get(i).toString();
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    imageListResult.postValue(new Result.Success<>(fileNames));
                    response.body().close();
                    response.close();
                }
            });
        } catch (Exception exception) {
            imageListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void doPlugin(String imageName, String pluginName) {
        try {
            OkHttpClient client = new OkHttpClient();
            String body = (new JSONObject().
                    put("method_name", pluginName).
                    put("image_name", imageName).
                    put("session_id", InfoCache.getAccount() + "_" + UUID.randomUUID().toString()).
                    put("user_name", InfoCache.getAccount()).
                    put("force_regenerate", true)
            ).toString();
            Request request = new Request.Builder().url(HttpUtilsPlugin.UrlExecuteMethod).
                    post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadPluginImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download Image")));
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        int responseCode = response.code();
                        if (responseCode == 200) {
                                String bodyJson = response.body().string();
                                downloadPluginImageResult.postValue(new Result.Success(bodyJson));
                                response.body().close();
                                response.close();
                        } else {
                            Log.e("response", String.valueOf(responseCode));
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

    public void getImageFile(String pathPrefix, String imageName) {
        try {
            OkHttpClient client = new OkHttpClient();
            String body = (new JSONObject().put("image_path", pathPrefix).put("image_name", imageName)).toString();
            Request request = new Request.Builder().url(HttpUtilsPlugin.UrlGetImageFile).
                    post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    originImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download Image")));
                }
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            if (response.body() != null) {
                                byte[] fileContent = response.body().bytes();
                                Log.e(TAG, "file size: " + fileContent.length);
                                String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
                                String filename = imageName+".zip";
                                if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                                    originImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
                                } else {
                                    String zipFilePath = storePath + "/" + filename;
                                    if (unzipFile(zipFilePath, storePath)) {
                                        originImageResult.postValue(new Result.Success(storePath + "/" + filename));
                                    } else {
                                        originImageResult.postValue(new Result.Error(new Exception("Fail to unzip image file !")));
                                    }
                                }
//                                originImageResult.postValue(new Result.Success(storePath + "/" + filename));
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

    private boolean unzipFile(String zipFilePath, String destDirectory) {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            // Iterate over entries in the zip file
            while (entry != null) {
                File filePath = new File(destDirectory, entry.getName());
                if (entry.isDirectory()) {
                    // If the entry is a directory, make the directory
                    if (!filePath.isDirectory() && !filePath.mkdirs()) {
                        throw new IOException("Failed to create directory " + filePath);
                    }
                } else {
                    // If the entry is a file, extracts it
                    File parentDir = filePath.getParentFile();
                    if (!parentDir.isDirectory() && !parentDir.mkdirs()) {
                        throw new IOException("Failed to create directory " + parentDir);
                    }
                    extractFile(zipIn, filePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } catch (IOException e) {
            Log.e(TAG, "Unzipping error: ", e);
            return false;
        }
        return true;
    }

    private void extractFile(ZipInputStream zipIn, File filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}
