package com.penglab.hi5.basic.tracingfunc.app2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.image.Image4DSimple.ImagePixelType;
import com.penglab.hi5.basic.NeuronTree;

import java.util.Timer;
import java.util.Vector;

import static com.penglab.hi5.basic.image.Image4DSimple.ImagePixelType.V3D_UINT8;
import static com.penglab.hi5.basic.tracingfunc.app2.HierarchyPruning.happ;
import static com.penglab.hi5.basic.tracingfunc.app2.HierarchyPruning.hierarchy_prune;
import static java.lang.System.*;

public class V3dNeuronAPP2Tracing {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean proc_app2(ParaAPP2 p) throws Exception{
        if(!p.p4dImage.valid()){
            out.println("image ia invalid!");
            return false;
        }
        if(p.landmarks.length<2 && p.b_intensity == 1){
            out.println("You have to select at least two markers if using high intensity background option.");
            return false;
        }


        Vector<String> infostring = new Vector<String>();//一些输出信息

        Timer timer1 = new Timer();

        Image4DSimple p4dImageNew = new Image4DSimple();

        out.println("---------------------1111111-----------------------");

        if(p.xc1>=p.xc0 && p.yc1>=p.yc0 && p.zc1>=p.zc0 &&
                p.xc0>=0 && p.xc1<p.p4dImage.getSz0() &&
                p.yc0>=0 && p.yc1<p.p4dImage.getSz1() &&
                p.zc0>=0 && p.zc1<p.p4dImage.getSz2()){
            out.println("---------------------aaaaaaaaaaaaaa-----------------------");
            if(!p4dImageNew.createImage(p.xc1-p.xc0+1, p.yc1-p.yc0+1, p.zc1-p.zc0+1, 1, p.p4dImage.getDatatype())){
                return false;
            }
            out.println("---------------------bbbbbbbbbbb-----------------------");
            if(p.b_brightfiled) {
                out.println("bright");
//                p.p4dImage.getDataCZYX();
                if (!Image4DSimple.invertedsubvolumecopy(p4dImageNew,
                        p.p4dImage,
                        p.xc0, p.xc1 - p.xc0 + 1,
                        p.yc0, p.yc1 - p.yc0 + 1,
                        p.zc0, p.zc1 - p.zc0 + 1,
                        p.channel, 1)) {
                    return false;
                }
            }
            else{
                out.println("no bright");
//                p.p4dImage.getDataCZYX();
                if(!Image4DSimple.subvolumecopy(p4dImageNew,
                        p.p4dImage,
                        p.xc0, p.xc1-p.xc0+1,
                        p.yc0, p.yc1-p.yc0+1,
                        p.zc0, p.zc1-p.zc0+1,
                        p.channel, 1)) {
                    return false;
                }
            }
        }
        else {
            out.println("Somehow invalid volume box info is detected. Ignore it. But check your Vaa3D program.");
            return false;
        }

        out.println("---------------------2222222222-----------------------");

        int marker_thresh = Integer.MAX_VALUE;
        if(p.b_intensity>0){
            if(p.b_brightfiled) p.bkg_thresh = 255 - p.bkg_thresh;

            for(int d = 1; d < p.landmarks.length; d++)
            {
                int marker_x = (int) p.landmarks[d].x - p.xc0;
                int marker_y = (int) p.landmarks[d].y - p.yc0;
                int marker_z = (int) p.landmarks[d].z - p.zc0;

                if(p4dImageNew.getValue(marker_x,marker_y,marker_z,0) < marker_thresh)
                {
                    marker_thresh = p4dImageNew.getValue(marker_x,marker_y,marker_z,0);
                }
            }

            p.bkg_thresh = (marker_thresh - 10 > p.bkg_thresh) ? marker_thresh - 10 : p.bkg_thresh;
        }

