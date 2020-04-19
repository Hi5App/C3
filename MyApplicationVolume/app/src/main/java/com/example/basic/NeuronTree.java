package com.example.basic;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import com.example.myapplication__volume.MainActivity;
import com.tracingfunc.gd.V_NeuronSWC;
import com.tracingfunc.gd.V_NeuronSWC_list;
import com.tracingfunc.gd.V_NeuronSWC_unit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import static com.example.myapplication__volume.MainActivity.getContext;



public class NeuronTree extends BasicSurfObj {
    public ArrayList<NeuronSWC> listNeuron = new ArrayList<>();
    public HashMap<Integer, Integer> hashNeuron = new HashMap<>();
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

    public boolean writeSWC_file(String swcfile){
        System.out.println("point num = "+this.listNeuron.size()+", save swc file to "+swcfile);
        try {
            File f = new File(swcfile);
            FileOutputStream fid = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fid, "UTF-8");
            writer.append("#name \n");
            writer.append("#comment \n");
            writer.append("##n,type,x,y,z,radius,parent\n");
            for(int i=0; i<this.listNeuron.size(); i++){
                NeuronSWC s = this.listNeuron.get(i);
                writer.append(Long.toString(s.n)).append(" ").append(Integer.toString(s.type))
                        .append(" ").append(Double.toString(s.x)).append(" ").append(Double.toString(s.y))
                        .append(" ").append(Double.toString(s.z)).append(" ").append(Double.toString(s.radius))
                        .append(" ").append(Long.toString(s.parent)).append("\n");
            }
            writer.close();
            fid.close();

        } catch (IOException e) {
            System.out.println("saveSWC Exception "+e.getMessage());
            return false;
        }
        System.out.println("done with saving file: "+swcfile);
        return true;
    }

    public static NeuronTree readSWC_file(String filename) {
        ArrayList<String> arraylist = new ArrayList<String>();
        NeuronTree nt = new NeuronTree();
        nt.file = filename;
        System.out.println("in read swc");
        // if (! qf.open(QIODevice::ReadOnly | QIODevice::Text))
        // {
        // #ifndef DISABLE_V3D_MSG
        // v3d_msg(QString("open file [%1] failed!").arg(filename));
        // #endif
        // return nt;
        // }
        try {
            File f = new File(filename);
            FileInputStream fid = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);
            String str;
            while ((str = br.readLine()) != null) {
                arraylist.add(str);
            }
            br.close();
            isr.close();
        } catch (Exception e) {
            System.out.println("SwcReaderException" + e.getMessage());
        }
        ArrayList<NeuronSWC> listNeuron = new ArrayList<>();
        HashMap<Integer, Integer> hashNeuron = new HashMap<>();
        listNeuron.clear();
        hashNeuron.clear();
        // String name = "";
        // String comment = "";
        for (int i = 0; i < arraylist.size(); i++) {
            NeuronSWC S = new NeuronSWC();
            String current = arraylist.get(i);
            String[] s = current.split(" ");
            if (s[0].substring(0, 1).equals("#")) continue;
            for (int j = 0; j < 7; j++) {
                if (j == 0)
                    S.n = Integer.parseInt(s[j]);
                else if (j == 1)
                    S.type = Integer.parseInt(s[j]);
                else if (j == 2)
                    S.x = Float.parseFloat(s[j]);
                else if (j == 3)
                    S.y = Float.parseFloat(s[j]);
                else if (j == 4)
                    S.z = Float.parseFloat(s[j]);
                else if (j == 5)
                    S.radius = Float.parseFloat(s[j]);
                else if (j == 6)
                    S.parent = Integer.parseInt(s[j]);
                    // the ESWC extension, by PHC, 20120217
                else if (j == 7)
                    S.seg_id = Integer.parseInt(s[j]);
                else if (j == 8)
                    S.level = Integer.parseInt(s[j]);
                else if (j == 9)
                    S.creatmode = Integer.parseInt(s[j]);
                else if (j == 10)
                    S.timestamp = Integer.parseInt(s[j]);
                else if (j == 11)
                    S.tfresindex = Integer.parseInt(s[j]);
                    // change ESWC format to adapt to flexible feature number, by WYN, 20150602
                else
                    S.fea_val.add(Float.parseFloat(s[j]));
            }

            // if (! listNeuron.contains(S)) // 081024
            {
                listNeuron.add(S);
                hashNeuron.put((int) (S.nodeinseg_id), listNeuron.size() - 1);
            }
        }

        if (listNeuron.size() < 1)
            return nt;

        // now update other NeuronTree members

        // nt.n = 1; //only one neuron if read from a file
        nt.listNeuron = listNeuron;
        nt.hashNeuron = hashNeuron;
        // nt.color = XYZW(0,0,0,0); /// alpha==0 means using default neuron color,
        // 081115
        // nt.on = true;
        // nt.name = name.remove('\n'); if (nt.name.isEmpty()) nt.name =
        // QFileInfo(filename).baseName();
        // nt.comment = comment.remove('\n');

        return nt;
    }


    public static NeuronTree readSWC_file(Uri uri) {
        Context context = getContext();

        ArrayList<String> arraylist = new ArrayList<String>();
        NeuronTree nt = new NeuronTree();
        nt.file = uri.toString();
        String file_type = nt.file.substring(nt.file.lastIndexOf(".")).toUpperCase();
        if (!(file_type.equals(".SWC") | file_type.equals(".ESWC"))) {
            Toast.makeText(MainActivity.getContext(), "failed, only support swc or eswc file", Toast.LENGTH_LONG).show();
            return null;
        }
        try {

            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");

            InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(parcelFileDescriptor);
            FileInputStream fid = (FileInputStream) (is);
            InputStreamReader isr = new InputStreamReader(fid);
            BufferedReader br = new BufferedReader(isr);
            String str;
            while ((str = br.readLine()) != null) {
                arraylist.add(str);
            }
            br.close();
            isr.close();
        } catch (Exception e) {
            System.out.println("SwcReaderException" + e.getMessage());
        }
        ArrayList<NeuronSWC> listNeuron = new ArrayList<>();
        HashMap<Integer, Integer> hashNeuron = new HashMap<>();
        listNeuron.clear();
        hashNeuron.clear();
        // String name = "";
        // String comment = "";

        for (int i = 0; i < arraylist.size(); i++) {
            NeuronSWC S = new NeuronSWC();
            String current = arraylist.get(i);
            String[] s = current.split("\\s+");
            if (s[0].substring(0, 1).equals("#")) continue;
            try {
                for (int j = 0; j < 7; j++) {
                    if (j == 0)
                        S.n = Integer.parseInt(s[j]);
                    else if (j == 1)
                        S.type = Integer.parseInt(s[j]);
                    else if (j == 2)
                        S.x = Float.parseFloat(s[j]);
                    else if (j == 3)
                        S.y = Float.parseFloat(s[j]);
                    else if (j == 4)
                        S.z = Float.parseFloat(s[j]);
                    else if (j == 5)
                        S.radius = Float.parseFloat(s[j]);
                    else if (j == 6)
                        S.parent = Integer.parseInt(s[j]);
                        // the ESWC extension, by PHC, 20120217
                    else if (j == 7)
                        S.seg_id = Integer.parseInt(s[j]);
                    else if (j == 8)
                        S.level = Integer.parseInt(s[j]);
                    else if (j == 9)
                        S.creatmode = Integer.parseInt(s[j]);
                    else if (j == 10)
                        S.timestamp = Integer.parseInt(s[j]);
                    else if (j == 11)
                        S.tfresindex = Integer.parseInt(s[j]);
                        // change ESWC format to adapt to flexible feature number, by WYN, 20150602
                    else
                        S.fea_val.add(Float.parseFloat(s[j]));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.getContext(), "failed, invalid swc/eswc file", Toast.LENGTH_LONG).show();
                return null;
            }
            // if (! listNeuron.contains(S)) // 081024
            {
                listNeuron.add(S);
                hashNeuron.put((int) (S.nodeinseg_id), listNeuron.size() - 1);
            }
        }

        if (listNeuron.size() < 1)
            return nt;

        // now update other NeuronTree members
        // nt.n = 1; //only one neuron if read from a file
        nt.listNeuron = listNeuron;
        nt.hashNeuron = hashNeuron;
        // nt.color = XYZW(0,0,0,0); /// alpha==0 means using default neuron color,
        // 081115
        // nt.on = true;
        // nt.name = name.remove('\n'); if (nt.name.isEmpty()) nt.name =
        // QFileInfo(filename).baseName();
        // nt.comment = comment.remove('\n');

        return nt;
    }

    public V_NeuronSWC convertV_NeuronSWCFormat()
    {
        NeuronTree SS = new NeuronTree();

        V_NeuronSWC seg = new V_NeuronSWC();
        seg.name = this.name;
        seg.file = this.file;

        Vector<V_NeuronSWC_unit> row = new Vector<>();
        row.clear();

        for(int i=0; i<this.listNeuron.size(); i++){
           NeuronSWC S = this.listNeuron.get(i);
           V_NeuronSWC_unit u = new V_NeuronSWC_unit();
           u.n = S.n;
           u.type = S.type;
           u.x = S.x;
           u.y = S.y;
           u.z = S.z;
           u.r = S.radius;
           u.parent = S.parent;
           u.seg_id = S.seg_id;
           u.nodeinseg_id = S.nodeinseg_id;
           row.add(u);
        }
        seg.row = row;
        seg.on = true;
        seg.color_uc[0] = this.color.r;
        seg.color_uc[1] = this.color.g;
        seg.color_uc[2] = this.color.b;
        seg.color_uc[3] = this.color.a;

        return seg;
    }

    public Vector<V_NeuronSWC> devideByBranch(){
        Vector<V_NeuronSWC> result = new Vector<V_NeuronSWC>();
        Vector<Integer> roots = new Vector<Integer>();
        for (int i = 0; i < listNeuron.size(); i++){
            NeuronSWC temp = listNeuron.get(i);
            temp.children.clear();
            listNeuron.set(i, temp);
        }
        for (int i = 0; i < listNeuron.size(); i++){

            NeuronSWC temp = listNeuron.get(i);
            int prt = (int)temp.parent;
            if (prt != -1) {
                NeuronSWC ptemp = listNeuron.get(hashNeuron.get(prt));
                ptemp.children.add(i);
                listNeuron.set(hashNeuron.get(prt), temp);
            }else{
                roots.add(i);
            }
        }
        for (int i = 0; i < roots.size(); i++){
            V_NeuronSWC empty = new V_NeuronSWC();

            NeuronSWC S = listNeuron.get(roots.get(i));
            V_NeuronSWC_unit u = new V_NeuronSWC_unit();
            u.n = S.n;
            u.type = S.type;
            u.x = S.x;
            u.y = S.y;
            u.z = S.z;
            u.r = S.radius;
            u.parent = S.parent;
            u.seg_id = S.seg_id;
            u.nodeinseg_id = S.nodeinseg_id;
            empty.append(u);

            result.add(empty);
            result = treeToList(roots.get(i), result);
        }
        return result;
    }

    private Vector<V_NeuronSWC> treeToList(int root, Vector<V_NeuronSWC> current){

        NeuronSWC p = listNeuron.get(root);
        if (p.children.size() == 1){
            V_NeuronSWC temp = current.lastElement();
            NeuronSWC S = listNeuron.get(p.children.get(0));
            V_NeuronSWC_unit u = new V_NeuronSWC_unit();
            u.n = S.n;
            u.type = S.type;
            u.x = S.x;
            u.y = S.y;
            u.z = S.z;
            u.r = S.radius;
            u.parent = S.parent;
            u.seg_id = S.seg_id;
            u.nodeinseg_id = S.nodeinseg_id;
            temp.append(u);
            current.set(current.size() - 1, temp);
            return treeToList(p.children.get(0), current);
        }else if (p.children.size() == 0){
            return current;
        }
        for (int i = 0; i < p.children.size(); i++){
            V_NeuronSWC empty = new V_NeuronSWC();

            NeuronSWC S = listNeuron.get(p.children.get(i));
            V_NeuronSWC_unit u = new V_NeuronSWC_unit();
            u.n = S.n;
            u.type = S.type;
            u.x = S.x;
            u.y = S.y;
            u.z = S.z;
            u.r = S.radius;
            u.parent = S.parent;
            u.seg_id = S.seg_id;
            u.nodeinseg_id = S.nodeinseg_id;
            empty.append(u);

            current.add(empty);
            current = treeToList(p.children.get(i), current);
        }
        return current;
    }


}
