package com.pa.paperless.bean;

/**
 * Created by Administrator on 2018/1/24.
 */

public class AdminInfo {
    int adminid;
    String adminname;
    String pw;
    String comment;
    String phone;
    String email;

    public int getAdminid() {
        return adminid;
    }

    public void setAdminid(int adminid) {
        this.adminid = adminid;
    }

    public String getAdminname() {
        return adminname;
    }

    public void setAdminname(String adminname) {
        this.adminname = adminname;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AdminInfo(int adminid, String adminname, String pw, String comment, String phone, String email) {

        this.adminid = adminid;
        this.adminname = adminname;
        this.pw = pw;
        this.comment = comment;
        this.phone = phone;
        this.email = email;
    }
}
