package com.feature_calc_func;

import com.example.basic.NeuronSWC;
import com.example.basic.NeuronTree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.ArrayList;

public class readSWC_file_nt {
    NeuronTree readSWC_file(String filename) {
        ArrayList<String> arraylist = new ArrayList<String>();
        NeuronTree nt = new NeuronTree();
        nt.file = filename;
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
        for (int i = 0; i < arraylist.size() - 2; i++) {
            NeuronSWC S = new NeuronSWC();
            String current = arraylist.get(i + 2);
            String[] s = current.split(" ");
            for (int j = 0; j < 7; j++) {
                if (i == 0)
                    S.nodeinseg_id = Integer.parseInt(arraylist.get(i));
                else if (i == 1)
                    S.type = Integer.parseInt(arraylist.get(i));
                else if (i == 2)
                    S.x = Float.parseFloat(arraylist.get(i));
                else if (i == 3)
                    S.y = Float.parseFloat(arraylist.get(i));
                else if (i == 4)
                    S.z = Float.parseFloat(arraylist.get(i));
                else if (i == 5)
                    S.radius = Float.parseFloat(arraylist.get(i));
                else if (i == 6)
                    S.parent = Integer.parseInt(arraylist.get(i));
                // the ESWC extension, by PHC, 20120217
                else if (i == 7)
                    S.seg_id = Integer.parseInt(arraylist.get(i));
                else if (i == 8)
                    S.level = Integer.parseInt(arraylist.get(i));
                else if (i == 9)
                    S.creatmode = Integer.parseInt(arraylist.get(i));
                else if (i == 10)
                    S.timestamp = Integer.parseInt(arraylist.get(i));
                else if (i == 11)
                    S.tfresindex = Integer.parseInt(arraylist.get(i));
                // change ESWC format to adapt to flexible feature number, by WYN, 20150602
                else
                    S.fea_val.add(Float.parseFloat(arraylist.get(i)));
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
}