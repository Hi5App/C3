package com.penglab.hi5.basic.tracingfunc.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.penglab.hi5.basic.ByteTranslate;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.NeuronSWC;
import com.penglab.hi5.basic.NeuronTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MyMarker implements Cloneable{
    public MyMarker parent;
    public double x,y,z;
    public double radius;
    int type;
    public MyMarker(){x=y=z=radius=0.0; type = 3;
        parent = null;
    }
    public MyMarker(double _x, double _y, double _z) {x = _x; y = _y; z = _z; radius = 0.0; type = 3;
        parent = null;
    }
    public MyMarker(MyMarker  v){x=v.x; y=v.y; z=v.z; radius = v.radius; type = v.type;
        parent = v.parent;
    }

    public boolean equals(@Nullable MyMarker other) {
        return other.x == this.x && other.y == this.y && other.z == this.z;
    }

    public static double dist(MyMarker a, MyMarker b){
        return Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y) + (a.z - b.z)*(a.z - b.z));
    }

    public double markerRadius(int[][][] img3d, int[] sz, double imgTH, float zthickness, boolean b_est_in_xyplaneonly){
        if (zthickness<=0) { zthickness=1.0f; System.out.println("Your zthickness value in fitRadiusPercent() is invalid. disable it (i.e. reset it to 1) in computation."); }//if it an invalid value then reset

        double max_r = sz[0]/2;
        if (max_r > sz[1]/2) max_r = sz[2]/2;
        if (!b_est_in_xyplaneonly)
        {
            if (max_r > (sz[2]*zthickness)/2) max_r = (sz[2]*zthickness)/2;
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
                            int i = (int) (x+dx);	if (i<0 || i>=sz[0]) break end;
                            int j = (int) (y+dy);	if (j<0 || j>=sz[1]) break end;
                            int k = (int) (z+dz);	if (k<0 || k>=sz[2]) break end;

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

    public static boolean smooth_radius(Vector<MyMarker> mCoord, int winsize, boolean median_filter) throws Exception
    {
        //std::cout<<" smooth_radius ";
        if (winsize < 2) return true;

        // Vector<T> mC = mCoord; // a copy
        Vector<MyMarker> mC = new Vector<MyMarker>();
        for(int s=0; s<mCoord.size(); s++){
            mC.add(mCoord.elementAt(s));
        }
        int N = mCoord.size();
        int halfwin = winsize / 2;

        for (int i = 1; i < N - 1; i++) // don't move start & end point
        {
            Vector<MyMarker> winC = new Vector<MyMarker>();
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
                Collections.sort(winC, new Comparator<MyMarker>() {
                    @Override
                    public int compare(MyMarker o1, MyMarker o2) {
                        return o1.radius<o2.radius?1:-1;
                    }
                });
                r = winC.elementAt(halfwin).radius;
            }
            else
            {
                double s = r = 0;
                for (int j = 0; j < winC.size(); j++)
                {
                    r += winW.elementAt(j) * winC.elementAt(j).radius;
                    s += winW.elementAt(j);
                }
                if (s>0)	r /= s;
            }

            mCoord.elementAt(i).radius = r; // output
        }
        return true;
    }

    public static boolean smoothCurve(Vector<MyMarker> markers, int winsize){
        System.out.println("---------------------smooth curve----------------------");
        if (winsize < 2) return true;

        int N = markers.size();
        int halfwin = winsize / 2;
        Vector<MyMarker> mC= new Vector<MyMarker>(); // a copy
        for(int i=0; i<N; i++){
            mC.add(new MyMarker(markers.get(i)));
        }


        for (int i = 1; i < N - 1; i++) // don't move start & end point
        {
            Vector<MyMarker> winC = new Vector<MyMarker>();
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

            markers.get(i).x = x; // output
            markers.get(i).y = y; // output
            markers.get(i).z = z; // output
        }
        return true;
    }

    public static boolean saveSWC_file(String swcfile, Vector<MyMarker> outmarkers, Vector<String> infostring){
        System.out.println("marker num = "+outmarkers.size()+", save swc file to "+swcfile);
        Map<MyMarker,Integer> ind = new HashMap<MyMarker,Integer>();
        try {
            File f = new File(swcfile);
            FileOutputStream fid = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fid, "UTF-8");
            writer.append("#name \n");
            writer.append("#comment \n");
            writer.append("##n,type,x,y,z,radius,parent\n");
            for(int i=0; i<outmarkers.size(); i++){
                ind.put(outmarkers.elementAt(i),i+1);
            }
            for(int i=0; i<outmarkers.size(); i++){
                MyMarker marker = outmarkers.elementAt(i);
                if(marker.x>500){
                    System.out.println("index i: "+i+" "+marker.x);
                }
                int parent_id;
                if(marker.parent == null)
                    parent_id = -1;
                else
                    parent_id = ind.get(marker.parent);
                writer.append(Integer.toString(i+1)).append(" ").append(Integer.toString(marker.type))
                        .append(" ").append(Double.toString(marker.x)).append(" ").append(Double.toString(marker.y))
                        .append(" ").append(Double.toString(marker.z)).append(" ").append(Double.toString(marker.radius))
                        .append(" ").append(Integer.toString(parent_id)).append("\n");
            }
            writer.close();
            fid.close();

        } catch (IOException e) {
            System.out.println("saveSWC Exception "+e.getMessage());
            return false;
        }
        return true;
    }

    public static Vector<MyMarker> swcConvert(NeuronTree nt){
        Vector<MyMarker> outSwc = new Vector<>();
        if(nt.listNeuron.isEmpty())
            return outSwc;

        int num = nt.listNeuron.size();
        HashMap<Integer, Integer> hN = new HashMap<>();

        for(int i=0; i<num; i++){
            NeuronSWC ns = nt.listNeuron.get(i);
            MyMarker marker = new MyMarker(ns.x,ns.y,ns.z);
            marker.radius = ns.radius;
            outSwc.add(marker);
            hN.put((int)nt.listNeuron.get(i).n, i);
        }
        for(int i=0; i<num; i++){
            MyMarker curMarker = outSwc.get(i);
            NeuronSWC ns = nt.listNeuron.get(i);
            if(ns.parent == -1)
                curMarker.parent = null;
            else {
                if(hN.get((int) ns.parent) == null){
//                    System.out.println("hN is error!");
                    curMarker.parent = null;
                }else {
                    int pid = hN.get((int) ns.parent);
                    curMarker.parent = outSwc.get(pid);
                }
            }
        }
        return outSwc;
    }

    public static NeuronTree swcConvert(Vector<MyMarker> inSwc){
        NeuronTree nt = new NeuronTree();
        if(inSwc.isEmpty())
            return nt;
        Map<MyMarker,Integer> swcMap = new HashMap<>();
        int num = inSwc.size();
        for(int i=0; i<num; i++){
            MyMarker marker = inSwc.get(i);
            swcMap.put(marker,i);
        }
        for(int i=0; i<num; i++){
            MyMarker marker = inSwc.get(i);
            long pid = -1;
            if(marker.parent != null)
                pid = swcMap.get(marker.parent);
            NeuronSWC ns = new NeuronSWC();
            ns.n = i;
            ns.x = (float) marker.x;
            ns.y = (float) marker.y;
            ns.z = (float) marker.z;
            ns.radius = (float) marker.radius;
            ns.type = marker.type;
            ns.parent = pid;
            nt.listNeuron.add(ns);
        }
        for(int i=0; i<num; i++){
            nt.hashNeuron.put((int) nt.listNeuron.get(i).n,i);
        }
        return nt;
    }

    //radius为0时，按swc本身的半径计算mask，否则按提供的半径进行计算
    public static boolean getMarkersBetween(Vector<MyMarker> allMarkers, MyMarker m1, MyMarker m2, double radius){
        if(radius<0){
            System.out.println("radius is negative!");
            return false;
        }
        double A = m2.x - m1.x;
        double B = m2.y - m1.y;
        double C = m2.z - m1.z;
        double R = (radius == 0)?m2.radius - m1.radius:0;
        double D = Math.sqrt(A*A + B*B + C*C);
        A = A/D; B = B/D; C = C/D; R = R/D;

        double ctz = A/Math.sqrt(A*A + B*B);
        double stz = B/Math.sqrt(A*A + B*B);

        double cty = C/D;
        double sty = Math.sqrt(A*A + B*B)/D;

        double x0 = m1.x;
        double y0 = m1.y;
        double z0 = m1.z;
        double r0 = (radius == 0)?m1.radius:radius;

//        Set<MyMarker> markerSet = new HashSet<>();

        for(double t=0.0; t<D; t+=1.0){
            int cx = (int) (x0 + A*t +0.5);
            int cy = (int) (y0 + B*t +0.5);
            int cz = (int) (z0 + C*t +0.5);
            int cr = (int) (r0 + R*t +0.5);
            int cr2 = cr*cr;
            for(int k=-cr; k<=cr; k++){
                for(int j=-cr; j<=cr; j++){
                    for(int i=-cr; i<=cr; i++){
                        if(i*i + j*j + k*k>cr2)
                            continue;
                        double x = i, y = j, z = k;
                        double x1,y1,z1;
                        y1 = y * ctz - x * stz; x1 = x * ctz + y * stz; y = y1; x = x1;
                        x1 = x * cty + z * sty; z1 = z * cty - x * sty; x = x1; z = z1;
                        z1 = z * ctz + y * stz; y1 = y * ctz - z * stz; z = z1; y = y1;
                        x += cx; y += cy; z += cz;
                        x = (int)(x+0.5);
                        y = (int)(y+0.5);
                        z = (int)(z+0.5);
                        MyMarker marker = new MyMarker(x,y,z);
                        boolean exist = false;
                        for(int m=0; m<allMarkers.size(); m++){
                            if(allMarkers.get(m).equals(marker)){
                                exist = true;
                                break;
                            }
                        }
                        if(!exist){
                            allMarkers.add(marker);
                        }
                    }
                }
            }
        }
        return true;
    }

    //radius为0时，按swc本身的半径计算mask，否则按提供的半径进行计算
    public static byte[] swcToMask(Vector<MyMarker> inswc, int[] sz, double radius, int flag){
        int totalSz = sz[0]*sz[1]*sz[2];
        int sz01 = sz[0]*sz[1];
        byte[] outMask = new byte[totalSz];
        for(int i=0; i<totalSz; i++)
            outMask[i] = 0;
        Vector<MyMarker> leafMarkers = HierarchyPruning.getLeafMarkers(inswc);
        Set<MyMarker> visitedMarkers = new HashSet<>();
        for(int i=0; i<leafMarkers.size(); i++){
            MyMarker leaf = leafMarkers.get(i);
            MyMarker p = leaf;
            while (!visitedMarkers.contains(p) && p.parent != null){
                MyMarker par = p.parent;
                Vector<MyMarker> tmpMarkers = new Vector<>();
                getMarkersBetween(tmpMarkers,p,par,radius);
                for(int j=0; j<tmpMarkers.size(); j++){
                    int x = (int) tmpMarkers.get(j).x;
                    int y = (int) tmpMarkers.get(j).y;
                    int z = (int) tmpMarkers.get(j).z;
                    if(x<0 || x>=sz[0] || y<0 || y>=sz[1] ||z<0 || z>=sz[2])
                        continue;
                    outMask[z*sz01+y*sz[0]+x] = (byte) flag;
                }
                visitedMarkers.add(p);
                p = par;
            }
        }
        return outMask;
    }

    public static byte[] swcToMask2(Vector<MyMarker> inswc, int[] sz, int flag){
        int totalSz = sz[0]*sz[1]*sz[2];
        int sz01 = sz[0]*sz[1];
        byte[] outMask = new byte[totalSz];
        for(int i=0; i<totalSz; i++)
            outMask[i] = 0;
        Vector<MyMarker> leafMarkers = HierarchyPruning.getLeafMarkers(inswc);
        Set<MyMarker> visitedMarkers = new HashSet<>();
        for(int i=0; i<leafMarkers.size(); i++){
            MyMarker leaf = leafMarkers.get(i);
            MyMarker p = leaf;
            outMask[(int) p.z*sz01+(int) p.y*sz[0]+(int) p.x] = (byte) flag;
            while (!visitedMarkers.contains(p) && p.parent != null){
                MyMarker par = p.parent;
                if(MyMarker.dist(par,p)>1){
                    int x = (int) ((p.x+par.x)/2);
                    int y = (int) ((p.y+par.y)/2);
                    int z = (int) ((p.z+par.z)/2);
                    if(x<0 || x>=sz[0] || y<0 || y>=sz[1] ||z<0 || z>=sz[2])
                        break;
                    outMask[z*sz01+y*sz[0]+x] = (byte) flag;
                }
                outMask[(int) par.z*sz01+(int) par.y*sz[0]+(int) par.x] = (byte) flag;
                visitedMarkers.add(p);
                p = par;
            }
        }
        return outMask;
    }

    public static double[] getMeanStdFromNeuronTree(NeuronTree nt, Image4DSimple img, double radius){
        double[] meanStd = new double[2];
        Vector<MyMarker> markers = MyMarker.swcConvert(nt);
        int[] sz = new int[]{(int) img.getSz0(), (int) img.getSz1(), (int) img.getSz2()};
        byte[] mask = MyMarker.swcToMask(markers,sz,radius,1);
        byte[] img1dByte = img.getData();

        meanStd[0] = 0;
        meanStd[1] = 0;
        int count = 0;
        for(int i=0; i<mask.length; i++){
            if(mask[i] == 1){
                meanStd[0] += ByteTranslate.byte1ToInt(img1dByte[i]);
                count++;
            }
        }
        if(count>0){
            meanStd[0] /= count;
        }

        System.out.println("-------------mean----------------");

        for(int i=0; i<mask.length; i++){
            if(mask[i] == 1){
                meanStd[1] += (ByteTranslate.byte1ToInt(img1dByte[i])-meanStd[0])*(ByteTranslate.byte1ToInt(img1dByte[i])-meanStd[0]);
            }
        }
        if(count>0){
            meanStd[1] = Math.sqrt(meanStd[1]/count);
        }
        System.out.println("count: "+count+"mean: "+meanStd[0]+" std: "+meanStd[1]);
        return meanStd;
    }



    @NonNull
    @Override
    public MyMarker clone() throws CloneNotSupportedException {
        return (MyMarker) super.clone();
    }
}
