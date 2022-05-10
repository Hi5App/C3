package com.penglab.hi5.basic.image;

/**
 * Created by Jackiexing on 01/15/21
 *
 * Add parameter name to support MarkerFactory Mode
 */
public class ImageMarkerExt extends ImageMarker {
    private final int id;

    public ImageMarkerExt(XYZ pos, int id) {
        super(pos);
        this.id = id;
    }

    public ImageMarkerExt(int type, XYZ pos, int id) {
        super(type, pos);
        this.id = id;
    }

    public ImageMarkerExt(float x, float y, float z, int id) {
        super(x, y, z);
        this.id = id;
    }

    public ImageMarkerExt(int type, float x, float y, float z, int id) {
        super(type, x, y, z);
        this.id = id;
    }

    public ImageMarkerExt(int type, int shape, float x, float y, float z, float radius, int id) {
        super(type, shape, x, y, z, radius);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
