package com.penglab.hi5.data.model.img;

import com.penglab.hi5.basic.image.XYZ;

public class PotentialArborMarkerInfo {
    private final int id;
    private final int somaId;
    private final String brainId;
    private final String name;
    private final XYZ location;  // under the highest resolution
    private boolean isBoring = false;
    private long createdTime;
    private boolean isFresh = true;
    private boolean alreadyUpload = false;
    private final long shelfLife = 7 * 60 * 1000;

    public PotentialArborMarkerInfo(int id, String name, int somaId, String brainId, XYZ location) {
        this.id = id;
        this.name = name;
        this.brainId = brainId;
        this.somaId = somaId;
        this.location = location;
    }

    public String getBrianId() {
        return brainId;
    }
    public int getSomaId(){
        return somaId;
    }
    public int getArborId(){
        return id;
    }

    public String getArborName(){
        return name;
    }


    public XYZ getLocation() {
        return location;
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
