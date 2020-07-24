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


    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static String getFilename_Local(Context context){
        String filename = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Local_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get filename", "Fail to create file");
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
                filename = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get filename", filename);
        return filename;
    }

    /**
     * put the ip address you input to local file
     * @param filename the ip address currently input
     */
    public static void setFilename_Local(String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Local_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set local filename", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(filename.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getoffset_Local(Context context, String filename){
        String offset = null;

//        filename = filename.split("RES")[1];
//        String offset_x = filename.split("x")[0];
//        String offset_y = filename.split("x")[1];
//        String offset_z = filename.split("x")[2];
//
//        int offset_x_i = Integer.parseInt(offset_x)/2;
//        int offset_y_i = Integer.parseInt(offset_y)/2;
//        int offset_z_i = Integer.parseInt(offset_z)/2;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/" + filename + "_Local.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "0" + "_" + "0" + "_" + "0" + "_128";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
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
                offset = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get offset", offset);
        return offset;
    }

    /**
     * put the offset you input to local file
     * @param offset the offset currently input
     */
    public static void setoffset_Local(String offset, String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/" + filename + "_Local.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(offset.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static String getUserAccount(Context context){
        String userAccount = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Chat_userAccount.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get Chat_userAccount", "Fail to create file");
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
                userAccount = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get Chat_userAccount", userAccount);
        return userAccount;
    }

    /**
     * put the ip address you input to local file
     * @param userAccount the ip address currently input
     */
    public static void setUserAccount(String userAccount, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Chat_userAccount.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set local filename", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(userAccount.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static String getFilename_Remote(Context context){
        String filename = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Remote_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get filename", "Fail to create file");
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
                filename = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get filename", filename);
        return filename;
    }

    /**
     * put the ip address you input to local file
     * @param filename the ip address currently input
     */
    public static void setFilename_Remote(String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Remote_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set filename", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(filename.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getoffset_Remote(Context context, String filename){
        String offset = null;

        String offset_x = filename.split("x")[3];
        String offset_y = filename.split("x")[4];
        String offset_z = filename.split("x")[5];

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = offset_x + "_" + offset_y + "_" + offset_z + "_128";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
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
                offset = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get offset", offset);
        return offset;
    }

    /**
     * put the offset you input to local file
     * @param offset the offset currently input
     */
    public static void setoffset_Remote(String offset, String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(offset.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
