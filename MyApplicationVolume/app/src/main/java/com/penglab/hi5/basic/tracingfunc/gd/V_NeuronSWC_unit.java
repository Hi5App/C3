package com.penglab.hi5.basic.tracingfunc.gd;

import android.util.Log;

import java.util.Vector;

public class V_NeuronSWC_unit implements Cloneable {
    public double n, type, x, y, z, r, parent, nchild, seg_id, nodeinseg_id, level, creatmode, timestamp, tfresindex;

    public V_NeuronSWC_unit()
    {n = type = x = y = z = parent = nchild = seg_id = nodeinseg_id = level = creatmode = timestamp = tfresindex = 0;r=0.5;}

    @Override
    public V_NeuronSWC_unit clone() throws CloneNotSupportedException {
        return (V_NeuronSWC_unit) super.clone();
    }
    // operator V_NeuronSWC_coord() {V_NeuronSWC_coord c; c.x=x; c.y=y; c.z=z; return c;}
    public V_NeuronSWC_coord get_coord() {
        V_NeuronSWC_coord c = new V_NeuronSWC_coord(x,y,z); return c;}
    public void set(double x1, double y1, double z1, double r1, double p1, double t1) {x=x1; y=y1; z=z1; r=r1;parent=p1;type=t1;}
    public void set(double x1, double y1, double z1, double r1, double p1) {x=x1; y=y1;z=z1;r=r1;parent=p1;}
    public void set(double x1, double y1, double z1, double r1) {x=x1; y=y1;z=z1;r=r1;}
    public void set(double x1, double y1, double z1) {x=x1; y=y1;z=z1;}
    public void setType(double t) {type = t;}

    //should be a struct at least included members (n, parent)
    public static void reset_simple_path_index (int base_n, Vector<V_NeuronSWC_unit>  mUnit)
    {
        int N = mUnit.size();
        for (int i=0; i<mUnit.size(); i++)
        {
            if (mUnit.elementAt(0).parent >=1) // same as index order
            {
                mUnit.elementAt(i).n = base_n +1+i;
                mUnit.elementAt(i).parent = (i>=N-1)? -1: (mUnit.elementAt(i).n +1);
            }
            else                    // reversed link order
            {
                mUnit.elementAt(i).n = base_n +1+i;
                mUnit.elementAt(i).parent = (i<=0)? -1: (mUnit.elementAt(i).n -1);
            }
        }
    }

    public void move(float [] dis, float l){
        Log.v("movebefore", Double.toString(x) + ' ' + Double.toString(y) + ' ' + Double.toString(z));
        x += dis[0] * l;
        y += dis[1] * l;
        z += dis[2] * l;
        Log.v("moveafter", Double.toString(x) + ' ' + Double.toString(y) + ' ' + Double.toString(z));

    }
}
