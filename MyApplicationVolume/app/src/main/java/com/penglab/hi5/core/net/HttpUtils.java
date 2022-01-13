package com.penglab.hi5.core.net;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Jackiexing on 12/15/21
 */
public class HttpUtils {

    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final String SERVER_IP = "http://192.168.3.158:8000";
//    protected static final String SERVER_IP = "http://139.155.28.154:26000";
    private static final OkHttpClient client =
            new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();;

    protected static void asyncRequest(String url, RequestBody body, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
