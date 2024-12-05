package com.penglab.hi5.core.ui.ImageClassify;

public class RatingImageInfo {
    public String ImageName;
    // 本质是一个时间戳
    public java.time.Instant Instant;
    public boolean IsDownloadCompleted;
    public String LocalImageFile;
    public boolean IsDownloading;
    public boolean DownloadFailed;
}
