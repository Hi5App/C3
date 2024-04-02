package com.penglab.hi5.core.net;

import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.RequestBody;

public class HttpUtilsRating extends HttpUtils {

    private static final String URL_GET_RATING_IMAGE_LIST = SERVER_IP + "/dynamic/GetRatingImageList";
    private static final String URL_UPDATE_RATING_RESULT = SERVER_IP + "/dynamic/UpdateRatingResult";
    private static final String URL_DOWNLOAD_RATING_IMAGE = SERVER_IP +"/dynamic/GetRatingImageFile/";

    public static void getRattingImageListWithOkHttp(String username,String password, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("UserName", username)
                    .put("Password", password)
                    .put("ImagesCount", 10)));
            asyncPostRequest(URL_GET_RATING_IMAGE_LIST, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadUserRatingResultWithOkHttp(String username, String password, String ImageName, String RatingEnum, String AdditionalRatingDescription, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("UserName", username)
                    .put("Password", password)
                    .put("ImageName", ImageName)
                    .put("RatingEnum",RatingEnum)
                    .put("AdditionalRatingDescription",AdditionalRatingDescription)));
            asyncPostRequest(URL_UPDATE_RATING_RESULT, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadSingleRattingImageWithOkHttp(String imageName, Callback callback) {
        try {
            asyncPostRequest(URL_DOWNLOAD_RATING_IMAGE + imageName, RequestBody.create(null, ""), callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}











