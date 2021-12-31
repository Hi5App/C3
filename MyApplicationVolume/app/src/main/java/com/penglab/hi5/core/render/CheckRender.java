package com.penglab.hi5.core.render;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static javax.microedition.khronos.opengles.GL10.GL_ALPHA_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.penglab.hi5.basic.ByteTranslate;
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
 * Created by Yihang zhu 12/25/21
 */
public class CheckRender extends BasicRender {

    private final String TAG = "CheckRender";
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
//        MyPattern2D.initProgram();

        // init basic Matrix
        matrixManager.initMatrix();
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
                    annotationDataManager.loadNeuronTree(neuronTree, false);
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
            drawNeuronSwc(annotationDataManager.getSyncSwcList());
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

    public float[] volumeToModel(float[] point){
        if (point == null){
            Log.e(TAG,"null array in volume2Model");
            return null;
        }

        float[] result = new float[3];
        result[0] = (originalSize[0] - point[0]) / originalSize[0] * normalizedSize[0];
        result[1] = (originalSize[1] - point[1]) / originalSize[1] * normalizedSize[1];
        result[2] = point[2] / originalSize[2] * normalizedSize[2];

        return result;
    }

    public float[] modelToVolume(float[] input){
        if (input == null)
            return null;

        float[] result = new float[3];
        result[0] = (1.0f - input[0] / normalizedSize[0]) * originalSize[0];
        result[1] = (1.0f - input[1] / normalizedSize[1]) * originalSize[1];
        result[2] = input[2] / normalizedSize[2] * originalSize[2];

        return result;
    }

    public float[] solveMarkerCenter(float x, float y){

        float [] loc1 = new float[3];
        float [] loc2 = new float[3];

        get_NearFar_Marker_2(x, y, loc1, loc2);

//        Log.v("loc1",Arrays.toString(loc1));
//        Log.v("loc2",Arrays.toString(loc2));

        float steps = 512;
        float [] step = devide(minus(loc1, loc2), steps);
//        Log.v("step",Arrays.toString(step));


        if(make_Point_near(loc1, loc2)){
            float [] Marker = getCenterOfLineProfile(loc1, loc2);
            if (Marker == null){
                return null;
            }
//            Log.v("Marker",Arrays.toString(Marker));
            return Marker;
        }else {
//            Log.v("solveMarkerCenter","please make sure the point inside the bounding box");
            ToastEasy("please make sure the point inside the bounding box");
            return null;
        }

    }

    //用于透视投影中获取近平面和远平面的焦点
    private void get_NearFar_Marker_2(float x, float y, float [] res1, float [] res2){
        // mvp矩阵的逆矩阵
        float [] invertfinalMatrix = new float[16];

        Matrix.invertM(invertfinalMatrix, 0, matrixManager.getFinalMatrix(), 0);
//        Log.v("invert_rotation",Arrays.toString(invertfinalMatrix));

        float [] near = new float[4];
        float [] far = new float[4];

        Matrix.multiplyMV(near, 0, invertfinalMatrix, 0, new float [] {x, y, -1, 1}, 0);
        Matrix.multiplyMV(far, 0, invertfinalMatrix, 0, new float [] {x, y, 1, 1}, 0);

        devideByw(near);
        devideByw(far);

//        Log.v("near",Arrays.toString(near));
//        Log.v("far",Arrays.toString(far));

        for(int i=0; i<3; i++){
            res1[i] = near[i];
            res2[i] = far[i];
        }

    }

