package com.penglab.hi5.data.model.img;

public class ImageInfo {

    private final int id;

    private final String imageName;

    private long createdTime;
    private boolean isFresh = true;
    private boolean alreadyUpload = false;
    private final long shelfLife = 7 * 60 * 1000;

    public ImageInfo(int id, String imageName) {
        this.id = id;
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public String getImageName() {return imageName;}

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
