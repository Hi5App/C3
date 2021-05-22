package com.penglab.hi5.core.render;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import static com.penglab.hi5.core.render.ShaderHelper.initShaderProgram;

public class MyDraw {
    private static final String TAG = "MyDraw";

    public final static float[][] colormap = {
            {1f,1f,1f},                        //white
            {0.07843f,0.07843f,0.07843f},      //black
            {0.7843f,0.07843f,0f},             //red
            {0f,0.07843f,0.7843f},             //blue
            {0.7843f,0f,0.7843f},              //purple
            {0f,0.7843f,0.7843f},              //cyan
            {0.8627f,0.7843f,0f},              //yellow
            {0f,0.7843f,0.07843f}              //green
    } ;

    private final float [] modelVertex = {
        //1:123
        0.020f, 0.000f, 0.010f,
        0.007f, 0.000f, 0.010f,
        0.007f, 0.002f, 0.010f,
        //2:345
        0.007f, 0.002f, 0.010f,
        -0.007f, 0.002f, 0.010f,
        0.000f, 0.010f, 0.010f,
        //3:467
        -0.007f, 0.002f, 0.010f,
        -0.007f, 0.000f, 0.010f,
        -0.020f, 0.000f, 0.010f,
        //4:234
        0.007f, 0.000f, 0.010f,
        0.007f, 0.002f, 0.010f,
        -0.007f, 0.002f, 0.010f,
        //5:264
        0.007f, 0.000f, 0.010f,
        -0.007f, 0.000f, 0.010f,
        -0.007f, 0.002f, 0.010f,
        //6:826
        0.000f, 0.000f, -0.010f,
        0.007f, 0.000f, 0.010f,
        -0.007f, 0.000f, 0.010f,
        //7:821
        0.000f, 0.000f, -0.010f,
        0.007f, 0.000f, 0.010f,
        0.020f, 0.000f, 0.010f,
        //8:813
        0.000f, 0.000f, -0.010f,
        0.020f, 0.000f, 0.010f,
        0.007f, 0.002f, 0.010f,
        //9:835
        0.000f, 0.000f, -0.010f,
        0.007f, 0.002f, 0.010f,
        0.000f, 0.010f, 0.010f,
        //10:854
        0.000f, 0.000f, -0.010f,
        0.000f, 0.010f, 0.010f,
        -0.007f, 0.002f, 0.010f,
        //11:847
        0.000f, 0.000f, -0.010f,
        -0.007f, 0.002f, 0.010f,
        -0.020f, 0.000f, 0.010f,
        //12:876
        0.000f, 0.000f, -0.010f,
        -0.020f, 0.000f, 0.010f,
        -0.007f, 0.000f, 0.010f,
    };



    private final float[] Vertex = {

             0.020f, 0.000f, -0.010f,   // 0
             0.007f, 0.000f, -0.010f,   // 1
             0.007f, 0.002f, -0.010f,   // 2
            -0.007f, 0.002f, -0.010f,   // 3
             0.000f, 0.010f, -0.010f,   // 4
            -0.007f, 0.000f, -0.010f,   // 5
            -0.020f, 0.000f, -0.010f,   // 6
             0.000f, 0.000f,  0.020f,   // 7

    };

    private final short[] drawList_model = {
            0, 2, 1,    2, 4, 3,    3, 6, 5,
            2, 3, 5,    2, 5, 1,    0, 7, 6,
            0, 7, 2,    2, 7, 4,    7, 3, 4,    7, 6, 3
    };


    private final short[] drawList_skeleton= {
            0, 1,   0, 2,   1, 2,   2, 3,   1, 5,
            3, 5,   3, 6,   5, 6,   2, 4,   3, 4,
            7, 0,   7, 1,   7, 2,   7, 3,   7, 4,   7, 5,   7, 6
    };

//    private float [] modelVertex = {
//            0.000f, 0.000f, 0.010f,
//            0.010f, 0.000f, -0.005f,
//            -0.010f, 0.000f, -0.005f,
//    };

   float n = 100;
//    final float radius = 0.1f;
//    final float radius = 0.02f;
    final float splitRadius = 0.005f;

    private static int mProgram_marker;
    private static int mProgram_line;
    private static int mProgram_points;
    private static int mProgram_game;

