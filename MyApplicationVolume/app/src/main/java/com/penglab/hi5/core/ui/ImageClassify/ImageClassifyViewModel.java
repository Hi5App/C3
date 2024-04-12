package com.penglab.hi5.core.ui.ImageClassify;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsRating;
import com.penglab.hi5.data.ImageClassifyDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.UserInfoRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ImageClassifyViewModel extends ViewModel {
    private final String TAG = "ImageClassifyViewModel";
    private final UserInfoRepository mUserInfoRepository;
    private final ImageInfoRepository mImageInfoRepository;
    private final ImageClassifyDataSource mImageClassifyDataSource;

    private Deque<RatingImageInfo> mPreviousRatingImagesInfoDeque = new ArrayDeque<>();
    private final MutableLiveData<RatingImageInfo> mCurrentRatingImageInfo = new MutableLiveData<>();
    private Deque<RatingImageInfo> mNextRatingImagesInfoDeque = new ArrayDeque<>();
    private final MutableLiveData<RatingImageInfo> mReScheduledDownloadImageInfo = new MutableLiveData<>();

    public ImageClassifyViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, ImageClassifyDataSource imageClassifyDataSource) {
        this.mUserInfoRepository = userInfoRepository;
        this.mImageInfoRepository = imageInfoRepository;
        this.mImageClassifyDataSource = imageClassifyDataSource;
    }

