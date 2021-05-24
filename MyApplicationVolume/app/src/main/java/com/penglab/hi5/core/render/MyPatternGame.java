package com.penglab.hi5.core.render;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.penglab.hi5.basic.ByteTranslate;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.game.GameMapPoint;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static com.penglab.hi5.core.render.ShaderHelper.initShaderProgram;
import static com.penglab.hi5.game.GameMapPoint.distance;

public class MyPatternGame {

    private static String TAG = "MyPatternGame";

    private Image4DSimple image;

    private float [] dim;
    private int [] sz;

    private int vol_w;
    private int vol_h;
    private int vol_d;

    private int nchannel;

    private int data_length;
    private boolean isBig;

    private ArrayList<GameMapPoint> lightPoints = new ArrayList<>();
    private ArrayList<GameMapPoint> removedPoints = new ArrayList<>();

    private float[] gamePos;

    private int threshold = 0;

    private static int positionHandle = 0;
    private int colorHandle = 1;
    private int mvpMatrixHandle;

    private static int mProgramGame;
    private static int mProgramMarker;
    private static int mProgramBackground;
    private static int mProgramFlag;
    private static int mProgramNumbers;

    private FloatBuffer vertexBuffer_flag;

    private FloatBuffer vertexBuffer;

    private float [] vertexPoints;

    private float [] markerPoints;
    private float [] markerPoints_half;
    private float [] markerPoints_quarter;
    private float [] markerPoints_norm;
    private float [] flagPoints;

    private float defaultRadius = 0.002f;

    private float [] markerVertexPoints;
    private float [] colorPoints_marker;

    private FloatBuffer vertexBuffer_marker;
    private FloatBuffer colorBuffer_marker;

    private static int vertexPoints_handle = 0;
    private static int normalizePoints_handle = 1;
    private static int colorPoints_handle = 2;

    private static int coordPoints_handle = 1;

    public final static float[][] colormap = {
            {0f,0f,0f},
            {1f,1f,1f},
            {1f,0f,0f},
            {0f,0f,1f},
            {0f,1f,0f},
            {1f,0f,1f},
            {1f,1f,0f}
    } ;

    private float [] normalizePoints_marker_small;
    private FloatBuffer normalizeBuffer_marker_small;

    private Timer timer;
    private TimerTask task;

    private Bitmap mBitmap;
    private int [] backgroundTexture = new int[1];

    private float [] bgPosition;
    private FloatBuffer bgBuffer;

    private float [] bgCoord;
    private FloatBuffer bgCoordBuffer;

    private final float[] sCoord={
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f,
    };
    private FloatBuffer bCoord;

    private float bgWidth;
    private float bgHeight;

    private float[] bgMatrix = new float[16];

    private float bgRadius = 1.0f;

    private Bitmap [] numbersBitmap = new Bitmap[10];
    private int [][] numbersTexture = new int[10][1];

    private float [] coordNumbers = new float[6];

    private FloatBuffer vertexBuffer_numbers;
    private FloatBuffer coordBuffer_numbers;

    private static final String vertexShaderCode_game =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "#version 300 es\n" +
                    "precision highp float;" +
                    "uniform mat4 uMVPMatrix;" +
                    "layout (location = 1) in vec4 a_position;" +

                    "out vec4 pos;" +

                    "void main() {" +
                    "  pos = uMVPMatrix * a_position;"+
                    "  gl_Position = pos;" +
                    "}";



    private static final String fragmentShaderCode_game =
            "#version 300 es\n" +
                    "precision highp float;" +

                    "in vec4 pos;" +

                    "uniform sampler2D uBackCoord;" +
                    "uniform highp sampler3D uVolData;" +
                    "uniform highp float dim[3];" +
                    "uniform highp float contrast;" +
                    "uniform highp float threshold;" +
                    "layout (location = 0) out vec4 fragColor;" +

                    "void main(void)" +
                    "{" +
                    "  vec2 texC = pos.xy/pos.w;" +
                    "  texC.x = 0.5*texC.x + 0.5;" +
                    "  texC.y = 0.5*texC.y + 0.5;" +
                    "  " +
                    "  vec4 backColor = texture(uBackCoord, texC);" +
                    "  " +
                    "  vec3 dir = backColor.rgb - frontColor.rgb;" +
                    "  float steps = 512.0;" +
                    "  vec4 vpos = frontColor;" +
                    "  " +
                    "  float cont = 0.0;" +
                    "  " +
                    "  vec3 Step = dir/steps;" +
                    "  " +
                    "  vec4 accum = vec4(0, 0, 0, 0);" +
//                    "  vec4 sample = vec4(0.0, 0.0, 0.0, 0.0);" +
                    "  vec4 value = vec4(0, 0, 0, 0);" +
                    "  " +
                    "  float opacityFactor = 8.0;" +
                    "  float lightFactor = 1.3;" +
                    "  " +
                    "  for(float i = 0.0; i < steps; i+=1.0)" +
                    "  {" +

                    "     vec4 tf_value;" +
//                    "     tf_value = texture(uVolData, vpos.xyz);" +
                    "     tf_value = texture(uVolData, vec3(1.0 - vpos.x/dim[0], 1.0 - vpos.y/dim[1], vpos.z/dim[2]));" +
//                    "     if (vpos" +
//                    "     tf_value = texture(uVolData, vec3(1.0 - vpos.x, 1.0 - vpos.y, vpos.z));" +
//                    "     tf_value = texture(uVolData, vec3(vpos.x, 1.0 - vpos.y, vpos.z));" +
//                    "     value = vec4(tf_value.x);" +
                    "     value = vec4(tf_value.x * contrast, tf_value.y * contrast, tf_value.z * contrast, tf_value.x);" +

                    "     if(value.r > accum.r)\n" +
                    "         accum.r = value.r;\n" +
                    "     if(value.g > accum.g)\n" +
                    "         accum.g = value.g\n;" +
                    "     if(value.b > accum.b)\n" +
                    "         accum.b = value.b\n;" +
                    "     accum.a += (1.0 - accum.a) * value.a;" +
                    "     vpos.xyz += Step;" +

                    "     if(vpos.x > 1.0 || vpos.y > 1.0 || vpos.z > 1.0 || accum.a>=1.0)" +
                    "         break;" +
                    "  }" +
                    "  accum.a = 1.0;" +
//                    "     float old_contrast = contrast;" +
                    "  if(threshold != 0.0){\n" +
//                    "         accum.r /= old_contrast;" +
//                    "         accum.g /= old_contrast;" +
//                    "         accum.b /= old_contrast;" +

