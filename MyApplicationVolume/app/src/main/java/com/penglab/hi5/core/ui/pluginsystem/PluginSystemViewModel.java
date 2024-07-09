package com.penglab.hi5.core.ui.pluginsystem;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.PluginDataSource;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;

import org.json.JSONObject;

public class PluginSystemViewModel extends ViewModel {

    private final UserInfoRepository userInfoRepository;
    private final PluginDataSource pluginDataSource;
    private final ImageInfoRepository imageInfoRepository;

    private boolean isDownloading = false;

    private final MutableLiveData<ResourceResult> imageResult = new MutableLiveData<>();


    private volatile PluginInfo pluginInfo = new PluginInfo();


    PluginInfo getPluginInfo() {
        return pluginInfo;
    }

    public PluginSystemViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, PluginDataSource pluginDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.imageInfoRepository = imageInfoRepository;
        this.pluginDataSource = pluginDataSource;

    }

    public enum ImageMode {
        BIG_DATA, NONE
    }

    private final MutableLiveData<ImageMode> imageMode = new MutableLiveData<>();

    public LiveData<ResourceResult> getPluginImageResult() {
        return imageResult;
    }

    public PluginDataSource getPluginDataSource() {
        return pluginDataSource;
    }

    public void handlePluginResult(JSONObject result) {
        String imagePath = "";
        String imageName = "";
        try {
            JSONObject response = result.getJSONObject("response");
            JSONObject processResult = response.getJSONObject("result");

            imageName = processResult.getString("result_image_name");
            imagePath = processResult.getString("result_image_path");
            Log.e("result_image_path", imagePath);
            Log.e("result_image_name", imageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pluginInfo.setInputImageName(imageName);
        pluginDataSource.getImageFile(imagePath, imageName);
    }

    public void getImageList() {
        pluginDataSource.getImageList();
    }

    public String getCurrentImageId() {
        return pluginInfo.getInputImageName();
    }

    public void getPluginList() {
        pluginDataSource.getPluginList();
    }

    public void handleImageList(String imageNumber) {
        Log.e("imageNumber", imageNumber);
        pluginInfo.setInputImageName(imageNumber);
        pluginDataSource.getImageFile("input", imageNumber);

    }

    public void handlePluginList(String pluginNumber) {
        Log.e("pluginNumber", pluginNumber);
        pluginInfo.setPluginName(pluginNumber);

    }

    public void execMethod() {
        String imageNumber = pluginInfo.getInputImageName();
        String pluginNumber = pluginInfo.getPluginName();

        if (imageNumber == null) {
            Toast.makeText(Myapplication.getContext(), "Please select an image!", Toast.LENGTH_LONG).show();
            pluginDataSource.downloadPluginImageResult.postValue(new Result.Success("Please select an image!"));
            return;
        }
        if (pluginNumber == null) {
            Toast.makeText(Myapplication.getContext(), "Please select a method from method list!", Toast.LENGTH_LONG).show();
            pluginDataSource.downloadPluginImageResult.postValue(new Result.Success("Please select a method from method list!"));
            return;
        }

        pluginDataSource.doPlugin(imageNumber, pluginNumber);
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
                isDownloading = false;
            }
        } else {
            isDownloading = false;
        }
    }

    public void openFile() {
        String filePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image" +
                "/" + pluginInfo.getInputImageName();
        String fileName = pluginInfo.getInputImageName();
        Log.e("pluginsystem+filename:", fileName);
        FileType fileType = FileManager.getFileType(filePath);
        Log.e("pluginsystem+filetype", fileType.toString());
        imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<>(filePath), fileType);
        imageResult.setValue(new ResourceResult(true));
    }

}
