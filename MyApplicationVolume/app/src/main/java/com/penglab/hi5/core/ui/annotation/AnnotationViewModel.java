package com.penglab.hi5.core.ui.annotation;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.core.collaboration.connector.ServerConnector;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.UserDataSource;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jackiexing on 12/09/21
 */
public class AnnotationViewModel extends ViewModel {

    public enum PenColor {
        WHITE, BLACK, RED, BLUE, PURPLE, CYAN, YELLOW, GREEN
    }

    public enum AnnotationMode{
        LOCAL_FILE, BIG_DATA
    }

    private final MutableLiveData<UserInfoView> userInfo = new MutableLiveData<>();
    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private MutableLiveData<EditMode> editMode = new MutableLiveData<>(EditMode.NONE);

    private ImageInfoRepository imageInfoRepository;
    private UserInfoRepository userInfoRepository;
    private UserDataSource userDataSource;
    private ImageDataSource imageDataSource;
    private FileInfoState fileInfoState = new FileInfoState();

    public AnnotationViewModel(ImageInfoRepository imageInfoRepository, UserInfoRepository userInfoRepository,
                               UserDataSource userDataSource, ImageDataSource imageDataSource) {
        this.imageInfoRepository = imageInfoRepository;
        this.userInfoRepository = userInfoRepository;
        this.userDataSource = userDataSource;
        this.imageDataSource = imageDataSource;
    }

    public void openLocalFile(Intent data){
        Uri uri = data.getData();
        String fileName = FileManager.getFileName(uri);
        FileType fileType = FileManager.getFileTypeUri(uri);

        imageInfoRepository.getBasicImage().setFileInfo(fileName, new FilePath<Uri>(uri), fileType);
    }

    public FileInfoState getFileInfoState() {
        return fileInfoState;
    }
}
