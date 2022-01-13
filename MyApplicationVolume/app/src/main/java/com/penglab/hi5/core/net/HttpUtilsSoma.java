package com.penglab.hi5.core.net;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

/**
 * Created by Jackiexing on 01/11/21
 */
public class HttpUtilsSoma extends HttpUtils {
    private static final String URL_GET_POTENTIAL_LOCATION = SERVER_IP + "/soma/getpotentiallocation";
    private static final String URL_GET_SOMA_LIST = SERVER_IP + "/soma/getsomalist";
    private static final String URL_INSERT_SOMA_LIST = SERVER_IP + "/soma/insertsomalist";

    public static void getPotentialLocationWithOkHttp(String username, String password, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                .put("name", username)
                .put("passwd", password)));
            asyncRequest(URL_GET_POTENTIAL_LOCATION, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSomaListWithOkHttp(String username, String password, String image, int x, int y, int z, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", username)
                    .put("passwd", password)
                    .put("image", image)
                    .put("pa2", new JSONObject().put("x", x).put("y", y).put("z", z))));
            asyncRequest(URL_GET_SOMA_LIST, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertSomaListWithOkHttp(String username, String password, int locationId, JSONArray somaList, String nickName, String image, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", username)
                    .put("passwd", password)
                    .put("locationId", locationId)
                    .put("somalist", somaList)
                    .put("owner", nickName)
                    .put("image", image)));
            asyncRequest(URL_INSERT_SOMA_LIST, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
