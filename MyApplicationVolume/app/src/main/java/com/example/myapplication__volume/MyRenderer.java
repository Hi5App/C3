package com.example.myapplication__volume;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.example.myapplication__volume.Myapplication.getContext;
import static javax.microedition.khronos.opengles.GL10.GL_ALPHA_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;


//@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class MyRenderer implements GLSurfaceView.Renderer {
    private MyPattern myPattern;

    private ByteBuffer imageBuffer;

    private int mProgram;

    //    private boolean ispause = false;
    private float angle = 0f;
    private float angleX = 0.0f;
    private float angleY = 0.0f;
    private int mTextureId;

    private int[] texture = new int[1]; //生成纹理id

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] scratch = new float[16];
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] rotationMatrix =new float[16];
    private final float[] rotationXMatrix = new float[16];
    private final float[] rotationYMatrix = new float[16];
    private final float[] translateMatrix = new float[16];//平移矩阵
    private final float[] translateAfterMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] RTMatrix = new float[16];
    private final float[] ZRTMatrix = new float[16];

    private final float[] zoomMatrix = new float[16];//缩放矩阵
    private final float[] zoomAfterMatrix = new float[16];
    private final float[] finalMatrix = new float[16];//缩放矩阵

    private String filepath = ""; //文件路径
    private InputStream is;
    private long length;


    private int screen_w;
    private int screen_h;
    private float cur_scale = 1.0f;


    //初次渲染画面
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        Log.v("onSurfaceCreated:","successfully");


        initTexture(getContext());

        Matrix.setIdentityM(translateMatrix,0);//建立单位矩阵

        Matrix.setIdentityM(zoomMatrix,0);//建立单位矩阵
        Matrix.setIdentityM(zoomAfterMatrix, 0);
        Matrix.setRotateM(rotationMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
//        Matrix.setIdentityM(translateAfterMatrix, 0);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -2, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

    }


    //画面大小发生改变后
    public void onSurfaceChanged(GL10 gl,int width, int height){
        //设置视图窗口
        GLES20.glViewport(0, 0, width, height);

        screen_w = width;
        screen_h = height;
        myPattern = new MyPattern(filepath, is, length, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
//        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

        if(width>height) {
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1,1,1, 100);
        }
        else{
            Matrix.orthoM(projectionMatrix, 0, -1, 1, -1/ratio, 1/ratio,1, 100);
        }

//        Matrix.perspectiveM(projectionMatrix,0,45,1,0.1f,100f);

    }




    //绘制画面
    @Override
    public void onDrawFrame(GL10 gl){

        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

//        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);


        //把颜色缓冲区设置为我们预设的颜色
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//        GLES20.glEnable(GL_BLEND);
//        GLES20.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GLES10.glEnable(GL_ALPHA_TEST);
//        glAlphaFunc(GL_GREATER, 0.05f);
//        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GL_BLEND);
        GLES20.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        setMatrix();

//                Log.v("roatation",Arrays.toString(finalMatrix));


        myPattern.drawVolume(finalMatrix, translateAfterMatrix, screen_w, screen_h, texture[0]);


//        angle += 1.0f;
//        angleX += 1.0f;

        GLES20.glDisable(GL_BLEND);
        GLES20.glDisable(GL_ALPHA_TEST);

    }



    private void setMatrix(){

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Set the Rotation matrix
//        Matrix.setRotateM(rotationMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
//        Matrix.setRotateM(rotationXMatrix, 0, angleX, 1.0f, 0.0f, 0.0f);
//        Matrix.setRotateM(rotationYMatrix, 0, angleY, 0.0f, 1.0f, 0.0f);

//        Log.v("roatation",Arrays.toString(rotationMatrix));

        Matrix.setIdentityM(translateMatrix,0);//建立单位矩阵


        Matrix.translateM(translateMatrix,0,-0.5f,-0.5f,-0.5f);
//        Matrix.multiplyMM(translateMatrix, 0, zoomMatrix, 0, translateMatrix, 0);
        Matrix.setIdentityM(translateAfterMatrix, 0);

        Matrix.translateM(translateAfterMatrix, 0, 0, 0, cur_scale);
        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
//        Matrix.multiplyMM(rotationMatrix, 0, rotationYMatrix, 0, rotationXMatrix, 0);
//        Matrix.multiplyMM(rotationMatrix, 0, zoomMatrix, 0, rotationMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, rotationMatrix, 0, translateMatrix, 0);

        Matrix.multiplyMM(RTMatrix, 0, zoomMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(ZRTMatrix, 0, translateAfterMatrix, 0, RTMatrix, 0);

        Matrix.multiplyMM(finalMatrix, 0, vPMatrix, 0, ZRTMatrix, 0);

//        Matrix.multiplyMM(finalMatrix, 0, zoomMatrix, 0, scratch, 0);

//        Matrix.setIdentityM(translateAfterMatrix, 0);
//        Matrix.translateM(translateAfterMatrix, 0, 0.0f, 0.0f, -0.1f);
//        Matrix.multiplyMM(translateAfterMatrix, 0, zoomAfterMatrix, 0, translateAfterMatrix, 0);
    }




    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void initTexture(Context context){

        GLES20.glGenTextures(  //创建纹理对象
                1, //产生纹理id的数量
                texture, //纹理id的数组
                0  //偏移量
        );

        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.aorta);

        //绑定纹理id，将对象绑定到环境的纹理单元
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);//设置MIN 采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);//设置MAG采样方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式


        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        bitmap.recycle();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);

    }

    private byte[][] getIntensity(){
        rawreader rr = new rawreader();
        String fileName = filepath;
        int[][][] grayscale =  rr.run(length, is);
        byte[][] data_image = new byte[128][128 * 128 * 4];



        for (int x = 0; x < 128; ++x){
            for (int y = 0; y < 128; ++y){
                for (int z = 0; z < 128; z++) {
                    data_image[z][(x * 128 + y) * 4] = intToByteArray(grayscale[x][y][z])[3];
                    data_image[z][(x * 128 + y) * 4 + 1] = intToByteArray(grayscale[x][y][z])[3];
                    data_image[z][(x * 128 + y) * 4 + 2] = intToByteArray(grayscale[x][y][z])[3];
                    if (grayscale[x][y][z] >= 20){
//                        Log.v("Render",String.valueOf(grayscale[x][y][z]));
                        data_image[z][(x * 128 + y) * 4 + 3] = intToByteArray(255)[3];
                    }
                    else
                        data_image[z][(x * 128 + y) * 4 + 3] = intToByteArray(0)[3];
                }
            }
        }

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


    //设置文件路径
    public void SetPath(String message){
        filepath = message;
    }


    public void setInputStream(InputStream Is){
        is = Is;
    }

    public void setLength(long Length){
        length = Length;
    }




    public void rotate(float dx, float dy, float dis){
//        Log.v("wwww", "66666666666666666");
//        Log.v("dddddxxxxx", Float.toString(dx));
//        Log.v("ddddddyyyy", Float.toString(dy));
//        Log.v("ddddddiiiissss", Float.toString(dis));
//
//        float [] currentRotateM = new float[16];
//        Matrix.setIdentityM(currentRotateM, 0);
//        Matrix.setRotateM(currentRotateM, 0, (float)(dis / Math.PI * 180.0f), dy, -dx, 0);
//        float [] templateMatrix = rotationMatrix;
//        Matrix.multiplyMM(rotationMatrix, 0, currentRotateM, 0, templateMatrix, 0);
////        float [] currentRotateM = rotateM((float)(dis / Math.PI * 180.0f), dy, -dx, 0);
////        finalRotateMatrix = multiplyMatrix(currentRotateM, finalRotateMatrix);
//        Log.v("rotation", Arrays.toString(currentRotateM));
////        Matrix.multiplyMM(rotationMatrix, 0, currentRotateM, 0, rotationMatrix, 0);
        angleX = dy * 20;
        angleY = dx * 20;
        Matrix.setRotateM(rotationXMatrix, 0, angleX, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(rotationYMatrix, 0, angleY, 0.0f, 1.0f, 0.0f);
        float [] curRotationMatrix = new float[16];
        Matrix.multiplyMM(curRotationMatrix, 0, rotationXMatrix, 0, rotationYMatrix, 0);
        Matrix.multiplyMM(rotationMatrix, 0, curRotationMatrix, 0, rotationMatrix, 0);
        Log.v("angleX = ", Float.toString(angleX));
        Log.v("angleY = ", Float.toString(angleY));
    }





    public void zoom(float f){
        Matrix.scaleM(zoomMatrix, 0, f, f, f);
        cur_scale *= f;
//        Matrix.scaleM(zoomAfterMatrix, 0, f-1, f-1, f-1);
//        float d = (f - 1) * 0.5f;
//        Matrix.translateM(translateAfterMatrix, 0, 0.0f, 0.0f, d);
//        zoomMatrix = {f, 0.0f, 0.0f, 0.0f,
//                      0.0f, f, 0.0f, 0.0f,
//                      0.0f, 0.0f, f, 0.0f,
//                      0.0f, 0.0f, 0.0f, f};
    }

    //矩阵乘法
    private float [] multiplyMatrix(float [] m1, float [] m2){
        float [] m = new float[9];
        for (int i = 0; i < 9; i++){
            int r = i / 3;
            int c = i % 3;
            m[i] = 0;
            for (int j = 0; j < 3; j++){
                m[i] += m1[r * 3 + j] * m2[j * 3 + c];
            }
        }
        return m;
    }


    private void CreateBuffer(byte[] data){
        //分配内存空间,每个字节型占1字节空间
        imageBuffer = ByteBuffer.allocateDirect(data.length)
                .order(ByteOrder.nativeOrder());
        //传入指定的坐标数据
        imageBuffer.put(data);
        imageBuffer.position(0);
    }

}






//
//    public float [] rotateM(float theta, float x, float y, float z){
//        double len = Math.sqrt(x * x + y * y + z * z);
//        double nx = x / len;
//        double ny = y / len;
//        double nz = z / len;
//        double cos = Math.cos(Math.PI * theta / 180.0f);
//        double sin = Math.sin(Math.PI * theta / 180.0f);
//        float [] rotateMatrix = new float[9];
//        rotateMatrix[0] = (float)(nx * nx * (1 - cos) + cos);  rotateMatrix[1] = (float)(nx * ny * (1 - cos) - nz * sin);  rotateMatrix[2] = (float)(nx * nz * (1 - cos) + ny * sin);
//        rotateMatrix[3] = (float)(nx * ny * (1 - cos) + nz * sin);  rotateMatrix[4] = (float)(ny * ny * (1 - cos) + cos);  rotateMatrix[5] = (float)(ny * nz * (1 - cos) - nx * sin);
//        rotateMatrix[6] = (float)(nx * nz * (1 - cos) - ny * sin);  rotateMatrix[7] = (float)(ny * nz * (1 - cos) + nx * sin);  rotateMatrix[8] = (float)(nz * nz * (1 - cos) + cos);
//        //默认旋转轴始终过原点
//        return rotateMatrix;
//    }






//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//
//    private void initTexture(Context context){
//
//        GLES20.glGenTextures(  //创建纹理对象
//                128, //产生纹理id的数量
//                textures, //纹理id的数组
//                0  //偏移量
//        );
//
////        int textures[] = new int[1]; //生成纹理id
////
////        GLES20.glGenTextures(  //创建纹理对象
////                1, //产生纹理id的数量
////                textures, //纹理id的数组
////                0  //偏移量
////        );
//
//        byte [][] image_data = getIntensity();
//
//        for(int nID=0; nID < 128; nID++ ){
//
//            mTextureId = textures[nID];
//
//            //绑定纹理id，将对象绑定到环境的纹理单元
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureId);
//
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);//设置MIN 采样方式
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);//设置MAG采样方式
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式
//
//
//            CreateBuffer(image_data[nID]);
//
//            GLES20.glTexImage2D(
//                    GLES20.GL_TEXTURE_2D, //纹理类型
//                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
//                    GLES20.GL_RGBA, //图片的格式
//                    128,   //
//                    128,   //
//                    0, //纹理边框尺寸();
//                    GLES20.GL_RGBA,
//                    GLES20.GL_UNSIGNED_BYTE,
//                    imageBuffer
//            );
//
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureId);
//        }
//
//
//
//
//    }


