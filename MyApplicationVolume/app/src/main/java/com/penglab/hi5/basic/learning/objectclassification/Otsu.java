package com.penglab.hi5.basic.learning.objectclassification;
/*
public class Otsu {
    public void Otsu(int high,int width){
        toGray();
        int h=high;
        int w=width;
        int num = h*w;
        int []date ;
        int[] hist = hist();
        int sum = Math.sum(hist);
        double[] res = new double[256];
        double m1=0,m2=0,w1=0,w2=0;

        for(int k=0;k<256;k++){
            for(int i=0;i<k;i++){
                m1 +=hist[i];
            }
            w1 = m1/num;
            w2 = 1-w1;
            m2 = (sum-m1)/(255-k);
            m1 = m1/(k+1);
            res[k] = w1*w2*Math.abs(m1 - m2)*Math.abs(m1 - m2);
        }

        int Threshold = Math.maxIndex(res); //获得最大值的下标

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if(data[x + y * w] < Threshold){
                    data[x + y * w] = 0;
                }else{
                    data[x + y * w] = 255; }
            }
        }




    }



}
*/