                    "      if (accum.r > 2.0*threshold){\n" +
                    "          accum.r *= contrast;" +
                    "          accum.g *= contrast;" +
                    "          accum.b *= contrast;" +
//                    "             accum.r = 1.0;" +
//                    "             accum.g = 1.0;" +
//                    "             accum.b = 1.0;" +
                    "      }else{\n" +
                    "          accum.r /= contrast;" +
                    "          accum.g /= contrast;" +
                    "          accum.b /= contrast;" +
//                    "             accum.r = 0.0;" +
//                    "             accum.g = 0.0;" +
//                    "             accum.b = 0.0;" +
                    "      }\n" +


                    "  }\n" +
//                    "  float threshold = (float)(myrenderer.threshold) / 255;" +
//                    "  float r,g,b;" +
//
//                    "  r = accum.r;" +
//                    "  g = accum.g;" +
//                    "  b = accum.b;" +
//                    "  float gray = r*0.3+g*0.59+b*0.11;" +
//                    "  if(gray < threshold){" +
//                    "    gray = 0.0;" +
//                    "  }else{" +
//                    "    gray = 1.0;}" +
////                    "  newpixel = accum.a | (gray << 16) | (gray << 8) | gray" +
////                    "  fragColor = newpixel;" +
//                    "  accum.r = gray;" +
//                    "  accum.g = gray;" +
//                    "  accum.b = gray;" +
                    "  fragColor = accum;" +
                    "}";

    private static final String vertexShaderCode_points =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +

                    "uniform mat4 uMVPMatrix;" +

                    "void main() {\n" +
                    "     gl_Position  = uMVPMatrix * vPosition;\n" + //
                    "     gl_PointSize = 20.0;\n" +
                    "}\n";


    private static final String fragmentShaderCode_points =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
                    "if (length(gl_PointCoord  - vec2(0.5)) > 0.5) {\n" +
                    "        discard;\n" +
                    "    }\n" +
                    "     fragColor = vec4(1.0,1.0,1.0,1.0);\n" +
                    "}\n";

    private static final String vertexShaderCode_marker =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;" +
                    "layout (location = 1) in vec3 aVertexNormal;" +
                    "layout (location = 2) in vec4 vColor;"+

                    "uniform mat4 uNormalMatrix;" +
                    "uniform mat4 uMVPMatrix;" +

                    "out vec3 vLighting;" +
                    "out vec4 vOutColor;" +

                    "void main() {" +
                    "    gl_Position = vPosition;" +

                    "    vec3 uAmbientColor = vec3(0.2, 0.2, 0.2);\n" +
                    "    vec3 uDirectionalColor = vec3(0.8, 0.8, 0.8);\n" +

                    "    vec3 directionalVector = normalize(vec3(-1.0, 1.0, -1.0));\n" +
                    "    vec3 transformedNormal = (uNormalMatrix * vec4(normalize(aVertexNormal), 1.0)).xyz;\n" +
                    "    float directionalLightWeighting = max(dot(transformedNormal, directionalVector), 0.0);\n" +
                    "    vLighting = uAmbientColor + uDirectionalColor * directionalLightWeighting;" +
                    "    vOutColor = vColor;"+
                    "}";



    private static final String fragmentShaderCode_marker =
            "#version 300 es\n" +
                    "precision mediump float;" +

                    "in vec4 vOutColor;" +
                    "in vec3 vLighting;" +
                    "out vec4 FragColor;" +

                    "void main() {" +
//                    "  vec4 markerColor = vec4(0.29, 0.13, 0.36, 1.0);" +
//                    "  vec4 markerColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                    "  vec4 markerColor = vOutColor;"+
//                            "FragColor = vOutColor;"+
                    "  FragColor = vec4(markerColor.rgb * vLighting, 1.0);" +
                    "}";

    private static final String vertexShaderCode_background =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;" +
                    "layout (location = 1) in vec2 vCoordinate;" +
                    "uniform mat4 vMatrix;" +

                    "out vec2 aCoordinate;" +

                    "void main() {" +
                    "  gl_Position = vMatrix * vPosition;" +
                    "  aCoordinate = vCoordinate;" +
                    "}";

    private static final String fragmentShaderCode_background =
            "#version 300 es\n" +
                    "precision mediump float;" +

                    "uniform sampler2D vTexture;" +
                    "in vec2 aCoordinate;" +
                    "out vec4 fragColor;" +

                    "void main() {" +
                    "   fragColor = texture(vTexture, aCoordinate);" +
                    "}";

    private static final String vertexShaderCode_flag =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +

                    "void main() {\n" +
                    "     gl_Position  = vPosition;\n" +
                    "}\n";


    private static final String fragmentShaderCode_flag =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
                    "     fragColor = vec4(1.0,1.0,1.0,1.0);\n" +
                    "}\n";

    private static final String vertexShaderCode_numbers =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;" +
                    "layout (location = 1) in vec2 vCoordinate;" +


                    "out vec2 aCoordinate;" +

                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "  aCoordinate = vCoordinate;" +
                    "}";

    private static final String fragmentShaderCode_numbers =
            "#version 300 es\n" +
                    "precision mediump float;" +

                    "uniform sampler2D vTexture;" +
                    "in vec2 aCoordinate;" +
                    "out vec4 fragColor;" +

                    "void main() {" +
                    "   fragColor = texture(vTexture, aCoordinate);" +
                    "}";

