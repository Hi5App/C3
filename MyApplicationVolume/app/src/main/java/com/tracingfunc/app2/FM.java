package com.tracingfunc.app2;

import static com.tracingfunc.app2.FM.Type.*;
import static java.lang.Math.abs;

public class FM {
    enum Type{ALIVE,TRIAL,FAR};
    public static boolean fastmarching_dt(int[][][] inimg, float[][][] phi, int[] sz, int cnn_type, int bkg_thresh){
        //int cnn_type = 3;  // ?

        int szx = sz[0], szy = sz[1], szz = sz[2];
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

        BasicHeap<HeapElem> heap;
        map<long, HeapElem*> elems;

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
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
            for(long ind = 0; ind < tol_sz; ind++)
            {
                i++; if(i%sz0 == 0){i=0; j++; if(j%sz1==0){j=0; k++;}}
                if(state[ind] == ALIVE)
                {
                    for(int kk = -1; kk <= 1; kk++)
                    {
                        long k2 = k+kk;
                        if(k2 < 0 || k2 >= sz2) continue;
                        for(int jj = -1; jj <= 1; jj++)
                        {
                            long j2 = j+jj;
                            if(j2 < 0 || j2 >= sz1) continue;
                            for(int ii = -1; ii <=1; ii++)
                            {
                                long i2 = i+ii;
                                if(i2 < 0 || i2 >= sz0) continue;
                                int offset = ABS(ii) + ABS(jj) + ABS(kk);
                                if(offset == 0 || offset > cnn_type) continue;
                                long ind2 = k2 * sz01 + j2 * sz0 + i2;
                                if(state[ind2] == FAR)
                                {
                                    long min_ind = ind;
                                    // get minimum Alive point around ind2
                                    if(phi[min_ind] > 0.0)
                                    {
                                        for(int kkk = -1; kkk <= 1; kkk++)
                                        {
                                            long k3 = k2 + kkk;
                                            if(k3 < 0 || k3 >= sz2) continue;
                                            for(int jjj = -1; jjj <= 1; jjj++)
                                            {
                                                long j3 = j2 + jjj;
                                                if(j3 < 0 || j3 >= sz1) continue;
                                                for(int iii = -1; iii <= 1; iii++)
                                                {
                                                    long i3 = i2 + iii;
                                                    if(i3 < 0 || i3 >= sz0) continue;
                                                    int offset2 = ABS(iii) + ABS(jjj) + ABS(kkk);
                                                    if(offset2 == 0 || offset2 > cnn_type) continue;
                                                    long ind3 = k3 * sz01 + j3 * sz0 + i3;
                                                    if(state[ind3] == ALIVE && phi[ind3] < phi[min_ind]) min_ind = ind3;
                                                }
                                            }
                                        }
                                    }
                                    // over
                                    phi[ind2] = phi[min_ind] + inimg1d[ind2];
                                    state[ind2] = TRIAL;
                                    HeapElem * elem = new HeapElem(ind2, phi[ind2]);
                                    heap.insert(elem);
                                    elems[ind2] = elem;
                                    bdr_count++;
                                }
                            }
                        }
                    }
                }
            }
        }

        cout<<"bkg_count = "<<bkg_count<<" ("<<bkg_count/(double)tol_sz<<")"<<endl;
        cout<<"bdr_count = "<<bdr_count<<" ("<<bdr_count/(double)tol_sz<<")"<<endl;
        cout<<"elems.size() = "<<elems.size()<<endl;
        // loop
        int time_counter = bkg_count;
        double process1 = 0;
        while(!heap.empty())
        {
            double process2 = (time_counter++)*100000.0/tol_sz;
            if(process2 - process1 >= 100) {cout<<"\r"<<((int)process2)/1000.0<<"%";cout.flush(); process1 = process2;
            }

            HeapElem* min_elem = heap.delete_min();
            elems.erase(min_elem->img_ind);

            long min_ind = min_elem->img_ind;
            delete min_elem;

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
                        int offset = ABS(ii) + ABS(jj) + ABS(kk);
                        if(offset == 0 || offset > cnn_type) continue;
                        long index = d*sz01 + h*sz0 + w;

                        if(state[index] != ALIVE)
                        {
                            float new_dist = phi[min_ind] + inimg1d[index] * sqrt(double(offset));

                            if(state[index] == FAR)
                            {
                                phi[index] = new_dist;
                                HeapElem * elem = new HeapElem(index, phi[index]);
                                heap.insert(elem);
                                elems[index] = elem;
                                state[index] = TRIAL;
                            }
                            else if(state[index] == TRIAL)
                            {
                                if(phi[index] > new_dist)
                                {
                                    phi[index] = new_dist;
                                    HeapElem * elem = elems[index];
                                    heap.adjust(elem->heap_id, phi[index]);
                                }
                            }
                        }
                    }
                }
            }
        }
        //END_CLOCK;
        assert(elems.empty());
        if(state) {delete [] state; state = 0;}
        return true;
    }
}
