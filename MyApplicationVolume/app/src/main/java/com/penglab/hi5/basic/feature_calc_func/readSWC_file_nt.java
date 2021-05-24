package com.penglab.hi5.basic.feature_calc_func;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import com.penglab.hi5.basic.utils.FileManager;
import com.penglab.hi5.basic.NeuronSWC;
import com.penglab.hi5.basic.NeuronTree;
import com.penglab.hi5.core.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import static com.penglab.hi5.core.MainActivity.getContext;

/*
    Just for debug...
    This file is to create a Input data for "Morphology features Calculation".
 */
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
                hashNeuron.put((int) (S.n), listNeuron.size() - 1);
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


    NeuronTree readSWC_file(Uri uri) {
        Context context = getContext();

        ArrayList<String> arraylist = new ArrayList<String>();
        NeuronTree nt = new NeuronTree();
        FileManager fileManager = new FileManager();
        nt.file = fileManager.getFileName(uri);
        String file_type = nt.file.substring(nt.file.lastIndexOf(".")).toUpperCase();
        if (!(file_type.equals(".SWC") | file_type.equals(".ESWC"))) {
            Toast.makeText(MainActivity.getContext(), "Failed, Only support SWC or ESWC file", Toast.LENGTH_LONG).show();
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
                hashNeuron.put((int) (S.n), listNeuron.size() - 1);
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