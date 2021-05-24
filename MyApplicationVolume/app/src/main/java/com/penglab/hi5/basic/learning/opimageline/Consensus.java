package com.penglab.hi5.basic.learning.opimageline;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.penglab.hi5.basic.ByteTranslate;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.LocationSimple;
import com.penglab.hi5.basic.NeuronSWC;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.tracingfunc.app2.MyMarker;
import com.penglab.hi5.basic.tracingfunc.app2.ParaAPP2;
import com.penglab.hi5.basic.tracingfunc.app2.V3dNeuronAPP2Tracing;

import java.util.Vector;

public class Consensus {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static NeuronTree run(Image4DSimple img, NeuronTree line, double th, boolean s) throws Exception {
        NeuronTree result = new NeuronTree();

        System.out.println("---------------in run----------------");

        Vector<NeuronTree> trees = new Vector<>();
        Vector<MyMarker> inSwc = MyMarker.swcConvert(line);
        int[] sz = new int[]{(int) img.getSz0(), (int) img.getSz1(), (int) img.getSz2()};
        byte[] mask = MyMarker.swcToMask(inSwc,sz,1,1);
        byte[] img1dByte = img.getData();

        double mean = 0, std = 0;
        int count = 0;
        for(int i=0; i<mask.length; i++){
            if(mask[i] == 1){
                mean += ByteTranslate.byte1ToInt(img1dByte[i]);
                count++;
            }
        }
        if(count>0){
            mean /= count;
        }

        System.out.println("-------------mean----------------");

        for(int i=0; i<mask.length; i++){
            if(mask[i] == 1){
                std += (ByteTranslate.byte1ToInt(img1dByte[i])-mean)*(ByteTranslate.byte1ToInt(img1dByte[i])-mean);
            }
        }
        if(count>0){
            std = Math.sqrt(std/count);
        }
        System.out.println("count: "+count+"mean: "+mean+" std: "+std);
        double min = Math.max(mean - 0.5*std,30);//Math.max(mean-std,30) - 5;

        Point p1 = new Point();
        Point p2 = new Point();
        for(int i=0; i<line.listNeuron.size(); i++){
            int z = (int) (line.listNeuron.get(i).z + 0.5);
            int y = (int) (line.listNeuron.get(i).y + 0.5);
            int x = (int) (line.listNeuron.get(i).x + 0.5);
            if(img.getValue(x,y,z,0)>min){
                p1.setXYZ(x,y,z);
                break;
            }
        }

        for(int i=line.listNeuron.size()-1; i>=0; i--){
            int z = (int) (line.listNeuron.get(i).z + 0.5);
            int y = (int) (line.listNeuron.get(i).y + 0.5);
            int x = (int) (line.listNeuron.get(i).x + 0.5);
            if(img.getValue(x,y,z,0)>min){
                p2.setXYZ(x,y,z);
                break;
            }
        }

//        Point p1 = new Point(line.listNeuron.get(0).x,line.listNeuron.get(0).y,line.listNeuron.get(0).z);
//        Point p2 = new Point(line.listNeuron.get(line.listNeuron.size()-1).x,
//                line.listNeuron.get(line.listNeuron.size()-1).y,
//                line.listNeuron.get(line.listNeuron.size()-1).z);

        Point p11 = new Point();
        Point p22 = new Point();
        Point p = new Point();
        double dis1 = 0;
        double dis2 = 0;

        for(int i=0; i<img1dByte.length; i++){
            if(img1dByte[i] > min){
                int ix = i % sz[0];
                int iy = i/sz[0] % sz[1];
                int iz = i/(sz[0]*sz[1]) % sz[2];
                p.setXYZ(ix,iy,iz);
                p.setXYZ(ix,iy,iz);
                double tmp1 = Point.distanceTwoPoint(p1,p);
                if(tmp1>dis1){
                    dis1 = tmp1;
                    p11.setXYZ(p);
                }
                double tmp2 = Point.distanceTwoPoint(p2,p);
                if(tmp2>dis2){
                    dis2 = tmp2;
                    p22.setXYZ(p);
                }
            }
        }

