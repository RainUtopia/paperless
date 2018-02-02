package com.pa.paperless.bean;

/**
 * Created by Administrator on 2018/2/1.
 * 同屏控制弹出框数据
 */

public class ScreenControlBean {
    int id;
    String name;

    public ScreenControlBean() {
    }

    public ScreenControlBean(String name) {
        this.name = name;
    }

    public ScreenControlBean(int id) {

        this.id = id;
    }

    public ScreenControlBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
