package com.penglab.hi5.core.ui.ImageClassify;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.playButtonSound;
import static com.penglab.hi5.core.Myapplication.updateMusicVolume;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.utils.view.ImageButtonExt;
import com.penglab.hi5.core.music.MusicService;
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.annotation.EditMode;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.dataStore.PreferenceMusic;
import com.penglab.hi5.data.dataStore.PreferenceSetting;
import com.penglab.hi5.data.dataStore.PreferenceSoma;
import com.penglab.hi5.data.model.img.ImageInfo;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import cn.carbs.android.library.MDDialog;

public class ImageClassifyActivity  extends AppCompatActivity {
    private AnnotationGLSurfaceView annotationGLSurfaceView;
    private ImageClassifyViewModel imageClassifyViewModel;
    private View imageClassifyView;
    private LinearLayout layoutSubcategories3, layoutSubcategories4;
    private EditText editTextRemark;
    private Toolbar toolbar;
    private SeekBar contrastSeekBar;
    private TextView imageIdLocationTextView;
    private final Handler uiHandler = new Handler();
    private BasePopupView downloadingPopupView;

    private boolean isImageExist = false;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_classify);
        annotationGLSurfaceView = findViewById(R.id.gl_surface_view);
        annotationGLSurfaceView.setBigData(true);
        downloadingPopupView = new XPopup.Builder(this).asLoading("Downloading......");
        imageClassifyViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ImageClassifyViewModel.class);
        toolbar = findViewById(R.id.toolbar_image_classify);
        imageIdLocationTextView = findViewById(R.id.imageid_location_text_view);
        setSupportActionBar(toolbar);

        updateOptionsMenu();
        updateUI();


        imageClassifyViewModel.getWorkStatus().observe(this, new Observer<ImageClassifyViewModel.WorkStatus>() {
            @Override
            public void onChanged(ImageClassifyViewModel.WorkStatus workStatus) {
                if (workStatus == null) {
                    return;
                }
                switch (workStatus) {
                    case NO_MORE_FILE:
                        hideDownloadingProgressBar();
                        isImageExist = false;
                        ToastEasy("No more file need to process !", Toast.LENGTH_LONG);
                        break;

                    case START_TO_DOWNLOAD_IMAGE:
                        showDownloadingProgressBar();
                        break;

                    case DOWNLOAD_IMAGE_FINISH:
                        hideDownloadingProgressBar();
                        imageClassifyViewModel.openNewFile();
                        break;

                    case IMAGE_FILE_EXPIRED:
                        warning4ExpiredFile();
                        break;
                }
            }
        });

        imageClassifyViewModel.getImageClassifyDataSource().getRatingImageListResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                imageClassifyViewModel.handleRatingImageList(result);
            }
        });

        imageClassifyViewModel.getImageClassifyDataSource().getUploadUserRatingResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                imageClassifyViewModel.handleUploadUserResult(result);
            }
        });

        imageClassifyViewModel.getImageClassifyDataSource().getDownloadSingleRatingImageResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                imageClassifyViewModel.handleDownloadRatingImage(result);
            }
        });

        imageClassifyViewModel.monitorDownloadedImageResult().observe(this, new Observer<ResourceResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult == null){
                    return;
                }
                if (resourceResult.isSuccess()){
                    annotationGLSurfaceView.openFile();
                    ImageInfo imageInfo = imageClassifyViewModel.getCurImageInfo();
                    imageIdLocationTextView.setText(imageInfo.getImageName());
                    annotationGLSurfaceView.setImageInfoInRender(imageInfo.getImageName());
                    isImageExist = true;
                    annotationGLSurfaceView.updateRenderOptions();
                } else {
                    ToastEasy(resourceResult.getError());
                }

            }
        });

        imageClassifyViewModel.monitorUploadedUserResult().observe(this, new Observer<ResourceResult>() {
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult.isSuccess()) {
                    ToastEasy("Upload image successfully !");
                } else if (resourceResult.getError().equals("Expired")) {
                    ToastEasy("The image you just upload is expired.");
                } else {
                    ToastEasy("Upload failed");
                }
            }
        });

    }

        protected void onResume() {
            super.onResume();
            annotationGLSurfaceView.onResume();
        }

        @Override
        protected void onPause() {
            super.onPause();
            annotationGLSurfaceView.onPause();
        }

        @Override
        protected void onRestart() {
            super.onRestart();
            startMusicService();
        }

        @Override
        protected void onStop() {
            super.onStop();
            stopMusicService();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            stopMusicService();
            imageClassifyViewModel.shutDownThreadPool();
        }

        private void startMusicService() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, MusicService.class));
            } else {
                startService(new Intent(this, MusicService.class));
            }
        }

        private void stopMusicService() {
            Intent bgmIntent = new Intent(this, MusicService.class);
            stopService(bgmIntent);
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_classify_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateOptionsMenu() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.image_classify_menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // support back button
            case android.R.id.home:
                finish();
                return true;
            case R.id.file:
                if (imageClassifyViewModel.isLoggedIn()) {
                    openFile();
                } else {
                    ToastEasy("Login first please !");
                }
                playButtonSound();
                return true;

            case R.id.share:
                annotationGLSurfaceView.screenCapture();
                playButtonSound();
                return true;

            case R.id.more:
                moreFunctions();
                playButtonSound();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDownloadingProgressBar() {
        downloadingPopupView.show();
        uiHandler.postDelayed(this::timeOutHandler, 30 * 1000);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideDownloadingProgressBar() {
        downloadingPopupView.dismiss();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void previousFile(){
        navigateFile(false,false,"0","");
    }

    private void nextFile(){
        navigateFile(false,true,"0","");
    }

    private void navigateFile(boolean needUpload, boolean nextFile, String ratingType,String additionalInfo) {
        /* locationType:
             -1: default, no update
             1: horizontal file,
             2: vertical file,
             3.1:
             3.2:
             4.1:
             4.2:

         */
        if (needUpload) {
            imageClassifyViewModel.uploadUserResult(ratingType,additionalInfo);
            }
        if (nextFile) {
            imageClassifyViewModel.nextFile();
        } else {
            imageClassifyViewModel.previousFile();
        }
    }
    private void warning4ExpiredFile() {
        new XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .asConfirm("Warning...",
                        "Current file is expired, will change another file for you.",
                        () -> navigateFile(false, true, "-1",""),
                        () -> navigateFile(false, true, "-1",""))
                .setConfirmText("Confirm")
                .setCancelText("I know")
                .show();
    }

    private void timeOutHandler() {
        downloadingPopupView.dismiss();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void openFile() {
        imageClassifyViewModel.openNewFile();
        }

    private void moreFunctions(){
        new XPopup.Builder(this)
                .maxHeight(1500)
                .asCenterList("More Functions...", new String[] {"Settings"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Settings":
                                        ToastEasy("To be added");
//                                        settings();
                                        break;
                                    default:
                                        ToastEasy("Something wrong with more functions...");
                                }
                            }
                        })
                .show();
    }

    private void settings(){
        new MDDialog.Builder(this)
                .setContentView(R.layout.image_classify_rating)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        PreferenceSetting preferenceSetting = PreferenceSetting.getInstance();
                        PreferenceMusic preferenceMusic = PreferenceMusic.getInstance();
                        PreferenceSoma preferenceSoma = PreferenceSoma.getInstance();

                        SwitchCompat downSampleSwitch = contentView.findViewById(R.id.downSample_mode);
                        SwitchCompat autoUploadSwitch = contentView.findViewById(R.id.autoUpload_mode);
                        IndicatorSeekBar contrastIndicator = contentView.findViewById(R.id.contrast_indicator_seekbar);
                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);

                        downSampleSwitch.setChecked(preferenceSetting.getDownSampleMode());
                        autoUploadSwitch.setChecked(preferenceSoma.getAutoUploadMode());
                        contrastIndicator.setProgress(preferenceSetting.getContrast());
                        bgmVolumeBar.setProgress(preferenceMusic.getBackgroundSound());
                        buttonVolumeBar.setProgress(preferenceMusic.getButtonSound());
                        actionVolumeBar.setProgress(preferenceMusic.getActionSound());

                        autoUploadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                preferenceSoma.setAutoUploadMode(isChecked);
                            }
                        });

                        downSampleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                preferenceSetting.setDownSampleMode(isChecked);
                                annotationGLSurfaceView.updateRenderOptions();
                                annotationGLSurfaceView.requestRender();
                            }
                        });

                        contrastIndicator.setOnSeekChangeListener(new OnSeekChangeListener() {
                            @Override
                            public void onSeeking(SeekParams seekParams) {
                                preferenceSetting.setContrast(seekParams.progress);
                                annotationGLSurfaceView.updateRenderOptions();
                            }
                            @Override
                            public void onStartTrackingTouch(IndicatorSeekBar seekBar) { }
                            @Override
                            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                                contrastSeekBar.setProgress(seekBar.getProgress());
                                annotationGLSurfaceView.requestRender();
                            }
                        });

                        bgmVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                preferenceMusic.setBackgroundSound(progress);
                                updateMusicVolume();
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) { }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) { }
                        });

                        buttonVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                preferenceMusic.setButtonSound(progress);
                                updateMusicVolume();
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) { }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) { }
                        });

                        actionVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                preferenceMusic.setActionSound(progress);
                                updateMusicVolume();
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) { }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) { }
                        });
                    }
                })
                .setNegativeButton("Cancel", v -> { })
                .setPositiveButton("Confirm", v -> {
                    playButtonSound();
                })
                .setTitle("Settings")
                .create()
                .show();
    }

    private void updateUI() {
        if (imageClassifyView == null) {
            // load layout view
            LinearLayout.LayoutParams lpImageClassifyView = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            imageClassifyView = getLayoutInflater().inflate(R.layout.image_classify_rating, null);
            this.addContentView(imageClassifyView, lpImageClassifyView);

            PreferenceSetting preferenceSetting = PreferenceSetting.getInstance();

            ImageButtonExt previousFile = findViewById(R.id.previous_file);
            ImageButtonExt nextFile = findViewById(R.id.next_file);
            contrastSeekBar = (SeekBar) findViewById(R.id.contrast_value);
            SeekBar contrastEnhanceRatio = (SeekBar) findViewById(R.id.contrast_enhance_ratio);
            contrastEnhanceRatio.setProgress(preferenceSetting.getContrastEnhanceRatio());
            contrastEnhanceRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
                    if(fromuser){
                        preferenceSetting.setContrastEnhanceRatio(progress);
                        annotationGLSurfaceView.updateRenderOptions();
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    annotationGLSurfaceView.requestRender();
                }});

            Button frontBtn = findViewById(R.id.front_ic);
            frontBtn.setOnClickListener(v -> {
                annotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eFront);
            });

            Button backBtn = findViewById(R.id.back_ic);
            backBtn.setOnClickListener(v -> {
                annotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eBack);
            });

            Button leftBtn = findViewById(R.id.left_ic);
            leftBtn.setOnClickListener(v -> {
                annotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eLeft);
            });

            Button rightBtn = findViewById(R.id.right_ic);
            rightBtn.setOnClickListener(v -> {
                annotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eRight);
            });

            Button upBtn = findViewById(R.id.up_ic);
            upBtn.setOnClickListener(v -> {
                annotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eUp);
            });

            Button downBtn = findViewById(R.id.down_ic);
            downBtn.setOnClickListener(v -> {
                annotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eDown);
            });

            layoutSubcategories3 = findViewById(R.id.layoutSubcategoryWindow3);
            layoutSubcategories4 = findViewById(R.id.layoutSubcategoryWindow4);


            Button btnHorizontal = findViewById(R.id.btnHorizontal);
            Button btnVertical = findViewById(R.id.btnVertical);
            Button btnSlanting = findViewById(R.id.btnSlanting);
            Button btnOther = findViewById(R.id.btnOther);
            Button btnInterceptive = findViewById(R.id.btnInterceptive);
            Button btnUntruncated = findViewById(R.id.btnUntruncated);
            Button btnSpecial = findViewById(R.id.btnSpecial);
            editTextRemark = findViewById(R.id.editTextRemark);


            contrastSeekBar.setProgress(preferenceSetting.getContrast());
            contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
                    if(fromuser){
                        preferenceSetting.setContrast(progress);
                        annotationGLSurfaceView.updateRenderOptions();
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    annotationGLSurfaceView.requestRender();
                }});

            previousFile.setOnClickListener(v -> previousFile());
            nextFile.setOnClickListener(v -> nextFile());

            btnHorizontal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isImageExist){
                        navigateFile(true,true,"1_horizontal","");
                    } else{
                        ToastEasy("please open image file first");
                    }
                }
            });

            btnVertical.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isImageExist){
                    navigateFile(true,true,"2_vertical","");
                    }else{
                        ToastEasy("please open image file first");
                    }
                }
            });

            btnSlanting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(layoutSubcategories3.getVisibility() == View.GONE) {
                        layoutSubcategories3.setVisibility(View.VISIBLE);

                    }else{
                        layoutSubcategories3.setVisibility(View.GONE);
                    }
                }
            });

            btnOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(layoutSubcategories4.getVisibility() == View.GONE){
                        layoutSubcategories4.setVisibility(View.VISIBLE);
                        btnSpecial.setVisibility(View.VISIBLE);
                        editTextRemark.setVisibility(View.VISIBLE);
                    }else{
                        layoutSubcategories4.setVisibility(View.GONE);
                    }
                }
            });

            btnInterceptive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isImageExist){
                    navigateFile(true,true,"3.1_interceptive","");}
                    else {
                        ToastEasy("please open image first");
                    }
                }
            });

            btnUntruncated.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isImageExist) {
                        navigateFile(true, true, "3.2_untruncated", "");
                    }else {
                        ToastEasy("please open image first");
                    }
                }
            });


            btnSpecial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 当特殊按钮可点击且被点击时执行上传数据到服务器的操作
                    if (btnSpecial.isEnabled()) {
                        if (!editTextRemark.getText().toString().isEmpty()) {
                            if (isImageExist) {
                                String remark = editTextRemark.getText().toString();
                                String utf8String = null;
                                try {
                                    utf8String = new String(remark.getBytes("UTF-8"), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                                navigateFile(true, true, "4.2_special", utf8String);
                                editTextRemark.setText("");
                                btnSpecial.setEnabled(false);
                                btnSpecial.setBackgroundColor(Color.GRAY);
                            } else {
                                ToastEasy("please open image first");
                            }
                        }
                    }
                }
            });

            editTextRemark.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // do nothing
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // do nothing
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!editable.toString().isEmpty()) {
                        // 如果编辑框有内容，则使特殊按钮可点击，设置背景为橙色
                        btnSpecial.setEnabled(true);
                        btnSpecial.setBackgroundColor(Color.parseColor("#F4A460"));
                    } else {
                        // 如果编辑框为空，则特殊按钮不可点击，设置背景为灰色
                        btnSpecial.setEnabled(false);
                        btnSpecial.setBackgroundColor(Color.GRAY);
                    }
                }
            });


        } else{
            imageClassifyView.setVisibility(View.VISIBLE);
        }
        }

    public static void start(Context context) {
        Intent intent = new Intent(context, ImageClassifyActivity.class);
        context.startActivity(intent);
    }


}









