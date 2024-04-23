package com.penglab.hi5.core.render.pattern;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLES32;
import android.util.Log;

import com.penglab.hi5.basic.ByteTranslate;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.MyRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Collections;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.getContext;
import static com.penglab.hi5.core.render.pattern.ShaderHelper.initShaderProgram;


public class MyPattern extends BasicPattern {
    private static final String TAG = "MyPattern";

    //两个shader program
    private static int mProgram_simple;
    private static int mProgram_raycasting;

    private FloatBuffer vertexBuffer;
    private FloatBuffer vertexPreBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer colorPreBuffer;
    private ShortBuffer drawListBuffer;
    private ShortBuffer drawListPreBuffer;
    private FloatBuffer dimBuffer;

    private ByteBuffer imageBuffer;
    private ByteBuffer imageDSBuffer;
    private ShortBuffer imageShortBuffer;
    private ShortBuffer imageShortDSBuffer;
    private IntBuffer imageIntBuffer;
    private IntBuffer imageIntDSBuffer;
    private ByteBuffer imageBuffer_FBO;

    private int positionHandle = 0;
    private int colorHandle = 1;
    private int vPMatrixHandle;
    private int dimHandle;
    private int contrastHandle;
    private ByteTranslate byteTranslate;

    private int[] vol_tex = new int[1]; //生成纹理id;
    private int[] fbo_tex = new int[1]; //生成纹理id;
    private int[] vol_texDS = new int[1];
    private int[] fbo = new int[1];//生成framebuffer
    private int[] rbo = new int[1];//生成renderbuffer

    // cut value
    private float cutx_left_value;
    private float cutx_right_value;
    private float cuty_left_value;
    private float cuty_right_value;
    private float cutz_left_value;
    private float cutz_right_value;


    private int fboBackCoord;

    private int vol_w;
    private int vol_h;
    private int vol_d;
    private int vol_wDS;
    private int vol_hDS;
    private int vol_dDS;
    private int data_length;
    private int nchannel;
    private boolean isBig;

    private int downSampleScale = 5;

    private Image4DSimple image;

    private float[] vertexPoints;
    private float[] vertexPointsPre;
    private float[] Colors;
    private float[] ColorsPre;
    private float[] dim;

    private int threshold;

    private boolean ifGame = false;

    public void setCutx_left_value(float cutx_left_value) {
        this.cutx_left_value = cutx_left_value;
    }

    public void setCutx_right_value(float cutx_right_value) {
        this.cutx_right_value = cutx_right_value;
    }

    public void setCuty_left_value(float cuty_left_value) {
        this.cuty_left_value = cuty_left_value;
    }

    public void setCuty_right_value(float cuty_right_value) {
        this.cuty_right_value = cuty_right_value;
    }

    public void setCutz_left_value(float cutz_left_value) {
        this.cutz_left_value = cutz_left_value;
    }

    public void setCutz_right_value(float cutz_right_value) {
        this.cutz_right_value = cutz_right_value;
    }

    public static enum Mode {NORMAL, GAME}

    ;
    private Mode mode = Mode.NORMAL;

    private final short[] drawlist = {

            0, 1, 2, 0, 2, 3,    // Front face
            4, 5, 6, 4, 6, 7,    // Back face
            8, 9, 10, 8, 10, 11,  // Top face
            12, 13, 14, 12, 14, 15, // Bottom face
            16, 17, 18, 16, 18, 19, // Right face
            20, 21, 22, 20, 22, 23  // Left face

    };

    private final short[] drawlistPre = {

            0, 1, 2, 0, 2, 3,    // Front face
            4, 5, 6, 4, 6, 7,    // Back face
            8, 9, 10, 8, 10, 11,  // Top face
            12, 13, 14, 12, 14, 15, // Bottom face
            16, 17, 18, 16, 18, 19, // Right face
            20, 21, 22, 20, 22, 23  // Left face

    };

    private int drawlistLength = 36;


    // opengl es 3.0 ------------------------------------------------------------------------------


    private static final String vertexShaderCode_1 =
            "#version 320 es\n" +
                    "layout (location = 0) in vec4 a_position;" +
                    "layout (location = 1) in vec4 a_color;" +
                    "uniform mat4 uMVPMatrix;" +

                    "out vec4 pos;" +
                    "out vec4 backColor;" +

                    "void main() {" +
                    "  gl_Position = uMVPMatrix * a_position;" +
                    "  backColor = a_color;" +
                    "}";


    private static final String fragmentShaderCode_1 =
            "#version 320 es\n" +
                    "precision mediump float;" +

                    "uniform highp float cutx_left;" +
                    "uniform highp float cutx_right;" +
                    "uniform highp float cuty_left;" +
                    "uniform highp float cuty_right;" +
                    "uniform highp float cutz_left;" +
                    "uniform highp float cutz_right;" +

                    "in vec4 backColor;" +
                    "in vec4 pos;" +

                    "layout (location = 0) out vec4 fragColor;" +

                    "void main() {" +

//                    "   // Check if the current position is within the cut-off values\n"+
//                    "   if((pos.x) < cutx_left || (pos.x) > cutx_right || (pos.y) < cuty_left || (pos.y) > cuty_right || (pos.z) < cutz_left || (pos.z) > cutz_right) "+
//                    "   {"+
//                    "        fragColor = vec4(0.0, 0.0, 0.0, 0.0); // Set to transparent\n"+
//                    "        return;"+
//                    "   }"+

                    "   fragColor = backColor;" +
                    "}";

    private static final String vertexShaderCode_2 =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "#version 320 es\n" +
                    "precision highp float;" +
                    "uniform mat4 uMVPMatrix;" +
                    "layout (location = 0) in vec4 a_position;" +
                    "layout (location = 1) in vec4 a_color;" +

                    "out vec4 pos;" +
                    "out vec4 frontColor;" +

                    "void main() {" +
                    "  pos = uMVPMatrix * a_position;" +
                    "  gl_Position = pos;" +
                    "  frontColor = a_color;" +
                    "}";

    private static final String fragmentShaderCode_2 =
            "#version 320 es\n" +
                    "precision highp float;" +

                    "in vec4 frontColor;" +
                    "in vec4 pos;" +

                    "layout (binding = 0) uniform highp sampler3D uVolData;" +
                    "layout (binding = 1) uniform sampler2D uBackCoord;" +