    public MyPatternGame(int width, int height, Image4DSimple img, float [] mz, int [] sz){
        image = img;
        dim = mz;
        this.sz = sz;

        initMap();

        initFontBitmap();

        setPanoramicPosition();

//        markerPoints = createPositions(0, 0, 0, defaultRadius);
//        markerPoints_half = createPositions(0, 0, 0, defaultRadius / 2);
//        markerPoints_quarter = createPositions(0, 0, 0, defaultRadius / 4);


        markerPoints_norm = new float[]{
                1, 0, 0,
                0, 1, 0,
                0, 0, 1,

                1, 0, 0,
                0, -1, 0,
                0, 0, 1,

                -1, 0, 0,
                0, 1, 0,
                0, 0, 1,

                -1, 0, 0,
                0, -1, 0,
                0, 0, 1,

                1, 0, 0,
                0, 1, 0,
                0, 0, -1,

                1, 0, 0,
                0, -1, 0,
                0, 0, -1,

                -1, 0, 0,
                0, 1, 0,
                0, 0, -1,

                -1, 0, 0,
                0, -1, 0,
                0, 0, -1,
        };

        markerPoints = new float[]{
                defaultRadius, 0, 0,
                0, defaultRadius, 0,
                0, 0, defaultRadius,

                defaultRadius, 0, 0,
                0, -defaultRadius, 0,
                0, 0, defaultRadius,

                -defaultRadius, 0, 0,
                0, defaultRadius, 0,
                0, 0, defaultRadius,

                -defaultRadius, 0, 0,
                0, -defaultRadius, 0,
                0, 0, defaultRadius,

                defaultRadius, 0, 0,
                0, defaultRadius, 0,
                0, 0, -defaultRadius,

                defaultRadius, 0, 0,
                0, -defaultRadius, 0,
                0, 0, -defaultRadius,

                -defaultRadius, 0, 0,
                0, defaultRadius, 0,
                0, 0, -defaultRadius,

                -defaultRadius, 0, 0,
                0, -defaultRadius, 0,
                0, 0, -defaultRadius,
        };

        markerPoints_half = new float[]{
                defaultRadius/2, 0, 0,
                0, defaultRadius/2, 0,
                0, 0, defaultRadius/2,

                defaultRadius/2, 0, 0,
                0, -defaultRadius/2, 0,
                0, 0, defaultRadius/2,

                -defaultRadius/2, 0, 0,
                0, defaultRadius/2, 0,
                0, 0, defaultRadius/2,

                -defaultRadius/2, 0, 0,
                0, -defaultRadius/2, 0,
                0, 0, defaultRadius/2,

                defaultRadius/2, 0, 0,
                0, defaultRadius/2, 0,
                0, 0, -defaultRadius/2,

                defaultRadius/2, 0, 0,
                0, -defaultRadius/2, 0,
                0, 0, -defaultRadius/2,

                -defaultRadius/2, 0, 0,
                0, defaultRadius/2, 0,
                0, 0, -defaultRadius/2,

                -defaultRadius/2, 0, 0,
                0, -defaultRadius/2, 0,
                0, 0, -defaultRadius/2,
        };

        markerPoints_quarter = new float[]{
                defaultRadius/4, 0, 0,
                0, defaultRadius/4, 0,
                0, 0, defaultRadius/4,

                defaultRadius/4, 0, 0,
                0, -defaultRadius/4, 0,
                0, 0, defaultRadius/4,

                -defaultRadius/4, 0, 0,
                0, defaultRadius/4, 0,
                0, 0, defaultRadius/4,

                -defaultRadius/4, 0, 0,
                0, -defaultRadius/4, 0,
                0, 0, defaultRadius/4,

                defaultRadius/4, 0, 0,
                0, defaultRadius/4, 0,
                0, 0, -defaultRadius/4,

                defaultRadius/4, 0, 0,
                0, -defaultRadius/4, 0,
                0, 0, -defaultRadius/4,

                -defaultRadius/4, 0, 0,
                0, defaultRadius/4, 0,
                0, 0, -defaultRadius/4,

                -defaultRadius/4, 0, 0,
                0, -defaultRadius/4, 0,
                0, 0, -defaultRadius/4,
        };

        flagPoints = new float[]{
                -0.005f, 0,     0,
                0.005f,  0,     0,
                -0.005f, 0.05f, 0,

                0.005f,  0,     0,
                0.005f,  0.05f, 0,
                -0.005f, 0.05f, 0,

                -0.005f, 0.05f, 0,
                -0.005f, 0.2f,  0,
                0.15f,  0.05f, 0,

                -0.005f, 0.2f,  0,
                0.15f,  0.05f, 0,
                0.15f,  0.2f,  0,
        };

        coordNumbers = new float[]{
                0, 1,
                0, 0,
                1, 1,

                0, 0,
                1, 1,
                1, 0,
        };

        gamePos = new float[3];

//        timer = new Timer();
//        task = new TimerTask() {
//            @Override
//            public void run() {
//                if (gamePos == null){
//                    return;
//                }
//
//                if (removedPoints.size() == 0){
//                    return;
//                }
//
//                for (int i = 0; i < removedPoints.size(); i++){
//                    GameMapPoint temp = removedPoints.get(i);
//                    temp.reduceRadius(0.5f);
//                    GameMapPoint centerPos = new GameMapPoint(gamePos[0], gamePos[1], gamePos[2]);
//                    temp.moveTo(centerPos, 0.5f);
//                    if (distance(temp, centerPos) < 0.01) {
//                        removedPoints.remove(i);
//                    }
//                }
//            }
//        };
    }

    public static void initProgram(){
        mProgramGame = initShaderProgram(TAG, vertexShaderCode_points, fragmentShaderCode_points);
        mProgramMarker = initShaderProgram(TAG, vertexShaderCode_marker, fragmentShaderCode_marker);
        mProgramBackground = initShaderProgram(TAG, vertexShaderCode_background, fragmentShaderCode_background);
        mProgramFlag = initShaderProgram(TAG, vertexShaderCode_flag, fragmentShaderCode_flag);
        mProgramNumbers = initShaderProgram(TAG, vertexShaderCode_numbers, fragmentShaderCode_numbers);
    }



    private void initMap(){
        Log.d(TAG, "initMap");
        vol_w = (int)image.getSz0();
        vol_h = (int)image.getSz1();
        vol_d = (int)image.getSz2();

        nchannel = (int)image.getSz3();

        byte [] dataSrc = image.getData();

        data_length = image.getDatatype().ordinal();
        isBig = image.getIsBig();

        resetLightPoints(dataSrc);

//        bufferSet();
    }

    private void resetLightPoints(byte [] dataSrc){
        Log.v(TAG, "resetLightPoints");
        lightPoints.clear();

        if (data_length == 1){
            byte [] grayscale = getIntensity_3d(dataSrc);

            for (int x = 0; x < vol_w; x += 3){
                for (int y = 0; y < vol_h; y += 3){
                    for (int z = 0; z < vol_d; z += 3){
                        if (ByteTranslate.byte1ToInt(grayscale[(z * vol_h * vol_w + y * vol_w + x) * 4]) > threshold){
                            lightPoints.add(new GameMapPoint(x, y, z));

                        }
                    }
                }
            }
        } else if (data_length == 2){
            short [] grayscale = getIntensity_short3d(dataSrc);

            for (int x = 0; x < vol_w; x += 3){
                for (int y = 0; y < vol_h; y += 3){
                    for (int z = 0; z < vol_d; z += 3){
                        if (grayscale[(z * vol_w * vol_h + y * vol_w + x) * 4] > threshold){
                            lightPoints.add(new GameMapPoint(z, y, x));

                        }
                    }
                }
            }
        } else if (data_length == 4){
            int [] grayscale = getIntensity_int3d(dataSrc);

            for (int x = 0; x < vol_w; x += 3){
                for (int y = 0; y < vol_h; y += 3){
                    for (int z = 0; z < vol_d; z += 3){
                        if (grayscale[(z * vol_w * vol_h + y * vol_w + x) * 4] > threshold){
                            lightPoints.add(new GameMapPoint(z, y, x));

                        }
                    }
                }
            }
        }

        Log.v(TAG, "resetLightPoints: " + Integer.toString(lightPoints.size()));

        for (int i = 0; i < 5; i++){
            Log.d(TAG, lightPoints.get(i).x + " " + lightPoints.get(i).y + " " + lightPoints.get(i).z);
        }
    }

