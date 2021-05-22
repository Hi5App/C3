package com.penglab.hi5.basic.learning.opimageline;

public class Angle {
    public float x,y,z;
    public Angle(){
        x = 0;
        y = 0;
        z = 0;
    }

    public Angle(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Angle(Angle other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    /**
     *
     * @param p1 起点
     * @param p2 终点 ，结果为一条从p1到p2的三维向量
     */
    public Angle(Point p1, Point p2){
        this.x = p2.x -p1.x;
        this.y = p2.y -p1.y;
        this.z = p2.z -p1.z;
    }

    public void setXYZ(Angle other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public void setXYZ(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void normAngle(){
        float s = (float) Math.sqrt(x*x + y*y + z*z);
        if(s>0){
            x /= s;
            y /= s;
            z /= s;
        }
    }

    public double length(){
        return Math.sqrt(x*x + y*y + z*z);
    }

    public float dot(Angle other){
        return (x*other.x + y*other.y + z*other.z);
    }
}
