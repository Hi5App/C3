package com.penglab.hi5.data.model.img;

/**
 * Created by Yihang zhu 01/04/21
 */
public class ArborInfo {
    private String arborName;
    private int xc;
    private int yc;
    private int zc;
    private String imageId;
    private String url;

    public ArborInfo(String arborName, int xc, int yc, int zc, String imageId, String url) {
        this.arborName = arborName;
        this.xc = xc;
        this.yc = yc;
        this.zc = zc;
        this.imageId = imageId;
        this.url = url;
    }

    public String getArborName() {
        return arborName;
    }

    public void setArborName(String arborName) {
        this.arborName = arborName;
    }

    public int getXc() {
        return xc;
    }

    public void setXc(int xc) {
        this.xc = xc;
    }

    public int getYc() {
        return yc;
    }

    public void setYc(int yc) {
        this.yc = yc;
    }

    public int getZc() {
        return zc;
    }

    public void setZc(int zc) {
        this.zc = zc;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
