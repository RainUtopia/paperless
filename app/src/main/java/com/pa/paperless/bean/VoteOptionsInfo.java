package com.pa.paperless.bean;

/**
 * Created by Administrator on 2018/1/20.
 */

public class VoteOptionsInfo {
    String text;   //投票选项的文本
    int selcnt; //投票数

    public VoteOptionsInfo(String text, int selcnt) {
        this.text = text;
        this.selcnt = selcnt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getSelcnt() {
        return selcnt;
    }

    public void setSelcnt(int selcnt) {
        this.selcnt = selcnt;
    }
}
