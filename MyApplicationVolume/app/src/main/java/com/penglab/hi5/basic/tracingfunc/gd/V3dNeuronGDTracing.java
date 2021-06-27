package com.penglab.hi5.basic.tracingfunc.gd;

import java.util.Vector;
import com.penglab.hi5.basic.*;
import com.penglab.hi5.basic.image.Image4DSimple;

import static com.penglab.hi5.basic.image.Image4DSimple.ImagePixelType.V3D_UINT8;
import static com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list.convertNeuronTreeFormat;

public class V3dNeuronGDTracing {

    public static String TRACED_NAME = "APP1_Tracing";
    public static String TRACED_FILE = "v3d_traced_neuron";

    public static NeuronTree v3dneuron_GD_tracing(Image4DSimple inimg, LocationSimple p0, Vector<LocationSimple> pp, CurveTracePara  trace_para, double trace_z_thickness)
            throws Exception
    {
        System.out.println("---------------------------------------in gd------------------------------");
        NeuronTree nt = new NeuronTree();

        int[] sz = new int[]{(int) inimg.getSz0(), (int) inimg.getSz1(), (int) inimg.getSz2(), (int) inimg.getSz3()};
        if (!inimg.valid() || trace_para.channo<0 || trace_para.channo>=sz[3])
        {
            System.out.println("Invalid image or sz for v3dneuron_GD_tracing().");
            return nt;
        }

        Image4DSimple p4dImageNew = new Image4DSimple();
        p4dImageNew.setData(inimg);
        double dfactor_xy = 1, dfactor_z = 1;
        Image4DSimple.ImagePixelType datatype = inimg.getDatatype();
        int[] in_sz = {(int) p4dImageNew.getSz0(), (int) p4dImageNew.getSz1(), (int) p4dImageNew.getSz2(), 1};
        if(datatype != V3D_UINT8 || in_sz[0]>128 || in_sz[1]>128 || in_sz[2]>128)// && datatype != V3D_UINT16)
        {
            if (datatype!=V3D_UINT8)
            {
                if (!Image4DSimple.scale_img_and_converto8bit(p4dImageNew, 0, 255))
                    System.out.println("scale error!");

//                indata1d = p4dImageNew->getRawDataAtChannel(0);
                in_sz[0] = (int) p4dImageNew.getSz0();
                in_sz[1] = (int) p4dImageNew.getSz1();
                in_sz[2] = (int) p4dImageNew.getSz2();
                in_sz[3] = (int) p4dImageNew.getSz3();

            }

            System.out.printf("x = %d  ", in_sz[0]);
            System.out.printf("y = %d  ", in_sz[1]);
            System.out.printf("z = %d  ", in_sz[2]);
            System.out.printf("c = %d\n", in_sz[3]);

            if (trace_para.b_128cube)
            {
                if (in_sz[0]<=128 && in_sz[1]<=128 && in_sz[2]<=128)
                {
                    dfactor_z = dfactor_xy = 1;
                }
                else if (in_sz[0] >= 2*in_sz[2] || in_sz[1] >= 2*in_sz[2])
                {
                    if (in_sz[2]<=128)
                    {
                        double MM = in_sz[0];
                        if (MM<in_sz[1]) MM=in_sz[1];
                        dfactor_xy = MM / 128.0;
                        dfactor_z = 1;
                    }
                    else
                    {
                        double MM = in_sz[0];
                        if (MM<in_sz[1]) MM=in_sz[1];
                        if (MM<in_sz[2]) MM=in_sz[2];
                        dfactor_xy = dfactor_z = MM / 128.0;
                    }
                }
                else
                {
                    double MM = in_sz[0];
                    if (MM<in_sz[1]) MM=in_sz[1];
                    if (MM<in_sz[2]) MM=in_sz[2];
                    dfactor_xy = dfactor_z = MM / 128.0;
                }

                System.out.printf("dfactor_xy=%5.3f\n", dfactor_xy);
                System.out.printf("dfactor_z=%5.3f\n", dfactor_z);

                if (dfactor_z>1 || dfactor_xy>1)
                {
                    System.out.println("enter ds code");
                    Image4DSimple p4dImageTmp = new Image4DSimple();
                    if (!Image4DSimple.downsampling_img_xyz(p4dImageTmp,p4dImageNew,dfactor_xy,dfactor_z))
                        System.out.println("downsample error!");
                    p4dImageNew.setData(p4dImageTmp);

                    in_sz[0] = (int) p4dImageNew.getSz0();
                    in_sz[1] = (int) p4dImageNew.getSz1();
                    in_sz[2] = (int) p4dImageNew.getSz2();
                    in_sz[3] = (int) p4dImageNew.getSz3();

                    p0.x = (float) (p0.x/dfactor_xy);
                    p0.y = (float) (p0.y/dfactor_xy);
                    p0.z = (float) (p0.z/dfactor_z);

                    for(int i=0; i<pp.size(); i++){
                        pp.get(i).x = (float) (pp.get(i).x/dfactor_xy);
                        pp.get(i).y = (float) (pp.get(i).y/dfactor_xy);
                        pp.get(i).z = (float) (pp.get(i).z/dfactor_z);
                    }
                }
            }
        }

        int[][][][] p4d = p4dImageNew.getDataCZYX();

        V_NeuronSWC_list tracedNeuron;
        Vector<Vector<V_NeuronSWC_unit>> mmUnit = new Vector<Vector<V_NeuronSWC_unit>>();
//        trace_para.sp_downsample_step = 1;

        tracedNeuron = trace_one_pt_to_N_points_shortestdist(p4d, in_sz, p0, pp, trace_para, trace_z_thickness, mmUnit);

        if (pp.size()>0) //trace to some selected markers
        {
            if (trace_para.b_deformcurve==false && tracedNeuron.nsegs()>=1)	proj_trace_smooth_downsample_last_traced_neuron(p4d, in_sz, tracedNeuron, trace_para, 0, tracedNeuron.nsegs()-1);
            if (trace_para.b_estRadii==true) proj_trace_compute_radius_of_last_traced_neuron(p4d, in_sz, tracedNeuron, trace_para, 0, tracedNeuron.nsegs()-1, (float) trace_z_thickness, true);
//            if (trace_para.b_postMergeClosebyBranches && tracedNeuron.nsegs()>=2) proj_trace_mergeAllClosebyNeuronNodes(tracedNeuron);
        }

        nt = convertNeuronTreeFormat(tracedNeuron);

        for(int i = 0; i < nt.listNeuron.size(); i++) //add scaling 121127, PHC //add cutbox offset 121202, PHC
        {
            if(dfactor_xy>1){
                nt.listNeuron.get(i).x *= dfactor_xy;
                nt.listNeuron.get(i).x += dfactor_xy/2;
                nt.listNeuron.get(i).y *= dfactor_xy;
                nt.listNeuron.get(i).y += dfactor_xy/2;
            }
            if(dfactor_z>1){
                nt.listNeuron.get(i).z *= dfactor_z;
                nt.listNeuron.get(i).z += dfactor_z;
            }

            nt.listNeuron.get(i).radius *= dfactor_xy; //use xy for now
        }


        return nt;
    }

