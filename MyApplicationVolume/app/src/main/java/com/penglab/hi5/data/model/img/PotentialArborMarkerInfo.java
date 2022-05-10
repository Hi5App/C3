package com.penglab.hi5.data.model.img;

import com.penglab.hi5.basic.image.XYZ;

public class PotentialArborMarkerInfo {
    private final int id;
    private final String somaId;
    private final String image;
    private final String name;
    private final XYZ loc;  // under the highest resolution
    private boolean isBoring = false;
    private boolean alreadyUpload = false;

    public PotentialArborMarkerInfo(int id, String name, String somaId, String image, XYZ loc) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.somaId = somaId;
        this.loc = loc;
    }

    public String getBrianId() {
        return image;
    }

    public String getSomaId(){
        return somaId;
    }

    public int getArborId(){
        return id;
    }

    public String getArborName(){
        return name;
    }

    public XYZ getLocation() {
        return loc;
    }

    public boolean isBoring() {
        return isBoring;
    }

    public void setBoring(boolean boring) {
        isBoring = boring;
    }

    public boolean isAlreadyUpload() {
        return alreadyUpload;
    }

    public void setAlreadyUpload(boolean alreadyUpload) {
        this.alreadyUpload = alreadyUpload;
    }

}
