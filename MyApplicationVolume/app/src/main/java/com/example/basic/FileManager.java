package com.example.basic;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.example.myapplication__volume.MainActivity.getContext;

public class FileManager {

    public FileManager(){

    }

    public String getFileType(Uri uri){
        String filetype = null;
        Context context = getContext();
        String file_head;

        String v3d_head = "raw_image_stack_by_hpeng";
        int length_v3d = v3d_head.length();


        try {

            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");

            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
            FileInputStream fid = (FileInputStream) (is);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);
            file_head = br.readLine();
//            System.out.println(file_head.substring(0, length_v3d));
            System.out.println(file_head);
//
//            file_head.substring(0, length_v3d);

            if (file_head.substring(0, 5).equals("#name")){

                filetype = ".SWC";

            }else if (file_head.substring(0, 7).equals("APOFILE")){

                filetype = ".ANO";

            }else if (file_head.substring(0, length_v3d).equals("raw_image_stack_by_hpeng")){

                filetype = ".V3DRAW";

            }else {

                filetype = "no such type";

            }

            br.close();
            isr.close();
        } catch (Exception e) {
            System.out.println("getFileType" + e.getMessage());
            return "fail to read file";
        }

        Log.v("filetype: ", filetype);

        return filetype;
    }

}
