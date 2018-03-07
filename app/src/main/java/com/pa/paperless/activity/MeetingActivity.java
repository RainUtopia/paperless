package com.pa.paperless.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.adapter.OnLineProjectorAdapter;
import com.pa.paperless.adapter.ScreenControlAdapter;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.MainDivMeetInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.AnnAgendaFragment;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.fragment.meeting.ChatFragment;
import com.pa.paperless.fragment.meeting.MeetingFileFragment;
import com.pa.paperless.fragment.meeting.NotationFragment;
import com.pa.paperless.fragment.meeting.SharedFileFragment;
import com.pa.paperless.fragment.meeting.SigninFragment;
import com.pa.paperless.fragment.meeting.VideoFragment;
import com.pa.paperless.fragment.meeting.VoteFragment;
import com.pa.paperless.fragment.meeting.WebBrowseFragment;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.utils.ScreenUtils;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.pa.paperless.utils.MyUtils.setAnimator;


public class MeetingActivity extends BaseActivity implements View.OnClickListener, CallListener {


    private TextView mMeetingCompanyName;
    private TextView mMeetingTheme;
    private ImageButton mMeetingMessage;
    private ImageButton mMeetingChatOnline;
    private TextView mMeetingNowTime;
    private TextView mMeetingNowDate;
    private TextView mMeetingNowWeek;
    private PercentLinearLayout mTheNowDate;
    public static TextView mAttendee;
    public static TextView mCompere;
    private ImageView mSignin;
    private ImageView mAgenda;
    private ImageView mMeetingfile;
    private ImageView mSharedfile;
    private ImageView mWhiteboard;
    private ImageView mChat;
    private ImageView mVideo;
    private ImageView mVote;
    private ImageView mPostil;
    private ImageView mWebbrowse;
    private PercentLinearLayout mLeftLayout;
    private FrameLayout mRightLayout;
    private FloatingActionButton mFab;
    private ArrayList<BaseFragment> mFragments;
    private List<ImageView> mImages;
    private PopupWindow mPopupWindow;//fab popup
    private PopupWindow mScreenPopupWindow;//同屏控制 popup
    private PopupWindow mPlayerPopupWindow;// 同屏控制 中的选择参会人
    private FragmentManager mFm;

