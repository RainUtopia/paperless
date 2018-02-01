package com.pa.paperless.bean;

/**
 * Created by Administrator on 2017/11/21.
 * 会议室信息
 */

public class RoomTopBean {
    int roomid;
    String RoomName;
    String RoomPlace;
    String RoomNote;

    public RoomTopBean(String roomName) {
        RoomName = roomName;
    }

    public int getNumber() {
        return roomid;
    }

    public void setNumber(int number) {
        this.roomid = number;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public String getRoomPlace() {
        return RoomPlace;
    }

    public void setRoomPlace(String roomPlace) {
        RoomPlace = roomPlace;
    }

    public String getRoomNote() {
        return RoomNote;
    }

    public void setRoomNote(String roomNote) {
        RoomNote = roomNote;
    }

    public RoomTopBean(int roid,String roomName, String roomPlace, String roomNote) {
        roomid = roid;
        RoomName = roomName;
        RoomPlace = roomPlace;
        RoomNote = roomNote;
    }
}
