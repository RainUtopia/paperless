package com.pa.paperless.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.pa.paperless.R;
import com.pa.paperless.adapter.JoinAdapter;
import com.pa.paperless.adapter.OnLineProjectorAdapter;
import com.pa.paperless.adapter.ScreenControlAdapter;
import com.pa.paperless.bean.DevMember;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.AnnAgendaFragment;
import com.pa.paperless.fragment.meeting.ChatFragment;
import com.pa.paperless.fragment.meeting.MeetingFileFragment;
import com.pa.paperless.fragment.meeting.NotationFragment;
import com.pa.paperless.fragment.meeting.OverviewDocFragment;
import com.pa.paperless.fragment.meeting.SharedFileFragment;
import com.pa.paperless.fragment.meeting.SigninFragment;
import com.pa.paperless.fragment.meeting.VideoFragment;
import com.pa.paperless.fragment.meeting.VoteFragment;
import com.pa.paperless.fragment.meeting.WebBrowseFragment;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.listener.ItemClickListener;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.Export;
import com.pa.paperless.utils.FileUtil;
import com.pa.paperless.utils.MyUtils;
import com.pa.paperless.utils.ScreenUtils;
import com.wind.myapplication.CameraDemo;
import com.wind.myapplication.NativeUtil;
import com.wind.myapplication.ScreenRecorder;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.pa.paperless.utils.MyUtils.setAnimator;
import static com.pa.paperless.utils.MyUtils.setBackgroundAlpha;


public class MeetingActivity extends BaseActivity implements View.OnClickListener, CallListener {

