package com.penglab.hi5.basic.tracingfunc.gd;

import android.util.Log;

import com.penglab.hi5.basic.tracingfunc.app2.MyMarker;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static com.penglab.hi5.basic.tracingfunc.app2.FM.fastmarching_dt;
import static com.penglab.hi5.basic.tracingfunc.app2.FM.fastmarching_tree;
import static com.penglab.hi5.basic.tracingfunc.app2.HierarchyPruning.getLeafMarkers;
import static java.lang.Math.sqrt;

//import javafx.util.Pair;

//import com.sun.tools.javac.util..Pair;

public class GD {
    public static int ITER_POSITION = 10;
    public Edge_table_item[] edge_table;
    public GD(){
        edge_table = new Edge_table_item[13];
        double[][] tmp = {
                {0,0,0, 1,0,0, 1},         // 3 dist=1
                {0,0,0, 0,1,0, 1},
                {0,0,0, 0,0,1, 1},
                {0,0,0, 1,1,0, sqrt(2.0)}, // 6 dist=sqrt(2)
                {0,0,0, 0,1,1, sqrt(2.0)},
                {0,0,0, 1,0,1, sqrt(2.0)},
                {1,0,0, 0,1,0, sqrt(2.0)},
                {0,1,0, 0,0,1, sqrt(2.0)},
                {0,0,1, 1,0,0, sqrt(2.0)},
                {0,0,0, 1,1,1, sqrt(3.0)}, // 4 dist=sqrt(3)
                {1,0,0, 0,1,1, sqrt(3.0)},
                {0,1,0, 1,0,1, sqrt(3.0)},
                {0,0,1, 1,1,0, sqrt(3.0)},
        };
        for(int i=0; i<edge_table.length; i++){
            edge_table[i] = new Edge_table_item();
            edge_table[i].i0 = (int) tmp[i][0];
            edge_table[i].j0 = (int) tmp[i][1];
            edge_table[i].k0 = (int) tmp[i][2];
            edge_table[i].i1 = (int) tmp[i][3];
            edge_table[i].j1 = (int) tmp[i][4];
            edge_table[i].k1 = (int) tmp[i][5];
            edge_table[i].dist = tmp[i][6];
        }
    }


