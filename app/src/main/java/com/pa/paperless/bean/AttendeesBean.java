package com.pa.paperless.bean;

/**
 * Created by Administrator on 2017/11/25.
 * 会前准备-参会人
 */

public class AttendeesBean  {
    String name;
    String unit;
    String job;
    String type;
    String signPassword;
    String note;

    public AttendeesBean(String name) {
        this.name = name;
    }

    public AttendeesBean(String name, String unit, String job, String note) {
        this.name = name;
        this.unit = unit;
        this.job = job;
        this.note = note;
    }

    public AttendeesBean(String name, String unit, String job, String type, String signPassword, String note) {
        this.name = name;
        this.unit = unit;
        this.job = job;
        this.type = type;
        this.signPassword = signPassword;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSignPassword() {
        return signPassword;
    }

    public void setSignPassword(String signPassword) {
        this.signPassword = signPassword;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
