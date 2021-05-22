package com.penglab.hi5.basic.tracingfunc.app2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

import static com.penglab.hi5.basic.tracingfunc.app2.FM.Type.*;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class FM {
    enum Type{ALIVE,TRIAL,FAR};

    public final static double[] givals = new double[]{
            22026.5,   20368, 18840.3, 17432.5, 16134.8, 14938.4, 13834.9, 12816.8,
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
            1.00756, 1.00555, 1.00385, 1.00246, 1.00139, 1.00062, 1.00015,       1
    };

    public static double GI(int[][][] img, int x, int y, int z, double min_int, double max_int){
        return FM.givals[(int) ((img[z][y][x]-min_int)/max_int*255)];
    }

    public static double GI(float[][][] img, int x, int y, int z, double min_int, double max_int){
        return FM.givals[(int) ((img[z][y][x]-min_int)/max_int*255)];
    }

    public static boolean fastmarching_dt(int[][][] inimg, float[][][] phi, int[] sz, int cnn_type, int bkg_thresh){
        //int cnn_type = 3;  // ?

        int szx = sz[0], szy = sz[1], szz = sz[2];
        int total_sz = szx*szy*szz;
        if(phi == null){
            System.out.println("phi is null");
            return false;
        }
        Type[][][] state = new Type[szz][szy][szx];
        int bkg_count = 0;// for process counting
        int bdr_count = 0;// for process counting
        int k,j,i;


        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
                    if(inimg[k][j][i]<=bkg_thresh){
                        phi[k][j][i] = inimg[k][j][i];
                        state[k][j][i] = ALIVE;
                        bkg_count ++;
                    }
                    else {
                        phi[k][j][i] = Float.MAX_VALUE;
                        state[k][j][i] = FAR;
                    }
                }
        }

        PriorityQueue<HeapElem> minHeap = new PriorityQueue<HeapElem>(total_sz, new Comparator<HeapElem>() {
            @Override
            public int compare(HeapElem o1, HeapElem o2) {
                return (o1.value - o2.value)>0?1:-1;
            }
        });
        Map<Integer,HeapElem> elems = new HashMap<Integer, HeapElem>();

        // init heap
        {
//            i = -1; j = -1; k = -1;
            for (k=0;k<szz;k++) {
                for (j=0;j<szy;j++)
                    for (i=0;i<szx;i++)
                    {
                        if(state[k][j][i] == ALIVE){
                            for(int kk=-1; kk<=1; kk++){
                                int k2 = k+kk;
                                if(k2 < 0 || k2 >= szz) continue;
                                for(int jj=-1; jj<=1; jj++){
                                    int j2 = j+jj;
                                    if(j2 < 0 || j2 >= szy) continue;
                                    for(int ii=-1; ii<=1; ii++){
                                        int i2 = i+ii;
                                        if(i2 < 0 || i2 >= szx) continue;
                                        int offset = abs(ii) + abs(jj) + abs(kk);
                                        if(offset == 0 || offset > cnn_type) continue;

                                        int[] min_ind = {k,j,i};

                                        if(state[k2][j2][i2] == FAR){

                                            if(phi[min_ind[0]][min_ind[1]][min_ind[2]]>0.0){
                                                for(int kkk = -1; kkk <= 1; kkk++)
                                                {
                                                    int k3 = k2 + kkk;
                                                    if(k3 < 0 || k3 >= szz) continue;
                                                    for(int jjj = -1; jjj <= 1; jjj++)
                                                    {
                                                        int j3 = j2 + jjj;
                                                        if(j3 < 0 || j3 >= szy) continue;
                                                        for(int iii = -1; iii <= 1; iii++)
                                                        {
                                                            int i3 = i2 + iii;
                                                            if(i3 < 0 || i3 >= szx) continue;
                                                            int offset2 = abs(iii) + abs(jjj) + abs(kkk);
                                                            if(offset2 == 0 || offset2 > cnn_type) continue;
                                                            if(state[k3][j3][i3] == ALIVE && phi[k3][j3][i3]  < phi[min_ind[0]][min_ind[1]][min_ind[2]]) {
                                                                min_ind[0] = k3;
                                                                min_ind[1] = j3;
                                                                min_ind[2] = i3;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            phi[k2][j2][i2] = phi[min_ind[0]][min_ind[1]][min_ind[2]] + inimg[k2][j2][i2];
                                            state[k2][j2][i2] = TRIAL;

                                            HeapElem  elem = new HeapElem(k2*szy*szx+j2*szx+i2, phi[k2][j2][i2]);
                                            minHeap.add(elem);
                                            elems.put(k2*szy*szx+j2*szx+i2,elem);
                                            bdr_count++;

                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }

        System.out.println("bkg_count = "+bkg_count+"("+bkg_count/(double)(szx*szy*szz)+")");
        System.out.println("bdr_count = "+bdr_count+"("+bkg_count/(double)(szx*szy*szz)+")");
        System.out.println("elems.size() = "+elems.size());
        // loop
        while(!minHeap.isEmpty())
        {
            HeapElem min_elem = minHeap.poll();
            elems.remove(min_elem.img_index);

            int min_index = min_elem.img_index;
            int ix = min_index % szx;
            int iy = (min_index/szx) % szy;
            int iz = (min_index/(szx*szy)) %szz;

            state[iz][iy][ix] = ALIVE;

            int w,h,d;
            for(int kk=-1; kk<=1; kk++){
                d = iz + kk;
                if(d < 0 || d >= szz) continue;
                for(int jj=-1; jj<=1; jj++){
                    h = iy + jj;
                    if(h < 0 || h >= szy) continue;
                    for(int ii=-1; ii<=1; ii++){
                        w = ix + ii;
                        if(w < 0 || w >= szx) continue;
                        int offset = abs(ii) + abs(jj) + abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;

                        if(state[d][h][w] != ALIVE){
                            float new_dist = (float) (phi[iz][iy][ix]+inimg[d][h][w]*sqrt((double)offset));

                            if(state[d][h][w] == FAR){
                                phi[d][h][w] = new_dist;
                                HeapElem elem = new HeapElem(d*szy*szx+h*szx+w,phi[d][h][w]);
                                minHeap.add(elem);
                                elems.put(d*szy*szx+h*szx+w,elem);
                                state[d][h][w] = TRIAL;
                            }
                            else if(state[d][h][w] == TRIAL){
                                if(phi[d][h][w]>new_dist){
                                    phi[d][h][w] = new_dist;
                                    HeapElem elem = elems.get(d*szy*szx+h*szx+w);
                                    minHeap.remove(elem);
                                    elem.value = phi[d][h][w];
                                    minHeap.add(elem);
                                    elems.remove(d*szy*szx+h*szx+w);
                                    elems.put(d*szy*szx+h*szx+w,elem);
                                }
                            }
                        }

                    }
                }
            }
        }

        phi = null;
        state = null;
        minHeap = null;
        elems = null;

        //END_CLOCK;
        return true;
    }

    public static boolean fastmarching_dt_tree(int[][][] inimg, Vector<MyMarker> outTree, int[] sz,
                                               int cnn_type, int bkg_thresh){
        int szx = sz[0], szy = sz[1], szz = sz[2];
        int total_sz = szx*szy*szz;

        float[][][] phi = new float[szz][szy][szx];
        Type[][][] state = new Type[szz][szy][szx];
        Map<Integer,Integer> parent = new HashMap<Integer, Integer>();
        int k,j,i;
//        int[][] index = new int[total_sz][3];

        int bkg_count = 0;                          // for process counting
        int bdr_count = 0;                          // for process counting
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
//                    index[k*szy*szx+j*szx+i][0] = k;
//                    index[k*szy*szx+j*szx+i][1] = j;
//                    index[k*szy*szx+j*szx+i][2] = i;
                    parent.put(k*szy*szx+j*szx+i,k*szy*szx+j*szx+i);
                    if(inimg[k][j][i] <= bkg_thresh){
                        phi[k][j][i] = inimg[k][j][i];
                        state[k][j][i] = ALIVE;
                        bkg_count++;
                    }
                    else {
                        phi[k][j][i] = Integer.MAX_VALUE;
                        state[k][j][i] = FAR;
                    }
                }
        }

        PriorityQueue<HeapElem> minHeap = new PriorityQueue<HeapElem>(total_sz, new Comparator<HeapElem>() {
            @Override
            public int compare(HeapElem o1, HeapElem o2) {
                return (o1.value - o2.value)>0?1:-1;
            }
        });
        Map<Integer,HeapElem> elems = new HashMap<Integer, HeapElem>();

        // init heap
        {
//            i = -1; j = -1; k = -1;
            for (k=0;k<szz;k++) {
                for (j=0;j<szy;j++)
                    for (i=0;i<szx;i++)
                    {
                        if(state[k][j][i] == ALIVE){
                            for(int kk=-1; kk<=1; kk++){
                                int k2 = k+kk;
                                if(k2 < 0 || k2 >= szz) continue;
                                for(int jj=-1; jj<=1; jj++){
                                    int j2 = j+jj;
                                    if(j2 < 0 || j2 >= szy) continue;
                                    for(int ii=-1; ii<=1; ii++){

                                        int i2 = i+ii;
                                        if(i2 < 0 || i2 >= szx) continue;
                                        int offset = abs(ii) + abs(jj) + abs(kk);
                                        if(offset == 0 || offset > cnn_type) continue;

                                        if(state[k2][j2][i2] == FAR){
                                            int[] min_ind = {k,j,i};
                                            if(phi[min_ind[0]][min_ind[1]][min_ind[2]]>0.0){
                                                for(int kkk = -1; kkk <= 1; kkk++)
                                                {
                                                    int k3 = k2 + kkk;
                                                    if(k3 < 0 || k3 >= szz) continue;
                                                    for(int jjj = -1; jjj <= 1; jjj++)
                                                    {
                                                        int j3 = j2 + jjj;
                                                        if(j3 < 0 || j3 >= szy) continue;
                                                        for(int iii = -1; iii <= 1; iii++)
                                                        {
                                                            int i3 = i2 + iii;
                                                            if(i3 < 0 || i3 >= szx) continue;
                                                            int offset2 = abs(iii) + abs(jjj) + abs(kkk);
                                                            if(offset2 == 0 || offset2 > cnn_type) continue;
                                                            if(state[k3][j3][i3] == ALIVE && phi[k3][j3][i3]  < phi[min_ind[0]][min_ind[1]][min_ind[2]]) {
                                                                min_ind[0] = k3;
                                                                min_ind[1] = j3;
                                                                min_ind[2] = i3;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            phi[k2][j2][i2] = phi[min_ind[0]][min_ind[1]][min_ind[2]] + inimg[k2][j2][i2];
                                            state[k2][j2][i2] = TRIAL;

                                            parent.remove(k2*szy*szx+j2*szx+i2);
                                            parent.put(k2*szy*szx+j2*szx+i2,min_ind[0]*szy*szx+min_ind[1]*szx+min_ind[3]);

                                            HeapElem  elem = new HeapElem(k2*szy*szx+j2*szx+i2, phi[k2][j2][i2]);
                                            minHeap.add(elem);
                                            elems.put(k2*szy*szx+j2*szx+i2,elem);
                                            bdr_count++;

                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }

        System.out.println("bkg_count = "+bkg_count+"("+bkg_count/(double)(szx*szy*szz)+")");
        System.out.println("bdr_count = "+bdr_count+"("+bkg_count/(double)(szx*szy*szz)+")");
        System.out.println("elems.size() = "+elems.size());
        // loop
        while(!minHeap.isEmpty())
        {
            HeapElem min_elem = minHeap.poll();
            elems.remove(min_elem.img_index);

            int min_index = min_elem.img_index;
            int ix = min_index % szx;
            int iy = (min_index/szx) % szy;
            int iz = (min_index/(szx*szy)) %szz;

            state[iz][iy][ix] = ALIVE;

            int w,h,d;
            for(int kk=-1; kk<=1; kk++){
                d = iz + kk;
                if(d < 0 || d >= szz) continue;
                for(int jj=-1; jj<=1; jj++){
                    h = iy + jj;
                    if(h < 0 || h >= szy) continue;
                    for(int ii=-1; ii<=1; ii++){
                        w = ix + ii;
                        if(w < 0 || w >= szx) continue;
                        int offset = abs(ii) + abs(jj) + abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;

                        if(state[d][h][w] != ALIVE){
                            float new_dist = (float) (phi[iz][iy][ix]+inimg[d][h][w]*sqrt((double)offset));

                            if(state[d][h][w] == FAR){
                                phi[d][h][w] = new_dist;
                                HeapElem elem = new HeapElem(d*szy*szx+h*szx+w,phi[d][h][w]);
                                minHeap.add(elem);
                                elems.put(d*szy*szx+h*szx+w,elem);
                                state[d][h][w] = TRIAL;

                                parent.remove(d*szy*szx+h*szx+w);
                                parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                            }
                            else if(state[d][h][w] == TRIAL){
                                if(phi[d][h][w]>new_dist){
                                    phi[d][h][w] = new_dist;
                                    HeapElem elem = elems.get(d*szy*szx+h*szx+w);
                                    minHeap.remove(elem);
                                    elem.value = phi[d][h][w];
                                    minHeap.add(elem);

                                    parent.remove(d*szy*szx+h*szx+w);
                                    parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                                }
                            }
                        }

                    }
                }
            }
        }

        Map<Integer,MyMarker> marker_map = new HashMap<Integer, MyMarker>();
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
                    int p = parent.get(k*szy*szx+j*szx+i);
                    if(inimg[k][j][i]<bkg_thresh || p == k*szy*szx+j*szx+i){
                        continue;
                    }
                    MyMarker myMarker = new MyMarker(i,j,k);
                    outTree.add(myMarker);
                    marker_map.put(k*szy*szx+j*szx+i,myMarker);
                }
        }
        for (int m=0; m<outTree.size(); m++){
            MyMarker child_marker = outTree.elementAt(m);
            int[] c = new int[]{(int) child_marker.z,(int) child_marker.y,(int) child_marker.x};
            MyMarker parent_marker = marker_map.get(parent.get(c[0]*szy*szx+c[1]*szx+c[2]));
            child_marker.parent = parent_marker;
        }

        phi = null;
        state = null;
        parent = null;
        minHeap = null;
        elems = null;
        marker_map = null;

        return true;
    }

    public static boolean fastmarching_linear_tree(){
        return true;
    }

    public static boolean fastmarching_tree(MyMarker root, int[][][] inimg, Vector<MyMarker> outTree, int[] sz,
                                            int cnn_type, double bkg_thresh, boolean is_break_accept){
        int szx = sz[0], szy = sz[1], szz = sz[2];
        int total_sz = szx*szy*szz;
        System.out.println("-------------------in fm tree------------------");

        float[][][] phi = new float[szz][szy][szx];
        Type[][][] state = new Type[szz][szy][szx];
        Map<Integer,Integer> parent = new HashMap<Integer, Integer>();
        //GI parameter min_int, max_int, li
        double max_int = 0;
        double min_int = Integer.MAX_VALUE;
        double li = 10;

        System.out.println("---------------before index---------------------");
//        int[] index = new int[total_sz];

        System.out.println("---------------after index---------------------");
        int k,j,i;
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
//                    index[k*szy*szx+j*szx+i] = k*szx*szy + j*szx + i;
                    if(inimg[k][j][i]>max_int) max_int = inimg[k][j][i];
                    if(inimg[k][j][i]<min_int) min_int = inimg[k][j][i];
                    phi[k][j][i] = Integer.MAX_VALUE;
                    state[k][j][i] = FAR;
                    parent.put(k*szx*szy + j*szx + i,k*szx*szy + j*szx + i);
                }
        }
        max_int -= min_int;
        System.out.println("-------------------memory-----------------------");

        // initialization

        // init state and phi for root
        int rootx = (int) (root.x +0.5);
        int rooty = (int) (root.y +0.5);
        int rootz = (int) (root.z +0.5);

        state[rootz][rooty][rootx] = ALIVE;
        phi[rootz][rooty][rootx] = 0.0f;

        PriorityQueue<HeapElemX> minHeap = new PriorityQueue<HeapElemX>(total_sz, new Comparator<HeapElemX>() {
            @Override
            public int compare(HeapElemX o1, HeapElemX o2) {
                return (o1.value - o2.value)>0?1:-1;
            }
        });
        Map<Integer,HeapElemX> elems = new HashMap<Integer, HeapElemX>();

        // init heap
        {
//            int[] img_index = new int[]{rootz,rooty,rootx};
//            int[] prev_index = new int[]{rootz,rooty,rootx};
            HeapElemX elem = new HeapElemX(rootz*szy*szx+rooty*szx+rootx,rootz*szy*szx+rooty*szx+rootx,phi[rootz][rooty][rootx]);
            minHeap.add(elem);
            elems.put(rootz*szy*szx+rooty*szx+rootx,elem);
        }

        System.out.println("-------------------initial end-----------------------");

        // loop
        while(!minHeap.isEmpty())
        {
            HeapElemX min_elem = minHeap.poll();
            elems.remove(min_elem.img_index);

            int min_index;
            int prev_index;
            min_index = min_elem.img_index;
            prev_index = min_elem.prev_index;


            parent.remove(min_index);
            parent.put(min_index,prev_index);
            int ix = min_index % szx;
            int iy = (min_index/szx) % szy;
            int iz = (min_index/(szx*szy)) %szz;

            state[iz][iy][ix] = ALIVE;

            int w,h,d;
            for(int kk=-1; kk<=1; kk++){
                d = iz + kk;
                if(d < 0 || d >= szz) continue;
                for(int jj=-1; jj<=1; jj++){
                    h = iy + jj;
                    if(h < 0 || h >= szy) continue;
                    for(int ii=-1; ii<=1; ii++){
                        w = ix + ii;
                        if(w < 0 || w >= szx) continue;
                        int offset = abs(ii) + abs(jj) + abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;
                        double factor = (offset == 1) ? 1.0 : ((offset == 2) ? 1.414214 : ((offset == 3) ? 1.732051 : 0.0));
                        if(is_break_accept){
                            if(inimg[d][h][w] <= bkg_thresh && inimg[iz][iy][ix] <=bkg_thresh)
                                continue;
                        }
                        else {
                            if(inimg[d][h][w] <= bkg_thresh)
                                continue;
                        }
                        if(state[d][h][w] != ALIVE){
                            float new_dist = (float) (phi[iz][iy][ix] +
                                    (GI(inimg,w,h,d,min_int,max_int) + GI(inimg,ix,iy,iz,min_int,max_int))*factor*0.5);
                            int prev_index1 = min_index;

                            if(state[d][h][w] == FAR){
                                phi[d][h][w] = new_dist;
                                HeapElemX elem = new HeapElemX(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix,phi[d][h][w]);
                                minHeap.add(elem);
                                elems.put(d*szy*szx+h*szx+w,elem);
                                state[d][h][w] = TRIAL;

                                parent.remove(d*szy*szx+h*szx+w);
                                parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                            }
                            else if(state[d][h][w] == TRIAL){
                                if(phi[d][h][w]>new_dist){
                                    phi[d][h][w] = new_dist;
                                    HeapElemX elem = elems.get(d*szy*szx+h*szx+w);
                                    minHeap.remove(elem);
                                    elem.value = phi[d][h][w];
                                    elem.setPrev_index(iz*szy*szx+iy*szx+ix);
                                    minHeap.add(elem);

                                    parent.remove(d*szy*szx+h*szx+w);
                                    parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                                }
                            }
                        }

                    }
                }
            }
        }

        System.out.println("--------------------------loop end------------------------");

        Map<Integer,MyMarker> marker_map = new HashMap<Integer, MyMarker>();
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
//                    int p = parent.get(k*szy*szx+j*szx+i);
                    if(state[k][j][i] != ALIVE){
                        continue;
                    }
                    MyMarker myMarker = new MyMarker(i,j,k);
                    outTree.add(myMarker);
                    marker_map.put(k*szy*szx+j*szx+i,myMarker);
                }
        }
        for (int m=0; m<outTree.size(); m++){
            MyMarker child_marker = outTree.elementAt(m);
            int[] c = new int[]{(int) child_marker.z,(int) child_marker.y,(int) child_marker.x};
            MyMarker parent_marker = marker_map.get(parent.get(c[0]*szy*szx+c[1]*szx+c[2]));
            if(!child_marker.equals(parent_marker))
                child_marker.parent = parent_marker;
            else
                child_marker.parent = null;
        }

        phi = null;
        state = null;
        parent = null;
        minHeap = null;
        elems = null;
        marker_map = null;

        System.out.println("----------------------------convert-----------------------------");
        return true;
    }

    public static boolean fastmarching_tree(MyMarker root, float[][][] inimg, Vector<MyMarker> outTree, int[] sz,
                                            int cnn_type, double bkg_thresh, boolean is_break_accept){
        int szx = sz[0], szy = sz[1], szz = sz[2];
        int total_sz = szx*szy*szz;
        System.out.println("-------------------in fm tree------------------");

        float[][][] phi = new float[szz][szy][szx];
        Type[][][] state = new Type[szz][szy][szx];
        Map<Integer,Integer> parent = new HashMap<Integer, Integer>();
        //GI parameter min_int, max_int, li
        double max_int = 0;
        double min_int = Double.MAX_VALUE;
        double li = 10;

        int k,j,i;
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
//                    index[k*szy*szx+j*szx+i] = k*szx*szy + j*szx + i;
                    if(inimg[k][j][i]>max_int) max_int = inimg[k][j][i];
                    if(inimg[k][j][i]<min_int) min_int = inimg[k][j][i];
                    phi[k][j][i] = Float.MAX_VALUE;
                    state[k][j][i] = FAR;
                    parent.put(k*szx*szy + j*szx + i,k*szx*szy + j*szx + i);
                }
        }
        max_int -= min_int;
        System.out.println("-------------------memory-----------------------");

        // initialization

        // init state and phi for root
        int rootx = (int) (root.x +0.5);
        int rooty = (int) (root.y +0.5);
        int rootz = (int) (root.z +0.5);

        state[rootz][rooty][rootx] = ALIVE;
        phi[rootz][rooty][rootx] = 0.0f;

        PriorityQueue<HeapElemX> minHeap = new PriorityQueue<HeapElemX>(total_sz, new Comparator<HeapElemX>() {
            @Override
            public int compare(HeapElemX o1, HeapElemX o2) {
                return (o1.value - o2.value)>0?1:-1;
            }
        });
        Map<Integer,HeapElemX> elems = new HashMap<Integer, HeapElemX>();

        // init heap
        {
//            int[] img_index = new int[]{rootz,rooty,rootx};
//            int[] prev_index = new int[]{rootz,rooty,rootx};
            HeapElemX elem = new HeapElemX(rootz*szy*szx+rooty*szx+rootx,rootz*szy*szx+rooty*szx+rootx,phi[rootz][rooty][rootx]);
            minHeap.add(elem);
            elems.put(rootz*szy*szx+rooty*szx+rootx,elem);
        }

        System.out.println("-------------------initial end-----------------------");

        // loop
        while(!minHeap.isEmpty())
        {
            HeapElemX min_elem = minHeap.poll();
            elems.remove(min_elem.img_index);

            int min_index;
            int prev_index;
            min_index = min_elem.img_index;
            prev_index = min_elem.prev_index;


            parent.remove(min_index);
            parent.put(min_index,prev_index);
            int ix = min_index % szx;
            int iy = (min_index/szx) % szy;
            int iz = (min_index/(szx*szy)) %szz;

            state[iz][iy][ix] = ALIVE;

            int w,h,d;
            for(int kk=-1; kk<=1; kk++){
                d = iz + kk;
                if(d < 0 || d >= szz) continue;
                for(int jj=-1; jj<=1; jj++){
                    h = iy + jj;
                    if(h < 0 || h >= szy) continue;
                    for(int ii=-1; ii<=1; ii++){
                        w = ix + ii;
                        if(w < 0 || w >= szx) continue;
                        int offset = abs(ii) + abs(jj) + abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;
                        double factor = (offset == 1) ? 1.0 : ((offset == 2) ? 1.414214 : ((offset == 3) ? 1.732051 : 0.0));
                        if(is_break_accept){
                            if(inimg[d][h][w] <= bkg_thresh && inimg[iz][iy][ix] <=bkg_thresh)
                                continue;
                        }
                        else {
                            if(inimg[d][h][w] <= bkg_thresh)
                                continue;
                        }
                        if(state[d][h][w] != ALIVE){
                            float new_dist = (float) (phi[iz][iy][ix] +
                                    (GI(inimg,w,h,d,min_int,max_int) + GI(inimg,ix,iy,iz,min_int,max_int))*factor*0.5);
                            int prev_index1 = min_index;

                            if(state[d][h][w] == FAR){
                                phi[d][h][w] = new_dist;
                                HeapElemX elem = new HeapElemX(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix,phi[d][h][w]);
                                minHeap.add(elem);
                                elems.put(d*szy*szx+h*szx+w,elem);
                                state[d][h][w] = TRIAL;

                                parent.remove(d*szy*szx+h*szx+w);
                                parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                            }
                            else if(state[d][h][w] == TRIAL){
                                if(phi[d][h][w]>new_dist){
                                    phi[d][h][w] = new_dist;
                                    HeapElemX elem = elems.get(d*szy*szx+h*szx+w);
                                    minHeap.remove(elem);
                                    elem.value = phi[d][h][w];
                                    elem.setPrev_index(iz*szy*szx+iy*szx+ix);
                                    minHeap.add(elem);

                                    parent.remove(d*szy*szx+h*szx+w);
                                    parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                                }
                            }
                        }

                    }
                }
            }
        }

        System.out.println("--------------------------loop end------------------------");

        Map<Integer,MyMarker> marker_map = new HashMap<Integer, MyMarker>();
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
//                    int p = parent.get(k*szy*szx+j*szx+i);
                    if(state[k][j][i] != ALIVE){
                        continue;
                    }
                    MyMarker myMarker = new MyMarker(i,j,k);
                    outTree.add(myMarker);
                    marker_map.put(k*szy*szx+j*szx+i,myMarker);
                }
        }
        for (int m=0; m<outTree.size(); m++){
            MyMarker child_marker = outTree.elementAt(m);
            int[] c = new int[]{(int) child_marker.z,(int) child_marker.y,(int) child_marker.x};
            MyMarker parent_marker = marker_map.get(parent.get(c[0]*szy*szx+c[1]*szx+c[2]));
            if(!child_marker.equals(parent_marker))
                child_marker.parent = parent_marker;
            else
                child_marker.parent = null;
        }
        System.out.println("----------------------------convert-----------------------------");
        return true;
    }

    public static boolean fastmarching_tree(MyMarker root, Vector<MyMarker> target, int[][][] inimg,
                                            Vector<MyMarker> outTree, int[] sz, int cnn_type, double bkg_thresh, boolean is_break_accept){
        int szx = sz[0], szy = sz[1], szz = sz[2];
        int total_sz = szx*szy*szz;
        System.out.println("-------------------in fm tree------------------");

        float[][][] phi = new float[szz][szy][szx];
        Type[][][] state = new Type[szz][szy][szx];
        Map<Integer,Integer> parent = new HashMap<Integer, Integer>();
        //GI parameter min_int, max_int, li
        double max_int = 0;
        double min_int = Integer.MAX_VALUE;
        double li = 10;

        int k,j,i;
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
//                    index[k*szy*szx+j*szx+i] = k*szx*szy + j*szx + i;
                    if(inimg[k][j][i]>max_int) max_int = inimg[k][j][i];
                    if(inimg[k][j][i]<min_int) min_int = inimg[k][j][i];
                    phi[k][j][i] = Integer.MAX_VALUE;
                    state[k][j][i] = FAR;
                    parent.put(k*szx*szy + j*szx + i,k*szx*szy + j*szx + i);
                }
        }
        max_int -= min_int;
        System.out.println("-------------------memory-----------------------");

        // initialization

        // init state and phi for root
        int rootx = (int) (root.x +0.5);
        int rooty = (int) (root.y +0.5);
        int rootz = (int) (root.z +0.5);

        state[rootz][rooty][rootx] = ALIVE;
        phi[rootz][rooty][rootx] = 0.0f;

        int[] target_index = new int[target.size()];
        for(i=0;i<target.size(); i++){
            target_index[i] = (int) (target.elementAt(i).z + 0.5)*szx*szy +
                    (int) (target.elementAt(i).y + 0.5)*szx +
                    (int) (target.elementAt(i).x + 0.5);
        }
        System.out.println("----------------ddddddd---------------------------");

        PriorityQueue<HeapElemX> minHeap = new PriorityQueue<HeapElemX>(total_sz, new Comparator<HeapElemX>() {
            @Override
            public int compare(HeapElemX o1, HeapElemX o2) {
                return (o1.value - o2.value)>0?1:-1;
            }
        });
        Map<Integer,HeapElemX> elems = new HashMap<Integer, HeapElemX>();

        // init heap
        {
//            int[] img_index = new int[]{rootz,rooty,rootx};
//            int[] prev_index = new int[]{rootz,rooty,rootx};
            HeapElemX elem = new HeapElemX(rootz*szy*szx+rooty*szx+rootx,rootz*szy*szx+rooty*szx+rootx,phi[rootz][rooty][rootx]);
            minHeap.add(elem);
            elems.put(rootz*szy*szx+rooty*szx+rootx,elem);
        }

        System.out.println("-------------------initial end-----------------------");

        // loop
        int time_counter = 1;
//        double process1 = 0;

        boolean is_break = true;
        while(!minHeap.isEmpty())
        {
//            double process2 = (time_counter++)*100000.0/total_sz;
//            if(process2 - process1>=1){
//                boolean is_break = true;
//                for(int t=0; t<target_index.length; t++){
//                    int index_t = target_index[t];
//                    int ptind = parent.get(index_t);
//                    if(ptind == index_t &&
//                            (ptind != rootz*szy*szx+rooty*szx+rootx)){
//                        is_break = false;
//                        break;
//                    }
//                }
//                if(is_break)
//                    break;
//            }

            is_break = true;
            for(int t=0; t<target_index.length; t++){
                int index_t = target_index[t];
                int ptind = parent.get(index_t);
                if(ptind == index_t &&
                        (ptind != rootz*szy*szx+rooty*szx+rootx)){
                    is_break = false;
                    break;
                }
            }
            if(is_break)
                break;


            HeapElemX min_elem = minHeap.poll();
            elems.remove(min_elem.img_index);

            int min_index;
            int prev_index;
            min_index = min_elem.img_index;
            prev_index = min_elem.prev_index;


            parent.remove(min_index);
            parent.put(min_index,prev_index);
            int ix = min_index % szx;
            int iy = (min_index/szx) % szy;
            int iz = (min_index/(szx*szy)) %szz;

            state[iz][iy][ix] = ALIVE;

            int w,h,d;
            for(int kk=-1; kk<=1; kk++){
                d = iz + kk;
                if(d < 0 || d >= szz) continue;
                for(int jj=-1; jj<=1; jj++){
                    h = iy + jj;
                    if(h < 0 || h >= szy) continue;
                    for(int ii=-1; ii<=1; ii++){
                        w = ix + ii;
                        if(w < 0 || w >= szx) continue;
                        int offset = abs(ii) + abs(jj) + abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;
                        double factor = (offset == 1) ? 1.0 : ((offset == 2) ? 1.414214 : ((offset == 3) ? 1.732051 : 0.0));

                        if(is_break_accept){
                            if(inimg[d][h][w] <= bkg_thresh && inimg[iz][iy][ix] <=bkg_thresh)
                                continue;
                        }
                        else {
                            if(inimg[d][h][w] <= bkg_thresh)
                                continue;
                        }

                        if(state[d][h][w] != ALIVE){
                            float new_dist = (float) (phi[iz][iy][ix] +
                                    (GI(inimg,w,h,d,min_int,max_int) + GI(inimg,ix,iy,iz,min_int,max_int))*factor*0.5);
                            int prev_index1 = min_index;

                            if(state[d][h][w] == FAR){
                                phi[d][h][w] = new_dist;
                                HeapElemX elem = new HeapElemX(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix,phi[d][h][w]);
                                minHeap.add(elem);
                                elems.put(d*szy*szx+h*szx+w,elem);
                                state[d][h][w] = TRIAL;

                                parent.remove(d*szy*szx+h*szx+w);
                                parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                            }
                            else if(state[d][h][w] == TRIAL){
                                if(phi[d][h][w]>new_dist){
                                    phi[d][h][w] = new_dist;
                                    HeapElemX elem = elems.get(d*szy*szx+h*szx+w);
                                    minHeap.remove(elem);
                                    elem.value = phi[d][h][w];
                                    elem.setPrev_index(iz*szy*szx+iy*szx+ix);
                                    minHeap.add(elem);

                                    parent.remove(d*szy*szx+h*szx+w);
                                    parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                                }
                            }
                        }

                    }
                }
            }
        }

        System.out.println("--------------------------loop end------------------------");

        Map<Integer,MyMarker> marker_map = new HashMap<Integer, MyMarker>();
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
//                    int p = parent.get(k*szy*szx+j*szx+i);
                    if(state[k][j][i] != ALIVE){
                        continue;
                    }
                    MyMarker myMarker = new MyMarker(i,j,k);
                    outTree.add(myMarker);
                    marker_map.put(k*szy*szx+j*szx+i,myMarker);
                }
        }
        for (int m=0; m<outTree.size(); m++){
            MyMarker child_marker = outTree.elementAt(m);
            int[] c = new int[]{(int) child_marker.z,(int) child_marker.y,(int) child_marker.x};
            MyMarker parent_marker = marker_map.get(parent.get(c[0]*szy*szx+c[1]*szx+c[2]));
            if(!child_marker.equals(parent_marker))
                child_marker.parent = parent_marker;
            else
                child_marker.parent = null;
        }

        phi = null;
        state = null;
        parent = null;
        minHeap = null;
        elems = null;
        marker_map = null;

        System.out.println("----------------------------convert-----------------------------");
        return true;



//        Map<Integer,MyMarker> marker_map = new HashMap<Integer, MyMarker>();
//        for(int t=0; t<target_index.length; t++){
//            int tind = target_index[t];
//            int p = tind;
//            while (true) {
//                if(marker_map.containsKey(p)) break;
//                int ix = p % szx;
//                int iy = p/szx % szy;
//                int iz = p/(szx*szy) % szz;
//                MyMarker marker = new MyMarker(ix,iy,iz);
////                marker_map.remove(p);
//                marker_map.put(p,marker);
//
//                if(p == parent.get(p))
//                {
////                    assert(p == root.ind(sz0,sz01));
////                    assert(marker_map.find(root.ind(sz0,sz01)) != marker_map.end());
//                    break;
//                }
//                else p = parent.get(p);
//            }
//        }
//
//        if(marker_map.containsKey(rootz*szy*szx+rooty*szx+rootx)){
//            System.out.println("break here");
//        }
//        else{
//            System.out.println("no root");
//        }
//
//        for(int m : marker_map.keySet()){
//            MyMarker marker = marker_map.get(m);
//            MyMarker parent_marker = marker_map.get(parent.get(m));
//            if(!marker.equals(parent_marker))
//                marker.parent = parent_marker;
//            else
//                marker.parent = null;
//            outTree.add(marker);
//        }
//
//
//        System.out.println("----------------------------convert-----------------------------");
//        return true;
    }

    public static boolean fastmarching_tree(MyMarker root, Vector<MyMarker> target, float[][][] inimg,
                                            Vector<MyMarker> outTree, int[] sz, int cnn_type, double bkg_thresh, boolean is_break_accept){
        int szx = sz[0], szy = sz[1], szz = sz[2];
        int total_sz = szx*szy*szz;
        System.out.println("-------------------in fm tree------------------");

        float[][][] phi = new float[szz][szy][szx];
        Type[][][] state = new Type[szz][szy][szx];
        Map<Integer,Integer> parent = new HashMap<Integer, Integer>();
        //GI parameter min_int, max_int, li
        double max_int = 0;
        double min_int = Integer.MAX_VALUE;
        double li = 10;

        int k,j,i;
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
//                    index[k*szy*szx+j*szx+i] = k*szx*szy + j*szx + i;
                    if(inimg[k][j][i]>max_int) max_int = inimg[k][j][i];
                    if(inimg[k][j][i]<min_int) min_int = inimg[k][j][i];
                    phi[k][j][i] = Integer.MAX_VALUE;
                    state[k][j][i] = FAR;
                    parent.put(k*szx*szy + j*szx + i,k*szx*szy + j*szx + i);
                }
        }
        max_int -= min_int;
        System.out.println("-------------------memory-----------------------");

        // initialization

        // init state and phi for root
        int rootx = (int) (root.x +0.5);
        int rooty = (int) (root.y +0.5);
        int rootz = (int) (root.z +0.5);

        state[rootz][rooty][rootx] = ALIVE;
        phi[rootz][rooty][rootx] = 0.0f;

        int[] target_index = new int[target.size()];
        for(i=0;i<target.size(); i++){
            target_index[i] = (int) (target.elementAt(i).z + 0.5)*szx*szy +
                    (int) (target.elementAt(i).y + 0.5)*szx +
                    (int) (target.elementAt(i).x + 0.5);
        }
        System.out.println("----------------ddddddd---------------------------");

        PriorityQueue<HeapElemX> minHeap = new PriorityQueue<HeapElemX>(total_sz, new Comparator<HeapElemX>() {
            @Override
            public int compare(HeapElemX o1, HeapElemX o2) {
                return (o1.value - o2.value)>0?1:-1;
            }
        });
        Map<Integer,HeapElemX> elems = new HashMap<Integer, HeapElemX>();

        // init heap
        {
//            int[] img_index = new int[]{rootz,rooty,rootx};
//            int[] prev_index = new int[]{rootz,rooty,rootx};
            HeapElemX elem = new HeapElemX(rootz*szy*szx+rooty*szx+rootx,rootz*szy*szx+rooty*szx+rootx,phi[rootz][rooty][rootx]);
            minHeap.add(elem);
            elems.put(rootz*szy*szx+rooty*szx+rootx,elem);
        }

        System.out.println("-------------------initial end-----------------------");

        // loop
        int time_counter = 1;
        double process1 = 0;

        boolean is_break = true;

        while(!minHeap.isEmpty())
        {
//            double process2 = (time_counter++)*100000.0/total_sz;
//            if(process2 - process1>=1){
//                boolean is_break = true;
//                for(int t=0; t<target_index.length; t++){
//                    int index_t = target_index[t];
//                    int ptind = parent.get(index_t);
//                    if(ptind == index_t &&
//                            (ptind != rootz*szy*szx+rooty*szx+rootx)){
//                        is_break = false;
//                        break;
//                    }
//                }
//                if(is_break)
//                    break;
//            }

            is_break = true;
            for(int t=0; t<target_index.length; t++){
                int index_t = target_index[t];
                int ptind = parent.get(index_t);
                if(ptind == index_t &&
                        (ptind != rootz*szy*szx+rooty*szx+rootx)){
                    is_break = false;
                    break;
                }
            }
            if(is_break)
                break;


            HeapElemX min_elem = minHeap.poll();
            elems.remove(min_elem.img_index);

            int min_index;
            int prev_index;
            min_index = min_elem.img_index;
            prev_index = min_elem.prev_index;


            parent.remove(min_index);
            parent.put(min_index,prev_index);
            int ix = min_index % szx;
            int iy = (min_index/szx) % szy;
            int iz = (min_index/(szx*szy)) %szz;

            state[iz][iy][ix] = ALIVE;

            int w,h,d;
            for(int kk=-1; kk<=1; kk++){
                d = iz + kk;
                if(d < 0 || d >= szz) continue;
                for(int jj=-1; jj<=1; jj++){
                    h = iy + jj;
                    if(h < 0 || h >= szy) continue;
                    for(int ii=-1; ii<=1; ii++){
                        w = ix + ii;
                        if(w < 0 || w >= szx) continue;
                        int offset = abs(ii) + abs(jj) + abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;
                        double factor = (offset == 1) ? 1.0 : ((offset == 2) ? 1.414214 : ((offset == 3) ? 1.732051 : 0.0));
                        if(is_break_accept){
                            if(inimg[d][h][w] <= bkg_thresh && inimg[iz][iy][ix] <=bkg_thresh)
                                continue;
                        }
                        else {
                            if(inimg[d][h][w] <= bkg_thresh)
                                continue;
                        }
                        if(state[d][h][w] != ALIVE){
                            float new_dist = (float) (phi[iz][iy][ix] +
                                    (GI(inimg,w,h,d,min_int,max_int) + GI(inimg,ix,iy,iz,min_int,max_int))*factor*0.5);
                            int prev_index1 = min_index;

                            if(state[d][h][w] == FAR){
                                phi[d][h][w] = new_dist;
                                HeapElemX elem = new HeapElemX(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix,phi[d][h][w]);
                                minHeap.add(elem);
                                elems.put(d*szy*szx+h*szx+w,elem);
                                state[d][h][w] = TRIAL;

                                parent.remove(d*szy*szx+h*szx+w);
                                parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                            }
                            else if(state[d][h][w] == TRIAL){
                                if(phi[d][h][w]>new_dist){
                                    phi[d][h][w] = new_dist;
                                    HeapElemX elem = elems.get(d*szy*szx+h*szx+w);
                                    minHeap.remove(elem);
                                    elem.value = phi[d][h][w];
                                    elem.setPrev_index(iz*szy*szx+iy*szx+ix);
                                    minHeap.add(elem);

                                    parent.remove(d*szy*szx+h*szx+w);
                                    parent.put(d*szy*szx+h*szx+w,iz*szy*szx+iy*szx+ix);
                                }
                            }
                        }

                    }
                }
            }
        }

        System.out.println("--------------------------loop end------------------------");

        Map<Integer,MyMarker> marker_map = new HashMap<Integer, MyMarker>();
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
//                    int p = parent.get(k*szy*szx+j*szx+i);
                    if(state[k][j][i] != ALIVE){
                        continue;
                    }
                    MyMarker myMarker = new MyMarker(i,j,k);
                    outTree.add(myMarker);
                    marker_map.put(k*szy*szx+j*szx+i,myMarker);
                }
        }
        for (int m=0; m<outTree.size(); m++){
            MyMarker child_marker = outTree.elementAt(m);
            int[] c = new int[]{(int) child_marker.z,(int) child_marker.y,(int) child_marker.x};
            MyMarker parent_marker = marker_map.get(parent.get(c[0]*szy*szx+c[1]*szx+c[2]));
            if(!child_marker.equals(parent_marker))
                child_marker.parent = parent_marker;
            else
                child_marker.parent = null;
        }


        phi = null;
        state = null;
        parent = null;
        minHeap = null;
        elems = null;
        marker_map = null;

        System.out.println("----------------------------convert-----------------------------");
        return true;

//        Map<Integer,MyMarker> marker_map = new HashMap<Integer, MyMarker>();
//        for(int t=0; t<target_index.length; t++){
//            int tind = target_index[t];
//            int p = tind;
//            while (true) {
//                if(marker_map.containsKey(p)) break;
//                int ix = p % szx;
//                int iy = p/szx % szy;
//                int iz = p/(szx*szy) % szz;
//                MyMarker marker = new MyMarker(ix,iy,iz);
////                marker_map.remove(p);
//                marker_map.put(p,marker);
//
//                if(p == parent.get(p))
//                {
////                    assert(p == root.ind(sz0,sz01));
////                    assert(marker_map.find(root.ind(sz0,sz01)) != marker_map.end());
//                    break;
//                }
//                else p = parent.get(p);
//            }
//        }
//
//        if(marker_map.containsKey(rootz*szy*szx+rooty*szx+rootx)){
//            System.out.println("break here");
//        }
//        else{
//            System.out.println("no root");
//        }
//
//        for(int m : marker_map.keySet()){
//            MyMarker marker = marker_map.get(m);
//            MyMarker parent_marker = marker_map.get(parent.get(m));
//            if(!marker.equals(parent_marker))
//                marker.parent = parent_marker;
//            else
//                marker.parent = null;
//            outTree.add(marker);
//        }
//
//        System.out.println("----------------------------convert-----------------------------");
//        return true;
    }




}
