package com.penglab.hi5.core.collaboration.basic;

import android.content.Context;

import com.penglab.hi5.dataStore.database.Image;

import org.litepal.LitePal;

import java.util.List;

import static com.penglab.hi5.core.MainActivity.Toast_in_Thread_static;

public class ImageInfo {

    private static String TAG = "ImageInfo";

    private static ImageInfo INSTANCE;

    private static Context mContext;

    public static void init(Context mContext){
        ImageInfo.mContext = mContext;
    }


    /**
     * 获取ImageInfo实例 ,单例模式
     */
    public static ImageInfo getInstance(){
        if (INSTANCE == null){
            synchronized (ImageInfo.class){
                if (INSTANCE == null){
                    INSTANCE = new ImageInfo();
                }
            }
        }
        return INSTANCE;
    }

    public boolean queryCurPath(String curPath){
//        Log.e(TAG, "queryCurPath start !");

        List<Image> images = LitePal.where("curPath = ?", curPath).find(Image.class);
        if (images.size() == 1) return true;
        return false;
    }




    public int queryRes(String curPath){
//        Log.e(TAG, "queryRes start !");

        if (curPath != null){
            List<Image> images = LitePal.where("curPath = ?", curPath).find(Image.class);
            if (images.size() != 1) {
                Toast_in_Thread_static("Something wrong with database !");
                return -1;
            }else {
                return images.get(0).getCurRes();
            }
        }
        else{
            Toast_in_Thread_static("Make sure curPath not null !");
            return -1;
        }
    }


    public boolean updateRes(String curPath, int curRes){
//        Log.e(TAG, "updateRes start !");

        if (curPath != null){
            List<Image> images = LitePal.where("curPath = ?", curPath).find(Image.class);
            if (images.size() != 1) {
                Toast_in_Thread_static("Something wrong with database !");
                return false;
            }else {
                Image image = new Image();
                image.setCurRes(curRes);
                image.updateAll("curPath = ?", curPath);
                return true;
            }
        }
        else{
            Toast_in_Thread_static("Make sure curPath not null !");
            return false;
        }
    }



    public String queryPos(String curPath){
//        Log.e(TAG, "queryPos start !");

        if (curPath != null){
            List<Image> images = LitePal.where("curPath = ?", curPath).find(Image.class);
            if (images.size() != 1) {
                Toast_in_Thread_static("Something wrong with database !");
                return null;
            }else {
                return images.get(0).getCurPos();
            }
        }
        else{
            Toast_in_Thread_static("Make sure curPath not null !");
            return null;
        }
    }


    public boolean updatePos(String curPath, String curPos){
//        Log.e(TAG, "updatePos start : " + curPos);

        if (curPath != null){
            List<Image> images = LitePal.where("curPath = ?", curPath).find(Image.class);
            if (images.size() != 1) {
                Toast_in_Thread_static("Something wrong with database !");
                return false;
            }else {
                Image image = new Image();
                image.setCurPos(curPos);
                image.updateAll("curPath = ?", curPath);
                return true;
            }
        }
        else{
            Toast_in_Thread_static("Make sure curPath not null !");
            return false;
        }
    }



    public boolean updatePosRes(String curPath, int curRes, String curPos){
//        Log.e(TAG, "updatePosRes start : " + curRes + ", " + curPos);
        return updatePos(curPath, curPos) && updateRes(curPath, curRes);
    }


    public boolean initImgInfo(String curPath, int curRes, String curPos){
//        Log.e(TAG, "initImgInfo start !");

        List<Image> images = LitePal.where("curPath = ?", curPath).find(Image.class);
        if (images.size() != 0){
            Toast_in_Thread_static("cur Img already has a record!");
            return false;
        }else {
            Image image = new Image();
            image.setCurPath(curPath);
            image.setCurRes(curRes);
            image.setCurPos(curPos);
            image.save();
            return true;
        }

    }



}
