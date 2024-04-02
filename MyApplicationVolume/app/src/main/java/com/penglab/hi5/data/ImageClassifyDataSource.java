package com.penglab.hi5.data;

import static com.penglab.hi5.chat.nim.main.helper.MessageHelper.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.net.HttpUtilsRating;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ImageClassifyDataSource {
    public static final String UPLOAD_SUCCESSFULLY = "Upload user result successfully !";

    public static final String NO_MORE_FILE = "No more file need to process !";
    private final MutableLiveData<Result> ratingImageListResult = new MutableLiveData<>();
    private final MutableLiveData<Result> uploadUserRatingResult = new MutableLiveData<>();
    private final MutableLiveData<Result> downloadSingleRatingImageResult = new MutableLiveData<>();

    public MutableLiveData<Result> getRatingImageListResult() {

        return ratingImageListResult;
    }

    public MutableLiveData<Result> getUploadUserRatingResult() {

        return uploadUserRatingResult;
    }

    public MutableLiveData<Result> getDownloadSingleRatingImageResult() {

        return downloadSingleRatingImageResult;
    }

    public void getRatingImageListResponse()
    {
        try {
            Log.e(TAG,"start getRatingImageListResponse");
            HttpUtilsRating.getRattingImageListWithOkHttp(InfoCache.getAccount(),InfoCache.getToken(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ratingImageListResult.postValue(new Result.Error(new Exception("Connect failed when get rating image list !")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG,"receive response");
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        String str = response.body().string();
                        if (str != null) {
                            Log.e("Get rating list", str);
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(str);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            ratingImageListResult.postValue(new Result.Success<JSONObject>(jsonObject));
                            response.body().close();
                            response.close();
                        } else {
                            ratingImageListResult.postValue(new Result.Success<String>(NO_MORE_FILE));
                            response.close();
                        }
                    } else {
                        ratingImageListResult.postValue(new Result.Error(new Exception("Fail to get rating image list !")));
                    }
                    response.close();
                }
            });
        } catch (Exception exception) {
            ratingImageListResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void uploadUserRatingResultResponse(String imageName, String ratingEnum, String additionalRatingDescription)
    {
        try {
            HttpUtilsRating.uploadUserRatingResultWithOkHttp(InfoCache.getAccount(),InfoCache.getToken(), imageName,ratingEnum, additionalRatingDescription,new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    uploadUserRatingResult.postValue(new Result.Error(new Exception("Connect failed when getUpdateImageResult !")));
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG,"receive response");
                    int responseCode = response.code();
                    if (responseCode == 200) {
                        uploadUserRatingResult.postValue(new Result.Success<String>(UPLOAD_SUCCESSFULLY));
                    } else {
                        Log.e(TAG,"response: " + response.body().string());
                        uploadUserRatingResult.postValue(new Result.Error(new Exception("Fail to getUpdateImageResult !")));
                    }
                    response.close();
                }
            });
        } catch (Exception exception) {
            uploadUserRatingResult.postValue(new Result.Error(new IOException("Check the network please !", exception)));
        }
    }

    public void getDownloadSingleRatingImageResponse(String imageName){
        try{
            HttpUtilsRating.downloadSingleRattingImageWithOkHttp(imageName, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            downloadSingleRatingImageResult.postValue(new Result.Error(new Exception("Connect Failed When Download bouton Image")));
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
                                            downloadSingleRatingImageResult.postValue(new Result.Error(new Exception("Fail to store image file !")));
                                        }
                                        downloadSingleRatingImageResult.postValue(new Result.Success(storePath + "/" + filename));
                                        response.body().close();
                                        response.close();
                                    } else {
                                        downloadSingleRatingImageResult.postValue(new Result.Error(new Exception("Response from server is null when download image !")));
                                    }
                                } else {
                                    downloadSingleRatingImageResult.postValue(new Result.Error(new Exception("Response from server is error when download image !")));
                                }
                            } catch (Exception exception) {
                                downloadSingleRatingImageResult.postValue(new Result.Error(new Exception("Fail to download image file !")));
                            }
                        }
                    });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
