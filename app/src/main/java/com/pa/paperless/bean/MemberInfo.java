package com.pa.paperless.bean;

/**
 * Created by Administrator on 2018/1/18.
 *  参会人信息
 */

public class MemberInfo {
    int personid;
    String name;
    String company;
    String job;
    String comment;
    String phone;
    String email;
    String password;

    public MemberInfo(int personid, String name, String company, String job, String comment, String phone, String email, String password) {
        this.personid = personid;
        this.name = name;
        this.company = company;
        this.job = job;
        this.comment = comment;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public int getPersonid() {
        return personid;
    }

    public void setPersonid(int personid) {
        this.personid = personid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
