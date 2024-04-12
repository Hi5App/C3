package com.penglab.hi5.core.ui.ImageClassify;

import static com.penglab.hi5.chat.nim.main.helper.MessageHelper.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.utils.FileHelper;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.util.concurrent.Future;

class RatingImageInfo {
    public String ImageName;
    public Instant Instant;
    public boolean IsDownloadCompleted;
    public String LocalImageFile;
    public boolean IsRatingCompleted;
}

public class ImageClassifyViewModel extends ViewModel {
    private final String TAG = "ImageClassifyViewModel";
    private final UserInfoRepository mUserInfoRepository;
    private final ImageInfoRepository mImageInfoRepository;
    private final ImageClassifyDataSource mImageClassifyDataSource;

    private Deque<RatingImageInfo> mPreviousRatingImagesInfoDeque = new ArrayDeque<>();
    private final MutableLiveData<RatingImageInfo> mCurrentRatingImageInfo = new MutableLiveData<>();
    private Deque<RatingImageInfo> mNextRatingImagesInfoDeque = new ArrayDeque<>();

    public ImageClassifyViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, ImageClassifyDataSource imageClassifyDataSource) {
        this.mUserInfoRepository = userInfoRepository;
        this.mImageInfoRepository = imageInfoRepository;
        this.mImageClassifyDataSource = imageClassifyDataSource;
    }

    private final Object mLock = new Object();

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
                                            ratingImageInfo.IsRatingCompleted = false;
                                            synchronized (mLock) {
                                                mNextRatingImagesInfoDeque.addLast(ratingImageInfo);
                                            }
                                            downloadImageFileAsync(ratingImageInfo);
                                        }
                                    } else {
                                        Log.e("TAG", "imageInfoList is null");
                                    }

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                response.body().close();
                            }
                        }
                        response.close();
                    }
                });
    }

    public boolean isImageFileExist(RatingImageInfo imageInfo) {
        if (imageInfo == null || imageInfo.IsDownloadCompleted) {
            return false;
        }

        String imageFilePath = InfoCache.getContext().getExternalFilesDir(null) + "/Image/" + imageInfo.ImageName;
        File file = new File(imageFilePath);
        return file.exists();
    }

    public RatingImageInfo acquirePreviousImage() {
        synchronized (mLock) {
            if (mPreviousRatingImagesInfoDeque.isEmpty()) {
                return null;
            }

            if (mCurrentRatingImageInfo.getValue() != null) {
                mNextRatingImagesInfoDeque.addFirst(mCurrentRatingImageInfo.getValue());
            }

            mCurrentRatingImageInfo.setValue(mPreviousRatingImagesInfoDeque.pollLast());
        }
        return mCurrentRatingImageInfo.getValue();
    }

    public void acquireImagesManually() {
        synchronized (mLock) {
            if (mNextRatingImagesInfoDeque.size() > 20) {
                return;
            }
            RequestGetImages(10);
        }
    }

    public MutableLiveData<RatingImageInfo> acquireCurrentImage() {
        return mCurrentRatingImageInfo;
    }

    public RatingImageInfo acquireNextImage() {
        synchronized (mLock) {
            if (mNextRatingImagesInfoDeque.isEmpty()) {
                return null;
            }

            if (mNextRatingImagesInfoDeque.size() <= 5) {
                RequestGetImages(5);
            }

            if (mCurrentRatingImageInfo.getValue() != null) {
                mPreviousRatingImagesInfoDeque.addLast(mCurrentRatingImageInfo.getValue());
                if (mPreviousRatingImagesInfoDeque.size() > 10) {
                    mPreviousRatingImagesInfoDeque.pollFirst();
                }
            }

            mCurrentRatingImageInfo.setValue(mNextRatingImagesInfoDeque.pollFirst());
        }
        return mCurrentRatingImageInfo.getValue();
    }

    public void downloadImageFileAsync(RatingImageInfo ratingImageInfo) {
        synchronized (mLock) {
            if (ratingImageInfo.IsDownloadCompleted) {
                return;
            }
        }
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Connect failed when download rating image !");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int responseCode = response.code();
                if (responseCode == 200) {
                    if (response.body() != null) {
                        byte[] fileContent = response.body().bytes();
                        Log.e(TAG, "image file name: " + ratingImageInfo.ImageName);
                        Log.e(TAG, "image file size: " + fileContent.length);
                        String storePath = InfoCache.getContext().getExternalFilesDir(null) + "/Image";
                        String filename = ratingImageInfo.ImageName;

                        if (ratingImageInfo.IsDownloadCompleted && ratingImageInfo.LocalImageFile != null && !ratingImageInfo.LocalImageFile.isEmpty()) {
                            Log.e(TAG, "Image file has been downloaded before ! Image: " + ratingImageInfo.LocalImageFile);
                            return;
                        }

                        if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                            Log.e(TAG, "Fail to store image file !");
                        } else {
                            Log.e(TAG, "Store image file successfully ! storepath: " + storePath + "/" + filename);

                        }
                        synchronized (mLock) {
                            ratingImageInfo.LocalImageFile = storePath + "/" + filename;
                            ratingImageInfo.IsDownloadCompleted = true;
                        }
                        response.body().close();
                        response.close();
                    } else {
                        Log.e(TAG, "Response from server is null when download image !");
                    }
                } else {
                    Log.e(TAG, "Response from server is error when download image !");
                }
            }
        };

        HttpUtilsRating.downloadSingleRattingImageWithOkHttpAsync(ratingImageInfo.ImageName, callback);
    }

    public void downloadImageFileSync(RatingImageInfo ratingImageInfo) {
        synchronized (mLock) {
            if (ratingImageInfo.IsDownloadCompleted) {
                return;
            }
        }
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Connect failed when download rating image !");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int responseCode = response.code();
                if (responseCode == 200) {
                    if (response.body() != null) {
                        byte[] fileContent = response.body().bytes();
                        Log.e(TAG, "image file name: " + ratingImageInfo.ImageName);
                        Log.e(TAG, "image file size: " + fileContent.length);
                        String storePath = InfoCache.getContext().getExternalFilesDir(null) + "/Image";
                        String filename = ratingImageInfo.ImageName;

                        if (ratingImageInfo.IsDownloadCompleted && ratingImageInfo.LocalImageFile != null && !ratingImageInfo.LocalImageFile.isEmpty()) {
                            Log.e(TAG, "Image file has been downloaded before ! Image: " + ratingImageInfo.LocalImageFile);
                            return;
                        }

                        if (!FileHelper.storeFile(storePath, filename, fileContent)) {
                            Log.e(TAG, "Fail to store image file !");
                        } else {
                            Log.e(TAG, "Store image file successfully ! storepath: " + storePath + "/" + filename);

                        }
                        synchronized (mLock) {
                            ratingImageInfo.LocalImageFile = storePath + "/" + filename;
                            ratingImageInfo.IsDownloadCompleted = true;
                        }
                        response.body().close();
                        response.close();

                        acquireNextImage();
                    } else {
                        Log.e(TAG, "Response from server is null when download image !");
                    }
                } else {
                    Log.e(TAG, "Response from server is error when download image !");
                }
            }
        };

        HttpUtilsRating.downloadSingleRattingImageWithOkHttpAsync(ratingImageInfo.ImageName, callback);

