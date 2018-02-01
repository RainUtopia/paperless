package com.pa.paperless.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/1/22.
 * 接收到的会议交流信息
 */

public class ReceiveMeetIMInfo {
    int msgtype;
    int role;
    int memberid;
    String msg;
    long utcsecond;
    boolean type;
    String name;
    List<String> names;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public ReceiveMeetIMInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public int getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(int msgtype) {
        this.msgtype = msgtype;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getMemberid() {
        return memberid;
    }

    public void setMemberid(int memberid) {
        this.memberid = memberid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getUtcsecond() {
        return utcsecond;
    }

    public void setUtcsecond(long utcsecond) {
        this.utcsecond = utcsecond;
    }

    public ReceiveMeetIMInfo(int msgtype, int role, int memberid, String msg, long utcsecond) {
        this.msgtype = msgtype;
        this.role = role;
        this.memberid = memberid;
        this.msg = msg;
        this.utcsecond = utcsecond;
    }
}
