package com.penglab.hi5.core.render.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.penglab.hi5.core.render.AnnotationRender;

/**
 * Created by Jackiexing on 12/20/21
 */
public class AnnotationGLSurfaceView extends BasicGLSurfaceView{

    AnnotationRender annotationRender;

    public AnnotationGLSurfaceView(Context context) {
        super(context, new AnnotationRender());
    }

    public AnnotationGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        annotationRender = new AnnotationRender();

        // 设置一下opengl版本；
        setEGLContextClientVersion(3);
        setRenderer(annotationRender);

        // 调用 onPause 的时候保存EGLContext
        setPreserveEGLContextOnPause(true);

        // 当发生交互时重新执行渲染， 需要配合requestRender();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void loadFile(){
        annotationRender.loadFile();
    }
}
