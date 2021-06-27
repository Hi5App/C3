package com.penglab.hi5.basic.image;

public class RGB16i implements Cloneable{
    public short r;
    public short g;
    public short b;

    public RGB16i(){
        r = 0;
        g = 0;
        b = 0;
    }

    public RGB16i(XYZ a){
        r = (short)a.x;
        g = (short)a.y;
        b = (short)a.z;
    }

    @Override
    public RGB16i clone() throws CloneNotSupportedException {
        return (RGB16i) super.clone();
    }
}
