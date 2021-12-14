package com.penglab.hi5.core.net;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtilsResource {

    private static final String URL_MUSIC = "http://192.168.3.158:8000/resource/getmusicres";
    private static final String URL_DOWNLOAD = "http://192.168.3.158:8000/download";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void getMusicListWithOkHttp(Callback callback){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()));
            Request request = new Request.Builder()
                    .url(URL_MUSIC)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);
            client.connectionPool().evictAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void downloadMusicWithOkHttp(String url, Callback callback){
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()));
            Request request = new Request.Builder()
                    .url(URL_DOWNLOAD + url)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(callback);
            client.connectionPool().evictAll();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
