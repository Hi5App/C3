package com.example.myapplication__volume;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import com.example.basic.Image4DSimple;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.example.myapplication__volume.MainActivity.getContext;


public class MyPattern{

    private final int scale = 128;
    private int count = 0;

    //两个shader program
    private final int mProgram_simple;
    private final int mProgram_raycasting;
    private final int mProgram_curve;


    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ShortBuffer drawListBuffer;
    private FloatBuffer dimBuffer;

    private FloatBuffer vertexBuffer_curve;



    private FloatBuffer vertexBuffer_suqre;
    private FloatBuffer colorBuffer_suqre;
    private ShortBuffer drawListBuffer_suqre;

    private int positionHandle = 0;
    private int colorHandle = 1;
    private int vPMatrixHandle;
    private int dimHandle;
    private int trAMatrixHandle;

    private int positionHandle_suqre;
    private int colorHandle_square;
    private int vPMatrixHandle_square;

    private String filepath = ""; //文件路径
    private InputStream is;
    private long length;


    private ByteBuffer imageBuffer;
    private ByteBuffer imageBuffer_FBO;

    private int[] vol_tex = new int[1]; //生成纹理id;
    private int[] fbo_tex = new int[1]; //生成纹理id;
    private int[] backCoord = new int[1]; //生成纹理id;

    private int[] fbo = new int[1];//生成framebuffer
    private int[] rbo = new int[1];//生成renderbuffer

    private final int fboBackCoord;

    private int vol_w;
    private int vol_h;
    private int vol_d;

    private Image4DSimple image;

    private float[] vertexPoints;
    private float[] Colors;
    private float[] dim;


//    private final float[] vertexPoints={
//            // Front face
//            0.0f, 0.0f, 1.0f,
//            1.0f, 0.0f, 1.0f,
//            1.0f, 1.0f, 1.0f,
//            0.0f, 1.0f, 1.0f,
//
//            // Back face
//            0.0f, 0.0f, 0.0f,
//            0.0f, 1.0f, 0.0f,
//            1.0f, 1.0f, 0.0f,
//            1.0f, 0.0f, 0.0f,
//
//            // Top face
//            0.0f, 1.0f, 0.0f,
//            0.0f, 1.0f, 1.0f,
//            1.0f, 1.0f, 1.0f,
//            1.0f, 1.0f, 0.0f,
//
//            // Bottom face
//            0.0f, 0.0f, 0.0f,
//            1.0f, 0.0f, 0.0f,
//            1.0f, 0.0f, 1.0f,
//            0.0f, 0.0f, 1.0f,
//
//            // Right face
//            1.0f, 0.0f, 0.0f,
//            1.0f, 1.0f, 0.0f,
//            1.0f, 1.0f, 1.0f,
//            1.0f, 0.0f, 1.0f,
//
//            // Left face
//            0.0f, 0.0f, 0.0f,
//            0.0f, 0.0f, 1.0f,
//            0.0f, 1.0f, 1.0f,
//            0.0f, 1.0f, 0.0f,
//    };
//
//
//
//    private final float[] Colors={
//            // Front face
//            0.0f, 0.0f, 1.0f, 1.0f,
//            1.0f, 0.0f, 1.0f, 1.0f,
//            1.0f, 1.0f, 1.0f, 1.0f,
//            0.0f, 1.0f, 1.0f, 1.0f,
//
//            // Back face
//            0.0f, 0.0f, 0.0f, 1.0f,
//            0.0f, 1.0f, 0.0f, 1.0f,
//            1.0f, 1.0f, 0.0f, 1.0f,
//            1.0f, 0.0f, 0.0f, 1.0f,
//
//            // Top face
//            0.0f, 1.0f, 0.0f, 1.0f,
//            0.0f, 1.0f, 1.0f, 1.0f,
//            1.0f, 1.0f, 1.0f, 1.0f,
//            1.0f, 1.0f, 0.0f, 1.0f,
//
//            // Bottom face
//            0.0f, 0.0f, 0.0f, 1.0f,
//            1.0f, 0.0f, 0.0f, 1.0f,
//            1.0f, 0.0f, 1.0f, 1.0f,
//            0.0f, 0.0f, 1.0f, 1.0f,
//
//            // Right face
//            1.0f, 0.0f, 0.0f, 1.0f,
//            1.0f, 1.0f, 0.0f, 1.0f,
//            1.0f, 1.0f, 1.0f, 1.0f,
//            1.0f, 0.0f, 1.0f, 1.0f,
//
//            // Left face
//            0.0f, 0.0f, 0.0f, 1.0f,
//            0.0f, 0.0f, 1.0f, 1.0f,
//            0.0f, 1.0f, 1.0f, 1.0f,
//            0.0f, 1.0f, 0.0f, 1.0f,
//    };



    private final short[] drawlist = {

            0, 1, 2,      0, 2, 3,    // Front face
            4, 5, 6,      4, 6, 7,    // Back face
            8, 9, 10,     8, 10, 11,  // Top face
            12, 13, 14,   12, 14, 15, // Bottom face
            16, 17, 18,   16, 18, 19, // Right face
            20, 21, 22,   20, 22, 23  // Left face

    };



//    private final float[] vertexPoints_curve={
//            0.0f, 0.5f, -1.0f,
//            -0.5f, -0.5f, -1.0f,
//            0.5f, -0.5f, -1.0f
//    };
//





    private final float[] vertexPoints_square={
            // Front face
            -1.0f,  1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f,  1.0f, 0.0f,
    };


