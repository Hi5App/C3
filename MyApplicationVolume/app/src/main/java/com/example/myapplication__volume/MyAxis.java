package com.example.myapplication__volume;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class MyAxis {

    private final int mProgram_axis;

    private FloatBuffer vertexBuffer_axis;
    private FloatBuffer colorBuffer_axis;

    private final int mProgram_border;

    private FloatBuffer vertexBuffer_border;
    private ShortBuffer ListBuffer_border;


    //坐标系
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final float[] vertexAxis={
            // x axis
            1.0f - 0.0f, 1.0f - 0.0f, 0.0f,
            1.0f - 1.1f, 1.0f - 0.0f, 0.0f,

            // y axis
            1.0f - 0.0f, 1.0f - 0.0f, 0.0f,
            1.0f - 0.0f, 1.0f - 1.1f, 0.0f,

            // z axis
            1.0f - 0.0f, 1.0f - 0.0f, 0.0f,
            1.0f - 0.0f, 1.0f - 0.0f, 1.1f,

    };


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


    private final float[] vertexBorder= {
            // x axis
            0f, 0f, 0f,  // num 0
            0f, 0f, 1f,  // num 1
            0f, 1f, 0f,  // num 2
            0f, 1f, 1f,  // num 3
            1f, 0f, 0f,  // num 4
            1f, 1f, 0f,  // num 5
            1f, 0f, 1f,  // num 6
            1f, 1f, 1f,  // num 7
    };


    private final short[] BorderList= {
            0, 1,   0, 2,   0, 4,
            6, 1,   6, 4,   6, 7,
            3, 1,   3, 2,   3, 7,
    };



    //draw the axis
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private final String vertexShaderCode_axis =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +
                    "layout (location = 1) in vec4 vColor;\n" +

                    "uniform mat4 uMVPMatrix;" +
                    "out vec4 outColor;\n" +

                    "void main() {\n" +
                    "     gl_Position  = uMVPMatrix * vPosition;\n" +
                    "     gl_PointSize = 20.0;\n" +
                    "     outColor  = vColor;\n" + //

                    "}\n";


    private final String fragmentShaderCode_axis =
            "#version 300 es\n" +
                    "precision mediump float;\n" +

                    "in vec4 outColor;\n" +
                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
                    "     fragColor = outColor;\n" +
//                    "     fragColor = vec4(1.0,1.0,1.0,1.0);\n" +
                    "}\n";



    //draw the border
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private final String vertexShaderCode_border =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +

                    "uniform mat4 uMVPMatrix;" +

                    "void main() {\n" +
                    "     gl_Position  = uMVPMatrix * vPosition;\n" +
                    "     gl_PointSize = 10.0;\n" +
                    "}\n";


    private final String fragmentShaderCode_border =
            "#version 300 es\n" +
                    "precision mediump float;\n" +

                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
//                    "     fragColor = outColor;\n" +
                    "     fragColor = vec4(0.75, 0.75, 0.75,1.0);\n" +
                    "}\n";


    public MyAxis(){

        mProgram_axis = initProgram(vertexShaderCode_axis, fragmentShaderCode_axis);
        Log.v("mProgram_line", Integer.toString(mProgram_axis));

        mProgram_border = initProgram(vertexShaderCode_border, fragmentShaderCode_border);
        Log.v("mProgram_border", Integer.toString(mProgram_border));

        BufferSet();

    }


    public void draw(float[] mvpMatrix){
        //draw the border
        draw_border(mvpMatrix);

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
        GLES30.glLineWidth(10);
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



    public void draw_border(float[] mvpMatrix){

        GLES30.glUseProgram(mProgram_border);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_border);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_border = GLES30.glGetUniformLocation(mProgram_border,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_border, 1, false, mvpMatrix, 0);

        //绘制三个点
        GLES30.glLineWidth(3);
        GLES30.glDrawElements(GLES30.GL_LINES, 18, GLES30.GL_UNSIGNED_SHORT, ListBuffer_border);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);

    }


    private void BufferSet(){

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



        // for the border
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_border = ByteBuffer.allocateDirect(vertexBorder.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_border.put(vertexBorder);
        vertexBuffer_border.position(0);


        //分配内存空间,每个浮点型占4字节空间
        ListBuffer_border = ByteBuffer.allocateDirect(BorderList.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的颜色数据
        ListBuffer_border.put(BorderList);
        ListBuffer_border.position(0);


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


}
