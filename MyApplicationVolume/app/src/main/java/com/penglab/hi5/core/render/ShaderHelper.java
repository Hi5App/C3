package com.penglab.hi5.core.render;

import android.opengl.GLES30;
import android.util.Log;

public class ShaderHelper {

    // 创建着色器程序 ------------------------------------------------------------------------------
    public static int initShaderProgram(String TAG, String vertShaderCode, String fragmShaderCode){

        //加载着色器
        int vertexShader = loadShader(TAG, GLES30.GL_VERTEX_SHADER,
                vertShaderCode);
        int fragmentShader = loadShader(TAG, GLES30.GL_FRAGMENT_SHADER,
                fragmShaderCode);

        // create empty OpenGL ES Program
        int mProgram = GLES30.glCreateProgram();

        // add the vertex shader to program
        GLES30.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES30.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES30.glLinkProgram(mProgram);


        GLES30.glValidateProgram(mProgram);   // 让OpenGL来验证一下我们的shader program，并获取验证的状态
        int[] status = new int[1];
        GLES30.glGetProgramiv(mProgram, GLES30.GL_VALIDATE_STATUS, status, 0);     // 获取验证的状态
        if (status[0] == 0) {
            String error = GLES30.glGetProgramInfoLog(mProgram);
            Log.v(TAG, "Error: validate shader program: " + error);
        }

        return mProgram;

    }





    // 加载着色器 ----------------------------------------------------------------------------------
    public static int loadShader(String TAG, int type, String shaderCode){

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        int[] status = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, status, 0);        // 获取验证的状态
        if (status[0] == 0) {
            String error = GLES30.glGetShaderInfoLog(shader);
            Log.v(TAG, "Error: validate shader code: " + error);
        }
        return shader;
    }

}
