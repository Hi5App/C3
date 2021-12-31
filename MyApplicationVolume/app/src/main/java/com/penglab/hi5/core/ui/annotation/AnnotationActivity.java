package com.penglab.hi5.core.ui.annotation;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.michaldrabik.tapbarmenulib.TapBarMenu;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.penglab.hi5.R;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.data.ImageInfoRepository;
import com.penglab.hi5.data.model.img.FilePath;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.carbs.android.library.MDDialog;

public class AnnotationActivity extends AppCompatActivity {

    private static final String TAG = "AnnotationActivity";

    private static final int OPEN_LOCAL_FILE = 1;
    private static final int OPEN_ANALYSIS_SWC = 2;
    private static final int LOAD_LOCAL_FILE = 3;

    private final HashMap<EditMode, Integer> editModeIconMap = new HashMap<EditMode, Integer>() {{
        put(EditMode.NONE, 0);
        put(EditMode.PAINT_CURVE, R.drawable.ic_draw_main);
        put(EditMode.PINPOINT, R.drawable.ic_add_marker);
        put(EditMode.DELETE_CURVE, R.drawable.ic_delete_curve);
        put(EditMode.DELETE_MARKER, R.drawable.ic_marker_delete);
        put(EditMode.CHANGE_CURVE_TYPE, R.drawable.ic_change_curve_type);
        put(EditMode.CHANGE_MARKER_TYPE, R.drawable.ic_change_marker_type);
        put(EditMode.DELETE_MULTI_MARKER, R.drawable.ic_delete_multimarker);
        put(EditMode.SPLIT, R.drawable.ic_split);
        put(EditMode.ZOOM, R.drawable.ic_zoom);
        put(EditMode.ZOOM_IN_ROI, R.drawable.ic_roi);
    }};

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);;

    private AnnotationViewModel annotationViewModel;
    private AnnotationGLSurfaceView annotationGLSurfaceView;

    private Toolbar toolbar;
    private View localFileModeView;
    private View bigDataModeView;
    private View commonView;

    private TapBarMenu tapBarMenu;
    private ImageView addCurve;
    private ImageView addMarker;
    private ImageView deleteCurve;
    private ImageView deleteMarker;
    private MDDialog featuresDisplay;
    private ImageButton editModeIndicator;

    private TickerView scoreTickerView;

    private int featureDisplayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_annotation);
        annotationGLSurfaceView = findViewById(R.id.gl_surface_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar_annotation);
        setSupportActionBar(toolbar);

        annotationViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(AnnotationViewModel.class);
        annotationViewModel.getWorkStatus().observe(this, new Observer<WorkStatus>() {
            @Override
            public void onChanged(WorkStatus workStatus) {
                if (workStatus == null){
                    return;
                }
                switch(workStatus){
                    case OPEN_FILE:
                        annotationGLSurfaceView.openFile();
                        break;
                    case LOAD_ANNOTATION_FILE:
                        annotationGLSurfaceView.loadFile();
                        break;
                    default:
                        ToastEasy("Something wrong with work status !");
                        break;
                }
            }
        });

        annotationViewModel.getAnnotationMode().observe(this, new Observer<AnnotationViewModel.AnnotationMode>() {
            @Override
            public void onChanged(AnnotationViewModel.AnnotationMode annotationMode) {
                if (annotationMode == null){
                    return;
                }
                updateOptionsMenu(annotationMode);
                updateUI(annotationMode);
            }
        });

        annotationViewModel.getAnalyzeSwcResults().observe(this, new Observer<List<double[]>>() {
            @Override
            public void onChanged(List<double[]> features) {
                if (features == null){
                    ToastEasy("Empty features !");
                    return;
                }
                featureDisplayId = 0;
                displayAnalyzeResults(features);
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

        ImageInfoRepository.getInstance().getScreenCapture().observe(this, new Observer<FilePath<?>>() {
            @Override
            public void onChanged(FilePath<?> filePath) {
                screenCapture((Uri) filePath.getData());
            }
        });
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

    private void updateUI(AnnotationViewModel.AnnotationMode annotationMode){
        resetUI4AllMode();
        switch (annotationMode){
            case LOCAL_FILE_EDITABLE:
                showCommonUI();
                showUI4LocalFileMode();
                break;
            case BIG_DATA:
                showCommonUI();
                showUI4BigDataMode();
                break;
            case NONE:
                Log.e(TAG,"Default UI");
                break;
            default:
                ToastEasy("Something wrong with annotation mode !");
        }
    }

    private void updateOptionsMenu(AnnotationViewModel.AnnotationMode annotationMode) {
        toolbar.getMenu().clear();
        switch (annotationMode) {
            case NONE:
                toolbar.inflateMenu(R.menu.annotation_menu_basic);
                break;
            case LOCAL_FILE_EDITABLE:
            case LOCAL_FILE_UNEDITABLE:
            case BIG_DATA:
                toolbar.inflateMenu(R.menu.annotation_menu_open_file);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.annotation_menu_basic, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load:
                Log.e(TAG,"load file");
                loadLocalFile();
                return true;

            case R.id.file:
                Log.e(TAG,"open file");
                openFile();
                return true;

            case R.id.share:
                annotationGLSurfaceView.screenCapture();
                return true;

            case R.id.more:
                Log.e(TAG,"more functions");
                moreFunctions();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null){
            return;
        }

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case OPEN_LOCAL_FILE:
                    Log.e(TAG,"open local file !");
                    annotationViewModel.openLocalFile(data);
                    break;
                case OPEN_ANALYSIS_SWC:
                    Log.e(TAG,"open analysis swc !");
                    annotationViewModel.analyzeSwcFile(data);
                    break;
                case LOAD_LOCAL_FILE:
                    Log.e(TAG,"load local file !");
                    annotationViewModel.loadLocalFile(data);
                    break;
                default:
                    ToastEasy("UnSupportable request type !");
            }
        }else {
            ToastEasy("Something wrong when get content !");
        }
    }

    private void openFile(){
        new XPopup.Builder(this)
                .asCenterList("File Open", new String[]{"Open BigData", "Open LocalFile"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String item) {
                                switch (item) {
                                    case "Open LocalFile":
                                        openLocalFile();
                                        break;
                                    case "Open BigData":
                                        break;
                                    default:
                                        ToastEasy("Something wrong in function openFile !");
                                }
                            }
                        })
                .show();
    }

    private void openLocalFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_LOCAL_FILE);
    }

    private void loadLocalFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, LOAD_LOCAL_FILE);
    }

    private void screenCapture(Uri uri){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        startActivity(Intent.createChooser(intent, "Share from Hi5"));
    }

    private void moreFunctions(){
        String[] centerList = new String[] {"Analyze Swc", "Filter by example", "Animate", "Settings"};
        new XPopup.Builder(this)
                .maxHeight(1500)
                .asCenterList("More Functions...", centerList,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Analyze Swc":
                                        analyzeSwc();
                                        break;
                                    case "Animate":
                                        break;
                                    case "Filter image by example":
                                        break;
                                    case "Settings":
//                                        settings();
                                        break;
                                    default:
                                        ToastEasy("Something wrong with more functions...");
                                }
                            }
                        })
                .show();
    }

    private void analyzeSwc(){
        new XPopup.Builder(this)
                .asCenterList("Morphology calculate", new String[] {"Analyze swc file", "Analyze current tracing"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Analyze swc file":
                                        analyzeSwcFile();
                                        break;
                                    case "Analyze current tracing":
                                        analyzeCurTracing();
                                        break;
                                    default:
                                        ToastEasy("Default in analysis");
                                }
                            }
                        })
                .show();
    }

    private void analyzeSwcFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, OPEN_ANALYSIS_SWC);
    }

    private void analyzeCurTracing(){
        NeuronTree neuronTree = annotationGLSurfaceView.getNeuronTree();
        if (neuronTree.listNeuron.isEmpty()){
            ToastEasy("Empty tracing, do nothing");
        } else {
            annotationViewModel.analyzeCurTracing(neuronTree);
        }
    }

    private void showCommonUI(){
        if (commonView == null){
            // load layout view
            LinearLayout.LayoutParams lpCommon = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            commonView = getLayoutInflater().inflate(R.layout.annotation_common, null);
            this.addContentView(commonView, lpCommon);

            editModeIndicator = findViewById(R.id.edit_mode_indicator);
            tapBarMenu = findViewById(R.id.tapBarMenu);
            addCurve = tapBarMenu.findViewById(R.id.draw_i);
            addMarker = tapBarMenu.findViewById(R.id.pinpoint);
            deleteCurve = tapBarMenu.findViewById(R.id.delete_curve);
            deleteMarker = tapBarMenu.findViewById(R.id.delete_marker);
            ImageView autoReconstruction = tapBarMenu.findViewById(R.id.auto_reconstruction);
            BoomMenuButton boomMenuButton = tapBarMenu.findViewById(R.id.expanded_menu);

            tapBarMenu.setOnClickListener(v -> tapBarMenu.toggle());
            addCurve.setOnClickListener(this::onMenuItemClick);
            addMarker.setOnClickListener(this::onMenuItemClick);
            deleteCurve.setOnClickListener(this::onMenuItemClick);
            deleteMarker.setOnClickListener(this::onMenuItemClick);
            autoReconstruction.setOnClickListener(this::onMenuItemClick);

            // All is lambda expression
            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .listener(index -> annotationGLSurfaceView.setEditMode(EditMode.CHANGE_CURVE_TYPE))
                    .normalImageRes(R.drawable.ic_change_curve_type).normalText("Change Curve Color"));

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .listener(index -> annotationGLSurfaceView.setEditMode(EditMode.CHANGE_MARKER_TYPE))
                    .normalImageRes(R.drawable.ic_change_marker_type).normalText("Change Marker Color"));

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .listener(index -> annotationGLSurfaceView.setEditMode(EditMode.SPLIT))
                    .normalImageRes(R.drawable.ic_split).normalText("Split"));

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .listener(index -> annotationGLSurfaceView.setEditMode(EditMode.DELETE_MULTI_MARKER))
                    .normalImageRes(R.drawable.ic_delete_multimarker).normalText("Delete Multi Markers"));

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder().listener(index -> {
                ToastEasy("GD tracing algorithm start !");
                executorService.submit(() -> annotationGLSurfaceView.GD());
            }).normalImageRes(R.drawable.ic_gd_tracing).normalText("GD-Tracing"));

            boomMenuButton.addBuilder(new TextOutsideCircleButton.Builder()
                    .listener(index -> annotationGLSurfaceView.clearAllTracing())
                    .normalImageRes(R.drawable.ic_clear).normalText("Clear Tracing"));

        } else {
            commonView.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    public void onMenuItemClick(View view) {
        // resetUI
        addCurve.setImageResource(R.drawable.ic_draw_main);
        addMarker.setImageResource(R.drawable.ic_marker_main);
        deleteCurve.setImageResource(R.drawable.ic_delete_curve_normal);
        deleteMarker.setImageResource(R.drawable.ic_marker_delete_normal);

        switch (view.getId()) {
            case R.id.draw_i:
                if (annotationGLSurfaceView.setEditMode(EditMode.PAINT_CURVE)){
                    addCurve.setImageResource(R.drawable.ic_draw);
                }
                break;
            case R.id.pinpoint:
                if (annotationGLSurfaceView.setEditMode(EditMode.PINPOINT)){
                    addMarker.setImageResource(R.drawable.ic_add_marker);
                }
                break;
            case R.id.auto_reconstruction:
                // TODO: run app2 here | async
                ToastEasy("APP2 tracing algorithm start !");
                executorService.submit(() -> annotationGLSurfaceView.APP2());
                break;
            case R.id.delete_curve:
                if (annotationGLSurfaceView.setEditMode(EditMode.DELETE_CURVE)){
                    deleteCurve.setImageResource(R.drawable.ic_delete_curve);
                }
                break;
            case R.id.delete_marker:
                if (annotationGLSurfaceView.setEditMode(EditMode.DELETE_MARKER)){
                    deleteMarker.setImageResource(R.drawable.ic_marker_delete);
                }
        }
    }

    private void showUI4LocalFileMode(){
        if (localFileModeView == null){
            // load layout view
            LinearLayout.LayoutParams lp4LocalFileMode = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            localFileModeView = getLayoutInflater().inflate(R.layout.annotation_local_file, null);
            this.addContentView(localFileModeView, lp4LocalFileMode);

            // set onClickListener for buttons
            ImageButton zoomIn = findViewById(R.id.zoomIn);
            ImageButton zoomOut = findViewById(R.id.zoomOut);
            ImageButton rotate = findViewById(R.id.rotate);

            zoomIn.setOnClickListener(v -> annotationGLSurfaceView.zoomIn());
            zoomOut.setOnClickListener(v -> annotationGLSurfaceView.zoomOut());
            rotate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        } else {
            localFileModeView.setVisibility(View.VISIBLE);
        }
    }

    private void showUI4BigDataMode(){
        if (bigDataModeView == null){
            // load layout view
            LinearLayout.LayoutParams lp4BigDataMode = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            bigDataModeView = getLayoutInflater().inflate(R.layout.annotation_big_data, null);
            this.addContentView(bigDataModeView, lp4BigDataMode);

        } else {
            bigDataModeView.setVisibility(View.VISIBLE);
        }
    }

    private void resetUI4AllMode(){
        hideUI4LocalFileMode();
        hideUI4BigDataMode();
        hideCommonUI();
    }

    private void hideUI4LocalFileMode(){
        if (localFileModeView != null){
            localFileModeView.setVisibility(View.GONE);
        }
    }

    private void hideUI4BigDataMode(){
        if (bigDataModeView != null){
            bigDataModeView.setVisibility(View.GONE);
        }
    }

    private void hideCommonUI(){
        if (commonView != null){
            commonView.setVisibility(View.GONE);
        }
    }


    /**
     * display the result of morphology calculate
     * @param featureList the features of result
     */
    @SuppressLint("DefaultLocale")
    private void displayAnalyzeResults(final List<double[]> featureList) {
        final String[] title;
        final int[] titleId;
        final int[] contentId;
        final int[] itemLayoutId;

        double[] result = featureList.get(featureDisplayId);
        String[] subtitle = new String[featureList.size()];
        for (int i = 0; i < featureList.size(); i++) {
            if (featureList.size() > 1) {
                subtitle[i] = String.format("Measured features Tree %d/%d", i + 1, featureList.size());
            } else {
                subtitle[i] = "";
            }
        }

        title = new String[]{
                "number of nodes",
                "soma surface",
                "number of stems",
                "number of bifurcations",
                "number of branches",
                "number of tips",
                "overall width",
                "overall height",
                "overall depth",
                "average diameter",
                "total length",
                "total surface",
                "total volume",
                "max euclidean distance",
                "max path distance",
                "max branch order",
                "average contraction",
                "average fragmentation",
                "average parent-daughter ratio",
                "average bifurcation angle local",
                "average bifurcation angle remote",
                "Hausdorff dimension"
        };

        titleId = new int[]{R.id.title0, R.id.title1, R.id.title2, R.id.title3, R.id.title4,
                R.id.title5, R.id.title6, R.id.title7, R.id.title8, R.id.title9,
                R.id.title10, R.id.title11, R.id.title12, R.id.title13, R.id.title14,
                R.id.title15, R.id.title16, R.id.title17, R.id.title18, R.id.title19,
                R.id.title20, R.id.title21};

        contentId = new int[]{R.id.content0, R.id.content1, R.id.content2, R.id.content3, R.id.content4,
                R.id.content5, R.id.content6, R.id.content7, R.id.content8, R.id.content9,
                R.id.content10, R.id.content11, R.id.content12, R.id.content13, R.id.content14,
                R.id.content15, R.id.content16, R.id.content17, R.id.content18, R.id.content19,
                R.id.content20, R.id.content21};

        itemLayoutId = new int[]{R.id.RL0, R.id.RL1, R.id.RL2, R.id.RL3, R.id.RL4,
                R.id.RL5, R.id.RL6, R.id.RL7, R.id.RL8, R.id.RL9,
                R.id.RL10, R.id.RL11, R.id.RL12, R.id.RL13, R.id.RL14,
                R.id.RL15, R.id.RL16, R.id.RL17, R.id.RL18, R.id.RL19,
                R.id.RL20, R.id.RL21};

        featuresDisplay = new MDDialog.Builder(this)
                .setContentView(R.layout.analysis_result)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        // 这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        // analysis_result next page
                        Button next = (Button) contentView.findViewById(R.id.ar_right);
                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (featureList.size() > 1) {
                                    featureDisplayId = (featureDisplayId + 1) % featureList.size();
                                    double[] result = featureList.get(featureDisplayId);
                                    setResultContentView(contentView, title, titleId, contentId, itemLayoutId, result, subtitle[featureDisplayId]);
                                    featuresDisplay.show();
                                }
                            }
                        });
                        Button previous = (Button) contentView.findViewById(R.id.ar_left);
                        previous.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (featureList.size() > 1) {
                                    featureDisplayId = (featureDisplayId - 1 + featureList.size()) % featureList.size();
                                    double[] result = featureList.get(featureDisplayId);
                                    setResultContentView(contentView, title, titleId, contentId, itemLayoutId, result, subtitle[featureDisplayId]);
                                    featuresDisplay.show();
                                }
                            }
                        });
                        setResultContentView(contentView, title, titleId, contentId, itemLayoutId, result, subtitle[featureDisplayId]);
                    }
                })
                .setShowTitle(false)
                .create();
        featuresDisplay.show();
    }

    private void setResultContentView(View contentView, String[] title, int[] titleId, int[] contentId, int[] itemLayoutId, double[] result, String titleString){
        if (title.length == 8) {
            for (int i = 8; i < itemLayoutId.length; i++) {
                contentView.findViewById(itemLayoutId[i]).setVisibility(View.GONE);
            }
        } else if (title.length == 22) {
            for (int i = 8; i < itemLayoutId.length; i++) {
                contentView.findViewById(itemLayoutId[i]).setVisibility(View.VISIBLE);
            }
        }

        TextView dialogTitle = contentView.findViewById(R.id.title);
        dialogTitle.setText(titleString);

        String resultString;
        int num;
        for (int i = 0; i < title.length; i++) {
            TextView tx = contentView.findViewById(titleId[i]);
            tx.setText(title[i]);

            TextView ct = contentView.findViewById(contentId[i]);
            if (title[i].substring(0, 6).equals("number") || title[i].substring(0, 6).equals("max br")) {
                resultString = ": " + String.format("%d", (int) result[i + 1]);
            } else {
                num = valueDisplayLength(result[i + 1]);
                resultString = ": " + String.format("%." + String.format("%d", num) + "f", (float) result[i + 1]);
            }
            ct.setText(resultString);
        }
    }

    /**
     * format output of morphology feature's value
     * @param value feature's value
     * @return Number of digits
     */
    private int valueDisplayLength(double value) {
        String valueString = (value + "").split("\\.")[0];
        int len = valueString.length();
        if (len >= 8) {
            return 0;
        } else {
            return Math.min((8 - len), 4);
        }
    }

    public static void start(Context context){
        Intent intent = new Intent(context, AnnotationActivity.class);
        context.startActivity(intent);
    }
}