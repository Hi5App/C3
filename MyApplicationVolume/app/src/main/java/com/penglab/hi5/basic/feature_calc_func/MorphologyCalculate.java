package com.penglab.hi5.basic.feature_calc_func;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.penglab.hi5.basic.NeuronSWC;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.core.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;


public class MorphologyCalculate {
    final int VOID = 1000000000;
    final double PI = 3.14159265359f;

    double Width = 0, Height = 0, Depth = 0, Diameter = 0, Length = 0, Volume = 0, Surface = 0, Hausdorff = 0;
    int N_node = 0, N_stem = 0, N_bifs = 0, N_branch = 0, N_tips = 0, Max_Order = 0;
    double Pd_ratio = 0, Contraction = 0, Max_Eux = 0, Max_Path = 0, BifA_local = 0, BifA_remote = 0, Soma_surface = 0,
            Fragmentation = 0;
    int rootidx = 0;
    int count = 0;  //components count...
//    List<Integer> rootidxlist = new ArrayList<>();

    Vector<Vector<Integer>> childs;

    List<double[]> computeFeature(NeuronTree nt, boolean isglobal) {
        List<double[]> featurelist = new ArrayList<>();
        List<NeuronTree> splited_NT = split_nt.split_nt_file(nt);
        for (int n_nt = 0; n_nt < splited_NT.size(); n_nt++) {
            nt = splited_NT.get(n_nt);
            double[] features = new double[23];
            Width = 0;
            Height = 0;
            Depth = 0;
            Diameter = 0;
            Length = 0;
            Volume = 0;
            Surface = 0;
            Hausdorff = 0;
            N_node = 0;
            N_stem = 0;
            N_bifs = 0;
            N_branch = 0;
            N_tips = 0;
            Max_Order = 0;
            Pd_ratio = 0;
            Contraction = 0;
            Max_Eux = 0;
            Max_Path = 0;
            BifA_local = 0;
            BifA_remote = 0;
            Soma_surface = 0;
            Fragmentation = 0;

            rootidx = 0;

            count = 0;

            int neuronNum = nt.listNeuron.size();

            // find the root
            rootidx = VOID;
            List<NeuronSWC> list = new ArrayList<>(nt.listNeuron);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).parent == -1) {
                    // compute the first tree in the forest
                    rootidx = i;
//                rootidxlist.add(i);
//                break;
                }
            }
            if (rootidx == VOID) {
                System.out.println("the input neuron tree does not have a root, please check your data");
                Toast.makeText(MainActivity.getContext(), "the input neuron tree does not have a root, please check your data", Toast.LENGTH_LONG).show();
                return null;
            }
            childs = new Vector<Vector<Integer>>(neuronNum);
            for (int i = 0; i < neuronNum; i++) {
                Vector<Integer> child_node = new Vector<Integer>();// declare&assign
                childs.addElement(child_node);
            }

            for (int i = 0; i < neuronNum; i++) {
                Long par = nt.listNeuron.get(i).parent;
                if (par < 0) {
                    count += 1;
                    continue;
                }
                try {   //there is sth wrong...Only calculate one neuron tree...
                    childs.get(nt.hashNeuron.get(par.intValue())).addElement(i);
                } catch (NullPointerException e) {
                    System.out.println("redundant root: " + par);
//                    nt.listNeuron.get(i).parent = -1;
                    count += 1;
                }
            }
