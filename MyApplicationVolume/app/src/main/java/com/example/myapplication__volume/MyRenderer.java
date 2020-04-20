package com.example.myapplication__volume;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLES10;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import com.example.basic.ByteTranslate;
import com.example.basic.Image4DSimple;
import com.example.basic.ImageMarker;
import com.example.basic.MyAnimation;
import com.example.basic.NeuronSWC;
import com.example.basic.NeuronTree;
import com.tracingfunc.gd.V_NeuronSWC;
import com.tracingfunc.gd.V_NeuronSWC_list;
import com.tracingfunc.gd.V_NeuronSWC_unit;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.example.myapplication__volume.MainActivity.getContext;
import static javax.microedition.khronos.opengles.GL10.GL_ALPHA_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_SRC_ALPHA;


//@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class MyRenderer implements GLSurfaceView.Renderer {
    public static final String OUTOFMEM_MESSAGE = "OutOfMemory";
    public static final String FILE_SUPPORT_ERROR = "FileSupportError";

    private MyPattern myPattern;
    private MyAxis myAxis;
    private MyDraw myDraw;
    public  MyAnimation myAnimation;

    private Image4DSimple img;
    private ByteBuffer imageBuffer;

    private int mProgram;

    //    private boolean ispause = false;
    private float angle = 0f;
    private float angleX = 0.0f;
    private float angleY = 0.0f;
    private int mTextureId;

    private int vol_w;
    private int vol_h;
    private int vol_d;
    private int[] sz = new int[3];
    private float[] mz = new float[3];


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
    private float[] ArotationMatrix = new float[16];


    private final float[] zoomMatrix = new float[16];//缩放矩阵
    private final float[] zoomAfterMatrix = new float[16];
    private final float[] finalMatrix = new float[16];//缩放矩阵
    private float[] linePoints = {

    };

    private ArrayList<Float> splitPoints = new ArrayList<Float>();
    private int splitType;

    private ArrayList<ArrayList<Float>> lineDrawed = new ArrayList<ArrayList<Float>>();

    private ArrayList<Float> markerDrawed = new ArrayList<Float>();

    private ArrayList<Float> eswcDrawed = new ArrayList<Float>();

    private ArrayList<Float> apoDrawed = new ArrayList<Float>();

    private ArrayList<Float> swcDrawed = new ArrayList<Float>();

    private ArrayList<ImageMarker> MarkerList = new ArrayList<ImageMarker>();

    private V_NeuronSWC_list curSwcList = new V_NeuronSWC_list();

    private int lastLineType = 2;
    private int lastMarkerType = 3;


    private String filepath = ""; //文件路径
    private InputStream is;
    private long length;

    private boolean ifPainting = false;

    private int screen_w;
    private int screen_h;
    private float cur_scale = 1.0f;

    private byte[] grayscale;
    private int data_length;
    private boolean isBig;

    private FileType fileType;
    private ByteBuffer mCaptureBuffer;
    private Bitmap mBitmap;
    private boolean isTakePic = false;
    private String mCapturePath;


    //初次渲染画面
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES30.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        Log.v("onSurfaceCreated:","successfully");

        SetFileType();

        if (fileType == FileType.V3draw)
            setImage();

        else if (fileType == FileType.SWC)
            setSWC();

        Matrix.setIdentityM(translateMatrix,0);//建立单位矩阵

        Matrix.setIdentityM(zoomMatrix,0);//建立单位矩阵
        Matrix.setIdentityM(zoomAfterMatrix, 0);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setRotateM(rotationMatrix, 0, 20, -1.0f, -1.0f, 0.0f);
//        Matrix.setIdentityM(translateAfterMatrix, 0);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -2, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

    }


    //画面大小发生改变后
    public void onSurfaceChanged(GL10 gl,int width, int height){
        //设置视图窗口
        GLES30.glViewport(0, 0, width, height);

        screen_w = width;
        screen_h = height;

        if (fileType == FileType.V3draw)
            myPattern = new MyPattern(filepath, is, length, width, height, img, mz);
        myAxis = new MyAxis(mz);
        myDraw = new MyDraw();
        myAnimation = new MyAnimation();

        mCaptureBuffer = ByteBuffer.allocate(screen_h*screen_w*4);
        mBitmap = Bitmap.createBitmap(screen_w,screen_h, Bitmap.Config.ARGB_8888);


        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
//        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

//        if(width>height) {
//            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1,1,1, 100);
//        }
//        else{
//            Matrix.orthoM(projectionMatrix, 0, -1, 1, -1/ratio, 1/ratio,1, 100);
//        }

        if(width>height) {
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1,1,2f, 100);
        }
        else{
            Matrix.frustumM(projectionMatrix, 0, -1, 1, -1/ratio, 1/ratio,2f, 100);
        }