    private byte[] getIntensity_3d(byte []  data_src){

        Log.v("getIntensity_3d:", "here we are!!!!!!!");

        Log.v("vol_w", Integer.toString(vol_w));
        Log.v("vol_h", Integer.toString(vol_h));
        Log.v("vol_d", Integer.toString(vol_d));

        byte [] data_image = new byte[vol_w * vol_h * vol_d * data_length * nchannel * 4];
        if (nchannel == 3){
            for (int i = 0; i < vol_w * vol_h * vol_d * data_length; i++){
                data_image[i * 4] = data_src[i];
                data_image[i * 4 + 1] = data_src[vol_w * vol_h * vol_d * data_length + i];
                data_image[i * 4 + 2] = data_src[vol_w * vol_h * vol_d * data_length * 2 + i];
                data_image[i * 4 + 3] = ByteTranslate.intToByte(1);
            }
        }else{
            for (int i = 0; i < vol_w * vol_h * vol_d * data_length; i++){
                data_image[i * 4] = data_src[i];
                data_image[i * 4 + 1] = data_src[i];
                data_image[i * 4 + 2] = data_src[i];
                data_image[i * 4 + 3] = ByteTranslate.intToByte(1);
            }
        }

        int[] data_gray = new int[(data_image.length + 1) / 4];

        int j = 0;
        for (int i = 0; i < data_image.length; i += 4) {
            data_gray[j] = ByteTranslate.byte1ToInt(data_image[i]);//(int)((float)data_image[i] * 0.3 + (float)data_image[i+1] * 0.59 + (float)data_image[i+2] * 0.11);
            j++;
        }

//        下面是迭代法求二值化阈值
//        求出最大灰度值和最小灰度值

        float Gmax = data_gray[0], Gmin = data_gray[0];
        for (int i = 0; i < data_gray.length; i++) {
            if (data_gray[i] > Gmax)
                Gmax = data_gray[i];
            if (data_gray[i] < Gmin)
                Gmin = data_gray[i];
        }

        int[] histogram = new int[256];
        for (int t = (int) Gmin; t <= Gmax; t++) {
            for (int index = 0; index < data_gray.length; index++)
                if (data_gray[index] == t) {
//                Log.d("t",String.valueOf(t));
                    histogram[t]++;
                }
        }
        // 迭代法求最佳分割阈值
        int T = 0;
        int newT = (int) ((Gmax + Gmin) / 2); //初始的阈值
        // 求背景（黑色的）和前景（前面白色的神经元信号）的平均灰度值bp和fp
        while (T != newT) {
            int sum1 = 0, sum2 = 0, count1 = 0, count2 = 0;
            int fp, bp;
            for (int ii = (int) Gmin; ii < newT; ii++) {
                count1 += histogram[ii]; //背景像素点的个数
                sum1 += histogram[ii] * ii; //背景像素的的灰度总值 i为灰度值，histogram[i]为对应的个数
            }
            bp = (count1 == 0) ? 0 : (sum1 / count1); //背景像素点的平均灰度值

            for (int jj = newT; jj < Gmax; jj++) {
                count2 += histogram[jj]; //前景像素点的个数
                sum2 += histogram[jj] * jj; //前景像素的的灰度总值 i为灰度值，histogram[i]为对应的个数
            }
            fp = (count2 == 0) ? 0 : (sum2 / count2); //前景像素点的平均灰度值
            T = newT;
            newT = (bp + fp) / 2;
        }
        threshold = newT; //最佳阈值
        Log.d("threshold",String.valueOf(threshold));


        if (threshold >= 35 & threshold <= 45)
            threshold += 2;
        else if (threshold < 35) //防止threshold太小了。
            threshold = 35;


        Log.d("newthreshold",String.valueOf(threshold));

        return data_image;
    }