    private FloatBuffer vertexBuffer_marker;
    private FloatBuffer vertexBuffer_marker_0_005;
    private FloatBuffer vertexBuffer_marker_0_01;
    private FloatBuffer vertexBuffer_marker_0_02;
    private FloatBuffer colorBuffer_marker;
    private FloatBuffer colorBuffer_model;
    private FloatBuffer colorBuffer_skeleton;
    private FloatBuffer normalizeBuffer_marker;
    private FloatBuffer normalizeBuffer_marker_small;
    private FloatBuffer vertexBuffer_line;
    private FloatBuffer colorBuffer_line;
    private FloatBuffer vertexBuffer_points;
    private ShortBuffer drawListBuffer_model;
    private ShortBuffer drawListBuffer_skeleton;

    float[] normalMatrix = new float[16];
    float[] normalMatrix_before = new float[16];

    private float[] normalizePoints_marker;
    private float[] normalizePoints_marker_small;
    private float[] vertexPoints_marker;
    private float[] vertexPoints_marker_0_005;
    private float[] vertexPoints_marker_0_01;
    private float[] vertexPoints_marker_0_02;
    private float[] colorPoints_marker;
    private float[] colorPoints_model;
    private float[] colorPoints_skeleton;

    private static int vertexPoints_handle = 0;
    private static int normalizePoints_handle = 1;
    private static int colorPoints_handle = 2;

    private static final String vertexShaderCode_line =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;" +
//                    "layout (location = 1) in vec3 aVertexNormal;" +
                    "layout (location = 2) in vec4 vColor;" +
                    "uniform mat4 uMVPMatrix;" +
                    "out vec4 vOutColor;" +
                    "void main() {" +
                    "    gl_Position = uMVPMatrix * vPosition;" +
//                    "  gl_Position = vec4((uMVPMatrix * vPosition).xy, -0.99999, 1.0);" +
                    "    vOutColor = vColor;" +
                    "}";



    private static final String fragmentShaderCode_line =
            "#version 300 es\n" +
                    "precision mediump float;" +
                    "in vec4 vOutColor;" +
                    "out vec4 FragColor;" +
                    "void main() {" +
//                    "  FragColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                    "  FragColor = vec4(vOutColor.rgb,1.0);" +
                    "}";




    private static final String vertexShaderCode_marker =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;" +
                    "layout (location = 1) in vec3 aVertexNormal;" +



                    "uniform mat4 uNormalMatrix;" +
                    "uniform mat4 uMVPMatrix;" +
                    "uniform float offset[3];" +
                    "uniform float color[3];" +


                    "out vec3 vLighting;" +
                    "out vec4 vOutColor;" +

                    "void main() {\n" +
//                    "    vOffset = vec4(0.5, 0.5, 0.5, 0.0);" +
                    "    vec4 vOffsetPosition = vPosition + vec4(offset[0], offset[1], offset[2], 0.0);\n" +
//                    "    vPosition.x = 0.5;" +
//                    "    vPosition.y = 0.5;" +
//                    "    vPosition.z = 0.5;" +
//                    "    vPosition = vec4(0.5, 0.5, 0.5, 1.0);" +


                    "    gl_Position = uMVPMatrix * vOffsetPosition;\n" +

                    "    vec3 uAmbientColor = vec3(0.2, 0.2, 0.2);\n" +
                    "    vec3 uDirectionalColor = vec3(0.8, 0.8, 0.8);\n" +
                    "    vec3 directionalVector = normalize(vec3(-1.0, 1.0, -1.0));\n" +
                    "    vec3 transformedNormal = (uNormalMatrix * vec4(aVertexNormal, 1.0)).xyz;\n" +
                    "    float directionalLightWeighting = max(dot(transformedNormal, directionalVector), 0.0);\n" +
                    "    vLighting = uAmbientColor + uDirectionalColor * directionalLightWeighting;\n" +
                    "    vOutColor = vec4(color[0], color[1], color[2], 1.0);\n" +
                    "}";



    private static final String fragmentShaderCode_marker =
            "#version 300 es\n" +
                    "precision mediump float;" +

                    "in vec4 vOutColor;" +
                    "in vec3 vLighting;" +
                    "out vec4 FragColor;" +

                    "void main() {" +
//                    "  vec4 markerColor = vec4(0.29, 0.13, 0.36, 1.0);" +
//                    "  vec4 markerColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                    "  vec4 markerColor = vOutColor;"+
                    "  FragColor = vec4(markerColor.rgb * vLighting, 1.0);" +
                    "}";

