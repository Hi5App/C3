package com.penglab.hi5.basic.tracingfunc.rg;

import com.penglab.hi5.basic.ByteTranslate;
import com.penglab.hi5.basic.image.Image4DSimple;

import java.util.Vector;

public class RegionGrow {

    public static int phcDebugPosNum = 0;
    public static int phcDebugRgnNum = 0;

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
            para.threshold = meanv + stdv;
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
                for(int i=0; i<sx; i++){
                    if(i==0 || i==sx-1 || j==0 || j==sy-1 || k==0 || k==sz-1)
                        continue;
                    int idx = idxJ+i;
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

        RgnGrow3dClass pRgnGrow = new RgnGrow3dClass();

        pRgnGrow.imgDep = sz;
        pRgnGrow.imgHei = sy;
        pRgnGrow.imgWid = sx;

//        int total = sz*sy*sx;

//        pRgnGrow.quantImg1d = new byte[total];
        pRgnGrow.quantImg = new byte[pRgnGrow.imgDep][pRgnGrow.imgHei][pRgnGrow.imgWid];

//        int depk,colj,rowi;
        for(int k=0; k<sz; k++) {
            int idxK = k * offsetZ;
            for (int j = 0; j < sy; j++) {
                int idxJ = idxK + j * offsetY;
                for (int i = 0; i < sx; i++) {
                    int idx = idxJ + i;
                    pRgnGrow.quantImg[k][j][i] = bw[idx];
                }
            }
        }


//        int nState;
//        byte minLevel = (byte) (bw[0] + 0.5);
//        byte maxLevel = minLevel;

//        int tmp;
//        double tmp1;
//        for(int i=0; i<total; i++){
//            tmp1 = ByteTranslate.byte1ToInt(bw[i]);
//
//            tmp = (int) (tmp1+0.5);
//            maxLevel = (minLevel<tmp)?minLevel: (byte) tmp;
//            maxLevel = (maxLevel>tmp)?maxLevel: (byte) tmp;
//
//            pRgnGrow.quantImg1d[i] = (byte) tmp;
//        }
//        nState = maxLevel-minLevel+1;
//
//        minLevel = (byte) (minLevel+1);
//        if(minLevel>maxLevel){
//            minLevel = maxLevel;
//        }



        phcDebugPosNum = 0;
        phcDebugRgnNum = 0;

        pRgnGrow.phcLabelStack = new int[1][3][pageSZ];
//        pRgnGrow.PHCLABELSTACK1d = new int[1*3*total];

        pRgnGrow.phcLabelStackPos = 0;

        pRgnGrow.phcRgn = new RGN();

        pRgnGrow.phcRgnHead = pRgnGrow.phcRgn;
        pRgnGrow.totalRgnNum = 1;

//        pRgnGrow.PHCDONEIMG3d = null;
//        pRgnGrow.PHCDONEIMG1d = null;
        pRgnGrow.phcDoneImg = null;

//        pRgnGrow.PHCDONEIMG1d = new byte[total];
        pRgnGrow.phcDoneImg= new byte[pRgnGrow.imgDep][pRgnGrow.imgHei][pRgnGrow.imgWid];

//        for(int j=minLevel; j<=maxLevel; j++){


//            byte[] PHCDONEIMG1d = pRgnGrow.PHCDONEIMG1d;
//            byte[] quantImg1d = pRgnGrow.quantImg1d;
        int depk,colj,rowi;
        byte[][][] flagImg = pRgnGrow.phcDoneImg;
        byte[][][] quantImg = pRgnGrow.quantImg;

        for(depk=0; depk<pRgnGrow.imgDep; depk++) {
            for (colj = 0; colj < pRgnGrow.imgHei; colj++) {
                for (rowi = 0; rowi < pRgnGrow.imgWid; rowi++) {
                    flagImg[depk][colj][rowi] = (byte) ((quantImg[depk][colj][rowi] == 1)?1:0);
                }
            }
        }

        pRgnGrow.phcCurLabel = 0;

        for(depk=0; depk<pRgnGrow.imgDep; depk++){
            for(colj=0; colj<pRgnGrow.imgHei; colj++){
                for(rowi=0; rowi<pRgnGrow.imgWid; rowi++){
                    if(flagImg[depk][colj][rowi] == 1){
                        pRgnGrow.ifIncreaseLabel = 1;

                        pRgnGrow.phcCurLabel++;

                        pRgnGrow.phcLabelStackPos = 0;

                        pRgnGrow.phcLabelStack[0][0][pRgnGrow.phcLabelStackPos] = depk;
                        pRgnGrow.phcLabelStack[0][1][pRgnGrow.phcLabelStackPos] = colj;
                        pRgnGrow.phcLabelStack[0][2][pRgnGrow.phcLabelStackPos] = rowi;

                        //create pos memory
                        pRgnGrow.phcRgnPos = new POS();
                        pRgnGrow.phcRgnPosHead = pRgnGrow.phcRgnPos;
                        pRgnGrow.totalPosNum = 1;

                        while (true){
                            pRgnGrow.ifIncreaseLabel = 1;
                            int posbeg = pRgnGrow.phcLabelStackPos;
                            int mypos = posbeg;

                            while (mypos>=0){
                                pRgnGrow.stackCount = 0;
                                int curdep = pRgnGrow.phcLabelStack[0][0][mypos];
                                int curcol = pRgnGrow.phcLabelStack[0][1][mypos];
                                int currow = pRgnGrow.phcLabelStack[0][2][mypos];

                                if (flagImg[curdep][curcol][currow]==1)
                                {
                                    pRgnGrow.rgnfindsub(currow,curcol,curdep,0,1);
                                }
                                else if(flagImg[curdep][curcol][currow]==-1)
                                {
                                    pRgnGrow.rgnfindsub(currow,curcol,curdep,0,0);
                                }

                                int posend = pRgnGrow.phcLabelStackPos;

                                if (posend>posbeg) {
                                    mypos = pRgnGrow.phcLabelStackPos;
                                }
                                else {
                                    mypos = mypos-1;
                                }
                                posbeg = posend;
                            }
                            if(pRgnGrow.ifIncreaseLabel == 1)
                                break;
                        }

                        //set pos as member of current RGN
                        pRgnGrow.phcRgn.layer = 1;
                        pRgnGrow.phcRgn.no = pRgnGrow.phcCurLabel;
                        pRgnGrow.phcRgn.poslist = pRgnGrow.phcRgnPosHead;
                        pRgnGrow.phcRgn.poslistlen = pRgnGrow.totalPosNum;

                        pRgnGrow.totalPosNum = 0;

                        pRgnGrow.phcRgn.next = new RGN();

                        pRgnGrow.phcRgn = pRgnGrow.phcRgn.next;
                        pRgnGrow.totalRgnNum++;

                    }
                }
            }
        }
//        }

        STCL staRegion = new STCL();
        STCL staRegionBegin = staRegion;
        RGN curRgn = pRgnGrow.phcRgnHead;
        int nrgncopied = 0;

        Vector<STCL> stclList = new Vector<>();

        while (curRgn != null && curRgn.next != null){
            staRegion.no = curRgn.no;
            staRegion.count = 0;

            POS curPos = curRgn.poslist;

            int count = 0;
            staRegion.desPosList = new int[curRgn.poslistlen-1];
            while(curPos != null && curPos.next != null)
            {
                staRegion.desPosList[count++] = curPos.pos;
                curPos = curPos.next;
//                count++;
            }
            staRegion.count = count;

//            qDebug() << "pixels ..." << count;

            if(para.bFilterSmallobjs && count<para.volSz) { nrgncopied++; curRgn = curRgn.next; continue; } // filter out the small components

            //
            stclList.add(staRegion);

            //
            curRgn = curRgn.next;
            staRegion.next = new STCL();
            staRegion = staRegion.next;

            nrgncopied++;

        }

        int length;

        int n_rgn = stclList.size(); // qMin(5, nrgncopied);

        System.out.println("display "+n_rgn+" rgns from "+nrgncopied);

        if(n_rgn>65534)
        {
            System.out.println("do not support the case of n_rgn>65534 temporarily");
            /*
            float[] pRGCL = new float[pageSZ];
            for(int i=0; i<pageSZ; i++)
                pRGCL[i] = 0;

            Vector<LocationSimple> cmList = new Vector<>();

            for(int ii=0; ii<n_rgn; ii++)
            {
                length = stclList.get(ii).count; //a[ii];

                System.out.println("region ..." + ii + length);
                // find idx
                int[] cutposlist = stclList.get(ii).desPosList;

                float scx=0,scy=0,scz=0,si=0;

                for(int i=0; i<length; i++)
                {
                    //qDebug() << "idx ..." << i << cutposlist[i] << pagesz;

                    pRGCL[ cutposlist[i] ] = (float)ii + 1.0f;

                    float cv = ByteTranslate.byte1ToInt(data1d[ cutposlist[i] ]);

                    int idx = cutposlist[i];

                    int k1 = idx/(sx*sy);
                    int j1 = (idx - k1*sx*sy)/sx;
                    int i1 = idx - k1*sx*sy - j1*sx;

                    scz += k1*cv;
                    scy += j1*cv;
                    scx += i1*cv;
                    si += cv;
                }

                if (si>0)
                {
                    int ncx = (int) (scx/si + 0.5 +1);
                    int ncy = (int) (scy/si + 0.5 +1);
                    int ncz = (int) (scz/si + 0.5 +1);

                    LocationSimple pp =  new LocationSimple(ncx, ncy, ncz);
                    cmList.add(pp);

                }

            }

            // display
            //p4DImage.setData((unsigned char*)pRGCL, sx, sy, sz, 1, V3D_FLOAT32);

            // save result image
//            int out_sz[4];
//            out_sz[0]=sx; out_sz[1]=sy; out_sz[2]=sz; out_sz[3]=1;
//            simple_saveimage_wrapper(callback, outimg_file, (unsigned char *)pRGCL, out_sz, 4);
*/
        }
        else if(n_rgn>254)
        {
            System.out.println("do not support the case of n_rgn>254 temporarily");
            /*
            unsigned short *pRGCL = NULL;
            try
            {
                pRGCL = new unsigned short [pagesz];

                memset(pRGCL, 0, sizeof(unsigned short)*pagesz);
            }
            catch (...)
            {
                printf("Fail to allocate memory.\n");
                return false;
            }

            LandmarkList cmList;

            for(int ii=0; ii<n_rgn; ii++)
            {
                length = stclList.at(ii).count; //a[ii];

                qDebug() << "region ..." << ii << length;

                // find idx
                int *cutposlist = stclList.at(ii).desposlist;

                float scx=0,scy=0,scz=0,si=0;

                for(int i=0; i<length; i++)
                {
                    //qDebug() << "idx ..." << i << cutposlist[i] << pagesz;

                    pRGCL[ cutposlist[i] ] = (unsigned short)ii + 1;

                    float cv = pSub[ cutposlist[i] ];

                    int idx = cutposlist[i];

                    int k1 = idx/(sx*sy);
                    int j1 = (idx - k1*sx*sy)/sx;
                    int i1 = idx - k1*sx*sy - j1*sx;

                    scz += k1*cv;
                    scy += j1*cv;
                    scx += i1*cv;
                    si += cv;
                }

                if (si>0)
                {
                    int ncx = scx/si + 0.5 +1;
                    int ncy = scy/si + 0.5 +1;
                    int ncz = scz/si + 0.5 +1;

                    LocationSimple pp(ncx, ncy, ncz);
                    cmList.push_back(pp);

                }

            }

            // display
            //p4DImage.setData((unsigned char*)pRGCL, sx, sy, sz, 1, V3D_UINT16);

            // save result image
            int out_sz[4];
            out_sz[0]=sx; out_sz[1]=sy; out_sz[2]=sz; out_sz[3]=1;
            simple_saveimage_wrapper(callback, outimg_file, (unsigned char *)pRGCL, out_sz, 2);
*/
        }
        else
        {
            byte[] pRGCL = new byte[pageSZ];
            for(int i=0; i<pageSZ; i++){
                pRGCL[i] = 0;
            }
//            LandmarkList cmList;

            for(int ii=0; ii<n_rgn; ii++)
            {
                length = stclList.get(ii).count; //a[ii];
                System.out.println("region ..."+ii+length);

                // find idx
                int[] cutposlist = stclList.get(ii).desPosList;

                float scx=0,scy=0,scz=0,si=0;

                for(int i=0; i<length; i++)
                {
                    pRGCL[ cutposlist[i] ] = (byte) (ii + 1);
                }
            }
            result.setDataFromImage(pRGCL,sx,sy,sz,1, Image4DSimple.ImagePixelType.V3D_UINT8,true);

            // display
            //p4DImage.setData((unsigned char*)pRGCL, sx, sy, sz, 1, V3D_UINT8);

            // save result image
//            int out_sz[4];
//            out_sz[0]=sx; out_sz[1]=sy; out_sz[2]=sz; out_sz[3]=1;
//            simple_saveimage_wrapper(callback, outimg_file, (unsigned char *)pRGCL, out_sz, 1);
        }

        return result;
    }

}
