package com.example.basic;

public class RGB32f {
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
}
