package com.penglab.hi5.basic.learning.filter;

import java.util.Arrays;

public class MultiArrayNavigator {
    private int[] array;
    private int[] shape;
    private int[] point;
    private int innerShape;
    private int innerDimension;
    private int step;
    private int index;
    public MultiArrayNavigator(int[] array, int[] shape, int innerDimension){
        this.array = array;
        this.shape = shape;
        this.innerShape = shape[innerDimension];
        this.innerDimension = innerDimension;
        this.step = 1;
        for(int i = this.innerDimension; i>0; i--){
            this.step *= shape[i-1];
        }
        this.index = 0;
        this.point = new int[shape.length];
        Arrays.fill(this.point,0);
    }

    public int[] getLine(){
        int[] line = new int[innerShape];
        int indexTmp = index;
        for(int i=0; i<innerShape; i++){
            line[i] = array[indexTmp];
            indexTmp += step;
        }
        return line;
    }

    public boolean setLine(int[] line){
        if(line.length != innerShape)
            return false;
        int indexTmp = index;
        for(int i=0; i<innerShape; i++){
            array[indexTmp] = line[i];
            indexTmp += step;
        }
        return true;
    }

    public boolean next(){
        int iTmp = 0;
        if(innerDimension == iTmp){
            iTmp++;
            if(iTmp >= point.length)
                return false;
        }
        point[iTmp]++;
        while (point[iTmp] >= shape[iTmp]){
            if(iTmp + 1 == innerDimension){
                iTmp++;
                if(iTmp + 1 >= point.length) {
                    return false;
                }else {
                    point[iTmp-1] = 0;
                    point[iTmp+1]++;
                    iTmp = iTmp+1;
                }
            }else {
                if(iTmp + 1 >= point.length) {
                    return false;
                }else {
                    point[iTmp] = 0;
                    point[iTmp+1]++;
                    iTmp = iTmp+1;
                }
            }
        }
        this.index = 0;
        for(int i=0; i<point.length; i++){
            int tmp = point[i];
            for(int j=i; j>0; j--){
                tmp *= shape[j-1];
            }
            this.index += tmp;
        }
        return true;
    }
}