    public static V_NeuronSWC_list trace_one_pt_to_N_points_shortestdist(int[][][][] p4d, int[] sz,
                                                                         LocationSimple  p0, Vector<LocationSimple> pp,
                                                                         CurveTracePara  trace_para, double trace_z_thickness,
                                                                         Vector<Vector<V_NeuronSWC_unit> > mmUnit) throws Exception
    {
        V_NeuronSWC_list tracedNeuron = new V_NeuronSWC_list();
        System.out.println("in trace_one_pt_to_N_points_shortestdist-----------");
        // CHECK_DATA_GD_TRACING(tracedNeuron); //check

        ParaShortestPath sp_para = new ParaShortestPath();
        sp_para.edge_select       = trace_para.sp_graph_connect;
        sp_para.background_select = trace_para.sp_graph_background;
        sp_para.node_step      = trace_para.sp_graph_resolution_step;
        sp_para.outsample_step = trace_para.sp_downsample_step;
        sp_para.smooth_winsize = trace_para.sp_smoothing_win_sz;
        sp_para.imgTH = trace_para.imgTH;
        sp_para.visible_thresh = trace_para.visible_thresh;
        sp_para.downsample_method = trace_para.sp_downsample_method;

        //170606
        sp_para.b_use_favorite_direction = trace_para.b_use_favorite_direction;
        for (int myii=0; myii<3; myii++)
            sp_para.favorite_direction[myii] = trace_para.favorite_direction[myii];



        mmUnit.clear();
        int chano = trace_para.channo;
        if (chano>=sz[3]) chano=(int)sz[3]-1; if (chano<0) chano=0;
        int n_end_nodes = pp.size();
//        Vector<float> px = Vector<float>(), py = Vector<float>(), pz = Vector<float>();
//        px.clear(), py.clear(), pz.clear();
        float[] px = new float[pp.size()];
        float[] py = new float[pp.size()];
        float[] pz = new float[pp.size()];

        for (int i=0;i<pp.size();i++) {
            px[i] = pp.elementAt(i).x;
            py[i] = pp.elementAt(i).y;
            pz[i] = pp.elementAt(i).z;
        }

        BoundingBox trace_bounding_box = new BoundingBox();
        System.out.println("find_shortest_path_graphimg >>> ");

        trace_bounding_box.x0 = trace_bounding_box.y0 = trace_bounding_box.z0 = 0;
        trace_bounding_box.x1 = sz[0]-1;
        trace_bounding_box.y1 = sz[1]-1;
        trace_bounding_box.z1 = sz[2]-1;
//        trace_bounding_box.x0 = trace_bounding_box.x1 = p0.x;
//        trace_bounding_box.y0 = trace_bounding_box.y1 = p0.y;
//        trace_bounding_box.z0 = trace_bounding_box.z1 = p0.z;
//        for(int i=0; i<pp.size();i++){
//            if(trace_bounding_box.x0>pp.get(i).x)
//                trace_bounding_box.x0 = pp.get(i).x;
//            if(trace_bounding_box.x1<pp.get(i).x)
//                trace_bounding_box.x1 = pp.get(i).x;
//            if(trace_bounding_box.y0>pp.get(i).y)
//                trace_bounding_box.y0 = pp.get(i).y;
//            if(trace_bounding_box.y1<pp.get(i).y)
//                trace_bounding_box.y1 = pp.get(i).y;
//            if(trace_bounding_box.z0>pp.get(i).z)
//                trace_bounding_box.z0 = pp.get(i).z;
//            if(trace_bounding_box.z1<pp.get(i).z)
//                trace_bounding_box.z1 = pp.get(i).z;
//        }
//        trace_bounding_box.x0 = trace_bounding_box.x0-20>=0?trace_bounding_box.x0-20:0;
//        trace_bounding_box.y0 = trace_bounding_box.y0-20>=0?trace_bounding_box.y0-20:0;
//        trace_bounding_box.z0 = trace_bounding_box.z0-20>=0?trace_bounding_box.z0-20:0;
//        trace_bounding_box.x1 = trace_bounding_box.x1+20<=sz[0]-1?trace_bounding_box.x1+20:sz[0]-1;
//        trace_bounding_box.y1 = trace_bounding_box.y1+20<=sz[1]-1?trace_bounding_box.y1+20:sz[1]-1;
//        trace_bounding_box.z1 = trace_bounding_box.z1+20<=sz[2]-1?trace_bounding_box.z1+20:sz[2]-1;

        System.out.println("set z1 "+ (int)(trace_bounding_box.z1));

        System.out.println("z1="+(int)(trace_bounding_box.z1));

//        float pxp = 0, pyp=0, pzp=0;
//        if (n_end_nodes>0)
//        {
//            pxp = px[0];
//            pyp = py[0];
//            pzp = pz[0];
//        }

        String s_error = null;
        GD gd = new GD();
//        s_error = gd.find_shortest_path_graghing_FM(p4d[chano], sz[0], sz[1], sz[2],
//                (float) trace_z_thickness,
//                (int)trace_bounding_box.x0, (int)trace_bounding_box.y0, (int) trace_bounding_box.z0,
//                (int)trace_bounding_box.x1, (int)trace_bounding_box.y1, (int) trace_bounding_box.z1,
//                p0.x, p0.y, p0.z,
//                n_end_nodes,
//                px, py, pz, //fix this bug 100624
//                mmUnit,
//                sp_para);
        if (sz[3]==1)
        {

            s_error = gd.find_shortest_path_graphimg(p4d[chano], sz[0], sz[1], sz[2],
                    (float) trace_z_thickness,
                    (int)trace_bounding_box.x0, (int)trace_bounding_box.y0, (int) trace_bounding_box.z0,
                    (int)trace_bounding_box.x1, (int)trace_bounding_box.y1, (int) trace_bounding_box.z1,
                        p0.x, p0.y, p0.z,
                        n_end_nodes,
                        px, py, pz, //fix this bug 100624
                        mmUnit,
                        sp_para);


        }
        else //note that this has NOT been updated yet to include the favorite direction , by PHC 20170606
        {
            System.out.println("please assure the image is one channel image");
        }
        System.out.println("find_shortest_path_graphimg <<< ");
        if (s_error!=null && s_error != "")
        {
            // System.out.println(s_error,0);
            System.out.println(s_error);
            throw new Exception(s_error);
            //  throw ( char*)s_error;
//            return tracedNeuron;
        }

        int nSegsTrace = mergeback_mmunits_to_neuron_path(n_end_nodes, mmUnit, tracedNeuron);

        //return traced res
        return tracedNeuron;
    }


