package com.penglab.hi5.basic.image;

import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.annotation.EditMode;

import java.util.ArrayList;
import java.util.HashMap;

public class ImageMarker extends BasicSurfObj {
    private static final int DEFAULT_TYPE = 3;

    private static final HashMap<Integer, Integer> typeToColor = new HashMap<Integer, Integer>() {{
        put(0, R.color.white_map);
        put(1, R.color.black_map);
        put(2, R.color.red_map);
        put(3, R.color.blue_map);
        put(4, R.color.purple_map);
        put(5, R.color.cyan_map);
        put(6, R.color.yellow_map);
        put(7, R.color.green_map);
    }};

    private static final HashMap<String, Integer> colorToType = new HashMap<String, Integer>() {{
        put("FFFFFFFF",  0);
        put("FF141414",  1);
        put("FFC81400",  2);
        put("FF0014C8",  3);
        put("FFC814C8",  4);
        put("FF00C8C8",  5);
        put("FFDCC800",  6);
        put("FF00C814",  7);
    }};

    /**
     * 0-white    RGB(255, 255, 255):  undefined
     * 1-black    RGB(20, 20, 20):     soma
     * 2-red      RGB(200, 20, 0):     axon
     * 3-blue     RGB(0, 20, 200):     dendrite
     * 4-purple   RGB(200, 20, 200):   apical dendrite
     * 5-cyan     RGB(0, 200, 200):
     * 6-yellow   RGB(220, 200, 0):
     * 7-green    RGB(0, 200, 20):
     */
    public int type;			// 0-pxUnknown, 1-pxLocaNotUseful, 2-pxLocaUseful, 3-pxLocaUnsure, 4-pxTemp

    public int shape;			// 0-pxUnset, 1-pxSphere, 2-pxCube, 3-pxCircleX, 4-pxCircleY, 5-pxCircleZ,
                                // 6-pxSquareX, 7-pxSquareY, 8-pxSquareZ, 9-pxLineX, 10-pxLineY, 11-pxLineZ,
                                // 12-pxTriangle, 13-pxDot;

    public float x, y, z;		// point coordinates

    public float radius;

    public ImageMarker() {
        this.type = 0;
        this.shape = 0;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.radius = 0;
    }

    public ImageMarker(XYZ pos) {
        this.type = 0;
        this.shape = 0;
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.radius = 0;
    }

    public ImageMarker(int type, XYZ pos) {
        this.type = type;
        this.shape = 0;
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.radius = 0;
    }

    public ImageMarker(float x, float y, float z) {
        this.type = 0;
        this.shape = 0;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = 0;
    }

    public ImageMarker(int type, float x, float y, float z) {
        this.type = type;
        this.shape = 0;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = 0;
    }

    public ImageMarker(int type, int shape, float x, float y, float z, float radius) {
        this.type = type;
        this.shape = shape;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    public XYZ getXYZ(){
        return new XYZ(x, y, z);
    }

    public static ImageMarker parse(String imageMarkerString) throws Exception {
        String[] imageMarkerStringArray = imageMarkerString.split(",");
        ArrayList<Float> imageMarkerFloatList = new ArrayList<Float>();

        for (String curNum: imageMarkerStringArray){
            curNum = curNum.trim();
            if (curNum.equals("")){
                curNum = "0";
            }
            imageMarkerFloatList.add(Float.parseFloat(curNum));
        }
        return parse(imageMarkerFloatList);
    }

    public static ImageMarker parse(ArrayList<Float> imageMarkerFloatList) throws Exception {
        if (imageMarkerFloatList.size() < 18){
            throw new Exception("Illegal float list for ImageMarker");
        }

        ImageMarker imageMarker = new ImageMarker(imageMarkerFloatList.get(5),
                imageMarkerFloatList.get(6),
                imageMarkerFloatList.get(4));

        int r = imageMarkerFloatList.get(15).intValue();
        int g = imageMarkerFloatList.get(16).intValue();
        int b = imageMarkerFloatList.get(17).intValue();

        if (r == 255 && g == 255 && b == 255){
            imageMarker.type = 0;

        }else if ((r == 0 && g == 0 && b == 0) || (r == 20 && g == 20 && b == 20)){
            imageMarker.type = 1;

        }else if ((r == 255 && g == 0 && b == 0) || (r == 200 && g == 20 && b == 0)){
            imageMarker.type = 2;

        }else if ((r == 0 && g == 0 && b == 255) || (r == 0 && g == 20 && b == 200)){
            imageMarker.type = 3;

        }else if ((r == 255 && g == 0 && b == 255) || (r == 200 && g == 0 && b == 200)){
            imageMarker.type = 4;

        }else if ((r == 0 && g == 255 && b == 255) || (r == 0 && g == 200 && b == 200)){
            imageMarker.type = 5;

        }else if ((r == 255 && g == 255 && b == 0) || (r == 220 && g == 200 && b == 0)){
            imageMarker.type = 6;

        }else if ((r == 0 && g == 255 && b == 0) || (r == 0 && g == 200 && b == 20)){
            imageMarker.type = 7;

        }
        return imageMarker;
    }

    public static int typeToColor(int type){
        if (!typeToColor.containsKey(type)){
            return typeToColor.get(DEFAULT_TYPE);
        } else {
            return typeToColor.get(type);
        }
    }

    public static int colorToType(String color){
        if (!colorToType.containsKey(color)){
            return DEFAULT_TYPE;
        } else {
            return colorToType.get(color);
        }
    }
}
