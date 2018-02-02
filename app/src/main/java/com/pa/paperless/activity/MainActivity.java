package com.pa.paperless.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.adapter.MainMemberAdapter;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.utils.PermissionUtils;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener, CallListener {

    public static TextView mCompanyName;
    private TextView mMainNowTime;
    private TextView mMainNowDate;
    private TextView mMainNowWeek;
    private Button mMianIntoMeeting;
    private Button mMainSecretaryManage;
    public static TextView mMainMeetName;
    private TextView mMainUnit;
    private TextView mMainMemberName;
    private TextView mMainMemberJob;
//    NativeUtil nativeUtil;
    /*handle接收*/
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IDivMessage.UPDATE_TIME:
                    String[] datatimes = msg.getData().getStringArray("datatime");
                    upDateMainTimeUI(datatimes);
                    break;
                case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                    ArrayList devMeetInfos = msg.getData().getParcelableArrayList("devMeetInfos");
                    if (devMeetInfos != null) {
                        upDateMainUI(devMeetInfos);
                    }
                    break;
                case IDivMessage.QUERY_DEVICE_INFO:
                    ArrayList devInfos = msg.getData().getParcelableArrayList("devInfos");
                    if (devInfos != null) {
                        InterfaceMain.pbui_Type_DeviceDetailInfo o = (InterfaceMain.pbui_Type_DeviceDetailInfo) devInfos.get(0);
//                        Dispose.DevInfo(o);
                    }
                    break;
                case IDivMessage.QUERY_ATTENDEE:
                    ArrayList queryMember_main = msg.getData().getParcelableArrayList("queryMember_main");
                    InterfaceMain.pbui_Type_MemberDetailInfo o = (InterfaceMain.pbui_Type_MemberDetailInfo) queryMember_main.get(0);
                    List<MemberInfo> memberInfos = Dispose.MemberInfo(o);
                    //将参会人信息 diaLog展示
//                    showCheckMemberDialog(memberInfos);
                    break;
                case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                    ArrayList queryMeetSeat = msg.getData().getParcelableArrayList("queryMeetSeat");
                    InterfaceMain2.pbui_Type_MeetSeatDetailInfo o5 = (InterfaceMain2.pbui_Type_MeetSeatDetailInfo) queryMeetSeat.get(0);
                    List<InterfaceMain2.pbui_Item_MeetSeatDetailInfo> itemList3 = o5.getItemList();
                    for (int i = 0; i < itemList3.size(); i++) {
                        InterfaceMain2.pbui_Item_MeetSeatDetailInfo pbui_item_meetSeatDetailInfo = itemList3.get(i);
                        int nameId = pbui_item_meetSeatDetailInfo.getNameId();
                        int role = pbui_item_meetSeatDetailInfo.getRole();
                        int seatid = pbui_item_meetSeatDetailInfo.getSeatid();
                        if (role == 3) {
                            //如果当前人员是主持人
                            CompereID = nameId;
                            try {
                                /** ************ ******  91.查询指定ID的参会人员  ****** ************ **/
                                nativeUtil.queryAttendPeopleFromId(CompereID);
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                            }
                            Log.e("MyLog", "MainActivity.handleMessage:  这个ID就是主持人 --->>> " + nameId);
                        }
                        Log.e("MyLog", "MeetingActivity.handleMessage:  人员ID： --->>> " + nameId + "  角色ID：" + role + "  设备ID：" + seatid);
                    }
                    break;
                case IDivMessage.QUERY_ATTEND_BYID://91.查询指定ID的参会人员
                    ArrayList queryAttendeeById = msg.getData().getParcelableArrayList("queryAttendeeById");
                    InterfaceMain.pbui_Type_MemberDetailInfo o1 = (InterfaceMain.pbui_Type_MemberDetailInfo) queryAttendeeById.get(0);
                    List<InterfaceMain.pbui_Item_MemberDetailInfo> itemList = o1.getItemList();
                    compereName = MyUtils.getBts(itemList.get(0).getName());
                    Log.e("MyLog", "MainActivity.handleMessage:  主持人姓名： --->>> " + compereName);
                    break;
            }
        }
    };
    private PopupWindow mPopupWindow;
    private InterfaceMain.pbui_Type_DeviceFaceShowDetail nowDivMeetInfo;
    private String compereName;
    private int CompereID;

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
        nowDivMeetInfo = (InterfaceMain.pbui_Type_DeviceFaceShowDetail) arrayList.get(0);
        String company = MyUtils.getBts(nowDivMeetInfo.getCompany());
        String job = MyUtils.getBts(nowDivMeetInfo.getJob());
        String memberName = MyUtils.getBts(nowDivMeetInfo.getMembername());
        String meetingName = MyUtils.getBts(nowDivMeetInfo.getMeetingname());
        int deviceid = nowDivMeetInfo.getDeviceid();
        int memberid = nowDivMeetInfo.getMemberid();
        Log.e("MyLog", "MainActivity.upDateMainUI:   --->>> deviceid:" + deviceid + "  memberid" + memberid);
        mCompanyName.setText(company);
        mMainMeetName.setText(meetingName);
        mMainMemberJob.setText(job);
        mMainMemberName.setText(memberName);
        try {
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
        Log.e("MyLog","MainActivity.onCreate:   --->>> ");
        //  动态申请权限
        initPermissions();
        initController();
        initView();
        //  初始化无纸化网络平台
        nativeUtil.javaInitSys();
        //  8.修改本机界面状态
        nativeUtil.setInterfaceState(InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace.getNumber());
//        try {
//            //  6.查询设备信息
////            nativeUtil.queryDeviceInfo();
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //  110.查询设备会议信息
                    nativeUtil.queryDeviceMeetInfo();
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        }, 5000);
        EventBus.getDefault().register(this);
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
                /** ************ ******  110.查询设备会议信息  ****** ************ **/
