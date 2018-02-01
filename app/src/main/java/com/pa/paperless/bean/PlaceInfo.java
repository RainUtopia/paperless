package com.pa.paperless.bean;

/**
 * Created by Administrator on 2018/1/24.
 */

public class PlaceInfo {
    int roomId;     //会场ID
    int roombgPicId;//会议底图ID
    int managerid;  //管理员ID
    String name;    //会场名称
    String addr;    //会场地址
    String comment; //备注

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoombgPicId() {
        return roombgPicId;
    }

    public void setRoombgPicId(int roombgPicId) {
        this.roombgPicId = roombgPicId;
    }

    public int getManagerid() {
        return managerid;
    }

    public void setManagerid(int managerid) {
        this.managerid = managerid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PlaceInfo(String name, String addr, String comment) {
        this.name = name;
        this.addr = addr;
        this.comment = comment;
    }

    public PlaceInfo(int roomId, int roombgPicId, int managerid, String name, String addr, String comment) {

        this.roomId = roomId;
        this.roombgPicId = roombgPicId;
        this.managerid = managerid;
        this.name = name;
        this.addr = addr;
        this.comment = comment;
    }
}
