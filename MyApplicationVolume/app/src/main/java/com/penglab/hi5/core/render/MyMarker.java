package com.penglab.hi5.core.render;

public class MyMarker {
    public double x;
    public double y;
    public double z;
    public double radius;
    public int type;

    public MyMarker parent;

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

    public long ind( long sz0, long sz01)
    {
        return ( (long)( z + 0.5 ) * sz01 + (long)( y + 0.5 ) * sz0 + (long)( x + 0.5 ));
    }

}
