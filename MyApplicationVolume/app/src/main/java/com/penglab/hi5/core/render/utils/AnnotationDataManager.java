package com.penglab.hi5.core.render.utils;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_unit;
import com.penglab.hi5.basic.image.ImageMarkerExt;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Jackiexing on 12/26/21
 */
public class AnnotationDataManager {

    private final String TAG = "AnnotationManager";

    private MarkerList markerList = new MarkerList();
    private V_NeuronSWC_list curSwcList = new V_NeuronSWC_list();
    private final MarkerList syncMarkerList = new MarkerList();
    private final V_NeuronSWC_list syncSwcList = new V_NeuronSWC_list();
    private int curUndo = -1;

    private final ArrayList<MarkerList> undoMarkerList = new ArrayList<>();
    private final ArrayList<V_NeuronSWC_list> undoCurveList = new ArrayList<>();

    public MarkerList getMarkerList() {
        return markerList;
    }

    public MarkerList getSyncMarkerList() {
        return syncMarkerList;
    }

    public V_NeuronSWC_list getCurSwcList() {
        return curSwcList;
    }

    public V_NeuronSWC_list getSyncSwcList() {
        return syncSwcList;
    }

    public void init(){
        curUndo = 0;
        undoCurveList.clear();
        undoMarkerList.clear();
        undoCurveList.add(new V_NeuronSWC_list());
        undoMarkerList.add(new MarkerList());

        curSwcList.clear();
        syncSwcList.clear();
        markerList.clear();
        syncMarkerList.clear();
    }

    public boolean loadMarkerList(MarkerList newMarkerList) {
        return markerList.add(newMarkerList.getMarkers());
    }

