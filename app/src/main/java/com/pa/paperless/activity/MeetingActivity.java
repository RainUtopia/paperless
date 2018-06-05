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
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
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
import com.mogujie.tt.protobuf.InterfaceAgenda;
import com.mogujie.tt.protobuf.InterfaceBase;
import com.mogujie.tt.protobuf.InterfaceBullet;
import com.mogujie.tt.protobuf.InterfaceDevice;
import com.mogujie.tt.protobuf.InterfaceDownload;
import com.mogujie.tt.protobuf.InterfaceFile;
import com.mogujie.tt.protobuf.InterfaceIM;
import com.mogujie.tt.protobuf.InterfaceMacro;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMeetfunction;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.mogujie.tt.protobuf.InterfaceVideo;
import com.mogujie.tt.protobuf.InterfaceVote;
import com.mogujie.tt.protobuf.InterfaceWhiteboard;
import com.pa.paperless.R;
import com.pa.paperless.adapter.JoinAdapter;
import com.pa.paperless.adapter.OnLineProjectorAdapter;
import com.pa.paperless.adapter.ScreenControlAdapter;
import com.pa.paperless.bean.DevMember;
import com.pa.paperless.bean.DeviceInfo;
import com.pa.paperless.bean.MeetDirFileInfo;
import com.pa.paperless.bean.MeetingFileTypeBean;
import com.pa.paperless.bean.MemberInfo;
import com.pa.paperless.bean.ReceiveMeetIMInfo;
import com.pa.paperless.bean.VideoInfo;
import com.pa.paperless.bean.VoteInfo;
import com.pa.paperless.constant.IDEventF;
import com.pa.paperless.constant.IDEventMessage;
import com.pa.paperless.constant.IDivMessage;
import com.pa.paperless.constant.Macro;
import com.pa.paperless.event.EventBadge;
import com.pa.paperless.event.EventMessage;
import com.pa.paperless.fab.FabInfo;
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
import com.pa.paperless.service.FabService;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.pa.paperless.fragment.meeting.ChatFragment.chatisshowing;
import static com.pa.paperless.fragment.meeting.MeetingFileFragment.meetfile_isshowing;
import static com.pa.paperless.fragment.meeting.NotationFragment.notationfragment_isshowing;
import static com.pa.paperless.fragment.meeting.SharedFileFragment.sharefile_isshowing;
import static com.pa.paperless.fragment.meeting.WebBrowseFragment.webView_isshowing;
import static com.pa.paperless.utils.MyUtils.getMediaid;
import static com.pa.paperless.utils.MyUtils.handTo;
import static com.pa.paperless.utils.MyUtils.setAnimator;
import static com.pa.paperless.utils.MyUtils.setBackgroundAlpha;


public class MeetingActivity extends BaseActivity implements View.OnClickListener, CallListener {

    public static boolean PaletteIsShowing = false;
    private ImageButton mMeetingMessage, mMeetingChatOnline;
    private TextView mMeetingNowTime, mMeetingNowDate, mMeetingNowWeek, mMeetingTheme, mMeetingCompanyName;
    private PopupWindow mFabPop, mProjectorPop, mProRlPop, mFunctionMsgPop, mNotePop, joinWatchPop, mScreenPop, mPlayerPop;
    //动态添加的ImageView控件
    private ImageView WhiteBoardIV, VideoIV, MeetChatIV, PostilIV, ShareFileIV, MeetFileIV, DocumentIV, SigninIV, VoteIV, AgendaIV, WebIV;
    private List<ImageView> mImages;
    public static TextView mAttendee, mCompere;//参会人，主持人
    //    private FloatingActionButton mFab;
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
    //    public static String mNoteCentent;
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
    private LinearLayout fragment_layout;
    private Resources resources;
    public static NativeUtil nativeUtil;
    public static MeetingActivity context;

    private List<DeviceInfo> deviceInfos;
    private int MSG_TYPE;
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
    private boolean isRestart = false;
    private int saveIndex;//存放跳转之前所展示的Fragment索引

