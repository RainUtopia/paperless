package com.pa.paperless.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceIM;
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
import com.pa.paperless.bean.VideoInfo;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.pa.paperless.utils.MyUtils.getMediaid;
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
    private NotationFragment mNotationFragment;
    private ChatFragment mChatFragment;
    private VideoFragment mVideoFragment;
    private VoteFragment mVoteFragment;
    private WebBrowseFragment mWebbrowseFragment;
    private OverviewDocFragment mOverViewDocFragment;
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
    public static InterfaceDevice.pbui_Type_DeviceFaceShowDetail DevMeetInfo = MainActivity.getLocalInfo();//设备会议信息
    public static InterfaceMember.pbui_Item_MemberDetailInfo mComperInfo = MainActivity.getCompereInfo();//主持人信息
    private LinearLayout fragment_layout;
    private Resources resources;
    public NativeUtil nativeUtil;
    public static MeetingActivity context;


    private Handler meetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                    ArrayList queryMember = msg.getData().getParcelableArrayList("queryMember");
                    InterfaceMember.pbui_Type_MemberDetailInfo o2 = (InterfaceMember.pbui_Type_MemberDetailInfo) queryMember.get(0);
                    memberInfos = Dispose.MemberInfo(o2);
                    try {
                        //查询设备信息
                        nativeUtil.queryDeviceInfo();
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
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
                    Log.e("MyLog", "MeetingActivity.handleMessage 170行:  22222222 --->>> ");
                    DisposeMeetFun(msg);
                    break;
                case IDivMessage.QUERY_CONFORM_DEVID://125.查询符合要求的设备ID(查询投影机)
                    queryProjector(msg);
                    break;
                case IDivMessage.DEV_PROBYID: //7.按属性ID查询指定设备属性（投影机）
                    queryProjectorName(msg);
                    break;
                case IDivMessage.RECEIVE_MEET_IMINFO://收到新的会议交流信息
                    Log.e("MyLog", "MeetingActivity.handleMessage 190行:  处理消息 --->>> ");
                    ArrayList resultNewMessage = msg.getData().getParcelableArrayList("resultNewMessage");
                    InterfaceIM.pbui_Type_MeetIM newMessage = (InterfaceIM.pbui_Type_MeetIM) resultNewMessage.get(0);
                    List<ReceiveMeetIMInfo> newMsglist = Dispose.ReceiveMeetIMinfo(newMessage);
                    for (int i = 0; i < newMsglist.size(); i++) {
                        ReceiveMeetIMInfo receiveMeetIMInfo = newMsglist.get(i);
                        //设置成接收到的消息
                        receiveMeetIMInfo.setType(true);
                    }
                    break;
            }
        }
    };
    private List<DeviceInfo> deviceInfos;
    private int MSG_TYPE;
    //用来传递数据
    private ITransfer mTransfer;
    private List<VideoInfo> videoInfos;
    private boolean fromVideo = false;
    private boolean openProjector = false;
    private List<File> filesss;//存放顺序文件
    private List<File> imgs;//存放排好后的文件
    private List<ImageView> ivs;
    private boolean isLastPage = false;//批注页面 ViewPager是否滑动到最后一页
    private boolean isDragPage = false;//批注页面 手指是否是按下状态
    private boolean isFirstPage = true;//批注页面 当前是否是首页
    private PagerAdapter pagerAdapter;
    private int pagerPosition;//存放当前所在的ViewPager索引

    public void setListener(ITransfer transfer) {
        mTransfer = transfer;
    }

    public interface ITransfer {
        void to(int action, Object data);
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_MEET_BYID://129.查询指定ID的会议
                MyUtils.handTo(IDivMessage.QUERY_MEET_BYID, (InterfaceMeet.pbui_Type_MeetMeetInfo) result, "queryMeetById", meetHandler);
                break;
            case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                MyUtils.handTo(IDivMessage.QUERY_ATTENDEE, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "queryMember", meetHandler);
                break;
            case IDivMessage.QUERY_Attendee_Property://96.查询参会人员属性
                MyUtils.handTo(IDivMessage.QUERY_Attendee_Property, (InterfaceMember.pbui_Type_MeetMembeProperty) result, "queryAttendeeProperty", meetHandler);
                break;
            case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息
                MyUtils.handTo(IDivMessage.QUERY_DEVICE_INFO, (InterfaceDevice.pbui_Type_DeviceDetailInfo) result, "devInfos", meetHandler);
                break;
            case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                MyUtils.handTo(IDivMessage.Query_MeetSeat_Inform, (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) result, "queryMeetRanking", meetHandler);
                break;
            case IDivMessage.QUERY_CAN_JOIN:// 查询可加入的同屏会话
                MyUtils.handTo(IDivMessage.QUERY_CAN_JOIN, (InterfaceDevice.pbui_Type_DeviceResPlay) result, "queryCanJoin", meetHandler);
                break;
            case IDivMessage.QUERY_MEET_FUNCTION://查询会议功能
                Log.e("MyLog", "MeetingActivity.callListener 204行:  11111111 --->>> ");
                MyUtils.handTo(IDivMessage.QUERY_MEET_FUNCTION, (InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo) result, "queryMeetFunction", meetHandler);
                break;
            case IDivMessage.QUERY_CONFORM_DEVID: //125.查询符合要求的设备ID
                MyUtils.handTo(IDivMessage.QUERY_CONFORM_DEVID, (InterfaceRoom.pbui_Type_ResMeetRoomDevInfo) result, "queryProjector", meetHandler);
                break;
            case IDivMessage.DEV_PROBYID://7.按属性ID查询指定设备属性（投影机）
                MyUtils.handTo(IDivMessage.DEV_PROBYID, (byte[]) result, "queryProjectorName", meetHandler);
                break;
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                Log.e("MyLog", "SigninFragment.callListener 296行:  收到会议消息 --->>> ");
                MyUtils.receiveMessage((InterfaceIM.pbui_Type_MeetIM) result, nativeUtil);
                break;
            case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人
                MyUtils.queryName((InterfaceMember.pbui_Type_MemberDetailInfo) result);
                break;
        }
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
                /** **** **  因为EventBus在主线程，所以可以刷新界面  ** **** **/
                updataTimeUi(message);
                break;
            case IDEventMessage.MEMBER_CHANGE_INFORM:// 参会人员变更通知
                InterfaceBase.pbui_MeetNotifyMsg object2 = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                int id1 = object2.getId();
                int opermethod1 = object2.getOpermethod();
                /** ************ ******  91.查询指定ID的参会人  ****** ************ **/
                nativeUtil.queryAttendPeopleFromId(id1);
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
            case IDEventMessage.START_COLLECTION_STREAM_NOTIFY://收集流
                enentCollectionStream(message);
                break;
            case IDEventMessage.STOP_COLLECTION_STREAM_NOTIFY:
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
            case IDEventMessage.MEET_FUNCTION_CHANGEINFO://会议功能变更通知
                /** **** **  238.查询会议功能  ** **** **/
                Log.e("MyLog", "MeetingActivity.getEventMessage 586行:  会议功能变更通知EventBus --->>> ");
                initController();
                //查询会议功能
                nativeUtil.queryMeetFunction();
                break;
            case IDEventMessage.DOWN_FINISH://67 下载进度回调
                enentDownFinish(message);
                break;
            case IDEventMessage.OPEN_BOARD://收到白板打开操作
                eventOpenBoard(message);
                break;
            case IDEventMessage.DEV_REGISTER_INFORM://设备寄存器变更通知（监听参会人退出）
                Log.i(TAG, "com.pa.paperless.activity_MeetingActivity.getEventMessage :  设备寄存器变更通知EventBus --->>> ");
