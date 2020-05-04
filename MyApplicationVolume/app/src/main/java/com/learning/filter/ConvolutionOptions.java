package com.learning.filter;

import java.util.Vector;

public class ConvolutionOptions {
    public int dim;
    public Vector<Float> sigmaEff;
    public Vector<Float> sigmaD;
    public Vector<Integer> stepSize;
    public Vector<Integer> outerScale;

    private int index;

    public double windowRatio;

    public ConvolutionOptions(ConvolutionOptions c){
        for(int i=0; i<c.sigmaEff.size(); i++){
            this.sigmaEff.add(c.sigmaEff.get(i));
            this.sigmaD.add(c.sigmaD.get(i));
            this.stepSize.add(c.stepSize.get(i));
            this.outerScale.add(c.outerScale.get(i));
        }
        this.dim = c.dim;
        this.index = c.index;
        this.windowRatio = c.windowRatio;
    }

    public double sigmaScaled(){
        if(sigmaEff.get(index)<0 || sigmaD.get(index)<0){
            System.out.println("Scale must be positive");
        }
        double sigmaSquared = sigmaEff.get(index)*sigmaEff.get(index) - sigmaD.get(index)*sigmaD.get(index);
        if(sigmaSquared>0.0){
            return Math.sqrt(sigmaSquared)/stepSize.get(index);
        }else {
            System.out.println("Scale would be imaginary");
            return 0;
        }
    }

    public boolean next(){
        if(index+1>=sigmaD.size()){
            return false;
        }
        index++;
        return true;
    }

    public double getWindowRatio() {
        return windowRatio;
    }

    public double sigmaEff(){
        return sigmaEff.get(index);
    }

    public double sigmaD(){
        return sigmaD.get(index);
    }

    public double stepSize(){
        return stepSize.get(index);
    }

    public void reset(){
        index = 0;
    }

    public int getDim() {
        return dim;
    }
}