//        for (int nn = 0; nn < rootidxlist.size(); nn++) {
//            rootidx = rootidxlist.get(nn);

            N_node = list.size();
            N_stem = childs.get(rootidx).size();
            Soma_surface = 4 * PI * (list.get(rootidx).radius) * (list.get(rootidx).radius);

            computeLinear(nt);
            computeTree(nt);
            Hausdorff = computeHausdorff(nt);

            features[0] = count;
            // feature # 0: Number of Nodes
            features[1] = N_node;
            // feature #1: Soma Surface
            features[2] = Soma_surface;
            // feature # 2: Number of Stems
            features[3] = N_stem;
            // feature # 3: Number of Bifurcations
            features[4] = N_bifs;
            // feature # 4: Number of Branches
            features[5] = N_branch;
            // feature # 5: Number of Tips
            features[6] = N_tips;
            // feature # 6: Overall Width
            features[7] = Width;
            // feature # 7: Overall Height
            features[8] = Height;
            // feature # 8: Overall Depth
            features[9] = Depth;
            // feature # 9: Average Diameter
            features[10] = Diameter;
            // feature # 10: Total Length
            features[11] = Length;
            // feature # 11: Total Surface
            features[12] = Surface;
            // feature # 12: Total Volume
            features[13] = Volume;
            // feature # 13: Max Euclidean Distance
            features[14] = Max_Eux;
            // feature # 14: Max Path Distance
            features[15] = Max_Path;
            // feature # 15: Max Branch Order
            features[16] = Max_Order;
            // feature # 16: Average Contraction
            features[17] = Contraction;
            // feature # 17: Average Fragmentation
            features[18] = Fragmentation;
            // feature # 18: Average Parent-daughter Ratio
            features[19] = Pd_ratio;
            // feature # 19: Average Bifurcation Angle Local
            features[20] = BifA_local;
            // feature # 20: Average Bifurcation Angle Remote
            features[21] = BifA_remote;
            // feature # 21: Hausdorr Dimension
            features[22] = Hausdorff; // Hausdorff program crash when running on complex neuron data, we don't use it
            featurelist.add(features);
        }

        return featurelist;
    }

    Vector<Integer> getRemoteChild(int t) {
        Vector<Integer> rchildlist = new Vector<Integer>();
        rchildlist.clear();
        int tmp;
        for (int i = 0; i < childs.get(t).size(); i++) {
            tmp = childs.get(t).get(i);
            while (childs.get(tmp).size() == 1)
                tmp = childs.get(tmp).get(0);
            rchildlist.addElement(tmp);
        }
        return rchildlist;
    }

    // do a search along the list to compute overall N_bif, N_tip, width, height,
    // depth, length, volume, surface, average diameter and max euclidean distance.
    void computeLinear(NeuronTree nt) {
        double xmin, ymin, zmin;
        xmin = ymin = zmin = VOID;
        double xmax, ymax, zmax;
        xmax = ymax = zmax = -VOID;
        List<NeuronSWC> list = new ArrayList<>(nt.listNeuron);
        NeuronSWC soma = list.get(rootidx);

        for (int i = 0; i < list.size(); i++) {
            NeuronSWC curr = list.get(i);
            xmin = Math.min(xmin, curr.x);
            ymin = Math.min(ymin, curr.y);
            zmin = Math.min(zmin, curr.z);
            xmax = Math.max(xmax, curr.x);
            ymax = Math.max(ymax, curr.y);
            zmax = Math.max(zmax, curr.z);
            if (childs.get(i).size() == 0) {
                N_tips++;
            } else if (childs.get(i).size() > 1) {
                N_bifs++;
            }
            int parent;
            try {
                parent = getParent(i, nt);
            } catch (NullPointerException e) {
                parent = VOID;
            }
            if (parent == VOID)
                continue;
            double l = dist(curr, list.get(parent));
            Diameter += 2 * curr.radius;
            Length += l;
            Surface += 2 * PI * curr.radius * l;
            Volume += PI * curr.radius * curr.radius * l;
            double lsoma = dist(curr, soma);
            Max_Eux = Math.max(Max_Eux, lsoma);
        }
        Width = xmax - xmin;
        Height = ymax - ymin;
        Depth = zmax - zmin;
        Diameter /= list.size();
    }

    // do a search along the tree to compute N_branch, max path distance, max branch
    // order,
    // average Pd_ratio, average Contraction, average Fragmentation, average bif
    // angle local & remote
    void computeTree(NeuronTree nt) {
        List<NeuronSWC> list = nt.listNeuron;
        NeuronSWC soma = nt.listNeuron.get(rootidx);

        double pathTotal[] = new double[list.size()];
        int depth[] = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            pathTotal[i] = 0;
            depth[i] = 0;
        }
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(rootidx);
        double pathlength, eudist, max_local_ang, max_remote_ang;
        Long N_ratio = 0L, N_Contraction = 0L;

        if (childs.get(rootidx).size() > 1) {
            double local_ang, remote_ang;
            max_local_ang = 0;
            max_remote_ang = 0;
            int ch_local1 = childs.get(rootidx).get(0);
            int ch_local2 = childs.get(rootidx).get(1);
            local_ang = angle(list.get(rootidx), list.get(ch_local1), list.get(ch_local2));

            int ch_remote1 = getRemoteChild(rootidx).get(0);
            int ch_remote2 = getRemoteChild(rootidx).get(1);
            remote_ang = angle(list.get(rootidx), list.get(ch_remote1), list.get(ch_remote2));
            if (local_ang == local_ang)
                max_local_ang = Math.max(max_local_ang, local_ang);
            if (remote_ang == remote_ang)
                max_remote_ang = Math.max(max_remote_ang, remote_ang);

            BifA_local += max_local_ang;
            BifA_remote += max_remote_ang;
        }

        int t, tmp, fragment;
        while (!stack.isEmpty()) {
            t = stack.pop();
            Vector<Integer> child = childs.get(t);
            for (int i = 0; i < child.size(); i++) {
                N_branch++;
                tmp = child.get(i);
                if (list.get(t).radius > 0) {
                    N_ratio++;
                    Pd_ratio += list.get(tmp).radius / list.get(t).radius;
                }
                pathlength = dist(list.get(tmp), list.get(t));

                fragment = 0;
                while (childs.get(tmp).size() == 1) {
                    int ch = childs.get(tmp).get(0);
                    pathlength += dist(list.get(ch), list.get(tmp));
                    fragment++;
                    tmp = ch;
                }
                eudist = dist(list.get(tmp), list.get(t));
                Fragmentation += fragment;
                if (pathlength > 0) {
                    Contraction += eudist / pathlength;
                    N_Contraction++;
                }

                // we are reaching a tip point or another branch point, computation for this
                // branch is over
                int chsz = childs.get(tmp).size();
                if (chsz > 1) // another branch
                {
                    stack.push(tmp);

                    // compute local bif angle and remote bif angle
                    double local_ang, remote_ang;
                    max_local_ang = 0;
                    max_remote_ang = 0;
                    int ch_local1 = childs.get(tmp).get(0);
                    int ch_local2 = childs.get(tmp).get(1);
                    local_ang = angle(list.get(tmp), list.get(ch_local1), list.get(ch_local2));

                    int ch_remote1 = getRemoteChild(tmp).get(0);
                    int ch_remote2 = getRemoteChild(tmp).get(1);
                    remote_ang = angle(list.get(tmp), list.get(ch_remote1), list.get(ch_remote2));
                    if (local_ang == local_ang)
                        max_local_ang = Math.max(max_local_ang, local_ang);
                    if (remote_ang == remote_ang)
                        max_remote_ang = Math.max(max_remote_ang, remote_ang);

                    BifA_local += max_local_ang;
                    BifA_remote += max_remote_ang;
                }
                pathTotal[tmp] = pathTotal[t] + pathlength;
                depth[tmp] = depth[t] + 1;
            }
        }

        Pd_ratio /= N_ratio;
        Fragmentation /= N_branch;
        Contraction /= N_Contraction;

        BifA_local /= N_bifs;
        BifA_remote /= N_bifs;

        for (int i = 0; i < list.size(); i++) {
            Max_Path = Math.max(Max_Path, pathTotal[i]);
            Max_Order = Math.max(Max_Order, depth[i]);
        }
        pathTotal = null;
        depth = null;
    }

    double dist(NeuronSWC a, NeuronSWC b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z));
    }

    int getParent(int n, NeuronTree nt) {
        return (nt.listNeuron.get(n).parent < 0) ? (VOID) : (nt.hashNeuron.get((int) nt.listNeuron.get(n).parent));
    }

    double angle(NeuronSWC a, NeuronSWC b, NeuronSWC c) {
        return (Math.acos(((b.x - a.x) * (c.x - a.x) + (b.y - a.y) * (c.y - a.y) + (b.z - a.z) * (c.z - a.z))
                / (dist(a, b) * dist(a, c))) * 180.0 / PI);
    }

    // compute Hausdorff dimension
    double computeHausdorff(NeuronTree nt) {
        final int LMINMAX = 2;
        final int LMAXMIN = 1;
        final int NCELL = 500000; // max# nonempty lattice cells in Hausdorff analysis

        int n;

        int r1[][], r2[][];

        n = nt.listNeuron.size();
        r1 = matrix(3, n);
        r2 = matrix(3, n);

        n = fillArray(nt, r1, r2);

        int i, k, k1, l, m, cnt, dl, lmin, lmax;
        int r[], rr[], cell[][];
        r = new int[3];
        rr = new int[3];

        int scale;
        float dr[] = new float[3];
        float rt[] = new float[3];
        float measure[] = new float[25];
        float total, hd, length;

        length = 0;
        lmin = 0;
        lmax = 0;
        for (i = 1; i < n; i++)
            for (k = 0; k < 3; k++) {
                lmin += Math.abs(r1[k][i] - r2[k][i]);
                if (lmax < Math.abs(r2[k][i] - r1[k][1]))
                    lmax = Math.abs(r2[k][i] - r1[k][1]);
            }
        lmin /= LMINMAX * n;
        lmax /= 2;
        /*------------start with lattice cell >= lmin ------------*/
        if (lmin < 1)
            lmin = 1;
        else if (lmin > 1) {
            lmax /= lmin;
            for (i = 1; i < n; i++)
                for (k = 0; k < 3; k++) {
                    r1[k][i] /= lmin;
                    r2[k][i] /= lmin;
                }
        }
        if (lmax <= 1)
            return (0.0);
        scale = lmin;
        cnt = 0;

        cell = matrix(NCELL, 3);
        /*-----------------------------------------------------main loop begin----------------------*/
        while (lmax > LMAXMIN) {
            for (k = 0; k < 3; k++)
                r[k] = r1[k][1];
            m = mark(0, r, cell);
            for (i = 1; i < n; i++)
                if ((r1[0][i] != r2[0][i]) || (r1[1][i] != r2[1][i]) || (r1[2][i] != r2[2][i])) {
                    /*-------------------------tracing link-------*/
                    total = 0.0f;
                    for (k = 0; k < 3; k++)
                        total += Math.abs(r2[k][i] - r1[k][i]);
                    for (k = 0; k < 3; k++) {
                        r[k] = r1[k][i];
                        dr[k] = (r2[k][i] - r[k]) / total;
                        rt[k] = dr[k];
                    }
                    m = mark(m, r, cell);
                    while ((r[0] != r2[0][i]) || (r[1] != r2[1][i]) || (r[2] != r2[2][i])) {
                        l = 0;
                        k1 = -1;
                        for (k = 0; k < 3; k++)
                            rr[k] = r2[k][i] - r[k];
                        for (k = 0; k < 3; k++) {
                            if ((rt[k] * rr[k] > 0) && (Math.abs(l) < Math.abs(rr[k]))) {
                                l = rr[k];
                                k1 = k;
                            }
                        }
                        if (l > 0) {
                            r[k1]++;
                            rt[k1]--;
                        } else {
                            r[k1]--;
                            rt[k1]++;
                        }
                        for (k = 0; k < 3; k++)
                            rt[k] += dr[k];
                        m = mark(m, r, cell);
                        if (m >= NCELL)
                            System.out.println("maximal cell number reached");
                        if (m >= NCELL)
                            System.exit(1);
                    }

                }
            measure[cnt] = m;
            cnt++;

            for (i = 1; i < n; i++)
                for (k = 0; k < 3; k++) {
                    r1[k][i] /= 2;
                    r2[k][i] /= 2;
                }
            lmax /= 2;
            scale *= 2;
        }
        /*-----------------------------main loop end-------------------------*/
        free_matrix(r1, 3, n);
        free_matrix(r2, 3, n);
        free_matrix(cell, NCELL, 3);
        /*-----------------------------computing Hausdorff dimension---------*/
        hd = 0;
        for (i = 0; i < cnt; i++)
            hd += (i - 0.5 * (cnt - 1)) * Math.log(measure[i]);
        hd *= -12.0 / (cnt * (cnt * cnt - 1.0)) / Math.log(2.0);
        return (hd);
    }

    int fillArray(NeuronTree nt, int r1[][], int r2[][]) {
        List<NeuronSWC> list = nt.listNeuron;

        int siz = list.size();
        for (int t = 0; t < siz; t++) {
            int s;
            try {
                s = getParent(t, nt);
            } catch (NullPointerException e) {
                s = VOID;
                continue;
            }
            if (s == VOID)
                s = t;
            int cst = 1;
            r2[0][t] = (short) list.get(s).x + cst;
            r2[1][t] = (short) list.get(s).y + cst;
            r2[2][t] = (short) list.get(s).z + 1;
            r1[0][t] = (short) list.get(t).x + cst;
            r1[1][t] = (short) list.get(t).y + cst;
            r1[2][t] = (short) list.get(t).z + 1;

        }
        return siz;
    }

    int matrix(int n, int m)[][] {
        int mat[][] = new int[n][];
        for (int i = 0; i < n; i++) {
            mat[i] = new int[m];
            for (int j = 0; j < m; j++)
                mat[i][j] = 0;
        }
        /* Return pointer to array of pointers to rows. */
        return mat;

    }

    void free_matrix(int mat[][], int n, int m)

        /* Free a float matrix allocated by matrix(). */ {
        int i;

        for (i = 0; i < n; i++) {
            mat[i] = null;
        }
        mat = null;
    }

    /*********************** mark lattice cell r, keep marked set ordered */
    int mark(int m, int r[], int c[][]) {
        int i, j, k;
        if (m <= 0)
            for (k = 0; k < 3; k++)
                c[0][k] = r[k]; /*--initialize the pool of marked cells--*/
        else {
            for (i = 0; i < m; i++) {
                if (c[i][0] == r[0] && c[i][1] == r[1] && c[i][2] == r[2])
                    return (m); /*--- already marked ---*/
                if (c[i][0] >= r[0] && c[i][1] >= r[1] && c[i][2] > r[2])
                    break; /*--- insert into ordered set ---*/
            }
            if (i < m)
                for (j = m; j > i; j--)
                    for (k = 0; k < 3; k++)
                        c[j][k] = c[j - 1][k];
            for (k = 0; k < 3; k++)
                c[i][k] = r[k];
        }
        return (m + 1);
    }