//        Future<Response> future = HttpUtilsRating.downloadSingleRattingImageWithOkHttpSync(ratingImageInfo.ImageName);
//        try {
//            Response response = future.get(); // This will block until the result is available
//            int responseCode = response.code();
//            if (responseCode == 200) {
//                if (response.body() != null) {
//                    byte[] fileContent = response.body().bytes();
//                    Log.e(TAG, "image file name: " + ratingImageInfo.ImageName);
//                    Log.e(TAG, "image file size: " + fileContent.length);
//                    String storePath = InfoCache.getContext().getExternalFilesDir(null) + "/Image";
//                    String filename = ratingImageInfo.ImageName;
//
//                    if (ratingImageInfo.IsDownloadCompleted && ratingImageInfo.LocalImageFile != null && !ratingImageInfo.LocalImageFile.isEmpty()) {
//                        Log.e(TAG, "Image file has been downloaded before ! Image: " + ratingImageInfo.LocalImageFile);
//                        return false;
//                    }
//
//                    if (!FileHelper.storeFile(storePath, filename, fileContent)) {
//                        Log.e(TAG, "Fail to store image file !");
//                    } else {
//                        Log.e(TAG, "Store image file successfully ! storepath: " + storePath + "/" + filename);
//
//                    }
//                    synchronized (mLock) {
//                        ratingImageInfo.LocalImageFile = storePath + "/" + filename;
//                        ratingImageInfo.IsDownloadCompleted = true;
//                    }
//                    response.body().close();
//                    response.close();
//
//                    return true;
//                } else {
//                    Log.e(TAG, "Response from server is null when download image !");
//                }
//            } else {
//                Log.e(TAG, "Response from server is error when download image !");
//            }
//
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return false;
    }

    public void uploadUserRatingResult(String ratingEnum, String additionalRatingDescription) {
        if (mCurrentRatingImageInfo.getValue() == null) {
            return;
        }

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
                                String status = jsonResponse.optString("Status", "");
                                if (status.equals("OK")) {
//                                    uploadUserRatingResult.postValue(new Result.Success<String>(UPLOAD_SUCCESSFULLY));
                                } else {
//                                    uploadUserRatingResult.postValue(new Result.Error(new Exception("Failed to uploadUserRatingResult: " + responseBody)));
                                }

                                if (mCurrentRatingImageInfo.getValue() != null) {
                                    mCurrentRatingImageInfo.getValue().IsRatingCompleted = true;
                                }

                                response.body().close();
                                response.close();
                            } else {
                                Log.e(TAG, "Response from server is null when upload user rating result !");
                            }
                        } else {
                            Log.e(TAG, "Response from server is error when upload user rating result !");
                        }
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
//public class ImageClassifyViewModel extends ViewModel {
//
//    private final String TAG = "ImageClassifyViewModel";

