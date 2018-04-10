package com.pa.paperless.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaCodecInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceContext;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.pa.paperless.R;
import com.pa.paperless.adapter.MainMemberAdapter;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.service.PlayService;
import com.pa.paperless.utils.DateUtil;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.AvcEncoder;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ini4j.Ini;
import org.libsdl.app.SDLActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.pa.paperless.utils.MyUtils.handTo;

/**
 * 必须先修改 app/build.gradle中的 jniLibs的路径
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, CallListener, EasyPermissions.PermissionCallbacks {

    public static TextView mCompanyName, mMainMeetName;
    private TextView mMainNowTime, mMainNowDate, mMainNowWeek, mMainMemberJob, mDeviceNameId, mMainMemberName, mMainUnit;
    private Button mMianIntoMeeting, mMainSecretaryManage;

    /*handle接收*/
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IDivMessage.UPDATE_TIME:
                    ArrayList datatime = msg.getData().getParcelableArrayList("datatime");
                    EventMessage o2 = (EventMessage) datatime.get(0);
                    long object = (long) o2.getObject();
                    String[] date = DateUtil.getGTMDate(object);
                    upDateMainTimeUI(date);
                    break;
                case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                    ArrayList devMeetInfos = msg.getData().getParcelableArrayList("devMeetInfos");
                    if (devMeetInfos != null) {
                        upDateMainUI(devMeetInfos);
                    }
                    break;
                case IDivMessage.QUERY_DEVICE_INFO://查询设备信息
                    ArrayList devInfos = msg.getData().getParcelableArrayList("devInfos");
                    if (devInfos != null) {
                        InterfaceDevice.pbui_Type_DeviceDetailInfo o = (InterfaceDevice.pbui_Type_DeviceDetailInfo) devInfos.get(0);
                        List<DeviceInfo> deviceInfos = Dispose.DevInfo(o);
                        for (int i = 0; i < deviceInfos.size(); i++) {
                            if (deviceInfos.get(i).getDevId() == mLocalDevID) {
                                String devName = deviceInfos.get(i).getDevName();
                                mDeviceNameId.setText(devName + " | " + mLocalDevID);
                                Log.e("MyLog", "MainActivity.handleMessage 115行:  本机设备名称与设备ID --->>> " + devName + " | " + mLocalDevID);
                            }
                        }
                    }
                    break;