    private ImageButton mMeetingMessage, mMeetingChatOnline;
    private TextView mMeetingNowTime, mMeetingNowDate, mMeetingNowWeek, mMeetingTheme, mMeetingCompanyName;
    private PopupWindow mFabPop, mProjectorPop, mProRlPop, mFunctionMsgPop, mNotePop, joinWatchPop, mScreenPop, mPlayerPop;
    //动态添加的ImageView控件
    private ImageView WhiteBoardIV, VideoIV, MeetChatIV, PostilIV, ShareFileIV, MeetFileIV, DocumentIV, SigninIV, VoteIV, AgendaIV, WebIV;
    private List<ImageView> mImages;
    public static TextView mAttendee, mCompere;//参会人，主持人
    private FloatingActionButton mFab;
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
    private OverviewDocFragment mOverViewDocFragment;
    public static NativeUtil nativeUtil;
    public static List<ReceiveMeetIMInfo> mReceiveMsg = new ArrayList<>();
    private ScreenControlAdapter onLineMemberAdapter;
    private List<MemberInfo> memberInfos;
    private List<DevMember> onLineMembers;
    private OnLineProjectorAdapter onLineProjectorAdapter, allProjectorAdapter;
    //用来存放所有/在线的投影机设备
    private List<DeviceInfo> allProjectors, onLineProjectors;
    public static Badge mBadge; //未读消息提示
    public static String mNoteCentent;
    public static List<Boolean> checks, checkProOL, checkProAll;
    private List<Integer> sameMemberDevIds, sameMemberDevRrsIds, applyProjectionIds, informDev, mFunIndexs;
    private List<InterfaceDevice.pbui_Item_DeviceResPlay> memberJoin, peojectorJoin;
    public static JoinAdapter joinMemberAdapter, joinProjectorAdapter;
    //存放是否点中
    public static List<Boolean> checkedJoinMember = new ArrayList<>();
    public static List<Boolean> checkedJoinProjector = new ArrayList<>();
    //add by gowcage
    final int REQUEST_CODE = 1;// >=0
    private int width, height, dpi, bitrate, VideoQuality = 1;//VideoQuality:1/2/3
    public static int W = 0, H = 0;
    private float density;
    private MediaProjectionManager manager;
    private MediaProjection projection;
    private ScreenRecorder recorder;
    private MeetingActivity context;
    public static InterfaceDevice.pbui_Type_DeviceFaceShowDetail DevMeetInfo = MainActivity.getLocalInfo();//设备会议信息
    public static InterfaceMember.pbui_Item_MemberDetailInfo mComperInfo = MainActivity.getCompereInfo();//主持人信息
    private LinearLayout fragment_layout;
    private Resources resources;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                    ArrayList queryMember = msg.getData().getParcelableArrayList("queryMember");
                    InterfaceMember.pbui_Type_MemberDetailInfo o2 = (InterfaceMember.pbui_Type_MemberDetailInfo) queryMember.get(0);
                    memberInfos = Dispose.MemberInfo(o2);
                    break;
                case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息(查找在线状态的投影机)
                    Log.e("MyLog", "MeetingActivity.handleMessage 184行:  查询设备信息 --->>> ");
                    DisposeDevInfo(msg);
                    break;
                case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                    DisposeIsCompere(msg);
                    break;
                case IDivMessage.QUERY_CAN_JOIN: // 查询可加入的同屏会话
                    DisposeCanJoin(msg);
                    break;
                case IDivMessage.QUERY_MEET_FUNCTION://查询会议功能
                    DisposeMeetFun(msg);
                    break;
            }
        }
    };

    //设置投票页面当前设备是否是主持人
    private void DisposeIsCompere(Message msg) {
        ArrayList queryMeetRanking = msg.getData().getParcelableArrayList("queryMeetRanking");
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo o5 = (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) queryMeetRanking.get(0);
        List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> itemList3 = o5.getItemList();
        for (int i = 0; i < itemList3.size(); i++) {
            InterfaceRoom.pbui_Item_MeetSeatDetailInfo pbui_item_meetSeatDetailInfo = itemList3.get(i);
            int nameId = pbui_item_meetSeatDetailInfo.getNameId();
            int role = pbui_item_meetSeatDetailInfo.getRole();
            int seatid = pbui_item_meetSeatDetailInfo.getSeatid();
            if (role == 3 && DevMeetInfo.getMemberid() == nameId) {
                //身份为主持人
                VoteFragment.isCompere = true;
            }
        }
    }

    //处理可加入的同屏绘画
    private void DisposeCanJoin(Message msg) {
        ArrayList queryCanJoin = msg.getData().getParcelableArrayList("queryCanJoin");
        InterfaceDevice.pbui_Type_DeviceResPlay o = (InterfaceDevice.pbui_Type_DeviceResPlay) queryCanJoin.get(0);
        List<InterfaceDevice.pbui_Item_DeviceResPlay> pdevList = o.getPdevList();
        memberJoin = new ArrayList<>();
        peojectorJoin = new ArrayList<>();
        for (int i = 0; i < pdevList.size(); i++) {
            InterfaceDevice.pbui_Item_DeviceResPlay pbui_item_deviceResPlay = pdevList.get(i);
            int devceid = pbui_item_deviceResPlay.getDevceid();
            Log.e("MyLog", "MeetingActivity.handleMessage 256行:  可加入设备ID： --->>> " + devceid);
            int i1 = devceid & Macro.DEVICE_MEET_DB;
            int i2 = devceid & Macro.DEVICE_MEET_SERVICE;
            int i3 = devceid & Macro.DEVICE_MEET_PROJECTIVE;
            int i4 = devceid & Macro.DEVICE_MEET_CAPTURE;
            int i5 = devceid & Macro.DEVICE_MEET_CLIENT;
            int i6 = devceid & Macro.DEVICE_MEET_ONEKEYSHARE;

            if (i1 == Macro.DEVICE_MEET_DB || i2 == Macro.DEVICE_MEET_SERVICE || i4 == Macro.DEVICE_MEET_CAPTURE
                    || i5 == Macro.DEVICE_MEET_CLIENT || i6 == Macro.DEVICE_MEET_ONEKEYSHARE) {
                Log.e("MyLog", "MeetingActivity.handleMessage 268行:  其它类型的设备 --->>> ");
            } else if (i3 == Macro.DEVICE_MEET_PROJECTIVE) {
                // 投影机
                checkedJoinProjector.add(false);
                peojectorJoin.add(pbui_item_deviceResPlay);
                Log.e("MyLog", "MeetingActivity.handleMessage 271行:  有一个投影机 --->>> ");
            } else {
                //参会人
                checkedJoinMember.add(false);
                memberJoin.add(pbui_item_deviceResPlay);
                int memberid = pbui_item_deviceResPlay.getMemberid();
                String name = MyUtils.getBts(pbui_item_deviceResPlay.getName());
                Log.e("MyLog", "MeetingActivity.handleMessage 276行:  名称 --->>> " + name + "  参会人ID：" + memberid);
            }
        }
        if (memberJoin.size() > 0) {
            joinMemberAdapter = new JoinAdapter(memberJoin);
        }
        if (peojectorJoin.size() > 0) {
            joinProjectorAdapter = new JoinAdapter(peojectorJoin);
        }
    }

    //查找在线的投影机和参会人
    private void DisposeDevInfo(Message msg) {
        ArrayList devInfos = msg.getData().getParcelableArrayList("devInfos");
        if (devInfos != null) {
            InterfaceDevice.pbui_Type_DeviceDetailInfo o5 = (InterfaceDevice.pbui_Type_DeviceDetailInfo) devInfos.get(0);
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
                    // 添加所有投影机
                    allProjectors.add(deviceInfo);
                    //判断是否是在线状态
                    if (netState == 1) {
                        //说明是在线状态的投影机
                        onLineProjectors.add(deviceInfo);
                    }
                }
                if (netState == 1 && memberInfos != null) {
                    for (int j = 0; j < memberInfos.size(); j++) {
                        MemberInfo memberInfo = memberInfos.get(j);
                        int personid = memberInfo.getPersonid();
                        if (personid == memberId) {
                            //过滤掉自己的设备
                            if (devId != DevMeetInfo.getDeviceid()) {
                                //查找到在线状态的参会人员
                                onLineMembers.add(new DevMember(memberInfos.get(j), devId));
                            }
                        }
                    }
                }
            }
            //初始化投影机是否选中集合
            checkProAll = new ArrayList<>();
            //初始化 全部设为false
            for (int k = 0; k < allProjectors.size(); k++) {
                checkProAll.add(false);
            }
            //初始化同屏控制是否选中集合
            checks = new ArrayList<>();
            //初始化 全部设为false
            for (int k = 0; k < onLineMembers.size(); k++) {
                checks.add(false);
            }
            //初始化同屏控制投影机选择集合
            checkProOL = new ArrayList<>();
            for (int i = 0; i < onLineProjectors.size(); i++) {
                checkProOL.add(false);
            }
            allProjectorAdapter = new OnLineProjectorAdapter(allProjectors, 1);
            onLineProjectorAdapter = new OnLineProjectorAdapter(onLineProjectors, 0);
            onLineMemberAdapter = new ScreenControlAdapter(onLineMembers);
        }
    }

    //获得会议功能
    private void DisposeMeetFun(Message msg) {
        ArrayList queryMeetFunction = msg.getData().getParcelableArrayList("queryMeetFunction");
        InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo o7 = (InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo) queryMeetFunction.get(0);
        List<InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo> itemList4 = o7.getItemList();
        mFunIndexs = new ArrayList<>();
        mImages = new ArrayList<>();
        for (int i = 0; i < itemList4.size(); i++) {
            InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo function = itemList4.get(i);
            int funcode = function.getFuncode();
            int position = function.getPosition();
            Log.e("MyLog", "MeetingActivity.handleMessage 300行:   --->>>  funcode： " + funcode + "   position ： " + position);
            if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_AGENDA_BULLETIN.getNumber()) {
                AgendaIV = new ImageView(context, null, R.style.MeetBottomImageView);
                AgendaIV.setBackground(resources.getDrawable(R.drawable.selector_announce_agenda));
                fragment_layout.addView(AgendaIV);
                mImages.add(AgendaIV);
                mFunIndexs.add(0);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MATERIAL.getNumber()) {
                mFunIndexs.add(1);
                MeetFileIV = new ImageView(context, null, R.style.MeetBottomImageView);
                MeetFileIV.setBackground(resources.getDrawable(R.drawable.selector_meetingfile));
                fragment_layout.addView(MeetFileIV);
                mImages.add(MeetFileIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SHAREDFILE.getNumber()) {
                mFunIndexs.add(2);
                ShareFileIV = new ImageView(context, null, R.style.MeetBottomImageView);
                ShareFileIV.setBackground(resources.getDrawable(R.drawable.selector_sharedfile));
                fragment_layout.addView(ShareFileIV);
                mImages.add(ShareFileIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_POSTIL.getNumber()) {
//                            mFunIndexs.add(new NotationFragment());
                mFunIndexs.add(3);
                PostilIV = new ImageView(context, null, R.style.MeetBottomImageView);
                PostilIV.setBackground(resources.getDrawable(R.drawable.selector_postil));
                fragment_layout.addView(PostilIV);
                mImages.add(PostilIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_MESSAGE.getNumber()) {
                mFunIndexs.add(4);
                MeetChatIV = new ImageView(context, null, R.style.MeetBottomImageView);
                MeetChatIV.setBackground(resources.getDrawable(R.drawable.selector_chat));
                fragment_layout.addView(MeetChatIV);
                mImages.add(MeetChatIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VIDEOSTREAM.getNumber()) {
                mFunIndexs.add(5);
                VideoIV = new ImageView(context, null, R.style.MeetBottomImageView);
                VideoIV.setBackground(resources.getDrawable(R.drawable.selector_video));
                fragment_layout.addView(VideoIV);
                mImages.add(VideoIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WHITEBOARD.getNumber()) {
                WhiteBoardIV = new ImageView(context, null, R.style.MeetBottomImageView);
                WhiteBoardIV.setBackground(resources.getDrawable(R.drawable.selector_whiteboard));
                fragment_layout.addView(WhiteBoardIV);
                mFunIndexs.add(6);
                mImages.add(WhiteBoardIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_WEBBROWSER.getNumber()) {
                mFunIndexs.add(7);
                WebIV = new ImageView(context, null, R.style.MeetBottomImageView);
                WebIV.setBackground(resources.getDrawable(R.drawable.selector_web));
                fragment_layout.addView(WebIV);
                mImages.add(WebIV);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_VOTERESULT.getNumber()) {
                VoteIV = new ImageView(context, null, R.style.MeetBottomImageView);
                VoteIV.setBackground(resources.getDrawable(R.drawable.selector_vote));
                fragment_layout.addView(VoteIV);
                mImages.add(VoteIV);
                mFunIndexs.add(8);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_SIGNINRESULT.getNumber()) {
                SigninIV = new ImageView(context, null, R.style.MeetBottomImageView);
                SigninIV.setBackground(resources.getDrawable(R.drawable.selector_signin));
                fragment_layout.addView(SigninIV);
                mImages.add(SigninIV);
                mFunIndexs.add(9);
            } else if (funcode == InterfaceMacro.Pb_Meet_FunctionCode.Pb_MEET_FUNCODE_DOCUMENT.getNumber()) {
                DocumentIV = new ImageView(context, null, R.style.MeetBottomImageView);
                DocumentIV.setBackground(resources.getDrawable(R.drawable.selector_document));
                fragment_layout.addView(DocumentIV);
                mImages.add(DocumentIV);
                mFunIndexs.add(10);
            }
        }
        if (mImages.size() > 0 && mFunIndexs.size() > 0) {
            //设置默认点击第一项
            showFragment(mFunIndexs.get(0));
            setImgSelect(0);
            initImages(mImages, mFunIndexs);
        }
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_MEET_BYID://129.查询指定ID的会议
                MyUtils.handTo(IDivMessage.QUERY_MEET_BYID, (InterfaceMeet.pbui_Type_MeetMeetInfo) result, "queryMeetById", mHandler);
                break;
            case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                MyUtils.handTo(IDivMessage.QUERY_ATTENDEE, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "queryMember", mHandler);
                break;
            case IDivMessage.QUERY_Attendee_Property://96.查询参会人员属性
                MyUtils.handTo(IDivMessage.QUERY_Attendee_Property, (InterfaceMember.pbui_Type_MeetMembeProperty) result, "queryAttendeeProperty", mHandler);
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                MyUtils.handTo(IDivMessage.QUERY_DEVICE_INFO, (InterfaceDevice.pbui_Type_DeviceDetailInfo) result, "devInfos", mHandler);
                break;
            case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                MyUtils.handTo(IDivMessage.Query_MeetSeat_Inform, (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) result, "queryMeetRanking", mHandler);
                break;
            case IDivMessage.QUERY_CAN_JOIN:// 查询可加入的同屏会话
                MyUtils.handTo(IDivMessage.QUERY_CAN_JOIN, (InterfaceDevice.pbui_Type_DeviceResPlay) result, "queryCanJoin", mHandler);
                break;
            case IDivMessage.QUERY_MEET_FUNCTION://查询会议功能
                MyUtils.handTo(IDivMessage.QUERY_MEET_FUNCTION, (InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo) result, "queryMeetFunction", mHandler);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        //获取到之前写得会议笔记存档
        mNoteCentent = Export.readText(new File("/sdcard/会议笔记存档.txt"));
        initController();
        context = this;
        resources = context.getResources();
        initView();
        mFm = getSupportFragmentManager();
        //8.修改本机界面状态
        nativeUtil.setInterfaceState(InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber());
        try {
            //238.查询会议功能
            nativeUtil.queryMeetFunction();
            //92.查询参会人员
            nativeUtil.queryAttendPeople();
            //181.查询会议排位
            nativeUtil.queryMeetRanking();
            //6.查询设备信息
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        getIntentBundle();
        //注册EventBus
        EventBus.getDefault().register(this);
        initScreenParam();
    }

    /**
     * 获取从MainActivity中传递过来的参数
     */
    private void getIntentBundle() {
        if (DevMeetInfo != null) {
            mMeetingCompanyName.setText(MyUtils.getBts(DevMeetInfo.getCompany()));
            mMeetingTheme.setText(MyUtils.getBts(DevMeetInfo.getMeetingname()));
            mAttendee.setText(MyUtils.getBts(DevMeetInfo.getMembername()));
        }
        if (mComperInfo != null) {
            mCompere.setText(MyUtils.getBts(mComperInfo.getName()));
        }
        try {
            //** ************ ******  125.查询符合要求的设备ID  （投影机）  ****** ************ **//
            // TODO: 2018/2/27 回调在签到页面进行的处理
            nativeUtil.queryRequestDeviceId(DevMeetInfo.getRoomid(),
                    Macro.DEVICE_MEET_PROJECTIVE,
//                    InterfaceMacro.Pb_RoomDeviceFilterFlag.Pb_MEET_ROOMDEVICE_FLAG_PROJECTIVE.getNumber(),
                    InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber(), 0);
            //96.查询参会人员属性
            nativeUtil.queryAttendPeopleProperties(InterfaceMacro.Pb_MemberPropertyID.Pb_MEETMEMBER_PROPERTY_JOB.getNumber(), DevMeetInfo.getMemberid());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    //获取当前的设备ID
    public static int getDevId() {
        return DevMeetInfo.getDeviceid();
    }

    //获取当前设备的人员ID
    public static int getMemberId() {
        return DevMeetInfo.getMemberid();
    }

    //获取当前的会议名称
    public static String getMeetName() {
        return MyUtils.getBts(DevMeetInfo.getMeetingname());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.UpDate_BadgeNumber:
                List<EventBadge> object3 = (List<EventBadge>) message.getObject();
                if (object3 != null) {
                    Log.e("MyLog", "MeetingActivity.getEventMessage 459行:  222 --->>> " + object3.get(0).getNum());
                    mBadge.setBadgeNumber(object3.get(0).getNum());
                }
                break;
            case IDEventMessage.MEET_DATE:
                //更新时间UI
                updataTimeUi(message);
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM:// 参会人员变更通知
                InterfaceBase.pbui_MeetNotifyMsg object2 = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                int id1 = object2.getId();
                int opermethod1 = object2.getOpermethod();
                Log.e("MyLog", "MeetingActivity.getEventMessage:  参会人员变更通知 EventBus --->>> id:  " + id1 + "  opermethod1：  " + opermethod1);
                //92.查询参会人员
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.MeetSeat_Change_Inform:
                Log.e("MyLog", "MeetingActivity.getEventMessage:  180.会议排位变更通知 EventBus --->>> ");
                InterfaceBase.pbui_MeetNotifyMsg object = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                /** ************ ******  181.查询会议排位  ****** ************ **/
                nativeUtil.queryMeetRanking();
                break;
            case IDEventMessage.PLACE_DEVINFO_CHANGEINFORM:
                //119 会场设备信息变更通知(投影机)
                Log.e("MyLog", "MeetingActivity.getEventMessage:  查询投影机ID EventBus --->>> ");
                /** ************ ******  125.查询符合要求的设备ID  （投影机）  ****** ************ **/
                // TODO: 2018/2/27 回调在签到页面做的处理
                nativeUtil.queryRequestDeviceId(DevMeetInfo.getRoomid(),
                        Macro.DEVICE_MEET_PROJECTIVE,
//                        InterfaceMacro.Pb_RoomDeviceFilterFlag.Pb_MEET_ROOMDEVICE_FLAG_PROJECTIVE.getNumber(),
                        InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber(), 0);
                break;
            case IDEventMessage.START_COLLECTION_STREAM_NOTIFY:
                Log.e("MyLog","MeetingActivity.getEventMessage 538行:  1111111 --->>> ");
                switch (message.getType()) {
                    case 2://屏幕
                        if (stopRecord()) {
                            Toast.makeText(this, "屏幕录制已停止", Toast.LENGTH_LONG).show();
                        } else {
                            // 1: 拿到 MediaProjectionManager 实例
                            manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                            // 2: 发起屏幕捕捉请求
                            Intent intent = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                intent = manager.createScreenCaptureIntent();
                            }
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                        break;
                    case 3://摄像头
                        startActivity(new Intent(MeetingActivity.this, CameraDemo.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                }
                break;
            case IDEventMessage.STOP_COLLECTION_STREAM_NOTIFY:
                Log.e("MyLog", "MeetingActivity.getEventMessage 587行:  22222222 --->>> ");
                switch (message.getType()) {
                    case 2:
                        if (stopRecord()) {
                            Toast.makeText(this, "屏幕录制已停止", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "屏幕录制停止失败", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                break;
            case IDEventMessage.MEET_FUNCTION_CHANGEINFO:
                /** **** **  238.查询会议功能  ** **** **/
                nativeUtil.queryMeetFunction();
                break;
            case IDEventMessage.DOWN_FINISH://下载完成通知
                InterfaceDownload.pbui_Type_DownloadCb object1 = (InterfaceDownload.pbui_Type_DownloadCb) message.getObject();
                int mediaid = object1.getMediaid();//下载完成的媒体ID
                Log.e("MyLog", "MeetingActivity.getEventMessage 577行:  下载完成的媒体ID --->>> ");
                Toast.makeText(context, "下载完成！", Toast.LENGTH_SHORT).show();
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
        Log.e("MyLog", "MeetingActivity.onDestroy 548行:   --->>> ");
        //将会议笔记的文本保存到本地
        Export.ToNoteText(mNoteCentent, "会议笔记存档");
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

    private void initImages(List<ImageView> imgs, final List<Integer> mf) {
        for (int i = 0; i < imgs.size(); i++) {
            ImageView imageView = imgs.get(i);
            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setImgSelect(finalI);
                    for (int j = 0; j < mf.size(); j++) {
                        if (finalI == j) {
                            showFragment(mf.get(j));
                            if (mf.get(j) == 6) {//电子白板
                                startActivity(new Intent(MeetingActivity.this, PeletteActivity.class));
                            }
                        }
                    }
                }
            });
        }
    }

    private void setImgSelect(int index) {
        for (int i = 0; i < mImages.size(); i++) {
            ImageView imageView = mImages.get(i);
            if (i == index) {
                imageView.setSelected(true);
            } else {
                imageView.setSelected(false);
            }
        }
    }

    public void showFragment(int index) {
        FragmentTransaction ft = mFm.beginTransaction();
        hideFragment(ft);
        switch (index) {
            case Macro.Pb_MEET_FUNCODE_SIGNINRESULT:
                if (mSigninFragment == null) {
                    mSigninFragment = new SigninFragment();
                    ft.add(R.id.right_layout, mSigninFragment);
                }
                ft.show(mSigninFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_AGENDA_BULLETIN:
                if (mAnnAgendaFragment == null) {
                    mAnnAgendaFragment = new AnnAgendaFragment();
                    ft.add(R.id.right_layout, mAnnAgendaFragment);
                }
                ft.show(mAnnAgendaFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_MATERIAL:
                if (mMeetingFileFragment == null) {
                    mMeetingFileFragment = new MeetingFileFragment();
                    ft.add(R.id.right_layout, mMeetingFileFragment);
                }
                ft.show(mMeetingFileFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_SHAREDFILE:
                if (mSharedFileFragment == null) {
                    mSharedFileFragment = new SharedFileFragment();
                    ft.add(R.id.right_layout, mSharedFileFragment);
                }
                ft.show(mSharedFileFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_MESSAGE:
                if (mChatFragment == null) {
                    mChatFragment = new ChatFragment();
                    ft.add(R.id.right_layout, mChatFragment);
                }
                ft.show(mChatFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_VIDEOSTREAM:
                if (mVideoFragment == null) {
                    mVideoFragment = new VideoFragment();
                    ft.add(R.id.right_layout, mVideoFragment);
                }
                ft.show(mVideoFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_VOTERESULT:
                if (mVoteFragment == null) {
                    mVoteFragment = new VoteFragment();
                    ft.add(R.id.right_layout, mVoteFragment);
                }
                ft.show(mVoteFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_POSTIL:
                if (mNotationFragment == null) {
                    mNotationFragment = new NotationFragment();
                    ft.add(R.id.right_layout, mNotationFragment);
                }
                ft.show(mNotationFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_WEBBROWSER:
                if (mWebbrowseFragment == null) {
                    mWebbrowseFragment = new WebBrowseFragment();
                    ft.add(R.id.right_layout, mWebbrowseFragment);
                }
                ft.show(mWebbrowseFragment);
                break;
            case Macro.Pb_MEET_FUNCODE_DOCUMENT:
                if (mOverViewDocFragment == null) {
                    mOverViewDocFragment = new OverviewDocFragment();
                    ft.add(R.id.right_layout, mOverViewDocFragment);
                }
                ft.show(mOverViewDocFragment);
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
        if (mWebbrowseFragment != null) {
            ft.hide(mWebbrowseFragment);
        }
        if (mOverViewDocFragment != null) {
            ft.hide(mOverViewDocFragment);
        }

    }

    private void initView() {
        mMeetingCompanyName = (TextView) findViewById(R.id.meeting_company_name);
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
                //只需要空实现，就可以拖拽消除未读消息
            }
        });
        mMeetingChatOnline = (ImageButton) findViewById(R.id.meeting_chat_online);
        mMeetingNowTime = (TextView) findViewById(R.id.meeting_now_time);
        mMeetingNowDate = (TextView) findViewById(R.id.meeting_now_date);
        mMeetingNowWeek = (TextView) findViewById(R.id.meeting_now_week);
        mAttendee = (TextView) findViewById(R.id.meeting_participate_name);
        mCompere = (TextView) findViewById(R.id.meeting_host_name);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        fragment_layout = (LinearLayout) findViewById(R.id.fragment_layout);
        mMeetingMessage.setOnClickListener(this);
        mMeetingChatOnline.setOnClickListener(this);
        mFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_message:
                showFragment(4);
                MeetChatIV.setSelected(true);
                break;
            case R.id.meeting_chat_online:
                // TODO: 2018/2/7 打开发送服务选项
                showSendFunctionMsg();
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
        mFabPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置动画
        mFabPop.setAnimationStyle(R.style.AnimHorizontal);
        mFabPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mFabPop.setTouchable(true);
        mFabPop.setOutsideTouchable(true);
        FabViewHolder holder = new FabViewHolder(popupView);
        Fab_Event(holder);
        mFabPop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
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
                setAnimator(holder.keyboard);
            }
        });
        //手写
        holder.handwritten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.handwritten);
            }
        });
        //桌面截图
        holder.screens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.screens);
                mFabPop.dismiss();
                Intent intent = new Intent(MeetingActivity.this, PostilActivity.class);
                //截图并将得到的bitmap转为byte数组传递给批注页面
                intent.putExtra("screenshot", FileUtil.Bitmap2bytes(ScreenUtils.snapShotWithStatusBar(context)));
                startActivity(intent);
                //截图预览
//                MyUtils.previewPic(context,ScreenUtils.snapShotWithStatusBar(context),findViewById(R.id.meeting_layout_id));
            }
        });
        //同屏控制
        holder.oneScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.oneScreen);
                mFabPop.dismiss();
                //打开同屏控制弹出框
                // TODO: 2018/2/6 打开同屏控制
                showScreensPop();
            }
        });
        //返回
        holder.backPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.backPop);
                mFabPop.dismiss();
            }
        });
        //投影控制
        holder.projection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimator(holder.projection);
                showProjector();

            }
        });
        //会议笔记
        holder.note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                // TODO: 2018/3/10 需要调用打开白板进行通知
                startActivity(new Intent(MeetingActivity.this, PeletteActivity.class));
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
        mNotePop.setFocusable(true);
        mNotePop.setOutsideTouchable(true);
