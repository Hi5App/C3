package com.penglab.hi5.core.ui.collaboration;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.hms.support.api.PendingResultImpl;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.ui.QualityInspection.QualityInspectionViewModel;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.annotation.AnnotationViewModel;
import com.penglab.hi5.core.ui.marker.CoordinateConvert;
import com.penglab.hi5.data.CollorationDataSource;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.QualityInspectionDataSource;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.img.CollaborateNeuronInfo;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.data.model.user.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.agora.rtc.internal.RtcEngineMessage;

public class CollaborationViewModel extends ViewModel {
    private final String TAG = "CollaborationViewModel";
    private final int DEFAULT_IMAGE_SIZE = 128;
    private final int DEFAULT_RES_INDEX = 2;
    private boolean isDownloading = false;
    private boolean noFileLeft = false;

    private final HashMap<String, String> resMap = new HashMap<>();
    private volatile CollaborateNeuronInfo potentialDownloadNeuronInfo = new CollaborateNeuronInfo();
    private final CoordinateConvert coordinateConvert = new CoordinateConvert();
    private final CoordinateConvert downloadCoordinateConvert = new CoordinateConvert();



    public CollaborationViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, ImageDataSource imageDataSource, CollorationDataSource collorationDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.imageInfoRepository = imageInfoRepository;
        this.imageDataSource = imageDataSource;
        this.collorationDataSource = collorationDataSource;
        downloadCoordinateConvert.setImgSize(DEFAULT_IMAGE_SIZE);
        downloadCoordinateConvert.setResIndex(DEFAULT_RES_INDEX);
    }

    public enum AnnotationMode{
        BIG_DATA, NONE
    }

    private final UserInfoRepository userInfoRepository;
    private final ImageInfoRepository imageInfoRepository;
    private final ImageDataSource imageDataSource;
    private final CollorationDataSource collorationDataSource;


    private final MutableLiveData<CollaborationViewModel.AnnotationMode> annotationMode = new MutableLiveData<>();
    private final MutableLiveData<ResourceResult> imageResult = new MutableLiveData<>();
    private final MutableLiveData<String> portStartCollaborate = new MutableLiveData<>();
    public ImageDataSource getImageDataSource() {
        return imageDataSource;
    }
    public boolean isLoggedIn(){
        return userInfoRepository.isLoggedIn();
    }


    public CollorationDataSource getCollorationDataSource() {
        return collorationDataSource;
    }


    public void handleBrainNumber(String brainNumber) {
        Log.e(TAG,"handleBrainNumber"+brainNumber);
        potentialDownloadNeuronInfo.setBrainNumber(brainNumber);
        getNeuronList(brainNumber);
    }

    public LiveData<ResourceResult> getImageResult() {
        return imageResult;
    }

    public  MutableLiveData<String> getPortResult() {
        return portStartCollaborate;
    }

    public void handleBrainListResult(Result result){
        if (result == null) {
            isDownloading = false;
            Log.e(TAG,"Fail to handle brain list result");
        }
        if (result instanceof Result.Success) {
            Log.e(TAG,"begin to handle collaboration brain list result");
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONArray){
                // process Brain List, store res info for each brain
                JSONArray jsonArray = (JSONArray) data;
                Log.e(TAG,"collaborate mode in handle brain list length"+jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String imageId = jsonObject.getString("name");
                        String detail = jsonObject.getString("detail");
//                        Log.e(TAG,"collaborate mode in get imageId"+imageId);
//                        Log.e(TAG,"collaborate mode in get detail"+detail);

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
            } else {
                isDownloading = false;
            }
        } else {
            isDownloading = false;
        }



    }
    public void handleNeuronNumber(String neuronNumber) {
        potentialDownloadNeuronInfo.setNeuronNumber(neuronNumber);
        getAno(neuronNumber);
    }

    public LiveData<CollaborationViewModel.AnnotationMode> getAnnotationMode() {
        return annotationMode;
    }


    public void handleAnoResult(String anoName){
        getDownloadAno(anoName);
    }

    public void handleLoadAnoResult (Result result){
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            Log.e(TAG,"handleloadanoresult"+data);
            if (data instanceof JSONObject) {
                JSONObject loadAnoResult = (JSONObject) data;
                try {
                    String anoName= loadAnoResult.getString("ano");
                    Log.e("anoName",""+anoName);
                    String port= loadAnoResult.getString("port");
                    Log.e("port",""+port);
                    portStartCollaborate.setValue(port);
                } catch (Exception e) {
                    ToastEasy("Fail to parse jsonArray when get user performance !");
                }
            }
        } else {
            ToastEasy(result.toString());
        }


    }
    public String getPortStartCollaborate(){
        Log.e(TAG,"port"+portStartCollaborate.getValue());
        return portStartCollaborate.getValue();

    }


    public void getImageList(){
        collorationDataSource.getImageList();
    }

    public void getNeuronList(String brainNumber) {
        collorationDataSource.getNeuron(brainNumber);
    }

    public void handleLoadImage(CollaborateNeuronInfo collaborateNeuronInfo){
        potentialDownloadNeuronInfo.setLocation(collaborateNeuronInfo.getLocation());
        downloadCoordinateConvert.initLocation(collaborateNeuronInfo.getLocation());
        getBrainList();

    }

    public void getAno(String neuronNumber) {
        collorationDataSource.getAno(neuronNumber);
    }

    public void getDownloadAno(String anoName) {
        String brainName = potentialDownloadNeuronInfo.getBrainName();
        String neuronName = potentialDownloadNeuronInfo.getNeuronName();
        Log.e("brainName",brainName);
        Log.e("neuronName",neuronName);
        collorationDataSource.loadAno(brainName,neuronName,anoName);
    }

    private void getBrainList() {
        imageDataSource.getBrainList();
    }

    public void downloadImage() {
        String brainId = potentialDownloadNeuronInfo.getBrainName();
        XYZ loc = downloadCoordinateConvert.getCenterLocation();
        String res = resMap.get(brainId);
        Log.e("collaborate_brainId"+brainId,"collaborate_loc"+loc.toString());
        Log.e(TAG,"resolution"+res);
        if (res == null){
            ToastEasy("Fail to download image, something wrong with res list !");
            isDownloading = false;
            return;
        }
        imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), res, (int) loc.x , (int) loc.y, (int) loc.z, DEFAULT_IMAGE_SIZE);
        Log.e("collaboration mode download image","brainName:"+potentialDownloadNeuronInfo.getBrainName()+"/resolution:"+res+"/location:"+loc.x+loc.y+loc.z+"DEFAULT_IMAGE_SIZE"+DEFAULT_IMAGE_SIZE);
    }

    public void handleDownloadImageResult(Result result){
        if (result == null) {
            isDownloading = false;
        }
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof String){
                Log.e(TAG,"Collaborate+Download image data" + data);
                openFile();

            }


