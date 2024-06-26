package com.penglab.hi5.core.ui.home.screens;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.google.android.material.navigation.NavigationView;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.utils.APKVersionInfoUtils;
import com.penglab.hi5.basic.utils.CrashHandler;
import com.penglab.hi5.basic.utils.CrashReports;
import com.penglab.hi5.core.BaseActivity;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.home.adapters.MainPagerAdapter;
import com.penglab.hi5.core.ui.login.LoginActivity;
import com.penglab.hi5.core.ui.userProfile.MyActivity;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.user.LogStatus;

import java.io.File;

import static com.penglab.hi5.core.MainActivity.getContext;
import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.data.model.user.LogStatus.GUEST;
import static com.penglab.hi5.data.model.user.LogStatus.LOGIN;

;


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

    private final Handler uiHandler = new Handler();
    private BasePopupView downloadingPopupView;
    private HomeViewModel homeViewModel;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private TextView nickName;
    private TextView email;
    private ImageView headview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//
//        File file = null;
//        file.mkdirs();
//
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

        checkStorageManagerPermission(this);



        downloadingPopupView = new XPopup.Builder(this).asLoading("Downloading......");

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

        homeViewModel.getWorkStatus().observe(this, new Observer<HomeViewModel.WorkStatus>() {
            @Override
            public void onChanged(HomeViewModel.WorkStatus workStatus) {
                if (workStatus == null) {
                    return;
                }
                switch (workStatus) {
                    case START_TO_DOWNLOAD_APK:
                        showDownloadingProgressBar();
                        break;
                    case ALREADY_LATEST_VERSION:
                        alreadyLatestVersionPopup();
                        break;
                    case NONE:
                        break;
                }
            }
        });

        homeViewModel.getUserView().observe(this, new Observer<UserView>() {
            @Override
            public void onChanged(UserView userView) {
                if (userView == null) {
                    return;
                }
                if (homeViewModel.isLogged()) {
                    nickName.setText(userView.getUserId());
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

        homeViewModel.getApkPath().observe(this, new Observer<FilePath>() {
            @Override
            public void onChanged(FilePath filePath) {
                if (filePath == null) {
                    return;
                }
                hideDownloadingProgressBar();
                installApk(filePath);
                homeViewModel.getApkPath().setValue(null);
            }
        });

        homeViewModel.getApkUrl().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String url) {
                if (url == null) {
                    return;
                }
                String[] details = url.split("/");
                newVersionPopup(url, details[details.length-1]);
                homeViewModel.getApkUrl().setValue(null);
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

        homeViewModel.getResourceDataSource().getResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                homeViewModel.updateResourceResult(result);
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
    public static void checkStorageManagerPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                !Environment.isExternalStorageManager()) {
            Log.e(TAG, " checkStorageManagerPermission;");
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
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
                checkLatestVersion();
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

                                "Version: 20220712p 14:55 UTC+8 build",

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
                                    String filePath = CrashHandler.getCrashFilePath(getContext()) + "/" + text + ".txt";
                                    File requestFile = new File(filePath);
                                    if (requestFile.exists()) {
                                        Intent intent = new Intent();
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                        intent.setAction(Intent.ACTION_SEND);
                                        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(HomeActivity.this, "com.penglab.hi5.provider", requestFile));
                                        intent.setType("*/*");   //分享文件
                                        startActivity(Intent.createChooser(intent, "Share From Hi5"));
                                    } else {
                                        ToastEasy("File does not exist !");
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

    private void checkLatestVersion() {
        homeViewModel.checkLatestVersion(APKVersionInfoUtils.getVersionName(this));
    }

    private void alreadyLatestVersionPopup() {
        new XPopup.Builder(this)
                .asConfirm("Already Latest Version", "Current version is already latest version.", null)
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
        .show();
    }

    private void newVersionPopup(String url, String newVersionName) {
        new XPopup.Builder(this)
                .asConfirm("New Version", "There is a new version: " + newVersionName + "\n\nDo you want to update ?", new OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        homeViewModel.downloadLatestVersion(url, newVersionName);
                    }
                })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
    }

    private void installApk(FilePath filePath) {
        File apkFile = new File((String) filePath.getData());
        if (!apkFile.exists()) {
            return;
        }
        Uri apkUri = FileProvider.getUriForFile(this, context.getPackageName() + ".provider", apkFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void showDownloadingProgressBar(){
        downloadingPopupView.show();
        uiHandler.postDelayed(this::timeOutHandler, 180 * 1000);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideDownloadingProgressBar(){
        downloadingPopupView.dismiss();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void timeOutHandler(){
        downloadingPopupView.dismiss();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}