        double dfactor_xy = 1, dfactor_z = 1;
        ImagePixelType datatype = p.p4dImage.getDatatype();
        long[] in_sz = {p4dImageNew.getSz0(), p4dImageNew.getSz1(), p4dImageNew.getSz2(), 1};
        if(datatype != V3D_UINT8 || in_sz[0]>128 || in_sz[1]>128 || in_sz[2]>128)// && datatype != V3D_UINT16)
        {
            if (datatype!=V3D_UINT8)
            {
                if (!Image4DSimple.scale_img_and_converto8bit(p4dImageNew, 0, 255))
                    return false;

//                indata1d = p4dImageNew->getRawDataAtChannel(0);
                in_sz[0] = p4dImageNew.getSz0();
                in_sz[1] = p4dImageNew.getSz1();
                in_sz[2] = p4dImageNew.getSz2();
                in_sz[3] = p4dImageNew.getSz3();

                datatype = V3D_UINT8;
            }

            out.printf("x = %d  ", in_sz[0]);
            out.printf("y = %d  ", in_sz[1]);
            out.printf("z = %d  ", in_sz[2]);
            out.printf("c = %d\n", in_sz[3]);

            if (p.b_128cube==1)
            {
                if (in_sz[0]<=128 && in_sz[1]<=128 && in_sz[2]<=128)
                {
                    dfactor_z = dfactor_xy = 1;
                }
                else if (in_sz[0] >= 2*in_sz[2] || in_sz[1] >= 2*in_sz[2])
                {
                    if (in_sz[2]<=128)
                    {
                        double MM = in_sz[0];
                        if (MM<in_sz[1]) MM=in_sz[1];
                        dfactor_xy = MM / 128.0;
                        dfactor_z = 1;
                    }
                    else
                    {
                        double MM = in_sz[0];
                        if (MM<in_sz[1]) MM=in_sz[1];
                        if (MM<in_sz[2]) MM=in_sz[2];
                        dfactor_xy = dfactor_z = MM / 128.0;
                    }
                }
                else
                {
                    double MM = in_sz[0];
                    if (MM<in_sz[1]) MM=in_sz[1];
                    if (MM<in_sz[2]) MM=in_sz[2];
                    dfactor_xy = dfactor_z = MM / 128.0;
                }

                out.printf("dfactor_xy=%5.3f\n", dfactor_xy);
                out.printf("dfactor_z=%5.3f\n", dfactor_z);

                if (dfactor_z>1 || dfactor_xy>1)
                {
                    out.println("enter ds code");
                    Image4DSimple p4dImageTmp = new Image4DSimple();
                    if (!Image4DSimple.downsampling_img_xyz(p4dImageTmp,p4dImageNew,dfactor_xy,dfactor_z))
                        return false; //need to clean memory before return. a bug here

                    p4dImageNew.setData(p4dImageTmp);

//                    indata1d = p4dImageNew->getRawDataAtChannel(0);
                    in_sz[0] = p4dImageNew.getSz0();
                    in_sz[1] = p4dImageNew.getSz1();
                    in_sz[2] = p4dImageNew.getSz2();
                    in_sz[3] = p4dImageNew.getSz3();
                }
            }
        }

        out.println("---------------------333333333333333-----------------------");

        //QString outtmpfile = QString(p.p4dImage->getFileName()) + "_extract_tmp000.raw";
        //p4dImageNew->saveImage(qPrintable(outtmpfile));  v3d_msg(QString("save immediate input image to ") + outtmpfile, 0);


        if (p.bkg_thresh < 0)
        {
            if (p.channel >=0 && p.channel <= p.p4dImage.getSz3()-1)
            {
                double imgAve, imgStd;
                double[] meanStd = p4dImageNew.getMeanStdValue(0);
                imgAve = meanStd[0];
                imgStd = meanStd[1];
//            p.bkg_thresh = imgAve; //+0.5*imgStd ; //(imgAve < imgStd)? imgAve : (imgAve+imgStd)*.5;
                double td= (imgStd<10)? 10: imgStd;
                p.bkg_thresh = (int) (imgAve +0.5*td) ; //(imgAve < imgStd)? imgAve : (imgAve+imgStd)*.5; //20170523, PHC
            }
            else {
                p.bkg_thresh = 0;
            }

//            tmpstr =  qPrintable( qtstr.setNum(p.bkg_thresh).prepend("#autoset #bkg_thresh = ") ); infostring.push_back(tmpstr);
        }
        else if (p.b_brightfiled)
        {
            p.bkg_thresh = 255 - p.bkg_thresh;
        }



        float[][][] phi = new float[(int) in_sz[2]][(int) in_sz[1]][(int) in_sz[0]];
        Vector<MyMarker> inmarkers = new Vector<MyMarker>();
        for(int i = 0; i < p.landmarks.length; i++)
        {
            double x = p.landmarks[i].x - p.xc0;
            double y = p.landmarks[i].y - p.yc0;
            double z = p.landmarks[i].z - p.zc0;

            //add scaling by PHC 121127
            x /= dfactor_xy;
            y /= dfactor_xy;
            z /= dfactor_z;
            MyMarker m = new MyMarker(x,y,z);
            inmarkers.add(m);
        }
//        qint64 etime1 = timer1.elapsed();
//        qDebug() << " **** neuron preprocessing takes [" << etime1 << " milliseconds]";


//        for (it=infostring.begin();it!=infostring.end();it++)
//            cout << *it <<endl;

        out.println("start neuron tracing for the preprocessed image.");

        Vector<MyMarker> outTree = new Vector<MyMarker>();

        //add a timer by PHC 121005
//        QElapsedTimer timer2;
//        timer2.start();

        boolean b_detect_cell = false;

        out.println("bkg_thres: "+p.bkg_thresh);

