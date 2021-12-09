package com.penglab.hi5.core.ui.annotation;

import androidx.lifecycle.MutableLiveData;

/**
 * Created by Jackiexing on 12/09/21
 */
public class AnnotationViewModel {

    private final MutableLiveData<UserInfoView> userInfo = new MutableLiveData<>();
    private final MutableLiveData<Integer> score = new MutableLiveData<>();

}
