package com.pa.paperless.bean;

import java.util.List;

/**
 * 提交投票结果
 * Created by Administrator on 2018/3/2.
 */

public class SubmitVoteBean {
    int voteid;
    int selcnt;
    int selectItem;

    public int getVoteid() {
        return voteid;
    }

    public void setVoteid(int voteid) {
        this.voteid = voteid;
    }

    public int getSelcnt() {
        return selcnt;
    }

    public void setSelcnt(int selcnt) {
        this.selcnt = selcnt;
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public SubmitVoteBean(int voteid, int selcnt, int selectItem) {

        this.voteid = voteid;
        this.selcnt = selcnt;
        this.selectItem = selectItem;
    }
}
