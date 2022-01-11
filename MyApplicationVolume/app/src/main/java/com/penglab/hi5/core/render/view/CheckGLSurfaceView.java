package com.penglab.hi5.core.render.view;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;

import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.core.MyRenderer;
import com.penglab.hi5.core.fileReader.annotationReader.ApoReader;
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.BasicRender;
import com.penglab.hi5.core.render.CheckRender;
import com.penglab.hi5.core.render.utils.AnnotationDataManager;
import com.penglab.hi5.core.render.utils.AnnotationHelper;
import com.penglab.hi5.core.render.utils.MatrixManager;
import com.penglab.hi5.core.render.utils.RenderOptions;
import com.penglab.hi5.core.ui.check.CheckArborInfoState;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.model.img.BasicFile;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;

import java.util.Arrays;
import java.util.Collections;

public class CheckGLSurfaceView extends BasicGLSurfaceView{

    private final String TAG = "CheckGLSurfaceView";
    private final int DOUBLE_TAP_TIMEOUT = 200;
    private boolean isMove = false;

    private final RenderOptions renderOptions = new RenderOptions();
    private final MatrixManager matrixManager = new MatrixManager();
    private final AnnotationDataManager annotationDataManager = new AnnotationDataManager();
    private final AnnotationHelper annotationHelper = new AnnotationHelper(annotationDataManager, matrixManager);
    private final CheckRender checkRender = new CheckRender(annotationDataManager, matrixManager, renderOptions);

    private final CheckArborInfoState checkArborInfoState = CheckArborInfoState.getInstance();
    private final ImageInfoRepository imageInfoRepository = ImageInfoRepository.getInstance();
    private Image4DSimple image4DSimple;
    private final float[] normalizedSize = new float[3];
    private final int[] originalSize = new int[3];

    private MotionEvent mCurrentDownEvent;
    private MotionEvent mPreviousUpEvent;
    private long previousUpTime = -1;
    private float previousUpX;
    private float previousUpY;

    private OnDoubleClickListener onDoubleClickListener;

    public CheckGLSurfaceView(Context context) {
        super(context);
    }

    public CheckGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 设置一下opengl版本；
        setEGLContextClientVersion(3);
        setRenderer(checkRender);

        // 调用 onPause 的时候保存EGLContext
        setPreserveEGLContextOnPause(true);

        // 当发生交互时重新执行渲染， 需要配合requestRender();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

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
                            renderOptions.setImageChanging(true);

                            float x2 = toOpenGLCoord(this, motionEvent.getX(1), true);
                            float y2 = toOpenGLCoord(this, motionEvent.getY(1), false);
                            double dis = computeDis(currentX, x2, currentY, y2);
                            double scale = dis / dis_start;
                            checkRender.zoom((float) scale);

