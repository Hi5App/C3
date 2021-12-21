package com.penglab.hi5.core.render;

import static javax.microedition.khronos.opengles.GL10.GL_ALPHA_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;

import android.opengl.GLES10;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.core.render.pattern.MyAxis;
import com.penglab.hi5.core.render.pattern.MyDraw;
import com.penglab.hi5.core.render.pattern.MyNavLoc;
import com.penglab.hi5.core.render.pattern.MyPattern;
import com.penglab.hi5.core.render.pattern.MyPattern2D;
import com.penglab.hi5.core.render.pattern.MyPatternGame;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jackiexing on 12/20/21
 */
public class AnnotationRender extends BasicRender{

    private final String TAG = "AnnotationRender";

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG,"onSurfaceCreated successfully");

        GLES30.glClearColor(121f/255f, 134f/255f, 203f/255f, 1.0f);

        // init shader program
//        MyPattern.initProgram();
//        MyPattern2D.initProgram();
//        MyAxis.initProgram();
//        MyDraw.initProgram();
//        MyNavLoc.initProgram();
//        MyPatternGame.initProgram();

//        // init matrix
//        Matrix.setIdentityM(translateMatrix,0);//建立单位矩阵
//        Matrix.setIdentityM(zoomMatrix,0);//建立单位矩阵
//        Matrix.setIdentityM(zoomAfterMatrix, 0);
//        Matrix.setIdentityM(rotationMatrix, 0);
//        Matrix.setRotateM(rotationMatrix, 0, 0, -1.0f, -1.0f, 0.0f);
//
//        // Set the camera position (View matrix)
//        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -2, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

//        if (width > height) {
//            Matrix.orthoM(paraProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 100);
//            Matrix.frustumM(persProjectionMatrix, 0, -ratio, ratio, -1, 1, 2f, 100);
//        } else {
//            Matrix.orthoM(paraProjectionMatrix, 0, -1, 1, -1 / ratio, 1 / ratio, 1, 100);
//            Matrix.frustumM(persProjectionMatrix, 0, -1, 1, -1 / ratio, 1 / ratio, 2f, 100);
//        }

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
    }
}
