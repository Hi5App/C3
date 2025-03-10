package com.penglab.hi5.core.ui.ImageClassify;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.net.HttpUtilsRating;
import com.penglab.hi5.core.ui.QualityInspection.QueryCheckerResult;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ImageClassifyViewModel extends ViewModel {
    private final String TAG = "ImageClassifyViewModel";
    private final UserInfoRepository mUserInfoRepository;
    private final ImageInfoRepository mImageInfoRepository;
    private final ImageClassifyDataSource mImageClassifyDataSource;

    private final Deque<RatingImageInfo> mPreviousRatingImagesInfoDeque = new ArrayDeque<>();
    private final MutableLiveData<RatingImageInfo> mCurrentRatingImageInfo = new MutableLiveData<>();
    private final Deque<RatingImageInfo> mNextRatingImagesInfoDeque = new ArrayDeque<>();
    private final MutableLiveData<RatingImageInfo> mReScheduledDownloadImageInfo = new MutableLiveData<>();

    private final MutableLiveData<List<UserRatingResultInfo>> mUserRatingResultTable = new MutableLiveData<>();

    private final MutableLiveData<List<ClassifySolutionInfo>> mClassifySolutionList = new MutableLiveData<>();
    private final MutableLiveData<List<String>> mClassifyUserNameList = new MutableLiveData<>();

    public ImageClassifyViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, ImageClassifyDataSource imageClassifyDataSource) {
        this.mUserInfoRepository = userInfoRepository;
        this.mImageInfoRepository = imageInfoRepository;
        this.mImageClassifyDataSource = imageClassifyDataSource;
    }

