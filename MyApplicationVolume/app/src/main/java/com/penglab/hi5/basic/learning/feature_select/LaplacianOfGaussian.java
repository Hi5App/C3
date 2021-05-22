package com.penglab.hi5.basic.learning.feature_select;

public class LaplacianOfGaussian {
    public static void filter3d(float sigma, int[] inPixels, int[] mask, int[] outPixels,
                              int weight, int height, int depth, int edgeAction) {
        GaussianBlur g = new GaussianBlur(3,sigma,3);
        Kernel gKernel  = g.getKernel();
        float[] lKernel = new float[27];
        for(int i=0; i<lKernel.length; i++){
            if(i==1*3*3+1*3+1)
                lKernel[i] = -6;
            if(i==0*3*3+1*3+1 || i==2*3*3+1*3+1
            || i==1*3*3+0*3+1 || i==1*3*3+2*3+1
            || i==1*3*3+1*3+0 || i==1*3*3+1*3+2){
                lKernel[i] = 1;
            }
        }
        Kernel l = new Kernel(lKernel,3,3,3);

        int[] out = new int[inPixels.length];
        GaussianBlur.convolveAndTranspose(gKernel,inPixels,mask,out,weight,height,depth,edgeAction);
        GaussianBlur.convolveAndTranspose(l,out,mask,outPixels,weight,height,depth,edgeAction);
    }
}
