package com.penglab.hi5.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.*;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.penglab.hi5.R;

/**
 * TODO: document your custom view class.
 */
public class TapBarMenuExpandMenu extends RelativeLayout {
    private ImageView splitImage;
    private ImageView changeMarkerColorImage;
    private ImageView removeMarkerImage;
    private ConstraintLayout menuLayout;

    public TapBarMenuExpandMenu(Context context) {
        super(context);
    }

    public TapBarMenuExpandMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TapBarMenuExpandMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.tapbarmenu_expand_menu, this, true);
        splitImage = (ImageView) findViewById(R.id.split);
        changeMarkerColorImage = (ImageView) findViewById(R.id.change_marker_color);
        removeMarkerImage = (ImageView) findViewById(R.id.remove_marker);
        menuLayout = (ConstraintLayout) findViewById(R.id.tapbarmenu_expand);

        menuLayout.setBackgroundColor(R.color.colorMid_yellow);

        splitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        changeMarkerColorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        removeMarkerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}