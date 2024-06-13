package com.penglab.hi5.core.ui.BoutonDetection;

import static com.penglab.hi5.core.Myapplication.ToastEasy;
import static com.penglab.hi5.core.Myapplication.playButtonSound;
import static com.penglab.hi5.core.Myapplication.updateMusicVolume;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.MarkerList;
import com.penglab.hi5.basic.utils.view.ImageButtonExt;
import com.penglab.hi5.basic.utils.xpopupExt.ConfirmPopupViewExt;
import com.penglab.hi5.basic.utils.xpopupExt.ConfirmPopupViewWithCheckBox;
import com.penglab.hi5.core.music.MusicService;
import com.penglab.hi5.core.render.AnnotationRender;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.QualityInspection.QualityInspectionViewModel;
import com.penglab.hi5.core.ui.QualityInspection.QueryCheckerResult;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.core.ui.annotation.EditMode;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.Result;
import com.penglab.hi5.data.dataStore.PreferenceMusic;
import com.penglab.hi5.data.dataStore.PreferenceSetting;
import com.penglab.hi5.data.dataStore.PreferenceSoma;
import com.penglab.hi5.data.model.img.FilePath;
import com.penglab.hi5.data.model.img.PotentialArborMarkerInfo;
import com.robinhood.ticker.TickerView;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.carbs.android.library.MDDialog;
import es.dmoral.toasty.Toasty;
/**
 * Created by Linglizhang on 05/11/23
 */
public class BoutonDetectionActivity extends AppCompatActivity {

    private static final String TAG = "BoutonDetectionActivity";

