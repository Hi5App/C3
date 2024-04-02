package com.penglab.hi5.core.ui.check;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.data.AnnotationDataSource;
import com.penglab.hi5.data.CheckDataSource;
import com.penglab.hi5.data.CheckArborDataSource;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.model.img.AnoInfo;
import com.penglab.hi5.data.model.img.ArborInfo;
import com.penglab.hi5.data.model.img.BrainInfo;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.data.model.img.NeuronInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jackiexing on 12/18/21
 */
public class CheckViewModel extends ViewModel {

    private ImageDataSource imageDataSource;
    private AnnotationDataSource annotationDataSource;
    private CheckDataSource checkDataSource;
    private CheckArborDataSource checkArborDataSource;
    private CheckArborInfoState checkArborInfoState;

    private MutableLiveData<ResourceResult> imageResult = new MutableLiveData<>();
    private MutableLiveData<ResourceResult> annotationResult = new MutableLiveData<>();
    private MutableLiveData<ResourceResult> checkArborResult = new MutableLiveData<>();

    private ImageInfoRepository imageInfoRepository;

    public CheckViewModel(ImageDataSource imageDataSource, AnnotationDataSource annotationDataSource,
                          CheckDataSource checkDataSource, ImageInfoRepository imageInfoRepository,
                          CheckArborDataSource checkArborDataSource, CheckArborInfoState checkArborInfoState) {
        this.imageDataSource = imageDataSource;
        this.annotationDataSource = annotationDataSource;
        this.checkDataSource = checkDataSource;
        this.imageInfoRepository = imageInfoRepository;
        this.checkArborDataSource = checkArborDataSource;
        this.checkArborInfoState = checkArborInfoState;
    }

    ImageDataSource getImageDataSource() {
        return imageDataSource;
    }

    public AnnotationDataSource getAnnotationDataSource() {
        return annotationDataSource;
    }

    public CheckDataSource getCheckDataSource() {
        return checkDataSource;
    }

    public CheckArborDataSource getCheckArborDataSource() {
        return checkArborDataSource;
    }

    public CheckArborInfoState getCheckArborInfoState() {
        return checkArborInfoState;
    }

    public MutableLiveData<ResourceResult> getCheckArborResult() {
        return checkArborResult;
    }

    public void getBrainList() {
        imageDataSource.getBrainList();
    }

    public void getImageWithROI(int roiPosition) {
        checkArborInfoState.zoomToROI(roiPosition);
        String roi = checkArborInfoState.getRois()[roiPosition];
        ArborInfo chosenArborInfo = checkArborInfoState.getChosenArbor();
        imageDataSource.downloadImage(chosenArborInfo.getImageId(), roi, chosenArborInfo.getXc(), chosenArborInfo.getYc(), chosenArborInfo.getZc(), 128);
    }

    public void getImageZoomIn() {
        if (checkArborInfoState.zoomIn()) {
            String [] rois = checkArborInfoState.getRois();
            int curRoi = checkArborInfoState.getCurROI();
            String roi = rois[curRoi];
            ArborInfo chosenArborInfo = checkArborInfoState.getChosenArbor();
            imageDataSource.downloadImage(chosenArborInfo.getImageId(), roi, chosenArborInfo.getXc(), chosenArborInfo.getYc(), chosenArborInfo.getZc(), 128);
        }
    }

    public void getImageZoomOut() {
        if (checkArborInfoState.zoomOut()) {
            String [] rois = checkArborInfoState.getRois();
            int curRoi = checkArborInfoState.getCurROI();
            String roi = rois[curRoi];
            ArborInfo chosenArborInfo = checkArborInfoState.getChosenArbor();
            imageDataSource.downloadImage(chosenArborInfo.getImageId(), roi, chosenArborInfo.getXc(), chosenArborInfo.getYc(), chosenArborInfo.getZc(), 128);
        }
    }

    public void getImageWithNewCenter(int [] center) {
        checkArborInfoState.getChosenArbor().setXc(center[0]);
        checkArborInfoState.getChosenArbor().setYc(center[1]);
        checkArborInfoState.getChosenArbor().setZc(center[2]);
        imageDataSource.downloadImage(checkArborInfoState.getChosenArbor().getImageId(), checkArborInfoState.getRois()[checkArborInfoState.getCurROI()],
                checkArborInfoState.getChosenArbor().getXc(), checkArborInfoState.getChosenArbor().getYc(), checkArborInfoState.getChosenArbor().getZc(), 128);
    }


    public void downloadSWC() {
        ArborInfo chosenArbor = checkArborInfoState.getChosenArbor();
        String url = chosenArbor.getUrl() + "/" + chosenArbor.getArborName() + ".eswc";
        int resScale = 1 << checkArborInfoState.getCurROI();
        annotationDataSource.downloadSWC(url, resScale, chosenArbor.getXc(), chosenArbor.getYc(), chosenArbor.getZc(), 128);
    }