//
//    //初始化纹理
//    private void initTexture(Context context){
//
//        GLES20.glGenTextures(  //创建纹理对象
//                224, //产生纹理id的数量
//                textures, //纹理id的数组
//                0  //偏移量
//        );
//
//
//        byte [][] image_data = getIntensity();
//
//        for(int nID=0; nID < 224; nID++ ){
//
//            mTextureId = textures[nID];
//
//            //绑定纹理id，将对象绑定到环境的纹理单元
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureId);
//
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);//设置MIN 采样方式
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);//设置MAG采样方式
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
//            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
//                    GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式
//
//
//            CreateBuffer(image_data[nID]);
//
//            GLES20.glTexImage2D(
//                    GLES20.GL_TEXTURE_2D, //纹理类型
//                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
//                    GLES20.GL_RGBA, //图片的格式
//                    224,   //
//                    224,   //
//                    0, //纹理边框尺寸();
//                    GLES20.GL_RGBA,
//                    GLES20.GL_UNSIGNED_BYTE,
//                    imageBuffer
//            );
//
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureId);
//        }
//
//    }


//    private byte[][] getIntensity(){
//
//        //最终的纹理信息
//        for (int x = 0; x < 224; x++){
//            for (int y = 0; y < 224; y++){
//                for (int z = 0; z < 224; z++){
//                    texPosition[x][y][z] = 0;
//                }
//            }
//        }
//
//        float [] rotateM = rotateM(angle, rotateX, rotateY, rotateZ);
////        int dx = (int)(112 - (rotateM[0] + rotateM[1] + rotateM[2]) * 64 + 0.5f);
////        int dy = (int)(112 - (rotateM[3] + rotateM[4] + rotateM[5]) * 64 + 0.5f);
////        int dz = (int)(112 - (rotateM[6] + rotateM[7] + rotateM[8]) * 64 + 0.5f);
//        float dx = 112.0f - (finalRotateMatrix[0] + finalRotateMatrix[1] + finalRotateMatrix[2]) * 64.0f;
//        float dy = 112.0f - (finalRotateMatrix[3] + finalRotateMatrix[4] + finalRotateMatrix[5]) * 64.0f;
//        float dz = 112.0f - (finalRotateMatrix[6] + finalRotateMatrix[7] + finalRotateMatrix[8]) * 64.0f;
//        float [] d = {dx, dy, dz};  //旋转后与中心的偏移量
//        rawreader rr = new rawreader();
//        String fileName = filepath;
//        int[][][] grayscale =  rr.run(fileName);
//        byte[][] data_image = new byte[224][224 * 224 * 4];
//
////        byte[] final_image = new byte[128 * 128 * 4];
//        for (int x = 0; x < 128; x++){
//            for (int y = 0; y < 128; y++){
//                for (int z = 0; z < 128; z++){
//                    int [] new_position = {0, 0, 0};
//                    for (int i = 0; i < 3; i++){
////                        float new_p = finalMatrix[i * 4] * x + finalMatrix[i * 4 + 1] * y
////                                + finalMatrix[i * 4 + 2] * z + finalMatrix[i * 4 + 3];
//                        float new_p = finalRotateMatrix[i * 3] * x + finalRotateMatrix[i * 3 + 1] * y
//                                + finalRotateMatrix[i * 3 + 2] * z;
////                        float new_p = rotationMatrix[i * 4] * x + rotationMatrix[i * 4 + 1] * y
////                                + rotationMatrix[i * 4 + 2] * z + rotationMatrix[i * 4 + 3];
////                        Log.v("new_p:", Float.toString(new_p));
//                        new_position[i] = (int)(new_p + 0.5f + d[i]);
////                        Log.v("new_position", Integer.toString(new_position[i]));
//                    }
//                    texPosition[new_position[0]][new_position[1]][new_position[2]] = grayscale[x][y][z];
//                }
//            }
//        }
//
//
//        for (int x = 0; x < 224; ++x){
//            for (int y = 0; y < 224; ++y){
//                for (int z = 0; z < 224; z++) {
//                    data_image[z][(x * 224 + y) * 4] = intToByteArray(texPosition[x][y][z])[3];
//                    data_image[z][(x * 224 + y) * 4 + 1] = intToByteArray(texPosition[x][y][z])[3];
//                    data_image[z][(x * 224 + y) * 4 + 2] = intToByteArray(texPosition[x][y][z])[3];
//                    if (texPosition[x][y][z] >= 20)
//                        data_image[z][(x * 224 + y) * 4 + 3] = intToByteArray(255)[3];
//                    else
//                        data_image[z][(x * 224 + y) * 4 + 3] = intToByteArray(0)[3];
//
//                }
//            }
//        }
//
////        for (int x = 0; x < 128; ++x){
////            for (int y = 0; y < 128; ++y){
////                for (int z = 0; z < 128; z++) {
////                    data_image[z][(x * 128 + y) * 4] = intToByteArray(grayscale[x][y][z])[3];
////                    data_image[z][(x * 128 + y) * 4 + 1] = intToByteArray(grayscale[x][y][z])[3];
////                    data_image[z][(x * 128 + y) * 4 + 2] = intToByteArray(grayscale[x][y][z])[3];
////                    if (grayscale[x][y][z] >= 20)
////                        data_image[z][(x * 128 + y) * 4 + 3] = intToByteArray(255)[3];
////                    else
////                        data_image[z][(x * 128 + y) * 4 + 3] = intToByteArray(0)[3];
////
////                }
////            }
////        }
//
//        return data_image;
//    }
