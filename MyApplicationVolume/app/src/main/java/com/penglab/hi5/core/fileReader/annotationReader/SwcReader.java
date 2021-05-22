package com.penglab.hi5.core.fileReader.annotationReader;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.penglab.hi5.core.MainActivity.getContext;

public class SwcReader {

    ArrayList<ArrayList<Float>> read(String filePath){

        ArrayList<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();
        ArrayList<String> arraylist = new ArrayList<String>();

        try{
            File f = new File(filePath);
            FileInputStream fid = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);
            String str;
            while ((str = br.readLine()) != null) {
                arraylist.add(str);
            }
            br.close();
            isr.close();
            for (int i = 0; i < arraylist.size() - 3; i++){
                String current = arraylist.get(i+3);
                String [] s = current.split(" ");
                ArrayList<Float> cur_line = new ArrayList<Float>();
                for (int j = 0; j < 7; j++){
                    cur_line.add(Float.parseFloat(s[j]));
                }
                result.add(cur_line);
            }
        }catch (Exception e){
            Log.v("SwcReaderException", e.getMessage());
        }
        return result;
    }


    ArrayList<ArrayList<Float>> read(long length, InputStream is){
        ArrayList<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();
        ArrayList<String> arraylist = new ArrayList<String>();
        try{
            FileInputStream fid = (FileInputStream)(is);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);
            String str;
            while ((str = br.readLine()) != null) {
                arraylist.add(str);
            }
            br.close();
            isr.close();
            for (int i = 0; i < arraylist.size() - 2; i++){
                String current = arraylist.get(i+2);
                String [] s = current.split(" ");
                ArrayList<Float> cur_line = new ArrayList<Float>();
                for (int j = 0; j < 7; j++){
                    cur_line.add(Float.parseFloat(s[j]));
//                    result[i][j] = Float.parseFloat(s[j]);
                }
                result.add(cur_line);
            }
        }catch (Exception e){
            Log.v("SwcReaderException", e.getMessage());
        }
        return result;
    }


    ArrayList<ArrayList<Float>> read(Uri uri){
        ArrayList<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();
        ArrayList<String> arraylist = new ArrayList<String>();

        Context context = getContext();

        try{

            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");

            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
            long filesize = (int)parcelFileDescriptor.getStatSize();

            Log.v("SwcReader", Long.toString(filesize));

            FileInputStream fid = (FileInputStream)(is);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);
            String str;
            while ((str = br.readLine()) != null) {
                arraylist.add(str);
            }
            br.close();
            isr.close();
            for (int i = 0; i < arraylist.size() - 3; i++){
                String current = arraylist.get(i+3);
                String [] s = current.split(" ");
                ArrayList<Float> cur_line = new ArrayList<Float>();
                for (int j = 0; j < 7; j++){
                    cur_line.add(Float.parseFloat(s[j]));
//                    result[i][j] = Float.parseFloat(s[j]);
                }
                result.add(cur_line);
            }
        }catch (Exception e){
            Log.v("SwcReaderException", e.getMessage());
        }
        return result;
    }

}
