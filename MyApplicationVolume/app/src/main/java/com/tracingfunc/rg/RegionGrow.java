package com.tracingfunc.rg;

import com.example.basic.ByteTranslate;
import com.example.basic.Image4DSimple;

public class RegionGrow {

    class RGPara{
        int ch,thIdx;
        double threshold;
        int volSz;
        boolean bFilterSmallobjs;
    }

    public static Image4DSimple reginGrowing(Image4DSimple inimg, RGPara para){
        Image4DSimple result = new Image4DSimple();

        if(!inimg.valid()){
            System.out.println("Please load img!");
        }

        if(inimg.getDatatype() != Image4DSimple.ImagePixelType.V3D_UINT8){
            System.out.println("Currently this program only support 8-bit data.");
        }

        int chRGB = para.ch - 1;

        int sz0 = (int) inimg.getSz0();
        int sz1 = (int) inimg.getSz1();
        int sz2 = (int) inimg.getSz2();
        int sz3 = (int) inimg.getSz3();

        int pageSZSub = sz0*sz1*sz2;
        int offsetSub = chRGB*pageSZSub;

        boolean vxy = true, vyz = true, vzx = true;

        int sx = sz0;
        int sy = sz1;
        int sz = sz2;
        int sc = sz3;

        int pageSZ = sx*sy*sz;
        double meanv = 0, stdv = 0;
        byte[] data1d;
        data1d = new byte[pageSZ];
//        for(int k = 0; k<sz; k++){
//            int offsetZ = k*sx*sy + offsetSub;
//            for(int j=0; j<sy; j++){
//                int offsetY = j*sx + offsetZ;
//                for(int i=0; i<sx; i++){
//                    data1d[i+offsetY] = inimg.getData()[i+offsetY];
//                    meanv += inimg.getData()[i+offsetY];
//                }
//            }
//        }
        for(int i=0; i<pageSZ; i++){
            data1d[i] = inimg.getData()[offsetSub+i];
            meanv += ByteTranslate.byte1ToInt(data1d[i]);
        }

        meanv /= pageSZ;
        for(int i=0; i<pageSZ; i++){
            stdv += Math.pow(((double) ByteTranslate.byte1ToInt(data1d[i]) - meanv),2);
        }
        stdv = Math.sqrt(stdv);

        System.out.println("meanv: " + meanv + " stdv:" + stdv);

        byte[] bw = new byte[pageSZ];

        if(para.thIdx == 0){
            para.threshold = meanv;
        }else if(para.thIdx == 1){
            para.threshold = meanv +stdv;
        }

        for(int i=0; i<pageSZ; i++){
            bw[i] = (byte) (((double)ByteTranslate.byte1ToInt(data1d[i])>para.threshold)?1:0);
        }

        int offsetY = sx;
        int offsetZ = sx*sy;

        int neighbors = 26;
        int[] neighborhood = new int[]{
                -1, 1, -offsetY, offsetY, -offsetZ, offsetZ,
                -offsetY-1, -offsetY+1, -offsetY-offsetZ, -offsetY+offsetZ,
                offsetY-1, offsetY+1, offsetY-offsetZ, offsetY+offsetZ,
                offsetZ-1, offsetZ+1, -offsetZ-1, -offsetZ+1,
                -1-offsetY-offsetZ, -1-offsetY+offsetZ, -1+offsetY-offsetZ, -1+offsetY+offsetZ,
                1-offsetY-offsetZ, 1-offsetY+offsetZ, 1+offsetY-offsetZ, 1+offsetY+offsetZ
        };

        for(int k=0; k<sz; k++){
            int idxK = k*offsetZ;
            for(int j=0 ; j<sy; j++){
                int idxJ = idxK + j*offsetY;
                for(int i=0, idx = idxJ; i<sx; i++, idx++){
                    if(i==0 || i==sx-1 || j==0 || j==sy-1 || k==0 || k==sz-1)
                        continue;
                    if(bw[idx]>0){
                        boolean onePoint = true;
                        for(int ineighbor = 0; ineighbor<neighbors; ineighbor++ ){
                            int nIdx = idx + neighborhood[ineighbor];
                            if(bw[nIdx]>0){
                                onePoint = false;
                                break;
                            }
                        }
                        if(onePoint){
                            bw[idx] = 0;
                        }
                    }
                }
            }
        }

        //save BW image


        // 3D region growing




        return result;
    }

}
