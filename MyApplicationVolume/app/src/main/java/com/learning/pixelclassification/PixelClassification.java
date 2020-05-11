package com.learning.pixelclassification;

import com.example.basic.ByteTranslate;
import com.example.basic.Image4DSimple;
import com.example.basic.NeuronTree;
import com.learning.feature_select.DifferenceOfGaussian;
import com.learning.feature_select.GaussianBlur;
import com.learning.feature_select.LaplacianOfGaussian;
import com.learning.filter.ConvolutionOptions;
import com.learning.filter.MultiConvolution;
import com.learning.randomforest.RandomForest;
import com.tracingfunc.app2.MyMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PixelClassification {
    public static double[] sigmaScales = new double[]{0.3,0.7,1,1.6,3.5,5.0,10.0};
    public static String[] featureName = new String[]{"GaussianSmoothing",
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

    public Image4DSimple getPixelClassificationResult(Image4DSimple inImg, NeuronTree nt) throws Exception{
        Image4DSimple result = new Image4DSimple();

        double dFactorXY,dFactorZ;
        int[] inSZ = new int[]{(int) inImg.getSz0(), (int) inImg.getSz1(), (int) inImg.getSz2()};

        if (inSZ[0]<=32 && inSZ[1]<=32 && inSZ[2]<=32)
        {
            dFactorZ = dFactorXY = 1;
        }
        else if (inSZ[0] >= 2*inSZ[2] || inSZ[1] >= 2*inSZ[2])
        {
            if (inSZ[2]<=32)
            {
                double MM = inSZ[0];
                if (MM<inSZ[1]) MM=inSZ[1];
                dFactorXY = MM / 32.0;
                dFactorZ = 1;
            }
            else
            {
                double MM = inSZ[0];
                if (MM<inSZ[1]) MM=inSZ[1];
                if (MM<inSZ[2]) MM=inSZ[2];
                dFactorXY = dFactorZ = MM / 32.0;
            }
        }
        else
        {
            double MM = inSZ[0];
            if (MM<inSZ[1]) MM=inSZ[1];
            if (MM<inSZ[2]) MM=inSZ[2];
            dFactorXY = dFactorZ = MM / 32.0;
        }

        Image4DSimple downSampleImg = new Image4DSimple();
        Image4DSimple.resample3dimg_interp(downSampleImg,inImg,dFactorXY,dFactorXY,dFactorZ,1);

        if(dFactorXY>1 || dFactorZ>1){
            for(int i=0; i<nt.listNeuron.size(); i++){
                nt.listNeuron.get(i).x /= dFactorXY;
                nt.listNeuron.get(i).y /= dFactorXY;
                nt.listNeuron.get(i).z /= dFactorZ;
            }
        }

        Vector<NeuronTree> labels = nt.splitNeuronTreeByType();
        Vector<NeuronTree> labelFB = new Vector<NeuronTree>();
        for(int i=0; i<labels.size(); i++){
            if(labels.get(i).listNeuron.get(0).type == 2){
                labelFB.add(labels.get(i));
            }
        }
        for(int i=0; i<labels.size(); i++){
            if(labels.get(i).listNeuron.get(0).type == 3){
                labelFB.add(labels.get(i));
            }
        }
        int C = labelFB.size();
        System.out.println("C: "+C);

        if(C != 2){
            throw new Exception("Please add two label!");
        }

        List<int[]> masks = new ArrayList<>(C);
        int[] sz = new int[]{(int) downSampleImg.getSz0(), (int) downSampleImg.getSz1(), (int) downSampleImg.getSz2()};
        int[] flag = new int[]{1,2,3,4,5,6,7,8,9,10};
        for(int i=0; i<C; i++){
            Vector<MyMarker> inswc = MyMarker.swcConvert(labelFB.get(i));
            int[] mask = MyMarker.swcToMask(inswc,sz,1, flag[i]);
            masks.add(mask);
        }

        int[] maskFinal = new int[sz[0]*sz[1]*sz[2]];
        for(int i=0; i<maskFinal.length; i++)
            maskFinal[i] = 0;
        for(int i=0; i<masks.size(); i++){
            for(int j=0; j<masks.get(i).length; j++){
                if(maskFinal[j] == 0 && masks.get(i)[j] != 0) {
                    System.out.println("mask is not zero");
                    maskFinal[j] = masks.get(i)[j];
                }
            }
        }

        if(downSampleImg.getDatatype() != Image4DSimple.ImagePixelType.V3D_UINT8){
            System.out.println("it is only support V3D_UINT8");
        }

        ArrayList<int[]> pixelsFeature = new ArrayList<>();
        byte[] img1dByte = downSampleImg.getData();
        int[] img1d = new int[img1dByte.length];
        for(int i=0; i<img1dByte.length; i++){
            img1d[i] = ByteTranslate.byte1ToInt(img1dByte[i]);

        }

        System.out.println("---------------get feature-----------");
        for(int i=0; i<selections.length; i++){
            for(int j=0; j<sigmaScales.length; j++){
                double sigma = sigmaScales[j];
                if(selections[i][j]){
                    int[] outFeature = getFilterFeature(img1d,sz,sigma,featureName[i]);
                    pixelsFeature.add(outFeature);
                }
            }
        }

        System.out.println("feature size: "+pixelsFeature.size());

        ArrayList<int[]> dataRandomForest = new ArrayList<>();
        int M = pixelsFeature.size();
        for(int i=0; i<img1dByte.length; i++){
            int[] features = new int[M+1];
            for(int j=0; j<M; j++){
                features[j] = pixelsFeature.get(j)[i];
            }
            features[M] = 0;
            dataRandomForest.add(features);
        }

        ArrayList<int[]> trainData = new ArrayList<>();
        ArrayList<int[]> testData = new ArrayList<>();

        for(int i=0; i<maskFinal.length; i++){
            if(maskFinal[i] != 0){
                System.out.println("-------------feature------------");
                int[] features = new int[M+1];
                if (dataRandomForest.get(i).length - 1 >= 0)
                    System.arraycopy(dataRandomForest.get(i), 0, features, 0, dataRandomForest.get(i).length - 1);
                features[M] = maskFinal[i];
                trainData.add(features);
            }
        }

        int numTrees = 100;
        RandomForest rf = new RandomForest(numTrees,trainData,testData);
        rf.C = C;
        rf.M = M;
        rf.Ms = (int) Math.round(Math.log(rf.M)/Math.log(2)+1);
        rf.start();

        int[] resultClassification = new int[dataRandomForest.size()];

        System.out.println("----------------------classification-----------------");

        for(int i=0; i<dataRandomForest.size(); i++){
            resultClassification[i] = rf.Evaluate(dataRandomForest.get(i));
//            if(i%100==0){
//                System.out.println(i+" : "+resultClassification[i]);
//            }
        }

        int[] pixelIntensity = new int[C];
        int step = 255/(C-1);
        for(int i=0; i<C; i++){
            pixelIntensity[i] = i*step;
        }

        byte[] data = new byte[dataRandomForest.size()];
        for(int i=0; i<dataRandomForest.size(); i++){
            data[i] = (byte) pixelIntensity[resultClassification[i]];
        }

        result.setDataFromImage(data,downSampleImg.getSz0(),downSampleImg.getSz1(),downSampleImg.getSz2(),downSampleImg.getSz3(),Image4DSimple.ImagePixelType.V3D_UINT8,downSampleImg.getIsBig());

        if(dFactorXY>1 || dFactorZ>1){
            Image4DSimple upSampleImg = new Image4DSimple();
            Image4DSimple.upsample3dimg_interp(upSampleImg,result,dFactorXY,dFactorXY,dFactorZ,1);
            return upSampleImg;
        }else {
            return result;
        }
    }

    public void setSelections(boolean[][] selections) {
        this.selections = selections;
        for(int i=0; i<featureName.length; i++){
            if(i!=0){
                this.selections[i][0] = false;
            }
        }
    }

    public int[] getFeature(int[] inPixel, int[] sz, double sigma, String method){
        int[] outPixel = new int[inPixel.length];
        if(method == featureName[0]){
            GaussianBlur g = new GaussianBlur(3, (float) sigma,3);
            GaussianBlur.convolveAndTranspose(g.getKernel(),inPixel,outPixel,sz[0],sz[1],sz[2],1);
        }else if(method == featureName[1]){
            LaplacianOfGaussian.filter3d((float) sigma,inPixel,outPixel,sz[0],sz[1],sz[2],1);
        }else if(method == featureName[5]){
            DifferenceOfGaussian.filter3d((float) sigma,inPixel,outPixel,sz[0],sz[1],sz[2],1);
        }
        return outPixel;
    }

    public int[] getFilterFeature(int[] inPixel, int[] sz, double sigma, String method){
        int[] outPixel = new int[inPixel.length];
        ConvolutionOptions opt = new ConvolutionOptions(sz.length, (float) sigma);
        if(method == featureName[0]){
            MultiConvolution.gaussianSmoothMultiArray(inPixel,sz,outPixel,opt);
        }else if(method == featureName[1]){
            MultiConvolution.laplacianOfGaussianMultiArray(inPixel,sz,outPixel,opt);
        }else if(method == featureName[2]){
            MultiConvolution.structureTensorMultiArray(inPixel,sz,outPixel,opt);
        }else if(method == featureName[3]){
            int N = sz.length;
            int M = N*(N+1)/2;
            int[] tmpPixl = new int[inPixel.length*M];
            MultiConvolution.hessianOfGaussianMultiArray(inPixel,sz,tmpPixl,opt);
            for(int i=0; i<outPixel.length; i++){
                outPixel[i] = tmpPixl[i];
            }
        }else if(method == featureName[4]){
            MultiConvolution.gaussianGradientMultiArray(inPixel,sz,outPixel,opt);
        }else if(method == featureName[5]){
            int[] tmpPixl = new int[inPixel.length];
            MultiConvolution.gaussianSmoothMultiArray(inPixel,sz,tmpPixl,opt);
            ConvolutionOptions opt0 = new ConvolutionOptions(sz.length,0.3f);
            MultiConvolution.gaussianSmoothMultiArray(inPixel,sz,outPixel,opt0);

            for(int i=0; i<outPixel.length; i++){
                outPixel[i] = tmpPixl[i] - outPixel[i];
            }
        }
        return outPixel;
    }
}
