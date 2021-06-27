package com.penglab.hi5.basic.tracingfunc.app2;

public class HeapElem {
    public int heap_id;
    public int img_index;
    public double value;
    public HeapElem(int img_index, double value){
        this.heap_id = -1;
        this.img_index = img_index;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public int getImg_index() {
        return img_index;
    }
}
