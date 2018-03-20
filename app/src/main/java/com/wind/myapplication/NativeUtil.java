package com.wind.myapplication;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceAdmin;
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceContext;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceFaceconfig;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceContext;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfacePerson;
import com.mogujie.tt.protobuf.InterfacePlaymedia;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.mogujie.tt.protobuf.InterfaceStatistic;
import com.mogujie.tt.protobuf.InterfaceStop;
import com.mogujie.tt.protobuf.InterfaceStream;
import com.mogujie.tt.protobuf.InterfaceTablecard;
import com.mogujie.tt.protobuf.InterfaceUpload;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.mogujie.tt.protobuf.InterfaceSystemlog;
import com.pa.paperless.activity.MeetingActivity;
//import com.pa.paperless.bean.CheckedChatIds;
import com.pa.paperless.bean.PlaceInfo;
import com.pa.paperless.bean.SubmitVoteBean;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.event.EventAgenda;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.event.EventMeetDir;
import com.pa.paperless.event.EventMeetDirFile;
import com.pa.paperless.event.EventNotice;
import com.pa.paperless.event.EventSign;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventVote;
import com.pa.paperless.event.MessageEvent;
//import com.pa.paperless.event.EventAttendPeople;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.DateUtil;
import com.pa.paperless.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDINK;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDPICTURE;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDRECT;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDTEXT;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ASK;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLEAR;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLOSE;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_EXIT;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_NOTIFY;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REJECT;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN;
import static com.mogujie.tt.protobuf.InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUPITEM;
import static com.pa.paperless.utils.MyUtils.getStb;

/**
 * Created by Administrator on 2017/12/6.
 */

public class NativeUtil {

    private static NativeUtil instance;

    static {
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avutil-55");
        System.loadLibrary("postproc-54");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("SDL2");
        System.loadLibrary("main");
        System.loadLibrary("NetClient");
        System.loadLibrary("ExecProc");
        System.loadLibrary("Device-OpenSles");
        System.loadLibrary("Codec");
        System.loadLibrary("meetcoreAnd");
        System.loadLibrary("PBmeetcoreAnd");
        System.loadLibrary("meetAnd");
        System.loadLibrary("native-lib");
        System.loadLibrary("z");
    }

    public CallListener mCallListener;

    private NativeUtil() {
    }

    public static synchronized NativeUtil getInstance() {
        if (instance == null) {
            instance = new NativeUtil();
        }
        return instance;
    }


    public void setCallListener(CallListener listener) {
        mCallListener = listener;
    }

