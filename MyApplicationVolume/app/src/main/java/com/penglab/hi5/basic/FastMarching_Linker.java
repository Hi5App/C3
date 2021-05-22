package com.penglab.hi5.basic;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.penglab.hi5.core.render.MyMarker;
import com.penglab.hi5.basic.tracingfunc.app2.HeapElemX;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import static com.penglab.hi5.basic.FastMarching_Linker.Type.ALIVE;
import static com.penglab.hi5.basic.FastMarching_Linker.Type.FAR;
import static com.penglab.hi5.basic.FastMarching_Linker.Type.TRIAL;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

public class FastMarching_Linker {

    final static private double INF = 3.4e+38;
    enum Type{ALIVE,TRIAL,FAR};
    final static private int MAX_INT = 2147483647;

    static double[] givals = {22026.5,   20368, 18840.3, 17432.5, 16134.8, 14938.4, 13834.9, 12816.8,
            11877.4, 11010.2, 10209.4,  9469.8, 8786.47, 8154.96, 7571.17, 7031.33,
            6531.99, 6069.98, 5642.39, 5246.52, 4879.94, 4540.36, 4225.71, 3934.08,
            3663.7, 3412.95, 3180.34,  2964.5, 2764.16, 2578.14, 2405.39,  2244.9,
            2095.77, 1957.14, 1828.24, 1708.36, 1596.83, 1493.05, 1396.43, 1306.47,
            1222.68, 1144.62, 1071.87, 1004.06, 940.819, 881.837, 826.806, 775.448,
            727.504, 682.734, 640.916, 601.845, 565.329, 531.193, 499.271, 469.412,
            441.474, 415.327, 390.848, 367.926, 346.454, 326.336, 307.481, 289.804,
            273.227, 257.678, 243.089, 229.396, 216.541, 204.469, 193.129, 182.475,
            172.461, 163.047, 154.195, 145.868, 138.033, 130.659, 123.717, 117.179,
            111.022,  105.22, 99.7524, 94.5979, 89.7372, 85.1526,  80.827, 76.7447,
            72.891, 69.2522, 65.8152, 62.5681, 59.4994, 56.5987,  53.856, 51.2619,
            48.8078, 46.4854, 44.2872, 42.2059, 40.2348, 38.3676, 36.5982, 34.9212,
            33.3313, 31.8236, 30.3934, 29.0364, 27.7485,  26.526,  25.365, 24.2624,
            23.2148, 22.2193,  21.273, 20.3733, 19.5176, 18.7037, 17.9292,  17.192,
            16.4902,  15.822, 15.1855,  14.579, 14.0011, 13.4503, 12.9251, 12.4242,
            11.9464, 11.4905, 11.0554, 10.6401, 10.2435, 9.86473, 9.50289, 9.15713,
            8.82667, 8.51075, 8.20867, 7.91974, 7.64333, 7.37884, 7.12569, 6.88334,
            6.65128, 6.42902,  6.2161, 6.01209, 5.81655, 5.62911, 5.44938, 5.27701,
            5.11167, 4.95303, 4.80079, 4.65467, 4.51437, 4.37966, 4.25027, 4.12597,
            4.00654, 3.89176, 3.78144, 3.67537, 3.57337, 3.47528, 3.38092, 3.29013,
            3.20276, 3.11868, 3.03773,  2.9598, 2.88475, 2.81247, 2.74285, 2.67577,
            2.61113, 2.54884, 2.48881, 2.43093, 2.37513, 2.32132, 2.26944, 2.21939,
            2.17111, 2.12454, 2.07961, 2.03625, 1.99441, 1.95403, 1.91506, 1.87744,
            1.84113, 1.80608, 1.77223, 1.73956, 1.70802, 1.67756, 1.64815, 1.61976,
            1.59234, 1.56587, 1.54032, 1.51564, 1.49182, 1.46883, 1.44664, 1.42522,
            1.40455,  1.3846, 1.36536,  1.3468,  1.3289, 1.31164, 1.29501, 1.27898,
            1.26353, 1.24866, 1.23434, 1.22056,  1.2073, 1.19456, 1.18231, 1.17055,
            1.15927, 1.14844, 1.13807, 1.12814, 1.11864, 1.10956, 1.10089, 1.09262,
            1.08475, 1.07727, 1.07017, 1.06345, 1.05709, 1.05109, 1.04545, 1.04015,
            1.03521,  1.0306, 1.02633, 1.02239, 1.01878,  1.0155, 1.01253, 1.00989,
            1.00756, 1.00555, 1.00385, 1.00246, 1.00139, 1.00062, 1.00015,       1};


    private static double GI(int index, byte[] inimg1d, double min_int, double max_int, int datatype, boolean isBig){
        int val = (int)min_int;
        int i = index;

        switch (datatype){
            case 1:
                val = ByteTranslate.byte1ToInt(inimg1d[i]);

                break;

            case 2:
                byte [] b = new byte[2];
                b[0] = inimg1d[i * 2];
                b[1] = inimg1d[i * 2 + 1];
                val = ByteTranslate.byte2ToInt(b, isBig);

                break;

            case 4:
                b= new byte[4];
                b[0] = inimg1d[i * 4];
                b[1] = inimg1d[i * 4 + 1];
                b[2] = inimg1d[i * 4 + 2];
                b[3] = inimg1d[i * 4 + 3];
                val = ByteTranslate.byte2ToInt(b, isBig);

                break;

            default:
                break;
        }

        return givals[(int)((val - min_int)/max_int*255)];
    }

