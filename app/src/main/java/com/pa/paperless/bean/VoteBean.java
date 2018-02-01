package com.pa.paperless.bean;

/**
 * Created by Administrator on 2017/11/14.
 */

public class VoteBean {
    String name;
    String type1;//    多选/单选
    String type2;//    记名/不记名
    int Ycount;
    int Ncount;
    int Wcount;


    public VoteBean(String name, String type1, String type2, int ycount, int ncount, int wcount) {
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        Ycount = ycount;
        Ncount = ncount;
        Wcount = wcount;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type) {
        this.type1 = type;
    }

    public int getYcount() {
        return Ycount;
    }

    public void setYcount(int ycount) {
        Ycount = ycount;
    }

    public int getNcount() {
        return Ncount;
    }

    public void setNcount(int ncount) {
        Ncount = ncount;
    }

    public int getWcount() {
        return Wcount;
    }

    public void setWcount(int wcount) {
        Wcount = wcount;
    }
}
