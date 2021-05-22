package com.penglab.hi5.basic.tracingfunc.rg;

public class RGN {
    public int layer;
    public int no;
    public POS poslist;
    public int poslistlen;
    public RGN next;

    public RGN(){
        layer = no = -1;
        poslist = null;
        next = null;
    }
}
