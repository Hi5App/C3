package com.penglab.hi5.core.net;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

/**
 * Created by Jackiexing on 12/25/21
 */
public class HttpUtilsCheck extends HttpUtils{

    private static final String URL_CHECK = "http://192.168.3.158:8080/check/insertcheckresult";

    public static void checkWithOkHttp(String arborname, String username, String password, String owner, int result, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("arborname", arborname)
                    .put("name", username)
                    .put("password", password)
                    .put("owner", owner)
                    .put("result", result)));
            asyncRequest(URL_CHECK, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
