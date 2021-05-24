package com.penglab.hi5.basic.image;

public class RGB32i implements Cloneable{
    public int r;
    public int g;
    public int b;

    public RGB32i(){
        r = 0;
        g = 0;
        b = 0;
    }

    public RGB32i(XYZ a){
        r = (int)a.x;
        g = (int)a.y;
        b = (int)a.z;
    }

    @Override
    public RGB32i clone() throws CloneNotSupportedException {
        return (RGB32i) super.clone();
    }
}
