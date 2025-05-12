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

import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.data.model.img.FilePath;

public class ApoReader {

    private static final String TAG = "ApoReader";

    public static MarkerList parse(FilePath<?> filePath){
        if (filePath == null){
            return null;
        }
        if (filePath.getData() instanceof Uri){
            Uri uri = (Uri) filePath.getData();
            return readFromUri(uri);
        } else if (filePath.getData() instanceof String){

        }
        return null;

    }
    public static MarkerList readFromUri(Uri uri) {
        // ##n, orderinfo,name,comment,z,x,y, pixmax,intensity,sdev,volsize,mass,,,, color_r,color_g,color_b
        MarkerList markerList = new MarkerList();
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContext().getContentResolver().openFileDescriptor(uri, "r");
            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            long fileSize = parcelFileDescriptor.getStatSize();

            // 文件头有多少行
            String headString = " ";
            int headLength = headString.length();
            if (fileSize < headLength){
                throw new Exception("The size of your input file is too small and is not correct, -- it is too small to contain the legal header.");
            }

            ArrayList<String> contentList = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.charAt(0) == '#'){
                    continue;
                }
                contentList.add(line);
            }
            br.close();
            is.close();

            if (contentList.size() <= 0){
                throw new Exception("The number of columns is not correct");
            }

            // 一共有多少行数据
            for (String curLine: contentList){
                markerList.add(ImageMarker.parse(curLine));
            }
            return markerList;
        } catch (Exception e){
            Log.e(TAG,"error " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<ArrayList<String>> readString(String filePath){

        // 文件头有多少行
        String headString = " ";
        int headLength = headString.length();

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> arraylist = new ArrayList<String>();
        try{

            File f = new File(filePath);
            FileInputStream fid = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);

            long filesize = f.length();
            Log.v("ApoReader", Long.toString(filesize));

            if (filesize < headLength){
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
//            float [][] result = new float[num][11];
            for (int i = 0; i < num; i++){
                String current = arraylist.get(i);
                if (current.startsWith("#")) continue;

                String [] s = current.split(",");
                ArrayList<String> cur_line = new ArrayList<>();
                for (int j = 0; j < 18; j++){
                    String cur_string = s[j];
                    if (j != 3){
                        cur_string = s[j].replace(" ","");
                    }
//                    if (cur_string.equals(""))
//                        cur_string = "1234";
                    cur_line.add(cur_string);
                }
                result.add(cur_line);
            }
            return result;
        }catch (Exception e){
            Log.v("ReadApoException", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<ArrayList<Float>> read(String filePath){

        // 文件头有多少行
        String headString = " ";
        int headLength = headString.length();

        ArrayList<ArrayList<Float>> result = new ArrayList<>();
        ArrayList<String> arraylist = new ArrayList<String>();
        try{

            File f = new File(filePath);
            FileInputStream fid = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);

            long filesize = f.length();
            Log.v("ApoReader", Long.toString(filesize));

            if (filesize < headLength){
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
//            float [][] result = new float[num][11];
            for (int i = 0; i < num; i++){
                String current = arraylist.get(i);
                if (current.startsWith("#")) continue;

                String [] s = current.split(",");
                ArrayList<Float> cur_line = new ArrayList<>();
                for (int j = 0; j < 18; j++){
                    String cur_string = s[j].replace(" ","");
                    if (cur_string.equals(""))
                        cur_string = "1234";
                    cur_line.add(Float.parseFloat(cur_string));
                }
                result.add(cur_line);
            }
            return result;
        }catch (Exception e){
            Log.v("ReadApoException", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public ArrayList<ArrayList<Float>> read(Uri uri){

        Context context = getContext();

        //文件头有多少行
        String headstr = " ";
        int head_length = headstr.length();

        ArrayList<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();
        ArrayList<String> arraylist = new ArrayList<String>();
        try{
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");

            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
            long filesize = (int)parcelFileDescriptor.getStatSize();

            Log.v("ApoReader", Long.toString(filesize));



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
//            float [][] result = new float[num][11];
            for (int i = 0; i < num; i++){
                String current = arraylist.get(i);
                if (current.substring(0, 1).equals("#")) continue;

                String [] s = current.split(",");
                ArrayList<Float> cur_line = new ArrayList<Float>();
                for (int j = 0; j < 18; j++){
                    String cur_string = s[j].replace(" ","");
                    if (cur_string.equals(""))
                        cur_string = "1234";
                    cur_line.add(Float.parseFloat(cur_string));
                }
                result.add(cur_line);
            }
            return result;
        }catch (Exception e){
            Log.v("ReadApoException", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



    ArrayList<ArrayList<Float>> read(long length, InputStream is){

        //文件头有多少行
//        String headstr = "#name vaa3d_traced_neuron\n#comment \n##n,type,x,y,z,radius,parent,seg_id,level,mode,timestamp,feature_value\n";
        String headstr = " ";
        int head_length = headstr.length();
        long filesize = length;

        ArrayList<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();
        ArrayList<String> arraylist = new ArrayList<String>();
        try{

            Log.v("ApoReader", Long.toString(filesize));
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
//            float [][] result = new float[num][11];
            for (int i = 0; i < num; i++){
                String current = arraylist.get(i);
                String [] s = current.split(",");
                ArrayList<Float> cur_line = new ArrayList<Float>();
                for (int j = 0; j < 12; j++){
                    String cur_string = s[j].replace(" ","");
                    if (cur_string.equals(""))
                        cur_string = "1234";
                    cur_line.add(Float.parseFloat(cur_string));
//                    cur_line.add(Float.parseFloat("111"));
//                    result[i][j] = Float.parseFloat(s[j]);
                }
                result.add(cur_line);
            }
            return result;
//            byte [] by = new byte[head_length];
//            long preread = fid.read(by);
//            if (preread != head_length){
//                throw new Exception("File unrecognized or corrupted file.");
//            }
//            String prestr = new String(by);
//            if (!prestr.equals(headstr)){
//                throw new Exception("Unrecognized file format.");
//            }

        }catch (Exception e){
            Log.v("Exception of ReadApo", e.getMessage());
            return null;
        }
    }


}
