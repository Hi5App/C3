package com.penglab.hi5.basic.tracingfunc.gd;

public class Vertex implements Comparable<Vertex>{
    private int index;
    private float path;

    public Vertex(int index){
        this.index = index;
        this.path = Float.MAX_VALUE;
    }
    public Vertex(int index, float path){
        this.index = index;
        this.path = path;
    }

    public float getPath() {
        return path;
    }

    public int getIndex() {
        return index;
    }

    public void setPath(float path) {
        this.path = path;
    }

    @Override
    public int compareTo(Vertex o) {
        return o.path>path?-1:1;
    }
}
