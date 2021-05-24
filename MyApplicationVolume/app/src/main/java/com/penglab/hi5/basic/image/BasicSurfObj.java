package com.penglab.hi5.basic.image;

public class BasicSurfObj implements Cloneable{
    public long n;				// index
    public RGBA8 color = new RGBA8((char)255,(char)255,(char)255,(char)255);
    public boolean on;
    public boolean selected;
    public String name;
    public String comment;
    public BasicSurfObj() {n=0; color.r=color.g=color.b=color.a=255; on=true;selected=false; name=comment="";}

    @Override
    public BasicSurfObj clone() throws CloneNotSupportedException {
        BasicSurfObj b = null;
        b = (BasicSurfObj) super.clone();
        b.color = this.color.clone();
        return b;
    }
}