                    "uniform highp float dim[3];" +
                    "uniform highp float contrast;" +

                    "uniform highp float cutx_left;" +
                    "uniform highp float cutx_right;" +
                    "uniform highp float cuty_left;" +
                    "uniform highp float cuty_right;" +
                    "uniform highp float cutz_left;" +
                    "uniform highp float cutz_right;" +

                    "layout (location = 0) out vec4 fragColor;" +

                    "void main(void)" +
                    "{" +
                    "  vec2 texC = pos.xy/pos.w;" +
                    "  texC.x = 0.5*texC.x + 0.5;" +
                    "  texC.y = 0.5*texC.y + 0.5;" +         // 将当前坐标转换为帧缓冲上对应的坐标
                    "  " +
                    "  vec4 backColor = texture(uBackCoord, texC);" +  // 读取帧光线的 射出点和射入点
                    "  " +
                    "  vec3 dir = backColor.rgb - frontColor.rgb;" +   // 得到方向向量
                    "  int steps = 256;" +
                    "  int stepSize = 1;" +
                    "  vec4 vpos = frontColor;" +
                    "  " +
                    "  vec3 Step = dir/float(steps);" +
                    "  " +
                    "  vec4 accumulatedValue = vec4(0, 0, 0, 0);" +
                    "  vec4 value = vec4(0, 0, 0, 0);" +
                    "  " +

                    "  for(int i = 0; i < steps; i+=stepSize)" + // 从入射点开始遍历体素，找灰度值最大的
                    "  {" +

                    "   // Check if the current position is within the cut-off values\n" +
                    "   if((vpos.x / dim[0]) < cutx_left || (vpos.x / dim[0]) > cutx_right || (vpos.y/dim[1]) < cuty_left || (vpos.y/dim[1]) > cuty_right || (vpos.z/dim[2]) < cutz_left || (vpos.z/dim[2]) > cutz_right) " +
                    "   {" +
                    "        continue;" +
                    "   }" +

                    "     vec4 texture_value;" +
                    "     texture_value = texture(uVolData, vec3(1.0 - vpos.x/dim[0], 1.0 - vpos.y/dim[1], vpos.z/dim[2]));" +
                    "     value = vec4(texture_value.x * contrast, texture_value.y * contrast, texture_value.z * contrast, texture_value.x);" +
//                    "     value = vec4(texture_value.x, texture_value.y, texture_value.z, texture_value.x);" +

//                    "     if(value.r <= 3.0/255.0 && value.g <= 3.0/255.0 && value.b <= 3.0/255.0)" +
//                    "         stepSize = 2;" +
//                    "     else" +
//                    "         stepSize = 1;" +

                    "     if(value.r > accumulatedValue.r)\n" +
                    "         accumulatedValue.r = value.r;\n" +
                    "     if(value.g > accumulatedValue.g)\n" +
                    "         accumulatedValue.g = value.g\n;" +
                    "     if(value.b > accumulatedValue.b)\n" +
                    "         accumulatedValue.b = value.b\n;" +
                    "     vpos.xyz += Step * float(stepSize);" +

//                    "     accumulatedValue.a += (1.0 - accumulatedValue.a) * value.a;" +
//                    "     if(accumulatedValue.r > 0.15 && accumulatedValue.g > 0.15 && accumulatedValue.b > 0.15)\n" +
//                    "         break;" +
                    "     if(vpos.x > 1.0 || vpos.y > 1.0 || vpos.z > 1.0 || accumulatedValue.a>=1.0)" +
                    "         break;" +
                    "}" +
//                    "  accumulatedValue.r *= contrast;" +
//                    "  accumulatedValue.g *= contrast;" +
//                    "  accumulatedValue.b *= contrast;" +
                    "  accumulatedValue.a = 1.0;" +
                    "  fragColor = accumulatedValue;" +
                    "}";


    public static void initProgram() {
        // 创建两个着色器程序
        mProgram_simple = initShaderProgram(TAG, vertexShaderCode_1, fragmentShaderCode_1);
        Log.v(TAG, "mProgram_simple: " + mProgram_simple);


        mProgram_raycasting = initShaderProgram(TAG, vertexShaderCode_2, fragmentShaderCode_2);
        Log.v(TAG, "mProgram_raycasting: " + mProgram_raycasting);
    }


    public MyPattern() {
    }

