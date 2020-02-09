package com.example.myapplication__volume;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


//打开文件管理器读取文件
public class FileActivity extends AppCompatActivity {

    //message 字符串用于传递文件的路径到Mainactivity中
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private static Context context;

    private InputStream is;
    private int length;

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file);
        tv = (TextView)findViewById(R.id.textView3);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
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


    public void ReadFile(View view) {
        Log.v("MainActivity","Log.v输入日志信息");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String message = "Hello World";

        Log.v("MainActivity","dfdsfsdfsdfsdfvxcxv");

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String filePath= uri.toString();
//            String filePath = uri.getPath();
//            String filePath = Uri2PathUtil.getRealPathFromUri(getApplicationContext(), uri);
//            String filePath = FilePath.substring(14);
//            String filePath = "/storage/emulated/0/Download/image.v3draw";



            Log.v("MainActivity",filePath);
            Log.v("Uri_Scheme:",uri.getScheme());
            tv.setText(filePath);
            Toast.makeText(this, "Open" + filePath + "--successfully", Toast.LENGTH_SHORT).show();

            try{

                Log.v("MainActivity","123456");
//                Log.v("MainActivity",String.valueOf(fileSize));

                Intent intent = new Intent(this, MainActivity.class);
                message = filePath;
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);

                Uri uri_1 = Uri.parse((String) filePath);

                Log.v("Uri_1: ", uri_1.toString());

                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(uri_1, "r");

                is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);

                length = (int)parcelFileDescriptor.getStatSize();

                Log.v("Legth: ", Integer.toString(length));


//                File f = new File(filePath);
//                FileInputStream fid = new FileInputStream(f);

//                fid.write(message.getBytes());
//                long fileSize = f.length();
            }catch (Exception e){
                Toast.makeText(this, " dddddd  ", Toast.LENGTH_SHORT).show();
                Log.v("MainActivity","111222");
            }
        }
    }

    public InputStream getInputStream(){
        return is;
    }

    public long getlength(){
        return length;
    }
}
