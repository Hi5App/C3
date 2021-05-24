package com.penglab.hi5.basic.learning.opimageline;

import com.penglab.hi5.basic.ByteTranslate;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.NeuronSWC;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.tracingfunc.app2.MyMarker;

import java.util.HashMap;
import java.util.Vector;

import Jama.Matrix;

public class Branch {
    private NeuronSWC headPoint,tailPoint;
    private Branch parent;
    private float length, distance;
    private float intensityMean, intensityStd, intensityRatioToGlobal, intensityRatioToLocal;
    private float gradientMean;
    private float angleChangeMean;
    private float sigma12;
    private float sigma13;

    public NeuronSWC getHeadPoint() {
        return headPoint;
    }

    public void setHeadPoint(NeuronSWC headPoint) {
        this.headPoint = headPoint;
    }

    public NeuronSWC getTailPoint() {
        return tailPoint;
    }

    public void setTailPoint(NeuronSWC tailPoint) {
        this.tailPoint = tailPoint;
    }

    public Branch getParent() {
        return parent;
    }

    public void setParent(Branch parent) {
        this.parent = parent;
    }

    public Vector<NeuronSWC> getRPointsOfBranch(NeuronTree nt){
        HashMap<Integer,Integer> hN = new HashMap<>();
        for(int i=0; i<nt.listNeuron.size(); i++){
//            System.out.println("i: "+i+" n: "+nt.listNeuron.get(i).n+" p: "+nt.listNeuron.get(i).parent);
            hN.put((int) nt.listNeuron.get(i).n,i);
        }
        Vector<NeuronSWC> rPoints = new Vector<>();
        NeuronSWC tmp = tailPoint;
        rPoints.add(tmp);
        while (tmp.n != headPoint.n && tmp.parent > 0){
//            System.out.println("parent: "+tmp.parent);
            tmp = nt.listNeuron.get(hN.get((int) tmp.parent));
            rPoints.add(tmp);
        }
        return  rPoints;
    }

    public Vector<NeuronSWC> getPointsOfBranch(NeuronTree nt){
        Vector<NeuronSWC> rPoints = this.getRPointsOfBranch(nt);
        Vector<NeuronSWC> points = new Vector<>();
        while (!rPoints.isEmpty()){
            NeuronSWC tmp = rPoints.lastElement();
            rPoints.remove(rPoints.size()-1);
            points.add(tmp);
        }
        return points;
    }

    public static double getNeuronSwcDistance(NeuronSWC p1, NeuronSWC p2){
        double a = Math.pow((p1.x - p2.x),2) + Math.pow((p1.y - p2.y),2) + Math.pow((p1.z - p2.z),2);
        return Math.sqrt(a);
    }

