package com.example.basic;

import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.myapplication__volume.Rawreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.basic.Image4DSimple.ImagePixelType.*;
import static com.example.basic.Image4DSimple.TimePackType.*;
import static com.example.myapplication__volume.MainActivity.getContext;
import static java.lang.Math.floor;
import static java.lang.System.*;



public class Image4DSimple {
    public enum ImagePixelType {V3D_UNKNOWN, V3D_UINT8, V3D_UINT16, V3D_THREEBYTE, V3D_FLOAT32}
    public enum TimePackType {TIME_PACK_NONE,TIME_PACK_Z,TIME_PACK_C}
    protected long sz0;
    protected long sz1;
    protected long sz2;
    protected long sz3;
    protected long sz_time;

    protected ImagePixelType datatype;
    protected TimePackType timepacktype;

    protected String imgSrcFile;

    protected int b_error;
    protected double rez_x, rez_y, rez_z;
    protected double origin_x, origin_y, origin_z;

    protected long valid_zslcenum;
    protected long prevalid_zslice_num;

    protected int [][][][] data; //顺序为channel,z,y,x

    public Image4DSimple(){
        data = null;

        sz0 = sz1 = sz2 = sz3 = 0;
        sz_time = 0;

        datatype = V3D_UNKNOWN;
        timepacktype = TIME_PACK_NONE;

        imgSrcFile = "\0";

        b_error = 0;

        rez_x = rez_y = rez_z = 1;
        origin_x = origin_y = origin_z = 0;

        valid_zslcenum = 0;
    }

    public void finalize(){
        cleanExistData();
    }
    public void cleanExistData(){
        data = null;

        sz0 = sz1 = sz2 = sz3 = 0;
        sz_time = 0;

        datatype = V3D_UNKNOWN;
        timepacktype = TIME_PACK_NONE;

        imgSrcFile = "\0";

        b_error = 0;

        rez_x = rez_y = rez_z = 1;
        origin_x = origin_y = origin_z = 0;

        valid_zslcenum = 0;

    }

    public int[][][][] getData() {
        return data;
    }

    public long getSz0() {
        return sz0;
    }

    public long getSz1() {
        return sz1;
    }

    public long getSz2() {
        return sz2;
    }

    public long getSz3() {
        return sz3;
    }

    public long getSz_time() {
        return sz_time;
    }

    public long getValid_zslcenum() {
        return valid_zslcenum;
    }

    public long getPrevalid_zslice_num() {
        return prevalid_zslice_num;
    }

    public int getB_error() {
        return b_error;
    }

    public ImagePixelType getDatatype() {
        return datatype;
    }

    public TimePackType getTimepacktype() {
        return timepacktype;
    }

    public long getTotalUnitNumber(){
        return sz0*sz1*sz2*sz3;
    }

    public long getTotalUnitNumberPerPlane(){
        return sz0*sz1;
    }

    public long getTotalUnitNumberPerChannel(){
        return sz0*sz1*sz2;
    }

    public int getUnitBytes(){
        switch (datatype)
        {
            case V3D_UINT16: return 2;
            case V3D_FLOAT32: return 4;
            default: return 1;
        }
    }

    public long getTotalBytes(){
        return this.getUnitBytes()*sz0*sz1*sz2*sz3;
    }

    public boolean valid(){
        return data != null &&
                data.length != 0 &&
                sz0 > 0 &&
                sz1 > 0 &&
                sz2 > 0 &&
                sz3 > 0 &&
                b_error == 0 &&
                (datatype == ImagePixelType.V3D_UINT8 ||
                        datatype == ImagePixelType.V3D_UINT16 ||
                        datatype == ImagePixelType.V3D_FLOAT32);
    }

    public double getRez_x() {
        return rez_x;
    }

    public double getRez_y() {
        return rez_y;
    }

    public double getRez_z() {
        return rez_z;
    }

    public double getOrigin_x() {
        return origin_x;
    }

    public double getOrigin_y() {
        return origin_y;
    }

    public double getOrigin_z() {
        return origin_z;
    }

    public void setSz0(long sz0) {
        this.sz0 = sz0;
    }

    public void setSz1(long sz1) {
        this.sz1 = sz1;
    }

    public void setSz2(long sz2) {
        this.sz2 = sz2;
    }

    public void setSz3(long sz3) {
        this.sz3 = sz3;
    }

    public void setSz_time(long sz_time) {
        this.sz_time = sz_time;
    }

