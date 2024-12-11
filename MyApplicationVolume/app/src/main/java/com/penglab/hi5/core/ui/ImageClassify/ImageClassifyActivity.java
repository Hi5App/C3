package com.penglab.hi5.core.ui.ImageClassify;

import static com.penglab.hi5.chat.nim.main.helper.MessageHelper.TAG;
import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.getContext;
import static com.penglab.hi5.core.Myapplication.playButtonSound;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.RangeSlider;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
//import com.penglab.hi5.Manifest;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.basic.utils.view.ImageButtonExt;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.ImageClassify.adapter.ClassifySolutionTableAdapter;
import com.penglab.hi5.core.ui.ImageClassify.adapter.ImageClassifyTableAdapter;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.data.dataStore.PreferenceSetting;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import cn.carbs.android.library.MDDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;


public class ImageClassifyActivity extends AppCompatActivity {
    private AnnotationGLSurfaceView mAnnotationGLSurfaceView;
    private ImageClassifyViewModel mImageClassifyViewModel;
    private TextView mImageIdLocationTextView;
    private View mImageClassifyView;
    private Toolbar mToolbar;
    private SeekBar mContrastSeekBar;
    private LinearLayout layoutSubcategories3, layoutSubcategories4;
    private EditText mEditTextRemark;

    private Spinner solutionSpinner;
    private Spinner userSpinner;

    private RecyclerView timeRecycleView;

    private EditText start_time_edit_text,end_time_edit_text;

    private Button queryButton,downloadButton;

    private Button btnSpecial;

    private CheckBox showDetailsCheckbox;

    private boolean currentShowDetails = false;
    private LoadingPopupView mDownloadingPopupView;

    private Button solutionAddButton;

    private Button solutionDeleteButton;

    private RecyclerView solutionRecycleView;
    private HashMap<String, String> classifySolutionInfoMap;
    private List<ClassifySolutionInfo> classifySolutionInfoList;
    private ClassifySolutionTableAdapter classifySolutionAdapter;
    private String curSelectedClassFirst;
    private String additionalRatingDescription;

    private final PreferenceSetting preferenceSetting = PreferenceSetting.getInstance();

    private final Handler uiHandler = new Handler();
    private final Timer mRenderTimer = new Timer();

    private final TimerTask mRenderTask = new TimerTask() {
        @Override
        public void run() {
            mAnnotationGLSurfaceView.requestRender();
        }
    };

    private final Timer mDownloadControlTimer = new Timer();

    private final TimerTask mDownloadControlTask = new TimerTask() {
        @Override
        public void run() {
            if (mImageClassifyViewModel.isNextImageDequeDownloadCompleted() && !mImageClassifyViewModel.getNextRatingImagesInfoDeque().isEmpty()) {
                uiHandler.post(() -> {
                    hideDownloadingProgressBar();
                    if (mImageClassifyViewModel.acquireCurrentImage().getValue() == null) {
                        mImageClassifyViewModel.acquireNextImage();
                    }
                });
            }else{
                mImageClassifyViewModel.getNextRatingImagesInfoDeque().forEach(ratingImageInfo -> {
                    if (!mImageClassifyViewModel.isImageFileExist(ratingImageInfo) && !ratingImageInfo.IsDownloading) {
                        ratingImageInfo.IsDownloading = true;
                        mImageClassifyViewModel.downloadImageFileAsync(ratingImageInfo);
                    }
                });
            }
        }
    };

    public static void start(Context context) {
        Intent intent = new Intent(context, ImageClassifyActivity.class);
        context.startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_classify);

        mDownloadingPopupView = new XPopup.Builder(this).asLoading("Downloading......");
        mDownloadingPopupView.setFocusable(false);

        mToolbar = findViewById(R.id.toolbar_image_classify);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mAnnotationGLSurfaceView = findViewById(R.id.gl_surface_view);
        mAnnotationGLSurfaceView.setBigData(true);

        mImageIdLocationTextView = findViewById(R.id.imageid_location_text_view);

        updateUI();

        Button openPopupButton = mImageClassifyView.findViewById(R.id.range_cut_button_ic);

        openPopupButton.setOnClickListener(v -> {
            PopupWindow popupWindow = new PopupWindow(LayoutInflater.from(ImageClassifyActivity.this).inflate(R.layout.popup_rangecut, null)
                    , ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(ImageClassifyActivity.this, R.drawable.global_blue_round_box_4));
            popupWindow.showAsDropDown(openPopupButton, Gravity.CENTER,0,0);

            RangeSlider xRangeSlider = popupWindow.getContentView().findViewById(R.id.x_cut_slider);
            xRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
                List<Float> values = xRangeSlider.getValues();
                mAnnotationGLSurfaceView.setCutx_left_value(values.get(0)/100);
                mAnnotationGLSurfaceView.setCutx_right_value(values.get(1)/100);
                mAnnotationGLSurfaceView.requestRender();
            });

