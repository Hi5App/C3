package com.penglab.hi5.core.fileReader.annotationReader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.penglab.hi5.core.MainActivity.getContext;

public class AnoReader {
    ArrayList<ArrayList<Float>> apo_result;
    ArrayList<ArrayList<Float>> swc_result;

    Uri apo_uri;
    Uri swc_uri;

    String apo_path;
    String swc_path;

    Context context;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    public void read(Uri uri){

        //文件头有多少行
        String headstr = "";
        int head_length = headstr.length();

        context = getContext();
        ArrayList<String> arraylist = new ArrayList<String>();


        try{

            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");

            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
            long filesize = (int)parcelFileDescriptor.getStatSize();

            Log.v("AnoReader", Long.toString(filesize));

            FileInputStream fid = (FileInputStream)(is);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);
            if (filesize < head_length){
                throw new Exception("The size of your input file is too small and is not correct, -- it is too small to contain the legal header.");
            }
            String str;
            while ((str = br.readLine()) != null) {
                arraylist.add(str);
            }
            br.close();
            isr.close();
            if (arraylist.size() < 0){
                throw new Exception("The number of columns is not correct");
            }

            //一共有多少行数据
            int num = arraylist.size();
            Log.v("AnoReader lines ", Integer.toString(num));
            for (int i = 0; i < num; i++){
                String current = arraylist.get(i);
                String [] s = current.split("=");
                ArrayList<Float> cur_line = new ArrayList<Float>();

                Log.v("AnoReader", s[1]);
                String filetype = s[1].substring(s[1].lastIndexOf("."));

                switch(filetype){
                    case ".apo":
                        apo_path = getFilePath(uri, s[1]);
                        Log.v("AnoReader apo_filepath", apo_path);
                        break;
                    case ".eswc":
                        swc_path = getFilePath(uri, s[1]);
                        Log.v("AnoReader swc_filepath", swc_path);
                        break;
                }
            }

        }catch (Exception e){
            Log.v("ReadAnoException", e.getMessage());
        }

    }

    private Uri getUri(Uri uri, String filename){
        String uri_string = uri.toString();
        int index = uri_string.lastIndexOf("/");

        String filepath = uri_string.substring(0, index+1) + filename;
        Uri result = Uri.parse((String) filepath);

        return result;
    }

    private String getFilePath(Uri uri, String filename){
        String filepath = uri.getPath();
        String path_result = "";

        //Xiao mi
        if(filepath.contains("/external_files/")) {
            String path_old = filepath.replace("/external_files/", "/storage/emulated/0/");

            int index = path_old.lastIndexOf("/");
            path_result = path_old.substring(0, index+1) + filename;
        }

        else if(filepath.contains("/storage")){

            int index_per = filepath.indexOf("/storage");
            int index_post = filepath.lastIndexOf("/");
            path_result = filepath.substring(index_per, index_post+1) + filename;
        }

        else {
            path_result = "/storage/emulated/0/Download/" + filename;
        }

        return path_result;
    }



    public void callApoReader(Uri uri){

        context.grantUriPermission(context.getPackageName(), uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try{
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
            long filesize = (int)parcelFileDescriptor.getStatSize();

            ApoReader apoReader = new ApoReader();
            apo_result = apoReader.read(filesize, is);

        }catch (Exception e){
            Log.v("ReadAnoException", e.getMessage());
        }
    }

    public void callSwcReader(Uri uri){

        context.grantUriPermission(context.getPackageName(), uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try{
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
            long filesize = (int)parcelFileDescriptor.getStatSize();

            ApoReader apoReader = new ApoReader();
            swc_result = apoReader.read(filesize, is);

        }catch (Exception e){
            Log.v("ReadAnoException", e.getMessage());
        }
    }

    public String getApo_Path(){
        return apo_path;
    }

    public String getSwc_Path(){
        return swc_path;
    }

}
