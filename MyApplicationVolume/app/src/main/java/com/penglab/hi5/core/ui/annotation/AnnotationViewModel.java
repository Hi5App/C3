package com.penglab.hi5.core.ui.annotation;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.feature_calc_func.MorphologyCalculate;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.data.model.img.FileTypeHelper;

import java.util.List;

/**
 * Created by Jackiexing on 12/09/21
 */
public class AnnotationViewModel extends ViewModel {

    public enum PenColor {
        WHITE, BLACK, RED, BLUE, PURPLE, CYAN, YELLOW, GREEN
    }

    public enum AnnotationMode{
        LOCAL_FILE_EDITABLE, LOCAL_FILE_UNEDITABLE, BIG_DATA, NONE
    }

    private final MutableLiveData<UserInfoView> userInfo = new MutableLiveData<>();
    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private final MutableLiveData<EditMode> editMode = new MutableLiveData<>();
    private final MutableLiveData<AnnotationMode> annotationMode = new MutableLiveData<>();
    private final MutableLiveData<WorkStatus> workStatus = new MutableLiveData<>();
    private final MutableLiveData<List<double[]>> analyzeSwcResults = new MutableLiveData<>();
    private final MutableLiveData<FilePath<?>> screenCaptureFile = new MutableLiveData<>();

    private final ImageInfoRepository imageInfoRepository;
    private final UserInfoRepository userInfoRepository;
    private UserDataSource userDataSource;
    private ImageDataSource imageDataSource;

    public AnnotationViewModel(ImageInfoRepository imageInfoRepository, UserInfoRepository userInfoRepository,
                               UserDataSource userDataSource, ImageDataSource imageDataSource) {
        this.imageInfoRepository = imageInfoRepository;
        this.userInfoRepository = userInfoRepository;
        this.userDataSource = userDataSource;
        this.imageDataSource = imageDataSource;
    }

    LiveData<EditMode> getEditMode() {
        return editMode;
    }

    LiveData<AnnotationMode> getAnnotationMode() {
        return annotationMode;
    }

    LiveData<WorkStatus> getWorkStatus() {
        return workStatus;
    }

    LiveData<List<double[]>> getAnalyzeSwcResults() {
        return analyzeSwcResults;
    }

    LiveData<FilePath<?>> getScreenCaptureFile(){
        return screenCaptureFile;
    }

    public void openLocalFile(Intent data){
        Uri uri = data.getData();
        String fileName = FileManager.getFileName(uri);
        FileType fileType = FileManager.getFileTypeUri(uri);

        if (FileTypeHelper.isOpenableType(fileType)){
            imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<Uri>(uri), fileType);
            workStatus.setValue(WorkStatus.OPEN_FILE);
            annotationMode.setValue(FileTypeHelper.isEditableType(fileType) ?
                    AnnotationMode.LOCAL_FILE_EDITABLE : AnnotationMode.LOCAL_FILE_UNEDITABLE);
        } else {
            ToastEasy("Do not support this file !");
        }
    }

    public void loadLocalFile(Intent data){
        Uri uri = data.getData();
        String fileName = FileManager.getFileName(uri);
        FileType fileType = FileManager.getFileTypeUri(uri);

        imageInfoRepository.getBasicFile().setFileInfo(fileName, new FilePath<Uri>(uri), fileType);
        workStatus.setValue(WorkStatus.LOAD_ANNOTATION_FILE);
    }

    public void analyzeSwcFile(Intent data){
        Uri uri = data.getData();
        MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
        List<double[]> features = morphologyCalculate.calculateFromFile(new FilePath<Uri>(uri), false);
        analyzeSwcResults.setValue(features);
    }

    public void analyzeCurTracing(NeuronTree neuronTree){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
                List<double[]> features = morphologyCalculate.calculatefromNT(neuronTree, false);
                analyzeSwcResults.postValue(features);
            }
        }).start();
    }

}
