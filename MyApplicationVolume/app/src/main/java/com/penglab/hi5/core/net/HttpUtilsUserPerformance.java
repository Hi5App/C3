package com.penglab.hi5.core.net;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

/**
 * Created by Yihang zhu 01/20/21
 */
public class HttpUtilsUserPerformance extends HttpUtils{
    private static String URL_SOMA_COUNT = SERVER_IP + "/dynamic/user/getuserperformance";
    private static String URL_SOMA_COUNT_TOP_K = SERVER_IP + "/dynamic/user/getuserperformancetopk";

    public static void getUserPerformance(String account, String password, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", account)
                    .put("passwd", password)
                    .put("limit", 1)));
            asyncRequest(URL_SOMA_COUNT, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getUserPerformanceTopK(String account, String password, int k, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", account)
                    .put("passwd", password)
                    .put("k", k)
                    .put("limit", 1)));
            asyncRequest(URL_SOMA_COUNT, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
