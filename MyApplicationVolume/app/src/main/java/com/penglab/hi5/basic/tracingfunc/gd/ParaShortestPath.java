package com.penglab.hi5.basic.tracingfunc.gd;

public class ParaShortestPath {
    public int node_step; //should be >=1 && odd.
    public int outsample_step;
    public int smooth_winsize;
    public int edge_select;  //0 -- only use length 1 edge(optimal for small step), 1 -- plus diagonal edge
    public int background_select; //0 -- no background, 1 -- compute background threshold
    public double imgTH;
    public double visible_thresh;

    public boolean b_use_favorite_direction; //add by PHC 170606
    public double[] favorite_direction = new double[3];

    public int downsample_method; //0 -- use average, 1 -- use max //added by Zhi 20170925

    public ParaShortestPath()
    {
        node_step = 3; //should be >=1
        outsample_step = 2;
        smooth_winsize = 5;
        edge_select = 0;  //0 -- bgl_shortest_path(), 1 -- phc_shortest_path()
        background_select = 1;
        imgTH = 0; //do not tracing image background
        visible_thresh = 30;

        b_use_favorite_direction = false;
        favorite_direction[0] = favorite_direction[1] = favorite_direction[2] = 0;

        downsample_method = 0;
    }
}
