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
import com.penglab.hi5.core.render.utils.AnnotationDataManager;
import com.penglab.hi5.core.render.utils.MatrixManager;
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

    private final float[] normalizedSize = new float[3];
    private final int[] originalSize = new int[3];

    private Image4DSimple image4DSimple;

    private final MyPattern myPattern = new MyPattern();
    private final MyAxis myAxis = new MyAxis();
    private final MyDraw myDraw = new MyDraw();

    private final RenderOptions renderOptions = new RenderOptions();
    private final AnnotationDataManager annotationDataManager = new AnnotationDataManager();
    private final MatrixManager matrixManager = new MatrixManager();

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
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int screenWidth, int screenHeight) {
        GLES30.glViewport(0, 0, screenWidth, screenHeight);

        // this projection matrix is applied to object coordinates
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        matrixManager.setProjectionMatrix(screenWidth, screenHeight);

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
        matrixManager.initMatrixByFile(normalizedSize, renderOptions.getScale());
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
                    annotationDataManager.loadNeuronTree(neuronTree);
                }
                break;
            case APO:
                MarkerList markerList = ApoReader.parse(filePath);
                if (markerList == null){
                    ToastEasy("Something wrong with this .apo file, can't load it");
                } else {
                    annotationDataManager.loadMarkerList(markerList);
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
            myPattern.drawVolume_3d(matrixManager.getFinalMatrix(), renderOptions.isDownSampling(), renderOptions.getContrast());
        }
        if (myAxis.isNeedDraw()){
            myAxis.draw(matrixManager.getFinalMatrix());
        }
        if (myDraw.isNeedDraw()){
            drawNeuronSwc(annotationDataManager.getCurSwcList());
            drawNeuronSwc(annotationDataManager.getNewSwcList());
            drawNeuronSwc(annotationDataManager.getLoadedSwcList());
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
}
