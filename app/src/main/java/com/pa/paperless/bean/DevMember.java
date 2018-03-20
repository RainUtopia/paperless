package com.pa.paperless.bean;


/**
 * Created by Administrator on 2018/3/8.
 */

public class DevMember {
    MemberInfo memberInfos;
    int devId;

    public MemberInfo getMemberInfos() {
        return memberInfos;
    }

    public void setMemberInfos(MemberInfo memberInfos) {
        this.memberInfos = memberInfos;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public DevMember(MemberInfo memberInfos, int devId) {

        this.memberInfos = memberInfos;
        this.devId = devId;
    }
}
