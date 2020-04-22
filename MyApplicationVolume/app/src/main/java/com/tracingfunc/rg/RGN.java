package com.tracingfunc.rg;

import java.util.Vector;

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
