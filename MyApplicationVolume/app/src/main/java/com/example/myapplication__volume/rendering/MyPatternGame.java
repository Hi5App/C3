package com.example.myapplication__volume.rendering;

import android.opengl.GLES30;
import android.util.Log;

import com.example.basic.ByteTranslate;
import com.example.basic.Image4DSimple;
import com.example.basic.XYZ;
import com.example.myapplication__volume.GameActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

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

    private ArrayList<XYZ> lightPoints = new ArrayList<>();
    private int [][][] pointRooms = new int[128][128][128];

    private int threshold = 0;

    private static int positionHandle = 0;
    private int colorHandle = 1;
    private int mvpMatrixHandle;

    private static int mProgramGame;

    private FloatBuffer vertexBuffer;

    private float [] vertexPoints;

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

    public MyPatternGame(int width, int height, Image4DSimple img, float [] mz, int [] sz){
        image = img;
        dim = mz;
        this.sz = sz;

        initMap();
    }

    public static void initProgram(){
        mProgramGame = initProgram(vertexShaderCode_points, fragmentShaderCode_points);
    }

    private static int initProgram(String vertShaderCode, String fragmShaderCode){

        //加载着色器
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER,
                vertShaderCode);
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER,
                fragmShaderCode);

        Log.v("vertexShader", Integer.toString(vertexShader));

        Log.v("fragmentShader", Integer.toString(fragmentShader));


        // create empty OpenGL ES Program
        int mProgram = GLES30.glCreateProgram();

        // add the vertex shader to program
        GLES30.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES30.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES30.glLinkProgram(mProgram);


        GLES30.glValidateProgram(mProgram); // 让OpenGL来验证一下我们的shader program，并获取验证的状态
        int[] status = new int[1];
        GLES30.glGetProgramiv(mProgram, GLES30.GL_VALIDATE_STATUS, status, 0);                                  // 获取验证的状态
        Log.d("Program:", "validate shader----program: " + GLES30.glGetProgramInfoLog(mProgram));

        return mProgram;

    }

    private static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        int[] status = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, status, 0);// 获取验证的状态
//        if (status[0] == 0) {
//            String error = GLES30.glGetShaderInfoLog(shader);
//            GLES30.glDeleteShader(shader);
//            Log.v("Error:", "validate shader program: " + error);
//        }
        Log.v("Error:", "validate shader program: " + GLES30.glGetShaderInfoLog(shader));


        return shader;
    }

    private void initMap(){
        vol_w = (int)image.getSz0();
        vol_h = (int)image.getSz1();
        vol_d = (int)image.getSz2();

        nchannel = (int)image.getSz3();

        byte [] dataSrc = image.getData();

        data_length = image.getDatatype().ordinal();
        isBig = image.getIsBig();

        resetLightPoints(dataSrc);

        bufferSet();
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
                            lightPoints.add(new XYZ(x, y, z));
                            pointRooms[x][y][z] = 1;
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
                            lightPoints.add(new XYZ(z, y, x));
                            pointRooms[x][y][z] = 1;
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
                            lightPoints.add(new XYZ(z, y, x));
                            pointRooms[x][y][z] = 1;
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

    public void removePointsByCenter(float [] centerPos){
        for (int i = 0; i < lightPoints.size(); i++){
            XYZ temp = lightPoints.get(i);
            XYZ dis = new XYZ(temp.x - centerPos[0], temp.y - centerPos[1], temp.z - centerPos[2]);
            if (XYZ.norm(dis) < 10){
                Log.d("RemovePointsByCenter", "Removed!!!");
                lightPoints.remove(i);
                GameActivity.addScore(10);
            }
        }

        bufferSet();
    }
}
