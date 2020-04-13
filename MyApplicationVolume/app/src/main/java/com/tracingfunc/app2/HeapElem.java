package com.tracingfunc.app2;

public class HeapElem {
    public int[] img_index;
    public double value;
    public HeapElem(int[] img_index, double value){
        this.img_index = new int[3];
        if(img_index.length!=this.img_index.length){
            throw new ExceptionInInitializerError();
        }
        System.arraycopy(img_index, 0, this.img_index, 0, this.img_index.length);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public int[] getImg_index() {
        return img_index;
    }
}
