package com.penglab.hi5.core.ui.home.screens;

import static com.penglab.hi5.core.MainActivity.getContext;
import static com.penglab.hi5.core.MainActivity.ifGuestLogin;
import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.data.model.user.LogStatus.GUEST;
import static com.penglab.hi5.data.model.user.LogStatus.LOGIN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.material.navigation.NavigationView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.utils.CrashHandler;
import com.penglab.hi5.basic.utils.CrashReports;
import com.penglab.hi5.core.BaseActivity;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.userProfile.MyActivity;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.core.ui.home.adapters.MainPagerAdapter;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.dataStore.PreferenceLogin;
import com.penglab.hi5.data.model.user.LogStatus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;


public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";
    private static final String GUEST_NICKNAME = "Guest user";
    private static final String GUEST_EMAIL = "hi5@penglab.com";

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_PERMISSION_CODE = 1;

    private long exitTime = 0;

    private HomeViewModel homeViewModel;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private TextView nickName;
    private TextView email;
    private ImageView headview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        // set up for NavDrawer
        setNavDrawer();

        // set up for cards view
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_main);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        // set up for the tab
        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts);
//        navigationTabStrip.setTitles("HOW WE WORK", "WE WORK WITH");
        navigationTabStrip.setTitles("HOW WE WORK");
        navigationTabStrip.setViewPager(viewPager);

        // Set the permission for user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
        }

        homeViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(HomeViewModel.class);
        homeViewModel.getLogStatus().observe(this, new Observer<LogStatus>() {
            @Override
            public void onChanged(LogStatus logStatus) {
                if (logStatus == null) {
                    return;
                }
                MenuItem accountItem = navigationView.getMenu().findItem(R.id.nav_account);
                if (logStatus == LOGIN) {
                    accountItem.setTitle("Logout");
                } else if (logStatus == GUEST) {
                    accountItem.setTitle("Login");
                } else {
                    LoginActivity.start(HomeActivity.this);
                    finish();
                }
                homeViewModel.updateUserView();
                homeViewModel.updateScore();
            }
        });

        homeViewModel.getUserView().observe(this, new Observer<UserView>() {
            @Override
            public void onChanged(UserView userView) {
                if (userView == null) {
                    return;
                }
                if (homeViewModel.isLogged()) {
                    nickName.setText(userView.getNickName());
                    email.setText(userView.getEmail());
                } else {
                    nickName.setText(GUEST_NICKNAME);
                    email.setText(GUEST_EMAIL);
                }
            }
        });

        homeViewModel.getScore().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == null) {
                    return;
                }
                Log.e(TAG, "score: " + integer);
            }
        });

        homeViewModel.getUserDataSource().getResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                homeViewModel.updateLogStatus(result);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE){
            for (int i = 0; i < permissions.length; i++) {
                Log.i(TAG,"Request permission " + permissions[i] + ", result " + grantResults[i]);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
        homeViewModel.updateLogged();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNavDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open_content_description,
                R.string.drawer_closed_content_description) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = LayoutInflater.from(HomeActivity.this)
                .inflate(R.layout.nav_header_main, navigationView);

        nickName = header.findViewById(R.id.nickname);
        email = header.findViewById(R.id.email);
        headview = header.findViewById(R.id.HeadView);

        headview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyActivity.start(HomeActivity.this);

                if (homeViewModel.isLogged()){
                    MyActivity.start(HomeActivity.this);
                }
            }
        });
        };


    @Override
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_account:
                if (homeViewModel.isLogged()) {
                    logout();
                } else {
                    login();
                }
                break;
            case R.id.nav_settings:
                settings();
                break;
            case R.id.nav_about:
                about();
                break;
            case R.id.nav_check_for_updates:
                installApk();
                break;
        }
        drawerLayout.closeDrawer(navigationView);
        return false;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    /* Press twice to exit app */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawers();
                return false;
            } else {
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

    private void logout() {
        new XPopup.Builder(this)
                .asConfirm("Log out", "Are you sure to Log out ?",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                homeViewModel.logout();
                            }
                        })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
    }

    private void login() {
        new XPopup.Builder(this)
                .asConfirm("Log in", "Are you sure to Log in ?",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                LoginActivity.start(HomeActivity.this);
                                finish();
                            }
                        })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
    }

    private void settings() {
        new XPopup.Builder(this)
                .asCenterList("Settings", new String[]{"Crash Report", "Clean Img Cache"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Crash Report":
                                        shareCrashReports();
                                        break;
                                    case "Clean Img Cache":
                                        cleanImgCache();
                                        break;
                                    default:
                                        Log.e(TAG, "Something wrong in settings !");
                                }
                            }
                        })
                .show();
    }

    private void about() {
        new XPopup.Builder(this)
                .asConfirm("Hi5: VizAnalyze Big 3D Images", "By Peng lab @ BrainTell. \n\n" +

                                "Version: 20220110a 11:39 UTC+8 build",

                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                            }
                        })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
    }

    /* share crash report */
    private void shareCrashReports() {
        CrashReports crashReports = CrashHandler.getCrashReportFiles(getContext());
        new XPopup.Builder(this)
                .maxHeight(1350)
                .asCenterList("Select a Crash Report", crashReports.reportNames,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                if (!crashReports.isEmpty) {
                                    // get the file path
                                    String filePath = CrashHandler.getCrashFilePath(getApplicationContext()) + "/" + text + ".txt";
                                    File file = new File(filePath);
                                    if (file.exists()) {
                                        Intent intent = new Intent();
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                        intent.setAction(Intent.ACTION_SEND);
                                        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), "com.penglab.hi5.provider", new File(filePath)));  //传输图片或者文件 采用流的方式
                                        intent.setType("*/*");   //分享文件
                                        startActivity(Intent.createChooser(intent, "Share From Hi5"));
                                    } else {
                                        ToastEasy("File does not exist");
                                    }
                                }
                            }
                        })
                .show();
    }

    /* clean img cache of Big Data */
    public void cleanImgCache() {
        new XPopup.Builder(this)
                .asConfirm("Clean the img cache", "Are you sure to CLEAN ALL IMG CACHE OF BIG DATA MODULE?",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                homeViewModel.cleanImgCache();
                            }
                        })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
    }

    private void installApk() {
        String filePath = null;
        try {
            filePath = Environment.getExternalStorageDirectory().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = "Hi5_20220105.apk";
        File apkFile = new File(filePath, fileName);
        if (!apkFile.exists()) {
            return;
        }
        Uri apkUri = FileProvider.getUriForFile(this, context.getPackageName() + ".provider", apkFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

}


