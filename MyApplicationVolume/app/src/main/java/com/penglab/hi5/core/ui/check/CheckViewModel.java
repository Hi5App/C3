package com.penglab.hi5.core.ui.check;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.data.AnnotationDataSource;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.dataStore.database.Image;
import com.penglab.hi5.data.model.img.AnoInfo;
import com.penglab.hi5.data.model.img.BrainInfo;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.data.model.img.NeuronInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jackiexing on 12/18/21
 */
public class CheckViewModel extends ViewModel {

    private ImageDataSource imageDataSource;
    private AnnotationDataSource annotationDataSource;
    private FileInfoState fileInfoState;

    private MutableLiveData<ImageResult> imageResult = new MutableLiveData<>();

    private ImageInfoRepository imageInfoRepository;

    public CheckViewModel(ImageDataSource imageDataSource, AnnotationDataSource annotationDataSource, FileInfoState fileInfoState, ImageInfoRepository imageInfoRepository) {
        this.imageDataSource = imageDataSource;
        this.annotationDataSource = annotationDataSource;
        this.fileInfoState = fileInfoState;
        this.imageInfoRepository = imageInfoRepository;
    }

    ImageDataSource getImageDataSource() {
        return imageDataSource;
    }

    public AnnotationDataSource getAnnotationDataSource() {
        return annotationDataSource;
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
        imageDataSource.downloadImage(fileInfoState.getImageId(), roi, fileInfoState.getX(), fileInfoState.getY(), fileInfoState.getZ(), 128);
    }

    public void getImageZoomIn() {
        String [] rois = fileInfoState.getRois();
        int curRoi = fileInfoState.getCurRoi();
        if (curRoi > 0) {
            fileInfoState.setCurRoi(curRoi - 1);
            String roi = rois[rois.length - curRoi];
            imageDataSource.downloadImage(fileInfoState.getImageId(), roi, fileInfoState.getX(), fileInfoState.getY(), fileInfoState.getZ(), 128);
        }
    }

    public void getImageZoomOut() {
        String [] rois = fileInfoState.getRois();
        int curRoi = fileInfoState.getCurRoi();
        if (curRoi < rois.length - 1) {
            fileInfoState.setCurRoi(curRoi + 1);
            String roi = rois[rois.length - curRoi - 2];
            imageDataSource.downloadImage(fileInfoState.getImageId(), roi, fileInfoState.getX(), fileInfoState.getY(), fileInfoState.getZ(), 128);
        }
    }

    public void getImageWithNewCenter(int [] center) {
        fileInfoState.setX(center[0]);
        fileInfoState.setY(center[1]);
        fileInfoState.setZ(center[2]);
        imageDataSource.downloadImage(fileInfoState.getImageId(), fileInfoState.getRois()[fileInfoState.getCurRoi()], fileInfoState.getX(), fileInfoState.getY(), fileInfoState.getZ(), 128);
    }


    public void downloadSWC() {
        annotationDataSource.downloadSWC(fileInfoState.getImageId(), fileInfoState.getRois()[fileInfoState.getCurRoi()], fileInfoState.getX(), fileInfoState.getY(), fileInfoState.getZ(), 128);
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
                            imageResult.setValue(new ImageResult(false, "JSON error"));
                        }
                    } else {
                        imageResult.setValue(new ImageResult(false, "No file here"));
                    }
                } catch (JSONException e) {
                    Log.e("updateImageResult", e.getMessage());
                    imageResult.setValue(new ImageResult(false, "Fail to parse file list"));
                }
            } else if (data instanceof String){
                String fileName = FileManager.getFileName((String) data);
                FileType fileType = FileManager.getFileType((String) data);
                imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String >((String) data), fileType);
                imageResult.setValue(new ImageResult(true));
            }
        }
    }

    private void handleBrainListJSON(JSONArray data) throws JSONException {
        int length = data.length();
        List<BrainInfo> brainList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String imageId = jsonObject.getString("imageid");
            JSONArray detail = jsonObject.getJSONArray("detail");
            String [] rois = new String[detail.length()];
            for (int j = 0; j < detail.length(); j++) {
                rois[j] = detail.getString(j);
            }
            String url = jsonObject.getString("url");
            BrainInfo brainInfo = new BrainInfo(imageId, rois, url);
            brainList.add(brainInfo);
        }
        fileInfoState.setBrainList(brainList);
        fileInfoState.setCurrentOpenState(FileInfoState.OpenState.BRAIN_LIST);
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

    public MutableLiveData<ImageResult> getImageResult() {
        return imageResult;
    }

}