    private static final String vertexShaderCode_game =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;" +
                    "layout (location = 2) in vec4 vColor;"+

                    "uniform mat4 uMVPMatrix;" +

                    "out vec4 vOutColor;" +

                    "void main() {" +
                    "    gl_Position = vPosition;" +
                    "    vOutColor = vColor;"+
                    "}";

    private static final String fragmentShaderCode_game =
            "#version 300 es\n" +
                    "precision mediump float;" +

                    "in vec4 vOutColor;" +

                    "out vec4 FragColor;" +

                    "void main() {" +
                    "  FragColor = vOutColor;"+
                    "}";

    // draw points ------------------------------------------------------------------------------
    private static final String vertexShaderCode_points =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 vPosition;\n" +
                    "void main() {\n" +
                    "     gl_Position  = vPosition;\n" + //
                    "     gl_PointSize = 20.0;\n" +
                    "}\n";


    private static final String fragmentShaderCode_points =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
                    "    if (length(gl_PointCoord  - vec2(0.5)) > 0.5) {\n" +
                    "        discard;\n" +
                    "    }" +
                    "    fragColor = vec4(1.0,1.0,1.0,1.0);\n" +
                    "}\n";


    public static void initProgram(){
        //for the marker
        mProgram_marker = initShaderProgram(TAG, vertexShaderCode_marker, fragmentShaderCode_marker);
        Log.v(TAG,"mProgram_marker: " + mProgram_marker);

        //for the line
        mProgram_line = initShaderProgram(TAG, vertexShaderCode_line, fragmentShaderCode_line);
        Log.v(TAG, "mProgram_line: " + mProgram_line);

        //for the points
        mProgram_points = initShaderProgram(TAG, vertexShaderCode_points, fragmentShaderCode_points);
        Log.v(TAG, "mProgram_points: " + mProgram_points);

        mProgram_game = initShaderProgram(TAG, vertexShaderCode_game, fragmentShaderCode_game);
        Log.v(TAG, "mProgram_game: " + mProgram_game);

    }


    public MyDraw(){

        BufferSet_Normalize();

        BufferSet_Marker_Default();

//        BufferSet_Marker_Vertex(0.02f);

    }

