package com.penglab.hi5.core.render.view;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.penglab.hi5.core.render.BasicRender;

/**
 * Created by Jackiexing on 12/20/21
 */
public class BasicGLSurfaceView extends GLSurfaceView {

    BasicRender basicRender;

    public BasicGLSurfaceView(Context context, BasicRender basicRender) {
        super(context);
        this.basicRender = basicRender;

        // 设置一下opengl版本；
        setEGLContextClientVersion(3);
        setRenderer(basicRender);

        // 调用 onPause 的时候保存EGLContext
        setPreserveEGLContextOnPause(true);

        // 当发生交互时重新执行渲染， 需要配合requestRender();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public BasicGLSurfaceView(Context context, AttributeSet attrs, BasicRender basicRender) {
        super(context, attrs);
        this.basicRender = basicRender;

        // 设置一下opengl版本；
        setEGLContextClientVersion(3);
        setRenderer(basicRender);

        // 调用 onPause 的时候保存EGLContext
        setPreserveEGLContextOnPause(true);

        // 当发生交互时重新执行渲染， 需要配合requestRender();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
