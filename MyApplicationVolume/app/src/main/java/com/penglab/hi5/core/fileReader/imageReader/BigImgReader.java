package com.penglab.hi5.core.fileReader.imageReader;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.penglab.hi5.basic.image.Image4DSimple;
import com.penglab.hi5.dataStore.SettingFileManager;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.R;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.carbs.android.library.MDDialog;

import static com.penglab.hi5.dataStore.SettingFileManager.getFilename_Local;
import static com.penglab.hi5.dataStore.SettingFileManager.getoffset_Local;
import static com.penglab.hi5.dataStore.SettingFileManager.setoffset_Local;
import static com.penglab.hi5.core.MainActivity.Toast_in_Thread_static;
import static com.penglab.hi5.core.MainActivity.setFileName;

public class BigImgReader {

    public static final String BIG_LOCAL_FILE_PATH = "LOCAL_FILEPATH";

    public BigImgReader(){

    }

    public Image4DSimple loadRawRegion(long length , InputStream is,
                              int start_x, int start_y, int start_z, int end_x, int end_y, int end_z){

        // Read in the header values...
        String formatkey = "raw_image_stack_by_hpeng";
        try {
            FileInputStream fis = (FileInputStream)(is);
            BufferedInputStream fid = new BufferedInputStream(fis);

            int lenkey = formatkey.length();
            long fileSize = length;

//            fid.mark(2 * (end_x - start_x) + lenkey+2+4*4+1 );

            //read the format key
            if (fileSize<lenkey+2+4*4+1) // datatype has 2 bytes, and sz has 4*4 bytes and endian flag has 1 byte.
                throw new Exception("The size of your input file is too small and is not correct, -- it is too small to contain the legal header.");
            byte[] by = new byte[lenkey];
            long nread = fid.read(by);
            String keyread = new String(by);
            if (nread!=lenkey)
                throw new Exception("File unrecognized or corrupted file.");
            if (!keyread.equals(formatkey))
                throw new Exception("Unrecognized file format.");
            by = null;

            //read the endianness
            by = new byte[1];
            fid.read(by);
//            System.out.println((char)(by[0]));
            if (by[0]!='B' && by[0]!='L')
                throw new Exception("This program only supports big- or little- endian but not other format. Check your endian.");

            boolean isBig = (by[0]=='B');
            by = null;

            //read the data type info
            by = new byte[2];
            fid.read(by);
            short dcode = (short)bytes2int(by,isBig);
            int datatype;
            switch (dcode){
                case 1:
                    datatype = 1;  /* temporarily I use the same number, which indicates the number of bytes for each data point (pixel). This can be extended in the future. */
                    break;
                case 2:
                    datatype = 2;
                    break;
                case 4:
                    datatype = 4;
                    break;
                default:
                    throw new Exception("Unrecognized datatype code"+dcode+". The file is incorrect or this code is not supported in this version");
            }
            int unitSize = datatype;
            by = null;

            //read the data size info (the data size is stored in either 2-byte or 4-byte space)
            long[] sz = new long[4];
            long totalUnit = 1;

            //first assume this is a 2-byte file
            by = new byte[2];
            for (int i=0;i<4;i++)
            {
                fid.read(by);
                sz[i] = bytes2int(by,isBig);
                totalUnit *= sz[i];
            }
            by = null;

//            for (int i = 0; i < 4; i++){
//                System.out.println(sz[i]);
//            }
//            System.out.println(totalUnit);

            if ((totalUnit*unitSize+4*2+2+1+lenkey) != fileSize)
            {
                //see if this is a 4-byte file
                if (isBig)  {
                    sz[0] = sz[0]*64+sz[1];
                    sz[1] = sz[2]*64+sz[3];
                }
                else {
                    sz[0] = sz[1]*64+sz[0];
                    sz[1] = sz[3]*64+sz[2];
                }
                by = new byte[4];
                for (int i=2;i<4;i++)
                {
                    fid.read(by);
                    sz[i] = bytes2int(by,isBig);
                }
                totalUnit = 1;
                for (int i=0;i<4;i++)
                    totalUnit *= sz[i];

                if ((totalUnit*unitSize+4*4+2+1+lenkey) != fileSize)
                    throw new Exception("The input file has a size different from what specified in the header. Exit.");
            }

            // calculate the size of block
            int tmpw = end_x - start_x;
            int tmph = end_y - start_y;
            int tmpz = end_z - start_z;

            int head = 4*4+2+1+lenkey;
            long pgsz1 = sz[2]*sz[1]*sz[0], pgsz2 = sz[1]*sz[0], pgsz3 = sz[0];
            int cn = tmpw*tmph*tmpz;
            int kn = tmpw*tmph;
            int total = tmpw * tmph * tmpz * (int)sz[3];

            int[] region_sz = new int[4];
            region_sz[0] = tmpw;
            region_sz[1] = tmph;
            region_sz[2] = tmpz;
            region_sz[3] = (int)sz[3];
            int totalBytes = total * unitSize;
            byte [] data = new byte[totalBytes];

            if (fid.markSupported()){
                System.out.println("------ Mark Supported ------");
            }else {
                System.out.println("------ Mark notSupported ------");
            }

            fid.mark((int)totalUnit);
            //read the pixel info
            int c,j,k;
            int count = 0;
            for (c = 0; c < sz[3]; c++)
            {
                for (k = start_z; k < end_z; k++)
                {
                    for (j = start_y; j< end_y; j++)
                    {
                        long skip = fid.skip((c*pgsz1 + k*pgsz2 + j*pgsz3 + start_x)*unitSize);
                        int read_len = tmpw;
                        int read_len_suc = fid.read(data, count, read_len);
                        fid.reset();
                        count += read_len_suc;
                    }
                }
            }


            fid.close();
            fis.close();
            Image4DSimple image = new Image4DSimple();
            Image4DSimple.ImagePixelType dt;
            switch (datatype){
                case 1:
                    dt = Image4DSimple.ImagePixelType.V3D_UINT8;
                    break;
                case 2:
                    dt = Image4DSimple.ImagePixelType.V3D_UINT16;
                    break;
                case 4:
                    dt = Image4DSimple.ImagePixelType.V3D_FLOAT32;
                    break;
                default:
                    dt = Image4DSimple.ImagePixelType.V3D_UNKNOWN;
            }
            image.setDataFromImage(data,region_sz[0],region_sz[1],region_sz[2],region_sz[3],dt, isBig);

            if (isBig){
                System.out.println("Image4DSimple isBig");
            }else {
                System.out.println("Image4DSimple isSmall");
            }

            return image;

        } catch (OutOfMemoryError e){

            System.out.println("Raw reader :" + e.toString() + "error!!!");
            return null;


        }catch ( Exception e ) {
            System.out.println("Raw reader :" + e.toString() + "error!!!");
            return null;
        }

    }

