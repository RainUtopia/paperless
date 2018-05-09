package com.pa.paperless.bean;

import com.mogujie.tt.protobuf.InterfaceVideo;

/**
 * Created by Administrator on 2018/5/7.
 */

public class VideoInfo {
    InterfaceVideo.pbui_Item_MeetVideoDetailInfo VideoInfo;
    String name;

    public VideoInfo(InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoInfo, String name) {
        VideoInfo = videoInfo;
        this.name = name;
    }

    public VideoInfo(InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoInfo) {
        VideoInfo = videoInfo;
    }

    public InterfaceVideo.pbui_Item_MeetVideoDetailInfo getVideoInfo() {
        return VideoInfo;
    }

    public void setVideoInfo(InterfaceVideo.pbui_Item_MeetVideoDetailInfo videoInfo) {
        VideoInfo = videoInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
