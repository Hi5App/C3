package com.example.basic;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class SettingFileManager {

    public SettingFileManager(){

    }

    public String getDownSampleMode(Context context){
        String DownSampleMode = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/DownSampleMode.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "DownSampleYes";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get DownSampleMode", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                DownSampleMode = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get DownSampleMode", DownSampleMode);
        return DownSampleMode;
    }


    public void setDownSampleMode(String DownSampleMode, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/DownSampleMode.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get DownSampleMode", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(DownSampleMode.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
