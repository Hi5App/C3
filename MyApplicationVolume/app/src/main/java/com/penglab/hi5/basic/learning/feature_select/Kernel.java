package com.penglab.hi5.basic.learning.feature_select;

public class Kernel {
    private int weight;
    private int height;
    private int depth;
    private float[] kernel;

    public Kernel(float[] kernel, int weight, int height, int depth){
        this.kernel = kernel;
        this.weight = weight;
        this.height = height;
        this.depth = depth;
    }

    public float[] getKernel() {
        return kernel;
    }

    public int getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }
}