    public void setImage(Image4DSimple image, int width, int height, float[] normalizedDim) {
        this.image = image;
        this.dim = normalizedDim;

        setCutx_left_value(0);
        setCutx_right_value(1);
        setCuty_left_value(0);
        setCuty_right_value(1);
        setCutz_left_value(0);
        setCutz_right_value(1);

        setPoint(normalizedDim);
        bufferSet();

        // load texture
        try {
            initTexture_3d();
        } catch (Exception e) {
            e.printStackTrace();
            ToastEasy("File to Init Texture !");
//            Context context = getContext();
//            Intent intent = new Intent(context, MainActivity.class);
//            String message = "File to Init Texture !";
//            intent.putExtra(MyRenderer.OUT_OF_MEMORY, message);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            ToastEasy("Out of memory when load file !");

//            Context context = getContext();
//            Intent intent = new Intent(context, MainActivity.class);
//            String message = "Out of memory when load file";
//            intent.putExtra(MyRenderer.OUT_OF_MEMORY, message);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        }

        fboBackCoord = initFBO(width, height);
        setNeedSetContent(false);
        setNeedDraw(true);
    }

    public MyPattern(int width, int height, Image4DSimple img, float[] mz, Mode m) {

        image = img;
        dim = mz;
        mode = m;

        setPoint(mz);

        bufferSet();

        // load texture
        try {
            initTexture_3d();
        } catch (Exception e) {
            e.printStackTrace();
            Context context = getContext();
            Intent intent = new Intent(context, MainActivity.class);
            String message = "File to Init Texture !";
            intent.putExtra(MyRenderer.OUT_OF_MEMORY, message);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            Context context = getContext();
            Intent intent = new Intent(context, MainActivity.class);
            String message = "Out of memory when load file";
            intent.putExtra(MyRenderer.OUT_OF_MEMORY, message);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        fboBackCoord = initFBO(width, height);

    }


    /**
     * the function will be called each time when gc collect the object
     */
    @Override
    protected void finalize() {
        GLES32.glDeleteTextures( //删除纹理对象
                1, //删除纹理id的数量
                fbo_tex, //纹理id的数组
                0  //偏移量
        );

        GLES32.glDeleteTextures( //删除纹理对象
                1, //删除纹理id的数量
                vol_tex, //纹理id的数组
                0  //偏移量
        );

        GLES32.glDeleteTextures( //删除纹理对象
                1, //删除纹理id的数量
                vol_texDS, //纹理id的数组
                0  //偏移量
        );
    }

    private float[] mmz;

    private void setPoint(float[] mz) {
        mmz = mz;

        vertexPoints = new float[]{
                // Front face
                0.0f, 0.0f, mz[2],
                mz[0], 0.0f, mz[2],
                mz[0], mz[1], mz[2],
                0.0f, mz[1], mz[2],

                // Back face
                0.0f, 0.0f, 0.0f,
                0.0f, mz[1], 0.0f,
                mz[0], mz[1], 0.0f,
                mz[0], 0.0f, 0.0f,

                // Top face
                0.0f, mz[1], 0.0f,
                0.0f, mz[1], mz[2],
                mz[0], mz[1], mz[2],
                mz[0], mz[1], 0.0f,

                // Bottom face
                0.0f, 0.0f, 0.0f,
                mz[0], 0.0f, 0.0f,
                mz[0], 0.0f, mz[2],
                0.0f, 0.0f, mz[2],

                // Right face
                mz[0], 0.0f, 0.0f,
                mz[0], mz[1], 0.0f,
                mz[0], mz[1], mz[2],
                mz[0], 0.0f, mz[2],

                // Left face
                // Left face
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, mz[2],
                0.0f, mz[1], mz[2],
                0.0f, mz[1], 0.0f,
        };

        vertexPointsPre = new float[]{
                // Front face
                0.0f, 0.0f, mz[2],
                mz[0], 0.0f, mz[2],
                mz[0], mz[1], mz[2],
                0.0f, mz[1], mz[2],

                // Back face
                0.0f, 0.0f, 0.0f,
                0.0f, mz[1], 0.0f,
                mz[0], mz[1], 0.0f,
                mz[0], 0.0f, 0.0f,

                // Top face
                0.0f, mz[1], 0.0f,
                0.0f, mz[1], mz[2],
                mz[0], mz[1], mz[2],
                mz[0], mz[1], 0.0f,

                // Bottom face
                0.0f, 0.0f, 0.0f,
                mz[0], 0.0f, 0.0f,
                mz[0], 0.0f, mz[2],
                0.0f, 0.0f, mz[2],

                // Right face
                mz[0], 0.0f, 0.0f,
                mz[0], mz[1], 0.0f,
                mz[0], mz[1], mz[2],
                mz[0], 0.0f, mz[2],

                // Left face
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, mz[2],
                0.0f, mz[1], mz[2],
                0.0f, mz[1], 0.0f,
        };

        Colors = new float[]{
                // Front face
                0.0f, 0.0f, mz[2], 1.0f,
                mz[0], 0.0f, mz[2], 1.0f,
                mz[0], mz[1], mz[2], 1.0f,
                0.0f, mz[1], mz[2], 1.0f,

                // Back face
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, mz[1], 0.0f, 1.0f,
                mz[0], mz[1], 0.0f, 1.0f,
                mz[0], 0.0f, 0.0f, 1.0f,

                // Top face
                0.0f, mz[1], 0.0f, 1.0f,
                0.0f, mz[1], mz[2], 1.0f,
                mz[0], mz[1], mz[2], 1.0f,
                mz[0], mz[1], 0.0f, 1.0f,

                // Bottom face
                0.0f, 0.0f, 0.0f, 1.0f,
                mz[0], 0.0f, 0.0f, 1.0f,
                mz[0], 0.0f, mz[2], 1.0f,
                0.0f, 0.0f, mz[2], 1.0f,

                // Right face
                mz[0], 0.0f, 0.0f, 1.0f,
                mz[0], mz[1], 0.0f, 1.0f,
                mz[0], mz[1], mz[2], 1.0f,
                mz[0], 0.0f, mz[2], 1.0f,

                // Left face
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, mz[2], 1.0f,
                0.0f, mz[1], mz[2], 1.0f,
                0.0f, mz[1], 0.0f, 1.0f,
        };

        ColorsPre = new float[]{
                // Front face
                0.0f, 0.0f, mz[2], 1.0f,
                mz[0], 0.0f, mz[2], 1.0f,
                mz[0], mz[1], mz[2], 1.0f,
                0.0f, mz[1], mz[2], 1.0f,

                // Back face
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, mz[1], 0.0f, 1.0f,
                mz[0], mz[1], 0.0f, 1.0f,
                mz[0], 0.0f, 0.0f, 1.0f,

                // Top face
                0.0f, mz[1], 0.0f, 1.0f,
                0.0f, mz[1], mz[2], 1.0f,
                mz[0], mz[1], mz[2], 1.0f,
                mz[0], mz[1], 0.0f, 1.0f,

                // Bottom face
                0.0f, 0.0f, 0.0f, 1.0f,
                mz[0], 0.0f, 0.0f, 1.0f,
                mz[0], 0.0f, mz[2], 1.0f,
                0.0f, 0.0f, mz[2], 1.0f,

                // Right face
                mz[0], 0.0f, 0.0f, 1.0f,
                mz[0], mz[1], 0.0f, 1.0f,
                mz[0], mz[1], mz[2], 1.0f,
                mz[0], 0.0f, mz[2], 1.0f,

                // Left face
                0.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, mz[2], 1.0f,
                0.0f, mz[1], mz[2], 1.0f,
                0.0f, mz[1], 0.0f, 1.0f,
        };
    }

    public void setCudePreVertex() {
        vertexPoints = new float[]{
                // Front face
                0.0f, 0.0f, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,

                // Back face
                0.0f, 0.0f, 0.0f,
                0.0f, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, 0.0f,

                // Top face
                0.0f, mmz[1]*cuty_right_value, 0.0f,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,

                // Bottom face
                0.0f, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,
                0.0f, 0.0f, mmz[2]*cutz_right_value,

                // Right face
                mmz[0]*cutx_right_value, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,

                // Left face
                // Left face
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, 0.0f,
        };

        vertexPointsPre = new float[]{
                // Front face
                0.0f, 0.0f, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,

                // Back face
                0.0f, 0.0f, 0.0f,
                0.0f, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, 0.0f,

                // Top face
                0.0f, mmz[1]*cuty_right_value, 0.0f,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,

                // Bottom face
                0.0f, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,
                0.0f, 0.0f, mmz[2]*cutz_right_value,

                // Right face
                mmz[0]*cutx_right_value, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,

                // Left face
                // Left face
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, 0.0f,
        };

        Colors = new float[]{
                // Front face
                0.0f, 0.0f, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,

                // Back face
                0.0f, 0.0f, 0.0f,
                0.0f, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, 0.0f,

                // Top face
                0.0f, mmz[1]*cuty_right_value, 0.0f,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,

                // Bottom face
                0.0f, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,
                0.0f, 0.0f, mmz[2]*cutz_right_value,

                // Right face
                mmz[0]*cutx_right_value, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,

                // Left face
                // Left face
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, 0.0f,
        };

        ColorsPre = new float[]{
                // Front face
                0.0f, 0.0f, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,

                // Back face
                0.0f, 0.0f, 0.0f,
                0.0f, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, 0.0f,

                // Top face
                0.0f, mmz[1]*cuty_right_value, 0.0f,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,

                // Bottom face
                0.0f, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,
                0.0f, 0.0f, mmz[2]*cutz_right_value,

                // Right face
                mmz[0]*cutx_right_value, 0.0f, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, 0.0f,
                mmz[0]*cutx_right_value, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                mmz[0]*cutx_right_value, 0.0f, mmz[2]*cutz_right_value,

                // Left face
                // Left face
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, mmz[2]*cutz_right_value,
                0.0f, mmz[1]*cuty_right_value, 0.0f,
        };
    }


    public void drawVolume_3d(float[] mvpMatrix, boolean ifDownSampling, float contrast, int contrastEnhanceRatio) {

//        setCudePreVertex();

        bufferSet();

        // Add program to OpenGL ES environment
        GLES32.glUseProgram(mProgram_simple);

        //绑定帧缓冲id，将对象绑定到环境的帧缓冲单元
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, fboBackCoord);

        GLES32.glClearDepthf(-50.0f);
        GLES32.glDepthFunc(GLES32.GL_GEQUAL);

        // first pass draw
        drawCubePre(mvpMatrix, mProgram_simple);

        //解除绑定
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0);

        // Add program to OpenGL ES environment
        GLES32.glUseProgram(mProgram_raycasting);
        GLES32.glClearDepthf(50.0f);
        GLES32.glDepthFunc(GLES32.GL_LEQUAL);

        dimHandle = GLES32.glGetUniformLocation(mProgram_raycasting, "dim");
        GLES32.glUniform1fv(dimHandle, 3, dimBuffer);

        contrastHandle = GLES32.glGetUniformLocation(mProgram_raycasting, "contrast");
        GLES32.glUniform1f(contrastHandle, contrast * contrastEnhanceRatio);

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0); // 设置使用的纹理编号
        if (ifDownSampling)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_3D, vol_texDS[0]);
        else
            GLES32.glBindTexture(GLES32.GL_TEXTURE_3D, vol_tex[0]); // 绑定指定的纹理id

        GLES32.glActiveTexture(GLES32.GL_TEXTURE1); // 设置使用的纹理编号
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, fbo_tex[0]); // 绑定指定的纹理id


        // 将纹理单元传递片段着色器的uVolData
        GLES32.glUniform1i(GLES32.glGetUniformLocation(mProgram_raycasting, "uVolData"), 0);

        // 将纹理单元传递片段着色器的uBackCoord
        GLES32.glUniform1i(GLES32.glGetUniformLocation(mProgram_raycasting, "uBackCoord"), 1);

        // 获取uniform cut变量的位置
        int cutxLeftHandle = GLES32.glGetUniformLocation(mProgram_raycasting, "cutx_left");
        int cutxRightHandle = GLES32.glGetUniformLocation(mProgram_raycasting, "cutx_right");
        int cutyLeftHandle = GLES32.glGetUniformLocation(mProgram_raycasting, "cuty_left");
        int cutyRightHandle = GLES32.glGetUniformLocation(mProgram_raycasting, "cuty_right");
        int cutzLeftHandle = GLES32.glGetUniformLocation(mProgram_raycasting, "cutz_left");
        int cutzRightHandle = GLES32.glGetUniformLocation(mProgram_raycasting, "cutz_right");

        // 设置uniform cut变量的值
        GLES32.glUniform1f(cutxLeftHandle, cutx_left_value);
        GLES32.glUniform1f(cutxRightHandle, cutx_right_value);
        GLES32.glUniform1f(cutyLeftHandle, cuty_left_value);
        GLES32.glUniform1f(cutyRightHandle, cuty_right_value);
        GLES32.glUniform1f(cutzLeftHandle, cutz_left_value);
        GLES32.glUniform1f(cutzRightHandle, cutz_right_value);

        drawCube(mvpMatrix, mProgram_raycasting);

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0); //解除绑定指定的纹理id
        GLES32.glBindTexture(GLES32.GL_TEXTURE_3D, 0); //解除绑定指定的纹理id

    }



    /*
    init texture buffer ---------------------------------------------------------------------
     */

    private int initFBO(int width, int height) {

        GLES32.glGenFramebuffers(  //创建帧缓冲对象
                1, //产生帧缓冲id的数量
                fbo,  //帧缓冲id的数组
                0  //偏移量
        );

        //绑定帧缓冲id，将对象绑定到环境的帧缓冲单元
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, fbo[0]);

        //创建randerbuffer
        GLES32.glGenRenderbuffers(
                1,
                rbo,
                0
        );

        GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, rbo[0]);

        GLES32.glRenderbufferStorage(GLES32.GL_RENDERBUFFER, GLES32.GL_DEPTH_COMPONENT16, width, height);

        GLES32.glFramebufferRenderbuffer(GLES32.GL_FRAMEBUFFER, GLES32.GL_DEPTH_ATTACHMENT, GLES32.GL_RENDERBUFFER, rbo[0]);

        GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, 0);

        GLES32.glGenTextures(  //创建纹理对象
                1, //产生纹理id的数量
                fbo_tex, //纹理id的数组
                0  //偏移量
        );


        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, fbo_tex[0]);

        imageBuffer_FBO = CreateBuffer(new byte[width * height * 4]);

        GLES32.glTexImage2D(
                GLES32.GL_TEXTURE_2D,
                0,
                GLES32.GL_RGBA,
                width,
                height,
                0,
                GLES32.GL_RGBA,
                GLES32.GL_UNSIGNED_BYTE,
                imageBuffer_FBO);

        GLES32.glTexParameterf(GLES32.GL_TEXTURE_2D,
                GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);//设置MIN 采样方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_2D,
                GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);//设置MAG采样方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_2D,
                GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_2D,
                GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式

        GLES32.glFramebufferTexture2D(GLES32.GL_FRAMEBUFFER, GLES32.GL_COLOR_ATTACHMENT0, GLES32.GL_TEXTURE_2D, fbo_tex[0], 0);


        int uStatus = GLES32.glCheckFramebufferStatus(GLES32.GL_FRAMEBUFFER);
        if (uStatus != GLES32.GL_FRAMEBUFFER_COMPLETE) {
            Log.v("ReInitFBO()", "glCheckFramebufferStatus=%X");
            return -1;
        }
        return fbo[0];
    }


    //生成三维纹理 for opengl es 3.0~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private void initTexture_3d() {

        vol_w = (int) image.getSz0();
        vol_h = (int) image.getSz1();
        vol_d = (int) image.getSz2();

        vol_wDS = vol_w / downSampleScale + 1;
        vol_hDS = vol_h / downSampleScale + 1;
        vol_dDS = vol_d / downSampleScale + 1;

        byte[] data_src = image.getData();

        nchannel = (int) image.getSz3();
        data_length = image.getDatatype().ordinal();
        isBig = image.getIsBig();

        GLES32.glGenTextures(  //创建纹理对象
                1, //产生纹理id的数量
                vol_tex, //纹理id的数组
                0  //偏移量
        );

