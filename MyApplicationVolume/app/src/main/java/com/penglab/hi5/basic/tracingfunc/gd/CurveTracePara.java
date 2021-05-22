package com.penglab.hi5.basic.tracingfunc.gd;

public class CurveTracePara {
    public double  false_th;
    public double image_force_weight;
    public double grident_force_weight;
    public double internal_force_weight;
    public double internal_force2_weight;
    public double prior_force_weight;
    public int nloops;
    public int channo; //which data channel the trace will be applied to
    public int n_points; //# of control points
    public boolean b_adaptiveCtrlPoints;
    public boolean b_estRadii;
    public boolean b_postMergeClosebyBranches;
    public boolean b_post_trimming;
    public boolean b_pruneArtifactBranches;

    public boolean b_deformcurve; //use shortest path or deformable model

    public boolean b_3dcurve_width_from_xyonly; //added 2010 Nov 30. Note this has not been added to the tracing parameter dialog yet (only for command line GD tracing)

    //shortest path parameters
    public int sp_num_end_nodes; //(0 for shortest path tree) (1 for shortest path) (>1 for n pair path)
    public int sp_graph_connect;    //(0 for 6-connect) (1 for include diagonal connects)
    public int sp_graph_background; //0 - full image, 1 - use mean value to threshold image and use foreground only
    public int sp_graph_resolution_step;
    public int sp_downsample_step;
    public int sp_smoothing_win_sz;
    public int sp_downsample_method; // 0 for average, 1 for max //Added by Zhi 20170915


    public double imgTH; //anything <=imgTH should NOT be traced! added by PHC, 2010-Dec-21 for the cmd line v3dneuron tracing program
    public double visible_thresh; //2013-02-10

    public boolean b_use_favorite_direction;//whether or not use the favorite direction to trace. // by PHC 170606
    public double[] favorite_direction = new double[3]; //170606. add by PHC to introduce a favorite tracing direction

    public int landmark_id_start, landmark_id_end;

    public boolean b_128cube;

    public CurveTracePara()
    {
        channo=0; n_points=8; landmark_id_start=0; landmark_id_end=0;
        nloops=100; //change from 100 to 1 for JHS data. 090824
        image_force_weight=1;
        grident_force_weight=1;
        internal_force_weight=0.1;
        internal_force2_weight=0.1;
        prior_force_weight=0.2;

        b_adaptiveCtrlPoints=true;
        b_deformcurve=false;

        b_estRadii = true;
        b_postMergeClosebyBranches=true;

        b_post_trimming = false; //20101213. by Hanchuan Peng
        b_pruneArtifactBranches = true; //20120402

        sp_num_end_nodes = 1;
        sp_graph_connect=0;
        sp_graph_background=1;
        sp_graph_resolution_step=2;
        sp_downsample_step=2;
        sp_smoothing_win_sz=7;
        sp_downsample_method=0;

        imgTH = 0;
        visible_thresh = 30;

        b_3dcurve_width_from_xyonly = false;

        b_use_favorite_direction = false; //default set to false
        favorite_direction[0] = favorite_direction[1] = favorite_direction[2] = 0;

        b_128cube = true;
    }
}
