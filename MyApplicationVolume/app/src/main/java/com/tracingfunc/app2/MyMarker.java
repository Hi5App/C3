package com.tracingfunc.app2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
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

    @NonNull
    @Override
    public MyMarker clone() throws CloneNotSupportedException {
        return (MyMarker) super.clone();
    }
}