//                nativeUtil.queryDeviceMeetInfo();
                break;
            case IDEventMessage.DEV_REGISTER_INFORM:
                Log.e("MyLog", "MainActivity.getEventMessage:  设备寄存器变更 EventBus --->>> ");
                /** ************ ******  6.查询设备信息  ****** ************ **/
//                nativeUtil.queryDeviceInfo();
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM:
                Log.e("MyLog", "MainActivity.getEventMessage:  参会人员变更通知 EventBus --->>> ");
                /** ************ ******  92.查询参会人员  ****** ************ **/
//                nativeUtil.queryAttendPeople();
                InterfaceMain.pbui_MeetNotifyMsg object1 = (InterfaceMain.pbui_MeetNotifyMsg) message.getObject();
                Log.e("MyLog", "MainActivity.getEventMessage:  查询指定ID的参会人员  object1.getId() --->>> " + object1.getId()
                        + "  object1.getOpermethod()" + object1.getOpermethod());
                /** ************ ******  91.查询指定ID的参会人员  ****** ************ **/
                nativeUtil.queryAttendPeopleFromId(CompereID);

                break;
            case IDEventMessage.DEVMEETINFO_CHANGE_INFORM:
                Log.e("MyLog", "MainActivity.getEventMessage:  109.设备会议信息变更通知 EventBus --->>> ");
                /** ************ ******  110.查询设备会议信息  ****** ************ **/
                nativeUtil.queryDeviceMeetInfo();
                break;
            case IDEventMessage.SIGN_EVENT:
                Log.e("MyLog", "MainActivity.getEventMessage:  17 辅助签到变更通知 EventBus --->>> ");
                /** ************ ******  18.辅助签到操作  ****** ************ **/
//                nativeUtil.signAlterationOperate(nowDivMeetInfo.getDeviceid());
                break;
            case IDEventMessage.MeetSeat_Change_Inform:
                Log.e("MyLog", "MainActivity.getEventMessage:  180.会议排位变更通知 EventBus --->>> ");
                InterfaceMain.pbui_MeetNotifyMsg object = (InterfaceMain.pbui_MeetNotifyMsg) message.getObject();
                int id = object.getId();
                int opermethod = object.getOpermethod();
                Log.e("MyLog", "MainActivity.getEventMessage:  会议排位返回的数据： --->>> id " + id + "  opermethod ：" + opermethod);
                /** ************ ******  181.查询会议排位  ****** ************ **/
                nativeUtil.queryMeetRanking();
                /** ************ ******  7.按属性ID查询指定设备属性  ****** ************ **/