//        byte [] image_data = getIntensity_3d();


//        createMarker(image_data, vol_w, vol_h, vol_d, 60, 60, 60);


        //绑定纹理id，将对象绑定到环境的纹理单元
        GLES32.glBindTexture(GLES32.GL_TEXTURE_3D, vol_tex[0]);

        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);//设置MIN 采样方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);//设置MAG采样方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_WRAP_R, GLES32.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式


//        byte [] image_data = getIntensity_3d();
//        imageBuffer = CreateBuffer(image_data);

        if (data_length == 1) {
            byte[] image_data = getIntensity_3d(data_src);
            imageBuffer = CreateBuffer(image_data);
            GLES32.glTexImage3D(
                    GLES32.GL_TEXTURE_3D, //纹理类型
                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
                    GLES32.GL_RGBA, //图片的格式
                    vol_w,   //宽
                    vol_h,   //高
                    vol_d,   //切片数
                    0, //纹理边框尺寸();
                    GLES32.GL_RGBA,
                    GLES32.GL_UNSIGNED_BYTE,
                    imageBuffer
            );
            imageBuffer.clear();
            imageBuffer = null;
        } else if (data_length == 2) {
            short[] image_data = getIntensity_short3d(data_src);
//            imageShortBuffer = ShortBuffer.allocate(image_data.length)
//                    .order(shortOrder.nativeOrder());
//            //传入指定的坐标数据
//            imageShortBuffer.put(image_data);
            imageShortBuffer = ShortBuffer.wrap(image_data);
            imageShortBuffer.position(0);
            GLES32.glTexImage3D(
                    GLES32.GL_TEXTURE_3D, //纹理类型
                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
                    GLES32.GL_RGBA, //图片的格式
                    vol_w,   //宽
                    vol_h,   //高
                    vol_d,   //切片数
                    0, //纹理边框尺寸();
                    GLES32.GL_RGBA,
                    GLES32.GL_UNSIGNED_SHORT,
                    imageShortBuffer
            );
            imageShortBuffer.clear();
            imageShortBuffer = null;
        } else if (data_length == 4) {
            int[] image_data = getIntensity_int3d(data_src);
            imageIntBuffer = IntBuffer.wrap(image_data);
            imageIntBuffer.position(0);
            GLES32.glTexImage3D(
                    GLES32.GL_TEXTURE_3D, //纹理类型
                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
                    GLES32.GL_RGBA, //图片的格式
                    vol_w,   //宽
                    vol_h,   //高
                    vol_d,   //切片数
                    0, //纹理边框尺寸();
                    GLES32.GL_RGBA,
                    GLES32.GL_UNSIGNED_INT,
                    imageIntBuffer
            );
            imageIntBuffer.clear();
            imageIntBuffer = null;
        }

        GLES32.glBindTexture(GLES32.GL_TEXTURE_3D, 0);

        GLES32.glGenTextures(  //创建纹理对象
                1, //产生纹理id的数量
                vol_texDS, //纹理id的数组
                0  //偏移量
        );

        GLES32.glBindTexture(GLES32.GL_TEXTURE_3D, vol_texDS[0]);

        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);//设置MIN 采样方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR);//设置MAG采样方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_3D,
                GLES32.GL_TEXTURE_WRAP_R, GLES32.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式

        if (data_length == 1) {
            byte[] image_data = getIntensity_3dDownSample(data_src);
            imageDSBuffer = CreateBuffer(image_data);
            GLES32.glTexImage3D(
                    GLES32.GL_TEXTURE_3D, //纹理类型
                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
                    GLES32.GL_RGBA, //图片的格式
                    vol_wDS,   //宽
                    vol_hDS,   //高
                    vol_dDS,   //切片数
                    0, //纹理边框尺寸();
                    GLES32.GL_RGBA,
                    GLES32.GL_UNSIGNED_BYTE,
                    imageDSBuffer
            );
            imageDSBuffer.clear();
            imageDSBuffer = null;
        } else if (data_length == 2) {
            short[] image_data = getIntensity_short3dDownSample(data_src);
//            imageShortBuffer = ShortBuffer.allocate(image_data.length)
//                    .order(shortOrder.nativeOrder());
//            //传入指定的坐标数据
//            imageShortBuffer.put(image_data);
            imageShortDSBuffer = ShortBuffer.wrap(image_data);
            imageShortDSBuffer.position(0);
            GLES32.glTexImage3D(
                    GLES32.GL_TEXTURE_3D, //纹理类型
                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
                    GLES32.GL_RGBA, //图片的格式
                    vol_wDS,   //宽
                    vol_hDS,   //高
                    vol_dDS,   //切片数
                    0, //纹理边框尺寸();
                    GLES32.GL_RGBA,
                    GLES32.GL_UNSIGNED_SHORT,
                    imageShortDSBuffer
            );
            imageShortDSBuffer.clear();
            imageShortDSBuffer = null;
        } else if (data_length == 4) {
            int[] image_data = getIntensity_int3dDownSample(data_src);
            imageIntDSBuffer = IntBuffer.wrap(image_data);
            imageIntDSBuffer.position(0);
            GLES32.glTexImage3D(
                    GLES32.GL_TEXTURE_3D, //纹理类型
                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
                    GLES32.GL_RGBA, //图片的格式
                    vol_wDS,   //宽
                    vol_hDS,   //高
                    vol_dDS,   //切片数
                    0, //纹理边框尺寸();
                    GLES32.GL_RGBA,
                    GLES32.GL_UNSIGNED_INT,
                    imageIntDSBuffer
            );
            imageIntDSBuffer.clear();
            imageIntDSBuffer = null;
        }

        GLES32.glBindTexture(GLES32.GL_TEXTURE_3D, 0);

    }

    /*
    init texture buffer --------------------------------------------------------------------- end
     */


    //为三维纹理准备数据
    private byte[] getIntensity_3d(byte[] data_src) {

        byte[] data_image = new byte[vol_w * vol_h * vol_d * data_length * nchannel * 4];
        if (nchannel == 3) {
            for (int i = 0; i < vol_w * vol_h * vol_d * data_length; i++) {
                data_image[i * 4] = data_src[i];
                data_image[i * 4 + 1] = data_src[vol_w * vol_h * vol_d * data_length + i];
                data_image[i * 4 + 2] = data_src[vol_w * vol_h * vol_d * data_length * 2 + i];
                data_image[i * 4 + 3] = ByteTranslate.intToByte(1);
            }
        } else {
            for (int i = 0; i < vol_w * vol_h * vol_d * data_length; i++) {
                data_image[i * 4] = data_src[i];
                data_image[i * 4 + 1] = data_src[i];
                data_image[i * 4 + 2] = data_src[i];
                data_image[i * 4 + 3] = ByteTranslate.intToByte(1);
            }
        }

        if (mode == Mode.GAME) {
            int[] data_gray = new int[(data_image.length + 1) / 4];

            int j = 0;
            for (int i = 0; i < data_image.length; i += 4) {
                data_gray[j] = byteTranslate.byte1ToInt(data_image[i]);  // (int)((float)data_image[i] * 0.3 + (float)data_image[i+1] * 0.59 + (float)data_image[i+2] * 0.11);
                j++;
            }

            // 下面是迭代法求二值化阈值
            // 求出最大灰度值和最小灰度值
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

            if (threshold >= 35 & threshold <= 45)
                threshold += 2;
            else if (threshold < 35) //防止threshold太小了。
                threshold = 35;

            Log.d("newthreshold", String.valueOf(threshold));
        }

        return data_image;
    }


    private short[] getIntensity_short3d(byte[] data_src) {

        short[] data_image = new short[vol_w * vol_h * vol_d * nchannel * 4];
        byte[] b = new byte[2];
        if (nchannel == 3) {
            for (int i = 0; i < vol_w * vol_h * vol_d; i++) {
                for (int c = 0; c < nchannel; c++) {
                    b[0] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 2];
                    b[1] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 2 + 1];
                    data_image[i * 4 + c] = ByteTranslate.byte2ToShort(b, isBig);
                }
                data_image[i * 4 + 3] = 1;
            }
        } else {
            for (int i = 0; i < vol_w * vol_h * vol_d; i++) {
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

    public int[] getIntensity_int3d(byte[] data_src) {

        Log.v(TAG, "getIntensity_int3d");

        int[] data_image = new int[vol_w * vol_h * vol_d * nchannel * 4];
        byte[] b = new byte[4];
        if (nchannel == 3) {
            for (int i = 0; i < vol_w * vol_h * vol_d; i++) {
                for (int c = 0; c < nchannel; c++) {
                    b[0] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4];
                    b[1] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4 + 1];
                    b[2] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4 + 2];
                    b[3] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4 + 3];
                    data_image[i * 4 + c] = ByteTranslate.byte2ToInt(b, isBig);
                }
                data_image[i * 4 + 3] = 1;
            }
        } else {
            for (int i = 0; i < vol_w * vol_h * vol_d; i++) {
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


    private byte[] getIntensity_3dDownSample(byte[] data_src) {
        Log.v(TAG, "getIntensity_3dDS");

        byte[] data_image = new byte[vol_wDS * vol_hDS * vol_dDS * data_length * nchannel * 4];
        if (nchannel == 3) {
            for (int x = 0; x < vol_w; x += downSampleScale) {
                for (int y = 0; y < vol_h; y += downSampleScale) {
                    for (int z = 0; z < vol_d; z += downSampleScale) {
                        int i = (z * vol_h * vol_w + y * vol_w + x) * data_length;
                        int j = (z / downSampleScale * vol_hDS * vol_wDS + y / downSampleScale * vol_wDS + x / downSampleScale) * data_length;
                        data_image[j * 4] = data_src[i];
                        data_image[j * 4 + 1] = data_src[vol_w * vol_h * vol_d * data_length + i];
                        data_image[j * 4 + 2] = data_src[vol_w * vol_h * vol_d * data_length * 2 + i];
                        data_image[j * 4 + 3] = ByteTranslate.intToByte(1);
                    }
                }
            }
        } else {
            for (int x = 0; x < vol_w; x += downSampleScale) {
                for (int y = 0; y < vol_h; y += downSampleScale) {
                    for (int z = 0; z < vol_d; z += downSampleScale) {
                        int i = (z * vol_h * vol_w + y * vol_w + x) * data_length;
                        int j = (z / downSampleScale * vol_hDS * vol_wDS + y / downSampleScale * vol_wDS + x / downSampleScale) * data_length;
                        data_image[j * 4] = data_src[i];
                        data_image[j * 4 + 1] = data_src[i];
                        data_image[j * 4 + 2] = data_src[i];
                        data_image[j * 4 + 3] = ByteTranslate.intToByte(1);
                    }
                }
            }
        }

//        vol_wDS /= downSampleScale;
//        vol_hDS /= downSampleScale;
//        vol_dDS /= downSampleScale;

        return data_image;
    }

    private short[] getIntensity_short3dDownSample(byte[] data_src) {
        Log.v(TAG, "getIntensity_short3dDS");

        short[] data_image = new short[vol_wDS * vol_hDS * vol_dDS * nchannel * 4];
        byte[] b = new byte[2];

        if (nchannel == 3) {
            for (int x = 0; x < vol_w; x += downSampleScale) {
                for (int y = 0; y < vol_h; y += downSampleScale) {
                    for (int z = 0; z < vol_d; z += downSampleScale) {
                        int i = (z * vol_h * vol_w + y * vol_w + x) * data_length;
                        int j = (z / downSampleScale * vol_hDS * vol_wDS + y / downSampleScale * vol_wDS + x / downSampleScale) * data_length;
                        for (int c = 0; c < nchannel; c++) {
                            b[0] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 2];
                            b[1] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 2 + 1];
                            data_image[j * 4 + c] = ByteTranslate.byte2ToShort(b, isBig);
                        }
                        data_image[j * 4 + 3] = 1;
                    }
                }
            }
        } else {
            for (int x = 0; x < vol_w; x += downSampleScale) {
                for (int y = 0; y < vol_h; y += downSampleScale) {
                    for (int z = 0; z < vol_d; z += downSampleScale) {
                        int i = (z * vol_h * vol_w + y * vol_w + x) * data_length;
                        int j = (z / downSampleScale * vol_hDS * vol_wDS + y / downSampleScale * vol_wDS + x / downSampleScale) * data_length;
                        b[0] = data_src[i * 2];
                        b[1] = data_src[i * 2 + 1];
                        short temp = ByteTranslate.byte2ToShort(b, isBig);
                        data_image[j * 4] = temp;
                        data_image[j * 4 + 1] = temp;
                        data_image[j * 4 + 2] = temp;
                        data_image[j * 4 + 3] = 1;
                    }
                }
            }
        }

//        vol_wDS /= downSampleScale;
//        vol_hDS /= downSampleScale;
//        vol_dDS /= downSampleScale;

        return data_image;
    }

    private int[] getIntensity_int3dDownSample(byte[] data_src) {
        Log.v(TAG, "getIntensity_int3dDS");

        int[] data_image = new int[vol_wDS * vol_hDS * vol_dDS * nchannel * 4];
        byte[] b = new byte[4];

        if (nchannel == 3) {
            for (int x = 0; x < vol_w; x += downSampleScale) {
                for (int y = 0; y < vol_h; y += downSampleScale) {
                    for (int z = 0; z < vol_d; z += downSampleScale) {
                        int i = (z * vol_h * vol_w + y * vol_w + x) * data_length;
                        int j = (z / downSampleScale * vol_hDS * vol_wDS + y / downSampleScale * vol_wDS + x / downSampleScale) * data_length;
                        for (int c = 0; c < nchannel; c++) {
                            b[0] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4];
                            b[1] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4 + 1];
                            b[2] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4 + 2];
                            b[3] = data_src[vol_w * vol_h * vol_d * data_length * c + i * 4 + 3];
                            data_image[j * 4 + c] = ByteTranslate.byte2ToInt(b, isBig);
                        }
                        data_image[j * 4 + 3] = 1;
                    }
                }
            }
        } else {
            for (int x = 0; x < vol_w; x += downSampleScale) {
                for (int y = 0; y < vol_h; y += downSampleScale) {
                    for (int z = 0; z < vol_d; z += downSampleScale) {
                        int i = (z * vol_h * vol_w + y * vol_w + x) * data_length;
                        int j = (z / downSampleScale * vol_hDS * vol_wDS + y / downSampleScale * vol_wDS + x / downSampleScale) * data_length;
                        b[0] = data_src[i * 4];
                        b[1] = data_src[i * 4 + 1];
                        b[2] = data_src[i * 4 + 2];
                        b[3] = data_src[i * 4 + 3];
                        int temp = ByteTranslate.byte2ToInt(b, isBig);
                        data_image[j * 4] = temp;
                        data_image[j * 4 + 1] = temp;
                        data_image[j * 4 + 2] = temp;
                        data_image[j * 4 + 3] = 1;
                    }
                }
            }
        }

