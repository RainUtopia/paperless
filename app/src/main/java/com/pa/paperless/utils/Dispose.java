package com.pa.paperless.utils;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.pa.paperless.bean.AdminInfo;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.IpInfo;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.PlaceInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.bean.ResInfo;
import com.pa.paperless.bean.VoteInfo;
import com.pa.paperless.bean.VoteOptionsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/15.
 * 数据处理工具类
 */

public class Dispose {
    private static List<MemberInfo> rMemberInfo;
    private static List<MeetDirFileInfo> rMeetDirFileInfo;
    private static List<VoteInfo> rVoteInfo;
    private static List<VoteOptionsInfo> rVoteOptionsInfo;
    private static List<ReceiveMeetIMInfo> rReceiveMeetIMInfo;
    private static List<ResInfo> resInfos;
    private static List<IpInfo> ipInfos;
    private static List<DeviceInfo> deviceInfos;
    private static List<PlaceInfo> placeInfos;
    private static List<AdminInfo> adminInfos;

    public static List<AdminInfo> AdminInfo(InterfaceAdmin.pbui_TypeAdminDetailInfo o) {
        if (adminInfos != null) {
            adminInfos.clear();
        } else {
            adminInfos = new ArrayList<>();
        }
        int itemCount = o.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            InterfaceAdmin.pbui_Item_AdminDetailInfo item = o.getItem(i);
            int adminid = item.getAdminid();
            String adminName = new String(item.getAdminname().toByteArray());
            String pwd = new String(item.getPw().toByteArray());
            String comment = new String(item.getComment().toByteArray());
            String phone = new String(item.getPhone().toByteArray());
            String email = new String(item.getEmail().toByteArray());
            adminInfos.add(new AdminInfo(adminid, adminName, pwd, comment, phone, email));
        }
        return adminInfos;
    }

    /**
     * 会场/会议室 信息
     *
     * @param o
     * @return
     */
    public static List<PlaceInfo> PlaceInfo(InterfaceRoom.pbui_Type_MeetRoomDetailInfo o) {
        if (placeInfos != null) {
            placeInfos.clear();
        } else {
            placeInfos = new ArrayList<>();
        }
        int itemCount = o.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            InterfaceRoom.pbui_Item_MeetRoomDetailInfo item = o.getItem(i);
            int roomid = item.getRoomid();
            int roombgpicid = item.getRoombgpicid();
            int managerid = item.getManagerid();
            String name = new String(item.getName().toByteArray());
            String addr = new String(item.getAddr().toByteArray());
            String comment = new String(item.getComment().toByteArray());
            placeInfos.add(new PlaceInfo(roomid, roombgpicid, managerid, name, addr, comment));
        }
        return placeInfos;
    }

    /**
     * 会议聊天信息
     *
     * @param o
     * @return
     */
    public static List<ReceiveMeetIMInfo> ReceiveMeetIMinfo(InterfaceIM.pbui_Type_MeetIM o) {
        if (rReceiveMeetIMInfo != null) {
            rReceiveMeetIMInfo.clear();
        } else {
            rReceiveMeetIMInfo = new ArrayList<>();
        }
        int msgtype = o.getMsgtype();
        //秒 转成毫秒
        long utcsecond = o.getUtcsecond() * 1000;
        int role = o.getRole();
        int memberid = o.getMemberid();
        String msg = MyUtils.getBts(o.getMsg());
        Log.e("MyLog", "Dispose.ReceiveMeetIMinfo:  消息类型： --->>> " + msgtype + "  角色：" + role + "  参会人员ID：" + memberid + "  消息：" + msg + "  时间：" + utcsecond);
        rReceiveMeetIMInfo.add(new ReceiveMeetIMInfo(msgtype, role, memberid, msg, utcsecond));
        return rReceiveMeetIMInfo;
    }

    /**
     * 投票信息
     *
     * @param o
     * @return
     */
    public static List<VoteInfo> Vote(InterfaceVote.pbui_Type_MeetVoteDetailInfo o) {
        if (rVoteInfo != null) {
            rVoteInfo.clear();
        } else {
            rVoteInfo = new ArrayList<>();
        }
        int itemCount = o.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            rVoteOptionsInfo = new ArrayList<>();
            InterfaceVote.pbui_Item_MeetVoteDetailInfo item = o.getItem(i);
            int voteid = item.getVoteid();
            String content = new String(item.getContent().toByteArray());
            int maintype = item.getMaintype();
            int mode = item.getMode();
            int type = item.getType();
            int votestate = item.getVotestate();
            int timeouts = item.getTimeouts();
            int selectcount = item.getSelectcount();
            List<InterfaceVote.pbui_SubItem_VoteItemInfo> itemList = item.getItemList();
            for (int j = 0; j < itemList.size(); j++) {
                InterfaceVote.pbui_SubItem_VoteItemInfo pbui_subItem_voteItemInfo = itemList.get(j);
                String text = new String(pbui_subItem_voteItemInfo.getText().toByteArray());
                int selcnt = pbui_subItem_voteItemInfo.getSelcnt();
                rVoteOptionsInfo.add(new VoteOptionsInfo(text, selcnt));
            }
            int itemCount1 = item.getItemCount();
            rVoteInfo.add(new VoteInfo(voteid, content, maintype, mode, type, votestate, timeouts, selectcount, rVoteOptionsInfo));
        }
        return rVoteInfo;
    }

    /**
     * 会议目录文件
     *
     * @param o
     * @return
     */
    public static List<MeetDirFileInfo> MeetDirFile(InterfaceFile.pbui_Type_MeetDirFileDetailInfo o) {
        if (rMeetDirFileInfo != null) {
            rMeetDirFileInfo.clear();
        } else {
            rMeetDirFileInfo = new ArrayList<>();
        }
        for (int i = 0; i < o.getItemCount(); i++) {
            InterfaceFile.pbui_Item_MeetDirFileDetailInfo item = o.getItem(i);
            int mediaid = item.getMediaid();
            String name = new String(item.getName().toByteArray());
            int uploaderid = item.getUploaderid();
            int uploaderRole = item.getUploaderRole();
            int mstime = item.getMstime();
            long size = item.getSize();
            int attrib = item.getAttrib();
            int filepos = item.getFilepos();
            String uploader_name = new String(item.getUploaderName().toByteArray());
            rMeetDirFileInfo.add(new MeetDirFileInfo(mediaid, name, uploaderid, uploaderRole, mstime, size, attrib, filepos, uploader_name));
        }
        return rMeetDirFileInfo;
    }

    /**
     * 参会人信息
     *
     * @param o
     */
    public static List<MemberInfo> MemberInfo(InterfaceMember.pbui_Type_MemberDetailInfo o) {
        if (rMemberInfo != null) {
            rMemberInfo.clear();
        } else {
            rMemberInfo = new ArrayList<>();
        }
        for (int i = 0; i < o.getItemCount(); i++) {
            InterfaceMember.pbui_Item_MemberDetailInfo item = o.getItem(i);
            int personid = item.getPersonid();
            String name = new String(item.getName().toByteArray());
            String company = new String(item.getCompany().toByteArray());
            String job = new String(item.getJob().toByteArray());
            String comment = new String(item.getComment().toByteArray());
            String phone = new String(item.getPhone().toByteArray());
            String email = new String(item.getEmail().toByteArray());
            String pwd = new String(item.getPassword().toByteArray());
            rMemberInfo.add(new MemberInfo(personid, name, company, job, comment, phone, email, pwd));
        }
        return rMemberInfo;
    }

    /**
     * 处理设备信息
     *
     * @param devInfos
     */
    public static List<DeviceInfo> DevInfo(InterfaceDevice.pbui_Type_DeviceDetailInfo devInfos) {
        if (deviceInfos != null) {
            deviceInfos.clear();
        } else {
            deviceInfos = new ArrayList<>();
        }
        int pdevCount = devInfos.getPdevCount();
        for (int i = 0; i < pdevCount; i++) {
            InterfaceDevice.pbui_Item_DeviceDetailInfo pdev = devInfos.getPdev(i);
            int devcieid = pdev.getDevcieid();
            ByteString devname = pdev.getDevname();
            String devName = new String(devname.toByteArray());
            List<IpInfo> ipInfos = ipInfo(pdev);
            int netstate = pdev.getNetstate();
            List<ResInfo> resInfos = resInfo(pdev);
            int facestate = pdev.getFacestate();
            int memberid = pdev.getMemberid();
            int meetingid = pdev.getMeetingid();
//            Log.e("MyLog", "Dispose.DevInfo:  设备ID --->>> " + devcieid + "  设备名称：" + devName + "  是否在线：" + netstate + "  界面状态：" + facestate
//                    + " 人员ID：" + memberid + "  会议ID：" + meetingid);
            deviceInfos.add(new DeviceInfo(devcieid, devName, ipInfos, netstate, resInfos, facestate, memberid, meetingid));
        }
        return deviceInfos;
    }

    private static List<ResInfo> resInfo(InterfaceDevice.pbui_Item_DeviceDetailInfo pdev) {
        if (resInfos != null) {
            resInfos.clear();
        } else {
            resInfos = new ArrayList<>();
        }
        int resinfoCount = pdev.getResinfoCount();
        for (int j = 0; j < resinfoCount; j++) {
            InterfaceDevice.pbui_SubItem_DeviceResInfo resinfo = pdev.getResinfo(j);
            int playstatus = resinfo.getPlaystatus();
            int triggerId = resinfo.getTriggerId();
            int val = resinfo.getVal();
            int val2 = resinfo.getVal2();
//            Log.e("MyLog", "Dispose.resInfo:  设备资源信息： --->>> " + val + " :::: " + val2 +
//                    "playstatus :  " + playstatus + "  triggerId:  " + triggerId);
            resInfos.add(new ResInfo(playstatus, triggerId, val, val2));
        }
        return resInfos;
    }

    private static List<IpInfo> ipInfo(InterfaceDevice.pbui_Item_DeviceDetailInfo pdev) {
        if (ipInfos != null) {
            ipInfos.clear();
        } else {
            ipInfos = new ArrayList<>();
        }
        int ipinfoCount = pdev.getIpinfoCount();
        for (int j = 0; j < ipinfoCount; j++) {
            InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo ipinfo = pdev.getIpinfo(j);
            String IpStr = new String(ipinfo.getIp().toByteArray());
            int port = ipinfo.getPort();
//            Log.e("MyLog", "Dispose.ipInfo:  设备IP信息： --->>> " + IpStr + "   端口号：" + port);
            ipInfos.add(new IpInfo(IpStr, port));
        }
        return ipInfos;
    }
}