    // return error message, 0 is no error
    //
    String find_shortest_path_graphimg(int[][][] img3d, int dim0, int dim1, int dim2, //image
                                       float zthickness, // z-thickness for weighted edge
                                       //final int box[6],  //bounding box
                                       int bx0, int by0, int bz0, int bx1, int by1, int bz1, //bounding box (ROI)
                                       float x0, float y0, float z0,       // start node
                                       int n_end_nodes,                    // n_end_nodes == (0 for shortest path tree) (1 for shortest path) (n-1 for n pair path)
                                       float[] x1, float[] y1, float[] z1,    // all end nodes
                                       Vector< Vector<V_NeuronSWC_unit> > mmUnit, // change from Coord3D for shortest path tree
                                       ParaShortestPath  para) throws Exception
    {
        //System.out.println("start of find_shortest_path_graphimg ");
        boolean b_error = false;
        String s_error = "";
        final double dd = 0.5;

        // System.out.println("sizeof(Weight) = %d, sizeof(Node) = %d ", sizeof(Weight), sizeof(Node));
        System.out.printf("bounding (%d %d %d)--(%d %d %d) in image (%d x %d x %d)\n", bx0,by0,bz0, bx1,by1,bz1, dim0,dim1,dim2);
        if (img3d == null || img3d.length == 0 || dim0<=0 || dim1<=0 || dim2<=0)
        {
            System.out.println(s_error="Error happens: no image data!");
            return s_error;
        }
        if ((bx0<0-dd || bx0>=dim0-dd || by0<0-dd || by0>=dim1-dd || bz0<0-dd || bz0>=dim2-dd)
                || (bx1<0-dd || bx1>=dim0-dd || by1<0-dd || by1>=dim1-dd || bz1<0-dd || bz1>=dim2-dd))
        {
            System.out.println(s_error="Error happens: bounding box out of image bound!");
            System.out.printf("inside z1=%d\n", bz1);;
            return s_error;
        }

        //now set parameters
        int min_step       = para.node_step; //should be >=1
        int smooth_winsize = para.smooth_winsize;
        int edge_select    = para.edge_select;  //0 -- only use length 1 edge(optimal for small step), 1 -- plus diagonal edge
        double imgTH = para.imgTH; //anything <= imgTH will NOT be traced!
        int background_select = para.background_select;

        int dowsample_method = para.downsample_method; //0 for average, 1 for max

        if (min_step<1)       min_step =1;
        if (smooth_winsize<1) smooth_winsize =1;

        //bounding box volume
        int xmin = bx0, xmax = bx1, ymin = by0, ymax = by1, zmin = bz0, zmax = bz1;

        int nx=((xmax-xmin)/min_step)+1, 	xstep=min_step,
                ny=((ymax-ymin)/min_step)+1, 	ystep=min_step,
                nz=((zmax-zmin)/min_step)+1, 	zstep=min_step;

//        edge_select = 1;

        int num_edge_table = (edge_select==0)? 3:13; // exclude/include diagonal-edge

        System.out.printf("valid bounding (%d %d %d)--(%d %d %d) ......  \n", xmin,ymin,zmin, xmax,ymax,zmax);
        System.out.printf("%d x %d x %d nodes, step = %d, connect = %d \n", nx, ny, nz, min_step, num_edge_table*2);

        int num_nodes = nx*ny*nz;
        int i,j,k,n,m;

        // ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // #define NODE_FROM_XYZ(x,y,z) 	(int((z+.5)-zmin)/zstep*ny*nx + int((y+.5)-ymin)/ystep*nx + int((x+.5)-xmin)/xstep)
        // #define NODE_TO_XYZ(j, x,y,z) \
        // { \
        //     z = (j)/(nx*ny); 		y = ((j)-int(z)*nx*ny)/nx; 	x = ((j)-int(z)*nx*ny-int(y)*nx); \
        //     x = xmin+(x)*xstep; 	y = ymin+(y)*ystep; 			z = zmin+(z)*zstep; \
        // }
        // #define NODE_FROM_IJK(i,j,k) 	((k)*ny*nx+(j)*nx+(i))
        // #define X_I(i)				 	(xmin+(i)*xstep)
        // #define Y_I(i)				 	(ymin+(i)*ystep)
        // #define Z_I(i)				 	(zmin+(i)*zstep)
        // ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // //out of bound handler
        // #define NODE_XYZ_OUT_OF_BOUND(x0,y0,z0)		(x0<xmin-dd || x0>xmax+dd || y0<ymin-dd || y0>ymax+dd || z0<zmin-dd || z0>zmax+dd)
        // #define NODE_INDEX_OUT_OF_BOUND(ind) 		(ind<0 || ind>=num_nodes)

        int start_nodeind;
        int[] end_nodeind = new int[n_end_nodes];
//        if (n_end_nodes>0) //101210 PHC
//            end_nodeind = new int [n_end_nodes]; //100520, PHC
//        else
//            System.out.println("**************** n_end_nodes is 0, and thus do not need to allocate memory. *********************");

//        System.out.println("start_nodeind: "+start_nodeind);
        System.out.println("x0,y0,z0:"+x0 + ", " + y0 + ", "+ z0);
//        System.out.println("num_nodes: "+num_nodes);
//        System.out.println("xm,ym,zm: "+xmin+ymin+zmin);
//        System.out.println("nx,ny,nz: "+nx+ny+nz);



        if (x0<xmin-dd || x0>xmax+dd || y0<ymin-dd || y0>ymax+dd || z0<zmin-dd || z0>zmax+dd)
        {
            System.out.println(s_error="Error happens: start_node out of bound! ");
            return s_error;
        }
        // start_nodeind = NODE_FROM_XYZ(x0,y0,z0);
        start_nodeind = (int)((z0+0.5)-zmin)/zstep*ny*nx + (int)((y0+0.5)-ymin)/ystep*nx + (int)((x0+0.5)-xmin)/xstep;


        if (start_nodeind<0 || start_nodeind>=num_nodes)
        {
            System.out.println(s_error="Error happens: start_node index out of range! ");
            // if (end_nodeind) {delete []end_nodeind; end_nodeind=0;} //100520, by PHC
            return s_error;
        }

        int n_end_outbound = 0;
        for (i=0; i<n_end_nodes; i++)
        {
            if (x1[i]<xmin-dd || x1[i]>xmax+dd || y1[i]<ymin-dd || y1[i]>ymax+dd || z1[i]<zmin-dd || z1[i]>zmax+dd)
            {
                end_nodeind[i] = -1;
                System.out.printf("Warning: end_node[%d] out of bound! \n", i);
                n_end_outbound ++;
                continue; //ignore this end_node out of ROI
            }
            end_nodeind[i]   = (int)((z1[i]+0.5)-zmin)/zstep*ny*nx + (int)((y1[i]+0.5)-ymin)/ystep*nx + (int)((x1[i]+0.5)-xmin)/xstep;

            Log.v("end_nodeind", Integer.toString(end_nodeind[i]));

            if (end_nodeind[i]<0 || end_nodeind[i]>=num_nodes)
            {
                end_nodeind[i] = -1;
                System.out.printf("Warning: end_node[%d] index out of range! \n", i);
                n_end_outbound ++;
                continue; //ignore this end_node out of ROI
            }
        }

        if (n_end_nodes>0 //for 1-to-N, not 1-to-image
                && n_end_outbound>=n_end_nodes)
        {
            System.out.println(s_error="Error happens: all end_nodes out of bound! At least one end_node must be in bound.");
            // if (end_nodeind) {delete []end_nodeind; end_nodeind=0;} //100520, by PHC
            return s_error;
        }


        double imgMax = getImageMaxValue(img3d, dim0, dim1, dim2);
        double imgAve = getImageAveValue(img3d, dim0, dim1, dim2);
        double imgStd = getImageStdValue(img3d, dim0, dim1, dim2);
        System.out.println("background: "+background_select);
        System.out.println("imgave: "+imgAve);
        System.out.println("imgstd: "+imgStd);
//        double imgTH = 0;
        if (background_select==1) imgTH = (imgAve < imgStd)? imgAve : (imgAve+imgStd)*0.5;
        System.out.println("imgth: "+imgTH);
        // #define _creating_graph_

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        //switch back to new[] from std::vector for *** glibc detected *** ??? on Linux
        // std::vector<Node> 	plist(num_nodes);		for (i=0;i<num_nodes;i++) plist[i]=i;
        // std::vector<Edge> 	edge_array;				edge_array.clear();
        // std::vector<Weight>	weights;				weights.clear();

        int[] plist = new int[(int)num_nodes];                                                  for (i=0;i<num_nodes;i++) plist[i]=i;
//        Vector<Integer> plist = new Vector<Integer>();
        Vector<Pair<Integer,Integer>> edge_array = new Vector<Pair<Integer, Integer>>();        edge_array.clear();
        Vector<Float> weights = new Vector<Float>();                                            weights.clear();
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        // #define _setting_weight_of_edges_
        System.out.println("setting weight of edges ......  ");

        // z-thickness weighted edge
        for (int it=0; it<num_edge_table; it++)
        {
            double di = (edge_table[it].i0 - edge_table[it].i1);
            double dj = (edge_table[it].j0 - edge_table[it].j1);
            double dk = (edge_table[it].k0 - edge_table[it].k1) * zthickness;
            edge_table[it].dist = sqrt(di*di + dj*dj + dk*dk);
        }

        double va = 0, vb = 0;
        double maxw=0, minw=1e+6; //for debug info
        n=0; m=0;
        try{
            for (k=0;k<nz;k++)
            {
                for (j=0;j<ny;j++)
                {
                    for (i=0;i<nx;i++)
                    {
                        for (int it=0; it<num_edge_table; it++)
                        {
                            // take an edge
                            int ii = i+ edge_table[it].i0;
                            int jj = j+ edge_table[it].j0;
                            int kk = k+ edge_table[it].k0;
                            int ii1 = i+ edge_table[it].i1;
                            int jj1 = j+ edge_table[it].j1;
                            int kk1 = k+ edge_table[it].k1;

                            if (ii>=nx || jj>=ny || kk>=nz || ii1>=nx || jj1>=ny || kk1>=nz) continue;//for boundary condition

                            Integer node_a = (int) ((kk) * ny * nx + (jj) * nx + (ii));//index
                            Integer node_b = (int) ((kk1) * ny * nx + (jj1) * nx + (ii1));

                            m++;

                            //=========================================================================================
                            // edge link
                            if(dowsample_method == 0)
                            {
                                va = getBlockAveValue(img3d, dim0, dim1, dim2, (xmin+(ii)*xstep),(ymin+(jj)*ystep),(zmin+(kk)*zstep),
                                        xstep, ystep, (int) ((double)zstep/zthickness)); //zthickness
                                vb = getBlockAveValue(img3d, dim0, dim1, dim2, (xmin+(ii1)*xstep),(ymin+(jj1)*ystep),(zmin+(kk1)*zstep),
                                        xstep, ystep, (int) ((double)zstep/zthickness)); //zthickness
                            }else if(dowsample_method == 1)
                            {
                                va = getBlockMaxValue(img3d, dim0, dim1, dim2, (xmin+(ii)*xstep),(ymin+(jj)*ystep),(zmin+(kk)*zstep),
                                        xstep, ystep, (int) ((double)zstep/zthickness)); //zthickness
                                vb = getBlockMaxValue(img3d, dim0, dim1, dim2, (xmin+(ii1)*xstep),(ymin+(jj1)*ystep),(zmin+(kk1)*zstep),
                                        xstep, ystep, (int) ((double)zstep/zthickness)); //zthickness
                            }

                            if (va<=imgTH || vb<=imgTH)
                                continue; //skip background node link

                            Pair<Integer,Integer> e = new Pair<Integer,Integer>(node_a, node_b);
                            edge_array.add(e);

                            Float w =(float) edge_weight_func(it, va,vb, imgMax);

//                            if(va>30||vb>30)
//                            {
//                                System.out.println("it: "+it+" i0: "+edge_table[it].i0+" j0: "+edge_table[it].j0+
//                                        " k0: "+edge_table[it].k0+" i1: "+edge_table[it].i1+" j1: "+edge_table[it].j1+
//                                        " k1: "+edge_table[it].k1+" dist: "+edge_table[it].dist);
//                                System.out.println("ii: "+ii+" jj: "+jj+" kk: "+kk+" ii1: "+ii1+" jj1: "+jj1+" kk1: "+kk1);
//                                System.out.println("node_a: "+node_a+" node_b: "+node_b+" va: "+va+" vb: "+vb+" w: "+w);
//                            }

                            //now try to use favorite direction if literally specified. by PHC 20170606
                            if (para.b_use_favorite_direction)
                            {
                                //do nothing
//                            double x_mid = (double)((xmin+(ii)*xstep) + xmin+(ii1)*xstep)/2.0;
//                            double y_mid = (double)((ymin+(jj)*ystep) + ymin+(jj1)*ystep)/2.0;
//                            double z_mid = (double)((zmin+(kk)*zstep) + zmin+(kk1)*zstep)/2.0;
//
//                            double[] d_startpt_2_mid = new double[3]; //the direction vector from the starting pt to the mid-pt of the two current ends of an edge
//                            d_startpt_2_mid[0] = x_mid - x0;
//                            d_startpt_2_mid[1] = y_mid - y0;
//                            d_startpt_2_mid[2] = z_mid - z0;
//
//                            double cangle = cosangle_two_vectors(para.favorite_direction, d_startpt_2_mid);
//
//                            if (cangle != -2) //-2 is a special return value indicating the status of cosangle_two_vectors()
//                            {
//                                double w_direction = 1 - (cangle+1.0)/2.0; //add 1.0 to force 0 degree angle to be max, 180 degree angle to be minimal; divide 2.0 to make sure w will not be too big
//                                if (w_direction>0.35) w += 100;
//                                //System.out.println("wd=%5.3f x_mid=%5.3f y_mid=%5.3f z_mid=%5.3f x0=%5.3f y0=%5.3f z0=%5.3f vpq=%5.3f vpp=%5.3f vqq=%5.3f ", w_direction, x_mid, y_mid, z_mid, x0, y0, z0, vpq, vpp, vqq);
//                            }
//                            else
//                            {
//                                //do nothing here.
//                            }
                                //also in the furture we may not need to divide Math.sqrt(vqq) because it is finalant, and this should also avoid the extra-exception when vqq=0
                            }

                            weights.add( w );
                            //=========================================================================================

                            n++; // that is the correct position of n++

                            if (w>maxw) maxw=w;	if (w<minw) minw=w;
                        }
                    }
                }
            }
        }catch (OutOfMemoryError e){
            System.out.println("-----------------start to print error info---------------");
            System.out.println(e.getMessage());
//            System.exit(1);
            return (s_error = e.getMessage());
        }

        System.out.printf(" minw=%g maxw=%g \n", minw,maxw);
        System.out.println(" graph defined! ");

        if (n != edge_array.size())
        {
            System.out.println(s_error="The number of edges is not consistent ");
            // if (end_nodeind) {delete []end_nodeind; end_nodeind=0;} //100520, by PHC
            return s_error;
        }
        int num_edges = n; // back to undirectEdge for less memory consumption

        //System.out.println("image average =%g, std =%g, max =%g.  select %ld out of %ld links ", imgAve, imgStd, imgMax, n, m);
        System.out.printf("select %d out of %d links \n", n, m);
        System.out.printf("total %d nodes, total %d edges \n", num_nodes, num_edges);
        System.out.printf("start from #%d to \n", start_nodeind);
        for(i=0; i<n_end_nodes; i++) System.out.printf("#%d ", end_nodeind[i]); System.out.println("");
        System.out.println("---------------------------------------------------------------");



        // #define _do_shortest_path_algorithm_
        //========================================================================================================

        int code_select = 2; // BGL has the best speed and correctness
        switch(code_select)
        {
            case 0:
//                System.out.println("bgl_shortest_path() ");
                // s_error = bgl_shortest_path(edge_array[0], num_edges, &weights[0], num_nodes, start_nodeind, &plist[0]);
                System.out.println("j_shortest_path ");
//                s_error = Jgragh.j_shortest_path(edge_array, (int) num_edges, weights, (int) num_nodes,	(int) start_nodeind, (int) end_nodeind[0], plist);
                break;
            case 1:
                System.out.println("phc_shortest_path() ");
                s_error = phc_shortest_path(edge_array, (int) num_edges, weights, (int) num_nodes,	(int) start_nodeind, plist);
                break;
            case 2:
//                System.out.println("mst_shortest_path() ");
                // s_error = mst_shortest_path(&edge_array[0], num_edges, &weights[0], num_nodes,	start_nodeind, &plist[0]);
                System.out.println("my_shortest_path()");
                s_error = my_shortest_path(edge_array, (int) num_edges, weights, (int) num_nodes,	(int) start_nodeind, plist);
                break;
        }
        if (s_error != "" && s_error != null)
        {
            // if (end_nodeind) {delete []end_nodeind; end_nodeind=0;} //100520, by PHC
            return s_error;
        }
        //=========================================================================================================
        //for (i=0;i<num_nodes;i++)	std::cout<<"p("<<i<<")="<<plist[i]<<";   ";  std::cout<<std::endl;



//        for(i = 0; i<plist.size(); i++){
//            System.out.println(plist.get(i));
//        }


        // output node coordinates of the shortest path
        mmUnit.clear();
        int nexist = 0;

        V_NeuronSWC_unit cc = new V_NeuronSWC_unit();
        Vector<V_NeuronSWC_unit> mUnit = new Vector<V_NeuronSWC_unit>();


        if (n_end_nodes==0) // trace from start-.each possible node
        {
            // #define _output_shortest_path_TREE_
            System.out.println("trace from start-.each possible node ");
            mUnit.clear();

            Map<Double,Integer> index_map = new HashMap<Double, Integer>();	index_map.clear();
            // set nchild=0
            for (j=0; j<num_nodes; j++)
            {
                if (j==start_nodeind)
                {
                    cc.x = x0;
                    cc.y = y0;
                    cc.z = z0;
                    cc.n = 1+j;
                    cc.parent = -1;
                    cc.nchild = 0; // although root isn't leaf, but the different should be told by checking their parent nodes instead of number of children. changed from 1 to 0, by PHC, 20110908. I think root can also be a leaf,
                    mUnit.add(cc.clone());
                    // index_map[cc.n] = mUnit.size()-1; //fix the missing line bug by PHC, 2010-12-30
                    index_map.put(cc.n,mUnit.size()-1);
                    System.out.printf("[start: x y z] %d: %g %g %g \n", j, cc.x, cc.y, cc.z);
                }
                else if ( (k=plist[j]) != j ) // has parent
                    if (k>=0 && k<num_nodes)  // is valid
                    {
                        // NODE_TO_XYZ(j, cc.x, cc.y, cc.z);
                        cc.z = (j)/(nx*ny); 		cc.y = ((j)-(int)(cc.z)*nx*ny)/nx; 	cc.x = ((j)-(int)(cc.z)*nx*ny-(int)(cc.y)*nx);
                        cc.x = xmin+(cc.x)*xstep; 	cc.y = ymin+(cc.y)*ystep; 			cc.z = zmin+(cc.z)*zstep;

                        cc.n = 1+j;
                        cc.parent = 1+k; //k=plist[j]
                        cc.nchild = 0;
                        mUnit.add(cc.clone());
                        //System.out.println("[node: x y z] %ld: %g %g %g ", j, cc.x, cc.y, cc.z);

                        // index_map[cc.n] = mUnit.size()-1;
                        index_map.put(cc.n,mUnit.size()-1);
                    }
            }

            System.out.println("counting parent.nchild ");
            // count parent.nchild
            for (j=0; j<mUnit.size(); j++)
            {
                double parent = mUnit.elementAt(j).parent;
                // int i = index_map[parent]; // this is very fast
                Integer index = index_map.get(parent);

                mUnit.elementAt(index).nchild++;
            }

            double myTH = imgTH; if (myTH<para.visible_thresh) myTH=para.visible_thresh;
            if (true)
            {
                System.out.println("labeling to remove bark leaf child ");
                System.out.printf("before dark-pruning there are %d nodes in total, from %d nodes in the initial graph. \n", mUnit.size(), num_nodes);
                //remove leaf node (nchild==0)

                for (k=0; ; k++)
                {
                    int nprune=0;
                    for (j=0; j<mUnit.size(); j++)
                    {
                        if (mUnit.elementAt(j).nchild ==0)
                        {
                            double parent = mUnit.elementAt(j).parent;
                            Integer index = index_map.get(parent);

                            int min_cut_level = 10/min_step;	//maybe can change to 1 ? 2011-01-13. by PHC
                            if (min_cut_level<1) min_cut_level=1;
                            //double va = getBlockAveValue(img3d, dim0, dim1, dim2, mUnit.elementAt(i).x,mUnit.elementAt(i).y,mUnit.elementAt(i).z,
                            //		min_cut_level, min_cut_level, min_cut_level);
                            va = getBlockAveValue(img3d, dim0, dim1, dim2, (int)mUnit.elementAt(j).x,(int)mUnit.elementAt(j).y,(int) mUnit.elementAt(j).z,
                                    min_cut_level, min_cut_level, min_cut_level);

                            //if (k<min_cut_level || va <= imgAve+imgStd*min_cut_level)

                            if (va <= myTH  //dark pruning
                                //|| (k<5
                                //&& mUnit[i].nchild >=2
                                //			   )
                            ) //this gives a symmetric pruning, seems better than (k<5) criterion which leads to an asymmetric prunning
                            {
                                mUnit.elementAt(index).nchild--;
                                //label to remove
                                mUnit.elementAt(j).nchild = -1;	//mUnit[j].parent = mUnit[j].n; //seems no need for this
                                nprune++;
                            }

                            // no need to do at this moment. by PHC, 101226
                            //					else //091108. update this node's coordinate using its CoM
                            //					{
                            //						if (1) //091120. temporary block the code for the demo
                            //						{
                            //							float curx= mUnit.elementAt(i).x, cury = mUnit.elementAt(i).y, curz = mUnit.elementAt(i).z;
                            //							fitPosition(img3d, dim0, dim1, dim2, imgTH, 3*min_cut_level, curx, cury, curz);
                            //							mUnit.elementAt(i).x = curx, mUnit.elementAt(i).y = cury, mUnit.elementAt(i).z = curz;
                            //						}
                            //					}
                        }
                    }

//                    if (0)
//                        for (j=mUnit.size()-1; j>=0; j--)
//                        {
//                            if (mUnit[j].nchild ==0)
//                            {
//                                double parent = mUnit[j].parent;
//                                // int i = index_map[parent];
//                                int i = index_map.get(parent);
//
//                                int min_cut_level = 10/min_step;	if (min_cut_level<1) min_cut_level=1;
//                                double va = getBlockAveValue(img3d, dim0, dim1, dim2, mUnit[i].x,mUnit[i].y,mUnit[i].z,
//                                        min_cut_level, min_cut_level, min_cut_level);
//
//                                if (va <= myTH || (k<5
//                                        //&& mUnit[i].nchild >=2
//                                )) //this gives a symmetric pruning, seems better than (k<5) criterion which leads to an asymmetric prunning
//                                {
//                                    mUnit[i].nchild--;
//                                    mUnit[j].nchild = -1; 							//label to remove
//                                }
//                            }
//                        }

                    System.out.printf("dark prune loop %d. remove %d nodes.\n", k, nprune);
                    if (nprune==0)
                        break;
                }
            }

            //remove those nchild<0 and rearraneg indexes

            mmUnit.add(mUnit);
            rearrange_and_remove_labeled_deletion_nodes_mmUnit(mmUnit);

            System.out.println("done with the SP step. ");
        }

        else {
            for (int npath = 0; npath < n_end_nodes; npath++) // n path of back tracing end-.start
            {
                // #define _output_shortest_path_N_
                System.out.printf("the #%d path of back tracing end-.start \n", npath + 1);
                mUnit.clear();

                j = (int) end_nodeind[npath]; //search from the last one
                cc.x = x1[npath];
                cc.y = y1[npath];
                cc.z = z1[npath];
                cc.n = nexist + 1 + mUnit.size();
                cc.parent = cc.n + 1; //父节点n属性比自己大1
                System.out.printf("[end: x y z] %d: %g %g %g \n", j, cc.x, cc.y, cc.z);
                if (j < 0 || j >= num_nodes) // for the end_node out of ROI
                {
                    System.out.println(" end_node is out of ROI, ignored.");
                    continue;
                }
                // System.out.println("");

                mUnit.add(cc.clone());

                for (k = 0; k < n; k++) //at most n edge links
                {
                    int jj = j;
                    j = plist[j];

                    if (j == jj) {
                        mUnit.clear();
                        System.out.println(s_error = "Error happens: this path is broken because a node has a self-link!");
                        System.out.printf(" [j.p(j)] %d.%d \n", jj, j);
                        break;
                    } else if (j >= num_nodes) {
                        mUnit.clear();
                        System.out.println(s_error = "Error happens: this node's parent has an index out of range!");
                        System.out.printf(" [j.p(j)] %d.%d \n", jj, j);
                        break;
                    } else if (j < 0) // should not be reached, because stop back trace at his child node
                    {
                        mUnit.clear();
                        System.out.println(s_error = "find the negative node, which should indicate the root has been over-reached.");
                        System.out.printf(" [j.p(j)] %d.%d \n", jj, j);
                        break;
                    }

                    if (j != start_nodeind) {
                        // NODE_TO_XYZ(j, cc.x, cc.y, cc.z);
                        cc.z = (double) (j / (nx * ny));
                        cc.y = (double) ((j % (nx * ny)) / nx);//(j - (int) (cc.z) * nx * ny) / nx;
                        cc.x = (double) ((j % (nx * ny)) % nx);//(j - (int) (cc.z) * nx * ny - (int) (cc.y) * nx);((j % (nx * ny)) % nx);
                        cc.x = xmin + (cc.x) * xstep;
                        cc.y = ymin + (cc.y) * ystep;
                        cc.z = zmin + (cc.z) * zstep;

                        cc.n = nexist + 1 + mUnit.size();
                        cc.parent = cc.n + 1;
                        mUnit.add(cc.clone());
                        //System.out.println("[node: x y z] %ld: %g %g %g ", j, cc.x, cc.y, cc.z);
                    } else //j==start_nodeind
                    {
                        cc.x = x0;
                        cc.y = y0;
                        cc.z = z0;
                        cc.n = nexist + 1 + mUnit.size();
                        cc.parent = -1;
                        mUnit.add(cc.clone());
                        System.out.printf("[start: x y z] %d: %g %g %g \n", j, cc.x, cc.y, cc.z);

                        break; //STOP back tracing
                    }
                }
                nexist += mUnit.size();

                if (mUnit.size() >= 2) {
                    Vector<V_NeuronSWC_unit> mUnit_tmp = new Vector<V_NeuronSWC_unit>();
                    mUnit_tmp.clear();
                    for (k = 0; k < mUnit.size(); k++) {
                        mUnit_tmp.add(mUnit.elementAt(k).clone());
                    }
                    mmUnit.add(mUnit_tmp);
                }
            }
        }

        //	//also can do smoothing outside in proj_trace_smooth_dwonsample_last_traced_neuron
        //	System.out.println("smooth_curve + downsample_curve ");
        //	smooth_curve(mCoord, smooth_winsize);
        //	mCoord = downsample_curve(mCoord, outsample_step);

        // if (end_nodeind) {delete []end_nodeind; end_nodeind=0;} //100520, by PHC

        // if (mmUnit.size())	return 0;
        return s_error;
    }

