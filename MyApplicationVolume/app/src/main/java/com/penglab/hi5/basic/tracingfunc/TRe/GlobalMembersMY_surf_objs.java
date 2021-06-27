package com.penglab.hi5.basic.tracingfunc.TRe;
import com.penglab.hi5.basic.NeuronSWC;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.tracingfunc.app2.MyMarker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class GlobalMembersMY_surf_objs
{


    ///#define MidMarker(m1, m2) MyMarker(((m1).x + (m2).x)/2.0,((m1).y + (m2).y)/2.0,((m1).z + (m2).z)/2.0)


    //public static boolean readMarker_file(String markerfile, Vector<MyMarker> markers)
   /* {
        File file=new File(markerfile);
        System.out.println("in read marker");

        try {
            File f = new File(markerfile);
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
            System.out.println("markerReaderException" + e.getMessage());
        }

    }*/

    public static boolean saveMarker_file(String markerfile, Vector<MyMarker> outmarkers, Vector<String> infostring)
    {
        System.out.println("marker num = "+outmarkers.size()+", save  file to "+markerfile);
        Map<MyMarker,Integer> ind = new HashMap<MyMarker,Integer>();
        try {
            File f = new File(markerfile);
            FileOutputStream fid = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fid, "UTF-8");
            writer.append("#x,y,z,radius\n");
            for(int i=0; i<outmarkers.size(); i++){
                ind.put(outmarkers.elementAt(i),i+1);
            }
            for(int i=0; i<outmarkers.size(); i++){
                MyMarker marker = outmarkers.elementAt(i);
                if(marker.x>500){
                    System.out.println("index i: "+i+" "+marker.x);
                }
                writer.append(Double.toString(marker.x)).append("1.0").append(Double.toString(marker.y))
                        .append("1.0 ").append(Double.toString(marker.z)).append("1.0 ").append(Double.toString(marker.radius))
                        .append("\n");
            }
            writer.close();
            fid.close();

        } catch (IOException e) {
            System.out.println("saveSWC Exception "+e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean readSWC_file(String swcfile, ArrayList<MyMarker> outmarkers)
    {
        ArrayList<String> arraylist = new ArrayList<String>();
        NeuronTree nt = new NeuronTree();
        nt.file = swcfile;
        System.out.println("in read swc");

        try {
            File f = new File(swcfile);
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
            return false;

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

        return true;
    }

    //public static boolean saveSWC_file_pn(String swcfile, Vector<MyMarker> outmarkers, int pn)
   /* {
        System.out.println("marker num = "+outmarkers.size()+", save swc file to "+swcfile);
        Map<MyMarker,Integer> ind = new HashMap<MyMarker,Integer>();
        try {
            File f = new File(swcfile);
            FileOutputStream fid = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fid, "UTF-8");
            writer.append("#name \n");
            writer.append("#comment \n");
            writer.append("##n,type,x,y,z,radius,parent\n");
            for(int i=0; i<outmarkers.size(); i++){
                ind.put(outmarkers.elementAt(i),i+1);
            }
            for(int i=0; i<outmarkers.size(); i++){
                MyMarker marker = outmarkers.elementAt(i);
                if(marker.x>500){
                    System.out.println("index i: "+i+" "+marker.x);
                }
                int parent_id;
                if(marker.parent == null)
                    parent_id = -1;
                else
                    parent_id = ind.get(marker.parent);
                writer.append(Integer.toString(i+1)).append(" ").append(Integer.toString(marker.type))
                        .append(" ").append(Double.toString(marker.x)).append(" ").append(Double.toString(marker.y))
                        .append(" ").append(Double.toString(marker.z)).append(" ").append(Double.toString(marker.radius))
                        .append(" ").append(Integer.toString(parent_id)).append("\n");
            }
            writer.close();
            fid.close();

        } catch (IOException e) {
            System.out.println("saveSWC Exception "+e.getMessage());
            return false;
        }
        return true;
    }*/

    //class NeuronSWC; //from vaa3d's basic_surf_objs.h // added by PHC, 2013-01-03
    public static boolean saveSWC_file(String swcfile, Vector<MyMarker> outmarkers, Vector<String> infostring){
        System.out.println("marker num = "+outmarkers.size()+", save swc file to "+swcfile);
        Map<MyMarker,Integer> ind = new HashMap<MyMarker,Integer>();
        try {
            File f = new File(swcfile);
            FileOutputStream fid = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fid, "UTF-8");
            writer.append("#name \n");
            writer.append("#comment \n");
            writer.append("##n,type,x,y,z,radius,parent\n");
            for(int i=0; i<outmarkers.size(); i++){
                ind.put(outmarkers.elementAt(i),i+1);
            }
            for(int i=0; i<outmarkers.size(); i++){
                MyMarker marker = outmarkers.elementAt(i);
                if(marker.x>500){
                    System.out.println("index i: "+i+" "+marker.x);
                }
                int parent_id;
                if(marker.parent == null)
                    parent_id = -1;
                else
                    parent_id = ind.get(marker.parent);
                writer.append(Integer.toString(i+1)).append(" ")./*append(Integer.toString(marker.type))
                        .*/append(" ").append(Double.toString(marker.x)).append(" ").append(Double.toString(marker.y))
                        .append(" ").append(Double.toString(marker.z)).append(" ").append(Double.toString(marker.radius))
                        .append(" ").append(Integer.toString(parent_id)).append("\n");
            }
            writer.close();
            fid.close();

        } catch (IOException e) {
            System.out.println("saveSWC Exception "+e.getMessage());
            return false;
        }
        return true;
    }

    //public static boolean saveDot_file(String dot_file, Vector<MyMarker> outmarkers)
    /*{
        System.out.print("marker num = ");
        System.out.print(outmarkers.size());
        System.out.print(", save swc file to ");
        System.out.print(dot_file);
        System.out.print("\n");
        HashMap<MyMarker, Integer> ind = new HashMap<MyMarker, Integer>();
        ofstream ofs = new ofstream(dot_file);

        if (ofs.fail())
        {
            System.out.print("open swc file error");
            System.out.print("\n");
            return false;
        }
        ofs << "digraph \"" << dot_file << "\" {" << "\n";
        ofs << "\trankdir = BT;" << "\n";

        for (int i = 0; i < outmarkers.size(); i++)
            ind.put(outmarkers.get(i), i + 1);
        for (int i = 0; i < outmarkers.size(); i++)
        {
            MyMarker marker = outmarkers.get(i);
            if (marker.parent != null)
            {
                int parent_id = ind.get(marker.parent);
                MyMarker parent = marker.parent;
                ofs << "\t" << i + 1 << " -> " << parent_id << ";" << "\n";
            }
        }
        ofs << "}" << "\n";
        ofs.close();
        return true;
    }*/

    /*public static boolean readESWC_file(String swc_file, ArrayList<MyMarkerX> swc)
    {

    }*/
    public static boolean saveESWC_file(String swcfile, Vector<MyMarker> outmarkers, Vector<String> infostring)
    {
        System.out.println("marker num = "+outmarkers.size()+", save swc file to "+swcfile);
        Map<MyMarker,Integer> ind = new HashMap<MyMarker,Integer>();
        try {
            File f = new File(swcfile);
            FileOutputStream fid = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fid, "UTF-8");
            writer.append("#name \n");
            writer.append("#comment \n");
            writer.append("##n,type,x,y,z,radius,parent\n");
            for(int i=0; i<outmarkers.size(); i++){
                ind.put(outmarkers.elementAt(i),i+1);
            }
            for(int i=0; i<outmarkers.size(); i++){
                MyMarker marker = outmarkers.elementAt(i);
                if(marker.x>500){
                    System.out.println("index i: "+i+" "+marker.x);
                }
                int parent_id;
                if(marker.parent == null)
                    parent_id = -1;
                else
                    parent_id = ind.get(marker.parent);
                writer.append(Integer.toString(i+1)).append(" ")./*append(Integer.toString(marker.type))*/append(" ").append(Double.toString(marker.x)).append(" ").append(Double.toString(marker.y))
                        .append(" ").append(Double.toString(marker.z)).append(" ").append(Double.toString(marker.radius))
                        .append(" ").append(Integer.toString(parent_id)).append("\n");
            }
            writer.close();
            fid.close();

        } catch (IOException e) {
            System.out.println("saveSWC Exception "+e.getMessage());
            return false;
        }
        return true;
    }
    ///#endif

    public static double dist(MyMarker a, MyMarker b)
    {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z));
    }

    //public static ArrayList<MyMarker> getLeaf_markers(ArrayList<MyMarker> inmarkers)
    /*{
        HashSet<MyMarker> par_markers = new HashSet<MyMarker>();
        ArrayList<MyMarker> leaf_markers = new ArrayList<MyMarker>();
        for (int i = 0; i < inmarkers.size(); i++)
        {
            MyMarker marker = inmarkers.get(i);
            if (marker.parent != null)
                par_markers.add(marker.parent);
        }
        for (int i = 0; i < inmarkers.size(); i++)
        {
            if (par_markers.find(inmarkers.get(i)) == par_markers.end())
                leaf_markers.add(inmarkers.get(i));
        }
        par_markers.clear();
        return leaf_markers;
    }*/
   // public static ArrayList<MyMarker> getLeaf_markers(ArrayList<MyMarker> inmarkers, HashMap<MyMarker , Integer> childs_num)
   /* {
        for (int i = 0; i < inmarkers.size(); i++)
            childs_num.put(inmarkers.get(i), 0);

        ArrayList<MyMarker> leaf_markers = new ArrayList<MyMarker>();
        for (int i = 0; i < inmarkers.size(); i++)
        {
            MyMarker marker = inmarkers.get(i);
            MyMarker parent = marker.parent;
            if (parent != null)
                childs_num.get(parent)++;
        }
        for (int i = 0; i < inmarkers.size(); i++)
        {
            if (childs_num.get(inmarkers.get(i)) == 0)
                leaf_markers.add(inmarkers.get(i));
        }
        return leaf_markers;
    }*/
    ///#endif
}