                            float dis_x = x2 - currentX;
                            float dis_y = y2 - currentY;
                            float ave_x = (x2 - x1_start + currentX - x0_start) / 2;
                            float ave_y = (y2 - y1_start + currentY - y0_start) / 2;
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
                            renderOptions.setImageChanging(true);
                            checkRender.rotate(currentX - lastX, currentY - lastY);
                            requestRender();
                            lastX = currentX;
                            lastY = currentY;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        renderOptions.setImageChanging(false);
                        isZoomingNotStop = false;
                        lastX = currentX;
                        lastY = currentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isMove) {
                            if (previousUpTime > 0 && isConsideredDoubleTap(previousUpTime, previousUpX, previousUpY, motionEvent)) {
                                int[] newCenter = newCenterWhenNavigateWhenClick(currentX, currentY);
                                onDoubleClickListener.run(newCenter);
                            }
                            previousUpTime = motionEvent.getEventTime();
                            previousUpX = motionEvent.getX();
                            previousUpY = motionEvent.getY();
                        } else {
                            previousUpTime = -1;
                        }
                        isMove = false;
                        requestRender();
                        isZooming = false;
                        renderOptions.setImageChanging(false);
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

    private boolean isConsideredDoubleTap(long firstUpTime, float firstUpX, float firstUpY, MotionEvent secondUp) {
        if (secondUp.getEventTime() - firstUpTime > DOUBLE_TAP_TIMEOUT) {
            return false;
        }
        int deltaX =(int) firstUpX - (int)secondUp.getX();
        int deltaY =(int) firstUpY- (int)secondUp.getY();
        return deltaX * deltaX + deltaY * deltaY < 10000;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openFile(){
        Log.d(TAG,"Open File");

        BasicFile basicFile = imageInfoRepository.getBasicImage();
        FilePath<?> filePath = basicFile.getFilePath();
        FileType fileType = basicFile.getFileType();

        switch (fileType){
            case V3DPBD:
            case V3DRAW:
            case TIFF:
                image4DSimple = Image4DSimple.loadImage(filePath, fileType);
                if (image4DSimple != null){
                    update3DFileSize(new Integer[]{
                            (int) image4DSimple.getSz0(), (int) image4DSimple.getSz1(), (int) image4DSimple.getSz2()});
                    checkRender.init3DImageInfo(image4DSimple, normalizedSize, originalSize);
                    annotationHelper.initImageInfo(image4DSimple, normalizedSize, originalSize);
                    annotationDataManager.init();
                }
                break;
            default:
                ToastEasy("Unsupported file !");
        }
        requestRender();
    }

    public void loadFile(){
        Log.d(TAG,"Load file");

        BasicFile basicFile = imageInfoRepository.getBasicFile();
        FilePath<?> filePath = basicFile.getFilePath();
        FileType fileType = basicFile.getFileType();

        switch (fileType){
            case ANO:
                Log.e(TAG,"load .ano file !");
                break;
            case SWC:
            case ESWC:
                Log.e(TAG,"load .swc file !");
                NeuronTree neuronTree = NeuronTree.parse(filePath);
                if (neuronTree == null){
                    ToastEasy("Something wrong with this .swc/.eswc file, can't load it");
                } else {
                    annotationDataManager.loadNeuronTree(neuronTree, false);
                }
                break;
            case APO:
                Log.e(TAG,"load .apo file !");
                MarkerList markerList = ApoReader.parse(filePath);
                if (markerList == null){
                    ToastEasy("Something wrong with this .apo file, can't load it");
                } else {
                    annotationDataManager.loadMarkerList(markerList);
                }
                break;
            default:
                ToastEasy("Unsupported file !");
        }
        requestRender();
    }

    public void zoomIn(){
        checkRender.zoom(2.0f);
        requestRender();
    }

    public void zoomOut(){
        checkRender.zoom(0.5f);
        requestRender();
    }

    public void autoRotateStart(){
        renderOptions.setImageChanging(true);
        matrixManager.autoRotateStart();
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        requestRender();
    }

    public void autoRotateStop(){
        renderOptions.setImageChanging(false);
        matrixManager.autoRotateStop();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }

    private void update3DFileSize(Integer[] size){
        float maxSize = (float) Collections.max(Arrays.asList(size));

        originalSize[0] = size[0];
        originalSize[1] = size[1];
        originalSize[2] = size[2];

        normalizedSize[0] = (float) size[0] / maxSize;
        normalizedSize[1] = (float) size[1] / maxSize;
        normalizedSize[2] = (float) size[2] / maxSize;

    }

    public int[] newCenterWhenNavigateWhenClick(float x, float y) {
        float[] center = annotationHelper.zoomInByPinpoint(x, y);
        return checkArborInfoState.newCenterWhenNavigateBlockToTargetOffset((int) center[0] - 64, (int) center[1] - 64, (int) center[2] - 64);
    }

    public interface OnDoubleClickListener {
        void run(int [] center);
    }

    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener;
    }

    public void screenCapture(){
        renderOptions.setScreenCapture(true);
        requestRender();
    }
}
