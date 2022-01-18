package com.penglab.hi5.core.game.leaderBoard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.netease.nim.uikit.common.media.imagepicker.view.SystemBarTintManager;
import com.penglab.hi5.R;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    private LeaderBoardAdapter adapter;

    private LeaderBoardViewModel leaderBoardViewModel;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    List<Fragment> fragmentList = new ArrayList<>();
    UniversalFragment UniversalFragment;
    TodayFragment TodayFragment;
    private String [] titles = {"today","universal"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.reward);
        }
        setContentView(R.layout.activity_leader_board);

        Toolbar toolbar = findViewById(R.id.toolbar_leaderboard);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabLayout = findViewById(R.id.tab_layout);
        viewPager =(ViewPager)findViewById(R.id.vp_pager);
        tabLayout.addTab(tabLayout.newTab().setText(titles[0]));
        tabLayout.addTab(tabLayout.newTab().setText(titles[1]));

        UniversalFragment = new UniversalFragment();
        fragmentList.add(UniversalFragment);
        TodayFragment = new TodayFragment();
        fragmentList.add(TodayFragment);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(LeaderBoardActivity.this,"selected: "+tab.getText(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setVerticalScrollbarPosition(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.back:
                finish();
                return true;
            default:
                return true;
        }
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        List<Fragment> mFragments;
        String[] mTitles;
        private  FragmentManager fm;
        public ViewPagerAdapter(FragmentManager fm,List<Fragment>fragmentList,String[]titles){
            super(fm);
            this.fm = fm;
            mFragments = fragmentList;
            mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new Fragment ();
            fragment = mFragments.get(position);
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);
            viewPager.setCurrentItem(position);
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment instantiateItem (ViewGroup container,int position){
            Fragment fragment =(Fragment)super.instantiateItem(container,position);
            fm.beginTransaction().show(fragment).commitAllowingStateLoss();
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }





}




