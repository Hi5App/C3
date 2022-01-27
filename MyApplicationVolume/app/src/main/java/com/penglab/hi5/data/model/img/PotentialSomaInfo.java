package com.penglab.hi5.data.model.img;

import com.penglab.hi5.basic.image.XYZ;

/**
 * Created by Jackiexing on 01/12/21
 */
public class PotentialSomaInfo {
    private final int id;
    private final String brainId;
    private final XYZ location;  // under the highest resolution
    private boolean isBoring = false;
    private long createdTime;
    private boolean isFresh = true;
    private boolean alreadyUpload = false;
    private final long shelfLife = 7 * 60 * 1000;

    public PotentialSomaInfo(int id, String brainId, XYZ location) {
        this.id = id;
        this.brainId = brainId;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getBrainId() {
        return brainId;
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
