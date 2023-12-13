package com.penglab.hi5.core.ui.pluginsystem;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.collaboration.CollaborationViewModel;
import com.penglab.hi5.core.ui.marker.MarkerFactoryViewModel;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.PluginDataSource;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.img.CollaborateNeuronInfo;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.core.ui.pluginsystem.PluginInfo;

import java.util.List;

public class PluginSystemViewModel extends ViewModel {

    private final UserInfoRepository userInfoRepository;
    private final PluginDataSource pluginDataSource;
    private final ImageInfoRepository imageInfoRepository;

    private boolean isDownloading = false;

    private final MutableLiveData<ResourceResult> imageResult = new MutableLiveData<>();


    private volatile PluginInfo pluginInfo = new PluginInfo();



    public PluginSystemViewModel(UserInfoRepository userInfoRepository,ImageInfoRepository imageInfoRepository, PluginDataSource pluginDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.imageInfoRepository = imageInfoRepository;
        this.pluginDataSource = pluginDataSource;

    }

    public enum ImageMode{
        BIG_DATA, NONE
    }
    private final MutableLiveData<ImageMode> imageMode = new MutableLiveData<>();

    public LiveData<ResourceResult> getPluginImageResult() {
        return imageResult;
    }



    public PluginDataSource getPluginDataSource() {
        return pluginDataSource;
    }

    public void getImageList() {
        pluginDataSource.getImageList();
    }

    public String getCurrentImageId(){
        return pluginInfo.getInputImageName();
    }
    public void getPluginList() {
        pluginDataSource.getPluginList();
    }


    public void downloadPluginResult() {

        String image = pluginInfo.getInputImageName();
        String pluginName = pluginInfo.getPluginName();
        Log.e("pluginSytemViewModel_image",image);
        Log.e("pluginSystemViewModel_pluginame",pluginName);
        pluginDataSource.doPlugin(image,pluginName);
    }

    public void handleImageList(String imageNumber) {
        Log.e("imageNumber",imageNumber);
        pluginInfo.setInputImageName(imageNumber);
        pluginDataSource.getOriginImage(imageNumber);

    }
    public void handlePluginList(String pluginNumber) {
        Log.e("pluginNumber",pluginNumber);
        pluginInfo.setPluginName(pluginNumber);

    }

    public void getModelResult(){
        String imageNumber =pluginInfo.getInputImageName();
        pluginDataSource.getModelImage(imageNumber);
    }

    public void handleDownloadImageResult(Result result) {
        if (result == null) {
            isDownloading = false;
        }
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            openFile();
            if (data instanceof String) {
            } else {
            isDownloading = false;}
        } else {
            isDownloading = false;
        }
    }

    public void handelModelImageResult(Result result){
        if (result == null) {
            isDownloading = false;
        }
        if (result instanceof Result.Success) {
            Object data = ((Result.Success<?>) result).getData();
            openFile();
            if (data instanceof String) {
            } else {
                isDownloading = false;}
        } else {
            isDownloading = false;
        }
    }



    public void openFile() {
        String filePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image" +
                "/" + pluginInfo.getInputImageName();
        String fileName = pluginInfo.getInputImageName();
        Log.e("pluginsystem+filename:",fileName);
        FileType fileType = FileManager.getFileType(filePath);
        Log.e("pluginsystem+filetype",fileType.toString());
        imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<String>(filePath), fileType);
        imageResult.setValue(new ResourceResult(true));
    }


    public LiveData<ImageMode> getImageMode(){
        return imageMode;
    }
}
