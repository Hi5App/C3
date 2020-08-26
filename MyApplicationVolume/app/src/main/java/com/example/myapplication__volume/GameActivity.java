package com.example.myapplication__volume;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private MyRenderer myrenderer;

    private MyGLSurfaceView myGLSurfaceView;

    private String filepath;

    private float[] position;
    private float[] dir;

    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        filepath = extras.getString("FilePath");
        position = extras.getFloatArray("Position");
        dir = extras.getFloatArray("Dir");

        context = getApplicationContext();

        setContentView(R.layout.activity_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myrenderer = new MyRenderer();
        myrenderer.SetPath(filepath);
        myrenderer.setGamePosition(position);
        myrenderer.setGameDir(dir);
        myrenderer.setIfGame(true);

        myGLSurfaceView = new GameActivity.MyGLSurfaceView(this);
        FrameLayout ll = (FrameLayout) findViewById(R.id.gameContainer);
        ll.addView(myGLSurfaceView);

        MyRockerView rockerView1 = (MyRockerView)findViewById(R.id.rockerView1);

        FrameLayout.LayoutParams lp_rocker1 = new FrameLayout.LayoutParams(300, 300);
        lp_rocker1.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_rocker1.setMargins(0, 0, 20, 20);
        ll.removeView(rockerView1);
        this.addContentView(rockerView1, lp_rocker1);

        MyRockerView rockerView2 = (MyRockerView)findViewById(R.id.rockerView2);

        FrameLayout.LayoutParams lp_rocker2 = new FrameLayout.LayoutParams(300, 300);
        lp_rocker2.gravity = Gravity.BOTTOM | Gravity.LEFT;
        lp_rocker2.setMargins(20, 0, 0, 20);
        ll.removeView(rockerView2);
        this.addContentView(rockerView2, lp_rocker2);

//        setVisual();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    /**
     * call the corresponding function when button in top bar clicked
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.back:
//                try {
//                    Intent intent = new Intent(HelpActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                finish();
            default:
                return true;
//                return super.onOptionsItemSelected(item);
        }
    }

    private void setVisual(){


        ArrayList<Integer> sec_proj1 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj2 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj3 = new ArrayList<Integer>();
        ArrayList<Integer> sec_proj4 = new ArrayList<Integer>();
        ArrayList<Float> sec_anti = new ArrayList<Float>();

        ArrayList<Float> tangent = myrenderer.tangentPlane(position[0], position[1], position[2], dir[0], dir[1], dir[2], 0, 1);

        System.out.println("TangentPlane:::::");
        System.out.println(tangent.size());

        //然后对三维坐标进行映射
        if (dir[2]==0)
        //先判断切面是不是与XOY面垂直，如果垂直就映射到XOZ平面
        {
            for (int i=0;i<tangent.size();i+=3) {
                if(tangent.get(i)>=0 & tangent.get(i+2)>=0) {

                    sec_proj1.add(i);

                }// 第一象限
                else if(tangent.get(i)<=0 & tangent.get(i+2)>=0) {

                    sec_proj2.add(i);

                }// 第二象限
                else if(tangent.get(i)<=0 & tangent.get(i+2)<=0) {

                    sec_proj3.add(i);

                }// 第三象限
                else if(tangent.get(i)>=0 & tangent.get(i+2)<=0) {

                    sec_proj4.add(i);

                }// 第四象限

            }



            //只用判断大于1的情况，如果没有那就刚好不用管了，如果只有一个元素，那也不用排序了
            if (sec_proj1.size()>1) {
                for (int i=0;i<sec_proj1.size();i++) {
                    for (int j=0;j<sec_proj1.size()-i-1;j++) {
                        if(tangent.get(sec_proj1.get(j))!=0 & tangent.get(sec_proj1.get(j+1))!=0) {
                            if(tangent.get(sec_proj1.get(j)+2)/tangent.get(sec_proj1.get(j)) > tangent.get(sec_proj1.get(j+1)+2)/tangent.get(sec_proj1.get(j+1))) {
                                int temp = sec_proj1.get(j);
                                sec_proj1.set(j, sec_proj1.get(j+1));
                                sec_proj1.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj1.get(j))==0 & tangent.get(sec_proj1.get(j+1))==0) {
                                if(tangent.get(sec_proj1.get(j)+2)<tangent.get(sec_proj1.get(j+1)+2)) {
                                    int temp = sec_proj1.get(j);
                                    sec_proj1.set(j, sec_proj1.get(j+1));
                                    sec_proj1.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj1.get(j))==0) {
                                    int temp = sec_proj1.get(j);
                                    sec_proj1.set(j, sec_proj1.get(j+1));
                                    sec_proj1.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj2.size()>1) {
                for (int i=0;i<sec_proj2.size();i++) {
                    for (int j=0;j<sec_proj2.size()-i-1;j++) {
                        if(tangent.get(sec_proj2.get(j))!=0 & tangent.get(sec_proj2.get(j+1))!=0) {
                            if(tangent.get(sec_proj2.get(j)+2)/tangent.get(sec_proj2.get(j)) > tangent.get(sec_proj2.get(j+1)+2)/tangent.get(sec_proj2.get(j+1))) {
                                int temp = sec_proj2.get(j);
                                sec_proj2.set(j, sec_proj2.get(j+1));
                                sec_proj2.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj2.get(j))==0 & tangent.get(sec_proj2.get(j+1))==0) {
                                if(tangent.get(sec_proj2.get(j)+2)<tangent.get(sec_proj2.get(j+1)+2)) {
                                    int temp = sec_proj2.get(j);
                                    sec_proj2.set(j, sec_proj2.get(j+1));
                                    sec_proj2.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj2.get(j))==0) {
                                    int temp = sec_proj2.get(j);
                                    sec_proj2.set(j, sec_proj2.get(j+1));
                                    sec_proj2.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj3.size()>1) {
                for (int i=0;i<sec_proj3.size();i++) {
                    for (int j=0;j<sec_proj3.size()-i-1;j++) {
                        if(tangent.get(sec_proj3.get(j))!=0 & tangent.get(sec_proj3.get(j+1))!=0) {
                            if(tangent.get(sec_proj3.get(j)+2)/tangent.get(sec_proj3.get(j)) > tangent.get(sec_proj3.get(j+1)+2)/tangent.get(sec_proj3.get(j+1))) {
                                int temp = sec_proj3.get(j);
                                sec_proj3.set(j, sec_proj3.get(j+1));
                                sec_proj3.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj3.get(j))==0 & tangent.get(sec_proj3.get(j+1))==0) {
                                if(tangent.get(sec_proj3.get(j)+2)<tangent.get(sec_proj3.get(j+1)+2)) {
                                    int temp = sec_proj3.get(j);
                                    sec_proj3.set(j, sec_proj3.get(j+1));
                                    sec_proj3.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj3.get(j))==0) {
                                    int temp = sec_proj3.get(j);
                                    sec_proj3.set(j, sec_proj3.get(j+1));
                                    sec_proj3.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }

            if (sec_proj4.size()>1) {
                for (int i=0;i<sec_proj4.size();i++) {
                    for (int j=0;j<sec_proj4.size()-i-1;j++) {
                        if(tangent.get(sec_proj4.get(j))!=0 & tangent.get(sec_proj4.get(j+1))!=0) {
                            if(tangent.get(sec_proj4.get(j)+2)/tangent.get(sec_proj4.get(j)) > tangent.get(sec_proj4.get(j+1)+2)/tangent.get(sec_proj4.get(j+1))) {
                                int temp = sec_proj4.get(j);
                                sec_proj4.set(j, sec_proj4.get(j+1));
                                sec_proj4.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj4.get(j))==0 & tangent.get(sec_proj4.get(j+1))==0) {
                                if(tangent.get(sec_proj4.get(j)+1)<tangent.get(sec_proj4.get(j+1)+1)) {
                                    int temp = sec_proj4.get(j);
                                    sec_proj4.set(j, sec_proj4.get(j+1));
                                    sec_proj4.set(j+1, temp); //冒泡排序
                                }
                            }
                            else {
                                if(tangent.get(sec_proj4.get(j))==0) {
                                    int temp = sec_proj4.get(j);
                                    sec_proj4.set(j, sec_proj4.get(j+1));
                                    sec_proj4.set(j+1, temp); //冒泡排序
                                }
                            }
                        }
                    }
                }
            }


        }
        else {
            for (int i=0;i<tangent.size();i+=3) {
                if(tangent.get(i)>=0 & tangent.get(i+1)>=0) {

                    sec_proj1.add(i);

                }// 第一象限
                else if(tangent.get(i)<=0 & tangent.get(i+1)>=0) {

                    sec_proj2.add(i);

                }// 第二象限
                else if(tangent.get(i)<=0 & tangent.get(i+1)<=0) {

                    sec_proj3.add(i);

                }// 第三象限
                else if(tangent.get(i)>=0 & tangent.get(i+1)<=0) {

                    sec_proj4.add(i);

                }// 第四象限

            }
        }





        //只用判断大于1的情况，如果没有那就刚好不用管了，如果只有一个元素，那也不用排序了
        if (sec_proj1.size()>1) {
            for (int i=0;i<sec_proj1.size();i++) {
                for (int j=0;j<sec_proj1.size()-i-1;j++) {
                    if(tangent.get(sec_proj1.get(j))!=0 & tangent.get(sec_proj1.get(j+1))!=0) {
                        if(tangent.get(sec_proj1.get(j)+1)/tangent.get(sec_proj1.get(j)) > tangent.get(sec_proj1.get(j+1)+1)/tangent.get(sec_proj1.get(j+1))) {
                            int temp = sec_proj1.get(j);
                            sec_proj1.set(j, sec_proj1.get(j+1));
                            sec_proj1.set(j+1, temp); //冒泡排序
                        }
                    }
                    else {
                        if(tangent.get(sec_proj1.get(j))==0 & tangent.get(sec_proj1.get(j+1))==0) {
                            if(tangent.get(sec_proj1.get(j)+1)<tangent.get(sec_proj1.get(j+1)+1)) {
                                int temp = sec_proj1.get(j);
                                sec_proj1.set(j, sec_proj1.get(j+1));
                                sec_proj1.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj1.get(j))==0) {
                                int temp = sec_proj1.get(j);
                                sec_proj1.set(j, sec_proj1.get(j+1));
                                sec_proj1.set(j+1, temp); //冒泡排序
                            }
                        }
                    }
                }
            }
        }

        if (sec_proj2.size()>1) {
            for (int i=0;i<sec_proj2.size();i++) {
                for (int j=0;j<sec_proj2.size()-i-1;j++) {
                    if(tangent.get(sec_proj2.get(j))!=0 & tangent.get(sec_proj2.get(j+1))!=0) {
                        if(tangent.get(sec_proj2.get(j)+1)/tangent.get(sec_proj2.get(j)) > tangent.get(sec_proj2.get(j+1)+1)/tangent.get(sec_proj2.get(j+1))) {
                            int temp = sec_proj2.get(j);
                            sec_proj2.set(j, sec_proj2.get(j+1));
                            sec_proj2.set(j+1, temp); //冒泡排序
                        }
                    }
                    else {
                        if(tangent.get(sec_proj2.get(j))==0 & tangent.get(sec_proj2.get(j+1))==0) {
                            if(tangent.get(sec_proj2.get(j)+1)<tangent.get(sec_proj2.get(j+1)+1)) {
                                int temp = sec_proj2.get(j);
                                sec_proj2.set(j, sec_proj2.get(j+1));
                                sec_proj2.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj2.get(j))==0) {
                                int temp = sec_proj2.get(j);
                                sec_proj2.set(j, sec_proj2.get(j+1));
                                sec_proj2.set(j+1, temp); //冒泡排序
                            }
                        }
                    }
                }
            }
        }

        if (sec_proj3.size()>1) {
            for (int i=0;i<sec_proj3.size();i++) {
                for (int j=0;j<sec_proj3.size()-i-1;j++) {
                    if(tangent.get(sec_proj3.get(j))!=0 & tangent.get(sec_proj3.get(j+1))!=0) {
                        if(tangent.get(sec_proj3.get(j)+1)/tangent.get(sec_proj3.get(j)) > tangent.get(sec_proj3.get(j+1)+1)/tangent.get(sec_proj3.get(j+1))) {
                            int temp = sec_proj3.get(j);
                            sec_proj3.set(j, sec_proj3.get(j+1));
                            sec_proj3.set(j+1, temp); //冒泡排序
                        }
                    }
                    else {
                        if(tangent.get(sec_proj3.get(j))==0 & tangent.get(sec_proj3.get(j+1))==0) {
                            if(tangent.get(sec_proj3.get(j)+1)<tangent.get(sec_proj3.get(j+1)+1)) {
                                int temp = sec_proj3.get(j);
                                sec_proj3.set(j, sec_proj3.get(j+1));
                                sec_proj3.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj3.get(j))==0) {
                                int temp = sec_proj3.get(j);
                                sec_proj3.set(j, sec_proj3.get(j+1));
                                sec_proj3.set(j+1, temp); //冒泡排序
                            }
                        }
                    }
                }
            }
        }

        if (sec_proj4.size()>1) {
            for (int i=0;i<sec_proj4.size();i++) {
                for (int j=0;j<sec_proj4.size()-i-1;j++) {
                    if(tangent.get(sec_proj4.get(j))!=0 & tangent.get(sec_proj4.get(j+1))!=0) {
                        if(tangent.get(sec_proj4.get(j)+1)/tangent.get(sec_proj4.get(j)) > tangent.get(sec_proj4.get(j+1)+1)/tangent.get(sec_proj4.get(j+1))) {
                            int temp = sec_proj4.get(j);
                            sec_proj4.set(j, sec_proj4.get(j+1));
                            sec_proj4.set(j+1, temp); //冒泡排序
                        }
                    }
                    else {
                        if(tangent.get(sec_proj4.get(j))==0 & tangent.get(sec_proj4.get(j+1))==0) {
                            if(tangent.get(sec_proj4.get(j)+1)<tangent.get(sec_proj4.get(j+1)+1)) {
                                int temp = sec_proj4.get(j);
                                sec_proj4.set(j, sec_proj4.get(j+1));
                                sec_proj4.set(j+1, temp); //冒泡排序
                            }
                        }
                        else {
                            if(tangent.get(sec_proj4.get(j))==0) {
                                int temp = sec_proj4.get(j);
                                sec_proj4.set(j, sec_proj4.get(j+1));
                                sec_proj4.set(j+1, temp); //冒泡排序
                            }
                        }
                    }
                }
            }
        }


        for(int i=0;i<sec_proj1.size();i++) {
            sec_anti.add(tangent.get(sec_proj1.get(i)));
            sec_anti.add(tangent.get(sec_proj1.get(i)+1));
            sec_anti.add(tangent.get(sec_proj1.get(i)+2));
        }
        for(int i=0;i<sec_proj2.size();i++) {
            sec_anti.add(tangent.get(sec_proj2.get(i)));
            sec_anti.add(tangent.get(sec_proj2.get(i)+1));
            sec_anti.add(tangent.get(sec_proj2.get(i)+2));
        }
        for(int i=0;i<sec_proj3.size();i++) {
            sec_anti.add(tangent.get(sec_proj3.get(i)));
            sec_anti.add(tangent.get(sec_proj3.get(i)+1));
            sec_anti.add(tangent.get(sec_proj3.get(i)+2));
        }
        for(int i=0;i<sec_proj4.size();i++) {
            sec_anti.add(tangent.get(sec_proj4.get(i)));
            sec_anti.add(tangent.get(sec_proj4.get(i)+1));
            sec_anti.add(tangent.get(sec_proj4.get(i)+2));
        }

        float [] vertexPoints = new float[sec_anti.size()];
        for (int i = 0; i < sec_anti.size(); i++){

            vertexPoints[i] = sec_anti.get(i);
            System.out.print(vertexPoints[i]);
            System.out.print(" ");
            if (i % 3 == 2){
                System.out.print("\n");
            }
        }

        boolean gameSucceed = myrenderer.driveMode(vertexPoints, dir);
        if (!gameSucceed){
            Toast.makeText(context, "wrong vertex to draw", Toast.LENGTH_SHORT);
        } else {
            myGLSurfaceView.requestRender();
        }
    }

    class MyGLSurfaceView extends GLSurfaceView {
//        private float X, Y;
//        private double dis_start;
//        private float dis_x_start;
//        private float dis_y_start;
//        private boolean isZooming;
//        private boolean isZoomingNotStop;
//        private float x1_start;
//        private float x0_start;
//        private float y1_start;
//        private float y0_start;


        public MyGLSurfaceView(Context context) {
            super(context);

            ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            String v = info.getGlEsVersion(); //判断是否为3.0 ，一般4.4就开始支持3.0版本了。

            Log.v("GameActivity", "GLES-version: " + v);

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

    }
}