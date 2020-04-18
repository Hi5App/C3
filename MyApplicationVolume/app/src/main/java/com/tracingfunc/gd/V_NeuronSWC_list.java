package com.tracingfunc.gd;

import com.example.basic.NeuronSWC;
import com.example.basic.NeuronTree;
import com.example.basic.RGBA8;

import java.util.ArrayList;
import java.util.HashMap;
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
                if (min_ind<0) System.out.println("Found illeagal neuron node index which is less than 0 in merge_V_NeuronSWC_list()!");
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
}
