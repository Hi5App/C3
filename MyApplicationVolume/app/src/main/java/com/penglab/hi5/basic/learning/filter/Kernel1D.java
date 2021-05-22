package com.penglab.hi5.basic.learning.filter;

import java.util.Arrays;
import java.util.Vector;

public class Kernel1D {
    public enum BorderTreatmentMode{
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

        if(norm != 0.0){
            this.normalize(norm);
        }else {
            this.norm = 1.0;
        }

        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initGaussian(double stdDev){
        initGaussian(stdDev,1.0,0.0);
    }

    public void initDiscreteGaussian(double stdDev, double norm){
        if(stdDev>0.0){
            int radius = (int) (3.0*stdDev +0.5);
            if(radius == 0)
                radius = 1;

            double f = 2.0/(stdDev*stdDev);

            int maxIndex = (int) (2.0*(radius + 5.0*Math.sqrt(radius)) + 0.5);
            double[] warray = new double[maxIndex+1];
            warray[maxIndex] = 0.0;
            warray[maxIndex-1] = 1.0;

            for (int i=maxIndex-1; i>=radius; --i){
                warray[i] = warray[i+2] + f*(i+1)*warray[i+1];
                if(warray[i]>1.0e40){
                    warray[i+1] /= warray[i];
                    warray[i] = 1.0;
                }
            }

            double er = Math.exp(-radius*radius/(2.0*stdDev*stdDev));
            warray[radius+1] = er*warray[radius+1]/warray[radius];
            warray[radius] = er;

            for(int i= radius-1; i>=0; --i){
                warray[i] = warray[i+2] + f*(i+1)*warray[i+1];
                er += warray[i];
            }

            double scale = norm/(2*er - warray[0]);

            double[] kernel = new double[2*radius+1];
            int c = radius;
            for(int i=0; i<=radius; i++){
                kernel[c+i] = kernel[c-i] = warray[i]*scale;
            }
            this.initExplicitly(-radius,radius,kernel);
        }else {
            this.kernel.clear();
            this.kernel.add(norm);
            this.left = 0;
            this.right = 0;
        }

        this.norm = norm;
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initDiscreteGaussian(double stdDev){
        initDiscreteGaussian(stdDev,1.0);
    }

    public void initGaussianDerivative(double stdDev, int order, double norm, double windowRatio){
        if(order<0){
            System.out.println("order is less than zero!");
        }
        if(order == 0){
            initGaussian(stdDev,norm,windowRatio);
            return;
        }

        Gaussian gauss = new Gaussian(stdDev,order);
        int radius;
        if(windowRatio == 0.0)
            radius = (int) ((3.0 + 0.5*order)*stdDev + 0.5);
        else
            radius = (int) (windowRatio*stdDev + 0.5);
        if(radius == 0)
            radius = 1;

        this.kernel.clear();
        double dc = 0.0;

        for(int x = -radius; x <= radius; ++x){
            this.kernel.add(gauss.gaussian(x));
            dc += this.kernel.get(this.kernel.size()-1);
        }
        dc = dc/(2.0*radius + 1.0);

        if(norm != 0.0){
            for(int i=0; i<this.kernel.size(); ++i){
                double k = this.kernel.get(i) - dc;
                this.kernel.set(i,k);
            }
        }

        this.left = -radius;
        this.right = radius;

        if(norm != 0.0){
            this.normalize(norm,order);
        }else
            this.norm = 1.0;
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initGaussianDerivative(double stdDev, int order){
        initGaussianDerivative(stdDev,order,1.0,0.0);
    }

    public void normalize(double norm, int derivativeOrder, double offset){
        double sum = 0;
        if(derivativeOrder == 0){
            for(int i=0; i<kernel.size(); i++){
                sum += kernel.get(i);
            }
        }else {
            int faculty = 1;
            for(int i=2; i<=derivativeOrder; ++i){
                faculty *= i;
            }
            double x = this.getLeft() + offset;
            for(int i=0; i<kernel.size(); ++i){
                sum = sum + kernel.get(i)*Math.pow(-x,(int) derivativeOrder/faculty);
                x++;
            }
        }
        if(sum!=0){
            sum = norm/sum;
            for(int i=0; i<kernel.size(); i++){
                double k = kernel.get(i)*sum;
                kernel.set(i,k);
            }
        }
        else {
            System.out.println("sum is zero!");
        }

        this.norm = norm;
    }

    public void normalize(double norm, int derivativeOrder){
        normalize(norm,derivativeOrder,0.0);
    }

    public void normalize(double norm){
        normalize(norm,0);
    }

    public void normalize(){
        normalize(1.0);
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public double getNorm() {
        return norm;
    }

    public int size(){
        return right - left + 1;
    }

    public BorderTreatmentMode getBorderTreatmentMode() {
        return borderTreatmentMode;
    }

    public void setBorderTreatmentMode(BorderTreatmentMode borderTreatmentMode) {
        this.borderTreatmentMode = borderTreatmentMode;
    }

    public double getElement(int location){
        return kernel.get(location - this.getLeft());
    }

    public void setElement(int location, double k){
        this.kernel.set(location - this.getLeft(),k);
    }

    public double center(){
        return kernel.get(0-this.getLeft());
    }

    public boolean initExplicitly(int left, int right, double[] kernel){
        if(left>0 || right<0 || right-left+1 != kernel.length){
            System.out.println("parameter is false!");
            return false;
        }
        this.left = left;
        this.right = right;
        this.kernel.clear();
        for(int i=0; i<kernel.length; i++){
            this.kernel.add(kernel[i]);
        }
        return true;
    }

    public void initOptimalSmoothing3(){
        double[] kernel = new double[]{0.216,0.568,0.216};
        this.initExplicitly(-1,1,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initOptimalFirstDerivativeSmoothing3(){
        double[] kernel = new double[]{0.224365,0.55127,0.224365};
        this.initExplicitly(-1,1,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initOptimalSecondDerivativeSmoothing3(){
        double[] kernel = new double[]{0.13,0.74,0.13};
        this.initExplicitly(-1,1,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initOptimalSmoothing5(){
        double[] kernel = new double[]{0.03134,0.24,0.45732,0.24,0.03134};
        this.initExplicitly(-2,2,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initOptimalFirstDerivativeSmoothing5(){
        double[] kernel = new double[]{0.04255,0.241,0.4329,0.241,0.04255};
        this.initExplicitly(-2,2,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initOptimalSecondDerivativeSmoothing5(){
        double[] kernel = new double[]{0.0243,0.23556,0.48028,0.23556,0.0243};
        this.initExplicitly(-2,2,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public boolean initBurtFilter(double a){
        if(a>=0.0 && a<= 0.0125){
            double[] kernel = new double[]{a,0.25,0.5-a*2,0.25,a};
            this.initExplicitly(-2,2,kernel);
            this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
            return true;
        }else {
            return false;
        }
    }

    public boolean initBurtFilter(){
        double a = 0.04785;
        return this.initBurtFilter(a);
    }

    public void initBinomial(int radius, double norm){
        double[] kernel = new double[radius*2 + 1];
        Arrays.fill(kernel, 0);
        int c = radius;
        kernel[c+radius] = norm;
        for(int j=radius-1; j>=-radius; --j){
            kernel[c+j] = 0.5*kernel[c+j+1];
            for(int i=j+1; i<radius; ++i){
                kernel[c+i] = 0.5*(kernel[c+i] + kernel[c+i+1]);
            }
            kernel[c+radius] *= 0.5;
        }

        this.initExplicitly(-radius,radius,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initBinomial(int radius){
        initBinomial(radius,1.0);
    }

    public void initAveraging(int radius, double norm){
        double scale = 1.0/(radius*2 + 1);
        this.kernel.clear();

        for(int i=0; i<=radius*2+1; ++i){
            this.kernel.add(scale*norm);
        }

        this.left = -radius;
        this.right = radius;
        this.norm = norm;
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_CLIP);
    }

    public void initAveraging(int radius){
        initAveraging(radius,1.0);
    }

    public void initSymmetricGradient(double norm){
        initSymmetricDifference(norm);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initSymmetricGradient(){
        initSymmetricGradient(1.0);
    }

    public void initForwardDifference(){
        double[] kernel = new double[]{1.0,-1.0};
        this.initExplicitly(-1,0,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initBackwardDifference(){
        double[] kernel = new double[]{1.0,-1.0};
        this.initExplicitly(0,1,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initSymmetricDifference(double norm){
        double[] kernel = new double[]{0.5*norm,0.0*norm,-0.5*norm};
        this.initExplicitly(-1,1,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initSymmetricDifference(){
        this.initSymmetricDifference(1.0);
    }

    public void initSecondDifference3(){
        double[] kernel = new double[]{1.0,-2.0,1.0};
        this.initExplicitly(-1,1,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initOptimalFirstDerivative5(){
        double[] kernel = new double[]{0.1,0.3,0.0,-0.3,-0.1};
        this.initExplicitly(-2,2,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void initOptimalSecondDerivative5(){
        double[] kernel = new double[]{0.22075,0.117,-0.6755,0.117,0.22075};
        this.initExplicitly(-2,2,kernel);
        this.setBorderTreatmentMode(BorderTreatmentMode.BORDER_TREATMENT_REFLECT);
    }

    public void scaleKernel(double a){
        for(int i=this.getLeft(); i<=this.getRight(); i++){
            this.setElement(i,this.getElement(i)*a);
        }
    }
}