    private void BufferSet_GameModel(float x, float y, float z, int type, float [] dir, float [] head){

        float [] vertexAfterMove = new float[modelVertex.length];
        float [] axis = new float[]{dir[1] * head[2] - dir[2] * head[1], dir[2] * head[0] - dir[0] * head[2], dir[1] * head[2] - dir[2] * head[1]};

//        for (int i = 0; i < modelVertex.length / 3; i++){
//            vertexAfterMove[i * 3] = x + (modelVertex[i * 3] * axis[0] + modelVertex[i * 3 + 1] * head[0] + modelVertex[i * 3 + 2] * dir[0]) * 10;
//            vertexAfterMove[i * 3 + 1] = y + (modelVertex[i * 3] * axis[1] + modelVertex[i * 3 + 1] * head[1] + modelVertex[i * 3 + 2] * dir[1]) * 10;
//            vertexAfterMove[i * 3 + 2] = z + (modelVertex[i * 3] * axis[2] + modelVertex[i * 3 + 1] * head[2] + modelVertex[i * 3 + 2] * dir[2]) * 10;
//        }


        for (int i = 0; i < Vertex.length / 3; i++){
//            vertexAfterMove[i * 3] = x + (Vertex[i * 3] * axis[0] + Vertex[i * 3 + 1] * head[0] + Vertex[i * 3 + 2] * dir[0]) * 10;
//            vertexAfterMove[i * 3 + 1] = y + (Vertex[i * 3] * axis[1] + Vertex[i * 3 + 1] * head[1] + Vertex[i * 3 + 2] * dir[1]) * 10;
//            vertexAfterMove[i * 3 + 2] = z + (Vertex[i * 3] * axis[2] + Vertex[i * 3 + 1] * head[2] + Vertex[i * 3 + 2] * dir[2]) * 10;
            vertexAfterMove[i * 3] = x + (Vertex[i * 3]) * 10;
            vertexAfterMove[i * 3 + 1] = y + (Vertex[i * 3 + 1]) * 10;
            vertexAfterMove[i * 3 + 2] = z + (Vertex[i * 3 + 2]) * 10;
        }
//
//        vertexAfterMove[0] = x + 0.010f * dir[0];
//        vertexAfterMove[1] = y + 0.010f * dir[1];
//        vertexAfterMove[2] = z + 0.010f * dir[2];
//
//        vertexAfterMove[3] = x + 0.005f * axis[0];
//        vertexAfterMove[4] = y + 0.005f * axis[1];
//        vertexAfterMove[5] = z + 0.005f * axis[2];
//
//        vertexAfterMove[6] = x - 0.005f * axis[0];
//        vertexAfterMove[7] = y - 0.005f * axis[1];
//        vertexAfterMove[8] = z - 0.005f * axis[2];


//        for (int i = 0; i < modelVertex.length / 3; i++){
//            vertexAfterMove[i * 3] = modelVertex[i * 3] * 10 + x;
//            vertexAfterMove[i * 3 + 1] = modelVertex[i * 3 + 1] * 10 + y;
//            vertexAfterMove[i * 3 + 2] = modelVertex[i * 3 + 2] * 10 + z;
//        }

        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_marker = ByteBuffer.allocateDirect(vertexAfterMove.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_marker.put(vertexAfterMove);
        vertexBuffer_marker.position(0);

        colorPoints_model = new float[drawList_model.length * 3];//colormap[type];
        for(int i=0; i<colorPoints_model.length; i++){
            colorPoints_model[i] = colormap[type%8][i%3];
        }
        colorBuffer_model = ByteBuffer.allocateDirect(colorPoints_model.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer_model.put(colorPoints_model);
        colorBuffer_model.position(0);


        colorPoints_skeleton = new float[drawList_skeleton.length * 3];//colormap[type];
        for(int i=0; i<colorPoints_skeleton.length; i++){
            colorPoints_skeleton[i] = colormap[0][i%3];
        }
        colorBuffer_skeleton = ByteBuffer.allocateDirect(colorPoints_skeleton.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer_skeleton.put(colorPoints_skeleton);
        colorBuffer_skeleton.position(0);


        //分配内存空间,每个short型占4字节空间
        drawListBuffer_model = ByteBuffer.allocateDirect(drawList_model.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的索引数据
        drawListBuffer_model.put(drawList_model);
        drawListBuffer_model.position(0);

        //分配内存空间,每个short型占4字节空间
        drawListBuffer_skeleton = ByteBuffer.allocateDirect(drawList_skeleton.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        //传入指定的索引数据
        drawListBuffer_skeleton.put(drawList_skeleton);
        drawListBuffer_skeleton.position(0);


    }

    private void BufferSet_Marker_Default(){
        vertexPoints_marker_0_005 = createPositions(0f, 0f, 0f, 0.005f);

        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_marker_0_005 = ByteBuffer.allocateDirect(vertexPoints_marker_0_005.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_marker_0_005.put(vertexPoints_marker_0_005);
        vertexBuffer_marker_0_005.position(0);

        vertexPoints_marker_0_01 = createPositions(0f, 0f, 0f, 0.01f);

        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_marker_0_01 = ByteBuffer.allocateDirect(vertexPoints_marker_0_01.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_marker_0_01.put(vertexPoints_marker_0_01);
        vertexBuffer_marker_0_01.position(0);

        vertexPoints_marker_0_02 = createPositions(0f, 0f, 0f, 0.02f);

        Log.d(TAG, "vertexPoints_marker_0_02: " + Integer.toString(vertexPoints_marker_0_02.length));

        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_marker_0_02 = ByteBuffer.allocateDirect(vertexPoints_marker_0_02.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_marker_0_02.put(vertexPoints_marker_0_02);
        vertexBuffer_marker_0_02.position(0);
    }


    private void BufferSet_Marker(float x, float y, float z, int type, float r){

        vertexPoints_marker = createPositions(x, y, z, r);

        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_marker = ByteBuffer.allocateDirect(vertexPoints_marker.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_marker.put(vertexPoints_marker);
        vertexBuffer_marker.position(0);

        colorPoints_marker = new float[vertexPoints_marker.length]; //colormap[type];
        for(int i=0; i<colorPoints_marker.length; i++){
            colorPoints_marker[i] = colormap[type % 8][i % 3];
        }
        colorBuffer_marker = ByteBuffer.allocateDirect(colorPoints_marker.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer_marker.put(colorPoints_marker);
        colorBuffer_marker.position(0);

    }

    private void BufferSet_Marker_Vertex(float r){
        vertexPoints_marker = createPositions(0f, 0f, 0f, r);


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

        normalizePoints_marker = createNormlizes(2.0f);

        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        normalizeBuffer_marker = ByteBuffer.allocateDirect(normalizePoints_marker.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        normalizeBuffer_marker.put(normalizePoints_marker);
        normalizeBuffer_marker.position(0);
        normalizePoints_marker_small = createNormlizes(6.0f);

        // for the marker
        //分配内存空间,每个浮点型占4字节空间
        normalizeBuffer_marker_small = ByteBuffer.allocateDirect(normalizePoints_marker_small.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        normalizeBuffer_marker_small.put(normalizePoints_marker_small);
        normalizeBuffer_marker_small.position(0);

    }

    private void BufferSet_Points(float [] vertexPoints_points){
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer_points = ByteBuffer.allocateDirect(vertexPoints_points.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer_points.put(vertexPoints_points);
        vertexBuffer_points.position(0);
    }


    private void BufferSet_Line(float [] line, int type){
        vertexBuffer_line = ByteBuffer.allocateDirect(line.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer_line.put(line);
        vertexBuffer_line.position(0);

        colorPoints_marker = new float[line.length];
        for(int i=0; i<colorPoints_marker.length; i++){
            colorPoints_marker[i] = colormap[type%8][i%3];
//            System.out.println(colorPoints_marker[i]);
        }
        colorBuffer_line = ByteBuffer.allocateDirect(colorPoints_marker.length* 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer_line.put(colorPoints_marker);
        colorBuffer_line.position(0);

    }

    public void drawGameModel(float[] mvpMatrix, float[] modelMatrix, float x, float y, float z, int type, float [] dir, float [] head){

        float [] locAfterRotate = new float[4];
        Matrix.multiplyMV(locAfterRotate, 0, mvpMatrix, 0, new float[]{x, y, z, 1.0f}, 0);

        float [] locAfterDivide = {locAfterRotate[0] / locAfterRotate[3], locAfterRotate[1] / locAfterRotate[3], locAfterRotate[2] / locAfterRotate[3]};

        
        BufferSet_GameModel(locAfterDivide[0], locAfterDivide[1], locAfterDivide[2], type, dir, head);

//        System.out.println("set marker end");

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

//        GLES30.glUseProgram(mProgram_points);
        GLES30.glUseProgram(mProgram_game);


        //准备坐标数据
        GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(vertexPoints_handle);

        //准备颜色数据
        GLES30.glVertexAttribPointer(colorPoints_handle,3,GLES30.GL_FLOAT, false, 0,colorBuffer_model);
        GLES30.glEnableVertexAttribArray(colorPoints_handle);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_game,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);


        // get handle to vertex shader's uMVPMatrix member
//        int normalizeMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_marker,"uNormalMatrix");

        // Pass the projection and view transformation to the shader
//        GLES30.glUniformMatrix4fv(normalizeMatrixHandle_marker, 1, false, modelMatrix, 0);


//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, modelVertex.length/3);

        // 通过索引来绘制
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawList_model.length, GLES30.GL_UNSIGNED_SHORT, drawListBuffer_model);

//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        GLES30.glVertexAttribPointer(colorPoints_handle,3,GLES30.GL_FLOAT, false, 0, colorBuffer_skeleton);
        GLES30.glEnableVertexAttribArray(colorPoints_handle);

        GLES30.glLineWidth(5);
        GLES30.glDrawElements(GLES30.GL_LINES, drawList_skeleton.length, GLES30.GL_UNSIGNED_SHORT, drawListBuffer_skeleton);

//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker.length/3);


        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(vertexPoints_handle);
        GLES30.glDisableVertexAttribArray(normalizePoints_handle);

        GLES30.glDisableVertexAttribArray(colorPoints_handle);


        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

    }

    public void drawMarker(float[] mvpMatrix, float[] modelMatrix, float x, float y, float z, int type, float radius){

        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        GLES30.glUseProgram(mProgram_marker);

        if (radius == 0.01f) {
            //准备坐标数据
            GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker_0_01);
        } else if (radius == 0.02f){
            //准备坐标数据
            GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker_0_02);
        } else {
            BufferSet_Marker_Vertex(radius);

            //准备坐标数据
            GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker);
        }

        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(vertexPoints_handle);

        if (radius < 0.015f){
            //准备f法向量数据
            GLES30.glVertexAttribPointer(normalizePoints_handle, 3, GLES30.GL_FLOAT, false, 0, normalizeBuffer_marker_small);
        }else {
            //准备f法向量数据
            GLES30.glVertexAttribPointer(normalizePoints_handle, 3, GLES30.GL_FLOAT, false, 0, normalizeBuffer_marker);
        }
        //启用f法向量的句柄
        GLES30.glEnableVertexAttribArray(normalizePoints_handle);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_marker,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);

        // get handle to vertex shader's uNormalMatrix member
        int normalizeMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_marker,"uNormalMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(normalizeMatrixHandle_marker, 1, false, modelMatrix, 0);

        int offsetHandle_marker = GLES30.glGetUniformLocation(mProgram_marker, "offset");

        GLES30.glUniform1fv(offsetHandle_marker, 3, new float[]{x, y, z}, 0);

        float [] colorMarker = new float[3];
        for (int i = 0; i < 3; i++) {
            colorMarker[i] = colormap[type % 8][i];
        }

        int colorHandle_marker = GLES30.glGetUniformLocation(mProgram_marker, "color");

        GLES30.glUniform1fv(colorHandle_marker, 3, colorMarker, 0);

        if (radius == 0.01f){
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker_0_01.length/3);
        } else if (radius == 0.02f) {
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker_0_02.length/3);
        } else {
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker.length / 3);
        }

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(vertexPoints_handle);
        GLES30.glDisableVertexAttribArray(normalizePoints_handle);

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

    }

    public void drawMarkerDepth(float[] mvpMatrix, float[] modelMatrix, float x, float y, float z, int type, float radius){
//        System.out.println("set marker");

        BufferSet_Marker(x, y, z, type, radius);

//        System.out.println("set marker end");

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        GLES30.glUseProgram(mProgram_marker);


        //准备坐标数据
        GLES30.glVertexAttribPointer(vertexPoints_handle, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(vertexPoints_handle);

        if (radius < 0.015f){
            //准备f法向量数据
            GLES30.glVertexAttribPointer(normalizePoints_handle, 3, GLES30.GL_FLOAT, false, 0, normalizeBuffer_marker_small);
        }else {
            //准备f法向量数据
            GLES30.glVertexAttribPointer(normalizePoints_handle, 3, GLES30.GL_FLOAT, false, 0, normalizeBuffer_marker);
        }
        //准备f法向量数据
        // GLES30.glVertexAttribPointer(normalizePoints_handle, 3, GLES30.GL_FLOAT, false, 0, normalizeBuffer_marker);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(normalizePoints_handle);

        //准备颜色数据
        GLES30.glVertexAttribPointer(colorPoints_handle,3,GLES30.GL_FLOAT, false, 0,colorBuffer_marker);
        GLES30.glEnableVertexAttribArray(colorPoints_handle);




        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_marker,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);


        // get handle to vertex shader's uNormalMatrix member
        int normalizeMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_marker,"uNormalMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(normalizeMatrixHandle_marker, 1, false, modelMatrix, 0);


        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker.length/3);

//        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker.length/3);


        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(vertexPoints_handle);
        GLES30.glDisableVertexAttribArray(normalizePoints_handle);

        GLES30.glDisableVertexAttribArray(colorPoints_handle);


        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

    }


    public void drawLine(float [] mvpMatrix, ArrayList<Float> lineDrawed, int type){
        float [] line = new float[lineDrawed.size()];

        for (int i = 0; i < lineDrawed.size(); i++){
            line[i] = lineDrawed.get(i);
        }
        BufferSet_Line(line, type);

        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glUseProgram(mProgram_line);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_line);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        //准备颜色数据
        GLES30.glVertexAttribPointer(colorPoints_handle, 3, GLES30.GL_FLOAT, false, 0, colorBuffer_line);
        //启用颜色的句柄
        GLES30.glEnableVertexAttribArray(colorPoints_handle);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_line,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);

        GLES30.glLineWidth(3);

        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, line.length/3);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);

        //禁止颜色的句柄
        GLES30.glDisableVertexAttribArray(2);

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    }

    public void drawPoints(float []  linePoints, int num){
        GLES30.glUseProgram(mProgram_points);

        BufferSet_Points(linePoints);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_points);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        //绘制点
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, num);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);
    }

    public void drawSplitPoints(float [] mvpMatrix, float x, float y, float z, int type){
        BufferSet_Marker(x, y, z, type, splitRadius);

        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glUseProgram(mProgram_line);

        //准备坐标数据
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_marker);
        //启用顶点的句柄
        GLES30.glEnableVertexAttribArray(0);

        //准备颜色数据
        GLES30.glVertexAttribPointer(colorPoints_handle, 3, GLES30.GL_FLOAT, false, 0, colorBuffer_marker);
        //启用颜色的句柄
        GLES30.glEnableVertexAttribArray(colorPoints_handle);

        // get handle to vertex shader's uMVPMatrix member
        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_line,"uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);

        GLES30.glLineWidth(3);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexPoints_marker.length/3);

        //禁止顶点数组的句柄
        GLES30.glDisableVertexAttribArray(0);

        //禁止颜色的句柄
        GLES30.glDisableVertexAttribArray(2);

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
    }

