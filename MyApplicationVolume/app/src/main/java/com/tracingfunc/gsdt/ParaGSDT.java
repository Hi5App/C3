package com.tracingfunc.gsdt;

import com.example.basic.Image4DSimple;
import com.example.basic.ImageMarker;

import java.util.ArrayList;

public class ParaGSDT {
    public Image4DSimple p4DImage;
    public Image4DSimple outImage;
    public int bkg_thresh;
    public int cnn_type;
    public int channel;
    public int z_thickness;
    public ArrayList<ImageMarker> markers;
    public int[] max_loc;
    public double max_val;
    public float[][][] phi;

    public ParaGSDT(){
         Image4DSimple p4DImage = null;
         Image4DSimple outImage = null;
         bkg_thresh = 10;
         cnn_type = 2;
         channel = 0;
         z_thickness = 5;
         markers = new ArrayList<>();
         max_loc = new int[]{0,0,0};
         max_val = 0;
         phi = null;
    }
}