    private final float[] TexCoord_square={
            // Front face
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    private final short[] drawlist_quare = {

            0, 1, 2,      0, 2, 3,    // Front face

    };



    //shader for test~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//
//    private final String vertexShaderCode =
//            "attribute vec4 a_position;" +
//                    "attribute vec4 a_color;" +
//                    "attribute vec2 a_TexCoord;" +
//
//                    "uniform mat4 uMVPMatrix;" +
//                    "varying vec2 v_TexCoord;" +
//
//                    "void main(){" +
//                    "   gl_Position = uMVPMatrix * a_position;" +
//                    "   v_TexCoord = a_TexCoord;" +
//                    "}";
//
//    private final String fragmentShaderCode =
//            "precision mediump float;" +
//
//                    "varying vec2 v_TexCoord;" +
//                    "uniform sampler2D u_TextureUnit;" +
//
//                    "void main(){" +
//                    "   gl_FragColor = texture2D(u_TextureUnit, v_TexCoord);" +
//                    "}";




    //opengl es 2.0~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~




//    private final String vertexShaderCode_1 =
////            "#version 300 es\n" +
//                    "attribute vec4 a_position;" +
//                    "attribute vec4 a_color;" +
//                    "uniform mat4 uMVPMatrix;" +
//
//                    "varying vec4 backColor;" +
//
//                    "void main() {" +
//                    "  gl_Position = uMVPMatrix * a_position;" +
//                    "  backColor = a_color;" +
//                    "}";
//
//
//
//
//    private final String fragmentShaderCode_1 =
////            "#version 300 es\n" +
//            "precision mediump float;" +
//
//                    "varying vec4 backColor;" +
//
//                    "void main() {" +
//                    "   gl_FragColor = backColor;" +
//                    "}";





//    private final String vertexShaderCode_2 =
//            // This matrix member variable provides a hook to manipulate
//            // the coordinates of the objects that use this vertex shader
////            "#version 300 es\n" +
//            "precision highp float;" +
//                    "uniform mat4 uMVPMatrix;" +
////                    "uniform mat4 trAMatrix;" +
//                    "attribute vec4 a_color;" +
//                    "attribute vec4 a_position;" +
//
//                    "varying vec4 pos;" +
//                    "varying vec4 frontColor;"+
//
//                    "void main() {" +
//                    "  pos = uMVPMatrix * a_position;"+
//                    "  gl_Position = pos;" +
////                    "  gl_Position = trAMatrix * pos;" +
//                    "  frontColor = a_color;" +
//                    "}";
//
//
//
//    private final String fragmentShaderCode_2 =
////            "#version 300 es\n" +
//            "#extension GL_OES_texture_3D : enable\n" +
//                    "precision highp float;" +
//
//                    "varying vec4 frontColor;" +
//                    "varying vec4 pos;" +
//
//                    "uniform sampler2D uBackCoord;" +
//                    "uniform sampler3D uVolData;" +
//                    "uniform sampler2D uTransferFunction;" +
//
//                    "const float numberOfSlices = 128.0;" +
//                    "const float slicesOverX = 8.0;" +
//                    "const float slicesOverY = 16.0;" +
//
//
//                    "void main(void)" +
//                    "{" +
//                    "  vec2 texC = pos.xy/pos.w;" +
//                    "  texC.x = 0.5*texC.x + 0.5;" +
//                    "  texC.y = 0.5*texC.y + 0.5;" +
//                    "  " +
//                    "  vec4 backColor = texture2D(uBackCoord,texC);" +
//                    "  " +
//                    "  vec3 dir = backColor.rgb - frontColor.rgb;" +
//                    "  float steps = 256.0;" +
//                    "  vec4 vpos = frontColor;" +
//                    "  " +
//                    "  float cont = 0.0;" +
//                    "  " +
//                    "  vec3 Step = dir/steps;" +
//                    "  " +
//                    "  vec4 accum = vec4(0, 0, 0, 0);" +
//                    "  vec4 sample = vec4(0.0, 0.0, 0.0, 0.0);" +
//                    "  vec4 value = vec4(0, 0, 0, 0);" +
//                    "  " +
//                    "  float opacityFactor = 8.0;" +
//                    "  float lightFactor = 1.3;" +
//                    "  " +
//                    "  for(float i = 0.0; i < steps; i+=1.0)" +
//                    "  {" +
////                    "     vec4 tf_pos;" +
////                    "     vec3 truepos = vec3(vpos.x, vpos.y, vpos.z);" +
////
////                    "     tf_pos = texture3D(uVolData, truepos);" +
////                    "     value = vec4(tf_pos.x);" +
//
//                    "     vec4 tf_value;" +
//                    "     tf_value = texture3D(uVolData, vpos.xyz);" +
//                    "     value = vec4(tf_value.x);" +
//
//
//                    "     if(value.r > accum.r)\n" +
//                    "         accum.rgb = value.rgb;" +
//                    "     accum.a += (1.0 - accum.a) * value.a;" +
//                    "     vpos.xyz += Step;" +
//
//                    "     if(vpos.x > 1.0 || vpos.y > 1.0 || vpos.z > 1.0 || accum.a>=1.0)" +
//                    "         break;" +
//                    "     }" +
//                    "  accum.a = 1.0;" +
//                    "  gl_FragColor = accum;" +
//                    "}";





    //opengl es 3.0~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`


    private final String vertexShaderCode_1 =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 a_position;" +
                    "layout (location = 1) in vec4 a_color;" +
                    "uniform mat4 uMVPMatrix;" +

                    "out vec4 backColor;" +

                    "void main() {" +
                    "  gl_Position = uMVPMatrix * a_position;" +
                    "  backColor = a_color;" +
                    "}";




    private final String fragmentShaderCode_1 =
            "#version 300 es\n" +
                    "precision mediump float;" +

                    "in vec4 backColor;" +
                    "layout (location = 0) out vec4 fragColor;" +

                    "void main() {" +
                    "   fragColor = backColor;" +
                    "}";




    private final String vertexShaderCode_2 =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "#version 300 es\n" +
                    "precision highp float;" +
                    "uniform mat4 uMVPMatrix;" +
                    "layout (location = 0) in vec4 a_color;" +
                    "layout (location = 1) in vec4 a_position;" +

                    "out vec4 pos;" +
                    "out vec4 frontColor;"+

                    "void main() {" +
                    "  pos = uMVPMatrix * a_position;"+
                    "  gl_Position = pos;" +
                    "  frontColor = a_color;" +
                    "}";



    private final String fragmentShaderCode_2 =
            "#version 300 es\n" +
//            "#extension GL_OES_texture_3D : enable\n" +
                    "precision highp float;" +

                    "in vec4 frontColor;" +
                    "in vec4 pos;" +

                    "uniform sampler2D uBackCoord;" +
                    "uniform highp sampler3D uVolData;" +
                    "uniform sampler2D uTransferFunction;" +
                    "uniform highp float dim[3];" +
                    "layout (location = 0) out vec4 fragColor;" +

//                    "const float numberOfSlices = 128.0;" +
//                    "const float slicesOverX = 8.0;" +
//                    "const float slicesOverY = 16.0;" +


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
//                    "     tf_value = texture(uVolData, vec3(1.0 - vpos.x, 1.0 - vpos.y, vpos.z));" +
//                    "     tf_value = texture(uVolData, vec3(vpos.x, 1.0 - vpos.y, vpos.z));" +
//                    "     value = vec4(tf_value.x);" +
                    " value = vec4(tf_value.x, tf_value.y, tf_value.z, tf_value.x);" +

                    "     if(value.r > accum.r)\n" +
                    "         accum.rgb = value.rgb;" +
                    "     accum.a += (1.0 - accum.a) * value.a;" +
                    "     vpos.xyz += Step;" +

                    "     if(vpos.x > 1.0 || vpos.y > 1.0 || vpos.z > 1.0 || accum.a>=1.0)" +
                    "         break;" +
                    "     }" +
                    "  accum.a = 1.0;" +
                    "  fragColor = accum;" +
                    "}";




//    private final String fragmentShaderCode_2 =
//            "#version 300 es\n" +
////            "#extension GL_OES_texture_3D : enable\n" +
//                    "precision highp float;" +
//
//                    "in vec4 frontColor;" +
//                    "in vec4 pos;" +
//
//                    "uniform sampler2D uBackCoord;" +
//                    "uniform highp sampler2D uVolData;" +
//                    "uniform sampler2D uTransferFunction;" +
//                    "layout (location = 0) out vec4 fragColor;" +
//
//                    "const float numberOfSlices = 128.0;" +
//                    "const float slicesOverX = 8.0;" +
//                    "const float slicesOverY = 16.0;" +
//
//
//                    "float getVolumeValue(vec3 volpos)" +
//                    "{" +
//                    "  float s1,s2;" +
//                    "  float dx1,dy1;" +
//                    "  float dx2,dy2;" +
//                    "  " +
//                    "  vec2 texpos1,texpos2;" +
//                    "  " +
//                    "  s1 = floor(volpos.z*numberOfSlices);" +
//                    "  s2 = s1+1.0;" +
//                    "  " +
//                    "  dx1 = fract(s1/slicesOverX);" +
//                    "  dy1 = floor(s1/slicesOverX)/slicesOverY;" +
//                    "  " +
//                    "  dx2 = fract(s2/slicesOverX);" +
//                    "  dy2 = floor(s2/slicesOverX)/slicesOverY;" +
//
//                    "  texpos1.x = dx1+(volpos.x/slicesOverX);" +
//                    "  texpos1.y = dy1+(volpos.y/slicesOverY);" +
//                    "  " +
//                    "  texpos2.x = dx2+(volpos.x/slicesOverX);" +
//                    "  texpos2.y = dy2+(volpos.y/slicesOverY);" +
//                    "  " +
//                    "  return mix( texture(uVolData,texpos1).x, texture(uVolData,texpos2).x, (volpos.z*numberOfSlices)-s1);\n" +
////                    "  return texture2D(uVolData,texpos1).x;" +
////                    "  return max(texture2D(uVolData,texpos1).x, texture2D(uVolData,texpos2).x);" +
//                    "}" +
//
//
//                    "void main(void)" +
//                    "{" +
//                    "  vec2 texC = pos.xy/pos.w;" +
//                    "  texC.x = 0.5*texC.x + 0.5;" +
//                    "  texC.y = 0.5*texC.y + 0.5;" +
//                    "  " +
//                    "  vec4 backColor = texture(uBackCoord,texC);" +
//                    "  " +
//                    "  vec3 dir = backColor.rgb - frontColor.rgb;" +
//                    "  float steps = 256.0;" +
//                    "  vec4 vpos = frontColor;" +
//                    "  " +
//                    "  float cont = 0.0;" +
//                    "  " +
//                    "  vec3 Step = dir/steps;" +
//                    "  " +
//                    "  vec4 accum = vec4(0, 0, 0, 0);" +
////                    "  vec4 sample = vec4(0.0, 0.0, 0.0, 0.0);" +
//                    "  vec4 value = vec4(0, 0, 0, 0);" +
//                    "  " +
//                    "  float opacityFactor = 8.0;" +
//                    "  float lightFactor = 1.3;" +
//                    "  " +
//                    "  for(float i = 0.0; i < steps; i+=1.0)" +
//                    "  {" +
//
////                    "     vec4 tf_value;" +
////                    "     tf_value = texture3D(uVolData, vpos.xyz);" +
////                    "     value = vec4(tf_value.x);" +
//
//                    "     vec2 tf_pos;" +
//
//                    "     tf_pos.x = getVolumeValue(vpos.xyz);" +
//                    "     tf_pos.y = 0.5;" +
//                    "     " +
//                    "     value = vec4(tf_pos.x);" +
//
//
//                    "     if(value.r > accum.r)\n" +
//                    "         accum.rgb = value.rgb;" +
//                    "     accum.a += (1.0 - accum.a) * value.a;" +
//                    "     vpos.xyz += Step;" +
//
//                    "     if(vpos.x > 1.0 || vpos.y > 1.0 || vpos.z > 1.0 || accum.a>=1.0)" +
//                    "         break;" +
//                    "     }" +
//                    "  accum.a = 1.0;" +
//                    "  fragColor = accum;" +
//                    "}";






    //draw points
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private final String vertexShaderCode_curve =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +
                    "void main() {\n" +
                    "     gl_Position  = vPosition;\n" + //
                    "     gl_PointSize = 20.0;\n" +
                    "}\n";


    private final String fragmentShaderCode_curve =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
                    "if (length(gl_PointCoord  - vec2(0.5)) > 0.5) {\n" +
                    "        discard;\n" +
                    "    }" +
                    "     fragColor = vec4(1.0,1.0,1.0,1.0);\n" +
                    "}\n";




    public MyPattern(String filepath, InputStream is, long length, int width, int height, Image4DSimple img, float[] mz) {

        image = img;

        dim = mz;

        setPoint(mz);

//        setPoint_2();

        //创建两个着色器程序
        mProgram_simple = initProgram(vertexShaderCode_1, fragmentShaderCode_1);
        Log.v("mProgram_simple", Integer.toString(mProgram_simple));


        mProgram_raycasting = initProgram(vertexShaderCode_2, fragmentShaderCode_2);
        Log.v("mProgram_raycasting", Integer.toString(mProgram_raycasting));


        mProgram_curve = initProgram(vertexShaderCode_curve, fragmentShaderCode_curve);
        Log.v("mProgram_curve", Integer.toString(mProgram_curve));

//
//        mProgram_axis = initProgram(vertexShaderCode_axis, fragmentShaderCode_axis);
//        Log.v("mProgram_line", Integer.toString(mProgram_axis));

        Log.v("MyPattern", "Init all the programs");



//        mProgram = initProgram(vertexShaderCode, fragmentShaderCode);

//        Log.v("mProgram()",Integer.toString(mProgram));

        //设置一下buffer
        BufferSet();
//        BufferSet_Square();

        //设置文件路径
        SetPath(filepath);

//        setInputStream(is);
//        setLength(length);

        //加载纹理
        try {
            initTexture_3d();
        }catch (Exception e){
            Context context = getContext();
            Intent intent = new Intent(context, FileActivity.class);
            String message = "out of memory when load file";
            intent.putExtra(MyRenderer.OUTOFMEM_MESSAGE, message);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        //        initTexture();

        Log.v("init()","success!!!!!!!!!!!!!!!");

        fboBackCoord = initFBO(width, height);

    }




    private void setPoint(float[] mz){

            vertexPoints = new float[]{
                    // Front face
                    0.0f,  0.0f,  mz[2],
                    mz[0], 0.0f,  mz[2],
                    mz[0], mz[1], mz[2],
                    0.0f,  mz[1], mz[2],

                    // Back face
                    0.0f,  0.0f,  0.0f,
                    0.0f,  mz[1], 0.0f,
                    mz[0], mz[1], 0.0f,
                    mz[0], 0.0f,  0.0f,

                    // Top face
                    0.0f, mz[1],  0.0f,
                    0.0f, mz[1],  mz[2],
                    mz[0], mz[1], mz[2],
                    mz[0], mz[1], 0.0f,

                    // Bottom face
                    0.0f,  0.0f, 0.0f,
                    mz[0], 0.0f, 0.0f,
                    mz[0], 0.0f, mz[2],
                    0.0f,  0.0f, mz[2],

                    // Right face
                    mz[0], 0.0f,  0.0f,
                    mz[0], mz[1], 0.0f,
                    mz[0], mz[1], mz[2],
                    mz[0], 0.0f,  mz[2],

                    // Left face
                    0.0f, 0.0f,  0.0f,
                    0.0f, 0.0f,  mz[2],
                    0.0f, mz[1], mz[2],
                    0.0f, mz[1], 0.0f,
            };

            Colors= new float[]{
                    // Front face
                    0.0f,  0.0f,  mz[2], 1.0f,
                    mz[0], 0.0f,  mz[2], 1.0f,
                    mz[0], mz[1], mz[2], 1.0f,
                    0.0f,  mz[1], mz[2], 1.0f,

                    // Back face
                    0.0f,  0.0f,  0.0f, 1.0f,
                    0.0f,  mz[1], 0.0f, 1.0f,
                    mz[0], mz[1], 0.0f, 1.0f,
                    mz[0], 0.0f,  0.0f, 1.0f,

                    // Top face
                    0.0f, mz[1],  0.0f,  1.0f,
                    0.0f, mz[1],  mz[2], 1.0f,
                    mz[0], mz[1], mz[2], 1.0f,
                    mz[0], mz[1], 0.0f,  1.0f,

                    // Bottom face
                    0.0f,  0.0f, 0.0f,  1.0f,
                    mz[0], 0.0f, 0.0f,  1.0f,
                    mz[0], 0.0f, mz[2], 1.0f,
                    0.0f,  0.0f, mz[2], 1.0f,

                    // Right face
                    mz[0], 0.0f,  0.0f,  1.0f,
                    mz[0], mz[1], 0.0f,  1.0f,
                    mz[0], mz[1], mz[2], 1.0f,
                    mz[0], 0.0f,  mz[2], 1.0f,

                    // Left face
                    0.0f, 0.0f,  0.0f,  1.0f,
                    0.0f, 0.0f,  mz[2], 1.0f,
                    0.0f, mz[1], mz[2], 1.0f,
                    0.0f, mz[1], 0.0f,  1.0f,
            };

//        Colors= new float[]{
//                // Front face
//                0.0f, 0.0f, 1.0f, 1.0f,
//                1.0f, 0.0f, 1.0f, 1.0f,
//                1.0f, 1.0f, 1.0f, 1.0f,
//                0.0f, 1.0f, 1.0f, 1.0f,
//
//                // Back face
//                0.0f, 0.0f, 0.0f, 1.0f,
//                0.0f, 1.0f, 0.0f, 1.0f,
//                1.0f, 1.0f, 0.0f, 1.0f,
//                1.0f, 0.0f, 0.0f, 1.0f,
//
//                // Top face
//                0.0f, 1.0f, 0.0f, 1.0f,
//                0.0f, 1.0f, 1.0f, 1.0f,
//                1.0f, 1.0f, 1.0f, 1.0f,
//                1.0f, 1.0f, 0.0f, 1.0f,
//
//                // Bottom face
//                0.0f, 0.0f, 0.0f, 1.0f,
//                1.0f, 0.0f, 0.0f, 1.0f,
//                1.0f, 0.0f, 1.0f, 1.0f,
//                0.0f, 0.0f, 1.0f, 1.0f,
//
//                // Right face
//                1.0f, 0.0f, 0.0f, 1.0f,
//                1.0f, 1.0f, 0.0f, 1.0f,
//                1.0f, 1.0f, 1.0f, 1.0f,
//                1.0f, 0.0f, 1.0f, 1.0f,
//
//                // Left face
//                0.0f, 0.0f, 0.0f, 1.0f,
//                0.0f, 0.0f, 1.0f, 1.0f,
//                0.0f, 1.0f, 1.0f, 1.0f,
//                0.0f, 1.0f, 0.0f, 1.0f,
//        };

    }


    private void setPoint_2(){

            vertexPoints= new float[]{
                    // Front face
                    0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f,

                    // Back face
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,

                    // Top face
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 0.0f,

                    // Bottom face
                    0.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,

                    // Right face
                    1.0f, 0.0f, 0.0f,
                    1.0f, 1.0f, 0.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f,

                    // Left face
                    0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 0.0f,
            };



            Colors= new float[]{
                    // Front face
                    0.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,

                    // Back face
                    0.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,

                    // Top face
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,

                    // Bottom face
                    0.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,

                    // Right face
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,

                    // Left face
                    0.0f, 0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
            };
    }


    //drawVolume for opengl es 2.0
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//    public void drawVolume(float[] mvpMatrix, float [] translateAfterMatrix, int width, int height, int texture) {
//
////        int fboBackCoord = initFBO(width, height);
//
////        Log.v("initFBO","success!!!!!!!!!!");
//
//        // Add program to OpenGL ES environment
//        GLES30.glUseProgram(mProgram_simple);
//
//        //绑定帧缓冲id，将对象绑定到环境的帧缓冲单元
//        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboBackCoord);
//
//        GLES30.glClearDepthf(-50.0f);
//        GLES30.glDepthFunc(GLES30.GL_GEQUAL);
//
////        Log.v("before draw","success!!!!!!!!!!");
//
//
//        // first pass draw
//        drawCube(mvpMatrix, translateAfterMatrix, mProgram_simple);
//
////        Log.v("draw","success!!!!!!!!!!");
//
//
//        //解除绑定
//        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
//
//
////        //Add program to OpenGL ES environment
////        GLES30.glUseProgram(mProgram);
////
//////        GLES30.glClearDepthf(-50.0f);
//////        GLES30.glDepthFunc(GLES30.GL_GEQUAL);
////
////        GLES30.glActiveTexture(GLES30.GL_TEXTURE0); //设置使用的纹理编号
////        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fbo_tex[0]); //绑定指定的纹理id
////
////
////        // 将纹理单元传递片段着色器的uVolData
////        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgram,"u_TextureUnit"), 0);
////
////        drawCube_Square(mvpMatrix,mProgram);
////
////        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0); //解除绑定指定的纹理id
//
//
//
//        // Add program to OpenGL ES environment
//        GLES30.glUseProgram(mProgram_raycasting);
//
//        GLES30.glClearDepthf(50.0f);
//        GLES30.glDepthFunc(GLES30.GL_LEQUAL);
//
//
//
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0); //设置使用的纹理编号
////        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,vol_tex[0]); //绑定指定的纹理id
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, vol_tex[0]); //绑定指定的纹理id
//
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE1); //设置使用的纹理编号
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fbo_tex[0]); //绑定指定的纹理id
//
//
//        // 将纹理单元传递片段着色器的uVolData
//        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgram_raycasting,"uVolData"), 0);
//
//        // 将纹理单元传递片段着色器的uBackCoord
//        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgram_raycasting,"uBackCoord"), 1);
//
//        drawCube(mvpMatrix, translateAfterMatrix, mProgram_raycasting);
//
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0); //解除绑定指定的纹理id
//
//
//    }




    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



    public void drawVolume_3d(float[] mvpMatrix, float [] translateAfterMatrix, int width, int height, int texture) {

//        if(count == 0){
//            count ++;
//            initTexture_3d();
//        }


//        int fboBackCoord = initFBO(width, height);

//        Log.v("drawVolume_3d","here we are!!!!!!!!!!");

        // Add program to OpenGL ES environment
        GLES30.glUseProgram(mProgram_simple);

        //绑定帧缓冲id，将对象绑定到环境的帧缓冲单元
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fboBackCoord);

        GLES30.glClearDepthf(-50.0f);
        GLES30.glDepthFunc(GLES30.GL_GEQUAL);

