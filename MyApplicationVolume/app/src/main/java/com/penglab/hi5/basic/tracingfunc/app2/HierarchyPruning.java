package com.penglab.hi5.basic.tracingfunc.app2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.penglab.hi5.basic.tracingfunc.gd.Pair;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import static com.penglab.hi5.basic.tracingfunc.app2.MyMarker.dist;
import static com.penglab.hi5.basic.tracingfunc.app2.MyMarker.smooth_radius;

public class HierarchyPruning {
    public static int INTENSITY_DISTANCE_METHOD = 0;

    public static Vector<MyMarker> getLeafMarkers(Vector<MyMarker> inmarkers){
        Set<MyMarker> par_marker = new HashSet<MyMarker>();
        Vector<MyMarker> leaf_markers = new Vector<MyMarker>();
        for(int i=0; i<inmarkers.size(); i++){
            MyMarker marker = inmarkers.elementAt(i);
            if(marker.parent != null){
                par_marker.add(marker.parent);
            }
        }
        for(int i=0; i<inmarkers.size(); i++){
            if(!par_marker.contains(inmarkers.elementAt(i))){
                leaf_markers.add(inmarkers.elementAt(i));
            }
        }
        return leaf_markers;
    }

    public static Vector<MyMarker> getLeafMarkers(Vector<MyMarker> inmarkers, Map<MyMarker,Integer> childs_num){
        for(int i = 0; i < inmarkers.size(); i++)
            childs_num.put(inmarkers.elementAt(i),i);
        int[] cnum = new int[inmarkers.size()];
        Vector<MyMarker> leaf_markers = new Vector<MyMarker>();
        for(int i = 0; i < inmarkers.size(); i++)
        {
            MyMarker  marker = inmarkers.elementAt(i);
            MyMarker  parent = marker.parent;
            if(parent!=null) cnum[childs_num.get(parent)]++;
        }
        childs_num.clear();
        for(int i = 0; i < inmarkers.size(); i++)
        {
            childs_num.put(inmarkers.elementAt(i),cnum[i]);
            if(cnum[i] == 0) leaf_markers.add(inmarkers.elementAt(i));
        }
        return leaf_markers;
    }


    public static boolean swcToSegs(Vector<MyMarker> inswc, Vector<HierarchySegment> segs,
                                    int length_method, int[][][] inimg, int[] sz){
        if(length_method == INTENSITY_DISTANCE_METHOD && (inimg == null || sz.length != 3 || sz[0]==0 || sz[1]==0 || sz[2]==0)){
            System.out.println("need image input for INTENSITY_DISTANCE_METHOD ");
            return false;
        }
        // 1. calc distance for every nodes
        System.out.println("calc distance for every nodes");
        int tol_num = inswc.size();
        Map<MyMarker, Integer> swc_map = new HashMap<MyMarker,Integer>();
        for(int i = 0; i < tol_num; i++)
            swc_map.put(inswc.elementAt(i),i);

        Vector<MyMarker> leaf_markers = new Vector<MyMarker>();
        //GET_LEAF_MARKERS(leaf_markers, inswc);
        int[] childs_num = new int[tol_num];
        {
            for(int i = 0; i < tol_num; i++)
                childs_num[i]=0;
            for(int m1 = 0; m1 < tol_num; m1++)
            {
                if(inswc.elementAt(m1).parent == null) continue;
                int m2 = swc_map.get(inswc.elementAt(m1).parent);
                childs_num[m2]++;
            }
            for(int i = 0; i < tol_num; i++) if(childs_num[i] == 0) leaf_markers.add(inswc.elementAt(i));
        }
        int leaf_num = leaf_markers.size();
        System.out.println("-----------------------end cal-------------------");

        int szx = sz[0], szy = sz[1], szz = sz[2];
        int total_sz = szx*szy*szz;

        double[] topo_dists = new double[tol_num]; // furthest leaf distance for each tree node
        MyMarker[] topo_leafs = new MyMarker[tol_num];

        for(int i=0; i<tol_num; i++) topo_dists[i] = 0;

        for(int i = 0; i < leaf_num; i++)
        {
            MyMarker leaf_marker = leaf_markers.elementAt(i);
            MyMarker child_node = leaf_markers.elementAt(i);
            MyMarker parent_node = child_node.parent;
            int cid = swc_map.get(child_node);
            topo_leafs[cid] = leaf_marker;
            topo_dists[cid] = (length_method == INTENSITY_DISTANCE_METHOD) ?
                    (double) inimg[(int) (leaf_marker.z+0.5)][(int) (leaf_marker.y+0.5)][(int) (leaf_marker.x+0.5)]/255.0 : 0;
            while(parent_node!=null)
            {
                int pid = swc_map.get(parent_node);
                double tmp_dst = (length_method == INTENSITY_DISTANCE_METHOD) ?
                        ((double) inimg[(int) (parent_node.z+0.5)][(int) (parent_node.y+0.5)][(int) (parent_node.x+0.5)]/255.0 + topo_dists[cid]) :
                        (dist(child_node, parent_node) + topo_dists[cid]);
                if(tmp_dst >= topo_dists[pid])   // >= instead of >
                {
                    topo_dists[pid] = tmp_dst;
                    topo_leafs[pid] = topo_leafs[cid];
                }
                else break;
                child_node = parent_node;
                cid = pid;
                parent_node = parent_node.parent;
            }
        }
        // 2. create Hierarchy Segments
        System.out.println("---------------create Hierarchy Segments----------------------");
        segs.clear();
        Map<MyMarker, Integer> leaf_ind_map = new HashMap<MyMarker, Integer>();
        for(int i = 0; i<leaf_num; i++){
//            segs.add(new HierarchySegment());
            leaf_ind_map.put(leaf_markers.elementAt(i),i);
        }

        for(int i = 0; i < leaf_num; i++)
        {
            MyMarker  leaf_marker = leaf_markers.elementAt(i);
            MyMarker  root_marker = leaf_marker;
            MyMarker  root_parent = root_marker.parent;
            int level = 1;
            while(root_parent != null && topo_leafs[swc_map.get(root_parent)].equals(leaf_marker))
            {
                if(childs_num[swc_map.get(root_marker)] >= 2) level++;
                root_marker = root_parent;
                root_parent = root_marker.parent;
            }

            double dst = topo_dists[swc_map.get(root_marker)];
            HierarchySegment seg = new HierarchySegment(leaf_marker, root_marker, dst, level);
            segs.add(seg);
        }
        for(int i=0; i<segs.size(); i++){
            MyMarker root_parent = segs.get(i).root_marker.parent;
            if(root_parent == null)
                segs.get(i).parent = null;
            else {
                MyMarker leaf_marker2 = topo_leafs[swc_map.get(root_parent)];
                int leaf_ind2 = leaf_ind_map.get(leaf_marker2);
                segs.get(i).parent = segs.get(leaf_ind2);
            }
        }
        System.out.println("-------------------------creat end-------------------------");
        return  true;
    }