    private SigninFragment mSigninFragment;
    private AnnAgendaFragment mAnnAgendaFragment;
    private MeetingFileFragment mMeetingFileFragment;
    private SharedFileFragment mSharedFileFragment;
    private ChatFragment mChatFragment;
    private VideoFragment mVideoFragment;
    private VoteFragment mVoteFragment;
    private NotationFragment mNotationFragment;
    private WebBrowseFragment mWebbrowseFragment;
    public static NativeUtil nativeUtil;
    public static List<ReceiveMeetIMInfo> mReceiveMsg = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_BYID://129.查询指定ID的会议
                    ArrayList queryMeetById = msg.getData().getParcelableArrayList("queryMeetById");
                    InterfaceMain.pbui_Type_MeetMeetInfo o6 = (InterfaceMain.pbui_Type_MeetMeetInfo) queryMeetById.get(0);
                    List<InterfaceMain.pbui_Item_MeetMeetInfo> itemList = o6.getItemList();
                    for (int i = 0; i < itemList.size(); i++) {
                        InterfaceMain.pbui_Item_MeetMeetInfo pbui_item_meetMeetInfo = itemList.get(i);
                        String name = MyUtils.getBts(pbui_item_meetMeetInfo.getName());
                        Log.e("MyLog", "MeetingActivity.handleMessage:  指定会议的名字： --->>> " + name + "  itemList：" + itemList.size());
                    }
                    break;
                case IDivMessage.QUERY_COMMON_BYID://91.查询指定ID的参会人员
                    ArrayList queryCommonById = msg.getData().getParcelableArrayList("queryAttendById");
                    InterfaceMain.pbui_Type_MemberDetailInfo o1 = (InterfaceMain.pbui_Type_MemberDetailInfo) queryCommonById.get(0);
                    List<InterfaceMain.pbui_Item_MemberDetailInfo> itemList1 = o1.getItemList();
                    for (int i = 0; i < itemList1.size(); i++) {
                        InterfaceMain.pbui_Item_MemberDetailInfo pbui_item_memberDetailInfo = itemList1.get(i);
                        String name = MyUtils.getBts(pbui_item_memberDetailInfo.getName());
                        Log.e("MyLog", "MeetingActivity.handleMessage:  指定ID的参会人员名称： --->>> " + name);
                    }
                    break;
                case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                    ArrayList queryMember = msg.getData().getParcelableArrayList("queryMember");
                    InterfaceMain.pbui_Type_MemberDetailInfo o2 = (InterfaceMain.pbui_Type_MemberDetailInfo) queryMember.get(0);
                    memberInfos = Dispose.MemberInfo(o2);
                    break;
                case IDivMessage.QUERY_ATTEND_BYID://91.查询指定ID的参会人员
                    ArrayList queryAttendById = msg.getData().getParcelableArrayList("queryAttendById");
                    InterfaceMain.pbui_Type_MemberDetailInfo o3 = (InterfaceMain.pbui_Type_MemberDetailInfo) queryAttendById.get(0);
                    List<InterfaceMain.pbui_Item_MemberDetailInfo> itemList2 = o3.getItemList();
                    for (int i = 0; i < itemList2.size(); i++) {
                        InterfaceMain.pbui_Item_MemberDetailInfo pbui_item_memberDetailInfo = itemList2.get(i);
                        String memberName = MyUtils.getBts(pbui_item_memberDetailInfo.getName());
                        int personid = pbui_item_memberDetailInfo.getPersonid();
                        Log.e("MyLog", "MeetingActivity.handleMessage:  参会人： --->>> " + memberName);
                    }
                    break;
                case IDivMessage.QUERY_Attendee_Property://96.查询参会人员属性(判断是否为主持人)
                    ArrayList queryAttendeeProperty = msg.getData().getParcelableArrayList("queryAttendeeProperty");
                    InterfaceMain.pbui_Type_MeetMembeProperty o4 = (InterfaceMain.pbui_Type_MeetMembeProperty) queryAttendeeProperty.get(0);
                    String bts = MyUtils.getBts(o4.getPropertytext());
                    int propertyval = o4.getPropertyval();
                    Log.e("MyLog", "MeetingActivity.handleMessage:  propertyval： --->>> " + propertyval + "  身份：" + bts);
                    break;
                case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息(查找在线状态的投影机)
                    Log.e("MyLog", "MeetingActivity.handleMessage 184行:  查询设备信息 --->>> ");
                    ArrayList devInfos = msg.getData().getParcelableArrayList("devInfos");
                    if (devInfos != null) {
                        InterfaceMain.pbui_Type_DeviceDetailInfo o5 = (InterfaceMain.pbui_Type_DeviceDetailInfo) devInfos.get(0);
                        List<DeviceInfo> deviceInfos = Dispose.DevInfo(o5);
                        int number = Macro.DEVICE_MEET_PROJECTIVE;
                        onLineMembers = new ArrayList<>();
                        onLineProjectors = new ArrayList<>();
                        allProjectors = new ArrayList<>();
                        for (int i = 0; i < deviceInfos.size(); i++) {
                            DeviceInfo deviceInfo = deviceInfos.get(i);
                            int netState = deviceInfo.getNetState();
                            int devId = deviceInfo.getDevId();
                            int memberId = deviceInfo.getMemberId();
                            int i1 = devId & number;
                            //判断是否是投影机
                            if (i1 == number) {
                                allProjectors.add(deviceInfo);
                                //判断是否是在线状态
                                if (netState == 1) {
                                    //说明是在线状态的投影机
                                    onLineProjectors.add(deviceInfo);
                                }
                            }
                            if (netState == 1) {
                                for (int j = 0; j < memberInfos.size(); j++) {
                                    MemberInfo memberInfo = memberInfos.get(j);
                                    if (memberInfos.get(j).getPersonid() == memberId) {
                                        //查找到在线状态的参会人员
                                        onLineMembers.add(memberInfos.get(j));
                                    }
                                }
                            }
                        }
                        //初始化投影机是否选中集合
                        allPerchecks = new ArrayList<>();
                        //初始化 全部设为false
                        for (int k = 0; k < allProjectors.size(); k++) {
                            allPerchecks.add(false);
                        }
                        //初始化同屏控制是否选中集合
                        checks = new ArrayList<>();
                        //初始化 全部设为false
                        for (int k = 0; k < onLineMembers.size(); k++) {
                            checks.add(false);
                        }
                        allProjectorAdapter = new OnLineProjectorAdapter(allProjectors);
                        onLineProjectorAdapter = new OnLineProjectorAdapter(onLineProjectors);
                        onLineMemberAdapter = new ScreenControlAdapter(onLineMembers);
                    }
                    break;
                case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                    ArrayList queryMeetRanking = msg.getData().getParcelableArrayList("queryMeetRanking");
                    InterfaceMain2.pbui_Type_MeetSeatDetailInfo o5 = (InterfaceMain2.pbui_Type_MeetSeatDetailInfo) queryMeetRanking.get(0);
                    List<InterfaceMain2.pbui_Item_MeetSeatDetailInfo> itemList3 = o5.getItemList();
                    for (int i = 0; i < itemList3.size(); i++) {
                        InterfaceMain2.pbui_Item_MeetSeatDetailInfo pbui_item_meetSeatDetailInfo = itemList3.get(i);
                        int nameId = pbui_item_meetSeatDetailInfo.getNameId();
                        int role = pbui_item_meetSeatDetailInfo.getRole();
                        int seatid = pbui_item_meetSeatDetailInfo.getSeatid();
                        if (role == 3 && MeetingActivity.o.getMemberid() == nameId) {
                            //身份为主持人
                            VoteFragment.isCompere = true;
                        }
                    }
                    break;
            }
        }
    };
    // 存放当前设备会议信息
    public static MainDivMeetInfo o;

    public static List<Boolean> checks;
    public static ArrayList<Boolean> allPerchecks;
    private ScreenControlAdapter onLineMemberAdapter;
    private LinearLayout mMeetActivityLayout;
    private PopupWindow mProjectorPop;
    private PopupWindow mProRlPop;
    private PopupWindow mFunctionMsgPop;
    //用来存放在线状态的投影机设备
    private List<DeviceInfo> onLineProjectors;
    private List<MemberInfo> memberInfos;
    //用来存放在线状态的参会人员
    private List<MemberInfo> onLineMembers;
    private OnLineProjectorAdapter onLineProjectorAdapter;
    //用来存放所有的投影机设备
    private List<DeviceInfo> allProjectors;
    private OnLineProjectorAdapter allProjectorAdapter;
    public static Badge mBadge; //未读消息提示
    public static String mNoteCentent;
    private PopupWindow mNotePop;

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_MEET_BYID://129.查询指定ID的会议
                InterfaceMain.pbui_Type_MeetMeetInfo result1 = (InterfaceMain.pbui_Type_MeetMeetInfo) result;
                if (result1 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result1);
                    bundle.putParcelableArrayList("queryMeetById", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_ATTEND_BYID://91.查询指定ID的参会人员
                InterfaceMain.pbui_Type_MemberDetailInfo result2 = (InterfaceMain.pbui_Type_MemberDetailInfo) result;
                if (result2 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result2);
                    bundle.putParcelableArrayList("queryAttendById", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_ATTENDEE://查询参会人员
                InterfaceMain.pbui_Type_MemberDetailInfo result3 = (InterfaceMain.pbui_Type_MemberDetailInfo) result;
                if (result3 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result3);
                    bundle.putParcelableArrayList("queryMember", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
            case IDivMessage.QUERY_Attendee_Property://96.查询参会人员属性
                InterfaceMain.pbui_Type_MeetMembeProperty result4 = (InterfaceMain.pbui_Type_MeetMembeProperty) result;
                if (result4 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result4);
                    bundle.putParcelableArrayList("queryAttendeeProperty", arrayList);
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
            case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                InterfaceMain2.pbui_Type_MeetSeatDetailInfo result5 = (InterfaceMain2.pbui_Type_MeetSeatDetailInfo) result;
                if (result5 != null) {
                    Bundle bundle = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(result5);
                    bundle.putParcelableArrayList("queryMeetRanking", arrayList);
                    Message message = new Message();
                    message.what = action;
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        initController();
        initView();
        initImages();
        mFm = getSupportFragmentManager();
        //设置默认点击第一项
        showFragment(0);
        setImgSelect(0);
        //8.修改本机界面状态
        nativeUtil.setInterfaceState(InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber());
        //获取从MainActivity中传递过来的参数
        getIntentBundle();
        try {
            //92.查询参会人员
            nativeUtil.queryAttendPeople();
            //181.查询会议排位
            nativeUtil.queryMeetRanking();
            //6.查询设备信息
            nativeUtil.queryDeviceInfo();
            //125.查询符合要求的设备ID
//            nativeUtil.queryRequestDeviceId();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        //注册EventBus
        EventBus.getDefault().register(this);
    }

    /**
     * 获取从MainActivity中传递过来的参数
     */
    private void getIntentBundle() {
        Intent intent = getIntent();
        Bundle putId = intent.getBundleExtra("putId");
        ArrayList devMeetInfo = putId.getParcelableArrayList("devMeetInfo");

        int devId = (int) devMeetInfo.get(0);
        int meetingid = (int) devMeetInfo.get(1);
        int memberid = (int) devMeetInfo.get(2);
        int roomid = (int) devMeetInfo.get(3);
        int signin_type = (int) devMeetInfo.get(4);
        String meetingname = (String) devMeetInfo.get(5);
        String membername = (String) devMeetInfo.get(6);
        String company = (String) devMeetInfo.get(7);
        String job = (String) devMeetInfo.get(8);
        String compereName = (String) devMeetInfo.get(9);

        mMeetingCompanyName.setText(company);
        mMeetingTheme.setText(meetingname);
        mAttendee.setText(membername);
        mCompere.setText(compereName);

        o = new MainDivMeetInfo();
        o.setDevId(devId);
        o.setMeetingid(meetingid);
        o.setMemberid(memberid);
        o.setRoomid(roomid);
        o.setSignin_type(signin_type);
        o.setMeetingname(meetingname);
        o.setMembername(membername);
        o.setCompany(company);
        o.setJob(job);

        try {
            //91.查询指定ID的参会人员
//            nativeUtil.queryAttendPeopleFromId(memberid);
            /** ************ ******  125.查询符合要求的设备ID  （投影机）  ****** ************ **/
            // TODO: 2018/2/27 回调在签到页面进行的处理
            nativeUtil.queryRequestDeviceId(roomid,
                    Macro.DEVICE_MEET_PROJECTIVE,
//                    InterfaceMacro.Pb_RoomDeviceFilterFlag.Pb_MEET_ROOMDEVICE_FLAG_PROJECTIVE.getNumber(),
                    InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber(), 0);

            Log.e("MyLog", "MeetingActivity.getIntentBundle:   --->>> devId： " + devId + "  memberid：" + memberid);
            /** ************ ******  96.查询参会人员属性  ****** ************ **/
            nativeUtil.queryAttendPeopleProperties(InterfaceMacro.Pb_MemberPropertyID.Pb_MEETMEMBER_PROPERTY_JOB.getNumber(), memberid);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    //获取当前的设备ID
    public static int getDevId() {
        return o.getDevId();
    }

    //获取当前的会议名称
    public static String getMeetName() {
        return o.getMeetingname();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.UpDate_BadgeNumber:
                Log.e("MyLog", "MeetingActivity.getEventMessage 457行:  11111111 --->>> ");
                List<EventBadge> object3 = (List<EventBadge>) message.getObject();
                if (object3 != null) {
                    Log.e("MyLog", "MeetingActivity.getEventMessage 459行:  222 --->>> " + object3.get(0).getNum());
                    mBadge.setBadgeNumber(object3.get(0).getNum());
                }
                break;
            case IDEventMessage.DEV_REGISTER_INFORM:
                Log.e("MyLog", "MeetingActivity.getEventMessage:  设备寄存器变更通知 EventBus --->>> ");
                break;
            case IDEventMessage.MEET_DATE:
                //更新时间UI
                updataTimeUi(message);
                break;
            case IDEventMessage.FACESTATUS_CHANGE_INFORM:
                InterfaceMain.pbui_MeetDeviceMeetStatus o1 = (InterfaceMain.pbui_MeetDeviceMeetStatus) message.getObject();
                Log.e("MyLog", "MeetingActivity.getEventMessage: 界面状态变更通知 EventBus --->>> 会议ID：" + o1.getMeetingid() +
                        " 人员ID：" + o1.getMemberid() + " 设备ID：" + o1.getDeviceid() + " 界面状态：" + o1.getFacestatus());
                //91.查询指定ID的参会人员
                nativeUtil.queryAttendPeopleFromId(o1.getMemberid());
                /** ************ ******  96.查询参会人员属性  ****** ************ **/
                nativeUtil.queryAttendPeopleProperties(InterfaceMacro.Pb_MemberPropertyID.Pb_MEETMEMBER_PROPERTY_JOB.getNumber(), o.getMemberid());
                //128.查询会议
//                nativeUtil.queryMeet();
//              //129.查询指定ID的会议
//                nativeUtil.queryMeetFromId(meetingid);
                break;
            case IDEventMessage.MEETINFO_CHANGE_INFORM:// 会议信息变更通知
                Log.e("MyLog", "MeetingActivity.getEventMessage:  会议信息变更通知 EventBus --->>> ");
                InterfaceMain.pbui_MeetNotifyMsg object1 = (InterfaceMain.pbui_MeetNotifyMsg) message.getObject();
//                int id = object1.getId();
//                int opermethod = object1.getOpermethod();
                //129.查询指定ID的会议
//                nativeUtil.queryMeetFromId(id);
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM:// 参会人员变更通知
                InterfaceMain.pbui_MeetNotifyMsg object2 = (InterfaceMain.pbui_MeetNotifyMsg) message.getObject();
                int id1 = object2.getId();
                int opermethod1 = object2.getOpermethod();
                Log.e("MyLog", "MeetingActivity.getEventMessage:  参会人员变更通知 EventBus --->>> id:  " + id1 + "  opermethod1：  " + opermethod1);
                //92.查询参会人员
                nativeUtil.queryAttendPeople();
                //91.查询指定ID的参会人员
//                nativeUtil.queryAttendPeopleFromId(id1);
                /** ************ ******  96.查询参会人员属性  ****** ************ **/
//                nativeUtil.queryAttendPeopleProperties(InterfaceMacro.Pb_MemberPropertyID.Pb_MEETMEMBER_PROPERTY_JOB.getNumber(), 12);

                break;
            case IDEventMessage.MeetSeat_Change_Inform:
                Log.e("MyLog", "MeetingActivity.getEventMessage:  180.会议排位变更通知 EventBus --->>> ");
                InterfaceMain.pbui_MeetNotifyMsg object = (InterfaceMain.pbui_MeetNotifyMsg) message.getObject();
                int id = object.getId();
                int opermethod = object.getOpermethod();
                /** ************ ******  181.查询会议排位  ****** ************ **/
                nativeUtil.queryMeetRanking();
                break;
            case IDEventMessage.PLACE_DEVINFO_CHANGEINFORM:
                //119 会场设备信息变更通知(投影机)
                Log.e("MyLog", "MeetingActivity.getEventMessage:  查询投影机ID EventBus --->>> ");
                /** ************ ******  125.查询符合要求的设备ID  （投影机）  ****** ************ **/
                // TODO: 2018/2/27 回调在签到页面做的处理
                nativeUtil.queryRequestDeviceId(o.getRoomid(),
                        Macro.DEVICE_MEET_PROJECTIVE,
//                        InterfaceMacro.Pb_RoomDeviceFilterFlag.Pb_MEET_ROOMDEVICE_FLAG_PROJECTIVE.getNumber(),
                        InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber(), 0);
                break;
        }
    }

    private void updataTimeUi(EventMessage message) {
        String[] object = (String[]) message.getObject();
        mMeetingNowDate.setText(object[0]);
        mMeetingNowWeek.setText(object[1]);
        mMeetingNowTime.setText(object[2]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将会议笔记的文本清空
        mNoteCentent = "";
        //取消注册事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initController() {
        nativeUtil = NativeUtil.getInstance();
        nativeUtil.setCallListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //回到主界面操作
        nativeUtil.backToMainInterfaceOperate();
    }

    private void initImages() {
        mImages = new ArrayList<>();
        mImages.add(mSignin);
        mImages.add(mAgenda);
        mImages.add(mMeetingfile);
        mImages.add(mSharedfile);
        mImages.add(mWhiteboard);
        mImages.add(mChat);
        mImages.add(mVideo);
        mImages.add(mVote);
        mImages.add(mPostil);
        mImages.add(mWebbrowse);
    }

    private void setImgSelect(int index) {
        for (int i = 0; i < mImages.size(); i++) {
            if (i == index) {
                mImages.get(i).setSelected(true);
            } else {
                mImages.get(i).setSelected(false);
            }
        }
    }

    public void showFragment(int index) {
        FragmentTransaction ft = mFm.beginTransaction();
        hideFragment(ft);
        switch (index) {
            case 0:
                if (mSigninFragment == null) {
                    mSigninFragment = new SigninFragment();
                    ft.add(R.id.right_layout, mSigninFragment);
                }
                ft.show(mSigninFragment);
                break;
            case 1:
                if (mAnnAgendaFragment == null) {
                    mAnnAgendaFragment = new AnnAgendaFragment();
                    ft.add(R.id.right_layout, mAnnAgendaFragment);
                }
                ft.show(mAnnAgendaFragment);
                break;
            case 2:
                if (mMeetingFileFragment == null) {
                    mMeetingFileFragment = new MeetingFileFragment();
                    ft.add(R.id.right_layout, mMeetingFileFragment);
                }
                ft.show(mMeetingFileFragment);
                break;
            case 3:
                if (mSharedFileFragment == null) {
                    mSharedFileFragment = new SharedFileFragment();
                    ft.add(R.id.right_layout, mSharedFileFragment);
                }
                ft.show(mSharedFileFragment);
                break;
            case 5:
                if (mChatFragment == null) {
                    mChatFragment = new ChatFragment();
                    ft.add(R.id.right_layout, mChatFragment);
                }
                ft.show(mChatFragment);
                break;
            case 6:
                if (mVideoFragment == null) {
                    mVideoFragment = new VideoFragment();
                    ft.add(R.id.right_layout, mVideoFragment);
                }
                ft.show(mVideoFragment);
                break;
            case 7:
                if (mVoteFragment == null) {
                    mVoteFragment = new VoteFragment();
                    ft.add(R.id.right_layout, mVoteFragment);
                }
                ft.show(mVoteFragment);
                break;
            case 8:
                if (mNotationFragment == null) {
                    mNotationFragment = new NotationFragment();
                    ft.add(R.id.right_layout, mNotationFragment);
                }
                ft.show(mNotationFragment);
                break;
            case 9:
                if (mWebbrowseFragment == null) {
                    mWebbrowseFragment = new WebBrowseFragment();
                    ft.add(R.id.right_layout, mWebbrowseFragment);
                }
                ft.show(mWebbrowseFragment);
                break;
        }
        ft.commit();
    }

    /**
     * 如果Fragment不为空 就先隐藏起来
     *
     * @param ft
     */
    public void hideFragment(FragmentTransaction ft) {

        if (mSigninFragment != null) {
            ft.hide(mSigninFragment);
        }
        if (mAnnAgendaFragment != null) {
            ft.hide(mAnnAgendaFragment);
        }
        if (mMeetingFileFragment != null) {
            ft.hide(mMeetingFileFragment);
        }
        if (mSharedFileFragment != null) {
            ft.hide(mSharedFileFragment);
        }
        if (mChatFragment != null) {
            ft.hide(mChatFragment);
        }
        if (mVideoFragment != null) {
            ft.hide(mVideoFragment);
        }
        if (mVoteFragment != null) {
            ft.hide(mVoteFragment);
        }
        if (mNotationFragment != null) {
            ft.hide(mNotationFragment);
        }

    }

    private void initView() {
        mMeetingCompanyName = (TextView) findViewById(R.id.meeting_company_name);
        mMeetActivityLayout = (LinearLayout) findViewById(R.id.meetActivity_layout);
        mMeetingTheme = (TextView) findViewById(R.id.meeting_theme);
        mMeetingMessage = (ImageButton) findViewById(R.id.meeting_message);
        /** ************ ******  设置未读消息展示  ****** ************ **/
        mBadge = new QBadgeView(this).bindTarget(mMeetingMessage);
        mBadge.setBadgeGravity(Gravity.END | Gravity.TOP);
        mBadge.setBadgeTextSize(20, false);
        mBadge.setShowShadow(true);
        mBadge.setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
            @Override
            public void onDragStateChanged(int dragState, Badge badge, View targetView) {

            }
        });

        mMeetingChatOnline = (ImageButton) findViewById(R.id.meeting_chat_online);
        mMeetingNowTime = (TextView) findViewById(R.id.meeting_now_time);
        mMeetingNowDate = (TextView) findViewById(R.id.meeting_now_date);
        mMeetingNowWeek = (TextView) findViewById(R.id.meeting_now_week);
        mTheNowDate = (PercentLinearLayout) findViewById(R.id.the_now_date);
        mAttendee = (TextView) findViewById(R.id.meeting_participate_name);
        mCompere = (TextView) findViewById(R.id.meeting_host_name);

        mSignin = (ImageView) findViewById(R.id.signin);
        mAgenda = (ImageView) findViewById(R.id.agenda);
        mMeetingfile = (ImageView) findViewById(R.id.meetingfile);
        mSharedfile = (ImageView) findViewById(R.id.sharedfile);
        mWhiteboard = (ImageView) findViewById(R.id.whiteboard);
        mChat = (ImageView) findViewById(R.id.chat);
        mVideo = (ImageView) findViewById(R.id.video);
        mVote = (ImageView) findViewById(R.id.vote);
        mPostil = (ImageView) findViewById(R.id.postil);
        mWebbrowse = (ImageView) findViewById(R.id.webbrowse);
        mLeftLayout = (PercentLinearLayout) findViewById(R.id.left_layout);
        mRightLayout = (FrameLayout) findViewById(R.id.right_layout);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mMeetingMessage.setOnClickListener(this);
        mMeetingChatOnline.setOnClickListener(this);
        mSignin.setOnClickListener(this);
        mAgenda.setOnClickListener(this);
        mMeetingfile.setOnClickListener(this);
        mSharedfile.setOnClickListener(this);
        mWhiteboard.setOnClickListener(this);
        mChat.setOnClickListener(this);
        mVideo.setOnClickListener(this);
        mVote.setOnClickListener(this);
        mPostil.setOnClickListener(this);
        mWebbrowse.setOnClickListener(this);
        mFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_message:
                setImgSelect(5);
                showFragment(5);
                break;
            case R.id.meeting_chat_online:
                // TODO: 2018/2/7 打开发送服务选项
                showSendFunctionMsg();
                break;
            case R.id.signin:
                setImgSelect(0);
                showFragment(0);
                break;
            case R.id.agenda:
                showFragment(1);
                setImgSelect(1);
                break;
            case R.id.meetingfile:
                showFragment(2);
                setImgSelect(2);
                break;
            case R.id.sharedfile:
                showFragment(3);
                setImgSelect(3);
                break;
            case R.id.whiteboard://跳转到画板
                nativeUtil.coerceStartWhiteBoard(o.getDevId(), o.getMemberid(), IDivMessage.OPEN_BROARD);
                startActivity(new Intent(MeetingActivity.this, DrawBoardActivity.class));
                // 收到打开白板操作
                // IDEventMessage.OPEN_BOARD
                break;
            case R.id.chat:
                showFragment(5);
                setImgSelect(5);
                break;
            case R.id.video:
                showFragment(6);
                setImgSelect(6);
                break;
            case R.id.vote:
                showFragment(7);
                setImgSelect(7);
                break;
            case R.id.postil:
                showFragment(8);
                setImgSelect(8);
                break;
            case R.id.webbrowse:
                showFragment(9);
                break;
            case R.id.fab:
                showFabPop();
                break;
        }
    }

    /**
     * 服务功能选项
     */
    private void showSendFunctionMsg() {
        View popupView = getLayoutInflater().inflate(R.layout.pop_function_msg, null);
        mFunctionMsgPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mFunctionMsgPop);
        mFunctionMsgPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mFunctionMsgPop.setTouchable(true);
        mFunctionMsgPop.setOutsideTouchable(true);
        FunViewHolder holder = new FunViewHolder(popupView);
        Function_Event(holder);
        mFunctionMsgPop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);

    }

    /**
     * 服务功能选项事件监听
     *
     * @param holder
     */
    private void Function_Event(final FunViewHolder holder) {
        final List<Integer> arr = new ArrayList<>();
        arr.add(0);
        // TODO: 2018/2/7 服务功能事件 发送会议交流信息
        //纸
        holder.paper_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                /** ************ ******  185.发送会议交流信息  ****** ************ **/
                nativeUtil.sendMeetChatInfo("纸", InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Paper.getNumber(), arr);
            }
        });
        //笔
        holder.pen_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                nativeUtil.sendMeetChatInfo("笔", InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Pen.getNumber(), arr);
            }
        });
        //茶水
        holder.tea_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                nativeUtil.sendMeetChatInfo("茶水", InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Tea.getNumber(), arr);
            }
        });
        //矿泉水
        holder.water_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                nativeUtil.sendMeetChatInfo("矿泉水", InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Water.getNumber(), arr);
            }
        });
        //计算器
        holder.calculator_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                nativeUtil.sendMeetChatInfo("计算器", InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber(), arr);

            }
        });
        //服务员
        holder.waiter_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                nativeUtil.sendMeetChatInfo("服务员", InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Waiter.getNumber(), arr);
            }
        });
        //清扫
        holder.sweep_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                nativeUtil.sendMeetChatInfo("清扫", InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber(), arr);
            }
        });
        //技术员
        holder.technician_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                nativeUtil.sendMeetChatInfo("技术员", InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Technical.getNumber(), arr);
            }
        });
        //发送
        holder.send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = holder.edt_msg.getText().toString();
                nativeUtil.sendMeetChatInfo(string, InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Message.getNumber(), arr);
                holder.edt_msg.setText("");
            }
        });

    }

    /**
     * 展示fab
     */
    public void showFabPop() {
        View popupView = getLayoutInflater().inflate(R.layout.pop_meeting, null);
        mPopupWindow = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置动画
        mPopupWindow.setAnimationStyle(R.style.AnimHorizontal);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        FabViewHolder holder = new FabViewHolder(popupView);
        Fab_Event(holder);
        mPopupWindow.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    /**
     * FabPopupWindow 点击事件处理
     *
     * @param holder
     */
    private void Fab_Event(final FabViewHolder holder) {
        //软键盘
        holder.keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MeetingActivity.this, "点击了软键盘", Toast.LENGTH_SHORT).show();
                setAnimator(holder.keyboard);
            }
        });
        //手写
        holder.handwritten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MeetingActivity.this, "点击了手写", Toast.LENGTH_SHORT).show();
                setAnimator(holder.handwritten);
            }
        });
        //桌面截图
        holder.screens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.screens);
                mPopupWindow.dismiss();
