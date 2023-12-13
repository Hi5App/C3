package com.penglab.hi5.core.net;

import static com.penglab.hi5.chat.nim.main.helper.MessageHelper.TAG;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class HttpUtilsPlugin extends HttpUtils {
    private static final String URL_GET_PlUGLIN_LIST = SERVER_IP + "/dynamic/image_processing/getPluginList";
//    private static final String URL_PLUGIN = SERVER_IP + "/dynamic/image_processing/convertimg";
    private static final String URL_GET_IMAGE_LIST = SERVER_IP + "/dynamic/image_processing/getImageProcessingList";

    private static final String URL_PLUGIN = SERVER_IP +"/dynamic/image_processing/commandline";

    private static final String URL_GETIMAGE = SERVER_IP+"/dynamic/image_processing/getImage";

    private static final String URL_GETMODEL = SERVER_IP+"/dynamic/image_processing/callModel";


    public static void getPluginListWithOkHttp(JSONObject userInfo, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_PlUGLIN_LIST, body, callback);
            Log.e("body",String.valueOf(new JSONObject()
                    .put("user",userInfo)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getImageListWithOkHttp(JSONObject userInfo, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_IMAGE_LIST, body, callback);
            Log.e("imagelist",String.valueOf(new JSONObject().put("user",userInfo)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doPluginListWithOkHttp(JSONObject userInfo, String imageName,String pluginName,Callback callback) {
        try {
            Log.e("imageName",imageName);
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("cmdname",pluginName)
                    .put("args","/home/BrainTellServer/PluginData/"+ imageName)
                    .put("user", userInfo)));
            Log.e("body","dopluginList"+String.valueOf(new JSONObject()
                    .put("cmdname", pluginName)
                            .put("args","/home/BrainTellServer/PluginData/"+ imageName)
                    .put("user",userInfo)));
            asyncPostRequest(URL_PLUGIN, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getOriginImageWithOkHttp(JSONObject userInfo, String imageName,Callback callback) {
        try{
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("cmdname","")
                    .put("args","/home/BrainTellServer/PluginData/"+ imageName)
                    .put("user", userInfo)));
            Log.e("body","getOriginImage"+String.valueOf(new JSONObject()
                    .put("cmdname", "")
                    .put("args","/home/BrainTellServer/PluginData/"+ imageName)
                    .put("user",userInfo)));
            asyncPostRequest(URL_GETIMAGE, body, callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void doModelWithOkHttp(JSONObject userInfo, String imageName,Callback callback) {
        try {
            Log.e("imageName", imageName);
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("args", "/home/BrainTellServer/PluginData/" + imageName)
                    .put("user", userInfo)));
            Log.e("body", "doModel" + String.valueOf(new JSONObject()
                    .put("args", "/home/BrainTellServer/PluginData/" + imageName)
                    .put("user", userInfo)));
            asyncPostRequest(URL_PLUGIN, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    }

