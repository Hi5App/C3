package com.penglab.hi5.basic.tracingfunc.app2;

public class HeapElemX extends HeapElem{
    public int prev_index;
    public HeapElemX(int img_index, int prev_index, double value) {
        super(img_index, value);
        this.prev_index = prev_index;
    }

    public int getPrev_index() {
        return prev_index;
    }

    public void setPrev_index(int prev_index) {
        this.prev_index = prev_index;
    }
}
