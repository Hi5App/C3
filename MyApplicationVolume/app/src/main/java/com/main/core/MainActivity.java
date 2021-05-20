package com.main.core;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.feature_calc_func.MorphologyCalculate;
import com.learning.pixelclassification.PixelClassification;
import com.learning.randomforest.RandomForest;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.main.basic.CrashHandler;
import com.main.basic.FileManager;
import com.main.basic.Image4DSimple;
import com.main.basic.ImageMarker;
import com.main.basic.LocationSimple;
import com.main.basic.NeuronSWC;
import com.main.basic.NeuronTree;
import com.main.core.fileReader.annotationReader.AnoReader;
import com.main.core.fileReader.annotationReader.ApoReader;
import com.main.core.fileReader.imageReader.BigImgReader;
import com.main.dataStore.SettingFileManager;
import com.tracingfunc.app2.ParaAPP2;
import com.tracingfunc.app2.V3dNeuronAPP2Tracing;
import com.tracingfunc.gd.CurveTracePara;
import com.tracingfunc.gd.V3dNeuronGDTracing;
import com.tracingfunc.gd.V_NeuronSWC;
import com.tracingfunc.gd.V_NeuronSWC_list;
import com.warkiz.widget.IndicatorSeekBar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import cn.carbs.android.library.MDDialog;

import static com.main.core.MyApplication.ToastEasy;
import static com.main.dataStore.SettingFileManager.setSelectSource;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class MainActivity extends BaseActivity {
    public static final String NAME = "com.example.core.MainActivity";
    public static final String File_path = "com.example.myfirstapp.MESSAGE";
    private static final String TAG = "MainActivity";

    private Timer timer = null;
    private TimerTask timerTask;


    private static MyGLSurfaceView myGLSurfaceView;
    private static MyRenderer myrenderer;
    private static Context mainContext;
    private String filepath = "";
    private boolean ifDeletingMultiMarker = false;
    private boolean ifChangeMarkerType = false;
    private boolean ifPainting = false;
    private boolean ifPoint = false;
    private boolean ifImport = false;
    private boolean ifAnalyze = false;
    private boolean ifUpload = false;

    private boolean ifDeletingMarker = false;
    private boolean ifDeletingLine = false;
    private boolean ifSpliting = false;
    private boolean ifChangeLineType = false;
    private boolean ifSwitch = false;
    private boolean select_img = false;
    private boolean ifLoadLocal = false;
    private boolean ifRemote = false;
    private boolean ifButtonShowed = true;

    private boolean[] temp_mode = new boolean[8];

    private boolean ifAnimation = false;
    private static Button Zoom_in;
    private static Button Zoom_out;

    private static Button Zoom_in_Big;
    private static Button Zoom_out_Big;
    private ImageButton Rotation_i;
    private ImageButton Hide_i;
    private static ImageButton Undo_i;
    private static ImageButton Redo_i;
    private Button Switch;
    private ImageButton animation_i;
    private ImageButton draw_i;
    private ImageButton tracing_i;
    private ImageButton classify_i;
    private static TextView filenametext;

    private static ImageButton navigation_left;
    private static ImageButton navigation_right;
    private static ImageButton navigation_up;
    private static ImageButton navigation_down;
    private static ImageButton navigation_location;
    private static Button navigation_front;
    private static Button navigation_back;


    private FrameLayout.LayoutParams lp_undo_i;
    private FrameLayout.LayoutParams lp_left_i;
    private FrameLayout.LayoutParams lp_right_i;
    private static FrameLayout.LayoutParams lp_up_i;
    private FrameLayout.LayoutParams lp_down_i;
    private FrameLayout.LayoutParams lp_front_i;
    private FrameLayout.LayoutParams lp_back_i;
    private static FrameLayout.LayoutParams lp_nacloc_i;

    private static FrameLayout.LayoutParams lp_res_list;
    private static FrameLayout.LayoutParams lp_animation_i;
    private static FrameLayout.LayoutParams lp_undo;
    private static FrameLayout.LayoutParams lp_redo;

    private static FrameLayout.LayoutParams lp_room_id;
    private static FrameLayout.LayoutParams lp_user_list;

    private boolean[][]select= {{true,true,true,false,false,false,false},
            {true,true,true,false,false,false,false},
            {false,false,false,false,false,false,false},
            {false,false,false,false,false,false,false},
            {false,false,false,false,false,false,false},
            {true,true,true,false,false,false,false}};

    private RandomForest rf = null;


    @SuppressLint("StaticFieldLeak")
    private BigImgReader bigImgReader;


    private static final int PICKFILE_REQUEST_CODE = 100;

    private LinearLayout ll_top;
    private LinearLayout ll_bottom;
    private static LinearLayout ll_file;

    private int measure_count = 0;
    private List<double[]> fl;

    private static boolean isBigData_Local;
    private static ProgressBar progressBar;

    // permission code
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int TOAST_INFO_STATIC = 5;

    private ArrayList<Float> lineDrawed = new ArrayList<Float>();

    private String currentPhotoPath; // 指定一个不会跟其他文件产生冲突的文件名，用于后面相机拍照的图片的保存

    private File showPic;
    private Uri picUri;


    private static final int animation_id = 0;
    private int rotation_speed = 36;

    private long exitTime = 0;

    private static String filename = "";

    private enum PenColor {
        WHITE, BLACK, RED, BLUE, PURPLE, CYAN, YELLOW, GREEN
    }

    private BasePopupView drawPopupView;

    public static String USERNAME = "username";

    private SoundPool soundPool;
    private final int SOUNDNUM = 4;
    private int [] soundId;

    private float bgmVolume = 0f;
    private float buttonVolume = 1.0f;
    private float actionVolume = 1.0f;


    @SuppressLint("HandlerLeak")
    private static Handler puiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    /*
                    popupView.show();
                     */
                    break;

                case 1:
                    /*
                    popupView.dismiss();
                     */
                    break;

                case 2:
                    setButtonsBigData();
                    if (isBigData_Local){
                        String filename = SettingFileManager.getFilename_Local(context);
                        String offset = SettingFileManager.getoffset_Local(context, filename);

                        String offset_x = offset.split("_")[0];
                        String offset_y = offset.split("_")[1];
                        String offset_z = offset.split("_")[2];
                        Toast.makeText(getContext(),"Current offset: " + "x: " + offset_x + " y: " + offset_y + " z: " + offset_z, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 3:
                    Toast.makeText(context,"Time out, please try again!",Toast.LENGTH_SHORT).show();
                    break;

                case 5:
                    String Toast_msg = msg.getData().getString("Toast_msg");
                    Toast.makeText(getContext(),Toast_msg, Toast.LENGTH_SHORT).show();
                    break;

                case 6:
                    progressBar.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        }
    };






    /**
     * The onCreate Function
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"------------------ onCreate ------------------");

        /*
        set layout
         */
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        music module  -------------------------------------------------------------------------------------------------
         */
        File volumeFile = new File(context.getExternalFilesDir(null).toString() + "/Settings/volume.txt");
        if (volumeFile.exists()){
            try {
                BufferedReader volumeReader = new BufferedReader(new InputStreamReader(new FileInputStream(volumeFile)));
                String volumeStr = volumeReader.readLine();
                if (volumeStr != null) {
                    String[] volumes = volumeStr.split(" ");
                    Log.d(TAG, "VolumeStr: " + volumeStr);
                    if (volumes.length == 3){
                        bgmVolume = Float.parseFloat(volumes[0]);
                        buttonVolume = Float.parseFloat(volumes[1]);
                        actionVolume = Float.parseFloat(volumes[2]);

                        Log.d(TAG, "Volumes: " + Arrays.toString(volumes));
                        Log.d(TAG, "Volumes: " + bgmVolume + " " + buttonVolume + " " + actionVolume);

                        MusicServer.setVolume(bgmVolume);
                    }
                }
                volumeReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        initMusicService();

        soundPool = new SoundPool(SOUNDNUM, AudioManager.STREAM_MUSIC, 5);
        soundId = new int[SOUNDNUM];
        soundId[0] = soundPool.load(this, R.raw.piano2, 1);
        soundId[1] = soundPool.load(this, R.raw.piano1, 1);
        soundId[2] = soundPool.load(this, R.raw.button01, 1);
        soundId[3] = soundPool.load(this, R.raw.fail, 1);

        /*
        music module end  -------------------------------------------------------------------------------------------------
         */




        Intent intent = getIntent();
        String msgOOM = intent.getStringExtra(MyRenderer.OUT_OF_MEMORY);

        if (msgOOM != null)
            Toast.makeText(this, msgOOM, Toast.LENGTH_SHORT).show();

        myrenderer = new MyRenderer(this);
        myGLSurfaceView = new MyGLSurfaceView(this);

        isBigData_Local  = false;

        /*
        Button Layout ------------------------------------------------------------------------------------------------------------------------
         */

        FrameLayout ll = (FrameLayout) findViewById(R.id.container);
        ll.addView(myGLSurfaceView);

        LinearLayout ll_up = new LinearLayout(this);
        ll_up.setOrientation(LinearLayout.VERTICAL);

        LinearLayout ll_hs_back = new LinearLayout(this);
        ll_hs_back.setOrientation(LinearLayout.HORIZONTAL);
        ll_hs_back.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout ll_space = new LinearLayout(this);
        ll_space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));


        ll_file = new LinearLayout(this);
        FrameLayout.LayoutParams lp_filename = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        ll_file.setLayoutParams(lp_filename);
        ll_file.setBackgroundColor(Color.YELLOW);

        filenametext = new TextView(this);
        filenametext.setText("");
        filenametext.setTextColor(Color.BLACK);
        ll_file.addView(filenametext);
        ll_file.setVisibility(View.GONE);

        ll_top = new LinearLayout(this);
        ll_bottom = new LinearLayout(this);

        HorizontalScrollView hs_top = new HorizontalScrollView(this);

        ll_up.addView(ll_file);

        ll_hs_back.addView(hs_top, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll_hs_back.addView(ll_space);

        ll_up.addView(ll_hs_back);
        ll.addView(ll_up);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(1080, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        this.addContentView(ll_bottom, lp);
        ll_bottom.setLayoutParams(lp);

        hs_top.addView(ll_top);


        Zoom_in = new Button(this);
        Zoom_in.setText("+");
        Zoom_in_Big = new Button(this);
        Zoom_in_Big.setText("+");

        Zoom_out = new Button(this);
        Zoom_out.setText("-");
        Zoom_out_Big = new Button(this);
        Zoom_out_Big.setText("-");

        FrameLayout.LayoutParams lp_zoom_in = new FrameLayout.LayoutParams(120, 120);
        lp_zoom_in.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_zoom_in.setMargins(0, 0, 20, 290);
        this.addContentView(Zoom_in_Big, lp_zoom_in);

        FrameLayout.LayoutParams lp_zoom_out = new FrameLayout.LayoutParams(120, 120);
        lp_zoom_out.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_zoom_out.setMargins(0, 0, 20, 200);
        this.addContentView(Zoom_out_Big, lp_zoom_out);

        Zoom_in_Big.setVisibility(View.GONE);
        Zoom_out_Big.setVisibility(View.GONE);

        FrameLayout.LayoutParams lp_zoom_in_no = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_in_no.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        this.addContentView(Zoom_in, lp_zoom_in_no);

        FrameLayout.LayoutParams lp_zoom_out_no = new FrameLayout.LayoutParams(100, 150);
        lp_zoom_out_no.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        this.addContentView(Zoom_out, lp_zoom_out_no);





        /*
        button onclick event  -----------------------------------------------------------------------------
         */
        Zoom_in.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myrenderer.zoom_in();
                        myGLSurfaceView.requestRender();
                    }
                }).start();

            }
        });


        Zoom_out.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        myrenderer.zoom_out();
                        myGLSurfaceView.requestRender();
                    }
                }).start();

            }
        });

        Zoom_in_Big.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                myrenderer.zoom_in();
                myGLSurfaceView.requestRender();

            }
        });


        Zoom_out_Big.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if(!myrenderer.getIfFileLoaded()){
                    Toast.makeText(getContext(), "Please load image first!", Toast.LENGTH_LONG).show();
                    return;
                }

                myrenderer.zoom_out();
                myGLSurfaceView.requestRender();

            }
        });



        FrameLayout.LayoutParams lp_draw_i = new FrameLayout.LayoutParams(200, 160);

        draw_i = new ImageButton(this);
        draw_i.setImageResource(R.drawable.ic_draw_main);
        ll_top.addView(draw_i,lp_draw_i);

        draw_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if (!myrenderer.getIfFileLoaded()){
                    Toast.makeText(context, "Please load a File First", Toast.LENGTH_SHORT).show();
                    return;
                }
                Draw_list(v);
            }
        });


        FrameLayout.LayoutParams lp_tracing_i = new FrameLayout.LayoutParams(200, 160);


        tracing_i=new ImageButton(this);
        tracing_i.setImageResource(R.drawable.ic_neuron);
        ll_top.addView(tracing_i,lp_tracing_i);

        tracing_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Tracing(v);
            }
        });


        FrameLayout.LayoutParams lp_classify_i = new FrameLayout.LayoutParams(200, 160);

        classify_i=new ImageButton(this);
        classify_i.setImageResource(R.drawable.ic_classify_mid);
        ll_top.addView(classify_i,lp_classify_i);

        classify_i.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                PixelClassification(v);
            }
        });


        FrameLayout.LayoutParams lp_rotation = new FrameLayout.LayoutParams(120, 120);
        lp_rotation.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_rotation.setMargins(0, 0, 160, 20);

        Rotation_i = new ImageButton(this);
        Rotation_i.setImageResource(R.drawable.ic_3d_rotation_red_24dp);
        Rotation_i.setBackgroundResource(R.drawable.circle_normal);

        this.addContentView(Rotation_i, lp_rotation);

        Rotation_i.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                Rotation();
            }
        });

        FrameLayout.LayoutParams lp_hide = new FrameLayout.LayoutParams(120, 120);
        lp_hide.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        lp_hide.setMargins(0, 0, 20, 20);
