package com.penglab.hi5.core.ui.home.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.penglab.hi5.R;
import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.ChatActivity;
import com.penglab.hi5.core.HelpActivity;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.S2Activity;
import com.penglab.hi5.core.net.HttpUtilsImage;
import com.penglab.hi5.core.ui.annotation.AnnotationActivity;
import com.penglab.hi5.core.ui.check.CheckActivity;
import com.penglab.hi5.data.UserInfoRepository;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Modified by Jackiexing on 11/22/21.
 */
public class Utils {

    private static final String TAG = "Home-Utils";

    public static void setupItem(final View view, final LibraryObject libraryObject) {
        final TextView txt = (TextView) view.findViewById(R.id.txt_item);
        txt.setText(libraryObject.getTitle());

        final ImageView img = (ImageView) view.findViewById(R.id.img_item);
        img.setImageResource(libraryObject.getRes());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                UserInfoRepository userInfoRepository = UserInfoRepository.getInstance();
                switch (libraryObject.getTitle()){
                    case "Big Image":
//                        HttpUtilsImage.getImageListWithOkHttp(
//                                userInfoRepository.getUser().getUserId(),
//                                "123456", new Callback() {
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                Log.e(TAG, "Failed");
//                                Log.e(TAG, e.getMessage());
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                Log.e(TAG, "Success");
//                                Log.e(TAG, response.body().string());
//                            }
//                        });
//                        MainActivity.actionStart(context, "empty");
                        AnnotationActivity.start(context);
                        break;
                    case "Smart Imaging":
//                        HttpUtilsImage.getNeuronListWithOkHttp(
//                                userInfoRepository.getUser().getUserId(),
//                                "123456", "18454", new Callback() {
//                                    @Override
//                                    public void onFailure(Call call, IOException e) {
//                                        Log.e(TAG, "Failed");
//                                        Log.e(TAG, e.getMessage());
//                                    }
//
//                                    @Override
//                                    public void onResponse(Call call, Response response) throws IOException {
//                                        Log.e(TAG, "Success");
//                                        Log.e(TAG, response.body().string());
//                                    }
//                                });
//                        S2Activity.actionStart(context, "empty");
                        S2Activity.start(context);
                        break;
                    case "Chat":
//                        HttpUtilsImage.getAnoListWithOkHttp(
//                                userInfoRepository.getUser().getUserId(),
//                                "123456", "18454_00049", new Callback() {
//                                    @Override
//                                    public void onFailure(Call call, IOException e) {
//                                        Log.e(TAG, "Failed");
//                                        Log.e(TAG, e.getMessage());
//                                    }
//
//                                    @Override
//                                    public void onResponse(Call call, Response response) throws IOException {
//                                        Log.e(TAG, "Success");
//                                        Log.e(TAG, response.body().string());
//                                    }
//                                });
                        ChatActivity.start(context);
                        break;
                    case "Help":
//                        HttpUtilsImage.downloadImageWithOkHttp(
//                                userInfoRepository.getUser().getUserId(),
//                                "123456",
//                                "18454/RES(26298x35000x11041)",
//                                14530,
//                                10693,
//                                3124,
//                                128,
//                                new Callback() {
//                                    @Override
//                                    public void onFailure(Call call, IOException e) {
//                                        Log.e(TAG, "Failed");
//                                        Log.e(TAG, e.getMessage());
//                                    }
//
//                                    @Override
//                                    public void onResponse(Call call, Response response) throws IOException {
//                                        Log.e(TAG, "Success");
//                                        byte[] fileContent = response.body().bytes();
//                                        Log.e(TAG,"content len: " + fileContent.length);
//                                        if (!FileHelper.storeFile(Myapplication.getContext().getExternalFilesDir(null) + "/Img", "18454_RES(26298x35000x11041).v3dpbd", fileContent)) {
//                                            Log.e(TAG, "Fail to store music");
//                                        }
//                                    }
//                                });
                        HelpActivity.start(context);
                        break;
                    case "Check":
                        CheckActivity.start(context);
                        break;
                    default:
                        Log.e("Utils","Something error");
                }
            }
        });
    }

    public static class LibraryObject {

        private String mTitle;
        private int mRes;

        public LibraryObject(final int res, final String title) {
            mRes = res;
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(final String title) {
            mTitle = title;
        }

        public int getRes() {
            return mRes;
        }

        public void setRes(final int res) {
            mRes = res;
        }
    }
}
