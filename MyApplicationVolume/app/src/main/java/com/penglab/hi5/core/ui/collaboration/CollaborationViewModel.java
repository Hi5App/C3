package com.penglab.hi5.core.ui.collaboration;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.ui.collaboration.CollaborationActivity.Toast_in_Thread_static;

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
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.basic.ImageInfo;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
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

    private final HashMap<String, List<String>> resMap = new HashMap<>();
    private volatile CollaborateNeuronInfo potentialDownloadNeuronInfo = new CollaborateNeuronInfo();
    private final CoordinateConvert downloadCoordinateConvert = new CoordinateConvert();

    public CollaborationViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, ImageDataSource imageDataSource, CollorationDataSource collorationDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.imageInfoRepository = imageInfoRepository;
        this.imageDataSource = imageDataSource;
        this.collorationDataSource = collorationDataSource;
        downloadCoordinateConvert.setImgSize(DEFAULT_IMAGE_SIZE);
        downloadCoordinateConvert.setResIndex(DEFAULT_RES_INDEX);
    }

    public enum AnnotationMode {
        BIG_DATA, NONE
    }

    private final UserInfoRepository userInfoRepository;
    private final ImageInfoRepository imageInfoRepository;
    private final ImageDataSource imageDataSource;
    private final CollorationDataSource collorationDataSource;
    private final MutableLiveData<CollaborationViewModel.AnnotationMode> annotationMode = new MutableLiveData<>();
    private final MutableLiveData<ResourceResult> imageResult = new MutableLiveData<>();
    private final MutableLiveData<String> portStartCollaborate = new MutableLiveData<>();

    private final CollaborationArborInfoState collaborationArborInfoState = CollaborationArborInfoState.getInstance();

    public HashMap<String, List<String>> getResMap() {
        return resMap;
    }

    public ImageDataSource getImageDataSource() {
        return imageDataSource;
    }

    public boolean isLoggedIn() {
        return userInfoRepository.isLoggedIn();
    }

    public CollorationDataSource getCollorationDataSource() {
        return collorationDataSource;
    }

    public CoordinateConvert getCoordinateConvert() {
        return downloadCoordinateConvert;
    }

    public CollaborationArborInfoState getCollaborationArborInfoState() {
        return collaborationArborInfoState;
    }

    public void handleBrainNumber(String brainNumber) {
        Log.e(TAG, "handleBrainNumber" + brainNumber);
        potentialDownloadNeuronInfo.setBrainNumber(brainNumber);
        getNeuronList(brainNumber);
    }

    public LiveData<ResourceResult> getImageResult() {
        return imageResult;
    }

    public MutableLiveData<String> getPortResult() {
        return portStartCollaborate;
    }

    public void handleBrainListResult(Result result) {
        if (result == null) {
            isDownloading = false;
            Log.e(TAG, "Fail to handle brain list result");
        }
        if (result instanceof Result.Success) {
            Log.e(TAG, "begin to handle collaboration brain list result");
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONArray) {
                // process Brain List, store res info for each brain
                JSONArray jsonArray = (JSONArray) data;
                Log.e(TAG, "collaborate mode in handle brain list length" + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String imageId = jsonObject.getString("name");
                        String detail = jsonObject.getString("detail");
                        // parse brain info
                        detail = detail.substring(1, detail.length() - 1);
                        String[] rois = detail.split(", ");
                        List<String> roiList = new ArrayList<>();
                        for (int j = 0; j < rois.length; j++) {
                            rois[j] = rois[j].substring(1, rois[j].length() - 1);
                            roiList.add(rois[j]);
                        }
                        if (rois.length >= 1) {
                            resMap.put(imageId, roiList);
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

    public void handleAnoResult(String anoName) {
        getDownloadAno(anoName);
    }

    public void handleLoadAnoResult(Result result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            Log.e(TAG, "handleloadanoresult" + data);
            if (data instanceof JSONObject) {
                JSONObject loadAnoResult = (JSONObject) data;
                try {
                    String anoName = loadAnoResult.getString("ano");
                    Log.e("anoName", "" + anoName);
                    String port = loadAnoResult.getString("port");
                    Log.e("port", "" + port);
                    portStartCollaborate.setValue(port);
                } catch (Exception e) {
                    ToastEasy("Fail to parse jsonArray when get user performance !");
                }
            }
        } else {
            ToastEasy(result.toString());
        }


    }

    public CollaborateNeuronInfo getPotentialDownloadNeuronInfo() {
        return potentialDownloadNeuronInfo;
    }

    public void getImageList() {
        collorationDataSource.getImageList();
    }

    public void getNeuronList(String brainNumber) {
        collorationDataSource.getNeuron(brainNumber);
    }

    public void handleLoadImage(CollaborateNeuronInfo collaborateNeuronInfo) {
        potentialDownloadNeuronInfo.setLocation(collaborateNeuronInfo.getLocation());
        downloadCoordinateConvert.initLocation(collaborateNeuronInfo.getLocation());
        Communicator.getInstance().setUp(downloadCoordinateConvert);
        getBrainList();

    }

    public void getAno(String neuronNumber) {
        collorationDataSource.getAno(neuronNumber);
    }

    public void getDownloadAno(String anoName) {
        String brainName = potentialDownloadNeuronInfo.getBrainName();
        String neuronName = potentialDownloadNeuronInfo.getNeuronName();
        Log.e("brainName", brainName);
        Log.e("neuronName", neuronName);
        collorationDataSource.loadAno(brainName, neuronName, anoName);
    }

    private void getBrainList() {
        imageDataSource.getBrainList();
    }

    public void downloadImage() {
        String brainId = potentialDownloadNeuronInfo.getBrainName();
        XYZ loc = downloadCoordinateConvert.getCenterLocationInMaxRes();
        List<String> resList = resMap.get(brainId);
//        Log.e("collaborate_brainId"+brainId,"collaborate_loc"+loc.toString());
        Log.e(TAG, "resolution:");
        assert resList != null;
        Log.e(TAG, resList.toString());

        if (resList.size() == 0) {
            ToastEasy("Fail to download image, something wrong with res list !");
            isDownloading = false;
            return;
        }
//        for (int i = 0; i < resList.size(); i++) {
//            XYZ coor = downloadCoordinateConvert.convertMaxResToCurRes(loc.x, loc.y, loc.z, i + 1);
//            imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(i), (int) coor.x, (int) coor.y, (int) coor.z, DEFAULT_IMAGE_SIZE);
//        }
        XYZ coor = downloadCoordinateConvert.convertMaxResToCurRes(loc.x, loc.y, loc.z, 2);
        imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(1), (int) coor.x, (int) coor.y, (int) coor.z, DEFAULT_IMAGE_SIZE);
//        Log.e("collaboration mode download image","brainName:"+potentialDownloadNeuronInfo.getBrainName()+"/resolution:"+res+"/location:"+loc.x+loc.y+loc.z+"DEFAULT_IMAGE_SIZE"+DEFAULT_IMAGE_SIZE);
    }

    public void handleDownloadImageResult(Result result) {
        if (result == null) {
            isDownloading = false;
        }
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof String) {
                Log.e(TAG, "Collaborate+Download image data" + data);
                openFile(downloadCoordinateConvert.getResIndex());
            }

        }

    }

    public void openFile(int resIndex) {
        String brainId = potentialDownloadNeuronInfo.getBrainName();
        XYZ loc=downloadCoordinateConvert.getCenterLocation();
        List<String> resList = resMap.get(brainId);
        if (resList == null) {
            imageResult.setValue(new ResourceResult(false, "No res found"));
            return;
        }
        String filePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image" +
                "/" + brainId + "_" + resList.get(resIndex - 1) + "_" + (int) loc.x + "_" + (int) loc.y + "_" + (int) loc.z + ".v3dpbd";
        String fileName = FileManager.getFileName(filePath);
        FileType fileType = FileManager.getFileType(filePath);
        imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String>(filePath), fileType);
        annotationMode.setValue(AnnotationMode.BIG_DATA);
        imageResult.setValue(new ResourceResult(true));

    }


    public void switchRes(int position, String res) {
        if (downloadCoordinateConvert.getResIndex() - 1 == position)
            return;
        Log.e(TAG, "Collaborate+SwitchRes" + res);

        float ratio = (float) Math.pow(2, downloadCoordinateConvert.getResIndex() - position - 1);

        downloadCoordinateConvert.setCenterLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x * ratio,
                downloadCoordinateConvert.getCenterLocation().y * ratio, downloadCoordinateConvert.getCenterLocation().z * ratio));
        downloadCoordinateConvert.setStartLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x - downloadCoordinateConvert.getImgSize() / 2,
                downloadCoordinateConvert.getCenterLocation().y - downloadCoordinateConvert.getImgSize() / 2, downloadCoordinateConvert.getCenterLocation().z - downloadCoordinateConvert.getImgSize() / 2));

        downloadCoordinateConvert.setResIndex(position + 1);
        List<String> resList = resMap.get(potentialDownloadNeuronInfo.getBrainName());
        assert resList != null;
        imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(downloadCoordinateConvert.getResIndex() - 1), (int) downloadCoordinateConvert.getCenterLocation().x,
                (int) downloadCoordinateConvert.getCenterLocation().y, (int) downloadCoordinateConvert.getCenterLocation().z, DEFAULT_IMAGE_SIZE);