    public static int mergeback_mmunits_to_neuron_path(int n_end_nodes, Vector< Vector<V_NeuronSWC_unit> >  mmUnit, V_NeuronSWC_list  tNeuron) throws Exception
    {
        if (mmUnit.size()<=0)
            return mmUnit.size();

        //merge traced path /////////////////////////////////////////////////////////
        if (n_end_nodes >=2)
        {
            GD.merge_back_traced_paths(mmUnit); // start --> n end
        }

        //put into tNeuron /////////////////////////////////////////////////////////
        if (n_end_nodes<=0) // entire image, direct copy
        {
            int nexist = tNeuron.maxnoden();

            V_NeuronSWC cur_seg = new V_NeuronSWC();
            cur_seg.clear();
            Vector<V_NeuronSWC_unit> mUnit = mmUnit.elementAt(0);

            for (int i=0;i<mUnit.size();i++)
            {
                if (mUnit.elementAt(i).nchild<0) continue;

                V_NeuronSWC_unit node = mUnit.elementAt(i).clone();
                //node.r = 0.5;
                node.n += nexist;
                if (node.parent >=1)  node.parent += nexist;
                else node.parent = -1;
                cur_seg.append(node.clone());
            }

            String tmpss = String.valueOf(tNeuron.nsegs()+1);
            cur_seg.name = tmpss;
            cur_seg.b_linegraph=false; //don't forget to do this
            tNeuron.append(cur_seg.clone());
            tNeuron.name = TRACED_NAME;
            tNeuron.file = TRACED_FILE;
        }
        else
        {
            for (int ii=0;ii<mmUnit.size();ii++)
            {
                int nexist = tNeuron.maxnoden();

                V_NeuronSWC cur_seg = new V_NeuronSWC();
                cur_seg.clear();
                Vector<V_NeuronSWC_unit> mUnit = mmUnit.elementAt(ii);

                for (int i=0;i<mUnit.size();i++)
                {
                    if (mUnit.elementAt(i).nchild<0) continue;

                    V_NeuronSWC_unit v = new V_NeuronSWC_unit();
                    set_simple_path_unit(v, nexist, mUnit, i, (n_end_nodes==1)); // link_order determined by 1/N path

                    cur_seg.append(v);
                    //qDebug("%d ", cur_seg.nnodes());
                }

                String tmpss = String.valueOf(tNeuron.nsegs()+1);
                cur_seg.name = tmpss;
                cur_seg.b_linegraph=true; //don't forget to do this
                tNeuron.append(cur_seg.clone());
                tNeuron.name = TRACED_NAME;
                tNeuron.file = TRACED_FILE;
            }
        }

        return mmUnit.size();
    }

