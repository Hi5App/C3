package com.penglab.hi5.basic.learning.pixelclassification;

import com.penglab.hi5.basic.ByteTranslate;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.learning.feature_select.DifferenceOfGaussian;
import com.penglab.hi5.basic.learning.feature_select.GaussianBlur;
import com.penglab.hi5.basic.learning.feature_select.LaplacianOfGaussian;
import com.penglab.hi5.basic.learning.filter.ConvolutionOptions;
import com.penglab.hi5.basic.learning.filter.MultiConvolution;
import com.penglab.hi5.basic.learning.randomforest.RandomForest;
import com.penglab.hi5.basic.tracingfunc.app2.MyMarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class PixelClassification {
    public static final double[] sigmaScales = new double[]{0.3,0.7,1,1.6,3.5,5.0,10.0};
    public static final String[] featureName = new String[]{"GaussianSmoothing",
            "LaplacianOfGaussian",
            "StructureTensorEigenvalues",
            "HessianOfGaussianEigenvalues",
            "GaussianGradientMagnitude",
            "DifferenceOfGaussians"};
    public boolean[][] selections = new boolean[][]{
            {true,false,false,false,false,false,false},
            {true,false,false,false,false,false,false},
            {true,false,false,false,false,false,false},
            {false,false,false,false,false,false,false},
            {false,false,false,false,false,false,false},
            {false,false,false,false,false,false,false}
    };

    private boolean is128Cube;

    public PixelClassification(){
        is128Cube = false;
    }

    public Image4DSimple getPixelClassificationResult(Image4DSimple inImg, NeuronTree nt) throws Exception{

        long startTime = System.currentTimeMillis();

        Image4DSimple result = new Image4DSimple();

        double dFactorXY = 1, dFactorZ = 1;
        Image4DSimple downSampleImg = new Image4DSimple();

        if(is128Cube){

            double size = 128;

            int[] inSZ = new int[]{(int) inImg.getSz0(), (int) inImg.getSz1(), (int) inImg.getSz2()};

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


            Image4DSimple.resample3dimg_interp(downSampleImg,inImg,dFactorXY,dFactorXY,dFactorZ,1);

            if(dFactorXY>1 || dFactorZ>1){
                for(int i=0; i<nt.listNeuron.size(); i++){
                    nt.listNeuron.get(i).x /= dFactorXY;
                    nt.listNeuron.get(i).y /= dFactorXY;
                    nt.listNeuron.get(i).z /= dFactorZ;
                }
            }
        }else {
            downSampleImg.setData(inImg);
        }



        Vector<NeuronTree> labels = nt.splitNeuronTreeByType();
        Vector<NeuronTree> labelFB = new Vector<NeuronTree>();
        for(int i=0; i<labels.size(); i++){
            if(labels.get(i).listNeuron.get(0).type == 3){
                labelFB.add(labels.get(i));
            }
        }
        if(labelFB.isEmpty()){
            throw new Exception("Please add foreground label!");
        }
        for(int i=0; i<labels.size(); i++){
            if(labels.get(i).listNeuron.get(0).type == 2){
                labelFB.add(labels.get(i));
            }
        }
        int C = 2;

        boolean bg = false;

        if(labelFB.size()!=C){
            bg = true;
        }

        List<byte[]> masks = new ArrayList<>(C);
        int[] sz = new int[]{(int) downSampleImg.getSz0(), (int) downSampleImg.getSz1(), (int) downSampleImg.getSz2()};
//        int[] flag = new int[]{1,2,3,4,5,6,7,8,9,10};

        Vector<MyMarker> inSwc = MyMarker.swcConvert(labelFB.get(0));
//        int[] mask = MyMarker.swcToMask(inSwc,sz,0.5, 2);
        byte[] mask = MyMarker.swcToMask2(inSwc,sz,2);
        masks.add(mask);

        if(!bg){
            Vector<MyMarker> inSwc2 = MyMarker.swcConvert(labelFB.get(1));
//            int[] mask2 = MyMarker.swcToMask(inSwc2,sz,0.5, 1);
            byte[] mask2 = MyMarker.swcToMask2(inSwc2,sz,1);
            masks.add(mask2);
        }


        byte[] maskFinal = new byte[sz[0]*sz[1]*sz[2]];
        Arrays.fill(maskFinal, (byte) 0);
        for(int i=0; i<masks.size(); i++){
            for(int j=0; j<masks.get(i).length; j++){
                if(maskFinal[j] == 0 && masks.get(i)[j] != 0) {
//                    System.out.println("mask is not zero");
                    maskFinal[j] = (byte) masks.get(i)[j];
                }
            }
        }

        if(downSampleImg.getDatatype() != Image4DSimple.ImagePixelType.V3D_UINT8){
            System.out.println("it is only support V3D_UINT8");
        }

        ArrayList<float[]> partPixelsFeature = new ArrayList<>();
        byte[] img1dByte = downSampleImg.getData();
        int[] img1d = new int[img1dByte.length];
        for(int i=0; i<img1dByte.length; i++){
            img1d[i] = ByteTranslate.byte1ToInt(img1dByte[i]);
        }

        double mean = 0, std = 0;
        int count = 0;
        for(int i=0; i<maskFinal.length; i++){
            if(maskFinal[i] == 2){
                mean += ByteTranslate.byte1ToInt(img1dByte[i]);
//                max = Math.max(max,img1d[i]);
                count++;
            }
        }
        mean /= count;

        for(int i=0; i<maskFinal.length; i++){
            if(maskFinal[i] == 2){
                std += (ByteTranslate.byte1ToInt(img1dByte[i])-mean)*(ByteTranslate.byte1ToInt(img1dByte[i])-mean);
            }
        }
        std = Math.sqrt(std/count);
        System.out.println("mean-std: "+(mean-std));
        if(bg){
            double th = Math.max(mean-std,30);

            ArrayList<Integer> randomNumber = new ArrayList<>();
            for(int i=0; i<maskFinal.length; i++){
                randomNumber.add(i);
            }
            Collections.shuffle(randomNumber);

            int countB = 0;
            for(int i=0; i<randomNumber.size(); i++){
                if(maskFinal[randomNumber.get(i)] != 2 && ByteTranslate.byte1ToInt(img1dByte[randomNumber.get(i)] )< th){
                    maskFinal[randomNumber.get(i)] = 1;
                    countB++;
                    if(countB>=count*2)
                        break;
                }
            }
        }

        byte[] featureMask0 = new byte[img1dByte.length];
        double min = Math.max(mean-std,30);
        double max = mean +std;
        System.out.println("min: "+min);
        System.out.println("max: "+max);

        int f = 0, b = 0;
        double margin = 5;
        for(int i=0; i<img1dByte.length; i++){
            if(maskFinal[i] != 0 || (ByteTranslate.byte1ToInt(img1dByte[i]) >= min - margin /*&& img1d[i] <= max + margin*/)){
                featureMask0[i] = 1;
                f++;
            }else {
                featureMask0[i] = 0;
                b++;
            }
        }
        System.out.println("f: "+f+" b: "+b);

//        byte[] data = new byte[img1dByte.length*3];
//        Arrays.fill(data, (byte) 0);
//        for(int i=0; i<img1dByte.length; i++){
//            data[i] = img1dByte[i];
//        }
//        for(int i=img1dByte.length; i<img1dByte.length*2; i++){
//            if(featureMask0[i-img1dByte.length] == 1){
//                data[i] = (byte) 60;
//            }else {
//                data[i] = (byte) 0;
//            }
//        }
//        result.setDataFromImage(data,downSampleImg.getSz0(),downSampleImg.getSz1(),downSampleImg.getSz2(),3,downSampleImg.getDatatype(),downSampleImg.getIsBig());


        int[] featureMask = new int[img1d.length];
        Arrays.fill(featureMask,0);
        for(int k=0; k<sz[2]; k++){
            for(int j=0; j<sz[1]; j++){
                for(int i=0; i<sz[0]; i++){
                    int index = k*sz[1]*sz[0] + j*sz[0] + i;
                    if(featureMask0[index] == 0){
                        end:
                        for(int kk=-1; kk<=1; kk++){
                            int nbk = k + kk;
                            if(nbk<0 || nbk>=sz[2])
                                continue;
                            for(int jj=-1; jj<=1; jj++){
                                int nbj = j + jj;
                                if(nbj<0 || nbj>=sz[1])
                                    continue;
                                for(int ii=-1; ii<=1; ii++){
                                    int nbi = i + ii;
                                    if(nbi<0 || nbi>=sz[0])
                                        continue;
                                    int d = Math.abs(kk) + Math.abs(jj) + Math.abs(ii);
                                    if(d == 0)
                                        continue;
                                    int nbIndex = nbk*sz[1]*sz[0] + nbj*sz[0] + nbi;
                                    if(featureMask0[nbIndex] == 1){
                                        featureMask[index] = 1;
                                        break end;
                                    }
                                }
                            }
                        }

                    }else if(featureMask0[index] == 1){
                        featureMask[index] = 1;
                    }
                }
            }
        }
        int afterF = 0;
        for(int i=0; i<featureMask.length; i++){
            if(featureMask[i] == 1)
                afterF++;
//            else
//                featureMask[i] = 1;
        }
        System.out.println("afterF: "+afterF);

        Vector<Integer> featureMaskIndex = new Vector<>();
        for(int i=0; i<featureMask.length; i++){
            if(featureMask[i] == 1)
                featureMaskIndex.add(i);
        }


        System.out.println("---------------get feature-----------");
        for(int i=0; i<selections.length; i++){
            for(int j=0; j<sigmaScales.length; j++){
                double sigma = sigmaScales[j];
                if(selections[i][j]){
                    int[] outFeature = getFilterFeature(img1d,sz,featureMask,sigma,featureName[i]);
//                    int[] outFeature = getFeature(img1d,sz,featureMask,sigma,featureName[i]);
                    float[] partOutFeature = new float[featureMaskIndex.size()];
                    for(int k=0; k<featureMaskIndex.size(); k++){
                        int index = featureMaskIndex.get(k);
                        partOutFeature[k] = outFeature[index];
                    }
                    partPixelsFeature.add(partOutFeature);
                }
            }
        }

        System.out.println("feature size: "+partPixelsFeature.size());

        ArrayList<float[]> dataRandomForest = new ArrayList<>();
        int M = partPixelsFeature.size();
        for(int i=0; i<featureMaskIndex.size(); i++){
            float[] features = new float[M+1];
            for(int j=0; j<M; j++){
                features[j] = partPixelsFeature.get(j)[i];
            }
            features[M] = 0;
            dataRandomForest.add(features);
        }

        ArrayList<float[]> trainData = new ArrayList<>();
        ArrayList<float[]> testData = new ArrayList<>();

//        if(bg){
//            ArrayList<int[]> data1 = new ArrayList<>();
//            ArrayList<int[]> data2 = new ArrayList<>();
//            for(int i=0; i<maskFinal.length; i++){
//                if(maskFinal[i] == 1){
//                    int[] features = new int[M+1];
//                    if (dataRandomForest.get(i).length - 1 >= 0)
//                        System.arraycopy(dataRandomForest.get(i), 0, features, 0, dataRandomForest.get(i).length - 1);
//                    features[M] = maskFinal[i];
//                    data1.add(features);
//                }else if(maskFinal[i] == 2){
//                    int[] features = new int[M+1];
//                    if (dataRandomForest.get(i).length - 1 >= 0)
//                        System.arraycopy(dataRandomForest.get(i), 0, features, 0, dataRandomForest.get(i).length - 1);
//                    features[M] = maskFinal[i];
//                    data2.add(features);
//                }
//            }
//            if(data1.size()>data2.size()*2){
//                Collections.shuffle(data1);
//                for(int i=0; i<data2.size()*2; i++){
//                    trainData.add(data1.get(i));
//                }
//                trainData.addAll(data2);
//            }else {
//                trainData.addAll(data1);
//                trainData.addAll(data2);
//            }
//        }else{
        {
            for(int i=0; i<dataRandomForest.size(); i++){
                if(maskFinal[featureMaskIndex.get(i)] != 0){
//                System.out.println("-------------feature------------");
                    float[] features = new float[M+1];
                    if (dataRandomForest.get(i).length - 1 >= 0)
                        System.arraycopy(dataRandomForest.get(i), 0, features, 0, dataRandomForest.get(i).length - 1);
                    features[M] = maskFinal[featureMaskIndex.get(i)];
                    trainData.add(features);
                }
            }
        }

        int numTrees = 100;
        RandomForest rf = new RandomForest(numTrees,trainData,testData);
        rf.C = C;
        rf.M = M;
        rf.Ms = (int) Math.round(Math.log(rf.M)/Math.log(2)+1);
        rf.start();

        int[] resultClassification = new int[featureMask.length];
        Arrays.fill(resultClassification,0);

        System.out.println("----------------------classification-----------------");

        for(int i=0; i<dataRandomForest.size(); i++){
            resultClassification[featureMaskIndex.get(i)] = rf.Evaluate(dataRandomForest.get(i));
//            if(featureMask[i] == 0){
//                resultClassification[i] = 0;
//            }else {
//                resultClassification[i] = rf.Evaluate(dataRandomForest.get(i));
//            }
//            if(i%100==0){
//                System.out.println(i+" : "+resultClassification[i]);
//            }
        }

        int[] pixelIntensity = new int[C];
        int step = 60/(C-1);
        for(int i=0; i<C; i++){
            pixelIntensity[i] = i*step;
        }

        byte[] data = new byte[img1dByte.length*3];
        Arrays.fill(data, (byte) 0);
        for(int i=0; i<img1dByte.length; i++){
            data[i] = img1dByte[i];
        }
        for(int i=img1dByte.length; i<img1dByte.length*2; i++){
            data[i] = (byte) pixelIntensity[resultClassification[i-img1dByte.length]];
//            if(featureMask0[i-img1dByte.length] == 1){
//                data[i] = (byte) 60;
//            }else {
//                data[i] = (byte) 0;
//            }
        }
        result.setDataFromImage(data,downSampleImg.getSz0(),downSampleImg.getSz1(),downSampleImg.getSz2(),3,downSampleImg.getDatatype(),downSampleImg.getIsBig());

//        byte[] data = new byte[featureMask.length];
//        for(int i=0; i<featureMask.length; i++){
//            data[i] = (byte) pixelIntensity[resultClassification[i]];
//        }

//        result.setDataFromImage(data,downSampleImg.getSz0(),downSampleImg.getSz1(),downSampleImg.getSz2(),downSampleImg.getSz3(),Image4DSimple.ImagePixelType.V3D_UINT8,downSampleImg.getIsBig());

        System.out.println("cost time: "+(double)(System.currentTimeMillis()-startTime)/1000+" s");
        if(dFactorXY>1 || dFactorZ>1){
            Image4DSimple upSampleImg = new Image4DSimple();
            System.out.println("--------------upSample--------------");
            Image4DSimple.upsample3dimg_interp(upSampleImg,result,dFactorXY,dFactorXY,dFactorZ,1);
            System.out.println("----------------over------------------");
            return upSampleImg;
        }else {
            return result;
        }
    }

    public void setIs128Cube(boolean is128Cube) {
        this.is128Cube = is128Cube;
    }

    public void setSelections(boolean[][] selections) {
        this.selections = selections;
        for(int i=0; i<featureName.length; i++){
            if(i!=0){
                this.selections[i][0] = false;
            }
        }
    }

    public int[] getFeature(int[] inPixel, int[] sz, int[] mask, double sigma, String method){
        int[] outPixel = new int[inPixel.length];
        if(method == featureName[0]){
            GaussianBlur g = new GaussianBlur(3, (float) sigma,3);
            GaussianBlur.convolveAndTranspose(g.getKernel(),inPixel,mask,outPixel,sz[0],sz[1],sz[2],1);
        }else if(method == featureName[1]){
            LaplacianOfGaussian.filter3d((float) sigma,inPixel,mask,outPixel,sz[0],sz[1],sz[2],1);
        }else if(method == featureName[5]){
            DifferenceOfGaussian.filter3d((float) sigma,inPixel,mask,outPixel,sz[0],sz[1],sz[2],1);
        }
        return outPixel;
    }

    public int[] getFilterFeature(int[] inPixel, int[] sz, int[] mask, double sigma, String method){
        int[] outPixel = new int[inPixel.length];
        ConvolutionOptions opt = new ConvolutionOptions(sz.length, (float) sigma);
        if(method == featureName[0]){
            MultiConvolution.gaussianSmoothMultiArray(inPixel,sz,mask,outPixel,opt);
        }else if(method == featureName[1]){
            MultiConvolution.laplacianOfGaussianMultiArray(inPixel,sz,mask,outPixel,opt);
        }else if(method == featureName[2]){
            MultiConvolution.structureTensorMultiArray(inPixel,sz,mask,outPixel,opt);
        }else if(method == featureName[3]){
            int N = sz.length;
            int M = N*(N+1)/2;
            int[] tmpPixl = new int[inPixel.length*M];
            MultiConvolution.hessianOfGaussianMultiArray(inPixel,sz,mask,tmpPixl,opt);
            for(int i=0; i<outPixel.length; i++){
                outPixel[i] = tmpPixl[i];
            }
        }else if(method == featureName[4]){
            MultiConvolution.gaussianGradientMultiArray(inPixel,sz,mask,outPixel,opt);
        }else if(method == featureName[5]){
            int[] tmpPixl = new int[inPixel.length];
            MultiConvolution.gaussianSmoothMultiArray(inPixel,sz,mask,tmpPixl,opt);
            ConvolutionOptions opt0 = new ConvolutionOptions(sz.length,0.3f);
            MultiConvolution.gaussianSmoothMultiArray(inPixel,sz,mask,outPixel,opt0);

            for(int i=0; i<outPixel.length; i++){
                outPixel[i] = tmpPixl[i] - outPixel[i];
            }
        }
        return outPixel;
    }
}
