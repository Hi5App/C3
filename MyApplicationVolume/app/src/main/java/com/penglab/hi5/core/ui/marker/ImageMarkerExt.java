package com.penglab.hi5.core.ui.marker;

import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.image.XYZ;

/**
 * Created by Jackiexing on 01/15/21
 */
public class ImageMarkerExt extends ImageMarker {
    private final String name;

    public ImageMarkerExt(XYZ pos, String name) {
        super(pos);
        this.name = name;
    }

    public ImageMarkerExt(int type, XYZ pos, String name) {
        super(type, pos);
        this.name = name;
    }

    public ImageMarkerExt(float x, float y, float z, String name) {
        super(x, y, z);
        this.name = name;
    }

    public ImageMarkerExt(int type, float x, float y, float z, String name) {
        super(type, x, y, z);
        this.name = name;
    }

    public ImageMarkerExt(int type, int shape, float x, float y, float z, float radius, String name) {
        super(type, shape, x, y, z, radius);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
