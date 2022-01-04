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
    private FileInfoState fileInfoState;
    private CheckArborDataSource checkArborDataSource;
    private CheckArborInfoState checkArborInfoState;

    private MutableLiveData<ResourceResult> imageResult = new MutableLiveData<>();
    private MutableLiveData<ResourceResult> annotationResult = new MutableLiveData<>();
    private MutableLiveData<ResourceResult> checkArborResult = new MutableLiveData<>();

    private ImageInfoRepository imageInfoRepository;

    public CheckViewModel(ImageDataSource imageDataSource, AnnotationDataSource annotationDataSource, CheckDataSource checkDataSource, FileInfoState fileInfoState, ImageInfoRepository imageInfoRepository) {
        this.imageDataSource = imageDataSource;
        this.annotationDataSource = annotationDataSource;
        this.checkDataSource = checkDataSource;
        this.fileInfoState = fileInfoState;
        this.imageInfoRepository = imageInfoRepository;
        this.checkArborDataSource = new CheckArborDataSource();
        this.checkArborInfoState = new CheckArborInfoState();
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

    public void getNeuronListWithBrainInfo(BrainInfo brainInfo) {
        fileInfoState.updateWithBrainInfo(brainInfo);
        imageDataSource.getNeuronList(brainInfo.getImageId());
    }

    public void getAnoListWithNeuronInfo(NeuronInfo neuronInfo) {
        fileInfoState.updateWithNeuronInfo(neuronInfo);
        imageDataSource.getAnoList(neuronInfo.getNeuronId());
    }

    public void getImageWithAnoInfo(AnoInfo anoInfo) {
        fileInfoState.updateWithAnoInfo(anoInfo);
        String [] rois = fileInfoState.getRois();
        String roi = rois[rois.length - 1 - fileInfoState.getCurRoi()];
        imageDataSource.downloadImage(fileInfoState.getImageId(), roi, fileInfoState.getX(), fileInfoState.getY(), fileInfoState.getZ(), 128);
    }

    public void getImageWithROI(String roi) {
        ArborInfo chosenArborInfo = checkArborInfoState.getChosenArbor();
        imageDataSource.downloadImage(chosenArborInfo.getImageId(), roi, chosenArborInfo.getXc(), chosenArborInfo.getYc(), chosenArborInfo.getZc(), 128);
    }

    public void getImageZoomIn() {
        String [] rois = checkArborInfoState.getRois();
        int curRoi = checkArborInfoState.getCurROI();
        if (curRoi < rois.length - 1) {
            checkArborInfoState.setCurROI(curRoi + 1);
            String roi = rois[curRoi + 1];
            ArborInfo chosenArborInfo = checkArborInfoState.getChosenArbor();
            imageDataSource.downloadImage(chosenArborInfo.getImageId(), roi, chosenArborInfo.getXc(), chosenArborInfo.getYc(), chosenArborInfo.getZc(), 128);
        }
    }

    public void getImageZoomOut() {
        String [] rois = checkArborInfoState.getRois();
        int curRoi = checkArborInfoState.getCurROI();
        if (curRoi > 0) {
            checkArborInfoState.setCurROI(curRoi - 1);
            String roi = rois[curRoi - 1];
            ArborInfo chosenArborInfo = checkArborInfoState.getChosenArbor();
            imageDataSource.downloadImage(chosenArborInfo.getImageId(), roi, chosenArborInfo.getXc(), chosenArborInfo.getYc(), chosenArborInfo.getZc(), 128);
        }
    }

    public void getImageWithNewCenter(int [] center) {
        fileInfoState.setX(center[0]);
        fileInfoState.setY(center[1]);
        fileInfoState.setZ(center[2]);
        imageDataSource.downloadImage(fileInfoState.getImageId(), fileInfoState.getRois()[fileInfoState.getCurRoi()], fileInfoState.getX(), fileInfoState.getY(), fileInfoState.getZ(), 128);
    }


    public void downloadSWC() {
        ArborInfo chosenArbor = checkArborInfoState.getChosenArbor();
        String url = chosenArbor.getUrl() + "/" + chosenArbor.getArborName() + ".eswc";
        annotationDataSource.downloadSWC(url, 1 << (checkArborInfoState.getRois().length - checkArborInfoState.getCurROI() - 1), chosenArbor.getXc(), chosenArbor.getYc(), chosenArbor.getZc(), 128);
    }

    public void getCheckArborList() {
        checkArborDataSource.getCheckArborList(true, 0, 10);
    }

    public void getImageWithArborInfo(ArborInfo arborInfo) {
        checkArborInfoState.setChosenArbor(arborInfo);
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
                        } else if (firstKey.equals("somaid")) {
                            handleNeuronListJSON((JSONArray) data);
                        } else if (firstKey.equals("anoname")) {
                            handleAnoListJSON((JSONArray) data);
                        } else {
                            imageResult.setValue(new ResourceResult(false, "JSON error"));
                        }
                    } else {
//                        imageResult.setValue(new ResourceResult(false, "No file here"));


                        String [] rois = fileInfoState.getRois();
                        String roi = rois[rois.length - 1 - fileInfoState.getCurRoi()];
                        imageDataSource.downloadImage(fileInfoState.getImageId(), roi, fileInfoState.getX(), fileInfoState.getY(), fileInfoState.getZ(), 128);
                    }
                } catch (JSONException e) {
                    Log.e("updateImageResult", e.getMessage());
                    imageResult.setValue(new ResourceResult(false, "Fail to parse file list"));
                }
            } else if (data instanceof String){
                Log.e("updateImageResultData", (String) data);
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
                checkArborInfoState.setCurROI(rois.length - 1);
                ArborInfo arborInfo = checkArborInfoState.getChosenArbor();
                imageDataSource.downloadImage(arborInfo.getImageId(), rois[checkArborInfoState.getCurROI()], arborInfo.getXc(), arborInfo.getYc(), arborInfo.getZc(), 128);
            }
        }