//    /**
//     * compute morphology features from a .swc file.
//     * Input: path: absolute path of .swc file
//     * Output:
//     */
//    double[] Compute_from_file(String path, boolean isglobal) {
//        readSWC_file_nt reader = new readSWC_file_nt();
//        MorphologyCalculate MC = new MorphologyCalculate();
//        NeuronTree nt = reader.readSWC_file(path);
//        double[] feature_list = new double[22];
//        MC.computeFeature(nt, feature_list, isglobal);
//
//        return feature_list;
//    }

//    public static void main(String[] args) {
//        MorphologyCalculate MC = new MorphologyCalculate();
//        //test data
//        System.out.println(MC.Compute_from_file("F:\\XiScience\\SEU\\C3\\test_data\\1pic1.v3draw.swc")[0]);
//
//    }


    public List<double[]> calculate(Uri uri, boolean isglobal) {
        readSWC_file_nt reader = new readSWC_file_nt();
        MorphologyCalculate MC = new MorphologyCalculate();
        //test data
        NeuronTree nt = reader.readSWC_file(uri);
        if (nt == null) return null;

        double[] ff = new double[23];
        List<double[]> fl = MC.computeFeature(nt, isglobal);

        for (int i = 0; i < ff.length; i++) {
            Log.v("Calculate", Double.toString(ff[i]));
        }

        return fl;

    }


    public List<double[]> calculatefromNT(NeuronTree nt, boolean isglobal) {

        MorphologyCalculate MC = new MorphologyCalculate();

        if (nt == null) return null;
        double[] ff = new double[23];
        List<double[]> fl = MC.computeFeature(nt, isglobal);

        for (int i = 0; i < ff.length; i++) {
            Log.v("Calculate", Double.toString(ff[i]));
        }

        return fl;

    }

}