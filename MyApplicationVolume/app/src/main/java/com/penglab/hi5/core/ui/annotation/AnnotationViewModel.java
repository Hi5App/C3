package com.penglab.hi5.core.ui.annotation;

import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;

/**
 * Created by Jackiexing on 12/09/21
 */
public class AnnotationViewModel extends ViewModel {

    public enum PenColor {
        WHITE, BLACK, RED, BLUE, PURPLE, CYAN, YELLOW, GREEN
    }

    public enum AnnotationMode{
        LOCAL_FILE, BIG_DATA, NONE
    }

    private final MutableLiveData<UserInfoView> userInfo = new MutableLiveData<>();
    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private final MutableLiveData<EditMode> editMode = new MutableLiveData<>();
    private final MutableLiveData<AnnotationMode> annotationMode = new MutableLiveData<>();
    private final MutableLiveData<WorkStatus> workStatus = new MutableLiveData<>();

    private ImageInfoRepository imageInfoRepository;
    private UserInfoRepository userInfoRepository;
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

    public void openLocalFile(Intent data){
        Uri uri = data.getData();
        String fileName = FileManager.getFileName(uri);
        FileType fileType = FileManager.getFileTypeUri(uri);

        imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<Uri>(uri), fileType);
        annotationMode.setValue(AnnotationMode.LOCAL_FILE);
        workStatus.setValue(WorkStatus.OPEN_FILE);
    }

    public void loadLocalFile(Intent data){
        Uri uri = data.getData();
        String fileName = FileManager.getFileName(uri);
        FileType fileType = FileManager.getFileTypeUri(uri);

        imageInfoRepository.getBasicFile().setFileInfo(fileName, new FilePath<Uri>(uri), fileType);
        workStatus.setValue(WorkStatus.LOAD_ANNOTATION_FILE);
    }

}