    public static double getBlockMaxValue(int[][][] img3d, int dim0, int dim1, int dim2,
                                          int x0, int y0, int z0,
                                          int xstep, int ystep, int zstep)
    {
        if (img3d == null || img3d.length == 0 || dim0<=0 || dim1<=0 || dim2<=0 ||
                x0<0 || x0>=dim0 || y0<0 || y0>=dim1 || z0<0 || z0>=dim2)
            return 0;

        double xsteph=(double) Math.abs(xstep)/2, ysteph=(double) Math.abs(ystep)/2, zsteph=(double) Math.abs(zstep)/2;
        int xs=(int) (x0-xsteph), xe=(int) (x0+xsteph),ys=(int) (y0-ysteph), ye=(int) (y0+ysteph),zs=(int) (z0-zsteph), ze=(int) (z0+zsteph);

        if (xs<0) xs=0; if (xe>=dim0) xe=(int) dim0-1;
        if (ys<0) ys=0; if (ye>=dim1) ye=(int) dim1-1;
        if (zs<0) zs=0; if (ze>=dim2) ze=(int) dim2-1;

        int i,j,k;
        double v=0;
        double mv=0;
        for (k=zs;k<=ze; k++)
            for (j=ys;j<=ye; j++)
                for (i=xs;i<=xe; i++)
                {
                    v = (double)(img3d[k][j][i]);
                    if (v>mv) mv = v;
                }
        return mv;
    }

    public static double getBlockAveValue(int[][][] img3d, int dim0, int dim1, int dim2,
                                          int x0, int y0, int z0,
                                          int xstep, int ystep, int zstep)
    {
        if (img3d == null || img3d.length == 0 || dim0<=0 || dim1<=0 || dim2<=0 ||
                x0<0 || x0>=dim0 || y0<0 || y0>=dim1 || z0<0 || z0>=dim2)
            return 0;

        double xsteph=(double) Math.abs(xstep)/2, ysteph=(double) Math.abs(ystep)/2, zsteph=(double) Math.abs(zstep)/2;
        int xs=(int) (x0-xsteph), xe=(int) (x0+xsteph),ys=(int) (y0-ysteph), ye=(int) (y0+ysteph),zs=(int) (z0-zsteph), ze=(int) (z0+zsteph);

        if (xs<0) xs=0; if (xe>=dim0) xe=(int) dim0-1;
        if (ys<0) ys=0; if (ye>=dim1) ye=(int) dim1-1;
        if (zs<0) zs=0; if (ze>=dim2) ze=(int) dim2-1;

        int i,j,k,n;
        double v=0;
        n=0;
        for (k=zs;k<=ze; k++)
            for (j=ys;j<=ye; j++)
                for (i=xs;i<=xe; i++)
                {
                    v += (double)(img3d[k][j][i]);
                    n++;
                }
        return (n==0)?0:v/n;
    }




    public static boolean setBlockAveValue(int[][][] img3d, int dim0, int dim1, int dim2,
                                           int x0, int y0, int z0,
                                           int xstep, int ystep, int zstep, int target_val)
    {
        if (img3d == null || img3d.length == 0 || dim0<=0 || dim1<=0 || dim2<=0 ||
                x0<0 || x0>=dim0 || y0<0 || y0>=dim1 || z0<0 || z0>=dim2)
            return false;

        double xsteph=(double) Math.abs(xstep)/2, ysteph=(double) Math.abs(ystep)/2, zsteph=(double) Math.abs(zstep)/2;
        int xs=(int) (x0-xsteph), xe=(int) (x0+xsteph),ys=(int) (y0-ysteph), ye=(int) (y0+ysteph),zs=(int) (z0-zsteph), ze=(int) (z0+zsteph);

        if (xs<0) xs=0; if (xe>=dim0) xe=(int) dim0-1;
        if (ys<0) ys=0; if (ye>=dim1) ye=(int) dim1-1;
        if (zs<0) zs=0; if (ze>=dim2) ze=(int) dim2-1;

        int i,j,k;
        for (k=zs;k<=ze; k++)
            for (j=ys;j<=ye; j++)
                for (i=xs;i<=xe; i++)
                {
                    img3d[k][j][i] = target_val;
                }
        return true;
    }


    public static double getBlockStdValue(int[][][] img3d, int dim0, int dim1, int dim2,
                                          int x0, int y0, int z0,
                                          int xstep, int ystep, int zstep)
    {
        if (img3d == null || img3d.length == 0 || dim0<=0 || dim1<=0 || dim2<=0 ||
                x0<0 || x0>=dim0 || y0<0 || y0>=dim1 || z0<0 || z0>=dim2)
            return 0;

        double blockAve = getBlockAveValue(img3d, dim0, dim1, dim2,
                x0, y0, z0,
                xstep, ystep, zstep);

        double xsteph=(double) Math.abs(xstep)/2, ysteph=(double) Math.abs(ystep)/2, zsteph=(double) Math.abs(zstep)/2;
        int xs=(int) (x0-xsteph), xe=(int) (x0+xsteph),ys=(int) (y0-ysteph), ye=(int) (y0+ysteph),zs=(int) (z0-zsteph), ze=(int) (z0+zsteph);

        if (xs<0) xs=0; if (xe>=dim0) xe=(int) dim0-1;
        if (ys<0) ys=0; if (ye>=dim1) ye=(int) dim1-1;
        if (zs<0) zs=0; if (ze>=dim2) ze=(int) dim2-1;

        int i,j,k,n;
        double v=0;
        n=0;
        for (k=zs;k<=ze; k++)
            for (j=ys;j<=ye; j++)
                for (i=xs;i<=xe; i++)
                {
                    double d = ((double)(img3d[k][j][i]) - blockAve);
                    v += d*d;
                    n++;
                }
        return (n==0)?0: Math.sqrt(v/n);
    }

