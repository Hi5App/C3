package com.penglab.hi5.core.net;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

/**
 * Created by Jackiexing on 06/24/21
 */
public class HttpUtilsCollaborate extends HttpUtils{
    private static final String URL_GET_NEURONS = SERVER_IP + "/release/collaborate/getanoneuron";
    private static final String URL_GET_GetProjectSwcNamesByProjectUuid = DBMS_SERVER_IP + "/proto.DBMS/GetProjectSwcNamesByProjectUuid";
    private static final String URL_GET_AllProject = DBMS_SERVER_IP + "/proto.DBMS/GetAllProject";
    private static final String URL_LOAD_ANO = SERVER_IP + "/release/collaborate/inheritother";

    public static void getNeuronsWithOkHttp(JSONObject userInfo,String brianList,Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("image", brianList)
                    .put("user", userInfo)));
            asyncPostRequest(URL_GET_NEURONS, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getProjectSwcNamesByProjectUuid(JSONObject param, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(param));
            asyncPostRequest(URL_GET_GetProjectSwcNamesByProjectUuid, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAllProject(JSONObject param, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(param));
            asyncPostRequest(URL_GET_AllProject, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadAnoWithOkHttp(JSONObject userInfo,String brainList,String neuron,String ano, String projectName, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("image",brainList)
                    .put("neuron", neuron)
                    .put("ano",ano)
                    .put("user", userInfo)
                    .put("project",projectName)));
            asyncPostRequest(URL_LOAD_ANO, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






























}
