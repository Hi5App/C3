package com.penglab.hi5.chat.nim.session.action;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.chat.nim.file.browser.FileBrowserActivity;
import com.penglab.hi5.R;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.constant.RequestCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class FileAction extends BaseAction {

    public FileAction() {
        super(R.drawable.message_plus_file_selector, R.string.input_panel_file);
    }

    /**
     * **********************文件************************
     */
    private void chooseFile() {
        FileBrowserActivity.startActivityForResult(getActivity(), makeRequestCode(RequestCode.GET_LOCAL_FILE));
    }

    @Override
    public void onClick() {
        Log.e("loadLocalFile","Successfully Start load localFile");
        chooseFile();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.GET_LOCAL_FILE) {
            String path = data.getStringExtra(FileBrowserActivity.EXTRA_DATA_PATH);
            File file = new File(path);
            IMMessage message = MessageBuilder.createFileMessage(getAccount(), getSessionType(), file, file.getName());
            sendMessage(message);
        }
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        Log.e("onActivityResult","Here we are");
//
//        if (requestCode == RequestCode.GET_LOCAL_FILE) {
//            Uri uri = data.getData();
//            InputStream is = null;
//            int length;
//
//            try {
//                ParcelFileDescriptor parcelFileDescriptor =
//                        getContext().getContentResolver().openFileDescriptor(uri, "r");
//
//                is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
//
//                length = (int)parcelFileDescriptor.getStatSize();
//
//                Log.v("MyPattern","Successfully load intensity");
//
//            }catch (Exception e){
//                e.printStackTrace();
//                Log.v("MyPattern","Some problems in the MyPattern when load intensity");
//                return;
//            }
//
//            String filename = getFileName(uri);
//            String path = getContext().getExternalFilesDir(null).toString();
//            File file = new File(path + "/" + filename);
//
//            try {
//                OutputStream outputStream = new FileOutputStream(file);
//                IOUtils.copy(is, outputStream);
//                outputStream.close();
//            }catch (Exception e){
//                e.printStackTrace();
//                Toast.makeText(DemoCache.getContext(), "Error when create file!" + e.getMessage(),Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            IMMessage message = MessageBuilder.createFileMessage(getAccount(), getSessionType(), file, filename);
//            sendMessage(message);
//        }
//    }


    private void loadLocalFile(){

        Log.e("loadLocalFile","Successfully Start load localFile");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");    //设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            getActivity().startActivityForResult(intent,RequestCode.GET_LOCAL_FILE);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(InfoCache.getContext(), "Error when open file!" + e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

}
