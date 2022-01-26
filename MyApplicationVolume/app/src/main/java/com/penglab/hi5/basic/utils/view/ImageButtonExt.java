package com.penglab.hi5.basic.utils.view;


import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

/**
 * Created by Jackiexing on 01/26/2022
 */
public class ImageButtonExt extends AppCompatImageButton {

    private static final int TIME = 500;
    private static long lastClickTime = 0;

    public ImageButtonExt(@NonNull Context context) {
        super(context);
    }

    public ImageButtonExt(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageButtonExt(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        if (!isFastDoubleClick()) {
            return super.performClick();
        }
        return false;
    }

    /**
     * 处理快速双击，多击事件，在TIME时间内只执行一次事件
     */
    public static boolean isFastDoubleClick() {
        long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - lastClickTime;
        if (0 < timeInterval && timeInterval < TIME) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }
}
