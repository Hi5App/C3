package com.penglab.hi5.core.render;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.penglab.hi5.core.render.ShaderHelper.initShaderProgram;

public class MyNavLoc {

    private static final String TAG = "MyNavLoc";
    private static int mProgram_axis;
    private static int mProgram_border_img;
    private static int mProgram_border_block;


    private FloatBuffer vertexBuffer_axis;
    private FloatBuffer colorBuffer_axis;


    private FloatBuffer vertexBuffer_border_img;
    private FloatBuffer vertexBuffer_border_block;
    private ShortBuffer ListBuffer_border;

    private float[] vertexAxis;
    private float[] vertexBorder_img;
    private float[] vertexBorder_block;


    private final float[] colorAxis={
            // x axis
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,

            // y axis
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,

            // z axis
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

    };


    private final short[] BorderList= {
            0, 1,   0, 2,   0, 4,
            6, 1,   6, 4,   6, 7,
            3, 1,   3, 2,   3, 7,
            5, 7,   5, 4,   5, 2
    };



    //draw the axis
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static final String vertexShaderCode_axis =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +
                    "layout (location = 1) in vec4 vColor;\n" +

                    "uniform mat4 uMVPMatrix;" +
                    "out vec4 outColor;\n" +

                    "void main() {\n" +
                    "     gl_Position  = uMVPMatrix * vPosition;\n" +
//                    "     gl_Position  = vec4((uMVPMatrix * vPosition).xy, -1.0, 1.0);\n" +
                    "     gl_PointSize = 20.0;\n" +
                    "     outColor  = vColor;\n" + //

                    "}\n";


    private static final String fragmentShaderCode_axis =
            "#version 300 es\n" +
                    "precision mediump float;\n" +

                    "in vec4 outColor;\n" +
                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
                    "     fragColor = outColor;\n" +
//                    "     fragColor = vec4(1.0,1.0,1.0,1.0);\n" +
                    "}\n";



    //draw the border of img
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static final String vertexShaderCode_border_img =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +

                    "uniform mat4 uMVPMatrix;" +

                    "void main() {\n" +
                    "     gl_Position  = uMVPMatrix * vPosition;\n" +
//                    "     gl_Position  = vec4((uMVPMatrix * vPosition).xy, -1.0, 1.0);\n" +

                    "     gl_PointSize = 10.0;\n" +
                    "}\n";


    private static final String fragmentShaderCode_border_img =
            "#version 300 es\n" +
                    "precision mediump float;\n" +

                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
//                    "     fragColor = outColor;\n" +
                    "     fragColor = vec4(0.75, 0.75, 0.75,1.0);\n" +
                    "}\n";


    //draw the border of block
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static final String vertexShaderCode_border_block =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +

                    "uniform mat4 uMVPMatrix;" +

                    "void main() {\n" +
                    "     gl_Position  = uMVPMatrix * vec4( 1.0 - vPosition.x,  1.0 - vPosition.y, vPosition.z, 1.0);\n" +
//                    "     gl_Position  = vec4((uMVPMatrix * vPosition).xy, -1.0, 1.0);\n" +

                    "     gl_PointSize = 10.0;\n" +
                    "}\n";


    private static final String fragmentShaderCode_border_block =
            "#version 300 es\n" +
                    "precision mediump float;\n" +

                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
//                    "     fragColor = outColor;\n" +
                    "     fragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
                    "}\n";


    public static void initProgram(){
        mProgram_axis = initShaderProgram(TAG, vertexShaderCode_axis, fragmentShaderCode_axis);
        Log.v(TAG, "mProgram_axis: " + Integer.toString(mProgram_axis));

        mProgram_border_img = initShaderProgram(TAG, vertexShaderCode_border_img, fragmentShaderCode_border_img);
        Log.v(TAG, "mProgram_border_img: : " + Integer.toString(mProgram_border_img));

        mProgram_border_block = initShaderProgram(TAG, vertexShaderCode_border_block, fragmentShaderCode_border_block);
        Log.v(TAG, "mProgram_border_block: " + Integer.toString(mProgram_border_block));
    }

    public MyNavLoc(float[] dim, float[] block_pos){

        setPoints(dim, block_pos);

        bufferSet();

    }