        if(inmarkers.isEmpty())
        {
            out.println("Start detecting cellbody");
            int[] sz = new int[]{(int) in_sz[0],(int) in_sz[1],(int) in_sz[2]};
            FM.fastmarching_dt(p4dImageNew.getDataCZYX()[0],phi,sz,p.cnn_type,p.bkg_thresh);
            out.println("-----------------phi end---------------");
            if(phi==null){
                out.println("phi is null");
            }


            long sz0 = in_sz[0];
            long sz1 = in_sz[1];
            long sz2 = in_sz[2];
            long sz01 = sz0 * sz1;
            long tol_sz = sz01 * sz2;

            int[] max_loc = new int[]{0,0,0};
            double max_val = phi[0][0][0];
            for (int k=0;k<sz2;k++) {
                for (int j=0;j<sz1;j++)
                    for (int i=0;i<sz0;i++)
                    {
                        if(phi[k][j][i]>100){
                            out.println(i +" "+ j + " "+ k+ " : "+phi[k][j][i]);
                        }
                        if(phi[k][j][i] > max_val){
                            max_val = phi[k][j][i];
                            max_loc[0] = i;
                            max_loc[1] = j;
                            max_loc[2] = k;
                        }
//                        if(phi[k][j][i] < 103 && phi[k][j][i] > 102){
//                            max_loc[0] = i;
//                            max_loc[1] = j;
//                            max_loc[2] = k;
//                        }
                    }
            }
            MyMarker max_marker = new MyMarker(max_loc[0],max_loc[1],max_loc[2]);
            inmarkers.add(max_marker);
            b_detect_cell = true;
        }

        out.println("=======================================");
        out.println("Construct the neuron tree");

        int[] sz = new int[]{(int) in_sz[0],(int) in_sz[1],(int) in_sz[2]};
        try {
            if(inmarkers.isEmpty())
            {
                out.println("need at least one markers");
            }
            else if(inmarkers.size() == 1)
            {
                out.println("only one input marker");
                if(p.is_gsdt)
                {
                    if(!b_detect_cell)
                    {
                        out.println("processing fastmarching distance transformation ...");
                        FM.fastmarching_dt(p4dImageNew.getDataCZYX()[0],phi,sz,p.cnn_type,p.bkg_thresh);
                    }

                    out.println("constructing fastmarching tree ...");
                    FM.fastmarching_tree(inmarkers.elementAt(0), phi, outTree, sz, p.cnn_type, p.bkg_thresh, p.is_break_accept);
                }
                else
                {
                    FM.fastmarching_tree(inmarkers.elementAt(0), p4dImageNew.getDataCZYX()[0], outTree, sz, p.cnn_type, p.bkg_thresh, p.is_break_accept);
                }
            }
            else
            {
                Vector<MyMarker> target = new Vector<MyMarker>();
                for(int item=1; item<inmarkers.size(); item++){
                    target.add(inmarkers.elementAt(item));
                }
                if(p.is_gsdt)
                {
                    if(!b_detect_cell)
                    {
                        out.println("processing fastmarching distance transformation ...");
                        FM.fastmarching_dt(p4dImageNew.getDataCZYX()[0],phi,sz,p.cnn_type,p.bkg_thresh);
                    }
                    out.println("constructing fastmarching tree ...");
                    FM.fastmarching_tree(inmarkers.elementAt(0), target, phi, outTree, sz, p.cnn_type,p.bkg_thresh,p.is_break_accept);
                }
                else
                {
                    FM.fastmarching_tree(inmarkers.elementAt(0), target, p4dImageNew.getDataCZYX()[0], outTree, sz, p.cnn_type,p.bkg_thresh,p.is_break_accept);
                }
            }
        }catch (OutOfMemoryError e){
            throw new Exception(e.getMessage());
        }

        out.println("=======================================");

        //save a copy of the ini tree
        out.println("Save the initial unprunned tree");

        {
            int tmpi;

            Vector<MyMarker> tmpswc = new Vector<MyMarker>();
            for (tmpi=0; tmpi< outTree.size(); tmpi++)
            {
                MyMarker  curp = new MyMarker(outTree.elementAt(tmpi));
                tmpswc.add(curp);

                if (dfactor_xy>1) outTree.elementAt(tmpi).x *= dfactor_xy;
                outTree.elementAt(tmpi).x += (p.xc0);
                if (dfactor_xy>1) outTree.elementAt(tmpi).x += dfactor_xy/2;

                if (dfactor_xy>1) outTree.elementAt(tmpi).y *= dfactor_xy;
                outTree.elementAt(tmpi).y += (p.yc0);
                if (dfactor_xy>1) outTree.elementAt(tmpi).y += dfactor_xy/2;

                if (dfactor_z>1) outTree.elementAt(tmpi).z *= dfactor_z;
                outTree.elementAt(tmpi).z += (p.zc0);
                if (dfactor_z>1)  outTree.elementAt(tmpi).z += dfactor_z/2;
            }

//            MyMarker.saveSWC_file(p.outswc_file.split("\\.")[0]+"_ini.swc", outTree, infostring);

            for (tmpi=0; tmpi< outTree.size(); tmpi++)
            {
                outTree.elementAt(tmpi).x = tmpswc.elementAt(tmpi).x;
                outTree.elementAt(tmpi).y = tmpswc.elementAt(tmpi).y;
                outTree.elementAt(tmpi).z = tmpswc.elementAt(tmpi).z;
            }
        }


