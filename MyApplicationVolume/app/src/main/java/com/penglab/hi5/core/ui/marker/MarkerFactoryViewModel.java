package com.penglab.hi5.core.ui.marker;


import static android.content.Context.DOWNLOAD_SERVICE;
import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.data.ImageDataSource.DOWNLOAD_IMAGE_FAILED;
import static com.penglab.hi5.data.MarkerFactoryDataSource.NO_MORE_FILE;
import static com.penglab.hi5.data.MarkerFactoryDataSource.UPLOAD_SUCCESSFULLY;

import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.core.game.Score;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.MarkerFactoryDataSource;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.data.model.img.PotentialSomaInfo;
import com.penglab.hi5.data.model.user.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jackiexing on 01/10/21
 */
public class MarkerFactoryViewModel extends ViewModel {

    private final String TAG = "MarkerFactoryViewModel";
    private final int DEFAULT_IMAGE_SIZE = 128;
    private final int DEFAULT_RES_INDEX = 2;

    public enum AnnotationMode{
       BIG_DATA, NONE
    }

    public enum WorkStatus{
        START_TO_DOWNLOAD_IMAGE, GET_SOMA_LIST_SUCCESSFULLY, UPLOAD_MARKERS_SUCCESSFULLY, NO_MORE_FILE, NONE
    }

    private final MutableLiveData<AnnotationMode> annotationMode = new MutableLiveData<>();
    private final MutableLiveData<WorkStatus> workStatus = new MutableLiveData<>();
    private final MutableLiveData<MarkerList> syncMarkerList = new MutableLiveData<>();
    private final MutableLiveData<ResourceResult> imageResult = new MutableLiveData<>();

    private final UserInfoRepository userInfoRepository;
    private final ImageInfoRepository imageInfoRepository;
    private final MarkerFactoryDataSource markerFactoryDataSource;
    private final ImageDataSource imageDataSource;

    private final LoggedInUser loggedInUser;
    private final CoordinateConvert coordinateConvert = new CoordinateConvert();
    private final HashMap<String, String> resMap = new HashMap<>();
    private final List<PotentialSomaInfo> potentialSomaInfoList = new ArrayList<>();
    private PotentialSomaInfo curPotentialSomaInfo;
    private PotentialSomaInfo lastDownloadPotentialSomaInfo;
    private int curIndex = -1;
    private boolean isDownloading = false;

    public MarkerFactoryViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, MarkerFactoryDataSource markerFactoryDataSource, ImageDataSource imageDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.imageInfoRepository = imageInfoRepository;
        this.markerFactoryDataSource = markerFactoryDataSource;
        this.imageDataSource = imageDataSource;
        this.loggedInUser = userInfoRepository.getUser();
        coordinateConvert.setResIndex(DEFAULT_RES_INDEX);
        coordinateConvert.setImgSize(DEFAULT_IMAGE_SIZE);