//                initController();
                nativeUtil.setCallListener(this);
                //查询参会人信息
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.FACESTATUS_CHANGE_INFORM://界面状态变更通知
                Log.i(TAG, "com.pa.paperless.activity_MeetingActivity.getEventMessage :  界面状态变更通知EventBus --->>> ");
//                initController();
                nativeUtil.setCallListener(this);
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.open_screenspop://从视屏直播页面收到打开同屏控制
                fromVideo = true;
                videoInfos = (List<VideoInfo>) message.getObject();
                showScreensPop();
                break;
            case IDEventMessage.open_projector://从视屏直播页面收到打开投影控制
                openProjector = true;
                videoInfos = (List<VideoInfo>) message.getObject();
                showProjector();
                break;
        }
    }


    private void enentCollectionStream(EventMessage message) {
        switch (message.getType()) {
            case 2://屏幕
                if (stopRecord()) {
                    Toast.makeText(this, "屏幕录制已停止", Toast.LENGTH_LONG).show();
                } else {
                    // 1: 拿到 MediaProjectionManager 实例
                    manager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                    // 2: 发起屏幕捕捉请求
                    Intent intent = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        intent = manager.createScreenCaptureIntent();
                    }
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
            case 3://摄像头
                startActivity(new Intent(MeetingActivity.this, CameraDemo.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }

    private void enentDownFinish(EventMessage message) throws InvalidProtocolBufferException {
        InterfaceDownload.pbui_Type_DownloadCb object5 = (InterfaceDownload.pbui_Type_DownloadCb) message.getObject();
        int mediaid = object5.getMediaid();
        byte[] bytes = nativeUtil.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber()
                , mediaid);
        InterfaceBase.pbui_CommonTextProperty pbui_commonTextProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
        final String bts = MyUtils.getBts(pbui_commonTextProperty.getPropertyval());
        Log.e("MyLog", "MeetingActivity.getEventMessage 315行:   查到的文件名 --->>> " + bts);
        Snackbar.make(context.getCurrentFocus(), bts + "  下载成功，是否打开？", Snackbar.LENGTH_LONG)
                .setAction("打开查看", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 2018/4/21 根据媒体ID打开文件
                        File file = new File(Macro.MEETFILE + bts);
                        MyUtils.OpenThisFile(context, file);
                    }
                }).show();
    }

    private void eventOpenBoard(EventMessage message) {
        InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard object4 = (InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard) message.getObject();
        Log.e("MyLog", "PeletteActivity.receiveOpenWhiteBoard 612行:   --->>> 收到白板打开操作EventBus");
        int operflag = object4.getOperflag();
        ByteString medianame = object4.getMedianame();
        int opermemberid = object4.getOpermemberid();
        int srcmemid = object4.getSrcmemid();
        long srcwbidd = object4.getSrcwbid();
        if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_FORCEOPEN.getNumber()) {
            Log.e("MyLog", "PeletteActivity.receiveOpenWhiteBoard 619行:   --->>> 这是强制式打开白板");
            //强制打开白板  直接强制同意加入
            nativeUtil.agreeJoin(opermemberid, srcmemid, srcwbidd);
            startActivity(new Intent(MeetingActivity.this, PeletteActivity.class));
            PeletteActivity.isSharing = true;//如果同意加入就设置已经在共享中
            PeletteActivity.launchPersonId = srcmemid;//设置发起的人员ID
            PeletteActivity.mSrcwbid = srcwbidd;
        } else if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber()) {
            Log.e("MyLog", "PeletteActivity.receiveOpenWhiteBoard 625行:   --->>> 这是询问式打开白板");
            //询问打开白板
            WhetherOpen(opermemberid, srcmemid, srcwbidd, MyUtils.getBts(medianame));
        }
    }

    private void WhetherOpen(final int opermemberid, final int srcmemid, final long srcwbidd, String medianame) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("是否同意加入 " + medianame + " 发起的共享批注？");
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //同意加入
                Log.e("MyLog", "MeetingActivity.onClick 344行:   --->>> " + opermemberid + " : " + MeetingActivity.getMemberId());
                nativeUtil.agreeJoin(MeetingActivity.getMemberId(), srcmemid, srcwbidd);
                PeletteActivity.isSharing = true;//如果同意加入就设置已经在共享中
                PeletteActivity.launchPersonId = srcmemid;//设置发起的人员ID
                PeletteActivity.mSrcwbid = srcwbidd;
                Log.e("MyLog", "MeetingActivity.onClick 349行:   --->>> " + PeletteActivity.isSharing + "   " + srcmemid + "   " + srcwbidd);
                startActivity(new Intent(MeetingActivity.this, PeletteActivity.class));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nativeUtil.rejectJoin(MeetingActivity.getMemberId(), srcmemid, srcwbidd);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    //获取到投影机的名称
    private void queryProjectorName(Message msg) {
        ArrayList queryProjector1 = msg.getData().getParcelableArrayList("queryProjectorName");
        byte[] array = (byte[]) queryProjector1.get(0);
        // 根据查找的类型 查询的是名称就解析成字符串格式
        InterfaceDevice.pbui_DeviceStringProperty pbui_deviceStringProperty = InterfaceDevice.pbui_DeviceStringProperty.getDefaultInstance();
        try {
            InterfaceDevice.pbui_DeviceStringProperty pbui_deviceStringProperty1 = pbui_deviceStringProperty.parseFrom(array);
            //获取到投影机的名称
            String ProName = new String(pbui_deviceStringProperty1.getPropertytext().toByteArray());
            Log.e("MyLog", "SigninFragment.handleMessage 145行:  投影机名称 --->>> " + ProName);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    //查找投影机设备
    private void queryProjector(Message msg) {
        ArrayList queryProjector = msg.getData().getParcelableArrayList("queryProjector");
        InterfaceRoom.pbui_Type_ResMeetRoomDevInfo o5 = (InterfaceRoom.pbui_Type_ResMeetRoomDevInfo) queryProjector.get(0);
        //获得投影机的设备ID
        List<Integer> devidList = o5.getDevidList();
        for (int i = 0; i < devidList.size(); i++) {
            int number = Macro.DEVICE_MEET_PROJECTIVE;
            Integer devId = devidList.get(i);
            int i1 = devId & number;
            Log.e("MyLog", "MeetingActivity.handleMessage:  所有的设备ID： --->>> " + devId + "  相于的结果  ：" + i1);
            if (i1 == number) {
                Log.e("MyLog", "MeetingActivity.handleMessage:  该设备ID为投影机 --->>> ");
                /** ************ ******  7.按属性ID查询指定设备属性  ****** ************ **/
                // TODO: 2018/2/27 没有二次查找  等待二次查找。。。。
                nativeUtil.queryDevicePropertiesById(
                        //表示只查找名称，返回时字符串格式解析
                        InterfaceMacro.Pb_MeetDevicePropertyID.Pb_MEETDEVICE_PROPERTY_NAME.getNumber() |
                                InterfaceMacro.Pb_MeetDevicePropertyID.Pb_MEETDEVICE_PROPERTY_IPADDR.getNumber(),
                        devId, 0);
            }
        }
    }

    //设置投票页面当前设备是否是主持人
    private void DisposeIsCompere(Message msg) {
        ArrayList queryMeetRanking = msg.getData().getParcelableArrayList("queryMeetRanking");
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo o5 = (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) queryMeetRanking.get(0);
        List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> itemList3 = o5.getItemList();
        for (int i = 0; i < itemList3.size(); i++) {
            InterfaceRoom.pbui_Item_MeetSeatDetailInfo pbui_item_meetSeatDetailInfo = itemList3.get(i);
            int nameId = pbui_item_meetSeatDetailInfo.getNameId();
            int role = pbui_item_meetSeatDetailInfo.getRole();
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

    //查找投影机和在线状态的参会人
    private void DisposeDevInfo(Message msg) {
        ArrayList devInfos = msg.getData().getParcelableArrayList("devInfos");
        if (devInfos != null) {
            InterfaceDevice.pbui_Type_DeviceDetailInfo o5 = (InterfaceDevice.pbui_Type_DeviceDetailInfo) devInfos.get(0);
            deviceInfos = Dispose.DevInfo(o5);
            int number = Macro.DEVICE_MEET_PROJECTIVE;
            initData();
            Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.DisposeDevInfo :  查询得到的参会人总数 --->>> " + memberInfos.size());
            for (int i = 0; i < deviceInfos.size(); i++) {
                DeviceInfo deviceInfo = deviceInfos.get(i);
                int netState = deviceInfo.getNetState();
                int faceState = deviceInfo.getFaceState();
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
                //判断是否是在线的并且界面状态为1的参会人
                if (faceState == 1 && memberInfos != null && netState == 1) {
                    for (int j = 0; j < memberInfos.size(); j++) {
                        MemberInfo memberInfo = memberInfos.get(j);
                        int personid = memberInfo.getPersonid();
                        if (personid == memberId) {
                            /** **** **  过滤掉自己的设备  ** **** **/
                            if (devId != DevMeetInfo.getDeviceid()) {
                                //查找到在线状态的参会人员
                                onLineMembers.add(new DevMember(memberInfos.get(j), devId));
                            }
                        }
                    }
                }
            }
            Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.DisposeDevInfo :  onLineMembers.size() --->>> " + onLineMembers.size());
            initCheckedList();
            initAdapter();
            /** **** **  刷新Adapter  ** **** **/
            allProjectorAdapter.notifyDataSetChanged();
            onLineProjectorAdapter.notifyDataSetChanged();
            onLineMemberAdapter.notifyDataSetChanged();
            EventBus.getDefault().post(new EventMessage(IDEventMessage.updata_chat_onLineMember));
        }
    }

    private void initAdapter() {
        /** **** **  这里的Adapter只有是空的时候才去重新创建，因为每次查找都创建的话，notifyDataSetChanged操作将会失效  ** **** **/
        if (allProjectorAdapter == null) {
            allProjectorAdapter = new OnLineProjectorAdapter(allProjectors, 1);
        }
        if (onLineProjectorAdapter == null) {
            onLineProjectorAdapter = new OnLineProjectorAdapter(onLineProjectors, 0);
        }
        if (onLineMemberAdapter == null) {
            onLineMemberAdapter = new ScreenControlAdapter(onLineMembers);
        }
    }

    private void initCheckedList() {
        //初始化投影机是否选中集合
        checkProAll = new ArrayList<>();
        //初始化 全部设为false
        for (int k = 0; k < allProjectors.size(); k++) {
            checkProAll.add(false);
        }
        //初始化同屏控制参会人是否选中集合
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
    }

    private void initData() {
        if (onLineMembers == null) {
            onLineMembers = new ArrayList<>();
        } else {
            onLineMembers.clear();
        }
        if (onLineProjectors == null) {
            onLineProjectors = new ArrayList<>();
        } else {
            onLineProjectors.clear();
        }
        if (allProjectors == null) {
            allProjectors = new ArrayList<>();
        } else {
            allProjectors.clear();
        }
    }

    //获得会议功能
    private void DisposeMeetFun(Message msg) {
        ArrayList queryMeetFunction = msg.getData().getParcelableArrayList("queryMeetFunction");
        InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo o7 = (InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo) queryMeetFunction.get(0);
        List<InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo> itemList4 = o7.getItemList();
        if (mFunIndexs != null) {
            mFunIndexs.clear();
            fragment_layout.removeAllViews();
        } else {
            mFunIndexs = new ArrayList<>();
        }
        if (mImages != null) {
            mImages.clear();
        } else {
            mImages = new ArrayList<>();
        }
        Log.e("MyLog", "MeetingActivity.DisposeMeetFun 585行:  查到的会议功能个数 --->>> " + itemList4.size());
        for (int i = 0; i < itemList4.size(); i++) {
            InterfaceMeetfunction.pbui_Item_MeetFunConfigDetailInfo function = itemList4.get(i);
            int funcode = function.getFuncode();
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
            /** **** **  查询到会议功能才进行下一步  ** **** **/
            try {
                /** **** **  小圆点中需要实现的方法  ** **** **/
                //92.查询参会人员
                nativeUtil.queryAttendPeople();
                //6.查询设备信息
//                nativeUtil.queryDeviceInfo();
                //181.查询会议排位
                nativeUtil.queryMeetRanking();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            getIntentBundle();
            //设置默认点击第一项
            if(mFunIndexs.contains(Macro.Pb_MEET_FUNCODE_POSTIL)){
                showFragment(Macro.Pb_MEET_FUNCODE_POSTIL);
            }else {
                showFragment(0);
            }
            setImgSelect(0);
            initImages(mImages, mFunIndexs);
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
        //注册EventBus:必须在查询会议功能之前，因为查询失败是要EventBus接收的
        EventBus.getDefault().register(this);
        resources = context.getResources();
        initView();
        mFm = getSupportFragmentManager();
        //8.修改本机界面状态
        nativeUtil.setInterfaceState(InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber());
        try {
            //238.查询会议功能
            nativeUtil.queryMeetFunction();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
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

    private void updataTimeUi(EventMessage message) {
        String[] object = (String[]) message.getObject();
        mMeetingNowDate.setText(object[0]);
        mMeetingNowWeek.setText(object[1]);
        mMeetingNowTime.setText(object[2]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MyLog", "MeetingActivity.onDestroy 799行:   --->>> ");
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

    //设置功能按钮和相关点击事件
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
        MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
        // TODO: 2018/2/7 服务功能事件 发送会议交流信息
        //纸
        holder.paper_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
//                /** ************ ******  185.发送会议交流信息  ****** ************ **/
                holder.edt_msg.setText("纸巾");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Paper.getNumber();
            }
        });
        //笔
        holder.pen_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("我需要一支笔");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Pen.getNumber();
            }
        });
        //茶水
        holder.tea_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("茶");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Tea.getNumber();
            }
        });
        //矿泉水
        holder.water_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("矿泉水");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Water.getNumber();
            }
        });
        //计算器
        holder.calculator_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("计算器");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
            }
        });
        //服务员
        holder.waiter_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("服务员");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Waiter.getNumber();
            }
        });
        //清扫
        holder.sweep_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("清扫");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
            }
        });
        //技术员
        holder.technician_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("技术员");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Technical.getNumber();
            }
        });
        //发送
        holder.send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = holder.edt_msg.getText().toString();
                nativeUtil.sendMeetChatInfo(string, MSG_TYPE, arr);
                /** **** **  发送后清空输入框  ** **** **/
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
                Bitmap bitmap = ScreenUtils.snapShotWithStatusBar(context);
                /** **** **  将截图保存到文件中  ** **** **/
