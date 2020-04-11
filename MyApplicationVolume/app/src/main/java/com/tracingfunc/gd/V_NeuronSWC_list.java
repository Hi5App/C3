package com.tracingfunc.gd;

import java.util.Vector;

public class V_NeuronSWC_list implements Cloneable{
    Vector<V_NeuronSWC> seg; //since each seg could be a complete neuron or multiple paths, thus I call it "seg", but not "path"
    int last_seg_num; //?? for what purpose? seems only used once in v3d_core.cpp. Questioned by Hanchuan, 20100210
    String name;
    String comment;
    String file;
    int[] color_uc;
    boolean b_traced;

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
    // boolean deleteSeg(int seg_id);

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
}
