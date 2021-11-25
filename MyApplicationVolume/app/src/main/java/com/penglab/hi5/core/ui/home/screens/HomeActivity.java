package com.penglab.hi5.core.ui.home.screens;

import static com.penglab.hi5.core.MainActivity.ifGuestLogin;
import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.viewpager.widget.ViewPager;;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.material.navigation.NavigationView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.penglab.hi5.R;
import com.penglab.hi5.core.HelpActivity;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.core.ui.home.adapters.MainPagerAdapter;
import com.penglab.hi5.dataStore.PreferenceLogin;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String USERNAME = "USERNAME";

    public static String username = null;
    private long exitTime = 0;

    private int mSelectedItem = 0;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Context homeContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeContext = this;

        username = getIntent().getStringExtra(USERNAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        drawerLayout  = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.drawable.ic_menu,R.string.drawer_open_content_description,R.string.drawer_closed_content_description);
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        final NavigationView navigationView = findViewById(R.id.nav_view);
        if (ifGuestLogin){
            MenuItem accountItem = navigationView.getMenu().findItem(R.id.nav_account);
            accountItem.setTitle("Login");
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mSelectedItem = item.getItemId();
                switch(item.getItemId()){
                    case R.id.nav_account:
                        if(ifGuestLogin){
                            login();
                        }else
                            logout();
                        break;
//                    case R.id.nav_settings:
//                        Settings();
//
////                        setSettings();
//                        break;
                    case R.id.nav_help:
                        try {
                            Intent helpIntent = new Intent(HomeActivity.this, HelpActivity.class);
                            startActivity(helpIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.nav_About:
                        About();
                        break;
                }

                item.setChecked(true);
                drawerLayout.closeDrawer(navigationView);
                return false;
            }
        });

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_main);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts);
//        navigationTabStrip.setTitles("HOW WE WORK", "WE WORK WITH");
        navigationTabStrip.setTitles("HOW WE WORK");
        navigationTabStrip.setViewPager(viewPager);
    }
        @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
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
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                ToastEasy("Press again to exit the program");
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

        public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if(drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawers();
                return false;
            }else{
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    ToastEasy("Press again to exit the program");
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void logout(){

        AlertDialog aDialog = new AlertDialog.Builder(homeContext)
                .setTitle("Log out")
                .setMessage("Are you sure to Log out?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 清理缓存&注销监听&清除状态
                        NimUIKit.logout();
                        NIMClient.getService(AuthService.class).logout();

//                        AgoraMsgManager.getInstance().getRtmClient().logout(null);

                        PreferenceLogin preferenceLogin = new PreferenceLogin(HomeActivity.this);
                        preferenceLogin.setPref(preferenceLogin.getUsername(),preferenceLogin.getPassword(),false, true);
                        // DemoCache.clear();

                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        aDialog.show();
    }

    private void login(){

        AlertDialog aDialog = new AlertDialog.Builder(homeContext)
                .setTitle("Log in")
                .setMessage("Are you sure to Log in?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 清理缓存&注销监听&清除状态
                        NimUIKit.logout();
                        NIMClient.getService(AuthService.class).logout();

//                        AgoraMsgManager.getInstance().getRtmClient().logout(null);

                        PreferenceLogin preferenceLogin = new PreferenceLogin(HomeActivity.this);
                        preferenceLogin.setPref(preferenceLogin.getUsername(),preferenceLogin.getPassword(),false, true);
                        // DemoCache.clear();

                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        aDialog.show();
    }

    private void About() {
        new XPopup.Builder(this)
                .asConfirm("Hi5: VizAnalyze Big 3D Images", "By Peng lab @ BrainTell. \n\n" +

                                "Version: 20210803a 11:39 UTC+8 build",

                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                            }
                        })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
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

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;}
    return true;
    }
}


