package com.penglab.hi5.core.ui.marker;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.playButtonSound;
import static com.penglab.hi5.core.Myapplication.playMusicReward;
import static com.penglab.hi5.core.Myapplication.playRewardSound;
import static com.penglab.hi5.core.Myapplication.playRightAnswerSound;
import static com.penglab.hi5.core.Myapplication.playWrongAnswerSound;
import static com.penglab.hi5.core.Myapplication.stopMusicRewardPlay;
import static com.penglab.hi5.core.Myapplication.updateMusicVolume;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.utils.view.ImageButtonExt;
import com.penglab.hi5.basic.utils.xpopupExt.ConfirmPopupViewExt;
import com.penglab.hi5.basic.utils.xpopupExt.ConfirmPopupViewWithCheckBox;
import com.penglab.hi5.core.music.MusicService;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.annotation.EditMode;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.dataStore.PreferenceMusic;
import com.penglab.hi5.data.dataStore.PreferenceSetting;
import com.penglab.hi5.data.dataStore.PreferenceSoma;

import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.PotentialSomaInfo;
import com.robinhood.ticker.TickerView;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.jetbrains.annotations.Contract;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.carbs.android.library.MDDialog;
import co.mobiwise.library.MusicPlayerView;
import com.sdsmdg.tastytoast.TastyToast;
/**
 * Created by Jackiexing on 01/10/21
 */
public class MarkerFactoryActivity extends AppCompatActivity {

    private static final String TAG = "MarkerFactoryActivity";

    private final HashMap<EditMode, Integer> editModeIconMap = new HashMap<EditMode, Integer>() {{
        put(EditMode.NONE, 0);
        put(EditMode.PAINT_CURVE, R.drawable.ic_draw_main);
        put(EditMode.PINPOINT, R.drawable.ic_add_marker);
        put(EditMode.PINPOINT_STROKE,R.drawable.ic_add_marker);
        put(EditMode.DELETE_CURVE, R.drawable.ic_delete_curve);
        put(EditMode.DELETE_MARKER, R.drawable.ic_marker_delete);
        put(EditMode.CHANGE_CURVE_TYPE, R.drawable.ic_change_curve_type);
        put(EditMode.CHANGE_MARKER_TYPE, R.drawable.ic_change_marker_type);
        put(EditMode.DELETE_MULTI_MARKER, R.drawable.ic_delete_multimarker);
        put(EditMode.SPLIT, R.drawable.ic_split);
        put(EditMode.ZOOM, R.drawable.ic_zoom);
        put(EditMode.ZOOM_IN_ROI, R.drawable.ic_roi);
    }};
    private final PreferenceSoma preferenceSoma = PreferenceSoma.getInstance();
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private AnnotationGLSurfaceView annotationGLSurfaceView;
    private MarkerFactoryViewModel markerFactoryViewModel;