    private static double GI(int index, byte[] inimg1d, double min_int, double max_int){
        return givals[(int)((inimg1d[index] - min_int)/max_int*255)];
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean fastmarching_linker(Vector<MyMarker> sub_markers, Vector<MyMarker> tar_markers,
                                        byte[] inimg1d, Vector<MyMarker> outswc, int sz0, int sz1, int sz2, int datatype, boolean isBig){
        int cnn_type = 2;
        

        //clock_t t1=clock(); // start time
        int tol_sz = sz0 * sz1 * sz2;
        int sz01 = sz0 * sz1;

        if (tol_sz <= 0) {
            System.out.println("wrong size info in fastmarching_linker");
            return false;
        }

        System.out.println("cnn_type = " + cnn_type);
        System.out.println("fastmarching_linker");

        int i;
        double[] phi = new double[tol_sz];
        for(i = 0; i < tol_sz; i++) { phi[i] = INF; }

        Map<Integer, MyMarker> sub_map = new HashMap<>();
        Map<Integer, MyMarker> tar_map = new HashMap<>();

        for(i = 0; i < tar_markers.size(); i++)
        {
            int x = (int) ( tar_markers.get(i).x + 0.5 );
            int y = (int) ( tar_markers.get(i).y + 0.5 );
            int z = (int) ( tar_markers.get(i).z + 0.5 );
            int ind = z * sz01 + y * sz0 + x;

            //assert(x >= 0 && x <=sz0-1 && y >= 0 && y <=sz1-1 && z >= 0 && z <=sz2-1); //Hang indicated that this is the problem why there would be crashing, 120405.
            
            if (x >= 0 && x < sz0 && y >= 0 && y < sz1 && z >= 0 && z < sz2) {
                tar_map.put(ind, tar_markers.get(i));
            }
        }


        for(i = 0; i < sub_markers.size(); i++)
        {
            int x   = (int) ( sub_markers.get(i).x + 0.5 );
            int y   = (int) ( sub_markers.get(i).y + 0.5 );
            int z   = (int) ( sub_markers.get(i).z + 0.5 );
            int ind = z * sz01 + y * sz0 + x;
            //assert(x >= 0 && x < sz0 && y >= 0 && y < sz1 && z >= 0 && z < sz2);
            
            if (x >= 0 && x <= sz0-1 && y >= 0 && y <= sz1-1 && z >= 0 && z <= sz2-1)
                sub_map.put(ind, sub_markers.get(i));
        }

        // GI parameter min_int, max_int, li
        double max_int = 0; // maximum intensity, used in GI
        double min_int = INF;
        for(i = 0; i < tol_sz; i++)
        {
            if(inimg1d[i] > max_int) max_int = inimg1d[i];
            if(inimg1d[i] < min_int) min_int = inimg1d[i];
        }
        max_int -= min_int;
        if (max_int == 0.0) return false; // no image data, avoid divide by zero in GI
        double li = 10;

        // initialization
        Type[] state = new Type[tol_sz];
        for(i = 0; i < tol_sz; i++) state[i] = FAR;

        Vector<Integer> submarker_inds = new Vector<>();

        for(int s = 0; s < sub_markers.size(); s++) {
            int ii = (int)(sub_markers.get(s).x + 0.5);
            int jj = (int)(sub_markers.get(s).y + 0.5);
            int kk = (int)(sub_markers.get(s).z + 0.5);
            int ind = kk * sz01 + jj * sz0 + ii;
            submarker_inds.add(ind);
            state[ind] = ALIVE;
            phi[ind] = 0.0;
        }
        System.out.println("totalsize = " + tol_sz);

        int[] parent = new int[(int) tol_sz];
        for (int ind = 0; ind < tol_sz; ind++){
            parent[(int) ind] = ind;
        }

        PriorityQueue<HeapElemX> heap = new PriorityQueue<HeapElemX>();
        Map<Integer, HeapElemX> elems = new HashMap<>();

        // init heap
        for(int s = 0; s < submarker_inds.size(); s++)
        {
            int index = submarker_inds.get(s);
            HeapElemX elem = new HeapElemX((int) index, (int) index, phi[(int) index]);
            heap.add(elem);
            elems.put(index, elem);
        }

        // loop
        int time_counter = sub_markers.size();
        double process1 = time_counter*1000.0/tol_sz;
        int stop_ind = -1;
        System.out.println("now prepare test heap");
        while (!heap.isEmpty()){
            double process2 = (time_counter++)*1000.0/tol_sz;
            if (process2 - process1 >= 1){
                System.out.println("\r" + ((int)process2)/10.0 + "%");
                process1 = process2;
            }

            HeapElemX min_elem = heap.poll();
            elems.remove((int) min_elem.img_index);

            int min_ind = min_elem.img_index;
            parent[(int) min_ind] = min_elem.prev_index;
            if(tar_map.containsKey(min_ind)){
                stop_ind = min_ind;
                break;
            }

            state[ min_ind] = ALIVE;
            int ix = min_ind % sz0;
            int jy   = (min_ind/sz0) % sz1;
            int kz  = (min_ind/sz01) % sz2;
            int w, h, d;

            for(int kk = -1; kk <= 1; kk++)
            {
                d = kz + kk;
                if(d < 0 || d >= sz2) continue;
                for(int jj = -1; jj <= 1; jj++)
                {
                    h = jy + jj;
                    if(h < 0 || h >= sz1) continue;
                    for(int ii = -1; ii <= 1; ii++)
                    {
                        w = ix + ii;
                        if(w < 0 || w >= sz0) continue;
                        int offset = Math.abs(ii) + Math.abs(jj) + Math.abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;
                        double factor = (offset == 1) ? 1.0 : ((offset == 2) ? 1.414214 : ((offset == 3) ? 1.732051 : 0.0));
                        int index = d * sz01 + h * sz0 + w;

                        if(state[index] != ALIVE)
                        {
                            double new_dist = phi[min_ind] + (GI(index, inimg1d, min_int, max_int, datatype, isBig) + GI(min_ind, inimg1d, min_int, max_int, datatype, isBig))*factor*0.5;
                            int prev_ind = min_ind;

                            if(state[index] == FAR)
                            {
                                phi[index] = new_dist;
                                HeapElemX  elem = new HeapElemX((int) index, (int) prev_ind, phi[(int) index]);
                                heap.add(elem);
                                elems.put(index, elem);
                                state[index] = TRIAL;
                            }
                            else if(state[index] == TRIAL)
                            {
                                if(phi[index] > new_dist)
                                {
                                    phi[index] = new_dist;
                                    HeapElemX elem = elems.get(index);
                                    heap.remove(elem);
                                    elem.value = phi[index];
                                    elem.setPrev_index(prev_ind);
                                    heap.add(elem);
                                }
                            }
                        }
                    }
                }
            }
            // assert(!mask_values.empty());
        }


        int[] in_sz = {sz0, sz1, sz2, 1};
        double thresh = 20;
        System.out.println("set thres=20");
        // connect markers according to disjoint set
        {
            // add tar_marker
            int ind = stop_ind;
            MyMarker tar_marker = tar_map.get(stop_ind);
            MyMarker new_marker = new MyMarker(tar_marker.x, tar_marker.y, tar_marker.z);
            //new_marker->radius = markerRadius(inimg1d, in_sz, *new_marker, thresh);
            new_marker.parent = null; //tar_marker;

            outswc.add(new_marker);

            MyMarker par_marker = new_marker;
            ind = parent[ind];
            while(!sub_map.containsKey(ind))
            {
                int ix = ind % sz0;
                int jy = ind/sz0 % sz1;
                int kz = ind/sz01 % sz2;
                new_marker = new MyMarker(ix, jy, kz);
                new_marker.parent = par_marker;
                //new_marker->radius = markerRadius(inimg1d, in_sz, *new_marker, thresh);
                outswc.add(new_marker);
                par_marker = new_marker;
                if (ind >= 0 && ind < tol_sz)
                {
                    if (ind == parent[ind])
                    {
                        System.out.println("[WARNING][VIRTUAL FINGER]: a self-loop exists. Abort!");
                        break;
                    }
                    ind = parent[ind];
                } else {
                    break;
                }
            }
            // add sub_marker
            MyMarker sub_marker = sub_map.get(ind);
            new_marker = new MyMarker(sub_marker.x, sub_marker.y, sub_marker.z);
            new_marker.parent = par_marker;
            //new_marker->radius = markerRadius(inimg1d, in_sz, *new_marker, thresh);
            outswc.add(new_marker);
        }

        Collections.reverse(outswc);

        System.out.println(outswc.size() + " markers linked");

        //for(int i = 0; i < sub_markers.size(); i++) outswc.push_back(sub_markers[i]);
        //for(int i = 0; i < tar_markers.size(); i++) outswc.push_back(tar_markers[i]);

        if(!elems.isEmpty()){
            elems.clear();
        }
        if(phi != null) {phi = null;}
        if(parent != null) {parent = null;}
        if(state != null) {state = null;}
        return true;



    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    private static Vector<MyMarker> fastmarching_linker(Vector<MyMarker> sub_markers, Vector<MyMarker> tar_markers,
                                        byte[] inimg1d, Vector<MyMarker> outswc, int sz0, int sz1, int sz2, int cnn_type, int datatype, boolean isBig){

        //clock_t t1=clock(); // start time
        int tol_sz = sz0 * sz1 * sz2;
        int sz01 = sz0 * sz1;

        if (tol_sz <= 0) {
            System.out.println("wrong size info in fastmarching_linker");
            return null;
        }

        System.out.println("cnn_type = " + cnn_type);
        System.out.println("fastmarching_linker");

        int i;
        double[] phi = new double[tol_sz];
        for(i = 0; i < tol_sz; i++) { phi[i] = INF; }

        Map<Integer, MyMarker> sub_map = new HashMap<>();
        Map<Integer, MyMarker> tar_map = new HashMap<>();

        for(i = 0; i < tar_markers.size(); i++)
        {
            int x = (int) ( tar_markers.get(i).x + 0.5 );
            int y = (int) ( tar_markers.get(i).y + 0.5 );
            int z = (int) ( tar_markers.get(i).z + 0.5 );
            int ind = z * sz01 + y * sz0 + x;

            //assert(x >= 0 && x <=sz0-1 && y >= 0 && y <=sz1-1 && z >= 0 && z <=sz2-1); //Hang indicated that this is the problem why there would be crashing, 120405.

            if (x >= 0 && x < sz0 && y >= 0 && y < sz1 && z >= 0 && z < sz2) {
                tar_map.put(ind, tar_markers.get(i));
            }
        }


        for(i = 0; i < sub_markers.size(); i++)
        {
            int x   = (int) ( sub_markers.get(i).x + 0.5 );
            int y   = (int) ( sub_markers.get(i).y + 0.5 );
            int z   = (int) ( sub_markers.get(i).z + 0.5 );
            int ind = z * sz01 + y * sz0 + x;
            //assert(x >= 0 && x < sz0 && y >= 0 && y < sz1 && z >= 0 && z < sz2);

            if (x >= 0 && x <= sz0-1 && y >= 0 && y <= sz1-1 && z >= 0 && z <= sz2-1)
                sub_map.put(ind, sub_markers.get(i));
        }

        // GI parameter min_int, max_int, li
        double max_int = 0; // maximum intensity, used in GI
        double min_int = INF;
        for(i = 0; i < tol_sz; i++)
        {
            int val = 0;

            switch (datatype){
                case 1:
                    val = ByteTranslate.byte1ToInt(inimg1d[i]);

                    break;

                case 2:
                    byte [] b = new byte[2];
                    b[0] = inimg1d[i * 2];
                    b[1] = inimg1d[i * 2 + 1];
                    val = ByteTranslate.byte2ToInt(b, isBig);

                    break;

                case 4:
                    b= new byte[4];
                    b[0] = inimg1d[i * 4];
                    b[1] = inimg1d[i * 4 + 1];
                    b[2] = inimg1d[i * 4 + 2];
                    b[3] = inimg1d[i * 4 + 3];
                    val = ByteTranslate.byte2ToInt(b, isBig);

                    break;

                default:
                    break;
            }

            if(val > max_int)
                max_int = val;
            if(val < min_int)
                min_int = val;
        }
        max_int -= min_int;
        if (max_int == 0.0) return null; // no image data, avoid divide by zero in GI
        double li = 10;

        // initialization
        Type[] state = new Type[tol_sz];
        for(i = 0; i < tol_sz; i++) state[i] = FAR;

        Vector<Integer> submarker_inds = new Vector<>();

        for(int s = 0; s < sub_markers.size(); s++) {
            int ii = (int)(sub_markers.get(s).x + 0.5);
            int jj = (int)(sub_markers.get(s).y + 0.5);
            int kk = (int)(sub_markers.get(s).z + 0.5);
            int ind = kk * sz01 + jj * sz0 + ii;
            submarker_inds.add(ind);
            state[ind] = ALIVE;
            phi[ind] = 0.0;
        }
        System.out.println("totalsize = " + tol_sz);

        int[] parent = new int[(int) tol_sz];
        for (int ind = 0; ind < tol_sz; ind++){
            parent[(int) ind] = ind;
        }

        BasicHeap heap = new BasicHeap();
        Map<Integer, HeapElemX> elems = new HashMap<>();

        // init heap
        for(int s = 0; s < submarker_inds.size(); s++)
        {
            int index = submarker_inds.get(s);
            HeapElemX elem = new HeapElemX((int) index, (int) index, phi[(int) index]);
            heap.insert(elem);
            elems.put(index, elem);
        }

        // loop
        int time_counter = sub_markers.size();
        double process1 = time_counter*1000.0/tol_sz;
        int stop_ind = -1;
        System.out.println("now prepare test heap");
        while (!heap.empty()){
            double process2 = (time_counter++)*1000.0/tol_sz;
            if (process2 - process1 >= 1){

//                System.out.println("\r" + ((int)process2)/10.0 + "%");
                process1 = process2;
            }

            HeapElemX min_elem = heap.delete_min();
            elems.remove((int) min_elem.img_index);

            int min_ind = min_elem.img_index;
            parent[(int) min_ind] = min_elem.prev_index;
            if(tar_map.containsKey(min_ind)){
                stop_ind = min_ind;
                break;
            }

            state[ min_ind] = ALIVE;
            int ix = min_ind % sz0;
            int jy   = (min_ind/sz0) % sz1;
            int kz  = (min_ind/sz01) % sz2;
            int w, h, d;

            for(int kk = -1; kk <= 1; kk++)
            {
                d = kz + kk;
                if(d < 0 || d >= sz2) continue;
                for(int jj = -1; jj <= 1; jj++)
                {
                    h = jy + jj;
                    if(h < 0 || h >= sz1) continue;
                    for(int ii = -1; ii <= 1; ii++)
                    {
                        w = ix + ii;
                        if(w < 0 || w >= sz0) continue;
                        int offset = Math.abs(ii) + Math.abs(jj) + Math.abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;
                        double factor = (offset == 1) ? 1.0 : ((offset == 2) ? 1.414214 : ((offset == 3) ? 1.732051 : 0.0));
                        int index = d*sz01 + h*sz0 + w;

                        if(state[index] != ALIVE)
                        {
                            double new_dist = phi[(int) min_ind] + (GI(index, inimg1d, min_int, max_int, datatype, isBig) + GI(min_ind, inimg1d, min_int, max_int, datatype, isBig))*factor*0.5;
                            int prev_ind = min_ind;

                            if(state[index] == FAR)
                            {
                                phi[index] = new_dist;
                                HeapElemX  elem = new HeapElemX((int) index, (int) prev_ind, phi[(int) index]);
                                heap.insert(elem);
                                elems.put(index, elem);
                                state[index] = TRIAL;
                            }
                            else if(state[index] == TRIAL)
                            {
                                if(phi[index] > new_dist)
                                {
                                    phi[index] = new_dist;
                                    HeapElemX elem = elems.get(index);
                                    heap.adjust(elem.heap_id, phi[index]);
//                                    heap.remove(elem);
//                                    elem.value = phi[index];
                                    elem.setPrev_index(prev_ind);
//                                    heap.add(elem);
                                }
                            }
                        }
                    }
                }
            }
            // assert(!mask_values.empty());
        }


        int[] in_sz = {sz0, sz1, sz2, 1};
        double thresh = 20;
        System.out.println("set thres=20");
        // connect markers according to disjoint set
        {
            // add tar_marker
            int ind = stop_ind;
            MyMarker tar_marker = tar_map.get(stop_ind);
            MyMarker new_marker = new MyMarker(tar_marker.x, tar_marker.y, tar_marker.z);
            //new_marker->radius = markerRadius(inimg1d, in_sz, *new_marker, thresh);
            new_marker.parent = null; //tar_marker;

            outswc.add(new_marker);

            MyMarker par_marker = new_marker;
            ind = parent[ind];
            while(!sub_map.containsKey(ind))
            {
                int ix = ind % sz0;
                int jy = ind/sz0 % sz1;
                int kz = ind/sz01 % sz2;
                new_marker = new MyMarker(ix, jy, kz);
                new_marker.parent = par_marker;
                //new_marker->radius = markerRadius(inimg1d, in_sz, *new_marker, thresh);
                outswc.add(new_marker);
                par_marker = new_marker;
                if (ind >= 0 && ind < tol_sz)
                {
                    if (ind == parent[ind])
                    {
                        System.out.println("[WARNING][VIRTUAL FINGER]: a self-loop exists. Abort!");
                        break;
                    }
                    ind = parent[ind];
                } else {
                    break;
                }
            }
            // add sub_marker
            MyMarker sub_marker = sub_map.get(ind);
            new_marker = new MyMarker(sub_marker.x, sub_marker.y, sub_marker.z);
            new_marker.parent = par_marker;
            //new_marker->radius = markerRadius(inimg1d, in_sz, *new_marker, thresh);
            outswc.add(new_marker);
        }

        Collections.reverse(outswc);

        System.out.println(outswc.size() + " markers linked");

        //for(int i = 0; i < sub_markers.size(); i++) outswc.push_back(sub_markers[i]);
        //for(int i = 0; i < tar_markers.size(); i++) outswc.push_back(tar_markers[i]);

        if(!elems.isEmpty()){
            elems.clear();
        }
        if(phi != null) {phi = null;}
        if(parent != null) {parent = null;}
        if(state != null) {state = null;}
        return outswc;

    }




    /******************************************************************************
     * Fast marching based linker, will give out the nearest two markers in two different sets
     *
     * Input :  sub_markers     the source markers with initial phi values
     *          tar_markers     the target markers, will store final phi values and the parent will the changed
     *          inimg1d         original input image
     *
     * Output : par_tree        the parental tree from tar_markers to sub_markers
     *
     * Notice :
     * min_int is set to 0
     * max_int is set to 255 always
     * markers in tar_markers and sub_markers are not included
     * *****************************************************************************/
    // int cnn_type = 2
    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean fastmarching_linker
            (Map<MyMarker, Double> sub_markers, Map<MyMarker, Double> tar_markers, byte[] inimg1d, Vector<MyMarker> par_tree,
             int sz0, int sz1, int sz2, int stop_num, int cnn_type){

        stop_num = MIN(stop_num, tar_markers.size());

        int tol_sz = sz0 * sz1 * sz2;
        int sz01 = sz0 * sz1;
        //int cnn_type = 2;  // ?

        System.out.println("cnn_type = " + cnn_type);

        double[] phi = new double[tol_sz];
        for(int i = 0; i < tol_sz; i++){phi[i] = INF;}

        Map<Integer, MyMarker> sub_map = new HashMap<>();
        Map<Integer, MyMarker> tar_map = new HashMap<>();

//        for(map<MyMarker*, double>::iterator it = tar_markers.begin(); it != tar_markers.end(); it++)
//        {
//            MyMarker * tar_marker = it->first;
//            int x = tar_marker->x + 0.5;
//            int y = tar_marker->y + 0.5;
//            int z = tar_marker->z + 0.5;
//            int ind = z*sz01 + y*sz0 + x;
//            tar_map[ind] = tar_marker;
//        }

        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {

            MyMarker tar_marker = entry.getKey();
            int x = (int) ( tar_marker.x + 0.5 );
            int y = (int) ( tar_marker.y + 0.5 );
            int z = (int) ( tar_marker.z + 0.5 );
            int ind = z * sz01 + y * sz0 + x;
            tar_map.put(ind, tar_marker);
        }

//        for(map<MyMarker*, double>::iterator it = sub_markers.begin(); it != sub_markers.end(); it++)
//        {
//            MyMarker * sub_marker = it->first;
//            int x = sub_marker->x + 0.5;
//            int y = sub_marker->y + 0.5;
//            int z = sub_marker->z + 0.5;
//            int ind = z*sz01 + y*sz0 + x;
//            sub_map[ind] = sub_marker;
//        }

        for (Map.Entry<MyMarker, Double> entry : sub_markers.entrySet()) {

            MyMarker sub_marker = entry.getKey();
            int x = (int) ( sub_marker.x + 0.5 );
            int y = (int) ( sub_marker.y + 0.5 );
            int z = (int) ( sub_marker.z + 0.5 );
            int ind = z*sz01 + y*sz0 + x;
            sub_map.put(ind, sub_marker);
        }

        // GI parameter min_int, max_int, li
	/*double max_int = 0; // maximum intensity, used in GI
	double min_int = INF;
	for(int i = 0; i < tol_sz; i++)
	{
		if(inimg1d[i] > max_int) max_int = inimg1d[i];
		if(inimg1d[i] < min_int) min_int = inimg1d[i];
	}
	max_int -= min_int;
	*/
        double min_int = 0;
        double max_int = 255;
        double li = 10;

        // initialization
        Type[] state = new Type[tol_sz];
        for(int i = 0; i < tol_sz; i++) state[i] = FAR;

        Vector<Integer> submarker_inds = new Vector<Integer>();


//        for(map<MyMarker*, double>::iterator it = sub_markers.begin(); it != sub_markers.end(); it++)
//        {
//            MyMarker * sub_marker = it->first;
//            int i = sub_marker->x + 0.5;
//            int j = sub_marker->y + 0.5;
//            int k = sub_marker->z + 0.5;
//            if(i < 0 || i >= sz0 || j < 0 || j >= sz1 || k < 0 || k >= sz2) continue;
//            int ind = k*sz01 + j*sz0 + i;
//            submarker_inds.push_back(ind);
//            state[ind] = ALIVE;
//            phi[ind] = sub_markers[sub_marker];
//        }

        for (Map.Entry<MyMarker, Double> entry : sub_markers.entrySet()) {

            MyMarker sub_marker = entry.getKey();
            int i = (int) ( sub_marker.x + 0.5 );
            int j = (int) ( sub_marker.y + 0.5 );
            int k = (int) ( sub_marker.z + 0.5 );
            if(i < 0 || i >= sz0 || j < 0 || j >= sz1 || k < 0 || k >= sz2) continue;
            int ind = k*sz01 + j*sz0 + i;
            submarker_inds.add(ind);
            state[ind] = ALIVE;
            phi[ind] = sub_markers.get(sub_marker);
        }

        int[] parent = new int[tol_sz];
        for(int ind = 0; ind < tol_sz; ind++) parent[ind] = ind;

//        BasicHeap<HeapElemX> heap;
//        map<int, HeapElemX*> elems;

        PriorityQueue<HeapElemX> heap = new PriorityQueue<HeapElemX>();
        Map<Integer, HeapElemX> elems = new HashMap<>();


        // init heap
        for(int s = 0; s < submarker_inds.size(); s++)
        {
            int index = submarker_inds.get(s);
            HeapElemX elem = new HeapElemX(index, index, phi[index]);
            heap.add(elem);
            elems.put(index, elem);
        }
        // loop
        int time_counter = sub_markers.size();
        double process1 = time_counter*1000.0/tol_sz;
        Vector<Integer> marched_inds = new Vector<>();
        while(!heap.isEmpty())
        {
            double process2 = (time_counter++)*1000.0/tol_sz;
            if(process2 - process1 >= 1){
                System.out.println("\r" + ((int)process2)/10.0 + "%");
                process1 = process2;}

            HeapElemX min_elem = heap.poll();
            elems.remove(min_elem.img_index);

            int min_ind = min_elem.img_index;
            parent[min_ind] = min_elem.prev_index;
            if(tar_map.containsKey(min_ind))
            {
                marched_inds.add(min_ind);
                if(marched_inds.size() > stop_num) break;
            }


            state[min_ind] = ALIVE;
            int i = min_ind % sz0;
            int j = (min_ind/sz0) % sz1;
            int k = (min_ind/sz01) % sz2;
            int w, h, d;

            for(int kk = -1; kk <= 1; kk++)
            {
                d = k+kk;
                if(d < 0 || d >= sz2) continue;
                for(int jj = -1; jj <= 1; jj++)
                {
                    h = j+jj;
                    if(h < 0 || h >= sz1) continue;
                    for(int ii = -1; ii <= 1; ii++)
                    {
                        w = i+ii;
                        if(w < 0 || w >= sz0) continue;
                        int offset = Math.abs(ii) + Math.abs(jj) + Math.abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;
                        double factor = (offset == 1) ? 1.0 : ((offset == 2) ? 1.414214 : ((offset == 3) ? 1.732051 : 0.0));
                        int index = d*sz01 + h*sz0 + w;

                        if(state[index] != ALIVE)
                        {
                            double new_dist = phi[min_ind] + (GI(index, inimg1d, min_int, max_int) + GI(min_ind, inimg1d, min_int, max_int))*factor*0.5;
                            int prev_ind = min_ind;

                            if(state[index] == FAR)
                            {
                                phi[index] = new_dist;
                                HeapElemX elem = new HeapElemX(index, prev_ind, phi[index]);
                                heap.add(elem);
                                elems.put(index, elem);
                                state[index] = TRIAL;
                            }
                            else if(state[index] == TRIAL)
                            {
                                if(phi[index] > new_dist)
                                {
                                    phi[index] = new_dist;
                                    HeapElemX elem = elems.get(index);

                                    heap.remove(elem);
                                    elem.value = phi[index];
                                    elem.setPrev_index(prev_ind);
                                    heap.add(elem);

                                    /**
                                     * adjust heap
                                     */

//                                    heap.adjust(elem->heap_id, phi[index]);
//                                    elem->prev_ind = prev_ind;
                                }
                            }
                        }
                    }
                }
            }
            // assert(!mask_values.empty());
        }


        // refresh the score values and parent info in tar_markers
//        for(map<MyMarker*, double>::iterator it = tar_markers.begin(); it != tar_markers.end(); it++)
//        {
//            MyMarker * tar_marker = it->first;
//            int x = tar_marker->x + 0.5;
//            int y = tar_marker->y + 0.5;
//            int z = tar_marker->z + 0.5;
//            int ind = z*sz01 + y*sz0 + x;
//            if(state[ind] != ALIVE || tar_marker != tar_map[ind])
//            {
//                tar_marker->parent = 0;
//                it->second = INF;
//                continue;
//            }
//            it->second = phi[ind];
//
//            // find the path from tar_marker
//            // tar_marker is not added
//            MyMarker * child_marker = tar_marker;
//
//            ind = parent[ind];
//            while(sub_map.find(ind) == sub_map.end())
//            {
//                int i = ind % sz0;
//                int j = ind/sz0 % sz1;
//                int k = ind/sz01 % sz2;
//                MyMarker * new_marker = new MyMarker(i,j,k);
//                child_marker->parent = new_marker;
//                new_marker->parent = 0;
//                new_marker->radius = 5;//markerRadius(inimg1d, in_sz, *new_marker, thresh);
//                par_tree.push_back(new_marker);
//                child_marker = new_marker;
//                ind = parent[ind];
//            }
//            MyMarker * sub_marker = sub_map[ind];
//            child_marker->parent = sub_marker;
//            // sub_marker is not added
//            //par_tree.push_back(sub_marker);
//        }


        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {

            MyMarker tar_marker = entry.getKey();
            int x = (int) ( tar_marker.x + 0.5 );
            int y = (int) ( tar_marker.y + 0.5 );
            int z = (int) ( tar_marker.z + 0.5 );
            int ind = z*sz01 + y*sz0 + x;
            if(state[ind] != ALIVE || tar_marker != tar_map.get(ind))
            {
                tar_marker.parent = null;
                entry.setValue(INF);
                continue;
            }
            entry.setValue(phi[ind]);

            // find the path from tar_marker
            // tar_marker is not added
            MyMarker child_marker = tar_marker;

            ind = parent[ind];
            while( !sub_map.containsKey(ind) )
            {
                int i = ind % sz0;
                int j = ind/sz0 % sz1;
                int k = ind/sz01 % sz2;
                MyMarker new_marker = new MyMarker(i,j,k);
                child_marker.parent = new_marker;
                new_marker.parent = null;
                new_marker.radius = 5;          //markerRadius(inimg1d, in_sz, *new_marker, thresh);
                par_tree.add(new_marker);
                child_marker = new_marker;
                ind = parent[ind];
            }
            MyMarker sub_marker = sub_map.get(ind);
            child_marker.parent = sub_marker;
            // sub_marker is not added
            //par_tree.push_back(sub_marker);
        }

        System.out.println(par_tree.size() + " markers in par_tree");

        if(phi == null) { phi = null;}
        if(parent == null) { parent = null;}
        if(state == null) { state = null;}
        return true;


    }

//    int cnn_type = 2, int margin = 5,double intensityThreshold=0
    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean fastmarching_linker
            (Map<MyMarker, Double> sub_markers, Map<MyMarker, Double> tar_markers, byte[] inimg1d, Vector<MyMarker> par_tree,
             int sz0, int sz1, int sz2, MyMarker nm1, MyMarker fm1, MyMarker nm2, MyMarker fm2, int stop_num, int cnn_type, int margin, double intensityThreshold){

        assert(par_tree.isEmpty());
        int sz01 = sz0 * sz1;

        // 1. calc rt
        double[] rt = {0.0, 0.0, 0.0};
        if(nm1 != fm1)
        {
            double tx1 = fm1.x - nm1.x;
            double ty1 = fm1.y - nm1.y;
            double tz1 = fm1.z - nm1.z;
            double dst1 = sqrt(tx1 * tx1 + ty1 * ty1 + tz1 * tz1);
            rt[0] = tx1 / dst1;
            rt[1] = ty1 / dst1;
            rt[2] = tz1 / dst1;
        }
        else if(nm2 != fm2)
        {
            double tx2 = fm2.x - nm2.x;
            double ty2 = fm2.y - nm2.y;
            double tz2 = fm2.z - nm2.z;
            double dst2 = sqrt(tx2 * tx2 + ty2 * ty2 + tz2 * tz2);
            rt[0] = tx2 / dst2;
            rt[1] = ty2 / dst2;
            rt[2] = tz2 / dst2;
        }
        else
        {
            System.out.println("Error : nm1 == nm2 && fm1 == fm2");
            return false;
        }
        // 2. calc different vectors
        double[] n1n2 = {nm2.x - nm1.x, nm2.y - nm1.y, nm2.z - nm1.z};
        MAKE_UNIT(n1n2);
        double[] n2n1 = {-n1n2[0], -n1n2[1], -n1n2[2]};

        double[] f1f2 = {fm2.x - fm1.x, fm2.y - fm1.y, fm2.z - fm1.z};
        MAKE_UNIT(f1f2);
        double[] f2f1 = {-f1f2[0], -f1f2[1], -f1f2[2]};

        double[] n1f1 = {rt[0], rt[1], rt[2]};
        double[] f1n1 = {-rt[0], -rt[1], -rt[2]};

        double[] n2f2 = {rt[0], rt[1], rt[2]};
        double[] f2n2 = {-rt[0], -rt[1], -rt[2]};

        //int margin = 5;

        // 1. get initial rectangel
        MyMarker[] rect = {nm1, nm2, fm2, fm1};
        double cos_n1, cos_n2, cos_f1, cos_f2;
        if((cos_n1 = COS_THETA_UNIT(n1f1, n1n2)) < 0.0)
        {
            double d = dist(nm1, nm2) * (-cos_n1);
            rect[0] = new MyMarker(nm1.x - d * rt[0], nm1.y - d * rt[1], nm1.z - d * rt[2]);
            System.out.println("cos_n1 = " + cos_n1);

        }
        if((cos_n2 = COS_THETA_UNIT(n2f2, n2n1)) < 0.0)
        {
            double d = dist(nm1, nm2) * (-cos_n2);
            rect[1] = new MyMarker(nm2.x - d * rt[0], nm2.y - d * rt[1], nm2.z - d * rt[2]);
            System.out.println("cos_n2 = " + cos_n2);

        }
        if((cos_f2 = COS_THETA_UNIT(f2n2, f2f1)) < 0.0)
        {
            double d = dist(fm1, fm2) * (-cos_f2);
            rect[2] = new MyMarker(fm2.x + d * rt[0], fm2.y + d * rt[1], fm2.z + d * rt[2]);
            System.out.println("cos_f2 = " + cos_f2);

        }
        if((cos_f1 = COS_THETA_UNIT(f1n1, f1f2)) < 0.0)
        {
            double d = dist(fm1, fm2) * (-cos_f1);
            rect[3] = new MyMarker(fm1.x + d * rt[0], fm1.y + d * rt[1], fm1.z + d * rt[2]);
            System.out.println("cos_f1 = " + cos_f1);

        }

        // 2. add margin
        double[] a = new double[3];
        a[0] = rect[3].x - rect[0].x;
        a[1] = rect[3].y - rect[0].y;
        a[2] = rect[3].z - rect[0].z;
        double la = sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
        a[0] /= la; a[1] /= la; a[2] /= la;

        double[] b = new double[3];
        b[0] = rect[1].x - rect[0].x;
        b[1] = rect[1].y - rect[0].y;
        b[2] = rect[1].z - rect[0].z;
        double lb = sqrt(b[0] * b[0] + b[1] * b[1] + b[2] * b[2]);
        b[0] /= lb; b[1] /= lb; b[2] /= lb;

        double[] c = new double[3];
        c[0] = a[1] * b[2] - a[2] * b[1];
        c[1] = a[2] * b[0] - a[0] * b[2];
        c[2] = a[0] * b[1] - a[1] * b[0];
        double lc = sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]);
        c[0] /= lc; c[1] /= lc; c[2] /= lc;

        MyMarker o = new MyMarker();
        o.x = rect[0].x - margin * a[0] - margin * b[0] - margin * c[0];
        o.y = rect[0].y - margin * a[1] - margin * b[1] - margin * c[1];
        o.z = rect[0].z - margin * a[2] - margin * b[2] - margin * c[2];

        int bsz0 = (int) ( dist(rect[0], rect[3]) + 1 + 2 * margin + 0.5 );
        int bsz1 = (int) ( dist(rect[0], rect[1]) + 1 + 2 * margin + 0.5 );
        int bsz2 = (int) ( 1 + 2 * margin + 0.5 );
        int bsz01 = bsz0 * bsz1;
        int btol_sz = bsz01 * bsz2;

        byte[] outimg1d = new byte[btol_sz];
        for(int i = 0; i < btol_sz; i++)
            outimg1d[i] = 0;

        if(intensityThreshold == 1)
        {
            /**
             * something need change here
             */
            
            boolean miok = true;
//            double d = QInputDialog::getDouble(0,"Intensity Threshold 1%-99%","please input your number",60,1,99,5,&miok);
            double d = 10f;
            if(miok)
            {
                System.out.println("input number is " + d);
                intensityThreshold = d * 0.01;
            }
        }

        if( intensityThreshold > 0 && ( intensityThreshold < 1))
        {
            double max_int = 0; // maximum intensity
            double min_int = INF;
            for(int k = 0; k < bsz2; k++)
            {
                for(int j = 0; j < bsz1; j++)
                {
                    for(int i = 0; i < bsz0; i++)
                    {
                        int ii = (int) ( o.x + i * a[0] + j * b[0] + k * c[0] + 0.5 );
                        int jj = (int) ( o.y + i * a[1] + j * b[1] + k * c[1] + 0.5 );
                        int kk = (int) ( o.z + i * a[2] + j * b[2] + k * c[2] + 0.5 );
                        if(ii >= 0 && ii < sz0 && jj >= 0 && jj < sz1 && kk >= 0 && kk < sz2)
                        {
                            int ind2 = kk * sz01 + jj * sz0 + ii;
                            if(inimg1d[ind2]>max_int) max_int=inimg1d[ind2];
                            if(inimg1d[ind2]<min_int) min_int=inimg1d[ind2];
                        }
                    }
                }
            }
            for(int k = 0; k < bsz2; k++)
            {
                for(int j = 0; j < bsz1; j++)
                {
                    for(int i = 0; i < bsz0; i++)
                    {
                        int ii = (int) ( o.x + i * a[0] + j * b[0] + k * c[0] + 0.5 );
                        int jj = (int) ( o.y + i * a[1] + j * b[1] + k * c[1] + 0.5 );
                        int kk = (int) ( o.z + i * a[2] + j * b[2] + k * c[2] + 0.5 );
                        if(ii >= 0 && ii < sz0 && jj >= 0 && jj < sz1 && kk >= 0 && kk < sz2)
                        {
                            int ind1 = k * bsz01 + j * bsz0 + i;
                            int ind2 = kk * sz01 + jj * sz0 + ii;
                            if( inimg1d[ind2] <= max_int * ( intensityThreshold))
                            outimg1d[ind1] = inimg1d[ind2];
                        }
                    }
                }
            }
        }
    else
        {
            for(int k = 0; k < bsz2; k++)
            {
                for(int j = 0; j < bsz1; j++)
                {
                    for(int i = 0; i < bsz0; i++)
                    {
                        int ii = (int) ( o.x + i * a[0] + j * b[0] + k * c[0] + 0.5 );
                        int jj = (int) ( o.y + i * a[1] + j * b[1] + k * c[1] + 0.5 );
                        int kk = (int) ( o.z + i * a[2] + j * b[2] + k * c[2] + 0.5 );
                        if(ii >= 0 && ii < sz0 && jj >= 0 && jj < sz1 && kk >= 0 && kk < sz2)
                        {
                            int ind1 = k * bsz01 + j * bsz0 + i;
                            int ind2 = kk * sz01 + jj * sz0 + ii;
                            outimg1d[ind1] = inimg1d[ind2];
                        }
                    }
                }
            }

        }

        // 3. get new_sub_markers and new_tar_markers
//        for(map<MyMarker*, double>::iterator it = sub_markers.begin(); it != sub_markers.end(); it++)
//        {
//            MyMarker * sub_marker = it->first;
//            sub_marker->x = margin + dist(*sub_marker, rect[0]);
//            sub_marker->y = margin;
//            sub_marker->z = margin;
//        }

        for (Map.Entry<MyMarker, Double> entry : sub_markers.entrySet()) {

            MyMarker sub_marker = entry.getKey();
            sub_marker.x = margin + dist( sub_marker, rect[0]);
            sub_marker.y = margin;
            sub_marker.z = margin;
        }

        double dst_rect01 = dist(rect[0], rect[1]);
//        for(map<MyMarker*, double>::iterator it = tar_markers.begin(); it != tar_markers.end(); it++)
//        {
//            MyMarker * tar_marker = it->first;
//            tar_marker->x = margin + dist(*tar_marker, rect[1]);
//            tar_marker->y = margin + dst_rect01;
//            tar_marker->z = margin;
//        }

        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {

            MyMarker tar_marker = entry.getKey();
            tar_marker.x = margin + dist( tar_marker, rect[1]);
            tar_marker.y = margin + dst_rect01;;
            tar_marker.z = margin;
        }

        fastmarching_linker(sub_markers, tar_markers, outimg1d, par_tree, bsz0, bsz1, bsz2, stop_num, cnn_type);

//        for(map<MyMarker*, double>::iterator it = sub_markers.begin(); it != sub_markers.end(); it++)
//        {
//            MyMarker * sub_marker = it->first;
//            double x = o.x + sub_marker->x * a[0] + sub_marker->y * b[0] + sub_marker->z * c[0];
//            double y = o.y + sub_marker->x * a[1] + sub_marker->y * b[1] + sub_marker->z * c[1];
//            double z = o.z + sub_marker->x * a[2] + sub_marker->y * b[2] + sub_marker->z * c[2];
//            sub_marker->x = x;
//            sub_marker->y = y;
//            sub_marker->z = z;
//        }

        for (Map.Entry<MyMarker, Double> entry : sub_markers.entrySet()) {

            MyMarker sub_marker = entry.getKey();
            double x = o.x + sub_marker.x * a[0] + sub_marker.y * b[0] + sub_marker.z * c[0];
            double y = o.y + sub_marker.x * a[1] + sub_marker.y * b[1] + sub_marker.z * c[1];
            double z = o.z + sub_marker.x * a[2] + sub_marker.y * b[2] + sub_marker.z * c[2];
            sub_marker.x = x;
            sub_marker.y = y;
            sub_marker.z = z;
        }


//        for(map<MyMarker*, double>::iterator it = tar_markers.begin(); it != tar_markers.end(); it++)
//        {
//            MyMarker * tar_marker = it->first;
//            double x = o.x + tar_marker->x * a[0] + tar_marker->y * b[0] + tar_marker->z * c[0];
//            double y = o.y + tar_marker->x * a[1] + tar_marker->y * b[1] + tar_marker->z * c[1];
//            double z = o.z + tar_marker->x * a[2] + tar_marker->y * b[2] + tar_marker->z * c[2];
//            tar_marker->x = x;
//            tar_marker->y = y;
//            tar_marker->z = z;
//        }

        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {

            MyMarker tar_marker = entry.getKey();
            double x = o.x + tar_marker.x * a[0] + tar_marker.y * b[0] + tar_marker.z * c[0];
            double y = o.y + tar_marker.x * a[1] + tar_marker.y * b[1] + tar_marker.z * c[1];
            double z = o.z + tar_marker.x * a[2] + tar_marker.y * b[2] + tar_marker.z * c[2];
            tar_marker.x = x;
            tar_marker.y = y;
            tar_marker.z = z;

        }


//        for(vector<MyMarker*>::iterator it = par_tree.begin(); it != par_tree.end(); it++)
//        {
//            MyMarker * marker = *it;
//            double x = o.x + marker->x * a[0] + marker->y * b[0] + marker->z * c[0];
//            double y = o.y + marker->x * a[1] + marker->y * b[1] + marker->z * c[1];
//            double z = o.z + marker->x * a[2] + marker->y * b[2] + marker->z * c[2];
//            marker->x = x;
//            marker->y = y;
//            marker->z = z;
//        }


        for (int i = 0; i < par_tree.size(); i++){

            MyMarker marker = par_tree.get(i);
            double x = o.x + marker.x * a[0] + marker.y * b[0] + marker.z * c[0];
            double y = o.y + marker.x * a[1] + marker.y * b[1] + marker.z * c[1];
            double z = o.z + marker.x * a[2] + marker.y * b[2] + marker.z * c[2];
            marker.x = x;
            marker.y = y;
            marker.z = z;
        }


        if( outimg1d != null ){ outimg1d = null;}
        return true;

    }





    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("Assert")
    private boolean fastmarching_drawing_dynamic
            (Vector<MyMarker> near_markers, Vector<MyMarker> far_markers, byte[] inimg1d, Vector<MyMarker> outswc,
             int sz0, int sz1, int sz2, int cnn_type, int margin, boolean intensityThresholdmode){

        int sz01 = sz0 * sz1;
        if (intensityThresholdmode)
            System.out.println("welcome to fastmarching_drawing_dynamicly (intensity threshold mode)");
        else
            System.out.println("welcome to fastmarching_drawing_dynamicly");

        assert ((near_markers.size() == far_markers.size()) && (near_markers.size() >= 2));
        MyMarker near_marker1;    // = near_markers[0];
        MyMarker far_marker1;     // = far_markers[0];
        MyMarker near_marker2 = near_markers.get(0);
        MyMarker far_marker2 = far_markers.get(0);

        Set<MyMarker> start_ray = new HashSet<>();
        Map<MyMarker, Double> sub_markers = new HashMap<>();
        Map<MyMarker, Double> tar_markers = new HashMap<>();

        tar_markers = get_line_marker_map(near_marker2, far_marker2);

        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {
            entry.setValue(0.0);
            start_ray.add(entry.getKey());
        }
        Vector<MyMarker> all_markers = new Vector<>();
        Vector<MyMarker> par_tree = new Vector<>();

        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {
            all_markers.add(entry.getKey());
        }

        double intensityThreshold = 0;

        if(intensityThresholdmode)
            intensityThreshold=1.0;

        for(int i = 1; i < near_markers.size(); i++)
        {
            if(near_markers.get(i).ind(sz0, sz01) == near_marker2.ind(sz0, sz01) || far_markers.get(i).ind(sz0, sz01) == far_marker2.ind(sz0,sz01))
            {
                System.out.println("ray " + i + " is duplicated ");
                continue;
            }
            else
            {
                System.out.println("fm-linker between ray " + (i-1) + " and ray " + i);
            }

            near_marker1 = near_marker2;         far_marker1 = far_marker2;
            near_marker2 = near_markers.get(i);  far_marker2 = far_markers.get(i);

            sub_markers.clear();                 sub_markers = tar_markers;
            tar_markers.clear();                 tar_markers = get_line_marker_map(near_marker2, far_marker2);

            int stop_num = (i == (near_markers.size()-1))? 1 : (tar_markers.size()+1)/2;
            fastmarching_linker(sub_markers, tar_markers, inimg1d, par_tree, sz0, sz1, sz2, near_marker1, far_marker1, near_marker2, far_marker2, stop_num, cnn_type, margin, intensityThreshold);

//            all_markers.insert(all_markers.end(), par_tree.begin(), par_tree.end());
            all_markers.addAll(par_tree);
            par_tree.clear();

            for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {
                all_markers.add(entry.getKey());
            }

        }

        // extract the best trajectory
        double min_score = 0;
        MyMarker min_marker = null;

        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {
            MyMarker myMarker = entry.getKey();
            double   score    = entry.getValue();

            if(min_marker == null || score < min_score)
            {
                min_score = score;
                min_marker = myMarker;
            }
        }

        MyMarker p = min_marker;
        MyMarker new_marker   = new MyMarker(p.x, p.y, p.z); outswc.add(new_marker);
        MyMarker child_marker = new_marker;
        p = p.parent;
        while(p != null)
        {
            new_marker = new MyMarker(p.x, p.y, p.z);        outswc.add(new_marker);
            child_marker.parent = new_marker;
            child_marker = new_marker;
            if(p.parent == null) break;
            p = p.parent;
        }

        boolean ret = (start_ray.contains(p));

        // reverse and smooth
        Collections.reverse(outswc);
//        reverse(outswc.begin(), outswc.end());
        //smooth_curve(outswc, 4);

//        for(int i = 0; i < all_markers.size(); i++)
//            all_markers.remove(i);

        all_markers.clear();

        return ret;

    }





    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean fastmarching_drawing_dynamic
            (Vector<MyMarker> near_markers, Vector<MyMarker> far_markers, byte[] inimg1d, Vector<MyMarker> outswc,
             int sz0, int sz1, int sz2 ){

        int cnn_type = 2;
        int margin = 5;
        boolean intensityThresholdmode = false;

        int sz01 = (int) sz0 * sz1;
        if (intensityThresholdmode)
            System.out.println("welcome to fastmarching_drawing_dynamicly (intensity threshold mode)");
        else
            System.out.println("welcome to fastmarching_drawing_dynamicly");

        assert ((near_markers.size() == far_markers.size()) && (near_markers.size() >= 2));
        MyMarker near_marker1;    // = near_markers[0];
        MyMarker far_marker1;     // = far_markers[0];
        MyMarker near_marker2 = near_markers.get(0);
        MyMarker far_marker2 = far_markers.get(0);

        Set<MyMarker> start_ray = new HashSet<>();
        Map<MyMarker, Double> sub_markers = new HashMap<>();
        Map<MyMarker, Double> tar_markers = new HashMap<>();

        tar_markers = get_line_marker_map(near_marker2, far_marker2);

        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {
            entry.setValue(0.0);
            start_ray.add(entry.getKey());
        }
        Vector<MyMarker> all_markers = new Vector<>();
        Vector<MyMarker> par_tree = new Vector<>();

        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {
            all_markers.add(entry.getKey());
        }

        double intensityThreshold = 0;

        if(intensityThresholdmode)
            intensityThreshold=1.0;

        for(int i = 1; i < near_markers.size(); i++)
        {
            if(near_markers.get(i).ind(sz0, sz01) == near_marker2.ind(sz0, sz01) || far_markers.get(i).ind(sz0, sz01) == far_marker2.ind(sz0,sz01))
            {
                System.out.println("ray " + i + " is duplicated ");
                continue;
            }
            else
            {
                System.out.println("fm-linker between ray " + (i-1) + " and ray " + i);
            }

            near_marker1 = near_marker2;         far_marker1 = far_marker2;
            near_marker2 = near_markers.get(i);  far_marker2 = far_markers.get(i);

            sub_markers.clear();                 sub_markers = tar_markers;
            tar_markers.clear();                 tar_markers = get_line_marker_map(near_marker2, far_marker2);

            int stop_num = (i == (near_markers.size()-1))? 1 : (tar_markers.size()+1)/2;
            fastmarching_linker(sub_markers, tar_markers, inimg1d, par_tree, sz0, sz1, sz2, near_marker1, far_marker1, near_marker2, far_marker2, stop_num, cnn_type, margin, intensityThreshold);

//            all_markers.insert(all_markers.end(), par_tree.begin(), par_tree.end());
            all_markers.addAll(par_tree);
            par_tree.clear();

            for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {
                all_markers.add(entry.getKey());
            }

        }

        // extract the best trajectory
        double min_score = 0;
        MyMarker min_marker = null;

        for (Map.Entry<MyMarker, Double> entry : tar_markers.entrySet()) {
            MyMarker myMarker = entry.getKey();
            double   score    = entry.getValue();

            if(min_marker == null || score < min_score)
            {
                min_score = score;
                min_marker = myMarker;
            }
        }

        MyMarker p = min_marker;
        MyMarker new_marker   = new MyMarker(p.x, p.y, p.z); outswc.add(new_marker);
        MyMarker child_marker = new_marker;
        p = p.parent;
        while(p != null)
        {
            new_marker = new MyMarker(p.x, p.y, p.z);        outswc.add(new_marker);
            child_marker.parent = new_marker;
            child_marker = new_marker;
            if(p.parent == null) break;
            p = p.parent;
        }

        boolean ret = (start_ray.contains(p));

        // reverse and smooth
        Collections.reverse(outswc);
//        reverse(outswc.begin(), outswc.end());
        //smooth_curve(outswc, 4);

//        for(int i = 0; i < all_markers.size(); i++)
//            all_markers.remove(i);

        all_markers.clear();

        return ret;

    }





