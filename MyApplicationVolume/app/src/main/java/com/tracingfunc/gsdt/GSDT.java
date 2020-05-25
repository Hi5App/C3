package com.tracingfunc.gsdt;

import com.example.basic.Image4DSimple;
import com.example.basic.ImageMarker;

import static java.lang.System.out;

public class GSDT {
    public static boolean GSDT_Fun(ParaGSDT p) throws Exception {
        //Image4DSimple inimg = loadImage(img_filepath,img_filetype);
        if(!p.p4DImage.valid()){
            out.println("image ia invalid!");
            return false;
        }
        Image4DSimple inimg = p.p4DImage;
        p.outImage = inimg;
        long sz0 = inimg.getSz0();
        long sz1 = inimg.getSz1();
        long sz2 = inimg.getSz2();
        long sz3 = inimg.getSz3();
       // out.println(sz0+" "+sz1+" "+sz2+" "+sz3);
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

        //find the local maximum
        p.max_val = p.phi[0][0][0];
        for(int ix=10;ix<=(sz[0]-11);ix=ix+21){
            for(int iy=10;iy<=(sz[1]-11);iy=iy+21){
                for(int iz=10;iz<=(sz[2]-11);iz=iz+21){
                    p.local_maxval = p.phi[iz][iy][ix];
                    p.local_maxloc[0] = ix;
                    p.local_maxloc[1] = iy;
                    p.local_maxloc[2] = iz;
                    for(int dx=-10;dx<=10;dx++){
                        for(int dy=-10;dy<=10;dy++){
                            for(int dz=-10;dz<=10;dz++){
                                if(p.phi[iz+dz][iy+dy][ix+dx]>p.local_maxval){
                                    p.local_maxval = p.phi[iz+dz][iy+dy][ix+dx];
                                    p.local_maxloc[0] = ix + dx;
                                    p.local_maxloc[1] = iy + dy;
                                    p.local_maxloc[2] = iz + dz;
                                }
                            }
                        }
                    }
                    if(p.local_maxval>p.max_val){
                        p.max_val = p.local_maxval;
                        p.max_loc[0] = p.local_maxloc[0];
                        p.max_loc[1] = p.local_maxloc[1];
                        p.max_loc[2] = p.local_maxloc[2];
                    }
                    if(p.local_maxval>=100){
                        ImageMarker marker_new = new ImageMarker(p.local_maxloc[0], p.local_maxloc[1], p.local_maxloc[2]);
                        marker_new.radius = 5;
                        marker_new.type = 3;
                        p.markers.add(marker_new);
                    }
                }
            }
        }

        p.MaxMarker = new ImageMarker(p.max_loc[0], p.max_loc[1], p.max_loc[2]);
        p.MaxMarker.radius = 10;
        p.MaxMarker.type = 2;

        return true;
    }
}
