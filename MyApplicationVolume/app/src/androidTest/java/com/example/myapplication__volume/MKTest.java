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

    @Test
    public void DemoTest() throws RemoteException, InterruptedException {

        // start the app
        mUidevice.click(928,1077);
        Thread.sleep(500);

        //open the file
        mUidevice.findObject(By.res("com.example.myapplication__volume:id/file")).click();
        Thread.sleep(500);


        mUidevice.findObject(By.text("Open BigData")).click();
        Thread.sleep(2000);

        mUidevice.findObject(By.text("Select Block")).click();
        Thread.sleep(4000);

        // click the draw button
        mUidevice.click(115,380);
        Thread.sleep(3000);
        mUidevice.click(140,720);
        Thread.sleep(2000);
        mUidevice.click(740,710);
        Thread.sleep(2000);




//        Draw Curve


//        mUidevice.findObject(By.text("+")).click();

        Thread.sleep(2000);

    }




    private void Test_for_PreConstruction(){
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





                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.v("Task","----------------------  finish task  ----------------------");
        }
    }
}
