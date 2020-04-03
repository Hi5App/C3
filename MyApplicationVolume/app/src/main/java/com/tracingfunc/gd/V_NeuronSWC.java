package com.tracingfunc.gd;

import java.util.Vector;

public class V_NeuronSWC implements Cloneable{
    public Vector<V_NeuronSWC_unit> row;
    public boolean b_linegraph;
    public String name;
    public String comment;
    public String file;
    public int[] color_uc;
    public boolean b_jointed;
    public boolean to_be_deleted;   // @ADDED by Alessandro on 2015-05-08. Needed to support late delete of multiple neuron segments.
    public boolean to_be_broken;
    public boolean on; //Added by Y. Wang on 2016-05-25. For the segment-wise display of a SWC.

    // boolean check_data_consistency() {/* to verify if unique node id have unique coord, and if parent are in the nid, except -1*/ return true;}
    public V_NeuronSWC()
    {
        name="unset"; b_linegraph=false;  color_uc = new int[4];
        for(int i=0; i<color_uc.length; ++i)
        {
            color_uc[i] = 0;
        }
        b_jointed=false;
        to_be_deleted = false;
        to_be_broken = false;
        on = true;
    }

    public V_NeuronSWC(String new_name, boolean is_linegraph)
    {
        name=new_name; b_linegraph=is_linegraph;  color_uc = new int[4];
        for(int i=0; i<color_uc.length; i++)
        {
            color_uc[i] = 0;
        }
        b_jointed=false;
        to_be_deleted = false;
        to_be_broken = false;
        on = true;
    }

    @Override
    protected V_NeuronSWC clone() throws CloneNotSupportedException {
        V_NeuronSWC t = null;
        t = (V_NeuronSWC) super.clone();
        t.row = new Vector<V_NeuronSWC_unit>();
        for(int i=0; i<this.row.size(); i++){
            t.row.add(this.row.elementAt(i).clone());
        }
        t.color_uc = new int[this.color_uc.length];
        for(int i=0; i<this.color_uc.length; i++){
            t.color_uc[i] = this.color_uc[i];
        }
        return t;
    }

    // V_BranchUnit branchingProfile;

    // void printInfo();

    long nrows() {return row.size();}

    // V_SWCNodes unique_nodes_info(); //determine the unique nodes

    // vector<long> unique_nid(); //unique node id (id is the first column value in SWC)
    // long n_unique_nid(); //number of unique node ids
    // vector<V_NeuronSWC_coord> unique_ncoord(); //unique node coordinates (coordinates are the 3rd to 5th column)
    // long n_unique_ncoord(); //number of unique node coords

    long maxnoden() //091029 change maxnoden from >=-1 to >=0 for base_n in set_simple_path...
    {
        long maxn=0;	for (int i=0;i<row.size();i++) if (row.elementAt(i).n > maxn) maxn = (long)row.elementAt(i).n;		return maxn;
    }
    // long getIndexofParent(long j)
    // {
    // 	long res=-1; long parent = row.at(j).parent;
    //             for (long i=0;i<(long)row.size();i++) if (row[i].n==parent)	{res=i; break;}
    // 	return res;
    // }
    // vector<long> getIndexofParent_nodeid(long nid) //return the array of of node "nid"'s parents' nid
    // {
    // 	vector<long> res;
    //             for (long i=0;i<(long)row.size();i++)
    // 	{
    // 		if (row[i].n==nid)
    // 		{
    // 			long curparent = row[i].parent;
    // 			boolean b_exist=false;
    //                             for (long j=0;j<(long)res.size();j++)
    // 				if (res.at(j)==curparent) {	b_exist=true; break;}
    // 			if (!b_exist)
    // 				res.add(curparent);
    // 		}
    // 	}
    // 	return res;
    // }

    public void append(V_NeuronSWC_unit new_node) {row.add(new_node);}
    public void clear() {row.clear();}
    // vector <V_NeuronSWC> decompose();
    // boolean reverse();

    // boolean isLineGraph() {return b_linegraph;} //just return the "claimed" property is a line graph
    // //check if a 3D location is contained in the swc
    // long getFirstIndexof3DPos(double x,double y,double z) //return -1 is no included, othwise return the first detected index
    // {
    // 	long res=-1;
    //             for (long i=0;i<(long)row.size();i++) if (row[i].data[2]==x && row[i].data[3]==y && row[i].data[4]==z)	{res=i; break;}
    // 	return res;
    // }
    // long getFirstIndexof3DPos(const V_NeuronSWC_unit & subject_node) {return getFirstIndexof3DPos(subject_node.data[2], subject_node.data[3], subject_node.data[4]);}
    // long getFirstIndexof3DPos(const V_NeuronSWC_unit * subject_node) {return getFirstIndexof3DPos(subject_node->data[2], subject_node->data[3], subject_node->data[4]);}

    // vector<long> getAllIndexof3DPos(double x,double y,double z, long noninclude_ind) //return all indexes except the one indicated as noninclude_ind
    // {
    // 	vector<long> res;
    //             for (long i=0;i<(long)row.size();i++) if (row[i].data[2]==x && row[i].data[3]==y && row[i].data[4]==z)	{ if (i!=noninclude_ind) res.add(i); }
    // 	return res;
    // }
    // vector <long> getAllIndexof3DPos(const V_NeuronSWC_unit & subject_node, long noninclude_ind) {return getAllIndexof3DPos(subject_node.data[2], subject_node.data[3], subject_node.data[4], noninclude_ind);}
    // vector <long> getAllIndexof3DPos(const V_NeuronSWC_unit * subject_node, long noninclude_ind) {return getAllIndexof3DPos(subject_node->data[2], subject_node->data[3], subject_node->data[4], noninclude_ind);}
}
