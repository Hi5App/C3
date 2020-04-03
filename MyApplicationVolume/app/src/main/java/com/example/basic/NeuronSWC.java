package com.example.basic;

import java.util.ArrayList;


public class NeuronSWC extends BasicSurfObj {
    public int type;
    public float x, y, z;

    public float radius;

    public long parent;

    public long level;
    public ArrayList<Float> fea_val;

    public long seg_id;
    public long nodeinseg_id;

    public long creatmode;
    public double timestamp;

    public double tfresindex;

    public NeuronSWC(){
        n = parent = 0;
        type = 0;
        x = y = z = radius = 0;
        seg_id = -1;
        nodeinseg_id = 0;
        fea_val = new ArrayList<Float>();
        level = -1;
        creatmode = 0;
        timestamp = 0;
        tfresindex = 0;
    }
}

