package com.penglab.hi5.data.dataStore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.Matrix;

import com.lxj.xpopup.widget.SmartDragLayout;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.ui.ImageClassify.ClassifySolutionInfo;

import java.util.HashSet;
import java.util.Set;


/**
 * Manager the pref in settings
 * three prefs: downsample mode;  check mode;  contrast
 */

public class PreferenceSetting {

    @SuppressLint("StaticFieldLeak")
    private static volatile PreferenceSetting INSTANCE;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private final SharedPreferences pref;

    private int contrastEnhanceRatio;

    public static void init(Context context){
        mContext = context;
    }

    public static PreferenceSetting getInstance(){
        if (INSTANCE == null){
            synchronized (PreferenceLogin.class){
                if (INSTANCE == null){
                    INSTANCE = new PreferenceSetting();
                }
            }
        }
        return INSTANCE;
    }
    private PreferenceSetting(){
        pref = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public int getContrastEnhanceRatio(){
        return pref.getInt("ContrastEnhanceRatio",1);
    }

    public void setContrastEnhanceRatio(int ratio){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ContrastEnhanceRatio", ratio);
        editor.apply();
    }

    public void setPref(boolean DownSampleMode, int Contrast){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("DownSampleMode",DownSampleMode);
        editor.putInt("Contrast",Contrast);
        editor.apply();
    }

    public void setDownSampleMode(boolean downSampleMode){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("DownSampleMode", downSampleMode);
        editor.putInt("Contrast", pref.getInt("Contrast",0));
        editor.apply();
    }

    public void setContrast(int contrast){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("DownSampleMode", pref.getBoolean("DownSampleMode",true));
        editor.putInt("Contrast", contrast);
        editor.apply();
    }

    public void setPointStroke(boolean pointStroke){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("DownSampleMode",pref.getBoolean("DownSampleMode",true));
        editor.putInt("Contrast", pref.getInt("Contrast",0));
        editor.putBoolean("pointStroke",pointStroke);
        editor.apply();
    }

    public void setRotationDistanceXY(float distanceX, float distanceY){
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("rotationDistanceX", distanceX);
        editor.putFloat("rotationDistanceY", distanceY);
        editor.apply();
    }

    public void setRotationMatrix(float[] rotationMatrix){
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("rotationMatrix_length", rotationMatrix.length);
        for (int i = 0; i < rotationMatrix.length; i++){
            editor.putFloat("rotationMatrix_" + i, rotationMatrix[i]);
        }
        editor.apply();
    }

    public boolean getDownSampleMode(){
        return pref.getBoolean("DownSampleMode",true);
    }

    public int getContrast(){
        return pref.getInt("Contrast",0);
    }

    public boolean getPointStrokeMode() {return pref.getBoolean("pointStroke",true);}

    public float getRotationDistanceX() {return pref.getFloat("rotationDistanceX", 0f);}

    public float getRotationDistanceY() {return pref.getFloat("rotationDistanceY", 0f);}

    public float getXRangeSliderStart() {return pref.getFloat("xRangeSliderStart", 0f);}
    public float getXRangeSliderEnd() {return pref.getFloat("xRangeSliderEnd", 100f);}
    public float getYRangeSliderStart() {return pref.getFloat("yRangeSliderStart", 0f);}
    public float getYRangeSliderEnd() {return pref.getFloat("yRangeSliderEnd", 100f);}
    public float getZRangeSliderStart() {return pref.getFloat("zRangeSliderStart", 0f);}
    public float getZRangeSliderEnd() {return pref.getFloat("zRangeSliderEnd", 100f);}

    public void setXRangeSliderStart(float val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("xRangeSliderStart", val);
        editor.apply();
    }
    public void setXRangeSliderEnd(float val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("xRangeSliderEnd", val);
        editor.apply();
    }
    public void setYRangeSliderStart(float val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("yRangeSliderStart", val);
        editor.apply();
    }
    public void setYRangeSliderEnd(float val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("yRangeSliderEnd", val);
        editor.apply();
    }
    public void setZRangeSliderStart(float val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("zRangeSliderStart", val);
        editor.apply();
    }
    public void setZRangeSliderEnd(float val) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat("zRangeSliderEnd", val);
        editor.apply();
    }

    public void resetRangeSlider(){
        SharedPreferences.Editor editor = pref.edit();
        if(pref.contains("xRangeSliderStart")){
            editor.putFloat("xRangeSliderStart", 0);
        }
        if(pref.contains("xRangeSliderEnd")){
            editor.putFloat("xRangeSliderEnd", 100);
        }
        if(pref.contains("yRangeSliderStart")){
            editor.putFloat("yRangeSliderStart", 0);
        }
        if(pref.contains("yRangeSliderEnd")){
            editor.putFloat("yRangeSliderEnd", 100);
        }
        if(pref.contains("zRangeSliderStart")){
            editor.putFloat("zRangeSliderStart", 0);
        }
        if(pref.contains("zRangeSliderEnd")){
            editor.putFloat("zRangeSliderEnd", 100);
        }
        editor.apply();
    }

    public void resetRotationMatrix(){
        SharedPreferences.Editor editor = pref.edit();
        int size = pref.getInt("rotationMatrix_length", 0);
        if (size == 0){
            return;
        }
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        for(int i = 0; i < size; i++){
            editor.putFloat("rotationMatrix_" + i, matrix[i]);
        }
        editor.apply();
    }

    public float[] getRotationMatrix(){
        int size = pref.getInt("rotationMatrix_length", 0);
        float[] matrix = new float[size];
        if (size == 0){
            matrix = new float[16];
            Matrix.setIdentityM(matrix, 0);
            return matrix;
        }
        for(int i = 0; i < size; i++){
            matrix[i] = pref.getFloat("rotationMatrix_" + i, 0f);
        }
        return matrix;
    }

    public void setUserClassifySolutionInfos(Set<ClassifySolutionInfo> solutionInfos){
        SharedPreferences.Editor editor = pref.edit();
        String username = InfoCache.getAccount();
        String key = username + "_classify_solution_info";
        Set<String> set = new HashSet<>();
        for(ClassifySolutionInfo info : solutionInfos){
            String item = String.join(":", info.solutionName, info.solutionDetail);
            set.add(item);
        }
        editor.putStringSet(key, set);
        editor.apply();
    }

    public Set<String> getUserClassifySolutionInfos(String username){
        String key = username + "_classify_solution_info";
        HashSet<String> defaultSet = new HashSet<String>();
        defaultSet.add("Default:HORIZONTAL,VERTICAL,SLANTING_INTERCEPTIVE,SLANTING_UNTRUNCATED,SPECIAL");
        return pref.getStringSet(key, defaultSet);
    }
}
