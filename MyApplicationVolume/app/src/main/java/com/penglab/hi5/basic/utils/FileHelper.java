package com.penglab.hi5.basic.utils;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.nfc.Tag;
import android.util.Log;

import com.penglab.hi5.core.Myapplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileHelper {

    private static final String TAG = "FileHelper";

    public static File createFile(String path, String name) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                ToastEasy("FileHelper: Fail to create dir !");
                return null;
            }
        }

        File file = new File(path + "/" + name);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return null;
            }
        }
        return file;
    }

    public static boolean storeFile(String path, String name, byte[] fileContent) throws IOException {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                ToastEasy("FileHelper: Fail to create dir !");
                return false;
            }
        }

        File file = new File(path + "/" + name);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return false;
            }
        }
        FileOutputStream out = new FileOutputStream(file);
        out.write(fileContent);
        out.close();
        return true;
    }

    public static File getDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                ToastEasy("FileHelper: Fail to create dir !");
            }
        }
        return dir;
    }

    public static boolean recursionDeleteFile(File file) {
        if (file.isFile()) {
            if (!file.delete()){
                return false;
            }
        }
        if (file.isDirectory()) {
            Log.v("RecursionDeleteFile","file.isDirectory()");
            File[] childFile = file.listFiles();
            Log.v("RecursionDeleteFile","childFile.length: " + childFile.length);
            if (childFile.length == 0) {
                if (!file.delete()){
                    return false;
                }
            }
            int count = 1;
            for (File f : childFile) {
                Log.v("RecursionDeleteFile","count: " + count++);
                recursionDeleteFile(f);
            }
            return file.delete();
        }
        return true;
    }
}
