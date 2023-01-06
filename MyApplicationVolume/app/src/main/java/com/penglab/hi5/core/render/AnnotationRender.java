package com.penglab.hi5.core.render;

import static com.penglab.hi5.core.Myapplication.getContext;
import static javax.microedition.khronos.opengles.GL10.GL_ALPHA_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.provider.MediaStore;
import android.util.Log;

import com.penglab.hi5.basic.MyAnimation;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.image.ImageUtil;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_unit;
import com.penglab.hi5.core.MyRenderer;
import com.penglab.hi5.core.render.pattern.MyAxis;
import com.penglab.hi5.core.render.pattern.MyDraw;
import com.penglab.hi5.core.render.pattern.MyPattern;
import com.penglab.hi5.core.render.pattern.MyPattern2D;
import com.penglab.hi5.core.render.pattern.MyPatternGame;
import com.penglab.hi5.core.render.utils.AnnotationDataManager;
import com.penglab.hi5.core.render.utils.MatrixManager;
import com.penglab.hi5.core.render.utils.RenderOptions;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.agora.rtc.internal.RtcEngineMessage;

/**
 * Created by Jackiexing on 12/20/21
 */
public class AnnotationRender implements GLSurfaceView.Renderer {

    private final String TAG = "AnnotationRender";

    private boolean is2DImage;
    private Image4DSimple image4DSimple;
    private Bitmap bitmap2D;
    private float[] normalizedSize = new float[3];
    private int[] originalSize = new int[3];

    private final MyPattern myPattern = new MyPattern();
    private final MyPattern2D myPattern2D = new MyPattern2D();
    private final MyAxis myAxis = new MyAxis();
    private final MyDraw myDraw = new MyDraw();

    private final RenderOptions renderOptions;
    private final MatrixManager matrixManager;
    private final AnnotationDataManager annotationDataManager;

    private final ExecutorService exeService = Executors.newSingleThreadExecutor();
    private ByteBuffer mCaptureBuffer;
    private int screenWidth;
    private int screenHeight;
    private float[] fingerTrajectory;
    private boolean ifShowSWC = true;
    private String imageInfo;



    public AnnotationRender(AnnotationDataManager annotationDataManager, MatrixManager matrixManager, RenderOptions renderOptions){
        this.annotationDataManager = annotationDataManager;
        this.matrixManager = matrixManager;
        this.renderOptions = renderOptions;
    }

    @Override
    public void
    onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(121f/255f, 134f/255f, 203f/255f, 1.0f);

