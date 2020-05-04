package com.learning.pixelclassification;

import com.example.basic.ByteTranslate;
import com.example.basic.Image4DSimple;
import com.example.basic.NeuronTree;
import com.learning.feature_select.DifferenceOfGaussian;
import com.learning.feature_select.GaussianBlur;
import com.learning.feature_select.LaplacianOfGaussian;
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

        Vector<NeuronTree> labels = nt.splitNeuronTreeByType();
        int C = labels.size();

        List<int[]> masks = new ArrayList<>(C);
        int[] sz = new int[]{(int) inImg.getSz0(), (int) inImg.getSz1(), (int) inImg.getSz2()};
        int[] flag = new int[]{1,2,3,4,5,6,7,8,9,10};
        for(int i=0; i<C; i++){
            Vector<MyMarker> inswc = MyMarker.swcConvert(labels.get(i));
            int[] mask = MyMarker.swcToMask(inswc,sz,1, flag[i]);
            masks.add(mask);
        }

        int[] maskFinal = new int[sz[0]*sz[1]*sz[2]];
        for(int i=0; i<maskFinal.length; i++)
            maskFinal[i] = 0;
        for(int i=0; i<masks.size(); i++){
            for(int j=0; j<masks.get(i).length; j++){
                if(maskFinal[j] == 0 && masks.get(i)[j] != 0)
                    maskFinal[j] = masks.get(i)[j];
            }
        }

        if(inImg.getDatatype() != Image4DSimple.ImagePixelType.V3D_UINT8){
            System.out.println("it is only support V3D_UINT8");
        }

        ArrayList<int[]> pixelsFeature = new ArrayList<>();
        byte[] img1dByte = inImg.getData();
        int[] img1d = new int[img1dByte.length];
        for(int i=0; i<img1dByte.length; i++){
            img1d[i] = ByteTranslate.byte1ToInt(img1dByte[i]);

        }

        for(int i=0; i<selections.length; i++){
            for(int j=0; j<sigmaScales.length; j++){
                double sigma = sigmaScales[j];
                if(selections[i][j]){
                    int[] outFeature = getFeature(img1d,sz,sigma,featureName[i]);
                    pixelsFeature.add(outFeature);
                }
            }
        }

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
                int[] features = new int[M+1];
                if (dataRandomForest.get(i).length - 1 >= 0)
                    System.arraycopy(dataRandomForest.get(i), 0, features, 0, dataRandomForest.get(i).length - 1);
                features[M] = maskFinal[i];
                testData.add(features);
            }
        }

        int numTrees = 100;
        RandomForest rf = new RandomForest(numTrees,trainData,testData);
        rf.C = C;
        rf.M = M;
        rf.Ms = (int) Math.round(Math.log(rf.M)/Math.log(2)+1);
        rf.start();

        int[] resultClassification = new int[dataRandomForest.size()];

        for(int i=0; i<dataRandomForest.size(); i++){
            resultClassification[i] = rf.Evaluate(dataRandomForest.get(i));
        }

        int[] pixelIntensity = new int[C];
        int step = 255/(C-1);
        for(int i=0; i<C; i++){
            pixelIntensity[i] = i*step;
        }

        byte[] data = new byte[dataRandomForest.size()];
        for(int i=0; i<dataRandomForest.size(); i++){
            data[i] = (byte) pixelIntensity[resultClassification[i] - 1];
        }

        result.setDataFromImage(data,inImg.getSz0(),inImg.getSz1(),inImg.getSz2(),inImg.getSz3(),Image4DSimple.ImagePixelType.V3D_UINT8,inImg.getIsBig());

        return result;
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
}