//                MyUtils.ScreenShot(mMeetActivityLayout);
                ScreenUtils.snapShotWithStatusBar(MeetingActivity.this);

            }
        });
        //同屏控制
        holder.oneScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.oneScreen);
                mPopupWindow.dismiss();
                //打开同屏控制弹出框
                // TODO: 2018/2/6 打开同屏控制
                showScreensPop();
            }
        });
        //返回
        holder.backPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MeetingActivity.this, "点击了返回", Toast.LENGTH_SHORT).show();
                setAnimator(holder.backPop);
                mPopupWindow.dismiss();
            }
        });
        //投影控制
        holder.projection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.projection);
                // TODO: 2018/2/6 打开投影控制
                showProjector();

            }
        });
        //会议笔记
        holder.note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MeetingActivity.this, "点击了会议笔记", Toast.LENGTH_SHORT).show();
                setAnimator(holder.note);
                showNotePop();
            }
        });
        //呼叫服务
        holder.callService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.callService);
                showSendFunctionMsg();
            }
        });
        //电子白板
        holder.whiteplatePop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.whiteplatePop);
                startActivity(new Intent(MeetingActivity.this, DrawBoardActivity.class));
            }
        });

    }

    /**
     * 打开会议笔记
     */
    private void showNotePop() {
        View popupView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.note_pop, null);
        mNotePop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mNotePop);
        mNotePop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mNotePop.setTouchable(true);
        mNotePop.setOutsideTouchable(true);
        NoteViewHolder holder = new NoteViewHolder(popupView);
        NoteHolderEvent(holder);
        mNotePop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
        Log.e("MyLog", "MeetingActivity.showNotePop 1015行:   --->>> ");
    }

    /**
     * 会议笔记pop事件监听
     * @param holder
     */
    private void NoteHolderEvent(final NoteViewHolder holder) {
        //返回
        holder.noteBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取输入的文本
                mNoteCentent = holder.edtNote.getText().toString();
                mNotePop.dismiss();
            }
        });
        //本地保存
        holder.noteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNoteCentent = holder.edtNote.getText().toString();
                //保存到手机
                // TODO: 2018/3/7  
                mNotePop.dismiss();
            }
        });
    }

    /**
     * 投影控制
     */
    private void showProjector() {
        View popupView = getLayoutInflater().inflate(R.layout.pop_projector, null);
        mProjectorPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mProjectorPop);
        mProjectorPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mProjectorPop.setTouchable(true);
        mProjectorPop.setOutsideTouchable(true);
        ProViewHolder holder = new ProViewHolder(popupView);
        Pro_Event(holder);
        mProjectorPop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    /**
     * 投影控制 事件监听
     *
     * @param holder
     */
    private void Pro_Event(final ProViewHolder holder) {
        //所有投影机
        holder.all_pro_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // TODO: 2018/2/6 获取全部的投影机
                    for (int i = 0; i < checks.size(); i++) {
                        checks.set(i, true);
                    }
                    //获取选中的投影机
                    List<DeviceInfo> checkedIds = allProjectorAdapter.getCheckedIds();
                }
            }
        });
        //选择投影机
        holder.choose_pro_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2018/2/6 打开投影机列表
                showProRlPop();
            }
        });
        //申请投影
        holder.start_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds = allProjectorAdapter.getCheckedIds();
                }
            }
        });
        //结束投影
        holder.stop_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.pro_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProjectorPop.dismiss();
            }
        });

    }

    /**
     * 投影控制 投影机列表
     */
    private void showProRlPop() {
        View popupView = getLayoutInflater().inflate(R.layout.pro_pop, null);
        mProRlPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mProRlPop);
        mProRlPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mProRlPop.setTouchable(true);
        mProRlPop.setOutsideTouchable(true);
        try {
            //6.查询设备信息
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        ProRlViewHolder holder = new ProRlViewHolder(popupView);
        ProRl_Enent(holder);
        mProRlPop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    /**
     * 投影控制 投影机列表 事件监听
     *
     * @param holder
     */
    private void ProRl_Enent(final ProRlViewHolder holder) {
        allProjectorAdapter.setItemClick(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Button player = view.findViewById(R.id.palyer_name);
                boolean selected = !player.isSelected();
                player.setSelected(selected);
                allPerchecks.set(posion, selected);
                holder.pro_all_cb.setChecked(!allPerchecks.contains(false));
                allProjectorAdapter.notifyDataSetChanged();
            }
        });
        ///////////
        holder.pro_all_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //如果包含false
                    for (int i = 0; i < allProjectors.size(); i++) {
                        if (!allPerchecks.get(i)) {
                            //将包含false的选项设为true
                            allPerchecks.set(i, true);
                        }
                    }
                    // TODO: 2018/2/6 获取全部的数据
                } else {
                    //全选为false
                    if (!allPerchecks.contains(false)) {
                        //不包含false 就是全部为true的状态
                        for (int i = 0; i < allProjectors.size(); i++) {
                            //全部都设为false
                            allPerchecks.set(i, false);
                        }
                    }
                }
                allProjectorAdapter.notifyDataSetChanged();
            }
        });
        holder.pro_ensure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds = allProjectorAdapter.getCheckedIds();
                    Log.e("MyLog", "MeetingActivity.onClick 1067行:  选中的投影机有 --->>> " + checkedIds.size());
                }
                mProRlPop.dismiss();
                allProjectorAdapter.notifyDataSetChanged();
            }
        });
        holder.pro_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProRlPop.dismiss();
            }
        });
    }


    /**
     * 同屏控制
     */
    private void showScreensPop() {
        View popupView = getLayoutInflater().inflate(R.layout.pop_sscreen, null);
        mScreenPopupWindow = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mScreenPopupWindow);
        mScreenPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mScreenPopupWindow.setTouchable(true);
        mScreenPopupWindow.setOutsideTouchable(true);
        ScreenViewHolder holder = new ScreenViewHolder(popupView);
        Screen_Event(holder);
        mScreenPopupWindow.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    /**
     * 同屏控制 事件监听
     */
    private void Screen_Event(final ScreenViewHolder holder) {
        //选中所有人
        holder.everyone_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //如果 所有人 为true
                    for (int i = 0; i < checks.size(); i++) {
                        checks.set(i, true);
                    }
                    Log.e("MyLog", "MeetingActivity.onCheckedChanged:  全部设置为 true --->>> ");
                    //直接获取全部的数据 onLineMembers
                    // TODO: 2018/2/2 获取到选中的人员
                    List<MemberInfo> checkedIds = onLineMemberAdapter.getCheckedIds();
                    Log.e("MyLog", "MeetingActivity.onCheckedChanged:  选中的参会人： --->>> " + checkedIds.size());
                }
            }
        });

        //选择参与人
        holder.choose_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //选择参与人 和 投影机
                showPlayerPop();
            }
        });


        //选中所有投影机
        holder.all_projector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //如果 所有人 为true
                    for (int i = 0; i < checks.size(); i++) {
                        checks.set(i, true);
                    }
                    List<DeviceInfo> checkedIds = onLineProjectorAdapter.getCheckedIds();
                }
            }
        });
        //自由选择投影机
        holder.choose_projector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPlayerPop();
            }
        });
        //同屏控制按钮
        holder.screens_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MeetingActivity.this, "同屏控制", Toast.LENGTH_SHORT).show();
            }
        });
        //停止同屏按钮
        holder.stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MeetingActivity.this, "停止同屏", Toast.LENGTH_SHORT).show();
            }
        });
        //加入同屏按钮
        holder.join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MeetingActivity.this, "加入同屏", Toast.LENGTH_SHORT).show();
            }
        });
        holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScreenPopupWindow.dismiss();
            }
        });

    }

    /**
     * 投影控制
     */
    public static class ProViewHolder {
        public View rootView;
        public RadioButton all_pro_cb;
        public RadioButton choose_pro_cb;
        public Button start_pro;
        public Button stop_pro;
        public Button pro_cancel;

        public ProViewHolder(View rootView) {
            this.rootView = rootView;
            this.all_pro_cb = (RadioButton) rootView.findViewById(R.id.all_pro_cb);
            this.choose_pro_cb = (RadioButton) rootView.findViewById(R.id.choose_pro_cb);
            this.start_pro = (Button) rootView.findViewById(R.id.start_pro);
            this.stop_pro = (Button) rootView.findViewById(R.id.stop_pro);
            this.pro_cancel = (Button) rootView.findViewById(R.id.pro_cancel);
        }

    }

    //同屏控制
    public static class ScreenViewHolder {
        public View rootView;
        public RadioButton everyone_cb;
        public RadioButton choose_player;
        public RadioButton all_projector;
        public RadioButton choose_projector;
        public Button screens_btn;
        public Button stop_btn;
        public Button join_btn;
        public Button cancel_btn;

        public ScreenViewHolder(View rootView) {
            this.rootView = rootView;
            this.everyone_cb = (RadioButton) rootView.findViewById(R.id.everyone_cb);
            this.choose_player = (RadioButton) rootView.findViewById(R.id.players);
            this.all_projector = (RadioButton) rootView.findViewById(R.id.all_projector);
            this.choose_projector = (RadioButton) rootView.findViewById(R.id.choose_projector);
            this.screens_btn = (Button) rootView.findViewById(R.id.screens_btn);
            this.stop_btn = (Button) rootView.findViewById(R.id.stop_btn);
            this.join_btn = (Button) rootView.findViewById(R.id.join_btn);
            this.cancel_btn = (Button) rootView.findViewById(R.id.cancel_btn);
        }

    }

    /**
     * 选择参会人
     */
    private void showPlayerPop() {
        View popupView = getLayoutInflater().inflate(R.layout.player_pop, null);
        mPlayerPopupWindow = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mPlayerPopupWindow);
        mPlayerPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPlayerPopupWindow.setTouchable(true);
        mPlayerPopupWindow.setOutsideTouchable(true);
        try {
            //6.查询设备信息（查找在线的投影机）
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        PlayerViewHolder holder = new PlayerViewHolder(popupView);
        Player_Event(holder);
        mPlayerPopupWindow.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    /**
     * 选择参会人Popup 事件监听
     *
     * @param holder
     */
    private void Player_Event(final PlayerViewHolder holder) {
        //参会人item点击事件
        onLineMemberAdapter.setItemClick(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Button player = view.findViewById(R.id.palyer_name);
                boolean selected = !player.isSelected();
                player.setSelected(selected);
                checks.set(posion, selected);
                holder.playersAllCb.setChecked(!checks.contains(false));
                onLineMemberAdapter.notifyDataSetChanged();
            }
        });
        //投影机item点击事件
        onLineProjectorAdapter.setItemClick(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int posion) {
                Button player = view.findViewById(R.id.palyer_name);
                boolean selected = !player.isSelected();
                player.setSelected(selected);
                checks.set(posion, selected);
                holder.playersAllCb.setChecked(!checks.contains(false));
                onLineProjectorAdapter.notifyDataSetChanged();
            }
        });
        ///////////
        //全选按钮状态监听
        holder.playersAllCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e("MyLog", "MeetingActivity.onCheckedChanged:  是否全部选中 --->>> " + b);
                if (b) {
                    //如果包含false
                    for (int i = 0; i < onLineMembers.size(); i++) {
                        if (!checks.get(i)) {
                            //将包含false的选项设为true
                            checks.set(i, true);
                        }
                    }
                    // TODO: 2018/2/6 获取全部的数据
                } else {
                    //全选为false
                    if (!checks.contains(false)) {
                        //不包含false 就是全部为true的状态
                        for (int i = 0; i < onLineMembers.size(); i++) {
                            //全部都设为false
                            checks.set(i, false);
                        }
                    }
                }
                onLineMemberAdapter.notifyDataSetChanged();
            }
        });
        // 投影机全选 选择框
        holder.projectorAllCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //如果包含false
                    for (int i = 0; i < onLineProjectors.size(); i++) {
                        if (!checks.get(i)) {
                            //将包含false的选项设为true
                            checks.set(i, true);
                        }
                    }
                    // TODO: 2018/2/6 获取全部的数据
                } else {
                    //全选为false
                    if (!checks.contains(false)) {
                        //不包含false 就是全部为true的状态
                        for (int i = 0; i < onLineProjectors.size(); i++) {
                            //全部都设为false
                            checks.set(i, false);
                        }
                    }
                }
                onLineProjectorAdapter.notifyDataSetChanged();

            }
        });
        //确定按钮
        holder.playerPopEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2018/2/2 获取到选中的人员
                if (onLineMemberAdapter != null) {
                    List<MemberInfo> checkedIds = onLineMemberAdapter.getCheckedIds();
                    Log.e("MyLog", "MeetingActivity.onClick 1262行:  选中的人员数量： --->>> " + checkedIds.size());
                }
                if (onLineProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds1 = onLineProjectorAdapter.getCheckedIds();
                    Log.e("MyLog", "MeetingActivity.onClick 1266行:  选中投影机数量 --->>> " + checkedIds1.size());
                }
                mPlayerPopupWindow.dismiss();
                onLineMemberAdapter.notifyDataSetChanged();
                onLineProjectorAdapter.notifyDataSetChanged();
            }
        });
        //取消按钮
        holder.playerPopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerPopupWindow.dismiss();
            }
        });

    }


    public static class FabViewHolder {
        public View rootView;
        public TextView keyboard;
        public TextView handwritten;
        public TextView screens;
        public TextView oneScreen;
        public TextView backPop;
        public TextView projection;
        public TextView note;
        public TextView callService;
        public TextView whiteplatePop;

        public FabViewHolder(View rootView) {
            this.rootView = rootView;
            this.keyboard = (TextView) rootView.findViewById(R.id.keyboard);
            this.handwritten = (TextView) rootView.findViewById(R.id.handwritten);
            this.screens = (TextView) rootView.findViewById(R.id.screens);
            this.oneScreen = (TextView) rootView.findViewById(R.id.one_screen);
            this.backPop = (TextView) rootView.findViewById(R.id.back_pop);
            this.projection = (TextView) rootView.findViewById(R.id.projection);
            this.note = (TextView) rootView.findViewById(R.id.note);
            this.callService = (TextView) rootView.findViewById(R.id.call_service);
            this.whiteplatePop = (TextView) rootView.findViewById(R.id.whiteplate_pop);
        }

    }


    public class PlayerViewHolder {
        public View rootView;
        public CheckBox playersAllCb;
        public RecyclerView playersRl;
        public CheckBox projectorAllCb;
        public RecyclerView projectorRl;
        public Button playerPopEnsure;
        public Button playerPopCancel;

        public PlayerViewHolder(final View rootView) {
            this.rootView = rootView;
            this.playersAllCb = (CheckBox) rootView.findViewById(R.id.players_all_cb);
            Log.e("MyLog", "PlayerViewHolder.PlayerViewHolder:  在线参会人数量： --->>> " + onLineMembers.size());
            this.playersRl = (RecyclerView) rootView.findViewById(R.id.players_rl);
            //瀑布流布局
            this.playersRl.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.HORIZONTAL));
            this.playersRl.setAdapter(onLineMemberAdapter);
            /**
             * *************************************
             */
            this.projectorAllCb = (CheckBox) rootView.findViewById(R.id.projector_all_cb);
            this.projectorRl = (RecyclerView) rootView.findViewById(R.id.projector_rl);
            //表格布局
            this.projectorRl.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
            this.projectorRl.setAdapter(onLineProjectorAdapter);
            /**
             * *************************************
             */
            this.playerPopEnsure = (Button) rootView.findViewById(R.id.player_pop_ensure);
            this.playerPopCancel = (Button) rootView.findViewById(R.id.player_pop_cancel);
        }

    }


    public class ProRlViewHolder {
        public View rootView;
        public CheckBox pro_all_cb;
        public RecyclerView pro_rl;
        public Button pro_ensure_btn;
        public Button pro_cancel_btn;

        public ProRlViewHolder(View rootView) {
            this.rootView = rootView;
            this.pro_all_cb = (CheckBox) rootView.findViewById(R.id.pro_all_cb);
            this.pro_rl = (RecyclerView) rootView.findViewById(R.id.pro_rl);
            this.pro_rl.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.HORIZONTAL));
            this.pro_rl.setAdapter(allProjectorAdapter);
            this.pro_ensure_btn = (Button) rootView.findViewById(R.id.pro_ensure_btn);
            this.pro_cancel_btn = (Button) rootView.findViewById(R.id.pro_cancel_btn);
        }
    }

    /**
     * 服务功能
     */
    public static class FunViewHolder {
        public View rootView;
        public TextView paper_msg;
        public TextView pen_msg;
        public TextView tea_msg;
        public TextView water_msg;
        public TextView calculator_msg;
        public TextView waiter_msg;
        public TextView sweep_msg;
        public TextView technician_msg;
        public EditText edt_msg;
        public TextView send_msg;

        public FunViewHolder(View rootView) {
            this.rootView = rootView;
            this.paper_msg = (TextView) rootView.findViewById(R.id.paper_msg);
            this.pen_msg = (TextView) rootView.findViewById(R.id.pen_msg);
            this.tea_msg = (TextView) rootView.findViewById(R.id.tea_msg);
            this.water_msg = (TextView) rootView.findViewById(R.id.water_msg);
            this.calculator_msg = (TextView) rootView.findViewById(R.id.calculator_msg);
            this.waiter_msg = (TextView) rootView.findViewById(R.id.waiter_msg);
            this.sweep_msg = (TextView) rootView.findViewById(R.id.sweep_msg);
            this.technician_msg = (TextView) rootView.findViewById(R.id.technician_msg);
            this.edt_msg = (EditText) rootView.findViewById(R.id.edt_msg);
            this.send_msg = (TextView) rootView.findViewById(R.id.send_msg);
        }

    }

    /**
     * 会议笔记控件
     */
    public static class NoteViewHolder {
        public View rootView;
        public EditText edtNote;
        public Button noteImport;
        public Button noteSave;
        public Button noteBack;

        public NoteViewHolder(View rootView) {
            this.rootView = rootView;
            this.edtNote = (EditText) rootView.findViewById(R.id.edt_note);
            if(!("".equals(mNoteCentent))){
                this.edtNote.setText(mNoteCentent);
            }
            this.noteImport = (Button) rootView.findViewById(R.id.note_import);
            this.noteSave = (Button) rootView.findViewById(R.id.note_save);
            this.noteBack = (Button) rootView.findViewById(R.id.note_back);
        }

    }
}