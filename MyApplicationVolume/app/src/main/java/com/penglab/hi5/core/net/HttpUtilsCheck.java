package com.penglab.hi5.core.net;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

/**
 * Created by Jackiexing on 12/25/21
 */
public class HttpUtilsCheck extends HttpUtils{

    private static final String URL_CHECK = "http://192.168.3.158:8000/check/sendcheckresult";

    public static void checkWithOkHttp(String username, String password, String brainId, String neuronId, String result, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", username)
                    .put("password", password)
                    .put("brainId", brainId)
                    .put("neuronId", neuronId)
                    .put("result", result)));
            asyncRequest(URL_CHECK, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
