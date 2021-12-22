package com.penglab.hi5.core.render.view;

import android.content.Context;
import android.util.AttributeSet;

import com.penglab.hi5.core.render.AnnotationRender;

/**
 * Created by Jackiexing on 12/20/21
 */
public class AnnotationGLSurfaceView extends BasicGLSurfaceView{

    public AnnotationGLSurfaceView(Context context) {
        super(context, new AnnotationRender());
    }

    public AnnotationGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs, new AnnotationRender());
    }

    public void openFile(){

    }
}
