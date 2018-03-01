package com.pa.paperless.bean;

/**
 * Created by Administrator on 2018/3/1.
 */

public class MeetDirBean {
    String dirName;
    int dirId;
    int parentid;

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public int getDirId() {
        return dirId;
    }

    public void setDirId(int dirId) {
        this.dirId = dirId;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public MeetDirBean(String dirName, int dirId, int parentid) {

        this.dirName = dirName;
        this.dirId = dirId;
        this.parentid = parentid;
    }

    public MeetDirBean(String dirName) {

        this.dirName = dirName;
    }
}
