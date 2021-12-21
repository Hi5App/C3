//package com.penglab.hi5.core.ui.annotation;
//
//import static java.lang.Math.pow;
//import static java.lang.Math.sqrt;
//
//import android.annotation.SuppressLint;
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.pm.ConfigurationInfo;
//import android.opengl.GLSurfaceView;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//
//import androidx.annotation.RequiresApi;
//
//import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
//import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
//import com.penglab.hi5.core.MainActivity;
//import com.penglab.hi5.core.MyRenderer;
//import com.penglab.hi5.core.collaboration.Communicator;
//import com.penglab.hi5.core.game.Score;
//
//import java.util.ArrayList;
//import java.util.Vector;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
//public class AnnotationGLSurfaceView extends GLSurfaceView {
//
//    EditMode editMode;
//    MyRenderer myRenderer;
//
//    private float X, Y;
//    private double dis_start;
//    private float dis_x_start;
//    private float dis_y_start;
//    private boolean isZooming;
//    private boolean isZoomingNotStop;
//    private float x1_start;
//    private float x0_start;
//    private float y1_start;
//    private float y0_start;
//
//    boolean isBigData_Remote;
//    boolean ifGuestLogin;
//
//    private ArrayList<Float> lineDrawed = new ArrayList<Float>();
//
//    public AnnotationGLSurfaceView(Context context) {
//        super(context);
//
//        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
//
//        // 设置一下opengl版本；
//        setEGLContextClientVersion(3);
//
//        setRenderer(myRenderer);
//
//        // 调用 onPause 的时候保存EGLContext
//        setPreserveEGLContextOnPause(true);
//
//        // 当发生交互时重新执行渲染， 需要配合requestRender();
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
////            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//    }
//
//    //触摸屏幕的事件
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @SuppressLint("ClickableViewAccessibility")
//    public boolean onTouchEvent(MotionEvent motionEvent) {
//
//        try {
//
//            //ACTION_DOWN不return true，就无触发后面的各个事件
//            if (motionEvent != null) {
//                final float normalizedX = toOpenGLCoord(this, motionEvent.getX(), true);
//                final float normalizedY = toOpenGLCoord(this, motionEvent.getY(), false);
////
////                final float normalizedX =motionEvent.getX();
////                final float normalizedY =motionEvent.getY();
//
//                switch (motionEvent.getActionMasked()) {
//                    case MotionEvent.ACTION_DOWN:
//                        X = normalizedX;
//                        Y = normalizedY;
//                        if (editMode == EditMode.PAINTCURVE || editMode == EditMode.DELETECURVE
//                                || editMode == EditMode.SPLIT || editMode == EditMode.DELETEMULTIMARKER
//                                || editMode == EditMode.CHANGECURVETYPE || editMode == EditMode.ZOOMINROI) {
//                            lineDrawed.add(X);
//                            lineDrawed.add(Y);
//                            lineDrawed.add(-1.0f);
//                            myRenderer.setIfPainting(true);
//                            requestRender();
//                        }
//                        break;
//                    case MotionEvent.ACTION_POINTER_DOWN:
//                        lineDrawed.clear();
//                        myRenderer.setIfPainting(false);
//                        requestRender();
//                        isZooming = true;
//                        isZoomingNotStop = true;
//                        float x1 = toOpenGLCoord(this, motionEvent.getX(1), true);
//                        float y1 = toOpenGLCoord(this, motionEvent.getY(1), false);
//
////                        float x1=motionEvent.getX(1);
////                        float y1=motionEvent.getY(1);
//                        dis_start = computeDis(normalizedX, x1, normalizedY, y1);
//                        dis_x_start = x1 - normalizedX;
//                        dis_y_start = y1 - normalizedY;
//
//                        x0_start = normalizedX;
//                        y0_start = normalizedY;
//                        x1_start = x1;
//                        y1_start = y1;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if (isZooming && isZoomingNotStop) {
//
//                            float x2 = toOpenGLCoord(this, motionEvent.getX(1), true);
//                            float y2 = toOpenGLCoord(this, motionEvent.getY(1), false);
//
//                            double dis = computeDis(normalizedX, x2, normalizedY, y2);
//                            double scale = dis / dis_start;
//
//                            myRenderer.zoom((float) scale);
//
//                            float dis_x = x2 - normalizedX;
//                            float dis_y = y2 - normalizedY;
//                            float ave_x = (x2 - x1_start + normalizedX - x0_start) / 2;
//                            float ave_y = (y2 - y1_start + normalizedY - y0_start) / 2;
//                            if (!(myRenderer.getFileType() == MyRenderer.FileType.JPG || myRenderer.getFileType() == MyRenderer.FileType.PNG)) {
//                                if (!myRenderer.getIfDownSampling())
//                                    myRenderer.setIfDownSampling(true);
//                            }
////                            if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker){
////                                myrenderer.rotate2f(dis_x_start, dis_x, dis_y_start, dis_y);
////                            }else {
////                                myrenderer.rotate(dis_x - dis_x_start, dis_y - dis_y_start, (float) (computeDis(dis_x, dis_x_start, dis_y, dis_y_start)));
//                            myRenderer.rotate(ave_x, ave_y, (float)(computeDis((x2 + normalizedX) / 2, (x1_start + x0_start) / 2, (y2 + normalizedY) / 2, (y1_start + y0_start) / 2)));
////                            }
//                            //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
//                            requestRender();
//                            dis_start = dis;
//                            dis_x_start = dis_x;
//                            dis_y_start = dis_y;
//                            x0_start = normalizedX;
//                            y0_start = normalizedY;
//                            x1_start = x2;
//                            y1_start = y2;
//                        } else if (!isZooming){
//                            if (editMode == EditMode.NONE) {
//                                if (!(myRenderer.getFileType() == MyRenderer.FileType.JPG || myRenderer.getFileType() == MyRenderer.FileType.PNG)) {
//                                    if (!myRenderer.getIfDownSampling())
//                                        myRenderer.setIfDownSampling(true);
//                                }
//                                myRenderer.rotate(normalizedX - X, normalizedY - Y, (float) (computeDis(normalizedX, X, normalizedY, Y)));
//
//                                //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
//                                requestRender();
//                                X = normalizedX;
//                                Y = normalizedY;
//                            } else {
//                                lineDrawed.add(normalizedX);
//                                lineDrawed.add(normalizedY);
//                                lineDrawed.add(-1.0f);
//
//                                myRenderer.setLineDrawed(lineDrawed);
//                                requestRender();
//
//                                invalidate();
//                            }
//                        }
//                        break;
//                    case MotionEvent.ACTION_POINTER_UP:
////                        isZooming = false;
//                        isZoomingNotStop = false;
//                        myRenderer.setIfDownSampling(false);
//                        X = normalizedX;
//                        Y = normalizedY;
//                        lineDrawed.clear();
//                        myRenderer.setIfPainting(false);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        if (!isZooming) {
//                            try {
//                                if (editMode == EditMode.ZOOM) {
//                                    editMode = EditMode.NONE;
//                                    float [] center = myRenderer.solveMarkerCenter(normalizedX, normalizedY);
//                                    if (center != null) {
//                                        Communicator communicator = Communicator.getInstance();
//                                        communicator.navigateAndZoomInBlock((int) center[0] - 64, (int) center[1] - 64, (int) center[2] - 64);
//                                        requestRender();
//                                    }
//                                }
//                                if (editMode == EditMode.PINPOINT) {
//                                    if(ifGuestLogin == false)
//                                    {
//                                        Score scoreInstance = Score.getInstance();
//                                        scoreInstance.pinpoint();
//                                    }
//                                    if (myRenderer.getFileType() == MyRenderer.FileType.JPG || myRenderer.getFileType() == MyRenderer.FileType.PNG)
//                                        myRenderer.add2DMarker(normalizedX, normalizedY);
//                                    else {
//                                        myRenderer.setMarkerDrawed(normalizedX, normalizedY, isBigData_Remote);
//                                    }
//                                    requestRender();
//                                }
//                                if (editMode == EditMode.DELETEMARKER) {
//                                    myRenderer.deleteMarkerDrawed(normalizedX, normalizedY, isBigData_Remote);
//                                    requestRender();
//                                }
//                                if (editMode == EditMode.DELETEMULTIMARKER) {
//                                    myRenderer.deleteMultiMarkerByStroke(lineDrawed, isBigData_Remote);
//                                    requestRender();
//                                }
//                                if (editMode == EditMode.CHANGEMARKERTYPE) {
//                                    myRenderer.changeMarkerType(normalizedX, normalizedY, isBigData_Remote);
//                                    requestRender();
//                                }
//                                if (editMode == EditMode.PAINTCURVE) {
//                                    Vector<Integer> segids = new Vector<>();
//                                    myRenderer.setIfPainting(false);
//                                    if(ifGuestLogin == false)
//                                    {
//                                        Score scoreInstance = Score.getInstance();
//                                        scoreInstance.drawACurve();
//                                    }
//
//                                    if (myRenderer.getFileType() == MyRenderer.FileType.JPG || myRenderer.getFileType() == MyRenderer.FileType.PNG)
//                                        myRenderer.add2DCurve(lineDrawed);
//                                    else {
//                                        Callable<String> task = new Callable<String>() {
//                                            @RequiresApi(api = Build.VERSION_CODES.N)
//                                            @Override
//                                            public String call() throws Exception {
//                                                int lineType = myRenderer.getLastLineType();
//                                                V_NeuronSWC_list[] v_neuronSWC_list = new V_NeuronSWC_list[1];
//                                                V_NeuronSWC seg = myRenderer.addBackgroundLineDrawed(lineDrawed, v_neuronSWC_list);
//                                                System.out.println("feature");
//                                                if (seg != null)
//                                                { myRenderer.addLineDrawed2(lineDrawed, seg, isBigData_Remote);
//                                                    myRenderer.deleteFromCur(seg, v_neuronSWC_list[0]);
//                                                }
//                                                requestRender();
//                                                return "succeed";
//                                            }
//                                        };
//                                        ExecutorService exeService = Executors.newSingleThreadExecutor();
//                                        Future<String> future = exeService.submit(task);
//                                        try {
//                                            String result = future.get(1500, TimeUnit.MILLISECONDS);
//                                            System.err.println("Result:" + result);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                            System.out.println("unfinished in 1.5 seconds");
//                                        }
//                                    }
//
//                                    lineDrawed.clear();
//                                    myRenderer.setLineDrawed(lineDrawed);
//
//                                    requestRender();
//                                }
//
//                                if(editMode == EditMode.ZOOMINROI){
//                                    editMode = EditMode.NONE;
//                                    float [] center = myRenderer.GetROICenter(lineDrawed,isBigData_Remote);
//                                    if (center != null) {
//                                        Communicator communicator = Communicator.getInstance();
//                                        communicator.navigateAndZoomInBlock((int) center[0] - 64, (int) center[1] - 64, (int) center[2] - 64);
//                                        requestRender();
//                                    }
//                                }
//
//                                if (editMode == EditMode.DELETECURVE) {
//                                    myRenderer.setIfPainting(false);
//                                    myRenderer.deleteLine1(lineDrawed, isBigData_Remote);
//                                    lineDrawed.clear();
//                                    myRenderer.setLineDrawed(lineDrawed);
//                                    requestRender();
//                                }
//
//                                if (editMode == EditMode.SPLIT) {
//                                    myRenderer.setIfPainting(false);
//                                    myRenderer.splitCurve(lineDrawed, isBigData_Remote);
//                                    lineDrawed.clear();
//                                    myRenderer.setLineDrawed(lineDrawed);
//                                    requestRender();
//                                }
//
//                                if (editMode == EditMode.CHANGECURVETYPE) {
//                                    myRenderer.setIfPainting(false);
//                                    int type = myRenderer.getLastLineType();
//                                    myRenderer.changeLineType(lineDrawed, type, isBigData_Remote);
//                                    lineDrawed.clear();
//                                    myRenderer.setLineDrawed(lineDrawed);
//                                    requestRender();
//                                }
//                            } catch (Exception e){
//                                e.printStackTrace();
//                            }
//                            lineDrawed.clear();
//                            myRenderer.setIfPainting(false);
//                        }
//                        lineDrawed.clear();
//                        myRenderer.setIfPainting(false);
//                        requestRender();
//                        isZooming = false;
//                        myRenderer.setIfDownSampling(false);
//                        break;
//                    default:
//                        break;
//                }
//                return true;
//            }
//
//        }catch (IllegalArgumentException e){
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//
//    // 坐标系变换
//    private float toOpenGLCoord(View view, float value, boolean isWidth) {
//        if (isWidth) {
//            return (value / (float) view.getWidth()) * 2 - 1;
//        } else {
//            return -((value / (float) view.getHeight()) * 2 - 1);
//        }
//    }
//
//
//    // 距离计算
//    private double computeDis(float x1, float x2, float y1, float y2) {
//        return sqrt(pow((x2 - x1), 2) + pow((y2 - y1), 2));
//    }
//}
