package com.penglab.hi5.core.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jackiexing on 12/15/21
 */
public class HttpUtils {

    protected static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //    protected static final String SERVER_IP = "http://192.168.3.79:8000";
    protected static final String SERVER_IP = "http://114.117.165.134:26000";
    protected static final String DBMS_SERVER_IP = "http://114.117.165.134:14252";

//    private static final String CONFIG_FILE_PATH = "D://C3//config.json";

//    public static String SERVER_IP;
//    public static String DBMS_SERVER_IP;


//    static {
//        try{
//            JSONObject config = new JSONObject((Map) new FileReader(CONFIG_FILE_PATH));
//            SERVER_IP = config.getString("braintellServerAddress");
//            DBMS_SERVER_IP = config.getString("dbmsServerAddress");
//
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public static final OkHttpClient client =
            new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

    protected static void asyncPostRequest(String url, RequestBody body, Callback callback) {
        Request request;
        if (body != null) {
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json"), "{}"))
                    .build();
        }

        // Add code to execute the request using OkHttpClient and the provided callback
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }

//    protected static void asyncPostRequest(String url, RequestBody body, Callback callback) {
//        Request request;
//        if (body != null) {
//            request = new Request.Builder()
//                    .url(url)
//                    .post(body)
//                    .build();
//        } else {
//            request = new Request.Builder()
//                    .url(url)
//                    .post(RequestBody.create(null, new byte[0]))
//                    .build();
//        }
//    }

    protected static Response syncPostRequest(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return client.newCall(request).execute();
    }

    protected static void asyncGetRequest(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }
}
