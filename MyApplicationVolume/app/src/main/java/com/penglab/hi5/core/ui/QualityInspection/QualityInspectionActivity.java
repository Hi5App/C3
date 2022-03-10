package com.penglab.hi5.core.ui.QualityInspection;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.playButtonSound;
import static com.penglab.hi5.core.Myapplication.updateMusicVolume;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;
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
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.HashMap;

import cn.carbs.android.library.MDDialog;

/**
 * Created by Jackiexing on 01/10/21
 */
public class QualityInspectionActivity extends AppCompatActivity {

    private static final String TAG = "QualityInspectionActivity";

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

    private AnnotationGLSurfaceView annotationGLSurfaceView;
    private QualityInspectionViewModel QualityInspectionViewModel;

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
    private View QualityInspectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quality_inspection);
        annotationGLSurfaceView = findViewById(R.id.gl_surface_view_quality_inspection);
        annotationGLSurfaceView.setOnScoreWinWithTouchEventListener(new AnnotationGLSurfaceView.OnScoreWinWithTouchEventListener() {
            @Override
            public void run() {
                if (annotationGLSurfaceView.getEditMode().getValue() == EditMode.PINPOINT ) {
                    QualityInspectionViewModel.winScoreByPinPoint();
                }
            }
        });
        imageIdLocationTextView = findViewById(R.id.imageid_location_text_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar_quality_control);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        downloadingPopupView = new XPopup.Builder(this).asLoading("Downloading......");
        QualityInspectionViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(QualityInspectionViewModel.class);
        QualityInspectionViewModel.getAnnotationMode().observe(this, new Observer<QualityInspectionViewModel.AnnotationMode>() {
            @Override
            public void onChanged(QualityInspectionViewModel.AnnotationMode annotationMode) {
                if (annotationMode == null){
                    return;
                }
                updateUI(annotationMode);
                updateOptionsMenu(annotationMode);
            }
        });

        QualityInspectionViewModel.getWorkStatus().observe(this, new Observer<QualityInspectionViewModel.WorkStatus>() {
            @Override
            public void onChanged(QualityInspectionViewModel.WorkStatus workStatus) {
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
                            QualityInspectionViewModel.getSomaList();
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
                        QualityInspectionViewModel.openNewFile();
                        break;

                    case IMAGE_FILE_EXPIRED:
                        warning4ExpiredFile();
                        break;
                }
            }
        });

        QualityInspectionViewModel.getMarkerFactoryDataSource().getPotentialLocationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null){
                    return;
                }
                QualityInspectionViewModel.handlePotentialLocationResult(result);
            }
        });

        QualityInspectionViewModel.getMarkerFactoryDataSource().getSomaListResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                QualityInspectionViewModel.handleMarkerListResult(result);
            }
        });

        QualityInspectionViewModel.getMarkerFactoryDataSource().getUpdateSomaResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                QualityInspectionViewModel.handleUpdateSomaResult(result);
            }
        });

        QualityInspectionViewModel.getImageDataSource().getBrainListResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                QualityInspectionViewModel.handleBrainListResult(result);
            }
        });

        QualityInspectionViewModel.getImageDataSource().getDownloadImageResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                QualityInspectionViewModel.handleDownloadImageResult(result);
            }
        });

        QualityInspectionViewModel.getImageResult().observe(this, new Observer<ResourceResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult == null){
                    return;
                }
                if (resourceResult.isSuccess()){
                    annotationGLSurfaceView.openFile();
                    QualityInspectionViewModel.getSomaList();
                    PotentialSomaInfo somaInfo = QualityInspectionViewModel.getCurPotentialSomaInfo();
                    imageIdLocationTextView.setText(somaInfo.getBrainId() + "_" + somaInfo.getLocation().toString());
                    annotationGLSurfaceView.setImageInfoInRender(somaInfo.getBrainId() + "_" + somaInfo.getLocation().toString());
                } else {
                    ToastEasy(resourceResult.getError());
                }

            }
        });

        QualityInspectionViewModel.getUploadResult().observe(this, new Observer<ResourceResult>() {
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

        QualityInspectionViewModel.getSyncMarkerList().observe(this, new Observer<MarkerList>() {
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
        QualityInspectionViewModel.shutDownThreadPool();
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

    private void updateOptionsMenu(QualityInspectionViewModel.AnnotationMode annotationMode) {
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
                    QualityInspectionViewModel.updateSomaList(annotationGLSurfaceView.getMarkerListToAdd(),
                            annotationGLSurfaceView.getMarkerListToDelete());
                    playButtonSound();
                }
                return true;

            case R.id.file:
                if (QualityInspectionViewModel.isLoggedIn()) {
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
        QualityInspectionViewModel.openNewFile();
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
                            public void onStopTrackingTouch(IndicatorSeekBar seekBar) { }
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
                    annotationGLSurfaceView.requestRender();
                    playButtonSound();
                })
                .setTitle("Settings")
                .create()
                .show();
    }

    private void updateUI(QualityInspectionViewModel.AnnotationMode annotationMode){
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
        if (QualityInspectionView == null) {
            // load layout view
            LinearLayout.LayoutParams lpQualityInspectionView = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            QualityInspectionView = getLayoutInflater().inflate(R.layout.quality_inspection_view, null);
            this.addContentView(QualityInspectionView, lpQualityInspectionView);

            editModeIndicator = findViewById(R.id.edit_mode_indicator);
            addMarker = findViewById(R.id.add_marker);
            deleteMarker = findViewById(R.id.delete_marker);
            contrastSeekBar = (SeekBar) findViewById(R.id.mySeekBar);
            PreferenceSetting preferenceSetting = PreferenceSetting.getInstance();

            ImageButtonExt previousFile = findViewById(R.id.previous_file);
            ImageButtonExt nextFile = findViewById(R.id.next_file);
            ImageButtonExt boringFile = findViewById(R.id.boring_file);
            ImageButtonExt ignoreFile = findViewById(R.id.ignore_file);
            ToggleButton pinpointStroke = findViewById(R.id.switch_marker_mode);
            ImageButton hideSwc = findViewById(R.id.hide_swc);

            addMarker.setOnClickListener(this::onButtonClick);
            deleteMarker.setOnClickListener(this::onButtonClick);
            previousFile.setOnClickListener(v -> previousFile());
            nextFile.setOnClickListener(v -> nextFile());
            boringFile.setOnClickListener(v -> boringFile());
            ignoreFile.setOnClickListener(v -> boringFile());
            hideSwc.setOnClickListener(v ->hideSwc());

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
            pinpointStroke.setOnCheckedChangeListener(this::OnCheckChanged);
        }
        else {
            markerFactoryView.setVisibility(View.VISIBLE);
        }
    }

    private void hideSwc() {

    }


    private void OnCheckChanged(CompoundButton compoundButton,boolean isChecked){
        switchMarkerMode = (isChecked ? true:false);
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

        QualityInspectionViewModel.getObservableScore().observe(this, new Observer<Integer>() {
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
            QualityInspectionViewModel.removeCurFileFromList();
            navigateFile(true, true);
        }
    }

    private void previousFile(){
        if (preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()) {
            navigateFile(true, false);
        } else if (!preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()){
            warning4ChangeFile(false);
        } else {
            navigateFile(false, false);
        }
        playButtonSound();
    }

    private void nextFile(){
        if (preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()) {
            navigateFile(true, true);
        } else if (!preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()){
            warning4ChangeFile(true);
        } else {
            navigateFile(false, true);
        }
        playButtonSound();
    }

    private void navigateFile(boolean needUpload, boolean nextFile) {
        if (needUpload) {
            QualityInspectionViewModel.updateSomaList(annotationGLSurfaceView.getMarkerListToAdd(),
                    annotationGLSurfaceView.getMarkerListToDelete());
        }
        if (nextFile) {
            QualityInspectionViewModel.nextFile();
        } else {
            QualityInspectionViewModel.previousFile();
        }
    }

    private void warning4ChangeFile(boolean nextFile) {
        new XPopup.Builder(this)
                .asCustom(
                        ConfirmPopupViewExt.init(this, "Warning...",
                                "You have not upload your annotation (by press √ button), navigate to another image will lose your annotation.\n\n" +
                                        " Do you want to upload your annotation? (Or you can choose auto upload in settings)",
                                () -> navigateFile(true, nextFile),
                                () -> navigateFile(false, nextFile),
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
                                        QualityInspectionViewModel.removeCurFileFromList();
                                        navigateFile(true, true);
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
                        () -> navigateFile(false, true),
                        () -> navigateFile(false, true))
                .setConfirmText("Confirm")
                .setCancelText("I know")
                .show();
    }

    private void resetUI4AllMode() {
        if (markerFactoryView != null){
            markerFactoryView.setVisibility(View.GONE);
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

    private void timeOutHandler() {
        downloadingPopupView.dismiss();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, QualityInspectionActivity.class);
        context.startActivity(intent);
    }
}