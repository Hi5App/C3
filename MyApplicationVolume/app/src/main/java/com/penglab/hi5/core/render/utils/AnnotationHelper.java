package com.penglab.hi5.core.render.utils;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.penglab.hi5.basic.ByteTranslate;
import com.penglab.hi5.basic.FastMarching_Linker;
import com.penglab.hi5.basic.LocationSimple;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.basic.tracingfunc.app2.ParaAPP2;
import com.penglab.hi5.basic.tracingfunc.app2.V3dNeuronAPP2Tracing;
import com.penglab.hi5.basic.tracingfunc.gd.CurveTracePara;
import com.penglab.hi5.basic.tracingfunc.gd.V3dNeuronGDTracing;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_unit;
import com.penglab.hi5.core.MyRenderer;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.render.pattern.MyMarker;
import com.penglab.hi5.core.ui.marker.CoordinateConvert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Jackiexing on 12/28/21
 */
public class AnnotationHelper {
    private final String TAG = "AnnotationHelper";
    private final AnnotationDataManager annotationDataManager;
    private final MatrixManager matrixManager;

    private int lastMarkerType = 6;
    private int lastCurveType = 3;

    private float[] normalizedSize = new float[3];
    private int[] originalSize = new int[3];
    private byte[] grayScale;
    private int dataLength;
    private boolean isBig;

    public AnnotationHelper(AnnotationDataManager annotationDataManager, MatrixManager matrixManager) {
        this.annotationDataManager = annotationDataManager;
        this.matrixManager = matrixManager;
    }

    public void initImageInfo(Image4DSimple image4DSimple, float[] normalizedSize, int[] originalSize) {
        this.grayScale = image4DSimple.getData();
        this.dataLength = image4DSimple.getDatatype().ordinal();
        this.isBig = image4DSimple.getIsBig();
        this.normalizedSize = normalizedSize;
        this.originalSize = originalSize;
    }

    public int getLastMarkerType() {
        return lastMarkerType;
    }

    public void setLastMarkerType(int lastMarkerType) {
        this.lastMarkerType = lastMarkerType;
    }

    public int getLastCurveType() {
        return lastCurveType;
    }

    public void setLastCurveType(int lastCurveType) {
        this.lastCurveType = lastCurveType;
    }

