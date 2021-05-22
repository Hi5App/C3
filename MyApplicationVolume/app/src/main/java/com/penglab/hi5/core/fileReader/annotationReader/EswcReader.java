package com.penglab.hi5.core.fileReader.annotationReader;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class EswcReader {
    ArrayList<ArrayList<Float>> read(long length, InputStream is){
        String headstr = "#name vaa3d_traced_neuron\n#comment \n##n,type,x,y,z,radius,parent,seg_id,level,mode,timestamp,feature_value\n";
        int head_length = headstr.length();
        long filesize = length;
        ArrayList<ArrayList<Float>> result = new ArrayList<ArrayList<Float>>();
        ArrayList<String> arraylist = new ArrayList<String>();
        try{
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
            if (arraylist.size() < 3){
                throw new Exception("The number of columns is not correct");
            }
            int num = arraylist.size() - 3;
//            float [][] result = new float[num][11];
            for (int i = 0; i < num; i++){
                String current = arraylist.get(i+3);
                String [] s = current.split(" ");
                ArrayList<Float> cur_line = new ArrayList<Float>();
                for (int j = 0; j < 11; j++){
                    cur_line.add(Float.parseFloat(s[j]));
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
            Log.v("ReadESWCException", e.getMessage());
            return null;
        }
    }
}
