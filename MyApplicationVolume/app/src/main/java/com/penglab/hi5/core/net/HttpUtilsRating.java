package com.penglab.hi5.core.net;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.util.Log;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.ui.ImageClassify.ImageClassifyViewModel;
import com.penglab.hi5.core.ui.ImageClassify.RatingImageInfo;
import com.penglab.hi5.core.ui.home.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtilsRating extends HttpUtils {

    private static final String URL_GET_RATING_IMAGE_LIST = SERVER_IP + "/release/GetRatingImageList";
    private static final String URL_UPDATE_RATING_RESULT = SERVER_IP + "/release/UpdateRatingResult";
    private static final String URL_DOWNLOAD_RATING_IMAGE = SERVER_IP +"/release/GetRatingImageFile/";

    private static final String URL_GET_RATTING_RESULT = SERVER_IP +"/release/GetRatingResult";

    public static void getRattingImageListWithOkHttp(String username,String password, int count, Callback callback) {
        try {
            RequestBody body = RequestBody.create(JSON, String.valueOf(new JSONObject()
                    .put("UserName", username)
                    .put("Password", password)
                    .put("ImagesCount", count)));
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

    public static void downloadFile(RatingImageInfo ratingImageInfo, ImageClassifyViewModel imageClassifyViewModel) {
        Request request = new Request.Builder().url(URL_DOWNLOAD_RATING_IMAGE + ratingImageInfo.ImageName).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ratingImageInfo.DownloadFailed = true;
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String ImagePath = InfoCache.getContext().getExternalFilesDir(null) + "/Image";
                String imageStorePath = ImagePath + "/" + ratingImageInfo.ImageName;
                InputStream in = null;
                FileOutputStream out = null;

                if (ratingImageInfo.IsDownloadCompleted && ratingImageInfo.LocalImageFile != null && !ratingImageInfo.LocalImageFile.isEmpty()) {
                    ratingImageInfo.DownloadFailed = true;
                    Log.e("httpUtilsRating", "Image file has been downloaded before ! Image: " + ratingImageInfo.LocalImageFile);
                    return;
                }

                try {
                    in = response.body().byteStream();

                    File dir = new File(ImagePath);
                    if (!dir.exists()) {
                        if (!dir.mkdirs()) {
                            Log.e("httpUtilsRating", "FileHelper: Fail to create directory !");
                            return;
                        }
                    }

                    File file = new File(imageStorePath);
                    out = new FileOutputStream(file);
                    byte[] buffer = new byte[1024*1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }

                    Log.e("httpUtilsRating", "image file name: " + ratingImageInfo.ImageName);
                    Log.e("httpUtilsRating", "image file size: " + file.length());

                    ratingImageInfo.LocalImageFile=imageStorePath;
                    ratingImageInfo.IsDownloading = false;
                    ratingImageInfo.IsDownloadCompleted = true;

                    if(imageClassifyViewModel != null) {
                        RatingImageInfo imageInfo = imageClassifyViewModel.acquireReScheduledDownloadImageInfo().getValue();
                        if(imageInfo !=null && imageInfo.ImageName.equals(ratingImageInfo.ImageName)) {
                            imageClassifyViewModel.acquireReScheduledDownloadImageInfo().postValue(null);
                        }
                    }

                }
                catch (Exception e) {
                    ratingImageInfo.DownloadFailed = true;
                    Log.e("httpUtilsRating", "Response from server is error when download image !");
                    e.printStackTrace();
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
            }
        });
    }



    public static void queryUserRattingTableWithOkHttp(String username, String password, String queryUserName, String queryStartTime, String queryEndTime, Callback callback) {
        try {
//            String rfc3339StartTime = Utils.convertToRFC3339(queryStartTime);
//            String rfc3339EndTime = Utils.convertToRFC3339(queryEndTime);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserName", username);
            jsonObject.put("Password", password);
            jsonObject.put("QueryUserName", queryUserName);
            jsonObject.put("QueryStartTime", queryStartTime);
            jsonObject.put("QueryEndTime", queryEndTime);

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            asyncPostRequest(URL_GET_RATTING_RESULT, body, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}