//                if (curDownloadIndex < arborInfoList.size()-1) {
//                    potentialArborMarkerInfoList.add(lastDownloadPotentialArborMarkerInfo);
//
//
//                    // next image
//                    lastDownloadPotentialArborMarkerInfo = arborInfoList.get(++curDownloadIndex);
//                    lastDownloadCoordinateConvert.initLocation(lastDownloadPotentialArborMarkerInfo.getLocation());
//                    downloadImage();
//                } else if (curDownloadIndex == arborInfoList.size()-1) {
//                    potentialArborMarkerInfoList.add(lastDownloadPotentialArborMarkerInfo);
//                    curDownloadIndex = 0;
//                    isDownloading = false;
//                }
//            } else {
//                Log.e(TAG,"Fail to parse download image result !");
//                isDownloading = false;
//            }
//        } else {
//            Log.e(TAG,"Download image result is error !");
//            isDownloading = false;
        }

    }

    public void openFile() {
        String brainId = potentialDownloadNeuronInfo.getBrainName();
        XYZ location = downloadCoordinateConvert.getCenterLocation();
        String res = resMap.get(brainId);
        if (res == null) {
            imageResult.setValue(new ResourceResult(false, "No res found"));
            return;
        }
        String filePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image" +
                "/" + brainId + "_" + res + "_" + (int)location.x + "_" + (int)location.y + "_" + (int)location.z + ".v3dpbd";
        String fileName = FileManager.getFileName(filePath);
        FileType fileType = FileManager.getFileType(filePath);
        imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String >(filePath), fileType);
        annotationMode.setValue(AnnotationMode.BIG_DATA);
        imageResult.setValue(new ResourceResult(true));

    }














}
