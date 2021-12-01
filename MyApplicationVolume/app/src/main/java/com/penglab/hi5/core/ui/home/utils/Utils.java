package com.penglab.hi5.core.ui.home.utils;

import static com.penglab.hi5.core.ui.home.screens.HomeActivity.username;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.penglab.hi5.R;
import com.penglab.hi5.chat.ChatActivity;
import com.penglab.hi5.core.HelpActivity;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.S2Activity;


/**
 * Modified by Jackiexing on 11/22/21.
 */
public class Utils {

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
                    case "Hi5":
                        MainActivity.actionStart(context, username);
                        break;
                    case "S2":
                        S2Activity.actionStart(context, username);
                        break;
                    case "Chat":
                        ChatActivity.start(context);
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
