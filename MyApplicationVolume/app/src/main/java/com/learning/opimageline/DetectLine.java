package com.learning.opimageline;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.basic.ByteTranslate;
import com.example.basic.Image4DSimple;
import com.example.basic.ImageMarker;
import com.example.basic.LocationSimple;
import com.example.basic.NeuronSWC;
import com.example.basic.NeuronTree;
import com.learning.randomforest.RandomForest;
import com.tracingfunc.app2.MyMarker;
import com.tracingfunc.app2.ParaAPP2;
import com.tracingfunc.app2.V3dNeuronAPP2Tracing;
import com.tracingfunc.gsdt.GSDT;
import com.tracingfunc.gsdt.ParaGSDT;
import com.tracingfunc.rg.RGPara;
import com.tracingfunc.rg.RegionGrow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class DetectLine {

    private boolean is128Cube;

    public DetectLine(){
        is128Cube = true;
    }

    public void setIs128Cube(boolean is128Cube) {
        this.is128Cube = is128Cube;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public NeuronTree detectLine(Image4DSimple img, NeuronTree nt) throws Exception {
        NeuronTree lines = new NeuronTree();

        long startTime = System.currentTimeMillis();

        Image4DSimple result = new Image4DSimple();

        double dFactorXY = 1, dFactorZ = 1;
        Image4DSimple downSampleImg = new Image4DSimple();

        if(is128Cube){

            double size = 128;

            int[] inSZ = new int[]{(int) img.getSz0(), (int) img.getSz1(), (int) img.getSz2()};

            if (inSZ[0]<=size && inSZ[1]<=size && inSZ[2]<=size)
            {
                dFactorZ = dFactorXY = 1;
            }
            else if (inSZ[0] >= 2*inSZ[2] || inSZ[1] >= 2*inSZ[2])
            {
                if (inSZ[2]<=size)
                {
                    double MM = inSZ[0];
                    if (MM<inSZ[1]) MM=inSZ[1];
                    dFactorXY = MM / size;
                    dFactorZ = 1;
                }
                else
                {
                    double MM = inSZ[0];
                    if (MM<inSZ[1]) MM=inSZ[1];
                    if (MM<inSZ[2]) MM=inSZ[2];
                    dFactorXY = dFactorZ = MM / size;
                }
            }
            else
            {
                double MM = inSZ[0];
                if (MM<inSZ[1]) MM=inSZ[1];
                if (MM<inSZ[2]) MM=inSZ[2];
                dFactorXY = dFactorZ = MM / size;
            }


            Image4DSimple.resample3dimg_interp(downSampleImg,img,dFactorXY,dFactorXY,dFactorZ,1);

            if(dFactorXY>1 || dFactorZ>1){
                for(int i=0; i<nt.listNeuron.size(); i++){
                    nt.listNeuron.get(i).x /= dFactorXY;
                    nt.listNeuron.get(i).y /= dFactorXY;
                    nt.listNeuron.get(i).z /= dFactorZ;
                }
            }
        }else {
            downSampleImg.setData(img);
        }

        Vector<MyMarker> inSwc = MyMarker.swcConvert(nt);
        int[] sz = new int[]{(int) downSampleImg.getSz0(), (int) downSampleImg.getSz1(), (int) downSampleImg.getSz2()};
        byte[] mask = MyMarker.swcToMask2(inSwc,sz,1);

        byte[] img1dByte = downSampleImg.getData();

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

        for(int i=0; i<mask.length; i++){
            if(mask[i] == 1){
                std += (ByteTranslate.byte1ToInt(img1dByte[i])-mean)*(ByteTranslate.byte1ToInt(img1dByte[i])-mean);
            }
        }
        if(count>0){
            std = Math.sqrt(std/count);
        }
        System.out.println("count: "+count+"mean: "+mean+" std: "+std);
        double min = mean;//Math.max(mean-std,30) - 5;

        System.out.println("min : "+min);

        System.out.println("-----------------rgGrow-----------------------");

        RGPara rgPara = new RGPara();
        rgPara.thIdx = 2;
        rgPara.threshold = min;
        Image4DSimple regions = RegionGrow.reginGrowing(downSampleImg,rgPara);

        byte[] regions1d = regions.getData();

        System.out.println("-------------------GSDT------------------------");

        ParaGSDT p = new ParaGSDT();
        p.p4DImage = downSampleImg;
        GSDT.GSDT_Fun(p);

        Vector<Integer> markerIndexs = new Vector<>();
        for(int i=0; i<p.markers.size(); i++){
            int z = (int) (p.markers.get(i).z+0.5);
            if(z>sz[2]-1)
                z = sz[2] -1;
            if(z<0)
                z = 0;
            int y = (int) (p.markers.get(i).y+0.5);
            if(y>sz[1]-1)
                y = sz[1] -1;
            if(y<0)
                y = 0;
            int x = (int) (p.markers.get(i).x+0.5);
            if(x>sz[0]-1)
                x = sz[0] -1;
            if(x<0)
                x = 0;
            int index = z*sz[1]*sz[0] + y*sz[0] + x;
            markerIndexs.add(index);
        }

        int maxMarkerIndex = (int) (p.MaxMarker.z+0.5)*sz[1]*sz[0] + (int) (p.MaxMarker.y+0.5)*sz[0] + (int) (p.MaxMarker.x+0.5);

        Set<Byte> flags = new HashSet<>();
        for(int i=0; i<regions1d.length; i++){
            flags.add(regions1d[i]);
        }

        System.out.println("flags size: "+flags.size());

        boolean[] flagsUsed = new boolean[flags.size()];
        flagsUsed[regions1d[maxMarkerIndex]] = true;

        Vector<ImageMarker> markers = new Vector<>();

        markers.add(p.MaxMarker);

        for(int i=0; i<markerIndexs.size(); i++){
            if(!flagsUsed[regions1d[markerIndexs.get(i)]]){
                flagsUsed[regions1d[markerIndexs.get(i)]] = true;
                markers.add(p.markers.get(i));
            }
        }

        Vector<NeuronTree> app2NeuronTrees = new Vector<>();

        for(int i=0; i<markers.size(); i++){
            ParaAPP2 paraAPP2 = new ParaAPP2();
            paraAPP2.p4dImage = downSampleImg;
            paraAPP2.xc0 = paraAPP2.yc0 = paraAPP2.zc0 = 0;
            paraAPP2.xc1 = (int) paraAPP2.p4dImage.getSz0() - 1;
            paraAPP2.yc1 = (int) paraAPP2.p4dImage.getSz1() - 1;
            paraAPP2.zc1 = (int) paraAPP2.p4dImage.getSz2() - 1;
            paraAPP2.landmarks = new LocationSimple[1];
            paraAPP2.bkg_thresh = (int) (min + 0.5);
            paraAPP2.landmarks[0] = new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
            System.out.println("---------------start---------------------");
            V3dNeuronAPP2Tracing.proc_app2(paraAPP2);
            System.out.println("i: "+i+" tree size: "+paraAPP2.resultNt.listNeuron.size());
            app2NeuronTrees.add(paraAPP2.resultNt);
        }

        lines = NeuronTree.mergeNeuronTrees(app2NeuronTrees);
        System.out.println("lines size: "+lines.listNeuron.size());

        if(dFactorXY>1 || dFactorZ>1){
            for(int i=0; i<lines.listNeuron.size(); i++){
                lines.listNeuron.get(i).x *= dFactorXY;
                lines.listNeuron.get(i).y *= dFactorXY;
                lines.listNeuron.get(i).z *= dFactorZ;
            }
        }
        for (int i=0; i<lines.listNeuron.size(); i++){
            lines.listNeuron.get(i).type = 4;
        }


//        for(int i=0; i<lines.listNeuron.size(); i++){
//            System.out.println("i : "+i+" n: "+lines.listNeuron.get(i).n+" p: "+lines.listNeuron.get(i).parent);
//        }
        return lines;
    }

    public RandomForest train(Image4DSimple img, NeuronTree nt){
        int numTrees = 100;
        ArrayList<float[]> trainData = new ArrayList<>();
        ArrayList<float[]> testData = new ArrayList<>();

        BranchTree bt = new BranchTree();
        bt.initialize(nt);
        Vector<Branch> branches = bt.getBranches();


        for(int i=0; i<branches.size(); i++){
            Branch b = branches.get(i);
            b.calculateFeature(img,nt);
            Vector<NeuronSWC> points = b.getPointsOfBranch(nt);
            if(points.size()<3)
                continue;
            int c = 0;
            if(points.get(1).type == 2)
                c = 1;
            else if(points.get(1).type == 3)
                c = 2;
            else
                continue;
            float[] features = new float[]{b.getAngleChangeMean(), b.getDistance(), b.getGradientMean(), b.getIntensityMean(),
                    b.getIntensityRatioToGlobal(), b.getIntensityRatioToLocal(), b.getIntensityStd(),b.getLength(),
                    b.getSigma12(),b.getSigma13(),c};
            trainData.add(features);
        }

        RandomForest rf = new RandomForest(numTrees,trainData,testData);
        rf.C = 2;
        rf.M = 10;
        rf.Ms = (int) Math.round(Math.log(rf.M)/Math.log(2)+1);
        rf.start();

        return rf;
    }

    public NeuronTree lineClassification(Image4DSimple img, NeuronTree nt, RandomForest rf){
        NeuronTree lines = new NeuronTree();

        BranchTree bt = new BranchTree();
        bt.initialize(nt);

        Vector<Branch> branches = bt.getBranches();

        ArrayList<float[]> data = new ArrayList<>();

        for(int i=0; i<branches.size(); i++){
            Branch b = branches.get(i);
            b.calculateFeature(img,nt);
            float[] features = new float[]{b.getAngleChangeMean(), b.getDistance(), b.getGradientMean(), b.getIntensityMean(),
                    b.getIntensityRatioToGlobal(), b.getIntensityRatioToLocal(), b.getIntensityStd(),b.getLength(), b.getLength(),
                    b.getSigma12(),b.getSigma13(),0};
            data.add(features);
        }

        for(int i=0; i<data.size(); i++){
            int c = rf.Evaluate(data.get(i));
            Vector<NeuronSWC> points = branches.get(i).getPointsOfBranch(nt);
            if(c == 0){
                for(int j=0; j<points.size(); j++){
                    points.get(j).type = 2;
                }
            }else if(c == 1){
                for(int j=0; j<points.size(); j++){
                    points.get(j).type = 3;
                }
            }
        }

        return nt;
    }
}
