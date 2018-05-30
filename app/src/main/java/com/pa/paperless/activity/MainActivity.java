package com.pa.paperless.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaCodecInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
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
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.utils.NetworkUtil;
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
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Mac;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.pa.paperless.utils.MyUtils.handTo;

/**
 * 必须先修改 app/build.gradle中的 jniLibs的路径
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, CallListener, EasyPermissions.PermissionCallbacks {
    private final String TAG = "MainActivity-->";
    public static TextView mCompanyName, mMainMeetName;
    private TextView mMainNowTime, mMainNowDate, mMainNowWeek, mMainMemberJob, mDeviceNameId, mMainMemberName, mMainUnit;
    private Button mMianIntoMeeting, mMainSecretaryManage;

    private NativeUtil nativeUtil;
    /*handle接收*/
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IDivMessage.UPDATE_TIME://更新时间
                    ArrayList datatime = msg.getData().getParcelableArrayList("datatime");
                    EventMessage o2 = (EventMessage) datatime.get(0);
                    long object = (long) o2.getObject();
                    String[] date = DateUtil.getGTMDate(object);
                    upDateMainTimeUI(date);
                    break;
                case IDivMessage.QUERY_CONTEXT://按属性ID查询指定上下文属性
                    ArrayList queryContext = msg.getData().getParcelableArrayList("queryContext");
                    InterfaceContext.pbui_MeetContextInfo o = (InterfaceContext.pbui_MeetContextInfo) queryContext.get(0);
                    int propertyid = o.getPropertyid();
                    DEVID = o.getPropertyval();
                    String bts = MyUtils.getBts(o.getPropertytext());
                    Log.e(TAG, "MainActivity.handleMessage 166行: propertyid--->>> " + propertyid + "  本机设备ID  " + DEVID + "  o.getPropertytext()  " + bts);
                    timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                //查询设备信息
                                nativeUtil.queryDeviceInfo();
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    timer.schedule(timerTask, 0, 2000);
                    break;
                case IDivMessage.QUERY_DEVICE_INFO://查询设备信息
                    disposeDevInfo(msg);
                    break;
                case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                    ArrayList devMeetInfos = msg.getData().getParcelableArrayList("devMeetInfos");
                    if (devMeetInfos != null) {
                        upDateMainUI(devMeetInfos);
                    }
                    break;
            }
        }
    };

    //处理设备信息
    private void disposeDevInfo(Message msg) {
        ArrayList devInfos = msg.getData().getParcelableArrayList("devInfos");
        if (devInfos != null) {
            InterfaceDevice.pbui_Type_DeviceDetailInfo o1 = (InterfaceDevice.pbui_Type_DeviceDetailInfo) devInfos.get(0);
            List<DeviceInfo> deviceInfos = Dispose.DevInfo(o1);
            for (int i = 0; i < deviceInfos.size(); i++) {
                if (deviceInfos.get(i).getDevId() == DEVID) {
                    DeviceInfo deviceInfo = deviceInfos.get(i);
                    String devName = deviceInfo.getDevName();
                    Log.e(TAG, "MainActivity.handleMessage :  会议ID --> " + deviceInfo.getMeetingId() + "  人员ID：" + deviceInfo.getMemberId());
                    if (deviceInfo.getMeetingId() == 0) {//没有查找到会议ID
                        Log.e(TAG, "MainActivity.handleMessage :  未绑定会议 --> ");
                        Toast.makeText(mContext, "未绑定会议", Toast.LENGTH_LONG).show();
                    } else if (deviceInfo.getMemberId() == 0) {
                        Toast.makeText(mContext, "未绑定人员", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "MainActivity.handleMessage :  未绑定人员 --> ");
                    } else {
                        mDeviceNameId.setText(devName + " | " + DEVID);
                        Log.e(TAG, "MainActivity.handleMessage 115行:  本机设备名称与设备ID --->>> " + devName + " | " + DEVID);
                        try {
                            timer.cancel();//到了这一步关闭定时器
                            //110.查询设备会议信息
                            nativeUtil.queryDeviceMeetInfo();
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private Timer timer;//定时器

    private void setPBGone() {
        if (pb.getVisibility() == View.VISIBLE) {
            pb.setVisibility(View.GONE);
        }
    }

    private ProgressBar pb;
    private long millis;
    private PopupWindow mPopupWindow;
    public static InterfaceDevice.pbui_Type_DeviceFaceShowDetail mLocalDevMeetInfo;//设备会议信息
    private Intent serviceIntent;
    public MainActivity mContext;
    private PopupWindow ipEdtPop;
    private int DEVID;//本机设备ID
    public static String uniqueId;


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
        Log.e(TAG, "MainActivity.upDateMainUI:   --->>> 本机设备ID: " + deviceid + "  本机人员ID：" + memberid);
        mCompanyName.setText(company);
        mMainMeetName.setText(meetingName);
        mMainMemberJob.setText(job);
        mMainMemberName.setText(memberName);
        setPBGone();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "MainActivity.onCreate 318行:   --->>> " + getApplication().getPackageName());
        mContext = this;
        //  动态申请权限 如果设备是6.0或以上 则动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initPermissions();
        }
        //获取手机/平板的唯一标识
        uniqueId = MyUtils.getUniqueId(mContext);
        Log.e(TAG, "MainActivity.onCreate 322行:   手机的唯一标识符--->>> " + uniqueId);
        initConfFile();
        initController();
        initView();
        String[] arr = {};
        SDLActivity.nativeInit(arr);
        EventBus.getDefault().register(this);
        if (NetworkUtil.isNetworkAvailable(mContext)) {
            //  初始化无纸化网络平台
            nativeUtil.javaInitSys();
            serviceIntent = new Intent(this, PlayService.class);
            startService(serviceIntent);
        } else {
            setPBGone();
            Toast.makeText(mContext, "请检查网络连接", Toast.LENGTH_SHORT).show();
        }
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
            case IDEventMessage.not_bound:
                Log.e(TAG, "MainActivity.getEventMessage :  未绑定 --> ");
                if (pb.getVisibility() == View.VISIBLE) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(mContext, "当前设备无会议", Toast.LENGTH_SHORT).show();
                }
                break;
            case IDEventMessage.DEVMEETINFO_CHANGE_INFORM:
                Log.e(TAG, "MainActivity.getEventMessage:  109.设备会议信息变更通知 EventBus --->>> ");
                //110.查询设备会议信息
                boolean haveDevMeetInfo = nativeUtil.queryDeviceMeetInfo();
                if (!haveDevMeetInfo) {
                    Log.e(TAG, "MainActivity.getEventMessage :  重新查找设备会议信息失败 --> ");
                    Toast.makeText(mContext, "你没有参加的会议", Toast.LENGTH_SHORT).show();
                    setPBGone();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(serviceIntent);

        Log.e(TAG, "MainActivity.onDestroy : 销毁  --->>> ");
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
        pb = (ProgressBar) findViewById(R.id.pb);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mian_into_meeting://进入会议
                if (pb.getVisibility() == View.GONE) {//如果加载框隐藏
//                    showDialog();
                    gotoMeet();
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
            Log.e(TAG, "MainActivity.onClick 573行:   --->>> " + nowIp);
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
//                        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//                        manager.restartPackage("com.pa.paperless");
                        /** **** **  app重启  ** **** **/
                        reStartApp();
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

    /**
     * 重启应用
     */
    private void reStartApp() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, restartIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //跳转到会议界面
    private void gotoMeet() {
        if (mLocalDevMeetInfo != null) {
            /** **** **  发送签到  ** **** **/
            nativeUtil.sendSign("", "", InterfaceMacro.Pb_MeetSignType.Pb_signin_direct.getNumber());
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
                Log.e(TAG, "MainActivity.onActivityResult:  进入了onActivityResult方法 --->>> ");
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
//        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        //内容
        TextView msg = new TextView(MainActivity.this);
        msg.setText("当前设备无会议!");
        msg.setTextColor(Color.RED);
        msg.setTextSize(20);
//        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCustomTitle(title);
        builder.setView(msg);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.UPDATE_TIME://平台时间高频回调
                handTo(IDivMessage.UPDATE_TIME, (EventMessage) result, "datatime", mHandler);
                break;
            case IDivMessage.PLATFORM_INITIALIZATION://平台初始化完毕
                Log.e(TAG, "MainActivity.callListener 696行:  平台初始化完毕 --->>> ");
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
                int number = InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_SELFID.getNumber();
                try {
                    //31.按属性ID查询指定上下文属性
                    nativeUtil.queryContextProperty(number);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                break;
            case IDivMessage.QUERY_CONTEXT://按属性ID查询指定上下文属性
                handTo(IDivMessage.QUERY_CONTEXT, (InterfaceContext.pbui_MeetContextInfo) result, "queryContext", mHandler);
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                handTo(IDivMessage.QUERY_DEVICE_INFO, (InterfaceDevice.pbui_Type_DeviceDetailInfo) result, "devInfos", mHandler);
                break;
            case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                handTo(IDivMessage.QUERY_DEVMEET_INFO, (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) result, "devMeetInfos", mHandler);
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
            this.ipPopCancel = (Button) rootView.findViewById(R.id.ip_pop_cancel);
            this.ipPopConfirm = (Button) rootView.findViewById(R.id.ip_pop_confirm);
        }

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - millis > 2000) {
            Toast.makeText(mContext, "再次点击退出应用", Toast.LENGTH_SHORT).show();
            millis = System.currentTimeMillis();
        } else {
            File file = new File(Macro.MEETFILE);
            boolean b = FileUtil.deleteAllFile(file);
            Log.e(TAG, "MainActivity.onBackPressed :   --> "+b);
            finish();
            System.exit(0);
        }
    }
}
