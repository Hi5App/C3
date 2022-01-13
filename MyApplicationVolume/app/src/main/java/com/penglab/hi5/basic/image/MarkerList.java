package com.penglab.hi5.basic.image;

import static com.penglab.hi5.core.Myapplication.ToastEasy;

import android.util.Log;

import androidx.annotation.NonNull;

import com.penglab.hi5.core.render.pattern.MyDraw;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MarkerList implements Cloneable {
    private final ArrayList<ImageMarker> markers;

    public MarkerList(){
        markers = new ArrayList<ImageMarker>();
    }

    public MarkerList(ArrayList<ImageMarker> arrayList){
        markers = arrayList;
    }

    public boolean add(ArrayList<ImageMarker> markerArrayList){
        return markers.addAll(markerArrayList);
    }

    public boolean add(ImageMarker marker){
        return markers.add(marker);
    }

    public boolean remove(ImageMarker marker){
        return markers.remove(marker);
    }

    public ImageMarker remove(int i){
        return markers.remove(i);
    }

    public int size(){
        return markers.size();
    }

    public ImageMarker get(int i){
        return markers.get(i);
    }

    public void clear(){
        markers.clear();
    }

    @NonNull
    public MarkerList clone() throws CloneNotSupportedException {
        super.clone();
        ArrayList<ImageMarker> newMarkerArrayList = (ArrayList<ImageMarker>) this.markers.clone();
        return new MarkerList(newMarkerArrayList);
    }

    public ArrayList<ImageMarker> getMarkers(){
        return markers;
    }

    public boolean saveAsApo(String filepath){
        try {
            File f = new File(filepath);
            if (!f.createNewFile()){
                Log.e("MarkerList","Fail to create file !");
                return false;
            }
            FileOutputStream fid = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fid, "UTF-8");

            writer.append("##n,orderinfo,name,comment,z,x,y, pixmax,intensity,sdev,volsize,mass,,,, color_r,color_g,color_b\n");

            for (int i = 0; i < markers.size(); i++){
                ImageMarker s = markers.get(i);
                writer.append(Integer.toString(i)).append(", , ,, ").append(String.format("%.3f", s.x )).append(",")
                        .append(String.format("%.3f", s.y)).append(",").append(String.format("%.3f", s.z)).append(",")
                        .append(", 0.000,0.000,0.000,314.159,0.000,,,");
                float [] color = MyDraw.colormap[s.type % 8];
                for (int j = 0; j < 3; j++){
                    writer.append(",");
                    int c = (int)(color[j] * 255);
                    writer.append(Integer.toString(c));
                }
                writer.append("\n");

            }
            writer.close();
            fid.close();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static JSONArray toJSONArray(MarkerList markerList) throws JSONException {
        if (markerList == null || markerList.size() == 0){
            ToastEasy("Empty markerList");
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<markerList.size(); i++){
            ImageMarker imageMarker = markerList.get(i);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("x", imageMarker.x + 5);
            jsonObject.put("y", imageMarker.y + 5);
            jsonObject.put("z", imageMarker.z + 5);

            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    /* for marker factory mode */
    public static MarkerList parseFromJSONArray(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null){
            ToastEasy("Empty jsonArray");
            return null;
        }

        MarkerList markerList = new MarkerList();
        for (int i=0; i<jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject loc = jsonObject.getJSONObject("loc");
            ImageMarker imageMarker = new ImageMarker(
                    (float) loc.getDouble("x"),
                    (float) loc.getDouble("y"),
                    (float) loc.getDouble("z"));

            markerList.add(imageMarker);
        }
        return markerList;
    }
}
