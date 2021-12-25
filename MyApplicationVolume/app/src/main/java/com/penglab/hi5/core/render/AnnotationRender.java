package com.penglab.hi5.core.render;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static javax.microedition.khronos.opengles.GL10.GL_ALPHA_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_unit;
import com.penglab.hi5.core.fileReader.annotationReader.ApoReader;
import com.penglab.hi5.core.render.pattern.MyAxis;
import com.penglab.hi5.core.render.pattern.MyDraw;
import com.penglab.hi5.core.render.pattern.MyPattern;
import com.penglab.hi5.core.render.utils.AnnotationManager;
import com.penglab.hi5.core.render.utils.RenderOptions;
import com.penglab.hi5.core.ui.check.FileInfoState;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.model.img.BasicFile;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jackiexing on 12/20/21
 */
public class AnnotationRender extends BasicRender{

    private final String TAG = "AnnotationRender";
    private final ImageInfoRepository imageInfoRepository = ImageInfoRepository.getInstance();
    private final FileInfoState fileInfoState = FileInfoState.getInstance();

    private final float[] persProjectionMatrix = new float[16];      // for 3D
    private final float[] orthProjectionMatrix = new float[16];      // for 2D
    private final float[] viewMatrix           = new float[16];
    private final float[] translate2Matrix     = new float[16];
    private final float[] zoomMatrix           = new float[16];
    private final float[] rotateMatrix         = new float[16];
    private final float[] translateMatrix      = new float[16];
    private final float[] modelMatrix          = new float[16];
    private final float[] finalMatrix          = new float[16];

    private final float[] normalizedSize = new float[3];
    private final int[] originalSize = new int[3];

    private Image4DSimple image4DSimple;

    private final MyPattern myPattern = new MyPattern();
    private final MyAxis myAxis = new MyAxis();
    private final MyDraw myDraw = new MyDraw();

    private final RenderOptions renderOptions = new RenderOptions();
    private final AnnotationManager annotationManager = new AnnotationManager();

    private int screenWidth;
    private int screenHeight;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG,"onSurfaceCreated successfully");

        GLES30.glClearColor(121f/255f, 134f/255f, 203f/255f, 1.0f);

        // init shader program
        MyPattern.initProgram();
        MyAxis.initProgram();
        MyDraw.initProgram();
