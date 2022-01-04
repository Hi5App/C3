package com.penglab.hi5.core.ui.check;

import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.data.model.img.ArborInfo;

import org.apache.lucene.util.packed.PackedInts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yihang zhu 01/04/21
 */
public class CheckArborInfoState {
    enum ArborOpenState {
        NONE, ARBOR_LIST, ARBOR_URL
    }

    private MutableLiveData<ArborOpenState> arborOpenState = new MutableLiveData<>(ArborOpenState.NONE);

    private List<ArborInfo> arborInfoList = new ArrayList<>();

    private ArborInfo chosenArbor;

    public List<ArborInfo> getArborInfoList() {
        return arborInfoList;
    }

    public void setArborInfoList(List<ArborInfo> arborInfoList) {
        this.arborInfoList = arborInfoList;
    }

    public MutableLiveData<ArborOpenState> getArborOpenState() {
        return arborOpenState;
    }

    public ArborOpenState getOpenState() {
        return arborOpenState.getValue();
    }

    public void setArborOpenState(ArborOpenState openState) {
        arborOpenState.postValue(openState);
    }

    public ArborInfo getChosenArbor() {
        return chosenArbor;
    }

    public void setChosenArbor(ArborInfo chosenArbor) {
        this.chosenArbor = chosenArbor;
    }
}
