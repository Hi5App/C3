package com.penglab.hi5.basic.tracingfunc.gd;

public class V_NeuronSWC_coord implements Cloneable{
    public double x,y,z;
    public V_NeuronSWC_coord(double x1, double y1, double z1){x = x1; y = y1; z = z1;}

    @Override
    protected V_NeuronSWC_coord clone() throws CloneNotSupportedException {
        return (V_NeuronSWC_coord) super.clone();
    }

    public boolean equal(V_NeuronSWC_coord other)  {if (x==other.x && y==other.y && z==other.z) return true; else return false;}
    public boolean equal(double x1, double y1, double z1)  {if (x==x1 && y==y1 && z==z1) return true; else return false;}
    public boolean nonequal(V_NeuronSWC_coord other)  {
        V_NeuronSWC_coord p = new V_NeuronSWC_coord(x,y,z); return !p.equal(other);}
    public boolean nonequal(double x1, double y1, double z1)  {
        V_NeuronSWC_coord p = new V_NeuronSWC_coord(x,y,z); return !p.equal(x1, y1, z1);}
}
