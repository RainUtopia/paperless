package com.pa.paperless.bean;



import java.util.List;

/**
 * Created by Administrator on 2018/1/20.
 */

public class VoteInfo {
    int voteid;         //id
    String content;     //投票内容
    int maintype;       // 类别 投票 选举 调查问卷 参见 Pb_MeetVoteType
    int mode;           //匿名投票 记名投票 参见 Pb_MeetVoteMode
    int type;           //多选 单选 参见 Pb_MeetVote_SelType
    int votestate;      //投票状态 参见 Pb_MeetVoteStatus
    int timeouts;       //超时值
    int selectcount;    //有效选项
    List<VoteOptionsInfo> optionInfo;   //选项描述文字

    public VoteInfo(int voteid, String content, int maintype, int mode, int type, int votestate, int timeouts, int selectcount, List<VoteOptionsInfo> optionInfo) {
        this.voteid = voteid;
        this.content = content;
        this.maintype = maintype;
        this.mode = mode;
        this.type = type;
        this.votestate = votestate;
        this.timeouts = timeouts;
        this.selectcount = selectcount;
        this.optionInfo = optionInfo;
    }

    public int getVoteid() {
        return voteid;
    }

    public void setVoteid(int voteid) {
        this.voteid = voteid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMaintype() {
        return maintype;
    }

    public void setMaintype(int maintype) {
        this.maintype = maintype;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVotestate() {
        return votestate;
    }

    public void setVotestate(int votestate) {
        this.votestate = votestate;
    }

    public int getTimeouts() {
        return timeouts;
    }

    public void setTimeouts(int timeouts) {
        this.timeouts = timeouts;
    }

    public int getSelectcount() {
        return selectcount;
    }

    public void setSelectcount(int selectcount) {
        this.selectcount = selectcount;
    }

    public List<VoteOptionsInfo> getOptionInfo() {
        return optionInfo;
    }

    public void setOptionInfo(List<VoteOptionsInfo> optionInfo) {
        this.optionInfo = optionInfo;
    }
}
