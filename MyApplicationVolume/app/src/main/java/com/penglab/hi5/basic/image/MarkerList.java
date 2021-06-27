package com.penglab.hi5.basic.image;

import android.util.Log;

import com.penglab.hi5.core.render.MyDraw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MarkerList implements Cloneable {
    private ArrayList<ImageMarker> markers;

    public MarkerList(){
        markers = new ArrayList<ImageMarker>();
    }

    public MarkerList(ArrayList<ImageMarker> arrayList){
        markers = arrayList;
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

    public MarkerList clone() throws CloneNotSupportedException {
        super.clone();
        ArrayList<ImageMarker> new_markers = (ArrayList<ImageMarker>)markers.clone();
        MarkerList new_imageMaker = new MarkerList(new_markers);

        return new_imageMaker;
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
}