        out.println("Pruning neuron tree");

        Vector<MyMarker> outswc = new Vector<MyMarker>();
        if(p.is_coverage_prune)
        {
            out.println("start to use APP2 program.");
            happ(outTree, outswc, p4dImageNew.getDataCZYX()[0], sz, p.bkg_thresh, p.length_thresh, p.SR_ratio, true,true);
        }
        else
        {
            hierarchy_prune(outTree, outswc, p4dImageNew.getDataCZYX()[0], sz, p.length_thresh);
            //get radius
            {
                double real_thres = 40; //PHC 20121011
                if (real_thres<p.bkg_thresh) real_thres = p.bkg_thresh;
                for(int i = 0; i < outswc.size(); i++)
                {
                    outswc.elementAt(i).radius = outswc.elementAt(i).markerRadius(p4dImageNew.getDataCZYX()[0], sz, real_thres,1,false);
                }
            }
        }

        out.println("ggggggggggggggggggggggggggggggggggggggggggggg");
        for(int i=0; i<outswc.size(); i++){
            if(outswc.get(i).x>500){
                out.println("index: "+i+" "+outswc.get(i).x);
            }
        }

//        qint64 etime2 = timer2.elapsed();
//        qDebug() << " **** neuron tracing procedure takes [" << etime2 << " milliseconds]";

//        if (p4dImageNew) {delete p4dImageNew; p4dImageNew=0;} //free buffer

        if(p.b_128cube==1)
        {
            inmarkers.elementAt(0).x *= dfactor_xy;
            inmarkers.elementAt(0).y *= dfactor_xy;
            inmarkers.elementAt(0).z *= dfactor_z;

        }


