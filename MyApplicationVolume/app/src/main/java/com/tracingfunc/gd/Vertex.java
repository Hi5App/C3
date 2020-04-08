package com.tracingfunc.gd;

public class Vertex implements Comparable<Vertex>{
    private int index;
    private int parent;
    private float path;
    private boolean isMarkered;

    public Vertex(int index){
        this.index = index;
        this.parent = -1;
        this.path = Integer.MAX_VALUE;
        this.setMarkered(false);
    }
    public Vertex(int index, float path){
        this.index = index;
        this.path = path;
        this.parent = -1;
        this.setMarkered(false);
    }

    public boolean isMarkered() {
        return isMarkered;
    }

    public float getPath() {
        return path;
    }

    public int getIndex() {
        return index;
    }

    public int getParent() {
        return parent;
    }

    public void setPath(float path) {
        this.path = path;
    }

    public void setMarkered(boolean markered) {
        isMarkered = markered;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(Vertex o) {
        return o.path>path?-1:1;
    }
}
