package com.penglab.hi5.core.net;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

/**
 * Created by Jackiexing on 01/11/21
 */
public class HttpUtilsSoma extends HttpUtils {
    private static final String URL_GET_POTENTIAL_LOCATION = SERVER_IP + "/dynamic/soma/getpotentiallocation";
    private static final String URL_GET_SOMA_LIST = SERVER_IP + "/dynamic/soma/getsomalist";
    private static final String URL_INSERT_SOMA_LIST = SERVER_IP + "/dynamic/soma/updatesomalist";

    public static void getPotentialLocationWithOkHttp(JSONObject userInfo, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                .put("user", userInfo)));
            asyncPostRequest(URL_GET_POTENTIAL_LOCATION, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSomaListWithOkHttp(JSONObject userInfo, String image, int x, int y, int z, int size, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("user", userInfo)
                    .put("image", image)
                    .put("pa1", new JSONObject().put("x", x - size/2).put("y", y - size/2).put("z", z - size/2))
                    .put("pa2", new JSONObject().put("x", x + size/2).put("y", y + size/2).put("z", z + size/2))));
            asyncPostRequest(URL_GET_SOMA_LIST, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSomaListWithOkHttp(JSONObject userInfo, int locationId, JSONArray insertSomaList, JSONArray deleteSomaList, String username, String image, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("user", userInfo)
                    .put("locationId", locationId)
                    .put("insertsomalist", insertSomaList)
                    .put("deletesomalist", deleteSomaList)
                    .put("owner", username)
                    .put("image", image)));
            asyncPostRequest(URL_INSERT_SOMA_LIST, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