    public boolean calculateFeature(Image4DSimple img, NeuronTree nt) {

        distance = (float) getNeuronSwcDistance(headPoint,tailPoint);

        intensityMean = 0;
        intensityStd = 0;
        length = 0;
        angleChangeMean = 0;
        gradientMean = 0;
        sigma12 = 0;
        sigma13 = 0;
        Vector<NeuronSWC> points = this.getPointsOfBranch(nt);
        int count = 0;
        long x, y, z;
        x = (long) (headPoint.x + 0.5);
        y = (long) (headPoint.y + 0.5);
        z = (long) (headPoint.z + 0.5);
        if(x>=img.getSz0()) x = img.getSz0() - 1;
        if(x<0) x= 0;
        if(y>=img.getSz1()) y = img.getSz1() - 1;
        if(y<0) y= 0;
        if(z>=img.getSz2()) z = img.getSz2() - 1;
        if(z<0) z= 0;

        int lastIntensity = img.getValue(x,y,z,0), curIntensity;
        intensityMean += lastIntensity;
        Angle lastAngle = new Angle(), curAngle = new Angle();
        count++;
        NeuronSWC p1 = headPoint;

        double[] sigmas;
        int sigmaCount = 0;
        int[][][][] imgCZYX = img.getDataCZYX();
        sigmas = computePca(imgCZYX,(int) img.getSz0(),(int) img.getSz1(),(int) img.getSz2(),(int) x,(int) y,(int) z,0, (int) p1.radius);
        if(sigmas != null){
            sigma12 += Math.sqrt(sigmas[0])/Math.sqrt(sigmas[1]);
            sigma13 += Math.sqrt(sigmas[0])/Math.sqrt(sigmas[2]);
            sigmaCount++;
        }

        for(int i=1; i<points.size(); i++){
            NeuronSWC p2 = points.get(i);
            if( i == 1){
                lastAngle.setXYZ(p2.x-p1.x,p2.y-p1.y,p2.z-p1.z);
                lastAngle.normAngle();
            }else {
                curAngle.setXYZ(p2.x-p1.x,p2.y-p1.y,p2.z-p1.z);
                curAngle.normAngle();
                angleChangeMean += Math.acos(lastAngle.dot(curAngle));
                lastAngle.setXYZ(curAngle);
            }
            length += getNeuronSwcDistance(p1,p2);
            if(getNeuronSwcDistance(p1,p2)>1){
                x = (long) ((p1.x + p2.x)/2 + 0.5);
                y = (long) ((p1.y + p2.y)/2 + 0.5);
                z = (long) ((p1.z + p2.z)/2 + 0.5);
                if(x>=img.getSz0()) x = img.getSz0() - 1;
                if(x<0) x= 0;
                if(y>=img.getSz1()) y = img.getSz1() - 1;
                if(y<0) y= 0;
                if(z>=img.getSz2()) z = img.getSz2() - 1;
                if(z<0) z= 0;
                curIntensity = img.getValue(x,y,z,0);
                intensityMean += curIntensity;
                gradientMean += Math.abs(curIntensity - lastIntensity);
                lastIntensity = curIntensity;
                count++;
            }
            x = (long) (p2.x + 0.5);
            y = (long) (p2.y + 0.5);
            z = (long) (p2.z + 0.5);
            if(x>=img.getSz0()) x = img.getSz0() - 1;
            if(x<0) x= 0;
            if(y>=img.getSz1()) y = img.getSz1() - 1;
            if(y<0) y= 0;
            if(z>=img.getSz2()) z = img.getSz2() - 1;
            if(z<0) z= 0;

            sigmas = computePca(imgCZYX,(int) img.getSz0(),(int) img.getSz1(),(int) img.getSz2(),(int) x,(int) y,(int) z,0,(int) p2.radius);
            if(sigmas != null){
                sigma12 += Math.sqrt(sigmas[0])/Math.sqrt(sigmas[1]);
                sigma13 += Math.sqrt(sigmas[0])/Math.sqrt(sigmas[2]);
                sigmaCount++;
            }

            curIntensity = img.getValue(x,y,z,0);
            intensityMean += curIntensity;
            gradientMean += Math.abs(curIntensity - lastIntensity);
            lastIntensity = curIntensity;
            count++;
            p1 = p2;
        }
        intensityMean /= count;
        if(length>0){
            angleChangeMean /= length;
        }
        if(count-1>0){
            gradientMean /= (count-1);
        }else {
            gradientMean = 0;
        }
        if(sigmaCount>0){
            sigma12 /= sigmaCount;
            sigma13 /= sigmaCount;
        }

        x = (long) (headPoint.x + 0.5);
        y = (long) (headPoint.y + 0.5);
        z = (long) (headPoint.z + 0.5);
        if(x>=img.getSz0()) x = img.getSz0() - 1;
        if(x<0) x= 0;
        if(y>=img.getSz1()) y = img.getSz1() - 1;
        if(y<0) y= 0;
        if(z>=img.getSz2()) z = img.getSz2() - 1;
        if(z<0) z= 0;
        intensityStd += Math.pow(img.getValue(x,y,z,0) - intensityMean,2);
        p1 = headPoint;

        for(int i=1; i<points.size(); i++){
            NeuronSWC p2 = points.get(i);
            if(getNeuronSwcDistance(p1,p2)>1){
                x = (long) ((p1.x + p2.x)/2 + 0.5);
                y = (long) ((p1.y + p2.y)/2 + 0.5);
                z = (long) ((p1.z + p2.z)/2 + 0.5);
                if(x>=img.getSz0()) x = img.getSz0() - 1;
                if(x<0) x= 0;
                if(y>=img.getSz1()) y = img.getSz1() - 1;
                if(y<0) y= 0;
                if(z>=img.getSz2()) z = img.getSz2() - 1;
                if(z<0) z= 0;
                intensityStd += Math.pow(img.getValue(x,y,z,0) - intensityMean,2);
            }
            x = (long) (p2.x + 0.5);
            y = (long) (p2.y + 0.5);
            z = (long) (p2.z + 0.5);
            if(x>=img.getSz0()) x = img.getSz0() - 1;
            if(x<0) x= 0;
            if(y>=img.getSz1()) y = img.getSz1() - 1;
            if(y<0) y= 0;
            if(z>=img.getSz2()) z = img.getSz2() - 1;
            if(z<0) z= 0;
            intensityStd += Math.pow(img.getValue(x,y,z,0) - intensityMean,2);
            p1 = p2;
        }
        intensityStd = (float) Math.sqrt(intensityStd/count);

        double[] imgMeanStd = img.getMeanStdValue(0);
        if(imgMeanStd[0] > 0){
            intensityRatioToGlobal = (float) (intensityMean/imgMeanStd[0]);
        }else {
            intensityRatioToGlobal = 0;
        }

        NeuronTree branch = new NeuronTree();
        branch.listNeuron.addAll(points);

        Vector<MyMarker> inSwc = MyMarker.swcConvert(branch);
        int[] sz = new int[]{(int) img.getSz0(), (int) img.getSz1(), (int) img.getSz2()};
        byte[] maskR1 = MyMarker.swcToMask(inSwc,sz,0,1);
        byte[] maskR5 = MyMarker.swcToMask(inSwc,sz,5,5);
        float intensityR1 = 0, intensityR5 = 0;
        int countR1 = 0, countR5 = 0;
        byte[] img1d = img.getData();
        for(int i=0; i<img1d.length; i++){
            if(maskR1[i] == 1){
                intensityR1 += ByteTranslate.byte1ToInt(img1d[i]);
                countR1++;
            }
            if(maskR5[i] == 5){
                intensityR5 += ByteTranslate.byte1ToInt(img1d[i]);
                countR5++;
            }
        }
        if(countR1>0){
            intensityR1 /= countR1;
        }
        if(countR5>0){
            intensityR5 /= countR5;
        }
        if(intensityR5>0){
            intensityRatioToLocal = intensityR1/intensityR5;
        }else {
            intensityRatioToLocal = 0;
        }

        return true;
    }

