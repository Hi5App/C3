package com.example.basic;

public class BasicSurfObj {
    public long n;				// index
    public RGBA8 color;
    public boolean on;
    public boolean selected;
    public String name;
    public String comment;
    public BasicSurfObj() {n=0; color.r=color.g=color.b=color.a=255; on=true;selected=false; name=comment="";}
}
