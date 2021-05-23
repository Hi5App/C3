//package com.penglab.hi5.core;
//
//import android.annotation.SuppressLint;
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.pm.ConfigurationInfo;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.opengl.GLSurfaceView;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.widget.Toolbar;
//
//import com.penglab.hi5.R;
//import com.penglab.hi5.core.fileReader.imageReader.BigImgReader;
//import com.penglab.hi5.basic.NeuronSWC;
//import com.penglab.hi5.basic.NeuronTree;
//import com.penglab.hi5.dataStore.SettingFileManager;
//import com.penglab.hi5.serverCommunicator.Remote_Socket;
//import com.penglab.hi5.game.GameCharacter;
//import com.lxj.xpopup.XPopup;
//import com.lxj.xpopup.core.BasePopupView;
//import com.lxj.xpopup.interfaces.OnCancelListener;
//import com.lxj.xpopup.interfaces.OnConfirmListener;
//import com.lxj.xpopup.interfaces.OnSelectListener;
//import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
//import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_list;
//import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_unit;
//
//import org.apache.commons.io.FileUtils;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import static com.penglab.hi5.dataStore.SettingFileManager.getFilename_Remote;
//import static com.penglab.hi5.dataStore.SettingFileManager.getNeuronNumber_Remote;
//import static com.penglab.hi5.dataStore.SettingFileManager.getoffset_Remote;
//import static com.penglab.hi5.dataStore.SettingFileManager.setFilename_Remote;
//import static com.penglab.hi5.dataStore.SettingFileManager.setoffset_Remote;
//
//public class GameActivity extends BaseActivity {
//
//    public static final String NAME = "com.example.core.GameActivity";
//
//    private static final int SHOW_PROGRESSBAR = 1;
//    private static final int HIDE_PROGRESSBAR = 2;
//    private static MyRenderer myrenderer;
//
//    private static MyGLSurfaceView myGLSurfaceView;
//
//    private String filepath;
//    private static BasePopupView progressBar;
//
//    private BasePopupView archiveListPopup;
//
//    //    private float[] position;
////    private float[] dir;
////    private float[] head;
//    private static GameCharacter gameCharacter;
//
//    private float [] moveDir;
//    private float [] viewRotateDir;
//
//    private static Context gameContext;
//
//    private Timer timer;
//    private TimerTask task;
//
//    private V_NeuronSWC travelPath;
//    private V_NeuronSWC_list curSWCList = new V_NeuronSWC_list();
//    private int curSWC = 0;
//
//    private float [] lastPlace;
//    private int lastIndex;
//    private int curN;
//
//    private Remote_Socket remoteSocket;
//
//    final private static String TAG = "GAMEACTIVITY";
//
//    private static String scoreString = "00000";
//
//    private static int score = 0;
//
//    private static TextView scoreText;
//
//    private static ImageButton flagButton;
//    private static ImageButton loadButton;
//
//    private BufferedWriter swcWriter;
//
//    private String filenameRES;
//    private String filename_root;
//    private String offset_str;
//    private int [] offset = new int[3];
//    private int size;
//
//    private File curSWCFile;
//
//    @SuppressLint("HandlerLeak")
//    private static Handler puiHandler = new Handler(){
//        // 覆写这个方法，接收并处理消息。
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case SHOW_PROGRESSBAR:
//                    Log.v("HandleMessage", "progressBar.show()");
//                    progressBar.show();
//                    break;
//
//                case HIDE_PROGRESSBAR:
//                    Log.v("HandleMessage", "progressbar.dismiss()");
//                    progressBar.dismiss();
//                    break;
//
//                default:
//                    Toast.makeText(gameContext,"Something Wrong in puiHandler !",Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Bundle extras = getIntent().getExtras();
//        filepath = extras.getString("FilePath");
//        float [] position = extras.getFloatArray("Position");
//        float [] dir = extras.getFloatArray("Dir");
////        float [] head = MyRenderer.locateHead(dir[0], dir[1], dir[2]);
//        float [] head = extras.getFloatArray("Head");
//        int lastIndexFromIntent = extras.getInt("LastIndex");
//        Boolean ifNewGame = extras.getBoolean("IfNewGame");
//        score = extras.getInt("Score");
//
//        progressBar = new XPopup.Builder(this).asLoading("Downloading...");
//
//        gameContext = this;
//
//        setContentView(R.layout.activity_game);
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        myrenderer = new MyRenderer(this);
//        myrenderer.setPath(filepath);
//        myrenderer.resetContrast(preferenceSetting.getContrast());
////        myrenderer.setGamePosition(position);
////        myrenderer.setGameDir(dir);
////        myrenderer.setGameHead(head);
//
//        gameCharacter = new GameCharacter(position, dir, head);
//        myrenderer.setGameCharacter(gameCharacter);
//
//        remoteSocket = new Remote_Socket(this);
//
////        myrenderer.addMarker(position);
//        myrenderer.setIfGame(true);
//
//        myGLSurfaceView = new GameActivity.MyGLSurfaceView(this);
//        FrameLayout ll = (FrameLayout) findViewById(R.id.gameContainer);
//        ll.addView(myGLSurfaceView);
//
//        MyRockerView rockerView1 = (MyRockerView)findViewById(R.id.rockerView1);
//
//        FrameLayout.LayoutParams lp_rocker1 = new FrameLayout.LayoutParams(300, 300);
//        lp_rocker1.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//        lp_rocker1.setMargins(0, 0, 20, 20);
//        ll.removeView(rockerView1);
//        this.addContentView(rockerView1, lp_rocker1);
//
//        MyRockerView rockerView2 = (MyRockerView)findViewById(R.id.rockerView2);
//
//        FrameLayout.LayoutParams lp_rocker2 = new FrameLayout.LayoutParams(300, 300);
//        lp_rocker2.gravity = Gravity.BOTTOM | Gravity.LEFT;
//        lp_rocker2.setMargins(20, 0, 0, 20);
//        ll.removeView(rockerView2);
//        this.addContentView(rockerView2, lp_rocker2);
//
//        scoreText = new TextView(this);
//        scoreText.setTextColor(Color.YELLOW);
//        scoreText.setText(scoreString);
//        scoreText.setTypeface(Typeface.DEFAULT_BOLD);
//        scoreText.setLetterSpacing(0.8f);
//        scoreText.setTextSize(15);
//
//        FrameLayout.LayoutParams lp_score = new FrameLayout.LayoutParams(350, 300);
//        lp_score.gravity = Gravity.TOP | Gravity.RIGHT;
//        lp_score.setMargins(0, 160, 20, 0);
//        this.addContentView(scoreText, lp_score);
//
//        flagButton = new ImageButton(this);
//        flagButton.setImageResource(R.drawable.ic_flag);
//        flagButton.setBackgroundResource(R.drawable.circle_normal);
//
//        flagButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (saveFlag()) {
//                    Toast_in_Thread("Save Successfully");
//                } else {
//                    Toast_in_Thread("Save Failed");
//                }
//
//            }
//        });
//
//        FrameLayout.LayoutParams lp_flag = new FrameLayout.LayoutParams(100, 100);
//        lp_flag.gravity = Gravity.TOP | Gravity.RIGHT;
//        lp_flag.setMargins(0, 240, 40, 0);
//        this.addContentView(flagButton, lp_flag);
//
//        loadButton = new ImageButton(this);
//        loadButton.setImageResource(R.drawable.ic_undo_black_24dp);
//        loadButton.setBackgroundResource(R.drawable.circle_normal);
//
//        loadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadFlag();
//            }
//        });
//
//        FrameLayout.LayoutParams lp_load = new FrameLayout.LayoutParams(100, 100);
//        lp_load.gravity = Gravity.TOP | Gravity.RIGHT;
//        lp_load.setMargins(0, 360, 40, 0);
//        this.addContentView(loadButton, lp_load);
//
//
//        moveDir = new float[]{0f, 0f, 0f};
//        viewRotateDir = new float[]{0f, 0f, 0f};
//
//
//
//        filenameRES = SettingFileManager.getFilename_Remote(context);
//
////        Log.d("Filename_Root", filename_root);
//        offset_str = SettingFileManager.getoffset_Remote(context, filenameRES);
//        filename_root = filenameRES.split("/")[0];
////        Log.d("Offset_Str", offset_str);
//        offset[0] = Integer.parseInt(offset_str.split("_")[0]);
//        offset[1] = Integer.parseInt(offset_str.split("_")[1]);
//        offset[2] = Integer.parseInt(offset_str.split("_")[2]);
//        size = Integer.parseInt(offset_str.split("_")[3]);
//
//        if (ifNewGame) {
//            initFlags();
//            initSWCWriter();
//
////        travelPath = new V_NeuronSWC();
//            V_NeuronSWC_unit startPoint = new V_NeuronSWC_unit();
//            startPoint.n = 0;
//            startPoint.parent = -1;
//            float[] startPlace = myrenderer.modeltoVolume(position);
//            startPoint.x = startPlace[0];
//            startPoint.y = startPlace[1];
//            startPoint.z = startPlace[2];
//            startPoint.type = 2;
//            myrenderer.addSwc(new V_NeuronSWC());
////        travelPath.append(startPoint);
//            myrenderer.appendCurSWC(0, startPoint);
//
//            lastPlace = new float[]{position[0], position[1], position[2]};
//            lastIndex = 0;
//
//            clearScore();
//
//
//            Log.d("swcWriter", "Write first point");
//            try {
//                Log.d("swcWriter", "try write");
//
////            swcWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(curSWCFile, true)));
//
//                if (curSWCFile.canWrite()) {
//                    Log.e("swcWriter", "curSWCFile.canWrite()");
//                }
//
//                if (curSWCFile != null) {
//                    Log.e("swcWriter", "curSWCFile != null");
//
//                }
//
//                swcWriter.append(Long.toString((int) startPoint.n)).append(" ").append(Integer.toString((int) startPoint.type))
//                        .append(" ").append(String.format("%.3f", (startPoint.x + offset[0] - size / 2))).append(" ").append(String.format("%.3f", (startPoint.y + offset[1] - size / 2)))
//                        .append(" ").append(String.format("%.3f", (startPoint.z + offset[2] - size / 2))).append(" ").append(String.format("%.3f", 0.0f))
//                        .append(" ").append(Long.toString((int) startPoint.parent)).append("\n");
//                swcWriter.flush();
////            swcWriter.close();
//
//                Log.e("swcWriter", "swcWriter.flush() successfully !");
//
//            } catch (IOException e) {
//                Log.d("swcWriter", "Exception occurred");
//                e.printStackTrace();
//            }
//        } else {
//            updateScore();
//
//            String externalFileDir = context.getExternalFilesDir(null).toString();
//
//            try {
//                File swcFile = new File(externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
//
//                swcWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(swcFile, true)));
//                if (swcFile.exists()) {
//                    Log.d("LoadFlag", "SwcFile exits");
//                    Log.d("LoadFlag", externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
//                    NeuronTree nt = NeuronTree.readSWC_file(externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
////                                    myrenderer.importNeuronTree(nt);
//
//
//                    V_NeuronSWC seg = new V_NeuronSWC();
//                    for (int i = 0; i < nt.listNeuron.size(); i++) {
//                        NeuronSWC unit = nt.listNeuron.get(i);
////                        if (unit.x > offset[0] + size / 2 || unit.x < offset[0] - size / 2
////                                || unit.y > offset[1] + size / 2 || unit.y < offset[1] - size / 2
////                                || unit.z > offset[2] + size / 2 || unit.z < offset[2] - size / 2) {
////
////                        } else {
//                            V_NeuronSWC_unit newUnit = new V_NeuronSWC_unit();
//                            newUnit.n = unit.n;
//                            newUnit.x = unit.x - offset[0] + size / 2;
//                            newUnit.y = unit.y - offset[1] + size / 2;
//                            newUnit.z = unit.z - offset[2] + size / 2;
//                            newUnit.parent = unit.parent;
//                            newUnit.type = unit.type;
//
//                            seg.append(newUnit);
////                        }
//                    }
//
//                    if (seg.nrows() > 0) {
//                        myrenderer.addSwc(seg);
//                    }
//
////                    Log.d("LoadFlag", "CurSWCList: " + myrenderer.getCurSwcList().nsegs());
////                    Log.d("LoadFlag", "CurSWCList seg0 size: " + myrenderer.getCurSwcList().seg.get(0).nrows());
////                    Log.d("LoadFlag", "CurSWCList first point: " + myrenderer.getCurSwcList().seg.get(0).row.get(0).x
////                            + " " + myrenderer.getCurSwcList().seg.get(0).row.get(0).y + " " + myrenderer.getCurSwcList().seg.get(0).row.get(0).z);
//
//
//
//                }
//
//                File flagFile = new File(externalFileDir + "/Game/Flags/" + filename_root + ".txt");
//                BufferedReader flagReader = new BufferedReader(new InputStreamReader(new FileInputStream(flagFile)));
//                ArrayList<String> arrayList = new ArrayList<>();
//                try {
//                    String str;
//                    while ((str = flagReader.readLine()) != null){
//                        arrayList.add(str);
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                for (int i = 0; i < arrayList.size(); i++){
//                    String str = arrayList.get(i);
//                    String offset_str = str.split("#")[1];
//                    String pos_str = str.split("#")[2];
//                    float [] offsetFlag = new float[3];
//                    offsetFlag[0] = Float.parseFloat(offset_str.split("_")[0]);
//                    offsetFlag[1] = Float.parseFloat(offset_str.split("_")[1]);
//                    offsetFlag[2] = Float.parseFloat(offset_str.split("_")[2]);
//                    float size = Float.parseFloat(offset_str.split("_")[3]);
//                    float [] posFlag = new float[3];
//                    posFlag[0] = Float.parseFloat(pos_str.split(" ")[0]);
//                    posFlag[1] = Float.parseFloat(pos_str.split(" ")[1]);
//                    posFlag[2] = Float.parseFloat(pos_str.split(" ")[2]);
//                    float [] curFlag = new float[3];
//                    curFlag[0] = (posFlag[0] * size + offsetFlag[0] - offset[0]) / size;
//                    curFlag[1] = (posFlag[1] * size + offsetFlag[1] - offset[1]) / size;
//                    curFlag[2] = (posFlag[2] * size + offsetFlag[2] - offset[2]) / size;
//                    myrenderer.appendGameFlags(curFlag);
//                }
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//
//            lastIndex = lastIndexFromIntent;
//        }
//
//        rockerView1.setRockerChangeListener(new MyRockerView.RockerChangeListener() {
//            @Override
//            public void report(float x, float y) {
//
//                moveDir[0] = x;
//                moveDir[1] = y;
//
//            }
//        });
//
//        rockerView2.setRockerChangeListener(new MyRockerView.RockerChangeListener() {
//            @Override
//            public void report(float x, float y) {
//
//                viewRotateDir[0] = x;
//                viewRotateDir[1] = y;
//            }
//        });
//
////        final boolean[] b = {false};
////
//        timer = new Timer();
//        task = new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    float angleH = viewRotateDir[0] / 100;
//                    float angleV = viewRotateDir[1] / 100;
//                    float x = moveDir[0] / 10000;
//                    float y = moveDir[1] / 10000;
//
//                    if (angleH != 0 || angleV != 0 || x != 0 || y != 0) {
//                        gameCharacter.rotateDir(angleH, angleV);
//
//
//                        if (x != 0 && y != 0) {
//                            gameCharacter.movePosition(x, y);
//
//                            if (gameCharacter.closeToBoundary()){
//                                remoteSocket.disConnectFromHost();
////                                remoteSocket.connectServer(ip_SEU);
//                                remoteSocket.connectServer(ip_TencentCloud);
//
////                                Thread.sleep(8000);
//                                float [] volumnePosition = myrenderer.modeltoVolume(gameCharacter.getPosition());
////                                float [] dis = new float[]{volumnePosition[0] - 64, volumnePosition[1] - 64, volumnePosition[2] - 64};
//                                float [] dis = new float[]{gameCharacter.getPosition()[0] - 0.5f, gameCharacter.getPosition()[1] - 0.5f, gameCharacter.getPosition()[2] - 0.5f};
//                                Log.v("DISSSSS", Arrays.toString(dis));
//                                float [] normalDir = new float[3];
//                                normalDir[0] = dis[0] / (float)Math.sqrt((dis[0] * dis[0] + dis[1] * dis[1] + dis[2] * dis[2]));
//                                normalDir[1] = dis[1] / (float)Math.sqrt((dis[0] * dis[0] + dis[1] * dis[1] + dis[2] * dis[2]));
//                                normalDir[2] = dis[2] / (float)Math.sqrt((dis[0] * dis[0] + dis[1] * dis[1] + dis[2] * dis[2]));
//                                float [] negativeDir = {-normalDir[0], -normalDir[1], -normalDir[2]};
//
//                                curSWCList = myrenderer.getCurSwcList().clone();
//                                Log.d(TAG, "curSWCList.nsegs(): " + Integer.toString(curSWCList.nsegs()));
//
//                                remoteSocket.PullImageBlock_Dir(context, normalDir);
////                                gameCharacter.setPosition(new float[]{0.5f, 0.5f, 0.5f});
//                                gameCharacter.move(negativeDir, 0.5f);
//
//                                float [] volumneDir = new float[]{64 - volumnePosition[0], 64 - volumnePosition[1], 64 - volumnePosition[2]};
//                                float [] volumneNormalDir = new float[3];
//                                volumneNormalDir[0] = volumneDir[0] / (float)Math.sqrt(volumneDir[0] * volumneDir[0] + volumneDir[1] * volumneDir[1] + volumneDir[2] * volumneDir[2]);
//                                volumneNormalDir[1] = volumneDir[1] / (float)Math.sqrt(volumneDir[0] * volumneDir[0] + volumneDir[1] * volumneDir[1] + volumneDir[2] * volumneDir[2]);
//                                volumneNormalDir[2] = volumneDir[2] / (float)Math.sqrt(volumneDir[0] * volumneDir[0] + volumneDir[1] * volumneDir[1] + volumneDir[2] * volumneDir[2]);
////                                travelPath.move(volumneNormalDir, 64);
//                                myrenderer.setCurSwcList(curSWCList);
//                                myrenderer.moveAllSWC(volumneNormalDir, 64);
//                                myrenderer.moveGameFlags(negativeDir, 0.5f);
//
//
////                                myrenderer.clearCurSwcList();
////                                myrenderer.addSwc(travelPath);
////                                myrenderer.clearCurSwcList();
////                                myrenderer.addSwc(travelPath);
//
////                                myrenderer.clearMarkerList();
////                                myrenderer.addMarker(new float[]{0.5f, 0.5f, 0.5f});
//
//                                lastPlace = new float[]{0.5f, 0.5f, 0.5f};
//
//                                myrenderer.setGameCharacter(gameCharacter);
//                            } else {
//
//                                float[] position = gameCharacter.getPosition();
//
////                                myrenderer.clearMarkerList();
////                                myrenderer.addMarker(position);
//
////                                if (((position[0] - lastPlace[0]) * (position[0] - lastPlace[0])
////                                        + (position[1] - lastPlace[1]) * (position[1] - lastPlace[1])
////                                        + (position[2] - lastPlace[2]) * (position[2] - lastPlace[2])) > 0.001) {
//
//                                V_NeuronSWC_unit newPoint = new V_NeuronSWC_unit();
////                                newPoint.parent = travelPath.nrows() - 1;
//                                newPoint.parent = lastIndex;
//                                newPoint.n = myrenderer.firstSwcLength();
//                                newPoint.type = 2;
//                                float[] newPlace = myrenderer.modeltoVolume(position);
//                                newPoint.x = newPlace[0];
//                                newPoint.y = newPlace[1];
//                                newPoint.z = newPlace[2];
////                                travelPath.append(newPoint);
//                                myrenderer.appendCurSWC(0, newPoint);
//
////                                myrenderer.clearCurSwcList();
////                                myrenderer.addSwc(travelPath);
//
//                                lastPlace = new float[]{position[0], position[1], position[2]};
//                                lastIndex = (int)newPoint.n;
//                                curN++;
//                                Log.d("LastIndex", Integer.toString(lastIndex));
//
////                                swcWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(curSWCFile, true)));
//                                swcWriter.append(Long.toString((int)newPoint.n)).append(" ").append(Integer.toString((int)newPoint.type))
//                                        .append(" ").append(String.format("%.3f", (newPoint.x + offset[0] - size / 2))).append(" ").append(String.format("%.3f", (newPoint.y + offset[1] - size / 2)))
//                                        .append(" ").append(String.format("%.3f", (newPoint.z + offset[2] - size / 2))).append(" ").append(String.format("%.3f", 0.0f ))
//                                        .append(" ").append(Long.toString((int)newPoint.parent)).append("\n");
//                                swcWriter.flush();
////                                swcWriter.close();
//
//                                myrenderer.setGameCharacter(gameCharacter);
//                                myrenderer.removeWhileMove();
////                                }
//                            }
//                        }
//
////                        myrenderer.moveBlock(x, 0, -y);
//
////                        myrenderer.setGameDir(gameCharacter.getDir());
////                        myrenderer.setGameHead(gameCharacter.getHead());
////                        myrenderer.setGamePosition(gameCharacter.getPosition());
//
//
//                        myGLSurfaceView.requestRender();
//                    }
//
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        };
//        timer.schedule(task, 0, 100);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        myGLSurfaceView.onPause();
//        Log.v("onPause", "start-----");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        myGLSurfaceView.onResume();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.game_menu, menu);
//        return true;
//    }
//
//    /**
//     * call the corresponding function when button in top bar clicked
//     * @param item
//     * @return
//     */
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.back:
//                clearScore();
//                timer.cancel();
//                task.cancel();
//                MainActivity.setIfGame(false);
//                finish();
//                return true;
//
//            case R.id.save_game:
//                archiveList(true);
////                if (saveGame(0))
////                    Toast_in_Thread("Saved Successfully");
////                else
////                    Toast_in_Thread("Failed To Save!!!");
//                return true;
//
//            case R.id.load_game:
//                archiveList(false);
//                return true;
//
//            default:
//                return true;
//        }
//    }
//
//    public static void showProgressBar(){
//        Log.v(TAG, "puiHandler.sendEmptyMessage(SHOW_PROGRESSBAR)");
//        puiHandler.sendEmptyMessage(SHOW_PROGRESSBAR);
//
////        try {
////            Thread.sleep(1000);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//
//    }
//
//    public static void hideProgressBar(){
//        puiHandler.sendEmptyMessage(HIDE_PROGRESSBAR);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public static void LoadBigFile_Remote(String filepath){
//
////        clearScore();
//        myrenderer.setGamePath(filepath);
////        myGLSurfaceView.requestRender();
////        myGLSurfaceView.requestRender();
//        myrenderer.setGameCharacter(gameCharacter);
//        Log.d("LoadBigFile_Remote", "Pos: " + Arrays.toString(gameCharacter.getPosition()));
//        Log.d("LoadBigFile_Remote", "Dir: " + Arrays.toString(gameCharacter.getDir()));
////        myGLSurfaceView.requestRender();
////        clearScore();
////        myrenderer.removeWhileLoad();
//
//
//    }
//
//    public static void addScore(int s){
//        score += s;
//
//        updateScore();
//    }
//
//    public static void clearScore(){
//        score = 0;
//
//        updateScore();
//    }
//
//    private static void updateScore(){
//        if (score < 10){
//            scoreString = "0000" + Integer.toString(score);
//        } else if (score >= 10 && score < 100){
//            scoreString = "000" + Integer.toString(score);
//        } else if (score >= 100 && score < 1000){
//            scoreString = "00" + Integer.toString(score);
//        } else if (score >= 1000 && score < 10000){
//            scoreString = "0" + Integer.toString(score);
//        } else {
//            scoreString = Integer.toString(score);
//        }
//        Log.d("UpdateScore", Integer.toString(score) + "   " + scoreString);
//        scoreText.setText(scoreString);
//    }
//
//    private void archiveList(boolean saving){
//        String externalFileDir = context.getExternalFilesDir(null).toString();
//        String [] fileList = {"[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]", "[Empty Archive]"};
//        File file = new File(externalFileDir + "/Game/Archives");
//        if (file.exists()){
//            try {
//                for (int i = 0; i < 10; i++) {
//                    File tempFile = new File(externalFileDir + "/Game/Archives/Archive_" + i);
//                    if (!tempFile.exists()) {
//                        tempFile.mkdir();
//                    } else {
//                        File [] archiveFiles = tempFile.listFiles();
//                        if (archiveFiles.length > 0){
//                            fileList[i] = archiveFiles[0].getName().split(".txt")[0];
//                        }
//                    }
//                }
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//
////            File[] tempList = file.listFiles();
//
////            for (int i = 0; i < tempList.length; i++){
////                if (tempList[i].isDirectory()){
////                    if (Pattern.matches("Archive_[0-9]", tempList[i].getName())){
////                        File [] archiveFile = tempList[i].listFiles();
////                        if (archiveFile.length != 0){
////                            fileList[i] = archiveFile[0].getName();
////                        }
////                    }
////                }
////            }
//        } else {
//            File parent = file.getParentFile();
//            if (!parent.exists()){
//                parent.mkdir();
//            }
//            file.mkdir();
//            for (int i = 0; i < 10; i++){
//                File tempFile = new File(externalFileDir + "/Game/Archives/Archive_" + i);
//                tempFile.mkdir();
//            }
//        }
//
//        archiveListPopup = new XPopup.Builder(this)
//                .autoDismiss(false)
//                .asCenterList("Archives", fileList,
//                        new OnSelectListener() {
//                            @Override
//                            public void onSelect(int position, String text) {
//                                if (text.equals("[Empty Archive]")){
//                                    if (saving){
//                                        if (saveGame(position))
//                                            Toast_in_Thread("Saved Successfully");
//                                        else
//                                            Toast_in_Thread("Failed To Save!!!");
//                                    }
//                                } else {
//                                    if (saving){
//                                        new XPopup.Builder(gameContext)
//                                                .dismissOnTouchOutside(false)
//                                                .asConfirm("Archive", "Are you sure to overwrite this archive?", "Cancel", "Confirm",
//                                                        new OnConfirmListener() {
//                                                            @Override
//                                                            public void onConfirm() {
//                                                                if (saveGame(position))
//                                                                    Toast_in_Thread("Saved Successfully");
//                                                                else
//                                                                    Toast_in_Thread("Failed To Save!!!");
//                                                            }
//                                                        },
//                                                        new OnCancelListener() {
//                                                            @Override
//                                                            public void onCancel() {
////                                                                archiveList(true);
//                                                            }
//                                                        },false).show();
//                                    } else {
//                                        if (loadGame(position))
//                                            Toast_in_Thread("Loaded successfully");
//                                        else
//                                            Toast_in_Thread("Failed To Load!!!");
//                                    }
//                                }
//                            }
//                        });
//        archiveListPopup.show();
//    }
//
//    public boolean saveGame(int num){
//        archiveListPopup.dismiss();
//        String offset_str = SettingFileManager.getoffset_Remote(context, filenameRES);
//
//        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
//        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
//        Date date = new Date();// 获取当前时间
//        String date_str = sdf.format(date);
//
//        float x_pos = gameCharacter.getPosition()[0];
//        float y_pos = gameCharacter.getPosition()[1];
//        float z_pos = gameCharacter.getPosition()[2];
//
//        float x_dir = gameCharacter.getDir()[0];
//        float y_dir = gameCharacter.getDir()[1];
//        float z_dir = gameCharacter.getDir()[2];
//
//        float x_head = gameCharacter.getHead()[0];
//        float y_head = gameCharacter.getHead()[1];
//        float z_head = gameCharacter.getHead()[2];
//
//        String pos_str = Float.toString(x_pos) + ' ' + Float.toString(y_pos) + ' ' + Float.toString(z_pos);
//        String dir_str = Float.toString(x_dir) + ' ' + Float.toString(y_dir) + ' ' + Float.toString(z_dir);
//        String head_str = Float.toString(x_head) + ' ' + Float.toString(y_head) + ' ' + Float.toString(z_head);
//        String last_str = Integer.toString(lastIndex);
//        String score_str = Integer.toString(score);
////        String curn_str = Integer.toString(curN);
////        String curSWC_str = Integer.toString(curSWC);
//
//        String externalFileDir = context.getExternalFilesDir(null).toString();
//        String str = filenameRES + '\n' + offset_str + '\n' + pos_str + '\n' + dir_str + '\n' + head_str + '\n' + last_str + '\n' + score_str + '\n';
////        File file = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + date_str + ".txt");
//        File file = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num);
//        if (!file.exists()){
//            try {
////                File dir = new File(file.getParent());
////                dir.mkdirs();
////                file.createNewFile();
//                file.mkdir();
//
////                String str = filename_root + '\n' + offset + '\n' + pos_str + '\n' + dir_str;
//
//            }catch (Exception e){
//                Log.v(TAG, "failed to create archive dir");
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        File [] oldFiles = file.listFiles();
//        for (int i = 0; i < oldFiles.length; i++){
//            oldFiles[i].delete();
//        }
//
//        File archiveFile = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + date_str + ".txt");
//
//        try {
//            archiveFile.createNewFile();
//        } catch (Exception e) {
//            Log.v(TAG, "failed to create archive file");
//            e.printStackTrace();
//            return false;
//        }
//
//        try {
//            FileOutputStream outStream = new FileOutputStream(archiveFile);
//            outStream.write(str.getBytes());
//            outStream.close();
//
//            File swcFile = new File(externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
//            if (swcFile.exists()){
////                InputStream swcInput = new FileInputStream(swcFile);
//                File archiveSWCFile = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + filename_root + ".swc");
//                if (archiveSWCFile.exists()) {
//                    archiveSWCFile.delete();
//                }
//                archiveSWCFile.createNewFile();
////                    OutputStream swcOutput = new FileOutputStream(archiveSWCFile);
//                FileUtils.copyFile(swcFile, archiveSWCFile);
//
//            }
//
//            File flagFile = new File(externalFileDir + "/Game/Flags/" + filename_root + ".txt");
//            if (flagFile.exists()){
//                File archiveFlagFile = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + filename_root + ".txt");
//                if (archiveFlagFile.exists()) {
//                    archiveFlagFile.delete();
//                }
//                archiveFlagFile.createNewFile();
//                FileUtils.copyFile(flagFile, archiveFlagFile);
//            }
//
//
//        } catch (Exception e){
//            Log.v(TAG, "failed to write archive");
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    private boolean loadGame(int num){
//        archiveListPopup.dismiss();
//        String archiveImageName;
//        String archiveOffset;
//        float [] pos = new float[3];
//        float [] dir = new float[3];
//        float [] head = new float[3];
//        String externalFileDir = context.getExternalFilesDir(null).toString();
//        File file = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num);
//        if (!file.exists()){
//            file.mkdir();
//            return false;
//        }
//
//        File [] tempList = file.listFiles();
//        if(tempList.length == 0){
//            return false;
//        }
//
//
//        try{
//            FileInputStream inStream = new FileInputStream(tempList[0]);
//            if (inStream != null) {
//
//                InputStreamReader inputreader
//                        = new InputStreamReader(inStream, "UTF-8");
//                BufferedReader buffreader = new BufferedReader(inputreader);
//                String line = "";
//
//                line = buffreader.readLine();
//                archiveImageName = line;
//                String tempFilename = archiveImageName.split("/")[0];
//
//                File archiveSWCFile = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + tempFilename + ".swc");
//                if (archiveSWCFile.exists()){
//                    File newSWCFile = new File(externalFileDir + "/Game/SWCs/" + tempFilename + ".swc");
//                    if (newSWCFile.exists()){
//                        newSWCFile.delete();
//                    }
//                    newSWCFile.createNewFile();
//
//                    FileUtils.copyFile(archiveSWCFile, newSWCFile);
//                } else {
//                    return false;
//                }
//
//                File archiveFlagFile = new File(externalFileDir + "/Game/Archives/" + "Archive_" + num + "/" + tempFilename + ".txt");
//                if (archiveFlagFile.exists()){
//                    File newFlagFile = new File(externalFileDir + "/Game/Flags/" + tempFilename + ".txt");
//                    if (newFlagFile.exists()){
//                        newFlagFile.delete();
//                    }
//                    newFlagFile.createNewFile();
//
//                    FileUtils.copyFile(archiveFlagFile, newFlagFile);
//                }
//
//                line = buffreader.readLine();
//                archiveOffset = line;
//
//
//                if (archiveImageName != null && archiveOffset != null){
//                    remoteSocket.disConnectFromHost();
////                    remoteSocket.connectServer(ip_SEU);
//                    remoteSocket.connectServer(ip_TencentCloud);
//                    remoteSocket.pullImageBlockWhenLoadGame(archiveImageName, archiveOffset);
//
//                    setFilename_Remote(archiveImageName, context);
////                    setNeuronNumber_Remote(neuronNum_Backup,fileName_Backup,mContext);
//                    setoffset_Remote(archiveOffset, archiveImageName, context);
//
//                    myrenderer.clearCurSwcList();
//                    myrenderer.clearGameFlags();
////                    travelPath.clear();
//                } else {
//                    return false;
//                }
//
//                filenameRES = archiveImageName;
//                filename_root = filenameRES.split("/")[0];
//
//                offset[0] = Integer.parseInt(archiveOffset.split("_")[0]);
//                offset[1] = Integer.parseInt(archiveOffset.split("_")[1]);
//                offset[2] = Integer.parseInt(archiveOffset.split("_")[2]);
//                size = Integer.parseInt(archiveOffset.split("_")[3]);
//
//                line = buffreader.readLine();
//                pos[0] = Float.parseFloat(line.split(" ")[0]);
//                pos[1] = Float.parseFloat(line.split(" ")[1]);
//                pos[2] = Float.parseFloat(line.split(" ")[2]);
//
//                line = buffreader.readLine();
//                dir[0] = Float.parseFloat(line.split(" ")[0]);
//                dir[1] = Float.parseFloat(line.split(" ")[1]);
//                dir[2] = Float.parseFloat(line.split(" ")[2]);
//
//                line = buffreader.readLine();
//                head[0] = Float.parseFloat(line.split(" ")[0]);
//                head[1] = Float.parseFloat(line.split(" ")[1]);
//                head[2] = Float.parseFloat(line.split(" ")[2]);
//
//                line = buffreader.readLine();
//                lastIndex = Integer.parseInt(line);
//
//                line = buffreader.readLine();
//                score = Integer.parseInt(line);
//
//                updateScore();
//
////                line = buffreader.readLine();
////                curN = Integer.parseInt(line);
//
////                line = buffreader.readLine();
////                curSWC = Integer.parseInt(line);
//
//                inStream.close();//关闭输入流
//                inputreader.close();
//                buffreader.close();
//
//                gameCharacter = new GameCharacter(pos, dir, head);
//
//                File swcFile = new File(externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
//                swcWriter.close();
//                swcWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(swcFile, true)));
//                if (swcFile.exists()){
//                    Log.d("LoadFlag", "SwcFile exits");
//                    Log.d("LoadFlag", externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
//                    NeuronTree nt = NeuronTree.readSWC_file(externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
////                                    myrenderer.importNeuronTree(nt);
//                    try {
//
//                        V_NeuronSWC seg = new V_NeuronSWC();
//                        for (int i = 0; i < nt.listNeuron.size(); i++){
//                            NeuronSWC unit = nt.listNeuron.get(i);
////                            if (unit.x > offset[0] + size / 2 || unit.x < offset[0] - size / 2
////                                    || unit.y > offset[1] + size / 2 || unit.y < offset[1] - size / 2
////                                    || unit.z > offset[2] + size / 2 || unit.z < offset[2] - size / 2){
////
////                            } else {
//                                V_NeuronSWC_unit newUnit = new V_NeuronSWC_unit();
//                                newUnit.n = unit.n;
//                                newUnit.x = unit.x - offset[0] + size / 2;
//                                newUnit.y = unit.y - offset[1] + size / 2;
//                                newUnit.z = unit.z - offset[2] + size / 2;
//                                newUnit.parent = unit.parent;
//                                newUnit.type = unit.type;
//
//                                seg.append(newUnit);
////                            }
//                        }
//
//                        if (seg.nrows() > 0){
//                            myrenderer.addSwc(seg);
//                        }
//
//                        Log.d("LoadFlag", "CurSWCList: " + myrenderer.getCurSwcList().nsegs());
//                        Log.d("LoadFlag", "CurSWCList seg0 size: " + myrenderer.getCurSwcList().seg.get(0).nrows());
//                        Log.d("LoadFlag", "CurSWCList first point: " + myrenderer.getCurSwcList().seg.get(0).row.get(0).x
//                                + " " + myrenderer.getCurSwcList().seg.get(0).row.get(0).y + " " + myrenderer.getCurSwcList().seg.get(0).row.get(0).z);
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//
//                File flagFile = new File(externalFileDir + "/Game/Flags/" + filename_root + ".txt");
//                if (flagFile.exists()) {
//                    BufferedReader flagReader = new BufferedReader(new InputStreamReader(new FileInputStream(flagFile)));
//                    ArrayList<String> arrayList = new ArrayList<>();
//                    try {
//                        String str;
//                        while ((str = flagReader.readLine()) != null) {
//                            arrayList.add(str);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    for (int i = 0; i < arrayList.size(); i++) {
//                        String str = arrayList.get(i);
//                        String offset_str = str.split("#")[1];
//                        String pos_str = str.split("#")[2];
//                        float[] offsetFlag = new float[3];
//                        offsetFlag[0] = Float.parseFloat(offset_str.split("_")[0]);
//                        offsetFlag[1] = Float.parseFloat(offset_str.split("_")[1]);
//                        offsetFlag[2] = Float.parseFloat(offset_str.split("_")[2]);
//                        float size = Float.parseFloat(offset_str.split("_")[3]);
//                        float[] posFlag = new float[3];
//                        posFlag[0] = Float.parseFloat(pos_str.split(" ")[0]);
//                        posFlag[1] = Float.parseFloat(pos_str.split(" ")[1]);
//                        posFlag[2] = Float.parseFloat(pos_str.split(" ")[2]);
//                        float[] curFlag = new float[3];
//                        curFlag[0] = (posFlag[0] * size + offsetFlag[0] - offset[0]) / size;
//                        curFlag[1] = (posFlag[1] * size + offsetFlag[1] - offset[1]) / size;
//                        curFlag[2] = (posFlag[2] * size + offsetFlag[2] - offset[2]) / size;
//                        myrenderer.appendGameFlags(curFlag);
//                    }
//                }
//
//                Log.d(TAG, "LoadGame: clearScore()");
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean saveFlag(){
//        String externalFileDir = context.getExternalFilesDir(null).toString();
//        File file = new File(externalFileDir + "/Game/Flags");
//        if (!file.exists()){
//            try {
//                file.mkdir();
//            } catch (Exception e){
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        float x_pos = gameCharacter.getPosition()[0];
//        float y_pos = gameCharacter.getPosition()[1];
//        float z_pos = gameCharacter.getPosition()[2];
//
//        float x_dir = gameCharacter.getDir()[0];
//        float y_dir = gameCharacter.getDir()[1];
//        float z_dir = gameCharacter.getDir()[2];
//
//        float x_head = gameCharacter.getHead()[0];
//        float y_head = gameCharacter.getHead()[1];
//        float z_head = gameCharacter.getHead()[2];
//
//        String pos_str = Float.toString(x_pos) + ' ' + Float.toString(y_pos) + ' ' + Float.toString(z_pos);
//        String dir_str = Float.toString(x_dir) + ' ' + Float.toString(y_dir) + ' ' + Float.toString(z_dir);
//        String head_str = Float.toString(x_head) + ' ' + Float.toString(y_head) + ' ' + Float.toString(z_head);
//        String last_str = Integer.toString(lastIndex);
////        String curn_str = Integer.toString(curN);
////        String curSWC_str = Integer.toString(curSWC);
//
//        File flagFile = new File(externalFileDir + "/Game/Flags/" + filename_root + ".txt");
//        if (!flagFile.exists()){
//            try {
//                flagFile.createNewFile();
//            } catch (Exception e){
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        try {
//            FileInputStream inStream = new FileInputStream(flagFile);
//            if (inStream != null){
//                InputStreamReader inputreader
//                        = new InputStreamReader(inStream, "UTF-8");
//                BufferedReader reader = new BufferedReader(inputreader);
//                String line;
//                int num = 0;
//                while ((line = reader.readLine()) != null) {
//                    num++;
//                }
//
//                inputreader.close();
//                reader.close();
//
//                String str = Integer.toString(num) + "#" + offset_str + "#" + pos_str + "#" + dir_str + "#" + head_str + "#" + last_str + "\n";
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(flagFile, true)));
//                writer.write(str);
//
//                writer.close();
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        myrenderer.appendGameFlags(gameCharacter.getPosition());
//
//
//        return true;
//    }
//
//    private boolean loadFlag(){
//        String externalFileDir = context.getExternalFilesDir(null).toString();
//        File file = new File(externalFileDir + "/Game/Flags");
//
//        if (!file.exists()){
//            return false;
//        }
//
//
//        File flagFile = new File(externalFileDir + "/Game/Flags/" + filename_root + ".txt");
//
//        if (!flagFile.exists()){
//            return false;
//        }
//
//        ArrayList<String> flagList = new ArrayList<>();
//        int num = 0;
//        try {
//            FileInputStream inStream = new FileInputStream(flagFile);
//            if (inStream != null){
//                InputStreamReader inputreader
//                        = new InputStreamReader(inStream, "UTF-8");
//                BufferedReader reader = new BufferedReader(inputreader);
//                String line;
//
//                while ((line = reader.readLine()) != null) {
//                    num++;
//                    flagList.add(line);
//                }
//
//                inStream.close();
//                inputreader.close();
//                reader.close();
//
//            } else {
//                return false;
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//
//        String [] list = new String[num];
//        for (int i = 0; i < num; i++){
//            list[i] = Integer.toString(i + 1);
//        }
//        new XPopup.Builder(this)
//                .autoDismiss(true)
//                .asCenterList("Flags", list, new OnSelectListener() {
//                    @Override
//                    public void onSelect(int position, String text) {
//                        String flagStr = flagList.get(position);
//                        String [] temp = flagStr.split("#");
//                        String offset_str = temp[1];
//                        String pos_str = temp[2];
//                        String dir_str = temp[3];
//                        String head_str = temp[4];
//                        String last_str = temp[5];
////                        String curn_str = temp[6];
////                        String curSWC_str = temp[6];
//
//                        float [] pos = new float[3];
//                        float [] dir = new float[3];
//                        float [] head = new float[3];
//
//                        pos[0] = Float.parseFloat(pos_str.split(" ")[0]);
//                        pos[1] = Float.parseFloat(pos_str.split(" ")[1]);
//                        pos[2] = Float.parseFloat(pos_str.split(" ")[2]);
//
//                        dir[0] = Float.parseFloat(dir_str.split(" ")[0]);
//                        dir[1] = Float.parseFloat(dir_str.split(" ")[1]);
//                        dir[2] = Float.parseFloat(dir_str.split(" ")[2]);
//
//                        head[0] = Float.parseFloat(head_str.split(" ")[0]);
//                        head[1] = Float.parseFloat(head_str.split(" ")[1]);
//                        head[2] = Float.parseFloat(head_str.split(" ")[2]);
//
//                        if (filename_root != null && offset_str != null){
//                            gameCharacter = new GameCharacter(pos, dir, head);
//                            lastIndex = Integer.parseInt(last_str);
////                            curN = Integer.parseInt(curn_str);
////                            curSWC = Integer.parseInt(curSWC_str);
//
//                            remoteSocket.disConnectFromHost();
////                            remoteSocket.connectServer(ip_SEU);
//                            remoteSocket.connectServer(ip_TencentCloud);
//                            remoteSocket.pullImageBlockWhenLoadGame(filenameRES, offset_str);
//
//                            setFilename_Remote(filenameRES, context);
////                    setNeuronNumber_Remote(neuronNum_Backup,fileName_Backup,mContext);
//                            setoffset_Remote(offset_str, filenameRES, context);
//
//                            offset[0] = Integer.parseInt(offset_str.split("_")[0]);
//                            offset[1] = Integer.parseInt(offset_str.split("_")[1]);
//                            offset[2] = Integer.parseInt(offset_str.split("_")[2]);
//                            size = Integer.parseInt(offset_str.split("_")[3]);
//
//                            myrenderer.clearCurSwcList();
//                            myrenderer.clearGameFlags();
////                            travelPath.clear();
//
//                            File swcDir = new File(externalFileDir + "/Game/SWCs");
//                            if (swcDir.exists()){
//                                File swcFile = new File(externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
//                                if (swcFile.exists()){
////                                    Log.d("LoadFlag", "SwcFile exits");
////                                    Log.d("LoadFlag", externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
//                                    NeuronTree nt = NeuronTree.readSWC_file(externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
////                                    myrenderer.importNeuronTree(nt);
//                                    try {
//
//                                        V_NeuronSWC seg = new V_NeuronSWC();
//                                        for (int i = 0; i < nt.listNeuron.size(); i++){
//                                            NeuronSWC unit = nt.listNeuron.get(i);
////                                            if (unit.x > offset[0] + size / 2 || unit.x < offset[0] - size / 2
////                                                    || unit.y > offset[1] + size / 2 || unit.y < offset[1] - size / 2
////                                                    || unit.z > offset[2] + size / 2 || unit.z < offset[2] - size / 2){
////
////                                            } else {
//                                                V_NeuronSWC_unit newUnit = new V_NeuronSWC_unit();
//                                                newUnit.n = unit.n;
//                                                newUnit.x = unit.x - offset[0] + size / 2;
//                                                newUnit.y = unit.y - offset[1] + size / 2;
//                                                newUnit.z = unit.z - offset[2] + size / 2;
//                                                newUnit.parent = unit.parent;
//                                                newUnit.type = unit.type;
//
//                                                seg.append(newUnit);
////                                            }
//                                        }
//
//                                        if (seg.nrows() > 0){
//                                            myrenderer.addSwc(seg);
//                                        }
//
////                                        Log.d("LoadFlag", "CurSWCList: " + myrenderer.getCurSwcList().nsegs());
////                                        Log.d("LoadFlag", "CurSWCList seg0 size: " + myrenderer.getCurSwcList().seg.get(0).nrows());
////                                        Log.d("LoadFlag", "CurSWCList first point: " + myrenderer.getCurSwcList().seg.get(0).row.get(0).x
////                                                + " " + myrenderer.getCurSwcList().seg.get(0).row.get(0).y + " " + myrenderer.getCurSwcList().seg.get(0).row.get(0).z);
//                                    } catch (Exception e){
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//
//                            for (int i = 0; i < flagList.size(); i++){
//                                String str = flagList.get(i);
//                                String offset_str_flag = str.split("#")[1];
//                                String pos_str_flag = str.split("#")[2];
//                                float [] offsetFlag = new float[3];
//                                offsetFlag[0] = Float.parseFloat(offset_str_flag.split("_")[0]);
//                                offsetFlag[1] = Float.parseFloat(offset_str_flag.split("_")[1]);
//                                offsetFlag[2] = Float.parseFloat(offset_str_flag.split("_")[2]);
//                                float size = Float.parseFloat(offset_str_flag.split("_")[3]);
//                                float [] posFlag = new float[3];
//                                posFlag[0] = Float.parseFloat(pos_str_flag.split(" ")[0]);
//                                posFlag[1] = Float.parseFloat(pos_str_flag.split(" ")[1]);
//                                posFlag[2] = Float.parseFloat(pos_str_flag.split(" ")[2]);
//                                float [] curFlag = new float[3];
//                                curFlag[0] = (posFlag[0] * size + offsetFlag[0] - offset[0]) / size;
//                                curFlag[1] = (posFlag[1] * size + offsetFlag[1] - offset[1]) / size;
//                                curFlag[2] = (posFlag[2] * size + offsetFlag[2] - offset[2]) / size;
//                                myrenderer.appendGameFlags(curFlag);
//                            }
//
////                            myrenderer.removeWhileLoad(offset, size);
//                        }
//                        myGLSurfaceView.requestRender();
//                    }
//                })
//                .show();
//
//        return true;
//    }
//
//    private void updateSwcToServer(){
//        String [] result = {"New", "New"};
//        result = SaveSWC_Block_Auto();
//
//        if (!result[0].equals("New")  && !result[1].equals("New")){
//            PushSWC_Block_Auto(result[0], result[1]);
//        }
//    }
//
//    private String[] SaveSWC_Block_Auto(){
//
//        String filepath = this.getExternalFilesDir(null).toString();
//        String swc_file_path = filepath + "/Sync/BlockSet";
//        File dir = new File(swc_file_path);
//
//        if (!dir.exists()){
//            if (!dir.mkdirs())
//                Toast.makeText(this,"Fail to create file: PushSWC_Block", Toast.LENGTH_SHORT).show();
//        }
//
//        String filename = getFilename_Remote(this);
//        String neuron_number = getNeuronNumber_Remote(this, filename);
//        String offset = getoffset_Remote(this, filename);
//        System.out.println(offset);
//        int[] index = BigImgReader.getIndex(offset);
//        System.out.println(filename);
//
//        String ratio = Integer.toString(remoteSocket.getRatio_SWC());
//        String SwcFileName = "blockSet__" + neuron_number + "__" +
//                index[0] + "__" + index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5] + "__" + ratio;
//
//        System.out.println(SwcFileName);
//
//        if (Save_curSwc_fast(SwcFileName, swc_file_path)){
//            return new String[]{ swc_file_path, SwcFileName };
//        }
//
//        Log.v("SaveSWC_Block_Auto","Save Successfully !");
//        return new String[]{"Error", "Error"};
//    }
//
//    private boolean Save_curSwc_fast(String SwcFileName, String dir_str){
//
//        System.out.println("start to save-------");
//        myrenderer.reNameCurrentSwc(SwcFileName);
//
//        String error = "init";
//        try {
//            error = myrenderer.saveCurrentSwc(dir_str);
//            System.out.println("error:" + error);
//        } catch (Exception e) {
//            Toast_in_Thread(e.getMessage());
//            return false;
//        }
//        if (!error.equals("")) {
//            if (error.equals("This file already exits")){
//                String errorMessage = "";
//                try{
//                    errorMessage = myrenderer.oversaveCurrentSwc(dir_str);
//                    if (errorMessage == "Overwrite failed!"){
//                        Toast_in_Thread("Fail to save swc file: Save_curSwc_fast");
//                        return false;
//                    }
//                }catch (Exception e){
//                    System.out.println(errorMessage);
//                    Toast_in_Thread(e.getMessage());
//                    return false;
//                }
//            }
////            if (error.equals("Current swc is empty!")){
////                Toast_in_Thread("Current swc file is empty!");
////                return false;
////            }
//        } else{
//            System.out.println("save SWC to " + dir_str + "/" + SwcFileName + ".swc");
//        }
//        return true;
//    }
//
//    private void PushSWC_Block_Auto(String swc_file_path, String SwcFileName){
//
//        if (swc_file_path.equals("Error"))
//            return;
//
//        File SwcFile = new File(swc_file_path + "/" + SwcFileName + ".swc");
//        if (!SwcFile.exists()){
//            Toast_in_Thread("Something Wrong When Upload SWC, Try Again Please !");
//            return;
//        }
//        try {
//            System.out.println("Start to push swc file");
//            InputStream is = new FileInputStream(SwcFile);
//            long length = SwcFile.length();
//
//            if (length <= 0 || length > Math.pow(2, 28)){
//                Toast_in_Thread("Something Wrong When Upload SWC, Try Again Please !");
//                return;
//            }
//            remoteSocket.PushSwc_block(SwcFileName + ".swc", is, length);
//
//        } catch (Exception e){
//            System.out.println("----" + e.getMessage() + "----");
//        }
//    }
//
//    private void initSWCWriter(){
//        String externalFileDir = context.getExternalFilesDir(null).toString();
////        String filename_root = SettingFileManager.getFilename_Remote(context);
//
//        File swcDir = new File(externalFileDir + "/Game/SWCs");
////        Log.d("InitSWCWriter", externalFileDir + "/Game/SWCs");
//
//        if (!swcDir.exists()){
//            File parent = swcDir.getParentFile();
//            if (!parent.exists())
//                parent.mkdir();
//            swcDir.mkdir();
//        }
//        curSWCFile = new File(externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
//        Log.d("InitSWCWriter", filename_root + "  " + externalFileDir + "/Game/SWCs/" + filename_root + ".swc");
//
//        if (curSWCFile.exists()){
//            curSWCFile.delete();
//        }
//
//        try {
//            curSWCFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        if (!curSWCFile.exists()){
////            try {
////                curSWCFile.createNewFile();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
//
//
//        try {
////            swcFile.createNewFile();
//            Log.d("initSWCWriter", "write head information");
//            swcWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(curSWCFile, true)));
//
//            swcWriter.write("#name \n");
//            swcWriter.write("#comment \n");
//            swcWriter.write("##n,type,x,y,z,radius,parent\n");
//            swcWriter.flush();
////            swcWriter.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
////        try {
////            Log.d("initSWCWriter", "write head information");
////            swcWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(swcFile, true)));
////
////            swcWriter.write("#name \n");
////            swcWriter.write("#comment \n");
////            swcWriter.write("##n,type,x,y,z,radius,parent\n");
////        } catch (Exception e){
////            Log.d("initSWCWriter", "Exception occurred");
////            e.printStackTrace();
////        }
//    }
//
//    public void initFlags(){
//        String externalFileDir = context.getExternalFilesDir(null).toString();
//        File file = new File(externalFileDir + "/Game/Flags");
//        if (file.exists()){
//            for (File f : file.listFiles()){
//                f.delete();
//            }
//            file.delete();
//        }
//
//        try {
//            file.mkdir();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        File flagFile = new File(externalFileDir + "/Game/Flags/" + filename_root + ".txt");
//        if (flagFile.exists()){
//            flagFile.delete();
//        }
//
//        try {
//            flagFile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    class MyGLSurfaceView extends GLSurfaceView {
//
//        public MyGLSurfaceView(Context context) {
//            super(context);
//
//            ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
//            ConfigurationInfo info = am.getDeviceConfigurationInfo();
//            String v = info.getGlEsVersion(); //判断是否为3.0 ，一般4.4就开始支持3.0版本了。
//
//            Log.v("GameActivity", "GLES-version: " + v);
//
//            //设置一下opengl版本；
//            setEGLContextClientVersion(3);
//
////            myrenderer.setLineDrawed(lineDrawed);
//
//            setRenderer(myrenderer);
//
//
//            //调用 onPause 的时候保存EGLContext
//            setPreserveEGLContextOnPause(true);
//
//            //当发生交互时重新执行渲染， 需要配合requestRender();
////            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//
//
//        }
//
//    }
//
//    public void onDestroy() {
//        super.onDestroy();
//
//        Log.d(TAG, "onDestroy");
//
//        timer.cancel();
//        task.cancel();
//
//        try {
//            swcWriter.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        gameContext = null;
//    }
//}