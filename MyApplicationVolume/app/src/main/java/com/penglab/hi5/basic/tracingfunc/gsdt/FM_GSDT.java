package com.penglab.hi5.basic.tracingfunc.gsdt;

import com.penglab.hi5.basic.tracingfunc.app2.HeapElem;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class FM_GSDT {
    enum Type {ALIVE, TRIAL, FAR};

    public static boolean gsdt_FM(int[][][] inimg, float[][][] phi, int[] sz, int cnn_type, int bkg_thresh) {
        int xdim = sz[0], ydim = sz[1], zdim = sz[2];
        System.out.println("Enter here gsdt_FM");
        int total_pix = xdim * ydim * zdim;
        if (phi == null) {
            System.out.println("phi is null");
            return false;
        }
        Type[][][] state = new Type[zdim][ydim][xdim];
        int i, j, k;
        //check para
        int forcounter = 0;
        int nbkg = 0;
        int bkg = 0;
        //System.out.println(inimg[0][0][0]);


        //Divide the initial background points according to the para bkg_thresh
        for (k = 0; k < zdim; k++) {
            for (j = 0; j < ydim; j++) {
                for (i = 0; i < xdim; i++) {
                    if (inimg[k][j][i] <= bkg_thresh) {
                        phi[k][j][i] = inimg[k][j][i];
                        //System.out.println(phi[k][j][i]);
                        state[k][j][i] = Type.ALIVE;
                        bkg++;
                        //System.out.println(bkg);
                    }
                    else {
                        phi[k][j][i] = Float.MAX_VALUE;
                        state[k][j][i] = Type.FAR;
                        forcounter++;
                       // System.out.println("xyz:"+k+j+i);
                    }
                }
            }
        }

        //System.out.println("前景数："+forcounter);
        //System.out.println("背景数："+bkg);
        //System.out.println("背景："+state[0][0][0]);
        //System.out.println("Enter here gsdt_FM0");

        //Create a priorityqueue to save temp Trival
        PriorityQueue<HeapElem> minHeap = new PriorityQueue<HeapElem>(total_pix, new Comparator<HeapElem>() {
            @Override
            public int compare(HeapElem o1, HeapElem o2) {
                return (o1.value - o2.value)>0?1:-1;
            }
        });
        Map<Integer,HeapElem> elems = new HashMap<Integer, HeapElem>();

        //Heap initialization
        {
            for (k = 0; k < zdim; k++) {
                for (j = 0; j < ydim; j++) {
                    for (i = 0; i < xdim; i++) {
                        if (state[k][j][i] == Type.ALIVE) {
                            //System.out.println("run here-------");
                            for (int dk = -1; dk <= 1; dk++) {
                                int k1 = k + dk;
                                if (k1 < 0 || k1 >= zdim) continue;
                                for (int dj = -1; dj <= 1; dj++) {
                                    int j1 = j + dj;
                                    if (j1 < 0 || j1 >= ydim) continue;
                                    for (int di = -1; di <= 1; di++) {
                                        int i1 = i + di;
                                        if (i1 < 0 || i1 >= xdim) continue;
                                        int offset = abs(di) + abs(dj) + abs(dk); //the coordinate offset between two point
                                        if (offset == 0 || offset > cnn_type)
                                            continue;//limit the search area size??
                                        int[] min_ind = {k, j, i};
                                        if (state[k1][j1][i1] == Type.FAR) {
                                            if (phi[min_ind[0]][min_ind[1]][min_ind[2]] > 0.0) {
                                                for (int dkk = -1; dkk <= 1; dkk++) {
                                                    int k2 = k1 + dkk;
                                                    if (k2 < 0 || k2 >= zdim) continue;
                                                    for (int djj = -1; djj <= 1; djj++) {
                                                        int j2 = j1 + djj;
                                                        if (j2 < 0 || j2 >= ydim) continue;
                                                        for (int dii = -1; dii <= 1; dii++) {
                                                            int i2 = i1 + dii;
                                                            if (i2 < 0 || i2 >= xdim) continue;
                                                            int offset1 = abs(dkk) + abs(djj) + abs(dii); //the coordinate offset between two point
                                                            if (offset1 == 0 || offset1 > cnn_type)
                                                                continue;
                                                            if (state[k2][j2][i2] == Type.ALIVE && phi[k2][j2][i2] < phi[min_ind[0]][min_ind[1]][min_ind[2]]) {
                                                                min_ind[0] = k2;
                                                                min_ind[1] = j2;
                                                                min_ind[2] = i2;
                                                                //System.out.println("run here!!!!");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            phi[k1][j1][i1] = phi[min_ind[0]][min_ind[1]][min_ind[2]] + inimg[k1][j1][i1];
                                            state[k1][j1][i1] = Type.TRIAL;
                                            HeapElem elem = new HeapElem(k1 * ydim * xdim + j1 * xdim + i1, phi[k1][j1][i1]);
                                            minHeap.add(elem);
                                            elems.put(k1 * ydim * xdim + j1 * xdim + i1, elem);
                                            nbkg++;
                                            //System.out.println("new_TRIALtemp："+nbkg);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("new_TRIAL："+nbkg);
       // System.out.println("elems.size() = "+elems.size());
        System.out.println("Enter here gsdt_FM1");
        //FM_gsdt
        while(!minHeap.isEmpty()){
            HeapElem trival_elem = minHeap.poll();
            elems.remove(trival_elem.img_index);
            int trival_elelindex = trival_elem.img_index;
            int tx = trival_elelindex % xdim;
            int ty = (trival_elelindex/xdim) % ydim;
            int tz = (trival_elelindex/(xdim*ydim)) %zdim;
            state[tz][ty][tx] = Type.ALIVE;
            int x0, y0, z0;
            for(int kk=-1; kk<=1; kk++) {
                z0 = tz + kk;
                if (z0 < 0 || z0 >= zdim) continue;
                for(int jj=-1; jj<=1; jj++) {
                    y0 = ty + jj;
                    if (y0 < 0 || y0 >= ydim) continue;
                    for(int ii=-1; ii<=1; ii++) {
                        x0 = tx + ii;
                        if (x0 < 0 || x0 >= xdim) continue;
                        int offset = abs(ii) + abs(jj) + abs(kk);
                        if (offset == 0 || offset > cnn_type) continue;
                        if(state[z0][y0][x0] != Type.ALIVE){
                            float new_dis = (float) (phi[tz][ty][tx]+inimg[z0][y0][x0]*sqrt((double)offset));
                                if(state[z0][y0][x0] == Type.FAR) {
                                    phi[z0][y0][x0] = new_dis;
                                    HeapElem elem = new HeapElem(z0 * ydim * xdim + y0 * xdim + x0, phi[z0][y0][x0]);
                                    minHeap.add(elem);
                                    elems.put(z0 * ydim * xdim + y0 * xdim + x0, elem);
                                    state[z0][y0][x0] = Type.TRIAL;
                                }
                                else if(state[z0][y0][x0] == Type.TRIAL){
                                    if(phi[z0][y0][x0]>new_dis){
                                        phi[z0][y0][x0] = new_dis;
                                        HeapElem elem = elems.get(z0 * ydim * xdim + y0 * xdim + x0);
                                        minHeap.remove(elem);
                                        elem.value = phi[z0][y0][x0];
                                        minHeap.add(elem);
                                        elems.remove(z0 * ydim * xdim + y0 * xdim + x0);
                                        elems.put(z0 * ydim * xdim + y0 * xdim + x0,elem);
                                    }
                                }
                             }
                        }
                    }
                }
            }
        System.out.println("Enter here gsdt_FM2");
        return true;
        }
    }
