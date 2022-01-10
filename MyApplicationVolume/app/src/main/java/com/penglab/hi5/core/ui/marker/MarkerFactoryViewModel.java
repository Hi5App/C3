package com.penglab.hi5.core.ui.marker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Jackiexing on 01/10/21
 */
public class MarkerFactoryViewModel extends ViewModel {

    public enum AnnotationMode{
       BIG_DATA, NONE
    }

    private final MutableLiveData<AnnotationMode> annotationMode = new MutableLiveData<>();

    public MarkerFactoryViewModel() {
    }

    public LiveData<AnnotationMode> getAnnotationMode(){
        return annotationMode;
    }

    public void openFile(){
        annotationMode.setValue(AnnotationMode.BIG_DATA);
    }
}
