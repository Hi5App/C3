package com.penglab.hi5.core.net;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class HttpUtilsQualityInspection extends HttpUtils{
    private static final String URL_GET_ARBOR = SERVER_IP + "/arbor/getarbor";
    private static final String URL_UPDATE_CHECK_RESULT = SERVER_IP + "/arbor/updatearborresult";
    private static final String URL_GET_SWC = SERVER_IP + "swc/cropswc";

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
    public static void UpdateCheckResultWithOkHttp(JSONObject userInfo, int somaId, int somaType,JSONArray insertList, JSONArray deleteList, String owner, String image,Callback callback)
    {
        try {
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("user", userInfo)
                    .put("somaId", somaId)
                    .put("somaType",somaType)
                    .put("insertlist", insertList)
                    .put("deletelist", deleteList)
                    .put("owner", owner)
                    .put("image", image)));
            asyncPostRequest(URL_UPDATE_CHECK_RESULT, body, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSwcWithOkHttp(JSONObject userInfo,Callback callback)
    {
        try {
            RequestBody body = RequestBody.create(JSON,String.valueOf(new JSONObject()
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_SWC, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        
    




}
