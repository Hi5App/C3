package com.example.myapplication__volume;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.connect.RemoteImg;

import java.io.InputStream;


//打开文件管理器读取文件
public class JumpActivity extends AppCompatActivity {

    //message 字符串用于传递文件的路径到Mainactivity中
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String Out_of_memory = "com.example.myfirstapp.MESSAGE";

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private static Context context;

    private InputStream is;
    private int length;
    private ManageSocket manageSocket;
    private RemoteImg remoteImg;
    private BroadcastReceiver broadcastReceiver;

    private boolean select_img = false;
//    private boolean select_img = true;


    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //接受从fileactivity传递过来的文件路径
        Intent intent1 = getIntent();
        String filepath = intent1.getStringExtra(JumpActivity.EXTRA_MESSAGE);

        if (filepath != null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MyRenderer.FILE_PATH, filepath);
            startActivity(intent);
        }

        Intent intent2 = getIntent();
        String MSG = intent2.getStringExtra(JumpActivity.Out_of_memory);

        if (MSG != null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MyRenderer.OUTOFMEM_MESSAGE, MSG);
            startActivity(intent);
        }




    }

    //renderer 的生存周期和activity保持一致
    @Override
    protected void onPause() {
        super.onPause();
        Log.v("onPause", "---------start------------");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("onResume", "---------start------------");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

}