    public boolean setValid_zslcenum(long valid_zslcenum) {
        if(valid_zslcenum == 0 && this.sz2 == 0){
            this.valid_zslcenum = 0; return true;
        }
        if(valid_zslcenum>=0 && valid_zslcenum<this.sz2){
            this.valid_zslcenum = valid_zslcenum;
            return true;
        }else {
            return false;
        }
    }

    public boolean setPrevalid_zslice_num(long prevalid_zslice_num) {
        if(prevalid_zslice_num == 0 && this.sz2 == 0){
            this.prevalid_zslice_num = 0; return true;
        }
        if(prevalid_zslice_num>=0 && prevalid_zslice_num<this.sz2){
            this.prevalid_zslice_num = prevalid_zslice_num;
            return true;
        }else {
            return false;
        }
    }

    public void setDatatype(ImagePixelType datatype) {
        this.datatype = datatype;
    }

    public void setTimepacktype(TimePackType timepacktype) {
        this.timepacktype = timepacktype;
    }

    public void setData(int[][][][] data) {
        this.data = data;
    }

    public boolean setData(Image4DSimple image){
        return this.setData(image.getData(),image.getSz0(),image.getSz1(),image.getSz2(),image.getSz3(),image.getDatatype());
    }

    public boolean setData(int[][][][] data,long sz0,long sz1,long sz2,long sz3,ImagePixelType dt){
        if(data!= null && data.length>0 && sz0>0 && sz1>0 && sz2>0 && sz3>0 &&
                (dt==V3D_UINT8 || dt==V3D_UINT16 || dt==V3D_FLOAT32)){
            if(this.getData()!=null){
                this.setDataToNull();
            }
            int[][][][] data4d = new int[(int) sz3][(int) sz2][(int) sz1][(int) sz0];
            int i,j,k,c;
            for (c=0;c<sz3;c++) {
                for (k = 0; k < sz2; k++)
                    for (j = 0; j < sz1; j++)
                        for (i = 0; i < sz0; i++) {
                            data4d[c][k][j][i] = data[c][k][j][i];
                        }
            }
            this.setData(data4d);
            this.setSz0(sz0);
            this.setSz1(sz1);
            this.setSz2(sz2);
            this.setSz3(sz3);
            this.setDatatype(dt);
            return true;
        }
        else
            return false;
    }

    public void setDataToNull(){
        this.data = null;
    }

    boolean setValueUINT8(long x,long y,long z,long channel,int val){
        if(datatype != ImagePixelType.V3D_UINT8){
            out.println("Warning: This image is not stored in 8bit.");
            return false;
        }
        if(x>=0&&x<sz0&&y>=0&&y<sz1&&z>=0&&z<sz1&&channel>=0&&channel<sz3){
            this.data[(int) channel][(int) z][(int) y][(int) x] =val;
            return  true;
        }else {
            return false;
        }

    }
    public int getValueUINT8(long x,long y,long z,long channel){
        if(datatype != ImagePixelType.V3D_UINT8){
            out.println("Warning: This image is not stored in 8bit.");
            return 0;
        }
        if(x>=0&&x<sz0&&y>=0&&y<sz1&&z>=0&&z<sz1&&channel>=0&&channel<sz3){
            int val = this.data[(int) channel][(int) z][(int) y][(int) x];
            return  val;
        }else {
            out.println("Image4DSimple::getIntensity() error: index exceeds the image size");
            return 0;
        }
    }

    boolean setValue(long x,long y,long z,long channel,int val){
        if(x>=0&&x<sz0&&y>=0&&y<sz1&&z>=0&&z<sz1&&channel>=0&&channel<sz3){
            this.data[(int) channel][(int) z][(int) y][(int) x] =val;
            return  true;
        }else {
            return false;
        }

    }
    public int getValue(long x,long y,long z,long channel){
        if(x>=0&&x<sz0&&y>=0&&y<sz1&&z>=0&&z<sz1&&channel>=0&&channel<sz3){
            int val = this.data[(int) channel][(int) z][(int) y][(int) x];
            return  val;
        }else {
            out.println("Image4DSimple::getIntensity() error: index exceeds the image size");
            return 0;
        }
    }

    public boolean setRez_x(double rez_x) {
        if(rez_x<0)
            return false;
        else {
            this.rez_x = rez_x;
            return true;
        }
    }

    public boolean setRez_y(double rez_y) {
        if(rez_y<0)
            return false;
        else {
            this.rez_y = rez_y;
            return true;
        }
    }

    public boolean setRez_z(double rez_z) {
        if(rez_z<0)
            return false;
        else {
            this.rez_z = rez_z;
            return true;
        }
    }

