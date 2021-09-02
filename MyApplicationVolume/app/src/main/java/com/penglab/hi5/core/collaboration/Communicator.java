package com.penglab.hi5.core.collaboration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.XYZ;
import com.penglab.hi5.core.collaboration.basic.ImageInfo;
import com.penglab.hi5.core.collaboration.connector.MsgConnector;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC;
import com.penglab.hi5.basic.tracingfunc.gd.V_NeuronSWC_unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.penglab.hi5.core.MainActivity.Toast_in_Thread_static;
import static com.penglab.hi5.core.MainActivity.username;

public class Communicator {

    /**
     *  TAG when print the log info
     */
    public static final String TAG = "Communicator";

    public static ArrayList<String> resolution;               // res list of current img

    private static int ImgRes;                                // 1 is highest; max num is lowest
    private static int CurRes;                                // current img res
    public static XYZ ImageMaxRes = new XYZ();                // Soma position in highest resolution
    public static XYZ ImageCurRes = new XYZ();                // Soma position in current resolution

    public static int ImgSize;
    public static XYZ ImageStartPoint = new XYZ();
    public static XYZ ImageCurPoint = new XYZ();


    public static String BrainNum = null;
    public static String Soma     = null;
    public static String Path     = null;                              // /18454/18454_00029/1.ano

    private static String initSomaMsg  = null;                         // for inviting user
    private static String conPath      = null;                         // for inviting user

    private static String conPathQuery = null;                         // for querying database


    /**
     * current context
     */
    private static Context mContext;

    public static volatile Communicator INSTANCE;

    private Communicator(){
    }



    /**
     * 初始化，注册Context对象
     * @param ctx
     */
    public static void init(Context ctx){
        mContext = ctx;
    }



    /**
     * 获取Communicator实例 ,单例模式
     */
    public static Communicator getInstance(){
        if (INSTANCE == null){
            synchronized (Communicator.class){
                if (INSTANCE == null){
                    INSTANCE = new Communicator();
                }
            }
        }
        return INSTANCE;
    }






    /*
    for collaboration -------------------------------------------------------------------------------
     */

    public ImageMarker MSGToImageMarker(String msg){

        ImageMarker imageMarker = new ImageMarker();

        XYZ LocalCroods = ConvertGlobaltoLocalBlockCroods(Float.parseFloat(msg.split(" ")[1]),
                Float.parseFloat(msg.split(" ")[2]), Float.parseFloat(msg.split(" ")[3]));

        imageMarker.type = Integer.parseInt(msg.split(" ")[0]);
        imageMarker.x = LocalCroods.x;
        imageMarker.y = LocalCroods.y;
        imageMarker.z = LocalCroods.z;

        return imageMarker;
    }


    public V_NeuronSWC MSGToV_NeuronSWC(String msg){

        V_NeuronSWC seg = new V_NeuronSWC();

        String[] swc = msg.split(";");
        String type = msg.split(";")[0].split(" ")[1];
        for (int i = 1; i < swc.length; i++){
            V_NeuronSWC_unit segUnit = new V_NeuronSWC_unit();

            XYZ LocalCroods = ConvertGlobaltoLocalBlockCroods(Double.parseDouble(swc[i].split(" ")[1]),
                    Double.parseDouble(swc[i].split(" ")[2]), Double.parseDouble(swc[i].split(" ")[3]));

            segUnit.type = Double.parseDouble(swc[i].split(" ")[0]);
            segUnit.x = LocalCroods.x;
            segUnit.y = LocalCroods.y;
            segUnit.z = LocalCroods.z;

            if (type.equals("HI5")){

                segUnit.n = Double.parseDouble(swc[i].split(" ")[4]);
                segUnit.parent = Double.parseDouble(swc[i].split(" ")[5]);

            }else if (type.equals("TeraFly")){

                segUnit.n = i;
                if (i == 1){
                    segUnit.parent = 0;
                }else {
                    segUnit.parent = i-1;
                }
            }


            seg.row.add(segUnit);
        }

        return seg;
    }



