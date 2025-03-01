package com.penglab.hi5.core.render.utils;

import android.content.SharedPreferences;
import android.opengl.Matrix;
import android.util.Log;

import com.penglab.hi5.basic.MyAnimation;
import com.penglab.hi5.data.dataStore.PreferenceSetting;

import java.util.Arrays;

/**
 * Created by Jackiexing on 12/25/21
 */
public class MatrixManager {

    private final String TAG = "MatrixManager";

    private float[] projectionMatrix           = new float[16];
    private final float[] persProjectionMatrix = new float[16];      // for 3D
    private final float[] orthProjectionMatrix = new float[16];      // for 2D
    private final float[] viewMatrix           = new float[16];
    private final float[] translate2Matrix     = new float[16];
    private final float[] zoomMatrix           = new float[16];
    private float[] rotateMatrix         = new float[16];
    private final float[] translateMatrix      = new float[16];
    private final float[] modelMatrix          = new float[16];
    private final float[] modelTempMatrix      = new float[16];
    private final float[] finalMatrix          = new float[16];
    private final float[] finalTempMatrix      = new float[16];

    private final MyAnimation myAnimation = new MyAnimation();

    public void clone(MatrixManager other){
        this.projectionMatrix = other.projectionMatrix;
        System.arraycopy(other.persProjectionMatrix, 0, this.persProjectionMatrix, 0, other.persProjectionMatrix.length);
        System.arraycopy(other.orthProjectionMatrix, 0, this.orthProjectionMatrix, 0, other.orthProjectionMatrix.length);
        System.arraycopy(other.viewMatrix, 0, this.viewMatrix, 0, other.viewMatrix.length);
        System.arraycopy(other.translate2Matrix, 0, this.translate2Matrix, 0, other.translate2Matrix.length);
        System.arraycopy(other.zoomMatrix, 0, this.zoomMatrix, 0, other.zoomMatrix.length);
        System.arraycopy(other.rotateMatrix, 0, this.rotateMatrix, 0, other.rotateMatrix.length);
        System.arraycopy(other.translateMatrix, 0, this.translateMatrix, 0, other.translateMatrix.length);
        System.arraycopy(other.modelMatrix, 0, this.modelMatrix, 0, other.modelMatrix.length);
        System.arraycopy(other.modelTempMatrix, 0, this.modelTempMatrix, 0, other.modelTempMatrix.length);
        System.arraycopy(other.finalMatrix, 0, this.finalMatrix, 0, other.finalMatrix.length);
        System.arraycopy(other.finalTempMatrix, 0, this.finalTempMatrix, 0, other.finalTempMatrix.length);
    }
    public void initMatrix() {
        // set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -2, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // init model matrix
        Matrix.setIdentityM(translateMatrix,0);
        Matrix.setIdentityM(zoomMatrix,0);

        Matrix.setIdentityM(rotateMatrix, 0);
        Matrix.setIdentityM(finalTempMatrix, 0);
        Matrix.setIdentityM(modelTempMatrix, 0);
        Matrix.setRotateM(rotateMatrix, 0, 0, -1.0f, -1.0f, 0.0f);
    }

    public void setMatrixByFile(float[] normalizedSize, float scale, boolean is2DImage) {
        // Set ProjectionMatrix
        if (is2DImage){
            projectionMatrix = orthProjectionMatrix;
        } else {
            projectionMatrix = persProjectionMatrix;
        }

        // Set translateMatrix
        Matrix.translateM(translateMatrix, 0, -0.5f * normalizedSize[0], -0.5f * normalizedSize[1], -0.5f * normalizedSize[2]);

        // Set translate2Matrix
        Matrix.setIdentityM(translate2Matrix, 0);
        Matrix.translateM(translate2Matrix, 0, 0, 0, scale / 2 * (float) Math.sqrt(3));

        // Set zoomMatrix
        Matrix.scaleM(zoomMatrix, 0, scale, scale, scale);
    }

    public void updateFinalMatrix(){
        // Calculate the projection and view transformation
        Matrix.multiplyMM(finalTempMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // model = zoom * rotate * translate
        // Calculate model matrix
        Matrix.multiplyMM(modelTempMatrix, 0, rotateMatrix, 0, translateMatrix, 0);
        Matrix.multiplyMM(modelTempMatrix, 0, zoomMatrix, 0, modelTempMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, translate2Matrix, 0, modelTempMatrix, 0);

        // Calculate final matrix
        Matrix.multiplyMM(finalMatrix, 0, finalTempMatrix, 0, modelMatrix, 0);

    }

    public void initMatrixByFile(float[] normalizedSize, float scale, boolean is2DImage){
        initMatrix();
        setMatrixByFile(normalizedSize, scale, is2DImage);
        updateFinalMatrix();
    }

    public void setProjectionMatrix(int screenWidth, int screenHeight){
        float ratio = (float) screenWidth / screenHeight;

        if (screenWidth > screenHeight) {
            Matrix.orthoM(orthProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
            Matrix.frustumM(persProjectionMatrix, 0, -ratio, ratio, -1, 1, 2f, 100);
        } else {
            Matrix.orthoM(orthProjectionMatrix, 0, -1, 1, -1 / ratio, 1 / ratio, 1, 100);
            Matrix.frustumM(persProjectionMatrix, 0, -1, 1, -1 / ratio, 1 / ratio, 2f, 100);
        }
    }

    public void zoom(float ratio, RenderOptions renderOptions){
        float curScale = renderOptions.getScale();
        if ((curScale >= 0.2 && curScale <= 30) || (curScale > 30 && ratio < 1) || (curScale < 0.2 && ratio > 1)){
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

    public void saveRotationMatrix(){
        PreferenceSetting prefs = PreferenceSetting.getInstance();
        prefs.setRotationMatrix(rotateMatrix);
    }

    public void setRotationMatrix(){
        PreferenceSetting prefs = PreferenceSetting.getInstance();
        rotateMatrix = prefs.getRotationMatrix();
//        System.out.println(rotateMatrix);
    }

    public void rotate(float[] matrix){
        System.arraycopy(matrix, 0, this.rotateMatrix, 0, matrix.length);
        updateFinalMatrix();
    }

    public float[] getFinalMatrix(){
        return finalMatrix;
    }

    public float[] getModelMatrix(){
        return modelMatrix;
    }

    public void autoRotate(){
        if (myAnimation.getStatus()){
            Matrix.multiplyMM(rotateMatrix, 0, rotateMatrix, 0, myAnimation.Rotation(), 0);
            updateFinalMatrix();
        }
    }

    public void autoRotateStart(){
        myAnimation.quickStart();
    }

    public void autoRotateStop(){
        myAnimation.quickStop();
    }

}
