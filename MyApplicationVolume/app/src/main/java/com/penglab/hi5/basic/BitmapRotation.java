package com.penglab.hi5.basic;

import static com.penglab.hi5.core.MainActivity.getContext;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.penglab.hi5.data.model.img.FilePath;

import java.io.IOException;
import java.io.InputStream;

import jxl.write.biff.File;

public class BitmapRotation {
    private static final String TAG = "BitmapRotation";

    /**
     * 获取图片的旋转角度
     *
     * @param
     * @return 图片的旋转角度
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getBitmapDegree(InputStream is) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(is);
            // 获取图片的旋转信息

            String ori = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = Integer.valueOf(ori);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static void setOrientation(String path){
        try{
            ExifInterface exifInterface = new ExifInterface(path);
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90+"");
            exifInterface.saveAttributes();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getBitmapDegree(FilePath<?> filePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = null;

            if (filePath.getData() instanceof Uri){
                ParcelFileDescriptor parcelFileDescriptor =
                        getContext().getContentResolver().openFileDescriptor((Uri) filePath.getData(), "r");
                exifInterface = new ExifInterface(parcelFileDescriptor.getFileDescriptor());

            } else if (filePath.getData() instanceof String) {
                exifInterface = new ExifInterface((String) filePath.getData());
            }
            // 从指定路径下读取图片，并获取其EXIF信息
            if (exifInterface == null){
                Log.e(TAG, "exifInterface is null");
            }
            // 获取图片的旋转信息
            String ori = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = Integer.valueOf(ori);
            Log.e(TAG, "orientation: " + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照指定的角度进行旋转
     *
     * @param bitmap 需要旋转的图片
     * @param degree 指定的旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bitmap, int degree) {
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        if (bitmap != null && !bitmap.isRecycled()) {
//            bitmap.recycle();
//        }
        return newBitmap;
    }
}