//        SwitchFile(position);
    }

    public void navigateAndZoomInBlock(int offset_x, int offset_y, int offset_z) {
        if(resMap.get(potentialDownloadNeuronInfo.getBrainName()) != null) {
            List<String> resList = resMap.get(potentialDownloadNeuronInfo.getBrainName());
            String img_size = resList.get(downloadCoordinateConvert.getResIndex() - 1).replace("RES(", "").replace(")", "");

            int img_size_x_i = Integer.parseInt(img_size.split("x")[0]);
            int img_size_y_i = Integer.parseInt(img_size.split("x")[1]);
            int img_size_z_i = Integer.parseInt(img_size.split("x")[2]);

            int offset_x_i = (int) downloadCoordinateConvert.getCenterLocation().x;
            int offset_y_i = (int) downloadCoordinateConvert.getCenterLocation().y;
            int offset_z_i = (int) downloadCoordinateConvert.getCenterLocation().z;
            int size_i = downloadCoordinateConvert.getImgSize();

            Log.e(TAG, String.format("img: x %d, y %d, z %d", img_size_x_i, img_size_y_i, img_size_z_i));
            Log.e(TAG, String.format("cur: x %d, y %d, z %d", offset_x_i, offset_y_i, offset_z_i));

            if ((offset_x_i + offset_x) <= 1 || (offset_x_i + offset_x) >= img_size_x_i - 1) {
//            System.out.println("----- You have already reached left boundary!!! -----");
                Toast_in_Thread_static("You have already reached boundary!!!");

            } else {
                offset_x_i += offset_x;
                if (offset_x_i - size_i / 2 <= 0)
                    offset_x_i = size_i / 2 + 1;
                else if (offset_x_i + size_i / 2 >= img_size_x_i - 1)
                    offset_x_i = img_size_x_i - size_i / 2 - 1;
            }

            if ((offset_y_i + offset_y) <= 1 || (offset_y_i + offset_y) >= img_size_y_i - 1) {
//            System.out.println("----- You have already reached left boundary!!! -----");
                Toast_in_Thread_static("You have already reached boundary!!!");

            } else {
                offset_y_i += offset_y;
                if (offset_y_i - size_i / 2 <= 0)
                    offset_y_i = size_i / 2 + 1;
                else if (offset_y_i + size_i / 2 >= img_size_y_i - 1)
                    offset_y_i = img_size_y_i - size_i / 2 - 1;
            }

            if ((offset_z_i + offset_z) <= 1 || (offset_z_i + offset_z) >= img_size_z_i - 1) {
//            System.out.println("----- You have already reached left boundary!!! -----");
                Toast_in_Thread_static("You have already reached boundary!!!");

            } else {
                offset_z_i += offset_z;
                if (offset_z_i - size_i / 2 <= 0)
                    offset_z_i = size_i / 2 + 1;
                else if (offset_z_i + size_i / 2 >= img_size_z_i - 1)
                    offset_z_i = img_size_z_i - size_i / 2 - 1;
            }

            Log.e(TAG, String.format("after: x %d, y %d, z %d", offset_x_i, offset_y_i, offset_z_i));
            downloadCoordinateConvert.setCenterLocation(new XYZ(offset_x_i, offset_y_i, offset_z_i));
            downloadCoordinateConvert.setStartLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x - size_i / 2,
                    downloadCoordinateConvert.getCenterLocation().y - size_i / 2, downloadCoordinateConvert.getCenterLocation().z - size_i / 2));

            if (downloadCoordinateConvert.getResIndex() <= 1) {
                imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(downloadCoordinateConvert.getResIndex() - 1), (int) downloadCoordinateConvert.getCenterLocation().x,
                        (int) downloadCoordinateConvert.getCenterLocation().y, (int) downloadCoordinateConvert.getCenterLocation().z, DEFAULT_IMAGE_SIZE);
                return;
            } else {
                downloadCoordinateConvert.setResIndex(downloadCoordinateConvert.getResIndex() - 1);
                downloadCoordinateConvert.setCenterLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x * 2,
                        downloadCoordinateConvert.getCenterLocation().y * 2, downloadCoordinateConvert.getCenterLocation().z * 2));

                downloadCoordinateConvert.setStartLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x - size_i / 2,
                        downloadCoordinateConvert.getCenterLocation().y - size_i / 2, downloadCoordinateConvert.getCenterLocation().z - size_i / 2));

                imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(downloadCoordinateConvert.getResIndex() - 1), (int) downloadCoordinateConvert.getCenterLocation().x,
                        (int) downloadCoordinateConvert.getCenterLocation().y, (int) downloadCoordinateConvert.getCenterLocation().z, DEFAULT_IMAGE_SIZE);
            }

        }


    }

}
