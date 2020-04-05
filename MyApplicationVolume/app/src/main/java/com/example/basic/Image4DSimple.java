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

    public int[][][][] getData() {
        return data;
    }

    public long getSz0() {
        return sz0;
    }

    public long getSz1() {
        return sz1;
    }

    public long getSz2() {
        return sz2;
    }

    public long getSz3() {
        return sz3;
    }

    public long getSz_time() {
        return sz_time;
    }

    public long getValid_zslcenum() {
        return valid_zslcenum;
    }

    public long getPrevalid_zslice_num() {
        return prevalid_zslice_num;
    }

    public int getB_error() {
        return b_error;
    }

    public ImagePixelType getDatatype() {
        return datatype;
    }

    public TimePackType getTimepacktype() {
        return timepacktype;
    }

    public long getTotalUnitNumber(){
        return sz0*sz1*sz2*sz3;
    }

    public long getTotalUnitNumberPerPlane(){
        return sz0*sz1;
    }

    public long getTotalUnitNumberPerChannel(){
        return sz0*sz1*sz2;
    }

    public int getUnitBytes(){
        switch (datatype)
        {
            case V3D_UINT16: return 2;
            case V3D_FLOAT32: return 4;
            default: return 1;
        }
    }

    public long getTotalBytes(){
        return this.getUnitBytes()*sz0*sz1*sz2*sz3;
    }

    public boolean valid(){
        return data != null &&
                data.length != 0 &&
                sz0 > 0 &&
                sz1 > 0 &&
                sz2 > 0 &&
                sz3 > 0 &&
                b_error == 0 &&
                (datatype == ImagePixelType.V3D_UINT8 ||
                        datatype == ImagePixelType.V3D_UINT16 ||
                        datatype == ImagePixelType.V3D_FLOAT32);
    }

    public double getRez_x() {
        return rez_x;
    }

    public double getRez_y() {
        return rez_y;
    }

    public double getRez_z() {
        return rez_z;
    }

    public double getOrigin_x() {
        return origin_x;
    }

    public double getOrigin_y() {
        return origin_y;
    }

    public double getOrigin_z() {
        return origin_z;
    }

    public void setSz0(long sz0) {
        this.sz0 = sz0;
    }

    public void setSz1(long sz1) {
        this.sz1 = sz1;
    }

    public void setSz2(long sz2) {
        this.sz2 = sz2;
    }

    public void setSz3(long sz3) {
        this.sz3 = sz3;
    }

    public void setSz_time(long sz_time) {
        this.sz_time = sz_time;
    }

    public boolean setValid_zslcenum(long valid_zslcenum) {
        if(valid_zslcenum == 0 && this.sz2 == 0){
            this.valid_zslcenum = 0; return true;
        }
        if(valid_zslcenum>=0 && valid_zslcenum<this.sz2){
            this.valid_zslcenum = valid_zslcenum;
            return true;
        }else {
            return false;
        }
    }

    public boolean setPrevalid_zslice_num(long prevalid_zslice_num) {
        if(prevalid_zslice_num == 0 && this.sz2 == 0){
            this.prevalid_zslice_num = 0; return true;
        }
        if(prevalid_zslice_num>=0 && prevalid_zslice_num<this.sz2){
            this.prevalid_zslice_num = prevalid_zslice_num;
            return true;
        }else {
            return false;
        }
    }

    public void setDatatype(ImagePixelType datatype) {
        this.datatype = datatype;
    }

    public void setTimepacktype(TimePackType timepacktype) {
        this.timepacktype = timepacktype;
    }

    public void setData(int[][][][] data) {
        this.data = data;
    }

    public void setDataToNull(){
        this.data = null;
    }

    boolean setValueUINT8(long x,long y,long z,long channel,int val){
        if(datatype != ImagePixelType.V3D_UINT8){
            System.out.println("Warning: This image is not stored in 8bit.");
            return false;
        }
        if(x>=0&&x<sz0&&y>=0&&y<sz1&&z>=0&&z<sz1&&channel>=0&&channel<sz3){
            this.data[(int) x][(int) y][(int) z][(int) channel] =val;
            return  true;
        }else {
            return false;
        }

    }
    public int getValueUINT8(long x,long y,long z,long channel){
        if(datatype != ImagePixelType.V3D_UINT8){
            System.out.println("Warning: This image is not stored in 8bit.");
            return 0;
        }
        if(x>=0&&x<sz0&&y>=0&&y<sz1&&z>=0&&z<sz1&&channel>=0&&channel<sz3){
            int val = this.data[(int) x][(int) y][(int) z][(int) channel];
            return  val;
        }else {
            System.out.println("Image4DSimple::getIntensity() error: index exceeds the image size");
            return 0;
        }
    }

    public boolean setRez_x(double rez_x) {
        if(rez_x<0)
            return false;
        else {
            this.rez_x = rez_x;
            return true;
        }
    }

    public boolean setRez_y(double rez_y) {
        if(rez_y<0)
            return false;
        else {
            this.rez_y = rez_y;
            return true;
        }
    }

    public boolean setRez_z(double rez_z) {
        if(rez_z<0)
            return false;
        else {
            this.rez_z = rez_z;
            return true;
        }
    }

    public void setOrigin_x(double origin_x) {
        this.origin_x = origin_x;
    }

    public void setOrigin_y(double origin_y) {
        this.origin_y = origin_y;
    }

    public void setOrigin_z(double origin_z) {
        this.origin_z = origin_z;
    }

    public void setImgSrcFile(String imgSrcFile) {
        this.imgSrcFile = imgSrcFile;
    }

    public String getImgSrcFile() {
        return imgSrcFile;
    }

    public void loadImage(String filename){

    }
}
