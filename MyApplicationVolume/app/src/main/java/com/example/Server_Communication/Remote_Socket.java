package com.example.Server_Communication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.ImageReader.BigImgReader;
import com.example.basic.NeuronTree;
import com.example.basic.SettingFileManager;
import com.example.myapplication__volume.MainActivity;
import com.example.myapplication__volume.R;
import com.example.server_connect.Filesocket_receive;
import com.feature_calc_func.MorphologyCalculate;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import cn.carbs.android.library.MDDialog;

import static com.example.basic.SettingFileManager.getFilename_Local;
import static com.example.basic.SettingFileManager.getFilename_Remote;
import static com.example.basic.SettingFileManager.getFilename_Remote_Check;
import static com.example.basic.SettingFileManager.getNeuronNumber_Remote;
import static com.example.basic.SettingFileManager.getRES;
import static com.example.basic.SettingFileManager.getoffset_Local;
import static com.example.basic.SettingFileManager.getoffset_Remote;
import static com.example.basic.SettingFileManager.getoffset_Remote_Check;
import static com.example.basic.SettingFileManager.setFilename_Remote;
import static com.example.basic.SettingFileManager.setFilename_Remote_Check;
import static com.example.basic.SettingFileManager.setNeuronNumber_Remote;
import static com.example.basic.SettingFileManager.setRES;
import static com.example.basic.SettingFileManager.setoffset_Remote;
import static com.example.basic.SettingFileManager.setoffset_Remote_Check;
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

    public static String ArborNumber_Selected = "Empty";
    public static String BrainNumber_Selected = "Empty";
    public static Vector<String> RES_List = new Vector<>();
    public static Vector<String> Neuron_Number_List = new Vector<>();
    public static HashMap<String, Vector<String>> Neuron_Info = new HashMap<>();
    public static Vector<String> Soma_List = new Vector<>();
    public static Vector<String> Arbor_List = new Vector<>();
    public static ArrayList<Integer> Marker_List = new ArrayList<Integer>();
    public static String RES_Selected = "Empty";
    public static String Neuron_Number_Selected = "Empty";
    public static String Pos_Selected = "Empty";
    public static String Offset_Selected = "Empty";

    public Remote_Socket(Context context){

        isSocketSet = false;
        mContext = context;
        mActivity = getActivity(mContext);
        socket_send = new Socket_Send(context);
        socket_receive = new Socket_Receive(context);

        Store_path = context.getExternalFilesDir(null).toString();

        if (!getFilename_Remote(mContext).equals("--11--")){
            BrainNumber_Selected = getFilename_Remote(mContext).split("/")[0];
        }

    }


    public void ConnectServer(String ip_server){

//        Log.v("ConnectServer","Start to Connect Server !");

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
//                        Toast_in_Thread("Connect Server Successfully !");
                        Log.v("ConnectServer", "Connect Server Successfully !");

                    } else {
                        Toast_in_Thread("Can't Connect, Try Again Please !");
                    }


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

    }



    public void onReadyRead(String information){

        String LoginRex = ":log in success.";
        String LogoutRex = ":log out success.";
        String ImportRex = ":import port.";
        String CurrentDirDownExp = ":currentDir_down.";
        String CurrentDirLoadExp = ":currentDir_load.";
        String CurrentDirImgDownExp = ":currentDirImg";
        String CurrentDirArborDownExp = ":currentDirArbor";
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
            }else if (information.contains(CurrentDirImgDownExp)) {
                String[] file_string = information.split(":");
                String[] file_list = file_string[0].split(";");

                for (int i = 0; i < file_list.length; i++)
                    Log.v("onReadyRead", file_list[i]);

                ShowListDialog(file_list);
//                ShowListDialog(mContext, file_list, "CurrentDirImgDownExp");
            }else if (information.contains(CurrentDirArborDownExp)){
                String[] file_string = information.split(":");
                String[] file_list = file_string[0].split(";");

                for (int i = 0; i < file_list.length; i++)
                    Log.v("onReadyRead", file_list[i]);

                ShowListDialog_Check(file_list);
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
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                Toast.makeText(context,"你点击了" + items[which], Toast.LENGTH_SHORT).show();

                Log.v("ShowListDialog", type);

                Send_Brain_Number(items[which]);

                if (type.equals("CurrentDirDownExp"))
//                    send1(items[which], context);
                if (type.equals("CurrentDirLoadExp"))
//                    send1(items[which], context);
                if (type == "CurrentDirImgDownExp" ){
                    Log.v("ShowListDialog","Start to Send BrainNumber.");
                    Send_Brain_Number(items[which]);
                }

//                Log.v("ShowListDialog","Start to Send BrainNumber.");

            }
        });
        listDialog.show();

    }


    public void ShowListDialog(final String[] items){

        new XPopup.Builder(mContext)
//        .maxWidth(400)
        .maxHeight(1350)
                .asCenterList("Select a Brain", items,
                        new OnSelectListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSelect(int position, String text) {
                                Send_Brain_Number(text);
                            }
                        })
                .show();

    }


    public void ShowListDialog_Check(final String[] items){

        new XPopup.Builder(mContext)
//        .maxWidth(400)
//        .maxHeight(1350)
                .asCenterList("Select a Arbor", items,
                        new OnSelectListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSelect(int position, String text) {
                                System.out.println("text: " + text);
                                System.out.println("text: " + text.replace(".v3draw",""));

                                Send_Arbor_Number(text.replace(".v3draw",""));
                            }
                        })
                .show();

    }



    public String PullSwc_block(boolean isDrawMode){

        Make_Connect();

        String SwcFilePath = "Error";

        if (CheckConnection()){

            if (isDrawMode){

                String file_path = mContext.getExternalFilesDir(null).toString() + "/Sync/BlockGet";

                String filename = getFilename_Remote(mContext);
                String neuron_number = getNeuronNumber_Remote(mContext, filename);
                String offset = getoffset_Remote(mContext, filename);
                int[] index = BigImgReader.getIndex(offset);
                System.out.println(filename);

                String ratio = Integer.toString(getRatio_SWC());

                String SwcFileName = neuron_number + "__" +
                        index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5];

                Send_Message(SwcFileName + "__" + ratio + ":GetBBSwc.\n");
                Get_File(file_path, true);

                SwcFilePath = file_path + "/blockGet__" + SwcFileName + "__" + ratio  + ".swc";

            }else {

                String file_path = mContext.getExternalFilesDir(null).toString() + "/Check/Sync/BlockGet";

                String filename = getFilename_Remote_Check(mContext);
                String offset = getoffset_Remote_Check(mContext, filename);
                System.out.println(filename);

                String offset_final = offsetSwitch(filename, offset);
                String SwcFileName = filename + offset_final;

                Send_Message(SwcFileName + ":GetArborSwc.\n");
                Get_File(file_path, true);

                SwcFilePath = file_path + "/blockGet__" + SwcFileName.replaceAll(";","__") + ".swc";

            }


        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }

        return SwcFilePath;
    }




    public String PullApo_block(){

        Make_Connect();

        String ApoFilePath = "Error";

        if (CheckConnection()){


            String file_path = mContext.getExternalFilesDir(null).toString() + "/Sync/BlockGet/Apo";

            String filename = getFilename_Remote(mContext);
            String neuron_number = getNeuronNumber_Remote(mContext, filename);
            String offset = getoffset_Remote(mContext, filename);
            int[] index = BigImgReader.getIndex(offset);
            System.out.println(filename);

            String ratio = Integer.toString(getRatio_SWC());

            String ApoFileName = neuron_number + "__" +
                    index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5];

            Send_Message(ApoFileName + "__" + ratio + ":GetBBApo.\n");
            Get_File(file_path, true);

            ApoFilePath = file_path + "/blockGet__" + ApoFileName + "__" + ratio  + ".apo";

        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }

        return ApoFilePath;
    }


    public void PushSwc_block(String filename, InputStream is, long length){

        Make_Connect();

        if (CheckConnection()){

            socket_send.Send_File(ManageSocket, filename, is, length);

        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }

    }


    public void PushApo_block(String filename, InputStream is, long length){

        Make_Connect();

        if (CheckConnection()){

            socket_send.Send_File(ManageSocket, filename, is, length);

        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }

    }


    public void Select_Arbor(){

        Send_Message("connect for android client" + ":GetArborList.\n");
        String Msg = Get_Message();

        if (Msg.equals("Error")){
            Toast_in_Thread("Something Wrong When Select_Arbor !");
            return;
        }

        onReadyRead(Msg);

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


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Send_Arbor_Number(String ArborNumber){

        Make_Connect();

        Log.v("Send_Brain_Number","Start to Send ArborNumber.");

        ArborNumber_Selected = ArborNumber;
        setFilename_Remote_Check(ArborNumber, mContext);
        String offset_check = getoffset_Remote_Check(mContext, ArborNumber);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Send_Message(ArborNumber + ";" + offset_check + ":GetArbor.\n");

                String Store_path_Img = Store_path + "/Img/Check";
                Get_Img(Store_path_Img,true);
            }
        });

        thread.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Send_Brain_Number(String BrainNumber){

        Make_Connect();

        Log.v("Send_Brain_Number","Start to Send BrainNumber.");

        BrainNumber_Selected = BrainNumber;
        setFilename_Remote(BrainNumber, mContext);
        Send_Message(BrainNumber + ":BrainNumber.\n");

        String Store_path_txt = Store_path + "/BrainInfo";
        String Final_Path = Get_File(Store_path_txt, true);

        if (Final_Path.equals("Error")){
            Toast_in_Thread("Something Error When Get_File");
            return;
        }

        Analyze_TXT(Final_Path);

        Select_RES(Transform(RES_List));

    }



    public void SelectBlock(){

        if (getFilename_Remote(mContext).equals("--11--")){
            Toast.makeText(mContext,"Select a Remote File First, please !", Toast.LENGTH_SHORT).show();
            Log.v("SelectBlock","The File is not Selected");
            return;
        }

        Make_Connect();

        if (CheckConnection()){
            PopUp(true);
        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }


    }

    private void PopUp(boolean isDirect){
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

                        String offset, offset_x, offset_y, offset_z, size;

                        if (isDirect){

                            String filename = getFilename_Remote(mContext);
                            offset = getoffset_Remote(mContext, filename);
                            offset_x = offset.split("_")[0];
                            offset_y = offset.split("_")[1];
                            offset_z = offset.split("_")[2];
                            size     = offset.split("_")[3];

                        }else {

                            String[] offset_transform = transform_offset();
                            offset_x = offset_transform[0];
                            offset_y = offset_transform[1];
                            offset_z = offset_transform[2];
                            size     = offset_transform[3];

                        }

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

                            String filename = getFilename_Remote(mContext);
                            setoffset_Remote(offset, filename, mContext);

                            String[] input = JudgeEven(offset_x, offset_y, offset_z, size);

                            if (!JudgeBounding(input)){
                                PopUp(isDirect);
                                Toast.makeText(mContext, "Please Make sure All the Information is Right !", Toast.LENGTH_SHORT).show();
                            }else {
                                Make_Connect();

                                if (CheckConnection()){
                                    PullImageBlock(input[0], input[1], input[2], input[3], false);
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




    private void PullImageBlock(final String offset_x, final String offset_y, final String offset_z, final String size, boolean NeedWaited){

        Thread thread = new Thread() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            public void run() {

                try {

                    String filename = getFilename_Remote(mContext);

                    String Store_path_Img = Store_path + "/Img";
                    String StoreFilename = filename +
                            "_" + offset_x + "_" + offset_y + "_" + offset_z + "_" + size +"_" + size +"_" + size + ".v3dpbd";

                    File img = new File(Store_path_Img + "/" + StoreFilename);

                    if (img.exists() && img.length()>0){

                        Log.d("PullImageBlock","The File exists Already !");
                        MainActivity.LoadBigFile_Remote(Store_path_Img + "/" + StoreFilename);

                    } else {

                        String msg = filename + "__" + offset_x + "__" + offset_y + "__" + offset_z + "__" + size + ":imgblock.\n";
                        Send_Message(msg);

                        Get_Img(Store_path_Img,true);
                        Log.v("PullImageBlock", "x: " + offset_x + ", y:" + offset_y + ", z:" +offset_z + ", size: " + size +  " successfully---------");

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast_in_Thread("Can't Connect, Try Again Please !");
                }

            }
        };

        thread.start();

        if (NeedWaited){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }




    public void Select_RES(String[] RESs){


        RES_Selected = RESs[RESs.length - 1];
        setFilename_Remote(BrainNumber_Selected + "/" + RES_Selected, mContext);   //  18454/RES250x250x250

        String filename = getFilename_Remote(mContext);
        String neuron_num = getNeuronNumber_Remote(mContext,filename);

        if (!neuron_num.equals("--11--")){
            Vector<String> Neuron_Number_List_show = Adjust_Index();
            Select_Neuron(Transform(Neuron_Number_List_show));

        }else {
            Select_Neuron(Transform(Neuron_Number_List));

        }

//        new XPopup.Builder(mContext)
////        .maxWidth(400)
////        .maxHeight(1350)
//                .asCenterList("Select a RES", RESs,
//                        new OnSelectListener() {
//                            @Override
//                            public void onSelect(int position, String text) {
//                                RES_Selected = text;
//                                Select_Neuron(Transform(Neuron_Number_List));
//                                setFilename_Remote(BrainNumber_Selected + "/" + RES_Selected, mContext);   //  18454/RES250x250x250
//                            }
//                        })
//                .show();

    }

    public void Select_Neuron(String[] Neurons){

        new XPopup.Builder(mContext)
//        .maxWidth(400)
        .maxHeight(1350)
                .asCenterList("Select a Neuron", Neurons,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                Neuron_Number_Selected = text;
                                System.out.println(Neuron_Number_Selected);
                                if (Neuron_Info.get(Neuron_Number_Selected) != null){

                                    // Select soma without choose pos
                                    Pos_Selected = Neuron_Info.get(Neuron_Number_Selected).get(0);
                                    PopUp(false);

//                                    Select_Pos(Transform(Neuron_Info.get(Neuron_Number_Selected)));
                                    setNeuronNumber_Remote(Neuron_Number_Selected, BrainNumber_Selected + "/" + RES_Selected, mContext);  //18454_00002
                                }else {
                                    Toast_in_Thread("Information not Exist !");
                                }
                            }
                        })
                .show();

    }


    public void Select_Neuron_Fast(){

        Vector<String> Neuron_Number_List_show = Adjust_Index();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Select_Neuron(Transform(Neuron_Number_List_show));
            }
        });

        thread.start();
    }


    private Vector<String> Adjust_Index(){

        Vector<String> Neuron_Number_List_show = new Vector<String>();

        String filename = getFilename_Remote(mContext);
        String neuron_num = getNeuronNumber_Remote(mContext,filename);

        int index = Neuron_Number_List.indexOf(neuron_num);
        Log.v("Adjust_Index"," " + index);

        int start = Math.max(index - 1, 0);
        int end   = (index - 1) > 0 ? Neuron_Number_List.size() + index : Neuron_Number_List.size() + index - 1;
        for (int i = start; i <= end; i++){
            Neuron_Number_List_show.add(Neuron_Number_List.get(i % Neuron_Number_List.size()));
        }

        return Neuron_Number_List_show;
    }

    public void Select_Pos(String[] Pos){

        new XPopup.Builder(mContext)
//        .maxWidth(400)
//        .maxHeight(1350)
                .asCenterList("Select a Pos", Pos,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                Pos_Selected = text;
                                PopUp(false);
                            }
                        })
                .show();

    }



    public void Selectblock_fast(Context context, boolean source, String direction){

        if (getFilename_Remote(context) == "--11--"){
            Toast.makeText(context,"Select file first!", Toast.LENGTH_SHORT).show();
            return;
        }

        Make_Connect();

        if (CheckConnection()){
            switch (direction){
                case "Left":
                    PullImageBlock_fast(context, "Left");
                    break;
                case "Right":
                    PullImageBlock_fast(context, "Right");
                    break;
                case "Top":
                    PullImageBlock_fast(context, "Top");
                    break;
                case "Bottom":
                    PullImageBlock_fast(context, "Bottom");
                    break;
                case "Front":
                    PullImageBlock_fast(context, "Front");
                    break;
                case "Back":
                    PullImageBlock_fast(context, "Back");
                    break;
                default:
                    Toast.makeText(context,"Something wrong when pull img", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }

    }

    private void PullImageBlock_fast(Context context, String direction){
        String filename = getFilename_Remote(context);        // BrainNumber: 18465/RES250x250x250
        String offset = getoffset_Remote(context, filename);  // offset:

        String offset_x = offset.split("_")[0];
        String offset_y = offset.split("_")[1];
        String offset_z = offset.split("_")[2];
        String size     = offset.split("_")[3];

        int size_i     = Integer.parseInt(size);
        int offset_x_i = Integer.parseInt(offset_x);
        int offset_y_i = Integer.parseInt(offset_y);
        int offset_z_i = Integer.parseInt(offset_z);

        switch (direction){
            case "Left":
                offset_x_i -= size_i/2;
                offset_x = Integer.toString(offset_x_i);
                break;
            case "Right":
                offset_x_i += size_i/2;
                offset_x = Integer.toString(offset_x_i);
                break;
            case "Top":
                offset_y_i -= size_i/2;
                offset_y = Integer.toString(offset_y_i);
                break;
            case "Bottom":
                offset_y_i += size_i/2;
                offset_y = Integer.toString(offset_y_i);
                break;
            case "Front":
                offset_z_i -= size_i/2;
                offset_z = Integer.toString(offset_z_i);
                break;
            case "Back":
                offset_z_i += size_i/2;
                offset_z = Integer.toString(offset_z_i);
                break;
        }


        offset = offset_x + "_" + offset_y + "_" + offset_z + "_" + size;
        setoffset_Remote(offset, filename, context);

        System.out.println("---------" + offset + "---------");
        String[] input = JudgeEven(offset_x, offset_y, offset_z, size);

        if (!JudgeBounding(input)){
            Toast_in_Thread("Please make sure all the information is right !");
        }else {
            PullImageBlock(input[0], input[1], input[2], input[3], true);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Selectblock_fast_Check(Context context, boolean source, String direction){

        if (getFilename_Remote_Check(context).equals("--11--")){
            Toast.makeText(context,"Select file first!", Toast.LENGTH_SHORT).show();
            return;
        }

        Make_Connect();

        if (CheckConnection()){

            String[] Direction = {"Left", "Right", "Top", "Bottom", "Front", "Back"};

            if (Arrays.asList(Direction).contains(direction)){
                PullImageBlock_fast_Check(context, direction);
            }else {
                Toast.makeText(context,"Something wrong when pull img", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void PullImageBlock_fast_Check(Context context, String direction){
        String filename = getFilename_Remote_Check(context);
        String offset = getoffset_Remote_Check(context, filename);

        int img_size_x_i = getImg_size()[0];
        int img_size_y_i = getImg_size()[1];
        int img_size_z_i = getImg_size()[2];

        int offset_x_i = ( Integer.parseInt(offset.split(";")[0]) + Integer.parseInt(offset.split(";")[1]) ) / 2 -1;
        int offset_y_i = ( Integer.parseInt(offset.split(";")[2]) + Integer.parseInt(offset.split(";")[3]) ) / 2 -1;
        int offset_z_i = ( Integer.parseInt(offset.split(";")[4]) + Integer.parseInt(offset.split(";")[5]) ) / 2 -1;
        int size_i     = ( Integer.parseInt(offset.split(";")[1]) - Integer.parseInt(offset.split(";")[0]) );

        switch (direction){
            case "Left":
                if ( (offset_x_i - size_i/2) == 0 ){
                    System.out.println("----- You have already reached left boundary!!! -----");
                    Toast_in_Thread("You have already reached left boundary!!!");
                    return;
                }else {
                    offset_x_i -= size_i/2;
                    if (offset_x_i - size_i/2 < 0)
                        offset_x_i = size_i/2;
                }
                break;

            case "Right":
                if ( (offset_x_i + size_i/2) == img_size_x_i - 1 ){
                    Toast_in_Thread("You have already reached right boundary!!!");
                    return;
                }else {
                    offset_x_i += size_i/2;
                    if (offset_x_i + size_i/2 > img_size_x_i - 1)
                        offset_x_i = img_size_x_i - 1 - size_i/2;
                }
                break;

            case "Top":
                if ( (offset_y_i - size_i/2) == 0 ){
                    Toast_in_Thread("You have already reached top boundary!!!");
                    return;
                }else {
                    offset_y_i -= size_i/2;
                    if (offset_y_i - size_i/2 < 0)
                        offset_y_i = size_i/2;
                }
                break;

            case "Bottom":
                if ( (offset_y_i + size_i/2) == img_size_y_i - 1 ){
                    Toast_in_Thread("You have already reached bottom boundary!!!");
                    return;
                }else {
                    offset_y_i += size_i/2;
                    if (offset_y_i + size_i/2 > img_size_y_i - 1)
                        offset_y_i = img_size_y_i - 1 - size_i/2;
                }
                break;

            case "Front":
                if ( (offset_z_i - size_i/2) == 0 ){
                    Toast_in_Thread("You have already reached front boundary!!!");
                    return;
                }else {
                    offset_z_i -= size_i/2;
                    if (offset_z_i - size_i/2 < 0)
                        offset_z_i = size_i/2;
                }
                break;

            case "Back":
                if ( (offset_z_i + size_i/2) == img_size_z_i - 1 ){
                    Toast_in_Thread("You have already reached back boundary!!!");
                    return;
                }else {
                    offset_z_i += size_i/2;
                    if (offset_z_i + size_i/2 > img_size_z_i - 1)
                        offset_z_i = img_size_z_i - 1 - size_i/2;
                }
                break;
        }

        String[] index = {Integer.toString(offset_x_i - size_i/2 + 1), Integer.toString(offset_y_i - size_i/2 + 1), Integer.toString(offset_z_i - size_i/2 +1 ),
                Integer.toString(offset_x_i + size_i/2 + 1), Integer.toString(offset_y_i + size_i/2 + 1), Integer.toString(offset_z_i + size_i/2 +1 )};

        offset = index[0] + ";" + index[3] + ";" + index[1] + ";" + index[4] + ";" + index[2] + ";" + index[5];
        setoffset_Remote_Check(offset, filename, context);

        Send_Arbor_Number(filename);

    }

    private int[] getImg_size(){

        String filename = getFilename_Remote_Check(mContext);

        int start_x = Integer.parseInt(filename.split("RES")[1].split("__")[1]);
        int end_x   = Integer.parseInt(filename.split("RES")[1].split("__")[2]);
        int start_y = Integer.parseInt(filename.split("RES")[1].split("__")[3]);
        int end_y   = Integer.parseInt(filename.split("RES")[1].split("__")[4]);
        int start_z = Integer.parseInt(filename.split("RES")[1].split("__")[5]);
        int end_z   = Integer.parseInt(filename.split("RES")[1].split("__")[6]);

        return new int[]{end_x - start_x, end_y - start_y, end_z - start_z};
    }

    public float[] getImg_size_f(){

        String filename = getFilename_Remote_Check(mContext);

        int start_x = Integer.parseInt(filename.split("RES")[1].split("__")[1]);
        int end_x   = Integer.parseInt(filename.split("RES")[1].split("__")[2]);
        int start_y = Integer.parseInt(filename.split("RES")[1].split("__")[3]);
        int end_y   = Integer.parseInt(filename.split("RES")[1].split("__")[4]);
        int start_z = Integer.parseInt(filename.split("RES")[1].split("__")[5]);
        int end_z   = Integer.parseInt(filename.split("RES")[1].split("__")[6]);

        return new float[]{end_x - start_x, end_y - start_y, end_z - start_z};
    }



    public void Zoom_in(){

        Vector<String> res_temp = getRES(mContext, BrainNumber_Selected);
        String filename = getFilename_Remote(mContext);   // BrainNumber: 18465/RES250x250x250
        String res_cur  = filename.split("/")[1];   // RES: RES250x250x250
        int res_index = res_temp.indexOf(res_cur);

        if (res_index >= res_temp.size() - 1){
            Toast_in_Thread("You have been in the Highest Resolution !");
            return;
        }

        float ratio = getRatio(res_temp.get(res_index + 1), res_cur);
        String offset = getoffset_Remote(mContext, filename);
        String[] offset_new = getNewOffset(offset, ratio);

        setFilename_Remote(filename.split("/")[0] + "/" + res_temp.get(res_index + 1), mContext);
        RES_Selected = res_temp.get(res_index + 1);


        String[] input = JudgeEven(offset_new[0], offset_new[1], offset_new[2], offset_new[3]);
        if (!JudgeBounding(input)){

            setFilename_Remote(filename, mContext);
            RES_Selected = res_cur;
            Toast_in_Thread("Check the Bounding Box !");

        }else {
            Make_Connect();

            if (CheckConnection()){
                offset = offset_new[0] + "_" + offset_new[1] + "_" + offset_new[2] + "_" + offset_new[3];
                setoffset_Remote(offset, filename.split("/")[0] + "/" + res_temp.get(res_index + 1), mContext);
                setNeuronNumber_Remote(Neuron_Number_Selected, filename.split("/")[0] + "/" + res_temp.get(res_index + 1), mContext);
                PullImageBlock(input[0], input[1], input[2], input[3], true);
            }else {
                setFilename_Remote(filename, mContext);
                RES_Selected = res_cur;
                Toast_in_Thread("Can't Connect Server, Try Again Later !");
            }
        }

    }

    public void Zoom_out(){

        Vector<String> res_temp = getRES(mContext, BrainNumber_Selected);
        String filename = getFilename_Remote(mContext);
        String res_cur  = filename.split("/")[1];
        int res_index = res_temp.indexOf(res_cur);

        if (res_index <= 0){
            Toast_in_Thread("You have been in the Lowest Resolution !");
            return;
        }

        float ratio = getRatio(res_temp.get(res_index - 1), res_cur);
        String offset = getoffset_Remote(mContext, filename);
        String[] offset_new = getNewOffset(offset, ratio);

        setFilename_Remote(filename.split("/")[0] + "/" + res_temp.get(res_index - 1), mContext);
        RES_Selected = res_temp.get(res_index - 1);

        String[] input = JudgeEven(offset_new[0], offset_new[1], offset_new[2], offset_new[3]);
        if (!JudgeBounding(input)){

            setFilename_Remote(filename, mContext);
            RES_Selected = res_cur;
            Toast_in_Thread("Check the Bounding Box !");

        }else {
            Make_Connect();

            if (CheckConnection()){
                offset = offset_new[0] + "_" + offset_new[1] + "_" + offset_new[2] + "_" + offset_new[3];
                setoffset_Remote(offset, filename.split("/")[0] + "/" + res_temp.get(res_index - 1), mContext);
                setNeuronNumber_Remote(Neuron_Number_Selected, filename.split("/")[0] + "/" + res_temp.get(res_index - 1), mContext);
                PullImageBlock(input[0], input[1], input[2], input[3], true);
            }else {
                setFilename_Remote(filename, mContext);
                RES_Selected = res_cur;
                Toast_in_Thread("Can't Connect Server, Try Again Later !");
            }
        }

    }


    public void PullCheckResult(){

        Make_Connect();

        if (CheckConnection()){

            Send_Message("From Android Client :GetArborResult.\n");

            String Store_path_check_txt = Store_path + "/Check/Check_Result";
            String Final_Path = Get_File(Store_path_check_txt, true);

            if (Final_Path.equals("Error")){
                Toast_in_Thread("Something Error When Get_File");
                return;
            }

            Display_Result(Final_Path);
        }

    }


    private void Display_Result(String File_Path){
        ArrayList<String> arraylist = new ArrayList<String>();
        File file = new File(File_Path);

        if (!file.exists()){
            Toast_in_Thread("Fail to Open TXT File !");
            return;
        }

        try {
            FileInputStream fid = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);
            String str;
            while ((str = br.readLine()) != null) {
                arraylist.add(str.trim());
            }

            String info = "";

            for (int i = 0; i < arraylist.size(); i++){
                String line = arraylist.get(i);

                String display = line.split(" ")[0] + ": ";

                if (line.substring(line.length() - 1).equals("0")){
                    display = display + "No";
                }else {
                    display = display + "Yes";
                }

                info = info + display + "\n\n";
            }


            String finalInfo = info;
            MDDialog mdDialog = new MDDialog.Builder(mContext)
                    .setContentView(R.layout.check_result)
                    .setContentViewOperator(new MDDialog.ContentViewOperator() {
                        @Override
                        public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                            //analysis_result next page
                            TextView display_info = (TextView) contentView.findViewById(R.id.check_result);
                           display_info.setText(finalInfo);
                        }
                    })
                    .setTitle("Check Result")
                    .create();
            mdDialog.show();
            mdDialog.getWindow().setLayout(1000, 1500);


//            new XPopup.Builder(mContext)
//                    .asConfirm("Check Result", info,
//                            new OnConfirmListener() {
//                                @Override
//                                public void onConfirm() {
//                                }
//                            })
//                    .show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast_in_Thread("Fail to Read TXT File !");
        }


    }




    public void Check_Yes(boolean isDrawMode){

        Make_Connect();

        if (CheckConnection()){

            if (isDrawMode){

                String filename = getFilename_Remote(mContext);
                String neuron_number = getNeuronNumber_Remote(mContext, filename);
                String offset = getoffset_Remote(mContext, filename);
                int[] index = BigImgReader.getIndex(offset);
                System.out.println(filename);

                String ratio = Integer.toString(getRatio_SWC());

                String Check_info = neuron_number + "__" +
                        index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5];

                Send_Message(Check_info + "__" + ratio + "__1" + ":SwcCheck.\n");

            }else {

                String filename = getFilename_Remote_Check(mContext);
                String offset = getoffset_Remote_Check(mContext, filename);
                System.out.println(filename);

                String offset_final = offsetSwitch(filename, offset);
                String Check_Info = filename + offset_final + ";1" + ":ArborCheck.\n";

                Send_Message(Check_Info);
            }

        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }

    }


    public void Check_No(boolean isDrawMode){

        Make_Connect();

        if (CheckConnection()){

            if (isDrawMode){

                String filename = getFilename_Remote(mContext);
                String neuron_number = getNeuronNumber_Remote(mContext, filename);
                String offset = getoffset_Remote(mContext, filename);
                int[] index = BigImgReader.getIndex(offset);
                System.out.println(filename);

                String ratio = Integer.toString(getRatio_SWC());

                String Check_info = neuron_number + "__" +
                        index[0] + "__" +index[3] + "__" + index[1] + "__" + index[4] + "__" + index[2] + "__" + index[5];

                Send_Message(Check_info + "__" + ratio + "__0" + ":SwcCheck.\n");

            }else {

                String filename = getFilename_Remote_Check(mContext);
                String offset = getoffset_Remote_Check(mContext, filename);
                System.out.println(filename);

                String offset_final = offsetSwitch(filename, offset);
                String Check_Info = filename + offset_final + ";0" + ":ArborCheck.\n";

                Send_Message(Check_Info);

            }

        }else {
            Toast_in_Thread("Can't Connect Server, Try Again Later !");
        }


    }

    private float getRatio(String res_new, String res_old){

        String y_new = res_new.split("x")[1];
        String y_old  = res_old.split("x")[1];

        int y_new_int = Integer.parseInt(y_new);
        int y_old_int = Integer.parseInt(y_old);

        float ratio = ((float) y_new_int)/ ((float) y_old_int);

        return ratio;

    }


    public int getRatio_SWC(){

        Vector<String> res_temp = getRES(mContext, BrainNumber_Selected);
        String filename = getFilename_Remote(mContext);
        String res_cur  = filename.split("/")[1];

        String res_new = res_temp.lastElement();
        String res_old = res_cur;

        int ratio = (int) (getRatio(res_new, res_old));

        return ratio;

    }

    private String[] getNewOffset(String offset, float ratio){

        String offset_x = offset.split("_")[0];
        String offset_y = offset.split("_")[1];
        String offset_z = offset.split("_")[2];
        String size     = offset.split("_")[3];

        float offset_x_float = Float.parseFloat(offset_x) * ratio;
        float offset_y_float = Float.parseFloat(offset_y) * ratio;
        float offset_z_float = Float.parseFloat(offset_z) * ratio;

        return new String[]{
                Integer.toString((int) offset_x_float),
                Integer.toString((int) offset_y_float),
                Integer.toString((int) offset_z_float),
                size};

    }

    private String[] transform_offset(){
        String offset = Pos_Selected.split(":")[1];

        Vector<String> res_temp = getRES(mContext, BrainNumber_Selected);
        String filename = getFilename_Remote(mContext);
        String res_cur  = filename.split("/")[1];
        int res_index = res_temp.indexOf(res_cur);

        System.out.println("res_cur: " + res_cur + "---");
        System.out.println("res_highest: " + res_temp.lastElement() + "---");

        if (res_index == res_temp.size()-1){
            return new String[]{Integer.toString((int) Float.parseFloat(offset.split(";")[0])),
                                Integer.toString((int) Float.parseFloat(offset.split(";")[1])),
                                Integer.toString((int) Float.parseFloat(offset.split(";")[2])),
                                "128"};
        }

        float ratio = getRatio(res_cur, res_temp.lastElement());
        String[] offset_result = getNewOffset(offset.replaceAll(";","_") + "_128", ratio);
        return offset_result;

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


    private String Get_File(String file_path, boolean Need_Waited){

        return socket_receive.Get_File(ManageSocket, file_path, Need_Waited);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Get_Img(String file_path, boolean Need_Waited){

        socket_receive.Get_Block(ManageSocket, file_path, Need_Waited);

    }


    private void Make_Connect(){

        if (ManageSocket == null || !CheckConnection()){
            Log.v("Make_Connect","Connect Again");
            ConnectServer(ip);
        }

    }

    private boolean CheckConnection(){

        return ManageSocket!= null && ManageSocket.isConnected() && !ManageSocket.isClosed();

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

        String filename = getFilename(mContext).replace("(","");
        filename = filename.replace(")","");
        String size = filename.split("RES")[1];

        System.out.println("hhh-------" + size + "--------hhh");

        // current filename mouse18864_teraconvert/RES(35001x27299x10392)   : y x z
        int x_size = Integer.parseInt(size.split("x")[1]);
        int y_size = Integer.parseInt(size.split("x")[0]);
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



    public void Analyze_TXT(String File_Path){
        ArrayList<String> arraylist = new ArrayList<String>();
        File file = new File(File_Path);

        if (!file.exists()){
            Toast_in_Thread("Fail to Open TXT File !");
            return;
        }

        try {
            FileInputStream fid = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);
            String str;
            while ((str = br.readLine()) != null) {
                arraylist.add(str.trim());
            }

            Vector<String> RES_List_temp = new Vector<>();
            Vector<String> Neuron_Number_List_temp = new Vector<>();
            HashMap<String, Vector<String>> Neuron_Info_temp = new HashMap<>();

            for (int i = 0; i < arraylist.size(); i++){
                String line = arraylist.get(i);

                if (line.startsWith("#RES")){
                    String[] split = line.split(":");
                    int num = Integer.parseInt(split[1]);
                    for (int j = 0; j < num; j++){
                        i++;
                        String RES = arraylist.get(i);
                        RES_List_temp.add(RES.split(":")[1]);
                    }
                }

                if (line.startsWith("#Neuron_number")){
                    String[] split = line.split(":");
                    int num = Integer.parseInt(split[1]);
                    for (int j = 0; j < num; j++){
                        i++;
                        String Number = arraylist.get(i);
                        Neuron_Number_List_temp.add(Number.split(":")[1]);
                    }
                }

                if (line.startsWith("##")){
                    String neuron_number = line.substring(2);
                    Vector<String> point_list = new Vector<>();
                    point_list.add(arraylist.get(++i));

                    String[] marker = arraylist.get(i).split(":")[1].split(";");
                    Marker_List.add(Integer.parseInt(marker[0]));
                    Marker_List.add(Integer.parseInt(marker[1]));
                    Marker_List.add(Integer.parseInt(marker[2]));

                    String[] split = arraylist.get(++i).split(":");
                    int num = Integer.parseInt(split[1]);
                    for (int j = 0; j < num; j++){
                        i++;
                        String offset = arraylist.get(i).split(":")[1];
                        point_list.add("arbor:" + offset);
                        Log.v("Neuron_Info: ", " " + Neuron_Info_temp.size());

                    }

                    Neuron_Info_temp.put(neuron_number, point_list);

                }

                RES_List = RES_List_temp;
                Neuron_Number_List = Neuron_Number_List_temp;
                Neuron_Info = Neuron_Info_temp;

                setRES(RES_List.toArray(new String[RES_List.size()]), BrainNumber_Selected, mContext);

            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast_in_Thread("Fail to Read TXT File !");
        }


    }


    public void LoadTXT(){

        Store_path = mContext.getExternalFilesDir(null).toString();

        String Store_path_txt = Store_path + "/BrainInfo/";
        String filename = getFilename_Remote(mContext).split("/")[0];
        Log.v("LoadTXT",Store_path_txt + filename);
        Analyze_TXT(Store_path_txt + filename + ".txt");

    }



    public ArrayList<ArrayList<Integer>> getMarker(int[] index){

        ArrayList<ArrayList<Integer>> marker_list = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < Marker_List.size(); i = i + 3){
            int x = Marker_List.get(i);
            int y = Marker_List.get(i+1);
            int z = Marker_List.get(i+2);

//            Log.v("getMarker","(" + x + ", " + y + ", " + z + ")");

            if (x >= index[0] && y >= index[1] && z>= index[2] &&
                x < index[3] && y < index[4] && z < index[5]){
                ArrayList<Integer> marker = new ArrayList<Integer>();
                marker.add(x - index[0]);
                marker.add(y - index[1]);
                marker.add(z - index[2]);
                marker_list.add(marker);
            }
        }

        Log.v("getMarker",marker_list.size() + " !");
        Log.v("getMarker",Marker_List.size() + " !");
        return marker_list;

    }

    private String offsetSwitch(String filename, String offset_old){

        String offset_neuron = filename.split("RES")[1];
        System.out.println("offset_neuron" + offset_neuron);
        int offset_start_x = Integer.parseInt(offset_neuron.split("__")[1]);
        int offset_start_y = Integer.parseInt(offset_neuron.split("__")[3]);
        int offset_start_z = Integer.parseInt(offset_neuron.split("__")[5]);

        String[] offset = offset_old.split(";");
        String offset_result = "";

        for (int i=0; i<6; i++){

            int bais = 0;
            switch (i+1){
                case 1:
                case 2:
                    bais = offset_start_x;
                    break;
                case 3:
                case 4:
                    bais = offset_start_y;
                    break;
                case 5:
                case 6:
                    bais = offset_start_z;
                    break;
            }

            offset_result = offset_result + ";" + Integer.toString(Integer.parseInt(offset[i]) + bais - 1);

        }

        return offset_result;

    }



    public String getIp(){
        Make_Connect();

        if (CheckConnection()){
            return this.ip;
        }

        return "Can't connect server";

    }

    String[] Transform(Vector<String> strings){

        String[] string_list = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++){
            string_list[i] = strings.get(i);
        }

        return string_list;
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
