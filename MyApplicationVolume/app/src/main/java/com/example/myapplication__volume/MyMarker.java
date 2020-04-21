package com.example.myapplication__volume;

public class MyMarker {
    double x;
    double y;
    double z;
    double radius;
    int type;

    MyMarker parent;

    public MyMarker(){ x = y = z = radius =0.0; type = 3; parent = null; }
    public MyMarker(double _x, double _y, double _z) {x = _x; y = _y; z = _z; radius = 0.0; type = 3; parent = null; }
    public MyMarker(final MyMarker v){ x = v.x; y = v.y; z = v.z; radius = v.radius; type = v.type; parent = v.parent; }

    public boolean equals(final MyMarker other){
        return ( this.x == other.x && this.y == other.y && this.z == other.z );
    }

    public boolean greater(final MyMarker other){
        if(this.z < other.z) return false;
        if(this.z > other.z) return true;
        if(this.y < other.y) return false;
        if(this.y > other.y) return true;
        if(this.x < other.x) return false;
        if(this.x > other.x) return true;
        return false;
    }

}