    public void setOrigin_x(double origin_x) {
        this.origin_x = origin_x;
    }

    public void setOrigin_y(double origin_y) {
        this.origin_y = origin_y;
    }

    public void setOrigin_z(double origin_z) {
        this.origin_z = origin_z;
    }

    public void setImgSrcFile(String imgSrcFile) {
        this.imgSrcFile = imgSrcFile;
    }

    public String getImgSrcFile() {
        return imgSrcFile;
    }

//    public void loadImage(String filename){
//
//    }

    public static Image4DSimple loadImage(String filepath){
        Image4DSimple image = new Image4DSimple();
        Rawreader rr = new Rawreader();
        File file = new File(filepath);
        long length = 0;
        InputStream is = null;
        if (file.exists()){
            try {
                length = file.length();
                is = new FileInputStream(file);
//                grayscale =  rr.run(length, is);
                image = rr.run(length, is);

                Log.v("getIntensity_3d", filepath);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        else {
            Uri uri = Uri.parse(filepath);

            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        getContext().getContentResolver().openFileDescriptor(uri, "r");

                is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);

                length = (int)parcelFileDescriptor.getStatSize();

                Log.v("MyPattern","Successfully load intensity");

            }catch (Exception e){
                Log.v("MyPattern","Some problems in the MyPattern when load intensity");
            }


            image =  rr.run(length, is);

        }
        if (image != null) {
            image.setImgSrcFile(filepath);
        }
        return image;
    }

    public boolean createImage(long mysz0, long mysz1, long mysz2, long mysz3, ImagePixelType mytype) throws Exception{
        if (mysz0<=0 || mysz1<=0 || mysz2<=0 || mysz3<=0) return false; //note that for this sentence I don't change b_error flag
        if (data.length>0) {data = null; sz0=0; sz1=0; sz2=0;sz3=0; datatype= V3D_UNKNOWN;}
        data = new int[(int) mysz0][(int) mysz1][(int) mysz2][(int) mysz3];
        if(data == null || data.length == 0) {
            b_error = 1;
            out.println("Fail to allocate memory in My4DImageSimple::createImage();");
            return false;
        }
        sz0=mysz0;
        sz1=mysz1;
        sz2=mysz2;sz3=mysz3;
        datatype=mytype;
        b_error=0; //note that here I update b_error
        return true;
    }

    public static boolean subvolumecopy(Image4DSimple  dstImg,
                                        Image4DSimple  srcImg, long x0, long szx, long y0, long szy, long z0, long szz, long c0, long szc){
        if (!dstImg.valid() || !srcImg.valid())
        {
            out.println("Invalid parameters for the function subvolumecopy(). 111");
            return false;
        }
        if (x0<0 || szx<1 || szx> dstImg.getSz0() || x0+szx > srcImg.getSz0() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 21.");
            return false;
        }
        if (y0<0 || szy<1 || szy> dstImg.getSz1() || y0+szy > srcImg.getSz1() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 22.");
            return false;
        }
        if (z0<0 || szz<1 || szz> dstImg.getSz2() || z0+szz > srcImg.getSz2() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 23.");
            return false;
        }
        if ( c0<0 || szc<1 || szc> dstImg.getSz3() || c0+szc > srcImg.getSz3() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 24.");
            return false;
        }
        if (dstImg.getDatatype() != srcImg.getDatatype() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 3.");
            return false;
        }

        int i,j,k,c;
        for (c=0;c<szc;c++)
            for (k=0;k<szz;k++)
                for (j=0;j<szy;j++)
                    for (i=0;i<szx;i++)
                    {
                        dstImg.setValue(i,j,k,c,srcImg.getValue(i+x0,j+y0,k+z0,c+c0));
                    }
        return true;
    }