//        Log.v("before draw","success!!!!!!!!!!");


        // first pass draw
        drawCube(mvpMatrix, translateAfterMatrix, mProgram_simple);

//        Log.v("draw","success!!!!!!!!!!");


        //解除绑定
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);


//        //Add program to OpenGL ES environment
//        GLES30.glUseProgram(mProgram);
//
////        GLES30.glClearDepthf(-50.0f);
////        GLES30.glDepthFunc(GLES30.GL_GEQUAL);
//
//        GLES30.glActiveTexture(GLES30.GL_TEXTURE0); //设置使用的纹理编号
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fbo_tex[0]); //绑定指定的纹理id
//
//
//        // 将纹理单元传递片段着色器的uVolData
//        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgram,"u_TextureUnit"), 0);
//
//        drawCube_Square(mvpMatrix,mProgram);
//
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0); //解除绑定指定的纹理id



        // Add program to OpenGL ES environment
        GLES30.glUseProgram(mProgram_raycasting);

        GLES30.glClearDepthf(50.0f);
        GLES30.glDepthFunc(GLES30.GL_LEQUAL);


        dimHandle = GLES30.glGetUniformLocation(mProgram_raycasting, "dim");
        GLES20.glUniform1fv(dimHandle,3, dimBuffer);


        GLES30.glActiveTexture(GLES30.GL_TEXTURE0); //设置使用的纹理编号
        GLES30.glBindTexture(GLES30.GL_TEXTURE_3D, vol_tex[0]); //绑定指定的纹理id

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1); //设置使用的纹理编号
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, fbo_tex[0]); //绑定指定的纹理id


        // 将纹理单元传递片段着色器的uVolData
        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgram_raycasting,"uVolData"), 0);

        // 将纹理单元传递片段着色器的uBackCoord
        GLES30.glUniform1i(GLES30.glGetUniformLocation(mProgram_raycasting,"uBackCoord"), 1);

        drawCube(mvpMatrix, translateAfterMatrix, mProgram_raycasting);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0); //解除绑定指定的纹理id
        GLES30.glBindTexture(GLES30.GL_TEXTURE_3D,0); //解除绑定指定的纹理id



    }





    //用来画线的时候生成轨迹
    public void draw_points(float [] linePoints, int num){

        GLES30.glUseProgram(mProgram_curve);

        BufferSet_Curve(linePoints);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_curve);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        //绘制三个点
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, num);

        //绘制直线
