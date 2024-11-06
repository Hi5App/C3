package com.penglab.hi5.ai_module;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

public class ImageUtils {

    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Allocation in, out;

    public ImageUtils(Context context, int width, int height) {
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(width * height * 3 / 2);
        in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
        out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
    }

    public Bitmap yuvToRgb(byte[] yuvData, int width, int height) {
        // Verify that the size matches the expected YUV420p size
        int expectedSize = width * height + (width / 2) * (height / 2) * 2;
        if (yuvData.length != expectedSize) {
            throw new IllegalArgumentException("YUV data size does not match expected YUV420p size");
        }

        // Initialize RenderScript allocation if not done
        if (in == null || in.getType().getX() != width || in.getType().getY() != height) {
            // Create input allocation with the YUV format and appropriate size
            Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs))
                    .setX(width)
                    .setY(height)
                    .setYuvFormat(android.graphics.ImageFormat.YUV_420_888);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            // Create output allocation with ARGB format
            out = Allocation.createFromBitmap(rs, Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
        }

        // Copy YUV data to RenderScript input allocation
        in.copyFrom(yuvData);

        // Set up and run the RenderScript conversion
        yuvToRgbIntrinsic.setInput(in);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        yuvToRgbIntrinsic.forEach(out);

        // Copy result to bitmap
        out.copyTo(bitmap);
        return bitmap;
    }

    public void release() {
        in.destroy();
        out.destroy();
        yuvToRgbIntrinsic.destroy();
        rs.destroy();
    }
}
