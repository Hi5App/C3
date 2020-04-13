package com.tracingfunc.gd;

public class Vertex implements Comparable<Vertex>{
    private int index;
    private float path;
    private float childpath;

    public Vertex(int index){
        this.index = index;
        this.path = Float.MAX_VALUE;
        this.childpath = Float.MAX_VALUE;
    }
    public Vertex(int index, float path){
        this.index = index;
        this.path = path;
        this.childpath = Float.MAX_VALUE;
    }
    public Vertex(int index, float path, float childpath){
        this.index = index;
        this.path = path;
        this.childpath = childpath;
    }


    public float getPath() {
        return path;
    }

    public float getChildpath() {
        return childpath;
    }

    public void setChildpath(float childpath) {
        this.childpath = childpath;
    }

    public int getIndex() {
        return index;
    }

    public void setPath(float path) {
        this.path = path;
    }

    @Override
    public int compareTo(Vertex o) {
//        int i;
//        if(o.path>path){
//            i = -1;
//        }else if(o.path<path){
//            i = 1;
//        }else {
//            if(o.childpath>o.childpath){
//                i = -1;
//            }else {
//                i = 1;
//            }
//        }
//        return i;
        return o.path>path?-1:1;
    }
}
