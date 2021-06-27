package com.penglab.hi5.basic.learning.feature_select;

public class DifferenceOfGaussian {
    public static void filter3d(float sigma, int[] inPixels, int[] mask, int[] outPixels,
                                            int weight, int height, int depth, int edgeAction) {
        GaussianBlur g1 = new GaussianBlur(3,0.3f,3);
        GaussianBlur g2 = new GaussianBlur(3,sigma,3);
        int[] out1 = new int[inPixels.length];
        int[] out2 = new int[inPixels.length];
        GaussianBlur.convolveAndTranspose(g1.getKernel(),inPixels,mask,out1,weight,height,depth,edgeAction);
        GaussianBlur.convolveAndTranspose(g2.getKernel(),inPixels,mask,out2,weight,height,depth,edgeAction);
        for(int i=0; i<outPixels.length; i++){
            outPixels[i] = Math.max((out2[i] - out1[i]), 0);
        }
    }
}
