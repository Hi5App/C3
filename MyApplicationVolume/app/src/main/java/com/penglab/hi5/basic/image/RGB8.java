package com.penglab.hi5.basic.image;

import java.util.Random;

import static com.penglab.hi5.basic.image.XYZ.normalize;

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

    public static RGB8 random_rgb8(Random rd){
        XYZ d = new XYZ(rd.nextInt(255), rd.nextInt(255), rd.nextInt(255));
        normalize(d);
        RGB8 c = new RGB8();
        c.r = (char)(d.x * 255);
        c.g = (char)(d.y * 255);
        c.b = (char)(d.z * 255);
        return c;
    }
}
