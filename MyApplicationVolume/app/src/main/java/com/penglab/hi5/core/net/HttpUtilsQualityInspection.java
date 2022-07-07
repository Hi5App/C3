package com.penglab.hi5.core.net;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class HttpUtilsQualityInspection extends HttpUtils{
    private static final String URL_GET_ARBOR = SERVER_IP + "/dynamic/arbor/getarbor";
    private static final String URL_QUERY_ARBOR_RESULT = SERVER_IP + "/dynamic/arbor/queryarborresult";
    private static final String URL_UPDATE_ARBOR_RESULT = SERVER_IP + "/dynamic/arbor/updatearborresult";
    private static final String URL_GET_SWC = SERVER_IP + "/dynamic/swc/cropswc";
    private static final String URL_GET_ARBOR_MARKER_LIST = SERVER_IP + "/dynamic/arbor/queryarborresult";
    private static final String URL_QUERY_ARBOR_MARKER_LIST = SERVER_IP + "/dynamic/arbordetail/query";
    private static final String URL_INSERT_ARBOR_MARKER_LIST = SERVER_IP + "/dynamic/arbordetail/insert";
    private static final String URL_DELETE_ARBOR_MARKER_LIST = SERVER_IP + "/dynamic/arbordetail/delete";

    public static void getArborWithOkHttp(JSONObject userInfo, int maxId,Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("MaxId",maxId)
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_ARBOR, body, callback);
            Log.e("body","updatecheckbody"+String.valueOf(new JSONObject()
                    .put("MaxId", maxId)
                    .put("user",userInfo)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void queryArborResultWithOkHttp(JSONObject userInfo, int arborId, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("arborId", arborId)
                    .put("user", userInfo)));
            asyncPostRequest(URL_QUERY_ARBOR_RESULT, body, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSwcWithOkHttp(JSONObject userInfo,JSONObject bBox,Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("bb", bBox)
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_SWC, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSingleArborResultWithOkHttp(JSONObject userInfo, int arborId, int result, String username, Callback callback) {
        try {
            JSONObject arborResult = new JSONObject()
                    .put("owner", username)
                    .put("arborid", arborId)
                    .put("result", result);
            JSONArray insertList = new JSONArray()
                    .put(arborResult);
            updateArborResultWithOkHttp(userInfo, insertList, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateArborResultWithOkHttp(JSONObject userInfo, JSONArray insertList, Callback callback) {
        try {
            JSONObject updateArborResultParam = new JSONObject()
                    .put("insertlist", insertList);
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("pa", updateArborResultParam)
                    .put("user", userInfo)));
            asyncPostRequest(URL_UPDATE_ARBOR_RESULT, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void UpdateCheckResultWithOkHttp(JSONObject userInfo, int arborId, int locationType, String arborName, JSONArray insertList, JSONArray deleteList, String owner, Callback callback) {
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
            asyncPostRequest(URL_UPDATE_ARBOR_RESULT, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getArborMarkerListWithOkHttp(JSONObject userInfo,String arborName,Callback callback) {
        try{
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("user", userInfo)
                    .put("arborname",arborName)));
            asyncPostRequest(URL_GET_ARBOR_MARKER_LIST,body,callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertArborMarkerListWithOkHttp(JSONObject userInfo, JSONArray insertList, Callback callback) {
        try{
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("user", userInfo)
                    .put("pa", insertList)));
            asyncPostRequest(URL_INSERT_ARBOR_MARKER_LIST,body,callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteArborMarkerListWithOkHttp(JSONObject userInfo, JSONArray deleteList, Callback callback) {
        try{
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("user", userInfo)
                    .put("pa", deleteList)));
            asyncPostRequest(URL_DELETE_ARBOR_MARKER_LIST, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void queryArborMarkerListWithOkHttp(JSONObject userInfo, int arborId, Callback callback) {
        try{
            JSONObject arborDetail = new JSONObject().put("arborId", arborId);
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("user", userInfo)
                    .put("pa", arborDetail)));
            asyncPostRequest(URL_QUERY_ARBOR_MARKER_LIST, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
