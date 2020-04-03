package com.example.basic;

enum ImagePixelType {V3D_UNKNOWN, V3D_UINT8, V3D_UINT16, V3D_THREEBYTE, V3D_FLOAT32}
enum TimePackType {TIME_PACK_NONE,TIME_PACK_Z,TIME_PACK_C}

public class Image4DSimple {
    protected long sz0;
    protected long sz1;
    protected long sz2;
    protected long sz3;
    protected long sz_time;

    protected ImagePixelType datatype;
    protected TimePackType timepacktype;

    protected String imgSrcFile;

    protected int b_error;
    protected double rez_x, rez_y, rez_z;
    protected double origin_x, origin_y, origin_z;

    protected long valid_zslcenum;
    protected long prevalid_zslice_num;

    protected int [][][][] data;

    public Image4DSimple(){
        data = null;

        sz0 = sz1 = sz2 = sz3 = 0;
        sz_time = 0;

        datatype = ImagePixelType.V3D_UNKNOWN;
        timepacktype = TimePackType.TIME_PACK_NONE;

        imgSrcFile = "\0";

        b_error = 0;

        rez_x = rez_y = rez_z = 1;
        origin_x = origin_y = origin_z = 0;

        valid_zslcenum = 0;
    }

    public void finalize(){
        cleanExistData();
    }
    public void cleanExistData(){
        data = null;

        sz0 = sz1 = sz2 = sz3 = 0;
        sz_time = 0;

        datatype = ImagePixelType.V3D_UNKNOWN;
        timepacktype = TimePackType.TIME_PACK_NONE;

        imgSrcFile = "\0";

        b_error = 0;

        rez_x = rez_y = rez_z = 1;
        origin_x = origin_y = origin_z = 0;

        valid_zslcenum = 0;

    }
}