//        List<BrainInfo> brainList = new ArrayList<>();
//        for (int i = 0; i < length; i++) {
//            JSONObject jsonObject = data.getJSONObject(i);
//            String imageId = jsonObject.getString("imageid");
//            String detail = jsonObject.getString("detail");
//            detail = detail.substring(1, detail.length() - 1);
//            String [] rois = detail.split(", ");
//            for (int j = 0; j < rois.length; j++) {
//                rois[j] = rois[j].substring(1, rois[j].length() - 1);
//            }
//            String url = jsonObject.getString("url");
//            BrainInfo brainInfo = new BrainInfo(imageId, rois, url);
//            brainList.add(brainInfo);
//        }
//        fileInfoState.setBrainList(brainList);
//        fileInfoState.setCurrentOpenState(FileInfoState.OpenState.BRAIN_LIST);
    }

    private void handleNeuronListJSON(JSONArray data) throws JSONException {
        int length = data.length();
        List<NeuronInfo> neuronList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String somaId = jsonObject.getString("somaid");
            String imageId = jsonObject.getString("imageid");
            String neuronId = jsonObject.getString("neuronid");
            int x = jsonObject.getInt("x");
            int y = jsonObject.getInt("y");
            int z = jsonObject.getInt("z");
            NeuronInfo neuronInfo = new NeuronInfo(somaId, imageId, neuronId, x, y, z);
            neuronList.add(neuronInfo);
        }
        fileInfoState.setNeuronList(neuronList);
        fileInfoState.setCurrentOpenState(FileInfoState.OpenState.NEURON_LIST);
    }

    private void handleAnoListJSON(JSONArray data) throws JSONException{
        int length = data.length();
        List<AnoInfo> anoList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String anoName = jsonObject.getString("anoname");
            String neuronId = jsonObject.getString("neuronid");
            String anoUrl = jsonObject.getString("anourl");
            String apoUrl = jsonObject.getString("apourl");
            String swcUrl = jsonObject.getString("swcurl");
            String owner = jsonObject.getString("owner");
            AnoInfo anoInfo = new AnoInfo(anoName, neuronId, anoUrl, apoUrl, swcUrl, owner);
            anoList.add(anoInfo);
        }
        fileInfoState.setAnoList(anoList);
        fileInfoState.setCurrentOpenState(FileInfoState.OpenState.ANO_LIST);
    }

    public FileInfoState getFileInfoState() {
        return fileInfoState;
    }

    public MutableLiveData<ResourceResult> getImageResult() {
        return imageResult;
    }

    public MutableLiveData<ResourceResult> getAnnotationResult() {
        return annotationResult;
    }
}