    public static void set_simple_path_unit (V_NeuronSWC_unit  v, int base_n, Vector<V_NeuronSWC_unit> mUnit, int i, boolean link_order)
    {
        double r=1; double default_type=3; double creatmode=0; double default_timestamp=10; double default_tfresindex = 0;
        int N = mUnit.size();
        v.type	= default_type;
        v.x 	= mUnit.elementAt(i).x;
        v.y 	= mUnit.elementAt(i).y;
        v.z 	= mUnit.elementAt(i).z;
        v.r 	= r;
        v.creatmode = creatmode;
        v.timestamp = default_timestamp; // LMG 11/10/2018
        v.tfresindex = default_tfresindex; // LMG 13/12/2018
        if (link_order) // same as index order
        {
            v.n		= base_n +1+i;
            v.parent = (i>=N-1)? -1 : (v.n +1);
        }
        else // reversed link order
        {
            v.n		= base_n +1+i;
            v.parent = (i<=0)? -1 : (v.n -1);
        }
    }


    public static boolean proj_trace_smooth_downsample_last_traced_neuron(int[][][][] p4d, int[] sz, V_NeuronSWC_list  tracedNeuron,
                                                                          CurveTracePara  trace_para, int seg_begin, int seg_end)throws Exception
    {
        System.out.println("proj_trace_smooth_downsample_last_traced_neuron(). ");
        // CHECK_DATA_GD_TRACING(false);  //check

        int nexist = 0; // re-create index number

        // (VneuronSWC_list tracedNeuron).(V_neuronSWC seg[]).(V_nueronSWC_unit row[])
        for(int iseg=0; iseg<tracedNeuron.seg.size(); iseg++)
        {
            if (iseg <seg_begin || iseg >seg_end) continue; //091023

            V_NeuronSWC  cur_seg = (tracedNeuron.seg.elementAt(iseg));
            System.out.printf("#seg=%d(%d)\n", iseg, cur_seg.row.size());

            Vector<V_NeuronSWC_unit>  mUnit = cur_seg.row; // do in place
            {
                //------------------------------------------------------------
                // Vector<V_NeuronSWC_unit> mUnit_prior = mUnit; // a copy as prior
                Vector<V_NeuronSWC_unit> mUnit_prior = new Vector<V_NeuronSWC_unit>();
                for(int s=0;s<mUnit.size();s++){
                    mUnit_prior.add(mUnit.elementAt(s).clone());
                }

                GD.smoothCurve(mUnit, trace_para.sp_smoothing_win_sz);
                mUnit = GD.downsample_curve(mUnit, trace_para.sp_downsample_step);
                //------------------------------------------------------------
                BDB_Minus_Prior_Parameter bdbp_para = new BDB_Minus_Prior_Parameter();
                bdbp_para.nloops   =trace_para.nloops;
                bdbp_para.f_smooth =trace_para.internal_force2_weight;
                bdbp_para.f_length =trace_para.internal_force_weight;
                bdbp_para.f_prior  = 0.2;
                int chano = trace_para.channo;

                GD.point_bdb_minus_3d_localwinmass_prior(p4d[chano], sz[0], sz[1], sz[2],
                        mUnit, bdbp_para, true,
                        mUnit_prior,1,1.0f,false);// 090618: add constraint to fix 2 ends
                //-------------------------------------------------------------

            }
            System.out.printf(">>%d(%d) \n", iseg, mUnit.size());

            V_NeuronSWC_unit.reset_simple_path_index (nexist, mUnit);
            nexist += mUnit.size();
        }
        System.out.println("");

        return true;
    }