    private short [] getIntensity_short3d(byte [] data_src){

        Log.v("getIntensity_short3d", "Here we are");

        short [] data_image = new short[vol_w * vol_h * vol_d * nchannel * 4];
        byte [] b= new byte[2];
        if (nchannel == 3){
            for (int i = 0; i < vol_w * vol_h * vol_d; i++){
                for (int c = 0; c < nchannel; c++) {
                    b[0] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 2];
                    b[1] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 2 + 1];
                    data_image[i * 4 + c] = ByteTranslate.byte2ToShort(b, isBig);
                }
                data_image[i * 4 + 3] = 1;
            }
        }else{
            for (int i = 0; i < vol_w * vol_h * vol_d; i++){
                b[0] = data_src[i * 2];
                b[1] = data_src[i * 2 + 1];
                short temp = ByteTranslate.byte2ToShort(b, isBig);
                data_image[i * 4] = temp;
                data_image[i * 4 + 1] = temp;
                data_image[i * 4 + 2] = temp;
                data_image[i * 4 + 3] = 1;
            }
        }
        return data_image;
    }

    private int [] getIntensity_int3d(byte [] data_src){

        Log.v("getIntensity_int3d", "Here we are");

        int [] data_image = new int[vol_w * vol_h * vol_d * nchannel * 4];
        byte [] b= new byte[4];
        if (nchannel == 3){
            for (int i = 0; i < vol_w * vol_h * vol_d; i++){
                for (int c = 0; c < nchannel; c++) {
                    b[0] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4];
                    b[1] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4 + 1];
                    b[2] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4 + 2];
                    b[3] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4 + 3];
                    data_image[i * 4 + c] = ByteTranslate.byte2ToInt(b, isBig);

                }
                data_image[i * 4 + 3] = 1;
            }
        }else{
            for (int i = 0; i < vol_w * vol_h * vol_d; i++){
                b[0] = data_src[i * 4];
                b[1] = data_src[i * 4 + 1];
                b[2] = data_src[i * 4 + 2];
                b[3] = data_src[i * 4 + 3];
                int temp = ByteTranslate.byte2ToInt(b, isBig);
                data_image[i * 4] = temp;
                data_image[i * 4 + 1] = temp;
                data_image[i * 4 + 2] = temp;
                data_image[i * 4 + 3] = 1;
            }
        }
        return data_image;
    }

    public void drawMap(float [] matrix){
        Log.v(TAG, "drawMap");
        GLES30.glUseProgram(mProgramGame);

        getHandle(mProgramGame);

        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, matrix, 0);

        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, vertexPoints.length/3);

        GLES30.glDisable(positionHandle);
        GLES30.glDisable(mvpMatrixHandle);
    }

    public void drawMarker(float [] mvpMatrix, float [] modelMatrix){

        updateRemovedPoints();

        bufferSet_Type(6);

//        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glUseProgram(mProgramMarker);

        GLES30.glVertexAttribPointer(normalizePoints_handle, 3, GLES30.GL_FLOAT, false, 0, normalizeBuffer_marker_small);

        //准备f法向量数据
        // GLES30.glVertexAttribPointer(normalizePoints_handle, 3, GLES30.GL_FLOAT, false, 0, normalizeBuffer_marker);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(normalizePoints_handle);



        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgramMarker,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);

        // get handle to vertex shader's uNormalMatrix member
        int normalizeMatrixHandle_marker = GLES30.glGetUniformLocation(mProgramMarker,"uNormalMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(normalizeMatrixHandle_marker, 1, false, modelMatrix, 0);

//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker.length/3);

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        for (int i = 0; i < lightPoints.size(); i++){
            bufferSet_Marker(lightPoints.get(i).x, lightPoints.get(i).y, lightPoints.get(i).z, lightPoints.get(i).radius, mvpMatrix, lightPoints.get(i).type, lightPoints.get(i).proportion);

            //准备坐标数据
            GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker);
            //启用顶点的句柄
            GLES30.glEnableVertexAttribArray(vertexPoints_handle);

            //准备颜色数据
            GLES30.glVertexAttribPointer(colorPoints_handle,3,GLES30.GL_FLOAT, false, 0,colorBuffer_marker);
            GLES30.glEnableVertexAttribArray(colorPoints_handle);

            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, markerPoints.length/3);
        }

        Log.d("DrawMarker", "RemovedPoints: " + Integer.toString(removedPoints.size()));

        for (int i = 0; i < removedPoints.size(); i++){
            bufferSet_Marker(removedPoints.get(i).x, removedPoints.get(i).y, removedPoints.get(i).z, removedPoints.get(i).radius, mvpMatrix, removedPoints.get(i).type, removedPoints.get(i).proportion);

            //准备坐标数据
            GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker);
            //启用顶点的句柄
            GLES30.glEnableVertexAttribArray(vertexPoints_handle);

            //准备颜色数据
            GLES30.glVertexAttribPointer(colorPoints_handle,3,GLES30.GL_FLOAT, false, 0,colorBuffer_marker);
            GLES30.glEnableVertexAttribArray(colorPoints_handle);

            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, markerPoints.length/3);
        }

