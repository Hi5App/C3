package com.penglab.hi5.basic.learning.opimageline;

import com.penglab.hi5.basic.NeuronSWC;
import com.penglab.hi5.basic.NeuronTree;

import java.util.HashMap;
import java.util.Vector;

public class BranchTree {
    private Vector<Branch> branches;
    private NeuronTree nt;

    public BranchTree(){
        nt = null;
        branches = null;
    }

    public boolean initialize(NeuronTree nt){
        this.nt = nt;
        branches = new Vector<>();

        Vector<Integer> roots = new Vector<>();
        int pointNum = nt.listNeuron.size();

        Vector<Vector<Integer>> child = new Vector<Vector<Integer>>(pointNum);

        HashMap<Integer, Integer> hN = new HashMap<>();

        for (int i = 0; i < pointNum; i++){
            hN.put((int)nt.listNeuron.get(i).n, i);
        }

        for (int i = 0; i < pointNum; i++){
            Vector<Integer> temp = new Vector<Integer>();
            child.add(temp);
        }

        for (int i = 0; i < pointNum; i++) {
            NeuronSWC p = nt.listNeuron.get(i);
            int prt = (int) p.parent;
            if (prt != -1) {
                int prtIndex = hN.get(prt);
                child.get(prtIndex).add(i);
            } else {
                roots.add(i);
            }
        }

        Vector<Integer> queue = new Vector<>();
        queue.addAll(roots);

        while (!queue.isEmpty()){
            int temp = queue.firstElement();
            queue.remove(0);
            Vector<Integer> children= child.get(temp);
            for (int i = 0; i < children.size(); i++){
                Branch branch = new Branch();
                branch.setHeadPoint(nt.listNeuron.get(temp));

                int ci_index = children.get(i);
                while (child.get(ci_index).size() == 1){
//                    NeuronSWC ci = nt.listNeuron.get(ci_index);
                    ci_index = child.get(ci_index).get(0);
                }
                if(child.get(ci_index).size()>1){
                    NeuronSWC ci = nt.listNeuron.get(ci_index);
                    branch.setTailPoint(ci);
                    queue.add(ci_index);
                }else if(child.get(ci_index).size() == 0){
                    NeuronSWC ci = nt.listNeuron.get(ci_index);
                    branch.setTailPoint(ci);
                }
                branches.add(branch);
            }
        }


        return true;
    }

    public NeuronTree getNt() {
        return nt;
    }

    public Vector<Branch> getBranches() {
        return branches;
    }
}