    public static double getImageMaxValue(int[][][] img3d, int dim0, int dim1, int dim2)
    {
        int x0 = dim0/2;
        int y0 = dim1/2;
        int z0 = dim2/2;
        int xstep = (int) dim0;
        int ystep = (int) dim1;
        int zstep = (int) dim2;
        return getBlockMaxValue(img3d, dim0, dim1, dim2, x0, y0, z0, xstep, ystep, zstep);
    }
    public static double getImageAveValue(int[][][] img3d, int dim0, int dim1, int dim2)
    {
        int x0 = dim0/2;
        int y0 = dim1/2;
        int z0 = dim2/2;
        int xstep = (int) dim0;
        int ystep = (int) dim1;
        int zstep = (int) dim2;
        return getBlockAveValue(img3d, dim0, dim1, dim2, x0, y0, z0, xstep, ystep, zstep);
    }


    public static double getImageStdValue(int[][][] img3d, int dim0, int dim1, int dim2)
    {
        int x0 = dim0/2;
        int y0 = dim1/2;
        int z0 = dim2/2;
        int xstep = (int) dim0;
        int ystep = (int) dim1;
        int zstep = (int) dim2;
        return getBlockStdValue(img3d, dim0, dim1, dim2, x0, y0, z0, xstep, ystep, zstep);
    }

    public static double metric_func(double v, double max_v)
    {
        double tmpv = 1.0-v/max_v;
        return	Math.exp((tmpv*tmpv)*10); //float min-step:1e-6, min:1e-37, max:1e38
    }

    public static double edge_weight_func(double dist, double va, double vb, double max_v)
    {
        double m_ab = (metric_func(va, max_v) + metric_func(vb, max_v))*0.5;
        // (metric_func((va + vb, max_v)*0.5);

        final double min_weight_step = 1e-5;   //090915 more precise //float min-step:1e-6
        return (dist * m_ab) *min_weight_step;
    }

    public double edge_weight_func(int it, double va, double vb, double max_v)
    {
        double dist = edge_table[it].dist;
        double m_ab = (metric_func(va, max_v) + metric_func(vb, max_v))*0.5;
        //System.out.println("[%5.3f] ", m_ab);
        // (metric_func((va + vb, max_v)*0.5);

        final double min_weight_step = 1e-5;   //090915 more precise //float min-step:1e-6
        return (dist * m_ab) *min_weight_step;
    }

    //090512 PHC: add function phc_shortest_path()
    public String phc_shortest_path(Vector<Pair<Integer,Integer>> edge_array, int n_edges, Vector<Float> weights, int n_nodes, //input graph
                             int start_nodeind, //input source
                             int[] plist) //output path
    {
        String s_error;

        //check data
        if (edge_array.isEmpty() || n_edges<=0 || weights.isEmpty() || n_nodes<=0 ||
                start_nodeind<0 || start_nodeind>=n_nodes ||
                plist == null || plist.length == 0)
        {
            System.out.println(s_error="Invalid parameters to phc_shortest_path(). do nothing");
            return s_error;
        }

        //copy data

        DijkstraClass p = new DijkstraClass(n_nodes);
        // if (!p)
        // {
        //     System.out.println(s_error="Fail to allocate memory for DijkstraClass().");
        //     return s_error;
        // }
//        p.nnode = n_nodes;
//        p.allocatememory(p.nnode);
        if (p.adjMatrix.isEmpty())
        {
            System.out.println(s_error="Fail to assign value to the internal edge matrix.");
            // if (p) {delete p; p=0;}
            return s_error;
        }

        // char* copyEdgeSparseData2Adj(Edge *edge_array, int n_edges, Weight *weights, int n_nodes,
        //                         vector <connectionVal> * adjMatrix,
        //                         float &minn,  float &maxx);

        Float minlevel = 1e+3f,maxlevel = 0f;//what to use?
        s_error = copyEdgeSparseData2Adj(edge_array, n_edges, weights, n_nodes,
                p.adjMatrix,
                minlevel, maxlevel);
        if (s_error != "")
        {
            System.out.println(s_error="Fail to assign value to the internal edge matrix.");
            // if (p) {delete p; p=0;}
            return s_error;
        }

        System.out.println("prepare dosearch----------------------------");

        p.dosearch(start_nodeind); //set root as the first node

        System.out.println("search end-----------------------------");

        //copy the output plist
        for (int i=0;i<n_nodes;i++)
        {
            plist[i] = p.nodeParent[i]-1; //-1 because I used the Matlab convention in dijk core function
        }

        //free memory and return

        // if (p) {delete p; p=0;}
        return s_error;
    }

    public String my_shortest_path(Vector<Pair<Integer,Integer>> edge_array, int n_edges, Vector<Float> weights, int n_nodes, //input graph
                                   int start_nodeind, //input source
                                   int[] plist){
        String s_error = "";

        //check data
        if (edge_array.isEmpty() || n_edges<=0 || weights.isEmpty() || n_nodes<=0 ||
                start_nodeind<0 || start_nodeind>=n_nodes ||
                plist == null || plist.length == 0)
        {
            System.out.println(s_error="Invalid parameters to my_shortest_path(). do nothing");
            return s_error;
        }

        try{
            Graph g = new Graph(n_nodes,edge_array,weights);
            g.search(start_nodeind);
            for(int i=0; i<g.plist.length; i++){
                plist[i] = g.plist[i];
            }
        }catch (OutOfMemoryError e){
            s_error = e.getMessage();
            System.out.println(e.getMessage());
        }

        return s_error;
    }

    public static String copyEdgeSparseData2Adj(Vector<Pair<Integer,Integer>> edge_array, int n_edges, Vector<Float> weights, int n_nodes,
                                                Vector<Vector<ConnectionVal>> adjMatrix,
                                                Float minn,
                                                Float maxx)
    {
        String s_error = "";

        if (edge_array.isEmpty() || n_edges<=0 || weights.isEmpty() || n_nodes<=0 || adjMatrix.isEmpty())
        {System.out.println(s_error="the adjMatrix pointer or nnodes of copyEdgeSparseData2Adj() is invalid. Do nothing.");return s_error;}

        for (int i=0; i<n_edges; i++)
        {
            if (edge_array.elementAt(i).getKey()<0 || edge_array.elementAt(i).getKey()>=n_nodes
                    || edge_array.elementAt(i).getValue()<0 || edge_array.elementAt(i).getValue()>=n_nodes)
            {
                System.out.printf("illegal edge parent and child node info found in copyEdgeSparseData2Adj(). [%d %d w=%5.3f] n_nodes=%d. do nothing\n",
                        edge_array.elementAt(i).getKey(), edge_array.elementAt(i).getValue(), weights.elementAt(i), n_nodes);
                return s_error = "illegal edge parent and child node info found in copyEdgeSparseData2Adj().";
            }

            ConnectionVal tmpVal = new ConnectionVal();

            // #define ADD_EDGE(p, c, w) { tmpVal.pNode=p; tmpVal.cNode=c; tmpVal.aVal=w; adjMatrix[tmpVal.pNode].add(tmpVal); }

            tmpVal.pNode=edge_array.elementAt(i).getKey();
            tmpVal.cNode=edge_array.elementAt(i).getValue();
            tmpVal.aVal=weights.elementAt(i);
            adjMatrix.elementAt(tmpVal.pNode).add(tmpVal);

            tmpVal.pNode=edge_array.elementAt(i).getValue();
            tmpVal.cNode=edge_array.elementAt(i).getKey();
            tmpVal.aVal=weights.elementAt(i);
            adjMatrix.elementAt(tmpVal.pNode).add(tmpVal);

            // ADD_EDGE( edge_array[i].getKey(), edge_array[i].getValue(), weights[i]);
            // ADD_EDGE( edge_array[i].getValue(), edge_array[i].getKey(), weights[i]);

            if (i==0)
            {
                maxx = minn = weights.elementAt(i);
            }
            else
            {
                if (weights.elementAt(i)>maxx) maxx=weights.elementAt(i);
                else if (weights.elementAt(i)<minn) minn=weights.elementAt(i);
            }
        }
        return s_error;
    }

    public static void rearrange_and_remove_labeled_deletion_nodes_mmUnit(Vector< Vector<V_NeuronSWC_unit> > mmUnit) throws  Exception//by PHC, 2011-01-15
    {
        System.out.println("....... rearranging index number ");
        Map<Double,Integer> index_map = new HashMap<Double, Integer>();

        int i,j,k;

        for (k=0; k<mmUnit.size(); k++)
        {
            System.out.printf("....... removing the [%d] segment children that have been labeled to delete (i.e. nchild < 0).\n", k);
            Vector<V_NeuronSWC_unit>  mUnit = mmUnit.elementAt(k);
            index_map.clear();

            for (j=0; j<mUnit.size(); j++)
            {
                if (mUnit.elementAt(j).nchild >=0)
                {
                    double ndx   = mUnit.elementAt(j).n;
                    int new_ndx = index_map.size()+1;
                    // index_map[ndx] = new_ndx;
                    index_map.put(ndx,new_ndx);
                }
            }
            for (j=0; j<mUnit.size(); j++)
            {
                if (mUnit.elementAt(j).nchild >=0)
                {
                    double ndx    = mUnit.elementAt(j).n;
                    double parent = mUnit.elementAt(j).parent;
                    // mUnit[j].n = index_map[ndx];
                    // if (parent>=0)	mUnit[j].parent = index_map[parent];
                    mUnit.elementAt(j).n = index_map.get(ndx);
                    if (parent>=0)	mUnit.elementAt(j).parent = index_map.get(parent);
                }
            }

            Vector<V_NeuronSWC_unit> mUnit_new = new Vector<V_NeuronSWC_unit>();
            int root_id=-1;
            for (j=0; j<mUnit.size(); j++)
            {
                if (mUnit.elementAt(j).nchild >= 0)
                {
                    mUnit_new.add(mUnit.elementAt(j).clone());
                }

                if (mUnit.elementAt(j).parent<0)
                {
                    if (root_id!=-1)
                        System.out.printf("== [segment %d] ================== detect a non-unique root!\n", k);
                    root_id = (int)(mUnit.elementAt(j).n);
                    System.out.printf("== [segment %d] ================== nchild of root [%d, id=%d] = %d\n",
                            k, j, (int)(mUnit.elementAt(j).n), (int)(mUnit.elementAt(j).nchild));
                }

            }

//            mmUnit[k] = mUnit_new;
            mmUnit.elementAt(k).clear();
            for(i=0; i<mUnit_new.size(); i++){
                mmUnit.elementAt(k).add(mUnit_new.elementAt(i).clone());
            }
        }
    }

