package com.example.myapplication__volume.collaboration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.basic.ImageMarker;
import com.example.basic.NeuronTree;
import com.example.basic.XYZ;
import com.example.chat.chatlist.LetterView;
import com.tracingfunc.gd.V_NeuronSWC;
import com.tracingfunc.gd.V_NeuronSWC_unit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.myapplication__volume.MainActivity.username;

public class Communicator {

    /**
     *  TAG when print the log info
     */
    public static final String TAG = "Communicator";

    public static XYZ ImageMaxRes = new XYZ();       // Soma
    public static XYZ ImageCurRes = new XYZ();       // Soma
    public static XYZ ImageStartPoint = new XYZ();


    public static String BrainNum = null;
    public static String ImgRes = null;   // 1 is highest; max num is lowest
    public static String Soma = null;

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


    public void processMsg(byte[] msg){


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
            seg.row.add(segUnit);
        }

        return seg;
    }





    public List<String> V_NeuronSWCToMSG(V_NeuronSWC seg){

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < seg.row.size(); i++){
            V_NeuronSWC_unit curSWCunit = seg.row.get(i);
            Log.e(TAG, "point " + String.format("%f %f %f %f %f %f", curSWCunit.type, curSWCunit.x, curSWCunit.y, curSWCunit.z, curSWCunit.n, curSWCunit.parent));

            XYZ GlobalCroods = ConvertLocalBlocktoGlobalCroods(curSWCunit.x,curSWCunit.y,curSWCunit.z);
//            if (!result.add(String.format("%f %f %f %f",curSWCunit.type, GlobalCroods.x, GlobalCroods.y, GlobalCroods.z))){
            if (!result.add(String.format("%f %f %f %f %f %f", curSWCunit.type, GlobalCroods.x, GlobalCroods.y, GlobalCroods.z, curSWCunit.n, curSWCunit.parent))){
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
            result.add(String.format("%d %f %f %f", marker.type, GlobalCroods.x, GlobalCroods.y, GlobalCroods.z));

            String msg = "/addmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
            msg = msg + String.join(";", result);

            MsgConnector msgConnector = MsgConnector.getInstance();
            msgConnector.sendMsg(msg);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDelMarkerMsg(ImageMarker marker)
    {
        List<String> result = new ArrayList<>();
        XYZ GlobalCroods = ConvertLocalBlocktoGlobalCroods(marker.x,marker.y,marker.z);
        result.add(String.format("%d %f %f %f", marker.type, GlobalCroods.x, GlobalCroods.y, GlobalCroods.z));

        String msg = "/delmarker_norm:" + String.format("%s %s %s %s %s;", username, "HI5", "128", "128", "128");
        msg = msg + String.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateAddSegSWC(V_NeuronSWC seg){
        List<String> result = V_NeuronSWCToMSG(seg);
        String msg = "/drawline_norm:" + username + " HI5 128 128 128;" + String.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDelSegSWC(V_NeuronSWC seg){
        List<String> result = V_NeuronSWCToMSG(seg);
        String msg = "/delline_norm:" + username + " HI5 128 128 128;" + String.join(";", result);

        MsgConnector msgConnector = MsgConnector.getInstance();
        msgConnector.sendMsg(msg);

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
        node.x -=(ImageStartPoint.x-1);
        node.y -=(ImageStartPoint.y-1);
        node.z -=(ImageStartPoint.z-1);
        Log.d(TAG,"ConvertGlobaltoLocalBlockCroods x y z = " + x + " " + y + " " + z + " -> " + XYZ2String(node));
        return node;
    }

    public XYZ ConvertLocalBlocktoGlobalCroods(double x,double y,double z)
    {
        x +=(ImageStartPoint.x-1);
        y +=(ImageStartPoint.y-1);
        z +=(ImageStartPoint.z-1);
        XYZ node = ConvertCurrRes2MaxResCoords(x,y,z);
        Log.d(TAG,"ConvertLocalBlocktoGlobalCroods x y z = " + x + " " + y + " " + z + " -> " + XYZ2String(node));
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

    public static String getSoma(String imgRes){

        int ratio = (int) Math.pow(2, Integer.parseInt(imgRes) - 1);
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


    public ArrayList<ArrayList<Float>> apoConvert(ArrayList<ArrayList<Float>> apo){

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

    public NeuronTree ConvertNeuronTree(NeuronTree nt){

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

}