//                case IDivMessage.QUERY_ATTENDEE:
//                    ArrayList queryMember_main = msg.getData().getParcelableArrayList("queryMember_main");
//                    InterfaceMember.pbui_Type_MemberDetailInfo o = (InterfaceMember.pbui_Type_MemberDetailInfo) queryMember_main.get(0);
//                    List<MemberInfo> memberInfos = Dispose.MemberInfo(o);
//
//                    //将参会人信息 diaLog展示
////                    showCheckMemberDialog(memberInfos);
//                    break;
                case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                    ArrayList queryMeetSeat = msg.getData().getParcelableArrayList("queryMeetSeat");
                    InterfaceRoom.pbui_Type_MeetSeatDetailInfo o5 = (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) queryMeetSeat.get(0);
                    List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> itemList3 = o5.getItemList();
                    for (int i = 0; i < itemList3.size(); i++) {
                        InterfaceRoom.pbui_Item_MeetSeatDetailInfo pbui_item_meetSeatDetailInfo = itemList3.get(i);
                        int nameId = pbui_item_meetSeatDetailInfo.getNameId();//参会人员ID
                        int role = pbui_item_meetSeatDetailInfo.getRole();// 身份：等于3则说明是主持人
                        int seatid = pbui_item_meetSeatDetailInfo.getSeatid();//设备ID
                        Log.e("MyLog", "MeetingActivity.handleMessage:  人员ID： --->>> " + nameId + "  角色ID：" + role + "  设备ID：" + seatid);
                        if (role == 3) {
                            //role为3则是主持人
                            CompereID = nameId;
                            try {
                                /** **** **  91.查询指定ID的参会人员  ** **** **/
                                nativeUtil.queryAttendPeopleFromId(CompereID);
                                Log.e("MyLog", "MainActivity.handleMessage:  主持人的ID为： --->>> " + nameId);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case IDivMessage.QUERY_ATTEND_BYID://91.查询指定ID的参会人员
                    ArrayList queryAttendeeById = msg.getData().getParcelableArrayList("queryAttendeeById");
                    InterfaceMember.pbui_Type_MemberDetailInfo o1 = (InterfaceMember.pbui_Type_MemberDetailInfo) queryAttendeeById.get(0);
                    List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = o1.getItemList();
                    CompereInfo = itemList.get(0);
                    compereName = MyUtils.getBts(CompereInfo.getName());
                    int personid = CompereInfo.getPersonid();
                    Log.e("MyLog", "MainActivity.handleMessage:  主持人姓名： --->>> " + compereName + "  personid： " + personid);
                    break;
                case IDivMessage.QUERY_CONTEXT://按属性ID查询指定上下文属性
                    ArrayList queryContext = msg.getData().getParcelableArrayList("queryContext");
                    InterfaceContext.pbui_MeetContextInfo o = (InterfaceContext.pbui_MeetContextInfo) queryContext.get(0);
                    int propertyid = o.getPropertyid();
                    mLocalDevID = o.getPropertyval();
                    String bts = MyUtils.getBts(o.getPropertytext());
                    Log.e("MyLog", "MainActivity.handleMessage 166行: propertyid--->>> " + propertyid + "  本机设备ID  " + mLocalDevID
                            + "  o.getPropertytext()  " + bts);
                    try {
                        /** **** **  7.按属性ID查询指定设备属性  ** **** **/
                        nativeUtil.queryDevicePropertiesById(InterfaceMacro.Pb_MeetDevicePropertyID.Pb_MEETDEVICE_PROPERTY_NAME.getNumber()
                                , 0, mLocalDevID);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    break;
                case IDivMessage.DEV_PROBYID://（本机的设备名称）
                    ArrayList queryDevProById = msg.getData().getParcelableArrayList("queryDevProById");
                    byte[] o3 = (byte[]) queryDevProById.get(0);
                    try {
                        InterfaceDevice.pbui_DeviceStringProperty pbui_deviceStringProperty = InterfaceDevice.pbui_DeviceStringProperty.parseFrom(o3);
                        String LocaldevName = MyUtils.getBts(pbui_deviceStringProperty.getPropertytext());
                        Log.e("MyLog", "MainActivity.handleMessage 185行:  本机的设备名称 --->>> " + LocaldevName);
                        mDeviceNameId.setText(LocaldevName + " | " + mLocalDevID);
                        //110.查询设备会议信息
                        haveDevMeetInfo = nativeUtil.queryDeviceMeetInfo();
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    private PopupWindow mPopupWindow;
    public static InterfaceDevice.pbui_Type_DeviceFaceShowDetail mLocalDevMeetInfo;//设备会议信息
    private String compereName;
    private int CompereID;//主持人的参会人员ID
    //是否有会议信息
    private boolean haveDevMeetInfo;
    private Intent serviceIntent;
    private static InterfaceMember.pbui_Item_MemberDetailInfo CompereInfo;//主持人的信息
    public MainActivity mContext;
    private PopupWindow ipEdtPop;
    private int mLocalDevID;
    public static String uniqueId;

    /**
     * 获取主持人的信息
     *
     * @return
     */
    public static InterfaceMember.pbui_Item_MemberDetailInfo getCompereInfo() {
        return CompereInfo != null ? CompereInfo : null;
    }

    /**
     * 获取本机的设备会议信息
     *
     * @return
     */
    public static InterfaceDevice.pbui_Type_DeviceFaceShowDetail getLocalInfo() {
        return mLocalDevMeetInfo != null ? mLocalDevMeetInfo : null;
    }

    /**
     * 展示选择参会人 弹出框
     *
     * @param memberInfos
     */
    private void showCheckMemberDialog(List<MemberInfo> memberInfos) {

        View popupView = getLayoutInflater().inflate(R.layout.pop_member, null);
        mPopupWindow = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        MemberViewHolder holder = new MemberViewHolder(popupView);
        //事件处理
        MemberEvent(holder, memberInfos);
        mPopupWindow.showAtLocation(findViewById(R.id.mainactivity_id), Gravity.CENTER, 0, 0);
    }

    private void MemberEvent(MemberViewHolder holder, List<MemberInfo> memberInfos) {
        //设置参会人列表数据
        MainMemberAdapter adapter = new MainMemberAdapter(getApplicationContext(), memberInfos);
//        holder.pop_member.addItemDecoration(new RecycleViewDivider(getApplicationContext(), LinearLayoutManager.HORIZONTAL));
        holder.pop_member.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        holder.pop_member.setAdapter(adapter);
        //
        holder.pop_member_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.pop_member_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

    }


    public static class MemberViewHolder {
        public View rootView;
        public TextView pop_tv_title;
        public RecyclerView pop_member;
        public Button pop_member_ok;
        public Button pop_member_cancel;

        public MemberViewHolder(View rootView) {
            this.rootView = rootView;
            this.pop_tv_title = (TextView) rootView.findViewById(R.id.pop_tv_title);
            this.pop_member = (RecyclerView) rootView.findViewById(R.id.pop_member);
            this.pop_member_ok = (Button) rootView.findViewById(R.id.pop_member_ok);
            this.pop_member_cancel = (Button) rootView.findViewById(R.id.pop_member_cancel);
        }

    }

    private void upDateMainTimeUI(String[] strings) {
        if (strings != null) {
            mMainNowDate.setText(strings[0]);
            mMainNowWeek.setText(strings[1]);
            mMainNowTime.setText(strings[2]);
        }
    }

    private void upDateMainUI(ArrayList arrayList) {
        mLocalDevMeetInfo = (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) arrayList.get(0);
        String company = MyUtils.getBts(mLocalDevMeetInfo.getCompany());
        String job = MyUtils.getBts(mLocalDevMeetInfo.getJob());
        String memberName = MyUtils.getBts(mLocalDevMeetInfo.getMembername());
        String meetingName = MyUtils.getBts(mLocalDevMeetInfo.getMeetingname());
        int deviceid = mLocalDevMeetInfo.getDeviceid();
        int memberid = mLocalDevMeetInfo.getMemberid();
        Log.e("MyLog", "MainActivity.upDateMainUI:   --->>> 本机设备ID: " + deviceid + "  本机人员ID：" + memberid);
        mCompanyName.setText(company);
        mMainMeetName.setText(meetingName);
        mMainMemberJob.setText(job);
        mMainMemberName.setText(memberName);
        try {
            //查询设备信息
            nativeUtil.queryDeviceInfo();
            /** ************ ******  181.查询会议排位  ****** ************ **/
            nativeUtil.queryMeetRanking();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        //  动态申请权限 如果设备是6.0或以上 则动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initPermissions();
        }
        //获取手机/平板的唯一标识
        uniqueId = MyUtils.getUniqueId(mContext);
        Log.e("MyLog", "MainActivity.onCreate 322行:   手机的唯一标识符--->>> " + uniqueId);
        initConfFile();
        initController();
        initView();
        String[] arr = {};
        SDLActivity.nativeInit(arr);
        //  初始化无纸化网络平台
        nativeUtil.javaInitSys();
        // 初始化流通道
        int format = AvcEncoder.selectColorFormat(AvcEncoder.selectCodec("video/avc"), "video/avc");
        switch (format) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                NativeUtil.COLOR_FORMAT = 0;
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                NativeUtil.COLOR_FORMAT = 1;
                break;
        }
        nativeUtil.InitAndCapture(0, 2);
        nativeUtil.InitAndCapture(0, 3);
        //8.修改本机界面状态
        nativeUtil.setInterfaceState(InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace.getNumber());
        try {
            /** **** **  31.按属性ID查询指定上下文属性  ** **** **/
            nativeUtil.queryContextProperty(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_SELFID.getNumber());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().register(this);
        serviceIntent = new Intent(this, PlayService.class);
        startService(serviceIntent);

    }


    // 复制ini、dev文件
    private void initConfFile() {
        //拷贝配置文件
        File path = new File(Macro.INITFILESDPATH + "/" + Macro.FILENAME);
        if (!path.exists()) {
            copyTo("client.ini", Macro.INITFILESDPATH, Macro.FILENAME);
        } else {
//            path.delete();
//            copyTo("client.ini", Macro.INITFILESDPATH, Macro.FILENAME);
        }
        File path_class = new File(Macro.INITFILESDPATH + "/" + Macro.FILENAME_DEV);
        if (!path_class.exists()) {
            copyTo("client.dev", Macro.INITFILESDPATH, Macro.FILENAME_DEV);
        } else {
            path_class.delete();
            copyTo("client.dev", Macro.INITFILESDPATH, Macro.FILENAME_DEV);
        }
    }

    // 复制文件
    private void copyTo(String fromPath, String toPath, String fileName) {
        // 复制位置
        // opPath：mnt/sdcard/lcuhg/health/
        // mnt/sdcard：表示sdcard
        File toFile = new File(toPath);
        // 如果不存在，创建文件夹
        if (!toFile.exists()) {
            boolean isCreate = toFile.mkdirs();
            // 打印创建结果
            Log.i("create dir", String.valueOf(isCreate));
        }

        try {
            // 根据文件名获取assets文件夹下的该文件的inputstream
            InputStream fromFileIs = mContext.getResources().getAssets().open(fromPath);
            int length = fromFileIs.available(); // 获取文件的字节数
            byte[] buffer = new byte[length]; // 创建byte数组
            FileOutputStream fileOutputStream = new FileOutputStream(toFile + "/" + fileName); // 字节输入流
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    fromFileIs);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    fileOutputStream);
            int len = bufferedInputStream.read(buffer);
            while (len != -1) {
                bufferedOutputStream.write(buffer, 0, len);
                len = bufferedInputStream.read(buffer);
            }
            bufferedInputStream.close();
            bufferedOutputStream.close();
            fromFileIs.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理EventBus 发送的信息
     *
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.MEETINFO_CHANGE_INFORM:
                Log.e("MyLog", "MainActivity.getEventMessage:  127.会议信息变更通知 EventBus --->>> ");
                //110.查询设备会议信息
                nativeUtil.queryDeviceMeetInfo();
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM:
                Log.e("MyLog", "MainActivity.getEventMessage:  参会人员变更通知 EventBus --->>> ");
                //92.查询参会人员
//                nativeUtil.queryAttendPeople();
                InterfaceBase.pbui_MeetNotifyMsg object1 = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                Log.e("MyLog", "MainActivity.getEventMessage:  查询指定ID的参会人员  object1.getId() --->>> " + object1.getId()
                        + "  object1.getOpermethod()" + object1.getOpermethod());
                //91.查询指定ID的参会人员
                nativeUtil.queryAttendPeopleFromId(CompereID);

                break;
            case IDEventMessage.DEVMEETINFO_CHANGE_INFORM:
                Log.e("MyLog", "MainActivity.getEventMessage:  109.设备会议信息变更通知 EventBus --->>> ");
                //110.查询设备会议信息
                nativeUtil.queryDeviceMeetInfo();
                break;
            case IDEventMessage.SIGN_EVENT:
                Log.e("MyLog", "MainActivity.getEventMessage:  17 辅助签到变更通知 EventBus --->>> ");
                //18.辅助签到操作
//                nativeUtil.signAlterationOperate(mLocalDevMeetInfo.getDeviceid());
                break;
            case IDEventMessage.MeetSeat_Change_Inform:
                Log.e("MyLog", "MainActivity.getEventMessage:  180.会议排位变更通知 EventBus --->>> ");
                InterfaceBase.pbui_MeetNotifyMsg object = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                int id = object.getId();
                int opermethod = object.getOpermethod();
                Log.e("MyLog", "MainActivity.getEventMessage:  会议排位返回的数据： --->>> id " + id + "  opermethod ：" + opermethod);
                //181.查询会议排位
                nativeUtil.queryMeetRanking();
                //7.按属性ID查询指定设备属性
//                nativeUtil.queryDevicePropertiesById(id);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(serviceIntent);
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }

    /**
     * 动态权限申请
     */
    private static final int RC_CAMERA_PERM = 123;

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private void initPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            EasyPermissions.requestPermissions(this, "需要获取该权限！", RC_CAMERA_PERM, PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        for (int i = 0; i < perms.size(); i++) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("请求权限")
                    .setRationale("是否同意开启" + perms.get(i) + "权限")
                    .build()
                    .show();
        }
    }

    private void initView() {
        mCompanyName = (TextView) findViewById(R.id.company_name);
        mMainNowTime = (TextView) findViewById(R.id.main_now_time);
        mMainNowDate = (TextView) findViewById(R.id.main_now_date);
        mMainNowWeek = (TextView) findViewById(R.id.main_now_week);
        mMianIntoMeeting = (Button) findViewById(R.id.mian_into_meeting);
        mMainSecretaryManage = (Button) findViewById(R.id.main_secretary_manage);
        mMianIntoMeeting.setOnClickListener(this);
        mMainSecretaryManage.setOnClickListener(this);
        mMainMeetName = (TextView) findViewById(R.id.main_meetName);
        mMainUnit = (TextView) findViewById(R.id.main_unit);
        mMainMemberName = (TextView) findViewById(R.id.main_memberName);
        mMainMemberJob = (TextView) findViewById(R.id.main_memberJob);
        mDeviceNameId = (TextView) findViewById(R.id.device_name_id);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mian_into_meeting://进入会议
                if (haveDevMeetInfo) {
                    gotoMeet();
                } else {
                    try {
                        //110.查询设备会议信息
                        haveDevMeetInfo = nativeUtil.queryDeviceMeetInfo();
                        if (!haveDevMeetInfo) {
                            //如果没有查找到
                            showDialog();
                        }
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.main_secretary_manage://秘书管理.....修改IP地址
//                startActivityForResult(new Intent(MainActivity.this, ManageActivity.class), IDivMessage.MAIN_REQUEST_CODE);
                showInputIniIp();
                break;
        }
    }

    /**
     * 展示修改ini文件IP地址弹出框
     */
    private void showInputIniIp() {
        View popupView = getLayoutInflater().inflate(R.layout.pop_ipfilter, null);
        ipEdtPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(ipEdtPop);
        ipEdtPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        ipEdtPop.setTouchable(true);
        ipEdtPop.setOutsideTouchable(true);
        IpViewHolder holder = new IpViewHolder(popupView);
        IPHolderEvent(holder);
        ipEdtPop.showAtLocation(findViewById(R.id.mainactivity_id), Gravity.CENTER, 0, 0);
    }

    private void IPHolderEvent(final IpViewHolder holder) {
        final File iniFile = new File(Macro.INITFILESDPATH + "/" + Macro.FILENAME);
        final Ini ini = new Ini();
        try {
            ini.load(new FileReader(iniFile));
            String nowIp = ini.get("areaaddr", "area0ip");
            Log.e("MyLog", "MainActivity.onClick 573行:   --->>> " + nowIp);
            String[] split = nowIp.split("\\.");
            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    holder.ipPopEdt1.setText(split[i]);
                }
                if (i == 1) {
                    holder.ipPopEdt2.setText(split[i]);
                }
                if (i == 2) {
                    holder.ipPopEdt3.setText(split[i]);
                }
                if (i == 3) {
                    holder.ipPopEdt4.setText(split[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.ipPopConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newIP = "";
                newIP += holder.ipPopEdt1.getText().toString() + ".";
                newIP += holder.ipPopEdt2.getText().toString() + ".";
                newIP += holder.ipPopEdt3.getText().toString() + ".";
                newIP += holder.ipPopEdt4.getText().toString();
                if (!newIP.equals("")) {
                    try {
                        ini.put("areaaddr", "area0ip", newIP);
                        ini.store(iniFile);//修改后提交
                        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
                        manager.restartPackage("com.pa.paperless");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        holder.ipPopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipEdtPop.dismiss();
            }
        });
    }

    //跳转到会议界面
    private void gotoMeet() {
        if (mLocalDevMeetInfo != null) {
            nativeUtil.sendSign("","",InterfaceMacro.Pb_MeetSignType.Pb_signin_direct.getNumber());
            Intent intent = new Intent(MainActivity.this, MeetingActivity.class);
            startActivityForResult(intent, IDivMessage.MAIN_REQUEST_CODE);
        } else {
            Toast.makeText(mContext, "没有查找到会议", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IDivMessage.MAIN_REQUEST_CODE:
                //进入其它页面后 nativeUtil对象就改变了，当返回主界面后就要重新设置
                Log.e("MyLog", "MainActivity.onActivityResult:  进入了onActivityResult方法 --->>> ");
                startActivity(new Intent(this, MainActivity.class));
                nativeUtil = NativeUtil.getInstance();
                nativeUtil.setCallListener(this);
                break;
        }
    }

    private void showDialog() {
        //标题
        TextView title = new TextView(MainActivity.this);
        title.setText("操作提示");
        title.setTextSize(23);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        //内容
        TextView msg = new TextView(MainActivity.this);
        msg.setText("当前设备没有绑定参会人员！是否前往绑定？");
        msg.setTextSize(18);
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCustomTitle(title);
        builder.setView(msg);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    //DigLog展示 参会人员 列表
                    nativeUtil.queryAttendPeople();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                gotoMeet();
            }
        }).create().show();
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.UPDATE_TIME://平台时间高频回调
                handTo(IDivMessage.UPDATE_TIME, (EventMessage) result, "datatime", mHandler);
                break;
            case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                handTo(IDivMessage.QUERY_DEVMEET_INFO, (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) result, "devMeetInfos", mHandler);
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                handTo(IDivMessage.QUERY_DEVICE_INFO, (InterfaceDevice.pbui_Type_DeviceDetailInfo) result, "devInfos", mHandler);
                break;
            case IDivMessage.QUERY_ATTENDEE://查询参会人员
                handTo(IDivMessage.QUERY_ATTENDEE, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "queryMember_main", mHandler);
                break;
            case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                handTo(IDivMessage.Query_MeetSeat_Inform, (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) result, "queryMeetSeat", mHandler);
                break;
            case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人员
                handTo(IDivMessage.QUERY_ATTEND_BYID, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "queryAttendeeById", mHandler);
                break;
            case IDivMessage.QUERY_CONTEXT://按属性ID查询指定上下文属性
                handTo(IDivMessage.QUERY_CONTEXT, (InterfaceContext.pbui_MeetContextInfo) result, "queryContext", mHandler);
                break;
            case IDivMessage.DEV_PROBYID://按属性ID查询指定设备属性
                handTo(IDivMessage.DEV_PROBYID, (byte[]) result, "queryDevProById", mHandler);
                break;
        }
    }

    public static class IpViewHolder {
        public View rootView;
        public EditText ipPopEdt1;
        public EditText ipPopEdt2;
        public EditText ipPopEdt3;
        public EditText ipPopEdt4;
        public Button ipPopCancel;
        public Button ipPopConfirm;
        public EditText[] gwEdit = {ipPopEdt1, ipPopEdt2, ipPopEdt3, ipPopEdt4};

        public IpViewHolder(View rootView) {
            this.rootView = rootView;
            this.ipPopEdt1 = (EditText) rootView.findViewById(R.id.ip_pop_edt1);
            this.ipPopEdt2 = (EditText) rootView.findViewById(R.id.ip_pop_edt2);
            this.ipPopEdt3 = (EditText) rootView.findViewById(R.id.ip_pop_edt3);
            this.ipPopEdt4 = (EditText) rootView.findViewById(R.id.ip_pop_edt4);

//            MyTextWatcher myTextWatcher = new MyTextWatcher(this.ipPopEdt1);
//            this.ipPopEdt1.addTextChangedListener(myTextWatcher);
//
//            MyTextWatcher myTextWatcher1 = new MyTextWatcher(this.ipPopEdt2);
//            this.ipPopEdt2.addTextChangedListener(myTextWatcher1);
//
//            MyTextWatcher myTextWatcher2 = new MyTextWatcher(this.ipPopEdt3);
//            this.ipPopEdt3.addTextChangedListener(myTextWatcher2);
//
//            MyTextWatcher myTextWatcher3 = new MyTextWatcher(this.ipPopEdt4);
//            this.ipPopEdt4.addTextChangedListener(myTextWatcher3);

            this.ipPopCancel = (Button) rootView.findViewById(R.id.ip_pop_cancel);
            this.ipPopConfirm = (Button) rootView.findViewById(R.id.ip_pop_confirm);
        }

//        class MyTextWatcher implements TextWatcher {
//            public EditText mEditText;
//
//            public MyTextWatcher(EditText mEditText) {
//                super();
//                this.mEditText = mEditText;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // TODO Auto-generated method stub
//                if (s.length() == 3) {
//                    if (this.mEditText == gwEdit[0]) {
//                        gwEdit[1].requestFocus();
//                    } else if (this.mEditText == gwEdit[1]) {
//                        gwEdit[2].requestFocus();
//                    } else if (this.mEditText == gwEdit[2]) {
//                        gwEdit[3].requestFocus();
//                    }
//                } else if (s.length() == 0) {
//                    if (this.mEditText == gwEdit[3]) {
//                        gwEdit[2].requestFocus();
//                    } else if (this.mEditText == gwEdit[2]) {
//                        gwEdit[1].requestFocus();
//                    } else if (this.mEditText == gwEdit[1]) {
//                        gwEdit[0].requestFocus();
//                    }
//                }
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//                // TODO Auto-generated method stub
//
//            }
//        }


    }
}
