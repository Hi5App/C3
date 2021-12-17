package com.penglab.hi5.core.ui.annotation;

import androidx.lifecycle.MutableLiveData;

public class FileInfoState {
    public boolean isAFile;

    public boolean isBigData;

    public MutableLiveData<String> conPath = new MutableLiveData<>();
    public String [] sonFileList;
    public String fileName = new String();
    public RoomInfoState roomInfoState = new RoomInfoState();

}
