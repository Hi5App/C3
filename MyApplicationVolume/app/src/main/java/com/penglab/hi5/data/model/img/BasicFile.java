package com.penglab.hi5.data.model.img;

/**
 * Created by Jackiexing on 12/18/21
 */
public class BasicFile {

    String fileName;

    FilePath filePath;

    FileType fileType;

    public BasicFile() {
    }

    public BasicFile(String fileName, FilePath filePath, FileType fileType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FilePath getFilePath() {
        return filePath;
    }

    public void setFilePath(FilePath filePath) {
        this.filePath = filePath;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public void setFileInfo(String fileName, FilePath filePath, FileType fileType){
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }
}
