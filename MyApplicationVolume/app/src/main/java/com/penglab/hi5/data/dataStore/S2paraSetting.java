package com.penglab.hi5.data.dataStore;

import android.content.Context;
import android.content.SharedPreferences;

public class S2paraSetting {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private Context mContext;

    public S2paraSetting(Context context){
        mContext = context;
        pref = mContext.getSharedPreferences("s2initialization",Context.MODE_PRIVATE);
    }

    public void setPara(boolean Connect_ServerMode,boolean Connect_ScopeMode, boolean Smart_ControlMode, int ParaXY, int ParaZ){
        editor = pref.edit();
        editor.putBoolean("Connect_ServerMode",Connect_ServerMode);
        editor.putBoolean("Connect_ScopeMode",Connect_ScopeMode);
        editor.putBoolean("Smart_ControlMode",Smart_ControlMode);
        editor.putInt("ParaXY",ParaXY);
        editor.putInt("ParaZ",ParaZ);
        editor.apply();

    }

    public void setTag(String img_score, String swc_score,String id ,String name,String note){
        editor = pref.edit();

        editor.putString("image_quality_score",img_score);
        editor.putString("swc_quality_score",swc_score);
        editor.putString("user_id",id);
        editor.putString("file_name",name);
        editor.putString("notes",note);
        editor.apply();

    }

    public boolean getConnect_ServerMode(){
        return pref.getBoolean("Connect_ServerMode",false);
    }

    public String getAllTags()
    {
        String alltags="";



        int i=0;
        String[] list = new String[]{"image_quality_score","swc_quality_score","user_id","file_name","notes"};
        String[] strings = new String[list.length];
        for(i=0;i<list.length;i++)
        {

            strings[i]=pref.getString(list[i],String.valueOf(0));

        }
        alltags=doJoin(strings);

        return alltags;
    }

    public String doJoin(String[] strings) {
        String result= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            result = String.join(";;", strings);
        }
        return result;
    }

    public int getImage_quality_score(){
        return pref.getInt("image_quality_score",0);
    }
    public int getSwc_quality_score(){
        return pref.getInt("swc_quality_score",0);
    }
    public String getUser_id(){
        return pref.getString("user_id", String.valueOf(0));
    }
    public String getFile_name(){
        return pref.getString("file_name",String.valueOf(0));
    }
    public String getNotes(){
        return pref.getString("notes",String.valueOf(0));
    }

    public boolean getConnect_ScopeMode(){
        return pref.getBoolean("Connect_ScopeMode",false);
    }

    public boolean getSmart_ControlMode(){
        return pref.getBoolean("Smart_ControlMode",false);
    }

    public int getParaXY(){
        return pref.getInt("ParaXY",0);
    }

    public int getParaZ(){
        return pref.getInt("ParaZ",0);
    }
}
