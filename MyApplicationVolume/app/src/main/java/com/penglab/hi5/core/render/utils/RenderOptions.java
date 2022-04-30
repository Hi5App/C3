package com.penglab.hi5.core.render.utils;

import com.penglab.hi5.data.dataStore.PreferenceSetting;

/**
 * Created by Jackiexing on 12/25/21
 */
public class RenderOptions {

    private final PreferenceSetting preferenceSetting;

    private boolean downSampling;

    private boolean showFingerTrajectory;

    private boolean imageChanging;

    private boolean screenCapture;

    private float contrast;

    private float scale;

    private boolean ifShowAnnotation = true;

    public RenderOptions(){
        preferenceSetting = PreferenceSetting.getInstance();
        initOptions();
    }

    public void initOptions(){
        this.downSampling = preferenceSetting.getDownSampleMode();
        this.contrast = preferenceSetting.getContrast() / 100.0f + 1.0f;
        this.imageChanging = false;
        this.screenCapture = false;
        this.scale = 2.5f;
    }

    public boolean isDownSampling() {
        return downSampling;
    }

    public void setDownSampling(boolean downSampling) {
        this.downSampling = downSampling;
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isShowFingerTrajectory() {
        return showFingerTrajectory;
    }

    public void setShowFingerTrajectory(boolean showFingerTrajectory) {
        this.showFingerTrajectory = showFingerTrajectory;
    }

    public boolean isScreenCapture() {
        return screenCapture;
    }

    public void setScreenCapture(boolean screenCapture) {
        this.screenCapture = screenCapture;
    }

    public boolean isImageChanging() {
        return imageChanging;
    }

    public void setImageChanging(boolean imageChanging) {
        this.imageChanging = imageChanging;
    }

    public void setIfShowSWC(boolean b){
        ifShowAnnotation = b;
    }

    public boolean getIfShowSWC(){
        return ifShowAnnotation;
    }

    public void update(){
        this.downSampling = preferenceSetting.getDownSampleMode();
        this.contrast = preferenceSetting.getContrast() / 100.0f + 1.0f;
    }
}
