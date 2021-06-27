package com.penglab.hi5.basic.tracingfunc.cornerDetection;

import android.graphics.Bitmap;

public class GrayFilter{


    private Bitmap createCompatibleDestImage(Bitmap src) {
        Bitmap.Config config = src.getConfig() != null ? src.getConfig() : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), config);
        return bitmap;

    }

    //	@Override
    public Bitmap filter(Bitmap src, Bitmap dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if ( dst == null )
            dst = createCompatibleDestImage( src);
        // ͼ��ҶȻ�
        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];

        src.getPixels(inPixels,0,width,0,0,width,height);
        int index = 0;
        for(int row=0; row<height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for(int col=0; col<width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                int gray= (int)(0.299 *tr + 0.587*tg + 0.114*tb);
                outPixels[index]  = (ta << 24) | (gray << 16) | (gray << 8) | gray;
            }
        }
        // ��˹ģ��
        dst.setPixels(outPixels,0,width,0,0,width,height);
        return dst;
    }
}
