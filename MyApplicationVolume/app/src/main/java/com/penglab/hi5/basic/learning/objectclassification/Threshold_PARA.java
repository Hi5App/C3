package com.penglab.hi5.basic.learning.objectclassification;

public class Threshold_PARA {
    public int  maxsize;
    public int minsize;
    public double highthreshold;
    public double lowthresold ;
    public double[] smoothersigma;
    public int channel;
    public int corechannel;
    public double beta;

    public Threshold_PARA(){
        maxsize=1000000;
        minsize=10;
        highthreshold=0.8;
        lowthresold=0.5 ;
        smoothersigma= new double[]{1.0, 1.0, 1.0};
        channel=0;
        corechannel=0;
        beta=0.2;



    }


}