        ParaAPP2 paraAPP2 = new ParaAPP2();
        paraAPP2.p4dImage = img;
        paraAPP2.xc0 = paraAPP2.yc0 = paraAPP2.zc0 = 0;
        paraAPP2.xc1 = (int) paraAPP2.p4dImage.getSz0() - 1;
        paraAPP2.yc1 = (int) paraAPP2.p4dImage.getSz1() - 1;
        paraAPP2.zc1 = (int) paraAPP2.p4dImage.getSz2() - 1;
        paraAPP2.bkg_thresh = -1;
        paraAPP2.landmarks = new LocationSimple[0];
        System.out.println("---------------start---------------------");
        V3dNeuronAPP2Tracing.proc_app2(paraAPP2);
        System.out.println(" tree size: "+paraAPP2.resultNt.listNeuron.size());
        trees.add(paraAPP2.resultNt);

        paraAPP2.landmarks = null;
        paraAPP2.landmarks = new LocationSimple[1];
        paraAPP2.landmarks[0] = new LocationSimple(p1.x,p1.y,p1.z);
        System.out.println("---------------start---------------------");
        V3dNeuronAPP2Tracing.proc_app2(paraAPP2);
        System.out.println(" tree size: "+paraAPP2.resultNt.listNeuron.size());
        trees.add(paraAPP2.resultNt);

        paraAPP2.landmarks[0].x = p2.x;
        paraAPP2.landmarks[0].y = p2.y;
        paraAPP2.landmarks[0].z = p2.z;
        System.out.println("---------------start---------------------");
        V3dNeuronAPP2Tracing.proc_app2(paraAPP2);
        System.out.println(" tree size: "+paraAPP2.resultNt.listNeuron.size());
        trees.add(paraAPP2.resultNt);

        paraAPP2.landmarks = null;
        paraAPP2.landmarks = new LocationSimple[2];
        paraAPP2.landmarks[0] = new LocationSimple(p1.x,p1.y,p1.z);
        paraAPP2.landmarks[1] = new LocationSimple(p11.x,p11.y,p11.z);
        System.out.println("---------------start---------------------");
        V3dNeuronAPP2Tracing.proc_app2(paraAPP2);
        System.out.println(" tree size: "+paraAPP2.resultNt.listNeuron.size());
        trees.add(paraAPP2.resultNt);

        paraAPP2.landmarks[0].x = p2.x;
        paraAPP2.landmarks[0].y = p2.y;
        paraAPP2.landmarks[0].z = p2.z;
        paraAPP2.landmarks[1].x = p22.x;
        paraAPP2.landmarks[1].y = p22.y;
        paraAPP2.landmarks[1].z = p22.z;
        System.out.println("---------------start---------------------");
        V3dNeuronAPP2Tracing.proc_app2(paraAPP2);
        System.out.println(" tree size: "+paraAPP2.resultNt.listNeuron.size());
        trees.add(paraAPP2.resultNt);

        NeuronTree consensus = consensus(trees,th);
        for(int i=0; i<consensus.listNeuron.size(); i++){
            consensus.listNeuron.get(i).type = 6;
        }

        for(int i=0; i<trees.size(); i++){
            for(int j=0; j<trees.get(i).listNeuron.size(); j++){
                trees.get(i).listNeuron.get(j).type = i + 1;
                if(i == 1){
                    trees.get(i).listNeuron.get(j).x += 1;
                }else if(i == 2){
                    trees.get(i).listNeuron.get(j).x -= 1;
                }else if(i==3){
                    trees.get(i).listNeuron.get(j).y += 1;
                }else if(i==4){
                    trees.get(i).listNeuron.get(j).y -= 1;
                }
            }
        }
        trees.add(consensus);

        result = NeuronTree.mergeNeuronTrees(trees);

//        return result;

        if(s)
            return result;
        else
            return  consensus;
    }

    public static NeuronTree consensus(Vector<NeuronTree> trees, double th) throws CloneNotSupportedException {
        NeuronTree result = new NeuronTree();

        Vector<NeuronTree> resultList = new Vector<>();

        for(int i=0; i<trees.size(); i++){

            NeuronTree cur = trees.get(i);

            Vector<NeuronTree> nts = new Vector<NeuronTree>();
            for(int j=0; j<trees.size(); j++){
                if(j != i)
                    nts.add(trees.get(i));
            }

            BranchTree curBt = new BranchTree();
            curBt.initialize(cur);

            Vector<Branch> curBts = curBt.getBranches();
            for(int j=0; j<curBts.size(); j++){
                NeuronTree tmp = branchToNeuronTree(curBts.get(j),cur,nts,th);
                double dis = Double.MAX_VALUE;
                for(int k=0; k<resultList.size(); k++){
                    double[] d = branchToBranch(tmp,resultList.get(k));
                    if(d[0] < dis && d[0] != -1)
                        dis = d[0];
                }
                if(dis>th && tmp.listNeuron.size()>2){
                    resultList.add(tmp);
                }
            }
        }
        result = NeuronTree.mergeNeuronTrees(resultList);
        return result;
    }

