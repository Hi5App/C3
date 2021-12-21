package com.penglab.hi5.core.ui.check;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penglab.hi5.data.ImageDataSource;

import java.util.List;

/**
 * Created by Jackiexing on 12/18/21
 */
public class CheckViewModel extends ViewModel {

    private final MutableLiveData<String> brainId = new MutableLiveData<>();
    private final MutableLiveData<String> neuronId = new MutableLiveData<>();
    private final MutableLiveData<String> anoId = new MutableLiveData<>();
    private ImageDataSource imageDataSource;

    public CheckViewModel(ImageDataSource imageDataSource) {
        this.imageDataSource = imageDataSource;


    }

    LiveData<String> getBrainId() {
        return brainId;
    }

    LiveData<String> getNeuronId() {
        return neuronId;
    }

    LiveData<String> getAnoId() {
        return anoId;
    }

    ImageDataSource getImageDataSource() {
        return imageDataSource;
    }

    public void getBrainList(){
        imageDataSource.getBrainList();
    }

    public void updateBrainList(){
        imageDataSource.getBrainList();
    }
}