    public List<String> V_NeuronSWCToMSG(V_NeuronSWC seg){

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < seg.row.size(); i++){
            V_NeuronSWC_unit curSWCunit = seg.row.get(i);

//            Log.e(TAG, "point " + String.format("%d %f %f %f %f %f", (int) (curSWCunit.type), curSWCunit.x, curSWCunit.y, curSWCunit.z, curSWCunit.n, curSWCunit.parent));
            XYZ GlobalCroods = ConvertLocalBlocktoGlobalCroods(curSWCunit.x,curSWCunit.y,curSWCunit.z);
            if (!result.add(String.format("%d %f %f %f %f %f", (int) (curSWCunit.type), GlobalCroods.x, GlobalCroods.y, GlobalCroods.z, curSWCunit.n, curSWCunit.parent))){
                Log.e(TAG, "Something wrong when convert V_NeuronSWC to MSG");
            }
        }

        return result;
    }


    public void updateAddMarkerMsg(ImageMarker marker)
    {
            List<String> result = new ArrayList<>();
            XYZ GlobalCroods = ConvertLocalBlocktoGlobalCroods(marker.x,marker.y,marker.z);
            result.add(String.format("%d %f %f %f", (int) marker.type, GlobalCroods.x, GlobalCroods.y, GlobalCroods.z));

            String msg = "/addmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
            msg = msg + TextUtils.join(";", result);

            MsgConnector msgConnector = MsgConnector.getInstance();
            msgConnector.sendMsg(msg);

    }


    public void updateDelMarkerMsg(ImageMarker marker)
    {
        List<String> result = new ArrayList<>();
        XYZ GlobalCroods = ConvertLocalBlocktoGlobalCroods(marker.x,marker.y,marker.z);
        result.add(String.format("%d %.3f %.3f %.3f", (int) marker.type, GlobalCroods.x, GlobalCroods.y, GlobalCroods.z));

        String msg = "/delmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
        msg = msg + TextUtils.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg);

    }



    public void updateRetypeMarkerMsg(ImageMarker origin_marker, ImageMarker current_marker)
    {
        MsgConnector msgConnector = MsgConnector.getInstance();

        /*
        del marker
         */
        List<String> result_origin = new ArrayList<>();
        XYZ GlobalCroods_origin = ConvertLocalBlocktoGlobalCroods(origin_marker.x,origin_marker.y,origin_marker.z);
        result_origin.add(String.format("%d %.3f %.3f %.3f", (int) origin_marker.type, GlobalCroods_origin.x, GlobalCroods_origin.y, GlobalCroods_origin.z));

        String msg_origin = "/delmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
        msg_origin = msg_origin + TextUtils.join(";", result_origin);
        msgConnector.sendMsg(msg_origin, true, true);

        /*
        add marker
         */
        List<String> result_current = new ArrayList<>();
        XYZ GlobalCroods_current = ConvertLocalBlocktoGlobalCroods(current_marker.x,current_marker.y,current_marker.z);
        result_current.add(String.format("%d %.3f %.3f %.3f", (int) current_marker.type, GlobalCroods_current.x, GlobalCroods_current.y, GlobalCroods_current.z));

        String msg_current = "/addmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
        msg_current = msg_current + TextUtils.join(";", result_current);
        msgConnector.sendMsg(msg_current, true, true);

    }


    public void updateAddSegSWC(V_NeuronSWC seg){
        List<String> result = V_NeuronSWCToMSG(seg);
        String msg = "/drawline_norm:" + username + " HI5 128 128 128;" + TextUtils.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg, true, true);

    }

    public void updateDelSegSWC(V_NeuronSWC seg){
        List<String> result = V_NeuronSWCToMSG(seg);
        String msg = "/delline_norm:" + username + " HI5 128 128 128;" + TextUtils.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg, true, true);

    }



    public void updateRetypeSegSWC(V_NeuronSWC seg, int type){
        List<String> result = V_NeuronSWCToMSG(seg);
        String msg = "/retypeline_norm:" + username + " HI5 " + type + " 128 128 128;" + TextUtils.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg, true, true);

    }


    public V_NeuronSWC syncSWC(String msg){
        return MSGToV_NeuronSWC(msg);
    }


    public ImageMarker syncMarker(String msg){
        return MSGToImageMarker(msg);
    }


    public XYZ ConvertGlobaltoLocalBlockCroods(double x,double y,double z)
    {
        XYZ node = ConvertMaxRes2CurrResCoords(x,y,z);
//        node.x -=(ImageStartPoint.x-1);
//        node.y -=(ImageStartPoint.y-1);
//        node.z -=(ImageStartPoint.z-1);

        /*
        -1 之后会出现删线不同步的问题
         */
        node.x -=(ImageStartPoint.x);
        node.y -=(ImageStartPoint.y);
        node.z -=(ImageStartPoint.z);
//        Log.d(TAG,"ConvertGlobaltoLocalBlockCroods x y z = " + x + " " + y + " " + z + " -> " + XYZ2String(node));
        return node;
    }

    public XYZ ConvertLocalBlocktoGlobalCroods(double x,double y,double z)
    {
//        x +=(ImageStartPoint.x-1);
//        y +=(ImageStartPoint.y-1);
//        z +=(ImageStartPoint.z-1);

        /*
        -1 之后会出现删线不同步的问题
         */
        x +=(ImageStartPoint.x);
        y +=(ImageStartPoint.y);
        z +=(ImageStartPoint.z);
        XYZ node = ConvertCurrRes2MaxResCoords(x,y,z);
//        Log.d(TAG,"ConvertLocalBlocktoGlobalCroods x y z = " + x + " " + y + " " + z + " -> " + XYZ2String(node));
        return node;
    }


    private XYZ ConvertMaxRes2CurrResCoords(double x, double y, double z){
//        x/=(ImageMaxRes.x/ImageCurRes.x);
//        y/=(ImageMaxRes.y/ImageCurRes.y);
//        z/=(ImageMaxRes.z/ImageCurRes.z);

        x /= Math.pow(2, CurRes-1);
        y /= Math.pow(2, CurRes-1);
        z /= Math.pow(2, CurRes-1);
        return new XYZ((float) x, (float) y, (float) z);
    }


    private XYZ ConvertCurrRes2MaxResCoords(double x, double y, double z){
//        x*=(ImageMaxRes.x/ImageCurRes.x);
//        y*=(ImageMaxRes.y/ImageCurRes.y);
//        z*=(ImageMaxRes.z/ImageCurRes.z);

        x *= Math.pow(2, CurRes-1);
        y *= Math.pow(2, CurRes-1);
        z *= Math.pow(2, CurRes-1);
        return new XYZ((float) x, (float) y, (float) z);
    }


    private String XYZ2String(XYZ node)
    {
        return String.format("%f %f %f", node.x, node.y, node.z);
    }

    private String XYZ2String(XYZ node,int type)
    {
        if(type!=-1)
            return String.format("%f %f %f %f", type, node.x, node.y, node.z);
        else
            return String.format("%f %f %f", node.x, node.y, node.z);
    }

    /*
    for collaboration -------------------------------------------------------------------------------
     */








    public boolean initSoma(final String msg){

//        Log.e(TAG,"msg: " + msg);
        String pattern = "(.*)_x(.*)_y(.*)_z(.*).ano";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(msg);
        if (m.find()){

//            Log.d(TAG,String.format("Found value x %s, y %s, z %s", m.group(2), m.group(3), m.group(4)));

            String x = m.group(2);
            String y = m.group(3);
            String z = m.group(4);

            if (x != null && y != null && z != null){
                ImageMaxRes.x = Float.parseFloat(x);
                ImageMaxRes.y = Float.parseFloat(y);
                ImageMaxRes.z = Float.parseFloat(z);
            }else {
                Toast_in_Thread_static("something Wrong with soma position !");
                return false;
            }

            initSomaMsg = msg;
            Soma = x.split("/.")[0] + ";" + y.split("/.")[0] + ";" + z.split("/.")[0];
            return true;
        }

        return false;
    }


    public void initImgInfo(String imgName, int imgres, int curRes, String[] resList){


        ImgRes = imgres;

        // set resolution list
        setResolution(resList);

        int[] curPos = new int[4];
        boolean exist = ImageInfo.getInstance().queryCurPath(conPathQuery);
        if (exist){
            ImageInfo imageInfo = ImageInfo.getInstance();

            /*
            read curRes from local file
            */
            if (imageInfo.queryRes(conPathQuery) != -1){
                CurRes = imageInfo.queryRes(conPathQuery);
            }else {
                Toast_in_Thread_static("Something Wrong with Res info");
            }


            /*
            read curPos from local file
            */
            if (imageInfo.queryPos(conPathQuery) != null){
                String[] curPosString = imageInfo.queryPos(conPathQuery).split(";");

                for (int i=0; i<curPosString.length; i++){
                    curPos[i] = Integer.parseInt(curPosString[i]);
                }

            }else {
                Toast_in_Thread_static("Something Wrong with Pos info");
            }


        }else {

            /*
            read curRes from local file
            */
            CurRes = curRes;

            int ratio = (int) Math.pow(2, (CurRes) - 1);
            String[] pos_str = Soma.split(";");
            for (int i = 0; i < pos_str.length; i++){
                curPos[i] = (int) (Float.parseFloat(pos_str[i]) / ratio);
            }
            curPos[3] = 128;

        }


        /*
        init
         */
        int[] pos = new int[3];
        int ratio = (int) Math.pow(2, (CurRes) - 1);
        String[] pos_str = Soma.split(";");
        for (int i = 0; i < pos_str.length; i++){
            pos[i] = (int) (Float.parseFloat(pos_str[i]) / ratio);
        }

        ImageCurRes.x = pos[0];
        ImageCurRes.y = pos[1];
        ImageCurRes.z = pos[2];

        ImageCurPoint.x = curPos[0];
        ImageCurPoint.y = curPos[1];
        ImageCurPoint.z = curPos[2];
        ImgSize         = curPos[3];

        ImageStartPoint.x = ImageCurPoint.x - ImgSize/2;
        ImageStartPoint.y = ImageCurPoint.y - ImgSize/2;
        ImageStartPoint.z = ImageCurPoint.z - ImgSize/2;

        if (!exist){
            Log.e(TAG,"initImgInfo");
            ImageInfo.getInstance().initImgInfo(conPathQuery, CurRes, String.format("%d;%d;%d;%d", (int) ImageCurPoint.x, (int) ImageCurPoint.y, (int) ImageCurPoint.z, ImgSize));
        }

        Log.e(TAG,"max res: "+ ImgRes + ", cur res: " + CurRes);

    }


    // msg format:   ImgRes:18454;6;RES(26298x35000x11041);RES(13149x17500x5520);RES(6574x8750x2760);RES(3287x4375x1380);RES(1643x2187x690);RES(821x1093x345)
    public void setResolution(String[] resList){

        resolution = new ArrayList<>();

        for (int i = 0; i < ImgRes; i++){
            resolution.add(resList[i + 2]);
        }

    }

    @SuppressLint("DefaultLocale")
    public static String getCurrentPos(){
        return String.format("%d;%d;%d;%d", (int) ImageCurPoint.x, (int) ImageCurPoint.y, (int) ImageCurPoint.z, ImgSize);
    }


    public static int getCurRes() {
        return CurRes;
    }

    public static void setCurRes(int curRes) {
        CurRes = curRes;
    }

    public static int getImgRes() {
        return ImgRes;
    }

    public static void setImgRes(int imgRes) {
        ImgRes = imgRes;
    }




    public void navigateBlock(String direction){

        String img_size = resolution.get(CurRes - 1).replace("RES(","").replace(")","");

        int img_size_x_i = Integer.parseInt(img_size.split("x")[0]);
        int img_size_y_i = Integer.parseInt(img_size.split("x")[1]);
        int img_size_z_i = Integer.parseInt(img_size.split("x")[2]);

        int offset_x_i = (int) ImageCurPoint.x;
        int offset_y_i = (int) ImageCurPoint.y;
        int offset_z_i = (int) ImageCurPoint.z;
        int size_i     =       ImgSize;

        Log.e(TAG, String.format("img: x %d, y %d, z %d", img_size_x_i, img_size_y_i, img_size_z_i));
        Log.e(TAG, String.format("cur: x %d, y %d, z %d", offset_x_i,   offset_y_i,   offset_z_i));


        String[] Direction = {"Left", "Right", "Top", "Bottom", "Front", "Back"};
        if (Arrays.asList(Direction).contains(direction)){

            switch (direction){
                case "Left":
                    if ( (offset_x_i - size_i/2 -1) == 0 ){
                        System.out.println("----- You have already reached left boundary!!! -----");
                        Toast_in_Thread_static("You have already reached left boundary!!!");
                        return;
                    }else {
                        offset_x_i -= size_i/2 + 1;
                        if (offset_x_i - size_i/2 <= 0)
                            offset_x_i = size_i/2 + 1;
                    }
                    break;

                case "Right":
                    if ( (offset_x_i + size_i/2) == img_size_x_i - 1 ){
                        Toast_in_Thread_static("You have already reached right boundary!!!");
                        return;
                    }else {
                        offset_x_i += size_i/2;
                        if (offset_x_i + size_i/2 > img_size_x_i - 1)
                            offset_x_i = img_size_x_i - 1 - size_i/2;
                    }
                    break;

                case "Top":
                    if ( (offset_y_i - size_i/2 -1) == 0 ){
                        Toast_in_Thread_static("You have already reached top boundary!!!");
                        return;
                    }else {
                        offset_y_i -= size_i/2 + 1;
                        if (offset_y_i - size_i/2 <= 0)
                            offset_y_i = size_i/2 + 1;
                    }
                    break;

                case "Bottom":
                    if ( (offset_y_i + size_i/2) == img_size_y_i - 1 ){
                        Toast_in_Thread_static("You have already reached bottom boundary!!!");
                        return;
                    }else {
                        offset_y_i += size_i/2;
                        if (offset_y_i + size_i/2 > img_size_y_i - 1)
                            offset_y_i = img_size_y_i - 1 - size_i/2;
                    }
                    break;

                case "Front":
                    if ( (offset_z_i - size_i/2 -1) == 0 ){
                        Toast_in_Thread_static("You have already reached front boundary!!!");
                        return;
                    }else {
                        offset_z_i -= size_i/2 + 1;
                        if (offset_z_i - size_i/2 <= 0)
                            offset_z_i = size_i/2 + 1;
                    }
                    break;

                case "Back":
                    if ( (offset_z_i + size_i/2) == img_size_z_i - 1 ){
                        Toast_in_Thread_static("You have already reached back boundary!!!");
                        return;
                    }else {
                        offset_z_i += size_i/2;
                        if (offset_z_i + size_i/2 > img_size_z_i - 1)
                            offset_z_i = img_size_z_i - 1 - size_i/2;
                    }
                    break;
            }
        }


        ImageCurPoint.x   = offset_x_i;
        ImageCurPoint.y   = offset_y_i;
        ImageCurPoint.z   = offset_z_i;

        ImageStartPoint.x = ImageCurPoint.x - ImgSize/2;
        ImageStartPoint.y = ImageCurPoint.y - ImgSize/2;
        ImageStartPoint.z = ImageCurPoint.z - ImgSize/2;

        MsgConnector.getInstance().sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + CurRes + ";" + Communicator.getCurrentPos() + ";");
        ImageInfo.getInstance().updatePosRes(conPathQuery, CurRes, getCurrentPos());

    }

    public void navigateAndZoomInBlock(int offset_x, int offset_y, int offset_z) {


        String img_size = resolution.get(CurRes - 1).replace("RES(","").replace(")","");

        int img_size_x_i = Integer.parseInt(img_size.split("x")[0]);
        int img_size_y_i = Integer.parseInt(img_size.split("x")[1]);
        int img_size_z_i = Integer.parseInt(img_size.split("x")[2]);

        int offset_x_i = (int) ImageCurPoint.x;
        int offset_y_i = (int) ImageCurPoint.y;
        int offset_z_i = (int) ImageCurPoint.z;
        int size_i     =       ImgSize;

        Log.e(TAG, String.format("img: x %d, y %d, z %d", img_size_x_i, img_size_y_i, img_size_z_i));
        Log.e(TAG, String.format("cur: x %d, y %d, z %d", offset_x_i,   offset_y_i,   offset_z_i));

        if ( (offset_x_i + offset_x) <= 1 || (offset_x_i + offset_x) >= img_size_x_i - 1){
//            System.out.println("----- You have already reached left boundary!!! -----");
            Toast_in_Thread_static("You have already reached boundary!!!");

        }else {
            offset_x_i += offset_x;
            if (offset_x_i - size_i / 2 <= 0)
                offset_x_i = size_i / 2 + 1;
            else if (offset_x_i + size_i / 2 >= img_size_x_i - 1)
                offset_x_i = img_size_x_i - size_i / 2 - 1;
        }

        if ( (offset_y_i + offset_y) <= 1 || (offset_y_i + offset_y) >= img_size_y_i - 1){
//            System.out.println("----- You have already reached left boundary!!! -----");
            Toast_in_Thread_static("You have already reached boundary!!!");

        }else {
            offset_y_i += offset_y;
            if (offset_y_i - size_i / 2 <= 0)
                offset_y_i = size_i / 2 + 1;
            else if (offset_y_i + size_i / 2 >= img_size_y_i - 1)
                offset_y_i = img_size_y_i - size_i / 2 - 1;
        }

        if ( (offset_z_i + offset_z) <= 1 || (offset_z_i + offset_z) >= img_size_z_i - 1){
//            System.out.println("----- You have already reached left boundary!!! -----");
            Toast_in_Thread_static("You have already reached boundary!!!");

        }else {
            offset_z_i += offset_z;
            if (offset_z_i - size_i / 2 <= 0)
                offset_z_i = size_i / 2 + 1;
            else if (offset_z_i + size_i / 2 >= img_size_z_i - 1)
                offset_z_i = img_size_z_i - size_i / 2 - 1;
        }

        ImageCurPoint.x   = offset_x_i;
        ImageCurPoint.y   = offset_y_i;
        ImageCurPoint.z   = offset_z_i;

        ImageStartPoint.x = ImageCurPoint.x - ImgSize/2;
        ImageStartPoint.y = ImageCurPoint.y - ImgSize/2;
        ImageStartPoint.z = ImageCurPoint.z - ImgSize/2;

        if (CurRes <= 1){
            MsgConnector.getInstance().sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + CurRes + ";" + Communicator.getCurrentPos() + ";");
            ImageInfo.getInstance().updatePosRes(conPathQuery, CurRes, getCurrentPos());
            return;
        }else{
            CurRes -= 1;
            ImageCurRes.x *= 2;
            ImageCurRes.y *= 2;
            ImageCurRes.z *= 2;

            ImageCurPoint.x *= 2;
            ImageCurPoint.y *= 2;
            ImageCurPoint.z *= 2;

            ImageStartPoint.x = ImageCurPoint.x - ImgSize/2;
            ImageStartPoint.y = ImageCurPoint.y - ImgSize/2;
            ImageStartPoint.z = ImageCurPoint.z - ImgSize/2;
            MsgConnector.getInstance().sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + CurRes + ";" + Communicator.getCurrentPos() + ";");
            ImageInfo.getInstance().updatePosRes(conPathQuery, CurRes, getCurrentPos());
        }
    }



    public void zoomIn(){

        if (CurRes <= 1){
            Toast_in_Thread_static("You have already reached the highest resolution !");
            return;
        }

        CurRes -= 1;
        ImageCurRes.x *= 2;
        ImageCurRes.y *= 2;
        ImageCurRes.z *= 2;

        ImageCurPoint.x *= 2;
        ImageCurPoint.y *= 2;
        ImageCurPoint.z *= 2;

        ImageStartPoint.x = ImageCurPoint.x - ImgSize/2;
        ImageStartPoint.y = ImageCurPoint.y - ImgSize/2;
        ImageStartPoint.z = ImageCurPoint.z - ImgSize/2;

        MsgConnector.getInstance().sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + CurRes + ";" + Communicator.getCurrentPos() + ";");
        ImageInfo.getInstance().updatePosRes(conPathQuery, CurRes, getCurrentPos());

    }



    public void zoomOut(){

        if (CurRes >= ImgRes){
            Toast_in_Thread_static("You have already reached the lowest resolution !");
            return;
        }

        CurRes += 1;
        ImageCurRes.x /= 2;
        ImageCurRes.y /= 2;
        ImageCurRes.z /= 2;

        ImageCurPoint.x /= 2;
        ImageCurPoint.y /= 2;
        ImageCurPoint.z /= 2;

        ImageStartPoint.x = ImageCurPoint.x - ImgSize/2;
        ImageStartPoint.y = ImageCurPoint.y - ImgSize/2;
        ImageStartPoint.z = ImageCurPoint.z - ImgSize/2;

        MsgConnector.getInstance().sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + CurRes + ";" + Communicator.getCurrentPos() + ";");
        ImageInfo.getInstance().updatePosRes(conPathQuery, CurRes, getCurrentPos());

    }


    public void switchRes(Context context){

        ArrayList<String> res_temp = (ArrayList<String>) resolution.clone();
        res_temp.set(CurRes-1, res_temp.get(CurRes-1) + "   √");

        new XPopup.Builder(context)
                .maxWidth(850)
                .asCenterList("Select a RES", Transform(res_temp,0, res_temp.size()),
                        new OnSelectListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSelect(int position, String text) {
                                setRes(position + 1);
                            }
                        })
                .show();

    }


    private void setRes(int newRes){
        if (newRes == CurRes)
            return;

        double ratio = Math.pow(2, CurRes - newRes);

        CurRes = newRes;
        ImageCurRes.x *= ratio;
        ImageCurRes.y *= ratio;
        ImageCurRes.z *= ratio;

        ImageCurPoint.x *= ratio;
        ImageCurPoint.y *= ratio;
        ImageCurPoint.z *= ratio;

        ImageStartPoint.x = ImageCurPoint.x - ImgSize/2;
        ImageStartPoint.y = ImageCurPoint.y - ImgSize/2;
        ImageStartPoint.z = ImageCurPoint.z - ImgSize/2;

        MsgConnector.getInstance().sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + CurRes + ";" + Communicator.getCurrentPos() + ";");
        ImageInfo.getInstance().updatePosRes(conPathQuery, CurRes, getCurrentPos());
    }



    /**
     * for file loading, global coords to local coords
     */

    public ArrayList<ArrayList<Float>> convertApo(ArrayList<ArrayList<Float>> apo){

        // ##n,orderinfo,name,comment,z,x,y, pixmax,intensity,sdev,volsize,mass,,,, color_r,color_g,color_b
        ArrayList<ArrayList<Float>> apo_converted = new ArrayList<ArrayList<Float>>();

        try{
            for (int i = 0; i < apo.size(); i++){
                ArrayList<Float> currentLine = apo.get(i);
                XYZ GlobalCroods = ConvertGlobaltoLocalBlockCroods(currentLine.get(5),
                        currentLine.get(6), currentLine.get(4));

                currentLine.set(5, GlobalCroods.x);
                currentLine.set(6, GlobalCroods.y);
                currentLine.set(4, GlobalCroods.z);

                apo_converted.add(currentLine);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return apo_converted;

    }

    public NeuronTree convertNeuronTree(NeuronTree nt){

        try {
            NeuronTree nt_converted = (NeuronTree) nt.clone();

            for (int i = 0; i < nt.listNeuron.size(); i++){

                XYZ GlobalCroods = ConvertGlobaltoLocalBlockCroods(nt_converted.listNeuron.get(i).x,
                        nt_converted.listNeuron.get(i).y, nt_converted.listNeuron.get(i).z);

                nt_converted.listNeuron.get(i).x = GlobalCroods.x;
                nt_converted.listNeuron.get(i).y = GlobalCroods.y;
                nt_converted.listNeuron.get(i).z = GlobalCroods.z;

            }

            return nt_converted;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public void setConPath(String p) {
        conPath = p;
        conPathQuery = conPath.replace("/","");
    }


    public static float[] getSoma(){
        return new float[]{ImageCurRes.x, ImageCurRes.y , ImageCurRes.z};
    }


    /*
    for inviting user
     */

    public String getInitSomaMsg() {
        return initSomaMsg;
    }


    public String getConPath() {
        return conPath;
    }


    String[] Transform(ArrayList<String> strings, int start, int end){

        String[] stringList = new String[end - start];
        int j = 0;
        for (int i = start; i < end; i++) {
            stringList[j++] = strings.get(i);
        }
        return stringList;
    }

}
