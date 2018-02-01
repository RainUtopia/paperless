package com.pa.paperless.bean;

/**
 * Created by Administrator on 2018/1/24.
 */

public class ResInfo {
    int playstatus;
    int triggerId;
    int val;
    int val2;

    public int getPlaystatus() {
        return playstatus;
    }

    public void setPlaystatus(int playstatus) {
        this.playstatus = playstatus;
    }

    public int getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(int triggerId) {
        this.triggerId = triggerId;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public int getVal2() {
        return val2;
    }

    public void setVal2(int val2) {
        this.val2 = val2;
    }

    public ResInfo(int playstatus, int triggerId, int val, int val2) {

        this.playstatus = playstatus;
        this.triggerId = triggerId;
        this.val = val;
        this.val2 = val2;
    }
}
