package com.penglab.hi5.basic.utils;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import static com.penglab.hi5.core.MainActivity.getContext;

import com.penglab.hi5.data.model.img.FileType;
import com.penglab.hi5.data.model.img.FileTypeHelper;

import java.io.File;

public class FileManager {
    public static String TAG = "FileManager";

    public FileManager() {
    }

    public static String getFileName(Uri uri) {
        Cursor returnCursor =
                getContext().getContentResolver().query(uri, null, null, null, null);
        if (returnCursor != null) {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();

            String filename = returnCursor.getString(nameIndex);
            returnCursor.close();
            Log.d(TAG, "getFileName: " + filename);
            return filename;
        } else {
            // Fallback: try to get filename from the path
            String result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                    return result;
                }
            }

            // Last resort: use URI string
            return uri.toString().substring(uri.toString().lastIndexOf('/') + 1);
        }
    }

    public static String getFileType(Uri uri) {
        String fileType = "";

        try {
            Cursor returnCursor =
                    getContext().getContentResolver().query(uri, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            String fileName = returnCursor.getString(nameIndex);

            if (fileName == null) {
                int columnIndex = returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                Log.d(TAG, "getFileType: columnIndex " + columnIndex);
                returnCursor.moveToFirst();
                fileName = returnCursor.getString(columnIndex);
            }

            fileType = fileName.substring(fileName.lastIndexOf(".")).toUpperCase();
            returnCursor.close();
            Log.d(TAG, "getFileType: " + fileType);

        } catch (Exception e) {
            e.printStackTrace();
            return "Fail to read";
        }
        return fileType;
    }

    public static FileType getFileTypeUri(Uri uri) {
        FileType fileType = FileType.UNSUPPORTED; // 默认返回值
        Cursor returnCursor = null;

        try {
            returnCursor = getContext().getContentResolver().query(uri, null, null, null, null);

            // 如果returnCursor为null，尝试通过Uri的路径获取文件类型
            if (returnCursor == null) {
                Log.d(TAG, "getFileTypeUri: cursor is null, trying to get file type from uri path");
                String path = uri.getPath();
                if (path != null) {
                    String fileName = new File(path).getName();
                    fileType = FileTypeHelper.getType(fileName);
                    Log.d(TAG, "getFileTypeUri: file type from path: " + fileType);
                }
                return fileType;
            }

            if (returnCursor.moveToFirst()) {
                String fileName = null;

                // 尝试从DISPLAY_NAME获取文件名
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = returnCursor.getString(nameIndex);
                }

                // 如果DISPLAY_NAME没有获取到，尝试从MediaStore获取
                if (fileName == null || fileName.isEmpty()) {
                    try {
                        int columnIndex = returnCursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                        fileName = returnCursor.getString(columnIndex);
                        Log.d(TAG, "getFileType: columnIndex " + columnIndex);
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "MediaStore.Images.Media.TITLE column not found", e);
                    }
                }

                // 如果获取到文件名，则获取文件类型
                if (fileName != null && !fileName.isEmpty()) {
                    fileType = FileTypeHelper.getType(fileName);
                    Log.d(TAG, "getFileType: " + fileType);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file type from uri", e);
        } finally {
            if (returnCursor != null) {
                returnCursor.close();
            }
        }

        return fileType;
    }


    public static String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    public static FileType getFileType(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/"));
        return FileTypeHelper.getType(fileName);
    }

}
