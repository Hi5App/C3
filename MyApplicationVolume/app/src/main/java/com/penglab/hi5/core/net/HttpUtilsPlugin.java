package com.penglab.hi5.core.net;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class HttpUtilsPlugin extends HttpUtils {
    public static final String UrlGetImageList = SERVER_IP + "/inference/api/netimagelist";

    public static final String UrlGetImageFile = SERVER_IP + "/inference/api/download";

    public static final String UrlGetMethodList = SERVER_IP + "/inference/api/methodlist";

    public static final String UrlExecuteMethod = SERVER_IP + "/inference/api/inference";

    public static final String UrlGetAllResult = SERVER_IP + "/inference/api/getallresult";

}