//        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, 2);
//        GLES30.glLineWidth(10);

//        //绘制三角形
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);

    }





    //No Problem
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    private int initFBO(int width, int height){

//        Log.v("Width of screen:",Integer.toString(width));

//        Log.v("Height of screen:",Integer.toString(height));

        GLES30.glGenFramebuffers(  //创建帧缓冲对象
                1, //产生帧缓冲id的数量
                fbo,  //帧缓冲id的数组
                0  //偏移量
        );

        //绑定帧缓冲id，将对象绑定到环境的帧缓冲单元
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,fbo[0]);

        //创建randerbuffer
        GLES30.glGenRenderbuffers(
                1,
                rbo,
                0
        );

        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER,rbo[0]);

        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT16, width, height);

        GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, rbo[0]);


        GLES30.glGenTextures(  //创建纹理对象
                1, //产生纹理id的数量
                fbo_tex, //纹理id的数组
                0  //偏移量
        );


        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,fbo_tex[0]);

        imageBuffer_FBO = CreateBuffer(new byte[width * height * 4]);

        GLES30.glTexImage2D(
                GLES30.GL_TEXTURE_2D,
                0,
                GLES30.GL_RGBA,
                width,
                height,
                0,
                GLES30.GL_RGBA,
                GLES30.GL_UNSIGNED_BYTE,
                imageBuffer_FBO);

        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);//设置MIN 采样方式
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);//设置MAG采样方式
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式

        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, fbo_tex[0], 0);


        int uStatus = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if(uStatus != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.v("ReInitFBO()","glCheckFramebufferStatus=%X");
            return -1;
        }
        return fbo[0];
    }



