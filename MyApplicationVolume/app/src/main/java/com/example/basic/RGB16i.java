package com.example.basic;

public class RGB16i {
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
}
