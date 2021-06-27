package com.penglab.hi5.dataStore.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Image extends LitePalSupport {

    @Column(unique = true, defaultValue = "unknown")
    private String curPath;

    @Column(nullable = false)
    private int curRes;

    @Column(nullable = false, defaultValue = "unkown")
    private String curPos;                                 // x;y;z;size





    public String getCurPath() {
        return curPath;
    }

    public void setCurPath(String curPath) {
        this.curPath = curPath;
    }

    public int getCurRes() {
        return curRes;
    }

    public void setCurRes(int curRes) {
        this.curRes = curRes;
    }

    public String getCurPos() {
        return curPos;
    }

    public void setCurPos(String curPos) {
        this.curPos = curPos;
    }
}
