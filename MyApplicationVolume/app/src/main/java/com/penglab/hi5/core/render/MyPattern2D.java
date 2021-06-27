package com.penglab.hi5.core.render;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.penglab.hi5.core.render.ShaderHelper.initShaderProgram;

public class MyPattern2D {

    private static final String TAG = "MyPattern2D";
    private static final String vertexShaderCode =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;" +
                    "layout (location = 1) in vec2 vCoordinate;" +
                    "uniform mat4 vMatrix;" +

                    "out vec2 aCoordinate;" +

                    "void main() {" +
                    "  gl_Position = vMatrix * vPosition;" +
                    "  aCoordinate = vCoordinate;" +
                    "}";

    private static final String fragmentShaderCode =
            "#version 300 es\n" +
                    "precision mediump float;" +

                    "uniform sampler2D vTexture;" +
                    "in vec2 aCoordinate;" +
                    "out vec4 fragColor;" +

                    "void main() {" +
                    "   fragColor = texture(vTexture, aCoordinate);" +
                    "}";


    private float[] sPos = new float[12];
    private final float[] sCoord={
            0.0f,0.0f,
            0.0f,1.0f,
            1.0f,0.0f,
            1.0f,1.0f,
    };

    private int width;
    private int height;

    private Bitmap mBitmap;
    private static int mProgram;

    private float[] mMVPMatrix = new float[16];
    private float[] mz = new float[3];

    private int[] texture=new int[1];

    private FloatBuffer bPos;
    private FloatBuffer bCoord;

    private boolean isNeedRelease;

    public MyPattern2D(Bitmap bitmap, int w, int h, float [] vmz){
        width = w;
        height = h;
        mBitmap = bitmap;
        mz = vmz;

        createTexture();
    }

    /**
     * the function will be called each time when gc collect the object
     */
    @Override
    protected void finalize(){
        Log.v(TAG,"finalize() is called");

        GLES30.glDeleteTextures( //删除纹理对象
                1, //删除纹理id的数量
                texture, //纹理id的数组
                0  //偏移量
        );

    }

    public void free(){
        Log.i(TAG,"free() is called");

        GLES30.glDeleteTextures( //删除纹理对象
                1, //删除纹理id的数量
                texture, //纹理id的数组
                0  //偏移量
        );

        bPos.clear();
        bPos = null;

        bCoord.clear();
        bCoord = null;

    }

    public static void initProgram(){
        mProgram = initShaderProgram(TAG, vertexShaderCode, fragmentShaderCode);
        Log.v(TAG,"mProgram: " + mProgram);
    }


    private void createTexture(){

        if(mBitmap!=null && !mBitmap.isRecycled()){

            //生成纹理
            GLES30.glGenTextures(1,texture,0);

            //生成纹理
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texture[0]);

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

    public void draw(float [] vMatrix){
        mMVPMatrix = vMatrix;

        GLES20.glUseProgram(mProgram);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0); //设置使用的纹理编号
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture[0]); //绑定指定的纹理id

        int glHMatrix = GLES30.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(glHMatrix,1,false,mMVPMatrix,0);

        int glHPosition = GLES30.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(glHPosition);

        int glHCoordinate = GLES30.glGetAttribLocation(mProgram, "vCoordinate");
        GLES20.glEnableVertexAttribArray(glHCoordinate);

        GLES20.glUniform1i(GLES30.glGetUniformLocation(mProgram, "vTexture"), 0);

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        setBuffer();

        //传入顶点坐标
        GLES20.glVertexAttribPointer(glHPosition, 3, GLES20.GL_FLOAT, false, 0, bPos);
        //传入纹理坐标
        GLES20.glVertexAttribPointer(glHCoordinate, 2, GLES20.GL_FLOAT, false, 0, bCoord);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);

    }

    void setBuffer(){
        sPos = new float[]{
                mz[0], mz[1], mz[2] / 2,
                mz[0], 0, mz[2] / 2,
                0, mz[1], mz[2] / 2,
                0, 0, mz[2] / 2
        };
        //分配内存空间,每个浮点型占4字节空间
        bPos = ByteBuffer.allocateDirect(sPos.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        bPos.put(sPos);
        bPos.position(0);

        //分配内存空间,每个浮点型占4字节空间
        bCoord = ByteBuffer.allocateDirect(sCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        bCoord.put(sCoord);
        bCoord.position(0);
    }

    public boolean getNeedRelease(){
        Log.i(TAG,"getNeedRelease(): " + isNeedRelease);
        return isNeedRelease;
    }


    public void setNeedRelease(){
        Log.i(TAG,"setNeedRelease()");
        isNeedRelease = true;
    }

}