//        bufferSet_Marker(0.5f, 0.5f, 0.5f, 6);
//
//        //准备坐标数据
//        GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker);
//        //启用顶点的句柄
//        GLES30.glEnableVertexAttribArray(vertexPoints_handle);
//
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, markerPoints.length/3);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(vertexPoints_handle);
        GLES30.glDisableVertexAttribArray(normalizePoints_handle);

        GLES30.glDisableVertexAttribArray(colorPoints_handle);
    }

    public void drawFlag(float [] matrix, float [] pos, int num){
        bufferSet_Flag(matrix, pos);

        GLES30.glUseProgram(mProgramFlag);

        //准备坐标数据
        GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_flag);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(vertexPoints_handle);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, flagPoints.length / 3);

        GLES30.glDisableVertexAttribArray(vertexPoints_handle);

        bufferSet_Numbers(matrix, pos);

        GLES30.glUseProgram(mProgramNumbers);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0); //设置使用的纹理编号
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, numbersTexture[num][0]); //绑定指定的纹理id

        GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_numbers);

        GLES30.glEnableVertexAttribArray(vertexPoints_handle);

        GLES30.glVertexAttribPointer(coordPoints_handle, 2, GLES30.GL_FLOAT, false, 0, coordBuffer_numbers);

        GLES30.glEnableVertexAttribArray(coordPoints_handle);

        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgramNumbers, "vTexture"), 0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, coordNumbers.length / 2);

        GLES30.glDisableVertexAttribArray(vertexPoints_handle);
        GLES30.glDisableVertexAttribArray(coordPoints_handle);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    private void bufferSet_Marker(float x, float y, float z, float r, float [] matrix, int type, float proportion){

        colorPoints_marker = new float[markerPoints.length];//colormap[type];
        for(int i=0; i<colorPoints_marker.length; i++){
            colorPoints_marker[i] = colormap[type % 7][i % 3] * proportion;
        }
        colorBuffer_marker = ByteBuffer.allocateDirect(colorPoints_marker.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer_marker.put(colorPoints_marker);
        colorBuffer_marker.position(0);


        markerPoints = new float[]{
                r, 0, 0,
                0, r, 0,
                0, 0, r,

                r, 0, 0,
                0, -r, 0,
                0, 0, r,

                -r, 0, 0,
                0, r, 0,
                0, 0, r,

                -r, 0, 0,
                0, -r, 0,
                0, 0, r,

                r, 0, 0,
                0, r, 0,
                0, 0, -r,

                r, 0, 0,
                0, -r, 0,
                0, 0, -r,

                -r, 0, 0,
                0, r, 0,
                0, 0, -r,

                -r, 0, 0,
                0, -r, 0,
                0, 0, -r,
        };

//        float [] pos = new float[]{x, y, z, 1.0f};
        float [] pos = new float[]{x / (float) (sz[0]) * dim[0] , y / (float) (sz[1]) * dim[1], z / (float) (sz[2]) * dim[2], 1.0f};
        float [] posAfter = new float[4];
        Matrix.multiplyMV(posAfter, 0, matrix, 0, pos, 0);
//        posAfter[0] = posAfter[0] / posAfter[3];
//        posAfter[1] = posAfter[1] / posAfter[3];
//        posAfter[2] = posAfter[2] / posAfter[3];
        float [] posDivide = new float[]{posAfter[0] / posAfter[3], posAfter[1] / posAfter[3], posAfter[2] / posAfter[3]};
//        Log.d(TAG, "posDivide: " + Arrays.toString(posDivide));


        markerVertexPoints = new float[markerPoints.length];
//        for (int i = 0; i < markerPoints.length / 3; i++) {
//            markerVertexPoints[i * 3] = x / (float) (sz[0]) * dim[0] + markerPoints[i * 3];
//            markerVertexPoints[i * 3 + 1] = y / (float) (sz[1]) * dim[1] + markerPoints[i * 3 + 1];
//            markerVertexPoints[i * 3 + 2] = z / (float) (sz[2]) * dim[2] + markerPoints[i * 3 + 2];
//        }
        for (int i = 0; i < markerPoints.length / 3; i++) {
            markerVertexPoints[i * 3] = posDivide[0] + markerPoints[i * 3] * 10;
            markerVertexPoints[i * 3 + 1] = posDivide[1] + markerPoints[i * 3 + 1] * 10;
            markerVertexPoints[i * 3 + 2] = posDivide[2] + markerPoints[i * 3 + 2] * 10;
        }

//        markerVertexPoints =createPositions(x / (float)(sz[0]) * dim[0], y / (float)(sz[1]) * dim[1], z / (float)(sz[2]) * dim[2], 0.01f);
        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_marker = ByteBuffer.allocateDirect(markerVertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_marker.put(markerVertexPoints);
        vertexBuffer_marker.position(0);

    }

    private void bufferSet_Type(int type){
//        colorPoints_marker = new float[markerPoints.length];//colormap[type];
//        for(int i=0; i<colorPoints_marker.length; i++){
//            colorPoints_marker[i] = colormap[type % 7][i % 3];
//        }
//        colorBuffer_marker = ByteBuffer.allocateDirect(colorPoints_marker.length*4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        colorBuffer_marker.put(colorPoints_marker);
//        colorBuffer_marker.position(0);

//        normalizePoints_marker_small = createNormlizes(6.0f);
        normalizePoints_marker_small = new float[markerPoints.length];
        for (int i = 0; i < normalizePoints_marker_small.length; i++){
            normalizePoints_marker_small[i] = markerPoints[i];
        }
        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        normalizeBuffer_marker_small = ByteBuffer.allocateDirect(normalizePoints_marker_small.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        normalizeBuffer_marker_small.put(normalizePoints_marker_small);
        normalizeBuffer_marker_small.position(0);
    }

    private void bufferSet_Flag(float [] matrix, float [] flag){
        float [] tempVertex = new float[4];
        Matrix.multiplyMV(tempVertex, 0, matrix, 0, new float[]{flag[0], flag[1], flag[2], 1.0f}, 0);
        float [] vertexDivide = new float[]{tempVertex[0] / tempVertex[3], tempVertex[1] / tempVertex[3], tempVertex[2] / tempVertex[3]};


        float [] vertex = new float[flagPoints.length];
        for (int i = 0; i < flagPoints.length / 3; i++){
            vertex[i * 3] = vertexDivide[0] + flagPoints[i * 3];
            vertex[i * 3 + 1] = vertexDivide[1] + flagPoints[i * 3 + 1];
            vertex[i * 3 + 2] = vertexDivide[2] + flagPoints[i * 3 + 2];
        }

        vertexBuffer_flag = ByteBuffer.allocateDirect(flagPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexBuffer_flag.put(vertex);
        vertexBuffer_flag.position(0);
    }

    private void bufferSet_Numbers(float [] matrix, float [] flag){
        float [] tempVertex = new float[4];
        Matrix.multiplyMV(tempVertex, 0, matrix, 0, new float[]{flag[0], flag[1], flag[2], 1.0f}, 0);
        float [] vertexDivide = new float[]{tempVertex[0] / tempVertex[3], tempVertex[1] / tempVertex[3], tempVertex[2] / tempVertex[3]};

        float [] vertex = new float[coordNumbers.length / 2 * 3];
        for (int i = 0; i < coordNumbers.length / 2; i++){
            vertex[i * 3] = vertexDivide[0] + flagPoints[i * 3 + 18];
            vertex[i * 3 + 1] = vertexDivide[1] + flagPoints[i * 3 + 19];
            vertex[i * 3 + 2] = vertexDivide[2] + flagPoints[i * 3 + 20] - 0.01f;
        }

        vertexBuffer_numbers = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexBuffer_numbers.put(vertex);
        vertexBuffer_numbers.position(0);

        coordBuffer_numbers = ByteBuffer.allocateDirect(coordNumbers.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        coordBuffer_numbers.put(coordNumbers);
        coordBuffer_numbers.position(0);
    }

    private void bufferSet(){
        vertexPoints = new float[lightPoints.size() * 3];
        for (int i = 0; i < lightPoints.size(); i++){
            vertexPoints[i * 3] = lightPoints.get(i).x / (float)(sz[0]) * dim[0];
            vertexPoints[i * 3 + 1] = lightPoints.get(i).y / (float)(sz[1]) * dim[1];
            vertexPoints[i * 3 + 2] = lightPoints.get(i).z / (float)(sz[2]) * dim[2];

        }
//        vertexPoints = new float[]{0.5f, 0.5f, 0.5f,1f};
//        for (int i = 0; i < 5; i++){
//            Log.d(TAG, "bufferSet: " + vertexPoints[i * 3] + " " + vertexPoints[i * 3 + 1] + " " + vertexPoints[i * 3 + 2]);
//        }

        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
    }

    private void getHandle(int mProgram){
        GLES30.glEnableVertexAttribArray(positionHandle);
//        GLES30.glEnable(colorHandle);
        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void removePointsByCenter(float [] pos){

        float [] centerPos = new float[]{
                pos[0] * (float) (sz[0]) / dim[0],
                pos[1] * (float) (sz[1]) / dim[1],
                pos[2] * (float) (sz[2]) / dim[2]
        };
        Log.d(TAG, "removeWhileMove" + Arrays.toString(centerPos));
        GameMapPoint temp2 = new GameMapPoint(centerPos);

        for (int i = 0; i < lightPoints.size(); i++){
            GameMapPoint temp = lightPoints.get(i);

            if (distance(temp, temp2) < 10){
//                Log.d("RemovePointsByCenter", "Removed!!!");
//                GameActivity.addScore(temp.score);
                removedPoints.add(lightPoints.remove(i));
            } else if (distance(temp, temp2) < 20){
                temp.updateScore();
            }
        }

        gamePos = centerPos;

//        bufferSet();
    }

    @SuppressLint("LongLogTag")
    public void removePointsByCenterWhileLoad(float [] pos){
        float [] centerPos = new float[]{
                pos[0] * (float) (sz[0]) / dim[0],
                pos[1] * (float) (sz[1]) / dim[1],
                pos[2] * (float) (sz[2]) / dim[2],
        };
        Log.d(TAG, "removeWhileLoad" + Arrays.toString(centerPos));

        GameMapPoint temp2 = new GameMapPoint(centerPos);

        for (int i = 0; i < lightPoints.size(); i++){
            GameMapPoint temp = lightPoints.get(i);

            if (distance(temp, temp2) < 10) {
                Log.d("RemovePointsByCenterWhileLoad", "Removed!!!");
//                GameActivity.addScore(temp.score);
                lightPoints.remove(i);
            } else if (distance(temp, temp2) < 20){
                temp.updateScore();
            }
        }
    }

    private float[]  createPositions(float x, float y, float z, float r){

        int step = 2;

        if(r <= 0.01f){
            step = 6;
        }

        int count = 0;
        ArrayList<Float> data=new ArrayList<>();
        float r1,r2;
        float h1,h2;
        float sin,cos;
        for(int i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0)          * r;
            r2 = (float)Math.cos((i + step) * Math.PI / 180.0) * r;
            h1 = (float)Math.sin(i * Math.PI / 180.0)          * r;
            h2 = (float)Math.sin((i + step) * Math.PI / 180.0) * r;
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (int j = 0; j < 360 + step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);

                data.add(r2 * cos + x);
                data.add(h2       + y);
                data.add(r2 * sin + z);

                data.add(r1 * cos + x);
                data.add(h1       + y);
                data.add(r1 * sin + z);

                count++;
//                System.out.println("Count: " + count);
            }
        }
        float[] f=new float[data.size()];
        for(int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }

        return f;
    }

    private void setPanoramicPosition(){
        int numH = 30;
        int numV = 30;

        float r1, r2;
        float h1, h2;
        float sin1, cos1;
        float sin2, cos2;

        ArrayList<Float> data = new ArrayList<>();
        bgPosition = new float[(numV) * (numH) * 18];
        bgCoord = new float[(numV) * (numH) * 12];
        for (int i = 0; i < numV; i++){
            r1 = (float)Math.cos((-90 + i * 180.0 / numV) * Math.PI / 180.0) * bgRadius;
            r2 = (float)Math.cos((-90 + (i + 1) * 180.0 / numV) * Math.PI / 180.0) * bgRadius;
            h1 = (float)Math.sin((-90 + i * 180.0 / numV) * Math.PI / 180.0) * bgRadius;
            h2 = (float)Math.sin((-90 + (i + 1) * 180.0 / numV) * Math.PI / 180.0) * bgRadius;
            for (int j = 0; j < numH; j++){
                cos1 = (float) Math.cos(j * 360.0 / numH * Math.PI / 180.0);
                sin1 = (float) -Math.sin(j * 360.0 / numH * Math.PI / 180.0);

                cos2 = (float) Math.cos((j + 1) * 360 / numH * Math.PI / 180.0);
                sin2 = (float) -Math.sin((j + 1) * 360 / numH * Math.PI / 180.0);
                bgPosition[(i * (numH) + j) * 18] = r2 * cos1 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 1] = h2 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 2] = r2 * sin1 + 0.5f;

                bgPosition[(i * (numH) + j) * 18 + 3] = r1 * cos1 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 4] = h1 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 5] = r1 * sin1 + 0.5f;

                bgPosition[(i * (numH) + j) * 18 + 6] = r1 * cos2 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 7] = h1 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 8] = r1 * sin2 + 0.5f;

                bgPosition[(i * (numH) + j) * 18 + 9] = r2 * cos2 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 10] = h2 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 11] = r2 * sin2 + 0.5f;

                bgPosition[(i * (numH) + j) * 18 + 12] = r1 * cos2 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 13] = h1 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 14] = r1 * sin2 + 0.5f;

                bgPosition[(i * (numH) + j) * 18 + 15] = r2 * cos1 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 16] = h2 + 0.5f;
                bgPosition[(i * (numH) + j) * 18 + 17] = r2 * sin1 + 0.5f;