//        @SuppressLint("ResourceType") XmlPullParser parser = MainActivity.this.getResources().getXml(R.layout.activity_main);
//        AttributeSet attributes = Xml.asAttributeSet(parser);
//        MyRockerView rockerView = new MyRockerView(context, attributes);

        Hide_i = new ImageButton(this);
        Hide_i.setImageResource(R.drawable.ic_not_hide);
        Hide_i.setBackgroundResource(R.drawable.circle_normal);

        this.addContentView(Hide_i, lp_hide);

        Hide_i.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                if (!myrenderer.getIfFileLoaded()){
                    Toast.makeText(context, "Please load a File First", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (myrenderer.getIfShowSWC()){
                    myrenderer.setIfShowSWC(false);
                    myGLSurfaceView.requestRender();
                    Hide_i.setImageResource(R.drawable.ic_hide);
                } else {
                    myrenderer.setIfShowSWC(true);
                    myGLSurfaceView.requestRender();
                    Hide_i.setImageResource(R.drawable.ic_not_hide);
                }
            }
        });

        lp_undo = new FrameLayout.LayoutParams(120, 120);

//        lp_undo.gravity = Gravity.RIGHT | Gravity.TOP;
        lp_undo.setMargins(0, 20, 20, 0);

        Undo_i = new ImageButton(this);
        Undo_i.setImageResource(R.drawable.ic_undo);
        Undo_i.setBackgroundResource(R.drawable.circle_normal);
        ll_hs_back.addView(Undo_i, lp_undo);
//        this.addContentView(Undo_i, lp_undo);

        Undo_i.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                boolean undoSuccess = false;
                try {
                    undoSuccess = myrenderer.undo2();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                if (!undoSuccess) {
                    Toast.makeText(context, "nothing to undo", Toast.LENGTH_SHORT).show();
                }
                myGLSurfaceView.requestRender();
            }
        });

        lp_redo = new FrameLayout.LayoutParams(120, 120);
        lp_redo.setMargins(0, 20, 20, 0);

        Redo_i = new ImageButton(this);
        Redo_i.setImageResource(R.drawable.ic_redo);
        Redo_i.setBackgroundResource(R.drawable.circle_normal);
        ll_hs_back.addView(Redo_i, lp_redo);

        Redo_i.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                boolean redoSuccess = myrenderer.redo();
                if (!redoSuccess){
                    Toast_in_Thread("nothing to redo");
                }
                myGLSurfaceView.requestRender();
            }
        });


        FrameLayout.LayoutParams lp_downsample = new FrameLayout.LayoutParams(120, 120);


        Switch = new Button(this);
        Switch.setText("Pause");

        Switch.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Switch();
            }
        });


        lp_animation_i = new FrameLayout.LayoutParams(200, 160);
        animation_i = new ImageButton(this);
        animation_i.setImageResource(R.drawable.ic_animation);
        animation_i.setId(animation_id);

        animation_i.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Animation(v);
            }
        });



        lp_undo_i = new FrameLayout.LayoutParams(230, 160);


        lp_left_i = new FrameLayout.LayoutParams(100, 150);
        lp_left_i.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;

        navigation_left = new ImageButton(this);
        navigation_left.setImageResource(R.drawable.ic_chevron_left_black_24dp);

        navigation_left.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Left");
            }
        });



        lp_right_i = new FrameLayout.LayoutParams(100, 150);
        lp_right_i.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        navigation_right = new ImageButton(this);
        navigation_right.setImageResource(R.drawable.ic_chevron_right_black_24dp);

        navigation_right.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Right");
            }
        });




        lp_up_i = new FrameLayout.LayoutParams(150, 100);
        lp_up_i.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        lp_up_i.setMargins(0, 310, 0, 0);


        navigation_up = new ImageButton(this);
        navigation_up.setImageResource(R.drawable.ic_expand_less_black_24dp);

        navigation_up.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Top");
            }
        });




        lp_down_i = new FrameLayout.LayoutParams(150, 100);
        lp_down_i.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        lp_down_i.setMargins(0, 0, 0, 0);

        navigation_down = new ImageButton(this);
        navigation_down.setImageResource(R.drawable.ic_expand_more_black_24dp);

        navigation_down.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Bottom");
            }
        });


        lp_front_i = new FrameLayout.LayoutParams(120, 120);
        lp_front_i.gravity = Gravity.LEFT | Gravity.BOTTOM;
        lp_front_i.setMargins(20, 0, 0, 290);

        navigation_front = new Button(this);
        navigation_front.setText("F");
        navigation_front.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Front");
            }
        });


        lp_back_i = new FrameLayout.LayoutParams(120, 120);
        lp_back_i.gravity = Gravity.LEFT | Gravity.BOTTOM;
        lp_back_i.setMargins(20, 0, 0, 200);

        navigation_back = new Button(this);
        navigation_back.setText("B");
        navigation_back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                Block_navigate("Back");
            }
        });


        lp_nacloc_i = new FrameLayout.LayoutParams(90, 90);
        lp_nacloc_i.gravity = Gravity.TOP | Gravity.LEFT;
        lp_nacloc_i.setMargins(20, 350, 0, 0);

        navigation_location = new ImageButton(this);
        navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
        navigation_location.setBackgroundResource(R.drawable.circle_normal);
        navigation_location.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

