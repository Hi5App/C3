package com.penglab.hi5.core.ui.check;

import static com.penglab.hi5.core.MainActivity.Toast_in_Thread_static;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.collaboration.basic.ImageInfo;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.model.img.AnoInfo;
import com.penglab.hi5.data.model.img.BrainInfo;
import com.penglab.hi5.data.model.img.NeuronInfo;

import java.util.List;


public class FileInfoState {
    enum OpenState {
        NONE, BRAIN_LIST, NEURON_LIST, ANO_LIST, FILE
    };

    private static volatile FileInfoState INSTANCE;

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
    private int curRoi;

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

    public static FileInfoState getInstance(){
        if (INSTANCE == null){
            synchronized (ImageInfoRepository.class){
                if (INSTANCE == null){
                    INSTANCE = new FileInfoState();
                }
            }
        }
        return INSTANCE;
    }

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

    public int getCurRoi() {
        return curRoi;
    }

    public void setCurRoi(int curRoi) {
        this.curRoi = curRoi;
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
        if (rois.length > 1) {
            curRoi = 1;
        } else {
            curRoi = 0;
        }
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

    public int [] newCenterWhenNavigateBlockToTargetOffset(int offset_x, int offset_y, int offset_z) {
        String img_size = rois[curRoi - 1].replace("RES(","").replace(")","");

        int img_size_x_i = Integer.parseInt(img_size.split("x")[0]);
        int img_size_y_i = Integer.parseInt(img_size.split("x")[1]);
        int img_size_z_i = Integer.parseInt(img_size.split("x")[2]);

        int offset_x_i = (int) x;
        int offset_y_i = (int) y;
        int offset_z_i = (int) z;
        int size_i     =       128;

        if ( (offset_x_i + offset_x) <= 1 || (offset_x_i + offset_x) >= img_size_x_i - 1){
//            System.out.println("----- You have already reached left boundary!!! -----");
            Toast_in_Thread_static("You have already reached boundary!!!");

        }else {
            offset_x_i += offset_x;
            if (offset_x_i - size_i / 2 <= 0)
                offset_x_i = size_i / 2 + 1;
            else if (offset_x_i + size_i / 2 >= img_size_x_i - 1)
                offset_x_i = img_size_x_i - size_i / 2 - 1;
        }

        if ( (offset_y_i + offset_y) <= 1 || (offset_y_i + offset_y) >= img_size_y_i - 1){
//            System.out.println("----- You have already reached left boundary!!! -----");
            Toast_in_Thread_static("You have already reached boundary!!!");

        }else {
            offset_y_i += offset_y;
            if (offset_y_i - size_i / 2 <= 0)
                offset_y_i = size_i / 2 + 1;
            else if (offset_y_i + size_i / 2 >= img_size_y_i - 1)
                offset_y_i = img_size_y_i - size_i / 2 - 1;
        }

        if ( (offset_z_i + offset_z) <= 1 || (offset_z_i + offset_z) >= img_size_z_i - 1){
//            System.out.println("----- You have already reached left boundary!!! -----");
            Toast_in_Thread_static("You have already reached boundary!!!");

        }else {
            offset_z_i += offset_z;
            if (offset_z_i - size_i / 2 <= 0)
                offset_z_i = size_i / 2 + 1;
            else if (offset_z_i + size_i / 2 >= img_size_z_i - 1)
                offset_z_i = img_size_z_i - size_i / 2 - 1;
        }

        return new int[]{offset_x_i, offset_y_i, offset_z_i};
    }
}
