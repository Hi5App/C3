package com.example.myapplication__volume;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;

import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;

import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private MyRenderer myrenderer;

    private MyGLSurfaceView myGLSurfaceView;

    private String filepath;

    private float[] position;
    private float[] dir;
    private float[] head;


    private float [] moveDir;
    private float [] viewRotateDir;

    private Context context;

    private Timer timer;
    private TimerTask task;
    private Handler TimerHandler;
    Runnable myTimerRun;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        filepath = extras.getString("FilePath");
        position = extras.getFloatArray("Position");
        dir = extras.getFloatArray("Dir");
        head = MyRenderer.locateHead(dir[0], dir[1], dir[2]);

        context = getApplicationContext();

        setContentView(R.layout.activity_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myrenderer = new MyRenderer();
        myrenderer.SetPath(filepath);
        myrenderer.setGamePosition(position);
        myrenderer.setGameDir(dir);
        myrenderer.setGameHead(head);
        myrenderer.addMarker(position);
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

//        Button Check_Yes = new Button(this);
//        Check_Yes.setText("Y");
//
//        FrameLayout.LayoutParams lp_check_yes = new FrameLayout.LayoutParams(120, 120);
//        lp_check_yes.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//        lp_check_yes.setMargins(0, 0, 20, 500);
//        this.addContentView(Check_Yes, lp_check_yes);
//
//        Check_Yes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                boolean [] b = {false};
//                timer = new Timer();
//                task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        System.out.println("AAAAA");
//                        try {
//                            b[0] = true;
//                            System.out.print(moveDir[0]);
//                            System.out.print(' ');
//                            System.out.print(moveDir[1]);
//
//                            System.out.print(viewRotateDir[0]);
//                            System.out.print(' ');
//                            System.out.print(viewRotateDir[1]);
//                        } catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                };
//                timer.schedule(task, 0, 1000);
//                if (b[0]){
//                    System.out.println("HHHHHHHHHHHH");
//                }
//            }
//        });

        moveDir = new float[]{0f, 0f, 0f};
        viewRotateDir = new float[]{0f, 0f, 0f};

        rockerView1.setRockerChangeListener(new MyRockerView.RockerChangeListener() {
            @Override
            public void report(float x, float y) {

                moveDir[0] = x;
                moveDir[1] = y;
//                System.out.println(x);
//                System.out.println(y);
            }
        });

        rockerView2.setRockerChangeListener(new MyRockerView.RockerChangeListener() {
            @Override
            public void report(float x, float y) {

                viewRotateDir[0] = x;
                viewRotateDir[1] = y;
            }
        });

//        final boolean[] b = {false};
//
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                try {
                    float angleH = viewRotateDir[0] / 100;
                    float angleV = viewRotateDir[1] / 100;
                    float x = moveDir[0] / 10000;
                    float y = moveDir[1] / 10000;
//                    System.out.println("SSSSSSSSSSSSSSSS");
//                    System.out.print(viewRotateDir[0]);
//                    System.out.print(' ');
//                    System.out.print(moveDir[0]);
//                    System.out.print(' ');
//                    System.out.println(moveDir[1]);
                    if (angleH != 0 || angleV != 0 || x != 0 || y != 0) {
                        float[] dirE = new float[]{dir[0], dir[1], dir[2], 1};
                        float[] headE = new float[]{head[0], head[1], head[2], 1};
                        float[] axisV = new float[3];
                        if (angleH != 0 && angleV != 0) {
//                        float[] head = MyRenderer.locateHead(dir[0], dir[1], dir[2]);
//                        float[] head = new float[]{0, 1, 0};
//                        float[] axisV = new float[]{dir[1] * head[2] - dir[2] * head[1], dir[2] * head[0] - dir[0] * head[2], dir[0] * head[1] - dir[1] * head[0]};

                            float[] rotationHMatrix = new float[16];
                            float[] rotationVMatrix = new float[16];
//                        float[] rotationMatrix = new float[16];

//                            if (angleH > 0) {
                                Matrix.setRotateM(rotationHMatrix, 0, -angleH, head[0], head[1], head[2]);
//                            } else {
//                                Matrix.setRotateM(rotationHMatrix, 0, -angleH, -head[0], -head[1], -head[2]);
//                            }

//                        float[] dirE = new float[]{dir[0], dir[1], dir[2], 1};
//                        float[] headE = new float[]{head[0], head[1], head[2], 1};
//                        float[] dirF = new float[4];
                            Matrix.multiplyMV(dirE, 0, rotationHMatrix, 0, dirE, 0);

                            axisV = new float[]{dirE[1] * head[2] - dirE[2] * head[1], dirE[2] * head[0] - dirE[0] * head[2], dirE[0] * head[1] - dirE[1] * head[0]};

//                            if (angleV > 0) {
                                Matrix.setRotateM(rotationVMatrix, 0, -angleV, axisV[0], axisV[1], axisV[2]);
//                            } else {
//                                Matrix.setRotateM(rotationVMatrix, 0, -angleV, -axisV[0], -axisV[1], -axisV[2]);
//                            }
                            Matrix.multiplyMV(dirE, 0, rotationVMatrix, 0, dirE, 0);
                            Matrix.multiplyMV(headE, 0, rotationVMatrix, 0, headE, 0);


                            dir = new float[]{dirE[0], dirE[1], dirE[2]};
                            head = new float[]{headE[0], headE[1], headE[2]};
                        }
                        if (x != 0 && y != 0) {
                            axisV = new float[]{dirE[1] * head[2] - dirE[2] * head[1], dirE[2] * head[0] - dirE[0] * head[2], dirE[0] * head[1] - dirE[1] * head[0]};
                            float XL = (float)Math.sqrt(axisV[0] * axisV[0] + axisV[1] * axisV[1] + axisV[2] * axisV[2]);
                            float [] X = new float[]{axisV[0] / XL, axisV[1] / XL, axisV[2] / XL};
                            float YL = (float)Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1] + dir[2] * dir[2]);
                            float [] Y = new float[]{dir[0] / YL, dir[1] / YL, dir[2] / YL};

                            position[0] = position[0] + X[0] * x - Y[0] * y;
                            position[1] = position[1] + X[1] * x - Y[1] * y;
                            position[2] = position[2] + X[2] * x - Y[2] * y;

                            myrenderer.clearMarkerList();
                            myrenderer.addMarker(position);
                        }

                        myrenderer.setGameDir(dir);
                        myrenderer.setGameHead(head);
                        myrenderer.setGamePosition(position);

//                        System.out.println("SSSSSSSSSSSSSSSS");
//                        System.out.print(head[0]);
//                        System.out.print(' ');
//                        System.out.print(head[1]);
//                        System.out.print(' ');
//                        System.out.println(head[2]);
//                        System.out.print(position[0]);
//                        System.out.print(' ');
//                        System.out.print(position[1]);
//                        System.out.print(' ');
//                        System.out.println(position[2]);

//                        myrenderer.updateVisual();
                        myGLSurfaceView.requestRender();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(task, 0, 100);
//
//        if (b[0]){
//            System.out.println("DDDDDDDDDDDDDDDD");
//        }

//        Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 1){
//                    Toast.makeText(context, "sssssssssssss", Toast.LENGTH_LONG);
//                }
//                super.handleMessage(msg);
//
//            }
//        };
//
//        class MyThread extends Thread {//这里也可用Runnable接口实现
//            @Override
//            public void run() {
//                while (true){
//                    try {
//                        Thread.sleep(1000);//每隔1s执行一次
//                        Message msg = new Message();
//                        msg.what = 1;
//                        handler.sendMessage(msg);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        }
//        new Thread(new MyThread()).start();

//        setVisual();

    }

    @Override
    protected void onPause() {
        super.onPause();
        myGLSurfaceView.onPause();
        Log.v("onPause", "start-----");
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

        ArrayList<Float> tangent = myrenderer.tangentPlane(position[0], position[1], position[2], dir[0], dir[1], dir[2],  1);

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
//        boolean gameSucceed = myrenderer.driveMode(vertexPoints, dir);
//        if (!gameSucceed){
//            Toast.makeText(context, "wrong vertex to draw", Toast.LENGTH_SHORT);
//        } else {
//            myGLSurfaceView.requestRender();
//        }

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