//        vol_wDS /= downSampleScale;
//        vol_hDS /= downSampleScale;
//        vol_dDS /= downSampleScale;

        return data_image;
    }


    // int 转 byte array
    private static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }


    public void drawCubePre(float[] mvpMatrix, int mProgram) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);

        // get the common handle
        getHandle(mProgram);

        // Pass the projection and view transformation to the shader
        GLES32.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
//        GLES32.glUniformMatrix4fv(trAMatrixHandle, 1, false, translateAfterMatrix, 0);

        // 获取uniform cut变量的位置
        int cutxLeftHandle = GLES32.glGetUniformLocation(mProgram, "cutx_left");
        int cutxRightHandle = GLES32.glGetUniformLocation(mProgram, "cutx_right");
        int cutyLeftHandle = GLES32.glGetUniformLocation(mProgram, "cuty_left");
        int cutyRightHandle = GLES32.glGetUniformLocation(mProgram, "cuty_right");
        int cutzLeftHandle = GLES32.glGetUniformLocation(mProgram, "cutz_left");
        int cutzRightHandle = GLES32.glGetUniformLocation(mProgram, "cutz_right");

        // 设置uniform cut变量的值
        GLES32.glUniform1f(cutxLeftHandle, cutx_left_value);
        GLES32.glUniform1f(cutxRightHandle, cutx_right_value);
        GLES32.glUniform1f(cutyLeftHandle, cuty_left_value);
        GLES32.glUniform1f(cutyRightHandle, cuty_right_value);
        GLES32.glUniform1f(cutzLeftHandle, cutz_left_value);
        GLES32.glUniform1f(cutzRightHandle, cutz_right_value);

        // 准备坐标数据
        GLES32.glVertexAttribPointer(positionHandle, 3, GLES32.GL_FLOAT, false, 0, vertexPreBuffer);

        // 准备颜色数据
        GLES32.glVertexAttribPointer(colorHandle, 4, GLES32.GL_FLOAT, false, 0, colorPreBuffer);

        // 通过索引来绘制
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, 36, GLES32.GL_UNSIGNED_SHORT, drawListPreBuffer);

        // 禁止顶点数组的句柄
        GLES32.glDisableVertexAttribArray(positionHandle);
        GLES32.glDisableVertexAttribArray(colorHandle);

    }

    public void drawCube(float[] mvpMatrix, int mProgram) {

        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);

        // get the common handle
        getHandle(mProgram);

        // Pass the projection and view transformation to the shader
        GLES32.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
