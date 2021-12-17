package com.penglab.hi5.core.ui.annotation;

import java.util.ArrayList;
import java.util.List;

public class RoomInfoState {
    private List<String> userList = new ArrayList<>();
    private String roomName;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
