package com.example.myapplication__volume;

import android.net.Uri;
import android.opengl.GLES20;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import static com.example.myapplication__volume.MainActivity.getContext;


public class MyPattern{

    private final int scale = 128;

    //两个人shaderprogram
    private final int mProgram_simple;
    private final int mProgram_raycasting;
    private final int mProgram;

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ShortBuffer drawListBuffer;

    private FloatBuffer vertexBuffer_suqre;
    private FloatBuffer colorBuffer_suqre;
    private ShortBuffer drawListBuffer_suqre;

    private int positionHandle;
    private int colorHandle;
    private int vPMatrixHandle;
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



    private final float[] vertexPoints={
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


    private final float[] Colors={
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

    private final short[] drawlist = {

            0, 1, 2,      0, 2, 3,    // Front face
            4, 5, 6,      4, 6, 7,    // Back face
            8, 9, 10,     8, 10, 11,  // Top face
            12, 13, 14,   12, 14, 15, // Bottom face
            16, 17, 18,   16, 18, 19, // Right face
            20, 21, 22,   20, 22, 23  // Left face

    };





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





    private final String vertexShaderCode =
            "attribute vec4 a_position;" +
                    "attribute vec4 a_color;" +
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec2 a_TexCoord;" +
                    "varying vec2 v_TexCoord;" +

                    "void main(){" +
                    "   gl_Position = uMVPMatrix * a_position;" +
                    "   v_TexCoord = a_TexCoord;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +

                    "varying vec2 v_TexCoord;" +
                    "uniform sampler2D u_TextureUnit;" +

                    "void main(){" +
                    "   gl_FragColor = texture2D(u_TextureUnit, v_TexCoord);" +
                    "}";








    private final String vertexShaderCode_1 =
            "attribute vec4 a_position;" +
                    "attribute vec4 a_color;" +
                    "uniform mat4 uMVPMatrix;" +

                    "varying vec4 backColor;" +

                    "void main() {" +
                    "  gl_Position = uMVPMatrix * a_position;" +
                    "  backColor = a_color;" +
                    "}";




    private final String fragmentShaderCode_1 =
            "precision mediump float;" +

                    "varying vec4 backColor;" +

                    "void main() {" +
                    "   gl_FragColor = backColor;" +
                    "}";








    private final String vertexShaderCode_2 =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
                    "precision highp float;" +
                    "uniform mat4 uMVPMatrix;" +
//                    "uniform mat4 trAMatrix;" +
                    "attribute vec4 a_color;" +
                    "attribute vec4 a_position;" +

                    "varying vec4 pos;" +
                    "varying vec4 frontColor;"+

                    "void main() {" +
                    "  pos = uMVPMatrix * a_position;"+
                    "  gl_Position = pos;" +
//                    "  gl_Position = trAMatrix * pos;" +
                    "  frontColor = a_color;" +
                    "}";



    private final String fragmentShaderCode_2 =
            "precision highp float;" +

                    "varying vec4 frontColor;" +
                    "varying vec4 pos;" +

                    "uniform sampler2D uBackCoord;" +
                    "uniform sampler2D uVolData;" +
                    "uniform sampler2D uTransferFunction;" +

//                    "const float steps = 40.0;" +
                    "const float numberOfSlices = 128.0;" +
                    "const float slicesOverX = 8.0;" +
                    "const float slicesOverY = 16.0;" +

                    "float getVolumeValue(vec3 volpos)" +
                    "{" +
                    "  float s1,s2;" +
                    "  float dx1,dy1;" +
                    "  float dx2,dy2;" +
                    "  " +
                    "  vec2 texpos1,texpos2;" +
                    "  " +
                    "  s1 = floor(volpos.z*numberOfSlices);" +
                    "  s2 = s1+1.0;" +
                    "  " +
                    "  dx1 = fract(s1/slicesOverX);" +
                    "  dy1 = floor(s1/slicesOverX)/slicesOverY;" +
                    "  " +
                    "  dx2 = fract(s2/slicesOverX);" +
                    "  dy2 = floor(s2/slicesOverX)/slicesOverY;" +

                    "  texpos1.x = dx1+(volpos.x/slicesOverX);" +
                    "  texpos1.y = dy1+(volpos.y/slicesOverY);" +
                    "  " +
                    "  texpos2.x = dx2+(volpos.x/slicesOverX);" +
                    "  texpos2.y = dy2+(volpos.y/slicesOverY);" +
                    "  " +
                    "  return mix( texture2D(uVolData,texpos1).x, texture2D(uVolData,texpos2).x, (volpos.z*numberOfSlices)-s1);\n" +
//                    "  return texture2D(uVolData,texpos1).x;" +
//                    "  return max(texture2D(uVolData,texpos1).x, texture2D(uVolData,texpos2).x);" +
                    "}" +

                    "void main(void)" +
                    "{" +
                    "  vec2 texC = pos.xy/pos.w;" +
                    "  texC.x = 0.5*texC.x + 0.5;" +
                    "  texC.y = 0.5*texC.y + 0.5;" +
                    "  " +
                    "  vec4 backColor = texture2D(uBackCoord,texC);" +
                    "  " +
                    "  vec3 dir = backColor.rgb - frontColor.rgb;" +
//                    "  float steps = floor(length(dir) * numberOfSlices);" +
                    "  float steps = 50.0;" +
                    "  vec4 vpos = frontColor;" +
                    "  " +
                    "  float cont = 0.0;" +
                    "  " +
                    "  vec3 Step = dir/steps;" +
                    "  " +
                    "  vec4 accum = vec4(0, 0, 0, 0);" +
                    "  vec4 sample = vec4(0.0, 0.0, 0.0, 0.0);" +
                    "  vec4 value = vec4(0, 0, 0, 0);" +
                    "  " +
                    "  float opacityFactor = 8.0;" +
                    "  float lightFactor = 1.3;" +
                    "  " +
                    "  for(float i = 0.0; i < steps; i+=1.0)" +
                    "  {" +
                    "     vec2 tf_pos;" +

                    "     tf_pos.x = getVolumeValue(vpos.xyz);" +
                    "     tf_pos.y = 0.5;" +
                    "     " +
                    "     value = vec4(tf_pos.x);" +

                    "     if(value.r > accum.r)\n" +
                    "         accum.rgb = value.rgb;" +
                    "     accum.a += (1.0 - accum.a) * value.a;" +
                    "     vpos.xyz += Step;" +

                    "     if(vpos.x > 1.0 || vpos.y > 1.0 || vpos.z > 1.0 || accum.a>=1.0)" +
                    "         break;" +
                    "     }" +
                    "  accum.a = 1.0;" +
                    "  gl_FragColor = accum;" +
                    "}";


//    private final String fragmentShaderCode_2 =
//            "precision mediump float;" +
//
//                    "varying vec4 frontColor;" +
//                    "varying vec4 pos;" +
//
//                    "uniform sampler2D uBackCoord;" +
//                    "uniform sampler2D uVolData;" +
//                    "uniform sampler2D uTransferFunction;" +
//
//                    "const float steps = 50.0;" +
//                    "const float numberOfSlices = 96.0;" +
//                    "const float slicesOverX = 10.0;" +
//                    "const float slicesOverY = 10.0;" +
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
//                    "  dy1 = floor(s1/slicesOverY)/slicesOverY;" +
//                    "  " +
//                    "  dx2 = fract(s2/slicesOverX);" +
//                    "  dy2 = floor(s2/slicesOverY)/slicesOverY;" +
//
//                    "  texpos1.x = dx1+(volpos.x/slicesOverX);" +
//                    "  texpos1.y = dy1+(volpos.y/slicesOverY);" +
//                    "  " +
//                    "  texpos2.x = dx2+(volpos.x/slicesOverX);" +
//                    "  texpos2.y = dy2+(volpos.y/slicesOverY);" +
//                    "  " +
//                    "  return mix( texture2D(uVolData,texpos1).x, texture2D(uVolData,texpos2).x, (volpos.z*numberOfSlices)-s1);\n" +
//                    "}" +
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
//                    "     vec2 tf_pos;" +
//
//                    "     tf_pos.x = getVolumeValue(vpos.xyz);" +
//                    "     tf_pos.y = 0.5;" +
//                    "     " +
//                    "     value = vec4(tf_pos.x);" +
//
//                    "     sample.a = value.a * opacityFactor * (1.0/steps);" +
//                    "     sample.rgb = value.rgb * sample.a * lightFactor;" +
//                    "     " +
//                    "     accum.rgb += (1.0 - accum.a) * sample.rgb;" +
//                    "     accum.a += sample.a;" +
//
//                    "     vpos.xyz += Step;" +
//
//                    "     if(vpos.x > 1.0 || vpos.y > 1.0 || vpos.z > 1.0 || accum.a>=1.0)" +
//                    "         break;" +
//
//
//                    "     }" +
//                    "  " +
//                    "  gl_FragColor = accum;" +
//                    "}";



    public MyPattern(String filepath, InputStream is, long length, int width, int height) {

        //创建两个着色器程序
        mProgram_simple = initProgram(vertexShaderCode_1, fragmentShaderCode_1);

        Log.v("mProgram_simple", Integer.toString(mProgram_simple));

        mProgram_raycasting = initProgram(vertexShaderCode_2, fragmentShaderCode_2);

        Log.v("mProgram_raycasting", Integer.toString(mProgram_raycasting));


        mProgram = initProgram(vertexShaderCode, fragmentShaderCode);

//        Log.v("mProgram()",Integer.toString(mProgram));

        //设置一下buffer
        BufferSet();

        BufferSet_Square();

        //设置文件路径
        SetPath(filepath);
//        setInputStream(is);
//        setLength(length);

        //加载纹理
        initTexture();

        Log.v("init()","success!!!!!!!!!!!!!!!");

        fboBackCoord = initFBO(width, height);

    }

//    public void drawVolume(float[] mvpMatrix, int width, int height, int texture){
//
//        //Add program to OpenGL ES environment
//        GLES20.glUseProgram(mProgram);
//
////        GLES20.glClearDepthf(-50.0f);
////        GLES20.glDepthFunc(GLES20.GL_GEQUAL);
//
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //设置使用的纹理编号
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture); //绑定指定的纹理id
//
//
//        // 将纹理单元传递片段着色器的uVolData
//        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgram,"u_TextureUnit"), 0);
//
//        drawCube_Square(mvpMatrix,mProgram);
//
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0); //解除绑定指定的纹理id
//    }




