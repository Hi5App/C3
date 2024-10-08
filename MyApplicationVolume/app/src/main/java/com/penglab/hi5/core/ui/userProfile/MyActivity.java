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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;

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
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.UserInfoRepository;

import java.util.ArrayList;
import java.util.List;

//import com.penglab.hi5.core.game.QuestActivity;
import com.penglab.hi5.data.UserInfoRepository;
import com.penglab.hi5.data.UserPerformanceDataSource;


public class MyActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private static final String USERNAME = "USERNAME";

    private MyViewModel myViewModel;

    public static String username = null;
    private AppBarLayout appbar;
    private long exitTime = 0;
    Toolbar toolbar_my;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView name;
    private TextView email;

    private TextView scoreTextView;
    private TextView somaTextView;
    private TextView dailySomaTextView;
    private TextView checkSwcTextView;
    private TextView dailyCheckSwcTextView;

    private ImageView like;
    private ImageView person_chat;
    private ImageView infoEdit;
    private LinearLayout layout_personal_top_view;
    private NestedScrollView bottom_view;
    private LinearLayout person_reward;
    private LinearLayout person_leaderboard;
    private LinearLayout person_daily_quests;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        myViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(MyViewModel.class);
        toolbar_my = findViewById(R.id.toolbar_my);
        collapsingToolbarLayout = findViewById(R.id.mCollapsingToolbarLayout);
        layout_personal_top_view = findViewById(R.id.person_top_view);

        setSupportActionBar(toolbar_my);

        name =layout_personal_top_view.findViewById(R.id.item_name);
        email = layout_personal_top_view.findViewById(R.id.item_email);
        name.setText(UserInfoRepository.getInstance().getUser().getNickName());
//        email.setText(UserInfoRepository.getInstance().getUser().getEmail());

//        scoreTextView = findViewById(R.id.score_textview);
        somaTextView = findViewById(R.id.soma_text_view);
        dailySomaTextView = findViewById(R.id.daily_soma_text_view);
        checkSwcTextView = findViewById(R.id.check_swc_view);
        dailyCheckSwcTextView =findViewById(R.id.daily_check_text_view);
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
                Intent intent = new Intent(MyActivity.this, RewardActivity.class);
                startActivity(intent);
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
        myViewModel.getUserPerformance();

//        myViewModel.getUserInfoRepository().getScoreModel().getObservableScore().observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer integer) {
//                if(integer == null) {
//                    return;
//                }
//                else {
//                    scoreTextView.setText(Integer.toString(integer));
//                }
//
//            }
//        });

        myViewModel.getUserPerformanceDataSource().getPersonalResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if(result == null) {
                    return;
                } else {
                    myViewModel.updateSomaAndDailySoma(result);
                }

            }
        });


        myViewModel.getCheckCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                checkSwcTextView.setText(Integer.toString(integer));
            }
        });

        myViewModel.getSomaCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                somaTextView.setText(Integer.toString(integer));
            }
        });

        myViewModel.getDailySomaCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                dailySomaTextView.setText(Integer.toString(integer));
            }
        });

        myViewModel.getDailyCheckCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                dailyCheckSwcTextView.setText(Integer.toString(integer));
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



    
    