    /**
     * AutoTracing algorithm
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean APP2(Image4DSimple img, boolean is2DImage, boolean isBigData) {
        if (img == null || !img.valid()) {
            ToastEasy("Please load image first !");
            return false;
        }

        float imgZ = is2DImage ? Math.max((int) img.getSz0(), (int) img.getSz1()) / 2.0f : 0;
        ArrayList<ImageMarker> markers = annotationDataManager.getMarkerList().getMarkers();

        try {
            ParaAPP2 p = new ParaAPP2();
            p.p4dImage = img;
            p.xc0 = p.yc0 = p.zc0 = 0;
            p.xc1 = (int) p.p4dImage.getSz0() - 1;
            p.yc1 = (int) p.p4dImage.getSz1() - 1;
            p.zc1 = (int) p.p4dImage.getSz2() - 1;
            p.landmarks = new LocationSimple[markers.size()];
            p.bkg_thresh = -1;
            for (int i = 0; i < markers.size(); i++) {
                p.landmarks[i] = is2DImage ? new LocationSimple(markers.get(i).x, markers.get(i).y, 0) :
                        new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
            }

            V3dNeuronAPP2Tracing.proc_app2(p);
            NeuronTree neuronTree = p.resultNt;
            for (int i = 0; i < neuronTree.listNeuron.size(); i++) {
                neuronTree.listNeuron.get(i).type = 4;
                if (is2DImage) {
                    neuronTree.listNeuron.get(i).z = imgZ;
                }
            }

            ToastEasy("APP2 tracing algorithm finished, size of result swc: " + Integer.toString(neuronTree.listNeuron.size()));
            annotationDataManager.loadNeuronTree(neuronTree, isBigData);
            annotationDataManager.saveUndo();
            return true;

        } catch (Exception e) {
            ToastEasy(e.getMessage());
            return false;
        }
    }

    public boolean GD(Image4DSimple img, boolean is2DImage, boolean isBigData) {
        if (img == null || !img.valid()) {
            ToastEasy("Please load image first !");
            return false;
        }

        ArrayList<ImageMarker> markerList = annotationDataManager.getMarkerList().getMarkers();
        if (markerList.size() <= 1) {
            ToastEasy("Please produce at least two markerList !");
            return false;
        }

        try {
            float imgZ = is2DImage ? markerList.get(0).z / 2.0f : 0;
            Vector<LocationSimple> pp = new Vector<LocationSimple>();
            LocationSimple p0 = is2DImage ? new LocationSimple(markerList.get(0).x, markerList.get(0).y, 0) :
                    new LocationSimple(markerList.get(0).x, markerList.get(0).y, markerList.get(0).z);

            for (int i = 1; i < markerList.size(); i++) {
                LocationSimple p = is2DImage ? new LocationSimple(markerList.get(i).x, markerList.get(i).y, 0) :
                        new LocationSimple(markerList.get(i).x, markerList.get(i).y, markerList.get(i).z);
                pp.add(p);
            }

            NeuronTree outSwc = new NeuronTree();
            CurveTracePara curveTracePara = new CurveTracePara();
            outSwc = V3dNeuronGDTracing.v3dneuron_GD_tracing(img, p0, pp, curveTracePara, 1.0);

            for (int i = 0; i < outSwc.listNeuron.size(); i++) {
                outSwc.listNeuron.get(i).type = 5;
                if (is2DImage) outSwc.listNeuron.get(i).z = imgZ;
            }

            ToastEasy("GD-Tracing finished, size of result swc: " + Integer.toString(outSwc.listNeuron.size()));
            annotationDataManager.loadNeuronTree(outSwc, isBigData);
            annotationDataManager.saveUndo();
            return true;

        } catch (Exception e) {
            ToastEasy(e.getMessage());
            return false;
        }
    }


    /**
     * Process of navigation in BigData
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public float[] getROICenter(ArrayList<Float> line, boolean isBigData) throws CloneNotSupportedException {
        if (grayScale == null) {
            return null;
        }
        Vector<MyMarker> outswc = solveCurveMarkerListsFM(line);

        if (outswc == null) {
            ToastEasy("Make sure the point is in boundingBox");
            return null;
        }

        int mx, Mx, my, My, mz, Mz;
        float[] centerXYZ = new float[3];
        mx = Mx = (int) outswc.get(0).x;
        my = My = (int) outswc.get(0).y;
        mz = Mz = (int) outswc.get(0).z;

        for (int i = 0; i < outswc.size() - 1; i++) {
            MyMarker node_cur = outswc.get(i);
            if (node_cur.x < mx) {
                mx = (int) node_cur.x;
            } else if (node_cur.x > Mx) {
                Mx = (int) node_cur.x;
            }
            if (node_cur.y < my) {
                my = (int) node_cur.y;
            } else if (node_cur.y > My) {
                My = (int) node_cur.y;
            }
            if (node_cur.z < mz) {
                mz = (int) node_cur.z;
            } else if (node_cur.z > Mz) {
                Mz = (int) node_cur.z;
            }
        }
        centerXYZ[0] = (mx + Mx) / 2.0f;
        centerXYZ[1] = (my + My) / 2.0f;
        centerXYZ[2] = (mz + Mz) / 2.0f;
        return centerXYZ;
    }

    public float[] zoomInByPinpoint(float x, float y) {
        return solveMarkerCenter(x, y);
    }


    /**
     * Process of curve
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addCurve(ArrayList<Float> line, V_NeuronSWC background_seg, boolean isBigData) throws CloneNotSupportedException {
        if (grayScale == null) {
            return;
        }
        Vector<MyMarker> outswc = solveCurveMarkerListsFM(line);

        if (outswc == null) {
            ToastEasy("Make sure the point is in boundingBox");
            return;
        }

        ArrayList<Float> lineAdded = new ArrayList<>();
        for (int i = 0; i < outswc.size(); i++) {

            lineAdded.add((float) outswc.get(i).x);
            lineAdded.add((float) outswc.get(i).y);
            lineAdded.add((float) outswc.get(i).z);

        }
        if (lineAdded != null) {
            V_NeuronSWC_list curSwcList = annotationDataManager.getCurSwcList();
            int max_n = curSwcList.maxnoden();
            V_NeuronSWC seg = new V_NeuronSWC();
            for (int i = 0; i < lineAdded.size() / 3; i++) {
                V_NeuronSWC_unit u = new V_NeuronSWC_unit();
                u.n = max_n + i + 1;
                if (i == 0) {
                    u.parent = -1;
                } else
                    u.parent = max_n + i;
                float[] xyz = new float[]{lineAdded.get(i * 3 + 0), lineAdded.get(i * 3 + 1), lineAdded.get(i * 3 + 2)};
                u.x = xyz[0];
                u.y = xyz[1];
                u.z = xyz[2];
                Communicator communicator = Communicator.getInstance();
                XYZ GlobalCoors= communicator.ConvertLocalBlocktoGlobalCroods(u.x, u.y, u.z);
                u.xGlobal = GlobalCoors.x;
                u.yGlobal = GlobalCoors.y;
                u.zGlobal = GlobalCoors.z;
                u.type = lastCurveType;
                seg.append(u);
            }

            try {
                seg.smoothCurve();
            } catch (Exception e) {
                ToastEasy("Fail to smooth the curve !");
                Log.e(TAG, "Exception: " + e.getMessage());
            }

            if (seg.row.size() < 3) {
                return;
            }
            float[] headXYZ = new float[]{(float) seg.row.get(0).x, (float) seg.row.get(0).y, (float) seg.row.get(0).z};
            float[] tailXYZ = new float[]{(float) seg.row.get(seg.row.size() - 1).x,
                    (float) seg.row.get(seg.row.size() - 1).y,
                    (float) seg.row.get(seg.row.size() - 1).z};
            boolean linked = false;
            for (int i = 0; i < curSwcList.seg.size() - 1; i++) {
                V_NeuronSWC s = curSwcList.seg.get(i);

                if (s == background_seg) {
                    continue;
                }

                for (int j = 0; j < s.row.size(); j++) {
                    if (linked)
                        break;
                    V_NeuronSWC_unit node = s.row.get(j);
                    float[] nodeXYZ = new float[]{(float) node.x, (float) node.y, (float) node.z};
                    if (distance(headXYZ, nodeXYZ) < 5) {
                        V_NeuronSWC_unit head = seg.row.get(0);
                        V_NeuronSWC_unit child = seg.row.get(1);
                        head.x = node.x;
                        head.y = node.y;
                        head.z = node.z;
                        head.xGlobal = node.xGlobal;
                        head.yGlobal = node.yGlobal;
                        head.zGlobal = node.zGlobal;
                        head.n = node.n;
                        head.parent = node.parent;
                        child.parent = head.n;
                        linked = true;
                        break;
                    }
                    if (distance(tailXYZ, nodeXYZ) < 5) {
                        seg.reverse();
                        V_NeuronSWC_unit tail = seg.row.get(seg.row.size() - 1);
                        V_NeuronSWC_unit child = seg.row.get(seg.row.size() - 2);
                        tail.x = node.x;
                        tail.y = node.y;
                        tail.z = node.z;
                        tail.xGlobal = node.xGlobal;
                        tail.yGlobal = node.yGlobal;
                        tail.zGlobal = node.zGlobal;
                        tail.n = node.n;
                        tail.parent = node.parent;
                        child.n = tail.n;
                        linked = true;
                        break;
                    }
                }
            }

            curSwcList.append(seg);
            int firstSegID = -1;
            int secondSegID = -1;
            for (int i = 0; i < curSwcList.seg.size(); i++) {
                V_NeuronSWC tmpSeg = curSwcList.seg.get(i);
                for (int j = 0; j < tmpSeg.row.size(); j++) {
                    if (tmpSeg.row.get(j).x == seg.row.get(0).x && tmpSeg.row.get(j).y == seg.row.get(0).y && tmpSeg.row.get(j).z == seg.row.get(0).z) {
                        firstSegID = i;
                    }
                    if (tmpSeg.row.get(j).x == seg.row.get(seg.row.size() - 1).x && tmpSeg.row.get(j).y == seg.row.get(seg.row.size() - 1).y && tmpSeg.row.get(j).z == seg.row.get(seg.row.size() - 1).z) {
                        secondSegID = i;
                    }
                }
            }

            Vector<V_NeuronSWC> connectedSegs = new Vector<>();
            if (firstSegID != -1) {
                connectedSegs.add(curSwcList.seg.get(firstSegID));
            }
            if (secondSegID != -1) {
                connectedSegs.add(curSwcList.seg.get(secondSegID));
            }

            if (isBigData) {
                updateAddSegSWC(seg, connectedSegs);
            }

        } else {
            Log.v(TAG, "Fail to draw curve");
        }
    }

    public void deleteCurve(ArrayList<Float> line, boolean isBigData) throws CloneNotSupportedException {
        if (deleteCurveInSwcList(line, isBigData, annotationDataManager.getCurSwcList()) || deleteCurveInSwcList(line, isBigData, annotationDataManager.getSyncSwcList())) {
            annotationDataManager.saveUndo();
        }
    }

    public void splitCurve(ArrayList<Float> line, boolean isBigData) throws CloneNotSupportedException {
        splitCurveInSwcList(line, isBigData, annotationDataManager.getCurSwcList());
        splitCurveInSwcList(line, isBigData, annotationDataManager.getSyncSwcList());
    }

    public void changeCurveType(ArrayList<Float> line, boolean isBigData) throws CloneNotSupportedException {
        if (changeCurveTypeInSwcList(line, lastCurveType, isBigData, annotationDataManager.getCurSwcList()) || changeCurveTypeInSwcList(line, lastCurveType, isBigData, annotationDataManager.getSyncSwcList())) {
            annotationDataManager.saveUndo();
        }
    }

    public void changeAllCurveType() throws CloneNotSupportedException {
        annotationDataManager.changeAllSwcType(lastCurveType);
    }

    public V_NeuronSWC addBackgroundCurve(ArrayList<Float> line, V_NeuronSWC_list[] v_neuronSWC_list) throws CloneNotSupportedException {
        if (grayScale == null) {
            return null;
        }
        ArrayList<Float> lineAdded;
        float[] lineCurrent = new float[line.size()];

        for (int i = 0; i < line.size(); i++) {
            lineCurrent[i] = line.get(i);
        }

        lineAdded = getCurveDrawed(lineCurrent);
        if (lineAdded != null) {
            V_NeuronSWC_list curSwcList = annotationDataManager.getCurSwcList();
            int max_n = curSwcList.maxnoden();
            V_NeuronSWC seg = new V_NeuronSWC();
            for (int i = 0; i < lineAdded.size() / 3; i++) {
                V_NeuronSWC_unit u = new V_NeuronSWC_unit();
                u.n = max_n + i + 1;
                if (i == 0)
                    u.parent = -1;
                else
                    u.parent = max_n + i;
                float[] xyz = modelToVolume(new float[]{lineAdded.get(i * 3 + 0), lineAdded.get(i * 3 + 1), lineAdded.get(i * 3 + 2)});
                u.x = xyz[0];
                u.y = xyz[1];
                u.z = xyz[2];
                u.type = lastCurveType;
                seg.append(u);
            }
            if (seg.row.size() < 3) {
                return null;
            }
            float[] headXYZ = new float[]{(float) seg.row.get(0).x, (float) seg.row.get(0).y, (float) seg.row.get(0).z};
            float[] tailXYZ = new float[]{(float) seg.row.get(seg.row.size() - 1).x,
                    (float) seg.row.get(seg.row.size() - 1).y,
                    (float) seg.row.get(seg.row.size() - 1).z};
            boolean linked = false;
            for (int i = 0; i < curSwcList.seg.size(); i++) {
                V_NeuronSWC s = curSwcList.seg.get(i);
                for (int j = 0; j < s.row.size(); j++) {
                    if (linked)
                        break;
                    V_NeuronSWC_unit node = s.row.get(j);
                    float[] nodeXYZ = new float[]{(float) node.x, (float) node.y, (float) node.z};
                    if (distance(headXYZ, nodeXYZ) < 5) {
                        V_NeuronSWC_unit head = seg.row.get(0);
                        V_NeuronSWC_unit child = seg.row.get(1);
                        head.x = node.x;
                        head.y = node.y;
                        head.z = node.z;
                        head.n = node.n;
                        head.parent = node.parent;
                        child.parent = head.n;
                        linked = true;
                        break;
                    }
                    if (distance(tailXYZ, nodeXYZ) < 5) {
                        seg.reverse();
                        V_NeuronSWC_unit tail = seg.row.get(seg.row.size() - 1);
                        V_NeuronSWC_unit child = seg.row.get(seg.row.size() - 2);
                        tail.x = node.x;
                        tail.y = node.y;
                        tail.z = node.z;
                        tail.n = node.n;
                        tail.parent = node.parent;
                        child.n = tail.n;
                        linked = true;
                        break;
                    }
                }
            }

            curSwcList.append(seg);
            v_neuronSWC_list[0] = annotationDataManager.saveUndo();
            ;
            return seg;
        } else {
            Log.v(TAG, "Draw background curve");
            return null;
        }
    }

    private ArrayList<Float> getCurveDrawed(float[] line) {
        float head_x = line[0];
        float head_y = line[1];
        ArrayList<Float> result = new ArrayList<Float>();
        float[] head_result = volumeToModel(solveMarkerCenter(head_x, head_y));
        if (head_result == null) {
            return null;
        }
        for (int i = 0; i < 3; i++) {
            result.add(head_result[i]);
        } // 计算第一个点在物体坐标系的位置并保存

        float[] ex_head_result = {head_result[0], head_result[1], head_result[2], 1.0f};
        float[] head_point = new float[4];
        Matrix.multiplyMV(head_point, 0, matrixManager.getFinalMatrix(), 0, ex_head_result, 0);
        float current_z = head_point[2] / head_point[3];

        for (int i = 1; i < line.length / 3; i++) {
            float x = line[i * 3];
            float y = line[i * 3 + 1];
            float[] mid_point = {x, y, current_z, 1.0f};
            float[] front_point = {x, y, -1.0f, 1.0f};
            float[] invertFinalMatrix = new float[16];
            Matrix.invertM(invertFinalMatrix, 0, matrixManager.getFinalMatrix(), 0);

            float[] temp1 = new float[4];
            float[] temp2 = new float[4];
            Matrix.multiplyMV(temp1, 0, invertFinalMatrix, 0, mid_point, 0);
            Matrix.multiplyMV(temp2, 0, invertFinalMatrix, 0, front_point, 0);

            divideByW(temp1);
            divideByW(temp2);

            float[] mid_point_pixel = new float[3];
            float[] front_point_pixel = new float[3];
            mid_point_pixel = modelToVolume(temp1);
            front_point_pixel = modelToVolume(temp2);

            float[] dir = minus(front_point_pixel, mid_point_pixel);
            normalize(dir);

            float[][] dim = new float[3][2];
            for (int j = 0; j < 3; j++) {
                dim[j][0] = 0;
                dim[j][1] = originalSize[j] - 1;
            }

            float value = 0;
            float[] result_pos = new float[3];
            for (int j = 1; j < 30; j++) {
                float[] pos = minus(mid_point_pixel, multiply(dir, (float) (j)));

                if (isInBoundingBox(pos, dim)) {
                    float current_value = sample3D(pos[0], pos[1], pos[2]);
                    if (current_value > value) {
                        value = current_value;
                        result_pos[0] = pos[0];
                        result_pos[1] = pos[1];
                        result_pos[2] = pos[2];
                    }
                } else {
                    break;
                }
            }
            for (int j = 1; j < 30; j++) {
                float[] pos = plus(mid_point_pixel, multiply(dir, (float) (j)));

                if (isInBoundingBox(pos, dim)) {
                    float current_value = sample3D(pos[0], pos[1], pos[2]);
                    if (current_value > value) {
                        value = current_value;
                        result_pos[0] = pos[0];
                        result_pos[1] = pos[1];
                        result_pos[2] = pos[2];
                    }
                } else {
                    break;
                }
            }
            if (value == 0) {
                break;
            }

            result_pos = volumeToModel(result_pos);

            for (int j = 0; j < 3; j++) {
                result.add(result_pos[j]);
            }

            float[] ex_result_pos = {result_pos[0], result_pos[1], result_pos[2], 1.0f};
            float[] current_pos = new float[4];
            Matrix.multiplyMV(current_pos, 0, matrixManager.getFinalMatrix(), 0, ex_result_pos, 0);
            current_z = current_pos[2] / current_pos[3];
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Vector<MyMarker> solveCurveMarkerListsFM(ArrayList<Float> listCurvePos) {
        Vector<MyMarker> outswc = new Vector<MyMarker>();
        if (listCurvePos.isEmpty()) {
            Log.v(TAG, "You enter an empty curve for solveCurveMarkerLists_fm(). Check your code.\n");
            return null;
        }

        int szx = originalSize[0];
        int szy = originalSize[1];
        int szz = originalSize[2];

        XYZ sub_orig;
        double[] psubdata;
        int sub_szx, sub_szy, sub_szz;

        Vector<MyMarker> nearpos_vec = new Vector<MyMarker>();
        Vector<MyMarker> farpos_vec = new Vector<MyMarker>();
        nearpos_vec.clear();
        farpos_vec.clear();

        int N = listCurvePos.size() / 3;
        int firstPointIndex = 0;

        Vector<Integer> inds = new Vector<>();
        inds = resampleCurveStroke(listCurvePos);

        for (firstPointIndex = 0; firstPointIndex < N; firstPointIndex++) {
            float[] loc_near = new float[3];
            float[] loc_far = new float[3];
            float[] cur_pos = {listCurvePos.get(firstPointIndex * 3), listCurvePos.get(firstPointIndex * 3 + 1), listCurvePos.get(firstPointIndex * 3 + 2)};
            get_NearFar_Marker_2(cur_pos[0], cur_pos[1], loc_near, loc_far);
            if (make_Point_near_2(loc_near, loc_far)) {

                float[] loc_near_volume = modelToVolume(loc_near);
                float[] loc_far_volume = modelToVolume(loc_far);
                nearpos_vec.add(new MyMarker(loc_near_volume[0], loc_near_volume[1], loc_near_volume[2]));
                farpos_vec.add(new MyMarker(loc_far_volume[0], loc_far_volume[1], loc_far_volume[2]));

                break;
            } else {
                continue;
            }
        }

        int last_i;
        for (int i = firstPointIndex; i < N; i++) {
            boolean b_inds = false;

            if (inds.isEmpty()) {
                b_inds = true;
            } else {
                if (inds.contains(i))
                    b_inds = true;
            }

            // only process resampled strokes
            if (i == 1 || i == (N - 1) || b_inds) { // make sure to include the last N-1 pos
                float[] cur_pos = {listCurvePos.get(i * 3), listCurvePos.get(i * 3 + 1), listCurvePos.get(i * 3 + 2)};
                float[] loc_near = new float[3];
                float[] loc_far = new float[3];
                get_NearFar_Marker_2(cur_pos[0], cur_pos[1], loc_near, loc_far);
                if (make_Point_near_2(loc_near, loc_far)) {

                    float[] loc_near_volume = modelToVolume(loc_near);
                    float[] loc_far_volume = modelToVolume(loc_far);
                    nearpos_vec.add(new MyMarker(loc_near_volume[0], loc_near_volume[1], loc_near_volume[2]));
                    farpos_vec.add(new MyMarker(loc_far_volume[0], loc_far_volume[1], loc_far_volume[2]));
                }
            }
        }
        outswc = FastMarching_Linker.fastmarching_drawing_serialboxes(nearpos_vec, farpos_vec, grayScale, outswc, szx, szy, szz, 1, 5, false, dataLength, isBig);
        return outswc;
    }

    private Vector<Integer> resampleCurveStroke(ArrayList<Float> listCurvePos) {
        Vector<Integer> ids = new Vector<>();
        int N = listCurvePos.size() / 3;
        Vector<Double> maxval = new Vector<>();
        maxval.clear();

        for (int i = 0; i < N; i++) {
            float[] curPos = {listCurvePos.get(i * 3), listCurvePos.get(i * 3 + 1), listCurvePos.get(i * 3 + 2)};
            float[] nearPos = new float[3];
            float[] farPos = new float[3];
            get_NearFar_Marker_2(curPos[0], curPos[1], nearPos, farPos);
            if (make_Point_near(nearPos, farPos)) {
                float[] centerPos = getCenterOfLineProfile(nearPos, farPos);
                double value = sample3D(centerPos[0], centerPos[1], centerPos[2]);
                maxval.add(value);
            }
        }

        Map<Double, Integer> max_score = new HashMap<>();
        for (int i = 0; i < maxval.size(); i++) {
            max_score.put(maxval.get(i), i);
        }

        for (int val : max_score.values()) {
            ids.add(val);
        }
        return ids;
    }

    public boolean deleteFromCur(V_NeuronSWC seg, V_NeuronSWC_list v_neuronSWC_list) throws CloneNotSupportedException {
        return annotationDataManager.deleteFromCur(seg, v_neuronSWC_list);
    }

    public boolean deleteCurveInSwcList(ArrayList<Float> line, boolean isBigData, V_NeuronSWC_list swcList) throws CloneNotSupportedException {

        Vector<Integer> indexToBeDeleted = new Vector<>();
        for (int i = 0; i < line.size() / 3 - 1; i++) {
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(i * 3 + 3);
            float y2 = line.get(i * 3 + 4);
            for (int j = 0; j < swcList.nsegs(); j++) {

                V_NeuronSWC seg = swcList.seg.get(j);
                if (seg.to_be_deleted)
                    continue;
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                for (int k = 0; k < seg.row.size(); k++) {
                    if (seg.row.get(k).parent != -1 && seg.getIndexofParent(k) != -1) {
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(k));
                        swcUnitMap.put(k, parent);
                    }
                }
                for (int k = 0; k < seg.row.size(); k++) {
                    V_NeuronSWC_unit child = seg.row.get(k);
                    int parentid = (int) child.parent;
                    if (parentid == -1 || seg.getIndexofParent(k) == -1) {
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(k);
                    float[] pchild = {(float) child.x, (float) child.y, (float) child.z};
                    float[] pparent = {(float) parent.x, (float) parent.y, (float) parent.z};
                    float[] pchildm = volumeToModel(pchild);
                    float[] pparentm = volumeToModel(pparent);
                    float[] p2 = {pchildm[0], pchildm[1], pchildm[2], 1.0f};
                    float[] p1 = {pparentm[0], pparentm[1], pparentm[2], 1.0f};

                    float[] p1Volumne = new float[4];
                    float[] p2Volumne = new float[4];
                    Matrix.multiplyMV(p1Volumne, 0, matrixManager.getFinalMatrix(), 0, p1, 0);
                    Matrix.multiplyMV(p2Volumne, 0, matrixManager.getFinalMatrix(), 0, p2, 0);
                    divideByW(p1Volumne);
                    divideByW(p2Volumne);
                    float x3 = p1Volumne[0];
                    float y3 = p1Volumne[1];
                    float x4 = p2Volumne[0];
                    float y4 = p2Volumne[1];

                    double m = (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
                    double n = (x2 - x1) * (y4 - y1) - (x4 - x1) * (y2 - y1);
                    double p = (x4 - x3) * (y1 - y3) - (x1 - x3) * (y4 - y3);
                    double q = (x4 - x3) * (y2 - y3) - (x2 - x3) * (y4 - y3);

                    if ((Math.max(x1, x2) >= Math.min(x3, x4))
                            && (Math.max(x3, x4) >= Math.min(x1, x2))
                            && (Math.max(y1, y2) >= Math.min(y3, y4))
                            && (Math.max(y3, y4) >= Math.min(y1, y2))
                            && ((m * n) <= 0) && (p * q <= 0)) {

                        seg.to_be_deleted = true;
                        indexToBeDeleted.add(j);

                        if (isBigData) {
                            updateDelSegSWC(seg);
                        }

                        break;
                    }
                }
            }
        }

        Vector<V_NeuronSWC> toBeDeleted = new Vector<>();
        boolean ifSucceed = false;
        for (int i = 0; i < indexToBeDeleted.size(); i++) {
            ifSucceed = true;
            int index = indexToBeDeleted.get(i);
            toBeDeleted.add(swcList.seg.get(index));
        }

        swcList.deleteMutiSeg(indexToBeDeleted);
        return ifSucceed;
    }

    public void splitCurveInSwcList(ArrayList<Float> line, boolean isBigData, V_NeuronSWC_list swcList) throws CloneNotSupportedException {

        boolean found = false;
        Vector<Integer> toSplit = new Vector<Integer>();
        Log.e("test for split in swc SWCLIST", "" + swcList.nsegs());

        Vector<V_NeuronSWC> segs = new Vector<>();
        for (int i = 0; i < line.size() / 3 - 1; i++) {
            if (found) {
                break;
            }
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(i * 3 + 3);
            float y2 = line.get(i * 3 + 4);
            for (int j = 0; j < swcList.nsegs(); j++) {
                if (found) {
                    break;
                }
                V_NeuronSWC seg = swcList.seg.get(j);
                if (seg.to_be_deleted)
                    continue;
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                for (int k = 0; k < seg.row.size(); k++) {
                    if (seg.row.get(k).parent != -1 && seg.getIndexofParent(k) != -1) {
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(k));
                        swcUnitMap.put(k, parent);
                    }
                }

                for (int k = 0; k < seg.row.size(); k++) {
                    V_NeuronSWC_unit child = seg.row.get(k);
                    int parentid = (int) child.parent;
                    if (parentid == -1 || seg.getIndexofParent(k) == -1) {
//                        System.out.println("parent -1");
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(k);
                    float[] pchild = {(float) child.x, (float) child.y, (float) child.z};
                    float[] pparent = {(float) parent.x, (float) parent.y, (float) parent.z};
                    float[] pchildm = volumeToModel(pchild);
                    float[] pparentm = volumeToModel(pparent);
                    float[] p2 = {pchildm[0], pchildm[1], pchildm[2], 1.0f};
                    float[] p1 = {pparentm[0], pparentm[1], pparentm[2], 1.0f};

                    float[] p1Volumne = new float[4];
                    float[] p2Volumne = new float[4];
                    Matrix.multiplyMV(p1Volumne, 0, matrixManager.getFinalMatrix(), 0, p1, 0);
                    Matrix.multiplyMV(p2Volumne, 0, matrixManager.getFinalMatrix(), 0, p2, 0);
                    divideByW(p1Volumne);
                    divideByW(p2Volumne);
                    float x3 = p1Volumne[0];
                    float y3 = p1Volumne[1];
                    float x4 = p2Volumne[0];
                    float y4 = p2Volumne[1];

                    double m = (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
                    double n = (x2 - x1) * (y4 - y1) - (x4 - x1) * (y2 - y1);
                    double p = (x4 - x3) * (y1 - y3) - (x1 - x3) * (y4 - y3);
                    double q = (x4 - x3) * (y2 - y3) - (x2 - x3) * (y4 - y3);

                    if ((Math.max(x1, x2) >= Math.min(x3, x4))
                            && (Math.max(x3, x4) >= Math.min(x1, x2))
                            && (Math.max(y1, y2) >= Math.min(y3, y4))
                            && (Math.max(y3, y4) >= Math.min(y1, y2))
                            && ((m * n) <= 0) && (p * q <= 0)) {

                        found = true;
                        int cur = k;
                        while (seg.getIndexofParent(cur) != -1) {
                            cur = seg.getIndexofParent(cur);
                            toSplit.add(cur);
                        }

                        V_NeuronSWC newSeg1 = new V_NeuronSWC();
                        V_NeuronSWC newSeg2 = new V_NeuronSWC();
                        int newSegid = swcList.nsegs();
                        V_NeuronSWC_unit first = seg.row.get(k);
                        Log.e("FIRST", "" + seg.row.get(k));
                        try {
                            V_NeuronSWC_unit firstClone2 = first.clone();
                            firstClone2.parent = -1;
                            newSeg2.append(firstClone2);
                        } catch (Exception e) {
                            Log.e(TAG, "splitCurveInSwcList: " + e.getMessage());
                        }
                        for (int w = 0; w < seg.row.size(); w++) {
                            try {
                                V_NeuronSWC_unit temp = seg.row.get(w);
                                if (!toSplit.contains(w) && (w != k)) {
                                    newSeg2.append(temp);
                                } else if (toSplit.contains(w) && (w != k)) {
                                    temp.seg_id = newSegid;
                                    newSeg1.append(temp);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "splitCurveInSwcList: " + e.getMessage());
                            }
                        }
                        try {
                            V_NeuronSWC_unit firstClone = first.clone();
                            newSeg1.append(firstClone);
                        } catch (Exception e) {
                            Log.e(TAG, "splitCurveInSwcList: " + e.getMessage());
                        }

                        segs.add(swcList.seg.get(j));
                        segs.add(newSeg1);
                        segs.add(newSeg2);

                        if (isBigData) {
                            updateSplitSegSWC(segs);
                        }

                        swcList.deleteSeg(j);
                        swcList.append(newSeg1);
                        swcList.append(newSeg2);

                        annotationDataManager.saveUndo();
                        break;
                    }
                }
            }
        }
        swcList.deleteMutiSeg(new Vector<Integer>());
    }

    public boolean changeCurveTypeInSwcList(ArrayList<Float> line, int type, boolean isBigData, V_NeuronSWC_list list) throws CloneNotSupportedException {

        Vector<Integer> indexToChangeLineType = new Vector<>();
        Vector<Integer> ChangeLineType = new Vector<>();
        for (int i = 0; i < line.size() / 3 - 1; i++) {
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(i * 3 + 3);
            float y2 = line.get(i * 3 + 4);
            for (int j = 0; j < list.nsegs(); j++) {
                V_NeuronSWC seg = list.seg.get(j);
                if (seg.to_be_deleted)
                    continue;
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                for (int k = 0; k < seg.row.size(); k++) {
                    if (seg.row.get(k).parent != -1 && seg.getIndexofParent(k) != -1) {
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(k));
                        swcUnitMap.put(k, parent);
                    }
                }
                for (int k = 0; k < seg.row.size(); k++) {
                    V_NeuronSWC_unit child = seg.row.get(k);
                    int parentid = (int) child.parent;
                    if (parentid == -1 || seg.getIndexofParent(k) == -1) {
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(k);
                    float[] pchild = {(float) child.x, (float) child.y, (float) child.z};
                    float[] pparent = {(float) parent.x, (float) parent.y, (float) parent.z};
                    float[] pchildm = volumeToModel(pchild);
                    float[] pparentm = volumeToModel(pparent);
                    float[] p2 = {pchildm[0], pchildm[1], pchildm[2], 1.0f};
                    float[] p1 = {pparentm[0], pparentm[1], pparentm[2], 1.0f};

                    float[] p1Volumne = new float[4];
                    float[] p2Volumne = new float[4];
                    Matrix.multiplyMV(p1Volumne, 0, matrixManager.getFinalMatrix(), 0, p1, 0);
                    Matrix.multiplyMV(p2Volumne, 0, matrixManager.getFinalMatrix(), 0, p2, 0);
                    divideByW(p1Volumne);
                    divideByW(p2Volumne);
                    float x3 = p1Volumne[0];
                    float y3 = p1Volumne[1];
                    float x4 = p2Volumne[0];
                    float y4 = p2Volumne[1];

                    double m = (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
                    double n = (x2 - x1) * (y4 - y1) - (x4 - x1) * (y2 - y1);
                    double p = (x4 - x3) * (y1 - y3) - (x1 - x3) * (y4 - y3);
                    double q = (x4 - x3) * (y2 - y3) - (x2 - x3) * (y4 - y3);

                    if ((Math.max(x1, x2) >= Math.min(x3, x4))
                            && (Math.max(x3, x4) >= Math.min(x1, x2))
                            && (Math.max(y1, y2) >= Math.min(y3, y4))
                            && (Math.max(y3, y4) >= Math.min(y1, y2))
                            && ((m * n) <= 0) && (p * q <= 0)) {
//                        System.out.println("------------------this is delete---------------");
                        seg.to_be_deleted = true;
                        indexToChangeLineType.add(j);
                        ChangeLineType.add((int) seg.row.get(0).type);
                        break;
                    }
                }
            }
        }

        boolean ifSucceed = false;
        for (V_NeuronSWC seg : list.seg) {
            if (seg.to_be_deleted) {
                for (int i = 0; i < seg.row.size(); i++) {
                    seg.row.get(i).type = type;
                }
                seg.to_be_deleted = false;

                if (isBigData) {
                    updateRetypeSegSWC(seg);
                }
                ifSucceed = true;
            }
        }
        return ifSucceed;
    }


    /**
     * Process of marker
     */
    public void addMarker(float x, float y, boolean isBigData) {
        try {
            float[] markerPosition = solveMarkerCenter(x, y);
            if (markerPosition != null) {
                ImageMarker imageMarker = new ImageMarker(lastMarkerType,
                        markerPosition[0], markerPosition[1], markerPosition[2]);
                imageMarker.color.r=(char)imageMarker.getTypeToColorList()[lastMarkerType][0];
                imageMarker.color.g=(char)imageMarker.getTypeToColorList()[lastMarkerType][1];
                imageMarker.color.b=(char)imageMarker.getTypeToColorList()[lastMarkerType][2];
                annotationDataManager.getMarkerList().add(imageMarker);

                annotationDataManager.saveUndo();

                if (isBigData) {
                    Communicator communicator=Communicator.getInstance();
                    XYZ GlobalCroods = communicator.ConvertLocalBlocktoGlobalCroods(imageMarker.x,imageMarker.y,imageMarker.z);
                    imageMarker.xGlobal = GlobalCroods.x;
                    imageMarker.yGlobal = GlobalCroods.y;
                    imageMarker.zGlobal = GlobalCroods.z;;
                    updateAddMarker(imageMarker);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addMarkerByStroke(ArrayList<Float> line, boolean isBigData) {
        try {
            float[] markerPosition = solveMarkerCenterMaxIntensity(line, isBigData);
            if (markerPosition != null) {
                ImageMarker imageMarker = new ImageMarker(lastMarkerType,
                        markerPosition[0], markerPosition[1], markerPosition[2]);
                imageMarker.color.r=(char)imageMarker.getTypeToColorList()[lastMarkerType][0];
                imageMarker.color.g=(char)imageMarker.getTypeToColorList()[lastMarkerType][1];
                imageMarker.color.b=(char)imageMarker.getTypeToColorList()[lastMarkerType][2];
                annotationDataManager.getMarkerList().add(imageMarker);
                annotationDataManager.saveUndo();
                if (isBigData) {
                    Communicator communicator=Communicator.getInstance();
                    XYZ GlobalCroods = communicator.ConvertLocalBlocktoGlobalCroods(imageMarker.x,imageMarker.y,imageMarker.z);
                    imageMarker.xGlobal = GlobalCroods.x;
                    imageMarker.yGlobal = GlobalCroods.y;
                    imageMarker.zGlobal = GlobalCroods.z;;
                    updateAddMarker(imageMarker);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMarkerInSWC(float x, float y, boolean isBigData) throws CloneNotSupportedException {
        addMarkerInSwc(x, y, isBigData, annotationDataManager.getSyncSwcList());

    }


    public void addMarkerInSwc(float x, float y, boolean isBigData, V_NeuronSWC_list swcList) {
        try {
            double minVaule = 100000.0f;
            float[] center = new float[3];
            int minIndexRow = 0;
            int minIndexColumn = 0;
            for (int j = 0; j < swcList.nsegs(); j++) {
                V_NeuronSWC seg = swcList.seg.get(j);
                for (int k = 0; k < seg.row.size(); k++) {
                    V_NeuronSWC_unit node = seg.row.get(k);
                    float[] pnode = {(float) node.x, (float) node.y, (float) node.z};
                    float[] pnodem = volumeToModel(pnode);
                    float[] p1node = {pnodem[0], pnodem[1], pnodem[2], 1.0f};
                    float[] p1nodeVolumn = new float[4];
                    Matrix.multiplyMV(p1nodeVolumn, 0, matrixManager.getFinalMatrix(), 0, p1node, 0);
                    divideByW(p1nodeVolumn);
                    float x1 = p1nodeVolumn[0];
                    float y1 = p1nodeVolumn[1];
                    double distance = Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y));
                    if (distance <= minVaule) {
                        minVaule = distance;
                        minIndexRow = j;
                        minIndexColumn = k;
                    }
                }
            }
            V_NeuronSWC_unit first = swcList.seg.get(minIndexRow).row.get(minIndexColumn);
            Log.e("first", "" + swcList.seg.get(minIndexRow).row.get(minIndexColumn));
            center[0] = (float) first.x;
            center[1] = (float) first.y;
            center[2] = (float) first.z;

            if (center != null) {
                ImageMarker imageMarker = new ImageMarker(lastMarkerType,
                        center[0], center[1], center[2]);
                imageMarker.color.r=(char)imageMarker.getTypeToColorList()[lastMarkerType][0];
                imageMarker.color.g=(char)imageMarker.getTypeToColorList()[lastMarkerType][1];
                imageMarker.color.b=(char)imageMarker.getTypeToColorList()[lastMarkerType][2];
                annotationDataManager.getMarkerList().add(imageMarker);
                annotationDataManager.saveUndo();
                if (isBigData) {
                    Communicator communicator=Communicator.getInstance();
                    XYZ GlobalCroods = communicator.ConvertLocalBlocktoGlobalCroods(imageMarker.x,imageMarker.y,imageMarker.z);
                    imageMarker.xGlobal = GlobalCroods.x;
                    imageMarker.yGlobal = GlobalCroods.y;
                    imageMarker.zGlobal = GlobalCroods.z;;
                    updateAddMarker(imageMarker);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void deleteMarker(float x, float y, boolean isBigData) throws CloneNotSupportedException {
        if (deleteMarkerInList(x, y, isBigData, annotationDataManager.getMarkerList()) || deleteMarkerInList(x, y, isBigData, annotationDataManager.getSyncMarkerList())) {
            annotationDataManager.saveUndo();
        }
    }

    public void deleteMultiMarkerByStroke(ArrayList<Float> line, boolean isBigData) throws CloneNotSupportedException {
        if (deleteMultiMarkerByStrokeInList(line, isBigData, annotationDataManager.getMarkerList()) || deleteMultiMarkerByStrokeInList(line, isBigData, annotationDataManager.getSyncMarkerList())) {
            annotationDataManager.saveUndo();
        }
    }

    public void changeMarkerType(float x, float y, boolean isBigData) throws CloneNotSupportedException {
        if (changeMarkerTypeInList(x, y, isBigData, annotationDataManager.getMarkerList()) || changeMarkerTypeInList(x, y, isBigData, annotationDataManager.getSyncMarkerList())) {
            annotationDataManager.saveUndo();
        }
    }

    public void changeAllMarkerType() throws CloneNotSupportedException {
        annotationDataManager.changeAllMarkerType(lastMarkerType);
    }

    // delete the marker drawed from the markerlist
    public boolean deleteMarkerInList(float x, float y, boolean isBigData, MarkerList list) throws CloneNotSupportedException {
        for (int i = 0; i < list.size(); i++) {
            ImageMarker tobeDeleted = list.get(i);
            float[] markerModel = volumeToModel(new float[]{tobeDeleted.x, tobeDeleted.y, tobeDeleted.z});
            float[] position = new float[4];
            position[0] = markerModel[0];
            position[1] = markerModel[1];
            position[2] = markerModel[2];
            position[3] = 1.0f;

            float[] positionVolumne = new float[4];
            Matrix.multiplyMV(positionVolumne, 0, matrixManager.getFinalMatrix(), 0, position, 0);
            divideByW(positionVolumne);

            float dx = Math.abs(positionVolumne[0] - x);
            float dy = Math.abs(positionVolumne[1] - y);

            if (dx < 0.08 && dy < 0.08) {
                ImageMarker temp = list.get(i);
                list.remove(i);

                /*
                update delete marker
                 */
                if (isBigData) {
                    updateDelMarker(temp);
                }

                return true;
            }
        }
        return false;
    }

    public boolean changeMarkerTypeInList(float x, float y, boolean isBigData, MarkerList list) throws CloneNotSupportedException {

        for (int i = 0; i < list.size(); i++) {
            ImageMarker tobeDeleted = list.get(i);
            float[] markerModel = volumeToModel(new float[]{tobeDeleted.x, tobeDeleted.y, tobeDeleted.z});
            float[] position = new float[4];
            position[0] = markerModel[0];
            position[1] = markerModel[1];
            position[2] = markerModel[2];
            position[3] = 1.0f;

            float[] positionVolumne = new float[4];
            Matrix.multiplyMV(positionVolumne, 0, matrixManager.getFinalMatrix(), 0, position, 0);
            divideByW(positionVolumne);

            float dx = Math.abs(positionVolumne[0] - x);
            float dy = Math.abs(positionVolumne[1] - y);

            if (dx < 0.08 && dy < 0.08) {
                ImageMarker temp = list.get(i);
                ImageMarker temp_backup = (ImageMarker) list.get(i).clone();
                temp_backup.color.r = (char)temp_backup.getTypeToColorList()[temp_backup.type][0];
                temp_backup.color.g = (char)temp_backup.getTypeToColorList()[temp_backup.type][1];
                temp_backup.color.b = (char)temp_backup.getTypeToColorList()[temp_backup.type][2];
                temp.type = lastMarkerType;
                temp.color.r = (char)temp.getTypeToColorList()[temp.type][0];
                temp.color.g = (char)temp.getTypeToColorList()[temp.type][1];
                temp.color.b = (char)temp.getTypeToColorList()[temp.type][2];

                if (isBigData) {
                    updateRetypeMarker(temp_backup, temp);
                }
                return true;
            }
        }
        return false;
    }

    public boolean deleteMultiMarkerByStrokeInList(ArrayList<Float> line, boolean isBigData, MarkerList list) throws CloneNotSupportedException {
        boolean already = false;
        for (int i = list.size() - 1; i >= 0; i--) {
            ImageMarker tobeDeleted = list.get(i);
            float[] markerModel = volumeToModel(new float[]{tobeDeleted.x, tobeDeleted.y, tobeDeleted.z});
            float[] position = new float[4];
            position[0] = markerModel[0];
            position[1] = markerModel[1];
            position[2] = markerModel[2];
            position[3] = 1.0f;

            float[] positionVolumne = new float[4];
            Matrix.multiplyMV(positionVolumne, 0, matrixManager.getFinalMatrix(), 0, position, 0);
            divideByW(positionVolumne);

            if (pnpoly(line, positionVolumne[0], positionVolumne[1])) {
                if (!already) {
                    already = true;
                }
                list.remove(tobeDeleted);
                if (isBigData) {
                    updateDelMarker(tobeDeleted);
                }
            }
        }
        return already;
    }

    public boolean pnpoly(ArrayList<Float> line, float x, float y) {
        int n = line.size() / 3;
        int i = 0;
        int j = n - 1;
        boolean result = false;
        for (; i < n; j = i++) {
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(j * 3);
            float y2 = line.get(j * 3 + 1);
            if (((y1 > y) != (y2 > y)) && (x < ((x2 - x1) * (y - y1) / (y2 - y1) + x1))) {
                result = !result;
            }
        }
        return result;
    }

    public float[] solveMarkerCenter(float x, float y) {

        float[] loc1 = new float[3];
        float[] loc2 = new float[3];

        get_NearFar_Marker_2(x, y, loc1, loc2);

        float steps = 512;
        float[] step = divideByNum(minus(loc1, loc2), steps);

        if (make_Point_near(loc1, loc2)) {
            return getCenterOfLineProfile(loc1, loc2);
        } else {
            ToastEasy("please make sure the point inside the bounding box");
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private float[] solveMarkerCenterMaxIntensity(ArrayList<Float> line, boolean isBigData) {

        if (grayScale == null) {
            return null;
        }
        Vector<MyMarker> outswc = solveCurveMarkerListsFM(line);
        if (outswc == null) {
            ToastEasy("Make sure pinpoint by one stroke");
            return null;
        }
        float[] center = new float[3];
        float value;
        float max_val = 0.0f;
        for (int i = 0; i < outswc.size() - 1; i++) {
            MyMarker node_cur = outswc.get(i);
            value = sample3D((float) node_cur.x, (float) node_cur.y, (float) node_cur.z);
            if (value > max_val) {
                max_val = value;
                center[0] = (float) node_cur.x;
                center[1] = (float) node_cur.y;
                center[2] = (float) node_cur.z;
            }
        }
        return center;
    }

    // 类似于光线投射，找直线上强度最大的一点
    private float[] getCenterOfLineProfile(float[] loc1, float[] loc2) {

        float[] result = new float[3];
        float[] loc1_index = new float[3];
        float[] loc2_index = new float[3];
        boolean isInBoundingBox = false;

        loc1_index = modelToVolume(loc1);
        loc2_index = modelToVolume(loc2);

        float[] d = minus(loc1_index, loc2_index);
        normalize(d);

        float[][] dim = new float[3][2];
        for (int i = 0; i < 3; i++) {
            dim[i][0] = 0;
            dim[i][1] = originalSize[i] - 1;
        }

        result = divideByNum(plus(loc1_index, loc2_index), 2);
        float max_value = 0f;

        // 判断是不是一个像素
        float length = distance(loc1_index, loc2_index);
        if (length < 0.5)
            return result;

        int nstep = (int) (length + 0.5);
        float one_step = length / nstep;

        float[] poc;
        for (int i = 0; i <= nstep; i++) {
            float value;
            poc = minus(loc1_index, multiply(d, one_step * i));

            if (isInBoundingBox(poc, dim)) {
                value = sample3D(poc[0], poc[1], poc[2]);

                isInBoundingBox = true;
                if (value > max_value) {
                    max_value = value;
                    for (int j = 0; j < 3; j++) {
                        result[j] = poc[j];
                    }
                    isInBoundingBox = true;
                }
            }
        }

        if (!isInBoundingBox) {
            ToastEasy("please make sure the point inside the bounding box");
            return null;
        }

        return result;
    }

    // 用于透视投影中获取近平面和远平面的焦点
    private void get_NearFar_Marker_2(float x, float y, float[] res1, float[] res2) {
        float[] invertFinalMatrix = new float[16];
        Matrix.invertM(invertFinalMatrix, 0, matrixManager.getFinalMatrix(), 0);

        float[] near = new float[4];
        float[] far = new float[4];

        Matrix.multiplyMV(near, 0, invertFinalMatrix, 0, new float[]{x, y, -1, 1}, 0);
        Matrix.multiplyMV(far, 0, invertFinalMatrix, 0, new float[]{x, y, 1, 1}, 0);

        divideByW(near);
        divideByW(far);

        for (int i = 0; i < 3; i++) {
            res1[i] = near[i];
            res2[i] = far[i];
        }
    }

    // 找到靠近boundingBox的两处端点
    private boolean make_Point_near(float[] loc1, float[] loc2) {

        float steps = 512;
        float[] near = loc1;
        float[] far = loc2;
        float[] step = divideByNum(minus(near, far), steps);

        float[][] dim = new float[3][2];
        for (int i = 0; i < 3; i++) {
            dim[i][0] = 0;
            dim[i][1] = normalizedSize[i];
        }

        int num = 0;
        while (num < steps && !isInBoundingBox(near, dim)) {
            near = minus(near, step);
            num++;
        }
        if (num == steps)
            return false;

        while (!isInBoundingBox(far, dim)) {
            far = plus(far, step);
        }

        near = plus(near, step);
        far = minus(far, step);

        for (int i = 0; i < 3; i++) {
            loc1[i] = near[i];
            loc2[i] = far[i];
        }

        return true;
    }

    // 找到靠近boundingBox的两处端点
    private boolean make_Point_near_2(float[] loc1, float[] loc2) {

        float steps = 512;
        float[] near = loc1;
        float[] far = loc2;
        float[] step = divideByNum(minus(near, far), steps);

        float[][] dim = new float[3][2];
        for (int i = 0; i < 3; i++) {
            dim[i][0] = 0;
            dim[i][1] = normalizedSize[i];
        }

        int num = 0;
        while (num < steps && !isInBoundingBox(near, dim)) {
            near = minus(near, step);
            num++;
        }
        if (num == steps)
            return false;


        while (!isInBoundingBox(far, dim)) {
            far = plus(far, step);
        }

        for (int i = 0; i < 3; i++) {
            loc1[i] = near[i];
            loc2[i] = far[i];
        }
        return true;
    }

    private float sample3D(float x, float y, float z) {
        int x0, x1, y0, y1, z0, z1;
        x0 = (int) Math.floor(x);
        x1 = (int) Math.ceil(x);
        y0 = (int) Math.floor(y);
        y1 = (int) Math.ceil(y);
        z0 = (int) Math.floor(z);
        z1 = (int) Math.ceil(z);

        float xf, yf, zf;
        xf = x - x0;
        yf = y - y0;
        zf = z - z0;

        float[][][] is = new float[2][2][2];
        is[0][0][0] = grayData(x0, y0, z0);
        is[0][0][1] = grayData(x0, y0, z1);
        is[0][1][0] = grayData(x0, y1, z0);
        is[0][1][1] = grayData(x0, y1, z1);
        is[1][0][0] = grayData(x1, y0, z0);
        is[1][0][1] = grayData(x1, y0, z1);
        is[1][1][0] = grayData(x1, y1, z0);
        is[1][1][1] = grayData(x1, y1, z1);

        float[][][] sf = new float[2][2][2];
        sf[0][0][0] = (1 - xf) * (1 - yf) * (1 - zf);
        sf[0][0][1] = (1 - xf) * (1 - yf) * (zf);
        sf[0][1][0] = (1 - xf) * (yf) * (1 - zf);
        sf[0][1][1] = (1 - xf) * (yf) * (zf);
        sf[1][0][0] = (xf) * (1 - yf) * (1 - zf);
        sf[1][0][1] = (xf) * (1 - yf) * (zf);
        sf[1][1][0] = (xf) * (yf) * (1 - zf);
        sf[1][1][1] = (xf) * (yf) * (zf);

        float result = 0f;

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    result += is[i][j][k] * sf[i][j][k];

        return result;
    }

    private int grayData(int x, int y, int z) {
        int result = 0;
        if (dataLength == 1) {
            byte b = grayScale[z * originalSize[0] * originalSize[1] + y * originalSize[0] + x];
            result = ByteTranslate.byte1ToInt(b);
        } else if (dataLength == 2) {
            byte[] b = new byte[2];
            b[0] = grayScale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 2];
            b[1] = grayScale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 2 + 1];
            result = ByteTranslate.byte2ToInt(b, isBig);
        } else if (dataLength == 4) {
            byte[] b = new byte[4];
            b[0] = grayScale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 4];
            b[1] = grayScale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 4 + 1];
            b[2] = grayScale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 4 + 2];
            b[3] = grayScale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 4 + 3];
            result = ByteTranslate.byte2ToInt(b, isBig);
        }
        return result;
    }

    /**
     * Process of 2D image
     */
    public void add2DCurve(ArrayList<Float> line) throws CloneNotSupportedException {
        ArrayList<Float> lineAdded = new ArrayList<>();
        for (int i = 0; i < line.size() / 3; i++) {
            float x = line.get(i * 3);
            float y = line.get(i * 3 + 1);

            float[] cur_point = solve2DMarker(x, y);
            if (cur_point == null) {
                if (i == 0) {
                    ToastEasy("Please make sure the point is in the image");
                    return;
                }
                break;
            } else {
                lineAdded.add(cur_point[0]);
                lineAdded.add(cur_point[1]);
                lineAdded.add(cur_point[2]);
            }
        }
        V_NeuronSWC_list curSwcList = annotationDataManager.getCurSwcList();
        if (lineAdded != null) {
            int max_n = curSwcList.maxnoden();
            V_NeuronSWC seg = new V_NeuronSWC();
            for (int i = 0; i < lineAdded.size() / 3; i++) {
                V_NeuronSWC_unit u = new V_NeuronSWC_unit();
                u.n = max_n + i + 1;
                if (i == 0)
                    u.parent = -1;
                else
                    u.parent = max_n + i;
                float[] xyz = new float[]{lineAdded.get(i * 3 + 0), lineAdded.get(i * 3 + 1), lineAdded.get(i * 3 + 2)};
                u.x = xyz[0];
                u.y = xyz[1];
                u.z = xyz[2];
                u.type = lastCurveType;
                seg.append(u);
            }
            if (seg.row.size() < 3) {
                return;
            }
            float[] headXYZ = new float[]{(float) seg.row.get(0).x, (float) seg.row.get(0).y, (float) seg.row.get(0).z};
            float[] tailXYZ = new float[]{(float) seg.row.get(seg.row.size() - 1).x,
                    (float) seg.row.get(seg.row.size() - 1).y,
                    (float) seg.row.get(seg.row.size() - 1).z};
            boolean linked = false;
            for (int i = 0; i < curSwcList.seg.size(); i++) {
                V_NeuronSWC s = curSwcList.seg.get(i);
                for (int j = 0; j < s.row.size(); j++) {
                    if (linked)
                        break;
                    V_NeuronSWC_unit node = s.row.get(j);
                    float[] nodeXYZ = new float[]{(float) node.x, (float) node.y, (float) node.z};
                    if (distance(headXYZ, nodeXYZ) < 5) {
                        V_NeuronSWC_unit head = seg.row.get(0);
                        V_NeuronSWC_unit child = seg.row.get(1);
                        head.x = node.x;
                        head.y = node.y;
                        head.z = node.z;
                        head.n = node.n;
                        head.parent = node.parent;
                        child.parent = head.n;
                        linked = true;
                        break;
                    }
                    if (distance(tailXYZ, nodeXYZ) < 5) {
                        seg.reverse();
                        V_NeuronSWC_unit tail = seg.row.get(seg.row.size() - 1);
                        V_NeuronSWC_unit child = seg.row.get(seg.row.size() - 2);
                        tail.x = node.x;
                        tail.y = node.y;
                        tail.z = node.z;
                        tail.n = node.n;
                        tail.parent = node.parent;
                        child.n = tail.n;
                        linked = true;
                        break;
                    }
                }
            }
            curSwcList.append(seg);
            annotationDataManager.saveUndo();
        } else {
            Log.v(TAG, "null while draw line");
        }
    }

    public void add2DMarker(float x, float y) throws CloneNotSupportedException {
        float[] newMarker = solve2DMarker(x, y);
        if (newMarker == null) {
            ToastEasy("Please make sure the point is in the image");
        } else {
            ImageMarker imageMarker = new ImageMarker(lastMarkerType,
                    newMarker[0], newMarker[1], newMarker[2]);

            annotationDataManager.getMarkerList().add(imageMarker);
            annotationDataManager.saveUndo();
        }
    }

    public float[] solve2DMarker(float x, float y) {
        if (ifIn2DImage(x, y)) {
            float[] result = new float[3];
            float[] invertFinalMatrix = new float[16];
            Matrix.invertM(invertFinalMatrix, 0, matrixManager.getFinalMatrix(), 0);

            for (float i = -1; i < 1; i += 0.005) {
                // calculate the temp result
                float[] temp = new float[4];
                Matrix.multiplyMV(temp, 0, invertFinalMatrix, 0, new float[]{x, y, i, 1}, 0);
                divideByW(temp);
                float dis = Math.abs(temp[2] - normalizedSize[2] / 2);

                if (dis < 0.1) {
                    result = new float[]{temp[0], temp[1], normalizedSize[2] / 2};
                    break;
                }
            }
            result = modelToVolume(result);
            return result;
        }
        return null;
    }

    // 判断是否在2D图像里
    public boolean ifIn2DImage(float x, float y) {
        float[] x1 = new float[]{0, 0, normalizedSize[2] / 2, 1};
        float[] x2 = new float[]{normalizedSize[0], 0, normalizedSize[2] / 2, 1};
        float[] x3 = new float[]{0, normalizedSize[1], normalizedSize[2] / 2, 1};
        float[] x4 = new float[]{normalizedSize[0], normalizedSize[1], normalizedSize[2] / 2, 1};
        float[] x1r = new float[4];
        float[] x2r = new float[4];
        float[] x3r = new float[4];
        float[] x4r = new float[4];

        float[] finalMatrix = matrixManager.getFinalMatrix();
        Matrix.multiplyMV(x1r, 0, finalMatrix, 0, x1, 0);
        Matrix.multiplyMV(x2r, 0, finalMatrix, 0, x2, 0);
        Matrix.multiplyMV(x3r, 0, finalMatrix, 0, x3, 0);
        Matrix.multiplyMV(x4r, 0, finalMatrix, 0, x4, 0);

        divideByW(x1r);
        divideByW(x2r);
        divideByW(x3r);
        divideByW(x4r);

        float signOfTrig = (x2r[0] - x1r[0]) * (x3r[1] - x1r[1]) - (x2r[1] - x1r[1]) * (x3r[0] - x1r[0]);
        float signOfAB = (x2r[0] - x1r[0]) * (y - x1r[1]) - (x2r[1] - x1r[1]) * (x - x1r[0]);
        float signOfCA = (x1r[0] - x3r[0]) * (y - x3r[1]) - (x1r[1] - x3r[1]) * (x - x3r[0]);
        float signOfBC = (x3r[0] - x2r[0]) * (y - x3r[1]) - (x3r[1] - x2r[1]) * (x - x3r[0]);

        boolean d1 = (signOfAB * signOfTrig > 0);
        boolean d2 = (signOfCA * signOfTrig > 0);
        boolean d3 = (signOfBC * signOfTrig > 0);

        boolean b1 = d1 && d2 && d3;

        float signOfTrig2 = (x3r[0] - x2r[0]) * (x4r[1] - x2r[1]) - (x3r[1] - x2r[1]) * (x4r[0] - x2r[0]);
        float signOfCB = (x3r[0] - x2r[0]) * (y - x2r[1]) - (x3r[1] - x2r[1]) * (x - x2r[0]);
        float signOfDB = (x2r[0] - x4r[0]) * (y - x4r[1]) - (x2r[1] - x4r[1]) * (x - x4r[0]);
        float signOfDC = (x4r[0] - x3r[0]) * (y - x4r[1]) - (x4r[1] - x3r[1]) * (x - x4r[0]);

        boolean d4 = (signOfCB * signOfTrig2 > 0);
        boolean d5 = (signOfDB * signOfTrig2 > 0);
        boolean d6 = (signOfDC * signOfTrig2 > 0);
        boolean b2 = d4 && d5 && d6;

        return b1 || b2;
    }


    /**
     * Basic functions
     */
    // 判断是否在图像内部了
    private boolean isInBoundingBox(float[] x, float[][] dim) {
        for (int i = 0; i < x.length; i++) {
            if (x[i] >= dim[i][1] || x[i] <= dim[i][0])
                return false;
        }
        return true;
    }

    public float[] modelToVolume(float[] point) {
        if (point == null) {
            Log.e(TAG, "null array in modeToVolume");
            return null;
        }

        float[] result = new float[3];
        result[0] = (1.0f - point[0] / normalizedSize[0]) * originalSize[0];
        result[1] = (1.0f - point[1] / normalizedSize[1]) * originalSize[1];
        result[2] = point[2] / normalizedSize[2] * originalSize[2];

        return result;
    }

    public float[] volumeToModel(float[] point) {
        if (point == null) {
            Log.e(TAG, "null array in volumeToModel");
            return null;
        }

        float[] result = new float[3];
        result[0] = (originalSize[0] - point[0]) / originalSize[0] * normalizedSize[0];
        result[1] = (originalSize[1] - point[1]) / originalSize[1] * normalizedSize[1];
        result[2] = point[2] / originalSize[2] * normalizedSize[2];

        return result;
    }

    private float distance(float[] x, float[] y) {
        int length = x.length;
        float sum = 0;

        for (int i = 0; i < length; i++) {
            sum += Math.pow(x[i] - y[i], 2);
        }
        return (float) Math.sqrt(sum);
    }

    private void normalize(float[] x) {
        int length = x.length;
        float sum = 0;

        for (int i = 0; i < length; i++)
            sum += Math.pow(x[i], 2);

        for (int i = 0; i < length; i++)
            x[i] = x[i] / (float) Math.sqrt(sum);
    }

    // 减法运算
    private float[] minus(float[] x, float[] y) {
        if (x.length != y.length) {
            Log.e(TAG, "length is not the same when minus!");
            return null;
        }

        float[] result = new float[x.length];
        for (int i = 0; i < x.length; i++)
            result[i] = x[i] - y[i];
        return result;
    }

    // 加法运算
    private float[] plus(float[] x, float[] y) {
        if (x.length != y.length) {
            Log.e(TAG, "length is not the same when plus!");
            return null;
        }

        float[] result = new float[x.length];
        for (int i = 0; i < x.length; i++)
            result[i] = x[i] + y[i];
        return result;
    }

    // 除法运算
    private float[] divideByNum(float[] x, float num) {
        if (Math.abs(num) < 0.000001f) {
            Log.e(TAG, "can not be divided by 0");
        }

        float[] result = new float[x.length];
        for (int i = 0; i < x.length; i++)
            result[i] = x[i] / num;
        return result;
    }

    // 除法运算
    private void divideByW(float[] x) {
        if (Math.abs(x[3]) < 0.000001f) {
            Log.e(TAG, "can not be divided by 0 | w is 0");
            return;
        }

        for (int i = 0; i < 3; i++)
            x[i] = x[i] / x[3];
    }

    // 乘法运算
    private float[] multiply(float[] x, float num) {
        float[] result = new float[x.length];
        for (int i = 0; i < x.length; i++)
            result[i] = x[i] * num;
        return result;
    }

    public ArrayList<ImageMarker> importApo(ArrayList<ArrayList<Float>> apo) {
        annotationDataManager.getSyncMarkerList().clear();
        ArrayList<ArrayList<Float>> localApo = Communicator.getInstance().convertApo(apo);
//        syncMarkerList.clear();

        // ##n,orderinfo,name,comment,z,x,y, pixmax,intensity,sdev,volsize,mass,,,, color_r,color_g,color_b
        ArrayList<ImageMarker> markerListLoaded = new ArrayList<>();

        try {
            for (int i = 0; i < localApo.size(); i++) {
                ArrayList<Float> currentLine = localApo.get(i);
                ArrayList<Float> currentLineGlobal = apo.get(i);

                ImageMarker imageMarker_drawed = new ImageMarker(currentLine.get(5),
                        currentLine.get(6),
                        currentLine.get(4));
                imageMarker_drawed.xGlobal = currentLineGlobal.get(5);
                imageMarker_drawed.yGlobal = currentLineGlobal.get(6);
                imageMarker_drawed.zGlobal = currentLineGlobal.get(4);

                int r = currentLine.get(15).intValue();
                int g = currentLine.get(16).intValue();
                int b = currentLine.get(17).intValue();

                if (r == 255 && g == 255 && b == 255) {
                    imageMarker_drawed.type = 0;

                } else if ((r == 0 && g == 0 && b == 0) || (r == 20 && g == 20 && b == 20)) {
                    imageMarker_drawed.type = 1;

                } else if ((r == 255 && g == 0 && b == 0) || (r == 200 && g == 20 && b == 0)) {
                    imageMarker_drawed.type = 2;

                } else if ((r == 0 && g == 0 && b == 255) || (r == 0 && g == 20 && b == 200)) {
                    imageMarker_drawed.type = 3;

                } else if ((r == 255 && g == 0 && b == 255) || (r == 200 && g == 0 && b == 200)) {
                    imageMarker_drawed.type = 4;

                } else if ((r == 0 && g == 255 && b == 255) || (r == 0 && g == 200 && b == 200)) {
                    imageMarker_drawed.type = 5;

                } else if ((r == 255 && g == 255 && b == 0) || (r == 220 && g == 200 && b == 0)) {
                    imageMarker_drawed.type = 6;

                } else if ((r == 0 && g == 255 && b == 0) || (r == 0 && g == 200 && b == 20)) {
                    imageMarker_drawed.type = 7;

                }

//                System.out.println("ImageType: " + imageMarker_drawed.type);
//                annotationDataManager.getSyncMarkerList().add(imageMarker_drawed);
                annotationDataManager.syncAddMarker(imageMarker_drawed);
                markerListLoaded.add(imageMarker_drawed);
                Log.e(TAG, "18454_apo_x " + imageMarker_drawed.x + " 18454_apo_y " + imageMarker_drawed.y + " 18454_apo_z " + imageMarker_drawed.z + " 18454_apo_type " + imageMarker_drawed.type);
                Log.e(TAG, "18454_apo_x_global " + imageMarker_drawed.xGlobal + " 18454_apo_y_global " + imageMarker_drawed.yGlobal + " 18454_apo_z_global " + imageMarker_drawed.zGlobal + " 18454_apo_type " + imageMarker_drawed.type);

            }

//            System.out.println("Size of : markerListLoaded: " + markerListLoaded.size());

        } catch (Exception e) {
            markerListLoaded.clear();
            e.printStackTrace();
        }

        return markerListLoaded;
    }

    public void convertCoordsForMarker(CoordinateConvert downloadCoordinateConvert) {
        for(int i=0; i<annotationDataManager.getMarkerList().size(); i++){
            XYZ newCood = downloadCoordinateConvert.convertGlobalToLocal(annotationDataManager.getMarkerList().get(i).xGlobal, annotationDataManager.getMarkerList().get(i).yGlobal,
                    annotationDataManager.getMarkerList().get(i).zGlobal);
            annotationDataManager.getMarkerList().get(i).x = newCood.x;
            annotationDataManager.getMarkerList().get(i).y = newCood.y;
            annotationDataManager.getMarkerList().get(i).z = newCood.z;
        }

        for(int i=0; i<annotationDataManager.getSyncMarkerList().size(); i++){
            XYZ newCood = downloadCoordinateConvert.convertGlobalToLocal(annotationDataManager.getSyncMarkerList().get(i).xGlobal, annotationDataManager.getSyncMarkerList().get(i).yGlobal,
                    annotationDataManager.getSyncMarkerList().get(i).zGlobal);
            annotationDataManager.getSyncMarkerList().get(i).x = newCood.x;
            annotationDataManager.getSyncMarkerList().get(i).y = newCood.y;
            annotationDataManager.getSyncMarkerList().get(i).z = newCood.z;
        }
    }

    public void convertCoordsForSWC(CoordinateConvert downloadCoordinateConvert) {
        for(int i=0; i<annotationDataManager.getCurSwcList().seg.size(); i++){
            V_NeuronSWC tmpSeg = annotationDataManager.getCurSwcList().seg.get(i);
            for(int j=0; j<tmpSeg.row.size(); j++){
                V_NeuronSWC_unit u=tmpSeg.row.get(j);
                XYZ newCood = downloadCoordinateConvert.convertGlobalToLocal(u.xGlobal, u.yGlobal, u.zGlobal);
                u.x = newCood.x;
                u.y = newCood.y;
                u.z = newCood.z;
            }
        }

        for(int i=0; i<annotationDataManager.getSyncSwcList().seg.size(); i++){
            V_NeuronSWC tmpSeg = annotationDataManager.getSyncSwcList().seg.get(i);
            for(int j=0; j<tmpSeg.row.size(); j++){
                V_NeuronSWC_unit u=tmpSeg.row.get(j);
                XYZ newCood = downloadCoordinateConvert.convertGlobalToLocal(u.xGlobal, u.yGlobal, u.zGlobal);
                u.x = newCood.x;
                u.y = newCood.y;
                u.z = newCood.z;
            }
        }
    }

    public void importNeuronTree(NeuronTree nt, boolean needSync) {

        Log.e(TAG, "----------------importNeuronTree----------------");
        annotationDataManager.getSyncSwcList().clear();
        try {
            Log.e(TAG, "nt size: " + nt.listNeuron.size());

            Vector<V_NeuronSWC> segs = nt.devideByBranch();
            for (int i = 0; i < segs.size(); i++) {
                annotationDataManager.getSyncSwcList().append(segs.get(i));
                Log.e(TAG, "x: "+segs.get(i).row.get(0).x);
                Log.e(TAG, "x: "+segs.get(i).row.get(0).xGlobal);
                if (needSync) {
                    updateAddSegSWC(segs.get(i), null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        ifLoadSWC = true;
    }


    /**
     * Collaboration part
     */

    public void updateSplitSegSWC(Vector<V_NeuronSWC> segs) {
        Communicator communicator = Communicator.getInstance();
        communicator.updateSplitSegSWC(segs);
    }

    public void updateAddSegSWC(V_NeuronSWC seg, Vector<V_NeuronSWC> connectedSegs) {
        Communicator communicator = Communicator.getInstance();
        communicator.updateAddSegSWC(seg, connectedSegs);
    }

    public void updateDelSegSWC(V_NeuronSWC seg) {
        Communicator communicator = Communicator.getInstance();
        communicator.updateDelSegSWC(seg);
    }

    public void updateAddMarker(ImageMarker marker) {
        Communicator communicator = Communicator.getInstance();
        communicator.updateAddMarkerMsg(marker);
//        annotationDataManager.getSyncMarkerListGlobal().add(marker);
    }

    public void updateDelMarker(ImageMarker marker) {
        Communicator communicator = Communicator.getInstance();
        communicator.updateDelMarkerMsg(marker);
        XYZ GlobalCroods = communicator.ConvertLocalBlocktoGlobalCroods(marker.x,marker.y,marker.z);
        marker.xGlobal=GlobalCroods.x;
        marker.yGlobal=GlobalCroods.y;
        marker.zGlobal=GlobalCroods.z;
//        annotationDataManager.syncDelMarkerGlobal(marker);
    }

    public void updateRetypeMarker(ImageMarker origin_marker, ImageMarker current_marker) {
        Communicator communicator = Communicator.getInstance();
        communicator.updateRetypeMarkerMsg(origin_marker, current_marker);
    }

    public void updateRetypeSegSWC(V_NeuronSWC seg) {
        Communicator communicator = Communicator.getInstance();
        communicator.updateRetypeSegSWC(seg, (int) seg.row.get(0).type);
    }

}