        // init shader program
        MyPattern.initProgram();
        MyPattern2D.initProgram();
        MyAxis.initProgram();
        MyDraw.initProgram();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int screenWidth, int screenHeight) {
        GLES30.glViewport(0, 0, screenWidth, screenHeight);

        boolean surfaceChanged = (screenWidth != this.screenWidth || screenHeight != this.screenHeight);

        // this projection matrix is applied to object coordinates
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        matrixManager.setProjectionMatrix(screenWidth, screenHeight);
        matrixManager.initMatrixByFile(normalizedSize, renderOptions.getScale(), false);
        mCaptureBuffer = ByteBuffer.allocate(screenHeight * screenWidth * 4);

        if (surfaceChanged){
            if (image4DSimple != null) {
                initPatterns();
                myPattern.setNeedSetContent(true);
                myAxis.setNeedSetContent(true);
                myDraw.setNeedDraw(true);
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // set the background
        GLES30.glClearColor(121f/255f, 134f/255f, 203f/255f, 1.0f);

        // 把颜色缓冲区设置为我们预设的颜色
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GL_ALPHA_TEST);
        GLES30.glEnable(GL_BLEND);
        GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        GLES30.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // free memory for old pattern
        freeMemory();

        // set content for new pattern
        setResource();

        // auto rotating
        autoRotate();

        // draw image | annotation | fingerTrajectory
        drawFrame();

        // screenCapture
        screenCapture();


        GLES30.glDisable(GL_BLEND);
        GLES30.glDisable(GL_ALPHA_TEST);
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
    }

    public void init3DImageInfo(Image4DSimple image4DSimple, float[] normalizedSize, int[] originalSize){
        this.image4DSimple = image4DSimple;
        this.normalizedSize = normalizedSize;
        this.originalSize = originalSize;
        this.is2DImage = false;

        initPatterns();
        matrixManager.initMatrixByFile(normalizedSize, renderOptions.getScale(), false);
        myPattern.setNeedSetContent(true);
        myAxis.setNeedSetContent(true);
        myDraw.setNeedDraw(true);
    }

    public void init2DImageInfo(Image4DSimple image4DSimple, Bitmap bitmap2D, float[] normalizedSize, int[] originalSize){
        this.image4DSimple = image4DSimple;
        this.bitmap2D = bitmap2D;
        this.normalizedSize = normalizedSize;
        this.originalSize = originalSize;
        this.is2DImage = true;

        initPatterns();
        matrixManager.initMatrixByFile(normalizedSize, renderOptions.getScale(), true);
        myPattern2D.setNeedSetContent(true);
        myDraw.setNeedDraw(true);
    }

    public void initSwcInfo(NeuronTree neuronTree, float[] normalizedSize, int[] originalSize){
        this.normalizedSize = normalizedSize;
        this.originalSize = originalSize;
        this.is2DImage = false;
        annotationDataManager.loadNeuronTree(neuronTree, false);

        initPatterns();
        matrixManager.initMatrixByFile(normalizedSize, renderOptions.getScale(), false);
        myAxis.setNeedSetContent(true);
        myDraw.setNeedDraw(true);
    }

    private void initPatterns(){
        myPattern.setNeedReleaseMemory(true);
        myPattern2D.setNeedReleaseMemory(true);
        myDraw.setNeedReleaseMemory(true);
        myAxis.setNeedReleaseMemory(true);
    }

    private void freeMemory(){
        if (myPattern.isNeedReleaseMemory()){
            myPattern.releaseMemory();
        }
        if (myPattern2D.isNeedReleaseMemory()){
            myPattern2D.releaseMemory();
        }
        if (myAxis.isNeedReleaseMemory()){
            myAxis.releaseMemory();
        }
        if (myDraw.isNeedReleaseMemory()){
            myDraw.releaseMemory();
        }
    }

    private void setResource(){
        if (myPattern.isNeedSetContent()){
            myPattern.setImage(image4DSimple, screenWidth, screenHeight, normalizedSize);
        }
        if (myAxis.isNeedSetContent()){
            myAxis.setAxis(normalizedSize);
        }
        if (myPattern2D.isNeedSetContent()){
            myPattern2D.setContent(bitmap2D, originalSize[0], originalSize[1], normalizedSize);
        }
    }

    private void autoRotate(){
        matrixManager.autoRotate();
    }

    private void drawFrame(){
        if (myPattern.isNeedDraw()){
            myPattern.drawVolume_3d(matrixManager.getFinalMatrix(),
                    renderOptions.isImageChanging() && renderOptions.isDownSampling(), renderOptions.getContrast());
        }
        if (myPattern2D.isNeedDraw()){
            myPattern2D.draw(matrixManager.getFinalMatrix());
        }
        if (myAxis.isNeedDraw()){
            myAxis.draw(matrixManager.getFinalMatrix());
        }
        if (myDraw.isNeedDraw() && renderOptions.getIfShowSWC()){
            drawNeuronSwc(annotationDataManager.getCurSwcList());
            drawNeuronSwc(annotationDataManager.getSyncSwcList());
            drawMarker(annotationDataManager.getMarkerList());
            drawMarker(annotationDataManager.getSyncMarkerList());
        }
        if (renderOptions.isShowFingerTrajectory()){
            drawTrajectory();
        }
    }

    private void screenCapture(){
        if (renderOptions.isScreenCapture()){
            mCaptureBuffer.rewind();
            GLES30.glReadPixels(0,0, screenWidth, screenHeight, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, mCaptureBuffer);
            renderOptions.setScreenCapture(false);
            exeService.submit(new Runnable() {
                @Override
                public void run() {
                    mCaptureBuffer.rewind();
                    Bitmap bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(mCaptureBuffer);

                    // reverse image
                    android.graphics.Matrix matrix = new android.graphics.Matrix();
                    matrix.postScale(1, -1);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight, matrix, true);
                    mCaptureBuffer.clear();

                    // add watermark
                    Bitmap outBitmap = ImageUtil.drawTextToRightBottom(getContext(),
                            bitmap, "Hi5", 20, Color.RED, 40, 30);
                    if (imageInfo != null) {
                        outBitmap = ImageUtil.drawTextToLeftTop(getContext(), outBitmap, imageInfo, 20, Color.RED, 40, 30);
                    }
                    Uri shareUri = Uri.parse(MediaStore.Images.Media.insertImage(getContext().getContentResolver(), outBitmap,
                            "Image" + System.currentTimeMillis(), "ScreenCapture from Hi5"));

                    ImageInfoRepository.getInstance().getScreenCaptureFilePath().postValue(new FilePath<>(shareUri));
                }
            });

        }
    }

    public void zoom(float ratio){
        matrixManager.zoom(ratio, renderOptions);
    }

    public void rotate(float distanceX, float distanceY){
        matrixManager.rotate(distanceX, distanceY);
    }

    public void drawNeuronSwc(V_NeuronSWC_list swcList){
        if (swcList.nsegs() > 0) {
            ArrayList<Float> lines = new ArrayList<Float>();
            for (int i = 0; i < swcList.seg.size(); i++) {
                V_NeuronSWC seg = swcList.seg.get(i);
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                lines.clear();

                for (int j = 0; j < seg.row.size(); j++) {
                    if (seg.row.get(j).parent != -1 && seg.getIndexofParent(j) != -1) {
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(j));
                        swcUnitMap.put(j, parent);
                    }
                }

                int type = 0;
                for (int j = 0; j < seg.row.size(); j++) {
                    V_NeuronSWC_unit child = seg.row.get(j);
                    int parentId = (int) child.parent;
                    if (parentId == -1 || seg.getIndexofParent(j) == -1) {
                        float[] position = volumeToModel(new float[]{(float) child.x, (float) child.y, (float) child.z});
                        myDraw.drawSplitPoints(matrixManager.getFinalMatrix(), position[0], position[1], position[2], (int) child.type);
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(j);
                    if (parent == null){
                        continue;
                    }
                    lines.add((float) ((originalSize[0] - parent.x) / originalSize[0] * normalizedSize[0]));
                    lines.add((float) ((originalSize[1] - parent.y) / originalSize[1] * normalizedSize[1]));
                    lines.add((float) ((parent.z) / originalSize[2] * normalizedSize[2]));
                    lines.add((float) ((originalSize[0] - child.x) / originalSize[0] * normalizedSize[0]));
                    lines.add((float) ((originalSize[1] - child.y) / originalSize[1] * normalizedSize[1]));
                    lines.add((float) ((child.z) / originalSize[2] * normalizedSize[2]));
                    type = (int) parent.type;
                }
                myDraw.drawLine(matrixManager.getFinalMatrix(), lines, type);
                lines.clear();
            }
        }
    }

    public void showSwc()
    {
        if (ifShowSWC) {
            V_NeuronSWC_list curSwcList = annotationDataManager.getCurSwcList();

                    /*
                    show swc
                     */
            if (curSwcList.nsegs() > 0) {
                ArrayList<Float> lines = new ArrayList<Float>();
                int type = 0;
                for (int i = 0; i < curSwcList.seg.size(); i++) {
                    V_NeuronSWC seg = curSwcList.seg.get(i);
                    Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                    lines.clear();
                    for (int j = 0; j < seg.row.size(); j++) {
                        if (seg.row.get(j).parent != -1 && seg.getIndexofParent(j) != -1) {
                            V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(j));
                            swcUnitMap.put(j, parent);
                        }
                    }
                    for (int j = 0; j < seg.row.size(); j++) {
                        V_NeuronSWC_unit child = seg.row.get(j);
                        int parentid = (int) child.parent;
                        if (parentid == -1 || seg.getIndexofParent(j) == -1) {
                            continue;
                        }
                        V_NeuronSWC_unit parent = swcUnitMap.get(j);
                        if (parent == null){
                            continue;
                        }
                        lines.add((float) ((originalSize[0] - parent.x) / originalSize[0] * normalizedSize[0]));
                        lines.add((float) ((originalSize[1] - parent.y) / originalSize[1] * normalizedSize[1]));
                        lines.add((float) ((parent.z) / originalSize[2] * normalizedSize[2]));
                        lines.add((float) ((originalSize[0] - child.x) / originalSize[0] * normalizedSize[0]));
                        lines.add((float) ((originalSize[1] - child.y) / originalSize[1] * normalizedSize[1]));
                        lines.add((float) ((child.z) / originalSize[2] * normalizedSize[2]));
                        type = (int) parent.type;
                    }
                    myDraw.drawLine(matrixManager.getFinalMatrix(), lines, type);
                    lines.clear();
                }
            }

//            if (newSwcList.nsegs() > 0) {
//                ArrayList<Float> lines = new ArrayList<Float>();
//                int type = 0;
//                for (int i = 0; i < newSwcList.seg.size(); i++) {
//                    V_NeuronSWC seg = newSwcList.seg.get(i);
//                    Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
//                    lines.clear();
//                    for (int j = 0; j < seg.row.size(); j++) {
//                        if (seg.row.get(j).parent != -1 && seg.getIndexofParent(j) != -1) {
//                            V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(j));
//                            swcUnitMap.put(j, parent);
//                        }
//                    }
//                    for (int j = 0; j < seg.row.size(); j++) {
//                        V_NeuronSWC_unit child = seg.row.get(j);
//                        int parentid = (int) child.parent;
//                        if (parentid == -1 || seg.getIndexofParent(j) == -1) {
//                            // Log.v(TAG,"parent == -1;");
//                            float x = (float) child.x;
//                            float y = (float) child.y;
//                            float z = (float) child.z;
////                            float[] position = volumetoModel(new float[]{x, y, z});
//                            float[] position = volumeToModel(new float[]{x, y, z});
//
//                            myDraw.drawSplitPoints(matrixManager.getFinalMatrix(), position[0], position[1], position[2], (int) child.type);
//                            continue;
//                        }
//
//                        V_NeuronSWC_unit parent = swcUnitMap.get(j);
//
//                        lines.add((float) ((originalSize[0] - parent.x) / originalSize[0] * normalizedSize[0]));
//                        lines.add((float) ((originalSize[1] - parent.y) / originalSize[1] * normalizedSize[1]));
//                        lines.add((float) ((parent.z) / originalSize[2] * normalizedSize[2]));
//                        lines.add((float) ((originalSize[0] - child.x) / originalSize[0] * normalizedSize[0]));
//                        lines.add((float) ((originalSize[1] - child.y) / originalSize[1] * normalizedSize[1]));
//                        lines.add((float) ((child.z) / originalSize[2] * normalizedSize[2]));
//                        type = (int) parent.type;
//                    }
//
//
//                    myDraw.drawLine(matrixManager.getFinalMatrix(), lines, type);
//                    lines.clear();
//                }
//
//            }
            V_NeuronSWC_list syncSwcList = annotationDataManager.getSyncSwcList();

            if (syncSwcList.nsegs() > 0) {
                ArrayList<Float> lines = new ArrayList<Float>();
                int type = 0;
                for (int i = 0; i < syncSwcList.seg.size(); i++) {
                    V_NeuronSWC seg = syncSwcList.seg.get(i);
                    Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                    lines.clear();
                    for (int j = 0; j < seg.row.size(); j++) {
                        if (seg.row.get(j).parent != -1 && seg.getIndexofParent(j) != -1) {
                            V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(j));
                            swcUnitMap.put(j, parent);
                        }
                    }

                    for (int j = 0; j < seg.row.size(); j++) {
                        V_NeuronSWC_unit child = seg.row.get(j);
                        int parentid = (int) child.parent;
                        if (parentid == -1 || seg.getIndexofParent(j) == -1) {
                            continue;
                        }
                        V_NeuronSWC_unit parent = swcUnitMap.get(j);
                        if (parent == null){
                            continue;
                        }
//                                Log.d(TAG, "lines.add: " + parent.x + " " + parent.y + " " + parent.z + " " + child.x + " " + child.y + " " + child.z);
                        lines.add((float) ((originalSize[0] - parent.x) / originalSize[0] * normalizedSize[0]));
                        lines.add((float) ((originalSize[1] - parent.y) / originalSize[1] * normalizedSize[1]));
                        lines.add((float) ((parent.z) / originalSize[2] * normalizedSize[2]));
                        lines.add((float) ((originalSize[0] - child.x) / originalSize[0] * normalizedSize[0]));
                        lines.add((float) ((originalSize[1] - child.y) / originalSize[1] * normalizedSize[1]));
                        lines.add((float) ((child.z) / originalSize[2] * normalizedSize[2]));
                        type = (int) parent.type;

                    }
                    myDraw.drawLine(matrixManager.getFinalMatrix(), lines, type);
                    lines.clear();
                }
            }

                    /*
                    draw the marker
                    */
            MarkerList markerList = annotationDataManager.getMarkerList();

            if (markerList.size() > 0) {
                float radius = is2DImage ? 0.01f : 0.02f;
                for (int i = 0; i < markerList.size(); i++) {
                    ImageMarker imageMarker = markerList.get(i);
                    float[] markerModel = volumeToModel(new float[]{imageMarker.x, imageMarker.y, imageMarker.z});

                    if (imageMarker.radius == 5) {
                        myDraw.drawMarker(matrixManager.getFinalMatrix(), matrixManager.getModelMatrix(), markerModel[0], markerModel[1], markerModel[2], imageMarker.type, 0.01f);
                    } else {
                        myDraw.drawMarker(matrixManager.getFinalMatrix(), matrixManager.getModelMatrix(), markerModel[0], markerModel[1], markerModel[2], imageMarker.type, radius);
                    }
                }
            }
        }

    }


    private void drawMarker(MarkerList markerList) {
        if (markerList.size() > 0) {
            float radius = is2DImage ? 0.01f : 0.02f;
            for (int i = 0; i < markerList.size(); i++) {
                ImageMarker imageMarker = markerList.get(i);
                float[] markerModel = volumeToModel(new float[]{imageMarker.x, imageMarker.y, imageMarker.z});

                if (imageMarker.radius == 5) {
                    myDraw.drawMarker(matrixManager.getFinalMatrix(), matrixManager.getModelMatrix(), markerModel[0], markerModel[1], markerModel[2], imageMarker.type, 0.01f);
                } else {
                    myDraw.drawMarker(matrixManager.getFinalMatrix(), matrixManager.getModelMatrix(), markerModel[0], markerModel[1], markerModel[2], imageMarker.type, radius);
                }
            }
        }
    }

    private void drawTrajectory(){
        if(fingerTrajectory != null && fingerTrajectory.length > 0){
            myDraw.drawPoints(fingerTrajectory, fingerTrajectory.length / 3);
            myDraw.freePoint();
        }
    }

    public void updateFingerTrajectory(List<Float> fingerTrajectory){
        if (fingerTrajectory.size() == 0){
            this.fingerTrajectory = null;
        }

        float[] data = new float[fingerTrajectory.size()];
        for (int i = 0; i < fingerTrajectory.size(); i++){
            data[i] =  fingerTrajectory.get(i);
        }
        this.fingerTrajectory = data;
    }

    /*
    *for collaboration activity
    *
    * */

