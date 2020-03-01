package com.example.myapplication__volume;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class MyDraw {

    float n = 100;
//    final float radius = 0.1f;
    final float radius = 0.02f;

    private final int mProgram_marker;
    private final int mProgram_line;

    private FloatBuffer vertexBuffer_marker;
    private FloatBuffer normalizeBuffer_marker;
    private FloatBuffer vertexBuffer_line;

    float[] normalMatrix = new float[16];
    float[] normalMatrix_before = new float[16];

    private float[] normalizePoints_marker;
    private float[] vertexPoints_marker;

    private int vertexPoints_handle = 0;
    private int normalizePoints_handle = 1;


//    private float[] normalizePoints_marker = createNormlizes();

    private final String vertexShaderCode_line =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;" +
//                    "layout (location = 1) in vec3 aVertexNormal;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "    gl_Position = uMVPMatrix * vPosition;" +
//                    "  gl_Position = vec4((uMVPMatrix * vPosition).xy, -0.99999, 1.0);" +
                    "}";



    private final String fragmentShaderCode_line =
            "#version 300 es\n" +
                    "precision mediump float;" +
                    "out vec4 FragColor;" +
                    "void main() {" +
                    "  FragColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                    "}";




    private final String vertexShaderCode_marker =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;" +
                    "layout (location = 1) in vec3 aVertexNormal;" +

                    "uniform mat4 uNormalMatrix;" +
                    "uniform mat4 uMVPMatrix;" +

                    "out vec3 vLighting;" +

                    "void main() {" +
                    "    gl_Position = uMVPMatrix * vPosition;" +

                    "    vec3 uAmbientColor = vec3(0.2, 0.2, 0.2);\n" +
                    "    vec3 uDirectionalColor = vec3(0.8, 0.8, 0.8);\n" +
                    "    vec3 directionalVector = normalize(vec3(-1.0, 1.0, -1.0));\n" +
                    "    vec3 transformedNormal = (uNormalMatrix * vec4(aVertexNormal, 1.0)).xyz;\n" +
                    "    float directionalLightWeighting = max(dot(transformedNormal, directionalVector), 0.0);\n" +
                    "    vLighting = uAmbientColor + uDirectionalColor * directionalLightWeighting;" +
                    "}";



    private final String fragmentShaderCode_maeker =
            "#version 300 es\n" +
                    "precision mediump float;" +

                    "in vec3 vLighting;" +
                    "out vec4 FragColor;" +

                    "void main() {" +
//                    "  vec4 markerColor = vec4(0.29, 0.13, 0.36, 1.0);" +
                    "  vec4 markerColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                    "  FragColor = vec4(markerColor.rgb * vLighting, 1.0);" +
                    "}";




    public MyDraw(){

        //for the marker
        mProgram_marker = initProgram(vertexShaderCode_marker, fragmentShaderCode_maeker);
        Log.v("MyDraw", "init the mProgram_marker");

        //for the line
        mProgram_line = initProgram(vertexShaderCode_line, fragmentShaderCode_line);
        Log.v("MyDraw", "init the mProgram_line");

        BufferSet_Normalize();

    }


    private void BufferSet_Marker(float x, float y, float z){

        vertexPoints_marker = createPositions(x, y, z);

        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_marker = ByteBuffer.allocateDirect(vertexPoints_marker.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_marker.put(vertexPoints_marker);
        vertexBuffer_marker.position(0);

    }


    private void BufferSet_Normalize(){

        normalizePoints_marker = createNormlizes();

        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        normalizeBuffer_marker = ByteBuffer.allocateDirect(normalizePoints_marker.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        normalizeBuffer_marker.put(normalizePoints_marker);
        normalizeBuffer_marker.position(0);

    }




    private void BufferSet_Line(float [] line){
        vertexBuffer_line = ByteBuffer.allocateDirect(line.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer_line.put(line);
        vertexBuffer_line.position(0);
    }





//    public void drawMarker(float[] mvpMatrix, float x, float y, float z){
//
//        BufferSet_Marker(x, y, z);
//
//        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
//
//        GLES30.glUseProgram(mProgram_marker);
//
////        Matrix.invertM(normalMatrix_before,0,mvpMatrix,0);
////
////        Matrix.transposeM(normalMatrix,0,normalMatrix_before,0);
//
//        //准备坐标数据
//        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker);
//        //启用顶点的句柄
//        GLES30.glEnableVertexAttribArray(0);
//
////        //准备f法向量数据
////        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, normalizeBuffer_marker);
////        //启用顶点的句柄
////        GLES30.glEnableVertexAttribArray(1);
//
//        // get handle to vertex shader's uMVPMatrix member
//        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_marker,"uMVPMatrix");
//
//        // Pass the projection and view transformation to the shader
//        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);
//
//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker.length/3);
//
//        //禁止顶点数组的句柄
//        GLES30.glDisableVertexAttribArray(0);
//
//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
//
//    }


    public void drawMarker(float[] mvpMatrix, float[] modelMatrix, float x, float y, float z){

        BufferSet_Marker(x, y, z);


        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glUseProgram(mProgram_marker);


        //准备坐标数据
        GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(vertexPoints_handle);


        //准备f法向量数据
        GLES30.glVertexAttribPointer(normalizePoints_handle, 3, GLES30.GL_FLOAT, false, 0, normalizeBuffer_marker);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(normalizePoints_handle);


        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_marker,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);


        // get handle to vertex shader's uMVPMatrix member
        int normalizeMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_marker,"uNormalMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(normalizeMatrixHandle_marker, 1, false, modelMatrix, 0);


        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker.length/3);


        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(vertexPoints_handle);
        GLES30.glDisableVertexAttribArray(normalizePoints_handle);


        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

    }



    public void drawLine(float [] mvpMatrix, ArrayList<Float> lineDrawed){
        float [] line = new float[lineDrawed.size()];
        for (int i = 0; i < lineDrawed.size(); i++){
            line[i] = lineDrawed.get(i);
        }
        BufferSet_Line(line);

        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glUseProgram(mProgram_line);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_line);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_line,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);

        GLES30.glLineWidth(3);

        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, line.length/3);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    }

    public void drawEswc(float [] mvpMatrix, ArrayList<Float> lineDrawed){
        float [] line = new float[lineDrawed.size()];
        for (int i = 0; i < lineDrawed.size(); i++){
            line[i] = lineDrawed.get(i);
        }
        BufferSet_Line(line);

        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glUseProgram(mProgram_line);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_line);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_line,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);

        GLES30.glLineWidth(3);

        GLES30.glDrawArrays(GLES30.GL_LINES, 0, line.length/3);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
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



    private float[]  createPositions(float x, float y, float z){

////        float x, y, z;
//        x = 0.5f;  y = 0.1f;  z = 0.2f;

        float step = 2.0f;
        ArrayList<Float> data=new ArrayList<>();
        float r1,r2;
        float h1,h2;
        float sin,cos;
        for(float i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0)          * radius;
            r2 = (float)Math.cos((i + step) * Math.PI / 180.0) * radius;
            h1 = (float)Math.sin(i * Math.PI / 180.0)          * radius;
            h2 = (float)Math.sin((i + step) * Math.PI / 180.0) * radius;
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (float j = 0.0f; j <360.0f+step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);

                data.add(r2 * cos + x);
                data.add(h2       + y);
                data.add(r2 * sin + z);

                data.add(r1 * cos + x);
                data.add(h1       + y);
                data.add(r1 * sin + z);
            }
        }
        float[] f=new float[data.size()];
        for(int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }

        return f;
    }



    private float[]  createNormlizes(){

        float step = 2.0f;
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

}