    // assume root node at tail of vector (result of back tracing)
    public static String merge_back_traced_paths(Vector< Vector<V_NeuronSWC_unit> > mmUnit) throws Exception
    {
        System.out.println("merge_back_traced_paths ");
        String s_error = "";
        int npath = mmUnit.size();
        if (npath <2) return s_error; // no need to merge

        Vector< Vector<V_NeuronSWC_unit> > all_segment = new Vector<Vector<V_NeuronSWC_unit>>();


//        Vector<int> path_start(npath); // 0-based index
//        Vector<int> path_try(npath);   // 0-based index
        int[] path_start = new int[npath];
        int[] path_try = new int[npath];
        for (int i=0; i<npath; i++)
            path_try[i] = path_start[i] = mmUnit.elementAt(i).size()-1;

        int nexist = 0; // count output node index
        int lastn_same_node;

        int[] flag_skipped = new int [npath];; //added by PHC, 2010-05-22. // 0--searching path, 1--connected branch, 2--separated branch, 3--over searched
        for (;;) //iteration for all segment
        {
            for (int i=0; i<npath; i++)		flag_skipped[i] = (path_start[i] <1)? 3:0;

            System.out.printf("all_segment.size=%d >> \n", all_segment.size());
            //		System.out.println("path_start:      "); 	for (int i=0; i<npath; i++)		System.out.println("%d(%ld) ", i+1, path_start[i]);	System.out.println("");
            //		System.out.println("path_try:        "); 	for (int i=0; i<npath; i++)		System.out.println("%d(%ld) ", i+1, path_try[i]);	System.out.println("");
            //		System.out.println("flag_skipped:    "); 	for (int i=0; i<npath; i++)		System.out.println("%d(%ld) ", i+1, flag_skipped[i]);	System.out.println("");

            /////////////////////////////////////////////////////////////////////////
            boolean all_skipped = true;
            for (int i=0; i<npath; i++)
                all_skipped = (all_skipped && flag_skipped[i]>0);

            if (all_skipped)
                break; /// STOP iteration when every path is over searched
            /////////////////////////////////////////////////////////////////////////

            int jj;
            V_NeuronSWC_unit same_node = new V_NeuronSWC_unit();
            int n_same_node;
            int ipath = -1;
            lastn_same_node = 0;

            for (int j=0; true; j++) //searching same_segment aint 1 path with other paths
            {
                n_same_node = 0;

                for (int i=0; i<npath; i++) //find a same node with other paths
                {
                    if (flag_skipped[i]>0) continue; // skip the path because of skipped branch/or over searched

                    jj = path_try[i]-1; // ##################
                    if (jj <0) // this path is over searched=======================
                    {
                        flag_skipped[i] = 3;
                        if (lastn_same_node==1)		break; 		// no other connected path
                        else						continue;	// try other connected path
                    }
                    V_NeuronSWC_unit  node = mmUnit.elementAt(i).elementAt((int) jj).clone();

                    if (n_same_node==0) // start a new segment by picking a node as a template ######################
                    {
                        same_node = node.clone();
                        n_same_node = 1;
                        ipath = i;
                        if (lastn_same_node==1)		break; 		// no other connected path
                        else						continue;	// compare with other connected path
                    }
                    else //(n_same_node >0)
                    {
                        if (same_node.x == node.x && same_node.y == node.y && same_node.z == node.z)
                        {
                            n_same_node ++;
                        }
                        else // this path is a branch================================
                        {
                            path_start[i] = path_try[i]; // ###################
                            switch (lastn_same_node)
                            {
                                case 0: // start a new iteration
                                    flag_skipped[i] = 2; // separated branch
                                    break;
                                case 1: // only 1 connected branch, here is impossible
                                    flag_skipped[i] = 3; // over searched
                                    break;
                                default: //(lastn_same_node >1)
                                    flag_skipped[i] = 1; // have other connected segment
                                    break;
                            }
                            continue; 				// try other connected path
                        }
                    }
                }//i -- compared the same_node with each start-node of other paths
                if (lastn_same_node==0) // initialize lastn_same_node when start a new iteration #####################
                {
                    lastn_same_node = n_same_node;
                }
                //System.out.println("   [%ld: ipath lastn_same_node n_same_node]         %d %d %d ", j, ipath+1, lastn_same_node, n_same_node);
                //System.out.println("	flag_skipped:    "); 	for (int i=0; i<npath; i++)		System.out.println("%d(%ld) ", i+1, flag_skipped[i]);	System.out.println("");
                //System.out.println("	path_start:      "); 	for (int i=0; i<npath; i++)		System.out.println("%d(%ld) ", i+1, path_start[i]);	System.out.println("");
                //System.out.println("	path_try:        "); 	for (int i=0; i<npath; i++)		System.out.println("%d(%ld) ", i+1, path_try[i]);	System.out.println("");


                if (n_same_node >0 && n_same_node ==lastn_same_node) // in a same segment to merge
                {
                    //System.out.println("add a node ");
                    for (int i=0; i<npath; i++)
                    {
                        switch (flag_skipped[i])
                        {
                            case 0: // searching path
                                path_try[i] --; // ##################
                                break;
                        }
                    }
                }
                else if (n_same_node <lastn_same_node ) // save the merged segment to output buffer
                {
                    //System.out.println("end of a segment ");
                    // push nodes from path_start to path_try
                    Vector<V_NeuronSWC_unit> same_segment = new Vector<V_NeuronSWC_unit>();
//                    same_segment.clear();
                    same_segment.clear();
                    if (ipath > -1)
                    {
                        System.out.printf("add a valid segment form path %d(%d -- %d)\n", ipath+1, path_start[ipath], path_try[ipath]);
                        for (jj = path_start[ipath]; jj >= path_try[ipath]; jj--)
                        {
                            same_node = mmUnit.elementAt(ipath).elementAt((int) jj).clone();
                            nexist ++;
                            same_node.n = nexist;
                            same_node.parent = nexist+1;
                            same_segment.add(same_node.clone());
                        }
                        same_segment.elementAt(same_segment.size()-1).parent = -1; //====================make segment have a root node
                        all_segment.add(same_segment);
                    }
                    // adjust start node index of branch
                    for (int i=0; i<npath; i++)
                    {
                        switch (flag_skipped[i])
                        {
                            case 0: // searching path
                            case 3: // over searched
                                path_start[i] = path_try[i]; // ################
                                break;
                        }
                    }

                    ///////////////////////////////////////////////////////////////////////////
                    if (n_same_node <1) // no need to continue merging node
                        break; // STOP at end of 1 branch
                    ///////////////////////////////////////////////////////////////////////////
                }
                else
                {
                    mmUnit = all_segment;
                    System.out.println(s_error="Error happens: n_same_node > lastn_same_node in merge_back_traced_paths() ");
                    return s_error;
                }

                lastn_same_node = n_same_node;

            }//j -- aint 1 branch
        }//all branch & path

        // if (flag_skipped) {delete []flag_skipped; flag_skipped=0;} //added by PHC, 2010-05-22
//        mmUnit = all_segment;
        for(int i=0; i<mmUnit.size(); i++){
            mmUnit.elementAt(i).clear();
        }
        for(int i=0; i<all_segment.size();i++){
            Vector<V_NeuronSWC_unit> tmp = new Vector<V_NeuronSWC_unit>();
            tmp.clear();
            for(int j=0; j<all_segment.elementAt(i).size(); j++){
                tmp.add(all_segment.elementAt(i).elementAt(j).clone());
            }
            mmUnit.add(tmp);
        }
        return s_error;
    }

    public static boolean smooth_radius(Vector <V_NeuronSWC_unit>  mCoord, int winsize, boolean median_filter) throws Exception
    {
        //std::cout<<" smooth_radius ";
        if (winsize < 2) return true;

        // Vector<T> mC = mCoord; // a copy
        Vector<V_NeuronSWC_unit> mC = new Vector<V_NeuronSWC_unit>();
        for(int s=0; s<mCoord.size(); s++){
            mC.add(mCoord.elementAt(s));
        }
        int N = mCoord.size();
        int halfwin = winsize / 2;

        for (int i = 1; i < N - 1; i++) // don't move start & end point
        {
            Vector<V_NeuronSWC_unit> winC = new Vector<V_NeuronSWC_unit>();
            Vector<Double> winW = new Vector<Double>();
            winC.clear();
            winW.clear();

            winC.add(mC.elementAt(i));
            winW.add(1.0 + halfwin);
            for (int j = 1; j <= halfwin; j++)
            {
                int k1 = i + j;	if (k1<0) k1 = 0;	if (k1>N - 1) k1 = N - 1;
                int k2 = i - j;	if (k2<0) k2 = 0;	if (k2>N - 1) k2 = N - 1;
                winC.add(mC.elementAt(k1).clone());
                winC.add(mC.elementAt(k2).clone());
                winW.add(1.0 + halfwin - j);
                winW.add(1.0 + halfwin - j);
            }
            //std::cout<<"winC.size = "<<winC.size()<<"\n";

            double r = 0;
            if (median_filter)
            {
                // sort(winC.begin(), winC.end(), less_r);
                Collections.sort(winC, new Comparator<V_NeuronSWC_unit>() {
                    @Override
                    public int compare(V_NeuronSWC_unit o1, V_NeuronSWC_unit o2) {
                        return o1.r<o2.r?1:-1;
                    }
                });
                r = winC.elementAt(halfwin).r;
            }
            else
            {
                double s = r = 0;
                for (int j = 0; j < winC.size(); j++)
                {
                    r += winW.elementAt(j) * winC.elementAt(j).r;
                    s += winW.elementAt(j);
                }
                if (s>0)	r /= s;
            }

            mCoord.elementAt(i).r = r; // output
        }
        return true;
    }

    public static void curve_bending_vector(Vector<V_NeuronSWC_unit> mCoord, int i, V_NeuronSWC_unit Coord_new)
    {
        float[] D = new float[3];
        // DIFF(D[0], mCoord, i, x, 5);
        // DIFF(D[1], mCoord, i, y, 5);
        // DIFF(D[2], mCoord, i, z, 5);
        DIFF(D,mCoord,i,5);
        //printf("[%g,%g,%g]	", D[0],D[1],D[2]);

        float x = (float) mCoord.elementAt(i).x;
        float y = (float) mCoord.elementAt(i).y;
        float z = (float) mCoord.elementAt(i).z;
        float cx = (float) Coord_new.x;
        float cy = (float) Coord_new.y;
        float cz = (float) Coord_new.z;
        {
            // make normal vector
            double len = sqrt(D[0] * D[0] + D[1] * D[1] + D[2] * D[2]);
            if (len>0)
            {
                D[0] /= len;
                D[1] /= len;
                D[2] /= len;
                // displacement
                cx = cx - x;
                cy = cy - y;
                cz = cz - z;
                //printf("<%g,%g,%g>	", cx,cy,cz);
                float proj = cx*D[0] + cy*D[1] + cz*D[2];
                cx = cx - proj*D[0];
                cy = cy - proj*D[1];
                cz = cz - proj*D[2];
                //printf("<<%g,%g,%g>>	", cx,cy,cz);
                x += cx;
                y += cy;
                z += cz;
            }
        }
        Coord_new.x = x;
        Coord_new.y = y;
        Coord_new.z = z;
    }

    public static double I(int[][][] img3d, int dim0, int dim1, int dim2, int ix, int iy, int iz){
        if ((ix<0) || (ix>dim0 - 1)) return 0;
        if ((iy<0) || (iy>dim1 - 1)) return 0;
        if ((iz<0) || (iz>dim2 - 1)) return 0;
        return (double)(img3d[iz][iy][ix]);
    }

