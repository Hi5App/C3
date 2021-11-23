package com.penglab.hi5.core.ui.main.screens;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.main.adapters.MainPagerAdapter;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String USERNAME = "USERNAME";

    public static String username = null;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        username = getIntent().getStringExtra(USERNAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_main);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts);
//        navigationTabStrip.setTitles("HOW WE WORK", "WE WORK WITH");
        navigationTabStrip.setTitles("HOW WE WORK");
        navigationTabStrip.setViewPager(viewPager);
    }

    /*
     Start this activity
     */
    public static void start(Context context){
        start(context, null);
    }

    public static void start(Context context, String username){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(USERNAME, username);
        context.startActivity(intent);
    }

    /*
    Press twice to return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastEasy("Press again to exit the program");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
    Create options menu for toolbar
    */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.items, menu);
//        return true;
//    }

    private void setUpNavDrawer(){

    }
}
