package com.penglab.hi5.data.model.img;

import com.penglab.hi5.basic.image.XYZ;

public class PotentialArborMarkerInfo {
    private final int id;
    private final String somaId;
    private final String image;
    private final String name;
    private final XYZ loc;  // under the highest resolution
    private boolean isBoring = false;
    private long createdTime;
    private boolean isFresh = true;
    private boolean alreadyUpload = false;
    private final long shelfLife = 20 * 60 * 1000;

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

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public boolean ifStillFresh() {
        if (!isFresh) {
            return false;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - createdTime > shelfLife) {
            isFresh = false;
            return false;
        }
        return true;
    }
}