//        Matrix.perspectiveM(projectionMatrix,0,45,1,0.1f,100f);

    }




    //绘制画面
    @Override
    public void onDrawFrame(GL10 gl){

        GLES30.glClearColor(0.5f, 0.4f, 0.3f, 1.0f);


        //把颜色缓冲区设置为我们预设的颜色
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
//        GLES30.glEnable(GL_BLEND);
//        GLES30.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GLES10.glEnable(GL_ALPHA_TEST);
//        glAlphaFunc(GL_GREATER, 0.05f);
//        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        GLES30.glEnable(GL_BLEND);
        GLES30.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

//        Log.v("onDrawFrame", Arrays.toString(rotationMatrix));

        setMatrix();




        if (myAnimation.status){
            AnimationRotation();
//            Log.v("onDrawFrame", "myAnimation");
        }

//        AnimationRotation();


//        Log.v("rotation",Arrays.toString(finalMatrix));




        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//
//        float [] result_1 = new float[4];
//        float [] result_2 = new float[4];
//
//
//
//        float [] invert_result_1 = new float[4];
//        float [] invert_result_2 = new float[4];
//
//        float [] invertfinalMatrix = new float[16];
//
////        float [] input_1 = {60/128.0f, 63/128.0f, 64/128.0f, 1f};
//        float [] input_1 = {1f, 1f, 1f, 1f};
//        float [] input_2 = {1f, 1f, 0f, 1f};
//
//
//        Matrix.multiplyMV(result_1, 0, finalMatrix, 0, input_1,0);
//
//        float w1 = result_1[3];
//
//        Log.v("result_1",Arrays.toString(result_1));
//
//        Log.v("result_1",Arrays.toString(devide(result_1, w1)));
//
//        Matrix.multiplyMV(result_2, 0, finalMatrix, 0, input_2,0);
//
//        float w2 = result_2[3];
//
//        Log.v("result_2",Arrays.toString(result_2));
//
//        Log.v("result_2",Arrays.toString(devide(result_2, w2)));
//
//
//        result_1 = new float[]{-0.4f, 0.209f, -1, 1};
//        result_2 = new float[]{-0.4f, 0.209f, 1, 1};
//
//        Matrix.invertM(invertfinalMatrix, 0, finalMatrix, 0);
//
//        Log.v("invert_rotation",Arrays.toString(invertfinalMatrix));
//
//        Matrix.multiplyMV(invert_result_1, 0, invertfinalMatrix, 0, result_1,0);
//
//        devideByw(invert_result_1);
//
//        Log.v("invert_result_1",Arrays.toString(invert_result_1));
//
//        Matrix.multiplyMV(invert_result_2, 0, invertfinalMatrix, 0, result_2,0);
//
//        devideByw(invert_result_2);
//
//        Log.v("invert_result_2",Arrays.toString(invert_result_2));
//
//
////        solveMarkerCenter(-0.4f, -0.209f);
//





        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



//        Log.v("onDrawFrame", "draw_axis");

        if (fileType == FileType.V3draw)
            myPattern.drawVolume_3d(finalMatrix, translateAfterMatrix, screen_w, screen_h, texture[0]);

//        Log.v("onDrawFrame: ", Integer.toString(markerDrawed.size()));

//        //现画的marker
//        if(markerDrawed.size() > 0){
//            for (int i = 0; i < markerDrawed.size(); i = i + 3){
//                myDraw.drawMarker(finalMatrix, modelMatrix, markerDrawed.get(i), markerDrawed.get(i+1), markerDrawed.get(i+2));
////                Log.v("onDrawFrame: ", "(" + markerDrawed.get(i) + ", " + markerDrawed.get(i+1) + ", " + markerDrawed.get(i+2) + ")");
//
//            }
//        }
        if(curSwcList.nsegs()>0){
//            System.out.println("------------draw curswclist------------------------");
            ArrayList<Float> lines = new ArrayList<Float>();
            for(int i=0; i<curSwcList.seg.size(); i++){
                System.out.println("i: "+i);
                V_NeuronSWC seg = curSwcList.seg.get(i);
//                ArrayList<Float> currentLine = swc.get(i);
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                lines.clear();
                for(int j=0; j<seg.row.size(); j++){
                    if(seg.row.get(j).parent != -1 && seg.getIndexofParent(j) != -1){
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(j));
                        swcUnitMap.put(j,parent);
                    }
                }
//                System.out.println("---------------end map-----------------------");
                for(int j=0; j<seg.row.size(); j++){
//                    System.out.println("in row: "+j+"-------------------");
                    V_NeuronSWC_unit child = seg.row.get(j);
                    int parentid = (int) child.parent;
                    if (parentid == -1 || seg.getIndexofParent(j) == -1 ){
//                        System.out.println("parent -1");
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(j);
                    lines.add((float) ((sz[0] - parent.x)/sz[0]*mz[0]));
                    lines.add((float) ((sz[1] - parent.y)/sz[1]*mz[1]));
                    lines.add((float) ((parent.z)/sz[2]*mz[2]));
                    lines.add((float) ((sz[0] - child.x)/sz[0]*mz[0]));
                    lines.add((float) ((sz[1] - child.y)/sz[1]*mz[1]));
                    lines.add((float) ((child.z)/sz[2]*mz[2]));
//                    System.out.println("in draw line--------------"+j);
//                    System.out.println("type: "+parent.type);
                    myDraw.drawLine(finalMatrix, lines, (int) parent.type);
                    lines.clear();
                }
            }

        }

        //画的分界点
        if (splitPoints.size() > 0){
            myDraw.drawSplitPoints(finalMatrix, splitPoints, splitType);
        }

        //现画的marker
        if(MarkerList.size() > 0){
            for (int i = 0; i < MarkerList.size(); i++){
                System.out.println("start draw marker---------------------");
                ImageMarker imageMarker = MarkerList.get(i);
                float[] markerModel = VolumetoModel(new float[]{imageMarker.x, imageMarker.y, imageMarker.z});
                myDraw.drawMarker(finalMatrix, modelMatrix, markerModel[0], markerModel[1], markerModel[2], imageMarker.type);
//                Log.v("onDrawFrame: ", "(" + markerDrawed.get(i) + ", " + markerDrawed.get(i+1) + ", " + markerDrawed.get(i+2) + ")");

            }
        }

        //现画的curve
//        if (lineDrawed.size() > 0){
//            for (int i = 0; i < lineDrawed.size(); i++){
//                myDraw.drawLine(finalMatrix, lineDrawed.get(i));
////                Log.v("onDrawFrameLine",
////                        "(" + lineDrawed.get(i).get(0) + "," + lineDrawed.get(i).get(1) + "," + lineDrawed.get(i).get(2) + ")"
////                            + "(" + lineDrawed.get(i).get(3) + "," + lineDrawed.get(i).get(4) + "," + lineDrawed.get(i).get(5) + ")");
////                Log.v("onDrawFrame", Integer.toString(lineDrawed.get(i).size()));
//            }
//        }



        //画curve留下的痕迹
        if (ifPainting) {
            if(linePoints.length > 0){
//                Log.v("drawline", "trueeeeeeeeeeeee");
//                String s = "";
//                for (int i = 0; i < linePoints.length; i++){
//                    s = s + " " + Float.toString(linePoints[i]);
//                }
//                Log.v("linePoints", s);
                int num = linePoints.length / 3;
                myPattern.draw_points(linePoints, num);
            }
        }

        //导入的eswc
//        if (eswcDrawed.size() > 0){
//            myDraw.drawEswc(finalMatrix, eswcDrawed);
//        }
//
//        //导入的swc
//        if (swcDrawed.size() > 0){
//            myDraw.drawEswc(finalMatrix, swcDrawed);
//        }

        //导入的apo
        if (apoDrawed.size() > 0){

            Log.v("MyRender", "Load data successfully!");
            for (int i = 0; i < apoDrawed.size(); i = i + 4){
                myDraw.drawMarker(finalMatrix, modelMatrix, apoDrawed.get(i), apoDrawed.get(i+1), apoDrawed.get(i+2),apoDrawed.get(i+3).intValue());
//                Log.v("onDrawFrame: ", "(" + markerDrawed.get(i) + ", " + markerDrawed.get(i+1) + ", " + markerDrawed.get(i+2) + ")");

            }
        }


        //
        myAxis.draw(finalMatrix);

        if(isTakePic){
            mCaptureBuffer.rewind();
            GLES30.glReadPixels(0,0,screen_w,screen_h,GLES30.GL_RGBA,GLES30.GL_UNSIGNED_BYTE,mCaptureBuffer);
            isTakePic = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mCaptureBuffer.rewind();
                    mBitmap.copyPixelsFromBuffer(mCaptureBuffer);
                    for(int i=0; i<screen_w; i++){
                        for(int j=0; j<screen_h/2; j++){
                            int jj = screen_h-1-j;
                            int pixelTmp = mBitmap.getPixel(i,jj);
                            mBitmap.setPixel(i,jj,mBitmap.getPixel(i,j));
                            mBitmap.setPixel(i,j,pixelTmp);
                        }
                    }

                    mCapturePath = Myapplication.getContext().getExternalFilesDir(null).toString() + "/" + "Image_" + System.currentTimeMillis() +".jpg";
                    System.out.println(mCapturePath+"------------------------------");
                    try {
                        FileOutputStream fos = new FileOutputStream(mCapturePath);
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                        fos.flush();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }


//        angle += 1.0f;
//        angleX += 1.0f;

        GLES30.glDisable(GL_BLEND);
        GLES30.glDisable(GL_ALPHA_TEST);
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

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


        Matrix.translateM(translateMatrix,0,-0.5f * mz[0],-0.5f * mz[1],-0.5f * mz[2]);
//        Matrix.multiplyMM(translateMatrix, 0, zoomMatrix, 0, translateMatrix, 0);
        Matrix.setIdentityM(translateAfterMatrix, 0);

        Matrix.translateM(translateAfterMatrix, 0, 0, 0, cur_scale);
//        Matrix.translateM(translateAfterMatrix, 0, 0, 0, -cur_scale);

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
//        Matrix.multiplyMM(rotationMatrix, 0, rotationYMatrix, 0, rotationXMatrix, 0);
//        Matrix.multiplyMM(rotationMatrix, 0, zoomMatrix, 0, rotationMatrix, 0);
        Matrix.multiplyMM(modelMatrix, 0, rotationMatrix, 0, translateMatrix, 0);

        Matrix.multiplyMM(RTMatrix, 0, zoomMatrix, 0, modelMatrix, 0);

        Matrix.multiplyMM(ZRTMatrix, 0, translateAfterMatrix, 0, RTMatrix, 0);

        Matrix.multiplyMM(finalMatrix, 0, vPMatrix, 0, ZRTMatrix, 0);      //ZRTMatrix代表modelMatrix

//        Matrix.multiplyMM(finalMatrix, 0, zoomMatrix, 0, scratch, 0);

//        Matrix.setIdentityM(translateAfterMatrix, 0);
//        Matrix.translateM(translateAfterMatrix, 0, 0.0f, 0.0f, -0.1f);
//        Matrix.multiplyMM(translateAfterMatrix, 0, zoomAfterMatrix, 0, translateAfterMatrix, 0);
    }




    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

//    private void initTexture(Context context){
//
//        GLES30.glGenTextures(  //创建纹理对象
//                1, //产生纹理id的数量
//                texture, //纹理id的数组
//                0  //偏移量
//        );
//
//        Bitmap bitmap = BitmapFactory.decodeResource(
//                context.getResources(), R.drawable.aorta);
//
//        //绑定纹理id，将对象绑定到环境的纹理单元
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,texture[0]);
//
//        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);//设置MIN 采样方式
//        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);//设置MAG采样方式
//        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
//        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式
//
//
//        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
//
//        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
//
//        bitmap.recycle();
//
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
//
//    }
//
//    private byte[][] getIntensity(){
//        Rawreader rr = new Rawreader();
//        String fileName = filepath;
//        int[][][] grayscale =  rr.run(length, is);
//        byte[][] data_image = new byte[128][128 * 128 * 4];
//
//
//
//        for (int x = 0; x < 128; ++x){
//            for (int y = 0; y < 128; ++y){
//                for (int z = 0; z < 128; z++) {
//                    data_image[z][(x * 128 + y) * 4] = intToByteArray(grayscale[x][y][z])[3];
//                    data_image[z][(x * 128 + y) * 4 + 1] = intToByteArray(grayscale[x][y][z])[3];
//                    data_image[z][(x * 128 + y) * 4 + 2] = intToByteArray(grayscale[x][y][z])[3];
//                    if (grayscale[x][y][z] >= 20){
////                        Log.v("Render",String.valueOf(grayscale[x][y][z]));
//                        data_image[z][(x * 128 + y) * 4 + 3] = intToByteArray(255)[3];
//                    }
//                    else
//                        data_image[z][(x * 128 + y) * 4 + 3] = intToByteArray(0)[3];
//                }
//            }
//        }
//
//        return data_image;
//    }


    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~




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


    private void SetFileType(){

        String filetype = filepath.substring(filepath.lastIndexOf(".")).toUpperCase();

        switch (filetype){
            case ".V3DRAW":
                fileType = FileType.V3draw;
                break;

            case ".SWC":
                fileType = FileType.SWC;
                break;

            default:
                System.out.println();
                Context context = getContext();
                Intent intent = new Intent(context, FileActivity.class);
                String errormsg = "Don't support this file!";
                intent.putExtra(MyRenderer.FILE_SUPPORT_ERROR, errormsg);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
        }

    }

    public void setInputStream(InputStream Is){

        is = Is;
    }

    public void setLength(long Length){

        length = Length;
    }


    /**
     * set rotationMatrix when the animation run
     */
    public void AnimationRotation(){

        ArotationMatrix = myAnimation.Rotation(rotationMatrix);
        Matrix.multiplyMM(rotationMatrix,0, rotationMatrix, 0, ArotationMatrix,0);

    }