            RangeSlider yRangeSlider = popupWindow.getContentView().findViewById(R.id.y_cut_slider);
            yRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
                List<Float> values = yRangeSlider.getValues();
                mAnnotationGLSurfaceView.setCuty_left_value(values.get(0) /100);
                mAnnotationGLSurfaceView.setCuty_right_value(values.get(1)/100);
                mAnnotationGLSurfaceView.requestRender();
            });

            RangeSlider zRangeSlider = popupWindow.getContentView().findViewById(R.id.z_cut_slider);
            zRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
                List<Float> values = zRangeSlider.getValues();
                mAnnotationGLSurfaceView.setCutz_left_value(values.get(0)/100);
                mAnnotationGLSurfaceView.setCutz_right_value(values.get(1) /100);
                mAnnotationGLSurfaceView.requestRender();
            });
        });

        mImageClassifyViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ImageClassifyViewModel.class);

        mImageClassifyViewModel.acquireCurrentImage().observe(this, this::renderImageFile);

        mImageClassifyViewModel.acquireReScheduledDownloadImageInfo().observe(this, imageInfo -> {
            if (imageInfo == null) {
                hideDownloadingProgressBar();
                RatingImageInfo currentImageInfo = mImageClassifyViewModel.acquireCurrentImage().getValue();
                renderImageFile(currentImageInfo);
            } else {
                showDownloadingProgressBar();
            }
        });

        mImageClassifyViewModel.getmUserRatingResultTable().observe(this, userRatingResultInfos -> {
            if(userRatingResultInfos == null || userRatingResultInfos.isEmpty()){
                Toast.makeText(getApplicationContext(),"no data available",Toast.LENGTH_SHORT);
                return;
            }
            generateExcel(userRatingResultInfos,currentShowDetails);
        });

        mImageClassifyViewModel.acquireImagesManually();

        showDownloadingProgressBar();

        // 开始定时器，延迟0毫秒后开始，每隔500毫秒执行一次
        mRenderTimer.schedule(mRenderTask, 0, 500);
        mDownloadControlTimer.schedule(mDownloadControlTask, 0, 500);
    }

    private void renderImageFile(RatingImageInfo imageInfo) {
        if (imageInfo == null) {
            return;
        }

        if (!mImageClassifyViewModel.isImageFileExist(imageInfo)) {
            Log.e("ImageClassifyActivity", "Image file not exist, download it first. Image: " + imageInfo.ImageName + " , Reschedule download task...");
            mImageClassifyViewModel.reScheduleDownloadImageFileAsync(imageInfo);
            return;
        }

        String filePath = Myapplication.getContext().getExternalFilesDir(null) + "/Image/" + imageInfo.ImageName;
        String fileName = FileManager.getFileName(filePath);
        FileType fileType = FileManager.getFileType(filePath);
        mImageClassifyViewModel.getImageInfoRepository().getBasicImage().setFileInfo(fileName, new FilePath<>(filePath), fileType);

        mAnnotationGLSurfaceView.openFile();
        mImageIdLocationTextView.setText(imageInfo.ImageName);
        mAnnotationGLSurfaceView.setImageInfoInRender(imageInfo.ImageName);
        mAnnotationGLSurfaceView.updateRenderOptions();

        mAnnotationGLSurfaceView.requestRender();
    }

    private void showDownloadingProgressBar() {
        mDownloadingPopupView.show();
        uiHandler.postDelayed(this::timeOutHandler, 60 * 1000);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideDownloadingProgressBar() {
        mDownloadingPopupView.dismiss();
        uiHandler.removeCallbacks(this::timeOutHandler);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void timeOutHandler() {
        mDownloadingPopupView.dismiss();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        ToastEasy("Download images time out! Please try again!");
    }

    public void setSupportActionBar(Toolbar mToolbar) {
        if(mToolbar.getMenu().size() != 0){
            mToolbar.getMenu().clear();
        }
        // 加载菜单
        mToolbar.inflateMenu(R.menu.image_classify_menu);
        super.setSupportActionBar(mToolbar);
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

            ImageButtonExt previousFile = findViewById(R.id.previous_file);
            ImageButtonExt nextFile = findViewById(R.id.next_file);

            ImageButton downSampleMode = findViewById(R.id.downSample_mode);
            downSampleMode.setOnClickListener(v -> {
                // 在这里处理点击事件的逻辑
                boolean newCheckedState = !preferenceSetting.getDownSampleMode();
                preferenceSetting.setDownSampleMode(newCheckedState);
                // 根据新的状态更新图像图案
                if (newCheckedState) {
                    downSampleMode.setImageResource(R.drawable.ic_iamge_downsample_foreground);
                } else {
                    downSampleMode.setImageResource(R.drawable.ic_iamge_downsample_off_foreground);
                }
                // 更新相关视图
                mAnnotationGLSurfaceView.updateRenderOptions();
                mAnnotationGLSurfaceView.requestRender();
            });
            downSampleMode.setImageResource(preferenceSetting.getDownSampleMode() ? R.drawable.ic_iamge_downsample_foreground : R.drawable.ic_iamge_downsample_off_foreground);

            mContrastSeekBar = findViewById(R.id.contrast_value);
            SeekBar contrastEnhanceRatio = findViewById(R.id.contrast_enhance_ratio);
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

//            layoutSubcategories3 = findViewById(R.id.layoutSubcategoryWindow3);
//            layoutSubcategories4 = findViewById(R.id.layoutSubcategoryWindow4);
//
//
//            Button btnHorizontal = findViewById(R.id.btnHorizontal);
//            Button btnVertical = findViewById(R.id.btnVertical);
//            Button btnSlanting = findViewById(R.id.btnSlanting);
//            Button btnOther = findViewById(R.id.btnOther);
//            Button btnInterceptive = findViewById(R.id.btnInterceptive);
//            Button btnUntruncated = findViewById(R.id.btnUntruncated);
//            btnSpecial = findViewById(R.id.btnSpecial);
//            mEditTextRemark = findViewById(R.id.editTextRemark);
//            updateButtonState(true);

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

//            btnHorizontal.setOnClickListener(v -> {
//                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
//                    navigateFile(true, true, "1_horizontal", "");
//                } else {
//                    ToastEasy("please open image file first");
//                }
//            });
//
//            btnVertical.setOnClickListener(v -> {
//                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
//                    navigateFile(true, true, "2_vertical", "");
//                } else {
//                    ToastEasy("please open image file first");
//                }
//            });
//
//            btnSlanting.setOnClickListener(v -> {
//                if (layoutSubcategories3.getVisibility() == View.GONE) {
//                    layoutSubcategories3.setVisibility(View.VISIBLE);
//
//                } else {
//                    layoutSubcategories3.setVisibility(View.GONE);
//                }
//            });
//
//            btnOther.setOnClickListener(v -> {
//                if (layoutSubcategories4.getVisibility() == View.GONE) {
//                    layoutSubcategories4.setVisibility(View.VISIBLE);
//                    btnSpecial.setVisibility(View.VISIBLE);
//                    mEditTextRemark.setVisibility(View.VISIBLE);
//                } else {
//                    layoutSubcategories4.setVisibility(View.GONE);
//                }
//            });
//
//            btnInterceptive.setOnClickListener(v -> {
//                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
//                    navigateFile(true, true, "3.1_interceptive", "");
//                    layoutSubcategories3.setVisibility(View.GONE);
//                } else {
//                    ToastEasy("please open image first");
//                }
//            });
//
//            btnUntruncated.setOnClickListener(v -> {
//                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
//                    navigateFile(true, true, "3.2_untruncated", "");
//                    layoutSubcategories3.setVisibility(View.GONE);
//                } else {
//                    ToastEasy("please open image first");
//                }
//            });


//            btnSpecial.setOnClickListener(v -> {
//                if (btnSpecial.isEnabled()) {
//                    String remark = mEditTextRemark.getText().toString();
//                    if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
//                        String utf8String = new String(remark.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
//                        navigateFile(true, true, "4.1_special", utf8String);
//                        mEditTextRemark.setText("other"); // 重设为默认值
//                        layoutSubcategories4.setVisibility(View.GONE);
//                    } else {
//                        ToastEasy("please open image first");
//                    }
//                }
//            });
//
//            mEditTextRemark.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    // do nothing
//                }
//
//                @Override
//                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                    // do nothing
//                }
//
//                @Override
//                public void afterTextChanged(Editable editable) {
//                    updateButtonState(!editable.toString().isEmpty());
//                }
//            });
            ImageButton solutionButton = findViewById(R.id.get_classify_solution_button);
            solutionButton.setOnClickListener(v -> {
                classifySoltions();
            });

            ImageButton attachmentButton = findViewById(R.id.open_classify_attachment_button);
            attachmentButton.setOnClickListener(v -> {
                // 创建对话框视图
                View dialogView = getLayoutInflater().inflate(R.layout.image_classify_attachment, null);
                EditText additionInfoEditText = dialogView.findViewById(R.id.additional_info_edit_text_view);
                additionInfoEditText.setText(additionalRatingDescription);

                // 创建对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Additional Info")
                        .setView(dialogView)
                        .setPositiveButton("OK", (dialog, which) -> {
                            additionalRatingDescription = additionInfoEditText.getText().toString().trim();
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            });

        } else {
            mImageClassifyView.setVisibility(View.VISIBLE);
        }
    }

    private void updateButtonState(boolean isEnabled) {
        btnSpecial.setEnabled(isEnabled);
        if (isEnabled) {
            btnSpecial.setBackgroundColor(Color.parseColor("#F4A460"));
        } else {
            btnSpecial.setBackgroundColor(Color.GRAY);
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
        String selectedSolutionName = getSelectedSolutionName();
        if (needUpload) {
            mImageClassifyViewModel.uploadUserRatingResult(selectedSolutionName, ratingType, additionalInfo);
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
                Log.e(TAG,"enter file");
                if (mImageClassifyViewModel.isLoggedIn()) {
                    mImageClassifyViewModel.acquireImagesManually();
                } else {
                    ToastEasy("PLease login first!");
                }
                return true;

            case R.id.share:
                mAnnotationGLSurfaceView.screenCapture();
                return true;

            case R.id.more_settings:
                settings();
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

    public void settings() {
        List<ClassifySolutionInfo> classifySolutionInfos = mImageClassifyViewModel.requestRatingSolutionList();
        assembleSolutions(classifySolutionInfos);
        new MDDialog.Builder(this)
                .setContentView(R.layout.image_classify_setting)
                .setContentViewOperator(contentView -> {
                    start_time_edit_text = contentView.findViewById(R.id.start_time_edit_text);
                    end_time_edit_text = contentView.findViewById(R.id.end_time_edit_text);
                    solutionSpinner = contentView.findViewById(R.id.solution_spinner);
                    userSpinner = contentView.findViewById(R.id.user_spinner);
                    queryButton = contentView.findViewById(R.id.query_button);
                    downloadButton = contentView.findViewById(R.id.download_button);
                    timeRecycleView = contentView.findViewById(R.id.recycler_view_table);
                    showDetailsCheckbox = contentView.findViewById(R.id.show_details_checkbox);

                    List<String> solutionNames = getSolutionNameList();
                    setupSolutionSpinner(solutionNames, solutionSpinner);
                    String selectedSolutionName = (String) solutionSpinner.getSelectedItem();
                    List<String> userNames = mImageClassifyViewModel.requestRatingUserNameList(selectedSolutionName);
                    setupUserSpinner(userNames, userSpinner);

                    start_time_edit_text.setOnClickListener(v -> showDateTimePicker(start_time_edit_text));
                    end_time_edit_text.setOnClickListener(v -> showDateTimePicker(end_time_edit_text));
                    queryButton.setOnClickListener(v -> {
                        // Fetch data from server and display in table
                        fetchDataFromServer(showDetailsCheckbox.isChecked());
                    });
                    downloadButton.setOnClickListener(v -> {
                        Log.e(TAG,"DOWNLOAD BEGIN!");
                        exportDataToExcel();
                    });
                }).setNegativeButton("Cancel", v -> { })
                        .setPositiveButton("Confirm", v -> {
                            playButtonSound();
                        })
                        .setTitle("QueryRatingResults")
                        .create()
                        .show();
    }

    public void setupUserSpinner(List<String> userNames, Spinner spinner) {
        if(spinner == null){
            return;
        }
        userNames.add("All");
        // 创建一个 ArrayAdapter 来绑定数据
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    public void setupSolutionSpinner(List<String> solutionNames, Spinner spinner){
        if(spinner == null){
            return;
        }
        solutionNames.add("All");
        // 创建一个 ArrayAdapter 来绑定数据
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, solutionNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 当选择项发生变化时触发的事件
                String selectedItem = parentView.getItemAtPosition(position).toString();
                List<String> userNames = mImageClassifyViewModel.requestRatingUserNameList(selectedItem);
                setupUserSpinner(userNames, userSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 当没有选择项时触发的事件
            }
        });
    }

    public void showDateTimePicker(final EditText editText) {
        final View dialogView = View.inflate(this, R.layout.data_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view -> {
            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
            TimePicker timePicker = dialogView.findViewById(R.id.time_picker);

            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                    datePicker.getMonth(),
                    datePicker.getDayOfMonth(),
                    timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute());

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Desired format
            String dateTime = formatter.format(calendar.getTime());
            editText.setText(dateTime);

            alertDialog.dismiss();
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    public void fetchDataFromServer(boolean showDetails) {
        this.currentShowDetails = showDetails;
        String queryUserName = (String) userSpinner.getSelectedItem();
        String queryStartTime = start_time_edit_text.getText().toString();
        String queryEndTime = end_time_edit_text.getText().toString();
        String querySolutionName = (String) solutionSpinner.getSelectedItem();
        mImageClassifyViewModel.requestRatingTable(querySolutionName, queryUserName, queryStartTime, queryEndTime);
    }


    public void generateExcel(List<UserRatingResultInfo> userRatingResultInfos, boolean showDetails) {
        if(timeRecycleView == null) {
            Toast.makeText(this, "RecyclerView not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check permission
        if (ContextCompat.checkSelfPermission(ImageClassifyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(ImageClassifyActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            // Pass the data to RecyclerView adapter with callback implementation
            ImageClassifyTableAdapter adapter = new ImageClassifyTableAdapter(this,userRatingResultInfos, new ImageClassifyTableAdapter.DataCallback() {
                @Override
                public void onTotalCountAvailable(int totalCount) {
                    if (!showDetails) {
                        Toast.makeText(ImageClassifyActivity.this, "Total count: " + totalCount, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onDetailAvailable(List<UserRatingResultInfo> details) {
                    if (showDetails) {
                        // Logic to display details or create an Excel file
                        Toast.makeText(ImageClassifyActivity.this, "Details are available in console/log", Toast.LENGTH_LONG).show();
                        for (UserRatingResultInfo detail : details) {
                            Log.i("Detail", "Image: " + detail.imageName + ", Rating: " + detail.ratingEnum);
                        }
                    }
                }
            });
            adapter.setShowDetails(showDetailsCheckbox.isChecked());
            // 设置布局管理器
            timeRecycleView.setLayoutManager(new LinearLayoutManager(this));
            timeRecycleView.setAdapter(adapter);
        }
    }

    public void exportDataToExcel() {
        if (timeRecycleView == null) {
            Toast.makeText(this, "RecyclerView not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageClassifyTableAdapter adapter = (ImageClassifyTableAdapter) timeRecycleView.getAdapter();
        if (adapter != null) {
            List<UserRatingResultInfo> data = adapter.getUserRatingResultInfos();
            if (data == null || data.isEmpty()) {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                saveCsvFile(data);  // Call the function to save data to Excel
            }
        } else {
            Toast.makeText(this, "Adapter is not set or data is empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveCsvFile(List<UserRatingResultInfo> userRatingResultInfos) {
        if (userRatingResultInfos == null || userRatingResultInfos.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        // 文件路径
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File csvFile = new File(downloadDir, "UserRatings.csv");

        try (FileWriter writer = new FileWriter(csvFile)) {
            // 写入标题头
            writer.append("Image Name,User Name,Rating,Additional Rating Description,Upload Time\n");

            // 写入数据
            for (UserRatingResultInfo info : userRatingResultInfos) {
                writer.append(info.imageName).append(",");
                writer.append(info.solutionName).append(",");
                writer.append(info.userName).append(",");
                writer.append(String.valueOf(info.ratingEnum)).append(",");
                writer.append(info.additionalRatingDescription).append(",");
                writer.append(info.uploadTime).append("\n");
            }

            Toast.makeText(this, "CSV file created and saved to " + csvFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create CSV file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void classifySoltions() {
        List<ClassifySolutionInfo> classifySolutionInfos = mImageClassifyViewModel.requestRatingSolutionList();
        assembleSolutions(classifySolutionInfos);
        HashMap<String, String> previousSolutionInfoMap = new HashMap<>(classifySolutionInfoMap);

        new MDDialog.Builder(this)
                .setContentView(R.layout.image_classify_solutions)
                .setContentViewOperator(contentView -> {
                    solutionAddButton = contentView.findViewById(R.id.add_button);
                    solutionDeleteButton = contentView.findViewById(R.id.delete_button);
                    solutionRecycleView = contentView.findViewById(R.id.recycler_view_table);
                    solutionRecycleView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
                        @Override
                        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                            // 判断是否是滑动事件
                            if (e.getAction() == MotionEvent.ACTION_MOVE) {
                                return true;  // 如果是滑动事件，拦截事件，不触发点击
                            }
                            return super.onInterceptTouchEvent(rv, e);
                        }
                    });
                    showSolutions();

                    solutionAddButton.setOnClickListener(v -> {
                        // Fetch data from server and display in table
                        addSolutionToTable();
                    });
                    solutionDeleteButton.setOnClickListener(v -> {
                        if(classifySolutionAdapter != null)
                        deleteSolutionFromTable(classifySolutionAdapter.getSelectedPosition());
                    });
                }).setNegativeButton("Cancel", v -> { })
                .setPositiveButton("Confirm", v -> {
                    playButtonSound();
                    showClassifications(classifySolutionAdapter.getSelectedPosition());
                    saveSolutionChanges(previousSolutionInfoMap);
                })
                .setTitle("Classification Solutions")
                .create()
                .show();
    }

    private void showClassifications(int position) {
        if(position == RecyclerView.NO_POSITION){
            return;
        }
        LinearLayout classFirstButtonContainer = findViewById(R.id.class_first_button_container);
        // 清空旧的按钮
        classFirstButtonContainer.removeAllViews();
        // 获取当前项目的键值对
        ClassifySolutionInfo selectedSolution = classifySolutionInfoList.get(position);
        String detail = selectedSolution.solutionDetail;
        String[] classNoSplit = detail.split(",");
        LinkedHashSet<String> classFirstSet = new LinkedHashSet<>();
        HashMap<String, LinkedHashSet<String>> classFirst2SecondMap = new HashMap<>();
        for (String s : classNoSplit) {
            String[] classSecond = s.split("_");
            classFirstSet.add(classSecond[0]);
            if (classSecond.length == 2) {
                if (classFirst2SecondMap.containsKey(classSecond[0])) {
                    Objects.requireNonNull(classFirst2SecondMap.get(classSecond[0])).add(classSecond[1]);
                } else {
                    LinkedHashSet<String> set = new LinkedHashSet<>();
                    set.add(classSecond[1]);
                    classFirst2SecondMap.put(classSecond[0], set);
                }
            }
        }

        // 添加按钮到布局中
        addButtonToLayout(classFirstButtonContainer, classFirstSet, classFirst2SecondMap);
    }

    private void addButtonToLayout(LinearLayout classFirstButtonContainer, LinkedHashSet<String> classFirstSet, HashMap<String, LinkedHashSet<String>> classFirst2SecondMap) {
        HashMap<String, Integer> classFirst2IndexMap = new HashMap<>();
        int index = 1;
        for(String classFirst : classFirstSet){
            classFirst2IndexMap.put(classFirst, index);
            index++;
        }

        LinearLayout classSecondButtonContainer = findViewById(R.id.class_second_button_container);
        for (String classFirst : classFirstSet) {
            Button button = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,  // 宽度设置为0
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.weight = 1;
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);

            // 设置文本
            button.setText(classFirst);
            // 设置按钮启用
            button.setEnabled(true);
            // 设置文本颜色
            button.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            // 设置按钮的边框样式，使用 theme 属性 (borderlessButtonStyle)
            // 在代码中我们无法直接引用 `?android:attr/borderlessButtonStyle`，但可以使用已定义好的自定义样式来设置
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            int[] attribute = new int[] { android.R.attr.selectableItemBackground};
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
            // 设置按钮的选择前景（点击时的背景效果）
            Drawable foreground = typedArray.getDrawable(0);
            button.setForeground(foreground);

            getContext().getTheme().resolveAttribute(android.R.attr.borderlessButtonStyle, typedValue, true);
            attribute = new int[] { android.R.attr.borderlessButtonStyle};
            typedArray = getContext().getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
            button.setBackground(typedArray.getDrawable(0));
            typedArray.recycle();

            // 设置点击事件
            button.setOnClickListener(v -> {
                if(!classFirst2SecondMap.containsKey(classFirst)){
                    if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                        String utf8String = new String(additionalRatingDescription.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                        navigateFile(true, true,  classFirst2IndexMap.get(classFirst) + "_" + classFirst.toLowerCase(Locale.ROOT), utf8String);
                        additionalRatingDescription = "";
                    } else {
                        ToastEasy("please open image file first");
                    }
                }
                else{
                    if(classSecondButtonContainer.getVisibility() != View.GONE && classFirst.equals(curSelectedClassFirst)){
                        classSecondButtonContainer.setVisibility(View.GONE);
                    }
                    else{
                        curSelectedClassFirst = classFirst;
                        classSecondButtonContainer.setVisibility(View.VISIBLE);
                        showSecondClassifications(classFirst, Objects.requireNonNull(classFirst2SecondMap.get(classFirst)), classFirst2IndexMap.get(classFirst));
                    }
                }
            });

            // 添加按钮到容器中
            classFirstButtonContainer.addView(button);
        }
    }

    private void showSecondClassifications(String classFirst, LinkedHashSet<String> classSecondSet, int index) {
        LinearLayout classSecondButtonContainer = findViewById(R.id.class_second_button_container);

        // 清空旧的按钮
        classSecondButtonContainer.removeAllViews();

        HashMap<String, Integer> classSecond2IndexMap = new HashMap<>();
        int indexSecond = 1;
        for(String classSecond : classSecondSet){
            classSecond2IndexMap.put(classSecond, indexSecond);
            indexSecond++;
        }

        for (String classSecond : classSecondSet) {
            Button button = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,  // 宽度设置为0
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.weight = 1;
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);

            // 设置文本
            button.setText(classSecond);
            // 设置按钮启用
            button.setEnabled(true);
            // 设置文本颜色
            button.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            // 设置按钮的边框样式，使用 theme 属性 (borderlessButtonStyle)
            // 在代码中我们无法直接引用 `?android:attr/borderlessButtonStyle`，但可以使用已定义好的自定义样式来设置
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            int[] attribute = new int[]{android.R.attr.selectableItemBackground};
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
            // 设置按钮的选择前景（点击时的背景效果）
            Drawable foreground = typedArray.getDrawable(0);
            button.setForeground(foreground);

            getContext().getTheme().resolveAttribute(android.R.attr.borderlessButtonStyle, typedValue, true);
            attribute = new int[]{android.R.attr.borderlessButtonStyle};
            typedArray = getContext().getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);
            button.setBackground(typedArray.getDrawable(0));
            typedArray.recycle();

            // 设置点击事件
            button.setOnClickListener(v -> {
                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                    String utf8String = new String(additionalRatingDescription.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                    navigateFile(true, true, index + "." + classSecond2IndexMap.get(classSecond) + "_" + classSecond.toLowerCase(Locale.ROOT), utf8String);
                    additionalRatingDescription = "";
                    classSecondButtonContainer.setVisibility(View.GONE);
                } else {
                    ToastEasy("please open image file first");
                }
            });

            // 添加按钮到容器中
            classSecondButtonContainer.addView(button);
        }
    }

    private void deleteSolutionFromTable(int position) {
        if(position == RecyclerView.NO_POSITION){
            return;
        }
        // 获取当前项目的键值对
        ClassifySolutionInfo selectedSolution = classifySolutionInfoList.get(position);
        classifySolutionInfoMap.remove(selectedSolution.solutionName);
        updateSolutionList();
    }

    // 显示修改项目的对话框
    private void editSolutionFromTable(int position) {
        // 获取当前项目的键值对
        ClassifySolutionInfo selectedSolution = classifySolutionInfoList.get(position);

        // 创建对话框视图
        View dialogView = LayoutInflater.from(this).inflate(R.layout.image_classify_add_edit_solution, null);
        EditText solutionDetailEditText = dialogView.findViewById(R.id.solution_detail_edit_text_view);
        EditText solutionNameEditText = dialogView.findViewById(R.id.solution_name_edit_text_view);

        // 设置当前描述为对话框中的默认值
        solutionNameEditText.setText(selectedSolution.solutionName);
        solutionDetailEditText.setText(selectedSolution.solutionDetail);

        // 创建对话框
        new AlertDialog.Builder(this)
                .setTitle("Edit Solution")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String updatedName = solutionNameEditText.getText().toString().trim();
                    String updatedDetail = solutionDetailEditText.getText().toString().trim();
                    if (!updatedDetail.isEmpty() && !updatedName.isEmpty()) {
                        // 更新 HashMap 和列表中的数据
                        classifySolutionInfoMap.put(updatedName, updatedDetail);
                        updateSolutionList();
                    } else {
                        Toast.makeText(this, "Solution Name or Detail cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void addSolutionToTable() {
        // 创建对话框视图
        View dialogView = getLayoutInflater().inflate(R.layout.image_classify_add_edit_solution, null);
        EditText solutionNameEditText = dialogView.findViewById(R.id.solution_name_edit_text_view);
        EditText solutionDetailEditText = dialogView.findViewById(R.id.solution_detail_edit_text_view);

        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Solution")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String solutionName = solutionNameEditText.getText().toString().trim();
                    String solutionDetail = solutionDetailEditText.getText().toString().trim();
                    if (!solutionName.isEmpty() && !solutionDetail.isEmpty()) {
                        classifySolutionInfoMap.put(solutionName, solutionDetail);
                        updateSolutionList();
                    } else {
                        Toast.makeText(ImageClassifyActivity.this, "Solution Name and Detail cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateSolutionList() {
        classifySolutionInfoList.clear();
        for(Map.Entry<String, String> entry : classifySolutionInfoMap.entrySet()){
            classifySolutionInfoList.add(new ClassifySolutionInfo(entry.getKey(), entry.getValue()));
        }
        classifySolutionAdapter.notifyDataSetChanged();
    }

    private void saveSolutionChanges(HashMap<String, String> previousSolutionInfoMap) {
        List<ClassifySolutionInfo> addedSolutionList = getAddedSolution(previousSolutionInfoMap);
        List<String> deletedSolutionList = getDeletedSolution(previousSolutionInfoMap);
        List<UpdateClassifySolution> updatedSolutionList = getUpdatedSolution(previousSolutionInfoMap);
        saveSolutionToDatabase(addedSolutionList, deletedSolutionList, updatedSolutionList);
    }

    private List<ClassifySolutionInfo> getAddedSolution(HashMap<String, String> previousSolutionInfoMap) {
        List<ClassifySolutionInfo> addedSolutionList = new ArrayList<>();
        for(Map.Entry<String, String> entry : classifySolutionInfoMap.entrySet()){
            if(!previousSolutionInfoMap.containsKey(entry.getKey())){
                addedSolutionList.add(new ClassifySolutionInfo(entry.getKey(), entry.getValue()));
            }
        }
        return addedSolutionList;
    }

    private List<String> getDeletedSolution(HashMap<String, String> previousSolutionInfoMap) {
        List<String> deletedSolutionList = new ArrayList<>();
        for(String solutionName : previousSolutionInfoMap.keySet()){
            if(!classifySolutionInfoMap.containsKey(solutionName)){
                deletedSolutionList.add(solutionName);
            }
        }
        return deletedSolutionList;
    }

    private List<UpdateClassifySolution> getUpdatedSolution(HashMap<String, String> previousSolutionInfoMap) {
        List<UpdateClassifySolution> updatedSolutionList = new ArrayList<>();
        for(Map.Entry<String, String> entry : classifySolutionInfoMap.entrySet()){
            if(previousSolutionInfoMap.containsKey(entry.getKey()) && !Objects.equals(previousSolutionInfoMap.get(entry.getKey()), entry.getValue())){
                ClassifySolutionInfo newSolutionInfo = new ClassifySolutionInfo(entry.getKey(), entry.getValue());
                UpdateClassifySolution updateClassifySolution = new UpdateClassifySolution();
                updateClassifySolution.oldSolutionName = entry.getKey();
                updateClassifySolution.newSolutionInfo = newSolutionInfo;
                updatedSolutionList.add(updateClassifySolution);
            }
        }
        return updatedSolutionList;
    }

    private void saveSolutionToDatabase(List<ClassifySolutionInfo> addedSolutionList, List<String> deletedSolutionList, List<UpdateClassifySolution> updatedSolutionList) {
        // 删除，添加，更新
        CompletableFuture<Boolean> deleteFuture = mImageClassifyViewModel.deleteRatingSolution(deletedSolutionList);
        CompletableFuture<Boolean> addFuture = deleteFuture.thenCompose(v -> mImageClassifyViewModel.addRatingSolution(addedSolutionList));
        CompletableFuture<Boolean> updateFuture = deleteFuture.thenCompose(v -> mImageClassifyViewModel.updateRatingSolution(updatedSolutionList));

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(deleteFuture, addFuture, updateFuture);
        allTasks.thenRun(() -> {
            try {
                boolean result1 = deleteFuture.get();
                boolean result2 = addFuture.get();
                boolean result3 = updateFuture.get();
                if(result1 && result2 && result3){
                    ToastEasy("Save solution success.");
                }
            } catch (Exception e){
                ToastEasy(e.toString());
            }
        });
    }

    private void assembleSolutions(List<ClassifySolutionInfo> classifySolutionInfos){
        if(classifySolutionInfos == null){
            return;
        }
        classifySolutionInfoMap = getSolutionInfoMap(classifySolutionInfos);
        classifySolutionInfoList = classifySolutionInfos;
    }

    private void showSolutions() {
        if(solutionRecycleView == null) {
            Toast.makeText(this, "SoluionRecycleView not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check permission
        if (ContextCompat.checkSelfPermission(ImageClassifyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(ImageClassifyActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            // Pass the data to RecyclerView adapter with callback implementation
            classifySolutionAdapter = new ClassifySolutionTableAdapter(this, classifySolutionInfoList, this::editSolutionFromTable);
            // 设置布局管理器
            solutionRecycleView.setLayoutManager(new LinearLayoutManager(this));
            solutionRecycleView.setAdapter(classifySolutionAdapter);
        }
    }

    private HashMap<String, String> getSolutionInfoMap(List<ClassifySolutionInfo> classifySolutionInfos) {
        HashMap<String, String> solutionInfoMap = new HashMap<>();
        for (ClassifySolutionInfo info : classifySolutionInfos){
           solutionInfoMap.put(info.solutionName, info.solutionDetail);
        }
        return solutionInfoMap;
    }

    private String getSelectedSolutionName(){
        if (classifySolutionAdapter == null || classifySolutionAdapter.getSelectedPosition() != RecyclerView.NO_POSITION){
            return null;
        }
        else{
            return classifySolutionInfoList.get(classifySolutionAdapter.getSelectedPosition()).solutionName;
        }
    }

    private List<String> getSolutionNameList(){
        if (classifySolutionInfoList == null){
            return null;
        }
        List<String> solutionNames = new ArrayList<>();
        for(ClassifySolutionInfo info : classifySolutionInfoList){
            solutionNames.add(info.solutionName);
        }
        return solutionNames;
    }

}
