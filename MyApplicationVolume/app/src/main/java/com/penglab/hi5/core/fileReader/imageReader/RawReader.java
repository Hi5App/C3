package com.penglab.hi5.core.fileReader.imageReader;

import android.util.Log;

import com.penglab.hi5.basic.ByteTranslate;
import com.penglab.hi5.basic.image.Image4DSimple;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RawReader {

    private final String TAG = "RawReader";
    private final String formatKey = "raw_image_stack_by_hpeng";

    public Image4DSimple read(long length, InputStream is) {

        // Read in the header values...
        try {
            FileInputStream fid = (FileInputStream)(is);
            int lenkey = formatKey.length();
            long fileSize = length;

            // read the format key
            if (fileSize<lenkey+2+4*4+1) // datatype has 2 bytes, and sz has 4*4 bytes and endian flag has 1 byte.
                throw new Exception("The size of your input file is too small and is not correct, -- it is too small to contain the legal header.");
            byte[] by = new byte[lenkey];
            long nread = fid.read(by);
            String keyread = new String(by);
            if (nread!=lenkey)
                throw new Exception("File unrecognized or corrupted file.");
            if (!keyread.equals(formatKey))
                throw new Exception("Unrecognized file format.");

            // read the endianness
            by = new byte[1];
            fid.read(by);

            if (by[0]!='B' && by[0]!='L')
                throw new Exception("This program only supports big- or little- endian but not other format. Check your endian.");

            boolean isBig = (by[0]=='B');

            // read the data type info
            by = new byte[2];
            fid.read(by);
            short deCode = (short) bytes2int(by,isBig);
            int datatype;
            switch (deCode){
                case 1:
                    datatype = 1;
                    break;
                case 2:
                    datatype = 2;
                    break;
                case 4:
                    datatype = 4;
                    break;
                default:
                    throw new Exception("Unrecognized datatype code"+deCode+". The file is incorrect or this code is not supported in this version");
            }
            int unitSize = datatype;

            // read the data size info (the data size is stored in either 2-byte or 4-byte space)
            long[] sz = new long[4];
            long totalUnit = 1;

            // first assume this is a 2-byte file
            by = new byte[2];
            for (int i=0 ; i<4; i++) {
                fid.read(by);
                sz[i] = bytes2int(by,isBig);
                totalUnit *= sz[i];
            }

            if ((totalUnit*unitSize+4*2+2+1+lenkey) != fileSize) {
                // see if this is a 4-byte file
                if (isBig)  {
                    sz[0] = sz[0]*64+sz[1];
                    sz[1] = sz[2]*64+sz[3];
                } else {
                    sz[0] = sz[1]*64+sz[0];
                    sz[1] = sz[3]*64+sz[2];
                }
                by = new byte[4];
                for (int i=2;i<4;i++) {
                    fid.read(by);
                    sz[i] = bytes2int(by,isBig);
                }
                totalUnit = 1;
                for (int i=0; i<4; i++){
                    totalUnit *= sz[i];
                }

                if ((totalUnit*unitSize+4*4+2+1+lenkey) != fileSize)
                    throw new Exception("The input file has a size different from what specified in the header. Exit.");
            }

            byte [] data = new byte[(int)(totalUnit*unitSize)];
            fid.read(data);
            int w = (int) sz[0];
            int h = (int) sz[1];
            int nChannel = (int) sz[3];

            byte [] data_n = new byte[(int)(totalUnit*unitSize/8)];
            downsampledata(data,data_n,w,h, (int) sz[2]);
            // construct img into an array of ImageStacks, the length of array equals number of color channels.


            fid.close();
            Image4DSimple image = new Image4DSimple();
            Image4DSimple.ImagePixelType dt;
            switch (datatype){
                case 1:
                    dt = Image4DSimple.ImagePixelType.V3D_UINT8;
                    break;
                case 2:
                    dt = Image4DSimple.ImagePixelType.V3D_UINT16;
                    break;
                case 4:
                    dt = Image4DSimple.ImagePixelType.V3D_FLOAT32;
                    break;
                default:
                    dt = Image4DSimple.ImagePixelType.V3D_UNKNOWN;
            }
            image.setDataFromImage(data_n, sz[0]/2, sz[1]/2, sz[2]/2, sz[3], dt, isBig);
          //  image.setDataFromImage(data, sz[0], sz[1], sz[2], sz[3], dt, isBig);

            if (isBig){
                Log.e(TAG, "Image4DSimple isBig");
            } else {
                Log.e(TAG, "Image4DSimple isSmall");
            }

            return image;

        } catch (OutOfMemoryError e){
            Log.e(TAG, "Out of memory when read v3draw image !");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Something wrong when read v3draw image !");
            return null;
        }
    }
    public Image4DSimple read(long length,boolean downsample, InputStream is) {

        // Read in the header values...
        try {
            FileInputStream fid = (FileInputStream)(is);
            int lenkey = formatKey.length();
            long fileSize = length;

            // read the format key
            if (fileSize<lenkey+2+4*4+1) // datatype has 2 bytes, and sz has 4*4 bytes and endian flag has 1 byte.
                throw new Exception("The size of your input file is too small and is not correct, -- it is too small to contain the legal header.");
            byte[] by = new byte[lenkey];
            long nread = fid.read(by);
            String keyread = new String(by);
            if (nread!=lenkey)
                throw new Exception("File unrecognized or corrupted file.");
            if (!keyread.equals(formatKey))
                throw new Exception("Unrecognized file format.");

            // read the endianness
            by = new byte[1];
            fid.read(by);

            if (by[0]!='B' && by[0]!='L')
                throw new Exception("This program only supports big- or little- endian but not other format. Check your endian.");

            boolean isBig = (by[0]=='B');

            // read the data type info
            by = new byte[2];
            fid.read(by);
            short deCode = (short) bytes2int(by,isBig);
            int datatype;
            switch (deCode){
                case 1:
                    datatype = 1;
                    break;
                case 2:
                    datatype = 2;
                    break;
                case 4:
                    datatype = 4;
                    break;
                default:
                    throw new Exception("Unrecognized datatype code"+deCode+". The file is incorrect or this code is not supported in this version");
            }
            int unitSize = datatype;

            // read the data size info (the data size is stored in either 2-byte or 4-byte space)
            long[] sz = new long[4];
            long totalUnit = 1;

            // first assume this is a 2-byte file
            by = new byte[2];
            for (int i=0 ; i<4; i++) {
                fid.read(by);
                sz[i] = bytes2int(by,isBig);
                totalUnit *= sz[i];
            }

            if ((totalUnit*unitSize+4*2+2+1+lenkey) != fileSize) {
                // see if this is a 4-byte file
                if (isBig)  {
                    sz[0] = sz[0]*64+sz[1];
                    sz[1] = sz[2]*64+sz[3];
                } else {
                    sz[0] = sz[1]*64+sz[0];
                    sz[1] = sz[3]*64+sz[2];
                }
                by = new byte[4];
                for (int i=2;i<4;i++) {
                    fid.read(by);
                    sz[i] = bytes2int(by,isBig);
                }
                totalUnit = 1;
                for (int i=0; i<4; i++){
                    totalUnit *= sz[i];
                }

                if ((totalUnit*unitSize+4*4+2+1+lenkey) != fileSize)
                    throw new Exception("The input file has a size different from what specified in the header. Exit.");
            }

            byte [] data = new byte[(int)(totalUnit*unitSize)];
            fid.read(data);
            int w = (int) sz[0];
            int h = (int) sz[1];
            int nChannel = (int) sz[3];

            // construct img into an array of ImageStacks, the length of array equals number of color channels.


            fid.close();
            Image4DSimple image = new Image4DSimple();
            Image4DSimple.ImagePixelType dt;
            switch (datatype){
                case 1:
                    dt = Image4DSimple.ImagePixelType.V3D_UINT8;
                    break;
                case 2:
                    dt = Image4DSimple.ImagePixelType.V3D_UINT16;
                    break;
                case 4:
                    dt = Image4DSimple.ImagePixelType.V3D_FLOAT32;
                    break;
                default:
                    dt = Image4DSimple.ImagePixelType.V3D_UNKNOWN;
            }
            if(downsample) {
                byte[] data_n = new byte[(int) (totalUnit * unitSize / 8)];
                downsampledata(data, data_n, w, h, (int) sz[2]);
                image.setDataFromImage(data_n, sz[0]/2, sz[1]/2, sz[2]/2, sz[3], dt, isBig);
            }else
            {
                image.setDataFromImage(data, sz[0], sz[1], sz[2], sz[3], dt, isBig);
            }



            if (isBig){
                Log.e(TAG, "Image4DSimple isBig");
            } else {
                Log.e(TAG, "Image4DSimple isSmall");
            }

            return image;

        } catch (OutOfMemoryError e){
            Log.e(TAG, "Out of memory when read v3draw image !");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Something wrong when read v3draw image !");
            return null;
        }
    }

    public static int bytes2int(byte[] b, boolean isBig) {
        int retVal = 0;

        if (!isBig){
            for (int i=b.length-1;i>=0;i--) {
                retVal = (retVal<<8) + (b[i] & 0xff);
            }
        } else{
            for (byte value : b) {
                retVal = (retVal << 8) + (value & 0xff);
            }
        }

        return retVal;
    }
    private void downsampledata(byte[] olddata, byte[] newdata,int w,int h,int zcount)
    {
        long num=olddata.length;
        long count=0;
        for (int z_n=0;z_n<zcount;z_n++)
        {
            if(z_n%2==1)continue;
            for (int h_n=0;h_n<h;h_n++)
            {
                if(h_n%2==1)continue;
                for (int w_n=0;w_n<w;w_n++)
                {

                    long i=z_n * w*h + h_n * w + w_n;
                    if(i%2==0)
                    {
                        newdata[(int) count]=olddata[(z_n * w*h + h_n * w + w_n)];
                        count++;
                    }

                }

            }



        }
        Log.e(TAG, "downsampledata!"+w+"  "+h+"  "+zcount);
        Log.e(TAG, "downsampledata!"+olddata.length+"  "+count);
        Log.e(TAG, "downsampledata!"+newdata.length+"  "+count);
        Log.e(TAG, "downsampledata!"+newdata[0]+"  "+newdata[1]+"  "+newdata[2]+"  "+newdata[3]);
        Log.e(TAG, "downsampledata!"+olddata[0]+"  "+olddata[1]+"  "+olddata[2]+"  "+olddata[3]+olddata[4]+"  "+olddata[5]+"  "+olddata[6]+"  "+olddata[7]);
    }

    private int grayData(byte[] grayscale,int datatype,boolean isBig,int w,int h,int zcount,int x, int y, int z){
        int result = 0;
        long[] sz = new long[4];

        sz[0]=w;
        sz[1]=h;
        sz[2]=zcount;

        if (datatype == 1){
            byte b = grayscale[(int) (z * sz[0] * sz[1] + y * sz[0] + x)];
            result = ByteTranslate.byte1ToInt(b);
        }else if (datatype == 2){
            byte [] b = new byte[2];
            b[0] = grayscale[(int) ((z * sz[0] * sz[1] + y * sz[0] + x) * 2)];
            b[1] = grayscale[(int) ((z * sz[0] * sz[1] + y * sz[0] + x) * 2 + 1)];
            result = ByteTranslate.byte2ToInt(b, isBig);
        }else if (datatype == 4){
            byte [] b = new byte[4];
            b[0] = grayscale[(int) ((z * sz[0] * sz[1] + y * sz[0] + x) * 4)];
            b[1] = grayscale[(int) ((z * sz[0] * sz[1] + y * sz[0] + x) * 4 + 1)];
            b[2] = grayscale[(int) ((z * sz[0] * sz[1] + y * sz[0] + x) * 4 + 2)];
            b[3] = grayscale[(int) ((z * sz[0] * sz[1] + y * sz[0] + x) * 4 + 3)];
            result = ByteTranslate.byte2ToInt(b, isBig);
        }
        return result;
    }


    public static int byteToInt(byte b){
        return b & 0xff;
    }

}