//    private final ImageClassifyDataSource imageClassifyDataSource;
//    private final List<ImageInfo> imageInfoList = new ArrayList<>();
//    private volatile ImageInfo lastDownloadImageInfo;
//    private volatile ImageInfo curImageInfo;
//
//
//    public boolean isLoggedIn(){
//        return userInfoRepository.isLoggedIn();
//    }
//    private int curIndex = -1;
//    private int lastIndex = -1;
//
//    private int curDownloadIndex = 0;
//    private boolean isDownloading = false;
//    private boolean noFileLeft = false;
//    public enum WorkStatus{
//        IMAGE_FILE_EXPIRED, START_TO_DOWNLOAD_IMAGE,  NO_MORE_FILE, DOWNLOAD_IMAGE_FINISH, NONE
//    }
//    private final MutableLiveData<ImageClassifyViewModel.WorkStatus> workStatus = new MutableLiveData<>();
//    private final MutableLiveData<ResourceResult> downloadedRatingImageResult = new MutableLiveData<>();
//    private final MutableLiveData<ResourceResult> uploadedUserResult = new MutableLiveData<>();
//    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
//    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
//
//    public ImageClassifyViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, ImageClassifyDataSource imageClassifyDataSource) {
//        initPreDownloadThread();
//    }
//
//    public ImageClassifyDataSource getImageClassifyDataSource() {
//        return imageClassifyDataSource;
//    }
//
//    public LiveData<ImageClassifyViewModel.WorkStatus> getWorkStatus() {
//        return workStatus;
//    }
//    public ImageInfo getCurImageInfo() {
//        return curImageInfo;
//    }
//
//    private void getRatingImageList() {
//        imageClassifyDataSource.getRatingImageListResponse();
//    }
//
//    public void downloadSingleRatingImage() {
//        String imageName = lastDownloadImageInfo.getImageName();
//        imageClassifyDataSource.getDownloadSingleRatingImageResponse(imageName);
//    }
//
//    public void uploadUserResult(String ratingType, String additionalInfo) {
//        String imageName = curImageInfo.getImageName();
//        imageClassifyDataSource.uploadUserRatingResultResponse(imageName,ratingType,additionalInfo);
//        if (!curImageInfo.isAlreadyUpload()) {
//            curImageInfo.setAlreadyUpload(true);
//        }
//    }
//
//
//    public LiveData<ResourceResult> monitorDownloadedImageResult() {
//        return downloadedRatingImageResult;
//    }
//
//    public MutableLiveData<ResourceResult> monitorUploadedUserResult() {
//        return uploadedUserResult;
//    }
//
//    public void handleRatingImageList(Result result) {
//        if (result instanceof Result.Success) {
//            Object data = ((Result.Success<?>) result).getData();
//            if (data instanceof JSONObject) {
//                JSONObject jsonObject = (JSONObject) data;
//                try {
//                    if (!jsonObject.isNull("ImageNameList")) {
//                        JSONArray imageNameList = jsonObject.getJSONArray("ImageNameList");
//                        for (int i = 0; i < imageNameList.length(); i++) {
//                            String imageName = imageNameList.getString(i);
//                            int id = i + 1;
//                            ImageInfo curDownloadImage = new ImageInfo(id, imageName);
//                            imageInfoList.add(curDownloadImage);
//                        }
//                        lastDownloadImageInfo = imageInfoList.get(curDownloadIndex);
//                        downloadSingleRatingImage();
//                    } else {
//                        Log.e("TAG","imageInfoList is null");
//                    }
//                } catch (JSONException | org.json.JSONException e) {
//                    e.printStackTrace();
//                }
//            } else if (data instanceof String && ((String) data).equals(NO_MORE_FILE)) {
//                workStatus.setValue(ImageClassifyViewModel.WorkStatus.NO_MORE_FILE);
//                noFileLeft = true;
//                isDownloading = false;
//            } else {
//                isDownloading = false;
//            }
//        } else {
//            ToastEasy(result.toString());
//            isDownloading = false;
//        }
//    }
//
//    public void handleUploadUserResult(Result result) {
//        if (result == null) {
//            return;
//        }
//        if (result instanceof Result.Success) {
//            Object data = ((Result.Success<?>) result).getData();
//            if (data instanceof String && ((String) data).equals(UPLOAD_SUCCESSFULLY)){
//                uploadedUserResult.setValue(new ResourceResult(true));
//            } else {
//                uploadedUserResult.setValue(new ResourceResult(false));
//            }
//        } else {
//            ToastEasy(result.toString());
//        }
//    }
//
//    public void handleDownloadRatingImage(Result result){
//        if (result == null) {
//            isDownloading = false;
//        }
//        if (result instanceof Result.Success) {
//            Object data = ((Result.Success<?>) result).getData();
//            if (data instanceof String){
//                Log.e(TAG,"Download rating image data" + data);
//                if(curDownloadIndex < imageInfoList.size()-1) {
//                    if (workStatus.getValue() == ImageClassifyViewModel.WorkStatus.START_TO_DOWNLOAD_IMAGE) {
//                        workStatus.setValue(ImageClassifyViewModel.WorkStatus.DOWNLOAD_IMAGE_FINISH);
//                    }
//                    lastDownloadImageInfo = imageInfoList.get(++curDownloadIndex);
//                    downloadSingleRatingImage();
//                } else if(curDownloadIndex == imageInfoList.size()-1) {
//                    curDownloadIndex = 0;
//                    isDownloading = false;
//                }
//            } else {
//                Log.e(TAG,"Fail to parse download rating image result !");
//                isDownloading = false;
//            }
//        } else {
//            Log.e(TAG,"Download rating image result is error !");
//            isDownloading = false;
//        }
//    }
//
//    public void openNewFile() {
//        noFileLeft = false;
//        if (lastIndex + 4 >= imageInfoList.size() && !isDownloading) {
//            initPreDownloadThread();
//        }
//        if (lastIndex + 2 >= imageInfoList.size()) {
//            workStatus.setValue(ImageClassifyViewModel.WorkStatus.START_TO_DOWNLOAD_IMAGE);
//        } else {
//            curIndex = ++lastIndex;
//            openFileWithCurIndex();
//        }
//    }
//
//    private void openFileWithCurIndex() {
//        curImageInfo = imageInfoList.get(curIndex);
//        curImageInfo.setCreatedTime(System.currentTimeMillis());
//        String filePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image"+ "/" + curImageInfo.getImageName() ;
//        String fileName = FileManager.getFileName(filePath);
//        FileType fileType = FileManager.getFileType(filePath);
//        imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String >(filePath), fileType);
//        downloadedRatingImageResult.setValue(new ResourceResult(true));
//    }
//
//    public void previousFile() {
//        int tempCurIndex = curIndex;
//        if (tempCurIndex == 0) {
//            ToastEasy("You have reached the earliest image !");
//        } else if (tempCurIndex <= imageInfoList.size() - 1 && tempCurIndex > 0) {
//            curIndex = tempCurIndex-1;
//            openFileWithCurIndex();
//        } else {
//            ToastEasy("Something wrong with curIndex");
//        }
//    }
//
//    public void nextFile() {
//        if (curIndex + 1 > lastIndex) {
//            openNewFile();
//        } else {
//            curIndex++;
//            openFileWithCurIndex();
//        }
//
//    }
//    private void initPreDownloadThread() {
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                getRatingImageList();
//            }
//        });
//    }
//
//    public void shutDownThreadPool() {
//        executorService.shutdown();
//        scheduledExecutorService.shutdown();
//    }
//}
