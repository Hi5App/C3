package com.penglab.hi5.basic.tracingfunc.gd;

public class BDB_Minus_Prior_Parameter {
    public double f_prior, f_smooth, f_length; //the coefficients for the prior force, smoothness and length forces, respectively.
    public double Kfactor; //the factor in "mean+Kfactor*sigma" of an image that would be used to find the backbone
    public int nloops;

    BDB_Minus_Prior_Parameter()
    {
        f_prior = 0.2;
        f_smooth = 0.1;
        f_length = 0.1;
        Kfactor = 1.0;
        nloops = 100;
    }
}
