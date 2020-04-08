package com.tracingfunc.gd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

//import javafx.util.Pair;

import static java.lang.Math.sqrt;

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
    String find_shortest_path_graphimg(int[][][] img3d, long dim0, long dim1, long dim2, //image
                                       float zthickness, // z-thickness for weighted edge
                                       //final long box[6],  //bounding box
                                       long bx0, long by0, long bz0, long bx1, long by1, long bz1, //bounding box (ROI)
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

        int dowsample_method = para.downsample_method; //0 for average, 1 for max

        if (min_step<1)       min_step =1;
        if (smooth_winsize<1) smooth_winsize =1;

        //bounding box volume
        long xmin = bx0, xmax = bx1, ymin = by0, ymax = by1, zmin = bz0, zmax = bz1;

        long nx=((xmax-xmin)/min_step)+1, 	xstep=min_step,
                ny=((ymax-ymin)/min_step)+1, 	ystep=min_step,
                nz=((zmax-zmin)/min_step)+1, 	zstep=min_step;

        long num_edge_table = (edge_select==0)? 3:13; // exclude/include diagonal-edge

        System.out.printf("valid bounding (%d %d %d)--(%d %d %d) ......  \n", xmin,ymin,zmin, xmax,ymax,zmax);
        System.out.printf("%d x %d x %d nodes, step = %d, connect = %d \n", nx, ny, nz, min_step, num_edge_table*2);

        long num_nodes = nx*ny*nz;
        int i,j,k,n,m;

        // ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // #define NODE_FROM_XYZ(x,y,z) 	(long((z+.5)-zmin)/zstep*ny*nx + long((y+.5)-ymin)/ystep*nx + long((x+.5)-xmin)/xstep)
        // #define NODE_TO_XYZ(j, x,y,z) \
        // { \
        //     z = (j)/(nx*ny); 		y = ((j)-long(z)*nx*ny)/nx; 	x = ((j)-long(z)*nx*ny-long(y)*nx); \
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

        long start_nodeind;
        long[] end_nodeind = new long[n_end_nodes];
//        if (n_end_nodes>0) //101210 PHC
//            end_nodeind = new long [n_end_nodes]; //100520, PHC
//        else
//            System.out.println("**************** n_end_nodes is 0, and thus do not need to allocate memory. *********************");


        if (x0<xmin-dd || x0>xmax+dd || y0<ymin-dd || y0>ymax+dd || z0<zmin-dd || z0>zmax+dd)
        {
            System.out.println(s_error="Error happens: start_node out of bound! ");
            return s_error;
        }
        // start_nodeind = NODE_FROM_XYZ(x0,y0,z0);
        start_nodeind = (long)((z0+0.5)-zmin)/zstep*ny*nx + (long)((y0+0.5)-ymin)/ystep*nx + (long)((x0+0.5)-xmin)/xstep;
        if (start_nodeind<0 || start_nodeind>=num_nodes)
        {
            System.out.println(s_error="Error happens: start_node index out of range! ");
            // if (end_nodeind) {delete []end_nodeind; end_nodeind=0;} //100520, by PHC
            return s_error;
        }

        long n_end_outbound = 0;
        for (i=0; i<n_end_nodes; i++)
        {
            if (x1[i]<xmin-dd || x1[i]>xmax+dd || y1[i]<ymin-dd || y1[i]>ymax+dd || z1[i]<zmin-dd || z1[i]>zmax+dd)
            {
                end_nodeind[i] = -1;
                System.out.printf("Warning: end_node[%d] out of bound! \n", i);
                n_end_outbound ++;
                continue; //ignore this end_node out of ROI
            }
            end_nodeind[i]   = (long)((z1[i]+0.5)-zmin)/zstep*ny*nx + (long)((y1[i]+0.5)-ymin)/ystep*nx + (long)((x1[i]+0.5)-xmin)/xstep;
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


        // #define _creating_graph_

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        //switch back to new[] from std::vector for *** glibc detected *** ??? on Linux
        // std::vector<Node> 	plist(num_nodes);		for (i=0;i<num_nodes;i++) plist[i]=i;
        // std::vector<Edge> 	edge_array;				edge_array.clear();
        // std::vector<Weight>	weights;				weights.clear();

        int[] plist = new int[(int)num_nodes];                                                  for (i=0;i<num_nodes;i++) plist[i]=i;
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
                                    (int) xstep, (int) ystep, (int) (zstep/zthickness)); //zthickness
                            vb = getBlockAveValue(img3d, dim0, dim1, dim2, (xmin+(ii1)*xstep),(ymin+(jj1)*ystep),(zmin+(kk1)*zstep),
                                    (int) xstep, (int) ystep, (int) (zstep/zthickness)); //zthickness
                        }else if(dowsample_method == 1)
                        {
                            va = getBlockMaxValue(img3d, dim0, dim1, dim2, (xmin+(ii)*xstep),(ymin+(jj)*ystep),(zmin+(kk)*zstep),
                                    (int) xstep, (int) ystep, (int) (zstep/zthickness)); //zthickness
                            vb = getBlockMaxValue(img3d, dim0, dim1, dim2, (xmin+(ii1)*xstep),(ymin+(jj1)*ystep),(zmin+(kk1)*zstep),
                                    (int) xstep, (int) ystep, (int) (zstep/zthickness)); //zthickness
                        }

                        if (va<=imgTH || vb<=imgTH)
                            continue; //skip background node link

                        Pair<Integer,Integer> e = new Pair<Integer,Integer>(node_a, node_b);
                        edge_array.add(e);

                        Float w = (float) edge_weight_func(it, va,vb, 255);

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
        System.out.printf(" minw=%g maxw=%g \n", minw,maxw);
        System.out.println(" graph defined! ");

        if (n != edge_array.size())
        {
            System.out.println(s_error="The number of edges is not consistent ");
            // if (end_nodeind) {delete []end_nodeind; end_nodeind=0;} //100520, by PHC
            return s_error;
        }
        long num_edges = n; // back to undirectEdge for less memory consumption

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
                System.out.println("bgl_shortest_path() ");
                // s_error = bgl_shortest_path(edge_array[0], num_edges, &weights[0], num_nodes, start_nodeind, &plist[0]);
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

        for(i = 0; i<plist.length; i++){
            System.out.println(plist[i]);
        }


        // output node coordinates of the shortest path
        mmUnit.clear();
        long nexist = 0;

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
                        cc.z = (j)/(nx*ny); 		cc.y = ((j)-(long)(cc.z)*nx*ny)/nx; 	cc.x = ((j)-(long)(cc.z)*nx*ny-(long)(cc.y)*nx);
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
                // long i = index_map[parent]; // this is very fast
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
                    long nprune=0;
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
                            va = getBlockAveValue(img3d, dim0, dim1, dim2, (long)mUnit.elementAt(j).x,(long)mUnit.elementAt(j).y,(long) mUnit.elementAt(j).z,
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
//                                // long i = index_map[parent];
//                                long i = index_map.get(parent);
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
                    long jj = j;
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
                        cc.z = (j) / (nx * ny);
                        cc.y = ((j) - (long) (cc.z) * nx * ny) / nx;
                        cc.x = ((j) - (long) (cc.z) * nx * ny - (long) (cc.y) * nx);
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

    public static double getBlockMaxValue(int[][][] img3d, long dim0, long dim1, long dim2,
                                          long x0, long y0, long z0,
                                          int xstep, int ystep, int zstep)
    {
        if (img3d == null || img3d.length == 0 || dim0<=0 || dim1<=0 || dim2<=0 ||
                x0<0 || x0>=dim0 || y0<0 || y0>=dim1 || z0<0 || z0>=dim2)
            return 0;

        double xsteph=Math.abs(xstep)/2, ysteph=Math.abs(ystep)/2, zsteph=Math.abs(zstep)/2;
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

    public static double getBlockAveValue(int[][][] img3d, long dim0, long dim1, long dim2,
                                          long x0, long y0, long z0,
                                          int xstep, int ystep, int zstep)
    {
        if (img3d == null || img3d.length == 0 || dim0<=0 || dim1<=0 || dim2<=0 ||
                x0<0 || x0>=dim0 || y0<0 || y0>=dim1 || z0<0 || z0>=dim2)
            return 0;

        double xsteph=Math.abs(xstep)/2, ysteph=Math.abs(ystep)/2, zsteph=Math.abs(zstep)/2;
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




    public static boolean setBlockAveValue(int[][][] img3d, long dim0, long dim1, long dim2,
                                           long x0, long y0, long z0,
                                           int xstep, int ystep, int zstep, int target_val)
    {
        if (img3d == null || img3d.length == 0 || dim0<=0 || dim1<=0 || dim2<=0 ||
                x0<0 || x0>=dim0 || y0<0 || y0>=dim1 || z0<0 || z0>=dim2)
            return false;

        double xsteph=Math.abs(xstep)/2, ysteph=Math.abs(ystep)/2, zsteph=Math.abs(zstep)/2;
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


    public static double getBlockStdValue(int[][][] img3d, long dim0, long dim1, long dim2,
                                          long x0, long y0, long z0,
                                          int xstep, int ystep, int zstep)
    {
        if (img3d == null || img3d.length == 0 || dim0<=0 || dim1<=0 || dim2<=0 ||
                x0<0 || x0>=dim0 || y0<0 || y0>=dim1 || z0<0 || z0>=dim2)
            return 0;

        double blockAve = getBlockAveValue(img3d, dim0, dim1, dim2,
                x0, y0, z0,
                xstep, ystep, zstep);

        double xsteph=Math.abs(xstep)/2, ysteph=Math.abs(ystep)/2, zsteph=Math.abs(zstep)/2;
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

    public static double getImageMaxValue(int[][][] img3d, long dim0, long dim1, long dim2)
    {
        long x0 = dim0/2;
        long y0 = dim1/2;
        long z0 = dim2/2;
        int xstep = (int) dim0;
        int ystep = (int) dim1;
        int zstep = (int) dim2;
        return getBlockMaxValue(img3d, dim0, dim1, dim2, x0, y0, z0, xstep, ystep, zstep);
    }
    public static double getImageAveValue(int[][][] img3d, long dim0, long dim1, long dim2)
    {
        long x0 = dim0/2;
        long y0 = dim1/2;
        long z0 = dim2/2;
        int xstep = (int) dim0;
        int ystep = (int) dim1;
        int zstep = (int) dim2;
        return getBlockAveValue(img3d, dim0, dim1, dim2, x0, y0, z0, xstep, ystep, zstep);
    }


    public static double getImageStdValue(int[][][] img3d, long dim0, long dim1, long dim2)
    {
        long x0 = dim0/2;
        long y0 = dim1/2;
        long z0 = dim2/2;
        int xstep = (int) dim0;
        int ystep = (int) dim1;
        int zstep = (int) dim2;
        return getBlockStdValue(img3d, dim0, dim1, dim2, x0, y0, z0, xstep, ystep, zstep);
    }

    public static double metric_func(double v, double max_v)
    {
        double tmpv = 1-v/max_v;
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

        // char* copyEdgeSparseData2Adj(Edge *edge_array, long n_edges, Weight *weights, long n_nodes,
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

        p.dosearch(start_nodeind); //set root as the first node

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
            System.out.println(s_error="Invalid parameters to phc_shortest_path(). do nothing");
            return s_error;
        }

        List<Vertex> vertexs = new ArrayList<Vertex>();
        for(int i=0; i<n_nodes; i++){
            Vertex v;
            if(i==start_nodeind){
                v = new Vertex(i,0);
                v.setParent(-1);
            }
            v = new Vertex(i);
            vertexs.add(v);
        }
        Gragh g = new Gragh(vertexs,edge_array,weights);
        g.search(start_nodeind);
        List<Vertex> vs = g.getVertexs();
        for(int i=0; i<vs.size(); i++){
            plist[i] = vs.get(i).getParent();
        }

        return s_error;
    }

    public static String copyEdgeSparseData2Adj(Vector<Pair<Integer,Integer>> edge_array, int n_edges, Vector<Float> weights, long n_nodes,
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
            long root_id=-1;
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
                    root_id = (long)(mUnit.elementAt(j).n);
                    System.out.printf("== [segment %d] ================== nchild of root [%d, id=%d] = %d\n",
                            k, j, (long)(mUnit.elementAt(j).n), (long)(mUnit.elementAt(j).nchild));
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


//        Vector<long> path_start(npath); // 0-based index
//        Vector<long> path_try(npath);   // 0-based index
        long[] path_start = new long[npath];
        long[] path_try = new long[npath];
        for (int i=0; i<npath; i++)
            path_try[i] = path_start[i] = mmUnit.elementAt(i).size()-1;

        long nexist = 0; // count output node index
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

            long jj;
            V_NeuronSWC_unit same_node = new V_NeuronSWC_unit();
            int n_same_node;
            int ipath = -1;
            lastn_same_node = 0;

            for (long j=0; true; j++) //searching same_segment along 1 path with other paths
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

            }//j -- along 1 branch
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



}