    public boolean loadNeuronTree(NeuronTree neuronTree, boolean isBigData) {
        try {
            Vector<V_NeuronSWC> segments = neuronTree.devideByBranch();
            for (V_NeuronSWC segment: segments){
                if (isBigData){
                    syncSwcList.append(segment);
                }else {
                    curSwcList.append(segment);
                }
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
            V_NeuronSWC_list loadedSwc = syncSwcList.clone();

            curSwc.append(loadedSwc.seg);
            return curSwc.mergeSameNode();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void clearAllTracing(){
        try {
            for (int i = curSwcList.seg.size(); i >= 0; i--){
                curSwcList.deleteSeg(i);
            }
            for (int i = markerList.size()-1; i >= 0; i--){
                markerList.remove(i);
            }
            saveUndo();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void changeAllSwcType(int lastCurveType) throws CloneNotSupportedException {

        Vector<Integer> indexToChangeLineType = new Vector<>();
        Vector<Integer> ChangeLineType = new Vector<>();
        for(int i=0; i<curSwcList.seg.size(); i++){
            V_NeuronSWC seg = curSwcList.seg.get(i);
            indexToChangeLineType.add(i);
            ChangeLineType.add((int) seg.row.get(0).type);
            for(V_NeuronSWC_unit u:seg.row){
                u.type = lastCurveType;
            }
        }

        for(int i = 0; i< syncSwcList.seg.size(); i++){
            V_NeuronSWC seg = syncSwcList.seg.get(i);
            indexToChangeLineType.add(i);
            ChangeLineType.add((int) seg.row.get(0).type);
            for(V_NeuronSWC_unit u:seg.row){
                u.type = lastCurveType;
            }
        }
        saveUndo();
    }

    public void changeAllMarkerType(int lastMarkerType) throws CloneNotSupportedException {

        MarkerList tempMarkerList = markerList.clone();
        V_NeuronSWC_list tempCurveList = curSwcList.clone();

        curUndo += 1;
        undoMarkerList.add(tempMarkerList);
        undoCurveList.add(tempCurveList);

        for (int i = 0; i < markerList.size(); i++){
            markerList.get(i).type = lastMarkerType;
        }
    }

    public boolean deleteFromCur(V_NeuronSWC seg, V_NeuronSWC_list v_neuronSWC_list) throws CloneNotSupportedException {

        if (curUndo > 0){
            int i = undoCurveList.lastIndexOf(v_neuronSWC_list);
            undoCurveList.remove(i);
            undoMarkerList.remove(i);
            curUndo -= 1;
        }

        boolean b = curSwcList.seg.remove(seg);
        saveUndo();
        return b;
    }

    public boolean undo() throws CloneNotSupportedException {
        if (curUndo == 0){
            return false;
        }

        markerList = undoMarkerList.get(curUndo - 1).clone();
        curSwcList = undoCurveList.get(curUndo - 1).clone();
        curUndo -= 1;
        return true;
    }

    public V_NeuronSWC_list saveUndo() throws CloneNotSupportedException {

        MarkerList tempMarkerList = markerList.clone();
        V_NeuronSWC_list tempCurveList = curSwcList.clone();

        for (int i = undoMarkerList.size() - 1; i > curUndo; i--){
            undoMarkerList.remove(i);
            undoCurveList.remove(i);
        }

        curUndo += 1;
        undoMarkerList.add(tempMarkerList);
        undoCurveList.add(tempCurveList);

        return tempCurveList;
    }

    public boolean redo() throws CloneNotSupportedException {
        if (curUndo >= undoMarkerList.size() - 1)
            return false;

        markerList = undoMarkerList.get(curUndo + 1).clone();
        curSwcList = undoCurveList.get(curUndo + 1).clone();
        curUndo += 1;

        return true;
    }

    /**
     * MarkerFactory part
     */
    public MarkerList getMarkerListToAdd() {
        MarkerList startStatus = undoMarkerList.get(0);
        MarkerList endStatus = undoMarkerList.get(curUndo);
        MarkerList markerListToAdd = new MarkerList();
        for (int i=0; i<endStatus.size(); i++){
            ImageMarker marker = endStatus.get(i);
            if (!startStatus.getMarkers().contains(marker)){
                markerListToAdd.add(marker);
            }
        }
        return markerListToAdd;
    }

    public JSONArray getMarkerListToDelete() {
        MarkerList startStatus = undoMarkerList.get(0);
        MarkerList endStatus = undoMarkerList.get(curUndo);
        JSONArray markerListToDelete = new JSONArray();
        for (int i=0; i<startStatus.size(); i++){
            ImageMarkerExt marker = (ImageMarkerExt) startStatus.get(i);
            if (!endStatus.getMarkers().contains(marker)){
                markerListToDelete.put(marker.getId());
            }
        }
        return markerListToDelete;
    }

    public synchronized void syncMarkerList(MarkerList newMarkerList) {
        try {
            markerList.clear();
            markerList.add(newMarkerList.getMarkers());

            curUndo = 0;
            undoMarkerList.clear();
            undoMarkerList.add(markerList.clone());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Collaboration part
     */
    public void syncAddSegSWC(V_NeuronSWC seg){
        syncSwcList.append(seg);
    }

    private boolean deleteSameSegFromList(V_NeuronSWC seg, V_NeuronSWC_list swcList) {
        for (int i = 0; i < swcList.nsegs(); i++){
            V_NeuronSWC cur_seg = swcList.seg.get(i);
            boolean delete = false;
            if (cur_seg.row.size() == seg.row.size()){
                delete = true;
                for (int j = 0; j < seg.row.size(); j++){

                    V_NeuronSWC_unit segUnit_del = seg.row.get(j);
                    V_NeuronSWC_unit segUnit_cur = cur_seg.row.get(j);
                    float[] point_del = new float[]{(float) segUnit_del.x, (float) segUnit_del.y, (float) segUnit_del.z};
                    float[] point_cur = new float[]{(float) segUnit_cur.x, (float) segUnit_cur.y, (float) segUnit_cur.z};

                    if (! (distance(point_del, point_cur)<0.5) ){
                        delete = false;
                        break;
                    }
                }

                if (!delete){
                    delete = true;
                    int segSize = seg.row.size()-1;
                    for (int j = 0; j < seg.row.size(); j++){

                        V_NeuronSWC_unit segUnit_del = seg.row.get(j);
                        V_NeuronSWC_unit segUnit_cur = cur_seg.row.get(segSize - j);
                        float[] point_del = new float[]{(float) segUnit_del.x, (float) segUnit_del.y, (float) segUnit_del.z};
                        float[] point_cur = new float[]{(float) segUnit_cur.x, (float) segUnit_cur.y, (float) segUnit_cur.z};

                        if (! (distance(point_del, point_cur)<0.5) ){
                            delete = false;
                            break;
                        }
                    }
                }

            }

            if (delete){
                Vector<Integer> tobeDelete = new Vector<>();
                tobeDelete.add(i);
                swcList.deleteMutiSeg(tobeDelete);
                return delete;
            }

        }

        return false;
    }

    public void syncDelSegSWC(V_NeuronSWC seg){

        if (deleteSameSegFromList(seg, curSwcList)) {
            for (int i = undoCurveList.size() - 1; i >= 0; i--) {
                V_NeuronSWC_list undoList = undoCurveList.get(i);
                if (!deleteSameSegFromList(seg, undoList)) {
                    break;
                }
            }
        }
        deleteSameSegFromList(seg, syncSwcList);
    }

    private void retypeSameSegFromList(V_NeuronSWC seg, V_NeuronSWC_list list) {
        for (int i = 0; i < list.nsegs(); i++){
            V_NeuronSWC cur_seg = list.seg.get(i);

            boolean retype = false;
            if (cur_seg.row.size() == seg.row.size()){
                retype = true;
                for (int j = 0; j < seg.row.size(); j++){

                    V_NeuronSWC_unit segUnit_del = seg.row.get(j);
                    V_NeuronSWC_unit segUnit_cur = cur_seg.row.get(j);
                    float[] point_del = new float[]{(float) segUnit_del.x, (float) segUnit_del.y, (float) segUnit_del.z};
                    float[] point_cur = new float[]{(float) segUnit_cur.x, (float) segUnit_cur.y, (float) segUnit_cur.z};

                    if (! (distance(point_del, point_cur)<0.5) ){
                        retype = false;
                        break;
                    }
                }
                if (!retype){
                    retype = true;
                    int segSize = seg.row.size()-1;
                    for (int j = 0; j < seg.row.size(); j++){

                        V_NeuronSWC_unit segUnit_del = seg.row.get(j);
                        V_NeuronSWC_unit segUnit_cur = cur_seg.row.get(segSize - j);
                        float[] point_del = new float[]{(float) segUnit_del.x, (float) segUnit_del.y, (float) segUnit_del.z};
                        float[] point_cur = new float[]{(float) segUnit_cur.x, (float) segUnit_cur.y, (float) segUnit_cur.z};

                        if (! (distance(point_del, point_cur)<0.5) ){
                            retype = false;
                            break;
                        }
                    }
                }

            }
            double newType = seg.row.get(0).type;
            if (retype){
                for (int j = 0; j < cur_seg.row.size(); j++){
                    cur_seg.row.get(j).type = newType;
                }
            }
        }
    }

    public void syncRetypeSegSWC(V_NeuronSWC seg){

        retypeSameSegFromList(seg, curSwcList);

        for (int i = 0; i < undoCurveList.size(); i++) {
            retypeSameSegFromList(seg, undoCurveList.get(i));
        }
        retypeSameSegFromList(seg, syncSwcList);
    }

    public void syncAddMarker(ImageMarker imageMarker){
        syncMarkerList.add(imageMarker);
    }

    public void syncDelMarker(ImageMarker imageMarker){
        if (deleteSameMarkerFromList(imageMarker, markerList)) {
            for (int i = undoMarkerList.size() - 1; i >= 0; i--) {
                MarkerList undoList = undoMarkerList.get(i);
                if (!deleteSameMarkerFromList(imageMarker, undoList)) {
                    break;
                }
            }
        }

        deleteSameMarkerFromList(imageMarker, syncMarkerList);
    }

    private boolean deleteSameMarkerFromList(ImageMarker imageMarker, MarkerList list) {
        for (int i = 0 ; i < list.size(); i++){

            ImageMarker marker = list.get(i);
            float[] marker_del = new float[]{imageMarker.x, imageMarker.y, imageMarker.z};
            float[] marker_cur = new float[]{marker.x, marker.y, marker.z};

            if (distance(marker_cur, marker_del) < 0.5){
                list.remove(i);
                return true;
            }
        }
        return false;
    }

    private float distance(float[] x, float[] y){
        int length = x.length;
        float sum = 0;

        for(int i=0; i<length; i++){
            sum += Math.pow(x[i]-y[i], 2);
        }
        return (float) Math.sqrt(sum);
    }

}