//    private final Object mLock = new Object();

    public Deque<RatingImageInfo> getNextRatingImagesInfoDeque() {
        return mNextRatingImagesInfoDeque;
    }

    public MutableLiveData<List<UserRatingResultInfo>> getmUserRatingResultTable() {
        return mUserRatingResultTable;
    }

    public MutableLiveData<List<ClassifySolutionInfo>> getmClassifySolutionList() {
        return mClassifySolutionList;
    }

    public MutableLiveData<List<String>> getmClassifyUserNameList() {
        return mClassifyUserNameList;
    }

    public synchronized boolean isNextImageDequeDownloadCompleted() {
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
                                        new Handler(Looper.getMainLooper()).post( ()-> ToastEasy("ImageInfoList is null! Maybe there are no more images to classify!"));
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
            RequestGetImages(1);
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

    public void uploadUserRatingResult(String solutionName, String ratingEnum, String additionalRatingDescription) {
        if (mCurrentRatingImageInfo.getValue() == null) {
            ToastEasy("Current Image is null! Click Next To Continue!");
            return;
        }

        String imageName = mCurrentRatingImageInfo.getValue().ImageName;

        if (mCurrentRatingImageInfo.getValue().IsDownloadCompleted && !mCurrentRatingImageInfo.getValue().LocalImageFile.isEmpty()) {
            HttpUtilsRating.uploadUserRatingResultWithOkHttp(
                    InfoCache.getAccount(), InfoCache.getToken(), mCurrentRatingImageInfo.getValue().ImageName, solutionName, ratingEnum, additionalRatingDescription, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "Connect failed when upload user rating result !");
                            new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Connect failed when upload user rating result !", Toast.LENGTH_LONG));
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
                                        new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Upload rating result successfully! \nImage: " + imageName + " \nSolution: " + solutionName + " \nRating: " + ratingEnum + " \nDescription: " + additionalRatingDescription));
                                    } else {
                                        Log.e(TAG, "Failed to uploadUserRatingResult: " + responseBody);
                                        new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Failed to uploadUserRatingResult: " + responseBody));
                                    }
                                } else {
                                    Log.e(TAG, "Response from server is null when upload user rating result !");
                                    new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Response from server is null when upload user rating result !", Toast.LENGTH_LONG));
                                }
                            } else {
                                Log.e(TAG, "Response from server is error when upload user rating result !");
                                new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Response from server is error when upload user rating result !", Toast.LENGTH_LONG));
                            }
                            response.close();
                        }
                    });
        } else {
            ToastEasy("Current Image is not being downloaded! Please waiting...");
        }
    }

    public void requestRatingTable(String querySolutionName, String queryUserName, String queryStartTime, String queryEndTime) {
        HttpUtilsRating.queryUserRatingTableWithOkHttp(
                InfoCache.getAccount(), InfoCache.getToken(), querySolutionName, queryUserName, queryStartTime, queryEndTime, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Connect failed when getting request rating table!");
                        new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Connect failed when getting request rating table!"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e(TAG, "Received response");
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            String str = Objects.requireNonNull(response.body()).string();
                            Log.e("Get rating result", str);
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String status = jsonObject.optString("Status");
                                if (status.equals("OK")) {
                                    if (!jsonObject.isNull("RatingQueryResult")) {
                                        JSONArray ratingResults = jsonObject.getJSONArray("RatingQueryResult");
                                        ArrayList<UserRatingResultInfo> userRatingResultInfoList = new ArrayList<>();
                                        for (int i = 0; i < ratingResults.length(); i++) {
                                            JSONObject ratingResult = ratingResults.getJSONObject(i);
                                            UserRatingResultInfo userRatingResultInfo = new UserRatingResultInfo();
                                            userRatingResultInfo.imageName = ratingResult.optString("ImageName").split("_")[0];
                                            userRatingResultInfo.ratingEnum = ratingResult.optString("RatingEnum");
                                            userRatingResultInfo.userName = ratingResult.optString("UserName");
                                            userRatingResultInfo.solutionName = ratingResult.optString("SolutionName");
                                            userRatingResultInfo.additionalRatingDescription = ratingResult.optString("AdditionalRatingDescription");
                                            userRatingResultInfo.uploadTime = ratingResult.optString("UploadTime");
                                            userRatingResultInfoList.add(userRatingResultInfo);
                                        }
                                        if (!userRatingResultInfoList.isEmpty()) {
                                            mUserRatingResultTable.postValue(userRatingResultInfoList);
                                        } else {
                                            // 发布一个空结果通知的信息
                                            mUserRatingResultTable.postValue(new ArrayList<>());
                                        }
                                    } else {
                                        new Handler(Looper.getMainLooper()).post(() -> ToastEasy("RatingQueryResult is null"));
                                        // 发布一个空结果通知的信息
                                        mUserRatingResultTable.postValue(new ArrayList<>());
                                    }
                                } else {
                                    Log.e(TAG, "Status is not OK: " + status);
                                    new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Status is not OK: " + status));
                                    mUserRatingResultTable.postValue(new ArrayList<>());  // 可以在这里处理错误状态
                                }
                            } catch (JSONException e) {
                                new Handler(Looper.getMainLooper()).post(() -> ToastEasy(e.toString()));
                            }
                        }
                        else{
                            new Handler(Looper.getMainLooper()).post(() -> ToastEasy("responseCode is: " + responseCode));
                        }
                        response.close();
                    }
                });
    }

    public void requestRatingSolutionList() {
        HttpUtilsRating.queryRatingSolutionListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Connect failed when requesting rating solution!");
                new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Connect failed when requesting rating solution!"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e(TAG, "Received response");
                int responseCode = response.code();
                if (responseCode == 200) {
                    String str = null;
                    try {
                        str = Objects.requireNonNull(response.body()).string();
                    } catch (IOException e) {
                        new Handler(Looper.getMainLooper()).post(() -> ToastEasy(e.toString()));
                    }
                    if (str != null) {
                        Log.e("Get rating solution", str);
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            String status = jsonObject.optString("Status");
                            if (status.equals("OK")) {
                                if (!jsonObject.isNull("SolutionResult")) {
                                    JSONArray solutionResults = jsonObject.getJSONArray("SolutionResult");
                                    ArrayList<ClassifySolutionInfo> classifySolutionInfoList = new ArrayList<>();
                                    for (int i = 0; i < solutionResults.length(); i++) {
                                        JSONObject solutionResult = solutionResults.getJSONObject(i);
                                        ClassifySolutionInfo classifySolutionInfo = new ClassifySolutionInfo();
                                        classifySolutionInfo.solutionName = solutionResult.optString("SolutionName");
                                        classifySolutionInfo.solutionDetail = solutionResult.optString("SolutionDetail");
                                        classifySolutionInfoList.add(classifySolutionInfo);
                                    }
                                    mClassifySolutionList.postValue(classifySolutionInfoList);
                                } else {
                                    new Handler(Looper.getMainLooper()).post(() -> ToastEasy("SolutionResult is null"));
                                    mClassifySolutionList.postValue(null);
                                }
                            } else {
                                Log.e(TAG, "Status is not OK: " + status);
                                new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Status is not OK: " + status));
                                mClassifySolutionList.postValue(null);
                            }
                        } catch (JSONException e) {
                            new Handler(Looper.getMainLooper()).post(() -> ToastEasy(e.toString()));
                            mClassifySolutionList.postValue(null);
                        }
                    }
                }
                else {
                    new Handler(Looper.getMainLooper()).post(() -> ToastEasy("responseCode is: " + responseCode));
                }
                response.close();
            }
        });
    }

    public CompletableFuture<Boolean> addRatingSolution(List<ClassifySolutionInfo> addedSolutionList) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(addedSolutionList.size() == 0){
            future.complete(true);
            return future;
        }
        HttpUtilsRating.addRatingSolutionWithOkHttp(
                InfoCache.getAccount(), InfoCache.getToken(), addedSolutionList, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Connect failed when adding rating solution!");
                        new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Connect failed when adding rating solution!"));
                        future.complete(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e(TAG, "Received response");
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            String str = Objects.requireNonNull(response.body()).string();
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String status = jsonObject.optString("Status");
                                if (!status.equals("OK")) {
                                    Log.e(TAG, "Status is not OK: " + status);
                                    new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Status is not OK: " + status));
                                    future.complete(false);
                                }
                                else{
                                    future.complete(true);
                                }
                            } catch (JSONException e) {
                                new Handler(Looper.getMainLooper()).post(() -> ToastEasy(e.toString()));
                                future.complete(false);
                            }
                        }
                        else{
                            new Handler(Looper.getMainLooper()).post(() -> ToastEasy("responseCode is: " + responseCode));
                            future.complete(false);
                        }
                        response.close();
                    }
                });
        return future;
    }

    public CompletableFuture<Boolean> deleteRatingSolution(List<String> deletedSolutionList) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(deletedSolutionList.size() == 0){
            future.complete(true);
            return future;
        }
        HttpUtilsRating.deleteRatingSolutionWithOkHttp(
                InfoCache.getAccount(), InfoCache.getToken(), deletedSolutionList, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Connect failed when adding rating solution!");
                        new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Connect failed when adding rating solution!"));
                        future.complete(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e(TAG, "Received response");
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            String str = Objects.requireNonNull(response.body()).string();
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String status = jsonObject.optString("Status");
                                if (!status.equals("OK")) {
                                    Log.e(TAG, "Status is not OK: " + status);
                                    new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Status is not OK: " + status));
                                    future.complete(false);
                                }
                                else{
                                    future.complete(true);
                                }
                            } catch (JSONException e) {
                                new Handler(Looper.getMainLooper()).post(() -> ToastEasy(e.toString()));
                                future.complete(false);
                            }
                        }
                        else{
                            new Handler(Looper.getMainLooper()).post(() -> ToastEasy("responseCode is: " + responseCode));
                            future.complete(false);
                        }
                        response.close();
                    }
                });
        return future;
    }

    public CompletableFuture<Boolean> updateRatingSolution(List<UpdateClassifySolution> updatedSolutionList) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if(updatedSolutionList.size() == 0){
            future.complete(true);
            return future;
        }
        HttpUtilsRating.updateRatingSolutionWithOkHttp(
                InfoCache.getAccount(), InfoCache.getToken(), updatedSolutionList, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Connect failed when updating rating solution!");
                        new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Connect failed when updating rating solution!"));
                        future.complete(false);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e(TAG, "Received response");
                        int responseCode = response.code();
                        if (responseCode == 200) {
                            String str = Objects.requireNonNull(response.body()).string();
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                String status = jsonObject.optString("Status");
                                if (!status.equals("OK")) {
                                    Log.e(TAG, "Status is not OK: " + status);
                                    new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Status is not OK: " + status));
                                    future.complete(false);
                                }
                                else{
                                    future.complete(true);
                                }
                            } catch (JSONException e) {
                                new Handler(Looper.getMainLooper()).post(() -> ToastEasy(e.toString()));
                                future.complete(false);
                            }
                        }
                        else{
                            new Handler(Looper.getMainLooper()).post(() -> ToastEasy("responseCode is: " + responseCode));
                            future.complete(false);
                        }
                        response.close();
                    }
                });
        return future;
    }

    public void requestRatingUserNameList(String solutionName) {
        HttpUtilsRating.queryRatingUserNameListWithOkHttp(InfoCache.getAccount(), InfoCache.getToken(), solutionName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Connect failed when querying rating username!");
                new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Connect failed when querying rating username!"));
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.e(TAG, "Received response");
                int responseCode = response.code();
                if (responseCode == 200) {
                    String str = null;
                    try {
                        str = Objects.requireNonNull(response.body()).string();
                    } catch (IOException e) {
                        new Handler(Looper.getMainLooper()).post(() -> ToastEasy(e.toString()));
                        mClassifyUserNameList.postValue(null);
                    }
                    if (str != null) {
                        Log.e("Get rating username", str);
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            String status = jsonObject.optString("Status");
                            if (status.equals("OK")) {
                                if (!jsonObject.isNull("UserNameResult")) {
                                    JSONArray userNameResults = jsonObject.getJSONArray("UserNameResult");
                                    ArrayList<String> usernames = new ArrayList<>();
                                    for (int i = 0; i < userNameResults.length(); i++) {
                                        String username = userNameResults.getString(i);
                                        usernames.add(username);
                                    }
                                    mClassifyUserNameList.postValue(usernames);
                                } else {
                                    new Handler(Looper.getMainLooper()).post(() -> ToastEasy("UserNameResult is null"));
                                    mClassifyUserNameList.postValue(null);
                                }
                            } else {
                                Log.e(TAG, "Status is not OK: " + status);
                                new Handler(Looper.getMainLooper()).post(() -> ToastEasy("Status is not OK: " + status));
                                mClassifyUserNameList.postValue(null);
                            }
                        } catch (JSONException e) {
                            new Handler(Looper.getMainLooper()).post(() -> ToastEasy(e.toString()));
                            mClassifyUserNameList.postValue(null);
                        }
                    }
                }
                else{
                    new Handler(Looper.getMainLooper()).post(() -> ToastEasy("responseCode is: " + responseCode));
                }
                response.close();
            }
        });
    }

    public boolean isLoggedIn() {
        return mUserInfoRepository.isLoggedIn();
    }

    public ImageInfoRepository getImageInfoRepository() {
        return mImageInfoRepository;
    }
}
