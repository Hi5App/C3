package com.penglab.hi5.basic.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.penglab.hi5.R;
import com.lxj.xpopup.core.PositionPopupView;

public class MsgPopup extends PositionPopupView {
    private CountTimer countTimer;

    public MsgPopup(@Nullable Context context, int millisInFuture){
        super(context);
        countTimer = new CountTimer(millisInFuture, 500);
        countTimer.start();
    }

    @Override
    protected int getImplLayoutId(){
        return R.layout.popup_msg;
    }

    public void setText(String text){
        TextView textView = this.findViewById(R.id.msg_text);
        textView.setText(text);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.d("MsgPopup", "onTouchEvent");
        return true;
    }

    private class CountTimer extends CountDownTimer{
        public CountTimer(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish(){
            cancel();
        }

        @Override
        public void onTick(long millisUntilFInished){
            if (millisUntilFInished < 1000 && MsgPopup.this.isShow())
                MsgPopup.this.dismiss();
        }
    }
}
