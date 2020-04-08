package com.tracingfunc.app2;

public class HeapElem {
    int heap_id;
    int[] img_index = new int[3];
    double value;
    public HeapElem(int[] img_index, double value){
        if(img_index.length!=this.img_index.length){
            throw new ExceptionInInitializerError();
        }
        for(int i = 0;i<this.img_index.length;i++) {
            this.img_index[i] = img_index[i];
        }
        this.value = value;
        this.heap_id = -1;
    }
}
