package com.penglab.hi5.core.ui.check;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.data.model.img.AnoInfo;
import com.penglab.hi5.data.model.img.BrainInfo;
import com.penglab.hi5.data.model.img.NeuronInfo;

import java.util.List;


public class FileInfoState {
    enum OpenState {
        NONE, BRAIN_LIST, NEURON_LIST, ANO_LIST, FILE
    };
    MutableLiveData<OpenState> currentOpenState;

    @Nullable
    private List<BrainInfo> brainList;

    @Nullable
    private List<NeuronInfo> neuronList;

    @Nullable
    private List<AnoInfo> anoList;

    @Nullable
    private String imageId;

    @Nullable
    private String [] rois;

    @Nullable
    private String somaId;

    @Nullable
    private String neuronId;

    @Nullable
    private int x;

    @Nullable
    private int y;

    @Nullable
    private int z;

    @Nullable
    private String anoName;

    @Nullable
    private String anoUrl;

    @Nullable
    private String apoUrl;

    @Nullable
    private String swcUrl;

    @Nullable
    private String owner;

    @Nullable
    public List<AnoInfo> getAnoList() {
        return anoList;
    }

    public void setAnoList(@Nullable List<AnoInfo> anoList) {
        this.anoList = anoList;
    }

    public FileInfoState() {
        currentOpenState = new MutableLiveData<>();
        currentOpenState.setValue(OpenState.NONE);
    }

    public OpenState getCurrentOpenState() {
        return currentOpenState.getValue();
    }

    public void setCurrentOpenState(OpenState openState) {
        this.currentOpenState.setValue(openState);
    }

    @Nullable
    public List<BrainInfo> getBrainList() {
        return brainList;
    }

    public void setBrainList(@Nullable List<BrainInfo> brainList) {
        this.brainList = brainList;
    }

    @Nullable
    public List<NeuronInfo> getNeuronList() {
        return neuronList;
    }

    public void setNeuronList(@Nullable List<NeuronInfo> neuronList) {
        this.neuronList = neuronList;
    }

    @Nullable
    public String getImageId() {
        return imageId;
    }

    public void setImageId(@Nullable String imageId) {
        this.imageId = imageId;
    }

    @Nullable
    public String[] getRois() {
        return rois;
    }

    public void setRois(@Nullable String[] rois) {
        this.rois = rois;
    }

    @Nullable
    public String getSomaId() {
        return somaId;
    }

    public void setSomaId(@Nullable String somaId) {
        this.somaId = somaId;
    }

    @Nullable
    public String getNeuronId() {
        return neuronId;
    }

    public void setNeuronId(@Nullable String neuronId) {
        this.neuronId = neuronId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Nullable
    public String getAnoName() {
        return anoName;
    }

    public void setAnoName(@Nullable String anoName) {
        this.anoName = anoName;
    }

    @Nullable
    public String getAnoUrl() {
        return anoUrl;
    }

    public void setAnoUrl(@Nullable String anoUrl) {
        this.anoUrl = anoUrl;
    }

    @Nullable
    public String getApoUrl() {
        return apoUrl;
    }

    public void setApoUrl(@Nullable String apoUrl) {
        this.apoUrl = apoUrl;
    }

    @Nullable
    public String getSwcUrl() {
        return swcUrl;
    }

    public void setSwcUrl(@Nullable String swcUrl) {
        this.swcUrl = swcUrl;
    }

    @Nullable
    public String getOwner() {
        return owner;
    }

    public void setOwner(@Nullable String owner) {
        this.owner = owner;
    }

    public void updateWithBrainInfo(BrainInfo brainInfo) {
        imageId = brainInfo.getImageId();
        rois = brainInfo.getRois();
    }

    public void updateWithNeuronInfo(NeuronInfo neuronInfo) {
        neuronId = neuronInfo.getNeuronId();
        somaId = neuronInfo.getSomaId();
        imageId = neuronInfo.getImageId();
        x = neuronInfo.getX();
        y = neuronInfo.getY();
        z = neuronInfo.getZ();
    }

    public void updateWithAnoInfo(AnoInfo anoInfo) {
        anoName = anoInfo.getAnoName();
        neuronId = anoInfo.getNeuronId();
        anoUrl = anoInfo.getAnoUrl();
        apoUrl = anoInfo.getApoUrl();
        swcUrl = anoInfo.getSwcUrl();
        owner = anoInfo.getOwner();
    }
}
