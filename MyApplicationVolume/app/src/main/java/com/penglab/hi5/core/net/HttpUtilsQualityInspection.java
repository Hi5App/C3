package com.penglab.hi5.core.net;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class HttpUtilsQualityInspection extends HttpUtils{
    private static final String URL_GET_ARBOR = SERVER_IP + "/dynamic/arbor/getarbor";
    private static final String URL_UPDATE_CHECK_RESULT = SERVER_IP + "/dynamic/arbor/updatearborresult";
    private static final String URL_GET_SWC = SERVER_IP + "/dynamic/swc/cropswc";
    private static final String URL_GET_ARBOR_MARKER_LIST = SERVER_IP + "/dynamic/arbor/queryarborresult";

    public static void getArborWithOkHttp(JSONObject userInfo, Callback callback) 
    {
        try {
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_ARBOR, body, callback);
                    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getSwcWithOkHttp(JSONObject userInfo,JSONObject bBox,Callback callback)
    {
        try {
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("bb", bBox)
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_SWC, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UpdateCheckResultWithOkHttp(JSONObject userInfo, int arborId, int locationType,String arborName,JSONArray insertList, JSONArray deleteList, String owner, Callback callback)
    {
        try {
            JSONObject updateCheckInfo = new JSONObject()
                    .put("arborid", arborId)
                    .put("arborname",arborName)
                    .put("status",locationType)
                    .put("insertlist", insertList)
                    .put("deletelist", deleteList)
                    .put("owner", owner);
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("pa",updateCheckInfo)
                    .put("user",userInfo)));
            asyncPostRequest(URL_UPDATE_CHECK_RESULT, body, callback);
            Log.e("body","updatecheckbody"+String.valueOf(new JSONObject()
                    .put("arborid", arborId)
                    .put("arborname",arborName)
                    .put("status",locationType)
                    .put("insertlist", insertList)
                    .put("deletelist", deleteList)
                    .put("owner", owner)
                    .put("user",userInfo)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getArborMarkerListWithOkHttp(JSONObject userInfo,String arborName,Callback callback)
    {
        try{
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("user", userInfo)
                    .put("arborname",arborName)));
            asyncPostRequest(URL_GET_ARBOR_MARKER_LIST,body,callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        
    




}