    private final HashMap<EditMode, Integer> editModeIconMap = new HashMap<EditMode, Integer>() {{
        put(EditMode.NONE, 0);
        put(EditMode.PAINT_CURVE, R.drawable.ic_draw_main);
        put(EditMode.PINPOINT, R.drawable.ic_add_marker);
        put(EditMode.PINPOINT_STROKE,R.drawable.ic_add_marker);
        put(EditMode.PINPOINT_CHECK,R.drawable.ic_add_marker);
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
    private AnnotationRender annotationRender;
    private BoutonDetectionViewModel boutonDetectionViewModel;

    private final Handler uiHandler = new Handler();
    private Toolbar toolbar;
    private BasePopupView downloadingPopupView;
    private ImageButton editModeIndicator;
    private ImageButton addMarkerRed;

    private ImageButton addMarkerYellow;
    private ImageButton deleteMarker;
    private SeekBar contrastSeekBar;
    private TextView imageIdLocationTextView;
    private boolean needSyncSomaList = false;
    private boolean switchMarkerMode = true;
    private View BoutonDetectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quality_inspection);
        annotationGLSurfaceView = findViewById(R.id.gl_surface_view_quality_inspection);
        annotationGLSurfaceView.setOnScoreWinWithTouchEventListener(() -> {
//                if (annotationGLSurfaceView.getEditMode().getValue() == EditMode.PINPOINT ) {
//                    qualityInspectionViewModel.winScoreByPinPoint();
//                }
        });
        imageIdLocationTextView = findViewById(R.id.imageid_location_text_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar_quality_control);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        downloadingPopupView = new XPopup.Builder(this).asLoading("Downloading......");
        boutonDetectionViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(BoutonDetectionViewModel.class);
        boutonDetectionViewModel.getAnnotationMode().observe(this, new Observer<BoutonDetectionViewModel.AnnotationMode>() {
            @Override
            public void onChanged(BoutonDetectionViewModel.AnnotationMode annotationMode) {
                if (annotationMode == null){
                    return;
                }
                updateUI(annotationMode);
                updateOptionsMenu(annotationMode);
            }
        });
        boutonDetectionViewModel.getWorkStatus().observe(this, new Observer<BoutonDetectionViewModel.WorkStatus>() {
            @Override
            public void onChanged(BoutonDetectionViewModel.WorkStatus workStatus) {
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
                            boutonDetectionViewModel.queryArborMarkerList();
                            needSyncSomaList = false;
                        }
                        break;

                    case GET_ARBOR_MARKER_LIST_SUCCESSFULLY:
                        hideDownloadingProgressBar();
                        Log.e(TAG,"GET ARBOR MARKERLIST SUCCESSFULLY");
                        break;

                    case START_TO_DOWNLOAD_IMAGE:
                        showDownloadingProgressBar();
                        break;

                    case DOWNLOAD_IMAGE_FINISH:
                        Log.e(TAG,"downloadImageFinished");
                        hideDownloadingProgressBar();
                        boutonDetectionViewModel.openNewFile();
                        break;

                    case IMAGE_FILE_EXPIRED:
                        warning4ExpiredFile();
                        break;
                }
            }
        });

        boutonDetectionViewModel.getBoutonDetectionDataSource().getPotentialArborLocationResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null){
                    return;
                }
                boutonDetectionViewModel.handlePotentialLocationResult(result);
            }
        });

        boutonDetectionViewModel.getBoutonDetectionDataSource().getArborMarkerListResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                boutonDetectionViewModel.handleMarkerListResult(result);
            }
        });

        boutonDetectionViewModel.getBoutonDetectionDataSource().getUpdateCheckResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                boutonDetectionViewModel.handleUpdateSomaResult(result);
            }
        });

        boutonDetectionViewModel.getImageDataSource().getBrainListResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                boutonDetectionViewModel.handleBrainListResult(result);
            }
        });

        boutonDetectionViewModel.getImageDataSource().getDownloadButtonImageResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                boutonDetectionViewModel.handleDownloadImageResult(result);
            }
        });

        boutonDetectionViewModel.getBoutonDetectionDataSource().getDownloadSwcResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if(result == null){
                    return;
                }
                boutonDetectionViewModel.handleDownloadSwcResult(result);
            }
        });

        boutonDetectionViewModel.getBoutonDetectionDataSource().getQueryArborResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if(result == null){
                    return;
                }
                Log.e("getQueryArborResult",result.toString());
                boutonDetectionViewModel.handleQueryArborResult(result);
            }
        });

        boutonDetectionViewModel.getSwcResult().observe(this, new Observer<NeuronTree>() {
            @Override
            public void onChanged(NeuronTree neuronTree) {
                if(neuronTree == null){
                    return;
                }
                annotationGLSurfaceView.syncNeuronTree(neuronTree);
                boutonDetectionViewModel.queryArborMarkerList();
            }
        });

        boutonDetectionViewModel.getImageResult().observe(this, new Observer<ResourceResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult == null){
                    return;
                }
                if (resourceResult.isSuccess()){
                    Log.e(TAG,"getImageResultSuccessfully");
                    annotationGLSurfaceView.openFile();
                    boutonDetectionViewModel.getSwc();
                    PotentialArborMarkerInfo arborMarkerInfo = boutonDetectionViewModel.getCurPotentialArborMarkerInfo();
                    imageIdLocationTextView.setText(arborMarkerInfo.getBrianId() + "_" + arborMarkerInfo.getLocation().toString());
                    Log.e(TAG,"image text view content"+arborMarkerInfo.getBrianId() + "_" + arborMarkerInfo.getLocation().toString());
                    annotationGLSurfaceView.setImageInfoInRender(arborMarkerInfo.getBrianId() + "_" + arborMarkerInfo.getLocation().toString());
                } else {
                    ToastEasy(resourceResult.getError());
                }

            }
        });

        boutonDetectionViewModel.getUploadResult().observe(this, new Observer<ResourceResult>() {
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

        boutonDetectionViewModel.getSyncMarkerList().observe(this, new Observer<MarkerList>() {
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

        startMusicService();
        boutonDetectionViewModel.cacheImage();



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

    private void updateOptionsMenu(BoutonDetectionViewModel.AnnotationMode annotationMode) {
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
                    boutonDetectionViewModel.updateCheckResult(annotationGLSurfaceView.getMarkerListToAdd(),
                            annotationGLSurfaceView.getMarkerListToDelete(),1);
                    playButtonSound();
                }
                return true;

            case R.id.file:
                if (boutonDetectionViewModel.isLoggedIn()) {
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
        boutonDetectionViewModel.openNewFile();
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

    private void updateUI(BoutonDetectionViewModel.AnnotationMode annotationMode){
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
        if (BoutonDetectionView == null) {
            // load layout view
            LinearLayout.LayoutParams lpBoutonDetectionView = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            BoutonDetectionView = getLayoutInflater().inflate(R.layout.bouton_detection_view, null);
            this.addContentView(BoutonDetectionView, lpBoutonDetectionView);

//            editModeIndicator = findViewById(R.id.edit_mode_indicator);
            addMarkerRed = findViewById(R.id.add_certain_bouton);
            addMarkerYellow = findViewById(R.id.add_uncertain_bouton);
            deleteMarker = findViewById(R.id.delete_bouton);
            contrastSeekBar = (SeekBar) findViewById(R.id.mySeekBar);
            PreferenceSetting preferenceSetting = PreferenceSetting.getInstance();

            ImageButtonExt previousFile = findViewById(R.id.previous_file);
            ImageButtonExt nextFile = findViewById(R.id.next_file);


            ImageButton goodFile = findViewById(R.id.confirmed);
//            ImageButton uncertainFile = findViewById(R.id.uncertainfile);
            ImageButton hideSwc = findViewById(R.id.hide_swc_bouton);

            addMarkerRed.setOnClickListener(this::onButtonClick);
            addMarkerYellow.setOnClickListener(this::onButtonClick);
            deleteMarker.setOnClickListener(this::onButtonClick);
            previousFile.setOnClickListener(v -> previousFile());
            nextFile.setOnClickListener(v -> nextFile());
            goodFile.setOnClickListener(v -> goodFile());
//            uncertainFile.setOnClickListener(v ->uncertainFile());
            hideSwc.setOnClickListener(v -> hideSwc());

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
//            pinpointStroke.setOnCheckedChangeListener(this::OnCheckChanged);
        }
        else {
            BoutonDetectionView.setVisibility(View.VISIBLE);
        }
    }

    private void showArborResult() {
        boutonDetectionViewModel.queryArborResult();
    }

//    private void showCurrentArborResult(List<QueryCheckerResult> queryCheckerResultArrayList) {
//
//        final Map<Integer, Integer> resultMap= new HashMap<>();
//        resultMap.put(-1,R.drawable.ic_boring);
//        resultMap.put(2,R.drawable.ic_normal);
//        resultMap.put(3,R.drawable.ic_suprised2);
//        resultMap.put(4,R.drawable.ic_verygood);
//
//        final int[] checker = new int[]{R.id.checker,R.id.checker02,R.id.checker03};
//        final int[] result = new int[]{R.id.check_result,R.id.check_result02,R.id.check_result03};
//        final int[] tableRowId = new int[]{R.id.first_checker,R.id.second_checker,R.id.third_checker};
//
//        dialog = new Dialog(com.penglab.hi5.core.ui.BoutonDetection.BoutonDetectionActivity.this);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setContentView(R.layout.show_arbor_result);
//        TableLayout tableLayout = dialog.findViewById(R.id.tablelayout);
//        ImageView closeDialog = dialog.findViewById(R.id.dialogClose);
//
//        for (int i =0;i<queryCheckerResultArrayList.size();i++) {
//            TableRow row = tableLayout.findViewById(tableRowId[i]);
//            row.setVisibility(View.VISIBLE);
//            TextView checkerName = tableLayout.findViewById(checker[i]);
//            ImageView checkerResult = tableLayout.findViewById(result[i]);
//            checkerName.setText(queryCheckerResultArrayList.get(i).getOwner());
//            for(Integer key:resultMap.keySet()){
//                checkerResult.setImageResource(resultMap.get(queryCheckerResultArrayList.get(i).getCheckResult()));
//            }
//        }
//
//        closeDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }

    private void hideSwc() {
        ImageButton hideSwc = findViewById(R.id.hide_swc_bouton);
        if (annotationGLSurfaceView.setShowAnnotation()){
            annotationGLSurfaceView.requestRender();
            hideSwc.setImageResource(R.drawable.ic_not_hide);
        } else {
            annotationGLSurfaceView.requestRender();
            hideSwc.setImageResource(R.drawable.ic_hide);
        }
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
        addMarkerRed.setImageResource(R.drawable.ic_marker_red);
        addMarkerYellow.setImageResource(R.drawable.ic_marker_yellow);
        deleteMarker.setImageResource(R.drawable.ic_delete_marker_check_main);
        playButtonSound();

        switch (view.getId()){
            case R.id.add_certain_bouton:
                annotationGLSurfaceView.setEditMode(EditMode.PINPOINT_CHECK);
                annotationGLSurfaceView.setLastMarkerType(2);
                Toasty.error(this,"Missing Point",Toast.LENGTH_SHORT,true).show();
                addMarkerRed.setImageResource(R.drawable.ic_marker_main_checkmode);
                break;
            case R.id.add_uncertain_bouton:
                annotationGLSurfaceView.setEditMode(EditMode.PINPOINT_CHECK);
                annotationGLSurfaceView.setLastMarkerType(6);
                Toasty.error(this,"Uncertain Point",Toast.LENGTH_SHORT,true).show();
                addMarkerYellow.setImageResource(R.drawable.ic_marker_main_checkmode);
                break;

            case R.id.delete_bouton:
                if (annotationGLSurfaceView.setEditMode(EditMode.DELETE_MARKER)){
                    deleteMarker.setImageResource(R.drawable.ic_delete_marker_check);
                }
                break;
        }
    }

    private void boringFile() {
        if (preferenceSoma.getShowBoringFileWarning()) {
            warning4BoringFile();
        } else {
            boutonDetectionViewModel.removeCurFileFromList();
            navigateFile(true, true,-1);
        }
    }

    private void goodFile() {
        navigateFile(true, true, 3);
    }

    private void uncertainFile() {
        navigateFile(true, true, 2);
    }

    private void veryGoodFile() { navigateFile(true,true,4); }

    private void previousFile(){
        if (preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()) {
            navigateFile(true, false,1);
        } else if (!preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()){
            warning4ChangeFile(false);
        } else {
            navigateFile(false, false,0);
        }
        playButtonSound();
    }

    private void nextFile(){
        if (preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()) {
            navigateFile(true, true,1);
        } else if (!preferenceSoma.getAutoUploadMode() && !annotationGLSurfaceView.nothingToUpload()){
            warning4ChangeFile(true);
        } else {
            navigateFile(false, true,0);
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
             4: veryGoodFile with annotation
         */
        if (needUpload) {
            if (locationType == -1) {
                // boringFile: can not add; only can delete
                boutonDetectionViewModel.updateCheckResult(annotationGLSurfaceView.getMarkerListToAdd(),
                        annotationGLSurfaceView.getMarkerListToDelete(), locationType);
            } else {
                boutonDetectionViewModel.updateCheckResult(annotationGLSurfaceView.getMarkerListToAdd(),
                        annotationGLSurfaceView.getMarkerListToDelete(), locationType);
            }
        }
        if (nextFile) {
            boutonDetectionViewModel.nextFile();
        } else {
            boutonDetectionViewModel.previousFile();
        }
    }

    private void warning4ChangeFile(boolean nextFile) {
        new XPopup.Builder(this)
                .asCustom(
                        ConfirmPopupViewExt.init(this, "Warning...",
                                        "You have not upload your annotation (by press √ button), navigate to another image will lose your annotation.\n\n" +
                                                " Do you want to upload your annotation? (Or you can choose auto upload in settings)",
                                        () -> navigateFile(true, nextFile,1),
                                        () -> navigateFile(false, nextFile,0),
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
                                                boutonDetectionViewModel.removeCurFileFromList();
                                                navigateFile(true, true,-1);
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
                        () -> navigateFile(false, true,0),
                        () -> navigateFile(false, true,0))
                .setConfirmText("Confirm")
                .setCancelText("I know")
                .show();
    }

    private void resetUI4AllMode() {
        if (BoutonDetectionView != null){
            BoutonDetectionView.setVisibility(View.GONE);
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
        Intent intent = new Intent(context, BoutonDetectionActivity.class);
        context.startActivity(intent);
    }
}
