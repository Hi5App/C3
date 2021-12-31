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
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.penglab.hi5.R;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.core.game.LeaderBoardActivity;
import com.penglab.hi5.core.game.QuestActivity;
import com.penglab.hi5.core.game.RewardActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;


public class MyActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private static final String USERNAME = "USERNAME";

    public static String username = null;
    private long exitTime = 0;
    Toolbar toolbar_my;
    CollapsingToolbarLayout collapsingToolbarLayout;




    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        toolbar_my = findViewById(R.id.toolbar_my);
        toolbar_my.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar_my.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        initButtons();

















    }


    private void initButtons(){


        collapsingToolbarLayout = findViewById(R.id.mCollapsingToolbarLayout);












    }


    public void start(Context context){
        context.startActivity(new Intent(context,MyActivity.class));
    }




//
//    @Override
//    public void onClick(View v)
//    {
//        switch(v.getId()){
//
//            case R.id.tv_back:
//                this.finish();
//                break;
//
//                case R.id.
//        }
//
//
//
//
//    }
    
    
    
}

