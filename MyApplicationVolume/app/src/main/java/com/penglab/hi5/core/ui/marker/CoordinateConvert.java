package com.penglab.hi5.core.ui.marker;

import com.penglab.hi5.basic.image.XYZ;

/**
 * Created by Jackiexing on 01/13/21
 *
 * Util Class to process coordinate conversion when communicate with Server
 */
public class CoordinateConvert {
    private final String TAG = "CoordinateConvert";

    private final XYZ startLocation = new XYZ();
    private final XYZ centerLocation = new XYZ();
    private int resIndex;
    private int imgSize;

    public XYZ convertGlobalToLocal(double x, double y, double z) {
        XYZ node = convertMaxResToCurRes(x, y, z);
        node.x -= startLocation.x;
        node.y -= startLocation.y;
        node.z -= startLocation.z;
        return node;
    }

    public XYZ convertLocalToGlobal(double x, double y, double z) {
        x += startLocation.x;
        y += startLocation.y;
        z += startLocation.z;
        XYZ node = convertCurResToMaxRes(x, y, z);
        return node;
    }

    private XYZ convertMaxResToCurRes(double x, double y, double z) {
        x /= Math.pow(2, resIndex-1);
        y /= Math.pow(2, resIndex-1);
        z /= Math.pow(2, resIndex-1);
        return new XYZ((float) x, (float) y, (float) z);
    }

    private XYZ convertCurResToMaxRes(double x, double y, double z) {
        x *= Math.pow(2, resIndex-1);
        y *= Math.pow(2, resIndex-1);
        z *= Math.pow(2, resIndex-1);
        return new XYZ((float) x, (float) y, (float) z);
    }

    public int getResIndex() {
        return resIndex;
    }

    public void setResIndex(int resIndex) {
        this.resIndex = resIndex;
    }

    public int getImgSize() {
        return imgSize;
    }

    public void setImgSize(int imgSize) {
        this.imgSize = imgSize;
    }

    public XYZ getStartLocation() {
        return startLocation;
    }

    public XYZ getCenterLocation() {
        return centerLocation;
    }

    public void initLocation(XYZ centerLoc) {
        centerLocation.x = (int) (centerLoc.x / Math.pow(2, resIndex-1));
        centerLocation.y = (int) (centerLoc.y / Math.pow(2, resIndex-1));
        centerLocation.z = (int) (centerLoc.z / Math.pow(2, resIndex-1));

        startLocation.x = centerLocation.x - imgSize / 2;
        startLocation.y = centerLocation.y - imgSize / 2;
        startLocation.z = centerLocation.z - imgSize / 2;
    }
}
