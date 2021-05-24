package com.penglab.hi5.basic;

import com.penglab.hi5.basic.image.RGBA8;
import com.penglab.hi5.basic.image.XYZ;

import java.util.Random;

import static com.penglab.hi5.basic.image.RGBA8.random_rgba8;

enum PxLocationUsefulness
{
    pxUnknown, pxLocaNotUseful, pxLocaUseful, pxLocaUnsure, pxTemp
}
enum PxLocationMarkerShape
{
    pxUnset,
    pxSphere,
    pxCube,
    pxCircleX,
    pxCircleY,
    pxCircleZ,
    pxSquareX,
    pxSquareY,
    pxSquareZ,
    pxLineX,
    pxLineY,
    pxLineZ,
    pxTriangle,
    pxDot
}

public class LocationSimple {
    public float x,y,z;
    public float radius;
    PxLocationUsefulness inputProperty;
    PxLocationMarkerShape shape;

    public double pixval;
    public double ave, sdev, skew, curt;
    public double size, mass, pixmax;
    public double ev_pc1, ev_pc2, ev_pc3; //the eigen values of principal components;
    public XYZ mcenter;


    public String name;//the name of a landmark
    public String comments;//other info of the landmark
    public int category;//the type of a particular landmark
    public RGBA8 color;
    boolean on;


    public void init(){
        x = 0;
        y = 0;
        z = 0;
        radius = 5;
        shape = PxLocationMarkerShape.pxSphere;
        inputProperty = PxLocationUsefulness.pxLocaUseful;
        name = "";
        comments = "";
        category = 0;
        //color.r = color.g = color.b = color.a = 255;

        Random rd = new Random();
        color = random_rgba8((char)255,rd);

        ave = sdev = skew = curt = 0;
        size = mass = 0;
        pixmax = 0;

        ev_pc1=ev_pc2=ev_pc3 = -9999; //set as invalid value

        mcenter = new XYZ(0, 0, 0);
//        mcenter.x=0; mcenter.y=0; mcenter.z=0;

        on=true;
    }

    public LocationSimple(float xx, float yy, float zz)
    {
        init();
        x = xx;
        y = yy;
        z = zz;
    }
    public LocationSimple()
    {
        init();
    }
    public int[] getCoord(int xx, int yy, int zz)                 //原本是引用
    {
        int [] coord = new int[3];
        coord[0] = (int) x;
        coord[1] = (int) y;
        coord[2] = (int) z;

        xx = (int) x;
        yy = (int) y;
        zz = (int) z;
        return coord;
    }
    public float[] getCoord(float xx, float yy, float zz)
    {

        float [] coord = new float[3];
        coord[0] = x;
        coord[1] = y;
        coord[2] = z;

        xx = x;
        yy = y;
        zz = z;

        return coord;
    }
    public int getPixVal()
    {
        return (int)pixval;
    }
    public double getAve()
    {
        return ave;
    }
    public double getSdev()
    {
        return sdev;
    }
    public double getSkew()
    {
        return skew;
    }
    public double getCurt()
    {
        return curt;
    }
    public PxLocationUsefulness howUseful()
    {
        return inputProperty;
    }

    //     // == operator for comparison of landmarks
    // bool operator==(const LocationSimple& rhs) const {
    //     return
    //             (  (x == rhs.x)
    //             && (y == rhs.y)
    //             && (z == rhs.z) );
    // }

    public boolean isequal(LocationSimple rhs){
        return
                (  (x == rhs.x)
                        && (y == rhs.y)
                        && (z == rhs.z) );
    }


    public void deepCopy(LocationSimple ls){
        x = ls.x;
        y = ls.y;
        z = ls.z;
        radius = ls.radius;
        inputProperty = ls.inputProperty;
        shape = ls.shape;

        pixval = ls.pixval;
        ave = ls.ave;   sdev = ls.sdev;   skew = ls.skew;   curt = ls.curt;
        size = ls.size; mass = ls.mass; pixmax = ls.pixmax;
        ev_pc1 = ls.ev_pc1; ev_pc2 = ls.ev_pc2; ev_pc3 = ls.ev_pc3;

        mcenter = ls.mcenter;
        name = ls.name;
        comments = ls.comments;
        category = ls.category;
        color = ls.color;
        on = ls.on;
    }
}
