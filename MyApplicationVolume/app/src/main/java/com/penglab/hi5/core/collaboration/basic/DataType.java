package com.penglab.hi5.core.collaboration.basic;

    /*
    class for data type
     */

public class DataType{
    public boolean isFile = false;   //  false for msg;  true for file
    public boolean isDataStream = false;
    public long    dataSize = 0;
    public String  filename;
    public String  filepath;
    public byte [] binimgdata;
    public long fileSize;
}