    public float getDistance() {
        return distance;
    }

    public float getLength() {
        return length;
    }

    public float getAngleChangeMean() {
        return angleChangeMean;
    }

    public float getGradientMean() {
        return gradientMean;
    }

    public float getIntensityMean() {
        return intensityMean;
    }

    public float getIntensityStd() {
        return intensityStd;
    }

    public float getIntensityRatioToGlobal() {
        return intensityRatioToGlobal;
    }

    public float getIntensityRatioToLocal() {
        return intensityRatioToLocal;
    }

    public float getSigma12() {
        return sigma12;
    }

    public float getSigma13() {
        return sigma13;
    }

    public double[] computePca(int[][][][] imageCZYX, int sx, int sy, int sz, int x0, int y0, int z0, int chl, int r) {

        //获取边界
        int xb, xe, yb, ye, zb, ze;
        xb = x0 - r;
        if (xb < 0) xb = 0;
        else if (xb >= sx) xb = sx - 1;
        xe = x0 + r;
        if (xe < 0) xe = 0;
        else if (xe >= sx) xe = sx - 1;
        yb = y0 - r;
        if (yb < 0) yb = 0;
        else if (yb >= sy) yb = sy - 1;
        ye = y0 + r;
        if (ye < 0) ye = 0;
        else if (ye >= sy) ye = sy - 1;
        zb = z0 - r;
        if (zb < 0) zb = 0;
        else if (zb >= sz) zb = sz - 1;
        ze = z0 + r;
        if (ze < 0) ze = 0;
        else if (ze >= sz) ze = sz - 1;

        //计算质心
        float xm = 0, ym = 0, zm = 0, s = 0, mv = 0;
        float w;

        for (int k = zb; k <= ze; k++) {
            for (int j = yb; j <= ye; j++) {
                for (int i = xb; i <= xe; i++) {
                    w = imageCZYX[chl][k][j][i];
                    xm += w * i;
                    ym += w * j;
                    zm += w * k;
                    s += w;
                }
            }
        }

        if (s > 0) {
            xm /= s;
            ym /= s;
            zm /= s;
            mv = s / (float)((ze - zb + 1) * (ye - yb + 1) * (xe - xb + 1));//转型可能有问题
            //System.out.println("center of mass is (xm, ym, zm) = ("+xm+","+ym+","+zm+")");
        } else {
            System.out.println("Sum of window pixels equals or is smaller than 0. The window is not valid or some other problems in the data. Do nothing.");
            //score = 0;  //有问题
            return null;
        }

        //计算协方差

        float cc11 = 0, cc12 = 0, cc13 = 0, cc22 = 0, cc23 = 0, cc33 = 0;
        float dfx, dfy, dfz;
        for (int k = zb; k <= ze; k++) {
            dfz = (float)k - zm;
            //if (b_normalize_score) dfz /= maxrr;
            for (int j = yb; j <= ye; j++) {
                dfy = (float)j - ym;
                //if (b_normalize_score) dfy /= maxrr;
                for (int i = xb; i <= xe; i++) {
                    dfx = (float)i - xm;
                    //if (b_normalize_score) dfx /= maxrr;

                    //w = img3d[k][j][i]; //140128
                    w = imageCZYX[chl][k][j][i] - mv;
                    if (w < 0) w = 0; //140128 try the new formula

                    cc11 += w * dfx * dfx;
                    cc12 += w * dfx * dfy;
                    cc13 += w * dfx * dfz;
                    cc22 += w * dfy * dfy;
                    cc23 += w * dfy * dfz;
                    cc33 += w * dfz * dfz;
                }
            }
        }

        cc11 /= s;
        cc12 /= s;
        cc13 /= s;
        cc22 /= s;
        cc23 /= s;
        cc33 /= s;
        //System.out.println("convariance value ("+cc11+","+cc12+","+cc13+","+cc22+","+cc23+","+cc33+")");

        //获取矩阵特征值

        double[][] cov_matrix = new double[3][3];
        cov_matrix[0][0] = (double) cc11;
        cov_matrix[0][1] = cov_matrix[1][0] = (double) cc12;
        cov_matrix[1][1] = (double) cc22;
        cov_matrix[0][2] = cov_matrix[2][0] = (double) cc13;
        cov_matrix[1][2] = cov_matrix[2][1] = (double) cc23;
        cov_matrix[2][2] = (double) cc33;

        //定义一个矩阵
        Matrix A = new Matrix(cov_matrix);

        //计算特征值
        double[] eigenvalues = A.eig().getRealEigenvalues();  //两个函数的差别
        //System.out.println(Arrays.toString(eigenvalues));
        return eigenvalues;
    }
}
