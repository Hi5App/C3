package com.penglab.hi5.core.render;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static javax.microedition.khronos.opengles.GL10.GL_ALPHA_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.core.render.pattern.MyPattern;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.model.img.BasicFile;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;

import java.util.Arrays;
import java.util.Collections;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jackiexing on 12/20/21
 */
public class AnnotationRender extends BasicRender{

    private final String TAG = "AnnotationRender";
    private final ImageInfoRepository imageInfoRepository = ImageInfoRepository.getInstance();

    private final float[] persProjectionMatrix = new float[16];      // for 3D
    private final float[] orthProjectionMatrix = new float[16];      // for 2D
    private final float[] viewMatrix           = new float[16];
    private final float[] vPMatrix             = new float[16];
    private final float[] translate2Matrix     = new float[16];
    private final float[] zoomMatrix           = new float[16];
    private final float[] rotateMatrix         = new float[16];
    private final float[] translateMatrix      = new float[16];
    private final float[] modelMatrix          = new float[16];
    private final float[] finalMatrix          = new float[16];

    private final float[] normalizedDim = new float[3];

    private Image4DSimple image4DSimple;

    private MyPattern myPattern = new MyPattern();

    private int screenWidth;
    private int screenHeight;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG,"onSurfaceCreated successfully");

        GLES30.glClearColor(121f/255f, 134f/255f, 203f/255f, 1.0f);

        // init shader program
        MyPattern.initProgram();
//        MyPattern2D.initProgram();
//        MyAxis.initProgram();
//        MyDraw.initProgram();
//        MyNavLoc.initProgram();

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
        Matrix.translateM(translateMatrix, 0, -0.5f * normalizedDim[0], -0.5f * normalizedDim[1], -0.5f * normalizedDim[2]);

        // Set translate2Matrix
        Matrix.setIdentityM(translate2Matrix, 0);
        Matrix.translateM(translate2Matrix, 0, 0, 0, 1.0f / 2 * (float) Math.sqrt(3));
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

        Log.e(TAG,"finalMatrix " + Arrays.toString(finalMatrix));
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
                    updateNormalizedDim(new Integer[]{
                            (int) image4DSimple.getSz0(), (int) image4DSimple.getSz1(), (int) image4DSimple.getSz2()});
                    myPattern.setNeedSet(true);
                }
                break;
            default:
                ToastEasy("Unsupported file !");
        }

        initMatrix();
        setMatrixByFile();
        updateFinalMatrix();
    }

    private void setResource(){
        if (myPattern.isNeedSet()){
//            myPattern = new MyPattern(screenWidth, screenHeight, image4DSimple, normalizedDim, MyPattern.Mode.NORMAL);
//            myPattern.setNeedDraw(true);
//            myPattern.setNeedSet(false);
            myPattern.setImage(image4DSimple, screenWidth, screenHeight, normalizedDim);
        }
    }

    private void drawFrame(){
        if (myPattern.isNeedDraw()){
            myPattern.drawVolume_3d(finalMatrix, false, 1.0f);
        }
    }

    private void updateNormalizedDim(Integer[] size){
        float maxDim = (float) Collections.max(Arrays.asList(size));

        normalizedDim[0] = (float) size[0] / maxDim;
        normalizedDim[1] = (float) size[1] / maxDim;
        normalizedDim[2] = (float) size[2] / maxDim;

        Log.e(TAG,"max dim " + maxDim);
        Log.e(TAG,Arrays.toString(normalizedDim));
    }

}
