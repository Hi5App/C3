package com.penglab.hi5.basic.image;

import java.io.FileInputStream;
import java.io.InputStream;

public class ImageLoaderBasic {


    private byte[] decompressionBuffer;
    private byte[] compressionBuffer;
    private long compressionPosition;
    private long decompressionPosition;
    private long decompressionPrior;


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

    public ImageLoaderBasic(){
        decompressionBuffer = null;
        compressionBuffer = null;
        compressionPosition = 0;
        decompressionPosition = 0;
        decompressionPrior = 0;
    }

    public Image4DSimple loadRaw2StackPBD(InputStream is, long fileSize, boolean useThreading) {

        FileInputStream fid = (FileInputStream)(is);

        Image4DSimple image = new Image4DSimple();

        if (useThreading) {
            System.out.println("Error: attempt to use threading with ImageLoaderBasic");
        }

        decompressionPrior = 0;
        int berror = 0;

        /* Read header */
        String formatkey = "v3d_volume_pkbitdf_encod";
        int lenkey = formatkey.length();

        if (fileSize < ( lenkey+2+4*4+1 )) // datatype has 2 bytes, and sz has 4*4 bytes and endian flag has 1 byte.
        {
            System.out.println("The size of your input file is too small and is not correct, -- it is too small to contain the legal header.\n");
            System.out.println("The fseek-ftell produces a file size = " + fileSize);
            return null;
        }

        Image4DSimple img_result = new Image4DSimple();

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

            System.out.println("datatype: " + datatype);

            // Here is the datatype
            Image4DSimple.ImagePixelType dt = null;
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


            //first assume this is a 4-byte file
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



            long headerSize = 4*4+2+1+lenkey;
            long compressedBytes = fileSize - headerSize;
            int channelLength = (int) ( sz[0]*sz[1]*sz[2] );
            long maxDecompressionSize = totalUnit * unitSize;

            compressionBuffer = new byte[(int) compressedBytes];
            decompressionBuffer = new byte[(int) ( totalUnit * unitSize)];

            System.out.println("length of compressionBuffer:   " + compressionBuffer.length);
            System.out.println("length of decompressionBuffer: " + decompressionBuffer.length);

            by = new byte[(int) compressedBytes];
            long pbd3_current_channel = 0;

            long remainingBytes = compressedBytes;
            long readStepSizeBytes = (long) (1024*20000);
            long totalReadBytes = 0;

            while (remainingBytes > 0){

                long curReadBytes = Math.min(remainingBytes, readStepSizeBytes);
                pbd3_current_channel=totalReadBytes/channelLength;
                System.out.println("pbd3_current_channel " + pbd3_current_channel);
                long bytesToChannelBoundary=(pbd3_current_channel+1)*channelLength - totalReadBytes;
                curReadBytes = Math.min(curReadBytes, bytesToChannelBoundary);

                nread = fid.read(compressionBuffer, (int)totalReadBytes, (int)curReadBytes);
                totalReadBytes+=nread;

                System.out.println( "nread=" + nread + " curReadBytes=" + curReadBytes + " bytesToChannelBoundary=" + bytesToChannelBoundary + " totalReadBytes=" + totalReadBytes );

                if (nread != curReadBytes){
                    System.out.println("Something wrong in file reading. The program reads [" +
                                        nread + " data points] but the file says there should be [" +
                                        curReadBytes + " data points].");
                    return null;
                }

                remainingBytes -= nread;

                if (datatype == 1){
                    updateCompressionBuffer8((int) totalReadBytes);
                }else if (datatype == 33){

                }else {
                    //assume datatype == 2

                }

            }
            
            img_result.setDataFromImage(decompressionBuffer, sz[0], sz[1], sz[2], sz[3], dt, isBig);



        }catch (Exception e){
            e.printStackTrace();
        }


