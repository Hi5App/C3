package com.penglab.hi5.core.render.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.core.render.AnnotationRender;

/**
 * Created by Jackiexing on 12/20/21
 */
public class AnnotationGLSurfaceView extends BasicGLSurfaceView{

    private final String TAG = "AnnotationGLSurfaceView";
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // ACTION_DOWN 不return true，就无触发后面的各个事件
        if (event != null) {
            final float currentX = toOpenGLCoord(this, event.getX(), true);
            final float currentY = toOpenGLCoord(this, event.getY(), false);

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = currentX;
                    lastY = currentY;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    requestRender();
                    isZooming = true;
                    isZoomingNotStop = true;
                    float x1 = toOpenGLCoord(this, event.getX(1), true);
                    float y1 = toOpenGLCoord(this, event.getY(1), false);

                    dis_start = computeDis(currentX, x1, currentY, y1);
                    dis_x_start = x1 - currentX;
                    dis_y_start = y1 - currentY;

                    x0_start = currentX;
                    y0_start = currentY;
                    x1_start = x1;
                    y1_start = y1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isZooming && isZoomingNotStop) {

                        float x2 = toOpenGLCoord(this, event.getX(1), true);
                        float y2 = toOpenGLCoord(this, event.getY(1), false);

                        double dis = computeDis(currentX, x2, currentY, y2);
                        double scale = dis / dis_start;
                        annotationRender.scale((float) scale);

                        float dis_x = x2 - currentX;
                        float dis_y = y2 - currentY;
                        float ave_x = (x2 - x1_start + currentX - x0_start) / 2;
                        float ave_y = (y2 - y1_start + currentY - y0_start) / 2;
                        annotationRender.rotate(ave_x, ave_y);

                        // 配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                        requestRender();
                        dis_start = dis;
                        dis_x_start = dis_x;
                        dis_y_start = dis_y;
                        x0_start = currentX;
                        y0_start = currentY;
                        x1_start = x2;
                        y1_start = y2;
                    } else if (!isZooming) {
                        annotationRender.rotate(currentX - lastX, currentY - lastY);

                        // 配合 GLSurfaceView.RENDERMODE_WHEN_DIRTY 使用
                        requestRender();
                        lastX = currentX;
                        lastY = currentY;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    isZoomingNotStop = false;
                    lastX = currentX;
                    lastY = currentY;
                    break;
                case MotionEvent.ACTION_UP:
                    requestRender();
                    isZooming = false;
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    public void loadFile(){
        annotationRender.loadFile();
    }

    public void loadAnnotationFile(){
        annotationRender.loadAnnotationFile();
    }

    public void zoomIn(){
        annotationRender.scale(2.0f);
        requestRender();
    }

    public void zoomOut(){
        annotationRender.scale(0.5f);
        requestRender();
    }

    public NeuronTree getNeuronTree(){
        return annotationRender.getNeuronTree();
    }
}
