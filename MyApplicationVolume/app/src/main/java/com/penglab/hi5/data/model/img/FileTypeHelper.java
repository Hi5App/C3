package com.penglab.hi5.data.model.img;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jackiexing on 12/22/21
 */
public class FileTypeHelper {

    public static Map<String, FileType> fileTypeMap = new HashMap<String, FileType>() {{
        put(".V3DRAW", FileType.V3DRAW);
        put(".V3DPBD", FileType.V3DPBD);
        put(".TIF", FileType.TIFF);
        put(".TIFF", FileType.TIFF);
        put(".PNG", FileType.PNG);
        put(".JPG", FileType.JPG);

        put(".ANO", FileType.ANO);
        put(".SWC", FileType.SWC);
        put(".ESWC", FileType.ESWC);
        put(".APO", FileType.APO);
    }};

    public static FileType getType(String fileName){
        FileType fileType = fileTypeMap.get(fileName.substring(fileName.lastIndexOf(".")).toUpperCase());
        if (fileType == null){
            return FileType.UNSUPPORTED;
        }
        return fileType;
    }

}