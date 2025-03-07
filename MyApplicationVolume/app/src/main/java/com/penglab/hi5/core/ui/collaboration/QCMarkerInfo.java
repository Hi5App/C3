package com.penglab.hi5.core.ui.collaboration;

public class QCMarkerInfo {
    private String color;
    private String type;
    private String state;

    public QCMarkerInfo(String color, String type, String state) {
        this.color = color;
        this.type = type;
        this.state = state;
    }

    public String getColor() {
        return color;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setState(String state) {
        this.state = state;
    }
}

