package com.penglab.hi5.basic.tracingfunc.TRe;
import com.penglab.hi5.basic.image.Image4DSimple;

public class APP2_LS_PARA
{
    public boolean is_gsdt;
    public boolean is_break_accept;
    public int bkg_thresh;
    public double length_thresh;
    public int cnn_type;
    public int channel;
    public double SR_ratio;
    public int b_256cube;
    public boolean b_RadiusFrom2D;
    public int[] root_1st = new int[3];
    public int mip_plane; //0 for XY, 1 for XZ, 2 for YZ

    public Image4DSimple[] image;

    public String inimg_file ;
    public String mip_image_file;

    public APP2_LS_PARA(){

        is_gsdt = false; //true; change to false by PHC, 2012-10-11. as there is clear GSDT artifacts
        is_break_accept = false;
        bkg_thresh = 10; //30; change to 10 by PHC 2012-10-11
        length_thresh = 5; // 1.0; change to 5 by PHC 2012-10-11
        cnn_type = 2; // default connection type 2
        channel = 0;
        SR_ratio = 3.0/9.0;
        b_RadiusFrom2D = true;

        inimg_file = "";
        mip_image_file = "";
    }
}

