package com.pa.paperless.bean;


/**
 * Created by Administrator on 2017/11/23.
 */

public class MeetManageBean {
    String meetName;
    String meetState;
    String meetRoomName;
    String secret;
    String startTime;
    String overTime;

    public String getMeetName() {
        return meetName;
    }

    public void setMeetName(String meetName) {
        this.meetName = meetName;
    }

    public String getMeetState() {
        return meetState;
    }

    public void setMeetState(String meetState) {
        this.meetState = meetState;
    }

    public String getMeetRoomName() {
        return meetRoomName;
    }

    public void setMeetRoomName(String meetRoomName) {
        this.meetRoomName = meetRoomName;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getOverTime() {
        return overTime;
    }

    public void setOverTime(String overTime) {
        this.overTime = overTime;
    }

    public MeetManageBean(String meetName, String meetState, String meetRoomName, String secret, String startTime, String overTime) {
        this.meetName = meetName;
        this.meetState = meetState;
        this.meetRoomName = meetRoomName;
        this.secret = secret;
        this.startTime = startTime;
        this.overTime = overTime;
    }
}
