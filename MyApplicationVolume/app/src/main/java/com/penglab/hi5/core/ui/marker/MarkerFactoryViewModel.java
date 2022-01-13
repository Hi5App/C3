package com.penglab.hi5.core.ui.marker;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.MarkerFactoryDataSource;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.data.model.img.PotentialSomaInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jackiexing on 01/10/21
 */
public class MarkerFactoryViewModel extends ViewModel {
    private final String TAG = "MarkerFactoryViewModel";
    public enum AnnotationMode{
       BIG_DATA, NONE
    }

    public enum WorkStatus{
        GET_POTENTIAL_LOCATION, NONE
    }

    private final MutableLiveData<AnnotationMode> annotationMode = new MutableLiveData<>();
    private final MutableLiveData<WorkStatus> workStatus = new MutableLiveData<>();
    private final MutableLiveData<MarkerList> syncMarkerList = new MutableLiveData<>();
    private final MutableLiveData<ResourceResult> imageResult = new MutableLiveData<>();

    private final ImageInfoRepository imageInfoRepository;
    private final MarkerFactoryDataSource markerFactoryDataSource;
    private final ImageDataSource imageDataSource;

    private final HashMap<String, String> resMap = new HashMap<>();
    private final ArrayList<PotentialSomaInfo> potentialSomaInfoList = new ArrayList<>();
    private PotentialSomaInfo curPotentialSomaInfo;
    private int curIndex = -1;

    public MarkerFactoryViewModel(ImageInfoRepository imageInfoRepository, MarkerFactoryDataSource markerFactoryDataSource, ImageDataSource imageDataSource) {
        this.imageInfoRepository = imageInfoRepository;
        this.markerFactoryDataSource = markerFactoryDataSource;
        this.imageDataSource = imageDataSource;
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

    public void updateImageResult(Result result) {
        if (result instanceof Result.Success){
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONArray){
                JSONArray jsonArray = (JSONArray) data;
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String imageId = jsonObject.getString("name");
                        String detail = jsonObject.getString("detail");
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
                String fileName = FileManager.getFileName((String) data);
                FileType fileType = FileManager.getFileType((String) data);
                imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String >((String) data), fileType);
                imageResult.setValue(new ResourceResult(true));
            }
        } else {

        }
    }

    public void updateSomaResult(Result result) {
        if (result instanceof Result.Success){
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof PotentialSomaInfo){
                curPotentialSomaInfo = (PotentialSomaInfo) data;
                // get res list when first download img
                if (resMap.isEmpty()){
                    getBrainList();
                } else {
                    downloadImage();
                }
                // TODO: open image
            } else if (data instanceof MarkerList){
                // TODO: import somaList
                syncMarkerList.setValue((MarkerList) data);
            } else if (data instanceof String){
                Log.e(TAG,"data: " + data);
            }
        } else {

        }
    }

    public void openNewFile() {
        getPotentialLocation();
        curIndex = potentialSomaInfoList.size();
        annotationMode.setValue(AnnotationMode.BIG_DATA);
    }

    public void previousFile() {
        if (curIndex == 0) {
            ToastEasy("You have reached the earliest image !");
        } else if (curIndex <= potentialSomaInfoList.size()-1 && curIndex > 0) {
            curIndex--;
            curPotentialSomaInfo = potentialSomaInfoList.get(curIndex);
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
        XYZ loc = curPotentialSomaInfo.getLocation();
        String res = resMap.get(brainId);
        if (res == null){
            ToastEasy("Fail to download image, something wrong with res list !");
            return;
        }
        imageDataSource.downloadImage(curPotentialSomaInfo.getBrainId(), res, (int) loc.x , (int) loc.y, (int) loc.z, 128);
    }

    public void getSomaList() {
        String brainId = curPotentialSomaInfo.getBrainId();
        XYZ loc = curPotentialSomaInfo.getLocation();
        markerFactoryDataSource.getSomaList(brainId, (int) loc.x, (int) loc.y, (int) loc.z);
    }

    public void insertSomaList(MarkerList markerList) {
        try {
            String brainId = curPotentialSomaInfo.getBrainId();
            int locationId = curPotentialSomaInfo.getId();
            markerFactoryDataSource.insertSomaList(brainId, locationId, MarkerList.toJSONArray(markerList));
        } catch (JSONException e) {
            ToastEasy("Fail to convert MarkerList ot JSONArray !");
            e.printStackTrace();
        }
    }
}
