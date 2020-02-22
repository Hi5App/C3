package com.example.myapplication__volume;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.net.Uri;
import android.os.Bundle;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity {
    private MyGLSurfaceView myGLSurfaceView;
    private MyRenderer myrenderer;
    private static final String DEBUG_TAG = "Gestures";
    private static Context context;
    private long length;
    private InputStream is;
    private String filepath = "";
    private boolean ifPainting = false;
    private boolean ifPoint = false;
//    private int Paintmode = 0;
    private ArrayList<Float> lineDrawed = new ArrayList<Float>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //接受从fileactivity传递过来的文件路径
        Intent intent = getIntent();
        filepath = intent.getStringExtra(FileActivity.EXTRA_MESSAGE);

        myrenderer = new MyRenderer();
        myrenderer.SetPath(filepath);

//        Uri uri = Uri.parse((String) filepath);
//
//        try {
//            ParcelFileDescriptor parcelFileDescriptor =
//                    getContentResolver().openFileDescriptor(uri, "r");
//
//            is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//
//            length = (int)parcelFileDescriptor.getStatSize();
//            myrenderer.setInputStream(is);
//            myrenderer.setLength(length);
//
//        }catch (Exception e){
//            Log.v("MainActivity","Some problems in the MainActivity");
//        }


        Log.v("filepath-mainactivity",filepath);

        myGLSurfaceView = new MyGLSurfaceView(this);
        setContentView(myGLSurfaceView);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        final Button button_1 = new Button(this);
        button_1.setText("draw");
        ll.addView(button_1);
        this.addContentView(ll, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT));

        final Button button_2 = new Button(this);
        button_2.setText("point");
        ll.addView(button_2);

        button_1.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                ifPainting = !ifPainting;
                ifPoint = false;
                if(ifPainting) {
                    button_1.setTextColor(Color.RED);
                    button_2.setTextColor(Color.BLACK);
                }
                else {
                    button_1.setTextColor(Color.BLACK);
                }
            }
        });
        button_2.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View v)
            {
                ifPoint = !ifPoint;
                ifPainting = false;
                if(ifPoint) {
                    button_2.setTextColor(Color.RED);
                    button_1.setTextColor(Color.BLACK);
                }
                else {
                    button_2.setTextColor(Color.BLACK);
                }
            }
        });


        context = getApplicationContext();

    }


    public static Context getContext() {
        return context;
    }


    //renderer 的生存周期和activity保持一致
    @Override
    protected void onPause() {
        super.onPause();
        myGLSurfaceView.onPause();
        Log.v("onPause","start-----");

    }

    @Override
    protected void onResume() {
        super.onResume();
        myGLSurfaceView.onResume();
        Log.v("Path", filepath);
        Log.v("onResume","start-----");

//        myrenderer = new MyRenderer();

//        Uri uri = Uri.parse((String) filepath);
//
//        try {
//            ParcelFileDescriptor parcelFileDescriptor =
//                    getContentResolver().openFileDescriptor(uri, "r");
//
//            is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//
//            length = (int)parcelFileDescriptor.getStatSize();
//            myrenderer.setInputStream(is);
//            myrenderer.setLength(length);
//
//        }catch (Exception e){
//            Log.v("MainActivity","Some problems in the MainActivity");
//        }
//
//        Log.v("Load data successfully!", "here we are");
    }



    //opengl中的显示区域
    class MyGLSurfaceView extends GLSurfaceView {
        private float X, Y;
        private double dis_start;
        private boolean isZooming;


        public MyGLSurfaceView(Context context) {
            super(context);

            ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            String v = info.getGlEsVersion(); //判断是否为3.0 ，一般4.4就开始支持3.0版本了。

            Log.v("MainActivity","GLES-version: " + v );

            //设置一下opengl版本；
            setEGLContextClientVersion(3);

//            myrenderer.setLineDrawed(lineDrawed);

            setRenderer(myrenderer);



            //调用 onPause 的时候保存EGLContext
            setPreserveEGLContextOnPause(true);

            //当发生交互时重新执行渲染， 需要配合requestRender();
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        }

        //触摸屏幕的事件
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouchEvent(MotionEvent motionEvent) {

            //ACTION_DOWN不return true，就无触发后面的各个事件
            if (motionEvent != null) {
                final float normalizedX =toOpenGLCoord(this,motionEvent.getX(),true);
                final float normalizedY =toOpenGLCoord(this,motionEvent.getY(),false);
//
//                final float normalizedX =motionEvent.getX();
//                final float normalizedY =motionEvent.getY();

                switch (motionEvent.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        X=normalizedX;
                        Y=normalizedY;
                        if(ifPainting){
                            lineDrawed.add(X);
                            lineDrawed.add(Y);
                            lineDrawed.add(-1.0f);
                            myrenderer.setIfPainting(true);
                            requestRender();
                            Log.v("actionPointerDown", "Paintinggggggggggg");
                        }
                        if(ifPoint){
                            Log.v("actionPointerDown", "Pointinggggggggggg");
                            myrenderer.setMarkerDrawed(X, Y);
                            Log.v("actionPointerDown", "(" + X + "," + Y +")");
                            requestRender();

                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        isZooming=true;
                        float x1=toOpenGLCoord(this,motionEvent.getX(1),true);
                        float y1=toOpenGLCoord(this,motionEvent.getY(1),false);

//                        float x1=motionEvent.getX(1);
//                        float y1=motionEvent.getY(1);
                        dis_start=computeDis(normalizedX,x1,normalizedY,y1);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!ifPainting) {
                            if (isZooming) {
                                float x2 = toOpenGLCoord(this, motionEvent.getX(1), true);
                                float y2 = toOpenGLCoord(this, motionEvent.getY(1), false);

//                            float x2=motionEvent.getX(1);
//                            float y2=motionEvent.getY(1);
                                double dis = computeDis(normalizedX, x2, normalizedY, y2);
                                double scale = dis / dis_start;
                                myrenderer.zoom((float) scale);

                                //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                                requestRender();
                                dis_start = dis;
                            } else {
                                myrenderer.rotate(normalizedX - X, normalizedY - Y, (float) (computeDis(normalizedX, X, normalizedY, Y)));

                                //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                                requestRender();
                                X = normalizedX;
                                Y = normalizedY;
                            }
                        } else {
                            lineDrawed.add(normalizedX);
                            lineDrawed.add(normalizedY);
                            lineDrawed.add(-1.0f);

                            myrenderer.setLineDrawed(lineDrawed);
                            requestRender();

                            invalidate();
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        isZooming=false;
                        X = normalizedX;
                        Y = normalizedY;
//                        if (ifPainting){
//                            lineDrawed.clear();
//                            myrenderer.setLineDrawed(lineDrawed);
//                            requestRender();
//
//                            myrenderer.setIfPainting(false);
//                            requestRender();
//
//                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (ifPainting){
                            myrenderer.setIfPainting(false);
                            myrenderer.addLineDrawed(lineDrawed);
                            lineDrawed.clear();
                            myrenderer.setLineDrawed(lineDrawed);

                            requestRender();
//                            requestRender();
                        }
                        break;
                    default:break;
                }
                return true;
            }
            return false;
        }



        //坐标系变换
        private float toOpenGLCoord(View view,float value,boolean isWidth){
            if(isWidth){
                return (value / (float) view.getWidth()) * 2 - 1;
            }else {
                return -((value / (float) view.getHeight()) * 2 - 1);
            }
        }


        //距离计算
        private double computeDis(float x1,float x2,float y1,float y2){
            return sqrt(pow((x2-x1),2)+pow((y2-y1),2));
        }
    }
}
