package com.penglab.hi5.data.model.img;

public class BrainInfo {
    private String imageId;
    private String [] rois;
    private String url;

    public BrainInfo(String imageId, String [] rois, String url) {
        this.imageId = imageId;
        this.rois = rois;
        this.url = url;
    }

    public String[] getRois() {
        return rois;
    }

    public void setRois(String[] rois) {
        this.rois = rois;
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
