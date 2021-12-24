package com.penglab.hi5.data.model.img;

public class NeuronInfo {
    private String somaId;
    private String imageId;
    private String neuronId;
    private int x;
    private int y;
    private int z;

    public NeuronInfo(String somaId, String imageId, String neuronId, int x, int y, int z) {
        this.somaId = somaId;
        this.imageId = imageId;
        this.neuronId = neuronId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getSomaId() {
        return somaId;
    }

    public void setSomaId(String somaId) {
        this.somaId = somaId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getNeuronId() {
        return neuronId;
    }

    public void setNeuronId(String neuronId) {
        this.neuronId = neuronId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
