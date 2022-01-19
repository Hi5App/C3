package com.penglab.hi5.core.game.leaderBoard;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.ViewModelFactory;

public class LeaderBoardActivity extends AppCompatActivity {

    private LeaderBoardAdapter adapter;

    private LeaderBoardViewModel leaderBoardViewModel;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private String [] titles = {"today","universal"};
    private int images[] = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        Toolbar toolbar = findViewById(R.id.toolbar_leaderboard);
        setSupportActionBar(toolbar);

        leaderBoardViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(LeaderBoardViewModel.class);


//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.leaderboard_recyclerview);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new LeaderBoardAdapter(leaderBoardViewModel.getLeaderBoardItemList());
//        recyclerView.setAdapter(adapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(titles[0]));
        tabLayout.addTab(tabLayout.newTab().setText(titles[1]));
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

        viewPager =(ViewPager)findViewById(R.id.vp_pager);

        TabPagerAdapter adapter = new TabPagerAdapter();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
//        List<Fragment> fragments = new ArrayList<>();
//        fragments.add(FirstFragment.newInstance());
//        fragments.add(SecondFragment.newInstance());

//        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(),fragments, Array.asList(titles));
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position){
//                Log.i(TAG,"select page:" + position);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    public class TabPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView tv = new TextView(LeaderBoardActivity.this);
            tv.setText(titles[position]);
            tv.setGravity(Gravity.CENTER);
            container.addView(tv, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
            return tv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}




