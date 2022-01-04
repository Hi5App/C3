package com.penglab.hi5.core.net;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

/**
 * Created by Yihang zhu 01/04/21
 */
public class HttpUtilsCheckArbor extends HttpUtils{
    private static final String GET_CHECK_INFOS_URL = SERVER_IP + "/check/getcheckinfos";

    public static void getCheckArborList(String username, String password, boolean withChecked, int off, int limit, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("name", username)
                    .put("password", password)
                    .put("WithChecked", withChecked)
                    .put("Off", off)
                    .put("Limit", limit)));
            asyncRequest(GET_CHECK_INFOS_URL, body, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getArborUrl(String arborName, int xc, int yc, int zc, String imageId, String url, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("arborName", arborName)
                    .put("xc", xc)
                    .put("yc", yc)
                    .put("zc", zc)
                    .put("imageId", imageId)
                    .put("url", url)));
            asyncRequest(GET_CHECK_INFOS_URL, body, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
