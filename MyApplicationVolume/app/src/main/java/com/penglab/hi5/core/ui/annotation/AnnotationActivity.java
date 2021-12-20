package com.penglab.hi5.core.ui.annotation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;

public class AnnotationActivity extends AppCompatActivity {

    private AnnotationViewModel annotationViewModel;

    private Context annotationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);

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
}