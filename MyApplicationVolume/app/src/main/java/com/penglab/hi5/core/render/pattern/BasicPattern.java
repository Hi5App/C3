package com.penglab.hi5.core.render.pattern;

/**
* Created by Jackiexing on 12/25/21
*/

abstract public class BasicPattern {

    protected boolean needSetContent = false;
    protected boolean needDraw = false;
    protected boolean needReleaseMemory = false;

    protected void releaseMemory(){
        needReleaseMemory = false;
    };

    public boolean isNeedSetContent() {
        return needSetContent;
    }

    public void setNeedSetContent(boolean needSetContent) {
        this.needSetContent = needSetContent;
    }

    public boolean isNeedDraw() {
        return needDraw;
    }

    public void setNeedDraw(boolean needDraw) {
        this.needDraw = needDraw;
    }

    public boolean isNeedReleaseMemory() {
        return needReleaseMemory;
    }

    public void setNeedReleaseMemory(boolean needReleaseMemory) {
        this.needReleaseMemory = needReleaseMemory;
        this.needDraw = false;
    }
}
