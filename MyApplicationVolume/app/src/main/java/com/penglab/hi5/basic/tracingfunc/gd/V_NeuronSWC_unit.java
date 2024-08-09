package com.penglab.hi5.basic.tracingfunc.gd;

import android.util.Log;

import com.penglab.hi5.R;

import java.util.HashMap;
import java.util.Vector;

public class V_NeuronSWC_unit implements Cloneable {
    private static final int DEFAULT_TYPE = 3;

    private static final HashMap<Integer, Integer> typeToColor = new HashMap<Integer, Integer>() {{
        put(0, R.color.white_map);
        put(1, R.color.black_map);
        put(2, R.color.red_map);
        put(3, R.color.blue_map);
        put(4, R.color.purple_map);
        put(5, R.color.cyan_map);
        put(6, R.color.yellow_map);
        put(7, R.color.green_map);
        put(8, R.color.pink_map);
        put(18, R.color.crossing_map);
    }};

    private static final HashMap<String, Integer> colorToType = new HashMap<String, Integer>() {{
        put("FFFFFFFF",  0);
        put("FF141414",  1);
        put("FFC81400",  2);
        put("FF0014C8",  3);
        put("FFC814C8",  4);
        put("FF00C8C8",  5);
        put("FFDCC800",  6);
        put("FF00C814",  7);
        put("FFFA6478",  8);
        put("FFA880FF", 18);
    }};

    public double n, type, x, y, z, r, parent, nchild, seg_id, nodeinseg_id, level, creatmode, timestamp, tfresindex;
    public double xGlobal, yGlobal, zGlobal;

    public V_NeuronSWC_unit()
    {n = type = x = y = z = parent = nchild = seg_id = nodeinseg_id = level = creatmode = timestamp = tfresindex = 0;r=0.5;
    xGlobal = yGlobal = zGlobal = 0;}

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
        x += dis[0] * l;
        y += dis[1] * l;
        z += dis[2] * l;
    }

    public static int typeToColor(int type){
        if (!typeToColor.containsKey(type)){
            return typeToColor.get(DEFAULT_TYPE);
        } else {
            return typeToColor.get(type);
        }
    }

    public static int colorToType(String color){
        if (!colorToType.containsKey(color)){
            return DEFAULT_TYPE;
        } else {
            return colorToType.get(color);
        }
    }
}