//
//                data.add(r2 * cos + 0.5f);
//                data.add(h2       + 0.5f);
//                data.add(r2 * sin + 0.5f);
//
//                data.add(r1 * cos + 0.5f);
//                data.add(h1       + 0.5f);
//                data.add(r1 * sin + 0.5f);

                bgCoord[(i * (numH) + j) * 12] = (float)j / numH;
                bgCoord[(i * (numH) + j) * 12 + 1] = (float)(i + 1) / numV;

                bgCoord[(i * (numH) + j) * 12 + 2] = (float)j / numH;
                bgCoord[(i * (numH) + j) * 12 + 3] = (float)i / numV;

                bgCoord[(i * (numH) + j) * 12 + 4] = (float)(j + 1) / numH;
                bgCoord[(i * (numH) + j) * 12 + 5] = (float)i / numV;

                bgCoord[(i * (numH) + j) * 12 + 6] = (float)(j + 1) / numH;
                bgCoord[(i * (numH) + j) * 12 + 7] = (float)(i + 1) / numV;

                bgCoord[(i * (numH) + j) * 12 + 8] = (float)(j + 1) / numH;
                bgCoord[(i * (numH) + j) * 12 + 9] = (float)i / numV;

                bgCoord[(i * (numH) + j) * 12 + 10] = (float)j / numH;
                bgCoord[(i * (numH) + j) * 12 + 11] = (float)(i + 1) / numV;

//                bgCoord[(i * (numH) + j) * 12] = 0;
//                bgCoord[(i * (numH) + j) * 12 + 1] = 1;
//
//                bgCoord[(i * (numH) + j) * 12 + 2] = 0;
//                bgCoord[(i * (numH) + j) * 12 + 3] = 0;
//
//                bgCoord[(i * (numH) + j) * 12 + 4] = 1;
//                bgCoord[(i * (numH) + j) * 12 + 5] = 0;
//
//                bgCoord[(i * (numH) + j) * 12 + 6] = 1;
//                bgCoord[(i * (numH) + j) * 12 + 7] = 1;
//
//                bgCoord[(i * (numH) + j) * 12 + 8] = 1;
//                bgCoord[(i * (numH) + j) * 12 + 9] = 0;
//
//                bgCoord[(i * (numH) + j) * 12 + 10] = 0;
//                bgCoord[(i * (numH) + j) * 12 + 11] = 1;
            }
        }
//        bgPosition = new float[data.size()];
//        for (int i = 0; i < data.size(); i++){
//            bgPosition[i] = data.get(i);
//        }

