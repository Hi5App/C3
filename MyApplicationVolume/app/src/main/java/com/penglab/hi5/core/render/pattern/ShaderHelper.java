package com.penglab.hi5.core.render.pattern;

import android.opengl.GLES32;
import android.util.Log;

public class ShaderHelper {

    // 创建着色器程序 ------------------------------------------------------------------------------
    public static int initShaderProgram(String TAG, String vertShaderCode, String fragmShaderCode){

        // 加载着色器
        int vertexShader = loadShader(TAG, GLES32.GL_VERTEX_SHADER,
                vertShaderCode);
        int fragmentShader = loadShader(TAG, GLES32.GL_FRAGMENT_SHADER,
                fragmShaderCode);

        // create empty OpenGL ES Program
        int mProgram = GLES32.glCreateProgram();

        // add the vertex shader to program
        GLES32.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES32.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES32.glLinkProgram(mProgram);

        GLES32.glValidateProgram(mProgram);   // 让OpenGL来验证一下我们的shader program，并获取验证的状态
        int[] status = new int[1];
        GLES32.glGetProgramiv(mProgram, GLES32.GL_VALIDATE_STATUS, status, 0);     // 获取验证的状态
        if (status[0] == 0) {
            String error = GLES32.glGetProgramInfoLog(mProgram);
            Log.e(TAG, "Error: validate shader program: " + error);
        }

        return mProgram;

    }

    // 加载着色器 ----------------------------------------------------------------------------------
    public static int loadShader(String TAG, int type, String shaderCode){

        // create a vertex shader type (GLES32.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES32.GL_FRAGMENT_SHADER)
        int shader = GLES32.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES32.glShaderSource(shader, shaderCode);
        GLES32.glCompileShader(shader);

        int[] status = new int[1];
        GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, status, 0);        // 获取验证的状态
        if (status[0] == 0) {
            String error = GLES32.glGetShaderInfoLog(shader);
            Log.e(TAG, "Error: validate shader code: " + error);
        }
        return shader;
    }

}