    // 类似于光线投射，找直线上强度最大的一点
    private float[] getCenterOfLineProfile(float[] loc1, float[] loc2){

        float[] result = new float[3];
        float[] loc1_index = new float[3];
        float[] loc2_index = new float[3];
        boolean isInBoundingBox = false;

        loc1_index = modelToVolume(loc1);
        loc2_index = modelToVolume(loc2);

        float[] d = minus(loc1_index, loc2_index);
        normalize(d);

        float[][] dim = new float[3][2];
        for(int i=0; i<3; i++){
            dim[i][0] = 0;
            dim[i][1] = originalSize[i] - 1;
        }

        result = devide(plus(loc1_index, loc2_index), 2);
        float max_value = 0f;

        //单位向量
//        float[] d = minus(loc1_index, loc2_index);
//        normalize(d);
//        Log.v("getCenterOfLineProfile:", "step: " + Arrays.toString(d));

        // 判断是不是一个像素
        float length = distance(loc1_index, loc2_index);
        if(length < 0.5)
            return result;

        int nstep = (int)(length+0.5);
        float one_step = length/nstep;

        Log.v("getCenterOfLineProfile", Float.toString(one_step));

        float[] poc;
        for (int i = 0; i <= nstep; i++) {
            float value;
            poc = minus(loc1_index, multiply(d, one_step * i));

            if (isInBoundingBox(poc, dim)) {
                value = sample3D(poc[0], poc[1], poc[2]);

                isInBoundingBox = true;
                if(value > max_value){
//                    Log.v("getCenterOfLineProfile", "(" + poc[0] + "," + poc[1] + "," + poc[2] + "): " +value);
//                    Log.v("getCenterOfLineProfile:", "update the max");
                    max_value = value;
                    for (int j = 0; j < 3; j++){
                        result[j] = poc[j];
                    }
                    isInBoundingBox = true;
                }
            }
        }

        if(!isInBoundingBox){
            ToastEasy("please make sure the point inside the bounding box");
            return null;
        }

        return result;
    }

    // 找到靠近boundingbox的两处端点
    private boolean make_Point_near(float[] loc1, float[] loc2){

        float steps = 512;
        float [] near = loc1;
        float [] far = loc2;
        float [] step = devide(minus(near, far), steps);

        float[][] dim = new float[3][2];
        for(int i=0; i<3; i++){
            dim[i][0]= 0;
            dim[i][1]= normalizedSize[i];
        }

        int num = 0;
        while(num<steps && !isInBoundingBox(near, dim)){
            near = minus(near, step);
            num++;
        }
        if(num == steps)
            return false;


        while(!isInBoundingBox(far, dim)){
            far = plus(far, step);
        }

        near = plus(near, step);
        far = minus(far, step);

        for(int i=0; i<3; i++){
            loc1[i] = near[i];
            loc2[i] = far[i];
        }

        return true;

    }

    // 除法运算
    private void devideByw(float[] x){
        if(Math.abs(x[3]) < 0.000001f){
            Log.v("devideByw","can not be devided by 0");
            return;
        }

        for(int i=0; i<3; i++)
            x[i] = x[i]/x[3];

    }

    // 除法运算
    private float [] devide(float[] x, float num){

        int length = x.length;
        float [] result = new float[length];

        for(int i=0; i<length; i++)
            result[i] = x[i]/num;

        return result;
    }

    // 减法运算
    private float [] minus(float[] x, float[] y){
        if(x.length != y.length){
            Log.v("minus","length is not the same!");
            return null;
        }

        int length = x.length;
        float [] result = new float[length];

        for (int i=0; i<length; i++)
            result[i] = x[i] - y[i];
        return result;
    }

    private void normalize(float[] x){
        int length = x.length;
        float sum = 0;

        for(int i=0; i<length; i++)
            sum += Math.pow(x[i], 2);

        for(int i=0; i<length; i++)
            x[i] = x[i] / (float)Math.sqrt(sum);
    }

    // 加法运算
    private float [] plus(float[] x, float[] y){
        if(x.length != y.length){
            Log.v("plus","length is not the same!");
            return null;
        }

        int length = x.length;
        float [] result = new float[length];

        for (int i=0; i<length; i++)
            result[i] = x[i] + y[i];
        return result;
    }

    private float distance(float[] x, float[] y){
        int length = x.length;
        float sum = 0;

        for(int i=0; i<length; i++){
            sum += Math.pow(x[i]-y[i], 2);
        }
        return (float)Math.sqrt(sum);
    }