//        bgPosition = createPositions(0.5f, 0.5f, 0.5f, bgRadius);

        bgBuffer = ByteBuffer.allocateDirect(bgPosition.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        bgBuffer.put(bgPosition);
        bgBuffer.position(0);

        bgCoordBuffer = ByteBuffer.allocateDirect(bgCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        bgCoordBuffer.put(bgCoord);
        bgCoordBuffer.position(0);
    }

    private float[]  createNormlizes(float step_input){

        float step = step_input;
        ArrayList<Float> normlizes=new ArrayList<>();
        float r1,r2;
        float h1,h2;
        float sin,cos;
        for(float i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0)         ;
            r2 = (float)Math.cos((i + step) * Math.PI / 180.0);
            h1 = (float)Math.sin(i * Math.PI / 180.0)         ;
            h2 = (float)Math.sin((i + step) * Math.PI / 180.0);
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (float j = 0.0f; j <360.0f+step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);

                normlizes.add(r2 * cos + 0.5f);
                normlizes.add(h2       + 0.5f);
                normlizes.add(r2 * sin + 0.5f);

                normlizes.add(r1 * cos + 0.5f);
                normlizes.add(h1       + 0.5f);
                normlizes.add(r1 * sin + 0.5f);
            }
        }
        float[] f=new float[normlizes.size()];
        for(int i=0;i<f.length;i++){
            f[i]=normlizes.get(i);
        }

        return f;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
//        timer.cancel();
//        task.cancel();
    }

    private void updateRemovedPoints(){
        if (gamePos == null){
            return;
        }

        if (removedPoints.size() == 0){
            return;
        }

        for (int i = 0; i < removedPoints.size(); i++){
            GameMapPoint temp = removedPoints.get(i);
            GameMapPoint center = new GameMapPoint(gamePos);
            temp.moveTo(center, 0.99f);
            temp.reduceRadius(0.99f);
//            if (distance(temp, center) < 0.01){
//                removedPoints.remove(i);
//            }
            if (temp.radius < defaultRadius / 4){
                removedPoints.remove(i);
            }
        }
    }

    private void createBGTexture(){

        if(mBitmap!=null&&!mBitmap.isRecycled()){

            Log.d("CreateBGTexture", "!!!!!!");

            //生成纹理
            GLES30.glGenTextures(1,backgroundTexture,0);

            //生成纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,backgroundTexture[0]);

            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);

            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);

            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);

            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, mBitmap, 0);

            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
        }

    }

    private void setBackgroundBuffer(){
        bgPosition = new float[]{
                1, 1, 1,
                1, 0, 1,
                0, 1, 1,
                0, 0, 1
        };
        //分配内存空间,每个浮点型占4字节空间
        bgBuffer = ByteBuffer.allocateDirect(bgPosition.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        bgBuffer.put(bgPosition);
        bgBuffer.position(0);

        //分配内存空间,每个浮点型占4字节空间
        bCoord = ByteBuffer.allocateDirect(sCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        bCoord.put(sCoord);
        bCoord.position(0);
    }

    public void setBackground(Bitmap b){
        Log.d("SetBackground: ", "!!!!!");
        mBitmap = b;
        bgWidth = b.getWidth();
        bgHeight = b.getHeight();

        Log.d("SetBackground: ", Float.toString(bgWidth) + " " + Float.toString(bgHeight));

        createBGTexture();
    }

    public void drawBackground(float [] vMatrix){
        bgMatrix = vMatrix;

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glUseProgram(mProgramBackground);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0); //设置使用的纹理编号
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, backgroundTexture[0]); //绑定指定的纹理id

        int glHMatrix = GLES30.glGetUniformLocation(mProgramBackground, "vMatrix");
        GLES20.glUniformMatrix4fv(glHMatrix,1,false,bgMatrix,0);

        int glHPosition = GLES30.glGetAttribLocation(mProgramBackground, "vPosition");
        GLES20.glEnableVertexAttribArray(glHPosition);

        int glHCoordinate = GLES30.glGetAttribLocation(mProgramBackground, "vCoordinate");
        GLES20.glEnableVertexAttribArray(glHCoordinate);

        GLES20.glUniform1i(GLES30.glGetUniformLocation(mProgramBackground, "vTexture"), 0);

//        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

//        setBackgroundBuffer();
//        setPanoramicPosition();

        //传入顶点坐标
        GLES20.glVertexAttribPointer(glHPosition, 3, GLES20.GL_FLOAT, false, 0, bgBuffer);
        //传入纹理坐标
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, bgCoordBuffer);

//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,bgPosition.length / 3);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,bgPosition.length / 3);

    }

    public void initFontBitmap(){

        for (int i = 0; i < 10; i++) {
//            Rect rect = new Rect(100, 100, 500, 500);
//            Paint rectPaint = new Paint();
//            rectPaint.setColor(Color.WHITE);
//            rectPaint.setStyle(Paint.Style.FILL);
            String font = Integer.toString(i);
            numbersBitmap[i] = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(numbersBitmap[i]);
            //背景颜色
            canvas.drawColor(Color.WHITE);
//            canvas.drawRect(rect, rectPaint);
            Paint p = new Paint();
            //字体设置
            String fontType = "宋体";
            Typeface typeface = Typeface.create(fontType, Typeface.BOLD);
            //消除锯齿
            p.setAntiAlias(true);
            //字体为红色
            p.setColor(Color.BLACK);
            p.setTypeface(typeface);
            p.setTextSize(100);
//            Paint.FontMetrics fontMetrics = p.getFontMetrics();

            float textWidth = p.measureText(font);
            float baseLineY = Math.abs(p.ascent() + p.descent()) / 2;

//            p.setTextAlign(Paint.Align.CENTER);
//            int baseLineY = (int)(rect.centerY() - top / 2 - bottom / 2);
            //绘制字体
//            canvas.drawText(font, -textWidth / 2, baseLineY, p);
            canvas.drawText(font, 100, 150, p);

            GLES30.glEnable(GLES30.GL_TEXTURE_2D);

            GLES30.glGenTextures(1, numbersTexture[i], 0);

            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, numbersTexture[i][0]);

            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);

            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);

            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);

            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);

            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, numbersBitmap[i], 0);

            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);


        }
    }

    public void free(){
        if (vertexBuffer != null) {
            vertexBuffer.clear();
            vertexBuffer = null;
        }

        if (vertexBuffer_marker != null) {
            vertexBuffer_marker.clear();
            vertexBuffer_marker = null;
        }

        if (colorBuffer_marker != null) {
            colorBuffer_marker.clear();
            colorBuffer_marker = null;
        }

        if (normalizeBuffer_marker_small != null) {
            normalizeBuffer_marker_small.clear();
            normalizeBuffer_marker_small = null;
        }

        if (bgBuffer != null) {
            bgBuffer.clear();
            bgBuffer = null;
        }

        if (bgCoordBuffer != null) {
            bgCoordBuffer.clear();
            bgCoordBuffer = null;
        }
    }
}