    // 090520: create according to point_bdb_minus_3d_localwinmass()
    // 090619: move from bdb_minus.h to here
    // And combined with adaptive radius estimation & orthogonal shift
    // 090621: add bending_code
    public static boolean point_bdb_minus_3d_localwinmass_prior(int[][][] img3d, int dim0, int dim1, int dim2,
                                                                   Vector <V_NeuronSWC_unit>  mCoord, BDB_Minus_Prior_Parameter  bdb_para, boolean b_fix_end,
                                                                   Vector <V_NeuronSWC_unit>  mCoord_prior, int bending_code, float zthickness, boolean b_est_in_xyplaneonly) throws Exception// 0--no bending, 1--bending M_term, 2--bending mCoord
    {
        boolean b_use_M_term = true; //for switch
        boolean b_use_P_term = true; //for switch
        boolean b_use_G_term = false; //for switch


        System.out.println("bending code: "+bending_code);
//        bending_code = 1;
        double f_image = 1;
        double f_length = bdb_para.f_length;
        double f_smooth = bdb_para.f_smooth;
        double f_prior = bdb_para.f_prior; //0.2;
        double f_gradient = 0;/// gradient is not stable
        int max_loops = bdb_para.nloops;

        double TH = 1; //pixel/node, the threshold to judge convergence

        double AR = 0;
        for (int i = 0; i < mCoord.size() - 1; i++)
        {
            double x = mCoord.elementAt(i).x;
            double y = mCoord.elementAt(i).y;
            double z = mCoord.elementAt(i).z;
            double x1 = mCoord.elementAt(i+1).x;
            double y1 = mCoord.elementAt(i+1).y;
            double z1 = mCoord.elementAt(i+1).z;
            AR += sqrt((x - x1)*(x - x1) + (y - y1)*(y - y1) + (z - z1)*(z - z1));
        }
        AR /= mCoord.size() - 1; // average distance between nodes
        double radius = AR * 2;
        double imgAve = getImageAveValue(img3d, dim0, dim1, dim2);
        double imgStd = getImageStdValue(img3d, dim0, dim1, dim2);
        double imgTH = imgAve + imgStd;


        int M = mCoord.size(); // number of control points / centers of k_means
        if (M <= 2) return true; //in this case no adjusting is needed. by PHC, 090119. also prevent a memory crash
        int M_prior = mCoord_prior.size(); // number of prior control points

        int i, j;
        V_NeuronSWC_unit F_1_term = new V_NeuronSWC_unit();
        V_NeuronSWC_unit F_2_term = new V_NeuronSWC_unit();
        V_NeuronSWC_unit M_term = new V_NeuronSWC_unit();
        V_NeuronSWC_unit P_term = new V_NeuronSWC_unit();
        V_NeuronSWC_unit G_term = new V_NeuronSWC_unit();
        // Vector <E> mCoord_new = mCoord; // temporary out
        // Vector <E> mCoord_old = mCoord; // a constant copy
        Vector <V_NeuronSWC_unit> mCoord_new = new Vector<V_NeuronSWC_unit>(), mCoord_old = new Vector<V_NeuronSWC_unit>();
        for(int s=0; s<mCoord.size(); s++){
            mCoord_new.add(mCoord.elementAt(s).clone());
            mCoord_old.add(mCoord.elementAt(s).clone());
        }

        double lastscore = 0;

        int wp_start = 0;
        int wp_end = 0;
//        int wp_radius = 10;
//        if (mCoord[0].timestamp == 99999){
//            wp_start = wp_radius;
//            System.out.println("yes");
//        }
//        else{
//            System.out.println("no");
//        }
//
//        if (mCoord[M - 1].timestamp == 99999){
//            wp_end = wp_radius;
//            System.out.println("yes");
//        }
//        else{
//            System.out.println("no");
//        }


        double org_x, org_y, org_z;
        org_x = mCoord.elementAt(M-1).x;
        org_y = mCoord.elementAt(M-1).y;
        org_z = mCoord.elementAt(M-1).z;
        System.out.println("max_loop: "+max_loops);
        for (int nloop = 0; nloop < max_loops; nloop++)
        {
            // for each control point
            double average_radius = 0;
            for (j = 0 + wp_start; j < M - wp_end; j++)
            {
                //mCoord_new[j].timestamp = 66666;
                //==================================================================
                // external force term initialization
                M_term = mCoord.elementAt(j).clone();
                P_term = mCoord.elementAt(j).clone();
                G_term = mCoord.elementAt(j).clone(); // 090623 RZC

                //------------------------------------------------------------------
                //image force: M_term

                if (img3d != null && (b_use_M_term))
                {
                    double xc = mCoord.elementAt(j).x;
                    double yc = mCoord.elementAt(j).y;
                    double zc = mCoord.elementAt(j).z;

                    //090621 RZC: dynamic radius estimation
                    radius = 2*fitRadiusPercent(img3d, dim0, dim1, dim2, imgTH, AR * 2, (float) xc, (float) yc, (float) zc, zthickness, b_est_in_xyplaneonly);
                    //radius = radius / 2;
//                    if (radius > 10){
//                        radius = 10;
//                    }
//                    if (radius < 2){
//                        radius = 2;
//                    }
                    //mCoord_new[j].r = radius;

                    //System.out.println(radius);
                    average_radius += radius / M;
                    //                                System.out.println(radius);

                    int x0 = (int) (xc - radius); x0 = Math.max(x0, 0);
                    int x1 = (int) (xc + radius); x1 = Math.min(x1, dim0 - 1);
                    int y0 = (int) (yc - radius); y0 = Math.max(y0, 0);
                    int y1 = (int) (yc + radius); y1 = Math.min(y1, dim1 - 1);
                    int z0 = (int) (zc - radius / zthickness); z0 = Math.max(z0, 0);
                    int z1 = (int) (zc + radius / zthickness); z1 = Math.min(z1, dim2 - 1);

                    double sum_x = 0, sum_y = 0, sum_z = 0, sum_px = 0, sum_py = 0, sum_pz = 0;
                    int ix, iy, iz;
                    //use a sphere region, as this is easiest to compute the unbiased center of mass
                    double dx, dy, dz, r2 = (double)(radius)*(radius);
                    for (iz = z0; iz <= z1; iz++)
                    {
                        dz = Math.abs(iz - zc) * zthickness; dz *= dz;
                        for (iy = y0; iy <= y1; iy++)
                        {
                            dy = Math.abs(iy - yc); dy *= dy;
                            if (dy + dz > r2) continue;
                            dy += dz;

                            for (ix = x0; ix < x1; ix++)
                            {
                                dx = Math.abs(ix - xc); dx *= dx;
                                if (dx + dy > r2) continue;

                                //register unsigned char tmpval = img3d[iz][iy][ix];

                                double tmpval = img3d[iz][iy][ix];


                                tmpval = Math.pow(tmpval / 10.0,2);
                                //if (tmpval>240) {
                                //
                                //	//System.out.println((tmpval*tmpval) << " " << (tmpval ^ 2));
                                //	System.out.println(tmpval);
                                //}
                                //tmpval = tmpval ^ 2;
                                if (tmpval>0)
                                {
                                    sum_x += tmpval;
                                    sum_y += tmpval;
                                    sum_z += tmpval;
                                    sum_px += tmpval * ix;
                                    sum_py += tmpval * iy;
                                    sum_pz += tmpval * iz;
                                }
                            }
                        }
                    }
                    if (sum_x>0 && sum_y>0 && sum_z>0)
                    {
                        M_term.x = sum_px / sum_x;
                        M_term.y = sum_py / sum_y;
                        M_term.z = sum_pz / sum_z;
                    }
                    else
                    {
                        M_term.x = xc;
                        M_term.y = yc;
                        M_term.z = zc;
                    }
                    /////////////////////////////////////////////
                    //std::System.out.println("wp_debug: " << __LINE__ << " " << bending_code);
                    //090621 RZC
                    if (bending_code == 1)	curve_bending_vector(mCoord, j, M_term);
                    /////////////////////////////////////////////
                }


                //----------------------------------------------------------------
                // image prior G_term (grident)
                if (img3d != null && b_use_G_term)
                {
                    double xc = mCoord.elementAt(j).x;
                    double yc = mCoord.elementAt(j).y;
                    double zc = mCoord.elementAt(j).z;

                    int ix = (int) (xc + 0.5);
                    int iy = (int) (yc + 0.5);
                    int iz = (int) (zc + 0.5);

                    double gx = 0;
                    for (int jj = -1; jj <= 1; jj++) for (int k = -1; k <= 1; k++)
                    {
                        gx += I(img3d, dim0, dim1, dim2, ix + 1, iy + jj, iz + k);
                        gx -= I(img3d, dim0, dim1, dim2, ix - 1, iy + jj, iz + k);
                    }
                    double gy = 0;
                    for (int jj = -1; jj <= 1; jj++) for (int k = -1; k <= 1; k++)
                    {
                        gy += I(img3d, dim0, dim1, dim2, ix + jj, iy + 1, iz + k);
                        gy -= I(img3d, dim0, dim1, dim2, ix + jj, iy - 1, iz + k);
                    }
                    double gz = 0;
                    for (int jj = -1; jj <= 1; jj++) for (int k = -1; k <= 1; k++)
                    {
                        gz += I(img3d, dim0, dim1, dim2, ix + k, iy + jj, iz + 1);
                        gz -= I(img3d, dim0, dim1, dim2, ix + k, iy + jj, iz - 1);
                    }
                    double Im = I(img3d, dim0, dim1, dim2, ix, iy, iz);

                    // factor to connect grayscle with pixel step for G_term
                    double gradient_step = (imgStd>0) ? 1 / (imgStd*imgStd) : 0;
                    Im = gradient_step*(255 - Im);

                    G_term.x += gx*Im;
                    G_term.y += gy*Im;
                    G_term.z += gz*Im;
                }

                //------------------------------------------------------------------
                // geometric prior P_term
                if (mCoord_prior.size() > 0 && (b_use_P_term))
                {
                    P_term = mCoord_prior.elementAt(0);
                    double dx, dy, dz;
                    dx = mCoord.elementAt(j).x - mCoord_prior.elementAt(0).x;
                    dy = mCoord.elementAt(j).y - mCoord_prior.elementAt(0).y;
                    dz = mCoord.elementAt(j).z - mCoord_prior.elementAt(0).z;
                    double d0 = sqrt(dx*dx + dy*dy + dz*dz);

                    for (int ip = 1; ip < mCoord_prior.size(); ip++)
                    {
                        dx = mCoord.elementAt(j).x - mCoord_prior.elementAt(ip).x;
                        dy = mCoord.elementAt(j).y - mCoord_prior.elementAt(ip).y;
                        dz = mCoord.elementAt(j).z - mCoord_prior.elementAt(ip).z;
                        double d1 = sqrt(dx*dx + dy*dy + dz*dz);

                        if (d1 < d0)
                        {
                            P_term = mCoord_prior.elementAt(ip);
                            d0 = d1;
                        }
                    }
                }

                //printf("M_term : [%5.3f, %5.3f, %5.3f], %d\n", M_term.x, M_term.y, M_term.z, j);
                //printf("P_term : [%5.3f, %5.3f, %5.3f], %d\n", P_term.x, P_term.y, P_term.z, j);
                //printf("G_term : [%5.3f, %5.3f, %5.3f], %d\n", G_term.x, G_term.y, G_term.z, j);


                //========================================================================================================================
                //           b{Ckm1 + Ckp1} + c{Ckm1 + Ckp1 - 0.25 Ckm2 - 0.25 Ckp2} + a{Mk} + d{Pk} + e{Ck + (1 - I[Ck] / Imax) I'[Ck]}
                // Ck_new = ---------------------------------------------------------------------------------------------------------------------
                //                                              (2 b + 1.5 c + a + d + e)
                //========================================================================================================================
                //internal force: F_1_term F_2_term for smoothing
                // new_coord = { f_length*F_1_term + f_smooth*F_2_term + f_image*M_term +
                //               f_prior*P_term + f_gradient*(G_term) } /(2*f_length + 1.5*f_smooth + f_image + f_prior + f_gradient)
                // boundary nodes have simple format
                double f;
                if (j == 0 || j == M - 1)
                {
                    f = (f_image + f_prior + f_gradient);
                    if (f == 0) f = 1;
                    mCoord_new.elementAt(j).x = (f_image*M_term.x + f_prior*P_term.x + f_gradient*G_term.x) / f;
                    mCoord_new.elementAt(j).y = (f_image*M_term.y + f_prior*P_term.y + f_gradient*G_term.y) / f;
                    mCoord_new.elementAt(j).z = (f_image*M_term.z + f_prior*P_term.z + f_gradient*G_term.z) / f;
                }
                else if (j == 1 || j == M - 2)
                {
                    F_1_term.x = mCoord.elementAt(j - 1).x + mCoord.elementAt(j + 1).x;
                    F_1_term.y = mCoord.elementAt(j - 1).y + mCoord.elementAt(j + 1).y;
                    F_1_term.z = mCoord.elementAt(j - 1).z + mCoord.elementAt(j + 1).z;

                    f = (2 * f_length + f_image + f_prior + f_gradient);
                    if (f == 0) f = 1;
                    mCoord_new.elementAt(j).x = (f_length*F_1_term.x + f_image*M_term.x + f_prior*P_term.x + f_gradient*G_term.x) / f;
                    mCoord_new.elementAt(j).y = (f_length*F_1_term.y + f_image*M_term.y + f_prior*P_term.y + f_gradient*G_term.y) / f;
                    mCoord_new.elementAt(j).z = (f_length*F_1_term.z + f_image*M_term.z + f_prior*P_term.z + f_gradient*G_term.z) / f;
                }
                else // not boundary nodes
                {
                    F_1_term.x = mCoord.elementAt(j - 1).x + mCoord.elementAt(j + 1).x;
                    F_1_term.y = mCoord.elementAt(j - 1).y + mCoord.elementAt(j + 1).y;
                    F_1_term.z = mCoord.elementAt(j - 1).z + mCoord.elementAt(j + 1).z;

                    F_2_term.x = (mCoord.elementAt(j - 1).x + mCoord.elementAt(j + 1).x) - 0.25* (mCoord.elementAt(j + 2).x + mCoord.elementAt(j - 2).x);
                    F_2_term.y = (mCoord.elementAt(j - 1).y + mCoord.elementAt(j + 1).y) - 0.25* (mCoord.elementAt(j + 2).y + mCoord.elementAt(j - 2).y);
                    F_2_term.z = (mCoord.elementAt(j - 1).z + mCoord.elementAt(j + 1).z) - 0.25* (mCoord.elementAt(j + 2).z + mCoord.elementAt(j - 2).z);

                    f = (2 * f_length + 1.5*f_smooth + f_image + f_prior + f_gradient);
                    if (f == 0) f = 1;
                    mCoord_new.elementAt(j).x = (f_length*F_1_term.x + f_smooth*F_2_term.x + f_image*M_term.x + f_prior*P_term.x + f_gradient*G_term.x) / f;
                    mCoord_new.elementAt(j).y = (f_length*F_1_term.y + f_smooth*F_2_term.y + f_image*M_term.y + f_prior*P_term.y + f_gradient*G_term.y) / f;
                    mCoord_new.elementAt(j).z = (f_length*F_1_term.z + f_smooth*F_2_term.z + f_image*M_term.z + f_prior*P_term.z + f_gradient*G_term.z) / f;
                }
                //cout<<"image w: "<<f_image*M_term.x<<endl;
                //cout<<"smooth w: "<<f_smooth*F_2_term.x<<endl;


                //printf("[%5.3f, %5.3f, %5.3f], %d\n", mCoord_new.elementAt(j).x, mCoord_new.elementAt(j).y, mCoord_new.elementAt(j).z, j);
            }
            //cout<<"Radius: "<<average_radius<<endl;


            ///////////////////////////////////////////////////////
            //090621 RZC
            if (bending_code == 2)
                for (j = 0; j < M; j++)		curve_bending_vector(mCoord, j, mCoord_new.elementAt(j));

            /////////////////////////////////////////////////////
            // compute curve score
            double score = 0.0;
            for (j = 0; j < M; j++)
                score += Math.abs(mCoord_new.elementAt(j).x - mCoord.elementAt(j).x) + Math.abs(mCoord_new.elementAt(j).y - mCoord.elementAt(j).y) + Math.abs(mCoord_new.elementAt(j).z - mCoord.elementAt(j).z);

            System.out.printf("score[%d]=%g \n", nloop, score);

            // update the coordinates of the control points
//            mCoord = mCoord_new;
            mCoord.clear();
            for(i=0;i<mCoord_new.size();i++){
                mCoord.add(mCoord_new.elementAt(i).clone());
            }
            /////////////////////////////////////////////////////
            if (b_fix_end)
            {
                //without changing the start and end points
                mCoord.elementAt(0).x = mCoord_old.elementAt(0).x;
                mCoord.elementAt(M-1).x = mCoord_old.elementAt(M-1).x;
                mCoord.elementAt(0).y = mCoord_old.elementAt(0).y;
                mCoord.elementAt(M-1).y = mCoord_old.elementAt(M-1).y;
                mCoord.elementAt(0).z = mCoord_old.elementAt(0).z;
                mCoord.elementAt(M-1).z = mCoord_old.elementAt(M-1).z;
            }


            //////////////////////////////////////////////////////
            // Can the iteration be terminated ?
            System.out.println("TH: "+TH);
            if (score < TH )
                break;
            if (nloop > 0)
            {
                if (Math.abs(lastscore - score) < TH*0.5 / M) // to prevent jumping around
                {
                    System.out.println("in there------------------------------");
                    break;
                }
            }

            lastscore = score;
        }



        //mCoord[M - 1].timestamp == 77777;
//        for (j = 0; j < M; j++)
//            mCoord.elementAt(j).z += 1;
        /*mCoord[M - 1].r = 1;
        mCoord[0].r = 1;*/
//        if (mCoord[M - 1].timestamp == 88888 && (org_x != mCoord[M - 1].x || org_y != mCoord[M - 1].y || org_z != mCoord[M - 1].z)){
//            mCoord[M - 1].timestamp = -mCoord[M - 1].timestamp;
//        }
        return true;
    }

