package com.learning.filter;

import java.util.Vector;

public class Kernel1D {
    enum BorderTreatmentMode{
        BORDER_TREATMENT_AVOID,
        BORDER_TREATMENT_CLIP,
        BORDER_TREATMENT_REPEAT,
        BORDER_TREATMENT_REFLECT,
        BORDER_TREATMENT_WRAP,
        BORDER_TREATMENT_ZEROPAD};
    private Vector<Double> kernel = new Vector<>();
    private int left,right;
    BorderTreatmentMode borderTreatmentMode;
    private double norm;
    public Kernel1D(){
        left = 0;
        right = 0;
        borderTreatmentMode = BorderTreatmentMode.BORDER_TREATMENT_REFLECT;
        norm = 1.0;
    }

    public Kernel1D(final Kernel1D k){
        for(int i=0; i<k.kernel.size(); i++){
            this.kernel.add(k.kernel.get(i));
        }
        this.left = k.left;
        this.right = k.right;
        this.borderTreatmentMode = k.borderTreatmentMode;
        this.norm = k.norm;
    }

    public void initGaussian(double stdDev, double norm, double windowRatio){
        if(stdDev>0){
            Gaussian gauss = new Gaussian(stdDev);

            int radius;
            if(windowRatio == 0.0){
                radius = (int) (stdDev*3 +0.5);
            }else {
                radius = (int) (stdDev*windowRatio +0.5);
            }
            if(radius == 0)
                radius = 1;
            kernel.clear();
            for(int x = -radius; x <= radius; x++){
                kernel.add(gauss.gaussian(x));
            }
            left = -radius;
            right = radius;
        }else {
            kernel.clear();
            kernel.add(1.0);
            left = 0;
            right = 0;
        }
    }

    public void initGaussian(double stdDev){
        initGaussian(stdDev,1.0,0.0);
    }

    public void initDiscreteGaussian(double stdDev, double norm){

    }

    public void initDiscreteGaussian(double stdDev){
        initDiscreteGaussian(stdDev,1.0);
    }

    public void initGaussianDerivative(double stdDev, int order, double norm, double windowRatio){

    }

    public void initGaussianDerivative(double stdDev, int order){
        initGaussianDerivative(stdDev,order,1.0,0.0);
    }
}
