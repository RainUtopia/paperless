package com.pa.paperless.bean;

/**
 * Created by Administrator on 2017/11/13.
 */

public class ChatRlBean {
    String sendName;
    String[] receiveName;
    String date;
    String content;

    public ChatRlBean(String sendName, String[] receiveName, String date, String content) {
        this.sendName = sendName;
        this.receiveName = receiveName;
        this.date = date;
        this.content = content;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String[] getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String[] receiveName) {
        this.receiveName = receiveName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