//        MyPattern2D.initProgram();

        // init basic Matrix
        initMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int screenWidth, int screenHeight) {
        GLES30.glViewport(0, 0, screenWidth, screenHeight);

        // this projection matrix is applied to object coordinates
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        float ratio = (float) screenWidth / screenHeight;

        if (screenWidth > screenHeight) {
            Matrix.orthoM(orthProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
            Matrix.frustumM(persProjectionMatrix, 0, -ratio, ratio, -1, 1, 2f, 100);
        } else {
            Matrix.orthoM(orthProjectionMatrix, 0, -1, 1, -1 / ratio, 1 / ratio, 1, 100);
            Matrix.frustumM(persProjectionMatrix, 0, -1, 1, -1 / ratio, 1 / ratio, 2f, 100);
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

        setResource();
        drawFrame();

        GLES30.glDisable(GL_BLEND);
        GLES30.glDisable(GL_ALPHA_TEST);
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
    }

    @Override
    protected void initMatrix() {
        // set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -2, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // init model matrix
        Matrix.setIdentityM(translateMatrix,0);
        Matrix.setIdentityM(zoomMatrix,0);
        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.setRotateM(rotateMatrix, 0, 0, -1.0f, -1.0f, 0.0f);
    }

    @Override
    protected void setMatrixByFile() {
        // Set translateMatrix
        Matrix.translateM(translateMatrix, 0, -0.5f * normalizedSize[0], -0.5f * normalizedSize[1], -0.5f * normalizedSize[2]);

        // Set translate2Matrix
        Matrix.setIdentityM(translate2Matrix, 0);
        Matrix.translateM(translate2Matrix, 0, 0, 0, renderOptions.getScale() / 2 * (float) Math.sqrt(3));
    }

    @Override
    protected void updateFinalMatrix(){

        // Calculate the projection and view transformation
        Matrix.multiplyMM(finalMatrix, 0, persProjectionMatrix, 0, viewMatrix, 0);

        // model = zoom * rotate * translate
        // Calculate model matrix
        Matrix.multiplyMM(modelMatrix, 0, rotateMatrix, 0, translateMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, zoomMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, translate2Matrix, 0, modelMatrix, 0);

        // Calculate final matrix
        Matrix.multiplyMM(finalMatrix, 0, finalMatrix, 0, modelMatrix, 0);

//        Log.e(TAG,"finalMatrix " + Arrays.toString(finalMatrix));
    }

    public void loadFile(){
        Log.d(TAG,"load File");

        BasicFile basicFile = imageInfoRepository.getBasicImage();
        FilePath<?> filePath = basicFile.getFilePath();
        FileType fileType = basicFile.getFileType();

        switch (fileType){
            case V3DPBD:
            case V3DRAW:
            case TIFF:
                image4DSimple = Image4DSimple.loadImage(filePath, fileType);
                if (image4DSimple != null){
                    updateFileSize(new Integer[]{
                            (int) image4DSimple.getSz0(), (int) image4DSimple.getSz1(), (int) image4DSimple.getSz2()});
                    myPattern.setNeedSetContent(true);
                    myAxis.setNeedSetContent(true);
                    myDraw.setNeedDraw(true);
                }
                break;
            default:
                ToastEasy("Unsupported file !");
        }

        initMatrix();
        setMatrixByFile();
        updateFinalMatrix();
    }

    public void loadAnnotationFile(){
        Log.d(TAG,"Load annotation file");

        BasicFile basicFile = imageInfoRepository.getBasicFile();
        FilePath<?> filePath = basicFile.getFilePath();
        FileType fileType = basicFile.getFileType();

        switch (fileType){
            case ANO:
                Log.e(TAG,"load .ano file !");
                break;
            case SWC:
            case ESWC:
                Log.e(TAG,"load .swc file !");
                NeuronTree neuronTree = NeuronTree.parse(filePath);
                if (neuronTree == null){
                    ToastEasy("Something wrong with this .swc/.eswc file, can't load it");
                } else {
                    annotationManager.loadNeuronTree(neuronTree);
                }
                break;
            case APO:
                MarkerList markerList = ApoReader.parse(filePath);
                if (markerList == null){
                    ToastEasy("Something wrong with this .apo file, can't load it");
                } else {
                    annotationManager.loadMarkerList(markerList);
                }
                break;
            default:
                ToastEasy("Unsupported file !");
        }
    }

    private void setResource(){
        if (myPattern.isNeedSetContent()){
            myPattern.setImage(image4DSimple, screenWidth, screenHeight, normalizedSize);
        }
        if (myAxis.isNeedSetContent()){
            myAxis.setAxis(normalizedSize);
        }
    }

    private void drawFrame(){
        if (myPattern.isNeedDraw()){
            myPattern.drawVolume_3d(finalMatrix, renderOptions.isDownSampling(), renderOptions.getContrast());
        }
        if (myAxis.isNeedDraw()){
            myAxis.draw(finalMatrix);
        }
        if (myDraw.isNeedDraw()){
            drawNeuronSwc(annotationManager.getCurSwcList());
            drawNeuronSwc(annotationManager.getNewSwcList());
            drawNeuronSwc(annotationManager.getLoadedSwcList());
        }
    }

    private void updateFileSize(Integer[] size){
        float maxSize = (float) Collections.max(Arrays.asList(size));

        originalSize[0] = size[0];
        originalSize[1] = size[1];
        originalSize[2] = size[2];

        normalizedSize[0] = (float) size[0] / maxSize;
        normalizedSize[1] = (float) size[1] / maxSize;
        normalizedSize[2] = (float) size[2] / maxSize;

        Log.e(TAG,"max Size " + maxSize);
        Log.e(TAG,Arrays.toString(normalizedSize));
    }


    public void scale(float ratio){
        float curScale = renderOptions.getScale();
        if ((curScale < 0.2 && ratio < 1) || (curScale > 30 && ratio > 1)){
            Log.e(TAG, "Can't be smaller or bigger !");
        } else {
            // set scale
            renderOptions.setScale(curScale * ratio);

            Matrix.scaleM(zoomMatrix, 0, ratio, ratio, ratio);
            Matrix.setIdentityM(translate2Matrix, 0);
            Matrix.translateM(translate2Matrix, 0, 0, 0, renderOptions.getScale() / 2 * (float) Math.sqrt(3));
            updateFinalMatrix();
        }
    }

    public void rotate(float distanceX, float distanceY){
        float[] tempRotateMatrix = new float[16];
        Matrix.setRotateM(tempRotateMatrix, 0, distanceY * 70, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(tempRotateMatrix, 0, distanceX * 70, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMM(rotateMatrix, 0, tempRotateMatrix, 0, rotateMatrix, 0);
        updateFinalMatrix();
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
                        myDraw.drawSplitPoints(finalMatrix, position[0], position[1], position[2], (int) child.type);
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
                myDraw.drawLine(finalMatrix, lines, type);
                lines.clear();
            }
        }
    }

    public NeuronTree getNeuronTree(){
        return annotationManager.getNeuronTree();
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
}
