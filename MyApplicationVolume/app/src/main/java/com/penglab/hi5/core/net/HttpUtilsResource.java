package com.penglab.hi5.core.net;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class HttpUtilsResource extends HttpUtils {
    private static final String URL_MUSIC = SERVER_IP + "/release/musics";
    private static final String URL_DOWNLOAD = SERVER_IP + "/static/music";
    private static final String URL_CHECK_LATEST_VERSION = SERVER_IP + "/release/updateapk";

    public static void getMusicListWithOkHttp(Callback callback) {
        try {
            // empty body
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()));
            asyncPostRequest(URL_MUSIC, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadMusicWithOkHttp(String musicName, Callback callback) {
        try {
            asyncGetRequest(URL_DOWNLOAD + "/" + musicName, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkLatestVersionWithOkHttp(String localVersionName, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject().put("version", localVersionName)));
            asyncPostRequest(URL_CHECK_LATEST_VERSION, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadLatestVersionWithOkHttp(String url, Callback callback) {
        try {
            asyncGetRequest(SERVER_IP + url, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
