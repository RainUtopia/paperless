package com.pa.paperless.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMain;
import com.mogujie.tt.protobuf.InterfaceMain2;
import com.pa.paperless.R;
import com.pa.paperless.adapter.PlayersPopupRLAdapter;
import com.pa.paperless.bean.MainDivMeetInfo;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fragment.meeting.AnnAgendaFragment;
import com.pa.paperless.fragment.meeting.BaseFragment;
import com.pa.paperless.fragment.meeting.ChatFragment;
import com.pa.paperless.fragment.meeting.MeetingFileFragment;
import com.pa.paperless.fragment.meeting.NotationFragment;
import com.pa.paperless.fragment.meeting.SigninFragment;
import com.pa.paperless.fragment.meeting.VideoFragment;
import com.pa.paperless.fragment.meeting.VoteFragment;
import com.pa.paperless.fragment.meeting.WebBrowseFragment;
import com.pa.paperless.fragment.meeting.SharedFileFragment;
import com.pa.paperless.listener.CallListener;
import com.pa.paperless.utils.ClickViewAnimationUtil;
import com.pa.paperless.utils.Dispose;
import com.pa.paperless.utils.MyUtils;
import com.wind.myapplication.NativeUtil;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class MeetingActivity extends BaseActivity implements View.OnClickListener, CallListener {


    private TextView mMeetingCompanyName;
    private TextView mMeetingTheme;
    private ImageButton mMeetingMessage;
    private ImageButton mMeetingChatOnline;
    private TextView mMeetingNowTime;
    private TextView mMeetingNowDate;
    private TextView mMeetingNowWeek;
    private PercentLinearLayout mTheNowDate;
    private TextView mAttendee;
    private TextView mCompere;
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
    private int posion;
    private static final String POSITION = "position";
    private boolean isCheckedAllPlayers = false;
    private boolean isCheckedAllProjector = false;
    private NativeUtil nativeUtil;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_BYID://129.查询指定ID的会议
                    Log.e("MyLog", "MeetingActivity.handleMessage:  11111111111 --->>> ");
                    ArrayList queryMeetById = msg.getData().getParcelableArrayList("queryMeetById");
                    InterfaceMain.pbui_Type_MeetMeetInfo o = (InterfaceMain.pbui_Type_MeetMeetInfo) queryMeetById.get(0);
                    List<InterfaceMain.pbui_Item_MeetMeetInfo> itemList = o.getItemList();
                    for (int i = 0; i < itemList.size(); i++) {
                        InterfaceMain.pbui_Item_MeetMeetInfo pbui_item_meetMeetInfo = itemList.get(i);
                        String name = new String(pbui_item_meetMeetInfo.getName().toByteArray());
                        Log.e("MyLog", "MeetingActivity.handleMessage:  指定会议的名字： --->>> " + name + "  itemList：" + itemList.size());
                    }
                    break;
                case IDivMessage.QUERY_COMMON_BYID://91.查询指定ID的参会人员
                    Log.e("MyLog", "MeetingActivity.handleMessage:  2222222 --->>> ");
                    ArrayList queryCommonById = msg.getData().getParcelableArrayList("queryAttendById");
                    InterfaceMain.pbui_Type_MemberDetailInfo o1 = (InterfaceMain.pbui_Type_MemberDetailInfo) queryCommonById.get(0);
                    List<InterfaceMain.pbui_Item_MemberDetailInfo> itemList1 = o1.getItemList();
                    for (int i = 0; i < itemList1.size(); i++) {
                        InterfaceMain.pbui_Item_MemberDetailInfo pbui_item_memberDetailInfo = itemList1.get(i);
                        String name = MyUtils.getBts(pbui_item_memberDetailInfo.getName());
                        Log.e("MyLog", "MeetingActivity.handleMessage:  指定ID的参会人员名称： --->>> " + name);
                    }
                    break;
                case IDivMessage.QUERY_ATTENDEE://查询参会人员
                    ArrayList queryMember = msg.getData().getParcelableArrayList("queryMember");
                    InterfaceMain.pbui_Type_MemberDetailInfo o2 = (InterfaceMain.pbui_Type_MemberDetailInfo) queryMember.get(0);
                    List<MemberInfo> memberInfos = Dispose.MemberInfo(o2);
                    members = new ArrayList<>();
                    for (int i = 0; i < memberInfos.size(); i++) {
                        members.add(memberInfos.get(i).getName());
                    }
//                    setString();
                    Log.e("MyLog", "MeetingActivity.handleMessage:  参会人数量： --->>> " + memberInfos.size());
                    break;
                case IDivMessage.QUERY_ATTEND_BYID:
                    ArrayList queryAttendById = msg.getData().getParcelableArrayList("queryAttendById");
                    InterfaceMain.pbui_Type_MemberDetailInfo o3 = (InterfaceMain.pbui_Type_MemberDetailInfo) queryAttendById.get(0);
                    List<InterfaceMain.pbui_Item_MemberDetailInfo> itemList2 = o3.getItemList();
                    for (int i = 0; i < itemList2.size(); i++) {
                        InterfaceMain.pbui_Item_MemberDetailInfo pbui_item_memberDetailInfo = itemList2.get(i);
                        String memberName = MyUtils.getBts(pbui_item_memberDetailInfo.getName());
                        Log.e("MyLog", "MeetingActivity.handleMessage:  参会人： --->>> " + memberName);
                    }
                    break;
                case IDivMessage.QUERY_Attendee_Property://96.查询参会人员属性
                    ArrayList queryAttendeeProperty = msg.getData().getParcelableArrayList("queryAttendeeProperty");
                    InterfaceMain.pbui_Type_MeetMembeProperty o4 = (InterfaceMain.pbui_Type_MeetMembeProperty) queryAttendeeProperty.get(0);
                    String bts = MyUtils.getBts(o4.getPropertytext());
                    int propertyval = o4.getPropertyval();
                    Log.e("MyLog", "MeetingActivity.handleMessage:  propertyval： --->>> " + propertyval + "  身份：" + bts);
                    break;
            }
        }
    };
    public static MainDivMeetInfo o;

    public List<String> setString() {
        return members;
    }

    private List<String> members;

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
        try {
            //92.查询参会人员
            nativeUtil.queryAttendPeople();
            //181.查询会议排位
            nativeUtil.queryMeetRanking();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        //2.参会人/主持人姓名
        //3.公司名称/会议名称
        //获取从MainActivity中传递过来的参数
        getIntentBundle();
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

        try {
            //91.查询指定ID的参会人员
//            nativeUtil.queryAttendPeopleFromId(memberid);

            Log.e("MyLog", "MeetingActivity.getIntentBundle:   --->>> devId： " + devId + "  memberid：" + memberid);
            /** ************ ******  96.查询参会人员属性  ****** ************ **/
            nativeUtil.queryAttendPeopleProperties(InterfaceMacro.Pb_MemberPropertyID.Pb_MEETMEMBER_PROPERTY_JOB.getNumber(), memberid);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

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
        Log.e("MyLog", "MeetingActivity.onCreate:  devid --->>> " + o.getDevId() + "  MEMBERID:" + o.getMemberid());
    }

    public static int getTitles() {
        return o.getDevId();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventMessage.DEV_REGISTER_INFORM:
                Log.e("MyLog", "MeetingActivity.getEventMessage:  设备寄存器变更通知 EventBus --->>> ");
                break;
            case IDEventMessage.MEET_DATE:
                //更新时间UI
                updataTimeUi(message);
                break;
            case IDEventMessage.FACESTATUS_CHANGE_INFORM:
                InterfaceMain.pbui_MeetDeviceMeetStatus o = (InterfaceMain.pbui_MeetDeviceMeetStatus) message.getObject();
                Log.e("MyLog", "MeetingActivity.getEventMessage: 界面状态变更通知 EventBus --->>> 会议ID：" + o.getMeetingid() +
                        " 人员ID：" + o.getMemberid() + " 设备ID：" + o.getDeviceid() + " 界面状态：" + o.getFacestatus());
                //91.查询指定ID的参会人员
                nativeUtil.queryAttendPeopleFromId(o.getMemberid());

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
//                nativeUtil.queryAttendPeople();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //屏幕旋转时记录位置
        outState.putInt(POSITION, posion);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //屏幕恢复时取出位置
        showFragment(savedInstanceState.getInt(POSITION));
        super.onRestoreInstanceState(savedInstanceState);
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
        posion = index;
        switch (index) {
            case 0:
                if (mSigninFragment == null) {
                    mSigninFragment = new SigninFragment();
                    ft.add(R.id.right_layout, mSigninFragment);
                } else {
                    ft.show(mSigninFragment);
                }
                break;
            case 1:
                if (mAnnAgendaFragment == null) {
                    mAnnAgendaFragment = new AnnAgendaFragment();
                    ft.add(R.id.right_layout, mAnnAgendaFragment);
                } else {
                    ft.show(mAnnAgendaFragment);
                }
                break;
            case 2:
                if (mMeetingFileFragment == null) {
                    mMeetingFileFragment = new MeetingFileFragment();
                    ft.add(R.id.right_layout, mMeetingFileFragment);
                } else {
                    ft.show(mMeetingFileFragment);
                }
                break;
            case 3:
                if (mSharedFileFragment == null) {
                    mSharedFileFragment = new SharedFileFragment();
                    ft.add(R.id.right_layout, mSharedFileFragment);
                } else {
                    ft.show(mSharedFileFragment);
                }
                break;
            case 5:
                if (mChatFragment == null) {
                    mChatFragment = new ChatFragment();
                    ft.add(R.id.right_layout, mChatFragment);
                } else {
                    ft.show(mChatFragment);
                }
                break;
            case 6:
                if (mVideoFragment == null) {
                    mVideoFragment = new VideoFragment();
                    ft.add(R.id.right_layout, mVideoFragment);
                } else {
                    ft.show(mVideoFragment);
                }
                break;
            case 7:
                if (mVoteFragment == null) {
                    mVoteFragment = new VoteFragment();
                    ft.add(R.id.right_layout, mVoteFragment);
                } else {
                    ft.show(mVoteFragment);
                }
                break;
            case 8:
                if (mNotationFragment == null) {
                    mNotationFragment = new NotationFragment();
                    ft.add(R.id.right_layout, mNotationFragment);
                } else {
                    ft.show(mNotationFragment);
                }
                break;
            case 9:
                if (mWebbrowseFragment == null) {
                    mWebbrowseFragment = new WebBrowseFragment();
                    ft.add(R.id.right_layout, mWebbrowseFragment);
                } else {
                    ft.show(mWebbrowseFragment);
                }
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
        mMeetingTheme = (TextView) findViewById(R.id.meeting_theme);
        mMeetingMessage = (ImageButton) findViewById(R.id.meeting_message);
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
                showFabPop();
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
     * 展示fab
     */
    public void showFabPop() {
        View popupView = getLayoutInflater().inflate(R.layout.pop_meeting, null);
        mPopupWindow = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
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
                ClickViewAnimationUtil.setAnimator(holder.keyboard);
            }
        });
        //手写
        holder.handwritten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickViewAnimationUtil.setAnimator(holder.handwritten);
            }
        });
        //桌面截图
        holder.screens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickViewAnimationUtil.setAnimator(holder.screens);
            }
        });
        //同屏录制
        holder.oneScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickViewAnimationUtil.setAnimator(holder.oneScreen);
                mPopupWindow.dismiss();
                showScreensPop();
            }
        });
        //返回
        holder.backPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickViewAnimationUtil.setAnimator(holder.backPop);
                mPopupWindow.dismiss();
            }
        });
        //投影控制
        holder.projection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickViewAnimationUtil.setAnimator(holder.projection);
            }
        });
        //会议笔记
        holder.note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickViewAnimationUtil.setAnimator(holder.note);
            }
        });
        //呼叫服务
        holder.callService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickViewAnimationUtil.setAnimator(holder.callService);
            }
        });
        //电子白板
        holder.whiteplatePop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickViewAnimationUtil.setAnimator(holder.whiteplatePop);
            }
        });

    }

    /**
     * 同屏控制
     */
    private void showScreensPop() {

        View popupView = getLayoutInflater().inflate(R.layout.pop_sscreen, null);
        mScreenPopupWindow = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
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
        // TODO: 2017/11/8 同屏控制
        //选中所有人
        holder.everyone_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.everyone_cb.setSelected(!holder.everyone_cb.isSelected());
                if (holder.everyone_cb.isSelected()) {
                    isCheckedAllPlayers = true;
                } else {
                    isCheckedAllPlayers = false;
                }
            }
        });
        //自由选择参与人
        holder.choose_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.choose_player.setSelected(!holder.choose_player.isSelected());
                showPlayerPop();
            }
        });
        //选中所有投影机
        holder.all_projector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.all_projector.setSelected(!holder.all_projector.isSelected());
                if (holder.all_projector.isSelected()) {
                    isCheckedAllProjector = true;
                } else {
                    isCheckedAllProjector = false;
                }
            }
        });
        //自由选择投影机
        holder.choose_projector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.choose_projector.setSelected(!holder.choose_projector.isSelected());
                showPlayerPop();

            }
        });
        holder.screens_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MeetingActivity.this, "同屏录制", Toast.LENGTH_SHORT).show();
            }
        });
        holder.stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MeetingActivity.this, "停止同屏", Toast.LENGTH_SHORT).show();
            }
        });
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
     * 选择参会人
     */
    private void showPlayerPop() {
        View popupView = getLayoutInflater().inflate(R.layout.player_pop, null);
        mPlayerPopupWindow = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.WRAP_CONTENT, PercentLinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPlayerPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPlayerPopupWindow.setTouchable(true);
        mPlayerPopupWindow.setOutsideTouchable(true);
        PlayerViewHolder holder = new PlayerViewHolder(popupView);
        if (isCheckedAllPlayers) {
            holder.PlayAdapter.setCheckAll(isCheckedAllPlayers);
            holder.playersAllCb.setSelected(true);
            holder.PlayAdapter.notifyDataSetChanged();
        } else {
            holder.PlayAdapter.setCheckAll(isCheckedAllPlayers);
            holder.playersAllCb.setSelected(false);
            holder.PlayAdapter.notifyDataSetChanged();
        }
        if (isCheckedAllProjector) {
            holder.ProAapter.setCheckAll(isCheckedAllProjector);
            holder.projectorAllCb.setSelected(true);
            holder.ProAapter.notifyDataSetChanged();
        } else {
            holder.ProAapter.setCheckAll(isCheckedAllProjector);
            holder.projectorAllCb.setSelected(false);
            holder.ProAapter.notifyDataSetChanged();
        }
        Player_Event(holder);
        mPlayerPopupWindow.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    /**
     * 选择参会人Popup 事件监听
     *
     * @param holder
     */
    private void Player_Event(final PlayerViewHolder holder) {
        //参会人全选  选择框
        holder.playersAllCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.playersAllCb.isSelected()) {
                    isCheckedAllPlayers = true;
                    holder.playersAllCb.setSelected(true);
                    holder.PlayAdapter.setCheckAll(true);
                    holder.PlayAdapter.notifyDataSetChanged();
                } else {
                    isCheckedAllPlayers = false;
                    holder.playersAllCb.setSelected(false);
                    holder.PlayAdapter.setCheckAll(false);
                    holder.PlayAdapter.notifyDataSetChanged();
                }
            }
        });
        // 投影机全选 选择框
        holder.projectorAllCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.projectorAllCb.isSelected()) {
                    isCheckedAllProjector = true;
                    holder.projectorAllCb.setSelected(true);
                    holder.ProAapter.setCheckAll(true);
                    holder.ProAapter.notifyDataSetChanged();
                } else {
                    isCheckedAllProjector = false;
                    holder.projectorAllCb.setSelected(false);
                    holder.ProAapter.setCheckAll(false);
                    holder.ProAapter.notifyDataSetChanged();
                }
            }
        });
        //确定按钮
        holder.playerPopEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.playersAllCb.isSelected()) {

                }
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

    //同屏控制
    public static class ScreenViewHolder {
        public View rootView;
        public CheckBox everyone_cb;
        public CheckBox choose_player;
        public CheckBox all_projector;
        public CheckBox choose_projector;
        public Button screens_btn;
        public Button stop_btn;
        public Button join_btn;
        public Button cancel_btn;

        public ScreenViewHolder(View rootView) {
            this.rootView = rootView;
            this.everyone_cb = (CheckBox) rootView.findViewById(R.id.everyone_cb);
            this.choose_player = (CheckBox) rootView.findViewById(R.id.players);
            this.all_projector = (CheckBox) rootView.findViewById(R.id.all_projector);
            this.choose_projector = (CheckBox) rootView.findViewById(R.id.choose_projector);
            this.screens_btn = (Button) rootView.findViewById(R.id.screens_btn);
            this.stop_btn = (Button) rootView.findViewById(R.id.stop_btn);
            this.join_btn = (Button) rootView.findViewById(R.id.join_btn);
            this.cancel_btn = (Button) rootView.findViewById(R.id.cancel_btn);
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
        private List<String> playerDatas;

        public PlayersPopupRLAdapter PlayAdapter;
        public PlayersPopupRLAdapter ProAapter;

        public PlayerViewHolder(final View rootView) {
            this.rootView = rootView;
            this.playersAllCb = (CheckBox) rootView.findViewById(R.id.players_all_cb);
            // TODO: 2018/1/30 展示参会人
//            playerDatas = new ArrayList<>();
//            for (int i = 1; i <= 18; i++) {
//                if (i % 2 == 0) {
//                    playerDatas.add("" + i);
//                } else {
//                    playerDatas.add("瀑布流" + i);
//                }
//            }
            List<String> strings = setString();
            Log.e("MyLog", "PlayerViewHolder.PlayerViewHolder:  members --->>> " + strings.size());
            PlayAdapter = new PlayersPopupRLAdapter(strings);
            this.playersRl = (RecyclerView) rootView.findViewById(R.id.players_rl);
            //瀑布流布局
            this.playersRl.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.HORIZONTAL));
            this.playersRl.setAdapter(PlayAdapter);

            /**
             * *************************************
             */
            this.projectorAllCb = (CheckBox) rootView.findViewById(R.id.projector_all_cb);
//            playerDatas = new ArrayList<>();
//            for (int i = 1; i <= 10; i++) {
//                playerDatas.add(i + "号投影机");
//            }
            this.projectorRl = (RecyclerView) rootView.findViewById(R.id.projector_rl);
            ProAapter = new PlayersPopupRLAdapter(strings);
            //表格布局
            this.projectorRl.setLayoutManager(new GridLayoutManager(rootView.getContext(), 2));
            this.projectorRl.setAdapter(ProAapter);
            /**
             * *************************************
             */
            this.playerPopEnsure = (Button) rootView.findViewById(R.id.player_pop_ensure);
            this.playerPopCancel = (Button) rootView.findViewById(R.id.player_pop_cancel);
        }

    }
}