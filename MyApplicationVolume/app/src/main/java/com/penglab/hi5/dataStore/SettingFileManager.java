package com.penglab.hi5.dataStore;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Vector;

public class SettingFileManager {

    /*
     Img Info Maintenance
     ImgInfo  --> Draw -> Brain_Cur.
                          Brain_Num -> RESList.
                                       RES_Cur.
                                    -> RES       -> Neuron
                                                 -> Offset

              --> Check -> Brain_Cur.
                           Brain_Num -> RESList.
                                        RES_Cur.
                                     -> RES      -> Arbor
                                                 -> Offset
     */


    // getFileName_Remote: brain_number  18454
    //


    private static String filepath = "EMPTY_PATH";

    public SettingFileManager(Context context){
        filepath = context.getExternalFilesDir(null).toString();
    }




    /**
     * get the latest BrainNum in Draw Mode
     * @param isDraw if in Draw Mode
     * @return the BrainNum in Draw Mode you chose currently
     */
    public static String getBrainNum_Remote(boolean isDraw){
        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        String BrainNum = null;
        File file = new File(filepath + "/ImgInfo" + mode + "/Brain_Cur.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--EMPTY--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get BrainNum", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                BrainNum = buffreader.readLine();
                buffreader.close();  //关闭ReaderBuffer
                inputStream.close(); //关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get BrainNum", BrainNum);
        return BrainNum;
    }


    /**
     * update the BrainNum
     * @param BrainNum the BrainNum currently chose
     * @param isDraw if in Draw Mode
     */
    public static void setBrainNum_Remote(String BrainNum, boolean isDraw){

        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        File file = new File(filepath + "/ImgInfo" + mode + "/Brain_Cur.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set BrainNum", "Fail to create file");
            }
        }

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(BrainNum.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * get the latest BrainNum in Draw Mode
     * @return the BrainNum in Draw Mode you chose currently
     */
    public static String getRES_Remote(String BrainNum, boolean isDraw){
        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        String RES = null;
        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum + "/RES_Cur.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--EMPTY--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get RES", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                RES = buffreader.readLine();
                buffreader.close();  //关闭ReaderBuffer
                inputStream.close(); //关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get RES", RES);
        return RES;
    }



    /**
     * update the RES
     * @param RES the RES currently chose
     */
    public static void setRES__Remote(String BrainNum, String RES, boolean isDraw){
        /*
        example: 1;RES(13149x17500x5520)
         */

        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum + "/RES_Cur.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set RES", "Fail to create file");
            }
        }

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(RES.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * get the latest BrainNum in Draw Mode
     * @return the BrainNum in Draw Mode you chose currently
     */
    public static Vector<String> getRESList_Remote(String BrainNum, boolean isDraw){
        String mode;
        mode = isDraw ? "/Draw" : "/Check";
        Vector<String> RESList = new Vector<>() ;

        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum + "/RESList.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--EMPTY--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get RESList", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                while ((line = buffreader.readLine()) != null){
                    RESList.add(line);
                }
                buffreader.close();
                inputreader.close();
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get RESList", Arrays.toString(RESList.toArray()));
        return RESList;
    }


    /**
     * update the RESList
     * @param RESList the RESList of current Brain
     */
    public static void setRESList__Remote(String BrainNum, String[] RESList, boolean isDraw){

        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum +"/RESList.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set RESList", "Fail to create file");
            }
        }

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter osw=new OutputStreamWriter(outStream, "UTF-8");
            BufferedWriter bw=new BufferedWriter(osw);

            for(String res:RESList){
                bw.write(res+"\n");
            }

            bw.close();
            osw.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * get the latest BrainNum in Draw Mode
     * @return the BrainNum in Draw Mode you chose currently
     */
    public static String getNeuronNum_Remote(String BrainNum, String RES, boolean isDraw){
        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        String NeuronNum = null;
        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum  + "/" + RES + "/NeuronNum.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--EMPTY--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get NeuronNum", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                NeuronNum = buffreader.readLine();
                buffreader.close();  //关闭ReaderBuffer
                inputStream.close(); //关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get NeuronNum", NeuronNum);
        return NeuronNum;
    }


    /**
     * update the NeuronNum
     * @param NeuronNum the NeuronNum of current Brain
     */
    public static void setNeuronNum_Remote(String BrainNum, String NeuronNum, String RES, boolean isDraw){

        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum + "/" + RES +"/NeuronNum.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set RESList", "Fail to create file");
            }
        }

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(NeuronNum.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the latest BrainNum in Draw Mode
     * @return the BrainNum in Draw Mode you chose currently
     */
    public static String[] getOffset_Remote(String BrainNum, String RES, boolean isDraw){
        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        String[] Offset = null;
        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum + "/" + RES + "/Offset.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--EMPTY--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get Offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String offset_str = buffreader.readLine();
                Offset = offset_str.split("_");
                buffreader.close();  //关闭ReaderBuffer
                inputStream.close(); //关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get Offset", Arrays.toString(Offset));
        return Offset;
    }


    /**
     * get the latest BrainNum in Draw Mode
     * @return the BrainNum in Draw Mode you chose currently
     */
    public static String getOffsetStr_Remote(String BrainNum, String RES, boolean isDraw){
        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        String Offset = "";
        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum + "/" + RES + "/Offset.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--EMPTY--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get Offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                Offset = buffreader.readLine();
                buffreader.close();  //关闭ReaderBuffer
                inputStream.close(); //关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get Offset", Offset);
        return Offset;
    }

    /**
     * update the NeuronNum
     * @param Offset the NeuronNum of current Brain
     */
    public static void setOffset_Remote(String BrainNum, String RES, String Offset, boolean isDraw){

        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum + "/"+ RES + "/Offset.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set Offset", "Fail to create file");
            }
        }

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(Offset.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    /**
     * get the latest BrainNum in Draw Mode
     * @return the BrainNum in Draw Mode you chose currently
     */
    public static Vector<String> getNeuronList_Remote(String BrainNum, boolean isDraw){
        String mode;
        mode = isDraw ? "/Draw" : "/Check";
        Vector<String> NeuronList = new Vector<>() ;

        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum + "/NeuronList.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--EMPTY--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get RESList", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                while ((line = buffreader.readLine()) != null){
                    NeuronList.add(line);
                }
                buffreader.close();
                inputreader.close();
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get NeuronList", Arrays.toString(NeuronList.toArray()));
        return NeuronList;
    }


    /**
     * update the RESList
     * @param NeuronList the RESList of current Brain
     */
    public static void setNeuronList__Remote(String BrainNum, String[] NeuronList, boolean isDraw){

        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum +"/NeuronList.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set RESList", "Fail to create file");
            }
        }

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter osw=new OutputStreamWriter(outStream, "UTF-8");
            BufferedWriter bw=new BufferedWriter(osw);

            for(String neuron:NeuronList){
                bw.write(neuron+"\n");
            }

            bw.close();
            osw.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * get the latest BrainNum in Draw Mode
     * @return the BrainNum in Draw Mode you chose currently
     */
    public static Vector<String> getArborList_Remote(String BrainNum, boolean isDraw){
        String mode;
        mode = isDraw ? "/Draw" : "/Check";
        Vector<String> NeuronList = new Vector<>() ;

        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum + "/NeuronList.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--EMPTY--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get RESList", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                while ((line = buffreader.readLine()) != null){
                    NeuronList.add(line);
                }
                buffreader.close();
                inputreader.close();
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get NeuronList", Arrays.toString(NeuronList.toArray()));
        return NeuronList;
    }


    /**
     * update the RESList
     * @param NeuronList the RESList of current Brain
     */
    public static void setArborList__Remote(String BrainNum, String[] NeuronList, boolean isDraw){

        String mode;
        mode = isDraw ? "/Draw" : "/Check";

        File file = new File(filepath + "/ImgInfo" + mode + "/" + BrainNum +"/NeuronList.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set RESList", "Fail to create file");
            }
        }

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter osw=new OutputStreamWriter(outStream, "UTF-8");
            BufferedWriter bw=new BufferedWriter(osw);

            for(String neuron:NeuronList){
                bw.write(neuron+"\n");
            }

            bw.close();
            osw.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static String getFilename_Local(Context context){
        String filename = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Local_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                if (!dir.exists()){
                    if (!dir.mkdirs()){
                        Log.e("SettingFileManager","Fail to create directory !");
                    }
                }
                if (!file.createNewFile()){
                    Log.e("SettingFileManager","Fail to create file !");
                }

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get filename", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                filename = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get filename", filename);
        return filename;
    }

    /**
     * put the ip address you input to local file
     * @param filename the ip address currently input
     */
    public static void setFilename_Local(String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Local_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                if (!dir.exists()){
                    if (!dir.mkdirs()){
                        Log.e("SettingFileManager","Fail to create directory !");
                    }
                }
                if (!file.createNewFile()){
                    Log.e("SettingFileManager","Fail to create file !");
                }
            }catch (Exception e){
                Log.v("set local filename", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(filename.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getoffset_Local(Context context, String filename){
        String offset = null;

//        filename = filename.split("RES")[1];
//        String offset_x = filename.split("x")[0];
//        String offset_y = filename.split("x")[1];
//        String offset_z = filename.split("x")[2];
//
//        int offset_x_i = Integer.parseInt(offset_x)/2;
//        int offset_y_i = Integer.parseInt(offset_y)/2;
//        int offset_z_i = Integer.parseInt(offset_z)/2;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/" + filename + "_Local.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "0" + "_" + "0" + "_" + "0" + "_128";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                offset = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get offset", offset);
        return offset;
    }

    /**
     * put the offset you input to local file
     * @param offset the offset currently input
     */
    public static void setoffset_Local(String offset, String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/" + filename + "_Local.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(offset.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static String getUserAccount(Context context){
        String userAccount = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Chat_userAccount.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get Chat_userAccount", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                userAccount = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get Chat_userAccount", userAccount);
        return userAccount;
    }

    /**
     * put the ip address you input to local file
     * @param userAccount the ip address currently input
     */
    public static void setUserAccount(String userAccount, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Chat_userAccount.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set local filename", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(userAccount.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static String getUserAccount_Check(Context context){
        String userAccount = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Check_userAccount.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get Chat_userAccount", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                userAccount = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get Check_userAccount", userAccount);
        return userAccount;
    }

    /**
     * put the ip address you input to local file
     * @param userAccount the ip address currently input
     */
    public static void setUserAccount_Check(String userAccount, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Check_userAccount.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set local filename", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(userAccount.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static String getFilename_Remote(Context context){
        String filename = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Remote_Filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get filename", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                filename = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get filename", filename);
        return filename;
    }

    /**
     * put the ip address you input to local file
     * @param filename the ip address currently input
     */
    public static void setFilename_Remote(String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Remote_Filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set filename", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(filename.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getoffset_Remote(Context context, String filename){
        String offset = null;

        String offset_x = "0";
        String offset_y = "0";
        String offset_z = "0";

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Brain/" + filename + "/Offset.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = offset_x + "_" + offset_y + "_" + offset_z + "_128";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                offset = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get offset", offset);
        return offset;
    }

    /**
     * put the offset you input to local file
     * @param offset the offset currently input
     */
    public static void setoffset_Remote(String offset, String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Brain/" + filename + "/Offset.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(offset.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getNeuronNumber_Remote(Context context, String filename){
        String number = "";
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Brain/" + filename + "/NeuronNum" + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                number = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get number", number);
        return number;
    }

    /**
     * put the number you input to local file
     * @param number the number currently input
     */
    public static void setNeuronNumber_Remote(String number, String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Brain/" + filename + "/NeuronNum" + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(number.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static Vector<String> getRES(Context context, String BrainNumber){
        Vector<String> res = new Vector<>() ;
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/RES/" + BrainNumber + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                while ((line = buffreader.readLine()) != null){
                    res.add(line);
                }
                buffreader.close();
                inputreader.close();
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get res", Arrays.toString(res.toArray()));
        return res;
    }


    /**
     * Set the RESs for current Brain File
     * @param RES the RES
     * @param BrainNumber current Brain Number
     * @param context context
     */
    public static void setRES(String[] RES, String BrainNumber, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/RES/" + BrainNumber + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter osw=new OutputStreamWriter(outStream, "UTF-8");
            BufferedWriter bw=new BufferedWriter(osw);

            for(String res:RES){
                bw.write(res+"\n");
            }

            bw.close();
            osw.close();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static String getFilename_Remote_Check(Context context){
        String filename = null;

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Check/Remote_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get filename", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                filename = line;
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get filename", filename);
        return filename;
    }


    /**
     * put the ip address you input to local file
     * @param filename the ip address currently input
     */
    public static void setFilename_Remote_Check(String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Check/Remote_filename.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set filename", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(filename.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getoffset_Remote_Check(Context context, String filename){
        String offset = null;

        String offset_x_1 = "1";
        String offset_y_1 = "1";
        String offset_z_1 = "1";

        String offset_x_2 = "129";
        String offset_y_2 = "129";
        String offset_z_2 = "129";

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Check/Offset/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = offset_x_1 + ";" + offset_x_2 + ";" + offset_y_1
                        + ";" + offset_y_2 + ";" + offset_z_1 + ";" + offset_z_2;

                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                offset = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get offset", offset);
        return offset;
    }

    /**
     * put the offset you input to local file
     * @param offset the offset currently input
     */
    public static void setoffset_Remote_Check(String offset, String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Check/Offset/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(offset.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }





    /**
     * get the ip address from local file
     * @return ip latest address you input
     */
    public static Vector<String> getArbor_List__Check(Context context){
        Vector<String> Arbor_List = new Vector<>();

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Check/Arbor_List.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get filename", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                while ((line = buffreader.readLine()) != null){
                    Arbor_List.add(line);
                }
                buffreader.close();
                inputreader.close();
                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Log.v("get Arbor_List", filename);
        return Arbor_List;
    }



    /**
     * Set the Arbor_List
     * @param context context
     */
    public static void setArbor_List_Check(String[] Arbor_List, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Config/Check/Arbor_List.txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get arbor list", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter osw=new OutputStreamWriter(outStream, "UTF-8");
            BufferedWriter bw=new BufferedWriter(osw);

            for(String arbor:Arbor_List){
                bw.write(arbor+"\n");
            }

            bw.close();
            osw.close();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getBoundingBox(Context context, String filename){
        String offset = "";

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Check/BoundingBox/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";

                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                offset = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("get offset", offset);
        return offset;
    }

    /**
     * put the offset you input to local file
     * @param boundingbox the offset currently input
     */
    public static void setBoundingBox(String boundingbox, String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Check/BoundingBox/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get boundingbox", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(boundingbox.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getArborNum(Context context, String filename){
        String arbor_num = "";

        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Check/ArborNum/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";

                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                arbor_num = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("get arbor_num", arbor_num);
        return arbor_num;
    }

    /**
     * put the offset you input to local file
     * @param ArborNum the offset currently input
     */
    public static void setArborNum(String ArborNum, String filename, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/Check/ArborNum/" + filename + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("get ArborNum", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(ArborNum.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getSelectSource(Context context){
        String source = "";


        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/SelectSource" + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "--11--";

                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                source = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        Log.v("get source", source);
        return source;
    }




    /**
     * put the offset you input to local file
     * @param source the offset currently input
     */
    public static void setSelectSource(String source, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/SelectSource" + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set SelectSource", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(source.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get the offset from local file
     * @return offset latest address you input
     */
    public static String getContrast(Context context){
        String source = "";


        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Settings/Contrast" + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();

                String str = "0";

                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(str.getBytes());
                outStream.close();

            }catch (Exception e){
                Log.v("get offset", "Fail to create file");
                e.printStackTrace();
            }
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";

                line = buffreader.readLine();
                source = line;

                inputStream.close();//关闭输入流
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
        Log.v("get source", source);
        return source;
    }




    /**
     * put the offset you input to local file
     * @param contrast the contrast currently input
     */
    public static void setContrast(String contrast, Context context){
        String filepath = context.getExternalFilesDir(null).toString();
        File file = new File(filepath + "/config/Settings/Contrast" + ".txt");
        if (!file.exists()){
            try {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }catch (Exception e){
                Log.v("set SelectSource", "Fail to create file");
            }
        }

        try {

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(contrast.getBytes());
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
