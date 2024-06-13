package com.penglab.hi5.core.render;

import static com.penglab.hi5.core.Myapplication.getContext;
import static javax.microedition.khronos.opengles.GL10.GL_ALPHA_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.provider.MediaStore;
import android.util.Log;

import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.image.ImageUtil;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_unit;
import com.penglab.hi5.core.render.pattern.MyAxis;
import com.penglab.hi5.core.render.pattern.MyDraw;
import com.penglab.hi5.core.render.pattern.MyPattern;
import com.penglab.hi5.core.render.utils.AnnotationDataManager;
import com.penglab.hi5.core.render.utils.MatrixManager;
import com.penglab.hi5.core.render.utils.RenderOptions;
import com.penglab.hi5.core.ui.check.CheckArborInfoState;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Yihang zhu 12/25/21
 */
public class CheckRender implements GLSurfaceView.Renderer {

    private final String TAG = "CheckRender";
    private final CheckArborInfoState checkArborInfoState = CheckArborInfoState.getInstance();

    private Image4DSimple image4DSimple;
    private float[] normalizedSize = new float[3];
    private int[] originalSize = new int[3];

    private final MyPattern myPattern = new MyPattern();
    private final MyAxis myAxis = new MyAxis();
    private final MyDraw myDraw = new MyDraw();

    private final RenderOptions renderOptions;
    private final MatrixManager matrixManager;
    private final AnnotationDataManager annotationDataManager;

    private int screenWidth;
    private int screenHeight;

    private final ExecutorService exeService = Executors.newSingleThreadExecutor();
    private ByteBuffer mCaptureBuffer;

    public CheckRender(AnnotationDataManager annotationDataManager, MatrixManager matrixManager, RenderOptions renderOptions){
        this.annotationDataManager = annotationDataManager;
        this.matrixManager = matrixManager;
        this.renderOptions = renderOptions;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG,"onSurfaceCreated successfully");

        GLES32.glClearColor(121f/255f, 134f/255f, 203f/255f, 1.0f);

        // init shader program
        MyPattern.initProgram();
        MyAxis.initProgram();
        MyDraw.initProgram();

        // init basic Matrix
        matrixManager.initMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int screenWidth, int screenHeight) {
        GLES32.glViewport(0, 0, screenWidth, screenHeight);

        // this projection matrix is applied to object coordinates
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        matrixManager.setProjectionMatrix(screenWidth, screenHeight);
        mCaptureBuffer = ByteBuffer.allocate(screenHeight * screenWidth * 4);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // set the background
        GLES32.glClearColor(121f/255f, 134f/255f, 203f/255f, 1.0f);

        // 把颜色缓冲区设置为我们预设的颜色
        GLES32.glEnable(GLES32.GL_DEPTH_TEST);
        GLES32.glEnable(GL_ALPHA_TEST);
        GLES32.glEnable(GL_BLEND);
        GLES32.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        GLES32.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // free memory for old pattern
        freeMemory();

        // set content for new pattern
        setResource();

        // auto rotating
        autoRotate();

        // draw image | annotation
        drawFrame();

        // screenCapture
        screenCapture();

        GLES32.glDisable(GL_BLEND);
        GLES32.glDisable(GL_ALPHA_TEST);
        GLES32.glDisable(GLES32.GL_DEPTH_TEST);
    }

    public void init3DImageInfo(Image4DSimple image4DSimple, float[] normalizedSize, int[] originalSize){
        this.image4DSimple = image4DSimple;
        this.normalizedSize = normalizedSize;
        this.originalSize = originalSize;

        initPatterns();
        matrixManager.initMatrixByFile(normalizedSize, renderOptions.getScale(), false);
        myPattern.setNeedSetContent(true);
        myAxis.setNeedSetContent(true);
        myDraw.setNeedDraw(true);
    }

    private void initPatterns(){
        myPattern.setNeedReleaseMemory(true);
        myDraw.setNeedReleaseMemory(true);
        myAxis.setNeedReleaseMemory(true);
    }

    private void freeMemory(){
        if (myPattern.isNeedReleaseMemory()){
            myPattern.releaseMemory();
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
    }

    private void drawFrame(){
        if (myPattern.isNeedDraw()){
            myPattern.drawVolume_3d(matrixManager.getFinalMatrix(), renderOptions.isDownSampling(), renderOptions.getContrast(), renderOptions.getContrastEnhanceRatio(), true);
        }
        if (myAxis.isNeedDraw()){
            myAxis.draw(matrixManager.getFinalMatrix());
        }
        if (myDraw.isNeedDraw()){
            drawNeuronSwc(annotationDataManager.getCurSwcList());
            drawNeuronSwc(annotationDataManager.getSyncSwcList());
        }
    }

    private void autoRotate(){
        matrixManager.autoRotate();
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

    private void screenCapture() {
        if (renderOptions.isScreenCapture()) {
            mCaptureBuffer.rewind();
            GLES32.glReadPixels(0, 0, screenWidth, screenHeight, GLES32.GL_RGBA, GLES32.GL_UNSIGNED_BYTE, mCaptureBuffer);
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
                    Uri shareUri = Uri.parse(MediaStore.Images.Media.insertImage(getContext().getContentResolver(), outBitmap,
                            "Image" + System.currentTimeMillis(), "ScreenCapture from Hi5"));

                    ImageInfoRepository.getInstance().getScreenCaptureFilePath().postValue(new FilePath<>(shareUri));
                }
            });

        }
    }

}
