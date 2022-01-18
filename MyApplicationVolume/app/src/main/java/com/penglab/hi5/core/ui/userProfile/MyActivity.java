package com.penglab.hi5.core.ui.userProfile;


import android.content.Context;
import android.content.Intent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.penglab.hi5.R;
import com.penglab.hi5.chat.ChatActivity;


import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.game.leaderBoard.LeaderBoardActivity;
//import com.penglab.hi5.core.game.QuestActivity;
import com.penglab.hi5.core.game.RewardActivity;
import com.penglab.hi5.core.game.quest.QuestActivity;

import com.penglab.hi5.data.UserInfoRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

//import com.penglab.hi5.core.game.QuestActivity;
import com.penglab.hi5.data.UserInfoRepository;


public class MyActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private static final String USERNAME = "USERNAME";

    public static String username = null;
    private AppBarLayout appbar;
    private long exitTime = 0;
    Toolbar toolbar_my;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView name;
    private TextView email;

    private ImageView like;
    private ImageView person_chat;
    private ImageView infoEdit;
    private LinearLayout layout_personal_top_view;
    private NestedScrollView bottom_view;
    private LinearLayout person_reward;
    private LinearLayout person_leaderboard;
    private LinearLayout person_daily_quests;
    private int mMaskColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        toolbar_my = findViewById(R.id.toolbar_my);
        collapsingToolbarLayout = findViewById(R.id.mCollapsingToolbarLayout);
        layout_personal_top_view = findViewById(R.id.person_top_view);

        setSupportActionBar(toolbar_my);

        name =layout_personal_top_view.findViewById(R.id.item_name);
        email = layout_personal_top_view.findViewById(R.id.item_email);
        name.setText(UserInfoRepository.getInstance().getUser().getNickName());
//        email.setText(UserInfoRepository.getInstance().getUser().getEmail());

        mMaskColor = getResources().getColor(R.color.blue);
        like = findViewById(R.id.like);
        infoEdit =findViewById(R.id.edit);
        person_chat =findViewById(R.id.person_chat);
        person_reward = findViewById(R.id.reward);
        person_leaderboard = findViewById(R.id.leaderBoard);
        person_daily_quests = findViewById(R.id.quest);


        infoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like.setImageResource(R.drawable.ic_like_alive);
            }
        });

        person_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatActivity();
            }
        });

        person_reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Reward activity is under maintenance!",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MyActivity.this, RewardActivity.class);
//                startActivity(intent);
            }

        });

        person_leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyActivity.this, LeaderBoardActivity.class));
            }
        });

        person_daily_quests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MyActivity.this, QuestActivity.class));
            }
        });

    }


    public static void start(Context context) {
        Intent intent = new Intent(context, MyActivity.class);
        context.startActivity(intent);
    }

    private void openChatActivity() {
        Intent intent = new Intent(MyActivity.this, ChatActivity.class);
        startActivity(intent);
    }
}



    
    


