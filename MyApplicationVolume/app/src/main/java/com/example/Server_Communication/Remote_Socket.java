package com.example.Server_Communication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.ImageReader.BigImgReader;
import com.example.myapplication__volume.MainActivity;
import com.example.myapplication__volume.R;
import com.example.server_connect.Filesocket_receive;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import cn.carbs.android.library.MDDialog;

import static com.example.basic.SettingFileManager.setFilename_Remote;
import static com.example.basic.SettingFileManager.setoffset_Remote;
import static com.example.server_connect.RemoteImg.getFilename;
import static com.example.server_connect.RemoteImg.getoffset;

public class Remote_Socket extends Socket {

    private Context mContext;
    private Activity mActivity;
    public String ip;
    public int id;
    public Socket ManageSocket = null;
    public PrintWriter mPWriter;  //PrinterWriter  用于接收消息
    public BufferedReader ImgReader;//BufferedWriter 用于推送消息
    public PrintWriter ImgPWriter;  //PrinterWriter  用于接收消息

    public boolean isSocketSet;
    public String Store_path;
    public volatile boolean flag;

    private Socket_Send socket_send;
    private Socket_Receive socket_receive;

    public Remote_Socket(Context context){

        isSocketSet = false;
        mContext = context;
        mActivity = getActivity(mContext);
        socket_send = new Socket_Send(context);
        socket_receive = new Socket_Receive(context);

        Store_path = context.getExternalFilesDir(null).toString();

    }


    public void ConnectServer(String ip_server){

        if (ManageSocket != null && !ManageSocket.isClosed() && ManageSocket.isConnected()){
            return;
        }

        //新建一个线程，用于初始化socket和检测是否有接收到新的消息
        Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    ip = ip_server;
                    ManageSocket = new Socket(ip_server, Integer.parseInt("9000"));
                    ImgReader = new BufferedReader(new InputStreamReader(ManageSocket.getInputStream(), "UTF-8"));
                    ImgPWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(ManageSocket.getOutputStream(), StandardCharsets.UTF_8)));

                    if (ManageSocket.isConnected()) {

                        isSocketSet = true;
                        Toast_in_Thread("Connect Server Successfully !");
                        Log.v("ConnectServer", "Connect Server Successfully !");

                    } else {
                        Toast_in_Thread("Can't Connect, Try Again Please !");
                    }


