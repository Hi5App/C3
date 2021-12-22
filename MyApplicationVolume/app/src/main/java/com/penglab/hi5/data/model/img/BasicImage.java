package com.penglab.hi5.data.model.img;

import com.penglab.hi5.basic.image.Image4DSimple;

/**
 * Created by Jackiexing on 12/22/21
 */
public class BasicImage extends BasicFile{

    private Image4DSimple image4DSimple;

    public BasicImage() {
    }

    public BasicImage(String fileName, FilePath filePath, FileType fileType) {
        super(fileName, filePath, fileType);
    }

    public BasicImage(String fileName, FilePath filePath, FileType fileType, Image4DSimple image4DSimple) {
        super(fileName, filePath, fileType);
        this.image4DSimple = image4DSimple;
    }

    public Image4DSimple getImage4DSimple() {
        return image4DSimple;
    }

    public void setImage4DSimple(Image4DSimple image4DSimple) {
        this.image4DSimple = image4DSimple;
    }

    public void setFileInfo(String fileName, FilePath filePath, FileType fileType){
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }
}
