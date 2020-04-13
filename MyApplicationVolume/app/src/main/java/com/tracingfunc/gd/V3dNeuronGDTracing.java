package com.tracingfunc.gd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import com.example.basic.*;

public class V3dNeuronGDTracing {

    public static String TRACED_NAME = "APP1_Tracing";
    public static String TRACED_FILE = "v3d_traced_neuron";

    public static NeuronTree v3dneuron_GD_tracing(int[][][][] p4d, int[] sz, LocationSimple p0, Vector<LocationSimple> pp, CurveTracePara  trace_para, double trace_z_thickness)
            throws Exception
    {
        NeuronTree nt = new NeuronTree();
        if (p4d == null || p4d.length == 0 || sz == null || sz.length == 0 || sz[0]<=0 || sz[1]<=0 || sz[2]<=0 || sz[3]<=0 || trace_para.channo<0 || trace_para.channo>=sz[3])
        {
            System.out.println("Invalid image or sz for v3dneuron_GD_tracing().");
            return nt;
        }
        V_NeuronSWC_list tracedNeuron;
        Vector<Vector<V_NeuronSWC_unit>> mmUnit = new Vector<Vector<V_NeuronSWC_unit>>();

        tracedNeuron = trace_one_pt_to_N_points_shortestdist(p4d, sz, p0, pp, trace_para, trace_z_thickness, mmUnit);

        if (pp.size()>0) //trace to some selected markers
        {
            if (trace_para.b_deformcurve==false && tracedNeuron.nsegs()>=1)	proj_trace_smooth_downsample_last_traced_neuron(p4d, sz, tracedNeuron, trace_para, 0, tracedNeuron.nsegs()-1);
            if (trace_para.b_estRadii==true) proj_trace_compute_radius_of_last_traced_neuron(p4d, sz, tracedNeuron, trace_para, 0, tracedNeuron.nsegs()-1, (float) trace_z_thickness, true);
//            if (trace_para.b_postMergeClosebyBranches && tracedNeuron.nsegs()>=2) proj_trace_mergeAllClosebyNeuronNodes(tracedNeuron);
        }

        nt = convertNeuronTreeFormat(tracedNeuron);
        return nt;
    }

    public static V_NeuronSWC_list trace_one_pt_to_N_points_shortestdist(int[][][][] p4d, int[] sz,
                                                                         LocationSimple  p0, Vector<LocationSimple> pp,
                                                                         CurveTracePara  trace_para, double trace_z_thickness,
                                                                         Vector<Vector<V_NeuronSWC_unit> > mmUnit) throws Exception
    {
        V_NeuronSWC_list tracedNeuron = new V_NeuronSWC_list();
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



    public static NeuronTree convertNeuronTreeFormat(V_NeuronSWC_list  tracedNeuron) throws Exception
    {
        NeuronTree SS = new NeuronTree();

        //first conversion

        V_NeuronSWC seg = V_NeuronSWC_list.merge_V_NeuronSWC_list(tracedNeuron);
        seg.name = tracedNeuron.name;
        seg.file = tracedNeuron.file;

        //second conversion

        ArrayList<NeuronSWC> listNeuron = new ArrayList<NeuronSWC>();
        HashMap<Integer, Integer> hashNeuron = new HashMap<Integer, Integer>();
        listNeuron.clear();
        hashNeuron.clear();

        {
            int count = 0;
            for (int k=0;k<seg.row.size();k++)
            {
                count++;
                NeuronSWC S = new NeuronSWC();

                S.n 	= (int)seg.row.elementAt(k).n;
                if (S.type<=0) S.type 	= 2; //seg.row.at(k).data[1];
                S.x 	= (float) seg.row.elementAt(k).x;
                S.y 	= (float) seg.row.elementAt(k).y;
                S.z 	= (float) seg.row.elementAt(k).z;
                S.radius 	= (float) seg.row.elementAt(k).r;
                S.parent 	= (int) seg.row.elementAt(k).parent;

                //for hit & editing
                S.seg_id       = (int)seg.row.elementAt(k).seg_id;
                S.nodeinseg_id = (int) seg.row.elementAt(k).nodeinseg_id;

                //qDebug("%s  ///  %d %d (%g %g %g) %g %d", buf, S.n, S.type, S.x, S.y, S.z, S.r, S.pn);

                //if (! listNeuron.contains(S)) // 081024
                {
                    listNeuron.add(S.clone());
                    hashNeuron.put((int)S.n, listNeuron.size()-1);
                }
            }
            System.out.printf("---------------------read %d lines, %d remained lines\n", count, listNeuron.size());

            SS.n = -1;
            SS.color = new RGBA8((char)seg.color_uc[0],(char)seg.color_uc[1],(char)seg.color_uc[2],(char)seg.color_uc[3]);
            SS.on = true;
            SS.listNeuron = listNeuron;
            SS.hashNeuron = hashNeuron;

            SS.name = seg.name;
            SS.file = seg.file;
        }

        return SS;
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

                //smooth_curve(mCoord, trace_para.sp_smoothing_win_sz);
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