//    public void drawEswc(float [] mvpMatrix, ArrayList<Float> lineDrawed){
//        float [] line = new float[lineDrawed.size()];
//        for (int i = 0; i < lineDrawed.size(); i++){
//            line[i] = lineDrawed.get(i);
//        }
//        BufferSet_Line(line);
//
//        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
//
//        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
//
//        GLES30.glUseProgram(mProgram_line);
//
//        //准备坐标数据
//        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer_line);
//        //启用顶点的句柄
//        GLES30.glEnableVertexAttribArray(0);
//
//        // get handle to vertex shader's uMVPMatrix member
//        int vPMatrixHandle_marker = GLES30.glGetUniformLocation(mProgram_line,"uMVPMatrix");
//
//        // Pass the projection and view transformation to the shader
//        GLES30.glUniformMatrix4fv(vPMatrixHandle_marker, 1, false, mvpMatrix, 0);
//
//        GLES30.glLineWidth(3);
//
//        GLES30.glDrawArrays(GLES30.GL_LINES, 0, line.length/3);
//
//        //禁止顶点数组的句柄
//        GLES30.glDisableVertexAttribArray(0);
//
//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
//    }



    // set the positions for markers
    private float[]  createPositions(float x, float y, float z, float r){

        int step = 2;

        if(r <= 0.01f){
            step = 6;
        }

        int count = 0;
        ArrayList<Float> data=new ArrayList<>();
        float r1,r2;
        float h1,h2;
        float sin,cos;
        for(int i=-90;i<90+step;i+=step){
            r1 = (float)Math.cos(i * Math.PI / 180.0)          * r;
            r2 = (float)Math.cos((i + step) * Math.PI / 180.0) * r;
            h1 = (float)Math.sin(i * Math.PI / 180.0)          * r;
            h2 = (float)Math.sin((i + step) * Math.PI / 180.0) * r;
            // 固定纬度, 360 度旋转遍历一条纬线
            float step2=step*2;
            for (int j = 0; j < 360 + step; j +=step2 ) {
                cos = (float) Math.cos(j * Math.PI / 180.0);
                sin = -(float) Math.sin(j * Math.PI / 180.0);

                data.add(r2 * cos + x);
                data.add(h2       + y);
                data.add(r2 * sin + z);

                data.add(r1 * cos + x);
                data.add(h1       + y);
                data.add(r1 * sin + z);

                count++;
            }
        }
        float[] f=new float[data.size()];
        for(int i=0;i<f.length;i++){
            f[i]=data.get(i);
        }

        return f;
    }



    private float[]  createNormlizes(float step_input){

        float step = step_input;
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


    public void freeMarker(){
        if (vertexBuffer_marker != null){
            vertexBuffer_marker.clear();
            vertexBuffer_marker = null;
        }
        if (colorBuffer_marker != null){
            colorBuffer_marker.clear();
            colorBuffer_marker = null;
        }
    }


    public void freeLine(){
        if (vertexBuffer_line != null){
            vertexBuffer_line.clear();
            vertexBuffer_line = null;
        }
        if (colorBuffer_line != null){
            colorBuffer_line.clear();
            colorBuffer_line = null;
        }
    }

    public void freePoint(){
        if (vertexBuffer_points != null){
            vertexBuffer_points.clear();
            vertexBuffer_points = null;
        }
    }

}