//                File file1 = new File(MyUtils.CreateFile(Macro.POSTILFILE) + "/" + System.currentTimeMillis() + ".jpg");
//                FileUtil.saveBitmap(bitmap, file1);
//                Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.onClick :   --->>> " + file1.getAbsolutePath());
                //打开批注页面
                byte[] bytes = FileUtil.Bitmap2bytes(bitmap);
                showPostil(bytes);
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
//                setAnimator(holder.whiteplatePop);
                // TODO: 2018/3/10 需要调用打开白板进行通知
                startActivity(new Intent(MeetingActivity.this, PeletteActivity.class));
            }
        });
    }

    /**
     * 打开批注页面
     *
     * @param bytes
     */
    private void showPostil(byte[] bytes) {
        View popupView = getLayoutInflater().inflate(R.layout.activity_postil, null);
        PopupWindow PostilPop = new PopupWindow(popupView, PercentLinearLayout.LayoutParams.MATCH_PARENT, PercentLinearLayout.LayoutParams.MATCH_PARENT, true);
        PostilPop.setAnimationStyle(R.style.AnimHorizontal);
        PostilPop.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        PostilPop.setTouchable(true);
        setBackgroundAlpha(context, 0.3f);
        PostilPop.setOutsideTouchable(true);
        PostilViewHolder holder = new PostilViewHolder(popupView);
        PostilHolderEvent(holder, bytes);
        PostilPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(context, 1.0f);
            }
        });
        PostilPop.showAtLocation(findViewById(R.id.meeting_layout_id), Gravity.CENTER, 0, 0);
    }

    private void PostilHolderEvent(PostilViewHolder holder, final byte[] bytes) {
        /** **** **  将截取屏幕的图片展示到ViewPager  ** **** **/
//        initPager(holder.postil_image);
        /** **** **  将截取的屏幕图片展示到ImageView  ** **** **/
        Glide.with(context).load(bytes).into(holder.postil_image);
        //保存本地
        holder.postil_save_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportFileNamePop(1, bytes);
            }
        });
        //保存到服务器
        holder.postil_save_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImportFileNamePop(2, bytes);
            }
        });
        //截图批注
        holder.postil_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PeletteActivity.class);
                intent.putExtra("postilpic", bytes);
                context.startActivity(intent);
            }
        });
        //发起同屏
        holder.postil_start_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** **** **  打开同屏控制pop  ** **** **/
                showScreensPop();
            }
        });
        //停止同屏
        holder.postil_stop_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sameMemberDevIds != null && sameMemberDevRrsIds != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    nativeUtil.stopResourceOperate(sameMemberDevRrsIds, sameMemberDevIds);
                }
            }
        });
        //发起投影
        holder.postil_start_projection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProjector();
            }
        });
        //停止投影
        holder.postil_stop_projection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (applyProjectionIds != null && informDev != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    nativeUtil.stopResourceOperate(informDev, applyProjectionIds);
                }
            }
        });
        //删除
        holder.postil_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                File file = imgs.get(pagerPosition);