    public void drawVolume(float[] mvpMatrix, float [] translateAfterMatrix, int width, int height, int texture) {

//        int fboBackCoord = initFBO(width, height);

//        Log.v("initFBO","success!!!!!!!!!!");

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram_simple);

        //绑定帧缓冲id，将对象绑定到环境的帧缓冲单元
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBackCoord);

        GLES20.glClearDepthf(-50.0f);
        GLES20.glDepthFunc(GLES20.GL_GEQUAL);

//        Log.v("before draw","success!!!!!!!!!!");


        // first pass draw
        drawCube(mvpMatrix, translateAfterMatrix, mProgram_simple);

//        Log.v("draw","success!!!!!!!!!!");


        //解除绑定
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);


//        //Add program to OpenGL ES environment
//        GLES20.glUseProgram(mProgram);
//
////        GLES20.glClearDepthf(-50.0f);
////        GLES20.glDepthFunc(GLES20.GL_GEQUAL);
//
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //设置使用的纹理编号
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fbo_tex[0]); //绑定指定的纹理id
//
//
//        // 将纹理单元传递片段着色器的uVolData
//        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgram,"u_TextureUnit"), 0);
//
//        drawCube_Square(mvpMatrix,mProgram);
//
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0); //解除绑定指定的纹理id



        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram_raycasting);

        GLES20.glClearDepthf(50.0f);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);



        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //设置使用的纹理编号
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,vol_tex[0]); //绑定指定的纹理id
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, vol_tex[0]); //绑定指定的纹理id

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1); //设置使用的纹理编号
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fbo_tex[0]); //绑定指定的纹理id


        // 将纹理单元传递片段着色器的uVolData
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgram_raycasting,"uVolData"), 0);

        // 将纹理单元传递片段着色器的uBackCoord
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mProgram_raycasting,"uBackCoord"), 1);

        drawCube(mvpMatrix, translateAfterMatrix, mProgram_raycasting);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0); //解除绑定指定的纹理id


    }


    private int initFBO(int width, int height){

//        Log.v("Width of screen:",Integer.toString(width));

//        Log.v("Height of screen:",Integer.toString(height));

        GLES20.glGenFramebuffers(  //创建帧缓冲对象
                1, //产生帧缓冲id的数量
                fbo,  //帧缓冲id的数组
                0  //偏移量
        );

        //绑定帧缓冲id，将对象绑定到环境的帧缓冲单元
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,fbo[0]);

        //创建randerbuffer
        GLES20.glGenRenderbuffers(
                1,
                rbo,
                0
        );

        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,rbo[0]);

        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);

        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, rbo[0]);


        GLES20.glGenTextures(  //创建纹理对象
                1, //产生纹理id的数量
                fbo_tex, //纹理id的数组
                0  //偏移量
        );


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,fbo_tex[0]);

        imageBuffer_FBO = CreateBuffer(new byte[width * height * 4]);

        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                width,
                height,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                imageBuffer_FBO);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);//设置MIN 采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);//设置MAG采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fbo_tex[0], 0);


        int uStatus = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if(uStatus != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.v("ReInitFBO()","glCheckFramebufferStatus=%X");
            return -1;
        }
        return fbo[0];
    }




    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void initTexture(){

        GLES20.glGenTextures(  //创建纹理对象
                1, //产生纹理id的数量
                vol_tex, //纹理id的数组
                0  //偏移量
        );

        byte [] image_data = getIntensity();


        //绑定纹理id，将对象绑定到环境的纹理单元
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,vol_tex[0]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);//设置MIN 采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);//设置MAG采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式



        imageBuffer = CreateBuffer(image_data);


        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D, //纹理类型
                0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
                GLES20.GL_RGBA, //图片的格式
                1024,   //
                2048,   //
                0, //纹理边框尺寸();
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                imageBuffer
        );

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);


    }


    //将图片信息存成1024 * 2048的二维图像
    private byte[] getIntensity(){
        rawreader rr = new rawreader();
        String fileName = filepath;

        Uri uri = Uri.parse(fileName);

        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContext().getContentResolver().openFileDescriptor(uri, "r");

            is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);

            length = (int)parcelFileDescriptor.getStatSize();

            Log.v("MyPattern","Successfully load intensity");

        }catch (Exception e){
            Log.v("MyPattern","Some problems in the MyPattern when load intensity");
        }


        int[][][] grayscale =  rr.run(length, is);
        byte[] data_image = new byte[128 * 8 * 128 * 16 * 4];


        for(int i = 0; i< 1024 * 2048; i++)
            data_image[i] = intToByteArray(0)[3];

        //把128个切片排成 8 * 16
        for(int z = 0; z < 128; z++){
            int start_x = z / 8 * 128;
            int start_y = z % 8 * 128;

            for(int x = 0; x < 128; x++){
                for(int y = 0; y <128; y++){
                    int pos = ((start_x + y) * 1024 + start_y + x);
//                    if (z == 60){
//                        data_image[pos * 4] = intToByteArray(255)[3];
//                        data_image[pos * 4 + 1] = intToByteArray(255)[3];
//                        data_image[pos * 4 + 2] = intToByteArray(255)[3];
//                        data_image[pos * 4 + 3] = intToByteArray(1)[3];
//                    }
//                    else{
//                        data_image[pos * 4] = intToByteArray(0)[3];
//                        data_image[pos * 4 + 1] = intToByteArray(0)[3];
//                        data_image[pos * 4 + 2] = intToByteArray(0)[3];
//                        data_image[pos * 4 + 3] = intToByteArray(1)[3];
//                    }
                    data_image[pos * 4] = intToByteArray(grayscale[x][y][z])[3];
                    data_image[pos * 4 + 1] = intToByteArray(grayscale[x][y][z])[3];
                    data_image[pos * 4 + 2] = intToByteArray(grayscale[x][y][z])[3];
                    data_image[pos * 4 + 3] = intToByteArray(1)[3];
                }
            }
        }

        Log.v("Intensity:", Arrays.toString(data_image));

        return data_image;
    }




    //int转byte
    private static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }





    public void drawCube(float[] mvpMatrix, float[] translateAfterMatrix, int mProgram) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


