package com.example.basic;

public class RGB8 implements Cloneable{
    public char r;
    public char g;
    public char b;

    public RGB8(){
        r = '0';
        g = '0';
        b = '0';
    }

    public RGB8(XYZ a){
        r = (char)a.x;
        g = (char)a.y;
        b = (char)a.z;
    }

    @Override
    public RGB8 clone() throws CloneNotSupportedException {
        return (RGB8) super.clone();
    }
}
