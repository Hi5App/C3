package com.tracingfunc.app2;

import com.example.basic.Image4DSimple;
import com.example.basic.Image4DSimple.ImagePixelType;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import static com.example.basic.Image4DSimple.ImagePixelType.V3D_UINT8;
import static java.lang.System.*;

public class V3dNeuronAPP2Tracing {
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

        if(p.xc1>=p.xc0 && p.yc1>=p.yc0 && p.zc1>=p.zc0 &&
                p.xc0>=0 && p.xc1<p.p4dImage.getSz0() &&
                p.yc0>=0 && p.yc1<p.p4dImage.getSz1() &&
                p.zc0>=0 && p.zc1<p.p4dImage.getSz2()){
            if(!p4dImageNew.createImage(p.xc1-p.xc0+1, p.yc1-p.yc0+1, p.zc1-p.zc0+1, 1, p.p4dImage.getDatatype())){
                return false;
            }
            if(p.b_brightfiled) {
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
        if(datatype != V3D_UINT8 || in_sz[0]>256 || in_sz[1]>256 || in_sz[2]>256)// && datatype != V3D_UINT16)
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

            out.printf("x = %ld  ", in_sz[0]);
            out.printf("y = %ld  ", in_sz[1]);
            out.printf("z = %ld  ", in_sz[2]);
            out.printf("c = %ld\n", in_sz[3]);

            if (p.b_256cube>0)
            {
                if (in_sz[0]<=256 && in_sz[1]<=256 && in_sz[2]<=256)
                {
                    dfactor_z = dfactor_xy = 1;
                }
                else if (in_sz[0] >= 2*in_sz[2] || in_sz[1] >= 2*in_sz[2])
                {
                    if (in_sz[2]<=256)
                    {
                        double MM = in_sz[0];
                        if (MM<in_sz[1]) MM=in_sz[1];
                        dfactor_xy = MM / 256.0;
                        dfactor_z = 1;
                    }
                    else
                    {
                        double MM = in_sz[0];
                        if (MM<in_sz[1]) MM=in_sz[1];
                        if (MM<in_sz[2]) MM=in_sz[2];
                        dfactor_xy = dfactor_z = MM / 256.0;
                    }
                }
                else
                {
                    double MM = in_sz[0];
                    if (MM<in_sz[1]) MM=in_sz[1];
                    if (MM<in_sz[2]) MM=in_sz[2];
                    dfactor_xy = dfactor_z = MM / 256.0;
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



        float[] phi = null;
        Vector<MyMarker> inmarkers;
        for(int i = 0; i < p.landmarks.size(); i++)
        {
            double x = p.landmarks[i].x - p.xc0 -1;
            double y = p.landmarks[i].y - p.yc0 -1;
            double z = p.landmarks[i].z - p.zc0 -1;

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

        Vector<MyMarker> outtree;

        //add a timer by PHC 121005
//        QElapsedTimer timer2;
//        timer2.start();

        if(inmarkers.isEmpty())
        {
            out.println("Start detecting cellbody");
//            cout << "IMAGE DATATYPE: " << datatype << endl;
            switch(datatype)
            {
                case V3D_UINT8:
                    fastmarching_dt_XY(indata1d, phi, in_sz[0], in_sz[1], in_sz[2],p.cnn_type, p.bkg_thresh);
                    break;
                case V3D_UINT16:  //this is no longer needed, as the data type has been converted above
                    fastmarching_dt_XY((short int*)indata1d, phi, in_sz[0], in_sz[1], in_sz[2],p.cnn_type, p.bkg_thresh);
                    break;
                default:
                    v3d_msg("Unsupported data type");
                    break;
            }

            long sz0 = in_sz[0];
            long sz1 = in_sz[1];
            long sz2 = in_sz[2];
            long sz01 = sz0 * sz1;
            long tol_sz = sz01 * sz2;

            long max_loc = 0;
            double max_val = phi[0];
            for(long i = 0; i < tol_sz; i++)
            {
                if(phi[i] > max_val)
                {
                    max_val = phi[i];
                    max_loc = i;
                }
            }
            MyMarker max_marker(max_loc % sz0, max_loc % sz01 / sz0, max_loc / sz01);
            inmarkers.push_back(max_marker);
        }

        out.println("=======================================");
        out.println("Construct the neuron tree");
        if(inmarkers.empty())
        {
            out.println("need at least one markers");
        }
        else if(inmarkers.size() == 1)
        {
            out.println("only one input marker");
            if(p.is_gsdt)
            {
                if(phi == 0)
                {
                    cout<<"processing fastmarching distance transformation ..."<<endl;
                    switch(datatype)
                    {
                        case V3D_UINT8:
                            fastmarching_dt(indata1d, phi, in_sz[0], in_sz[1], in_sz[2], p.cnn_type, p.bkg_thresh);
                            break;
                        case V3D_UINT16:  //this is no longer needed, as the data type has been converted above
                            fastmarching_dt((short int *)indata1d, phi, in_sz[0], in_sz[1], in_sz[2], p.cnn_type, p.bkg_thresh);
                            break;
                        default:
                            v3d_msg("Unsupported data type");
                            break;
                    }
                }

                out.println("constructing fastmarching tree ...");
                fastmarching_tree(inmarkers[0], phi, outtree, in_sz[0], in_sz[1], in_sz[2], p.cnn_type, p.bkg_thresh, p.is_break_accept);
            }
            else
            {
                switch(datatype)
                {
                    case V3D_UINT8:
                        v3d_msg("8bit", 0);
                        fastmarching_tree(inmarkers[0], indata1d, outtree, in_sz[0], in_sz[1], in_sz[2], p.cnn_type, p.bkg_thresh, p.is_break_accept);
                        break;
                    case V3D_UINT16: //this is no longer needed, as the data type has been converted above
                        v3d_msg("16bit", 0);
                        fastmarching_tree(inmarkers[0], (short int*)indata1d, outtree, in_sz[0], in_sz[1], in_sz[2], p.cnn_type, p.bkg_thresh, p.is_break_accept);
                        break;
                    default:
                        v3d_msg("Unsupported data type");
                        break;
                }
            }
        }
        else
        {
            vector<MyMarker> target; target.insert(target.end(), inmarkers.begin()+1, inmarkers.end());
            if(p.is_gsdt)
            {
                if(phi == 0)
                {
                    cout<<"processing fastmarching distance transformation ..."<<endl;
                    switch(datatype)
                    {
                        case V3D_UINT8:
                            fastmarching_dt(indata1d, phi, in_sz[0], in_sz[1], in_sz[2], p.cnn_type, p.bkg_thresh);
                            break;
                        case V3D_UINT16:
                            fastmarching_dt((short int *)indata1d, phi, in_sz[0], in_sz[1], in_sz[2], p.cnn_type, p.bkg_thresh);
                            break;
                    }
                }
                cout<<endl<<"constructing fastmarching tree ..."<<endl;
                fastmarching_tree(inmarkers[0], target, phi, outtree, in_sz[0], in_sz[1], in_sz[2], p.cnn_type);
            }
            else
            {
                switch(datatype)
                {
                    case V3D_UINT8:
                        fastmarching_tree(inmarkers[0], target, indata1d, outtree, in_sz[0], in_sz[1], in_sz[2], p.cnn_type);
                        break;
                    case V3D_UINT16:
                        fastmarching_tree(inmarkers[0], target, (short int*) indata1d, outtree, in_sz[0], in_sz[1], in_sz[2], p.cnn_type);
                        break;
                }
            }
        }
        cout<<"======================================="<<endl;

        //save a copy of the ini tree
        cout<<"Save the initial unprunned tree"<<endl;
        vector<MyMarker*> & inswc = outtree;

        if (1)
        {
            long tmpi;

            vector<MyMarker*> tmpswc;
            for (tmpi=0; tmpi<inswc.size(); tmpi++)
            {
                MyMarker * curp = new MyMarker(*(inswc[tmpi]));
                tmpswc.push_back(curp);

                if (dfactor_xy>1) inswc[tmpi]->x *= dfactor_xy;
                inswc[tmpi]->x += (p.xc0);
                if (dfactor_xy>1) inswc[tmpi]->x += dfactor_xy/2;

                if (dfactor_xy>1) inswc[tmpi]->y *= dfactor_xy;
                inswc[tmpi]->y += (p.yc0);
                if (dfactor_xy>1) inswc[tmpi]->y += dfactor_xy/2;

                if (dfactor_z>1) inswc[tmpi]->z *= dfactor_z;
                inswc[tmpi]->z += (p.zc0);
                if (dfactor_z>1)  inswc[tmpi]->z += dfactor_z/2;
            }

            saveSWC_file(QString(p.p4dImage->getFileName()).append("_ini.swc").toStdString(), inswc, infostring);

            for (tmpi=0; tmpi<inswc.size(); tmpi++)
            {
                inswc[tmpi]->x = tmpswc[tmpi]->x;
                inswc[tmpi]->y = tmpswc[tmpi]->y;
                inswc[tmpi]->z = tmpswc[tmpi]->z;
            }

            for(tmpi = 0; tmpi < tmpswc.size(); tmpi++)
                delete tmpswc[tmpi];
            tmpswc.clear();
        }


        cout<<"Pruning neuron tree"<<endl;

        vector<MyMarker*> outswc;
        if(p.is_coverage_prune)
        {
            v3d_msg("start to use APP2 program.\n", 0);
            happ(inswc, outswc, indata1d, in_sz[0], in_sz[1], in_sz[2], p.bkg_thresh, p.length_thresh, p.SR_ratio);
        }
        else
        {
            hierarchy_prune(inswc, outswc, indata1d, in_sz[0], in_sz[1], in_sz[2], p.length_thresh);
            if(1) //get radius
            {
                double real_thres = 40; //PHC 20121011
                if (real_thres<p.bkg_thresh) real_thres = p.bkg_thresh;
                for(i = 0; i < outswc.size(); i++)
                {
                    outswc[i]->radius = markerRadius(indata1d, in_sz, *(outswc[i]), real_thres);
                }
            }
        }

        qint64 etime2 = timer2.elapsed();
        qDebug() << " **** neuron tracing procedure takes [" << etime2 << " milliseconds]";

        if (p4dImageNew) {delete p4dImageNew; p4dImageNew=0;} //free buffer

        if(p.b_256cube)
        {
            inmarkers[0].x *= dfactor_xy;
            inmarkers[0].y *= dfactor_xy;
            inmarkers[0].z *= dfactor_z;

        }

        if(1)
        {
            QString rootposstr="", tmps;
            tmps.setNum(int(inmarkers[0].x+0.5)).prepend("_x"); rootposstr += tmps;
            tmps.setNum(int(inmarkers[0].y+0.5)).prepend("_y"); rootposstr += tmps;
            tmps.setNum(int(inmarkers[0].z+0.5)).prepend("_z"); rootposstr += tmps;
            //QString outswc_file = callback.getImageName(curwin) + rootposstr + "_app2.swc";
            QString outswc_file;
            if(!p.outswc_file.isEmpty())
                outswc_file = p.outswc_file;
            else
                outswc_file = QString(p.p4dImage->getFileName()) + rootposstr + "_app2.swc";

            for(i = 0; i < outswc.size(); i++) //add scaling 121127, PHC //add cutbox offset 121202, PHC
            {
                if (dfactor_xy>1) outswc[i]->x *= dfactor_xy;
                outswc[i]->x += (p.xc0);
                if (dfactor_xy>1) outswc[i]->x += dfactor_xy/2; //note that the offset corretion might not be accurate. PHC 121127

                if (dfactor_xy>1) outswc[i]->y *= dfactor_xy;
                outswc[i]->y += (p.yc0);
                if (dfactor_xy>1) outswc[i]->y += dfactor_xy/2;

                if (dfactor_z>1) outswc[i]->z *= dfactor_z;
                outswc[i]->z += (p.zc0);
                if (dfactor_z>1)  outswc[i]->z += dfactor_z/2;

                outswc[i]->radius *= dfactor_xy; //use xy for now
            }

            //re-estimate the radius using the original image
            double real_thres = 40; //PHC 20121011 //This should be rescaled later for datatypes that are not UINT8

            if (real_thres<p.bkg_thresh) real_thres = p.bkg_thresh;
            long szOriginalData[4] = {p.p4dImage->getXDim(), p.p4dImage->getYDim(), p.p4dImage->getZDim(), 1};
            unsigned char * pOriginalData = (unsigned char *)(p.p4dImage->getRawDataAtChannel(p.channel));
            if(p.b_brightfiled)
            {
                for(long i = 0; i < p.p4dImage->getTotalUnitNumberPerChannel(); i++)
                pOriginalData[i] = 255 - pOriginalData[i];

            }

            int method_radius_est = ( p.b_RadiusFrom2D ) ? 1 : 2;

            switch (p.p4dImage->getDatatype())
            {
                case V3D_UINT8:
                {
                    for(i = 0; i < outswc.size(); i++)
                    {
                        //printf(" node %ld of %ld.\n", i, outswc.size());
                        outswc[i]->radius = markerRadius(pOriginalData, szOriginalData, *(outswc[i]), real_thres, method_radius_est);
                    }
                }
                break;
                case V3D_UINT16:
                {
                    unsigned short int *pOriginalData_uint16 = (unsigned short int *)pOriginalData;
                    for(i = 0; i < outswc.size(); i++)
                    {
                        //printf(" node %ld of %ld.\n", i, outswc.size());
                        outswc[i]->radius = markerRadius(pOriginalData_uint16, szOriginalData, *(outswc[i]), real_thres * 16, method_radius_est); //*16 as it is often 12 bit data
                    }
                }
                break;
                case V3D_FLOAT32:
                {
                    float *pOriginalData_float = (float *)pOriginalData;
                    for(i = 0; i < outswc.size(); i++)
                    {
                        //printf(" node %ld of %ld.\n", i, outswc.size());
                        outswc[i]->radius = markerRadius(pOriginalData_float, szOriginalData, *(outswc[i]), real_thres, method_radius_est);
                    }
                }
                break;
                default:
                    break;
            }

            if(p.b_brightfiled)
            {
                for(long i = 0; i < p.p4dImage->getTotalUnitNumberPerChannel(); i++)
                pOriginalData[i] = 255 - pOriginalData[i];

            }
            //prepare the output comments for neuron info in the swc file

            tmpstr =  qPrintable( qtstr.setNum(etime1).prepend("#neuron preprocessing time (milliseconds) = ") ); infostring.push_back(tmpstr);
            tmpstr =  qPrintable( qtstr.setNum(etime2).prepend("#neuron tracing time (milliseconds) = ") ); infostring.push_back(tmpstr);
            saveSWC_file(outswc_file.toStdString(), outswc, infostring);

            if(outswc.size()>1)
            {

                //call sort_swc function

                V3DPluginArgItem arg;
                V3DPluginArgList input_resample;
                V3DPluginArgList input_sort;
                V3DPluginArgList output;

                arg.type = "random";std::vector<char*> arg_input_resample;
                std:: string fileName_Qstring(outswc_file.toStdString());char* fileName_string =  new char[fileName_Qstring.length() + 1]; strcpy(fileName_string, fileName_Qstring.c_str());
                arg_input_resample.push_back(fileName_string);
                arg.p = (void *) & arg_input_resample; input_resample<< arg;
                arg.type = "random";std::vector<char*> arg_resample_para; arg_resample_para.push_back("10");arg.p = (void *) & arg_resample_para; input_resample << arg;
                arg.type = "random";std::vector<char*> arg_output;arg_output.push_back(fileName_string); arg.p = (void *) & arg_output; output<< arg;
                QString full_plugin_name_resample = "resample_swc";
                QString func_name_resample = "resample_swc";
                if(p.b_resample)
                    callback.callPluginFunc(full_plugin_name_resample,func_name_resample,input_resample,output);
                arg.type = "random";std::vector<char*> arg_input_sort;
                arg_input_sort.push_back(fileName_string);
                arg.p = (void *) & arg_input_sort; input_sort<< arg;
                arg.type = "random";std::vector<char*> arg_sort_para; arg_sort_para.push_back("0");arg.p = (void *) & arg_sort_para; input_sort << arg;
                QString full_plugin_name_sort = "sort_neuron_swc";
                QString func_name_sort = "sort_swc";
                callback.callPluginFunc(full_plugin_name_sort,func_name_sort, input_sort,output);

                vector<MyMarker*> temp_out_swc = readSWC_file(outswc_file.toStdString());
                saveSWC_file_app2(outswc_file.toStdString(), temp_out_swc, infostring);
            }
            //v3d_msg(QString("The tracing uses %1 ms (%2 ms for preprocessing and %3 for tracing). Now you can drag and drop the generated swc fle [%4] into Vaa3D."
            //                ).arg(etime1+etime2).arg(etime1).arg(etime2).arg(outswc_file), p.b_menu);

            if (0) //by PHC 120909
            {
//            try
//            {
//                NeuronTree nt = readSWC_file(outswc_file);
//                callback.setSWC(curwin, nt);
//                callback.open3DWindow(curwin);
//                callback.getView3DControl(curwin)->updateWithTriView();
//            }
//            catch(...)
//            {
//                return false;
//            }
            }
        }
        else
        {
//        NeuronTree nt = swc_convert(outswc);
//        callback.setSWC(curwin, nt);
//        callback.open3DWindow(curwin);
//        callback.getView3DControl(curwin)->updateWithTriView();
        }
        //release memory
        if(phi){delete [] phi; phi = 0;}
        for(long i = 0; i < outtree.size(); i++) delete outtree[i];
        outtree.clear();

        if (b_dofunc)
        {
            if (p.p4dImage) {delete p.p4dImage; p.p4dImage=NULL;}
        }

        return true;
    }
}