    private void setPoints(float[] mz, float[] mz_block){

        vertexAxis = new float[]{
                // x axis
                (1.0f - 0.0f) * mz[0], (1.0f - 0.0f) * mz[1], 0.0f,
                (1.0f - 0.2f) * mz[0], (1.0f - 0.0f) * mz[1], 0.0f,

                // y axis
                (1.0f - 0.0f) * mz[0], (1.0f - 0.0f) * mz[1], 0.0f,
                (1.0f - 0.0f) * mz[0], (1.0f - 0.2f) * mz[1], 0.0f,

                // z axis
                (1.0f - 0.0f) * mz[0], (1.0f - 0.0f) * mz[1], 0.0f * mz[2],
                (1.0f - 0.0f) * mz[0], (1.0f - 0.0f) * mz[1], 0.2f * mz[2],

        };

        vertexBorder_img = new float[]{
                // x axis
                0f,     0f,     0f,  // num 0
                0f,     0f,     mz[2],  // num 1
                0f,     mz[1],  0f,  // num 2
                0f,     mz[1],  mz[2],  // num 3
                mz[0],  0f,     0f,  // num 4
                mz[0],  mz[1],  0f,  // num 5
                mz[0],  0f,     mz[2],  // num 6
                mz[0],  mz[1],  mz[2],  // num 7
        };


        vertexBorder_block = new float[]{
                // x axis
                mz_block[0],     mz_block[2],     mz_block[4],  // num 0
                mz_block[0],     mz_block[2],     mz_block[5],  // num 1
                mz_block[0],     mz_block[3],     mz_block[4],  // num 2
                mz_block[0],     mz_block[3],     mz_block[5],  // num 3
                mz_block[1],     mz_block[2],     mz_block[4],  // num 4
                mz_block[1],     mz_block[3],     mz_block[4],  // num 5
                mz_block[1],     mz_block[2],     mz_block[5],  // num 6
                mz_block[1],     mz_block[3],     mz_block[5],  // num 7
        };

    }


    public void draw(float[] mvpMatrix){
        //draw the border of img
        draw_border_img(mvpMatrix);

        //draw the border of block
        draw_border_block(mvpMatrix);

        //draw the axis
        draw_axis(mvpMatrix);
    }



    public void draw_axis(float[] mvpMatrix){

        GLES30.glUseProgram(mProgram_axis);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_axis);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        //准备坐标数据
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer_axis);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(1);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_axis = GLES30.glGetUniformLocation(mProgram_axis,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_axis, 1, false, mvpMatrix, 0);

        //绘制三个点
//        GLES30.glLineWidth(10);
        GLES30.glLineWidth(5);
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, 6);


        //绘制直线
//        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, 2);
//        GLES30.glLineWidth(10);

//        //绘制三角形
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);

    }



    public void draw_border_img(float[] mvpMatrix){

        GLES30.glUseProgram(mProgram_border_img);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_border_img);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_border = GLES30.glGetUniformLocation(mProgram_border_img,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_border, 1, false, mvpMatrix, 0);

        //绘制三个点
        GLES30.glLineWidth(3);
        GLES30.glDrawElements(GLES30.GL_LINES, 24, GLES30.GL_UNSIGNED_SHORT, ListBuffer_border);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);


    }


    private void draw_border_block(float[] mvpMatrix){

        GLES30.glUseProgram(mProgram_border_block);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_border_block);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_border = GLES30.glGetUniformLocation(mProgram_border_block,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_border, 1, false, mvpMatrix, 0);

        //绘制三个点
        GLES30.glLineWidth(2);
        GLES30.glDrawElements(GLES30.GL_LINES, 24, GLES30.GL_UNSIGNED_SHORT, ListBuffer_border);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);
    }

    private void bufferSet(){

        // for the axis
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_axis = ByteBuffer.allocateDirect(vertexAxis.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_axis.put(vertexAxis);
        vertexBuffer_axis.position(0);


        //分配内存空间,每个浮点型占4字节空间
        colorBuffer_axis = ByteBuffer.allocateDirect(colorAxis.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的颜色数据
        colorBuffer_axis.put(colorAxis);
        colorBuffer_axis.position(0);



        // for the border of img
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_border_img = ByteBuffer.allocateDirect(vertexBorder_img.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_border_img.put(vertexBorder_img);
        vertexBuffer_border_img.position(0);


        // for the border of block
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_border_block = ByteBuffer.allocateDirect(vertexBorder_block.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_border_block.put(vertexBorder_block);
        vertexBuffer_border_block.position(0);


        //分配内存空间,每个浮点型占4字节空间
        ListBuffer_border = ByteBuffer.allocateDirect(BorderList.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的颜色数据
        ListBuffer_border.put(BorderList);
        ListBuffer_border.position(0);


    }

}