//        Log.v("mProgram()",Integer.toString(mProgram));
//
//        Log.v("before gethandle","success!!!!!!!!!!");

        // get the common handle
        getHandle(mProgram);

//        Log.v("gethandle","success!!!!!!!!!!");


        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
//        GLES20.glUniformMatrix4fv(trAMatrixHandle, 1, false, translateAfterMatrix, 0);
        // 准备坐标数据
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        // 准备颜色数据
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        // 通过索引来绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // 禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);

    }



    public void drawCube_Square(float[] mvpMatrix, int mProgram) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


//        Log.v("mProgram()",Integer.toString(mProgram));
//
//        Log.v("before gethandle","success!!!!!!!!!!");

        // get the common handle
        getHandle_Square(mProgram);

//        Log.v("gethandle","success!!!!!!!!!!");


        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle_square, 1, false, mvpMatrix, 0);

        // 准备坐标数据
        GLES20.glVertexAttribPointer(positionHandle_suqre, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer_suqre);

        // 准备颜色数据
        GLES20.glVertexAttribPointer(colorHandle_square, 2, GLES20.GL_FLOAT, false, 0, colorBuffer_suqre);

        // 通过索引来绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, drawListBuffer_suqre);

        // 禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(positionHandle_suqre);
        GLES20.glDisableVertexAttribArray(colorHandle_square);

    }




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

    }




    private void BufferSet_Square(){
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_suqre = ByteBuffer.allocateDirect(vertexPoints_square.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_suqre.put(vertexPoints_square);
        vertexBuffer_suqre.position(0);


        //分配内存空间,每个浮点型占4字节空间
        colorBuffer_suqre = ByteBuffer.allocateDirect(TexCoord_square.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的颜色数据
        colorBuffer_suqre.put(TexCoord_square);
        colorBuffer_suqre.position(0);


        //分配内存空间,每个short型占4字节空间
        drawListBuffer_suqre = ByteBuffer.allocateDirect(drawlist_quare.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的索引数据
        drawListBuffer_suqre.put(drawlist_quare);
        drawListBuffer_suqre.position(0);
    }




    private void getHandle_Square(int mProgram){

//        Log.v("mProgram()",Integer.toString(mProgram));


        //get handle to vertex shader's vPosition member
        //启用顶点的句柄
        positionHandle_suqre = GLES20.glGetAttribLocation(mProgram, "a_position");
        GLES20.glEnableVertexAttribArray(positionHandle_suqre);
//        Log.v("positionHandle", Integer.toString(positionHandle));

        //get handle to vertex shader's vPosition member
        //启用纹理的句柄
        colorHandle_square = GLES20.glGetAttribLocation(mProgram, "a_TexCoord");
        GLES20.glEnableVertexAttribArray(colorHandle_square);
//        Log.v("colorHandle", Integer.toString(colorHandle));

        // get handle to vertex shader's uMVPMatrix member
        vPMatrixHandle_square = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
    }



    private void getHandle(int mProgram){

//        Log.v("mProgram()",Integer.toString(mProgram));


        //get handle to vertex shader's vPosition member
        //启用顶点的句柄
        positionHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
        GLES20.glEnableVertexAttribArray(positionHandle);
//        Log.v("positionHandle", Integer.toString(positionHandle));

        //get handle to vertex shader's vPosition member
        //启用纹理的句柄
        colorHandle = GLES20.glGetAttribLocation(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(colorHandle);
//        Log.v("colorHandle", Integer.toString(colorHandle));

        // get handle to vertex shader's uMVPMatrix member
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");

//        trAMatrixHandle = GLES20.glGetUniformLocation(mProgram, "trAMatrix");
    }




    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //创建着色器程序
    private int initProgram(String vertShaderCode, String fragmShaderCode){

        //加载着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmShaderCode);

        Log.v("vertexShader", Integer.toString(vertexShader));

        Log.v("fragmentShader", Integer.toString(fragmentShader));


        // create empty OpenGL ES Program
        int mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);


        GLES20.glValidateProgram(mProgram); // 让OpenGL来验证一下我们的shader program，并获取验证的状态
        int[] status = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_VALIDATE_STATUS, status, 0);                                  // 获取验证的状态
        Log.d("Program:", "validate shader----program: " + GLES20.glGetProgramInfoLog(mProgram));

        return mProgram;

    }



    //加载着色器
    private static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);// 获取验证的状态
//        if (status[0] == 0) {
//            String error = GLES20.glGetShaderInfoLog(shader);
//            GLES20.glDeleteShader(shader);
//            Log.v("Error:", "validate shader program: " + error);
//        }
        Log.v("Error:", "validate shader program: " + GLES20.glGetShaderInfoLog(shader));


        return shader;
    }



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

}