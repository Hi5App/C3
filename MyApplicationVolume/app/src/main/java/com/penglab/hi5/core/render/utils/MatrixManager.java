package com.penglab.hi5.core.render.utils;

import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by Jackiexing on 12/25/21
 */
public class MatrixManager {

    private final String TAG = "MatrixManager";

    private final float[] persProjectionMatrix = new float[16];      // for 3D
    private final float[] orthProjectionMatrix = new float[16];      // for 2D
    private final float[] viewMatrix           = new float[16];
    private final float[] translate2Matrix     = new float[16];
    private final float[] zoomMatrix           = new float[16];
    private final float[] rotateMatrix         = new float[16];
    private final float[] translateMatrix      = new float[16];
    private final float[] modelMatrix          = new float[16];
    private final float[] modelTempMatrix      = new float[16];
    private final float[] finalMatrix          = new float[16];
    private final float[] finalTempMatrix      = new float[16];

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

    public void setMatrixByFile(float[] normalizedSize, float scale) {
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
        Matrix.multiplyMM(finalTempMatrix, 0, persProjectionMatrix, 0, viewMatrix, 0);

        // model = zoom * rotate * translate
        // Calculate model matrix
        Matrix.multiplyMM(modelTempMatrix, 0, rotateMatrix, 0, translateMatrix, 0);
        Matrix.multiplyMM(modelTempMatrix, 0, zoomMatrix, 0, modelTempMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, translate2Matrix, 0, modelTempMatrix, 0);

        // Calculate final matrix
        Matrix.multiplyMM(finalMatrix, 0, finalTempMatrix, 0, modelMatrix, 0);

    }

    public void initMatrixByFile(float[] normalizedSize, float scale){
        initMatrix();
        setMatrixByFile(normalizedSize, scale);
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

    public float[] getFinalMatrix(){
        return finalMatrix;
    }

    public float[] getModelMatrix(){
        return modelMatrix;
    }

}
