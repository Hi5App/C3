package com.tracingfunc.app2;

import android.provider.Settings;

public class HeapElemX extends HeapElem{
    public int[] prev_index;
    public HeapElemX(int[] img_index, int[] prev_index, double value) {
        super(img_index, value);
        this.prev_index = new int[3];
        if(prev_index.length!=this.prev_index.length){
            throw new ExceptionInInitializerError();
        }
        System.arraycopy(prev_index, 0, this.prev_index, 0, this.prev_index.length);
    }

    public int[] getPrev_index() {
        return prev_index;
    }

    public void setPrev_index(int[] prev_index) {
        System.arraycopy(prev_index, 0, this.prev_index, 0, this.prev_index.length);
    }
}