//                file.delete();
//                initImgFilepath();
//                pagerAdapter.notifyDataSetChanged();
            }
        });
        //文件命名
        holder.postil_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                File file = imgs.get(pagerPosition);
//                Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.onClick :   --->>> " + file.getAbsolutePath() + " , " + file.getParentFile().getAbsolutePath());
//                String newName = FileUtil.EdtNewFileName(context, ".jpg");
//                File ne = new File(file.getParentFile().getAbsolutePath() + "/" + newName);
//                try {
//                    ne.createNewFile();//创建新的文件
//                    file.delete();//删除原来的
//                    initImgFilepath();
//                    pagerAdapter.notifyDataSetChanged();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    private void initPager(final ViewPager pager) {
        initImgFilepath();
        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return ivs != null ? ivs.size() : 0;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Glide.with(context).load(imgs.get(position)).into(ivs.get(position));
                container.addView(ivs.get(position));
                return ivs.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(ivs.get(position));
            }

        };
        pager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                imitateQQ(page, position);
            }
        });
//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//
//            /**
//             * 在屏幕滚动过程中不断被调用
//             * @param position
//             * @param positionOffset 是当前页面滑动比例，如果页面向右翻动，这个值不断变大，最后在趋近1的情况后突变为0。
//             *                       如果页面向左翻动，这个值不断变小，最后变为0
//             * @param positionOffsetPixels 是当前页面滑动像素，变化情况和positionOffset一致
//             */
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.onPageScrolled :  position --->>> " + position
//                        + ",positionOffsetPixels:" + positionOffsetPixels
//                        + ",isLastPage:" + isLastPage + ",isDragPage:" + isDragPage + ",ivs.size():" + ivs.size());
//                if (isLastPage && isDragPage && positionOffsetPixels == 0 && position == ivs.size() - 1) {
//                    //当前页是最后一页，并且是拖动状态，并且像素偏移量为0
//                    Toast.makeText(MeetingActivity.this, "已经是最后一页，将跳转到首页", Toast.LENGTH_SHORT).show();
//                    pager.setCurrentItem(0, false);
//                } else if (isFirstPage && isDragPage && positionOffsetPixels == 0 && position == 0) {
//                    Toast.makeText(MeetingActivity.this, "已经是首页，将跳转到最后一页", Toast.LENGTH_SHORT).show();
//                    pager.setCurrentItem(ivs.size() - 1, false);
//                }
//            }
//
//            /**
//             * 哪个页面被选中 -->当前页面
//             * @param position
//             */
//            @Override
//            public void onPageSelected(int position) {
//                pagerPosition = position;
//                isLastPage = position == ivs.size() - 1;
//                isFirstPage = position == 0;
//                Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.onPageSelected :  isLastPage --->>> "
//                        + isLastPage + "，isFirstPage:" + isFirstPage + "，当前索引：" + position
//                        + ",ivs.size():" + ivs.size());
//            }
//
//            /**
//             * 在手指操作屏幕的时候发生变化 :
//             * @param state 有三个值：0（END）,1(PRESS) , 2(UP)
//             */
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                isDragPage = state == 1;
//                Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.onPageScrollStateChanged :  state --->>> " +
//                        state + ",isDragPage:" + isDragPage + ",ivs.size():" + ivs.size());
//                // 这里主要是解决在onPageScrolled出现的闪屏问题
//                // (positionOffset为0的时候，并不一定是切换完成，所以动画还在执行，强制再次切换，就会闪屏)
//                switch (state) {
//                    case ViewPager.SCROLL_STATE_IDLE:// 0 空闲状态，没有任何滚动正在进行（表明完成滚动）
//                        break;
//                    case ViewPager.SCROLL_STATE_DRAGGING:// 1 正在拖动page状态
//                        break;
//                    case ViewPager.SCROLL_STATE_SETTLING:// 2 手指已离开屏幕，自动完成剩余的动画效果
//                        break;
//                }
//            }
//        });
        pager.setAdapter(pagerAdapter);
    }

    /**
     * 仿QQ的缩放动画效果
     */
    public void imitateQQ(View view, float position) {
        if (position >= -1 && position <= 1) {
            view.setPivotX(position > 0 ? 0 : view.getWidth() / 2);
            //view.setPivotY(view.getHeight()/2);
            view.setScaleX((float) ((1 - Math.abs(position) < 0.5) ? 0.5 : (1 - Math.abs(position))));
            view.setScaleY((float) ((1 - Math.abs(position) < 0.5) ? 0.5 : (1 - Math.abs(position))));
        }
    }


    private void initImgFilepath() {
        if (imgs == null) {
            imgs = new ArrayList<>();
        } else {
            imgs.clear();
        }
        if (filesss == null) {
            filesss = new ArrayList<>();
        } else {
            filesss.clear();
        }
        if (ivs == null) {
            ivs = new ArrayList<>();
        } else {
            ivs.clear();
        }
        File file = new File(Macro.POSTILFILE);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.initImgFilepath :  PostilFile文件夹下没有文件 --->>> ");
        } else {
            for (File f : files) {
                if (!f.isDirectory()) {
                    if (f.getName().endsWith(".jpg")) {
                        //将图片文件添加进去：这里排在索引最后的位置才是最新创建的
                        filesss.add(f);
                        ivs.add(new ImageView(context));
                    }
                }
            }
        }
        /** **** **  将排放的位置倒过来放到新的集合中  ** **** **/
        if (filesss.size() > 0) {
            for (int i = filesss.size() - 1; i >= 0; i--) {
                imgs.add(filesss.get(i));
            }
            Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.initImgFilepath :  filesss.size() --->>> " +
                    filesss.size() + " , imgs.size(): " + imgs.size());
        }
    }

    /**
     * 将该截图保存到手机或者上传到服务器
     */
    private void showImportFileNamePop(final int type, final byte[] bytes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("输入文件名：");
        final EditText edt = new EditText(context);
        edt.setText(System.currentTimeMillis() + "");
        builder.setView(edt);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bitmap bitmap = FileUtil.bytes2Bitmap(bytes);
                MyUtils.CreateFile(Macro.POSTILFILE);
                String strName = edt.getText().toString();
                if (strName.equals("")) {
                    Toast.makeText(context, "请先填写文件名", Toast.LENGTH_SHORT).show();
                } else {
                    File file = new File(Macro.POSTILFILE + strName + ".jpg");
                    try {
                        if (type == 1) {//保存到本地
                            file.createNewFile();
                            FileUtil.saveBitmap(bitmap, file);
                        } else if (type == 2) {//保存到服务器
                            if (!file.exists()) {
                                file.createNewFile();
                                FileUtil.saveBitmap(bitmap, file);
                            }
                            String path = file.getPath();
                            int mediaid = getMediaid(path);
                            String fileEnd = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase();
                            nativeUtil.uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ONLYENDCALLBACK.getNumber(),
                                    2, 0, strName + "." + fileEnd, path, 0, mediaid);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //完成操作隐藏对话框
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
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

            private int subid;
            private int srcdeviceid;

            @Override
            public void onClick(View view) {
                applyProjectionIds = new ArrayList<Integer>();
                informDev = new ArrayList<Integer>();
                if (openProjector) {
                    srcdeviceid = videoInfos.get(0).getVideoInfo().getDeviceid();
                    subid = videoInfos.get(0).getVideoInfo().getSubid();
//                    informDev.add(srcdeviceid);
                    applyProjectionIds.add(MeetingActivity.getDevId());
                } else {
                    subid = 2;
                    srcdeviceid = DevMeetInfo.getDeviceid();
                }
                if (allProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds = allProjectorAdapter.getCheckedIds(1);
                    for (int i = 0; i < checkedIds.size(); i++) {
                        applyProjectionIds.add(checkedIds.get(i).getDevId());
                    }
                    informDev.add(0);
                    applyProjectionIds.add(0);
                    /** ************ ******  流播放  0x1080004****** ************ **/
                    nativeUtil.streamPlay(srcdeviceid, subid, 0, informDev, applyProjectionIds);
                }
            }
        });
        //结束投影
        holder.stop_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (applyProjectionIds != null && informDev != null) {
                    /** ************ ******  停止资源操作  ****** ************ **/
                    nativeUtil.stopResourceOperate(informDev, applyProjectionIds);
                }
            }
        });
        holder.pro_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProjectorPop.dismiss();
            }
        });
        mProjectorPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                openProjector = false;
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
    public void showScreensPop() {
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

            private int subid;  // 2：屏幕 3：摄像头
            private int srcdeviceid;//需要采集的设备ID

            @Override
            public void onClick(View view) {
                sameMemberDevRrsIds = new ArrayList<Integer>();
                sameMemberDevIds = new ArrayList<Integer>();
                /** **** **  在线的参会人  ** **** **/
                if (onLineMemberAdapter != null) {
                    List<DevMember> checkedIds = onLineMemberAdapter.getCheckedIds();
                    for (int i = 0; i < checkedIds.size(); i++) {
                        sameMemberDevIds.add(checkedIds.get(i).getDevId());
                    }
                }
                /** **** **  在线的投影机  ** **** **/
                if (onLineProjectorAdapter != null) {
                    List<DeviceInfo> checkedIds1 = onLineProjectorAdapter.getCheckedIds(0);
                    for (int i = 0; i < checkedIds1.size(); i++) {
                        sameMemberDevRrsIds.add(checkedIds1.get(i).getDevId());
                    }
                }
                /** **** **  如果是从视屏直播列表点击：  ** **** **/
                Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.onClick :  fromVideo --->>> " + fromVideo);
                if (fromVideo) {
                    srcdeviceid = videoInfos.get(0).getVideoInfo().getDeviceid();
                    subid = videoInfos.get(0).getVideoInfo().getSubid();
                    sameMemberDevIds.add(MeetingActivity.getDevId());
                } else {
                    srcdeviceid = DevMeetInfo.getDeviceid();
                    subid = 2;
                }
                Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.onClick :  subid --->>> " + subid);
                sameMemberDevRrsIds.add(0);
                sameMemberDevIds.add(0);//要播放的屏幕源(需要看的设备ID)
                /** ************ ******  流播放  ******0x1080004  0x1100003************ **/
                nativeUtil.streamPlay(srcdeviceid, subid, 0,
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

        mScreenPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.e(TAG, "com.pa.paperless.activity_MeetingActivity.onDismiss :  同屏pop隐藏了 --->>> ");
                fromVideo = false;
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
    public static final String TAG = "MeetingActivity-->";

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

    public static class PostilViewHolder {
        public View rootView;
        public ImageView postil_image;
        public Button postil_save_local;
        public Button postil_save_server;
        public Button postil_pic;
        public Button postil_start_screen;
        public Button postil_stop_screen;
        public Button postil_start_projection;
        public Button postil_stop_projection;
        public Button postil_pre;
        public Button postil_next;
        public LinearLayout postil_layout_id;

        public PostilViewHolder(View rootView) {
            this.rootView = rootView;
            this.postil_image = (ImageView) rootView.findViewById(R.id.postil_image);
            this.postil_save_local = (Button) rootView.findViewById(R.id.postil_save_local);
            this.postil_save_server = (Button) rootView.findViewById(R.id.postil_save_server);
            this.postil_pic = (Button) rootView.findViewById(R.id.postil_pic);
            this.postil_start_screen = (Button) rootView.findViewById(R.id.postil_start_screen);
            this.postil_stop_screen = (Button) rootView.findViewById(R.id.postil_stop_screen);
            this.postil_start_projection = (Button) rootView.findViewById(R.id.postil_start_projection);
            this.postil_stop_projection = (Button) rootView.findViewById(R.id.postil_stop_projection);
            this.postil_pre = (Button) rootView.findViewById(R.id.postil_pre);
            this.postil_next = (Button) rootView.findViewById(R.id.postil_next);
            this.postil_layout_id = (LinearLayout) rootView.findViewById(R.id.postil_layout_id);
        }

    }
}

