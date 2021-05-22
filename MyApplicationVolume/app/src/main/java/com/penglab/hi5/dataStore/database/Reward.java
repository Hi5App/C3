package com.penglab.hi5.dataStore.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Reward extends LitePalSupport {
    @Column(defaultValue = "0")
    private int num;

    @Column(defaultValue = "0")
    private int received;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }
}
