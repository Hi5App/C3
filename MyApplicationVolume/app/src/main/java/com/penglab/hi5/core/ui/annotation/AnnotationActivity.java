package com.penglab.hi5.core.ui.annotation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;

public class AnnotationActivity extends AppCompatActivity {

    private final String TAG = "AnnotationActivity";
    private AnnotationViewModel annotationViewModel;
    private Context annotationContext;
    private AnnotationGLSurfaceView annotationGLSurfaceView;

    private View localFileModeView;
    private View bigDataModeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(annotationGLSurfaceView);
        setContentView(R.layout.activity_annotation);
        annotationGLSurfaceView = findViewById(R.id.gl_surface_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_annotation);
        setSupportActionBar(toolbar);

        annotationContext = this;
        annotationViewModel = new AnnotationViewModel();
        annotationViewModel.getFileInfoState().conPath.observe(this, new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(String s) {
                FileInfoState fileInfoState = annotationViewModel.getFileInfoState();
                new XPopup.Builder(annotationContext)
                        .maxHeight(1350)
                        .maxWidth(800)
                        .asCenterList("BigData File", fileInfoState.sonFileList,
                                new OnSelectListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onSelect(int position, String text) {
                                        switch (text) {
                                            case "Create A New Room":
                                                createFilePopup();
                                                break;
                                            default:
                                                annotationViewModel.loadFile(text);
                                        }
                                    }
                                })
                        .show();
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
                hideUI4BigDataMode();
                loadUI4LocalFileMode();
                return true;

            case R.id.more:
                Log.e(TAG,"more functions");
                hideUI4LocalFileMode();
                loadUI4BigDataMode();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void loadUI4LocalFileMode(){
        LinearLayout.LayoutParams lp4LocalFileMode = new LinearLayout.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
        localFileModeView = getLayoutInflater().inflate(R.layout.annotation_local_file, null);
        this.addContentView(localFileModeView, lp4LocalFileMode);
    }

    private void loadUI4BigDataMode(){
        LinearLayout.LayoutParams lp4BigDataMode = new LinearLayout.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
        bigDataModeView = getLayoutInflater().inflate(R.layout.annotation_big_data, null);
        this.addContentView(bigDataModeView, lp4BigDataMode);
    }

    private void showUI4LocalFileMode(){
        if (localFileModeView != null){
            localFileModeView.setVisibility(View.VISIBLE);
        }
    }

    private void showUI4BigDataMode(){
        if (bigDataModeView != null){
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

    private void createFilePopup() {
        new XPopup.Builder(this)
                .asInputConfirm("Create Room", "Input the name of the new room",
                        new OnInputConfirmListener() {
                            @Override
                            public void onConfirm(String text) {
                                annotationViewModel.createFile(text);
                            }
                        })
                .show();
    }

    public static void start(Context context){
        Intent intent = new Intent(context, AnnotationActivity.class);
        context.startActivity(intent);
    }
}