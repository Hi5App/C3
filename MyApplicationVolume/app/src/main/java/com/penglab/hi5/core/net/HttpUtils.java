package com.penglab.hi5.core.net;

import com.penglab.hi5.R;

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

    protected static void asyncRequest(String url, RequestBody body, Callback callback) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
        client.connectionPool().evictAll();
    }
}
