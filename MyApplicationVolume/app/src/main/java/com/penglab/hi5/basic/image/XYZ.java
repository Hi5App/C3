package com.penglab.hi5.basic.image;

public class XYZ implements Cloneable{
    public float x;
    public float y;
    public float z;

    public XYZ(float px, float py, float pz){
        x = px;
        y = py;
        z = pz;
    }

    public XYZ(float a){
        x = a;
        y = a;
        z = a;
    }

    public XYZ(){   //a = 0 when default
        x = 0;
        y = 0;
        z = 0;
    }
    public XYZ(RGB8 c){
        x = c.r;
        y = c.g;
        z = c.b;
    }

    public XYZ(RGB16i c){
        x = c.r;
        y = c.g;
        z = c.b;
    }

    public XYZ(RGB32i c){
        x = c.r;
        y = c.g;
        z = c.b;
    }

    public XYZ(RGB32f c){
        x = c.r;
        y = c.g;
        z = c.b;
    }

    @Override
    public XYZ clone() throws CloneNotSupportedException {
        return (XYZ) super.clone();
    }

    public static float norm(XYZ a){
        return (float)Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
    }

    public static XYZ normalize(XYZ a){
        float m = norm(a);
        if (m > 0){
            a.x /= m;
            a.y /= m;
            a.z /= m;
        }
        return a;
    }


}
