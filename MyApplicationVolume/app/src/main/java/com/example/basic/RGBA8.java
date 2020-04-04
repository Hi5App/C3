package com.example.basic;

public class RGBA8 implements Cloneable{
    public char r;
    public char g;
    public char b;
    public char a;
    public RGBA8(char r, char g, char b, char a){
        this.r = r; this.g = g; this.b = b; this.a = a;
    }

    @Override
    protected RGBA8 clone() throws CloneNotSupportedException {
        return (RGBA8) super.clone();
    }
}
