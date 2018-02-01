package com.pa.paperless.bean;


/**
 * Created by Administrator on 2018/1/4.
 *  会议议程-议程文本bean
 */

public class AgendContext {
    String time;
    String text;

    public AgendContext(String time, String text) {
        this.time = time;
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
