package com.penglab.hi5.core.fileReader.imageReader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.penglab.hi5.basic.image.Image4DSimple;

public class BitmapReader {
    public BitmapReader(){
    }

    public Image4DSimple read(Bitmap bitmapOrigin){
        Image4DSimple img = new Image4DSimple();

        Bitmap argbBitmap = bitmapOrigin.copy(Bitmap.Config.ARGB_8888, true);
        Image4DSimple.ImagePixelType dt = Image4DSimple.ImagePixelType.V3D_UINT8;
        boolean isBig = false;
        byte[] data = new byte[argbBitmap.getHeight() * argbBitmap.getWidth()];
        long[] sz = new long[]{argbBitmap.getWidth(), argbBitmap.getHeight(), 1, 1};

        for (int i=0; i<argbBitmap.getHeight(); i++){
            for (int j=0; j<argbBitmap.getWidth(); j++){
                int color = argbBitmap.getPixel(j, i);
//                data[i*argbBitmap.getWidth() + j] = (byte) ((int) (Color.red(color)*30 + Color.green(color)*59 + Color.blue(color)*11 + 50) / 100.0f);
                data[i*argbBitmap.getWidth() + j] = (byte) ((int) ( Color.red(color)*0.299 + Color.green(color)*0.587 + Color.blue(color)*0.114 ));
//                Log.e("BitmapReader",String.format("value: %s", data[i*argbBitmap.getWidth() + j]));
            }
        }

        Log.e("BitmapReader",String.format("x: %s, y: %s, z: %s, a: %s", sz[0],sz[1],sz[2],sz[3]));
        img.setDataFromImage(data,sz[0],sz[1],sz[2],sz[3], dt, isBig);

        for (int i=0; i<sz[0]; i++){
            for(int j=0; j<sz[1]; j++){
                if (img.getValue(i,j,0,0)>255 || img.getValue(i,j,0,0)<0)
                Log.e("Img4D","value: " + img.getValue(i,j,0,0));
            }
        }

        return img;
    }
}