    public static boolean invertedsubvolumecopy(Image4DSimple  dstImg,
                                                Image4DSimple  srcImg,
                                                long x0, long szx,
                                                long y0, long szy,
                                                long z0, long szz,
                                                long c0, long szc){
        if (!dstImg.valid() || !srcImg.valid())
        {
            out.println("Invalid parameters for the function subvolumecopy(). 111");
            return false;
        }
        if (x0<0 || szx<1 || szx> dstImg.getSz0() || x0+szx > srcImg.getSz0() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 21.");
            return false;
        }
        if (y0<0 || szy<1 || szy> dstImg.getSz1() || y0+szy > srcImg.getSz1() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 22.");
            return false;
        }
        if (z0<0 || szz<1 || szz> dstImg.getSz2() || z0+szz > srcImg.getSz2() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 23.");
            return false;
        }
        if ( c0<0 || szc<1 || szc> dstImg.getSz3() || c0+szc > srcImg.getSz3() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 24.");
            return false;
        }
        if (dstImg.getDatatype() != srcImg.getDatatype() )
        {
            out.println("Invalid parameters for the function subvolumecopy() 3.");
            return false;
        }

        int i,j,k,c;
        if(dstImg.getDatatype() == ImagePixelType.V3D_UINT8){
            for (c=0;c<szc;c++)
                for (k=0;k<szz;k++)
                    for (j=0;j<szy;j++)
                        for (i=0;i<szx;i++)
                        {
                            dstImg.setValueUINT8(i,j,k,c,255-srcImg.getValueUINT8(i+x0,j+y0,k+z0,c+c0));
                        }
        }else {
            out.println("Invalid data type in invertedsubvolumecopy(). do nothing.");
            return false;
        }

        return true;
    }

    public int[] getMinMaxValue(long channel){
        int[] minMax = null;
        if(!this.valid()){
            return minMax;
        }else {
            minMax = new int[2];
            minMax[0] = Integer.MAX_VALUE;
            minMax[1] = 0;
            int k,j,i;
            for (k=0;k<this.getSz2();k++)
                for (j=0;j<this.getSz1();j++)
                    for (i=0;i<this.getSz0();i++)
                    {
                        minMax[0] = (this.getValue(i,j,k,channel)<minMax[0])?this.getValue(i,j,k,channel):minMax[0];
                        minMax[1] = (this.getValue(i,j,k,channel)>minMax[0])?this.getValue(i,j,k,channel):minMax[1];
                    }

            return minMax;
        }
    }

    public double[] getMeanStdValue(long channel){
        double[] meanStd = null;
        if(channel<0 || channel>=this.getSz3()){
            out.println("The channel is out of data!");
            return  meanStd;
        }
        if(!this.valid()){
            return meanStd;
        }else{
            meanStd = new double[2];
            double mean = 0.0;
            double std = 0.0;
            int k,j,i;
            for (k=0;k<this.getSz2();k++) {
                for (j = 0; j < this.getSz1(); j++)
                    for (i = 0; i < this.getSz0(); i++) {
                        mean += this.getValue(i, j, k, channel);
                    }
            }
            mean /= (double)this.getTotalUnitNumberPerChannel();
            for (k=0;k<this.getSz2();k++) {
                for (j = 0; j < this.getSz1(); j++)
                    for (i = 0; i < this.getSz0(); i++) {
                        std += Math.pow(this.getValue(i, j, k, channel)-meanStd[0],2);
                    }
            }
            std /= (double)(this.getTotalUnitNumberPerChannel()-1);
            std = Math.sqrt(std);

            meanStd[0] = mean;
            meanStd[1] = std;
            return meanStd;
        }
    }

    //map the value linear from [lower_th, higher_th] to [target_min, target_max].
    public static boolean scaleintensity(Image4DSimple  p4dImage, int channo, double lower_th, double higher_th, double target_min, double target_max){
        if ( !p4dImage.valid() || channo>=p4dImage.getSz3() || channo < 0)
        {
            out.println("Invalid chan parameter in scaleintensity();");
            return false;
        }

        if (lower_th==-9999 && higher_th==-9999)
        {
            int[] minMax = p4dImage.getMinMaxValue(channo);
            lower_th = minMax[0];
            higher_th = minMax[1];
        }

        double t;
        if (lower_th>higher_th) {t=lower_th; lower_th=higher_th; higher_th=t;}
        if (target_min>target_max) {t=target_min; target_min=target_max; target_max=t;}

        double rate = (higher_th==lower_th) ? 1 : (target_max-target_min)/(higher_th-lower_th); //if the two th vals equal, then later-on t-lower_th will be 0 anyway

        int i,j,k;
        for (k=0;k<p4dImage.getSz2();k++)
            for (j=0;j<p4dImage.getSz1();j++)
                for (i=0;i<p4dImage.getSz0();i++)
                {
                    t = p4dImage.getValue(i,j,k,channo);
                    if (t>higher_th) t=higher_th;
                    else if (t<lower_th) t=lower_th;
                    p4dImage.setValue(i,j,k,channo,(int)((t-lower_th)*rate +target_min));
                }
        return true;
    }

