package com.pa.paperless.bean;

/**
 * Created by Administrator on 2017/11/7.
 */

public class ChatBean {
    String name;
    int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChatBean(String name, int id) {

        this.name = name;
        this.id = id;
    }
}
