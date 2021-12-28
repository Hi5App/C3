//package com.penglab.hi5.core.render.utils;
//
//import static com.penglab.hi5.core.Myapplication.ToastEasy;
//
//import android.opengl.Matrix;
//import android.util.Log;
//
//import com.penglab.hi5.basic.image.ImageMarker;
//
//import java.util.Arrays;
//
///**
// * Created by Jackiexing on 12/28/21
// */
//public class AnnotationHelper {
//    private final String TAG = "AnnotationHelper";
//
//    public void addMarker(float x, float y, boolean isBigData) throws CloneNotSupportedException {
//        Log.d(TAG, "addMarker");
//
//        if(solveMarkerCenter(x, y) != null) {
//            float[] new_marker = solveMarkerCenter(x, y);
//            Log.d(TAG, "imageMarker_drawed: " + Arrays.toString(new_marker));
//            ImageMarker imageMarker_drawed = new ImageMarker(new_marker[0],
//                    new_marker[1],
//                    new_marker[2]);
//
//            imageMarker_drawed.type = lastMarkerType;
//            markerList.add(imageMarker_drawed);
//
//            if (isBigData){
//                updateAddMarker(imageMarker_drawed);
//            }
//
//            saveUndo();
//        }
//    }
//
//
//    public float[] solveMarkerCenter(float x, float y){
//
//        float [] loc1 = new float[3];
//        float [] loc2 = new float[3];
//
//        get_NearFar_Marker_2(x, y, loc1, loc2);
//
////        Log.v("loc1",Arrays.toString(loc1));
////        Log.v("loc2",Arrays.toString(loc2));
//
//        float steps = 512;
//        float [] step = devide(minus(loc1, loc2), steps);
////        Log.v("step",Arrays.toString(step));
//
//
//        if(make_Point_near(loc1, loc2)){
//            float [] Marker = getCenterOfLineProfile(loc1, loc2);
//            if (Marker == null){
//                return null;
//            }
////            Log.v("Marker",Arrays.toString(Marker));
//            return Marker;
//        }else {
////            Log.v("solveMarkerCenter","please make sure the point inside the bounding box");
//            ToastEasy("please make sure the point inside the bounding box");
//            return null;
//        }
//
//    }
//
//
//    // 类似于光线投射，找直线上强度最大的一点
//    private float[] getCenterOfLineProfile(float[] loc1, float[] loc2){
//
//        float[] result = new float[3];
//        float[] loc1_index = new float[3];
//        float[] loc2_index = new float[3];
//        boolean isInBoundingBox = false;
//
//        loc1_index = modeltoVolume(loc1);
//        loc2_index = modeltoVolume(loc2);
//
//        float[] d = minus(loc1_index, loc2_index);
//        normalize(d);
//
//        float[][] dim = new float[3][2];
//        for(int i=0; i<3; i++){
//            dim[i][0] = 0;
//            dim[i][1] = sz[i] - 1;
//        }
//
//        result = devide(plus(loc1_index, loc2_index), 2);
//        float max_value = 0f;
//
//        //单位向量
////        float[] d = minus(loc1_index, loc2_index);
////        normalize(d);
////        Log.v("getCenterOfLineProfile:", "step: " + Arrays.toString(d));
//
//        // 判断是不是一个像素
//        float length = distance(loc1_index, loc2_index);
//        if(length < 0.5)
//            return result;
//
//        int nstep = (int)(length+0.5);
//        float one_step = length/nstep;
//
//        Log.v("getCenterOfLineProfile", Float.toString(one_step));
//
//        float[] poc;
//        for (int i = 0; i <= nstep; i++) {
//            float value;
//            poc = minus(loc1_index, multiply(d, one_step * i));
//
//            if (isInBoundingBox(poc, dim)) {
//                value = Sample3d(poc[0], poc[1], poc[2]);
//
//                isInBoundingBox = true;
//                if(value > max_value){
////                    Log.v("getCenterOfLineProfile", "(" + poc[0] + "," + poc[1] + "," + poc[2] + "): " +value);
////                    Log.v("getCenterOfLineProfile:", "update the max");
//                    max_value = value;
//                    for (int j = 0; j < 3; j++){
//                        result[j] = poc[j];
//                    }
//                    isInBoundingBox = true;
//                }
//            }
//        }
//
//        if(!isInBoundingBox){
//            ToastEasy("please make sure the point inside the bounding box");
//            return null;
//        }
//
//        return result;
//    }
//
//
//    private void get_NearFar_Marker(float x, float y, float [] res1, float [] res2){
//
//        // mvp矩阵的逆矩阵
//        float [] invertfinalMatrix = new float[16];
//
//        Matrix.invertM(invertfinalMatrix, 0, finalMatrix, 0);
////        Log.v("invert_rotation",Arrays.toString(invertfinalMatrix));
//
//        float [] near = new float[4];
//        float [] far = new float[4];
//
//        Matrix.multiplyMV(near, 0, invertfinalMatrix, 0, new float [] {x, y, -1, 1}, 0);
//        Matrix.multiplyMV(far, 0, invertfinalMatrix, 0, new float [] {x, y, 0, 1}, 0);
//
////        Log.v("near",Arrays.toString(near));
////        Log.v("far",Arrays.toString(far));
//
//        for(int i=0; i<3; i++){
//            res1[i] = near[i];
//            res2[i] = far[i];
//        }
//
//    }
//
//
//    //用于透视投影中获取近平面和远平面的焦点
//    private void get_NearFar_Marker_2(float x, float y, float [] res1, float [] res2){
//        // mvp矩阵的逆矩阵
//        float [] invertfinalMatrix = new float[16];
//
//        Matrix.invertM(invertfinalMatrix, 0, finalMatrix, 0);
////        Log.v("invert_rotation",Arrays.toString(invertfinalMatrix));
//
//        float [] near = new float[4];
//        float [] far = new float[4];
//
//        Matrix.multiplyMV(near, 0, invertfinalMatrix, 0, new float [] {x, y, -1, 1}, 0);
//        Matrix.multiplyMV(far, 0, invertfinalMatrix, 0, new float [] {x, y, 1, 1}, 0);
//
//        devideByw(near);
//        devideByw(far);
//
////        Log.v("near",Arrays.toString(near));
////        Log.v("far",Arrays.toString(far));
//
//        for(int i=0; i<3; i++){
//            res1[i] = near[i];
//            res2[i] = far[i];
//        }
//
//    }
//
//
//    // 找到靠近boundingbox的两处端点
//    private boolean make_Point_near(float[] loc1, float[] loc2){
//
//        float steps = 512;
//        float [] near = loc1;
//        float [] far = loc2;
//        float [] step = devide(minus(near, far), steps);
//
//        float[][] dim = new float[3][2];
//        for(int i=0; i<3; i++){
//            dim[i][0]= 0;
//            dim[i][1]= mz[i];
//        }
//
//        int num = 0;
//        while(num<steps && !isInBoundingBox(near, dim)){
//            near = minus(near, step);
//            num++;
//        }
//        if(num == steps)
//            return false;
//
//
//        while(!isInBoundingBox(far, dim)){
//            far = plus(far, step);
//        }
//
//        near = plus(near, step);
//        far = minus(far, step);
//
//        for(int i=0; i<3; i++){
//            loc1[i] = near[i];
//            loc2[i] = far[i];
//        }
//
//        return true;
//
//    }
//
//
//    // 找到靠近boundingbox的两处端点
//    private boolean make_Point_near_2(float[] loc1, float[] loc2){
//
//        float steps = 512;
//        float [] near = loc1;
//        float [] far = loc2;
//        float [] step = devide(minus(near, far), steps);
//
//        float[][] dim = new float[3][2];
//        for(int i=0; i<3; i++){
//            dim[i][0]= 0;
//            dim[i][1]= mz[i];
//        }
//
//        int num = 0;
//        while(num<steps && !isInBoundingBox(near, dim)){
//            near = minus(near, step);
//            num++;
//        }
//        if(num == steps)
//            return false;
//
//
//        while(!isInBoundingBox(far, dim)){
//            far = plus(far, step);
//        }
//
//
//        for(int i=0; i<3; i++){
//            loc1[i] = near[i];
//            loc2[i] = far[i];
//        }
//
//        return true;
//
//    }
//
//
//    // 判断是否在图像内部了
//    private boolean isInBoundingBox(float[] x, float[][] dim){
//        int length = x.length;
//        for(int i=0; i<length; i++){
//            if(x[i]>=dim[i][1] || x[i]<=dim[i][0])
//                return false;
//        }
//        return true;
//    }
//}
