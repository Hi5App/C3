package com.penglab.hi5.core.ui.ImageClassify;


import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.playButtonSound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.basic.utils.view.ImageButtonExt;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.data.dataStore.PreferenceMusic;
import com.penglab.hi5.data.dataStore.PreferenceSetting;
import com.penglab.hi5.data.dataStore.PreferenceSoma;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.UnsupportedEncodingException;

import cn.carbs.android.library.MDDialog;

public class ImageClassifyActivity extends AppCompatActivity {
    private AnnotationGLSurfaceView mAnnotationGLSurfaceView;
    private ImageClassifyViewModel mImageClassifyViewModel;
    private TextView mImageIdLocationTextView;
    private View mImageClassifyView;
    private Toolbar mToolbar;
    private SeekBar mContrastSeekBar;
    private LinearLayout layoutSubcategories3, layoutSubcategories4;
    private EditText mEditTextRemark;

    public static void start(Context context) {
        Intent intent = new Intent(context, ImageClassifyActivity.class);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_classify);

        mToolbar = findViewById(R.id.toolbar_image_classify);
        setSupportActionBar(mToolbar);

        mAnnotationGLSurfaceView = findViewById(R.id.gl_surface_view);
        mAnnotationGLSurfaceView.setBigData(true);

        mImageIdLocationTextView = findViewById(R.id.imageid_location_text_view);

        mImageClassifyViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ImageClassifyViewModel.class);

        updateUI();

        mImageClassifyViewModel.acquireCurrentImage().observe(this, imageInfo -> {
            if (imageInfo == null) {
                return;
            }

            if (!mImageClassifyViewModel.isImageFileExist(imageInfo)) {
                Log.e("ImageClassifyActivity", "Image file not exist, download it first.");
                mImageClassifyViewModel.downloadImageFileSync(imageInfo);
                return;

            }

            String filePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image/" + imageInfo.ImageName;
            String fileName = FileManager.getFileName(filePath);
            FileType fileType = FileManager.getFileType(filePath);
            mImageClassifyViewModel.getImageInfoRepository().getBasicImage().setFileInfo(fileName, new FilePath<String>(filePath), fileType);

            mAnnotationGLSurfaceView.openFile();
            mImageIdLocationTextView.setText(imageInfo.ImageName);
            mAnnotationGLSurfaceView.setImageInfoInRender(imageInfo.ImageName);
            mAnnotationGLSurfaceView.updateRenderOptions();
        });

        mImageClassifyViewModel.acquireImagesManually();
    }

    public void setSupportActionBar(Toolbar mToolbar) {
        mToolbar.getMenu().clear();
        mToolbar.inflateMenu(R.menu.image_classify_menu);
    }

    protected void onResume() {
        super.onResume();
        mAnnotationGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAnnotationGLSurfaceView.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateUI() {
        if (mImageClassifyView == null) {
            // load layout view
            LinearLayout.LayoutParams lpImageClassifyView = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            mImageClassifyView = getLayoutInflater().inflate(R.layout.image_classify_rating, null);
            this.addContentView(mImageClassifyView, lpImageClassifyView);

            PreferenceSetting preferenceSetting = PreferenceSetting.getInstance();

            ImageButtonExt previousFile = findViewById(R.id.previous_file);
            ImageButtonExt nextFile = findViewById(R.id.next_file);
            mContrastSeekBar = (SeekBar) findViewById(R.id.contrast_value);
            SeekBar contrastEnhanceRatio = (SeekBar) findViewById(R.id.contrast_enhance_ratio);
            contrastEnhanceRatio.setProgress(preferenceSetting.getContrastEnhanceRatio());
            contrastEnhanceRatio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
                    if (fromuser) {
                        preferenceSetting.setContrastEnhanceRatio(progress);
                        mAnnotationGLSurfaceView.updateRenderOptions();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mAnnotationGLSurfaceView.requestRender();
                }
            });

            Button frontBtn = findViewById(R.id.front_ic);
            frontBtn.setOnClickListener(v -> {
                mAnnotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eFront);
            });

            Button backBtn = findViewById(R.id.back_ic);
            backBtn.setOnClickListener(v -> {
                mAnnotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eBack);
            });

            Button leftBtn = findViewById(R.id.left_ic);
            leftBtn.setOnClickListener(v -> {
                mAnnotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eLeft);
            });

            Button rightBtn = findViewById(R.id.right_ic);
            rightBtn.setOnClickListener(v -> {
                mAnnotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eRight);
            });

            Button upBtn = findViewById(R.id.up_ic);
            upBtn.setOnClickListener(v -> {
                mAnnotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eUp);
            });

            Button downBtn = findViewById(R.id.down_ic);
            downBtn.setOnClickListener(v -> {
                mAnnotationGLSurfaceView.setFaceDirection(AnnotationRender.FaceDirection.eDown);
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
            mEditTextRemark = findViewById(R.id.editTextRemark);


            mContrastSeekBar.setProgress(preferenceSetting.getContrast());
            mContrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromuser) {
                    if (fromuser) {
                        preferenceSetting.setContrast(progress);
                        mAnnotationGLSurfaceView.updateRenderOptions();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mAnnotationGLSurfaceView.requestRender();
                }
            });

            previousFile.setOnClickListener(v -> previousFile());
            nextFile.setOnClickListener(v -> nextFile());

            btnHorizontal.setOnClickListener(v -> {
                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                    navigateFile(true, true, "1_horizontal", "");
                } else {
                    ToastEasy("please open image file first");
                }
            });

            btnVertical.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                        navigateFile(true, true, "2_vertical", "");
                    } else {
                        ToastEasy("please open image file first");
                    }
                }
            });

            btnSlanting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutSubcategories3.getVisibility() == View.GONE) {
                        layoutSubcategories3.setVisibility(View.VISIBLE);

                    } else {
                        layoutSubcategories3.setVisibility(View.GONE);
                    }
                }
            });

            btnOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutSubcategories4.getVisibility() == View.GONE) {
                        layoutSubcategories4.setVisibility(View.VISIBLE);
                        btnSpecial.setVisibility(View.VISIBLE);
                        mEditTextRemark.setVisibility(View.VISIBLE);
                    } else {
                        layoutSubcategories4.setVisibility(View.GONE);
                    }
                }
            });

            btnInterceptive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                        navigateFile(true, true, "3.1_interceptive", "");
                    } else {
                        ToastEasy("please open image first");
                    }
                }
            });

            btnUntruncated.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                        navigateFile(true, true, "3.2_untruncated", "");
                    } else {
                        ToastEasy("please open image first");
                    }
                }
            });


            btnSpecial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 当特殊按钮可点击且被点击时执行上传数据到服务器的操作
                    if (btnSpecial.isEnabled()) {
                        if (!mEditTextRemark.getText().toString().isEmpty()) {
                            if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                                String remark = mEditTextRemark.getText().toString();
                                String utf8String = null;
                                try {
                                    utf8String = new String(remark.getBytes("UTF-8"), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                                navigateFile(true, true, "4.2_special", utf8String);
                                mEditTextRemark.setText("");
                                btnSpecial.setEnabled(false);
                                btnSpecial.setBackgroundColor(Color.GRAY);
                            } else {
                                ToastEasy("please open image first");
                            }
                        }
                    }
                }
            });

            mEditTextRemark.addTextChangedListener(new TextWatcher() {
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


        } else {
            mImageClassifyView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_classify_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void navigateFile(boolean needUpload, boolean nextFile, String ratingType, String
            additionalInfo) {
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
            mImageClassifyViewModel.uploadUserRatingResult(ratingType, additionalInfo);
        }
        if (nextFile) {
            mImageClassifyViewModel.acquireNextImage();
        } else {
            mImageClassifyViewModel.acquirePreviousImage();
        }
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
                if (mImageClassifyViewModel.isLoggedIn()) {
                    mImageClassifyViewModel.acquireImagesManually();
                } else {
                    ToastEasy("PLease login first!");
                }
                playButtonSound();
                return true;

            case R.id.share:
                mAnnotationGLSurfaceView.screenCapture();
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

    private void previousFile() {
        navigateFile(false, false, "0", "");
    }

    private void nextFile() {
        navigateFile(false, true, "0", "");
    }

    private void moreFunctions() {
        new XPopup.Builder(this)
                .maxHeight(1500)
                .asCenterList("More Functions...", new String[]{"Settings"},
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

}
//public class ImageClassifyActivity  extends AppCompatActivity {
//    private mAnnotationGLSurfaceView mAnnotationGLSurfaceView;
//    private ImageClassifyViewModel imageClassifyViewModel;
//    private View mImageClassifyView;
//    private LinearLayout layoutSubcategories3, layoutSubcategories4;
//    private EditText editTextRemark;

//    private TextView imageIdLocationTextView;
//    private final Handler uiHandler = new Handler();
//    private BasePopupView downloadingPopupView;
//
//    private boolean isImageExist = false;
//
//
//    protected void onCreate(Bundle savedInstanceState) {
//        downloadingPopupView = new XPopup.Builder(this).asLoading("Downloading......");

//
//        imageClassifyViewModel.getWorkStatus().observe(this, new Observer<ImageClassifyViewModel.WorkStatus>() {
//            @Override
//            public void onChanged(ImageClassifyViewModel.WorkStatus workStatus) {
//                if (workStatus == null) {
//                    return;
//                }
//                switch (workStatus) {
//                    case NO_MORE_FILE:
//                        hideDownloadingProgressBar();
//                        isImageExist = false;
//                        ToastEasy("No more file need to process !", Toast.LENGTH_LONG);
//                        break;
//
//                    case START_TO_DOWNLOAD_IMAGE:
//                        showDownloadingProgressBar();
//                        break;
//
//                    case DOWNLOAD_IMAGE_FINISH:
//                        hideDownloadingProgressBar();
//                        imageClassifyViewModel.openNewFile();
//                        break;
//
//                    case IMAGE_FILE_EXPIRED:
//                        warning4ExpiredFile();
//                        break;
//                }
//            }
//        });
//
//        imageClassifyViewModel.getImageClassifyDataSource().getRatingImageListResult().observe(this, new Observer<Result>() {
//            @Override
//            public void onChanged(Result result) {
//                if (result == null) {
//                    return;
//                }
//                imageClassifyViewModel.handleRatingImageList(result);
//            }
//        });
//
//        imageClassifyViewModel.getImageClassifyDataSource().getUploadUserRatingResult().observe(this, new Observer<Result>() {
//            @Override
//            public void onChanged(Result result) {
//                imageClassifyViewModel.handleUploadUserResult(result);
//            }
//        });
//
//        imageClassifyViewModel.getImageClassifyDataSource().getDownloadSingleRatingImageResult().observe(this, new Observer<Result>() {
//            @Override
//            public void onChanged(Result result) {
//                if (result == null) {
//                    return;
//                }
//                imageClassifyViewModel.handleDownloadRatingImage(result);
//            }
//        });
//
//        imageClassifyViewModel.monitorDownloadedImageResult().observe(this, new Observer<ResourceResult>() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onChanged(ResourceResult resourceResult) {
//                if (resourceResult == null){
//                    return;
//                }
//                if (resourceResult.isSuccess()){
//                    mAnnotationGLSurfaceView.openFile();
//                    ImageInfo imageInfo = imageClassifyViewModel.getCurImageInfo();
//                    imageIdLocationTextView.setText(imageInfo.getImageName());
//                    mAnnotationGLSurfaceView.setImageInfoInRender(imageInfo.getImageName());
//                    isImageExist = true;
//                    mAnnotationGLSurfaceView.updateRenderOptions();
//                } else {
//                    ToastEasy(resourceResult.getError());
//                }
//
//            }
//        });
//
//        imageClassifyViewModel.monitorUploadedUserResult().observe(this, new Observer<ResourceResult>() {
//            @Override
//            public void onChanged(ResourceResult resourceResult) {
//                if (resourceResult.isSuccess()) {
//                    ToastEasy("Upload image successfully !");
//                } else if (resourceResult.getError().equals("Expired")) {
//                    ToastEasy("The image you just upload is expired.");
//                } else {
//                    ToastEasy("Upload failed");
//                }
//            }
//        });
//
//    }
//
//
//    private void showDownloadingProgressBar() {
//        downloadingPopupView.show();
//        uiHandler.postDelayed(this::timeOutHandler, 30 * 1000);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    }
//
//    private void hideDownloadingProgressBar() {
//        downloadingPopupView.dismiss();
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    }
//

//

//    private void warning4ExpiredFile() {
//        new XPopup.Builder(this)
//                .dismissOnTouchOutside(false)
//                .asConfirm("Warning...",
//                        "Current file is expired, will change another file for you.",
//                        () -> navigateFile(false, true, "-1",""),
//                        () -> navigateFile(false, true, "-1",""))
//                .setConfirmText("Confirm")
//                .setCancelText("I know")
//                .show();
//    }
//
//    private void timeOutHandler() {
//        downloadingPopupView.dismiss();
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//    }
//
//    private void openFile() {
//        imageClassifyViewModel.openNewFile();
//        }
//










