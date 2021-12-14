package com.penglab.hi5.basic.utils;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.nfc.Tag;

import com.penglab.hi5.core.Myapplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileHelper {

    private static final String TAG = "FileHelper";

    public static boolean storeFile(String path, String name, byte[] fileContent) throws IOException {
        File dir = new File(Myapplication.getContext().getExternalFilesDir(null) + path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                ToastEasy("FileHelper: Fail to create dir !");
                return false;
            }
        } else {
            File file = new File(path + name);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return false;
                }
            }
            FileOutputStream out = new FileOutputStream(file);
            out.write(fileContent);
            out.close();
        }
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
}