        {
            String rootposstr=""+ (int)(inmarkers.elementAt(0).x+0.5) + "_x" +
                    (int)(inmarkers.elementAt(0).y+0.5) + "_y" + (int)(inmarkers.elementAt(0).z+0.5) + "_z";
            //QString outswc_file = callback.getImageName(curwin) + rootposstr + "_app2.swc";
            String outswc_file;
            if(!p.outswc_file.isEmpty())
                outswc_file = p.outswc_file;
            else
                outswc_file = p.p4dImage.getImgSrcFile() + rootposstr + "_app2.swc";


            for(int i = 0; i < outswc.size(); i++) //add scaling 121127, PHC //add cutbox offset 121202, PHC
            {
                if(dfactor_xy>1){
                    outswc.get(i).x *= dfactor_xy;
                    outswc.get(i).x += dfactor_xy/2;
                    outswc.get(i).y *= dfactor_xy;
                    outswc.get(i).y += dfactor_xy/2;
                }
                if(dfactor_z>1){
                    outswc.get(i).z *= dfactor_z;
                    outswc.get(i).z += dfactor_z;
                }
                outswc.get(i).x += p.xc0;
                outswc.get(i).y += p.yc0;
                outswc.get(i).z += p.zc0;

                outswc.get(i).radius *= dfactor_xy; //use xy for now
            }
            out.println("dfactorxy: "+dfactor_xy+" dfactorz: "+dfactor_z);

            //re-estimate the radius using the original image
            double real_thres = 40; //PHC 20121011 //This should be rescaled later for datatypes that are not UINT8

            if (real_thres<p.bkg_thresh) real_thres = p.bkg_thresh;
            int[] szOriginalData = new int[]{(int) p.p4dImage.getSz0(), (int) p.p4dImage.getSz1(), (int) p.p4dImage.getSz2(), 1};
            int[][][] pOriginalData = p.p4dImage.getDataCZYX()[0];
            if(p.b_brightfiled)
            {
                for(int k=0; k<p.p4dImage.getSz2(); k++)
                    for(int j=0; j<p.p4dImage.getSz1(); j++)
                        for(int i=0; i<p.p4dImage.getSz0(); i++)
                            pOriginalData[k][j][i] = 255 - pOriginalData[k][j][i];
            }

            boolean method_radius_est = ( p.b_RadiusFrom2D ) ? true : false;

//            switch (p.p4dImage->getDatatype())
//            {
//                case V3D_UINT8:
//                {
//                    for(i = 0; i < outswc.size(); i++)
//                    {
//                        //printf(" node %ld of %ld.\n", i, outswc.size());
//                        outswc.elementAt(i).radius = markerRadius(pOriginalData, szOriginalData, *(outswc[i]), real_thres, method_radius_est);
//                    }
//                }
//                break;
//                case V3D_UINT16:
//                {
//                    unsigned short int *pOriginalData_uint16 = (unsigned short int *)pOriginalData;
//                    for(i = 0; i < outswc.size(); i++)
//                    {
//                        //printf(" node %ld of %ld.\n", i, outswc.size());
//                        outswc.elementAt(i).radius = markerRadius(pOriginalData_uint16, szOriginalData, *(outswc[i]), real_thres * 16, method_radius_est); //*16 as it is often 12 bit data
//                    }
//                }
//                break;
//                case V3D_FLOAT32:
//                {
//                    float *pOriginalData_float = (float *)pOriginalData;
//                    for(i = 0; i < outswc.size(); i++)
//                    {
//                        //printf(" node %ld of %ld.\n", i, outswc.size());
//                        outswc.elementAt(i).radius = markerRadius(pOriginalData_float, szOriginalData, *(outswc[i]), real_thres, method_radius_est);
//                    }
//                }
//                break;
//                default:
//                    break;
//            }
            for(int i=0; i<outswc.size(); i++){
                outswc.elementAt(i).radius = outswc.elementAt(i).markerRadius(pOriginalData,szOriginalData,real_thres,1,method_radius_est);
            }

            if(p.b_brightfiled)
            {
                for(int k=0; k<p.p4dImage.getSz2(); k++)
                    for(int j=0; j<p.p4dImage.getSz1(); j++)
                        for(int i=0; i<p.p4dImage.getSz0(); i++)
                            pOriginalData[k][j][i] = 255 - pOriginalData[k][j][i];

            }
            //prepare the output comments for neuron info in the swc file

//            tmpstr =  qPrintable( qtstr.setNum(etime1).prepend("#neuron preprocessing time (milliseconds) = ") ); infostring.push_back(tmpstr);
//            tmpstr =  qPrintable( qtstr.setNum(etime2).prepend("#neuron tracing time (milliseconds) = ") ); infostring.push_back(tmpstr);

//            MyMarker.saveSWC_file(outswc_file, outswc, infostring);

            p.resultNt = MyMarker.swcConvert(outswc);

//            if(outswc.size()>1)
//            {
//
//                //call sort_swc function
//
//                V3DPluginArgItem arg;
//                V3DPluginArgList input_resample;
//                V3DPluginArgList input_sort;
//                V3DPluginArgList output;
//
//                arg.type = "random";std::vector<char*> arg_input_resample;
//                std:: string fileName_Qstring(outswc_file.toStdString());char* fileName_string =  new char[fileName_Qstring.length() + 1]; strcpy(fileName_string, fileName_Qstring.c_str());
//                arg_input_resample.push_back(fileName_string);
//                arg.p = (void *) & arg_input_resample; input_resample<< arg;
//                arg.type = "random";std::vector<char*> arg_resample_para; arg_resample_para.push_back("10");arg.p = (void *) & arg_resample_para; input_resample << arg;
//                arg.type = "random";std::vector<char*> arg_output;arg_output.push_back(fileName_string); arg.p = (void *) & arg_output; output<< arg;
//                QString full_plugin_name_resample = "resample_swc";
//                QString func_name_resample = "resample_swc";
//                if(p.b_resample)
//                    callback.callPluginFunc(full_plugin_name_resample,func_name_resample,input_resample,output);
//                arg.type = "random";std::vector<char*> arg_input_sort;
//                arg_input_sort.push_back(fileName_string);
//                arg.p = (void *) & arg_input_sort; input_sort<< arg;
//                arg.type = "random";std::vector<char*> arg_sort_para; arg_sort_para.push_back("0");arg.p = (void *) & arg_sort_para; input_sort << arg;
//                QString full_plugin_name_sort = "sort_neuron_swc";
//                QString func_name_sort = "sort_swc";
//                callback.callPluginFunc(full_plugin_name_sort,func_name_sort, input_sort,output);
//
//                vector<MyMarker*> temp_out_swc = readSWC_file(outswc_file.toStdString());
//                saveSWC_file_app2(outswc_file.toStdString(), temp_out_swc, infostring);
//            }


        }



        return true;
    }