    public String[] ChooseFile(Context context) {
        String server_path = "/storage/emulated/0/C3/Server";
        File path = new File(server_path);
        if (!path.exists()) {
            if (!path.mkdirs()){
                Toast_in_Thread_static("Fail to create the folder!");
            }
        }

        File[] files = path.listFiles();
        if (files == null) {
            Log.e("error", "空目录");
            Toast_in_Thread_static("There is no file in the server folder");
            return null;
        }

        List<String> s = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String[] filename = files[i].getAbsolutePath().split("/");
            String filename_split = filename[filename.length - 1];
            String substring = filename_split.substring(0, filename_split.length() - 7);
            System.out.println("---" + substring + "---");
            s.add(substring);
        }
        String[] filename_list = new String[s.size()];
        for (int i = 0; i < s.size(); i++)
            filename_list[i] = s.get(i);

        return filename_list;
    }

    /**
     * choose the file you want down
     * @param context the activity context
     * @param items the list of filename
     */
    public void ShowListDialog(final Context context, final String[] items) {

        new XPopup.Builder(context)
                .asCenterList("Select the File", items, new OnSelectListener(){

                    @Override
                    public void onSelect(int position, String text) {
                        Toast_in_Thread_static("You choose " + text);
                        setFileName(text);
                        SettingFileManager.setFilename_Local(text, context);
                        PopUp(context);
                    }
                }).show();

    }


    public int[] PopUp(Context context){
        final int[][] index = {null};

        if (getFilename_Local(context).equals("--11--")){
            Toast_in_Thread_static("Select file first!");
            return null;
        }

        new MDDialog.Builder(context)
//              .setContentView(customizedView)
                .setContentView(R.layout.image_bais_select)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {//这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);
                        EditText et4 = (EditText) contentView.findViewById(R.id.edit4);

                        String filename = getFilename_Local(context);
                        String offset = getoffset_Local(context, filename);

                        String offset_x = offset.split("_")[0];
                        String offset_y = offset.split("_")[1];
                        String offset_z = offset.split("_")[2];
                        String size     = offset.split("_")[3];


                        et1.setText(offset_x);
                        et2.setText(offset_y);
                        et3.setText(offset_z);
                        et4.setText(size);

                    }
                })
                .setTitle("Input the offset")
                .setNegativeButton("CANCEL",new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton("CONFIRM",new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        //这里的contentView就是上面代码中传入的自定义的View或者layout资源inflate出来的view，目的是方便在确定/取消按键中对contentView进行操作，如获取数据等。
                        EditText et1 = (EditText) contentView.findViewById(R.id.edit1);
                        EditText et2 = (EditText) contentView.findViewById(R.id.edit2);
                        EditText et3 = (EditText) contentView.findViewById(R.id.edit3);
                        EditText et4 = (EditText) contentView.findViewById(R.id.edit4);

                        String offset_x   = et1.getText().toString();
                        String offset_y   = et2.getText().toString();
                        String offset_z   = et3.getText().toString();
                        String size       = et4.getText().toString();

                        if( !offset_x.isEmpty() && !offset_y.isEmpty() && !offset_z.isEmpty() && !size.isEmpty()){

                            String offset = offset_x + "_" + offset_y + "_" + offset_z + "_" + size;

                            String filename = getFilename_Local(context);
                            setoffset_Local(offset, filename, context);

                            String[] input = JudgeEven(offset_x, offset_y, offset_z, size);

                            if (!JudgeBounding(input, context)){
                                PopUp(context);
                                Toast_in_Thread_static("Please make sure all the information is right!!!");
                            }else {
                                index[0] = new int[4];
                                index[0] = new int[]{Integer.parseInt(input[0]),
                                                     Integer.parseInt(input[1]),
                                                     Integer.parseInt(input[2]),
                                                     Integer.parseInt(input[3])};

                                offset = input[0] + "_" + input[1] + "_" + input[2] + "_" + input[3];
                                setoffset_Local(offset, filename, context);

                                MainActivity.LoadBigFile_Local("/storage/emulated/0/C3/Server/" + filename + ".v3draw");

                            }

                        }else{
                            PopUp(context);
                            Toast_in_Thread_static("Please make sure all the information is right!!!");
                        }

                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setWidthMaxDp(600)
                .create()
                .show();

        return index[0];
    }


    private String[] JudgeEven(String offset_x, String offset_y, String offset_z, String size){

        float x_offset_f = Float.parseFloat(offset_x);
        float y_offset_f = Float.parseFloat(offset_y);
        float z_offset_f = Float.parseFloat(offset_z);
        float size_f     = Float.parseFloat(size);

        int x_offset_i = (int) x_offset_f;
        int y_offset_i = (int) y_offset_f;
        int z_offset_i = (int) z_offset_f;
        int size_i = (int) size_f;

        if (x_offset_i % 2 == 1)
            x_offset_i += 1;

        if (y_offset_i % 2 == 1)
            y_offset_i += 1;

        if (z_offset_i % 2 == 1)
            z_offset_i += 1;

        if (size_i % 2 == 1)
            size_i += 1;

        String[] result = new String[4];
        result[0] = Integer.toString(x_offset_i);
        result[1] = Integer.toString(y_offset_i);
        result[2] = Integer.toString(z_offset_i);
        result[3] = Integer.toString(size_i);

        Log.v("JudgeEven", Arrays.toString(result));

        return result;
    }

    private boolean JudgeBounding(String[] input, Context context){

        int x_offset_i = Integer.parseInt(input[0]);
        int y_offset_i = Integer.parseInt(input[1]);
        int z_offset_i = Integer.parseInt(input[2]);
        int size_i = Integer.parseInt(input[3]);

        String filename = getFilename_Local(context);
        String size = filename.split("RES")[1];

        System.out.println("hhh-------" + size + "--------hhh");

        int x_size = Integer.parseInt(size.split("x")[0]);
        int y_size = Integer.parseInt(size.split("x")[1]);
        int z_size = Integer.parseInt(size.split("x")[2]);



        if ( ( x_offset_i - size_i/2 < 0 ) || ( x_offset_i + size_i/2 > x_size -2) )
            return false;

        if ( ( y_offset_i - size_i/2 < 0 ) || ( y_offset_i + size_i/2 > y_size -2) )
            return false;

        if ( ( z_offset_i - size_i/2 < 0 ) || ( z_offset_i + size_i/2 > z_size -2) )
            return false;


        return true;
    }

    public int[] SelectBlock_fast(String direction, Context context){
        String filename = getFilename_Local(context);
        String offset = getoffset_Local(context, filename);

        String img_size_x = filename.split("RES")[1].split("x")[0];
        String img_size_y = filename.split("RES")[1].split("x")[1];
        String img_size_z = filename.split("RES")[1].split("x")[2];

        int img_size_x_i = Integer.parseInt(img_size_x);
        int img_size_y_i = Integer.parseInt(img_size_y);
        int img_size_z_i = Integer.parseInt(img_size_z);

        String offset_x = offset.split("_")[0];
        String offset_y = offset.split("_")[1];
        String offset_z = offset.split("_")[2];
        String size     = offset.split("_")[3];

        int offset_x_i = Integer.parseInt(offset_x);
        int offset_y_i = Integer.parseInt(offset_y);
        int offset_z_i = Integer.parseInt(offset_z);
        int size_i     = Integer.parseInt(size);

        switch (direction){
            case "Left":
                if ( (offset_x_i - size_i/2) == 0 ){
                    System.out.println("----- You have already reached left boundary!!! -----");
                    Toast_in_Thread_static("You have already reached left boundary!!!");
                    return null;
                }else {
                    offset_x_i -= size_i/2;
                    if (offset_x_i - size_i/2 < 0)
                        offset_x_i = size_i/2;
                }
                break;

            case "Right":
                if ( (offset_x_i + size_i/2) == img_size_x_i - 1 ){
                    Toast_in_Thread_static("You have already reached right boundary!!!");
                    return null;
                }else {
                    offset_x_i += size_i/2;
                    if (offset_x_i + size_i/2 > img_size_x_i - 1)
                        offset_x_i = img_size_x_i - 1 - size_i/2;
                }
                break;

            case "Top":
                if ( (offset_y_i - size_i/2) == 0 ){
                    Toast_in_Thread_static("You have already reached top boundary!!!");
                    return null;
                }else {
                    offset_y_i -= size_i/2;
                    if (offset_y_i - size_i/2 < 0)
                        offset_y_i = size_i/2;
                }
                break;

            case "Bottom":
                if ( (offset_y_i + size_i/2) == img_size_y_i - 1 ){
                    Toast_in_Thread_static("You have already reached bottom boundary!!!");
                    return null;
                }else {
                    offset_y_i += size_i/2;
                    if (offset_y_i + size_i/2 > img_size_y_i - 1)
                        offset_y_i = img_size_y_i - 1 - size_i/2;
                }
                break;

            case "Front":
                if ( (offset_z_i - size_i/2) == 0 ){
                    Toast_in_Thread_static("You have already reached front boundary!!!");
                    return null;
                }else {
                    offset_z_i -= size_i/2;
                    if (offset_z_i - size_i/2 < 0)
                        offset_z_i = size_i/2;
                }
                break;

            case "Back":
                if ( (offset_z_i + size_i/2) == img_size_z_i - 1 ){
                    Toast_in_Thread_static("You have already reached back boundary!!!");
                    return null;
                }else {
                    offset_z_i += size_i/2;
                    if (offset_z_i + size_i/2 > img_size_z_i - 1)
                        offset_z_i = img_size_z_i - 1 - size_i/2;
                }
                break;
            default:
                Log.e("BigImgReader","something wrong when SelectBlock_fast");
        }

        offset_x = Integer.toString(offset_x_i);
        offset_y = Integer.toString(offset_y_i);
        offset_z = Integer.toString(offset_z_i);
        size     = Integer.toString(size_i);
        offset = offset_x + "_" + offset_y + "_" + offset_z + "_" + size;

        SettingFileManager.setoffset_Local(offset, filename, context);

        int[] index = {offset_x_i - size_i/2, offset_y_i - size_i/2, offset_z_i - size_i/2,
                offset_x_i + size_i/2, offset_y_i + size_i/2, offset_z_i + size_i/2};

        return index;
    }

    public static int[] getIndex(String offset){
        String offset_x = offset.split("_")[0];
        String offset_y = offset.split("_")[1];
        String offset_z = offset.split("_")[2];
        String size     = offset.split("_")[3];

        int offset_x_i = Integer.parseInt(offset_x);
        int offset_y_i = Integer.parseInt(offset_y);
        int offset_z_i = Integer.parseInt(offset_z);
        int size_i     = Integer.parseInt(size);

        int[] index = {offset_x_i - size_i/2, offset_y_i - size_i/2, offset_z_i - size_i/2,
                       offset_x_i + size_i/2, offset_y_i + size_i/2, offset_z_i + size_i/2};

        return index;
    }


    public static final int bytes2int(byte[] b,boolean isBig)
    {
        int retVal = 0;
        if (!isBig)
            for (int i=b.length-1;i>=0;i--) {
                retVal = (retVal<<8) + (b[i] & 0xff);
            }
        else
            for (int i=0;i<b.length;i++) {
                retVal = (retVal<<8) + (b[i] & 0xff);
            }

        return retVal;
    }

}
