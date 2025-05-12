package com.penglab.hi5.basic.image;

import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.annotation.EditMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImageMarker extends BasicSurfObj {
    private static final int DEFAULT_TYPE = 6;

    private static final int[][] typeToColorList = {
        {255,255,255},
        {20,20,20},
        {200,20,0},
        {0,20,200},
        {200,20,200},
        {0,200,200},
        {220,200,0,},
        {0,200,20},
        {250,100,120},
        {168,128,255},
        {188,94,37}
    };

    public int[][] getTypeToColorList(){
        return typeToColorList;
    }

    private static final HashMap<Integer, Integer> typeToColor = new HashMap<Integer, Integer>() {{
        put(0, R.color.white_map);
        put(1, R.color.black_map);
        put(2, R.color.red_map);
        put(3, R.color.blue_map);
        put(4, R.color.purple_map);
        put(5, R.color.cyan_map);
        put(6, R.color.yellow_map);
        put(7, R.color.green_map);
        put(8, R.color.pink_map);
        put(9, R.color.crossing_map);
        put(10, R.color.coffee_map);
    }};

    private static final HashMap<Integer, String> typeToColorStr = new HashMap<Integer, String>() {{
        put(0, "#FFFFFF");
        put(1, "#141414");
        put(2, "#C81400");
        put(3, "#0014C8");
        put(4, "#C814C8");
        put(5, "#00C8C8");
        put(6, "#DCC800");
        put(7, "#00C814");
        put(8, "#FA6478");
        put(9, "#A880FF");
        put(10, "#BC5E25");
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
        put("FFFA6478",  8);
        put("FFA880FF",  9);
        put("FFBC5E25", 10);
    }};

    public static final List<String> qcTypes = Stream.of(
            "Multifurcation",
            "Approaching bifurcation",
            "Loop",
            "Missing",
            "Crossing error",
            "Color mutation",
            "Isolated branch",
            "Angle error"
    ).collect(Collectors.toList());

    /**
     * 0-white    RGB(255, 255, 255):  undefined
     * 1-black    RGB(20, 20, 20):     soma
     * 2-red      RGB(200, 20, 0):     axon
     * 3-blue     RGB(0, 20, 200):     dendrite
     * 4-purple   RGB(200, 20, 200):   apical dendrite
     * 5-cyan     RGB(0, 200, 200):
     * 6-yellow   RGB(220, 200, 0):
     * 7-green    RGB(0, 200, 20):
     * 8-pink     RGB(250, 100, 120):
     * 9-crossing RGB(168, 128, 255):
     * 10-multifurcation RGB(188, 94, 37)
     */
    public int type;			// 0-pxUnknown, 1-pxLocaNotUseful, 2-pxLocaUseful, 3-pxLocaUnsure, 4-pxTemp

    public int shape;			// 0-pxUnset, 1-pxSphere, 2-pxCube, 3-pxCircleX, 4-pxCircleY, 5-pxCircleZ,
                                // 6-pxSquareX, 7-pxSquareY, 8-pxSquareZ, 9-pxLineX, 10-pxLineY, 11-pxLineZ,
                                // 12-pxTriangle, 13-pxDot;

    public float x, y, z;		// point coordinates

    public float xGlobal, yGlobal, zGlobal;

    public float radius;

    public String comment;

    public ImageMarker() {
        this.type = 0;
        this.shape = 0;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.xGlobal=0;
        this.yGlobal=0;
        this.zGlobal=0;
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

    public String getColorStr() {
        return typeToColorStr.getOrDefault(type, "#C81400");
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

        }else if ((r == 250 && g == 100 && b == 120)) {
            imageMarker.type = 8;

        } else if ((r == 168 && g == 128 && b == 255)) {
            imageMarker.type = 9;
        }else if ((r == 188 && g == 94 && b == 37)) {
            imageMarker.type = 10;
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

    public static int colorToType(char r, char g, char b){
        if (r == 255 && g == 255 && b == 255){
            return 0;

        }else if ((r == 0 && g == 0 && b == 0) || (r == 20 && g == 20 && b == 20)){
            return 1;

        }else if ((r == 255 && g == 0 && b == 0) || (r == 200 && g == 20 && b == 0)){
            return 2;

        }else if ((r == 0 && g == 0 && b == 255) || (r == 0 && g == 20 && b == 200)){
            return 3;

        }else if ((r == 255 && g == 0 && b == 255) || (r == 200 && g == 0 && b == 200)){
            return 4;

        }else if ((r == 0 && g == 255 && b == 255) || (r == 0 && g == 200 && b == 200)){
            return 5;

        }else if ((r == 255 && g == 255 && b == 0) || (r == 220 && g == 200 && b == 0)){
            return 6;

        }else if ((r == 0 && g == 255 && b == 0) || (r == 0 && g == 200 && b == 20)){
            return 7;

        }else if ((r == 250 && g == 100 && b == 120)) {
            return 8;

        } else if ((r == 168 && g == 128 && b == 255)) {
            return 9;
        }
        else if ((r == 188 && g == 94 && b == 37)) {
            return 10;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "ImageMarker{" +
                "type=" + type +
                ", shape=" + shape +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", xGlobal=" + xGlobal +
                ", yGlobal=" + yGlobal +
                ", zGlobal=" + zGlobal +
                ", radius=" + radius +
                ", comment='" + comment + '\'' +
                '}';
    }
}
