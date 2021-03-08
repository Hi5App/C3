package com.example.myapplication__volume.collaboration;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.basic.ImageMarker;
import com.example.basic.NeuronTree;
import com.example.basic.XYZ;
import com.tracingfunc.gd.V_NeuronSWC;
import com.tracingfunc.gd.V_NeuronSWC_unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.myapplication__volume.MainActivity.Toast_in_Thread_static;
import static com.example.myapplication__volume.MainActivity.username;

public class Communicator {

    /**
     *  TAG when print the log info
     */
    public static final String TAG = "Communicator";

    public static XYZ ImageMaxRes = new XYZ();         // Soma
    public static XYZ ImageCurRes = new XYZ();         // Soma
    public static XYZ ImageStartPoint = new XYZ();
    public static XYZ ImageCurPoint = new XYZ();


    public static String BrainNum = null;
    public static String Soma = null;

    private int ImgRes;                          // 1 is highest; max num is lowest
    private int CurRes;
    public static int ImgSize;

    public static ArrayList<String> resolution;

    /**
     * current context
     */
    private static Context mContext;

    public static  Communicator INSTANCE;

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
     * 获取CrashHandler实例 ,单例模式
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



    public ImageMarker MSGToImageMarker(String msg){

        ImageMarker imageMarker = new ImageMarker();

        XYZ GlobalCroods = ConvertGlobaltoLocalBlockCroods(Float.parseFloat(msg.split(" ")[1]),
                Float.parseFloat(msg.split(" ")[2]), Float.parseFloat(msg.split(" ")[3]));

        imageMarker.type = Integer.parseInt(msg.split(" ")[0]);
        imageMarker.x = GlobalCroods.x;
        imageMarker.y = GlobalCroods.y;
        imageMarker.z = GlobalCroods.z;

        return imageMarker;
    }