//                    /*
//                     Read the feedback when connect successfully
//                     */
//                    if (ManageSocket.isConnected()) {
//                        if (!ManageSocket.isInputShutdown()) {
//                            String content = "";
//                            Log.d("-- ConnectServer --:", "Start to Read Line");
//
//                            if ((content = ImgReader.readLine()) != null) {
//                                Log.v("-- ConnectServer --:", content);
////                                onReadyRead(content, context);
//
//                            }
//                        }
//                    }
                } catch (IOException e) {
                    Toast_in_Thread("Something Wrong When Connect Server");
                    e.printStackTrace();
                }

            }
        };
        thread.start();


        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.v("ConnectServer", "thread.join() Successfully !");

    }



    public void onReadyRead(String information){

        String LoginRex = ":log in success.";
        String LogoutRex = ":log out success.";
        String ImportRex = ":import port.";
        String CurrentDirDownExp = ":currentDir_down.";
        String CurrentDirLoadExp = ":currentDir_load.";
        String CurrentDirImgDownExp = ":currentDirImg.";
        String MessagePortExp = ":messageport.\n";
        String ImportportExp = ":importport.\n";


        Log.v("onReadyRead", information);

        if(information != null){

            if (information.contains(LoginRex)){

                Toast.makeText(mContext, "login successfully.", Toast.LENGTH_SHORT).show();
            }else if (information.contains(LogoutRex)){

                Toast.makeText(mContext, "logout successfully.", Toast.LENGTH_SHORT).show();

            }else if (information.contains(ImportRex)){

                if (!ManageSocket.isConnected()){

                    Toast.makeText(mContext, "can not connect with Manageserver.", Toast.LENGTH_SHORT).show();
                    return;
                }
                /**
                 *  something
                 */
            }else if (information.contains(CurrentDirDownExp)){
                Log.v("onReadyRead", "CurrentDirDownExp  here we are");
                String [] file_string = information.split(":");
                String [] file_list = file_string[0].split(";");

                for (int i = 0; i < file_list.length; i++)
                    Log.v("onReadyRead", file_list[i]);

                ShowListDialog(mContext, file_list, "CurrentDirDownExp");

            }else if (information.contains(CurrentDirLoadExp)){
                String [] file_string = information.split(":");
                String [] file_list = file_string[0].split(";");

                for (int i = 0; i < file_list.length; i++)
                    Log.v("onReadyRead", file_list[i]);

                ShowListDialog(mContext, file_list, "CurrentDirLoadExp");
            }else if (information.contains(CurrentDirImgDownExp)){
                String [] file_string = information.split(":");
                String [] file_list = file_string[0].split(";");

                for (int i = 0; i < file_list.length; i++)
                    Log.v("onReadyRead", file_list[i]);

                ShowListDialog(mContext, file_list, "CurrentDirImgDownExp");
            }else if (information.contains(ImportportExp)){

            }
        }
    }



    /**
     * Choose the file you want down
     * @param context the activity context
     * @param items the list of filename
     * @param type the communication type
     */
    private void ShowListDialog(final Context context, final String[] items, final String type) {

        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(context);
        listDialog.setTitle("选择要下载的文件");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(context,"你点击了" + items[which], Toast.LENGTH_SHORT).show();
                if (type.equals("CurrentDirDownExp"))
//                    send1(items[which], context);
                if (type.equals("CurrentDirLoadExp"))
//                    send1(items[which], context);
                if (type.equals("CurrentDirImgDownExp")){
                    Send_Brain_Number(items[which]);
                }
            }
        });
        listDialog.show();

    }



    public String PullSwc_block(){

        Make_Connect();

        String SwcFilePath = "Error";

        if (CheckConnection()){

            String file_path = mContext.getExternalFilesDir(null).toString() + "/Sync/BlockGet";

            String filename = getFilename(mContext);
            String offset = getoffset(mContext, filename);
            int[] index = BigImgReader.getIndex(offset);
            System.out.println(filename);

            String SwcFileName = filename.split("RES")[0] + "__" +
                    index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5];

            Send_Message(SwcFileName + ":GetBBSwc.");
            Get_File(file_path, true);

            SwcFilePath = file_path + "/Sync/BlockGet/" +  "blockGet__" + SwcFileName + ".swc";

        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }

        return SwcFilePath;
    }


    public void Select_Brain(){

        Send_Message("connect for android client" + ":choose3.\n");
        String Msg = Get_Message();

        if (Msg.equals("Error")){
            Toast_in_Thread("Something Wrong When Select_Brain !");
            return;
        }

        onReadyRead(Msg);

    }


    public void Send_Brain_Number(String BrainNumber){

        setFilename_Remote(BrainNumber, mContext);
        Send_Message(BrainNumber + ":BrainNumber.\n");

        String Store_path_txt = Store_path + "/" + BrainNumber;
        Get_File(Store_path_txt, true);

    }



    public void SelectBlock(){

        if (getFilename(mContext).equals("--11--")){
            Toast.makeText(mContext,"Select a Remote File First, please !", Toast.LENGTH_SHORT).show();
            Log.v("SelectBlock","The File is not Selected");
            return;
        }

        Make_Connect();

        if (CheckConnection()){
            PopUp();
        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }


    }

    private void PopUp(){
        new MDDialog.Builder(mContext)
//              .setContentView(customizedView)
                .setContentView(R.layout.image_bais_select)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);
                        EditText et4 = (EditText) contentView.findViewById(R.id.edit4);

                        String filename = getFilename(mContext);
                        String offset = getoffset(mContext, filename);

                        String offset_x = offset.split("_")[0];
                        String offset_y = offset.split("_")[1];
                        String offset_z = offset.split("_")[2];
                        String size     = offset.split("_")[3];


                        et1.setText(offset_x);
                        et2.setText(offset_y);
                        et3.setText(offset_z);
                        et4.setText(size);

                    }
                })
                .setTitle("Download Image")
                .setNegativeButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                        //这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view，目的是方便在确定/取消按键中对contentView进行操作，如获取数据等。
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);
                        EditText et4 = (EditText) contentView.findViewById(R.id.edit4);

                        String offset_x   = et1.getText().toString();
                        String offset_y   = et2.getText().toString();
                        String offset_z   = et3.getText().toString();
                        String size       = et4.getText().toString();

                        if( !offset_x.isEmpty() && !offset_y.isEmpty() && !offset_z.isEmpty() && !size.isEmpty()){

                            String offset = offset_x + "_" + offset_y + "_" + offset_z + "_" + size;

                            String filename = getFilename(mContext);
                            setoffset_Remote(offset, filename, mContext);

                            String[] input = JudgeEven(offset_x, offset_y, offset_z, size);

                            if (!JudgeBounding(input)){
                                PopUp();
                                Toast.makeText(mContext, "Please Make sure All the Information is Right !", Toast.LENGTH_SHORT).show();
                            }else {
                                Make_Connect();

                                if (CheckConnection()){
                                    PullImageBlock(input[0], input[1], input[2], input[3]);
                                }else {
                                    Toast_in_Thread("Can't Connect Server, Try Again Later !");
                                }

                            }

                        }else{

                            Toast.makeText(mContext, "Please Make sure All the Information is Right !", Toast.LENGTH_SHORT).show();
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






    private void PullImageBlock(final String offset_x, final String offset_y, final String offset_z, final String size){

        new Thread() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {

                try {

                    String filename = getFilename(mContext);

                    String StorePath = mContext.getExternalFilesDir(null).toString();
                    String StoreFilename = filename.split("RES")[0] +
                            "_" + offset_x + "_" + offset_y + "_" + offset_z + "_" + size +"_" + size +"_" + size + ".v3dpbd";

                    File img = new File(StorePath + "/" + StoreFilename);

                    if (img.exists() && img.length()>0){

                        Log.d("PullImageBlock","The File exists Already !");
                        MainActivity.LoadBigFile_Remote(StorePath + "/" + StoreFilename);

                    } else {

                        String msg = filename + "__" + offset_x + "__" + offset_y + "__" + offset_z + "__" + size + ":imgblock.\n";
                        Send_Message(msg);

                        Get_Img(StoreFilename,true);
                        Log.v("PullImageBlock", "x: " + offset_x + ", y:" + offset_y + ", z:" +offset_z + ", size: " + size +  " successfully---------");

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast_in_Thread("Can't Connect, Try Again Please !");
                }

            }
        }.start();

    }



    private void Send_Message(String message) {
        Make_Connect();

        if (CheckConnection()){
            socket_send.Send_Message(ManageSocket, message);
        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }

    }

    private void Send_File(String filename, InputStream is, long length_content){
        Make_Connect();

        if (CheckConnection()){
            socket_send.Send_File(ManageSocket, filename, is, length_content);
        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }
    }


    private String Get_Message(){

        if (ManageSocket == null || ManageSocket.isClosed()){
            Log.d("Get_Message","ManageSocket.isClosed()");
            return "Error";
        }

        String msg = socket_receive.Get_Message(ManageSocket);

        if (msg != null)
            return msg;
        else
            return "Error";
    }


    private void Get_File(String file_path, boolean Need_Waited){

        socket_receive.Get_File(ManageSocket, file_path, Need_Waited);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Get_Img(String file_path, boolean Need_Waited){

        socket_receive.Get_Block(ManageSocket, file_path, Need_Waited);

    }


    private void Make_Connect(){

        if (ManageSocket == null || !CheckConnection()){
            ConnectServer(ip);
        }

    }

    private boolean CheckConnection(){

        return ManageSocket.isConnected() && !ManageSocket.isClosed();

    }



    private String[] JudgeEven(String offset_x, String offset_y, String offset_z, String size){

        float x_offset_f = Float.parseFloat(offset_x);
        float y_offset_f = Float.parseFloat(offset_y);
        float z_offset_f = Float.parseFloat(offset_z);
        float size_f     = Float.parseFloat(size);

        int x_offset_i = (int) x_offset_f;
        int y_offset_i = (int) y_offset_f;
        int z_offset_i = (int) z_offset_f;
        int size_i = (int) size_f;

        if (x_offset_i % 2 == 1)
            x_offset_i += 1;

        if (y_offset_i % 2 == 1)
            y_offset_i += 1;

        if (z_offset_i % 2 == 1)
            z_offset_i += 1;

        if (size_i % 2 == 1)
            size_i += 1;

        String[] result = new String[4];
        result[0] = Integer.toString(x_offset_i);
        result[1] = Integer.toString(y_offset_i);
        result[2] = Integer.toString(z_offset_i);
        result[3] = Integer.toString(size_i);

        Log.v("JudgeEven", Arrays.toString(result));

        return result;
    }

    private boolean JudgeBounding(String[] input){

        int x_offset_i = Integer.parseInt(input[0]);
        int y_offset_i = Integer.parseInt(input[1]);
        int z_offset_i = Integer.parseInt(input[2]);
        int size_i = Integer.parseInt(input[3]);

        String filename = getFilename(mContext);
        String size = filename.split("RES")[1];

        System.out.println("hhh-------" + size + "--------hhh");

        int x_size = Integer.parseInt(size.split("x")[0]);
        int y_size = Integer.parseInt(size.split("x")[1]);
        int z_size = Integer.parseInt(size.split("x")[2]);


        if ( ( x_offset_i - size_i/2 < 0 ) || ( x_offset_i + size_i/2 > x_size -2) )
            return false;

        if ( ( y_offset_i - size_i/2 < 0 ) || ( y_offset_i + size_i/2 > y_size -2) )
            return false;

        if ( ( z_offset_i - size_i/2 < 0 ) || ( z_offset_i + size_i/2 > z_size -2) )
            return false;


        return true;
    }



    public void DisConnectFromHost(){

        System.out.println("---- Disconnect from Host ----");
        try {

            if (ManageSocket != null){
                ManageSocket.close();
            }

            if (ImgReader != null){
                ImgReader.close();
            }

            if (ImgPWriter != null){
                ImgPWriter.close();
            }


        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    /**
     * toast info in the thread
     * @param message the message you wanna toast
     */
    public void Toast_in_Thread(String message){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * get the activity
     * @param context current context
     * @return current activity
     */
    @Nullable
    private static Activity getActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            ContextWrapper wrapper = (ContextWrapper) context;
            return getActivity(wrapper.getBaseContext());
        } else {
            return null;
        }
    }


}