    // 1. will change the type of each segment
    // swc_type : 0 for length heatmap, 1 for level heatmap
    public static boolean segsToSwc(Vector<HierarchySegment> segs,Vector<MyMarker> outmarkers, int swc_type){
        if(segs.isEmpty()) return false;

        double min_dst = segs.elementAt(0).length, max_dst = min_dst;
        double min_level = segs.elementAt(0).level, max_level = min_level;
        for(int i = 0; i < segs.size(); i++)
        {
            double dst = segs.elementAt(i).length;
            min_dst = Math.min(min_dst, dst);
            max_dst = Math.max(max_dst, dst);

            int level = segs.elementAt(i).level;
            min_level = Math.min(min_level, level);
            max_level = Math.max(max_level, level);
        }
        max_level = Math.min(max_level, 20);                         // todo1

        System.out.println("min_dst = "+min_dst);
        System.out.println("max_dst = "+max_dst);
        System.out.println("min_level = "+min_level);
        System.out.println("max_level = "+max_level);


        max_dst -= min_dst; if(max_dst == 0.0) max_dst = 0.0000001;
        max_level -= min_level; if(max_level == 0) max_level = 1.0;
//        boolean rget = false;
        for(int i = 0; i < segs.size(); i++)
        {
//            if(!rget&&segs.get(i).root_marker.parent == null){
//                System.out.println(i+" am parent---------------------------");
//                MyMarker root = segs.get(i).root_marker;
//                outmarkers.add(root);
//                rget = true;
//            }
            double dst = segs.elementAt(i).length;
            int level = (int) Math.min(segs.elementAt(i).level, max_level);    // todo1
            int color_id = (int) ((swc_type == 0) ? (dst - min_dst) / max_dst * 254.0 + 20.5 : (level - min_level)/max_level * 254.0 + 20.5);
            Vector<MyMarker> tmp_markers = new Vector<MyMarker>();
            segs.elementAt(i).getMarkers(tmp_markers);
            for(int j = 0; j < tmp_markers.size() ; j++)
            {
                tmp_markers.elementAt(j).type= color_id;
                outmarkers.add(tmp_markers.elementAt(j));
            }
        }
        return true;
    }

    public static boolean hierarchy_prune(Vector<MyMarker> inswc, Vector<MyMarker> outswc, int[][][] inimg, int[] sz, double length_thresh){
        Vector<HierarchySegment> segs = new Vector<HierarchySegment>();
        swcToSegs(inswc,segs,INTENSITY_DISTANCE_METHOD,inimg,sz);
        Vector<HierarchySegment> filter_segs = new Vector<HierarchySegment>();
        for(int i=0; i<segs.size(); i++){
            if(segs.elementAt(i).length>=length_thresh){
                filter_segs.add(segs.elementAt(i));
            }
        }
        segsToSwc(filter_segs,outswc,0);
        return true;
    }

