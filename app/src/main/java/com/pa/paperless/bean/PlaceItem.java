package com.pa.paperless.bean;

import com.google.protobuf.ByteString;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/25.
 */

public class PlaceItem implements Serializable {
    int placeID;
    int roombgpicid;
    int managerid;
    ByteString name;
    ByteString addr;
    ByteString comment;

    public int getPlaceID() {
        return placeID;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }

    public int getRoombgpicid() {
        return roombgpicid;
    }

    public void setRoombgpicid(int roombgpicid) {
        this.roombgpicid = roombgpicid;
    }

    public int getManagerid() {
        return managerid;
    }

    public void setManagerid(int managerid) {
        this.managerid = managerid;
    }

    public ByteString getName() {
        return name;
    }

    public void setName(ByteString name) {
        this.name = name;
    }

    public ByteString getAddr() {
        return addr;
    }

    public void setAddr(ByteString addr) {
        this.addr = addr;
    }

    public ByteString getComment() {
        return comment;
    }

    public void setComment(ByteString comment) {
        this.comment = comment;
    }

    public PlaceItem(int placeID, int roombgpicid, int managerid, ByteString name, ByteString addr, ByteString comment) {

        this.placeID = placeID;
        this.roombgpicid = roombgpicid;
        this.managerid = managerid;
        this.name = name;
        this.addr = addr;
        this.comment = comment;
    }
}
