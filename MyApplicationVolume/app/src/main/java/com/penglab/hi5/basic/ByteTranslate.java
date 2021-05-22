package com.penglab.hi5.basic;

public class ByteTranslate {
    public static int byte1ToInt(byte b){
        int x = b & 0xff;
        return x;
    }

    public static int byte2ToInt(byte [] b, boolean isBig){
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
    public static short byte2ToShort(byte [] b, boolean isBig){
        short retVal = 0;
        if (!isBig)
            for (int i=b.length-1;i>=0;i--) {
                retVal <<= 8;
                retVal += (b[i] & 0xff);
            }
        else
            for (int i=0;i<b.length;i++) {
                retVal <<= 8;
                retVal += (b[i] & 0xff);
            }

        return retVal;
    }

    public static byte intToByte(int i){
        byte [] result = intToByte4(i);
        return result[3];
    }

    public static byte [] intToByte4(int i){
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }
}
