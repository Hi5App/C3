package com.penglab.hi5.core.net;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

/**
 * Created by Jackiexing on 06/24/21
 */
public class HttpUtilsCollaborate extends HttpUtils{
    private static final String URL_GET_IMAGES = SERVER_IP + "/release/collaborate/getanoimage";
    private static final String URL_GET_NEURONS = SERVER_IP + "/release/collaborate/getanoneuron";
    private static final String URL_GET_ANOS = SERVER_IP + "/release/collaborate/getano";
    private static final String URL_GET_AllSWCMETAINFO = DBMS_SERVER_IP + "/proto.DBMS/GetAllSwcMetaInfo";
    private static final String URL_LOAD_ANO = SERVER_IP + "/release/collaborate/inheritother";

    public static void getImageListWithOkHttp(JSONObject userInfo, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_IMAGES, body, callback);
            Log.e("body","getImageListCollaborate"+String.valueOf(new JSONObject()
                    .put("user",userInfo)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getNeuronsWithOkHttp(JSONObject userInfo,String brianList,Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("image", brianList)
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_NEURONS, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAnoWithOkHttp(JSONObject userInfo,String neuron,Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("neuron", neuron)
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_ANOS, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAllSwcMetaInfoWithOkHttp(JSONObject param, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(param));
            asyncPostRequest(URL_GET_AllSWCMETAINFO, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadAnoWithOkHttp(JSONObject userInfo,String brainList,String neuron,String ano,Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("image",brainList)
                    .put("neuron", neuron)
                    .put("ano",ano)
                    .put("user", userInfo)));
            asyncPostRequest(URL_LOAD_ANO, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






























}
