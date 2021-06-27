package com.penglab.hi5.basic.tracingfunc.gd;

import android.opengl.Matrix;
import android.util.Log;

import androidx.annotation.NonNull;

import com.penglab.hi5.basic.NeuronSWC;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.basic.image.RGBA8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class V_NeuronSWC_list implements Cloneable{
    public Vector<V_NeuronSWC> seg; //since each seg could be a complete neuron or multiple paths, thus I call it "seg", but not "path"
    public int last_seg_num; //?? for what purpose? seems only used once in v3d_core.cpp. Questioned by Hanchuan, 20100210
    public String name;
    public String comment;
    public String file;
    public int[] color_uc;
    public boolean b_traced;

    public V_NeuronSWC_list() {
        last_seg_num=-1; color_uc=new int[4];
        for(int i=0; i<color_uc.length; i++){
            color_uc[i] = 0;
        }
        b_traced=true;
        seg = new Vector<V_NeuronSWC>();
    }

    @NonNull
    @Override
    public V_NeuronSWC_list clone() throws CloneNotSupportedException {
        V_NeuronSWC_list t = null;
        t = (V_NeuronSWC_list) super.clone();
        t.seg = new Vector<V_NeuronSWC>();

        for(int i=0; i<this.seg.size(); i++){
            t.seg.add(this.seg.get(i).clone());
        }
        t.color_uc = new int[this.color_uc.length];
        for(int i=0; i<this.color_uc.length; i++){
            t.color_uc[i] = this.color_uc[i];
        }
        return t;
    }

    public int nsegs() {return seg.size();}
    public int nrows() {int n=0; for (int i=0;i<seg.size();i++) n+=(int)seg.elementAt(i).nrows(); return n;}
    public int maxnoden()
    {
        int max_n=0;	for (int i=0;i<seg.size();i++) if (seg.elementAt(i).maxnoden() > max_n) max_n = (int)seg.elementAt(i).maxnoden();	return max_n;
    }
    public boolean isJointed() {return nsegs()==1 && seg.elementAt(0).b_jointed;}

    public void append(V_NeuronSWC  new_seg) {seg.add(new_seg); last_seg_num=seg.size();}
    public void append(Vector<V_NeuronSWC>  new_segs) throws Exception
    {
        for (int k=0; k<new_segs.size(); k++)
            seg.add(new_segs.elementAt(k).clone());
        last_seg_num=seg.size();
    }
    public void clear() {last_seg_num=seg.size(); seg.clear();}
    // void merge();
    // void decompose();
    // boolean reverse();
    // boolean split(int seg_id, int nodeinseg_id);
    public boolean deleteSeg(int seg_id){
        System.out.println("delete id "+seg_id);
        if(seg_id>=0 && seg_id<this.nsegs()){
            this.seg.remove(seg_id);
            return true;
        }else {
            return false;
        }
    }

    public boolean deleteMutiSeg(Vector<Integer> seg_ids){
        if(seg_ids.size()>0){
            for(int i=0; i<seg_ids.size(); i++){
                int seg_id = seg_ids.get(i);
                if(seg_id>=0 && seg_id<this.nsegs()){
                    this.seg.get(seg_id).to_be_deleted = true;
                }else {
                    return false;
                }
            }
        }
//        for(V_NeuronSWC seg : this.seg){
//            if(seg.to_be_deleted){
//                this.seg.remove(seg);
//            }
//        }
        for(int i=this.seg.size()-1; i>=0; i--){
            V_NeuronSWC seg = this.seg.get(i);
            if(seg.to_be_deleted){
                this.seg.remove(seg);
            }
        }
        return true;
    }

    // // @ADDED by Alessandro on 2015-05-08. Needed to support late delete of multiple neuron segments.
    // void                                            // no value returned
    //     deleteMultiSeg(                             // by default, deletes neuron segments having 'to_be_deleted' field set to 'true'
    //         std::vector <int> *seg_ids = 0);    // if provided, deletes the corresponding neuron segments.

    public static V_NeuronSWC merge_V_NeuronSWC_list(V_NeuronSWC_list  in_swc_list) throws Exception
    {
        V_NeuronSWC out_swc = new V_NeuronSWC();  out_swc.clear();
        V_NeuronSWC_unit v = new V_NeuronSWC_unit();
        int n=0, i,j,k;
        int nsegs = in_swc_list.seg.size();
        for (k=0;k<nsegs;k++)
        {
            if(in_swc_list.seg.elementAt(k).to_be_deleted)
                continue;

            if(!in_swc_list.seg.elementAt(k).on)
                continue;

            Vector <V_NeuronSWC_unit> row = in_swc_list.seg.elementAt(k).row;
            if (row.size()<=0) continue;

            //first find the min index number, then all index will be automatically adjusted
            int min_ind = (int)row.elementAt(0).n;
            for (j=1;j<row.size();j++)
            {
                if (row.elementAt(j).n < min_ind)  min_ind = (int)row.elementAt(j).n;
                if (min_ind<0) System.out.println("Found illegal neuron node index which is less than 0 in merge_V_NeuronSWC_list()!");
            }
            //qDebug()<<min_ind;

            // segment id & color type
            int seg_id = k;

            //now merge
            int n0=n;
            for (j=0;j<row.size();j++)
            {
                v.seg_id = seg_id;
                v.nodeinseg_id = j;
                v.level = row.elementAt(j).level;
                v.creatmode = row.elementAt(j).creatmode;
                v.timestamp = row.elementAt(j).timestamp;
                v.tfresindex = row.elementAt(j).tfresindex;
                v.n = (n0+1) + row.elementAt(j).n-min_ind;
//                for (i=1;i<=5;i++)	v.data[i] = row.elementAt(j).data[i];
                v.x = row.elementAt(j).x;
                v.y = row.elementAt(j).y;
                v.z = row.elementAt(j).z;
                v.type = row.elementAt(j).type;
                v.r = row.elementAt(j).r;

                v.parent = (row.elementAt(j).parent<0)? -1 : ((n0+1) + row.elementAt(j).parent-min_ind); //change row[j].parent<=0 to row[j].parent<0, PHC 091123.

                //qDebug()<<row[j].n<<"->"<<v.n<<" "<<row[j].parent<<"->"<<v.parent<<" "<<n;

                out_swc.row.add(v.clone());
                n++;
            }
        }
        out_swc.color_uc[0] = in_swc_list.color_uc[0];
        out_swc.color_uc[1] = in_swc_list.color_uc[1];
        out_swc.color_uc[2] = in_swc_list.color_uc[2];
        out_swc.color_uc[3] = in_swc_list.color_uc[3];

        out_swc.name = "merged";
        //	for (i=0;i<out_swc.nrows();i++)
        //		qDebug()<<out_swc.row.at(i).data[2]<<" "<<out_swc.row.at(i).data[3]<<" "<<out_swc.row.at(i).data[4]<<" "<<out_swc.row.at(i).data[6];

        return out_swc;
    }

    public static NeuronTree convertNeuronTreeFormat(V_NeuronSWC_list  tracedNeuron) throws Exception
    {
        NeuronTree SS = new NeuronTree();

        //first conversion

        V_NeuronSWC seg = V_NeuronSWC_list.merge_V_NeuronSWC_list(tracedNeuron);
        seg.name = tracedNeuron.name;
        seg.file = tracedNeuron.file;

        //second conversion

        ArrayList<NeuronSWC> listNeuron = new ArrayList<NeuronSWC>();
        HashMap<Integer, Integer> hashNeuron = new HashMap<Integer, Integer>();
        listNeuron.clear();
        hashNeuron.clear();

        {
            int count = 0;
            for (int k=0;k<seg.row.size();k++)
            {
                count++;
                NeuronSWC S = new NeuronSWC();

                S.n 	= (int)seg.row.elementAt(k).n;
                if (S.type<=0) S.type 	= 2; //seg.row.at(k).data[1];
                S.x 	= (float) seg.row.elementAt(k).x;
                S.y 	= (float) seg.row.elementAt(k).y;
                S.z 	= (float) seg.row.elementAt(k).z;
                S.radius 	= (float) seg.row.elementAt(k).r;
                S.parent 	= (int) seg.row.elementAt(k).parent;

                //for hit & editing
                S.seg_id       = (int)seg.row.elementAt(k).seg_id;
                S.nodeinseg_id = (int) seg.row.elementAt(k).nodeinseg_id;

                //qDebug("%s  ///  %d %d (%g %g %g) %g %d", buf, S.n, S.type, S.x, S.y, S.z, S.r, S.pn);

                //if (! listNeuron.contains(S)) // 081024
                {
                    listNeuron.add(S.clone());
                    hashNeuron.put((int)S.n, listNeuron.size()-1);
                }
            }
            System.out.printf("---------------------read %d lines, %d remained lines\n", count, listNeuron.size());

            SS.n = -1;
            SS.color = new RGBA8((char)seg.color_uc[0],(char)seg.color_uc[1],(char)seg.color_uc[2],(char)seg.color_uc[3]);
            SS.on = true;
            SS.listNeuron = listNeuron;
            SS.hashNeuron = hashNeuron;

            SS.name = seg.name;
            SS.file = seg.file;
        }

        return SS;
    }

    public NeuronTree mergeSameNode() throws Exception{
        NeuronTree SS = new NeuronTree();

        System.out.println("-----------------mergeSameNode------------------------");


        Vector<V_NeuronSWC_unit> roots = new Vector<>();

        for(int i=0; i<this.seg.size(); i++){
            Vector<Integer> mark = new Vector<>();
            V_NeuronSWC s = this.seg.get(i);
            System.out.println(i+" size: "+s.row.size());
            for(int j=0; j<s.row.size(); j++){
                if(s.getIndexofParent(j) == -1){
                    V_NeuronSWC_unit u = s.row.get(j);
                    if(u.parent == -1){
                        boolean e = false;
                        for(int k=0; k<roots.size(); k++){
                            if(u.n == roots.get(k).n && u.x == roots.get(k).x && u.y == roots.get(k).y && u.z == roots.get(k).z){
                                e = true;
                                break;
                            }
                        }
                        if(e){
                            mark.add(j);
                        }else {
                            roots.add(u);
                        }
                    }
                    else {
                        mark.add(j);
                    }
                }
            }

            for (int k=0; k<mark.size(); k++){
                V_NeuronSWC_unit u = s.row.get(mark.get(k));
                s.row.remove(u);
            }
        }

//        //first conversion
//
//        V_NeuronSWC seg = V_NeuronSWC_list.merge_V_NeuronSWC_list(this);
//        seg.name = this.name;
//        seg.file = this.file;
//
//        //second conversion

        ArrayList<NeuronSWC> listNeuron = new ArrayList<NeuronSWC>();
        HashMap<Integer, Integer> hashNeuron = new HashMap<Integer, Integer>();
        listNeuron.clear();
        hashNeuron.clear();

        int count = 0;
        for(int i=0; i<this.seg.size(); i++){
            V_NeuronSWC s = this.seg.get(i);
            for(int j=0; j<s.row.size(); j++){
                count++;

                NeuronSWC S = new NeuronSWC();

                S.n 	= (int)s.row.elementAt(j).n;
                S.type = (int) s.row.elementAt(j).type;
                if (S.type<=0) S.type 	= 2; //s.row.at(j).data[1];
                S.x 	= (float) s.row.elementAt(j).x;
                S.y 	= (float) s.row.elementAt(j).y;
                S.z 	= (float) s.row.elementAt(j).z;
                S.radius 	= (float) s.row.elementAt(j).r;
                S.parent 	= (int) s.row.elementAt(j).parent;

                //for hit & editing
                S.seg_id       = (int)s.row.elementAt(j).seg_id;
                S.nodeinseg_id = (int) s.row.elementAt(j).nodeinseg_id;

                //qDebug("%s  ///  %d %d (%g %g %g) %g %d", buf, S.n, S.type, S.x, S.y, S.z, S.r, S.pn);

                //if (! listNeuron.contains(S)) // 081024
                {
                    listNeuron.add(S);
                    hashNeuron.put((int)S.n, listNeuron.size()-1);
                }
            }
        }
//        for (int k=0;k<seg.row.size();k++)
//        {
//            count++;
//
//            NeuronSWC S = new NeuronSWC();
//
//            S.n 	= (int)seg.row.elementAt(k).n;
//            if (S.type<=0) S.type 	= 2; //seg.row.at(k).data[1];
//            S.x 	= (float) seg.row.elementAt(k).x;
//            S.y 	= (float) seg.row.elementAt(k).y;
//            S.z 	= (float) seg.row.elementAt(k).z;
//            S.radius 	= (float) seg.row.elementAt(k).r;
//            S.parent 	= (int) seg.row.elementAt(k).parent;
//
//            //for hit & editing
//            S.seg_id       = (int)seg.row.elementAt(k).seg_id;
//            S.nodeinseg_id = (int) seg.row.elementAt(k).nodeinseg_id;
//
//            //qDebug("%s  ///  %d %d (%g %g %g) %g %d", buf, S.n, S.type, S.x, S.y, S.z, S.r, S.pn);
//
//            //if (! listNeuron.contains(S)) // 081024
//            {
//                listNeuron.add(S);
//                hashNeuron.put((int)S.n, listNeuron.size()-1);
//            }
//        }
        System.out.printf("---------------------read %d lines, %d remained lines\n", count, listNeuron.size());

        SS.n = -1;
        SS.color = new RGBA8((char)this.color_uc[0],(char)this.color_uc[1],(char)this.color_uc[2],(char)this.color_uc[3]);
        SS.on = true;
        SS.listNeuron = listNeuron;
        SS.hashNeuron = hashNeuron;

        SS.name = this.name;
        SS.file = this.file;

        return SS;

    }

    public void deleteCurve(ArrayList<Float> line, float [] finalMatrix, int [] sz, float [] mz){
        System.out.println("deleteline1--------------------------");
        for (int i = 0; i < line.size() / 3 - 1; i++){
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(i * 3 + 3);
            float y2 = line.get(i * 3 + 4);
            for(int j=0; j<this.nsegs(); j++){
                System.out.println("delete curswclist --"+j);
                V_NeuronSWC seg = this.seg.get(j);
                if(seg.to_be_deleted)
                    continue;
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                for(int k=0; k<seg.row.size(); k++){
                    if(seg.row.get(k).parent != -1 && seg.getIndexofParent(k) != -1){
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(k));
                        swcUnitMap.put(k,parent);
                    }
                }
                System.out.println("delete: end map");
                for(int k=0; k<seg.row.size(); k++){
                    System.out.println("j: "+j+" k: "+k);
                    V_NeuronSWC_unit child = seg.row.get(k);
                    int parentid = (int) child.parent;
                    if (parentid == -1 || seg.getIndexofParent(k) == -1){
                        System.out.println("parent -1");
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(k);
                    float[] pchild = {(float) child.x, (float) child.y, (float) child.z};
                    float[] pparent = {(float) parent.x, (float) parent.y, (float) parent.z};
                    float[] pchildm = VolumetoModel(pchild, sz, mz);
                    float[] pparentm = VolumetoModel(pparent, sz, mz);
                    float[] p2 = {pchildm[0],pchildm[1],pchildm[2],1.0f};
                    float[] p1 = {pparentm[0],pparentm[1],pparentm[2],1.0f};

                    float [] p1Volumne = new float[4];
                    float [] p2Volumne = new float[4];
                    Matrix.multiplyMV(p1Volumne, 0, finalMatrix, 0, p1, 0);
                    Matrix.multiplyMV(p2Volumne, 0, finalMatrix, 0, p2, 0);
                    devideByw(p1Volumne);
                    devideByw(p2Volumne);
                    float x3 = p1Volumne[0];
                    float y3 = p1Volumne[1];
                    float x4 = p2Volumne[0];
                    float y4 = p2Volumne[1];

                    double m=(x2-x1)*(y3-y1)-(x3-x1)*(y2-y1);
                    double n=(x2-x1)*(y4-y1)-(x4-x1)*(y2-y1);
                    double p=(x4-x3)*(y1-y3)-(x1-x3)*(y4-y3);
                    double q=(x4-x3)*(y2-y3)-(x2-x3)*(y4-y3);

                    if( (Math.max(x1, x2) >= Math.min(x3, x4))
                            && (Math.max(x3, x4) >= Math.min(x1, x2))
                            && (Math.max(y1, y2) >= Math.min(y3, y4))
                            && (Math.max(y3, y4) >= Math.min(y1, y2))
                            && ((m * n) <= 0) && (p * q <= 0)){
                        System.out.println("------------------this is delete---------------");
                        seg.to_be_deleted = true;
                        break;
                    }
                }
            }
        }
        this.deleteMutiSeg(new Vector<Integer>());
    }

    public void splitCurve(ArrayList<Float> line, float [] finalMatrix, int [] sz, float [] mz){
        System.out.println("split1--------------------------");
        boolean found = false;
        Vector<Integer> toSplit = new Vector<Integer>();
        for (int i = 0; i < line.size() / 3 - 1; i++){
            if (found == true){
                break;
            }
            float x1 = line.get(i * 3);
            float y1 = line.get(i * 3 + 1);
            float x2 = line.get(i * 3 + 3);
            float y2 = line.get(i * 3 + 4);
            for(int j=0; j<this.nsegs(); j++){
                System.out.println("delete curswclist --"+j);
                V_NeuronSWC seg = this.seg.get(j);
                if(seg.to_be_deleted)
                    continue;
                Map<Integer, V_NeuronSWC_unit> swcUnitMap = new HashMap<Integer, V_NeuronSWC_unit>();
                for(int k=0; k<seg.row.size(); k++){
                    if(seg.row.get(k).parent != -1 && seg.getIndexofParent(k) != -1){
                        V_NeuronSWC_unit parent = seg.row.get(seg.getIndexofParent(k));
                        swcUnitMap.put(k,parent);
                    }
                }
                System.out.println("delete: end map");
                for(int k=0; k<seg.row.size(); k++){
                    System.out.println("j: "+j+" k: "+k);
                    V_NeuronSWC_unit child = seg.row.get(k);
                    int parentid = (int) child.parent;
                    if (parentid == -1 || seg.getIndexofParent(k) == -1){
                        System.out.println("parent -1");
                        continue;
                    }
                    V_NeuronSWC_unit parent = swcUnitMap.get(k);
                    float[] pchild = {(float) child.x, (float) child.y, (float) child.z};
                    float[] pparent = {(float) parent.x, (float) parent.y, (float) parent.z};
                    float[] pchildm = VolumetoModel(pchild, sz, mz);
                    float[] pparentm = VolumetoModel(pparent, sz, mz);
                    float[] p2 = {pchildm[0],pchildm[1],pchildm[2],1.0f};
                    float[] p1 = {pparentm[0],pparentm[1],pparentm[2],1.0f};

                    float [] p1Volumne = new float[4];
                    float [] p2Volumne = new float[4];
                    Matrix.multiplyMV(p1Volumne, 0, finalMatrix, 0, p1, 0);
                    Matrix.multiplyMV(p2Volumne, 0, finalMatrix, 0, p2, 0);
                    devideByw(p1Volumne);
                    devideByw(p2Volumne);
                    float x3 = p1Volumne[0];
                    float y3 = p1Volumne[1];
                    float x4 = p2Volumne[0];
                    float y4 = p2Volumne[1];

                    double m=(x2-x1)*(y3-y1)-(x3-x1)*(y2-y1);
                    double n=(x2-x1)*(y4-y1)-(x4-x1)*(y2-y1);
                    double p=(x4-x3)*(y1-y3)-(x1-x3)*(y4-y3);
                    double q=(x4-x3)*(y2-y3)-(x2-x3)*(y4-y3);

                    if( (Math.max(x1, x2) >= Math.min(x3, x4))
                            && (Math.max(x3, x4) >= Math.min(x1, x2))
                            && (Math.max(y1, y2) >= Math.min(y3, y4))
                            && (Math.max(y3, y4) >= Math.min(y1, y2))
                            && ((m * n) <= 0) && (p * q <= 0)){
                        System.out.println("------------------this is split---------------");
//                        seg.to_be_deleted = true;
//                        break;
                        found = true;
//                        V_NeuronSWC newSeg = new V_NeuronSWC();
//                        V_NeuronSWC_unit first = seg.row.get(k);
//                        try {
//                            V_NeuronSWC_unit firstClone = first.clone();
//                            newSeg.append(firstClone);
//                        }catch (Exception e){
//                            System.out.println(e.getMessage());
//                        }
                        splitOnJK(j, k, seg, toSplit);
//                        int cur = k;
////                        toSplit.add(k);
//                        while (seg.getIndexofParent(cur) != -1){
//                            cur = seg.getIndexofParent(cur);
//                            toSplit.add(cur);
////                            V_NeuronSWC_unit nsu = swcUnitMap.get(cur);
////                            try{
////                                V_NeuronSWC_unit nsuClone = nsu.clone();
////                                newSeg.append(nsuClone);
////                            }catch (Exception e){
////                                System.out.println(e.getMessage());
////                            }
////                            seg.row.remove(cur);
//
//                        }
//                        V_NeuronSWC newSeg1 = new V_NeuronSWC();
//                        V_NeuronSWC newSeg2 = new V_NeuronSWC();
//                        int newSegid = this.nsegs();
//                        V_NeuronSWC_unit first = seg.row.get(k);
//                        try {
//                            V_NeuronSWC_unit firstClone = first.clone();
//                            V_NeuronSWC_unit firstClone2 = first.clone();
//                            newSeg1.append(firstClone);
//                            newSeg2.append(firstClone2);
//                        }catch (Exception e){
//                            System.out.println(e.getMessage());
//                        }
//                        for (int w = 0; w < seg.row.size(); w++){
//                            try {
//                                V_NeuronSWC_unit temp = seg.row.get(w);
//                                if (!toSplit.contains(w)) {
//                                    newSeg2.append(temp);
//                                }else if(toSplit.contains(w) && (w != k)){
//                                    temp.seg_id = newSegid;
//                                    newSeg1.append(temp);
//                                }
//                            }catch (Exception e){
//                                System.out.println(e.getMessage());
//                            }
//                        }
//                        this.deleteSeg(j);
//                        this.append(newSeg1);
//                        this.append(newSeg2);
//                        splitPoints.add(pchildm[0]);
//                        splitPoints.add(pchildm[1]);
//                        splitPoints.add(pchildm[2]);
//                        splitType = (int)child.type;
                        break;
                    }
                }
            }
        }
    }

    private void splitOnJK(int j, int k, V_NeuronSWC seg, Vector<Integer> toSplit){
        int cur = k;
//                        toSplit.add(k);
        while (seg.getIndexofParent(cur) != -1){
            cur = seg.getIndexofParent(cur);
            toSplit.add(cur);
//                            V_NeuronSWC_unit nsu = swcUnitMap.get(cur);
//                            try{
//                                V_NeuronSWC_unit nsuClone = nsu.clone();
//                                newSeg.append(nsuClone);
//                            }catch (Exception e){
//                                System.out.println(e.getMessage());
//                            }
//                            seg.row.remove(cur);

        }
        V_NeuronSWC newSeg1 = new V_NeuronSWC();
        V_NeuronSWC newSeg2 = new V_NeuronSWC();
        int newSegid = this.nsegs();
        V_NeuronSWC_unit first = seg.row.get(k);
        try {
            V_NeuronSWC_unit firstClone = first.clone();
            V_NeuronSWC_unit firstClone2 = first.clone();
            newSeg1.append(firstClone);
            newSeg2.append(firstClone2);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        for (int w = 0; w < seg.row.size(); w++){
            try {
                V_NeuronSWC_unit temp = seg.row.get(w);
                if (!toSplit.contains(w)) {
                    newSeg2.append(temp);
                }else if(toSplit.contains(w) && (w != k)){
                    temp.seg_id = newSegid;
                    newSeg1.append(temp);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        this.deleteSeg(j);
        this.append(newSeg1);
        this.append(newSeg2);
    }

    private float[] VolumetoModel(float[] input, int [] sz, float [] mz){
        float[] result = new float[3];

        result[0] = (sz[0] - input[0]) / sz[0] * mz[0];
        result[1] = (sz[1] - input[1]) / sz[1] * mz[1];
        result[2] = input[2] / sz[2] * mz[2];

        return result;
    }

    private void devideByw(float[] x){
        if(Math.abs(x[3]) < 0.000001f){
            Log.v("devideByw","can not be devided by 0");
            return;
        }

        for(int i=0; i<3; i++)
            x[i] = x[i]/x[3];

    }
}
