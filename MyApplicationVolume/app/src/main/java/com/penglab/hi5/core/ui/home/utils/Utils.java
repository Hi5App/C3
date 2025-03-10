package com.penglab.hi5.core.ui.home.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.penglab.hi5.R;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.utils.FileHelper;
import com.penglab.hi5.chat.ChatActivity;
import com.penglab.hi5.core.HelpActivity;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.S2Activity;
import com.penglab.hi5.core.net.HttpUtilsImage;
import com.penglab.hi5.core.ui.BoutonDetection.BoutonDetectionActivity;
import com.penglab.hi5.core.ui.ImageClassify.ImageClassifyActivity;
import com.penglab.hi5.core.ui.QualityInspection.QualityInspectionActivity;
import com.penglab.hi5.core.ui.annotation.AnnotationActivity;
import com.penglab.hi5.core.ui.check.CheckActivity;
import com.penglab.hi5.core.ui.collaboration.CollaborationActivity;
import com.penglab.hi5.core.ui.marker.MarkerFactoryActivity;
import com.penglab.hi5.core.ui.pluginsystem.PluginSystemActivity;
import com.penglab.hi5.data.UserInfoRepository;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
                switch (libraryObject.getTitle()){
                    case "Marker Factory":
                        MarkerFactoryActivity.start(context);
                        break;
                    case "Annotation":
                        AnnotationActivity.start(context);
                        break;
                    case "Check":
                        QualityInspectionActivity.start(context);
                        break;
                    case "Smart Imaging":
                        S2Activity.start(context);
                        break;
                    case "Collaboration":
                        CollaborationActivity.start(context);
                        break;
                    case "Synapse Validation":
                        BoutonDetectionActivity.start(context);
                        break;
                    case "Chat":
                        ChatActivity.start(context);
                        break;
                    case "Image Processing":
                        PluginSystemActivity.start(context);
                        break;
                    case "Image Classify":
                        ImageClassifyActivity.start(context);
                        break;
                    case "Help":
                        HelpActivity.start(context);
                        break;
                    default:
                        Log.e("Utils","Something error");
                }
            }
        });
    }

    public static String convertToRFC3339(String dateTimeString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateTimeString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
