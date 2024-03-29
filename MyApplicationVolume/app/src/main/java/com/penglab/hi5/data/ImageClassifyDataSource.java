package com.penglab.hi5.data;

import static com.penglab.hi5.chat.nim.main.helper.MessageHelper.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.net.HttpUtilsRating;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ImageClassifyDataSource {

    public static final String UPLOAD_SUCCESSFULLY = "Upload soma successfully !";
    public static final String NO_MORE_FILE = "No more file need to process !";



    private final MutableLiveData<Result> ratingImageListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> updateRatingImageResult = new MutableLiveData<>();
    private final MutableLiveData<Result> downloadRatingImageResult = new MutableLiveData<>();


    public MutableLiveData<Result> getRatingImageListResult() {

        return ratingImageListResult;
    }

    public MutableLiveData<Result> getUpdateRatingImageResult() {

        return updateRatingImageResult;
    }

    public MutableLiveData<Result> downloadRatingImageResult() {

        return downloadRatingImageResult;
    }

    public void getRatingImageList()
    {
        try {
            Log.e(TAG,"start getRatingImageList");
            HttpUtilsRating.getRattingImageListWithOkHttp(InfoCache.getAccount(),InfoCache.getToken(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ratingImageListResult.postValue(new Result.Error(new Exception("Connect failed when get rating image list !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG,"receive response");
                    int responseCode = response.code();
                    String responseData = response.body().string();
                    if (responseCode == 200) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(responseData);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        JSONArray imageNameList = null;
                        try {
                            imageNameList = jsonResponse.getJSONArray("ImageNameList");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        for (int i = 0; i < imageNameList.length(); i++) {
                            String imageName = null;
                            try {
                                imageName = imageNameList.getString(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("Image Name: " + imageName);
                        }

                    } else {
                        Log.e(TAG,"response: " + response.body().string());
                        ratingImageListResult.postValue(new Result.Error(new Exception("Fail to get rating image list !")));
                    }
                    response.close();
                }
            });
        } catch (Exception exception) {
            ratingImageListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void updateImageResult(String imageName,String ratingEnum,String additionalRatingDescription)
    {
        try {
            Log.e(TAG,"start getUpdateImageResult");
            HttpUtilsRating.updateRatingResultWithOkHttp(InfoCache.getAccount(),InfoCache.getToken(), imageName,ratingEnum, additionalRatingDescription,new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    updateRatingImageResult.postValue(new Result.Error(new Exception("Connect failed when getUpdateImageResult !")));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG,"receive response");
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        // process response
                        updateRatingImageResult.postValue(new Result.Success<String>(UPLOAD_SUCCESSFULLY));
                    } else {
                        Log.e(TAG,"response: " + response.body().string());
                        updateRatingImageResult.postValue(new Result.Error(new Exception("Fail to getUpdateImageResult !")));
                    }
                    response.close();
                }
            });
        } catch (Exception exception) {
            updateRatingImageResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getDownloadRatingImage(String imageName){
        try{
            HttpUtilsRating.downloadRattingImage(imageName, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            downloadRatingImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download bouton Image")));
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                int responseCode = response.code();
                                Log.e(TAG, "download_rating_Image_responseCode" + responseCode);
                                if (responseCode == 200) {
                                    if (response.body() != null) {
                                        byte[] fileContent = response.body().bytes();
                                        Log.e(TAG, "file size: " + fileContent.length);
                                        String storePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image";
                                        String filename = imageName;
                                        if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                                            downloadRatingImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
                                        }
                                        downloadRatingImageResult.postValue(new Result.Success(storePath + "/" + filename));
                                        response.body().close();
                                        response.close();
                                    } else {
                                        downloadRatingImageResult.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
                                    }
                                } else {
                                    downloadRatingImageResult.postValue(new Result.Error(new Exception("Response from server is error when download image !")));
                                }
                            } catch (Exception exception) {
                                downloadRatingImageResult.postValue(new Result.Error(new Exception("Fail to download image file !")));
                            }
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