    private Map<MyMarker, Double> get_line_marker_map(MyMarker marker1, MyMarker marker2 ){

        Map<MyMarker, Double> marker_map = new HashMap<>();
        Set<MyMarker> unique_markers = new HashSet<>();

        double dst = sqrt((marker1.x - marker2.x) * (marker1.x - marker2.x) +
        (marker1.y - marker2.y) * (marker1.y - marker2.y) +
        (marker1.z - marker2.z) * (marker1.z - marker2.z));
        if(dst==0.0){
            MyMarker  marker = new MyMarker(marker1);
            marker_map.put(marker, INF);
        }

        else{
            double tx = (marker2.x - marker1.x) / dst;
            double ty = (marker2.y - marker1.y) / dst;
            double tz = (marker2.z - marker1.z) / dst;

            for(double r = 0.0; r < dst+1; r++){
                int x = (int) ( marker1.x + tx * r + 0.5 );
                int y = (int) ( marker1.y + ty * r + 0.5 );
                int z = (int) ( marker1.z + tz * r + 0.5 );
                if(!unique_markers.contains(new MyMarker(x, y, z)))
                {
                    MyMarker marker = new MyMarker(x,y,z);
                    marker_map.put(marker, INF);
                    unique_markers.add(marker);
                }
            }
        }
        unique_markers.clear();

        return marker_map;
    }




