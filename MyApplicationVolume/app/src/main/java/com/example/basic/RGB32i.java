package com.example.basic;

public class RGB32i {
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
}