//    private final Object mLock = new Object();

    public Deque<RatingImageInfo> getNextRatingImagesInfoDeque() {
        return mNextRatingImagesInfoDeque;
    }

    public boolean isNextImageDequeDownloadCompleted() {
        for (RatingImageInfo ratingImageInfo : mNextRatingImagesInfoDeque) {
            if (!isImageFileExist(ratingImageInfo)) {
                return false;
            }
        }
        return true;
    }

    private void RequestGetImages(int count) {
        HttpUtilsRating.getRattingImageListWithOkHttp(
                InfoCache.getAccount(), InfoCache.getToken(), count, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Connect failed when get rating image list !");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e(TAG, "receive response");
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            String str = response.body().string();
                            if (str != null) {
                                Log.e("Get rating list", str);
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    if (!jsonObject.isNull("ImageNameList")) {
                                        JSONArray imageNameList = jsonObject.getJSONArray("ImageNameList");
                                        for (int i = 0; i < imageNameList.length(); i++) {
                                            String imageName = imageNameList.getString(i);
                                            RatingImageInfo ratingImageInfo = new RatingImageInfo();
                                            ratingImageInfo.ImageName = imageName;
                                            ratingImageInfo.Instant = Instant.now();
                                            ratingImageInfo.IsDownloadCompleted = false;
                                            ratingImageInfo.LocalImageFile = "";
                                            ratingImageInfo.IsDownloading = false;
                                            ratingImageInfo.DownloadFailed = false;
//                                            synchronized (mLock) {
                                            mNextRatingImagesInfoDeque.addLast(ratingImageInfo);
//                                            }
                                            response.close();
                                        }
                                    } else {
                                        ToastEasy("ImageInfoList is null! Maybe there are no more images to classify!");
                                        Log.e("TAG", "imageInfoList is null");
                                    }

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        response.close();
                    }
                });
    }

    public boolean isImageFileExist(RatingImageInfo imageInfo) {
        if (imageInfo == null || !imageInfo.IsDownloadCompleted || imageInfo.LocalImageFile.isEmpty()) {
            return false;
        }

        String imageFilePath = imageInfo.LocalImageFile;
        File file = new File(imageFilePath);
        return file.exists();
    }

    public MutableLiveData<RatingImageInfo> acquireReScheduledDownloadImageInfo() {
        return mReScheduledDownloadImageInfo;
    }

    public void acquireImagesManually() {
//        synchronized (mLock) {
        if (mNextRatingImagesInfoDeque.size() >= 15) {
            return;
        }

        if (mNextRatingImagesInfoDeque.size() >= 10 && mNextRatingImagesInfoDeque.size() < 15) {
            RequestGetImages(1);
            return;
        }

        RequestGetImages(10);
//        }
    }

    public RatingImageInfo acquirePreviousImage() {
//        synchronized (mLock) {
        if (mPreviousRatingImagesInfoDeque.isEmpty()) {
            return null;
        }

        if (mCurrentRatingImageInfo.getValue() != null) {
            mNextRatingImagesInfoDeque.addFirst(mCurrentRatingImageInfo.getValue());
        }

        mCurrentRatingImageInfo.postValue(mPreviousRatingImagesInfoDeque.pollLast());
//        }
        return mCurrentRatingImageInfo.getValue();
    }

    public MutableLiveData<RatingImageInfo> acquireCurrentImage() {
        return mCurrentRatingImageInfo;
    }

    public RatingImageInfo acquireNextImage() {
        if (mReScheduledDownloadImageInfo.getValue() != null) {
            ToastEasy("Current Image is not being downloaded! Please waiting... When download finished, the image will be shown automatically!");
            return null;
        }

//        synchronized (mLock) {
        if (mNextRatingImagesInfoDeque.isEmpty()) {
            return null;
        }

        if (mCurrentRatingImageInfo.getValue() != null) {
            mPreviousRatingImagesInfoDeque.addLast(mCurrentRatingImageInfo.getValue());
            if (mPreviousRatingImagesInfoDeque.size() > 10) {
                mPreviousRatingImagesInfoDeque.pollFirst();
            }
        }

        mCurrentRatingImageInfo.postValue(mNextRatingImagesInfoDeque.pollFirst());

        if (mNextRatingImagesInfoDeque.size() < 10) {
            RequestGetImages(1);
        }

//        }
        return mCurrentRatingImageInfo.getValue();
    }

    public void reScheduleDownloadImageFileAsync(RatingImageInfo ratingImageInfo) {
        if (mCurrentRatingImageInfo.getValue() == null) {
            return;
        }

        if (!mCurrentRatingImageInfo.getValue().DownloadFailed && !mCurrentRatingImageInfo.getValue().IsDownloadCompleted && !mCurrentRatingImageInfo.getValue().IsDownloading) {
            mCurrentRatingImageInfo.getValue().IsDownloading = true;

            downloadImageFileAsync(ratingImageInfo);
        }

        mReScheduledDownloadImageInfo.postValue(mCurrentRatingImageInfo.getValue());
    }

    public void downloadImageFileAsync(RatingImageInfo ratingImageInfo) {
        if (ratingImageInfo == null) {
            return;
        }

//        synchronized (mLock) {
        if (ratingImageInfo.IsDownloadCompleted) {
            return;
        }
//        }

        HttpUtilsRating.downloadFile(ratingImageInfo, this);
    }

    public void uploadUserRatingResult(String ratingEnum, String additionalRatingDescription) {
        if (mCurrentRatingImageInfo.getValue() == null) {
            ToastEasy("Current Image is null! Click Next To Continue!");
            return;
        }

        String imageName = mCurrentRatingImageInfo.getValue().ImageName;

        if (mCurrentRatingImageInfo.getValue().IsDownloadCompleted && !mCurrentRatingImageInfo.getValue().LocalImageFile.isEmpty()) {
            HttpUtilsRating.uploadUserRatingResultWithOkHttp(
                    InfoCache.getAccount(), InfoCache.getToken(), mCurrentRatingImageInfo.getValue().ImageName, ratingEnum, additionalRatingDescription, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "Connect failed when upload user rating result !");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            int responseCode = response.code();
                            if (responseCode == 200) {
                                if (response.body() != null) {
                                    String responseBody = response.body().string();
                                    Log.e(TAG, "upload rating result response: " + responseBody);

                                    JSONObject jsonResponse = null;
                                    try {
                                        jsonResponse = new JSONObject(responseBody);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    response.close();

                                    String status = jsonResponse.optString("Status", "");
                                    if (status.equals("OK")) {
                                        ToastEasy("Upload rating result successfully! \nImage: " + imageName + " \nRating: " + ratingEnum + " \nDescription: " + additionalRatingDescription);
                                    } else {
                                        ToastEasy("Failed to uploadUserRatingResult: " + responseBody);
                                    }
                                } else {
                                    Log.e(TAG, "Response from server is null when upload user rating result !");
                                }
                            } else {
                                Log.e(TAG, "Response from server is error when upload user rating result !");
                            }
                            response.close();
                        }
                    });
        } else {
            ToastEasy("Current Image is not being downloaded! Please waiting...");
        }
    }

    public boolean isLoggedIn() {
        return mUserInfoRepository.isLoggedIn();
    }

    public ImageInfoRepository getImageInfoRepository() {
        return mImageInfoRepository;
    }

}
