package com.pa.paperless.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/1/24.
 */

public class DeviceInfo {
    int devId;
    String devName;
    List<IpInfo> ipInfo;
    int netState;
    List<ResInfo> resInfo;
    int faceState;
    int memberId;
    int meetingId;

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public List<IpInfo> getIpInfo() {
        return ipInfo;
    }

    public void setIpInfo(List<IpInfo> ipInfo) {
        this.ipInfo = ipInfo;
    }

    public int getNetState() {
        return netState;
    }

    public void setNetState(int netState) {
        this.netState = netState;
    }

    public List<ResInfo> getResInfo() {
        return resInfo;
    }

    public void setResInfo(List<ResInfo> resInfo) {
        this.resInfo = resInfo;
    }

    public int getFaceState() {
        return faceState;
    }

    public void setFaceState(int faceState) {
        this.faceState = faceState;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

    public DeviceInfo(int devId, String devName, List<IpInfo> ipInfo, int netState, List<ResInfo> resInfo, int faceState, int memberId, int meetingId) {

        this.devId = devId;
        this.devName = devName;
        this.ipInfo = ipInfo;
        this.netState = netState;
        this.resInfo = resInfo;
        this.faceState = faceState;
        this.memberId = memberId;
        this.meetingId = meetingId;
    }
}
