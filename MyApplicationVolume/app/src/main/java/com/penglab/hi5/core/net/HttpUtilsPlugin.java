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
            Log.e("body","dopluginList"+String.valueOf(new JSONObject()
                    .put("cmdname", "")
                    .put("args","/home/BrainTellServer/PluginData/"+ imageName)
                    .put("user",userInfo)));
            asyncPostRequest(URL_GETIMAGE, body, callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    public static void getNeuronListWithOkHttp(String username, String password, String brain_id, Callback callback) {
//        try {
//            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
//                    .put("name", username)
//                    .put("password", password)
//                    .put("brain_id", brain_id)));
//            asyncPostRequest(URL_GET_NEURON_LIST, body, callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void getAnoListWithOkHttp(String username, String password, String neuron_id, Callback callback) {
//        try {
//            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
//                    .put("name", username)
//                    .put("password", password)
//                    .put("neuron_id", neuron_id)));
//            asyncPostRequest(URL_GET_ANO_LIST, body, callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * download image block
//     * @param userInfo username & password
//     * @param bBox info of Bounding Box, include startPos, endPos, resolution, brainId
//     * @param callback the callback func
//     */
//    public static void downloadImageWithOkHttp(JSONObject userInfo, JSONObject bBox, Callback callback) {
//        try {
//            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
//                    .put("bb", bBox)
//                    .put("user", userInfo)));
//            asyncPostRequest(URL_DOWNLOAD_IMAGE, body, callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * download image block
//     * @param username username
//     * @param password password
//     * @param swc such as 18454/18454_01130/18454_01130_DAH_YLL_SYY_stamp_2021_12_29_17_45/18454_01130_DAH_YLL_SYY_stamp_2021_12_29_17_45.ano.eswc
//     * @param res such as RES(26298x35000x11041)"
//     * @param x offset of axis x
//     * @param y offset of axis y
//     * @param z offset of axis z
//     * @param len size of image block
//     * @param callback the callback func
//     */
//    public static void getBBSwcWithOkHttp(String username, String password, String swc, int res, int x, int y, int z, int len, Callback callback) {
//        try {
//            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
//                    .put("name", username)
//                    .put("password", password)
//                    .put("swc", swc)
//                    .put("x", x)
//                    .put("y", y)
//                    .put("z", z)
//                    .put("len", len)
//                    .put("res", res)));
//            asyncPostRequest(URL_GET_BBSWC, body, callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void downloadSomaBlockWithOkHttp(String username, String password, String swc, int res, int x, int y, int z, int len, Callback callback) {
//        try {
//            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
//                    .put("name", username)
//                    .put("password", password)
//                    .put("swc", swc)
//                    .put("x", x)
//                    .put("y", y)
//                    .put("z", z)
//                    .put("len", len)
//                    .put("res", res)));
//            asyncPostRequest(URL_GET_BBSWC, body, callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void downloadButtonImageWithOkHttp(JSONObject User,String ArborId, Callback callback) {
//        try {
//            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
//                    .put("arborId",ArborId )
//                    .put("user", User)));
//            asyncPostRequest(URL_GET_ARBOR_IMAGE, body, callback);
//            Log.e("body","getdownloadButtonImage"+String.valueOf(new JSONObject()
//                    .put("user",User).put("arborId",ArborId)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
