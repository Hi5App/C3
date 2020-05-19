package com.tracingfunc.gsdt;

import java.util.ArrayList;
import com.example.basic.Image4DSimple;
import com.example.basic.ImageMarker;
import com.example.basic.RGBA8;
import com.tracingfunc.app2.FM;
import com.tracingfunc.gsdt.FM_GSDT;
import com.tracingfunc.gsdt.ParaGSDT;

import static com.example.basic.Image4DSimple.downsampling_img_xyz;
import static com.example.basic.Image4DSimple.loadImage;
import static java.lang.System.*;

public class GSDT {
    public static boolean GSDT_Fun(ParaGSDT p) throws Exception {
        //Image4DSimple inimg = loadImage(img_filepath,img_filetype);
        if(!p.p4DImage.valid()){
            out.println("image ia invalid!");
            return false;
        }
        Image4DSimple inimg = p.p4DImage;
        long sz0 = inimg.getSz0();
        long sz1 = inimg.getSz1();
        long sz2 = inimg.getSz2();
        long sz3 = inimg.getSz3();
        out.println(sz0+" "+sz1+" "+sz2+" "+sz3);
        //int bkg_thresh = p.bkg_thresh;
        int cnn_type = p.cnn_type;
        int channel = p.channel;
        int z_thickness = p.z_thickness;
        //[][][] phi = new float[(int) sz0][(int) sz1][(int) sz2];
        p.phi = new float[(int) sz2][(int) sz1][(int) sz0];
        int[] sz = new int[]{(int) sz0,(int) sz1,(int) sz2};
        out.println("FM_GSDTfun..");

        //reset the para bkg_thresh(eg.image_ave)
        if (channel >=0 && channel <= sz3-1)
        {
            double imgAve, imgStd;
            double[] meanStd = inimg.getMeanStdValue(0);
            imgAve = meanStd[0];
            imgStd = meanStd[1];
//            p.bkg_thresh = imgAve; //+0.5*imgStd ; //(imgAve < imgStd)? imgAve : (imgAve+imgStd)*.5;
            double td= (imgStd<10)? 10: imgStd;
            p.bkg_thresh = (int) (imgAve +0.5*td) ; //(imgAve < imgStd)? imgAve : (imgAve+imgStd)*.5; //20170523, PHC
        }

        int bkg_thresh = p.bkg_thresh;
        FM_GSDT.gsdt_FM(inimg.getDataCZYX()[0],p.phi, sz, cnn_type, bkg_thresh);//re
        //FM.fastmarching_dt(inimg.getDataCZYX()[0],p.phi, sz, cnn_type, bkg_thresh);
        out.println("FM_GSDTfun..finished");
        p.max_val = p.phi[0][0][0];
        out.println("max_val_origin:" + p.max_val);
        for (int k=0;k<sz2;k++) {
            for (int j=0;j<sz1;j++)
                for (int i=0;i<sz0;i++)
                {
                    /*
                    if(phi[k][j][i]>100){
                        out.println(i +" "+ j + " "+ k+ " : "+phi[k][j][i]);
                    }

                     */
                    if(p.phi[k][j][i] > p.max_val){
                        p.max_val = p.phi[k][j][i];
                        p.max_loc[0] = i;
                        p.max_loc[1] = j;
                        p.max_loc[2] = k;
                    }
                }
        }
        out.println("max_loc:" + p.max_loc[0] + p.max_loc[1] + p.max_loc[2]);
        out.println("max_val:" + p.max_val);

        //prepare the processed img format
        int[][][][] outimage = new int[(int) sz3][(int) sz2][(int) sz1][(int) sz0];//CZYX
        for(int zz=0;zz<sz2;zz++){
            for(int yy=0;yy<sz1;yy++){
                for(int xx=0;xx<sz0;xx++){
                    outimage[0][zz][yy][xx] = (int)p.phi[zz][yy][xx];
                }
            }
        }
        p.outImage.setDataFormCZYX(outimage,sz0,sz1,sz2,sz3,p.p4DImage.getDatatype(),p.p4DImage.getIsBig());


        //Marker --mark the location with max_value
        ImageMarker marker_new = new ImageMarker(p.max_loc[0], p.max_loc[1], p.max_loc[2]);
        marker_new.radius = 10;
        marker_new.type = 1;
        p.markers.add(marker_new);
        /*
        int bkg_thresh = (int)para.get(0);
        int cnn_type = (int)para.get(1);
        int channel = (int)para.get(2);
        int z_thickness = (int)para.get(3);
        float[][][] phi = new float[(int) sz0][(int) sz1][(int) sz2];
        int[] sz = new int[]{(int) sz0,(int) sz1,(int) sz2};
        FM_GSDT.gsdt_FM(inimg.getDataCZYX()[0],phi, sz, cnn_type, bkg_thresh);//re

        int[] max_loc = new int[]{0,0,0};
        double max_val = phi[0][0][0];
        for (int k=0;k<sz2;k++) {
            for (int j=0;j<sz1;j++)
                for (int i=0;i<sz0;i++)
                {
                    if(phi[k][j][i] > max_val){
                        max_val = phi[k][j][i];
                        max_loc[0] = i;
                        max_loc[1] = j;
                        max_loc[2] = k;
                    }
                }
        }
        out.println("max_loc:" + max_loc[0] + max_loc[1] + max_loc[2]);
        out.println("max_val:" + max_val);

        ImageMarker marker_new = new ImageMarker(max_loc[0], max_loc[1], max_loc[2]);
*/
        return true;
    }
}
