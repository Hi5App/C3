package com.penglab.hi5.core.ui.userProfile;

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
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
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
import android.view.ViewGroup;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


public class MyActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private static final String USERNAME = "USERNAME";

    public static String username = null;
    private AppBarLayout appbar;
    private long exitTime = 0;
    Toolbar toolbar_my;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView name;
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
//        appbar = (AppBarLayout)findViewById(R.id.appbar);
//        appbar.addOnOffsetChangedListener(this::onOffsetChanged);


        toolbar_my = findViewById(R.id.toolbar_my);
//        toolbar_my.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        collapsingToolbarLayout = findViewById(R.id.mCollapsingToolbarLayout);
        layout_personal_top_view = findViewById(R.id.person_top_view);

        setSupportActionBar(toolbar_my);

        name =layout_personal_top_view.findViewById(R.id.item_name);
        name.setText(UserInfoRepository.getInstance().getUser().getNickName());


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

    }


//    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//        Log.d("aaa", "verticalOffset=" + verticalOffset);
//        //720*1080手机 verticalOffset取值范围[0-200]px
//        int absVerticalOffset = Math.abs(verticalOffset);//AppBarLayout竖直方向偏移距离px
//        int totalScrollRange = appBarLayout.getTotalScrollRange();//AppBarLayout总的距离px
//        //背景颜色转化成RGB的渐变色
//        int argb = Color.argb(absVerticalOffset, Color.red(mMaskColor), Color.green(mMaskColor), Color.blue(mMaskColor));
//        int argbDouble = Color.argb(absVerticalOffset * 2, Color.red(mMaskColor), Color.green(mMaskColor), Color.blue(mMaskColor));
//        //appBarLayout上滑一半距离后小图标应该由渐变到全透明
//        int title_small_offset = (200 - absVerticalOffset) < 0 ? 0 : 200 - absVerticalOffset;
//        int title_small_argb = Color.argb(title_small_offset * 2, Color.red(mMaskColor),
//                Color.green(mMaskColor), Color.blue(mMaskColor));
//        //appBarLayout上滑不到一半距离
//        if (absVerticalOffset <= totalScrollRange / 2) {
//            toolbar_my.setVisibility(View.VISIBLE);
//            layout_personal_top_view.setVisibility(View.GONE);
//            //为了和下面的大图标渐变区分,乘以2倍渐变
////            v_toolbar_search_mask.setBackgroundColor(argbDouble);
//        } else {
//            toolbar_my.setVisibility(View.GONE);
//            layout_personal_top_view.setVisibility(View.VISIBLE);
//            //appBarLayout上滑一半距离后小图标应该由渐变到全透明
////            v_toolbar_small_mask.setBackgroundColor(title_small_argb);
//
//        }
////        //上滑时遮罩由全透明到半透明
////        v_title_big_mask.setBackgroundColor(argb);
//    }



    public static void start(Context context) {
        Intent intent = new Intent(context, MyActivity.class);
        context.startActivity(intent);
    }

    private void openChatActivity() {
        Intent intent = new Intent(MyActivity.this, ChatActivity.class);
        startActivity(intent);
    }
}



    
    


