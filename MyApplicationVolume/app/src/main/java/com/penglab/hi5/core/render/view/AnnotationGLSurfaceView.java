package com.penglab.hi5.core.render.view;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.playCurveActionSound;
import static com.penglab.hi5.core.Myapplication.playMarkerActionSound;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.basic.learning.pixelclassification.PixelClassification;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
import com.penglab.hi5.core.collaboration.Communicator;
import com.penglab.hi5.core.fileReader.annotationReader.ApoReader;
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.utils.AnnotationDataManager;
import com.penglab.hi5.core.render.utils.AnnotationHelper;
import com.penglab.hi5.core.render.utils.MatrixManager;
import com.penglab.hi5.core.render.utils.RenderOptions;
import com.penglab.hi5.core.ui.annotation.EditMode;
import com.penglab.hi5.core.ui.annotation.SwitchMutableLiveData;
import com.penglab.hi5.core.ui.collaboration.CollaborationArborInfoState;
import com.penglab.hi5.core.ui.marker.CoordinateConvert;
import com.penglab.hi5.data.ImageDataSource;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.model.img.BasicFile;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;

import org.json.JSONArray;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jackiexing on 12/20/21
 */
public class AnnotationGLSurfaceView extends BasicGLSurfaceView {

    private final String TAG = "AnnotationGLSurfaceView";
    private final int DEFAULT_SIZE = 128;
    private final boolean[][] selections =
            {{true,true,true,false,false,false,false},
             {true,true,true,false,false,false,false},
             {false,false,false,false,false,false,false},
             {false,false,false,false,false,false,false},
             {false,false,false,false,false,false,false},
             {true,true,true,false,false,false,false}};
    private final SwitchMutableLiveData<EditMode> editMode = new SwitchMutableLiveData<>(EditMode.NONE);

    private final RenderOptions renderOptions = new RenderOptions();
    private final MatrixManager matrixManager = new MatrixManager();
    private final AnnotationDataManager annotationDataManager = new AnnotationDataManager();
    private final AnnotationHelper annotationHelper = new AnnotationHelper(annotationDataManager, matrixManager);
    private final AnnotationRender annotationRender = new AnnotationRender(annotationDataManager, matrixManager, renderOptions);

    private final ImageInfoRepository imageInfoRepository = ImageInfoRepository.getInstance();

    private final CollaborationArborInfoState collaborationArborInfoState = CollaborationArborInfoState.getInstance();
    private Image4DSimple image4DSimple;
    private final float[] normalizedSize = new float[3];
    private final int[] originalSize = new int[3];

    private final ExecutorService exeService = Executors.newSingleThreadExecutor();
    private final ArrayList<Float> fingerTrajectory = new ArrayList<Float>();
    private FileType fileType;
    private boolean isBigData;

    private OnScoreWinWithTouchEventListener onScoreWinWithTouchEventListener;

    public AnnotationGLSurfaceView(Context context) {
        super(context);
    }

    public AnnotationGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 设置一下opengl版本；
        setEGLContextClientVersion(3);
        setRenderer(annotationRender);

        // 调用 onPause 的时候保存EGLContext
        setPreserveEGLContextOnPause(true);