    // 乘法运算
    private float [] multiply(float[] x, float num){
        if(num == 0){
            Log.v("multiply","can not be multiply by 0");
        }

        int length = x.length;
        float [] result = new float[length];

        for(int i=0; i<length; i++)
            result[i] = x[i] * num;

        return result;
    }

    private float sample3D(float x, float y, float z){
        int x0, x1, y0, y1, z0, z1;
        x0 = (int) Math.floor(x);         x1 = (int) Math.ceil(x);
        y0 = (int) Math.floor(y);         y1 = (int) Math.ceil(y);
        z0 = (int) Math.floor(z);         z1 = (int) Math.ceil(z);

        float xf, yf, zf;
        xf = x-x0;
        yf = y-y0;
        zf = z-z0;

        float [][][] is = new float[2][2][2];
        is[0][0][0] = grayData(x0, y0, z0);
        is[0][0][1] = grayData(x0, y0, z1);
        is[0][1][0] = grayData(x0, y1, z0);
        is[0][1][1] = grayData(x0, y1, z1);
        is[1][0][0] = grayData(x1, y0, z0);
        is[1][0][1] = grayData(x1, y0, z1);
        is[1][1][0] = grayData(x1, y1, z0);
        is[1][1][1] = grayData(x1, y1, z1);

        float [][][] sf = new float[2][2][2];
        sf[0][0][0] = (1-xf)*(1-yf)*(1-zf);
        sf[0][0][1] = (1-xf)*(1-yf)*(  zf);
        sf[0][1][0] = (1-xf)*(  yf)*(1-zf);
        sf[0][1][1] = (1-xf)*(  yf)*(  zf);
        sf[1][0][0] = (  xf)*(1-yf)*(1-zf);
        sf[1][0][1] = (  xf)*(1-yf)*(  zf);
        sf[1][1][0] = (  xf)*(  yf)*(1-zf);
        sf[1][1][1] = (  xf)*(  yf)*(  zf);

        float result = 0f;

        for(int i=0; i<2; i++)
            for(int j=0; j<2; j++)
                for(int k=0; k<2; k++)
                    result +=  is[i][j][k] * sf[i][j][k];

        return result;
    }

    private int grayData(int x, int y, int z){
        int result = 0;
        int data_length = imageInfoRepository.getBasicImage().getImage4DSimple().getDatatype().ordinal();
        byte [] grayscale = imageInfoRepository.getBasicImage().getImage4DSimple().getData();
        boolean isBig = imageInfoRepository.getBasicImage().getImage4DSimple().getIsBig();
        if (data_length == 1){
            byte b = grayscale[z * originalSize[0] * originalSize[1] + y * originalSize[0] + x];
            result = ByteTranslate.byte1ToInt(b);
        }else if (data_length == 2){
            byte [] b = new byte[2];
            b[0] = grayscale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 2];
            b[1] = grayscale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 2 + 1];
            result = ByteTranslate.byte2ToInt(b, isBig);
        }else if (data_length == 4){
            byte [] b = new byte[4];
            b[0] = grayscale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 4];
            b[1] = grayscale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 4 + 1];
            b[2] = grayscale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 4 + 2];
            b[3] = grayscale[(z * originalSize[0] * originalSize[1] + y * originalSize[0] + x) * 4 + 3];
            result = ByteTranslate.byte2ToInt(b, isBig);
        }
        return result;
    }

    // 判断是否在图像内部了
    private boolean isInBoundingBox(float[] x, float[][] dim){
        int length = x.length;
        for(int i=0; i<length; i++){
            if(x[i]>=dim[i][1] || x[i]<=dim[i][0])
                return false;
        }
        return true;
    }

    public int [] newCenterWhenNavigateWhenClick(float x, float y) {
        float [] center = solveMarkerCenter(x, y);
        return fileInfoState.newCenterWhenNavigateBlockToTargetOffset((int) center[0] - 64, (int) center[1] - 64, (int) center[2] - 64);
    }
}