        initPreDownloadThread();
    }

    public LiveData<AnnotationMode> getAnnotationMode(){
        return annotationMode;
    }

    public LiveData<WorkStatus> getWorkStatus() {
        return workStatus;
    }

    public LiveData<MarkerList> getSyncMarkerList() {
        return syncMarkerList;
    }

    public LiveData<ResourceResult> getImageResult() {
        return imageResult;
    }

    public ImageDataSource getImageDataSource() {
        return imageDataSource;
    }

    public MarkerFactoryDataSource getMarkerFactoryDataSource() {
        return markerFactoryDataSource;
    }

    public boolean isLoggedIn(){
        return userInfoRepository.isLoggedIn();
    }

    public MutableLiveData<Integer> getObservableScore() {
        return userInfoRepository.getScoreModel().getObservableScore();
    }

    public PotentialSomaInfo getCurPotentialSomaInfo() {
        return curPotentialSomaInfo;
    }

    public void updateImageResult(Result result) {
        if (result instanceof Result.Success){
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONArray){
                // process Brain List, store res info for each brain
                JSONArray jsonArray = (JSONArray) data;
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String imageId = jsonObject.getString("name");
                        String detail = jsonObject.getString("detail");

                        // parse brain info
                        detail = detail.substring(1, detail.length() - 1);
                        String [] rois = detail.split(", ");
                        for (int j = 0; j < rois.length; j++) {
                            rois[j] = rois[j].substring(1, rois[j].length() - 1);
                        }
                        if (rois.length >= 2){
                            resMap.put(imageId, rois[1]);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                downloadImage();
            } else if (data instanceof String){
                potentialSomaInfoList.add(lastDownloadPotentialSomaInfo);
                isDownloading = false;
                // process image file after download
//                String fileName = FileManager.getFileName((String) data);
//                FileType fileType = FileManager.getFileType((String) data);
//                imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String >((String) data), fileType);
//                imageResult.setValue(new ResourceResult(true));
            }
        } else if (result instanceof Result.Error){
            // Fail to download image
            if (result.toString().equals("Error: " + DOWNLOAD_IMAGE_FAILED)) {
                if (curIndex == potentialSomaInfoList.size()-1) {
                    curIndex--;
                }
                potentialSomaInfoList.remove(potentialSomaInfoList.size()-1);
            }
            isDownloading = false;
            ToastEasy(result.toString());
        }
    }

    public void updateSomaResult(Result result) {
        if (result instanceof Result.Success){
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof PotentialSomaInfo){
                lastDownloadPotentialSomaInfo = (PotentialSomaInfo) data;
//                potentialSomaInfoList.add(curPotentialSomaInfo);
//                coordinateConvert.initLocation(curPotentialSomaInfo.getLocation());
//                curIndex = potentialSomaInfoList.size()-1;

                // get res list when first download img
                if (resMap.isEmpty()) {
                    getBrainList();
                } else {
                    downloadImage();
                }
            } else if (data instanceof MarkerList) {
                // get soma list successfully
                syncMarkerList.setValue(MarkerList.covertGlobalToLocal((MarkerList) data, coordinateConvert));
                annotationMode.setValue(AnnotationMode.BIG_DATA);
                workStatus.setValue(WorkStatus.GET_SOMA_LIST_SUCCESSFULLY);
            } else if (data instanceof String){
                String response = (String) data;
                if (response.equals(UPLOAD_SUCCESSFULLY)){
                    workStatus.setValue(WorkStatus.UPLOAD_MARKERS_SUCCESSFULLY);
                } else if (response.equals(NO_MORE_FILE)){
                    workStatus.setValue(WorkStatus.NO_MORE_FILE);
                }
            }
        } else if (result instanceof Result.Error){
            isDownloading = false;
            ToastEasy(result.toString());
        }
    }

    public void openNewFile() {
        getPotentialLocation();
    }

    public void removeCurFileFromList() {
        if (curIndex >= 0 && curIndex < potentialSomaInfoList.size()) {
            potentialSomaInfoList.get(curIndex).setBoring(true);
        } else {
            ToastEasy("Something wrong with curIndex");
        }
    }

    public void previousFile() {
        while (curIndex > 0) {
            if (!potentialSomaInfoList.get(curIndex - 1).isBoring()) {
                break;
            }
            curIndex--;
        }
        if (curIndex == 0) {
            ToastEasy("You have reached the earliest image !");
        } else if (curIndex <= potentialSomaInfoList.size() - 1 && curIndex > 0) {
            curIndex--;
            curPotentialSomaInfo = potentialSomaInfoList.get(curIndex);
            coordinateConvert.initLocation(curPotentialSomaInfo.getLocation());
            downloadImage();
        } else {
            ToastEasy("Something wrong with curIndex");
        }
    }

    public void nextFile() {
        while (curIndex < potentialSomaInfoList.size() - 1) {
            if (!potentialSomaInfoList.get(curIndex + 1).isBoring()) {
                break;
            }
            curIndex++;
        }
        if (curIndex == potentialSomaInfoList.size()-1) {
            // open new file
            openNewFile();
        } else if (curIndex < potentialSomaInfoList.size()-1 && curIndex >= 0) {
            curIndex++;
            curPotentialSomaInfo = potentialSomaInfoList.get(curIndex);
            coordinateConvert.initLocation(curPotentialSomaInfo.getLocation());
            downloadImage();
        } else {
            ToastEasy("Something wrong with curIndex");
        }
    }

    private void getBrainList() {
        imageDataSource.getBrainList();
    }

    private void getPotentialLocation() {
        markerFactoryDataSource.getPotentialLocation();
    }

    public void downloadImage() {
        String brainId = lastDownloadPotentialSomaInfo.getBrainId();
        XYZ loc = lastDownloadPotentialSomaInfo.getLocation();
        String res = resMap.get(brainId);
        if (res == null){
            ToastEasy("Fail to download image, something wrong with res list !");
            return;
        }
        // show progressBar
        workStatus.setValue(WorkStatus.START_TO_DOWNLOAD_IMAGE);
        imageDataSource.downloadImage(lastDownloadPotentialSomaInfo.getBrainId(), res, (int) loc.x , (int) loc.y, (int) loc.z, DEFAULT_IMAGE_SIZE);
    }

    public void getSomaList() {
        String brainId = curPotentialSomaInfo.getBrainId();
        XYZ loc = curPotentialSomaInfo.getLocation();
        markerFactoryDataSource.getSomaList(brainId, (int) loc.x, (int) loc.y, (int) loc.z, DEFAULT_IMAGE_SIZE * (int) Math.pow(2, coordinateConvert.getResIndex()-1));
    }

    public void updateSomaList(MarkerList markerListToAdd, JSONArray markerListToDelete) {
        if ((markerListToAdd == null) && (markerListToDelete == null)){
            return;
        }
        try {
            int locationId = curPotentialSomaInfo.getId();
            String brainId = curPotentialSomaInfo.getBrainId();
            String username = loggedInUser.getUserId();
            markerFactoryDataSource.updateSomaList(brainId, locationId, username,
                    MarkerList.toJSONArray(MarkerList.covertLocalToGlobal(markerListToAdd, coordinateConvert)), markerListToDelete);
            if (curIndex == potentialSomaInfoList.size() - 1) {
                winScoreByFinishConfirmAnImage();
            }
        } catch (JSONException e) {
            ToastEasy("Fail to convert MarkerList ot JSONArray !");
            e.printStackTrace();
        }
    }
    
    public void winScoreByFinishConfirmAnImage() {
        userInfoRepository.getScoreModel().finishAnImage();
    }

    public void winScoreByPinPoint() {
        userInfoRepository.getScoreModel().pinpoint();
    }

    private void initPreDownloadThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    if (curIndex > potentialSomaInfoList.size() - 5 && !isDownloading) {
                        getPotentialLocation();
                        isDownloading = true;
                    }
                }
            }
        };
        thread.start();
    }
}