    private static double [] MAKE_UNIT(double[] V){
        double len = sqrt(V[0] * V[0] + V[1] * V[1] + V[2] * V[2]);
        V[0] /= len;
        V[1] /= len;
        V[2] /= len;
        return V;
    }

    private static double COS_THETA_UNIT(double[] A, double[] B){
        return A[0] * B[0] + A[1] * B[1] + A[2] * B[2];
    }

    private int COS_THETA_UNIT(int[] A, int[] B){
        return A[0] * B[0] + A[1] * B[1] + A[2] * B[2];
    }

    private static double dist(MyMarker a, MyMarker b){
        return sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y) + (a.z - b.z)*(a.z - b.z));
    }

    private int MIN(int x, int y){
        return x < y ? x:y;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Vector<MyMarker> fastmarching_drawing_serialboxes(Vector<MyMarker> near_markers, Vector<MyMarker> far_markers,
                                                    byte [] inimg1d, Vector<MyMarker> outswc, int sz0, int sz1, int sz2,
                                                    int cnn_type, int margin, boolean intensityThresholdmode, int datatype, boolean isBig){

        if(intensityThresholdmode)
            System.out.println("welcome to fastmarching_drawing4 (intensity threshold adjust mode)");
        else
            System.out.println("welcome to fastmarching_drawing4");
        assert(near_markers.size() == far_markers.size());

        if (near_markers.isEmpty())
        {
            // no stroke points to trace; bail out early
            return null;
        }

        MyMarker nm1, nm2, fm1, fm2;
        nm2 = near_markers.get(0);
        fm2 = far_markers.get(0);
        long sz01 = (long)sz0 * sz1;

        long mx = MAX_INT, my = MAX_INT, mz = MAX_INT;
        long Mx = 0, My = 0, Mz = 0;

        for (long m = 1; m < near_markers.size(); m++){
            nm1 = nm2; fm1 = fm2;
            nm2 = near_markers.get((int)m); fm2 = far_markers.get((int)m);

            // 1. calc rt
            double [] rt = {0.0, 0.0, 0.0};
            if(nm1 != fm1)
            {
                double tx1 = fm1.x - nm1.x;
                double ty1 = fm1.y - nm1.y;
                double tz1 = fm1.z - nm1.z;
                double dst1 = sqrt(tx1 * tx1 + ty1 * ty1 + tz1 * tz1);
                rt[0] = tx1 / dst1;
                rt[1] = ty1 / dst1;
                rt[2] = tz1 / dst1;
            }
            else if(nm2 != fm2)
            {
                double tx2 = fm2.x - nm2.x;
                double ty2 = fm2.y - nm2.y;
                double tz2 = fm2.z - nm2.z;
                double dst2 = sqrt(tx2 * tx2 + ty2 * ty2 + tz2 * tz2);
                rt[0] = tx2 / dst2;
                rt[1] = ty2 / dst2;
                rt[2] = tz2 / dst2;
            }
            else
            {
                System.out.println("Error : nm1 == nm2 && fm1 == fm2");
                return null;
            }

            // 2. calc different vectors
            double [] n1n2 = {nm2.x - nm1.x, nm2.y - nm1.y, nm2.z - nm1.z};
            n1n2 = MAKE_UNIT(n1n2);
            double [] n2n1 = {-n1n2[0], -n1n2[1], -n1n2[2]};

            double [] f1f2 = {fm2.x - fm1.x, fm2.y - fm1.y, fm2.z - fm1.z};
            f1f2 = MAKE_UNIT(f1f2);
            double [] f2f1 = {-f1f2[0], -f1f2[1], -f1f2[2]};

            double [] n1f1 = {rt[0], rt[1], rt[2]};
            double [] f1n1 = {-rt[0], -rt[1], -rt[2]};

            double [] n2f2 = {rt[0], rt[1], rt[2]};
            double [] f2n2 = {-rt[0], -rt[1], -rt[2]};

            margin = 5;

            // 1. get initial rectangel
            MyMarker [] rect = {nm1, nm2, fm2, fm1};
            double cos_n1, cos_n2, cos_f1, cos_f2;
            if((cos_n1 = COS_THETA_UNIT(n1f1, n1n2)) < 0.0)
            {
                double d = dist(nm1, nm2) * (-cos_n1);
                rect[0] = new MyMarker(nm1.x - d * rt[0], nm1.y - d * rt[1], nm1.z - d * rt[2]);
            }
            if((cos_n2 = COS_THETA_UNIT(n2f2, n2n1)) < 0.0)
            {
                double d = dist(nm1, nm2) * (-cos_n2);
                rect[1] = new MyMarker(nm2.x - d * rt[0], nm2.y - d * rt[1], nm2.z - d * rt[2]);
            }
            if((cos_f2 = COS_THETA_UNIT(f2n2, f2f1)) < 0.0)
            {
                double d = dist(fm1, fm2) * (-cos_f2);
                rect[2] = new MyMarker(fm2.x + d * rt[0], fm2.y + d * rt[1], fm2.z + d * rt[2]);
            }
            if((cos_f1 = COS_THETA_UNIT(f1n1, f1f2)) < 0.0)
            {
                double d = dist(fm1, fm2) * (-cos_f1);
                rect[3] = new MyMarker(fm1.x + d * rt[0], fm1.y + d * rt[1], fm1.z + d * rt[2]);
            }

            double [] a = new double[3];
            a[0] = rect[3].x - rect[0].x;
            a[1] = rect[3].y - rect[0].y;
            a[2] = rect[3].z - rect[0].z;
            double la = sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
            a[0] /= la; a[1] /= la; a[2] /= la;

            double []b = new double[3];
            b[0] = rect[1].x - rect[0].x;
            b[1] = rect[1].y - rect[0].y;
            b[2] = rect[1].z - rect[0].z;
            double lb = sqrt(b[0] * b[0] + b[1] * b[1] + b[2] * b[2]);
            b[0] /= lb; b[1] /= lb; b[2] /= lb;

            double [] c = new double[3];
            c[0] = a[1] * b[2] - a[2] * b[1];
            c[1] = a[2] * b[0] - a[0] * b[2];
            c[2] = a[0] * b[1] - a[1] * b[0];
            double lc = sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]);
            c[0] /= lc; c[1] /= lc; c[2] /= lc;

            MyMarker o = new MyMarker();
            o.x = rect[0].x - margin * a[0] - margin * b[0] - margin * c[0];
            o.y = rect[0].y - margin * a[1] - margin * b[1] - margin * c[1];
            o.z = rect[0].z - margin * a[2] - margin * b[2] - margin * c[2];

            long bsz0 = (long)(dist(rect[0], rect[3]) + 1 + 2 * margin + 0.5);
            long bsz1 = (long)(dist(rect[0], rect[1]) + 1 + 2 * margin + 0.5);
            long bsz2 = (long)(1 + 2 * margin + 0.5);
            long bsz01 = bsz0 * bsz1;

            for(long k = 0; k < bsz2; k++)
            {
                for(long j = 0; j < bsz1; j++)
                {
                    for(long i = 0; i < bsz0; i++)
                    {
                        long ii = (long)(o.x + i * a[0] + j * b[0] + k * c[0] + 0.5);
                        long jj = (long)(o.y + i * a[1] + j * b[1] + k * c[1] + 0.5);
                        long kk = (long)(o.z + i * a[2] + j * b[2] + k * c[2] + 0.5);

                        if(ii >= 0 && ii < sz0 && jj >= 0 && jj < sz1 && kk >= 0 && kk < sz2)
                        {
                            // create bounding box
                            mx = min(mx, ii);
                            my = min(my, jj);
                            mz = min(mz, kk);

                            Mx = max(Mx, ii);
                            My = max(My, jj);
                            Mz = max(Mz, kk);
                        }
                    }
                }
            }
        }

        long msz0 = Mx - mx + 1;
        long msz1 = My - my + 1;
        long msz2 = Mz - mz + 1;
        long msz01 = msz0 * msz1;
        long mtol_sz = msz2 * msz01;

        byte [] mskimg1d = new byte[(int)mtol_sz * datatype];
        Arrays.fill(mskimg1d, (byte)0);
        double max_int = 0; // maximum intensity
        double min_int = INF;
        double intensityThreshold=0;
        if (intensityThresholdmode){
            double d;
            for (long z = 0; z < msz2; z++){
                for (long y = 0; y < msz1; y++){
                    for (long x = 0; x < msz0; x++){
                        MyMarker marker = new MyMarker(mx + x, my + y, mz + z);
                        int p = (int)marker.ind(sz0, sz01);
                        int val = 0;
                        switch (datatype){
                            case 1:
                                val = ByteTranslate.byte1ToInt(inimg1d[p]);

                                break;

                            case 2:
                                byte [] b = new byte[2];
                                b[0] = inimg1d[p * 2];
                                b[1] = inimg1d[p * 2 + 1];
                                val = ByteTranslate.byte2ToInt(b, isBig);

                                break;

                            case 4:
                                b= new byte[4];
                                b[0] = inimg1d[p * 4];
                                b[1] = inimg1d[p * 4 + 1];
                                b[2] = inimg1d[p * 4 + 2];
                                b[3] = inimg1d[p * 4 + 3];
                                val = ByteTranslate.byte2ToInt(b, isBig);

                                break;

                                default:
                                    break;
                        }
                        if(val > max_int)
                            max_int = val;
                        if(val < min_int)
                            min_int = val;
                    }
                }
            }
            for (long z = 0; z < msz2; z++)
            {
                for (long y = 0; y < msz1; y++)
                {
                    for (long x = 0; x < msz0; x++)
                    {
                        MyMarker marker = new MyMarker(mx + x, my + y, mz + z);
                        MyMarker m_marker = new MyMarker(x, y, z);

                        int pm = (int)m_marker.ind(msz0, msz01);
                        int p = (int)(int)marker.ind(sz0, sz01);
                        switch (datatype){
                            case 1:
                                int val = ByteTranslate.byte1ToInt(inimg1d[p]);
                                if (val < max_int * intensityThreshold)
                                    mskimg1d[pm] = inimg1d[p];
                                break;

                            case 2:
                                byte [] b = new byte[2];
                                b[0] = inimg1d[p * 2];
                                b[1] = inimg1d[p * 2 + 1];
                                val = ByteTranslate.byte2ToInt(b, isBig);
                                if (val < max_int * intensityThreshold){
                                    mskimg1d[pm * 2] = inimg1d[p * 2];
                                    mskimg1d[pm * 2 + 1] = inimg1d[p * 2 + 1];
                                }
                                break;

                            case 4:
                                b = new byte[4];
                                b[0] = inimg1d[p * 4];
                                b[1] = inimg1d[p * 4 + 1];
                                b[2] = inimg1d[p * 4 + 2];
                                b[3] = inimg1d[p * 4 + 3];
                                val = ByteTranslate.byte2ToInt(b, isBig);
                                if (val < max_int * intensityThreshold){
                                    mskimg1d[pm * 4] = inimg1d[p * 4];
                                    mskimg1d[pm * 4 + 1] = inimg1d[p * 4 + 1];
                                    mskimg1d[pm * 4 + 2] = inimg1d[p * 4 + 2];
                                    mskimg1d[pm * 4 + 3] = inimg1d[p * 4 + 3];
                                }
                                break;

                                default:
                                    break;

                        }
//                        if(inimg1d[(int)marker.ind(sz0,sz01)] <= max_int*intensityThreshold)
//                            mskimg1d[(int)m_marker.ind(msz0, msz01)] = inimg1d[(int)marker.ind(sz0, sz01)];
                    }
                }
            }
        }
        else{
            for (long z = 0; z < msz2; z++)
            {
                for (long y = 0; y < msz1; y++)
                {
                    for (long x = 0; x < msz0; x++)
                    {
                        MyMarker marker = new MyMarker(mx + x, my + y, mz + z);
                        MyMarker m_marker = new MyMarker(x, y, z);

                        int pm = (int)m_marker.ind(msz0, msz01);
                        int p = (int)(int)marker.ind(sz0, sz01);

                        switch (datatype){
                            case 1:
                                mskimg1d[pm] = inimg1d[p];

                                break;

                            case 2:
                                mskimg1d[pm * 2] = inimg1d[p * 2];
                                mskimg1d[pm * 2 + 1] = inimg1d[p * 2 + 1];

                                break;

                            case 4:
                                mskimg1d[pm * 4] = inimg1d[p * 4];
                                mskimg1d[pm * 4 + 1] = inimg1d[p * 4 + 1];
                                mskimg1d[pm * 4 + 2] = inimg1d[p * 4 + 2];
                                mskimg1d[pm * 4 + 3] = inimg1d[p * 4 + 3];

                                break;

                                default:
                                    break;
                        }
//                        mskimg1d[(int)m_marker.ind(msz0, msz01)] = inimg1d[(int)marker.ind(sz0, sz01)];
                    }
                }
            }
        }

        nm1 = near_markers.get(0); fm1 = far_markers.get(0);
        nm2 = near_markers.lastElement(); fm2 = far_markers.lastElement();

        nm1 = new MyMarker(nm1.x - mx, nm1.y - my, nm1.z - mz);
        nm2 = new MyMarker(nm2.x - mx, nm2.y - my, nm2.z - mz);
        fm1 = new MyMarker(fm1.x - mx, fm1.y - my, fm1.z - mz);
        fm2 = new MyMarker(fm2.x - mx, fm2.y - my, fm2.z - mz);

        Vector<MyMarker> sub_markers, tar_markers;
        sub_markers = GET_LINE_MARKERS(nm1, fm1);
        tar_markers = GET_LINE_MARKERS(nm2, fm2);

        System.out.println("mx: " + mx + ", my: " + my + ", mz: " + mz);
        System.out.println("msz0: " + msz0 + ", msz1: " + msz1 + ", msz2: " + msz2);

        outswc = fastmarching_linker(sub_markers, tar_markers, mskimg1d, outswc, (int)msz0, (int)msz1, (int)msz2, cnn_type, datatype, isBig);

        if (outswc == null)
            return null;

        for(int i = 0; i < outswc.size(); i++)
        {
            outswc.get(i).x += mx;
            outswc.get(i).y += my;
            outswc.get(i).z += mz;
        }
        return outswc;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Vector<MyMarker> fastmarching_drawing_serialboxes(Vector<MyMarker> near_markers, Vector<MyMarker> far_markers,
                                                                    byte [] inimg1d, Vector<MyMarker> outswc, int sz0, int sz1, int sz2,
                                                                    int datatype, boolean isBig){


        System.out.println("welcome to fastmarching_drawing4");
        assert(near_markers.size() == far_markers.size());

        if (near_markers.isEmpty())
        {
            // no stroke points to trace; bail out early
            return null;
        }

        MyMarker nm1, nm2, fm1, fm2;
        nm2 = near_markers.get(0);
        fm2 = far_markers.get(0);
        long sz01 = (long)sz0 * sz1;

        long mx = MAX_INT, my = MAX_INT, mz = MAX_INT;
        long Mx = 0, My = 0, Mz = 0;

        for (long m = 1; m < near_markers.size(); m++){
            nm1 = nm2; fm1 = fm2;
            nm2 = near_markers.get((int)m); fm2 = far_markers.get((int)m);

            // 1. calc rt
            double [] rt = {0.0, 0.0, 0.0};
            if(nm1 != fm1)
            {
                double tx1 = fm1.x - nm1.x;
                double ty1 = fm1.y - nm1.y;
                double tz1 = fm1.z - nm1.z;
                double dst1 = sqrt(tx1 * tx1 + ty1 * ty1 + tz1 * tz1);
                rt[0] = tx1 / dst1;
                rt[1] = ty1 / dst1;
                rt[2] = tz1 / dst1;
            }
            else if(nm2 != fm2)
            {
                double tx2 = fm2.x - nm2.x;
                double ty2 = fm2.y - nm2.y;
                double tz2 = fm2.z - nm2.z;
                double dst2 = sqrt(tx2 * tx2 + ty2 * ty2 + tz2 * tz2);
                rt[0] = tx2 / dst2;
                rt[1] = ty2 / dst2;
                rt[2] = tz2 / dst2;
            }
            else
            {
                System.out.println("Error : nm1 == nm2 && fm1 == fm2");
                return null;
            }

            // 2. calc different vectors
            double [] n1n2 = {nm2.x - nm1.x, nm2.y - nm1.y, nm2.z - nm1.z};
            n1n2 = MAKE_UNIT(n1n2);
            double [] n2n1 = {-n1n2[0], -n1n2[1], -n1n2[2]};

            double [] f1f2 = {fm2.x - fm1.x, fm2.y - fm1.y, fm2.z - fm1.z};
            f1f2 = MAKE_UNIT(f1f2);
            double [] f2f1 = {-f1f2[0], -f1f2[1], -f1f2[2]};

            double [] n1f1 = {rt[0], rt[1], rt[2]};
            double [] f1n1 = {-rt[0], -rt[1], -rt[2]};

            double [] n2f2 = {rt[0], rt[1], rt[2]};
            double [] f2n2 = {-rt[0], -rt[1], -rt[2]};

            int margin = 5;

            // 1. get initial rectangel
            MyMarker [] rect = {nm1, nm2, fm2, fm1};
            double cos_n1, cos_n2, cos_f1, cos_f2;
            if((cos_n1 = COS_THETA_UNIT(n1f1, n1n2)) < 0.0)
            {
                double d = dist(nm1, nm2) * (-cos_n1);
                rect[0] = new MyMarker(nm1.x - d * rt[0], nm1.y - d * rt[1], nm1.z - d * rt[2]);
            }
            if((cos_n2 = COS_THETA_UNIT(n2f2, n2n1)) < 0.0)
            {
                double d = dist(nm1, nm2) * (-cos_n2);
                rect[1] = new MyMarker(nm2.x - d * rt[0], nm2.y - d * rt[1], nm2.z - d * rt[2]);
            }
            if((cos_f2 = COS_THETA_UNIT(f2n2, f2f1)) < 0.0)
            {
                double d = dist(fm1, fm2) * (-cos_f2);
                rect[2] = new MyMarker(fm2.x + d * rt[0], fm2.y + d * rt[1], fm2.z + d * rt[2]);
            }
            if((cos_f1 = COS_THETA_UNIT(f1n1, f1f2)) < 0.0)
            {
                double d = dist(fm1, fm2) * (-cos_f1);
                rect[3] = new MyMarker(fm1.x + d * rt[0], fm1.y + d * rt[1], fm1.z + d * rt[2]);
            }

            double [] a = new double[3];
            a[0] = rect[3].x - rect[0].x;
            a[1] = rect[3].y - rect[0].y;
            a[2] = rect[3].z - rect[0].z;
            double la = sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
            a[0] /= la; a[1] /= la; a[2] /= la;

            double []b = new double[3];
            b[0] = rect[1].x - rect[0].x;
            b[1] = rect[1].y - rect[0].y;
            b[2] = rect[1].z - rect[0].z;
            double lb = sqrt(b[0] * b[0] + b[1] * b[1] + b[2] * b[2]);
            b[0] /= lb; b[1] /= lb; b[2] /= lb;

            double [] c = new double[3];
            c[0] = a[1] * b[2] - a[2] * b[1];
            c[1] = a[2] * b[0] - a[0] * b[2];
            c[2] = a[0] * b[1] - a[1] * b[0];
            double lc = sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]);
            c[0] /= lc; c[1] /= lc; c[2] /= lc;

            MyMarker o = new MyMarker();
            o.x = rect[0].x - margin * a[0] - margin * b[0] - margin * c[0];
            o.y = rect[0].y - margin * a[1] - margin * b[1] - margin * c[1];
            o.z = rect[0].z - margin * a[2] - margin * b[2] - margin * c[2];

            long bsz0 = (long)(dist(rect[0], rect[3]) + 1 + 2 * margin + 0.5);
            long bsz1 = (long)(dist(rect[0], rect[1]) + 1 + 2 * margin + 0.5);
            long bsz2 = (long)(1 + 2 * margin + 0.5);
            long bsz01 = bsz0 * bsz1;

            for(long k = 0; k < bsz2; k++)
            {
                for(long j = 0; j < bsz1; j++)
                {
                    for(long i = 0; i < bsz0; i++)
                    {
                        long ii = (long)(o.x + i * a[0] + j * b[0] + k * c[0] + 0.5);
                        long jj = (long)(o.y + i * a[1] + j * b[1] + k * c[1] + 0.5);
                        long kk = (long)(o.z + i * a[2] + j * b[2] + k * c[2] + 0.5);

                        if(ii >= 0 && ii < sz0 && jj >= 0 && jj < sz1 && kk >= 0 && kk < sz2)
                        {
                            // create bounding box
                            mx = min(mx, ii);
                            my = min(my, jj);
                            mz = min(mz, kk);

                            Mx = max(Mx, ii);
                            My = max(My, jj);
                            Mz = max(Mz, kk);
                        }
                    }
                }
            }
        }

        long msz0 = Mx - mx + 1;
        long msz1 = My - my + 1;
        long msz2 = Mz - mz + 1;
        long msz01 = msz0 * msz1;
        long mtol_sz = msz2 * msz01;

        byte [] mskimg1d = new byte[(int)mtol_sz * datatype];
        Arrays.fill(mskimg1d, (byte)0);
        double max_int = 0; // maximum intensity
        double min_int = INF;
        double intensityThreshold=0;


        for (long z = 0; z < msz2; z++)
        {
            for (long y = 0; y < msz1; y++)
            {
                for (long x = 0; x < msz0; x++)
                {
                    MyMarker marker = new MyMarker(mx + x, my + y, mz + z);
                    MyMarker m_marker = new MyMarker(x, y, z);

                    int pm = (int)m_marker.ind(msz0, msz01);
                    int p = (int)(int)marker.ind(sz0, sz01);

                    switch (datatype){
                        case 1:
                            mskimg1d[pm] = inimg1d[p];

                            break;

                        case 2:
                            mskimg1d[pm * 2] = inimg1d[p * 2];
                            mskimg1d[pm * 2 + 1] = inimg1d[p * 2 + 1];

                            break;

                        case 4:
                            mskimg1d[pm * 4] = inimg1d[p * 4];
                            mskimg1d[pm * 4 + 1] = inimg1d[p * 4 + 1];
                            mskimg1d[pm * 4 + 2] = inimg1d[p * 4 + 2];
                            mskimg1d[pm * 4 + 3] = inimg1d[p * 4 + 3];

                            break;

                        default:
                            break;
                    }
//                        mskimg1d[(int)m_marker.ind(msz0, msz01)] = inimg1d[(int)marker.ind(sz0, sz01)];
                }
            }
        }


        nm1 = near_markers.get(0); fm1 = far_markers.get(0);
        nm2 = near_markers.lastElement(); fm2 = far_markers.lastElement();

        nm1 = new MyMarker(nm1.x - mx, nm1.y - my, nm1.z - mz);
        nm2 = new MyMarker(nm2.x - mx, nm2.y - my, nm2.z - mz);
        fm1 = new MyMarker(fm1.x - mx, fm1.y - my, fm1.z - mz);
        fm2 = new MyMarker(fm2.x - mx, fm2.y - my, fm2.z - mz);

        Vector<MyMarker> sub_markers, tar_markers;
        sub_markers = GET_LINE_MARKERS(nm1, fm1);
        tar_markers = GET_LINE_MARKERS(nm2, fm2);

        System.out.println("mx: " + mx + ", my: " + my + ", mz: " + mz);
        System.out.println("msz0: " + msz0 + ", msz1: " + msz1 + ", msz2: " + msz2);

        outswc = fastmarching_linker(sub_markers, tar_markers, mskimg1d, outswc, (int)msz0, (int)msz1, (int)msz2, 2, datatype, isBig);

        if (outswc == null)
            return null;

        for(int i = 0; i < outswc.size(); i++)
        {
            outswc.get(i).x += mx;
            outswc.get(i).y += my;
            outswc.get(i).z += mz;
        }
        return outswc;
    }

    private static Vector<MyMarker> GET_LINE_MARKERS(MyMarker marker1, MyMarker marker2){
        Set<MyMarker> marker_set = new HashSet<MyMarker>();
        Vector<MyMarker> outmarkers = new Vector<MyMarker>();
        double dst = sqrt((marker1.x - marker2.x) * (marker1.x - marker2.x) +
            (marker1.y - marker2.y) * (marker1.y - marker2.y) +
            (marker1.z - marker2.z) * (marker1.z - marker2.z));
        if(dst==0.0) outmarkers.add(marker1);
        else
        {
            double tx = (marker2.x - marker1.x) / dst;
            double ty = (marker2.y - marker1.y) / dst;
            double tz = (marker2.z - marker1.z) / dst;
            for(double r = 0.0; r <= dst; r++)
            {
                int x = (int)(marker1.x + tx * r + 0.5);
                int y = (int)(marker1.y + ty * r + 0.5);
                int z = (int)(marker1.z + tz * r + 0.5);
                marker_set.add(new MyMarker(x,y,z));
            }
            outmarkers.addAll(0, marker_set);

        }
        return outmarkers;
    }
}

