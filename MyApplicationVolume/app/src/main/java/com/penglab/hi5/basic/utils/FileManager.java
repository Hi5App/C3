package com.penglab.hi5.basic.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import static com.penglab.hi5.core.MainActivity.getContext;

import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.data.model.img.FileTypeHelper;

public class FileManager {
    public static String TAG = "FileManager";
    public FileManager(){ }

    public static String getFileName(Uri uri){
        Cursor returnCursor =
                getContext().getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        String filename = returnCursor.getString(nameIndex);
        returnCursor.close();
        Log.d(TAG, "getFileName: " + filename);

        return filename;
    }

    public static String getFileType(Uri uri){
        String fileType = "";

        try {
            Cursor returnCursor =
                    getContext().getContentResolver().query(uri, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            String fileName = returnCursor.getString(nameIndex);

            if (fileName == null){
                int columnIndex = returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                Log.d(TAG, "getFileType: columnIndex " + columnIndex);
                returnCursor.moveToFirst();
                fileName = returnCursor.getString(columnIndex);
            }

            fileType= fileName.substring(fileName.lastIndexOf(".")).toUpperCase();
            returnCursor.close();
            Log.d(TAG, "getFileType: " + fileType);

        }catch (Exception e){
            e.printStackTrace();
            return "Fail to read";
        }
        return fileType;
    }

    public static FileType getFileTypeUri(Uri uri){
        FileType fileType;
        try {
            Cursor returnCursor =
                    getContext().getContentResolver().query(uri, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String fileName = returnCursor.getString(nameIndex);

            if (fileName == null){
                int columnIndex = returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                Log.d(TAG, "getFileType: columnIndex " + columnIndex);
                returnCursor.moveToFirst();
                fileName = returnCursor.getString(columnIndex);
            }

            fileType = FileTypeHelper.getType(fileName);
            returnCursor.close();
            Log.d(TAG, "getFileType: " + fileType);

        }catch (Exception e){
            e.printStackTrace();
            return FileType.UNSUPPORTED;
        }
        return fileType;
    }

    public static String getFileName(String filePath){
        return filePath.substring(filePath.lastIndexOf("/"));
    }

    public static FileType getFileType(String filePath){
        String fileName =  filePath.substring(filePath.lastIndexOf("/"));
        return FileTypeHelper.getType(fileName);
    }

}
