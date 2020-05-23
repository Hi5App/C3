package com.example.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ImageLoaderBasic {
//    public int loadRaw2StackPBD(String filename, Image4DSimple image, boolean useThreading){
//
//        File file = new File(filename);
//        fid = fopen(filename, "rb");
//        if (! fid)
//            return exitWithError(std::string("Fail to open file for reading."));
//        fseek (fid, 0, SEEK_END);
//        int fileSize = ftell(fid);
//        rewind(fid);
//        FileStarStream fileStream(fid);
//        return loadRaw2StackPBD(fileStream, fileSize, image, useThreading);
//    }

    private int loadRaw2StackPBD(InputStream is, int fileSize, boolean useThreading) {

        FileInputStream fid = (FileInputStream)(is);

        Image4DSimple image = new Image4DSimple();

        if (useThreading) {
            System.out.println("Error: attempt to use threading with ImageLoaderBasic");
        }

        int decompressionPrior = 0;
        int berror = 0;

        /* Read header */
        String formatkey = "v3d_volume_pkbitdf_encod";
        int lenkey = formatkey.length();

        if (fileSize < ( lenkey+2+4*4+1 )) // datatype has 2 bytes, and sz has 4*4 bytes and endian flag has 1 byte.
        {
            System.out.println("The size of your input file is too small and is not correct, -- it is too small to contain the legal header.\n");
            System.out.println("The fseek-ftell produces a file size = " + fileSize);
            return 0;
        }

        try {

            byte[] by = new byte[lenkey];
            long nread = fid.read(by);
            String keyread = new String(by);
            if (nread!=lenkey)
                throw new Exception("File unrecognized or corrupted file.");
            if (!keyread.equals(formatkey))
                throw new Exception("Unrecognized file format.");


            //read the endianness
            by = new byte[1];
            fid.read(by);
//            System.out.println((char)(by[0]));
            if (by[0]!='B' && by[0]!='L')
                throw new Exception("This program only supports big- or little- endian but not other format. Check your endian.");

            boolean isBig = (by[0]=='B');
            by = null;

            //read the data type info
            by = new byte[2];
            fid.read(by);
            short dcode = (short)bytes2int(by,isBig);
            System.out.println(dcode);

            int datatype;
            switch (dcode){
                case 33:            //  (PBD_3_BIT_DTYPE)
                    datatype = 33;
                    break;
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
                    throw new Exception("Unrecognized datatype code"+dcode+". The file is incorrect or this code is not supported in this version");
            }
            int unitSize = datatype;
            by = null;


            Image4DSimple.ImagePixelType dt;
            switch (datatype){
                case 33:
                case 1:
                    dt = Image4DSimple.ImagePixelType.V3D_UINT8;
                    break;
                case 2:
                    dt = Image4DSimple.ImagePixelType.V3D_UINT16;
                    break;
                default:
                    System.out.println("ImageLoader::loadRaw2StackPBD : only datatype=1 or datatype=2 supported");
            }

            //read the data size info (the data size is stored in either 2-byte or 4-byte space)
            long[] sz = new long[4];
            long[] pbd_sz = new long[4];
            long totalUnit = 1;


            //first assume this is a 2-byte file
            by = new byte[4];
            for (int i=0;i<4;i++)
            {
                fid.read(by);
                sz[i] = bytes2int(by,isBig);
                pbd_sz[i] = sz[i];

                System.out.println("sz[" + i + "]: " + sz[i]);
                totalUnit *= sz[i];
            }
            by = null;


            //first assume this is a 2-byte file
            by = new byte[2];
            for (int i=0;i<4;i++)
            {
                fid.read(by);
                sz[i] = bytes2int(by,isBig);
                totalUnit *= sz[i];
            }
            by = null;


            long headerSize = 4*4+2+1+lenkey;
            long compressedBytes = fileSize - headerSize;
            int channelLength = (int) ( sz[0]*sz[1]*sz[2] );
            long maxDecompressionSize = totalUnit * unitSize;

            byte[] data = new byte[(int) ( totalUnit * unitSize)];

            by = new byte[(int) compressedBytes];
            long pbd3_current_channel = 0;

            long remainBytes = compressedBytes;
            long readStepSizeBytes = (long) (1024*20000);
            long totalReadBytes = 0;

            


        }catch (Exception e){
            e.printStackTrace();
        }

        return berror;

    }


    public static final int bytes2int(byte[] b,boolean isBig)
    {
        int retVal = 0;
        if (!isBig)
            for (int i=b.length-1;i>=0;i--) {
                retVal = (retVal<<8) + (b[i] & 0xff);
            }
        else
            for (int i=0;i<b.length;i++) {
                retVal = (retVal<<8) + (b[i] & 0xff);
            }

        return retVal;
    }
    public static int byteToInt(byte b){
        int x = b & 0xff;
        return x;
    }


}