    public static Vector<V_NeuronSWC_unit> downsample_curve(Vector<V_NeuronSWC_unit> mCoord, int step) throws Exception
    {
        //std::cout<<" downsample_curve ";
        if (step < 1) return mCoord;

        Vector<V_NeuronSWC_unit> mC = new Vector<V_NeuronSWC_unit>(); // for out put
        mC.clear();

        int N = mCoord.size();

        if (N > 0)	mC.add(mCoord.elementAt(0).clone()); // output
        for (int i = 1; i < N - 1; i += step) // don't move start & end point
        {
            mC.add(mCoord.elementAt(i)); // output
        }
        if (N > 1)	mC.add(mCoord.elementAt(N-1)); // output

        return mC;
    }

//    public static<E> double cosangle_two_vectors(E[] a, E[] b) //in case an error, return -2
//    {
//        double vab=0,vaa=0,vbb=0;
//        for (int i=0;i<3;i++)
//        {
//            vab += a[i]*b[i];
//            vaa += a[i]*a[i];
//            vbb += b[i]*b[i];
//        }
//        return (vaa*vbb<1e-10) ? -2 : vab/sqrt(vaa*vbb);
//    }


    public static double fitRadiusPercent(int[][][] img3d, int dim0, int dim1, int dim2, double imgTH, double bound_r,
                                          float x, float y, float z, float zthickness, boolean b_est_in_xyplaneonly)
    {
        if (zthickness<=0) { zthickness=1.0f; System.out.println("Your zthickness value in fitRadiusPercent() is invalid. disable it (i.e. reset it to 1) in computation."); }//if it an invalid value then reset

        double max_r = dim0/2;
        if (max_r > dim1/2) max_r = dim1/2;
        if (!b_est_in_xyplaneonly)
        {
            if (max_r > (dim2*zthickness)/2) max_r = (dim2*zthickness)/2;
        }
        //max_r = bound_r; //unused as of now (comment added by PHC, 2010-Dec-21)

        double total_num, background_num;
        double ir;
        end: for (ir=1; ir<=max_r; ir++)
        {
            total_num = background_num = 0;

            double dz, dy, dx;
            double zlower = -ir/zthickness, zupper = +ir/zthickness;
            if (b_est_in_xyplaneonly)
                zlower = zupper = 0;
            for (dz= zlower; dz <= zupper; ++dz)
                for (dy= -ir; dy <= +ir; ++dy)
                    for (dx= -ir; dx <= +ir; ++dx)
                    {
                        total_num++;

                        double r = Math.sqrt(dx*dx + dy*dy + dz*dz);
                        if (r>ir-1 && r<=ir)
                        {
                            int i = (int) (x+dx);	if (i<0 || i>=dim0) break end;
                            int j = (int) (y+dy);	if (j<0 || j>=dim1) break end;
                            int k = (int) (z+dz);	if (k<0 || k>=dim2) break end;

                            if (img3d[k][j][i] <= imgTH)
                            {
                                background_num++;

                                if ((background_num/total_num) > 0.001)	break end; //change 0.01 to 0.001 on 100104
                            }
                        }
                    }
        }

        return ir;
    }


    public static void fitPosition(int[][][] img3d, int dim0, int dim1, int dim2, double imgTH, double ir,
                                   float[] xyz,  float[] D, float zthickness) // 090602: add tangent D to remove movement of tangent direction
    {
        if (zthickness<=0) { zthickness=1.0f; System.out.println("Your zthickness value in fitPosition() is invalid. disable it (i.e. reset it to 1) in computation."); }//if it an invalid value then reset

        double s, cx,cy,cz;
        s = cx = cy = cz = 0;

        double r2= ir * ir;
        for (double dz= -ir/zthickness; dz <= +ir/zthickness; ++dz)
        {
            double rtmpz = dz*dz;
            for (double dy= -ir; dy <= +ir; ++dy)
            {
                double rtmpy = rtmpz+dy*dy;
                if (rtmpy>r2)
                    continue;

                for (double dx= -ir; dx <= +ir; ++dx)
                {
                    double rtmpx = rtmpy+dx*dx;
                    if (rtmpx>r2)
                        continue;

                    double r = Math.sqrt(rtmpx);
                    if (r<=ir)
                    {
                        int i = (int) (xyz[0]+dx);	if (i<0 || i>=dim0) continue;
                        int j = (int) (xyz[1]+dy);	if (j<0 || j>=dim1) continue;
                        int k = (int) (xyz[2]+dz);	if (k<0 || k>=dim2) continue;
                        double f = (img3d[k][j][i]);

                        if (f > imgTH)
                        {
                            s += f;
                            cx += f*(xyz[0]+dx);
                            cy += f*(xyz[1]+dy);
                            cz += f*(xyz[2]+dz);
                        }
                    }
                }
            }
        }
        if (s>0)
        {
            cx = cx/s;
            cy = cy/s;
            cz = cz/s;

            if (D.length == 3)
            {
                // make unit vector
                double len = Math.sqrt(D[0]*D[0] + D[1]*D[1] + D[2]*D[2]);
                if (len>0)
                {
                    D[0] /= len;
                    D[1] /= len;
                    D[2] /= len;
                    // displacement
                    cx = cx-xyz[0];
                    cy = cy-xyz[1];
                    cz = cz-xyz[2];
                    double proj = cx*D[0] + cy*D[1] + cz*D[2];
                    // remove movement of tangent direction
                    cx = cx - proj*D[0];
                    cy = cy - proj*D[1];
                    cz = cz - proj*D[2];
                    xyz[0] += (float)cx;
                    xyz[1] += (float)cy;
                    xyz[2] += (float)cz;
                }
            }
            else //by PHC, 2010-12-29
            {
                xyz[0] = (float)cx;
                xyz[1] = (float)cy;
                xyz[2] = (float)cz;
            }
        }
    }

    //#define fitRadius fitRadiusPCA
    // #define fitRadius fitRadiusPercent
    //////////////////////////////////////////////////
    // #define DIFF(diff, mCoord, i, xyz, HW) \
    // { \
    //         diff = 0; \
    //         int kk; \
    //         int N = mCoord.size(); \
    //         for (int k=1;k<=HW;k++) \
    //         { \
    //             kk = i+k; if (kk<0) kk=0; if (kk>N-1) kk=N-1; \
    //             diff += mCoord[kk].xyz; \
    //             kk = i-k; if (kk<0) kk=0; if (kk>N-1) kk=N-1; \
    //             diff -= mCoord[kk].xyz; \
    //         } \
    // }
    //////////////////////////////////////////////////
    public static void DIFF(float[] diff,Vector <V_NeuronSWC_unit>  mCoord,int i,int Hw)
    {
        for(int item=0; item<3; item++){
            diff[item] = 0;
            int kk;
            int N = mCoord.size();
            for (int k=1;k<=Hw;k++)
            {
                kk = i+k;
                if (kk<0) kk=0;
                if (kk>N-1) kk=N-1;
                float tmp = 0;
                if(item == 0){
                    tmp = (float) mCoord.elementAt(kk).x;
                }
                else if(item == 1){
                    tmp = (float) mCoord.elementAt(kk).y;
                }
                else if(item == 2){
                    tmp = (float) mCoord.elementAt(kk).z;
                }
                diff[item] += tmp;
                kk = i-k;
                if (kk<0) kk=0;
                if (kk>N-1) kk=N-1;
                diff[item] -= tmp;
            }
        }
    }