    public static boolean proj_trace_compute_radius_of_last_traced_neuron(int[][][][] p4d, int[] sz, V_NeuronSWC_list  tracedNeuron,
                                                                          CurveTracePara  trace_para, int seg_begin, int seg_end,
                                                                          float myzthickness, boolean b_smooth)throws Exception
    {
        System.out.println("proj_trace_compute_radius_of_last_traced_neuron(). ");
        // CHECK_DATA_GD_TRACING(false); //check

        //int chano = trace_para.channo; //20110917. the multichannel and single channel tracing can be distinguished based sz[3].
        int smoothing_win_sz = trace_para.sp_smoothing_win_sz;

        for(int iseg=0; iseg<tracedNeuron.seg.size(); iseg++)
        {
            if (iseg <seg_begin || iseg >seg_end) continue;

            V_NeuronSWC cur_seg = tracedNeuron.seg.elementAt(iseg);
            System.out.printf("#seg=%d(%d) ", iseg, cur_seg.row.size());

            Vector<V_NeuronSWC_unit> mUnit = cur_seg.row; // do in place
            {
                if (sz[3]==1)
                {
                    GD.fit_radius_and_position(p4d[0], sz[0], sz[1], sz[2],
                            mUnit,
                            false,       // do not move points here
                            myzthickness,
                            trace_para.b_3dcurve_width_from_xyonly,trace_para.visible_thresh);
                }

                if (b_smooth)
                    GD.smooth_radius(mUnit, smoothing_win_sz, false);
            }
        }
        System.out.println("");

        return true;
    }

}
