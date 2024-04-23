package com.penglab.hi5.core.ui.ImageClassify;

import static com.penglab.hi5.chat.nim.main.helper.MessageHelper.TAG;
import static com.penglab.hi5.core.Myapplication.ToastEasy;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;
//import com.penglab.hi5.Manifest;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.basic.utils.view.ImageButtonExt;
import com.penglab.hi5.core.Myapplication;
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.home.utils.Utils;
import com.penglab.hi5.data.dataStore.PreferenceMusic;
import com.penglab.hi5.data.dataStore.PreferenceSetting;
import com.penglab.hi5.data.dataStore.PreferenceSoma;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.FileType;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import cn.carbs.android.library.MDDialog;
import jxl.Cell;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;


public class ImageClassifyActivity extends AppCompatActivity {
    private AnnotationGLSurfaceView mAnnotationGLSurfaceView;
    private ImageClassifyViewModel mImageClassifyViewModel;
    private TextView mImageIdLocationTextView;
    private View mImageClassifyView;
    private Toolbar mToolbar;
    private SeekBar mContrastSeekBar;
    private LinearLayout layoutSubcategories3, layoutSubcategories4;
    private EditText mEditTextRemark;

    private Spinner userSpinner,startTimeSpinner,endTimeSpinner;

    private Button queryButton,downloadButton;

    private LoadingPopupView mDownloadingPopupView;

    private final Handler uiHandler = new Handler();
    private Timer mRenderTimer = new Timer();

    private TimerTask mRenderTask = new TimerTask() {
        @Override
        public void run() {
            mAnnotationGLSurfaceView.requestRender();
        }
    };

    private Timer mDownloadControlTimer = new Timer();

    private TimerTask mDownloadControlTask = new TimerTask() {
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

        mImageClassifyViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(ImageClassifyViewModel.class);

        updateUI();

        mImageClassifyViewModel.acquireCurrentImage().observe(this, imageInfo -> {
            renderImageFile(imageInfo);
        });

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
            if(userRatingResultInfos == null){
                return;
            }
            generateExcel(userRatingResultInfos);
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
                } else {
                    ToastEasy("please open image first");
                }
            });

            btnUntruncated.setOnClickListener(v -> {
                if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                    navigateFile(true, true, "3.2_untruncated", "");
                } else {
                    ToastEasy("please open image first");
                }
            });


            btnSpecial.setOnClickListener(v -> {
                // 当特殊按钮可点击且被点击时执行上传数据到服务器的操作
                if (btnSpecial.isEnabled()) {
                    if (!mEditTextRemark.getText().toString().isEmpty()) {
                        if (mImageClassifyViewModel.acquireCurrentImage().getValue() != null) {
                            String remark = mEditTextRemark.getText().toString();
                            String utf8String = null;
                            utf8String = new String(remark.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                            navigateFile(true, true, "4.2_special", utf8String);
                            mEditTextRemark.setText("");
                            btnSpecial.setEnabled(false);
                            btnSpecial.setBackgroundColor(Color.GRAY);
                        } else {
                            ToastEasy("please open image first");
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
                ToastEasy("click");
                Log.e(TAG,"enter the setting function");
                moreFunctions();
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
                        (position, text) -> {
                            if (text.equals("Settings")) {
                                Log.e(TAG,"enter settings");
                                settings();
                            } else {
                                ToastEasy("Something wrong with more functions...");
                            }
                        })
                .show();
    }

    public void settings() {
        new MDDialog.Builder(this)
                .setContentView(R.layout.image_classify_setting)
                .setContentViewOperator(contentView -> {
                    startTimeSpinner = contentView.findViewById(R.id.start_time_spinner);
                    endTimeSpinner = contentView.findViewById(R.id.end_time_spinner);
                    userSpinner = contentView.findViewById(R.id.user_spinner);
                    queryButton = contentView.findViewById(R.id.query_button);
                    downloadButton = contentView.findViewById(R.id.download_button);
                    setupSpinners();
                    queryButton.setOnClickListener(v -> {
                        // Fetch data from server and display in table
                        fetchDataFromServer();
                    });
                    downloadButton.setOnClickListener(v -> {
                        // Generate Excel file and provide download option
//                                generateExcel();
                    });
                });
    }

    public void setupSpinners() {
        startTimeSpinner.setOnClickListener(v -> showDateTimePickerDialog(startTimeSpinner));

        // End time spinner
        endTimeSpinner.setOnClickListener(v -> showDateTimePickerDialog(endTimeSpinner));

        ArrayAdapter<CharSequence> userAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_options, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);
    }

    public void showDateTimePickerDialog(final Spinner spinner) {
        // Get current date and time
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay1, minute1) -> {
                    // Construct RFC3339 formatted string
                    Calendar selectedDateTime = Calendar.getInstance();
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay1);
                    selectedDateTime.set(Calendar.MINUTE, minute1);
                    selectedDateTime.set(Calendar.SECOND, second);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    String formattedDateTime = sdf.format(selectedDateTime.getTime());
                    // Set the selected date and time to the spinner
                    ((EditText) spinner.getSelectedView()).setText(formattedDateTime);
                }, hourOfDay, minute, true); // true indicates 24-hour time format

        // Show the TimePickerDialog
        timePickerDialog.show();
    }

    public void fetchDataFromServer() {
        String queryUserName = (String) userSpinner.getSelectedItem();
        String queryStartTime = Utils.convertToRFC3339((String) startTimeSpinner.getSelectedItem());
        String queryEndTime = Utils.convertToRFC3339((String) endTimeSpinner.getSelectedItem());
        mImageClassifyViewModel.requestRatingTable(queryUserName, queryStartTime, queryEndTime);
    }

    public void generateExcel(List<UserRatingResultInfo> userRatingResultInfos) {
        // Check permission
        if (ContextCompat.checkSelfPermission(ImageClassifyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(ImageClassifyActivity.this,
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    1);
        } else {
            // Pass the data to RecyclerView adapter
            RecyclerView recyclerView = findViewById(R.id.recycler_view_table);
            ImageClassifyTableAdapter adapter = new ImageClassifyTableAdapter(userRatingResultInfos); // Your custom adapter
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }

    private void createExcelFile(List<UserRatingResultInfo> userRatingResultInfos) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Table Data");
        try {
            int rowNum = 0;
            // Add headers
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Image Name");
            headerRow.createCell(1).setCellValue("Rating");
            headerRow.createCell(2).setCellValue("Additional Rating Description");
            headerRow.createCell(3).setCellValue("Upload Time");

            // Add data
            for (int i = 0; i < userRatingResultInfos.size(); i++) {
                UserRatingResultInfo resultInfo = userRatingResultInfos.get(i);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(resultInfo.imageName);
                row.createCell(1).setCellValue(resultInfo.ratingEnum);
                row.createCell(2).setCellValue(resultInfo.additionalRatingDescription);
                row.createCell(3).setCellValue(resultInfo.uploadTime);
            }

            // Save the workbook to external storage
            String fileName = "table_data.xlsx";
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;
            try (FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
                workbook.write(outputStream);
                Toast.makeText(ImageClassifyActivity.this, "Excel file created and saved to Downloads", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ImageClassifyActivity.this, "Failed to create Excel file", Toast.LENGTH_SHORT).show();
            }
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    }