    public void getCheckArborList() {
        checkArborDataSource.getCheckArborList(true, 0, 10);
    }

    public void getImageWithArborInfoPos(int position) {
        List<ArborInfo> arborInfoList = checkArborInfoState.getArborInfoList();
        try {
            checkArborInfoState.setChosenArbor((ArborInfo) arborInfoList.get(position).clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        checkArborInfoState.setChosenPos(position);
        getBrainList();
    }

    public void sendCheckYes() {
        ArborInfo chosenArborInfo = checkArborInfoState.getChosenArbor();
        checkDataSource.uploadCheckResult(chosenArborInfo.getArborName(), 0);
    }

    public void sendCheckNo() {
        ArborInfo chosenArborInfo = checkArborInfoState.getChosenArbor();
        checkDataSource.uploadCheckResult(chosenArborInfo.getArborName(), 1);
    }

    public void updateImageResult(Result result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONArray) {
                try {
                    if (((JSONArray) data).length() > 0) {
                        JSONObject jsonObject = ((JSONArray) data).getJSONObject(0);
                        String firstKey = jsonObject.keys().next().toString();
                        if (firstKey.equals("imageid")) {
                            handleBrainListJSON((JSONArray) data);
                        } else {
                            imageResult.setValue(new ResourceResult(false, "JSON error"));
                        }
                    } else {
                        imageResult.setValue(new ResourceResult(false, "No file here"));
                    }
                } catch (JSONException e) {
                    Log.e("uploadUserRatingResultResponse", e.getMessage());
                    imageResult.setValue(new ResourceResult(false, "Fail to parse file list"));
                }
            } else if (data instanceof String){
                String fileName = FileManager.getFileName((String) data);
                FileType fileType = FileManager.getFileType((String) data);
                imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String >((String) data), fileType);
                imageResult.setValue(new ResourceResult(true));
            }
        } else {
            imageResult.setValue(new ResourceResult(false, result.toString()));
        }
    }

    public void updateAnnotationResult(Result result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof String) {
                String fileName = FileManager.getFileName((String) data);
                FileType fileType = FileManager.getFileType((String) data);
                imageInfoRepository.getBasicFile().setFileInfo(fileName, new FilePath<String >((String) data), fileType);
                annotationResult.setValue(new ResourceResult(true));
            } else {
                annotationResult.setValue(new ResourceResult(false, result.toString()));
            }
        } else {
            annotationResult.setValue(new ResourceResult(false, result.toString()));
        }
    }

    public void updateCheckArborResult(Result result) {
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            if (data instanceof JSONArray) {
                try {
                    handleArborListJSON((JSONArray) data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleArborListJSON(JSONArray data) throws JSONException {
        int length = data.length();
        List<ArborInfo> arborInfoList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String arborName = jsonObject.getString("arborName");
            int xc = jsonObject.getInt("xc");
            int yc = jsonObject.getInt("yc");
            int zc = jsonObject.getInt("zc");
            String imageId = jsonObject.getString("imageId");
            String url = jsonObject.getString("url");
            ArborInfo arborInfo = new ArborInfo(arborName, xc, yc, zc, imageId, url);
            arborInfoList.add(arborInfo);
        }
        checkArborInfoState.setArborInfoList(arborInfoList);
        checkArborInfoState.setArborOpenState(CheckArborInfoState.ArborOpenState.ARBOR_LIST);
    }

    private void handleBrainListJSON(JSONArray data) throws JSONException {
        int length = data.length();
        ArborInfo chosenArborInfo = checkArborInfoState.getChosenArbor();
        String imageId = chosenArborInfo.getImageId();
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            if (imageId.equals(jsonObject.getString("imageid"))) {
                String detail = jsonObject.getString("detail");
                detail = detail.substring(1, detail.length() - 1);
                String [] rois = detail.split(", ");
                for (int j = 0; j < rois.length; j++) {
                    rois[j] = rois[j].substring(1, rois[j].length() - 1);
                }
                checkArborInfoState.setRois(rois);
                checkArborInfoState.setCurROI(0);
                ArborInfo arborInfo = checkArborInfoState.getChosenArbor();
                imageDataSource.downloadImage(arborInfo.getImageId(), rois[checkArborInfoState.getCurROI()], arborInfo.getXc(), arborInfo.getYc(), arborInfo.getZc(), 128);
            }
        }
    }

    public MutableLiveData<ResourceResult> getImageResult() {
        return imageResult;
    }

    public MutableLiveData<ResourceResult> getAnnotationResult() {
        return annotationResult;
    }

    public void getNextArbor() {
        checkArborInfoState.nextArbor();
        getBrainList();
    }

    public void getFormerArbor() {
        checkArborInfoState.formerArbor();
        getBrainList();
    }
}