//                nativeUtil.queryDevicePropertiesById(id);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
//        nativeUtil = new NativeUtil();
        nativeUtil.setCallListener(this);
    }

    /**
     * 动态权限申请
     */
    private void initPermissions() {
        String[] permissions = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };
        PermissionUtils.checkAndRequestMorePermissions(this, permissions, IDivMessage.PERMISSION_REQUEST_CODE, new PermissionUtils.PermissionRequestSuccessCallBack() {
            @Override
            public void onHasPermission() {
                Log.e("MyLog", "MainActivity.onHasPermission:  已授予权限 --->>> ");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IDivMessage.PERMISSION_REQUEST_CODE) {
            Log.e("MyLog", "MainActivity.onRequestPermissionsResult:  true --->>> ");
        } else {
            Log.e("MyLog", "MainActivity.onRequestPermissionsResult:  false --->>> ");
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mian_into_meeting://进入会议
//                nativeUtil.sendSign();
                if (true) {
                    /*b_devMeetInfo*/
                    /** ************ ******  18.辅助签到操作  ****** ************ **/
//                    nativeUtil.signAlterationOperate(nowDivMeetInfo.getDeviceid());
                    Intent intent = new Intent(MainActivity.this, MeetingActivity.class);
                    Bundle bundle = new Bundle();
                    ArrayList arraylist = new ArrayList();
                    //将设备会议信息的值传递给MeetingActivity
                    arraylist.add(nowDivMeetInfo.getDeviceid());
                    arraylist.add(nowDivMeetInfo.getMeetingid());
                    arraylist.add(nowDivMeetInfo.getMemberid());
                    arraylist.add(nowDivMeetInfo.getRoomid());
                    arraylist.add(nowDivMeetInfo.getSigninType());
                    arraylist.add(MyUtils.getBts(nowDivMeetInfo.getMeetingname()));
                    arraylist.add(MyUtils.getBts(nowDivMeetInfo.getMembername()));
                    arraylist.add(MyUtils.getBts(nowDivMeetInfo.getCompany()));
                    arraylist.add(MyUtils.getBts(nowDivMeetInfo.getJob()));
                    arraylist.add(compereName);
                    Log.e("MyLog", "MainActivity.onClick:  传递给MeetingActivity --->>> devid：" + nowDivMeetInfo.getDeviceid() + " memberid:" + nowDivMeetInfo.getMemberid());
                    bundle.putParcelableArrayList("devMeetInfo", arraylist);
                    intent.putExtra("putId", bundle);
                    startActivityForResult(intent, IDivMessage.MAIN_REQUEST_CODE);
                } else {
                    //如果没有查找到
                    showDialog();
                }
                break;
            case R.id.main_secretary_manage://秘书管理
                startActivityForResult(new Intent(MainActivity.this, ManageActivity.class), IDivMessage.MAIN_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IDivMessage.MAIN_REQUEST_CODE:
                //进入其它页面后 nativeUtil对象就改变了，当返回主界面后就要重新设置
                Log.e("MyLog", "MainActivity.onActivityResult:  进入了onActivityResult方法 --->>> ");
                startActivity(new Intent(this, MainActivity.class));
//                nativeUtil = NativeUtil.getInstance();
//                nativeUtil.setCallListener(this);
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
            }
        }).create().show();
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.UPDATE_TIME://平台时间高频回调
                String[] str1 = (String[]) result;
                if (str1 != null) {
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("datatime", str1);
                    Message message = new Message();
                    message.what = IDivMessage.UPDATE_TIME;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                InterfaceMain.pbui_Type_DeviceFaceShowDetail devMeetInfos = (InterfaceMain.pbui_Type_DeviceFaceShowDetail) result;
                if (devMeetInfos != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(devMeetInfos);
                    bundle.putParcelableArrayList("devMeetInfos", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                InterfaceMain.pbui_Type_DeviceDetailInfo devInfos = (InterfaceMain.pbui_Type_DeviceDetailInfo) result;
                if (devInfos != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(devInfos);
                    bundle.putParcelableArrayList("devInfos", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_ATTENDEE:
                InterfaceMain.pbui_Type_MemberDetailInfo result1 = (InterfaceMain.pbui_Type_MemberDetailInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryMember_main", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;/** ************ ******  在会议Activity中接收的  ****** ************ **/
            case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                InterfaceMain2.pbui_Type_MeetSeatDetailInfo result5 = (InterfaceMain2.pbui_Type_MeetSeatDetailInfo) result;
                if (result5 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result5);
                    bundle.putParcelableArrayList("queryMeetSeat", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_ATTEND_BYID:
                InterfaceMain.pbui_Type_MemberDetailInfo result2 = (InterfaceMain.pbui_Type_MemberDetailInfo) result;
                if (result2 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result2);
                    bundle.putParcelableArrayList("queryAttendeeById", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }

}