        return img_result;

    }


    private void updateCompressionBuffer8(int updatedCompressionBuffer){

        if (compressionPosition == 0){

        }

        long lookAhead = compressionPosition;
        System.out.println("lookAhead: " + lookAhead + ",  compressionPosition: " + compressionPosition + ", updatedCompressionBuffer: " + updatedCompressionBuffer);

        while( lookAhead < updatedCompressionBuffer){

            int lav = byteToInt(compressionBuffer[(int) lookAhead]);
            // We will keep going until we find nonsense or reach the end of the block
            if (lav < 33){
                // Literal values - the actual number of following literal values
                // is equal to the lav+1, so that if lav==32, there are 33 following
                // literal values.
                if (lookAhead+lav+1 <updatedCompressionBuffer){
                    // Then we can process the whole literal section - we can move to
                    // the next position
                    lookAhead += (lav+2);
                }else {
                    break;  // leave lookAhead in current maximum position
                }
            }else if (lav < 128){
                // Difference section. The number of difference entries is equal to lav-32, so that
                // if lav==33, the minimum, there will be 1 difference entry. For a given number of
                // difference entries, there are a factor of 4 fewer compressed entries. With the
                // equation below, lav-33 will be 4 when lav==37, which is 5 entries, requiring 2 bytes, etc.
                byte compressedDiffEntries = (byte) ( (lav-33) / 4 + 1 );
                if ( lookAhead+compressedDiffEntries < updatedCompressionBuffer ) {
                    // We can process this section, so advance to next position to evaluate
                    lookAhead += (compressedDiffEntries+1);
                } else {
                    break; // leave in current max position
                }
            }else {
                // Repeat section. Number of repeats is equal to lav-127, but the very first
                // value is the value to be repeated. The total number of compressed positions
                // is always == 2
                if ( lookAhead + 1 < updatedCompressionBuffer ) {
                    lookAhead += 2;
                } else {
                    break; // leave in current max position
                }
            }
        }
        // At this point, lookAhead is in an invalid position, which if equal to updatedCompressionBuffer
        // means the entire compressed update can be processed.
        long compressionLength = lookAhead - compressionPosition;
        if (decompressionPosition == 0) {
            // Needs to be initialized
        }

        long dlength = decompressPBD8((int) compressionPosition, (int) decompressionPosition, (int) compressionLength);
        compressionPosition = lookAhead;
        decompressionPosition += dlength;

        System.out.println("compressionPosition: " + compressionPosition + ",  decompressionPosition:" + decompressionPosition);

    }


    private int decompressPBD8(int soureDataPos, int targetDataPos, int sourceLength){

        // Decompress data
        int cp = 0;
        int dp = 0;
        byte mask = 0x0003;
        byte p0,p1,p2,p3;
        int value = 0;
        byte pva   = 0;
        byte pvb   = 0;
        int leftToFill = 0;
        int fillNumber = 0;
        int toFill = 0;
        byte sourceChar = 0;
        while( cp < sourceLength ) {

            value = byteToInt(compressionBuffer[cp + soureDataPos]);

            if (value<33) {
                // Literal 0-32
                int count = value+1;
                for (int j = cp+1; j < cp+1+count; j++) {
                    decompressionBuffer[targetDataPos + dp++] = compressionBuffer[j + soureDataPos];
                }
                cp += (count+1);
                decompressionPrior = byteToInt(decompressionBuffer[dp-1 + targetDataPos]);

            } else if (value<128) {
                // Difference 33-127
                leftToFill = value-32;
                while(leftToFill > 0) {
                    fillNumber = (Math.min(leftToFill, 4));
                    sourceChar = compressionBuffer[++cp + soureDataPos];
                    toFill = targetDataPos + dp;
                    p0 = (byte) (sourceChar & mask);
                    sourceChar >>= 2;
                    p1 = (byte) (sourceChar & mask);
                    sourceChar >>= 2;
                    p2 = (byte) (sourceChar & mask);
                    sourceChar >>= 2;
                    p3 = (byte) (sourceChar & mask);
                    pva = (byte) ((p0==3?-1:p0) + (byte) decompressionPrior);

                    decompressionBuffer[toFill] = pva;
                    if (fillNumber>1) {
                        toFill++;
                        pvb = (byte) (pva + (p1==3?-1:p1));
                        decompressionBuffer[toFill] = pvb;
                        if (fillNumber>2) {
                            toFill++;
                            pva= (byte) ((p2==3?-1:p2)+pvb);
                            decompressionBuffer[toFill] = pva;
                            if (fillNumber>3) {
                                toFill++;
                                decompressionBuffer[toFill] = (byte) ((p3==3?-1:p3)+pva);
                            }
                        }
                    }

                    decompressionPrior = decompressionBuffer[toFill];
                    dp += fillNumber;
                    leftToFill -= fillNumber;
                }
                cp++;
            } else {
                // Repeat 128-255
                int repeatCount = value-127;
                byte repeatValue = compressionBuffer[++cp + soureDataPos];

                for (int j=0; j < repeatCount; j++) {
                    decompressionBuffer[targetDataPos + dp++] = repeatValue;
                }
                decompressionPrior = byteToInt(repeatValue);
                cp++;
            }

        }
        return dp;
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