    // hierarchy coverage pruning
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean happ(Vector<MyMarker> inswc, Vector<MyMarker> outswc, int[][][] inimg, int[] sz, double bkg_thresh,
                               double length_thresh, double SR_ratio, boolean is_leaf_prune, boolean is_smooth) throws Exception{
        double T_max = (111<<1);

        Map<MyMarker,Integer> child_num = new HashMap<MyMarker, Integer>();
        getLeafMarkers(inswc,child_num);

        Vector<HierarchySegment> segs = new Vector<HierarchySegment>();
        System.out.println("Construct hierarchical segments");
        swcToSegs(inswc,segs,INTENSITY_DISTANCE_METHOD,inimg,sz);



        Vector<HierarchySegment> filter_segs = new Vector<HierarchySegment>();
        for(int i=0; i<segs.size(); i++){
            if(segs.elementAt(i).length>=length_thresh){
                filter_segs.add(segs.elementAt(i));
            }
        }
        System.out.println("pruned by length_thresh (segment number) : "+segs.size()+" - "+(segs.size()-filter_segs.size())+" = "+filter_segs.size());


//        Map<Double,HierarchySegment> seg_dist_map;
        Queue<Pair<Double,HierarchySegment>> seg_dist_map = new PriorityQueue<Pair<Double,HierarchySegment>>(new Comparator<Pair<Double, HierarchySegment>>() {
            @Override
            public int compare(Pair<Double, HierarchySegment> o1, Pair<Double, HierarchySegment> o2) {
                return o2.getKey()-o1.getKey()>0?1:-1;
            }
        });
        for(int i=0; i<filter_segs.size(); i++){
            double dst = filter_segs.elementAt(i).length;
            seg_dist_map.add(new Pair<Double, HierarchySegment>(dst,filter_segs.elementAt(i)));
        }

        if(true) // dark nodes pruning
        {
            int dark_num_pruned = 1;
            int iteration = 1;
            boolean[] is_pruneable = new boolean[filter_segs.size()];
            for(int i=0; i<filter_segs.size(); i++) is_pruneable[i] = false;
            System.out.println("===== Perform dark node pruning =====");
            while(dark_num_pruned > 0)
            {
                dark_num_pruned = 0;
                for(int i = 0; i < filter_segs.size(); i++)
                {
                    if(iteration > 1 && !is_pruneable[i]) continue;
                    HierarchySegment seg = filter_segs.elementAt(i);
                    MyMarker leaf_marker = seg.leaf_marker;
                    MyMarker root_marker = seg.root_marker;
                    if(leaf_marker.equals(root_marker)) continue;
                    if(inimg[(int) (leaf_marker.z+0.5)][(int) (leaf_marker.y+0.5)][(int) (leaf_marker.x+0.5)] <= bkg_thresh)
                    {
                        seg.leaf_marker = leaf_marker.parent;
                        dark_num_pruned ++;
                        is_pruneable[i] = true;
                    }
                    else is_pruneable[i] = false;
                }
                iteration++;
                System.out.println("\t iteration ["+iteration+"] "+dark_num_pruned+" dark node pruned");
            }
        }

        if(true) // dark segment pruning
        {
            Set<Integer> delete_index_set = new HashSet<Integer>();
            for(int i = 0; i < filter_segs.size(); i++)
            {
                HierarchySegment  seg = filter_segs.elementAt(i);
                MyMarker leaf_marker = seg.leaf_marker;
                MyMarker root_marker = seg.root_marker;
                if(leaf_marker.equals(root_marker)) {delete_index_set.add(i); continue;}
                MyMarker p = leaf_marker;
                double sum_int = 0.0, tol_num = 0.0, dark_num = 0.0;
                while(true)
                {
                    double intensity = inimg[(int) (p.z+0.5)][(int) (p.y+0.5)][(int) (p.x+0.5)];
                    sum_int += intensity;
                    tol_num++;
                    if(intensity <= bkg_thresh) dark_num++;
                    if(p.equals(root_marker)) break;
                    p = p.parent;
                }
                if(sum_int/tol_num <= bkg_thresh ||
                        dark_num/tol_num >= 0.2)
                    delete_index_set.add(i);
            }
            Vector<HierarchySegment> tmp_segs = new Vector<HierarchySegment>();
            for(int i = 0; i < filter_segs.size(); i++)
            {
                HierarchySegment seg = filter_segs.elementAt(i);
                if(!delete_index_set.contains(i)) tmp_segs.add(seg);
            }
            System.out.println("\t"+delete_index_set.size()+" dark segments are deleted");
            filter_segs.clear();
            for(int i=0; i<tmp_segs.size(); i++){
                filter_segs.add(tmp_segs.elementAt(i));
            }
        }

        // calculate radius for every node
        {
            System.out.println("Calculating radius for every node");
            int[] in_sz = new int[]{sz[0], sz[1], sz[2], 1};
            for(int i = 0; i < filter_segs.size(); i++)
            {
                HierarchySegment  seg = filter_segs.elementAt(i);
                MyMarker  leaf_marker = seg.leaf_marker;
                MyMarker  root_marker = seg.root_marker;
                MyMarker  p = leaf_marker;
                while(true)
                {
                    double real_thres = 40; if (real_thres<bkg_thresh) real_thres = bkg_thresh; //by PHC 20121012

                    p.radius = p.markerRadius(inimg, in_sz, real_thres,1,true);
                    if(p.equals(root_marker)) break;
                    p = p.parent;
                }
            }
        }


        if(true) // hierarchy coverage order pruning
        {
            System.out.println("Perform hierarchical pruning");
//            T * tmpimg1d = new T[tol_sz]; memcpy(tmpimg1d, inimg1d, tol_sz * sizeof(T));
            int[][][] tmpimg = new int[sz[2]][sz[1]][sz[0]];
            for(int k=0;k<sz[2];k++) {
                for(int j=0; j<sz[1]; j++)
                    if (sz[0] >= 0) System.arraycopy(inimg[k][j], 0, tmpimg[k][j], 0, sz[0]);
            }
            int[] tmp_sz = new int[]{sz[0], sz[1], sz[2], 1};

//            multimap<double, HierarchySegment*>::reverse_iterator it = seg_dist_map.rbegin();
            //MyMarker * soma = (*it).second->root_marker;  // 2012/07 Hang, no need to consider soma
            //cout<<"soma ("<<soma->x<<","<<soma->y<<","<<soma->z<<") radius = "<<soma->radius<<" value = "<<(int)inimg1d[soma->ind(sz0, sz01)]<<endl;
            filter_segs.clear();
            Set<HierarchySegment> visited_segs = new HashSet<HierarchySegment>();
            double tol_sum_sig = 0.0, tol_sum_rdc = 0.0;
            while(!seg_dist_map.isEmpty())
            {
                HierarchySegment seg = seg_dist_map.poll().getValue();
                if(seg.parent != null && !visited_segs.contains(seg.parent)){continue;}

                MyMarker leaf_marker = seg.leaf_marker;
                MyMarker root_marker = seg.root_marker;
                double SR_RATIO = SR_ratio;     // the soma area will use different SR_ratio
                //if(dist(*soma, *root_marker) <= soma->radius) SR_RATIO = 1.0;

                double sum_sig = 0;
                double sum_rdc = 0;

                if(true)
                {
                    MyMarker p = leaf_marker;
                    while(true)
                    {
                        if(tmpimg[(int) (p.z+0.5)][(int) (p.y+0.5)][(int) (p.x+0.5)] == 0) {
                            sum_rdc += inimg[(int) (p.z+0.5)][(int) (p.y+0.5)][(int) (p.x+0.5)];
                        }
                        else
                        {
//                            if(0) sum_sig += inimg1d[p->ind(sz0, sz01)]; // simple stragety
                            if(true)// if sphere overlap
                            {
                                int r = (int) p.radius;
                                int x1 = (int) (p.x + 0.5);
                                int y1 = (int) (p.y + 0.5);
                                int z1 = (int) (p.z + 0.5);
                                double sum_sphere_size = 0.0;
                                double sum_delete_size = 0.0;
                                for(int kk = -r; kk <= r; kk++)
                                {
                                    int z2 = z1 + kk;
                                    if(z2 < 0 || z2 >= sz[2]) continue;
                                    for(int jj = -r; jj <= r; jj++)
                                    {
                                        int y2 = y1 + jj;
                                        if(y2 < 0 || y2 >= sz[1]) continue;
                                        for(int ii = -r; ii <= r; ii++)
                                        {
                                            int x2 = x1 + ii;
                                            if(x2 < 0 || x2 >= sz[0]) continue;
                                            if(kk*kk + jj*jj + ii*ii > r*r) continue;
                                            sum_sphere_size++;
                                            if(tmpimg[z2][y2][x2] != inimg[z2][y2][x2]){sum_delete_size ++;}
                                        }
                                    }
                                }
                                // the intersection between two sphere with equal size and distance = R is 5/16 (0.3125)
                                // sum_delete_size/sum_sphere_size should be < 5/16 for outsize points
                                if(sum_sphere_size > 0 && sum_delete_size/sum_sphere_size > 0.1)
                                {
                                    sum_rdc += inimg[(int) (p.z+0.5)][(int) (p.y+0.5)][(int) (p.x+0.5)];
                                }
                                else sum_sig += inimg[(int) (p.z+0.5)][(int) (p.y+0.5)][(int) (p.x+0.5)];
                            }
                        }
                        if(p.equals(root_marker)) break;
                        p = p.parent;
                    }
                }

                //double sum_sig = total_sum_int - sum_rdc;
                if(seg.parent == null || sum_rdc == 0.0 || (sum_sig/sum_rdc >= SR_RATIO && sum_sig >= 1.0 * T_max))
                {
                    tol_sum_sig += sum_sig;
                    tol_sum_rdc += sum_rdc;

                    Vector<MyMarker> seg_markers = new Vector<MyMarker>();
                    MyMarker p = leaf_marker;
                    while(true){if(tmpimg[(int) (p.z+0.5)][(int) (p.y+0.5)][(int) (p.x+0.5)] != 0) seg_markers.add(p); if(p == root_marker) break; p = p.parent;}
                    //reverse(seg_markers.begin(), seg_markers.end()); // need to reverse if resampling

                    for(int m = 0; m < seg_markers.size(); m++)
                    {
                        p = seg_markers.elementAt(m);

                        int r = (int) p.radius;
                        if(r > 0)// && tmpimg1d[p->ind(sz0, sz01)] != 0)
                        {
                            double rr = r * r;
                            int x = (int) (p.x + 0.5);
                            int y = (int) (p.y + 0.5);
                            int z = (int) (p.z + 0.5);
                            for(int kk = -r; kk <= r; kk++)
                            {
                                int z2 = z + kk;
                                if(z2 < 0 || z2 >= sz[2]) continue;
                                for(int jj = -r; jj <= r; jj++)
                                {
                                    int y2 = y + jj;
                                    if(y2 < 0 || y2 >= sz[1]) continue;
                                    for(int ii = -r; ii <= r; ii++)
                                    {
                                        int x2 = x + ii;
                                        if(x2 < 0 || x2 >= sz[0]) continue;
                                        double dst = ii*ii + jj*jj + kk*kk;
                                        if(dst > rr) continue;
                                        tmpimg[z2][y2][x2] = 0;
                                    }
                                }
                            }
                        }
                    }

                    filter_segs.add(seg);
                    visited_segs.add(seg);     // used to delete children when parent node is delete
                }
            }


            System.out.println("prune by coverage (segment number) : "+seg_dist_map.size()+" - "+filter_segs.size()+" = "+(seg_dist_map.size() - filter_segs.size()));
            System.out.println("R/S ratio = "+tol_sum_rdc/tol_sum_sig+" ("+tol_sum_rdc+"/"+tol_sum_sig+")");
            if(true) // evaluation
            {
                double tree_sig = 0.0; for(int m = 0; m < inswc.size(); m++) tree_sig += inimg[(int) (inswc.elementAt(m).z+0.5)][(int) (inswc.elementAt(m).y+0.5)][(int) (inswc.elementAt(m).x+0.5)];
                double covered_sig = 0.0;
                for(int k = 0; k<sz[2]; k++) {
                    for(int j=0; j<sz[1]; j++)
                        for(int i=0; i<sz[0]; i++)
                            if(tmpimg[k][j][i] == 0) covered_sig += inimg[k][j][i];
                }
                System.out.println("S/T ratio = "+covered_sig/tree_sig+" ("+covered_sig+"/"+tree_sig+")");
            }
        }

//        if(0) // resampling markers or internal node pruning //this part of code has bug: many fragmentations. noted by PHC, 20120628
//        {
//            cout<<"resampling markers"<<endl;
//            vector<MyMarker*> tmp_markers;
//            topo_segs2swc(filter_segs, tmp_markers, 0); // no resampling
//            child_num.clear();
//            getLeaf_markers(tmp_markers, child_num);
//
//            // calculate sampling markers
//            for(int i = 0; i < filter_segs.size(); i++)
//            {
//                HierarchySegment * seg = filter_segs[i];
//                MyMarker * leaf_marker = seg->leaf_marker;
//                MyMarker * root_marker = seg->root_marker;
//                vector<MyMarker*> seg_markers;
//                MyMarker * p = leaf_marker;
//                while(true){seg_markers.push_back(p); if(p == root_marker) break; p = p->parent;}
//                //reverse(seg_markers.begin(), seg_markers.end()); // need to reverse if resampling //commened by PHC, 130520 to build on Ubuntu. This should make no difference as the outside code is if (0)
//                vector<MyMarker*> sampling_markers; // store resampling markers
//                p = root_marker; sampling_markers.push_back(p);
//                for(int m = 0; m < seg_markers.size(); m++)
//                {
//                    MyMarker * marker = seg_markers[m];
//                    if(child_num[marker] > 1 || dist(*marker, *p) >= p->radius)// + marker->radius)
//                    {
//                        sampling_markers.push_back(marker);
//                        p = marker;
//                    }
//                }
//                if((*sampling_markers.rbegin()) != leaf_marker) sampling_markers.push_back(leaf_marker);
//                for(int m = 1; m < sampling_markers.size(); m++) sampling_markers[m]->parent = sampling_markers[m-1];
//            }
//        }

        if(true)//is_leaf_prune)  // leaf nodes pruning
        {
            System.out.println("Perform leaf node pruning");

            Map<MyMarker,Integer> tmp_child_num = new HashMap<MyMarker, Integer>();
            if(true) // get child_num of each node
            {
                Vector<MyMarker> current_markers = new Vector<MyMarker>();
                Map<MyMarker,Integer> marker_index_map = new HashMap<MyMarker,Integer>();
                for(int i = 0; i < filter_segs.size(); i++)
                {
                    HierarchySegment  seg = filter_segs.elementAt(i);
                    seg.getMarkers(current_markers);
                }
                int[] cnum = new int[current_markers.size()];
                for(int m = 0; m < current_markers.size(); m++) {
                    marker_index_map.put(current_markers.elementAt(m),m);
                    cnum[m] = 0;
                }
                for(int m = 0; m < current_markers.size(); m++)
                {
                    MyMarker par_marker = current_markers.elementAt(m).parent;
                    if(par_marker != null) cnum[marker_index_map.get(par_marker)]++;
                }
                for(int m = 0; m< current_markers.size(); m++){
                    tmp_child_num.put(current_markers.elementAt(m),cnum[m]);
                }
            }
            int leaf_num_pruned = 1;
            int iteration = 1;
            boolean[] is_pruneable = new boolean[filter_segs.size()];
            while(leaf_num_pruned > 0)
            {
                leaf_num_pruned = 0;
                for(int i = 0; i < filter_segs.size(); i++)
                {
                    if(iteration > 1 && !is_pruneable[i]) continue;
                    HierarchySegment seg = filter_segs.elementAt(i);
                    MyMarker leaf_marker = seg.leaf_marker;
                    MyMarker root_marker = seg.root_marker;

                    if(tmp_child_num.get(leaf_marker) >= 1) continue;

//                    assert(leaf_marker);
                    MyMarker par_marker = leaf_marker.parent;
                    if(par_marker == null)
                    {
                        is_pruneable[i] = false;
                        continue;
                    }
                    int r1 = (int) leaf_marker.radius;
                    int r2 = (int) par_marker.radius;
                    double r1_r1 = r1 * r1;
                    double r2_r2 = r2 * r2;
                    int x1 = (int) (leaf_marker.x + 0.5);
                    int y1 = (int) (leaf_marker.y + 0.5);
                    int z1 = (int) (leaf_marker.z + 0.5);
                    int x2 = (int) (par_marker.x + 0.5);
                    int y2 = (int) (par_marker.y + 0.5);
                    int z2 = (int) (par_marker.z + 0.5);

                    double sum_leaf_int  = 0.0;
                    double sum_over_int = 0.0;
                    for(int kk = -r1; kk <= r1; kk++)
                    {
                        int zz = z1 + kk;
                        if(zz < 0 || zz >= sz[2]) continue;
                        for(int jj = -r1; jj <= r1; jj++)
                        {
                            int yy = y1 + jj;
                            if(yy < 0 || yy >= sz[1]) continue;
                            for(int ii = -r1; ii <= r1; ii++)
                            {
                                int xx = x1 + ii;
                                if(xx < 0 || xx >= sz[0]) continue;
                                double dst = kk * kk + jj * jj + ii * ii;
                                if(dst > r1_r1) continue;
                                sum_leaf_int += inimg[zz][yy][xx];
                                if((z2 - zz) * (z2 - zz) + (y2 - yy) * (y2 - yy) + (x2 - xx) * (x2 - xx) <= r2 * r2)
                                {
                                    sum_over_int += inimg[zz][yy][xx];
                                }
                            }
                        }
                    }
                    if(sum_leaf_int > 0 && sum_over_int/sum_leaf_int >= 0.9)
                    {
                        leaf_num_pruned ++;
//                        tmp_child_num[par_marker]--;
                        int childnum = tmp_child_num.get(par_marker);
                        tmp_child_num.remove(par_marker);
                        tmp_child_num.put(par_marker,childnum-1);
//                        assert(tmp_child_num[leaf_marker] == 0);
                        if(leaf_marker != root_marker)
                        {
                            seg.leaf_marker = par_marker;
                            is_pruneable[i] = true;
                        }
                        else
                        {
                            seg.leaf_marker = null;
                            seg.root_marker = null;
                            is_pruneable[i] = false;
                        }
                    }
                    else is_pruneable[i] = false;
                }
                iteration++;
                System.out.println("\t iteration ["+iteration+"] "+leaf_num_pruned+" leaf node pruned");
            }
            // filter out segments with single marker
            Vector<HierarchySegment> tmp_segs = new Vector<HierarchySegment>();
            for(int i = 0; i < filter_segs.size(); i++)
            {
                HierarchySegment seg = filter_segs.elementAt(i);
                MyMarker leaf_marker = seg.leaf_marker;
                MyMarker root_marker = seg.root_marker;
                if(leaf_marker != null && root_marker !=null) tmp_segs.add(seg);
            }
            System.out.println("\t"+(filter_segs.size() - tmp_segs.size())+" hierarchical segments are pruned in leaf node pruning");
            filter_segs.clear();
            for(int i=0; i<tmp_segs.size(); i++){
                filter_segs.add(tmp_segs.elementAt(i));
            }
        }


        if(true) // joint leaf node pruning
        {
            System.out.println("Perform joint leaf node pruning");
            System.out.println("\tcompute mask area");
            short[][][] mask = new short[sz[2]][sz[1]][sz[0]];
            for(int k=0;k<sz[2];k++) {
                for(int j=0; j<sz[1]; j++)
                    for(int i=0; i<sz[0]; i++)
                        mask[k][j][i] = 0;
            }
            for(int s = 0; s < filter_segs.size(); s++)
            {
                HierarchySegment seg = filter_segs.elementAt(s);
                MyMarker leaf_marker = seg.leaf_marker;
                MyMarker root_marker = seg.root_marker;
                MyMarker p = leaf_marker;
                while(true)
                {
                    int r = (int) p.radius;
                    if(r > 0)
                    {
                        double rr = r * r;
                        int x = (int) (p.x + 0.5);
                        int y = (int) (p.y + 0.5);
                        int z = (int) (p.z + 0.5);
                        for(int kk = -r; kk <= r; kk++)
                        {
                            int z2 = z + kk;
                            if(z2 < 0 || z2 >= sz[2]) continue;
                            for(int jj = -r; jj <= r; jj++)
                            {
                                int y2 = y + jj;
                                if(y2 < 0 || y2 >= sz[1]) continue;
                                for(int ii = -r; ii <= r; ii++)
                                {
                                    int x2 = x + ii;
                                    if(x2 < 0 || x2 >= sz[0]) continue;
                                    double dst = ii*ii + jj*jj + kk*kk;
                                    if(dst > rr) continue;
                                    mask[z2][y2][x2]++;
                                }
                            }
                        }
                    }
                    if(p.equals(root_marker)) break;
                    p = p.parent;
                }
            }
            System.out.println("\tget post_segs");
            Vector<HierarchySegment> post_segs;
//            if(0) // get post order of filter_segs
//            {
//                multimap<double, HierarchySegment*> tmp_seg_map;
//                for(int s = 0; s < filter_segs.size(); s++)
//                {
//                    double dst = filter_segs[s]->length;
//                    tmp_seg_map.insert(pair<double, HierarchySegment*>(dst, filter_segs[s]));
//                }
//                multimap<double, HierarchySegment*>::iterator it = tmp_seg_map.begin();
//                while(it != tmp_seg_map.end())
//                {
//                    post_segs.push_back(it->second);
//                    it++;
//                }
//            }
//            else post_segs = filter_segs; // random order


            Map<MyMarker,Integer> tmp_child_num = new HashMap<MyMarker,Integer>();
            if(true) // get child_num of each node
            {
                Vector<MyMarker> current_markers = new Vector<MyMarker>();
                Map<MyMarker,Integer> marker_index_map = new HashMap<MyMarker,Integer>();
                for(int i = 0; i < filter_segs.size(); i++)
                {
                    HierarchySegment  seg = filter_segs.elementAt(i);
                    seg.getMarkers(current_markers);
                }
                int[] cnum = new int[current_markers.size()];
                for(int m = 0; m < current_markers.size(); m++) {
                    marker_index_map.put(current_markers.elementAt(m),m);
                    cnum[m] = 0;
                }
                for(int m = 0; m < current_markers.size(); m++)
                {
                    MyMarker par_marker = current_markers.elementAt(m).parent;
                    if(par_marker != null) cnum[marker_index_map.get(par_marker)]++;
                }
                for(int m = 0; m< current_markers.size(); m++){
                    tmp_child_num.put(current_markers.elementAt(m),cnum[m]);
                }
            }

            if(true) // start prune leaf nodes
            {
                System.out.println("\tleaf node pruning");
                int leaf_num_pruned = 1;
                int iteration = 1;
                boolean[] is_pruneable = new boolean[filter_segs.size()];
                while(leaf_num_pruned > 0)
                {
                    leaf_num_pruned = 0;
                    for(int i = 0; i < filter_segs.size(); i++)
                    {
                        if(iteration > 1 && !is_pruneable[i]) continue;
                        HierarchySegment seg = filter_segs.elementAt(i);
                        MyMarker leaf_marker = seg.leaf_marker;
                        MyMarker root_marker = seg.root_marker;
                        int r = (int) leaf_marker.radius;
                        if(r <= 0 )
                        {
                            is_pruneable[i] = false;
                            continue;
                        }
                        double rr = r * r;
                        int x = (int) (leaf_marker.x + 0.5);
                        int y = (int) (leaf_marker.y + 0.5);
                        int z = (int) (leaf_marker.z + 0.5);

                        double covered_sig = 0; double total_sig = 0.0;
                        for(int kk = -r; kk <= r; kk++)
                        {
                            int z2 = z + kk;
                            if(z2 < 0 || z2 >= sz[2]) continue;
                            for(int jj = -r; jj <= r; jj++)
                            {
                                int y2 = y + jj;
                                if(y2 < 0 || y2 >= sz[1]) continue;
                                for(int ii = -r; ii <= r; ii++)
                                {
                                    int x2 = x + ii;
                                    if(x2 < 0 || x2 >= sz[0]) continue;
                                    double dst = ii*ii + jj*jj + kk*kk;
                                    if(dst > rr) continue;
                                    if(mask[z2][y2][x2] > 1) covered_sig += inimg[z2][y2][x2];
                                    total_sig += inimg[z2][y2][x2];
                                }
                            }
                        }
                        if(covered_sig / total_sig >= 0.9) // 90% joint cover, prune it
                        {
                            if(tmp_child_num.get(leaf_marker) == 0) // real leaf node
                            {
                                leaf_num_pruned++;
                                MyMarker par_marker = leaf_marker.parent;
                                if(par_marker != null){
                                    int childnum = tmp_child_num.get(par_marker);
                                    tmp_child_num.remove(par_marker);
                                    tmp_child_num.put(par_marker,childnum-1);
                                }
                                if(!leaf_marker.equals(root_marker))
                                {
                                    seg.leaf_marker = par_marker;
                                    is_pruneable[i] = true; // *** able to prune continuous
                                }
                                else // if(leaf_marker == root_marker) // unable to prune
                                {
                                    seg.leaf_marker = null;
                                    seg.root_marker = null;
                                    is_pruneable[i] = false; // *** no marker left, unable to prune again
                                }
                                // unmask leaf_marker area
                                {
                                    for(int kk = -r; kk <= r; kk++)
                                    {
                                        int z2 = z + kk;
                                        if(z2 < 0 || z2 >= sz[2]) continue;
                                        for(int jj = -r; jj <= r; jj++)
                                        {
                                            int y2 = y + jj;
                                            if(y2 < 0 || y2 >= sz[1]) continue;
                                            for(int ii = -r; ii <= r; ii++)
                                            {
                                                int x2 = x + ii;
                                                if(x2 < 0 || x2 >= sz[0]) continue;
                                                double dst = ii*ii + jj*jj + kk*kk;
                                                if(dst > rr) continue;
                                                if(mask[z2][y2][x2] > 1) mask[z2][y2][x2]--;
                                            }
                                        }
                                    }
                                }
                            }
                            else is_pruneable[i] = true; // keep it until it is leaf node
                        }
                        else is_pruneable[i] = false;
                    }
                    iteration++;
                    System.out.println("\t iteration ["+iteration+"] "+leaf_num_pruned+" leaf node pruned");
                }
                // filter out segments with single marker
                Vector<HierarchySegment> tmp_segs = new Vector<HierarchySegment>();
                for(int i = 0; i < filter_segs.size(); i++)
                {
                    HierarchySegment seg = filter_segs.elementAt(i);
                    MyMarker leaf_marker = seg.leaf_marker;
                    MyMarker root_marker = seg.root_marker;
                    if(leaf_marker != null && root_marker != null) tmp_segs.add(seg); // filter out empty segments
                }
                System.out.println("\t"+(filter_segs.size() - tmp_segs.size())+" hierarchical segments are pruned in joint leaf node pruning");
                filter_segs.clear();
                for(int i=0; i<tmp_segs.size(); i++){
                    filter_segs.add(tmp_segs.elementAt(i));
                }
            }
        }

        if(is_smooth) // smooth curve
        {
            System.out.println("Smooth the final curve");
            for(int i = 0; i < filter_segs.size(); i++)
            {
                HierarchySegment seg = filter_segs.elementAt(i);
                Vector<MyMarker> seg_markers = new Vector<MyMarker>();
                seg.getMarkers(seg_markers);
                MyMarker.smoothCurve(seg_markers,7);
                smooth_radius(seg_markers, 5,false);
            }
        }

        outswc.clear();
        System.out.println(filter_segs.size()+" segments left");
        segsToSwc(filter_segs, outswc, 0); // no resampling

        System.out.println("outswc size: "+outswc.size());

        return true;
    }

