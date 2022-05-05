package com.penglab.hi5.core.net;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

/**
 * Created by Yihang zhu 01/20/21
 */
public class HttpUtilsUserPerformance extends HttpUtils{
    private static String URL_SOMA_AND_CHECK_COUNT = SERVER_IP + "/dynamic/user/getuserperformance";
    private static String URL_SOMA_COUNT_TOP_K = SERVER_IP + "/dynamic/user/getuserperformancetopk";

    public static void getUserPerformance(JSONObject userInfo, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("user", userInfo)));
            asyncPostRequest(URL_SOMA_AND_CHECK_COUNT, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getUserPerformanceTopK(JSONObject userInfo, int k, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("user", userInfo)
                    .put("k", k)
                    .put("limit", 1)));
            asyncPostRequest(URL_SOMA_COUNT_TOP_K, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