//        GLES32.glUniformMatrix4fv(trAMatrixHandle, 1, false, translateAfterMatrix, 0);
        // 准备坐标数据
        GLES32.glVertexAttribPointer(positionHandle, 3, GLES32.GL_FLOAT, false, 0, vertexBuffer);

        // 准备颜色数据
        GLES32.glVertexAttribPointer(colorHandle, 4, GLES32.GL_FLOAT, false, 0, colorBuffer);

        // 通过索引来绘制
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, drawlistLength, GLES32.GL_UNSIGNED_SHORT, drawListBuffer);

        // 禁止顶点数组的句柄
        GLES32.glDisableVertexAttribArray(positionHandle);
        GLES32.glDisableVertexAttribArray(colorHandle);

    }


    /*
    prepare the buffer -----------------------------------------------------------------------
     */
    private void bufferSet() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);

        vertexPreBuffer = ByteBuffer.allocateDirect(vertexPointsPre.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexPreBuffer.put(vertexPointsPre);
        vertexPreBuffer.position(0);


        //分配内存空间,每个浮点型占4字节空间
        colorBuffer = ByteBuffer.allocateDirect(Colors.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的颜色数据
        colorBuffer.put(Colors);
        colorBuffer.position(0);

        //分配内存空间,每个浮点型占4字节空间
        colorPreBuffer = ByteBuffer.allocateDirect(ColorsPre.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的颜色数据
        colorPreBuffer.put(ColorsPre);
        colorPreBuffer.position(0);


        //分配内存空间,每个short型占4字节空间
        drawListBuffer = ByteBuffer.allocateDirect(drawlist.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的索引数据
        drawListBuffer.put(drawlist);
        drawListBuffer.position(0);

        //分配内存空间,每个short型占4字节空间
        drawListPreBuffer = ByteBuffer.allocateDirect(drawlistPre.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的索引数据
        drawListPreBuffer.put(drawlistPre);
        drawListPreBuffer.position(0);

        //分配内存空间,每个浮点型占4字节空间
        dimBuffer = ByteBuffer.allocateDirect(dim.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的颜色数据
        dimBuffer.put(dim);
        dimBuffer.position(0);

    }


    public void setVertex(float[] vertex) {
        vertexBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertex);
        vertexBuffer.position(0);

        float[] color = new float[vertex.length / 3 * 4];
        for (int i = 0; i < vertex.length / 3; i++) {
            color[i * 4] = vertex[i * 3];
            color[i * 4 + 1] = vertex[i * 3 + 1];
            color[i * 4 + 2] = vertex[i * 3 + 2];
            color[i * 4 + 3] = 1.0f;
        }

        //分配内存空间,每个浮点型占4字节空间
        colorBuffer = ByteBuffer.allocateDirect(color.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的颜色数据
        colorBuffer.put(color);
        colorBuffer.position(0);
    }


    private ByteBuffer CreateBuffer(byte[] data) {

        ByteBuffer templateBuffer;
        //分配内存空间,每个字节型占1字节空间
        templateBuffer = ByteBuffer.allocateDirect(data.length)
                .order(ByteOrder.nativeOrder());
        //传入指定的坐标数据
        templateBuffer.put(data);
        templateBuffer.position(0);
        return templateBuffer;

    }


    private void getHandle(int mProgram) {

        //get handle to vertex shader's vPosition member
        //启用顶点的句柄
        GLES32.glEnableVertexAttribArray(positionHandle);

        //get handle to vertex shader's vPosition member
        //启用纹理的句柄
        GLES32.glEnableVertexAttribArray(colorHandle);

        // get handle to vertex shader's uMVPMatrix member
        vPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix");

    }

    @Override
    public void releaseMemory() {
        super.releaseMemory();
        GLES32.glDeleteTextures( //删除纹理对象
                1, //删除纹理id的数量
                fbo_tex, //纹理id的数组
                0  //偏移量
        );

        GLES32.glDeleteTextures( //删除纹理对象
                1, //删除纹理id的数量
                vol_tex, //纹理id的数组
                0  //偏移量
        );

        GLES32.glDeleteTextures( //删除纹理对象
                1, //删除纹理id的数量
                vol_texDS, //纹理id的数组
                0  //偏移量
        );


        GLES32.glDeleteFramebuffers(
                1,
                fbo,
                0);


        GLES32.glDeleteRenderbuffers(
                1,
                rbo,
                0);

        if (vertexBuffer != null) {
            vertexBuffer.clear();
            vertexBuffer = null;
        }

        if (colorBuffer != null) {
            colorBuffer.clear();
            colorBuffer = null;
        }

        if (drawListBuffer != null) {
            drawListBuffer.clear();
            drawListBuffer = null;
        }

        if (dimBuffer != null) {
            dimBuffer.clear();
            dimBuffer = null;
        }

        if (imageBuffer_FBO != null) {
            imageBuffer_FBO.clear();
            imageBuffer_FBO = null;
        }

        if (vertexPreBuffer != null) {
            vertexPreBuffer.clear();
            vertexPreBuffer = null;
        }

        if (colorPreBuffer != null) {
            colorPreBuffer.clear();
            colorPreBuffer = null;
        }

        if (drawListPreBuffer != null) {
            drawListPreBuffer.clear();
            drawListPreBuffer = null;
        }
    }


    public boolean ifImageLoaded() {
        return !(image == null);
    }

    public void setIfGame(boolean b) {
        ifGame = b;
    }

}