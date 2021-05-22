package com.penglab.hi5.basic.tracingfunc.app2;

import java.util.Vector;

public class HierarchySegment {
    HierarchySegment parent;
    MyMarker leaf_marker;
    MyMarker root_marker;
    double length;
    int level;

    public HierarchySegment(){
        leaf_marker = null;
        root_marker = null;
        length = 0;
        level = 1;
        parent = null;
    }

    public HierarchySegment(MyMarker leaf, MyMarker root, double len, int level){
        leaf_marker = leaf;
        root_marker = root;
        length = len;
        this.level = level;
        parent = null;
    }

    public void getMarkers(Vector<MyMarker> outswc){
        if(leaf_marker==null || root_marker==null)
            return;
        MyMarker p = leaf_marker;
        while (!p.equals(root_marker)){
            outswc.add(p);
            p = p.parent;
        }
        outswc.add(root_marker);
    }

    public boolean smoothCurve(int winsize){
        Vector<MyMarker> outswc = new Vector<MyMarker>();
        MyMarker.smoothCurve(outswc,winsize);
        return true;
    }
}