    public static NeuronTree branchToNeuronTree(Branch b1, NeuronTree nt1, Vector<NeuronTree> nts, double th) throws CloneNotSupportedException {
        NeuronTree result = new NeuronTree();

        Vector<NeuronSWC> points1 = b1.getPointsOfBranch(nt1);
        boolean flagStop = false;
        for(int i=1; i<points1.size(); i++){
            NeuronSWC p = points1.get(i);
            for(int j=0; j<nts.size(); j++){
                BranchTree bt = new BranchTree();
                NeuronTree nt2 = nts.get(j);
                bt.initialize(nt2);
                double d = Double.MAX_VALUE;
                Vector<Branch> bts = bt.getBranches();

                for(int k=0; k<bts.size(); k++){
                    double temp = pointToBranch(p,bts.get(k),nt2);
                    if(temp<d)
                        d = temp;
                }
                if(d>th){
                    flagStop = true;
                    break;
                }
            }

            if(flagStop)
                break;

            if(i == 1){
                NeuronSWC n = p.clone();
                n.parent = -1;
                result.listNeuron.add(n);
            }else {
                result.listNeuron.add(p.clone());
            }

        }

        for(int i=0; i<result.listNeuron.size(); i++){
            result.hashNeuron.put((int) result.listNeuron.get(i).n,i);
        }

        return result;
    }

    public static double pointToBranch(NeuronSWC p, Branch b, NeuronTree nt){
        double d = Double.MAX_VALUE;
        Vector<NeuronSWC> points = b.getPointsOfBranch(nt);
        if(points.size()<2){
            System.out.println("pointToBranch: branch size < 2");
            return -1;
        }
        Point p1 = new Point(p.x,p.y,p.z);
        Point p2 = new Point(points.get(0).x, points.get(0).y, points.get(0).z);
        Point p3 = new Point();
        for(int i=1; i<points.size(); i++){
            p2.setXYZ(points.get(i).x, points.get(i).y, points.get(i).z);
            double temp = Point.pointToLine(p1,p2,p3);
            if(temp<d)
                d = temp;
        }
        return d;
    }

    public static double[] branchToBranch(Branch b1, Branch b2, NeuronTree nt1, NeuronTree nt2){
        double[] dis = new double[2];
        for(int i=0; i<2; i++) {
            dis[i] = 0;
        }
        Vector<NeuronSWC> points1 = b1.getPointsOfBranch(nt1);
        Vector<NeuronSWC> points2 = b2.getPointsOfBranch(nt2);
        for(int i=0; i<points1.size(); i++){
            NeuronSWC p = points1.get(i);
            dis[0] += pointToBranch(p,b2,nt2);
        }
        if(points1.size()>0)
            dis[0] /= points1.size();

        for(int i=0; i<points2.size(); i++){
            NeuronSWC p = points2.get(i);
            dis[1] += pointToBranch(p,b1,nt1);
        }
        if(points2.size()>0)
            dis[1] /= points2.size();

        return dis;
    }

    /**
     *
     * @param nt1 只有一个branch
     * @param nt2 只有一个branch
     * @return
     */
    public static double[] branchToBranch(NeuronTree nt1, NeuronTree nt2){
        BranchTree bt1 = new BranchTree();
        BranchTree bt2 = new BranchTree();

        bt1.initialize(nt1);
        if(bt1.getBranches().size() != 1){
            double[] dis = new double[2];
            dis[0] = -1;
            dis[1] = -1;
            return dis;
        }

        bt2.initialize(nt2);
        if(bt2.getBranches().size() != 1){
            double[] dis = new double[2];
            dis[0] = -1;
            dis[1] = -1;
            return dis;
        }

        Branch b1 = bt1.getBranches().get(0);
        Branch b2 = bt2.getBranches().get(0);

        return branchToBranch(b1,b2,nt1,nt2);
    }
}
