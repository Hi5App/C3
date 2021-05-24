package com.penglab.hi5.basic.image;

import java.util.Random;

import static com.penglab.hi5.basic.image.RGB8.random_rgb8;

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

    public static RGBA8 random_rgba8(char a, Random rd){
        RGB8 c = new RGB8();
        c = random_rgb8(rd);
        RGBA8 cc = new RGBA8(c.r, c.g, c.b, a);
        return cc;
    }
}