    // #define ITER_POSITION 10
    public static boolean fit_radius_and_position(int[][][] img3d, int dim0, int dim1, int dim2,
                                                  Vector <V_NeuronSWC_unit>  mCoord, boolean b_move_position, float zthickness, boolean b_est_in_xyplaneonly, double vis_threshold)
    //template <class T>
    //boolean fit_radius_and_position(unsigned char ***img3d, int dim0, int dim1, int dim2,
    //							vector <T> & mCoord, boolean b_move_position)
    {
        if (zthickness<=0) { zthickness=1.0f; System.out.println("Your zthickness value in fit_radius_and_position() is invalid. disable it (i.e. reset it to 1) in computation."); }//if it an invalid value then reset

        if (mCoord.size()<2)
            return false;

        double AR = 0;
        for (int i=0; i<mCoord.size()-1; i++)
        {
            float x = (float) mCoord.elementAt(i).x;
            float y = (float) mCoord.elementAt(i).y;
            float z = (float) mCoord.elementAt(i).z;
            float x1 = (float) mCoord.elementAt(i+1).x;
            float y1 = (float) mCoord.elementAt(i+1).y;
            float z1 = (float) mCoord.elementAt(i+1).z;
            AR += Math.sqrt((x-x1)*(x-x1) + (y-y1)*(y-y1) + (z-z1)*(z-z1));
        }
        AR /= mCoord.size()-1; // average distance between nodes

        double imgAve = getImageAveValue(img3d, dim0, dim1, dim2);
        double imgStd = getImageStdValue(img3d, dim0, dim1, dim2);
        double imgTH = imgAve + imgStd; //change to VISIBLE_THRESHOLD 2011-01-21 but the result is not good
        //if (imgTH < vis_threshold) imgTH = vis_threshold; //added by PHC 20121016. consistent with APP2 improvement

        for (int i=0; i<mCoord.size(); i++)
        {
            float[] xyz = new float[]{(float) mCoord.elementAt(i).x,(float) mCoord.elementAt(i).y,(float) mCoord.elementAt(i).z};

            double r;
            if (i==0 || i==mCoord.size()-1) // don't move start && end point
            {
                r = fitRadiusPercent(img3d, dim0, dim1, dim2, imgTH, AR*2, xyz[0], xyz[1], xyz[2], zthickness, b_est_in_xyplaneonly);
            }
            else
            {
                if (! b_move_position)
                {
                    r = fitRadiusPercent(img3d, dim0, dim1, dim2, imgTH, AR*2, xyz[0], xyz[1], xyz[2], zthickness, b_est_in_xyplaneonly);
                }
                else
                {
                    float[] axdir = new float[3];
                    // DIFF(axdir[0], mCoord, i, x, 5);
                    // DIFF(axdir[1], mCoord, i, y, 5);
                    // DIFF(axdir[2], mCoord, i, z, 5);
                    DIFF(axdir,mCoord,i,5);

                    r = AR;
                    for (int j=0; j<ITER_POSITION; j++)
                    {
                        fitPosition(img3d, dim0, dim1, dim2,   0,   r*2, xyz,  axdir, zthickness);
                        r = fitRadiusPercent(img3d, dim0, dim1, dim2, imgTH,  AR*2, xyz[0], xyz[1], xyz[2], zthickness, b_est_in_xyplaneonly);
                    }
                }
            }

            mCoord.elementAt(i).r = r;
            mCoord.elementAt(i).x = xyz[0];
            mCoord.elementAt(i).y = xyz[1];
            mCoord.elementAt(i).z = xyz[2];
        }
        return true;
    }

    public static boolean fixZigzag(int[][][] img3d, int dim0, int dim1, int dim2,
                                                  Vector <V_NeuronSWC_unit>  mCoord, int times) throws Exception{
        Vector<V_NeuronSWC_unit> mCoordOld= new Vector<V_NeuronSWC_unit>();
        for(int t=0; t<times; t++){
            mCoordOld.clear();
            for(int i=0;i<mCoord.size(); i++){
                mCoordOld.add(mCoord.get(i).clone());
            }
            for(int i=1; i<mCoord.size()-1; i++){
                double x = (mCoordOld.get(i-1).x +mCoordOld.get(i+1).x)/2;
                double y = (mCoordOld.get(i-1).y +mCoordOld.get(i+1).y)/2;
                double z = (mCoordOld.get(i-1).z +mCoordOld.get(i+1).z)/2;
                if(x<0) x = 0; if(x>dim0-1) x= dim0-1;
                if(y<0) y = 0; if(y>dim1-1) y= dim1-1;
                if(z<0) z = 0; if(z>dim2-1) z= dim2-1;
                mCoord.get(i).x = x;
                mCoord.get(i).y = y;
                mCoord.get(i).z = z;
            }
        }
        return  true;
    }

    public static boolean smoothCurve(Vector <V_NeuronSWC_unit>  mCoord, int winsize) throws Exception
    {
        System.out.println("---------------------smooth curve----------------------");
        if (winsize < 2) return true;

        int N = mCoord.size();
        int halfwin = winsize / 2;
        Vector<V_NeuronSWC_unit> mC= new Vector<V_NeuronSWC_unit>(); // a copy
        for(int i=0; i<N; i++){
            mC.add(mCoord.get(i).clone());
        }


        for (int i = 1; i < N - 1; i++) // don't move start & end point
        {
            Vector<V_NeuronSWC_unit> winC = new Vector<V_NeuronSWC_unit>();
            Vector<Double> winW = new Vector<Double>();
            winC.clear();
            winW.clear();

            winC.add(mC.get(i));
            winW.add(1.0 + halfwin);
            for (int j = 1; j <= halfwin; j++)
            {
                int k1 = i + j;	if (k1<0) k1 = 0;	if (k1>N - 1) k1 = N - 1;
                int k2 = i - j;	if (k2<0) k2 = 0;	if (k2>N - 1) k2 = N - 1;
                winC.add(mC.get(k1));
                winC.add(mC.get(k2));
                winW.add(1.0 + halfwin - j);
                winW.add(1.0 + halfwin - j);
            }
            //std::cout<<"winC.size = "<<winC.size()<<"\n";

            double s, x, y, z;
            s = x = y = z = 0;
            for (int j = 0; j < winC.size(); j++)
            {
                x += winW.get(j) * winC.get(j).x;
                y += winW.get(j) * winC.get(j).y;
                z += winW.get(j) * winC.get(j).z;
                s += winW.get(j);
            }
            if (s>0)
            {
                x /= s;
                y /= s;
                z /= s;
            }

            mCoord.get(i).x = x; // output
            mCoord.get(i).y = y; // output
            mCoord.get(i).z = z; // output
        }
        return true;
    }

    public boolean swc_to_segments(Vector<MyMarker> inmarkers, Vector<Vector<V_NeuronSWC_unit> > segments)throws Exception
    {
        Map<MyMarker, Integer>  child_num = new HashMap<MyMarker, Integer>();
        Vector<MyMarker> leaf_markers = getLeafMarkers(inmarkers, child_num);
        Map<MyMarker, Integer> node_index = new HashMap<MyMarker, Integer>();
        for(int i = 0; i < inmarkers.size(); i++) {
            node_index.put(inmarkers.get(i),i+1);
        }

        Set<MyMarker> start_markers = new HashSet<MyMarker>();
        for(int i = 0; i < leaf_markers.size(); i++)
        {
            MyMarker start_marker = leaf_markers.get(i);
            while(!start_markers.contains(start_marker))
            {
                start_markers.add(start_marker);
                Vector<V_NeuronSWC_unit> segment = new Vector<V_NeuronSWC_unit>();
                MyMarker p = start_marker;
                do
                {
                    V_NeuronSWC_unit m2 = new V_NeuronSWC_unit();
                    m2.x = p.x;
                    m2.y = p.y;
                    m2.z = p.z;
                    m2.r = p.radius;
                    m2.nchild = child_num.get(p);
                    m2.n = node_index.get(p);
                    m2.parent = (p.parent != null) ? node_index.get(p.parent) : -1;
                    segment.add(m2.clone());
                    p = p.parent;
                }while(p != null && child_num.get(p) == 1);
                if(true && p != null) // add branch or root node to segment
                {
                    V_NeuronSWC_unit m2 = new V_NeuronSWC_unit();
                    m2.x = p.x;
                    m2.y = p.y;
                    m2.z = p.z;
                    m2.r = p.radius;
                    m2.nchild = child_num.get(p);
                    m2.n = node_index.get(p);
                    m2.parent = (p.parent != null) ? node_index.get(p.parent) : -1;
                    segment.add(m2.clone());
                }
                segments.add(segment);
                if(p != null) start_marker = p;
                else break;
            }
        }
        return true;
    }

    public String find_shortest_path_graghing_FM(int[][][] img3d, int dim0, int dim1, int dim2, //image
                                                 float zthickness, // z-thickness for weighted edge
                                                 //final int box[6],  //bounding box
                                                 int bx0, int by0, int bz0, int bx1, int by1, int bz1, //bounding box (ROI)
                                                 float x0, float y0, float z0,       // start node
                                                 int n_end_nodes,                    // n_end_nodes == (0 for shortest path tree) (1 for shortest path) (n-1 for n pair path)
                                                 float[] x1, float[] y1, float[] z1,    // all end nodes
                                                 Vector< Vector<V_NeuronSWC_unit> > mmUnit, // change from Coord3D for shortest path tree
                                                 ParaShortestPath  para)throws Exception{
        boolean is_gsdt = false;
        double bkg_thres = para.imgTH;
        double length_thresh = 2.0;
        int cnn_type = 2; // default connection type 2
        int channel = 0;

//        cout<<"bkg_thresh = "<<bkg_thresh<<endl;
//        cout<<"length_thresh = "<<length_thresh<<endl;
//        cout<<"is_gsdt = "<<is_gsdt<<endl;
//        cout<<"cnn_type = "<<cnn_type<<endl;
//        cout<<"channel = "<<channel<<endl;

//        unsigned char * indata1d = img3d[0][0];
        int[]  in_sz = new int[]{dim0, dim1, dim2, 1};

        Vector<MyMarker> inmarkers = new Vector<MyMarker>();
        Vector<MyMarker> outtree = new Vector<MyMarker>();;
        Vector<MyMarker> target = new Vector<MyMarker>();;

        inmarkers.add(new MyMarker(x0, y0, z0));
        for(int i = 0; i < n_end_nodes; i++)
        {
            target.add(new MyMarker(x1[i], y1[i], z1[i]));
        }


        if(is_gsdt)
        {
            float[][][] phi = null;
            System.out.println("processing fastmarching distance transformation ...");
            fastmarching_dt(img3d, phi, in_sz, cnn_type, (int) bkg_thres);
            System.out.println("constructing fastmarching tree ...");
            fastmarching_tree(inmarkers.get(0), target, phi, outtree, in_sz, cnn_type,bkg_thres,true);

        }
        else
        {
            System.out.println("constructing fastmarching tree ...");
            fastmarching_tree(inmarkers.get(0), target, img3d, outtree, in_sz, cnn_type,bkg_thres,true);
            System.out.println("constructing fastmarching tree end...");
        }
//        cout<<"======================================="<<endl;

        swc_to_segments(outtree, mmUnit);
        //rearrange_and_remove_labeled_deletion_nodes_mmUnit(mmUnit);
        //int nSegsTrace = mergeback_mmunits_to_neuron_path(pp.size(), mmUnit, tracedNeuron);
        System.out.println("===== Finish FM tree construction ====");
        return "";
    }


}
