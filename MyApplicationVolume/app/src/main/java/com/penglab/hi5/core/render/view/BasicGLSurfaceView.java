package com.penglab.hi5.core.render.view;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Jackiexing on 12/20/21
 */
public class BasicGLSurfaceView extends GLSurfaceView {

    protected float lastX, lastY;
    protected double dis_start;
    protected float dis_x_start;
    protected float dis_y_start;
    protected boolean isZooming;
    protected boolean isZoomingNotStop;
    protected float x1_start;
    protected float x0_start;
    protected float y1_start;
    protected float y0_start;

    public BasicGLSurfaceView(Context context) {
        super(context);
    }

    public BasicGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 坐标系变换
    protected float toOpenGLCoord(View view, float value, boolean isWidth) {
        if (isWidth) {
            return (value / (float) view.getWidth()) * 2 - 1;
        } else {
            return -((value / (float) view.getHeight()) * 2 - 1);
        }
    }

    // 距离计算
    protected double computeDis(float x1, float x2, float y1, float y2) {
        return sqrt(pow((x2 - x1), 2) + pow((y2 - y1), 2));
    }
}
