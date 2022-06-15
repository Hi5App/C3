package com.penglab.hi5.core.net;

import android.util.Log;

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

    public static void getSomaListWithOkHttp(JSONObject userInfo, JSONObject bBox, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("bb", bBox)
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_SOMA_LIST, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSomaListWithOkHttp(JSONObject userInfo, int locationId, int locationType, JSONArray insertSomaList, JSONArray deleteSomaList, String username, String image, Callback callback) {
        try {
            /* locationType:
                -1: boringFile,
                 0: default, no update
                 1: normalFile with annotation,
                 1: normalFile without annotation
            */
            JSONObject updateInfo = new JSONObject()
                    .put("locationId", locationId)
                    .put("locationtype", locationType)
                    .put("insertsomalist", insertSomaList)
                    .put("deletesomalist", deleteSomaList)
                    .put("owner", username)
                    .put("image", image);
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("pa", updateInfo)
                    .put("user", userInfo)));
            asyncPostRequest(URL_INSERT_SOMA_LIST, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
