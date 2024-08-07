package com.penglab.hi5.core.ui.collaboration;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.ui.collaboration.CollaborationActivity.Toast_in_Thread_static;

import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.marker.CoordinateConvert;
import com.penglab.hi5.data.CollorationDataSource;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.dataStore.PreferenceSetting;
import com.penglab.hi5.data.model.img.CollaborateNeuronInfo;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollaborationViewModel extends ViewModel {
    private final String TAG = "CollaborationViewModel";
    private final int DEFAULT_IMAGE_SIZE = 128;

    private boolean firstJoinRoom = true;
    public static final String ip_TencentCloud = "114.117.165.134";
    private final int DEFAULT_RES_INDEX = 2;

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

    public LiveData<ResourceResult> getImageResult() {
        return imageResult;
    }

    public MutableLiveData<String> getPortResult() {
        return portStartCollaborate;
    }

    public void handleBrainListResult(Result result) {
        if (result == null) {
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
                        for (String s : rois) {
                            if (s.isEmpty()) {
                                continue;
                            }
                            String res = s.substring(1, s.length() - 1);
                            roiList.add(res);
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
            }
        } else {
        }

    }

    public LiveData<CollaborationViewModel.AnnotationMode> getAnnotationMode() {
        return annotationMode;
    }

    public void handleAnoResult(String swcUuid, String anoName) {
        PreferenceSetting pref = PreferenceSetting.getInstance();
        pref.resetRotationMatrix();
        collorationDataSource.CurrentSwcInfo = new Pair<>(swcUuid,anoName);

        getDownloadAno(anoName);
        getNeuronList(potentialDownloadNeuronInfo.getBrainName());

        collorationDataSource.loadAno(potentialDownloadNeuronInfo.getBrainName(), potentialDownloadNeuronInfo.getNeuronName(), anoName);

    }

    public void handleProjectResult(String projectUuid, String projectName) {
        collorationDataSource.CurrentProjectInfo = new Pair<>(projectUuid,projectName);
        collorationDataSource.getSwcNameAndUuidByProject(projectUuid);
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

    public void getNeuronList(String brainNumber) {
        collorationDataSource.getNeuron(brainNumber);
    }

    public void handleLoadImage(CollaborateNeuronInfo collaborateNeuronInfo) {
        potentialDownloadNeuronInfo.setLocation(collaborateNeuronInfo.getLocation());
        downloadCoordinateConvert.initLocation(collaborateNeuronInfo.getLocation());
        Communicator.getInstance().setUp(downloadCoordinateConvert);
        getBrainList();
    }

    public void getDownloadAno(String anoName) {
        String[] parts = anoName.split("_");
        String image="", neuron="";

        // Check if image has a suffix
        if(parts.length >= 3 && parts[1].length() == 1){
            image = parts[0] + "_" + parts[1];
            neuron = image + "_" + parts[2];
        }
        else if(parts.length >= 2) {
            image = parts[0];
            neuron = image + "_" + parts[1];
        }else{
            throw new IllegalArgumentException("Invalid annotation name");
        }

        potentialDownloadNeuronInfo.setBrainNumber(image);
        potentialDownloadNeuronInfo.setNeuronNumber(neuron);
        Log.e("brainName", image);
        Log.e("neuronName", neuron);
    }

    private void getBrainList() {
        imageDataSource.getBrainList();
    }

    // 调用之前需更新centerLocation
    public void updateImgStartLocation(String resStr){
        // 根据分辨率大小再次更新coordinate
        String patternString = "RES\\((\\d+)x(\\d+)x(\\d+)\\)";
        XYZ res = new XYZ();
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher1 = pattern.matcher(resStr);
        while(matcher1.find()){
            res.y = Integer.parseInt(Objects.requireNonNull(matcher1.group(1)));
            res.x = Integer.parseInt(Objects.requireNonNull(matcher1.group(2)));
            res.z = Integer.parseInt(Objects.requireNonNull(matcher1.group(3)));
        }
        XYZ centerLocation = downloadCoordinateConvert.getCenterLocation();
        XYZ min = new XYZ();
        XYZ max = new XYZ();
        if(centerLocation.x + DEFAULT_IMAGE_SIZE / 2 < res.x){
            max.x = centerLocation.x + DEFAULT_IMAGE_SIZE / 2;
        }
        else{
            max.x = res.x;
        }
        if(centerLocation.y + DEFAULT_IMAGE_SIZE / 2 < res.y){
            max.y = centerLocation.y + DEFAULT_IMAGE_SIZE / 2;
        }
        else{
            max.y = res.y;
        }
        if(centerLocation.z + DEFAULT_IMAGE_SIZE / 2 < res.z){
            max.z = centerLocation.z + DEFAULT_IMAGE_SIZE / 2;
        }
        else{
            max.z = res.z;
        }
        if(centerLocation.x - DEFAULT_IMAGE_SIZE / 2 >= 0){
            min.x = centerLocation.x - DEFAULT_IMAGE_SIZE / 2;
        }
        else{
            min.x = 0;
        }
        if(centerLocation.y - DEFAULT_IMAGE_SIZE / 2 >= 0){
            min.y = centerLocation.y - DEFAULT_IMAGE_SIZE / 2;
        }
        else{
            min.y = 0;
        }
        if(centerLocation.z - DEFAULT_IMAGE_SIZE / 2 >= 0){
            min.z = centerLocation.z - DEFAULT_IMAGE_SIZE / 2;
        }
        else{
            min.z = 0;
        }
        downloadCoordinateConvert.updateMinAndMaxLoc(min.x, min.y, min.z, max.x, max.y, max.z);
        downloadCoordinateConvert.updateStartLoc(min.x, min.y, min.z);
    }

    public void downloadImage() {
        String brainId = potentialDownloadNeuronInfo.getBrainName();
        List<String> resList = resMap.get(brainId);
//        Log.e("collaborate_brainId"+brainId,"collaborate_loc"+loc.toString());
        Log.e(TAG, "resolution:");
        assert resList != null;
        Log.e(TAG, resList.toString());

        if (resList.size() == 0) {
            ToastEasy("Fail to download image, something wrong with res list !");
            return;
        }
//        for (int i = 0; i < resList.size(); i++) {
//            XYZ coor = downloadCoordinateConvert.convertMaxResToCurRes(loc.x, loc.y, loc.z, i + 1);
//            imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(i), (int) coor.x, (int) coor.y, (int) coor.z, DEFAULT_IMAGE_SIZE);
//        }
        // 根据分辨率大小再次更新coordinate
        String subMaxResStr = resList.get(1);
        updateImgStartLocation(subMaxResStr);
//        XYZ coor = downloadCoordinateConvert.convertMaxResToCurRes(loc.x, loc.y, loc.z, 2);
        imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(1), downloadCoordinateConvert.imgXMin, downloadCoordinateConvert.imgYMin,
                downloadCoordinateConvert.imgZMin, downloadCoordinateConvert.imgXMax, downloadCoordinateConvert.imgYMax, downloadCoordinateConvert.imgZMax);
//        Log.e("collaboration mode download image","brainName:"+potentialDownloadNeuronInfo.getBrainName()+"/resolution:"+res+"/location:"+loc.x+loc.y+loc.z+"DEFAULT_IMAGE_SIZE"+DEFAULT_IMAGE_SIZE);
    }

    public void handleDownloadImageResult(Result result) {
        if (result == null) {
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
        int xMin = downloadCoordinateConvert.imgXMin;
        int yMin = downloadCoordinateConvert.imgYMin;
        int zMin = downloadCoordinateConvert.imgZMin;
        int xMax = downloadCoordinateConvert.imgXMax;
        int yMax = downloadCoordinateConvert.imgYMax;
        int zMax = downloadCoordinateConvert.imgZMax;
        List<String> resList = resMap.get(brainId);
        if (resList == null) {
            imageResult.setValue(new ResourceResult(false, "No res found"));
            return;
        }
        String filename = brainId + "_" + resList.get(resIndex - 1) + "_"  + xMin + "_" + xMax + "_" + yMin + "_" + yMax + "_" + zMin + "_" + zMax + ".v3dpbd";
        String filePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image" +
                "/" + filename;
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
        String brainId = potentialDownloadNeuronInfo.getBrainName();
        List<String> resList = resMap.get(brainId);
        assert resList != null;
        String resStr = resList.get(position);
        updateImgStartLocation(resStr);

//        downloadCoordinateConvert.setStartLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x - downloadCoordinateConvert.getImgSize() / 2,
//                downloadCoordinateConvert.getCenterLocation().y - downloadCoordinateConvert.getImgSize() / 2, downloadCoordinateConvert.getCenterLocation().z - downloadCoordinateConvert.getImgSize() / 2));
        downloadCoordinateConvert.setResIndex(position + 1);
        imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resStr, downloadCoordinateConvert.imgXMin, downloadCoordinateConvert.imgYMin,
                downloadCoordinateConvert.imgZMin, downloadCoordinateConvert.imgXMax, downloadCoordinateConvert.imgYMax, downloadCoordinateConvert.imgZMax);
//        SwitchFile(position);
    }

    public void navigateAndZoomInBlock(int offset_x, int offset_y, int offset_z) {
        if (resMap.get(potentialDownloadNeuronInfo.getBrainName()) != null) {
            List<String> resList = resMap.get(potentialDownloadNeuronInfo.getBrainName());
            assert resList != null;
            String img_size = resList.get(downloadCoordinateConvert.getResIndex() - 1).replace("RES(", "").replace(")", "");

            int img_size_x_i = Integer.parseInt(img_size.split("x")[1]);
            int img_size_y_i = Integer.parseInt(img_size.split("x")[0]);
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
//                if (offset_x_i - size_i / 2 <= 0)
//                    offset_x_i = size_i / 2 + 1;
//                else if (offset_x_i + size_i / 2 >= img_size_x_i - 1)
//                    offset_x_i = img_size_x_i - size_i / 2 - 1;
            }

            if ((offset_y_i + offset_y) <= 1 || (offset_y_i + offset_y) >= img_size_y_i - 1) {
//            System.out.println("----- You have already reached left boundary!!! -----");
                Toast_in_Thread_static("You have already reached boundary!!!");

            } else {
                offset_y_i += offset_y;
//                if (offset_y_i - size_i / 2 <= 0)
//                    offset_y_i = size_i / 2 + 1;
//                else if (offset_y_i + size_i / 2 >= img_size_y_i - 1)
//                    offset_y_i = img_size_y_i - size_i / 2 - 1;
            }

            if ((offset_z_i + offset_z) <= 1 || (offset_z_i + offset_z) >= img_size_z_i - 1) {
//            System.out.println("----- You have already reached left boundary!!! -----");
                Toast_in_Thread_static("You have already reached boundary!!!");

            } else {
                offset_z_i += offset_z;
//                if (offset_z_i - size_i / 2 <= 0)
//                    offset_z_i = size_i / 2 + 1;
//                else if (offset_z_i + size_i / 2 >= img_size_z_i - 1)
//                    offset_z_i = img_size_z_i - size_i / 2 - 1;
            }

            Log.e(TAG, String.format("after: x %d, y %d, z %d", offset_x_i, offset_y_i, offset_z_i));
            downloadCoordinateConvert.setCenterLocation(new XYZ(offset_x_i, offset_y_i, offset_z_i));
//            downloadCoordinateConvert.setStartLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x - size_i / 2,
//                    downloadCoordinateConvert.getCenterLocation().y - size_i / 2, downloadCoordinateConvert.getCenterLocation().z - size_i / 2));
            updateImgStartLocation(resList.get(downloadCoordinateConvert.getResIndex() - 1));

            if (downloadCoordinateConvert.getResIndex() <= 1) {
//                imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(downloadCoordinateConvert.getResIndex() - 1), (int) downloadCoordinateConvert.getCenterLocation().x,
//                        (int) downloadCoordinateConvert.getCenterLocation().y, (int) downloadCoordinateConvert.getCenterLocation().z, DEFAULT_IMAGE_SIZE);
                imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(downloadCoordinateConvert.getResIndex() - 1), downloadCoordinateConvert.imgXMin, downloadCoordinateConvert.imgYMin,
                        downloadCoordinateConvert.imgZMin, downloadCoordinateConvert.imgXMax, downloadCoordinateConvert.imgYMax, downloadCoordinateConvert.imgZMax);
            } else {
                downloadCoordinateConvert.setResIndex(downloadCoordinateConvert.getResIndex() - 1);
                downloadCoordinateConvert.setCenterLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x * 2,
                        downloadCoordinateConvert.getCenterLocation().y * 2, downloadCoordinateConvert.getCenterLocation().z * 2));

