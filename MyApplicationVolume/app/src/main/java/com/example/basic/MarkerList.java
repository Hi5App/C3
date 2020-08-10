package com.example.basic;

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
        ArrayList<ImageMarker> new_markers = (ArrayList<ImageMarker>)markers.clone();
        MarkerList new_imageMaker = new MarkerList(new_markers);

        return new_imageMaker;
    }

    public ArrayList<ImageMarker> getMarkers(){
        return markers;
    }
}