    public V_NeuronSWC MSGToV_NeuronSWC(String msg){

        V_NeuronSWC seg = new V_NeuronSWC();

        String[] swc = msg.split(";");
        for (int i = 1; i < swc.length; i++){
            V_NeuronSWC_unit segUnit = new V_NeuronSWC_unit();

            XYZ GlobalCroods = ConvertGlobaltoLocalBlockCroods(Double.parseDouble(swc[i].split(" ")[1]),
                    Double.parseDouble(swc[i].split(" ")[2]), Double.parseDouble(swc[i].split(" ")[3]));

            segUnit.type = Double.parseDouble(swc[i].split(" ")[0]);
            segUnit.x = GlobalCroods.x;
            segUnit.y = GlobalCroods.y;
            segUnit.z = GlobalCroods.z;

            segUnit.n = Double.parseDouble(swc[i].split(" ")[4]);
            segUnit.parent = Double.parseDouble(swc[i].split(" ")[5]);


//            segUnit.n = i;
//            if (i == 1){
//                segUnit.parent = -1;
//            }else {
//                segUnit.parent = i-1;
//            }

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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateAddMarkerMsg(ImageMarker marker)
    {
            List<String> result = new ArrayList<>();
            XYZ GlobalCroods = ConvertLocalBlocktoGlobalCroods(marker.x,marker.y,marker.z);
            result.add(String.format("%d %f %f %f", (int) marker.type, GlobalCroods.x, GlobalCroods.y, GlobalCroods.z));

            String msg = "/addmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
            msg = msg + String.join(";", result);

            MsgConnector msgConnector = MsgConnector.getInstance();
            msgConnector.sendMsg(msg, false);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDelMarkerMsg(ImageMarker marker)
    {
        List<String> result = new ArrayList<>();
        XYZ GlobalCroods = ConvertLocalBlocktoGlobalCroods(marker.x,marker.y,marker.z);
        result.add(String.format("%d %f %f %f", (int) marker.type, GlobalCroods.x, GlobalCroods.y, GlobalCroods.z));

        String msg = "/delmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
        msg = msg + String.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg, false);

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateRetypeMarkerMsg(ImageMarker origin_marker, ImageMarker current_marker)
    {
        MsgConnector msgConnector = MsgConnector.getInstance();

        /*
        del marker
         */
        List<String> result_origin = new ArrayList<>();
        XYZ GlobalCroods_origin = ConvertLocalBlocktoGlobalCroods(origin_marker.x,origin_marker.y,origin_marker.z);
        result_origin.add(String.format("%d %f %f %f", (int) origin_marker.type, GlobalCroods_origin.x, GlobalCroods_origin.y, GlobalCroods_origin.z));

        String msg_origin = "/delmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
        msg_origin = msg_origin + String.join(";", result_origin);
        msgConnector.sendMsg(msg_origin, true);

        /*
        add marker
         */
        List<String> result_current = new ArrayList<>();
        XYZ GlobalCroods_current = ConvertLocalBlocktoGlobalCroods(current_marker.x,current_marker.y,current_marker.z);
        result_current.add(String.format("%d %f %f %f", (int) current_marker.type, GlobalCroods_current.x, GlobalCroods_current.y, GlobalCroods_current.z));

        String msg_current = "/addmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
        msg_current = msg_current + String.join(";", result_current);
        msgConnector.sendMsg(msg_current, true);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateAddSegSWC(V_NeuronSWC seg){
        List<String> result = V_NeuronSWCToMSG(seg);
        String msg = "/drawline_norm:" + username + " HI5 128 128 128;" + String.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg, false);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDelSegSWC(V_NeuronSWC seg){
        List<String> result = V_NeuronSWCToMSG(seg);
        String msg = "/delline_norm:" + username + " HI5 128 128 128;" + String.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg, false);

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateRetypeSegSWC(V_NeuronSWC seg, int type){
        List<String> result = V_NeuronSWCToMSG(seg);
        String msg = "/retypeline_norm:" + username + " HI5 " + type + " 128 128 128;" + String.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg, false);

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
        x/=(ImageMaxRes.x/ImageCurRes.x);
        y/=(ImageMaxRes.y/ImageCurRes.y);
        z/=(ImageMaxRes.z/ImageCurRes.z);
        return new XYZ((float) x, (float) y, (float) z);
    }


    private XYZ ConvertCurrRes2MaxResCoords(double x, double y, double z){
        x*=(ImageMaxRes.x/ImageCurRes.x);
        y*=(ImageMaxRes.y/ImageCurRes.y);
        z*=(ImageMaxRes.z/ImageCurRes.z);
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


    public boolean setSoma(final String msg){

        Log.e(TAG,"msg: " + msg);
        String pattern = "(.*)_x(.*)_y(.*)_z(.*).ano";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(msg);
        if (m.find()){

            System.out.println("Found value: " + m.group(0) );
            System.out.println("Found value: " + m.group(1) );
            System.out.println("Found value: " + m.group(2) );
            System.out.println("Found value: " + m.group(3) );
            System.out.println("Found value: " + m.group(4) );

            String x = m.group(2);
            String y = m.group(3);
            String z = m.group(4);

            ImageMaxRes.x = Float.parseFloat(x);
            ImageMaxRes.y = Float.parseFloat(y);
            ImageMaxRes.z = Float.parseFloat(z);

            Soma = x.split("/.")[0] + ";" + y.split("/.")[0] + ";" + z.split("/.")[0];
            return true;
        }

        return false;
    }

    public static String getSoma(int imgRes){

        int ratio = (int) Math.pow(2, (imgRes) - 1);
        int[] pos = new int[3];
        String[] pos_str = Soma.split(";");
        for (int i = 0; i < pos_str.length; i++){
            pos[i] = (int) (Float.parseFloat(pos_str[i]) / ratio);
            Log.e(TAG,"pos[" + i +"]: " + pos_str[i]);
            Log.e(TAG,"pos[" + i +"]: " + Float.parseFloat(pos_str[i]) / ratio);
            Log.e(TAG,"pos[" + i +"]: " + pos[i]);
        }

        ImageCurRes.x = pos[0];
        ImageCurRes.y = pos[1];
        ImageCurRes.z = pos[2];

        ImageCurPoint.x = pos[0];
        ImageCurPoint.y = pos[1];
        ImageCurPoint.z = pos[2];


        String res = pos[0] + ";" + pos[1] + ";" + pos[2];
        return res;

    }

    public static float[] getSoma(){
        return new float[]{ImageCurRes.x, ImageCurRes.y , ImageCurRes.z};
    }



    public static void setImageStartPoint(float[] point) {

        ImageStartPoint = new XYZ();
        ImageStartPoint.x = point[0] ;
        ImageStartPoint.y = point[1];
        ImageStartPoint.z = point[2];

    }


    public static String getCurrentPos(){
        return String.format("%d;%d;%d;%d", (int) ImageCurPoint.x, (int) ImageCurPoint.y, (int) ImageCurPoint.z, ImgSize);
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

        Log.e(TAG, String.format("img: x %d, y %d, z %d",img_size_x_i, img_size_y_i, img_size_z_i));
        Log.e(TAG, String.format("cur: x %d, y %d, z %d",offset_x_i, offset_y_i, offset_z_i));


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


        ImageCurPoint.x = offset_x_i;
        ImageCurPoint.y = offset_y_i;
        ImageCurPoint.z = offset_z_i;

        ImageStartPoint.x = ImageCurPoint.x - ImgSize/2;
        ImageStartPoint.y = ImageCurPoint.y - ImgSize/2;
        ImageStartPoint.z = ImageCurPoint.z - ImgSize/2;

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + CurRes + ";" + Communicator.getCurrentPos() + ";");


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

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + CurRes + ";" + Communicator.getCurrentPos() + ";");


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

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg("/Imgblock:" + Communicator.BrainNum + ";" + CurRes + ";" + Communicator.getCurrentPos() + ";");

    }



    // msg format:   ImgRes:18454;6;RES(26298x35000x11041);RES(13149x17500x5520);RES(6574x8750x2760);RES(3287x4375x1380);RES(1643x2187x690);RES(821x1093x345)
    public void setResolution(String[] resList){

        resolution = new ArrayList<>();

        for (int i = 0; i < ImgRes; i++){
            resolution.add(resList[i + 2]);
        }

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

    public int getCurRes() {
        return CurRes;
    }

    public void setCurRes(int curRes) {
        CurRes = curRes;
    }

    public int getImgRes() {
        return ImgRes;
    }

    public void setImgRes(int imgRes) {
        ImgRes = imgRes;
    }
}
