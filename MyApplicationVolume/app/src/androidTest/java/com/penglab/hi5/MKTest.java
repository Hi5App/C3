package com.penglab.hi5;

import android.app.Instrumentation;
import android.os.RemoteException;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class MKTest {
    public Instrumentation mInstrumentation;
    public UiDevice mUidevice;

    @Before
    public void setUp(){
        mInstrumentation = InstrumentationRegistry.getInstrumentation();
        mUidevice = UiDevice.getInstance(mInstrumentation);
    }




    /*
    Test collaboration for XiaoMi
   */
    @Test
    public void MiTest() throws RemoteException, InterruptedException {

        // start the app
        mUidevice.click(930,250);
        Thread.sleep(3000);

        //open the file
        mUidevice.findObject(By.res("com.penglab.hi5:id/file")).click();
        Thread.sleep(1000);


        mUidevice.findObject(By.text("Open BigData")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("18454")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("18454_00049")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("1.ano")).click();
        Thread.sleep(8000);


        // click the draw button
        mUidevice.click(110,300);
        Thread.sleep(3000);
        mUidevice.click(150,680);
        Thread.sleep(2000);
        mUidevice.click(730,530);
        Thread.sleep(2000);

        for (int i=0; i<3000; i++){

            Random ran = new Random(3000-i);

            if (i % 20 == 0){
                // x 200 -> 800,  y 800 -> 1400
                int x1 = ran.nextInt(600) + 200;
                int x2 = ran.nextInt(600) + 200;
                int y1 = ran.nextInt(600) + 800;
                int y2 = ran.nextInt(600) + 800;

                mUidevice.swipe(x1,y1,x2,y2,10);
                Thread.sleep(5000);
            }

            // open the file
            mUidevice.findObject(By.res("com.penglab.hi5:id/file")).click();
            Thread.sleep(1000);

            mUidevice.findObject(By.text("Open BigData")).click();
            Thread.sleep(2000);

            mUidevice.click(260,1100);
//            mUidevice.findObject(By.text("18454")).click();
            Thread.sleep(2000);

            mUidevice.click(360,1160);
//            mUidevice.findObject(By.text("18454_00049")).click();
            Thread.sleep(2000);

            if(i%6 == 0){
                mUidevice.findObject(By.text("2.ano")).click();
            }else if (i%6 == 1){
                mUidevice.findObject(By.text("3.ano")).click();
            }else if (i%6 == 2){
                mUidevice.findObject(By.text("4.ano")).click();
            }else if (i%6 == 3){
                mUidevice.findObject(By.text("5.ano")).click();
            }else if (i%6 == 4){
                mUidevice.findObject(By.text("6.ano")).click();
            }else if (i%6 == 5){
                mUidevice.findObject(By.text("1.ano")).click();
            }

            Thread.sleep(20000);
        }

        Thread.sleep(2000);

    }




    /*
    Test collaboration for Vivo
    */
    @Test
    public void VivoTest() throws RemoteException, InterruptedException {

        // start the app
        mUidevice.click(910,780);
        Thread.sleep(3000);

        //open the file
        mUidevice.findObject(By.res("com.penglab.hi5:id/file")).click();
        Thread.sleep(1000);


        mUidevice.findObject(By.text("Open BigData")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("18454")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("18454_00049")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("2.ano")).click();
        Thread.sleep(8000);


        // click the draw button
        mUidevice.click(100,360);
        Thread.sleep(3000);
        mUidevice.click(140,670);
        Thread.sleep(2000);
        mUidevice.click(750,520);
        Thread.sleep(2000);

        for (int i=0; i<3000; i++){

            Random ran = new Random(i);

            if (i % 3 == 0){
                // x 200 -> 800,  y 800 -> 1400
                int x1 = ran.nextInt(600) + 200;
                int x2 = ran.nextInt(600) + 200;
                int y1 = ran.nextInt(600) + 800;
                int y2 = ran.nextInt(600) + 800;

                mUidevice.swipe(x1,y1,x2,y2,10);
                Thread.sleep(5000);
            }


            // open the file
//            mUidevice.findObject(By.res("com.penglab.hi5:id/file")).click();
            mUidevice.click(750,150);
            Thread.sleep(1000);

//            mUidevice.findObject(By.text("Open BigData")).click();
            mUidevice.click(360,980);
            Thread.sleep(2000);

//            mUidevice.findObject(By.text("18454")).click();
            mUidevice.click(260,1100);
            Thread.sleep(2000);

//            mUidevice.findObject(By.text("18454_00049")).click();
            mUidevice.click(360,1160);
            Thread.sleep(2000);

            if(i%6 == 0){
//                mUidevice.findObject(By.text("2.ano")).click();
                mUidevice.click(270,830);

            }else if (i%6 == 1){
//                mUidevice.findObject(By.text("3.ano")).click();
                mUidevice.click(270,990);

            }else if (i%6 == 2){
//                mUidevice.findObject(By.text("4.ano")).click();
                mUidevice.click(270,1140);

            }else if (i%6 == 3){
//                mUidevice.findObject(By.text("5.ano")).click();
                mUidevice.click(270,1290);

            }else if (i%6 == 4){
//                mUidevice.findObject(By.text("6.ano")).click();
                mUidevice.click(270,1440);

            }else if (i%6 == 5){
//                mUidevice.findObject(By.text("1.ano")).click();
                mUidevice.click(270,690);

            }

            Thread.sleep(15000);
        }

        Thread.sleep(2000);

    }




    /*
    Test collaboration for MEIZU
     */
    @Test
    public void MeiZuTest() throws RemoteException, InterruptedException {

        // start the app
        mUidevice.click(410,540);
        Thread.sleep(3000);

        //open the file
        mUidevice.findObject(By.res("com.penglab.hi5:id/file")).click();
        Thread.sleep(1000);


        mUidevice.findObject(By.text("Open BigData")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("18455")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("18455_00004")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("1.ano")).click();
        Thread.sleep(8000);


        // click the draw button
        mUidevice.click(100,360);
        Thread.sleep(3000);
        mUidevice.click(140,700);
        Thread.sleep(2000);
        mUidevice.click(750,560);
        Thread.sleep(2000);

        for (int i=0; i<3000; i++){

            Random ran = new Random(i);

            if (i % 20 == 0) {
                // x 250 -> 900,  y 900 -> 1400
                int x1 = ran.nextInt(650) + 250;
                int x2 = ran.nextInt(650) + 250;
                int y1 = ran.nextInt(500) + 900;
                int y2 = ran.nextInt(500) + 900;

                mUidevice.swipe(x1, y1, x2, y2, 10);
                Thread.sleep(5000);
            }


            // open the file
            mUidevice.findObject(By.res("com.penglab.hi5:id/file")).click();
            Thread.sleep(1000);

            mUidevice.findObject(By.text("Open BigData")).click();
            Thread.sleep(2000);

            mUidevice.click(270,1220);
//            mUidevice.findObject(By.text("18454")).click();
            Thread.sleep(2000);

            mUidevice.click(350,1140);
//            mUidevice.findObject(By.text("18454_00049")).click();
            Thread.sleep(2000);

            if(i%6 == 0){
                mUidevice.findObject(By.text("2.ano")).click();
            }else if (i%6 == 1){
                mUidevice.findObject(By.text("3.ano")).click();
            }else if (i%6 == 2){
                mUidevice.findObject(By.text("4.ano")).click();
            }else if (i%6 == 3){
                mUidevice.findObject(By.text("5.ano")).click();
            }else if (i%6 == 4){
                mUidevice.findObject(By.text("6.ano")).click();
            }else if (i%6 == 5){
                mUidevice.findObject(By.text("1.ano")).click();
            }

            Thread.sleep(15000);
        }

        Thread.sleep(2000);

    }




    /*
    Test collaboration for Huawei
    */
    @Test
    public void HuaweiTest() throws RemoteException, InterruptedException {

        // start the app
        mUidevice.click(930,250);
        Thread.sleep(3000);

        //open the file
        mUidevice.findObject(By.res("com.penglab.hi5:id/file")).click();
        Thread.sleep(1000);


        mUidevice.findObject(By.text("Open BigData")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("18454")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("18454_00049")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("1")).click();
        Thread.sleep(8000);


        // click the draw button
        mUidevice.click(110,300);
        Thread.sleep(3000);
        mUidevice.click(150,680);
        Thread.sleep(2000);
        mUidevice.click(730,530);
        Thread.sleep(2000);

        for (int i=0; i<10000; i++){

//            //change pen blue
//            mUidevice.click(75,630);
//            Thread.sleep(2000);
//
            mUidevice.swipe(380,1060,650,1300,10);
            Thread.sleep(5000);


            // open the file
            mUidevice.findObject(By.res("com.penglab.hi5:id/file")).click();
            Thread.sleep(1000);

            mUidevice.findObject(By.text("Open BigData")).click();
            Thread.sleep(2000);

            mUidevice.click(260,1100);
//            mUidevice.findObject(By.text("18454")).click();
            Thread.sleep(2000);

            mUidevice.click(360,1160);
//            mUidevice.findObject(By.text("18454_00049")).click();
            Thread.sleep(2000);

            if(i%6 == 0){
                mUidevice.findObject(By.text("2")).click();
            }else if (i%6 == 1){
                mUidevice.findObject(By.text("3")).click();
            }else if (i%6 == 2){
                mUidevice.findObject(By.text("4")).click();
            }else if (i%6 == 3){
                mUidevice.findObject(By.text("5")).click();
            }else if (i%6 == 4){
                mUidevice.findObject(By.text("6")).click();
            }else if (i%6 == 5){
                mUidevice.findObject(By.text("1")).click();
            }

            Thread.sleep(10000);
        }

        Thread.sleep(2000);

    }




    /*
   Test for pre-construction  for OnePlus
    */
    @Test
    public void OnePlusTest() throws RemoteException, InterruptedException {

        // start the app
        mUidevice.click(185,1290);
        Thread.sleep(1000);

        //open the file
        mUidevice.findObject(By.res("com.example.core:id/file")).click();
        Thread.sleep(500);


        mUidevice.findObject(By.text("Open BigData")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("Select Block")).click();
        Thread.sleep(8000);

        // click the draw button
        mUidevice.click(120,460);
        Thread.sleep(3000);
        mUidevice.click(200,840);
        Thread.sleep(2000);
        mUidevice.click(830,650);
        Thread.sleep(2000);

        for (int i=0; i<1000; i++){

            //change pen blue
            mUidevice.click(50,720);
            Thread.sleep(2000);

            mUidevice.swipe(950,1250,320,1500,10);
            Thread.sleep(4000);

            //change pen red
            mUidevice.click(50,800);
            Thread.sleep(2000);

            mUidevice.swipe(950,1700,320,1950,10);
            Thread.sleep(4000);

            mUidevice.click(1360,810);
            Thread.sleep(8000);
//            Test_Unit();
        }
//        Test_for_PreConstruction();

//        Draw Curve


//        mUidevice.findObject(By.text("+")).click();

        Thread.sleep(2000);

    }


    /*
    Test for pre-construction  for SamSung
    */
    @Test
    public void SamSung() throws RemoteException, InterruptedException {

        // start the app
        mUidevice.click(660,280);
        Thread.sleep(1000);

        //open the file
        mUidevice.findObject(By.res("com.example.core:id/file")).click();
        Thread.sleep(500);


        mUidevice.findObject(By.text("Open BigData")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("Open RecentBlock")).click();
        Thread.sleep(8000);
//
//        // click the draw button
//        mUidevice.click(120,460);
//        Thread.sleep(3000);
//        mUidevice.click(200,840);
//        Thread.sleep(2000);
//        mUidevice.click(830,650);
//        Thread.sleep(2000);

        for (int i=0; i<10000; i++){

//            //change pen blue
//            mUidevice.click(50,720);
//            Thread.sleep(2000);
//
//            mUidevice.swipe(950,1250,320,1500,10);
//            Thread.sleep(4000);
//
//            //change pen red
//            mUidevice.click(50,800);
//            Thread.sleep(2000);
//
//            mUidevice.swipe(950,1700,320,1950,10);
//            Thread.sleep(4000);

            mUidevice.click(1000,760);
            Thread.sleep(8000);
//            Test_Unit();
        }
//        Test_for_PreConstruction();

//        Draw Curve


//        mUidevice.findObject(By.text("+")).click();

        Thread.sleep(2000);

    }



//        mUidevice.findObject(By.text("Open BigData")).click();
//        Thread.sleep(2000);
//
//        mUidevice.findObject(By.text("Select Block")).click();
//        Thread.sleep(6000);

    private void Test_Unit(){
        Log.v("Task","----------------------  start task  ----------------------");
        try {

            //change pen blue
            mUidevice.click(65,685);
            Thread.sleep(2000);

            mUidevice.drag(700,980,340,1200,10);
            Thread.sleep(4000);

            //change pen red
            mUidevice.click(65,780);
            Thread.sleep(2000);

            mUidevice.drag(700,1180,340,1400,10);
            Thread.sleep(4000);

            mUidevice.click(1000,770);
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void Test_for_PreConstruction() throws InterruptedException {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(4);
        ses.scheduleWithFixedDelay(new Task("fixed-delay"), 3, 15, TimeUnit.SECONDS);
    }


    class Task implements Runnable {
        private final String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Log.v("Task","----------------------  start task  ----------------------");
            try {
                mUidevice.click(65,685);
                Thread.sleep(2000);





                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.v("Task","----------------------  finish task  ----------------------");
        }
    }
}
