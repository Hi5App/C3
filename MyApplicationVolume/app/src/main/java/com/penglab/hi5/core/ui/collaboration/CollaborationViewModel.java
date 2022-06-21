package com.penglab.hi5.core.ui.collaboration;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.penglab.hi5.core.ui.QualityInspection.QualityInspectionViewModel;
import com.penglab.hi5.data.CollorationDataSource;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.model.user.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CollaborationViewModel extends ViewModel {
    private final String TAG = "CollaborationViewModel";

    public CollaborationViewModel(UserInfoRepository userInfoRepository, ImageInfoRepository imageInfoRepository, ImageDataSource imageDataSource, CollorationDataSource collorationDataSource) {
        this.userInfoRepository = userInfoRepository;
        this.imageInfoRepository = imageInfoRepository;
        this.imageDataSource = imageDataSource;
        this.collorationDataSource = collorationDataSource;
    }



    public enum AnnotationMode{
        BIG_DATA, NONE
    }

    private final UserInfoRepository userInfoRepository;
    private final ImageInfoRepository imageInfoRepository;
    private final ImageDataSource imageDataSource;
    private final CollorationDataSource collorationDataSource;
//
//    private final LoggedInUser loggedInUser;

    public ImageDataSource getImageDataSource() {
        return imageDataSource;
    }
    public boolean isLoggedIn(){
        return userInfoRepository.isLoggedIn();
    }






}
