package com.penglab.hi5.basic.image;

public class RGB32f implements Cloneable{
    public float r;
    public float g;
    public float b;

    public RGB32f(){
        r = 0;
        g = 0;
        b = 0;
    }

    public RGB32f(XYZ a){
        r = a.x;
        g = a.y;
        b = a.z;
    }

    @Override
    public RGB32f clone() throws CloneNotSupportedException {
        return (RGB32f) super.clone();
    }
}
