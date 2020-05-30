package com.learning.opimageline;

public class Angle {
    private float x,y,z;
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

    public float dot(Angle other){
        return (x*other.x + y*other.y + z*other.z);
    }
}
