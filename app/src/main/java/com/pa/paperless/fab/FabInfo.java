package com.pa.paperless.fab;

import com.pa.paperless.bean.DevMember;
import com.pa.paperless.bean.DeviceInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/5/31.
 * 悬浮窗 参会人和投影机信息
 */

public class FabInfo {
    List<DevMember> onLineMembers;
    List<DeviceInfo> allProjectors;
    List<DeviceInfo> onLineProjectors;
    List<Integer> onlineClientIds;

    public FabInfo(List<DevMember> onLineMembers, List<DeviceInfo> allProjectors, List<DeviceInfo> onLineProjectors, List<Integer> onlineClientIds) {
        this.onLineMembers = onLineMembers;
        this.allProjectors = allProjectors;
        this.onLineProjectors = onLineProjectors;
        this.onlineClientIds = onlineClientIds;
    }

    public List<Integer> getOnlineClientIds() {
        return onlineClientIds;
    }

    public void setOnlineClientIds(List<Integer> onlineClientIds) {
        this.onlineClientIds = onlineClientIds;
    }

    public FabInfo(List<DevMember> onLineMembers, List<DeviceInfo> allProjectors, List<DeviceInfo> onLineProjectors) {
        this.onLineMembers = onLineMembers;
        this.allProjectors = allProjectors;
        this.onLineProjectors = onLineProjectors;
    }

    public List<DevMember> getOnLineMembers() {
        return onLineMembers;
    }

    public void setOnLineMembers(List<DevMember> onLineMembers) {
        this.onLineMembers = onLineMembers;
    }

    public List<DeviceInfo> getAllProjectors() {
        return allProjectors;
    }

    public void setAllProjectors(List<DeviceInfo> allProjectors) {
        this.allProjectors = allProjectors;
    }

    public List<DeviceInfo> getOnLineProjectors() {
        return onLineProjectors;
    }

    public void setOnLineProjectors(List<DeviceInfo> onLineProjectors) {
        this.onLineProjectors = onLineProjectors;
    }
}
