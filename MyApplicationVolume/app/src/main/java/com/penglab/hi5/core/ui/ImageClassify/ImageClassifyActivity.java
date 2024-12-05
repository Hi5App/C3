package com.penglab.hi5.core.ui.ImageClassify;

import static com.penglab.hi5.chat.nim.main.helper.MessageHelper.TAG;
import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.playButtonSound;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;



public class ImageClassifyActivity extends AppCompatActivity {
    private AnnotationGLSurfaceView mAnnotationGLSurfaceView;
    private ImageClassifyViewModel mImageClassifyViewModel;
    private TextView mImageIdLocationTextView;
    private View mImageClassifyView;
    private Toolbar mToolbar;
    private SeekBar mContrastSeekBar;
    private LinearLayout layoutSubcategories3, layoutSubcategories4;
    private EditText mEditTextRemark;

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
    private HashSet<String> classifySolutionInfoSet = null;

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

            PreferenceSetting preferenceSetting = PreferenceSetting.getInstance();

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

            layoutSubcategories3 = findViewById(R.id.layoutSubcategoryWindow3);
            layoutSubcategories4 = findViewById(R.id.layoutSubcategoryWindow4);


            Button btnHorizontal = findViewById(R.id.btnHorizontal);
            Button btnVertical = findViewById(R.id.btnVertical);
            Button btnSlanting = findViewById(R.id.btnSlanting);
            Button btnOther = findViewById(R.id.btnOther);
            Button btnInterceptive = findViewById(R.id.btnInterceptive);
            Button btnUntruncated = findViewById(R.id.btnUntruncated);
            btnSpecial = findViewById(R.id.btnSpecial);
            mEditTextRemark = findViewById(R.id.editTextRemark);
            updateButtonState(true);


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

            btnVertical.setOnClickListener(v -> {
                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                    navigateFile(true, true, "2_vertical", "");
                } else {
                    ToastEasy("please open image file first");
                }
            });

            btnSlanting.setOnClickListener(v -> {
                if (layoutSubcategories3.getVisibility() == View.GONE) {
                    layoutSubcategories3.setVisibility(View.VISIBLE);

                } else {
                    layoutSubcategories3.setVisibility(View.GONE);
                }
            });

            btnOther.setOnClickListener(v -> {
                if (layoutSubcategories4.getVisibility() == View.GONE) {
                    layoutSubcategories4.setVisibility(View.VISIBLE);
                    btnSpecial.setVisibility(View.VISIBLE);
                    mEditTextRemark.setVisibility(View.VISIBLE);
                } else {
                    layoutSubcategories4.setVisibility(View.GONE);
                }
            });

            btnInterceptive.setOnClickListener(v -> {
                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                    navigateFile(true, true, "3.1_interceptive", "");
                    layoutSubcategories3.setVisibility(View.GONE);
                } else {
                    ToastEasy("please open image first");
                }
            });

            btnUntruncated.setOnClickListener(v -> {
                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                    navigateFile(true, true, "3.2_untruncated", "");
                    layoutSubcategories3.setVisibility(View.GONE);
                } else {
                    ToastEasy("please open image first");
                }
            });


            btnSpecial.setOnClickListener(v -> {
                if (btnSpecial.isEnabled()) {
                    String remark = mEditTextRemark.getText().toString();
                    if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                        String utf8String = new String(remark.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                        navigateFile(true, true, "4.2_special", utf8String);
                        mEditTextRemark.setText("other"); // 重设为默认值
                        layoutSubcategories4.setVisibility(View.GONE);
                    } else {
                        ToastEasy("please open image first");
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
                    updateButtonState(!editable.toString().isEmpty());
                }
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
        new MDDialog.Builder(this)
                .setContentView(R.layout.image_classify_setting)
                .setContentViewOperator(contentView -> {
                    start_time_edit_text = contentView.findViewById(R.id.start_time_edit_text);
                    end_time_edit_text = contentView.findViewById(R.id.end_time_edit_text);
                    userSpinner = contentView.findViewById(R.id.user_spinner);
                    queryButton = contentView.findViewById(R.id.query_button);
                    downloadButton = contentView.findViewById(R.id.download_button);
                    timeRecycleView = contentView.findViewById(R.id.recycler_view_table);
                    showDetailsCheckbox = contentView.findViewById(R.id.show_details_checkbox);

                    setupSpinners();
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

    public void setupSpinners() {
        start_time_edit_text.setOnClickListener(v -> showDateTimePicker(start_time_edit_text));

        end_time_edit_text.setOnClickListener(v -> showDateTimePicker(end_time_edit_text));

        ArrayAdapter<CharSequence> userAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_options, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);
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
        mImageClassifyViewModel.requestRatingTable(queryUserName, queryStartTime, queryEndTime);
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
            writer.append("Image Name,Rating,Additional Rating Description,Upload Time\n");

            // 写入数据
            for (UserRatingResultInfo info : userRatingResultInfos) {
                writer.append(info.imageName).append(",");
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
//                        addSolutionToTable();
                    });
                    solutionDeleteButton.setOnClickListener(v -> {
//                        deleteSolutionFromTable();
                    });
                }).setNegativeButton("Cancel", v -> { })
                .setPositiveButton("Confirm", v -> {
                    playButtonSound();
//                    saveSolutions();
                })
                .setTitle("Classification Solutions")
                .create()
                .show();
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
            Set<String> set = preferenceSetting.getUserClassifySolutionInfos(InfoCache.getAccount());
            classifySolutionInfoSet = new HashSet<>(set);
            Map<String, String> infoMap = getSolutionInfoMap(classifySolutionInfoSet);
            // Pass the data to RecyclerView adapter with callback implementation
            ClassifySolutionTableAdapter adapter = new ClassifySolutionTableAdapter(this, infoMap);
            // 设置布局管理器
            solutionRecycleView.setLayoutManager(new LinearLayoutManager(this));
            solutionRecycleView.setAdapter(adapter);
        }
    }

    private Map<String, String> getSolutionInfoMap(HashSet<String> classifySolutionInfoSet) {
        HashMap<String, String> solutionInfoMap = new HashMap<>();
        for (String item : classifySolutionInfoSet){
            String[] solutionInfo = item.split(":");
            if(solutionInfo.length >= 2){
                String solutionName = solutionInfo[0];
                String solutionDetail = solutionInfo[1];
                solutionInfoMap.put(solutionName, solutionDetail);
            }
            else{
                ToastEasy("Errors in the format of the classification solution!");
                return new HashMap<String, String>();
            }
        }
        return solutionInfoMap;
    }

}