//
//    //生成二维纹理 for opengl es 2.0~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    private void initTexture(){
//
//        GLES30.glGenTextures(  //创建纹理对象
//                1, //产生纹理id的数量
//                vol_tex, //纹理id的数组
//                0  //偏移量
//        );
//
//        byte [] image_data = getIntensity();
//
//
//        //绑定纹理id，将对象绑定到环境的纹理单元
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,vol_tex[0]);
//
//        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);//设置MIN 采样方式
//        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);//设置MAG采样方式
//        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
//        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式
//
//
//
//        imageBuffer = CreateBuffer(image_data);
//
//
//        GLES30.glTexImage2D(
//                GLES30.GL_TEXTURE_2D, //纹理类型
//                0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
//                GLES30.GL_RGBA, //图片的格式
//                1024,   //
//                2048,   //
//                0, //纹理边框尺寸();
//                GLES30.GL_RGBA,
//                GLES30.GL_UNSIGNED_BYTE,
//                imageBuffer
//        );
//
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
//
//
//    }
//
//
//    //为二维纹理准备数据 for opengl es 2.0~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    //将图片信息存成1024 * 2048的二维图像
//    private byte[] getIntensity(){
//        Rawreader rr = new Rawreader();
//        String fileName = filepath;
//
//        Uri uri = Uri.parse(fileName);
//
//        try {
//            ParcelFileDescriptor parcelFileDescriptor =
//                    getContext().getContentResolver().openFileDescriptor(uri, "r");
//
//            is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//
//            length = (int)parcelFileDescriptor.getStatSize();
//
//            Log.v("MyPattern","Successfully load intensity");
//
//        }catch (Exception e){
//            Log.v("MyPattern","Some problems in the MyPattern when load intensity");
//        }
//
//
//        int[][][] grayscale =  rr.run(length, is);
//        byte[] data_image = new byte[128 * 8 * 128 * 16 * 4];
//
//
//        for(int i = 0; i< 1024 * 2048; i++)
//            data_image[i] = intToByteArray(0)[3];
//
//        //把128个切片排成 8 * 16
//        for(int z = 0; z < 128; z++){
//            int start_x = z / 8 * 128;
//            int start_y = z % 8 * 128;
//
//            for(int x = 0; x < 128; x++){
//                for(int y = 0; y <128; y++){
//                    int pos = ((start_x + y) * 1024 + start_y + x);
//                    data_image[pos * 4] = intToByteArray(grayscale[x][y][z])[3];
//                    data_image[pos * 4 + 1] = intToByteArray(grayscale[x][y][z])[3];
//                    data_image[pos * 4 + 2] = intToByteArray(grayscale[x][y][z])[3];
//                    data_image[pos * 4 + 3] = intToByteArray(1)[3];
//                }
//            }
//        }
//
//        Log.v("Intensity:", Arrays.toString(data_image));
//
//        return data_image;
//    }






    //生成三维纹理 for opengl es 3.0~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initTexture_3d(){
        Log.v("initTexture_3d:", "here we are!!!!!!!");


        GLES30.glGenTextures(  //创建纹理对象
                1, //产生纹理id的数量
                vol_tex, //纹理id的数组
                0  //偏移量
        );

        byte [] image_data = getIntensity_3d();


