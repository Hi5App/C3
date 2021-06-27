package com.penglab.hi5.basic.feature_calc_func;

import android.widget.Toast;

import com.penglab.hi5.basic.NeuronSWC;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.core.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class split_nt {
    static List<NeuronTree> split_nt_file(NeuronTree nt) {
        List<NeuronSWC> list = new ArrayList<>(nt.listNeuron);
        List<NeuronSWC> rootSlist = new ArrayList<>();
        List<Integer> nodeidxlist = new ArrayList<>();
        List<NeuronTree> splited_NT = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            nodeidxlist.add((int) list.get(i).n);
        }
        for (int i = 0; i < list.size(); i++) {
//            if ((int) list.get(i).parent == -1 || !nodeidxlist.contains((int) list.get(i).parent)) {
            if ((int) list.get(i).parent == -1) {
                rootSlist.add(list.get(i));
            }
        }
        if (rootSlist.size() == 1) {
            splited_NT.add(nt);
            return splited_NT;
        } else {
            for (int i = 0; i < rootSlist.size(); i++) {
                List<Boolean> countmatrix = new ArrayList<>();
                int idx = 0;
                NeuronTree tempnt = new NeuronTree();
                tempnt.listNeuron.add(rootSlist.get(i));
                tempnt.hashNeuron.put((int) (rootSlist.get(i).n), tempnt.listNeuron.size() - 1);
                countmatrix.add(false);
                while (true) {
                    if (!countmatrix.get(idx)) {
                        for (int k = 0; k < list.size(); k++) {
                            if (list.get(k).parent == tempnt.listNeuron.get(idx).n) {
                                tempnt.listNeuron.add(list.get(k));
                                tempnt.hashNeuron.put((int) (list.get(k).n), tempnt.listNeuron.size() - 1);
                                countmatrix.add(false);
                            }
                        }
                        countmatrix.set(idx, true);
                        idx++;


                    }
                    int tempcount = 0;
                    for (int j = 0; j < countmatrix.size(); j++) {
                        if (!countmatrix.get(j)) {
                            tempcount++;
                            break;
                        }
                    }
                    if (tempcount == 0) {
                        break;
                    }
                    if (idx > list.size()) {
                        Toast.makeText(MainActivity.getContext(), "reach max iteration, may have loop structure", Toast.LENGTH_LONG).show();
                        tempnt = null;
                        break;
                    }
                }
                if (tempnt != null)
                    splited_NT.add(tempnt);
            }

            return splited_NT;
        }
    }

}

