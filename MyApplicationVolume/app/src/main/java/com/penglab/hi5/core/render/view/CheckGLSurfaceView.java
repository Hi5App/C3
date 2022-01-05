package com.penglab.hi5.core.render.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.penglab.hi5.core.MyRenderer;
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.BasicRender;
import com.penglab.hi5.core.render.CheckRender;

public class CheckGLSurfaceView extends BasicGLSurfaceView{

    private CheckRender checkRender;

    private final int DOUBLE_TAP_TIMEOUT = 200;

    private boolean isMove = false;

    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;

    private OnDoubleClickListener onDoubleClickListener;

    public CheckGLSurfaceView(Context context) {
        super(context);
    }

    public CheckGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkRender = new CheckRender();

        // 设置一下opengl版本；
        setEGLContextClientVersion(3);
        setRenderer(checkRender);

        // 调用 onPause 的时候保存EGLContext
        setPreserveEGLContextOnPause(true);

        // 当发生交互时重新执行渲染， 需要配合requestRender();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    public CheckGLSurfaceView(Context context, BasicRender basicRender) {
        super(context, basicRender);
    }

    public CheckGLSurfaceView(Context context, AttributeSet attrs, BasicRender basicRender) {
        super(context, attrs, basicRender);
        setRenderer(checkRender);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            if (motionEvent != null) {
                final float currentX = toOpenGLCoord(this, motionEvent.getX(), true);
                final float currentY = toOpenGLCoord(this, motionEvent.getY(), false);

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = currentX;
                        lastY = currentY;

                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        requestRender();
                        isZooming = true;
                        isZoomingNotStop = true;
                        float x1 = toOpenGLCoord(this, motionEvent.getX(1), true);
                        float y1 = toOpenGLCoord(this, motionEvent.getY(1), false);

                        dis_start = computeDis(currentX, x1, currentY, y1);
                        dis_x_start = x1 - currentX;
                        dis_y_start = y1 - currentY;

                        x0_start = currentX;
                        y0_start = currentY;
                        x1_start = x1;
                        y1_start = y1;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMove = true;
                        if (isZooming && isZoomingNotStop) {

                            float x2 = toOpenGLCoord(this, motionEvent.getX(1), true);
                            float y2 = toOpenGLCoord(this, motionEvent.getY(1), false);

                            double dis = computeDis(currentX, x2, currentY, y2);
                            double scale = dis / dis_start;

                            checkRender.zoom((float) scale);

                            float dis_x = x2 - currentX;
                            float dis_y = y2 - currentY;
                            float ave_x = (x2 - x1_start + currentX - x0_start) / 2;
                            float ave_y = (y2 - y1_start + currentY - y0_start) / 2;
//                            if (!(checkRender.getFileType() == MyRenderer.FileType.JPG || checkRender.getFileType() == MyRenderer.FileType.PNG)) {
//                                if (!checkRender.getIfDownSampling())
//                                    checkRender.setIfDownSampling(true);
//                            }
                            checkRender.rotate(ave_x, ave_y);
                            requestRender();
                            dis_start = dis;
                            dis_x_start = dis_x;
                            dis_y_start = dis_y;
                            x0_start = currentX;
                            y0_start = currentY;
                            x1_start = x2;
                            y1_start = y2;
                        } else if (!isZooming) {
//                            if (!(checkRender.getFileType() == MyRenderer.FileType.JPG || checkRender.getFileType() == MyRenderer.FileType.PNG)) {
//                                if (!checkRender.getIfDownSampling())
//                                    checkRender.setIfDownSampling(true);
//                            }
                            checkRender.rotate(currentX - lastX, currentY - lastY);

                            requestRender();
                            lastX = currentX;
                            lastY = currentY;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:

                        isZoomingNotStop = false;
//                        checkRender.setIfDownSampling(false);
                        lastX = currentX;
                        lastY = currentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isMove) {
                            if (mPreviousUpEvent != null && isConsideredDoubleTap(mPreviousUpEvent, motionEvent)) {
//                                int [] newCenter = checkRender.newCenterWhenNavigateWhenClick(currentX, currentY);
//                                onDoubleClickListener.run(newCenter);
                            }
                            mPreviousUpEvent = motionEvent;
                        } else {
                            mPreviousUpEvent = null;
                        }
                        isMove = false;
                        requestRender();
                        isZooming = false;
//                        checkRender.setIfDownSampling(false);
                        break;
                    default:
                        break;
                }
                return true;
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isConsideredDoubleTap(MotionEvent firstUp, MotionEvent secondUp){
        if (secondUp.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
            return false;
        }
        int deltaX =(int) firstUp.getX() - (int)secondUp.getX();
        int deltaY =(int) firstUp.getY()- (int)secondUp.getY();
        return deltaX * deltaX + deltaY * deltaY < 10000;
    }

    public void loadImageFile(){
        checkRender.loadFile();
        requestRender();
    }

    public void loadAnnotationFile() {
        checkRender.loadAnnotationFile();
        requestRender();
    }

    public interface OnDoubleClickListener {
        void run(int [] center);
    }

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener;
    }
}
