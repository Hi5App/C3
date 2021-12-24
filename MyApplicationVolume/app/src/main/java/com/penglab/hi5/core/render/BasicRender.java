package com.penglab.hi5.core.render;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Jackiexing on 12/20/21
 */
public abstract class BasicRender implements GLSurfaceView.Renderer {

    abstract protected void initMatrix();
    abstract protected void setMatrixByFile();
    abstract protected void updateFinalMatrix();

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
