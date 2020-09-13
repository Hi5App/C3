package com.example.myapplication__volume;

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
    Test for pre-construction for MEIZU
     */
    @Test
    public void DemoTest() throws RemoteException, InterruptedException {

        // start the app
        mUidevice.click(928,1077);
        Thread.sleep(1000);

        //open the file
        mUidevice.findObject(By.res("com.example.myapplication__volume:id/file")).click();
        Thread.sleep(500);


        mUidevice.findObject(By.text("Open BigData")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("Select Block")).click();
        Thread.sleep(8000);

        // click the draw button
        mUidevice.click(115,380);
        Thread.sleep(3000);
        mUidevice.click(140,720);
        Thread.sleep(2000);
        mUidevice.click(800,560);
        Thread.sleep(2000);

        for (int i=0; i<1000; i++){

            //change pen blue
            mUidevice.click(65,685);
            Thread.sleep(2000);

            mUidevice.swipe(700,980,340,1200,10);
            Thread.sleep(4000);

            //change pen red
            mUidevice.click(65,780);
            Thread.sleep(2000);

            mUidevice.swipe(700,1180,340,1400,10);
            Thread.sleep(4000);

            mUidevice.click(1000,770);
            Thread.sleep(8000);
//            Test_Unit();
        }
//        Test_for_PreConstruction();

//        Draw Curve


//        mUidevice.findObject(By.text("+")).click();

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
        mUidevice.findObject(By.res("com.example.myapplication__volume:id/file")).click();
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
