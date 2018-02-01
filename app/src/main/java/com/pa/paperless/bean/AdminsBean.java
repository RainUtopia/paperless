package com.pa.paperless.bean;

/**
 * Created by Administrator on 2017/11/22.
 * 管理员信息
 */

public class AdminsBean {
    String user;
    String password;
    String note;

    public AdminsBean(String user) {
        this.user = user;
    }

    public AdminsBean(String user, String password, String note) {
        this.user = user;
        this.password = password;
        this.note = note;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