    private final Handler uiHandler = new Handler();
    private Toolbar toolbar;
    private View markerFactoryView;
    private BasePopupView downloadingPopupView;
    private ImageButton editModeIndicator;
    private ImageButton addMarker;
    private ImageButton deleteMarker;
    private TickerView scoreTickerView;
    private SeekBar contrastSeekBar;
    private TextView imageIdLocationTextView;
    private boolean needSyncSomaList = false;
    private boolean switchMarkerMode = true;
    private LottieDialog lottieDialog;
    private MusicPlayerView mpv;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_marker_factory);
        annotationGLSurfaceView = findViewById(R.id.gl_surface_view_marker_factory);
        annotationGLSurfaceView.setOnScoreWinWithTouchEventListener(new AnnotationGLSurfaceView.OnScoreWinWithTouchEventListener() {
            @Override
            public void run() {
                if (annotationGLSurfaceView.getEditMode().getValue() == EditMode.PINPOINT ) {
                    markerFactoryViewModel.winScoreByPinPoint();
                }
            }
        });
        imageIdLocationTextView = findViewById(R.id.imageid_location_text_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar_marker_factory);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        downloadingPopupView = new XPopup.Builder(this).asLoading("Downloading......");
        markerFactoryViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(MarkerFactoryViewModel.class);
        markerFactoryViewModel.getAnnotationMode().observe(this, new Observer<MarkerFactoryViewModel.AnnotationMode>() {
            @Override
            public void onChanged(MarkerFactoryViewModel.AnnotationMode annotationMode) {
                if (annotationMode == null){
                    return;
                }
                updateUI(annotationMode);
                updateOptionsMenu(annotationMode);
            }
        });

        markerFactoryViewModel.getWorkStatus().observe(this, new Observer<MarkerFactoryViewModel.WorkStatus>() {
            @Override
            public void onChanged(MarkerFactoryViewModel.WorkStatus workStatus) {
                if (workStatus == null) {
                    return;
                }
                switch (workStatus) {
                    case NO_MORE_FILE:
                        hideDownloadingProgressBar();
                        ToastEasy("No more file need to process !", Toast.LENGTH_LONG);
                        break;

                    case UPLOAD_MARKERS_SUCCESSFULLY:
                        ToastEasy("Upload successfully");
                        if (needSyncSomaList) {
                            markerFactoryViewModel.getSomaList();
                            needSyncSomaList = false;
                        }
                        break;

//                    case GET_SOMA_LIST_SUCCESSFULLY:
//                        hideDownloadingProgressBar();
//                        break;

                    case START_TO_DOWNLOAD_IMAGE:
                        showDownloadingProgressBar();
                        break;

                    case DOWNLOAD_IMAGE_FINISH:
                        hideDownloadingProgressBar();
                        markerFactoryViewModel.openNewFile();
                        break;

                    case IMAGE_FILE_EXPIRED:
                        warning4ExpiredFile();
                        break;
                }
            }
        });

        markerFactoryViewModel.getMarkerFactoryDataSource().getPotentialLocationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null){
                    return;
                }
                markerFactoryViewModel.handlePotentialLocationResult(result);
            }
        });

        markerFactoryViewModel.getMarkerFactoryDataSource().getSomaListResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                markerFactoryViewModel.handleMarkerListResult(result);
            }
        });

        markerFactoryViewModel.getMarkerFactoryDataSource().getUpdateSomaResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                markerFactoryViewModel.handleUpdateSomaResult(result);
            }
        });

        markerFactoryViewModel.getImageDataSource().getBrainListResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                markerFactoryViewModel.handleBrainListResult(result);
            }
        });

        markerFactoryViewModel.getImageDataSource().getDownloadImageResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                markerFactoryViewModel.handleDownloadImageResult(result);
            }
        });

        markerFactoryViewModel.getImageResult().observe(this, new Observer<ResourceResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult == null){
                    return;
                }
                if (resourceResult.isSuccess()){
                    annotationGLSurfaceView.openFile();
                    markerFactoryViewModel.getSomaList();
                    PotentialSomaInfo somaInfo = markerFactoryViewModel.getCurPotentialSomaInfo();
                    imageIdLocationTextView.setText(somaInfo.getBrainId() + "_" + somaInfo.getLocation().toString());
                    annotationGLSurfaceView.setImageInfoInRender(somaInfo.getBrainId() + "_" + somaInfo.getLocation().toString());
                } else {
                    ToastEasy(resourceResult.getError());
                }

            }
        });

        markerFactoryViewModel.getUploadResult().observe(this, new Observer<ResourceResult>() {
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult.isSuccess()) {
                    ToastEasy("Upload soma successfully !");
                } else if (resourceResult.getError().equals("Expired")) {
                    ToastEasy("The image you just upload is expired.");
                } else {
                    ToastEasy("Upload failed");
                }
            }
        });

        markerFactoryViewModel.getSyncMarkerList().observe(this, new Observer<MarkerList>() {
            @Override
            public void onChanged(MarkerList markerList) {
                if (markerList == null){
                    return;
                }
                annotationGLSurfaceView.syncMarkerList(markerList);
            }
        });

        annotationGLSurfaceView.getEditMode().observe(this, new Observer<EditMode>() {
            @Override
            public void onChanged(EditMode editMode) {
                if (editMode == null){
                    return;
                }
                if (editModeIconMap.get(editMode) != null && editModeIndicator != null){
                    editModeIndicator.setImageResource(editModeIconMap.get(editMode));
                }
            }
        });

        ImageInfoRepository.getInstance().getScreenCaptureFilePath().observe(this, new Observer<FilePath<?>>() {
            @Override
            public void onChanged(FilePath<?> filePath) {
                if (filePath == null){
                    return;
                }
                screenCapture((Uri) filePath.getData());
            }
        });

        markerFactoryViewModel.getSomaNum().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer somaNum) {
                MarkerFactoryViewModel.SomaNumStatus somaNumStatus = markerFactoryViewModel.getSomaNumStatus();
                if (somaNum >= 1 && somaNum < 2 && somaNumStatus == MarkerFactoryViewModel.SomaNumStatus.ZERO) {
                    playRewardSound(1);
                    showRewardDialog(1);
                    markerFactoryViewModel.setSomaNumStatus(MarkerFactoryViewModel.SomaNumStatus.TEN);
                } else if (somaNum >= 2 && somaNum < 3 && somaNumStatus.ordinal() < 2) {
                    playRewardSound(2);
                    showRewardDialog(2);
                    markerFactoryViewModel.setSomaNumStatus(MarkerFactoryViewModel.SomaNumStatus.FIFTY);
                } else if (somaNum >= 3 && somaNumStatus.ordinal() < 3) {
                    playRewardSound(3);
                    showRewardDialog(3);
                    markerFactoryViewModel.setSomaNumStatus(MarkerFactoryViewModel.SomaNumStatus.HUNDRED);
                }
            }
        });

        markerFactoryViewModel.getEditImageNumToday().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer editImageToday) {
                MarkerFactoryViewModel.EditImageTodayStatus editImageTodayStatus = markerFactoryViewModel.getEditImageTodayStatus();
                if(editImageToday >=2 && editImageToday < 3 && editImageTodayStatus == MarkerFactoryViewModel.EditImageTodayStatus.ZERO) {
                    TastyToast.makeText(getApplicationContext(), String.format("Nice! you have scanned %s images,cheer up!",editImageToday), TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    markerFactoryViewModel.setEditImageTodayStatus(MarkerFactoryViewModel.EditImageTodayStatus.FORTY);

                } else if (editImageToday >= 3 && editImageToday <4 && editImageTodayStatus.ordinal() < 2) {
                    TastyToast.makeText(getApplicationContext(), String.format("Great! you have scanned %s images",editImageToday), TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    markerFactoryViewModel.setEditImageTodayStatus(MarkerFactoryViewModel.EditImageTodayStatus.EIGHTY);

                } else if(editImageToday >= 4 && editImageToday < 5&& editImageTodayStatus.ordinal() < 3) {
                    TastyToast.makeText(getApplicationContext(), String.format("Wonderful! you have scanned %s images",editImageToday), TastyToast.LENGTH_LONG, TastyToast.WARNING);
                    markerFactoryViewModel.setEditImageTodayStatus(MarkerFactoryViewModel.EditImageTodayStatus.LONG_HUNDRED);

                }else if (editImageToday >= 5  && editImageTodayStatus.ordinal() <4) {
                    TastyToast.makeText(getApplicationContext(), String.format("Unbelievable! you have scanned %s images",editImageToday), TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                    markerFactoryViewModel.setEditImageTodayStatus(MarkerFactoryViewModel.EditImageTodayStatus.FIVE_HUNDRED);
                }
            }
        });

        initScoreTickerView();
        startMusicService();
    }

    @Override
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
        markerFactoryViewModel.shutDownThreadPool();
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
        getMenuInflater().inflate(R.menu.marker_factory_menu_basic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateOptionsMenu(MarkerFactoryViewModel.AnnotationMode annotationMode) {
        toolbar.getMenu().clear();
        switch (annotationMode) {
            case NONE:
                toolbar.inflateMenu(R.menu.marker_factory_menu_basic);
                break;
            case BIG_DATA:
                toolbar.inflateMenu(R.menu.marker_factory_menu_annotation);
                break;
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

            case R.id.undo:
                annotationGLSurfaceView.undo();
                return true;

            case R.id.redo:
                annotationGLSurfaceView.redo();
                return true;

            case R.id.confirm:
                if (!annotationGLSurfaceView.nothingToUpload()) {
                    needSyncSomaList = true;
                    markerFactoryViewModel.updateSomaList(annotationGLSurfaceView.getMarkerListToAdd(),
                            annotationGLSurfaceView.getMarkerListToDelete(), 1);
                    playButtonSound();
                }
                return true;

            case R.id.file:
                if (markerFactoryViewModel.isLoggedIn()) {
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

    private void openFile(){
        // TODO: download image



//        playGuessMusicGame();
//        showRewardDialog(1);

//        getMusicPlayReward();
        markerFactoryViewModel.openNewFile();
//        executorService.submit(() -> showRewardDialog());

    }

    private void screenCapture(Uri uri){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        startActivity(Intent.createChooser(intent, "Share from Hi5"));

        // need to reset after use
        ImageInfoRepository.getInstance().getScreenCaptureFilePath().setValue(null);
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
                                        settings();
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
                .setContentView(R.layout.marker_factory_settings)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        PreferenceSetting preferenceSetting = PreferenceSetting.getInstance();
                        PreferenceMusic preferenceMusic = PreferenceMusic.getInstance();
                        PreferenceSoma preferenceSoma = PreferenceSoma.getInstance();

                        SwitchCompat downSampleSwitch = contentView.findViewById(R.id.downSample_mode);
                        SwitchCompat autoUploadSwitch = contentView.findViewById(R.id.autoUpload_mode);
                        SwitchCompat pinpointStrokeSwitch = contentView.findViewById(R.id.switch_marker_mode);
                        IndicatorSeekBar contrastIndicator = contentView.findViewById(R.id.contrast_indicator_seekbar);
                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);

                        downSampleSwitch.setChecked(preferenceSetting.getDownSampleMode());
                        autoUploadSwitch.setChecked(preferenceSoma.getAutoUploadMode());
                        pinpointStrokeSwitch.setChecked(preferenceSetting.getPointStrokeMode());
                        contrastIndicator.setProgress(preferenceSetting.getContrast());
                        bgmVolumeBar.setProgress(preferenceMusic.getBackgroundSound());
                        buttonVolumeBar.setProgress(preferenceMusic.getButtonSound());
                        actionVolumeBar.setProgress(preferenceMusic.getActionSound());

                        autoUploadSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                preferenceSoma.setAutoUploadMode(isChecked);
                            }
                        });

                        downSampleSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                preferenceSetting.setDownSampleMode(isChecked);
                                annotationGLSurfaceView.updateRenderOptions();
                                annotationGLSurfaceView.requestRender();
                            }
                        });

                        pinpointStrokeSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                preferenceSetting.setPointStroke(isChecked);
                                if (annotationGLSurfaceView.getEditModeValue() != EditMode.NONE) {
                                    if (isChecked) {
                                        annotationGLSurfaceView.setEditMode(EditMode.PINPOINT);
                                    } else {
                                        annotationGLSurfaceView.setEditMode(EditMode.PINPOINT_STROKE);
                                    }
                                }
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

    private void updateUI(MarkerFactoryViewModel.AnnotationMode annotationMode){
        resetUI4AllMode();
        switch (annotationMode){
            case BIG_DATA:
                showUI4Annotation();
                break;

            case NONE:
                break;
        }
    }

    private void showUI4Annotation() {
        if (markerFactoryView == null) {
            // load layout view
            LinearLayout.LayoutParams lpMarkerFactoryView = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            markerFactoryView = getLayoutInflater().inflate(R.layout.marker_factory_annotation, null);
            this.addContentView(markerFactoryView, lpMarkerFactoryView);
            editModeIndicator = findViewById(R.id.edit_mode_indicator);
            addMarker = findViewById(R.id.add_marker);
            deleteMarker = findViewById(R.id.delete_marker);
            contrastSeekBar = (SeekBar) findViewById(R.id.mySeekBar);
            PreferenceSetting preferenceSetting = PreferenceSetting.getInstance();

            ImageButtonExt previousFile = findViewById(R.id.previous_file);
            ImageButtonExt nextFile = findViewById(R.id.next_file);
            ImageButtonExt boringFile = findViewById(R.id.boring_file);
            ImageButtonExt ignoreFile = findViewById(R.id.ignore_file);
//            ToggleButton pinpointStroke = findViewById(R.id.switch_marker_mode);
            ImageButtonExt goodFile = findViewById(R.id.good_file);

            addMarker.setOnClickListener(this::onButtonClick);
            deleteMarker.setOnClickListener(this::onButtonClick);
            previousFile.setOnClickListener(v -> previousFile());
            nextFile.setOnClickListener(v -> nextFile());
            boringFile.setOnClickListener(v -> boringFile());
            ignoreFile.setOnClickListener(v -> ignoreFile());
            goodFile.setOnClickListener(v -> goodFile());
//            pinpointStroke.setOnCheckedChangeListener(this::OnCheckChanged);

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
        }
        else {
            markerFactoryView.setVisibility(View.VISIBLE);
        }
    }

    private void OnCheckChanged(CompoundButton compoundButton, boolean isChecked){
        switchMarkerMode = isChecked;
        if (annotationGLSurfaceView.getEditModeValue() != EditMode.NONE) {
            if (switchMarkerMode) {
                annotationGLSurfaceView.setEditMode(EditMode.PINPOINT);
            } else {
                annotationGLSurfaceView.setEditMode(EditMode.PINPOINT_STROKE);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void onButtonClick(View view) {
        // reset UI
        addMarker.setImageResource(R.drawable.ic_add_marker);
        deleteMarker.setImageResource(R.drawable.ic_marker_delete);
        playButtonSound();

        switch (view.getId()){
            case R.id.add_marker:
                if(switchMarkerMode) {
                    if (annotationGLSurfaceView.setEditMode(EditMode.PINPOINT)) {
                        addMarker.setImageResource(R.drawable.ic_marker_main);
                    }
                }else{
                    if (annotationGLSurfaceView.setEditMode(EditMode.PINPOINT_STROKE)) {
                        addMarker.setImageResource(R.drawable.ic_marker_main);
                    }
                }
                break;

            case R.id.delete_marker:
                if (annotationGLSurfaceView.setEditMode(EditMode.DELETE_MARKER)){
                    deleteMarker.setImageResource(R.drawable.ic_marker_delete_normal);
                }
                break;
        }
    }

    private void initScoreTickerView() {
        scoreTickerView = findViewById(R.id.score_ticker_view);
        scoreTickerView.setTypeface(Typeface.DEFAULT);
        scoreTickerView.setAnimationDuration(500);
        scoreTickerView.setAnimationInterpolator(new OvershootInterpolator());
        scoreTickerView.setPreferredScrollingDirection(TickerView.ScrollingDirection.ANY);

        markerFactoryViewModel.getObservableScore().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                scoreTickerView.setText(Integer.toString(integer));
            }
        });
    }

    private void boringFile() {
        if (preferenceSoma.getShowBoringFileWarning()) {
            warning4BoringFile();
        } else {
            markerFactoryViewModel.removeCurFileFromList();
            navigateFile(true, true, -1);
        }
    }

    private void ignoreFile() {
        navigateFile(true, true, 2);
    }

    private void goodFile() {
        navigateFile(true, true, 3);
    }

    private void previousFile() {
        if (preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()) {
            navigateFile(true, false, 1);
        } else if (!preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()){
            warning4ChangeFile(false);
        } else {
            navigateFile(false, false, 0);
        }
        playButtonSound();
    }

    private void nextFile(){
        if (preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()) {
            navigateFile(true, true, 1);
        } else if (!preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()){
            warning4ChangeFile(true);
        } else {
            navigateFile(false, true, 0);
        }
        playButtonSound();
    }

    private void navigateFile(boolean needUpload, boolean nextFile, int locationType) {
        /* locationType:
            -1: boringFile,
             0: default, no update
             1: normalFile with annotation,
             2: normalFile without annotation
             3: goodFile with annotation
         */
        if (needUpload) {
            if (locationType == -1) {
                // boringFile: can not add; only can delete
                markerFactoryViewModel.updateSomaList(new MarkerList(),
                        annotationGLSurfaceView.getMarkerListToDelete(), locationType);
            } else {
                markerFactoryViewModel.updateSomaList(annotationGLSurfaceView.getMarkerListToAdd(),
                        annotationGLSurfaceView.getMarkerListToDelete(), locationType);
            }
        }
        if (nextFile) {
            markerFactoryViewModel.nextFile();
        } else {
            markerFactoryViewModel.previousFile();
        }
    }

    private void warning4ChangeFile(boolean nextFile) {
        new XPopup.Builder(this)
                .asCustom(
                        ConfirmPopupViewExt.init(this, "Warning...",
                                "You have not upload your annotation (by press √ button), navigate to another image will lose your annotation.\n\n" +
                                        " Do you want to upload your annotation? (Or you can choose auto upload in settings)",
                                () -> navigateFile(true, nextFile, 1),
                                () -> navigateFile(false, nextFile, 0),
                                null)
                                .setConfirmText("Upload")
                                .setIgnoreText("Don't upload")
                                .setCancelText("Cancel")
                ).show();
    }

    private void warning4BoringFile() {
        new XPopup.Builder(this)
                .asCustom(
                        ConfirmPopupViewWithCheckBox.init(this, "Warning...",
                                "You are marking this file as a boring file, are you sure to do that ?",
                                new OnConfirmListener() {
                                    @Override
                                    public void onConfirm() {
                                        markerFactoryViewModel.removeCurFileFromList();
                                        navigateFile(true, true, -1);
                                    }
                                },
                                null,
                                v -> preferenceSoma.setShowBoringFileWarning(!((CheckBox) v).isChecked()))
                                .setConfirmText("Confirm")
                                .setOptionText("Don't show again")
                                .setCancelText("Cancel")
                ).show();
    }

    private void warning4ExpiredFile() {
        new XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .asConfirm("Warning...",
                        "Current file is expired, will change another file for you.",
                        () -> navigateFile(false, true, 0),
                        () -> navigateFile(false, true, 0))
                .setConfirmText("Confirm")
                .setCancelText("I know")
                .show();
    }

    private void resetUI4AllMode() {
        if (markerFactoryView != null){
            markerFactoryView.setVisibility(View.GONE);
        }
    }

    private void showRewardDialog(int level) {
        final int [] gifId = new int [] {R.raw.success_like,R.raw.present,R.raw.achievement};
        final int [] somaNum = new int [] {50,100,200,500};
        int randomNumber = new Random().nextInt(10);
        Button okButton = new Button(MarkerFactoryActivity.this);
        okButton.setText("OK");
        okButton.setTextColor(Color.rgb(60,179,113));
        okButton.setOnClickListener(view -> {
            switch (level){
                case 1:
                    markerFactoryViewModel.winScoreByReward(1);
                    break;
                case 2:
                    markerFactoryViewModel.winScoreByReward(2);
                    break;
                case 3:
                    markerFactoryViewModel.winScoreByReward(3);
                    break;
            }
            if(randomNumber %2 == 0){
                playGuessMusicGame();
            }else{
                getJokeDialog();
            }
            lottieDialog.dismiss();
        });
        Button cancelButton = new Button(MarkerFactoryActivity.this);
            cancelButton.setText("No Need");
        cancelButton.setOnClickListener(view -> {
            lottieDialog.dismiss();

        });
        lottieDialog = new LottieDialog(MarkerFactoryActivity.this)
                .setAnimation(gifId[level-1])
                .setAnimationRepeatCount(10)
                .setAutoPlayAnimation(true)
                .setTitle("")
                .setTitleColor(R.color.account_lock_bg)
                .setMessage(String.format("You have finished over %s soma! There is a present for you...",somaNum[level-1]))
                .setMessageTextSize(20f)
                .setMessageColor(Color.BLACK)
                .setDialogBackground(Color.WHITE)
                .setCancelable(false)
                .addActionButton(cancelButton)
                .addActionButton(okButton)
                .setOnShowListener(dialogInterface -> {})
                .setOnDismissListener(dialogInterface -> {})
                .setOnCancelListener(dialogInterface -> {});
        lottieDialog.show();
    }

    private void getJokeDialog() {
        final int[] jokeNum = new int[]{R.raw.gif4, R.raw.gif5, R.raw.gif6, R.raw.gif7, R.raw.gif8,
                R.raw.gif10, R.raw.gif12, R.raw.gif13, R.raw.gif15, R.raw.gif20,R.raw.gif2};
//        final int[] bgColor = new int[]{};
        final String[] messNum = new String[]{
                "我虽然不能为你上天揽月但是我能为你下海底捞,捞鱼丸捞虾捞肥牛",
                "特别能吃苦，这句话我想了想，我只能做到前四个",
                "安全感是什么？安全感就是你在快迟到的路上,碰到了你的同事,但他跑的比你慢",
                "我呼唤星期五的频率古今中外，只有鲁滨逊可与我比肩",
                "和人吵架的时候最好去楼梯吵，这样的好处是，吵完了双方都有台阶下",
                "其实之前也考虑过劳斯莱斯和宾利，到最后还是选择了公交，因为人多热闹",
                "刚刚点外卖的时候，突然想起来自己160斤，我猛地扇了自己一耳光，点外卖的时候怎么可以分心",
                "跟公司说完，“我身体不舒服今天请假”，之后身体就好多了，比药有效5000倍",
                "曾经我的一位同事到公司面试，HR问他：“今天来面试有做什么准备工作吗”，同事一本正经的回答：有的，用飘柔洗了个头",
                "小时候看古装剧，很羡慕那些拿令牌出入的人，长大后梦想成真了，去哪儿都要掏出手机给保安看",
                "排队做核酸的朋友们，一定要仔细看清楚!别像我，排了半天，买了一杯奶茶"};
        int randomNum = new Random().nextInt(10);
        Log.e(TAG,"randomNum"+randomNum);
        new FancyGifDialog.Builder(this)
                .setTitle(messNum[randomNum]) // You can also send title like R.string.from_resources
//                .setMessage("") // or pass like R.string.description_from_resources
                .setTitleTextColor(R.color.black)
                .setDescriptionTextColor(R.color.descriptionText)
                .setNegativeBtnText("Cancel") // or pass it like android.R.string.cancel
                .setPositiveBtnBackground(R.color.positiveButton)
                .setPositiveBtnText("Ok") // or pass it like android.R.string.ok
                .setNegativeBtnBackground(R.color.negativeButton)
                .setGifResource(jokeNum[randomNum])   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(MarkerFactoryActivity.this,"Ok",Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(MarkerFactoryActivity.this,"Cancel",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

    }

    private void getMusicPlayReward() {
        String musicName[] = new String[]{"克罗地亚狂想曲","天空之城","偷功","瓦妮莎的微笑","一千个伤心的理由","遇见","冢森的大树"};
        int randomNum = new Random().nextInt(7);
        final FlatDialog flatDialog = new FlatDialog(MarkerFactoryActivity.this);
        flatDialog.setTitle("A Music for you")
                .setSubtitle(musicName[randomNum])
                .setFirstTextFieldHint("Write here everything")
                .setFirstButtonText("Play")
                .setSecondButtonText("Stop")
                .setThirdButtonText("Cancel")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playMusicReward(randomNum);
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopMusicRewardPlay();
                    }
                })
                .withThirdButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopMusicRewardPlay();
                        flatDialog.dismiss();
                    }
                })
                .show();
    }

    private void playGuessMusicGame() {
        int randomNum = new Random().nextInt(7);
        String arrayName[][] = new String[][]{
                {"克罗地亚狂想曲","亡灵序曲","悲怆"},
                {"天空之城","天空","城堡"},
                {"醉拳","随缘","偷功"},
                {"遇见","听见","再见"},
                {"龙猫","冢森的大树","风之谷"},
                {"the day you went away","the day you leave me","One day"},
                {"梦中的婚礼","婚礼","水边的阿狄丽娜"}
        };
        String rightName[] = new String[]{"克罗地亚狂想曲","天空之城","偷功","遇见","冢森的大树","the day you went away","梦中的婚礼"};

        dialog = new Dialog(MarkerFactoryActivity.this);
        dialog.setContentView(R.layout.guess_music);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose);
        mpv = dialog.findViewById(R.id.mpv);
        mpv.setMax(12);
        Button firstAnswer = dialog.findViewById(R.id.firstAnswer);
        Button secondAnswer = dialog.findViewById(R.id.secondAnswer);
        Button thirdAnswer = dialog.findViewById(R.id.thirdAnswer);
        mpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mpv.isRotating()){
                    mpv.stop();
                    stopMusicRewardPlay();
                }else{
                    mpv.start();
                    playMusicReward(randomNum);
                }
            }
        });

        firstAnswer.setText(arrayName[randomNum][0]);
        secondAnswer.setText(arrayName[randomNum][1]);
        thirdAnswer.setText(arrayName[randomNum][2]);
        firstAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firstAnswer.getText() == rightName[randomNum]) {
                    playRightAnswerSound();
                    firstAnswer.setBackgroundColor(Color.rgb(69,179,113));

                    markerFactoryViewModel.winScoreByGuessMusic();

                }else {
                    playWrongAnswerSound();
                    firstAnswer.setBackgroundColor(Color.rgb(211,211,211));
                }
                firstAnswer.setEnabled(false);
                secondAnswer.setEnabled(false);
                thirdAnswer.setEnabled(false);
            }
        });

        secondAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(secondAnswer.getText() == rightName[randomNum]) {
                    playRightAnswerSound();
                    secondAnswer.setBackgroundColor(Color.rgb(69,179,113));
                    markerFactoryViewModel.winScoreByGuessMusic();
                }else{
                    playWrongAnswerSound();
                    secondAnswer.setBackgroundColor(Color.rgb(211,211,211));
                }
                firstAnswer.setEnabled(false);
                secondAnswer.setEnabled(false);
                thirdAnswer.setEnabled(false);
            }
        });

        thirdAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(thirdAnswer.getText() == rightName[randomNum]) {
                    playRightAnswerSound();
                    thirdAnswer.setBackgroundColor(Color.rgb(69,179,113));
                    markerFactoryViewModel.winScoreByGuessMusic();
                }else{
                    playWrongAnswerSound();
                    thirdAnswer.setBackgroundColor(Color.rgb(211,211,211));
                }
                firstAnswer.setEnabled(false);
                secondAnswer.setEnabled(false);
                thirdAnswer.setEnabled(false);
            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

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

    private void timeOutHandler() {
        downloadingPopupView.dismiss();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MarkerFactoryActivity.class);
        context.startActivity(intent);
    }
}