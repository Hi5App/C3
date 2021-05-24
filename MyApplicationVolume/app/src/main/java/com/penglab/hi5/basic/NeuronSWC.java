package com.penglab.hi5.basic;

import com.penglab.hi5.basic.image.BasicSurfObj;

import java.util.ArrayList;
import java.util.Vector;


public class NeuronSWC extends BasicSurfObj implements Cloneable{
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

    public Vector<Integer> children;

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
        children = new Vector<Integer>();
    }

    @Override
    public NeuronSWC clone() throws CloneNotSupportedException {
        NeuronSWC n = null;
        n = (NeuronSWC) super.clone();
        n.fea_val = new ArrayList<Float>(this.fea_val.size());
        for(int i=0; i<this.fea_val.size(); i++){
            n.fea_val.set(i,this.fea_val.get(i));
        }
        return n;
    }
}

