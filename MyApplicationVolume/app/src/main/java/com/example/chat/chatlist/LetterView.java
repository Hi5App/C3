package com.example.chat.chatlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication__volume.R;


public class LetterView extends LinearLayout {
    private Context mContext;
    private CharacterClickListener mListener;

    public LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;//接收传进来的上下文
        setOrientation(VERTICAL);
        initView();
    }

    private void initView(){
        addView(buildImageLayout());

        for (char i = 'A';i <= 'Z';i++){
            final String character = i + "";
            TextView tv = buildTextLayout(character);
            addView(tv);
        }

        addView(buildTextLayout("#"));
    }

    private TextView buildTextLayout(final String character){
        LayoutParams layoutParams =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1);
        TextView tv = new TextView(mContext);
        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER);
        tv.setClickable(true);
        tv.setText(character);
        tv.setTextSize(12);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.clickCharacter(character);
                }
            }
        });
        return tv;
    }
    private ImageView buildImageLayout() {
        LayoutParams layoutParams = new LayoutParams(20, LayoutParams.MATCH_PARENT, 1);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        ImageView iv = new ImageView(mContext);
        iv.setLayoutParams(layoutParams);

        iv.setBackgroundResource(R.drawable.ic_baseline_arrow_upward_24);

        iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.clickArrow();
                }
            }
        });
        return iv;
    }

    public void setCharacterListener(CharacterClickListener listener) {
        mListener = listener;
    }

    public interface CharacterClickListener {
        void clickCharacter(String character);

        void clickArrow();
    }
}

