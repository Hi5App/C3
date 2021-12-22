package com.penglab.hi5.core.render.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;

import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.MyRenderer;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.game.Score;
import com.penglab.hi5.core.render.BasicRender;

import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CheckGLSurfaceView extends BasicGLSurfaceView{

    MyRenderer myrenderer;

    public CheckGLSurfaceView(Context context) {
        super(context);
    }

    public CheckGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckGLSurfaceView(Context context, BasicRender basicRender) {
        super(context, basicRender);
    }

    public CheckGLSurfaceView(Context context, AttributeSet attrs, BasicRender basicRender) {
        super(context, attrs, basicRender);
        setRenderer(myrenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            //ACTION_DOWN不return true，就无触发后面的各个事件
            if (motionEvent != null) {
                final float normalizedX = toOpenGLCoord(this, motionEvent.getX(), true);
                final float normalizedY = toOpenGLCoord(this, motionEvent.getY(), false);
//
//                final float normalizedX =motionEvent.getX();
//                final float normalizedY =motionEvent.getY();

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        X = normalizedX;
                        Y = normalizedY;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        requestRender();
                        isZooming = true;
                        isZoomingNotStop = true;
                        float x1 = toOpenGLCoord(this, motionEvent.getX(1), true);
                        float y1 = toOpenGLCoord(this, motionEvent.getY(1), false);

//                        float x1=motionEvent.getX(1);
//                        float y1=motionEvent.getY(1);
                        dis_start = computeDis(normalizedX, x1, normalizedY, y1);
                        dis_x_start = x1 - normalizedX;
                        dis_y_start = y1 - normalizedY;

                        x0_start = normalizedX;
                        y0_start = normalizedY;
                        x1_start = x1;
                        y1_start = y1;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isZooming && isZoomingNotStop) {

                            float x2 = toOpenGLCoord(this, motionEvent.getX(1), true);
                            float y2 = toOpenGLCoord(this, motionEvent.getY(1), false);

                            double dis = computeDis(normalizedX, x2, normalizedY, y2);
                            double scale = dis / dis_start;

                            myrenderer.zoom((float) scale);

                            float dis_x = x2 - normalizedX;
                            float dis_y = y2 - normalizedY;
                            float ave_x = (x2 - x1_start + normalizedX - x0_start) / 2;
                            float ave_y = (y2 - y1_start + normalizedY - y0_start) / 2;
                            if (!(myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)) {
                                if (!myrenderer.getIfDownSampling())
                                    myrenderer.setIfDownSampling(true);
                            }
//                            if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker){
//                                myrenderer.rotate2f(dis_x_start, dis_x, dis_y_start, dis_y);
//                            }else {
//                                myrenderer.rotate(dis_x - dis_x_start, dis_y - dis_y_start, (float) (computeDis(dis_x, dis_x_start, dis_y, dis_y_start)));
                            myrenderer.rotate(ave_x, ave_y, (float) (computeDis((x2 + normalizedX) / 2, (x1_start + x0_start) / 2, (y2 + normalizedY) / 2, (y1_start + y0_start) / 2)));
//                            }
                            //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                            requestRender();
                            dis_start = dis;
                            dis_x_start = dis_x;
                            dis_y_start = dis_y;
                            x0_start = normalizedX;
                            y0_start = normalizedY;
                            x1_start = x2;
                            y1_start = y2;
                        } else if (!isZooming) {
                            if (!(myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)) {
                                if (!myrenderer.getIfDownSampling())
                                    myrenderer.setIfDownSampling(true);
                            }
                            myrenderer.rotate(normalizedX - X, normalizedY - Y, (float) (computeDis(normalizedX, X, normalizedY, Y)));

                            //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                            requestRender();
                            X = normalizedX;
                            Y = normalizedY;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
//                        isZooming = false;
                        isZoomingNotStop = false;
                        myrenderer.setIfDownSampling(false);
                        X = normalizedX;
                        Y = normalizedY;
                        myrenderer.setIfPainting(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        myrenderer.setIfPainting(false);
                        requestRender();
                        isZooming = false;
                        myrenderer.setIfDownSampling(false);
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
}