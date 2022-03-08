package com.penglab.hi5.core.fileReader.imageReader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.penglab.hi5.basic.image.Image4DSimple;

import org.beyka.tiffbitmapfactory.TiffBitmapFactory;

import java.io.File;

public class TiffReader {

    private final String TAG = "TiffReader";

    public Image4DSimple read(File file){
        int reqHeight = 512;
        int reqWidth  = 512;

        try {
            //Read data about image to Options object
            TiffBitmapFactory.Options options = new TiffBitmapFactory.Options();
            options.inJustDecodeBounds = true;
            TiffBitmapFactory.decodeFile(file, options);
//            options.inPreferredConfig = TiffBitmapFactory.ImageConfig.ALPHA_8;
            int dirCount = options.outDirectoryCount;
            int width_0 = options.outWidth;
            int height_0 = options.outHeight;

            int datatype = options.outSamplePerPixel;
            int numofbits = options.outBitsPerSample;
            boolean isBig = false;

            Image4DSimple image = new Image4DSimple();
            int totalUnit = dirCount * width_0 * height_0;
            int unitSize = datatype;
            int offset_xy = width_0 * height_0 * numofbits / 8;

            long[] sz = new long[4];
            sz[0] = width_0;
            sz[1] = height_0;
            sz[2] = dirCount;
            sz[3] = datatype;
            byte [] data = new byte[(totalUnit * unitSize)];

            //Read and process all images in file
            for (int i = 0; i < dirCount; i++) {
                options.inDirectoryNumber = i;
                TiffBitmapFactory.decodeFile(file, options);
                int curDir = options.outCurDirectoryNumber;
                int width = options.outWidth;
                int height = options.outHeight;

                if (width != width_0 || height != height_0){
                    System.out.println("Images of stack are not of the same dimensions!");
                    return null;
                }

                //Change sample size if width or height bigger than required width or height
                int inSampleSize = 1;
                if (height > reqHeight || width > reqWidth) {

                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                    // height and width larger than the requested height and width.
                    while ((halfHeight / inSampleSize) > reqHeight
                            && (halfWidth / inSampleSize) > reqWidth) {
                        inSampleSize *= 2;
                    }
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = inSampleSize;


                // Specify the amount of memory available for the final bitmap and temporary storage.
                options.inAvailableMemory = 20000000; // bytes

                Bitmap bmp = TiffBitmapFactory.decodeFile(file, options);

                System.out.println(bmp.getRowBytes());
                System.out.println(bmp.getConfig());

                for (int k = 0; k < width; k++){

                    for (int j = 0; j < height/2; j++){
                        int pixel;
//                        if(dirCount!=1)
//                        {
//                            pixel = bmp.getPixel(k, height/2 - ( j + 1));
//                            int red = Color.red(pixel);
//                            byte gray = (byte) (red & 0xff);
//                            data[curDir * offset_xy + j * width + k] = gray;
//                           // Log.e(TAG, "dirCount!"+dirCount);
//                        }
//                       else{
                       pixel = bmp.getPixel(k, j);
                            int red = Color.red(pixel);
                            byte gray = (byte) (red & 0xff);
                            data[curDir * offset_xy + j * width + k] = gray;
//                            //Log.e(TAG, "dirCount!"+dirCount);
//
//                       }


                    }

                    for (int j = height/2; j < height; j++){
                        int pixel;
//                        if(dirCount!=1)
//                        {
//                             pixel = bmp.getPixel(k, height - (j + 1) + height / 2);
//                            int red = Color.red(pixel);
//                            byte gray = (byte) (red & 0xff);
//                            data[curDir * offset_xy + j * width + k] = gray;
//                           // Log.e(TAG, "dirCount!"+dirCount);
//
//                        }
//                      else{
                      pixel = bmp.getPixel(k, j);

                            int red = Color.red(pixel);
                            byte gray = (byte) (red & 0xff);
                            data[curDir * offset_xy + j * width + k] = gray;
//                          //  Log.e(TAG, "dirCount!"+dirCount);
//
//                      }


                    }
                }
            }

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
            image.setDataFromImage(data,sz[0],sz[1],sz[2],sz[3],dt, isBig);
            return image;

        }catch (OutOfMemoryError e){
            Log.e(TAG, "Out of memory error when read tiff file !");
            return null;
        }catch (Exception e){
            Log.e(TAG, "Something error when read tiff file !");
            e.printStackTrace();
            return null;
        }
    }
}
