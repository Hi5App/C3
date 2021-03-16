package com.example.myapplication__volume.collaboration.basic;

import android.content.Context;
import android.widget.Toast;

import com.example.datastore.database.Image;
import com.example.myapplication__volume.game.Score;

import org.litepal.LitePal;

import java.util.List;

public class ImageInfo {

    private static Score INSTANCE;

    private static Context mContext;

    public static void init(Context mContext){
        ImageInfo.mContext = mContext;
    }


    /**
     * 获取ImageInfo实例 ,单例模式
     */
    public static Score getInstance(){
        if (INSTANCE == null){
            synchronized (Score.class){
                if (INSTANCE == null){
                    INSTANCE = new Score();
                }
            }
        }
        return INSTANCE;
    }


    public int queryRes(String curPath){
        if (curPath != null){
            List<Image> images = LitePal.select("curPath", curPath).find(Image.class);
            if (images.size() == 0 ) {
                Toast.makeText(mContext, "Something wrong with database !", Toast.LENGTH_SHORT).show();
                return -1;
            }else {
                return -1;
            }
        }
        else{
            return -1;
        }
    }



    public boolean updateRes(String curPath, int curRes){
        return false;
    }



}
