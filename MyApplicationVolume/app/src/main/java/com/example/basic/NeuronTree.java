package com.example.basic;

import java.util.ArrayList;
import java.util.HashMap;



public class NeuronTree extends BasicSurfObj {
    public ArrayList<NeuronSWC> listNeuron;
    public HashMap<Integer, Integer> hashNeuron;
    public String file;
    public boolean editable;
    public int linemode;

    public NeuronTree(){
        listNeuron.clear();
        hashNeuron.clear();
        file = "";
        editable = false;
        linemode = -1;
    }

    public void deepCopy(NeuronTree p){
        n = p.n;
        color = p.color;
        on = p.on;
        selected = p.selected;
        name = p.name;
        comment = p.comment;

        file = p.file;
        editable = p.editable;
        linemode = p.linemode;
        listNeuron.clear();
        hashNeuron.clear();

        for (int i = 0; i < p.listNeuron.size(); i++){
            NeuronSWC S = new NeuronSWC();
            S.n = p.listNeuron.get(i).n;
            S.type = p.listNeuron.get(i).type;
            S.x = p.listNeuron.get(i).x;
            S.y= p.listNeuron.get(i).y;
            S.z = p.listNeuron.get(i).z;
            S.radius = p.listNeuron.get(i).radius;
            S.parent = p.listNeuron.get(i).parent;
            S.seg_id = p.listNeuron.get(i).seg_id;
            S.level = p.listNeuron.get(i).level;
            S.creatmode = p.listNeuron.get(i).creatmode;  // Creation Mode LMG 8/10/2018
            S.timestamp = p.listNeuron.get(i).timestamp;  // Timestamp LMG 27/9/2018
            S.tfresindex = p.listNeuron.get(i).tfresindex; // TeraFly resolution index LMG 13/12/2018
            S.fea_val = p.listNeuron.get(i).fea_val;
            listNeuron.add(S);
            hashNeuron.put((int)S.n, listNeuron.size()-1);
        }
    }

    public void copy(NeuronTree p){
        n=p.n;
        color=p.color;
        on=p.on;
        selected=p.selected;
        name=p.name;
        comment=p.comment;
        listNeuron = p.listNeuron;
        hashNeuron = p.hashNeuron;
        file     = p.file;
        editable = p.editable;
        linemode = p.linemode;
    }

    public void copyGeometry(NeuronTree p){
        if (listNeuron.size() != p.listNeuron.size()){
            return;
        }

        for (int i = 0; i < listNeuron.size(); i++){
            NeuronSWC S = new NeuronSWC();

            S.x = p.listNeuron.get(i).x;
            S.y= p.listNeuron.get(i).y;
            S.z = p.listNeuron.get(i).z;
            S.radius = p.listNeuron.get(i).radius;

            S.creatmode = p.listNeuron.get(i).creatmode;  // Creation Mode LMG 8/10/2018
            S.timestamp = p.listNeuron.get(i).timestamp;  // Timestamp LMG 27/9/2018
            S.tfresindex = p.listNeuron.get(i).tfresindex; // TeraFly resolution index LMG 13/12/2018

            listNeuron.set(i, S);
        }
    }

    public boolean projection(int axiscode){    //axiscode, 1 -- x, 2 -- y, 3 -- z, 4 -- r
        if (axiscode!=1 && axiscode!=2 && axiscode!=3 && axiscode!=4) return false;
        for (int i =0; i < listNeuron.size(); i++){
            NeuronSWC S = new NeuronSWC();
            S.n = listNeuron.get(i).n;
            S.type = listNeuron.get(i).type;
            S.x = listNeuron.get(i).x;
            S.y= listNeuron.get(i).y;
            S.z = listNeuron.get(i).z;
            S.radius = listNeuron.get(i).radius;
            S.parent = listNeuron.get(i).parent;
            S.seg_id = listNeuron.get(i).seg_id;
            S.level = listNeuron.get(i).level;
            S.creatmode = listNeuron.get(i).creatmode;  // Creation Mode LMG 8/10/2018
            S.timestamp = listNeuron.get(i).timestamp;  // Timestamp LMG 27/9/2018
            S.tfresindex = listNeuron.get(i).tfresindex; // TeraFly resolution index LMG 13/12/2018
            S.fea_val = listNeuron.get(i).fea_val;
            if (axiscode==1) S.x = 0;
            else if (axiscode==2) S.y = 0;
            else if (axiscode==3) S.z = 0;
            else if (axiscode==4) S.radius = 0.5f;
            listNeuron.set(i, S);
        }
        return true;
    }

    public boolean projection(){    //axiscode = 3 when default
        for (int i =0; i < listNeuron.size(); i++){
            NeuronSWC S = new NeuronSWC();
            S.n = listNeuron.get(i).n;
            S.type = listNeuron.get(i).type;
            S.x = listNeuron.get(i).x;
            S.y= listNeuron.get(i).y;
            S.z = 0;
            S.radius = listNeuron.get(i).radius;
            S.parent = listNeuron.get(i).parent;
            S.seg_id = listNeuron.get(i).seg_id;
            S.level = listNeuron.get(i).level;
            S.creatmode = listNeuron.get(i).creatmode;  // Creation Mode LMG 8/10/2018
            S.timestamp = listNeuron.get(i).timestamp;  // Timestamp LMG 27/9/2018
            S.tfresindex = listNeuron.get(i).tfresindex; // TeraFly resolution index LMG 13/12/2018
            S.fea_val = listNeuron.get(i).fea_val;

            listNeuron.set(i, S);
        }
        return true;
    }
}
