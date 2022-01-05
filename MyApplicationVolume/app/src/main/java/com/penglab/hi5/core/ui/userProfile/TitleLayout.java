package com.penglab.hi5.core.ui.userProfile;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.penglab.hi5.R;

public class TitleLayout extends LinearLayout {
    private ImageView iv_backward;
    private TextView tv_title, tv_forward;

    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LinearLayout bar_title = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.person_bar_title, this);
        iv_backward = (ImageView) bar_title.findViewById(R.id.iv_backward);
        tv_title = (TextView) bar_title.findViewById(R.id.tv_title);
        tv_forward = (TextView) bar_title.findViewById(R.id.tv_forward);
//        if(ActivityCollector.getCurrentActivity().getClass().equals(UserInfoActivity.class)){
//            tv_forward.setText("save");
//            tv_title.setText("Edit");
//        }
//        if(ActivityCollector.getCurrentActivity().getClass().equals(EditName.class)){
//            tv_forward.setText("Done");
//            tv_title.setText("Edit nickname");
//        }

        //设置监听器
        //如果点击back则结束活动
        iv_backward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }
    public TextView getTextView_forward(){
        return tv_forward;
    }
}