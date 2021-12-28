package com.penglab.hi5.core.render.utils;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.util.Log;

import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;

import java.util.Vector;

/**
 * Created by Jackiexing on 12/26/21
 */
public class AnnotationManager {

    private final String TAG = "AnnotationManager";

    private final MarkerList markerList = new MarkerList();
    private final MarkerList loadedMarkerList = new MarkerList();

    private final V_NeuronSWC_list newSwcList = new V_NeuronSWC_list();
    private final V_NeuronSWC_list curSwcList = new V_NeuronSWC_list();
    private final V_NeuronSWC_list loadedSwcList = new V_NeuronSWC_list();

    public MarkerList getMarkerList() {
        return markerList;
    }

    public MarkerList getLoadedMarkerList() {
        return loadedMarkerList;
    }

    public V_NeuronSWC_list getNewSwcList() {
        return newSwcList;
    }

    public V_NeuronSWC_list getCurSwcList() {
        return curSwcList;
    }

    public V_NeuronSWC_list getLoadedSwcList() {
        return loadedSwcList;
    }

    public boolean loadMarkerList(MarkerList markerList) {
        return loadedMarkerList.add(markerList.getMarkers());
    }

    public boolean loadNeuronTree(NeuronTree neuronTree) {
        try {
            Vector<V_NeuronSWC> segments = neuronTree.devideByBranch();
            for (V_NeuronSWC segment: segments){
                loadedSwcList.append(segment);
            }
            return true;
        } catch (Exception e){
            ToastEasy("Fail to divide NeuronTree by branch !");
            e.printStackTrace();
            return false;
        }
    }

    public NeuronTree getNeuronTree(){
        try {
            V_NeuronSWC_list curSwc = curSwcList.clone();
            V_NeuronSWC_list loadedSwc = loadedSwcList.clone();

            curSwc.append(loadedSwc.seg);
            return curSwc.mergeSameNode();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
