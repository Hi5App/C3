package com.penglab.hi5.core.net;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtilsResource extends HttpUtils {
    private static final String URL_MUSIC = SERVER_IP + "/dynamic/musics";
    private static final String URL_DOWNLOAD = SERVER_IP + "/static/music";

    public static void getMusicListWithOkHttp(Callback callback) {
        try {
            // empty body
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()));
            asyncRequest(URL_MUSIC, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadMusicWithOkHttp(String musicName, Callback callback) {
        try {
            // url: ""
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()));
            asyncRequest(URL_DOWNLOAD + "/" + musicName, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
