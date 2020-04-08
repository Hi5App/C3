package com.tracingfunc.app2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

import static com.tracingfunc.app2.FM.Type.*;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class FM {
    enum Type{ALIVE,TRIAL,FAR};
    public static boolean fastmarching_dt(int[][][] inimg, float[][][] phi, int[] sz, int cnn_type, int bkg_thresh){
        //int cnn_type = 3;  // ?

        int szx = sz[0], szy = sz[1], szz = sz[2];
        int total_sz = szx*szy*szz;
        if(phi != null || phi.length>0)
            phi = null;
        phi = new float[szz][szy][szx];
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
                        phi[k][j][i] = Integer.MAX_VALUE;
                        state[k][j][i] = FAR;
                    }
                }
        }

        PriorityQueue<HeapElem> minHeap = new PriorityQueue<HeapElem>(total_sz, new Comparator<HeapElem>() {
            @Override
            public int compare(HeapElem o1, HeapElem o2) {
                return (int) Math.ceil(o1.value - o2.value);
            }
        });
        Map<int[],HeapElem> elems = new HashMap<int[], HeapElem>();

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

                                            HeapElem  elem = new HeapElem(new int[]{k2,j2,i2}, phi[k2][j2][i2]);
                                            minHeap.add(elem);
                                            elems.put(new int[]{k2,j2,i2},elem);
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

            int[] min_index = new int[3];
            for (i=0; i<min_index.length; i++){
                min_index[i] = min_elem.img_index[i];
            }
            state[min_index[0]][min_index[1]][min_index[2]] = ALIVE;

            int w,h,d;
            for(int kk=-1; kk<=1; kk++){
                d = min_index[0] + kk;
                if(d < 0 || d >= szz) continue;
                for(int jj=-1; jj<=1; jj++){
                    h = min_index[1] + jj;
                    if(h < 0 || h >= szy) continue;
                    for(int ii=-1; ii<=1; ii++){
                        w = min_index[2] + ii;
                        if(w < 0 || w >= szx) continue;
                        int offset = abs(ii) + abs(jj) + abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;

                        if(state[d][h][w] != ALIVE){
                            float new_dist = (float) (phi[min_index[0]][min_index[1]][min_index[2]]+inimg[d][h][w]*sqrt((double)offset));

                            if(state[d][h][w] == FAR){
                                phi[d][h][w] = new_dist;
                                HeapElem elem = new HeapElem(new int[]{d,h,w},phi[d][h][w]);
                                minHeap.add(elem);
                                elems.put(new int[]{d,h,w},elem);
                                state[d][h][w] = TRIAL;
                            }
                            else if(state[d][h][w] == TRIAL){
                                if(phi[d][h][w]>new_dist){
                                    phi[d][h][w] = new_dist;
                                    HeapElem elem = elems.get(new int[]{d,h,w});
                                    minHeap.remove(elem);
                                    elem.value = phi[d][h][w];
                                    minHeap.add(elem);
                                }
                            }
                        }

                    }
                }
            }
        }
        //END_CLOCK;
        return true;
    }

    public static boolean fastmarching_dt_tree(int[][][] inimg, Vector<MyMarker> outtree, int[] sz, int cnn_type, int bkg_thresh){
        int szx = sz[0], szy = sz[1], szz = sz[2];
        int total_sz = szx*szy*szz;

        float[][][] phi = new float[szz][szy][szx];
        Type[][][] state = new Type[szz][szy][szx];
        Map<int[],int[]> parnent = new HashMap<int[], int[]>();
        int k,j,i;
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
                    parnent.put(new int[]{k,j,i},new int[]{k,j,i});
                }
        }

        int bkg_count = 0;                          // for process counting
        int bdr_count = 0;                          // for process counting
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
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
                return (int) Math.ceil(o1.value - o2.value);
            }
        });
        Map<int[],HeapElem> elems = new HashMap<int[], HeapElem>();

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

                                            parnent.remove(new int[]{k2,j2,i2});
                                            parnent.put(new int[]{k2,j2,i2},min_ind);

                                            HeapElem  elem = new HeapElem(new int[]{k2,j2,i2}, phi[k2][j2][i2]);
                                            minHeap.add(elem);
                                            elems.put(new int[]{k2,j2,i2},elem);
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

            int[] min_index = new int[3];
            for (i=0; i<min_index.length; i++){
                min_index[i] = min_elem.img_index[i];
            }
            state[min_index[0]][min_index[1]][min_index[2]] = ALIVE;

            int w,h,d;
            for(int kk=-1; kk<=1; kk++){
                d = min_index[0] + kk;
                if(d < 0 || d >= szz) continue;
                for(int jj=-1; jj<=1; jj++){
                    h = min_index[1] + jj;
                    if(h < 0 || h >= szy) continue;
                    for(int ii=-1; ii<=1; ii++){
                        w = min_index[2] + ii;
                        if(w < 0 || w >= szx) continue;
                        int offset = abs(ii) + abs(jj) + abs(kk);
                        if(offset == 0 || offset > cnn_type) continue;

                        if(state[d][h][w] != ALIVE){
                            float new_dist = (float) (phi[min_index[0]][min_index[1]][min_index[2]]+inimg[d][h][w]*sqrt((double)offset));

                            if(state[d][h][w] == FAR){
                                phi[d][h][w] = new_dist;
                                HeapElem elem = new HeapElem(new int[]{d,h,w},phi[d][h][w]);
                                minHeap.add(elem);
                                elems.put(new int[]{d,h,w},elem);
                                state[d][h][w] = TRIAL;

                                parnent.remove(new int[]{d,h,w});
                                parnent.put(new int[]{d,h,w},min_index);
                            }
                            else if(state[d][h][w] == TRIAL){
                                if(phi[d][h][w]>new_dist){
                                    phi[d][h][w] = new_dist;
                                    HeapElem elem = elems.get(new int[]{d,h,w});
                                    minHeap.remove(elem);
                                    elem.value = phi[d][h][w];
                                    minHeap.add(elem);

                                    parnent.remove(new int[]{d,h,w});
                                    parnent.put(new int[]{d,h,w},min_index);
                                }
                            }
                        }

                    }
                }
            }
        }

        Map<int[],MyMarker> marke_map = new HashMap<int[], MyMarker>();
        for (k=0;k<szz;k++) {
            for (j=0;j<szy;j++)
                for (i=0;i<szx;i++)
                {
                    int[] p = parnent.get(new int[]{k,j,i});
                    if(inimg[k][j][i]<bkg_thresh || (p[0]==k && p[1]==j && p[2]==i)){
                        continue;
                    }
                    MyMarker myMarker = new MyMarker(i,j,k);
                    outtree.add(myMarker);
                    marke_map.put(new int[]{k,j,i},myMarker);
                }
        }
        for (int m=0; m<outtree.size(); m++){
            MyMarker child_marker = outtree.elementAt(m);
            int[] c = new int[]{(int) child_marker.z,(int) child_marker.y,(int) child_marker.x};
            MyMarker parent_marker = marke_map.get(parnent.get(c));
            child_marker.parent = parent_marker;
        }

        return true;
    }
}
