package com.pa.paperless.bean;


/**
 * Created by Administrator on 2018/1/4.
 * 会议议程-公告文本
 */

public class NoticeBean {
    int time;
    String content;

    public NoticeBean(int time, String content) {
        this.time = time;
        this.content = content;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