    // 查询可加入的同屏会话
    public boolean queryCanJoin() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESINFO.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryCanJoin 93行:  查询可加入的同屏会话失败 --->>> ");
            return false;
        }
        InterfaceDevice.pbui_Type_DeviceResPlay pbui_type_deviceResPlay = InterfaceDevice.pbui_Type_DeviceResPlay.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_CAN_JOIN, pbui_type_deviceResPlay);
            Log.e("MyLog", "NativeUtil.queryCanJoin 100行:  查询可加入的同屏会话成功 --->>> ");
        }
        return true;
    }

    /*2.初始化无纸化网络平台*/
    public boolean javaInitSys() {
        String cfgpath = "/mnt/sdcard/NETCONFIG/client.ini";
        InterfaceBase.pbui_MeetCore_InitParam.Builder tmp = InterfaceBase.pbui_MeetCore_InitParam.newBuilder();
        tmp.setPconfigpathname(getStb(cfgpath));
        tmp.setProgramtype(InterfaceMacro.Pb_ProgramType.Pb_MEET_PROGRAM_TYPE_MEETCLIENT.getNumber());
        tmp.setStreamnum(4);
        tmp.setLogtofile(0);
        InterfaceBase.pbui_MeetCore_InitParam pb = tmp.build();
        boolean bret = true;
        Log.i("NativeUtil", "javaInitSys:start!");
        Log.i("NativeUtil", "javaInitSys:failed!");
        if (-1 == Init_walletSys(pb.toByteArray())) {
            bret = false;
        }
        Log.i("NativeUtil", "javaInitSys:finish!");
        return bret;
    }


    public boolean initvideores() throws InvalidProtocolBufferException {
        InterfacePlaymedia.pbui_Type_MeetInitPlayRes.Builder builder = InterfacePlaymedia.pbui_Type_MeetInitPlayRes.newBuilder();
        builder.setRes(0);
        builder.setY(0);
        builder.setX(0);
        builder.setH(1080);
        builder.setW(1920);
        InterfacePlaymedia.pbui_Type_MeetInitPlayRes build = builder.build();
        call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_INIT.getNumber(), build.toByteArray());

        Log.e("MyLog", "NativeUtil.initvideores:  初始化资源 --->>> ");
        return true;
    }

    //  6.查询设备信息
    public boolean queryDeviceInfo() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryDeviceInfo:  6.查询设备信息失败 --->>> ");
            return false;
        }
        InterfaceDevice.pbui_Type_DeviceDetailInfo tmp = InterfaceDevice.pbui_Type_DeviceDetailInfo.getDefaultInstance();
        InterfaceDevice.pbui_Type_DeviceDetailInfo pbui_type_deviceDetailInfo = tmp.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_DEVICE_INFO, pbui_type_deviceDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryDeviceInfo:   6.查询设备信息成功 --->>> ");
        return true;
    }

    //  7.按属性ID查询指定设备属性
    //  传入数据结构：   pbui_MeetDeviceQueryProperty
    //  成功返回：       pbui_DeviceInt32uProperty（整数）、pbui_DeviceStringProperty（字符串）
    //  相关结构体：     Pb_MeetDevicePropertyID
    public boolean queryDevicePropertiesById(int propetyid, int deviceid, int paramterval) throws InvalidProtocolBufferException {
        InterfaceDevice.pbui_MeetDeviceQueryProperty.Builder builder = InterfaceDevice.pbui_MeetDeviceQueryProperty.newBuilder();
        builder.setDeviceid(deviceid);
        builder.setParamterval(paramterval);
        builder.setPropertyid(propetyid);
        InterfaceDevice.pbui_MeetDeviceQueryProperty build = builder.build();
        byte[] bytes = build.toByteArray();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), bytes);
        if (array == null) {
            return false;
        }
        if (true) {
            //整数
            InterfaceDevice.pbui_DeviceInt32uProperty pbui_deviceInt32uProperty = InterfaceDevice.pbui_DeviceInt32uProperty.getDefaultInstance();
            pbui_deviceInt32uProperty.parseFrom(array);
            int propertyval = pbui_deviceInt32uProperty.getPropertyval();
            int propertyval2 = pbui_deviceInt32uProperty.getPropertyval2();
            int propertyval3 = pbui_deviceInt32uProperty.getPropertyval3();
            int propertyval4 = pbui_deviceInt32uProperty.getPropertyval4();
            Log.e("MyLog", "NativeUtil.queryDeviceProperties: 整数 --->>> " + propertyval + "   :||:  " + propertyval2 + "   :||:  " + propertyval3 + "   :||:  " + propertyval4);
        } else {
            //字符串
            InterfaceDevice.pbui_DeviceStringProperty pbui_deviceStringProperty = InterfaceDevice.pbui_DeviceStringProperty.getDefaultInstance();
            pbui_deviceStringProperty.parseFrom(array);
            ByteString propertytext = pbui_deviceStringProperty.getPropertytext();
            Log.e("MyLog", "NativeUtil.queryDeviceProperties:  字符串 --->>> " + propertytext.toString());
        }
        Log.e("MyLog", "NativeUtil.queryDeviceProperties:  7.按属性ID查询指定设备属性 --->>> ");
        return true;
    }

    //    8.修改本机界面状态
    //    Pb_MemState_MainFace=0; //处于主界面
    //    Pb_MemState_MemFace=1;//参会人员界面
    //    Pb_MemState_AdminFace=2;//后台管理界面
    public boolean setInterfaceState(int value) {
        InterfaceContext.pbui_MeetContextInfo.Builder builder = InterfaceContext.pbui_MeetContextInfo.newBuilder();
        builder.setPropertyid(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE.getNumber());
        builder.setPropertyval(value);
        InterfaceContext.pbui_MeetContextInfo build = builder.build();
        byte[] bytes = build.toByteArray();
        Log.e("MyLog","NativeUtil.setInterfaceState:  InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE.getNumber(): --->>> "+InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_ROLE.getNumber()+"  value: "+value);
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETCONTEXT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SETPROPERTY.getNumber(), bytes);
        Log.e("MyLog", "NativeUtil.setInterfaceState:  8.修改本机界面状态  为 --->>> " + value);
        return true;
    }

    //  9.查询界面状态
    //  value -->> InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace.getNumber()
    public boolean queryInterfaceState() throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        int id = builder.getId();
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] bytes1 = build.toByteArray();
        byte[] bytes = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), bytes1);
        if (bytes == null) {
            Log.e("MyLog", "NativeUtil.queryInterfaceState:  9.查询界面状态失败 --->>> ");
            return false;
        }
        InterfaceDevice.pbui_MeetDeviceMeetStatus resultb = InterfaceDevice.pbui_MeetDeviceMeetStatus.getDefaultInstance();
        InterfaceDevice.pbui_MeetDeviceMeetStatus pbui_meetDeviceMeetStatus = resultb.parseFrom(bytes);
        int facestatus = resultb.getFacestatus();
        int deviceid = resultb.getDeviceid();
        int meetingid = resultb.getMeetingid();
        int memberid = resultb.getMemberid();
        Log.e("MyLog", "NativeUtil.queryInterfaceState:  9.查询界面状态成功 --->>> ");
        return true;
    }

    //  11.请求刷新设备的会议信息
    public boolean requsetRefresh() {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REFRESH.getNumber(), null);
        Log.e("MyLog", "NativeUtil.requsetRefresh:  //  11.请求刷新设备的会议信息 --->>> ");
        return true;
    }

    //  12.广播本机设备的会议信息
    public boolean broadcastDeviceInfo() {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEMEETSTATUS.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_BROADCAST.getNumber(), null);
        Log.e("MyLog", "NativeUtil.broadcastDeviceInfo:  //  12.广播本机设备的会议信息 --->>> ");
        return true;
    }

    /**
     * 13.修改设备信息  value -->> InterfaceMacro.Pb_DeviceModifyFlag.Pb_DEVICE_MODIFYFLAG_IPADDR.getNumber()
     * pbui_DeviceModInfo
     *
     * @param value 需要修改的标志位 Pb_DeviceModifyFlag :名称等。。。
     * @return
     */
    public boolean modifyDeviceInfo(int value, int devid, String name, String ip) {

        InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo.Builder builder1 = InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo.newBuilder();
        builder1.setIp(getStb(ip));
        InterfaceDevice.pbui_SubItem_DeviceIpAddrInfo build1 = builder1.build();

        InterfaceDevice.pbui_DeviceModInfo.Builder builder = InterfaceDevice.pbui_DeviceModInfo.newBuilder();
        builder.addIpinfo(build1);
        builder.setModflag(value);
        builder.setDevcieid(devid);
        Log.e("MyLog", "NativeUtil.modifyDeviceInfo:  传入的设备ID： --->>> " + devid);
        builder.setDevname(getStb(name));

        InterfaceDevice.pbui_DeviceModInfo build = builder.build();
        byte[] bytes = build.toByteArray();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFYINFO.getNumber(), bytes);
        Log.e("MyLog", "NativeUtil.modifyDeviceInfo:  13.修改设备信息 --->>> ");
        return true;
    }


    //  14.删除设备
    public boolean deleteDevice(int divid) {
        InterfaceDevice.pbui_DeviceDel.Builder builder = InterfaceDevice.pbui_DeviceDel.newBuilder();
        builder.addDevid(divid);
        InterfaceDevice.pbui_DeviceDel build = builder.build();
        byte[] bytes = build.toByteArray();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), bytes);
        Log.e("MyLog", "NativeUtil.deleteDevice:  14.删除设备 --->>> ");
        return true;
    }


    //  16.窗口缩放操作
    public boolean windowScaleOperate() {

        InterfaceDevice.pbui_MeetDoZoomResWin.Builder builder = InterfaceDevice.pbui_MeetDoZoomResWin.newBuilder();
        builder.setDevid(0, 0);
        builder.setRes(0, 0);
        builder.setZoomper(0);
        InterfaceDevice.pbui_MeetDoZoomResWin build = builder.build();
        byte[] bytes = build.toByteArray();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ZOOM.getNumber(), bytes);
        Log.e("MyLog", "NativeUtil.windowScaleOperate:  16.窗口缩放操作 --->>> ");
        return true;
    }

    //  18.辅助签到操作
    public boolean signAlterationOperate(int devid) {
        InterfaceDevice.pbui_MeetDoEnterMeet.Builder builder = InterfaceDevice.pbui_MeetDoEnterMeet.newBuilder();
        builder.addDevid(devid);
        InterfaceDevice.pbui_MeetDoEnterMeet build = builder.build();
        byte[] bytes = build.toByteArray();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER.getNumber(), bytes);
        Log.e("MyLog", "NativeUtil.signAlterationOperate:  18.辅助签到操作 --->>> ");
        return true;
    }


    //  20.回到主界面操作
    public boolean backToMainInterfaceOperate() {
        InterfaceDevice.pbui_MeetDoToHomePage.Builder builder = InterfaceDevice.pbui_MeetDoToHomePage.newBuilder();
//        builder.setDevid()
        InterfaceDevice.pbui_MeetDoToHomePage build = builder.build();
        byte[] bytes = build.toByteArray();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_BACK.getNumber(), bytes);
        Log.e("MyLog", "NativeUtil.backToMainInterfaceOperate:  20.回到主界面操作 --->>> ");
        return true;
    }

    //  22.广播参会人员数量和签到数量
    public boolean attendSignCountBroadcast() {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_BROADCAST.getNumber(), null);
        Log.e("MyLog", "NativeUtil.attendSignCountBroadcast:  22.广播参会人员数量和签到数量 --->>> ");
        return true;
    }

    //  23.发送请求成为管理员
    public boolean sendRequestConservator() {
        InterfaceDevice.pbui_Type_MeetRequestManage.Builder builder = InterfaceDevice.pbui_Type_MeetRequestManage.newBuilder();
        builder.setDevid(0, 0);
        InterfaceDevice.pbui_Type_MeetRequestManage build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTTOMANAGE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.sendRequestConservator:  //  23.发送请求成为管理员 --->>> ");
        return true;
    }

    //  25.回复请求成为管理员请求
    public boolean revertRequestConservatorRequest() {
        InterfaceDevice.pbui_Type_MeetResponseRequestManage.Builder builder = InterfaceDevice.pbui_Type_MeetResponseRequestManage.newBuilder();
        builder.setDevid(0, 0);
        builder.setReturncode(0);
        InterfaceDevice.pbui_Type_MeetResponseRequestManage build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSETOMANAGE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.revertRequestConservator:  //  25.回复请求成为管理员请求 --->>> ");
        return true;
    }

    //  27.发送请求参会人员权限请求
    //  value -->> InterfaceMacro.Pb_MemberPermissionPropertyID.Pb_memperm_download.getNumber()
    public boolean sendAttendRequestPermissions(int value) {
        InterfaceDevice.pbui_Type_MeetRequestPrivilege.Builder builder = InterfaceDevice.pbui_Type_MeetRequestPrivilege.newBuilder();
        builder.setDevid(0, 0);
        builder.setPrivilege(value);
        InterfaceDevice.pbui_Type_MeetRequestPrivilege build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTPRIVELIGE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.sendRequestPermissions:  //  27.发送请求参会人员权限请求 --->>> ");
        return true;
    }

    //  29.回复参会人员权限请求
    public boolean revertAttendPermissionsRequest(int value) {
        InterfaceDevice.pbui_Type_MeetResponseRequestPrivilege.Builder builder = InterfaceDevice.pbui_Type_MeetResponseRequestPrivilege.newBuilder();
        builder.setDevid(0, 0);
        builder.setReturncode(value);
        InterfaceDevice.pbui_Type_MeetResponseRequestPrivilege build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEOPER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSEPRIVELIGE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.revertPermissionsRequest:  //  29.回复参会人员权限请求 --->>> ");
        return true;
    }

    //  31.按属性ID查询指定上下文属性
    //  value -->> InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_CURADMINID.getNumber()
    public boolean queryContextProperty(int value) throws InvalidProtocolBufferException {
        InterfaceContext.pbui_QueryMeetContextInfo.Builder builder = InterfaceContext.pbui_QueryMeetContextInfo.newBuilder();
        builder.setPropertyid(value);
        InterfaceContext.pbui_QueryMeetContextInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETCONTEXT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceContext.pbui_MeetContextInfo pbui_meetContextInfo = InterfaceContext.pbui_MeetContextInfo.getDefaultInstance();
        pbui_meetContextInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryContextProperty:  //  31.按属性ID查询指定上下文属性 --->>> ");
        return true;
    }

    //  32.修改上下文属性
    public boolean modificationContextProperty() {
        String cfgpath = "修改上下文属性";
        InterfaceContext.pbui_MeetContextInfo.Builder builder = InterfaceContext.pbui_MeetContextInfo.newBuilder();
        builder.setPropertyid(0);
        builder.setPropertytext(getStb(cfgpath));
        builder.setPropertyval(0);
        InterfaceContext.pbui_MeetContextInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETCONTEXT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SETPROPERTY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modificationContextProperty:  //  32.修改上下文属性 --->>> ");
        return true;
    }


    //  34.查询议程
    public boolean queryAgenda() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryAgenda:  34.查询议程失败 --->>> ");
            return false;
        }
        InterfaceAgenda.pbui_meetAgenda pbui_meetAgenda = InterfaceAgenda.pbui_meetAgenda.getDefaultInstance();
        InterfaceAgenda.pbui_meetAgenda pbui_meetAgenda1 = pbui_meetAgenda.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_AGENDA, pbui_meetAgenda1);
        }
        Log.e("MyLog", "NativeUtil.queryAgenda:  34.查询议程成功 --->>> ");
        return true;
    }

    //  35.添加时间轴式议程
    public boolean addTimeAgenda(String strText) {
        InterfaceAgenda.pbui_meetAgenda.Builder builder = InterfaceAgenda.pbui_meetAgenda.newBuilder();
        builder.setAgendatype(InterfaceMacro.Pb_AgendaType.Pb_MEET_AGENDA_TYPE_TIME.getNumber());
        InterfaceAgenda.pbui_ItemAgendaTimeInfo defaultInstance = InterfaceAgenda.pbui_ItemAgendaTimeInfo.getDefaultInstance();
        builder.setItem(1, defaultInstance);
        builder.setMediaid(0);
        builder.setText(getStb(strText));
        InterfaceAgenda.pbui_meetAgenda build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addTimeAgenda:  //  35.添加时间轴式议程 --->>> ");
        return true;
    }

    //  36.修改议程
    public boolean modificationAgenda() {
        InterfaceAgenda.pbui_meetAgenda.Builder builder = InterfaceAgenda.pbui_meetAgenda.newBuilder();
//        builder.setText()
//        ... ...
        InterfaceAgenda.pbui_meetAgenda build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modificationAgenda:  36.修改议程 --->>> ");
        return true;
    }

    //  37.删除时间轴式议程
    public boolean deleteTimeAgenda() {

        InterfaceAgenda.pbui_meetAgenda.Builder builder = InterfaceAgenda.pbui_meetAgenda.newBuilder();
//        builder.setText()
//        ... ...
        InterfaceAgenda.pbui_meetAgenda build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteTimeAgenda:  //  37.删除时间轴式议程 --->>> ");
        return true;
    }

    //  38.查询时间轴指定的议程
    public boolean queryTimeAgenda(int value) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(value);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceAgenda.pbui_meetAgenda pbui_meetAgenda = InterfaceAgenda.pbui_meetAgenda.getDefaultInstance();
        pbui_meetAgenda.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryTimeAgenda:  //  38.查询时间轴指定的议程 --->>> ");
        return true;
    }

    //  39.修改时间轴指定的议程状态
    public boolean modificationTimeAgendaState() {
        InterfaceAgenda.pbui_meetAgenda.Builder builder = InterfaceAgenda.pbui_meetAgenda.newBuilder();
        builder.setAgendatype(0);
        // ... ...
        InterfaceAgenda.pbui_meetAgenda build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETAGENDA.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modificationTimeAgendaState:  //  39.修改时间轴指定的议程状态 --->>> ");
        return true;
    }


    //  41.网页查询
    public boolean webQuery() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfaceBase.pbui_meetUrl pbui_meetUrl = InterfaceBase.pbui_meetUrl.getDefaultInstance();
        pbui_meetUrl.parseFrom(array);
        Log.e("MyLog", "NativeUtil.webChangeInform:  41.网页查询 --->>> ");
        return true;
    }

    //  42.网页修改
    public boolean webModification(String value) {

        InterfaceBase.pbui_meetUrl.Builder builder = InterfaceBase.pbui_meetUrl.newBuilder();
        builder.setUrl(getStb(value));
        InterfaceBase.pbui_meetUrl build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEFAULTURL.getNumber(), Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.webModification:  42.网页修改 --->>> ");
        return true;
    }


    //  44.查询公告
    public boolean queryNotice() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryNotice:  44.查询公告失败 --->>> ");
            return false;
        }
        InterfaceBullet.pbui_BulletDetailInfo pbui_bulletDetailInfo = InterfaceBullet.pbui_BulletDetailInfo.getDefaultInstance();
        InterfaceBullet.pbui_BulletDetailInfo pbui_bulletDetailInfo1 = pbui_bulletDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_NOTICE, pbui_bulletDetailInfo1);
        }
        Log.e("MyLog", "NativeUtil.queryNotice:  44.查询公告成功 --->>> ");
        return true;
    }

    /**
     * 查询长文本公告失败
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryLongNotice() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUEST.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryLongNotice:  查询长文本公告失败 --->>> ");
            return false;
        }
        InterfaceBullet.pbui_BigBulletDetailInfo defaultInstance = InterfaceBullet.pbui_BigBulletDetailInfo.getDefaultInstance();
        InterfaceBullet.pbui_BigBulletDetailInfo pbui_bigBulletDetailInfo = defaultInstance.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_LONG_NOTICE, pbui_bigBulletDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryLongNotice:  查询长文本公告成功 --->>> ");
        return true;
    }

    //  45.添加公告
    public boolean addNotice(String content) {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
        InterfaceBullet.pbui_Item_BulletDetailInfo.Builder builder1 = InterfaceBullet.pbui_Item_BulletDetailInfo.newBuilder();
//        builder1.setType(4);
//        builder1.setStarttime()
//        builder1.setBulletid()
        builder1.setContent(getStb(content));
//        builder1.setTimeouts()
//        builder1.setTitle()
        builder.addItem(builder1);
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addNotice:  45.添加公告 --->>> ");
        return true;
    }

    //  46.修改公告
    public boolean modifNotice(int index) {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
        InterfaceBullet.pbui_Item_BulletDetailInfo item = builder.getItem(index);
        InterfaceBullet.pbui_Item_BulletDetailInfo.Builder builder1 = item.newBuilderForType();
//        builder1.setType(4);
//        builder1.setStarttime()
//        builder1.setBulletid()
//        builder1.setContent(ByteString.copyFrom(content, Charset.forName("UTF-8")));
//        builder1.setTimeouts()
//        builder1.setTitle()
        builder.setItem(index, builder1);
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifNotice:  46.修改公告 --->>> ");
        return true;
    }

    //  47.删除公告
    public boolean deleteNotice() {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
//        builder.setItem()
//        ... ...
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteNotice:  47.删除公告 --->>> ");
        return true;
    }

    //  48.查询指定的公告
    public boolean queryAssignNotice(int value) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(value);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceBullet.pbui_BulletDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryAssignNotice:  48.查询指定的公告 --->>> ");
        return true;
    }

    //  49.发布公告
    public boolean issueNotice() {
        InterfaceBullet.pbui_BulletDetailInfo.Builder builder = InterfaceBullet.pbui_BulletDetailInfo.newBuilder();
//        builder.setItem()
        InterfaceBullet.pbui_BulletDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETBULLET.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PUBLIST.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.issueNotice:  49.发布公告 --->>> ");
        return true;
    }

    //  52.查询管理员
    public boolean queryAdmin() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryAdmin:  52.查询管理员失败 --->>> ");
            return false;
        }
        InterfaceAdmin.pbui_TypeAdminDetailInfo pbui_typeAdminDetailInfo1 = InterfaceAdmin.pbui_TypeAdminDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_ADMIN_INFO, pbui_typeAdminDetailInfo1);
        }
        Log.e("MyLog", "NativeUtil.queryAdmin:  52.查询管理员成功 --->>> ");
        return true;
    }

    //  53.添加管理员
    public boolean addAdmin(InterfaceAdmin.pbui_Item_AdminDetailInfo value) {
        InterfaceAdmin.pbui_TypeAdminDetailInfo.Builder builder = InterfaceAdmin.pbui_TypeAdminDetailInfo.newBuilder();
        builder.addItem(value);
        InterfaceAdmin.pbui_TypeAdminDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        Log.e("MyLog", "NativeUtil.addAdmin:  53.添加管理员 --->>> ");
        return true;
    }

    //  54.修改管理员自身的密码
    public boolean modifAdminPw(String name, String oldpwd, String newpwd) {
        InterfaceAdmin.pbui_Type_AdminModifyPwd.Builder builder = InterfaceAdmin.pbui_Type_AdminModifyPwd.newBuilder();
        builder.setAdminname(getStb(name));
        builder.setAdminoldpwd(getStb(oldpwd));
        builder.setAdminoldpwd(getStb(newpwd));
        InterfaceAdmin.pbui_Type_AdminModifyPwd build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifAdminPw:  54.修改管理员自身的密码 --->>> ");
        return true;
    }

    //  55.修改管理员
    public boolean modifAdmin(int index, InterfaceAdmin.pbui_Item_AdminDetailInfo value) {
        InterfaceAdmin.pbui_TypeAdminDetailInfo.Builder builder = InterfaceAdmin.pbui_TypeAdminDetailInfo.newBuilder();
        builder.setItem(index, value);
        InterfaceAdmin.pbui_TypeAdminDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifAdmin:  55.修改管理员 --->>> ");
        return true;
    }

    //  56.删除管理员
    public boolean deleteAdmin(int index) {
        InterfaceAdmin.pbui_TypeAdminDetailInfo.Builder builder = InterfaceAdmin.pbui_TypeAdminDetailInfo.newBuilder();
        builder.removeItem(index);
        InterfaceAdmin.pbui_TypeAdminDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteAdmin:  56.删除管理员 --->>> ");
        return true;
    }

    /**
     * 57.管理员登录
     *
     * @param value 是否已经md5加密
     * @param name  用户名
     * @param pwd   密码
     * @return
     */
    public boolean adminLogin(int value, String name, String pwd) {
        InterfaceAdmin.pbui_Type_AdminLogon.Builder builder = InterfaceAdmin.pbui_Type_AdminLogon.newBuilder();
        builder.setIsascill(value);
        builder.setAdminname(getStb(name));
        builder.setAdminpwd(getStb(pwd));
        InterfaceAdmin.pbui_Type_AdminLogon build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ADMIN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.adminLogin:  57.管理员登录 --->>> ");
        return true;
    }

    /**
     * 60.查询会议管理员控制的会场
     *
     * @return
     */
    public boolean queryMeetManagerPlace() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MANAGEROOM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryMeetManagerPlace:  60.查询会议管理员控制的会场失败 --->>> ");
            return false;
        }
        InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo pbui_type_meetManagerRoomDetailInfo1 = InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_ADMIN_PLACE, pbui_type_meetManagerRoomDetailInfo1);
        }
        Log.e("MyLog", "NativeUtil.queryMeetManagerPlace:  60.查询会议管理员控制的会场成功 --->>> ");
        return true;
    }

    /**
     * 61.保存会议管理员控制的会场
     *
     * @param index
     * @return
     */
    public boolean saveMeetManagerPlace(int index) {
        InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo.Builder builder = InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo.newBuilder();
        builder.getRoomid(index);
        builder.getMgrid();
        InterfaceAdmin.pbui_Type_MeetManagerRoomDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MANAGEROOM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.saveMeetManagerPlace:  61.保存会议管理员控制的会场 --->>> ");
        return true;
    }

    /**
     * 62.执行终端控制
     *
     * @param value enum Pb_DeviceControlFlag
     * @return
     */
    public boolean executeTerminalControl(int value) {
        InterfaceDevice.pbui_Type_DeviceOperControl.Builder builder = InterfaceDevice.pbui_Type_DeviceOperControl.newBuilder();
        builder.setOper(value);
//        builder.setOperval1()
//        builder.setOperval2()
        InterfaceDevice.pbui_Type_DeviceOperControl build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICECONTROL.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.executeTerminalControl:  62.执行终端控制 --->>> ");
        return true;
    }


    /**
     * 64.清空下载
     *
     * @return
     */
    public boolean emptyDownload() {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DOWNLOAD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLEAR.getNumber(), null);
        Log.e("MyLog", "NativeUtil.emptyDownload:  64.清空下载 --->>> ");
        return true;
    }

    /**
     * 65.创建一个文件下载
     *
     * @param pathname
     * @param mediaid
     * @param newfile
     * @param onlyfinish
     * @return
     */
    public boolean creationFileDownload(String pathname, int mediaid, int newfile, int onlyfinish) {
        InterfaceDownload.pbui_Type_DownloadStart.Builder builder = InterfaceDownload.pbui_Type_DownloadStart.newBuilder();
        builder.setMediaid(mediaid);
        builder.setNewfile(newfile);
        builder.setOnlyfinish(onlyfinish);
        Log.e("MyLog", "NativeUtil.creationFileDownload:  mediaid --->>> " + mediaid + "  文件：" + pathname);
        builder.setPathname(getStb(pathname));
        InterfaceDownload.pbui_Type_DownloadStart build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DOWNLOAD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.creationFileDownload:  65.创建一个文件下载 --->>> ");
        return true;
    }

    /**
     * 66.删除一个文件下载
     *
     * @param mediaid 下载媒体id
     * @return
     */
    public boolean deleteFileDownload(int mediaid) {
        InterfaceDownload.pbui_Type_DownloadDel.Builder builder = InterfaceDownload.pbui_Type_DownloadDel.newBuilder();
        builder.setMediaid(mediaid);
        InterfaceDownload.pbui_Type_DownloadDel build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DOWNLOAD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteFileDownload:  66.删除一个文件下载 --->>> ");
        return true;
    }


    /**
     * 69.上传文件
     *
     * @param dirid
     * @param attrib
     * @param newname
     * @param pathname
     * @param userval
     * @param mediaid
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean uploadFile(int uploadflag, int dirid, int attrib, String newname, String pathname, int userval, int mediaid) throws InvalidProtocolBufferException {
        InterfaceUpload.pbui_Type_AddUploadFile.Builder builder = InterfaceUpload.pbui_Type_AddUploadFile.newBuilder();
        builder.setUploadflag(uploadflag);
        builder.setDirid(dirid);
        builder.setAttrib(attrib);
        builder.setNewname(getStb(newname));
        builder.setPathname(getStb(pathname));
        builder.setUserval(userval);
        builder.setMediaid(mediaid);
        InterfaceUpload.pbui_Type_AddUploadFile build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_UPLOAD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.uploadFile 815行:  69.上传文件失败 --->>> ");
            return false;
        }
        InterfaceUpload.pbui_TypeUploadReturn pbui_typeUploadReturn = InterfaceUpload.pbui_TypeUploadReturn.parseFrom(array);
        Log.e("MyLog", "NativeUtil.uploadFile 820行:  69.上传文件成功 --->>> ");
        return true;
    }

    /**
     * 70.查询上传文件
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryUploadFile() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_UPLOAD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryUploadFile:  70.查询上传文件失败 --->>> ");
            return false;
        }
        InterfaceUpload.pbui_Type_UploadFileDetailInfo defaultInstance = InterfaceUpload.pbui_Type_UploadFileDetailInfo.getDefaultInstance();
        InterfaceUpload.pbui_Type_UploadFileDetailInfo pbui_type_uploadFileDetailInfo = defaultInstance.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_UPLOAD_FILE, pbui_type_uploadFileDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryUploadFile:  70.查询上传文件成功 --->>> ");
        return true;
    }

    /**
     * 71.删除上传
     *
     * @param mediaid
     * @return
     */
    public boolean deleteUpload(int mediaid) {
        InterfaceUpload.pbui_Type_DelUploadFile.Builder builder = InterfaceUpload.pbui_Type_DelUploadFile.newBuilder();
        builder.setMediaid(mediaid);
        InterfaceUpload.pbui_Type_DelUploadFile build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_UPLOAD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteUpload:  71.删除上传 --->>> ");
        return true;
    }


    /**
     * 74.查询指定ID的常用人员
     *
     * @param value
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryCommonPeopleFromId(int value) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(value);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfacePerson.pbui_Type_PersonDetailInfo defaultInstance = InterfacePerson.pbui_Type_PersonDetailInfo.getDefaultInstance();
        InterfacePerson.pbui_Type_PersonDetailInfo pbui_type_personDetailInfo = defaultInstance.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryCommonPeopleFromId:  74.查询指定ID的常用人员 --->>> ");
        return true;
    }

    /**
     * 75.查询常用人员
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryCommonPeople() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryCommonPeople:   75.查询常用人员 失败 --->>> ");
            return false;
        }
        InterfacePerson.pbui_Type_PersonDetailInfo defaultInstance = InterfacePerson.pbui_Type_PersonDetailInfo.getDefaultInstance();
        InterfacePerson.pbui_Type_PersonDetailInfo pbui_type_personDetailInfo = defaultInstance.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryCommonPeople:  75.查询常用人员 成功--->>> ");
        return true;
    }

    /**
     * 76.添加常用人员
     *
     * @param
     * @return
     */
    public boolean addCommonPeople(int personid, String name, String company, String job, String comment, String phone, String email, String password) {
        InterfacePerson.pbui_Item_PersonDetailInfo.Builder info = InterfacePerson.pbui_Item_PersonDetailInfo.newBuilder();
//        info.setPersonid(personid);
        info.setName(getStb(name));
        info.setCompany(getStb(company));
        info.setJob(getStb(job));
        info.setComment(getStb(comment));
//        info.setPhone(ByteString.copyFrom(phone, Charset.forName("UTF-8")));
//        info.setEmail(ByteString.copyFrom(email, Charset.forName("UTF-8")));
//        info.setPassword(ByteString.copyFrom(password, Charset.forName("UTF-8")));
        InterfacePerson.pbui_Type_PersonDetailInfo.Builder builder = InterfacePerson.pbui_Type_PersonDetailInfo.newBuilder();
        builder.addItem(info);
        InterfacePerson.pbui_Type_PersonDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addCommonPeople:  76.添加常用人员 --->>> ");
        return true;
    }

    /**
     * 77.修改常用人员
     *
     * @return
     */
    public boolean modifCommonPeople(int personid, String name, String company, String job, String comment, String phone, String email, String password) {
        InterfacePerson.pbui_Item_PersonDetailInfo.Builder info = InterfacePerson.pbui_Item_PersonDetailInfo.newBuilder();
        info.setName(getStb(name));
        info.setCompany(getStb(company));
        info.setJob(getStb(job));
        info.setComment(getStb(comment));
        info.setPhone(getStb(phone));
        info.setEmail(getStb(email));
        info.setPassword(getStb(password));
        InterfacePerson.pbui_Type_PersonDetailInfo.Builder builder = InterfacePerson.pbui_Type_PersonDetailInfo.newBuilder();
        List<InterfacePerson.pbui_Item_PersonDetailInfo> itemList = builder.getItemList();
        itemList.remove(personid);
//        builder.setItem(0, info);
        InterfacePerson.pbui_Type_PersonDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifCommonPeople:  77.修改常用人员 --->>> ");
        return true;
    }

    /**
     * 78.删除常用人员
     *
     * @return
     */
    public boolean deleteCommonPeople(int index) {
        InterfacePerson.pbui_Type_PersonDetailInfo.Builder builder = InterfacePerson.pbui_Type_PersonDetailInfo.newBuilder();
        builder.removeItem(index);
        InterfacePerson.pbui_Type_PersonDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteCommonPeople:  78.删除常用人员 --->>> ");
        return true;
    }

    /**
     * 80.查询常用人员分组
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryCommonPeopleGroup() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUP.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfacePerson.pbui_Type_PeopleGroupDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryCommonPeopleGroup:  80.查询常用人员分组 --->>> ");
        return true;
    }

    /**
     * 81.添加常用人员分组
     *
     * @param index
     * @return
     */
    public boolean addCommonPeopleGroup(int index) {
        InterfacePerson.pbui_Type_PeopleGroupDetailInfo.Builder builder = InterfacePerson.pbui_Type_PeopleGroupDetailInfo.newBuilder();
        builder.addItem(InterfacePerson.pbui_Type_PeopleGroupDetailInfo.getDefaultInstance().getItem(index));
        InterfacePerson.pbui_Type_PeopleGroupDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUP.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addCommonPeopleGroup:  81.添加常用人员分组 --->>> ");
        return true;
    }

    /**
     * 82.修改常用人员分组
     *
     * @param index
     * @return
     */
    public boolean modifCommonPeopleGroup(int index) {
        InterfacePerson.pbui_Type_PeopleGroupDetailInfo.Builder builder = InterfacePerson.pbui_Type_PeopleGroupDetailInfo.newBuilder();
        builder.addItem(InterfacePerson.pbui_Type_PeopleGroupDetailInfo.getDefaultInstance().getItem(index));
        InterfacePerson.pbui_Type_PeopleGroupDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUP.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifCommonPeopleGroup:  82.修改常用人员分组 --->>> ");
        return true;
    }

    /**
     * 83.删除常用人员分组
     *
     * @param index
     * @return
     */
    public boolean deleteCommonPeopleGroup(int index) {
        InterfacePerson.pbui_Type_PeopleGroupDetailInfo.Builder builder = InterfacePerson.pbui_Type_PeopleGroupDetailInfo.newBuilder();
        builder.addItem(InterfacePerson.pbui_Type_PeopleGroupDetailInfo.getDefaultInstance().getItem(index));
        InterfacePerson.pbui_Type_PeopleGroupDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUP.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteCommonPeopleGroup:  83.删除常用人员分组 --->>> ");
        return true;
    }


    /**
     * 85.查询常用人员分组人员
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryCommonPeopleGroupPeople() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUPITEM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfacePerson.pbui_Type_PeopleInGroupDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryCommonPeopleGroupPeople:  85.查询常用人员分组人员 --->>> ");
        return true;
    }

    /**
     * 86.添加常用人员分组人员
     *
     * @param groupid
     * @return
     */
    public boolean addCommonPeopleGroupPeople(int groupid) {
        InterfacePerson.pbui_Type_AddPeopleToGroup.Builder builder = InterfacePerson.pbui_Type_AddPeopleToGroup.newBuilder();
        builder.setGroupid(groupid);
        InterfacePerson.pbui_Type_AddPeopleToGroup build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUPITEM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addCommonPeopleGroupPeople:  86.添加常用人员分组人员 --->>> ");
        return true;
    }

    /**
     * 87.保存常用人员分组人员
     *
     * @param groupid
     * @return
     */
    public boolean saveCommonPeopleGroupPeople(int groupid) {
        InterfacePerson.pbui_Type_PeopleInGroupDetailInfo.Builder builder = InterfacePerson.pbui_Type_PeopleInGroupDetailInfo.newBuilder();
        builder.setGroupid(groupid);
        InterfacePerson.pbui_Type_PeopleInGroupDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUPITEM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.saveCommonPeopleGroupPeople:  87.保存常用人员分组人员 --->>> ");
        return true;
    }

    /**
     * 88.删除常用人员分组人员
     *
     * @return
     */
    public boolean deleteCommonPeopleGroupPeople() {
        InterfacePerson.pbui_Type_PeopleInGroupDetailInfo.Builder builder = InterfacePerson.pbui_Type_PeopleInGroupDetailInfo.newBuilder();
//        builder.get
        InterfacePerson.pbui_Type_PeopleInGroupDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUPITEM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteCommonPeopleGroupPeople:  88.删除常用人员分组人员 --->>> ");
        return true;
    }

    /**
     * 89.移入常用人员分组人员
     *
     * @return
     */
    public boolean insertCommonPeopleGroupPeople() {
        InterfacePerson.pbui_Type_PeopleInGroupDetailInfo.Builder builder = InterfacePerson.pbui_Type_PeopleInGroupDetailInfo.newBuilder();
//        builder.get
        InterfacePerson.pbui_Type_PeopleInGroupDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_PEOPLEGROUPITEM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MOVE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.insertCommonPeopleGroupPeople:  89.移入常用人员分组人员 --->>> ");
        return true;
    }

    /**
     * 91.查询指定ID的参会人员
     *
     * @param infoId
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryAttendPeopleFromId(int infoId) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(infoId);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryAttendPeopleFromId:  91.查询指定ID的参会人员失败 --->>> ");
            return false;
        }
        InterfaceMember.pbui_Type_MemberDetailInfo pbui_type_memberDetailInfo = InterfaceMember.pbui_Type_MemberDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_ATTEND_BYID, pbui_type_memberDetailInfo);
            Log.e("MyLog", "NativeUtil.queryAttendPeopleFromId:  91.查询指定ID的参会人员成功 --->>> id 是：" + infoId);
        }
        return true;
    }

    /**
     * 92.查询参会人员
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryAttendPeople() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryAttendPeople:  92.查询参会人员失败 --->>> ");
            return false;
        }
        InterfaceMember.pbui_Type_MemberDetailInfo pbui_type_memberDetailInfo = InterfaceMember.pbui_Type_MemberDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_ATTENDEE, pbui_type_memberDetailInfo);
            Log.e("MyLog", "NativeUtil.queryAttendPeopleFromId:  92.查询参会人员成功 --->>> ");
        }
        return true;
    }

    /**
     * 93.添加参会人员
     *
     * @return
     */
    public boolean addAttendPeople() {
        InterfaceMember.pbui_Type_MemberDetailInfo.Builder builder = InterfaceMember.pbui_Type_MemberDetailInfo.newBuilder();
//        builder.addItem()
        InterfaceMember.pbui_Type_MemberDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addAttendPeople:  93.添加参会人员 --->>> ");
        return true;
    }

    /**
     * 94.修改参会人员
     *
     * @return
     */
    public boolean modifAttendPeople() {
        InterfaceMember.pbui_Type_MemberDetailInfo.Builder builder = InterfaceMember.pbui_Type_MemberDetailInfo.newBuilder();
//        builder.addItem()
        InterfaceMember.pbui_Type_MemberDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifAttendPeople:  94.修改参会人员 --->>> ");
        return true;
    }

    /**
     * 95.删除参会人员
     *
     * @return
     */
    public boolean deleteAttendPeople() {
        InterfaceMember.pbui_Type_MemberDetailInfo.Builder builder = InterfaceMember.pbui_Type_MemberDetailInfo.newBuilder();
//        builder.addItem()
        InterfaceMember.pbui_Type_MemberDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteAttendPeople:  95.删除参会人员 --->>> ");
        return true;
    }

    /**
     * 96.查询参会人员属性
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryAttendPeopleProperties(int value, int propertyId) throws InvalidProtocolBufferException {
        InterfaceMember.pbui_Type_MeetMemberQueryProperty.Builder builder = InterfaceMember.pbui_Type_MeetMemberQueryProperty.newBuilder();
        builder.setParameterval(value);
        builder.setPropertyid(propertyId);
        InterfaceMember.pbui_Type_MeetMemberQueryProperty build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBER.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryAttendPeopleProperties:  96.查询参会人员属性失败 --->>> ");
            return false;
        }
        InterfaceMember.pbui_Type_MeetMembeProperty pbui_type_meetMembeProperty = InterfaceMember.pbui_Type_MeetMembeProperty.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_Attendee_Property, pbui_type_meetMembeProperty);
            Log.e("MyLog", "NativeUtil.queryAttendPeopleProperties:  96.查询参会人员属性成功 --->>> ");
        }
        return true;
    }

    /**
     * 97.查询参会人员权限
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryAttendPeoplePermissions() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfaceMember.pbui_Type_MemberPermission defaultInstance = InterfaceMember.pbui_Type_MemberPermission.getDefaultInstance();
        defaultInstance.parseFrom(array);
        int serializedSize = defaultInstance.getSerializedSize();
        for (int i = 0; i < serializedSize; i++) {
            InterfaceMember.pbui_Item_MemberPermission item = defaultInstance.getItem(i);
            int permission = item.getPermission();
        }
        Log.e("MyLog", "NativeUtil.queryAttendPeoplePermissions:  97.查询参会人员权限 --->>> ");
        return true;
    }

    /**
     * 98.保存参会人员权限
     *
     * @return
     */
    public boolean saveAttendPeoplePermissions() {
        InterfaceMember.pbui_Type_MemberPermission.Builder builder = InterfaceMember.pbui_Type_MemberPermission.newBuilder();
//        builder.setItem()
        InterfaceMember.pbui_Type_MemberPermission build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.saveAttendPerplePermissions:  98.保存参会人员权限 --->>> ");
        return true;
    }

    /**
     * 99.查询参会人员权限属性
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryAttendPeoplePermissionsProperties() throws InvalidProtocolBufferException {
        InterfaceMember.pbui_Type_MeetMemberPermissionQueryProperty.Builder builder = InterfaceMember.pbui_Type_MeetMemberPermissionQueryProperty.newBuilder();
//        builder.setPropertyid()
//        builder.setMemberid()
        InterfaceMember.pbui_Type_MeetMemberPermissionQueryProperty build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERPERMISSION.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceMember.pbui_Type_MemberPermissionProperty.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryAttendPeoplePermissionsProperties:  99.查询参会人员权限属性 --->>> ");
        return true;
    }


    /**
     * 102.查询参会人员分组
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryAttendPeopleGroup() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERGROUP.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfaceMember.pbui_Type_MemberGroupDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryAttendPeopleGroup:  102.查询参会人员分组 --->>> ");
        return true;
    }

    /**
     * 103.添加参会人员分组
     *
     * @return
     */
    public boolean addAttendPeopleGroup() {
        InterfaceMember.pbui_Type_MemberGroupDetailInfo.Builder builder = InterfaceMember.pbui_Type_MemberGroupDetailInfo.newBuilder();
        InterfaceMember.pbui_Type_MemberGroupDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERGROUP.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addAttendPeopleGroup:  103.添加参会人员分组 --->>> ");
        return true;
    }

    /**
     * 104.修改参会人员分组
     *
     * @return
     */
    public boolean modifAttendPeopleGroup() {
        InterfaceMember.pbui_Type_MemberGroupDetailInfo.Builder builder = InterfaceMember.pbui_Type_MemberGroupDetailInfo.newBuilder();
        InterfaceMember.pbui_Type_MemberGroupDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERGROUP.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifAttendPeopleGroup:  104.修改参会人员分组 --->>> ");
        return true;
    }

    /**
     * 105.删除参会人员分组
     *
     * @return
     */
    public boolean deleteAttendPeopleGroup() {
        InterfaceMember.pbui_Type_MemberGroupDetailInfo.Builder builder = InterfaceMember.pbui_Type_MemberGroupDetailInfo.newBuilder();
//        builder
        InterfaceMember.pbui_Type_MemberGroupDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERGROUP.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifAttendPeopleGroup:  105.删除参会人员分组 --->>> ");
        return true;
    }


    /**
     * 107.查询参会人员分组人员
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryAttendPeopleGroupPeople() throws InvalidProtocolBufferException {
        byte[] array = call_method(Pb_TYPE_MEET_INTERFACE_PEOPLEGROUPITEM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfaceMember.pbui_Type_MemberInGroupDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryAttendPeopleGroupPeople:  107.查询参会人员分组人员 --->>> ");
        return true;
    }

    /**
     * 108.保存参会人员分组人员
     *
     * @return
     */
    public boolean savaAttendPeopleGroupPeople() {
        InterfaceMember.pbui_Type_MemberInGroupDetailInfo.Builder builder = InterfaceMember.pbui_Type_MemberInGroupDetailInfo.newBuilder();
        InterfaceMember.pbui_Type_MemberInGroupDetailInfo build = builder.build();
        byte[] array = call_method(Pb_TYPE_MEET_INTERFACE_PEOPLEGROUPITEM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.savaAttendPeopleGroupPeople:  108.保存参会人员分组人员 --->>> ");
        return true;
    }


    /**
     * 110.查询设备会议信息
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryDeviceMeetInfo() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryDeviceMeetInfo:  110.查询设备会议信息 --->>> 失败");
            return false;
        }
        InterfaceDevice.pbui_Type_DeviceFaceShowDetail pbui_type_deviceFaceShowDetail = InterfaceDevice.pbui_Type_DeviceFaceShowDetail.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_DEVMEET_INFO, pbui_type_deviceFaceShowDetail);
            Log.e("MyLog", "NativeUtil.queryDeviceMeetInfo:  110.查询设备会议信息 --->>> 成功");
        }
        return true;
    }


    /**
     * 112.查询会场
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryPlace() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryPlace:  112.查询会场失败 --->>> ");
            return false;
        }
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo pbui_type_meetRoomDetailInfo = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_PLACE_INFO, pbui_type_meetRoomDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryPlace:  112.查询会场成功 --->>> ");
        return true;
    }

    /**
     * 113.添加会场
     *
     * @return
     */
    public boolean addPlace(PlaceInfo value) {

        InterfaceRoom.pbui_Item_MeetRoomDetailInfo.Builder builder1 = InterfaceRoom.pbui_Item_MeetRoomDetailInfo.newBuilder();
        builder1.setRoomid(value.getRoomId());
        builder1.setComment(MyUtils.getStb(value.getComment()));
        builder1.setAddr(getStb(value.getAddr()));
        builder1.setManagerid(value.getManagerid());
        builder1.setRoombgpicid(value.getRoombgPicId());
        builder1.setName(getStb(value.getName()));

        // TODO: 2018/1/26
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo.Builder builder = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.newBuilder();
        InterfaceRoom.pbui_Item_MeetRoomDetailInfo build1 = builder1.build();
        builder.addItem(build1);
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addPlace:  113.添加会场 --->>> ");
        return true;
    }

    /**
     * 114.修改会场
     *
     * @return
     */
    public boolean modifPlace(PlaceInfo value) {

        InterfaceRoom.pbui_Item_MeetRoomDetailInfo.Builder builder1 = InterfaceRoom.pbui_Item_MeetRoomDetailInfo.newBuilder();
        builder1.setRoomid(value.getRoomId());
        builder1.setComment(MyUtils.getStb(value.getComment()));
        // TODO: 2018/1/26
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo.Builder builder = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.newBuilder();
        InterfaceRoom.pbui_Item_MeetRoomDetailInfo build1 = builder1.build();
        builder.addItem(build1);
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifPlace:  114.修改会场 --->>> ");
        return true;
    }

    /**
     * 115.删除会场
     *
     * @return
     */
    public boolean deletePlace(InterfaceRoom.pbui_Item_MeetRoomDetailInfo value) {
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo.Builder builder = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.newBuilder();
        builder.addItem(value);
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deletePlace:  115.删除会场 --->>> ");
        return true;
    }

    /**
     * 116.查询会场属性
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryPlaceProperties(int value, int id) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_CommonQueryProperty.Builder builder = InterfaceBase.pbui_CommonQueryProperty.newBuilder();
        builder.setParameterval(value);
        builder.setPropertyid(id);
        InterfaceBase.pbui_CommonQueryProperty build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryPlaceProperties:  116.查询会场属性失败 --->>> ");
            return false;
        }
        InterfaceBase.pbui_CommonInt32uProperty pbui_commonInt32uProperty = InterfaceBase.pbui_CommonInt32uProperty.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_PLACE_PROPERTIES, pbui_commonInt32uProperty);
        }
        Log.e("MyLog", "NativeUtil.queryPlaceProperties:  116.查询会场属性成功 --->>> ");
        return true;
    }

    /**
     * 117.设置会场底图信息
     *
     * @return
     */
    public boolean setPlaceBGInfo(int roomId, int bgpicId) {
        InterfaceRoom.pbui_Type_MeetRoomModBGInfo.Builder builder = InterfaceRoom.pbui_Type_MeetRoomModBGInfo.newBuilder();
        builder.setRoomid(roomId);
        builder.setBgpicid(bgpicId);
        InterfaceRoom.pbui_Type_MeetRoomModBGInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.setPlaceBGInfo:  117.设置会场底图信息 --->>> ");
        return true;
    }

    /**
     * 118.查询指定ID的会场
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryPlaceById(int id) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(id);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryPlaceById:  118.查询指定ID的会场失败 --->>> ");
            return false;
        }
        InterfaceRoom.pbui_Type_MeetRoomDetailInfo pbui_type_meetRoomDetailInfo = InterfaceRoom.pbui_Type_MeetRoomDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_PLACE_BYID, pbui_type_meetRoomDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryPlaceFromId:  118.查询指定ID的会场成功 --->>> ");
        return true;
    }


    /**
     * 120.添加会场设备
     *
     * @return
     */
    public boolean addPlaceDevice() {
        InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo.Builder builder = InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo.newBuilder();
        InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addPlaceDevice:  120.添加会场设备 --->>> ");
        return true;
    }

    /**
     * 121.删除会场设备
     *
     * @return
     */
    public boolean deletePlaceDevice() {
        InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo.Builder builder = InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo.newBuilder();
        InterfaceRoom.pbui_Type_MeetRoomModDeviceInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deletePlaceDevice:  121.删除会场设备 --->>> ");
        return true;
    }

    /**
     * 122.查询会场设备坐标朝向信息
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryPlaceDeviceCoordFaceInfo(int value) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(value);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryPlaceDeviceCoordFaceInfo:  122.查询会场设备坐标朝向信息失败 --->>> ");
            return false;
        }
        InterfaceRoom.pbui_Type_MeetRoomDevPosInfo pbui_type_meetRoomDevPosInfo = InterfaceRoom.pbui_Type_MeetRoomDevPosInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_PLACEDEV, pbui_type_meetRoomDevPosInfo);
        }
        Log.e("MyLog", "NativeUtil.queryPlaceDeviceCoordFaceInfo:  122.查询会场设备坐标朝向信息成功 --->>> ");
        return true;
    }

    /**
     * 123.设置会场设备坐标朝向信息
     *
     * @return
     */
    public boolean setPlaceDeviceCoordFaceInfo() {
        InterfaceRoom.pbui_Type_MeetRoomDevPosInfo.Builder builder = InterfaceRoom.pbui_Type_MeetRoomDevPosInfo.newBuilder();
        InterfaceRoom.pbui_Type_MeetRoomDevPosInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.setPlaceDeviceCoordFaceInfo:  123.设置会场设备坐标朝向信息 --->>> ");
        return true;
    }

    /**
     * 124.会场设备排位详细信息
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean placeDeviceRankingInfo(int id) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(id);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DETAILINFO.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.placeDeviceRankingInfo:  124.会场设备排位详细信息失败 --->>> ");
            return false;
        }
        InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo pbui_item_meetRoomDevSeatDetailInfo = InterfaceRoom.pbui_Item_MeetRoomDevSeatDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.placeDeviceRankingInfo:  124.会场设备排位详细信息成功 --->>> ");
        return true;
    }

    /**
     * 125.查询符合要求的设备ID
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryRequestDeviceId(int roomid, int filterflag, int facestatus, int exceptdevid) throws InvalidProtocolBufferException {
        InterfaceRoom.pbui_Type_DumpMeetRoomDevInfo.Builder builder = InterfaceRoom.pbui_Type_DumpMeetRoomDevInfo.newBuilder();
        builder.setFilterflag(filterflag);
        builder.setRoomid(roomid);
        builder.setFacestatus(facestatus);
//        builder.setExceptdevid(exceptdevid);
        InterfaceRoom.pbui_Type_DumpMeetRoomDevInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_COMPLEXQUERY.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryRequestDeviceId:  125.查询符合要求的设备ID失败 --->>> ");
            return false;
        }
        InterfaceRoom.pbui_Type_ResMeetRoomDevInfo pbui_type_resMeetRoomDevInfo = InterfaceRoom.pbui_Type_ResMeetRoomDevInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_CONFORM_DEVID, pbui_type_resMeetRoomDevInfo);
        }
        Log.e("MyLog", "NativeUtil.queryRequestDeviceId:  125.查询符合要求的设备ID成功 --->>> ");
        return true;
    }

    /**
     * 126.查询会场设备的流通道信息
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryPlaceDeviceStreamPipeInfo(int id) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(id);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_ROOMDEVICE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MAKEVIDEO.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryPlaceDeviceStreamPipeInfo 1656行:  126.查询会场设备的流通道信息失败 --->>> ");
            return false;
        }
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo pbui_type_meetVideoDetailInfo = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_STREAM_INFORM, pbui_type_meetVideoDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryPlaceDeviceStreamPipeInfo:  126.查询会场设备的流通道信息成功 --->>> ");
        return true;
    }


    /**
     * 128.查询会议
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeet() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryMeet:  128.查询会议失败 --->>> ");
            return false;
        }
        InterfaceMeet.pbui_Type_MeetMeetInfo pbui_type_meetMeetInfo = InterfaceMeet.pbui_Type_MeetMeetInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_MEET, pbui_type_meetMeetInfo);
        }
        Log.e("MyLog", "NativeUtil.queryMeet:  128.查询会议成功 --->>> ");
        return true;
    }

    /**
     * 129.查询指定ID的会议
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetFromId(int value) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(value);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryMeetFromId:  129.查询指定ID的会议失败 --->>> ");
            return false;
        }
        InterfaceMeet.pbui_Type_MeetMeetInfo pbui_type_meetMeetInfo = InterfaceMeet.pbui_Type_MeetMeetInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_MEET_BYID, pbui_type_meetMeetInfo);
        }
        Log.e("MyLog", "NativeUtil.queryMeetFromId:  129.查询指定ID的会议成功 --->>> ");
        return true;
    }

    /**
     * 130.添加会议
     *
     * @return
     */
    public boolean addMeet() {
        InterfaceMeet.pbui_Type_MeetMeetInfo.Builder builder = InterfaceMeet.pbui_Type_MeetMeetInfo.newBuilder();
        InterfaceMeet.pbui_Type_MeetMeetInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeet:  130.添加会议 --->>> ");
        return true;
    }

    /**
     * 131.修改会议
     *
     * @return
     */
    public boolean modifMeet() {
        InterfaceMeet.pbui_Type_MeetMeetInfo.Builder builder = InterfaceMeet.pbui_Type_MeetMeetInfo.newBuilder();
        InterfaceMeet.pbui_Type_MeetMeetInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifMeet:  131.修改会议 --->>> ");
        return true;
    }

    /**
     * 132.删除会议
     *
     * @return
     */
    public boolean deleteMeet() {
        InterfaceMeet.pbui_Type_MeetMeetInfo.Builder builder = InterfaceMeet.pbui_Type_MeetMeetInfo.newBuilder();
        InterfaceMeet.pbui_Type_MeetMeetInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteMeet:  132.删除会议 --->>> ");
        return true;
    }

    /**
     * 133.查询会议属性
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetProperty() throws InvalidProtocolBufferException {
        InterfaceBase.pbui_CommonQueryProperty.Builder builder = InterfaceBase.pbui_CommonQueryProperty.newBuilder();
        InterfaceBase.pbui_CommonQueryProperty build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        if (true) {
            InterfaceBase.pbui_CommonInt32uProperty.parseFrom(array);
        } else {
            InterfaceBase.pbui_CommonInt64uProperty.parseFrom(array);
        }
        Log.e("MyLog", "NativeUtil.queryMeetProperty:  133.查询会议属性 --->>> ");
        return true;
    }

    /**
     * 134.修改会议状态
     *
     * @return
     */
    public boolean modifMeetState() {
        InterfaceMeet.pbui_Type_MeetModStatus.Builder builder = InterfaceMeet.pbui_Type_MeetModStatus.newBuilder();
        InterfaceMeet.pbui_Type_MeetModStatus build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFYSTATUS.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifMeetState:  134.修改会议状态 --->>> ");
        return true;
    }


    /**
     * 136.查询会议目录
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetDir() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryMeetDir:  136.查询会议目录失败 --->>> ");
            return false;
        }
        InterfaceFile.pbui_Type_MeetDirDetailInfo pbui_type_meetDirDetailInfo = InterfaceFile.pbui_Type_MeetDirDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_MEET_DIR, pbui_type_meetDirDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryMeetDir:  136.查询会议目录成功 --->>> ");
        return true;
    }

    /**
     * 137.查询会议第一层子级目录
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetFistChildDir(int firstID) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(firstID);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CHILDRED.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryMeetFistChildDir:  137.查询会议第一层子级目录失败 --->>> ");
            return false;
        }
        InterfaceFile.pbui_Type_MeetDirDetailInfo defaultInstance = InterfaceFile.pbui_Type_MeetDirDetailInfo.getDefaultInstance();
        InterfaceFile.pbui_Type_MeetDirDetailInfo pbui_type_meetDirDetailInfo = defaultInstance.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_MEET_FIRSTChildDir, pbui_type_meetDirDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryMeetFistChildDir:  137.查询会议第一层子级目录成功 --->>> ");
        return true;
    }

    /**
     * 138.查询会议所有子级目录
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetAllChildDir(int groupid) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(groupid);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ALLCHILDRED.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryMeetAllChildDir 1848行:  138.查询会议所有子级目录失败 --->>> ");
            return false;
        }
        InterfaceFile.pbui_Type_MeetDirDetailInfo pbui_type_meetDirDetailInfo = InterfaceFile.pbui_Type_MeetDirDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryMeetAllChildDir 1853行:  138.查询会议所有子级目录成功 --->>> ");
        return true;
    }

    /**
     * 139.添加会议目录
     *
     * @return
     */
    public boolean addMeetDir() {
        InterfaceFile.pbui_Type_MeetDirDetailInfo.Builder builder = InterfaceFile.pbui_Type_MeetDirDetailInfo.newBuilder();
        InterfaceFile.pbui_Type_MeetDirDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeetDir:  139.添加会议目录 --->>> ");
        return true;
    }

    /**
     * 140.修改会议目录
     *
     * @return
     */
    public boolean modifMeetDir() {
        InterfaceFile.pbui_Type_MeetDirDetailInfo.Builder builder = InterfaceFile.pbui_Type_MeetDirDetailInfo.newBuilder();
        InterfaceFile.pbui_Type_MeetDirDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifMeetDir:  140.修改会议目录 --->>> ");
        return true;
    }

    /**
     * 141.删除会议目录
     *
     * @return
     */
    public boolean deleteMeetDir() {
        InterfaceFile.pbui_Type_MeetDirDetailInfo.Builder builder = InterfaceFile.pbui_Type_MeetDirDetailInfo.newBuilder();
        InterfaceFile.pbui_Type_MeetDirDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteMeetDir:  141.删除会议目录 --->>> ");
        return true;
    }

    /**
     * 143.查询会议目录文件
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetDirFile(int value) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(value);
        Log.e("MyLog", "NativeUtil.queryMeetDirFile:  传递过来的ID： --->>> " + value);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryMeetDirFile:  143.查询会议目录文件失败 --->>> ");
            return false;
        }
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo pbui_type_meetDirFileDetailInfo = InterfaceFile.pbui_Type_MeetDirFileDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_MEET_DIR_FILE, pbui_type_meetDirFileDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryMeetDirFile:  143.查询会议目录文件成功 --->>> ");
        return true;
    }

    /**
     * 144.添加会议目录文件
     *
     * @return
     */
    public boolean addMeetDirFile() {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo.Builder builder = InterfaceFile.pbui_Type_MeetDirFileDetailInfo.newBuilder();
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeetDirFile:  144.添加会议目录文件 --->>> ");
        return true;
    }

    /**
     * 145.修改会议目录文件
     *
     * @return
     */
    public boolean modifMeetDirFile() {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo.Builder builder = InterfaceFile.pbui_Type_MeetDirFileDetailInfo.newBuilder();
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifMeetDirFile:  145.修改会议目录文件 --->>> ");
        return true;
    }

    /**
     * 146.删除会议目录文件
     *
     * @return
     */
    public boolean deleteMeetDirFile() {
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo.Builder builder = InterfaceFile.pbui_Type_MeetDirFileDetailInfo.newBuilder();
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteMeetDirFile:  146.删除会议目录文件 --->>> ");
        return true;
    }

    /**
     * 147.修改会议目录文件名称
     *
     * @return
     */
    public boolean modifMeetDirFileName() {
        InterfaceFile.pbui_Type_ModMeetDirFile.Builder builder = InterfaceFile.pbui_Type_ModMeetDirFile.newBuilder();
        InterfaceFile.pbui_Type_ModMeetDirFile build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFYINFO.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifMeetDirFileName:  147.修改会议目录文件名称 --->>> ");
        return true;
    }

    /**
     * 148.修改会议目录文件名称
     *
     * @return
     */
    public boolean modifMeetDirFileSort() {
        InterfaceFile.pbui_Type_ModMeetDirFilePos.Builder builder = InterfaceFile.pbui_Type_ModMeetDirFilePos.newBuilder();
        InterfaceFile.pbui_Type_ModMeetDirFilePos build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifMeetDirFileSort:  148.修改会议目录文件名称 --->>> ");
        return true;
    }

    /**
     * 149.查询文件属性
     *
     * @return
     */
    public byte[] queryFileProperty(int propertyid, int parmeterval) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_CommonQueryProperty.Builder builder = InterfaceBase.pbui_CommonQueryProperty.newBuilder();
        builder.setPropertyid(propertyid);
        builder.setParameterval(parmeterval);
        InterfaceBase.pbui_CommonQueryProperty build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        //  pbui_CommonInt64uProperty、Type_MeetMFileQueryPropertyString、
        //
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryFileProperty:  149.查询文件属性失败 --->>> ");
            return new byte[0];
        }
        InterfaceBase.pbui_CommonInt64uProperty defaultInstance = InterfaceBase.pbui_CommonInt64uProperty.getDefaultInstance();
        InterfaceBase.pbui_CommonInt64uProperty pbui_commonInt64uProperty = defaultInstance.parseFrom(array);
        long propertyval = pbui_commonInt64uProperty.getPropertyval();
        Log.e("MyLog", "NativeUtil.queryFileProperty:  149.查询文件属性成功 --->>>propertyval： " + propertyval);
        return array;
    }

    /**
     * 150.分页文件高级查询
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean pageFileQuery() throws InvalidProtocolBufferException {
        InterfaceFile.pbui_Type_ComplexQueryMeetDirFile.Builder builder = InterfaceFile.pbui_Type_ComplexQueryMeetDirFile.newBuilder();
        //为 0 表示从平台里查询
        builder.setDirid(0);
        //设置查询标志  -->> 文件类型有效
        builder.setQueryflag(InterfaceMacro.Pb_MeetFileQueryFlag.Pb_MEET_FILETYPE_QUERYFLAG_FILETYPE.getNumber());
        //文件类型  -->> 文档
        builder.setFiletype(InterfaceMacro.Pb_MeetFileType.Pb_MEET_FILETYPE_DOCUMENT.getNumber());
        //文件属性 为 0 表示全部
        builder.setAttrib(0);
        //每一页的个数
        builder.setPagenum(9);
        //控制多少页   为 0 表示全部加载
        builder.setPageindex(0);
        InterfaceFile.pbui_Type_ComplexQueryMeetDirFile build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYFILE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_COMPLEXPAGEQUERY.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.pageFileQuery:  150.分页文件高级查询失败 --->>> ");
            return false;
        }
        InterfaceFile.pbui_TypePageResQueryrFileInfo pbui_typePageResQueryrFileInfo = InterfaceFile.pbui_TypePageResQueryrFileInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.PAGE_FILE_QUERY, pbui_typePageResQueryrFileInfo);
        }
        Log.e("MyLog", "NativeUtil.pageFileQuery:  150.分页文件高级查询成功 --->>> ");
        return true;
    }

    /**
     * 151.查询会议目录权限
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetDirPermission() throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceFile.pbui_Type_MeetDirRightDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryMeetDirPermission:  151.查询会议目录权限 --->>> ");
        return true;
    }

    /**
     * 152.保存会议目录权限
     *
     * @return
     */
    public boolean savaMeetDirPermission() {
        InterfaceFile.pbui_Type_MeetDirRightDetailInfo.Builder builder = InterfaceFile.pbui_Type_MeetDirRightDetailInfo.newBuilder();
        InterfaceFile.pbui_Type_MeetDirRightDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETDIRECTORYRIGHT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.savaMeetDirPermission:  152.保存会议目录权限 --->>> ");
        return true;
    }


    /**
     * 156.查询会议文件评分
     *
     * @return
     */
    public boolean queryMeetFileScore() {
        InterfaceFile.pbui_Type_QueryFileScore.Builder builder = InterfaceFile.pbui_Type_QueryFileScore.newBuilder();
        InterfaceFile.pbui_Type_QueryFileScore build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCORE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.queryMeetFileScore:  156.查询会议文件评分 --->>> ");
        return true;
    }

    /**
     * 157.查询会议文件评分的平均分
     *
     * @return
     */
    public boolean queryMeetFileScoreAverage() {
        InterfaceFile.pbui_Type_QueryFileScore.Builder builder = InterfaceFile.pbui_Type_QueryFileScore.newBuilder();
        InterfaceFile.pbui_Type_QueryFileScore build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCORE.getNumber(), Pb_METHOD_MEET_INTERFACE_ASK.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.queryMeetFileScoreAverage:  157.查询会议文件评分的平均分 --->>> ");
        return true;
    }

    /**
     * 158.添加会议文件评分
     *
     * @return
     */
    public boolean addMeetFileScore() {
        InterfaceFile.pbui_Type_MeetFileScore.Builder builder = InterfaceFile.pbui_Type_MeetFileScore.newBuilder();
        InterfaceFile.pbui_Type_MeetFileScore build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCORE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeetFileScore:  158.添加会议文件评分 --->>> ");
        return true;
    }

    /**
     * 159.修改会议文件评分
     *
     * @return
     */
    public boolean modifMeetFileScore() {
        InterfaceFile.pbui_Type_MeetFileScore.Builder builder = InterfaceFile.pbui_Type_MeetFileScore.newBuilder();
        InterfaceFile.pbui_Type_MeetFileScore build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCORE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeetFileScore:  159.修改会议文件评分 --->>> ");
        return true;
    }

    /**
     * 160.删除会议文件评分
     *
     * @return
     */
    public boolean deleteMeetFileScore() {
        InterfaceFile.pbui_Type_MeetFileScore.Builder builder = InterfaceFile.pbui_Type_MeetFileScore.newBuilder();
        InterfaceFile.pbui_Type_MeetFileScore build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILESCORE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeetFileScore:  160.删除会议文件评分 --->>> ");
        return true;
    }

    /**
     * 162.查询会议文件评价
     *
     * @return
     */
    public boolean queryMeetFileEvaluate() {
        InterfaceFile.pbui_Type_QueryFileEvaluate.Builder builder = InterfaceFile.pbui_Type_QueryFileEvaluate.newBuilder();
        InterfaceFile.pbui_Type_QueryFileEvaluate build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILEEVALUATE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.queryMeetFileEvaluate:  162.查询会议文件评价 --->>> ");
        return true;
    }

    /**
     * 163.添加会议文件评价
     *
     * @return
     */
    public boolean addMeetFileEvaluate() {
        InterfaceFile.pbui_Type_QueryFileEvaluate.Builder builder = InterfaceFile.pbui_Type_QueryFileEvaluate.newBuilder();
        InterfaceFile.pbui_Type_QueryFileEvaluate build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILEEVALUATE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.queryMeetFileEvaluate:  162.查询会议文件评价 --->>> ");
        return true;
    }

    /**
     * 164.删除会议文件评价
     *
     * @return
     */
    public boolean deleteMeetFileEvaluate() {
        InterfaceFile.pbui_Type_QueryFileEvaluate.Builder builder = InterfaceFile.pbui_Type_QueryFileEvaluate.newBuilder();
        InterfaceFile.pbui_Type_QueryFileEvaluate build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FILEEVALUATE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.queryMeetFileEvaluate:  164.删除会议文件评价 --->>> ");
        return true;
    }


    /**
     * 166.查询会议评价
     *
     * @return
     */
    public boolean queryMeetEvaluate() {
        InterfaceMeet.pbui_Type_QueryMeetEvaluate.Builder builder = InterfaceMeet.pbui_Type_QueryMeetEvaluate.newBuilder();
        InterfaceMeet.pbui_Type_QueryMeetEvaluate build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETEVALUATE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.queryMeetEvaluate:  166.查询会议评价 --->>> ");
        return true;
    }

    /**
     * 167.添加会议评价
     *
     * @return
     */
    public boolean addMeetEvaluate() {
        InterfaceFile.pbui_Type_MeetingFileEvaluate.Builder builder = InterfaceFile.pbui_Type_MeetingFileEvaluate.newBuilder();
        InterfaceFile.pbui_Type_MeetingFileEvaluate build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETEVALUATE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeetEvaluate:  167.添加会议评价 --->>> ");
        return true;
    }

    /**
     * 168.删除会议评价
     *
     * @return
     */
    public boolean deleteMeetEvaluate() {
        InterfaceMeet.pbui_Type_DelMeetEvaluate.Builder builder = InterfaceMeet.pbui_Type_DelMeetEvaluate.newBuilder();
        InterfaceMeet.pbui_Type_DelMeetEvaluate build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETEVALUATE.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteMeetEvaluate:  168.删除会议评价 --->>> ");
        return true;
    }

    /**
     * 170.查询管理员日志
     *
     * @return
     */
    public boolean queryAdminLog() {
        InterfaceSystemlog.pbui_Type_QueryMeetSystemLog.Builder builder = InterfaceSystemlog.pbui_Type_QueryMeetSystemLog.newBuilder();
        InterfaceSystemlog.pbui_Type_QueryMeetSystemLog build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_SYSTEMLOG.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.queryAdminLog:  170.查询管理员日志 --->>> ");
        return true;
    }


    /**
     * 172.查询会议视频
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetVedio() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo pbui_type_meetVideoDetailInfo = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_MEET_VEDIO, pbui_type_meetVideoDetailInfo);
            Log.e("MyLog", "NativeUtil.queryMeetVedio:  172.查询会议视频成功 --->>> ");
        }
        return true;
    }

    /**
     * 173.查询指定ID的会议视频
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetVedioFromId() throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryMeetVedio:  173.查询指定ID的会议视频 --->>> ");
        return true;
    }

    /**
     * 174.添加会议视频
     *
     * @return
     */
    public boolean addMeetVedio() {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo.Builder builder = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.newBuilder();
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeetVedio:  174.添加会议视频 --->>> ");
        return true;
    }

    /**
     * 175.修改会议视频
     *
     * @return
     */
    public boolean modifMeetVedio() {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo.Builder builder = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.newBuilder();
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeetVedio:  175.修改会议视频 --->>> ");
        return true;
    }

    /**
     * 176.删除会议视频
     *
     * @return
     */
    public boolean deleteMeetVedio() {
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo.Builder builder = InterfaceVideo.pbui_Type_MeetVideoDetailInfo.newBuilder();
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVIDEO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addMeetVedio:  176.删除会议视频 --->>> ");
        return true;
    }


    /**
     * 178.查询会议双屏显示
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetDoubleShow() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTABLECARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryMeetDoubleShow:  178.查询会议双屏显示 --->>> ");
        return true;
    }

    /**
     * 179.修改会议双屏显示
     *
     * @return
     */
    public boolean modifDoubleShow() {
        InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo.Builder builder = InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo.newBuilder();
        InterfaceTablecard.pbui_Type_MeetTableCardDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETTABLECARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifDoubleShow:  179.修改会议双屏显示 --->>> ");
        return true;
    }

    /**
     * 181.查询会议排位
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetRanking() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryMeetRanking:  181.查询会议排位失败 --->>> ");
            return false;
        }
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo pbui_type_meetSeatDetailInfo = InterfaceRoom.pbui_Type_MeetSeatDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.Query_MeetSeat_Inform, pbui_type_meetSeatDetailInfo);
            Log.e("MyLog", "NativeUtil.queryMeetRanking:  181.查询会议排位成功 --->>> ");
        }
        return true;
    }

    /**
     * 182.修改会议排位
     *
     * @return
     */
    public boolean modifMeetRanking() {
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo.Builder builder = InterfaceRoom.pbui_Type_MeetSeatDetailInfo.newBuilder();
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifMeetRanking:  182.修改会议排位 --->>> ");
        return true;
    }

    /**
     * 183.查询会议排位属性
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetRankingProperty() throws InvalidProtocolBufferException {
        InterfaceBase.pbui_CommonQueryProperty.Builder builder = InterfaceBase.pbui_CommonQueryProperty.newBuilder();
        InterfaceBase.pbui_CommonQueryProperty build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSEAT.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceBase.pbui_CommonInt32uProperty.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryMeetRankingProperty:  183.查询会议排位属性 --->>> ");
        return true;
    }


    /**
     * 185.发送会议交流信息
     *
     * @return
     */
    public boolean sendMeetChatInfo(String sendMsg, int msgType, Iterable<Integer> iterable) {
        InterfaceIM.pbui_Type_SendMeetIM.Builder builder = InterfaceIM.pbui_Type_SendMeetIM.newBuilder();
        builder.setMsg(getStb(sendMsg));
        builder.setMsgtype(msgType);
        builder.addAllUserids(iterable);
        InterfaceIM.pbui_Type_SendMeetIM build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SEND.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.sendMeetInfo:  185.发送会议交流信息 --->>> ");
        return true;
    }

    /**
     * 186.查询全部会议交流信息
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryAllMeetInfo() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfaceIM.pbui_Type_MeetIMDetailInfo pbui_type_meetIMDetailInfo = InterfaceIM.pbui_Type_MeetIMDetailInfo.parseFrom(array);

        Log.e("MyLog", "NativeUtil.queryAllMeetInfo:  186.查询全部会议交流信息 --->>> ");
        return true;
    }

    /**
     * 187.组合查询会议交流信息
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean complexQueryMeetInfo() throws InvalidProtocolBufferException {
        InterfaceIM.pbui_Type_MeetComplexQueryIM.Builder builder = InterfaceIM.pbui_Type_MeetComplexQueryIM.newBuilder();
        InterfaceIM.pbui_Type_MeetComplexQueryIM build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETIM.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_COMPLEXQUERY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceIM.pbui_TypePageResQueryrMsgInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.complexQueryMeetInfo:  187.组合查询会议交流信息 --->>> ");

        return true;
    }


    /**
     * 189.查询发起的投票
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryInitiateVote() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryInitiateVote:  189.查询发起的投票失败 --->>> ");
            return false;
        }
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo pbui_type_meetOnVotingDetailInfo = InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryVote:  189.查询发起的投票成功 --->>> ");
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_LUNCHED_VOTE, pbui_type_meetOnVotingDetailInfo);
        }
        return true;
    }

    /**
     * 190.查询指定ID发起的投票
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryInitiateVoteById(int id) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(id);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo pbui_type_meetOnVotingDetailInfo = InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryVoteById:  190.查询指定ID发起的投票 --->>> ");
        return true;
    }

    /**
     * 191.新建一个投票
     *
     * @return
     */
    public boolean createVote() {
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.Builder builder = InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.newBuilder();
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.createVote:  191.新建一个投票 --->>> ");
        return true;
    }

    /**
     * 192.修改一个投票
     *
     * @return
     */
    public boolean modifVote() {
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.Builder builder = InterfaceVote.pbui_Type_MeetOnVotingDetailInfo.newBuilder();
        InterfaceVote.pbui_Type_MeetOnVotingDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifVote:  192.修改一个投票 --->>> ");
        return true;
    }

    /**
     * 193.发起投票
     *
     * @return
     */
    public boolean initiateVote(int voteid) {
        InterfaceVote.pbui_Type_MeetStopVoteInfo.Builder builder = InterfaceVote.pbui_Type_MeetStopVoteInfo.newBuilder();
        Log.e("MyLog", "NativeUtil.initiateVote 2509行:  发起投票传递的投票ID --->>> " + voteid);
        builder.addVoteid(voteid);
        InterfaceVote.pbui_Type_MeetStopVoteInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.startVote:  193.发起投票 --->>> ");
        return true;
    }

    /**
     * 194.删除投票
     *
     * @return
     */
    public boolean deleteVote() {
        InterfaceVote.pbui_Type_MeetStopVoteInfo.Builder builder = InterfaceVote.pbui_Type_MeetStopVoteInfo.newBuilder();
        InterfaceVote.pbui_Type_MeetStopVoteInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteVote:  194.删除投票 --->>> ");
        return true;
    }


    /**
     * 195.停止投票
     *
     * @return
     */
    public boolean stopVote(int voteid) {
        InterfaceVote.pbui_Type_MeetStopVoteInfo.Builder builder = InterfaceVote.pbui_Type_MeetStopVoteInfo.newBuilder();
        builder.addVoteid(voteid);
        InterfaceVote.pbui_Type_MeetStopVoteInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.deleteVote:  195.停止投票 --->>> ");
        return true;
    }

    /**
     * 196.提交投票结果
     *
     * @return
     */
    public boolean submitVoteResult(SubmitVoteBean submitVoteBeen) {
        InterfaceVote.pbui_Item_MeetSubmitVote.Builder builder1 = InterfaceVote.pbui_Item_MeetSubmitVote.newBuilder();
        builder1.setSelcnt(submitVoteBeen.getSelcnt());
        builder1.setVoteid(submitVoteBeen.getVoteid());
        builder1.setSelitem(submitVoteBeen.getSelectItem());
        Log.e("MyLog", "NativeUtil.submitVoteResult 2555行:  提交投票结果传递参数 --->>> 选择了的个数：" + submitVoteBeen.getSelcnt() +
                "  传递的投票ID  " + submitVoteBeen.getVoteid() + "   已经选择的十进制代表数： " + submitVoteBeen.getSelectItem());
        InterfaceVote.pbui_Type_MeetSubmitVote.Builder builder = InterfaceVote.pbui_Type_MeetSubmitVote.newBuilder();
        builder.addItem(builder1);

        InterfaceVote.pbui_Type_MeetSubmitVote build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SUBMIT.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.submitVoteResult:  196.提交投票结果 --->>> ");
        return true;
    }

    /**
     * 197.设置投票超时值
     *
     * @param timeout
     * @return
     */
    public boolean setVoteTimeouts(int timeout) {
        InterfaceVote.pbui_Type_MeetSetVoteTimeouts.Builder builder = InterfaceVote.pbui_Type_MeetSetVoteTimeouts.newBuilder();
        builder.setTimeouts(timeout);
        InterfaceVote.pbui_Type_MeetSetVoteTimeouts build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETONVOTING.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SET.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.setVoteTimeouts:  197.设置投票超时值 --->>> ");
        return true;
    }


    /**
     * 199.查询指定ID的投票
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryVoteById(int value) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(value);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SINGLEQUERYBYID.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceVote.pbui_Type_MeetVoteDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryVoteById:  199.查询指定ID的投票 --->>> ");
        return true;
    }

    /**
     * 200.查询投票
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryVote() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryVote:  200.查询投票失败 --->>> ");
            return false;
        }
        InterfaceVote.pbui_Type_MeetVoteDetailInfo pbui_type_meetVoteDetailInfo = InterfaceVote.pbui_Type_MeetVoteDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_VOTE, pbui_type_meetVoteDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.queryVote:  200.查询投票成功 --->>> ");
        return true;
    }

    /**
     * 201.按类别查询投票
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryVoteByCategory() throws InvalidProtocolBufferException {
        InterfaceVote.pbui_Type_MeetVoteComplexQuery.Builder builder = InterfaceVote.pbui_Type_MeetVoteComplexQuery.newBuilder();
        InterfaceVote.pbui_Type_MeetVoteComplexQuery build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_COMPLEXQUERY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceVote.pbui_Type_MeetVoteDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryVoteByCategory:  201.按类别查询投票 --->>> ");
        return true;
    }


    /**
     * 203.查询指定投票的提交人
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryOneVoteSubmitter(int value) throws InvalidProtocolBufferException {
        InterfaceBase.pbui_QueryInfoByID.Builder builder = InterfaceBase.pbui_QueryInfoByID.newBuilder();
        builder.setId(value);
        InterfaceBase.pbui_QueryInfoByID build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTESIGNED.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryOneVoteSubmitter:  203.查询指定投票的提交人失败 --->>> ");
            return false;
        }
        InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo pbui_type_meetVoteSignInDetailInfo = InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_MEMBER_BYVOTE, pbui_type_meetVoteSignInDetailInfo);
            Log.e("MyLog", "NativeUtil.queryOneVoteSubmitter:  203.查询指定投票的提交人成功 --->>> ");
        }
        return true;
    }

    /**
     * 204.查询投票提交人属性
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryVoteSubmitterProperty() throws InvalidProtocolBufferException {
        InterfaceVote.pbui_Type_MeetVoteQueryProperty.Builder builder = InterfaceVote.pbui_Type_MeetVoteQueryProperty.newBuilder();
        InterfaceVote.pbui_Type_MeetVoteQueryProperty build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETVOTESIGNED.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceBase.pbui_CommonInt32uProperty.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryVoteSubmitterProperty:  204.查询投票提交人属性 --->>> ");
        return true;
    }


    /**
     * 206.查询签到
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean querySign() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.querySign:  206.查询签到失败 --->>> ");
            return false;
        }
        InterfaceSignin.pbui_Type_MeetSignInDetailInfo pbui_type_meetSignInDetailInfo = InterfaceSignin.pbui_Type_MeetSignInDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_SIGN, pbui_type_meetSignInDetailInfo);
        }
        Log.e("MyLog", "NativeUtil.querySign:  206.查询签到成功 --->>> ");
        return true;
    }

    /**
     * 207.发送签到
     *
     * @param pwd      密码
     * @param signData 数据类型
     * @param signType 签到方式
     * @return
     */
    public boolean sendSign(String pwd, String signData, int signType) {
        InterfaceSignin.pbui_Type_DoMeetSignIno.Builder builder = InterfaceSignin.pbui_Type_DoMeetSignIno.newBuilder();
        builder.setPassword(getStb(pwd));
        builder.setPsigndata(getStb(signData));
        builder.setSigninType(signType);
        InterfaceSignin.pbui_Type_DoMeetSignIno build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.sendSign:  207.发送签到 --->>> ");
        return true;
    }

    /**
     * 208.查询会议签到属性
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetSignProperty() throws InvalidProtocolBufferException {
        InterfaceBase.pbui_CommonQueryProperty.Builder builder = InterfaceBase.pbui_CommonQueryProperty.newBuilder();
        InterfaceBase.pbui_CommonQueryProperty build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSIGN.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERYPROPERTY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceBase.pbui_CommonInt32uProperty.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryMeetSignProperty:  208.查询会议签到属性 --->>> ");
        return true;
    }

    /**
     * 209.强制发起白板
     *
     * @return
     */
    public boolean coerceStartWhiteBoard(int operFlag, String mediaName, int operMemberid, int srcmemId, long srcwbId, List<Integer> allUserId) {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.Builder tmp3 = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.newBuilder();
        tmp3.setOperflag(operFlag);
        tmp3.setMedianame(MyUtils.getStb(mediaName));
        tmp3.setOpermemberid(operMemberid);
        tmp3.setSrcmemid(srcmemId);
        tmp3.setSrcwbid(srcwbId);
        tmp3.addAllUserid(allUserId);
        tmp3.setOperflag(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber());
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl build = tmp3.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.coerceStartBoard:  209.强制发起白板 --->>> ");
        return true;
    }

    /**
     * 210.询问式发起白板
     * int operFlag, String mediaName, int operMemberid, int srcmemId, int srcwbId, List<Integer> allUserId
     *
     * @return
     */
    public boolean inquiryStartWhiteBoard() {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.newBuilder();
        builder.setOperflag(InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber());
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl build = builder.build();
        call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.inquiryStartWhiteBoard:  210.询问式发起白板 --->>> ");
        return true;
    }

    /**
     * 211.强制关闭白板
     *
     * @return
     */
    public boolean coerceStopWhiteBoard() {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.inquiryStartWhiteBoard:  211.强制关闭白板 --->>> ");
        return true;
    }

    /**
     * 212.请求式关闭白板
     *
     * @return
     */
    public boolean inquiryStopWhiteBoard() {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.inquiryStartWhiteBoard:  212.请求式关闭白板 --->>> ");
        return true;
    }

    /**
     * 213.广播本身退出白板
     *
     * @return
     */
    public boolean broadcastStopWhiteBoard() {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardControl build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.inquiryStartWhiteBoard:  213.广播本身退出白板 --->>> ");
        return true;
    }


    /**
     * 215.拒绝加入
     *
     * @return
     */
    public boolean rejectJoin() {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REJECT.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.rejectJoin:  215.拒绝加入 --->>> ");
        return true;
    }

    /**
     * 217.同意加入
     *
     * @return
     */
    public boolean agreeJoin() {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.agreeJoin:  217.同意加入 --->>> ");
        return true;
    }


    /**
     * 222.白板清空记录
     *
     * @return
     */
    public boolean whiteBoardClearRecord() {
        InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard.Builder builder = InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DELALL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.whiteBoardClearInform:  222.白板清空记录 --->>> ");
        return true;
    }

    /**
     * 223.白板删除记录
     *
     * @return
     */
    public boolean whiteBoardDeleteRecord() {
        InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard.Builder builder = InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.whiteBoardDeleteRecord:  223.白板删除记录 --->>> ");
        return true;
    }


    /**
     * 225.添加墨迹
     *
     * @return
     */
    public boolean addInk() {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDINK.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addInk:  225.添加墨迹 --->>> ");
        return true;
    }

    /**
     * 226.查询墨迹
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryInk() throws InvalidProtocolBufferException {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceWhiteboard.pbui_Type_MeetWBInkDetail.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryInk:  226.查询墨迹 --->>> ");
        return true;
    }

    /**
     * 228.添加矩形、直线、圆形
     *
     * @return
     */
    public boolean addDrawFigure(int operid, int opermemberid, int srcmemid, long srcwbid, long utcstamp,
                                 int type, int size, int color,
                                 float lx, float ly, float rx, float ry) {
        InterfaceWhiteboard.pbui_Item_MeetWBRectDetail.Builder builder = InterfaceWhiteboard.pbui_Item_MeetWBRectDetail.newBuilder();
        builder.setOperid(operid);
        builder.setOpermemberid(opermemberid);
        builder.setSrcmemid(srcmemid);
        builder.setSrcwbid(srcwbid);
        builder.setUtcstamp(utcstamp);
        builder.setFiguretype(type);
        builder.setLinesize(size);
        builder.setArgb(color);
        builder.addPt(lx);
        builder.addPt(ly);
        builder.addPt(rx);
        builder.addPt(ry);
        InterfaceWhiteboard.pbui_Item_MeetWBRectDetail build = builder.build();
        call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDRECT.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addDrawFigure:  228.添加矩形、直线、圆形 --->>> ");
        return true;
    }

    /**
     * 229.查询矩形、直线、圆形
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryFigure() throws InvalidProtocolBufferException {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceWhiteboard.pbui_Type_MeetWBRectDetail.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryFigure:  229.查询矩形、直线、圆形 --->>> ");
        return true;
    }


    /**
     * 231.添加文本
     *
     * @return
     */
    public boolean addText() {
        InterfaceWhiteboard.pbui_Item_MeetWBTextDetail.Builder builder = InterfaceWhiteboard.pbui_Item_MeetWBTextDetail.newBuilder();

//        builder.setOperid();
//        builder.setOpermemberid();
//        builder.setSrcmemid();
//        builder.setSrcwbid();
//        builder.setUtcstamp();
//        builder.setFiguretype();
//        builder.setFontsize();
//        builder.setFontflag();
//        builder.setArgb();
//        builder.setFontname();
//        builder.setLx();
//        builder.setLy();
//        builder.setPtext();

        InterfaceWhiteboard.pbui_Item_MeetWBTextDetail build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDTEXT.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addText:  231.添加文本 --->>> ");
        return true;
    }

    /**
     * 232.查询文本
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryText() throws InvalidProtocolBufferException {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceWhiteboard.pbui_Type_MeetWBTextDetail.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryText:  232.查询文本 --->>> ");
        return true;
    }


    /**
     * 234.添加图片通知
     * 被动
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean addPictureInform2() throws InvalidProtocolBufferException {
        InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail.Builder builder = InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail.newBuilder();
        InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDPICTURE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.addPictureInform:  234.添加图片通知 --->>> ");
        return true;
    }

    /**
     * 235.查询图片
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryPicture() throws InvalidProtocolBufferException {
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery.Builder builder = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetWhiteBoardComplexQuery build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_WHITEBOARD.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        if (array == null) {
            return false;
        }
        InterfaceWhiteboard.pbui_Type_MeetWBPictureDetail.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryPicture:  235.查询图片 --->>> ");
        return true;
    }


    /**
     * 237.修改会议功能
     *
     * @return
     */
    public boolean modifMeetFunction() {
        InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo.Builder builder = InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo.newBuilder();
        InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FUNCONFIG.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_SAVE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifMeetFunction:  237.修改会议功能 --->>> ");
        return true;
    }

    /**
     * 238.查询会议功能
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMeetFunction() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_FUNCONFIG.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryMeetFunction:  238.查询会议功能失败 --->>> ");
            return false;
        }
        InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo pbui_type_meetFunConfigDetailInfo = InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryMeetFunction:  238.查询会议功能成功 --->>> ");
        return true;
    }

    /**
     * 240.修改参会人员白板颜色
     *
     * @return
     */
    public boolean modifAttendPeopleBoardColor() {
        InterfaceWhiteboard.pbui_Type_MeetMemberColorDetailInfo.Builder builder = InterfaceWhiteboard.pbui_Type_MeetMemberColorDetailInfo.newBuilder();
        InterfaceWhiteboard.pbui_Type_MeetMemberColorDetailInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERCOLOR.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifAttendPeopleBoardColor:  240.修改参会人员白板颜色 --->>> ");
        return true;
    }

    /**
     * 241.查询参会人员白板颜色
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryAttendPeopleBoardColor() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEMBERCOLOR.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfaceWhiteboard.pbui_Type_MeetMemberColorDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryAttendPeopleBoardColor:  241.查询参会人员白板颜色 --->>> ");
        return true;
    }

    /**
     * 243.鼠标操作
     *
     * @return
     */
    public boolean mouseOperate() {
        InterfaceStream.pbui_Type_MeetDoScreenMouseControl.Builder builder = InterfaceStream.pbui_Type_MeetDoScreenMouseControl.newBuilder();
        InterfaceStream.pbui_Type_MeetDoScreenMouseControl build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_SCREENMOUSECONTROL.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.mouseOperate:  243.鼠标操作 --->>> ");
        return true;
    }

    /**
     * 245.键盘操作
     *
     * @return
     */
    public boolean keyBoardOperate(int devid, int flag, int key, int otherFlag) {
        InterfaceStream.pbui_Type_MeetDoScreenKeyBoardControl.Builder builder = InterfaceStream.pbui_Type_MeetDoScreenKeyBoardControl.newBuilder();
        builder.setDeviceid(devid);
        builder.setFlag(flag);
        builder.setKey(key);
        builder.setOtherflag(otherFlag);
        InterfaceStream.pbui_Type_MeetDoScreenKeyBoardControl build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_SCREENKEYBOARDCONTROL.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CONTROL.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.keyBoardOperate:  245.键盘操作 --->>> ");
        return true;
    }

    /**
     * 248.停止资源操作
     *
     * @return
     */
    public boolean stopResourceOperate(List<Integer> resVal, List<Integer> devid) {
        InterfaceStop.pbui_Type_MeetDoStopResWork.Builder builder = InterfaceStop.pbui_Type_MeetDoStopResWork.newBuilder();
        builder.addAllRes(resVal);
        builder.addAllDeviceid(devid);
        InterfaceStop.pbui_Type_MeetDoStopResWork build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY.getNumber(), Pb_METHOD_MEET_INTERFACE_CLOSE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.stopResourceOperate:  248.停止资源操作  --->>> ");
        return true;
    }

    /**
     * 249.停止触发器操作
     *
     * @return
     */
    public boolean stopTriggerOperate() {
        InterfaceStop.pbui_Type_MeetDoStopTrrigerWork.Builder builder = InterfaceStop.pbui_Type_MeetDoStopTrrigerWork.newBuilder();
        InterfaceStop.pbui_Type_MeetDoStopTrrigerWork build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STOPPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_STOP.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.stopTriggerOperate:  249.停止触发器操作 --->>> ");
        return true;
    }


    /**
     * 252.媒体播放操作
     *
     * @return
     */
    public boolean mediaPlayOperate(int mediaid, List<Integer> devIds, int pos) {
        InterfacePlaymedia.pbui_Type_MeetDoMediaPlay.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoMediaPlay.newBuilder();
        builder.setPlayflag(InterfaceMacro.Pb_MeetPlayFlag.Pb_MEDIA_PLAYFLAG_ZERO.getNumber());
        builder.setPos(pos);
        builder.setMediaid(mediaid);
        builder.addRes(0);
        builder.addAllDeviceid(devIds);

        builder.setTriggeruserval(0);
        InterfacePlaymedia.pbui_Type_MeetDoMediaPlay build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.mediaPlayOperate:  252.媒体播放操作 --->>> ");
        return true;
    }

    /**
     * 释放播放资源
     *
     * @return
     */
    public boolean mediaDestroy(int resValue) {
        InterfacePlaymedia.pbui_Type_MeetDestroyPlayRes.Builder builder = InterfacePlaymedia.pbui_Type_MeetDestroyPlayRes.newBuilder();
        builder.setRes(resValue);
        InterfacePlaymedia.pbui_Type_MeetDestroyPlayRes build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DESTORY.getNumber(), build.toByteArray());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("MyLog", "NativeUtil.mediaDestroy 3176行:  释放播放资源 --->>> ");
        return true;
    }

    /**
     * 253.设置播放位置
     *
     * @return
     */
    public boolean setPlayPlace(int resIndex, int pos, List<Integer> devIds) {

        InterfacePlaymedia.pbui_Type_MeetDoSetPlayPos.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoSetPlayPos.newBuilder();
        builder.setResindex(resIndex);
        builder.setPos(pos);
        builder.addAllDeviceid(devIds);
        InterfacePlaymedia.pbui_Type_MeetDoSetPlayPos build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MOVE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.setPlayPlace:  253.设置播放位置 --->>> ");
        return true;
    }

    /**
     * 254.设置播放暂停
     *
     * @return
     */
    public boolean setPlayStop(int resIndex, List<Integer> devIds) {
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoPlayControl.newBuilder();
        builder.setResindex(resIndex);
        builder.addAllDeviceid(devIds);
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PAUSE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.setPlayStop:  254.设置播放暂停 --->>> ");
        return true;
    }


    /**
     * 255.设置播放回复
     *
     * @return
     */
    public boolean setPlayRecover(int resIndex, List<Integer> devIds) {
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl.Builder builder = InterfacePlaymedia.pbui_Type_MeetDoPlayControl.newBuilder();
        builder.setResindex(resIndex);
        builder.addAllDeviceid(devIds);
        InterfacePlaymedia.pbui_Type_MeetDoPlayControl build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PLAY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.setPlayRecover:  255.设置播放回复 --->>> ");
        return true;
    }

    /**
     * 256.查询媒体播放
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryMediaPlay() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfacePlaymedia.pbui_Type_MeetMediaPlayDetailInfo pbui_type_meetMediaPlayDetailInfo = InterfacePlaymedia.pbui_Type_MeetMediaPlayDetailInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryMediaPlay:  256.查询媒体播放 --->>> ");
        return true;
    }


    /**
     * 258.文件推送
     *
     * @return
     */
    public boolean filePush() {
        InterfacePlaymedia.pbui_Type_DoFilePush.Builder builder = InterfacePlaymedia.pbui_Type_DoFilePush.newBuilder();
        InterfacePlaymedia.pbui_Type_DoFilePush build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEDIAPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PUSH.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.filePush:  258.文件推送 --->>> ");
        return true;
    }


    /**
     * 260.请求播放流通道
     *
     * @return
     */
    public boolean requestPlayStreamWay() {
        InterfaceStream.pbui_Type_DoReqStreamPush.Builder builder = InterfaceStream.pbui_Type_DoReqStreamPush.newBuilder();
        InterfaceStream.pbui_Type_DoReqStreamPush build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTPUSH.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.requestPlayStreamWay:  260.请求播放流通道 --->>> ");
        return true;
    }


    /**
     * 262.推送流
     *
     * @return
     */
    public boolean pushStream() {
        InterfaceStream.pbui_Type_DoStreamPush.Builder builder = InterfaceStream.pbui_Type_DoStreamPush.newBuilder();
        InterfaceStream.pbui_Type_DoStreamPush build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PUSH.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.pushStream:  262.推送流 --->>> ");
        return true;
    }

    /**
     * 264.流播放
     *
     * @param srcdeviceid
     * @param subid
     * @param triggeruserval
     * @param allres
     * @param alldeviceid
     * @return
     */
    public boolean streamPlay(int srcdeviceid, int subid, int triggeruserval, List<Integer> allres, List<Integer> alldeviceid) {
        InterfaceStream.pbui_Type_MeetDoStreamPlay.Builder builder = InterfaceStream.pbui_Type_MeetDoStreamPlay.newBuilder();
        builder.setSrcdeviceid(srcdeviceid);
        builder.setSubid(subid);
        builder.setTriggeruserval(triggeruserval);
        builder.addAllRes(allres);
        builder.addAllDeviceid(alldeviceid);
        InterfaceStream.pbui_Type_MeetDoStreamPlay build = builder.build();
        call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_START.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.streamPlay:  264.流播放 --->>> ");
        return true;
    }

    /**
     * 265.查询流播放
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryStreamPlay() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_STREAMPLAY.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            Log.e("MyLog", "NativeUtil.queryStreamPlay:  265.查询流播放失败 --->>> ");
            return false;
        }
        InterfaceStream.pbui_Type_MeetStreamPlayDetailInfo pbui_type_meetStreamPlayDetailInfo = InterfaceStream.pbui_Type_MeetStreamPlayDetailInfo.parseFrom(array);
        if (mCallListener != null) {
            mCallListener.callListener(IDivMessage.QUERY_STREAM_PLAY, pbui_type_meetStreamPlayDetailInfo);
            Log.e("MyLog", "NativeUtil.queryStreamPlay:  265.查询流播放成功 --->>> ");
        }
        return true;
    }


    /**
     * 267.查询会议统计
     *
     * @return
     */
    public boolean queryMeetStatistics() {
        InterfaceStatistic.pbui_Type_MeetDoReqStatistic.Builder builder = InterfaceStatistic.pbui_Type_MeetDoReqStatistic.newBuilder();
        InterfaceStatistic.pbui_Type_MeetDoReqStatistic build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSTATISTIC.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.queryMeetStatistics:  267.查询会议统计 --->>> ");
        return true;
    }


    /**
     * 269.按时间段查询会议统计
     *
     * @return
     */
    public boolean queryMeetStatisticsByTime() {
        InterfaceStatistic.pbui_Type_QueryQuarterStatistic.Builder builder = InterfaceStatistic.pbui_Type_QueryQuarterStatistic.newBuilder();
        InterfaceStatistic.pbui_Type_QueryQuarterStatistic build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETSTATISTIC.getNumber(), Pb_METHOD_MEET_INTERFACE_ASK.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.queryMeetStatisticsByTime:  269.按时间段查询会议统计 --->>> ");
        return true;
    }


    /**
     * 271.查询界面配置
     *
     * @return
     * @throws InvalidProtocolBufferException
     */
    public boolean queryInterFaceConfiguration() throws InvalidProtocolBufferException {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber(), null);
        if (array == null) {
            return false;
        }
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo pbui_type_faceConfigInfo = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.parseFrom(array);
        Log.e("MyLog", "NativeUtil.queryInterFaceConfiguration:  271.查询界面配置 --->>> ");
        return true;
    }

    /**
     * 272.修改高级文本界面配置
     *
     * @return
     */
    public boolean modifHighTextInterFaceConfiguration() {
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo.Builder builder = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder();
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifHighTextInterFaceConfiguration:  272.修改高级文本界面配置 --->>> ");
        return true;
    }

    /**
     * 273.修改图片项界面配置
     *
     * @return
     */
    public boolean modifPictureInterFaceConfiguration() {
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo.Builder builder = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder();
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifHighTextInterFaceConfiguration:  273.修改图片项界面配置 --->>> ");
        return true;
    }

    /**
     * 274.修改单纯文本项界面配置
     *
     * @return
     */
    public boolean modifTextInterFaceConfiguration() {
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo.Builder builder = InterfaceFaceconfig.pbui_Type_FaceConfigInfo.newBuilder();
        InterfaceFaceconfig.pbui_Type_FaceConfigInfo build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETFACECONFIG.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_MODIFY.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.modifHighTextInterFaceConfiguration:  274.修改单纯文本项界面配置 --->>> ");
        return true;
    }

    /**
     * 277.清除全部数据
     *
     * @return
     */
    public boolean clearAllInfo() {
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_MEETCLEAR.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLEAR.getNumber(), null);
        Log.e("MyLog", "NativeUtil.clearAllInfo:  277.清除全部数据 --->>> ");
        return true;
    }


    /**
     * 279.升级设备
     *
     * @return
     */
    public boolean updata() {
        InterfaceDevice.pbui_Type_DoDeviceUpdate.Builder builder = InterfaceDevice.pbui_Type_DoDeviceUpdate.newBuilder();
        InterfaceDevice.pbui_Type_DoDeviceUpdate build = builder.build();
        byte[] array = call_method(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber(), InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_UPDATE.getNumber(), build.toByteArray());
        Log.e("MyLog", "NativeUtil.updata:  279.升级设备 --->>> ");
        return true;
    }

    //初始化无纸化接口
    //data 参考无纸化接口对照表
    //成功返回0 失败返回-1
    public native int Init_walletSys(byte[] data);

    //无纸化功能接口中调用
    //type 功能类型
    //method 功能类型的方法
    //data  方法需要的参数 参考无纸化接口对照表
    //成功返回对应的数组 失败返回null数组
    public native byte[] call_method(int type, int method, byte[] data);


    //初始化桌面、摄像头采集
    //type 流类型
    //channelstart 流通道索引值
    //成功返回0 失败返回-1
    public native int InitAndCapture(int type, int channelindex);

    //初始化桌面、摄像头采集
    //type 流类型
    //data 采集数据
    //成功返回0 失败返回-1
    public native int call(int type, byte[] data);

    //JNI获取桌面、摄像头的参数
    //type 流类型
    //oper 参数标识 用于区分获取的数据类型
    //成功返回操作属性对应的值 失败返回-1
    public int callback(int type, int oper) {
        switch (oper) {
            case 1://pixel format
            {
                return 1;
            }
            case 2://width
            {
//                return 1920;1836
                switch (type) {
                    case 2:
                        return MeetingActivity.W;
                    case 3:
                        return CameraDemo.width;
                }

            }
            case 3://height
            {
//                return 1080;1200
                switch (type) {
                    case 2:
                        return MeetingActivity.H;
                    case 3:
                        return CameraDemo.height;
                }

            }
            case 4://start capture
            {
                EventBus.getDefault().post(new EventMessage(IDEventMessage.START_COLLECTION_STREAM_NOTIFY, type));
                return 0;
            }
            case 5://stop capture
            {
                EventBus.getDefault().post(new EventMessage(IDEventMessage.STOP_COLLECTION_STREAM_NOTIFY, type));
                return 0;
            }
        }
        return 0;
    }

    /**
     * bytes转换成十六进制字符串
     * @param byte[] b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n= b.length - 4;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    //无纸化功能接口回调接口
    //type 功能类型
    //method 功能类型的方法
    //data  方法需要的参数 参考无纸化接口对照表
    //datalen data有数据时 datalen就有长度
    //返回0即可
    public int callback_method(int type, int method, byte[] data, int datalen) throws InvalidProtocolBufferException {
        if (datalen <= 0)
            return 0;

        //if(datalen > 4)
        //    Log.e("CaseLog", "logprintf: " + type + "," + method + "data:" + byte2HexStr(data) + "length:" + data.length + "," + datalen);
        switch (type) {
            case 1: //3 高频回调
                InterfaceBase.pbui_Time pbui_time = InterfaceBase.pbui_Time.parseFrom(data);
                //微秒 转换成毫秒 除以 1000
                long usec = pbui_time.getUsec() / 1000;
                String[] date = DateUtil.getGTMDate(usec);
                EventMessage eventMessage = new EventMessage(IDEventMessage.DATE_TIME, usec);
                if (mCallListener != null) {
                    mCallListener.callListener(IDivMessage.UPDATE_TIME, eventMessage);
                    //EventBus发布消息
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.MEET_DATE, date));
                }
                break;
            case 3://67 高频回调
                InterfaceDownload.pbui_Type_DownloadCb pbui_type_downloadCb = InterfaceDownload.pbui_Type_DownloadCb.parseFrom(data);
                int mediaid = pbui_type_downloadCb.getMediaid();
                int err1 = pbui_type_downloadCb.getErr();
                int nstate = pbui_type_downloadCb.getNstate();
                int progress = pbui_type_downloadCb.getProgress();
                if (progress == 100) {
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.DOWN_FINISH, pbui_type_downloadCb));
                }
                Log.e("CaseLog", "NativeUtil.callback_method:  67 下载进度回调  -- 高频回调 --->>> " + progress + "   mediaid: " + mediaid);
                break;
            case 5://68 高频回调
                InterfacePlaymedia.pbui_Type_PlayPosCb pbui_type_playPosCb = InterfacePlaymedia.pbui_Type_PlayPosCb.parseFrom(data);
                int mediaId = pbui_type_playPosCb.getMediaId();
                int per = pbui_type_playPosCb.getPer();
                EventBus.getDefault().post(new EventMessage(IDEventMessage.PLAY_PROGRESS_NOTIFY, pbui_type_playPosCb));
                Log.e("CaseLog", "NativeUtil.callback_method:  68 播放进度通知  -- 高频回调 --->>> mediaId:  " + mediaId + " per: " + per);
                break;
            case 4://72 高频回调
                InterfaceUpload.pbui_TypeUploadPosCb pbui_typeUploadPosCb = InterfaceUpload.pbui_TypeUploadPosCb.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.Upload_Progress, pbui_typeUploadPosCb));
                Log.e("CaseLog", "NativeUtil.callback_method:  72 上传进度通知 -- 高频回调 --->>> ");
                break;
            case 2: //4  平台初始化完毕
                InterfaceBase.pbui_Ready pbui_ready = InterfaceBase.pbui_Ready.parseFrom(data);
                int areaid = pbui_ready.getAreaid();
                Log.e("CaseLog", "NativeUtil.callback_method:  4 平台初始化完毕 --->>> areaid   " + areaid);
                break;
            case 6: //5   设备寄存器变更通知
                InterfaceDevice.pbui_Type_MeetDeviceBaseInfo pbui_type_meetDeviceBaseInfo = InterfaceDevice.pbui_Type_MeetDeviceBaseInfo.parseFrom(data);
                int attribid = pbui_type_meetDeviceBaseInfo.getAttribid();
                int deviceid = pbui_type_meetDeviceBaseInfo.getDeviceid();
                EventBus.getDefault().post(new EventMessage(IDEventMessage.DEV_REGISTER_INFORM, pbui_type_meetDeviceBaseInfo));
//                Log.e("CaseLog", "NativeUtil.callback_method:  5 设备寄存器变更通知 --->>> 寄存器ID ： " + attribid + "  设备ID：" + deviceid);
                break;
            case 7://40
                InterfaceBase.pbui_meetUrl pbui_meetUrl1 = InterfaceBase.pbui_meetUrl.parseFrom(data);
                String netUrl = new String(pbui_meetUrl1.getUrl().toByteArray());
                Log.e("CaseLog", "NativeUtil.callback_method:  40 网页变更通知 --->>> netUrl : " + netUrl);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.NETWEB_INFORM, pbui_meetUrl1));
                break;
            case 8://51-58
                if (method == Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg1 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                    int id = pbui_meetNotifyMsg1.getId();
                    int opermethod = pbui_meetNotifyMsg1.getOpermethod();
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.ADMIN_NOTIFI_INFORM));
                    Log.e("CaseLog", "NativeUtil.callback_method:  51 管理员变更通知 --->>> opermethod : " + opermethod + "  id : " + id);
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_LOGON.getNumber()) {
                    InterfaceAdmin.pbui_Type_AdminLogonStatus pbui_type_adminLogonStatus1 = InterfaceAdmin.pbui_Type_AdminLogonStatus.parseFrom(data);
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.LOGIN_BACK, pbui_type_adminLogonStatus1));
                    Log.e("CaseLog", "NativeUtil.callback_method:  58 管理员登陆返回 --->>> ");
                }
                break;
            case 9:
                InterfaceDevice.pbui_Type_DeviceControl pbui_type_deviceControl1 = InterfaceDevice.pbui_Type_DeviceControl.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  63 变更通知 --->>> ");
                break;
            case 10://73
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg2 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.QUERY_COMMON_PEOPLE));
                Log.e("CaseLog", "NativeUtil.callback_method:  73 常用人员变更通知 --->>> ");
                break;
            case 11://90
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg5 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.MEMBER_CHANGE_INFORM, pbui_meetNotifyMsg5));
                Log.e("CaseLog", "NativeUtil.callback_method:  90 参会人员变更通知 --->>> ");
                break;
            case 12://101
                InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  101 参会人员分组变更通知 --->>> ");
                break;
            case 14://109
                EventBus.getDefault().post(new EventMessage(IDEventMessage.DEVMEETINFO_CHANGE_INFORM));
                Log.e("CaseLog", "NativeUtil.callback_method:  109 设备会议信息变更通知 --->>> ");
                break;
            case 15://111
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg6 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                int id4 = pbui_meetNotifyMsg6.getId();
                Log.e("CaseLog", "NativeUtil.callback_method:  111 会场信息变更通知 --->>> ");
                EventBus.getDefault().post(new EventMessage(IDEventMessage.PLACEINFO_CHANGE_INFORM));

//                    InterfaceBase.pbui_MeetNotifyMsg placeInfoChangeInform = InterfaceBase.pbui_MeetNotifyMsg.getDefaultInstance();
//                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg6 = placeInfoChangeInform.parseFrom(data);
//                    Log.e("MyLog", "NativeUtil.callback_method:  118.查询指定ID的会场变更通知 --->>> ");
//                    EventBus.getDefault().post(new EventMessage(IDEventMessage.QUERY_PLACE_BYID,id4));
                break;
            case 16://119
                InterfaceBase.pbui_MeetNotifyMsgForDouble pbui_meetNotifyMsgForDouble1 = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.PLACE_DEVINFO_CHANGEINFORM, pbui_meetNotifyMsgForDouble1));
                Log.e("CaseLog", "NativeUtil.callback_method:  119 会场设备信息变更通知 --->>> ");
                break;
            case 17://257
                InterfacePlaymedia.pbui_Type_FilePush.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  257 文件推送通知 --->>> ");
                break;
            case 18://259
                InterfaceStream.pbui_Type_ReqStreamPush pbui_type_reqStreamPush = InterfaceStream.pbui_Type_ReqStreamPush.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  259 请求播放流通道通知 --->>> ");
                break;
            case 19://261
                InterfaceStream.pbui_Type_StreamPush.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  261 推送流通知 --->>> ");
                break;
            case 20://127
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg7 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.MEETINFO_CHANGE_INFORM, pbui_meetNotifyMsg7));
                Log.e("CaseLog", "NativeUtil.callback_method:  127 会议信息变更通知 --->>> ");
                break;
            case 21://33
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg3 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventAgenda());
                Log.e("CaseLog", "NativeUtil.callback_method:  33 议程变更通知 --->>> ");
                break;
            case 22://43-50
                if (method == Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg1 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                    EventBus.getDefault().post(new EventNotice());
                    Log.e("CaseLog", "NativeUtil.callback_method:  43 公告变更通知 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_PUBLIST.getNumber()) {
                    InterfaceBullet.pbui_BulletDetailInfo pbui_bulletDetailInfo1 = InterfaceBullet.pbui_BulletDetailInfo.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  50 发布公告通知 --->>> ");
                }
                break;
            case 23://135
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg4 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                int id2 = pbui_meetNotifyMsg4.getId();
//                EventBus.getDefault().post(new EventMeetDir(id2));
                EventBus.getDefault().post(new EventMessage(IDEventMessage.MEETDIR_CHANGE_INFORM, pbui_meetNotifyMsg4));
                Log.e("CaseLog", "NativeUtil.callback_method:  135 会议目录变更通知 --->>> ");
                break;
            case 24://142-278
                if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADD.getNumber()) {
                    InterfaceFile.pbui_Type_MeetNewRecordFile pbui_type_meetNewRecordFile = InterfaceFile.pbui_Type_MeetNewRecordFile.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  278 有新的录音文件媒体文件通知 --->>> ");
                } else if (method == Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    InterfaceBase.pbui_MeetNotifyMsgForDouble pbui_meetNotifyMsgForDouble = InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(data);
//                    int id1 = pbui_meetNotifyMsgForDouble.getId();
//                    EventBus.getDefault().post(new EventMeetDirFile(id1));
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.MEETDIR_FILE_CHANGE_INFORM, pbui_meetNotifyMsgForDouble));
                    Log.e("CaseLog", "NativeUtil.callback_method:  142 会议目录文件变更通知 --->>> ");
                }
                break;
            case 25://153
                InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  153 会议目录权限变更通知 --->>> ");
                break;
            case 26://171
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg12 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.Meet_vedio_changeInform, pbui_meetNotifyMsg12));
                Log.e("CaseLog", "NativeUtil.callback_method:  171 会议视频变更通知 --->>> ");
                break;
            case 27://177
                Log.e("CaseLog", "NativeUtil.callback_method:  177 会议双屏显示信息变更通知 --->>> ");
                break;
            case 28://180
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg9 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.MeetSeat_Change_Inform, pbui_meetNotifyMsg9));
                Log.e("CaseLog", "NativeUtil.callback_method:  180 会议排位变更通知 --->>> ");
                break;
            case 29://184
                InterfaceIM.pbui_Type_MeetIM pbui_type_meetIM = InterfaceIM.pbui_Type_MeetIM.parseFrom(data);
                if (mCallListener != null) {
                    mCallListener.callListener(IDivMessage.RECEIVE_MEET_IMINFO, pbui_type_meetIM);
                }
//                EventBus.getDefault().post(new EventMessage(IDEventMessage.Receive_MeetChat_Info,pbui_type_meetIM));
                Log.e("CaseLog", "NativeUtil.callback_method:  184 收到新的会议交流信息 --->>> ");
                break;
            case 30://188
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg11 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.newVote_launch_inform, pbui_meetNotifyMsg11));
                Log.e("CaseLog", "NativeUtil.callback_method:  188 有新的投票发起通知 --->>> ");
                break;
            case 31://198
                InterfaceBase.pbui_MeetNotifyMsg queryVote = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.Vote_Change_Inform, queryVote));
                Log.e("MyLog", "NativeUtil.callback_method:  198 投票变更通知 --->>> ");
                break;
            case 32://202
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg10 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.VoteMember_ChangeInform, pbui_meetNotifyMsg10));
                Log.e("CaseLog", "NativeUtil.callback_method:  202 投票提交人变更通知 --->>> ");
                break;
            case 33://59
                InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg8 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.MEETADMIN_PLACE_NOTIFIINFROM));
                Log.e("CaseLog", "NativeUtil.callback_method:  59 会议管理员控制的会场变更通知 --->>> ");
                break;
            case 34://205
                if (method == Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    InterfaceBase.pbui_MeetNotifyMsg pbui_meetNotifyMsg1 = InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                    int opermethod1 = pbui_meetNotifyMsg1.getOpermethod();
                    int id1 = pbui_meetNotifyMsg1.getId();
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.SIGN_CHANGE_INFORM, pbui_meetNotifyMsg1));
                    Log.e("CaseLog", "NativeUtil.callback_method:  205 签到变更通知 --->>> opermethod1 ： " + opermethod1 + "  id1 : " + id1);
                }
                break;
            case 35://214-233
                if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ASK.getNumber()) {
                    InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard pbui_type_meetStartWhiteBoard = InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard.parseFrom(data);
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.OPEN_BOARD, pbui_type_meetStartWhiteBoard));
                    Log.e("CaseLog", "NativeUtil.callback_method:  214 收到白板打开操作 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REJECT.getNumber()) {
                    InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  216 拒绝加入通知 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER.getNumber()) {
                    InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  218 同意加入通知 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_EXIT.getNumber()) {
                    InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper pbui_type_meetWhiteBoardOper = InterfaceWhiteboard.pbui_Type_MeetWhiteBoardOper.parseFrom(data);
                    int srcmemid = pbui_type_meetWhiteBoardOper.getSrcmemid();
                    long srcwbid = pbui_type_meetWhiteBoardOper.getSrcwbid();
                    Log.e("CaseLog", "NativeUtil.callback_method:  219 参会人员退出白板通知 --->>> srcmemid " + srcmemid + "  srcwbid :" + srcwbid);
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_CLEAR.getNumber()) {
                    InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  220 白板清空记录通知 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_DEL.getNumber()) {
                    InterfaceWhiteboard.pbui_Type_MeetClearWhiteBoard.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  221 白板删除记录通知 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDINK.getNumber()) {
                    InterfaceWhiteboard.pbui_Type_MeetWhiteBoardInkItem.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  224 添加墨迹通知 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDRECT.getNumber()) {
                    InterfaceWhiteboard.pbui_Item_MeetWBRectDetail.parseFrom(data);
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.ADD_DRAW_INFORM,InterfaceWhiteboard.pbui_Item_MeetWBRectDetail.parseFrom(data)));
                    Log.e("CaseLog", "NativeUtil.callback_method:  227 添加矩形、直线、圆形通知 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDTEXT.getNumber()) {
                    InterfaceWhiteboard.pbui_Item_MeetWBTextDetail.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  230 添加文本通知 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ADDPICTURE.getNumber()) {
                    InterfaceWhiteboard.pbui_Item_MeetWBPictureDetail.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  233 添加图片通知 --->>> ");
                }
                break;
            case 36://236
                InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  236 会议功能变更通知 --->>> ");
                break;
            case 37://100
                InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  100 参会人员权限变更通知 --->>> ");
                break;
            case 38://79
                InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  79 常用人员分组变更通知 --->>> ");
                break;
            case 39://84-106 // TODO: 2017/12/29 ？？？两个回调一样 
                InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  84 常用人员分组人员变更通知 --->>> ");
                if (method == Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    InterfaceBase.pbui_MeetNotifyMsgForDouble.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  106 参会人员分组人员变更通知 --->>> ");
                }
                break;
            case 40://242
                InterfaceStream.pbui_Type_MeetScreenKeyBoardControl.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  242 鼠标操作通知 --->>> ");
                break;
            case 41://244
                InterfaceStream.pbui_Type_MeetScreenKeyBoardControl.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  244 键盘操作通知 --->>> ");
                break;
            case 42://10.界面状态变更通知
                InterfaceDevice.pbui_MeetDeviceMeetStatus pbui_meetDeviceMeetStatus = InterfaceDevice.pbui_MeetDeviceMeetStatus.parseFrom(data);
                int facestatus = pbui_meetDeviceMeetStatus.getFacestatus();
                int deviceid1 = pbui_meetDeviceMeetStatus.getDeviceid();
                int meetingid = pbui_meetDeviceMeetStatus.getMeetingid();
                int memberid = pbui_meetDeviceMeetStatus.getMemberid();
                EventBus.getDefault().post(new EventMessage(IDEventMessage.FACESTATUS_CHANGE_INFORM, pbui_meetDeviceMeetStatus));
                Log.e("CaseLog", "NativeUtil.callback_method:  界面状态变更通知回调 --->>> 界面状态： " + facestatus + " 设备ID ： " + deviceid1 + "  会议ID： " + meetingid + " 成员ID： " + memberid);
                break;
            case 43://15-30
                if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ZOOM.getNumber()) {
                    InterfaceDevice.pbui_MeetZoomResWin pbui_meetZoomResWin = InterfaceDevice.pbui_MeetZoomResWin.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  15 窗口缩放变更 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ENTER.getNumber()) {
                    Log.e("CaseLog", "NativeUtil.callback_method:  17 辅助签到变更通知 --->>> ");
                    EventBus.getDefault().post(new EventMessage(IDEventMessage.SIGN_EVENT));
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_BROADCAST.getNumber()) {
                    InterfaceDevice.pbui_Type_MeetMemberCastInfo pbui_type_meetMemberCastInfo = InterfaceDevice.pbui_Type_MeetMemberCastInfo.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  21 收到参会人员数量和签到数量广播通知 --->>> ");
                    if (mCallListener != null) {
                        mCallListener.callListener(IDivMessage.BROADCAST_COUNT_ATTENDEE, pbui_type_meetMemberCastInfo);
                    }
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTTOMANAGE.getNumber()) {
                    InterfaceDevice.pbui_Type_MeetRequestManageNotify.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  24 收到请求成为管理员请求 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSETOMANAGE.getNumber()) {
                    InterfaceDevice.pbui_Type_MeetRequestManageResponse.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  26 收到请求成为管理员回复 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_REQUESTPRIVELIGE.getNumber()) {
                    InterfaceDevice.pbui_Type_MeetRequestPrivilegeNotify.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  28 收到请求参会人员权限请求 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_RESPONSEPRIVELIGE.getNumber()) {
                    InterfaceDevice.pbui_Type_MeetRequestPrivilegeResponse.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  30 收到参会人员权限请求回复 --->>> ");
                }
                break;
            case 44://250
                InterfaceBase.pbui_Type_MeetDBServerOperError pbui_type_meetDBServerOperError = InterfaceBase.pbui_Type_MeetDBServerOperError.parseFrom(data);
                int status = pbui_type_meetDBServerOperError.getStatus();
                switch (status) {
                    case 0://多条查询记录
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 0 --->>> 多条查询记录");
                        break;
                    case 1://单条查询记录
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 1 --->>> 单条查询记录");
                        break;
                    case 2://无返回记录
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 2 --->>> 无返回记录");
                        break;
                    case 3://操作成功
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 3 --->>> 操作成功");
                        break;
                    case 4://请求失败
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 4 --->>> 请求失败");
                        break;
                    case 5://数据库异常
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 5 --->>> 数据库异常");
                        break;
                    case 6://服务器异常
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 6 --->>> 服务器异常");
                        break;
                    case 7://权限限制
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 7 --->>> 权限限制");
                        break;
                    case 8://密码错误
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 8 --->>> 密码错误");
                        break;
                    case 9://创建会议有冲突
                        Log.e("MyLog", "NativeUtil.callback_method: 250 数据后台回复的信息 9 --->>> 创建会议有冲突");
                        break;
                }
                break;
            case 45://251
                InterfacePlaymedia.pbui_Type_MeetMediaPlay pbui_type_meetMediaPlay = InterfacePlaymedia.pbui_Type_MeetMediaPlay.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.MEDIA_PLAY_INFORM, pbui_type_meetMediaPlay));
                Log.e("CaseLog", "NativeUtil.callback_method:  251 媒体播放通知 --->>> ");
                break;
            case 46://263
                InterfaceStream.pbui_Type_MeetStreamPlay streamPlayInform = InterfaceStream.pbui_Type_MeetStreamPlay.getDefaultInstance();
                InterfaceStream.pbui_Type_MeetStreamPlay pbui_type_meetStreamPlay = streamPlayInform.parseFrom(data);
                EventBus.getDefault().post(new EventMessage(IDEventMessage.PLAY_STREAM_NOTIFY, pbui_type_meetStreamPlay));
                Log.e("CaseLog", "NativeUtil.callback_method:  263 流播放通知 --->>> ");
                break;
            case 47://246-247
                if (method == Pb_METHOD_MEET_INTERFACE_CLOSE.getNumber()) {
                    InterfaceStop.pbui_Type_MeetStopResWork stopResourceInform = InterfaceStop.pbui_Type_MeetStopResWork.getDefaultInstance();
                    InterfaceStop.pbui_Type_MeetStopResWork pbui_type_meetStopResWork = stopResourceInform.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  246 停止资源通知 --->>> ");
                } else if (method == Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    InterfaceStop.pbui_Type_MeetStopPlay pbui_type_meetStopPlay = InterfaceStop.pbui_Type_MeetStopPlay.parseFrom(data);
                    if (pbui_type_meetStopPlay.getCreatedeviceid() != MeetingActivity.getDevId())
                        EventBus.getDefault().post(new EventMessage(IDEventMessage.STOP_PLAY, Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()));
                    Log.e("CaseLog", "NativeUtil.callback_method:  247 停止播放通知 --->>> ");
                }
                break;
            case 48://239
                if (method == Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  239 参会人员白板颜色变更通知 --->>> ");
                }
                break;
            case 51://266-268
                if (method == Pb_METHOD_MEET_INTERFACE_NOTIFY.getNumber()) {
                    InterfaceStatistic.pbui_Type_MeetStatisticInfo.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  266 返回查询会议统计通知 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ASK.getNumber()) {
                    InterfaceStatistic.pbui_Type_MeetQuarterStatisticInfo.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  268 返回按时间段查询的会议统计通知 --->>> ");
                }
                break;
            case 52://270
                InterfaceBase.pbui_MeetNotifyMsg.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  270 界面配置变更通知 --->>> ");
                break;
            case 54://154
                if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_QUERY.getNumber()) {
                    InterfaceFile.pbui_Type_MeetFileScore.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  154 返回查询会议文件评分 --->>> ");
                } else if (method == InterfaceMacro.Pb_Method.Pb_METHOD_MEET_INTERFACE_ASK.getNumber()) {
                    InterfaceFile.pbui_Type_QueryAverageFileScore.parseFrom(data);
                    Log.e("CaseLog", "NativeUtil.callback_method:  155 返回查询会议文件评分的平均分 --->>> ");
                }
                break;
            case 55://161
                InterfaceFile.pbui_Type_MeetingFileEvaluate.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  161 返回查询会议文件评价  --->>> ");
                break;
            case 56://165
                InterfaceMeet.pbui_Type_MeetingMeetEvaluate.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  165 返回查询会议评价 --->>> ");
                break;
            case 57://169
                InterfaceSystemlog.pbui_Type_MeetingMeetSystemLog.parseFrom(data);
                Log.e("CaseLog", "NativeUtil.callback_method:  169 返回查询管理员日志 --->>> ");
                break;
            default:
                android.util.Log.i("NativeUtil", "callback_method: type: " + type + ", method: " + method);
                Log.e("MyLog", "NativeUtil.callback_method 3969行:  默认回调 --->>> ");
                break;
        }
        return 0;
    }


}
