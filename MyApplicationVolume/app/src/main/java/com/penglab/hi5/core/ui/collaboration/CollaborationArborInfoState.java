package com.penglab.hi5.core.ui.collaboration;

import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.core.ui.check.CheckArborInfoState;

public class CollaborationArborInfoState {

    private final MutableLiveData<XYZ> centerLocation = new MutableLiveData<>();

    private static volatile  CollaborationArborInfoState INSTANCE;

    public static CollaborationArborInfoState getInstance(){
        if (INSTANCE == null){
            synchronized (CollaborationArborInfoState.class){
                if (INSTANCE == null){
                    INSTANCE = new CollaborationArborInfoState();
                }
            }
        }
        return INSTANCE;
    }

    public MutableLiveData<XYZ> getCenterLocation() {
        return centerLocation;
    }

    public void setCenterLocation(XYZ centerLocation) {
        this.centerLocation.postValue(centerLocation);
    }
}
