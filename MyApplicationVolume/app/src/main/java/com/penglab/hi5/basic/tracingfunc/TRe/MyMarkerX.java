package com.penglab.hi5.basic.tracingfunc.TRe;

import com.penglab.hi5.basic.tracingfunc.app2.MyMarker;

public class MyMarkerX extends MyMarker {
    public double feature;
    public int seg_id;
    public int seg_level;
    public MyMarkerX()
    {
        super();
        seg_id = -1;
        seg_level = -1;
        feature = 0.0;
    }
    public MyMarkerX(MyMarker _marker)
    {
        x = _marker.x;
        y = _marker.y;
        z = _marker.z;
        //type = _marker.type;
        radius = _marker.radius;
        seg_id = -1;
        seg_level = -1;
        feature = 0.0;
    }
    public MyMarkerX(double _x, double _y, double _z)
    {
        super(_x, _y, _z);
        seg_id = -1;
        seg_level = -1;
        feature = 0.0;
    }
}