//                Set_Nav_Mode();
            }
        });




        /*
        add button to the view  -------------------------------------------------------------------
         */
        this.addContentView(navigation_left, lp_left_i);
        this.addContentView(navigation_right, lp_right_i);
        this.addContentView(navigation_up, lp_up_i);
        this.addContentView(navigation_down, lp_down_i);
        this.addContentView(navigation_front, lp_front_i);
        this.addContentView(navigation_back, lp_back_i);
        this.addContentView(navigation_location, lp_nacloc_i);


        navigation_left.setVisibility(View.GONE);
        navigation_right.setVisibility(View.GONE);
        navigation_up.setVisibility(View.GONE);
        navigation_down.setVisibility(View.GONE);
        navigation_front.setVisibility(View.GONE);
        navigation_back.setVisibility(View.GONE);
        navigation_location.setVisibility(View.GONE);



        // set Check Mode  & DownSample Mode
        myrenderer.setIfNeedDownSample(preferenceSetting.getDownSampleMode());
        myrenderer.resetContrast(preferenceSetting.getContrast());


        // Set the permission for user
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }


        myGLSurfaceView.requestRender();
        bigImgReader = new BigImgReader();



        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 200);
        params.gravity = Gravity.CENTER;
        this.addContentView(progressBar, params);
        progressBar.setVisibility(View.GONE);

        mainContext = this;

        initDir();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    //renderer 的生存周期和activity保持一致
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        Intent bgmIntent = new Intent(this, MusicServer.class);
        stopService(bgmIntent);

        mainContext = null;

        if (timer != null){
            timer.cancel();
            timer = null;
        }

        if (timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause start");
        myGLSurfaceView.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume start");
        Log.v("Path", filepath);
        myGLSurfaceView.onResume();

    }

    @Override
    protected void onStop() {
        Intent bgmIntent = new Intent(MainActivity.this, MusicServer.class);
        stopService(bgmIntent);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        initMusicService();
        super.onRestart();
    }

    /**
     * quick start for MainActivity
     * @param context
     */

    public static void start(Context context) {
        start(context, null);
    }


    /**
     * quick start for MainActivity
     * @param context
     * @param extras
     */
    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }


    public static void actionStart(Context context, String username){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(USERNAME, username);
        context.startActivity(intent);
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }



    /*
    init dir
     */
    private void initDir(){

        try{
            String dir_str = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + context.getResources().getString(R.string.app_name);;
            File dir = new File(dir_str);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String dir_str_server = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + context.getResources().getString(R.string.app_name) + "/Server";
            File dir_server = new File(dir_str_server);
            if (!dir_server.exists()) {
                dir_server.mkdirs();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }


    /*
    for service ------------------------------------------------------------------------------------
     */


    private void initMusicService(){
        Log.e(TAG,"init MusicService");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MusicServer.class));
        } else {
            startService(new Intent(this, MusicServer.class));
        }
    }

    /*
    for service ------------------------------------------------------------------------------------
     */






    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            Toast.makeText(getContext(), "横屏", Toast.LENGTH_LONG).show();
        }else{
//            Toast.makeText(getContext(), "竖屏", Toast.LENGTH_LONG).show();
        }
    }



    /**
     * on top bar menu created, link res/menu/main.xml
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }





    /**
     * open file
     */
    public void File_icon(){

        new XPopup.Builder(this)
                .asCenterList("File Open", new String[]{"Open LocalFile", "Load SwcFile", "Open DemoFile"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                                switch (text) {
                                    case "Open LocalFile":
                                        openLocalFile();
                                        break;

                                    case "Load SwcFile":
                                        loadSwcFile();
                                        break;

                                    case "Open DemoFile":
                                        openDemoFile();
                                        break;

                                    default:
                                        ToastEasy("Default in file");
                                }
                            }
                        })
                .show();

    }

    private void loadSwcFile() {
        if (!ifImport) {
            ifImport = true;
            ifAnalyze = false;
            ifUpload = false;
            ifLoadLocal = false;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }


    private void openLocalFile(){
        ifLoadLocal = true;
        ifImport = false;
        ifAnalyze = false;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent,1);
        }catch (Exception e){
            e.printStackTrace();
            ToastEasy("Error when open file!" + e.getMessage());
        }
    }


    private void openDemoFile(){
        new XPopup.Builder(this)
                .asCenterList("More Functions...", new String[]{"DemoData1", "DemoData2", "DemoData3", "DemoData4", "DemoData5"},
                        new OnSelectListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSelect(int position, String text) {
                                Log.e(TAG,"File name: " + text);
                                myrenderer.loaDemoFile("Demo:" + text);
                                myGLSurfaceView.requestRender();
                                setFileName(text);
                            }
                        })
                .show();
    }



    /**
     * for draw button
     * @param v
     */
    private void Draw_list(View v){
        String[] drawList = new String[]{"For Marker", "For Curve", "Clear Tracing", "Exit Drawing Mode"};
        drawPopupView = new XPopup.Builder(this)
                .atView(v)
                .autoDismiss(false)
                .asAttachList(drawList,
                        new int[]{}, new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text){
                                    case "For Marker":
                                        markerProcessList(v);
                                        break;
                                    case "For Curve":
                                        curveProcessList(v);
                                        break;
                                    case "Clear Tracing":
                                        myrenderer.deleteAllTracing();
                                        myGLSurfaceView.requestRender();
                                        drawPopupView.dismiss();
                                        break;
                                    case "Exit Drawing Mode":
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        draw_i.setImageResource(R.drawable.ic_draw_main);
                                        ll_bottom.removeView(Switch);
                                        drawPopupView.dismiss();
//                                        ll_top.removeView(buttonUndo_i);
                                        break;
                                }
                            }
                        }).show();
    }

    private void markerProcessList(View v){
        String[] processList = new String[]{"PinPoint   ", "Delete Marker", "Delete MultiMarker", "Set MColor", "Change MColor", "Change All MColor"};
        new XPopup.Builder(this)
                .atView(v)
                .offsetX(580)
                .isRequestFocus(false)
                .popupPosition(PopupPosition.Right)
                .asAttachList(processList, new int[]{}, new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                        switch (text){
                            case "PinPoint   ":
                                if (!myrenderer.ifImageLoaded()){
                                    ToastEasy("Please load a image first");
                                    return;
                                }
                                ifPoint = !ifPoint;
                                ifPainting = false;
                                ifDeletingMarker = false;
                                ifDeletingLine = false;
                                ifSpliting = false;
                                ifChangeLineType = false;
                                ifChangeMarkerType = false;
                                ifDeletingMultiMarker = false;
                                if (ifPoint && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_add_marker);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                      ll_top.addView(buttonUndo_i, lp_undo_i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifPoint = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                  ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Delete Marker":
                                ifDeletingMarker = !ifDeletingMarker;
                                ifPainting = false;
                                ifPoint = false;
                                ifDeletingLine = false;
                                ifSpliting = false;
                                ifChangeLineType = false;
                                ifChangeMarkerType = false;
                                ifDeletingMultiMarker = false;
                                if (ifDeletingMarker && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_marker_delete);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                      ll_top.addView(buttonUndo_i, lp_undo_i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifDeletingMarker = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                  ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Delete MultiMarker":
                                ifDeletingMultiMarker = !ifDeletingMultiMarker;
                                ifPainting = false;
                                ifPoint = false;
                                ifDeletingMarker = false;
                                ifSpliting = false;
                                ifChangeLineType = false;
                                ifChangeMarkerType = false;
                                ifDeletingLine = false;
                                if (ifDeletingMultiMarker && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_draw_main);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                      ll_top.addView(buttonUndo_i, lp_undo_i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifDeletingMultiMarker = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                  ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Set MColor":
                                markerPenSet();
                                break;

                            case "Change MColor":
                                ifChangeMarkerType = !ifChangeMarkerType;
                                ifDeletingLine = false;
                                ifPainting = false;
                                ifPoint = false;
                                ifDeletingMarker = false;
                                ifChangeLineType = false;
                                ifSpliting = false;
                                ifDeletingMultiMarker = false;
                                if (ifChangeMarkerType && !ifSwitch) {
                                    draw_i.setImageResource(R.drawable.ic_draw_main);

                                    try {
                                        ifSwitch = false;
                                        ll_bottom.addView(Switch);
//                                      ll_top.addView(buttonUndo_i, lp_undo_i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    ifSwitch = false;
                                    ifChangeMarkerType = false;
                                    Switch.setText("Pause");
                                    Switch.setTextColor(Color.BLACK);
                                    draw_i.setImageResource(R.drawable.ic_draw_main);
                                    ll_bottom.removeView(Switch);
//                                  ll_top.removeView(buttonUndo_i);
                                }
                                break;

                            case "Change All MColor":
                                try {
                                    myrenderer.changeAllMarkerType();
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                                myGLSurfaceView.requestRender();
                                break;

                        }
                        drawPopupView.dismiss();
                    }
                }).show();
    }



    private void curveProcessList(View v){
        String[] processList = new String[]{"Draw Curve", "Delete Curve", "Split       ", "Set PenColor", "Change PenColor", "Change All PenColor"};
        new XPopup.Builder(this)
                .atView(v)
                .offsetX(580)
                .asAttachList(processList, new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text){
                                    case "Draw Curve":
                                        if (!myrenderer.ifImageLoaded()){
                                            ToastEasy("Please load a image first");
                                            return;
                                        }
                                        ifPainting = !ifPainting;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifDeletingLine = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifPainting && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_draw);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifPainting = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Delete Curve":
                                        ifDeletingLine = !ifDeletingLine;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifDeletingLine && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_delete_curve);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifDeletingLine = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Split       ":
                                        ifSpliting = !ifSpliting;
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifChangeLineType = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if (ifSpliting && !ifSwitch) {
                                            draw_i.setImageResource(R.drawable.ic_split);

                                            try {
                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifSpliting = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
                                        }
                                        break;

                                    case "Set PenColor":
                                        // 调用选择画笔窗口
                                        penSet();
                                        break;

                                    case "Change PenColor":
                                        ifChangeLineType = !ifChangeLineType;
                                        ifDeletingLine = false;
                                        ifPainting = false;
                                        ifPoint = false;
                                        ifDeletingMarker = false;
                                        ifSpliting = false;
                                        ifChangeMarkerType = false;
                                        ifDeletingMultiMarker = false;
                                        if(ifChangeLineType && !ifSwitch){
//                                            Draw.setText("Change PenColor");
//                                            Draw.setTextColor(Color.RED);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);

                                            try {
                                                ifSwitch = false;
//                                                ifChangeLineType = false;
//                                                Switch.setText("Pause");
//                                                Switch.setTextColor(Color.BLACK);
//                                                ifSwitch = false;
                                                ll_bottom.addView(Switch);
//                                                ll_top.addView(buttonUndo_i, lp_undo_i);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                        } else {
                                            ifSwitch = false;
                                            ifChangeLineType = false;
                                            Switch.setText("Pause");
                                            Switch.setTextColor(Color.BLACK);
                                            draw_i.setImageResource(R.drawable.ic_draw_main);
                                            ll_bottom.removeView(Switch);
//                                            ll_top.removeView(buttonUndo_i);
//                                            draw_i.setImageResource(R.drawable.ic_draw_main);

                                        }
                                        break;

                                    case "Change All PenColor":
                                        try {
                                            myrenderer.changeAllType();
                                        } catch (CloneNotSupportedException e) {
                                            e.printStackTrace();
                                        }
                                        myGLSurfaceView.requestRender();
                                        break;

                                }
                                drawPopupView.dismiss();
                            }
                        }).show();
    }



    public void penSet(){
        String [] pcolor = new String[1];
        new MDDialog.Builder(this)
                .setContentView(R.layout.pen_choose)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view

                        final Spinner chooseColor = contentView.findViewById(R.id.pencolor);
                        chooseColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                pcolor[0] = chooseColor.getSelectedItem().toString();
                                Log.v("onItemSelected", chooseColor.getSelectedItem().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                    }
                })
                .setTitle("Pen Set")
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        //这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view，目的是方便在确定/取消按键中对contentView进行操作，如获取数据等。
//                        EditText et1 = (EditText) contentView.findViewById(R.id.pencolor);
                        String color  = pcolor[0];

                        if( !color.isEmpty()){
                            myrenderer.pencolorchange(PenColor.valueOf(color).ordinal());
                            Log.v(TAG,"pen color is " + color);
                            ToastEasy("penColor set !");
                        }else{
                            ToastEasy("Please make sure all the information is right !!!");
                        }

                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                    }
                })
                .setWidthMaxDp(600)
                .create()
                .show();
    }

    public void markerPenSet(){
        String [] pcolor = new String[1];
        new MDDialog.Builder(this)
                .setContentView(R.layout.marker_pen_choose)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view

                        final Spinner chooseColor = contentView.findViewById(R.id.markercolor);
                        chooseColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                pcolor[0] = chooseColor.getSelectedItem().toString();
                                Log.v("onItemSelected", chooseColor.getSelectedItem().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                })
                .setTitle("Marker Color Set")
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        //这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view，目的是方便在确定/取消按键中对contentView进行操作，如获取数据等。
                        String color = pcolor[0];

                        if( !color.isEmpty()){

                            myrenderer.markercolorchange(PenColor.valueOf(color).ordinal());
                            Log.v(TAG,"marker color is " + color);
                            ToastEasy("markerColor set !");

                        }else{
                            ToastEasy("Please make sure all the information is right !");
                        }

                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                    }
                })
                .setWidthMaxDp(600)
                .create()
                .show();
    }




    /**
     * function for the Tracing button
     * @param v the button: tracing
     */
    private void Tracing(final View v) {

        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            ToastEasy("Please load image first !");
            return;
        }

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"APP2", "GD", "Save SwcFile"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text) {
                                    case "GD":
                                        try {
                                            Log.v(TAG, "GD-Tracing start ~");
                                            ToastEasy("GD-Tracing start !");
                                            progressBar.setVisibility(View.VISIBLE);
                                            timer = new Timer();
                                            timerTask = new TimerTask() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void run() {
                                                    try {
                                                        GDTracing();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            timer.schedule(timerTask, 1000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        break;

                                    case "APP2":
                                        try {
                                            Log.v(TAG, "APP2-Tracing start !");
                                            ToastEasy("APP2-Tracing start !");
                                            progressBar.setVisibility(View.VISIBLE);
                                            timer = new Timer();
                                            timerTask = new TimerTask() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void run() {
                                                    try {
                                                        APP2();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            timer.schedule(timerTask, 1000);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case "Save SwcFile":
                                        SaveSwc();
                                        break;

                                }
                            }
                        })
                .show();
    }

    private void SaveSwc() {
        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.save_swc)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                    }
                })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                        EditText swcName = contentView.findViewById(R.id.swcname);
                        String swcFileName = swcName.getText().toString();
                        if (swcFileName == ""){
                            ToastEasy("The name should not be empty !");
                        }
                        myrenderer.reNameCurrentSwc(swcFileName);

                        String dir_str = "/storage/emulated/0/" + context.getResources().getString(R.string.app_name) + "/SwcSaved";
                        File dir = new File(dir_str);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        String error = null;
                        try {
                            error = myrenderer.saveCurrentSwc(dir_str);
                        } catch (Exception e) {
                            ToastEasy(e.getMessage());
                        }
                        if (!error.equals("")) {
                            if (error.equals("This file already exits")){
                                AlertDialog aDialog = new AlertDialog.Builder(mainContext)
                                        .setTitle("This file already exits")
                                        .setMessage("Are you sure to overwrite it?")
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String errorMessage = "";
                                                try{
                                                    errorMessage = myrenderer.oversaveCurrentSwc(dir_str);
                                                    if (errorMessage.equals(""))
                                                        ToastEasy("Overwrite successfully !");
                                                    if (errorMessage == "Overwrite failed!")
                                                        ToastEasy("Overwrite failed !");

                                                }catch (Exception e){
                                                    System.out.println(errorMessage);
                                                    ToastEasy(e.getMessage());
                                                }
                                            }
                                        })

                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .create();
                                aDialog.show();
                            }
                        } else{
                            ToastEasy("save SWC to " + dir + "/" + swcFileName + ".swc", Toast.LENGTH_LONG);
                        }
                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle("Save SWC File")
                .create();
        mdDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void APP2() throws Exception {
        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            ToastEasy("Please load image first !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        ArrayList<ImageMarker> markers = myrenderer.getMarkerList().getMarkers();
        try {
            ParaAPP2 p = new ParaAPP2();
            p.p4dImage = img;
            p.xc0 = p.yc0 = p.zc0 = 0;
            p.xc1 = (int) p.p4dImage.getSz0() - 1;
            p.yc1 = (int) p.p4dImage.getSz1() - 1;
            p.zc1 = (int) p.p4dImage.getSz2() - 1;
            p.landmarks = new LocationSimple[markers.size()];
            p.bkg_thresh = -1;
            for (int i = 0; i < markers.size(); i++) {
                p.landmarks[i] = new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
            }
            System.out.println("---------------start---------------------");
            V3dNeuronAPP2Tracing.proc_app2(p);
            NeuronTree nt = p.resultNt;
            for (int i = 0; i < nt.listNeuron.size(); i++) {
                nt.listNeuron.get(i).type = 4;
                if (nt.listNeuron.get(i).parent == -1) {
                    NeuronSWC s = nt.listNeuron.get(i);
                    ImageMarker m = new ImageMarker(s.x, s.y, s.z);
                    m.type = 2;
                    myrenderer.getMarkerList().add(m);
                }
            }
            System.out.println("size: " + nt.listNeuron.size());

            ToastEasy("APP2-Tracing finish, size of result swc: " + Integer.toString(nt.listNeuron.size()));
            myrenderer.importNeuronTree(nt);
            myrenderer.saveUndo();
            myGLSurfaceView.requestRender();
            progressBar.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            ToastEasy(e.getMessage());
            progressBar.setVisibility(View.INVISIBLE);
        }


    }

    private void GDTracing() throws Exception {
        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            ToastEasy("Please load image first !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        ArrayList<ImageMarker> markers = myrenderer.getMarkerList().getMarkers();
        if (markers.size() <= 1) {
            Log.v("GDTracing", "Please generate at least two markers!");
            ToastEasy("Please produce at least two markers !");
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        LocationSimple p0 = new LocationSimple(markers.get(0).x, markers.get(0).y, markers.get(0).z);
        Vector<LocationSimple> pp = new Vector<LocationSimple>();
        for (int i = 1; i < markers.size(); i++) {
            LocationSimple p = new LocationSimple(markers.get(i).x, markers.get(i).y, markers.get(i).z);
            pp.add(p);
        }

        NeuronTree outswc = new NeuronTree();
        CurveTracePara curveTracePara = new CurveTracePara();

        try {
            outswc = V3dNeuronGDTracing.v3dneuron_GD_tracing(img, p0, pp, curveTracePara, 1.0);
        } catch (Exception e) {
            ToastEasy(e.getMessage());
            progressBar.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < outswc.listNeuron.size(); i++) {
            outswc.listNeuron.get(i).type = 5;
        }

        ToastEasy("GD-Tracing finished, size of result swc: " + Integer.toString(outswc.listNeuron.size()));
        myrenderer.importNeuronTree(outswc);
        myrenderer.saveUndo();
        myGLSurfaceView.requestRender();
        progressBar.setVisibility(View.INVISIBLE);

    }



    private void PixelClassification(final View v) {

        Image4DSimple img = myrenderer.getImg();
        if(img == null || !img.valid()){
            ToastEasy("Please load image first !");
            return;
        }

        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Filter by example"}, new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text) {
                                    case "Filter by example":
                                        //调用像素分类接口，显示分类结果
                                        progressBar.setVisibility(View.VISIBLE);

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pixelClassification();
                                                puiHandler.sendEmptyMessage(6);
                                            }
                                        }).start();
                                        break;
                                }
                            }
                        })
                .show();
    }

    private void pixelClassification() {

        Image4DSimple img = myrenderer.getImg();
        if(img == null){

            ToastEasy("Please load image first !");
            return;
        }
        Image4DSimple outImg = new Image4DSimple();

        NeuronTree nt = myrenderer.getNeuronTree();
        PixelClassification p = new PixelClassification();

        boolean[][] selections = select;
        System.out.println("select is");
        System.out.println(select);
        p.setSelections(selections);

        ToastEasy("pixel  classification start !");

        try{
            outImg = p.getPixelClassificationResult(img,nt);
            System.out.println("outImg: "+outImg.getSz0()+" "+outImg.getSz1()+" "+outImg.getSz2()+" "+outImg.getSz3());
            System.out.println(outImg.getData().length);

            myrenderer.resetImg(outImg);
            myGLSurfaceView.requestRender();
        }catch (Exception e){
            ToastEasy(e.getMessage());
        }
    }


    private void ShareScreenShot() {

        myrenderer.setTakePic(true, this);
        myGLSurfaceView.requestRender();
    }









    /**
     * call the corresponding function when button in top bar clicked
     * @param item
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.file:
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                File_icon();
                return true;

            case R.id.share:
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                ShareScreenShot();
                return true;

            case R.id.more:
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                More_icon();
                return true;

            case R.id.view:
                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);
                if (ifButtonShowed){
                    hideButtons();
                    item.setIcon(R.drawable.ic_visibility_off_black_24dp);
                } else {
                    showButtons();
                    item.setIcon(R.drawable.ic_visibility_black_24dp);
                }
                return true;
            default:
                return true;
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastEasy("Press again to exit the program");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }





    /**
     * pop up a menu when button more is clicked, include analyze swc file, sensor information, downsample mode, animate and version
     */
    public void More_icon(){

        new XPopup.Builder(this)
                .asCenterList("More Functions...", new String[]{"Analyze Swc", "Animate", "Settings", "Crash Info", "Help", "About"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                soundPool.play(soundId[2], buttonVolume, buttonVolume, 0, 0, 1.0f);

                                switch (text) {
                                    case "Analyze Swc":
                                        AnalyzeSwc();
                                        break;

                                    case "Animate":
                                        if (myrenderer.ifImageLoaded()){
                                            ifPainting = false;
                                            ifPoint = false;
                                            ifDeletingMarker = false;
                                            ifDeletingLine = false;
                                            ifSpliting = false;
                                            ifChangeLineType = false;
                                            setAnimation();
                                        }else {
                                            ToastEasy("Please Load a Img First !");
                                        }
                                        break;

                                    case "Settings":
                                        setSettings();
                                        break;

                                    case "Crash Info":
                                        CrashInfoShare();
                                        break;

                                    case "About":
                                        About();;
                                        break;

                                    case "Help":
                                        try{
                                            Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                                            startActivity(helpIntent);
                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        break;

                                    default:
                                        ToastEasy("Default in More Functions...");

                                }
                            }
                        })
                .show();

    }


    private void Animation(final View v) {
        new XPopup.Builder(this)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(new String[]{"Start", "Pause", "Resume", "Stop"},
                        new int[]{},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Start":
                                        myrenderer.setIfDownSampling(true);
                                        myrenderer.myAnimation.Start();
                                        break;

                                    case "Pause":
                                        myrenderer.setIfDownSampling(false);
                                        myrenderer.myAnimation.Pause();
                                        break;

                                    case "Resume":
                                        myrenderer.setIfDownSampling(true);
                                        myrenderer.myAnimation.Resume();
                                        break;

                                    case "Stop":
                                        myrenderer.setIfDownSampling(false);
                                        myrenderer.myAnimation.Stop();
                                        ifAnimation = false;
                                        ll_top.removeView(animation_i);
                                        Rotation_i.setVisibility(View.VISIBLE);
                                        break;

                                    default:
                                        ToastEasy("There is something wrong in animation");
                                }
                            }
                        })
                .show();

    }


    private void Rotation() {

        if (myrenderer.myAnimation != null && myrenderer.ifImageLoaded()){
            ifAnimation = !ifAnimation;

            if (ifAnimation) {
                myrenderer.setIfDownSampling(true);
                Rotation_i.setImageResource(R.drawable.ic_block_red_24dp);
                myrenderer.myAnimation.quickStart();
                myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            } else {
                myrenderer.setIfDownSampling(false);
                Rotation_i.setImageResource(R.drawable.ic_3d_rotation_red_24dp);
                myrenderer.myAnimation.quickStop();
                myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                myGLSurfaceView.requestRender();
            }

        }else {
            ToastEasy("Pleas load a file first !");
        }
    }


    private void Switch() {
        ifSwitch = !ifSwitch;
        if (ifSwitch) {
            Switch.setText("Restart");
            Switch.setTextColor(Color.RED);
            temp_mode[0] = ifPainting;
            temp_mode[1] = ifPoint;
            temp_mode[2] = ifDeletingLine;
            temp_mode[3] = ifDeletingMarker;
            temp_mode[4] = ifSpliting;
            temp_mode[5] = ifChangeLineType;
            temp_mode[6] = ifChangeMarkerType;
            temp_mode[7] = ifDeletingMultiMarker;

            ifPainting = false;
            ifPoint = false;
            ifDeletingLine = false;
            ifDeletingMarker = false;
            ifSpliting = false;
            ifChangeLineType = false;
            ifChangeMarkerType = false;
            ifDeletingMultiMarker = false;

        } else {
            Switch.setText("Pause");
            Switch.setTextColor(Color.BLACK);
            ifPainting            = temp_mode[0];
            ifPoint               = temp_mode[1];
            ifDeletingLine        = temp_mode[2];
            ifDeletingMarker      = temp_mode[3];
            ifSpliting            = temp_mode[4];
            ifChangeLineType      = temp_mode[5];
            ifChangeMarkerType    = temp_mode[6];
            ifDeletingMultiMarker = temp_mode[7];
        }
    }


    private void About() {
        new XPopup.Builder(this)
                .asConfirm("Hi5: VizAnalyze Big 3D Images", "By Peng lab @ BrainTell. \n\n" +
                                "Version: 20210520a 20:18 UTC+8 build",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                            }
                        })
                .setCancelText("Cancel")
                .setConfirmText("Confirm")
                .show();
    }


    private void AnalyzeSwc() {
        new XPopup.Builder(this)
                .asCenterList("morphology calculate", new String[]{"Analyze a Swc file", "Analyze current tracing"},
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                switch (text) {
                                    case "Analyze a Swc file":
                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                        intent.setType("*/*");    // 设置类型，我这里是任意类型，任意后缀的可以这样写。
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        startActivityForResult(intent, 1);
                                        ifAnalyze = true;
                                        ifImport = false;
                                        ifUpload = false;
                                        break;

                                    case "Analyze current tracing":
                                        NeuronTree nt = myrenderer.getNeuronTree();
                                        if (nt.listNeuron.isEmpty()) {
                                            ToastEasy("Empty tracing, do nothing");
                                            break;
                                        }
                                        MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
                                        List<double[]> features = morphologyCalculate.calculatefromNT(nt, false);
                                        fl = new ArrayList<double[]>(features);
                                        if (features.size() != 0) displayResult(features);
                                        else ToastEasy("The file is empty");
                                        break;

                                    default:
                                        ToastEasy("Default in analysis");

                                }
                            }
                        })
                .show();


    }

    MDDialog.Builder ar_mdDialog_bd = new MDDialog.Builder(this).setContentView(R.layout.analysis_result);
    MDDialog ar_mdDialog = null;

    /**
     * display the result of morphology calculate
     * @param featureList the features of result
     */
    @SuppressLint("DefaultLocale")
    private void displayResult(final List<double[]> featureList) {
        final String[] title;
        final int[] id_title;
        final int[] id_content;
        final int[] id_rl;
        if (measure_count > featureList.size() - 1) {
            measure_count = 0;
        } else if (measure_count < 0) {
            measure_count = featureList.size() - 1;
        }
        double[] result = featureList.get(measure_count);
        String[] subtitle = new String[featureList.size()];
        for (int i = 0; i < featureList.size(); i++) {
            if (featureList.size() > 1) {
                subtitle[i] = String.format("Tree %d/%d", i + 1, featureList.size());
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

        id_title = new int[]{R.id.title0, R.id.title1, R.id.title2, R.id.title3, R.id.title4,
                R.id.title5, R.id.title6, R.id.title7, R.id.title8, R.id.title9,
                R.id.title10, R.id.title11, R.id.title12, R.id.title13, R.id.title14,
                R.id.title15, R.id.title16, R.id.title17, R.id.title18, R.id.title19,
                R.id.title20, R.id.title21};

        id_content = new int[]{R.id.content0, R.id.content1, R.id.content2, R.id.content3, R.id.content4,
                R.id.content5, R.id.content6, R.id.content7, R.id.content8, R.id.content9,
                R.id.content10, R.id.content11, R.id.content12, R.id.content13, R.id.content14,
                R.id.content15, R.id.content16, R.id.content17, R.id.content18, R.id.content19,
                R.id.content20, R.id.content21};

        id_rl = new int[]{R.id.RL0, R.id.RL1, R.id.RL2, R.id.RL3, R.id.RL4,
                R.id.RL5, R.id.RL6, R.id.RL7, R.id.RL8, R.id.RL9,
                R.id.RL10, R.id.RL11, R.id.RL12, R.id.RL13, R.id.RL14,
                R.id.RL15, R.id.RL16, R.id.RL17, R.id.RL18, R.id.RL19,
                R.id.RL20, R.id.RL21};
        ar_mdDialog = ar_mdDialog_bd
                        .setContentView(R.layout.analysis_result)
                        .setContentViewOperator(new MDDialog.ContentViewOperator() {
                            @Override
                            public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                                //analysis_result next page
                                Button ar_right = (Button) contentView.findViewById(R.id.ar_right);
                                ar_right.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (featureList.size() > 1) {
                                            measure_count++;
                                            ar_mdDialog.dismiss();
                                            displayResult(fl);
                                        }
                                    }
                                });
                                Button ar_left = (Button) contentView.findViewById(R.id.ar_left);
                                ar_left.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (featureList.size() > 1) {
                                            measure_count--;
                                            ar_mdDialog.dismiss();
                                            displayResult(fl);
                                        }
                                    }
                                });
                                if (title.length == 8) {
                                    for (int i = 8; i < id_rl.length; i++) {
                                        contentView.findViewById(id_rl[i]).setVisibility(View.GONE);
                                    }
                                } else if (title.length == 22) {
                                    for (int i = 8; i < id_rl.length; i++) {
                                        contentView.findViewById(id_rl[i]).setVisibility(View.VISIBLE);
                                    }
                                }


                                String result_str;
                                int num;
                                for (int i = 0; i < title.length; i++) {
                                    TextView tx = contentView.findViewById(id_title[i]);
                                    tx.setText(title[i]);

                                    TextView ct = contentView.findViewById(id_content[i]);
                                    if (title[i].substring(0, 6).equals("number") || title[i].substring(0, 6).equals("max br")) {
                                        result_str = ": " + String.format("%d", (int) result[i + 1]);
                                    } else {
                                        num = Value_Display_Length(result[i + 1]);
                                        result_str = ": " + String.format("%." + String.format("%d", num) + "f", (float) result[i + 1]);
                                    }
                                    ct.setText(result_str);

                                }
                            }

                        })
                        .setTitle("Measured features " + subtitle[measure_count])
                        .create();
        ar_mdDialog.show();
    }


    /**
     * format output of morphology feature's value
     * @param value feature's value
     * @return Number of digits
     */
    private int Value_Display_Length(double value) {
        String s_value = (value + "").split("\\.")[0];
        int len = s_value.length();
        if (len >= 8) {
            return 0;
        } else if ((8 - len) > 4) {
            return 4;
        } else {
            return 8 - len;
        }
    }


    private void setSettings(){

        boolean [] downsample = new boolean[1];
        boolean [] check = new boolean[1];

        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.settings)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        Switch downsample_on_off = contentView.findViewById(R.id.switch_rotation_mode);
                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.indicator_seekbar);
                        TextView clean_cache = contentView.findViewById(R.id.clean_cache);
                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);

                        boolean ifDownSample = preferenceSetting.getDownSampleMode();
                        int contrast = preferenceSetting.getContrast();

                        downsample_on_off.setChecked(ifDownSample);
                        seekbar.setProgress(contrast);
                        bgmVolumeBar.setProgress((int)(bgmVolume * 100));
                        buttonVolumeBar.setProgress((int)(buttonVolume * 100));
                        actionVolumeBar.setProgress((int)(actionVolume * 100));

                        downsample[0] = downsample_on_off.isChecked();

                        downsample_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                downsample[0] = isChecked;
                            }
                        });


                        clean_cache.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cleanCache();
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        IndicatorSeekBar seekbar = contentView.findViewById(R.id.indicator_seekbar);
                        int contrast = seekbar.getProgress();

                        myrenderer.setIfNeedDownSample(downsample[0]);
                        myrenderer.resetContrast(contrast);

                        Log.v(TAG,"downsample: " + downsample[0] + ", check: " + check[0] + ",contrast: " + contrast);
                        preferenceSetting.setPref(downsample[0], check[0], contrast);
                        myGLSurfaceView.requestRender();

                        SeekBar bgmVolumeBar = contentView.findViewById(R.id.bgSoundBar);
                        SeekBar buttonVolumeBar = contentView.findViewById(R.id.buttonSoundBar);
                        SeekBar actionVolumeBar = contentView.findViewById(R.id.actionSoundBar);

                        bgmVolume = (float)(bgmVolumeBar.getProgress()) / 100.0f;
                        buttonVolume = (float)(buttonVolumeBar.getProgress()) / 100.0f;
                        actionVolume = (float)(actionVolumeBar.getProgress()) / 100.0f;


                        MusicServer.setBgmVolume(bgmVolume);
                        MusicServer.setVolume(bgmVolume);

                        String settingsPath = context.getExternalFilesDir(null).toString() + "/Settings";
                        File settingsFile = new File(settingsPath);
                        if (!settingsFile.exists()){
                            settingsFile.mkdir();
                        }

                        String volumePath = settingsPath + "/volume.txt";
                        File volumeFile = new File(volumePath);
                        if (!volumeFile.exists()){
                            try {
                                volumeFile.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            BufferedWriter volumeWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(volumeFile)));
                            volumeWriter.write(Float.toString(bgmVolume) + " " + Float.toString(buttonVolume) + " " + Float.toString(actionVolume));
                            volumeWriter.flush();
                            volumeWriter.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle("Settings")
                .create();
        mdDialog.show();
    }


    public void cleanCache(){
        AlertDialog aDialog = new AlertDialog.Builder(mainContext)
                .setTitle("Clean All The Img Cache")
                .setMessage("Are you sure to CLEAN ALL IMG CACHE?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImg();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        aDialog.show();
    }


    private void deleteImg(){
        Log.v("BaseActivity","deleteImg()");
        String img_path = context.getExternalFilesDir(null).toString() + "/Img";
        Log.v("BaseActivity","img_path" + img_path);

        File file = new File(img_path);
        recursionDeleteFile(file);
    }


    private void CrashInfoShare(){
        String[] info_path = CrashHandler.getCrashReportFiles(getApplicationContext());
        new XPopup.Builder(this)
                .maxHeight(1350)
                .asCenterList("Select a Crash Report", info_path,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                String file_path = CrashHandler.getCrashFilePath(getApplicationContext()) + "/" + text + ".txt";
                                File file = new File(file_path);
                                if (file.exists()){
                                    Intent intent = new Intent();

                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    intent.setAction(Intent.ACTION_SEND);
                                    intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "com.main.core.provider", new File(file_path)));  //传输图片或者文件 采用流的方式
                                    intent.setType("*/*");   //分享文件
                                    startActivity(Intent.createChooser(intent, "Share From Hi5"));
                                }else {
                                    Toast_in_Thread("File does not exist");
                                }
                            }
                        })
                .show();
    }


    private void setAnimation() {

        final String[] rotation_type = new String[1];
        final boolean [] ifChecked = {false, false};

        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.animation)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view

                        Switch on_off = contentView.findViewById(R.id.switch_animation);
                        on_off.setChecked(ifAnimation);
                        EditText speed = contentView.findViewById(R.id.edit_speed);
                        speed.setText(Integer.toString(rotation_speed));

                        on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                ifChecked[1] = true;
                                if (isChecked) {
                                    ifChecked[0] = true;
                                } else {
                                    ifChecked[0] = false;
                                }
                            }
                        });

                        final Spinner type = contentView.findViewById(R.id.spinner_type);
                        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                rotation_type[0] = type.getSelectedItem().toString();
                                Log.v("onItemSelected", type.getSelectedItem().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        if (ifChecked[1]) {
                            ifAnimation = ifChecked[0];
                        }

                        EditText speed = contentView.findViewById(R.id.edit_speed);
                        String rotation_speed_string = speed.getText().toString();

                        if (rotation_speed_string.isEmpty()){
                            ToastEasy("Make sure the input is right !");
                            setAnimation();
                            return;
                        }
                        rotation_speed = (int) Float.parseFloat(rotation_speed_string);

                        myrenderer.myAnimation.Stop();
                        myrenderer.setIfDownSampling(false);
                        myGLSurfaceView.requestRender();

                        if (ifAnimation) {
                            myrenderer.myAnimation.setAnimation(true, Float.parseFloat(rotation_speed_string), rotation_type[0]);
                            myrenderer.setIfDownSampling(true);

                            Rotation_i.setVisibility(View.GONE);

                            if (ll_top.findViewById(animation_id) == null){
                                ll_top.addView(animation_i,lp_animation_i);
                            }
                            myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

                        } else {

                            Rotation_i.setVisibility(View.VISIBLE);

                            if (ll_top.findViewById(animation_id) != null){
                                ll_top.removeView(animation_i);
                            }

                            myGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

                        }

                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                    }
                })
                .setTitle("Animation")
                .create();
        mdDialog.show();
    }







    /**
     * called when request permission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_PERMISSION_CODE: {
                for (int i = 0; i < permissions.length; i++) {
                    Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
                }
                break;
            }
        }
    }


    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG,"onActivityResult start");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            myrenderer.setPath(showPic.getAbsolutePath());
            myGLSurfaceView.requestRender();
            return;
        }

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String filePath = uri.toString();
            Log.e(TAG, filePath);

            try {
                if (ifImport) {

                    FileManager fileManager = new FileManager();
                    String fileName = fileManager.getFileName(uri);
                    String filetype = fileName.substring(fileName.lastIndexOf(".")).toUpperCase();
                    Log.e(TAG,"FileType: " + filetype + ", FileName: " + fileName);

                    if (myrenderer.getIfFileLoaded()) {
                        System.out.println("------ load local file ------");
                        switch (filetype) {
                            case ".APO":
                                Log.e("MainActivity", uri.toString());
                                ArrayList<ArrayList<Float>> apo = new ArrayList<ArrayList<Float>>();
                                ApoReader apoReader = new ApoReader();
                                apo = apoReader.read(uri);
                                if (apo == null){
                                    ToastEasy("Make sure the .apo file is right");
                                    break;
                                }

                                myrenderer.importApo(apo);
                                myrenderer.saveUndo();
                                break;
                            case ".SWC":
                            case ".ESWC":
                                Log.e(TAG, "onActivityResult: .eswc");
                                NeuronTree nt = NeuronTree.readSWC_file(uri);

                                myrenderer.importNeuronTree(nt);
                                myrenderer.saveUndo();
                                break;

                            case ".ANO":
                                Log.e(TAG, "onActivityResult: .ano");

                                ArrayList<ArrayList<Float>> ano_apo = new ArrayList<ArrayList<Float>>();
                                AnoReader anoReader = new AnoReader();
                                ApoReader apoReader_1 = new ApoReader();
                                anoReader.read(uri);

                                String swc_path = anoReader.getSwc_Path();
                                String apo_path = anoReader.getApo_Path();

                                NeuronTree nt2 = NeuronTree.readSWC_file(swc_path);
                                ano_apo = apoReader_1.read(apo_path);

                                myrenderer.importNeuronTree(nt2);
                                myrenderer.importApo(ano_apo);
                                myrenderer.saveUndo();
                                break;

                            default:
                                ToastEasy("Unsupported file type");
                        }
                    }

                    else {
                        System.out.println("-------- open --------");
                        myrenderer.setSWCPath(filePath);
                        ifLoadLocal = false;
                        setButtonsImport();
                    }
                    ifImport = false;
                }


                if (ifAnalyze) {
                    MorphologyCalculate morphologyCalculate = new MorphologyCalculate();
                    List features = morphologyCalculate.calculate(uri, false);

                    if (features != null) {
                        fl = new ArrayList<double[]>(features);
                        displayResult(features);
                    }
                }


                if (ifLoadLocal) {
                    Log.e(TAG,"Load Local File");
                    myrenderer.setPath(filePath);

                    setButtonsLocal();
                    String filename = FileManager.getFileName(uri);
                    setFileName(filename);

                }


            } catch (OutOfMemoryError e) {
                ToastEasy("Fail to load file");
                Log.v("Exception", e.toString());
            } catch (CloneNotSupportedException e){
                Log.v("Exception:", e.toString());
            }
        }
    }






    private void BigFileRead_local(){
        String[] filename_list = bigImgReader.ChooseFile(this);
        if (filename_list != null){
            String [] str = new String[1];
            bigImgReader.ShowListDialog(this, filename_list);
        }
    }



    public void Block_navigate(String text){

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {

                if (isBigData_Local){
                    boolean ifNavigationLocation = myrenderer.getNav_location_Mode();
                    if (ifNavigationLocation){
                        Quit_Nav_Mode();
                    }

                    String filename = SettingFileManager.getFilename_Local(context);
                    int[] index = bigImgReader.SelectBlock_fast(text, context);
                    if (index == null){
                        System.out.println("----- index is null -----");
                        return;
                    }
                    try {
                        String filepath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + context.getResources().getString(R.string.app_name) + "/Server/" + filename + ".v3draw";
                        myrenderer.SetPath_Bigdata(filepath, index);
                        myGLSurfaceView.requestRender();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }


            }
        }, 0 * 1000); // 延时5秒


    }


    private void Quit_Nav_Mode(){
        System.out.println("---------QuitNavigationLocation---------");
        myrenderer.quitNav_location_Mode();
        navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
    }


    private void Update_Nav_Mode(){
        String filename = SettingFileManager.getFilename_Local(this);
        String offset   = SettingFileManager.getoffset_Local(this, filename);

        float size_x = Float.parseFloat(filename.split("RES")[1].split("x")[0]);
        float size_y = Float.parseFloat(filename.split("RES")[1].split("x")[1]);
        float size_z = Float.parseFloat(filename.split("RES")[1].split("x")[2]);

        float offset_x = Float.parseFloat(offset.split("_")[0]);
        float offset_y = Float.parseFloat(offset.split("_")[1]);
        float offset_z = Float.parseFloat(offset.split("_")[2]);
        float size_block = Float.parseFloat(offset.split("_")[3]);

        float[] neuron = {size_x, size_y, size_z};
        float[] block = {offset_x, offset_y, offset_z};
        float[] size = {size_block, size_block, size_block};

        myrenderer.setNav_location(neuron, block, size);
    }


    public void Set_Nav_Mode(){
        String filename = null;
        String offset   = null;
        float[] neuron = null; float[] block = null; float[] size = null;
        if (isBigData_Local){
            filename = SettingFileManager.getFilename_Local(this);
            offset   = SettingFileManager.getoffset_Local(this, filename);
        }

        if (filename == null || offset == null)
            return;

        float size_x, size_y, size_z;
        if (isBigData_Local){
            size_x = Float.parseFloat(filename.split("RES")[1].split("x")[0]);
            size_y = Float.parseFloat(filename.split("RES")[1].split("x")[1]);
            size_z = Float.parseFloat(filename.split("RES")[1].split("x")[2]);
        }else {
            size_x = Float.parseFloat(filename.split("RES")[1].split("x")[1]);
            size_y = Float.parseFloat(filename.split("RES")[1].split("x")[0].replace("(",""));
            size_z = Float.parseFloat(filename.split("RES")[1].split("x")[2].replace(")",""));
        }

        float offset_x = Float.parseFloat(offset.split("_")[0]);
        float offset_y = Float.parseFloat(offset.split("_")[1]);
        float offset_z = Float.parseFloat(offset.split("_")[2]);
        float size_block = Float.parseFloat(offset.split("_")[3]);

        neuron = new float[]{size_x, size_y, size_z};
        block  = new float[]{offset_x, offset_y, offset_z};
        size   = new float[]{size_block, size_block, size_block};


        boolean ifNavigationLocation = myrenderer.getNav_location_Mode();

        if (!ifNavigationLocation){
            System.out.println("--------!ifNavigationLocation---------");
            myrenderer.setNav_location_Mode();
            myrenderer.setNav_location(neuron, block, size);
            myGLSurfaceView.requestRender();
            navigation_location.setImageResource(R.drawable.ic_gps_off_black_24dp);
        }else {
            System.out.println("---------ifNavigationLocation---------");
            myrenderer.setNav_location_Mode();
            myGLSurfaceView.requestRender();
            navigation_location.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
        }
    }



    // OpenGL 中的显示区域
    class MyGLSurfaceView extends GLSurfaceView {
        private float X, Y;
        private double dis_start;
        private float dis_x_start;
        private float dis_y_start;
        private boolean isZooming;
        private boolean isZoomingNotStop;
        private float x1_start;
        private float x0_start;
        private float y1_start;
        private float y0_start;


        public MyGLSurfaceView(Context context) {
            super(context);

            ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo info = am.getDeviceConfigurationInfo();
            String v = info.getGlEsVersion(); //判断是否为3.0 ，一般4.4就开始支持3.0版本了。

            Log.e(TAG, "GLES-version: " + v);

            //设置一下opengl版本；
            setEGLContextClientVersion(3);

            setRenderer(myrenderer);

            //调用 onPause 的时候保存EGLContext
            setPreserveEGLContextOnPause(true);

            //当发生交互时重新执行渲染， 需要配合requestRender();
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        }


        //触摸屏幕的事件
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouchEvent(MotionEvent motionEvent) {

            try {

                //ACTION_DOWN不return true，就无触发后面的各个事件
                if (motionEvent != null) {
                    final float normalizedX = toOpenGLCoord(this, motionEvent.getX(), true);
                    final float normalizedY = toOpenGLCoord(this, motionEvent.getY(), false);
//
//                final float normalizedX =motionEvent.getX();
//                final float normalizedY =motionEvent.getY();

                    switch (motionEvent.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            X = normalizedX;
                            Y = normalizedY;
                            if (ifPainting || ifDeletingLine || ifSpliting || ifChangeLineType || ifDeletingMultiMarker) {
                                soundPool.play(soundId[0], actionVolume, actionVolume, 0, 0, 1.0f);
                                lineDrawed.add(X);
                                lineDrawed.add(Y);
                                lineDrawed.add(-1.0f);
                                myrenderer.setIfPainting(true);
                                requestRender();
                                Log.v("actionPointerDown", "Paintinggggggggggg");
                            }
                            if (ifPoint){
                                soundPool.play(soundId[1], actionVolume, actionVolume, 0, 0, 1.0f);
                            }
                            if (ifPainting){
                                soundPool.play(soundId[0], actionVolume, actionVolume, 0, 0, 1.0f);
                            }
//                        if (ifPoint) {
//                            Log.v("actionPointerDown", "Pointinggggggggggg");
//                            if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)
//                                myrenderer.add2DMarker(X, Y);
//                            else {
//                                myrenderer.setMarkerDrawed(X, Y);
//                            }
//                            Log.v("actionPointerDown", "(" + X + "," + Y + ")");
//                            requestRender();
//
//                        }
//                        if (ifDeletingMarker) {
//                            Log.v("actionPointerDown", "DeletingMarker");
//                            myrenderer.deleteMarkerDrawed(X, Y);
//                            requestRender();
//                        }
//                        if (ifDeletingLine){
//                            lineDrawed.add(X);
//                            lineDrawed.add(Y);
//                            lineDrawed.add(-1.0f);
//                            myrenderer.setIfPainting(true);
//                            requestRender();
//                        }
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            lineDrawed.clear();
                            myrenderer.setIfPainting(false);
                            requestRender();
                            isZooming = true;
                            isZoomingNotStop = true;
                            float x1 = toOpenGLCoord(this, motionEvent.getX(1), true);
                            float y1 = toOpenGLCoord(this, motionEvent.getY(1), false);

//                        float x1=motionEvent.getX(1);
//                        float y1=motionEvent.getY(1);
                            dis_start = computeDis(normalizedX, x1, normalizedY, y1);
                            dis_x_start = x1 - normalizedX;
                            dis_y_start = y1 - normalizedY;

                            x0_start = normalizedX;
                            y0_start = normalizedY;
                            x1_start = x1;
                            y1_start = y1;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (isZooming && isZoomingNotStop) {

                                float x2 = toOpenGLCoord(this, motionEvent.getX(1), true);
                                float y2 = toOpenGLCoord(this, motionEvent.getY(1), false);

//                            float x2=motionEvent.getX(1);
//                            float y2=motionEvent.getY(1);
                                double dis = computeDis(normalizedX, x2, normalizedY, y2);
                                double scale = dis / dis_start;
//                            if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker) {
                                myrenderer.zoom((float) scale);
//                            }
                                float dis_x = x2 - normalizedX;
                                float dis_y = y2 - normalizedY;
                                float ave_x = (x2 - x1_start + normalizedX - x0_start) / 2;
                                float ave_y = (y2 - y1_start + normalizedY - y0_start) / 2;
                                if (!(myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)) {
                                    if (myrenderer.getIfDownSampling() == false)
                                        myrenderer.setIfDownSampling(true);
                                }
//                            if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker){
//                                myrenderer.rotate2f(dis_x_start, dis_x, dis_y_start, dis_y);
//                            }else {
//                                myrenderer.rotate(dis_x - dis_x_start, dis_y - dis_y_start, (float) (computeDis(dis_x, dis_x_start, dis_y, dis_y_start)));
                                myrenderer.rotate(ave_x, ave_y, (float)(computeDis((x2 + normalizedX) / 2, (x1_start + x0_start) / 2, (y2 + normalizedY) / 2, (y1_start + y0_start) / 2)));
//                            }
                                //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                                requestRender();
                                dis_start = dis;
                                dis_x_start = dis_x;
                                dis_y_start = dis_y;
                                x0_start = normalizedX;
                                y0_start = normalizedY;
                                x1_start = x2;
                                y1_start = y2;
                            } else if (!isZooming){
                                if (!ifPainting && !ifDeletingLine && !ifSpliting && !ifChangeLineType && !ifPoint && !ifDeletingMarker && !ifChangeMarkerType && !ifDeletingMultiMarker) {
                                    if (!(myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)) {
                                        if (myrenderer.getIfDownSampling() == false)
                                            myrenderer.setIfDownSampling(true);
                                    }
                                    myrenderer.rotate(normalizedX - X, normalizedY - Y, (float) (computeDis(normalizedX, X, normalizedY, Y)));

                                    //配合GLSurfaceView.RENDERMODE_WHEN_DIRTY使用
                                    requestRender();
                                    X = normalizedX;
                                    Y = normalizedY;
                                } else {
                                    lineDrawed.add(normalizedX);
                                    lineDrawed.add(normalizedY);
                                    lineDrawed.add(-1.0f);

                                    myrenderer.setLineDrawed(lineDrawed);
                                    requestRender();

                                    invalidate();
                                }
                            }
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
//                        isZooming = false;
                            isZoomingNotStop = false;
                            myrenderer.setIfDownSampling(false);
                            X = normalizedX;
                            Y = normalizedY;
                            lineDrawed.clear();
                            myrenderer.setIfPainting(false);
//                        if (ifPainting){
//                            lineDrawed.clear();
//                            myrenderer.setLineDrawed(lineDrawed);
//                            requestRender();
//
//                            myrenderer.setIfPaiFting(false);
//                            requestRender();
//
//                        }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!isZooming) {
                                try {
                                    if (ifPoint) {

                                        Log.v("actionUp", "Pointinggggggggggg");
                                        if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)
                                            myrenderer.add2DMarker(normalizedX, normalizedY);
                                        else {
                                            myrenderer.setMarkerDrawed(normalizedX, normalizedY);
                                        }
                                        Log.v("actionPointerDown", "(" + X + "," + Y + ")");
                                        requestRender();

                                    }
                                    if (ifDeletingMarker) {
                                        Log.v("actionUp", "DeletingMarker");
                                        myrenderer.deleteMarkerDrawed(normalizedX, normalizedY);
                                        requestRender();
                                    }
                                    if (ifDeletingMultiMarker) {
                                        myrenderer.deleteMultiMarkerByStroke(lineDrawed);
                                        requestRender();
                                    }
                                    if (ifChangeMarkerType) {
                                        myrenderer.changeMarkerType(normalizedX, normalizedY);
                                        requestRender();
                                    }
                                    if (ifPainting) {
                                        Vector<Integer> segids = new Vector<>();
                                        myrenderer.setIfPainting(false);

                                        if (myrenderer.getFileType() == MyRenderer.FileType.JPG || myrenderer.getFileType() == MyRenderer.FileType.PNG)
                                            myrenderer.add2DCurve(lineDrawed);
                                        else {

                                            Callable<String> task = new Callable<String>() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public String call() throws Exception {
                                                    int lineType = myrenderer.getLastLineType();
                                                        V_NeuronSWC_list [] v_neuronSWC_list = new V_NeuronSWC_list[1];
                                                        V_NeuronSWC seg = myrenderer.addBackgroundLineDrawed(lineDrawed, v_neuronSWC_list);
                                                        System.out.println("feature");

                                                        if (seg != null) {
                                                            myrenderer.addLineDrawed2(lineDrawed, seg);
                                                            myrenderer.deleteFromCur(seg, v_neuronSWC_list[0]);
                                                        }
                                                    requestRender();
                                                    return "succeed";


                                                }
                                            };
                                            ExecutorService exeService = Executors.newSingleThreadExecutor();
                                            Future<String> future = exeService.submit(task);
                                            try {
                                                String result = future.get(1500, TimeUnit.MILLISECONDS);
                                                System.err.println("Result:" + result);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                System.out.println("unfinished in 1.5 seconds");
//                                            exeService.shutdown();
//                                            future.cancel(true);
                                            }
                                        }

                                        lineDrawed.clear();
                                        myrenderer.setLineDrawed(lineDrawed);

                                        requestRender();
                                    }

                                    if (ifDeletingLine) {
                                        myrenderer.setIfPainting(false);
                                        myrenderer.deleteLine1(lineDrawed);
                                        lineDrawed.clear();
                                        myrenderer.setLineDrawed(lineDrawed);
                                        requestRender();
                                    }
                                    if (ifSpliting) {
                                        myrenderer.setIfPainting(false);
                                        myrenderer.splitCurve(lineDrawed);
                                        lineDrawed.clear();
                                        myrenderer.setLineDrawed(lineDrawed);
                                        requestRender();
                                    }
                                    if (ifChangeLineType) {
                                        myrenderer.setIfPainting(false);
                                        int type = myrenderer.getLastLineType();
                                        myrenderer.changeLineType(lineDrawed, type);
                                        lineDrawed.clear();
                                        myrenderer.setLineDrawed(lineDrawed);
                                        requestRender();
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                lineDrawed.clear();
                                myrenderer.setIfPainting(false);
                            }
                            lineDrawed.clear();
                            myrenderer.setIfPainting(false);
                            requestRender();
                            isZooming = false;
                            myrenderer.setIfDownSampling(false);
                            break;
                        default:
                            break;
                    }
                    return true;
                }

            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }

            return false;
        }


        // 坐标系变换
        private float toOpenGLCoord(View view, float value, boolean isWidth) {
            if (isWidth) {
                return (value / (float) view.getWidth()) * 2 - 1;
            } else {
                return -((value / (float) view.getHeight()) * 2 - 1);
            }
        }


        // 距离计算
        private double computeDis(float x1, float x2, float y1, float y2) {
            return sqrt(pow((x2 - x1), 2) + pow((y2 - y1), 2));
        }
    }



    public static void setFileName(String name){
        filename = name;

        filenametext.setText(filename);
        ll_file.setVisibility(View.VISIBLE);

        lp_up_i.setMargins(0, 360, 0, 0);
        navigation_up.setLayoutParams(lp_up_i);

        lp_nacloc_i.setMargins(20, 400, 0, 0);
        navigation_location.setLayoutParams(lp_nacloc_i);

    }

    private static void setButtons(){
        puiHandler.sendEmptyMessage(2);
    }

    public static void setButtonsBigData(){
        if (isBigData_Local){
            navigation_left.setVisibility(View.VISIBLE);
            navigation_right.setVisibility(View.VISIBLE);
            navigation_up.setVisibility(View.VISIBLE);
            navigation_down.setVisibility(View.VISIBLE);
            navigation_front.setVisibility(View.VISIBLE);
            navigation_back.setVisibility(View.VISIBLE);

            Zoom_in_Big.setVisibility(View.VISIBLE);
            Zoom_out_Big.setVisibility(View.VISIBLE);
            Zoom_in.setVisibility(View.GONE);
            Zoom_out.setVisibility(View.GONE);
        }
    }

    public void setButtonsLocal(){
        if (isBigData_Local){
            isBigData_Local  = false;
            try {
                Zoom_in.setVisibility(View.VISIBLE);
                Zoom_out.setVisibility(View.VISIBLE);

                Zoom_in_Big.setVisibility(View.GONE);
                Zoom_out_Big.setVisibility(View.GONE);
                navigation_left.setVisibility(View.GONE);
                navigation_right.setVisibility(View.GONE);
                navigation_up.setVisibility(View.GONE);
                navigation_down.setVisibility(View.GONE);
                navigation_front.setVisibility(View.GONE);
                navigation_back.setVisibility(View.GONE);
                navigation_location.setVisibility(View.GONE);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void setButtonsImport(){
        if (isBigData_Local){
            isBigData_Local  = false;
            try {

                Zoom_in_Big.setVisibility(View.GONE);
                Zoom_out_Big.setVisibility(View.GONE);
                navigation_left.setVisibility(View.GONE);
                navigation_right.setVisibility(View.GONE);
                navigation_up.setVisibility(View.GONE);
                navigation_down.setVisibility(View.GONE);
                navigation_front.setVisibility(View.GONE);
                navigation_back.setVisibility(View.GONE);
                navigation_location.setVisibility(View.GONE);

                Zoom_in.setVisibility(View.VISIBLE);
                Zoom_out.setVisibility(View.VISIBLE);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void hideButtons(){
        if (!ifButtonShowed)
            return;

        ll_top.setVisibility(View.GONE);
        ll_bottom.setVisibility(View.GONE);

        animation_i.setVisibility(View.GONE);
        Rotation_i.setVisibility(View.GONE);
        Hide_i.setVisibility(View.GONE);
        Undo_i.setVisibility(View.GONE);
        Redo_i.setVisibility(View.GONE);

        if (isBigData_Local){
            navigation_back.setVisibility(View.GONE);
            navigation_down.setVisibility(View.GONE);
            navigation_front.setVisibility(View.GONE);
            navigation_left.setVisibility(View.GONE);
            navigation_right.setVisibility(View.GONE);
            navigation_up.setVisibility(View.GONE);
            navigation_location.setVisibility(View.GONE);

            Zoom_in_Big.setVisibility(View.GONE);
            Zoom_out_Big.setVisibility(View.GONE);
        }else {
            Zoom_in.setVisibility(View.GONE);
            Zoom_out.setVisibility(View.GONE);
        }


        ifButtonShowed = false;

    }


    private void showButtons(){
        if (ifButtonShowed)
            return;

        ll_top.setVisibility(View.VISIBLE);
        ll_bottom.setVisibility(View.VISIBLE);

        animation_i.setVisibility(View.VISIBLE);
        Rotation_i.setVisibility(View.VISIBLE);
        Hide_i.setVisibility(View.VISIBLE);
        Undo_i.setVisibility(View.VISIBLE);
        Redo_i.setVisibility(View.VISIBLE);

        if (isBigData_Local){
            navigation_back.setVisibility(View.VISIBLE);
            navigation_down.setVisibility(View.VISIBLE);
            navigation_front.setVisibility(View.VISIBLE);
            navigation_left.setVisibility(View.VISIBLE);
            navigation_right.setVisibility(View.VISIBLE);
            navigation_up.setVisibility(View.VISIBLE);
            navigation_location.setVisibility(View.VISIBLE);

            Zoom_in_Big.setVisibility(View.VISIBLE);
            Zoom_out_Big.setVisibility(View.VISIBLE);
        }else {
            Zoom_in.setVisibility(View.VISIBLE);
            Zoom_out.setVisibility(View.VISIBLE);
        }

        ifButtonShowed = true;
    }



    public static void Toast_in_Thread_static(String message){
        Message msg = new Message();
        msg.what = TOAST_INFO_STATIC;
        Bundle bundle = new Bundle();
        bundle.putString("Toast_msg",message);
        msg.setData(bundle);
        puiHandler.sendMessage(msg);
    }

    public static Context getContext() {
        return context;
    }






    /*
    functions for old version bigdata  ---------------------------------------------------------------------------------
     */
    public static void LoadBigFile_Local(String filepath_local){
        Log.e(TAG,"------" + filepath_local + "------");
        isBigData_Local = true;
        String filename = SettingFileManager.getFilename_Local(context);
        String offset = SettingFileManager.getoffset_Local(context, filename);

        int[] index = BigImgReader.getIndex(offset);
        myrenderer.SetPath_Bigdata(filepath_local, index);

        String offset_x = offset.split("_")[0];
        String offset_y = offset.split("_")[1];
        String offset_z = offset.split("_")[2];
        ToastEasy("Current offset: " + "x: " + offset_x + " y: " + offset_y + " z: " + offset_z);
        myGLSurfaceView.requestRender();

        setSelectSource("Local Server",context);
        setButtons();

    }




    /*
    functions for old version bigdata  ---------------------------------------------------------------------------------
     */


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
        File image = File.createTempFile(
                imageFileName,     /* prefix */
                ".jpg",     /* suffix */
                storageDir         /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private String getImageFilePath(){
        try {
            String mCaptureDir = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + context.getResources().getString(R.string.app_name) + "/cameraPhoto";
            File dir = new File(mCaptureDir);
            if (!dir.exists()){
                dir.mkdirs();
            }
            String mCapturePath = mCaptureDir + "/" + "Photo_" + System.currentTimeMillis() +".jpg";
            return mCapturePath;

        } catch (IOException e) {
            e.printStackTrace();
            return "Wrong file path";
        }

    }


}