class ByteArray64 {

    private final long CHUNK_SIZE = 128*128*128; //1GiB

    long size;
    byte [][] data;

    public ByteArray64( long size ) {
        this.size = size;
        if( size == 0 ) {
            data = null;
        } else {
            int chunks = (int)(size/CHUNK_SIZE);
            int remainder = (int)(size - ((long)chunks)*CHUNK_SIZE);
            data = new byte[chunks+(remainder==0?0:1)][];
            for( int idx=chunks; --idx>=0; ) {
                data[idx] = new byte[(int)CHUNK_SIZE];
            }
            if( remainder != 0 ) {
                data[chunks] = new byte[remainder];
            }
        }
    }
    public byte get( long index ) {
        if( index<0 || index>=size ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+size+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        return data[chunk][offset];
    }
    public void set( long index, byte b ) {
        if( index<0 || index>=size ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+size+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        data[chunk][offset] = b;
    }
    /**
     * Simulates a single read which fills the entire array via several smaller reads.
     *
     * @param fileInputStream
     * @throws IOException
     */
    public void read( FileInputStream fileInputStream ) throws IOException {
        if( size == 0 ) {
            return;
        }
        for( int idx=0; idx<data.length; idx++ ) {
            if( fileInputStream.read( data[idx] ) != data[idx].length ) {
                throw new IOException("short read.");
            }
        }
    }
    public void write( FileOutputStream fileOutputStream ) throws IOException {
        if( size == 0 ) {
            return;
        }
        for( int idx=0; idx<data.length; idx++ )
            fileOutputStream.write( data[idx] );
    }
    public long size() {
        return size;
    }
}