//        createMarker(image_data, vol_w, vol_h, vol_d, 60, 60, 60);


        //绑定纹理id，将对象绑定到环境的纹理单元
        GLES30.glBindTexture(GLES30.GL_TEXTURE_3D,vol_tex[0]);

        GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D,
                GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);//设置MIN 采样方式
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D,
                GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);//设置MAG采样方式
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D,
                GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D,
                GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_3D,
                GLES30.GL_TEXTURE_WRAP_R,GLES30.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式



        imageBuffer = CreateBuffer(image_data);


        GLES30.glTexImage3D(
                GLES30.GL_TEXTURE_3D, //纹理类型
                0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
                GLES30.GL_RGBA, //图片的格式
                vol_w,   //宽
                vol_h,   //高
                vol_d,   //切片数
                0, //纹理边框尺寸();
                GLES30.GL_RGBA,
                GLES30.GL_UNSIGNED_BYTE,
                imageBuffer
        );

        GLES30.glBindTexture(GLES30.GL_TEXTURE_3D,0);


    }


    //为三维纹理准备数据
    private byte[] getIntensity_3d(){

        Log.v("getIntensity_3d:", "here we are!!!!!!!");

//        Rawreader rr = new Rawreader();
//        String fileName = filepath;
////        int[][][] grayscale= null;
//        Image4DSimple image = new Image4DSimple();
////        Log.v("getIntensity_3d", filepath);
//        image = Image4DSimple.loadImage(filepath);
//        File file = new File(filepath);
//
//        if (file.exists()){
//            try {
//                length = file.length();
//                is = new FileInputStream(file);
////                grayscale =  rr.run(length, is);
//                image = rr.run(length, is);
//
//                Log.v("getIntensity_3d", filepath);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//
//        else {
//            Uri uri = Uri.parse(fileName);
//
//            try {
//                ParcelFileDescriptor parcelFileDescriptor =
//                        getContext().getContentResolver().openFileDescriptor(uri, "r");
//
//                is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//
//                length = (int)parcelFileDescriptor.getStatSize();
//
//                Log.v("MyPattern","Successfully load intensity");
//
//            }catch (Exception e){
//                Log.v("MyPattern","Some problems in the MyPattern when load intensity");
//            }
//
//
//             image =  rr.run(length, is);
//
//        }

//
//        vol_w = rr.get_w();
//        vol_h = rr.get_h();
//        vol_d = rr.get_d();
        vol_w = (int)image.getSz0();
        vol_h = (int)image.getSz1();
        vol_d = (int)image.getSz2();
//        vol_w = 128;
//        vol_h = 128;
//        vol_d = 128;


        Log.v("vol_w", Integer.toString(vol_w));
        Log.v("vol_h", Integer.toString(vol_h));
        Log.v("vol_d", Integer.toString(vol_d));

        int [][][][] grayscale = image.getData();

        byte[] data_image = new byte[vol_w * vol_h * vol_d * 4];

        for (int z = 0; z < vol_d; ++z){

            int layeroffset = vol_h * vol_w;

            for (int y = 0; y < vol_h; ++y){
                for (int x = 0; x < vol_w; x++) {
                    data_image[(layeroffset * z + vol_w * y + x) * 4] = intToByteArray(grayscale[0][x][y][z])[3];
                    data_image[(layeroffset * z + vol_w * y + x) * 4 + 1] = intToByteArray(grayscale[0][x][y][z])[3];
                    data_image[(layeroffset * z + vol_w * y + x) * 4 + 2] = intToByteArray(grayscale[0][x][y][z])[3];
                    data_image[(layeroffset * z + vol_w * y + x) * 4 + 3] = intToByteArray(1)[3];

//                    if(grayscale[x][y][z] > 50){
//                        Log.v("intensity: ", Integer.toString(grayscale[x][y][z]));
//                    }

                }
            }
        }

//        for (int z = 0; z < vol_d; ++z){
//
//            int layeroffset = vol_h * vol_d;
//
//            for (int y = 0; y < vol_h; ++y){
//                for (int x = 0; x < vol_w; x++) {
//                    data_image[(layeroffset * x + vol_d * y + z) * 4] = intToByteArray(grayscale[0][z][y][x])[3];
//                    data_image[(layeroffset * x + vol_d * y + z) * 4 + 1] = intToByteArray(grayscale[0][z][y][x])[3];
//                    data_image[(layeroffset * x + vol_d * y + z) * 4 + 2] = intToByteArray(grayscale[0][z][y][x])[3];
//                    data_image[(layeroffset * x + vol_d * y + z) * 4 + 3] = intToByteArray(1)[3];
//
////                    if(grayscale[x][y][z] > 50){
////                        Log.v("intensity: ", Integer.toString(grayscale[x][y][z]));
////                    }
//
//                }
//            }
//        }


        return data_image;
    }





    //No Problem
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    //int转byte
    private static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~





    public void drawCube(float[] mvpMatrix, float[] translateAfterMatrix, int mProgram) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);