//    public void syncAddSegSWC(V_NeuronSWC seg) {
//        annotationDataManager.syncAddSegSWC(seg);
//    }
//
//    public void syncRetypeSegSWC(V_NeuronSWC seg) {
//        annotationDataManager.syncRetypeSegSWC(seg);
//    }
//
//    public void syncDelSegSWC(V_NeuronSWC seg) {
//        annotationDataManager.syncDelSegSWC(seg);
//    }
//
//    public void syncAddMarker(ImageMarker imageMarker) {
//        annotationDataManager.syncAddMarker(imageMarker);
//    }
//
//    public void syncDelMarker(ImageMarker imageMarker) {
//        annotationDataManager.syncDelMarker(imageMarker);
//    }








    public NeuronTree getNeuronTree(){
        return annotationDataManager.getNeuronTree();
    }

    public float[] modeToVolume(float[] point){
        if (point == null){
            Log.e(TAG,"null array in modeToVolume");
            return null;
        }

        float[] result = new float[3];
        result[0] = (1.0f - point[0] / normalizedSize[0]) * originalSize[0];
        result[1] = (1.0f - point[1] / normalizedSize[1]) * originalSize[1];
        result[2] = point[2] / normalizedSize[2] * originalSize[2];

        return result;
    }

    public float[] volumeToModel(float[] point){
        if (point == null){
            Log.e(TAG,"null array in volumeToModel");
            return null;
        }

        float[] result = new float[3];
        result[0] = (originalSize[0] - point[0]) / originalSize[0] * normalizedSize[0];
        result[1] = (originalSize[1] - point[1]) / originalSize[1] * normalizedSize[1];
        result[2] = point[2] / originalSize[2] * normalizedSize[2];

        return result;
    }

    public void setImageInfo(String imageInfo) {
        this.imageInfo = imageInfo;
    }
}
