package com.penglab.hi5.core.render.utils;

/**
 * Created by Jackiexing on 12/25/21
 */
public class RenderOptions {

    private boolean downSampling;

    private float contrast;

    private float scale;

    public RenderOptions(){
        initOptions();
    }

    public void initOptions(){
        this.downSampling = false;
        this.contrast = 1.0f;
        this.scale = 1.0f;
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
}
