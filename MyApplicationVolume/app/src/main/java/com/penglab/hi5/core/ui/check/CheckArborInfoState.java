package com.penglab.hi5.core.ui.check;

import static com.penglab.hi5.core.MainActivity.Toast_in_Thread_static;

import androidx.lifecycle.MutableLiveData;

import com.penglab.hi5.data.ImageInfoRepository;
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

    private static volatile CheckArborInfoState INSTANCE;

    private MutableLiveData<ArborOpenState> arborOpenState = new MutableLiveData<>(ArborOpenState.NONE);

    private List<ArborInfo> arborInfoList = new ArrayList<>();

    private ArborInfo chosenArbor;

    private int chosenPos;

    private String [] rois;

    private int curROI;

    public static CheckArborInfoState getInstance(){
        if (INSTANCE == null){
            synchronized (CheckArborInfoState.class){
                if (INSTANCE == null){
                    INSTANCE = new CheckArborInfoState();
                }
            }
        }
        return INSTANCE;
    }

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

    public String[] getRois() {
        return rois;
    }

    public void setRois(String[] rois) {
        this.rois = rois;
    }

    public int getCurROI() {
        return curROI;
    }

    public void setCurROI(int curROI) {
        this.curROI = curROI;
    }

    public void setChosenPos(int chosenPos) {
        this.chosenPos = chosenPos;
    }

    public int getChosenPos() {
        return chosenPos;
    }

    public int [] newCenterWhenNavigateBlockToTargetOffset(int offset_x, int offset_y, int offset_z) {
        String img_size = rois[curROI].replace("RES(","").replace(")","");

        int img_size_x_i = Integer.parseInt(img_size.split("x")[0]);
        int img_size_y_i = Integer.parseInt(img_size.split("x")[1]);
        int img_size_z_i = Integer.parseInt(img_size.split("x")[2]);

        int offset_x_i = (int) chosenArbor.getXc();
        int offset_y_i = (int) chosenArbor.getYc();
        int offset_z_i = (int) chosenArbor.getZc();
        int size_i     =       128;

        if ( (offset_x_i + offset_x) <= 1 || (offset_x_i + offset_x) >= img_size_x_i - 1){
            Toast_in_Thread_static("You have already reached boundary!!!");
        }else {
            offset_x_i += offset_x;
            if (offset_x_i - size_i / 2 <= 0)
                offset_x_i = size_i / 2 + 1;
            else if (offset_x_i + size_i / 2 >= img_size_x_i - 1)
                offset_x_i = img_size_x_i - size_i / 2 - 1;
        }

        if ( (offset_y_i + offset_y) <= 1 || (offset_y_i + offset_y) >= img_size_y_i - 1){
            Toast_in_Thread_static("You have already reached boundary!!!");
        }else {
            offset_y_i += offset_y;
            if (offset_y_i - size_i / 2 <= 0)
                offset_y_i = size_i / 2 + 1;
            else if (offset_y_i + size_i / 2 >= img_size_y_i - 1)
                offset_y_i = img_size_y_i - size_i / 2 - 1;
        }

        if ( (offset_z_i + offset_z) <= 1 || (offset_z_i + offset_z) >= img_size_z_i - 1){
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

    public boolean zoomIn() {
        if (curROI > 0) {
            curROI--;
            chosenArbor.zoomIn();
            return true;
        }
        return false;
    }

    public boolean zoomOut() {
        if (curROI < rois.length - 1) {
            curROI++;
            chosenArbor.zoomOut();
            return true;
        }
        return false;
    }

    public void zoomToROI(int roiPos) {
        chosenArbor.zoomScale(roiPos - curROI);
        curROI = roiPos;
    }

    public void nextArbor() {
        if (chosenPos < arborInfoList.size() - 1) {
            chosenPos++;
        } else {
            chosenPos = 0;
        }
        try {
            chosenArbor = (ArborInfo) arborInfoList.get(chosenPos).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void formerArbor() {
        if (chosenPos > 0) {
            chosenPos--;
        } else {
            chosenPos = arborInfoList.size() - 1;
        }
        try {
            chosenArbor = (ArborInfo) arborInfoList.get(chosenPos).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
