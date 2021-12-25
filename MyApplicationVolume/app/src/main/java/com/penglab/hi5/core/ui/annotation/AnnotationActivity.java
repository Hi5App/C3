package com.penglab.hi5.core.ui.annotation;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.ViewModelFactory;

public class AnnotationActivity extends AppCompatActivity {

    private static final String TAG = "AnnotationActivity";

    private static final int OPEN_LOCAL_FILE = 1;
    private static final int OPEN_ANALYSIS_SWC = 2;
    private static final int LOAD_LOCAL_FILE = 3;

    private Context annotationContext;
    private AnnotationViewModel annotationViewModel;
    private AnnotationGLSurfaceView annotationGLSurfaceView;

    private View localFileModeView;
    private View bigDataModeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_annotation);
        annotationGLSurfaceView = findViewById(R.id.gl_surface_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_annotation);
        setSupportActionBar(toolbar);

        annotationContext = this;
        annotationViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(AnnotationViewModel.class);
        annotationViewModel.getWorkStatus().observe(this, new Observer<WorkStatus>() {
            @Override
            public void onChanged(WorkStatus workStatus) {
                if (workStatus == null){
                    return;
                }

                switch(workStatus){
                    case OPEN_FILE:
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

                switch (annotationMode){
                    case LOCAL_FILE:
                        showUI4LocalFileMode();
                        break;
                    case BIG_DATA:
                    case NONE:
                    default:
                        ToastEasy("Something wrong with annotation mode !");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.annotation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.file:
                Log.e(TAG,"open file");
                openFile();
                return true;

            case R.id.more:
                Log.e(TAG,"more functions");
                showUI4LocalFileMode();
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

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case OPEN_LOCAL_FILE:
                    Log.e(TAG,"open local file !");
                    annotationViewModel.openLocalFile(data);
                    break;
                case OPEN_ANALYSIS_SWC:
                    Log.e(TAG,"open analysis swc !");
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

//    private void loadUI4LocalFileMode(){
//        LinearLayout.LayoutParams lp4LocalFileMode = new LinearLayout.LayoutParams(
//                LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
//        localFileModeView = getLayoutInflater().inflate(R.layout.annotation_local_file, null);
//        this.addContentView(localFileModeView, lp4LocalFileMode);
//    }
//
//    private void loadUI4BigDataMode(){
//        LinearLayout.LayoutParams lp4BigDataMode = new LinearLayout.LayoutParams(
//                LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
//        bigDataModeView = getLayoutInflater().inflate(R.layout.annotation_big_data, null);
//        this.addContentView(bigDataModeView, lp4BigDataMode);
//    }

    private void showUI4LocalFileMode(){

        resetUI4AllMode();
        if (localFileModeView == null){
            // loadUI4LocalFileMode
            LinearLayout.LayoutParams lp4LocalFileMode = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            localFileModeView = getLayoutInflater().inflate(R.layout.annotation_local_file, null);
            this.addContentView(localFileModeView, lp4LocalFileMode);
        } else {
            localFileModeView.setVisibility(View.VISIBLE);
        }
    }

    private void showUI4BigDataMode(){

        resetUI4AllMode();
        if (bigDataModeView == null){
            // loadUI4BigDataMode
            LinearLayout.LayoutParams lp4BigDataMode = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            bigDataModeView = getLayoutInflater().inflate(R.layout.annotation_big_data, null);
            this.addContentView(bigDataModeView, lp4BigDataMode);
        } else {
            bigDataModeView.setVisibility(View.VISIBLE);
        }
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

    private void resetUI4AllMode(){
        hideUI4LocalFileMode();
        hideUI4BigDataMode();
    }

    public static void start(Context context){
        Intent intent = new Intent(context, AnnotationActivity.class);
        context.startActivity(intent);
    }
}