    public static boolean app2DetectLine(ParaAPP2 p, NeuronTree sample) throws Exception{
        if(!p.p4dImage.valid()){
            out.println("image ia invalid!");
            return false;
        }
        if(p.landmarks.length<2 && p.b_intensity == 1){
            out.println("You have to select at least two markers if using high intensity background option.");
            return false;
        }

        double[] sampleMeanStd = MyMarker.getMeanStdFromNeuronTree(sample,p.p4dImage,1);
        double sampleThreshold = sampleMeanStd[0] - sampleMeanStd[1]*0.5;
        int sampleSize = sample.listNeuron.size();
        boolean[] sampleDeleteFlag = new boolean[sampleSize];
        for(int i=0; i<sampleSize; i++){
            sampleDeleteFlag[i] = false;
        }

        for(int i=0; i<sampleSize; i++){
            long sx = (long) (sample.listNeuron.get(i).x + 0.5);
            long sy = (long) (sample.listNeuron.get(i).y + 0.5);
            long sz = (long) (sample.listNeuron.get(i).z + 0.5);
            if(p.p4dImage.getValue(sx,sy,sz,0)<sampleThreshold){
                sampleDeleteFlag[i] = true;
            }
            else {
                break;
            }
        }
        for(int i=sampleSize-1; i>=0; i--){
            long sx = (long) (sample.listNeuron.get(i).x + 0.5);
            long sy = (long) (sample.listNeuron.get(i).y + 0.5);
            long sz = (long) (sample.listNeuron.get(i).z + 0.5);
            if(p.p4dImage.getValue(sx,sy,sz,0)<sampleThreshold){
                sampleDeleteFlag[i] = true;
            }
            else {
                break;
            }
        }
        NeuronTree sample2 = new NeuronTree();
        for(int i=0; i<sampleSize; i++){
            if(!sampleDeleteFlag[i]){
                sample2.listNeuron.add(sample.listNeuron.get(i));
            }
        }
        double[] sampleMeanStd2 = MyMarker.getMeanStdFromNeuronTree(sample2,p.p4dImage,1);
        double sampleThreshold2 = sampleMeanStd2[0] - sampleMeanStd2[1]*0.5;


        Image4DSimple p4dImageNew = new Image4DSimple();

        if(p.xc1>=p.xc0 && p.yc1>=p.yc0 && p.zc1>=p.zc0 &&
                p.xc0>=0 && p.xc1<p.p4dImage.getSz0() &&
                p.yc0>=0 && p.yc1<p.p4dImage.getSz1() &&
                p.zc0>=0 && p.zc1<p.p4dImage.getSz2()){
            out.println("---------------------aaaaaaaaaaaaaa-----------------------");
            if(!p4dImageNew.createImage(p.xc1-p.xc0+1, p.yc1-p.yc0+1, p.zc1-p.zc0+1, 1, p.p4dImage.getDatatype())){
                return false;
            }
            out.println("---------------------bbbbbbbbbbb-----------------------");
            if(p.b_brightfiled) {
                out.println("bright");
                if (!Image4DSimple.invertedsubvolumecopy(p4dImageNew,
                        p.p4dImage,
                        p.xc0, p.xc1 - p.xc0 + 1,
                        p.yc0, p.yc1 - p.yc0 + 1,
                        p.zc0, p.zc1 - p.zc0 + 1,
                        p.channel, 1)) {
                    return false;
                }
            }
            else{
                out.println("no bright");
                if(!Image4DSimple.subvolumecopy(p4dImageNew,
                        p.p4dImage,
                        p.xc0, p.xc1-p.xc0+1,
                        p.yc0, p.yc1-p.yc0+1,
                        p.zc0, p.zc1-p.zc0+1,
                        p.channel, 1)) {
                    return false;
                }
            }
        }
        else {
            out.println("Somehow invalid volume box info is detected. Ignore it. But check your Vaa3D program.");
            return false;
        }

        int marker_thresh = Integer.MAX_VALUE;
        if(p.b_intensity>0){
            if(p.b_brightfiled) p.bkg_thresh = 255 - p.bkg_thresh;

            for(int d = 1; d < p.landmarks.length; d++)
            {
                int marker_x = (int) p.landmarks[d].x - p.xc0;
                int marker_y = (int) p.landmarks[d].y - p.yc0;
                int marker_z = (int) p.landmarks[d].z - p.zc0;

                if(p4dImageNew.getValue(marker_x,marker_y,marker_z,0) < marker_thresh)
                {
                    marker_thresh = p4dImageNew.getValue(marker_x,marker_y,marker_z,0);
                }
            }

            p.bkg_thresh = (marker_thresh - 10 > p.bkg_thresh) ? marker_thresh - 10 : p.bkg_thresh;
        }

        double dfactor_xy = 1, dfactor_z = 1;
        ImagePixelType datatype = p.p4dImage.getDatatype();
        long[] in_sz = {p4dImageNew.getSz0(), p4dImageNew.getSz1(), p4dImageNew.getSz2(), 1};
        if(datatype != V3D_UINT8 || in_sz[0]>128 || in_sz[1]>128 || in_sz[2]>128)// && datatype != V3D_UINT16)
        {
            if (datatype!=V3D_UINT8)
            {
                if (!Image4DSimple.scale_img_and_converto8bit(p4dImageNew, 0, 255))
                    return false;

//                indata1d = p4dImageNew->getRawDataAtChannel(0);
                in_sz[0] = p4dImageNew.getSz0();
                in_sz[1] = p4dImageNew.getSz1();
                in_sz[2] = p4dImageNew.getSz2();
                in_sz[3] = p4dImageNew.getSz3();

                datatype = V3D_UINT8;
            }

            out.printf("x = %d  ", in_sz[0]);
            out.printf("y = %d  ", in_sz[1]);
            out.printf("z = %d  ", in_sz[2]);
            out.printf("c = %d\n", in_sz[3]);

            if (p.b_128cube==1)
            {
                if (in_sz[0]<=128 && in_sz[1]<=128 && in_sz[2]<=128)
                {
                    dfactor_z = dfactor_xy = 1;
                }
                else if (in_sz[0] >= 2*in_sz[2] || in_sz[1] >= 2*in_sz[2])
                {
                    if (in_sz[2]<=128)
                    {
                        double MM = in_sz[0];
                        if (MM<in_sz[1]) MM=in_sz[1];
                        dfactor_xy = MM / 128.0;
                        dfactor_z = 1;
                    }
                    else
                    {
                        double MM = in_sz[0];
                        if (MM<in_sz[1]) MM=in_sz[1];
                        if (MM<in_sz[2]) MM=in_sz[2];
                        dfactor_xy = dfactor_z = MM / 128.0;
                    }
                }
                else
                {
                    double MM = in_sz[0];
                    if (MM<in_sz[1]) MM=in_sz[1];
                    if (MM<in_sz[2]) MM=in_sz[2];
                    dfactor_xy = dfactor_z = MM / 128.0;
                }

                out.printf("dfactor_xy=%5.3f\n", dfactor_xy);
                out.printf("dfactor_z=%5.3f\n", dfactor_z);

                if (dfactor_z>1 || dfactor_xy>1)
                {
                    out.println("enter ds code");
                    Image4DSimple p4dImageTmp = new Image4DSimple();
                    if (!Image4DSimple.downsampling_img_xyz(p4dImageTmp,p4dImageNew,dfactor_xy,dfactor_z))
                        return false; //need to clean memory before return. a bug here

                    p4dImageNew.setData(p4dImageTmp);

//                    indata1d = p4dImageNew->getRawDataAtChannel(0);
                    in_sz[0] = p4dImageNew.getSz0();
                    in_sz[1] = p4dImageNew.getSz1();
                    in_sz[2] = p4dImageNew.getSz2();
                    in_sz[3] = p4dImageNew.getSz3();
                }
            }
        }

        if (p.bkg_thresh < 0)
        {
            if (p.channel >=0 && p.channel <= p.p4dImage.getSz3()-1)
            {
                double imgAve, imgStd;
                double[] meanStd = p4dImageNew.getMeanStdValue(0);
                imgAve = meanStd[0];
                imgStd = meanStd[1];
//            p.bkg_thresh = imgAve; //+0.5*imgStd ; //(imgAve < imgStd)? imgAve : (imgAve+imgStd)*.5;
                double td= (imgStd<10)? 10: imgStd;
                p.bkg_thresh = (int) (imgAve +0.5*td) ; //(imgAve < imgStd)? imgAve : (imgAve+imgStd)*.5; //20170523, PHC
            }
            else {
                p.bkg_thresh = 0;
            }

//            tmpstr =  qPrintable( qtstr.setNum(p.bkg_thresh).prepend("#autoset #bkg_thresh = ") ); infostring.push_back(tmpstr);
        }
        else if (p.b_brightfiled)
        {
            p.bkg_thresh = 255 - p.bkg_thresh;
        }

        float[][][] phi = new float[(int) in_sz[2]][(int) in_sz[1]][(int) in_sz[0]];
        Vector<MyMarker> inmarkers = new Vector<MyMarker>();
        for(int i = 0; i < p.landmarks.length; i++)
        {
            double x = p.landmarks[i].x - p.xc0;
            double y = p.landmarks[i].y - p.yc0;
            double z = p.landmarks[i].z - p.zc0;

            //add scaling by PHC 121127
            x /= dfactor_xy;
            y /= dfactor_xy;
            z /= dfactor_z;
            MyMarker m = new MyMarker(x,y,z);
            inmarkers.add(m);
        }

        out.println("start neuron tracing for the preprocessed image.");
        Vector<MyMarker> outTree = new Vector<MyMarker>();
        boolean b_detect_cell = false;

        out.println("bkg_thres: "+p.bkg_thresh);

        if(inmarkers.isEmpty())
        {
            out.println("Start detecting cellbody");
            int[] sz = new int[]{(int) in_sz[0],(int) in_sz[1],(int) in_sz[2]};
            FM.fastmarching_dt(p4dImageNew.getDataCZYX()[0],phi,sz,p.cnn_type,p.bkg_thresh);
            out.println("-----------------phi end---------------");
            if(phi==null){
                out.println("phi is null");
            }


            long sz0 = in_sz[0];
            long sz1 = in_sz[1];
            long sz2 = in_sz[2];
            long sz01 = sz0 * sz1;
            long tol_sz = sz01 * sz2;

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
            MyMarker max_marker = new MyMarker(max_loc[0],max_loc[1],max_loc[2]);
            inmarkers.add(max_marker);
            b_detect_cell = true;
        }

        out.println("=======================================");
        out.println("Construct the neuron tree");

        int[] sz = new int[]{(int) in_sz[0],(int) in_sz[1],(int) in_sz[2]};
        try {
            if(inmarkers.isEmpty())
            {
                out.println("need at least one markers");
            }
            else if(inmarkers.size() == 1)
            {
                out.println("only one input marker");
                if(p.is_gsdt)
                {
                    if(!b_detect_cell)
                    {
                        out.println("processing fastmarching distance transformation ...");
                        FM.fastmarching_dt(p4dImageNew.getDataCZYX()[0],phi,sz,p.cnn_type,p.bkg_thresh);
                    }

                    out.println("constructing fastmarching tree ...");
                    FM.fastmarching_tree(inmarkers.elementAt(0), phi, outTree, sz, p.cnn_type, p.bkg_thresh, p.is_break_accept);
                }
                else
                {
                    FM.fastmarching_tree(inmarkers.elementAt(0), p4dImageNew.getDataCZYX()[0], outTree, sz, p.cnn_type, p.bkg_thresh, p.is_break_accept);
                }
            }
            else
            {
                Vector<MyMarker> target = new Vector<MyMarker>();
                for(int item=1; item<inmarkers.size(); item++){
                    target.add(inmarkers.elementAt(item));
                }
                if(p.is_gsdt)
                {
                    if(!b_detect_cell)
                    {
                        out.println("processing fastmarching distance transformation ...");
                        FM.fastmarching_dt(p4dImageNew.getDataCZYX()[0],phi,sz,p.cnn_type,p.bkg_thresh);
                    }
                    out.println("constructing fastmarching tree ...");
                    FM.fastmarching_tree(inmarkers.elementAt(0), target, phi, outTree, sz, p.cnn_type,p.bkg_thresh,p.is_break_accept);
                }
                else
                {
                    FM.fastmarching_tree(inmarkers.elementAt(0), target, p4dImageNew.getDataCZYX()[0], outTree, sz, p.cnn_type,p.bkg_thresh,p.is_break_accept);
                }
            }
        }catch (OutOfMemoryError e){
            throw new Exception(e.getMessage());
        }

        out.println("Pruning neuron tree");

        Vector<MyMarker> outswc = new Vector<MyMarker>();

        HierarchyPruning.happBySample(outTree,outswc,p4dImageNew.getDataCZYX()[0], sz,sampleThreshold2,p.length_thresh);
        out.println("outswc size: " + outswc.size());

        if(p.b_128cube==1)
        {
            inmarkers.elementAt(0).x *= dfactor_xy;
            inmarkers.elementAt(0).y *= dfactor_xy;
            inmarkers.elementAt(0).z *= dfactor_z;

        }

        for(int i = 0; i < outswc.size(); i++) //add scaling 121127, PHC //add cutbox offset 121202, PHC
        {
            if(dfactor_xy>1){
                outswc.get(i).x *= dfactor_xy;
                outswc.get(i).x += dfactor_xy/2;
                outswc.get(i).y *= dfactor_xy;
                outswc.get(i).y += dfactor_xy/2;
            }
            if(dfactor_z>1){
                outswc.get(i).z *= dfactor_z;
                outswc.get(i).z += dfactor_z;
            }
            outswc.get(i).x += p.xc0;
            outswc.get(i).y += p.yc0;
            outswc.get(i).z += p.zc0;

            outswc.get(i).radius *= dfactor_xy; //use xy for now
        }
        out.println("dfactorxy: "+dfactor_xy+" dfactorz: "+dfactor_z);

        //re-estimate the radius using the original image
        double real_thres = 40; //PHC 20121011 //This should be rescaled later for datatypes that are not UINT8

        if (real_thres<p.bkg_thresh) real_thres = p.bkg_thresh;
        int[] szOriginalData = new int[]{(int) p.p4dImage.getSz0(), (int) p.p4dImage.getSz1(), (int) p.p4dImage.getSz2(), 1};
        int[][][] pOriginalData = p.p4dImage.getDataCZYX()[0];
        if(p.b_brightfiled)
        {
            for(int k=0; k<p.p4dImage.getSz2(); k++)
                for(int j=0; j<p.p4dImage.getSz1(); j++)
                    for(int i=0; i<p.p4dImage.getSz0(); i++)
                        pOriginalData[k][j][i] = 255 - pOriginalData[k][j][i];
        }

        boolean method_radius_est = p.b_RadiusFrom2D;
        for(int i=0; i<outswc.size(); i++){
            outswc.elementAt(i).radius = outswc.elementAt(i).markerRadius(pOriginalData,szOriginalData,real_thres,1,method_radius_est);
        }

        if(p.b_brightfiled)
        {
            for(int k=0; k<p.p4dImage.getSz2(); k++)
                for(int j=0; j<p.p4dImage.getSz1(); j++)
                    for(int i=0; i<p.p4dImage.getSz0(); i++)
                        pOriginalData[k][j][i] = 255 - pOriginalData[k][j][i];

        }

        p.resultNt = MyMarker.swcConvert(outswc);


        return true;
    }
}
