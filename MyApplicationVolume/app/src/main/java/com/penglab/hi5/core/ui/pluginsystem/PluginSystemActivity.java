package com.penglab.hi5.core.ui.pluginsystem;

import static com.penglab.hi5.basic.image.Image4DSimple.TAG;
import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.R;
import com.penglab.hi5.core.render.view.AnnotationGLSurfaceView;
import com.penglab.hi5.core.ui.ResourceResult;
import com.penglab.hi5.core.ui.ViewModelFactory;
import com.penglab.hi5.data.Result;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class PluginSystemActivity extends AppCompatActivity {
    private AnnotationGLSurfaceView annotationGLSurfaceView;
    private static final int OPEN_LOCAL_FILE = 1;
    private Toolbar toolbar;
    private View commonView;
    private PluginSystemViewModel pluginSystemViewModel;
    private static Context mainContext;

    private TextView imageIdLocationTextView;
    private ImageButton getPlugin;
    private ImageButton getModel;


    @SuppressLint("MissingInflatedId")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
        annotationGLSurfaceView = findViewById(R.id.gl_surface_view);
        annotationGLSurfaceView.setBigData(true);
        pluginSystemViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(PluginSystemViewModel.class);
        toolbar = findViewById(R.id.toolbar_plugin);
        imageIdLocationTextView = findViewById(R.id.imageid_location_text_view);
        setSupportActionBar(toolbar);

        updateOptionsMenu();
        updateUI();

        pluginSystemViewModel.getPluginDataSource().getPluginListResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result instanceof Result.Success) {
                    String[] data = (String[]) ((Result.Success<?>) result).getData();
                    Set<String> set = new HashSet<String>(Arrays.asList(data));
                    String[] listShow = set.toArray(new String[set.size()]);
                    new XPopup.Builder(PluginSystemActivity.this).
                            maxHeight(1350).
                            maxWidth(800).
                            asCenterList("Image Processing",
                                    listShow, new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {
                                            ToastEasy("Click" + text);
                                            pluginSystemViewModel.handlePluginList(text.trim());
                                            pluginSystemViewModel.downloadPluginResult();
                                        }
                                    }).show();
                } else if (result instanceof Result.Error) {
                    ToastEasy(result.toString());
                }
            }
        });

        pluginSystemViewModel.getPluginDataSource().getImageListResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result instanceof Result.Success) {
                    String[] data = (String[]) ((Result.Success<?>) result).getData();
                    Set<String> set = new HashSet<String>(Arrays.asList(data));
                    String[] listShow = set.toArray(new String[set.size()]);

                    new XPopup.Builder(PluginSystemActivity.this).
                            maxHeight(1350).
                            maxWidth(800).
                            asCenterList("Image List",
                                    listShow, new OnSelectListener() {
                                        @Override
                                        public void onSelect(int position, String text) {
                                            ToastEasy("Click" + text);
                                            pluginSystemViewModel.handleImageList(text.trim());
                                        }
                                    }).show();
                } else if (result instanceof Result.Error) {
                    ToastEasy(result.toString());
                }
            }
        });

        pluginSystemViewModel.getPluginImageResult().observe(this, new Observer<ResourceResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(ResourceResult resourceResult) {
                if (resourceResult == null){
                    return;
                }
                if (resourceResult.isSuccess()){
                    annotationGLSurfaceView.openFile();
                    String imageId= pluginSystemViewModel.getCurrentImageId();
                    Log.e("textview",imageId);
                    imageIdLocationTextView.setText(imageId);
                    annotationGLSurfaceView.setImageInfoInRender(imageId);
                } else {
                    ToastEasy(resourceResult.getError());
                }
            }
        });

        pluginSystemViewModel.getPluginDataSource().getDownloadPluginImageResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null) {
                    return;
                }
                pluginSystemViewModel.handleDownloadImageResult(result);
            }
        });

        pluginSystemViewModel.getPluginDataSource().getOriginImageResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result == null){
                    return;
                }
                pluginSystemViewModel.handleDownloadImageResult(result);
            }
        });

        pluginSystemViewModel.getPluginDataSource().getModelImageResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if(result == null){
                    return;
                }
                pluginSystemViewModel.handelModelImageResult(result);
            }
        });
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        annotationGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
        annotationGLSurfaceView.onPause();
    }

    @Override
    protected void onRestart() {
        Log.e(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainContext = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plugin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updateOptionsMenu() {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.plugin_menu);
    }

    private void updateUI() {
        if (commonView == null) {
            // load layout view
            LinearLayout.LayoutParams lpCommon = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            commonView = getLayoutInflater().inflate(R.layout.plugin_main, null);
            this.addContentView(commonView, lpCommon);

            getPlugin = findViewById(R.id.get_plugin_list_button);
            getModel = findViewById(R.id.get_model_list_button);

            getPlugin.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pluginSystemViewModel.getPluginList();
                }
            });
            getModel.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    pluginSystemViewModel.getModelResult();
                }
            });
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.file:
                openAllFile();
                return true;

            case R.id.share:
                annotationGLSurfaceView.screenCapture();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    private void openAllFile(){
        new XPopup.Builder(this)
                .asCenterList("File Open", new String[]{"Open File From Server", "Open LocalFile"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String item) {
                                switch (item) {
                                    case "Open File From Server":
                                        openFileFromServer();
                                        break;
                                    case "Open LocalFile":
                                        openLocalFile();
                                        break;
                                    default:
                                        ToastEasy("Something wrong in function openFile !");
                                }
                            }
                        })
                .show();
    }

    private void openFileFromServer(){
        pluginSystemViewModel.getImageList();
    }

    private void openLocalFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_LOCAL_FILE);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PluginSystemActivity.class);
        context.startActivity(intent);
    }
}
