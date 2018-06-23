package com.pa.paperless.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaCodecInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.pa.paperless.R;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.service.NativeService;
import com.pa.paperless.service.ShotApplication;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.pa.paperless.service.NativeService.localDevId;
import static com.pa.paperless.service.NativeService.nativeUtil;


public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private final String TAG = "MainActivity-->";
    public static TextView mCompanyName, mMainMeetName;
    private TextView mMainNowTime, mMainNowDate, mMainNowWeek, mMainMemberJob, mDeviceNameId, mMainMemberName, mMainUnit;
    private Button mMianIntoMeeting, mMainSecretaryManage;
    private Timer timer;//定时器
    private long millis;
    public static InterfaceDevice.pbui_Type_DeviceFaceShowDetail mLocalDevMeetInfo;//设备会议信息
    public MainActivity mContext;
    private PopupWindow ipEdtPop;
    private int DEVID;//本机设备ID
    public static String uniqueId;
    private Intent nativeIntent;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.init_native://初始化NativeUtil完成
                Log.e(TAG, "MainActivity.getEventMessage :  初始化NativeUtil完成... --> ");
                if (NetworkUtil.isNetworkAvailable(mContext)) {
                    String[] arr = {};
                    SDLActivity.nativeInit(arr);
                    //  初始化无纸化网络平台
                    nativeUtil.javaInitSys();
                } else {
                    showTip("请检查网络连接");
                }
                break;
            case IDEventMessage.MEET_DATE://获得后台回调时间
                String[] date = (String[]) message.getObject();
                upDateMainTimeUI(date);
                break;
            case IDEventMessage.PLATFORM_INITIALIZATION://平台初始化完毕
                MainStart();
                break;
            case IDEventF.context_proper://获取上下文信息
                receiveContextInfo(message);
                break;
            case IDEventF.dev_info://获取设备信息
                receiveDevInfo(message);
                break;
            case IDEventF.dev_meet_info://获取设备会议信息
                receiveDevMeetInfo(message);
                break;
            case IDEventMessage.DEVMEETINFO_CHANGE_INFORM:
                Log.e(TAG, "MainActivity.getEventMessage:  109.设备会议信息变更通知 EventBus --->>> ");
                //110.查询设备会议信息
                boolean haveDevMeetInfo = nativeUtil.queryDeviceMeetInfo();
                if (!haveDevMeetInfo) {
                    Log.e(TAG, "MainActivity.getEventMessage :  重新查找设备会议信息失败 --> ");
                    showTip("你没有参加的会议");
                }
                break;
            case IDEventF.ICC_changed_inform://界面配置变更通知
                //31.按属性ID查询指定上下文属性
                nativeUtil.queryContextProperty(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_SELFID.getNumber());
                break;
            case IDEventMessage.MeetSeat_Change_Inform://会议排位变更通知
                //31.按属性ID查询指定上下文属性
                nativeUtil.queryContextProperty(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_SELFID.getNumber());
                break;
        }
    }

    private void receiveDevMeetInfo(EventMessage message) {
        mLocalDevMeetInfo = (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) message.getObject();
        String company = MyUtils.getBts(mLocalDevMeetInfo.getCompany());
        String job = MyUtils.getBts(mLocalDevMeetInfo.getJob());
        String memberName = MyUtils.getBts(mLocalDevMeetInfo.getMembername());
        String meetingName = MyUtils.getBts(mLocalDevMeetInfo.getMeetingname());
        int deviceid = mLocalDevMeetInfo.getDeviceid();
        localDevId = deviceid;//设置全局设备ID
        int memberid = mLocalDevMeetInfo.getMemberid();
        Log.e(TAG, "MainActivity.receiveDevMeetInfo :  本机设备ID: " + deviceid + "  本机人员ID：" + memberid);
        mCompanyName.setText(company);
        mMainMeetName.setText(meetingName);
        mMainMemberJob.setText(job);
        mMainMemberName.setText(memberName);
    }

    private void MainStart() {
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
        try {
            nativeUtil.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEINFO.getNumber());//缓存设备信息
            nativeUtil.cacheData(InterfaceMacro.Pb_Type.Pb_TYPE_MEET_INTERFACE_DEVICEFACESHOW.getNumber());//缓存设备会议信息
            //31.按属性ID查询指定上下文属性
            nativeUtil.queryContextProperty(InterfaceMacro.Pb_ContextPropertyID.Pb_MEETCONTEXT_PROPERTY_SELFID.getNumber());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void receiveContextInfo(EventMessage message) {
        InterfaceContext.pbui_MeetContextInfo o = (InterfaceContext.pbui_MeetContextInfo) message.getObject();
        DEVID = o.getPropertyval();
        mDeviceNameId.setText(" 设备ID | " + DEVID);
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
        if (timer != null) {
            timer.schedule(timerTask, 0, 2000);
        }
    }

    private void receiveDevInfo(EventMessage message) {
        InterfaceDevice.pbui_Type_DeviceDetailInfo devInfos = (InterfaceDevice.pbui_Type_DeviceDetailInfo) message.getObject();
        List<DeviceInfo> deviceInfos = Dispose.DevInfo(devInfos);
        for (int i = 0; i < deviceInfos.size(); i++) {
            if (deviceInfos.get(i).getDevId() == DEVID) {
                DeviceInfo deviceInfo = deviceInfos.get(i);
                String devName = deviceInfo.getDevName();
                Log.e(TAG, "MainActivity.receiveDevInfo :  会议ID --> " + deviceInfo.getMeetingId() + "  人员ID：" + deviceInfo.getMemberId());
                if (deviceInfo.getMeetingId() == 0) {//没有查找到会议ID
                    Log.e(TAG, "MainActivity.receiveDevInfo :  未绑定会议 --> ");
                    showTip("未绑定会议");
                } else if (deviceInfo.getMemberId() == 0) {
                    showTip("未绑定人员");
                    Log.e(TAG, "MainActivity.receiveDevInfo :  未绑定人员 --> ");
                } else {
                    mDeviceNameId.setText(devName + " | " + DEVID);
                    Log.e(TAG, "MainActivity.receiveDevInfo :  本机设备名称与设备ID --->>> " + devName + " | " + DEVID);
                    try {
                        if (timer != null) {
                            timer.cancel();//停止定时器
                            timer = null;
                        }
                        //110.查询设备会议信息
                        nativeUtil.queryDeviceMeetInfo();
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 获取本机的设备会议信息
     *
     * @return
     */
    public static InterfaceDevice.pbui_Type_DeviceFaceShowDetail getLocalInfo() {
        return mLocalDevMeetInfo != null ? mLocalDevMeetInfo : null;
    }

    private void upDateMainTimeUI(String[] strings) {
        if (strings != null) {
            mMainNowDate.setText(strings[0]);
            mMainNowWeek.setText(strings[1]);
            mMainNowTime.setText(strings[2]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("A_life", "MainActivity.onCreate :   --->>> ");
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
        initView();
        boolean naService = MyUtils.isServiceRunning(getApplicationContext(), "com.pa.paperless.service.NativeService");
        Log.i(TAG, "MainActivity.onCreate :   naService --->>> " + naService);
        if (!naService) {
            nativeIntent = new Intent(MainActivity.this, NativeService.class);
            startService(nativeIntent);
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

    @Override
    protected void onDestroy() {
        boolean naService = MyUtils.isServiceRunning(getApplicationContext(), "com.pa.paperless.service.NativeService");
        Log.i("A_life", "MainActivity.onDestroy :   --->>> " + naService);
        if (naService && nativeIntent != null) {
            stopService(nativeIntent);
        }
        super.onDestroy();
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
                gotoMeet();
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
            /** **** **  清空文件  ** **** **/
            File file = new File(Macro.MEETFILE);
            FileUtil.deleteAllFile(file);

            /** **** **  清空临时存放的图片  ** **** **/
            File f = new File(Macro.TAKE_PHOTO);
            FileUtil.deleteAllFile(f);

            boolean naService = MyUtils.isServiceRunning(getApplicationContext(), "com.pa.paperless.service.NativeService");
            Log.i("Service_Log", "MainActivity.onBackPressed :  NativeService --->>> " + naService);
            if (naService && nativeIntent != null) {
                stopService(nativeIntent);
            }
            System.exit(0);
        }
    }


    @Override
    protected void onStart() {
        Log.e("A_life", "MainActivity.onStart :   --->>> ");
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (timer == null) {
            timer = new Timer();
        }
        Log.e("A_life", "MainActivity.onResume :   --->>> " + getTaskId());
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e("A_life", "MainActivity.onPause :   --->>> ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e("A_life", "MainActivity.onStop :   --->>> ");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.e("A_life", "MainActivity.onRestart :   --->>> ");
        super.onRestart();
    }
}