//        Log.v("mProgram()",Integer.toString(mProgram));
//
//        Log.v("before gethandle","success!!!!!!!!!!");

        // get the common handle
        getHandle(mProgram);

//        Log.v("gethandle","success!!!!!!!!!!");


        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
//        GLES30.glUniformMatrix4fv(trAMatrixHandle, 1, false, translateAfterMatrix, 0);
        // 准备坐标数据
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);

        // 准备颜色数据
        GLES30.glVertexAttribPointer(colorHandle, 4, GLES30.GL_FLOAT, false, 0, colorBuffer);

        // 通过索引来绘制
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 36, GLES30.GL_UNSIGNED_SHORT, drawListBuffer);

        // 禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(positionHandle);
        GLES30.glDisableVertexAttribArray(colorHandle);

    }




//    public void drawCube_Square(float[] mvpMatrix, int mProgram) {
//
//        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
//
//
////        Log.v("mProgram()",Integer.toString(mProgram));
////
////        Log.v("before gethandle","success!!!!!!!!!!");
//
//        // get the common handle
//        getHandle_Square(mProgram);
//
////        Log.v("gethandle","success!!!!!!!!!!");
//
//
//        // Pass the projection and view transformation to the shader
//        GLES30.glUniformMatrix4fv(vPMatrixHandle_square, 1, false, mvpMatrix, 0);
//
//        // 准备坐标数据
//        GLES30.glVertexAttribPointer(positionHandle_suqre, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_suqre);
//
//        // 准备颜色数据
//        GLES30.glVertexAttribPointer(colorHandle_square, 2, GLES30.GL_FLOAT, false, 0, colorBuffer_suqre);
//
//        // 通过索引来绘制
//        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_SHORT, drawListBuffer_suqre);
//
//        // 禁止顶点数组的句柄
//        GLES30.glDisableVertexAttribArray(positionHandle_suqre);
//        GLES30.glDisableVertexAttribArray(colorHandle_square);
//
//    }






    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    private void BufferSet(){
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);


        //分配内存空间,每个浮点型占4字节空间
        colorBuffer = ByteBuffer.allocateDirect(Colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的颜色数据
        colorBuffer.put(Colors);
        colorBuffer.position(0);


        //分配内存空间,每个short型占4字节空间
        drawListBuffer = ByteBuffer.allocateDirect(drawlist.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的索引数据
        drawListBuffer.put(drawlist);
        drawListBuffer.position(0);


        //分配内存空间,每个浮点型占4字节空间
        dimBuffer = ByteBuffer.allocateDirect(dim.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的颜色数据
        dimBuffer.put(dim);
        dimBuffer.position(0);

    }


    private void BufferSet_Curve(float [] vertexPoints_curve){
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_curve = ByteBuffer.allocateDirect(vertexPoints_curve.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_curve.put(vertexPoints_curve);
        vertexBuffer_curve.position(0);
    }




    private ByteBuffer CreateBuffer(byte[] data){

        ByteBuffer templateBuffer;
        //分配内存空间,每个字节型占1字节空间
        templateBuffer = ByteBuffer.allocateDirect(data.length)
                .order(ByteOrder.nativeOrder());
        //传入指定的坐标数据
        templateBuffer.put(data);
        templateBuffer.position(0);
        return templateBuffer;

    }




//    private void BufferSet_Square(){
//        //分配内存空间,每个浮点型占4字节空间
//        vertexBuffer_suqre = ByteBuffer.allocateDirect(vertexPoints_square.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        //传入指定的坐标数据
//        vertexBuffer_suqre.put(vertexPoints_square);
//        vertexBuffer_suqre.position(0);
//
//
//        //分配内存空间,每个浮点型占4字节空间
//        colorBuffer_suqre = ByteBuffer.allocateDirect(TexCoord_square.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        //传入指定的颜色数据
//        colorBuffer_suqre.put(TexCoord_square);
//        colorBuffer_suqre.position(0);
//
//
//        //分配内存空间,每个short型占4字节空间
//        drawListBuffer_suqre = ByteBuffer.allocateDirect(drawlist_quare.length * 2)
//                .order(ByteOrder.nativeOrder())
//                .asShortBuffer();
//        //传入指定的索引数据
//        drawListBuffer_suqre.put(drawlist_quare);
//        drawListBuffer_suqre.position(0);
//    }




    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



    private void getHandle_Square(int mProgram){

//        Log.v("mProgram()",Integer.toString(mProgram));


        //get handle to vertex shader's vPosition member
        //启用顶点的句柄
        positionHandle_suqre = GLES30.glGetAttribLocation(mProgram, "a_position");
        GLES30.glEnableVertexAttribArray(positionHandle_suqre);
//        Log.v("positionHandle", Integer.toString(positionHandle));

        //get handle to vertex shader's vPosition member
        //启用纹理的句柄
        colorHandle_square = GLES30.glGetAttribLocation(mProgram, "a_TexCoord");
        GLES30.glEnableVertexAttribArray(colorHandle_square);
//        Log.v("colorHandle", Integer.toString(colorHandle));

        // get handle to vertex shader's uMVPMatrix member
        vPMatrixHandle_square = GLES30.glGetUniformLocation(mProgram,"uMVPMatrix");
    }







//    private void getHandle(int mProgram){
//
//        Log.v("mProgram()",Integer.toString(mProgram));
//
//
//        //get handle to vertex shader's vPosition member
//        //启用顶点的句柄
//        positionHandle = GLES30.glGetAttribLocation(mProgram, "a_position");
//        GLES30.glEnableVertexAttribArray(positionHandle);
//        Log.v("positionHandle", Integer.toString(positionHandle));
//
//        //get handle to vertex shader's vPosition member
//        //启用纹理的句柄
//        colorHandle = GLES30.glGetAttribLocation(mProgram, "a_color");
//        GLES30.glEnableVertexAttribArray(colorHandle);
//        Log.v("colorHandle", Integer.toString(colorHandle));
//
//        // get handle to vertex shader's uMVPMatrix member
//        vPMatrixHandle = GLES30.glGetUniformLocation(mProgram,"uMVPMatrix");
//
////        trAMatrixHandle = GLES30.glGetUniformLocation(mProgram, "trAMatrix");
//    }






    private void getHandle(int mProgram){

//        Log.v("mProgram()",Integer.toString(mProgram));


        //get handle to vertex shader's vPosition member
        //启用顶点的句柄
//        positionHandle = 0;
        GLES30.glEnableVertexAttribArray(positionHandle);
//        Log.v("positionHandle", Integer.toString(positionHandle));

        //get handle to vertex shader's vPosition member
        //启用纹理的句柄
//        colorHandle = 1;
        GLES30.glEnableVertexAttribArray(colorHandle);
//        Log.v("colorHandle", Integer.toString(colorHandle));

        // get handle to vertex shader's uMVPMatrix member
        vPMatrixHandle = GLES30.glGetUniformLocation(mProgram,"uMVPMatrix");

//        trAMatrixHandle = GLES30.glGetUniformLocation(mProgram, "trAMatrix");
    }















    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //创建着色器程序
    private int initProgram(String vertShaderCode, String fragmShaderCode){

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





    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //加载着色器
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



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //设置文件路径
    private void SetPath(String message){

        filepath = message;
    }



    //设置文件路径
    private void setInputStream(InputStream Is){
        is = Is;
    }

    private void setLength(long Length){
        length = Length;
    }



    private void createMarker(byte [] data_image, int vol_w, int vol_h, int vol_d, int cx, int cy, int cz){
        float step = 5.0f;
//        ArrayList<Float> data=new ArrayList<>();
        float r1;
        float h1;
        float sin,cos;
        for(float i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0);
            h1 = (float)Math.sin(i * Math.PI / 180.0);
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (float j = 0.0f; j <360.0f+step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);
                for (float r = 0; r < 3.0f; r = r + 0.5f) {
                    int x = (int) (r1 * cos * r) + cx;
                    int y = (int) (r1 * sin * r) + cy;
                    int z = (int) (h1 * r) + cz;
                    data_image[(vol_w * vol_h * z + vol_w * y + x) * 4] = intToByteArray(255)[3];
                    data_image[(vol_w * vol_h * z + vol_w * y + x) * 4 + 1] = intToByteArray(0)[3];
                    data_image[(vol_w * vol_h * z + vol_w * y + x) * 4 + 2] = intToByteArray(0)[3];
                    data_image[(vol_w * vol_h * z + vol_w * y + x) * 4 + 3] = intToByteArray(1)[3];
                }
            }
        }
    }


}