        // 当发生交互时重新执行渲染， 需要配合requestRender();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean onTouchEvent(MotionEvent event) {
        try{
            // ACTION_DOWN 不 return true，就无触发后面的各个事件
            if (event != null) {
                final float currentX = toOpenGLCoord(this, event.getX(), true);
                final float currentY = toOpenGLCoord(this, event.getY(), false);

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = currentX;
                        lastY = currentY;
                        switch (Objects.requireNonNull(editMode.getValue())){
                            case PAINT_CURVE:
                            case PINPOINT_STROKE:
                            case PINPOINT_CHECK:
                            case DELETE_CURVE:
                            case SPLIT:
                            case CHANGE_CURVE_TYPE:
                            case DELETE_MULTI_MARKER:
                            case ZOOM_IN_ROI:
                                updateFingerTrajectory(lastX, lastY);
                                renderOptions.setShowFingerTrajectory(true);
                                requestRender();
                                break;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        clearFingerTrajectory();
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
                            if (!is2DImage()){
                                renderOptions.setImageChanging(true);
                            }

                            float x2 = toOpenGLCoord(this, event.getX(1), true);
                            float y2 = toOpenGLCoord(this, event.getY(1), false);
                            double dis = computeDis(currentX, x2, currentY, y2);
                            double scale = dis / dis_start;
                            annotationRender.zoom((float) scale);

                            float dis_x = x2 - currentX;
                            float dis_y = y2 - currentY;
                            float ave_x = (x2 - x1_start + currentX - x0_start) / 2;
                            float ave_y = (y2 - y1_start + currentY - y0_start) / 2;
                            annotationRender.rotate(ave_x, ave_y);

                            requestRender();
                            dis_start = dis;
                            dis_x_start = dis_x;
                            dis_y_start = dis_y;
                            x0_start = currentX;
                            y0_start = currentY;
                            x1_start = x2;
                            y1_start = y2;
                        } else if (!isZooming) {
                            if (editMode.getValue() == EditMode.NONE){
                                if (!is2DImage()){
                                    renderOptions.setImageChanging(true);
                                }
                                annotationRender.rotate(currentX - lastX, currentY - lastY);
                                requestRender();
                                lastX = currentX;
                                lastY = currentY;
                            } else {
                                // play music when curve / marker action start
                                // The step ACTION_DOWN will add 3 item into fingerTrajectory
                                if (fingerTrajectory.size() <= 3){
                                    switch (Objects.requireNonNull(editMode.getValue())){
                                        case PINPOINT:
                                        case PINPOINT_STROKE:
                                        case PINPOINT_CHECK:
                                        case DELETE_MARKER:
                                        case CHANGE_MARKER_TYPE:
                                        case ZOOM_IN_ROI:
                                            playMarkerActionSound();
                                            break;
                                        case PAINT_CURVE:
                                        case DELETE_CURVE:
                                        case CHANGE_CURVE_TYPE:
                                        case SPLIT:
                                            playCurveActionSound();
                                            break;
                                    }
                                }
                                updateFingerTrajectory(currentX, currentY);
                                annotationRender.updateFingerTrajectory(fingerTrajectory);
                                requestRender();
                            }
                        }
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        if (editMode.getValue() == EditMode.NONE){
                            renderOptions.setImageChanging(false);
                        }
                        isZoomingNotStop = false;
                        lastX = currentX;
                        lastY = currentY;
                        clearFingerTrajectory();
                        break;

                    case MotionEvent.ACTION_UP:
                        if (!isZooming) {
                            try {
                                switch (Objects.requireNonNull(editMode.getValue())){
                                    case ZOOM:
                                        editMode.setValue(EditMode.NONE);
                                        float [] center = annotationHelper.solveMarkerCenter(currentX, currentY);
                                        if (center != null) {

                                            //Communicator communicator = Communicator.getInstance();
                                            //communicator.navigateAndZoomInBlock((int) center[0] - 64, (int) center[1] - 64, (int) center[2] - 64);
                                            collaborationArborInfoState.setCenterLocation(new XYZ(center[0] - 64, center[1] - 64, center[2] - 64));
                                            clearFingerTrajectory();
                                            requestRender();
                                        }
                                        clearFingerTrajectory();
                                        break;

                                    case ZOOM_IN_ROI:
                                        editMode.setValue(EditMode.NONE);
                                        float [] roiCenter = annotationHelper.getROICenter(fingerTrajectory, isBigData);
                                        if (roiCenter != null) {
//                                            Communicator communicator = Communicator.getInstance();
//                                            communicator.navigateAndZoomInBlock((int) roiCenter[0] - 64, (int) roiCenter[1] - 64, (int) roiCenter[2] - 64);
                                            collaborationArborInfoState.setCenterLocation(new XYZ(roiCenter[0] - 64, roiCenter[1] - 64, roiCenter[2] - 64));
                                            clearFingerTrajectory();
                                            requestRender();
                                        }
                                        break;

                                    case PINPOINT:
                                        // TODO: set score
                                        if (is2DImage()){
                                            annotationHelper.add2DMarker(currentX, currentY);
                                        } else {
                                            annotationHelper.addMarker(currentX,currentY,isBigData);
                                            onScoreWinWithTouchEventListener.run();
                                        }
                                        requestRender();
                                        break;

                                    case PINPOINT_STROKE:
                                        if (is2DImage()){
                                            annotationHelper.add2DMarker(currentX, currentY);
                                        } else {
                                            annotationHelper.addMarkerByStroke(fingerTrajectory, isBigData);
                                            onScoreWinWithTouchEventListener.run();
                                        }
                                        requestRender();
                                        break;

                                    case PINPOINT_CHECK:
                                        annotationHelper.addMarkerInSWC(currentX, currentY,isBigData);
                                        onScoreWinWithTouchEventListener.run();
                                        requestRender();
                                        break;

                                    case DELETE_MARKER:
                                        annotationHelper.deleteMarker(currentX, currentY, isBigData);
                                        requestRender();
                                        break;

                                    case DELETE_MULTI_MARKER:
                                        annotationHelper.deleteMultiMarkerByStroke(fingerTrajectory, isBigData);
                                        requestRender();
                                        break;

                                    case CHANGE_MARKER_TYPE:
                                        annotationHelper.changeMarkerType(currentX, currentY, isBigData);
                                        requestRender();
                                        break;

                                    case PAINT_CURVE:
                                        renderOptions.setShowFingerTrajectory(false);
                                        // TODO: set score

                                        if (is2DImage()){
                                            annotationHelper.add2DCurve(fingerTrajectory);
                                        } else {
                                            Future<String> future = exeService.submit(new Callable<String>() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public String call() throws Exception {
                                                    V_NeuronSWC_list[] v_neuronSWC_list = new V_NeuronSWC_list[1];
                                                    V_NeuronSWC seg = annotationHelper.addBackgroundCurve(fingerTrajectory, v_neuronSWC_list);
                                                    if (seg != null) {
                                                        annotationHelper.addCurve(fingerTrajectory, seg, isBigData);
                                                        annotationHelper.deleteFromCur(seg, v_neuronSWC_list[0]);
                                                    }
                                                    requestRender();
                                                    return "success";
                                                }
                                            });
                                            try {
                                                String result = future.get(1500, TimeUnit.MILLISECONDS);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Log.e(TAG, "The curve is too complex, unfinished in 1.5 second");
                                            }
                                        }
                                        requestRender();
                                        break;

                                    case DELETE_CURVE:
                                        Log.e(TAG,"enter delete curve");
                                        annotationHelper.deleteCurve(fingerTrajectory, isBigData);
                                        requestRender();
                                        break;

                                    case SPLIT:
                                        Log.e(TAG,"enter split curve");
                                        annotationHelper.splitCurve(fingerTrajectory, isBigData);
                                        requestRender();
                                        break;

                                    case CHANGE_CURVE_TYPE:
                                        annotationHelper.changeCurveType(fingerTrajectory, isBigData);
                                        requestRender();
                                        break;
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        clearFingerTrajectory();
                        requestRender();
                        isZooming = false;
                        renderOptions.setImageChanging(false);
                        break;
                    default:
                        break;
                }
                return true;
            }
            return false;
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return false;
    }

    public void syncSplitSegSWC(Vector<V_NeuronSWC> segs){
        annotationDataManager.syncSplitSegSWC(segs);
    }
    public void syncAddSegSWC(Vector<V_NeuronSWC> segs) {
        annotationDataManager.syncAddSegSWC(segs);
    }

    public void syncRetypeSegSWC(Vector<V_NeuronSWC> segs) {
        annotationDataManager.syncRetypeSegSWC(segs);
    }

    public void syncDelSegSWC(Vector<V_NeuronSWC> segs) {
        annotationDataManager.syncDelSegSWC(segs);
    }

    public void syncAddMarker(ImageMarker imageMarker) {
        annotationDataManager.syncAddMarker(imageMarker);
    }

    public void syncAddMarkerGlobal(ImageMarker imageMarker){
        annotationDataManager.syncAddMarkerGlobal(imageMarker);
    }

    public void syncDelMarker(ImageMarker imageMarker) {
        annotationDataManager.syncDelMarker(imageMarker);
    }

    public void syncDelMarkerGlobal(ImageMarker imageMarker)
    {
        annotationDataManager.syncDelMarkerGlobal(imageMarker);
    }

    private void clearFingerTrajectory(){
        fingerTrajectory.clear();
        renderOptions.setShowFingerTrajectory(false);
        annotationRender.updateFingerTrajectory(fingerTrajectory);
    }

    private void updateFingerTrajectory(float x, float y){
        fingerTrajectory.add(x);
        fingerTrajectory.add(y);
        fingerTrajectory.add(-1.0f);
    }

    private boolean is2DImage(){
        return (fileType == FileType.JPG || fileType == FileType.PNG);
    }

    public LiveData<EditMode> getEditMode(){
        return editMode;
    }

    public boolean setEditMode(EditMode mode){
        return editMode.setSwitchableValue(mode);
    }

    public EditMode getEditModeValue() {
        return editMode.getValue();
    }

    public void updateRenderOptions(){
        renderOptions.update();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void openFile(){
        BasicFile basicFile = imageInfoRepository.getBasicImage();
        FilePath<?> filePath = basicFile.getFilePath();
        Log.d("openfile", filePath.getData().toString());
        FileType fileType = basicFile.getFileType();
        this.fileType = fileType;

        switch (fileType){
            case V3DPBD:
            case V3DRAW:
            case TIFF:
                Image4DSimple curImage = Image4DSimple.loadImage(filePath, fileType);
                if (curImage != null){
                    image4DSimple = curImage;
                    update3DFileSize(new Integer[]{(int) image4DSimple.getSz0(), (int) image4DSimple.getSz1(), (int) image4DSimple.getSz2()});
                    renderOptions.initOptions();
                    annotationRender.init3DImageInfo(image4DSimple, normalizedSize, originalSize);
                    annotationHelper.initImageInfo(image4DSimple, normalizedSize, originalSize);
                    annotationDataManager.init(isBigData);
                }
                break;
            case JPG:
            case PNG:
                Bitmap bitmap2D = Image4DSimple.loadImage2D(filePath);
                Image4DSimple curImage2D = Image4DSimple.loadImage2D(bitmap2D, filePath.getData().toString());
                if (bitmap2D != null && curImage2D != null){
                    image4DSimple = curImage2D;
                    update2DImageSize(new Integer[]{
                            bitmap2D.getWidth(), bitmap2D.getHeight(), Math.max(bitmap2D.getWidth(), bitmap2D.getHeight())});
                    annotationRender.init2DImageInfo(image4DSimple, bitmap2D, normalizedSize, originalSize);
                    annotationHelper.initImageInfo(image4DSimple, normalizedSize, originalSize);
                    annotationDataManager.init(isBigData);
                }
                break;
            case SWC:
            case ESWC:
                NeuronTree neuronTree = NeuronTree.parse(filePath);
                if (neuronTree != null){
                    update3DFileSize(new Integer[]{DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE});
                    annotationDataManager.init(isBigData);
                    annotationRender.initSwcInfo(neuronTree, normalizedSize, originalSize );
                }
                break;
            default:
                ToastEasy("Unsupported file !");
        }
//        requestRender();
    }

    public void loadFile(){
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
        annotationRender.zoom(2.0f);
        requestRender();
    }

    public void zoomOut(){
        annotationRender.zoom(0.5f);
        requestRender();
    }

    public void autoRotateStart(){
        renderOptions.setImageChanging(true);
        matrixManager.autoRotateStart();
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        requestRender();
    }

    public void importApo(ArrayList<ArrayList<Float>> apo){
        annotationHelper.importApo(apo);
    }

    public void convertCoordsForMarker(CoordinateConvert downloadCoordinateConvert){
        annotationHelper.convertCoordsForMarker(downloadCoordinateConvert);
    }

    public void convertCoordsForSWC(CoordinateConvert downloadCoordinateConvert){
        annotationHelper.convertCoordsForSWC(downloadCoordinateConvert);
    }


    public void importNeuronTree(NeuronTree nt, boolean needSync){
        annotationHelper.importNeuronTree(nt,needSync);
    }

    public void autoRotateStop(){
        renderOptions.setImageChanging(false);
        matrixManager.autoRotateStop();
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }

    public void undo(){
        try {
            if (!annotationDataManager.undo()){
                ToastEasy("nothing to undo");
            }
        } catch (Exception e){
          e.printStackTrace();
        }
        requestRender();
    }

    public void redo(){
        try {
            if (!annotationDataManager.redo()){
                ToastEasy("nothing to redo");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        requestRender();
    }

    public void setLastCurveType(int curveType){
        annotationHelper.setLastCurveType(curveType);
    }

    public void setLastMarkerType(int markerType){
        annotationHelper.setLastMarkerType(markerType);
    }

    public int getLastCurveType(){
        Log.e("LastCurveType",":"+annotationHelper.getLastCurveType());
        return annotationHelper.getLastCurveType();
    }

    public int getLastMarkerType(){
        return annotationHelper.getLastMarkerType();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean APP2(){
        boolean result = annotationHelper.APP2(image4DSimple, is2DImage(), isBigData);
        requestRender();
        return result;
    }

    public boolean GD(){
        boolean result = annotationHelper.GD(image4DSimple, is2DImage(), isBigData);
        requestRender();
        return result;
    }

    public void clearAllTracing(){
        annotationDataManager.clearAllTracing();
        requestRender();
    }

    public void screenCapture(){
        renderOptions.setScreenCapture(true);
        requestRender();
    }


    public boolean setShowAnnotation() {
        if (renderOptions.getIfShowSWC()) {
            renderOptions.setIfShowSWC(false);
            return false;
        }else{
            renderOptions.setIfShowSWC(true);
            return true;
        }
    }

    public void pixelClassification(){
        if (annotationDataManager.getNeuronTree() != null && image4DSimple != null){
            NeuronTree neuronTree = annotationDataManager.getNeuronTree();
            PixelClassification pixelClassification = new PixelClassification();
            pixelClassification.setSelections(selections);

            try {
                image4DSimple = pixelClassification.getPixelClassificationResult(image4DSimple, neuronTree);
                if (image4DSimple != null){
                    update3DFileSize(new Integer[]{
                            (int) image4DSimple.getSz0(), (int) image4DSimple.getSz1(), (int) image4DSimple.getSz2()});
                    annotationRender.init3DImageInfo(image4DSimple, normalizedSize, originalSize);
                    annotationHelper.initImageInfo(image4DSimple, normalizedSize, originalSize);
                    annotationDataManager.init(isBigData);
                }
                requestRender();
            } catch (Exception e){
                ToastEasy(e.getMessage());
            }
        } else {
            ToastEasy("Load a image first");
        }
    }

    public Image4DSimple getImage() {
        return image4DSimple;
    }

    public NeuronTree getNeuronTree(){
        return annotationDataManager.getNeuronTree();
    }

    public MarkerList getMarkerList(){
        return annotationDataManager.getMarkerList();
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

    private void update2DImageSize(Integer[] size){
        float maxSize = (float) Collections.max(Arrays.asList(size));

        originalSize[0] = size[0];
        originalSize[1] = size[1];
        originalSize[2] = size[2];

        normalizedSize[0] = (float) size[0] / maxSize;
        normalizedSize[1] = (float) size[1] / maxSize;
        normalizedSize[2] = (float) size[2] / maxSize;
    }

    /**
     * MarkerFactory part
     */
    public void syncMarkerList(MarkerList markerList) {
        annotationDataManager.syncMarkerList(markerList);
        requestRender();
    }

    public void syncNeuronTree(NeuronTree neuronTree) {
        annotationDataManager.loadNeuronTree(neuronTree,true);
        requestRender();
    }

    public MarkerList getMarkerListToAdd() {
        return annotationDataManager.getMarkerListToAdd();
    }

    public JSONArray getMarkerListToDelete() {
        return annotationDataManager.getMarkerListToDelete();
    }

    public boolean nothingToUpload() {
        MarkerList markerListToAdd = annotationDataManager.getMarkerListToAdd();
        JSONArray markerListToDelete = annotationDataManager.getMarkerListToDelete();
        return ((markerListToAdd == null || markerListToAdd.size() == 0) &&
                (markerListToDelete == null || markerListToDelete.length() == 0));
    }

    public interface OnScoreWinWithTouchEventListener {
        void run();
    }

    public void setOnScoreWinWithTouchEventListener(OnScoreWinWithTouchEventListener onScoreWinWithTouchEventListener) {
        this.onScoreWinWithTouchEventListener = onScoreWinWithTouchEventListener;
    }

    public void setImageInfoInRender(String imageInfo) {
        annotationRender.setImageInfo(imageInfo);
    }


    public void setBigData(boolean isBigData) {
        this.isBigData = isBigData;
    }
}
