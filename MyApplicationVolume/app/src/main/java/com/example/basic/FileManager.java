package com.example.basic;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import static com.example.myapplication__volume.MainActivity.getContext;

public class FileManager {

    public FileManager(){

    }

    public String getFileName(Uri uri){
        String filetype = null;
        String v3d_head = "raw_image_stack_by_hpeng";
        int length_v3d = v3d_head.length();

        Context context = getContext();

        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String filename = returnCursor.getString(nameIndex);

        System.out.println("getFileType:  " + filename);

        return filename;

//        try {
//
//            ParcelFileDescriptor parcelFileDescriptor =
//                    context.getContentResolver().openFileDescriptor(uri, "r");
//
//            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//            FileInputStream fid = (FileInputStream) (is);
//            InputStreamReader isr = new InputStreamReader(fid);
//            BufferedReader br = new BufferedReader(isr);
//            file_head = br.readLine();
////            System.out.println(file_head.substring(0, length_v3d));
//            System.out.println(file_head);
////
////            file_head.substring(0, length_v3d);
//
//            if (file_head.substring(0, 5).equals("#name")){
//
//                filetype = ".SWC";
//
//            }else if (file_head.substring(0, 7).equals("APOFILE")){
//
//                filetype = ".ANO";
//
//            }else if (file_head.substring(0, length_v3d).equals("raw_image_stack_by_hpeng")){
//
//                filetype = ".V3DRAW";
//
//            }else {
//
//                filetype = "no such type";
//
//            }
//
//            br.close();
//            isr.close();
//        } catch (Exception e) {
//            System.out.println("getFileType" + e.getMessage());
//            return "fail to read file";
//        }
//
//        Log.v("filetype: ", filetype);
//
//        return filetype;
    }

    public String getFileType(Uri uri){
        String v3d_head = "raw_image_stack_by_hpeng";
        int length_v3d = v3d_head.length();

        Context context = getContext();

        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String filename = returnCursor.getString(nameIndex);
        String filetype = filename.substring(filename.lastIndexOf(".")).toUpperCase();

        return filetype;

//        try {
//
//            ParcelFileDescriptor parcelFileDescriptor =
//                    context.getContentResolver().openFileDescriptor(uri, "r");
//
//            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//            FileInputStream fid = (FileInputStream) (is);
//            InputStreamReader isr = new InputStreamReader(fid);
//            BufferedReader br = new BufferedReader(isr);
//            file_head = br.readLine();
////            System.out.println(file_head.substring(0, length_v3d));
//            System.out.println(file_head);
////
////            file_head.substring(0, length_v3d);
//
//            if (file_head.substring(0, 5).equals("#name")){
//
//                filetype = ".SWC";
//
//            }else if (file_head.substring(0, 7).equals("APOFILE")){
//
//                filetype = ".ANO";
//
//            }else if (file_head.substring(0, length_v3d).equals("raw_image_stack_by_hpeng")){
//
//                filetype = ".V3DRAW";
//
//            }else {
//
//                filetype = "no such type";
//
//            }
//
//            br.close();
//            isr.close();
//        } catch (Exception e) {
//            System.out.println("getFileType" + e.getMessage());
//            return "fail to read file";
//        }
//
//        Log.v("filetype: ", filetype);
//
//        return filetype;
    }

}