//                downloadCoordinateConvert.setStartLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x - size_i / 2,
//                        downloadCoordinateConvert.getCenterLocation().y - size_i / 2, downloadCoordinateConvert.getCenterLocation().z - size_i / 2));
//
//                imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(downloadCoordinateConvert.getResIndex() - 1), (int) downloadCoordinateConvert.getCenterLocation().x,
//                        (int) downloadCoordinateConvert.getCenterLocation().y, (int) downloadCoordinateConvert.getCenterLocation().z, DEFAULT_IMAGE_SIZE);
                updateImgStartLocation(resList.get(downloadCoordinateConvert.getResIndex() - 1));
                imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(downloadCoordinateConvert.getResIndex() - 1), downloadCoordinateConvert.imgXMin, downloadCoordinateConvert.imgYMin,
                        downloadCoordinateConvert.imgZMin, downloadCoordinateConvert.imgXMax, downloadCoordinateConvert.imgYMax, downloadCoordinateConvert.imgZMax);
            }

        }


    }

    public void shiftBlock(CollaborationActivity.ShiftDirection shiftDirection) {
        if (resMap.get(potentialDownloadNeuronInfo.getBrainName()) != null) {
            List<String> resList = resMap.get(potentialDownloadNeuronInfo.getBrainName());
            assert resList != null;
            String img_size = resList.get(downloadCoordinateConvert.getResIndex() - 1).replace("RES(", "").replace(")", "");

            int img_size_x_i = Integer.parseInt(img_size.split("x")[1]);
            int img_size_y_i = Integer.parseInt(img_size.split("x")[0]);
            int img_size_z_i = Integer.parseInt(img_size.split("x")[2]);

            int offset_x_i = (int) downloadCoordinateConvert.getCenterLocation().x;
            int offset_y_i = (int) downloadCoordinateConvert.getCenterLocation().y;
            int offset_z_i = (int) downloadCoordinateConvert.getCenterLocation().z;
            int size_i = downloadCoordinateConvert.getImgSize();

            Log.e(TAG, String.format("img: x %d, y %d, z %d", img_size_x_i, img_size_y_i, img_size_z_i));
            Log.e(TAG, String.format("cur: x %d, y %d, z %d", offset_x_i, offset_y_i, offset_z_i));

            switch (shiftDirection) {
                case RIGHT: {
                    if (offset_x_i + size_i / 2 >= img_size_x_i - 1) {
                        Toast_in_Thread_static("You have already reached boundary!!!");
                        return;
                    } else {
                        offset_x_i += size_i / 2;
//                        if (offset_x_i + size_i / 2 > img_size_x_i - 1) {
//                            offset_x_i = img_size_x_i - size_i / 2 - 1;
//                        }
                    }
                    break;
                }
                case LEFT: {
                    if (offset_x_i - size_i / 2 <= 1) {
                        Toast_in_Thread_static("You have already reached boundary!!!");
                        return;
                    } else {
                        offset_x_i -= size_i / 2;
//                        if (offset_x_i - size_i / 2 < 1) {
//                            offset_x_i = 1 + size_i / 2;
//                        }
                    }
                    break;
                }
                case UP: {
                    if (offset_z_i + size_i / 2 >= img_size_z_i - 1) {
                        Toast_in_Thread_static("You have already reached boundary!!!");
                        return;
                    } else {
                        offset_z_i += size_i / 2;
//                        if (offset_z_i + size_i / 2 > img_size_z_i - 1) {
//                            offset_z_i = img_size_z_i - size_i / 2 - 1;
//                        }
                    }
                    break;
                }
                case DOWN: {
                    if (offset_z_i - size_i / 2 <= 1) {
                        Toast_in_Thread_static("You have already reached boundary!!!");
                        return;
                    } else {
                        offset_z_i -= size_i / 2;
//                        if (offset_z_i - size_i / 2 < 1) {
//                            offset_z_i = 1 + size_i / 2;
//                        }
                    }
                    break;
                }
                case FRONT: {
                    if (offset_y_i + size_i / 2 >= img_size_y_i - 1) {
                        Toast_in_Thread_static("You have already reached boundary!!!");
                        return;
                    } else {
                        offset_y_i += size_i / 2;
//                        if (offset_y_i + size_i / 2 > img_size_x_i - 1) {
//                            offset_y_i = img_size_x_i - size_i / 2 - 1;
//                        }
                    }
                    break;
                }
                case BACK: {
                    if (offset_y_i - size_i / 2 <= 1) {
                        Toast_in_Thread_static("You have already reached boundary!!!");
                        return;
                    } else {
                        offset_y_i -= size_i / 2;
//                        if (offset_y_i - size_i / 2 < 1) {
//                            offset_y_i = 1 + size_i / 2;
//                        }
                    }
                    break;
                }
                default:
                    break;
            }

            Log.e(TAG, String.format("after: x %d, y %d, z %d", offset_x_i, offset_y_i, offset_z_i));
            downloadCoordinateConvert.setCenterLocation(new XYZ(offset_x_i, offset_y_i, offset_z_i));
//            downloadCoordinateConvert.setStartLocation(new XYZ(downloadCoordinateConvert.getCenterLocation().x - size_i / 2,
//                    downloadCoordinateConvert.getCenterLocation().y - size_i / 2, downloadCoordinateConvert.getCenterLocation().z - size_i / 2));
            updateImgStartLocation(resList.get(downloadCoordinateConvert.getResIndex() - 1));
//            imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(downloadCoordinateConvert.getResIndex() - 1), (int) downloadCoordinateConvert.getCenterLocation().x,
//                    (int) downloadCoordinateConvert.getCenterLocation().y, (int) downloadCoordinateConvert.getCenterLocation().z, DEFAULT_IMAGE_SIZE);
            imageDataSource.downloadImage(potentialDownloadNeuronInfo.getBrainName(), resList.get(downloadCoordinateConvert.getResIndex() - 1), downloadCoordinateConvert.imgXMin, downloadCoordinateConvert.imgYMin,
                    downloadCoordinateConvert.imgZMin, downloadCoordinateConvert.imgXMax, downloadCoordinateConvert.imgYMax, downloadCoordinateConvert.imgZMax);
        }

    }

    public void getUserIdForCollaborate(String username){
        collorationDataSource.getUserId(username);
    }

    private void initMsgConnector(String port) {
        MsgConnector msgConnector = MsgConnector.getInstance();
        if (!firstJoinRoom)
            msgConnector.releaseConnection();
        msgConnector.setIp(ip_TencentCloud);
        msgConnector.setPort(port);
        msgConnector.initConnection();
    }

}