//    public void setAnimation(boolean status, float speed, String type){
//        myAnimation.status = status;
//        myAnimation.speed = speed/60f;
//        myAnimation.setRotationType(type);
//        myAnimation.ResetAnimation();
//        if (status == false){
//            myAnimation.setRotationType("X");
//        }
//    }



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
        angleX = dy * 30;
        angleY = dx * 30;
        Matrix.setRotateM(rotationXMatrix, 0, angleX, 1.0f, 0.0f, 0.0f);
        Matrix.setRotateM(rotationYMatrix, 0, angleY, 0.0f, 1.0f, 0.0f);
        float [] curRotationMatrix = new float[16];
        Matrix.multiplyMM(curRotationMatrix, 0, rotationXMatrix, 0, rotationYMatrix, 0);
        Matrix.multiplyMM(rotationMatrix, 0, curRotationMatrix, 0, rotationMatrix, 0);

//        Log.v("angleX = ", Float.toString(angleX));
//        Log.v("angleY = ", Float.toString(angleY));
    }





    public void zoom(float f){

        if (cur_scale > 0.2 && cur_scale < 30) {
            Matrix.scaleM(zoomMatrix, 0, f, f, f);
            cur_scale *= f;
        }else if(cur_scale < 0.2 && f > 1){
            Matrix.scaleM(zoomMatrix, 0, f, f, f);
            cur_scale *= f;
        }else if (cur_scale > 30 && f < 1){
            Matrix.scaleM(zoomMatrix, 0, f, f, f);
            cur_scale *= f;
        }

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

    public void setLineDrawed(ArrayList<Float> lineDrawed){
//        Float [] linePoints = lineDrawed.toArray(new Float[lineDrawed.size()]);

        linePoints = new float[lineDrawed.size()];
        for (int i =0; i < lineDrawed.size(); i++){
            linePoints[i] = lineDrawed.get(i);
        }
    }

    public void setIfPainting(boolean b){

        ifPainting = b;
    }




    // add the marker drawed into markerlist
    public void setMarkerDrawed(float x, float y){

        if(solveMarkerCenter(x, y) != null) {
            float[] new_marker = solveMarkerCenter(x, y);

            ImageMarker imageMarker_drawed = new ImageMarker(new_marker[0],
                                                             new_marker[1],
                                                             new_marker[2]);
            imageMarker_drawed.type = lastMarkerType;
            System.out.println("set type to 3");

            MarkerList.add(imageMarker_drawed);
        }
    }

    // delete the marker drawed from the markerlist
    public void deleteMarkerDrawed(float x, float y){
        for (int i = 0; i < MarkerList.size(); i++){
            ImageMarker tobeDeleted = MarkerList.get(i);
            float[] markerModel = VolumetoModel(new float[]{tobeDeleted.x,tobeDeleted.y,tobeDeleted.z});
            float [] position = new float[4];
            position[0] = markerModel[0];
            position[1] = markerModel[1];
            position[2] = markerModel[2];
            position[3] = 1.0f;

            float [] positionVolumne = new float[4];
            Matrix.multiplyMV(positionVolumne, 0, finalMatrix, 0, position, 0);
            devideByw(positionVolumne);

            float dx = Math.abs(positionVolumne[0] - x);
            float dy = Math.abs(positionVolumne[1] - y);

            if (dx < 0.08 && dy < 0.08){
                MarkerList.remove(i);
                break;
            }
        }
    }



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //To set the marker

    private void setImage(){
//        Rawreader rr = new Rawreader();
//        String fileName = filepath;

//        Uri uri = Uri.parse(fileName);
        img = Image4DSimple.loadImage(filepath);
        if (img == null){
            return;
        }
//        try {
//            ParcelFileDescriptor parcelFileDescriptor =
//                    MainActivity.getContext().getContentResolver().openFileDescriptor(uri, "r");
//
//            is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//
//            length = (int)parcelFileDescriptor.getStatSize();
//
//            Log.v("MyPattern","Successfully load intensity");
//
//        }catch (Exception e){
//            Log.v("MyPattern","Some problems in the MyPattern when load intensity");
//        }


        grayscale =  img.getData();

        data_length = img.getDatatype().ordinal();
        isBig = img.getIsBig();

//        vol_w = rr.get_w();
//        vol_h = rr.get_h();
//        vol_d = rr.get_d();

//        sz[0] = vol_w;
//        sz[1] = vol_h;
//        sz[2] = vol_d;

        sz[0] = (int)img.getSz0();
        sz[1] = (int)img.getSz1();
        sz[2] = (int)img.getSz2();

        Integer[] num = {sz[0], sz[1], sz[2]};
        float max_dim = (float) Collections.max(Arrays.asList(num));
        Log.v("MyRenderer", Float.toString(max_dim));

        mz[0] = (float) sz[0]/max_dim;
        mz[1] = (float) sz[1]/max_dim;
        mz[2] = (float) sz[2]/max_dim;

        Log.v("MyRenderer", Arrays.toString(mz));

//        for (int i = 0; i < vol_w; i++){
//            for (int j = 0; j < vol_h; j++){
//                for (int k = 0; k < vol_d; k++){
//                    if(grayscale[i][j][k]>120) {
//                        Log.v("greater than 150 at:", "[" + i + "]" + "[" + j + "]" + "[" + k + "]: " + grayscale[i][j][k]);
//                        count ++;
//                        float[] model_points = VolumetoModel(new float[] {i, j, k});
//
//                        Log.v("onDrawFrame: ", "(" + model_points[0] + ", " + model_points[1] + ", " + model_points[2] + ")");
//
//                        markerDrawed.add(model_points[0]);
//                        markerDrawed.add(model_points[1]);
//                        markerDrawed.add(model_points[2]);
//                    }
//                }
//            }
//        }

//        Log.v("the num of > 150", Integer.toString(count));

    }


    private void setSWC(){

        Uri uri = Uri.parse(filepath);
        NeuronTree nt = NeuronTree.readSWC_file(uri);
        V_NeuronSWC seg_swc = nt.convertV_NeuronSWCFormat();
        curSwcList.append(seg_swc);


        sz[0] = 0;
        sz[1] = 0;
        sz[2] = 0;

        for(int i=0; i<curSwcList.seg.size(); i++){
            V_NeuronSWC seg = curSwcList.seg.get(i);

            for(int j=0; j<seg.row.size(); j++){

                V_NeuronSWC_unit node = seg.row.get(j);

                if (node.x > sz[0])
                    sz[0] = (int) node.x;
                if (node.y > sz[1])
                    sz[1] = (int) node.y;
                if (node.z > sz[2])
                    sz[2] = (int) node.z;

            }
        }

        sz[0] = (int) (1.2f * sz[0]);
        sz[1] = (int) (1.2f * sz[1]);
        sz[2] = (int) (1.2f * sz[2]);

        Integer[] num = {sz[0], sz[1], sz[2]};
        float max_dim = (float) Collections.max(Arrays.asList(num));
        Log.v("MyRenderer", Float.toString(max_dim));

        mz[0] = (float) sz[0]/max_dim;
        mz[1] = (float) sz[1]/max_dim;
        mz[2] = (float) sz[2]/max_dim;

    }



    //寻找marker点的位置~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public float[] solveMarkerCenter(float x, float y){

//        float [] result = new float[3];
        float [] loc1 = new float[3];
        float [] loc2 = new float[3];

//        get_NearFar_Marker(x, y, loc1, loc2);
        get_NearFar_Marker_2(x, y, loc1, loc2);

        Log.v("loc1",Arrays.toString(loc1));
        Log.v("loc2",Arrays.toString(loc2));

        float steps = 512;
        float [] step = devide(minus(loc1, loc2), steps);
        Log.v("step",Arrays.toString(step));


        if(make_Point_near(loc1, loc2)){
//            Log.v("loc1",Arrays.toString(loc1));
//            Log.v("loc2",Arrays.toString(loc2));

            float [] Marker = getCenterOfLineProfile(loc1, loc2);
//            float [] Marker = {60.1f, 63.2f, 63.6f};
            if (Marker == null){
                return null;
            }

            Log.v("Marker",Arrays.toString(Marker));

//            float intensity = Sample3d(Marker[0], Marker[1], Marker[2]);
//            Log.v("intensity",Float.toString(intensity));

            return Marker;
        }else {
            Log.v("solveMarkerCenter","please make sure the point inside the bounding box");
//            Looper.prepare();
            Toast.makeText(getContext(), "please make sure the point inside the bounding box", Toast.LENGTH_SHORT).show();
            return null;
        }


    }




    //类似于光线投射，找直线上强度最大的一点
    // in Image space (model space)
    private float[] getCenterOfLineProfile(float[] loc1, float[] loc2){

        float[] result = new float[3];
        float[] loc1_index = new float[3];
        float[] loc2_index = new float[3];
        boolean isInBoundingBox = false;

//        for(int i=0; i<3; i++){
//            loc1_index[i] = loc1[i] * sz[i];
//            loc2_index[i] = loc2[i] * sz[i];
//        }

        loc1_index = ModeltoVolume(loc1);
        loc2_index = ModeltoVolume(loc2);

//        for(int i=0; i<2; i++){
//            loc1_index[i] = (1.0f - loc1[i]) * sz[2 - i];
//            loc2_index[i] = (1.0f - loc2[i]) * sz[2 - i];
//        }
//
//
//        loc1_index[2] = loc1[2] * sz[0];
//        loc2_index[2] = loc2[2] * sz[0];


        result = devide(plus(loc1_index, loc2_index), 2);

        float max_value = 0f;

        //单位向量
        float[] d = minus(loc1_index, loc2_index);
        normalize(d);

        Log.v("getCenterOfLineProfile:", "step: " + Arrays.toString(d));

        //判断是不是一个像素
        float length = distance(loc1_index, loc2_index);
        if(length < 0.5)
            return  result;

        int nstep = (int)(length+0.5);
        float one_step = length/nstep;

        Log.v("getCenterOfLineProfile", Float.toString(one_step));

        float[][] dim = new float[3][2];
        for(int i=0; i<3; i++){
            dim[i][0] = 0;
            dim[i][1] = sz[i] - 1;
        }

        float[] poc;
        for(int i = 0; i <= nstep; i++){

            float value;

            poc = minus(loc1_index, multiply(d, one_step * i));
//            poc = multiply(d, one_step);

//            Log.v("getCenterOfLineProfile:", "update the max");

//            Log.v("getCenterOfLineProfile", "(" + poc[0] + "," + poc[1] + "," + poc[2] + ")");


            if(IsInBoundingBox(poc, dim)){

                value = Sample3d(poc[0], poc[1], poc[2]);

                if(value > max_value){
//                    Log.v("getCenterOfLineProfile", "(" + poc[0] + "," + poc[1] + "," + poc[2] + "): " +value);
//                    Log.v("getCenterOfLineProfile:", "update the max");
                    max_value = value;
                    for (int j = 0; j < 3; j++){
                        result[j] = poc[j];
                    }
                    isInBoundingBox = true;
                }
            }
        }

        if(!isInBoundingBox){
            Toast.makeText(getContext(), "please make sure the point inside the bounding box", Toast.LENGTH_SHORT).show();
            return null;
        }

        return result;
    }




    //
    private void get_NearFar_Marker(float x, float y, float [] res1, float [] res2){

        //mvp矩阵的逆矩阵
        float [] invertfinalMatrix = new float[16];

        Matrix.invertM(invertfinalMatrix, 0, finalMatrix, 0);
        Log.v("invert_rotation",Arrays.toString(invertfinalMatrix));

        float [] near = new float[4];
        float [] far = new float[4];

        Matrix.multiplyMV(near, 0, invertfinalMatrix, 0, new float [] {x, y, -1, 1}, 0);
        Matrix.multiplyMV(far, 0, invertfinalMatrix, 0, new float [] {x, y, 0, 1}, 0);

        Log.v("near",Arrays.toString(near));
        Log.v("far",Arrays.toString(far));

        for(int i=0; i<3; i++){
            res1[i] = near[i];
            res2[i] = far[i];
        }

    }



    //用于透视投影中获取近平面和远平面的焦点
    private void get_NearFar_Marker_2(float x, float y, float [] res1, float [] res2){

        //mvp矩阵的逆矩阵
        float [] invertfinalMatrix = new float[16];

        Matrix.invertM(invertfinalMatrix, 0, finalMatrix, 0);
//        Log.v("invert_rotation",Arrays.toString(invertfinalMatrix));

        float [] near = new float[4];
        float [] far = new float[4];

        Matrix.multiplyMV(near, 0, invertfinalMatrix, 0, new float [] {x, y, -1, 1}, 0);
        Matrix.multiplyMV(far, 0, invertfinalMatrix, 0, new float [] {x, y, 1, 1}, 0);

        devideByw(near);
        devideByw(far);

//        Log.v("near",Arrays.toString(near));
//        Log.v("far",Arrays.toString(far));

        for(int i=0; i<3; i++){
            res1[i] = near[i];
            res2[i] = far[i];
        }

    }





    //找到靠近boundingbox的两处端点
    private boolean make_Point_near(float[] loc1, float[] loc2){

        float steps = 512;
        float [] near = loc1;
        float [] far = loc2;
        float [] step = devide(minus(near, far), steps);

        float[][] dim = new float[3][2];
        for(int i=0; i<3; i++){
            dim[i][0]= 0;
            dim[i][1]= mz[i];
        }

        int num = 0;
        while(num<steps && !IsInBoundingBox(near, dim)){
            near = minus(near, step);
            num++;
        }
        if(num == steps)
            return false;


        while(!IsInBoundingBox(far, dim)){
            far = plus(far, step);
        }

        near = plus(near, step);
        far = minus(far, step);

        for(int i=0; i<3; i++){
            loc1[i] = near[i];
            loc2[i] = far[i];
        }

//        Log.v("make_point_near","here we are");
        return true;

    }


    //判断是否在图像内部了
    private boolean IsInBoundingBox(float[] x, float[][] dim){
        int length = x.length;

        for(int i=0; i<length; i++){
//            Log.v("IsInBoundingBox", Float.toString(x[i]));
            if(x[i]>=dim[i][1] || x[i]<=dim[i][0])
                return false;
        }
//        Log.v("IsInBoundingBox", Arrays.toString(x));
//        Log.v("IsInBoundingBox", Arrays.toString(dim));
        return true;
    }



    float Sample3d(float x, float y, float z){
        int x0, x1, y0, y1, z0, z1;
        x0 = (int) Math.floor(x);         x1 = (int) Math.ceil(x);
        y0 = (int) Math.floor(y);         y1 = (int) Math.ceil(y);
        z0 = (int) Math.floor(z);         z1 = (int) Math.ceil(z);

        float xf, yf, zf;
        xf = x-x0;
        yf = y-y0;
        zf = z-z0;

        float [][][] is = new float[2][2][2];
        is[0][0][0] = grayData(x0, y0, z0);
        is[0][0][1] = grayData(x0, y0, z1);
        is[0][1][0] = grayData(x0, y1, z0);
        is[0][1][1] = grayData(x0, y1, z1);
        is[1][0][0] = grayData(x1, y0, z0);
        is[1][0][1] = grayData(x1, y0, z1);
        is[1][1][0] = grayData(x1, y1, z0);
        is[1][1][1] = grayData(x1, y1, z1);

        float [][][] sf = new float[2][2][2];
        sf[0][0][0] = (1-xf)*(1-yf)*(1-zf);
        sf[0][0][1] = (1-xf)*(1-yf)*(  zf);
        sf[0][1][0] = (1-xf)*(  yf)*(1-zf);
        sf[0][1][1] = (1-xf)*(  yf)*(  zf);
        sf[1][0][0] = (  xf)*(1-yf)*(1-zf);
        sf[1][0][1] = (  xf)*(1-yf)*(  zf);
        sf[1][1][0] = (  xf)*(  yf)*(1-zf);
        sf[1][1][1] = (  xf)*(  yf)*(  zf);

        float result = 0f;

        for(int i=0; i<2; i++)
            for(int j=0; j<2; j++)
                for(int k=0; k<2; k++)
                    result +=  is[i][j][k] * sf[i][j][k];

//        for(int i=0; i<2; i++)
//            for(int j=0; j<2; j++)
//                for(int k=0; k<2; k++)
//                    Log.v("Sample3d", Float.toString(is[i][j][k]));

        return result;
    }

    private int grayData(int x, int y, int z){
        int result = 0;
        if (data_length == 1){
            byte b = grayscale[z * sz[0] * sz[1] + y * sz[0] + x];
            result = ByteTranslate.byte1ToInt(b);
        }else if (data_length == 2){
            byte [] b = new byte[2];
            b[0] = grayscale[(z * sz[0] * sz[1] + y * sz[0] + x) * 2];
            b[1] = grayscale[(z * sz[0] * sz[1] + y * sz[0] + x) * 2 + 1];
            result = ByteTranslate.byte2ToInt(b, isBig);
        }else if (data_length == 4){
            byte [] b = new byte[4];
            b[0] = grayscale[(z * sz[0] * sz[1] + y * sz[0] + x) * 4];
            b[1] = grayscale[(z * sz[0] * sz[1] + y * sz[0] + x) * 4 + 1];
            b[2] = grayscale[(z * sz[0] * sz[1] + y * sz[0] + x) * 4 + 2];
            b[3] = grayscale[(z * sz[0] * sz[1] + y * sz[0] + x) * 4 + 3];
            result = ByteTranslate.byte2ToInt(b, isBig);
        }
        return result;
    }


    private float distance(float[] x, float[] y){
        int length = x.length;
        float sum = 0;

        for(int i=0; i<length; i++){
            sum += Math.pow(x[i]-y[i], 2);
        }
        return (float)Math.sqrt(sum);
    }

    private void normalize(float[] x){
        int length = x.length;
        float sum = 0;

        for(int i=0; i<length; i++)
            sum += Math.pow(x[i], 2);

        for(int i=0; i<length; i++)
            x[i] = x[i] / (float)Math.sqrt(sum);
    }



    private float[] ModeltoVolume(float[] input){
        float[] result = new float[3];

        result[0] = (1.0f - input[0] / mz[0]) * sz[0];
        result[1] = (1.0f - input[1] / mz[1]) * sz[1];
        result[2] = input[2] / mz[2] * sz[2];

        return result;
    }

    private float[] VolumetoModel(float[] input){
        float[] result = new float[3];

        result[0] = (sz[0] - input[0]) / sz[0] * mz[0];
        result[1] = (sz[1] - input[1]) / sz[1] * mz[1];
        result[2] = input[2] / sz[2] * mz[2];

        return result;
    }


    //减法运算
    private float [] minus(float[] x, float[] y){
        if(x.length != y.length){
            Log.v("minus","length is not the same!");
            return null;
        }

        int length = x.length;
        float [] result = new float[length];

        for (int i=0; i<length; i++)
            result[i] = x[i] - y[i];
        return result;
    }

    //加法运算
    private float [] plus(float[] x, float[] y){
        if(x.length != y.length){
            Log.v("plus","length is not the same!");
            return null;
        }

        int length = x.length;
        float [] result = new float[length];

        for (int i=0; i<length; i++)
            result[i] = x[i] + y[i];
        return result;
    }

    //除法运算
    private float [] devide(float[] x, float num){
        if(num == 0){
            Log.v("devide","can not be devided by 0");
        }

        int length = x.length;
        float [] result = new float[length];

        for(int i=0; i<length; i++)
            result[i] = x[i]/num;

        return result;
    }


    //除法运算
    private void devideByw(float[] x){
        if(Math.abs(x[3]) < 0.000001f){
            Log.v("devideByw","can not be devided by 0");
            return;
        }

        for(int i=0; i<3; i++)
            x[i] = x[i]/x[3];

    }

    //除法运算
    private float [] multiply(float[] x, float num){
        if(num == 0){
            Log.v("multiply","can not be multiply by 0");
        }

        int length = x.length;
        float [] result = new float[length];

        for(int i=0; i<length; i++)
            result[i] = x[i] * num;

        return result;
    }



    private ArrayList<Float> getLineDrawed(float [] line){
        float head_x = line[0];
        float head_y = line[1];
//        float [] result = new float[line.length];
        ArrayList<Float> result = new ArrayList<Float>();
        float [] head_result = solveMarkerCenter(head_x, head_y);
        if (head_result == null){
            return null;
        }
        for (int i = 0; i < 3; i++){
//            result[i] = head_result[i];
            result.add(head_result[i]);
        }//计算第一个点在物体坐标系的位置并保存

        float [] ex_head_result  = {head_result[0], head_result[1], head_result[2], 1.0f};
        float [] head_point = new float[4];
        Matrix.multiplyMV(head_point, 0, finalMatrix, 0,ex_head_result, 0);
        float current_z = head_point[2];

        for (int i = 1; i < line.length/3; i++){
            float x = line[i * 3];
            float y = line[i * 3 + 1];
            float [] mid_point = {x, y, current_z, 1.0f};
            float [] front_point = {x, y, -1.0f, 1.0f};

            float [] invertfinalMatrix = new float[16];
            Matrix.invertM(invertfinalMatrix, 0, finalMatrix, 0);

            float [] temp1 = new float[4];
            float [] temp2 = new float[4];
            Matrix.multiplyMV(temp1, 0, invertfinalMatrix, 0, mid_point, 0);
            Matrix.multiplyMV(temp2, 0, invertfinalMatrix, 0, front_point, 0);

            float [] mid_point_pixel = new float[3];
            float [] front_point_pixel = new float[3];
            mid_point_pixel = ModeltoVolume(temp1);
            front_point_pixel = ModeltoVolume(temp2);
//            for (int j = 0; j < 3; j++){
//                mid_point_pixel[j] = temp1[j] * sz[j];
//                front_point_pixel[j] = temp2[j] * sz[j];
//            }

            float [] dir = minus(front_point_pixel, mid_point_pixel);
            normalize(dir);

            float[][] dim = new float[3][2];
            for(int j = 0; j < 3; j++){
                dim[j][0] = 0;
                dim[j][1] = sz[j] - 1;
            }

            float value = 0;
            float [] result_pos = new float[3];
            for (int j = 0; j < 128; j++){
//                Log.v("getLineDrawed","~~~~~~~~~~~~~~");
                float [] pos = minus(mid_point_pixel, multiply(dir, (float)(j)));

                if (IsInBoundingBox(pos, dim)){
                    float current_value = Sample3d(pos[0], pos[1], pos[2]);
                    if (current_value > value){
                        value = current_value;
                        result_pos[0] = pos[0];
                        result_pos[1] = pos[1];
                        result_pos[2] = pos[2];
                    }
                }else{
                    break;
                }
            }
            for (int j = 0; j < 128; j++){
                float [] pos = plus(mid_point_pixel, multiply(dir, (float)(j)));

                if (IsInBoundingBox(pos, dim)){
                    float current_value = Sample3d(pos[0], pos[1], pos[2]);
                    if (current_value > value){
                        value = current_value;
                        result_pos[0] = pos[0];
                        result_pos[1] = pos[1];
                        result_pos[2] = pos[2];
                    }
                }else{
                    break;
                }
            }
            if (value == 0){
                break;
            }

            result_pos = VolumetoModel(result_pos);

            for (int j = 0; j < 3; j++){
                result.add(result_pos[j]);
            }

            float [] ex_result_pos = {result_pos[0], result_pos[1], result_pos[2], 1.0f};
            float [] current_pos = new float[4];
            Matrix.multiplyMV(current_pos, 0, finalMatrix, 0, ex_result_pos, 0);
            current_z = current_pos[2];
//            for (int j = 0; j < 3; j++){
////                result[i * 3 + j] = result_pos[j];
//                result.add(result_pos[j] / sz[j]);
//            }
//            current_z = result_pos[2];
        }
        return result;
    }





    private ArrayList<Float> getLineDrawed_2(float [] line){
        float head_x = line[0];
        float head_y = line[1];
//        float [] result = new float[line.length];
        ArrayList<Float> result = new ArrayList<Float>();
        float [] head_result = VolumetoModel(solveMarkerCenter(head_x, head_y));
        if (head_result == null){
            return null;
        }
        for (int i = 0; i < 3; i++){
//            result[i] = head_result[i];
            result.add(head_result[i]);
        }//计算第一个点在物体坐标系的位置并保存

        float [] ex_head_result  = {head_result[0], head_result[1], head_result[2], 1.0f};
        float [] head_point = new float[4];
        Matrix.multiplyMV(head_point, 0, finalMatrix, 0,ex_head_result, 0);
        float current_z = head_point[2]/head_point[3];

        for (int i = 1; i < line.length/3; i++){
            float x = line[i * 3];
            float y = line[i * 3 + 1];
            float [] mid_point = {x, y, current_z, 1.0f};
            float [] front_point = {x, y, -1.0f, 1.0f};

            float [] invertfinalMatrix = new float[16];
            Matrix.invertM(invertfinalMatrix, 0, finalMatrix, 0);

            float [] temp1 = new float[4];
            float [] temp2 = new float[4];
            Matrix.multiplyMV(temp1, 0, invertfinalMatrix, 0, mid_point, 0);
            Matrix.multiplyMV(temp2, 0, invertfinalMatrix, 0, front_point, 0);

            devideByw(temp1);
            devideByw(temp2);

            float [] mid_point_pixel = new float[3];
            float [] front_point_pixel = new float[3];
            mid_point_pixel = ModeltoVolume(temp1);
            front_point_pixel = ModeltoVolume(temp2);
//            for (int j = 0; j < 3; j++){
//                mid_point_pixel[j] = temp1[j] * sz[j];
//                front_point_pixel[j] = temp2[j] * sz[j];
//            }

            float [] dir = minus(front_point_pixel, mid_point_pixel);
            normalize(dir);

            float[][] dim = new float[3][2];
            for(int j = 0; j < 3; j++){
                dim[j][0] = 0;
                dim[j][1] = sz[j] - 1;
            }

            float value = 0;
            float [] result_pos = new float[3];
            for (int j = 1; j < 30; j++){
//                Log.v("getLineDrawed","~~~~~~~~~~~~~~");
                float [] pos = minus(mid_point_pixel, multiply(dir, (float)(j)));

                if (IsInBoundingBox(pos, dim)){
                    float current_value = Sample3d(pos[0], pos[1], pos[2]);
                    if (current_value > value){
                        value = current_value;
                        result_pos[0] = pos[0];
                        result_pos[1] = pos[1];
                        result_pos[2] = pos[2];
                    }
                }else{
                    break;
                }
            }
            for (int j = 1; j < 30; j++){
                float [] pos = plus(mid_point_pixel, multiply(dir, (float)(j)));

                if (IsInBoundingBox(pos, dim)){
                    float current_value = Sample3d(pos[0], pos[1], pos[2]);
                    if (current_value > value){
                        value = current_value;
                        result_pos[0] = pos[0];
                        result_pos[1] = pos[1];
                        result_pos[2] = pos[2];
                    }
                }else{
                    break;
                }
            }
            if (value == 0){
                break;
            }

            result_pos = VolumetoModel(result_pos);

            for (int j = 0; j < 3; j++){
                result.add(result_pos[j]);
            }

            float [] ex_result_pos = {result_pos[0], result_pos[1], result_pos[2], 1.0f};
            float [] current_pos = new float[4];
            Matrix.multiplyMV(current_pos, 0, finalMatrix, 0, ex_result_pos, 0);
            current_z = current_pos[2]/current_pos[3];
//            for (int j = 0; j < 3; j++){
////                result[i * 3 + j] = result_pos[j];
//                result.add(result_pos[j] / sz[j]);
//            }
//            current_z = result_pos[2];
        }
        return result;
    }





    public void addLineDrawed(ArrayList<Float> line){
        ArrayList<Float> lineAdded;
        float [] lineCurrent = new float[line.size()];
        Log.v("addLineDrawed", Integer.toString(line.size()));
        for (int i = 0; i < line.size(); i++){
            lineCurrent[i] = line.get(i);
        }
//        lineAdded = getLineDrawed(lineCurrent);
        lineAdded = getLineDrawed_2(lineCurrent);

        if (lineAdded != null){
//            lineDrawed.add(lineAdded);
            int max_n = curSwcList.maxnoden();
            V_NeuronSWC seg = new  V_NeuronSWC();
            for(int i=0; i < lineAdded.size()/3; i++){
                V_NeuronSWC_unit u = new V_NeuronSWC_unit();
                u.n = max_n + i+ 1;
                if(i==0)
                    u.parent = -1;
                else
                    u.parent = max_n + i;
                float[] xyz = ModeltoVolume(new float[]{lineAdded.get(i*3+0),lineAdded.get(i*3+1),lineAdded.get(i*3+2)});
                u.x = xyz[0];
                u.y = xyz[1];
                u.z = xyz[2];
                u.type = lastLineType;
                seg.append(u);
//                System.out.println("u n p x y z: "+ u.n +" "+u.parent+" "+u.x +" "+u.y+ " "+u.z);
            }
            curSwcList.append(seg);
//            Log.v("addLineDrawed", Integer.toString(lineAdded.size()));
        }
        else
            Log.v("draw line:::::", "nulllllllllllllllllll");
    }

    public  void deleteLine1(ArrayList<Float> line){
        System.out.println("deleteline1--------------------------");
        for (int i = 0; i < line.size() / 3 - 1; i++){
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(i * 3 + 3);
            float y2 = line.get(i * 3 + 4);
            for(int j=0; j<curSwcList.nsegs(); j++){
                System.out.println("delete curswclist --"+j);
                V_NeuronSWC seg = curSwcList.seg.get(j);
                if(seg.to_be_deleted)
                    continue;
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                for(int k=0; k<seg.row.size(); k++){
                    if(seg.row.get(k).parent != -1 && seg.getIndexofParent(k) != -1){
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(k));
                        swcUnitMap.put(k,parent);
                    }
                }
                System.out.println("delete: end map");
                for(int k=0; k<seg.row.size(); k++){
                    System.out.println("j: "+j+" k: "+k);
                    V_NeuronSWC_unit child = seg.row.get(k);
                    int parentid = (int) child.parent;
                    if (parentid == -1 || seg.getIndexofParent(k) == -1){
                        System.out.println("parent -1");
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(k);
                    float[] pchild = {(float) child.x, (float) child.y, (float) child.z};
                    float[] pparent = {(float) parent.x, (float) parent.y, (float) parent.z};
                    float[] pchildm = VolumetoModel(pchild);
                    float[] pparentm = VolumetoModel(pparent);
                    float[] p2 = {pchildm[0],pchildm[1],pchildm[2],1.0f};
                    float[] p1 = {pparentm[0],pparentm[1],pparentm[2],1.0f};

                    float [] p1Volumne = new float[4];
                    float [] p2Volumne = new float[4];
                    Matrix.multiplyMV(p1Volumne, 0, finalMatrix, 0, p1, 0);
                    Matrix.multiplyMV(p2Volumne, 0, finalMatrix, 0, p2, 0);
                    devideByw(p1Volumne);
                    devideByw(p2Volumne);
                    float x3 = p1Volumne[0];
                    float y3 = p1Volumne[1];
                    float x4 = p2Volumne[0];
                    float y4 = p2Volumne[1];

                    double m=(x2-x1)*(y3-y1)-(x3-x1)*(y2-y1);
                    double n=(x2-x1)*(y4-y1)-(x4-x1)*(y2-y1);
                    double p=(x4-x3)*(y1-y3)-(x1-x3)*(y4-y3);
                    double q=(x4-x3)*(y2-y3)-(x2-x3)*(y4-y3);

                    if( (Math.max(x1, x2) >= Math.min(x3, x4))
                            && (Math.max(x3, x4) >= Math.min(x1, x2))
                            && (Math.max(y1, y2) >= Math.min(y3, y4))
                            && (Math.max(y3, y4) >= Math.min(y1, y2))
                            && ((m * n) <= 0) && (p * q <= 0)){
                        System.out.println("------------------this is delete---------------");
                        seg.to_be_deleted = true;
                        break;
                    }
                }
            }
        }
        curSwcList.deleteMutiSeg(new Vector<Integer>());
    }

    public void splitCurve(ArrayList<Float> line){
        System.out.println("split1--------------------------");
        for (int i = 0; i < line.size() / 3 - 1; i++){
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(i * 3 + 3);
            float y2 = line.get(i * 3 + 4);
            for(int j=0; j<curSwcList.nsegs(); j++){
                System.out.println("delete curswclist --"+j);
                V_NeuronSWC seg = curSwcList.seg.get(j);
                if(seg.to_be_deleted)
                    continue;
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                for(int k=0; k<seg.row.size(); k++){
                    if(seg.row.get(k).parent != -1){
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(k));
                        swcUnitMap.put(k,parent);
                    }
                }
                System.out.println("delete: end map");
                for(int k=0; k<seg.row.size(); k++){
                    System.out.println("j: "+j+" k: "+k);
                    V_NeuronSWC_unit child = seg.row.get(k);
                    int parentid = (int) child.parent;
                    if (parentid == -1){
                        System.out.println("parent -1");
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(k);
                    float[] pchild = {(float) child.x, (float) child.y, (float) child.z};
                    float[] pparent = {(float) parent.x, (float) parent.y, (float) parent.z};
                    float[] pchildm = VolumetoModel(pchild);
                    float[] pparentm = VolumetoModel(pparent);
                    float[] p2 = {pchildm[0],pchildm[1],pchildm[2],1.0f};
                    float[] p1 = {pparentm[0],pparentm[1],pparentm[2],1.0f};

                    float [] p1Volumne = new float[4];
                    float [] p2Volumne = new float[4];
                    Matrix.multiplyMV(p1Volumne, 0, finalMatrix, 0, p1, 0);
                    Matrix.multiplyMV(p2Volumne, 0, finalMatrix, 0, p2, 0);
                    devideByw(p1Volumne);
                    devideByw(p2Volumne);
                    float x3 = p1Volumne[0];
                    float y3 = p1Volumne[1];
                    float x4 = p2Volumne[0];
                    float y4 = p2Volumne[1];

                    double m=(x2-x1)*(y3-y1)-(x3-x1)*(y2-y1);
                    double n=(x2-x1)*(y4-y1)-(x4-x1)*(y2-y1);
                    double p=(x4-x3)*(y1-y3)-(x1-x3)*(y4-y3);
                    double q=(x4-x3)*(y2-y3)-(x2-x3)*(y4-y3);

                    if( (Math.max(x1, x2) >= Math.min(x3, x4))
                            && (Math.max(x3, x4) >= Math.min(x1, x2))
                            && (Math.max(y1, y2) >= Math.min(y3, y4))
                            && (Math.max(y3, y4) >= Math.min(y1, y2))
                            && ((m * n) <= 0) && (p * q <= 0)){
                        System.out.println("------------------this is split---------------");
//                        seg.to_be_deleted = true;
//                        break;
                        V_NeuronSWC newSeg = new V_NeuronSWC();
                        V_NeuronSWC_unit first = seg.row.get(k);
                        try {
                            V_NeuronSWC_unit firstClone = first.clone();
                            newSeg.append(firstClone);
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                        int cur = k;
                        while (seg.getIndexofParent(cur) != -1){
                            cur = seg.getIndexofParent(cur);
                            V_NeuronSWC_unit nsu = swcUnitMap.get(cur);
                            try{
                                V_NeuronSWC_unit nsuClone = nsu.clone();
                                newSeg.append(nsuClone);
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                            seg.row.remove(cur);

                        }
                        curSwcList.append(newSeg);
                        splitPoints.add(pchildm[0]);
                        splitPoints.add(pchildm[1]);
                        splitPoints.add(pchildm[2]);
                        splitType = (int)child.type;
                        break;
                    }
                }
            }
        }
        curSwcList.deleteMutiSeg(new Vector<Integer>());
    }

    public void deleteLine(ArrayList<Float> line){
        for (int i = 0; i < line.size() / 3 - 1; i++){
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(i * 3 + 3);
            float y2 = line.get(i * 3 + 4);
            for (int j = 0; j < lineDrawed.size(); j++){
                ArrayList<Float> curLine = lineDrawed.get(j);
                for (int k = 0; k < curLine.size() / 3 - 1; k++){
                    float [] p1 = {curLine.get(k * 3), curLine.get(k * 3 + 1), curLine.get(k * 3 + 2), 1.0f};
                    float [] p2 = {curLine.get(k * 3 + 3), curLine.get(k * 3 + 4), curLine.get(k * 3 + 5), 1.0f};
                    float [] p1Volumne = new float[4];
                    float [] p2Volumne = new float[4];
                    Matrix.multiplyMV(p1Volumne, 0, finalMatrix, 0, p1, 0);
                    Matrix.multiplyMV(p2Volumne, 0, finalMatrix, 0, p2, 0);
                    devideByw(p1Volumne);
                    devideByw(p2Volumne);
                    float x3 = p1Volumne[0];
                    float y3 = p1Volumne[1];
                    float x4 = p2Volumne[0];
                    float y4 = p2Volumne[1];
                    float temp = 0.000001f;
//                    if( Math.abs((x2-x1)*(y4-y3)-(x4-x3)*(y2-y1))<temp ){
//                        if( (x1==x3)&&((y3-y1)*(y3-y2)<=temp||(y4-y1)*(y4-y2)<=temp) ){
//                            lineDrawed.remove(j);
//                            break;
//                        }
//                    }
//                    else{
                    double m=(x2-x1)*(y3-y1)-(x3-x1)*(y2-y1);
                    double n=(x2-x1)*(y4-y1)-(x4-x1)*(y2-y1);
                    double p=(x4-x3)*(y1-y3)-(x1-x3)*(y4-y3);
                    double q=(x4-x3)*(y2-y3)-(x2-x3)*(y4-y3);

                    if( (Math.max(x1, x2) >= Math.min(x3, x4))
                            && (Math.max(x3, x4) >= Math.min(x1, x2))
                            && (Math.max(y1, y2) >= Math.min(y3, y4))
                            && (Math.max(y3, y4) >= Math.min(y1, y2))
                            && ((m * n) <= 0) && (p * q <= 0)){
                        lineDrawed.remove(j);
                        break;
                    }
//                    }
                }
            }
        }
    }

    public void changeLineType(ArrayList<Float> line, int type){
        for (int i = 0; i < line.size() / 3 - 1; i++){
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(i * 3 + 3);
            float y2 = line.get(i * 3 + 4);
            for(int j=0; j<curSwcList.nsegs(); j++){
                System.out.println("delete curswclist --"+j);
                V_NeuronSWC seg = curSwcList.seg.get(j);
                if(seg.to_be_deleted)
                    continue;
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                for(int k=0; k<seg.row.size(); k++){
                    if(seg.row.get(k).parent != -1){
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(k));
                        swcUnitMap.put(k,parent);
                    }
                }
                System.out.println("delete: end map");
                for(int k=0; k<seg.row.size(); k++){
                    System.out.println("j: "+j+" k: "+k);
                    V_NeuronSWC_unit child = seg.row.get(k);
                    int parentid = (int) child.parent;
                    if (parentid == -1){
                        System.out.println("parent -1");
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(k);
                    float[] pchild = {(float) child.x, (float) child.y, (float) child.z};
                    float[] pparent = {(float) parent.x, (float) parent.y, (float) parent.z};
                    float[] pchildm = VolumetoModel(pchild);
                    float[] pparentm = VolumetoModel(pparent);
                    float[] p1 = {pchildm[0],pchildm[1],pchildm[2],1.0f};
                    float[] p2 = {pparentm[0],pparentm[1],pparentm[2],1.0f};

                    float [] p1Volumne = new float[4];
                    float [] p2Volumne = new float[4];
                    Matrix.multiplyMV(p1Volumne, 0, finalMatrix, 0, p1, 0);
                    Matrix.multiplyMV(p2Volumne, 0, finalMatrix, 0, p2, 0);
                    devideByw(p1Volumne);
                    devideByw(p2Volumne);
                    float x3 = p1Volumne[0];
                    float y3 = p1Volumne[1];
                    float x4 = p2Volumne[0];
                    float y4 = p2Volumne[1];

                    double m=(x2-x1)*(y3-y1)-(x3-x1)*(y2-y1);
                    double n=(x2-x1)*(y4-y1)-(x4-x1)*(y2-y1);
                    double p=(x4-x3)*(y1-y3)-(x1-x3)*(y4-y3);
                    double q=(x4-x3)*(y2-y3)-(x2-x3)*(y4-y3);

                    if( (Math.max(x1, x2) >= Math.min(x3, x4))
                            && (Math.max(x3, x4) >= Math.min(x1, x2))
                            && (Math.max(y1, y2) >= Math.min(y3, y4))
                            && (Math.max(y3, y4) >= Math.min(y1, y2))
                            && ((m * n) <= 0) && (p * q <= 0)){
                        seg.to_be_deleted = true;
                        break;
                    }
                }
            }
        }
        for(V_NeuronSWC seg : this.curSwcList.seg ){
            if (seg.to_be_deleted == true){
                for(int i = 0; i<seg.row.size(); i++){
                    seg.row.get(i).type = type;
                }
                seg.to_be_deleted = false;
            }
        }
    }



    public void importEswc(ArrayList<ArrayList<Float>> eswc){
        for (int i = 0; i < eswc.size(); i++){
            ArrayList<Float> currentLine = eswc.get(i);
            int parent = currentLine.get(6).intValue();
            if (parent == -1){
                continue;
            }
            ArrayList<Float> parentLine = eswc.get(parent - 1);
            eswcDrawed.add((sz[0] - parentLine.get(2)) / sz[0] * mz[0]);
            eswcDrawed.add((sz[1] - parentLine.get(3)) / sz[1] * mz[1]);
            eswcDrawed.add((parentLine.get(4)) / sz[2] * mz[2]);
            eswcDrawed.add((sz[0] - currentLine.get(2)) / sz[0] * mz[0]);
            eswcDrawed.add((sz[1] - currentLine.get(3)) / sz[1] * mz[1]);
            eswcDrawed.add((currentLine.get(4)) / sz[2] * mz[2]);
        }
    }

    public void importSwc(ArrayList<ArrayList<Float>> swc){
        for (int i = 0; i < swc.size(); i++){
            ArrayList<Float> currentLine = swc.get(i);
            int parent = currentLine.get(6).intValue();
            if (parent == -1){
                continue;
            }
            ArrayList<Float> parentLine = swc.get(parent - 1);
            swcDrawed.add((sz[0] - parentLine.get(2)) / sz[0] * mz[0]);
            swcDrawed.add((sz[1] - parentLine.get(3)) / sz[1] * mz[1]);
            swcDrawed.add((parentLine.get(4)) / sz[2] * mz[2]);
            swcDrawed.add((sz[0] - currentLine.get(2)) / sz[0] * mz[0]);
            swcDrawed.add((sz[1] - currentLine.get(3)) / sz[1] * mz[1]);
            swcDrawed.add((currentLine.get(4)) / sz[2] * mz[2]);
        }
    }
    public void importNeuronTree(NeuronTree nt){
//        V_NeuronSWC seg = nt.convertV_NeuronSWCFormat();
//        curSwcList.append(seg);

        System.out.println("----------------importNeuronTree----------------");
        try{
            Vector<V_NeuronSWC> segs = nt.devideByBranch();
            for (int i = 0; i < segs.size(); i++){
                curSwcList.append(segs.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void importApo(ArrayList<ArrayList<Float>> apo){
        for (int i = 0; i < apo.size(); i++){
            ArrayList<Float> currentLine = apo.get(i);
            apoDrawed.add((sz[0] - currentLine.get(5)) / sz[0] * mz[0]);
            apoDrawed.add((sz[1] - currentLine.get(6)) / sz[1] * mz[1]);
            apoDrawed.add((currentLine.get(4)) / sz[2] * mz[2]);
        }
    }



//    class XYZ{
//        private float this_x;
//
//        public XYZ(float x, float y, float z){
//
//        }
//    }

    public void saveCurrentSwc(String dir) throws Exception{
        NeuronTree nt = curSwcList.mergeSameNode();
        String filePath = dir + "//" + nt.name + ".swc";
        System.out.println("filepath: "+filePath);
        nt.writeSWC_file(filePath);
    }

    public void reNameCurrentSwc(String name){
        curSwcList.name = name;
    }

    public Image4DSimple getImg() {
        return img;
    }

    public ArrayList<ImageMarker> getMarkerList() {
        return MarkerList;

//        ArrayList<ImageMarker> Marker_volume_List = new ArrayList<ImageMarker>();
//
//        for (int i = 0; i < MarkerList.size(); i++){
//            ImageMarker marker_model = MarkerList.get(i);
//            float[] model = {marker_model.x, marker_model.y, marker_model.z};
//            float[] volume = ModeltoVolume(model);
//            ImageMarker marker_volume = new ImageMarker(volume[0], volume[1], volume[2]);
//            Marker_volume_List.add(marker_volume);
//        }
//        return Marker_volume_List;
    }


    public NeuronTree getNeuronTree(){
        try {
            return curSwcList.mergeSameNode();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }


    enum FileType
    {
        V3draw,
        SWC
    }

    public void setTakePic(boolean takePic) {
        isTakePic = takePic;
    }

    public String getmCapturePath() {
        return mCapturePath;
    }

    public void resetCapturePath() {
        mCapturePath = null;
    }

}







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
//        GLES30.glGenTextures(  //创建纹理对象
//                128, //产生纹理id的数量
//                textures, //纹理id的数组
//                0  //偏移量
//        );
//
////        int textures[] = new int[1]; //生成纹理id
////
////        GLES30.glGenTextures(  //创建纹理对象
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
//            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,mTextureId);
//
//            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                    GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);//设置MIN 采样方式
//            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                    GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);//设置MAG采样方式
//            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                    GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
//            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                    GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式
//
//
//            CreateBuffer(image_data[nID]);
//
//            GLES30.glTexImage2D(
//                    GLES30.GL_TEXTURE_2D, //纹理类型
//                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
//                    GLES30.GL_RGBA, //图片的格式
//                    128,   //
//                    128,   //
//                    0, //纹理边框尺寸();
//                    GLES30.GL_RGBA,
//                    GLES30.GL_UNSIGNED_BYTE,
//                    imageBuffer
//            );
//
//            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,mTextureId);
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
//        GLES30.glGenTextures(  //创建纹理对象
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
//            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,mTextureId);
//
//            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                    GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);//设置MIN 采样方式
//            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                    GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);//设置MAG采样方式
//            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                    GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
//            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
//                    GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式
//
//
//            CreateBuffer(image_data[nID]);
//
//            GLES30.glTexImage2D(
//                    GLES30.GL_TEXTURE_2D, //纹理类型
//                    0,//纹理的层次，0表示基本图像层，可以理解为直接贴图
//                    GLES30.GL_RGBA, //图片的格式
//                    224,   //
//                    224,   //
//                    0, //纹理边框尺寸();
//                    GLES30.GL_RGBA,
//                    GLES30.GL_UNSIGNED_BYTE,
//                    imageBuffer
//            );
//
//            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,mTextureId);
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