class BasicHeap
{
    private Vector<HeapElemX> elems;

    public BasicHeap(){
        elems = new Vector<>(1000);
    }

    public HeapElemX delete_min(){
        if (elems.isEmpty())
            return null;

        HeapElemX min_elem = elems.get(0);

        if (elems.size() == 1)
            elems.clear();
        else{
            elems.set(0, elems.lastElement());
            elems.get(0).heap_id = 0;
            elems.remove(elems.size() - 1);
            down_heap(0);
        }
        return min_elem;
    }

    public void insert(HeapElemX t){
        elems.add(t);
        t.heap_id = elems.size() - 1;
        up_heap(t.heap_id);
    }

    public boolean empty(){
        return elems.isEmpty();
    }

    public void adjust(int id, double new_value){
        double old_value = elems.get(id).value;
        elems.get(id).value = new_value;
        if (new_value < old_value){
            up_heap(id);
        }else if (new_value > old_value){
            down_heap(id);
        }
    }

    private boolean swap_heap(int id1, int id2){
        if (id1 < 0 || id2 < 0 || id1 >= elems.size() || id2 >= elems.size()){
            return false;
        }
        if (id1 == id2) {
            return false;
        }
        int pid = id1 < id2 ? id1 : id2;
        int cid = id1 > id2 ? id1 : id2;
        assert (cid == 2*(pid+1) -1 || cid == 2*(pid+1));

        if (elems.get(pid).value <= elems.get(cid).value){
            return false;
        }else{
            HeapElemX tmp = elems.get(pid);
            elems.set(pid, elems.get(cid));
            elems.set(cid, tmp);
            elems.get(pid).heap_id = pid;
            elems.get(cid).heap_id = cid;
            return true;
        }
    }

    private void up_heap(int id){
        int pid = (id + 1) / 2 - 1;
        if (swap_heap(id, pid)) up_heap(pid);
    }

    private void down_heap(int id){
        int cid1 = 2 * (id + 1) - 1;
        int cid2 = 2 * (id + 1);
        if (cid1 >= elems.size())
            return;
        else if (cid1 == elems.size() - 1){
            swap_heap(id, cid1);
        }else if (cid1 < (elems.size() - 1)){
            int cid = elems.get(cid1).value < elems.get(cid2).value ? cid1 : cid2;
            if (swap_heap(id, cid)) down_heap(cid);
        }
    }
}