    public static boolean happBySample(Vector<MyMarker> inswc, Vector<MyMarker> outswc, int[][][] inimg, int[] sz, double bkg_thresh,
                                       double length_thresh){

        int markerSize = inswc.size();
        Map<MyMarker,Integer> markerIndexMap = new HashMap<>();
        int[] markerFlag = new int[markerSize];
        for(int i=0; i<markerSize; i++){
            markerIndexMap.put(inswc.get(i),i);
            markerFlag[i] = 0;
        }


        Vector<HierarchySegment> segs = new Vector<HierarchySegment>();
        System.out.println("Construct hierarchical segments");
        swcToSegs(inswc,segs,INTENSITY_DISTANCE_METHOD,inimg,sz);

        int segSize = segs.size();
        boolean[] segFlag = new boolean[segSize];
        for(int i=0; i<segSize; i++){
            if(segs.get(i).length >= length_thresh){
                segFlag[i] = true;
            }else {
                segFlag[i] = false;
            }
        }

        for(int i=0; i<segSize; i++){
            if(!segFlag[i]){
                Vector<MyMarker> segMarkers = new Vector<>();
                segs.get(i).getMarkers(segMarkers);
                for(int j=0; j<segMarkers.size(); j++){
                    int markerIndex = markerIndexMap.get(segMarkers.get(j));
                    markerFlag[markerIndex] = 2;
                }
            }
        }

        for(int i=0; i<markerSize; i++){
            if(markerFlag[i] != 2) {
                int x = (int) (inswc.get(i).x + 0.5);
                int y = (int) (inswc.get(i).y + 0.5);
                int z = (int) (inswc.get(i).z + 0.5);
                if(inimg[z][y][x] < bkg_thresh){
                    markerFlag[i] = 1;
                }
            }
        }

        for(int i=0; i<segSize; i++){
            if(segFlag[i]){
                Vector<MyMarker> segMarkers = new Vector<>();
                segs.get(i).getMarkers(segMarkers);
                int count = 0;
                Vector<Integer> markersUndefinedIndex = new Vector<>();
                for(int j=0; j<segMarkers.size(); j++){
                    int markerIndex = markerIndexMap.get(segMarkers.get(j));
                    if(markerFlag[markerIndex] == 1){
                        markersUndefinedIndex.add(markerIndex);
                        count++;
                    }else if(markerFlag[markerIndex] == 0){
                        if(count>5){
                            for(int k=0; k<markersUndefinedIndex.size(); k++){
                                int markerDeleteIndex = markersUndefinedIndex.get(k);
                                markerFlag[markerDeleteIndex] = 2;
                            }
                            markersUndefinedIndex.clear();
                            count = 0;
                        }else {
                            markersUndefinedIndex.clear();
                            count = 0;
                        }
                    }
                }
            }
        }

        for(int i=0; i<segSize; i++){
            if(segFlag[i]){
                Vector<MyMarker> segMarkers = new Vector<>();
                segs.get(i).getMarkers(segMarkers);
                int count = 0;
                Vector<Integer> markersUndefinedIndex = new Vector<>();
                for(int j=0; j<segMarkers.size(); j++){
                    int markerIndex = markerIndexMap.get(segMarkers.get(j));
                    if(markerFlag[markerIndex] != 2){
                        markersUndefinedIndex.add(markerIndex);
                        count++;
                    }else{
                        if(count<5){
                            for(int k=0; k<markersUndefinedIndex.size(); k++){
                                int markerDeleteIndex = markersUndefinedIndex.get(k);
                                markerFlag[markerDeleteIndex] = 2;
                            }
                            markersUndefinedIndex.clear();
                            count = 0;
                        }else {
                            markersUndefinedIndex.clear();
                            count = 0;
                        }
                    }
                }
            }
        }

        for(int i=0; i<markerSize; i++){
            if(inswc.get(i).parent!=null){
                int prtIndex = markerIndexMap.get(inswc.get(i).parent);
                if(markerFlag[prtIndex] == 2){
                    inswc.get(i).parent = null;
                }
            }
            if(markerFlag[i] != 2){
                outswc.add(inswc.get(i));
            }
        }

        Vector<HierarchySegment> outSegs = new Vector<HierarchySegment>();
        System.out.println("Construct out swc hierarchical segments");
        swcToSegs(outswc,outSegs,INTENSITY_DISTANCE_METHOD,inimg,sz);

        // smooth curve
        {
            System.out.println("Smooth the final curve");
            for(int i = 0; i < outSegs.size(); i++)
            {
                HierarchySegment outSeg = outSegs.elementAt(i);
                Vector<MyMarker> seg_markers = new Vector<MyMarker>();
                outSeg.getMarkers(seg_markers);
                MyMarker.smoothCurve(seg_markers,7);
//                smooth_radius(seg_markers, 5,false);
            }
        }

        return true;
    }


}
