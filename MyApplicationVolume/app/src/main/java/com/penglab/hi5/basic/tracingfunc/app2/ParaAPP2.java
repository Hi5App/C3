package com.penglab.hi5.basic.tracingfunc.app2;

import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.LocationSimple;
import com.penglab.hi5.basic.NeuronTree;

public class ParaAPP2 {
    public Image4DSimple p4dImage;
    public int xc0, xc1, yc0, yc1, zc0, zc1; //the six bounding box boundaries
    public LocationSimple[] landmarks;
    public boolean is_gsdt;
    public boolean is_coverage_prune;
    public boolean is_break_accept;
    public int  bkg_thresh;
    public double length_thresh;
    public int  cnn_type;
    public int  channel;
    public double SR_ratio;
    public int  b_128cube;
    public boolean b_RadiusFrom2D; //how to estimate radius of each reconstruction node, from 2D plane (for anisotropic case) or 3D (for isotropic case)
    public int b_resample;
    public int b_intensity;
    public boolean b_brightfiled;
    public boolean s2Mode;

    public boolean b_menu;
    public String inimg_file, inmarker_file, outswc_file;

    public NeuronTree resultNt;

    public ParaAPP2(){
        p4dImage = null;
        xc0 = xc1 = yc0 = yc1 = zc0 = zc1 = 0;
        landmarks = null;

        is_gsdt = false; //true; change to false by PHC, 2012-10-11. as there is clear GSDT artifacts
        is_coverage_prune = true;
        is_break_accept = false;
        bkg_thresh = 10; //30; change to 10 by PHC 2012-10-11
        length_thresh = 5; // 1.0; change to 5 by PHC 2012-10-11
        cnn_type = 2; // default connection type 2
        channel = 0;
        SR_ratio = 3.0/9.0;
        b_128cube = 1; //whether or not preprocessing to downsample to a 256xYxZ cube UINT8 for tracing
        b_RadiusFrom2D = true;
        b_resample = 1;
        b_intensity = 0;
        b_brightfiled = false;
        b_menu = true;

        inimg_file = "";
        inmarker_file = "";
        outswc_file = "";
    }
}
