package com.penglab.hi5.core.ui.marker;


import static com.penglab.hi5.core.Myapplication.ToastEasy;
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
       BIG_DATA, NO_MORE_FILE, NONE
    }


    public enum WorkStatus{
        GET_POTENTIAL_LOCATION, NONE
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
    private int curIndex = -1;

    public MarkerFactoryViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, MarkerFactoryDataSource markerFactoryDataSource, ImageDataSource imageDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.imageInfoRepository = imageInfoRepository;
        this.markerFactoryDataSource = markerFactoryDataSource;
        this.imageDataSource = imageDataSource;
        this.loggedInUser = userInfoRepository.getUser();
        coordinateConvert.setResIndex(DEFAULT_RES_INDEX);
        coordinateConvert.setImgSize(DEFAULT_IMAGE_SIZE);

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
                // process image file after download
                String fileName = FileManager.getFileName((String) data);
                FileType fileType = FileManager.getFileType((String) data);
                imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String >((String) data), fileType);
                imageResult.setValue(new ResourceResult(true));
            }
        } else if (result instanceof Result.Error){
            ToastEasy(result.toString());
        }
    }

    public void updateSomaResult(Result result) {
        if (result instanceof Result.Success){
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof PotentialSomaInfo){
                curPotentialSomaInfo = (PotentialSomaInfo) data;
                potentialSomaInfoList.add(curPotentialSomaInfo);
                coordinateConvert.initLocation(curPotentialSomaInfo.getLocation());

                // get res list when first download img
                if (resMap.isEmpty()) {
                    getBrainList();
                } else {
                    downloadImage();
                }
                // TODO: open image
            } else if (data instanceof MarkerList) {
                // TODO: import somaList
                syncMarkerList.setValue(MarkerList.covertGlobalToLocal((MarkerList) data, coordinateConvert));
                annotationMode.setValue(AnnotationMode.BIG_DATA);
            } else if (data instanceof String){
                String response = (String) data;
                if (response.equals(UPLOAD_SUCCESSFULLY)){
                    ToastEasy("Upload markers successfully");
                } else if (response.equals(NO_MORE_FILE)){
                    annotationMode.setValue(AnnotationMode.NO_MORE_FILE);
                    ToastEasy("No more file need to process !");
                }
            }
        } else if (result instanceof Result.Error){
            ToastEasy(result.toString());
        }
    }

    public void openNewFile() {
        Log.e(TAG,"openNewFile");
        curIndex = potentialSomaInfoList.size();
        getPotentialLocation();
    }

    public void previousFile() {
        Log.e(TAG,"previousFile");
        if (curIndex == 0) {
            ToastEasy("You have reached the earliest image !");
        } else if (curIndex <= potentialSomaInfoList.size()-1 && curIndex > 0) {
            curIndex--;
            curPotentialSomaInfo = potentialSomaInfoList.get(curIndex);
            coordinateConvert.initLocation(curPotentialSomaInfo.getLocation());
            downloadImage();
        } else {
            // TODO: something wrong with curIndex
        }
    }

    public void nextFile() {
        if (curIndex == potentialSomaInfoList.size()-1) {
            // open new file
            openNewFile();
        } else if (curIndex < potentialSomaInfoList.size()-1 && curIndex >= 0) {
            curIndex++;
            curPotentialSomaInfo = potentialSomaInfoList.get(curIndex);
            coordinateConvert.initLocation(curPotentialSomaInfo.getLocation());
            downloadImage();
        } else {
            // TODO: something wrong with curIndex
        }
    }

    private void getBrainList() {
        imageDataSource.getBrainList();
    }

    private void getPotentialLocation() {
        markerFactoryDataSource.getPotentialLocation();
    }

    public void downloadImage() {
        String brainId = curPotentialSomaInfo.getBrainId();
        XYZ loc = coordinateConvert.getCenterLocation();
        String res = resMap.get(brainId);
        if (res == null){
            ToastEasy("Fail to download image, something wrong with res list !");
            return;
        }
        imageDataSource.downloadImage(curPotentialSomaInfo.getBrainId(), res, (int) loc.x , (int) loc.y, (int) loc.z, DEFAULT_IMAGE_SIZE);
    }

    public void getSomaList() {
        String brainId = curPotentialSomaInfo.getBrainId();
        XYZ loc = curPotentialSomaInfo.getLocation();
        Log.e(TAG,"loc: (" + loc.x + ", " + loc.y + ", " + loc.z + ")");
        markerFactoryDataSource.getSomaList(brainId, (int) loc.x, (int) loc.y, (int) loc.z, DEFAULT_IMAGE_SIZE * (int) Math.pow(2, coordinateConvert.getResIndex()-1));
    }

    public void updateSomaList(MarkerList markerListToAdd, JSONArray markerListToDelete) {
        if ((markerListToAdd == null || markerListToAdd.size() == 0) && (markerListToDelete == null || markerListToDelete.length() == 0)){
            return;
        }
        try {
            int locationId = curPotentialSomaInfo.getId();
            String brainId = curPotentialSomaInfo.getBrainId();
            String username = loggedInUser.getUserId();
            markerFactoryDataSource.updateSomaList(brainId, locationId, username,
                    MarkerList.toJSONArray(MarkerList.covertLocalToGlobal(markerListToAdd, coordinateConvert)), markerListToDelete);
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

}
