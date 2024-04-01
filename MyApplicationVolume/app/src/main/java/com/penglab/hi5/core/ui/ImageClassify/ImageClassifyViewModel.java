package com.penglab.hi5.core.ui.ImageClassify;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.data.MarkerFactoryDataSource.NO_MORE_FILE;
import static com.penglab.hi5.data.MarkerFactoryDataSource.UPLOAD_SUCCESSFULLY;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson.JSONException;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.ui.QualityInspection.QualityInspectionViewModel;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.data.ImageClassifyDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.data.model.img.ImageInfo;
import com.penglab.hi5.data.model.img.PotentialArborMarkerInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ImageClassifyViewModel extends ViewModel {

    private final String TAG = "ImageClassifyViewModel";
    private final UserInfoRepository userInfoRepository;
    private final ImageInfoRepository imageInfoRepository;
    private final ImageClassifyDataSource imageClassifyDataSource;
    private final List<ImageInfo> imageInfoList = new ArrayList<>();
    private volatile ImageInfo lastDownloadImageInfo;
    private volatile ImageInfo curImageInfo;


    public boolean isLoggedIn(){
        return userInfoRepository.isLoggedIn();
    }
    private int curIndex = -1;
    private int lastIndex = -1;

    private int curDownloadIndex = 0;
    private boolean isDownloading = false;
    private boolean noFileLeft = false;

    public enum AnnotationMode{
        BIG_DATA, NONE
    }

    public enum WorkStatus{
        IMAGE_FILE_EXPIRED, START_TO_DOWNLOAD_IMAGE,  NO_MORE_FILE, DOWNLOAD_IMAGE_FINISH, NONE
    }

    private final MutableLiveData<ImageClassifyViewModel.WorkStatus> workStatus = new MutableLiveData<>();

    private final MutableLiveData<ResourceResult> ratingImageResult = new MutableLiveData<>();

    private final MutableLiveData<ResourceResult> uploadResult = new MutableLiveData<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ImageClassifyViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, ImageClassifyDataSource imageClassifyDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.imageInfoRepository = imageInfoRepository;
        this.imageClassifyDataSource = imageClassifyDataSource;
        initPreDownloadThread();
        initCheckFreshThread();
    }

    public ImageClassifyDataSource getImageClassifyDataSource() {
        return imageClassifyDataSource;
    }

    public LiveData<ImageClassifyViewModel.WorkStatus> getWorkStatus() {
        return workStatus;
    }

    private void getRatingImageList() {
        imageClassifyDataSource.getRatingImageList();
    }

    public void downloadRatingImage() {
        String imageName = lastDownloadImageInfo.getImageName();
        Log.e(TAG,"rating_image"+imageName);
        imageClassifyDataSource.getDownloadRatingImage(imageName);
    }

    public ImageInfo getCurImageInfo() {
        return curImageInfo;
    }

    public LiveData<ResourceResult> getDownloadRatingImageResult() {
        return ratingImageResult;
    }

    public MutableLiveData<ResourceResult> getUploadResult() {
        return uploadResult;
    }

    public void handleRatingImageList(Result result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) data;
                try {
                    if (!jsonObject.isNull("ImageNameList")) {
                        JSONArray imageNameList = jsonObject.getJSONArray("ImageNameList");

                        for (int i = 0; i < imageNameList.length(); i++) {
                            String imageName = imageNameList.getString(i);
                            int id = i + 1;
                            ImageInfo curDownloadImage = new ImageInfo(id, imageName);
                            curDownloadImage.setCreatedTime(System.currentTimeMillis());
                            imageInfoList.add(curDownloadImage);
                        }
                        lastDownloadImageInfo = imageInfoList.get(curDownloadIndex);
                        downloadRatingImage();
                    } else {
                        Log.e("TAG","imageInfoList is null");
                    }
                } catch (JSONException | org.json.JSONException e) {
                    e.printStackTrace();
                }

            } else if (data instanceof String && ((String) data).equals(NO_MORE_FILE)) {
                workStatus.setValue(ImageClassifyViewModel.WorkStatus.NO_MORE_FILE);
                noFileLeft = true;
                isDownloading = false;
            } else {
                isDownloading = false;
            }
        } else {
            ToastEasy(result.toString());
            isDownloading = false;
        }
    }

    public void handleUpdateRatingImageResult(Result result) {
        if (result == null) {
            return;
        }
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof String && ((String) data).equals(UPLOAD_SUCCESSFULLY)){
                uploadResult.setValue(new ResourceResult(true));
            } else {
                uploadResult.setValue(new ResourceResult(false));
            }
        } else {
            ToastEasy(result.toString());
        }
    }

    public void handleDownloadRatingImage(Result result){
        if (result == null) {
            isDownloading = false;
        }
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof String){
                Log.e(TAG,"Download rating image data" + data);
                if(curDownloadIndex< imageInfoList.size()-1){
                    if (workStatus.getValue() == ImageClassifyViewModel.WorkStatus.START_TO_DOWNLOAD_IMAGE) {
                        workStatus.setValue(ImageClassifyViewModel.WorkStatus.DOWNLOAD_IMAGE_FINISH);
                    }
                    lastDownloadImageInfo = imageInfoList.get(++curDownloadIndex);
                    downloadRatingImage();
                }else if(curDownloadIndex == imageInfoList.size()-1){
                    curDownloadIndex = 0;
                    isDownloading = false;
                }
            } else {
                Log.e(TAG,"Fail to parse download ratingimage result !");
                isDownloading = false;
            }
        } else {
            Log.e(TAG,"Download rating image result is error !");
            isDownloading = false;
        }

    }


    public void uploadRatingResult(String ratingType,String addtionalInfo) {
        if (!curImageInfo.ifStillFresh() && !curImageInfo.isAlreadyUpload()) {
            uploadResult.setValue(new ResourceResult(false, "Expired"));
            return;
        }
        String imageName = curImageInfo.getImageName();
        imageClassifyDataSource.updateImageResult(imageName,ratingType,addtionalInfo);
        if (!curImageInfo.isAlreadyUpload()) {
            curImageInfo.setAlreadyUpload(true);
        }

    }

    public void openNewFile() {
        noFileLeft = false;
        while (lastIndex < imageInfoList.size() - 1) {
            ImageInfo imageInfo = imageInfoList.get(lastIndex + 1);
            if (imageInfo.ifStillFresh()) {
                break;
            }
            lastIndex++;
        }
        if (lastIndex + 1 >= imageInfoList.size()) {
            workStatus.setValue(ImageClassifyViewModel.WorkStatus.START_TO_DOWNLOAD_IMAGE);
        } else {
            curIndex = ++lastIndex;
            openFileWithCurIndex();
        }
    }

    private void openFileWithCurIndex() {
        curImageInfo = imageInfoList.get(curIndex);
        String filePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image"+ "/" + curImageInfo.getImageName() ;
        String fileName =FileManager.getFileName(filePath);
        FileType fileType = FileManager.getFileType(filePath);
        imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String >(filePath), fileType);
        ratingImageResult.setValue(new ResourceResult(true));
    }

    public void previousFile() {
        int tempCurIndex = curIndex;
        while (tempCurIndex > 0) {
            if ((imageInfoList.get(tempCurIndex - 1).ifStillFresh()
                    || (imageInfoList.get(tempCurIndex - 1).isAlreadyUpload()))) {
                break;
            }
            tempCurIndex--;
        }
        if (tempCurIndex == 0) {
            ToastEasy("You have reached the earliest image !");
        } else if (tempCurIndex <= imageInfoList.size() - 1 && tempCurIndex > 0) {
            curIndex = tempCurIndex-1;
            openFileWithCurIndex();
        } else {
            ToastEasy("Something wrong with curIndex");
        }
    }

    public void nextFile() {
        int tempCurIndex = curIndex;
        while (tempCurIndex < imageInfoList.size() - 1) {
            if ((imageInfoList.get(tempCurIndex + 1).ifStillFresh()
                    || (imageInfoList.get(tempCurIndex + 1).isAlreadyUpload()))) {
                break;
            }
            tempCurIndex++;
        }
        if (tempCurIndex == imageInfoList.size()-1) {
            // open new file
            openNewFile();
        } else if (tempCurIndex < imageInfoList.size()-1 && tempCurIndex >= 0) {
            curIndex = tempCurIndex+1;
            if (curIndex > lastIndex) {
                lastIndex = curIndex;
            }
            openFileWithCurIndex();
        } else {
            ToastEasy("Something wrong with curIndex");
        }

    }
    private void initPreDownloadThread() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (lastIndex > imageInfoList.size() - 7 && !isDownloading && !noFileLeft) {
                        getRatingImageList();
                        isDownloading = true;
                    }
                }
            }
        });
    }

    private void initCheckFreshThread() {
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (curImageInfo != null &&
                        !curImageInfo.isAlreadyUpload() && !curImageInfo.ifStillFresh()) {
                    if (workStatus.getValue() != ImageClassifyViewModel.WorkStatus.IMAGE_FILE_EXPIRED) {
                        workStatus.postValue(ImageClassifyViewModel.WorkStatus.IMAGE_FILE_EXPIRED);
                    }
                }
            }
        }, 30,30, TimeUnit.SECONDS);
    }

    public void shutDownThreadPool() {
        executorService.shutdown();
        scheduledExecutorService.shutdown();
    }
}