    private Handler meetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IDivMessage.QUERY_MEET_FUNCTION://查询会议功能
                    DisposeMeetFun(msg);
                    break;
                case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                    disposeAttendee(msg);
                    break;
                case IDivMessage.QUERY_DEVICE_INFO://6.查询设备信息(查找在线状态的投影机)
                    DisposeDevInfo(msg);
                    break;
                case IDivMessage.Query_MeetSeat_Inform://181.查询会议排位
                    DisposeIsCompere(msg);
                    break;
                case IDivMessage.QUERY_CAN_JOIN: // 查询可加入的同屏会话
                    DisposeCanJoin(msg);
                    break;
                case IDivMessage.QUERY_CONFORM_DEVID://125.查询符合要求的设备ID(查询投影机)
                    queryProjector(msg);
                    break;
                case IDivMessage.DEV_PROBYID: //7.按属性ID查询指定设备属性（投影机）
                    queryProjectorName(msg);
                    break;
                case IDivMessage.QUERY_AGENDA://收到议程信息
                    disposeAgenda(msg);
                    break;
                case IDivMessage.RECEIVE_MEET_IMINFO://收到新的会议交流信息
                    disposeMeetChatMsg(msg);
                    break;
                case IDivMessage.QUERY_ATTEND_BYID://查询指定ID参会人
                    disposeAttendeeById(msg);
                    break;
                case IDivMessage.QUERY_MEET_DIR://查询会议目录
                    disopseMeetDir(msg);
                    break;
                case IDivMessage.QUERY_MEET_DIR_FILE://查询会议目录文件
                    disposeMeetDirFile(msg);
                    break;
                case IDivMessage.QUERY_LONG_NOTICE://查询长文本公告
                    disposeLongNotice(msg);
                    break;
                case IDivMessage.QUERY_SIGN://查询签到
                    disposeSignin(msg);
                    break;
                case IDivMessage.QUERY_MEET_VEDIO://查询会议视频
                    disposeMeetVideo(msg);
                    break;
                case IDivMessage.QUERY_VOTE://投票查询
                    disposeVote(msg);
                    break;
                case IDivMessage.QUERY_MEMBER_BYVOTE://查询指定投票的提交人
                    disposeMemberByVote(msg);
                    break;
                case IDivMessage.QUERY_NET: //网页查询
                    disposeNetWeb(msg);
                    break;
                case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                    ArrayList devMeetInfos = msg.getData().getParcelableArrayList("devMeetInfos");
                    if (devMeetInfos != null) {
                        updataTopUi(devMeetInfos);
                    }
                    break;
            }
        }
    };
    private boolean uploadPostil = false;
    private File upLoadPostilFile;
    private List<Integer> onlineClientIds;


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
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            getIntentBundle();
            //设置默认点击第一项
            if (mFunIndexs.contains(Macro.Pb_MEET_FUNCODE_POSTIL)) {
                for (int i = 0; i < mFunIndexs.size(); i++) {
                    if (mFunIndexs.get(i) == Macro.Pb_MEET_FUNCODE_POSTIL) {
                        /** **** **  目前的方法：如果有批注就先展示批注页面  ** **** **/
                        showFragment(Macro.Pb_MEET_FUNCODE_POSTIL);
                        setImgSelect(i);
                    }
                }
                /** **** **  如果是第一次，则是默认值0  如果是经过了 restart 则是屏幕跳转前的值  ** **** **/
            }
            if (isRestart) {
                showFragment(saveIndex);
                setImgSelect(saveIndex);
                isRestart = false;
            } else {
                showFragment(0);
                setImgSelect(0);
            }
            initImages(mImages, mFunIndexs);
            boolean fabService = MyUtils.isServiceRunning(getApplicationContext(), "com.pa.paperless.service.FabService");
            Log.e(TAG, "MeetingActivity.DisposeMeetFun :  FabService服务是否启动中 --> " + fabService);
            if (!fabService) {
                Intent intent = new Intent(this, FabService.class);
                startService(intent);
            }
        }
    }

    private void updataTopUi(ArrayList devMeetInfos) {
        InterfaceDevice.pbui_Type_DeviceFaceShowDetail devMeetInfo = (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) devMeetInfos.get(0);
        mMeetingCompanyName.setText(MyUtils.getBts(devMeetInfo.getCompany()));
        mMeetingTheme.setText(MyUtils.getBts(devMeetInfo.getMeetingname()));
        mAttendee.setText(MyUtils.getBts(devMeetInfo.getMembername()));
    }

    private void disposeNetWeb(Message msg) {
        ArrayList queryNet = msg.getData().getParcelableArrayList("queryNet");
        InterfaceBase.pbui_meetUrl o = (InterfaceBase.pbui_meetUrl) queryNet.get(0);
        String url = MyUtils.getBts(o.getUrl());
        EventBus.getDefault().post(new EventMessage(IDEventF.post_newWeb_url, url));
    }

    private void disposeMemberByVote(Message msg) {
        ArrayList queryVoteMember = msg.getData().getParcelableArrayList("queryVoteMember");
        InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo o1 = (InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo) queryVoteMember.get(0);
        EventBus.getDefault().post(new EventMessage(IDEventF.post_member_byVote, o1));
    }

    private void disposeVote(Message msg) {
        ArrayList queryVote = msg.getData().getParcelableArrayList("queryVote");
        InterfaceVote.pbui_Type_MeetVoteDetailInfo o = (InterfaceVote.pbui_Type_MeetVoteDetailInfo) queryVote.get(0);
        //获取到所有的投票信息
        List<VoteInfo> mVoteData = Dispose.Vote(o);
        EventBus.getDefault().post(new EventMessage(IDEventF.post_vote, mVoteData));
    }

    private void disposeMeetVideo(Message msg) {
        ArrayList queryDevInfo = msg.getData().getParcelableArrayList("queryMeetVideo");
        InterfaceVideo.pbui_Type_MeetVideoDetailInfo o = (InterfaceVideo.pbui_Type_MeetVideoDetailInfo) queryDevInfo.get(0);
        EventBus.getDefault().post(new EventMessage(IDEventF.post_meet_video, o));
    }

    private void disposeSignin(Message msg) {
        ArrayList signInfo = msg.getData().getParcelableArrayList("signInfo");
        InterfaceSignin.pbui_Type_MeetSignInDetailInfo o = (InterfaceSignin.pbui_Type_MeetSignInDetailInfo) signInfo.get(0);
        EventBus.getDefault().post(new EventMessage(IDEventF.post_signin_info, o));
    }

    private void disposeLongNotice(Message msg) {
        ArrayList notice = msg.getData().getParcelableArrayList("notice");
        InterfaceBullet.pbui_BigBulletDetailInfo o = (InterfaceBullet.pbui_BigBulletDetailInfo) notice.get(0);
        EventBus.getDefault().post(new EventMessage(IDEventF.post_long_notice, MyUtils.getBts(o.getText())));
    }

    private void disposeMeetDirFile(Message msg) {
        ArrayList queryMeetDirFile = msg.getData().getParcelableArrayList("queryMeetDirFile");
        InterfaceFile.pbui_Type_MeetDirFileDetailInfo o = (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) queryMeetDirFile.get(0);
        List<MeetDirFileInfo> meetDirFileInfos = Dispose.MeetDirFile(o);
        int dirid = o.getDirid();//目录ID
        if (dirid == 2) {//批注文件的目录ID 固定为2
            if (uploadPostil) {
                EventBus.getDefault().post(new EventMessage(IDEventF.upload_postil, meetDirFileInfos));
            }
            if (notationfragment_isshowing) {
                EventBus.getDefault().post(new EventMessage(IDEventF.post_postil_file, meetDirFileInfos));
            }
        } else if (dirid == 1 && sharefile_isshowing) {//共享资料的目录ID 固定为1
            EventBus.getDefault().post(new EventMessage(IDEventF.post_share_file, meetDirFileInfos));
        } else if (meetfile_isshowing) {
            EventBus.getDefault().post(new EventMessage(IDEventF.post_meet_dirFile, meetDirFileInfos));
        }
    }

    private void disopseMeetDir(Message msg) {
        List<MeetingFileTypeBean> dirdata = new ArrayList<>();
        ArrayList queryMeetDir = msg.getData().getParcelableArrayList("queryMeetDir");
        InterfaceFile.pbui_Type_MeetDirDetailInfo o = (InterfaceFile.pbui_Type_MeetDirDetailInfo) queryMeetDir.get(0);
        List<InterfaceFile.pbui_Item_MeetDirDetailInfo> itemList = o.getItemList();
        for (int i = 0; i < itemList.size(); i++) {
            InterfaceFile.pbui_Item_MeetDirDetailInfo item = itemList.get(i);
            //过滤掉共享文件和批注文件
            String dirname = MyUtils.getBts(item.getName());
            int dirid = item.getId();
            int filenum = item.getFilenum();
            if (dirname.equals("批注文件") && notationfragment_isshowing) {
                try {
                    nativeUtil.queryMeetDirFile(dirid);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            } else if (dirname.equals("共享文件") && sharefile_isshowing) {
                try {
                    nativeUtil.queryMeetDirFile(dirid);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            } else if (!dirname.equals("批注文件") && !dirname.equals("共享文件")) {
                dirdata.add(new MeetingFileTypeBean(dirid, item.getParentid(), dirname, filenum));
            }
        }
        if (meetfile_isshowing) {
            /** **** **  将查询得到的会议目录发送给MeetFileFragment  ** **** **/
            EventBus.getDefault().post(new EventMessage(IDEventF.post_meet_dir, dirdata));
        }
    }

    private void disposeAttendeeById(Message msg) {
        ArrayList assignMember = msg.getData().getParcelableArrayList("assignMember");
        InterfaceMember.pbui_Type_MemberDetailInfo o3 = (InterfaceMember.pbui_Type_MemberDetailInfo) assignMember.get(0);
        List<InterfaceMember.pbui_Item_MemberDetailInfo> itemList = o3.getItemList();
        if (itemList != null) {
            String name = MyUtils.getBts(itemList.get(0).getName());
            mReceiveMsg.get(mReceiveMsg.size() - 1).setName(name);
            EventBus.getDefault().post(new EventMessage(IDEventF.post_new_chatMsg));
        }
    }

    private void disposeMeetChatMsg(Message msg) {
        ArrayList chatMsg = msg.getData().getParcelableArrayList("chatMsg");
        InterfaceIM.pbui_Type_MeetIM o1 = (InterfaceIM.pbui_Type_MeetIM) chatMsg.get(0);
        if (o1.getMsgtype() == 0) {//证明是文本消息
            mReceiveMsg.add(Dispose.ReceiveMeetIMinfo(o1).get(0));
            //为true时，表示是接收的消息
            mReceiveMsg.get(mReceiveMsg.size() - 1).setType(true);
            try {
                //91.查询指定ID的参会人员
                nativeUtil.queryAttendPeopleFromId(o1.getMemberid());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            mBadge.setBadgeNumber(0);
        }
    }

    private void disposeAttendee(Message msg) {
        ArrayList queryMember = msg.getData().getParcelableArrayList("queryMember");
        InterfaceMember.pbui_Type_MemberDetailInfo o2 = (InterfaceMember.pbui_Type_MemberDetailInfo) queryMember.get(0);
        memberInfos = Dispose.MemberInfo(o2);
        EventBus.getDefault().post(new EventMessage(IDEventF.post_member_toSignin, memberInfos));
        try {
            //查询设备信息
            nativeUtil.queryDeviceInfo();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private void disposeAgenda(Message msg) {
        ArrayList agenda = msg.getData().getParcelableArrayList("agenda_meet");
        InterfaceAgenda.pbui_meetAgenda o = (InterfaceAgenda.pbui_meetAgenda) agenda.get(0);
        //议程文本
        String text = new String(o.getText().toByteArray());
        /** **** **  发送查询到的议程文本  ** **** **/
        EventBus.getDefault().post(new EventMessage(IDEventF.post_agenda, text));
    }

    @Override
    public void callListener(int action, Object result) {
        switch (action) {
            case IDivMessage.QUERY_DEVMEET_INFO://110.查询设备会议信息
                handTo(IDivMessage.QUERY_DEVMEET_INFO, (InterfaceDevice.pbui_Type_DeviceFaceShowDetail) result, "devMeetInfos", meetHandler);
                break;
            case IDivMessage.QUERY_SIGN://查询签到
                MyUtils.handTo(IDivMessage.QUERY_SIGN, (InterfaceSignin.pbui_Type_MeetSignInDetailInfo) result, "signInfo", meetHandler);
                break;
            case IDivMessage.QUERY_MEET_BYID://129.查询指定ID的会议
                MyUtils.handTo(IDivMessage.QUERY_MEET_BYID, (InterfaceMeet.pbui_Type_MeetMeetInfo) result, "queryMeetById", meetHandler);
                break;
            case IDivMessage.QUERY_ATTENDEE://92.查询参会人员
                MyUtils.handTo(IDivMessage.QUERY_ATTENDEE, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "queryMember", meetHandler);
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
                MyUtils.handTo(IDivMessage.QUERY_MEET_FUNCTION, (InterfaceMeetfunction.pbui_Type_MeetFunConfigDetailInfo) result, "queryMeetFunction", meetHandler);
                break;
            case IDivMessage.QUERY_CONFORM_DEVID: //125.查询符合要求的设备ID
                MyUtils.handTo(IDivMessage.QUERY_CONFORM_DEVID, (InterfaceRoom.pbui_Type_ResMeetRoomDevInfo) result, "queryProjector", meetHandler);
                break;
            case IDivMessage.DEV_PROBYID://7.按属性ID查询指定设备属性（投影机）
                MyUtils.handTo(IDivMessage.DEV_PROBYID, (byte[]) result, "queryProjectorName", meetHandler);
                break;
            case IDivMessage.QUERY_AGENDA://收到议程的数据
                MyUtils.handTo(IDivMessage.QUERY_AGENDA, (InterfaceAgenda.pbui_meetAgenda) result, "agenda_meet", meetHandler);
                break;
            case IDivMessage.RECEIVE_MEET_IMINFO: //收到会议消息
                if (chatisshowing) {//判断当前ChatFragment是否显示状态
                    MyUtils.handTo(IDivMessage.RECEIVE_MEET_IMINFO, (InterfaceIM.pbui_Type_MeetIM) result, "chatMsg", meetHandler);
                } else {
                    MyUtils.receiveMessage((InterfaceIM.pbui_Type_MeetIM) result, nativeUtil);
                }
                break;
            case IDivMessage.QUERY_ATTEND_BYID://查询指定ID的参会人(发送消息的人员)
                if (chatisshowing) {
                    MyUtils.handTo(IDivMessage.QUERY_ATTEND_BYID, (InterfaceMember.pbui_Type_MemberDetailInfo) result, "assignMember", meetHandler);
                } else {
                    MyUtils.queryName((InterfaceMember.pbui_Type_MemberDetailInfo) result);
                }
                break;
            case IDivMessage.QUERY_MEET_DIR://136.查询会议目录
                MyUtils.handTo(IDivMessage.QUERY_MEET_DIR, (InterfaceFile.pbui_Type_MeetDirDetailInfo) result, "queryMeetDir", meetHandler);
                break;
            case IDivMessage.QUERY_MEET_DIR_FILE://查询会议目录文件
                MyUtils.handTo(IDivMessage.QUERY_MEET_DIR_FILE, (InterfaceFile.pbui_Type_MeetDirFileDetailInfo) result, "queryMeetDirFile", meetHandler);
                break;
            case IDivMessage.QUERY_LONG_NOTICE:
                MyUtils.handTo(IDivMessage.QUERY_LONG_NOTICE, (InterfaceBullet.pbui_BigBulletDetailInfo) result, "notice", meetHandler);
                break;
            case IDivMessage.QUERY_MEET_VEDIO://查询会议视频
                MyUtils.handTo(IDivMessage.QUERY_MEET_VEDIO, (InterfaceVideo.pbui_Type_MeetVideoDetailInfo) result, "queryMeetVideo", meetHandler);
                break;
            case IDivMessage.QUERY_VOTE://投票查询
                MyUtils.handTo(IDivMessage.QUERY_VOTE, (InterfaceVote.pbui_Type_MeetVoteDetailInfo) result, "queryVote", meetHandler);
                break;
            case IDivMessage.QUERY_MEMBER_BYVOTE://203.查询指定投票的提交人
                MyUtils.handTo(IDivMessage.QUERY_MEMBER_BYVOTE, (InterfaceVote.pbui_Type_MeetVoteSignInDetailInfo) result, "queryVoteMember", meetHandler);
                break;
            case IDivMessage.QUERY_NET:
                MyUtils.handTo(IDivMessage.QUERY_NET, (InterfaceBase.pbui_meetUrl) result, "queryNet", meetHandler);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) throws InvalidProtocolBufferException {
        switch (message.getAction()) {
            case IDEventF.open_note:
                startActivity(new Intent(this, NoteActivity.class));
                break;
            case IDEventF.updata_to_postil://收到WPS编辑完成后的文件路径
                Log.e(TAG, "MeetingActivity.getEventMessage :  收到WPS编辑完成后的文件路径 --> ");
                uploadPostil = true;
                //获取要上传的文件
                upLoadPostilFile = (File) message.getObject();
                //查找一次所有的批注文件
                nativeUtil.queryMeetDirFile(2);//2 为批注目录
                break;
            case IDEventF.upload_postil:// 收到查找的批注文件
                List<MeetDirFileInfo> object = (List<MeetDirFileInfo>) message.getObject();
                String path = upLoadPostilFile.getPath();
                int mediaid = getMediaid(path);
                String fileName = upLoadPostilFile.getName();
                for (int i = 0; i < object.size(); i++) {
                    if (upLoadPostilFile.getName().equals(object.get(i).getFileName())) {
                        // 如果批注文件中有这个文件名,那么将时间信息添加进去
                        fileName = "(" + mMeetingNowDate.getText().toString() + "" + mMeetingNowTime.getText().toString() + "-修订)" + upLoadPostilFile.getName();
                    }
                }
                /** **** **  将编辑保存完的文件上传到批注文档目录  ** **** **/
                nativeUtil.uploadFile(InterfaceMacro.Pb_Upload_Flag.Pb_MEET_UPLOADFLAG_ONLYENDCALLBACK.getNumber(),
                        2, 0, fileName, path, 0, mediaid);
                break;
            case IDEventMessage.Upload_Progress://上传进度通知
                uploadPostil = false;
                break;
            case IDEventF.open_picture://收到打开图片
                String filepath = (String) message.getObject();
                Log.e(TAG, "MeetingActivity.getEventMessage :  将要打开的图片的路径 --> " + filepath);
                FileUtil.openPicture(filepath, context, findViewById(R.id.meeting_layout_id));
                break;
            case IDEventMessage.DEVMEETINFO_CHANGE_INFORM://设备会议信息变更通知
                nativeUtil.queryDeviceMeetInfo();
                break;
            case IDEventMessage.MEETDIR_CHANGE_INFORM://135 会议目录变更通知
                nativeUtil.queryMeetDir();
                break;
            case IDEventMessage.MEETDIR_FILE_CHANGE_INFORM://142 会议目录文件变更通知
                InterfaceBase.pbui_MeetNotifyMsgForDouble object1 = (InterfaceBase.pbui_MeetNotifyMsgForDouble) message.getObject();
                int id = object1.getId();
                nativeUtil.queryMeetDirFile(id);
                break;
            case IDEventMessage.AGENDA_CHANGE_INFO://议程变更通知
                nativeUtil.queryAgenda();
                break;
            case IDEventMessage.START_COLLECTION_STREAM_NOTIFY://收集流
                enentCollectionStream(message);
                break;
            case IDEventMessage.FACESTATUS_CHANGE_INFORM://界面状态变更通知
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.UpDate_BadgeNumber:
                List<EventBadge> object3 = (List<EventBadge>) message.getObject();
                if (object3 != null) {
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
                //92.查询参会人员
                nativeUtil.queryAttendPeople();
                InterfaceBase.pbui_MeetNotifyMsg MrmberName = (InterfaceBase.pbui_MeetNotifyMsg) message.getObject();
                /** ************ ******  91.查询指定ID的参会人 （接收到消息时） ****** ************ **/
                nativeUtil.queryAttendPeopleFromId(MrmberName.getId());
                break;
            case IDEventMessage.MeetSeat_Change_Inform:
//                nativeUtil.queryMeetRanking();
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.PLACE_DEVINFO_CHANGEINFORM:
                //119 会场设备信息变更通知(投影机)
                /** ************ ******  125.查询符合要求的设备ID  （投影机）  ****** ************ **/
                // TODO: 2018/2/27 回调在签到页面做的处理
                nativeUtil.queryRequestDeviceId(DevMeetInfo.getRoomid(),
                        Macro.DEVICE_MEET_PROJECTIVE,
//                        InterfaceMacro.Pb_RoomDeviceFilterFlag.Pb_MEET_ROOMDEVICE_FLAG_PROJECTIVE.getNumber(),
                        InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber(), 0);
                break;
            case IDEventMessage.STOP_COLLECTION_STREAM_NOTIFY://停止采集流通知
                switch (message.getType()) {
                    case 2:
                        if (stopRecord()) {
                            showToast("屏幕录制已停止");
                        } else {
                            showToast("屏幕录制停止失败");
                        }
                        break;
                }
                break;
            case IDEventMessage.MEET_FUNCTION_CHANGEINFO://会议功能变更通知
                /** **** **  238.查询会议功能  ** **** **/
                //查询会议功能
                nativeUtil.queryMeetFunction();
                break;
            case IDEventMessage.DOWN_FINISH://67 下载进度回调
                enentDownFinish(message);
                break;
            case IDEventMessage.OPEN_BOARD://收到白板打开操作
                if (!PaletteIsShowing) {
                    eventOpenBoard(message);
                }
                break;
            case IDEventMessage.DEV_REGISTER_INFORM://设备寄存器变更通知（监听参会人退出）
                //查询参会人信息
                nativeUtil.queryAttendPeople();
                break;
            case IDEventMessage.SIGN_CHANGE_INFORM://签到变更通知(监听参会人进入会议界面)
                //查询参会人信息
                nativeUtil.queryAttendPeople();
                break;
        }
    }

    private void enentCollectionStream(EventMessage message) {
        switch (message.getType()) {
            case 2://屏幕
                if (stopRecord()) {
                    showToast("屏幕录制已停止");
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

    /**
     * 下载进度回调
     *
     * @param message
     * @throws InvalidProtocolBufferException
     */
    private void enentDownFinish(EventMessage message) throws InvalidProtocolBufferException {
        InterfaceDownload.pbui_Type_DownloadCb object5 = (InterfaceDownload.pbui_Type_DownloadCb) message.getObject();
        int mediaid = object5.getMediaid();
        int progress = object5.getProgress();
        byte[] bytes = nativeUtil.queryFileProperty(InterfaceMacro.Pb_MeetFilePropertyID.Pb_MEETFILE_PROPERTY_NAME.getNumber()
                , mediaid);
        InterfaceBase.pbui_CommonTextProperty pbui_commonTextProperty = InterfaceBase.pbui_CommonTextProperty.parseFrom(bytes);
        String downOverFileName = MyUtils.getBts(pbui_commonTextProperty.getPropertyval());
        if (object5.getProgress() < 100) {
            showToast(downOverFileName + " 当前下载进度：" + progress + "%");
        } else {
            showToast(downOverFileName + " 下载完毕，正在打开");
            File file = FileUtil.findFilePathByName(Macro.MEETFILE, downOverFileName);
            if (file != null) {
                MyUtils.OpenThisFile(context, file);
            }
        }
    }

    private Toast mT;

    private void showToast(String msg) {
        if (mT == null) {
            mT = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mT.setText(msg);
        }
        mT.show();
    }

    private void eventOpenBoard(EventMessage message) {
        InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard object4 = (InterfaceWhiteboard.pbui_Type_MeetStartWhiteBoard) message.getObject();
        int operflag = object4.getOperflag();
        ByteString medianame = object4.getMedianame();
        int opermemberid = object4.getOpermemberid();
        int srcmemid = object4.getSrcmemid();
        long srcwbidd = object4.getSrcwbid();
        if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_FORCEOPEN.getNumber()) {
            //强制打开白板  直接强制同意加入
            nativeUtil.agreeJoin(opermemberid, srcmemid, srcwbidd);
            startActivity(new Intent(MeetingActivity.this, PeletteActivity.class));
            PeletteActivity.isSharing = true;//如果同意加入就设置已经在共享中
            PeletteActivity.launchPersonId = srcmemid;//设置发起的人员ID
            PeletteActivity.mSrcwbid = srcwbidd;
        } else if (operflag == InterfaceMacro.Pb_MeetPostilOperType.Pb_MEETPOTIL_FLAG_REQUESTOPEN.getNumber()) {
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
                nativeUtil.agreeJoin(MeetingActivity.getMemberId(), srcmemid, srcwbidd);
                PeletteActivity.isSharing = true;//如果同意加入就设置已经在共享中
                PeletteActivity.launchPersonId = srcmemid;//设置发起的人员ID
                PeletteActivity.mSrcwbid = srcwbidd;
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
            if (i1 == number) {
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

    //设置投票页面当前设备是否是主持人并查找到主持人信息
    private void DisposeIsCompere(Message msg) {
        ArrayList queryMeetRanking = msg.getData().getParcelableArrayList("queryMeetRanking");
        InterfaceRoom.pbui_Type_MeetSeatDetailInfo o5 = (InterfaceRoom.pbui_Type_MeetSeatDetailInfo) queryMeetRanking.get(0);
        List<InterfaceRoom.pbui_Item_MeetSeatDetailInfo> itemList3 = o5.getItemList();
        for (int i = 0; i < itemList3.size(); i++) {
            InterfaceRoom.pbui_Item_MeetSeatDetailInfo pbui_item_meetSeatDetailInfo = itemList3.get(i);
            int nameId = pbui_item_meetSeatDetailInfo.getNameId();
            int seatid = pbui_item_meetSeatDetailInfo.getSeatid();//这是设备ID
            int role = pbui_item_meetSeatDetailInfo.getRole();
            if (role == 3) {
                if (DevMeetInfo.getMemberid() == nameId) {
                    EventBus.getDefault().post(new EventMessage(IDEventF.you_are_compere));
                    //本机的身份为主持人
                    VoteFragment.isCompere = true;
                    // 设置控件展示主持人名称
                    mCompere.setText(MyUtils.getBts(DevMeetInfo.getMembername()));
                } else {
                    //本机的身份不是主持人
                    VoteFragment.isCompere = false;
                    EventBus.getDefault().post(new EventMessage(IDEventF.you_not_compere));
                    // 设置控件展示主持人名称
                    for (int j = 0; j < deviceInfos.size(); j++) {
                        if (deviceInfos.get(j).getDevId() == seatid) {  //查找到 为主持人身份的设备信息
                            for (int k = 0; k < memberInfos.size(); k++) {
                                if (memberInfos.get(k).getPersonid() == nameId) {    //匹对人员ID  获取参会人（主持人）姓名
                                    Log.e(TAG, "MeetingActivity.DisposeIsCompere :  主持人名称 --> " + memberInfos.get(k).getName());
                                    mCompere.setText(memberInfos.get(k).getName());
                                }
                            }
                        }
                    }
                }
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
            int i1 = devceid & Macro.DEVICE_MEET_DB;
            int i2 = devceid & Macro.DEVICE_MEET_SERVICE;
            int i3 = devceid & Macro.DEVICE_MEET_PROJECTIVE;
            int i4 = devceid & Macro.DEVICE_MEET_CAPTURE;
            int i5 = devceid & Macro.DEVICE_MEET_CLIENT;
            int i6 = devceid & Macro.DEVICE_MEET_ONEKEYSHARE;

            if (i1 == Macro.DEVICE_MEET_DB || i2 == Macro.DEVICE_MEET_SERVICE || i4 == Macro.DEVICE_MEET_CAPTURE
                    || i5 == Macro.DEVICE_MEET_CLIENT || i6 == Macro.DEVICE_MEET_ONEKEYSHARE) {
            } else if (i3 == Macro.DEVICE_MEET_PROJECTIVE) {
                // 投影机
                checkedJoinProjector.add(false);
                peojectorJoin.add(pbui_item_deviceResPlay);
            } else {
                //参会人
                checkedJoinMember.add(false);
                memberJoin.add(pbui_item_deviceResPlay);
                int memberid = pbui_item_deviceResPlay.getMemberid();
                String name = MyUtils.getBts(pbui_item_deviceResPlay.getName());
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
            try {
                //181.查询会议排位
                nativeUtil.queryMeetRanking();
                nativeUtil.queryDeviceMeetInfo();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            EventBus.getDefault().post(new EventMessage(IDEventF.post_devInfo_video, deviceInfos));
            int number = Macro.DEVICE_MEET_PROJECTIVE;
            initData();
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
                if ((devId & Macro.DEVICE_MEET_CLIENT) == Macro.DEVICE_MEET_CLIENT) {//客户端
                    if (netState == 1 && devId != MeetingActivity.getDevId()) {
                        onlineClientIds.add(devId);//添加在线客户端
                    }
                }
            }
            /** **** **  发送给悬浮框参会人和投影机信息  ** **** **/
            EventBus.getDefault().post(new EventMessage(IDEventF.fab_member_pro, new FabInfo(onLineMembers, allProjectors, onLineProjectors, onlineClientIds)));
            initCheckedList();
            initAdapter();
            /** **** **  刷新Adapter  ** **** **/
            allProjectorAdapter.notifyDataSetChanged();
            onLineProjectorAdapter.notifyDataSetChanged();
            onLineMemberAdapter.notifyDataSetChanged();
            EventBus.getDefault().post(new EventMessage(IDEventF.post_chat_onLineMember, onLineMembers));
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
        if (onlineClientIds == null) {//存放在线状态客户端ID
            onlineClientIds = new ArrayList<>();
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
//        startService(new Intent(MeetingActivity.this, FabService.class));
        initScreenParam();
    }


    /**
     * 获取从MainActivity中传递过来的参数
     */
    private void getIntentBundle() {
//        if (DevMeetInfo != null) {
//            mMeetingCompanyName.setText(MyUtils.getBts(DevMeetInfo.getCompany()));
//            mMeetingTheme.setText(MyUtils.getBts(DevMeetInfo.getMeetingname()));
//            mAttendee.setText(MyUtils.getBts(DevMeetInfo.getMembername()));
//        }
        try {
            //** ************ ******  125.查询符合要求的设备ID  （投影机）  ****** ************ **//
            // TODO: 2018/2/27 回调在签到页面进行的处理
            nativeUtil.queryRequestDeviceId(DevMeetInfo.getRoomid(),
                    Macro.DEVICE_MEET_PROJECTIVE,
//                    InterfaceMacro.Pb_RoomDeviceFilterFlag.Pb_MEET_ROOMDEVICE_FLAG_PROJECTIVE.getNumber(),
                    InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MemFace.getNumber(), 0);
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
//        Export.ToNoteText(mNoteCentent, "会议笔记");
        //8.修改本机界面状态
        nativeUtil.setInterfaceState(InterfaceMacro.Pb_MeetFaceStatus.Pb_MemState_MainFace.getNumber());
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
        if (webView_isshowing) {//判断当前网络界面是否显示
            EventBus.getDefault().post(new EventMessage(IDEventF.go_back_html));
        } else {
            //回到主界面操作
            nativeUtil.backToMainInterfaceOperate(DevMeetInfo.getDeviceid());
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
//        super.finish();
        moveTaskToBack(true);
    }

    @Override
    protected void onRestart() {
        //Activity进行跳转之后回到本界面时重新设置
        isRestart = true;
        initController();
        /** **** **  回到MeetingActivity时设置默认点击  ** **** **/
        try {
            nativeUtil.queryMeetFunction();
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        super.onRestart();
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
        if (Macro.Pb_MEET_FUNCODE_POSTIL != index && Macro.Pb_MEET_FUNCODE_WHITEBOARD != index) {
            saveIndex = index;
        }
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
//        mFab = (FloatingActionButton) findViewById(R.id.fab);
        fragment_layout = (LinearLayout) findViewById(R.id.fragment_layout);
        mMeetingMessage.setOnClickListener(this);
        mMeetingChatOnline.setOnClickListener(this);
//        mFab.setOnClickListener(this);
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
//            case R.id.fab:
////                showFabPop();
//                break;
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
        arr.add(0);//会议服务类请求则为 0
        MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
        // TODO: 2018/2/7 服务功能事件 发送会议交流信息
        //纸
        holder.paper_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
//                /** ************ ******  185.发送会议交流信息  ****** ************ **/
                holder.edt_msg.setText("纸张申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Paper.getNumber();
            }
        });
        //笔
        holder.pen_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("需要一支笔");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Pen.getNumber();
            }
        });
        //茶水
        holder.tea_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("茶水申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Tea.getNumber();
            }
        });
        //矿泉水
        holder.water_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("矿泉水申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Water.getNumber();
            }
        });
        //计算器
        holder.calculator_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("计算器申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
            }
        });
        //服务员
        holder.waiter_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("服务员申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Waiter.getNumber();
            }
        });
        //清扫
        holder.sweep_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("需要清扫");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Other.getNumber();
            }
        });
        //技术员
        holder.technician_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.setAnimator(view);
                holder.edt_msg.setText("技术员申请");
                MSG_TYPE = InterfaceMacro.Pb_MeetIMMSG_TYPE.Pb_MEETIM_CHAT_Technical.getNumber();
            }
        });
        //发送
        holder.send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String other = holder.edt_msg.getText().toString();
                nativeUtil.sendMeetChatInfo(other, MSG_TYPE, arr);
                /** **** **  发送后清空输入框  ** **** **/
                holder.edt_msg.setText("");
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
        if (manager == null) {
            Log.e(TAG, "MeetingActivity.onActivityResult :  manager为null --> ");
            return;
        }
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                projection = manager.getMediaProjection(resultCode, data);
            }

        if (projection == null) {
            Log.e(TAG, "media projection is null");
            return;
        }
        recorder = new ScreenRecorder(width, height, bitrate, dpi, projection, "");
        recorder.start();//�启动录屏线程
        showToast("屏幕录制中...");
    }

    private void initScreenParam() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; // �屏幕宽度（像素）
        height = metric.heightPixels; // �屏幕高度（像素）
        if (width > 1920)
            width = 1920;
        if (height > 1080)
            height = 1080;
        W = width;
        H = height;
        density = metric.density; // �屏幕密度（0.75 / 1.0 / 1.5）
        dpi = metric.densityDpi; // �屏幕密度DPI（120 / 160 / 240）
        bitrate = width * height * VideoQuality;//�比特率/码率
    }

    private boolean stopRecord() {
        if (recorder != null) {
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