//        setBackgroundAlpha(context, 0.5f);//设置背景半透明
        final NoteViewHolder holder = new NoteViewHolder(popupView);
        NoteHolderEvent(holder);
        //当弹出框隐藏时就会调用
        mNotePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mNoteCentent = holder.edtNote.getText().toString();
                setBackgroundAlpha(context, 1.0f);//恢复背景透明度
            }
        });
        mNotePop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }


    /**
     * 会议笔记 pop事件监听
     *
     * @param holder
     */
    private void NoteHolderEvent(final NoteViewHolder holder) {
        holder.edtNote.setBackgroundColor(Color.WHITE);
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
                showFileNamePop(mNoteCentent);
                mNotePop.dismiss();
            }
        });
        //清空
        holder.empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNoteCentent = "";
                holder.edtNote.setText(mNoteCentent);
            }
        });
        //导入
        holder.noteImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.showTxtDialog(MeetingActivity.this, MyUtils.getSDPostfixFile(".txt"), holder.edtNote);
            }
        });
    }


    /**
     * 展示输入要保存笔记的文件名称
     * 导出成txt文件
     *
     * @param mNoteCentent 文本内容
     */
    private void showFileNamePop(final String mNoteCentent) {
        //用来输入文件名
        final EditText edt = new EditText(MeetingActivity.this);
        edt.setText("会议笔记");
        new AlertDialog.Builder(MeetingActivity.this).setTitle("请输入保存的文件名称")
                .setView(edt).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String string = edt.getText().toString();
                Export.ToNoteText(mNoteCentent, string);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
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
     * @param holder
     */
    private void Pro_Event(final ProViewHolder holder) {
        //所有投影机
        holder.all_pro_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // TODO: 2018/2/6 获取全部的投影机
                    for (int i = 0; i < checkProAll.size(); i++) {
                        checkProAll.set(i, true);
                    }
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
                    applyProjectionIds = new ArrayList<Integer>();
                    List<DeviceInfo> checkedIds = allProjectorAdapter.getCheckedIds(1);
                    //**//
                    for (int i = 0; i < checkedIds.size(); i++) {
                        applyProjectionIds.add(checkedIds.get(i).getDevId());
                    }

                    informDev = new ArrayList<Integer>();
//                    applyProjectionIds.add(0);
                    informDev.add(0x1080004);
                    /** ************ ******  流播放  ****** ************ **/
                    nativeUtil.streamPlay(DevMeetInfo.getDeviceid(), 3, 0, applyProjectionIds, informDev);
                }
            }
        });
        //结束投影
        holder.stop_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (applyProjectionIds != null && informDev != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    nativeUtil.stopResourceOperate(applyProjectionIds, informDev);
                }
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
                checkProAll.set(posion, selected);
                holder.pro_all_cb.setChecked(!checkProAll.contains(false));
                allProjectorAdapter.notifyDataSetChanged();
            }
        });
        // 全选按钮监听
        holder.pro_all_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //如果包含false
                    for (int i = 0; i < allProjectors.size(); i++) {
                        if (!checkProAll.get(i)) {
                            //将包含false的选项设为true
                            checkProAll.set(i, true);
                        }
                    }
                    // TODO: 2018/2/6 获取全部的数据
                } else {
                    //全选为false
                    if (!checkProAll.contains(false)) {
                        //不包含false 就是全部为true的状态
                        for (int i = 0; i < allProjectors.size(); i++) {
                            //全部都设为false
                            checkProAll.set(i, false);
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
                    List<DeviceInfo> checkedIds = allProjectorAdapter.getCheckedIds(1);
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
        mScreenPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mScreenPop);
        mScreenPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mScreenPop.setTouchable(true);
        mScreenPop.setOutsideTouchable(true);
        ScreenViewHolder holder = new ScreenViewHolder(popupView);
        Screen_Event(holder);
        mScreenPop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
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
                    for (int i = 0; i < checkProOL.size(); i++) {
                        checkProOL.set(i, true);
                    }
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
                sameMemberDevRrsIds = new ArrayList<Integer>();
                sameMemberDevIds = new ArrayList<Integer>();
//                if (onLineMemberAdapter != null && onLineProjectorAdapter != null) {
//                    List<DevMember> checkedIds = onLineMemberAdapter.getCheckedIds();
//                    for (int i = 0; i < checkedIds.size(); i++) {
//                        sameMemberDevIds.add(checkedIds.get(i).getDevId());
//                    }
//                    List<DeviceInfo> checkedIds1 = onLineProjectorAdapter.getCheckedIds(0);
//                    for (int i = 0; i < checkedIds1.size(); i++) {
//                        sameMemberDevRrsIds.add(checkedIds1.get(i).getDevId());
//                    }
//                }
                sameMemberDevRrsIds.add(0);
                sameMemberDevIds.add(0x1100003);//要播放的屏幕源  要同屏的人员
                /** ************ ******  流播放  ******0x1080004 ************ **/
                nativeUtil.streamPlay(DevMeetInfo.getDeviceid(), 2, 0,
                        sameMemberDevRrsIds, sameMemberDevIds);
                Toast.makeText(MeetingActivity.this, "同屏控制", Toast.LENGTH_SHORT).show();
            }
        });
        //停止同屏按钮
        holder.stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sameMemberDevIds != null && sameMemberDevRrsIds != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    nativeUtil.stopResourceOperate(sameMemberDevRrsIds, sameMemberDevIds);
                }
            }
        });
        //加入同屏按钮
        holder.join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showJoinPop();
            }
        });

        holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScreenPop.dismiss();
            }
        });

    }

    // 展示可观看的屏幕
    private void showJoinPop() {
        View popupView = getLayoutInflater().inflate(R.layout.pop_join, null);
        joinWatchPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(joinWatchPop);
        joinWatchPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        joinWatchPop.setTouchable(true);
        joinWatchPop.setOutsideTouchable(true);
        try {
            /** ************ ******  查询可加入的同屏会话  ****** ************ **/
            nativeUtil.queryCanJoin();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        JoinViewHolder holder = new JoinViewHolder(popupView);
        Join_Event(holder);
        joinWatchPop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    // 加入同屏事件监听
    private void Join_Event(JoinViewHolder holder) {
        // item 点击事件
        if (joinMemberAdapter != null) {
            holder.join_member_screen.setAdapter(joinMemberAdapter);
            joinMemberAdapter.setItemListener(new ItemClickListener() {
                @Override
                public void onItemClick(View view, int posion) {
                    Button player = view.findViewById(R.id.palyer_name);
                    boolean selected = !player.isSelected();
                    player.setSelected(selected);
                    checkedJoinMember.set(posion, selected);
                    joinMemberAdapter.notifyDataSetChanged();
                }
            });
        }
        // item 点击事件
        if (joinProjectorAdapter != null) {
            holder.join_projector_screen.setAdapter(joinProjectorAdapter);
            joinProjectorAdapter.setItemListener(new ItemClickListener() {
                @Override
                public void onItemClick(View view, int posion) {
                    Button player = view.findViewById(R.id.palyer_name);
                    boolean selected = !player.isSelected();
                    player.setSelected(selected);
                    checkedJoinProjector.set(posion, selected);
                    joinProjectorAdapter.notifyDataSetChanged();
                }
            });
        }
        // 观看
        holder.join_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        // 取消
        holder.join_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinWatchPop.dismiss();
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
        mPlayerPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        MyUtils.setPopAnimal(mPlayerPop);
        mPlayerPop.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPlayerPop.setTouchable(true);
        mPlayerPop.setOutsideTouchable(true);
        try {
            //6.查询设备信息（查找在线的投影机）
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        PlayerViewHolder holder = new PlayerViewHolder(popupView);
        Player_Event(holder);
        mPlayerPop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
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
                checkProOL.set(posion, selected);
                holder.projectorAllCb.setChecked(!checkProOL.contains(false));
                onLineProjectorAdapter.notifyDataSetChanged();
            }
        });
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
                        if (!checkProOL.get(i)) {
                            //将包含false的选项设为true
                            checkProOL.set(i, true);
                        }
                    }
                    // TODO: 2018/2/6 获取全部的数据
                } else {
                    //全选为false
                    if (!checkProOL.contains(false)) {
                        //不包含false 就是全部为true的状态
                        for (int i = 0; i < onLineProjectors.size(); i++) {
                            //全部都设为false
                            checkProOL.set(i, false);
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
                    List<DevMember> checkedIds = onLineMemberAdapter.getCheckedIds();
                    Log.e("MyLog", "MeetingActivity.onClick 1262行:  选中的人员数量： --->>> " + checkedIds.size());
                }
                if (onLineProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds1 = onLineProjectorAdapter.getCheckedIds(0);
                    Log.e("MyLog", "MeetingActivity.onClick 1266行:  选中投影机数量 --->>> " + checkedIds1.size());
                }
                mPlayerPop.dismiss();
                onLineMemberAdapter.notifyDataSetChanged();
                onLineProjectorAdapter.notifyDataSetChanged();
            }
        });
        //取消按钮
        holder.playerPopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerPop.dismiss();
            }
        });
    }

    /**
     * FloatingActionButton 控件
     */
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

    /**
     * 同屏控制 选择参会人/投影机 控件
     */
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
            this.projectorAllCb = (CheckBox) rootView.findViewById(R.id.projector_all_cb);
            this.projectorRl = (RecyclerView) rootView.findViewById(R.id.projector_rl);
            //瀑布流布局
            this.projectorRl.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
            this.projectorRl.setAdapter(onLineProjectorAdapter);
            this.playerPopEnsure = (Button) rootView.findViewById(R.id.player_pop_ensure);
            this.playerPopCancel = (Button) rootView.findViewById(R.id.player_pop_cancel);
        }

    }

    /**
     * 投影控制 选择投影机 控件
     */
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
     * 服务功能 控件
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
     * 会议笔记 控件
     */
    public static class NoteViewHolder {
        public View rootView;
        public EditText edtNote;
        public Button noteImport;
        public Button noteSave;
        public Button noteBack;
        public TextView empty;

        public NoteViewHolder(View rootView) {
            this.rootView = rootView;
            this.edtNote = (EditText) rootView.findViewById(R.id.edt_note);

            if (!("".equals(mNoteCentent))) {
                this.edtNote.setText(mNoteCentent);
            }
            this.noteImport = (Button) rootView.findViewById(R.id.note_import);
            this.empty = (TextView) rootView.findViewById(R.id.empty);
            this.noteSave = (Button) rootView.findViewById(R.id.note_save);
            this.noteBack = (Button) rootView.findViewById(R.id.note_back);
        }
    }

    /**
     * 可观看屏幕 控件
     */
    public static class JoinViewHolder {
        public View rootView;
        public RecyclerView join_member_screen;
        public RecyclerView join_projector_screen;
        public Button join_watch;
        public Button join_cancel;

        public JoinViewHolder(View rootView) {
            this.rootView = rootView;
            this.join_member_screen = (RecyclerView) rootView.findViewById(R.id.join_member_screen);
            if (joinMemberAdapter != null) {


                this.join_member_screen.setAdapter(joinMemberAdapter);
                joinMemberAdapter.setItemListener(new ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int posion) {
                        Button player = view.findViewById(R.id.palyer_name);
                        boolean selected = !player.isSelected();
                        player.setSelected(selected);
                        checkedJoinMember.set(posion, selected);
                        joinMemberAdapter.notifyDataSetChanged();
                    }
                });
            }
            this.join_member_screen.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.HORIZONTAL));

            this.join_projector_screen = (RecyclerView) rootView.findViewById(R.id.join_projector_screen);

            if (joinProjectorAdapter != null) {
                this.join_projector_screen.setAdapter(joinProjectorAdapter);
                joinProjectorAdapter.setItemListener(new ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int posion) {
                        Button player = view.findViewById(R.id.palyer_name);
                        boolean selected = !player.isSelected();
                        player.setSelected(selected);
                        checkedJoinProjector.set(posion, selected);
                        joinProjectorAdapter.notifyDataSetChanged();
                    }
                });
            }
            this.join_projector_screen.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.HORIZONTAL));
            this.join_watch = (Button) rootView.findViewById(R.id.join_watch);
            this.join_cancel = (Button) rootView.findViewById(R.id.join_cancel);
        }
    }

    /**
     * add by gowcage
     */
    private final String TAG = "MeetingActivity-->";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                projection = manager.getMediaProjection(resultCode, data);
            }

        if (projection == null) {
            Log.e(TAG, "media projection is null");
            return;
        }
        recorder = new ScreenRecorder(width, height, bitrate, dpi, projection, "");
        Log.e("MyLog", "MeetingActivity.onActivityResult 1905行:   --->>> " + width + "  " + height + "  " + bitrate + "  " + dpi);
        recorder.start();//�启动录屏线程
        Toast.makeText(this, "屏幕录制中...", Toast.LENGTH_LONG).show();
    }

    private void initScreenParam() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; // �屏幕宽度（像素）
        height = metric.heightPixels; // �屏幕高度（像素）
        Log.v(TAG, "ScreenSize: width=" + width + " height=" + height);
        if (width > 1920)
            width = 1920;
        if (height > 1080)
            height = 1080;
        W = width;
        H = height;
        Log.i(TAG, "w:" + width + "/h:" + height);
        density = metric.density; // �屏幕密度（0.75 / 1.0 / 1.5）
        dpi = metric.densityDpi; // �屏幕密度DPI（120 / 160 / 240）
        bitrate = width * height * VideoQuality;//�比特率/码率
    }

    private boolean stopRecord() {
        if (recorder != null) {
            Log.d(TAG, "quit");
            /** ************ ******  停止资源操作  ****** ************ **/
//            nativeUtil.stopResourceOperate(allRes, devIds);
            /** ************ ******  释放播放资源  ****** ************ **/
//            nativeUtil.mediaDestroy(0);
            recorder.quit();
            recorder = null;
            return true;
        } else return false;
    }
}

