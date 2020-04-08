package com.tracingfunc.app2;

import androidx.annotation.NonNull;

public class MyMarker implements Cloneable{
    public double x,y,z;
    public double radius;
    int type;
    MyMarker(){x=y=z=radius=0.0; type = 3;}
    MyMarker(double _x, double _y, double _z) {x = _x; y = _y; z = _z; radius = 0.0; type = 3;}
    MyMarker(MyMarker  v){x=v.x; y=v.y; z=v.z; radius = v.radius; type = v.type;}

    @NonNull
    @Override
    public MyMarker clone() throws CloneNotSupportedException {
        return (MyMarker) super.clone();
    }
}
