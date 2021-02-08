package com.example.myapplication__volume.collaboration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.basic.XYZ;
import com.example.chat.chatlist.LetterView;
import com.tracingfunc.gd.V_NeuronSWC;
import com.tracingfunc.gd.V_NeuronSWC_unit;

import java.util.ArrayList;
import java.util.List;

public class Communicator {

    /**
     *  TAG when print the log info
     */
    public static final String TAG = "Communicator";

    public static XYZ ImageMaxRes;
    public static XYZ ImageCurRes;
    public static XYZ ImageStartPoint;

    /**
     * current context
     */
    private Context mContext;

    public static  Communicator INSTANCE;

    private Communicator(){

    }


    /**
     * 初始化，注册Context对象
     * @param ctx
     */
    public void init(Context ctx){
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




    public V_NeuronSWC MSGToV_NeuronSWC(String msg){

        V_NeuronSWC seg = new V_NeuronSWC();

        String[] swc = msg.split(";");
        for (int i = 0; i < swc.length; i++){
            V_NeuronSWC_unit swcUnit = new V_NeuronSWC_unit();

        }

        return seg;
    }





    public List<String> V_NeuronSWCToMSG(V_NeuronSWC seg){

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < seg.row.size(); i++){
            V_NeuronSWC_unit curSWCunit = seg.row.get(i);
            XYZ GlobalCroods = ConvertLocalBlocktoGlobalCroods(curSWCunit.x,curSWCunit.y,curSWCunit.z);
            if (!result.add(String.format("%f %f %f %f",curSWCunit.type, GlobalCroods.x, GlobalCroods.y, GlobalCroods.z))){
                Log.e(TAG, "Something wrong when convert V_NeuronSWC to MSG");
            }
        }

        return result;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateSWC(V_NeuronSWC seg){
        List<String> result = V_NeuronSWCToMSG(seg);
        String msg = String.join(";", result);

        ServerConnector serverConnector = ServerConnector.getInstance();
        serverConnector.sendMsg(msg);

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

}