    //lb, ub: lower bound, upper bound
    public static boolean scale_img_and_converto8bit(Image4DSimple  p4dImage, int lb, int ub){
        if ( !p4dImage.valid())
        {
            out.println("Your data is invalid in scale_img_and_converto8bit().");
            return false;
        }
        int c,k,j,i;
        for(c=0;c<p4dImage.getSz3();c++){
            scaleintensity(p4dImage,c,-9999,-9999,lb,ub);
        }
        p4dImage.setDatatype(V3D_UINT8);
        return true;
    }

    public static boolean resample3dimg_interp(Image4DSimple  dstImg,
                                               Image4DSimple  srcImg,
                                               double dfactor_x, double dfactor_y, double dfactor_z, int interp_method)
    {
        long[] sz = {srcImg.getSz0(),srcImg.getSz1(),srcImg.getSz2(),srcImg.getSz3()};
        if (!srcImg.valid())
        {
            out.println("The input to resample3dimg_interp() are invalid");
            return false;
        }

        if (dfactor_x<1 || dfactor_y<1 || dfactor_z<1)
        {
            out.println("The resampling factor must be >1 in resample3dimg_linear_interp(), because now only DOWN-sampling is supported");
            return false;
        }

        if (sz[0]<1 || sz[1]<1 || sz[2]<1 || sz[3]<1)
        {
            out.println("Input image size is not valid in resample3dimg_interp()");
            return false;
        }

        if (interp_method!=1) //0 for nearest neighbor interp and 1 for linear
        {
            out.printf("Invalid interpolation code. Now only linear interpolation is supported in  resample3dimg_linear_interp() [you pass a code %d].\n", interp_method);
            return false;
        }

        int cur_sz0 = (int) (floor((double)sz[0]/ (double)(dfactor_x)));
        int cur_sz1 = (int) (floor((double)sz[1] / (double)(dfactor_y)));
        int cur_sz2 = (int) (floor((double)sz[2] / (double)(dfactor_z)));
        int cur_sz3 = (int) sz[3];

        if (cur_sz0 <= 0 || cur_sz1 <=0 || cur_sz2<=0 || cur_sz3<=0)
        {
            out.println("The dfactors are not properly set, -- the resulted resampled size is too small. Do nothing. ");
            return false;
        }

        int[][][][] img4d = new int[cur_sz3][cur_sz2][cur_sz1][cur_sz0];
        if (img4d == null || img4d.length == 0)
        {
            out.println("Fail to allocate memory. [%s][%d].");
            return false;
        }

        for (int c=0;c<cur_sz3;c++)
        {
            for (int k=0;k<cur_sz2;k++)
            {
                long k2low=(long)(floor(k*dfactor_z)), k2high=(long)(floor((k+1)*dfactor_z-1));
                if (k2high>sz[2] -1) k2high = sz[2] -1;
                long kw = k2high - k2low + 1;

                for (int j=0;j<cur_sz1;j++)
                {
                    long j2low=(long)(floor(j*dfactor_y)), j2high=(long)(floor((j+1)*dfactor_y-1));
                    if (j2high>sz[1]-1) j2high = sz[1]-1;
                    long jw = j2high - j2low + 1;

                    for (int i=0;i<cur_sz0;i++)
                    {
                        long i2low=(long)(floor(i*dfactor_x)), i2high=(long)(floor((i+1)*dfactor_x-1));
                        if (i2high>sz[0]-1) i2high = sz[0]-1;
                        long iw = i2high - i2low + 1;

                        double cubevolume = (double)(kw * jw * iw);
                        //cout<<cubevolume <<" ";

                        double s=0.0;
                        for (long k1=k2low;k1<=k2high;k1++)
                        {
                            for (long j1=j2low;j1<=j2high;j1++)
                            {
                                for (long i1=i2low;i1<=i2high;i1++)
                                {
                                    s += srcImg.getValue(i1,j1,k1,c);// in_tmp4d[c][k1][j1][i1];
                                }
                            }
                        }

                        img4d[c][k][j][i] = (int) (s/cubevolume);
                    }
                }
            }
        }

        dstImg.setData(img4d,cur_sz0,cur_sz1,cur_sz2,cur_sz3,srcImg.getDatatype());

        return true;
    }

    public static boolean downsampling_img_xyz(Image4DSimple  dstImg,
                                               Image4DSimple  srcImg,
                                               double dfactor_xy, double dfactor_z){
        if (dfactor_xy <= 1 || dfactor_z < 1) //note allow dfactor_z=1
        {
            out.println("The input parameters of downsampling_xyz() is not valid.");
            return false;
        }
        int interp_method = 1;
        return Image4DSimple.resample3dimg_interp(dstImg,srcImg,dfactor_xy,dfactor_xy,dfactor_z,interp_method);
    }
}
