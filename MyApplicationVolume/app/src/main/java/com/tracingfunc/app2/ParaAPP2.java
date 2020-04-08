package com.tracingfunc.app2;

import com.example.basic.Image4DSimple;
import com.example.basic.LocationSimple;

public class ParaAPP2 {
    Image4DSimple p4dImage;
    int xc0, xc1, yc0, yc1, zc0, zc1; //the six bounding box boundaries
    LocationSimple[] landmarks;
    boolean is_gsdt;
    boolean is_coverage_prune;
    boolean is_break_accept;
    int  bkg_thresh;
    double length_thresh;
    int  cnn_type;
    int  channel;
    double SR_ratio;
    int  b_256cube;
    boolean b_RadiusFrom2D; //how to estimate radius of each reconstruction node, from 2D plane (for anisotropic case) or 3D (for isotropic case)
    int b_resample;
    int b_intensity;
    boolean b_brightfiled;
    boolean s2Mode;

    boolean b_menu;
    String inimg_file, inmarker_file, outswc_file;

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
        b_256cube = 1; //whether or not preprocessing to downsample to a 256xYxZ cube UINT8 for tracing
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
